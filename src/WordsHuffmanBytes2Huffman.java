import java.util.*;

/**
 * Created by Borys Minaiev on 26.04.17.
 */
public class WordsHuffmanBytes2Huffman implements CompressAlgorithm {

    /*
     *  Описание:
     *  Разбиваем на "слова" и "неслова" (знаки припинания, пробелы, ...).
     *  Считаем, что они чередуются.
     *  Выкидываем из словаря слова, которые встретились меньше DICTIONARY_REMOVE раз
     *  Те слова, которые были в тексте кодируем Хаффманом, остальное кодируем хаффманом по байтам беря в учет предыдущий байт + escape слово
     */

    class SubCompressor {
        HashMap<SubString, Integer> count = new HashMap<>();
        HashMap<SubString, HuffmanNode> node = new HashMap<>();
        HuffmanNode root;
        HuffmanNode escapeWord;
        SubString[] strings;

        HuffmanNode[] rootBytes;
        HuffmanNode[][] bytesNodes;
    }

    WordsHuffmanBytes2Huffman(int dictionaryRemove) {
        DICTIONARY_REMOVE = dictionaryRemove;
    }


    private SubCompressor words = new SubCompressor(), notWords = new SubCompressor();

    SubCompressor getSubCompressor(boolean alphabetic) {
        return alphabetic ? words : notWords;
    }

    @Override
    public void learn(byte[] line) {
        boolean currentAlph = true;
        for (int i = 0; i < line.length; ) {
            int j = i;
            while (j != line.length && Utils.isAlphabetic(line[j]) == currentAlph) {
                j++;
            }
            HashMap<SubString, Integer> dict = getSubCompressor(currentAlph).count;
            SubString str = new SubString(line, i, j);
            dict.put(str, 1 + dict.getOrDefault(str, 0));
            i = j;
            currentAlph = !currentAlph;
        }
    }

    int DICTIONARY_REMOVE = 5;

    int dictionarySize;

    private void buildDictionary(boolean alph) {
        SubCompressor subCompressor = getSubCompressor(alph);
        HashMap<SubString, Integer> dict = subCompressor.count;
        List<HuffmanNode> nodes = new ArrayList<>();
        SubString[] substring = new SubString[dict.size()];
        int it = 0;
        for (Map.Entry<SubString, Integer> entry : dict.entrySet()) {
            nodes.add(new HuffmanNode(it, entry.getValue()));
            substring[it++] = entry.getKey();
        }

        System.err.println("total nodes = " + nodes.size());
        Collections.sort(nodes);
        Collections.reverse(nodes);
        long notHereSum = 0;
        long[][] probBytes = new long[Utils.ALPH_SIZE][Utils.ALPH_SIZE];
        for (long[] prob : probBytes) {
            Arrays.fill(prob, 1);
        }
        while (nodes.size() > 0 && nodes.get(nodes.size() - 1).count < DICTIONARY_REMOVE) {
            SubString str = substring[(int) nodes.get(nodes.size() - 1).id];
            int prev = 0;
            for (int i = str.from; i < str.to; i++) {
                probBytes[prev][Utils.byteToUnsignedInt(str.array[i])]++;
                prev = Utils.byteToUnsignedInt(str.array[i]);
            }
            probBytes[prev][0]++;
            notHereSum += nodes.get(nodes.size() - 1).count;
            nodes.remove(nodes.size() - 1);
        }
        subCompressor.bytesNodes = new HuffmanNode[Utils.ALPH_SIZE][];
        subCompressor.rootBytes = new HuffmanNode[Utils.ALPH_SIZE];
        for (int prev = 0; prev < Utils.ALPH_SIZE; prev++) {
            ArrayList<HuffmanNode> byteNodes = new ArrayList<>();
            subCompressor.bytesNodes[prev] = new HuffmanNode[Utils.ALPH_SIZE];
            for (int i = 0; i < probBytes.length; i++) {
                subCompressor.bytesNodes[prev][i] = new HuffmanNode(i, probBytes[prev][i]);
                byteNodes.add(subCompressor.bytesNodes[prev][i]);
            }
            subCompressor.rootBytes[prev] = HuffmanTree.buildTree(byteNodes);
        }

        HashMap<SubString, HuffmanNode> nodeDict = subCompressor.node;
        for (HuffmanNode node : nodes) {
            nodeDict.put(substring[(int) node.id], node);
        }
        HuffmanNode escapeNode = new HuffmanNode(Integer.MAX_VALUE, notHereSum);
        subCompressor.escapeWord = escapeNode;
        nodes.add(escapeNode);
        for (HuffmanNode node : nodes) {
            if (node.id != Integer.MAX_VALUE) {
                SubString str = substring[(int) node.id];
                dictionarySize += str.to - str.from + 8;
            }
        }

        System.err.println("total nodes after remove = " + nodes.size());
        HuffmanNode root = HuffmanTree.buildTree(nodes);
        subCompressor.root = root;
        subCompressor.strings = substring;
    }

    @Override
    public void learnEnd() {
        buildDictionary(false);
        buildDictionary(true);
        System.err.println("total dict size is " + getDictionarySize());
        System.err.println("words " + dictionarySize + ", bytes2huffman = " + 4 * Utils.ALPH_SIZE * Utils.ALPH_SIZE);
    }

    long compressedByDictionary = 0;
    long compressedByDictionaryFinalSizeInBits = 0;
    long compressedBytes = 0;
    long compressedBytesFinalSizeInBits = 0;

    @Override
    public byte[] compress(byte[] line) {
        BitStream stream = new BitStream();
        boolean currentAlph = true;
        for (int i = 0; i < line.length; ) {
            int j = i;
            while (j != line.length && Utils.isAlphabetic(line[j]) == currentAlph) {
                j++;
            }
            SubCompressor subCompressor = getSubCompressor(currentAlph);
            HashMap<SubString, HuffmanNode> dict = subCompressor.node;

            SubString str = new SubString(line, i, j);
            HuffmanNode node = dict.get(str);
            if (node != null) {
                compressedByDictionary += (j - i);
                compressedByDictionaryFinalSizeInBits += node.codeLen;
                node.writeToStream(stream);
            } else {
                compressedBytes += (j - i);
                compressedBytesFinalSizeInBits += subCompressor.escapeWord.codeLen;
                subCompressor.escapeWord.writeToStream(stream);
                int prev = 0;
                for (int k = i; k < j; k++) {
                    byte b = line[k];
                    HuffmanNode curNode = subCompressor.bytesNodes[prev][Utils.byteToUnsignedInt(b)];
                    prev = Utils.byteToUnsignedInt(b);
                    curNode.writeToStream(stream);
                    compressedBytesFinalSizeInBits += curNode.codeLen;
                }
                HuffmanNode curNode = subCompressor.bytesNodes[prev][0];
                curNode.writeToStream(stream);
                compressedBytesFinalSizeInBits += curNode.codeLen;
            }
            i = j;
            currentAlph = !currentAlph;
        }
        return stream.convertToByteArray();
    }

    @Override
    public byte[] decompress(byte[] data) {
        BitStream stream = new BitStream(data);
        ArrayList<Byte> bytes = new ArrayList<>();
        boolean currentAlph = true;
        while (stream.hasMoreBits()) {
            SubCompressor subCompressor = getSubCompressor(currentAlph);
            HuffmanNode node = subCompressor.root;
            while (node.left != null) {
                if (stream.readNextBit()) {
                    node = node.right;
                } else {
                    node = node.left;
                }
            }
            SubString[] strings = subCompressor.strings;
            if (node.id != Integer.MAX_VALUE) {
                SubString str = strings[(int) node.id];
                for (int i = str.from; i < str.to; i++) {
                    bytes.add(str.array[i]);
                }
            } else {
                int prev = 0;
                while (true) {
                    node = subCompressor.rootBytes[prev];
                    while (node.left != null) {
                        if (stream.readNextBit()) {
                            node = node.right;
                        } else {
                            node = node.left;
                        }
                    }
                    byte b = (byte) node.id;
                    prev = Utils.byteToUnsignedInt(b);
                    if (b == 0) {
                        break;
                    } else {
                        bytes.add(b);
                    }
                }
            }
            currentAlph = !currentAlph;
        }
        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = bytes.get(i);
        }
        return res;
    }

    @Override
    public String getAlgorithmName() {
        return "Words huffman, bytes 2 huffman (" + DICTIONARY_REMOVE + ")";
    }

    @Override
    public int getDictionarySize() {
        return dictionarySize + Utils.ALPH_SIZE * Utils.ALPH_SIZE * 4;
    }

    @Override
    public void outputStatistics() {
        System.err.println("Statistics:");
        System.err.printf("compressed words: %d / %d = %.4f\n", compressedByDictionaryFinalSizeInBits / 8, compressedByDictionary, compressedByDictionaryFinalSizeInBits / 8. / compressedByDictionary);
        System.err.printf("compressed bytes: %d / %d = %.4f\n", compressedBytesFinalSizeInBits / 8, compressedBytes, compressedBytesFinalSizeInBits / 8. / compressedBytes);
        System.err.printf("words total size = %.4f\n", compressedByDictionary / (compressedByDictionary + compressedBytes + 0.));
        System.err.printf("words final size = %.4f\n", compressedByDictionaryFinalSizeInBits / (compressedByDictionaryFinalSizeInBits + compressedBytesFinalSizeInBits + 0.));

    }
}

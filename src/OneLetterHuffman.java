import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class OneLetterHuffman implements CompressAlgorithm {
    Map<Byte, Long> count = new HashMap<>();
    Map<Byte, HuffmanNode> nodes = new HashMap<>();
    private HuffmanNode root;

    @Override
    public void learn(byte[] line) {
        for (byte c : line) {
            count.put(c, count.getOrDefault(c, 0L) + 1);
        }
    }

    @Override
    public void learnEnd() {
        for (int i = 0; i < 256; i++) {
            count.putIfAbsent((byte) i, 1L);
        }
        List<HuffmanNode> nodesList = new ArrayList<>();
        for (Map.Entry<Byte, Long> entry : count.entrySet()) {
            HuffmanNode newNode = new HuffmanNode((long) entry.getKey(), entry.getValue());
            nodes.put(entry.getKey(), newNode);
            nodesList.add(newNode);
        }
        root = HuffmanTree.buildTree(nodesList);
    }

    @Override
    public byte[] compress(byte[] line) {
        BitStream stream = new BitStream();
        for (byte c : line) {
            HuffmanNode node = nodes.get(c);
            if (node == null) {
                throw new AssertionError("Not found node for c = " + c);
            }
            node.writeToStream(stream);
        }
        return stream.convertToByteArray();
    }

    @Override
    public byte[] decompress(byte[] data) {
        BitStream stream = new BitStream(data);
        ArrayList<Byte> bytes = new ArrayList<>();
        while (stream.hasMoreBits()) {
            HuffmanNode node = root;
            while (node.left != null) {
                if (stream.readNextBit()) {
                    node = node.right;
                } else {
                    node = node.left;
                }
            }
            bytes.add((byte) node.id);
        }
        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = bytes.get(i);
        }
        return res;
    }

    @Override
    public String getAlgorithmName() {
        return "One letter huffman";
    }

    @Override
    public int getDictionarySize() {
        return 256 * 4;
    }

    @Override
    public void outputStatistics() {

    }
}

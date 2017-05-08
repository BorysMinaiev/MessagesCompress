import java.util.ArrayList;

/**
 * Created by Borys Minaiev on 25.04.17.
 */
public class LZWCompression implements CompressAlgorithm {
    LZW engine = new LZW(1 << 16);

    long sumLen = 0;
    long sumPacked = 0;

    @Override
    public void learn(byte[] line) {
        int from = 0;
        sumLen += line.length;
        int packedLen = 0;
        while (from != line.length) {
            int to = from + 1;
            while (to != line.length && engine.contains(new SubString(line, from, to))) {
                to++;
            }
            packedLen += engine.getCodeSizeInBits();
            engine.add(new SubString(line, from, to));
            from = to - 1;
            if (to == line.length) {
                break;
            }
        }
        sumPacked += (packedLen + 8) / 8;
    }

    @Override
    public void learnEnd() {
        engine.assignCodes();
        System.err.println("expected ratio is: " + sumPacked + "/" + sumLen + " = " + (sumPacked * 1. / sumLen));
        long dictionarySize = getDictionarySize();
        System.err.println("dictionary size is " + dictionarySize);
    }

    @Override
    public byte[] compress(byte[] line) {
        BitStream stream = new BitStream();
        int from = 0;
        while (from != line.length) {
            int to = from + 1;
            while (to != line.length && engine.contains(new SubString(line, from, to))) {
                to++;
            }
            LZW.LZWNode node = engine.get(new SubString(line, from, to));
            if (node == null) {
                to = to - 1;
                node = engine.get(new SubString(line, from, to));
            }
            from = to;
            for (int i = engine.useBits - 1; i >= 0; i--) {
                stream.addBit(((1 << i) & node.code) != 0);
            }
        }
        return stream.convertToByteArray();
    }

    @Override
    public byte[] decompress(byte[] data) {
        BitStream stream = new BitStream(data);
        ArrayList<Byte> bytes = new ArrayList<>();
        while (stream.hasMoreBits()) {
            int code = 0;
            for (int i = 0; i < engine.useBits; i++) {
                code *= 2;
                if (stream.readNextBit()) {
                    code++;
                }
            }
            LZW.LZWNode node = engine.nodes[code];
            for (int i = node.str.from; i < node.str.to; i++) {
                bytes.add(node.str.array[i]);
            }
        }
        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = bytes.get(i);
        }
        return res;
    }

    @Override
    public String getAlgorithmName() {
        return "LZW Compression";
    }

    @Override
    public int getDictionarySize() {
        return engine.map.size() * 5;// 5 = 4 bytes for prev link + 1 byte (what char to add)
    }

    @Override
    public void outputStatistics() {

    }
}

/**
 * Created by Borys Minaiev on 25.04.17.
 */
public class PPMCompression implements CompressAlgorithm {
    PPM engine = new PPM();
    int modelLen;

    PPMCompression(int modelLen) {
        this.modelLen = modelLen;
    }

    long sumLen = 0;
    long sumCompressedLen = 0;

    @Override
    public void learn(byte[] line) {
        double expLengthBits = 0;
        for (int i = 0; i < line.length; i++) {
            boolean found = false;
            boolean[] next = new boolean[267];
            for (int contextLength = Math.min(modelLen, i); contextLength >= 0; contextLength--) {
                SubString str = new SubString(line, i - contextLength, i);
                if (engine.contains(str, line[i])) {
                    found = true;
                    expLengthBits += engine.getExpLengthInBits(str, Utils.byteToUnsignedInt(line[i]), next);
                    break;
                }
                expLengthBits += engine.getExpLengthInBits(str, 256, next);
            }
            if (!found) {
                expLengthBits += 8;
            }
            engine.add(new SubString(line, i - Math.min(i, modelLen), i), line[i]);
        }
        int expectedLen = (int) Math.ceil(expLengthBits / 8);
        sumLen += line.length;
        sumCompressedLen += expectedLen;
//        System.err.println(expectedLen + "/" + line.length);
    }

    @Override
    public void learnEnd() {
        System.err.printf("expected compression ratio: " +sumCompressedLen + "/" + sumLen + " = %.3f%c\n", sumCompressedLen * 100.0 / sumLen, '%');
        System.err.println("but this doesn't include dictionary size :(");
    }

    @Override
    public byte[] compress(byte[] line) {
        return new byte[0];
    }

    @Override
    public byte[] decompress(byte[] data) {
        return new byte[0];
    }

    @Override
    public String getAlgorithmName() {
        return "PPM";
    }

    @Override
    public int getDictionarySize() {
        return 0;
    }

    @Override
    public void outputStatistics() {

    }
}

import javax.management.RuntimeErrorException;

/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class EmptyCompressAlgorithm implements CompressAlgorithm {
    @Override
    public void learn(byte[] line) {
        // nothing to do
    }

    @Override
    public void learnEnd() {

    }

    @Override
    public byte[] compress(byte[] line) {
        return line;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }

    @Override
    public String getAlgorithmName() {
        return "Empty";
    }

    @Override
    public int getDictionarySize() {
        return 0;
    }

    @Override
    public void outputStatistics() {

    }
}

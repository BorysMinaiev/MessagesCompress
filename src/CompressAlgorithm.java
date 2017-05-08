/**
 * Created by Borys Minaiev on 24.04.17.
 */
public interface CompressAlgorithm {
    void learn(byte[] line);
    void learnEnd();
    byte[] compress(byte[] line);
    byte[] decompress(byte[] data);
    String getAlgorithmName();

    int getDictionarySize();
    void outputStatistics();
}

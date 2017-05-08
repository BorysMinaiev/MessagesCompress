import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class Main {
    private static final String DATABASE_FILE = "twits.txt";
    private static final String DATABASE_FILE_2 = "book1.txt";
    private static final String DATABASE_FILE_3 = "text.txt";
    private static final String DATABASE_FILE_4 = "text8";
    private static final String DATABASE_FILE_5 = "random-twits-ru.txt";

    private static void checkAlgorithm(final CompressAlgorithm algorithm, final DataBase dataBase) {
        System.err.println("Check \"" + algorithm.getAlgorithmName() + "\"");
        int it = 0;
        for (byte[] s : dataBase.twits) {
            if (it++ % 100 == 0) {
//                System.err.println("iter = " + it);
            }
            algorithm.learn(s);
        }
        algorithm.learnEnd();
        double sumLen = 0, sumCompressedLen = 0;
        for (byte[] s : dataBase.twits) {
            sumLen += s.length;
            byte[] compressedMessage = algorithm.compress(s);
            byte[] decompressedMessage = algorithm.decompress(compressedMessage);
            if (!Arrays.equals(s, decompressedMessage)) {
                System.err.println("Was " + Arrays.toString(s));
                System.err.println("Now " + Arrays.toString(decompressedMessage));
                throw new AssertionError("Decompressed message is not the same as message given to algorithm");
            }
            sumCompressedLen += compressedMessage.length;
        }
        System.err.printf("Compressed messages size are %.2f%c of original size\n", 100.0 * sumCompressedLen / sumLen, '%');
        System.err.printf("Real ratio (including dictionary size) is = %.2f%c\n", 100.0 * (sumCompressedLen + algorithm.getDictionarySize()) / sumLen, '%');
        System.err.println("");
        algorithm.outputStatistics();
        System.err.println("");
    }

    public static void main(String[] args) {
        DataBase dataBase = new DataBase(DATABASE_FILE_5);
        System.err.println("Data base was read");
        List<CompressAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new EmptyCompressAlgorithm());
        algorithms.add(new OneLetterHuffman());
        algorithms.add(new LZWCompression());
        algorithms.add(new WordsHuffman(3));
            algorithms.add(new WordsHuffmanBytesHuffman(4));
        algorithms.add(new WordsHuffmanBytes2Huffman(6));

//        algorithms.add(new PPMCompression(3));
        while (algorithms.size() > 0) {
            CompressAlgorithm algorithm = algorithms.get(0);
            checkAlgorithm(algorithm, dataBase);
            algorithms.remove(0);
        }
    }
}

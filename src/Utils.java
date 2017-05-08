import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Borys Minaiev on 25.04.17.
 */
public class Utils {
    public static char convertToChar(byte b) {
        return Charset.forName("cp1251").decode(ByteBuffer.wrap(new byte[]{b})).get(0);
    }

    public static String convertToString(byte[] line) {
        return Charset.forName("cp1251").decode(ByteBuffer.wrap(line)).toString();
    }

    public static String convertToString(SubString str) {
       return convertToString(Arrays.copyOfRange(str.array, str.from, str.to));
    }

    public static int byteToUnsignedInt(byte b) {
        if (b < 0) {
            return 256 + b;
        }
        return b;
    }

    final static int ALPH_SIZE = 256;

    public static boolean isAlphabetic(byte b) {
        char c = Utils.convertToChar(b);
        if (c == '@') {
            return true; // twitter-only hack
        }
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        if (c >= 'а' && c <= 'я') {
            return true;
        }
        if (c >= 'A' && c <= 'Я') {
            return true;
        }
        if (c == 'ё' || c == 'Ё') {
            return true;
        }
        return false;
    }

}

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class DataBase {
    List<byte[]> twits = new ArrayList<>();

    Random rnd = new Random(123);
    ArrayList<String> splitLongText(String s) {
        ArrayList<String> res = new ArrayList<>();
        String[] words = s.split(" ");
        int it = 0;
        while (it != words.length) {
            int needLen = rnd.nextInt(200);
            String zz = "";
            while (zz.length() < needLen && it != words.length) {
                if (zz.length() != 0) {
                    zz = zz + " ";
                }
                zz += words[it++];
            }
            res.add(zz);
        }
        return res;
    }

    DataBase(String fileName) {
        try {
            int z = 0;
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                ArrayList<String> tmp = splitLongText(s);
                for (String ss : tmp) {
                    if (z ++ < 100) {
                        System.err.println("!" + ss);
                    }
                    byte[] res = ss.getBytes("cp1251");
                    twits.add(res);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

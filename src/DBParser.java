import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class DBParser {
    private static PrintWriter pw;

    private static void parseOneLine(String line) {
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c != ',' && c != '\"') {
                sb.append(c);
            }
        }
        if (sb.length() != 0) {
            pw.println(sb.toString());
        }
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("positive.csv"));
            pw = new PrintWriter(new File("twits.txt"));
            // db.sql was downloaded from http://study.mokoron.com/
            int iter = 0;
            while (true) {
                iter++;
                if (iter % 10000 == 0) {
                    System.err.println("iter = " + iter);
                }
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                parseOneLine(line);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



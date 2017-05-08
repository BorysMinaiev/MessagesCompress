import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Borys Minaiev on 25.04.17.
 */
public class PPM {

    class Node {
        Node[] next = new Node[256];
        int[] count = new int[257];

        Node() {
            count[256] = 1;
        }
    }

    Node root = new Node();

    Node findNode(SubString s) {
        Node node = root;
        for (int i = s.to - 1; i >= s.from; i--) {
            node = node.next[Utils.byteToUnsignedInt(s.array[i])];
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    boolean contains(SubString s, byte next) {
        Node node = findNode(s);
        return node != null && node.count[Utils.byteToUnsignedInt(next)] != 0;
    }

    double getExpLengthInBits(SubString s, int next, boolean[] notCount) {
        Node node = findNode(s);
        if (node == null) {
            return 0;
        }
        long sum = 0;
        for (int i = 0; i < node.count.length; i++) {
            if (notCount[i]) {
                continue;
            }
            notCount[i] = true;
            sum += node.count[i];
        }
        notCount[256] = false;
        double prob = node.count[next] * 1. / sum;
        return Math.ceil(-Math.log(prob) / Math.log(2));
    }

    void add(SubString s, byte nextByte) {
        Node node = root;
        node.count[Utils.byteToUnsignedInt(nextByte)]++;
        for (int i = s.to - 1; i >= s.from; i--) {
            Node next = node.next[Utils.byteToUnsignedInt(s.array[i])];
            if (next == null) {
                next = node.next[Utils.byteToUnsignedInt(s.array[i])] = new Node();
            }
            next.count[Utils.byteToUnsignedInt(nextByte)]++;
            node = next;
        }
    }

    PPM() {

    }
}

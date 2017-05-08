/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class HuffmanNode implements Comparable<HuffmanNode>{
    long id;
    long count;
    HuffmanNode left, right;

    int codeLen;
    static long iter = -1;
    long code;

    public void writeToStream(BitStream stream) {
        for (int i = codeLen - 1; i >= 0; i--) {
            stream.addBit((code & (1L << i)) != 0);
        }
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.left = left;
        this.right = right;
        id = --iter;
        count = left.count + right.count;
    }

    public HuffmanNode(long id, long count) {
        this.id = id;
        this.count = count;
    }

    public void assignCode(long code, int codeLen) {
        if (codeLen > 64) {
            throw new AssertionError("Want to assign code lenght > 64");
        }
        this.codeLen = codeLen;
        this.code = code;
        if (left != null) {
            left.assignCode(code * 2, codeLen + 1);
            right.assignCode(code * 2 + 1, codeLen + 1);
        }
    }

    @Override
    public int compareTo(HuffmanNode o) {
        if (count != o.count) {
            return Long.compare(count, o.count);
        }
        return Long.compare(id, o.id);
    }
}

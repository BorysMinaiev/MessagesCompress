import java.util.ArrayList;

/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class BitStream {
    ArrayList<Boolean> list = new ArrayList<>();
    private int readIterator = 0;

    public void addBit(boolean value) {
        list.add(value);
    }

    public boolean hasMoreBits() {
        return readIterator < list.size();
    }

    public boolean readNextBit() {
        if (readIterator == list.size()) {
            throw new AssertionError();
        }
        return list.get(readIterator++);
    }

    byte[] convertToByteArray() {
        // add "01111" up to end of char
        list.add(false);
        while (list.size() % 8 != 0) {
            list.add(true);
        }
        int len = (list.size() + 7) / 8;
        byte[] res = new byte[len];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < 8; j++) {
                int pos = i * 8 + j;
                if (list.get(pos)) {
                    res[i] |= 1 << j;
                }
            }
        }
        return res;
    }

    void writeByte(byte b) {
        for (int i = 0; i < 8; i++) {
            addBit(((1 << i) & b) != 0);
        }
    }

    byte readByte() {
        byte res = 0;
        for (int i = 0; i < 8; i++) {
            if (readNextBit()) {
                res |= 1 << i;
            }
        }
        return res;
    }

    BitStream(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < 8; j++) {
                list.add((array[i] & (1 << j)) != 0);
            }
        }
        while (list.get(list.size() - 1)) {
            list.remove(list.size() - 1);
        }
        list.remove(list.size() - 1);
    }

    BitStream() {

    }
}

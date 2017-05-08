import java.util.Arrays;

/**
 * Created by Borys Minaiev on 25.04.17.
 */
public class SubString {
    byte[] array;
    int from, to;

    public SubString(byte[] array) {
        this.from = 0;
        this.to = array.length;
        this.array = array;
    }

    @Override
    public String toString() {
        return "SubString{" +
                "array=" + Arrays.toString(array) +
                '}';
    }

    // [from, to)
    public SubString(byte[] array, int from, int to) {
        this.array = array;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubString subString = (SubString) o;
        if (to - from != subString.to - subString.from) {
            return false;
        }

        for (int i = from; i < to; i++) {
            if (array[i] != subString.array[i - from + subString.from]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int res = 0;
        for (int i = from; i < to; i++) {
            res = res * 239 + array[i];
        }
        return res;
    }
}

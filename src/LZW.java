import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Borys Minaiev on 25.04.17.
 */
public class LZW {
    class LZWNode {
        LZWNode next, prev;
        SubString str;
        int code;

        void remove() {
            prev.next = next;
            next.prev = prev;
        }

        LZWNode(SubString str) {
            this.str = str;
        }
    }

    void assignCodes() {
        nodes = new LZWNode[map.size()];
        int it = 0;
        for (Map.Entry<SubString, LZWNode> entry : map.entrySet()){
            entry.getValue().code = it;
            nodes[it++] = entry.getValue();
        }
        useBits = getCodeSizeInBits();
    }

    int useBits;

    LZWNode[] nodes;

    HashMap<SubString, LZWNode> map = new HashMap<>();
    LZWNode LRU = new LZWNode(new SubString(new byte[]{}));

    void moveToFront(LZWNode node) {
        if (node.prev == null) {
            // one byte strings are not stored in LRU
            return;
        }
        node.remove();
        node.next = LRU.next;
        node.prev = LRU;
        node.prev.next = node;
        node.next.prev = node;
    }

    public int getCodeSizeInBits() {
        int res = 8;
        while ((1 << res) < map.size()) {
            res++;
        }
        return res;
    }

    void removeFromLRU() {
        LZWNode node = LRU.prev;
        map.remove(node.str);
        node.remove();
    }

    int size;

    LZW(int size) {
        LRU.next = LRU.prev = LRU;
        this.size = size;
        for (int x = 0; x < 256; x++) {
            SubString sub = new SubString(new byte[]{(byte) x});
            map.put(sub, new LZWNode(sub));
        }
    }

    boolean add(SubString str) {
        if (map.containsKey(str)) {
            return false;
        }
        LZWNode node = new LZWNode(str);
        node.prev = node.next = node;
        moveToFront(node);
        map.put(str, node);
        if (map.size() > size) {
            removeFromLRU();
        }
        return true;
    }

    LZWNode get(SubString str) {
        return map.get(str);
    }

    boolean contains(SubString str) {
        LZWNode node = map.get(str);
        if (node == null) {
            return false;
        }
        moveToFront(node);
        return true;
    }

}

import java.util.List;
import java.util.TreeSet;

/**
 * Created by Borys Minaiev on 24.04.17.
 */
public class HuffmanTree {


    public static HuffmanNode buildTree(List<HuffmanNode> nodes) {
        TreeSet<HuffmanNode> treeSet = new TreeSet<>(nodes);
        while (treeSet.size() > 1) {
            HuffmanNode one = treeSet.pollFirst();
            HuffmanNode two = treeSet.pollFirst();
            treeSet.add(new HuffmanNode(one, two));
        }
        treeSet.first().assignCode(0, 0);
        return treeSet.first();
    }
}

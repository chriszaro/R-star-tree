import java.util.ArrayList;
import java.util.List;

public class PrettyPrinter {
    public static String printRStarTree(Node root) {
        ArrayList<String> treeData = new ArrayList<>();
        traverseRStarTree(root, treeData, 0);
        return String.join(",", treeData);
    }

    private static void traverseRStarTree(Node node, List<String> treeData, int depth) {
        if (node == null) return;

        /*for (Node child : node.childNodes) {
            traverseRStarTree(child, treeData, depth + 1);
        }*/
        // Recursively traverse child nodes
        if (node instanceof NoLeafNode){
            NoLeafNode n = (NoLeafNode) node;
            for (RectangleEntry re : n.getRectangleEntries()) {
                Node child = re.getChild();
                traverseRStarTree(child, treeData, depth + 1);
            }
//            traverseRStarTree(child, treeData, depth + 1);
        } else if (node instanceof LeafNode) { // unless we've hit a leaf node
            LeafNode n = (LeafNode) node;
            for (PointEntry pe: n.getPointEntries()) {
//                traverseRStarTree(child, treeData, depth + 1);
                // Append node data to the list in a structured way
                String nodeData = String.format("%d:%s", depth, pe.toString()); // Adjust as per your node structure
                treeData.add(nodeData);
            }
        }
    }
}

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Create a list of points
//        List<Point> points = new ArrayList<>();
//        points.add(new Point(7, 7));
//        points.add(new Point(4, 6));
//        points.add(new Point(7, 3));
//        points.add(new Point(3, 1));
//
//        // Sort the points using Z-order curve
//        List<Point> sortedPoints = ZOrderCurveSort.sortPoints(points);
//
//        // Print the sorted points
//        for (Point point : sortedPoints) {
//            System.out.println(point);

        //725 records
        LinkedList<PointEntry> entries = IO.loadInput("assets/mapEast.osm");

//        //203
        entries.addAll(IO.loadInput("assets/mapCenter.osm"));
//
//        //623
        entries.addAll(IO.loadInput("assets/mapUnis.osm"));
//
//        //725
//        entries.addAll(IO.loadInput("assets/mapEast.osm"));

//        //2276 total

        List<PointEntry> sortedEntries = ZOrderCurveSort.sortPoints(entries);


        int M = 16;
//        int num_of_nodes = entries.size() / M + 1;

//        LinkedList<LeafNode> leafNodesList = new LinkedList<>(); //list with level 0 nodes
//        LinkedList<RectangleEntry> rectangleEntriesList = new LinkedList<>(); //list for the next iteration, 1 rectangle entry in [level 1 node] is 1 [level 0 node]
//
//        Iterator<PointEntry> iterator = sortedEntries.iterator(); //list with sorted point entries to fill level 0 nodes
//        for (int i = 0; i < num_of_nodes; i++) { //posa nodes
//            while (iterator.hasNext()) {
//                LinkedList<PointEntry> tempList = new LinkedList<>(); //list with M elements
//
//                for (int j = 0; j < M; j++) { //16ades
//                    if (iterator.hasNext()) tempList.add(iterator.next());
//                }
//
//                LeafNode tempNode = new LeafNode(tempList); //create leaf node with M entries
//                for (PointEntry pointEntry : tempList) { //update node's entries about their container
//                    pointEntry.setContainer(tempNode);
//                }
//
//                leafNodesList.add(tempNode); // add leaf node to list with level 0 nodes
//
//                RectangleEntry tempRect = new RectangleEntry(tempNode); //create rectangle and connect it with leaf node
//                tempNode.setParent(tempRect); //connect leaf node with rectangle
//
//                rectangleEntriesList.add(tempRect); //add rectangle to list with level 0 rectangles
//            }
//        }
//        System.out.println(leafNodesList.size());

//        int N = leafNodesList.size() / M + 1;
//        LinkedList<RectangleEntry> rectangleEntriesList2 = new LinkedList<>();
//        LinkedList<NoLeafNode> noLeafNodesList = new LinkedList<>();
//
//        //dimioyrgo mia lista me NoLeafNodes kai mia lista me rectangles
//        Iterator<RectangleEntry> iterator2 = rectangleEntriesList.iterator();
//        for (int i = 0; i < N; i++) { //posa nodes
//            while (iterator2.hasNext()) {
//                LinkedList<RectangleEntry> tempList = new LinkedList<>();
//                for (int j = 0; j < M; j++) { //16ades
//                    if (iterator2.hasNext()) tempList.add(iterator2.next());
//                }
//
//                NoLeafNode tempNode = new NoLeafNode(tempList);
//                for (RectangleEntry rectangleEntry : tempList) {
//                    rectangleEntry.setContainer(tempNode);
//                }
//                noLeafNodesList.add(tempNode);
//
//                LinkedList<Node> temp2 = new LinkedList<>();
//
//                for (RectangleEntry rectangleEntry : tempNode.getRectangleEntries()) {
//                    temp2.add(rectangleEntry.getChild());
//                }
//                tempNode.setChildren(temp2);
//
//                RectangleEntry tempRect = new RectangleEntry(tempNode);
//                tempNode.setParent(tempRect);
//                rectangleEntriesList2.add(tempRect);
//            }
//        }
//
//        System.out.println(noLeafNodesList.size());

//        int N2 = noLeafNodesList.size() / M + 1;
//        LinkedList<RectangleEntry> rectangleEntriesList3 = new LinkedList<>();
//        LinkedList<NoLeafNode> noLeafNodesList2 = new LinkedList<>();
//        //dimioyrgo mia lista me NoLeafNodes kai mia lista me rectangles
//        Iterator<RectangleEntry> iterator3 = rectangleEntriesList2.iterator();
//        for (int i = 0; i < N2; i++) { //posa nodes
//            while (iterator3.hasNext()) {
//                LinkedList<RectangleEntry> tempList = new LinkedList<>();
//                for (int j = 0; j < M; j++) { //16ades
//                    if (iterator3.hasNext()) tempList.add(iterator3.next());
//                }
//
//                NoLeafNode tempNode = new NoLeafNode(tempList);
//                for (RectangleEntry rectangleEntry : tempList) {
//                    rectangleEntry.setContainer(tempNode);
//                }
//                noLeafNodesList2.add(tempNode);
//
//                LinkedList<Node> temp2 = new LinkedList<>();
//
//                for (RectangleEntry rectangleEntry : tempNode.getRectangleEntries()) {
//                    temp2.add(rectangleEntry.getChild());
//                }
//                tempNode.setChildren(temp2);
//
//                RectangleEntry tempRect = new RectangleEntry(tempNode);
//                tempNode.setParent(tempRect);
//                rectangleEntriesList3.add(tempRect);
//            }
//        }
//
//        System.out.println(noLeafNodesList2.size());

        LinkedList<LinkedList> leaves = bottomUpLevel0(sortedEntries, M);

        LinkedList<LinkedList> root = iterative(leaves.get(0), M, leaves.get(1));

        for (NoLeafNode noLeafNode : (LinkedList<NoLeafNode>) root.get(0)) {
            noLeafNode.setRoot(true);
        }

    }

    public static LinkedList<LinkedList> iterative(LinkedList nodeList, int M, LinkedList<RectangleEntry> rectangleEntriesListIn) {
        LinkedList<LinkedList> result = bottomUp(nodeList, M, rectangleEntriesListIn);
        System.out.println(result.get(0).size());
        while (result.get(0).size() != 1) {
            result = bottomUp(result.get(0), M, result.get(1));
        }
        System.out.println(result.get(0).size());

        return result;
    }

    public static LinkedList<LinkedList> bottomUp(LinkedList nodeList, int M, LinkedList<RectangleEntry> rectangleEntriesListIn) {

        int N2 = nodeList.size() / M + 1;
        LinkedList<RectangleEntry> rectangleEntriesListOut = new LinkedList<>();
        LinkedList<NoLeafNode> noLeafNodesList = new LinkedList<>();
        //dimioyrgo mia lista me NoLeafNodes kai mia lista me rectangles
        Iterator<RectangleEntry> iterator3 = rectangleEntriesListIn.iterator();
        for (int i = 0; i < N2; i++) { //posa nodes
            while (iterator3.hasNext()) {
                LinkedList<RectangleEntry> tempList = new LinkedList<>();
                for (int j = 0; j < M; j++) { //16ades
                    if (iterator3.hasNext()) tempList.add(iterator3.next());
                }

                NoLeafNode tempNode = new NoLeafNode(tempList);
                for (RectangleEntry rectangleEntry : tempList) {
                    rectangleEntry.setContainer(tempNode);
                }
                noLeafNodesList.add(tempNode);

                LinkedList<Node> temp2 = new LinkedList<>();

                for (RectangleEntry rectangleEntry : tempNode.getRectangleEntries()) {
                    temp2.add(rectangleEntry.getChild());
                }
                tempNode.setChildren(temp2);

                RectangleEntry tempRect = new RectangleEntry(tempNode);
                tempNode.setParent(tempRect);
                rectangleEntriesListOut.add(tempRect);
            }
        }

        LinkedList<LinkedList> returnable = new LinkedList<>();
        returnable.add(noLeafNodesList);
        returnable.add(rectangleEntriesListOut);
        return returnable;
    }

    public static LinkedList<LinkedList> bottomUpLevel0(List<PointEntry> sortedEntries, int M) {

        int num_of_nodes = sortedEntries.size() / M + 1;

        LinkedList<LeafNode> leafNodesList = new LinkedList<>(); //list with level 0 nodes
        LinkedList<RectangleEntry> rectangleEntriesListOut = new LinkedList<>(); //list for the next iteration, 1 rectangle entry in [level 1 node] is 1 [level 0 node]

        Iterator<PointEntry> iterator = sortedEntries.iterator(); //list with sorted point entries to fill level 0 nodes
        for (int i = 0; i < num_of_nodes; i++) { //posa nodes
            while (iterator.hasNext()) {
                LinkedList<PointEntry> tempList = new LinkedList<>(); //list with M elements

                for (int j = 0; j < M; j++) { //16ades
                    if (iterator.hasNext()) tempList.add(iterator.next());
                }

                LeafNode tempNode = new LeafNode(tempList); //create leaf node with M entries
                for (PointEntry pointEntry : tempList) { //update node's entries about their container
                    pointEntry.setContainer(tempNode);
                }

                leafNodesList.add(tempNode); // add leaf node to list with level 0 nodes

                RectangleEntry tempRect = new RectangleEntry(tempNode); //create rectangle and connect it with leaf node
                tempNode.setParent(tempRect); //connect leaf node with rectangle

                rectangleEntriesListOut.add(tempRect); //add rectangle to list with level 0 rectangles
            }
        }
        System.out.println(leafNodesList.size());


        LinkedList<LinkedList> returnable = new LinkedList<>();
        returnable.add(leafNodesList);
        returnable.add(rectangleEntriesListOut);
        return returnable;
    }

}

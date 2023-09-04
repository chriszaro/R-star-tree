import java.util.*;

/**
 * What we want to do is split a Rectangle into two rectangles
 * And we must find the best way to do so
 */
public class AlgorithmSplit {

    public static class RectangleEntryGroups {
        List<RectangleEntry> groupOne;
        List<RectangleEntry> groupTwo;

        public RectangleEntryGroups(List<RectangleEntry> groupOne, List<RectangleEntry> groupTwo) {
            this.groupOne = groupOne;
            this.groupTwo = groupTwo;
        }

        public List<RectangleEntry> getGroupOne() {
            return groupOne;
        }

        public List<RectangleEntry> getGroupTwo() {
            return groupTwo;
        }
    }

    public static class PointEntryGroups {
        LinkedList<PointEntry> groupOne;
        LinkedList<PointEntry> groupTwo;

        public PointEntryGroups(LinkedList<PointEntry> groupOne, LinkedList<PointEntry> groupTwo) {
            this.groupOne = groupOne;
            this.groupTwo = groupTwo;
        }

        public LinkedList<PointEntry> getGroupOne() {
            return groupOne;
        }

        public LinkedList<PointEntry> getGroupTwo() {
            return groupTwo;
        }
    }

    public static PointEntryGroups splitLeafNode(RectangleEntry rect, PointEntry pe) {
        if (!rect.getChild().leaf) return null;
        // if we're dealing with a leaf
        LeafNode leaf = (LeafNode) rect.getChild();
        // not sure if a new variable is needed. but it's all references, so it doesn't really matter
//        List<PointEntry> points = ZOrderCurveSort.sortPoints(leaf.getPointEntries());
        LinkedList<PointEntry> points = leaf.getPointEntries();
        points.add(pe);
        LinkedList<PointEntry> groupOne = new LinkedList<>();
        LinkedList<PointEntry> groupTwo = new LinkedList<>();
        int mid = points.size() / 2; // get median
        // but why was this sort needed?

        for (int i = 0; i < points.size(); i++) {
            PointEntry pointEntry = points.get(i);
            if (i <= mid)
                groupOne.add(pointEntry);
            else
                groupTwo.add(pointEntry);

        }
//        groupOne = (LinkedList<PointEntry>) points.subList(0, mid);
//        groupTwo = (LinkedList<PointEntry>) points.subList(mid, points.size());
        return new PointEntryGroups(groupOne, groupTwo);
    }

    public static NoLeafNode split(LeafNode N, PointEntry pe) {
        NoLeafNode returnable = null;

        RectangleEntry parent = N.getParent(); //rootEntry

        PointEntryGroups groups = splitLeafNode(parent, pe);

        LeafNode ln1 = new LeafNode(groups.getGroupOne());
        LeafNode ln2 = new LeafNode(groups.getGroupTwo());

        RectangleEntry re1 = new RectangleEntry(ln1);
        ln1.setParent(re1);
        RectangleEntry re2 = new RectangleEntry(ln2);
        ln2.setParent(re2);

        NoLeafNode parentContainer = (NoLeafNode) parent.getContainer(); // imaginary root
        if (N.isRoot()){
            LinkedList<RectangleEntry> temp = new LinkedList<>();
            temp.add(re1);
            temp.add(re2);

            NoLeafNode newRoot = new NoLeafNode(temp);
            N.setRoot(false);
            newRoot.setRoot(true);
            re1.setContainer(newRoot);
            re2.setContainer(newRoot);
            return newRoot;
        }

        if (parentContainer.getRectangleEntries().size() + 1 <= Main.M) {
            parentContainer.getRectangleEntries().remove(parent);
            parentContainer.getRectangleEntries().add(re1);
            parentContainer.getRectangleEntries().add(re2);
            re1.setContainer(parentContainer);
            re2.setContainer(parentContainer);
        } else {
            while (true) {
                LinkedList<RectangleEntry> temp = new LinkedList<>();
                temp.add(re1);
                temp.add(re2);

                parent = parentContainer.getParent();

                RectangleEntryGroups rGroups = splitNonLeafNode(parent, temp);
                NoLeafNode nln1 = new NoLeafNode((LinkedList<RectangleEntry>) rGroups.getGroupOne());
                NoLeafNode nln2 = new NoLeafNode((LinkedList<RectangleEntry>) rGroups.getGroupTwo());

                RectangleEntry re3 = new RectangleEntry(nln1);
                nln1.setParent(re3);
                RectangleEntry re4 = new RectangleEntry(nln2);
                nln2.setParent(re4);

                NoLeafNode parentContainer2 = (NoLeafNode) parent.getContainer();
                if (parentContainer2.getRectangleEntries().size() + 1 <= Main.M) {
                    parentContainer.getRectangleEntries().remove(parent);
                    parentContainer.getRectangleEntries().add(re3);
                    parentContainer.getRectangleEntries().add(re4);
                    re3.setContainer(parentContainer);
                    re4.setContainer(parentContainer);
                    break;
                }

                re1 = re3;
                re2 = re4;
                if (parentContainer.getParent() == null) { // exoume ftasei riza
                    // if (parentContainer.isRoot()) {
                    parentContainer.setRoot(false);
                    LinkedList<RectangleEntry> temp2 = new LinkedList<>();
                    temp2.add(re1);
                    temp2.add(re2);
                    NoLeafNode newRoot = new NoLeafNode(temp2);
                    re1.setContainer(newRoot);
                    re2.setContainer(newRoot);
                    newRoot.setRoot(true);
                    //}
                    return newRoot;
                }
            }
        }
        return returnable;
    }

    public static RectangleEntryGroups splitNonLeafNode(RectangleEntry rect, LinkedList<RectangleEntry> rects) {
        if (rect.getChild().leaf) return null;
        // else:
        List<RectangleEntry> groupOne;
        List<RectangleEntry> groupTwo;
//        int dimensions = rect.getRectangle().getStartPoint().getCoords().size();
        NoLeafNode node;
        node = (NoLeafNode) rect.getChild();
        LinkedList<RectangleEntry> rectangleEntries = node.getRectangleEntries();
        rectangleEntries.addAll(rects);

        NoLeafNode temp2 = new NoLeafNode(rectangleEntries);
        RectangleEntry temp = new RectangleEntry(temp2);
        int splitAxis = chooseSplitAxis(temp);
        int splitIndex = chooseSplitIndex(temp, splitAxis);

        /*for (int i = 0; i < rectangleEntries.size(); i++) {
            RectangleEntry rectangleEntry = rectangleEntries.get(i);
            if (i <= splitIndex)
                groupOne.add(rectangleEntry);
            else
                groupTwo.add(rectangleEntry);
        }
        */
        groupOne = rectangleEntries.subList(0, splitIndex);
        groupTwo = rectangleEntries.subList(splitIndex, rectangleEntries.size());
        RectangleEntryGroups groups = new RectangleEntryGroups(groupOne, groupTwo);
        return groups;
    }

    /**
     * Determine which axis/dimension should be selected by calculating goodness values
     */
    public static int chooseSplitAxis(RectangleEntry parentRect) {
        int M = ((NoLeafNode) parentRect.getChild()).getRectangleEntries().size();
        int m = (int) (M * 0.4);
        // for each axis/dimension! --> we need an arraylist (A) of arraylists (B)
        // size of A -> # of dimensions
        // A holds D arraylists
        // each array list has the starting points' values of the axis at index i
        // and for each such array we need to sort it
        int dimensions = parentRect.getRectangle().getStartPoint().getCoords().size();
        // let's make this as clear as day

        if (parentRect.getChild().leaf) {
/*
            LeafNode leaf = (LeafNode) parentRect.getChild();
            // not sure if a new variable is needed. but it's all references so it doesn't really matter
            List<PointEntry> points = ZOrderCurveSort.sortPoints(leaf.getPointEntries());
            int mid = points.size()/2; // get median WRONG!
            // we're looking for an axis! not an index
            // this was all needless
*/
            // HENCE:
            //  here we just return 0
            //  because no matter what we do it's a leaf
            //  and leaves cannot produce goodness values axis-wise
            //  we just return the first dimension
            //  and deal with the index later on
            //  with a z order curve sort
            return 0; // return the first dimension
        }
        // else:
        NoLeafNode node;
        node = (NoLeafNode) parentRect.getChild();

        ArrayList<RectanglePointPair> startingPoints = new ArrayList<>(M);
        ArrayList<RectanglePointPair> endingPoints = new ArrayList<>(M);
        // first get all start and end points
        for (RectangleEntry rectangleEntry : node.getRectangleEntries()) {
            Point startPoint = rectangleEntry.getRectangle().getStartPoint();
            Point endPoint = rectangleEntry.getRectangle().getEndPoint();

            startingPoints.add(new RectanglePointPair(rectangleEntry, startPoint));
            endingPoints.add(new RectanglePointPair(rectangleEntry, endPoint));
        }

        // int M = startingPoints.size(); // M is the number of POINTS we have, or the number of axes
        double min = -1d;
        int selectedAxis = 0; // AXIS = DIMENSION
        for (int i = 0; i < dimensions; i++) { // for each AXIS !
            ArrayList<RectangleEntryDoublePair> startCoordsOfAxisI = new ArrayList<>(M);
            ArrayList<RectangleEntryDoublePair> endCoordsOfAxisI = new ArrayList<>(M);
            // now for each POINT
            for (int j = 0; j < M; j++) {
                RectangleEntryDoublePair startPair = new RectangleEntryDoublePair(startingPoints.get(j).getRectangleEntry(), startingPoints.get(j).getPoint().getCoords().get(i));
                RectangleEntryDoublePair endPair = new RectangleEntryDoublePair(endingPoints.get(j).getRectangleEntry(), endingPoints.get(j).getPoint().getCoords().get(i));
                startCoordsOfAxisI.add(startPair);
                endCoordsOfAxisI.add(endPair);
            }
            Collections.sort(startCoordsOfAxisI, new RectangleEntryDoublePairComparator());
            Collections.sort(endCoordsOfAxisI, new RectangleEntryDoublePairComparator());
            // now determine all distributions. how?
            // for each sort, M - 2m + 2 distributions of the M+1 entries into two groups are determined
            // for the k-th distribution, where k is in [1, M-2m+2], the first group contains
            // the first m-1+k entries, the second group the remaining ones
            // for these two groups, goodness values are determined
            // now, in the current axis

            for (int j = 0; j < M - 2 * m + 2; j++) {
                int k = m - 1 + j; // taken straight from the R*-tree paper
                List<RectangleEntryDoublePair> groupOneStartPairs = startCoordsOfAxisI.subList(0, k);
                List<RectangleEntryDoublePair> groupTwoStartPairs = startCoordsOfAxisI.subList(k, startCoordsOfAxisI.size());

                List<RectangleEntryDoublePair> groupOneEndPairs = endCoordsOfAxisI.subList(0, k);
                List<RectangleEntryDoublePair> groupTwoEndPairs = endCoordsOfAxisI.subList(k, endCoordsOfAxisI.size());

                double goodnessGroupOneStartPairs = determineGoodnessValueMarginWise(groupOneStartPairs);
                double goodnessGroupTwoStartPairs = determineGoodnessValueMarginWise(groupTwoStartPairs);
                double goodnessGroupOneEndPairs = determineGoodnessValueMarginWise(groupOneEndPairs);
                double goodnessGroupTwoEndPairs = determineGoodnessValueMarginWise(groupTwoEndPairs);
                double sum = goodnessGroupOneStartPairs + goodnessGroupTwoStartPairs +
                        goodnessGroupOneEndPairs + goodnessGroupTwoEndPairs;
                if (min == -1d || min > sum) {
                    min = sum;
                    selectedAxis = i;
                }
                // now do something with choose split index
                // with different goodness values and such

            }
        }
        return selectedAxis;
    }

    private static class RectanglePointPair {
        private RectangleEntry rectangleEntry;
        private Point point;

        public RectanglePointPair(RectangleEntry rectangleEntry, Point point) {
            this.rectangleEntry = rectangleEntry;
            this.point = point;
        }

        public RectangleEntry getRectangleEntry() {
            return rectangleEntry;
        }

        public Point getPoint() {
            return point;
        }
    }

    private static class RectangleEntryDoublePair {
        private RectangleEntry rectangleEntry;
        private Double value; // the value of some point

        public RectangleEntryDoublePair(RectangleEntry rectangleEntry, Double value) {
            this.rectangleEntry = rectangleEntry;
            this.value = value;
        }

        public RectangleEntry getRectangleEntry() {
            return rectangleEntry;
        }

        public Double getValue() {
            return value;
        }
    }

    private static class RectangleEntryDoublePairComparator implements Comparator<RectangleEntryDoublePair> {
        @Override
        public int compare(RectangleEntryDoublePair o, RectangleEntryDoublePair t1) {
            return Double.compare(o.getValue(), t1.getValue());
        }
    }

    /**
     * Determine the entry/index at which the split will happen
     */
    public static int chooseSplitIndex(RectangleEntry parentRect, int axis) {
        int M = ((NoLeafNode) parentRect.getChild()).getRectangleEntries().size();
        int m = (int) (M * 0.4);

        int dimensions = parentRect.getRectangle().getStartPoint().getCoords().size();
        // let's make this as clear as day

        if (parentRect.getChild().leaf) return 0;
        /*{
            // if we're dealing with a leaf
            LeafNode leaf = (LeafNode) parentRect.getChild();
            // not sure if a new variable is needed. but it's all references, so it doesn't really matter
//            List<PointEntry> points = ZOrderCurveSort.sortPoints(leaf.getPointEntries());
            int mid = leaf.getPointEntries().size() / 2; // get median
            // this code block will never be reached though
        }*/
        // else:
        NoLeafNode node;
        node = (NoLeafNode) parentRect.getChild();

        ArrayList<RectanglePointPair> startingPoints = new ArrayList<>(dimensions);
        ArrayList<RectanglePointPair> endingPoints = new ArrayList<>(dimensions);
        // first get all start and end points
        for (RectangleEntry rectangleEntry : node.getRectangleEntries()) {
            Point startPoint = rectangleEntry.getRectangle().getStartPoint();
            Point endPoint = rectangleEntry.getRectangle().getEndPoint();

            startingPoints.add(new RectanglePointPair(rectangleEntry, startPoint));
            endingPoints.add(new RectanglePointPair(rectangleEntry, endPoint));
        }

        // int M = startingPoints.size(); // M is the number of POINTS we have, or the number of axes
        double min = -1d;
        int selectedIndex = 0;
        // for the given axis/dimension
        ArrayList<RectangleEntryDoublePair> startCoordsOfGivenAxis = new ArrayList<>(M);
        ArrayList<RectangleEntryDoublePair> endCoordsOfGivenAxis = new ArrayList<>(M);
        // now for each POINT
        for (int j = 0; j < M; j++) {
            RectangleEntryDoublePair startPair = new RectangleEntryDoublePair(startingPoints.get(j).getRectangleEntry(), startingPoints.get(j).getPoint().getCoords().get(axis));
            RectangleEntryDoublePair endPair = new RectangleEntryDoublePair(endingPoints.get(j).getRectangleEntry(), endingPoints.get(j).getPoint().getCoords().get(axis));
            startCoordsOfGivenAxis.add(startPair);
            endCoordsOfGivenAxis.add(endPair);
        }
        Collections.sort(startCoordsOfGivenAxis, new RectangleEntryDoublePairComparator());
        Collections.sort(endCoordsOfGivenAxis, new RectangleEntryDoublePairComparator());
        // now determine all distributions. how?
        // for each sort, M - 2m + 2 distributions of the M+1 entries into two groups are determined
        // for the k-th distribution, where k is in [1, M-2m+2], the first group contains
        // the first m-1+k entries, the second group the remaining ones
        // for these two groups, goodness values are determined
        // now, in the current axis
        for (int j = 0; j < M - 2 * m + 2; j++) {
            int k = m - 1 + j; // taken straight from the R*-tree paper
            List<RectangleEntryDoublePair> groupOneStartPairs = startCoordsOfGivenAxis.subList(0, k);
            List<RectangleEntryDoublePair> groupTwoStartPairs = startCoordsOfGivenAxis.subList(k, startCoordsOfGivenAxis.size());

            List<RectangleEntryDoublePair> groupOneEndPairs = endCoordsOfGivenAxis.subList(0, k);
            List<RectangleEntryDoublePair> groupTwoEndPairs = endCoordsOfGivenAxis.subList(k, endCoordsOfGivenAxis.size());

            double goodnessGroupOneStartPairs = determineGoodnessValueOverlapWise(groupOneStartPairs);
            double goodnessGroupTwoStartPairs = determineGoodnessValueOverlapWise(groupTwoStartPairs);
            double goodnessGroupOneEndPairs = determineGoodnessValueOverlapWise(groupOneEndPairs);
            double goodnessGroupTwoEndPairs = determineGoodnessValueOverlapWise(groupTwoEndPairs);
            double sum = goodnessGroupOneStartPairs + goodnessGroupTwoStartPairs +
                    goodnessGroupOneEndPairs + goodnessGroupTwoEndPairs;
            if (min == -1d || min > sum) {
                min = sum;
                selectedIndex = j;
            } else if (min == sum) { // would this be a tie? How are ties defined?
                goodnessGroupOneStartPairs = determineGoodnessValueAreaWise(groupOneStartPairs);
                goodnessGroupTwoStartPairs = determineGoodnessValueAreaWise(groupTwoStartPairs);
                goodnessGroupOneEndPairs = determineGoodnessValueAreaWise(groupOneEndPairs);
                goodnessGroupTwoEndPairs = determineGoodnessValueAreaWise(groupTwoEndPairs);
                sum = goodnessGroupOneStartPairs + goodnessGroupTwoStartPairs +
                        goodnessGroupOneEndPairs + goodnessGroupTwoEndPairs;
                min = sum;
                selectedIndex = j;
            }
        }
        return selectedIndex;
    }

    // we will go with the margin value
    private static double determineGoodnessValueMarginWise(List<RectangleEntryDoublePair> entries) {
        double sum = 0;
        for (RectangleEntryDoublePair entry : entries) {
            Rectangle r = entry.getRectangleEntry().getRectangle();
            double margin = r.getMargin();
//            double area = r.getArea();
            sum += margin;
        }
        return sum;
    }

    private static double determineGoodnessValueOverlapWise(List<RectangleEntryDoublePair> entries) {
        double sum = 0;
        for (int i = 0; i < entries.size(); i++) {
            RectangleEntryDoublePair entry = entries.get(i);
            for (int j = i + 1; j < entries.size(); j++) {
                Rectangle r = entry.getRectangleEntry().getRectangle();
                RectangleEntryDoublePair otherEntry = entries.get(j);
                Rectangle otherRectangle = otherEntry.getRectangleEntry().getRectangle();
                double overlap = 0;
                if (r.overlaps(otherRectangle)) {
                    overlap = r.getOverlap(otherRectangle);
                }
//            double margin = r.getMargin();
//                double area = r.getArea();
                sum += overlap;
            }
        }
        return sum;
    }

    private static double determineGoodnessValueAreaWise(List<RectangleEntryDoublePair> entries) {
        double sum = 0;
        for (RectangleEntryDoublePair entry : entries) {
            Rectangle r = entry.getRectangleEntry().getRectangle();
//            double margin = r.getMargin();
            double area = r.getArea();
            sum += area;
        }
        return sum;
    }
}

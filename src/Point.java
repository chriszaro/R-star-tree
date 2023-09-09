import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * This class represents the Point, not necessarily a Point that has been in the tree.
 */
public class Point implements java.io.Serializable {
    private String name;
    private long id;

    private ArrayList<Double> coords;

    public ArrayList<Integer> getCoordsIntegers() {
        return coordsIntegers;
    }

    public ArrayList<String> getCoordsStrings() {
        return coordsStrings;
    }

    private ArrayList<Integer> coordsIntegers;
    private ArrayList<String> coordsStrings;

    public Point(double... A) {
        this.coordsStrings = new ArrayList<>();
        this.coordsIntegers = new ArrayList<>();
        this.coords = new ArrayList<>();
        for (double v : A) {
            this.coords.add(v);
            this.coordsIntegers.add((int) v);
            this.coordsStrings.add(Double.toString(v));
        }
    }

    public Point(ArrayList<Double> coords) {
        this.coordsStrings = new ArrayList<>();
        this.coordsIntegers = new ArrayList<>();
        this.coords = new ArrayList<>();
        for (Double v : coords) {
            this.coords.add(v);
            this.coordsIntegers.add((int) ((double) v));
            this.coordsStrings.add(Double.toString(v));
        }
    }

    public Point(long id, String name, ArrayList<String> coords) {
        this.coordsStrings = new ArrayList<>();
        this.coordsIntegers = new ArrayList<>();
        this.coords = new ArrayList<>();

        this.coordsStrings.addAll(coords);
        for (String coord : coordsStrings) {
            this.coords.add(Double.parseDouble(coord));
            this.coordsIntegers.add(Integer.parseInt(coord.replace(".", "")));
        }
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (String coordsString : this.coordsStrings) {
            sb.append(coordsString);
            sb.append('_');
        }
        int pos = sb.lastIndexOf("_");
        String temp = sb.substring(0, pos);
        sb = new StringBuilder();
        sb.append(temp);
        sb.append(')');
//        sb.append(' ');
//        sb.append(name);
//        sb.append(' ');
//        sb.append(id);
        return sb.toString();
    }

    public String toFile() {
        String splitter = ":";
        StringBuilder sb = new StringBuilder();
        for (String coordsString : this.coordsStrings) {
            sb.append(coordsString);
            sb.append(splitter);
        }
        int pos = sb.lastIndexOf(splitter);
        String temp = sb.substring(0, pos);
        sb = new StringBuilder();
        sb.append(temp);
        sb.append(splitter);
        sb.append(id);
        sb.append(splitter);
        sb.append(name);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point other = (Point) o;
        boolean res = true;
        for (int i = 0; i < this.coords.size(); i++) {
            // this.coords.size() is EQUAL TO other.getCoords().size()
            res &= Double.compare(this.coords.get(i), other.getCoords().get(i)) == 0;
        }
        return res;
//        return Double.compare(this.getX(), other.getX()) == 0 && Double.compare(this.getY(), other.getY()) == 0;
    }

    public double distance(PointEntry other) {
        return Math.sqrt(pseudoDistance(other.getPoint()));
    }

    public double distance(Point other) {
        return Math.sqrt(pseudoDistance(other));
    }

    public double pseudoDistance(Point other) {
        double sum = 0;
        ArrayList<Double> doubles = this.coords;
        for (int i = 0; i < doubles.size(); i++) {
            Double coord1 = doubles.get(i);
            Double coord2 = other.getCoords().get(i);
            sum += Math.pow(coord1 - coord2, 2);
            // to get the actual distance, you must find the sqrt of this number
        }
        // But, it's faster not to calculate the sqrt, so if it's not needed, it shall not be calculated
        return sum;
        /*
        float d1 = (float) (Math.pow(this.getX() - other.getX(), 2));
        float d2 = (float) (Math.pow(this.getY() - other.getY(), 2));
        return d1 + d2; // to get the actual distance, you must find the sqrt of this number
        // But, it's faster not to calculate the sqrt, so if it's not needed, it shall not be calculated
        */
    }

    /**
     * Calculate the distance of a point from a given rectangle
     *
     * @param rect The rectangle from which the distance is needed
     * @return The distance between this point and the given rectangle
     */
    public double distance(RectangleEntry rect) {
        return this.distance(rect.getRectangle());
    }

    /**
     * Calculate the distance of a point from a given rectangle
     *
     * @param rect The rectangle from which the distance is needed
     * @return The distance between this point and the given rectangle
     */
    public double distance(Rectangle rect) {
        // first determine if this point is inside this rectangle
        // if point is inside rect => return 0
        // if not
        // find the minimum distance between two possible distances
        // one is perpendicular to the closest edge of the rectangle
        // the other is perpendicular to the rectangle's nearest right-angle
        // return the min_dist

        int n = this.getCoords().size(); // n is the number of dimensions we're working with
//        ArrayList<Double> linesOfBoundingBoxInEachAxis = rect.getLinesOfRectangle();

        if (rect.contains(this))
            return 0;

        ArrayList<Double> closest = new ArrayList<>(n);
        for (int i = 0; i < n; i++) { // for each side
            double k = this.getCoords().get(i);
            double k2 = rect.getStartPoint().getCoords().get(i);
            double k1 = rect.getEndPoint().getCoords().get(i);
            // three possibilites
            if (k < k1)
                closest.add(k1);
            else if (k >= k1 && k < k2)
                closest.add(k);
            else // if k >= k2
                closest.add(k2);
        }

        Point other = new Point(closest);
        return this.distance(other);
    }

    public ArrayList<Double> getCoords() {
        return coords;
    }
}

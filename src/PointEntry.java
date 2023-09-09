import java.util.Objects;

/**
 * This class represents a Point that has been inserted in a LeafNode of the tree.
 */
public class PointEntry extends Entry {
    private Point point;
    private String record_ID;


    public PointEntry(Point point, String record_ID) {
        this.point = point;
        this.record_ID = record_ID;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointEntry that = (PointEntry) o;
        return Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point);
    }

    @Override
    public String toString() {
        return point.toString();
    }
}

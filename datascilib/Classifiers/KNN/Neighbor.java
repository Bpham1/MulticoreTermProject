package datascilib.Classifiers.KNN;

public class Neighbor implements Comparable<Neighbor> {
    public double distance;
    public Integer label;

    public Neighbor(double distance, Integer label) {
        this.distance = distance;
        this.label = label;
    }

    public String toString() {
        String out = "KNN.Distance: " + distance + ", label: " + label;
        return out;
    }

    @Override
    public int compareTo(Neighbor o) {
        return Double.compare(this.distance, o.distance);
    }
}
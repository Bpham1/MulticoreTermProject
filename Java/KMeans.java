import java.util.ArrayList;
import java.util.List;

public class KMeans {
    List<Integer> points;
    List<Integer> centers;

    public KMeans(List<Integer> points, int n_cluster){
        this.points = points;
    }

    public KMeans(List<Integer> points, int n_cluster, int max_iters){
        this.points = points;
    }

    public List<Integer> getCenters(){
        //TODO: Replace null with K-means centers
        return null;
    }
}

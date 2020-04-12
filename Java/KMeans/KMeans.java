import java.util.ArrayList;
import java.util.List;

public class KMeans {
    private KMeansMultiD kmd;
    private KMeans1D km;
    private String mode;

    public KMeans(int n_cluster, int n_jobs){
        this.kmd = new KMeansMultiD(n_cluster, n_jobs);
        this.km = new KMeans1D(n_cluster, n_jobs);
        this.mode = "None";
    }

    public KMeans(int n_cluster, int max_iter, int n_jobs){
        this.kmd = new KMeansMultiD(n_cluster, max_iter, n_jobs);
        this.km = new KMeans1D(n_cluster, max_iter, n_jobs);
        this.mode = "None";
    }

    public void fit(List points) throws InterruptedException {
        if(points.size() == 0){
            throw new IllegalStateException("Cannot fit an empty list");
        } else if (!(points.get(0) instanceof List) && !(points.get(0) instanceof Integer) ){
            throw new IllegalStateException("points must be in the format List<Integer> or List<List<Integer>>");
        } else if (points.get(0) instanceof List) {
            kmd.fit(points);
            mode = "kmd";
        } else if (points.get(0) instanceof Integer){
            km.fit(points);
            mode = "km";
        }
    }

    public List<Integer> predict(List points) throws InterruptedException {
        if(points.size() == 0){
            return new ArrayList<Integer>();
        } else if (!(points.get(0) instanceof List) && !(points.get(0) instanceof Integer) ){
            throw new IllegalStateException("points must be in the format List<Integer> or List<List<Integer>>");
        } else if (points.get(0) instanceof List && mode.equals("kmd")) {
            return kmd.predict(points);
        } else if (points.get(0) instanceof Integer && mode.equals("km")){
            return km.predict(points);
        } else {
            throw new IllegalStateException("You must fit the model with the proper model first.");
        }
    }

    public List<?> getCenters(){
        if(mode.equals("kmd")){
            return kmd.getCenters();
        } else if(mode.equals("km")){
            return km.getCenters();
        } else {
            return null;
        }
    }
}

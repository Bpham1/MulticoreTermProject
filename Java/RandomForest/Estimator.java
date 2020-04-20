import java.util.List;

public interface Estimator {
    public void fit(List X, List Y);
    public List<Integer> predict(List X);
    public Estimator copy();
}

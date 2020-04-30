package datascilib.Classifiers.Intefaces;

import java.util.List;

public interface NewEstimator {
    public void fit(List<List<Double>> X, List<Integer> Y);
    public List<Integer> predict(List<List<Double>> X);
    public NewEstimator copy();
}

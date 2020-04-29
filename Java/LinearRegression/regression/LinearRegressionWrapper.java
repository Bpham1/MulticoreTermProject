import java.util.ArrayList;
import java.util.List;

public class LinearRegressionWrapper {
    private List<Double> X;
    private List<Double> Y;
    private LinearRegression lr;
    private double slope;
    private double intercept;

    public LinearRegressionWrapper(){
        this.X = new ArrayList<Double>();
        this.Y = new ArrayList<Double>();
        this.lr = null;
        this.slope = 0;
        this.intercept = 0;
    }

    public void fit(List<Double> X, List<Double> Y){
        Double[] Xarray = new Double[X.size()];
        Double[] Yarray = new Double[Y.size()];
        lr = new LinearRegression(X.toArray(Xarray), Y.toArray(Yarray));
        try {
            lr.fit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Double> predict(List<Double> X){
        if(lr == null){
            return null;
        }
        List<Double> pred = new ArrayList<Double>();
        for(Double x: X){
            pred.add(lr.getEstimatedValue(x));
        }
        return pred;
    }
}

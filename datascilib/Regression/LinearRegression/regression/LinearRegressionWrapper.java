package datascilib.Regression.LinearRegression.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LinearRegressionWrapper {
    private LinearRegression lr;

    public LinearRegressionWrapper(){
        this.lr = null;
    }

    public void fit(List<Double> X, List<Double> Y){
        if(X == null || Y == null || X.size() == 0 || Y.size() == 0){
            throw new IllegalArgumentException("X and Y cannot be null or empty");
        }

        Double[] Xarray = new Double[X.size()];
        Double[] Yarray = new Double[Y.size()];
        lr = new LinearRegression(X.toArray(Xarray), Y.toArray(Yarray));
        try {
            lr.simpleFit(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void fit(List<Double> X, List<Double> Y, boolean getGradient){
        if(X == null || Y == null || X.size() == 0 || Y.size() == 0){
            throw new IllegalArgumentException("X and Y cannot be null or empty");
        }

        Double[] Xarray = new Double[X.size()];
        Double[] Yarray = new Double[Y.size()];
        lr = new LinearRegression(X.toArray(Xarray), Y.toArray(Yarray));
        try {
            if(getGradient){
                lr.gradientFit(true);
            } else {
                lr.simpleFit();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<Double> predict(List<Double> X){
        if(X == null){
            return null;
        }

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

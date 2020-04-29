package datascilib.Regression.LinearRegression.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MultiLinearRegressionWrapper {
    private MultiLinearRegression lr;

    public MultiLinearRegressionWrapper(){
        this.lr = null;
    }

    public void fit(List<List<Double>> X, List<Double> Y){

        Double[][] Xarray = new Double[X.size()][];
        Double[] Yarray = new Double[Y.size()];
        for(int i = 0; i < X.size(); i++){
            List<Double> row = X.get(i);
            Xarray[i] = row.toArray(new Double[0]);
        }
        lr = new MultiLinearRegression(Xarray, Y.toArray(Yarray));
        try {
            lr.simpleFit();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<Double> predict(List<List<Double>> X){
        if(lr == null){
            return null;
        }
        List<Double> pred = new ArrayList<Double>();
        for(List<Double> x: X){
            Double[] Xarray = new Double[x.size()];
            pred.add(lr.getEstimatedValue(x.toArray(Xarray)));
        }
        return pred;
    }
}

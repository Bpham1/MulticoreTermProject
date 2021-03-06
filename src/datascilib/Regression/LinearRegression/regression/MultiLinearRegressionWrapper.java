package datascilib.Regression.LinearRegression.regression;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <h1>Multi-Linear Regression Wrapper</h1>
 * A wrapper for {@link MultiLinearRegression} that is more inline with the rest of the library with a single fit
 * and predict method.
 * </br></br>
 * @author Brandon Pham
 * @version 0.1
 * @since 2020-04-29
 */
public class MultiLinearRegressionWrapper {
    /**
     * A {@link MultiLinearRegression} object that is wrapped.
     */
    private MultiLinearRegression lr;

    /**
     * The main constructor.
     */
    public MultiLinearRegressionWrapper(){
        this.lr = null;
    }

    /**
     * Fits 2-D {@link List} of {@link Double} points X and a 1-D {@link List} of corresponding
     * {@link Double} values Y.
     * @param X is a 2-D {@link List} of {@link Double} points
     * @param Y is a 1-D {@link List} of corresponding {@link Double} values
     */
    public void fit(List<List<Double>> X, List<Double> Y){
        if(X == null || Y == null || X.size() == 0 || Y.size() == 0){
            throw new IllegalArgumentException("X and Y cannot be null or empty");
        }

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

    /**
     * Predicts 1-D {@link List} of {@link Double} values corresponding to passed 2-D {@link List} of
     * {@link Double} points X
     * @param X is a 2-D {@link List} of {@link Double} points
     * @return a 1-D {@link List} of {@link Double} values corresponding to X
     */
    public List<Double> predict(List<List<Double>> X){
        if(X == null){
            return null;
        } else if (X.size() == 0) {
            return new ArrayList<Double>();
        } else {
            for(List<Double> x: X){
                if(x == null || x.size() == 0){
                    throw new IllegalArgumentException("X contains Empty or null points");
                }
            }
        }

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

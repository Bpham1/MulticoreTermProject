package datascilib.Regression.LinearRegression.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <h1>Linear Regression Wrapper</h1>
 * A wrapper for {@link LinearRegression} that is more inline with the rest of the library with a single fit
 * and predict method.
 * </br></br>
 * @author Brandon Pham
 * @version 0.1
 * @since 2020-04-29
 */
public class LinearRegressionWrapperSeq {
    /**
     * A {@link MultiLinearRegression} object that is wrapped.
     */
    private LinearRegression lr;

    /**
     * The main constructor.
     */
    public LinearRegressionWrapperSeq(){
        this.lr = null;
    }

    /**
     * Fits 1-D {@link List} of {@link Double} X coordinates and a 1-D {@link List} of corresponding
     * {@link Double} Y coordinates.
     * @param X is a 1-D {@link List} of {@link Double} X coordinates
     * @param Y is a 1-D {@link List} of corresponding {@link Double} Y coordinates
     */
    public void fit(List<Double> X, List<Double> Y){
        if(X == null || Y == null || X.size() == 0 || Y.size() == 0){
            throw new IllegalArgumentException("X and Y cannot be null or empty");
        }

        Double[] Xarray = new Double[X.size()];
        Double[] Yarray = new Double[Y.size()];
        lr = new LinearRegression(X.toArray(Xarray), Y.toArray(Yarray));
        try {
            lr.simpleFit(false);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    /**
     * Fits 1-D {@link List} of {@link Double} X coordinates and a 1-D {@link List} of corresponding
     * {@link Double} Y coordinates. Additionally, takes a boolean to specify whether to enable gradient
     * descent or not.
     * @param X is a 1-D {@link List} of {@link Double} X coordinates
     * @param Y is a 1-D {@link List} of corresponding {@link Double} Y coordinates
     * @param getGradient is a {@link Boolean} that enables gradient descent if true; does not otherwise
     */
    public void fit(List<Double> X, List<Double> Y, boolean getGradient){
        if(X == null || Y == null || X.size() == 0 || Y.size() == 0){
            throw new IllegalArgumentException("X and Y cannot be null or empty");
        }

        Double[] Xarray = new Double[X.size()];
        Double[] Yarray = new Double[Y.size()];
        lr = new LinearRegression(X.toArray(Xarray), Y.toArray(Yarray));
        try {
            if(getGradient){
                lr.gradientFit(false);
            } else {
                lr.simpleFit();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Predicts 1-D {@link List} of {@link Double} values corresponding to passed 1-D {@link List} of
     * {@link Double} X coordinates.
     * @param X is a 1-D {@link List} of {@link Double} X coordinates.
     * @return a 1-D {@link List} of {@link Double} values corresponding to X
     */
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

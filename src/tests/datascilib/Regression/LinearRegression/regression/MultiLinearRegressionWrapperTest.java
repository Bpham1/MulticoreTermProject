package tests.datascilib.Regression.LinearRegression.regression;

import datascilib.Regression.LinearRegression.regression.MultiLinearRegressionWrapper;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MultiLinearRegressionWrapperTest {

    List<List<Double>> trainX;
    List<Double> trainY;
    List<List<Double>> testX;
    DecimalFormat dcf = new DecimalFormat("00.00");

    @Test
    void fit() {
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 9; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (3*i)), (double) (2 + (3*i)), (double) (3*(i+1)))));
        }
        trainY = new ArrayList<Double>(Arrays.asList(1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0));
        MultiLinearRegressionWrapper mlr = new MultiLinearRegressionWrapper();
        mlr.fit(trainX, trainY);
    }

    @Test
    void predict() {
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 9; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (3*i)), (double) (2 + (3*i)), (double) (3*(i+1)))));
        }
        testX = new ArrayList<List<Double>>();
        for(int i = 10; i < 19; i++){
            testX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (3*i)), (double) (2 + (3*i)), (double) (3*(i+1)))));
        }
        trainY = new ArrayList<Double>(Arrays.asList(1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0));
        MultiLinearRegressionWrapper mlr = new MultiLinearRegressionWrapper();
        mlr.fit(trainX, trainY);

        List<Double> pred = mlr.predict(trainX);
        System.out.print("Train X:    ");
        for(List<Double> label: trainX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf.format(label.get(i)) + ", ");
                } else {
                    System.out.print(dcf.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();
        System.out.print("Prediction: ");
        for(Double label: pred){
            System.out.print(dcf.format(label) + "                 ");
        }
        System.out.println();
        pred = mlr.predict(testX);
        System.out.print("Test X:     ");
        for(List<Double> label: testX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf.format(label.get(i))  + ", ");
                } else {
                    System.out.print(dcf.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();
        System.out.print("Prediction: ");
        for(Double label: pred){
            System.out.print(dcf.format(label) + "                 ");
        }
        System.out.println();
    }
}
package tests.datascilib.Regression.LinearRegression.regression;

import datascilib.Regression.LinearRegression.regression.LinearRegressionWrapper;
import org.junit.jupiter.api.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LinearRegressionWrapperTest {

    List<Double> trainX;
    List<Double> trainY;
    List<Double> testX;
    List<Double> testY;
    DecimalFormat dcf = new DecimalFormat("00.00");

    @Test
    void testSimpleFit() {
        trainX = new ArrayList<Double>(Arrays.asList(1.0, 4.0, 7.0, 10.0, 13.0, 16.0, 19.0, 22.0, 25.0));
        trainY = new ArrayList<Double>(Arrays.asList(1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0));
        LinearRegressionWrapper lr = new LinearRegressionWrapper();
        lr.fit(trainX, trainY);
    }

    @Test
    void testGradientFit() {
        trainX = new ArrayList<Double>(Arrays.asList(1.0, 4.0, 7.0, 10.0, 13.0, 16.0, 19.0, 22.0, 25.0));
        trainY = new ArrayList<Double>(Arrays.asList(1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0));
        LinearRegressionWrapper lr = new LinearRegressionWrapper();
        lr.fit(trainX, trainY, true);
    }

    @Test
    void testPredict() {
        trainX = new ArrayList<Double>(Arrays.asList(1.0, 4.0, 7.0, 10.0, 13.0, 16.0, 19.0, 22.0, 25.0));
        trainY = new ArrayList<Double>(Arrays.asList(1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0, 3.0));
        testX = new ArrayList<Double>(Arrays.asList(28.0, 31.0, 34.0, 37.0, 40.0, 43.0, 46.0, 49.0, 52.0));
        LinearRegressionWrapper lr = new LinearRegressionWrapper();
        lr.fit(trainX, trainY);
        List<Double> pred = lr.predict(trainX);
        System.out.print("Train X:    ");
        for(Double label: trainX){
            System.out.print(dcf.format(label) + " ");
        }
        System.out.println();
        System.out.print("Prediction: ");
        for(Double label: pred){
            System.out.print(dcf.format(label) + " ");
        }
        System.out.println();
        pred = lr.predict(testX);
        System.out.print("Test X:     ");
        for(Double label: testX){
            System.out.print(dcf.format(label) + " ");
        }
        System.out.println();
        System.out.print("Prediction: ");
        for(Double label: pred){
            System.out.print(dcf.format(label) + " ");
        }
        System.out.println();
    }

    @Test
    void emptyInput(){
        LinearRegressionWrapper lr = new LinearRegressionWrapper();

        trainX = new ArrayList<Double>();
        trainY = new ArrayList<Double>();
        try{
            lr.fit(trainX, trainY);
            System.out.println("Empty input accepted");
        } catch (Exception e){
            System.out.println("Empty input causes exception");
        }
    }

    @Test
    void nullInput(){
        LinearRegressionWrapper lr = new LinearRegressionWrapper();

        try{
            lr.fit(null, null);
            System.out.println("Null input accepted");
        } catch (Exception e){
            System.out.println("Null input causes exception");
        }
        assertNull(lr.predict(null));
    }

    @Test
    void smallInput(){
        LinearRegressionWrapper lr = new LinearRegressionWrapper();

        trainX = new ArrayList<Double>();
        trainX.add(1.0);

        testX = new ArrayList<Double>();
        testX.add(4.0);

        trainY = new ArrayList<Double>();
        trainY.add(1.0);

        lr.fit(trainX, trainY);
        lr.predict(testX);
    }
}
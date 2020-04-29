package datascilib.Clustering.KMeans;

import datascilib.Classifiers.DecisionTree.DescisionTreeEstimator;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KMeansTest {

    List<List<Double>> trainX;
    List<List<Double>> testX;
    DecimalFormat dcf1 = new DecimalFormat("00.00");

    @Test
    void fit() {
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (9*i)), (double) (2 + (9*i)), (double) (3 + (9*i)))));
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (7 + (9*i)), (double) (8 + (9*i)), (double) (9*(i+1)))));
        }
        KMeans km = new KMeans(3);
        km.fit(trainX);
    }

    @Test
    void predict() {
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (9*i)), (double) (2 + (9*i)), (double) (3 + (9*i)))));
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (7 + (9*i)), (double) (8 + (9*i)), (double) (9*(i+1)))));
        }

        testX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            testX.add(new ArrayList<Double>(Arrays.asList((double) (4 + (9*i)), (double) (5 + (9*i)), (double) (6 + (9*i)))));
        }

        KMeans km = new KMeans(3);
        km.fit(trainX);

        List<Integer> pred = km.predict(trainX);
        assertEquals(pred.size(), trainX.size());

        System.out.print("Train X:    ");
        for(List<Double> label: trainX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf1.format(label.get(i))  + ", ");
                } else {
                    System.out.print(dcf1.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();

        System.out.print("Prediction: ");
        for(Integer label: pred){
            assertTrue(label == 0 || label == 1 || label == 2);
            System.out.print(label + "                     ");
        }
        System.out.println();

        pred = km.predict(testX);
        assertEquals(pred.size(), testX.size());

        System.out.print("Test X:     ");
        for(List<Double> label: testX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf1.format(label.get(i))  + ", ");
                } else {
                    System.out.print(dcf1.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();
        System.out.print("Prediction: ");
        for(Integer label: pred){
            assertTrue(label == 0 || label == 1 || label == 2);
            System.out.print(label + "                     ");
        }
        System.out.println();
    }

    @Test
    void startEdgePredict(){
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (4 + (9*i)), (double) (5 + (9*i)), (double) (6 + (9*i)))));
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (7 + (9*i)), (double) (8 + (9*i)), (double) (9 + (9*i)))));
        }

        testX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            testX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (9*i)), (double) (2 + (9*i)), (double) (3 + (9*i)))));
        }

        KMeans km = new KMeans(3);
        km.fit(trainX);

        List<Integer> pred = km.predict(trainX);
        assertEquals(pred.size(), trainX.size());

        System.out.print("Train X:    ");
        for(List<Double> label: trainX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf1.format(label.get(i))  + ", ");
                } else {
                    System.out.print(dcf1.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();

        System.out.print("Prediction: ");
        for(Integer label: pred){
            System.out.print(label + "                     ");
            assertTrue(label == 0 || label == 1 || label == 2);
        }
        System.out.println();

        pred = km.predict(testX);
        assertEquals(pred.size(), testX.size());

        System.out.print("Test X:     ");
        for(List<Double> label: testX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf1.format(label.get(i))  + ", ");
                } else {
                    System.out.print(dcf1.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();
        System.out.print("Prediction: ");
        for(Integer label: pred){
            assertTrue(label == 0 || label == 1 || label == 2);
            System.out.print(label + "                     ");
        }
        System.out.println();
    }

    @Test
    void endEdgePredict(){
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (9*i)), (double) (2 + (9*i)), (double) (3 + (9*i)))));
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (4 + (9*i)), (double) (5 + (9*i)), (double) (6 + (9*i)))));
        }

        testX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            testX.add(new ArrayList<Double>(Arrays.asList((double) (7 + (9*i)), (double) (8 + (9*i)), (double) (9 + (9*i)))));
        }

        KMeans km = new KMeans(3);
        km.fit(trainX);

        List<Integer> pred = km.predict(trainX);
        assertEquals(pred.size(), trainX.size());

        System.out.print("Train X:    ");
        for(List<Double> label: trainX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf1.format(label.get(i))  + ", ");
                } else {
                    System.out.print(dcf1.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();

        System.out.print("Prediction: ");
        for(Integer label: pred){
            assertTrue(label == 0 || label == 1 || label == 2);
            System.out.print(label + "                     ");
        }
        System.out.println();

        pred = km.predict(testX);
        assertEquals(pred.size(), testX.size());

        System.out.print("Test X:     ");
        for(List<Double> label: testX){
            System.out.print("(");
            for(int i = 0; i < label.size(); i++){
                if(i != label.size() - 1){
                    System.out.print(dcf1.format(label.get(i))  + ", ");
                } else {
                    System.out.print(dcf1.format(label.get(i)));
                }
            }
            System.out.print(") ");
        }
        System.out.println();
        System.out.print("Prediction: ");
        for(Integer label: pred){
            assertTrue(label == 0 || label == 1 || label == 2);
            System.out.print(label + "                     ");
        }
        System.out.println();
    }

    @Test
    void emptyInput(){
        KMeans km = new KMeans(3);

        trainX = new ArrayList<List<Double>>();
        try{
            km.fit(trainX);
            System.out.println("Empty input accepted");
        } catch (Exception e){
            System.out.println("Empty input causes exception");
        }

        assertEquals(km.predict(trainX).size(), trainX.size());

        for(int i = 0; i < 9; i++){
            trainX.add(new ArrayList<Double>());
        }

        try{
            km.fit(trainX);
            km.predict(trainX);
            System.out.println("Empty points accepted");
        } catch (Exception e){
            System.out.println("Empty points causes exception");
        }
    }

    @Test
    void nullInput(){
        KMeans km = new KMeans(3);
        try{
            km.fit(null);
            System.out.println("Null input accepted");
        } catch (Exception e){
            System.out.println("Null input causes exception");
        }
        assertNull(km.predict(null));
    }

    @Test
    void smallInput(){
        KMeans km = new KMeans(3);

        trainX = new ArrayList<List<Double>>();
        trainX.add(new ArrayList<Double>());
        trainX.get(0).add(1.0);

        testX = new ArrayList<List<Double>>();
        testX.add(new ArrayList<Double>());
        testX.get(0).add(4.0);

        km.fit(trainX);
        km.predict(testX);
    }
}
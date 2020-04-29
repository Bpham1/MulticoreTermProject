package datascilib.Classifiers.KNN;

import datascilib.Classifiers.RandomForest.RandomForest;
import datascilib.Utils.CSVReaderWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class KNearestNeightborsTest {

    List<List<Double>> trainX;
    List<Integer> trainY;
    List<List<Double>> testX;
    DecimalFormat dcf1 = new DecimalFormat("00.00");

    @Test
    void fit() {
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (9*i)), (double) (2 + (9*i)), (double) (3 + (9*i)))));
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (7 + (9*i)), (double) (8 + (9*i)), (double) (9*(i+1)))));
        }
        trainY = new ArrayList<Integer>(Arrays.asList(1, 1, 2, 2, 3, 3));
        KNearestNeightbors knn = new KNearestNeightbors(3);
        knn.fit(trainX, trainY);
    }

    @Test
    void predict() {
        trainX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (9*i)), (double) (2 + (9*i)), (double) (3 + (9*i)))));
            trainX.add(new ArrayList<Double>(Arrays.asList((double) (7 + (9*i)), (double) (8 + (9*i)), (double) (9*(i+1)))));
        }
        trainY = new ArrayList<Integer>(Arrays.asList(1, 1, 2, 2, 3, 3));
        KNearestNeightbors knn = new KNearestNeightbors(3);
        knn.fit(trainX, trainY);

        testX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            testX.add(new ArrayList<Double>(Arrays.asList((double) (4 + (9*i)), (double) (5 + (9*i)), (double) (6 + (9*i)))));
        }
        List<Integer> pred = knn.predict(trainX);
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
            assertTrue(label == 1 || label == 2 || label == 3);
            System.out.print(label + "                     ");
        }
        System.out.println();

        pred = knn.predict(testX);
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
            assertTrue(label == 1 || label == 2 || label == 3);
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
        trainY = new ArrayList<Integer>(Arrays.asList(1, 1, 2, 2, 3, 3));

        testX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            testX.add(new ArrayList<Double>(Arrays.asList((double) (1 + (9*i)), (double) (2 + (9*i)), (double) (3 + (9*i)))));
        }

        KNearestNeightbors knn = new KNearestNeightbors(3);
        knn.fit(trainX, trainY);

        List<Integer> pred = knn.predict(trainX);
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
            assertTrue(label == 1 || label == 2 || label == 3);
            System.out.print(label + "                     ");
        }
        System.out.println();

        pred = knn.predict(testX);
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
            assertTrue(label == 1 || label == 2 || label == 3);
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
        trainY = new ArrayList<Integer>(Arrays.asList(1, 1, 2, 2, 3, 3));

        testX = new ArrayList<List<Double>>();
        for(int i = 0; i < 3; i++){
            testX.add(new ArrayList<Double>(Arrays.asList((double) (7 + (9*i)), (double) (8 + (9*i)), (double) (9 + (9*i)))));
        }

        KNearestNeightbors knn = new KNearestNeightbors(3);
        knn.fit(trainX, trainY);

        List<Integer> pred = knn.predict(trainX);
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
            assertTrue(label == 1 || label == 2 || label == 3);
            System.out.print(label + "                     ");
        }
        System.out.println();

        pred = knn.predict(testX);
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
            assertTrue(label == 1 || label == 2 || label == 3);
            System.out.print(label + "                     ");
        }
        System.out.println();
    }

    @Test
    void emptyInput(){
        KNearestNeightbors knn = new KNearestNeightbors(3);

        trainX = new ArrayList<List<Double>>();
        trainY = new ArrayList<Integer>();
        try{
            knn.fit(trainX, trainY);
            System.out.println("Empty input accepted");
        } catch (Exception e){
            System.out.println("Empty input causes exception");
        }

        assertEquals(knn.predict(trainX).size(), trainX.size());

        for(int i = 0; i < 9; i++){
            trainX.add(new ArrayList<Double>());
        }

        try{
            knn.fit(trainX, trainY);
            knn.predict(trainX);
            System.out.println("Empty points accepted");
        } catch (Exception e){
            System.out.println("Empty points causes exception");
        }
    }

    @Test
    void nullInput(){
        KNearestNeightbors knn = new KNearestNeightbors(3);
        try{
            knn.fit(null, null);
            System.out.println("Null input accepted");
        } catch (Exception e){
            System.out.println("Null input causes exception");
        }
        assertNull(knn.predict(null));
    }

    @Test
    void smallInput(){
        KNearestNeightbors knn = new KNearestNeightbors(3);

        trainX = new ArrayList<List<Double>>();
        trainX.add(new ArrayList<Double>());
        trainX.get(0).add(1.0);

        testX = new ArrayList<List<Double>>();
        testX.add(new ArrayList<Double>());
        testX.get(0).add(4.0);

        trainY = new ArrayList<Integer>();
        trainY.add(1);

        knn.fit(trainX, trainY);
        knn.predict(testX);
    }
}
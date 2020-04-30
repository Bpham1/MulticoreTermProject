package examples;

import datascilib.Classifiers.DecisionTree.DescisionTreeEstimator;
import datascilib.Classifiers.KNN.KNearestNeightbors;
import datascilib.Classifiers.NaiveBayes.ParallelGaussianBayesClassifier;
import datascilib.Classifiers.RandomForest.RandomForest;
import datascilib.Clustering.KMeans.KMeans;
import datascilib.Regression.LinearRegression.regression.LinearRegressionWrapper;
import datascilib.Regression.LinearRegression.regression.MultiLinearRegressionWrapper;
import datascilib.Utils.CSVReaderWriter;

import java.util.List;

public class SmallInputs {
    public static void main(String[] args){
        CSVReaderWriter csvWR = new CSVReaderWriter();
        boolean readSuccess = csvWR.read("./src/sampleinputs/SmallInput.csv");
        assert readSuccess;

        List<List<Double>> train_X = csvWR.getColumnRangeAsDouble("Feat 1", "Feat 1");
        List<Double> train_X_single = csvWR.getColumnAsDouble("Feat 1");
        List<Integer> train_Y = csvWR.getColumnAsInt("Y");
        List<Double> train_Y_double = csvWR.getColumnAsDouble("Y");

        System.out.println("train_X");
        for(List<Double> row: train_X){
            for(Double ele: row){
                System.out.print(ele + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("train_Y");
        for(Integer ele: train_Y){
            System.out.println(ele);
        }
        System.out.println();

        DescisionTreeEstimator dte = new DescisionTreeEstimator();
        KNearestNeightbors knn = new KNearestNeightbors(3);
        ParallelGaussianBayesClassifier gbc = new ParallelGaussianBayesClassifier();
        RandomForest rf = new RandomForest(300);
        KMeans km = new KMeans(20);
        LinearRegressionWrapper lr = new LinearRegressionWrapper();
        MultiLinearRegressionWrapper mlr = new MultiLinearRegressionWrapper();

        dte.fit(train_X, train_Y);
        knn.fit(train_X, train_Y);
        gbc.fit(train_X, train_Y);
        rf.fit(train_X, train_Y);
        km.fit(train_X);
        lr.fit(train_X_single, train_Y_double);
        mlr.fit(train_X, train_Y_double);

        System.out.println("Decision Tree Estimator");
        print1DIntList(dte.predict(train_X));
        System.out.println("K Nearest Neighbors");
        print1DIntList(knn.predict(train_X));
        System.out.println("Gaussian Bayes Classifier");
        print1DIntList(gbc.predict(train_X));
        System.out.println("Random Forest");
        print1DIntList(rf.predict(train_X));
        System.out.println("K Means");
        print1DIntList(km.predict(train_X));
        System.out.println("Linear Regression");
        print1DDoubleList(lr.predict(train_X_single));
        System.out.println("Multi-D Linear Regression");
        print1DDoubleList(mlr.predict(train_X));
    }

    public static void print2DList(List<List<Double>> list){
        for(List<Double> row: list){
            for(Double ele: row){
                System.out.print(ele + " ");
            }
            System.out.println();
        }
    }

    public static void print1DDoubleList(List<Double> list){
        for(Double ele: list){
            System.out.print(ele + " ");
        }
        System.out.println();
    }

    public static void print1DIntList(List<Integer> list){
        for(Integer ele: list){
            System.out.print(ele + " ");
        }
        System.out.println();
    }
}

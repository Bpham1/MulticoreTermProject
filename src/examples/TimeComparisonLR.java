package examples;

import datascilib.Classifiers.KNN.KNearestNeightbors;
import datascilib.Classifiers.KNN.KNearestNeightborsSeq;
import datascilib.Classifiers.NaiveBayes.ParallelGaussianBayesClassifier;
import datascilib.Regression.LinearRegression.regression.LinearRegressionWrapper;
import datascilib.Regression.LinearRegression.regression.LinearRegressionWrapperSeq;
import datascilib.Utils.CSVReaderWriter;

import java.util.*;

public class TimeComparisonLR {
    public static void main(String[] args){
        List<List<Double>> train_Xs = new ArrayList<>();
        List<List<Double>> train_Ys = new ArrayList<>();

        CSVReaderWriter csvWR = new CSVReaderWriter();

        boolean readSuccess = csvWR.read("./src/sampleinputs/SuperExtremeInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnAsDouble("Feat 1"));
        train_Ys.add(csvWR.getColumnAsDouble("Y"));

        readSuccess = csvWR.read("./src/sampleinputs/ExtremeInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnAsDouble("Feat 1"));
        train_Ys.add(csvWR.getColumnAsDouble("Y"));

        readSuccess = csvWR.read("./src/sampleinputs/LargeInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnAsDouble("Feat 1"));
        train_Ys.add(csvWR.getColumnAsDouble("Y"));

        readSuccess = csvWR.read("./src/sampleinputs/SampleInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnAsDouble("Feat 1"));
        train_Ys.add(csvWR.getColumnAsDouble("Y"));

        readSuccess = csvWR.read("./src/sampleinputs/SmallInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnAsDouble("Feat 1"));
        train_Ys.add(csvWR.getColumnAsDouble("Y"));

        LinearRegressionWrapperSeq dteSeq;
        LinearRegressionWrapper dtePar;

        List<Long> fitTimesSeq = new ArrayList<Long>();
        List<Long> fitTimesPar = new ArrayList<Long>();

        List<Long> predictTimesSeq = new ArrayList<Long>();
        List<Long> predictTimesPar = new ArrayList<Long>();

        List<Double> train_X;
        List<Double> train_Y;
        List<String> sizes = new ArrayList<>(Arrays.asList("Super Extreme Set (100000)", "Extreme Set (10000)", "Large Set (100)", "Medium Set (30)", "Small Set (1)"));

        for(int k = 0; k < train_Xs.size(); k++){
            dteSeq = new LinearRegressionWrapperSeq();
            dtePar = new LinearRegressionWrapper();

            System.out.println(sizes.get(k));

            train_X = train_Xs.get(k);
            train_Y = train_Ys.get(k);

            for(int i = 0; i < 20; i++){
                long startFitTime = System.nanoTime();
                dteSeq.fit(train_X, train_Y);
                long endFitTime = System.nanoTime();
                fitTimesSeq.add(endFitTime - startFitTime);

                startFitTime = System.nanoTime();
                dtePar.fit(train_X, train_Y);
                endFitTime = System.nanoTime();
                fitTimesPar.add(endFitTime - startFitTime);
            }

            Collections.sort(fitTimesSeq);
            Collections.sort(fitTimesPar);

            LongSummaryStatistics statsSeq = fitTimesSeq.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            LongSummaryStatistics statsPar2 = fitTimesPar.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();


            System.out.println("Sequential Fit Mean:           " + String.format("%d",(int) statsSeq.getAverage()));
            System.out.println("Sequential Fit Median:         " + String.format("%d",fitTimesSeq.get(9)));

            System.out.println("Parallel Fit Mean:   " + String.format("%d",(int) statsPar2.getAverage()));
            System.out.println("Parallel Fit Median: " + String.format("%d",fitTimesPar.get(9)));

            for(int i = 0; i < 20; i++){
                long startFitTime = System.nanoTime();
                dteSeq.predict(train_X);
                long endFitTime = System.nanoTime();
                predictTimesSeq.add(endFitTime - startFitTime);

                startFitTime = System.nanoTime();
                dtePar.predict(train_X);
                endFitTime = System.nanoTime();
                predictTimesPar.add(endFitTime - startFitTime);
            }

            Collections.sort(predictTimesSeq);
            Collections.sort(predictTimesPar);

            statsSeq = predictTimesSeq.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            statsPar2 = predictTimesPar.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            System.out.println("Sequential Predict Mean:           " + String.format("%d",(int) statsSeq.getAverage()));
            System.out.println("Sequential Predict Median:         " + String.format("%d",predictTimesSeq.get(9)));

            System.out.println("Parallel Predict Mean:   " + String.format("%d",(int) statsPar2.getAverage()));
            System.out.println("Parallel Predict Median: " + String.format("%d",predictTimesPar.get(9)));
            System.out.println();
        }
    }
}

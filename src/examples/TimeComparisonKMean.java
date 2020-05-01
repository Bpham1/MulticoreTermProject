package examples;

import datascilib.Classifiers.RandomForest.RandomForest;
import datascilib.Clustering.KMeans.KMeans;
import datascilib.Utils.CSVReaderWriter;

import java.util.*;

public class TimeComparisonKMean {
    public static void main(String[] args){
        List<List<List<Double>>> train_Xs = new ArrayList<>();
        List<List<Integer>> train_Ys = new ArrayList<>();

        CSVReaderWriter csvWR = new CSVReaderWriter();

        boolean readSuccess = csvWR.read("./src/sampleinputs/ExtremeInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnRangeAsDouble("Feat 1", "Feat 10"));
        train_Ys.add(csvWR.getColumnAsInt("Y"));

        readSuccess = csvWR.read("./src/sampleinputs/LargeInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnRangeAsDouble("Feat 1", "Feat 10"));
        train_Ys.add(csvWR.getColumnAsInt("Y"));

        readSuccess = csvWR.read("./src/sampleinputs/SampleInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnRangeAsDouble("Feat 1", "Feat 3"));
        train_Ys.add(csvWR.getColumnAsInt("Y"));

        readSuccess = csvWR.read("./src/sampleinputs/SmallInput.csv");
        assert readSuccess;
        train_Xs.add(csvWR.getColumnRangeAsDouble("Feat 1", "Feat 1"));
        train_Ys.add(csvWR.getColumnAsInt("Y"));

        KMeans dteSeq;
        KMeans dtePar2;
        KMeans dtePar4;

        List<Long> fitTimesSeq = new ArrayList<Long>();
        List<Long> fitTimesPar2 = new ArrayList<Long>();
        List<Long> fitTimesPar4 = new ArrayList<Long>();

        List<Long> predictTimesSeq = new ArrayList<Long>();
        List<Long> predictTimesPar2 = new ArrayList<Long>();
        List<Long> predictTimesPar4 = new ArrayList<Long>();

        List<List<Double>> train_X;
        List<Integer> train_Y;
        List<String> sizes = new ArrayList<>(Arrays.asList("Extreme Set (10000 x 10)", "Large Set (100 x 10)", "Medium Set (30 x 3)", "Small Set (1 x 1)"));

        for(int k = 0; k < train_Xs.size(); k++){
            System.out.println(sizes.get(k));

            train_X = train_Xs.get(k);
            train_Y = train_Ys.get(k);

            for(int i = 0; i < 20; i++){
                dteSeq = new KMeans(10, 1);
                dtePar2 = new KMeans(10, 2);
                dtePar4 = new KMeans(10,4);
                //System.out.println("Iter: " + i);
                long startFitTime = System.nanoTime();
                dteSeq.fit(train_X);
                long endFitTime = System.nanoTime();
                fitTimesSeq.add(endFitTime - startFitTime);

                startFitTime = System.nanoTime();
                dtePar2.fit(train_X);
                endFitTime = System.nanoTime();
                fitTimesPar2.add(endFitTime - startFitTime);

                startFitTime = System.nanoTime();
                dtePar4.fit(train_X);
                endFitTime = System.nanoTime();
                fitTimesPar4.add(endFitTime - startFitTime);
            }

            Collections.sort(fitTimesSeq);
            Collections.sort(fitTimesPar2);
            Collections.sort(fitTimesPar4);

            LongSummaryStatistics statsSeq = fitTimesSeq.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            LongSummaryStatistics statsPar2 = fitTimesPar2.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            LongSummaryStatistics statsPar4 = fitTimesPar4.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            System.out.println("Sequential Fit Mean:           " + String.format("%d",(int) statsSeq.getAverage()));
            System.out.println("Sequential Fit Median:         " + String.format("%d",fitTimesSeq.get(9)));

            System.out.println("Parallel Fit 2 Threads Mean:   " + String.format("%d",(int) statsPar2.getAverage()));
            System.out.println("Parallel Fit 2 Threads Median: " + String.format("%d",fitTimesPar2.get(9)));

            System.out.println("Parallel Fit 4 Threads Mean:   " + String.format("%d",(int) statsPar4.getAverage()));
            System.out.println("Parallel Fit 4 Threads Median: " + String.format("%d",fitTimesPar4.get(9)));

            dteSeq = new KMeans(10, 1);
            dtePar2 = new KMeans(10, 2);
            dtePar4 = new KMeans(10,4);
            dteSeq.fit(train_X);
            dtePar2.fit(train_X);
            dtePar4.fit(train_X);

            for(int i = 0; i < 20; i++){
                //System.out.println("Iter: " + i);
                long startFitTime = System.nanoTime();
                dteSeq.predict(train_X);
                long endFitTime = System.nanoTime();
                predictTimesSeq.add(endFitTime - startFitTime);

                startFitTime = System.nanoTime();
                dtePar2.predict(train_X);
                endFitTime = System.nanoTime();
                predictTimesPar2.add(endFitTime - startFitTime);

                startFitTime = System.nanoTime();
                dtePar4.predict(train_X);
                endFitTime = System.nanoTime();
                predictTimesPar4.add(endFitTime - startFitTime);
            }

            Collections.sort(predictTimesSeq);
            Collections.sort(predictTimesPar2);
            Collections.sort(predictTimesPar4);

            statsSeq = predictTimesSeq.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            statsPar2 = predictTimesPar2.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            statsPar4 = predictTimesPar4.stream()
                    .mapToLong((x) -> x)
                    .summaryStatistics();

            System.out.println("Sequential Predict Mean:           " + String.format("%d",(int) statsSeq.getAverage()));
            System.out.println("Sequential Predict Median:         " + String.format("%d",predictTimesSeq.get(9)));

            System.out.println("Parallel Predict 2 Threads Mean:   " + String.format("%d",(int) statsPar2.getAverage()));
            System.out.println("Parallel Predict 2 Threads Median: " + String.format("%d",predictTimesPar2.get(9)));

            System.out.println("Parallel Predict 4 Threads Mean:   " + String.format("%d",(int) statsPar4.getAverage()));
            System.out.println("Parallel Predict 4 Threads Median: " + String.format("%d",predictTimesPar4.get(9)));
            System.out.println();
        }
    }
}

package datascilib.Classifiers.DecisionTree;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <h1>Descision Tree Estimator</h1>
 * Decision Tree Estimator is a tree that splits fitted values into a tree, where at each split, the fitted X and Y is
 * split based on a threshold and feature that optimizes the GINI gain.
 * </br></br>
 * If the tree is a leaf, returns a randomly selected classifier from the labels stored in that tree.
 * </br></br>
 * @author Brandon Pham
 * @version 0.1
 * @since 2020-04-29
 */
public class DescisionTreeEstimator{
    /**
     * A {@link Integer} which defines maximum number of threads to use.
     */
    private int n_jobs;
    /**
     * A {@link Double} which defines the threshold to compare with.
     */
    private double threshold;
    /**
     * A {@link Integer} which defines the feature to compare with.
     */
    private int feature_idx;
    /**
     * A {@link List} which defines the possible classifiers the tree returns.
     */
    private List<Integer> classifiers;
    /**
     * A {@link Double} which defines the GINI impurity of the fitted values.
     */
    private double impurity;
    /**
     * A {@link HashMap} which maps the # of unique labels/classes.
     */
    private HashMap<Integer, Integer> classCount;
    /**
     * A 2-D {@link List} which contains {@link Double} points that this tree was fitted on.
     */
    private List<List<Double>> X;
    /**
     * A 1-D {@link List} which contains {@link Integer} labels that this tree was fitted on.
     */
    private List<Integer> Y;
    /**
     * A 1-D {@link List} which contains {@link Integer} indexes corresponding to which features to
     * consider.
     */
    private List<Integer> feature_set;
    /**
     * A {@link DescisionTreeEstimator} which was fit on values less than the threshold.
     */
    private DescisionTreeEstimator lessTree;
    /**
     * A {@link DescisionTreeEstimator} which was fit on values greater than or equal to the threshold.
     */
    private DescisionTreeEstimator greaterTree;
    /**
     * A {@link List} of {@link DescisionTreeEstimator}s which contains all children
     * {@link DescisionTreeEstimator}s.
     */
    private List<DescisionTreeEstimator> treePool;
    /**
     * A {@link List} which contains the X values sent to the children {@link DescisionTreeEstimator}s.
     */
    private List<List<List<Double>>> XInputs;
    /**
     * A {@link List} which contains the Y values sent to the children {@link DescisionTreeEstimator}s.
     */
    private List<List<Integer>> YInputs;

    /**
     * The main constructor.
     */
    public DescisionTreeEstimator(){
        this.n_jobs = ManagementFactory.getThreadMXBean().getThreadCount();
        this.classCount = null;
        this.impurity = 1.0;
        this.feature_set = null;
        this.lessTree = null;
        this.greaterTree = null;
        this.treePool = new ArrayList<DescisionTreeEstimator>();
        this.XInputs = new ArrayList<List<List<Double>>>();
        this.YInputs = new ArrayList<List<Integer>>();
    }

    /**
     * An alternative constructor. Takes a {@link Integer} that defines maximum number of threads to use.
     *
     * Checks for if the passed value is greater than 0.
     *
     * @param n_jobs is a {@link Integer} that defines maximum number of threads to use.
     */
    public DescisionTreeEstimator(int n_jobs){
        if(n_jobs <= 0){
            throw new IllegalArgumentException("n_jobs must be at least 1");
        }
        this.n_jobs = n_jobs;
        this.classCount = null;
        this.impurity = 1.0;
        this.feature_set = null;
        this.lessTree = null;
        this.greaterTree = null;
        this.treePool = new ArrayList<DescisionTreeEstimator>();
        this.XInputs = new ArrayList<List<List<Double>>>();
        this.YInputs = new ArrayList<List<Integer>>();
    }

    /**
     * An alternative constructor. Takes a {@link Integer} that defines maximum number of threads to use and
     * a {@link List} contains {@link Integer} indexes of the features to consider.
     *
     * Checks for if the passed value is greater than 0.
     *
     * @param n_jobs is a {@link Integer} that defines maximum number of threads to use.
     * @param feature_set is a {@link List} that contains {@link Integer} indexes of the features
     *                    to consider.
     */
    public DescisionTreeEstimator(int n_jobs, List<Integer> feature_set){
        if(n_jobs <= 0){
            throw new IllegalArgumentException("n_jobs must be at least 1");
        }
        this.n_jobs = n_jobs;
        this.classCount = null;
        this.impurity = 1.0;
        this.feature_set = feature_set;
        this.lessTree = null;
        this.greaterTree = null;
        this.classifiers = new ArrayList<Integer>();
        this.treePool = new ArrayList<DescisionTreeEstimator>();
        this.XInputs = new ArrayList<List<List<Double>>>();
        this.YInputs = new ArrayList<List<Integer>>();
    }

    /**
     * An private constructor used for creating a copy of the current tree.
     */
    private DescisionTreeEstimator(int n_jobs, double threshold, int feature_idx, List<Integer> classifiers, double impurity,
                                   HashMap<Integer, Integer> classCount, List<List<Double>> X, List<Integer> Y,
                                   List<Integer> feature_set, DescisionTreeEstimator lessTree,
                                   DescisionTreeEstimator greaterTree){
        this.n_jobs = n_jobs;
        this.threshold = threshold;
        this.feature_idx = feature_idx;
        this.classifiers = new ArrayList<Integer>(classifiers);
        this.impurity = impurity;
        this.classCount = new HashMap<Integer, Integer>(classCount);
        this.X = new ArrayList<List<Double>>();
        for(List<Double> element: X){
            X.add(new ArrayList<Double>(element));
        }
        this.Y = new ArrayList<Integer>(Y);
        this.feature_set = new ArrayList<Integer>(feature_set);
        if(lessTree != null){
            this.lessTree = lessTree.copy();
        } else {
            this.lessTree = null;
        }
        if(greaterTree != null){
            this.greaterTree = greaterTree.copy();
        } else {
            this.greaterTree = null;
        }
    }

    /**
     * Fits a 2-D List of {@link Double}s X and a 1-D List of {@link Integer}s Y. Compares every
     * possible combination of thresholds and features to find the optimal combination that leads to the most gain.
     * Splits the X and Y into two trees that recursively split until gain can't be increased.
     *
     * Checks of: empty lists, null values
     *
     * @param X is a 2-D List of {@link Double} points
     * @param Y is a 1-D List of {@link Integer} labels
     */
    // O(N^2 F) - slow, should be parallelized
    public void fit(List<List<Double>> X, List<Integer> Y) {
        if(X == null || Y == null || X.size() == 0 || Y.size() == 0){
            throw new IllegalArgumentException("X and Y cannot be null or empty");
        } else {
            for(List<Double> x: X){
                if(x == null || x.size() == 0){
                    throw new IllegalArgumentException("Empty or null points added");
                }
            }
        }

        this.X = X;
        this.Y = Y;
        XInputs.add(X);
        YInputs.add(Y);
        if(feature_set == null){
            IntStream idx_stream = IntStream.range(0, this.X.get(0).size());
            feature_set = idx_stream.boxed().collect(Collectors.toList());
        }
        if(Y.size() == 1){
            classifiers = Y;
            return;
        }
        this.treePool.add(this);

        IntStream idx_stream;
        List<Integer> rowIdxs;

        DescisionTreeEstimator currentTree;
        List<List<Double>> currentX;
        List<Integer> currentY;
        while(treePool.size() != 0){
            Double highestGain = null;
            double bestThreshold = -1;
            int bestFeature = -1;

            //System.out.println("Getting Current Tree");
            //System.out.println("Tree Pool Size: " + treePool.size());
            //System.out.println("Removing 1 tree");
            currentTree = treePool.remove(0);
            currentX = XInputs.remove(0);
            currentY = YInputs.remove(0);

            currentTree.classCount = getClassCount(currentY);
            currentTree.impurity = getImpurity(currentTree.classCount, currentX.size());

            //System.out.println("Impurity: " + currentTree.impurity);

            if(currentTree.classCount.keySet().size() == 1){
                //System.out.println("Only one class");
                currentTree.classifiers = new ArrayList<Integer>();
                currentTree.classifiers.addAll(currentTree.classCount.keySet());
            } else {
                //System.out.println("More than one class");
                idx_stream = IntStream.range(0, currentX.size());
                rowIdxs = idx_stream.boxed().collect(Collectors.toList());

                ExecutorService executor;
                Future<FeatThreshPair>[] futures;
                if(rowIdxs.size() < n_jobs){
                    executor = Executors.newFixedThreadPool(rowIdxs.size());
                    futures = new Future[rowIdxs.size()];
                    for(int i = 0; i < rowIdxs.size(); i++){
                        Callable<FeatThreshPair> cb = new testThresholdCallable(new ArrayList<Integer>(rowIdxs.subList(i,i+1)), currentX, currentY, currentTree.impurity);
                        futures[i] = executor.submit(cb);
                    }
                } else {
                    executor = Executors.newFixedThreadPool(n_jobs);
                    futures = new Future[n_jobs];
                    int thread_size = rowIdxs.size() / n_jobs;
                    for(int i = 0; i < n_jobs; i++){
                        Callable<FeatThreshPair> cb;
                        if(i == n_jobs - 1){
                            cb = new testThresholdCallable(new ArrayList<Integer>(rowIdxs.subList(i*thread_size, rowIdxs.size())), currentX, currentY, currentTree.impurity);
                        } else {
                            cb = new testThresholdCallable(new ArrayList<Integer>(rowIdxs.subList(i*thread_size, (i+1)*thread_size)), currentX, currentY, currentTree.impurity);
                        }
                        futures[i] = executor.submit(cb);
                    }
                }
                for (Future<FeatThreshPair> future : futures) {
                    FeatThreshPair feat_thresh_pair = new FeatThreshPair(-1, 0, -2);
                    try {
                        feat_thresh_pair = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (highestGain == null || feat_thresh_pair.gain > highestGain) {
                        highestGain = feat_thresh_pair.gain;
                        bestThreshold = feat_thresh_pair.threshold;
                        bestFeature = feat_thresh_pair.feature;
                    }
                }
                executor.shutdown();
                //System.out.println("Highest Gain: " + highestGain);
                //System.out.println("Best Threshold: " + bestThreshold);
                //System.out.println("Best Feature: " + bestFeature);
                currentTree.feature_idx = bestFeature;
                currentTree.threshold = bestThreshold;

                if(highestGain != null && highestGain > 0.0){
                    splitTree(currentX, currentY, bestFeature, bestThreshold, currentTree);
                } else {
                    currentTree.classifiers = new ArrayList<Integer>();
                    currentTree.classifiers.addAll(currentTree.classCount.keySet());
                }
            }
        }
    }

    //O(N)
    private void splitTree(List<List<Double>> splitX, List<Integer> splitY, int feature_idx,  double threshold, DescisionTreeEstimator currentTree){
        List<List<Double>> lessX = new ArrayList<List<Double>>();
        List<Integer> lessY = new ArrayList<Integer>();
        List<List<Double>> greaterX = new ArrayList<List<Double>>();
        List<Integer> greaterY = new ArrayList<Integer>();

        for(int i = 0; i < splitX.size(); i++){
            //System.out.println("Value: " + splitX.get(i).get(feature_idx) + "; Threshold: " + threshold);

            if(splitX.get(i).get(feature_idx) >= threshold){
                greaterX.add(splitX.get(i));
                greaterY.add(splitY.get(i));
            } else {
                lessX.add(splitX.get(i));
                lessY.add(splitY.get(i));
            }
        }

        if(greaterX.size() != 0 && lessX.size() != 0){
            currentTree.greaterTree = new DescisionTreeEstimator(n_jobs, feature_set);
            currentTree.lessTree = new DescisionTreeEstimator(n_jobs, feature_set);
            //System.out.println("Adding 2 trees");
            treePool.add(currentTree.greaterTree);
            XInputs.add(greaterX);
            YInputs.add(greaterY);
            treePool.add(currentTree.lessTree);
            XInputs.add(lessX);
            YInputs.add(lessY);
        } else {
            //System.out.println("Special case");

            // Special case: identical features, but different classification
            currentTree.classifiers = new ArrayList<Integer>();
            currentTree.classifiers.addAll(currentTree.classCount.keySet());
        }
    }

    public class predictHelperRunnable implements Runnable{
        int start;
        int end;
        List<List<Double>> X;
        DescisionTreeEstimator root;
        List<Integer> pred;

        public predictHelperRunnable(int start, int end, List<List<Double>> X, DescisionTreeEstimator root, List<Integer> predictions){
            this.start = start;
            this.end = end;
            this.X = X;
            this.root = root;
            this.pred = predictions;
        }

        @Override
        public void run() {
            for(int i = start; i < end; i++){
                DescisionTreeEstimator current = root;
                while(pred.get(i) == -1){
                    if(current.classifiers != null && current.classifiers.size() > 0){
                        int randomClass = ThreadLocalRandom.current().nextInt(current.classifiers.size());
                        pred.set(i, current.classifiers.get(randomClass));
                    } else {
                        if(this.X.get(i).get(current.feature_idx) >= current.threshold){
                            // O(logN)
                            current = current.greaterTree;
                        } else {
                            // O(logN)
                            current = current.lessTree;
                        }
                    }
                }
            }
        }
    }

    /**
     * Predicts the labels for a 2-D List of {@link Double}s X. Compares features and thresholds. Traverses
     * trees until a classifier is returned.
     *
     * Checks of: empty lists
     *
     * @param X is a 2-D List of {@link Double} points.
     * @return 1-D List of {@link Integer} labels corresponding to X.
     */
    //O(NlogN) - slowish, but difficult to parallelize
    public List<Integer> predict(List<List<Double>> X) {
        if(X == null){
            return null;
        } else {
            for(List<Double> x: X){
                if(x == null || x.size() == 0){
                    throw new IllegalArgumentException("X contains Empty or null points");
                }
            }
        }

        List<Integer> predictions = new ArrayList<Integer>();
        for(int i = 0; i < X.size(); i++){
            predictions.add(-1);
        }

        /*
        if(classifiers != null && classifiers.size() != 0){
            for(int i = 0; i < X.size(); i++){
                int randomClass = ThreadLocalRandom.current().nextInt(classifiers.size());
                predictions.set(i, classifiers.get(randomClass));
            }
        } else {
            for(int i = 0; i < X.size(); i++){
                predictions.set(i, predictHelper(X.get(i)));
            }
        }*/
        List<Thread> threads = new ArrayList<Thread>();
        if(X.size() < n_jobs){
            for(int i = 0; i < X.size(); i++){
                Runnable rb = new predictHelperRunnable(i, i+1, X, this, predictions);
                threads.add(new Thread(rb));
                threads.get(i).start();
            }
        } else {
            int threadSize = X.size()/n_jobs;
            for(int i = 0; i < n_jobs; i++){
                Runnable rb;
                if(i == n_jobs-1){
                    rb = new predictHelperRunnable(i*threadSize, (i+1)*threadSize, X, this, predictions);
                } else {
                    rb = new predictHelperRunnable(i*threadSize, X.size(), X, this, predictions);
                }
                threads.add(new Thread(rb));
                threads.get(i).start();
            }
        }
        for(Thread thread: threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return predictions;
    }

    /**
     * Helper function for {@link #predict(List)}. Predicts the labels for a List of {@link Double}s X.
     * Compares features and thresholds. Traverses trees until a classifier is returned.
     *
     * Checks of: empty lists
     *
     * @param X is a 1-D List of {@link Double} points.
     * @return {@link Integer} label corresponding to X.
     */
    private Integer predictHelper(List<Double> X){
        if(classifiers != null && classifiers.size() != 0){
            int randomClass = ThreadLocalRandom.current().nextInt(classifiers.size());
            return classifiers.get(randomClass);
        } else {
            if(X.get(feature_idx) >= threshold){
                // O(logN)
                return greaterTree.predictHelper(X);
            } else {
                // O(logN)
                return lessTree.predictHelper(X);
            }
        }
    }

    /**
     * Copies current tree and returns it.
     * @return {@link DescisionTreeEstimator} copy of current tree.
     */
    public DescisionTreeEstimator copy() {
        return new DescisionTreeEstimator(n_jobs, threshold,feature_idx, classifiers, impurity, classCount, X, Y,
                feature_set, lessTree, greaterTree);
    }

    /**
     * Helper class that groups a feature index, threshold, and associated gain together.
     */
    public class FeatThreshPair{
        public int feature;
        public double threshold;
        public double gain;

        public FeatThreshPair(int feature, double threshold, double gain){
            this.feature = feature;
            this.threshold = threshold;
            this.gain = gain;
        }
    }

    /**
     * Helper Callable used to return the gain associated with a certain feature and threshold.
     */
    private class testThresholdCallable implements Callable<FeatThreshPair> {
        List<Integer> rowIdxs;
        double gain;
        HashMap<Integer, Integer> testLessCount;
        HashMap<Integer, Integer> testGreaterCount;
        List<List<Double>> testX;
        List<Integer> testY;
        double testImpurity;

        public testThresholdCallable(List<Integer> rowIdxs, List<List<Double>> testX, List<Integer> testY, double testImpurity){
            this.rowIdxs = rowIdxs;
            this.gain = 0.0;
            this.testLessCount = new HashMap<Integer, Integer>();
            this.testGreaterCount = new HashMap<Integer, Integer>();
            this.testX = testX;
            this.testY = testY;
            this.testImpurity = testImpurity;
        }

        @Override
        public FeatThreshPair call() {
            Double highestGain = null;
            int bestFeatureIdx = -1;
            double bestThreshold = -1;

            // For each threshold row
            for(int rowIdx: rowIdxs){
                // For each feature
                for(int i = 0; i < feature_set.size(); i++){
                    List<Double> row = testX.get(rowIdx);
                    double threshold = row.get(i);
                    //System.out.println("Feature: " + i + "; Threshold: " + threshold + " ############");
                    // Split rows into groups based on threshold
                    int numLess = 0;
                    int numGreater = 0;
                    for(int j = 0; j < testX.size(); j++){
                        //System.out.println("Value: " + testX.get(j).get(i) + "; Threshold: " + threshold);
                        if(testX.get(j).get(i) >= threshold){
                            //System.out.println("Adding row " + j + " to greater group");
                            if(!testGreaterCount.containsKey(testY.get(j))){
                                testGreaterCount.put(testY.get(j), 0);
                            }
                            testGreaterCount.put(testY.get(j), testGreaterCount.get(testY.get(j)) + 1);
                            numGreater++;
                        } else {
                            //System.out.println("Adding row " + j + " to less group");
                            if(!testLessCount.containsKey(testY.get(j))){
                                testLessCount.put(testY.get(j), 0);
                            }
                            testLessCount.put(testY.get(j), testLessCount.get(testY.get(j)) + 1);
                            numLess++;
                        }
                    }
                    double avgImpurity;
                    // Calculate avgerage impurity
                    if(testLessCount.keySet().size() == 0 || testGreaterCount.keySet().size() == 0){
                        if(testLessCount.keySet().size() == 0){
                            //System.out.println("Test Less Count Empty");
                        } else {
                            //System.out.println("Test Greater Count Empty");
                        }
                        avgImpurity = 1.0;
                    } else {
                        // Get impurity for each group
                        double lessImpurity = getImpurity(testLessCount, numLess);
                        //System.out.println("Less Impurity: " + lessImpurity +  "; Size: " + testLessCount.keySet().size());
                        double greaterImpurity = getImpurity(testGreaterCount, numGreater);
                        //System.out.println("Greater Impurity: " + greaterImpurity +  "; Size: " + testGreaterCount.keySet().size());

                        avgImpurity = ((double) testLessCount.size() / (double) testX.size()) * lessImpurity
                                + ((double) testLessCount.size() / (double) testX.size()) * greaterImpurity;
                    }
                    //System.out.println("Average Impurity: " + avgImpurity);
                    double gain = testImpurity - avgImpurity;
                    //System.out.println("Gain: " + gain);

                    if(highestGain == null || gain > highestGain){
                        //System.out.println("New Highest Gain: " + gain);
                        //System.out.println("New Best Feature: " + i);
                        //System.out.println("New Best Threshold: " + threshold);
                        highestGain = gain;
                        bestFeatureIdx = i;
                        bestThreshold = threshold;
                    }
                    testLessCount.clear();
                    testGreaterCount.clear();
                }
            }
            if(highestGain == null){
                highestGain = -1.0;
            }
            //System.out.println("bestFeatureIdx: " + bestFeatureIdx);
            //System.out.println("bestThreshold: " + bestThreshold);
            //System.out.println("highestGain: " + highestGain);
            return new FeatThreshPair(bestFeatureIdx, bestThreshold, highestGain);
        }
    }

    // O(Y)
    // Y - length of labels
    /**
     * Calculates # of unique labels/classes
     * @param labels is a {@link List} of {@link Integer} labels
     */
    private HashMap<Integer, Integer> getClassCount(List<Integer> labels){
        HashMap<Integer, Integer>  classCount = new HashMap<Integer, Integer> ();
        for (Integer label : (List<Integer>) labels) {
            if (!classCount.containsKey(label)) {
                classCount.put(label, 0);
            }
            classCount.put(label, classCount.get(label) + 1);
        }
        return classCount;
    }

    // O(C)
    // C - # of unique labels
    /**
     * Calculates impurity of a classCount {@link HashMap}
     * @param classCount is a {@link HashMap} used to store # of unique labels/classes
     * @param numRows is a {@link Integer} containing # of rows in fitted X
     */
    private double getImpurity(HashMap<Integer, Integer> classCount, int numRows){
        double impurity = 1;
        if(classCount.keySet().size() == 1){
            return 0.0;
        }
        for(Integer key: classCount.keySet()){
            double prob = (double) classCount.get(key) / (double) numRows;
            //System.out.println("Prob: " + prob);
            impurity -= Math.pow(prob, 2);
        }
        return impurity;
    }
}

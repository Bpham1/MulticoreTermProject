import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DescisionTreeEstimator implements Estimator{
    int n_jobs;
    double threshold;
    int feature_idx;
    List<Integer> classifiers;
    double impurity;
    HashMap<Integer, Integer> classCount;
    List<List<Double>> X;
    List<Integer> Y;
    List<Integer> feature_set;
    DescisionTreeEstimator lessTree;
    DescisionTreeEstimator greaterTree;
    Random r;
    private List<DescisionTreeEstimator> treePool;
    private List<List<List<Double>>> XInputs;
    private List<List<Integer>> YInputs;

    public DescisionTreeEstimator(int n_jobs){
        this.n_jobs = n_jobs;
        this.classCount = null;
        this.impurity = 1.0;
        this.feature_set = null;
        this.lessTree = null;
        this.greaterTree = null;
        this.classifiers = new ArrayList<Integer>();
        this.r = new Random();
        this.treePool = new ArrayList<DescisionTreeEstimator>();
        this.XInputs = new ArrayList<List<List<Double>>>();
        this.YInputs = new ArrayList<List<Integer>>();
    }

    public DescisionTreeEstimator(int n_jobs, List<Integer> feature_set){
        this.n_jobs = n_jobs;
        this.classCount = null;
        this.impurity = 1.0;
        this.feature_set = feature_set;
        this.lessTree = null;
        this.greaterTree = null;
        this.classifiers = new ArrayList<Integer>();
        this.r = new Random();
        this.treePool = new ArrayList<DescisionTreeEstimator>();
        this.XInputs = new ArrayList<List<List<Double>>>();
        this.YInputs = new ArrayList<List<Integer>>();
    }

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
            this.lessTree = (DescisionTreeEstimator) lessTree.copy();
        } else {
            this.lessTree = null;
        }
        if(greaterTree != null){
            this.greaterTree = (DescisionTreeEstimator) greaterTree.copy();
        } else {
            this.greaterTree = null;
        }
    }

    // O(N^2 F) - slow, should be parallelized
    @Override
    public void fit(List X, List Y) {
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
            double highestGain = -1;
            double bestThreshold = -1;
            int bestFeature = -1;

            System.out.println("Getting Current Tree");
            System.out.println("Tree Pool Size: " + treePool.size());
            System.out.println("Removing 1 tree");
            currentTree = treePool.remove(0);
            currentX = XInputs.remove(0);
            currentY = YInputs.remove(0);

            currentTree.classCount = getClassCount(currentY);
            currentTree.impurity = getImpurity(currentTree.classCount);

            //System.out.println("Impurity: " + currentTree.impurity);

            if(currentTree.classCount.keySet().size() == 1){
                System.out.println("Only one class");
                currentTree.classifiers = new ArrayList<Integer>();
                currentTree.classifiers.addAll(currentTree.classCount.keySet());
            } else {
                System.out.println("More than one class");
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
                    if (feat_thresh_pair.gain > highestGain) {
                        highestGain = feat_thresh_pair.gain;
                        bestThreshold = feat_thresh_pair.threshold;
                        bestFeature = feat_thresh_pair.feature;
                    }
                }
                executor.shutdown();
                System.out.println("Highest Gain: " + highestGain);
                System.out.println("Best Threshold: " + bestThreshold);
                System.out.println("Best Feature: " + bestFeature);
                currentTree.feature_idx = bestFeature;
                currentTree.threshold = bestThreshold;
                splitTree(currentX, currentY, bestFeature, bestThreshold, currentTree);
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
            System.out.println("Value: " + splitX.get(i).get(feature_idx) + "; Threshold: " + threshold);
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
            System.out.println("Adding 2 trees");
            treePool.add(currentTree.greaterTree);
            XInputs.add(greaterX);
            YInputs.add(greaterY);
            treePool.add(currentTree.lessTree);
            XInputs.add(lessX);
            YInputs.add(lessY);
        } else {
            System.out.println("Special case");

            // Special case: identical features, but different classification
            currentTree.classifiers = new ArrayList<Integer>();
            currentTree.classifiers.addAll(currentTree.classCount.keySet());
        }
    }

    //O(NlogN) - slowish, but difficult to parallelize
    @Override
    public List<Integer> predict(List X) {
        List<List<Double>> castedX = X;
        List<Integer> predictions = new ArrayList<Integer>();
        if(classifiers.size() != 0){
            for(int i = 0; i < X.size(); i++){
                int randomClass = r.nextInt(classifiers.size());
                predictions.add(classifiers.get(randomClass));
            }
        } else {
            for(int i = 0; i < X.size(); i++){
                if(castedX.get(i).get(feature_idx) > threshold){
                    // O(logN)
                    predictions.add(greaterTree.predict(castedX.get(i)).get(0));
                } else {
                    // O(logN)
                    predictions.add(lessTree.predict(castedX.get(i)).get(0));
                }
            }
        }
        return predictions;
    }

    @Override
    public Estimator copy() {
        return new DescisionTreeEstimator(n_jobs, threshold,feature_idx, classifiers, impurity, classCount, X, Y,
                feature_set, lessTree, greaterTree);
    }

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
        public FeatThreshPair call() throws Exception {
            double highestGain = 0;
            int bestFeatureIdx = -1;
            double bestThreshold = -1;

            // For each threshold row
            for(int rowIdx: rowIdxs){
                // For each feature
                for(int i = 0; i < feature_set.size(); i++){
                    List<Double> row = testX.get(rowIdx);
                    double threshold = row.get(i);
                    //System.out.println("Feature: " + i + "; Threshold: " + threshold);
                    // Split rows into groups based on threshold
                    for(int j = 0; j < testX.size(); j++){
                        //System.out.println("Value: " + testX.get(j).get(feature_idx));
                        if(testX.get(j).get(i) >= threshold){
                            //System.out.println("Adding row " + j + " to greater group");
                            if(!testGreaterCount.containsKey(testY.get(j))){
                                testGreaterCount.put(testY.get(j), 0);
                            }
                            testGreaterCount.put(testY.get(j), testGreaterCount.get(testY.get(j)) + 1);
                        } else {
                            //System.out.println("Adding row " + j + " to less group");
                            if(!testLessCount.containsKey(testY.get(j))){
                                testLessCount.put(testY.get(j), 0);
                            }
                            testLessCount.put(testY.get(j), testLessCount.get(testY.get(j)) + 1);
                        }
                    }
                    double avgImpurity;
                    // Calculate avgerage impurity (no gain if
                    if(testLessCount.keySet().size() == 0 || testGreaterCount.keySet().size() == 0){
                        avgImpurity = 1.0;
                    } else {
                        // Get impurity for each group
                        double lessImpurity = getImpurity(testLessCount);
                        //System.out.println("Less Impurity: " + lessImpurity +  "; Size: " + testLessCount.keySet().size());
                        double greaterImpurity = getImpurity(testGreaterCount);
                        //System.out.println("Greater Impurity: " + greaterImpurity +  "; Size: " + testGreaterCount.keySet().size());

                        avgImpurity = ((double) testLessCount.size() / (double) testX.size()) * lessImpurity
                                + ((double) testLessCount.size() / (double) testX.size()) * greaterImpurity;
                    }
                    //System.out.println("Average Impurity: " + avgImpurity);
                    double gain = testImpurity - avgImpurity;
                    //System.out.println("Gain: " + gain);

                    if(gain > highestGain){
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
            //System.out.println("bestFeatureIdx: " + bestFeatureIdx);
            //System.out.println("bestThreshold: " + bestThreshold);
            //System.out.println("highestGain: " + highestGain);
            return new FeatThreshPair(bestFeatureIdx, bestThreshold, highestGain);
        }
    }

    // O(Y)
    // Y - length of labels
    HashMap<Integer, Integer> getClassCount(List<Integer> labels){
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
    double getImpurity(HashMap<Integer, Integer> classCount){
        double impurity = 1;
        if(classCount.keySet().size() == 1){
            return 0.0;
        }
        for(Integer key: classCount.keySet()){
            double prob = (double) classCount.get(key) / (double) classCount.keySet().size();
            impurity -= Math.pow(prob, 2);
        }
        return impurity;
    }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DescisionTreeEstimator implements Estimator{
    int n_jobs;
    double threshold;
    int feature_idx;
    Integer classifier;
    double impurity;
    HashMap<Integer, Integer> classCount;
    List<List<Double>> X;
    List<Integer> Y;
    List<Integer> feature_set;
    DescisionTreeEstimator lessTree;
    DescisionTreeEstimator greaterTree;

    public DescisionTreeEstimator(int n_jobs){
        this.n_jobs = n_jobs;
        this.classCount = getClassCount(Y);
        this.impurity = getImpurity(classCount);
        this.feature_set = null;
        this.lessTree = null;
        this.greaterTree = null;
        this.classifier = null;
    }

    public DescisionTreeEstimator(int n_jobs, List<Integer> feature_set){
        this.n_jobs = n_jobs;
        this.classCount = getClassCount(Y);
        this.impurity = getImpurity(classCount);
        this.feature_set = feature_set;
        this.lessTree = null;
        this.greaterTree = null;
        this.classifier = null;
    }

    private DescisionTreeEstimator(int n_jobs, double threshold, int feature_idx, Integer classifier, double impurity,
                                  HashMap<Integer, Integer> classCount, List<List<Double>> X, List<Integer> Y,
                                  List<Integer> feature_set, DescisionTreeEstimator lessTree,
                                  DescisionTreeEstimator greaterTree){
        this.n_jobs = n_jobs;
        this.threshold = threshold;
        this.feature_idx = feature_idx;
        this.classifier = classifier;
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
        if(feature_set == null){
            IntStream idx_stream = IntStream.range(0, ((List<List<Double>>) X).get(0).size());
            feature_set = idx_stream.boxed().collect(Collectors.toList());
        }
        if(Y.size() == 1){
            classifier = (int) Y.get(0);
            return;
        }
        double highestGain = -1;
        double bestThreshold = -1;
        int bestFeature = -1;
        for(int feature_idx: feature_set){
            for(List<Double> row: this.X){
                double gain = testThreshold(feature_idx, row.get(feature_idx));
                if (gain > highestGain){
                    highestGain = gain;
                    bestThreshold = row.get(feature_idx);
                    bestFeature = feature_idx;
                }
            }
        }
        this.feature_idx = bestFeature;
        this.threshold = bestThreshold;
        splitTree();
    }

    //O(NlogN) - slowish, should be parallelized
    @Override
    public List<Integer> predict(List X) {
        List<List<Double>> castedX = X;
        List<Integer> predictions = new ArrayList<Integer>();
        if(classifier != null){
            for(int i = 0; i < X.size(); i++){
                predictions.add(classifier);
            }
        } else {
            // Do this in parallel (However, writing to predictions must be shared)
            for(int i = 0; i < X.size(); i++){
                if(castedX.get(i).get(feature_idx) > threshold){
                    predictions.add(greaterTree.predict(castedX.get(i)).get(0));
                }
            }
        }
        return predictions;
    }

    @Override
    public Estimator copy() {
        return new DescisionTreeEstimator(n_jobs, threshold,feature_idx, classifier, impurity, classCount, X, Y,
                feature_set, lessTree, greaterTree);
    }

    // O(N) or O(Y)
    private double testThreshold(int feature_idx, double threshold){
        HashMap<Integer, Integer> testLessCount = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> testGreaterCount = new HashMap<Integer, Integer>();

        for(int i = 0; i < X.size(); i++){
            if(X.get(i).get(feature_idx) > threshold){
                if(!testGreaterCount.containsKey(Y.get(i))){
                    testGreaterCount.put(Y.get(i), 0);
                }
                testGreaterCount.put(Y.get(i), testGreaterCount.get(Y.get(i)) + 1);
            } else {
                if(!testLessCount.containsKey(Y.get(i))){
                    testLessCount.put(Y.get(i), 0);
                }
                testLessCount.put(Y.get(i), testLessCount.get(Y.get(i)) + 1);
            }
        }
        double lessImpurity = getImpurity(testLessCount);
        double greaterImpurity = getImpurity(testGreaterCount);
        if(lessImpurity == 0.0 || greaterImpurity == 0.0){
            return 0;
        }
        double avgImpurity = ((double) testLessCount.size() / (double) X.size()) * lessImpurity
                + ((double) testLessCount.size() / (double) X.size()) * greaterImpurity;
        return impurity - avgImpurity;
    }

    //O(N^2 F) - slow, update fit function to fix
    private void splitTree(){
        List<List<Double>> lessX = new ArrayList<List<Double>>();
        List<Integer> lessY = new ArrayList<Integer>();
        List<List<Double>> greaterX = new ArrayList<List<Double>>();
        List<Integer> greaterY = new ArrayList<Integer>();
        if(greaterTree == null){
            greaterTree = new DescisionTreeEstimator(n_jobs, feature_set);
        }
        if(lessTree == null){
            lessTree = new DescisionTreeEstimator(n_jobs, feature_set);
        }
        for(int i = 0; i < X.size(); i++){
            if(X.get(i).get(feature_idx) > threshold){
                greaterX.add(X.get(i));
                greaterY.add(Y.get(i));
            } else {
                lessX.add(X.get(i));
                lessY.add(Y.get(i));
            }
        }
        greaterTree.fit(greaterX, greaterY);
        lessTree.fit(lessX, lessY);
    }

    // O(Y)
    // Y - length of labels
    HashMap<Integer, Integer> getClassCount(List labels){
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
        for(Integer key: classCount.keySet()){
            double prob = (double) classCount.get(key) / (double) classCount.keySet().size();
            impurity -= Math.pow(prob, 2);
        }
        return impurity;
    }
}

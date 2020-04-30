package datascilib.Classifiers.RandomForest;

import datascilib.Classifiers.DecisionTree.DescisionTreeEstimator;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <h1>Random Forest</h1>
 * Random Forest is an enhanced version of Decision Tree that aims to reduce overfitting by averaging many weaker
 * Decision Trees.
 * </br></br>
 * Random Forest works by implementing many {@link DescisionTreeEstimator}s defined by {@link #n_estimators} that each fit on
 * a randomized data set derived from the X and Y passed into the fit function. We first randomize which rows to fit on
 * by taking a "bootstrap", which is the same dimensions as X, but is created by picking random rows from X with
 * repeats. Next, the features that are considered are also randomized.
 * </br></br>
 * When using {@link #predict(List)}, each {@link DescisionTreeEstimator} predicts the results and those predictions are averaged to
 * create the final result.
 * </br></br>
 * @author Brandon Pham
 * @version 0.1
 * @since 2020-04-29
 */
public class RandomForest{
    /**
     * A {@link Integer} which defines how many Decision Tree Estimators will be used
     */
    private int n_estimators;
    /**
     * A {@link Integer} which defines maximum number of threads to use
     */
    private int n_jobs;

    /**
     * A {@link List} which holds the {@link DescisionTreeEstimator}s used
     */
    private List<DescisionTreeEstimator> estimators;
    /**
     * A {@link List} which holds the data to fit each {@link DescisionTreeEstimator}
     */
    private List<List<List<Double>>> train_datas;
    /**
     * A {@link Random} object used to generate random {@link Integer}s
     */
    private Random r;

    /**
     * The main constructor. Takes a {@link Integer} that defines how many {@link DescisionTreeEstimator}s
     * to use.
     *
     * Checks for if the passed value is greater than 0.
     *
     * @param n_estimators is a {@link Integer} that defines how many {@link DescisionTreeEstimator}s to use
     */
    public RandomForest(int n_estimators){
        if(n_estimators <= 0){
            throw new IllegalArgumentException("Must have at least 1 estimator");
        }
        this.n_estimators = n_estimators;
        this.n_jobs = ManagementFactory.getThreadMXBean().getThreadCount();
        this.r = new Random();
        this.estimators = new ArrayList<DescisionTreeEstimator>();
        this.train_datas = new ArrayList<List<List<Double>>>();
    }

    /**
     * An alternative constructor. Takes two {@link Integer}s that defines how many
     * {@link DescisionTreeEstimator}s to use and maximum number of threads to use.
     *
     * Checks for if the passed values are greater than 0.
     *
     * @param n_estimators is a {@link Integer} that defines how many {@link DescisionTreeEstimator}s to use
     * @param n_jobs is a {@link Integer} that defines maximum number of threads to use.
     */
    public RandomForest(int n_estimators, int n_jobs){
        if(n_estimators <= 0){
            throw new IllegalArgumentException("Must have at least 1 estimator");
        }
        this.n_estimators = n_estimators;
        if(n_jobs <= 0){
            throw new IllegalArgumentException("Must have at least 1 n_job");
        }
        this.n_jobs = n_jobs;
        this.r = new Random();
        this.estimators = new ArrayList<DescisionTreeEstimator>();
        this.train_datas = new ArrayList<List<List<Double>>>();
    }

    /**
     * Fits a 2-D List of {@link Double}s X and a 1-D List of {@link Integer}s Y.  Creates
     * {@link #n_estimators} number of {@link DescisionTreeEstimator}s and fits a randomized bootstrap of the passed X
     * to each.
     *
     * Checks of: empty lists, null values
     *
     * @param X is a 2-D List of {@link Double} points
     * @param Y is a 1-D List of {@link Integer} labels
     */
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

        List<List<Integer>> labels = new ArrayList<List<Integer>>();
        List<List<Integer>> feature_subsets = new ArrayList<List<Integer>>();
        train_datas = new ArrayList<List<List<Double>>>();
        estimators = new ArrayList<DescisionTreeEstimator>();

        // Create bootstraps
        int randomNum;

        for(int i = 0; i < n_estimators; i++){
            List<List<Double>> train_data = new ArrayList<List<Double>>();
            List<Integer> label = new ArrayList<Integer>();
            for(int j = 0; j < X.size(); j++){
                randomNum = r.nextInt(X.size());
                train_data.add(X.get(randomNum));
                label.add(Y.get(randomNum));
            }
            train_datas.add(train_data);
            labels.add(label);
        }

        // Create feature subset
        for(int i = 0; i < n_estimators; i++){
            int size = X.get(0).size();

            IntStream idx_stream = IntStream.range(0, size);
            List<Integer> idxs = idx_stream.boxed().collect(Collectors.toList());
            Collections.shuffle(idxs);

            if(size == 1){
                randomNum = 1;
            } else {
                randomNum = r.nextInt(size - 1) + 1;
            }

            List<Integer> feature_subset = idxs.subList(0, randomNum);
            feature_subsets.add(feature_subset);
        }

        for(int i = 0; i < n_estimators; i++){
            estimators.add(new DescisionTreeEstimator(n_jobs, feature_subsets.get(i)));
            estimators.get(i).fit(train_datas.get(i), labels.get(i));
        }
    }

    /**
     * Predicts the labels for a 2-D List of {@link Double}s X. Passed the X to each estimator and averages
     * the results to create the final prediction, which is a 1-D List of {@link Integer} labels.
     *
     * Checks of: empty lists
     *
     * @param X is a 2-D List of {@link Double} points
     * @return 1-D List of {@link Integer} labels corresponding to X
     */
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

        List<List<Integer>> preds = new ArrayList<List<Integer>>();
        for(DescisionTreeEstimator est: estimators){
            preds.add(est.predict(X));
        }
        List<Integer> final_pred = new ArrayList<Integer>();
        for(int i = 0; i < X.size(); i++){
            HashMap<Integer, Integer> predMap = new HashMap<Integer, Integer>();
            for(List<Integer> pred: preds){
                if(!predMap.containsKey(pred.get(i))){
                    predMap.put(pred.get(i), 0);
                }
                predMap.put(pred.get(i), predMap.get(pred.get(i)) + 1);
            }
            int greatestPred = -1;
            int greatestPredAmount = 0;
            for(Integer key: predMap.keySet()){
                if(greatestPred == -1 || predMap.get(key) > greatestPredAmount) {
                    greatestPred = key;
                    greatestPredAmount = predMap.get(key);
                }
            }
            final_pred.add(greatestPred);
        }
        return final_pred;
    }
}

package datascilib.Classifiers.RandomForest;

import datascilib.Classifiers.DecisionTree.DescisionTreeEstimator;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomForest{
    private int n_estimators;
    private int n_jobs;
    private List<DescisionTreeEstimator> estimators;
    private List<List<List<Double>>> train_datas;
    private Random r;

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

    private RandomForest(int n_jobs, List<DescisionTreeEstimator> estimators){
        this.n_estimators = estimators.size();
        this.n_jobs = n_jobs;
        this.r = new Random();
        this.estimators = new ArrayList<DescisionTreeEstimator>();
        for(int i = 0; i < n_estimators; i++){
            estimators.add(estimators.get(i).copy());
        }
        this.train_datas = new ArrayList<List<List<Double>>>();
    }

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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomForest implements Estimator{
    int n_estimators;
    int n_jobs;
    List<Estimator> estimators;
    List<List> train_datas;
    List<List> labels;
    List<List<Integer>> feature_subsets;
    Random r;

    public RandomForest(int n_estimators, int n_jobs){
        this.n_estimators = n_estimators;
        this.n_jobs = n_jobs;
        this.r = new Random();
        this.estimators = new ArrayList<Estimator>();
    }

    private RandomForest(int n_jobs, List<Estimator> estimators){
        this.n_estimators = estimators.size();
        this.n_jobs = n_jobs;
        this.r = new Random();
        this.estimators = new ArrayList<Estimator>();
        for(int i = 0; i < n_estimators; i++){
            estimators.add(estimators.get(i).copy());
        }
    }

    @Override
    public void fit(List X, List Y) {
        // Create bootstraps
        int randomNum;
        for(int i = 0; i < n_estimators; i++){
            List train_data = new ArrayList();
            List label = new ArrayList();
            for(int j = 0; j < X.size(); j++){
                randomNum = r.nextInt(X.size()-1) + 1;
                train_data.add(X.get(j));
                label.add(Y.get(j));
            }
            train_datas.add(train_data);
            labels.add(label);
        }

        // Create feature subset
        if(X.get(0) instanceof Double || X.get(0) instanceof Float || X.get(0) instanceof Integer){
            for(int i = 0; i < n_estimators; i++){
                List<Integer> feature_subset = new ArrayList<Integer>();
                feature_subset.add(0);
                feature_subsets.add(feature_subset);
            }
        } else if (X.get(0) instanceof List) {
            for(int i = 0; i < n_estimators; i++){
                List temp = (List) X.get(0);
                int size = temp.size();

                IntStream idx_stream = IntStream.range(0, size);
                List<Integer> idxs = idx_stream.boxed().collect(Collectors.toList());
                Collections.shuffle(idxs);

                randomNum = r.nextInt(size - 1) + 1;

                List<Integer> feature_subset = idxs.subList(0, randomNum);
                feature_subsets.add(feature_subset);
            }
        }

        for(int i = 0; i < n_estimators; i++){
            estimators.add(new DescisionTreeEstimator(1, feature_subsets.get(i)));
            estimators.get(i).fit(train_datas.get(i), labels.get(i));
        }
    }

    @Override
    public List<Integer> predict(List X) {
        List<List<Integer>> preds = new ArrayList<List<Integer>>();
        for(Estimator est: estimators){
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

    @Override
    public Estimator copy() {
        return new RandomForest(n_jobs, estimators);
    }
}

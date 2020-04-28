import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.IntStream;

public class KNearestNeightbors {
    public int k;
    public List<List<Double>> X_train;
    public List<Integer> Y_train;
    public KNearestNeightbors(int k) {
        this.k = k;
    }

    public void fit(List<List<Double>> X, List<Integer> Y) {
        this.X_train = X;
        this.Y_train = Y;
    }

    public List<Integer> predict(List<List<Double>> X) {
        List<Integer> y_pred = new ArrayList<Integer>();
        // Can do in parallel
//        for (List<Double> x : X) {
//            y_pred.add(_predict(x));
//        }

        // Parallel
        X.parallelStream().forEach((x)-> {
            y_pred.add(_predict(x));
        });

        return y_pred;
    }

    private Integer _predict(List<Double> x) {
        Neighbor[] neighbors = new Neighbor[X_train.size()];

        // Can do in parallel
//        for (int i = 0; i < neighbors.length; i++) {
//            neighbors[i] = new Neighbor(distance(X_train.get(i), x), Y_train.get(i));
//        }

        // Parallel
        IntStream.range(0, neighbors.length).parallel().forEach(i -> {
            neighbors[i] = new Neighbor(distance(X_train.get(i), x), Y_train.get(i));
        });


        // Use Parallel sort
        Arrays.sort(neighbors);

        LinkedHashMap<Integer, Integer> votes = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < k; i++) {
            Integer label = neighbors[i].label;
            if (votes.containsKey(label)) {
                votes.put(label, votes.get(label) + 1);
            } else
                votes.put(label, 1);
        }

        Integer decision = null;
        double maxVote = 0;
        for (Map.Entry<Integer, Integer> vote : votes.entrySet()) {
            if (vote.getValue() > maxVote) {
                decision = vote.getKey();
                maxVote = vote.getValue();
            }
        }
        return decision;
    }

    private Double distance(List<Double> x, List<Double> x_train) {
        double sum = 0;

        // Can do in parallel
//        for (int i = 0; i < x_train.size(); i++) {
//            sum += Math.pow((x.get(i) - x_train.get(i)), 2);
//        }

        // Parallel
        List<Double> parallelSum = new ArrayList<Double>();
        IntStream.range(0, x_train.size()).forEach(i -> {
            parallelSum.add(Math.pow((x.get(i) - x_train.get(i)), 2));
        });
        sum = parallelSum.parallelStream().reduce(0.0, Double::sum);

        return Math.sqrt(sum);
    }


}

package datascilib.Classifiers.KNN;

import java.util.*;
import java.util.stream.IntStream;

/**
 * <h1>K Nearest Neighbors</h1>
 * A distance-based classifier that predicts labels finding the K "nearest" points and using the majority label of those
 * "neighbors". How near a point is based on a Euclidean distance.
 * </br></br>
 * Note that no actions are required during the fitting.
 * </br></br>
 * @author Jonathan Mounsif
 * @version 0.1
 * @since 2020-04-29
 */
public class KNearestNeightbors {
    private int k;
    private List<List<Double>> X_train;
    private List<Integer> Y_train;

    /**
     * The main constructor. Takes in an {@link java.lang.Integer} k that specifies how many neighbors to consider.
     * @param k is an {@link java.lang.Integer} k that specifies how many neighbors to consider.
     */
    public KNearestNeightbors(int k) {
        this.k = k;
    }

    /**
     * Stores a 2-D List of {@link java.lang.Double}s X and a 1-D List of {@link java.lang.Integer}s Y.
     *
     * Checks of: empty lists, null values
     *
     * @param X is a 2-D List of {@link java.lang.Double} points
     * @param Y is a 1-D List of {@link java.lang.Integer} labels
     */
    public void fit(List<List<Double>> X, List<Integer> Y) {
        if(X == null || Y == null || X.size() == 0 || Y.size() == 0){
            throw new IllegalArgumentException("X and Y cannot be null or empty");
        } else {
            for(List<Double> x: X){
                if(x == null || x.size() == 0){
                    throw new IllegalArgumentException("X contains Empty or null points");
                }
            }
        }

        this.X_train = X;
        this.Y_train = Y;
    }

    /**
     * Predicts the labels for a 2-D List of {@link java.lang.Double}s X.
     *
     * Checks of: empty lists
     *
     * @param X is a 2-D List of {@link java.lang.Double} points.
     * @return 1-D List of {@link java.lang.Integer} labels corresponding to X.
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

        List<Integer> y_pred = new ArrayList<Integer>();

        // Can do in parallel
        for (List<Double> x : X) {
            if(x.size() == 0){
                y_pred.add(null);
            } else {
                y_pred.add(_predict(x));
            }
        }

        return y_pred;
    }

    /**
     * Helper function for predict. Takes in a {@link List} of {@link java.lang.Double} that represents a single row in
     * X.
     * @param x is a {@link List} of {@link java.lang.Double} that is a single row of X
     */
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
        Arrays.parallelSort(neighbors);

        LinkedHashMap<Integer, Integer> votes = new LinkedHashMap<Integer, Integer>();

        int numNeighbors = k;
        if(X_train.size() < k){
            numNeighbors = X_train.size();
        }

        for (int i = 0; i < numNeighbors; i++) {
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

    /**
     * Calculates the distance between two points that are a {@link List} of {@link java.lang.Double}s.
     * @param x is a {@link List} of {@link java.lang.Double} that is a single row of X
     * @param x_train is a {@link List} of {@link java.lang.Double} that is a single row of fitted X
     */
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

package datascilib.Clustering.KMeans;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <h1>K Means</h1>
 * Clustering algorithm that can generate optimal labels when no training labels are provided.
 * </br></br>
 * Works by alternating between assigning points to their closest center and moving the center to the average of the
 * assigned points, until assigned points no longer change. The centers are now the optimal labels for the points
 * assigned to it. When predicting, the label is determined by the closest center to the point.
 * </br></br>
 * @author Brandon Pham
 * @version 0.1
 * @since 2020-04-29
 */
public class KMeans {
    /**
     * A 2-D {@link List} which contains {@link Double} points to generate centers around.
     */
    private List<List<Double>> points;
    /**
     * A 2-D {@link List} which contains {@link Integer} labels that is returned by {@link #predict(List)}
     */
    private List<Integer> prediction;
    /**
     * A 2-D {@link List} which contains {@link Double} centers.
     */
    private List<List<Double>> centers;
    /**
     * A {@link List} which contains {@link CenterMultiD} center objects corresponding to the centers.
     */
    private List<CenterMultiD> centerObjs;
    /**
     * A {@link Integer} which defines maximum number iterations before stopping the fitting.
     */
    int max_iter;
    /**
     * A {@link Integer} which defines # of clusters to create.
     */
    int n_clusters;
    /**
     * A {@link Integer} which defines maximum number of threads to use.
     */
    int n_jobs;

    // P - # of points
    // C - # of centers
    // D - # of dimensions for each point
    // I - # of iterations
    // N - # of clusters
    /**
     * The main constructor. Takes in a {@link Integer} that defines # of clusters to create. Defaults to
     * 300 iterations.
     * @param n_cluster is an {@link Integer} that defines # of clusters to create.
     */
    public KMeans(int n_cluster){
        if(n_cluster <= 0){
            throw new IllegalArgumentException("n_cluster must be at least 1");
        }
        this.max_iter = 300;
        this.n_clusters = n_cluster;
        this.n_jobs = ManagementFactory.getThreadMXBean().getThreadCount();
    }

    /**
     * An altnerative constructor. Takes in two {@link Integer}s that defines # of clusters to create and
     * maximum number of Threads to use. Defaults to 300 iterations.
     * @param n_cluster is an {@link Integer} that defines # of clusters to create.
     * @param n_jobs is an {@link Integer} that defines maximum number of threads to use.
     */
    public KMeans(int n_cluster, int n_jobs){
        if(n_cluster <= 0){
            throw new IllegalArgumentException("n_cluster must be at least 1");
        }
        this.max_iter = 300;
        this.n_clusters = n_cluster;
        if(n_jobs <= 0){
            throw new IllegalArgumentException("n_jobs must be at least 1");
        }
        this.n_jobs = n_jobs;
    }

    /**
     * An altnerative constructor. Takes in three {@link Integer}s that defines # of clusters to create,
     * maximum number of iterations for fitting, and maximum number of Threads to use.
     * @param n_cluster is an {@link Integer} that defines # of clusters to create.
     * @param max_iter is an {@link Integer} that defines maximum number of iterations before fitting stops.
     * @param n_jobs is an {@link Integer} that defines maximum number of threads to use.
     */
    public KMeans(int n_cluster, int max_iter, int n_jobs){
        if(n_cluster <= 0){
            throw new IllegalArgumentException("n_cluster must be at least 1");
        }
        if(max_iter <= 0){
            throw new IllegalArgumentException("max_iter must be at least 1");
        }
        this.max_iter = max_iter;
        this.n_clusters = n_cluster;
        if(n_jobs <= 0){
            throw new IllegalArgumentException("n_jobs must be at least 1");
        }
        this.n_jobs = n_jobs;
    }

    /**
     * Fits a 2-D List of {@link Double} points. Alternates between assigning points to their closest center
     * and moving the centers to the average of their assigned points, until assigned points no longer change.
     *
     * Checks of: empty lists, null values
     *
     * @param points is a 2-D List of {@link Double} points
     */
    public void fit(List<List<Double>> points){
        if(points == null || points.size() == 0){
            throw new IllegalArgumentException("points cannot be null or empty");
        } else {
            for(List<Double> x: points){
                if(x == null || x.size() == 0){
                    throw new IllegalArgumentException("Empty or null points added");
                }
            }
        }

        this.points = points;
        this.prediction = new ArrayList<Integer>();
        this.centers = initCenters(n_clusters);
        this.centerObjs = createCenters(this.centers);

        // O(P)
        for(int i = 0; i < points.size(); i++){
            prediction.add(-1);
        }

        // O(I)
        int num_iters = 0;
        try{
            while(num_iters < max_iter && UpdateCenters()){
                num_iters++;
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    /**
     * Initializes the centers.
     *
     * @param n_clusters is an {@link Integer} that defines # of centers to make.
     * @return 2-D List of {@link Double} centers randomly selected from X.
     */
    private List<List<Double>> initCenters(int n_clusters){
        List<List<Double>> temp = new ArrayList<List<Double>>(points);
        List<List<Double>> new_points = new ArrayList<List<Double>>();
        Collections.shuffle(temp);

        if(points.size() < n_clusters){
            for(int i = 0; i < points.size(); i++){
                List<Double> temp2 = new ArrayList<Double>();
                for(int j = 0; j < temp.get(i).size(); j++){
                    temp2.add(temp.get(i).get(j));
                }
                new_points.add(temp2);
            }
        } else {
            // O(ND)
            for(int i = 0; i < n_clusters; i++){
                List<Double> temp2 = new ArrayList<Double>();
                for(int j = 0; j < temp.get(i).size(); j++){
                    temp2.add(temp.get(i).get(j));
                }
                new_points.add(temp2);
            }
        }
        return new_points;
    }

    /**
     * Initializes the center objects based on centers.
     *
     * @param centers is a 2-D {@link List} of {@link Double} centers
     * @return List of {@link CenterMultiD} center objects corresponding to centers.
     */
    private List<CenterMultiD> createCenters(List<List<Double>> centers){
        // O(C)
        List<CenterMultiD> centerObjs = new ArrayList<CenterMultiD>();
        for(List<Double> center: centers){
            centerObjs.add(new CenterMultiD(center));
        }
        return centerObjs;
    }

    /**
     * Initializes the center objects based on centers. Alternates between assigning points to their closest center
     * and moving the centers to the average of their assigned points, until assigned points no longer change.
     *
     * @return true if centers were updated and false if centers did not change.
     */
    private boolean UpdateCenters() throws InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        int thread_size;
        int start;
        int end;

        if(points.size() < n_jobs){
            for(int i = 0; i < points.size(); i++){
                Runnable rb = new AssignPointsRunnableMultiD(points, centerObjs, prediction, i, i+1);
                threads.add(new Thread(rb));
                threads.get(i).start();
            }
        } else {
            thread_size = points.size() / n_jobs;
            // O(PCD/T)
            for(int i = 0; i < n_jobs; i++){
                start = i*thread_size;
                if(i == n_jobs - 1){
                    end = points.size();
                } else {
                    end = (i+1)*thread_size;
                }
                Runnable rb = new AssignPointsRunnableMultiD(points, centerObjs, prediction, start, end);
                threads.add(new Thread(rb));
                threads.get(i).start();
            }
        }
        for(Thread thread: threads){
            thread.join();
        }
        threads.clear();

        ExecutorService executor = Executors.newFixedThreadPool(n_jobs);
        Future<Boolean>[] futures;

        if(centers.size() < n_jobs){
            futures = new Future[centers.size()];
            for(int i = 0; i < centers.size(); i++){
                Callable<Boolean> cb = new MoveCentersCallableMultiD(centers, centerObjs, i, i+1);
                futures[i] = executor.submit(cb);
            }
        } else {
            futures = new Future[n_jobs];
            thread_size = centers.size() / n_jobs;
            // O(PCD/T)
            for(int i = 0; i < n_jobs; i++){
                start = i*thread_size;
                if(i == n_jobs - 1){
                    end = centers.size();
                } else {
                    end = (i+1)*thread_size;
                }
                Callable<Boolean> cb = new MoveCentersCallableMultiD(centers, centerObjs, start, end);
                futures[i] = executor.submit(cb);
            }
        }

        for (Future<Boolean> future : futures){
            try {
                if (future.get()){
                    executor.shutdown();
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return false;
    }

    /**
     * Returns centers.
     *
     * @return generated centers
     */
    public List<List<Double>> getCenters() {
        return centers;
    }

    /**
     * Predicts the labels for a 2-D List of {@link Double}s new_points. Assigns each point to their closest
     * center and returns their value as the label.
     *
     * Checks of: empty lists
     *
     * @param new_points is a 2-D {@link List} of {@link Double} points.
     * @return 1-D List of {@link Integer} labels corresponding to X.
     */
    public List<Integer> predict(List<List<Double>> new_points) {
        if(new_points == null){
            return null;
        } else {
            for(List<Double> x: new_points){
                if(x == null || x.size() == 0){
                    throw new IllegalArgumentException("new_points contains Empty or null points");
                }
            }
        }

        if(prediction != null){
            prediction.clear();
        } else {
            prediction = new ArrayList<Integer>();
        }
        for(int i = 0; i < new_points.size(); i++){
            prediction.add(-1);
        }
        List<Thread> threads = new ArrayList<Thread>();
        if(new_points.size() < n_jobs){
            for(int i = 0; i < new_points.size(); i++){
                Runnable rb = new AssignPointsRunnableMultiD(new_points, centerObjs, prediction, i, i+1);
                threads.add(new Thread(rb));
                threads.get(i).start();
            }
        } else {
            int thread_size = new_points.size() / n_jobs;
            int start;
            int end;

            for(int i = 0; i < n_jobs; i++){
                start = i*thread_size;
                if(i == n_jobs - 1){
                    end = new_points.size();
                } else {
                    end = (i+1)*thread_size;
                }
                Runnable rb = new AssignPointsRunnableMultiD(new_points, centerObjs, prediction, start, end);
                threads.add(new Thread(rb));
                threads.get(i).start();
            }
        }
        try {
            for(Thread thread: threads){
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return prediction;
    }
}

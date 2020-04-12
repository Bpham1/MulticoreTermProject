import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KMeans1D {
    private List<Integer> points;
    private List<Integer> prediction;
    private List<Double> centers;
    private List<Center1D> centerObjs;
    private int max_iter;
    private int n_clusters;
    private int n_jobs;

    public KMeans1D(int n_cluster, int n_jobs){
        this.max_iter = 300;
        this.n_clusters = n_cluster;
        this.n_jobs = n_jobs;
    }

    public KMeans1D(int n_cluster, int max_iter, int n_jobs){
        this.max_iter = max_iter;
        this.n_clusters = n_cluster;
        this.n_jobs = n_jobs;
    }

    public void fit(List<Integer> points) throws InterruptedException {
        this.points = points;
        this.prediction = new ArrayList<Integer>();
        this.centers = initCenters(n_clusters);
        this.centerObjs = createCenters(this.centers);

        for(int i = 0; i < points.size(); i++){
            prediction.add(0);
        }

        int num_iters = 0;
        while(num_iters < max_iter && UpdateCenters()){
            num_iters++;
        }
    }

    public List<Integer> predict(List<Integer> new_points) throws InterruptedException {
        prediction.clear();
        for(int i = 0; i < new_points.size(); i++){
            prediction.add(-1);
        }

        List<Thread> threads = new ArrayList<Thread>();
        int thread_size = new_points.size() / n_jobs;
        int start;
        int end;

        for(int i = 0; i < n_jobs; i++){
            start = i*thread_size;
            if(i == n_jobs - 1){
                end = points.size();
            } else {
                end = (i+1)*thread_size;
            }
            Runnable rb = new AssignPointsRunnable(points, centerObjs, prediction, start, end);
            threads.add(new Thread(rb));
            threads.get(i).start();
        }
        for(int i = 0; i < 4; i++){
            threads.get(i).join();
        }

        return prediction;
    }

    private List<Double> initCenters(int n_cluster){
        List<Integer> temp = new ArrayList<Integer>(points);
        List<Double> new_points = new ArrayList<Double>();
        Collections.shuffle(temp);
        // Can parallelize this
        for(int i = 0; i < n_cluster; i++){
            new_points.add((double) temp.get(i));
        }
        return new_points;
    }

    private List<Center1D> createCenters(List<Double> centers){
        List<Center1D> centerObjs = new ArrayList<Center1D>();
        for(Double center: centers){
            centerObjs.add(new Center1D(center));
        }
        return centerObjs;
    }

    private boolean UpdateCenters() throws InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        int thread_size = points.size() / n_jobs;
        int start;
        int end;

        for(int i = 0; i < n_jobs; i++){
            start = i*thread_size;
            if(i == n_jobs - 1){
                end = points.size();
            } else {
                end = (i+1)*thread_size;
            }
            Runnable rb = new AssignPointsRunnable(points, centerObjs, prediction, start, end);
            threads.add(new Thread(rb));
            threads.get(i).start();
        }
        for(int i = 0; i < 4; i++){
            threads.get(i).join();
        }

        ExecutorService executor = Executors.newFixedThreadPool(n_jobs);
        Future<Boolean>[] futures = new Future[n_jobs];
        thread_size = centers.size() / n_jobs;

        for(int i = 0; i < n_jobs; i++){
            start = i*thread_size;
            if(i == n_jobs - 1){
                end = centers.size();
            } else {
                end = (i+1)*thread_size;
            }
            Callable<Boolean> cb = new MoveCentersCallable(centers, centerObjs, start, end);
            futures[i] = executor.submit(cb);
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

    public List<Double> getCenters() {
        return centers;
    }
}

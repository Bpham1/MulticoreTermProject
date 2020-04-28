import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ParallelGaussianBayesClassifier {
    private List<List<Double>> inputList;
    private List<Integer> resultList;
    private int numJobs;
    private int numClasses;
    private int numSamples;
    private int numFeatures;
    private ArrayList<Integer> classes;
    private double means[][];
    private double[][] vars;
    private float[] priors;
    public ParallelGaussianBayesClassifier(int numJobs){
        this.numJobs = numJobs;
    }

    private class getClassArrayRunnable implements Runnable{
        private int startidx;
        private int chunksize;
        private List<List<Double>>[] classX;
        public getClassArrayRunnable(int thidx, int chunksize, List<List<Double>>[] classX){
            this.startidx = thidx * chunksize;
            this.chunksize = chunksize;
            this.classX = classX;
        }
        @Override
        public void run() { //O(1) time & O(N^2) work where N = num of rows in input
            for(int i = startidx; i < startidx + chunksize && i < resultList.size()*numClasses; i++){
                int sampleidx = i%numSamples;
                int classidx = i/numSamples; //thidx is modeled as indexing to a 2d array of [class][samples]
                if(resultList.get(sampleidx).equals(classes.get(classidx))){
                    synchronized (classX[classidx]){
                        classX[classidx].add(inputList.get(sampleidx));
                    }
                }
            }
        }
    }

    private class getMeansRunnable implements Runnable{
        private int startidx;
        private int chunksize;
        private final List<List<Double>>[] classX;
        public getMeansRunnable(int thidx, int chunksize, List<List<Double>>[] classX){
            this.startidx = thidx * chunksize;
            this.chunksize = chunksize;
            this.classX = classX;
        }
        @Override
        public void run() { //O(N) time and O(N*M) work where N = rows and M = columns
            for(int i =  startidx; i < startidx + chunksize && i < numFeatures*numClasses; i++) {
                int featureidx = i % numFeatures;
                int classidx = i / numFeatures; //thidx is modeled as indexing to a 2d array of [class][features]
                double mean = getMean(classX[classidx], featureidx);
                means[classidx][featureidx] = mean;
                vars[classidx][featureidx] = getVar(classX[classidx], featureidx, mean);
                if (featureidx == 0) {
                    priors[classidx] = (float) classX[classidx].size() / (float) numSamples;
                }
            }
        }
        private double getMean(List<List<Double>> x, int j){
            int count = 0;
            for( List<Double> l : x){
                count += l.get(j);
            }
            return ((double)count)/x.size();
        }
        private double getVar(List<List<Double>> x, int j, double avg){
            double sumDiffSquared = 0.0;
            for(List<Double> l : x){
                double diff = l.get(j) - avg;
                diff *= diff;
                sumDiffSquared += diff;
            }
            return sumDiffSquared / x.size();
        }
    }

    public void fit(List<List<Double>> X, List<Integer> Y){ //O(N^2) where N is the number of rows, but much faster in practice
        inputList = X;
        resultList = Y;
        numSamples = X.size();
        numFeatures = X.get(0).size();
        classes = new ArrayList<>(Y.stream().unordered().distinct().collect(Collectors.toList()));
        numClasses = classes.size();

        means = new double[numClasses][numFeatures];
        vars = new double[numClasses][numFeatures];
        priors = new float[numClasses];
        int calcidx = numClasses * numFeatures;
        int classArrayIdx = numClasses * numSamples;
        ExecutorService executor = Executors.newFixedThreadPool(numJobs);
        int sampleChunkSize;
        int indexChunkSize;
        int numTasks;
        ArrayList<List<Double>>[] classX = new ArrayList[numClasses]; //array of lists where the index corresponds to the index of the unique classifier in classes
        Arrays.setAll(classX, element -> new ArrayList<List<Double>>());
        List<Callable<Object>> classArrayTasks = new ArrayList<>();
        if (classArrayIdx < numJobs) {
            sampleChunkSize = 1;
            numTasks = classArrayIdx;
        } else {
            sampleChunkSize = (int)Math.ceil((double)classArrayIdx / numJobs);
            numTasks = numJobs;
        }
        for (int t = 0; t < numTasks; t++) { //O(N^2) where N = numRows, but in practice will usually be much faster
            classArrayTasks.add(Executors.callable(new getClassArrayRunnable(t, sampleChunkSize, classX)));
        }
        try {
            executor.invokeAll(classArrayTasks); //invokeAll waits for all threads to be finished, required bc the class array is needed to get the mean and variance
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Callable<Object>> calcMeanTasks = new ArrayList<>();
        if (calcidx < numJobs) {
            indexChunkSize = 1;
            numTasks = calcidx;
        } else {
            indexChunkSize = (int)Math.ceil((double)calcidx / numJobs);
            numTasks = numJobs;
        }
        for (int t = 0; t < numTasks; t++) { // O(N*M) at worst but should be much faster in practice
            calcMeanTasks.add(Executors.callable(new getMeansRunnable(t, indexChunkSize, classX)));
        }
        try {
            executor.invokeAll(calcMeanTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }


    public List<Integer> predict(List<List<Double>> X) { //same as fit
        List<Integer> predictions = new ArrayList<Integer>();
        double[][] posteriors = new double[X.size()][numClasses];
        int taskidx = X.size()*numClasses;
        int chunkSize;
        int numTasks;
        if(taskidx < numJobs){
            chunkSize = 1;
            numTasks = taskidx;
        }
        else{
            numTasks = numJobs;
            chunkSize = (int)Math.ceil((double)taskidx/numJobs);
        }
        ExecutorService executor = Executors.newFixedThreadPool(numJobs);
        List<Callable<Object>> getPDFTasks = new ArrayList<>();
        for(int i = 0; i < numTasks; i++){
            getPDFTasks.add(Executors.callable(new predictHelperRunnable(i, chunkSize, posteriors, X)));
        }
        try {
            executor.invokeAll(getPDFTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskidx = X.size();
        if(taskidx < numJobs){
            chunkSize = 1;
            numTasks = taskidx;
        }
        else{
            numTasks = numJobs;
            chunkSize = (int)Math.ceil((double)taskidx/numJobs);
        }
        List<Callable<Object>> getPredTasks = new ArrayList<>();
        int[] pred = new int[X.size()];
        for(int i = 0; i < numTasks; i++){
            getPredTasks.add(Executors.callable(new getPredRunnable(i, chunkSize, posteriors, pred)));
        }
        try {
            executor.invokeAll(getPredTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i : pred){
            predictions.add(i);
        }
        executor.shutdown();
        return predictions;
    }

    private class predictHelperRunnable implements Runnable{
        private double[][] posteriors;
        private int startid;
        private int chunkSize;
        private List<List<Double>> input;
        public predictHelperRunnable(int thid, int chunkSize, double[][] posteriors, List<List<Double>> X){
            this.posteriors = posteriors;
            this.startid = thid*chunkSize;
            this.chunkSize = chunkSize;
            this.input = X;
        }
        @Override
        public void run() { //O(M) time & O(N^2*M) work
            for(int i = startid; i < startid+chunkSize && i < input.size()*numClasses; i++) {
                int sampleidx = i%input.size();
                int classidx = i/input.size();
                List<Double> sample = input.get(sampleidx);
                double prior = Math.log(priors[classidx]);
                double conditional = 0;
                for(int j = 0; j < sample.size(); j++){
                    double pdf = getPDF(classidx, j, sample.get(j));
                    conditional += Math.log(pdf);
                }
                double posterior = prior + conditional;
                posteriors[sampleidx][classidx] = posterior;
            }
        }
        private double getPDF(int classIndex, int featureIndex, double x){
            double meanDiffSquared = x - means[classIndex][featureIndex];
            meanDiffSquared *= meanDiffSquared;
            double var = vars[classIndex][featureIndex];
            double numerator = Math.exp(-1 * (meanDiffSquared / (2*var)));
            double denominator = Math.sqrt(2 * Math.PI * var);
            return numerator/denominator;

        }
    }

    private class getPredRunnable implements Runnable{
        private double[][] posteriors;
        private int[] pred;
        private int startid;
        private int chunkSize;
        private List<List<Double>> input;
        public getPredRunnable(int thid, int chunkSize, double[][] posteriors, int[] pred){
            this.posteriors = posteriors;
            this.startid = thid*chunkSize;
            this.chunkSize = chunkSize;
            this.pred = pred;
        }
        @Override
        public void run() { //O(N) time (likely much faster in practice) and O(N^2) work
            for(int i = startid; i < startid+chunkSize && i < posteriors.length; i++) {
                int maxIndex = getMaxIndex(posteriors[i]);
                pred[i] = classes.get(maxIndex);
            }
        }
        private int getMaxIndex(double[] x){
            double max = Integer.MIN_VALUE;
            int maxIndex = 0;
            for(int i = 0; i < x.length; i++){
                if(max < x[i]){
                    max = x[i];
                    maxIndex = i;
                }
            }
            return maxIndex;
        }
    }
}

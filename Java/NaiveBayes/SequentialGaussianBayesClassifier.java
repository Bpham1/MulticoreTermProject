import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SequentialGaussianBayesClassifier {
        private int numClasses;
        private int numSamples;
        private int numFeatures;
        private ArrayList<Integer> classes;
        private double means[][];
        private double[][] vars;
        private float[] priors;

        public void fit(List<List<Double>> X, List<Integer> Y) { //O(N^2*M)
                numSamples = X.size();
                numFeatures = X.get(0).size();
                classes = new ArrayList<>(Y.stream().unordered().distinct().collect(Collectors.toList()));
                numClasses = classes.size();

                means = new double[numClasses][numFeatures];
                vars = new double[numClasses][numFeatures];
                priors = new float[numClasses];

                for( int i = 0; i < numClasses; i++){
                        List<List<Double>> classX = new ArrayList<List<Double>>();
                        for( int yIndex = 0; yIndex < numSamples; yIndex++){
                                if(Y.get(yIndex).equals(classes.get(i))){
                                        classX.add(X.get(yIndex));
                                }
                        }
                        for(int j = 0; j < numFeatures; j++){
                                means[i][j] = getMean(classX, j);
                                vars[i][j] = getVar(classX, j, means[i][j]);
                        }
                        priors[i] = (float)classX.size() / (float)numSamples;
                }


        }

        public List<Integer> predict(List<List<Double>> X) {//O(N^2*M)
                List<Integer> predictions = new ArrayList<Integer>();
                for(List<Double> x : X){
                        predictions.add(predictHelper(x));
                }
                return predictions;
        }

        private int predictHelper(List<Double> x){ //O(N*M)
                ArrayList<Double> posteriors = new ArrayList<Double>();
                for(int i = 0; i < numClasses; i++){
                        double prior = Math.log(priors[i]);
                        double conditional = 0;
                        for(int j = 0; j < x.size(); j++){
                                double pdf = getPDF(i, j, x.get(j));
                                conditional += Math.log(pdf);
                        }
                        double posterior = prior + conditional;
                        posteriors.add(posterior);
                }
                return classes.get(getMaxIndex(posteriors));
        }

        private double getPDF(int classIndex, int featureIndex, double x){
                double meanDiffSquared = x - means[classIndex][featureIndex];
                meanDiffSquared *= meanDiffSquared;
                double var = vars[classIndex][featureIndex];
                double numerator = Math.exp(-1 * (meanDiffSquared / (2*var)));
                double denominator = Math.sqrt(2 * Math.PI * var);
                return numerator/denominator;

        }

        private int getMaxIndex(List<Double> x){ // O(N) where n is size of x
                double max = Integer.MIN_VALUE;
                int maxIndex = 0;
                for(int i = 0; i < x.size(); i++){
                        if(max < x.get(i)){
                                max = x.get(i);
                                maxIndex = i;
                        }
                }
                return maxIndex;
        }

        private double getMean(List<List<Double>> x, int j){ //O(N) where N is numSamples
                int count = 0;
                for( List<Double> l : x){
                        count += l.get(j);
                }
                return ((double)count)/x.size();
        }
        private double getVar(List<List<Double>> x, int j, double avg){ //same as getMean
                double sumDiffSquared = 0.0;
                for(List<Double> l : x){
                        double diff = l.get(j) - avg;
                        diff *= diff;
                        sumDiffSquared += diff;
                }
                return sumDiffSquared / x.size();
        }

}

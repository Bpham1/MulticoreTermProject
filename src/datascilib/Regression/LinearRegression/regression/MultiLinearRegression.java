package datascilib.Regression.LinearRegression.regression;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import datascilib.Regression.LinearRegression.utilities.OptionalPrinter;
import datascilib.Regression.LinearRegression.utilities.ThreadInfo;

/**
 * <h1>MultiLinearRegression</h1>
 * Multi-Linear Regression is a functional extension of Linear Regression, where each dependent variable is 
 * predicted using multiple independent variables. E.g. for every observation, there are multiple factors in play
 * to create the observed phenomenon. The inputs should be a 1-dimensional {@linkplain Double} array {@link #y}
 * and a {@link #numObservations} by {@link #numFactors} {@linkplain Double} array {@link #x}.
 * As of 2020-04-26, it is implemented assuming non-correlative relationships between factors.
 *  </br></br>
 *  Use any of the 'fit' functions to calculate coefficients.
 *  As of 2020-04-26 only simple fit is implemented which summates results from {@link #numFactors}  1-dimensional
 *  LineaarRegression calculations in order to calculate coefficients {@link #coeffs} and uses a weighted average of 
 *  1-dimensional LinearRegression outputs to calculate the {@link #intercept}.
 * </br></br>
 * Some global values may be unused as other methods of multivariate linear regression are being added.
 * @author Neel Drain
 * @version 0.1
 * @since 2020-04-26
 */
public class MultiLinearRegression  {
	
	/**
	 * The {@linkplain Double} array which acts as the 1-dimensional set of dependent variables.
	 */
	private Double[] y;
	
	/**
	 *  A {@link AtomicReference} which acts as a reference to {@link #y}.
	 */
	@SuppressWarnings("unused")
	private AtomicReference<Double[]> yRef;
	
	/**
	 * The {@linkplain Double} 2-dimensional array which acts as the set of factors per observation.
	 */
	private Double[][] x;
	
	/**
	 * A {@link AtomicReference} which acts as a reference to {@link #x}.
	 */
	@SuppressWarnings("unused")
	private AtomicReference<Double[][]> xRef;
	
	/**
	 * The int value representing the number of observations in the overall dataset, equals {@link #y}'s length and {@link #x}'s number of rows.
	 */
	private int numObservations;
	
	/**
	 * The int value representing the number of factors per observation in input {@link #x}.
	 */
	private int numFactors;
	
	/**
	 * A {@linkplain Double} representing the calculated slope coefficients for the input dataset.
	 */
	private Double[] coeffs;
	
	/**
	 * A {@linkplain Double} representing the calculated intercept for the input dataset.
	 */
	private Double intercept;
	
	/**
	 * A {@linkplain Double} array representing the calculated intercepts for the 1-dimensional LinearRegression using a single factor.
	 */
	private Double[] intercepts;
	
	/**
	 * A {@linkplain Double} representing the calculates standard error values of 1-dimensional LinearRegression calculations for a single factor.
	 */
	private Double[] standardErrors;
	
	/**
	 * A {@linkplain Double} representing the calculated mean in {@link #y}.
	 */
	private Double yMean;
	
	/**
	 * The number of threads to use in multi-threaded implementations.
	 */
	private int threadCount;
	
	/**
	 * The number of data points to allocate each thread.
	 */
	private int dataPerThread;
	
	/**
	 * The number of data points to allocate to the last thread in addition to {@link #dataPerThread}.
	 */
	private int remainder;
	
	
	/**
	 * The System.out.println wrapper class which is used for multileveled printing.
	 */
	public OptionalPrinter op;

	/**
	 * Constructor to set inputs.
	 * @param y {@link #y}
	 * @param x {@link #x}
	 */
	public MultiLinearRegression(Double[][] x, Double[] y) {
		//Input Checks
				if(x == null) throw new NullPointerException("Input x was null");
				if(y == null) throw new NullPointerException("Input y was null");
				if(x.length == 0) throw new IllegalStateException("Input x is empty");
				if(y.length == 0) throw new IllegalStateException("Input y is empty");
				if(x.length != y.length) throw new IllegalStateException("Size of input x != size of input y");
				//Global Assignments
				this.x = x;
				this.xRef = new AtomicReference<Double[][]>(this.x);
				this.y = y;
				this.yRef = new AtomicReference<Double[]>(this.y);
				this.numFactors = x[0].length;
				this.numObservations = y.length;
				coeffs = new Double[numFactors];
				intercept = 0.0;
				intercepts = new Double[numFactors];
				yMean = Arrays.asList(y).stream().mapToDouble(val -> val).average().orElse(0.0);
				threadCount = ThreadInfo.threadCount(x.length);
				int[] threadData = ThreadInfo.dataPerThread(x.length, threadCount);
				dataPerThread = threadData[0];
				remainder = threadData[1];
				op = new OptionalPrinter(OptionalPrinter.HIGH_PRIORITY);
				standardErrors = new Double[numFactors];
	}
	
	/**
	 * Calculates {@link #coeffs}, {@link #intercepts}, and {@link #intercept} using multiple instances of {@link LinearRegression}
	 * to calculate each coefficient. The calculated standard errors are used as weightings to calculate {@link #intercept}.
	 * No arg {@link #simpleFit()} sequentially calls the parallel version of {@linkplain LinearRegression}'s simpleFit.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void simpleFit() throws InterruptedException, ExecutionException {
		//For each Observation-Factor pair, calculate the 1-Dimensional Linear regression coefficient for that factor and intercept.
		for(int factorIndex = 0; factorIndex < numFactors; factorIndex++) {
			//Create an x-input array for the 1-dimensional Linear Regression
			Double[] xIn = new Double[numObservations];
			for(int observationIndex = 0; observationIndex < numObservations; observationIndex++) {
				xIn[observationIndex] = x[observationIndex][factorIndex];
			}
			//Create an instance of LinearRegression
			op.print(OptionalPrinter.DEBUG, "xIn : " + Arrays.deepToString(xIn) + " y : " + Arrays.deepToString(y));
			LinearRegression lr = new LinearRegression(xIn,y);
			lr.op.setVerbosity(OptionalPrinter.NONE);
			//Fit using simpleRegression(true) which is a parallel LMS implementation
			lr.simpleFit(true);
			//Get coefficient (slope), and standard errors
			coeffs[factorIndex] = lr.getSlope();
			intercepts[factorIndex] = lr.getIntercept();
			standardErrors[factorIndex] = lr.calcualteStandardError();
			op.print(OptionalPrinter.DEBUG, "slope : " + lr.getSlope() + " intercept : " + lr.getIntercept() + " standardError : " + standardErrors[factorIndex]);
		}
		//Calculate intercept giving higher weights to values with higher error
		Double[] weightedIntercepts = new Double[numFactors];
		Double errorSum = Arrays.stream(standardErrors).mapToDouble(val -> val).parallel().sum();
		op.print(OptionalPrinter.DEBUG, "errorSum : " + errorSum);
		for(int index = 0; index < numFactors; index++) {
			weightedIntercepts[index] = intercepts[index] * ((errorSum == 0.0) ? 1 : (standardErrors[index] / errorSum));
		}
		intercept = Arrays.stream(weightedIntercepts).mapToDouble(val -> val).parallel().average().orElse(0.0);
	}
	
	
	//Getters
	
	/**
	 * Calculates the estimated (predicted) value given a {@linkplain Double} array of factors.
	 * @param xValues is the input array of factors, must be the same length as the length of factors MultiLinearRegression was constructed with.
	 * @return the estimated (predicted) value = sum ( coeff_i * xValue_i ) + intercept, where i ranges from 0 - the number of factors {@link #numFactors}.
	 */
	public Double getEstimatedValue(Double[] xValues) {
		if(x != null && y != null && x.length == 1 && y.length == 1){
			return y[0];
		}
		if(xValues == null) throw new NullPointerException("Input xValues is null, maybe you forgot to intialize it?");
		if(xValues.length != numFactors) throw new IllegalStateException("Input xValues length (" + xValues.length + ") must be " + numFactors + " long.");
		double estimatedValue = 0.0;
		for(int index = 0; index < numFactors; index++) {
			estimatedValue += coeffs[index] * xValues[index];
		}
		estimatedValue += intercept;
		return estimatedValue;
	}
	
	/**
	 * Calculated the standard (average) error between the predicted and observed data.
	 * Since the standard error calculation comprises simple 2-step sub-calculations parallelerization time would be equivalent to sequential time.
	 * Therefore this method is fully sequential.
	 * @return the standard error of the prediction.
	 */
	public Double calcualteStandardError() {
		double standardError = 0.0;
		double[] errors = new double[numObservations];
		for(int estimationIndex = 0; estimationIndex < x.length; estimationIndex++) {
			//Simple difference error TODO use different error calculation types especially those that can normalize 
			errors[estimationIndex] = Math.abs(y[estimationIndex] - getEstimatedValue(x[estimationIndex]));
		}
		double sum = Arrays.stream(errors).parallel().sum();
		standardError = sum / numObservations;
		return standardError;
	}
	
	/**
	 * Getter for {@linkplain #y}
	 * @return the value of {@link #y} as a {@link Double} array.
	 */
	public Double[] getY() {
		return y;
	}

	/**
	 * Getter for {@linkplain #x}
	 * @return the value of {@link #x} as a {@link Double} 2-dimensional array.
	 */
	public Double[][] getX() {
		return x;
	}

	/**
	 * Getter for {@linkplain #numObservations}
	 * @return the value of {@link #numObservations} as an int
	 */
	public int getNumObservations() {
		return numObservations;
	}

	/**
	 * Getter for {@linkplain #numFactors}
	 * @return the value of {@link #numFactors} as an int
	 */
	public int getNumFactors() {
		return numFactors;
	}

	/**
	 * Getter for {@linkplain #coeffs}
	 * @return {@link #coeffs} as a {@link Double} array
	 */
	public Double[] getCoeffs() {
		return coeffs;
	}

	/**
	 * Getter for {@linkplain #intercept}
	 * @return the value of {@link #intercept} as a {@link Double}, , possible to return null.
	 */
	public Double getIntercept() {
		return intercept;
	}

	/**
	 * Getter for {@linkplain #yMean}
	 * @return the value of {@link #yMean} as a {@link Double}.
	 */
	public Double getyMean() {
		return yMean;
	}

	/**
	 * Getter for {@linkplain #dataPerThread}
	 * @return the value of {@link #dataPerThread} as an int
	 */
	public int getDataPerThread() {
		return dataPerThread;
	}

	/**
	 * Getter for {@linkplain #remainder}
	 * @return the value of {@link #remainder} as an int
	 */
	public int getRemainder() {
		return remainder;
	}

	/**
	 * Getter for {@linkplain #op}
	 * @return the {@link OptionalPrinter} {@link #op}.
	 */
	public OptionalPrinter getOptionalPrinter() {
		return op;
	}

	/**
	 * Getter for {@linkplain #standardErrors}
	 * @return {@link #standardErrors} as a {@link Double} array/
	 */
	public Double[] getStandardErrors() {
		return standardErrors;
	}
	
	/**
	 * Getter for {@link #intercepts}
	 * @return {@link #intercepts}
	 */
	public Double[] getIntercepts() {
		return intercepts;
	}
	
}

package datascilib.Regression.LinearRegression.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import datascilib.Regression.LinearRegression.utilities.ThreadInfo;
import datascilib.Regression.LinearRegression.utilities.OptionalPrinter;

/**
 * <h1> Linear Regression </h1>
 * Linear Regression implements a sequential and parallel LMS (Least Mean Squared) algorithm to calculate the slope.
 * The intercept can be calculated via sequential LMS, sequential Gradient Descent, or a custom parallel Stochastic Gradient Descent.
 * </br></br>
 * <h4> Usage </h4>
*Import datasets x values and y values as Double[] : Double[] x,y
*</br>
*Create a new LinearRegression class : LinearRegression lr = new LinearRegression(x,y)
*</br>
*Choose a 'fit option' : simpleFit() calculates  using sequential LMS for slope and intercept
*</br>
*			simpleFit(false) same as simpleFit()
*</br>
*			simpleFit(true) calculates using parallel LMS for slope, sequential LMS for intercept
*</br>
*			gradientFit(false) calculates using sequential LMS for slope, sequential Gradient Descent for intercept
*</br>
*			gradientFit(true) calculates using parallel LMS for slope, parallel GradientDescent for intercept
*</br>
*Get slope : getSlope() returns Double
*</br>
*Get intercept : getIntercept() returns Double
*</br>
*Get estimated y value at user-specified x value : getEstimatedValue(xValue) returns Double
* 
* @author Neel Drain
* @version 0.2
* @since 2019-04-20
*/ 
public class LinearRegression {

	/**
	 * A {@link java.lang.Double} which acts as the set of independent variables.
	 * @see #getX() for the getter.
	 */
	private Double[] x;
	
	/**
	 * A {@link java.util.concurrent.atomic.AtomicReference} which acts as a reference to {@link #x}.
	 */
	private AtomicReference<Double[]> xRef;
	
	/**
	 * A {@link java.lang.Double} which acts as the set of dependent variables.
	 * @see #getY() for the getter.
	 */
	private Double[] y;
	
	/**
	 *  A {@link java.util.concurrent.atomic.AtomicReference} which acts as a reference to {@link #y}.
	 */
	private AtomicReference<Double[]> yRef;
	
	/**
	 * A {@link java.lang.Double} which holds the value of the calculated intercept.
	 * @see #getIntercept for the getter.
	 **/
	private Double intercept;
	
	/**
	 * A {@link java.lang.Double} which holds the value of the calculated slope.
	 * @see #getSlope() for the getter.
	 */
	private Double slope;
	
	/**
	 * A {@link java.lang.Double} which holds the mean value of {@link #x}.
	 * @see #getxMean() for the getter.
	 */
	private Double xMean;
	
	/**
	 * A {@link java.lang.Double} which holds the mean value of {@link #y}.
	 * @see #getyMean() for the getter.
	 */
	private Double yMean;
	
	/**
	 * An int which holds the thread count parallel methods use.
	 * @see #getThreadCount() for the getter.
	 */
	private int threadCount;
	
	/**
	 * A int which holds the amount of data per thread.
	 * @see #getDataPerThread() for the getter.
	 */
	private int dataPerThread;
	
	/**
	 * An int which holds the amount of remainder data to use in the last thread.
	 * @see #getRemainder() for the getter.
	 */
	private int remainder;
	
	/**
	 * An int which sets the amount of amount of max. gradient descent iterations.
	 */
	public int MAX_DESCENT_ITERATIONS = 5000;
	
	/**
	 * A double which sets the initial intercept value for use in gradient descent;
	 */
	public double INITIAL_INTERCEPT = 0.0;
	
	/**
	 * A double which sets the allowable precision of gradient descent results.
	 */
	public double PRESICION = 0.001;
	
	/**
	 * An int which sets the number of sub-estimates in the parallel custom Stochastic Gradient Descent algorithm.
	 */
	public int NUM_SUB_ESTIMATES = 1;
	
	/**
	 * The OptionalPrinter which allows for controlling what messages print via a determined VERBOSITY level.
	 */
	public OptionalPrinter op;
	
	/**
	 * The main constructor. Takes in a pair of {@link java.lang.Double} arrays.
	 * The empty constructor type is not allowed.
	 * 
	 * Checks for: nullity, empty arrays, and that the inputs are of the same length.
	 * 
	 * @param x is the input array of independent variables.
	 * @param y is the input array of dependent variables.
	 */
	public LinearRegression(Double[] x, Double[] y) throws NullPointerException, IllegalStateException{
		//Input Checks
		if(x == null) throw new NullPointerException("Input x was null");
		if(y == null) throw new NullPointerException("Input y was null");
		if(x.length == 0) throw new IllegalStateException("Input x is empty");
		if(y.length == 0) throw new IllegalStateException("Input y is empty");
		if(x.length != y.length) throw new IllegalStateException("Size of input x != size of input y");
		//Global Assignments
		this.x = x;
		this.xRef = new AtomicReference<Double[]>(this.x);
		this.y = y;
		this.yRef = new AtomicReference<Double[]>(this.y);
		slope = 0.0;
		intercept = 0.0;
		xMean = Arrays.asList(x).stream().mapToDouble(val -> val).average().orElse(0.0);
		yMean = Arrays.asList(y).stream().mapToDouble(val -> val).average().orElse(0.0);
		threadCount = ThreadInfo.threadCount(x.length);
		int[] threadData = ThreadInfo.dataPerThread(x.length, threadCount);
		dataPerThread = threadData[0];
		remainder = threadData[1];
		op = new OptionalPrinter(OptionalPrinter.NONE);
	}
	
	/**
	 * Calculates {@link #slope} and {@link #intercept} using sequential Least Mean Squared (LMS)
	 * for both calculations
	 * @return true if the calculation succeeded 
	 * @throws InterruptedException
	 */
	public boolean simpleFit() throws InterruptedException {
		sequentialSlopeEstimator();
		simpleInterceptEstimator();
		return true;
	}
	
	/**
	 * Calculates the {@link #slope} and {@link #intercept} using either sequential or parallel Least Mean Squared (LMS)
	 * based upon the value of the parameter parallel. If parameter parallel is true, then the {@link #slope} is calculated using
	 * {@link #parallelSlopeEstimator()} else uses {@link #sequentialSlopeEstimator()}. The {@link #intercept }is always calculated using
	 * {@link #simpleInterceptEstimator()}.
	 * @param parallel determines whether to use parallel LMS or not
	 * @return true if the calculation succeeded
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public boolean simpleFit(boolean parallel) throws InterruptedException, ExecutionException {
		if(!parallel) sequentialSlopeEstimator();
		else parallelSlopeEstimator();
		simpleInterceptEstimator();
		return true;
	} 
	
	/**
	 * Calculates the {@link #slope} and {@link #intercept} using either sequential or parallel Least Mean Squared (LMS)
	 * coupled with either a sequential or parallel custom Gradient Descent algorithm. If parameter parallel is true, then the {@link #slope}
	 * is calculated using {@link #sequentialSlopeEstimator()} and the {@link #intercept} is calculated using {@link #sequentialGradientInterceptEstimator()}.
	 * If the parameter parallel is false, the {@link #slope} is calculated using {@link #parallelSlopeEstimator()} and the {@link #intercept} is calculated
	 * using {@link #parallelGradientInterceptEstimator()}.
	 * @param parallel determines whether to use parallel LMS and Gradient Descent or not
	 * @return true if the calculation succeeded
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public boolean gradientFit(boolean parallel) throws InterruptedException, ExecutionException {
		if(!parallel) sequentialSlopeEstimator();
		else parallelSlopeEstimator();
		if(!parallel) sequentialGradientInterceptEstimator();
		else parallelGradientInterceptEstimator();
		return true;
	}
	
	/**
	 * Calculates the {@link #slope} and {@link #intercept} using a parallel batch Least Mean Squared (LMS) calculation.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void parallelSlopeEstimator() throws InterruptedException, ExecutionException {
		op.print(OptionalPrinter.HIGH_PRIORITY,"Running parallel slope estimator");
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		List<Future<Double[]>> futures = new ArrayList<Future<Double[]>>();
		for(int i = 0; i < threadCount; i++) {
			int start = i * (x.length/threadCount);
			int end = (i+1) * (x.length/threadCount) - 1;
			if(i == threadCount -1 && remainder != x.length) end += remainder;
			slopeThread t = new slopeThread(start, end, x, y, xMean, yMean);
			Future<Double[]> sums = pool.submit(t);
			futures.add(sums);
		}
		double numerator = 0;
		double denominator = 0;
		op.print(OptionalPrinter.HIGH_PRIORITY,"slopeThreads finished execution");
		for(Future<Double[]> sums : futures) {
			Double[] sum = sums.get();
			numerator += sum[0];
			denominator += sum[1];
		}
		pool.shutdown();
		slope = numerator / denominator;
	}
	
	/**
	 * Calculates the {@link #slope} sequentially using Least Mean Squared (LMS)
	 * </br></br>
	 * numerator = sum( ({@linkplain #x}[index] - {@linkplain #xMean}) * ({@linkplain #y}[index] - {@linkplain #yMean}))
	 * </br>
	 * denominator = sum( ({@linkplain #x}[index] - {@linkplain #xMean})^2 )
	 * </br>
	 * {@linkplain #slope} = numerator / denominator
	 */
	protected void sequentialSlopeEstimator() {
		op.print(OptionalPrinter.HIGH_PRIORITY, "Running sequential slope estimator using LMS");
		Double numerator = 0.0;
		for(int index = 0; index < x.length; index++) {
			Double xError = (x[index] - xMean);
			Double yError = y[index] - yMean;
			numerator += xError * yError;
		}
		Double denominator = 0.0;
		for(int index = 0; index < x.length; index++) {
			Double xError = x[index] - xMean;
			denominator += xError * xError;
		}
		slope = numerator / denominator;
	}
	
	/**
	 * Calculates {@link #intercept} using a custom version of gradient descent with parallel derivative sum squared residual calcualtions.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void parallelGradientInterceptEstimator() throws InterruptedException, ExecutionException {
		op.print(OptionalPrinter.HIGH_PRIORITY,"Running parallel gradient descent estimator");
		double learningRate = 0.001;
		double stepSize = 0;
		double gIntercept = INITIAL_INTERCEPT;
		double gSlope = Double.MAX_VALUE;
		int iteration  = 0;
		//Loop for MAX_ITERATIONS or until gSlope is 0.0 +- PRESICION
		while(iteration < MAX_DESCENT_ITERATIONS && (gSlope <= PRESICION || gSlope >= -PRESICION)) {
			//Loop through each sub estimate
			for(int estimate = 0; estimate < NUM_SUB_ESTIMATES; estimate++) {
				Double modGIntercept = gIntercept - stepSize * (estimate + 1);
				double threadSlope = parallelSumSquareResidual(modGIntercept);
				gIntercept = (Math.abs(threadSlope) < Math.abs(gSlope)) ? modGIntercept : gIntercept;
				gSlope = (Math.abs(threadSlope) < Math.abs(gSlope)) ? threadSlope : gSlope;
			}
			stepSize = gSlope * learningRate;
			gIntercept -= stepSize;
			iteration += 1;
			op.print(OptionalPrinter.DEBUG,"gSlope : " + gSlope + " gIntercept: " + gIntercept);
		}
		intercept = gIntercept;
	}
	
	/**
	 * Calculates the derivative of sum square residuals in parallel by breaking the data into {@linkplain #threadCount} sections
	 * with {@linkplain #dataPerThread} data points per section except for the highest numbered thread which contains {@linkplain #dataPerThread} + {@linkplain #remainder} data points.
	 * @param gIntercept is the intercept calculated by {@link #parallelGradientInterceptEstimator()}.
	 * @return the derivative of sum squared residuals
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private double parallelSumSquareResidual(Double gIntercept) throws InterruptedException, ExecutionException {
		double residualSum = 0.0;
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		List<Future<Double>> sums = new ArrayList<Future<Double>>();
		for(int i = 0; i < threadCount; i++) {
			int start = i * (x.length/threadCount);
			int end = (i+1) * (x.length/threadCount) - 1;
			if(i == threadCount -1 && remainder != x.length) end += remainder;
			sumSquareResidualsThread t = new sumSquareResidualsThread(start,end,slope,gIntercept,x,y);
			Future<Double> sum = pool.submit(t);
			sums.add(sum);
		}
		for(Future<Double> sum : sums) {
			residualSum += sum.get();
		}
		pool.shutdown();
		return residualSum;
	}
	
	/**
	 * Calculates the {@link #intercept} sequentially using Least Mean Squared (LMS)
	 * </br></br>
	 * {@linkplain #intercept} = {@linkplain #yMean} - {@linkplain #slope} * {@linkplain #xMean}
	 */
	private void simpleInterceptEstimator() {
		intercept = yMean - slope * xMean;
	}
	
	/**
	 * Calculates the {@link #slope} using sequential gradient descent with
	 * an initial intercept value of {@link #INITIAL_INTERCEPT}
	 */
	private void sequentialGradientInterceptEstimator() {
		op.print(OptionalPrinter.HIGH_PRIORITY, "Running sequential gradient descent estimator");
		double learningRate = 0.001;
		double stepSize = 0;
		double gIntercept = INITIAL_INTERCEPT;
		double gSlope = Double.MAX_VALUE;
		int iteration = 0;
		while(iteration < MAX_DESCENT_ITERATIONS && (gSlope <= PRESICION || gSlope >= -PRESICION)) {
			gSlope = sequentialSquareResidualSum(gIntercept);
			stepSize = gSlope * learningRate;
			gIntercept = gIntercept - stepSize;
			iteration += 1;
		}
		intercept = gIntercept;
	}
	
	/**
	 * Calculates the derivative sum squared residuals sequentially
	 * @param gIntercept is the intercept chosen by {@link #sequentialGradientInterceptEstimator()}
	 * @return the derivative of the sum of square residuals
	 */
	private double sequentialSquareResidualSum(double gIntercept) {
		double sum = 0;
		for(int index = 0; index < x.length; index++) {
			sum += -2*(y[index] - (gIntercept + slope * x[index]));
		}
		return sum;
	}
	
	/**
	 * Calculated the standard (average) error between the predicted and observed data.
	 * Since the standard error calculation comprises simple 2-step sub-calculations parallelerization time would be equivalent to sequential time.
	 * Therefore this method is fully sequential.
	 * @return the standard error of the prediction.
	 */
	public Double calcualteStandardError() {
		Double standardError = 0.0;
		double[] errors = new double[x.length];
		for(int estimationIndex = 0; estimationIndex < x.length; estimationIndex++) {
			//Simple difference error TODO use different error calculation types especially those that can normalize 
			errors[estimationIndex] = Math.abs(y[estimationIndex] - getEstimatedValue(x[estimationIndex]));
		}
		double sum = Arrays.stream(errors).parallel().sum();
		standardError = sum / x.length;
		return standardError;
	}
	
	/**
	 * Gets the estimated value of data at xValue using the previously calculated {@link #slope} and {@link #intercept}.
	 * @param xValue is the value to estimate at
	 * @return the estimated value (y-value), null if one or neither has been calculated yet.
	 */
	public Double getEstimatedValue(Double xValue) {
		if(intercept == null || slope == null) return null;
		return intercept + xValue * slope;
	}
	
	/**
	 * Getter for {@linkplain #x}
	 * @return the value of {@link #x} as a {@link java.lang.Double} array.
	 */
	public Double[] getX() {
		return x;
	}

	/**
	 * Getter for {@linkplain #y}
	 * @return the value of {@link #y} as a {@link java.lang.Double} array.
	 */
	public Double[] getY() {
		return y;
	}

	/**
	 * Getter for {@linkplain #intercept}
	 * @return the value of {@link #intercept} as a {@link java.lang.Double}, , possible to return null.
	 */
	public Double getIntercept() {
		return intercept;
	}

	/**
	 * Getter for {@linkplain #slope}
	 * @return the value of {@link #slope} as a {@link java.lang.Double}, possible to return null.
	 */
	public Double getSlope() {
		return slope;
	}

	/**
	 * Getter for {@linkplain #xMean}
	 * @return the value of {@link #xMean} as a {@link java.lang.Double}.
	 */
	public Double getxMean() {
		return xMean;
	}

	/**
	 * Getter for {@linkplain #yMean}
	 * @return the value of {@link #yMean} as a {@link java.lang.Double}.
	 */
	public Double getyMean() {
		return yMean;
	}

	/**
	 * Getter for {@linkplain #threadCount}
	 * @return the value of {@link #threadCount} as an int
	 */
	public int getThreadCount() {
		return threadCount;
	}
	
	/**
	 * Getter for {@linkplain #dataPerThread}
	 * @return the value of {@link #dataPerThread} as an int
	 */
	public int getDataPerThread() {
		return dataPerThread;
	}

	/**
	 * getter for {@linkplain #remainder}
	 * @return the value of {@link #remainder} as an int
	 */
	public int getRemainder() {
		return remainder;
	}
}

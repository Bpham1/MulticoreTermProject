package optimized.regression;

import java.util.Arrays;
import optimized.utils.OptionalPrinter;

/**
 * <h1> Linear Regression </h1>
 * Linear Regression implements a sequential and parallel LMS (Least Mean Squared) algorithm to calculate the slope.
 * The intercept can be calculated via sequential LMS, sequential Gradient Descent, or a custom parallel Stochastic Gradient Descent.
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
	 * A {@link java.lang.Double} which acts as the set of dependent variables.
	 * @see #getY() for the getter.
	 */
	private Double[] y;
	
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
	private OptionalPrinter op;
	
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
		this.y = y;
		slope = new Double(0.0);
		intercept = new Double(0.0);
		xMean = Arrays.asList(x).stream().mapToDouble(val -> val).average().orElse(0.0);
		yMean = Arrays.asList(y).stream().mapToDouble(val -> val).average().orElse(0.0);
		threadCount = optimized.threadutils.ThreadInfo.threadCount(x.length);
		int[] threadData = optimized.threadutils.ThreadInfo.dataPerThread(x.length, threadCount);
		dataPerThread = threadData[0];
		remainder = threadData[1];
		op = new OptionalPrinter(OptionalPrinter.HIGH_PRIORITY);
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
		Double numerator = new Double(0.0);
		for(int index = 0; index < x.length; index++) {
			Double xError = (x[index] - xMean);
			Double yError = y[index] - yMean;
			numerator += xError * yError;
		}
		Double denominator = new Double(0.0);
		for(int index = 0; index < x.length; index++) {
			Double xError = x[index] - xMean;
			denominator += xError * xError;
		}
		slope = numerator / denominator;
	}
	
	//TODO finish adding methods with JavaDoc comments
	
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
	 * @return the value of {@link #intercept} as a {@link java.lang.Double}.
	 */
	public Double getIntercept() {
		return intercept;
	}

	/**
	 * Getter for {@linkplain #slope}
	 * @return the value of {@link #slope} as a {@link java.lang.Double}.
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

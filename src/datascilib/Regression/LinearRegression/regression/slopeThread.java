package datascilib.Regression.LinearRegression.regression;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <h1>slopeThread</h1>
 * Calculates the slope using LMS of a partition of datasets {@link #x} and {@link #y}
 * @author Neel Drain
 * @version 0.2
 * @since 2019-04-20
 */
public class slopeThread implements Callable<Double[]>{
	
	/**
	 * The start of the partition to calculate with.
	 */
	int start;
	
	/**
	 * The end of the partition to calculate with.
	 */
	int end;
	
	/**
	 * The {@link Double} representing the mean of referenced {@link #x}.
	 */
	Double xMean;
	
	/**
	 * The {@link Double} representing the mean of references {@link #y}.
	 */
	Double yMean;
	
	/**
	 * The {@link AtomicReference} of {@link Double} dataset x.
	 */
	Double[] x;
	
	/**
	 * The {@link AtomicReference} of {@link Double} dataset y.
	 */
	Double[] y;

	/**
	 * The {@link AtomicReference} to the {@link CountDownLatch} countDownLatch which allows the main
	 * thread to wait for each subthread of type slopeThread to finish execution.
	 */
	AtomicReference<CountDownLatch> countDownLatch;
	
	/**
	 * The constructor of slopeThread.
	 * @param start @see {@link #start}
	 * @param end @see {@link #end}
	 * @param x @see {@link #x}
	 * @param y @see {@link #y}
	 * @param xMean @see {@link #xMean}
	 * @param yMean @see {@link #yMean}
	 */
	public slopeThread(int start, int end,
			Double[] x, Double[] y,
			Double xMean, Double yMean) {
		this.start = start;
		this.end =  end;
		this.xMean = xMean;
		this.yMean = yMean;
		this.x = x;
		this.y = y;
	}

	/**
	 * The LMS calculation
	 * @return the sums of the numerator and denominator as a double array.
	 */
	@Override
	public Double[] call() throws Exception {
		Double[] sums = new Double[2]; 
		sums[0] = (double) 0; //numerator
		sums[1] = (double) 0; //denominator
		for(int index = start; index <= end; index++) {
			double xError = x[index] - xMean;
			double yError = y[index] - yMean;
			double numErrorMultiplication = xError * yError;
			double denomErrorMultiplication = xError * xError;
			sums[0] += numErrorMultiplication;
			sums[1] += denomErrorMultiplication;
		}
		return sums;
	}

}

package optimized.regression;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <h1>sumSquareResidualsThread</h1>
 * Calculates the derivative of the sum squared residuals for a partition of the dataset {@link #x} and {@link #y}.
 * @author Neel Drain
 * @version 0.2
 * @since 2019-04-20
 */
public class sumSquareResidualsThread implements Callable<Double>{
	
	/**
	 * The start of the partition to calculate with.
	 */
	int start;
	
	/**
	 * The end of the partition to calculate with.
	 */
	int end;
	
	/**
	 * The {@link java.lang.Double} representing the previously calculated slope/gSlope.
	 */
	Double slope;
	
	/**
	 * The double representing the intercept to test on.
	 */
	double gIntercept;
	
	/**
	 * The {@link java.util.concurrent.atomic.AtomicReferenec} of {@link java.lang.Double} dataset x.
	 */
	AtomicReference<Double[]> x;
	
	/**
	 * The {@link java.util.concurrent.atomic.AtomicReferenec} of {@link java.lang.Double} dataset y.
	 */
	AtomicReference<Double[]> y;
	
	/**
	 * The {@link java.util.concurrent.atomic.AtomicReferenec} to the {@link java.util.concurrent.CountDownLatch} countDownLatch which allows the main
	 * thread to wait for each subthread of type slopeThread to finish execution. 
	 */
	AtomicReference<CountDownLatch> countDownLatch;
	
	/**
	 * The constructor of sumSquareResidualsThread.
	 * @param start @see {@link #start}
	 * @param end @see {@link #end}
	 * @param slope @see {@link #slope}
	 * @param gIntercept @see {@link #gIntercept}
	 * @param x @see {@link #x}
	 * @param y @see {@link #y}
	 * @param countDownLatch @see {@link #countDownLatch}
	 */
	public sumSquareResidualsThread(
			int start, int end,
			Double slope,
			double gIntercept,
			AtomicReference<Double[]> x, AtomicReference<Double[]> y,
			AtomicReference<CountDownLatch> countDownLatch) {
		this.start = start;
		this.end = end;
		this.slope = slope;
		this.gIntercept = gIntercept;
		this.x = x;
		this.y = y;
		this.countDownLatch = countDownLatch; 
	}

	/**
	 * Calculates the derivative of sum squared residuals for the specified {@link #start} - {@link #end} partition of data.
	 * @return the sum
	 */
	@Override
	public Double call() throws Exception {
		Double sum = 0.0;
		for(int index = start; index <= end; index++) {
			sum += -2*(y.get()[index] - (gIntercept + slope * x.get()[index]));
		}
		countDownLatch.get().countDown();
		return sum;
	}

}

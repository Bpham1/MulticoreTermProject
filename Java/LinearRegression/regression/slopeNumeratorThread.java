package regression;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class slopeNumeratorThread implements Runnable{
	int start, end;
	Double xMean, yMean;
	AtomicReference<AtomicLong> numeratorSum;
	AtomicReference<Double[]> x,y;
	AtomicReference<CountDownLatch> countDownLatch;
	
	public slopeNumeratorThread(int start, int end, 
			AtomicReference<Double[]> x, AtomicReference<Double[]> y,
			AtomicReference<AtomicLong> numeratorSum, 
			Double xMean, Double yMean,
			AtomicReference<CountDownLatch> countDownLatch) {
		this.start = start;
		this.end =  end;
		this.numeratorSum = numeratorSum;
		this.xMean = xMean;
		this.yMean = yMean;
		this.x = x;
		this.y = y;
		this.countDownLatch = countDownLatch;
	}
	
	@Override
	public void run() {
		for(int index = start; index < end; index++) {
			Double xError = x.get()[index] - xMean;
			Double yError = y.get()[index] - yMean;
			long errorMultiplication = (long) (xError * yError);
			numeratorSum.get().addAndGet(errorMultiplication);
		}
		countDownLatch.get().countDown();
	} 

}

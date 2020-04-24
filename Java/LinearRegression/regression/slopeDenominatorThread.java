package regression;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class slopeDenominatorThread implements Runnable{
	int start, end;
	Double xMean;
	AtomicReference<Double[]> x;
	AtomicReference<AtomicLong> denominatorSum;
	AtomicReference<CountDownLatch> countDownLatch;
	
	public slopeDenominatorThread(int start, int end,
			Double xMean,
			AtomicReference<Double[]> x,
			AtomicReference<AtomicLong> denominatorSum,
			AtomicReference<CountDownLatch> countDownLatch) {
		this.xMean = xMean;
		this.start = start;
		this.end = end;
		this.x = x;
		this.denominatorSum = denominatorSum;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		for(int index = start; index < end; index++) {
			long xErrorSquared = (long) ((x.get()[index] - xMean) * (x.get()[index] - xMean));
			denominatorSum.get().addAndGet(xErrorSquared);
		}
		countDownLatch.get().countDown();
	}

}

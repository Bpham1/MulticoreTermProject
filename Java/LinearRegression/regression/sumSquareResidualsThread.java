package regression;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class sumSquareResidualsThread implements Callable<Double>{
	int start, end;
	Double slope;
	double gIntercept;
	AtomicReference<Double[]> x,y;
	AtomicReference<CountDownLatch> countDownLatch;

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

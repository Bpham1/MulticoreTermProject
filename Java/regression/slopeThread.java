package regression;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class slopeThread implements Callable<Double[]>{
	int start, end;
	Double xMean, yMean;
	AtomicReference<Double[]> x,y;
	AtomicReference<CountDownLatch> countDownLatch;
	
	public slopeThread(int start, int end,
			AtomicReference<Double[]> x, AtomicReference<Double[]> y,
			Double xMean, Double yMean,
			AtomicReference<CountDownLatch> countDownLatch) {
		this.start = start;
		this.end =  end;
		this.xMean = xMean;
		this.yMean = yMean;
		this.x = x;
		this.y = y;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public Double[] call() throws Exception {
		Double[] sums = new Double[2];
		sums[0] = new Double(0); //numerator
		sums[1] = new Double(0); //denominator
		for(int index = start; index <= end; index++) {
			double xError = x.get()[index] - xMean;
			double yError = y.get()[index] - yMean;
			double numErrorMultiplication = xError * yError;
			double denomErrorMultiplication = xError * xError;
			sums[0] += numErrorMultiplication;
			sums[1] += denomErrorMultiplication;
		}
		countDownLatch.get().countDown();
		return sums;
	}

}

package regression;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class sumSquareResidualsThread implements Runnable{
	int start, end;
	Double slope;
	double gIntercept;
	AtomicReference<Double[]> x,y;
	AtomicReference<AtomicLong> sumSquareResidual;

	public sumSquareResidualsThread(
			int start, int end,
			Double slope,
			double gIntercept,
			AtomicReference<Double[]> x, AtomicReference<Double[]> y,
			AtomicReference<AtomicLong> sumSquareResidual) {
		this.start = start;
		this.end = end;
		this.slope = slope;
		this.gIntercept = gIntercept;
		this.x = x;
		this.y = y;
		this.sumSquareResidual = sumSquareResidual;
	}
	
	@Override
	public void run() {
		for(int index = start; index < end; index++) {
			double sum = -2*(y.get()[index] - (gIntercept + slope * x.get()[index]));
			sumSquareResidual.get().addAndGet((long) sum);
		}
	}

}

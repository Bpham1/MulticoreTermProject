package regression;

import java.util.ArrayList;

public class LinearRegression {
	
	//Global Input Variables
	ArrayList<Double> x,y;
	//Global Output Variables
	Double intercept;
	Double slope;
	//Global Calculation Variables
	Double xMean, yMean;
	
	
	public LinearRegression(ArrayList<Double> x, ArrayList<Double> y) {
		this.x = x;
		this.y = y;
		intercept = 0.0;
		slope = 0.0;
		xMean = x.parallelStream().mapToDouble(val -> val).average().orElse(0.0);
		yMean = y.parallelStream().mapToDouble(val -> val).average().orElse(0.0);
	}
	
	public boolean fit(int numJobs) {
		slopeEstimator(numJobs);
		interceptEstimator();
		return true;
	}
	
	class SlopeEstimationThread implements Runnable {
		
		int start, end;
		
		public SlopeEstimationThread(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public void run() {
			//Calculate temporary array ranges
			
			//Calculate error for each x-value
			
			//Calculate error for each y-value
			
			//Calculate individual numerators
			
			//Add to atomic numerator sum 
			
			//Increment numeratorCompletion count
			
			//Calculate individual denominators
			
			//Add to denominator sum
		}
		
	}
	
	private void slopeEstimator(int numJobs) {
		//Calculate the x,y ranges for each thread
		
		//Init. the threads
		
		//Execute the threads
		
		//Wait for CountDownLatch
	}
	
	private void interceptEstimator() {
		intercept = yMean - slope * xMean;
	}
}

package regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import utilities.MultithreadUtilities;

public class LinearRegression {
	
	//Global Input Variables
	Double[] x,y;
	AtomicReference<Double[]> xRef, yRef;
	//Global Output Variables
	Double intercept;
	Double slope;
	//Global Calculation Variables
	Double xMean, yMean;
	int dataSize;
	int threadCount;
	int threadData;
	int remainder;
	final int MAX_DESCENT_ITERATIONS = 5000;
	final double INITIAL_INTERCEPT = 0.0;
	final double PRESICION = 0.001;
	final int NUM_SUB_ESTIMATES = 1;
	
	public LinearRegression(Double[] x, Double[] y) {
		//Input Checks
		if(x == null) throw new NullPointerException("Input x was null");
		if(y == null) throw new NullPointerException("Input y was null");
		if(x.length == 0) throw new IllegalStateException("Input x is empty");
		if(y.length == 0) throw new IllegalStateException("Input y is empty");
		if(x.length != y.length) throw new IllegalStateException("Size of input x != size of input y");
		//Global Assignments
		this.x = x;
		this.y = y;
		this.xRef = new AtomicReference<Double[]>(this.x);
		this.yRef = new AtomicReference<Double[]>(this.y);
		intercept = 0.0;
		slope = 0.0;
		dataSize = x.length;
		xMean = Arrays.asList(x).stream().mapToDouble(val -> val).average().orElse(0.0);
		yMean = Arrays.asList(y).stream().mapToDouble(val -> val).average().orElse(0.0);
		System.out.println("xMean, yMean: " + xMean + "," + yMean);
		threadCount = MultithreadUtilities.getThreadStats(dataSize).threadCount;
		remainder = MultithreadUtilities.getThreadStats(dataSize).remainderData;
		threadData = MultithreadUtilities.getThreadStats(dataSize).threadData;
		System.out.println("remainder: " + remainder);
	}
	
	public boolean fit(boolean gradient, boolean parallel) throws InterruptedException, ExecutionException {
		if(parallel) {
			parallelSlopeEstimator();
			if (gradient) gradientInterceptEstimator(); //Change to parallel later
			else simpleInterceptEstimator();
		} else {
			sequentialSlopeEstimator();
			if (gradient) sequentialGradientInterceptEstimator();
			else simpleInterceptEstimator();
		}
		return true;
	}
	
	public boolean fit() throws InterruptedException {
		sequentialSlopeEstimator();
		simpleInterceptEstimator();
		return true;
	}

	public boolean fit(boolean parallel) throws InterruptedException, ExecutionException{
		if(parallel) {
			parallelSlopeEstimator();
			simpleInterceptEstimator();
		} else {
			sequentialSlopeEstimator();
			simpleInterceptEstimator();
		}
		return true;
	}

	//TODO Optimize 
	
	private void parallelSlopeEstimator() throws InterruptedException, ExecutionException {
		System.out.println("Running parallel slope estimator with dataSize: " + dataSize + " and threadCount: " + threadCount);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		AtomicReference<CountDownLatch> countDownLatchRef = new AtomicReference<CountDownLatch>(countDownLatch);
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		List<Future<Double[]>> futures = new ArrayList<Future<Double[]>>();
		for(int i = 0; i < threadCount; i++) {
			int start = i * dataSize;
			int end = (i+1) * dataSize -1;
			if(i == threadCount -1 && remainder != dataSize) end += remainder;
			slopeThread t = new slopeThread(start, end, xRef, yRef, xMean, yMean, countDownLatchRef);
			Future<Double[]> sums = pool.submit(t);
			futures.add(sums);
		}
		double numerator = 0;
		double denominator = 0;
		countDownLatchRef.get().await();
		System.out.println("slopeThreads finished execution");
		Thread.sleep(5);
		for(Future<Double[]> sums : futures) {
			numerator += sums.get()[0];
			denominator += sums.get()[1];
		}
		slope = new Double(numerator / denominator);
	}

	private void sequentialSlopeEstimator() {
		System.out.println("Running sequential slope estimator with dataSize: " + dataSize);
		Double numerator = new Double(0.0);
		for(int index = 0; index < dataSize; index++) {
			Double xError = (x[index] - xMean);
			Double yError = y[index] - yMean;
			numerator += xError * yError;
		}
		Double denominator = new Double(0.0);
		for(int index = 0; index < dataSize; index++) {
			Double xError = x[index] - xMean;
			denominator += xError * xError;
		}
		slope = numerator / denominator;
	}
	
	private void simpleInterceptEstimator() {
		intercept = yMean - slope * xMean;
	}
	
	private void gradientInterceptEstimator() throws InterruptedException, ExecutionException {
		System.out.println("Running parallel gradient descent estimator v1 with dataSize: " + dataSize + " threadData: " + threadData);
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
			System.out.println("gSlope : " + gSlope + " gIntercept: " + gIntercept);
		}
		intercept = new Double(gIntercept);
	}
	
	private double parallelSumSquareResidual(Double gIntercept) throws InterruptedException, ExecutionException {
		double residualSum = 0.0;
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		AtomicReference<CountDownLatch> countDownLatchRef = new AtomicReference<CountDownLatch>(countDownLatch);
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		List<Future<Double>> sums = new ArrayList<Future<Double>>();
		for(int i = 0; i < threadCount; i++) {
			int start = i * dataSize;
			int end = (i+1) * dataSize -1;
			if(i == threadCount -1 && remainder != dataSize) end += remainder;
			sumSquareResidualsThread t = new sumSquareResidualsThread(start,end,slope,gIntercept,xRef,yRef,countDownLatchRef);
			Future<Double> sum = pool.submit(t);
			sums.add(sum);
		}
		countDownLatchRef.get().await();
		Thread.sleep(5);
		for(Future<Double> sum : sums) {
			residualSum += sum.get();
		}
		return residualSum;
	}
	
	private void sequentialGradientInterceptEstimator() {
		System.out.println("Running sequential gradient descent estimator with dataSize: " + dataSize);
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
		intercept = new Double(gIntercept);
	}
	
	private double sequentialSquareResidualSum(double gIntercept) {
		double sum = 0;
		for(int index = 0; index < dataSize; index++) {
			sum += -2*(y[index] - (gIntercept + slope * x[index]));
		}
		return sum;
	}
	
	public Double getSlope() {
		return slope;
	}
	
	public Double getIntercept() {
		return intercept;
	}
	
	public Double getEstimatedValue(Double xValue) {
		return intercept + xValue * slope;
	}
	
	
	//Main method, used for testing purposes
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("Starting Linear Regression");
		Double[] xIn = {0.5,2.3,2.9};
		Double[] yIn = {1.4,1.9,3.2};
		boolean gradient = true;
		boolean parallel = true;
		LinearRegression lr = new LinearRegression(xIn,yIn);
		System.out.println("Running fit with gradient: " + gradient + " parallel: " + parallel);
		lr.fit(gradient, parallel);
		Double slopeOut = lr.getSlope();
		Double interceptOut = lr.getIntercept();
		System.out.println("Estimated Equation: y = " + slopeOut + "x + " + interceptOut);
		System.exit(0);
	}
	
}

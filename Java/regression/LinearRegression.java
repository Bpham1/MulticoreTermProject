package regression;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
		xMean = Arrays.asList(x).parallelStream().mapToDouble(val -> val).average().orElse(0.0);
		yMean = Arrays.asList(y).parallelStream().mapToDouble(val -> val).average().orElse(0.0);
		dataSize = x.length;
		threadCount = MultithreadUtilities.getThreadStats(dataSize).threadData;
		remainder = MultithreadUtilities.getThreadStats(dataSize).remainderData;
		threadData = MultithreadUtilities.getThreadStats(dataSize).threadData;
		
	}
	
	public boolean fit() throws InterruptedException {
		slopeEstimator();
		interceptEstimator();
		return true;
	}
	
	//@TODO Optimize 
	
	private void slopeEstimator() throws InterruptedException {
		//Calculate slope numerator
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		AtomicReference<CountDownLatch> countDownLatchRef = new AtomicReference<CountDownLatch>(countDownLatch);
		slopeNumeratorThread[] slopeNumeratorThreads = new slopeNumeratorThread[threadCount];
		AtomicLong numeratorSum = new AtomicLong(0);
		AtomicReference<AtomicLong> numeratorSumRef = new AtomicReference<AtomicLong>(numeratorSum);
		ExecutorService numeratorPool = Executors.newFixedThreadPool(threadCount);
		for(int i  = 0; i < threadCount; i++) {
			int start = i * threadData;
			int end = (i+1) * threadData - 1;
			if(i == threadCount -1) end += remainder;
			slopeNumeratorThreads[i] = new slopeNumeratorThread(
					start, end,
					xRef, yRef,
					numeratorSumRef,
					xMean, yMean,
					countDownLatchRef);
			numeratorPool.execute(slopeNumeratorThreads[i]);
		}
		countDownLatch.await();
		numeratorPool.shutdown();
		
		//Calculate slope denominator
		countDownLatch = new CountDownLatch(threadCount);
		countDownLatchRef = new AtomicReference<CountDownLatch>(countDownLatch);
		slopeDenominatorThread[] slopeDenominatorThreads = new slopeDenominatorThread[threadCount];
		AtomicLong denominatorSum = new AtomicLong(0);
		AtomicReference<AtomicLong> denominatorSumRef = new AtomicReference<AtomicLong>(denominatorSum);
		ExecutorService denominatorPool = Executors.newFixedThreadPool(threadCount);
		for(int i = 0; i < threadCount; i++) {
			int start = i * threadData;
			int end = (i+1) * threadData - 1;
			if(i == threadCount -1) end += remainder;
			slopeDenominatorThreads[i] = new slopeDenominatorThread(
					start, end,
					xMean,
					xRef,
					denominatorSumRef,
					countDownLatchRef);
			denominatorPool.execute(slopeDenominatorThreads[i]);
		}
		countDownLatch.await();
		denominatorPool.shutdown();
		//Calculate slope
		long slope_long = numeratorSumRef.get().get() / denominatorSumRef.get().get();
		slope = new Double(slope_long);
	}
	
	
	
	private void interceptEstimator() {
		
	}
}

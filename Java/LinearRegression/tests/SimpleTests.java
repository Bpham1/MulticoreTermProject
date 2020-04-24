package tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.ExecutionException;
import optimized.regression.LinearRegression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <h1> SimpleTests </h1>
 * Tests all combinations of LinearRegression's 'fit' using a simple dataset of x = {0.5, 2.3, 2.9} and y = 1.4, 1.9, 3.2}.
 * 
 * @author Neel Drain
 * @version 0.1
 * @since 2020-04-24
 */
class SimpleTests {
	
	LinearRegression lr;
	Double[] xIn, yIn;
	final double slope = 0.641;
	final double intercept = 0.948;
	final double precision = 0.001;
	
	@BeforeEach
	void initLinearRegression() {
		System.out.println("Setting up LinearRegression lr");
		xIn = new Double[]{0.5,2.3,2.9};
		yIn = new Double[]{1.4,1.9,3.2};
		lr = new LinearRegression(xIn,yIn);
	}

	void testAssertions() {
		boolean estimatedSlope = lr.getSlope().doubleValue() < slope + precision && lr.getSlope().doubleValue() > slope - precision;
		assertTrue(estimatedSlope);
		boolean estimatedIntercept = lr.getIntercept().doubleValue() < intercept + precision && lr.getIntercept().doubleValue() > intercept - precision;
		assertTrue(estimatedIntercept);
	}
	
	@Test
	void testSimpleFit() throws InterruptedException {
		System.out.println("Testing simpleFit() within 3 decimal points");
		lr.simpleFit();
		testAssertions();
	}
	
	@Test
	void testParallelSimpleFit() throws InterruptedException, ExecutionException {
		System.out.println("Testing simpleFit(true) within 3 decimal points");
		lr.simpleFit(true);
		testAssertions();
	}
	
	@Test
	void testSequentialGradientFit() throws InterruptedException, ExecutionException {
		System.out.println("Testing gradientFit(false) within 3 decimal points");
		lr.gradientFit(false);
		testAssertions();
	}
	
	@Test
	void testParallelGradientFit() throws InterruptedException, ExecutionException {
		System.out.println("Testing gradientDescent(true) within 3 decimal points");
		lr.gradientFit(true);
		testAssertions();
	}

}

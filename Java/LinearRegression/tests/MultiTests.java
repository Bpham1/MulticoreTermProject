package tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import optimized.regression.MultiLinearRegression;
import optimized.utils.OptionalPrinter;
import optimized.utils.SimpleCSVReader;

class MultiTests {

	@Test
	void simpleSmallTest() throws InterruptedException, ExecutionException, IOException {
		String filename = "SimpleInputSmall.csv";
		SimpleCSVReader reader = new SimpleCSVReader(filename);
		reader.read();
		Double[][] x = reader.getXAsArray();
		System.out.println("x : " + Arrays.deepToString(x));
		Double[] y = reader.getYAsArray();
		System.out.println("y : " + Arrays.deepToString(y));
		MultiLinearRegression mlr = new MultiLinearRegression(x,y);
		mlr.op.setVerbosity(OptionalPrinter.DEBUG);
		mlr.simpleFit();
		System.out.println("Coeffs: " + Arrays.toString(mlr.getCoeffs()));
		System.out.println("Intercept: " + mlr.getIntercept());
	}

}

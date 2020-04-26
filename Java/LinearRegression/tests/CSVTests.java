package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import optimized.utils.SimpleCSVReader;

class CSVTests {

	@Test
	void testRead() throws IOException {
		String filename = "SimpleInputSmall.csv";
		SimpleCSVReader reader = new SimpleCSVReader(filename);
		reader.read();
		Double[][] x = reader.getXAsArray();
		Double[] y = reader.getYAsArray();
		Double[][] expX = {{1.0,2.0,3.0},{4.0,5.0,6.0},{7.0,8.0,9.0},{8.2,9.4,18.8},{13.4,12.6,14.4}};
		Double[] expY = {1.0,2.0,3.0,9.1,24.5};
		assertEquals(expX[0].length,x[0].length);
		assertEquals(expY.length,y.length);
		//Check x values
		for(int i = 0; i < x.length; i++) {
			for(int j = 0; j < x[0].length; j++) {
				assertEquals(x[i][j].doubleValue(),expX[i][j].doubleValue());
			}
		}
		//Check y values
		for(int i = 0; i < y.length; i++) {
			assertEquals(y[i],expY[i].doubleValue());
		}
	}

}

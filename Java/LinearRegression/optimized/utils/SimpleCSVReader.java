package optimized.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <h1> SimpleCSVReader </h1>
 * Simple CSV Reader partially adapted from Brandon Pham's CSVReaderWriter located in the /Java/Utils folder of the github repo.
 * Reads all data from a CSV file without extra functionality to maintain simplicity of use.
 * Input x / independent variable / factor data must be left of y data
 * Input y / dependent variable / observation data must be right of x data e.g. in the last column of the CSV file
 * 
 * @author Neel Drain
 * @version 0.1
 * @since 2020-04-26
 */
public class SimpleCSVReader {
	
	/**
	 * A dynamic list of dynamic columns of {@linkplain java.lang.Doube} x data;
	 */
	private ArrayList<ArrayList<Double>> x;
	
	/**
	 * A dynamic list of {@linkplain java.lang.Double} y data.
	 */
	private ArrayList<Double> y;
	
	/**
	 * The file name of the CSV file being read.
	 */
	private String filename;
	
	/**
	 * The optional labels included in the CSV file.
	 * If multiple lines of labels are included, only the bottom-most line of labels will be used.
	 */
	private ArrayList<String> labels;
	
	/**
	 * Constructor which takes in a String input file name, note that there is no path correction or checking
	 * @param filename is the filename of the CSV file to read from
	 */
	public SimpleCSVReader(String filename) {
		x = new ArrayList<ArrayList<Double>>();
		y = new ArrayList<Double>();
		labels = new ArrayList<String>();
		this.filename = filename;
	}
	
	/**
	 * Reads the CSV file and puts the data into the appropriate {@link #x} and {@link #y} lists
	 * @throws IOException 
	 */
	public void read() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = new String();
		try {
			boolean firstRead = true;
			int colsLength = 0;
			while ((line = br.readLine()) != null) {
			    // Split by commas
			    String[] cols = line.split(",");
			    //If first read, init. lists for each column
			    if(firstRead) {
			    	for(int i = 0; i < cols.length; i++) x.add(new ArrayList<Double>());
			    	colsLength = cols.length;
			    	firstRead = false;
			    }
			    //Add data to each ArrayList<Double> in x
			    for(int i = 0; i < cols.length; i++) {
			    	try {
			    		x.get(i).add(new Double(Double.valueOf(cols[i])));
			    	}
			    	catch(Exception e) {
			    		//If cannot convert to Double, assume label lines
			    		if(i == 0) labels = new ArrayList<String>();
			    		labels.add(cols[i]);
			    	}
			    }
			}
		    //Separate y data from x data
		    y = x.get(colsLength-1);
		    x.remove(colsLength-1);
		} catch (IOException e) {
			System.err.println("Couldn't read line.");
			e.printStackTrace();
		}
		br.close();
	}
		
	//Getters
	
	/**
	 * Gets {@link #x} as a 2-dimensional {@linkplain java.lang.Double} array
	 * Runs sequentially TODO needs parallel optimization
	 * @return {@link #x} values
	 */
	public Double[][] getXAsArray() {
		int numFactors = x.size();
		int numObservations = y.size();
		Double[][] xArray = new Double[numObservations][numFactors];
		for(int observationIndex = 0; observationIndex < numObservations; observationIndex++) {
			for(int factorIndex = 0; factorIndex < numFactors; factorIndex++) {
				xArray[observationIndex][factorIndex] = new Double(x.get(factorIndex).get(observationIndex));
			}
		}
		return xArray;
	}
	
	/**
	 * Gets {@link #y} as a {@linkplain java.lang.Double} array
	 * Runs sequentially since init. parallel threads is same time cost as sequential assignment
	 * @return
	 */
	public Double[] getYAsArray() {
		return y.stream().map(val -> val).parallel().toArray(Double[]::new);
	}

	/**
	 * Getter for {@link #x}
	 * @return {@link #x}
	 */
	public ArrayList<ArrayList<Double>> getX() {
		return x;
	}

	/**
	 * Getter for {@link #y}
	 * @return {@link #y}
	 */
	public ArrayList<Double> getY() {
		return y;
	}

	/**
	 * Getter for {@link #filename}
	 * @return {@link #filename}
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Getter for {@link #labels}
	 * @return {@link #labels}
	 */
	public ArrayList<String> getLabels() {
		return labels;
	}
	
}

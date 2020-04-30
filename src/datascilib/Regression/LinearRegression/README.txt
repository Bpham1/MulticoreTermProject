-- Linear Regression Calculator --
Calcuates the slope and intercept of an estimated line-of-best fit for data seperated into x values and y values
using Least Mean Squared (LMS) and/or a custom version of Gradient Descent with both sequential and parallel iterations.

FOR FULL DESCRIPTIONS OF CLASSES, METHODS, VARIABLES PLEASE SEE THE INCLUDED JAVADOC IN ./doc
OR AT https://aswanmordor.github.io/LinearRegression/

-- Package Descriptions --
regression (NON PRODUCTION) :  The development version of the Linear Regression calculator containing
	LinearRegression.java : the main Linear Regression calculator class
	MultiLinearRegression.java : the main multivariate Linear Regression calculator class
	slopeThread.java : calculates the slope of a partition of the dataset
	sumSquareResidualsThread.java : calculates the derivatve of sum squared residuals for a partition of the dataset
utilities (NON PRODUCTION) : The development version of the utils packages containing
	ThreadInfo.java : calculates thread-related information for multi-threaded operations
	Optionalprinter.java : 	a System.out.println wrapper with added user-specified verbosity	
	SimpleCSVReader.java : 	a partial adaptation of Brandon Pham's CSVReaderWriter.java located in /Java/Utils
				folder on github, which reads an enitre CSV file and splits it into the correct inputs
				for MultiLinearRegression (Double[][] x, Double[] y)
	ThreadStats.java : stores the stats for a thread

-- LinearRegression Usage --
Inport datasets x values and y values as Double[] x, Double[] y
Create a new LinearRegression class : LinearRegression lr = new LinearRegression(x,y)
Choose a 'fit option' : simpleFit() calculates  using sequential LMS for slope and intercept
			simpleFit(false) same as simpleFit()
			simpleFit(true) calculates using parallel LMS for slope, sequential LMS for intercept
			gradientFit(false) calculates using sequential LMS for slope, sequential Gradient Descent for intercept
			gradientFit(true) calculates using parallel LMS for slope, parallel GradientDescent for intercept
Get slope : getSlope() returns Double
Get intercept : getIntercept() returns Double
Get estimated (predicted) y value at user-specified x value : getEstimatedValue(xValue) returns Double

-- MultiLinearRegression Usage --
Import datasets x values and y values as Double[][] x, Double[] y, from either CSV using SimpleCSVReader or raw
Create a new MultiLinearRegression class : MultiLinearRegression mlr = new MultiLinearRegression(x,y)
Call 'fit' function : mlr.simpleFit()
Get slope coefficients : mlr.getCoeffs() as Double[]
Get intercept : mlr.getIntercept() as Double
Get estimated (predicted) y value at user-specified x coefficients : mlr.getEstimatedValue(xValues) returns Double

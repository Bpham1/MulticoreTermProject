Authors:
Brandon Pham 		bp23792
Matthew Machado		
Jonathan Mounsif	
Neel Drain 		npd388

## How to Use ##

1. Copy the contents of src into your project's source folder.
2. Create Data
	1. Create a .csv (Comma-Separated Values) file
	2. Add values to the .csv file in the correct format
		I.  First row must contain column names
		II. Data elements must either be Double or Integer (String is also possible for a data labels (Y) column, but it will need to be converted with LabelEnumerator)

3. Import data using CSVReaderWriter
	CSVReaderWriter csvWR = new CSVReaderWriter();
	csvWR.read("path/to/inputs.csv");

4. Create X and Y
	If X needs to be of type List<List<Double>> (Everything, but Linear Regression Wrapper):
		List<List<Double>> train_X = csvWR.getColumnRangeAsDouble("Col 1", "Col Last");

	If X or Y needs to be of type List<Double> (Linear Regression Wrapper):
		List<Double> train_X = csvWR.getColumnAsDouble("X");
		List<Double> train_Y = csvWR.getColumnAsDouble("Y");

	If Y needs to be of type List<Integer> (Everything, but (Multi) Linear Regression Wrapper):
		Scenario 1: column contains integer labels
			List<Integer> train_Y = csvWR.getColumnAsInt("Y");

		Scenario 2: column contains string labels
			List<String> tempY = csvWR.getColumn("Y");
			LabelEnumerator<String> le = new LabelEnumerator<String>();
			List<Integer> train_Y = le.enumerateLabels(tempY);

5. Pick an algorithm and create an object
	Ex: RandomForest rf = new RandomForest(300);

6. Fit the data
	rf.fit(train_X, train_Y);

7. Predict labels using algorithm
	List<Integer> predictions = rf.predict(test_X);

(Optional) 8. Output labels using CSVReaderWriter
	csvWR.addColumn("prediction", predictions);
	csvWR.write("path/to/output/file.csv");



## Regression Algorithms ##

-- Linear Regression Wrapper --

Description: 
	Linear Regression works by estimating a line based on the fitted data. Predicting values is done by passing the X value into the line and returning the Y value.
	The LinearRegression object that this class wraps implements a sequential and parallel LMS (Least Mean Squared) algorithm to calculate the slope. 
	The intercept can be calculated via sequential LMS, sequential Gradient Descent, or a custom parallel Stochastic Gradient Descent.

	For more information on the original LinearRegression class, please read the README.txt file at ./src/datascilib/Regression/LinearRegression/README.txt

Import: datascilib.Regression.LinearRegression.regression.LinearRegressionWrapper

Constructor:
	LinearRegressionWrapper()

Methods:
	void fit(List<Double> X, List<Double> Y) - fits model on given X and Y
		X - X coordinates of the points to train model on
		Y - Y coordinates of the points to train model on
	void fit(List<Double> X, List<Double> Y, boolean getGradient) - fits model on given X and Y; can improve model with gradient descent
		X - X coordinates of the points to train model on
		Y - Y coordinates of the points to train model on
		getGradient - specifies whether to use gradient descent to improve model
	List<Double> predict(List<Double> X) - returns labels (Y) corresponding to X
		X - X coordinates to predict labels on

Example Usage:
	LinearRegressionWrapper lr = new LinearRegressionWrapper();
	lr.fit(train_X, train_Y);
	List<Double> prediction = lr.predict(test_X);

-- Multi-Dimensional Linear Regression Wrapper --

Description: 
	Linear Regression works by estimating a line based on the fitted data. Predicting values is done 
	by passing the X value into the line and returning the Y value.
	
	In this case, X now has multiple dimensions. The MultiLinearRegression object that this class wraps 
	is a functional extension of LinearRegression, where each dependent variable is predicted using multiple independent variables.

	For more information on the original MultiLinearRegression class, please read the README.txt file at ./src/datascilib/Regression/LinearRegression/README.txt

Import: datascilib.Regression.LinearRegression.regression.MultiLinearRegressionWrapper

Methods:
	void fit(List<List<Double>> X, List<Double> Y) - fits model on given X and Y with gradient descent
		X - multi-feature list of the points to train model on
		Y - Corresponding values of the X points to train model on
	List<Double> predict(List<List<Double>> X) - returns values (Y) corresponding to X
		X - multi-feature X to predict labels on

Example Usage:
	MultiLinearRegressionWrapper mlr = new MultiLinearRegressionWrapper();
	mlr.fit(train_X, train_Y);
	List<Double> prediction = mlr.predict(test_X);



## Classification Algorithms ##

-- K Nearest Neighbors --

Description:
	A distance-based classifier that predicts labels finding the K "nearest" points and using the majority 
	label of those "neighbors". How near a point is based on a Euclidean distance.

Import: datascilib.Classifiers.KNN.KNearestNeightbors

Constructor: 
	KNearestNeightbors(int k)
		k - number of neighbors used to decide labels
Methods:
	void fit(List<List<Double>> X, List<Integer> Y) - fits model on given X and Y
		X - multi-feature list of the points to train model on
		Y - corresponding integer labels of the X points to train model on
	List<Integer> predict(List<List<Double>> X) - returns integer labels (Y) corresponding to X
		X - multi-feature X to predict integer labels on

Example Usage:
	KNearestNeightbors knn = new KNearestNeightbors(3);
	knn.fit(train_X, train_Y);
	List<Integer> prediction = knn.predict(test_X);

-- Gaussian Bayes --

Description:
	A probabilistic classifier based on Bayes' theorem. Calculates the probability of a label given a 
	feature value and uses the probabilities to calculate the probability of a label based on fitted.

Import: datascilib.Classifiers.NaiveBayes.ParallelGaussianBayesClassifier

Constructor:
	ParallelGaussianBayesClassifier()
	ParallelGaussianBayesClassifier(int numJobs)
		numJobs - maximum # of threads to use

Methods:
	void fit(List<List<Double>> X, List<Integer> Y) - fits model on given X and Y
		X - multi-feature list of the points to train model on
		Y - corresponding integer labels of the X points to train model on
	List<Integer> predict(List<List<Double>> X) - returns integer labels (Y) corresponding to X
		X - multi-feature X to predict integer labels on

Example Usage:
	ParallelGaussianBayesClassifier gbc = new ParallelGaussianBayesClassifier();
	gbc.fit(train_X, train_Y);
	List<Integer> prediction = gbc.predict(test_X);

-- Descision Tree --

Description:
	Decision Tree Estimator is a tree that splits fitted values into a tree, where at each split, 
	the fitted X and Y is split based on a threshold and feature that optimizes the GINI gain.	

Import: datascilib.Classifiers.DecisionTree.DescisionTreeEstimator

Constructor:
	DescisionTreeEstimator()
	DescisionTreeEstimator(int n_jobs)
		n_jobs - maximum # of threads to use

Methods:
	void fit(List<List<Double>> X, List<Integer> Y) - fits model on given X and Y
		X - multi-feature list of the points to train model on
		Y - corresponding integer labels of the X points to train model on
	List<Integer> predict(List<List<Double>> X) - returns integer labels (Y) corresponding to X
		X - multi-feature X to predict integer labels on

Example Usage:
	DescisionTreeEstimator dte = new DescisionTreeEstimator();
	dte.fit(train_X, train_Y);
	List<Integer> prediction = dte.predict(test_X);

-- Random Forest --

Import: datascilib.Classifiers.RandomForest.RandomForest

Constructor:
	RandomForest(int n_estimators)
		n_estimators - # of DecisionTreeEstimators to average to produce labels
	RandomForest(int n_estimators, int n_jobs)
		n_estimators - # of DecisionTreeEstimators to average to produce labels
		n_jobs - maximum # of threads to use

Methods:
	void fit(List<List<Double>> X, List<Integer> Y) - fits model on given X and Y
		X - multi-feature list of the points to train model on
		Y - corresponding integer labels of the X points to train model on
	List<Integer> predict(List<List<Double>> X) - returns integer labels (Y) corresponding to X
		X - multi-feature X to predict integer labels on

Example Usage:
	RandomForest rf = new RandomForest(300);
	rf.fit(train_X, train_Y);
	List<Integer> prediction = rf.predict(test_X);



## Clustering ##

-- K Means --

Description:
	Clustering algorithm that can generate optimal labels when no training labels are provided.
	Works by alternating between assigning points to their closest center and moving the center to the average of the
	assigned points, until assigned points no longer change. The centers are now the optimal labels for the points
	assigned to it. When predicting, the label is determined by the closest center to the point.

Import: datascilib.Clustering.KMeans.KMeans

Constructor:
	KMeans(int n_cluster)
		n_cluster - # of clusters centers to calculate
	KMeans(int n_cluster, int n_jobs)
		n_cluster - # of clusters centers to calculate
		n_jobs - maximum # of threads to use

Methods:
	void fit(List<List<Double>> X) - fits model on given X
		X - multi-feature list of the points to train model on
	List<Integer> predict(List<List<Double>> X) - returns integer labels (Y) corresponding to X
		X - multi-feature X to predict integer labels on

Example Usage:
	KMeans km = new KMeans(3);
	km.fit(train_X);
	List<Integer> prediction = km.predict(test_X);



## Utilities ##

-- CSV Reader/Writer --
Description:
	Tool used to read and store CSV data in a format that can easily be modified, extracted from, and outputted to another CSV.

Import: datascilib.Utils.CSVReaderWriter

Constructor:
	CSVReaderWriter()

Methods:
	boolean read(String filePath) - reads in and stores a correctly formatted CSV; returns true if read is successful, false otherwise
		filePath - path to the CSV file

	boolean set(List<String> featureNames, List data) - sets the featureNames and data to write/modify; returns true if read is successful, false otherwise
		featureNames - feature/column names for the data
		data - List of rows containing the data to write/modify

	List<String> getColumn(String colName) - returns column corresponding to colName as a List<String>
		colName - column name

	List<Integer> getColumnAsInt(String colName) - returns column corresponding to colName as a List<Integer>
		colName - column name

	List<Double> getColumnAsDouble(String colName) - returns column corresponding to colName as a List<Double>
		colName - column name

	List<List<String>> getColumnRange(String startCol, String endCol) - returns List<List<String>> of rows that only contains columns between startCol and endCol (both inclusive)
		startCol - name of first column in range
		endCol - name of last column in range

	List<List<Double>> getColumnRangeAsDouble(String startCol, String endCol) - returns List<List<Double>> List of rows that only contains columns between startCol and endCol (both inclusive)
		startCol - name of first column in range
		endCol - name of last column in range

	boolean addColumn(String name, List column) - adds a column to the data; returns true if add is successful, false otherwise
		name - column name
		column - List<String> or List<Integer> or List<Double> to add to data

	boolean write(String filePath, List<String> featureNames, List data) - writes a CSV file based on provided feature names and data; returns true if write is successful, false otherwise
		filePath - path to write a CSV file to or overwrite an existing file
		featureNames - name of the columns
		data - List of rows containing the data to write

	boolean write(String filePath) - writes a CSV file based on stored feature names and data; returns true if write is successful, false otherwise
		filePath - path to write a CSV file to or overwrite an existing file

	List<String> getFeatureNames() - returns a list of the stored feature names

	List<String> getData() - returns a list of rows of the stored data

	void clear() - clears the stored feature names and data

	void shuffle() - shuffles the stored data

Example Usage:
	CSVReaderWriter csvWR = CSVReaderWriter();
	csvWR.read("./src/SampleInputs/SampleInputs.csv");
	List<List<Double>> train_X = csvWR.getColumnRangeAsDouble("Feat 1", "Feat 3");
	List<Integer> train_X = csvWR.getColumnAsInt("Y");
	...
	csvWR.addColumn("Pred", prediction);
	csvWR.write("./src/SampleInputs/SampleOutput.csv")

-- Label Enumerator --
Description:
	Tool used to convert non-Integer lists to integer lists or to simply enumerate labels.

Import: datascilib.Utils.LabelEnumerator

Constructor:
	LabelEnumerator<T>()
		T - class type to enumerate
	LabelEnumerator<T>(List<T> labels)
		T - class type to enumerate
		labels - List to map and enumerate

Methods:
	List<Integer> enumerateLabels(List<T> labels) - returns a list of enumerated labels (starting from 0 and incrementing) corresponding to passed labels; stores a mapping for the labelEnumerates function
		labels - labels to convert and enumerate
	List<T> labelEnumerates(List<Integer> enumeratedLabels) - converts a list to its original labels corresponding to the stored mapping and returns it
		enumeratedLabels - labels previously enumerated that will be converted back

Example Usage:
	LabelEnumerator<String> le = CSVReaderWriter<String>();
	KMeans km = new KMeans(3);
	km.fit(train_X);
	List<Integer> prediction = km.predict(test_X);			// Cluster labels are not necessarily in order
	List<Integer> enumeratedPred = le.enumerateLabels(prediction);	// Ensures labels are in order

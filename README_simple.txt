## Regression Algorithms ##

-- Linear Regression --

Import: datascilib.Regression.LinearRegression.regression.LinearRegression

Constructor:
	LinearRegression(Double[] x, Double[] y)
		x - X coordinates of the points to fit model on
		y - Y coordinates of the points to fit model on

Methods:
	boolean simpleFit() - fits x and y; returns true if successful

	boolean simpleFit(boolean parallel) - fits x and y; returns true if successful
		parallel - fits in parallel if true; fits in sequence if false

	boolean gradientFit(boolean parallel) - fits x and y with gradient descent to improve model; returns true if successful
		parallel - fits in parallel if true; fits in sequence if false
	
	Double calcualteStandardError() - calculates how off the model is from the fitted points

	Double getEstimatedValue(Double xValue) - get Y value corresponding to xValue
		xValue - X coordinate to predict Y value on

	Double[] getX() - returns stored x
	
	Double[] getY() - returns stored y

	Double getIntercept() - returns model intercept

	Double getxMean() - returns average of x

	Double getyMean() - returns average of y
	
	int getThreadCount() - get # of threads used

	int getDataPerThread() - get # of points per thread

	int getRemainder() - returns remainder threads

Example Usage:
	LinearRegression lr = new LinearRegression(train_X, train_Y);
	lr.gradientFit(true);
	List<Double> pred = new ArrayList<Double>();
	for(double x: test_X){
		pred.add(lr.getEstimatedValue(x));
	}

-- Multi-Feature Linear Regression --

Import: datascilib.Regression.LinearRegression.regression.MultiLinearRegression

Constructor:
	LinearRegression(Double[][] x, Double[] y)
		x - array of the multi-feature points to train model on
		y - Y coordinates of the points to fit model on

Methods:
	boolean simpleFit() - fits x and y in parallel with gradient descent to improve model; returns true if successful
	
	Double calcualteStandardError() - calculates how off the model is from the fitted points

	Double getEstimatedValue(Double xValue) - get Y value corresponding to xValue
		xValue - X coordinate to predict Y value on

	Double[][] getX() - returns stored x
	
	Double[] getY() - returns stored y

	int getNumObservations() - get # of observations used by model

	int getNumFactors() - gets # of factors used by model

	Double[] getCoeffs() - get coefficients calculated by the model

	Double getyMean() - returns average of y

	int getDataPerThread() - get # of points per thread

	int getRemainder() - returns remainder threads

	Double[] getStandardErrors() - gets the standard error of the model
	
	int getThreadCount() - get # of threads used

	Double getIntercept() - returns model intercepts
	

-- Linear Regression Wrapper --

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

Import: datascilib.Utils.LabelEnumerator

Constructor:
	LabelEnumerator<T>()
	LabelEnumerator<T>(List<T> labels)
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
	






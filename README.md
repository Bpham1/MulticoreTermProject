Authors:
Brandon Pham 		bp23792
Matthew Machado		mrm5664	
Neel Drain 			npd388
Jonathan Mounsif	jrm4496

Java Doc: http://bpham1.github.io/MulticoreTermProject

## Project Description ##

Topic: Parallel Algorithms for Data Mining

Implement parallel algorithms related to data mining such as for clustering, classification or regression.


Data Mining has predominantly been done in Python due to its simplicity in syntax and programming requirements.
However, Python is very slow as a language and uses GIL (Global interpreter lock) that prevents more than one thread from executing Python code more than once.
GIL can be circumvented in various ways, but the speed of Python was still bringing down efficiency.

The goal of our project was to bring the speed and multithreading capabilities of Java to Data Mining.

In our current release, we have provided the following algorithms:

Linear Regression
Decision Tree Classification
Random Forest Classification
K Nearest Neighbors Classification
Gaussian Bayes Classification
K Means Clustering



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

Run Time Evaluation (Each function ran 20 times per data set without gradient descent calculation):
	Super Extreme Set (100000)
	Sequential Fit Mean:           	6428460
	Sequential Fit Median:         	4645700
	Parallel Fit Mean:   			6921965
	Parallel Fit Median: 			3595400
	Sequential Predict Mean:        2423965
	Sequential Predict Median:      2021800
	Parallel Predict Mean:   		2492535
	Parallel Predict Median: 		2031500

	Extreme Set (10000)
	Sequential Fit Mean:           	3431300
	Sequential Fit Median:         	347300
	Parallel Fit Mean:   			4260790
	Parallel Fit Median: 			1492200
	Sequential Predict Mean:        1258595
	Sequential Predict Median:      84400
	Parallel Predict Mean:   		1292402
	Parallel Predict Median: 		85200

	Large Set (100)
	Sequential Fit Mean:           	2304443
	Sequential Fit Median:         	47100
	Parallel Fit Mean:   			3439860
	Parallel Fit Median: 			1469900
	Sequential Predict Mean:        839806
	Sequential Predict Median:      1900
	Parallel Predict Mean:   		862380
	Parallel Predict Median: 		1900

	Medium Set (30)
	Sequential Fit Mean:           	1739980
	Sequential Fit Median:         	34200
	Parallel Fit Mean:   			3090506
	Parallel Fit Median: 			1469900
	Sequential Predict Mean:        630006
	Sequential Predict Median:      400
	Parallel Predict Mean:   		646898
	Parallel Predict Median: 		400

	Small Set (1)
	Sequential Fit Mean:           	1396053
	Sequential Fit Median:         	17500
	Parallel Fit Mean:   			2521886
	Parallel Fit Median: 			234700
	Sequential Predict Mean:        504546
	Sequential Predict Median:      300
	Parallel Predict Mean:   		517765
	Parallel Predict Median: 		300
	
	Testing on the original 3 data sets, I observed that the algorithm's fit was running significantly worse in parallel than sequentially.
	However, I noticed a trend that the parallel fit performance was improving compared to sequential as data set was enlarged.

	I proved this trend by using much larger data sets, which show significantly better performance.
	
	Predict simply returns a Y based on the slope and intercept calculated by fit, so there would be no performance increase.
	
Run Time Evaluation (Each function ran 20 times per data set with gradient descent calculation):
	Super Extreme Set (100000)
	Sequential Fit Mean:           	24030585
	Sequential Fit Median:         	17382700
	Parallel Fit Mean:   			2147483647
	Parallel Fit Median: 			8968519400
	Sequential Predict Mean:        1825395
	Sequential Predict Median:      1165900
	Parallel Predict Mean:   		1895145
	Parallel Predict Median: 		1268600
	
	Extreme Set (10000)
	Sequential Fit Mean:           	13333997
	Sequential Fit Median:         	2565000
	Parallel Fit Mean:   			2147483647
	Parallel Fit Median: 			8447078600
	Sequential Predict Mean:        983475
	Sequential Predict Median:      161500
	Parallel Predict Mean:   		1016752
	Parallel Predict Median: 		158000
	
	Large Set (100)
	Sequential Fit Mean:           	9265253
	Sequential Fit Median:         	1143600
	Parallel Fit Mean:   			2147483647
	Parallel Fit Median: 			8376931500
	Sequential Predict Mean:        656941
	Sequential Predict Median:      3900
	Parallel Predict Mean:   		679243
	Parallel Predict Median: 		4200
	
	Medium Set (30)
	Sequential Fit Mean:           	7014050
	Sequential Fit Median:         	255800
	Parallel Fit Mean:   			2147483647
	Parallel Fit Median: 			8374253100
	Sequential Predict Mean:        492856
	Sequential Predict Median:      400
	Parallel Predict Mean:   		509551
	Parallel Predict Median: 		400
	
	Small Set (1)
	Sequential Fit Mean:           	5619297
	Sequential Fit Median:         	41700
	Parallel Fit Mean:   			2147483647
	Parallel Fit Median: 			842598900
	Sequential Predict Mean:        394919
	Sequential Predict Median:      300
	Parallel Predict Mean:   		407912
	Parallel Predict Median: 		300
	
	The sequential gradient descent portion of LR decreases in performance as data set scales. Parallel gradient descent portion of LR runs a "smarter" version 
	that runs many gradient descents at the same time and has a set run time (defined by a maximum number of iterations).
	
	In this case we use the threads to improve the model rather than improve performance. However, as data set size gets extremely large, the sequential gradient 
	descent will also hit maximum iterations before stopping. This is where parallel gradient will have identical performance, but better results.

-- Multi-Dimensional Linear Regression Wrapper --

Description: 
	Linear Regression works by estimating a line based on the fitted data. Predicting values is done 
	by passing the X value into the line and returning the Y value.
	
	In this case, X now has multiple dimensions. The MultiLinearRegression object that this class wraps 
	is a functional extension of LinearRegression, where each dependent variable is predicted using multiple independent variables.

	For more information on the original MultiLinearRegression class, please read the README.txt file at ./src/datascilib/Regression/LinearRegression/README.txt

Import: datascilib.Regression.LinearRegression.regression.MultiLinearRegressionWrapper

Constructor:
	MultiLinearRegressionWrapper()

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

Run Time Evaluation:
	Our algorithm for Multi Dimension Linear Regression simply runs the same Linear Regression object multiple times to fit.
	Please refer to Linear Regression Wrapper's run time evaluation.



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
	
Run Time Evaluation (Each function ran 20 times per data set with k=3):
	Extreme Set (10000 x 10)
	Sequential Fit Mean:           	459155
	Sequential Fit Median:         	175700
	Parallel Fit Mean:   			449065
	Parallel Fit Median: 			169600
	Sequential Predict Mean:        2147483647
	Sequential Predict Median:      2997946800
	Parallel Predict Mean:   		1501183830
	Parallel Predict Median: 		1478373900

	Large Set (100 x 10)
	Sequential Fit Mean:           	274607
	Sequential Fit Median:         	3500
	Parallel Fit Mean:   			227200
	Parallel Fit Median: 			3500
	Sequential Predict Mean:        1509033435
	Sequential Predict Median:      1557500
	Parallel Predict Mean:   		753576852
	Parallel Predict Median: 		5859300

	Medium Set (30 x 3)
	Sequential Fit Mean:           	183280
	Sequential Fit Median:         	100
	Parallel Fit Mean:   			151586
	Parallel Fit Mean:   			200
	Sequential Predict Mean:        1006034970
	Sequential Predict Median:      36200
	Parallel Predict Mean:   		502610395
	Parallel Predict Median: 		672800

	Small Set (1 x 1)
	Sequential Fit Mean:           	137488
	Sequential Fit Median:         	100
	Parallel Fit Mean:   			113720
	Parallel Fit Mean:   			100
	Sequential Predict Mean:        754527031
	Sequential Predict Median:      1300
	Parallel Predict Mean:			376957996
	Parallel Predict Median: 		400

	
	Testing on the original 3 data sets, I observed that the algorithm's predict was running significantly worse in parallel than sequentially. 
	However, this algorithm utilizes IntStream's parallel stream functionality. This has been shown to improve performance for large amounts of actions.
	
	With this in mind, I noticed a trend that the performance between sequential and parallel was converging as I increased the size of the data set.
	This suggested to me that this program improves predict performance as the size approaches the 10s of thousands of elements. So I created an extremely large data set
	and it showed that predict performance does improve performance for large data sets.
	
	The fit function simply stores a given X and Y, so there is no performance changes here.

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
	
Run Time Evaluation (Each function ran 20 times per data set):
	Large Set (100 x 10)
	Sequential Fit Mean:           		1093010
	Sequential Fit Median:         		738200
	Parallel Fit 2 Threads Mean:   		591215
	Parallel Fit 2 Threads Median: 		605100
	Parallel Fit 4 Threads Mean:   		764230
	Parallel Fit 4 Threads Median: 		718700
	Sequential Predict Mean:           	1242405
	Sequential Predict Median:         	959800
	Parallel Predict 2 Threads Mean:   	985805
	Parallel Predict 2 Threads Median: 	708900
	Parallel Predict 4 Threads Mean:   	1135235
	Parallel Predict 4 Threads Median: 	763400

	Medium Set (30 x 3)
	Sequential Fit Mean:           		694530
	Sequential Fit Median:         		289900
	Parallel Fit 2 Threads Mean:   		468677
	Parallel Fit 2 Threads Median: 		338500
	Parallel Fit 4 Threads Mean:   		645747
	Parallel Fit 4 Threads Median: 		523300
	Sequential Predict Mean:           	765942
	Sequential Predict Median:         	273900
	Parallel Predict 2 Threads Mean:   	651345
	Parallel Predict 2 Threads Median: 	308500
	Parallel Predict 4 Threads Mean:   	853305
	Parallel Predict 4 Threads Median: 	479300

	Small Set (1 x 1)
	Sequential Fit Mean:           		549871
	Sequential Fit Median:         		250300
	Parallel Fit 2 Threads Mean:   		446193
	Parallel Fit 2 Threads Median: 		331900
	Parallel Fit 4 Threads Mean:   		567793
	Parallel Fit 4 Threads Median: 		409900
	Sequential Predict Mean:           	590860
	Sequential Predict Median:         	213500
	Parallel Predict 2 Threads Mean:   	567918
	Parallel Predict 2 Threads Median: 	308500
	Parallel Predict 4 Threads Mean:   	705866
	Parallel Predict 4 Threads Median: 	401000

	This algorithm has an odd behavior of having significant performance increases at 2 threads, but consistently worse when at 4 threads. However, multi-threading seems to have an overall
	increase in performance for both functions over running sequentially.

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
	
Run Time Evaluation (Each function ran 20 times per data set):
	Large Set (100 x 10)
	Sequential Fit Mean:           		37270065
	Sequential Fit Median:         		30847000
	Parallel Fit 2 Threads Mean:   		29144110
	Parallel Fit 2 Threads Median: 		19643200
	Parallel Fit 4 Threads Mean:   		27947585
	Parallel Fit 4 Threads Median: 		16928700
	Sequential Predict Mean:           	408375
	Sequential Predict Median:         	433900
	Parallel Predict 2 Threads Mean:   	426345
	Parallel Predict 2 Threads Median: 	411600
	Parallel Predict 4 Threads Mean:   	448220
	Parallel Predict 4 Threads Median: 	458000

	Medium Set (30 x 3)
	Sequential Fit Mean:           		19742697
	Sequential Fit Median:         		2168100
	Parallel Fit 2 Threads Mean:   		15957197
	Parallel Fit 2 Threads Median: 		2646200
	Parallel Fit 4 Threads Mean:   		16155417
	Parallel Fit 4 Threads Median: 		4237800
	Sequential Predict Mean:           	303350
	Sequential Predict Median:         	194300
	Parallel Predict 2 Threads Mean:   	333217
	Parallel Predict 2 Threads Median: 	234000
	Parallel Predict 4 Threads Mean:   	422005
	Parallel Predict 4 Threads Median: 	374100

	Small Set (1 x 1)
	Sequential Fit Mean:           		13162178
	Sequential Fit Median:         		600
	Parallel Fit 2 Threads Mean:   		10638358
	Parallel Fit 2 Threads Median: 		700
	Parallel Fit 4 Threads Mean:   		10770525
	Parallel Fit 4 Threads Median: 		600
	Sequential Predict Mean:           	260078
	Sequential Predict Median:         	170200
	Parallel Predict 2 Threads Mean:   	278131
	Parallel Predict 2 Threads Median: 	165400
	Parallel Predict 4 Threads Mean:   	337880
	Parallel Predict 4 Threads Median: 	166500
	
	It is clear that the predict function does not scale with threads despite multithreading without any locks. It actually appears to have an inverse relationship.
	This leads me to believe that checking that X is a valid input, initialize predictions, and creating the threads took significantly more time than actually running the threads.
	
	This may explain why we also see multi-threading gains for fit diminish as the data set shrinks.
	
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

Run Time Evaluation (Each function ran 20 times per data set with 100 estimators):
	Large Set (100 x 10)
	Sequential Fit Mean:           		2147483647
	Sequential Fit Median:         		3255248800
	Parallel Fit 2 Threads Mean:   		1939086210
	Parallel Fit 2 Threads Median: 		1934186800
	Parallel Fit 4 Threads Mean:   		1691578600
	Parallel Fit 4 Threads Median: 		1650793900
	Sequential Predict Mean:           	19431810
	Sequential Predict Median:         	17767700
	Parallel Predict 2 Threads Mean:   	25200210
	Parallel Predict 2 Threads Median: 	24270500
	Parallel Predict 4 Threads Mean:   	50843725
	Parallel Predict 4 Threads Median: 	40953900
	
	Medium Set (30 x 3)
	Sequential Fit Mean:           		1747636155
	Sequential Fit Median:         		187670800
	Parallel Fit 2 Threads Mean:   		1099262595
	Parallel Fit 2 Threads Median: 		247662700
	Parallel Fit 4 Threads Mean:   		1057716880
	Parallel Fit 4 Threads Median: 		413904000
	Sequential Predict Mean:           	17969462
	Sequential Predict Median:         	16440200
	Parallel Predict 2 Threads Mean:   	24503510
	Parallel Predict 2 Threads Median: 	23492400
	Parallel Predict 4 Threads Mean:   	45744240
	Parallel Predict 4 Threads Median: 	40408800
	
	Small Set (1 x 1)
	Sequential Fit Mean:           		1165145025
	Sequential Fit Median:         		169600
	Parallel Fit 2 Threads Mean:   		732896390
	Parallel Fit 2 Threads Median: 		167400
	Parallel Fit 4 Threads Mean:   		705196228
	Parallel Fit 4 Threads Median: 		111600
	Sequential Predict Mean:           	17391145
	Sequential Predict Median:         	16124100
	Parallel Predict 2 Threads Mean:   	21753328
	Parallel Predict 2 Threads Median: 	16128900
	Parallel Predict 4 Threads Mean:   	35939045
	Parallel Predict 4 Threads Median: 	16071600

	It is clear that the predict function does not scale with threads despite multithreading without any locks. It actually appears to have an inverse relationship.
	This is consistent with the results from DecisionTreeEstimators, in which this algorithm uses as estimators.
	

	
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

Run Time Evaluation (Each function ran 20 times per data set with 10 clusters):
	Extreme Set (10000 x 10)
	Sequential Fit Mean:           		916189310
	Sequential Fit Median:         		897564200
	Parallel Fit 2 Threads Mean:   		730159545
	Parallel Fit 2 Threads Median: 		708724100
	Parallel Fit 4 Threads Mean:   		587672875
	Parallel Fit 4 Threads Median: 		569074000
	Sequential Predict Mean:           	2673075
	Sequential Predict Median:         	2562200
	Parallel Predict 2 Threads Mean:   	2055720
	Parallel Predict 2 Threads Median: 	1908100
	Parallel Predict 4 Threads Mean:   	1730475
	Parallel Predict 4 Threads Median: 	1683800

	Large Set (100 x 10)
	Sequential Fit Mean:           		518967372
	Sequential Fit Median:         		112632400
	Parallel Fit 2 Threads Mean:   		436678907
	Parallel Fit 2 Threads Median: 		142423600
	Parallel Fit 4 Threads Mean:   		420334732
	Parallel Fit 4 Threads Median: 		242434800
	Sequential Predict Mean:           	1447885
	Sequential Predict Median:         	225000
	Parallel Predict 2 Threads Mean:   	1156065
	Parallel Predict 2 Threads Median: 	255200
	Parallel Predict 4 Threads Mean:   	1074562
	Parallel Predict 4 Threads Median: 	393800

	Medium Set (30 x 3)
	Sequential Fit Mean:           		378868335
	Sequential Fit Median:         		98478400
	Parallel Fit 2 Threads Mean:   		342155001
	Parallel Fit 2 Threads Median: 		134457700
	Parallel Fit 4 Threads Mean:   		360080873
	Parallel Fit 4 Threads Median: 		239168400
	Sequential Predict Mean:           	1031056
	Sequential Predict Median:         	195800
	Parallel Predict 2 Threads Mean:   	853946
	Parallel Predict 2 Threads Median: 	245700
	Parallel Predict 4 Threads Mean:   	854400
	Parallel Predict 4 Threads Median: 	385300

	Small Set (1 x 1)
	Sequential Fit Mean:           		308784560
	Sequential Fit Median:         		97691600
	Parallel Fit 2 Threads Mean:   		283501521
	Parallel Fit 2 Threads Median: 		97733700
	Parallel Fit 4 Threads Mean:   		294679975
	Parallel Fit 4 Threads Median: 		98161200
	Sequential Predict Mean:           	814208
	Sequential Predict Median:         	165300
	Parallel Predict 2 Threads Mean:   	682423
	Parallel Predict 2 Threads Median: 	165100
	Parallel Predict 4 Threads Mean:   	682315
	Parallel Predict 4 Threads Median: 	164400
	
	These results show an significant boost in performance for fitting and predicting especially as data set size increase.



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

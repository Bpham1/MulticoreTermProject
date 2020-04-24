package optimized.threadutils;

/**
 * <h1>ThreadInfo</h1>
 * Contains static methods for finding the number of threads to use, how much data per thread, and any remainder data.
 * 
 * @author Neel Drain
 * @version 0.2
 * @since 2019-04-20
 *
 */
public class ThreadInfo {
	
	/**
	 * 
	 * @param dataSize is the size of the array to do multi-threaded operations on.
	 * @return the number of threads to use for multi-threaded operations.
	 */
	public static int threadCount(int dataSize) {
		int availProcs = Runtime.getRuntime().availableProcessors();
		return (dataSize / availProcs == 0) ? 1 : availProcs; 
	}
	
	/**
	 * 
	 * @param dataSize is the size of the array to do multi-threaded operations on.
	 * @param threadCount is the number of threads to be used.
	 * @return an int array where the first entry is the data per thread, and the second entry is the remainder data.
	 */
	public static int[] dataPerThread(int dataSize, int threadCount) {
		int[] threadData = {0, 0};
		threadData[0] = dataSize / threadCount;
		threadData[1] = dataSize % threadCount;
		return threadData;
	}
}

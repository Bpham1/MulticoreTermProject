package utilities;

public class MultithreadUtilities {
	
	public static ThreadStats getThreadStats(int dataSize) {
		ThreadStats stats = new ThreadStats();
		int availProcs = Runtime.getRuntime().availableProcessors();
		stats.threadCount = (dataSize / availProcs == 0) ? 1 : availProcs;
		stats.threadData = (dataSize / availProcs == 0) ? dataSize : dataSize / availProcs;
		stats.remainderData = dataSize % availProcs;
		return stats;
	}
}

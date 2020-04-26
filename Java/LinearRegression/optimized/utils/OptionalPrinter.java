package optimized.utils;

/**
 *<h1> OptionalPrinter </h1>
 * Prints with set {@linkplain #VERBOSITY} level
 *</br>
 *0 : No Printing, 1 : Print Method Runs, 2 : Print Debugging Statements
 *
 * @author Neel Drain
 * @version 0.1
 * @since 2020-20-4
 */
public class OptionalPrinter {
	/**
	 * The verbosity level
	 */
	public int VERBOSITY = 1;
	
	/**
	 * Wrapper int for level 0 verbosity, used to ensure no print output
	 */
	public static final int NONE = 0;
	
	/**
	 * Wrapper int for level 1 verbosity, used to help organization.
	 */
	public static final int HIGH_PRIORITY = 1;
	 
	/**
	 * Wrapper int for level 2 verbosity, used to help organization.
	 */
	public static final int DEBUG = 2;
	
	public OptionalPrinter() {}
	
	/**
	 * The recommended constructor which sets the {@linkplain #VERBOSITY} level.
	 * @param verbosity is the level to set {@linkplain #VERBOSITY} to.
	 */
	public OptionalPrinter(int verbosity) {
		VERBOSITY = verbosity;
	}
	
	/**
	 * Setter for {@link #VERBOSITY}
	 * @param verbosity is the new verbosity level
	 */
	public void setVerbosity(int verbosity) {
		VERBOSITY = verbosity;
	}
	
	/**
	 * Print with verbosity
	 * @param verbosity is the verbosity level of the message
	 * @param message is the message to print
	 */
	public void print(int verbosity, String message) {
		if( verbosity == VERBOSITY) System.out.println(message);
	}
}

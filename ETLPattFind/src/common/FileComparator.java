package common;

/**
 * Interface that ensures implementation of distance() method for comparing two
 * files.
 * @author Juraj
 */
public interface FileComparator {

	/**
	 * Compares two files and returns normalized distance between them.
	 * @param f1 first file
	 * @param f2 second file
	 * @return normalized distance, i.e. between 0.0 and 1.0 (including)
	 */
	public double distance(File f1, File f2);
	
}

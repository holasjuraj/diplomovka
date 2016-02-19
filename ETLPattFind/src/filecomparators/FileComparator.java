package filecomparators;

import common.File;

/**
 * Interface that ensures implementation of distance() method for comparing two
 * files.
 * @author Juraj
 */
public abstract class FileComparator {

	/**
	 * Compares two files and returns normalized distance between them.
	 * @param f1 first file
	 * @param f2 second file
	 * @return normalized distance, i.e. between 0.0 and 1.0 (including)
	 */
	public abstract double distance(File file1, File file2);
	
	public static double normalizeDist(int distance, int size1, int size2) {
		return 1.0 - ((double)(size1+size2-distance) / (double)(2*Math.max(size1, size2)));
	}
	
}

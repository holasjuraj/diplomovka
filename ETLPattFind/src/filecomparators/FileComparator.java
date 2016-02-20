package filecomparators;

import common.File;

/**
 * Interface that ensures implementation of distance() method for comparing two files.
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
	
	/**
	 * Converts the distance of files to normalized form, i.e. between 0.0 and 1.0 (including). The
	 * conversion preserves metric features including triangular inequality.
	 * @param distance distance of files in any metric
	 * @param size1 size of file1 - distance of file1 from fixed point
	 * @param size2 size of file2 - distance of file2 from fixed point
	 * @return normalized distance between 0.0 and 1.0 (including)
	 */
	public static double normalizeDist(int distance, int size1, int size2) {
		return 1.0 - ((double)(size1+size2-distance) / (double)(2*Math.max(size1, size2)));
	}
	
}

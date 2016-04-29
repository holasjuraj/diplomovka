package filecomparators;

import common.File;

/**
 * Umbrella class that ensures implementation of distance() method for comparing two {@link File}s.
 * @author Juraj
 */
public abstract class FileComparator {

	/**
	 * Compares two {@link File}s and returns normalized distance between them.
	 * @param f1 first file
	 * @param f2 second file
	 * @return normalized distance, i.e. between 0.0 and 1.0 (including)
	 */
	public abstract double distance(File file1, File file2);
	
	/**
	 * @return ID of subclass of {@link File}s, that is required by the comparator
	 */
	public abstract int getRequiredFileType();
	
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

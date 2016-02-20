package common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Container for storing comparisons for multiple files.
 * @author Juraj
 */
public class DistanceMatrix {
	private Map<Pair<Integer>, FileComparison> matrix = new HashMap<>();
	private Set<Integer> usedIds = new HashSet<>();
	private int numFiles;
	
	/**
	 * Adds a FileComparison to the matrix, increases counter of files if necessary.
	 * @param fc comparison to be added.
	 * @return FileComparison object that was assigned to compared pair of files, null if no such
	 * existed.
	 */
	public FileComparison put(FileComparison fc) {
		int[] ids = new int[]{
				fc.getFiles()[0].getId(),
				fc.getFiles()[1].getId()};
		for (int i = 0; i < 2; i++) {
			if (usedIds.add(ids[i])) {
				numFiles++;
			}
		}
		
		return matrix.put(new Pair<Integer>(ids[0], ids[1]), fc);
	}
	
	/**
	 * @return FileComparison object for files specified by their IDs.
	 */
	public FileComparison get(int id1, int id2) {
		int idLow  = id1;
		int idHigh = id2;
		if (id1 > id2) {
			idLow  = id2;
			idHigh = id1;
		}
		return matrix.get(new Pair<Integer>(idLow, idHigh));
	}

	/**
	 * @return FileComparison object for files passed as references.
	 */
	public FileComparison get(File f1, File f2) {
		return get(f1.getId(), f2.getId());
	}
	
}

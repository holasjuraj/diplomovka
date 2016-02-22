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
	
	/**
   * Adds a {@link FileComparison} to the matrix, increases counter of files if necessary. Function
   * is synchronized - safe reading from multiple threads.
   * @param fc comparison to be added.
   * @return {@link FileComparison} object that was assigned to compared pair of files, null if no
   *         such existed.
   */
	public synchronized FileComparison put(FileComparison fc) {
		int[] ids = new int[]{
				fc.getFiles()[0].getId(),
				fc.getFiles()[1].getId()};
		for (int i = 0; i < 2; i++) {
			usedIds.add(ids[i]); 
		}
		
		return matrix.put(new Pair<Integer>(ids[0], ids[1]), fc);
	}
	
	public String toString() {
	  StringBuilder sb = new StringBuilder("Files: ");
    for (Integer i : usedIds) {
      sb.append(i);
      sb.append(", ");
    }
    sb.append("\nPairs:");
    for (Map.Entry<Pair<Integer>, FileComparison> item : matrix.entrySet()) {
      sb.append(item.getValue());
      sb.append("\n");
    }
    return sb.toString();
	}
	
	/**
   * Finds {@link FileComparison} object for files specified by their IDs. Function is symmetric,
   * i.e. get(i, j) == get(j, i), and synchronized - safe reading from multiple threads.
   */
	public synchronized FileComparison get(int id1, int id2) {
		int idLow  = id1;
		int idHigh = id2;
		if (id1 > id2) {
			idLow  = id2;
			idHigh = id1;
		}
		return matrix.get(new Pair<Integer>(idLow, idHigh));
	}

	/**
   * Finds {@link FileComparison} object for files passed as references. Function is symmetric, i.e.
   * get(f1, f2) == get(f2, f1), and synchronized - safe reading from multiple threads.
   */
	public synchronized FileComparison get(File f1, File f2) {
		return get(f1.getId(), f2.getId());
	}
	
}

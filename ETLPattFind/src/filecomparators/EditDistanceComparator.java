package filecomparators;

import common.File;
import common.Main;
import common.SequenceFile;

/**
 * Implementation of Eugene W. Myers`s algorithm from his article
 * "An O(ND) Difference Algorithm and Its Variations" with early stopping and estimate of result.
 * @author Juraj
 */
public class EditDistanceComparator extends FileComparator {
	/**
	 * Default value for early stopping threshold (gives reasonable results).
	 */
	private static final double DEFAULT_ES_THRESHOLD = 0.1;
	/**
	 * If distance of files exceeds this value, algorithm execution quits and final result is
	 * approximated by partial results computed up to that moment. It applies to normalized metric, so
	 * it must be also normalized, i.e. 0.0 < EST <= 1.0 .
	 */
	public final double earlyStoppingThreshold;
	
	/**
	 * Initialize the comparator for given early stopping threshold (EST). If EST is out of normalized
	 * range (0,1> , default value (0.1) is set and warning is printed.
	 */
	public EditDistanceComparator(double earlyStoppingThreshold) {
		if (earlyStoppingThreshold <= 0 || earlyStoppingThreshold > 1) {
			this.earlyStoppingThreshold = DEFAULT_ES_THRESHOLD;
			System.out.println(
					"WARN: EditDistanceComparator.EditDistanceComparator: "
					+ "Early stopping thershold out of bounds, setting default value = "
					+ DEFAULT_ES_THRESHOLD + " .");
		} else {
			this.earlyStoppingThreshold = earlyStoppingThreshold;
		}
	}

  /**
   * Implementation of Eugene W. Myers`s algorithm from his article
   * "An O(ND) Difference Algorithm and Its Variations" with further improvements:
   * <li>MAX threshold is here computed from normalized early stopping threshold (EST)</li>
   * <li>if algorithm passes EST, it estimates the result based on previous partial results</li>
   * @return if result <= EST then it is exact distance of the files, otherwise it`s estimate of the
   *         distance. Result distance is normalized to interval (0.0, 1.0).
   * @throws {@link IllegalArgumentException} if input files are not type {@link SequenceFile}
	 * @see filecomparators.FileComparator#distance(common.File, common.File)
	 */
	@Override
	public double distance(File file1, File file2) {
	  if (!(file1 instanceof SequenceFile) || !(file2 instanceof SequenceFile)) {
	    System.out.println("ERROR: EditDistanceComparator.distance: Incompatable file types.");
	    throw new IllegalArgumentException();
	  }
    SequenceFile sFile1 = (SequenceFile) file1;
    SequenceFile sFile2 = (SequenceFile) file2;
		int n = sFile1.size();
  	int m = sFile2.size();
  	int max = (int) Math.ceil((double)(n + m) * earlyStoppingThreshold);
		double diagonal = Math.sqrt(n * n + m * m);
		int[] v = new int[2 * max + 1];	// int[-max ... max]
		int low = 1;
		int high = -1;
		
		int L = (int)Math.round((m + n) * 0.075);
		
		for (int d = 0; d <= max; d++) {
			int x = 0;
			int y = 0;
			int frDiag = 0; // number of furthest anti-diagonal
			low--;
			high++;
			for (int k = low; k <= high; k += 2) {
				x = 0;
				if (k == -d || (k != d && v[max + k - 1] < v[max + k + 1])) {
					x = v[max + k + 1];
				} else {
					x = v[max + k - 1] + 1;
				}
				y = x - k;
				while (x < n  &&  y < m  &&  sFile1.get(x).equals(sFile2.get(y))) {
					x++;
					y++;
				}
				v[max + k] = x;
				frDiag = Math.max(frDiag, x + y);
				if (x >= n  &&  y >= m) {
					return normalizeDist(d, n, m);
				}
			}

      while (2 * v[max + low] - low  <  frDiag - L) {
        low++;
      }
      while (2 * v[max + high] - high  <  frDiag - L) {
        high--;
      }
    }
		
		/* Edit distance is greater than max.
		 * Score approximates how close did we get to success in last run (when distance==d).
		 * If score == max then distance == d, if score == max/2 then distance ~~ d*2. 
		 */
		double score = 0;
		for (int k = -max; k < max + 1; k += 2) {
			int x = v[max + k];
			int y = x - k;
			double l = Math.sqrt((n - x) * (n - x)  +  (m - y) * (m - y));	// Distance by L2 norm
			score = Math.max(score, diagonal - l);
		}
		
		if (score == 0) {
			// Division by zero case
			return normalizeDist(n + m, n, m);
		} else {
			// Distance cannot be more than n+m, hence the Math.min(...)
			return normalizeDist(
			    Math.min(n + m, (int)Math.round( (double)max * diagonal / score )),
			    n, m);
    }
  }

  /**
   * @return ID of {@link SequenceFile} type
   * @see filecomparators.FileComparator#getRequiredFileType()
   */
  @Override
  public int getRequiredFileType() {
    return Main.FILETYPE_SEQUENCEFILE;
  }

}

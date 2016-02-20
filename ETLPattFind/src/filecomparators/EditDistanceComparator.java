package filecomparators;

import common.File;

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
	public EditDistanceComparator(int earlyStoppingThreshold) {
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
	 * - MAX threshold is here computed from normalized early stopping threshold (EST)
	 * - if algorithm passes EST, it estimates the result based on previous partial results
	 * @return if result <= EST then it is exact distance of the files, otherwise it`s estimate of the
	 * distance
	 */
	@Override
	public double distance(File file1, File file2) {
		int n = file1.size();
  	int m = file2.size();
  	int max = (int) Math.ceil((double)(n + m) * earlyStoppingThreshold);
		double diagonal = Math.sqrt(n * n + m * m);
		int[] v = new int[2 * max + 1];	// int[-max ... max]
		
		for (int d = 0; d < max + 1; d++) {
			int x = 0;
			int y = 0;
			for (int k = -d; k < d + 1; k += 2) {
				x = 0;
				if (k == -d || (k != d && v[max + k - 1] < v[max + k + 1])) {
					x = v[max + k + 1];
				} else {
					x = v[max + k - 1] + 1;
				}
				y = x - k;
				while (x < n  &&  y < m  &&  file1.get(x).equals(file2.get(y))) {
					x++;
					y++;
				}
				v[max + k] = x;
				if (x >= n  &&  y >= m) {
					return d;
				}
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
			return n + m;
		} else {
			// Distance cannot be more than n+m, hence the Math.min(...)
			return Math.min(
					n + m,
					(int)Math.round( (double)max * diagonal / score ));
		}
	}

}

package common;

/**
 * Container for holding distance of two {@link File}s, either exact or estimated (low and high
 * bound). Distances have to be normalized, e.g. between 0.0 and 1.0 (including). Files are stored
 * in a way, that file1 is the file with lower ID, file2 has higher ID.
 * @author Juraj
 */
public class FileComparison {
	private final File f1;
	private final File f2;
	private double lowBound = 0.0;
	private double highBound = 1.0;
	private boolean exact = false;
	
	/**
   * Initialize the comparison for two given files. It is initialized as estimate with lowBound == 0
   * and highBound == 1. Files are stored in a way, that file1 is the file with lower ID, file2 has
   * higher ID.
   * @throws {@link NullPointerException} if one or both of the files are null.
   */
	public FileComparison(File f1, File f2) {
		if (f1 == null || f2 == null) {
			System.out.println(
			    "ERROR: FileComparison.FileComparison: Initializing with null file"
					+ (f1 == null ? 1 : 2) + ".");
			throw new NullPointerException();
		}
		if (f1.getId() < f2.getId()) {
			this.f1 = f1;
			this.f2 = f2;
		} else {
			this.f1 = f2;
			this.f2 = f1;			
		}
	}
  
  /**
   * Initialize the comparison with exact distance.
   * @see FileComparison#FileComparison(File, File)
   */
  public FileComparison(File f1, File f2, double exactDistance) {
    this(f1, f2);
    setDistanceExact(exactDistance);
  }

  /**
   * Initialize the comparison with low and high bound of the distance.
   * @see FileComparison#FileComparison(File, File)
   */
  public FileComparison(File f1, File f2, double lowBound, double highBound) {
    this(f1, f2);
    try {
      setDistanceApprox(lowBound, highBound);
    }
    catch (IllegalArgumentException e) {
      System.out.println("WARN: FileComparison.FileComparison: Low bound " + lowBound
          + " is greater than high bound " + highBound + ".");
    }
  }

	/**
	 * @return two-field array of compared {@link File}s
	 */
	public File[] getFiles() {
		return new File[] {f1, f2};
	}
	
	public double getLowBound() {
		return lowBound;
	}
	
	public double getHighBound() {
		return highBound;
	}
	
	public boolean isExact() {
		return exact;
	}
	
	/**
	 * It is meant only for retrieving distance if comparison is exact. If it is not, method returns
	 * lowBound and prints a warning message.
	 * @return exact distance of compared files, or lowBound if not exact 
	 */
	public double getDistance() {
		if (!exact) {
			System.out.println(
			    "WARN: FileComparison.getDistance: retreiving exact distance from estimate record.");
		}
		return lowBound;
	}

	/**
	 * Sets the distance of {@link File}s, and marks the comparison as exact.
	 */
	public void setDistanceExact(double distance) {
		checkDistRange(distance);
		lowBound  = distance;
		highBound = distance;
		exact = true;
	}

	/**
   * Sets the bounds of estimated distance of the {@link File}s, and marks the comparison as
   * estimate.
	 * @throws IllegalArgumentException if lowBound > highBound
	 */
	public void setDistanceApprox(double lowBound, double highBound) throws IllegalArgumentException {
		checkDistRange(lowBound);
		checkDistRange(highBound);
		if (highBound < lowBound) {
		  throw new IllegalArgumentException("Low bound " + lowBound + " is greater than high bound "
		      + highBound + ".");
		}
		this.lowBound  = lowBound;
		this.highBound = highBound;
		exact = false;
	}

	/**
	 * Checks if distance is within allowed range <0, 1>. If not, prints a warning message.
	 */
	private void checkDistRange(double distance) {
		if (distance < 0 || distance > 1) {
			System.out.println("WARN: FileComparison.checkDistRange: Distance " + distance
			    + " out of range <0, 1>.");
		}
	}

	public String toString() {
	  if (exact) {
	    return "{" + f1.getId() + "," + f2.getId() + " = " + lowBound + "}";
	  } else {
      return "{" + f1.getId() + "," + f2.getId() + " = <" + lowBound + "," + highBound + ">}";
	  }
	}
	
	/**
   * Creates a {@link FileComparison} object for comparing the same {@link File}, with exact
   * distance of 0.
   */
	public static FileComparison getSelfComparison(File f){
		FileComparison fc = new FileComparison(f, f);
		fc.setDistanceExact(0.0);
		return fc;
	}
	
}

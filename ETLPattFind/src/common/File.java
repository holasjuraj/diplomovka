package common;

/**
 * Object holding information about single file.
 * @author Juraj
 */
public abstract class File {
	private final int id;
	private final String name;

	/**
	 * @param id must be unique (same IDs are considered as same files)
	 */
	public File(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

  /**
   * Two files are equal iff they have same ID.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof File)) {
      return false;
    }
    return ((File)obj).id == id;
  }

  /**
   * Hash code is generated only from ID of the file.
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }
	
	

}

package common;

/**
 * Object holding information about single file.
 * @author Juraj
 */
public abstract class File {
	private final int id;
	private final String name;

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

}

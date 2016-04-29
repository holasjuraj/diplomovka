package common;

/**
 * Encapsulation of ordered pair of two elements (objects) of the same type. Class is hash-table
 * friendly thanks to implemented hashCode() and equals().
 * @author Juraj
 * @param <T> class of elements
 */
public class Pair<T> {
	public static final int HASH_PRIME = 49157;
	public T elem1;
	public T elem2;
	
	/**
	 * Initializes both elements to null.
	 */
	public Pair() {
		this(null, null);
	}
	
	public Pair(T elem1, T elem2) {
		this.elem1 = elem1;
		this.elem2 = elem2;
	}
	
	@Override
	public int hashCode() {
		return elem1.hashCode() + HASH_PRIME * elem2.hashCode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		return obj instanceof Pair
				&& elem1.equals(((Pair<T>)obj).elem1)
				&& elem2.equals(((Pair<T>)obj).elem2);
	}
	
}

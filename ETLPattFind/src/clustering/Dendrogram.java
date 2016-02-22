package clustering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.File;

/**
 * Dendrogram tree representation.
 * @author Juraj
 */
public class Dendrogram implements Iterable<File> {
	private Dendrogram left  = null;
	private Dendrogram right = null;
	private double distance  = 1.0;
	/* TODO Change copying all elements from child nodes, modify size() and iterator() to work with
	 * this.
	 */
	public List<File> files;
	
	/**
	 * Initiates the {@link Dendrogram} node as leaf - with reference to a file.
	 */
	public Dendrogram(File file){
		if (file == null) {
			System.out.println("ERROR: Dendrogram.Dendrogram: Null file.");
		}
		files = new ArrayList<>();
		files.add(file);
	}
	
  /**
   * Initiates the {@link Dendrogram} node as inner node - with left and right children, and
   * connecting distance between them.
   */
	public Dendrogram(Dendrogram left, Dendrogram right, double distance){
		this.left  = left;
		this.right = right;
		this.distance = distance;
		files = new ArrayList<>(left.size() + right.size());
		files.addAll(left.files);
		files.addAll(right.files);
	}
	
  /**
   * Determines if node is a leaf node, and therefore referring to single file.
   */
	public boolean isLeaf(){
		return left == null && right == null;
	}
	
	/**
	 * @return String representation using only IDs of the files.
	 */
	public String toStringIds(){
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(files.get(i).getId());
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * @return String representation using names of the files.
	 */
	public String toStringNames(){
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(files.get(i).getName());
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * @return number of files contained in this subtree.
	 */
	public int size() {
		return (files == null) ? 0 : files.size(); 
	}

	public Dendrogram getLeft() {
		return left;
	}
	
	public Dendrogram getRight() {
		return right;
	}
	
	public double getDistance() {
		return distance;
	}
	
	@Override
	public Iterator<File> iterator() {
		return files.iterator();
	}
	
}

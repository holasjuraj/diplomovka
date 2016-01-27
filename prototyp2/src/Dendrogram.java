import java.util.ArrayList;
import java.util.List;

public class Dendrogram {
	public Dendrogram left = null, right = null;
	public double dist = 1;
	public List<Integer> items;
	
	public Dendrogram(int item){
		items = new ArrayList<Integer>();
		items.add(item);
	}
	
	public Dendrogram(Dendrogram left, Dendrogram right, double dist){
		this.left = left;
		this.right = right;
		this.dist = dist;
		items = new ArrayList<Integer>(left.items.size() + right.items.size());
		items.addAll(left.items);
		items.addAll(right.items);
	}
	
	public boolean isItem(){
		return left == null && right == null;
	}
	
	public String toString(){
		return items.toString();
	}
	
	public String toStringFiles(String[] fileNames){
		StringBuilder sb = new StringBuilder("[");
		for(int i = 0; i < items.size(); i++){
			if(i > 0){ sb.append(", "); }
			sb.append(fileNames[items.get(i)]);
		}
		sb.append("]");
		return sb.toString();
	}
}
package mayersES;

public class Main {
	public static final String DATA_PATH = "data/set2";
		
	public static void main(String[] args) {
		System.out.println("Dataset: "+DATA_PATH);
		FileComparator.compareDir(DATA_PATH, FileComparator.METHOD_PER_TOKEN);
	}

}
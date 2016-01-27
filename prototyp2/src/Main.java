import java.util.List;

public class Main {
	public static final String DATA_PATH = "data/set1";

	public static void main(String[] args) {
		String[] clustMethods = {"UPGMA", "C-link", "S-link"};
		System.out.println();
		double[][] dist = FileComparator.compareDir(DATA_PATH);
		String[] fileNames = FileComparator.lastFilesList;
		for(int clustMethod = 0; clustMethod < 3; clustMethod++){
			System.out.println();
			List<Dendrogram> clustering = HAC.clusterize(dist, clustMethod, 0.1);
			HAC.sortClusters(clustering);
			
			System.out.println("clustering: "+clustMethods[clustMethod]);
			for(Dendrogram cluster : clustering){
				System.out.println(cluster);
//					System.out.println(cluster.toStringFiles(fileNames));
			}
		}
	}

}
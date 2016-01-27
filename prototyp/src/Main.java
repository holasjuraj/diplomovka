import java.util.List;

public class Main {
	public static final String DATA_PATH = "data/set1";

	public static void main(String[] args) {
		String[] compMethods = {"per char", "per line", "per token", "combination char+line"};
		String[] clustMethods = {"UPGMA", "C-link", "S-link"};
		for(int compMethod = 3; compMethod < 4; compMethod++){
			System.out.println();
			double[][] dist = FileComparator.compareDir(DATA_PATH, compMethod);
			String[] fileNames = FileComparator.lastFilesList;
			for(int clustMethod = 0; clustMethod < 3; clustMethod++){
				System.out.println();
				List<Dendrogram> clustering = HAC.clusterize(dist, clustMethod, 0.1);
				HAC.sortClusters(clustering);
				
				System.out.println("comparing: " + compMethods[compMethod] + ", clustering: "+clustMethods[clustMethod]);
				for(Dendrogram cluster : clustering){
					System.out.println(cluster);
//					System.out.println(cluster.toStringFiles(fileNames));
				}
			}
		}
	}

}
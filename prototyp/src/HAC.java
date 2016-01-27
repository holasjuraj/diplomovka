import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HAC {
	public static final int METHOD_UPGMA = 0;
	public static final int METHOD_CLINK = 1;
	public static final int METHOD_SLINK = 2;

	public static List<Dendrogram> clusterize(double[][] distMatrix, int method, double stopCond){
		System.out.println("Clustering started");
		Date start = new Date();
		// initialize all items as clusters
		List<Dendrogram> clustering = new ArrayList<Dendrogram>(distMatrix.length);
		for(int i = 0; i < distMatrix.length; i++){
			clustering.add(new Dendrogram(i));
		}
		
		// merge to one cluster
		double lastMergeDist = 0;					// STOPPING 1
		double maxMergeDistGap = 0;					// STOPPING 1
		List<Dendrogram> bestClustering = new ArrayList<Dendrogram>(distMatrix.length);
		bestClustering.addAll(clustering);
		
		while(clustering.size() > 1){
			double minDist = Double.MAX_VALUE;		// minimal distance between clusters A and B 
			int minA = 0, minB = 1;					// indexes of clusters A and B with minimal distance
			for(int ia = 0; ia < clustering.size(); ia++){
				for(int ib = ia+1; ib < clustering.size(); ib++){
					double d = 1;
					switch (method) {
						case METHOD_UPGMA: d = UPGMA(clustering.get(ia), clustering.get(ib), distMatrix); break;
						case METHOD_CLINK: d = cLink(clustering.get(ia), clustering.get(ib), distMatrix); break;
						case METHOD_SLINK: d = sLink(clustering.get(ia), clustering.get(ib), distMatrix); break;
						default: d = UPGMA(clustering.get(ia), clustering.get(ib), distMatrix); break;
					}
					if(d < minDist){
						// new best choice for merge
						minDist = d;
						minA = ia;
						minB = ib;
					}
				}
			}
			
			double mergeDist = minDist;
			
			if(stopCond == 0){
				// STOPPING 1: remember state before largest merging distance
				if((mergeDist - lastMergeDist) > maxMergeDistGap){
					maxMergeDistGap = mergeDist - lastMergeDist;
					bestClustering = new ArrayList<Dendrogram>(clustering.size());
					bestClustering.addAll(clustering);
					System.out.println(maxMergeDistGap);
				}
			}
			else{
				// STOPPING 2: return if merge distance reaches threshold
				if(mergeDist > stopCond){
					System.out.println("Clustering finished, total time: " + ((new Date().getTime()) - start.getTime()) + "ms");
					return clustering;
				}
				bestClustering = clustering;
			}
			
			Dendrogram	a = clustering.get(minA),
						b = clustering.get(minB);
			Dendrogram newCluster = new Dendrogram(a, b, mergeDist);
			lastMergeDist = mergeDist;
			clustering.remove(minB);
			clustering.remove(minA);
			clustering.add(newCluster);
			
//			System.out.println("new cluster:" + newCluster.items);
		}

		System.out.println("Clustering finished, total time: " + ((new Date().getTime()) - start.getTime()) + "ms");
		return bestClustering;
	}
	
	public static void sortClusters(List<Dendrogram> clustering) {
		for(Dendrogram cluster : clustering){
			Collections.sort(cluster.items);
		}
		Collections.sort(clustering, new Comparator<Dendrogram>() {
			public int compare(Dendrogram a, Dendrogram b) {
				int lengths = Integer.compare(a.items.size(), b.items.size());
				if(lengths != 0){ return -lengths; }
				return Integer.compare(a.items.get(0), b.items.get(0));
			}
		});
	}
	
	private static double UPGMA(Dendrogram a, Dendrogram b, double[][] dist){
		double dSum = 0;
		for(int ai : a.items){
			for(int bi : b.items){
				dSum += dist[ai][bi];
			}
		}
		return dSum / (double)(a.items.size() * b.items.size());
	}
	
	private static double cLink(Dendrogram a, Dendrogram b, double[][] dist){
		double dMax = 0;
		for(int ai : a.items){
			for(int bi : b.items){
				dMax = Math.max(dMax, dist[ai][bi]);
			}
		}
		return dMax;
	}
	
	private static double sLink(Dendrogram a, Dendrogram b, double[][] dist){
		double dMin = Double.MAX_VALUE;
		for(int ai : a.items){
			for(int bi : b.items){
				dMin = Math.min(dMin, dist[ai][bi]);
			}
		}
		return dMin;
	}

}
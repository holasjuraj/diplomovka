package clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;

public class HAC {
	public static final int METHOD_UPGMA = 0;
	public static final int METHOD_CLINK = 1;
	public static final int METHOD_SLINK = 2;
	
	private final int joinMethod;
	
	public HAC() {
		this(METHOD_UPGMA);
	}
	
	public HAC(int joinMethod) {
		this.joinMethod = joinMethod;
	}

	public List<Dendrogram> clusterize(
	    List<File> files, DistanceMatrix distMatrix, double stopCondition) {
		System.out.println("INFO: Clustering started");
		Date start = new Date();
		
		// Initialize all files as clusters
		List<Dendrogram> clustering = new ArrayList<Dendrogram>(files.size());
		for(File f : files){
			clustering.add(new Dendrogram(f));
		}
		
		// Merge to one cluster
		while (clustering.size() > 1) {
			double minDist = Double.MAX_VALUE;	// Minimal distance between clusters A and B		 
			int minA = 0, minB = 1;		          // Indexes of clusters A and B with minimal distance
			for (int ia = 0; ia < clustering.size(); ia++) {
				for (int ib = ia + 1; ib < clustering.size(); ib++) {
					double d = 1.0;
					switch (joinMethod) {
						case METHOD_CLINK:
							d = cLink(clustering.get(ia), clustering.get(ib), distMatrix);
							break;
						case METHOD_SLINK:
							d = sLink(clustering.get(ia), clustering.get(ib), distMatrix);
							break;
						case METHOD_UPGMA:	// fall-through
						default:
							d = upgma(clustering.get(ia), clustering.get(ib), distMatrix);
					}
					if (d < minDist) {
						// new best choice for merge
						minDist = d;
						minA = ia;
						minB = ib;
					}
				}
			}
			
			double mergeDist = minDist;
			
			// Stop condition - return if merge distance reaches given threshold
			if (mergeDist > stopCondition) {
				System.out.println("INFO: Clustering finished, time: "
				    + ((new Date().getTime()) - start.getTime()) + "ms");
				return clustering;
			}
			
			// Merge two clusters
			Dendrogram a = clustering.get(minA);
			Dendrogram b = clustering.get(minB);
			Dendrogram newCluster = new Dendrogram(a, b, mergeDist);
			clustering.remove(minB);
			clustering.remove(minA);
			clustering.add(newCluster);
			
			System.out.println("INFO: Clustering progress: " + (mergeDist / stopCondition * 100.0) + "%");
		}

		// If all files merged within merging threshold
		System.out.println("INFO: Clustering finished, time: "
		    + ((new Date().getTime()) - start.getTime()) + "ms");
		return clustering;
	}
	
	public static void sortClusters(List<Dendrogram> clustering) {
		for(Dendrogram cluster : clustering){
			Collections.sort(cluster.files, new Comparator<File>() {
				@Override
				public int compare(File a, File b) {
					return Integer.compare(a.getId(), b.getId());
				}
			});
		}
		
		Collections.sort(clustering, new Comparator<Dendrogram>() {
			@Override
			public int compare(Dendrogram a, Dendrogram b) {
				int lengths = Integer.compare(a.size(), b.size());
				if (lengths != 0) {
					return -lengths;
				}
				return Integer.compare(a.files.get(0).getId(), b.files.get(0).getId());
			}
		});
	}
	
	private static double upgma(Dendrogram a, Dendrogram b, DistanceMatrix dist){
		double dSum = 0;
		for (File fa : a) {
			for (File fb : b) {
				FileComparison d = dist.get(fa, fb);
				if (d.isExact()) {
					dSum += d.getDistance();
				} else { /* TODO */ }
			}
		}
		return dSum / (double)(a.size() * b.size());
	}
	
	private static double cLink(Dendrogram a, Dendrogram b, DistanceMatrix dist){
		double dMax = 0;
		for (File fa : a) {
			for (File fb : b) {
				FileComparison d = dist.get(fa, fb);
				if (d.isExact()) {
					dMax = Math.max(dMax, d.getDistance());
				} else { /* TODO */ }
			}
		}
		return dMax;
	}
	
	private static double sLink(Dendrogram a, Dendrogram b, DistanceMatrix dist){
		double dMin = Double.MAX_VALUE;
		for (File fa : a) {
			for (File fb : b) {
				FileComparison d = dist.get(fa, fb);
				if (d.isExact()) {
					dMin = Math.min(dMin, d.getDistance());
				} else { /* TODO */ }
			}
		}
		return dMin;
	}

	public int getJoinMethod() {
		return joinMethod;
	}
	
}
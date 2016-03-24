package clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;

/**
 * Hierarchical Agglomerative Clustering - tools for splitting list of files into clusters of
 * similar files.
 * @author Juraj
 */
public class HAC {
	public static final int METHOD_UPGMA = 0;
	public static final int METHOD_CLINK = 1;
	public static final int METHOD_SLINK = 2;
	
	private final int joinMethod;
	
	/**
	 * Initialize {@link HAC} with default joining method - UPGMA.
	 */
	public HAC() {
		this(METHOD_UPGMA);
	}
	
	/**
	 * Initialize {@link HAC} with selected joining method.
	 */
	public HAC(int joinMethod) {
		this.joinMethod = joinMethod;
	}

	/**
	 * Given files list and corresponding distance matrix, split all files into clusters of similar
	 * files. Function iteratively merges two most-similar clusters, until the merging distance
	 * exceeds given threshold (stopCondition).
	 * @param files list of files to be split into clusters
	 * @param distMatrix distance matrix - must contain comparisons for all pairs of files
	 * @param stopCondition maximal distance of two clusters that can be merged into one
	 * @return list of {@link Dendrogram}s representing individual clusters
	 */
	public List<Dendrogram> clusterize(
	    List<File> files, DistanceMatrix distMatrix, double stopCondition) {
		System.out.println("INFO: Clustering started");
		Date start = new Date();
		
		// Initialize all files as clusters
		List<Dendrogram> clustering = new ArrayList<Dendrogram>(files.size());
		for(File f : files){
			clustering.add(new Dendrogram(f));
		}
		
		// Initialize distances, fill cluster.nearest queues
    for (int ia = 0; ia < clustering.size(); ia++) {
      for (int ib = ia + 1; ib < clustering.size(); ib++) {
        Dendrogram a = clustering.get(ia);
        Dendrogram b = clustering.get(ib);
        ClusterDistance d = getClusterDist(a, b, distMatrix, joinMethod);
        a.nearest.add(d);
        b.nearest.add(d);
      }
    }
		
    // Merge to one cluster
    while (clustering.size() > 1) {
      // Find best match (closest clusters)
      ClusterDistance best = new ClusterDistance(null, null, Double.MAX_VALUE, Double.MAX_VALUE);
      for (Dendrogram c : clustering) {
        ClusterDistance queueHead = c.nearest.peek();
        if (queueHead.lowBound < best.lowBound) {
          best = queueHead;
        }
      }
      // Stop condition - return if merge distance reaches given threshold
      if (best.lowBound > stopCondition) {
        System.out.println("INFO: Clustering finished, time: "
            + ((new Date().getTime()) - start.getTime()) + "ms");
        return clustering;
      }
      // Merge clusters
      Dendrogram newClus = mergeClusters(clustering, best);
      if (clustering.size() % 10 == 0) {
        System.out.printf("INFO: Clustering progress: %.2f%%\n",
            (best.lowBound / stopCondition * 100.0));
      }
      // Update cluster.nearest queues
      for (Dendrogram c : clustering) {
        if (c == newClus) {
          continue;
        }
        queueRemoveMerged(c, best);
        ClusterDistance newDist = getClusterDist(c, newClus, distMatrix, joinMethod);
        c.nearest.add(newDist);
        newClus.nearest.add(newDist);
      }
    }
		
		// If all files merged within merging threshold
		System.out.println("INFO: Clustering finished, time: "
		    + ((new Date().getTime()) - start.getTime()) + "ms");
		return clustering;
	}
	
	/**
	 * Sorts the clustering - useful for comparing and outputting. Sorting is two-leveled:
	 * <li>each cluster is sorted based on file IDs (low to high)</li>
	 * <li>clusters are sorted based their size (high to low), if two clusters are the same size then
	 * based on the ID of their first file (low to high)</li>
	 * Function modifies input structure.
	 * @param clustering list of {@link Dendrogram}s representing individual clusters
	 */
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
	
	private static ClusterDistance getClusterDist(
	    Dendrogram a, Dendrogram b, DistanceMatrix distMatrix, int joinMethod) {
    double d = 1.0;
    switch (joinMethod) {
      case METHOD_CLINK:
        d = cLink(a, b, distMatrix);
        break;
      case METHOD_SLINK:
        d = sLink(a, b, distMatrix);
        break;
      case METHOD_UPGMA:  // fall-through
      default:
        d = upgma(a, b, distMatrix);
    }
    return new ClusterDistance(a, b, d, d);
	}
	
	private static Dendrogram mergeClusters(List<Dendrogram> clustering, ClusterDistance dist) {
    Dendrogram a = dist.clusterA;
    Dendrogram b = dist.clusterB;
    Dendrogram newCluster = new Dendrogram(a, b, dist.lowBound);
    clustering.remove(a);
    clustering.remove(b);
    clustering.add(newCluster);
    return newCluster;
	}
	
	private static void queueRemoveMerged(Dendrogram cluster, ClusterDistance merged) {
    Dendrogram a = merged.clusterA;
    Dendrogram b = merged.clusterB;
    for (Iterator<ClusterDistance> it = cluster.nearest.iterator(); it.hasNext(); ) {
      ClusterDistance d = it.next();
      if (d.clusterA == a || d.clusterB == a || d.clusterA == b || d.clusterB == b) {
        it.remove();
      }
    }
	}
	
	/**
	 * Function for comparing distance of two clusters using UPGMA method (Unweighted Pair Group
	 * Method with Arithmetic Mean) - arithmetic average of distances of all pairs from cluster A and
	 * cluster B.
	 * @param a cluster A
	 * @param b cluster B
	 * @param dist distance matrix - must contain comparisons for all pairs of files
	 * @return UPGMA distance of clusters
	 */
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
	
	/**
	 * Function for comparing distance of two clusters using C-Link method (Complete Linkage) -
	 * maximal distance of two points from cluster A and cluster B.
   * @param a cluster A
   * @param b cluster B
   * @param dist distance matrix - must contain comparisons for all pairs of files
   * @return C-Link distance of clusters
	 */
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
	
	/**
   * Function for comparing distance of two clusters using S-Link method (Single Linkage) -
   * minimal distance of two points from cluster A and cluster B.
   * @param a cluster A
   * @param b cluster B
   * @param dist distance matrix - must contain comparisons for all pairs of files
   * @return S-Link distance of clusters
	 */
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
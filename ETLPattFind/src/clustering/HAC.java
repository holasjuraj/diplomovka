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
import filecomparators.FileComparator;

/**
 * Hierarchical Agglomerative Clustering - tools for splitting list of files into clusters of
 * similar files.
 * @author Juraj
 */
public class HAC {
	public static final int METHOD_UPGMA = 0;
	public static final int METHOD_CLINK = 1;
	public static final int METHOD_SLINK = 2;

	private final DistanceMatrix distMatrix;
	private final FileComparator comparator;
	private final int joinMethod;
	
	/**
	 * Initialize {@link HAC} with default joining method - UPGMA.
	 */
	public HAC(DistanceMatrix distMatrix, FileComparator comparator) {
		this(METHOD_UPGMA, distMatrix, comparator);
	}
	
	/**
	 * Initialize {@link HAC} with selected joining method.
	 */
	public HAC(int joinMethod, DistanceMatrix distMatrix, FileComparator comparator) {
		this.joinMethod = joinMethod;
		this.distMatrix = distMatrix;
		this.comparator = comparator;
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
	public List<Dendrogram> clusterize(List<File> files, double stopCondition) {
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
        ClusterDistance d = getClusterDist(a, b);
        if (d.lowBound <= stopCondition) {    // Don`t need to store large distances
          a.nearest.add(d);
          b.nearest.add(d);
        }
      }
    }
		
    
    // Merge to one cluster
    while (clustering.size() > 1) {
      // Find closest clusters
      ClusterDistance closest = findClosestClusters(clustering, stopCondition);
      
      // Stop condition:
      //  -> if closest==null => no merging with lowBound < stopCondition was found =>
      //     merging threshold was exceeded
      //  -> if closest.highBound > stopCondition => all other mergings have lowBound even higher =>
      //     merging threshold was exceeded
      if (closest == null || closest.highBound > stopCondition) {
        System.out.println("INFO: Clustering finished, time: "
            + ((new Date().getTime()) - start.getTime()) + "ms");
        return clustering;
      }
      
      // Merge closest clusters
//      System.out.println("MERGING "+closest);
      Dendrogram newClus = mergeClusters(clustering, closest);
      if (clustering.size() % 10 == 0) {
        System.out.printf("INFO: Clustering progress: %.2f%%\n",
            (closest.lowBound / stopCondition * 100.0));
      }
      
      // Update cluster.nearest queues
      for (Dendrogram otherClus : clustering) {
        if (otherClus == newClus) {
          continue;
        }
        queueRemoveMerged(otherClus, closest);
        ClusterDistance newDist = getClusterDist(otherClus, newClus);
        if (newDist.lowBound <= stopCondition) {    // Don`t need to store large distances
          otherClus.nearest.add(newDist);
          newClus.nearest.add(newDist);
        }
      }
    }
		
		// If all files merged within merging threshold
		System.out.println("INFO: Clustering finished, time: "
		    + ((new Date().getTime()) - start.getTime()) + "ms");
		return clustering;
	}

	//// SUPPORT METHODS FOR CLUSTERING ////
	private ClusterDistance getClusterDist(Dendrogram a, Dendrogram b) {
    switch (joinMethod) {
      case METHOD_CLINK:
        return cLink(a, b);
      case METHOD_SLINK:
        return sLink(a, b);
      case METHOD_UPGMA:  // fall-through
      default:
        return upgma(a, b);
    }
	}
	
	private ClusterDistance findClosestClusters(List<Dendrogram> clustering, double stopCondition) {
	  // Find two closest pairs
	  PriorityQueue<ClusterDistance> candidates = new PriorityQueue<>(clustering.size());
    for (Dendrogram c : clustering) {
      ClusterDistance queueHead = c.nearest.peek();
      if (queueHead != null && queueHead != candidates.peek()) {
        candidates.add(queueHead);
      }
    }
    ClusterDistance first = candidates.poll();
    ClusterDistance second = candidates.poll();
    
    // Return if they do not overlap (or exist)
    if (second == null || first.highBound <= second.lowBound) {
      return first;
    }
    
    // Compute exact distances between files
    Dendrogram clusterA = first.clusterA;
    Dendrogram clusterB = first.clusterB;
    for (File fa : clusterA.files) {
      for (File fb : clusterB.files) {
        FileComparison filesDist = distMatrix.get(fa, fb);
        if (!filesDist.isExact()) {
          filesDist.setDistanceExact(comparator.distance(fa, fb));
        }
      }
    }
    
    // Update cluster distance
    ClusterDistance newFirst = getClusterDist(clusterA, clusterB);
    clusterA.nearest.remove(first);
    clusterB.nearest.remove(first);
    if (newFirst.lowBound <= stopCondition) {
      clusterA.nearest.add(newFirst);
      clusterB.nearest.add(newFirst);
    }

    // Try again
    return findClosestClusters(clustering, stopCondition);
	}
	
	private Dendrogram mergeClusters(List<Dendrogram> clustering, ClusterDistance dist) {
    Dendrogram a = dist.clusterA;
    Dendrogram b = dist.clusterB;
    Dendrogram newCluster = new Dendrogram(a, b, dist.lowBound);
    clustering.remove(a);
    clustering.remove(b);
    clustering.add(newCluster);
    return newCluster;
	}
	
	private void queueRemoveMerged(Dendrogram cluster, ClusterDistance merged) {
    Dendrogram a = merged.clusterA;
    Dendrogram b = merged.clusterB;
    for (Iterator<ClusterDistance> it = cluster.nearest.iterator(); it.hasNext(); ) {
      ClusterDistance d = it.next();
      if (d.clusterA == a || d.clusterB == a || d.clusterA == b || d.clusterB == b) {
        it.remove();
      }
    }
	}
	
	//// JOINING METHODS ////
	/**
	 * Function for comparing distance of two clusters using UPGMA method (Unweighted Pair Group
	 * Method with Arithmetic Mean) - arithmetic average of distances of all pairs from cluster A and
	 * cluster B.
	 * @param a cluster A
	 * @param b cluster B
	 * @param dist distance matrix - must contain comparisons for all pairs of files
	 * @return UPGMA distance of clusters
	 */
	private ClusterDistance upgma(Dendrogram a, Dendrogram b){
    double lbSum = 0;
    double hbSum = 0;
		for (File fa : a) {
			for (File fb : b) {
				FileComparison d = distMatrix.get(fa, fb);
        lbSum += d.getLowBound();
        hbSum += d.getHighBound();
			}
		}
		double count = a.size() * b.size();
		return new ClusterDistance(a, b, lbSum / count, hbSum / count);
	}
	
	/**
	 * Function for comparing distance of two clusters using C-Link method (Complete Linkage) -
	 * maximal distance of two points from cluster A and cluster B.
   * @param a cluster A
   * @param b cluster B
   * @param dist distance matrix - must contain comparisons for all pairs of files
   * @return C-Link distance of clusters
	 */
	private ClusterDistance cLink(Dendrogram a, Dendrogram b){
    double lbMax = 0;
    double hbMax = 0;
		for (File fa : a) {
			for (File fb : b) {
				FileComparison d = distMatrix.get(fa, fb);
        lbMax = Math.max(lbMax, d.getLowBound());
        hbMax = Math.max(hbMax, d.getHighBound());
			}
		}
    return new ClusterDistance(a, b, lbMax, hbMax);
	}
	
	/**
   * Function for comparing distance of two clusters using S-Link method (Single Linkage) -
   * minimal distance of two points from cluster A and cluster B.
   * @param a cluster A
   * @param b cluster B
   * @param dist distance matrix - must contain comparisons for all pairs of files
   * @return S-Link distance of clusters
	 */
	private ClusterDistance sLink(Dendrogram a, Dendrogram b){
    double lbMin = Double.MAX_VALUE;
    double hbMin = Double.MAX_VALUE;
		for (File fa : a) {
			for (File fb : b) {
				FileComparison d = distMatrix.get(fa, fb);
        lbMin = Math.min(lbMin, d.getLowBound());
        hbMin = Math.min(hbMin, d.getHighBound());
			}
		}
    return new ClusterDistance(a, b, lbMin, hbMin);
	}

  //// OTHER PUBLIC METHODS ////
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
  
	public int getJoinMethod() {
		return joinMethod;
	}
	
}
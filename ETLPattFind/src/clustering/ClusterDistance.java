package clustering;

class ClusterDistance implements Comparable<ClusterDistance> {
  Dendrogram clusterA;
  Dendrogram clusterB;
  double lowBound;
  double highBound;
  
  public ClusterDistance(Dendrogram cA, Dendrogram cB, double lb, double hb) {
    clusterA = cA;
    clusterB = cB;
    lowBound = lb;
    highBound = hb;
  }

  @Override
  public int compareTo(ClusterDistance o) {
    return Double.compare(lowBound, o.lowBound);
  }

}

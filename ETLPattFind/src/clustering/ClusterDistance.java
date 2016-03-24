package clustering;

/**
 * Structure for holding distance bounds of two clusters. Natural ordering according to lower bound
 * is provided.
 * @author Juraj
 */
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

  /**
   * Compares lower bounds.
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(ClusterDistance o) {
    return Double.compare(lowBound, o.lowBound);
  }
  
  @Override
  public String toString() {
    return clusterA.toStringIds() + ":" + clusterB.toStringIds() + " = <" + lowBound + ", "
        + highBound + ">";
  }

}

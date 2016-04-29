package workers;

import java.util.List;

import common.DistanceMatrix;
import common.File;
import common.Parameters;
import filecomparators.FileComparator;

/**
 * Umbrella class for worker managers. A worker manager is managing a set of workers of its own
 * type, creates their threads and provides tasks for them. Manager and its workers are used to
 * compare {@link File}s and fill the {@link DistanceMatrix}. Main task of manager is running
 * {@link WorkerManager#compareFiles(List, DistanceMatrix, FileComparator)} method.
 * @author Juraj
 */
public abstract class WorkerManager {
  protected DistanceMatrix distMatrix;
  protected FileComparator comparator;
  protected List<Thread> workerPool;
  final Parameters params;
    
  /**
   * Create manager with given set of parameters.
   * @param params
   */
  public WorkerManager(Parameters params) {
    this.params = params;
  }

  /**
   * Takes list of {@link File}s, compares every pair from the list using given
   * {@link FileComparator}, and fills the results into {@link DistanceMatrix}.
   */
  public abstract void compareFiles(
      List<File> files, DistanceMatrix distMatrix, FileComparator comparator);
  
  /**
   * Wait until all workers finish their work. Use this method only when all workers have been
   * launched.
   */
  protected void waitForWorkers() {
    try {
      for (Thread w : workerPool) {
        w.join();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  DistanceMatrix getDistanceMatrix() {
    return distMatrix;
  }
  
  FileComparator getComparator() {
    return comparator;
  }
  
}

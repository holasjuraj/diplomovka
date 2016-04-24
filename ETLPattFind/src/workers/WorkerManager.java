package workers;

import java.util.List;

import common.DistanceMatrix;
import common.File;
import common.Parameters;
import filecomparators.FileComparator;

public abstract class WorkerManager {
  protected DistanceMatrix distMatrix;
  protected FileComparator comparator;
  protected List<Thread> workerPool;
  final Parameters params;
    
  public WorkerManager(Parameters params) {
    this.params = params;
  }

  public abstract void compareFiles(
      List<File> files, DistanceMatrix distMatrix, FileComparator comparator);
  
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

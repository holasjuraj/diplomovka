package workers;

import java.util.ArrayList;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;

/**
 * Worker for {@link WorkerManagerNaive}.
 * @author Juraj
 */
public class WorkerNaive extends Thread {  
  private final WorkerManagerNaive manager;
  private volatile double status;
  
  /**
   * Initialization of worker.
   * @param manager object managing my execution and tasks
   */
  public WorkerNaive(WorkerManagerNaive manager) {
    this.manager = manager;
  }

  /**
   * Start working and proactively take and execute tasks. Quit if no more tasks are available.
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    WorkerTask task;
    while ((task = manager.serveTask()) != null) {   // Fetch task from manager
      synchronized (this) {
        status = 0;
      }
      // Execute task
      for (FileComparison fc : task) {
        File[] files = fc.getFiles();
        double dist;
        if (files[0].getId() == files[1].getId()) {
          dist = 0.0;
        } else {
          dist = manager.getComparator().distance(files[0], files[1]);
        }
        fc.setDistanceExact(dist);
        synchronized (this) {
          status = status + (1 / task.size());
        }
      }
      // Submit result
      task.submitResult(manager.getDistanceMatrix());
    }
  }
  
  /**
   * @return status of current task execution (from 0.0 to 1.0), of 1.0 if no task is being executed
   */
  public synchronized double getStatus() {
    return status;
  }


  /**
   * Encapsulation of {@link WorkerNaive}`s task - list of {@link FileComparison}s.
   * @author Juraj
   */
  static class WorkerTask extends ArrayList<FileComparison>{
    private static final long serialVersionUID = -2722368324885448651L;
    public static final int COMPARISONS_PER_TASK = 25;
    
    /**
     * Writes all {@link FileComparison}s from task into result {@link DistanceMatrix}.
     * @param dm result holder
     */
    public void submitResult(DistanceMatrix dm) {
      for (FileComparison fc : this) {
        dm.put(fc);
      }
    }
  }
  
}

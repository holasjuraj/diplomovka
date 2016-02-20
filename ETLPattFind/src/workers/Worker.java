package workers;

import java.util.ArrayList;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;

public class Worker extends Thread {  
  private final WorkerManager manager;
  private double status;
  
  public Worker(WorkerManager manager) {
    this.manager = manager;
  }

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
  
  public synchronized double getStatus() {
    return status;
  }


  static class WorkerTask extends ArrayList<FileComparison>{
    private static final long serialVersionUID = -2722368324885448651L;
    public static final int COMPARISONS_PER_TASK = 25;
    
    public void submitResult(DistanceMatrix dm) {
      for (FileComparison fc : this) {
        dm.put(fc);
      }
    }
  }
  
}

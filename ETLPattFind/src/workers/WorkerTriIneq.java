package workers;

import java.util.ArrayList;
import java.util.List;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;

public class WorkerTriIneq extends Thread {
  public static final double LB_MINIMUM = 0.2;
  /**
   * If range defined by low bound and high bound is smaller than this, then it is considered as
   * exact comparison instead of approximation.
   */
  public static final double MIN_BOUND_RANGE = 0.001;
  
  private final WorkerManagerTriIneq manager;
  private volatile double status;
  
  /**
   * Initialization of worker.
   * @param workerManagerTriIneq object managing my execution and tasks
   */
  public WorkerTriIneq(WorkerManagerTriIneq workerManagerTriIneq) {
    this.manager = workerManagerTriIneq;
    // TODO ?
  }

  /**
   * Start working and proactively take and execute tasks. Quit if no more tasks are available.
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    WorkerTask task;
    while ((task = manager.serveTask()) != null) {   // Fetch task from manager
      setStatus(0);
      // Execute task
      processTask(task);
    }
  }
  
  private void processTask(WorkerTask task) {
    List<File> A = task.getSetA();
    List<File> B = task.getSetB();
    int compCount = 0;
    String taskDesc = "|A|=" + A.size() + (task.sameSet ? "" : (", |B|=" + B.size()));
    System.out.println("INFO: Task (" + taskDesc + ") started.");
    
    for (int iFrom = 0; iFrom < A.size(); iFrom++) {
      File from = A.get(iFrom);
      for (int iTo = (task.sameSet ? iFrom + 1 : 0); iTo < B.size(); iTo++) {
        File to = B.get(iTo);
        setStatus(( (double)iFrom + (double)iTo / B.size() ) / A.size());

        // Skip if edge is already in graph
        if (manager.getDistanceMatrix().get(from, to) != null) {
          continue;
        }
        
        // Count approximation ...
        FileComparison edge = new FileComparison(from, to);
        // ... through points in A (always)
        for (int iThrough = 0; iThrough < iFrom; iThrough++) {
          updateBounds(from, A.get(iThrough), to, edge);
        }
        // ... through points in B (only if A != B)
        if (!task.sameSet) {
          for (int iThrough = 0; iThrough < iTo; iThrough++) {
            updateBounds(from, B.get(iThrough), to, edge);
          }
        }

        // Accept or reject approximation
        if (edge.getHighBound() - edge.getLowBound() < MIN_BOUND_RANGE) {
          // Bounding range is so small it is considered exact -> accept
          edge.setDistanceExact((edge.getLowBound() + edge.getHighBound()) / 2);
        } else if (edge.getLowBound() < LB_MINIMUM) {
          // Lower bound is too low -> reject, compute exact
          double dist = manager.getComparator().distance(from, to);
          edge.setDistanceExact(dist);
          compCount++;
        } // else: Lower bound is good enough -> accept
        
        // Save to graph
        manager.getDistanceMatrix().put(edge);
      }
    }

    int maxComps = task.sameSet ? (A.size() * (A.size() - 1) / 2) : (A.size() * B.size());
    System.out.println("INFO: Task (" + taskDesc + ") finished, performed " + compCount + " out of "
        + maxComps + " possible exact comparisons.");
    setStatus(1);
  }
  
  private void updateBounds(File from, File through, File to, FileComparison eFromTo) {
    try {
    // Determine which edge is longer (in terms of their lower bounds).
    FileComparison eLong = manager.getDistanceMatrix().get(from, through);
    FileComparison eShort = manager.getDistanceMatrix().get(through, to);
    if (eLong.getLowBound() < eShort.getLowBound()) {
      FileComparison temp = eLong;
      eLong = eShort;
      eShort = temp;
    }
    // Compute bounds
    double lb = Math.max(eLong.getLowBound() - eShort.getHighBound(), eFromTo.getLowBound());
    double hb = Math.min(eLong.getHighBound() + eShort.getHighBound(), eFromTo.getHighBound());
    // Update values
    eFromTo.setDistanceApprox(lb, hb);
    } catch(Exception e) {
      // DEBUG
      System.out.println(from.getId() + " -> " + through.getId() + " -> " + to.getId());
      System.out.println(manager.getDistanceMatrix().get(from, through));
      System.out.println(manager.getDistanceMatrix().get(through, to));
      throw e;
    }
  }
  
  /**
   * @return status of current task execution (from 0.0 to 1.0), of 1.0 if no task is being executed
   */
  public synchronized double getStatus() {
    return status;
  }
  
  /**
   * @return status of current task execution (from 0.0 to 1.0), of 1.0 if no task is being executed
   */
  private synchronized void setStatus(double newStatus) {
    status = newStatus;
  }


  /**
   * Encapsulation of {@link Worker}`s task - list of {@link FileComparison}s.
   * @author Juraj
   */
  static class WorkerTask extends ArrayList<FileComparison> {
    private static final long serialVersionUID = -1621179815050580357L;
    private List<File> setA;
    private List<File> setB;
    private boolean sameSet;
    
    public WorkerTask(List<File> setA, List<File> setB) {
      this.setA = setA;
      this.setB = setB;
      sameSet = false;
    }
    
    public WorkerTask(List<File> set) {
      setA = set;
      sameSet = true;
    }

    public List<File> getSetA() {
      return setA;
    }

    public List<File> getSetB() {
      return sameSet ? setA : setB;
    }

    public boolean isSameSet() {
      return sameSet;
    }
    
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
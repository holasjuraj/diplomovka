package workers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;
import common.Parameters;
import filecomparators.FileComparator;
import workers.WorkerNaive.WorkerTask;

/**
 * A {@link WorkerManager} that compares every file to every other file.
 * @author Juraj
 */
public class WorkerManagerNaive extends WorkerManager {
  private List<WorkerTask> taskPool;
  private int assignedTasks; 
  
  /**
   * Creates new manager and creates the {@link WorkerNaive}s. Workers are created, but not yet
   * launched.
   */
  public WorkerManagerNaive(Parameters params) {
    super(params);
    workerPool = new ArrayList<>(params.numberOfWorkers);
    for (int i = 0; i < params.numberOfWorkers; i++) {
      workerPool.add(new WorkerNaive(this));
    }
  }
  
  /**
   * Compares all files by given comparator, and stores results into distMatrix. This method
   * prepares all tasks, launches all workers and wait for them to end.
   * @param files list of files to be compared
   * @param distMatrix distance matrix that the results will be stored in. Only adding is performed,
   *          therefore matrix can be already partially filled (current values will be replaced).
   * @param comparator comparator that will be used for comparing files. Make sure that files in
   *          list are of the same type as comparator`s required file type.
   * @see WorkerManager#compareFiles(List, DistanceMatrix, FileComparator)
   */
  public void compareFiles(
      List<File> files, DistanceMatrix distMatrix, FileComparator comparator) {
    this.comparator = comparator;
    this.distMatrix = distMatrix;
    prepareTaskPool(files);
    System.out.println("INFO: Comparing started.");
    Date start = new Date();
    for (Thread w : workerPool) {
      w.start();
    }
    waitForWorkers();
    System.out.println("INFO: Comparing finished, time: "
        + ((new Date().getTime()) - start.getTime()) + "ms");
  }

  /**
   * Provides next task from the task pool, removing it from the pool. Also prints progress.
   */
  synchronized WorkerTask serveTask() {
    if (assignedTasks < taskPool.size()) {
      if (assignedTasks % 10 == 0) {
        System.out.printf("INFO: Comparing progress: %.2f%%\n",
            ((double)assignedTasks / (double)taskPool.size() * 100));
      }
      return taskPool.get(assignedTasks++);
    } else {
      return null;
    }
  }
    
  /**
   * Creates all tasks for {@link WorkerNaive}s and stores it into task pool.
   * @param files list of {@link File}s to be compared
   */
  private synchronized void prepareTaskPool(List<File> files) {
    System.out.println("INFO: Preparing comparison tasks.");
    assignedTasks = 0;
    taskPool = new ArrayList<>(files.size() * (files.size() + 1) / 2);
    int f1 = 0;
    int f2 = 0;
    while (f1 < files.size()) {
      WorkerTask task = new WorkerTask();
      for (int i = 0; i < WorkerTask.COMPARISONS_PER_TASK; i++) {
        task.add(new FileComparison(files.get(f1), files.get(f2)));
        f2++;
        if (f2 > f1) {
          f1++;
          f2 = 0;
        }
        if (f1 >= files.size()) {
          break;
        }
      }
      taskPool.add(task);
    }
  }
  
}

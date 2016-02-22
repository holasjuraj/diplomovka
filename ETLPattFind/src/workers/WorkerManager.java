package workers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;
import filecomparators.FileComparator;
import workers.Worker.WorkerTask;

/**
 * Class for managing a set of {@link Worker}s for comparing files. Manager creates {@link Worker}
 * threads, prepares and delegates tasks to them.
 * @author Juraj
 */
public class WorkerManager {
  private DistanceMatrix distMatrix;
  private FileComparator comparator;
  private List<WorkerTask> taskPool;
  private final List<Worker> workerPool;
  private int assignedTasks; 
  
  /**
   * Creates new manager with a given number of {@link Worker}s. Workers are created, but not yet
   * launched.
   */
  public WorkerManager(int numWorkers) {
    workerPool = new ArrayList<>(numWorkers);
    for (int i = 0; i < numWorkers; i++) {
      workerPool.add(new Worker(this));
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
   */
  public void compareFiles(
      List<File> files, DistanceMatrix distMatrix, FileComparator comparator) {
    this.comparator = comparator;
    this.distMatrix = distMatrix;
    prepareTaskPool(files);
    System.out.println("INFO: Comparing started.");
    Date start = new Date();
    for (Worker w : workerPool) {
      w.start();
    }
    // TODO join?
    try {
      for (Worker w : workerPool) {
        w.join();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("INFO: Comparing finished, time: "
        + ((new Date().getTime()) - start.getTime()) + "ms");
  }

  /**
   * Provides next task from the task pool, removing it from the pool. Also prints progress.
   */
  synchronized WorkerTask serveTask() {
    if (assignedTasks < taskPool.size()) {
      System.out.println("INFO: Comparing progress: "
          + ((double)assignedTasks / (double)taskPool.size() * 100) + "%");
      return taskPool.get(assignedTasks++);
    } else {
      return null;
    }
  }
  
  DistanceMatrix getDistanceMatrix() {
    return distMatrix;
  }
  
  FileComparator getComparator() {
    return comparator;
  }
  
  /**
   * Creates all tasks for {@link Worker}s and stores it into task pool.
   * @param files list of {@link File}s to be compared
   */
  private synchronized void prepareTaskPool(List<File> files) {
    System.out.println("INFO: Preparing camparison tasks.");
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

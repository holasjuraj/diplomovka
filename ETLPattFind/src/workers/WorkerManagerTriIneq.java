package workers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.DistanceMatrix;
import common.File;
import common.FileComparison;
import common.Parameters;
import filecomparators.FileComparator;
import workers.WorkerTriIneq.WorkerTask;

/**
 * A {@link WorkerManager} that uses triangular inequality to compute bounds of some distances
 * instead of exhaustively computing them all.
 * @author Juraj
 */
public class WorkerManagerTriIneq extends WorkerManager {
  private List<WorkerTask> stage1TaskPool;
  private List<WorkerTask> stage2TaskPool;
  private final int numWorkers;
  private int assignedTasks;
  private int stage = 1;
  
  /**
   * Creates new manager.
   */
  public WorkerManagerTriIneq(Parameters params) {
    super(params);
    this.numWorkers = params.numberOfWorkers;
  }
  
  /**
   * Creates all {@link WorkerTriIneq}s and runs them.
   */
  private void createAndStartWorkers() {
    workerPool = new ArrayList<>(numWorkers);
    for (int i = 0; i < numWorkers; i++) {
      WorkerTriIneq worker = new WorkerTriIneq(this);
      workerPool.add(worker);
      worker.start();
    }
  }
  
  /**
   * Compares all files by given comparator, and stores results into distMatrix. This method
   * prepares all tasks, launches all workers and wait for them to end. Lot of computation is spared
   * by replacing some distances by their lower and upper bounds.
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
    prepareTaskPools(files);
    System.out.println("INFO: Comparing started.");
    Date start = new Date();
    
    // Stage 1
    System.out.println("INFO: Stage 1 started.");
    assignedTasks = 0;
    stage = 1;
    createAndStartWorkers();
    waitForWorkers();
    System.out.println("INFO: Stage 1 finished.");
    
    // Stage 2
    System.out.println("INFO: Stage 2 started.");
    assignedTasks = 0;
    stage = 2;
    createAndStartWorkers();
    waitForWorkers();
    System.out.println("INFO: Stage 2 finished.");
    
    System.out.println("INFO: Comparing finished, time: "
        + ((new Date().getTime()) - start.getTime()) + "ms");
  }

  /**
   * Provides next task from the task pool, removing it from the pool. Also prints progress.
   */
  synchronized WorkerTask serveTask() {
    if (stage == 1) {
      
      if (assignedTasks < stage1TaskPool.size()) {
        System.out.println("INFO: Stage 1 - assigning task " + assignedTasks);
        return stage1TaskPool.get(assignedTasks++);
      } else {
        return null;
      }
      
    } else {
      
      if (assignedTasks < stage2TaskPool.size()) {
        System.out.println("INFO: Stage 2 - assigning task " + assignedTasks);
        return stage2TaskPool.get(assignedTasks++);
      } else {
        return null;
      }
      
    }
  }
    
  /**
   * Creates all tasks for {@link WorkerTriIneq}s and stores it into Stage1 and Stage2 task pools.
   * @param files list of {@link File}s to be compared
   */
  private synchronized void prepareTaskPools(List<File> files) {
    System.out.println("INFO: Preparing comparison tasks.");
    assignedTasks = 0;
    stage1TaskPool = new ArrayList<>();
    stage2TaskPool = new ArrayList<>();
    // Split set of files into non-overlapping subsets
    List<List<File>> sets = new ArrayList<>(numWorkers);
    for (int i = 0; i < numWorkers; i++) {
      sets.add(files.subList(i * files.size() / numWorkers, (i + 1) * files.size() / numWorkers));
    }
    // Fill the task pools
    for (int i = 0; i < sets.size(); i++) {
      stage1TaskPool.add(new WorkerTask(sets.get(i)));
      for (int j = i + 1; j < sets.size(); j++) {
        stage2TaskPool.add(new WorkerTask(sets.get(i), sets.get(j)));
      }
    }
    // Fill matrix diagonal
    for (File f : files) {
      distMatrix.put(FileComparison.getSelfComparison(f));
    }
  }
  
}

package common;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import clustering.Dendrogram;
import clustering.HAC;
import filecomparators.EditDistanceComparator;
import filecomparators.FileComparator;
import workers.WorkerManager;

public class Main {

  public static void main(String[] args) {
    System.out.println("INFO: Initialization.");
    Date start = new Date();
    DistanceMatrix matrix = new DistanceMatrix();
    WorkerManager manager = new WorkerManager(3);
    FileComparator comparator = new EditDistanceComparator(0.1);
    HAC hac = new HAC(HAC.METHOD_UPGMA);
    
    List<File> files = EtlReader.readAndSeparate("data/set2/output2.xml");
    // DEBUG
    FileComparison fc = new FileComparison(files.get(128), files.get(129));
    matrix.put(fc);
    System.out.println(matrix.get(128, 129));
    // /DEBUG
//    manager.compareFiles(files, matrix, comparator);
//    List<Dendrogram> clustering = hac.clusterize(files, matrix, 0.1);
//    HAC.sortClusters(clustering);
//    
//    System.out.println("INFO: Results:");
//    for(Dendrogram cluster : clustering){
//      System.out.println(cluster.toStringNames());
//    }
//
//    System.out.println("INFO: All finished, total time: "
//        + ((new Date().getTime()) - start.getTime()) + "ms");
  }

  /**
   * Redirects standard output (System.out) to specified file.
   * @param path path to file
   * @return PrintStream to redirected output (needed to be flushed and closed on exit)
   */
  public static PrintStream redirectOutput(String path) {
    PrintStream outPs = null;
    try {
      outPs = new PrintStream(new BufferedOutputStream(new FileOutputStream(path)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    System.setOut(outPs);
    return outPs;
  }

  /**
   * Resets standard output (System.out) to default value.
   * @param outPs PrintStream that was used up to now - it will be flushed and closed
   */
  public static void resetOutput(PrintStream outPs) {
    outPs.flush();
    outPs.close();
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
  }

}

package common;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import clustering.Dendrogram;
import clustering.HAC;
import filecomparators.CosineComparator;
import filecomparators.EditDistanceComparator;
import filecomparators.FileComparator;
import filecomparators.JaccardComparator;
import filecomparators.SorsenDiceComparator;
import filecomparators.UkkonenComparator;
import workers.WorkerManager;

public class Main {
  // Parameters
  public static final int NUM_WORKERS = 3;
  public static final double EDITDISTANCE_EST = 0.1;
  public static final double HAC_THRESHOLD = 0.05;
  
  public static final int FILETYPE_SEQUENCEFILE = 0;
  public static final int FILETYPE_QGRAMFILE = 1;

  public static void main(String[] args) {    
    System.out.println("INFO: Initialization.");
    Date start = new Date();
    DistanceMatrix matrix = new DistanceMatrix();
    WorkerManager manager = new WorkerManager(NUM_WORKERS);
//    FileComparator comparator = new EditDistanceComparator(EDITDISTANCE_EST);
    FileComparator comparator = new UkkonenComparator();
//    FileComparator comparator = new CosineComparator();
//    FileComparator comparator = new JaccardComparator();
//    FileComparator comparator = new SorsenDiceComparator();
    HAC hac = new HAC(HAC.METHOD_UPGMA);
    
    List<File> files = EtlReader.readAndSeparate(
        "data/set3/output2.zip", comparator.getRequiredFileType());
    manager.compareFiles(files, matrix, comparator);
    
    Scanner in = new Scanner(System.in);
    double thr;
    while ((thr = in.nextDouble()) > 0) {
      List<Dendrogram> clustering = hac.clusterize(files, matrix, thr);
      HAC.sortClusters(clustering);
      System.out.println("INFO: Results for threshold " + thr + ":");
      for (int i = 0; i < clustering.size(); i++) {
        Dendrogram cluster = clustering.get(i);
        for (File file : cluster.files) {
          System.out.println(file.getName() + "\t" + (i + 1));
        }
      }
    }
    in.close();

//    List<Dendrogram> clustering = hac.clusterize(files, matrix, HAC_THRESHOLD);
//    HAC.sortClusters(clustering);
//    System.out.println("INFO: Results for threshold " + HAC_THRESHOLD + ":");
//    for (Dendrogram cluster : clustering) {
//      System.out.println(cluster.toStringNames());
//    }
    System.out.println("INFO: All finished, total time: "
        + ((new Date().getTime()) - start.getTime()) + "ms");
  }

  /**
   * Redirects standard output (System.out) to specified file.
   * @param path path to file
   * @return {@link PrintStream} to redirected output (needed to be flushed and closed on exit)
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
   * @param outPs {@link PrintStream} that was used up to now - it will be flushed and closed
   */
  public static void resetOutput(PrintStream outPs) {
    outPs.flush();
    outPs.close();
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
  }

}

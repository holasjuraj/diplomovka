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
import filecomparators.CosineComparator;
import filecomparators.EditDistanceComparator;
import filecomparators.FileComparator;
import filecomparators.JaccardComparator;
import filecomparators.SorsenDiceComparator;
import filecomparators.UkkonenComparator;
import workers.WorkerManager;

public class Main {
  public static final int COMPARATOR_EDITDISTANCE = 0;
  public static final int COMPARATOR_UKKONEN = 1;
  public static final int COMPARATOR_COSINE = 2;
  public static final int COMPARATOR_JACCARD = 3;
  public static final int COMPARATOR_SORSENDICE = 4;
  
  public static final int FILETYPE_SEQUENCEFILE = 0;
  public static final int FILETYPE_QGRAMFILE = 1;

  public static void main(String[] args) {
    System.out.println("INFO: Initialization.");
    Date start = new Date();
    
    // Load parameters
    // DEBUG
    args = new String[2];
//    args[0] = "data/set1/btl_export.zip";
//    args[0] = "data/set2/output2.xml";
    args[0] = "data/set3/output2.zip";
    args[1] = "test.params";
    // /DEBUG
    if (args.length < 1) {
      System.out.println("ERROR: Main.main: No input file specified.");
      return;
    }
    String inputFilePath = args[0];
    Parameters params;
    if (args.length >= 2) {
      params = new Parameters(args[1]);
      System.out.println("INFO: Parameters file loaded.");
    } else {
      params = new Parameters();
      System.out.println("INFO: Using default parameters.");
    }
    
    // Prepare structures
    DistanceMatrix matrix = new DistanceMatrix();
    WorkerManager manager = new WorkerManager(params.numberOfWorkers);
    FileComparator comparator;
    switch (params.comparingMethod) {
      case COMPARATOR_EDITDISTANCE:
        comparator = new EditDistanceComparator(params.editDistEst);
        break;
      case COMPARATOR_COSINE:
        comparator = new CosineComparator();
        break;
      case COMPARATOR_JACCARD:
        comparator = new JaccardComparator();
        break;
      case COMPARATOR_SORSENDICE:
        comparator = new SorsenDiceComparator();
        break;
      case COMPARATOR_UKKONEN: // fall-through
      default:
        comparator = new UkkonenComparator();
    }
    HAC hac = new HAC(params.hacMethod);
    
    // Read and prepare files
    List<File> files =
        EtlReader.readAndSeparate(inputFilePath, comparator.getRequiredFileType(), params);
    
    // Compare all files
    manager.compareFiles(files, matrix, comparator);
    
    // DEBUG
//    Scanner in = new Scanner(System.in);
//    double thr;
//    while ((thr = in.nextDouble()) > 0) {
//      List<Dendrogram> clustering = hac.clusterize(files, matrix, thr);
//      HAC.sortClusters(clustering);
//      System.out.println("INFO: Results for threshold " + thr + ":");
//      for (int i = 0; i < clustering.size(); i++) {
//        Dendrogram cluster = clustering.get(i);
//        for (File file : cluster.files) {
//          System.out.println(file.getName() + "\t" + (i + 1));
//        }
//      }
//    }
//    in.close();
    // /DEBUG

    // Clusterize
    List<Dendrogram> clustering = hac.clusterize(files, matrix, params.hacThreshold);
    HAC.sortClusters(clustering);
    
    // Print results
    System.out.println("INFO: Results:");
    for (Dendrogram cluster : clustering) {
      System.out.println(cluster.toStringNames());
    }    
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

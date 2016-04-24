package common;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import clustering.Dendrogram;
import clustering.HAC;
import filecomparators.CosineComparator;
import filecomparators.EditDistanceComparator;
import filecomparators.FileComparator;
import filecomparators.JaccardComparator;
import filecomparators.SorensenDiceComparator;
import filecomparators.UkkonenComparator;
import workers.WorkerManager;
import workers.WorkerManagerNaive;
import workers.WorkerManagerTriIneq;

public class Main {
  public static final int COMPARATOR_EDITDISTANCE = 0;
  public static final int COMPARATOR_UKKONEN = 1;
  public static final int COMPARATOR_COSINE = 2;
  public static final int COMPARATOR_JACCARD = 3;
  public static final int COMPARATOR_SORENSENDICE = 4;

  public static final int SCHEDULER_FULL_COMP = 0;
  public static final int SCHEDULER_TRI_INEQ = 1;
  
  public static final int FILETYPE_SEQUENCEFILE = 0;
  public static final int FILETYPE_QGRAMFILE = 1;
  
  /** Manual for correct command line usage. */
  public static final String USAGE =
      "\nUsage: java -jar ETLPattFind.jar <inputfile> [options]\n"
      + "    <inputfile> must be either .xml file, or .zip archive (not .rar or .tar.gz!)\n"
      + "                containing the .xml file. Archive cannot be locked.\n"
      + "Options include:\n"
      + "    -o <outputfile>     specify the output file. Default output file is\n"
      + "                        \"<inputfile>_patterns.xml\".\n"
      + "    -p <parametersfile> use this *.params file. See default.params for\n"
      + "                        parameters explanation.\n"
      + "    -s <sysoutfile>     redirect system output to this file. Note that system\n"
      + "                        output prints only status messages, not the results.";

//  public static void dummyRun() {
//    // DEBUG TOOL
//    List<File> files = new ArrayList<>();
//    String inputFilePath = "data/set1/btl_export.zip";
//    FileComparator comparator = new EditDistanceComparator(1);
//    Parameters params = new Parameters();
//    files = EtlReader.readAndSeparate(inputFilePath, comparator.getRequiredFileType(), params);
//  }
  
  public static void main(String[] args) {
    PrintStream sysout = System.out;
    try {
      Date start = new Date();
      
      // DEBUG - launch dummyRun()
//      dummyRun();
//      if (start.toString() != null) {
//        return;
//      }
      // /DEBUG
  
      // DEBUG - substitute program arguments
//      args = new String[7];
////      args[0] = "data/set1/btl_export.zip";
//      args[0] = "data/set2/output2.xml";
////      args[0] = "data/set2_25/output2.xml";
////      args[0] = "data/set3/output2.zip";
////      args[0] = "data/set4/ABC.zip";
////      args[1] = "-s";
////      args[2] = "data/syso.txt";
//      args[3] = "-p";
//      args[4] = "test.params";
////      args[5] = "-o";
////      args[6] = "data/set3/testout.xml";
      // /DEBUG
      
      
      // Parse arguments
      if (args.length < 1) {
        System.out.println("ERROR: Main.main: No input file specified.");
        System.out.println(USAGE);
        return;
      }
      List<String> argsList = Arrays.asList(args);
      // Input file
      String inputFilePath = argsList.get(0);
      // Standard output redirect
      PrintStream outputRedirection = null;
      int s = argsList.indexOf("-s");
      if (s > -1 && argsList.size() > s) {
        outputRedirection = redirectOutput(argsList.get(s + 1));
        sysout = outputRedirection;
      }
      System.out.println("INFO: Initialization.");
      // Application parameters
      Parameters params;
      int p = argsList.indexOf("-p");
      if (p > -1 && argsList.size() > p) {
        params = new Parameters(argsList.get(p + 1));
        System.out.println("INFO: Parameters file loaded.");
      } else {
        params = new Parameters();
        System.out.println("INFO: Using default parameters.");
      }
      // Output file
      String outputFilePath;
      int o = argsList.indexOf("-o");
      if (o > -1 && argsList.size() > o) {
        outputFilePath = argsList.get(o + 1);
      } else {
        int ext = inputFilePath.lastIndexOf('.');
        outputFilePath = inputFilePath.substring(0, ext) + "_patterns.xml";
      }
      System.out.println("INFO: Output file set to \"" + outputFilePath + "\".");
      
  
      // Prepare structures
      DistanceMatrix matrix = new DistanceMatrix();
      FileComparator comparator;
      WorkerManager manager;
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
        case COMPARATOR_SORENSENDICE:
          comparator = new SorensenDiceComparator();
          break;
        case COMPARATOR_UKKONEN: // fall-through
        default:
          comparator = new UkkonenComparator();
      }
      switch (params.comparingMethod) {
        // Choose manager according to metric properties of distance function.
        case COMPARATOR_EDITDISTANCE: // fall-through
        case COMPARATOR_UKKONEN: // fall-through
        case COMPARATOR_JACCARD:
          manager = new WorkerManagerTriIneq(params);
          break;
        case COMPARATOR_COSINE: // fall-through
        case COMPARATOR_SORENSENDICE: // fall-through
        default:
          manager = new WorkerManagerNaive(params);
      }
      
      // Read and prepare files
      List<File> files =
          EtlReader.readAndSeparate(inputFilePath, comparator.getRequiredFileType(), params);
      
      // Compare all files
      manager.compareFiles(files, matrix, comparator);
      
      // Clusterize
      HAC hac = new HAC(params.hacMethod, matrix, comparator);
      List<Dendrogram> clustering = hac.clusterize(files, params.hacThreshold);

      // Write results
      writeResultsXml(inputFilePath, outputFilePath, clustering, params.minClusterSize);
      
      // DEBUG - print results into console
//      System.out.println("INFO: Results:");
//      HAC.sortClusters(clustering);
//      for (Dendrogram cluster : clustering) {
//        System.out.println(cluster.toStringIds());
//      }
      // /DEBUG
      
      // Finalize
      System.out.println("INFO: All finished, total time: "
          + ((new Date().getTime()) - start.getTime()) + "ms");
      if (outputRedirection != null) {
        resetOutput(outputRedirection);
      }
      
    } catch (Exception e) {
      e.printStackTrace(sysout);
    }
  }

  /**
   * Redirects standard output (System.out) to specified file.
   * @param path path to file
   * @return {@link PrintStream} to redirected output (needed to be flushed and closed on exit)
   */
  public static PrintStream redirectOutput(String path) {
    PrintStream outPs = null;
    try {
      outPs = new PrintStream(new BufferedOutputStream(new FileOutputStream(path)), true, "UTF-8");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
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

  /**
   * Writes resulting clustering into XML file. Only clusters (patterns) with at least
   * minClusterSize Files (jobs) are written to output file.
   * @param inputFileName path of the INPUT file - name of this file is included in XML root element
   *          as an attribute
   * @param outputPath path to a file that will be written
   * @param clustering clustering to be written. It will be sorted within this method.
   * @param minClusterSize minimum number of files in a cluster
   */
  public static void writeResultsXml(
      String inputFileName, String outputPath, List<Dendrogram> clustering, int minClusterSize) {
    try {
      int slash = Math.max(inputFileName.lastIndexOf('/'), inputFileName.lastIndexOf('\\'));
      int ext = inputFileName.lastIndexOf('.');
      String pureName = inputFileName.substring(slash + 1, ext);
      
      HAC.sortClusters(clustering);
      
      PrintStream out = new PrintStream(
          new FileOutputStream(outputPath), false, "UTF-8");
      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); // Header
      out.println("<patterns inputFile=\"" + pureName + "\">");  // Open root element
      for (int i = 0; i < clustering.size(); i++) {
        Dendrogram cluster = clustering.get(i);
        if (cluster.size() < minClusterSize) {
          continue;
        }
        out.println("\t<pattern id=\"" + i + "\">");             // Open cluster element
        for (File job : cluster.files) {
          out.println("\t\t<job>" + job.getName() + "</job>");   // Job element
        }
        out.println("\t</pattern>");                             // Close cluster element
      }      
      
      out.println("</patterns>");                                // Close root element
      out.close();      
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      System.out.println("ERROR: Main.writeResultsXml: Error writing output file.");
      e.printStackTrace();
    }
  }
  
}

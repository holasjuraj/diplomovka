package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import clustering.HAC;

/**
 * Storage of parameters for the application.
 * @author Juraj
 */
public class Parameters {
  // Common parameters with default values
  public int numberOfWorkers = 4;
  public int comparingMethod = Main.COMPARATOR_UKKONEN;
  public int scheduler = Main.SCHEDULER_FULL_COMP;
  public double hacThreshold = 0.05;
  public int hacMethod = HAC.METHOD_UPGMA;
  public int minClusterSize = 2;
  // Specific parameters with default values
  public double editDistEst = 0.05;
  public int qGramSize = 2;
  public double schTriIneqLBMin = 0.05;
  public double schTriIneqBoundRange = 0.001;
  
  // Labels
  private static final String label_numberOfWorkers = "threads";
  private static final String label_comparingMethod = "comparing-method";
    private static final String label_cmEditDistance = "editdistance";
    private static final String label_cmUkkonen = "ukkonen";
    private static final String label_cmCosine = "cosine";
    private static final String label_cmJaccard = "jaccard";
    private static final String label_cmSorsenDice = "sorsendice";
  private static final String label_scheduler = "scheduler";
    private static final String label_schFullComp = "full-comparison";
    private static final String label_schTriIneq = "tri-ineq";
  private static final String label_hacThreshold = "clustering-threshold";
  private static final String label_hacmethod = "clustering-method";
    private static final String label_hmUpgma = "upgma";
    private static final String label_hmClink = "clink";
    private static final String label_hmSlink = "slink";
  private static final String label_minClusterSize = "min-pattern-size";
  private static final String label_editDistEst = "editdistance-early-stopping";
  private static final String label_qGramSize = "qgram-size";
  private static final String label_schTriIneqLBMin = "scheduler-tri-ineq--lb-minimum";
  private static final String label_schTriIneqBoundRange = "scheduler-tri-ineq--min-bound-range";
  
  /**
   * Creates new parameter set with all default values.
   */
  public Parameters() {}
  
  /**
   * Creates parameter set based on specified parameter file. If a value cannot be parsed, it is set
   * to default with a warning message. If a parameter is omitted, it is set to default value
   * without warning.
   * @param paramFile path to parameter file
   */
  public Parameters(String paramFile) {
    try {
      File f;
      if (paramFile == null || !(f = new File(paramFile)).exists()) {
        // Load default parameters
        System.out.println("WARN: Parameters file cannot be found, using default parameters.");
        return;
      }
      
      Scanner in = new Scanner(f);
      in.useDelimiter(":|[\\n\\r]+"); // ":" or new lines
      while (in.hasNext()) {
        String paramName = in.next().toLowerCase().trim();
        if (paramName.startsWith("#")) {
          // Skip comments
          in.nextLine();
          continue;
        }
        String paramVal;
        try {
          paramVal = in.next().toLowerCase().trim();
        } catch (NoSuchElementException e) {
          System.out.println(
              "WARN: Parameters.Parameters: Unknown parameter \"" + paramName + "\"");
          in.close();
          return;
        }
        
        // Number of workers
        if (paramName.equals(label_numberOfWorkers)) {
          Integer num = tryParseInt(paramName, paramVal, 1, Integer.MAX_VALUE, true);
          if (num != null) {
            numberOfWorkers = num.intValue();
          }
          continue;
        }
        
        // Comparing method
        if (paramName.equals(label_comparingMethod)) {
          // Try interpret as number
          Integer num = tryParseInt(paramName, paramVal, 0, Main.COMPARATOR_SORSENDICE, false);
          if (num != null) {
            comparingMethod = num.intValue();
            continue;
          }
          // Interpret as string
          if (paramVal.equals(label_cmEditDistance)) {
            comparingMethod = Main.COMPARATOR_EDITDISTANCE;
          } else if (paramVal.equals(label_cmUkkonen)) {
            comparingMethod = Main.COMPARATOR_UKKONEN;
          } else if (paramVal.equals(label_cmCosine)) {
            comparingMethod = Main.COMPARATOR_COSINE;
          } else if (paramVal.equals(label_cmJaccard)) {
            comparingMethod = Main.COMPARATOR_JACCARD;
          } else if (paramVal.equals(label_cmSorsenDice)) {
            comparingMethod = Main.COMPARATOR_SORSENDICE;
          } else {
            System.out.println("WARN: Parameters.Parameters: Unrecognized value of \""
                + paramName + "\", applying default value.");
          }
          continue;
        }
        
        // Scheduler
        if (paramName.equals(label_scheduler)) {
          // Try interpret as number
          Integer num = tryParseInt(paramName, paramVal, 0, Main.SCHEDULER_TRI_INEQ, false);
          if (num != null) {
            scheduler = num.intValue();
            continue;
          }
          // Interpret as string
          if (paramVal.equals(label_schFullComp)) {
            scheduler = Main.SCHEDULER_FULL_COMP;
          } else if (paramVal.equals(label_schTriIneq)) {
            scheduler = Main.SCHEDULER_TRI_INEQ;
          } else {
            System.out.println("WARN: Parameters.Parameters: Unrecognized value of \""
                + paramName + "\", applying default value.");
          }
          continue;
        }

        // HAC threshold
        if (paramName.equals(label_hacThreshold)) {
          Double num = tryParseDouble(paramName, paramVal, 0.0001, 1.0, true);
          if (num != null) {
            hacThreshold = num.doubleValue();
          }
          continue;
        }
        
        // HAC method
        if (paramName.equals(label_hacmethod)) {
          // Try interpret as number
          Integer num = tryParseInt(paramName, paramVal, 0, HAC.METHOD_SLINK, false);
          if (num != null) {
            hacMethod = num.intValue();
            continue;
          }
          // Interpret as string
          if (paramVal.equals(label_hmUpgma)) {
            hacMethod = HAC.METHOD_UPGMA;
          } else if (paramVal.equals(label_hmClink)) {
            hacMethod = HAC.METHOD_CLINK;
          } else if (paramVal.equals(label_hmSlink)) {
            hacMethod = HAC.METHOD_SLINK;
          } else {
            System.out.println("WARN: Parameters.Parameters: Unrecognized value of \""
                + paramName + "\", applying default value.");
          }
          continue;
        }
        
        // Minimal cluster size
        if (paramName.equals(label_minClusterSize)) {
          Integer num = tryParseInt(paramName, paramVal, 1, Integer.MAX_VALUE, true);
          if (num != null) {
            minClusterSize = num.intValue();
          }
          continue;  
        }

        // Edit distance - early stopping threshold
        if (paramName.equals(label_editDistEst)) {
          Double num = tryParseDouble(paramName, paramVal, 0.01, 1.0, true);
          if (num != null) {
            editDistEst = num.doubleValue();
          }
          continue;          
        }

        // q-gram size
        if (paramName.equals(label_qGramSize)) {
          Integer num = tryParseInt(paramName, paramVal, 1, 100, true);
          if (num != null) {
            qGramSize = num.intValue();
          }
          continue;  
        }

        // TriIneq scheduler - low bound minimum
        if (paramName.equals(label_schTriIneqLBMin)) {
          Double num = tryParseDouble(paramName, paramVal, 0.0001, 1.0, true);
          if (num != null) {
            schTriIneqLBMin = num.doubleValue();
          }
          continue;          
        }

        // TriIneq scheduler - minimal bound range
        if (paramName.equals(label_schTriIneqBoundRange)) {
          Double num = tryParseDouble(paramName, paramVal, 0.0, 1.0, true);
          if (num != null) {
            schTriIneqBoundRange = num.doubleValue();
          }
          continue;          
        }
        
        // None of previous
        System.out.println("WARN: Parameters.Parameters: Unknown parameter \"" + paramName + "\"");
      }
      
      in.close();
    } catch (FileNotFoundException e) {
      System.out.println("ERROR: Parameters.Parameters: Error reading parameters file.");
      e.printStackTrace();
    }
  }
  
  /**
   * Tries parsing parameter as integer within a bounds. Prints warning message if value is out of
   * specified bounds, optionally also if parsing fails.
   * @param paramName name of parameter (used for warning messages)
   * @param paramVal value to be parsed
   * @param min minimal allowed value of the parameter (inclusive)
   * @param max maximal allowed value of the parameter (inclusive)
   * @param printParseErr if true and parsing fails, method prints a warning message
   * @return value of parameter, or null if parsing failed or value was out of bounds
   */
  private static Integer tryParseInt(
      String paramName, String paramVal, int min, int max, boolean printParseErr) {
    Integer result = null;
    try {
      int num = Integer.parseInt(paramVal);
      if (num >= min && num <= max) {
        result = num;
      } else {
        System.out.println("WARN: Parameters.Parameters: Value of \"" + paramName
            + "\" out of range, applying default value.");
      }
    } catch (NumberFormatException e) {
      if (printParseErr) {
        System.out.println("WARN: Parameters.Parameters: Cannot parse value of \""
            + paramName + "\", applying default value.");
      }
    }
    return result;
  }
  
  /**
   * Tries parsing parameter as double within a bounds. Prints warning message if value is out of
   * specified bounds, optionally also if parsing fails.
   * @param paramName name of parameter (used for warning messages)
   * @param paramVal value to be parsed
   * @param min minimal allowed value of the parameter (inclusive)
   * @param max maximal allowed value of the parameter (inclusive)
   * @param printParseErr if true and parsing fails, method prints a warning message
   * @return value of parameter, or null if parsing failed or value was out of bounds
   */
  private static Double tryParseDouble(
      String paramName, String paramVal, double min, double max, boolean printParseErr) {
    Double result = null;
    try {
      double num = Double.parseDouble(paramVal);
      if (num >= min && num <= max) {
        result = num;
      } else {
        System.out.println("WARN: Parameters.Parameters: Value of \"" + paramName
            + "\" out of range, applying default value.");
      }
    } catch (NumberFormatException e) {
      if (printParseErr) {
        System.out.println("WARN: Parameters.Parameters: Cannot parse value of \""
            + paramName + "\", applying default value.");
      }
    }
    return result;
  }
  
}

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Tool3populateResults {
  public static final String[] datasets = {
      "web/ABC", "web/BMT", "web/CLA", "web/CLA2", "web/EDW", "web/INT",
      "set1", "set2", "set2_25", "set3",
      "setGen1cluster", "setGen2clusters",
      "setGen1cluster/noMBR/2", "setGen2clusters/noMBR/2",
      "comprehensiveCLA/1", "comprehensiveCLA/2", "comprehensiveCLA/3", "comprehensiveCLA/23"
      };

  public static void main(String[] args) {
    try {
      // Initialization
      PrintWriter outComp  = new PrintWriter("data/resultsComparing.txt");
      PrintWriter outClust = new PrintWriter("data/resultsClustering.txt");

      outComp.println("dataset\tcomparator\tthreshold\tscheduler\t" +
          "timeComp\ttimeClust\ttimeAll\t" +
          "ti1Done\tti1All\tti1Per\tti2Done\tti2All\tti2Per\ttiTotalDone\ttiTotalAll\ttiTotalPer\t"+
          "TP\tTN\tFP\tFN\tTP%\tTN%\trandomIndex\tF2score");
      
      outClust.println("dataset\tclustMethod\tthreshold\t" +
          "TP\tTN\tFP\tFN\tTP%\tTN%\trandomIndex\tF2score");
      
      // Process datasets
      for (String dataset : datasets) {
        System.out.println("Processing dataset: " + dataset);
        File folder = new File("../../Samples/" + dataset);
        for (File file : folder.listFiles()) {
          // Skip unwanted
          String nameIPE = file.getName();
          if (file.isDirectory() || !nameIPE.endsWith(".txt") || nameIPE.endsWith("patterns.txt")) {
            continue;
          }
          // Parse names
          String nameIP = nameIPE.substring(0, nameIPE.lastIndexOf(".txt"));
          String nameP = nameIP.substring(nameIP.indexOf("_red_") + 5);
          String[] params = nameP.split("_");
          boolean clust = params[0].startsWith("C");
          params[0] = params[0].substring(params[0].indexOf("C") + 1);
          PrintWriter out = (clust) ? outClust : outComp;
          
          // OUTPUT parameters
          out.print(dataset);
          for (String param : params) {
            out.print("\t" + param);
          }
          
          if (!clust) {
            // Parse times & TriIneq savings
            int timeComp = 0, timeClust = 0, timeAll = 0;
            int ti1Done = 0, ti1All = 0, ti2Done = 0, ti2All = 0;
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
              String line = in.nextLine();
              // Times
              if (line.startsWith("INFO: Comparing finished, time: ")) {
                timeComp = Integer.parseInt(line.substring(
                    "INFO: Comparing finished, time: ".length(),
                    line.lastIndexOf("ms")));
              }
              else if (line.startsWith("INFO: Clustering finished, time: ")) {
                timeClust = Integer.parseInt(line.substring(
                    "INFO: Clustering finished, time: ".length(),
                    line.lastIndexOf("ms")));
              }
              else if (line.startsWith("INFO: All finished, total time: ")) {
                timeAll = Integer.parseInt(line.substring(
                    "INFO: All finished, total time: ".length(),
                    line.lastIndexOf("ms")));
              }
              // TriIneq savings
              else if (line.endsWith(" possible exact comparisons.")) {
                int performed = Integer.parseInt(line.substring(
                    line.indexOf("performed ") + "performed ".length(),
                    line.indexOf(" out of ")));
                int possible = Integer.parseInt(line.substring(
                    line.indexOf(" out of ") + " out of ".length(),
                    line.indexOf(" possible exact comparisons.")));
                if (line.indexOf("|B|") < 0) {
                  ti1Done += performed;
                  ti1All += possible;
                }
                else {
                  ti2Done += performed;
                  ti2All += possible;
                }
              }              
            }
            in.close();
            double ti1Per = (double)ti1Done / (double)ti1All;
            double ti2Per = (double)ti2Done / (double)ti2All;
            int tiTotalDone = ti1Done + ti2Done;
            int tiTotalAll = ti1All + ti2All;
            double tiTotalPer = (double)tiTotalDone / (double)tiTotalAll;
            if (Double.isNaN(ti1Per)) { ti1Per = 0.0; }
            if (Double.isNaN(ti2Per)) { ti2Per = 0.0; }
            if (Double.isNaN(tiTotalPer)) { tiTotalPer = 0.0; }
          
            // OUTPUT times & TriIneq savings
            out.print(
                "\t" + timeComp +
                "\t" + timeClust +
                "\t" + timeAll +
                "\t" + ti1Done +
                "\t" + ti1All +
                "\t" + ti1Per +
                "\t" + ti2Done +
                "\t" + ti2All +
                "\t" + ti2Per +
                "\t" + tiTotalDone +
                "\t" + tiTotalAll +
                "\t" + tiTotalPer);
          }

          // OUTPUT scores
          Tool2calculateClusteringScore.Result scores = Tool2calculateClusteringScore.countScore(
              folder.getPath()+"/patterns.txt", folder.getPath()+"/"+nameIP+"_patterns.txt");
          out.print(
              "\t" + scores.tp +
              "\t" + scores.tn +
              "\t" + scores.fp +
              "\t" + scores.fn +
              "\t" + scores.tpPer +
              "\t" + scores.tnPer +
              "\t" + scores.randomIndex +
              "\t" + scores.f2score);
          
          out.println();
        }
      }
      
      // Finalize
      outComp.close();
      outClust.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}

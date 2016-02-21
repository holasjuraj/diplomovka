import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Tool2calculateClusteringScore {

  public static void main(String[] args) {
    try {
      String pathReal = "data/real.txt";
      String pathPred = "data/predicted.txt";
      Scanner inReal = new Scanner(new File(pathReal));
      Scanner inPred = new Scanner(new File(pathPred));
      Map<String, String> mapReal = new HashMap<>();
      Map<String, String> mapPred = new HashMap<>();
      List<String> jobs = new ArrayList<>();
      while (inReal.hasNext()) {
        String job = inReal.next();
        String pat = inReal.next();
        mapReal.put(job, pat);
        jobs.add(job);
      }
      while (inPred.hasNext()) {
        String job = inPred.next();
        String pat = inPred.next();
        mapPred.put(job, pat);
      }
      inReal.close();
      inPred.close();
      
      double tp = 0, tn = 0, fp = 0, fn = 0;
      for (int i = 0; i < jobs.size(); i++) {
        String jobi = jobs.get(i);
        String pi = mapPred.get(jobi);
        String ri = mapReal.get(jobi);
        for (int j = i+1; j < jobs.size(); j++) {
          String jobj = jobs.get(j);
          String pj = mapPred.get(jobj);
          String rj = mapReal.get(jobj);
          
          if (pi.equals(pj)) {
            if (ri.equals(rj)) {
              tp++;
            } else {
              fp++;
            }
          } else {
            if (ri.equals(rj)) {
              fn++;
            } else {
              tn++;
            }
          }
        }
      }

      System.out.println("True positives: " + (int)tp);
      System.out.println("True negatives: " + (int)tn);
      System.out.println("False positives: " + (int)fp);
      System.out.println("False negatives: " + (int)fn);
      
      double ri = (tp + tn) / (tp + tn + fp + fn);
      System.out.println("Random index = " + ri);
      
      double b = 2;
      double fBeta = ((1 + b*b) * tp) / ((1 + b*b) * tp + b*b * fn + fp);
      System.out.println("F_beta measure = " + fBeta);

      System.out.println("TP% = " + (tp / (tp + fn)));
      System.out.println("TN% = " + (tn / (fp + tn)));
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}

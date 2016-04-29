import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Tool2calculateClusteringScore {
  public static class Result {
    double randomIndex = 0;
    double f2score = 0;
    double tp = 0;
    double tn = 0;
    double fp = 0;
    double fn = 0;
    double tpPer = 0;
    double tnPer = 0;
  }
  
  public static void main(String[] args) {
//    String pathReal = "data/real.txt";
//    String pathPred = "data/predicted.txt";
    String pathReal = "data/patterns.txt";
    String pathPred = "data/output2_red_0_0.02_0_patterns.txt";
    
    Result res = countScore(pathReal, pathPred);

    System.out.println("True positives: " + (int)res.tp);
    System.out.println("True negatives: " + (int)res.tn);
    System.out.println("False positives: " + (int)res.fp);
    System.out.println("False negatives: " + (int)res.fn);
    System.out.println("Random index = " + res.randomIndex);
    System.out.println("F_2 measure = " + res.f2score);
    System.out.println("TP% = " + res.tpPer);
    System.out.println("TN% = " + res.tnPer);
  }
    
  public static Result countScore(String pathReal, String pathPred) {
    try {
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
      
      Result res = new Result();
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
              res.tp++;
            } else {
              res.fp++;
            }
          } else {
            if (ri.equals(rj)) {
              res.fn++;
            } else {
              res.tn++;
            }
          }
        }
      }
      
      res.randomIndex = (res.tp + res.tn) / (res.tp + res.tn + res.fp + res.fn);
      
      final double b = 2;
      res.f2score = ((1 + b*b) * res.tp) / ((1 + b*b) * res.tp + b*b * res.fn + res.fp);
      if (Double.isNaN(res.f2score)) { res.f2score = 1.0; }
      
      res.tpPer = res.tp / (res.tp + res.fn);
      res.tnPer = res.tn / (res.fp + res.tn);
      if (Double.isNaN(res.tpPer)) { res.tpPer = 1.0; }
      if (Double.isNaN(res.tnPer)) { res.tnPer = 1.0; }
      
      return res;
    } catch (FileNotFoundException e) {
      return new Result();
    }
  }

}

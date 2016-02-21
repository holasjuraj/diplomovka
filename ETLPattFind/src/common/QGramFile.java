package common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QGramFile extends File {
  private Map<Integer, Integer> profile;
  private int q = -1;
  private double vectorLength = -1;
  private int setSize = -1;

  public QGramFile(int id, String name) {
    super(id, name);
  }
  
  public Map<Integer, Integer> getProfile() {
    return profile;
  }

  public int getQ() {
    if (vectorLength < 0) {
      System.out.println("WARN: QGramFile.getQ: File profile has not been created.");
    }
    return q;
  }
  
  public double getVectorLength() {
    if (vectorLength < 0) {
      System.out.println("WARN: QGramFile.getVectorLength: File profile has not been created.");
    }
    return vectorLength;
  }

  public int getSetSize() {
    if (setSize < 0) {
      System.out.println("WARN: QGramFile.getSetSize: File profile has not been created.");
    }
    return setSize;
  }
  
  public int getProfileSize() {
    return profile.size();
  }
  
  public void createProfile(List<String> tokens, int q) {
    this.q = q;
    profile = new HashMap<>();
    double vLenSqr = 0;
    setSize = 0;
    
    for (int i = 0; i < tokens.size() - q + 1; i++) {
      int qGram = qGramHash(tokens, q, i);
      int val = 0;
      if (profile.containsKey(qGram)) {
        val = profile.get(qGram);
      }
      vLenSqr += 2 * val + 1;       // Vector length update -> n^2 + (2*n+1) == (n+1)^2
      setSize++;                    // Set size update
      profile.put(qGram, val + 1);  // Profile update
    }
    
    vectorLength = Math.sqrt(vLenSqr);
  }
  
  private static final int HASH_PRIME = 37;
  private int qGramHash(List<String> tokens, int q, int index) {
    int result = 0;
    for (int i = index; (i < index + q) && (i < tokens.size()); i++) {
      result = HASH_PRIME * result + tokens.get(i).hashCode();
    }
    return result;
  }

}

package common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Type of {@link File} storing the q-gram profile of the file. L2 length of q-gram vector and size
 * of q-gram multiset are pre-computed and stored for direct retrieval.
 * @author Juraj
 */
public class QGramFile extends File {
  /**
   * {@link Map} q-gram ID -> number of occurrences
   */
  private Map<Integer, Integer> profile;
  private int q = -1;
  private double vectorLength = -1.0;
  private int setSize = -1;

  /**
   * Initialize with no content. Recommended to create profile right afterwards.
   */
  public QGramFile(int id, String name) {
    super(id, name);
  }
  
  /**
   * @return {@link Map} q-gram ID -> number of occurrences, or null if profile has not been created
   *         yet
   */
  public Map<Integer, Integer> getProfile() {
    return profile;
  }

  /**
   * @return value of q, or -1 if profile has not been created yet
   */
  public int getQ() {
    if (vectorLength < 0) {
      System.out.println("WARN: QGramFile.getQ: File profile has not been created.");
    }
    return q;
  }
  
  /**
   * @return L2 length of q-gram vector, or -1 if profile has not been created yet
   */
  public double getVectorLength() {
    if (vectorLength < 0) {
      System.out.println("WARN: QGramFile.getVectorLength: File profile has not been created.");
    }
    return vectorLength;
  }

  /**
   * @return size (cardinality) of q-gram multiser, or -1 if profile has not been created yet
   */
  public int getSetSize() {
    if (setSize < 0) {
      System.out.println("WARN: QGramFile.getSetSize: File profile has not been created.");
    }
    return setSize;
  }
  
  /**
   * @return number of distinct q-grams in profile, or -1 if profile has not been created yet
   */
  public int getProfileSize() {
    if (profile == null) {
      System.out.println("WARN: QGramFile.getProfileSize: File profile has not been created.");
      return -1;
    }
    return profile.size();
  }
  
  /**
   * Creates q-gram profile from list of tokens, pre-computes L2 length of q-gram vector and size
   * of q-gram multiset.
   * @param tokens
   * @param q number of consecutive tokens that are grouped into a q-gram
   */
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
  /**
   * Computes hash (ID) of a q-gram by combining IDs of individual tokens within.
   * @param tokens list of tokens to choose from
   * @param q number of tokens in a q-gram
   * @param index starting index of a q-gram
   * @return ID of a q-gram
   */
  private int qGramHash(List<String> tokens, int q, int index) {
    int result = 0;
    for (int i = index; (i < index + q) && (i < tokens.size()); i++) {
      result = HASH_PRIME * result + tokens.get(i).hashCode();
    }
    return result;
  }

}

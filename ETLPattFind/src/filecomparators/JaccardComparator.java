package filecomparators;

import java.util.Map;

import common.File;
import common.Main;
import common.QGramFile;

/**
 * Implementation of Jaccard index for two files.
 * @author Juraj
 */
public class JaccardComparator extends FileComparator {

  /**
   * Computes distance from Jaccard index of files: given two q-gram multisets A and B,
   * d = 1 - |A intersection B| / |A union B|
   * @return distance normalized to interval (0.0, 1.0)
   * @throws {@link IllegalArgumentException} if input files are not type {@link QGramFile}
   * @see filecomparators.FileComparator#distance(common.File, common.File)
   */
  @Override
  public double distance(File file1, File file2) {
    if (!(file1 instanceof QGramFile) || !(file2 instanceof QGramFile)) {
      System.out.println("ERROR: JaccardComparator.distance: Incompatable file types.");
      throw new IllegalArgumentException();
    }
    QGramFile qFile1 = (QGramFile) file1;
    QGramFile qFile2 = (QGramFile) file2;
    Map<Integer, Integer> profileLarge;
    Map<Integer, Integer> profileSmall;
    if (qFile1.getProfileSize() > qFile2.getProfileSize()) {
      profileLarge = qFile1.getProfile();
      profileSmall = qFile2.getProfile();
    } else {
      profileLarge = qFile2.getProfile();
      profileSmall = qFile1.getProfile();      
    }
    
    int intersec = 0;
    for (Map.Entry<Integer, Integer> qGram : profileSmall.entrySet()) {
      int val1 = qGram.getValue();
      int val2 = profileLarge.getOrDefault(qGram.getKey(), 0);
      intersec += Math.min(val1, val2);
    }  
    return 1.0 - (double)intersec / (double)(qFile1.getSetSize() + qFile2.getSetSize() - intersec);
  }

  /**
   * @return ID of {@link QGramFile} type
   * @see filecomparators.FileComparator#getRequiredFileType()
   */
  @Override
  public int getRequiredFileType() {
    return Main.FILETYPE_QGRAMFILE;
  }

}

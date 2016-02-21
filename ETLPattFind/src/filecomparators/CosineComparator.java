package filecomparators;

import java.util.Map;

import common.File;
import common.Main;
import common.QGramFile;

public class CosineComparator extends FileComparator {

  @Override
  public double distance(File file1, File file2) {
    if (!(file1 instanceof QGramFile) || !(file2 instanceof QGramFile)) {
      System.out.println("ERROR: CosineComparator.distance: Incompatable file types.");
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
    
    int dotProd = 0;
    for (Map.Entry<Integer, Integer> qGram : profileSmall.entrySet()) {
      int val1 = qGram.getValue();
      int val2 = profileLarge.getOrDefault(qGram.getKey(), 0);
      dotProd += val1 * val2;
    }  
    return 1.0 - (double)dotProd / (qFile1.getVectorLength() * qFile2.getVectorLength());
  }

  @Override
  public int getRequiredFileType() {
    return Main.FILETYPE_QGRAMFILE;
  }

}

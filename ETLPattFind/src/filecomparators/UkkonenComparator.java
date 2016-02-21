package filecomparators;

import java.util.Map;

import common.File;
import common.Main;
import common.QGramFile;

public class UkkonenComparator extends FileComparator {

  @Override
  public double distance(File file1, File file2) {
    if (!(file1 instanceof QGramFile) || !(file2 instanceof QGramFile)) {
      System.out.println("ERROR: UkkonenComparator.distance: Incompatable file types.");
      throw new IllegalArgumentException();
    }
    QGramFile qFile1 = (QGramFile) file1;
    QGramFile qFile2 = (QGramFile) file2;
    Map<Integer, Integer> profile1 = qFile1.getProfile();
    Map<Integer, Integer> profile2 = qFile2.getProfile();
    
    int sum = 0;
    for (Map.Entry<Integer, Integer> qGram : profile1.entrySet()) {
      int val1 = qGram.getValue();
      int val2 = profile2.getOrDefault(qGram.getKey(), 0);
      sum += Math.abs(val1 - val2);
    }
    for (Map.Entry<Integer, Integer> qGram : profile2.entrySet()) {
      if (!profile1.containsKey(qGram.getKey())) {
        sum += qGram.getValue();
      }
    }
    return normalizeDist(sum, qFile1.getSetSize(), qFile2.getSetSize());
  }

  @Override
  public int getRequiredFileType() {
    return Main.FILETYPE_QGRAMFILE;
  }

}
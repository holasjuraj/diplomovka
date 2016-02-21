package common;

import java.util.List;

public class SequenceFile extends File {
  private List<String> content = null;

  public SequenceFile(int id, String name) {
    super(id, name);
  }
  
  public SequenceFile(int id, String name, List<String> content) {
    this(id, name);
    this.setContent(content);
  }

  public List<String> getContent() {
    return content;
  }

  public void setContent(List<String> content) {
    this.content = content;
  }
  
  /**
   * @return number of elements in content, or 0 if content is null
   */
  public int size() {
    if (content == null) {
      return 0;
    }
    return content.size();
  }
  
  /**
   * Fetch element in content by specified index.
   * @throws NullPointerException if content is null
   */
  public String get(int index) {
    if (content == null) {
      System.out.println("ERROR: File.get: content not initialized.");
      throw new NullPointerException();
    }
    return content.get(index);
  }
  
}

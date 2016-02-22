package common;

import java.util.List;

/**
 * Type of {@link File} where tokens are stored in sequential fashion - in a {@link List}.
 * @author Juraj
 */
public class SequenceFile extends File {
  private List<String> content = null;

  /**
   * Initialize with no content. Recommended to set content right afterwards.
   */
  public SequenceFile(int id, String name) {
    super(id, name);
  }
  
  /**
   * Initialize and set content right away.
   * @param content list of tokens
   */
  public SequenceFile(int id, String name, List<String> content) {
    this(id, name);
    setContent(content);
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
   * @throws {@link NullPointerException} if content is null
   */
  public String get(int index) {
    if (content == null) {
      System.out.println("ERROR: File.get: content not initialized.");
      throw new NullPointerException();
    }
    return content.get(index);
  }
  
}

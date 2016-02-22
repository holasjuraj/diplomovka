package common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Pack of tools for preprocessing the ETL files:
 * <li>readAndSeparate(path) - reads the input ETL, splits the jobs, and converts them into
 * {@link File} objects of specified type.</li>
 * <li>tokenize(xml) - splits XML into tokens</li>
 * <li>readFile(path) - fast reading of file from disk</li>
 * @author Juraj
 */
public class EtlReader {

  /**
   * Reads input ETL file from disk, separates ETL jobs, and converts each of them into {@link File}
   * object of given type.
   * @param path path to input ETL file
   * @return list of {@link File} objects
   */
  public static List<File> readAndSeparate(String path, int fileType) {
    System.out.println("INFO: Input file reading started.");
    int start = 0;
    int end = 0;
    List<File> result = new ArrayList<>();
    
    String inFile = readFile(path);
    inFile = inFile.replaceAll(">[\\n\\s]*<", "><");    // Remove whitespace between tags
    String inFLower = inFile.toLowerCase();
    
    while ((start = inFLower.indexOf("<job", end)) != -1) {
      end = inFLower.indexOf("</job>", start) + "</job>".length();
      if (end < start) {
        System.out.println("WARN: EtlReader.readAndSeparate: ETL file probably corrupted - "
            + "job not complete.");
        break;
      }
      String jobXml = inFile.substring(start, end);
      int nameStart = -start + inFLower.indexOf("identifier=\"", start) + "identifier='".length();
      int nameEnd = jobXml.indexOf("\"", nameStart);
      String jobName;
      if (nameStart < 0 || nameEnd < nameStart) {
        System.out.println("WARN: EtlReader.readAndSeparate: ETL file probably corrupted - "
            + "job name not found.");
        jobName = "unnamed job";
      } else {
        jobName = jobXml.substring(nameStart, nameEnd);
      }

      File f;
      int newId = result.size();    // result.size is next ID
      switch (fileType) {
        case Main.FILETYPE_QGRAMFILE:
          f = processQGramFile(newId, jobName, jobXml);
          break;
        case Main.FILETYPE_SEQUENCEFILE: // fall-through
        default:
          f = processSequenceFile(newId, jobName, jobXml);
      }
      result.add(f);
    }
    
    System.out.println("INFO: Input file reading finished (" + result.size() + " ETL jobs).");
    return result;
  }
  
  /**
   * Creates a {@link SequenceFile} object from raw text content of the file.
   * @param id ID to be given to the {@link File} object
   * @param jobName file (ETL job) name
   * @param jobXml file content (ETL job body)
   */
  private static SequenceFile processSequenceFile(int id, String jobName, String jobXml) {
    SequenceFile f = new SequenceFile(id, jobName);
    f.setContent(tokenize(jobXml));
    return f;
  }
  
  /**
   * Creates a {@link QGramFile} object from raw text content of the file.
   * @param id ID to be given to the {@link File} object
   * @param jobName file (ETL job) name
   * @param jobXml file content (ETL job body)
   */
  private static QGramFile processQGramFile(int id, String jobName, String jobXml) {
    QGramFile f = new QGramFile(id, jobName);
    f.createProfile(tokenize(jobXml), 2); // TODO extract fixed parameter q!
    return f;
  }

  
  private static final SAXParserFactory saxFactory = SAXParserFactory.newInstance();

  /**
   * Split XML file (passed as string) into list of tokens. Token is:
   * <li>start of opening tag - pair attribute-value</li>
   * <li>end of opening tag - one line in inner body</li>
   * </li>closing tag</li>
   * Tag names are converted to lower case before splitting into tokens.
   * @param xml XML file
   * @return list of tokens
   */
  public static List<String> tokenize(String xml) {
    List<String> result = new ArrayList<String>();
    TokenHandler handler = new TokenHandler(result);
    try {
      SAXParser saxPar = saxFactory.newSAXParser();
      saxPar.parse(new InputSource(new StringReader(xml)), handler);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Handler to separate tokens
   * @author Juraj
   */
  private static class TokenHandler extends DefaultHandler {
    private List<String> tokens;

    public TokenHandler(List<String> tokens) {
      this.tokens = tokens;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
      tokens.add("<" + qName.toLowerCase());
      for (int i = 0; i < attributes.getLength(); i++) {
        tokens.add(attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"");
      }
      tokens.add(">");
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
      tokens.add("</" + qName.toLowerCase() + ">");
    }

    public void characters(char ch[], int start, int length) throws SAXException {
      String[] sList = new String(ch, start, length).split("\n");
      for (String s : sList) {
        tokens.add(s);
      }
    }
  }


  /**
   * Reads a file into a string using {@link BufferedInputStream} (tested to be the fastest option).
   */
  public static String readFile(String path) {
    final int SIZE = 8192; // 8k
    try {
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
      StringBuilder sb = new StringBuilder();
      byte[] bArr = new byte[SIZE];
      int nRead = 0;
      while ((nRead = bis.read(bArr, 0, SIZE)) != -1) {
        for (int i = 0; i < nRead; i++) {
          sb.append((char) bArr[i]);
        }
      }
      bis.close();
      return sb.toString();
    } catch (IOException e) {
      System.out.println("ERROR: EtlReader.readFile: Error reading file " + path + ".");
      e.printStackTrace();
    }
    return null;
  }
  
}

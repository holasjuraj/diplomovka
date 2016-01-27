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

public class XMLTokenizer {
	
	private static SAXParserFactory saxFac = SAXParserFactory.newInstance();
	
	public static List<String> tokenize(String xml){
		String normalized = xml.replaceAll(">[\\n\\s]*<", "><");
		List<String> res = new ArrayList<String>();
		TokenHandler handler = new TokenHandler(res);
		try {
			SAXParser saxPar = saxFac.newSAXParser();
			saxPar.parse(new InputSource(new StringReader(normalized)), handler);
		}
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		catch (SAXException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return res;
	}
	
	private static class TokenHandler extends DefaultHandler{
		private List<String> tokens;
		
		public TokenHandler(List<String> tokens){
			this.tokens = tokens;
		}
		
		public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
			tokens.add("<" + qName.toLowerCase());
			for(int i = 0; i < attributes.getLength(); i++){
				tokens.add(attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"");
			}
			tokens.add(">");
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			tokens.add("</" + qName.toLowerCase() + ">");
		}
		
		public void characters(char ch[], int start, int length) throws SAXException {
			String[] sList = new String(ch, start, length).split("\n");
			for(String s : sList){
				tokens.add(s);
			}
		}
	}

}
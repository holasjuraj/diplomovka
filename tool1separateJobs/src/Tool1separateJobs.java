import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Tool1separateJobs {

	public static void main(String[] args) {
		System.out.println("Started");
		String inPath = "data/output2.xml";
		String outPath = "data/output/";
		String inFile = readFile(inPath);
		int start = 0, end = 0;
		while((start = inFile.indexOf("<Job", end)) != -1){
			end = inFile.indexOf("</Job>", start) + "</Job>".length();
			String job = inFile.substring(start, end);
			int nameStart = job.indexOf("\"") + 1,
				nameEnd = job.indexOf("\"", nameStart);
			String name = job.substring(nameStart, nameEnd);
			name = name.replaceAll("::", "_");
			try {
				System.out.println("writing file: "+outPath+name+".xml");
				PrintWriter writer = new PrintWriter(outPath+name+".xml", "UTF-8");
				writer.print(job);
				writer.close();
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		}
		System.out.println("Finished");
	}
	
	public static String readFile(String path){
		final int SIZE = 8192;	// 8k
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			StringBuilder sb = new StringBuilder();
			byte[] bArr = new byte[SIZE];
			int nRead = 0;
			while((nRead=bis.read(bArr, 0, SIZE)) != -1){
				for(int i = 0; i < nRead; i++){
					sb.append((char)bArr[i]);
				}
			}
			bis.close();
			return sb.toString();
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return null;
	}

}
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TestRead {

	public static void main(String[] args) {
		System.out.println("Started");
		String inputDir = "data/all";
		final List<String> paths = new ArrayList<String>();
		for(File file : (new File(inputDir)).listFiles()){
			if(!file.isDirectory()){
				paths.add(file.getAbsolutePath());
			}
		}
		
		Date start = new Date();
		for(String path : paths){
			readFile(path);
		}
		System.out.println("normal reading: " + ((new Date().getTime()) - start.getTime()) + "ms");
		
		start = new Date();
		for(String path : paths){
			readFileLinesNorm(path);
		}
		System.out.println("per-line reading: " + ((new Date().getTime()) - start.getTime()) + "ms");

		start = new Date();
		for(String path : paths){
			String file = readFile(path);
			BufferedReader br = new BufferedReader(new StringReader(file));
			try {
				List<String> res = new ArrayList<String>();
				String line;
				while((line=br.readLine()) != null){
					line = line.trim();
					line = line.replaceAll("[ \t]{2,}", " ");
					res.add(line);
				}
				br.close();
			}
			catch (IOException e) { e.printStackTrace(); }
		}
		System.out.println("normal reading + BufferedReader split: " + ((new Date().getTime()) - start.getTime()) + "ms");

		start = new Date();
		for(String path : paths){
			String file = readFile(path);
			Scanner sc = new Scanner(file);
			List<String> res = new ArrayList<String>();
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				line = line.trim();
				line = line.replaceAll("[ \t]{2,}", " ");
				res.add(line);
			}
			sc.close();
		}
		System.out.println("normal reading + Scanner split: " + ((new Date().getTime()) - start.getTime()) + "ms");

		start = new Date();
		for(String path : paths){
			String[] lines = readFile(path).split("\n");
			List<String> res = new ArrayList<String>();
			for(String line : lines){
				line = line.trim();
				line = line.replaceAll("[ \t]{2,}", " ");
				res.add(line);
			}
		}
		System.out.println("normal reading + array split: " + ((new Date().getTime()) - start.getTime()) + "ms");
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

	public static List<String> readFileLinesNorm(String path){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			List<String> res = new ArrayList<String>();
			String line;
			while((line=br.readLine()) != null){
				line = line.trim();
				line = line.replaceAll("[ \t]{2,}", " ");
				res.add(line);
			}
			br.close();
			return res;
		}
		catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	
}
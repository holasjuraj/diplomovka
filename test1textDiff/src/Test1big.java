import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Test1big {
	public static final int N_THREADS = 4;
	public static final int FILES_LIMIT = 55;

	public static void main(String[] args) {
		System.out.println("Started");
		Date start = new Date();
		String inputDir = "data/all";
		final List<String> paths = new ArrayList<String>();
		for(File file : (new File(inputDir)).listFiles()){
			if(!file.isDirectory()){
				paths.add(file.getAbsolutePath());
				if(paths.size() >= FILES_LIMIT){
					break;
				}
			}
		}

		final String[] files = new String[paths.size()];
		for(int i = 0; i < paths.size(); i++){
			files[i] = readFile(paths.get(i));
		}
		System.out.println("Reading files: " + ((new Date().getTime()) - start.getTime()) + "ms");
		
		
		final int[] starts = new int[N_THREADS+1];
		for(int i = 0; i < starts.length; i++){
			starts[i] = (int)Math.round( Math.sqrt(i)/Math.sqrt(N_THREADS) * (double)files.length );
		}

//		System.out.println("threadNum\tfNum1\tfNum2\tfile1\tfile2\tdist\tdistL\tper\tperL\tAPX-L\tl1\tl2\tl1L\tl2L\ttime\ttimeL");
//		System.out.println("threadNum\tfNum1\tfNum2\tfile1\tfile2\tdistC\tperC\tl1C\tl2C\ttimeC\tmethodC");
		System.out.println("threadNum\tfNum1\tfNum2\tdistT\tperT\tl1T\tl2T\ttimeT");
		
		Thread[] threads = new Thread[N_THREADS];
		for(int t = 0; t < N_THREADS; t++){
			final int tt = t;
			threads[t] = new Thread(new Runnable() {
				public void run() {
					for(int i = starts[tt]; i < starts[tt+1]; i++){
						for(int j = i-1; j >= 0; j--){
							Date start = new Date();
//							int[] res = editDistMayers(files[i], files[j]);
//							int dist = res[0];
//							int l1 = res[1];
//							int l2 = res[2];
//							double per = (double)dist / (double)(l1 + l2) * 100;
//							long time = (new Date().getTime()) - start.getTime();
//
//							start = new Date();
//							int[] resL = editDistMayersLines(files[i], files[j]);
//							int distL = resL[0];
//							int l1L = resL[1];
//							int l2L = resL[2];
//							double perL = (double)distL / (double)(l1L + l2L) * 100;
//							long timeL = (new Date().getTime()) - start.getTime();
//
//							start = new Date();
//							int[] resC = editDistMayersComb(files[i], files[j]);
//							int distC = resC[0];
//							int l1C = resC[1];
//							int l2C = resC[2];
//							double perC = (double)distC / (double)(l1C + l2C) * 100;
//							long timeC = (new Date().getTime()) - start.getTime();

							start = new Date();
							int[] resT = editDistMayersPerTokens(files[i], files[j]);
							int distT = resT[0];
							int l1T = resT[1];
							int l2T = resT[2];
							double perT = (double)distT / (double)(l1T + l2T) * 100;
							long timeT = (new Date().getTime()) - start.getTime();

//							System.out.println(
//									tt +"\t"+ i +"\t"+ j +"\t"+ paths.get(i) +"\t"+ paths.get(j) +"\t" +
//									dist +"\t"+ distL +"\t"+
//									per +"\t"+ perL +"\t"+ (perL / per) +"\t"+
//									l1 +"\t"+ l2 +"\t"+ l1L +"\t"+ l2L +"\t"+
//									time +"\t"+ timeL
//									);
//
//							System.out.println(
//									tt +"\t"+ i +"\t"+ j +"\t"+ //paths.get(i) +"\t"+ paths.get(j) +"\t" +
//									distC +"\t"+
//									perC +"\t"+
//									l1C +"\t"+ l2C +"\t"+
//									timeC +"\t"+ resC[3]
//									);

							System.out.println(
									tt +"\t"+ i +"\t"+ j +"\t"+ //paths.get(i) +"\t"+ paths.get(j) +"\t" +
									distT +"\t"+
									perT +"\t"+
									l1T +"\t"+ l2T +"\t"+
									timeT
									);
						}
					}
				}
			});
		}
		for(int t = 0; t < N_THREADS; t++){
			threads[t].start();
		}
		for(int t = 0; t < N_THREADS; t++){
			try { threads[t].join(); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		System.out.println("\n\n\nFinished");
		System.out.println("Total time: " + ((new Date().getTime()) - start.getTime()) + "ms");
	}

	private static String readFile(String path){
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

	private static List<String> convertFileToLines(String file){
		try {
			BufferedReader br = new BufferedReader(new StringReader(file));
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
	
	public static int[] editDistMayersPerChar(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1.length(), m = file2.length(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1.length(), file2.length()};
		for(int d = 0; d < max+1; d++){
			for(int k = -d; k < d+1; k+=2){
				int x = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					x = v[max + k+1];
				}
				else{
					x = v[max + k-1] + 1;
				}
				int y = x - k;
				while(x < n  &&  y < m  &&  file1.charAt(x) == file2.charAt(y)){
					x++;
					y++;
				}
				v[max + k] = x;
				if(x >= n  &&  y >= m){
					res[0] = d;
					return res;
				}
			}
		}
		return res;
	}
	
	public static int[] editDistMayersFromList(List<String> file1L, List<String> file2L){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1L.size(), m = file2L.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1L.size(), file2L.size()};
		for(int d = 0; d < max+1; d++){
			for(int k = -d; k < d+1; k+=2){
				int x = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					x = v[max + k+1];
				}
				else{
					x = v[max + k-1] + 1;
				}
				int y = x - k;
				while(x < n  &&  y < m  &&  file1L.get(x).equals(file2L.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;
				if(x >= n  &&  y >= m){
					res[0] = d;
					return res;
				}
			}
		}
		return res;
	}
	
	public static int[] editDistMayersPerLines(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		return editDistMayersFromList(convertFileToLines(file1), convertFileToLines(file2));
	}
	
	public static int[] editDistMayersPerTokens(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		return editDistMayersFromList(XMLTokenizer.tokenize(file1), XMLTokenizer.tokenize(file2));
	}
	
	public static int[] editDistMayersComb(String file1, String file2){
		final double APX_FACTOR = 1.233736968;
		int method = 0;
		int[] res = editDistMayersPerLines(file1, file2);
		double per = (double)(res[0]) / (double)(res[1] + res[2]);
		if(per < 0.15){
			res = editDistMayersPerChar(file1, file2);
			res[0] = (int)Math.round((double)res[0] * APX_FACTOR);
			method = 1;
		}
		return new int[]{res[0], res[1], res[2], method};
	}

}
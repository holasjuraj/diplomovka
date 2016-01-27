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

import mdsj.MDSJ;


public class TestMDS {
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
		
		final int[] starts = new int[N_THREADS+1];
		for(int i = 0; i < starts.length; i++){
			starts[i] = (int)Math.round( Math.sqrt(i)/Math.sqrt(N_THREADS) * (double)files.length );
		}
		
		final double[][] dis = new double[FILES_LIMIT][FILES_LIMIT];
		
		Thread[] threads = new Thread[N_THREADS];
		for(int t = 0; t < N_THREADS; t++){
			final int tt = t;
			threads[t] = new Thread(new Runnable() {
				public void run() {
					for(int i = starts[tt]; i < starts[tt+1]; i++){
						for(int j = i-1; j >= 0; j--){
							int[] resC = editDistMayersComb(files[i], files[j]);
							int distC = resC[0];
							int l1C = resC[1];
							int l2C = resC[2];
							double perC = (double)distC / (double)(l1C + l2C) * 100;
							dis[i][j] = dis[j][i] = perC;
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
		
		System.out.println("file num\tx\ty");
		double[][] mds = MDSJ.classicalScaling(dis);
		for(int i = 0; i < FILES_LIMIT; i++){
			System.out.println(i + "\t" + mds[0][i] + "\t" + mds[1][i]);
		}
		
		System.out.println("\n\nDissimilarity matrix:");
		System.out.print("{");
		for(int i = 0; i < FILES_LIMIT; i++){
			if(i != 0){ System.out.print(","); }
			System.out.print("\n\t{");
			for(int j = 0; j < FILES_LIMIT; j++){
				if(j != 0){ System.out.print(", "); }
				System.out.print(dis[i][j]);
			}
			System.out.print("}");
		}
		System.out.println("\n}");
		
		System.out.print("\n\nFile names:\n{");
		for(int i = 0; i < FILES_LIMIT; i++){
			if(i != 0){ System.out.print(","); }
			String path = paths.get(i);
			System.out.print("\n\t\"" + path.substring(path.lastIndexOf("\\")+1) + "\"");
		}
		System.out.println("\n}");
		
		System.out.println("\n\n\nFinished");
		System.out.println("Total time: " + ((new Date().getTime()) - start.getTime()) + "ms");
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

	public static List<String> convertFileToLines(String file){
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
	
	public static int[] editDistMayers(String file1, String file2){
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
	
	public static int[] editDistMayersLines(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		List<String> file1L = convertFileToLines(file1),
					 file2L = convertFileToLines(file2);
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
	
	public static int[] editDistMayersComb(String file1, String file2){
		final double APX_FACTOR = 1.233736968;
		int method = 0;
		int[] res = editDistMayersLines(file1, file2);
		double per = (double)(res[0]) / (double)(res[1] + res[2]);
		if(per < 0.15){
			res = editDistMayers(file1, file2);
			res[0] = (int)Math.round((double)res[0] * APX_FACTOR);
			method = 1;
		}
		return new int[]{res[0], res[1], res[2], method};
	}

}

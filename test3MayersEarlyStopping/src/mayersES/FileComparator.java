package mayersES;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileComparator {
	public static final int METHOD_PER_TOKEN = 2;
	public static final int METHOD_PER_TOKEN_EARLY_STOP = 4;
	
	public static final int N_THREADS = 1;
	public static final double EARLY_STOPPING_THRESHOLD = 0.15;
	public static final int FILES_LIMIT = 55;
	
	public static String[] lastFilesList = {};

	public static double[][] compareDir(String inputDir, final int method) {
		System.out.println("Comparing started");
		Date start = new Date();
		final List<String> paths = new ArrayList<String>();
		for(File file : (new File(inputDir)).listFiles()){
			if(!file.isDirectory()){
				paths.add(file.getAbsolutePath());
				if(paths.size() >= FILES_LIMIT){
					break;
				}
			}
		}
		
		System.out.println("Read "+paths.size()+" files.");

		final String[] files = new String[paths.size()];
		lastFilesList = new String[paths.size()];
		for(int i = 0; i < paths.size(); i++){
			String path = paths.get(i);
			files[i] = readFile(path);
			lastFilesList[i] = path.substring(Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\")) + 1);
		}
		System.out.println("Reading files: " + ((new Date().getTime()) - start.getTime()) + "ms");
		
		final int[] starts = new int[N_THREADS+1];
		for(int i = 0; i < starts.length; i++){
			starts[i] = (int)Math.round( Math.sqrt(i)/Math.sqrt(N_THREADS) * (double)files.length );
		}
		
		final double[][] dist = new double[files.length][files.length];

		System.out.println("Early stopping threshold = "+EARLY_STOPPING_THRESHOLD);
//		System.out.println("threadNum\tfNum1\tfNum2\tdist\tdistES\tdistES2\tper\tperES\tperES2\ttime\ttimeES\ttimeES2\tl1\tl2");
		System.out.println("threadNum\tfNum1\tfNum2\tdistESexp\tperESexp\ttimeESexp");
		final Object key = new Object();
		
		Thread[] threads = new Thread[N_THREADS];
		for(int t = 0; t < N_THREADS; t++){
			final int tt = t;
			threads[t] = new Thread(new Runnable() {
				public void run() {
					for(int i = starts[tt]; i < starts[tt+1]; i++){
						for(int j = i-1; j >= 0; j--){

//							without early stopping
							Date start = new Date();
//							int[] res = editDist(files[i], files[j]);
//							int rDist = res[0];
//							int l1 = res[1];
//							int l2 = res[2];
//							double per = (double)rDist / (double)(l1 + l2);
//							long time = (new Date().getTime()) - start.getTime();
//							
//							dist[i][j] = dist[j][i] = per;
//
//
//							// with early stopping
//							start = new Date();
//							int[] resES = editDistEarlyStopL1(files[i], files[j]);
//							int rDistES = resES[0];
//							int l1ES = resES[1];
//							int l2ES = resES[2];
//							double perES = (double)rDistES / (double)(l1ES + l2ES);
//							long timeES = (new Date().getTime()) - start.getTime();
//
//
//							// with early stopping L2 norm
//							start = new Date();
//							int[] resES2 = editDistEarlyStopL2(files[i], files[j]);
//							int rDistES2 = resES2[0];
//							int l1ES2 = resES2[1];
//							int l2ES2 = resES2[2];
//							double perES2 = (double)rDistES2 / (double)(l1ES2 + l2ES2);
//							long timeES2 = (new Date().getTime()) - start.getTime();
//
//
							// experiments
							start = new Date();
							int[] resESexp = editDistEarlyStopL1(files[i], files[j]);
							int rDistESexp = resESexp[0];
							int l1ESexp = resESexp[1];
							int l2ESexp = resESexp[2];
							double perESexp = (double)rDistESexp / (double)(l1ESexp + l2ESexp);
							long timeESexp = (new Date().getTime()) - start.getTime();
							
//							dist[i][j] = dist[j][i] = per;

							synchronized (key) {
//								System.out.println(
//										tt +"\t"+ i +"\t"+ j +"\t"+
//										rDist +"\t"+
//										rDistES +"\t"+
//										rDistES2 +"\t"+
//										(per*100) +"\t"+
//										(perES*100) +"\t"+
//										(perES2*100) +"\t"+
//										time +"\t"+
//										timeES +"\t"+
//										timeES2 +"\t"+
//										l1 +"\t"+ l2
//										);
								System.out.println(
										i +"\t"+ j +"\t"+
										rDistESexp +"\t"+
										(perESexp*100) +"\t"+
										timeESexp
										);
							}
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
		System.out.println("Comparing finished, total time: " + ((new Date().getTime()) - start.getTime()) + "ms");
		return dist;
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

	public static int[] editDist(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		List<String> file1L = XMLTokenizer.tokenize(file1),
					 file2L = XMLTokenizer.tokenize(file2);
//		List<String> file1L = convertFileToLines(file1),
//					 file2L = convertFileToLines(file2);
		int n = file1L.size(), m = file2L.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1L.size(), file2L.size()};
//		try {
//		PrintWriter out = new PrintWriter("dump.txt", "UTF-8");
//		System.out.println(max);
//		out.println("##########################################################################");
//		int tempmax = 0;
//		int tempcount = 0;
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
				
//				tempcount++;
//				tempmax = Math.max(tempmax, x+y);
//				if(tempcount%20 == 0){
//					out.println(""+ (x+y) +"\t"+ d);
//					tempcount=0;
//				}
				
				if(x >= n  &&  y >= m){
					res[0] = d;
//					out.flush();
//					out.close();
					return res;
				}
			}
		}
//		out.flush();
//		out.close();
//		}catch (FileNotFoundException e) { e.printStackTrace(); }
//		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		return res;
	}

	public static int[] editDistEarlyStopL1(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		List<String> file1L = XMLTokenizer.tokenize(file1),
					 file2L = XMLTokenizer.tokenize(file2);
//		List<String> file1L = convertFileToLines(file1),
//				 	 file2L = convertFileToLines(file2);
		int n = file1L.size(), m = file2L.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1L.size(), file2L.size()};
		double score = 0;
		for(int d = 0; d < max+1; d++){
			int x = 0, y = 0;
			for(int k = -d; k < d+1; k+=2){
				x = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					x = v[max + k+1];
				}
				else{
					x = v[max + k-1] + 1;
				}
				y = x - k;
				while(x < n  &&  y < m  &&  file1L.get(x).equals(file2L.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;
				score = Math.max(score, x+y);
				if(x >= n  &&  y >= m){
					res[0] = d;
					return res;
				}
			}
			
			if( ((double)d / (double)max) > EARLY_STOPPING_THRESHOLD ){
				// skore x+y aproximuje ako blizko som sa pri poslednom behu (kde dist=d) dostak k uspesnemu vysledku
				// ak skore==max, tak dist==d. ak skore==max/2, tak dist~~d*2
//				System.out.println("max="+max+", d="+d+", score="+score+", count="+tempcount);
				if(score == 0){
					// division by zero case
					res[0] = max;
				}
				else{
					// distance cannot be more than max, hence the Math.min(...)
					res[0] = Math.min(
								(int) Math.round( (double)d * (double)max / score ),
								max
							);
				}
				return res;
			}
		}
		return res;
	}
	
	public static int[] editDistEarlyStopL2(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		List<String> file1L = XMLTokenizer.tokenize(file1),
					 file2L = XMLTokenizer.tokenize(file2);
//		List<String> file1L = convertFileToLines(file1),
//				 	 file2L = convertFileToLines(file2);
		int n = file1L.size(), m = file2L.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1L.size(), file2L.size()};
		double score = 0;
		double diagonal = Math.sqrt(n*n + m*m);
		for(int d = 0; d < max+1; d++){
			int x = 0, y = 0;
			for(int k = -d; k < d+1; k+=2){
				x = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					x = v[max + k+1];
				}
				else{
					x = v[max + k-1] + 1;
				}
				y = x - k;
				while(x < n  &&  y < m  &&  file1L.get(x).equals(file2L.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;
//				score = Math.max(score, x+y);
				double l = Math.sqrt((n-x)*(n-x) + (m-y)*(m-y));
				score = Math.max(score, diagonal-l);
				if(x >= n  &&  y >= m){
					res[0] = d;
					return res;
				}
			}
			
			if( ((double)d / (double)max) > EARLY_STOPPING_THRESHOLD ){
				// skore x+y aproximuje ako blizko som sa pri poslednom behu (kde dist=d) dostak k uspesnemu vysledku
				// ak skore==max, tak dist==d. ak skore==max/2, tak dist~~d*2
//				System.out.println("max="+max+", d="+d+", score="+score+", count="+tempcount);
				if(score == 0){
					// division by zero case
					res[0] = max;
				}
				else{
					// distance cannot be more than max, hence the Math.min(...)
					res[0] = Math.min(
								(int) Math.round( (double)d * diagonal / score ),
								max
							);
				}
				return res;
			}
		}
		return res;
	}
	
	public static int[] editDistEarlyStopL1sqrt(String file1, String file2){
		// Tries to correct previous results - modify from parabola-like shape to linear
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		List<String> file1L = XMLTokenizer.tokenize(file1),
					 file2L = XMLTokenizer.tokenize(file2);
//		List<String> file1L = convertFileToLines(file1),
//				 	 file2L = convertFileToLines(file2);
		int n = file1L.size(), m = file2L.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1L.size(), file2L.size()};
		double score = 0;
		for(int d = 0; d < max+1; d++){
			int x = 0, y = 0;
			for(int k = -d; k < d+1; k+=2){
				x = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					x = v[max + k+1];
				}
				else{
					x = v[max + k-1] + 1;
				}
				y = x - k;
				while(x < n  &&  y < m  &&  file1L.get(x).equals(file2L.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;
				score = Math.max(score, x+y);
				if(x >= n  &&  y >= m){
					res[0] = d;
					return res;
				}
			}
			
			if( ((double)d / (double)max) > EARLY_STOPPING_THRESHOLD ){
				// skore x+y aproximuje ako blizko som sa pri poslednom behu (kde dist=d) dostak k uspesnemu vysledku
				// ak skore==max, tak dist==d. ak skore==max/2, tak dist~~d*2
//				System.out.println("max="+max+", d="+d+", score="+score+", count="+tempcount);
				if(score == 0){
					// division by zero case
					res[0] = max;
				}
				else{
					// distance cannot be more than max, hence the Math.min(...)
					double originalResult = (double)d * (double)max / score;
					double estAbsolute = EARLY_STOPPING_THRESHOLD*(n+m);
					double scaledResult = estAbsolute + Math.sqrt(estAbsolute*estAbsolute - estAbsolute*max
																	+ (max-estAbsolute)*originalResult);
					res[0] = Math.min(
								(int) Math.round(scaledResult),
								max
							);
				}
				return res;
			}
		}
		return res;
	}
		
	public static int[] editDistEarlyStopL2sqrt(String file1, String file2){
		// Tries to correct previous results - modify from parabola-like shape to linear
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		List<String> file1L = XMLTokenizer.tokenize(file1),
					 file2L = XMLTokenizer.tokenize(file2);
//		List<String> file1L = convertFileToLines(file1),
//				 	 file2L = convertFileToLines(file2);
		int n = file1L.size(), m = file2L.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1L.size(), file2L.size()};
		double score = 0;
		double diagonal = Math.sqrt(n*n + m*m);
		for(int d = 0; d < max+1; d++){
			int x = 0, y = 0;
			for(int k = -d; k < d+1; k+=2){
				x = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					x = v[max + k+1];
				}
				else{
					x = v[max + k-1] + 1;
				}
				y = x - k;
				while(x < n  &&  y < m  &&  file1L.get(x).equals(file2L.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;
//				score = Math.max(score, x+y);
				double l = Math.sqrt((n-x)*(n-x) + (m-y)*(m-y));
				score = Math.max(score, diagonal-l);
				if(x >= n  &&  y >= m){
					res[0] = d;
					return res;
				}
			}
			
			if( ((double)d / (double)max) > EARLY_STOPPING_THRESHOLD ){
				// skore x+y aproximuje ako blizko som sa pri poslednom behu (kde dist=d) dostak k uspesnemu vysledku
				// ak skore==max, tak dist==d. ak skore==max/2, tak dist~~d*2
//				System.out.println("max="+max+", d="+d+", score="+score+", count="+tempcount);
				if(score == 0){
					// division by zero case
					res[0] = max;
				}
				else{
					// distance cannot be more than max, hence the Math.min(...)
					double originalResult = (double)d * diagonal / score;
					double estAbsolute = EARLY_STOPPING_THRESHOLD*(n+m);
					double scaledResult = estAbsolute + Math.sqrt(estAbsolute*estAbsolute - estAbsolute*max
																	+ (max-estAbsolute)*originalResult);
					res[0] = Math.min(
								(int) Math.round(scaledResult),
								max
							);
				}
				return res;
			}
		}
		return res;
	}
	
	private static class Score{
		// linear interpolation of L1 and L2 norm:
		//   L1==EST          -> res= 1 *L1 +  0 *L2
		//   L1==EST*MIDPOINT -> res=0.5*L1 + 0.5*L2
		//   L1==max          -> res= 0 *L1 +  1 *L2
		// doesn't work very well
		public static final double MIDPOINT_POSITION = 1.1;	// -times EARLY_STOPPING_THRESHOLD
		private double m, n, diagonal, topScore = 0;
		
		public Score(double n, double m){
			this.n = n;
			this.m = m;
			diagonal = Math.sqrt(n*n + m*m);
		}
		
		public void update(double x, double y){
			double scoreL1 = x+y;
			double distToEnd = Math.sqrt((n-x)*(n-x) + (m-y)*(m-y));
			double scoreL2 = diagonal - distToEnd;
			
			double estAbsolute = EARLY_STOPPING_THRESHOLD*(n+m);
			double midpoindAbsolute = MIDPOINT_POSITION*estAbsolute;
			double u;
			
			if(scoreL1 < midpoindAbsolute){ // interpolation 1
				u = 0.5 * (scoreL1 - estAbsolute)/(midpoindAbsolute-estAbsolute);
			}
			else{ // interpolation 2
				u = 0.5 + 0.5 * (scoreL1 - midpoindAbsolute)/(n+m-midpoindAbsolute);
			}
			double scoreFinal = (1-u)*scoreL1 + u*scoreL2;
			topScore = Math.max(topScore, scoreFinal);
		}
		
		public double get(){ return topScore; }
	}
	public static int[] editDistEarlyStopComb(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		List<String> file1L = XMLTokenizer.tokenize(file1),
					 file2L = XMLTokenizer.tokenize(file2);
//		List<String> file1L = convertFileToLines(file1),
//				 	 file2L = convertFileToLines(file2);
		int n = file1L.size(), m = file2L.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1L.size(), file2L.size()};
		Score score = new Score(n, m);		
		
		for(int d = 0; d < max+1; d++){
			int x = 0, y = 0;
			for(int k = -d; k < d+1; k+=2){
				x = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					x = v[max + k+1];
				}
				else{
					x = v[max + k-1] + 1;
				}
				y = x - k;
				while(x < n  &&  y < m  &&  file1L.get(x).equals(file2L.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;
				score.update(x, y);
				if(x >= n  &&  y >= m){
					res[0] = d;
					return res;
				}
			}
			
			if( ((double)d / (double)max) > EARLY_STOPPING_THRESHOLD ){
				// skore x+y aproximuje ako blizko som sa pri poslednom behu (kde dist=d) dostak k uspesnemu vysledku
				// ak skore==max, tak dist==d. ak skore==max/2, tak dist~~d*2
//				System.out.println("max="+max+", d="+d+", score="+score+", count="+tempcount);
				if(score.get() == 0){
					// division by zero case
					res[0] = max;
				}
				else{
					// distance cannot be more than max, hence the Math.min(...)
					res[0] = Math.min(
								(int) Math.round( (double)d * (double)max / score.get() ),
								max
							);
				}
				return res;
			}
		}
		return res;
	}
}
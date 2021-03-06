package mayersESrefactor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import mayersES.XMLTokenizer;

public class FileComparator {	
	public static final int N_THREADS = 3;
	public static final double EARLY_STOPPING_THRESHOLD = 0.6;
	public static final int FILES_LIMIT = 55;
	
	public static String[] lastFilesList = {};

	public static double[][] compareDir(String inputDir) {
		Date start = new Date();
		// Output redirection
		/* * /
		PrintStream outPs = null;
		try { outPs = new PrintStream(new BufferedOutputStream(new FileOutputStream("results_raw/output.txt"))); }
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		System.setOut(outPs);
		/* */
		// Intro
		System.out.println( "Comparing started:\n" +
							"  dataset: " + inputDir + "\n" +
							"  early stopping threshold (EST): " + EARLY_STOPPING_THRESHOLD + "\n" +
							"  threads: " + N_THREADS + "\n");
		
		// Loading files
		System.out.println("Loading files:");
		final List<String> paths = new ArrayList<String>();
		for(File file : (new File(inputDir)).listFiles()){
			if(!file.isDirectory()){
				paths.add(file.getAbsolutePath());
			}
		}
		Random r = new Random(12345);
		while (paths.size() > FILES_LIMIT) {
		  paths.remove(r.nextInt(paths.size()));
		}
		int filesNum = paths.size();
		System.out.println("  count: " + filesNum + " files.");

		final List<List<String>> files = new ArrayList<List<String>>(filesNum);
		lastFilesList = new String[paths.size()];
		for(int i = 0; i < filesNum; i++){
			String path = paths.get(i);
			String rawFile = readFile(path);
			files.add(XMLTokenizer.tokenize(rawFile));
			lastFilesList[i] = path.substring(Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\")) + 1);
		}
		System.out.println("  time: " + ((new Date().getTime()) - start.getTime()) + "ms");
		
		// Thread organization
		final int[] starts = new int[N_THREADS+1];
		for(int i = 0; i < starts.length; i++){
			starts[i] = (int)Math.round( Math.sqrt(i)/Math.sqrt(N_THREADS) * (double)filesNum );
		}		
		Thread[] threads = new Thread[N_THREADS];
		
		// Result & output initialization
		final double[][] dist = new double[filesNum][filesNum];
		System.out.println("\nResults:");
//		System.out.println("threadNum\tfNum1\tfNum2\tdistL1\tpercentL1\ttimeL1\tlength1\tlength2");
//		System.out.println("threadNum\tfNum1\tfNum2\tdistL2\tpercentL2\ttimeL2\tlength1\tlength2");
//		System.out.println("threadNum\tfNum1\tfNum2\tdist\tdistL2\tpercent\tpercentL2\ttime\ttimeL2\tlength1\tlength2");
//		System.out.println("threadNum\tfNum1\tfNum2\tdistL1\tdistL2\tpercentL1\tpercentL2\ttimeL1\ttimeL2\tlength1\tlength2");
//		System.out.println("threadNum\tfNum1\tfNum2\tdist\tdistL1\tdistL2\tpercent\tpercentL1\tpercentL2\ttime\ttimeL1\ttimeL2\tlength1\tlength2");
		System.out.println("threadNum\tfNum1\tfNum2\tdist\tdistL0\tdistL1\tdistL2\tnormDist\tnormDistL0\tnormDistL1\tnormDistL2\ttime\ttimeL1\ttimeL2\tlength1\tlength2");
		final Object printKey = new Object();
		
		for(int t = 0; t < N_THREADS; t++){
			final int tt = t;
			// Thread implementation
			threads[t] = new Thread(new Runnable() {
				public void run() {
					for(int i = starts[tt]; i < starts[tt+1]; i++){
						for(int j = i-1; j >= 0; j--){
							Date start = new Date();

							/* without earlystopping */
							start = new Date();
							int[] res = editDist(files.get(i), files.get(j));
							long time = (new Date().getTime()) - start.getTime();
							int rDist = res[0];
							int length1 = res[1];
							int length2 = res[2];
							// double percent = (double)rDist / (double)(length1 + length2);
							double normDist = normalizeDist(rDist, length1, length2);
							double dmaxNorm =  EARLY_STOPPING_THRESHOLD;
							double normDistL0 = Math.min(dmaxNorm, normDist);
							int dmax = (int) Math.ceil((length1 + length2) * EARLY_STOPPING_THRESHOLD );
							int rDistL0 = Math.min(dmax, rDist);
							/**/

							/* L1 norm */
							start = new Date();
							int[] resL1 = editDistL1(files.get(i), files.get(j));
							long timeL1 = (new Date().getTime()) - start.getTime();
							int rDistL1 = resL1[0];
							int length1L1 = resL1[1];
							int length2L1 = resL1[2];
							// double percentL1 = (double)rDistL1 / (double)(length1L1 + length2L1);
							double normDistL1 = normalizeDist(rDistL1, length1L1, length2L1);
							/**/

							/* L2 norm */
							start = new Date();
							int[] resL2 = editDistL2(files.get(i), files.get(j));
							long timeL2 = (new Date().getTime()) - start.getTime();
							int rDistL2 = resL2[0];
							int length1L2 = resL2[1];
							int length2L2 = resL2[2];
							// double percentL2 = (double)rDistL2 / (double)(length1L2 + length2L2);
							double normDistL2 = normalizeDist(rDistL2, length1L2, length2L2);
							/**/
							
							// Save result
							dist[i][j] = dist[j][i] = normDistL2;

							// Print results & statistics
							synchronized (printKey) {
								System.out.print(tt +"\t"+ i +"\t"+ j +"\t");	// intro
								System.out.print(rDist +"\t");				// no ES
                System.out.print(rDistL0 +"\t");       //  L0
                System.out.print(rDistL1 +"\t");        //  L1
								System.out.print(rDistL2 +"\t");				 //   L2
								System.out.print(normDist +"\t");		// no ES
                System.out.print(normDistL0 +"\t");  //  L0
                System.out.print(normDistL1 +"\t");   //  L1
								System.out.print(normDistL2 +"\t");		 //   L2
								System.out.print(time +"\t");					// no ES
								System.out.print(timeL1 +"\t");				//  L1
								System.out.print(timeL2 +"\t");				//   L2
								System.out.println(length1L2 +"\t"+ length2L2);	// outro
							}
						}
					}
				}
			});
		}
		
		// Launching threads
		for(int t = 0; t < N_THREADS; t++){
			threads[t].start();
		}
		// Waiting for threads
		for(int t = 0; t < N_THREADS; t++){
			try { threads[t].join(); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		
		// Finalization
		System.out.println("Comparing finished, total time: " + ((new Date().getTime()) - start.getTime()) + "ms");
		/* Redirect output back * /
		outPs.flush();
		outPs.close();
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		System.out.println("Finished");
		/* */
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

	public static int[] editDist(List<String> file1, List<String> file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1.size(),
			m = file2.size(),
			max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1.size(), file2.size()};
		
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
				while(x < n  &&  y < m  &&  file1.get(x).equals(file2.get(y))){
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
	
	public static int[] editDistL1(List<String> file1, List<String> file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1.size(),
			m = file2.size(),
			max = (int) Math.ceil((double)(n + m) * EARLY_STOPPING_THRESHOLD);
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1.size(), file2.size()};
		
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
				while(x < n  &&  y < m  &&  file1.get(x).equals(file2.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;
				if(x >= n  &&  y >= m){
					// Edit distance is d
					res[0] = d;
					return res;
				}
			}
		}
		
		// Edit distance is greater than max
		// skore x+y aproximuje ako blizko som sa pri poslednom behu (kde dist=d) dostak k uspesnemu vysledku
		// ak skore==max, tak dist==d. ak skore==max/2, tak dist~~d*2
		double score = 0;
		for(int k = -max; k < max+1; k+=2){
			int x = v[max + k];
			int y = x - k;
			score = Math.max(score, x+y);
		}
		
		if(score == 0){
			// division by zero case
			res[0] = n+m;
		}
		else{
			// distance cannot be more than n+m, hence the Math.min(...)
			res[0] = Math.min(n+m, (int)Math.round( (double)((n+m)*max) / score ));
		}
		
		return res;
	}
	
	public static int[] editDistL2(List<String> file1, List<String> file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1.size(),
			m = file2.size(),
			max = (int) Math.ceil((double)(n + m) * EARLY_STOPPING_THRESHOLD);
		double diagonal = Math.sqrt(n*n + m*m);
		int[] v = new int[2*max + 1];	// int[-max ... max]
		int[] res = {max, file1.size(), file2.size()};
		
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
				while(x < n  &&  y < m  &&  file1.get(x).equals(file2.get(y))){
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
		
		// Edit distance is greater than max
		// skore x+y aproximuje ako blizko som sa pri poslednom behu (kde dist=d) dostak k uspesnemu vysledku
		// ak skore==max, tak dist==d. ak skore==max/2, tak dist~~d*2
		double score = 0;
		for(int k = -max; k < max+1; k+=2){
			int x = v[max + k];
			int y = x - k;
			double l = Math.sqrt((n-x)*(n-x) + (m-y)*(m-y));
			score = Math.max(score, diagonal-l);
		}
		
		if(score == 0){
			// division by zero case
			res[0] = n+m;
		}
		else{
			// distance cannot be more than n+m, hence the Math.min(...)
			res[0] = Math.min(n+m, (int)Math.round( (double)max*diagonal / score ));
		}
		
		return res;
	}
  
  public static double normalizeDist(int dist, int n, int m){
    return 1.0 - ((double)(n+m-dist) / (double)(2*Math.max(n, m)));
  }

}
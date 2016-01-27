import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test1textDiff {

	public static void main(String[] args) {
		Date start = new Date();
		String[] paths = {
				"data/btl_add_org_core_1_to_ds.xml",
				"data/btl_add_org_work_history_core_1_to_ds.xml",
				"data/btl_add_org_core_2_cc.xml",
				"data/btl_add_org_work_history_core_2_cc.xml",
				"data/btl_add_org_core_seq.xml",
				"data/btl_add_org_work_history_core_seq.xml"
				};
		String[] files = new String[paths.length];
		@SuppressWarnings("unchecked")
		List<String>[] filesL = (List<String>[]) new List<?>[paths.length];
		for(int i = 0; i < paths.length; i++){
			files[i] = readFile(paths[i]);
			filesL[i] = readFileLinesNorm(paths[i]);
		}
		System.out.println("Reading files: " + ((new Date().getTime()) - start.getTime()) + "ms");

		System.out.println("file1\tfile2\tdist\tdistL\tper\tperL\tAPX-L\tl1\tl2\tl1L\tl2L\ttime\ttimeL");
//		System.out.println("file1\tfile2\tdist\tdistL\tdistC\tper\tperL\tperC\tAPX-L\tAPX-C\tl1\tl2\tl1L\tl2L\ttime\ttimeL\ttimeC");
		for(int i = 0; i < paths.length; i++){
			for(int j = i+1; j < paths.length; j++){
				System.out.print(paths[i] +"\t"+ paths[j] +"\t");

				start = new Date();
				int dist = editDistMayers(files[i], files[j]);
				int l1 = files[i].length();
				int l2 = files[j].length();
				double per = (double)dist / (double)(l1 + l2) * 100;
				long time = (new Date().getTime()) - start.getTime();

				start = new Date();
				int distL = editDistMayersLines(filesL[i], filesL[j]);
				int l1L = filesL[i].size();
				int l2L = filesL[j].size();
				double perL = (double)distL / (double)(l1L + l2L) * 100;
				long timeL = (new Date().getTime()) - start.getTime();

//				start = new Date();
//				double distC = editDistMayersCombine(filesL[i], filesL[j]);
//				int l1C = filesL[i].size();
//				int l2C = filesL[j].size();
//				double perC = (double)distC / (double)(l1C + l2C) * 100;
//				long timeC = (new Date().getTime()) - start.getTime();
				
				System.out.println(
						dist +"\t"+ distL +"\t"+
						per +"\t"+ perL +"\t"+ (perL / per) +"\t"+
						l1 +"\t"+ l2 +"\t"+ l1L +"\t"+ l2L +"\t"+
						time +"\t"+ timeL
						);
				
//				System.out.println(
//						dist +"\t"+ distL +"\t"+ distC +"\t"+
//						per +"\t"+ perL +"\t"+ perC +"\t"+ (perL / per) +"\t"+ (perC / per) +"\t"+
//						l1 +"\t"+ l2 +"\t"+ l1L +"\t"+ l2L +"\t"+
//						time +"\t"+ timeL +"\t"+ timeC
//						);
			}
		}
		
		
		
//		String path1 = "data/btl_add_org_core_1_to_ds.xml";
//		String path2 = "data/btl_add_org_work_history_core_1_to_ds.xml";
//		String path1 = "data/btl_add_org_core_seq.xml";
//		String path2 = "data/btl_add_org_work_history_core_seq.xml";
//		String path3 = "data/btl_add_org_core_2_cc.xml";
		
		// Per chars
//		System.out.println("Per chars:");
//		String file1 = readFile(path1);
//		String file2 = readFile(path2);
//		String file3 = readFile(path3);
//		
//		int dist = editDistMayers(file1, file2);
//		int l1 = file1.length();
//		int l2 = file2.length();
//		double per = (double)dist / (double)(l1 + l2) * 100;
//		System.out.println("1 vs. 2: "+dist+", "+per+" % ("+l1+", "+l2+")");
//		System.out.println("time: "+((new Date().getTime()) - start.getTime())+"ms"); start = new Date();
//		
//		dist = editDistMayers(file1, file3);
//		l1 = file1.length();
//		l2 = file3.length();
//		per = (double)dist / (double)(l1 + l2) * 100;
//		System.out.println("1 vs. 3: "+dist+", "+per+" % ("+l1+", "+l2+")");
//		System.out.println("time: "+((new Date().getTime()) - start.getTime())+"ms"); start = new Date();
//		
//		dist = editDistMayers(file2, file3);
//		l1 = file2.length();
//		l2 = file3.length();
//		per = (double)dist / (double)(l1 + l2) * 100;
//		System.out.println("2 vs. 3: "+dist+", "+per+" % ("+l1+", "+l2+")");
//		System.out.println("time: "+((new Date().getTime()) - start.getTime())+"ms"); start = new Date();
		
//		dist = editDistBR(file1, file3);
//		l1 = file1.length();
//		l2 = file3.length();
//		per = (double)dist / (double)(l1 + l2) * 100;
//		System.out.println("1 vs. 3: "+dist+", "+per+" % ("+l1+", "+l2+")");
//		System.out.println("time: "+((new Date().getTime()) - start.getTime())+"ms"); start = new Date();
		
		// Per lines
//		System.out.println("\nPer lines:");
//		List<String> file1L = readFileLines(path1);
//		List<String> file2L = readFileLines(path2);
//		List<String> file3L = readFileLines(path3);
//		start = new Date();
//		
//		dist = editDistMayersLines(file1L, file2L);
//		l1 = file1L.size();
//		l2 = file2L.size();
//		per = (double)dist / (double)(l1 + l2) * 100;
//		System.out.println("1 vs. 2: "+dist+", "+per+" % ("+l1+", "+l2+")");
//		System.out.println("time: "+((new Date().getTime()) - start.getTime())+"ms"); start = new Date();
//		
//		dist = editDistMayersLines(file1L, file3L);
//		l1 = file1L.size();
//		l2 = file3L.size();
//		per = (double)dist / (double)(l1 + l2) * 100;
//		System.out.println("1 vs. 3: "+dist+", "+per+" % ("+l1+", "+l2+")");
//		System.out.println("time: "+((new Date().getTime()) - start.getTime())+"ms"); start = new Date();
//		
//		dist = editDistMayersLines(file1L, file3L);
//		l1 = file1L.size();
//		l2 = file3L.size();
//		per = (double)dist / (double)(l1 + l2) * 100;
//		System.out.println("1 vs. 3: "+dist+", "+per+" % ("+l1+", "+l2+")");
//		System.out.println("time: "+((new Date().getTime()) - start.getTime())+"ms"); start = new Date();

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

	public static List<String> readFileLines(String path){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			List<String> res = new ArrayList<String>();
			String line;
			while((line=br.readLine()) != null){
				res.add(line);
			}
			br.close();
			return res;
		}
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
	
	public static int editDistMayers(String file1, String file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1.length(), m = file2.length(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
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
					return d;
				}
			}
		}
		return max;
	}
	
	public static int editDistMayersLines(List<String> file1, List<String> file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1.size(), m = file2.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
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
					return d;
				}
			}
		}
		return max;
	}
	
	// UNUSED - slow
	public static double editDistMayersCombine(List<String> file1, List<String> file2){
		// An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)
		int n = file1.size(), m = file2.size(), max = n+m;
		int[] v = new int[2*max + 1];	// int[-max ... max]
		double dCorrection[] = new double[2*max + 1];	// double[-max ... max]
		for(int d = 0; d < max+1; d++){
			for(int k = -d; k < d+1; k+=2){
				int x = 0;
				int fromK = 0;
				if(k == -d || (k != d && v[max + k-1] < v[max + k+1])){
					fromK = max + k+1;
					x = v[fromK];
				}
				else{
					fromK = max + k-1;
					x = v[fromK] + 1;
				}
				
				int y = x - k;
				while(x < n  &&  y < m  &&  file1.get(x).equals(file2.get(y))){
					x++;
					y++;
				}
				v[max + k] = x;

				dCorrection[max + k] = dCorrection[fromK];
				if(x < n  &&  y < m){
					double dCor = (double)editDistMayers(file1.get(x), file2.get(y)) / (double)(file1.get(x).length() + file2.get(y).length());
					dCorrection[max + k] += dCor;
				}
						
				if(x >= n  &&  y >= m){
					return (double)d - dCorrection[max + k];
				}
			}
		}
		return max;
	}
	
	// UNUSED - super slow, ultra memory consuming
	public static int editDistBR(String file1, String file2){
		// An Extension of Ukkonen’s Enhanced Dynamic Programming ASM Algorithm (Hal Berghel, David Roach)
		int n = file1.length(),
			m = file2.length(),
			ZERO_K = m,
			MAX_K = n+m+1,
			ZERO_P = 1,
			MAX_P = n+m+2;
		int[][] FKP = new int[MAX_K][MAX_P];
		for(int k = -ZERO_K; k < MAX_K-ZERO_K; k++){
			for(int p = -ZERO_P; p < MAX_P-ZERO_P; p++){
				if(p == Math.abs(k) - 1){
					if(k < 0){
						FKP[k + ZERO_K][p + ZERO_P] = Math.abs(k) - 1;
					}
					else{
						FKP[k + ZERO_K][p + ZERO_P] = -1;
					}
				}
				else if(p < Math.abs(k)){
					FKP[k + ZERO_K][p + ZERO_P] = Integer.MIN_VALUE;
				}
			}
		}
		
		return 0;
	}

}

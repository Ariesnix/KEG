package edu.thu.keg.mdap.dm.uri;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import edu.thu.keg.mdap.dm.uri.Document;
import edu.thu.keg.mdap.dm.uri.LDADataset;
import edu.thu.keg.mdap.dm.uri.MathUtil;



public class TestDirect {
	public static int V = 924;
	public static int L = 265;
	public static int M = 1000;
	public static void main(String[] args) {
		String dir = "D:\\Beijing_result\\finally\\data\\";
		String fileName1 = "ImsiTimeslotLda_Allday_be10_top.txt";
		String fileName2 = "ImsiTimeslotLda_Allday_af10_top.txt";
		LDADataset data = LDADataset.readDataSet(dir + fileName1, M, V, L);
		LDADataset data2 = LDADataset.readDataSet(dir + fileName2, M, V, L);
	
		try{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(dir + "top50000.txt"), "UTF-8"));
			double total = 0, total2 = 0;
			double count = M;
			double count1000 = 1000;
			double total1000 = 0, total2_1000 = 0;
			for (int m = 0; m < M; m++) {
				if (m == 1000) {
					total1000 = total / count1000;
					total2_1000 = total2 / count1000;
				}
				if (m % 500 == 0)
					System.out.println("Processing doc" + m);
				Document d = data2.docs[m];
				Document d2 = data.getDoc(d.imsi);
				if (d.length == 0 || d2 == null || d2.length == 0) {
					count--;
					count1000--;
					continue;
				}
				double [] p = calculateWordDistribution(d);
				
				double [] p2 = calculateWordDistribution(d2);
				smoothWordDistribution(p2);
				
				double [][] locDist = calculateWordLocationDistribution(d2);
				for (int l = 0; l < L; l++) {
					smoothWordDistribution(locDist[l]);
				}
				double [] p3 = new double[V];
				for (int[] arr : d.words) {
					int location = arr[1];
					for (int w = 0; w < V; w++) {
						p3[w] += locDist[location][w]; 
					}
				}
				
				for (int i = 0; i < V; ++i) {
					p3[i] /= d.length;
				}
				double perp = MathUtil.perplexity(p, p2);
				total += perp;
				double perp2 = MathUtil.perplexity(p, p3);
				total2 += perp2;
				writer.write(d.imsi+ " " + perp + " " + perp2 + "\n");
			}
			total /= count;
			total2 /= count;
			writer.write("1000: " + total1000
					+ " " + total2_1000);
			writer.write("All " + count + ": " + total + " " + total2);
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private static void smoothWordDistribution(double[] p) {
		assert(p.length == V);
		double factor = 0.5 / V;
		for (int i = 0; i < p.length; i++) {
			p[i] = p[i]/2 + factor;
		}
	}
	public static double[] calculateWordDistribution(Document d) {
		double [] p = new double[V];
		int w;
		for (int[] arr : d.words) {
			w = arr[0];
			p[w]++;
		}
		for (int i = 0; i < p.length; ++i) {
			p[i] /= d.length;
		}
		return p;
	}
	
	public static double[][] calculateWordLocationDistribution(Document d) {
		double [][] p = new double[L][V];
		double [] sum = new double[L];
		int w,l;
		for (int[] arr : d.words) {
			w = arr[0];
			l = arr[1];
			p[l][w]++;
			sum[l]++;
		}
		for (int i = 0; i < L; ++i) {
			if (sum[i] == 0) {
				p[i] = calculateWordDistribution(d);
			} else {
				for (int j = 0; j < V; ++j) {
					p[i][j] /= sum[i];
				}
			}
		}
		return p;
	}
}

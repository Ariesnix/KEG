/**
 * 
 */
package edu.thu.keg.mdap.dm.uri;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Yuanchao Ma
 *
 */
public class Tester {
	Model model;
	LDADataset testData;
	
	boolean topicTest;
	boolean locationTest;
	boolean directTest;
	
	String dataFile;
	String resultFile;
	
	class DocWordDistribution {
		Document doc;
		private double[] calculatedDist;
		private double[] directTrainDist;
		private double[] estimatedDist;
		private double[] estimatedDistWithouLocation;
		
		public DocWordDistribution(Document doc) {
			super();
			this.doc = doc;
			this.calculatedDist = null;
			this.directTrainDist = null;
			this.estimatedDist = null;
			this.estimatedDistWithouLocation = null;
		}
		public double[] getCalculatedDist() {
			if (calculatedDist == null) {
				calculatedDist = model.calculateWordDistribution(this.doc);
			}
			return calculatedDist;
		}
		
		public double[] getDirectTrainDist() {
			if (directTrainDist == null) {
				Document trainD = model.data.getDoc(doc.imsi);
				if (trainD == null || trainD.length == 0)
					return new double[model.V];
				else	
					directTrainDist = model.calculateWordDistribution(trainD);
				
			}
			return directTrainDist;
		}

		public double[] getEstimatedDist() {
			if (estimatedDist == null) {
				estimatedDist = model.estimateWordDistribution(this.doc);
			}
			return estimatedDist;
		}

		public double[] getEstimatedDistWithouLocation() {
			if (estimatedDistWithouLocation == null) {
				estimatedDistWithouLocation = 
						model.estimateWordDistributionWithoutLocation(this.doc);
			}
			return estimatedDistWithouLocation;
		}
		
		
		
		
		
	}
	DocWordDistribution[] docDistributions;
	public void init(LDACmdOption option, Model m) {
		this.model = m;
		String dir = option.dir;
		
		this.topicTest = option.topicTest;
		this.directTest = option.directTest;
		this.locationTest = option.locationTest;
		
		if (option.testAll) {
			this.topicTest = true;
			this.directTest = true;
			this.locationTest = true;
		}
		
		if (!option.testFile.isEmpty()) {
			this.dataFile = dir + File.separator + option.testFile;	
			testData = LDADataset.readDataSet(dataFile, option.testDocCount, model.V, model.L);
		}
		if (!option.resSubDir.isEmpty()) {
			dir = dir + File.separator + option.resSubDir;
		}
		this.resultFile = dir + File.separator + "test-result.txt";
		
		docDistributions = new DocWordDistribution[testData.M];
		for (int i = 0; i < testData.M; i++) {
			docDistributions[i] = new DocWordDistribution(testData.docs[i]);
		}
	}
	
	private void testPerplexity() {
		System.out.println("Evaluating...");
		try{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(resultFile), "UTF-8"));
			double total = 0, directTotal = 0, topicTotal = 0,
					locationTotal = 0;
			double count = testData.M;
			for (int i = 0; i < testData.M; i++) {
				DocWordDistribution dist = docDistributions[i];
				if (dist.doc.length == 0 || model.data.getDocIdx(dist.doc.imsi) == -1) {
					count--;
					continue;
				}
				writer.write(dist.doc.imsi);
				double [] testP = dist.getCalculatedDist();
				double per0 = MathUtil.perplexity(testP, testP);
				total += per0;
				writer.write(" " + per0);
				if (directTest) {
					double [] d1 = dist.getDirectTrainDist();
					double per = MathUtil.perplexity(testP, d1);
					directTotal += per;
					writer.write(" " + per);
				}
				if (topicTest) {
					double [] d1 = dist.getEstimatedDistWithouLocation();
					double per = MathUtil.perplexity(testP, d1);
					topicTotal += per;
					writer.write(" " + per);
				}
				if (locationTest) {
					double [] d1 = dist.getEstimatedDist();
					double per = MathUtil.perplexity(testP, d1);
					locationTotal += per;
					writer.write(" " + per);
				}
				writer.write("\n");
				if (i % 500 == 0) {
					System.out.println("Testing data file " + i + "..." + LDA.getTime());
				}
			}
			writer.write("All ");
			total /= count;
			writer.write(" " + total);
			if (directTest) {
				directTotal /= count;
				writer.write(" " + directTotal);
			}
			if (topicTest) {
				topicTotal /= count;
				writer.write(" " + topicTotal);
			}
			if (locationTest) {
				locationTotal /= count;
				writer.write(" " + locationTotal);
			}
			writer.close();
		} catch (Exception ex) {
			
		}
	}
	
	public void test() {
		testPerplexity();
	}


}

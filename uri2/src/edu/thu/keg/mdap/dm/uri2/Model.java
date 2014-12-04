/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package edu.thu.keg.mdap.dm.uri2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Model {	
	
	//---------------------------------------------------------------
	//	Class Variables
	//---------------------------------------------------------------
	
	public static String tassignSuffix;	//suffix for topic assignment file
	public static String thetaSuffix;		//suffix for theta (topic - document distribution) file
	public static String phiSuffix;		//suffix for phi file (topic - word distribution) file
	public static String phiLSuffix;		//suffix for phi_L file (topic - location - word distribution) file
	public static String othersSuffix; 	//suffix for containing other parameters
	public static String twordsSuffix;		//suffix for file containing words-per-topics
	public static String binarySuffix;
	
	//---------------------------------------------------------------
	//	Model Parameters and Variables
	//---------------------------------------------------------------
	
	public String trainlogFile; 	//training log file	
	
	public Dictionary dict;
	public Dictionary locDict;
	
	public String dir;
	public String resSubDir;
	public String dfile;
	public String modelName;
	public int modelStatus; 		//see Constants class for status of model
	public LDADataset data;			// link to a dataset
	
	public int M; //dataset size (i.e., number of docs)
	public int V; //vocabulary size
	public int K; //number of topics
	public int L;
	public double alpha, beta; //LDA  hyperparameters
	public int niters; //number of Gibbs sampling iteration
	public int liter; //the iteration at which the model was saved	
	public int savestep; //saving period
	public int twords; //print out top words per each topic
	public int withrawdata;
	
	// Estimated/Inferenced parameters
	/**
	 * theta: document - topic distributions, size M x K
	 */
	public double [][] theta; 
	
	/**
	 * phi: topic-word distributions, size K x V
	 */
	public double [][] phi; 
	/**
	 * theta_l: document-location-topic distributions, size M x L x K
	 */
	public double [][][] theta_l;
	/**
	 * psi: location-topic distributions, size L x K
	 */
	public double[][] psi;
	/**
	 * rpsi: reversed psi. topic-location distributions, size K x L
	 */
	public double[][] rpsi;
	/**
	 * topic_dist: topic distribution over whole corpus. size K
	 */
	public double [] topic_dist;
	/**
	 * topic_dist: location distribution over whole corpus. size L
	 */
	public double [] location_dist;
	/**
	 * topic words, size K
	 */
	public List<Pair<Double> > [] topic_words;
	
	
	// Temp variables while sampling
	public Vector<Integer> [] z; //topic assignments for words, size M x doc.size()
		/**
	 * nw[i][j]: number of instances of word/term i assigned to topic j, size V x K
	 */
	protected int [][] nw; 
	/**
	 * nd[i][j]: number of words in document i assigned to topic j, size M x K
	 */
	protected int [][] nd; 
	/**
	 * nd[i][j]: number of words in document i assigned to topic j under location l, size M x L x K
	 */
	protected int [][][] ndl; 
	/**
	 * nwsum[j]: total number of words assigned to topic j, size K
	 */
	protected int [] nwsum; 
	/**
	 * ndlsum[i][l]: total number of words words in document i under location l, size M x L
	 */
	protected int [][] ndlsum; 
	/**
	 * ndsum[i]: total number of words in document i, size M
	 */
	protected int [] ndsum; 
	/**
	 * nl[i][j]: number of words in location i assigned to topic j, size L x K
	 */
	protected int [][] nl;
	/**
	 * nlsum[i]: total number of words in location i, size L
	 */
	protected int [] nlsum; 
	
	
	// temp variables for sampling
	protected double [] p; 
	
	//---------------------------------------------------------------
	//	Constructors
	//---------------------------------------------------------------	

	public Model(){
		setDefaultValues();	
		//setWordMapFile("wordMap.txt");
	}
	
	/**
	 * Set default values for variables
	 */
	public void setDefaultValues(){
		
		trainlogFile = "trainlog.txt";
		tassignSuffix = ".tassign";
		thetaSuffix = ".theta";
		phiSuffix = ".phi";
		phiLSuffix = ".phil";
		othersSuffix = ".others";
		twordsSuffix = ".twords";
		binarySuffix = ".dat";
		
		dir = "./";
		dfile = "trndocs.dat";
		modelName = "model-final";
		modelStatus = Constants.MODEL_STATUS_UNKNOWN;		
		
		M = 0;
		V = 0;
		K = 100;
		alpha = 50.0 / K;
		beta = 0.1;
		niters = 2000;
		liter = 0;
		
		z = null;
		nw = null;
		ndl = null;
		nd = null;
		nwsum = null;
		ndsum = null;
		ndlsum = null;
		theta = null;
		phi = null;
	}
	
	//---------------------------------------------------------------
	//	I/O Methods
	//---------------------------------------------------------------
	/**
	 * read other file to get parameters
	 */
	protected boolean readOthersFile(String otherFile){
		//open file <model>.others to read:
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(otherFile));
			String line;
			while((line = reader.readLine()) != null){
				StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");
				
				int count = tknr.countTokens();
				if (count != 2)
					continue;
				
				String optstr = tknr.nextToken();
				String optval = tknr.nextToken();
				
				if (optstr.equalsIgnoreCase("alpha")){
					alpha = Double.parseDouble(optval);					
				}
				else if (optstr.equalsIgnoreCase("beta")){
					beta = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("ntopics")){
					K = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("liter")){
					liter = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nwords")){
					V = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("ndocs")){
					M = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nlocations")){
					L = Integer.parseInt(optval);
				}
			}
			
			reader.close();
		}
		catch (Exception e){
			System.out.println("Error while reading other file:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected boolean readTAssignFile(String tassignFile){
		BufferedReader reader = null;
		try {
			int i,j;
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(tassignFile), "UTF-8"));
			
			String line;
			z = new Vector[M];			
			data = new LDADataset(M, V, L);
			data.V = V;			
			for (i = 0; i < M; i++){
				line = reader.readLine();
				StringTokenizer tknr = new StringTokenizer(line, " \t\r\n");
				String imsi = tknr.nextToken();
				int length = tknr.countTokens();
				
				Vector<int[]> words = new Vector<int[]>();
				Vector<Integer> topics = new Vector<Integer>();
				
				for (j = 0; j < length; j++){
					String token = tknr.nextToken();
					
					StringTokenizer tknr2 = new StringTokenizer(token, ":");
					if (tknr2.countTokens() != 3){
						System.out.println("Invalid word-location-topic assignment line\n");
						return false;
					}
					int arr[] = {Integer.parseInt(tknr2.nextToken()),
							Integer.parseInt(tknr2.nextToken())
					};
					words.add(arr);
					topics.add(Integer.parseInt(tknr2.nextToken()));
				}//end for each topic assignment
				
				//allocate and add new document to the corpus
				Document doc = new Document(words, imsi);
				data.setDoc(doc, i);
				
				//assign values for z
				z[i] = new Vector<Integer>();
				for (j = 0; j < topics.size(); j++){
					z[i].add(topics.get(j));
				}
				
			}//end for each doc
			
		}
		catch (Exception e){
			System.out.println("Error while loading model: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * load saved model
	 */
	public boolean loadModel(){
		String d = dir;
		if (!resSubDir.isEmpty())
			d = dir + File.separator + resSubDir;
		if (!readOthersFile(d + File.separator + modelName + othersSuffix))
			return false;
		
		if (!readTAssignFile(d + File.separator + modelName + tassignSuffix))
			return false;
		
		
		return true;
	}
	
	/**
	 * Save word-topic assignments for this model
	 */
	public boolean saveModelTAssign(String filename){
		int i, j;
		
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			//write docs with topic assignments for words
			for (i = 0; i < data.M; i++){
				writer.write(data.docs[i].imsi + " ");
				for (j = 0; j < data.docs[i].length; ++j){
					writer.write(data.docs[i].words[j][0] + ":"
						+ data.docs[i].words[j][1] + ":" + z[i].get(j) + " ");					
				}
				writer.write("\n");
			}
				
			writer.close();
		}
		catch (Exception e){
			System.out.println("Error while saving model tassign: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Save theta (topic distribution) for this model
	 */
	public boolean saveModelTheta(String filename){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (int i = 0; i < M; i++){
				for (int j = 0; j < K; j++){
					writer.write(theta[i][j] + " ");
				}
				writer.write("\n");
			}
			writer.close();
		}
		catch (Exception e){
			System.out.println("Error while saving topic distribution file for this model: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Save word-topic distribution
	 */
	
	public boolean saveModelPhi(String filename){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			for (int i = 0; i < K; i++){
				for (int j = 0; j < V; j++){
					writer.write(phi[i][j] + " ");
				}
				writer.write("\n");
			}
			writer.close();
		}
		catch (Exception e){
			System.out.println("Error while saving word-topic distribution:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Save word-location-topic distribution
	 */
	
	public boolean saveModelTheta_L(String filename){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			for (int m = 0; m < M; m++){
				for (int l = 0; l < L; l++) {
					for (int k = 0; k < K; k++){
						writer.write(theta_l[m][l][k] + " ");
					}
					writer.write("\n");
				}
			}
			writer.close();
		}
		catch (Exception e){
			System.out.println("Error while saving word-topic distribution:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Save other information of this model
	 */
	public boolean saveModelOthers(String filename){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			writer.write("alpha=" + alpha + "\n");
			writer.write("beta=" + beta + "\n");
			writer.write("ntopics=" + K + "\n");
			writer.write("ndocs=" + M + "\n");
			writer.write("nwords=" + V + "\n");
			writer.write("liters=" + liter + "\n");
			writer.write("nlocations=" + L + "\n");
			
			writer.close();
		}
		catch(Exception e){
			System.out.println("Error while saving model others:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Save location information of this model
	 */
	public boolean saveModelLocations(String filename){
		try{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), "UTF-8"));
			List<Pair<Double> > rankMap = new ArrayList<Pair<Double> >(L);
			for (int i = 0; i < L; i++) {
				rankMap.add(new Pair<Double>(i, location_dist[i], false));
			}
			Collections.sort(rankMap);
			for (int rank = 0; rank < L; rank++){
				int l = (int)rankMap.get(rank).first;
				List<Pair<Double> > topicsProbsList
				  = new ArrayList<Pair<Double> >(K);
				for (int k = 0; k < K; k++) {
					Pair<Double> p = new Pair<Double>
					(k, psi[l][k], false);
					topicsProbsList.add(p);
				}
				Collections.sort(topicsProbsList);
				
				writer.write("Location " + rank + "th:\n");
				writer.write("ID: " + l + "\tName: " + locDict.getWord(l) + "\tWeight: " + location_dist[l] + "\n");
				
				int nt = 20;
				if (nt > K)
					nt = K;
				
				for (int i = 0; i < nt; i++) {
					int k = (Integer)topicsProbsList.get(i).first;
					double weight = topicsProbsList.get(i).second;
					writer.write("\t" + k + "\t" + weight);
					for (int w = 0; w < twords; w++) {
						int wordIndex = (Integer)topic_words[k].get(w).first;
						writer.write("\t" + dict.getWord(wordIndex));
					}
					writer.write("\n");
				}
				
			}
			
			writer.close();
		}
		catch(Exception e){
			System.out.println("Error while saving model others:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void computeTwords() {

		topic_words = new List[K];
		for (int k = 0; k < K; k++) {
			topic_words[k] = new ArrayList<Pair<Double> >(V);
			for (int w = 0; w < V; w++){
				Pair<Double> p = new Pair<Double>
					(w, phi[k][w], false);
				topic_words[k].add(p);
			}//end foreach word
	
			Collections.sort(topic_words[k]);
		}
	}
	
	/**
	 * Save model the most likely words for each topic
	 */
	public boolean saveModelTwords(String filename){
		computeTwords();
		try{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), "UTF-8"));
			if (twords > V){
				twords = V;
			}
			System.out.println("Calculating divergence..." + LDA.getTime());
			double divergence = calculateTopicDivergence();
			writer.write("JSD: " + divergence + "\n");
			System.out.println("Writing twords..." + LDA.getTime());
			
			List<Pair<Double> > rankMap = new ArrayList<Pair<Double> >(K);
			for (int i = 0; i < K; i++) {
				rankMap.add(new Pair<Double>(i, topic_dist[i], false));
			}
			Collections.sort(rankMap);
			
			for (int rank = 0; rank < K; rank++){
				int k = (int)rankMap.get(rank).first;
				List<Pair<Double> > wordsProbsList
				  = topic_words[k]; 
				List<Pair<Double> > locsProbsList
				  = new ArrayList<Pair<Double> >(L); 
				
				for (int l = 0; l < L; l++) {
					Pair<Double> p = new Pair<Double>
						(l, rpsi[k][l], false);
					locsProbsList.add(p);
				}
				
				//print topic				
				writer.write("Topic " + rank + "th:\n");
				writer.write("Index: " + k +"\tWeight: " + topic_dist[k] + "\n");
				Collections.sort(locsProbsList);
				
				for (int i = 0; i < twords; i++){
					if ((Integer)wordsProbsList.get(i).first < V){
						String word = dict.getWord((Integer)wordsProbsList.get(i).first);
						writer.write("\t" + word + " " + wordsProbsList.get(i).second);
						String loc = locDict.getWord((Integer)locsProbsList.get(i).first);
						writer.write("\t\t\t" + loc
								+ " " + locsProbsList.get(i).second);
						writer.write("\n");
					}
				}
			} //end foreach topic			
						
			writer.close();
		}
		catch(Exception e){
			System.out.println("Error while saving model twords: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private double calculateTopicDivergence() {
		double divergence = 0;
		System.out.println("Calculating word divergence..." + LDA.getTime());
		for (int i = 0; i < K; i++) {
			double [] p1 = phi[i];
			for (int j = i + 1; j < K; j++) {
				double [] p2 = phi[j];
				double jsd = MathUtil.jensenShannonDivergence(p1, p2);
				divergence += jsd;
			}
		}		
		divergence = divergence * 2 / (K * K - K);
		return divergence;
	}

	/**
	 * Save model
	 */
	public boolean saveModel(String modelName){
		String d = dir;
		if (!resSubDir.isEmpty()) {
			d = d + File.separator + resSubDir;
			File f = new File(d);
			if (!f.exists())
				f.mkdir();
		}

		System.out.println("Saving twords..." + LDA.getTime());
		if (twords > 0){
			if (!saveModelTwords(d + File.separator + modelName + twordsSuffix))
				return false;
		}
		System.out.println("Saving locations..." + LDA.getTime());
		if (!saveModelLocations(d + File.separator + modelName + ".loc")){
			return false;
		}
		System.out.println("Saving tassign..." + LDA.getTime());
		if (!saveModelTAssign(d + File.separator + modelName + tassignSuffix)){
			return false;
		}
		System.out.println("Saving others..." + LDA.getTime());
		if (!saveModelOthers(d + File.separator + modelName + othersSuffix)){			
			return false;
		}
		System.out.println("Saving theta..." + LDA.getTime());
		if (!saveModelTheta(d + File.separator + modelName + thetaSuffix)){
			return false;
		}
		System.out.println("Saving phi..." + LDA.getTime());
		if (!saveModelPhi(d + File.separator + modelName + phiSuffix)){
			return false;
		}
		
		
		return true;
	}
	
	//---------------------------------------------------------------
	//	Init Methods
	//---------------------------------------------------------------
	/**
	 * initialize the model
	 */
	protected boolean init(LDACmdOption option){		
		if (option == null)
			return false;
		
		modelName = option.modelName;
		K = option.K;
		
		alpha = option.alpha;
		if (alpha < 0.0) {
			alpha = 50.0 / K;
		}
		
		if (option.beta >= 0)
			beta = option.beta;
		
		niters = option.niters;
		
		dir = option.dir;
		resSubDir = option.resSubDir;
		if (dir.endsWith(File.separator))
			dir = dir.substring(0, dir.length() - 1);
		
		dfile = option.dfile;
		twords = option.twords;
		dict = new Dictionary();
		locDict = new Dictionary();
		dict.readWordMap(this.dir + File.separator + option.wordMapFileName);
		locDict.readWordMap(this.dir + File.separator + option.locMapFileName);
		
		return true;
	}
	
	/**
	 * Init parameters for estimation
	 */
	public boolean initNewModel(LDACmdOption option){
		if (!init(option))
			return false;
		
			
		p = new double[K];		
		
		data = LDADataset.readDataSet(dir + File.separator + dfile, option.trainDocCount, option.V, option.L);
		if (data == null){
			System.out.println("Fail to read training data!\n");
			return false;
		}
		
		//+ allocate memory and assign values for variables		
		M = data.M;
		V = data.V;
		L = data.L;
		dir = option.dir;
		resSubDir = option.resSubDir;
		savestep = option.savestep;

		initWithDataset(data, true);

		return true;
	}
	@SuppressWarnings("unchecked")
	private void initWithDataset (LDADataset data, boolean randomInit) {
		// K: from command line or default value
	    // alpha, beta: from command line or default values
	    // niters, savestep: from command line or default values
		int m, n, w, k, l;	
		ndl = new int[M][L][K];
		for (m = 0; m < M; m++){
			for (l = 0; l < L; l++) {
				for (k = 0; k < K; k++){
					ndl[m][l][k] = 0;
				}
			}
		}
		nw = new int[V][K];
		for (w = 0; w < V; w++){
			for (k = 0; k < K; k++){
				nw[w][k] = 0;	
			}
		}

		
		nd = new int[M][K];
		for (m = 0; m < M; m++){
			for (k = 0; k < K; k++){
				nd[m][k] = 0;
			}
		}
		
		nwsum = new int[K];
		for (k = 0; k < K; k++){
			nwsum[k] = 0;
		}
		
		ndlsum = new int[M][L];
		for (m = 0; m < M; m++){
			for (l = 0; l < L; l++) {
				ndlsum[m][l] = 0;
			}
		}
		
		ndsum = new int[M];
		for (m = 0; m < M; m++){
			ndsum[m] = 0;
		}
		nl = new int[L][K];
		for (l = 0; l < L; l++){
			for (k = 0; k < K; k++) {
				nl[k][k] = 0;
			}
		}
		nlsum = new int[L];
		for (l = 0; l < L; l++){
			ndsum[l] = 0;
		}
		if (randomInit) {
			z = new Vector[M];
			for (m = 0; m < data.M; m++){
				int N = data.docs[m].length;
				z[m] = new Vector<Integer>();
				
				//initilize for z
				for (n = 0; n < N; n++){
					int topic = (int)Math.floor(Math.random() * K);
					z[m].add(topic);
					
					int _w = data.docs[m].words[n][0];
					int _l = data.docs[m].words[n][1];
					addTopic(_w, _l, m, topic, 1);
				}
			}
		} else {
			for (m = 0; m < data.M; m++){
				int N = data.docs[m].length;
				
				//initilize for z
				for (n = 0; n < N; n++){
					int topic = z[m].get(n);					
					
					int _w = data.docs[m].words[n][0];
					int _l = data.docs[m].words[n][1];
					addTopic(_w, _l, m, topic, 1);
				}
			}
		}
		
		theta = new double[M][K];		
		phi = new double[K][V];
		theta_l = new double[M][L][K];
		psi = new double[L][K];
		topic_dist = new double[K];
		rpsi = new double[K][L];
		location_dist = new double[L];
		
	}
	
	/**
	 * Init parameters for inference
	 * @param newData DataSet for which we do inference
	 */
	public boolean initNewModel(LDACmdOption option, LDADataset newData, Model trnModel){
		if (!init(option))
			return false;
		
		
		K = trnModel.K;
		alpha = trnModel.alpha;
		beta = trnModel.beta;		
		
		p = new double[K];
		System.out.println("K:" + K);
		
		data = newData;
		
		//+ allocate memory and assign values for variables		
		M = data.M;
		V = data.V;
		dir = option.dir;
		resSubDir = option.resSubDir;
		savestep = option.savestep;
		System.out.println("M:" + M);
		System.out.println("V:" + V);
		
		initWithDataset(newData, true);
		
		return true;
	}
	
	
	/**
	 * Init parameters for inference
	 * reading new dataset from file
	 */
	public boolean initNewModel(LDACmdOption option, Model trnModel){
		if (!init(option))
			return false;
		
		LDADataset dataset = LDADataset.readDataSet(dir + File.separator + dfile,
				option.trainDocCount, option.V, option.L);
		if (dataset == null){
			System.out.println("Fail to read dataset!\n");
			return false;
		}
		
		return initNewModel(option, dataset , trnModel);
	}
	
	/**
	 * init parameter for continue estimating or for later inference
	 */
	public boolean initEstimatedModel(LDACmdOption option){
		System.out.println("Loading model..." + LDA.getTime());
		if (!init(option))
			return false;
		
		
		p = new double[K];
		
		// load model, i.e., read z and trndata
		if (!loadModel()){
			System.out.println("Fail to load word-topic assignment file of the model!\n");
			return false;
		}
		
		System.out.println("Model loaded:");
		System.out.println("\talpha:" + alpha);
		System.out.println("\tbeta:" + beta);
		System.out.println("\tM:" + M);
		System.out.println("\tV:" + V);		
		
		initWithDataset(data, false);
		
	    dir = option.dir;
	    resSubDir = option.resSubDir;
		savestep = option.savestep;
	    
		return true;
	}
	/**
	 * Add (or subtract, if count is negative) a topic assignment to
	 * a word w in a document m under a location
	 * @param w
	 * @param l
	 * @param topic
	 * @param count
	 */
	public void addTopic(int w, int l, int m, int topic, int count) {
		ndl[m][l][topic] += count;
		nw[w][topic] += count;
		nd[m][topic] += count;
		nwsum[topic] += count;
		ndlsum[m][l] += count;
		ndsum[m] += count;
		nl[l][topic] += count;
		nlsum[l] += count;
	}
	
	public double[] calculateWordDistribution(Document d) {
		double [] p = new double[this.V];
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
	
	public double[] estimateWordDistribution(Document d) {
		double [] p = new double[this.V];
		int idx = this.data.getDocIdx(d.imsi);
		double [][] topicDist;
		if (idx != -1 && data.docs[idx].length != 0)
			topicDist = theta_l[idx];
		else
			topicDist = psi;
		double [][] wordDist = new double[this.L][this.V];
		for (int l = 0; l < L; l++) {
			for (int w = 0; w < V; w++) {
				wordDist[l][w] = 0;
				for (int k = 0; k < K; k++) {
					wordDist[l][w] += phi[k][w] * topicDist[l][k];
				}
			}
		}
		for (int[] arr : d.words) {
			int location = arr[1];
			for (int w = 0; w < V; w++) {
				p[w] += wordDist[location][w];
			}
		}
		for (int i = 0; i < p.length; ++i) {
			p[i] /= d.length;
		}
		return p;
	}
	
	public double[] estimateWordDistributionWithoutLocation(Document d) {
		double [] p = new double[this.V];
		int idx = this.data.getDocIdx(d.imsi);
		double [] topicDist;
		if (idx != -1 && data.docs[idx].length != 0)
			topicDist = theta[idx];
		else
			topicDist = topic_dist;
		for (int w = 0; w < V; w++) {
			p[w] = 0;
			for (int k = 0; k < K; k++) {
				p[w] += phi[k][w] * topicDist[k];
			}
		}
		return p;
	}
}

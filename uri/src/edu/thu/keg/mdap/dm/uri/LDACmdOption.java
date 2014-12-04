package edu.thu.keg.mdap.dm.uri;

import org.kohsuke.args4j.*;

public class LDACmdOption {
	
	@Option(name="-est", usage="Specify whether we want to estimate model from scratch")
	public boolean est = false;
	
	@Option(name="-estc", usage="Specify whether we want to continue the last estimation")
	public boolean estc = false;
	
	@Option(name="-inf", usage="Specify whether we want to do inference")
	public boolean inf = true;
	
	@Option(name="-dir", usage="Specify directory")
	public String dir = "";
	
	@Option(name="-dfile", usage="Specify data file")
	public String dfile = "";
	
	@Option(name="-model", usage="Specify the model name")
	public String modelName = "model";
	
	@Option(name="-alpha", usage="Specify alpha")
	public double alpha = -1.0;
	
	@Option(name="-beta", usage="Specify beta")
	public double beta = -1.0;
	
	@Option(name="-ntopics", usage="Specify the number of topics")
	public int K = 100;
	
	@Option(name="-vocab_size", usage="Specify the size of vocabulary")
	public int V = 100;
	
	@Option(name="-nlocs", usage="Specify the number of locations")
	public int L = 100;
	
	@Option(name="-niters", usage="Specify the number of iterations")
	public int niters = 1000;
	
	@Option(name="-savestep", usage="Specify the number of steps to save the model since the last save")
	public int savestep = 100;
	
	@Option(name="-twords", usage="Specify the number of most likely words to be printed for each topic")
	public int twords = 100;
	
	@Option(name="-withrawdata", usage="Specify whether we include raw data in the input")
	public boolean withrawdata = false;
	
	@Option(name="-wordmap", usage="Specify the wordmap file")
	public String wordMapFileName = "wordmap.txt";
	
	@Option(name="-locmap", usage="Specify the wordmap file")
	public String locMapFileName = "locmap.txt";
	
	@Option(name="-type", usage="Specify which model to use (lda or uri)")
	public String type = "uri";
	
	@Option(name="-topictest", usage="Specify whether to use topic test")
	public boolean topicTest = false;
	
	@Option(name="-locationtest", usage="Specify whether to use topic test")
	public boolean locationTest = false;
	
	@Option(name="-directtest", usage="Specify whether to use topic test")
	public boolean directTest = false;
	
	@Option(name="-topicdivergencetest", usage="Specify whether to use topic divergence test")
	public boolean topicDivergenceTest = false;
	
	@Option(name="-testall", usage="Specify whether to use all tests")
	public boolean testAll = false;
	
	@Option(name="-loadmodel", usage="Specify whether to load existing model")
	public boolean loadModel = false;
	
	@Option(name="-testfile", usage="Specify test data file")
	public String testFile = "";
	
	@Option(name="-ressubdir", usage="Specify sub directory for storing results")
	public String resSubDir = "";
	
	@Option(name="-traindoccount", usage="Specify training corpus size")
	public int trainDocCount = 0;
	
	@Option(name="-testdoccount", usage="Specify evaluation corpus size")
	public int testDocCount = 0;
}

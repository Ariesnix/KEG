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

import org.kohsuke.args4j.*;

public class LDA {
	public static void main(String args[]){
		LDACmdOption option = new LDACmdOption();
		CmdLineParser parser = new CmdLineParser(option);
		
		try {
			if (args.length == 0){
				showHelp(parser);
				return;
			}
			
			parser.parseArgument(args);
			if (option.testAll) {
				option.directTest = true;
				option.topicTest = true;
				option.locationTest = true;
				option.topicDivergenceTest = true;
			}
			Model m = null;
			if (option.est || option.estc){
				Estimator estimator = new Estimator();
				estimator.init(option);
				estimator.estimate();
				m = estimator.trnModel;
				
			} else if (option.loadModel) {
				m = new Model();
				option.modelName = option.modelName + "-final";
				m.initEstimatedModel(option);
				System.out.println("Computing parameters..." + LDA.getTime());
				Estimator.computeAll(m);
//				String d = m.dir;
//				if (!m.resSubDir.isEmpty()) {
//					d = d + File.separator + m.resSubDir;
//				}
				//m.saveModelTwords(d + File.separator + m.modelName + Model.twordsSuffix);
			}
			else if (option.inf){
				Inferencer inferencer = new Inferencer();
				inferencer.init(option);
				
				Model newModel = inferencer.inference();
			
				for (int i = 0; i < newModel.phi.length; ++i){
					//phi: K * V
					System.out.println("-----------------------\ntopic" + i  + " : ");
					for (int j = 0; j < 10; ++j){
						System.out.println(j + "\t" + newModel.phi[i][j]);
					}
				}
			}
			if (option.directTest || option.topicDivergenceTest
					|| option.topicTest || option.locationTest) {
				Tester tester = new Tester();
				tester.init(option, m);
				tester.test();
			}
		}
		catch (CmdLineException cle){
			System.out.println("Command line error: " + cle.getMessage());
			showHelp(parser);
			return;
		}
		catch (Exception e){
			System.out.println("Error in main: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
	public static void showHelp(CmdLineParser parser){
		System.out.println("LDA [options ...] [arguments...]");
		parser.printUsage(System.out);
	}
	
	private static long time = -1;
	public static long getTime() {
		if (time == -1) {
			time = System.currentTimeMillis() / 1000;
		} 
		return System.currentTimeMillis() / 1000 - time;
	}
	
}

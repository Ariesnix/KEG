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

package edu.thu.keg.mdap.dm.uri;


public class Estimator {
	
	// output model
	protected Model trnModel;
	LDACmdOption option;
	
	public boolean init(LDACmdOption option){
		this.option = option;
		trnModel = new Model();
		
		if (option.est){
			if (!trnModel.initNewModel(option))
				return false;
			}
		else if (option.estc){
			if (!trnModel.initEstimatedModel(option))
				return false;
		}
		
		return true;
	}
	
	public void estimate(){
		System.out.println("Sampling " + trnModel.niters + " iteration!");
		
		int lastIter = trnModel.liter;
		for (trnModel.liter = lastIter + 1; trnModel.liter < trnModel.niters + lastIter; trnModel.liter++){
			System.out.println("Iteration " + trnModel.liter + " ..." + LDA.getTime());
			
			// for all z_i
			for (int m = 0; m < trnModel.M; m++){				
				for (int n = 0; n < trnModel.data.docs[m].length; n++){
					// z_i = z[m][n]
					// sample from p(z_i|z_-i, w)
					int topic = sampling(m, n);
					trnModel.z[m].set(n, topic);
				}// end for each word
			}// end for each document
			
			if (option.savestep > 0){
				if (trnModel.liter % option.savestep == 0){
					System.out.println("Saving the model at iteration " + trnModel.liter + " ...");
					computeAll();
					trnModel.saveModel("model-" + Conversion.ZeroPad(trnModel.liter, 5));
				}
			}
		}// end iterations		
		
		System.out.println("Gibbs sampling completed!\n");
		System.out.println("Saving the final model!\n");
		computeAll();
		trnModel.liter--;
		trnModel.saveModel("model-final");
	}
	
	/**
	 * Do sampling
	 * @param m document number
	 * @param n word number
	 * @return topic id
	 */
	public int sampling(int m, int n){
		// remove z_i from the count variable
		int topic = trnModel.z[m].get(n);
		int w = trnModel.data.docs[m].words[n][0];
		int l = trnModel.data.docs[m].words[n][1];
		
		trnModel.addTopic(w, l, m, topic, -1);
		
		topic = drawNewTopic(m , w, l);
				
		
		// add newly estimated z_i to count variables
		trnModel.addTopic(w, l, m, topic, 1);
		
 		return topic;
	}
	private int drawNewTopic(int m, int w, int l) {
		int topic;
		if (option.type.equals("lda")) {
			computeP_LDA(m, w);
		} else if (option.type.equals("uri")) {
			computeP_URI(m, w, l);
		}

		topic = MathUtil.sampleFromMultiNominal(trnModel.p);
		return topic;
	}
	private void computeP_URI(int m, int w, int l) {
		// TODO Auto-generated method stub
		double Vbeta = trnModel.V * trnModel.beta;
		double Kalpha = trnModel.K * trnModel.alpha;
		//do multinominal sampling via cumulative method
		for (int k = 0; k < trnModel.K; k++){
			trnModel.p[k] = (trnModel.nwl[w][l][k] + trnModel.beta)/(trnModel.nwlsum[k][l] + Vbeta) *
					(trnModel.nd[m][k] + trnModel.alpha)/(trnModel.ndsum[m] + Kalpha);
		}
		
	}

	private void computeP_LDA(int m, int w) {
		double Vbeta = trnModel.V * trnModel.beta;
		double Kalpha = trnModel.K * trnModel.alpha;
		//do multinominal sampling via cumulative method
		for (int k = 0; k < trnModel.K; k++){
			trnModel.p[k] = (trnModel.nw[w][k] + trnModel.beta)/(trnModel.nwsum[k] + Vbeta) *
					(trnModel.nd[m][k] + trnModel.alpha)/(trnModel.ndsum[m] + Kalpha);
		}
				
	}
	
	public static void computeTheta(Model trnModel){
		for (int m = 0; m < trnModel.M; m++){
			for (int k = 0; k < trnModel.K; k++){
				trnModel.theta[m][k] = (trnModel.nd[m][k] + trnModel.alpha) / (trnModel.ndsum[m] + trnModel.K * trnModel.alpha);
			}
		}
	}
	
	public static void computeTopicDist(Model trnModel){
		double [] temp = new double[trnModel.K];
		double total = 0;
		for (int m = 0; m < trnModel.M; m++){
			for (int k = 0; k < trnModel.K; k++){
				temp[k] += trnModel.nd[m][k];
			}
			total += trnModel.ndsum[m];
		}
		for (int k = 0; k < trnModel.K; k++){
			trnModel.topic_dist[k] = (temp[k] + trnModel.alpha) / (total + trnModel.K * trnModel.alpha);
		}
	}
		
	public static void computePhi(Model trnModel){
		for (int k = 0; k < trnModel.K; k++){
			for (int w = 0; w < trnModel.V; w++){
				trnModel.phi[k][w] = (trnModel.nw[w][k] + trnModel.beta) / (trnModel.nwsum[k] + trnModel.V * trnModel.beta);
			}
		}
	}
	
	public static void computePhi_L(Model trnModel){
		double Vbeta = trnModel.V * trnModel.beta;
		for (int k = 0; k < trnModel.K; k++){
			for (int l = 0; l < trnModel.L; l++) {
				for (int w = 0; w < trnModel.V; w++){
					trnModel.phi_l[k][l][w] = (trnModel.nwl[w][l][k] + trnModel.beta) / (trnModel.nwlsum[k][l] + Vbeta);
				}
			}
		}
	}
	
	public static void computeAll(Model model) {
		computeTheta(model);
		computeTopicDist(model);
		computePhi(model);
		computePhi_L(model);
	}
	
	private void computeAll() {
		Estimator.computeAll(trnModel);
	}
}

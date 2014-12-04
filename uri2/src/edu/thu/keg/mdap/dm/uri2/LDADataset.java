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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class LDADataset {
	//---------------------------------------------------------------
	// Instance Variables
	//---------------------------------------------------------------

	
	public Document [] docs; 		// a list of documents	
	public int M; 			 		// number of documents
	public int V;			 		// number of words
	public int L;
	
	private HashMap<String, Integer> imsi2docid; //store IMSI - array index of docs mapping
	
	
	//--------------------------------------------------------------
	// Constructor
	//--------------------------------------------------------------
	public LDADataset(int v, int l){
		M = 0;
		V = v;
		L = l;
		docs = null;
	}
	
	public LDADataset(int M, int v, int l){
		this.M = M;
		this.V = v;
		this.L = l;
		imsi2docid = new HashMap<String, Integer>(M);
		docs = new Document[M];	
	}
	
	//-------------------------------------------------------------
	//Public Instance Methods
	//-------------------------------------------------------------
	/**
	 * set the document at the index idx if idx is greater than 0 and less than M
	 * @param doc document to be set
	 * @param idx index in the document array
	 */	
	public void setDoc(Document doc, int idx){
		if (0 <= idx && idx < M){
			docs[idx] = doc;
			imsi2docid.put(doc.imsi, idx);
		}
	}
	/**
	 * set the document at the index idx if idx is greater than 0 and less than M
	 * @param str string contains doc
	 * @param idx index in the document array
	 */
	public void setDoc(String str, int idx){
		if (idx % 10000 == 0)
			System.out.println("Processing file " + idx + "..." + LDA.getTime());
		if (0 <= idx && idx < M){
			StringTokenizer tknr = new StringTokenizer(str, " \t\r\n");
			String uri = tknr.nextToken();
			Vector<int[]> ids = new Vector<int[]>();
			
			while (tknr.hasMoreTokens()){
				int word = Integer.parseInt(tknr.nextToken());
				int regionId = Integer.parseInt(tknr.nextToken());
				int n = Integer.parseInt(tknr.nextToken());
				for(int j = 0; j < n; j++) {
					int[] arr = {word, regionId};
					ids.add(arr);
				}
			}
			Document doc = new Document(ids, uri);
			setDoc(doc, idx);	
		}
	}
	//---------------------------------------------------------------
	// I/O methods
	//---------------------------------------------------------------
	
	/**
	 *  read a dataset from a stream, create new dictionary
	 *  @return dataset if success and null otherwise
	 */
	public static LDADataset readDataSet(String filename, int m, int v, int l){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "UTF-8"));
			
			LDADataset data = readDataSet(reader, m, v, l);
			
			reader.close();
			return data;
		}
		catch (Exception e){
			System.out.println("Read Dataset Error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	

	
	/**
	 *  read a dataset from a stream, create new dictionary
	 *  @return dataset if success and null otherwise
	 */
	public static LDADataset readDataSet(BufferedReader reader, int m, int v, int l){
		try {
			//read number of document
			String line;
			
			LDADataset data = new LDADataset(m, v, l);
			for (int i = 0; i < m; ++i){
				line = reader.readLine();
				
				data.setDoc(line, i);
			}
			
			return data;
		}
		catch (Exception e){
			System.out.println("Read Dataset Error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	

	
	/**
	 * read a dataset from a string, create new dictionary
	 * @param str String from which we get the dataset, documents are seperated by newline character 
	 * @return dataset if success and null otherwise
	 */
	public static LDADataset readDataSet(String [] strs, int v, int l){
		LDADataset data = new LDADataset(strs.length, v, l);
		
		for (int i = 0 ; i < strs.length; ++i){
			data.setDoc(strs[i], i);
		}
		return data;
	}
	public int getDocIdx(String imsi) {
		Integer index;
		index = imsi2docid.get(imsi);
		if (index == null) {
			return -1;
		} else {
			Document d = docs[index];
			if (d.imsi.equals(imsi))
				return index;
			else return -1;
		}
	}
	public Document getDoc(String imsi) {
		int idx = getDocIdx(imsi);
		if (idx == -1)
			return null;
		else return docs[idx];
	}
	
}

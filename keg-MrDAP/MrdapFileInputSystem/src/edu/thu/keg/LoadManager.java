package edu.thu.keg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import edu.thu.keg.mr.IHBaseManager;

public class LoadManager {
	static LoadManager instance;
	HashMap<String, IHBaseManager> tName2hBase = new HashMap<String, IHBaseManager>();

	public synchronized static LoadManager getInstance() {
		// TODO multi-thread
		if (instance == null)
			instance = new LoadManager();
		return instance;
	}

	public void addTableImpoting(String key, IHBaseManager ihm) {
		if (!tName2hBase.containsKey(key))
			tName2hBase.put(key, ihm);
	}

	public IHBaseManager getIHB(String key) {
		return tName2hBase.get(key);
	}

	public Set<String> getMigrationTableList() {
		return tName2hBase.keySet();
	}
	
	public boolean removeMigrationTable(String id){
		if(tName2hBase.containsKey(id))
		{
			tName2hBase.remove(id);
		return true;}
		else return false;
		
	}
}

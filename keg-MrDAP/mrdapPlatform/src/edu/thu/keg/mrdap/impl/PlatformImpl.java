package edu.thu.keg.mrdap.impl;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.Platform;
import edu.thu.keg.mrdap.TaskManager;
import edu.thu.keg.mrdap.dataset.Dataset;

public class PlatformImpl implements Platform {
	public PlatformImpl(String file) {
		try {
			Config.init(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public DatasetManager getDatasetManager() {
		// TODO Auto-generated method stub
		return DatasetManagerImpl.getInstance();
	}

	@Override
	public TaskManager getTaskManager() {
		// TODO Auto-generated method stub
		return TaskManagerImpl.getInstance();
	}

	public static void main(String arg[]) {
		PlatformImpl p = new PlatformImpl("config.xml");
		System.out.println("Loading...");
		p.crud();
	}

	public void crud() {

		// MFile mf= new MFile("/mobile/mrf");
		// try {
		// mf.mkdir();
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// // ha
		// getDatasetManager();
		// getDatasetManager().createDataset("1", "1st", new Date(), "mro",
		// "one",
		// "ybz", "a1/sd", 10);
		// getDatasetManager().createDataset("2", "2ed", new Date(), "mro",
		// "two",
		// "ybz", "a2/sd", 20);
		// getDatasetManager().createDataset("3", "1st", new Date(), "mro",
		// "three", "ybz", "a4/sd", 30);

		// getTaskManager().createTask(
		// "1",
		// new Date(),
		// "name2",
		// "ybz",
		// Arrays.asList(getDatasetManager().getDatasetList().toArray(
		// new Dataset[0])));

		try {
			getDatasetManager();
			getTaskManager().saveXMLChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

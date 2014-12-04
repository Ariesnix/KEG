package edu.thu.keg.mrdap.impl;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.net.nntp.NewsgroupInfo;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.client.hdfs.MFile;
import edu.thu.keg.link.client2.TaskTableMapOp;
import edu.thu.keg.link.client2.TaskTypeOp;
import edu.thu.keg.link.common.taskTable.TaskTable;
import edu.thu.keg.link.common.taskType.TaskType;
import edu.thu.keg.link.common.taskType.TaskTypeField;
import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.dataset.impl.DatasetImpl;
import edu.thu.keg.mrdap.task.Task;

public class DatasetManagerImpl implements DatasetManager {

	public static class Storage {

		Dataset[] datasets;

		/**
		 * @param datasets
		 * @param views
		 */
		private Storage(Dataset[] datasets) {
			super();
			this.datasets = datasets;

		}

	}

	private HashMap<String, Dataset> datasets = null;
	private HashMap<String, List<Dataset>> taskType2dataset = null;

	private XStream xstream;
	private static DatasetManagerImpl instance;

	private DatasetManagerImpl() {
		datasets = new HashMap<String, Dataset>();
		taskType2dataset = new HashMap<String, List<Dataset>>();
		try {
			System.out
					.println("(DatasetManagerImpl) Loading Dataset File Name: "
							+ Config.getDataSetFile());
			// loadXMLDatasets(Config.getDataSetFile());
			// loadHadoopDatasets(false);
			loadHBaseDatasets();
		} catch (Exception ex) {
			// log.warn(ex.getMessage());
			ex.printStackTrace();
		}
	}

	private XStream getXstream() {
		if (xstream == null) {
			xstream = new XStream(new StaxDriver());
			// xstream.alias("Jdbc", JdbcProvider.class);
			xstream.registerConverter(new DateConverter("yyyy-MM-dd", null));
			// xstream.registerConverter(new AbstractSingleValueConverter() {

			// @Override
			// public boolean canConvert(
			// @SuppressWarnings("rawtypes") Class arg0) {
			// // return arg0.equals(JdbcProvider.class);
			// }

			// @Override
			// public Object fromString(String str) {
			// return new JdbcProvider(str);
			// }

			// });
		}
		return xstream;
	}

	private void addDataset(Dataset ds) {
		datasets.put(ds.getId(), ds);
	}

	private void loadXMLDatasets(String fileName) {
		String f = fileName;
		File Fi = new File(f);
		System.out.println(Fi.getAbsolutePath());
		Storage sto = (Storage) getXstream().fromXML(Fi);
		for (Dataset ds : sto.datasets) {
			addDataset(ds);
		}

	}

	private void loadHadoopDatasets(boolean isRemainOld) {

		List<MFile> mfs = new ArrayList<MFile>();
		MFile mfile = new MFile("/mobile");
		getAllMfiles(mfile, mfs);
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat smf = new SimpleDateFormat("yyyyMMdd");
		if (!isRemainOld || datasets == null)
			datasets = new HashMap<String, Dataset>();
		for (MFile mf : mfs) {
			MFile type = new MFile(mf.getParent());
			MFile serial = new MFile(type.getParent());
			try {
				createDataset(mf.getPath(), "", serial.getName(), new Date(smf
						.parse(mf.getName()).getTime()), type.getName(),
						mf.getName(), "admin", 0, mf.getPath(), mf.totalSize(),
						mf.isDirectory());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void loadHBaseDatasets() {
		taskType2dataset = new HashMap<String, List<Dataset>>();
		try {
			JSONArray tps = TaskTypeOp.nameList();
			for (int i = 0; i < tps.length(); i++) {
				JSONObject tp = tps.getJSONObject(i);
				String taskType = tp.getString("id");
				System.out.println(taskType + ":");
				taskType2dataset.put(taskType, new ArrayList<Dataset>());
				JSONArray tbs = TaskTableMapOp.query2(taskType);
				for (int j = 0; j < tbs.length(); j++) {
					JSONObject tb = tbs.getJSONObject(j);
					// System.out.print(j + " " + tb.getString("id"));
					String tableId = tb.getString("id");
					int rowCount = tb.getInt("lineCount");
					System.out.println("N表名称" + tableId);
					Dataset ds = createDataset(tableId, "", "#1", new Date(),
							taskType, tableId, "admin", rowCount, "HBase",
							8421, false);
					// if (ds != null)
					taskType2dataset.get(taskType).add(ds);
				}
			}
			// for (JSONObject tp : tps.) {
			// String id = tp.getValue(TaskTypeField.ID);
			// taskType2dataset.put(id, new ArrayList<Dataset>());
			// // System.out.println(tp.getArgs().toString());
			// TaskTable tb = TaskTableQuery.getTable(id);
			// System.out.println(tb.getTables());
			// JSONObject tbj = tb.getTables();
			// Iterator<?> tableNames = tbj.keys();
			// String tableN = "";
			// while (tableNames.hasNext()) {
			// tableN = (String) tableNames.next();
			// // tableN = tableJ.toString();
			// System.out.println("N表名称" + tableN);
			// Dataset ds = createDataset(tableN, tbj.get(tableN)
			// .toString(), "#1", new Date(), id, tableN, "admin",
			// "HBase", 8421, false);
			// taskType2dataset.get(id).add(ds);
			// }
			// }
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void getAllMfiles(MFile mf, List<MFile> mfs) {

		try {
			if (!mf.isDirectory()) {
				System.out.println(mf.getPath());
				mfs.add(mf);
				return;
			}
			for (MFile submf : mf.list()) {
				if (!submf.isDirectory() && !submf.getName().endsWith(".ha"))
					continue;
				getAllMfiles(submf, mfs);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.DatasetManager#getAllFilesPath()
	 */
	@Override
	public List<String> getAllFilesPath(String rootPath) {
		List<String> re = null;
		try {
			MFile mRoot = new MFile(rootPath);
			MFile[] mfs = mRoot.list();
			re = new ArrayList<String>();

			for (MFile mf : mfs) {
				if (mf.isDirectory())
					re.addAll(getAllFilesPath(mf.getPath()));
				else {
					if (!mf.getName().endsWith(".ha"))
						continue;
					re.add(mf.getPath());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return re;
	}

	@Override
	public void saveXMLChanges() throws IOException {
		Writer fw;
		Storage sto = new Storage(datasets.values().toArray(new Dataset[0]));
		fw = new OutputStreamWriter(new FileOutputStream(
				Config.getDataSetFile()), "UTF-8");
		System.out.println(Config.getDataSetFile());
		getXstream().marshal(sto, new PrettyPrintWriter(fw));
		fw.close();
	}

	public synchronized static DatasetManager getInstance() {
		// TODO multi-thread
		if (instance == null)
			instance = new DatasetManagerImpl();
		return instance;
	}

	@Override
	public Collection<Dataset> getDatasetList() {
		// TODO Auto-generated method stub
		return datasets.values();
	}

	@Override
	public Dataset getDataset(String id) {
		// TODO Auto-generated method stub
		return datasets.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.DatasetManager#getHDatasets(java.lang.String)
	 */
	@Override
	public List<Dataset> getHDatasets(String typeId) {
		// TODO Auto-generated method stub
		return taskType2dataset.get(typeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.DatasetManager#containsDataset(java.lang.String)
	 */
	@Override
	public boolean containsDataset(String id) {
		// TODO Auto-generated method stub
		return datasets.containsKey(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.DatasetManager#createDataset(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public Dataset createDataset(String id, String args, String serial,
			Date date, String type, String name, String owner, int rowCount,
			String path, long sizeMb, boolean isDic) {
		if (datasets.containsKey(id)) {
			System.out.println(id + " dataset is already egxisted!");
			return datasets.get(id);
		}
		Dataset ds = new DatasetImpl(id, args, serial, date, type, name, owner,
				rowCount, path, sizeMb, isDic);
		addDataset(ds);
		return ds;
	}

	@Override
	public void refreshDatasetInHadoop() {
		loadHadoopDatasets(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.DatasetManager#refreshDatasetInHBase()
	 */
	@Override
	public void refreshDatasetInHBase() {
		loadHBaseDatasets();

	}

	@Override
	public void addDataset2Hbase(String tableId, String type) {
		// Dataset ds = createDataset(tableId, "", "#1", new Date(), type,
		// tableId, "admin", "HBase", 8421, false);
		// if (ds == null)
		// return;
		// if (!taskType2dataset.containsKey(type))
		// taskType2dataset.put(type, new ArrayList<Dataset>());
		// taskType2dataset.get(type).add(ds);

	}

	@Override
	public void removeDatasetInHbase(String tableId) {

		List<Dataset> ds = taskType2dataset.get((datasets.get(tableId))
				.getType());

		for (int i = 0; i < ds.size(); i++) {
			if (ds.get(i).getId().equals(tableId)) {
				ds.remove(i);
				return;
			}
		}
	}

	// public static void main(String arg[]) {
	// Default.initDefault("default.xml");
	// DatasetManagerImpl.loadHBaseDatasets();
	// }
}

package edu.thu.keg.mrdap.impl;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.thu.keg.link.client.hdfs.MFile;
import edu.thu.keg.link.client.taskClient.TaskClient;
import edu.thu.keg.link.client.taskQuery.FinalStatus;
import edu.thu.keg.link.client.taskQuery.State;
import edu.thu.keg.link.client.taskQuery.TaskQuery;
import edu.thu.keg.link.client.taskQuery.TaskQuery.TaskInfo;
import edu.thu.keg.link.client2.TaskTypeOp;
import edu.thu.keg.link.common.taskTable.TaskTable;
import edu.thu.keg.link.common.taskType.TaskType;
import edu.thu.keg.link.common.taskType.TaskTypeField;
import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.TaskManager;
import edu.thu.keg.mrdap.Util;
import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.dataset.impl.DatasetImpl;
import edu.thu.keg.mrdap.impl.DatasetManagerImpl.Storage;
import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskImpl;
import edu.thu.keg.mrdap.task.impl.TaskStatus;

public class TaskManagerImpl implements TaskManager {

	public static class Storage {
		Task[] tasks;

		/**
		 * @param datasets
		 * @param views
		 */
		private Storage(Task[] tasks) {
			super();
			this.tasks = tasks;
		}

	}

	private static TaskManagerImpl instance;
	private HashMap<String, Task> tasks = null;
	private XStream xstream;

	private TaskManagerImpl() {
		tasks = new HashMap<String, Task>();
		try {
			System.out.println("(TaskManagerImpl) Loading Task File Name: "
					+ Config.getTaskFile());
			loadTasks(Config.getTaskFile());
		} catch (Exception ex) {
			// log.warn(ex.getMessage());

		}
	}

	private XStream getXstream() {
		if (xstream == null) {
			xstream = new XStream(new StaxDriver());
		}
		return xstream;
	}

	private void addTask(Task ts) {
		tasks.put(ts.getId(), ts);
	}

	private void loadTasks(String fileName) {
		String f = fileName;
		File Fi = new File(f);
		Storage sto = (Storage) getXstream().fromXML(Fi);
		for (Task ts : sto.tasks) {
			addTask(ts);
		}
	}

	@Override
	public void saveXMLChanges() throws IOException {
		Writer fw;
		Storage sto = new Storage(tasks.values().toArray(new Task[0]));
		fw = new OutputStreamWriter(new FileOutputStream(Config.getTaskFile()),
				"UTF-8");
		getXstream().marshal(sto, new PrettyPrintWriter(fw));
		fw.close();
	}

	public synchronized static TaskManager getInstance() {
		// TODO multi-thread
		if (instance == null)
			instance = new TaskManagerImpl();
		return instance;
	}

	@Override
	public Collection<Task> getTaskList() {
		// TODO Auto-generated method stub
		return tasks.values();
	}

	@Override
	public Task getTask(String id) {
		// TODO Auto-generated method stub
		return tasks.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#getTaskTypeIds()
	 */
	@Override
	public Collection<String> getTaskTypeIds() {
		List<String> tpsList = new ArrayList<String>();
		try {
			JSONArray tps = TaskTypeOp.nameList();

			// TaskType tps[] = TaskTypeQuery.getTaskTypeList();
			for (int i = 0; i < tps.length(); i++) {
				tpsList.add(tps.getJSONObject(i).getString("id"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tpsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#createTask(java.lang.String,
	 * java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public Task setTask(String typeId, String typeName, String name,
			String owner, List<String> filePaths) {
		// if (tasks.containsKey(id)) {
		// System.out.println(id + " task is already egxisted!");
		// return null;
		// }
		List<String> datasets = new ArrayList<String>();
		try {
			for (String path : filePaths) {
				MFile mf = new MFile(path);

				if (mf.isDirectory()) {
					datasets.addAll(DatasetManagerImpl.getInstance()
							.getAllFilesPath(mf.getPath()));
				} else
					datasets.add(mf.getPath());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date now = new Date();
		Task ts = new TaskImpl(now, name, owner, TaskStatus.READY, typeId,
				typeName, datasets, Config.getHadoopRoot() + "mobileRES/"
						+ typeId + now.getTime(), "");
		return ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#setHBaseTask(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public Task setHBaseTask(String typeId, String typeName, String name,
			String owner, List<String> tableNames, String params) {
		Date now = new Date();
		Task ts = new TaskImpl(now, name, owner, TaskStatus.READY, typeId,
				typeName, tableNames, Config.getHadoopRoot() + "mobileRES/"
						+ typeId + now.getTime(), params);

		return ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#runTask(edu.thu.keg.mrdap.task.Task)
	 */
	@Override
	public String runTask(Task task) {
		// try {
		List<String> paths = task.getDatasets();
		String pathAll = paths.get(0);
		for (int i = 1; i < paths.size(); i++) {
			pathAll = pathAll + "," + paths.get(i);
		}
		System.out.println(pathAll);
		String appId = "WCLOVELQ" + Util.getNowTime();

		// appId = TaskClient.submit(pack, 5, 4, 2, 2, jar, pathAll,
		// // task.getOutputPath(), "");
		// appId = TaskClient.submit(task.getTypeId(), pathAll,
		// task.getOutputPath(), "");
		String paramStr = "{\"tables\":{\"MRO1\":\"cf:st,cf:et,cf:ltescrsrp,cf:ltencrsrp\"},"
				+ "\"keySpan\":\"(418048-1,418048-1)\","
				+ "\"timeSpan\":\"(20140806,20140807)\","
				+ "\"num\":\"3\","
				+ "\"diff\":\"-6\","
				+ "\"min\":\"31\","
				+ "\"output\":\"shsx111\"}";
		try {
			JSONArray tables = new JSONArray();
			// JSONObject tablesTypeId =
			// TaskTableQuery.getTable(task.getTypeId())
			// .getTables();
			for (String table : task.getDatasets()) {
				tables.put(table);
			}
			System.out.println("前台传过来参数 " + task.getParams());
			JSONObject param = new JSONObject(task.getParams());
			param.put("tableIds", tables);
			// param.put("RUNTIME_INFO", "");
			param.put("output", task.getOutputPath());
			paramStr = param.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject param = new JSONObject();
		// param.put("tables", value)
		System.out.println("typeId: " + task.getTypeId());
		System.out.println("params: " + paramStr);
		appId = TaskClient.sumbit(task.getTypeId(), paramStr);
		System.out.println("任务Id：" + appId);
		if (appId.startsWith("err"))
			return appId;
		System.out.println("输出路径：" + task.getOutputPath());

		// 运行该任务，更新task id
		task.run(appId);
		// 从hadoop得到任务运行状态，并更新task的状态
		getTaskInfo(appId);
		// 将hadoop的任务添加到tasks前台虚拟任务集中
		addTask(task);
		try {
			saveXMLChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return TaskStatus.RUNNING.name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#killTask(java.lang.String)
	 */
	@Override
	public void killTask(String id) {
		tasks.get(id).kill();
		TaskClient.kill(id);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#removeTask(java.lang.String)
	 */
	@Override
	public void removeTask(String id) {
		// TODO Auto-generated method stub
		if (tasks.containsKey(id)) {
			tasks.remove(id);
			try {
				saveXMLChanges();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#getTaskInfo(java.lang.String)
	 */
	@Override
	public TaskStatus getTaskInfo(String id) {
		// TODO Auto-generated method stub
		if (!tasks.containsKey(id))
			return null;
		Task task = tasks.get(id);

		try {
			TaskInfo ti = TaskQuery.getTaskInfo(id);
			System.out.println("获取任务状态id： " + id);
			System.out.println("获取任务状态state： " + ti.getState());
			System.out.println("获取任务状态finalstatus： " + ti.getFinalStatus());
			if (ti.getState().equals(State.RUNNING.name()))
				task.run(id);
			else if (ti.getState().equals(State.KILLED.name()))
				task.kill();
			else if (ti.getState().equals(State.FINISHED.name())) {
				if (ti.getFinalStatus().equals(FinalStatus.SUCCEEDED.name())) {
					task.success();
				} else if (ti.getFinalStatus()
						.equals(FinalStatus.FAILED.name())) {
					task.fail();
				}
			} else if (ti.getState().equals(State.FAILED.name())) {
				if (ti.getFinalStatus().equals(FinalStatus.FAILED.name())) {
					task.fail();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return task.getStute();
	}
}

package edu.thu.keg.mrdap.task.impl;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.task.Task;

public class TaskImpl implements Task {
	private String id = "undentifier";
	private Date date = null;
	private String name = null;
	private String owner = null;
	private List<String> datasets = null;
	private String outputPath = null;
	TaskStatus status = null;
	String typeName = "";
	String typeId = "";
	private String params = "";

	public TaskImpl(Date date, String name, String owner, TaskStatus status,
			String typeId, String typeName, List<String> datasets,
			String outputPath, String params) {

		this.date = date;
		this.name = name;
		this.owner = owner;
		this.status = status;
		this.typeId = typeId;
		this.typeName = typeName;
		this.datasets = datasets;
		this.outputPath = outputPath;
		this.params = params;
	}

	// public TaskImpl(String id, Date date, String name, String owner,
	// TaskStatus status, TaskType type, List<String> direcorys,
	// List<String> files) {
	// this.id = id;
	// this.date = date;
	// this.name = name;
	// this.owner = owner;
	// this.status = status;
	// this.type = type;
	// this.directorys = direcorys;
	// this.files = files;
	//
	// }

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getDatasets()
	 */
	@Override
	public List<String> getDatasets() {
		// TODO Auto-generated method stub
		return this.datasets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getDate()
	 */
	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return this.date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getStute()
	 */
	@Override
	public TaskStatus getStute() {
		// TODO Auto-generated method stub
		return this.status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getTypeName()
	 */
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return this.typeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getTypeId()
	 */
	@Override
	public String getTypeId() {
		// TODO Auto-generated method stub
		return this.typeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getOutputPath()
	 */
	@Override
	public String getOutputPath() {
		// TODO Auto-generated method stub
		return this.outputPath;
	}

	/**
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#run(java.lang.String)
	 */
	@Override
	public void run(String id) {
		this.id = id;
		this.status = TaskStatus.RUNNING;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#kill()
	 */
	@Override
	public void kill() {
		this.status = TaskStatus.KILLED;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#fail()
	 */
	@Override
	public void fail() {
		this.status = TaskStatus.FAILED;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#success()
	 */
	@Override
	public void success() {
		this.status = TaskStatus.SUCCEEDED;

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	/**
	 * @return the params
	 */
	public String getParams() {
		return params;
	}

}

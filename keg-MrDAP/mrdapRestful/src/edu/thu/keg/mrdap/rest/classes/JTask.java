package edu.thu.keg.mrdap.rest.classes;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskStatus;

@XmlRootElement
public class JTask {

	private String id;
	private Date date;
	private String name;
	private List<String> jdatasets = new ArrayList<String>();;
	private TaskStatus taskstatus;
	private String taskTypeId;
	private String taskTypeName;

	private String outputPath;

	public JTask() {

	}

	public JTask(Task task) {
		this.id = task.getId();
		this.date = task.getDate();
		this.name = task.getName();
		// this.setJdatasets(task.getDatasets());
		for (String dataset : task.getDatasets())
			jdatasets.add(dataset);
		this.taskstatus = task.getStute();
		this.taskTypeId = task.getTypeId();
		this.taskTypeName = task.getTypeName();
		this.outputPath = task.getOutputPath();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the datasets
	 */
	public List<String> getJdatasets() {
		return jdatasets;
	}

	/**
	 * @param datasets
	 *            the datasets to set
	 */
	// public void setJdatasets(List<Dataset> datasets) {
	// for (Dataset dataset : datasets)
	// jdatasets.add(new JDataset(dataset));
	// }

	/**
	 * @param jdatasets
	 *            the jdatasets to set
	 */
	public void setJdatasets(List<String> jdatasets) {
		this.jdatasets = jdatasets;
	}

	/**
	 * @return the taskstatus
	 */
	public TaskStatus getTaskstatus() {
		return taskstatus;
	}

	/**
	 * @param taskstatus
	 *            the taskstatus to set
	 */
	public void setTaskstatus(TaskStatus taskstatus) {
		this.taskstatus = taskstatus;
	}

	/**
	 * @return the tasktype
	 */
	public String getTaskTypeName() {
		return this.taskTypeName;
	}

	/**
	 * @param tasktype
	 *            the tasktype to set
	 */
	public void setTaskTypeName(String taskTypeName) {
		this.taskTypeName = taskTypeName;
	}

	/**
	 * @return the taskTypeId
	 */
	public String getTaskTypeId() {
		return taskTypeId;
	}

	/**
	 * @param taskTypeId
	 *            the taskTypeId to set
	 */
	public void setTaskTypeId(String taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	/**
	 * @return the outputPath
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @param outputPath
	 *            the outputPath to set
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}

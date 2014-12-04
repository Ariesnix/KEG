package edu.thu.keg.mrdap;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskStatus;

public interface TaskManager {

	public Collection<Task> getTaskList();

	public Collection<String> getTaskTypeIds();

	public Task getTask(String id);

	public void saveXMLChanges() throws IOException;

	public Task setTask(String typeId, String typeName, String name,
			String owner, List<String> filePaths);

	public Task setHBaseTask(String typeId, String typeName, String name,
			String owner, List<String> tableNames, String params);

	//
	// public void createTask(String id, Date date, TaskType type, String name,
	// String owner, List<String> directorys, List<String> filePaths);

	public String runTask(Task task);

	public void killTask(String id);

	public void removeTask(String id);

	public TaskStatus getTaskInfo(String id);
}

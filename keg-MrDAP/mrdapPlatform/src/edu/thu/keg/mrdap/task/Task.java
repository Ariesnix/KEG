package edu.thu.keg.mrdap.task;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.task.impl.TaskStatus;

public interface Task {
	public String getId();

	public List<String> getDatasets();

	public Date getDate();

	public TaskStatus getStute();

	public String getTypeName();

	public String getTypeId();

	public String getName();

	public String getOutputPath();

	public String getParams();

	public void run(String id);

	public void kill();

	public void fail();

	public void success();
}

package edu.thu.keg.link.common.taskTable;

public enum TaskTableField
{
	
	 TASKID("taskId");
	
	 
	 private String info=null;
	 private TaskTableField(String info)
	 {
		 this.info=info;
	 }
	 
	 @Override
	 public String toString()
	 {
		 return info;
	 }
	 
}

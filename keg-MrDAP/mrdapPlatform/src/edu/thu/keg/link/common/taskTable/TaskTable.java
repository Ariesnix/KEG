package edu.thu.keg.link.common.taskTable;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TaskTable
{

	private JSONObject json=null;
	
	public TaskTable(String info) throws JSONException
	{
		json=new JSONObject(info);
	}
	
	public String getInfo(TaskTableField ttf) throws JSONException
	{
		return json.getString(ttf.toString());
	}
	
	//返回表的字段
	public JSONObject getTables() throws JSONException
	{
		JSONObject jarr= json.getJSONObject("tables");
		
		return jarr;
		
	}
	
	@Override
	public String toString()
	{
		return json.toString();
	}
	
	
	

}

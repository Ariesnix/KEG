package edu.thu.keg.link.common.taskType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TaskType
{
	private JSONObject info=null;
	
	public TaskType(String jsonStr) throws JSONException
	{
		info=new JSONObject(jsonStr);
		
	}
	
	public String getValue(TaskTypeField key) throws JSONException
	{
		return this.info.getString(key.toString());
	}
	
	public JSONObject getArgs() throws JSONException
	{
		return this.info.getJSONObject("args");
	}
	
	
	public String toString()
	{
		return info.toString();
	}

	
}

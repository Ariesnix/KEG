package edu.thu.keg.link.client2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class TaskTableMapOp
{

	public static void main(String[] args) throws JSONException
	{
		//query
		//System.out.println(query("task1"));
		
		JSONArray jsonArr=query2("task3_1");

		for(int i=0;i<jsonArr.length();i++)
		{
			System.out.println(jsonArr.getString(i));
			
		}
		//JSONObject newValues=jsonArr.getJSONObject(0);
		
		//newValues.put("tableId", "kkk");
		//create
		//System.out.println(create(newValues));
		//update
		
		//System.out.println(update("task1","kkk",newValues));

		//delete
		//System.out.println(delete("task1","kkk"));

		
		
	}
	
	//直接获取 任务与表的关系，而不是通过 表的映射
	public static JSONArray query2(String taskTypeId) throws JSONException
	{
		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[]{"taskTypeId",taskTypeId});
		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "TaskTableByTaskId22", list);
		return new JSONArray(info);
	}
	
	public static JSONArray query(String taskTypeId) throws JSONException
	{
		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[]{"taskTypeId",taskTypeId});
		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "TaskTableByTaskId2", list);
		return new JSONArray(info);
	}
	
	public static String create(JSONObject newValues) throws JSONException
	{
		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[]{"newValues",newValues.toString()});
		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "CreateTaskTableMap2", list);
		return info;
	}

	//update
	public static String update(String taskTypeId,String tableId,JSONObject newValues)
	{
		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[]{"newValues",newValues.toString()});
		list.add(new String[]{"taskTypeId",taskTypeId});
		list.add(new String[]{"tableId",tableId});

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "UpdateTaskTableInfo2", list);
		return info;
	}
	
	public static String delete(String taskTypeId,String tableId)
	{
		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[]{"taskTypeId",taskTypeId});
		list.add(new String[]{"tableId",tableId});

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "DeleteTaskTableMap2", list);
		return info;
	}
}

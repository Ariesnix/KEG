package edu.thu.keg.link.client2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class TaskTypeArgsOp
{

	public static void main(String[] args) throws JSONException
	{
		
		//query
		System.out.println(query("task1"));
		//delete
		//System.out.println(delete("task1","timeSpan"));
		//create
		
		//JSONObject json=new JSONObject();
		//json.put("taskTypeId","task1");
		//json.put("realName","timeSpan2");
		//json.put("defaultValue","xxx");
		//json.put("type","Optional");
		//json.put("name","Optional");

		//System.out.println(create(json));

		//update
		//json.put("name", "sdjfajdfaskj");
		//System.out.println(update("task1","timeSpan2",json));


		
	}
	
	
	//列出 所有hbase表
	public static JSONArray query(String taskTypeId) throws JSONException
	{
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[]{"taskTypeId",taskTypeId});
		
		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "TaskTypeArgsByTaskId2", list);
		return new JSONArray(info);
	}
	
		public static String delete(String taskTypeId,String realName) throws JSONException
		{
			List<String[]> list = new ArrayList<String[]>();
			list.add(new String[]{"taskTypeId",taskTypeId});
			list.add(new String[]{"realName",realName});


			String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

			String info= Request.post(app_submit_url + "DeleteTaskArgs", list);
			return info;
		}
		
		public static String create(JSONObject newValues)
		{
			List<String[]> list = new ArrayList<String[]>();
			list.add(new String[]{"newValues",newValues.toString()});

			String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

			String info= Request.post(app_submit_url + "CreateTaskArgs2", list);
			return info;
		}
		
		//update
		public static String update(String taskTypeId,String realName,JSONObject newValues)
		{
			List<String[]> list = new ArrayList<String[]>();
			
			list.add(new String[]{"taskTypeId",taskTypeId});
			list.add(new String[]{"realName",realName});

			list.add(new String[]{"newValues",newValues.toString()});

			String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

			String info= Request.post(app_submit_url + "UpdateTaskTypeArgsInfo2", list);
			return info;
		}


}

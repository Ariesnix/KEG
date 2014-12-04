package edu.thu.keg.link.client2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class TaskTypeOp
{

	public static void main(String[] args) throws JSONException
	{
		//System.out.println(nameList().toString());
		System.out.println(query("task1").toString());
		//System.out.println(delete("taskF").toString());
		
		
		/*JSONObject json=query("task1");
		json.put("id","taskXXXXX");
		json.put("className","UUU");

		
		
		System.out.println(create(json).toString());
		
		json.put("className","taskXXXXX");

		System.out.println(update("taskXXXXX",json).toString());*/




	}
	
	
		public static JSONArray nameList() throws JSONException
		{
			List<String[]> list = new ArrayList<String[]>();

			String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

			String info= Request.post(app_submit_url + "TaskNameList2", list);
			return new JSONArray(info);
		}
		
		public static JSONObject query(String id) throws JSONException
		{
			List<String[]> list = new ArrayList<String[]>();

			list.add(new String[]{"id",id});
			
			String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

			String info= Request.post(app_submit_url + "TaskTypeById2", list);
			return new JSONObject(info);
		}
		
			public static String delete(String id) throws JSONException
			{
				List<String[]> list = new ArrayList<String[]>();
				list.add(new String[]{"id",id});

				String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

				String info= Request.post(app_submit_url + "DeleteTaskType2", list);
				return info;
			}
			
			public static String create(JSONObject newValues)
			{
				List<String[]> list = new ArrayList<String[]>();
				list.add(new String[]{"newValues",newValues.toString()});

				String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

				String info= Request.post(app_submit_url + "CreateTaskType2", list);
				return info;
			}
			
			public static String update(String id,JSONObject newValues)
			{
				List<String[]> list = new ArrayList<String[]>();
				
				list.add(new String[]{"id",id});

				list.add(new String[]{"newValues",newValues.toString()});

				String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

				String info= Request.post(app_submit_url + "UpdateTaskTypeInfo2", list);
				return info;
			}


}

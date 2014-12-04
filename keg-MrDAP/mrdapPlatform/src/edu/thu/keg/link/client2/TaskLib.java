package edu.thu.keg.link.client2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class TaskLib
{

	public static void main(String[] args) throws JSONException
	{
		System.out.println(list());
	}
	
	public static JSONArray list() throws JSONException
	{
		List<String[]> list = new ArrayList<String[]>();

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "TaskJarCollection2", list);
		return new JSONArray(info);
	}

}

package edu.thu.keg.link.client.fs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class FS
{

	public static JSONObject getList(String path) throws JSONException
	{
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[]{"path",path});

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		return new JSONObject(Request.post(app_submit_url + "ListFile", list));

	}

	public static void main(String[] args) throws IOException,
			InterruptedException, JSONException
	{
		
		System.out.println(getList("/home"));

	}

	
}

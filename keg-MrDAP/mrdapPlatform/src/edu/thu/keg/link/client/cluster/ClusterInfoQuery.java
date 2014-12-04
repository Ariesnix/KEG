package edu.thu.keg.link.client.cluster;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class ClusterInfoQuery
{

	public static JSONObject query() throws JSONException
	{

		String APPLICATION_INFO_URL = Default.getValue("APPLICATION_INFO_URL");

		String str=Request.get(APPLICATION_INFO_URL + "/ws/v1/cluster/metrics");
	
		
		return new JSONObject(str);
	}
	
	public static void main(String[] args) throws JSONException
	{
		System.out.println(query().toString());
		
		
	}

}

package edu.thu.keg.link.client2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class MetaInfo
{

	private  static JSONArray getMetaInfo(String name) throws JSONException
	{
		
			List<String[]> list = new ArrayList<String[]>();

			String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

			String info= Request.post(app_submit_url + name, list);
			return new JSONArray(info);
		
	}
	
	public static JSONArray getTaskTypeArgsMetaInfo() throws JSONException
	{
		return getMetaInfo("TaskArgsMeta2") ;
	}
	
	public static JSONArray getTaskTypeMetaInfo() throws JSONException
	{
		return getMetaInfo("TaskTypeMeta2") ;
	}
	public static JSONArray getTaskTableMapMetaInfo() throws JSONException
	{
		return getMetaInfo("TaskTableMapMeta2") ;
	}
	public static JSONArray getHBaseTableMetaInfo() throws JSONException
	{
		return getMetaInfo("HbaseTableMeta2") ;
	}
	
	
	public static void main(String[] args) throws JSONException
	{
		System.out.println(getTaskTypeArgsMetaInfo());
		System.out.println(getTaskTypeMetaInfo());
		System.out.println(getTaskTableMapMetaInfo());
		System.out.println(getHBaseTableMetaInfo());

		
		
	}

}

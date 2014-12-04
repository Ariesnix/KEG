package edu.thu.keg.link.client2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class ZK_info
{

	//获取zookeper 配置信息
	public static JSONObject get() throws JSONException
	{
		List<String[]> list = new ArrayList<String[]>();
		
		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info= Request.post(app_submit_url + "ZKinfo", list);
		
		return new JSONObject(info);

	}
	
	public static void main(String[] args) throws JSONException
	{
		System.out.println(get());
	}

}

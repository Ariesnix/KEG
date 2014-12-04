package edu.thu.keg.link.client.taskClient;

import it.sauronsoftware.base64.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class TaskClient {

	public static String reload() {
		List<String[]> list = new ArrayList<String[]>();

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		return Request.post(app_submit_url + "ReloadXML", list);

	}

	public static void main(String[] args) throws IOException,
			InterruptedException, JSONException {
		// String str =
		// "{\"tables\":{\"mro1412933964827\":\"cf:startTime,cf:endTime,cf:MR.LteScRSRP,cf:MR.LteNcRSRP\"},\"keySpan\":\"\",\"timeSpan\":\"\",\"num\":\"3\",\"diff\":\"-6\",\"min\":\"31\",\"output\":\"shsx15511\"}";
		Default.initDefault("./default.xml");
		String str = "{\"keySpan\":\"\",\"timeSpan\":\"\",\"num\":\"3\",\"diff\":\"-6\",\"min\":\"31\",\"output\":\"shsx15511\"}";

		JSONObject uu = new JSONObject(str);

		JSONArray jarr = new JSONArray();
		jarr.put("MRO1413638437384");

		uu.put("tableIds", jarr);

		System.out.println(uu.toString());

		// / num_executors=taskTypeInfo.getString("num_executors");
		// driver_memory = taskTypeInfo.getString("driver_memory");
		// executor_memory = taskTypeInfo.getString("executor_memory");
		// executor_cores = taskTypeInfo.getString("executor_cores");

		JSONObject runtimeinfo = new JSONObject();
		runtimeinfo.put("num_executors", 7);
		runtimeinfo.put("driver_memory", 7);
		runtimeinfo.put("executor_memory", 10);
		runtimeinfo.put("executor_cores", 7);

		uu.put("RUNTIME_INFO", runtimeinfo);

		reload();

		// 待测试运行
		System.out.println(sumbit("task3_1_extension", uu.toString()));

	}

	/*
	 * String argss=" --class pagerank.PageRank "+ "--master yarn-cluster "+
	 * "--num-executors 6 "+ "--driver-memory 4g "+ "--executor-memory 8g "+
	 * "--executor-cores 8 "+ "./spark-kmeans-10.jar";
	 */

	// mainClass=pagerank.PageRank&num_executors=6&driver_memory=4&executor_memory=8&executor_cores=8&jar_path=./spark-kmeans-10.jar

	// for hbase
	public static String sumbit(String typeId, String args) {
		args = Base64.encode(args);

		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[] { "id", typeId });
		list.add(new String[] { "args", args });
		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		return Request.post(app_submit_url + "SubmitJiangJiu", list);

	}

	public static String kill(String taskId) {
		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[] { "appId", taskId });

		return Request.post(app_submit_url + "kill", list);

	}

}

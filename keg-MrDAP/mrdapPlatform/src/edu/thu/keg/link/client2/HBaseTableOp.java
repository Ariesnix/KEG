package edu.thu.keg.link.client2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.client.env.Default;
import edu.thu.keg.link.common.request.Request;

public class HBaseTableOp {

	public static void main(String[] args) throws JSONException {
//		JSONObject newValues = new JSONObject();
//		try {
//			// dm.addDataset2Hbase(hBase.getTableName(type), type);
//			newValues.put("id", "9idtable");
//			newValues.put("dataType", "");
//			newValues.put("rowKeyPattern", "rowkey");
//			newValues.put("columns", "colomn");
//			newValues.put("lineCount", "9");
//			// System.out.println("mro行数：" + getRows(""));
//			String s = HBaseTableOp.create(newValues);
//			System.out.println(s);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		// list
		 System.out.println(list());
		//
		// //delete
		// System.out.println(delete("tableUU"));
		//
		// //create
		// JSONObject newValues=new JSONObject();
		// newValues.put("id","sfsfsf" );
		// newValues.put("rowKeyPattern","UUUUXXX" );
		// newValues.put("columns","sfsfsf" );
		//
		// System.out.println(create(newValues));
		//
		// //update
		// System.out.println(update("sfsfsf",newValues));

	}

	// 列出 所有hbase表
	public static JSONArray list() throws JSONException {
		List<String[]> list = new ArrayList<String[]>();

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info = Request.post(app_submit_url + "HBaseTableList2", list);
		return new JSONArray(info);
	}

	// 列出 所有hbase表
	public static String delete(String id) throws JSONException {
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] { "id", id });

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info = Request.post(app_submit_url + "DeleteHBaseTable2", list);
		return info;
	}

	public static String create(JSONObject newValues) {
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] { "newValues", newValues.toString() });

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info = Request.post(app_submit_url + "CreateHBaseTable2", list);
		return info;
	}

	public static String update(String id, JSONObject newValues) {
		List<String[]> list = new ArrayList<String[]>();

		list.add(new String[] { "id", id });

		list.add(new String[] { "newValues", newValues.toString() });

		String app_submit_url = Default.getValue("APPLICATION_SUBMIT_URL");

		String info = Request.post(app_submit_url + "UpdateHBaseTableInfo2",
				list);
		return info;
	}

}

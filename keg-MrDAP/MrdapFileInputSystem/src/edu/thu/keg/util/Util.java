package edu.thu.keg.util;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.thu.keg.driver.HAdmin;
import edu.thu.keg.driver.PutIn;
import edu.thu.keg.link.client2.ZK_info;

public class Util {
	public static void extractFile(File dir) {
		for (File files : dir.listFiles()) {
			if (!files.isDirectory()) {
				if (files.getName().endsWith(".zip"))
					try {
						ZipTools.unzip(files.getPath(), null);
					} catch (IOException e) {
						// System.out.println(files.getName());
						e.printStackTrace();
					}
			} else
				extractFile(files);
		}
	}

	public static void getXMLFiles(File dir, List<File> re) {
		if (!dir.isDirectory()) {
			return;
		}
		for (File file : dir.listFiles()) {
			if (!file.isDirectory()) {
				if (file.getName().endsWith(".xml"))
					re.add(file);
			} else
				getXMLFiles(file, re);
		}
	}

	public static Document read(File fileName) throws MalformedURLException,
			DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(fileName);
		return document;
	}

	public static String getTimeAllString(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date reportTime;
		String timeNew = "";
		try {
			reportTime = sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(reportTime);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);

			timeNew = year + String.format("%02d", month)
					+ String.format("%02d", day) + String.format("%02d", hour)
					+ String.format("%02d", minute)
					+ String.format("%02d", second);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return timeNew;
	}

	public static String getNowTime() {
		String time = "";
		// TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
		time = sdf.format(new Date());
		return time;
	}

	public static Element getRootElement(Document doc) {
		return doc.getRootElement();
	}

	public static void addLine2HBase(PutIn pi, String rollkey, String[] line) {

		try {
			pi.put(rollkey, line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static PutIn getPutIn(List<String[]> confList, String tableName,
			String[] columns) {
		try {
			return new PutIn(confList, tableName, columns);
		} catch (MasterNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void closeHBase(PutIn pi) {

		try {
			pi.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void removeHtable(List<String[]> confList, String tableName) {
		HAdmin had = null;
		try {
			had = new HAdmin(confList);
			if (had.tableExists(tableName)) {
				had.deleteTable(tableName);
			}
			had.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static List<String[]> getConList() {
		List<String[]> confList = new ArrayList<String[]>();
		JSONObject ja;
		try {
			ja = ZK_info.get();
			Iterator<String> itKeys = ja.keys();
			while (itKeys.hasNext()) {
				String key = itKeys.next();
				confList.add(new String[] { key, ja.getString(key) });
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return confList;

	}

	public static void initHBase(List<String[]> confList, String tableName) {
		HAdmin had = null;
		try {
			Util.removeHtable(confList, tableName);
			had = new HAdmin(confList);
			had.createTable(tableName, new String[] { "cf" });
			had.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String arg[]) {
		System.out.println(getNowTime());
	}
}

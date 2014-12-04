package edu.thu.keg.mr;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.thu.keg.driver.PutIn;
import edu.thu.keg.link.client2.HBaseTableOp;
import edu.thu.keg.link.client2.ZK_info;
import edu.thu.keg.util.Util;

public class MrsDao extends Thread implements IHBaseManager {
	final static String[] RSRP = { "cf:towerId", "cf:startTime", "cf:endTime",
			"cf:id", "cf:MR.RSRP.00", "cf:MR.RSRP.01", "cf:MR.RSRP.02",
			"cf:MR.RSRP.03", "cf:MR.RSRP.04", "cf:MR.RSRP.05", "cf:MR.RSRP.06",
			"cf:MR.RSRP.07", "cf:MR.RSRP.08", "cf:MR.RSRP.09", "cf:MR.RSRP.10",
			"cf:MR.RSRP.11", "cf:MR.RSRP.12", "cf:MR.RSRP.13", "cf:MR.RSRP.14",
			"cf:MR.RSRP.15", "cf:MR.RSRP.16", "cf:MR.RSRP.17", "cf:MR.RSRP.18",
			"cf:MR.RSRP.19", "cf:MR.RSRP.20", "cf:MR.RSRP.21", "cf:MR.RSRP.22",
			"cf:MR.RSRP.23", "cf:MR.RSRP.24", "cf:MR.RSRP.25", "cf:MR.RSRP.26",
			"cf:MR.RSRP.27", "cf:MR.RSRP.28", "cf:MR.RSRP.29", "cf:MR.RSRP.30",
			"cf:MR.RSRP.31", "cf:MR.RSRP.32", "cf:MR.RSRP.33", "cf:MR.RSRP.34",
			"cf:MR.RSRP.35", "cf:MR.RSRP.36", "cf:MR.RSRP.37", "cf:MR.RSRP.38",
			"cf:MR.RSRP.39", "cf:MR.RSRP.40", "cf:MR.RSRP.41", "cf:MR.RSRP.42",
			"cf:MR.RSRP.43", "cf:MR.RSRP.44", "cf:MR.RSRP.45", "cf:MR.RSRP.46",
			"cf:MR.RSRP.47" };
	final static String[] ReceivedIPower = { "cf:towerId", "cf:startTime",
			"cf:endTime", "cf:id", "cf:MR.ReceivedIPower.00",
			"cf:MR.ReceivedIPower.01", "cf:MR.ReceivedIPower.02",
			"cf:MR.ReceivedIPower.03", "cf:MR.ReceivedIPower.04",
			"cf:MR.ReceivedIPower.05", "cf:MR.ReceivedIPower.06",
			"cf:MR.ReceivedIPower.07", "cf:MR.ReceivedIPower.08",
			"cf:MR.ReceivedIPower.09", "cf:MR.ReceivedIPower.10",
			"cf:MR.ReceivedIPower.11", "cf:MR.ReceivedIPower.12",
			"cf:MR.ReceivedIPower.13", "cf:MR.ReceivedIPower.14",
			"cf:MR.ReceivedIPower.15", "cf:MR.ReceivedIPower.16",
			"cf:MR.ReceivedIPower.17", "cf:MR.ReceivedIPower.18",
			"cf:MR.ReceivedIPower.19", "cf:MR.ReceivedIPower.20",
			"cf:MR.ReceivedIPower.21", "cf:MR.ReceivedIPower.22",
			"cf:MR.ReceivedIPower.23", "cf:MR.ReceivedIPower.24",
			"cf:MR.ReceivedIPower.25", "cf:MR.ReceivedIPower.26",
			"cf:MR.ReceivedIPower.27", "cf:MR.ReceivedIPower.28",
			"cf:MR.ReceivedIPower.29", "cf:MR.ReceivedIPower.30",
			"cf:MR.ReceivedIPower.31", "cf:MR.ReceivedIPower.32",
			"cf:MR.ReceivedIPower.33", "cf:MR.ReceivedIPower.34",
			"cf:MR.ReceivedIPower.35", "cf:MR.ReceivedIPower.36",
			"cf:MR.ReceivedIPower.37", "cf:MR.ReceivedIPower.38",
			"cf:MR.ReceivedIPower.39", "cf:MR.ReceivedIPower.40",
			"cf:MR.ReceivedIPower.41", "cf:MR.ReceivedIPower.42",
			"cf:MR.ReceivedIPower.43", "cf:MR.ReceivedIPower.44",
			"cf:MR.ReceivedIPower.45", "cf:MR.ReceivedIPower.46",
			"cf:MR.ReceivedIPower.47", "cf:MR.ReceivedIPower.48",
			"cf:MR.ReceivedIPower.49", "cf:MR.ReceivedIPower.50",
			"cf:MR.ReceivedIPower.51", "cf:MR.ReceivedIPower.52" };
	final static String[] TadvRsrp = { "cf:towerId", "cf:startTime",
			"cf:endTime", "cf:id", "cf:MR.Tadv00Rsrp00", "cf:MR.Tadv00Rsrp01",
			"cf:MR.Tadv00Rsrp02", "cf:MR.Tadv00Rsrp03", "cf:MR.Tadv00Rsrp04",
			"cf:MR.Tadv00Rsrp05", "cf:MR.Tadv00Rsrp06", "cf:MR.Tadv00Rsrp07",
			"cf:MR.Tadv00Rsrp08", "cf:MR.Tadv00Rsrp09", "cf:MR.Tadv00Rsrp10",
			"cf:MR.Tadv00Rsrp11", "cf:MR.Tadv01Rsrp00", "cf:MR.Tadv01Rsrp01",
			"cf:MR.Tadv01Rsrp02", "cf:MR.Tadv01Rsrp03", "cf:MR.Tadv01Rsrp04",
			"cf:MR.Tadv01Rsrp05", "cf:MR.Tadv01Rsrp06", "cf:MR.Tadv01Rsrp07",
			"cf:MR.Tadv01Rsrp08", "cf:MR.Tadv01Rsrp09", "cf:MR.Tadv01Rsrp10",
			"cf:MR.Tadv01Rsrp11", "cf:MR.Tadv02Rsrp00", "cf:MR.Tadv02Rsrp01",
			"cf:MR.Tadv02Rsrp02", "cf:MR.Tadv02Rsrp03", "cf:MR.Tadv02Rsrp04",
			"cf:MR.Tadv02Rsrp05", "cf:MR.Tadv02Rsrp06", "cf:MR.Tadv02Rsrp07",
			"cf:MR.Tadv02Rsrp08", "cf:MR.Tadv02Rsrp09", "cf:MR.Tadv02Rsrp10",
			"cf:MR.Tadv02Rsrp11", "cf:MR.Tadv03Rsrp00", "cf:MR.Tadv03Rsrp01",
			"cf:MR.Tadv03Rsrp02", "cf:MR.Tadv03Rsrp03", "cf:MR.Tadv03Rsrp04",
			"cf:MR.Tadv03Rsrp05", "cf:MR.Tadv03Rsrp06", "cf:MR.Tadv03Rsrp07",
			"cf:MR.Tadv03Rsrp08", "cf:MR.Tadv03Rsrp09", "cf:MR.Tadv03Rsrp10",
			"cf:MR.Tadv03Rsrp11", "cf:MR.Tadv04Rsrp00", "cf:MR.Tadv04Rsrp01",
			"cf:MR.Tadv04Rsrp02", "cf:MR.Tadv04Rsrp03", "cf:MR.Tadv04Rsrp04",
			"cf:MR.Tadv04Rsrp05", "cf:MR.Tadv04Rsrp06", "cf:MR.Tadv04Rsrp07",
			"cf:MR.Tadv04Rsrp08", "cf:MR.Tadv04Rsrp09", "cf:MR.Tadv04Rsrp10",
			"cf:MR.Tadv04Rsrp11", "cf:MR.Tadv05Rsrp00", "cf:MR.Tadv05Rsrp01",
			"cf:MR.Tadv05Rsrp02", "cf:MR.Tadv05Rsrp03", "cf:MR.Tadv05Rsrp04",
			"cf:MR.Tadv05Rsrp05", "cf:MR.Tadv05Rsrp06", "cf:MR.Tadv05Rsrp07",
			"cf:MR.Tadv05Rsrp08", "cf:MR.Tadv05Rsrp09", "cf:MR.Tadv05Rsrp10",
			"cf:MR.Tadv05Rsrp11", "cf:MR.Tadv06Rsrp00", "cf:MR.Tadv06Rsrp01",
			"cf:MR.Tadv06Rsrp02", "cf:MR.Tadv06Rsrp03", "cf:MR.Tadv06Rsrp04",
			"cf:MR.Tadv06Rsrp05", "cf:MR.Tadv06Rsrp06", "cf:MR.Tadv06Rsrp07",
			"cf:MR.Tadv06Rsrp08", "cf:MR.Tadv06Rsrp09", "cf:MR.Tadv06Rsrp10",
			"cf:MR.Tadv06Rsrp11", "cf:MR.Tadv07Rsrp00", "cf:MR.Tadv07Rsrp01",
			"cf:MR.Tadv07Rsrp02", "cf:MR.Tadv07Rsrp03", "cf:MR.Tadv07Rsrp04",
			"cf:MR.Tadv07Rsrp05", "cf:MR.Tadv07Rsrp06", "cf:MR.Tadv07Rsrp07",
			"cf:MR.Tadv07Rsrp08", "cf:MR.Tadv07Rsrp09", "cf:MR.Tadv07Rsrp10",
			"cf:MR.Tadv07Rsrp11", "cf:MR.Tadv08Rsrp00", "cf:MR.Tadv08Rsrp01",
			"cf:MR.Tadv08Rsrp02", "cf:MR.Tadv08Rsrp03", "cf:MR.Tadv08Rsrp04",
			"cf:MR.Tadv08Rsrp05", "cf:MR.Tadv08Rsrp06", "cf:MR.Tadv08Rsrp07",
			"cf:MR.Tadv08Rsrp08", "cf:MR.Tadv08Rsrp09", "cf:MR.Tadv08Rsrp10",
			"cf:MR.Tadv08Rsrp11", "cf:MR.Tadv09Rsrp00", "cf:MR.Tadv09Rsrp01",
			"cf:MR.Tadv09Rsrp02", "cf:MR.Tadv09Rsrp03", "cf:MR.Tadv09Rsrp04",
			"cf:MR.Tadv09Rsrp05", "cf:MR.Tadv09Rsrp06", "cf:MR.Tadv09Rsrp07",
			"cf:MR.Tadv09Rsrp08", "cf:MR.Tadv09Rsrp09", "cf:MR.Tadv09Rsrp10",
			"cf:MR.Tadv09Rsrp11", "cf:MR.Tadv10Rsrp00", "cf:MR.Tadv10Rsrp01",
			"cf:MR.Tadv10Rsrp02", "cf:MR.Tadv10Rsrp03", "cf:MR.Tadv10Rsrp04",
			"cf:MR.Tadv10Rsrp05", "cf:MR.Tadv10Rsrp06", "cf:MR.Tadv10Rsrp07",
			"cf:MR.Tadv10Rsrp08", "cf:MR.Tadv10Rsrp09", "cf:MR.Tadv10Rsrp10",
			"cf:MR.Tadv10Rsrp11" };
	final static List<String[]> confList = Util.getConList();

	boolean overFlag = false;
	String inputFolder;
	String outputFolder;
	String tableName = "mrs";
	String version;
	List<File> files = null;
	int lineCountRsrp = 0;
	int lineReceivedipower = 0;
	int lineTadvrsrp = 0;
	HashMap<String, PutIn> piMap = new HashMap<String, PutIn>();
	final public static List<String> KIDTableList = new ArrayList<String>();
	static {
		KIDTableList.add("rsrp");
		KIDTableList.add("receivedipower");
		KIDTableList.add("tadvrsrp");
	}
	HashMap<String, String> kidTableMap = new HashMap<String, String>();

	String RSRPTName = "RSRP";
	String ReceivedIPowerTName = "ReceivedIPower";
	String TadvRsrpTName = "TadvRsrp";
	int process = 0;

	@Override
	public void init(String inputFolder, String outputFolder, String tableName,
			int serial, String version) throws RuntimeException {
		this.overFlag = false;
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder + "/" + serial + "/";
		String fidx = Util.getNowTime();
		this.lineCountRsrp = 0;
		this.lineReceivedipower = 0;
		this.lineTadvrsrp = 0;
		this.tableName += fidx;
		for (String name : KIDTableList)
			kidTableMap.put(name, name + fidx);
		File folder = new File(inputFolder);
		Util.extractFile(folder);
		this.version = version;
		files = new ArrayList<File>();
		Util.getXMLFiles(folder, files);
		if (files.size() == 0)
			throw new RuntimeException("输入路径错误MRS！" + " 是否是目录"
					+ folder.isDirectory() + " 路径" + inputFolder);

	}

	public void run() {
		for (String type : KIDTableList) {
			Util.initHBase(confList, kidTableMap.get(type));

			System.out.println("已经添加" + kidTableMap.get(type));

		}
		piMap.put(KIDTableList.get(0), Util.getPutIn(confList,
				kidTableMap.get(KIDTableList.get(0)), RSRP));
		piMap.put(KIDTableList.get(1), Util.getPutIn(confList,
				kidTableMap.get(KIDTableList.get(1)), ReceivedIPower));
		piMap.put(KIDTableList.get(2), Util.getPutIn(confList,
				kidTableMap.get(KIDTableList.get(2)), TadvRsrp));
		for (int i = 0; i < files.size(); i++) {
			process++;
			if (overFlag)
				break;
			if (!files.get(i).isDirectory()) {
				System.out.println(i + "/" + files.size() + " " + "Analyzing: "
						+ files.get(i).getPath());
				Element root = null;
				try {
					root = Util.getRootElement(Util.read(files.get(i)));
					loadMrs(root);
				} catch (Exception e) {
					System.err.println("xml文件有错！" + e.getMessage());
					// e.printStackTrace();
				}

			}
		}
		for (String type : KIDTableList)
			Util.closeHBase(piMap.get(type));
		System.out.println(tableName + "中的" + files.size() + "文件已完成！");
		inputHbaseMetaData();
	}

	void loadMrs(Element root) {
		Node fileHeader = root.selectSingleNode("//fileHeader");
		String startTime = fileHeader.valueOf("@startTime");
		String endTime = fileHeader.valueOf("@endTime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Node eNB = root.selectSingleNode("//eNB");
		String towerId = eNB.valueOf("@id");
		String lineMain = towerId;
		List<Element> measurements = root.selectNodes("//measurement");
		PutIn pi = null;
		for (int i = 0; i < measurements.size(); i++) {
			Element measure = measurements.get(i);
			String measureName = measure.valueOf("@mrName");
			switch (measureName.toLowerCase()) {
			case "mr.rsrp":

				pi = piMap.get("rsrp");
				rsrp(measure, towerId, Util.getTimeAllString(startTime),
						Util.getTimeAllString(endTime), pi);

				break;
			case "mr.receivedipower":
				pi = piMap.get("receivedipower");
				receivedipower(measure, towerId,
						Util.getTimeAllString(startTime),
						Util.getTimeAllString(endTime), pi);

				break;
			case "mr.tadvrsrp":
				pi = piMap.get("tadvrsrp");
				tadvrsrp(measure, towerId, Util.getTimeAllString(startTime),
						Util.getTimeAllString(endTime), pi);
				break;
			default:
				continue;
			}
		}

	}

	private String mergeTime(String time1, String time2) {
		// System.out.print(time1 + "" + time2);
		return time1.substring(0, 12) + time2.subSequence(8, 12);
	}

	void rsrp(Element measure, String towerId, String startTime,
			String endTime, PutIn pi) {
		List<Node> objs = measure.selectNodes("object");
		for (int j = 0; j < objs.size(); j++) {
			Node obj = objs.get(j);
			String id = obj.valueOf("@id");
			List<Node> vs = obj.selectNodes("v");
			String contentInNode[];
			String rollkey = id + "#" + mergeTime(startTime, endTime);
			for (Node v : vs) {
				contentInNode = v.getText().trim().split(" ");
				String colomn[] = new String[contentInNode.length + 4];
				colomn[0] = towerId;
				colomn[1] = startTime;
				colomn[2] = endTime;
				colomn[3] = id;
				System.arraycopy(contentInNode, 0, colomn, 4,
						contentInNode.length);
				lineCountRsrp++;
				Util.addLine2HBase(pi, rollkey, colomn);
			}
		}
	}

	void receivedipower(Element measure, String towerId, String startTime,
			String endTime, PutIn pi) {
		List<Node> objs = measure.selectNodes("object");
		for (int j = 0; j < objs.size(); j++) {
			Node obj = objs.get(j);
			String id = obj.valueOf("@id");
			String[] ids = id.split(":");
			List<Node> vs = obj.selectNodes("v");
			String contentInNode[];
			String rollkey = ids[0] + "#" + mergeTime(startTime, endTime) + "#"
					+ ids[2];
			for (Node v : vs) {
				contentInNode = v.getText().trim().split(" ");
				String colomn[] = new String[contentInNode.length + 4];
				colomn[0] = towerId;
				colomn[1] = startTime;
				colomn[2] = endTime;
				colomn[3] = id;
				System.arraycopy(contentInNode, 0, colomn, 4,
						contentInNode.length);
				lineReceivedipower++;
				Util.addLine2HBase(pi, rollkey, colomn);
			}
		}
	}

	void tadvrsrp(Element measure, String towerId, String startTime,
			String endTime, PutIn pi) {
		List<Node> objs = measure.selectNodes("object");
		for (int j = 0; j < objs.size(); j++) {
			Node obj = objs.get(j);
			String id = obj.valueOf("@id");
			List<Node> vs = obj.selectNodes("v");
			String contentInNode[];
			String rollkey = id + "#" + mergeTime(startTime, endTime);
			for (Node v : vs) {
				contentInNode = v.getText().trim().split(" ");
				String colomn[] = new String[contentInNode.length + 4];
				colomn[0] = towerId;
				colomn[1] = startTime;
				colomn[2] = endTime;
				colomn[3] = id;
				System.arraycopy(contentInNode, 0, colomn, 4,
						contentInNode.length);
				lineTadvrsrp++;
				Util.addLine2HBase(pi, rollkey, colomn);
			}
		}
	}

	@Override
	public void setInputFile(String folder) {
		this.inputFolder = folder;

	}

	@Override
	public void startMigration() {
		this.start();

	}

	@Override
	public void killMigration() {
		overFlag = true;

	}

	@Override
	public double getProcess() {
		// TODO Auto-generated method stub
		return (process + 1) / files.size();
	}

	@Override
	public String getTableName(String type) {
		String s = kidTableMap.get(type.toLowerCase());
		if (s == null)
			return tableName;
		return s;
	}

	@Override
	public String getRowKey(String type) {
		if (type.toLowerCase().equals("rsrp"))
			return "id#timestamp";
		else if (type.toLowerCase().equals("receivedipower"))
			return "id#timestamp#zizhen";
		else if (type.toLowerCase().equals("tadvrsrp"))
			return "id#timestamp";
		return null;
	}

	@Override
	public String getColumn(String type) {
		String column = "";
		if (type.toLowerCase().equals("rsrp")) {
			for (String c : RSRP)
				column += "," + c;
			column = column.replaceFirst(",", "");
		} else if (type.toLowerCase().equals("receivedipower")) {
			for (String c : ReceivedIPower)
				column += "," + c;
			column = column.replaceFirst(",", "");
		} else if (type.toLowerCase().equals("tadvrsrp")) {
			for (String c : TadvRsrp)
				column += "," + c;
			column = column.replaceFirst(",", "");
		}
		return column;
	}

	@Override
	public int getRows(String type) {
		if (type.toLowerCase().equals("rsrp")) {
			return lineCountRsrp;
		} else if (type.toLowerCase().equals("receivedipower")) {
			return lineReceivedipower;
		} else if (type.toLowerCase().equals("tadvrsrp")) {
			return lineTadvrsrp;
		}
		return 0;
	}

	@Override
	public void inputHbaseMetaData() {
		JSONObject newValues = new JSONObject();
		try {
			for (String subTableType : MrsDao.KIDTableList) {
				// dm.addDataset2Hbase(hBase.getTableName(type), type);
				newValues.put("id", getTableName(subTableType));
				newValues.put("dataType", subTableType);
				newValues.put("rowKeyPattern", getRowKey(subTableType));
				newValues.put("columns", getColumn(subTableType));
				newValues.put("lineCount", getRows(subTableType));
				System.out.println("mrs行数：" + getRows(subTableType));
				HBaseTableOp.create(newValues);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

package edu.thu.keg.mr;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import edu.thu.keg.driver.Default;
import edu.thu.keg.driver.HAdmin;
import edu.thu.keg.driver.PutIn;
import edu.thu.keg.link.client2.HBaseTableOp;
import edu.thu.keg.link.client2.ZK_info;
import edu.thu.keg.util.Util;

public class MroDao extends Thread implements IHBaseManager {
	final static List<String[]> confList = Util.getConList();

	final static String[] columns = { "cf:cellid", "cf:TimeStamp",
			"cf:MmeCode", "cf:MmeGroupId", "cf:MmeUeS1apId", "cf:times",
			"cf:zizhen", "cf:enbId", "cf:startTime", "cf:endTime", "cf:zaibo",
			"cf:MR.LteScRSRP", "cf:MR.LteNcRSRP", "cf:MR.LteScRSRQ",
			"cf:MR.LteNcRSRQ", "cf:MR.LteScTadv", "cf:MR.LteScPHR",
			"cf:MR.LteScAOA", "cf:MR.LteScSinrUL", "cf:MR.LteScEarfcn",
			"cf:MR.LteScPci", "cf:MR.LteNcEarfcn", "cf:MR.LteNcPci",
			"cf:MR.GsmNcellBcch", "cf:MR.GsmNcellCarrierRSSI",
			"cf:MR.GsmNcellNcc", "cf:MR.GsmNcellBcc", "cf:MR.TdsPccpchRSCP",
			"cf:MR.TdsNcellUarfcn", "cf:MR.TdsCellParameterId",
			"cf:MR.LteScRI1", "cf:MR.LteScRI2", "cf:MR.LteScRI4",
			"cf:MR.LteScRI8", "cf:MR.LteSceNBRxTxTimeDiff", "cf:MR.LteScBSR",
			"cf:MR.LteScPUSCHPRBNum", "cf:MR.LteScPDSCHPRBNum" };

	String inputFolder = null;
	String outputFolder = null;

	final static String rowKey = "cf:cellid#cf:TimeStamp#cf:MmeCode#cf:MmeGroupId#cf:MmeUeS1apId#cf:times#cf:zizhen";

	FileWriter fw1 = null;
	FileWriter fw2 = null;
	FileWriter fw3 = null;
	String version = "new";
	LinkedHashMap<String, Integer> Attribute2Num = new LinkedHashMap<String, Integer>();
	LinkedHashMap<String, Integer> Attribute2index = new LinkedHashMap<String, Integer>();
	List<File> files;
	int process = 0;
	boolean overFlag = false;
	HAdmin had = null;
	PutIn pi = null;
	String tableName = "mro";
	int lineCount;

	@Override
	public void init(String inputFolder, String outputFolder, String tableName,
			int serial, String version) throws RuntimeException {
		this.overFlag = false;
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder + "/" + serial + "/";
		this.tableName = tableName + Util.getNowTime();
		this.lineCount = 0;
		loadFormat("MRO1", 1);
		loadFormat("MRO2", 2);
		loadFormat("MRO3", 3);

		File folder = new File(inputFolder);
		Util.extractFile(folder);
		this.version = version;
		files = new ArrayList<File>();
		Util.getXMLFiles(folder, files);
		if (files.size() == 0)
			throw new RuntimeException("输入路径错误MRO！" + " 是否是目录"
					+ folder.isDirectory() + " 路径" + inputFolder);
		try {
			had = new HAdmin(confList);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if (had.tableExists(this.tableName)) {
				had.deleteTable(this.tableName);
				had.createTable(this.tableName, new String[] { "cf" });
			} else {
				had.createTable(this.tableName, new String[] { "cf" });

			}
			had.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// printAttribute();

	}

	private void loadFormat(String fileName, int serail) {
		LineNumberReader ln = null;
		try {
			FileReader fr = new FileReader(new File(fileName));
			ln = new LineNumberReader(fr);
			String line = ln.readLine();
			while (line != null && !line.equals("")) {
				Attribute2Num.put(line.trim().toLowerCase(), serail);
				line = ln.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				ln.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readFromXML(Element root) throws DocumentException {

		List<Element> smr = root.selectNodes("//smr");
		if (smr == null)
			throw new DocumentException("xml文件没有smr");
		String[] attbutes = smr.get(0).getText().trim().toLowerCase()
				.split(" +");
		// System.out.println("一共有属性" + attbutes.length + "个");
		for (int i = 0; i < attbutes.length; i++) {
			if (Attribute2Num.get(attbutes[i]) == null)
				System.out.println(attbutes[i]);
			Attribute2index.put(attbutes[i].toLowerCase(), i);
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
	public void run() {
		Util.initHBase(confList, tableName);
		pi = Util.getPutIn(confList, tableName, columns);
		for (int i = 0; i < files.size(); i++) {
			process = i;
			if (overFlag)
				break;
			if (!files.get(i).isDirectory()) {
				System.out.println(i + "/" + files.size() + " " + "Analyzing: "
						+ files.get(i).getPath());
				Element root = null;
				try {
					root = Util.getRootElement(Util.read(files.get(i)));
					readFromXML(root);
					if (version.toLowerCase().equals("old"))
						writeToFile(root);
					else
						writeToFileNew(root);
				} catch (MalformedURLException | DocumentException e) {
					System.err.println("xml文件有错！");
					// e.printStackTrace();
				}

			}
		}

		Util.closeHBase(pi);
		System.out.println(tableName + "中的" + files.size() + "文件已完成！");
		inputHbaseMetaData();
	}

	@Override
	public void killMigration() {
		this.overFlag = true;// 终止线程thread

	}

	@Override
	public double getProcess() {
		double d = (double) (process + 1) / files.size();
		return (d);
	}

	// @Override
	// public void writeLine(String rollkey, String[] content) {
	// // TODO Auto-generated method stub
	//
	// }

	private void writeToFile(Element root) {
		// makeHaFile(root);
		Node fileHeader = root.selectSingleNode("//fileHeader");
		String startTime = fileHeader.valueOf("@startTime");
		String endTime = fileHeader.valueOf("@endTime");
		Node eNB = root.selectSingleNode("//eNB");
		String towerId = eNB.valueOf("@id");
		// System.out.println("基站号:" + towerId);
		List<Element> objs = root.selectNodes("//object");
		try {
			for (Element obj : objs) {
				String lineMain = towerId;
				lineMain += " " + startTime;
				lineMain += " " + endTime;
				String line1 = "";
				String line2 = "";
				String line3 = "";
				lineMain += " " + obj.valueOf("@id").trim();
				lineMain += " " + obj.valueOf("@MmeUeS1apId").trim();
				lineMain += " " + obj.valueOf("@MmeGroupId").trim();
				lineMain += " " + obj.valueOf("@MmeCode").trim();
				lineMain += " " + obj.valueOf("@TimeStamp").trim();
				line1 = "";
				line2 = "";
				line3 = "";
				line1 += lineMain.trim();
				line2 += lineMain.trim();
				line3 += lineMain.trim();
				for (Iterator<Element> v = obj.elementIterator(); v.hasNext();) {
					String[] valueLine = v.next().getText().trim().split(" +");
					Iterator<String> itAttr = Attribute2Num.keySet().iterator();
					String s = "";
					if (!line1.equals("")) {
						line1 = line1.trim() + "#";
						line2 = line2.trim() + "#";
						line3 = line3.trim() + "#";
					}

					while (itAttr.hasNext()) {
						s = itAttr.next();
						// System.out.println(s);
						int fileNum = Attribute2Num.get(s);
						// if(valueLine[fileNum])
						String word = "NIL";
						if (Attribute2index.containsKey(s))
							word = valueLine[Attribute2index.get(s)];
						if (fileNum == 1) {
							line1 += word + " ";
						} else if (Attribute2Num.get(s) == 2) {
							line2 += word + " ";
						} else if (Attribute2Num.get(s) == 3) {
							line3 += word + " ";
						} else
							throw new IllegalArgumentException("解析xml分配行数有错误！ "
									+ " " + s);
					}

				}
				fw1.write(line1.trim() + "\n");
				fw2.write(line2.trim() + "\n");
				fw3.write(line3.trim() + "\n");
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {

			try {
				fw1.close();
				fw2.close();
				fw3.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void writeToFileNew(Element root) {
		// makeHaFile(root);
		Node fileHeader = root.selectSingleNode("//fileHeader");
		String startTime = fileHeader.valueOf("@startTime");
		String endTime = fileHeader.valueOf("@endTime");
		Node eNB = root.selectSingleNode("//eNB");
		String enbId = eNB.valueOf("@id");
		// System.out.println("基站号:" + towerId);
		List<Element> objs = root.selectNodes("//object");
		// try {
		// 每一个obj

		String rollkey = "";
		for (Element obj : objs) {
			ArrayList<String> lineHBaseHead = new ArrayList<String>();

			String lineMain = "";
			String id[] = obj.valueOf("@id").trim().split(":");
			lineMain = id[0];
			lineHBaseHead.add(id[0]);
			if (id.length < 3)
				continue;
			lineMain += "#"
					+ Util.getTimeAllString(obj.valueOf("@TimeStamp").trim());

			lineMain += "#" + obj.valueOf("@MmeCode").trim();
			lineMain += "#" + obj.valueOf("@MmeGroupId").trim();
			lineMain += "#" + obj.valueOf("@MmeUeS1apId").trim();

			lineHBaseHead.add(Util.getTimeAllString(obj.valueOf("@TimeStamp")
					.trim()));
			lineHBaseHead.add(obj.valueOf("@MmeCode").trim());
			lineHBaseHead.add(obj.valueOf("@MmeGroupId").trim());
			lineHBaseHead.add(obj.valueOf("@MmeUeS1apId").trim());

			String line1 = "";
			String line2 = "";
			String line3 = "";

			int times = 1;
			// 每一个v
			// System.out.println(obj.valueOf("@TimeStamp").trim() + " "
			// + obj.valueOf("@id").trim());

			for (Iterator<Element> v = obj.elementIterator(); v.hasNext();) {
				ArrayList<String> lineHBase = new ArrayList<String>();
				String[] valueLine = v.next().getText().trim().split(" +");
				Iterator<String> itAttr = Attribute2Num.keySet().iterator();
				String s = "";
				line1 = lineMain.trim();
				line2 = lineMain.trim();
				line3 = lineMain.trim();
				lineHBase.add(times + "");
				lineHBase.add(id[2]);
				lineHBase.add(enbId);
				lineHBase.add(getTimeMinuString(startTime));
				lineHBase.add(getTimeMinuString(endTime));
				lineHBase.add(id[1]);

				String content = enbId + " " + getTimeMinuString(startTime)
						+ " " + getTimeMinuString(endTime) + " " + id[1];
				rollkey = lineMain.trim() + "#" + times + "#" + id[2];
				line1 += "#" + times + "#" + id[2] + " " + content;
				line2 += "#" + times + "#" + id[2] + " " + content;
				line3 += "#" + times + "#" + id[2] + " " + content;

				while (itAttr.hasNext()) {
					s = itAttr.next();
					// System.out.println(s);
					int fileNum = Attribute2Num.get(s);
					// if(valueLine[fileNum])
					String word = "NIL";
					if (Attribute2index.containsKey(s))
						word = valueLine[Attribute2index.get(s)];
					if (fileNum == 1) {
						line1 += " " + word;
						if (!word.equals("NIL"))
							lineHBase.add(word);
						else
							lineHBase.add(null);
					} else if (Attribute2Num.get(s) == 2) {
						line2 += " " + word;
					} else if (Attribute2Num.get(s) == 3) {
						line3 += " " + word;
					} else
						throw new IllegalArgumentException("解析xml分配行数有错误！ "
								+ " " + s);
				}
				// System.out.print(lineHBase.size() + " ");
				String col[] = new String[lineHBaseHead.size()
						+ lineHBase.size()];
				String lineHbHead[] = lineHBaseHead.toArray(new String[0]);
				String lineHb[] = lineHBase.toArray(new String[0]);

				System.arraycopy(lineHbHead, 0, col, 0, lineHbHead.length);
				System.arraycopy(lineHb, 0, col, lineHbHead.length,
						lineHb.length);
				// System.out.print(col.length + " ");
				lineCount++;
				Util.addLine2HBase(pi, rollkey, col);
				// fw1.write(line1.trim() + "\n");
				// fw2.write(line2.trim() + "\n");
				// fw3.write(line3.trim() + "\n");
				times++;
			}

		}
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// } finally {
		//
		// try {
		// fw1.close();
		// fw2.close();
		// fw3.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
	}

	private String getTimeMinuString(String time) {
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

			timeNew = String.format("%02d", hour)
					+ String.format("%02d", minute);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return timeNew;
	}

	void writeAttribute(File file, int type, String frontAttri) {
		Iterator<String> it = Attribute2Num.keySet().iterator();
		try {
			FileWriter fw = new FileWriter(file);
			if (!frontAttri.equals(""))
				fw.write(frontAttri + " ");
			String s = "";
			while (it.hasNext()) {
				s = it.next();
				if (Attribute2Num.get(s) == type)
					fw.write(s.trim() + " ");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String arg[]) {
		Default.init();
		IHBaseManager ihm = new MroDao();
		ihm.init("./in/mro", "./out/mro", "mro", 1, "new");
		ihm.startMigration();
	}

	@Override
	public String getTableName(String type) {
		// TODO Auto-generated method stub
		return this.tableName;
	}

	@Override
	public String getRowKey(String type) {
		// TODO Auto-generated method stub
		return this.rowKey;
	}

	@Override
	public String getColumn(String type) {
		String column = "";
		for (String c : columns)
			column += "," + c;
		column = column.replaceFirst(",", "");
		return column;
	}

	@Override
	public int getRows(String type) {
		// TODO Auto-generated method stub
		return this.lineCount;
	}

	@Override
	public void inputHbaseMetaData() {
		JSONObject newValues = new JSONObject();
		try {
			// dm.addDataset2Hbase(hBase.getTableName(type), type);
			newValues.put("id", getTableName(""));
			newValues.put("dataType", "MRO");
			newValues.put("rowKeyPattern", getRowKey(""));
			newValues.put("columns", getColumn(""));
			newValues.put("lineCount", getRows(""));
			System.out.println("mro行数：" + getRows(""));
			HBaseTableOp.create(newValues);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

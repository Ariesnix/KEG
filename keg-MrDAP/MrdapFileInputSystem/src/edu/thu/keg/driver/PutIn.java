package edu.thu.keg.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;

public class PutIn {
	private Configuration hconf = null;
	private HTableInterface table = null;
	private List<byte[][]> columnNames = null;
	private List<Put> putList = new ArrayList<Put>(1000);

	public static void main(String[] args) throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException {
		Default.init();
		List<String[]> confList = new ArrayList<String[]>();
		confList.add(new String[] { "hbase.zookeeper.property.clientPort",
				"2181" });
		confList.add(new String[] { "hbase.zookeeper.quorum",
				"10.1.1.122,10.1.1.124,10.1.1.126" });
		confList.add(new String[] { "hbase.master", "10.1.1.121:60000" });

		HAdmin had = new HAdmin(confList);

		// if (had.tableExists("ttt")) {
		// had.deleteTable("ttt");
		// had.createTable("ttt", new String[] { "cf" });
		// } else {
		had.createTable("ttt", new String[] { "cf" });

		// }

		had.close();
		String columns[] = { "cf:a", "cf:b" };
		PutIn pi = new PutIn(confList, "ttt", columns);
		pi.put("ss", new String[] { "a", "b" });
		pi.close();
	}

	/*
	 * 
	 * ("hbase.zookeeper.property.clientPort", "2181")
	 * ("hbase.zookeeper.quorum", "10.1.1.122,10.1.1.124,10.1.1.126")
	 * ("hbase.master", "10.1.1.121:60000") val hbaseConf =
	 * HBaseConfiguration.create()
	 */

	// columns:[cf:a,cf:b,..]
	public PutIn(List<String[]> confList, String tableName, String[] columns)
			throws MasterNotRunningException, ZooKeeperConnectionException,
			IOException {
		hconf = HBaseConfiguration.create();

		for (String[] str : confList) {
			hconf.set(str[0], str[1]);
		}

		HConnection connection = HConnectionManager.createConnection(hconf);
		table = connection.getTable(tableName);

		table.setAutoFlush(false, true);
		table.setWriteBufferSize(5 * 1024 * 1024);

		columnNames = new ArrayList<byte[][]>();
		// 设置columnFamilys
		for (int i = 0; i < columns.length; i++) {
			String[] f_c = columns[i].split(":");
			// System.out.println(columns[i]);
			byte[][] tuple = { f_c[0].getBytes(), f_c[1].getBytes() };
			columnNames.add(tuple);
		}

	}

	public void close() throws IOException {
		table.put(putList);
		putList.clear();
		table.flushCommits();
		table.close();
	}

	public void put(String rowkey, String[] values) throws IOException {

		if (values.length != columnNames.size()) {
			throw new IllegalArgumentException(rowkey + ":"
					+ "(columnNames.length:" + columnNames.size()
					+ ",values.length:" + values.length);
		}

		byte[][] temp = null;
		Put p = new Put(rowkey.getBytes());

		for (int i = 0; i < columnNames.size(); i++) {
			// 没有值 则不加入
			if (values[i] == null) {
				continue;
			}

			temp = columnNames.get(i);
			p.add(temp[0], temp[1], values[i].getBytes());

		}

		putList.add(p);

		if (putList.size() >= 1000) {
			table.put(putList);
			putList.clear();
		}

	}

}

package edu.thu.keg.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class Test {
	private HBaseAdmin hba = null;

	public Test() throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException {
		List<String[]> confList = new ArrayList<String[]>();
		confList.add(new String[] { "hbase.zookeeper.property.clientPort",
				"2181" });
		confList.add(new String[] { "hbase.zookeeper.quorum",
				"10.1.1.122,10.1.1.124,10.1.1.126" });
		confList.add(new String[] { "hbase.master", "10.1.1.121:60000" });

		Configuration hconf = HBaseConfiguration.create();
		for (String[] strs : confList) {
			hconf.set(strs[0], strs[1]);
		}

		hba = new HBaseAdmin(hconf);

		TableName[] names = hba.listTableNames();

		HTableDescriptor HTDes = hba.getTableDescriptor("TTT".getBytes());

		// HTDes.

		for (TableName tn : names) {
			System.out.println(tn.getNameAsString());
		}

	}

	public static void main(String[] args) throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException {

		new Test();

	}
}

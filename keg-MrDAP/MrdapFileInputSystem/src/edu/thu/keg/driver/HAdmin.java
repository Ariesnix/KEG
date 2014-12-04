package edu.thu.keg.driver;

import java.io.IOException;
import java.util.List;




import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;


//Hbase 表管理
public class HAdmin
{
	private HBaseAdmin hba =null;

	public HAdmin(List<String[]> confList) throws MasterNotRunningException, ZooKeeperConnectionException, IOException
	{
		Configuration hconf=HBaseConfiguration.create();
		for(String[] strs:confList)
		{
			hconf.set(strs[0], strs[1]);
		}
		
		hba=new HBaseAdmin(hconf);
		
		
	}
	
	public boolean tableExists(String tableName) throws IOException
	{
		return hba.isTableAvailable(tableName);
		
	}
	
	public void createTable(String tableName,String[] columnFamilys) throws IOException
	{
		HTableDescriptor tableDes = new HTableDescriptor(TableName.valueOf(tableName));

		for (String cf : columnFamilys)
		{
			tableDes.addFamily(new HColumnDescriptor(cf));
		}
		
		hba.createTable(tableDes);
		
	}
	
	public void deleteTable(String tableName) throws IOException
	{
		hba.disableTable(tableName);
		hba.deleteTable(tableName);
		System.out.println("delete");
		
	}
	
	public void close() throws IOException
	{
		hba.close();
	}

}

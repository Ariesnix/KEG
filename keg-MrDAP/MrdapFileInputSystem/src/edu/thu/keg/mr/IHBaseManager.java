package edu.thu.keg.mr;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.util.ArrayList;
import java.util.List;

public interface IHBaseManager {

	public void init(String inputFolder, String outputFolder, String tableName,
			int serial, String version) throws RuntimeException;

	public void setInputFile(String folder);

	public void startMigration();

	public void killMigration();

	public double getProcess();

	public String getTableName(String type);

	public String getRowKey(String type);

	public String getColumn(String type);

	public int getRows(String type);

	public void inputHbaseMetaData();

	// public void writeLine(String rollkey, String[] content);

}

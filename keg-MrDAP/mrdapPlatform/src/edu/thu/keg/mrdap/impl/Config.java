package edu.thu.keg.mrdap.impl;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Store platform-wide configurations
 * 
 * @author Yuanchao Ma
 * 
 */
public final class Config {
	private static String file = "config.xml";
	public static final String DataSetFile = "DataSetFile";
	public static final String SqlAddress = "SqlAddress";
	public static final String OracleAddress = "OracleAddress";
	public static final String HiveAddress = "HiveAddress";

	private static Properties prop = null;

	public static void init(String file) throws IOException {
		Config.file = file;
		File f = new File(".");
		System.out.println("config " + f.getAbsolutePath());
		InputStream is = new FileInputStream(file);
		prop = new Properties();
		prop.loadFromXML(is);
	}

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

	public static void setProperty(String key, String value) throws IOException {
		prop.setProperty(key, value);
		OutputStream os = new FileOutputStream(file);
		prop.storeToXML(os, null);
	}

	public static String getDataSetFile() {
		return getProperty("DataSetFile");
	}

	public static String getTaskFile() {
		return getProperty("TaskFile");
	}

	public static String getHadoopRoot() {
		return getProperty("HadoopRoot");
	}
}

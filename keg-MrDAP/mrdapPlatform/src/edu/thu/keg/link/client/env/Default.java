package edu.thu.keg.link.client.env;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Default {
	// public static final String MASTER_ADDR="10.1.1.121";
	/*
	 * public static final String HADOOP_HOME="/usr/local/hadoop-2.2.0"; public
	 * static final String SPARK_HOME="/usr/local/spark-1.0.1"; public static
	 * final String DATA_LOCATION="/Mobile"; public static final String
	 * HDFS_URL="hdfs://10.1.1.121:9900"; public static final String
	 * APPLICATION_INFO_URL="http://10.1.1.121:8088";
	 */

	private static Map<String, String> conf = null;
	private static final Default def = new Default();

	public static String getValue(String key) {
		if (conf == null) {
			initDefault("default.xml");
		}
		return conf.get(key);
	}

	public static void initDefault(String fileName) {

		conf = ReadXML.getConf(fileName);

		if (conf.get("HADOOP_HOME") == null) {
			// load System env_setting by default
			conf.put("HADOOP_HOME", System.getenv("HADOOP_HOME"));
		}

		if (conf.get("SPARK_HOME") == null) {
			conf.put("SPARK_HOME", System.getenv("SPARK_HOME"));
		}

		// if the executable file doesn't exist,then build it on current
		// directory

		if ((conf.get("HADOOP_HOME") == null || !new File(
				conf.get("HADOOP_HOME")).exists())

				&& System.getProperties().get("hadoop.home.dir") == null) {

			System.out.println("create new executable files : ./bin/xx");

			File file = new File(".");
			System.getProperties().put("hadoop.home.dir",
					file.getAbsolutePath());

			File newbin = new File(file, "bin");
			if (!newbin.exists()) {
				newbin.mkdirs();

			}

			// System.out.println(newbin.getAbsoluteFile());

			File f = new File(newbin, "winutils.exe");
			if (!f.exists()) {

				try {
					new File(newbin, "winutils.exe").createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.getProperties().put("hadoop.home.dir",
						file.getAbsolutePath());

			}
		}
	}

}

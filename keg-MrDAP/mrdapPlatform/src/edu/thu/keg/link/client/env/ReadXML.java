package edu.thu.keg.link.client.env;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ReadXML {

	public static Map<String, String> getServerConf() {
		String location = "/home/" + System.getProperty("user.name")
				+ "/.spark";
		Map<String, String> map = getConf(location + "/conf/conf.xml");
		map.put("SPAKR_APPLICATION_INFO_LOCATION", location);

		for (Entry<String, String> entry : map.entrySet()) {
			System.out.println(entry);
		}

		return map;

	}

	public static Map<String, String> getConf(String inputXml) {
		Map<String, String> confM = null;
		try {
			SAXReader saxReader = new SAXReader();
			File file = new File(inputXml);
			Document document = saxReader.read(file);

			System.out.println("default_file:" + file.exists());

			List list = document.selectNodes("//Configurations/Configuration");
			Iterator iter = list.iterator();

			confM = new HashMap<String, String>();

			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				confM.put(element.elementText("name"),
						element.elementText("value"));

				System.out.println(element.elementText("name") + ":"
						+ element.elementText("value"));

			}

		}

		catch (DocumentException e) {
			e.printStackTrace();

		}

		return confM;

	}

	public static void main(String[] argv) {
		// getConf(new File("default.xml"));
		System.getProperties().getProperty("os.name");
	}
}

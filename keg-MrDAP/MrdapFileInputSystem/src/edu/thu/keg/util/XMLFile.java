package edu.thu.keg.util;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.io.File;
import java.net.MalformedURLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public interface XMLFile {
	public Document read(File fileName) throws MalformedURLException,
			DocumentException;

	public Element getRootElement(Document doc);
}

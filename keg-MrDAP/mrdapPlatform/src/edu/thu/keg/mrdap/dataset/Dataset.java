package edu.thu.keg.mrdap.dataset;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import java.util.Date;

public interface Dataset {
	public String getId();

	public String getArgs();

	public int getRowCount();

	public Date getDate();

	public String getSerial();

	public String getName();

	public String getType();

	public String getPath();

	public long getSizeMb();

}

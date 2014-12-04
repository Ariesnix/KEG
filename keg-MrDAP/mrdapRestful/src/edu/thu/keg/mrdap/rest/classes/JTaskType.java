package edu.thu.keg.mrdap.rest.classes;

/**
 * @author Yuan Bohi
 * @com keg205.thu.edu
 * @date 2014-11-14
 * 
 */
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.common.taskType.TaskType;
import edu.thu.keg.link.common.taskType.TaskTypeField;

@XmlRootElement
public class JTaskType {
	private String id = "";
	private String name = "";
	private String inputMeta = "";
	private String outputMeta = "";
	private String description = "";
	private String args = "";

	public JTaskType() {

	}

	public JTaskType(TaskType taskType) {
		try {
			this.id = taskType.getValue(TaskTypeField.ID);
			this.name = taskType.getValue(TaskTypeField.NAME);
			this.inputMeta = taskType.getValue(TaskTypeField.INPUTMETA);
			this.outputMeta = taskType.getValue(TaskTypeField.OUTPUT_META);
			this.description = taskType.getValue(TaskTypeField.DESCRIPTION);
			this.args = taskType.getArgs().toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public JTaskType(String id, String name, String outputMeta,
			String description, String args) {

		this.id = id;
		this.name = name;
		// this.inputMeta = inputMeta;
		this.outputMeta = outputMeta;
		this.description = description;
		this.args = args;

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the inputMeta
	 */
	public String getInputMeta() {
		return inputMeta;
	}

	/**
	 * @param inputMeta
	 *            the inputMeta to set
	 */
	public void setInputMeta(String inputMeta) {
		this.inputMeta = inputMeta;
	}

	/**
	 * @return the outputMeta
	 */
	public String getOutputMeta() {
		return outputMeta;
	}

	/**
	 * @param outputMeta
	 *            the outputMeta to set
	 */
	public void setOutputMeta(String outputMeta) {
		this.outputMeta = outputMeta;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the args
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(String args) {
		this.args = args;
	}

}

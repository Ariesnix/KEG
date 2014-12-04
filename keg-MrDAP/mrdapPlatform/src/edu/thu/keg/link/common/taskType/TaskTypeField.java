package edu.thu.keg.link.common.taskType;

public enum TaskTypeField
{
	
	/*
	 <?xml version="1.0" encoding="UTF-8"?>

<task>
	<basic>
		<id>201408181321</id>
		<name>Task3</name>
		<jarName>Task3.jar</jarName>
		<className>mobile.Task3</className>
		<argsNum>0</argsNum>
		<extraLib>hbaseLib,jsonLib</extraLib>
		<inputMeta>EnodeBId CellId startTime endTime MR.LteScRSRP MR.LteNcRSRP</inputMeta>
		<outputNum>1</outputNum>
		<outputMeta>1: EnodeBId, CellId, Time, 总采样点数, 邻区数大于等于3的样本点数,
			是否为高重叠覆盖度小区</outputMeta>
		<description>重叠覆盖度分析$$输入字段: EnodeBId, CellId, startTime, endTime,
			MR.LteScRSRP, MR.LteNcRSRP$$输出字段: EnodeBId, CellId, Time, 总采样点数,
			邻区数大于等于3的样本点数, 是否为高重叠覆盖度小区$$描述: 根据 LTE 服务小区的参考信号接收功率 RSRP 及 LTE
			已定义邻区关系和未定义邻区关系小区的参考信号接收功率 RSRP 计算小区重叠覆盖指数,
			该指数用于描述某小区覆盖范围内强信号邻区叠加的程度.</description>
	</basic>

	<args>
		<timeSpan>时间范围</timeSpan>
		<keySpan>小区范围</keySpan>
		<num>數量</num>
		<diff>差值</diff>
	</args>

</task>

	 * 
	 * */
	 ID("id"),
	 NAME("name"),
	 JAR_NAME("jarName"),
	 CLASS_NAME("className"),
	 ARGS_NUM("argsNum"),
	 EXTRALIB("extraLib"),
	 INPUTMETA("inputMeta"),
	 OUTPUT_NUM("outputNum"),
     OUTPUT_META("outputMeta"),
	 DESCRIPTION("description");
	// ARGS("args");
	 
	 private String info=null;
	 private TaskTypeField(String info)
	 {
		 this.info=info;
	 }
	 
	
	 @Override
	 public String toString()
	 {
		 return info;
	 }
	 
}

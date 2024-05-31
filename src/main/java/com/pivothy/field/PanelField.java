package com.pivothy.field;

import java.io.Serializable;

/**
 * 
 * 表示区域字段,这个区域可能包括行区域、列区域或值区域。
 * 包含关于如何布局和显示数据的额外信息，例如是否显示小计、是否为树形结构等。
 * @author 石浩炎
 */
public class PanelField implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 525330345049552324L;

	/**
	 * 数据源的字段对象
	 */
	protected DataField dataField;
	
	/** 设置字段别名。用于显示报名标题名称。 */
	protected String fieldAlias;
	
	/**
	 * 通配符
	 * 定义通配符规则
	 * 假设我们定义了以下通配符规则：
	 * #：表示一个数字占位符。
	 * 0：表示如果位数不足时用0填充。
	 * ,：表示千位分隔符。
	 * 
	 */
	// eg:
	// #,##0.00  12345.6789->12,345.68
	protected String pattern;//用于定义数值格式的
	
	public PanelField(DataField dataField) {
		this(dataField,dataField.getFieldName());
	}
	
	public PanelField(DataField dataField,String fieldAlias) {
		this.dataField = dataField;
		this.fieldAlias = fieldAlias;
	}
	/**
	 * 字段可能为空：TotalField对象的字段名称为空。因它没有DataField对象
	 * @return  返回数据字段名称
	 */
	public String getFieldName() {
		return dataField==null?null:dataField.getFieldName();
	}

	public DataField getDataField() {
		return dataField;
	}

	public void setDataField(DataField dataField) {
		this.dataField = dataField;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	public void setFieldAlias(String fieldAlias) {
		this.fieldAlias = fieldAlias;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}	
	
}

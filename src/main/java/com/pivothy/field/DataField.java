package com.pivothy.field;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.pivothy.annotation.FieldAnn;

/**
 * 代表报表中的一个字段,包含字段名称、字段格式、数据类型等属性。
 * @version 1.0
 * @author shyzq
 */
public class DataField implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String FIELD_TYPE_STR="String";
	public static String FIELD_TYPE_INT="Integer";
	public static String FIELD_TYPE_LONG="Long";
	public static String FIELD_TYPE_DOUBLE="Double";
	public static String FIELD_TYPE_BIGDECIMAL="BigDecemal";
	public static String FIELD_TYPE_DATE="Date";
	/** 数据源字段 */
	private String fieldName;
	/**
	 * 数据源字段类型
	 */
	private String feildType = FIELD_TYPE_STR;
	
	/**
	 * 实体类字段对象
	 */
	private Field field;
	/**
	 * 实体类字段注解
	 */
	private FieldAnn fieldAnn;
	
	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public FieldAnn getFieldAnn() {
		return fieldAnn;
	}

	public void setFieldAnn(FieldAnn fieldAnn) {
		this.fieldAnn = fieldAnn;
	}
	
	public DataField() {
		
	}

	public DataField(String fieldName){
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	

	public String getFeildType() {
		return feildType;
	}

	public void setFeildType(String feildType) {
		this.feildType = feildType;
	}
	
}

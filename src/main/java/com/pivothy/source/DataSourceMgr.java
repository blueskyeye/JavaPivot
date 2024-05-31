package com.pivothy.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pivothy.exception.ReportException;
import com.pivothy.field.DataField;
import com.pivothy.service.FieldMapper;

/**
 * 
 * @version 1.0
 * @author shihy
 */
public class DataSourceMgr<T> {
	/**
	 * 源数据集
	 */
	private List<T> dataSource;
	
	private FieldMapper fieldMapper;
	/**
	 *  源数据集字段列表
	 */
	private List<DataField> fieldList;
	
	private Map<String,DataField> fieldMap;
	
	public DataSourceMgr(List<T> dataSource) {
		this(dataSource, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataSourceMgr(List<T> dataSource,Class<T> clazz) {
		this.dataSource = dataSource;		
		if(clazz==null) {
			this.fieldMapper = new MapFieldMapper(this);
		}else {
			this.fieldMapper = new EntityFieldMapper(this,clazz);
		}
        if (dataSource == null || dataSource.isEmpty())
			return;
        T vo = (T) dataSource.get(0);
		fieldList = fieldMapper.mapFields(vo);
		this.fieldMap = new HashMap<String,DataField>();
		if(fieldList!=null && fieldList.size()>0) {			
        	for(DataField field:this.fieldList) {
        		fieldMap.put(field.getFieldName(), field);
        	}
        }
	}

	public List<T> getDataSource() {
		return dataSource;
	}

	public List<DataField> getFieldList() {
		return fieldList;
	}

	public Map<String, DataField> getFieldMap() {
		return fieldMap;
	}

	public FieldMapper getFieldMapper() {
		return fieldMapper;
	}
	
	public DataField getDataField(String fieldName) {
		DataField dataField = this.fieldMap.get(fieldName);
		if(dataField==null) {
			throw new ReportException(fieldName+"字段信息不存在！");
		}
		return dataField;
	}
	
}

package com.pivothy.service;

import java.util.List;

import com.pivothy.data.DataItem;
import com.pivothy.field.DataField;
import com.pivothy.field.PanelField;
import com.pivothy.panel.AxisPanelHandle;

/**
 * 
 * @version 1.0
 * @author shihy
 */
public interface FieldMapper {
	//构建数据源字段列表
	public List<DataField> mapFields(Object vo);
	
	public List<DataItem> mapDataItem(AxisPanelHandle panelHandle,PanelField panelField
			,List dataSource,List<PanelField> valPanelFields);
	
	public List	mapDataSource(List dataSource,String fieldName, String fieldValue);
	
	public List<String> getValuesOfField(List dataSource,String fieldName);
	
}

package com.pivothy.source;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pivothy.annotation.FieldAnn;
import com.pivothy.data.DataItem;
import com.pivothy.field.DataField;
import com.pivothy.field.PanelField;
import com.pivothy.panel.AxisPanelHandle;
import com.pivothy.service.FieldMapper;

public class EntityFieldMapper implements FieldMapper {
	/**
      * 实体对象
     */
    private Class<?> clazz;
    
    private DataSourceMgr<?> dataSourceMgr;
    
	public EntityFieldMapper(DataSourceMgr<?> dataSourceMgr,Class<?> clazz) {
		this.clazz = clazz;
		this.dataSourceMgr = dataSourceMgr;
	}
	
	public EntityFieldMapper(Class<?> clazz) {
    	this.clazz = clazz;
    	
    }
    @Override
	public List<DataField> mapFields(Object data) {
		List<Field> tempFields = new ArrayList<>();
        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        List<DataField> list = new ArrayList<DataField>();
        for (Field field : tempFields)
        {
        	// 单注解
            if (field.isAnnotationPresent(FieldAnn.class))
            {
            	FieldAnn attr = field.getAnnotation(FieldAnn.class);
            	String fieldName = field.getName();
            	DataField dField = new DataField(fieldName);
            	dField.setField(field);
            	dField.setFieldAnn(attr);
            	list.add(dField);
            }
        }
        //list.stream().sorted(Comparator.comparing(objects -> ((DataField)objects).getFieldAnn().sort())).collect(Collectors.toList());
		return list;
	}
	
	
	public DataSourceMgr getDataSourceMgr() {
		return dataSourceMgr;
	}
    
	public void setDataSourceMgr(DataSourceMgr dataSourceMgr) {
		this.dataSourceMgr = dataSourceMgr;
	}

	@Override
	public List<DataItem> mapDataItem(AxisPanelHandle panelHandle, PanelField panelField, List dataSource,
			List<PanelField> valPanelFields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List mapDataSource(List dataSource, String fieldName, String fieldValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getValuesOfField(List dataSource, String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String getValueOfField(T obj, String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

}

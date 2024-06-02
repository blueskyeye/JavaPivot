package com.pivothy.panel;

import java.util.ArrayList;
import java.util.List;

import com.pivothy.Pivot;
import com.pivothy.data.DataCell;
import com.pivothy.field.PanelField;
import com.pivothy.field.TotalField;
import com.pivothy.field.ValueField;

/**
 * 
 * 
 * @author 石浩炎
 */
public abstract class PanelHandleBase {

	protected Pivot<?> privotForge;
	
	/**
	 * 区域字段列表
	 */
	protected List<PanelField> panelFields;
	
	protected List<List<DataCell>> panelCells;//区域单元格列表
	
	
	public PanelHandleBase(Pivot privotForge) {
		this.privotForge = privotForge;
		this.panelFields = new ArrayList<>();
		this.panelCells = new ArrayList<>();
	}
	
	public List<List<DataCell>> getPanelCells() {
		return panelCells;
	}
	
	protected void clearCells() {
		this.panelCells = new ArrayList<>();
	}
	/**
	 * 区域是否空字段。
	 * @return 是否空字段。
	 */
	protected boolean isEmptyOfPanel() {
		return this.panelFields.size()==0;
	}

	/**
	 * 根据字段名称判断数据源字段是否存在。
	 * @param fieldName  数据源字段
	 * @return 判断数据源字段是否存在。
	 */
	public boolean isExist(String fieldName) {
		for(PanelField field:this.panelFields) {
			if (fieldName.equals(field.getFieldName())) {
	            return true; // 找到匹配的fieldName，返回true
	        }
		}
		return false;
	}
	
	/**
	 * 获取指定别名所在区域的位置。
	 * @param panelField 区域字段
	 * @return 获取指定别名所在区域的位置
	 */
	public int getIndexOfField(PanelField panelField) {
		int index=0;
		for(PanelField field:this.panelFields) {
			if(panelField instanceof ValueField) {
				//当前字段为值字段时
				if(TotalField.isTotalField(field)) {
					break;
				}
			}
			if (panelField.getFieldAlias().equals(field.getFieldAlias())) {
	            break;
	        }
			index++;
		}
		return index;
	}
	/**
	 * 根据字段名称获取数据源字段对象
	 * @param fieldName 数据源字段
	 * @return 根据字段名称获取数据源字段对象
	 */
	public PanelField getPanelField(String fieldName) {
		for(PanelField field:this.panelFields) {			
			if (fieldName.equals(field.getFieldName())) {
	            return field; // 找到匹配的fieldName，返回true
	        }
		}
		return null;
	}
	
	public PanelField getValPanelField(String alias) {
		PanelField field = null;
		for(PanelField pfield:this.panelFields) {
			if(pfield.getFieldAlias().equals(alias)) {
				field = pfield;
				break;
			}
		}
		return field;
	}
	
	/**
	 * 
	 * @param panelField 区域字段
	 * @return 是否值区域字段
	 */
	protected boolean isValueField(PanelField panelField) {
		return TotalField.isTotalField(panelField) || panelField instanceof ValueField;
	}
	
	/**
	 * 
	 * @param panelField 区域字段
	 * @return 是否非值区域字段
	 */
	protected boolean isAxisField(PanelField panelField) {
		 return !this.isValueField(panelField);
	}
	
	/**
	 * 增加区域字段
	 * @param field 区域字段
	 */
	public void addPanelField(PanelField field) {
		this.panelFields.add(field);
	}
	
	
	public List<PanelField> getPanelFields() {
		return panelFields;
	}
	/**
	 * 构建当前区域的单元格列表
	 */
	public abstract void buildPanelCells();
	
}

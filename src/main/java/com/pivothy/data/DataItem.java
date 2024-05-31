package com.pivothy.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pivothy.field.DataField;
import com.pivothy.field.Layout;
import com.pivothy.field.PanelField;
import com.pivothy.service.FieldMapper;

/**
 *  数据源数据实体
 * 
 * @author shihy
 */
public class DataItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataField dataField;// 数据源字段对象
	private PanelField panelField;//区域字段对象
	private String format;// 数据显示值
	private String value;// 节点实际值
	/**
	 * 当前数值节点对应的数据集
	 */
	private List<Object> dataSource;
	
	private FieldMapper fieldMapper;
	//子结点信息
	private List<DataItem> sonList;
	
	private TreeDict treeDict;

	public DataItem(DataField field, String value) {
		this.dataField = field;
		this.value = value;
		this.dataSource = new ArrayList<Object>();
	}
	
	public TreeDict getTreeDict() {
		return treeDict;
	}

	public void setTreeDict(TreeDict treeDict) {
		this.treeDict = treeDict;
	}




	public DataCell getCurCell() {
		DataCell cell = new DataCell(panelField,value);
		cell.setDataField(dataField);
		return cell;
	}
	
	public DataCell getEmptyCell() {
		DataCell cell = new DataCell(panelField,"");
		cell.setFormat("");
		cell.setDataField(dataField);
		return cell;
	}
	
	/**
	 * 判断是否叶子节点
	 * @return boolean 
	 */
	public boolean isLeafDataIem(){
		return this.sonList==null || this.sonList.size()==0;
	}
	
	/**
	 * 计算当前结点的最大结点数
	 * @return int 最大结点数
	 */	
	public int getMaxCol() {
		int maxCol = 0;
		if(this.isLeafDataIem()) {
			maxCol++;
		}else {
			for(DataItem sonItem:this.sonList) {
				maxCol+=sonItem.getMaxCol();
			}
		}		
		return maxCol;
	}
	
	/**
	 * 增加一行数据源
	 * @param row 一行数据源
	 */
	public void addDataRow(Object row) {
		this.dataSource.add(row);
	}
	
	/**
	 * 增加子节点
	 * @param sonNode 子节点对象
	 */
	public void addSonDataItem(DataItem sonNode){
		if (sonList == null) {
			sonList = new ArrayList<DataItem>();
		}
		sonList.add(sonNode);
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the sonList
	 */
	@SuppressWarnings("rawtypes")
	public List<DataItem> getSonList() {
		return sonList;
	}

	/**
	 * @param sonList
	 *            the sonList to set
	 */
	public void setSonList(List<DataItem> sonList) {
		this.sonList = sonList;
	}

	public String getFieldName() {
		return this.dataField.getFieldName();
	}

	public DataField getDataField() {
		return dataField;
	}

	public void setDataField(DataField dataField) {
		this.dataField = dataField;
	}

	public PanelField getPanelField() {
		return panelField;
	}

	public void setPanelField(PanelField panelField) {
		this.panelField = panelField;
	}

	/**
	 * 返回当前结点的数据源
	 * @return list
	 */
	public List<Object> getDataSource() {
		return dataSource;
	}

	public void setDataSource(List<Object> dataSource) {
		this.dataSource = dataSource;
	}

	public FieldMapper getFieldMapper() {
		return fieldMapper;
	}

	public void setFieldMapper(FieldMapper fieldMapper) {
		this.fieldMapper = fieldMapper;
	}
	
	
}

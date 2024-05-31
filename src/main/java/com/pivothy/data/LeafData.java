package com.pivothy.data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 
 * @author 石浩炎
 */
public class LeafData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DataCell dataCell;
	
	/**
	 * 当前数值节点对应的数据集
	 */
	private List dataSource;
	
	
	public LeafData(DataCell dataCell) {
		this.dataCell = dataCell;
	}


	public DataCell getDataCell() {
		return dataCell;
	}


	public void setDataCell(DataCell dataCell) {
		this.dataCell = dataCell;
	}


	public List getDataSource() {
		return dataSource;
	}


	public void setDataSource(List dataSource) {
		this.dataSource = dataSource;
	}
	
}

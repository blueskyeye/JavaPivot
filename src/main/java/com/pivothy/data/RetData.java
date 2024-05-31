package com.pivothy.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author 石浩炎
 */
public class RetData extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 记录表格的最大行和最大列
	 */
	private int maxRow=0;
	private int maxCol=0;
	/**
	 * 单元化数据
	 */
	private List<List<Map<String,Object>>> retCells;
	/**
	 * 表格化数据
	 */
	private List<List<String>> retDatas;
	
	

}

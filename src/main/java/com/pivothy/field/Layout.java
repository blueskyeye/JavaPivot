package com.pivothy.field;

/**
 * 
 * 
 * @author 石浩炎
 */
public enum Layout {
	TABLE("表格"),
	TREE("压缩");//大纲格式
	private String desc;
	private Layout(String desc){
		this.desc = desc;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
}

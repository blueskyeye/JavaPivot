package com.pivothy.data;

/**
 * 
 * 
 * @author 石浩炎
 */
public enum DataType {
	NORMAL("普通"), SUBTOTAL("汇总"), TOTAL("总计"),MUTIL("数值"),TITLE("标题");
	private String desc;

	private DataType(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
}

package com.pivothy.field;

/**
 * 
 * 
 * @author 石浩炎
 */
public enum Subtotal {
	DEFAULT("自动"),
	NOTHING("无"),
	DEFINDE("自定义");//存在多个时，小计始终放在下面,情况不多。
	private String desc;
	private Subtotal(String desc){
		this.desc = desc;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
}

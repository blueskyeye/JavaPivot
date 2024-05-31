package com.pivothy.field;

/**
 * 
 * 
 * @author 石浩炎
 */
public class TotalField extends PanelField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2569232449407082974L;


	public TotalField(DataField dataField) {
		super(null,"∑数值");
	}
	
	public TotalField() {
		super(null,"∑数值");
	}
	
	
	/**
	 * 判断是否多值字段
	 * @param field 区域字段
	 * @return 是否多值虚拟字段
	 */
	public static boolean isTotalField(PanelField field) {
		return field instanceof TotalField;
	}

}

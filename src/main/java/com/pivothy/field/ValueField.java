package com.pivothy.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author 石浩炎
 */
public class ValueField extends PanelField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**VALUE区域字段的计算类型**/
	protected Calculation calculation;
	
	/**
	 * 当前字段是否公式
	 */
	private boolean isFormula = false;
	
	private static String FORMULA_REG = "\\(|\\)|\\+|-|\\*|/|%";
	
	private String formula;
	
	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public boolean isFormula() {
		return isFormula;
	}

	public void setFormula(boolean isFormula) {
		this.isFormula = isFormula;
	}

	public ValueField(DataField dataField) {
		super(dataField);
		this.calculation = Calculation.SUM;
	}
	
	public ValueField(DataField dataField,String alias) {
		super(dataField,alias);
		this.calculation = Calculation.SUM;
	}
	
	public Calculation getCalculation() {
		return calculation;
	}

	public void setCalculation(Calculation calculation) {
		this.calculation = calculation;
	}
	
	public List<String> getValFields(Map<String,DataField> fields){
		List<String> list = new ArrayList<String>();
		String[] fieldNameList = this.formula.split(FORMULA_REG);//拆分出当前公式包含多少个字段。		
		for(String fieldName:fieldNameList) {
			DataField field = fields.get(fieldName);
			if(field!=null) {
				list.add(fieldName);
			}
		}
		return list;
	}

}

package com.pivothy.field;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * 
 * @author 石浩炎
 */
public enum Calculation {
	SUM("求和"),
	CNT("计数"),
	AVG("平均值"),
	MAX("最大值"),
	MIN("最小值"),
	STR("字符串");
	
	private String desc;
	
	private Calculation(String desc){
		this.desc = desc;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	
	public String getDescInfo(){
		return desc+"项:";
	}
	
	
	public static String calcNum(List<String> valList,Calculation function){
		if(SUM==function){
			return calcSum(valList);
		}else if(CNT==function){
			return calcCnt(valList);
		}else if(AVG==function){
			return calcAvg(valList);
		}else if(MAX==function){
			return calcMax(valList);
		}else if(MIN==function){
			return calcMin(valList);
		}else if(STR==function){
			return calcStr(valList);
		}
		return null;
	}
	/**
	 * @param valList
	 * @return
	 */
	private static BigDecimal sum(List<String> valList) {
		BigDecimal svalue = new BigDecimal("0");
		for(String value:valList){			
			try {
				svalue = svalue.add(new BigDecimal(value));				
			} catch (Exception e) {
				return null;
			}
		}
		return svalue;
	}
	
	public static String calcSum(List<String> valList){
		String strValue="";
		BigDecimal svalue = sum(valList);
		if(svalue!=null){
			strValue = String.valueOf(svalue.doubleValue());
		}
		return strValue;
	}
		
	public static String calcCnt(List<String> valList){
		String strValue=String.valueOf(valList.size());		
		return strValue;
	}
	
	public static String calcAvg(List<String> valList){
		String strValue="";
		BigDecimal svalue = sum(valList);
		if(svalue!=null){
			try {
				svalue = svalue.divide(new BigDecimal(valList.size()));
			} catch (Exception e) {
				svalue = null;
			}
		}
		if(svalue!=null){
			strValue = String.valueOf(svalue.doubleValue());
		}
		return strValue;
	}
	
	
	
	public static String calcMax(List<String> valList){
		String strValue="";
		BigDecimal svalue = new BigDecimal("0");
		for(String value:valList){			
			try {
				if(svalue.compareTo(new BigDecimal(value))<0){
					svalue = new BigDecimal(value);
				}
			} catch (Exception e) {
				return "0";
			}
		}
		if(svalue!=null){
			strValue = String.valueOf(svalue.doubleValue());
		}
		return strValue;
	}
	
	
	public static String calcMin(List<String> valList){
		String strValue="";
		BigDecimal svalue = new BigDecimal("0");
		for(String value:valList){			
			try {
				if(svalue.compareTo(new BigDecimal(value))>0){
					svalue = new BigDecimal(value);
				}
			} catch (Exception e) {
				return "0";
			}
		}
		if(svalue!=null){
			strValue = String.valueOf(svalue.doubleValue());
		}
		return strValue;
	}
	
	public static String calcStr(List<String> valList){
		String strValue="";		
		for(String value:valList){			
			try {
				strValue+=value;
			} catch (Exception e) {
				return "";
			}
		}
		return strValue;
	}
	
}

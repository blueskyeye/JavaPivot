package com.pivothy;

import java.util.List;
import java.util.Map;

import com.pivothy.report.tool.StrUtil;

public class TestTool {

	protected void printListMap(List<List<Map<String,Object>>> list) {
		for(List<Map<String,Object>> row:list) {
			for(Map<String,Object> cell:row) {
				String format = StrUtil.getStrValue(cell, "format");
	    		getCellDis(cell,format);
	    	}
	    	System.out.println();
	    }
	}

	protected void getCellDis(Map<String, Object> cell, String format) {		
		format+="("+StrUtil.getInt(cell, "posX")+","+StrUtil.getInt(cell, "posY")+")";
		format+="("+StrUtil.getInt(cell, "spanrow")+","+StrUtil.getInt(cell, "spancol")+")";
		System.out.print(format+" ");
	}

	protected void printFormat(List<List<Map<String,Object>>> list) {
		int maxRows = list.size();//最大行
		int maxCols = 0;//最大列
		if(maxRows>0) {
			List<Map<String,Object>> row = (List<Map<String,Object>>)list.get(0);
			if(row!=null &&row.size()>0) {
				Map<String,Object> cell = row.get(row.size()-1);//最后一个单元
				int spancol = StrUtil.getInt(cell, "spancol");
				maxCols = StrUtil.getInt(cell, "posX")+spancol;
			}
		}   	   	
		for(int rowIndex=0;rowIndex<maxRows;rowIndex++) {
			List<Map<String,Object>> row = list.get(rowIndex);
			int colIndex=0;
			int cellIndex=0;
			for(;colIndex<maxCols;colIndex++) {
				Map<String,Object> cell = null;
				int posX=0;
				int posY=0;
				if(cellIndex<row.size()) {
					cell = row.get(cellIndex);
					posX = StrUtil.getInt(cell, "posX");
					posY = StrUtil.getInt(cell, "posY");
				}
				if(cell!=null && rowIndex==posY && colIndex==posX) {//坐标对上
					String format = StrUtil.getStrValue(cell, "format");
					getCellDis(cell,format);
					cellIndex++;
				}else {
					String format = "空";
					format+="("+colIndex+","+rowIndex+")";
					format+="(1,1)";
					System.out.print(format+" ");
				}
				
			}
			System.out.println();
		}
	}

	public void printTable(List<List<String>> datas) {
		for(List<String> row:datas) {
			for(String cell:row) {
				System.out.format("%-10s", cell);
			}
			System.out.println();
		}
	}

}

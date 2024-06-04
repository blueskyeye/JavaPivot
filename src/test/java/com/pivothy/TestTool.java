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
	
	/**
	 *
	    表格样式如下
	    @charset "utf-8";
		.tabtop13 {
			margin-top: 13px;
		}
		.tabtop13 td{
			background-color:#ffffff;
			height:25px;
			line-height:150%;
		}
		.font-center{ text-align:center}
		.btbg{background:#e9faff !important;}
		
		.titfont {
			
			font-family: 微软雅黑;
			font-size: 16px;
			font-weight: bold;
			color: #255e95;
			background: url(../images/ico3.gif) no-repeat 15px center;
			background-color:#e9faff;
		}
	 * @param datas
	 */
	public void mapHtml(List<List<Map<String,Object>>> list) {
		StringBuilder build = new StringBuilder();
		build.append("<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" bgcolor=\"#cccccc\" class=\"tabtop13\" align=\"center\">");
		for(List<Map<String,Object>> row:list) {
			build.append("<tr>");
			boolean isTitle=false;
			for(Map<String,Object> cell:row) {
				String dataType=StrUtil.getStr(cell, "dataType");
				if(!isTitle && "TITLE".equals(dataType)) {//按第一个字段的类型判断即可
					isTitle = true;
				}
				build.append("<td");
				int spancol = StrUtil.getInt(cell, "spancol");
				if(spancol>1) {
					build.append(" colspan=\""+spancol+"\"");
				}
				int spanrow = StrUtil.getInt(cell, "spanrow");
				if(spanrow>1) {
					build.append(" rowspan=\""+spanrow+"\"");
				}
				if(isTitle) {
					build.append(" class=\"btbg font-center titfont\"");
				}
				build.append(">");
				build.append(StrUtil.getStr(cell, "format"));
				build.append("</td>");
			}
			build.append("</tr>");
			isTitle = false;
		}
		build.append("</table>");
		System.out.println(build.toString());
	}
	
	
	public void tableHtml(List<List<String>> datas) {
		StringBuilder build = new StringBuilder();
		build.append("<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" bgcolor=\"#cccccc\" class=\"tabtop13\" align=\"center\">");
		for(List<String> row:datas) {
			build.append("<tr>");
			for(String cell:row) {
				build.append("<td>");
				build.append(cell);
				build.append("</td>");
			}
			build.append("</tr>");
		}
		build.append("</table>");
		System.out.println(build.toString());
	}

}

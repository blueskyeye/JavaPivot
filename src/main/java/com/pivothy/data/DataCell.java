package com.pivothy.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pivothy.field.Calculation;
import com.pivothy.field.ColAxisField;
import com.pivothy.field.DataField;
import com.pivothy.field.PanelField;
import com.pivothy.field.RowAxisField;
import com.pivothy.report.tool.StrUtil;

/**
 *   表格单元对象。
 * 
 * @author 石浩炎
 */
public class DataCell implements Serializable{
	private static final long serialVersionUID = -2552575280925058028L;
	private int spancol;//跨列数
	private int spanrow;//跨行数.
	private int posX;//X轴坐标.列数所在位置
	private int posY;//Y轴坐标,行数所在位置
	private String prefix;//单元格前缀内容
	private String format;//格式化数据
	private String value;//实际数值
	private DataType dataType;
	private PanelField panelField;//设置当前单元所在区域字段
	private DataField dataField;// 数据源字段对象，用于记录TotalField对象中的valField值。
	private PanelField valPanelField;//设置分类汇总单元对象的值区域字段
	private List<DataCell> rowCells;//同行父单元对象列表
	private List<DataCell> colCells;//同列父单元对象列表
	private Calculation calc;//用于设置当前字段的汇总类型,可以为null,只有当前节点为非叶子节点才设置。
	
	
	public DataCell(PanelField panelField) {
		this(panelField,null);
	}
	
	public DataCell(PanelField panelField,String value) {
		this.panelField = panelField;
		this.value = value;
		this.dataType = DataType.NORMAL;
		this.posX=0;
		this.posY=0;
		this.spancol=1;
		this.spanrow=1;
		this.format = value;
		if(panelField!=null) {
			String pattern = panelField.getPattern();
			if(StrUtil.isNotBlank(pattern) && StrUtil.isNotBlank(value)) {
				this.format = StrUtil.formatNumber(StrUtil.toDouble(value), pattern);
			}
		}
		this.rowCells = new ArrayList<>();
		this.colCells = new ArrayList<>();
	}
	
	
	
	public Calculation getCalc() {
		return calc;
	}

	public void setCalc(Calculation calc) {
		this.calc = calc;
	}

	public DataField getDataField() {
		return dataField;
	}

	public void setDataField(DataField dataField) {
		this.dataField = dataField;
	}

	public void addRowCell(DataCell rowCell) {
		this.rowCells.add(rowCell.getClone());
	}
	
	public PanelField getValPanelField() {
		return valPanelField;
	}

	public void setValPanelField(PanelField valPanelField) {
		this.valPanelField = valPanelField;
	}

	public void addColCell(DataCell colCell) {
		this.colCells.add(colCell.getClone());
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getSpancol() {
		return spancol;
	}

	public void setSpancol(int spancol) {
		this.spancol = spancol;
	}

	public int getSpanrow() {
		return spanrow;
	}

	public void setSpanrow(int spanrow) {
		this.spanrow = spanrow;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public PanelField getPanelField() {
		return panelField;
	}

	public void setPanelField(PanelField panelField) {
		this.panelField = panelField;
	}

	public List<DataCell> getRowCells() {
		return rowCells;
	}

	public void setRowCells(List<DataCell> rowCells) {
		this.rowCells = rowCells;
	}

	public List<DataCell> getColCells() {
		return colCells;
	}

	public void setColCells(List<DataCell> colCells) {
		this.colCells = colCells;
	}
	
	public String getDisplay() {
		if(this.prefix==null) {
			return this.format;
					
		}
		return this.prefix + this.format;
	}
	
	public DataCell getClone() {
		DataCell cell = new DataCell(this.panelField);
		cell.setValue(this.value);		
		cell.setPosX(this.posX);		
		cell.setPosY(this.posY);
		cell.setRowCells(this.rowCells.stream()
				.collect(Collectors.toList()));//所有的Y值要加1
		cell.setColCells(this.colCells.stream()
				.collect(Collectors.toList()));
		cell.setFormat(this.format);
		cell.setDataType(this.dataType);
		cell.setSpancol(this.spancol);
		cell.setSpanrow(this.spanrow);
		cell.setPrefix(this.prefix);
		cell.setCalc(this.calc);
		return cell;
	}
	
	public DataCell getNextClone(DataCell cell) {
		DataCell curCell = this.getClone();
		if(curCell.getPanelField() instanceof ColAxisField) {
			curCell.setPosX(cell.getPosX()+1);
			List<DataCell> cells = curCell.getColCells();
			cells.forEach(dataCell -> dataCell.setPosX(cell.getPosX()+1));
		}
		if(curCell.getPanelField() instanceof RowAxisField) {
			curCell.setPosY(cell.getPosY()+1);			
			List<DataCell> cells = curCell.getRowCells();
			cells.forEach(dataCell -> dataCell.setPosY(cell.getPosY()+1));
		}
		return curCell;
	}
	
	public Map<String,Object> getDataOfMap(){
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("value", this.value);
		dataMap.put("format", this.getDisplay());
		dataMap.put("posX", this.getPosX());
		dataMap.put("posY", this.getPosY());
		dataMap.put("spancol", this.spancol);
		dataMap.put("spanrow", this.spanrow);
		dataMap.put("dataType", this.dataType);
		return dataMap;
	}

	@Override
	public String toString() {
		return "DataCell [spancol=" + spancol + ", spanrow=" + spanrow + ", posX=" + posX + ", posY=" + posY
				+ ", prefix=" + prefix + ", format=" + format + ", value=" + value + ", dataType=" + dataType + "]";
	}
	
}

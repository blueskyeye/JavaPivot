/*
 * Copyright (C)  2024 pivothy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pivothy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pivothy.data.DataCell;
import com.pivothy.data.TreeDict;
import com.pivothy.exception.ReportException;
import com.pivothy.field.AxisField;
import com.pivothy.field.Calculation;
import com.pivothy.field.ColAxisField;
import com.pivothy.field.DataField;
import com.pivothy.field.Layout;
import com.pivothy.field.PanelField;
import com.pivothy.field.RowAxisField;
import com.pivothy.field.Subtotal;
import com.pivothy.field.ValueField;
import com.pivothy.panel.ColPanelHandle;
import com.pivothy.panel.RowPanelHandle;
import com.pivothy.panel.ValPanelHandle;
import com.pivothy.report.tool.StrUtil;
import com.pivothy.service.FieldMapper;
import com.pivothy.source.DataSourceMgr;

/**
 * 透视表主类
 * @author 石浩炎
 */
public class Pivot<T> {
	
	private DataSourceMgr<T> dataSourceMgr;
	/**
	 * 区域处理对象
	 */
	private RowPanelHandle rowPanel;
	private ColPanelHandle colPanel;
	private ValPanelHandle valPanel;
	
	/**
	 * 用于存储行列（坐标）区域的字段对象，用于后续方面获取相应的字段属性。
	 */
	private Map<String,AxisField> axisPanelFields;
	
	//多值字段所在区域：
	private String panelOfTotalField;//
	
	public final static String PANEL_ROW="row";
	public final static String PANEL_COL="col";
	
	private List<List<DataCell>> retCells;//最终单元
	
	private int maxRows=0;//最大行
	private int maxCols=0;//最大列
	
	private boolean isExec=false;//是否执行

	public Pivot(List<Map<String,Object>> dataSource) {
		this.dataSourceMgr = new DataSourceMgr(dataSource);
		init();
	}
	
	
	public Pivot(DataSourceMgr<T> dataSource) {
		this.dataSourceMgr = dataSource;
		init();
	}


	private void init() {
		rowPanel = new RowPanelHandle(this);
        colPanel = new ColPanelHandle(this);
        valPanel = new ValPanelHandle(this);
        this.axisPanelFields = new HashMap<String,AxisField>();
	}
	
	
	
	/**
	 * 构建单元列表
	 * @return 构建结果
	 */
	public Map<String,Object> exec(){
		//1.构建行区域数据结点-》树结构
		this.rowPanel.buildDataItem();
		//2.构建列区域数据结点-》树结构（与行区域数据结点构建不存在依赖关系）
		this.colPanel.buildDataItem();
		//3.构建行区域标题单元列表。
		this.rowPanel.buildHeadCells();
		//4.构建行区域数值单元列表(与行区域标题单元构建存在依赖关系)
		this.rowPanel.buildPanelCells();
		//5.构建列区域数据单元列表(同行区域构建不存在依赖关系)
		this.colPanel.buildPanelCells();		
		//6.构建值区域数据单元列表
		this.valPanel.buildPanelCells();
		//7.构建整个透视表的单元列表。
		this.buildPrivotCells();
		this.isExec=true;
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("rowDatas", this.rowPanel.getPanelCells());//行区域列表
		result.put("colDatas", this.colPanel.getPanelCells());//列区域列表
		result.put("valDatas", this.valPanel.getPanelCells());//值区域列表
		result.put("retDatas", this.retCells);//透视列表
		return result;
	}	
	/**
	 * 行最大值
	 * @return 行最大值
	 */
	public int getMaxRows() {
		if(!this.isExec) {
			throw new ReportException("请先执行运算");
		}
		List<List<DataCell>> colCells = this.colPanel.getPanelCells();
		int titleRowNum = colCells.size();//获取标题行数及后续起始行值。
		int yOffset = titleRowNum>1?(titleRowNum-1):0;
		List<List<DataCell>> rowCells = this.rowPanel.getPanelCells();
		int rows = rowCells.size();		
		if(rows>0) {
			//最后一行的第一个单元的Y轴+跨行+标题行的偏移量
			List<DataCell> row = rowCells.get(rows-1);//最后一行
			if(row.size()>0) {
				DataCell cell = row.get(0);//第一个单元
				this.maxRows = cell.getPosY()+cell.getSpanrow()+yOffset;
			}
		}else {
			//行区域没有数据，则使用列区域行数+值区域行数
			List<List<DataCell>> valCells = this.valPanel.getPanelCells();
			this.maxRows = titleRowNum + valCells.size();
		}
		return this.maxRows;
	}
	/**
	 * 最大列
	 * @return 列最大值
	 */
	public int getMaxCol() {
		if(!this.isExec) {
			throw new ReportException("请先执行运算");
		}
		List<List<DataCell>> rowCells = this.rowPanel.getPanelCells();
		if(rowCells.size()>0) {
			List<DataCell> row = rowCells.get(0);
			this.maxCols +=row.size();//增加行区域的偏移量
		}
		List<List<DataCell>> colCells = this.colPanel.getPanelCells();
		if(colCells.size()>0) {
			List<DataCell> row = colCells.get(0);
			if(row.size()>0) {
				DataCell cell = row.get(row.size()-1);
				this.maxCols += cell.getPosX() + cell.getSpancol();
			}
		}
		return this.maxCols;
	}
	
	//构建透视表单元格
	private void buildPrivotCells() {
		List<List<DataCell>> rowCells = this.rowPanel.getPanelCells();
		List<List<DataCell>> colCells = this.colPanel.getPanelCells();
		List<List<DataCell>> valCells = this.valPanel.getPanelCells();
		//重新初始化
		this.retCells = new ArrayList<>();
		int rowStart = colCells.size();//获取标题行数及后续起始行值。
		int yOffset = rowStart>1?(rowStart-1):0;
		//计算最大行
		//int maxRows = this.getMaxRows();
		if(rowCells.size()>0) {
			int rowIndex=0;//行数：rowCells的行数			
			for(List<DataCell> row:rowCells) {				
				List<DataCell> dataRow = new ArrayList<>();
				for(DataCell cell:row) {
					DataCell cCell = cell.getClone();
					int posY = cCell.getPosY();
					int posX = cCell.getPosX();
					if(rowStart>1) {
						//如果列区域存在多行，则要重新计算行区域的Y轴
						if(posY==0) {
							cCell.setSpanrow(rowStart);//设置标题行的跨行数据。
						}else {
							//非首行（数据行），则设置数据行的Y轴位置
							posY = posY+yOffset;//下面计算的要以此为准。
							cCell.setPosY(posY);
						}
					}
					if(posX==0) {//只处理第一行第一个节点						
						//处理上一行结点存在跨行的情况
						//只需要处理每行第一个单元，后续节点存在跨行的不影响
						//相当于Y值>当前行时需要将空行补上。
						for(int i=0;i<posY-rowIndex;i++) {//增加空行。上面行存在跨行的情况
							this.retCells.add(new ArrayList<DataCell>());
						}
					}
					dataRow.add(cCell);
				}				
				this.retCells.add(dataRow);
				rowIndex++;
			}
		}
		int colStart = offsetOfCol();//获取列区域单元的起始X位置
		if(colCells.size()>0) {			
			int rowIndex=0;
			for(List<DataCell> row:colCells) {	
				List<DataCell> dataRow = this.getRowList(rowIndex);
				for(DataCell cell:row) {
					DataCell cCell = cell.getClone();
					cCell.setPosX(colStart+cCell.getPosX());
					dataRow.add(cCell);
				}
				rowIndex++;
			}
		}		
		if(valCells.size()>0) {
			int rowIndex = rowStart;
			for(List<DataCell> row:valCells) {
				List<DataCell> dataRow = getRowList(rowIndex);
				for(DataCell cell:row) {
					DataCell cCell = cell.getClone();
					cCell.setPosX(colStart+cell.getPosX());
					cCell.setPosY(rowStart+cell.getPosY());//修改行坐标值
					dataRow.add(cCell);
				}
				rowIndex++;
			}
		}
	}
	
	/**
	 * 返回单元格数据集合。
	 * @return list 数据集合
	 */
	public List<List<Map<String,Object>>> outOfTableMap(){
		List<List<Map<String,Object>>> tableRows = new ArrayList<List<Map<String,Object>>>();
		if(this.retCells.size()>0) {
			for(List<DataCell> row:this.retCells) {
				List<Map<String,Object>> dataRow = new ArrayList<Map<String,Object>>();
				for(DataCell cell:row) {
					dataRow.add(cell.getDataOfMap());
				}
				tableRows.add(dataRow);
			}
		}
		return tableRows;
	}
	/**
	 * 返回table表格数据内容。
	 * @return 返回table表格数据内容。
	 */
	public List<List<String>> outOfTable(){
		List<List<String>> tableRows = new ArrayList<>();
		if(this.retCells.size()>0) {
			int rowIndex=0;
			int maxCol = this.getMaxCol();
			for(List<DataCell> row:this.retCells) {
				int colIndex=0;
				for(DataCell cell:row) {
					List<String> dataRow = getCurRow(tableRows, rowIndex);
					int posX = cell.getPosX();
					int spancol = cell.getSpancol();
					if(posX>colIndex) {
						//针对父结点存在小计（有跨行）导致posX比当前列索引大.
						//当存在多个父节点小计时，posX
						for(;colIndex<posX;colIndex++) {
							dataRow.add("");//用同样的内容覆盖。
						}
					}
					dataRow.add(cell.getDisplay());
					//处理同行跨列的情况
					for(int i=1;i<spancol;i++) {
						dataRow.add("");//用同样的内容覆盖。
					}					
					colIndex++;
				}
				if(colIndex<maxCol) {
					//非首行标题行需要计算最后的小计和总计
					List<String> dataRow = getCurRow(tableRows, rowIndex);
					for(;colIndex<maxCol;colIndex++) {
						dataRow.add("");
					}
				}
				rowIndex++;
			}
		}
		
		return tableRows;
	}
	/**
	 * 获取当前行。
	 * @param tableRows
	 * @param curRowNum
	 * @return
	 */
	private List<String> getCurRow(List<List<String>> tableRows, int curRowNum) {
		List<String> dataRow = null;
		if(curRowNum<tableRows.size()) {
			dataRow = tableRows.get(curRowNum);
		}else {
			dataRow = new ArrayList<String>();
			tableRows.add(dataRow);//先增加该行
		}
		return dataRow;
	}
	
	/**
	 * 计算行区域的偏移量。
	 * 首行最后单元的X轴+跨列数
	 * @return
	 */
	private int offsetOfCol() {
		int offset=0;
		if(this.retCells.size()>0) {
			List<DataCell> firstRow = this.retCells.get(0);
			if(firstRow.size()>0) {
				DataCell cell = firstRow.get(firstRow.size()-1);
				offset = cell.getPosX() + cell.getSpancol();
			}
		}
		return offset;
	}
	
	private List<DataCell> getRowList(int rowIndex){
		List<DataCell> dataRow = null;
		if(this.retCells.size()>rowIndex) {
			dataRow = this.retCells.get(rowIndex);
		}else {
			dataRow = new ArrayList<>();
			this.retCells.add(dataRow);
		}
		return dataRow;
	}
	/**
	 * 行区域增加一个字段
	 * @param fieldName 数据源字段名称
	 */
	public void addRowField(String fieldName) {
		this.addRowField(fieldName, fieldName,Layout.TABLE);
	}
	/**
	 * 行区域增加一个字段
	 * @param fieldName 数据源字段名称
	 * @param fieldAlias 字段别名
	 */
	public void addRowField(String fieldName,String fieldAlias) {
		this.addRowField(fieldName, fieldAlias,Layout.TABLE);
	}
	/**
	 * 行区域增加一个字段
	 * @param fieldName 数据源字段名称
	 * @param fieldAlias 字段别名
	 * @param layout 字段布局：Layout.TABLE(表格形式-默认),Layout.TREE(大纲形式-树型)
	 */
	public void addRowField(String fieldName,String fieldAlias,Layout layout) {
		DataField dataField = dataSourceMgr.getDataField(fieldName);
		if(!rowPanel.isExist(fieldName)) {
			RowAxisField field = new RowAxisField(dataField);
			field.setFieldAlias(fieldAlias);
			field.setLayout(layout);
			this.rowPanel.addPanelField(field);
			this.axisPanelFields.put(fieldName, field);
			this.calcRowPanelCols();
		}
	}
	/**
	 * 设置行(列)区域字段的分类汇总属性
	 * @param fieldName 数据源字段名称
	 * @param subtotal  设置小计汇总类型，Subtotal.DEFAULT(求和-默认),Subtotal.NOTHING(无汇总)
	 */
	public void setFieldSubtotal(String fieldName,Subtotal subtotal) {
		AxisField field = getAxisField(fieldName);
		field.setSubtotal(subtotal);
	}
	
	/**
	 * 设置行(列)区域字段的分类汇总属性，会自动设置为自定义汇总类型。
	 * @param fieldName 数据源字段名称
	 * @param funs 汇总函数列表(可以一个或多个汇总)
	 */
	public void setDefineSubtotal(String fieldName,List<Calculation> funs) {
		AxisField field = getAxisField(fieldName);
		field.setSubtotal(Subtotal.DEFINDE);
		field.setDefFuns(funs);
	}
	
	/**
	 * 根据数据源字段名称获取行(列)区域的字段对象。
	 * @param fieldName
	 * @return
	 */
	private AxisField getAxisField(String fieldName) {
		AxisField field = this.axisPanelFields.get(fieldName);
		if(field==null) {
			throw new ReportException(fieldName+"字段信息不存在！");
		}
		return field;
	}
	/**
	 * 设置行(列)区域字段的布局属性
	 * @param fieldName 数据源字段名称
	 * @param layout 字段布局：Layout.TABLE(表格形式-默认),Layout.TREE(大纲形式-树型)
	 */
	public void setRowFieldLayout(String fieldName,Layout layout) {
		AxisField field = getAxisField(fieldName);
		field.setLayout(layout);
		setRowPanelCols(field);
	}
	/**
	 * 设置大纲形式下在同一列中显示下一字段的标签
	 * @param fieldName 数据源字段名称 
	 * @param isSingleColoumn 是否在同一列中显示下一个字段的标签。true:同列显示,false:不同列显示
	 */
	public void setLayoutOfSameCol(String fieldName,boolean isSingleColoumn) {
		AxisField field = getAxisField(fieldName);
		field.setLayout(Layout.TREE);
		field.setSingleColoumn(isSingleColoumn);
		setRowPanelCols(field);//布局影响行区域的列数
	}
	/**
	 * 设置大纲形式下在每个组顶端显示分类汇总
	 * @param fieldName 数据源字段名称 
	 * @param isTopSubtotal 是否在每个组顶端显示分类汇总，true:顶端显示,false:底部显示
	 */
	public void setLayoutOfToptotal(String fieldName,boolean isTopSubtotal) {
		AxisField field = getAxisField(fieldName);
		field.setLayout(Layout.TREE);
		field.setTopSubtotal(isTopSubtotal);
	}
	
	/**
	 * 设置行(列)区域字段是否重复显示
	 * @param fieldName 数据源字段名称 
	 * @param isRepeatShow 是否重复显示项目标签 true:重复显示,false:不重复显示
	 */
	public void setRepeatShow(String fieldName,boolean isRepeatShow) {
		AxisField field = getAxisField(fieldName);			
		field.setRepeatShow(isRepeatShow);
	}
	/**
	 * 设置字段字典，用于编码转换并按字典内容展示。
	 * @param fieldName 数据源字段名称 
	 * @param arrays 当前key与value一致时
	 */
	public void setDict(String fieldName,String[] arrays) {		
		LinkedHashMap<String, Object> dictMap = new LinkedHashMap<>();
        for (String str : arrays) {
            dictMap.put(str, str);
        }
        this.setDict(fieldName, dictMap, true);
	}
	
	/**
	 * 设置字段字典，用于编码转换并按字典内容展示。
	 * @param fieldName 数据源字段名称
	 * @param list  字典列表
	 */
	public void setDict(String fieldName,List<String> list) {
		this.setListDict(fieldName, list,true);
	}
	
	// 使用泛型来创建一个通用的方法
    public <T extends Collection<String>> void setListDict(String fieldName, T collection,boolean isShowAll) {
        LinkedHashMap<String, Object> dictMap = new LinkedHashMap<>();
        for (String str : collection) {
            dictMap.put(str, str);
        }
        this.setDict(fieldName, dictMap, isShowAll);
    } 
	/**
	 * 设置字段字典，用于编码转换
	 * @param fieldName 数据源字段名称 
	 * @param dictMap 有序字典列表
	 */
	public void setDict(String fieldName,LinkedHashMap<String,Object> dictMap) {
		this.setDict(fieldName, dictMap,true);
	}
	
	/**
	 * 设置字段字典，用于编码转换并按字典内容展示。
	 * @param fieldName 数据源字段名称 
	 * @param dictMap 有序 字典列表
	 * @param isShowAll 展示全部字典内容，而不是以数据源为准。
	 */
	public void setDict(String fieldName,LinkedHashMap<String,Object> dictMap,boolean isShowAll) {
		AxisField field = getAxisField(fieldName);			
		field.setDictMap(dictMap);
		field.setShowDictData(isShowAll);
	}
	
	
	
	private void setRowPanelCols(AxisField field) {
		if(field.isRowPanel()) {
			this.calcRowPanelCols();//布局影响行区域的列数
		}
	}	
	
	/**
	 * 设置行区域的树结构字段。树节点从第一个字段，根据树层次依次往后映射字段
	 * @param tree 树型字典
	 */
	public void setTreeDictOfRowPanel(List<TreeDict> tree) {
		this.rowPanel.setTreeDicts(tree);
	}
	
	/**
	 * 设置列区域的树结构字段。树节点从第一个字段，根据树层次依次往后映射字段
	 * @param tree 树型字典
	 */
	public void setTreeDictOfColPanel(List<TreeDict> tree) {
		this.colPanel.setTreeDicts(tree);
	}
	
	
	/**
	 * 计算行区域的最终列数
	 */
	private void calcRowPanelCols() {
		rowPanel.setMaxCol();
	}
	
	
	/**
	 * 列区域增加一个字段
	 * @param fieldName 数据源字段
	 */
	public void addColwField(String fieldName) {
		this.addColField(fieldName, fieldName);
	}
	/**
	 * 列区域增加一个字段
	 * @param fieldName  数据源字段名称 
	 * @param fieldAlias 字段别名
	 */
	public void addColField(String fieldName,String fieldAlias) {
		DataField dataField = dataSourceMgr.getDataField(fieldName);
		if(!colPanel.isExist(fieldName)) {
			ColAxisField field = new ColAxisField(dataField);
			field.setFieldAlias(fieldAlias);
			this.colPanel.addPanelField(field);
			this.axisPanelFields.put(fieldName, field);
		}
	}
	/**
	 * 值区域增加一个字段
	 * @param fieldName 数据源字段名称 
	 */
	public void addValField(String fieldName) {
		String fieldAlias = this.valPanel.getFieldAlias(fieldName, Calculation.SUM);
		this.addValField(fieldName,fieldAlias,Calculation.SUM);
	}
	/**
	 * 值区域增加一个字段，并设置字段别名
	 * @param fieldName 数据源字段名称
	 * @param fieldAlias 字段别名
	 */
    public void addValField(String fieldName,String fieldAlias) {
    	this.addValField(fieldName,fieldAlias,Calculation.SUM);
	}
	/**
	 * 值区域增加一个字段,并设置字段汇总类型
	 * @param fieldName 数据源字段名称
	 * @param calc  字段汇总类型 Calculation.SUM(默认) 
	 *  SUM("求和"),
	 *  CNT("计数"),
	 *  AVG("平均值"),
	 *  MAX("最大值"),
	 *  MIN("最小值"),
	 *  STR("字符串");
	 */
    public void addValField(String fieldName,Calculation calc) {
    	String fieldAlias = this.valPanel.getFieldAlias(fieldName, calc);
		this.addValField(fieldName,fieldAlias,calc);
	}
    /**
     * 值区域增加一个字段，设置字段别名,并设置字段汇总类型
     * @param fieldName 数据源字段名称
     * @param fieldAlias 数据源字段别名
     * @param calc  计算规则
     */
	public void addValField(String fieldName,String fieldAlias,Calculation calc) {
		DataField dataField = dataSourceMgr.getDataField(fieldName);
		ValueField valField = new ValueField(dataField);
		valField.setFieldAlias(fieldAlias);//TODO 关于名称重复的问题，因是后端设置先不考虑。
		valField.setCalculation(calc);
		this.valPanel.addPanelField(valField);
		//如果存在多个值的情况,多值字段只需要设置一次即可
		if(this.valPanel.isMutilField() && StrUtil.isBlank(this.panelOfTotalField)) {
			setTotalFieldOfColPanel(true);//默认设置
		}
	}
	
	/**
	 * 值区域增加一个公式，并设置别名和汇总类型
	 * @param formula 公式
	 * @param fieldAlias 数据源字段别名
	 * @param calc 分类汇总
	 */
	public void addValFieldOfFormula(String formula,String fieldAlias,Calculation calc) {
		ValueField valField = new ValueField(null,fieldAlias);
		valField.setFormula(true);
		valField.setFormula(formula);
		valField.setCalculation(calc);
		this.valPanel.addPanelField(valField);
		//如果存在多个值的情况,多值字段只需要设置一次即可
		if(this.valPanel.isMutilField() && StrUtil.isBlank(this.panelOfTotalField)) {
			setTotalFieldOfColPanel(true);//默认设置
		}
	}
	
	/**
	 * 设置多值字段展示所在区域
	 * @param isColPanel true:列区域,false:行区域
	 */
	public void setTotalFieldOfColPanel(boolean isColPanel) {
		if(this.valPanel.isMutilField()) {//存在多值区域设置才有效
			List<PanelField> valFields = this.valPanel.getPanelFields();
			if(isColPanel) {//存在列区域
				this.colPanel.addTotalField(valFields);
				this.rowPanel.removeTotalField();
				this.panelOfTotalField=PANEL_COL;
			}else {//存在行区域
				this.rowPanel.addTotalField(valFields);
				this.colPanel.removeTotalField();
				this.panelOfTotalField=PANEL_ROW;
				this.calcRowPanelCols();
			}
		}else {
			this.panelOfTotalField =null;
		}
	}
	
	

	/**
	 * 设置行区域是否显示总计
	 * @param isShowRowTotal 是否显示行总计
	 */
	public void setShowRowTotal(boolean isShowRowTotal) {
		this.rowPanel.setShowTotal(isShowRowTotal);
	}


	/**
	 * 设置列区域是否显示总计
	 * @param isShowColTotal 是否显示列总计
	 */
	public void setShowColTotal(boolean isShowColTotal) {
		this.colPanel.setShowTotal(isShowColTotal);
	}
	
	/**
	 * 获取数据源所有字段列表。
	 * @return 获取数据源所有字段列表。
	 */
	public Map<String,DataField> getSourceFields(){
		return this.dataSourceMgr.getFieldMap();
	}
	
	/**
	 * 获取源数据集
	 * @return 获取源数据集
	 */
	public List<T> getDataSource(){
		return this.dataSourceMgr.getDataSource();
	}
	/**
	 * 获取字段区域的字段列表。
	 * @return 获取字段区域的字段列表。
	 */
	public List<PanelField> getValPanelFields() {
		return this.valPanel.getPanelFields();
	}
	
	public FieldMapper getFieldMapper(){
		return this.dataSourceMgr.getFieldMapper();
	}
	
	public RowPanelHandle getRowPanel() {
		return rowPanel;
	}
	public ColPanelHandle getColPanel() {
		return colPanel;
	}
	public ValPanelHandle getValPanel() {
		return valPanel;
	}
	public String getPanelOfTotalField() {
		return panelOfTotalField;
	}
	public List<List<DataCell>> getRowPanelCells(){
		return this.rowPanel.getPanelCells();
	}
	
	public List<List<DataCell>> getColPanelCells(){
		return this.colPanel.getPanelCells();
	}
	
}

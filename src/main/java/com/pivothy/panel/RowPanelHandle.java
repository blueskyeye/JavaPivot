package com.pivothy.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pivothy.Pivot;
import com.pivothy.data.DataCell;
import com.pivothy.data.DataItem;
import com.pivothy.data.DataType;
import com.pivothy.field.AxisField;
import com.pivothy.field.Calculation;
import com.pivothy.field.Layout;
import com.pivothy.field.PanelField;
import com.pivothy.field.RowAxisField;
import com.pivothy.field.Subtotal;
import com.pivothy.field.TotalField;
import com.pivothy.report.tool.RConstant;

/**
 * 
 * 
 * @author 石浩炎
 */
public class RowPanelHandle extends AxisPanelHandle {

	/**
	 * 用于记录行区域的列数，该数值会在如下情况更新：
	 * 1.增加行字段
	 * 2.增加多值字段
	 * 3.设置非叶子节点字段汇总.
	 * 4.设置非叶子节点字段布局
	 */
	private int cols=0;
	
	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getCols() {
		return cols;
	}

	public RowPanelHandle(Pivot privotForge) {
		super(privotForge);
	}
	
	
	public void setMaxCol() {
		List<PanelField> panelFields = this.getPanelFields();
		if(panelFields==null || panelFields.isEmpty()) {
			return;
		}
		int cols=panelFields.size();//默认为字段数(包括多值字段)
		//行区域最后一个字段不影响列数
		for (int index = 0; index < panelFields.size() - 1; index++) {
			PanelField panelField = panelFields.get(index);
			if(this.isTreeSameCol(panelField)) {
				cols -=1;
			}			
		}
		this.cols = cols;
	}
	/**
	 * 是否存在行标题的情况。
	 * @return
	 */
	private boolean hasRowTitle() {
		//不存在标题行的情况
		if(this.isEmptyOfPanel()) {
			List<PanelField> valField = this.privotForge.getValPanel().getPanelFields();
			if(valField.size()!=1) {
				//值区域的字段数不为1的情况下，行区域没有标题行。
				return false;
			}
			ColPanelHandle colPanel = this.privotForge.getColPanel();
			List<PanelField> colFields = colPanel.getPanelFields();
			if(colFields.size()==0&&valField.size()==0) {
				return false;
			}
		}
		return true;		
	}
	
	/**
	 * 构建标题行（只有一行，但单元格会存在行跨的情况，根据列区域的字段设置情况判断
	 */
	public void buildHeadCells() {
		if(!this.hasRowTitle()) {
			//没有标题行的情况
			return ;
		}
		if(hasEmptyCell()) {
			//行区域没有字段，列区域有字段，值区域只有一个字段的情况，在行区域有一个空标题的单元格。
			addEmptyTitleCell();
			return;
		}
		//存在行字段时
		int colIndex=0;
		List<DataCell> curRow = new ArrayList<>();
		PanelField preField = null;//前字段		
		//int rows = this.privotForge.getTitleRows();
		List<PanelField> pFields = this.getPanelFields();
		for(PanelField field:pFields) {
			if(preField!=null) {
				//只有在前字段和当前字段都为大纲布局且前字段设置显示为单列时，当前字段不显示标题
				if(!TotalField.isTotalField(preField) && !TotalField.isTotalField(field)) {
					RowAxisField axisField = (RowAxisField)preField;
					if(axisField.isTreeSameCol()) {
						continue;
					}
				}
			}
			String alias = field.getFieldAlias();//字段别名
			if(TotalField.isTotalField(field)) {
				//当前字段是值字段时
				alias = "值";
			}
			DataCell cell = new DataCell(field,alias);
			cell.setPosY(getPosY());//起始行
			cell.setPosX(colIndex++);//起始列(汇总字段不存在合并到一列的情况）			
			//cell.setSpanrow(rows);//跨行数为列区域的字段数，跨列数默认为1
			cell.setDataType(DataType.TITLE);
			curRow.add(cell);
			preField = field;//设置当前字段为前一个字段
		}
		this.panelCells.add(curRow);//增加一行标题行。
	}

	/**
	 * 增加空标题
	 */
	private void addEmptyTitleCell() {
		DataCell headCell = new DataCell(null,"");		
		//headCell.setSpanrow(this.privotForge.getTitleRows());//跨行数
		headCell.setDataType(DataType.TITLE);//标题单元
		List<DataCell> headRow = new ArrayList<>();
		headRow.add(headCell);
		this.panelCells.add(headRow);
	}
	
	/**
	 * 是否存在单个值单元对象
	 * @return 是否存在单个值单元对象
	 */
	private boolean hasEmptyCell() {
		if(this.isEmptyOfPanel()) {
			//行区域不存在字段时的处理。
			ColPanelHandle colPanel = this.privotForge.getColPanel();
			List<PanelField> colFields = colPanel.getPanelFields();
			List<PanelField> valField = this.privotForge.getValPanel().getPanelFields();
			if(valField.size()==1 && colFields.size()>0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isEmptyDatas() {
		return this.rootDatas==null||this.rootDatas.size()==0;
	}
	

	@Override
	public void buildPanelCells() {
		if(this.isEmptyDatas()) {
			//行区域不存在数据结点时的情况处理。
			if(hasEmptyCell()) {
				List<PanelField> valFields = this.privotForge.getValPanelFields();
				PanelField vField = valFields.get(0);
				DataCell valDataCell = new DataCell(null,vField.getFieldAlias());
				valDataCell.setValPanelField(vField);
				valDataCell.setPosY(this.getPosY());
				List<DataCell> headRow = new ArrayList<>();
				headRow.add(valDataCell);
				//this.addLeafData(valDataCell, this.privotForge.getDataSource());
				this.addLeafCell(headRow,this.privotForge.getDataSource());
			}			
			return;
		}
		//处理一级节点
		for(DataItem dataItem:this.rootDatas) {
			String space = this.initSpace();
			buildItemCells(dataItem,null,null,space);
		}
		//处理总计	
		if(hasTotal()) {
			if(this.isHasTotalField()) {
				for(PanelField field:this.valFields) {
					addTotalCell(field,field.getFieldAlias());
				}
			}else {
				addTotalCell(null,"总计");
			}
		}
	}

	private void addTotalCell(PanelField field,String alias) {
		DataCell totalCell = new DataCell(null,alias);
		totalCell.setPosY(this.getPosY());
		totalCell.setSpancol(this.cols);
		totalCell.setValPanelField(field);
		totalCell.setDataType(DataType.TOTAL);
		if(field!=null) {
			totalCell.setFormat(field.getFieldAlias()+"汇总");
		}		
		List<DataCell> totalRow = new ArrayList<>();
		totalRow.add(totalCell);
		this.addLeafCell(totalRow, this.privotForge.getDataSource());
	}
	
	//在处理行区域数据单元时，增加叶子节点，同时进行换行
	private void addLeafCell(List<DataCell> curRow,List dataSource) {
		DataCell leafCell = curRow.get(curRow.size()-1);
		this.addLeafData(leafCell, dataSource);
		this.panelCells.add(curRow);//最后一个节点，则必须换行。
	}
	
	/**
	 * 构建单元格：值(value)，位置(posx,posy),跨行列(spanrow,spancol),格式(format),样式(style)
	 * @param dataItem 当前节点
	 * @param pCell  同行前单元结点
	 * @param curRow 当前行
	 * @param space  缩进空格
	 */
	private int buildItemCells(DataItem dataItem,DataCell pCell,List<DataCell> curRow,String space){
		int spanRow=0;//用于记录前一字段(父结点)的跨行数,不用另外单独获取
		if(curRow==null) {
			curRow = new ArrayList<>();
			spanRow++;//如果前面行是空行（新增行），则增加跨行数
		}	
		PanelField panelField = dataItem.getPanelField();
		//如果是最后一个节点
		//构建当前数据结点的单元对象
		DataCell curCell = dataItem.getCurCell();
		int curPosY = this.getPosY();//起始行
		int curPosX = this.getPosXofItem(dataItem);//起始列
		curCell.setPosY(curPosY);
		curCell.setPosX(curPosX);
		curCell.setPrefix(space);//增加空白前缀
		if(pCell!=null) {			
			//如果存在父对象，添加父对象到当前对象列表
			List<DataCell> pCells = pCell.getRowCells().stream()
					.collect(Collectors.toList());
			curCell.setRowCells(pCells);
		}
		curCell.addRowCell(curCell);				
		//增加当前节点
		curRow.add(curCell);
		
		//1.判断是否最后一个节点
		if(dataItem.isLeafDataIem()) {
			//无论是TotalField还是PanelField处理方式都一致。
			this.addLeafCell(curRow, dataItem.getDataSource());		
		}else {
			/**
			 * 子结点处理根据父结点的处理有三种类型：
			 * 1.表格：父结点与子结点在同行。  分支：父结点设置表格布局。(父结点是否跨行)
			 * 2.树型非同列：子结点另起一行，父结点同行用空白内容填充。 分支：父结点设置树型非同列或父结点为数值字段。
			 * 3.树型同列：父结点与子结点为同列，子结点用空白缩进方式展示，分支：父结点设置树型同列
			 */
			/**
			 * 关于空白前缀的处理：
			 * 1.只有在大纲布局的同列设置时需要增加空白前缀。有多个前缀字段时，只要中间某个字段不是大纲同列设置的，则前缀空格需要清空。
			 */
			//当前节点为非叶子节点，继续处理后续节点
			AxisField axisField = (AxisField)panelField;
			//非叶子节点时设置汇总公式
			curCell.setCalc(axisField.getCellCalc());
			
			if(this.isTreeMutiCol(panelField)) {
				//大纲不同列
				//1.父结点同行后续单元对象（空白对象）
				addEmptyCell(dataItem, curRow,curCell);//增加同行后续空白结点
				//2.子结点单元对象。
				List<DataItem> sonList = dataItem.getSonList();
				//2.1 处理子结点的前缀内容。
				space = this.initSpace();
				for(DataItem sonItem:sonList) {
					//2.2 处理子结点的前缀单元对象（因每个子结点的前缀对象的Y轴不一样，所放在循环内处理。
					List<DataCell> nextRow = getNextRow(curCell);
					//2.3 处理子节点.
					spanRow += buildItemCells(sonItem,curCell,nextRow,space);//数值节点下个字段需要换行.
				}
				//3.处理当前节点的跨行
				if(!this.isRepeatShow(panelField)) {
					curCell.setSpanrow(spanRow);
				}
				
			}else if(this.isTreeSameCol(panelField)) {
				//大纲同列
				//1.父结点同行后续单元对象（空白对象）,原因：当后面字段（非叶子字段）存在非同列的情况，仍会存在空白单元的情况。
				addEmptyCell(dataItem, curRow,curCell);//增加同行后续空白结点
				//2.子结点单元对象。
				List<DataItem> sonList = dataItem.getSonList();
				//2.1 处理子结点的前缀内容。
				space = this.addSpace(space);
				for(DataItem sonItem:sonList) {
					//2.2 处理子结点的前缀单元对象（因每个子结点的前缀对象的Y轴不一样，所放在循环内处理。
					List<DataCell> nextRow = getNextRow(curCell);
					//2.3 处理子节点.
					spanRow += buildItemCells(sonItem,curCell,nextRow,space);//数值节点下个字段需要换行.
				}
				//3.处理当前节点的跨行,因同列跨行始终为1，所不需要处理跨行。
			}else {
				//表格
				//1.不需要处理后续空白单元，如果非直接子结点的空白情况，由后续子结点去处理。
				//addEmptyCell(dataItem, curRow,curCell);//增加同行后续空白结点
				//2.子结点单元对象。
				List<DataItem> sonList = dataItem.getSonList();
				//2.1 处理子结点的前缀内容。
				space = this.initSpace();
				//表格布局，先处理本行，再处理下一行
				int index=0;
				for(DataItem sonItem:sonList) {
					//2.2 不需要处理子节点的前缀单元。因不存在换行处理，因后续子结点与本节点同行。
					List<DataCell> nextRow = curRow;//首行不换行。
					if(index>0) {
						nextRow =getNextRow(curCell);
					}	
					//2.3 处理子节点.
					spanRow += buildItemCells(sonItem,curCell,nextRow,space);//数值节点下个字段需要换行.
					index++;
				}
				//3.处理当前节点的跨行(不存在重复时，需要设置跨行属性)
				if(!this.isRepeatShow(panelField)) {
					curCell.setSpanrow(spanRow);
				}
			}
			//处理字段的分组汇总
			/**
			 * 分组汇总有三种情况：
			 * 1.不作任何处理:
			 *   1.1 分类汇总为无。
			 *   1.2 为大纲且汇总在顶部显示,且不存在多值情况  顶部展示时在行区域不需要增加小计行。
			 *   1.3 多值字段
			 *   1.4 最后一个非多值字段
			 * 2.在底部展示汇总(其他情况)
			 *   2.1 表格布局
			 *   2.2 后续存在多值字段
			 */
			if(this.isShowSubTotal(panelField)) {				
				handleSubtotal(dataItem, pCell, curCell);				
			}			
		}
		return spanRow;
		
	}

	public void handleSubtotal(DataItem dataItem, DataCell pCell, DataCell curCell) {
		PanelField panelField = dataItem.getPanelField();
		AxisField axisField = (AxisField)panelField;
		Subtotal subtotal = axisField.getSubtotal();//分类汇总
		List<Calculation> defFuns = axisField.getDefFuns();//自定义汇总
		List<PanelField> vFields = this.getValFields();//值区域的列表				
		//subtotal只有默认和自定义两种
		if(Subtotal.DEFAULT==subtotal) {
			defFuns = new ArrayList<>();
			defFuns.add(Calculation.SUM);
		}
		String space = this.initSpace();
		for(Calculation fun:defFuns) {					
			if(this.includeTotalField(panelField)) {
				//需要当前区域存在多值字段时,且在当前字段后面的情况下汇总才需要考虑
				for(PanelField vField:vFields) {							
					addSubCell(dataItem, pCell, space, curCell,vField.getFieldAlias(),vField,fun);
				}
			}else {
				String alias="汇总";
				if(Subtotal.DEFINDE==subtotal) {
					alias = fun.getDesc();
				}
				addSubCell(dataItem, pCell, space, curCell,alias,null,fun);
			}
		}
	}

	private void addSubCell(DataItem dataItem, DataCell pCell, String space, DataCell curCell,String alias,PanelField vField,Calculation fun) {
		DataCell subCell = dataItem.getCurCell();//分类汇总单元
		subCell.setDataType(DataType.SUBTOTAL);
		subCell.setPosY(this.getPosY());
		subCell.setPosX(curCell.getPosX());
		subCell.setPrefix(space);
		subCell.setValPanelField(vField);
		subCell.setCalc(fun);
		String format = subCell.getFormat() +" "+ alias;
		subCell.setFormat(format);
		if(pCell!=null) {			
			//如果存在父对象，添加父对象到当前对象列表
			List<DataCell> pCells = pCell.getRowCells().stream()
					.collect(Collectors.toList());
			subCell.setRowCells(pCells);
		}
		subCell.addRowCell(subCell);
		subCell.setSpancol(this.cols-curCell.getPosX());
		List<DataCell> subRow = new ArrayList<>();
		subRow.add(subCell);
		this.addLeafCell(subRow, dataItem.getDataSource());
	}
	
	/**
	 * 是否在不需要处理分类汇总
	 * @param panelField
	 * @return 是否在不需要处理分类汇总
	 */
	private boolean isShowSubTotal(PanelField panelField) {
		if(TotalField.isTotalField(panelField)) {
			//1.多值字段没有分类汇总
			return false;
		}else {
			AxisField axisField = (AxisField)panelField;
			//无汇总时不需要处理
			if(Subtotal.NOTHING==axisField.getSubtotal()) {
				//2.分类汇总设置为无。
				return false;
			}
			//
			List<Calculation> defFuns = axisField.getDefFuns();
			if(Subtotal.DEFINDE==axisField.getSubtotal() && (defFuns==null||defFuns.size()==0)) {
				//2.这种情况相当于设置汇总类型为无。
				return false;
			}
			
			//存在多个自定义汇总时需要小计
			if(defFuns!=null && defFuns.size()>1) {
				return true;
			}
			
			//为顶部显示且后续字段不存在多值情况（多值字段在当前字段前面时不影响），则不显示分类汇总行
			if(axisField.isTopTotal() && !this.includeTotalField(panelField)) {
				//3.树型布局，显示在顶部且不存在多值,只有行区域需要此判断，列区域不需要
				return false;
			}
			if(this.isLastAxisField(panelField)) {
				//4.最后一个非值字段，不需要分类汇总
				return false;
			}
			
			return true;			
		}	
	}

	/**
	 * 获取下一行的数据。
	 * @param curCell
	 * @return 获取下一行的数据。
	 */
	private List<DataCell> getNextRow(DataCell curCell) {
		List<DataCell> nextRow=null;
		List<DataCell> rowCells = curCell.getRowCells();
		for(DataCell rowCell:rowCells) {
			PanelField pField = rowCell.getPanelField();
			if(this.isRepeatShow(pField)) {
				//需要重复展示
				if(nextRow==null) {//如果没有初始化，则需要初始化
					nextRow = new ArrayList<>();
				}
				nextRow.add(curCell.getNextClone(rowCell));//复制父结点前面单元对象的一个单元对象，复制过程Y轴会增加
			}
		}
		return nextRow;
	}

	/**
	 * 增加当前节点后面的空白单元
	 * @param dataItem
	 * @param curRow
	 * @param curCell
	 */
	private void addEmptyCell(DataItem dataItem, List<DataCell> curRow,DataCell curCell) {
		int curPosX = curCell.getPosX();
		int curPosY = curCell.getPosY();
		int emptyCols = this.cols - (curPosX+1);
		if(emptyCols>0) {
			//增加当前行的空白单元格
			for(int index=0;index<emptyCols;index++) {
				curPosX +=1;//同行，x轴坐标要增加1
				DataCell emptyCell =dataItem.getEmptyCell();
				emptyCell.setPosY(curPosY);
				emptyCell.setPosX(curPosX);
				emptyCell.setRowCells(curCell.getRowCells());
				curRow.add(emptyCell);
			}
		}
		this.addLeafCell(curRow, dataItem.getDataSource());
	}
	
	public String addSpace(String space){
		return space += RConstant.SPACE;//两个空格
	}
	
	public String initSpace(){
		return "";
	}
	
	
	
	/**
	 * 计算每个结点(字段)X轴位置
	 * @param dataItem
	 * @return 每个结点(字段)X轴位置
	 */
	private int getPosXofItem(DataItem dataItem) {
		int index=0;
		PanelField itemField = dataItem.getPanelField();
		//panelField存在别名列表，所有字段必须存在别名且别名设置各不相同，在值区域字段名称可以相同。所这里用别名判断即可
		String curFieldAlias = itemField.getFieldAlias();
		for(PanelField field:this.panelFields) {
			if(field.getFieldAlias().equals(curFieldAlias)) {
				//遇到相同就退出。
				break;
			}
			if(this.isTreeSameCol(field)) {
				continue;
			}
			index++;
		}		
		return index;
	}
	
	
	/**
	 * 计算行区域当前节点行数
	 * @param dataItem 数据结点
	 * @return 行区域当前节点行数
	 */
	public int itemRows(DataItem dataItem) {
		int rowIndex=1;
		List<DataItem> sonList = dataItem.getSonList();
		//如果没有子节点，则说明为叶子节点,则直接返回1
		if(sonList==null || sonList.isEmpty()) {
			return rowIndex;
		}
		PanelField panelField = dataItem.getPanelField();
		if(TotalField.isTotalField(panelField)) {
			rowIndex +=1;//如果是数值行，则另起一行
		}else {
			RowAxisField itemField = (RowAxisField)panelField;
			Layout itemLayout = itemField.getLayout();
			if(Layout.TABLE==itemLayout) {//构建表格
				Subtotal subtotal = itemField.getSubtotal();
				if(Subtotal.NOTHING==subtotal) {//如果没有汇总
					rowIndex +=0;
				}else {
					rowIndex +=1;//TODO,如果自定义汇总是多个的情况暂先不支持。
				}
			}else {//大纲形式展示
				rowIndex +=1;//相当于在顶行增加一个汇总行。
			}
		}
		//存在子节点的情况
		for(DataItem sonItem:sonList) {
			rowIndex += itemRows(sonItem);
		}	
		
		return rowIndex;
	}
	

}

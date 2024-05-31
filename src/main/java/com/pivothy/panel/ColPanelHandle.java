package com.pivothy.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.pivothy.Pivot;
import com.pivothy.data.DataCell;
import com.pivothy.data.DataItem;
import com.pivothy.data.DataType;
import com.pivothy.field.AxisField;
import com.pivothy.field.Calculation;
import com.pivothy.field.PanelField;
import com.pivothy.field.Subtotal;
import com.pivothy.field.TotalField;

/**
 * 
 * 
 * @author 石浩炎
 */
public class ColPanelHandle extends AxisPanelHandle {

	/**
	 * 用于记录列区域的行数，该数值会在如下情况更新： 1.增加行字段 2.增加多值字段 为字段列表数
	 * @return 列区域的行数
	 */
	public int getRows() {
		return this.getPanelFields().size();
	}

	public ColPanelHandle(Pivot<?> privotForge) {
		super(privotForge);
	}
	
	

	@Override
	public void buildPanelCells() {
		if (this.rootDatas.size()==0) {
			//列区域不存在的情况，还存在值区域只有一个字段的情况或多个字段时totalField在行区域的
			List<PanelField> valFields = this.privotForge.getValPanelFields();
			if(valFields.size()==0) {
				//列区域和值区域都没有值时，列区域没有标题内容。
				return;
			}else if(valFields.size()==1) {
				//只有一个字段的情况,增加一个标题行。
				PanelField vField = valFields.get(0);
				DataCell headCell = new DataCell(null,vField.getFieldAlias());
				headCell.setValPanelField(vField);
				headCell.setDataType(DataType.TITLE);
				List<DataCell> headRow = new ArrayList<>();
				headRow.add(headCell);
				this.addLeafData(headCell, this.privotForge.getDataSource());
				this.panelCells.add(headRow);
			}else {
				//存在多个字段的情况且在行区域时，增加一个空白单元标题
				if(Pivot.PANEL_ROW.equals(this.privotForge.getPanelOfTotalField())) {
					DataCell headCell = new DataCell(null,"");
					headCell.setDataType(DataType.TITLE);
					List<DataCell> headRow = new ArrayList<>();
					headRow.add(headCell);
					this.addLeafData(headCell, this.privotForge.getDataSource());
					this.panelCells.add(headRow);
				}
			}
		}else {
			// 处理一级节点
			for (DataItem dataItem : this.rootDatas) {
				buildItemCell(dataItem,null);
			}
			//对叶子节点按x坐标进行排序。
			this.sortLeafCells();
			//处理总计单元
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
	}
	
	//对叶子节点按X坐标值进行排序。因在多字段时，上层字段的小节在前面。
	private void sortLeafCells() {
		 Collections.sort(this.leafDatas,(leaf1,leaf2) ->Integer.compare(leaf1.getDataCell().getPosX(), leaf2.getDataCell().getPosX()));
	}

	private void addTotalCell(PanelField field,String alias) {
		DataCell totalCell = new DataCell(null,alias);
		totalCell.setPosY(0);
		totalCell.setValPanelField(field);
		List<DataCell> totalRow = this.panelCells.get(0);
		totalCell.setPosX(this.getPosXOfLastCell(totalRow));
		totalCell.setSpanrow(this.getRows());
		totalCell.setDataType(DataType.TOTAL);
		if(field!=null) {
			totalCell.setFormat(field.getFieldAlias()+"汇总");
		}
		this.addLeafData(totalCell, this.privotForge.getDataSource());
		totalRow.add(totalCell);
	}

	/**
	 * 构建列区域的数据结点集合
	 * @param dataItem 数据结点对象
	 * @param pCell  父单元
	 */
	public void buildItemCell(DataItem dataItem,DataCell pCell) {
		// 构建当前数据结点的单元对象
		PanelField panelField = dataItem.getPanelField();
		int rowIndex = this.getIndexOfField(panelField);//获取当前单元所在行列表
		List<DataCell> curRow = null;
		if(rowIndex<this.panelCells.size()) {
			curRow = this.panelCells.get(rowIndex);
		}
		int posX = 0;
		if(curRow==null) {//首行第一个字段为空
			curRow = new ArrayList<>();
			this.panelCells.add(curRow);
			posX = 0;
		}else {
			posX = getPosXOfLastCell(curRow);//获取最后一个单元的POSX值
			if(pCell!=null) {
				DataCell lastCell = curRow.get(curRow.size()-1);//获取当前行最后一个单元
				/**
				 * 当节点最后一个字段的X轴坐标小于父节点时，说明父节点存在小计节点，这里就要以父节点的X轴坐标一致。
				 */
				if(lastCell.getPosX()<pCell.getPosX()) {
					posX = pCell.getPosX();
				}
			}
		}
		//1.构建当前数据结点单元对象到行单元对象列表中。
		DataCell curCell = dataItem.getCurCell();
		int curPosY = rowIndex;//起始行
		int curPosX = posX;
		curCell.setPosY(curPosY);
		curCell.setPosX(curPosX);
		curCell.setDataType(DataType.TITLE);
		curCell.addColCell(curCell);		
		//增加当前节点
		curRow.add(curCell);
		if(dataItem.isLeafDataIem()) {
			this.addLeafData(curCell, dataItem.getDataSource());//增加叶子单元
			//最后一个结点
			return;
		}else {
			int maxCol=getMaxCol(dataItem);//获取总列数
			//处理同行
			if(this.isRepeatShow(panelField)) {
				//同行增加复制单元
				for(int index=1;index<maxCol;index++) {
					curRow.add(curCell.getClone());//在复制中已增加了posX的值。
				}
			}else {
				curCell.setSpancol(maxCol);//设置跨列数
			}
			AxisField axisField = (AxisField)panelField;
			curCell.setCalc(axisField.getCellCalc());
			//处理该字段的分类汇总
			if(isShowSubtotal(panelField)) {
				//AxisField axisField = (AxisField)panelField;
				Subtotal subtotal = axisField.getSubtotal();//分类汇总
				List<Calculation> defFuns = axisField.getDefFuns();//自定义汇总
				List<PanelField> vFields = this.getValFields();//值区域的列表
				//subtotal只有默认和自定义两种
				if(Subtotal.DEFAULT==subtotal) {
					defFuns = new ArrayList<>();
					defFuns.add(Calculation.SUM);
				}
				for(Calculation fun:defFuns) {					
					if(this.includeTotalField(panelField)) {
						//需要当前区域存在多值字段时,且在当前字段后面的情况下汇总才需要考虑
						for(PanelField vField:vFields) {							
							addSubCell(dataItem, pCell, rowIndex, curRow, curCell,vField.getFieldAlias(),vField);
						}
					}else {
						String alias="汇总";
						if(Subtotal.DEFINDE==subtotal) {
							alias = fun.getDesc();
						}
						addSubCell(dataItem,pCell,rowIndex,curRow,curCell,alias,null);						
					}
				}
			}
			//同一行处理完成，再处理下一个字段（即下一行)
			for(DataItem sonItem:dataItem.getSonList()) {
				buildItemCell(sonItem,curCell);
			}
		}
		
		
	}
	
	/**
	 * 增加分类汇总字段
	 * @param dataItem
	 * @param pCell
	 * @param rowIndex
	 * @param curRow
	 * @param curCell
	 * @param alias
	 * @param vField
	 */
	private void addSubCell(DataItem dataItem, DataCell pCell, int rowIndex, List<DataCell> curRow, DataCell curCell,
			String alias,PanelField vField) {
		DataCell subCell = dataItem.getCurCell();//分类汇总单元
		subCell.setDataType(DataType.SUBTOTAL);
		subCell.setPosY(rowIndex);//同当前
		subCell.setPosX(getPosXOfLastCell(curRow));
		subCell.setValPanelField(vField);
		String format = subCell.getFormat() +" "+ alias;
		subCell.setFormat(format);
		if(pCell!=null) {
			List<DataCell> pCells = pCell.getColCells().stream()
					.collect(Collectors.toList());
			subCell.setColCells(pCells);
		}
		subCell.addColCell(subCell);
		subCell.setSpanrow(this.getRows()-rowIndex);//设置分类汇总的跨行数
		curRow.add(subCell);//当前行增加合计单元
		this.addLeafData(subCell, dataItem.getDataSource());//这里的顺序不对，需要重新排序。
	}

	/**
	 * 获取最后一个单元的POSX值
	 * @param curRow
	 * @return
	 */
	private int getPosXOfLastCell(List<DataCell> curRow) {
		int posX;
		DataCell lastCell = curRow.get(curRow.size()-1);//获取当前行最后一个单元
		posX = lastCell.getPosX() + lastCell.getSpancol();
		return posX;
	}
	
	public int getMaxCol(DataItem dataItem) {
		int maxCol = 0;
		if(dataItem.isLeafDataIem()) {
			maxCol++;
		}else {
			for(DataItem sonItem:dataItem.getSonList()) {
				maxCol+=this.getMaxCol(sonItem);//子节点的列数
				PanelField panelField = sonItem.getPanelField();
				if(isShowSubtotal(panelField)) {
					AxisField axisField = (AxisField)panelField;
					Subtotal subtotal = axisField.getSubtotal();//分类汇总
					List<Calculation> defFuns = axisField.getDefFuns();//自定义汇总
					List<PanelField> vFields = this.getValFields();//值区域的列表
					int funNum=1;//默认为default
					if(Subtotal.DEFINDE==subtotal &&defFuns.size()>0) {
						funNum = defFuns.size();
					}
					if(vFields.size()==0) {
						maxCol+=funNum;
					}else {
						maxCol+=funNum * vFields.size();
					}
				}
			}		
		}		
		return maxCol;
	}
	
	public boolean isShowSubtotal(PanelField panelField) {
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
				//3.这种情况相当于设置汇总类型为无。
				return false;
			}
			if(this.isLastAxisField(panelField)) {
				//4.最后一个非值字段，不需要分类汇总
				return false;
			}
			return true;
		}
	}

}

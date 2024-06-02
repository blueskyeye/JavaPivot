package com.pivothy.panel;

import java.util.ArrayList;
import java.util.List;

import com.pivothy.Pivot;
import com.pivothy.data.DataCell;
import com.pivothy.data.DataItem;
import com.pivothy.data.LeafData;
import com.pivothy.data.TreeDict;
import com.pivothy.field.AxisField;
import com.pivothy.field.PanelField;
import com.pivothy.field.TotalField;
import com.pivothy.service.FieldMapper;

/**
 * 
 * 
 * @author 石浩炎
 */
public abstract class  AxisPanelHandle extends PanelHandleBase {

	private boolean hasTotalField = false;//判断是否存在多值字段
	
	/**
	 * 该区域的一级数据结点对象列表。(可按树型字典，字典，源数据三种类型存储结点为信息）
	 */
	protected List<DataItem> rootDatas;
	/**
	 *设置树型结点，因设置多个字段，需要设置行或列区域对象上。优先级最高。
	 *单个字段的字典，设置在各个字段本身。
	 *字段没有设置，则使用源数据结点。
	 */
	protected List<TreeDict> treeDicts;//
	
	/**
	 * 值区域字段列表
	 */
	protected List<PanelField> valFields;
	
	/**
	 * 是否显示区域总计,默认显示
	 */
	protected boolean isShowTotal;
	
	/**
	 * 行（列）区域的叶子单元对象列表。
	 */
	protected List<LeafData> leafDatas;
	
	/**
	 * 增加一外节点
	 * @param cell  叶子单元对象
	 * @param dataSource  当前节点对应的数据源
	 */
	public void addLeafData(DataCell cell,List dataSource) {
		LeafData data = new LeafData(cell);
		data.setDataSource(dataSource);
		this.leafDatas.add(data);
	}
	
	public List<LeafData> getLeafDatas() {
		return leafDatas;
	}

	public AxisPanelHandle(Pivot privotForge) {
		super(privotForge);
		this.rootDatas = new ArrayList<>();
		this.leafDatas = new ArrayList<>();
		this.isShowTotal = true;
	}
	
	public boolean isShowTotal() {
		return isShowTotal;
	}

	public void setShowTotal(boolean isShowTotal) {
		this.isShowTotal = isShowTotal;
	}

	public boolean isHasTotalField() {
		return hasTotalField;
	}
	
	/**
	 * 是否需要总计
	 * @return 是否需要总计
	 */
	public boolean hasTotal() {
		return this.isShowTotal && this.panelFields.size()>0 && this.hasAxisField();
	}
	
	/**
	 * 
	 * @return 获取当前区域的最新行数
	 */
	public int getRowNumOfCells() {
		return this.panelCells.size();
	}
	
	/**
	 * 获取当前行高
	 * @return 获取当前行高
	 */
	protected int getPosY() {
		int nums=0;
		int rows = this.panelCells.size();
		if(rows>0) {
			List<DataCell> lastRow = this.panelCells.get(rows-1);
			if(lastRow!=null && lastRow.size()>0) {				
				DataCell dataCell = lastRow.get(0);
				//针对列区域有多个值的情况下。
				nums = dataCell.getPosY()+dataCell.getSpanrow();//最后一个单元Y+跨行。
			}
		}
		
		return nums;
	}
	
	/**
	 * 当前字段后续是否包含多值字段
	 * @param panelField 区域字段
	 * @return 当前字段后续是否包含多值字段
	 */
	public boolean includeTotalField(PanelField panelField) {
		String fieldAlias = panelField.getFieldAlias();
		boolean isInclude = false;
		boolean isFind = false;
		for(PanelField field:this.panelFields) {
			if(fieldAlias.equals(field.getFieldAlias())) {
				isFind=true;
				continue;
			}
			//在找到当前字段的后续字段是否包括多值字段
			if(isFind && TotalField.isTotalField(field)) {
				isInclude = true;
				break;
			}
		}
		
		return isInclude;
	}
	
	/**
	 * 是否为列或行区域最后一个非多值字段
	 * @param panelField 区域字段
	 * @return 是否为列或行区域最后一个非多值字段
	 */
	protected boolean isLastAxisField(PanelField panelField) {
		boolean isLast = true;
		boolean isFind = false;
		String fieldAlias = panelField.getFieldAlias();
		for(PanelField field:this.panelFields) {
			if(fieldAlias.equals(field.getFieldAlias())) {
				isFind=true;
				continue;
			}
			//在找到当前字段的后续字段是否仍包括非多值字段,找到说明不是。
			if(isFind && !TotalField.isTotalField(field)) {
				isLast = false;
				break;
			}
		}
		return isLast;
	}
	
	/**
	 * 是否存在非多值字段
	 * @return 是否存在非多值字段
	 */
	protected boolean hasAxisField() {
		for(PanelField field:this.panelFields) {
			if(!TotalField.isTotalField(field)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 增加多值虚拟字段
	 * @param valFields 多值虚拟字段 
	 */
	public void addTotalField(List<PanelField> valFields) {
		//不存在时才增加
		if(!this.hasTotalField) {
			TotalField field = new TotalField();
			this.panelFields.add(field);
			this.hasTotalField = true;
		}
		this.valFields = valFields;
	}
	/**
	 * 移除多值字段
	 */
	public void removeTotalField() {
		if(this.hasTotalField) {
			for(PanelField field:this.panelFields) {
				if(TotalField.isTotalField(field)) {
					this.panelFields.remove(field);
					this.hasTotalField = false;
					break;
				}
			}
		}
		this.valFields = null;
	}
	
	/**
	 * 判断是否树型布局且同列字段。
	 * @param panelField 区域字段
	 * @return 是否树型布局且同列字段
	 */
	protected boolean isTreeSameCol(PanelField panelField) {
		if(!TotalField.isTotalField(panelField)) {
			AxisField axisField = (AxisField)panelField;
			if(axisField.isTreeSameCol()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否树型布局且不同列字段
	 * @param panelField 区域字段
	 * @return 是否树型布局且不同列字段
	 */
	protected boolean isTreeMutiCol(PanelField panelField) {
		if(!TotalField.isTotalField(panelField)) {
			AxisField axisField = (AxisField)panelField;
			if(axisField.isTreeMutiCol()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断当前字段是否重复显示。
	 * @param panelField 区域字段
	 * @return 当前字段是否重复显示。
	 */
	protected boolean isRepeatShow(PanelField panelField) {
		if(TotalField.isTotalField(panelField)) {
			return false;
		}else {
			AxisField axisField = (AxisField)panelField;
			return axisField.isShowReatField();
		}
	}
	
	
	//构建行、列区域的源数据对象
	public void buildDataItem() {
		List<PanelField> valFields = this.privotForge.getValPanelFields();
		if(this.isEmptyOfPanel()) {
			return ;
		}
		List dataSource = this.privotForge.getDataSource();
		//构建首字段的数据节点列表
		PanelField field = this.panelFields.get(0);//处理第一个字段
		FieldMapper fieldMapper = this.privotForge.getFieldMapper();
		
		this.rootDatas = fieldMapper.mapDataItem(this, field, dataSource, valFields);		
	}
	/**
	 * 获取当前字段的下一字段，如果没有找到返回null，说明已是最后一个字段
	 * @param curField  当前字段
	 * @return 是否有下一个字段
	 */
	public PanelField getNextField(PanelField curField) {
		int listSize = this.panelFields.size();
		for (int i = 0; i < listSize; i++) {
			PanelField panelField = this.panelFields.get(i);
			if (panelField.getFieldAlias().equals(curField.getFieldAlias())) {
		        if (i + 1 < listSize) {
		            return this.panelFields.get(i + 1); // 返回下一个对象
		        } else {
		        	return null;
		        }
		    }
		}
		return null;
	}
	
	
	
	public List<TreeDict> getTreeDicts() {
		return treeDicts;
	}
	public void setTreeDicts(List<TreeDict> treeDicts) {
		this.treeDicts = treeDicts;
	}
	public List<DataItem> getRootDatas() {
		return rootDatas;
	}
	

	public List<PanelField> getValFields() {
		return valFields;
	}

	public void setValFields(List<PanelField> valFields) {
		this.valFields = valFields;
	}

}

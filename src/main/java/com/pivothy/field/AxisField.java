package com.pivothy.field;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 * 
 * @author 石浩炎
 */
public class AxisField extends PanelField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 设置字典名称显示 */
	protected String dictFieldName;
	/** 分类汇总,默认为自动*/
	protected Subtotal subtotal;	
	protected List<Calculation> defFuns;
	/**布局类型-默认表格形式**/
	protected Layout layout;
	/**布局类型-大纲形式-在同一列中显示下一字段的标签**/
	protected boolean isSingleColoumn=false;
	/**布局类型-大纲形式-在每个组顶端显示分类汇总**/
	protected boolean isTopSubtotal=false;
	
	/**
	 * 针对存在子节点的情况，是否重复展示当前对象值。
	 */
	protected boolean isRepeatShow=false;
	/**
	 * 设置字段的字典名称,只能设置单个字段
	 */
	private LinkedHashMap<String,Object> dictMap;
	
	private boolean isShowDictData;//是否展示字典数据
	
	private boolean isRowPanel;//是否在行区域字段
	
	
	
	public AxisField(DataField dataField) {
		this(dataField,true);
	}
	
	public AxisField(DataField dataField,boolean isRowPanel) {
		super(dataField);
		this.subtotal = Subtotal.DEFAULT;
		this.layout = Layout.TABLE;
		this.isRowPanel = isRowPanel;//设置字段所在区域
	}
	
	public boolean isShowDict() {
		if(this.dictMap==null || this.dictMap.isEmpty()) {
			return false;
		}
		return this.isShowDictData;
	}
	
	public Calculation getCellCalc() {
		if(Subtotal.DEFAULT==this.getSubtotal()) {
			return Calculation.SUM;
		}else if(Subtotal.NOTHING==this.getSubtotal()){
			return null;
		}else if(Subtotal.DEFINDE==this.getSubtotal()){
			if(this.defFuns!=null && this.defFuns.size()==1) {
				return this.defFuns.get(0);
			}else {
				return null;
			}
		}
		return null;
	}
	
	
	/**
	 * 树型同列设置
	 * @return  在大纳布局下是否同列显示
	 */
	public boolean isTreeSameCol() {
		if(Layout.TREE==this.layout) {
			if(this.isSingleColoumn) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 树型非同列设置
	 * @return 在大纳布局下是否同列显示
	 */
	public boolean isTreeMutiCol() {
		if(Layout.TREE==this.layout) {
			if(!this.isSingleColoumn) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 是否存在多个自定义汇总函数
	 * @return 是否存在多个自定义汇总函数
	 */
	public boolean isMutiFuns() {
		if(this.defFuns==null||this.defFuns.size()<2) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断当前字段是否重复显示
	 * @return 判断当前字段是否重复显示
	 */
	public boolean isShowReatField() {
		if(this.isTreeSameCol()) {
			return false;
		}else {
			return this.isRepeatShow;
		}
	}
	
	/**
	 * 判断
	 * @return 是否大纲布局。
	 */
	public boolean isTreeLayout() {
		if(Layout.TREE==this.layout) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 是否在顶部显示汇总
	 * @return 是否在顶部显示汇总
	 */
	public boolean isTopTotal() {
		if(Layout.TREE==this.layout) {
			if(this.isTopSubtotal) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public boolean isRowPanel() {
		return isRowPanel;
	}

	public void setRowPanel(boolean isRowPanel) {
		this.isRowPanel = isRowPanel;
	}
	
	/**
	 * 判断当前字段是否totalField字段.
	 * @return 判断当前字段是否totalField字段.
	 */
	public boolean isTotalField() {
		//totalField字段不存在dataField属性。
		return this.dataField == null ? true:false;
	}

	public Subtotal getSubtotal() {
		return subtotal;
	}


	public void setSubtotal(Subtotal subtotal) {
		this.subtotal = subtotal;
		if(Subtotal.DEFINDE==subtotal) {
			this.defFuns = new ArrayList<>();
		}else {
			this.defFuns = null;
		}
	}
	
	public Layout getLayout() {
		return layout;
	}


	public void setLayout(Layout layout) {
		this.layout = layout;
	}
	
	public boolean isSingleColoumn() {
		return isSingleColoumn;
	}


	public void setSingleColoumn(boolean isSingleColoumn) {
		this.isSingleColoumn = isSingleColoumn;
	}


	public boolean isTopSubtotal() {
		return isTopSubtotal;
	}


	public void setTopSubtotal(boolean isTopSubtotal) {
		this.isTopSubtotal = isTopSubtotal;
	}
	
	public String getDictFieldName() {
		return dictFieldName;
	}

	public void setDictFieldName(String dictFieldName) {
		this.dictFieldName = dictFieldName;
	}
	
	public LinkedHashMap<String, Object> getDictMap() {
		return dictMap;
	}

	public void setDictMap(LinkedHashMap<String, Object> dictMap) {
		this.dictMap = dictMap;
	}

	public boolean isShowDictData() {
		return isShowDictData;
	}

	public void setShowDictData(boolean isShowDictData) {
		this.isShowDictData = isShowDictData;
	}

	public boolean isRepeatShow() {
		return isRepeatShow;
	}

	public void setRepeatShow(boolean isRepeatShow) {
		this.isRepeatShow = isRepeatShow;
	}

	public List<Calculation> getDefFuns() {
		return defFuns;
	}

	public void setDefFuns(List<Calculation> defFuns) {
		this.defFuns = defFuns;
	}
	
}

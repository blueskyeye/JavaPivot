package com.pivothy.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.pivothy.Pivot;
import com.pivothy.data.DataCell;
import com.pivothy.data.DataType;
import com.pivothy.data.LeafData;
import com.pivothy.exception.ReportException;
import com.pivothy.field.AxisField;
import com.pivothy.field.Calculation;
import com.pivothy.field.PanelField;
import com.pivothy.field.TotalField;
import com.pivothy.field.ValueField;
import com.pivothy.report.tool.StrUtil;
import com.pivothy.service.FieldMapper;

/**
 * 
 * 
 * @author 石浩炎
 */
public class ValPanelHandle extends PanelHandleBase {

	public ValPanelHandle(Pivot<?> privotForge) {
		super(privotForge);
	}
	
	/**
	 * 是否存在多值字段
	 * @return 是否存在多值字段
	 */
	public boolean isMutilField() {
		return this.panelFields!=null && this.panelFields.size()>1?true:false;
	}
	
	/**
	 * 获取一个字段存在多个合计项时的后缀名称。
	 * @param fieldName
	 * @return 后缀名称。
	 */
	private String getFieldAliasSuffix(String fieldName) {
		String suffix = "";
		int index=1;
		for(PanelField field:this.panelFields) {
			if (fieldName.equals(field.getFieldName())) {
	            index++;//从2开始显示后缀
	        }
		}
		if(index>1) {
			suffix=String.valueOf(index);
		}
		return suffix;
	}
	
	public String getFieldAlias(String fieldName) {
		return this.getFieldAlias(fieldName, Calculation.SUM);
	}
	
	public String getFieldAlias(String fieldName,Calculation calc) {
		String prefix = "";//前缀
		if(calc==null) {
			prefix = Calculation.SUM.getDescInfo();//求和项:
		}else {
			prefix = calc.getDescInfo();
		}
		String suffix =getFieldAliasSuffix(fieldName);//后缀
		String fieldAlias = prefix + fieldName + suffix;
		return fieldAlias;
	}

	/**
	 * 值区域处理的过程：
	 * 1.获取交叉单元的数据源，根据行列的字段设置（需要检查字段链接列表的所有匹配）匹配的数据源。
	 * 2.获取交叉单元的值对象，并根据值对象名称获取数据源中对应的字段数据列表。
	 * 3.根据交叉单元设置的汇总类型计算字段数据列表的汇总数据。
	 * 4.将交叉单元加入列的行单元对象列表中。
	 */
	@Override
	public void buildPanelCells() {
		RowPanelHandle rowPanel = this.privotForge.getRowPanel();
		ColPanelHandle colPanel = this.privotForge.getColPanel();
		List<LeafData> rowLeafs = rowPanel.getLeafDatas();
		List<LeafData> colLeafs = colPanel.getLeafDatas();//不为空。
		
		List<PanelField> valFeilds = this.getPanelFields();
		PanelField valFeild = null;
		if(valFeilds.size()==0) {
			//返回空白列表即可。后面处理。
			return;
		}else if(valFeilds.size()==1) {
			valFeild = valFeilds.get(0);
		}
		/**
		 * 行区域没有数据单元对象的情况
		 * 1.行区域没有字段且列区域没有字段（包括TotalField-多值字段)。
		 * 2.行区域没有字段列区域有字段且存在多值字段，同时多值字段在列区域。
		 * 3.行区域没有字段，值区域有一个字段，列区域有字段(肯定没有多值字段)
		 */
		//int posX = 0;				
		FieldMapper fieldMapper = this.privotForge.getFieldMapper();
		if(rowLeafs.size()==0) {
			//列区域肯定存在字段。
			this.handleValPanelOfNoRow(colLeafs, valFeild);
		}else {
			this.handleValPanel(rowLeafs, colLeafs, valFeild);
		}		
	}

	/**
	 * 处理值区域的内容。
	 * @param rowLeafs
	 * @param colLeafs
	 * @param valFeild
	 */
	private void handleValPanel(List<LeafData> rowLeafs, List<LeafData> colLeafs, PanelField valFeild) {
		FieldMapper fieldMapper = this.privotForge.getFieldMapper();
		//行区域有值
		for(LeafData rowLeaf:rowLeafs) {
			DataCell rowCell = rowLeaf.getDataCell();//行区域单元对象
			List<DataCell> rowCells = rowCell.getRowCells();
			if(isBlankRow(rowCell)) {
				addBlankRow(colLeafs, rowCells);
				continue;//这里要跳转到下一个叶子对象进行处理。
			}
			//后面处理非空行
			/**
			 * PanelField为空的情况
			 * 1.
			 */
			//PanelField panelField = rowCell.getPanelField();//可能为空。
			List dataSource = rowLeaf.getDataSource();//默认用行区域数据源。				
			PanelField curValFeild = valFeild;
			if(curValFeild==null) {
				if(Pivot.PANEL_ROW.equals(this.privotForge.getPanelOfTotalField())) {
					String alias = getValFieldAlias(rowCells);
					curValFeild = getValPanelField(alias);						
				}
			}
			int posX = 0;
			List<DataCell> curRow = new ArrayList<>();
			Calculation rowCalc = rowCell.getCalc();//当单元对应的字段在对应区域为非最后一个字段时才有值，其他为空
			for(LeafData colLeaf:colLeafs) {
				DataCell colCell = colLeaf.getDataCell();//列区域单元对象
				List<DataCell> colCells = colCell.getColCells();
				List<DataCell> fields = colCells;//默认过滤列区域的字段列表。
				/**
				 * 当行区域字段>=列区域字段时：
				 *   使用行区域字段的数据源再次过滤列区域字段列表。
				 * 当行区域字段<列区域字段时：
				 *   使用列区域字段的数据源再次过滤行区域字段列表。
				 */
				List filterSource = dataSource;//后面的数据过滤不能改变原来的数据对象。
				if(rowCells.size()<colCells.size()) {//使用较少的字段列表进行二次过滤
					filterSource = colLeaf.getDataSource();
					fields = rowCells;
				}
				//2.获取交叉单元的值对象，并根据值对象名称获取数据源中对应的字段数据列表。
				for(DataCell cell:fields) {
					String value = cell.getValue();
					PanelField cellField = cell.getPanelField();
					String fieldName = cellField.getFieldName();
					if(StrUtil.isBlank(fieldName)) {
						//fieldName为TotalField字段。
						continue;
					}
					filterSource = fieldMapper.mapDataSource(filterSource, fieldName, value);
				}
				//3.获取当前处理的值字段的别名，再根据别名获取当前处理的值字段。
				//获取当前处理的值字段
				curValFeild = getCurValPanelField(curValFeild, colLeaf);					
				ValueField valField = (ValueField)curValFeild;
				
				Calculation colCalc = colCell.getCalc();
				//明确计算规则
				Calculation calc=getCalcOfCell(rowCalc, valField, colCalc);
				//4.获取当前字段单元的值。
				String vValue = null;
				if(calc!=null) {
					vValue = getValueOfDataCell(fieldMapper, filterSource, valField,calc);	
				}
				DataCell valCell = new DataCell(valField,vValue);//空对象
				int curPosY = this.getPosY();//起始行
				int curPosX = posX++;//起始列
				valCell.setPosY(curPosY);
				valCell.setPosX(curPosX);
				valCell.setRowCells(rowCells);
				valCell.setColCells(colCells);					
				curRow.add(valCell);
			}				
			this.panelCells.add(curRow);				
		}
	}

	private Calculation getCalcOfCell(Calculation rowCalc, ValueField valField, Calculation colCalc) {
		Calculation calc=null;
		if(rowCalc==null) {//叶子结点
			if(colCalc==null) {//叶子结点
				//普通单元
				calc = valField.getCalculation();//使用值区域字段的汇总类型
			}else {
				//列汇总
				calc = colCalc;//使用列汇总的汇总类型
			}
		}else {
			if(colCalc==null) {
				calc = rowCalc;//
			}else {
				//行和列的汇总交叉
				if(rowCalc==colCalc) {
					calc = rowCalc;
				}else {
					//行列不相等，直接使用空单元
					calc = null;
				}
			}
		}
		return calc;
	}

	private void handleValPanelOfNoRow(List<LeafData> colLeafs, PanelField valFeild) {
		int posX = 0;
		FieldMapper fieldMapper = this.privotForge.getFieldMapper();
		List<DataCell> curRow = new ArrayList<>();
		for(LeafData leafData:colLeafs) {
			//处理一行对象
			DataCell colCell = leafData.getDataCell();//列区域单元对象
			List<DataCell> colCells = colCell.getColCells();
			//1.获取数据源和区域字段列表
			List<DataCell> fields = colCells;//默认过滤列区域的字段列表。
			List dataSource = leafData.getDataSource();//当前叶子节点的数据源
			
			//2.获取交叉单元的值对象，并根据值对象名称获取数据源中对应的字段数据列表。
			for(DataCell cell:fields) {
				String value = cell.getValue();
				PanelField cellField = cell.getPanelField();
				String fieldName = cellField.getFieldName();
				if(StrUtil.isBlank(fieldName)) {
					//fieldName为TotalField字段。
					continue;
				}
				dataSource = fieldMapper.mapDataSource(dataSource, fieldName, value);
			}
			//3.获取当前值区域的字段
			PanelField curValFeild = valFeild;
			curValFeild = getCurValPanelField(curValFeild, leafData);
			ValueField valField = (ValueField)curValFeild;
			
			Calculation calc=null;
			//4.获取当前字段单元的值。
			String vValue = getValueOfDataCell(fieldMapper, dataSource, valField,calc);
			DataCell valCell = new DataCell(valField,vValue);//空对象
			int curPosY = this.getPosY();//起始行
			int curPosX = posX++;//起始列
			valCell.setPosY(curPosY);
			valCell.setPosX(curPosX);
			valCell.setColCells(colCells);					
			curRow.add(valCell);
		}
		this.panelCells.add(curRow);
	}

	/**
	 * 
	 * @param fieldMapper
	 * @param dataSource
	 * @param valField
	 * @param calc  分类汇总规则
	 * @return 值内容
	 */
	private String getValueOfDataCell(FieldMapper fieldMapper, List dataSource, ValueField valField,Calculation calc) {
		String vValue="";
		//4.
		if(valField.isFormula()) {
			//按公式处理.
			//4.1先拆分出公式的字段列表。
			List<String> mulaFields = valField.getValFields(this.privotForge.getSourceFields());
			//先按单个字段计算出符合条件的和值。
			Map<String,String> mulaValueMap = new HashMap<String,String>();
			
			//4.2获取拆分出的每个字段的数据列表。
			for(String mulaField:mulaFields) {
				List<String> valList = fieldMapper.getValuesOfField(dataSource, mulaField);
				if(valList==null || valList.isEmpty()) {
					vValue="0";
				}else {
					vValue = Calculation.calcSum(valList);//这里只按求和计算。有其他方式需要扩展。
				}
				mulaValueMap.put(mulaField, vValue);
			}
			//4.3 计算公式的值。
			vValue = calcFormula(valField.getFormula(),mulaValueMap);
		}else {
			List<String> valList = fieldMapper.getValuesOfField(dataSource, valField.getFieldName());
			if(valList==null || valList.isEmpty()) {
				vValue="0";
			}else {
				vValue = Calculation.calcNum(valList, calc==null?valField.getCalculation():calc);	
			}
		}
		return vValue;
	}

	private PanelField getCurValPanelField(PanelField valFeild, LeafData leafData) {
		DataCell colCell = leafData.getDataCell();//列区域单元对象
		List<DataCell> colCells = colCell.getColCells();
		//获取当前处理的值字段
		if(Pivot.PANEL_COL.equals(this.privotForge.getPanelOfTotalField())) {
			String alias = getValFieldAlias(colCells);
			valFeild = getValPanelField(alias);
		}
		if(valFeild==null) {
			throw new ReportException("值字段信息不存在！");
		}
		return valFeild;
	}
	
	
	private String calcFormula(String fieldName, Map<String, String> mulaValueMap) {
		String vValue="";
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
		Compilable compilable = (Compilable) engine;
		Bindings bindings = engine.createBindings(); // Local级别的Binding
		CompiledScript JSFunction = null; // 解析编译脚本函数
		try {
			JSFunction = compilable.compile(fieldName);
		} catch (ScriptException e) {
			throw new RuntimeException("设置公式:"+fieldName+"不合法-"+e.getMessage());
		}
		try {
			Iterator<Entry<String, String>> iterator = mulaValueMap.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<String, String> next = iterator.next();
				bindings.put(next.getKey(), next.getValue());
			}
			Object result = JSFunction.eval(bindings);
			if (StrUtil.toDouble(result).isNaN() || StrUtil.toDouble(result).isInfinite()) {
				vValue ="0";
			}else {
				vValue = String.valueOf(result);
			}
			if(StrUtil.isBlank(vValue)) {
				vValue ="0";
			}
		} catch (Exception e) {
			//公式计算出错，如0当除数。作直接作0处理。
			vValue ="0";
		}
		return vValue;
	}
	
	

	/**
	 * 增加空白行
	 * @param colLeafs
	 * @param rowCells
	 */
	private void addBlankRow(List<LeafData> colLeafs, List<DataCell> rowCells) {
		List<DataCell> curRow = new ArrayList<>();
		int posX = 0;
		for(LeafData colLeaf:colLeafs) {
			DataCell colCell = colLeaf.getDataCell();//列区域单元对象
			DataCell valCell = new DataCell(null,"");//空对象
			int curPosY = this.getPosY();//起始行
			int curPosX = posX++;//起始列
			valCell.setPosY(curPosY);
			valCell.setPosX(curPosX);
			valCell.setRowCells(rowCells);
			valCell.setColCells(colCell.getColCells());
			curRow.add(valCell);
		}
		this.panelCells.add(curRow);//增加一空行。
	}
	
	private String getValFieldAlias(List<DataCell> cells) {
		String alias=null;
		for(DataCell cell:cells) {
			PanelField panelField = cell.getPanelField();
			if(TotalField.isTotalField(panelField)) {
				alias = cell.getValue();
				break;
			}
		}
		return alias;
	}
	
	
	
	private int getPosY() {
		return this.getPanelCells().size();
	}
	/**
	 * 判断当前行是否为空白行，如下的情况值区域为空白行。
	 * 1.在行区域字段的布局设置为TREE（大纲类型）且不是显示在同列的情况。
	 * 2.存在多值区域，当前字段为TotalField（多值字段)，且非叶子节点的情况下。
	 * 3.在行区域字段的布局设置为TREE（大纲类型）且当前字段自定义设置多于一个汇总。最后一个单元是否为空都有可能存在。
	 * 4.在行区域字段的布局设置为TREE（大纲类型）且在每个组顶端显示分类汇总的值为false
	 * 5.在行区域字段的布局设置为TREE（大纲类型）且存在多个分类汇总的情况。
	 * @param cell
	 */
	private boolean isBlankRow(DataCell cell) {
		boolean isBlankRow = false;
		String format = cell.getFormat();
		//布局是表格的
		PanelField panelField = cell.getPanelField();
		if(panelField==null) {//总计单元的情况
			return isBlankRow;
		}		
		RowPanelHandle rowPanel = this.privotForge.getRowPanel();		
		//只有在当前字段布局为大纲且非压缩（非同列）的情况下才有可能为空。
		if(StrUtil.isBlank(format)) {//
			//存在上述情况时，叶子节点的单元格内容必然为空.						
			if(TotalField.isTotalField(panelField)) {
				//情况2
				isBlankRow = true;
			}else {	
				AxisField axisField = (AxisField)panelField;//行区域字段
				if(rowPanel.includeTotalField(panelField)) {
					//当前字段后面是否存在多值字段，如果存在，则也为空行
					isBlankRow = true;
				}else if(axisField.isMutiFuns()) {
					//这里只需要判断是否多个函数，上面已判断是否为大纲布局（空白单元只有大纲布局下才可能存在）。
					isBlankRow = true;
				}else if(!axisField.isTopSubtotal()) {
					isBlankRow = true;
				}
			}
		}else {
			if(!TotalField.isTotalField(panelField)) {
				AxisField axisField = (AxisField)panelField;//行区域字段
				if(DataType.NORMAL.equals(cell.getDataType()) 
						&& axisField.isTreeLayout() && axisField.isMutiFuns()) {
					//针对普通单元对象（小计对象需要计算数值，不能算空白行)
					isBlankRow = true;
				}
			}
		}
		return isBlankRow;
	}
	
	
	

}

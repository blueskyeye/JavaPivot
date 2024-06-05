package com.pivothy.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.pivothy.data.DataItem;
import com.pivothy.data.TreeDict;
import com.pivothy.field.AxisField;
import com.pivothy.field.DataField;
import com.pivothy.field.PanelField;
import com.pivothy.field.TotalField;
import com.pivothy.panel.AxisPanelHandle;
import com.pivothy.report.tool.StrUtil;
import com.pivothy.service.FieldMapper;

public class MapFieldMapper implements FieldMapper {

	private DataSourceMgr<?> dataSourceMgr;

	public MapFieldMapper() {

	}

	public DataSourceMgr<?> getDataSourceMgr() {
		return dataSourceMgr;
	}

	public void setDataSourceMgr(DataSourceMgr<?> dataSourceMgr) {
		this.dataSourceMgr = dataSourceMgr;
	}

	public MapFieldMapper(DataSourceMgr<?> dataSourceMgr) {
		this.dataSourceMgr = dataSourceMgr;
	}

	@Override
	public List<DataField> mapFields(Object data) {
		@SuppressWarnings("unchecked")
		Map<String, Object> dataMap = (Map<String, Object>) data;
		Set<String> keySet = dataMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		List<DataField> list = new ArrayList<DataField>();
		while (iterator.hasNext()) {
			String fieldName = iterator.next();
			DataField field = new DataField(fieldName);
			list.add(field);
		}
		return list;
	}

	@Override
	public List<DataItem> mapDataItem(AxisPanelHandle panelHandle, PanelField panelField, List dataSource,
			List<PanelField> valPanelFields) {
		List<DataItem> rootDatas = new ArrayList<>();
		// 针对多值字段，则使用值区域的字段列表作为数据节点，数据节点值为值区域字段的别名。
		if (TotalField.isTotalField(panelField)) {// 多值字段
			// 如果存在多值，则valPanelFields不应该为空
			for (PanelField field : valPanelFields) {
				String fieldValue = field.getFieldAlias();// 作为当前节点的实际值和显示值
				DataItem dataItem = new DataItem(field.getDataField(), fieldValue);
				dataItem.setPanelField(panelField);// 设置值区域字段
				// 设置当前节点的数据源
				dataItem.setDataSource(dataSource);
				rootDatas.add(dataItem);
			}
		} else {// 行或列区域字段
			List<TreeDict> treeDicts = panelHandle.getTreeDicts();
			// 树结点的优化级最高
			if (treeDicts != null && treeDicts.size() > 0) {
				String fieldName = panelField.getFieldName();
				for (TreeDict dict : treeDicts) {
					String fieldValue = (String) dict.getKey();// 直接获取值
					// 直接构建节点对象
					DataItem dataItem = new DataItem(panelField.getDataField(), fieldValue);
					dataItem.setPanelField(panelField);
					dataItem.setTreeDict(dict);
					addSourceData(dataSource, fieldName, fieldValue, dataItem);
					// 添加到一级节点
					rootDatas.add(dataItem);
				}
			} else {
				AxisField axisField = (AxisField) panelField;// 坐标字段
				// 按字典顺序显示
				if (axisField.isShowDict()) {
					LinkedHashMap<String, Object> dictMap = axisField.getDictMap();
					Set<Entry<String, Object>> entrySet = dictMap.entrySet();
					Iterator<Entry<String, Object>> iterator = entrySet.iterator();
					String fieldName = panelField.getFieldName();
					while (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						String fieldValue = entry.getKey();// 当前字段的值
						// 直接构建节点对象
						DataItem dataItem = new DataItem(panelField.getDataField(), fieldValue);
						dataItem.setPanelField(panelField);
						addSourceData(dataSource, fieldName, fieldValue, dataItem);
						// 添加到一级节点
						rootDatas.add(dataItem);
					}
				} else {// 最后按数据源自身数据进行构建
					Map<String, DataItem> dataMap = new HashMap<String, DataItem>();
					for (Object data : dataSource) {
						Map<String, Object> row = (Map<String, Object>) data;
						String fieldName = panelField.getFieldName();
						String fieldValue = StrUtil.getStrValue(row, fieldName);// 当前字段的值
						DataItem dataItem = dataMap.get(fieldValue);
						if (dataItem == null) {
							dataItem = new DataItem(panelField.getDataField(), fieldValue);
							dataItem.setPanelField(panelField);
							rootDatas.add(dataItem);
							dataMap.put(fieldValue, dataItem);
						}
						dataItem.addDataRow(row);// 增加当前数据行。
					}
				}
			}
		}
		// 构建首字段的子数据节点列表
		PanelField nextField = panelHandle.getNextField(panelField);
		if (nextField != null) {
			for (DataItem dataItem : rootDatas) {
				mapSonDataItem(panelHandle, nextField, dataItem, valPanelFields);
			}
		}
		return rootDatas;
	}

	private void mapSonDataItem(AxisPanelHandle panelHandle, PanelField panelField, DataItem curItem,
			List<PanelField> valPanelFields) {
		List dataSource = curItem.getDataSource();
		// 针对多值字段，则使用值区域的字段列表作为数据节点，数据节点值为值区域字段的别名。
		if (TotalField.isTotalField(panelField)) {// 多值字段
			// 如果存在多值，则valPanelFields不应该为空
			for (PanelField field : valPanelFields) {
				String fieldValue = field.getFieldAlias();// 作为当前节点的实际值和显示值
				DataItem dataItem = new DataItem(field.getDataField(), fieldValue);
				dataItem.setPanelField(field);// 设置值区域字段
				// 设置当前节点的数据源
				dataItem.setDataSource(dataSource);
				curItem.addSonDataItem(dataItem);// 添加到父节点中
			}
		} else {// 行或列区域字段
			TreeDict treeDict = curItem.getTreeDict();
			if (treeDict != null && !treeDict.isLeaf()) {// 存在子点时
				String fieldName = panelField.getFieldName();
				// 查看当前结点是否存在子结点
				LinkedHashMap children = (LinkedHashMap) treeDict.getChildren();
				Set<Entry<String, Object>> entrySet = children.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					String fieldValue = entry.getKey();// 当前字段的值
					Object sonObject = entry.getValue();

					// 直接构建节点对象
					DataItem dataItem = new DataItem(panelField.getDataField(), fieldValue);
					dataItem.setPanelField(panelField);
					if (sonObject != null) {
						TreeDict sonTree = (TreeDict) sonObject;
						dataItem.setTreeDict(sonTree);
					}
					addSourceData(dataSource, fieldName, fieldValue, dataItem);
					// 添加到父节点中
					curItem.addSonDataItem(dataItem);
				}
			} else {
				AxisField axisField = (AxisField) panelField;// 坐标字段
				// 按字典顺序显示
				if (axisField.isShowDict()) {
					LinkedHashMap<String, Object> dictMap = axisField.getDictMap();
					Set<Entry<String, Object>> entrySet = dictMap.entrySet();
					Iterator<Entry<String, Object>> iterator = entrySet.iterator();
					String fieldName = panelField.getFieldName();
					while (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						String fieldValue = entry.getKey();// 当前字段的值
						// 直接构建节点对象
						DataItem dataItem = new DataItem(panelField.getDataField(), fieldValue);
						dataItem.setPanelField(panelField);
						addSourceData(dataSource, fieldName, fieldValue, dataItem);
						// 添加到父节点中
						curItem.addSonDataItem(dataItem);
					}
				} else {// 最后按数据源自身数据进行构建
					Map<String, DataItem> dataMap = new HashMap<String, DataItem>();
					for (Object data : dataSource) {
						Map<String, Object> row = (Map<String, Object>) data;
						String fieldName = panelField.getFieldName();
						String fieldValue = StrUtil.getStrValue(row, fieldName);// 当前字段的值
						DataItem dataItem = dataMap.get(fieldValue);
						if (dataItem == null) {
							dataItem = new DataItem(panelField.getDataField(), fieldValue);
							dataItem.setPanelField(panelField);
							dataMap.put(fieldValue, dataItem);
							// 添加到父节点中
							curItem.addSonDataItem(dataItem);
						}
						dataItem.addDataRow(row);// 增加当前数据行。
					}
				}
			}
		}
		// 构建首字段的子数据节点列表
		PanelField nextField = panelHandle.getNextField(panelField);
		if (nextField != null) {
			for (DataItem dataItem : curItem.getSonList()) {
				mapSonDataItem(panelHandle, nextField, dataItem, valPanelFields);
			}
		}
	}

	private void addSourceData(List dataSource, String fieldName, String fieldValue, DataItem dataItem) {
		// 添加对应的数据源
		for (Object data : dataSource) {
			Map<String, Object> row = (Map<String, Object>) data;
			String value = StrUtil.getStrValue(row, fieldName);// 当前字段的值
			if (fieldValue.equals(value)) {// 如果值同,则增加一行。
				dataItem.addDataRow(row);
			}
		}
	}

	@Override
	public List mapDataSource(List dataSource, String fieldName, String fieldValue) {
		List<Map<String, Object>> filterDatas = new ArrayList<>();
		// 添加对应的数据源
		for (Object data : dataSource) {
			Map<String, Object> row = (Map<String, Object>) data;
			String value = StrUtil.getStrValue(row, fieldName);// 当前字段的值
			if (fieldValue.equals(value)) {// 如果值同,则增加一行。
				filterDatas.add(row);
			}
		}
		return filterDatas;
	}

	@Override
	public List<String> getValuesOfField(List dataSource, String fieldName) {
		List<String> values = new ArrayList<>();
		// 添加对应的数据源
		for (Object data : dataSource) {
			Map<String, Object> row = (Map<String, Object>) data;
			String value = StrUtil.getStrValue(row, fieldName);// 当前字段的值
			if (StrUtil.isNotBlank(value)) {// 如果值同,则增加一行。
				values.add(value);
			}
		}
		return values;
	}

	@Override
	public <T> String getValueOfField(T obj, String fieldName) {
		Map<String, Object> row = (Map<String, Object>) obj;
		String value = StrUtil.getStrValue(row, fieldName);// 当前字段的值
		return value;
	}

}

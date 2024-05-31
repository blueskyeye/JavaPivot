package com.pivothy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.pivothy.Pivot;
import com.pivothy.data.DataCell;
import com.pivothy.field.Calculation;
import com.pivothy.field.Layout;
import com.pivothy.field.PanelField;
import com.pivothy.field.ValueField;
import com.pivothy.panel.ColPanelHandle;
import com.pivothy.panel.RowPanelHandle;
import com.pivothy.panel.ValPanelHandle;
import com.pivothy.report.tool.StrUtil;
import com.pivothy.source.DataSourceMgr;
import com.pivothy.source.MockDataSource;

public class PrivotForgeTest {
	private Pivot<Map<String, Object>> privotForge;
    private DataSourceMgr<Map<String, Object>> dataSourceMgr;

    @Before
    public void setUp() {    	
    	// 创建模拟的DataSourceMgr
        dataSourceMgr = new DataSourceMgr<>(MockDataSource.getList());
        // 初始化PrivotForge
        privotForge = new Pivot<Map<String, Object>>(dataSourceMgr);
    }
    
    @Test
    public void testTableRowCell() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("date", "日期", layout);
        privotForge.addRowField("city", "城市", layout);
        Map<String,Object> result = privotForge.exec();
        Object object = result.get("rowDatas");
        List<List<DataCell>> list = (List<List<DataCell>>)object;
        assertEquals(32, list.size());
        System.out.println(result);
    }
    
    /**
     * 测试大纲布局不同列展示。
     */
    @Test
    public void testTreeMutiCol() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("date", "日期", Layout.TREE);
        privotForge.addRowField("city", "城市", layout);
        Map<String,Object> result = privotForge.exec();
        System.out.println(result);
        
    }
    
    /**
     * 测试大纲布局下的重复展示。
     */
    @Test
    public void testTreeMutiColOfRepeat() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("date", "日期", Layout.TREE);
        privotForge.addRowField("city", "城市", layout);
        privotForge.setRepeatShow("date", true);//设置父节点重复显示。
        Map<String,Object> result = privotForge.exec();
        System.out.println(result);        
    }
    /**
     * 测试大纲布局同列展示，同列展示会导致重复展示不启效。
     */
    @Test
    public void testTreeSameCol() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("date", "日期", Layout.TREE);
        privotForge.addRowField("city", "城市", layout);
        privotForge.setLayoutOfSameCol("date", true);//设置大纲同列展示
        Map<String,Object> result = privotForge.exec();
        System.out.println(result);
    }
    
    @Test
    public void testAddRowField() {
        // Arrange
        String fieldName = "city";
        String fieldAlias = "城市";
        Layout layout = Layout.TABLE;

        // Act
        privotForge.addRowField(fieldName, fieldAlias, layout);
        RowPanelHandle rowPanel =privotForge.getRowPanel();
        assertTrue(rowPanel.isExist(fieldName));
        PanelField rowField = rowPanel.getPanelField(fieldName);
        assertNotNull(rowField);
        assertEquals(fieldName, rowField.getFieldName());
        assertEquals(fieldAlias, rowField.getFieldAlias());
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(privotForge);
        
        List<PanelField> valFields = privotForge.getValPanelFields();
        
        assertNotNull(valFields);
        assertEquals(0, valFields.size());
    }

    

    @Test
    public void testAddColField() {
        // 准备
        String fieldName = "prodtype";
        String fieldAlias = "产品类别";

        // 执行
        privotForge.addColField(fieldName, fieldAlias);
        ColPanelHandle colPanel = privotForge.getColPanel();
        assertTrue(colPanel.isExist(fieldName));
        PanelField colField = colPanel.getPanelField(fieldName);

        // 断言
        assertNotNull(colField);
        assertEquals(fieldName, colField.getFieldName());
        assertEquals(fieldAlias, colField.getFieldAlias());
    }
    
    @Test
    public void testAddValField() {
        // 准备
        String fieldName = "num";
        String fieldAlias = "数量";
        Calculation calculation = Calculation.AVG;
        // 执行
        privotForge.addValField(fieldName, fieldAlias, calculation);
        ValPanelHandle valPanel = privotForge.getValPanel();
        assertTrue(valPanel.isExist(fieldName));
        PanelField valField = valPanel.getPanelField(fieldName);

        // 断言
        assertNotNull(valField);
        assertEquals(fieldName, valField.getFieldName());
        assertEquals(fieldAlias, valField.getFieldAlias());
    }
    
    @Test
    public void testAddValField2() {
    	// Arrange
        String fieldName = "amount";
        ValPanelHandle valPanel = privotForge.getValPanel();
        String fieldAlias = valPanel.getFieldAlias(fieldName, Calculation.SUM);
        Calculation calculation = Calculation.SUM;

        // Act
        privotForge.addValField(fieldName);
        List<PanelField> valPanelFields = privotForge.getValPanelFields();

        // Assert
        assertFalse(valPanelFields.isEmpty());
        assertTrue(privotForge.getValPanel().isExist(fieldName));
        PanelField valField = privotForge.getValPanel().getPanelField(fieldName);
        assertNotNull(valField);
        assertEquals(fieldName, valField.getFieldName());
        assertEquals(fieldAlias, valField.getFieldAlias());
        assertEquals(calculation, ((ValueField)valField).getCalculation());
    }


    @Test
    public void testAddValFields() {
        // 添加值字段
    	privotForge.addValField("num");
    	privotForge.addValField("amount");
        ValPanelHandle valPanel =privotForge.getValPanel();
        assertTrue(valPanel.isMutilField());
    }

    @Test
    public void testSetTotalField() {
    	privotForge.addValField("num");
    	privotForge.addValField("amount");
        // 设置多值字段展示区域为列区域
    	privotForge.setTotalFieldOfColPanel(true);
        assertEquals("col", privotForge.getPanelOfTotalField());
    }
    
    /**
     * 测试大纲布局同列展示，同列展示会导致重复展示不启效。
     */
    @Test
    public void testColSpanCell() {
        privotForge.addColField("date", "日期");
        privotForge.addColField("city", "城市");
        String[] cities = {"广州", "上海", "北京", "深圳", "成都"};
        privotForge.setDict("city",cities);
        Map<String,Object> result = privotForge.exec();
        Object object = result.get("colDatas");
        List<List<DataCell>> list = (List<List<DataCell>>)object;
        System.out.println(result);
        printList(list);
    }

    
	private void printList(List<List<DataCell>> list) {
		String empty = "          ";
		for(List<DataCell> row:list) {
			for(DataCell cell:row) {
        		String format = cell.getFormat()+"("+cell.getPosX()+","+cell.getPosY()+")";
        		String space="";
        		for(int i=0;i<cell.getSpancol();i++) {        			
					space +=empty;
        		}
        		if(format.length()<space.length()) {
        			format += space.substring(format.length());
        		}
        		System.out.print(format+" ");
        	}
        	System.out.println();
        }
	}
	
	private void printListMap(List<List<Map<String,Object>>> list) {
		for(List<Map<String,Object>> row:list) {
			for(Map<String,Object> cell:row) {
        		String format = StrUtil.getStrValue(cell, "format")+"("+StrUtil.getStrValue(cell, "posX")+","+StrUtil.getStrValue(cell, "posY")+")";
        		System.out.print(format+" ");
        	}
        	System.out.println();
        }
	}
	
	
	/**
     * 只有多值时且设置在列区域的输出。
     */
    @Test
    public void testValSpanCellOfCol() {
        privotForge.addValField("num");
        privotForge.addValField("price");
        Map<String,Object> result = privotForge.exec();
        Object object = result.get("colDatas");
        List<List<DataCell>> list = (List<List<DataCell>>)object;
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).size());
        System.out.println(result);
        printList(list);
        Object valObj = result.get("valDatas");
        List<List<DataCell>> valList = (List<List<DataCell>>)valObj;
        printList(valList);
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).size());
        
    }
    
    /**
     * 测试只有多值时且设置在行区域的输出。
     */
    @Test
    public void testValSpanCellOfRow() {
        privotForge.addValField("num");
        privotForge.addValField("price");
        privotForge.setTotalFieldOfColPanel(false);//多值设置在行区域
        Map<String,Object> result = privotForge.exec();
        Object object = result.get("rowDatas");
        List<List<DataCell>> list = (List<List<DataCell>>)object;
        assertEquals(3, list.size());
        System.out.println(result);
        printList(list);
    }
    
    /**
     * 测试大纲布局不同列展示。
     */
    @Test
    public void testTableMap() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("city", "城市", layout);
        privotForge.addValField("num");
        privotForge.addValField("price");
        Map<String,Object> result = privotForge.exec();
        System.out.println(result);
        List<List<DataCell>> rowList = (List<List<DataCell>>)result.get("rowDatas");
        printList(rowList);
        List<List<DataCell>> colList = (List<List<DataCell>>)result.get("colDatas");
        printList(colList);
        List<List<DataCell>> valList = (List<List<DataCell>>)result.get("valDatas");
        printList(valList);
        List tableMap = privotForge.outOfTableMap();
        printListMap(tableMap);  
    }
    
    
}

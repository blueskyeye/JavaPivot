package com.pivothy.panel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.pivothy.Pivot;
import com.pivothy.field.DataField;
import com.pivothy.field.PanelField;
import com.pivothy.panel.PanelHandleBase;
import com.pivothy.source.DataSourceMgr;
import com.pivothy.source.MockDataSource;

public class RowPanelHandleTest {
	private PanelHandleBase panelHandleBase;

    @Before
    public void setUp() {
    	// 创建模拟的 DataSourceMgr
        DataSourceMgr dataSourceMgr = new DataSourceMgr<>(MockDataSource.getMapList());
        // 初始化 PrivotForge
        Pivot privotForge = new Pivot(dataSourceMgr);
        panelHandleBase = privotForge.getRowPanel();
    }

    @Test
    public void testIsExist_True() {
        // 添加一个名为 "name" 的字段
        PanelField panelField = new PanelField(new DataField("name"));
        panelHandleBase.addPanelField(panelField);
        // 检查字段是否存在
        assertTrue(panelHandleBase.isExist("name"));
    }

    @Test
    public void testIsExist_False() {
        // 检查未添加的字段是否存在
        assertFalse(panelHandleBase.isExist("age"));
    }

    @Test
    public void testGetPanelField_Exists() {
        // 添加一个名为 "age" 的字段
        PanelField panelField = new PanelField(new DataField("age"));
        panelHandleBase.addPanelField(panelField);
        // 获取字段对象并验证
        PanelField retrievedField = panelHandleBase.getPanelField("age");
        assertNotNull(retrievedField);
        assertEquals("age", retrievedField.getFieldName());
    }

    @Test
    public void testGetPanelField_NotExists() {
        // 尝试获取未添加的字段
        PanelField retrievedField = panelHandleBase.getPanelField("score");
        assertNull(retrievedField);
    }

    @Test
    public void testAddPanelField() {
        // 添加一个新字段
        PanelField newPanelField = new PanelField(new DataField("score"));
        panelHandleBase.addPanelField(newPanelField);
        // 检查新字段是否存在
        assertTrue(panelHandleBase.isExist("score"));
    }

    
}

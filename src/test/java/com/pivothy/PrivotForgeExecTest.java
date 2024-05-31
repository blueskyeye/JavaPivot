package com.pivothy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.pivothy.data.DataCell;
import com.pivothy.data.TreeDict;
import com.pivothy.field.Calculation;
import com.pivothy.field.Layout;
import com.pivothy.report.tool.StrUtil;
import com.pivothy.source.DataSourceMgr;
import com.pivothy.source.MockDataSource;



/**
 * 
 * @version 1.0.0
 * @author shyzq
 * @email haoyan665@163.com
 * @微信 zqmshy
 */
public class PrivotForgeExecTest extends TestTool	{
	private Pivot<Map<String, Object>> privotForge;
    private DataSourceMgr<Map<String, Object>> dataSourceMgr;
    @Before
    public void setUp() {    	
    	// 创建模拟的DataSourceMgr
        dataSourceMgr = new DataSourceMgr<>(MockDataSource.getList());
        // 初始化PrivotForge
        privotForge = new Pivot<Map<String, Object>>(dataSourceMgr);
    }
    /**
     * 行区域1个字段:
     * 列区域1个字段:
     * 值区域1个字段:
     */
    @Test
    public void testOneRowAndOneCol() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("city", "城市", layout);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        printTable(privotForge.outOfTable());
        assertEquals(7, tableMap.size());//7行
        int posY=0;
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(7, row.size());//7列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals("城市", StrUtil.getStr(firstCell, "format"));
        Map<String,Object> tailCell = row.get(6);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
        posY=6;
        List<Map<String,Object>> tailRow = tableMap.get(posY);//尾行
        for(Map<String,Object> cell : tailRow) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCellOfTailRow = tailRow.get(0);
        assertEquals("总计", StrUtil.getStr(firstCellOfTailRow, "format"));
        Map<String,Object> tailCellOfTailRow = tailRow.get(6);
        assertEquals(300.0, StrUtil.getDouble(tailCellOfTailRow, "format"),0.0001);
    }
    
    /**
     * 行区域1个字段: 增加字典功能
     * 列区域1个字段:
     * 值区域1个字段:
     */
    @Test
    public void testDict() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("city", "城市", layout);
        String[] dicts = {"北京","广州","深圳","成都"};
        privotForge.setDict("city",dicts);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        assertEquals(6, tableMap.size());//7行
        int posY=0;
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(7, row.size());//7列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals("城市", StrUtil.getStr(firstCell, "format"));        
        Map<String,Object> tailCell = row.get(6);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
        List<Map<String,Object>> row2 = tableMap.get(2);
        Map<String,Object> firstOfrow2 = row2.get(0);
        assertEquals("广州", StrUtil.getStr(firstOfrow2, "format"));
        
        posY=tableMap.size()-1;
        List<Map<String,Object>> tailRow = tableMap.get(posY);//尾行
        for(Map<String,Object> cell : tailRow) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCellOfTailRow = tailRow.get(0);
        assertEquals("总计", StrUtil.getStr(firstCellOfTailRow, "format"));
        Map<String,Object> tailCellOfTailRow = tailRow.get(6);
        assertEquals(300.0, StrUtil.getDouble(tailCellOfTailRow, "format"),0.0001);
    }
    /**
     * 行区域2个字段: 增加树型字典功能
     * 列区域1个字段:
     * 值区域1个字段:
     */
    @Test
    public void testTreeDict() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("city", "城市", layout);
        privotForge.addRowField("saler", "销售");
        List<TreeDict> treeDict = MockDataSource.getTreeDict();
        privotForge.setTreeDictOfRowPanel(treeDict);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        assertEquals(17, tableMap.size());//7行
        int posY=0;
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(8, row.size());//7列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals("城市", StrUtil.getStr(firstCell, "format"));        
        Map<String,Object> tailCell = row.get(7);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
              
    }
    
    /**
     *  设置2行1列，并设置行区域为树结构字典，同时设置行区域父结点的值重复显示。
     */
    /**
     * 行区域2个字段: 增加字典功能
     * 列区域1个字段:
     * 值区域1个字段:
     */
    @Test
    public void testTreeDictOfRepeat() {
        Layout layout = Layout.TABLE;
        privotForge.addRowField("city", "城市", layout);
        privotForge.addRowField("saler", "销售");
        List<TreeDict> treeDict = MockDataSource.getTreeDict();
        privotForge.setTreeDictOfRowPanel(treeDict);
        privotForge.setRepeatShow("city", true);//设置重复显示
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        assertEquals(17, tableMap.size());//7行
        int posY=0;
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(8, row.size());//7列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals("城市", StrUtil.getStr(firstCell, "format"));        
        Map<String,Object> tailCell = row.get(7);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));            
    }
    /**
     * 行2字段，列1字段，值1字段.
     */
    @Test
    public void test2RowAndOneCol() {
        Layout layout = Layout.TABLE;
        String firstAlias="城市";
        String twoAlias="销售";
        privotForge.addRowField("city", firstAlias, layout);
        privotForge.addRowField("saler", twoAlias, layout);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        assertEquals(12, tableMap.size());//7行
        int posY=0;
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(8, row.size());//8列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals(firstAlias, StrUtil.getStr(firstCell, "format"));
        Map<String,Object> twoCell = row.get(1);
        assertEquals(twoAlias, StrUtil.getStr(twoCell, "format"));
        int tail = row.size()-1;
        Map<String,Object> tailCell = row.get(tail);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
        posY=tableMap.size()-1;//最后一行
        List<Map<String,Object>> tailRow = tableMap.get(posY);//尾行
        for(Map<String,Object> cell : tailRow) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCellOfTailRow = tailRow.get(0);
        assertEquals("总计", StrUtil.getStr(firstCellOfTailRow, "format"));
        tail = tailRow.size()-1;
        Map<String,Object> tailCellOfTailRow = tailRow.get(tail);
        assertEquals(300.0, StrUtil.getDouble(tailCellOfTailRow, "format"),0.0001);
    }
    
    /**
     * 行区域2个字段:为大纲布局，非压缩（非同列），汇总不在顶端。
     * 列区域1个字段
     */
    @Test
    public void test2RowOfTree() {
        Layout layout = Layout.TREE;
        String firstAlias="城市";
        String twoAlias="销售";
        privotForge.addRowField("city", firstAlias, layout);
        privotForge.addRowField("saler", twoAlias, layout);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();        
        this.printFormat(tableMap);
        List<List<String>> tableDatas = privotForge.outOfTable();
        this.printTable(tableDatas);
        assertEquals(17, tableMap.size());//7行
        int posY=0;
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(8, row.size());//8列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals(firstAlias, StrUtil.getStr(firstCell, "format"));
        Map<String,Object> twoCell = row.get(1);
        assertEquals(twoAlias, StrUtil.getStr(twoCell, "format"));
        int tail = row.size()-1;
        Map<String,Object> tailCell = row.get(tail);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
        posY=tableMap.size()-1;//最后一行
        List<Map<String,Object>> tailRow = tableMap.get(posY);//尾行
        for(Map<String,Object> cell : tailRow) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        //数据行
        List<Map<String,Object>> dataRow = tableMap.get(1);
        Map<String,Object> emptyCell = dataRow.get(1);//第二列为空白单元
        assertEquals("", StrUtil.getStr(emptyCell, "format"));
        Map<String,Object> emptyData = dataRow.get(2);//数据列为空白单元
        assertEquals("", StrUtil.getStr(emptyData, "format"));
        
        Map<String,Object> firstCellOfTailRow = tailRow.get(0);
        assertEquals("总计", StrUtil.getStr(firstCellOfTailRow, "format"));
        tail = tailRow.size()-1;
        Map<String,Object> tailCellOfTailRow = tailRow.get(tail);
        assertEquals(300.0, StrUtil.getDouble(tailCellOfTailRow, "format"),0.0001);
    }
    /**
     * 行区域2个字段:为大纲布局，非压缩（非同列），汇总在顶端。
     * 列区域1个字段
     */
    @Test
    public void test2RowOfTreeToptotal() {
        Layout layout = Layout.TREE;
        String firstAlias="城市";
        String twoAlias="销售";
        privotForge.addRowField("city", firstAlias, layout);
        privotForge.setLayoutOfToptotal("city", true);//设置分类汇总在顶部
        privotForge.addRowField("saler", twoAlias, layout);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        assertEquals(12, tableMap.size());//7行
        int posY=0;
        //标题行验证
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(8, row.size());//8列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals(firstAlias, StrUtil.getStr(firstCell, "format"));
        Map<String,Object> twoCell = row.get(1);
        assertEquals(twoAlias, StrUtil.getStr(twoCell, "format"));
        int tail = row.size()-1;
        Map<String,Object> tailCell = row.get(tail);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
        
        //数据行
        List<Map<String,Object>> dataRow = tableMap.get(1);
        Map<String,Object> emptyCell = dataRow.get(1);//第二列为空白单元
        assertEquals("", StrUtil.getStr(emptyCell, "format"));
        Map<String,Object> emptyData = dataRow.get(2);//数据列为空白单元
        assertEquals(12, StrUtil.getDouble(emptyData, "format"),0.001);
        
        //总计行
        posY=tableMap.size()-1;//最后一行
        List<Map<String,Object>> tailRow = tableMap.get(posY);//尾行
        for(Map<String,Object> cell : tailRow) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCellOfTailRow = tailRow.get(0);
        assertEquals("总计", StrUtil.getStr(firstCellOfTailRow, "format"));
        tail = tailRow.size()-1;
        Map<String,Object> tailCellOfTailRow = tailRow.get(tail);
        assertEquals(300.0, StrUtil.getDouble(tailCellOfTailRow, "format"),0.0001);
    }
    
    /**
     * 行区域2个字段:为大纲布局，压缩（同列），汇总在顶端。
     * 列区域1个字段:
     * 值区域1个字段:
     */
    @Test
    public void test2RowOfTreeSameCol() {
        Layout layout = Layout.TREE;
        String firstAlias="城市";
        String twoAlias="销售";
        privotForge.addRowField("city", firstAlias, layout);
        privotForge.setLayoutOfSameCol("city", true);//设置大纲列压缩
        privotForge.setLayoutOfToptotal("city", true);//设置分类汇总在顶部
        privotForge.addRowField("saler", twoAlias, layout);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        assertEquals(12, tableMap.size());//7行
        int posY=0;
        //标题行验证
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(7, row.size());//7列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals(firstAlias, StrUtil.getStr(firstCell, "format"));
        Map<String,Object> twoCell = row.get(1);
        assertEquals("2020/1", StrUtil.getStr(twoCell, "format"));
        int tail = row.size()-1;
        Map<String,Object> tailCell = row.get(tail);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
        
        //数据行 父级
        List<Map<String,Object>> dataRow = tableMap.get(1);
        Map<String,Object> dataCell = dataRow.get(1);//第二列为空白单元
        assertEquals(12, StrUtil.getDouble(dataCell, "format"),0.001);        
        
        //总计行
        posY=tableMap.size()-1;//最后一行
        List<Map<String,Object>> tailRow = tableMap.get(posY);//尾行
        for(Map<String,Object> cell : tailRow) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCellOfTailRow = tailRow.get(0);
        assertEquals("总计", StrUtil.getStr(firstCellOfTailRow, "format"));
        tail = tailRow.size()-1;
        Map<String,Object> tailCellOfTailRow = tailRow.get(tail);
        assertEquals(300.0, StrUtil.getDouble(tailCellOfTailRow, "format"),0.0001);
    }
    
    /**
     * 行区域2个字段:为大纲布局，压缩（同列），汇总在顶端，有多个自定义汇总
     * 列区域1个字段:
     * 值区域1个字段:
     */
    @Test
    public void test2RowOfTreeSameColAndMutiFuns() {
        Layout layout = Layout.TREE;
        String firstAlias="城市";
        String twoAlias="销售";
        privotForge.addRowField("city", firstAlias, layout);
        List<Calculation> funs=new ArrayList<>();
        funs.add(Calculation.SUM);
        funs.add(Calculation.CNT);
        privotForge.setLayoutOfSameCol("city", true);//设置大纲列压缩
        privotForge.setLayoutOfToptotal("city", true);//设置分类汇总在顶部
        privotForge.setDefineSubtotal("city", funs);//设置自定义分类汇总
        privotForge.addRowField("saler", twoAlias, layout);
        privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        printFormat(tableMap);
        assertEquals(22, tableMap.size());//7行
        int posY=0;
        //标题行验证
        List<Map<String,Object>> row = tableMap.get(posY);
        assertEquals(7, row.size());//7列
        for(Map<String,Object> cell : row) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCell = row.get(0);
        assertEquals(firstAlias, StrUtil.getStr(firstCell, "format"));
        Map<String,Object> twoCell = row.get(1);
        assertEquals("2020/1", StrUtil.getStr(twoCell, "format"));
        int tail = row.size()-1;
        Map<String,Object> tailCell = row.get(tail);
        assertEquals("总计", StrUtil.getStr(tailCell, "format"));
        
        //数据行 父级
        List<Map<String,Object>> dataRow = tableMap.get(1);
        Map<String,Object> emptyCell = dataRow.get(1);//第二列为空白单元
        assertEquals("", StrUtil.getStr(emptyCell, "format"));        
        
        //总计行
        posY=tableMap.size()-1;//最后一行
        List<Map<String,Object>> tailRow = tableMap.get(posY);//尾行
        for(Map<String,Object> cell : tailRow) {
        	int curY = StrUtil.getInt(cell, "posY");
        	assertEquals(posY, curY);//7行
        }
        Map<String,Object> firstCellOfTailRow = tailRow.get(0);
        assertEquals("总计", StrUtil.getStr(firstCellOfTailRow, "format"));
        tail = tailRow.size()-1;
        Map<String,Object> tailCellOfTailRow = tailRow.get(tail);
        assertEquals(300.0, StrUtil.getDouble(tailCellOfTailRow, "format"),0.0001);
    }
    
    /**
     * 行区域0个字段:
     * 列区域1个字段:
     * 值区域1个字段:
     */
    @Test
    public void test0Row1Col() {
    	privotForge.addColField("date", "日期");
        privotForge.addValField("num");
        Map<String,Object> result = privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        List<List<DataCell>> rowDatas = (List<List<DataCell>>)result.get("rowDatas");
        assertEquals(2, rowDatas.size());//2行
    }
    /**
     * 行区域0个字段:
     * 列区域2个字段:
     * 值区域1个字段:
     */
    @Test
    public void test0Row2Col() {
    	privotForge.addColField("date", "日期");
    	privotForge.addColField("city", "城市");
        privotForge.addValField("num");
        Map<String,Object> result = privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        assertEquals(3, datas.size());//2行
        List<List<DataCell>> list = (List<List<DataCell>>)result.get("rowDatas");
		List<List<DataCell>> rowDatas = list;
        assertEquals(2, rowDatas.size());//2行
    }
    
    
}

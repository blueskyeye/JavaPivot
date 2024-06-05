/**
 * 
 */
package com.pivothy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.pivothy.data.TreeDict;
import com.pivothy.field.Calculation;
import com.pivothy.field.Layout;
import com.pivothy.field.Subtotal;
import com.pivothy.source.DataSourceMgr;
import com.pivothy.source.MockDataSource;

/**
 * 
 * @author 石浩炎
 */
public class PivotTest extends TestTool {
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
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域1个字段: city
     * 列区域1个字段: date
     * 值区域1个字段: amount
     */
    @Test
    public void test1Row1Col1Val() {
    	privotForge.addRowField("city", "城市");
    	privotForge.addColField("date", "日期");
        privotForge.addValField("amount");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        this.mapHtml(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.tableHtml(datas);        
    }
    
    
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,prodtype
     * 列区域字段: date
     * 值区域字段: amount
     */
    @Test
    public void test2Row1Col1Val() {
    	privotForge.addRowField("city", "城市");
    	privotForge.addRowField("prodtype", "商品类型");
    	privotForge.addColField("date", "日期");
        privotForge.addValField("amount");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,prodtype
     * 列区域字段: date
     * 值区域字段: amount
     */
    @Test
    public void test2Row1Col1ValOfFormula() {
    	privotForge.addRowField("city", "城市");
    	privotForge.addRowField("prodtype", "商品类型");
    	privotForge.addColField("date", "日期");
        privotForge.addValFieldOfFormula("2*num*price","金额",Calculation.SUM);//值区域设置公式
        privotForge.setFieldSubtotal("city", Subtotal.NOTHING);
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,prodtype
     * 列区域字段: date
     * 值区域字段: amount
     */
    @Test
    public void test2Row1Col1ValOfNothing() {
    	privotForge.addRowField("city", "城市");
    	privotForge.addRowField("prodtype", "商品类型");
    	privotForge.addColField("date", "日期");
        privotForge.addValField("amount");
        privotForge.setFieldSubtotal("city", Subtotal.NOTHING);//设置城市字段不需要小计。
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city
     * 列区域字段: date,prodtype
     * 值区域字段: amount
     */
    @Test
    public void test1Row2Col1Val() {
    	privotForge.addRowField("city", "城市");
    	privotForge.addColField("date", "日期");
    	privotForge.addColField("prodtype", "商品类型");
        privotForge.addValField("amount");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city
     * 列区域字段: date
     * 值区域字段: num,amount
     */
    @Test
    public void test1Row1Col2Val() {
    	privotForge.addRowField("city", "城市");
    	privotForge.addColField("date", "日期");
    	privotForge.addValField("num","数量");
        privotForge.addValField("amount","金额");
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city
     * 列区域字段: date
     * 值区域字段: num,amount
     */
    @Test
    public void test1Row1Col2ValOfRowPanel() {
    	privotForge.addRowField("city", "城市");
    	privotForge.addColField("date", "日期");
    	privotForge.addValField("num","数量");
        privotForge.addValField("amount","金额");
        privotForge.setTotalFieldOfColPanel(false);//设置多值字段展示所在区域 true:列区域(默认),false:行区域
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,date
     * 列区域字段: prodtype
     * 值区域字段: num,amount
     * 大纲布局：city
     */
    @Test
    public void test2Row1Col1ValOfTree() {
    	privotForge.addRowField("city", "城市",Layout.TREE);//设置行区域字段(用于纵向扩展）
    	privotForge.addRowField("date","日期");//设置行区域字段（用于纵向扩展)
    	privotForge.addColField("prodtype","商品类型");//设置列区域字段（用于显示数值）
    	privotForge.addValField("amount","金额");//设置值区域字段（用于显示数值）
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,date
     * 列区域字段: prodtype
     * 值区域字段: num,amount
     * 大纲布局：city 汇总在顶端
     */
    @Test
    public void test2Row1Col1ValOfTreeTopsubTotal() {
    	privotForge.addRowField("city", "城市",Layout.TREE);//设置行区域字段(用于纵向扩展）
    	privotForge.addRowField("date","日期");//设置行区域字段（用于纵向扩展)
    	privotForge.addColField("prodtype","商品类型");//设置列区域字段（用于显示数值）
    	privotForge.addValField("amount","金额");//设置值区域字段（用于显示数值）
    	privotForge.setLayoutOfToptotal("city", true);//设置城市汇总在顶端。
    	privotForge.setLayoutOfSameCol("city", true);//设置城市布局压缩显示。
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,date
     * 列区域字段: prodtype
     * 值区域字段: num,amount
     * 大纲布局：city 汇总在顶端,压缩显示
     */
    @Test
    public void test2Row1Col1ValOfTreeSameCol() {
    	privotForge.addRowField("city", "城市",Layout.TREE);//设置行区域字段(用于纵向扩展）
    	privotForge.addRowField("date","日期");//设置行区域字段（用于纵向扩展)
    	privotForge.addColField("prodtype","商品类型");//设置列区域字段（用于横向扩展）
    	privotForge.addValField("amount","金额");//设置值区域字段（用于显示数值）
    	privotForge.setLayoutOfToptotal("city", true);//设置城市汇总在顶端。
    	privotForge.setLayoutOfSameCol("city", true);//设置城市布局压缩显示。
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,date
     * 列区域字段: prodtype
     * 值区域字段: num,amount
     * 大纲布局：city
     */
    @Test
    public void test2Row1Col1ValOfDICT() {
    	privotForge.addRowField("date","日期");//设置行区域字段（用于纵向扩展)
    	privotForge.addRowField("city", "城市");//设置行区域字段(用于纵向扩展）
    	privotForge.addColField("prodtype","商品类型");//设置列区域字段（用于横向扩展）
    	privotForge.addValField("amount","金额");//设置值区域字段（用于显示数值）
    	String[] dicts = {"北京","广州","深圳"};//只显示三个，因编码与名称相同，可直接使用数组。
    	privotForge.setDict("city", dicts);
    	//如果需要使用key,value方式进行转换的,则使用LinkedHashMap对象列表
    	//LinkedHashMap<String, Object> listDict = MockDataSource.getListDict();
    	//privotForge.setDict("city", listDict);
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
    /**
     * 原始字段：date,city,prodtype,saler,num,price,amount
     * 行区域字段: city,date
     * 列区域字段: prodtype
     * 值区域字段: num,amount
     * 大纲布局：city 汇总在顶端,压缩显示
     */
    @Test
    public void test2Row1Col1ValOfTreeDict() {
    	privotForge.addRowField("city", "城市",Layout.TREE);//设置行区域字段(用于纵向扩展）
    	privotForge.addRowField("prodtype","商品类型");//设置行区域字段（用于纵向扩展)
    	privotForge.addColField("date","日期");//设置列区域字段（用于横向扩展）
    	privotForge.addValField("amount","金额");//设置值区域字段（用于显示数值）
    	privotForge.setLayoutOfToptotal("city", true);//设置城市汇总在顶端。
    	privotForge.setLayoutOfSameCol("city", true);//设置城市布局压缩显示。
    	List<TreeDict> treeDict = MockDataSource.getTreeDict();//获取树型结构字典。
    	privotForge.setTreeDictOfRowPanel(treeDict);//设置行区域的字段为树型结构。
        privotForge.exec();
        List<List<Map<String, Object>>> tableMap = privotForge.outOfTableMap();
        this.printFormat(tableMap);
        List<List<String>> datas = privotForge.outOfTable();
        this.printTable(datas);
        this.mapHtml(tableMap);
        this.tableHtml(datas);
    }
    
}

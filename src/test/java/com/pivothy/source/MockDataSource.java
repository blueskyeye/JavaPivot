package com.pivothy.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.pivothy.data.TreeDict;

public class MockDataSource {

	private static Random random = new Random();
	
	public static void main(String[] args) {
		getList();
		//getMapList();
	}
	
	public static List<TreeDict> getTreeDict() {
		List<TreeDict> roots = new ArrayList<TreeDict>();
		TreeDict<String, String> root = new TreeDict<String, String>("广州","广州");
		root.addChild("表姐", "表姐");
		root.addChild("表哥", "表哥");
		root.addChild("堂姐", "堂姐");
		roots.add(root);
		root = new TreeDict<String, String>("上海","上海");
		root.addChild("表姐", "表姐");
		root.addChild("堂兄", "堂兄");
		root.addChild("朋友", "朋友");
		roots.add(root);
		root = new TreeDict<String, String>("北京","北京");
		root.addChild("堂兄", "堂兄");
		root.addChild("朋友", "朋友");
		root.addChild("表姐", "表姐");
		roots.add(root);
		root = new TreeDict<String, String>("深圳","深圳");
		root.addChild("堂兄", "堂兄");
		root.addChild("表姐", "表姐");
		roots.add(root);		
		return roots;
	}
	
	
	public static List<Map<String,Object>> getList(){
		List<Map<String, Object>> data = new ArrayList<>();
        String[] cities = {"广州", "上海", "北京", "深圳", "成都"};
        String[] productTypes = {"纯棉口罩", "医用口罩", "N95口罩", "防护服", "消毒液"};
        String[] salers = {"表姐", "表哥", "堂姐", "堂兄", "朋友"};

        // 假设我们想要生成5个月的数据，每个月10条记录
        int recordsPerMonth = 10;
        for (int month = 1; month <= 5; month++) {
            for (int day = 1; day <= recordsPerMonth; day++) {
                Map<String, Object> record = new HashMap<>();
                int cityIndex = (month + day) % cities.length;
                int productTypeIndex = (month + day + 1) % productTypes.length;
                int salerIndex = (month + day + 2) % salers.length;

                // 生成日期，格式为 "2020/x/y"
                String date = "2020/" + month ;
                
                record.put("date", date); // 日期，从2020/1/1到2020/5/10
                record.put("city", cities[cityIndex]); // 城市
                record.put("prodtype", productTypes[productTypeIndex]); // 产品类别
                record.put("saler", salers[salerIndex]); // 销售员
                record.put("num", 6); // 数量，这里固定为6
                record.put("price", 1); // 单价，这里固定为1
                record.put("amount", 6); // 金额，这里是数量乘以单价
                data.add(record);
            }
        }

        // 打印生成的数据以供检查
        for (Map<String, Object> record : data) {
            System.out.println(record);
        }
        
        return data;
	}
	
	public static List<Map<String, Object>> getMapList() {
		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			Map<String, Object> record = new HashMap<>();
			record.put("date", generateRandomDate());
			record.put("city", generateRandomCity());
			record.put("prodtype", generateRandomProductType());
			record.put("saler", generateRandomSaler());
			record.put("num", generateRandomNum());
			record.put("price", generateRandomPrice());
			record.put("amount", generateRandomAmount((int)record.get("num"), (int)record.get("price")));
			data.add(record);
		}
		// 打印生成的数据以供检查
		for (Map<String, Object> record : data) {
			System.out.println(record);
		}
		return data;
	}

	private static String generateRandomDate() {
		// 随机生成一个日期字符串，格式为 "2020/x/x"
		return "2020/" + (random.nextInt(12) + 1) + "/" + (random.nextInt(28) + 1);
	}

	private static String generateRandomCity() {
		// 随机选择一个城市
		String[] cities = { "广州", "上海", "北京", "深圳", "成都" };
		return cities[random.nextInt(cities.length)];
	}

	private static String generateRandomProductType() {
		// 随机选择一个产品类别
		String[] productTypes = { "纯棉口罩", "医用口罩", "N95口罩", "防护服", "消毒液" };
		return productTypes[random.nextInt(productTypes.length)];
	}

	private static String generateRandomSaler() {
		// 随机选择一个销售员
		String[] salers = { "王大刀", "张盛", "赵小平", "凌祯", "表姐" };
		return salers[random.nextInt(salers.length)];
	}

	private static int generateRandomNum() {
		// 随机生成一个数量，范围在1到10之间
		return random.nextInt(10) + 1;
	}

	private static int generateRandomPrice() {
		// 随机生成一个价格，范围在1到5之间
		return random.nextInt(5) + 1;
	}

	private static int generateRandomAmount(int num, int price) {
		// 根据数量和价格计算金额
		return num * price;
	}

}

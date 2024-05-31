package com.pivothy.report.tool;

import org.junit.Test;

import com.pivothy.report.tool.StrUtil;

public class StrUtilTest {
	@Test
    public void testIsExist_True() {
		double number = 1233345.6789;
		String num ="1233345.6789"; 
        String pattern = "#,##0.00"; // 使用通配符定义格式

        String formattedNumber = StrUtil.formatNumber(number, pattern);
        System.out.println(formattedNumber); // 输出: 12,345.68
    }
}

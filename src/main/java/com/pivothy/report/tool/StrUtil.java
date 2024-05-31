package com.pivothy.report.tool;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

/**
 *
 *
 * @author shihy
 */
public class StrUtil {
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	

	public static String getStrValue(Map<String, Object> map, String key) {
		return getObj(map,key)==null ? "" : map.get(key).toString();
	}
	
	public static String getStr(Map<String, Object> map, String key) {
		return getStrValue(map,key);
	}
	
	public static Object getObj(Map<String, Object> map, String key) {
		return map==null? null: map.get(key) == null ? null :map.get(key);
	}
	
	public static double getDouble(Map<String, Object> map, String key) {
		return getObj(map,key)==null ? null :Double.parseDouble(map.get(key).toString());
	}
	
	public static int getInt(Map<String,Object> map,String key) {
		return getObj(map,key)==null ? null :Integer.parseInt(map.get(key).toString());
	}

	public static String toStr(Object value, String defaultValue) {
		if (null == value) {
			return defaultValue;
		}
		if (value instanceof String) {
			return (String) value;
		}
		return value.toString();
	}

	public static Number toNumber(Object value) {
		return toNumber(value, null);
	}

	/**
	 * 转换为Number<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value        被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Number toNumber(Object value, Number defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return (Number) value;
		}
		final String valueStr = toStr(value, null);
		if (isEmpty(valueStr)) {
			return defaultValue;
		}
		try {
			return NumberFormat.getInstance().parse(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	   * 转换为double<br>
	   * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
	   * 转换失败不会报错
	   *
	   * @param value 被转换的值
	   * @return 结果
	   */
	  public static Double toDouble(Object value) {
	    return toDouble(value, null);
	  }

	
	public static Double toDouble(Object value, Double defaultValue) {
	    if (value == null) {
	      return defaultValue;
	    }
	    if (value instanceof Double) {
	      return (Double) value;
	    }
	    if (value instanceof Number) {
	      return ((Number) value).doubleValue();
	    }
	    final String valueStr = toStr(value, null);
	    if (isEmpty(valueStr)) {
	      return defaultValue;
	    }
	    try {
	      // 支持科学计数法
	      return new BigDecimal(valueStr.trim()).doubleValue();
	    } catch (Exception e) {
	      return defaultValue;
	    }
	  }
	
	public static String formatNumber(double number, String pattern) {
		// 将通配符替换为DecimalFormat可以理解的模式
        String decimalPattern = pattern.replaceAll("#", "0").replace("X", "#");
        DecimalFormat decimalFormat = new DecimalFormat(decimalPattern);
        // 格式化数字
        String formattedNumber="";
		try {
			formattedNumber = decimalFormat.format(number);
		} catch (Exception e) {
			formattedNumber="";
		}
        // 如果需要，可以在这里将模式中的逗号替换回普通的逗号
        // 由于我们没有在模式中使用逗号，这一步在这里不是必需的
        return formattedNumber;
    }

}

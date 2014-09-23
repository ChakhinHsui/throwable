package com.seava.throwable.thrift.utils;

/**
 * 安全类型转换类
 */
public class Converts {

	/**
	 * 安全地将 str 转换为 int，转换失败时会返回缺省值
	 */
	public static int strToInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return defaultValue;
		}
	}
	
}

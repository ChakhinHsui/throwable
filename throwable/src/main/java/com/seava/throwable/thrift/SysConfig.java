package com.seava.throwable.thrift;

import org.nutz.json.JsonFormat;

/**
 * @author WaterHsu@xiu8.com
 * @version 2014年9月23日
 */
public class SysConfig {
	public static int port = 9160;
	
	public static int handleTimeout = 5;
	
	public static int handlerThreadPoolSize = 512;
	
	
	/**
	 * 监控开启状态，默认关闭
	 */
	public static boolean profileOpen = false;
	
	/**
	 * 监控的打印时间周期，单位：秒
	 */
	public static int profileLoop = 60;
	
	/**
	 * Json串公用格式
	 */
	public static JsonFormat jsonFormat = JsonFormat.compact();
}

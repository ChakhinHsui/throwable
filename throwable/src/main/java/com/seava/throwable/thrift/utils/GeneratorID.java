package com.seava.throwable.thrift.utils;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 定单ID生成器
 * <p>
 * 规则： 2位年月日时分秒 + 服务器IP取后3位 + 流水号（过1000从1开始）
 * </p>
 * 
 */
public class GeneratorID {

	/**
	 * 获取流水号
	 */
	public static String getID() {
		StringBuffer sbs = new StringBuffer(sFormat.format(new Date()));
		sbs.append(IP);
		synchronized (sep) {
			long inc = sep.addAndGet(1);
			if (inc >= 9999) {
				sep = new AtomicLong();
			}
			sbs.append(alignRight(inc, 4));
		}
		return sbs.toString();
	}

	private static SimpleDateFormat sFormat = new SimpleDateFormat("yyMMddHHmmss");
	private static AtomicLong sep = new AtomicLong();
	private static final String IP;
	static {
		int ipadd;
		try {
			ipadd = toInt(InetAddress.getLocalHost().getAddress());
		} catch (final Exception e) {
			// 如果IP取不到，则取毫秒
			Calendar cal = Calendar.getInstance();
			ipadd = cal.get(Calendar.MILLISECOND);
		}
		IP = alignRight(ipadd, 3);
	}

	// 取IP地址后两位
	private static int toInt(final byte[] bytes) {
		int result = 0;
		for (int i = 3; i < 4; i++) {
			result = (result << 8) - Byte.MIN_VALUE + bytes[i];
		}
		return result;
	}

	/**
	 * 字符串右对齐
	 */
	private static String alignRight(long src, int len) {
		StringBuffer sbs = new StringBuffer();
		sbs.append(src);
		while (sbs.length() < len) {
			sbs.insert(0, "0");
		}
		return sbs.toString();
	}

//	public static void main(String[] args) {
//		for (int i = 0; i < 100000; i++) {
//			System.out.println(getID());
//		}
//	}
}

package com.seava.throwable.thrift.profile;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 性能分析线程
 * 
 */
public class StatisticsThread extends Thread {

	private static Statistics stats = new Statistics();
	private static Log log = Logs.get();
	private static boolean runFirst = true;

	/**
	 * 获取当前分析对象
	 */
	public static Statistics get() {
		return stats;
	}

	/**
	 * 每隔一段时间打印一次性能跟踪信息，并重新开始计数
	 */
	@Override
	public void run() {
		if (runFirst) {
			runFirst = false;
			return;
		}

		// 打印性能跟踪信息
		if (get().size() > 0) 
			log.info(stats.toString());

		//重新生成跟踪信息
		stats = new Statistics();
	}
}

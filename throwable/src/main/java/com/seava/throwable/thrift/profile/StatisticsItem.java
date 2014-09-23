package com.seava.throwable.thrift.profile;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控项
 * 
 */
public class StatisticsItem {
	//总处理次数
	public AtomicLong times = new AtomicLong();
	//处理总耗时
	public AtomicLong used = new AtomicLong();
	//最大处理时长
	public long maxUsed;
	//最小处理时长
	public long minUsed;

	//平均处理时长
	public long average() {
		long currTime = times.get();
		if (currTime <= 0)
			return used.get();
		else
			return used.get() / currTime;
	}

	@Override
	public String toString() {
		return String.format("|%-15d|%-15d|%-15d|%-15d|%-15d", times.get(), used.get(), average(), maxUsed, minUsed);
	}

}

package com.seava.throwable.thrift.profile;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.lang.Times;

/**
 * 性能分析
 * 
 */
public class Statistics {
	//监控的URL信息
	private Map<String, StatisticsItem> uriMap = new ConcurrentHashMap<String, StatisticsItem>();
	//监控开始时间
	private Date beginTime = new Date();

	//增加一次访问
	public void statistics(String uri, long used) {
		StatisticsItem item = uriMap.get(uri);
		if (null == item) {
			item = new StatisticsItem();
			uriMap.put(uri, item);
		}
		item.times.addAndGet(1);
		item.used.addAndGet(used);
		if (item.maxUsed < used)
			item.maxUsed = used;
		if (item.minUsed > used || item.minUsed == 0)
			item.minUsed = used;
	}
	
	public int size() {
		return uriMap.size();
	}

	@Override
	public String toString() {
		StringBuffer sbs = new StringBuffer("Statistics from ");
		sbs.append(Times.sDTms(beginTime)).append(" - ").append(Times.sDTms(new Date())).append(" : \n");
		sbs.append(String.format("|%-30s|%-15s|%-15s|%-15s|%-15s|%-15s", "requestUrl", "times", "used(ms)", "avg used(ms)", "max used", "min used"));
		sbs.append("\n==============================================================================================================");
		for (Map.Entry<String, StatisticsItem> item : uriMap.entrySet()) {
			sbs.append("\n").append(String.format("|%-30s%s", item.getKey(), item.getValue().toString()));
		}
		sbs.append("\n--------------------------------------------------------------------------------------------------------------");
		return sbs.toString();
	}

}

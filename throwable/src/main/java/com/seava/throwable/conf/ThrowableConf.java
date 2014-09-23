/**
 * Studia Seava
 * 
 * created in 2014年9月24日
 *
 * author by WaterHsu
 * 
 */
package com.seava.throwable.conf;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author WaterHsu
 *
 */
@IocBean
public class ThrowableConf {
	
	@Inject
	private Dao dao;
	
	private Log log = Logs.get();
	private long sleepTime;
	private static Ioc ioc;
	public static PropertiesProxy appProp = new PropertiesProxy();
	public static PropertiesProxy errorMsg = new PropertiesProxy();
	
	public void run(Ioc ioc){
		initPro(ioc);
		while(true){
			init();
			sleep();
		}
	}
	
	private void initPro(Ioc ioc){
		ThrowableConf.ioc = ioc;
		appProp.setPaths(new String[]{"application.properties"});
		errorMsg.setPaths(new String[]{"errorMsg.properties"});
		sleepTime = appProp.getLong("system.sleepTime",120)*1000L;
	}
	
	private void init(){
	
	}
	
	private void sleep(){
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			log.error("配置循环sleep出错。\r\n"+e.getLocalizedMessage());
		}
	}
}

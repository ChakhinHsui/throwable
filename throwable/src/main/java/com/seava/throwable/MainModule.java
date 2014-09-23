package com.seava.throwable;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

import com.seava.throwable.conf.ThrowableConf;

/**
 * 主模块声明
 * @author WaterHsu@xiu8.com
 * @version 2014年9月23日
 */
@Modules(scanPackage = true)
@IocBy(type = ComboIocProvider.class, args={ "*org.nutz.ioc.loader.json.JsonLoader",
											 "application.js",
											 "*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
											 "com.seava.throwable"
})
@SetupBy(MainModule.class)
@ChainBy(args={"com/seava/throwable/thrift/mvc/mvc-chains.js"})
public class MainModule implements Setup, Runnable {
	
	private Log log = Logs.getLog("MainModule");
	private Ioc ioc;
	
	public void init(NutConfig config){
		ioc = config.getIoc();
		Thread t = new Thread(this);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}
	
	public void destroy(NutConfig config){
		
	}
	
	public void run(){
		try{
			ioc.get(Dao.class, "dao");
			log.debug("数据库初始化成功");
			ioc.get(ThrowableConf.class).run(ioc);
		}catch(Exception e){
			log.fatal("数据库连接初始化失败 !!!", e);
		}
	}
}

/**
 * Studia Seava
 * 
 * created in 2014年9月24日
 *
 * author by WaterHsu
 * 
 */
package com.seava.throwable;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Tasks;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.seava.throwable.thrift.SysConfig;
import com.seava.throwable.thrift.ThrowableServiceImpl;
import com.seava.throwable.thrift.mvc.RPCDispatcher;
import com.seava.throwable.thrift.profile.StatisticsThread;
import com.seava.throwable.thrift.rpc.ThrowableService;
import com.seava.throwable.thrift.utils.Converts;

/**
 * @author WaterHsu
 *
 */
public class ThrowableMain {
	
	//日志
	private static Log log = Logs.getLog("Main");
	private static Stopwatch sw;
	private static RPCDispatcher dispatcher;
	
	public static void main(String[] args){
		sw = Stopwatch.begin();
		initParam(args);
		dispatcher = new RPCDispatcher();
		dispatcher.init();
		
		//是否开启性能分析
		if (SysConfig.profileOpen)
			Tasks.scheduleAtFixedRate(new StatisticsThread(), SysConfig.profileLoop);

		// 启动监听服务
		start();
	}
	
	public static void initParam(String[] args){
		try{
			PropertiesProxy pp = new PropertiesProxy();
			pp.setPaths(new String[] { "application.properties" });

			// 检查参数中是否传入端口号
			int port = 0;
			if (args.length > 0) {
				port = Converts.strToInt(args[0], 0);
			}
			// 如果传入端口号，则以该端口号优先
			if (port != 0) {
				SysConfig.port = port;
			} else {
				SysConfig.port = pp.getInt("socket.port", 9160);
			}
			// 处理的最长超时时间
			SysConfig.handleTimeout = pp.getInt("handler.timeout", 5);
			SysConfig.handlerThreadPoolSize = pp.getInt("handler.thread_pool_size", 512);

			//是否开启性能分析
			if ("true".equalsIgnoreCase(pp.getTrim("profile.open", "false")))
				SysConfig.profileOpen = true;
			int loop = pp.getInt("profile.loop", 60);
			if (loop > 0)
				SysConfig.profileLoop = loop;
		}catch(Exception e){
			log.warn("application.properties not find!!! use default setting.");
		}
	}
	
	/**
	 * 启动监听
	 */
	private static void start(){
		TNonblockingServerTransport serverTransport = null;
		try{
			serverTransport = new TNonblockingServerSocket(SysConfig.port);
		}catch(Exception e){
			log.error("************************************************************************");
			log.error(" Throwable Service is run fail!", e);
			log.error("************************************************************************");
			return;
		}
		
		ThrowableService.Processor<ThrowableService.Iface> processor = new ThrowableService.Processor<ThrowableService.Iface>(new ThrowableServiceImpl(dispatcher));
		TCompactProtocol.Factory protFactory = new TCompactProtocol.Factory();
		
		// 多线程 non blocking的服务
		THsHaServer.Args args = new THsHaServer.Args(serverTransport);
		args.processor(processor);
		args.protocolFactory(protFactory);
		TServer server = new THsHaServer(args);
		
		// 计时结束
				sw.stop();
				log.info("========================================================================");
				log.infof(" Throwable Service is up in %s ms, port: %d, profile: %s !", sw.getDuration(), SysConfig.port, SysConfig.profileOpen);
				log.info("========================================================================");
				server.serve();
	}
}

/**
 * Studia Seava
 * 
 * created in 2014年9月24日
 *
 * author by WaterHsu
 * 
 */
package com.seava.throwable.thrift;

import java.util.Map;

import org.apache.thrift.TException;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.seava.throwable.thrift.mvc.RPCDispatcher;
import com.seava.throwable.thrift.rpc.BackCode;
import com.seava.throwable.thrift.rpc.BackMSG;
import com.seava.throwable.thrift.rpc.ThrowableService;

/**
 * @author WaterHsu
 *
 */
public class ThrowableServiceImpl implements ThrowableService.Iface {

	private static Log log = Logs.get();
	//将RPC转化为MVC调度服务
	private RPCDispatcher dispatcher;
	
	public ThrowableServiceImpl(RPCDispatcher dispatcher){
		this.dispatcher = dispatcher;
	}
	
	/**
	 * Client 同步调用的具体方法
	 */
	@Override
	public BackMSG call(String rpid, String funCode,
			Map<String, String> paramMap) throws TException {
		
		Stopwatch sw = Stopwatch.begin();
		if(log.isInfoEnabled()){
			log.infof("%s receive:[%s] %s", rpid, funCode, paramMap);
		}
		BackMSG backMsg = null;
		
		// 如果是批量接口处理，则特殊化处理
		if("/batch".equals(funCode)){
			
		}
		else{
			// 否则正常处理
			try{
				backMsg = dispatcher.dispatch(rpid, funCode, paramMap);
			}catch(Exception e){
				log.errorf("%s error: %s", rpid, e.getStackTrace());
				backMsg = new BackMSG();
				backMsg.backCode = BackCode.SERVERE_RROR;
				backMsg.msg = e.getLocalizedMessage();
			}
		}
		
		sw.stop();
		
		if(SysConfig.profileOpen){
			
		}
		
		if(log.isInfoEnabled()){
			log.infof("%s return: [%s] (T:%s) %s", rpid, funCode, sw.getDuration(), backMsg);
		}
		return backMsg;
	}

	/**
	 * Client 异步调用的具体方法
	 */
	@Override
	public void asyCall(String rpid, String funCode,
			Map<String, String> paramMap) throws TException {
		if(log.isInfoEnabled()){
			log.infof("%s receive:[%s] %s", rpid, funCode, paramMap);
		}
		BackMSG backMsg = null;
		
		// 如果是批量接口处理，则特殊化处理
		if("/batch".equals(funCode)){
			
		}
		else{
			backMsg = dispatcher.dispatch(rpid, funCode, paramMap);
		}
		
		if(log.isInfoEnabled()){
			log.infof("%s return: [%s]  %s", rpid, funCode, backMsg);
		}
	}	
}

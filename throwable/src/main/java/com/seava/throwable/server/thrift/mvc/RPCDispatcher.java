package com.seava.throwable.server.thrift.mvc;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;





import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletResponse;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.NutServlet;

import com.seava.throwable.server.thrift.SysConfig;
import com.seava.throwable.server.thrift.rpc.BackMSG;

/**
 * @author WaterHsu@xiu8.com
 * @version 2014年9月23日
 */
public class RPCDispatcher {
	private Log log = Logs.get();
	private ExecutorService exec = Executors.newFixedThreadPool(SysConfig.handlerThreadPoolSize);
	
	protected NutServlet servlet = new NutServlet();
	protected MockServletContext servletContext = new MockServletContext();
	protected MockHttpServletResponse response = new MockHttpServletResponse();
	protected MockHttpSession session = Mock.servlet.session(servletContext);
	
	/**
	 * 系统初始化
	 */
	public void init(){
		MockServletConfig servletConfig = new MockServletConfig(servletContext, "seava");
		//指定主模块
		servletConfig.addInitParameter("modules", "com.seava.throwable.MainModule");
		try{
			servlet.init(servletConfig);
		}catch(ServletException e){
			log.error(e);
		}
	}
	
	public BackMSG dispatch(String rpid, String funCode, Map<String, String> param){
		BackMSG backMsg = new BackMSG();
		backMsg.setMsg("OK");
		
		ThriftRequest request = new ThriftRequest();
		request.setSession(session);
		request.setPathInfo(funCode);
		if(null != param){
			request.setAttribute(param);
		}
		
		try{
			Future<Boolean> task = exec.submit(new ServiceRunCallable(request));
			task.get(SysConfig.handleTimeout, TimeUnit.SECONDS);
		}catch(TimeoutException ex){
			log.warnf("%s 任务执行超时! funCode:%s, param: %s", rpid, funCode, param);
			ret.setRetCode(ResultCode.SERVER_ERROR);
			ret.retMsg = "TimeoutException";
			return ret;
		}catch(Exception e){
			Throwable t = e.getCause().getCause();
			if (t instanceof SystemError) {
				ret.retCode = ((SystemError) t).getRetcode();
			} else {
				ret.retCode = ResultCode.SERVER_ERROR;
			}
			String error = t == null ? e.getMessage() : t.getMessage();
			ret.retMsg = error;
			return ret;
		}
		
		return backMsg;
	}
	
	class ServiceRunCallable implements Callable<Boolean>{
		private ThriftRequest request;
		
		public ServiceRunCallable(ThriftRequest request) {
			this.request = request;
		}
		
		public Boolean call() throws Exception{
			servlet.service(request, response);
			return true;
		}
	}
}

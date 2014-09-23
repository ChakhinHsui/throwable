package com.seava.throwable.thrift.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;





import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;

import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletResponse;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.NutServlet;

import com.seava.throwable.thrift.SysConfig;
import com.seava.throwable.thrift.rpc.BackCode;
import com.seava.throwable.thrift.rpc.BackMSG;

/**
 * @author WaterHsu@xiu8.com
 * @version 2014年9月23日
 */
@SuppressWarnings("unchecked")
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
			backMsg.setBackCode(BackCode.SERVERE_RROR);
			backMsg.msg = "TimeoutException";
			return backMsg;
		}catch(Exception e){
			Throwable t = e.getCause().getCause();
			if (t instanceof SystemError) {
				backMsg.backCode = ((SystemError) t).getBackCode();
			} else {
				backMsg.backCode = BackCode.SERVERE_RROR;
			}
			String error = t == null ? e.getMessage() : t.getMessage();
			backMsg.msg = error;
			return backMsg;
		}
		// 如果没有运行过的标志，则表示该请求未定义
		if(null == request.getAttribute("funFlag")){
			backMsg.backCode = BackCode.NOT_DEFINED;
			backMsg.msg = "该请求未定义";
			return backMsg;
		}
		
		// 如果运行中发生了错误
		if(null != request.getAttribute("err")){
			backMsg.backCode = BackCode.SERVERE_RROR;
			Throwable e = (Throwable)request.getAttribute("err");
			if(e instanceof FailToCastObjectException){
				backMsg.backCode = BackCode.PARAME_RROR;
				backMsg.msg = e.getMessage();
			}
			else if(e.getCause() != null && e.getCause() instanceof SystemError){
				SystemError se = (SystemError)e.getCause();
				backMsg.msg = se.getMessage();
				backMsg.setBackCode(se.getBackCode());
				backMsg.setErrorCode(se.getErrorCode());
				log.errorf("发生错误 -> errorCode: %s, msg: %s ", se.getErrorCode(), backMsg.msg);
			}
			else{
				backMsg.msg = e.getMessage();
				log.error("系统错误-> " + backMsg.msg, e);
			}
			
			return backMsg;
		}
		
		//将mvc结果转换为ResultMsg结构
		Object obj = request.getAttribute("obj");
		if(null == obj){
			backMsg.setMsg("OK");
			return backMsg;
		}
		else if(obj instanceof String){
			backMsg.setMsg((String)obj);
			return backMsg;
		}
		else if(obj instanceof Number){
			backMsg.setMsg(obj.toString());
			return backMsg;
		}
		else if(obj instanceof BackMSG){
			return (BackMSG)obj;
		}
		else if(obj instanceof Map){
			Map<String, String> map = map2map((Map<String, Object>) obj);
			backMsg.setBackMap(map);
			return backMsg;
		}
		else if(obj instanceof List){
			backMsg.setBackList(list2list((List<Object>) obj));
			return backMsg;
		}
		else{
			Map<String, String> map = map2map(Lang.obj2map(obj));
			backMsg.setBackMap(map);
			return backMsg;
		}
	}
	
	
	private Map<String, String> map2map(Map<String, Object> map){
		Map<String, String> ret = new HashMap<String, String>();
		for(Entry<?, ?> e : map.entrySet()){
			String key = e.getKey().toString();
			String value = "";
			try{
				Object ovalue = e.getValue();
				if(null == ovalue){
					continue;
				}
				if(ovalue instanceof String || ovalue instanceof Number){
					value = ovalue.toString();
				}
				else{
					value = Json.toJson(ovalue, SysConfig.jsonFormat);
				}
			}catch(Exception ex){
				log.warn("map2map error!" + value + "\n" + ex.getLocalizedMessage());
			}
			ret.put(key, value);
		}
		
		return ret;
	}
	
	
	private List<String> list2list(List<Object> list){
		List<String> ret = new ArrayList<String>(list.size());
		for(Object obj : list){
			if(obj == null){
				continue;
			}
			try{
				if(obj instanceof String){
					ret.add(obj.toString());
				}else{
					ret.add(Json.toJson(obj, SysConfig.jsonFormat));
				}
			}catch(Exception e){
				log.warn("list2list error !" + obj + "\n" + e.getLocalizedMessage());
			}
		}
		return ret;
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

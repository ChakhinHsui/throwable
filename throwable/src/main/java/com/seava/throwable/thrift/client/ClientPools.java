package com.seava.throwable.thrift.client;

import java.net.SocketException;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TTransportException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.seava.throwable.thrift.rpc.BackMSG;


/**
 * thrift 连接池封装
 * 
 */
public class ClientPools {
	//连接池
	private GenericObjectPool<ThriftConnect> pool;
	//服务地址
	private String host;
	//服务端口
	private int port;

	private Log log = Logs.get();

	/**
	 * 创建连接池
	 * 
	 * @param host
	 *            服务地址
	 * @param port
	 *            服务端口
	 */
	public ClientPools(String host, int port) {
		if (null == host || port == 0) {
			log.fatal("thrift 连接池初始化失败，未指定host或port");
			return;
		}
		log.infof("Thrift连接池初始化-->host: %s, post: %d", host, port);
		this.host = host;
		this.port = port;

		// 连接超时时间设为 10 秒
		ThriftClientFactory factory = new ThriftClientFactory(host, port, 10 * 1000);
		pool = new GenericObjectPool<ThriftConnect>(factory);
		pool.setMaxActive(100); //默认最大100个连接
		pool.setMaxIdle(5);
		pool.setMinIdle(1);
		pool.setMaxWait(10 * 1000); //获取对象的等待时间 
		pool.setMinEvictableIdleTimeMillis(10 * 60 * 1000); //对象过期时间 10分钟
		pool.setTimeBetweenEvictionRunsMillis(30 * 1000);//检测线程执行时间间隔 30秒
		pool.setSoftMinEvictableIdleTimeMillis(60 * 1000); //设置对象最小空闲时间
		pool.setTestOnBorrow(false);
		pool.setTestWhileIdle(true);
	}

	/**
	 * 调用服务器接口
	 * 
	 * @param rpid
	 *            请求唯一ID
	 * @param funCode
	 *            请求接口地址
	 * @param uid
	 *            用户ID，没有时传 0
	 * @param paramMap
	 *            参数
	 * @return 处理结果
	 */
	public BackMSG call(String rpid, String funCode, Map<String, String> paramMap) throws Exception {
		return retryCall(rpid, funCode, paramMap, true);
	}

	//如果是连接失败的错误时，是否重试一次
	private BackMSG retryCall(String rpid, String funCode, Map<String, String> paramMap, boolean retry) throws Exception {
		if (log.isInfoEnabled())
			log.infof("%s call:[%s] %s", rpid, funCode, Json.toJson(paramMap, JsonFormat.compact().setQuoteName(false)));

		ThriftConnect conn = null;
		Stopwatch sw = Stopwatch.begin();
		try {
			conn = pool.borrowObject();
			BackMSG ret = conn.getClient().call(rpid, funCode, paramMap);

			sw.stop();
			if (log.isInfoEnabled())
				log.infof("%s return:[%s](T:%s) %s", rpid, funCode, sw.getDuration(), ret);

			pool.setTestOnReturn(false);
			pool.returnObject(conn);

			return ret;
		} catch (SocketException se) {
			log.errorf("%s funCode=%s socketConnection error! %s", rpid, funCode, se.getLocalizedMessage());
			pool.setTestOnReturn(true);
			pool.invalidateObject(conn);
			//如果是连接失败的问题时，则重试一次
			if (retry)
				return retryCall(rpid, funCode, paramMap, false);
			throw se;
		} catch (TTransportException ce) {
			log.errorf("%s funCode=%s TransportConnection error! %s", rpid, funCode, ce.getLocalizedMessage());
			pool.setTestOnReturn(true);
			pool.invalidateObject(conn);
			//如果是连接失败的问题时，则重试一次
			if (retry)
				return retryCall(rpid, funCode, paramMap, false);
			throw ce;
		} catch (Exception e) {
			log.errorf("%s funCode=%s error! %s", rpid, funCode, e.getMessage());
			pool.setTestOnReturn(false);
			pool.returnObject(conn);
			throw e;
		}
	}

	/**
	 * 最小空闲连接
	 * 
	 * @param minIdle
	 */
	public void setMinIdle(int minIdle) {
		pool.setMinIdle(minIdle);
	}

	/**
	 * 最大空闲连接
	 * 
	 * @param maxIdle
	 */
	public void setMaxIdle(int maxIdle) {
		pool.setMaxIdle(maxIdle);
	}

	/**
	 * 最大激活连接数
	 * 
	 * @param maxActive
	 */
	public void setMaxActive(int maxActive) {
		pool.setMaxActive(maxActive);
	}

	@Override
	public String toString() {
		return "ClientPools [host=" + host + ", port=" + port + "]";
	}

}

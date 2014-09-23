package com.seava.throwable.thrift.client;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Thrift连接池工厂
 * 
 */
public class ThriftClientFactory extends BasePoolableObjectFactory<ThriftConnect> {
	protected String host;
	protected int port;
	protected int timeout;
	private final int RE_COUNT = 10; //尝试获取连接的次数 
	private Log log = Logs.get();

	public ThriftClientFactory(String host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	public ThriftConnect makeObject() throws Exception {
		// 尝试获取 n 次，防止连接不上或错误时发生问题
		for (int i = 0; i < RE_COUNT; i++) {
			ThriftConnect conn = new ThriftConnect(host, port);
			if (validateObject(conn)) { //检验合格的才返回
				log.info("获取thrift连接成功");
				return conn;
			}
			destroyObject(conn);
		}
		//log.warnf("尝试获取 %d次连接均失败!", RE_COUNT);
		//如果n次都不成功的话，最后再创建一个返回
		return new ThriftConnect(host, port);
	}

	public boolean validateObject(ThriftConnect conn) {
		try {
			if (null != conn && conn.isOpen()) {
				long pingTime = System.currentTimeMillis();
				if (pingTime > 0)
					return true;
			} 
			//else
				//log.info("thrift pool validate conn isClose.");
		} catch (Exception e) {
			//log.errorf("thrift pool validate conn error! %s", e.getMessage());
		}
		return false;
	}

	@Override
	public void destroyObject(ThriftConnect conn) throws Exception {
		Streams.safeClose(conn);
	}

}

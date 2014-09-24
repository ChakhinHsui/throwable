/**
 * Studia Seava
 * 
 * created in 2014年9月24日
 *
 * author by WaterHsu
 * 
 */
package com.seava.throwable;

import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.junit.Before;

import com.seava.throwable.thrift.client.ThriftClientFactory;
import com.seava.throwable.thrift.client.ThriftConnect;
import com.seava.throwable.thrift.rpc.BackMSG;

/**
 * @author WaterHsu
 *
 */
public class AbstractThriftController {

	private String host = "localhost";
	private int port = 9168;
	
	private static GenericObjectPool<ThriftConnect> pool;
	
	@Before
	public void setUp() throws Exception{
		ThriftClientFactory factory = new ThriftClientFactory(host, port, 10 * 1000);
		pool = new  GenericObjectPool<ThriftConnect>(factory);
		pool.setMinIdle(5);
		pool.setMaxIdle(50);
		pool.setMaxActive(100);
	}
	
	public BackMSG call(String rpid, String funCode, Map<String, String> paramMap) throws org.apache.thrift.TException{
		ThriftConnect conn = null;
		
		try{
			conn = pool.borrowObject();
			BackMSG ret = conn.getClient().call(rpid, funCode, paramMap);
			
			return ret;
		}catch(Exception e){
			
		}finally{
			try {
				pool.returnObject(conn);
			} catch (Exception e) {
			}
		}
		
		return null;
	}
}

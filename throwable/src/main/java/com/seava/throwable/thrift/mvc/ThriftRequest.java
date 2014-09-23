package com.seava.throwable.thrift.mvc;

import java.util.Map;
import java.util.Map.Entry;

import org.nutz.mock.servlet.MockHttpServletRequest;

/**
 * @author WaterHsu@xiu8.com
 * @version 2014年9月23日
 */
public class ThriftRequest extends MockHttpServletRequest {
	public void setAttribute(Map<String, String> paramMap){
		for(Entry<String, String> e : paramMap.entrySet()){
			setParameter(e.getKey(), e.getValue());
		}
	}
}

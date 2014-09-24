/**
 * 秀吧网络科技有限公司版权所有
 * Copyright (C) xiu8 Corporation. All Rights Reserved
 */
package com.seava.throwable.user;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.seava.throwable.AbstractThriftController;
import com.seava.throwable.thrift.utils.GeneratorID;

/**
 * @author WaterHsu@xiu8.com
 * @version 2014年9月24日
 */
public class UserControllerTest extends AbstractThriftController {

	@Test
	public void login() throws Exception{
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("username", "FFFFF");
		paramMap.put("password", "GGGGGG");
		
		call(GeneratorID.getID(), "/user/login", paramMap);
	}
}

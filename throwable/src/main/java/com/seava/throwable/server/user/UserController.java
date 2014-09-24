/**
 * 秀吧网络科技有限公司版权所有
 * Copyright (C) xiu8 Corporation. All Rights Reserved
 */
package com.seava.throwable.server.user;

import java.util.HashMap;
import java.util.Map;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;

/**
 * @author WaterHsu@xiu8.com
 * @version 2014年9月24日
 */
@At("/user")
public class UserController {

	@At("/login")
	public Map<String, String> login(@Param("username") String username, @Param("password") String password){
		System.out.println("username: " + username + "   password: " + password);
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("password", password);
		return map;
	}
}

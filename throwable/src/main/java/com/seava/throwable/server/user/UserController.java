/**
 * 秀吧网络科技有限公司版权所有
 * Copyright (C) xiu8 Corporation. All Rights Reserved
 */
package com.seava.throwable.server.user;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;

/**
 * @author WaterHsu@xiu8.com
 * @version 2014年9月24日
 */
@At("/user")
public class UserController {

	@At("/login")
	public void login(@Param("username") String username, @Param("password") String password){
		System.out.println("username: " + username + "   password: " + password);
	}
}

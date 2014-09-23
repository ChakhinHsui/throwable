package com.seava.throwable.thrift.mvc;

import com.seava.throwable.thrift.rpc.BackCode;


/**
 * 服务器系统错误
 * @author WaterHsu
 *
 */
public class SystemError extends Throwable {

	private static final long serialVersionUID = 1L;
	//错误类型
	private BackCode backCode = BackCode.SERVERE_RROR;
	//自定义的错误码
	private String errorCode = "";

	public SystemError(BackCode backCode, String message) {
		super(message);
		this.backCode = backCode;
	}
	
	public SystemError(BackCode backCode, String errorCode, String message) {
		super(message);
		this.backCode = backCode;
		this.errorCode = errorCode;
	}
	
	/**
	 * 自定义错误码
	 * @return
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * 自定义错误码
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * 返回的状态码
	 * @return
	 */
	public BackCode getBackCode() {
		return backCode;
	}

	/**
	 * 返回的状态码
	 */
	public SystemError setBackCode(BackCode backCode) {
		this.backCode = backCode;
		return this;
	}
}

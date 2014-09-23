package com.seava.throwable.thrift.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否需要参数检验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Check {
	
	/**
	 * 缓存时间，单位秒，0 为不缓存
	 */
	boolean value() default true;
}

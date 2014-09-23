package com.seava.throwable.thrift.validation;

import java.lang.reflect.Method;
import java.util.Map;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import com.seava.throwable.thrift.mvc.SystemError;
import com.seava.throwable.thrift.rpc.BackCode;
import com.seava.throwable.thrift.utils.Validations;


/**
 * 可用于 MVC 效验的动作链
 * <p>
 * 要求 action 参数中必须有一个 Errors 类型的参数（不需要使用 Param 声明）。当验证完成后会向这个参数赋值
 * 
 */
public class ValidationProcessor extends AbstractProcessor {

	public void process(ActionContext ac) throws Throwable {
		Method m = ac.getMethod();
		Check check = m.getAnnotation(Check.class);
		
		//如果方法前未加 check  注解，忽略处理，继续其它动作链处理
		if (null != check) {
			for (Object obj: ac.getMethodArgs()) {
				 Map<String, String> errors = Validations.validate(obj);
				//如果发现参数错误，则直接抛出
				 if (null != errors && !errors.isEmpty()) {
					 throw new SystemError(BackCode.PARAME_RROR, errors.toString());
				 }
			}
		}
		
		doNext(ac);
	}
}
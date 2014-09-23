package com.seava.throwable.thrift.mvc;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * 发生错误时的处理器链
 * 
 */
public class ErrorProcessor extends AbstractProcessor {

	@Override
	public void process(ActionContext ac) throws Throwable {
		Throwable err = ac.getError();
		if (null != err) {
			ac.getRequest().setAttribute("runFlag", true);
			if (err instanceof SystemError)
				throw err;
			ac.getRequest().setAttribute("err", err);
		}
		doNext(ac);
	}
}

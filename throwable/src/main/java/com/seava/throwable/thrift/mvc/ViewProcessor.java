package com.seava.throwable.thrift.mvc;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * 视图处理，将返回值写入 request 中.
 * 
 */
public class ViewProcessor extends AbstractProcessor {

	@Override
	public void process(ActionContext ac) {
		// 设置处理完成的标志位
		ac.getRequest().setAttribute("runFlag", true);
		ac.getRequest().setAttribute("obj", ac.getMethodReturn());
	}

}

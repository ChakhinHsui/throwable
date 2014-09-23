var chains = {
	"default" : {
		"ps" : [ 
		        "org.nutz.mvc.impl.processor.ModuleProcessor",
				"org.nutz.mvc.impl.processor.AdaptorProcessor",
				"com.seava.throwable.thrift.validation.ValidationProcessor",
				"org.nutz.mvc.impl.processor.MethodInvokeProcessor",
				"com.seava.throwable.thrift.mvc.ViewProcessor"
		],
		"error" : 'com.seava.throwable.thrift.mvc.ErrorProcessor'
	}
}
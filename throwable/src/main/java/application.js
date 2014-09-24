var ioc = {
	dispatcher: {
		type  : "com.seava.throwable.thrift.mvc.RPCDispatcher",
		events: {create: 'init'}
	},
	config: {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : "conf/"
		}
	},
	dataSource: {
        type: "com.alibaba.druid.pool.DruidDataSource",
        events: {create: 'init', depose: "close"},
        fields: {
        	// 基本属性 url、user、password
            driverClassName: { java: "$config.get('db.master.driver')" },
            url:       { java: "$config.get('db.master.url')" },
            username:  { java: "$config.get('db.master.user')" },
            password:  { java: "$config.get('db.master.pwd')" },
            // 配置初始化大小、最小、最大
            initialSize: "1",
            maxActive: { java: "$config.get('db.master.maxActive')" },
            maxIdle:   { java: "$config.get('db.master.maxIdle')" },
            
            // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            timeBetweenEvictionRunsMillis: "3600000",
            minEvictableIdleTimeMillis: "3600000",
            
            // 配置获取连接等待超时的时间
            maxWait: "60000",

            // 配置一个连接在池中最小生存的时间，单位是毫秒
            minEvictableIdleTimeMillis: "300000",
            defaultAutoCommit: "false",
            testWhileIdle: "true",
            testOnBorrow: "false",
            testOnReturn: "false",
            
            //打开removeAbandoned功能, 用来关闭长时间不使用的连接, 超时时间为 秒
            removeAbandoned: "true",
            removeAbandonedTimeout: "120",
            
            // 打开PSCache，并且指定每个连接上PSCache的大小
            poolPreparedStatements: "true",
            maxPoolPreparedStatementPerConnectionSize: "20",
            
            // 配置监控统计拦截的filters    wall,
             filters: "wall,stat"
        }
	},
	sqlManager: {
		type: "org.nutz.dao.impl.FileSqlManager",
		args: ["sqls.sql"]
	}, 
	// 主库 DAO
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [ {
			refer : "dataSource"
		}, {
			refer : "sqlManager"
		} ]
	}
}
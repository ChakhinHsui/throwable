package com.seava.throwable.thrift.client;

import java.io.Closeable;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.seava.throwable.thrift.rpc.ThrowableService;


/**
 * Thrift 连接封装
 * 
 */
public class ThriftConnect implements Closeable {

	private TTransport transport;
	private ThrowableService.Client client;

	public ThriftConnect(String host, int port) throws Exception {
		transport = new TFramedTransport(new TSocket(host, port));
		TProtocol protocol = new TCompactProtocol(transport);
		client = new ThrowableService.Client(protocol);
		
		transport.open();
	}

	public boolean isOpen() {
		return transport == null ? false : transport.isOpen();
	}

	public ThrowableService.Client getClient() {
		return client;
	}

	@Override
	public void close() {
		if (transport != null)
			transport.close();
		transport = null;
	}

}

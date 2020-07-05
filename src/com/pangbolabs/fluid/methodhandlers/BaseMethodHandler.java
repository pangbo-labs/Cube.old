package com.pangbolabs.fluid.methodhandlers;

import java.net.Socket;

import com.pangbolabs.fluid.Host;
import com.pangbolabs.fluid.HttpMethodHandler;
import com.pangbolabs.fluid.HttpRequest;

public abstract class BaseMethodHandler implements HttpMethodHandler
{

	@Override
	public void handle( HttpRequest requestHeader, Host host, Socket socket ) throws Exception
	{
		doHandling( requestHeader, host, socket );
	}

	protected abstract void doHandling( HttpRequest requestHeader, Host host, Socket socket ) throws Exception;
}

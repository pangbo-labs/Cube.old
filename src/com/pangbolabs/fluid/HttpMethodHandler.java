package com.pangbolabs.fluid;

import java.net.Socket;

public interface HttpMethodHandler
{
	void handle( HttpRequest requestHeader, Host host, Socket socket ) throws Exception;
}

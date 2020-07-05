package com.pangbolabs.fluid;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest
{
	private String method;
	private String requestTarget;
	private String httpVersion;
	private Map<String, String> headers = new HashMap<>();
	
	public String getMethod()
	{
		return method;
	}
	
	public void setMethod( String method )
	{
		this.method = method;
	}
	
	public String getRequestTarget()
	{
		return requestTarget;
	}

	public void setRequestTarget( String requestTarget )
	{
		this.requestTarget = requestTarget;
	}

	public String getHttpVersion()
	{
		return httpVersion;
	}
	
	public void setHttpVersion( String httpVersion )
	{
		this.httpVersion = httpVersion;
	}
	
	public Map<String, String> getHeaders()
	{
		return headers;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName()  + " {\n" );
		sb.append( "    method: '" + method + "'\n" );
		sb.append( "    requestUri: '" + requestTarget + "'\n" );
		sb.append( "    httpVersion: '" + httpVersion + "'\n" );
		sb.append( "    headers: {\n" );
		for (String key : headers.keySet())
			sb.append( String.format( "        '%s': '%s'\n", key, headers.get( key ) ) );
		sb.append( "    }\n" );
		sb.append( "}" );
		return sb.toString();
	}
}

package com.pangbolabs.fluid;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse
{
	private HttpStatusCode statusCode = HttpStatusCode._200;
	private Map<String, String> headers = new HashMap<>();
	private byte[] contents;
	
	public HttpResponse()
	{
	}

	public HttpStatusCode getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode( HttpStatusCode statusCode )
	{
		this.statusCode = statusCode;
	}

	public void addHeader( String name, String value )
	{
		this.headers.put( name, value );
	}
	
	public void setHeader( String name, String value )
	{
		this.headers.put( name, value );
	}
	
	public void deleteHeader( String name )
	{
		this.headers.remove( name );
	}
	
	public String getHeader( String name )
	{
		return this.headers.get( name );
	}
	
	public void setContent( byte[] contents )
	{
		this.contents = contents;
	}
	
	public void send( Socket socket ) throws IOException
	{
		OutputStream out = socket.getOutputStream();
		
		String statusLine =
			String.format( "HTTP/1.1 %d %s\r\n", this.statusCode.getCode(), this.statusCode.getText() );
		out.write( statusLine.getBytes() );
		
		for (String name : this.headers.keySet())
		{
			String line = String.format( "%s: %s\r\n", name, headers.get( name ) );
			out.write( line.getBytes() );
		}
		
		out.write( "\r\n".getBytes() );
		
		if (this.contents != null)
			out.write( this.contents );
		
		out.flush();
	}
}

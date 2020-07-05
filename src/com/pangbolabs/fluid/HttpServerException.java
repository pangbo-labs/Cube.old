package com.pangbolabs.fluid;

public class HttpServerException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private HttpStatusCode statusCode;
	
	public HttpServerException( HttpStatusCode statusCode )
	{
		this.statusCode = statusCode;
	}

	public HttpServerException( HttpStatusCode statusCode, String message )
	{
		super( message );
		this.statusCode = statusCode;
	}

	public HttpStatusCode getStatusCode()
	{
		return statusCode;
	}
}

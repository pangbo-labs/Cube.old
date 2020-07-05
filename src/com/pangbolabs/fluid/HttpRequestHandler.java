package com.pangbolabs.fluid;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler implements TcpPortListener.RequestHandler
{
	private static Logger logger = LoggerFactory.getLogger( HttpRequestHandler.class );
	
	private HttpServer httpServer;
	
	public HttpRequestHandler( HttpServer httpServer )
	{
		this.httpServer = httpServer;
	}
	
	@Override
	public void handleRequest( Socket socket )
	{
		try
		{
			try
			{
				HttpRequest httpRequest = readRequest( socket );
				Host host = getHost( httpRequest );
				handleRequest( httpRequest, host, socket );
			}
			catch (HttpServerException e)
			{
				HttpResponse response = new HttpResponse();
				response.setStatusCode( e.getStatusCode() );
				response.send( socket );
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static final int MAX_METHOD_LEN = 64;
	private static final int MAX_URL_LEN = 8000; // RFC 7230 section 3.1.1
	private static final int MAX_VERSION_LEN = 16;
	private static final int MAX_HEADER_NAME_LEN = 256;
	private static final int MAX_HEADER_VALUE_LEN = 2048;
	
	public HttpRequest readRequest( Socket socket ) throws IOException, HttpServerException
	{
		logger.trace( "Reading HTTP request...", socket.getLocalPort() );
		
		HttpRequest httpRequest = new HttpRequest();
		InputStream in = socket.getInputStream();
		
		// request line
		
		Holder<Boolean> exceedLimit = new Holder<>( false );
		Holder<Boolean> foundStopChar = new Holder<>( false );
		
		String method = readToCharOrCRLF( in, ' ', MAX_METHOD_LEN, exceedLimit, foundStopChar );
		if (!foundStopChar.value) throw new HttpServerException( HttpStatusCode._400 ); // Bad Request
		HttpMethodHandler handler = HttpMethodHandlerRegistry.getHandler( method );
		if (handler == null) throw new HttpServerException( HttpStatusCode._501 ); // Not Implemented
		httpRequest.setMethod( method );
		
		String requestTarget = readToCharOrCRLF( in, ' ', MAX_URL_LEN, exceedLimit, foundStopChar );
		if (!foundStopChar.value) throw new HttpServerException( HttpStatusCode._414 ); // URI Too Long
		if (requestTarget.length() == 0) throw new HttpServerException( HttpStatusCode._400 ); // Bad Request
		httpRequest.setRequestTarget( requestTarget );
		
		String httpVersion = readToCRLF( in, MAX_VERSION_LEN, exceedLimit );
		if (exceedLimit.value) throw new HttpServerException( HttpStatusCode._400 ); // Bad Request
		httpRequest.setHttpVersion( httpVersion );
		
		// header lines
		
		while (true)
		{
			String headerName, headerValue;
			
			headerName = readToCharOrCRLF( in, ':', MAX_HEADER_NAME_LEN, exceedLimit, foundStopChar );
			if (!foundStopChar.value && (headerName.length() > 0))
				throw new HttpServerException( HttpStatusCode._400 ); // Bad Request
			if (headerName.length() == 0) // empty line
				break;
			
			headerValue = readToCRLF( in, MAX_HEADER_VALUE_LEN, exceedLimit );
			if (exceedLimit.value) throw new HttpServerException( HttpStatusCode._400 ); // Bad Request\
			
			// remove leading and trailing OWS (optional whitespace)
			// header-field   = field-name ":" OWS field-value OWS
			// OWS            = *( SP / HTAB )
			
			int begin, end;
			for (begin = 0; begin < headerValue.length(); begin ++)
			{
				char ch = headerValue.charAt( begin );
				if ((ch != ' ') && (ch != '\t'))
					break;
			}
			for (end = headerValue.length() - 1; end >= 0; end --)
			{
				char ch = headerValue.charAt( end );
				if ((ch != ' ') && (ch != '\t'))
					break;
			}
			headerValue = headerValue.substring( begin, end + 1 );
			
			logger.trace( "'{}': '{}'", headerName, headerValue );
			
			validateHeaderInReadingRequest( httpRequest, headerName, headerValue );
			httpRequest.getHeaders().put( headerName, headerValue );
		}
		
		logger.trace( "Finished reading HTTP request. httpRequest: " + httpRequest );
		
		return httpRequest;
	}
	
	/**
	 * RFC 7230 section 3.5 Message Parsing Robustness
	 * 
	 * Although the line terminator for the start-line and header fields is
	 * the sequence CRLF, a recipient MAY recognize a single LF as a line
	 * terminator and ignore any preceding CR.
	 */
	private String readToCharOrCRLF( InputStream in, char stopChar, int charLimit,
		Holder<Boolean> exceedLimit, Holder<Boolean> foundStopChar ) throws IOException
	{
		int ch, lastchar = 0;
		if (charLimit == -1) charLimit = Integer.MAX_VALUE;
		StringBuilder sb = new StringBuilder();
		while (true)
		{
			ch = in.read();
			if ((ch == stopChar) || (ch == '\n') || (sb.length() >= charLimit))
				break;
			sb.append( (char)ch );
			lastchar = ch;
		}
		exceedLimit.value = (sb.length() >= charLimit);
		foundStopChar.value = (ch == stopChar);
		if ((ch == '\n') && (lastchar == '\r'))
			sb.deleteCharAt( sb.length() - 1 );
		return sb.toString();
	}
	
	/**
	 * RFC 7230 section 3.5 Message Parsing Robustness
	 * 
	 * Although the line terminator for the start-line and header fields is
	 * the sequence CRLF, a recipient MAY recognize a single LF as a line
	 * terminator and ignore any preceding CR.
	 */
	private String readToCRLF( InputStream in, int charLimit, Holder<Boolean> exceedLimit ) throws IOException
	{
		int ch, lastchar = 0;
		char stopChar = '\n';
		if (charLimit == -1) charLimit = Integer.MAX_VALUE;
		StringBuilder sb = new StringBuilder();
		while (((ch = in.read()) != stopChar) && (sb.length() < charLimit))
		{
			sb.append( (char)ch );
			lastchar = ch;
		}
		exceedLimit.value = (ch != stopChar);
		if ((ch == '\n') && (lastchar == '\r'))
			sb.deleteCharAt( sb.length() - 1 );
		return sb.toString();
	}
	
	private void validateHeaderInReadingRequest(
		HttpRequest httpRequest, String name, String value ) throws HttpServerException
	{
		if (name.equals( "Host" ))
		{
			/**
			 * RFC 7230 section 5.4
			 * 
			 * A server MUST respond with a 400 (Bad Request) status code to any
			 * HTTP/1.1 request message that lacks a Host header field and to any
			 * request message that contains more than one Host header field or
			 * a Host header field with an invalid field-value.
			 */
			if (httpRequest.getHeaders().containsKey( name )) // duplicated header
				throw new HttpServerException( HttpStatusCode._400 ); // Bad Request
		}
	}
	
	private Host getHost( HttpRequest httpRequest ) throws HttpServerException
	{
		Host host = null;
		
		String value = httpRequest.getHeaders().get( "Host" );
		if (value != null)
		{
			String hostName = parseHostHeader( value );
			host = httpServer.getHosts().get( hostName.toLowerCase() );
		}
		
		if (host != null)
			return host;
		
		if (this.httpServer.getDefaultHost().isDisabled())
			throw new HttpServerException( HttpStatusCode._400 );
		
		return httpServer.getDefaultHost();
	}
	
	/**
	 * RFC 7230 section 5.4
	 * Host = uri-host [ ":" port ]
	 */
	private String parseHostHeader( String hostHeaderValue ) throws HttpServerException
	{
		int colonIndex = hostHeaderValue.indexOf( ':' );
		if (colonIndex >= 0)
		{
			String portStr = hostHeaderValue.substring( colonIndex + 1, hostHeaderValue.length() );
			try
			{
				int port = Integer.parseInt( portStr );
				if (port != this.httpServer.getPort())
					throw new HttpServerException( HttpStatusCode._400 ); // Bad Request
			}
			catch (NumberFormatException e)
			{
				throw new HttpServerException( HttpStatusCode._400 ); // Bad Request
			}
		}
		else // no port specified
		{
			colonIndex = hostHeaderValue.length();
		}
		return hostHeaderValue.substring( 0, colonIndex );
	}

	private void handleRequest(
		HttpRequest httpRequest, Host host, Socket socket ) throws Exception
	{
		HttpMethodHandler handler = HttpMethodHandlerRegistry.getHandler( httpRequest.getMethod() );
		handler.handle( httpRequest, host, socket );
	}
}

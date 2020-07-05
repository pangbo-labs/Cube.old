package com.pangbolabs.fluid.methodhandlers;

import java.io.File;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangbolabs.fluid.Host;
import com.pangbolabs.fluid.HttpRequest;
import com.pangbolabs.fluid.HttpResponse;
import com.pangbolabs.fluid.HttpStatusCode;
import com.pangbolabs.fluid.IndexPageGenerator;
import com.pangbolabs.fluid.URL;

public class GetMethodHandler extends BaseMethodHandler
{
	private static Logger logger = LoggerFactory.getLogger( GetMethodHandler.class );

	@Override
	protected void doHandling( HttpRequest httpRequest, Host host, Socket socket ) throws Exception
	{
		logger.trace( "Begin to handle request..." );
		
		URL requestTarget = new URL( httpRequest.getRequestTarget() );
		String urlPath = requestTarget.getPath();
		urlPath = URLDecoder.decode( urlPath, "UTF-8" );
		urlPath = urlPath.isEmpty() ? "/" : urlPath;
		File file = new File( host.getWebroot(), urlPath.substring( 1, urlPath.length() ) );
		file = file.getCanonicalFile();
		
		HttpResponse response = new HttpResponse();
		
		if (!file.getPath().startsWith( host.getWebroot().getPath() ))
		{
			// the requested path is not under the webroot
			response.setStatusCode( HttpStatusCode._404 ); // Not Found
		}
		else if (file.isDirectory())
		{
			if (!urlPath.endsWith( "/" ))
			{
				response.setStatusCode( HttpStatusCode._301 ); // Moved Permanently
				response.addHeader( "Location", urlPath + "/" );
			}
			else
			{
				File path = new File( urlPath );
				String indexPage = IndexPageGenerator.getIndexPage( file, path.getPath().replace( '\\', '/' ) );
				
				response.setStatusCode( HttpStatusCode._200 ); // OK
				response.addHeader( "Content-Type", "text/html" );
				response.setContent( indexPage.getBytes( "UTF-8" ) );
			}
		}
		else if (file.canRead())
		{
			String contentType = URLConnection.getFileNameMap().getContentTypeFor( file.getName() );
			byte[] bytes = Files.readAllBytes( file.toPath() );
			
			response.setStatusCode( HttpStatusCode._200 ); // OK
			response.addHeader( "Content-Type", contentType );
			response.setContent( bytes );
		}
		else // file not found or can't access
		{
			String _404Page =
				"<html><head><title>File Not Found</title></head><body><h1>404 : File not found</h1></body></html>";
			
			response.setStatusCode( HttpStatusCode._404 ); // Not Found
			response.addHeader( "Content-Type", "text/html" );
			response.setContent( _404Page.getBytes() );
		}
		
		response.send( socket );
		
		logger.trace( "Finished handling request." );
	}
}

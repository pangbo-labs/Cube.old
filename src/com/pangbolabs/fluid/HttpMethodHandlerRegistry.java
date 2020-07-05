package com.pangbolabs.fluid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pangbolabs.fluid.methodhandlers.GetMethodHandler;

public class HttpMethodHandlerRegistry
{
	private static class HandlerEntry
	{
		public Class<?> handlerClass;
		public HttpMethodHandler handlerInstance;
	}
	
	private static Map<String, HandlerEntry> handlerMap = new ConcurrentHashMap<>();
	
	static
	{
		addHandler( "GET", GetMethodHandler.class );
	}
	
	private static void addHandler( String method, Class<?> handlerClass )
	{
		HandlerEntry entry = new HandlerEntry();
		entry.handlerClass = handlerClass;
		handlerMap.put( method, entry );
	}
	
	public static HttpMethodHandler getHandler( String method )
	{
		HandlerEntry entry = handlerMap.get( method );
		if (entry == null)
			return null;
		
		if (entry.handlerInstance == null)
		{
			try
			{
				entry.handlerInstance = (HttpMethodHandler)entry.handlerClass.newInstance();
			}
			catch (Exception e)
			{
				return null;
			}
		}
		
		return entry.handlerInstance;
	}
}

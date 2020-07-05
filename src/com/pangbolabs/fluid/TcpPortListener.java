package com.pangbolabs.fluid;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpPortListener
{
	public interface RequestHandler
	{
		void handleRequest( Socket socket );
	}
	
	private int port;
	private RequestHandler requestHandler;
	private int numOfWorkers;
	private ExecutorService threadPool;
	
	public TcpPortListener( int port, RequestHandler requestHandler, int numOfWorkers )
	{
		this.port = port;
		this.requestHandler = requestHandler;
		this.numOfWorkers = numOfWorkers;
	}
	
	public void start()
	{
		this.threadPool = Executors.newFixedThreadPool( numOfWorkers );
		
		try (ServerSocket serverSocket = new ServerSocket())
		{
			serverSocket.bind( new InetSocketAddress( this.port ) );
			while (true)
			{
				try
				{
					Socket socket = serverSocket.accept();
					HandlerThread handlerThread = new HandlerThread( socket, this.requestHandler );
					threadPool.execute( handlerThread );
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		threadPool.shutdown();
	}
	
	private static class HandlerThread implements Runnable
	{
		private Socket socket;
		private RequestHandler handler;
		
		public HandlerThread( Socket socket, RequestHandler handler )
		{
			this.socket = socket;
			this.handler = handler;
		}

		@Override
		public void run()
		{
			try
			{
				handler.handleRequest( socket );
			}
			finally
			{
				try { socket.close(); } catch (Exception e) {}
			}
		}
	}
}

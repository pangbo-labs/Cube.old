package com.pangbolabs.fluid;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangbolabs.fluid.configuration.HostConfig;
import com.pangbolabs.fluid.configuration.HostDefSettings;
import com.pangbolabs.fluid.configuration.PortConfig;
import com.pangbolabs.fluid.configuration.ServerConfig;

public class HttpServer
{
	private static Logger logger = LoggerFactory.getLogger( HttpServer.class );
	
	private static final int DEFAULT_HTTP_PORT = 80;
	private static final int DEFAULT_NUM_OF_WORKERS = 2;
	
	private ServerHome serverHome;
	private ServerConfig serverConfig;
	private int port;
	private int numOfWorkers;
	private Map<String, Host> hostMap;
	private Host defaultHost;
	
	public HttpServer( ServerHome serverHome, ServerConfig serverConfig, PortConfig portConfig )
	{
		init( serverHome, serverConfig, portConfig, DEFAULT_NUM_OF_WORKERS );
	}
	
	public HttpServer( ServerHome serverHome, ServerConfig serverConfig, PortConfig portConfig, int numOfWorkers )
	{
		init( serverHome, serverConfig, portConfig, numOfWorkers );
	}
	
	private void init( ServerHome serverHome, ServerConfig serverConfig, PortConfig portConfig, int numOfWorkers )
	{
		if (serverHome == null)
			throw new IllegalArgumentException( "'serverHome' is null." );
		
		if (serverConfig == null)
			throw new IllegalArgumentException( "'serverConfig' is null." );
		
		if (portConfig == null)
			throw new IllegalArgumentException( "'portConfig' is null." );
		
		if ((portConfig.getPort() <= 0) || (portConfig.getPort() > 65535))
			throw new IllegalArgumentException( "'portConfig.getPort()' out of range." );
		
		this.serverHome = serverHome;
		this.serverConfig = serverConfig;
		this.port = portConfig.getPort();
		this.numOfWorkers = numOfWorkers;
		this.defaultHost = createHostFromConfig( portConfig.getDefaultHost(), serverConfig.getHostDefSettings(), serverHome );
		
		this.hostMap = new HashMap<>();
		for (HostConfig hostConfig : portConfig.getHosts())
		{
			Host host = createHostFromConfig( hostConfig, serverConfig.getHostDefSettings(), serverHome );
			for (String hostName : host.getHostNames())
				this.hostMap.put( hostName.trim().toLowerCase(), host );
		}
	}
	
	private Host createHostFromConfig( HostConfig hostConfig, HostDefSettings defaultSettings, ServerHome serverHome )
	{
		Host host = new Host( defaultSettings );
		
		if (hostConfig.getHostNames() != null)
			host.getHostNames().addAll( hostConfig.getHostNames() );
		
		String hostHome = hostConfig.getHostHome();
		hostHome = hostHome.replace( "${FLUID_HOME}", serverHome.getHome().getPath() );
		host.setHostHome( new File( hostHome ) );
		
		if (hostConfig.getIndexPages() != null)
			host.getIndexFiles().addAll( hostConfig.getIndexPages() );
		
		host.setDisabled( hostConfig.isDisabled() );
		
		return host;
	}
	
	public void start()
	{
		ListeningRunnable runnable = new ListeningRunnable( this );
		Thread thread = new Thread( runnable );
		thread.start();
	}
	
	public ServerHome getServerHome()
	{
		return serverHome;
	}

	public ServerConfig getServerConfig()
	{
		return serverConfig;
	}

	public int getPort()
	{
		return port;
	}

	public Map<String, Host> getHosts()
	{
		return hostMap;
	}

	public Host getDefaultHost()
	{
		return defaultHost;
	}
	
	private static class ListeningRunnable implements Runnable
	{
		private HttpServer server;
		
		public ListeningRunnable( HttpServer server )
		{
			this.server = server;
		}
		
		@Override
		public void run()
		{
			logger.info( "Starting HTTP server on port {}...", server.port );
			
			TcpPortListener portListener =
				new TcpPortListener( server.port, new HttpRequestHandler( server ), server.numOfWorkers );
			portListener.start();
		}
	}
}

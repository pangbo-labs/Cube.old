package com.pangbolabs.fluid;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangbolabs.fluid.configuration.ServerConfig;
import com.pangbolabs.fluid.configuration.PortConfig;

public class Fluid
{
	private static Logger logger = LoggerFactory.getLogger( Fluid.class );
	
	private ServerHome serverHome;
	
	public Fluid( ServerHome serverHome )
	{
		this.serverHome = serverHome;
	}
	
	public void start()
	{
		configLogs( serverHome );
		
		logger.info( "Fluid HTTP Server is starting..." );
		
		ServerConfig serverConfig = ServerConfig.load( serverHome ) ;
		serverConfig.save( serverHome );
		for (PortConfig portConfig : serverConfig.getPortConfigs())
		{
			HttpServer httpServer = new HttpServer( serverHome, serverConfig, portConfig );
			httpServer.start();
		}
		
		Runtime.getRuntime().addShutdownHook( new Thread() {
			@Override
			public void run()
			{
				logger.info( "Fluid HTTP Server exited" );
			}
		});
	}

	private void configLogs( ServerHome serverHome )
	{
		File logConfigFile = new File( serverHome.getConfigFolder(), "log4j2.xml" );
//		System.setProperty( "log4j.configurationFile", logConfigFile.getPath() );
		LoggerContext loggerContext = (LoggerContext)LogManager.getContext( false );
		loggerContext.setConfigLocation( logConfigFile.toURI() );
		loggerContext.reconfigure();
	}
}

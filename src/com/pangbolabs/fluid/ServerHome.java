package com.pangbolabs.fluid;

import java.io.File;

public class ServerHome
{
	private static final String BIN_FOLDER = "bin";
	private static final String CONFIG_FOLDER = "conf";
	private static final String LOG_FOLDER = "logs";
	private static final String LIB_FOLDER = "lib";
	
	private File home;
	
	public ServerHome( String homePath )
	{
		setHome( homePath );
	}
	
	public ServerHome( File home )
	{
		setHome( home );
	}

	public File getHome()
	{
		return home;
	}
	
	public void setHome( String homePath )
	{
		this.home = new File( homePath );
	}

	public void setHome( File home )
	{
		this.home = home;
	}
	
	public File getBinFolder()
	{
		return new File( home, BIN_FOLDER );
	}
	
	public File getConfigFolder()
	{
		return new File( home, CONFIG_FOLDER );
	}
	
	public File getLibFolder()
	{
		return new File( home, LIB_FOLDER );
	}
	
	public File getLogFolder()
	{
		return new File( home, LOG_FOLDER );
	}
}

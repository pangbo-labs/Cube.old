package com.pangbolabs.fluid;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pangbolabs.fluid.configuration.HostDefSettings;

public class Host
{
	private Set<String> hostNames = new HashSet<>();
	private int port;
	private File hostHome;
	private File webRoot;
	private List<String> indexFiles;
	private boolean isDisabled = false;
	
	public Host( HostDefSettings defaultSettings )
	{
		this.indexFiles = new ArrayList<>( defaultSettings.getIndexPages() );
	}
	
//	public Host( String hostName, int port, File hostHome )
//	{
//		this.hostNames.add( hostName );
//		this.port = port;
//		this.hostHome = hostHome;
//	}
	
	public Set<String> getHostNames()
	{
		return hostNames;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort( int port )
	{
		this.port = port;
	}

	public File getHostHome()
	{
		return hostHome;
	}

	public void setHostHome( File hostHome )
	{
		this.hostHome = hostHome;
		this.webRoot = null;
	}

	public File getWebroot()
	{
		if (webRoot == null)
			webRoot = new File( hostHome, "webroot" );
		return webRoot;
	}
	
	public List<String> getIndexFiles()
	{
		return indexFiles;
	}

	public boolean isDisabled()
	{
		return isDisabled;
	}

	public void setDisabled( boolean isDisabled )
	{
		this.isDisabled = isDisabled;
	}
}

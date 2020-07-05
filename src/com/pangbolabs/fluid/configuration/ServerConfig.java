package com.pangbolabs.fluid.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.pangbolabs.fluid.ServerHome;

@XmlRootElement( name = "fluidConfiguration" )
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerConfig
{
	@XmlElementWrapper( name = "listening" )
	@XmlElement( name = "port" )
	private List<PortConfig> portConfigs = new ArrayList<>();
	
	@XmlElement( name = "hostDefaultSettings" )
	private HostDefSettings hostDefSettings;
	
	@XmlElement( name = "management" )
	private MgmtSettings manageSettings = new MgmtSettings();
	
	////////////////////////////////////////////////////////////////

	public List<PortConfig> getPortConfigs()
	{
		return portConfigs;
	}

	public HostDefSettings getHostDefSettings()
	{
		return hostDefSettings;
	}

	public void setHostDefSettings( HostDefSettings hostDefSettings )
	{
		this.hostDefSettings = hostDefSettings;
	}

	private static File getFilePath( File folder )
	{
		return new File( folder, "FluidConfiguration.xml" );
	}
	
	public void save( ServerHome homeDir )
	{
		File filePath = getFilePath( homeDir.getConfigFolder() );
		JAXB.marshal( this, filePath );
	}
	
	public static ServerConfig load( ServerHome homeDir )
	{
		ServerConfig conf = new ServerConfig();
		
		PortConfig hostsOnPort = new PortConfig();
		hostsOnPort.setPort( 8080 );
		conf.getPortConfigs().add( hostsOnPort );
		
		HostConfig host;
		
		host = new HostConfig();
		host.setHostHome( "${FLUID_HOME}\\hosts\\_default_" );
		hostsOnPort.setDefaultHost( host );
		
		host = new HostConfig();
		host.getHostNames().add( "example.com" );
		host.getHostNames().add( "www.example.com" );
		host.setHostHome( "${FLUID_HOME}\\hosts\\example.com" );
		host.setDisabled( true );
		hostsOnPort.getHosts().add( host );
		
		HostDefSettings hostDefSettings = new HostDefSettings();
		hostDefSettings.setIndexPages( new ArrayList<>() );
		hostDefSettings.getIndexPages().add( "index.html" );
		hostDefSettings.getIndexPages().add( "index.htm" );
		conf.setHostDefSettings( hostDefSettings );
		
		return conf;
	}

}

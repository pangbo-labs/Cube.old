package com.pangbolabs.fluid.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"port", "defaultHost" , "hosts" } )
public class PortConfig
{
	@XmlAttribute( name = "no", required = true )
	private int port;
	
	@XmlElement( name="host" )
	private List<HostConfig> hosts = new ArrayList<>();
	
	private HostConfig defaultHost;

	@XmlTransient
	public int getPort()
	{
		return port;
	}

	public void setPort( int port )
	{
		this.port = port;
	}

	public List<HostConfig> getHosts()
	{
		return hosts;
	}

	public HostConfig getDefaultHost()
	{
		return defaultHost;
	}

	public void setDefaultHost( HostConfig defaultHost )
	{
		this.defaultHost = defaultHost;
	}
}

package com.pangbolabs.fluid.configuration;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType( propOrder = {"netInterface", "hostNames" , "hostHome", "indexPages", "errorPageMap", "disabled" } )
public class HostConfig
{
	private InetAddress netInterface = null;
	
	@XmlElementWrapper
	@XmlElement( name = "hostName" )
	private List<String> hostNames = new ArrayList<>();
	
	private String hostHome = null;
	
	@XmlElementWrapper
	@XmlElement( name = "indexPage" )
	private List<String> indexPages = null;
	
	private Map<Integer, String> errorPageMap = null;
	private boolean isDisabled = false;
	
	////////////////////////////////////////////////////////////////

	public InetAddress getNetInterface()
	{
		return netInterface;
	}

	public void setNetInterface( InetAddress netInterface )
	{
		this.netInterface = netInterface;
	}

	@XmlTransient
	public List<String> getHostNames()
	{
		return hostNames;
	}

	public void setHostNames( List<String> hostNames )
	{
		this.hostNames = hostNames;
	}

	public String getHostHome()
	{
		return hostHome;
	}

	public void setHostHome( String hostHome )
	{
		this.hostHome = hostHome;
	}

	public boolean isDisabled()
	{
		return isDisabled;
	}

	public void setDisabled( boolean isDisabled )
	{
		this.isDisabled = isDisabled;
	}

	@XmlTransient
	public List<String> getIndexPages()
	{
		return indexPages;
	}

	public void setIndexPages( List<String> indexPages )
	{
		this.indexPages = indexPages;
	}

	public Map<Integer, String> getErrorPageMap()
	{
		return errorPageMap;
	}

	public void setErrorPageMap( Map<Integer, String> errorPageMap )
	{
		this.errorPageMap = errorPageMap;
	}
}

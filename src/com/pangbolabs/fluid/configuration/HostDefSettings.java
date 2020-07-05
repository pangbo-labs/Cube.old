package com.pangbolabs.fluid.configuration;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

public class HostDefSettings
{
	@XmlElementWrapper
	@XmlElement( name = "indexPage" )
	private List<String> indexPages;

	@XmlTransient
	public List<String> getIndexPages()
	{
		return indexPages;
	}

	public void setIndexPages( List<String> indexPages )
	{
		this.indexPages = indexPages;
	}
}

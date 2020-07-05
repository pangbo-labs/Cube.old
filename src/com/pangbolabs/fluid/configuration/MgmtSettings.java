package com.pangbolabs.fluid.configuration;

public class MgmtSettings
{
	private static final int DEFAULT_MANAGEMENT_PORT = 6160;
	
	private int managementPort = DEFAULT_MANAGEMENT_PORT;

	public int getManagementPort()
	{
		return managementPort;
	}

	public void setManagementPort( int managementPort )
	{
		this.managementPort = managementPort;
	}
}

package com.pangbolabs.fluid;

public class Main
{
	public static void main( String[] args )
	{
		ServerHome homeDir = new ServerHome( "D:\\Projects (Java)\\Fluid\\Deployed" );
		Fluid fluid = new Fluid( homeDir );
		fluid.start();
	}
}

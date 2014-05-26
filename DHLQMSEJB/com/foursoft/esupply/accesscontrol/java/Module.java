/*
 * Copyright ©.
 */
package com.foursoft.esupply.accesscontrol.java;

import java.io.Serializable;
import java.util.ArrayList;

public class Module implements Serializable
{
	public String moduleName;
	public ArrayList uiList;
	public Module()
	{
	}
	public Module(String moduleName,ArrayList uiList)
	{
		this.moduleName = moduleName;
		this.uiList		= uiList;
	}
}

/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.accesscontrol.java;

/**
 * File			: UserInterface.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This java object used to store properties of one UserInterface
 * 
 * @author	Sasi Bhushan. P, 
 * @date	22-12-2001
 */

public class UserInterface
{
	public int accessLevel;  // AccessLevel required for that User Interface
	public String process;  // Process of the User Interface
	public String url;      // Url for User Interface
	public String txid;     // Traction id of the User Interface 
	
	public UserInterface()
	{
	}
	public UserInterface(int accessLevel,String txid,String process,String url)
	{
		this.accessLevel 	= accessLevel;
		this.txid		= txid;
		this.process 	= process;
		this.url 		= url;
	}
	public String toString()
	{
		return "\n\t" + txid + ":" + process + ":" +url;
	}
}
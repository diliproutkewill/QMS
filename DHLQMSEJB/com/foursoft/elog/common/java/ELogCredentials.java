/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x      
 * 
 */ 
 
package com.foursoft.elog.common.java;


import java.util.ArrayList;
import java.util.Hashtable;

/**
 * File			: ELogCredentials.java
 * sub-module 	: Common
 * module 		: elog
 * 
 * This holds the credentials related to ELog
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	30-10-2001
 */
public class ELogCredentials implements java.io.Serializable
{
	/**
	* default empty constrctor
	*/
	public ELogCredentials()
	{
	}
	/**
	* constructor which takes wareHouse Name and custWHIdInfo as arguments
	* @param wareHouseName
	* @param custWHInfo
	*/
	public ELogCredentials(String wareHouseName, ArrayList custWHInfo)
	{
		this.wareHouseName	= wareHouseName;
		this.custWHIdInfo		= custWHIdInfo;
	}
	
	/**
	* used to get the WareHouseName
	*/
	public String getWareHouseName()
	{
		return this.wareHouseName;
	} 	

	/**
	* used to get the list of customer WareHouses allowed
	*/
	public ArrayList getCustWHIdInfo()
	{
		return this.custWHIdInfo;
	}

	public ArrayList getWHInfo()
	{
		return this.custWHIdInfo;
	}

	/**
	* used to get the list of customer WareHouses allowed
	*/
	public ArrayList getPermittedWHIds()
	{
		return this.permittedWHIds;
	}

	
	/**
	* used to get the list of WareHouses Projects allowed
	*/
	public Hashtable getProjectInfo()
	{
		return this.projectTable;
	}
	/**
	* used to set the WareHouseName
	*/
	public void setWareHouseName(String wareHouseName)
	{
		this.wareHouseName	= wareHouseName;
	}
	
	/**
	* used to set the list of customer WareHouses allowed
	*/
	public void setCustWHInfo(ArrayList custWHIdInfo)
	{
		this.custWHIdInfo	= custWHIdInfo;		
	}

	public void setPermittedWHIds(ArrayList permittedWHIds)
	{
		this.permittedWHIds	= permittedWHIds;		
	}
	
	/**
	* used to set the list of customer WareHouses allowed
	*/
	public void setProjectInfo(Hashtable projectTable)
	{
		this.projectTable	= projectTable;		
	}

	/**
	* used to set the list of customer WareHouses allowed
	*/
	public void setWHInfo(ArrayList custWHIdInfo)
	{
		this.custWHIdInfo	= custWHIdInfo;		
	}

	private ArrayList	permittedWHIds		= null;		//	list of WHIds for which permissions are given to the current User
	private ArrayList	custWHIdInfo		= null;		//	list of Allowed CustWHIds	
	private String 		wareHouseName		= null;		//	Warehouse name
	private Hashtable   projectTable		= null;
	
	public String toString()
	{
		return custWHIdInfo.toString();
	}
}
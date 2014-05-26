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
 * File			: TxDetailVOB.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is used as the value object, which represents a transaction detail(application)
 * which holds the values like transactionId, accesType, accessLevel
 * 
 * @author	Madhu. P, 
 * @date	28-08-2001
 */
public class TxDetailVOB implements java.io.Serializable
{
	public String	txId 			= null; // transactionId of the application
	public int		accessLevel		= 0;	// access level, it is varied from (1 , 2 ..7)
	public String	accessType		= null; // accessTye like Project, CustWh, Company
	public String	description		= null; // description of transactionId
	public int		shipmentMode	= -1;	// shipment mode code 
	public String	module			= "";	// Non peristent value holder field
	
	/* over loading constructor TxDetailVOB()
	* @param txId - transactionId
	* @param accessLevel 
	*/
	public TxDetailVOB( String aTxId, int anAccessLevel ) 
	{
		this.txId = aTxId;
		this.accessLevel = anAccessLevel;
	}
	
	/* constructor TxDetailVOB()
	* @param txId - transactionId
	* @param accessType  	
	* @param accessLevel 
	*/
	public TxDetailVOB( String aTxId, String description, int anAccessLevel ) 
	{
		this.txId = aTxId;
		this.description = description;
		this.accessLevel = anAccessLevel;
	}
	
	/* constructor TxDetailVOB()
	* @param txId - transactionId
	* @param accessType  	
	* @param accessLevel 
	*/
	public TxDetailVOB( String aTxId, String aDescription,String anAccessType, int anAccessLevel ) 
	{
		this.txId = aTxId;
		this.description = aDescription;
		this.accessType = anAccessType;
		this.accessLevel = anAccessLevel;
	}
	
	public String toString()
	{
		return txId+" - "+description+" _ "+accessType+" - "+accessLevel;
	}
}

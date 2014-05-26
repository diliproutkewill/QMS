/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.java;

import java.sql.Timestamp;

/**
 * File			: UserLogVOB.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents UserLog details, used as value object
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	30-10-2001
 */

public class UserLogVOB implements java.io.Serializable
{
	public String 		locationId;		// location of the user(can be terminal, custWHId, projectId etc.,)
	public String 		userId;			// user's Id
	public String 		documentType;	// Type of the document
	public String 		documentRefNo;	// document's reference number
	public Timestamp 	documentDate;	// document created/modified date
	public String 		transactionType;// transaction type(add, modify and delete etc..)
	
	public UserLogVOB()
	{
	}
	/**
	* Constructor which takes arguments are (locationId, userId, documentType, documentRefNo, documentDate, and transactionType)
	* @param locationId
	* @param userId
	* @param documentType
	* @param documentRefNo
	* @param documentDate
	* @param transactionType
	*/
	public UserLogVOB(String locationId, String userId, String documentType, String documentRefNo, Timestamp documentDate, String transactionType) 
	{
		this.locationId 		= locationId;
		this.userId 			= userId;
		this.documentType 		= documentType;
		this.documentRefNo 		= documentRefNo;
		this.documentDate 		= documentDate;
		this.transactionType 	= transactionType;
	}

	/**
	* Constructor which takes arguments are (locationId, userId, documentType, documentRefNo, and transactionType)
	* @param locationId
	* @param userId
	* @param documentType
	* @param documentRefNo
	* @param transactionType
	*/
	public UserLogVOB(String locationId, String userId, String documentType, String documentRefNo, String transactionType) 
	{
		this.locationId 		= locationId;
		this.userId 			= userId;
		this.documentType 		= documentType;
		this.documentRefNo 		= documentRefNo;
		this.transactionType 	= transactionType;
	}
	
	public void clear()
	{
		this.locationId 		= null;
		this.userId 			= null;
		this.documentType 		= null;
		this.documentRefNo 		= null;
		this.transactionType 	= null;		
	}
}

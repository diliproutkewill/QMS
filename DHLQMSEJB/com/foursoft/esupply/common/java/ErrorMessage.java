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

import java.util.ArrayList;
import com.foursoft.esupply.common.exception.FoursoftException;

/**
 * File			: ErrorMessage.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents the message object which is passed to ErrorPage
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	30-10-2001
 * @Modified by Ramakumar.B
 * @date	20-05-2003
 */

public class ErrorMessage
{
	private String 		message 		= null;		// message to be passed
	private String 		nextNavigation	= null;;	// nextNavigation JSP
	private ArrayList	keyValueList 	= null; 	// parameters to be passed to the next page 
	private String		errorCode		= null;		// represents the error.
	private String		componentDetails= null;  	// represents the component where the error is raised.	


	/**
	* Constructor which takes message, nextNavigation and keyValueList as arguments
	* @param message
	* @param nextNavigation
	* @param keyValueList
	*/

	public ErrorMessage(String message,String nextNavigation, ArrayList keyValueList)
	{
		this.message		= message;
		this.nextNavigation	= nextNavigation;
		this.keyValueList	= keyValueList;
	}

	/**
	* Constructor which takes message, nextNavigation only
	* @param message
	* @param nextNavigation
	*/

	public ErrorMessage(String message, String nextNavigation)
	{
		this.message		= message;
		this.nextNavigation	= nextNavigation;
	}
	
	/**
	* Constructor which takes message, nextNavigation, keyValueList, errorCode and componentDetails as arguments
	* @param message
	* @param nextNavigation
	* @param keyvalueList
	* @param errorCode
	* @param componentDetails
	*/

	public ErrorMessage(String message,String nextNavigation, ArrayList keyValueList,String errorCode,String componentDetails)
	{
		this.message			= message;
		this.nextNavigation		= nextNavigation;
		this.keyValueList		= keyValueList;
		this.errorCode			= errorCode;
		this.componentDetails	= componentDetails;		
	}
	

	/**
	* Constructor which takes message, nextNavigation, errorCode and componentDetails as arguments
	* @param message
	* @param nextNavigation
	* @param errorCode
	* @param componentDetails
	*/

	public ErrorMessage(String message,String nextNavigation,String errorCode,String componentDetails)
	{
		this.message			= message;
		this.nextNavigation		= nextNavigation;
		this.errorCode			= errorCode;
		this.componentDetails	= componentDetails;		
	}
	
	/**
	* getter method for message
	*/
	public String getMessage()
	{
		return this.message;
	}


	/**
	* getter method for nextNavigation
	*/	
	public String getNextNavigation()
	{
		return this.nextNavigation;		
	}

	/**
	* getter method for keyValueSet
	*/		
	public ArrayList getKeyValueList()
	{
		return this.keyValueList;		
	}

    /**
	 * setter method for keyvalueSet
	 */
	public void setKeyValueList(ArrayList keyValueList)
	{
		this.keyValueList	= keyValueList;		
	}
  
	/**
	* getter method for errorCode
	*/	
	public String getErrorCode()
	{
		return this.errorCode;		
	}
		
	/**
	* getter method for componentDetails
	*/		
	public String getComponentDetails()
	{
		return this.componentDetails;		
	}	
	
	
	/**
	*
	*/
	public String toString()
	{
		StringBuffer sb	= new StringBuffer(100);
		sb.append("Error Message Obj :");
		sb.append("\t "+message);
		sb.append("\r\n\t nextNavigation : ");
		sb.append(nextNavigation);
		sb.append("\r\n\t\t Key Values : ");
		sb.append(keyValueList);
		sb.append("\r\n\t errorCode : ");
		sb.append(errorCode);
		sb.append("\r\n\t componentDetails : ");
		sb.append(componentDetails);

		return sb.toString();
	}	
}

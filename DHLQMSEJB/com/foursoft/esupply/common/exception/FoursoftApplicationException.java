/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */

package com.foursoft.esupply.common.exception;


/**
 * File			: FoursoftApplicationException.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents the user defined exception to throw the application exceptions
 * from Middle tier compoents.
 * 	   
 * @Author by Ramakumar.B
 * @date   24-07-2003
 */

public class FoursoftApplicationException extends Exception
{
	
	/*
	*	Empty argument constructor
	*/

	public FoursoftApplicationException()
	{
	}

	/*
	*	Constructor which will take Error Message String as argument
	*/	

	public FoursoftApplicationException(String message)
	{
		super(message);		
	}
	
	public String getMessage()
	{
		String msg = super.getMessage();
		return msg;
	}
	
}

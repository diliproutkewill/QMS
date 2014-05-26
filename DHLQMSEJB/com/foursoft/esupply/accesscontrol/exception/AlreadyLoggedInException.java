
/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.accesscontrol.exception;

/**
 * File			: AlreadyLoggedInException
 * sub-module	: common
 * module		: esupply
 * 
 * This is an AlreadyLoggedInException .
 * This exception used throw if another user is trying to login with same userId
 * 
 * @author	Madhu. P, 
 * @date	28-08-2001
 */
import com.foursoft.esupply.common.exception.FoursoftApplicationException;
 
public class AlreadyLoggedInException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30011";
	/*
	*	Empty argument constructor
	*/
	public AlreadyLoggedInException() 
	{
		
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public AlreadyLoggedInException( String exp ) 
	{
		super( exp );
	}
	
	/*
	*	Constructor which will take Error Message String as argument and the hidden exception
	*/
/*
	public AlreadyLoggedInException( String exp, Exception e ) 
	{
		super( exp, e);
	}
*/	
}

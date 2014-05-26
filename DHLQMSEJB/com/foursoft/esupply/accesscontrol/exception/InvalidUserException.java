
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
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is an Exception class : InvalidUserException .
 * This exception used throw, when the user is not authorised to use the application
 * 
 * @author	Madhu. P, 
 * @date	28-08-2001
 */
import com.foursoft.esupply.common.exception.FoursoftApplicationException; 
 
public class InvalidUserException extends FoursoftApplicationException
{
	
	public static String ERRORCODE = "AE-30014";
	/*
	*	Empty argument constructor
	*/
	public InvalidUserException( String exp ) 
	{
		super( exp );
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public InvalidUserException() 
	{
		
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/

/*
	public InvalidUserException( String exp, Exception hiddenException ) 
	{
		super(exp, hiddenException);
	}
*/    
}


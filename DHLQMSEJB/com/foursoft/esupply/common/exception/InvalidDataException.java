/*
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
 * File			: AlreadyLoggedInException
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is an Exception class : InvalidUserException .
 * This exception used throw, when the user is not authorised to use the application
 * 
 * @author	Madhu. P, 
 * @date	10-02-2002
 */
import com.foursoft.esupply.common.exception.FoursoftApplicationException;
 
public class InvalidDataException extends FoursoftApplicationException
{
	
	public static String ERRORCODE = "AE-30025";
	
	/*
	 *	Empty argument constructor
	 */
	public InvalidDataException( String exp ) 
	{
		super( exp );
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public InvalidDataException() 
	{
		
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
/*
	public InvalidDataException( String exp, Exception hiddenException ) 
	{
		super( exp, hiddenException);
	}
*/    
}


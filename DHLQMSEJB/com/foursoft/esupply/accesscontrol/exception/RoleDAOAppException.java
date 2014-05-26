package com.foursoft.esupply.accesscontrol.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * File			: RoleDAOAppException
 * sub-module	: accesscontrol
 * module		: esupply
 * 
 * This is an Exception class : RoleDAOAppException .
 * This exception used thrown, application exception while registering the Role 
 * 
 * module 		: esupply
 * sub-module	: common
 * @author Madhu. P, 2002-04-10
 */
 
public class RoleDAOAppException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30017";
	
	/*
	 *	Empty argument constructor
	 */
	public RoleDAOAppException() 
	{
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public RoleDAOAppException( String exp ) 
	{
		super( exp );
	}

	/*
	*	Constructor which will take Error Message String and Hidden Exception as arguments
	*/
/*
	public RoleDAOAppException( String exp, Exception e ) 
	{
		super( exp, e );
	}	
*/
}

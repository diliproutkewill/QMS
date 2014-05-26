package com.foursoft.esupply.accesscontrol.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * File			: UserDAOAppException
 * sub-module	: accesscontrol
 * module		: esupply
 * 
 * This is an Exception class : UserDAOAppException .
 * This exception used thrown, application exception while registering the User 
 * 
 * module 		: esupply
 * sub-module	: common
 * @author Madhu. P, 2002-04-10
 */
 
public class UserDAOAppException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30018";
	
	/*
	 *	Empty argument constructor
	 */
	public UserDAOAppException() 
	{
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public UserDAOAppException( String exp ) 
	{
		super( exp );
	}

	/*
	*	Constructor which will take Error Message String and Hidden Exception as arguments
	*/
/*
    public UserDAOAppException( String exp, Exception e ) 
	{
		super( exp, e );
	}	
*/    
}

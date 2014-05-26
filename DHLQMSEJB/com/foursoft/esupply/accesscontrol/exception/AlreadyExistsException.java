package com.foursoft.esupply.accesscontrol.exception;

/**
 * File			: AlreadyExistsException
 * sub-module	: common
 * module		: esupply
 * 
 * 
 * This is an Exception class : AlreadyExistException .
 * This exception used throw if user is trying create a record, where the record already Exist
 * 
 
 * @author Madhu. P, 28-08-2001
 */
 
import com.foursoft.esupply.common.exception.FoursoftApplicationException;

public class AlreadyExistsException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30010";
	/*
	*	Empty argument constructor
	*/
	public AlreadyExistsException() 
	{
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public AlreadyExistsException( String exp ) 
	{
		super( exp );
	}

	/*
	*	Constructor which will take Error Message String and Hidden Exception as arguments
	*/
/*
	public AlreadyExistsException( String exp, Exception e ) 
	{
		super( exp, e );
	}	
*/

}

/*
 * Copyright ©.
 */
 
package com.foursoft.esupply.common.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * @author  ramakumar
 */
public class InvalidDateFormatException extends FoursoftApplicationException
{
	
	public static String ERRORCODE = "AE-30027";
	
	/*
	 *	Empty argument constructor
	 */
	public String errorMessage = "";
	
	public InvalidDateFormatException()
	{
	}

	/*
	*	Empty argument constructor
	*/
	public InvalidDateFormatException(String message)
	{
		this.errorMessage = message ;
	}
	
}

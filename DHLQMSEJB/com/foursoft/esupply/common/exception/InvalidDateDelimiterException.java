/*
 * Copyright ©.
 */
 
package com.foursoft.esupply.common.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * @author  ramakumar
 */
public class InvalidDateDelimiterException extends FoursoftApplicationException
{
	
	public static String ERRORCODE = "AE-30026";
	
	/*
	 *	Empty argument constructor
	 */
	public String errorMessage = "";
	
	public InvalidDateDelimiterException()
	{
	}

	/*
	*	Empty argument constructor
	*/
	public InvalidDateDelimiterException(String message)
	{
		this.errorMessage = message ;
	}
	
}

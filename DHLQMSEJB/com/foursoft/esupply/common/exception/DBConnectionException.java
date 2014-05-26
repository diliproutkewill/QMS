/*
 * Copyright ©.
 */
package com.foursoft.esupply.common.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * @author  madhu
 */
public class DBConnectionException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30022";
	
	public DBConnectionException()
	{
	}
/*
	public DBConnectionException(String message,Exception e)
	{
		super(message, e);
	}
*/
	public DBConnectionException(String message)
	{
		super(message);		
	}
}

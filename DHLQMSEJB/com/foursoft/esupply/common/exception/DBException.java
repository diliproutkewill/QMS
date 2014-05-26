/*
 * Copyright ©.
 */
package com.foursoft.esupply.common.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;
/**
 * @author  madhu
 */
public class DBException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30023";
	
	public DBException()
	{
	}
/*
	public DBException(String message,Exception e)
	{
		super(message, e);
	}
*/    
	public DBException(String message)
	{
		super(message);		
	}
}

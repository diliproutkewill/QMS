/*
 * Copyright ©.
 */
package com.foursoft.esupply.common.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * @author  madhu
 */
public class DataSourceException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30021";

	public DataSourceException()
	{
	}

/*
	public DataSourceException(String message, Exception e)
	{
		super(message, e);
	}
*/
}

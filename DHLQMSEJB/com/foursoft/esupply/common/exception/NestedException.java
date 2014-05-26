/*
 * Copyright ©.
 */
package com.foursoft.esupply.common.exception;

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * @author  sasibhushan
 */
public class NestedException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30029";
	
	public Exception hiddenException;
	
	public NestedException()
	{
	}
	public NestedException(String message,Exception e)
	{
		super(message);
		hiddenException = e;
	}
	public NestedException(String message)
	{
		super(message);		
	}
	public Exception NestedException()
	{
		return hiddenException;
	}
	public String getMessage()
	{
		StringBuffer msg = new StringBuffer(super.getMessage());
		if(hiddenException != null)
		{
			msg.append("\r\n");
			msg.append(hiddenException.getMessage() ); 
		}
		return msg.toString();
	}
}

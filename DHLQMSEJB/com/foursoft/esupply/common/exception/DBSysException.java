package com.foursoft.esupply.common.exception;

import java.lang.RuntimeException;
import com.foursoft.esupply.common.exception.FoursoftApplicationException;
/**
 * @author  madhu
 */
public class DBSysException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30024";
	
	public DBSysException()
	{
		super();
	}
	public DBSysException(String message)
	{
		super(message);
	}
	
}

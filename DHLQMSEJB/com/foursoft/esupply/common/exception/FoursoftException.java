/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */

package com.foursoft.esupply.common.exception;

import com.foursoft.esupply.common.java.EJBErrorCodes;
import javax.naming.NamingException;
import javax.ejb.CreateException;
import java.rmi.RemoteException;
import javax.ejb.FinderException;
import java.text.ParseException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * File			: FoursoftException.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents the user defined exception class used to throw from all Middle tier components.
 * 	   
 * @Author by Ramakumar.B
 * @date   20-05-2003
 */


public class FoursoftException extends Exception
{
	public Exception hiddenException;
	String message 		= null;
	String errorCode 	= null;
	String fileName 	= null;
	String methodName	= null;


	public FoursoftException()
	{
	}

	public static FoursoftException getException(Exception e,String fileName,String methodName)
	{
		if(e instanceof FoursoftException)
			return (FoursoftException)e;
		else
			return new FoursoftException(e,fileName,methodName);
	}	

    public static FoursoftException getException(Exception e, String fileName, String methodName, String msg)
	{
		if(e instanceof FoursoftException)
			return (FoursoftException)e;
		else
			return new FoursoftException(e,fileName,methodName,msg);
	}	

	
	public FoursoftException(Exception e,String fileName,String methodName)
	{
		this.hiddenException = e;
		this.fileName		 = fileName;
		this.methodName		 = methodName;

		String code = EJBErrorCodes.EXCEPTION_UNKNOWN;
		
		if(e instanceof NullPointerException)
			code = EJBErrorCodes.NULLPOINTER_EXCEPTION;
		else if(e instanceof IndexOutOfBoundsException)
			code = EJBErrorCodes.OUTOFINDEX_EXCEPTION;
		else if(e instanceof ClassCastException)
			code = EJBErrorCodes.CLASSCAST_EXCEPTION;
		else if(e instanceof ArithmeticException)
			code = EJBErrorCodes.ARITHMETIC_EXCEPTION;	
		else if(e instanceof IllegalStateException)
			code = EJBErrorCodes.ILLEGALSTATE_EXCEPTION;
		else if(e instanceof NoSuchElementException)		
			code= EJBErrorCodes.NOSUCHELEMENT_EXCEPTION;
		else if(e instanceof NumberFormatException)
			code = EJBErrorCodes.NUMBERFORMAT_EXCEPTION;
		else if(e instanceof SQLException)
			code= getSQLRootError(e);
		else if(e instanceof NamingException)
			code = EJBErrorCodes.NAMING_EXCEPTION;			
		else if(e instanceof CreateException)
			code = EJBErrorCodes.CREATE_EXCEPTION;
		else if(e instanceof RemoteException)
			code = getRemoteRootError(e);
		else if(e instanceof FinderException)
			code = EJBErrorCodes.FINDER_EXCEPTION;
		else if(e instanceof ParseException)
			code= EJBErrorCodes.PARSE_EXCEPTION;
		
		this.errorCode= code;
	}

    public FoursoftException(Exception e,String fileName,String methodName, String msg)
	{
		this.hiddenException = e;
		this.fileName		 = fileName;
		this.methodName		 = methodName;
        this.message         = msg;

		String code = EJBErrorCodes.EXCEPTION_UNKNOWN;
		
		if(e instanceof NullPointerException)
			code = EJBErrorCodes.NULLPOINTER_EXCEPTION;
		else if(e instanceof IndexOutOfBoundsException)
			code = EJBErrorCodes.OUTOFINDEX_EXCEPTION;
		else if(e instanceof ClassCastException)
			code = EJBErrorCodes.CLASSCAST_EXCEPTION;
		else if(e instanceof ArithmeticException)
			code = EJBErrorCodes.ARITHMETIC_EXCEPTION;	
		else if(e instanceof IllegalStateException)
			code = EJBErrorCodes.ILLEGALSTATE_EXCEPTION;
		else if(e instanceof NoSuchElementException)		
			code= EJBErrorCodes.NOSUCHELEMENT_EXCEPTION;
		else if(e instanceof NumberFormatException)
			code = EJBErrorCodes.NUMBERFORMAT_EXCEPTION;
		else if(e instanceof SQLException)
			code= getSQLRootError(e);
		else if(e instanceof NamingException)
			code = EJBErrorCodes.NAMING_EXCEPTION;			
		else if(e instanceof CreateException)
			code = EJBErrorCodes.CREATE_EXCEPTION;
		else if(e instanceof RemoteException)
			code = getRemoteRootError(e);
		else if(e instanceof FinderException)
			code = EJBErrorCodes.FINDER_EXCEPTION;
		else if(e instanceof ParseException)
			code= EJBErrorCodes.PARSE_EXCEPTION;
		
		this.errorCode= code;
	}

	private String getSQLRootError(Exception e)
	{
		String errorCode = null;
		
		String errorMessage = e.getMessage();
		SQLException se = (SQLException)e;
		
		String sqlErrorCode = se.getErrorCode()+""; 		
		errorCode = (String) EJBErrorCodes.DB_Exceptions.get(sqlErrorCode);

		if(errorCode == null)
		 	errorCode = EJBErrorCodes.SQL_EXCEPTION;

		return 	errorCode;			
	}

	
	private String getRemoteRootError(Exception e)
	{
		String errorCode =  EJBErrorCodes.REMOTE_EXCEPTION;
		
		String errorMessage = e.getMessage();
				
		if(errorMessage.indexOf("SQLException") > -1)
			errorCode = EJBErrorCodes.REMOTE_SQL_EXCEPTION;
		if(errorMessage.indexOf("NullPointerException") > -1)
			errorCode = EJBErrorCodes.REMOTE_NULLPOINTER_EXCEPTION;
		
		return 	errorCode;			
	}

	public static FoursoftException getException(Exception e,String fileName,String methodName,String message,String errorCode)
	{
		if(e instanceof FoursoftException)
			return (FoursoftException)e;
		else
			return new FoursoftException(e,fileName,methodName,message,errorCode);
	}	

	public FoursoftException(Exception e,String fileName,String methodName,String message,String errorCode)
	{
		this.hiddenException = e;
		this.fileName		 = fileName;
		this.methodName		 = methodName;
		this.message		 = message;	
		this.errorCode		 = errorCode;
	}	

	public FoursoftException(String message, Exception e)
	{
		this.message = message;
		hiddenException = e;
	}

	public FoursoftException(String message)
	{
		this.message = message;
	}

	public Exception getHiddenException()
	{
		return hiddenException;
	}

	public String getMessage()
	{
		return this.message;
	}

	public String getErrorCode()
	{
		return this.errorCode;
	}

	public String getComponentDetails()
	{
		return this.fileName+" # "+this.methodName;
	}
    
}

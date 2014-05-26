/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */

package com.foursoft.esupply.common.java;

import com.foursoft.esupply.common.java.ClientErrorCodes;
import java.text.ParseException;
import java.util.NoSuchElementException;
import javax.naming.NamingException;
import java.rmi.RemoteException;


/**
 * File			: BuildMessage.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This class contains static method used to get the client side error code. 
 * 
 * @Author by Ramakumar.B
 * @date   20-05-2003
 */		


public class BuildMessage 
{
	public BuildMessage()
	{
	}

	public static String getErrorCode(Exception e)
	{
		String code = ClientErrorCodes.EXCEPTION_UNKNOWN;
		
		if(e instanceof NullPointerException)
			code = ClientErrorCodes.NULLPOINTER_EXCEPTION;
		else if(e instanceof IndexOutOfBoundsException)
			code = ClientErrorCodes.OUTOFINDEX_EXCEPTION;
		else if(e instanceof ClassCastException)
			code = ClientErrorCodes.CLASSCAST_EXCEPTION;
		else if(e instanceof ArithmeticException)
			code = ClientErrorCodes.ARITHMETIC_EXCEPTION;	
		else if(e instanceof IllegalStateException)
			code = ClientErrorCodes.ILLEGALSTATE_EXCEPTION;
		else if(e instanceof NoSuchElementException)		
			code= ClientErrorCodes.NOSUCHELEMENT_EXCEPTION;
		else if(e instanceof ParseException)
			code= ClientErrorCodes.PARSE_EXCEPTION;
		else if(e instanceof NumberFormatException)
			code= ClientErrorCodes.NUMBERFORMAT_EXCEPTION;
		else if(e instanceof NamingException)
			code = ClientErrorCodes.NAMING_EXCEPTION;
		else if(e instanceof RemoteException)
			code = getRemoteRootError(e);
		
		return code;
	}
	
	private static String getRemoteRootError(Exception e)
	{
		String errorCode =  ClientErrorCodes.REMOTE_EXCEPTION;
		
		String errorMessage = e.getMessage();
				
		if(errorMessage.indexOf("SQLException") > -1)
			errorCode = ClientErrorCodes.REMOTE_SQL_EXCEPTION;
		if(errorMessage.indexOf("NullPointerException") > -1)
			errorCode = ClientErrorCodes.REMOTE_NULLPOINTER_EXCEPTION;
		
		return 	errorCode;			
	}

}

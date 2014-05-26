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

/**
 * File			: NotExistsException
 * sub-module	: accesscontrol
 * module		: esupply
 * 
 * This is an Exception class : NotExistException .
 * This exception used thrown, when a record is not found which your looking for
 * 
 * module 		: esupply
 * sub-module	: common
 * @author Madhu. P, 28-08-2001
 */
//Modified for New access control (Merging both Four soft Exisiting and Non Exisiting exceptions
import com.foursoft.esupply.common.exception.FoursoftApplicationException;

public class  AlreadyExistsException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30020";
	
	String message = "";
	
	public AlreadyExistsException()
	{
	}
	public AlreadyExistsException(String exp)
	{
		super(exp);
		message = exp;
		
	}
	public String toString()
	{
		return message;
	}
}

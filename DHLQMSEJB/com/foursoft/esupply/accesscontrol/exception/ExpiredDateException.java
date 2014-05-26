/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.accesscontrol.exception;

/**
 * File			: ExpiredDateException
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is an Exception class : ExpiredDateException .
 * This exception used throw, when the Date of application expires
 * 
 * @author	Madhu. V, 
 * @date	13-06-2002
 */
 
import com.foursoft.esupply.common.exception.FoursoftApplicationException;

public class ExpiredDateException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30013";
	
	public ExpiredDateException( String exp ) 
	{
		super( exp );
	}
    
	public ExpiredDateException() 
	{
		
	}    
	
/*
    public ExpiredDateException( String exp, Exception hiddenException ) 
	{
		super(exp, hiddenException);
	}
*/	
}

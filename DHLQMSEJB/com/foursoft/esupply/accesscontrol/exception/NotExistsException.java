
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
import com.foursoft.esupply.common.exception.FoursoftApplicationException;
 
public class NotExistsException extends FoursoftApplicationException
{
	
	public static String ERRORCODE = "AE-30016";
	
	/*
	 *	Empty argument constructor
	 */
	public NotExistsException() 
	{
		super();
	}
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public NotExistsException( String exp ) 
	{
		super( exp );
	}
	/*
	*	Constructor which will take Error Message String and hidden Exception as argument
	*/
/*
	public NotExistsException( String exp, Exception hiddenException ) 
	{
		super( exp , hiddenException);
	}	
*/	
	
}
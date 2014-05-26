
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
 * File			: DataIntegrityViolationException
 * sub-module	: common
 * module		: esupply
 * 
 * This is an Exception class : DataIntegrityViolationException .
 * This exception used thrown, when some other records are pointing to this record.
 * you cannot delete this
 * 
 *
 * @author Madhu. P, 28-08-2001
 */
import com.foursoft.esupply.common.exception.FoursoftApplicationException;

public class DataIntegrityViolationException extends FoursoftApplicationException
{
	public static String ERRORCODE = "AE-30012";
	/*
	*	Empty argument constructor
	*/
	public DataIntegrityViolationException() 
	{
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
	public DataIntegrityViolationException( String exp ) 
	{
		super( exp );
	}
	
	/*
	*	Constructor which will take Error Message String as argument
	*/
/*
    public DataIntegrityViolationException( String exp, Exception hiddenException ) 
	{
		super(exp, hiddenException);
	}	
*/    

}

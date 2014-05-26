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
 * File			: InvalidUnitException.java
 * sub-module 	: Common
 * module 		: eSupply
 * 
 * This is an Exception class. An object this class is suppossed to be thrown
 * from a method when a Unit passed as argument to a method does not exist
 * among the pre-defined ones (constants) 
 * 
 * @author	Amit Parekh 
 * @date	27-12-2002
 */

import com.foursoft.esupply.common.exception.FoursoftApplicationException;

public final class InvalidUnitException extends FoursoftApplicationException 
{

	public static String ERRORCODE = "AE-30028";
	
	/**
	 * The message of the exception
	 */
	private String		message	=	"";

	/**
	 * The nested exception
	 */
	private	Throwable	exception;

	/**
	 * The default Constructor
	 */
	public InvalidUnitException() {
	}

	/**
	 * The Constructor that takes only a single String message as argument
	 * 
	 * @param	String	message		The message of the exception
	 */
	public InvalidUnitException(String message) {
		this.message = message;
	}

	/**
	 * The Constructor that takes String message and an Exception object (most probably will be 
	 * the one which caused this exception) as arguments
	 * 
	 * @param	String		message			The message of the exception
	 * @param	Exception	exception		The nested exception
	 */
	public InvalidUnitException(String message, Exception exception) {
		this.message	= message;
		this.exception	= exception;
	}

	/**
	 * Gets the message of this exception
	 * 
	 * @return	The message of the exception
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets the String representation of this exception
	 * 
	 * @return	The String representation of this exception
	 */
	public String toString() {
		return this.exception.toString();
	}
}
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

import java.io.*;
import java.util.*;


/**
 * File			: ClientErrorCodes.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents contains all error codes corresponding to the all possible client side exceptions. 
 * 
 * @Author by Ramakumar.B
 * @date   20-05-2003
 */
		
public class ClientErrorCodes 
{
	public static final String EXCEPTION_UNKNOWN 			= "CE-10001";
	public static final String NULLPOINTER_EXCEPTION 		= "CE-10002";
	public static final String OUTOFINDEX_EXCEPTION 		= "CE-10003";
 	public static final String CLASSCAST_EXCEPTION 			= "CE-10004";
 	public static final String ARITHMETIC_EXCEPTION 		= "CE-10005";
 	public static final String ILLEGALSTATE_EXCEPTION 		= "CE-10006";
 	public static final String NOSUCHELEMENT_EXCEPTION 		= "CE-10007";
	public static final String PARSE_EXCEPTION 				= "CE-10008";
	public static final String NUMBERFORMAT_EXCEPTION 		= "CE-10009";
	public static final String NAMING_EXCEPTION 			= "CE-10010";
	public static final String REMOTE_EXCEPTION 			= "CE-10011";
	public static final String REMOTE_SQL_EXCEPTION			= "CE-10012";
	public static final String REMOTE_NULLPOINTER_EXCEPTION = "CE-10013";

	
	public ClientErrorCodes()
	{
	}
	
}














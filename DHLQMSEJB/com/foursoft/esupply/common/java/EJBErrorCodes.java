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

/**
 * File			: EJBErrorCodes.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents contains all error codes corresponding to the all possible server side exceptions. 
 * 
 * @Author by Ramakumar.B
 * @date   20-05-2003
 */		


public class EJBErrorCodes 
{

	public static final String EXCEPTION_UNKNOWN 			= "SE-10001";
	public static final String NULLPOINTER_EXCEPTION 		= "SE-10002";
	public static final String OUTOFINDEX_EXCEPTION 		= "SE-10003";
 	public static final String CLASSCAST_EXCEPTION 			= "SE-10004";
 	public static final String ARITHMETIC_EXCEPTION 		= "SE-10005";
 	public static final String ILLEGALSTATE_EXCEPTION 		= "SE-10006";
 	public static final String NOSUCHELEMENT_EXCEPTION 		= "SE-10007";
	public static final String PARSE_EXCEPTION 				= "SE-10008";
	public static final String NUMBERFORMAT_EXCEPTION 		= "SE-10009";
 	public static final String NAMING_EXCEPTION 			= "SE-10010";
	public static final String REMOTE_EXCEPTION 			= "SE-10011";
	public static final String REMOTE_SQL_EXCEPTION			= "SE-10012";
	public static final String REMOTE_NULLPOINTER_EXCEPTION = "SE-10013";

	public static final String CREATE_EXCEPTION 			= "SE-20001";
	public static final String FINDER_EXCEPTION 			= "SE-20002";
	

	public static final String SQL_EXCEPTION 				= "DB-30001";

	public static final java.util.HashMap DB_Exceptions = new java.util.HashMap();
	static
	{
		DB_Exceptions.put("1","DB-30002");		// unique constraint (string.string) violated
		DB_Exceptions.put("904","DB-30003");	// invalid column name
		DB_Exceptions.put("911","DB-30004");	// invalid character	
		DB_Exceptions.put("913","DB-30005");	// too many values
		DB_Exceptions.put("933","DB-30006");	// command not properly ended
		DB_Exceptions.put("1000","DB-30007");	// maximum open cursors exceeded
		DB_Exceptions.put("1006","DB-30008");	// bind variable does not exist
		DB_Exceptions.put("1008","DB-30009");	// not all variables bound
		DB_Exceptions.put("1400","DB-30010");	// cannot insert NULL into (string)
		DB_Exceptions.put("1401","DB-30011");	// inserted value too large for column
		DB_Exceptions.put("2291","DB-30012");	// integrity constraint (string.string) violated - parent key not found
		DB_Exceptions.put("2292","DB-30013");	// integrity constraint (string.string) violated - child record found
		DB_Exceptions.put("17011","DB-30014");	// Exhausted resultset.
	}

	public EJBErrorCodes()
	{
	}

}

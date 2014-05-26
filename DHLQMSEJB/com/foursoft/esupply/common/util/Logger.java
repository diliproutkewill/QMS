/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.util;

/**
 * File			: Logger.java
 * sub-module	: common
 * module		: esupply
 *
 * This class is just a helper class to make it handy
 * to print out debug statements
 * it will print three types of messages (info, warning and error) based on the LogLevel
 * by changing the level you can control the message to be printed
 *
 * @author	Madhusudhan Rao. P, 
 * @date	28-08-2001
 */

 /**
 * Method summery
 *
 * info(String fileName, String msg)
 * info(String fileName, String msg, Exception e)
 * info(String fileName, String methodName, String msg)
 * info(String fileName, String methodName, String msg, Exception exception)
 * warning(String fileName, String msg)
 * warning(String fileName, String msg, Exception e)
 * warning(String fileName, String methodName, String msg)
 * warning(String fileName, String methodName, String msg, Exception exception)
 * error(String fileName, String msg)
 * error(String fileName, String msg, Exception e)
 * error(String fileName, String methodName, String msg)
 * error(String fileName, String methodName, String msg, Exception exception)
 */

public class Logger {

	public static final int LEVEL = 4;	// log level

	public static final int ERROR	= 1; // error log level
	public static final int WARNING	= 2; // warning log level
	public static final int INFO	= 4; // info log level
  
  //public Timestamp tstmp  = null;
	/**
	* for informative messages (especially in for JSP's)
	* @param fileName
	* @param msg
	*/
	public static void info(String fileName, String msg)
	{
		log(Logger.INFO, fileName, msg);
	}
	
	/**
	* for informative messages (especially in for JSP's with exception)
	* @param fileName
	* @param msg
	* @param e
	*/	
	public static void info(String fileName, String msg, Exception e)
	{
		log(Logger.INFO, fileName, msg, e);
	}	
	
	/**
	* for informative messages (especially in for EJB's )
	* @param fileName
	* @param methodName
	* @param msg
	*/		
	public static void info(String fileName, String methodName, String msg)
	{
		log(Logger.INFO, fileName, methodName, msg);
	}
	
	/**
	* for informative messages (especially in for EJB's with exception)
	* @param fileName
	* @param methodName
	* @param msg
	* @param e
	*/		
	public static void info(String fileName, String methodName, String msg, Exception exception)
	{
		log(Logger.INFO, fileName, methodName, msg, exception);
	}

	/**
	* for warning messages (especially in for JSP's)
	* @param fileName
	* @param msg
	*/
	public static void warning(String fileName, String msg)
	{
		log(Logger.WARNING, fileName, msg);
	}
	
	/**
	* for Warning messages (especially in for JSP's with exception)
	* @param fileName
	* @param msg
	*/		
	public static void warning(String fileName, String msg, Exception e)
	{
		log(Logger.WARNING, fileName, msg, e);
	}	
	
	/**
	* for warning messages (especially in for EJB's without exception)
	* @param fileName
	* @param methodName
	* @param msg
	*/			
	public static void warning(String fileName, String methodName, String msg)
	{
		log(Logger.WARNING, fileName, methodName, msg);
	}
	
	/**
	* for warning messages (especially in for EJB's with exception)
	* @param fileName
	* @param methodName
	* @param msg
	* @param exception
	*/		
	public static void warning(String fileName, String methodName, String msg, Exception exception)
	{
		log(Logger.WARNING, fileName, methodName, msg, exception);
	}

	/**
	* for error messages (especially in for JSP's with exception)
	* @param fileName
	* @param msg
	*/	
	public static void error(String fileName, String msg)
	{
		log(Logger.ERROR, fileName, msg);
	}
	
	/**
	* for error messages (especially in for JSP's with exception)
	* @param fileName
	* @param msg
	* @param e
	*/		
	public static void error(String fileName, String msg, Exception e)
	{
		log(Logger.ERROR, fileName, msg, e);
	}	
	
	/**
	* for error messages (especially in for EJB's with exception)
	* @param fileName
	* @param methodName
	* @param msg
	*/		
	public static void error(String fileName, String methodName, String msg)
	{
		log(Logger.ERROR, fileName, methodName, msg);
	}
	
	/**
	* for error messages (especially in for EJB's with exception)
	* @param fileName
	* @param methodName
	* @param msg
	* @param exception
	*/		
	public static void error(String fileName, String methodName, String msg, Exception exception)
	{
		log(Logger.ERROR, fileName, methodName, msg, exception);
	}


	/**
	* this is used to print the message 
	* @param fileName
	* @param msg
	*/	
    private static void log(int level, String fileName, String msg) {
		if(Logger.LEVEL >= level)
            System.out.println("["+new java.util.Date()+"]"+levelToString(level)+fileName+" --  "+msg);
    }
	
	/**
	* this is used to print the message 
	* @param fileName
	* @param msg
	* @param t
	*/		
    private static void log(int level, String fileName, String msg, Throwable t) {
		if(Logger.LEVEL >= level) {
            System.out.println("["+new java.util.Date()+"]"+levelToString(level)+fileName+" --  "+msg);
            t.printStackTrace();
		}
    }	
	
	/**
	* this is used to print the message 
	* @param fileName
	* @param methodName
	* @param msg
	*/		
    private static void log(int level, String fileName, String methodName, String msg) {
		if(Logger.LEVEL >= level)
            System.out.println("["+new java.util.Date()+"]"+levelToString(level)+fileName+" : [ "+methodName+" ]-->  "+msg);
    }
	
	/**
	* this is used to print the message 
	* @param level
	* @param fileName
	* @param methodName
	* @param msg
	* @param e
	*/		
    private static void log(int level, String fileName, String methodName, String msg, Throwable t) {
		if(Logger.LEVEL >= level) {
            System.out.println("["+new java.util.Date()+"]"+levelToString(level)+fileName+" : [ "+methodName+" ]-->  "+msg);
            t.printStackTrace();
		}
    }
	
	/**
	* to get the level as a String 
	* @param level
	*/		
	private static String levelToString(int level)
	{
		if(level == 1)
			return "[ERROR   ] ";
		else if (level == 2)
			return "[WARNING ] ";
		else
			return "[INFO    ] ";
	}
}

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

import java.util.Date;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.foursoft.esupply.accesscontrol.exception.ExpiredDateException;

/**
 * File			: DateValidationSingleton.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is staic utility Class which takes and keep modified date of the application
 * 
 * @author	Madhu V, 
 * @date	13-06-2002
 */
 
public class DateValidationSingleton 
{
	private static  DateValidationSingleton _instance = new DateValidationSingleton();
	String FILE_NAME = "DateValidationSingleton.java";
  private static Logger logger = null;


    private DateValidationSingleton() {
        logger  = Logger.getLogger(DateValidationSingleton.class);
        startDate = null;
    }

    public static DateValidationSingleton getInstance() {
        return _instance;
    }
    
    public java.util.Date getDate()
    {
        return startDate;
    }
    
    public void setDate(java.util.Date myDate)
    {
        startDate = myDate;
		//Logger.error(FILE_NAME,"Mishandled Application Images on "+myDate, new ExpiredDateException("Mishandled"));
    logger.error(FILE_NAME+"Mishandled Application Images on "+myDate+ new ExpiredDateException("Mishandled"));
    }
    
	private java.util.Date startDate;
}

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
 * File			: ReportFormatter.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This class is used to hold the possible values(Domain) for User Preferences 
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	13-03-2002
 */
 
public final class UserPreferenceMaster 
{
	//,"DD-MON-YY","DD-MON-YYYY","DD/MON/YY","DD/MON/YYYY"
	public static final String[] 	DATE_FORMAT	= 
						{"DD-MM-YY", "DD/MM/YY", "MM-DD-YY", "MM/DD/YY", "DD-MM-YYYY", "DD/MM/YYYY", "MM-DD-YYYY", "MM/DD/YYYY"};

	public static final String[] 	VOLUME	= 
						{"CUBIC_METER", "CUBIC_FEET"};

	public static final String[] 	WEIGHT	= 
						{"KG", "LB"};

	public static final String[] 	DIMENSION	= 
                        {"METRIC", "ENGLISH"};
						//{"METER", "CM", "INCH", "FEET"};

	public static String[] getDateFormat()
	{
		return DATE_FORMAT;
	}
	public static String[] getVolume()
	{
		return VOLUME;
	}
	
	public static String[] getWeight()
	{
		return WEIGHT;
	}
	public static String[] getDimension()
	{
		return DIMENSION;
	}	
}

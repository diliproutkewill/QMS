/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.bean;
/**
 * File			: UserPreferences.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is a Value Object bean that encapsulates the User Preferences for different parameters
 * like Date Format, Dimension (UOM), Weight, Volume.
 * 
 * @author	Amit Parekh 
 * @date	31-07-2002
 */

import java.util.*;
import java.io.*;

public class UserPreferences implements java.io.Serializable
{
	private String	dateFormat	=	"";
	private String	dimension	=	"";
	private String	weight		=	"";
	private String	volume		=	"";

	private String	lovSize		=	"";
	private String	segmentSize		=	"";
	private String	language		=	"";
	
	public UserPreferences(	String	dateFormat, String	dimension, String	weight, String	volume )
	{
		this.dateFormat	=	dateFormat;
		this.dimension	=	dimension;
		this.weight		=	weight;
		this.volume		=	volume;
	}

	public UserPreferences(	String	dateFormat, String	dimension, String	weight, String	volume, String lovSize, String segmentSize, String language)
	{
		this.dateFormat	=	dateFormat;
		this.dimension	=	dimension;
		this.weight		=	weight;
		this.volume		=	volume;
		this.lovSize	=   lovSize;	
		this.segmentSize=   segmentSize;	
		this.language	=   language;	
	}
	

	// ACCESSOR Methods
	public String	getDateFormat() {	return	this.dateFormat;	}
	public String	getDimension() {	return	this.dimension;		}
	public String	getWeight() {		return	this.weight;		}
	public String	getVolume() {		return	this.volume;		}
	public String	getLovSize()	{		return	this.lovSize;		}
	public String	getSegmentSize(){		return	this.segmentSize;	}
	public String	getLanguage()	{		return	this.language;		}
	
}
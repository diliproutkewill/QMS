/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
 
package com.foursoft.esupply.accesscontrol.java;

/**
 * File			: VersionedModel.java
 * sub-module	: accesscontrol
 * module		: esupply
 * 
 * This class serves the common functionlity of versioning entities. Any class that needs this
 * versioning functioality can subclass this class to extends the versioning functionality.
 * This class cannot be instantiated as its declared as abstract (as a standalone it has no meaning)
 * and is sole purpose is to be extended by other classes.
 *
 * @author	Amit Parekh, 
 * @date	01-03-2003
 * 
 */
 
import java.sql.Timestamp;

public abstract class VersionedModel implements java.io.Serializable 
{

	private int			version			= 0;	// Default Version will be Zero
	private String		modifiedBy		= null;	// Initially when created, no modifying User
	private Timestamp	modifiedDate	= null;	// Initially when created, no modified Date
	
	private String		dateformat		= null;
	
	private java.text.DateFormat	dateFormat =	null;

	public String getModifiedByMessage() 
	{
		String	message = "";
		String	dateString = "";
		String	timeFormat = "HH:mm:ss";

		if(dateformat == null) 
			{
			dateformat = "MM/dd/yy";
		}

		String	format = dateformat + " 'at' " + timeFormat;
		
		dateFormat = new java.text.SimpleDateFormat( format );
		
		dateString = dateFormat.format( modifiedDate );
		
		message =	"This information was last modified by User '"+this.modifiedBy+"' on '"+dateString+"'. "+
					"Please get the latest information and repeat the process.";

		return message;
	}

	public String getModificationInfoMessage(String type) 
	{
		String	message = "";
		String	dateString = "";
		String	timeFormat = "HH:mm:ss";
		String	lineBrk = "\n";

		if(type!=null && type.equalsIgnoreCase("HTML")) 
		{
			lineBrk = "<br>";
		}

		
		if(dateformat == null) 
		{
			dateformat = "MM/dd/yy";
		}

		String	format = dateformat + " 'at' " + timeFormat;
		
		dateFormat = new java.text.SimpleDateFormat( format );
		
		if(modifiedDate!=null)
		{
		dateString = dateFormat.format( modifiedDate );
		message =	"This record has been modified "+this.version+" time(s). "+lineBrk+
					" Last modified by User '"+this.modifiedBy+"' on '"+dateString+"'.";
		}
		else
			message ="";

		return message;
	}

	public VersionedModel() 
	{
	}

	// Accessors

	public int getVersion() 
	{
		return version;
	}

	public String getModifiedBy() 
	{
		return modifiedBy;
	}

	public Timestamp getModifiedDate() 
	{
		return modifiedDate;
	}

	// Mutators


	public void setVersion(int version) {
		this.version = version;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
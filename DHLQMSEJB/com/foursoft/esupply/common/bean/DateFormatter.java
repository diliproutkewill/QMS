package com.foursoft.esupply.common.bean;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Locale;

	
public class DateFormatter //implements java.io.Serializable
{
	private DateFormat		df;	 
	private	Locale			locale;
    private String 			dateFormat;
	private static DateFormatter	defaultDateFormatter= null ;
	
	public static DateFormatter getInstance()
	{
		if(defaultDateFormatter ==null)
		{
			defaultDateFormatter =new DateFormatter();
		}
		return defaultDateFormatter;
	}

    public DateFormatter()
    {
		dateFormat		= "DD/MM/YY";
		locale			= Locale.UK;
		df				= DateFormat.getDateInstance(DateFormat.SHORT, locale);
    }	             
    public DateFormatter(Locale locale)
    {
		dateFormat		= "DD/MM/YY";
		this.locale			= locale;
		df				= DateFormat.getDateInstance(DateFormat.SHORT, locale);
    }	             
	public Timestamp convertToTimestamp(String strDate) throws java.text.ParseException
	{
		Date dt = df.parse(strDate);
		return new Timestamp(dt.getTime());
	}

	public String convertToString(Timestamp timestamp)
	{
		return df.format(timestamp);
	}

	public String getCurrentDateString()
	{
		Date dt = new Date();
		return df.format(dt);
	}
	public static String getCurrentTimeString()
	{
		java.util.Date dt = new java.util.Date();
		java.util.Calendar cal	= java.util.Calendar.getInstance();
		cal.setTime(dt);
		return cal.get(java.util.Calendar.HOUR_OF_DAY)+":"+cal.get(java.util.Calendar.MINUTE)+":"+cal.get(java.util.Calendar.SECOND);
		
		/*
		Calendar cal = Calendar.getInstance();
		Date dt=cal.getTime();
		return df.format(dt);
		*/
	}
}
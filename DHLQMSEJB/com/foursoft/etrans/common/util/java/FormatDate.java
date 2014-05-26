package com.foursoft.etrans.common.util.java;

/*
 * @(#) FormatDate.java		1.0		2002/03/20
 *
 * Copyright (c) 2001 Four Soft Pvt Ltd.
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 * 
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
/**
 * This class contains a number of static methods that can be used to
 * validate the format of date and parsing the date, 
 * typically received as input from a user
 *
 * @author Madhu. P, 
 * @version 1.0
 */
public class FormatDate
{
	private static final String FILE_NAME = "FormatDate.java";
	static final String dateSeperator	="/";
	static final String timeSeperator	=":";
  private static Logger logger = null;

	//private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK);
	
	public FormatDate()
  {
    logger  = Logger.getLogger(FormatDate.class);
  }
  /**
   * 
   * @param stringDate
   * @return timeStamp
   */
	public static Timestamp doParseDate(String stringDate)
	{
		StringTokenizer		st			= null;
		GregorianCalendar	gc			= null;
		Timestamp			timeStamp	= null;
		int year	= 0;
		int month	= 0;
		int day		= 0;
		try
		{
		st	=	new  StringTokenizer(stringDate,dateSeperator);
		if(st.hasMoreTokens())
	   { 	year = Integer.parseInt(st.nextToken()); }
		
		if(st.hasMoreTokens())
			{ month = Integer.parseInt(st.nextToken()); }
		if(st.hasMoreTokens())
			{ day = Integer.parseInt(st.nextToken());} 
			
		gc = new GregorianCalendar(year,month-1,day);
		timeStamp = new Timestamp((gc.getTime()).getTime());
		}
		catch(Exception e)
		{
//			Logger.error(FILE_NAME,"Exception in parsing the date	of FormatDate .getTimeStamp()"+e.toString());
		}
	
		return timeStamp;
}

// for inserting onlyDate...

  /**
   * 
   * @param stringDate
   * @param dateFormat
   * @return timeStamp
   */
public static Timestamp doParseDate(String stringDate, String dateFormat)
	{
		StringTokenizer		st			= null;
		StringTokenizer		df			= null;
		
		GregorianCalendar	gc			= null;
		Timestamp			timeStamp	= null;
					
		String[] months = {"","JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};

		int year	= 0;
		int month	= 0;
		int day		= 0;
		try
		{
			
		st		=	new  StringTokenizer(stringDate,"-,/");
		df		=	new  StringTokenizer(dateFormat,"-,/");
		
		while(df.hasMoreTokens())
		{
			String dToken = df.nextToken();
			String sToken = st.nextToken();

			if(dToken.startsWith("y"))
				{ year = Integer.parseInt(sToken); }
				
			else if(dToken.equalsIgnoreCase("MM"))
				{ month = Integer.parseInt(sToken);		} 

			else if(dToken.equalsIgnoreCase("MMM"))
			{
				for(int i=1;i<=13;i++)
				{
					if(months[i].equalsIgnoreCase(sToken))
					{
						month = i;
						break;
					}
				}

			}
			else if(dToken.startsWith("d"))
					{ day = Integer.parseInt(sToken); }
		}	
				
		gc = new GregorianCalendar(year,month-1,day);
		timeStamp = new Timestamp((gc.getTime()).getTime());
		}
		catch(Exception e)
		{
//			Logger.error(FILE_NAME,"Exception in parsing the date	of FormatDate .getTimeStamp()"+e.toString());
		}
	
		return timeStamp;
	}
	
	

	// For inserting Date and Time
	
  /**
   * 
   * @param stringDate
   * @param stringTime
   * @param dateFormat
   * @return timeStamp
   */
	public static Timestamp doParseDateTime(String stringDate, String stringTime, String dateFormat)
	{
		StringTokenizer		stTime			= null;
		
		StringTokenizer		st			= null;
		StringTokenizer		df			= null;
		
		GregorianCalendar	gc			= null;
		Timestamp			timeStamp	= null;

		int year		= 0;
		int month		= 0;
		int day			= 0;

		int hour		= 0;
		int minute		= 0;
		int seconds		= 0;
		
		if(!(stringDate==null || stringDate.equals("")))
		{
			try
			{
				String[] months = {"","JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
				
				st		=	new  StringTokenizer(stringDate,"-,/");
				df		=	new  StringTokenizer(dateFormat,"-,/");
		
				while(df.hasMoreTokens())
				{
					String dToken = df.nextToken();
					String sToken = st.nextToken();

					if(dToken.startsWith("y"))
						{ year = Integer.parseInt(sToken); }
				
					else if(dToken.equalsIgnoreCase("MM"))
           {	month = Integer.parseInt(sToken);		}
 
					else if(dToken.equalsIgnoreCase("MMM"))
					{
						for(int i=1;i<=13;i++)
						{
							if(months[i].equalsIgnoreCase(sToken))
							{
								month = i;
								break;
							}
						}

					}
					else if(dToken.startsWith("d"))
						{ day = Integer.parseInt(sToken); }

				}	


				try
				{
					if(stringTime.equals(""))
						{ stringTime = null; }
				
					stTime	=	new  StringTokenizer(stringTime,timeSeperator);
					if(stTime.hasMoreTokens())
						{ hour = Integer.parseInt(stTime.nextToken());}

					if(stTime.hasMoreTokens())
						{ minute = Integer.parseInt(stTime.nextToken());}

					if(stTime.hasMoreTokens())
						{ seconds = Integer.parseInt(stTime.nextToken()); }
				}
				catch(Exception e)
				{
					hour		= 0;
					minute		= 0;
					seconds		= 0;
				}

				gc = new GregorianCalendar(year, month-1, day, hour, minute, seconds);
				timeStamp = new Timestamp((gc.getTime()).getTime());
			}
			catch(Exception e)
			{
//				Logger.error(FILE_NAME,"Exception in parsing the date	of FormatDate .getTimeStamp()"+e.toString());
			}
		}
		//Logger.info(FILE_NAME,"doParseDateTime  timeStamp : "+timeStamp);
    logger.info(FILE_NAME+"doParseDateTime  timeStamp : "+timeStamp);
		return timeStamp;
	}
	
	
	// for getting Date and Time

  /**
   * 
   * @param timeStamp
   * @param dateFormat
   * @return stringDateNTime
   */
	public static String[] doFormatDateTime(Timestamp timeStamp,String dateFormat)
	{
		String stringDateNTime[]	=new String[2];

//		int year		= 0;
//		int month		= 0;
//		int day			= 0;
//		int hour		= 0;
//		int minute		= 0;
//		int seconds		= 0;

		GregorianCalendar	gc	= null;

		try
		{
			gc = new GregorianCalendar();
			gc.setTime(new Date(timeStamp.getTime()));
			
			if(gc.get(Calendar.DAY_OF_MONTH) < 10 )
				{ stringDateNTime[0]	=  "0"+gc.get(Calendar.DAY_OF_MONTH)+dateSeperator;}
			else
				{ stringDateNTime[0]	= gc.get(Calendar.DAY_OF_MONTH)+dateSeperator; }
			
			if((gc.get(Calendar.MONTH)+1) < 10)
			{ 	stringDateNTime[0]	+= "0"+(gc.get(Calendar.MONTH)+1)+dateSeperator;} 
			else
			 {  stringDateNTime[0]	+=gc.get(Calendar.MONTH)+1+dateSeperator;}
			 
			stringDateNTime[0]		+= gc.get(Calendar.YEAR);      
			
			//  stringDateNTime[0] = doFormatDate(timeStamp,dateFormat);
		    
		    //Logger.info(FILE_NAME,"stringDateNTime[0] : "+stringDateNTime[0]);
        logger.info(FILE_NAME+"stringDateNTime[0] : "+stringDateNTime[0]);
		 					
			
			if(gc.get(Calendar.HOUR_OF_DAY) < 10)
				{ stringDateNTime[1]		= "0"+gc.get(Calendar.HOUR_OF_DAY)+timeSeperator;}
			else
				{ stringDateNTime[1]		= gc.get(Calendar.HOUR_OF_DAY)+timeSeperator; }
			
			if(gc.get(Calendar.MINUTE) < 10)
				{ stringDateNTime[1]		+= "0"+gc.get(Calendar.MINUTE); }
			else
				{ stringDateNTime[1]		+= gc.get(Calendar.MINUTE);} 
				
		/*	if(gc.get(Calendar.MINUTE) < 10)
				stringDateNTime[1]		+= "0"+gc.get(Calendar.MINUTE)+timeSeperator;
			else
				stringDateNTime[1]		+= gc.get(Calendar.MINUTE)+timeSeperator;
			
			if(gc.get(Calendar.SECOND) < 10)
				stringDateNTime[1]		+= "0"+gc.get(Calendar.SECOND);
			else
				stringDateNTime[1]		+= gc.get(Calendar.SECOND);*/

		}
		catch(Exception e)
		{
			stringDateNTime[0]="";
			stringDateNTime[1]="";
		}
		return stringDateNTime;
	}
	



	// for getting only Date

  /**
   * 
   * @param timeStamp
   * @param formatType
   * @return string
   */
	public static String doFormatDate(Timestamp timeStamp, String formatType) 
    {
        Date date = (Date)timeStamp;
        DateFormat dateFormat1 = new SimpleDateFormat(formatType);
		
		return dateFormat1.format(date);
    }
	
  /**
   * 
   * @param timeStamp
   * @return string
   */
	public static String doFormatDate(Timestamp timeStamp)
	{
		String stringDate	="";
		int year	= 0;
		int month	= 0;
		int day		= 0;
		GregorianCalendar	gc	= null;
		try
		{
		gc = new GregorianCalendar();
		gc.setTime(new Date(timeStamp.getTime()));

		stringDate	+= gc.get(Calendar.YEAR)+dateSeperator;
		if((gc.get(Calendar.MONTH)+1) < 10)
			{ stringDate	+= "0"+(gc.get(Calendar.MONTH)+1)+dateSeperator; }
		else
		  {  stringDate	+=gc.get(Calendar.MONTH)+1+dateSeperator;}
		if(gc.get(Calendar.DAY_OF_MONTH) < 10 )
			{ stringDate	+=  "0"+gc.get(Calendar.DAY_OF_MONTH); }
		else
			{ stringDate	+= gc.get(Calendar.DAY_OF_MONTH); }
		
		}
		catch(Exception e)
		{
//			Logger.error(FILE_NAME,"Exception in getting the Date in the format yyyy-mm-dd	"+e.toString());
		    stringDate="";
		}
		return stringDate; 
	}			
	
}

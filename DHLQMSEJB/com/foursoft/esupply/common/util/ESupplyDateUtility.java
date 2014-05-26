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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.foursoft.esupply.common.exception.InvalidDateFormatException;
import com.foursoft.esupply.common.exception.InvalidDateDelimiterException;
import org.apache.log4j.Logger;

/**
 * File			: ESupplyDateUtility.java
 * sub-module	: common.util
 * module		: esupply
 *
 * This is a utility class to get the Timestamp or date sting according to the user preferences.
 * @author	Ramakumar.B 
 * @date	29-10-2002
 * @version
 */
 
 /**
  * Method summery
  * 
  * setPattern(String format)
  * setPatternWithTime(String format)
  * getTimestamp(String format,String date)
  * getTimestampWithTime(String format,String date,String time)
  * getDisplayString(Timestamp date)
  * getDisplayStringArray(Timestamp date)
  * getCurrentDateString(String format)	
  * getCurrentDateStringWithTime(String format) 	
  * getFirstDayofMonth(String format)		
  * getFortNightDateString(String format)
  */	
 	
  
 
public class ESupplyDateUtility implements Serializable
{
    /**
     * @associates String 
     */
	private static Hashtable patternList = null ;

    /**
     * @associates String 
     */
	private static Hashtable patternListWithTime = null;
	// Murali Added Regarding Time Format
  private static Hashtable patternListWithTimeAndSeconds  = null;
  // Murali
  private SimpleDateFormat dateFormatter = new SimpleDateFormat(); 
	  private static Logger logger = null;
	/*
	 * setPattern(String format) is used to set the display pattern to the SimpleDate Format object,
	 * to display the date according to the user preferences.
	 */
	
  /**
   * 
   * @param format
   * @throws com.foursoft.esupply.common.exception.InvalidDateFormatException
 
   */
   public ESupplyDateUtility()
  {
    logger  = Logger.getLogger(ESupplyDateUtility.class);
   
  }
	public void setPattern(String format) throws InvalidDateFormatException
	{
		if(patternList == null)
			setPatternList() ;
			
		String displyPattern = (String)patternList.get(format);
		dateFormatter.applyPattern(displyPattern); // setting the disply pattern to SimpleDateFormat object.
		
		if(displyPattern == null)
			throw new InvalidDateFormatException("Undefined Date Format usage.");
	}
	
	/*
	 * setPatternWithTime(String format) is used to set the display pattern to the SimpleDate Format object,
	 * to display the date along with time according to the user preferences.
	 */
	
  /**
   * 
   * @param format
   * @throws com.foursoft.esupply.common.exception.InvalidDateFormatException
   */
	public void setPatternWithTime(String format) throws InvalidDateFormatException
	{
		if(patternListWithTime == null)
			setPatternListWithTime();
		
		String displyPattern = (String)patternListWithTime.get(format);
		dateFormatter.applyPattern(displyPattern); // setting the disply pattern to SimpleDateFormat object.
		
		if(displyPattern == null)
			throw new InvalidDateFormatException("Undefined Date Format usage.");
	} 
 
 // Murali Added Regarding Time Format
 public void setPatternWithTimeAndSeconds(String format) throws InvalidDateFormatException
	{
		if(patternListWithTimeAndSeconds == null)
			setPatternListWithTimeAndSeconds();
		
		String displyPattern = (String)patternListWithTimeAndSeconds.get(format);
		dateFormatter.applyPattern(displyPattern); // setting the disply pattern to SimpleDateFormat object.
		
		if(displyPattern == null)
			throw new InvalidDateFormatException("Undefined Date Format usage.");
	}
 // Murali
	/*
	 * setPatternList() is a private method used to populate the different display patterns in to the
	 * Hashtable.
	 */
	
	private void setPatternList()
	{
		patternList = new Hashtable(8);
		patternList.put("DD-MM-YY","dd-MM-yy");
		patternList.put("DD-MM-YYYY","dd-MM-yyyy");
		patternList.put("MM-DD-YY","MM-dd-yy");
		patternList.put("MM-DD-YYYY","MM-dd-yyyy");
		patternList.put("DD/MM/YY","dd/MM/yy");
		patternList.put("DD/MM/YYYY","dd/MM/yyyy");
		patternList.put("MM/DD/YY","MM/dd/yy");
		patternList.put("MM/DD/YYYY","MM/dd/yyyy");
    patternList.put("DD-MON-YY","dd-MMM-yy");
		patternList.put("DD-MON-YYYY","dd-MMM-yyyy");
		patternList.put("DD/MON/YY","dd/MMM/yy");
		patternList.put("DD/MON/YYYY","dd/MMM/yyyy");
    patternList.put("DD-MONTH-YYYY","dd-MMMMM-yyyy");//this is to get full month name
		
	}
	
	/*
	 * setPatternListWithTime() is a private method used to populate the different display patterns with time 
	 * in to the Hashtable.
	 */
	
	private void setPatternListWithTime()
	{
		patternListWithTime = new Hashtable(8);
		patternListWithTime.put("DD-MM-YY","dd-MM-yy HH:mm");
		patternListWithTime.put("DD-MM-YYYY","dd-MM-yyyy HH:mm");
		patternListWithTime.put("MM-DD-YY","MM-dd-yy HH:mm");
		patternListWithTime.put("MM-DD-YYYY","MM-dd-yyyy HH:mm");
		patternListWithTime.put("DD/MM/YY","dd/MM/yy HH:mm");
		patternListWithTime.put("DD/MM/YYYY","dd/MM/yyyy HH:mm");
		patternListWithTime.put("MM/DD/YY","MM/dd/yy HH:mm");
		patternListWithTime.put("MM/DD/YYYY","MM/dd/yyyy HH:mm");
    patternListWithTime.put("DD-MON-YY","dd-MMM-yy HH:mm");
		patternListWithTime.put("DD-MON-YYYY","dd-MMM-yyyy HH:mm");
		patternListWithTime.put("DD/MON/YY","dd/MMM/yy HH:mm");
		patternListWithTime.put("DD/MON/YYYY","dd/MMM/yyyy HH:mm");
    patternListWithTime.put("DD-MONTH-YYYY","dd-MMMMM-yyyy HH:mm");//this is to get full month name
    
	}
  
  // Murali Added Regarding Time Format
  private void setPatternListWithTimeAndSeconds()
	{
		patternListWithTimeAndSeconds = new Hashtable(8);
		patternListWithTimeAndSeconds.put("DD-MM-YY","dd-MM-yy HH:mm:ss");
		patternListWithTimeAndSeconds.put("DD-MM-YYYY","dd-MM-yyyy HH:mm:ss");
		patternListWithTimeAndSeconds.put("MM-DD-YY","MM-dd-yy HH:mm:ss");
		patternListWithTimeAndSeconds.put("MM-DD-YYYY","MM-dd-yyyy HH:mm:ss");
		patternListWithTimeAndSeconds.put("DD/MM/YY","dd/MM/yy HH:mm:ss");
		patternListWithTimeAndSeconds.put("DD/MM/YYYY","dd/MM/yyyy HH:mm:ss");
		patternListWithTimeAndSeconds.put("MM/DD/YY","MM/dd/yy HH:mm:ss");
		patternListWithTimeAndSeconds.put("MM/DD/YYYY","MM/dd/yyyy HH:mm:ss");
	}
  // Murali
	
	/*
	 * getTimestamp(String format,String date) is used to get the Timestamp for the given date. 
	 */
	
  /**
   * 
   * @param format
   * @param date
   * @return Timestamp
   * @throws com.foursoft.esupply.common.exception.InvalidDateFormatException
   * @throws com.foursoft.esupply.common.exception.InvalidDateDelimiterException
   */
	public Timestamp getTimestamp(String format,String date) throws InvalidDateFormatException,InvalidDateDelimiterException
	{
	
		DateFormat df  		= null;
		Date  utilDate 		= null;
		Timestamp timestamp	= null;	
		
		int i = date.indexOf("-");  // This is to check delimiter in the user input.
	
		if(i > -1)
		{
			date = date.replace('-','/');
		}
	
	
		// Creation DateFormat object based on the format required by the user.
		
		if(format.equals("DD/MM/YY") || format.equals("DD/MM/YYYY") || format.equals("DD-MM-YY") || format.equals("DD-MM-YYYY"))
		{
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.UK);
		}	
		
		if(format.equals("MM/DD/YY") || format.equals("MM/DD/YYYY") || format.equals("MM-DD-YY") || format.equals("MM-DD-YYYY"))
		{	
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.US);
		}	
		
		if(df == null)
		{
			throw new InvalidDateFormatException("Undefined Date Format usage.");
		}
			
		if(df != null)
		{	
			try
			{	
				utilDate = df.parse(date);  // Parsing input date into java.util.Date object.
			}
			catch(ParseException pe)
			{
				throw new InvalidDateDelimiterException("Invalid Date delimiter usage.");
			}		
		}	
	
		if(utilDate != null)
		{	
			timestamp = new java.sql.Timestamp(utilDate.getTime());
		}	
			
		return timestamp;
	}

	/*
	 * getTimestampWithTime(String format,String date,String time) is used to get the Timestamp with time for the given date.
	 */
	
  /**
   * 
   * @param format
   * @param date
   * @param time
   * @return Timestamp
   * @throws com.foursoft.esupply.common.exception.InvalidDateFormatException
   * @throws com.foursoft.esupply.common.exception.InvalidDateDelimiterException
   */
	public Timestamp getTimestampWithTime(String format,String date,String time) throws InvalidDateFormatException,InvalidDateDelimiterException
	{
	
		int hour  			= 0;
		int minute			= 0;
		DateFormat df  		= null;
		Date  utilDate 		= null;
		Timestamp timestamp	= null;	
		Calendar  c 		= null;		
		
		int i = date.indexOf("-");  // This is to check delimiter in the user input.
	
		if(i > -1)
		{
			date = date.replace('-','/');
		}
	
		if(time != null && time.length() >2)
		{
			hour   = Integer.parseInt(time.substring(0,2));
			minute = Integer.parseInt(time.substring(3,5));
		}
	
		// Creation DateFormat object based on the format required by the user.
		
		if(format.equals("DD/MM/YY") || format.equals("DD/MM/YYYY") || format.equals("DD-MM-YY") || format.equals("DD-MM-YYYY"))
		{
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.UK);
		}	
		
		if(format.equals("MM/DD/YY") || format.equals("MM/DD/YYYY") || format.equals("MM-DD-YY") || format.equals("MM-DD-YYYY"))
		{	
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.US);
		}	
		
		if(df == null)
		{
			throw new InvalidDateFormatException("Undefined Date Format usage.");
		}
			
		if(df != null)
		{	
			try
			{	
				utilDate = df.parse(date);     // Parsing input date into java.util.Date object.
				c = df.getCalendar(); // Instanciate Calendar object from DateFormat object
				c.setTime(utilDate);           // setting the time to Calndar object.   
				c.set(Calendar.HOUR,hour);     // setting the HOUR to Calndar object.  
				c.set(Calendar.MINUTE,minute); // setting the MINUTE to Calndar object.
		 
       //logger.info("df"+df);
    	}
			catch(ParseException pe)
			{
				throw new InvalidDateDelimiterException("Invalid Date delimiter usage.");
			}		
		}	
	
		if(c != null)
		{	
			//logger.info("c"+c);
      timestamp = new java.sql.Timestamp(c.getTime().getTime());
		}	
			
		return timestamp;
	}
	
  // Murali Added Regarding Time Format
  public Timestamp getTimestampWithTimeAndSeconds(String format,String date,String time) throws InvalidDateFormatException,InvalidDateDelimiterException
	{
	
		int hour  			= 0;
		int minute			= 0;
    int second     = 0;
		DateFormat df  		= null;
		Date  utilDate 		= null;
		Timestamp timestamp	= null;	
		Calendar  c 		= null;		
		
		int i = date.indexOf("-");  // This is to check delimiter in the user input.
	
		if(i > -1)
		{
			date = date.replace('-','/');
		}
	
		if(time != null && time.length() >2)
		{
			hour   = Integer.parseInt(time.substring(0,2));
			minute = Integer.parseInt(time.substring(3,5));
      second = Integer.parseInt(time.substring(6,8));
		}
	
		// Creation DateFormat object based on the format required by the user.
		
		if(format.equals("DD/MM/YY") || format.equals("DD/MM/YYYY") || format.equals("DD-MM-YY") || format.equals("DD-MM-YYYY"))
		{
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.UK);
		}	
		
		if(format.equals("MM/DD/YY") || format.equals("MM/DD/YYYY") || format.equals("MM-DD-YY") || format.equals("MM-DD-YYYY"))
		{	
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.US);
		}	
		
		if(df == null)
		{
			throw new InvalidDateFormatException("Undefined Date Format usage.");
		}
			
		if(df != null)
		{	
			try
			{	
				utilDate = df.parse(date);     // Parsing input date into java.util.Date object.
				c = df.getCalendar(); // Instanciate Calendar object from DateFormat object
				c.setTime(utilDate);           // setting the time to Calndar object.   
				c.set(Calendar.HOUR,hour);     // setting the HOUR to Calndar object.  
				c.set(Calendar.MINUTE,minute); // setting the MINUTE to Calndar object.
        c.set(Calendar.SECOND,second);
			}
			catch(ParseException pe)
			{
				throw new InvalidDateDelimiterException("Invalid Date delimiter usage.");
			}		
		}	
	
		if(c != null)
		{	
			timestamp = new java.sql.Timestamp(c.getTime().getTime());
		}	
			
		return timestamp;
	}
  // Murali
	/*
	 * getDisplayString(Timestamp date) is used to get the display sting for the given Timestamp.
	 * This method is useful to display the date accoding to the user preferences in reports. 
	 */
	
  /**
   * 
   * @param date
   * @return String
   */
	public String getDisplayString(Timestamp date)
	{
		String  pattern = null;
		String displayString = "";
		
		if(date != null)
		{	 
			Date utilDate = new Date(date.getTime());
			displayString = dateFormatter.format(utilDate);
		}
		return displayString;
	}
	
	
	//Added by Anil.k
	public String getFortNightDateString(String format)
	{
		String displayString = "";
		Date utilDate = new Date();
		utilDate.setDate(utilDate.getDate()+29);//modified by silpa 
		if(patternList == null)
			setPatternList() ;
			
		String pattern = (String)patternList.get(format);
		SimpleDateFormat formatter = new SimpleDateFormat(); 
		formatter.applyPattern(pattern);
		
		displayString = formatter.format(utilDate);
		return displayString;
	}
	//@@ added by silpa   on 1-mar-11
	public String getFort1NightDateString(String format)
	{
		String displayString = "";
		Date utilDate = new Date();
		utilDate.setDate(utilDate.getDate()+29);
		if(patternList == null)
			setPatternList() ;
	
		String pattern = (String)patternList.get(format);
		SimpleDateFormat formatter = new SimpleDateFormat(); 
		formatter.applyPattern(pattern);
		
		displayString = formatter.format(utilDate);
		return displayString;
	}//@@ ended by silpa   on 1-mar-11
	
	/*
	 * getDisplayStringArray(Timestamp date) is used to get the array of date and time for the given 
	 * Timestamp.This method is useful to display date and time in two different text fields to the user.
	 * This method is generally useful in operation modules.  
	 */
	
  /**
   * 
   * @param date
   * @return displayArray
   */
	public String[] getDisplayStringArray(Timestamp date)
	{
		
		String[] displayArray = {"",""};
		
		if(date != null)
		{	 
			Date utilDate = new Date(date.getTime());
            
			String displayString = dateFormatter.format(utilDate);
			int i=0;
			StringTokenizer stk = new StringTokenizer(displayString," ");
			while(stk.hasMoreTokens())
			{
				displayArray[i] = stk.nextToken();
				i++ ;
			} 	
		}
		return displayArray;
	}
	
	
	/*
	 * getCurrentDateString() is used to get the display sting for current date.
	 * This method is useful to display the current date in operations or reports. 
	 */
	
  /**
   * 
   * @param format
   * @return String
   */
	public String getCurrentDateString(String format)
	{
  	String displayString = "";
		Date utilDate = new Date();
    
		if(patternList == null)
			setPatternList() ;
			
		String pattern = (String)patternList.get(format);
		SimpleDateFormat formatter = new SimpleDateFormat(); 
		formatter.applyPattern(pattern);
		
		displayString = formatter.format(utilDate);
		return displayString;
	}
	


	/*
	 * getCurrentDateStringWithTime() is used to get the display sting for current date with time.
	 * This method is useful to display the current date in operations or reports. 
	 */
	
  /**
   * 
   * @param format
   * @return String
   */
	public String getCurrentDateStringWithTime(String format)
	{
		String displayString = "";
		Date utilDate = new Date();
		
		if(patternListWithTime == null)
			setPatternListWithTime();
			
		String pattern = (String)patternListWithTime.get(format);
		SimpleDateFormat formatter = new SimpleDateFormat(); 
		formatter.applyPattern(pattern);
		
		displayString = formatter.format(utilDate);
		return displayString;
	}
  
  // Murali Added Regarding Time Format
  public String getCurrentDateStringWithTimeAndSeconds(String format)
	{
		String displayString = "";
		Date utilDate = new Date();
		
		if(patternListWithTimeAndSeconds == null)
			setPatternListWithTimeAndSeconds();
			
		String pattern = (String)patternListWithTimeAndSeconds.get(format);
		SimpleDateFormat formatter = new SimpleDateFormat(); 
		formatter.applyPattern(pattern);
		
		displayString = formatter.format(utilDate);
		return displayString;
	}
  // Murali
	/*
	 * getFirstDayofMonth(String format) is used to get the display sting for first day of month.
	 */
   
  public String getDisplayDateStringWithTime(Timestamp date,String format)
	{
		String displayString = "";
		Date utilDate = null;
		if(date != null)
		{	 
			utilDate = new Date(date.getTime());
			displayString = dateFormatter.format(utilDate);
		}
		
		if(patternListWithTime == null)
			setPatternListWithTime();
			
		String pattern = (String)patternListWithTime.get(format);
		SimpleDateFormat formatter = new SimpleDateFormat(); 
		formatter.applyPattern(pattern);
		
		displayString = formatter.format(utilDate);
		return displayString;
	}
	
  /**
   * 
   * @param format
   * @return String
   * @throws com.foursoft.esupply.common.exception.InvalidDateFormatException
   */
	public String getFirstDayofMonth(String format) throws InvalidDateFormatException
	{
		DateFormat df  		= null;
		Date  utilDate 		= new Date();
		Calendar  c 		= null;	
		String 	displayString = "";
		
		// Creation DateFormat object based on the format required by the user.
		
		if(format.equals("DD/MM/YY") || format.equals("DD/MM/YYYY") || format.equals("DD-MM-YY") || format.equals("DD-MM-YYYY"))
		{
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.UK);
		}	
		
		if(format.equals("MM/DD/YY") || format.equals("MM/DD/YYYY") || format.equals("MM-DD-YY") || format.equals("MM-DD-YYYY"))
		{	
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.US);
		}	
		
		if(df == null)
		{
			throw new InvalidDateFormatException("Undefined Date Format usage.");
		}
			
		if(df != null)
		{	
				c = df.getCalendar(); // Instanciate Calendar object from DateFormat object
				c.setTime(utilDate);           // setting the time to Calndar object.   
				c.set(Calendar.DATE,1); // setting the MINUTE to Calndar object.
		}	
	
		if(c != null)
		{	
			utilDate = c.getTime();
			setPattern(format);
			displayString = dateFormatter.format(utilDate);
		}	
		return displayString;
	}
/*  New Method add for get Time Difference. It is used in Terminal Creation and GateWay Creation*/
  /**
   * 
   * @param timeZone
   * @return String
   */
   	public String getTimeDifference(String timeZone)
	{

			String ft = "+0000";
			Calendar gc = Calendar.getInstance();
			TimeZone tz = TimeZone.getDefault(); // set default TimeZone
			TimeZone t = TimeZone.getTimeZone(timeZone); // user Timezone
			
			//def TimeZone offset 		
			int serverOffset = tz.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,
															Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);
			// user TimeZone offset
			int localOffset = t.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,
															Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);

			int relativeOffset	= localOffset - serverOffset;

			// Convert milliseconds to Hours.Minutes (Here returned Minutes are wrong)
			String hours_min = ""+relativeOffset / 3600000.0;
			
			String f_hours_min = "";
			String hours_min_sign = "+"; // Stores if time is + or -

			// Remove + or - Sign from 'hours_min'
			for(int i=0;i<hours_min.length();i++)
			{
				if( !( hours_min.charAt(i)== '+' || 	hours_min.charAt(i)=='-' ) )
					 f_hours_min = f_hours_min + hours_min.charAt(i);

				if( hours_min.charAt(i)=='-' ) 
					 hours_min_sign = "-";
			}		
			// Assign final hours_minutes back to 'hours_min'
			hours_min = f_hours_min;
			
			// Get hours from 'hours_min' (Before decimal part)
			String hours	=	hours_min.substring(0,hours_min.indexOf("."));
			// If hours is single digit then make it two digits by prefixing with '0'
				   if(!(hours.length() > 1))
						hours = "0"+hours;		
			// Get minutes from 'hours_min' (Before decimal part which is only fractional 
			// part but not actual minutes)
			String min	=	hours_min.substring(hours_min.indexOf("."));		
			// Convert the frational part of minutes to actual minutes. 
			// This will return 30.0 as minutes
			String bf_min	=	""+new Double(min).doubleValue() * 60;
			// Get minutes before decimal
			String f_min	=	bf_min.substring(0,bf_min.indexOf("."));
			// If minutes is zero then make it two digits with '00'
				   if(f_min.equals("0"))
						f_min = "00";
			// return the time difference
			 ft = hours_min_sign + hours + f_min;
				return ft; 
	}

   	public String getMM(String mon)
	{

	String [] monthArray = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	String returnValue="";

	for(int i=0; i<12; i++)
	{
	if(monthArray[i].equalsIgnoreCase(mon)){
	returnValue = String.valueOf(i+1);
	}
	
	}
	return returnValue;
	}	
	//@@Added by kiran.v on 17/11/2011 for Wpbn Issue 280269
	public String getCurrentTime(){
		 Calendar calendar = new GregorianCalendar();
		  int hour = calendar.get(Calendar.HOUR_OF_DAY);
		  int minute = calendar.get(Calendar.MINUTE);
		  String min="";
		  if(minute<=9)
			  min="0"+minute;
		  else
			  min=""+minute;
		 // int second = calendar.get(Calendar.SECOND);
		  String time=""+hour+":"+min;
		  return time;
	}
   	
   	
}

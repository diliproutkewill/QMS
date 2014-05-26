/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/*
Programme Name	:HORegistrationJspBean.java
Module    Name  :ETrans
Task			:HOCompany Registration
Sub Task		:To create a utility java object
Author Name		:Raghavender.G.
Date Started    :
Date Completed  :
Date Modified   :Sept 12,2001.By Ratan K.M.
Description     :
Method's Summary:

/* * @(#)HORegistrationJspBean.java         10/01/2001
 * * Copyright (c) 2000-2001 Four-Soft Pvt Ltd, 
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft. For more information on the Four Soft Pvt Ltd
*/

package com.foursoft.etrans.setup.company.bean;
import com.foursoft.etrans.common.bean.Address;
/**
 * @author	: 
 * @date	  : 2001-08-07
 * @version : 1.6
 */
public class HORegistrationJspBean implements java.io.Serializable
{
/** Creates a new HORegistrationJspBean Object . It's a default Constructor, which sets the class variables by default values . */	        
  public HORegistrationJspBean()
 	{
 			companyId   	  = null;
			companyName 	  = null;
			addressId   	  = 0;
			dateFormat  	  = null;
			timeZone	  	  = null;
			currencyId   	  = null;
			dayLightSavings   = "N";
			address			  = null;
			city              = null; 
			contactName       = null;
      		designation       = null; 
			iataCode		  = null;
			contactName		  = null;   
//@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005        
      contactLastName = null;    
      companyEIN  = null;
//@@ 01-05-2005      
 	}
	/**
    * returns the Company Id .
    * @return the companyId as String.
    */
	public String getCompanyId()
	{
	  	return 	companyId;
	}
	/**
    * returns the Company Name .
    * @return the companyName as String.
    */
	public String getCompanyName()
	{
    	return companyName;
	}
	/**
    * returns the Company Address Id .
    * @return the company addressId as int.
    */
	public int getAddressId()
    {
    	return addressId;
 	}
	/**
    * returns the City of Company  .
    * @return the company city.
    */
	public String getCity()
    {
    	return city;
 	}
	/**
    * returns the Company DateFormat, are it is in the format of DD/MM/YY or  MM/DD/YY.
    * @return the dateFormat as String.
    */
 	public String getDateFormat()
    {
    	return dateFormat;
 	}
	/**
    * returns the Company timeZone. 
    * @return the timeZone as String.
    */
    public String getTimeZone()
    {
    	return timeZone;
    }
   /**
    * returns the currency of the company. 
    * @return the currency as String.
    */
   public String getHCurrency()
   {
     	return currencyId;
   }
    /**
    * returns the day light savings. 
    * @return the dayLightSavings as String
    */
   public String getDayLightSavings()
   {
    	return dayLightSavings;
   }
	/**
    * returns the Address object, which contains the address details of the company.
    * @return the address as Address.
    */
   public Address getAddress()
   {
    	return address;
   }
   
   /**
    * returns the iataCode,
    * @return the iataCode as String.
    */
   public String getIataCode()
   {
    	return iataCode;
   }
   
   /**
    * returns the operationseEmailId,
    * @return the operationEmailId as String.
    */
   public String getOpEmailId()
   {
    	return opEmailId;
   }
  /**
   * 
   * @return 
   */
   public String getContactName()
   {
      	return contactName;
   }  		
  //@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005  
  /** 
   *
   * @param contactLastName
   */
  public  String getContactLastName()
  {
      return contactLastName;
  }		
/** 
   *
   * @param companyEID
   */
  public  String getCompanyEIN()
  {
      return companyEIN;
  }		  
//@@   01-05-2005   
  /**
   * 
   * @return 
   */
   public  String getDesignation()
   {
   	 return  designation;
   }
   
   
   /**
   * sets the company Id.
   * @param companyId is a String used to set companyId.
   */
  public void setCompanyId( String companyId )
	{
	  	this.companyId = companyId;
	}
   /**
   * sets the company Name.
   * @param companyName is a String used to set companyName.
   */
  public void setCompanyName( String companyName )
  {
    	this.companyName = companyName;
  }
   /**
   * sets the company address Id.
   * @param addressId is a int used to set addressId.
   */
  public void setAddressId( int addressId )
  {
    	this.addressId = addressId;
  }
   /**
   * sets the dateFormat ,are it is in the format of DD/MM/YY or MM/DD/YY .
   * @param dateFormat is a String used to set dateFormat of the company.
   */
  public void setDateFormat( String dateFormat )
  {
    	this.dateFormat = dateFormat;
  }
   /**
   * sets the company timezone.
   * @param timeZoce is a String used to set company timeZone.
   */
  public void setTimeZone( String timeZone )
  {
    	this.timeZone = timeZone;
  }
   /**
   * sets the company currency.
   * @param hCurrency is a String used to set company currency.
   */
  public void setHCurrency( String currencyId )
  {
    	this.currencyId = currencyId;
  }
  /**
   * 
   * @param currencyId
   */
  public void setCurrencyId( String currencyId )
  {
    	this.currencyId = currencyId;
  }
  /**
   * sets the company city.
   * @param city is a String used to set company city.
   */
  public void setCity( String city)
  {
    	this.city = city;
  }
   /**
   * sets the company daylight savings.
   * @param dayLghtSavings is a String used to set dayLightSavings.
   */
  public void setDayLightSavings( String dayLightSavings )
  {
    	this.dayLightSavings = dayLightSavings;
  }
   /**
   * sets the company IataCode.
   * @param iataCode is a String, used to set the company IataCode
   */
  public void setIataCode( String iataCode )
  {
    	this.iataCode = iataCode;
  }
  
  /**
   * sets the company Operations EmaiLId.
   * @param opEmaiLId is a String used to set company Operation	EmailId.
   */
  public void setOpEmailId( String opEmailId )
  {
    	this.opEmailId = opEmailId;
  }
   /**
   * sets the company Address.
   * @param address is an Address object, used to set the company Address
   */
  public void setAddress( Address address )
  {
    	this.address = address;
  }
  /**
   * 
   * @param contactName
   */
  public  void setContactName(String contactName)
  {
      	this.contactName = contactName;
  }		
  //@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005  
  /** 
   *
   * @param contactLastName
   */
  public  void setContactLastName(String contactLastName)
  {
      	this.contactLastName = contactLastName;
  }		
/** 
   *
   * @param companyEID
   */
  public  void setCompanyEIN(String companyEIN)
  {
      	this.companyEIN = companyEIN;
  }		  
//@@   01-05-2005
  /**
   * 
   * @param designation
   */
  public void  setDesignation(String designation)	  	 	 	
  {
      	this.designation = designation;
  }
  
  
  
  
  
  
	// member variables
	/** It is a String contains CompanyId. */
	private	String 	companyId   	= null;
	/** It is a String contains comapny name */
	private	String 	companyName 	= null;
	/** It is a int contains comapny addressId. */
	private	int 	  addressId   	= 0;
	/** It is a String contains date format,it informs whether it is in the format of DD/MM/YY or MM/DD/YY . */
	private	String 	dateFormat  	= null;
	/** It represents the TimeZone of the company .*/
	private	String 	timeZone	  	= null;
	/** It represents the currency */
	private	String 	currencyId   	= null;
	/** It represents the Day or Light savings. */
	private	String 	dayLightSavings = "N";
	/** It contains the company address. */
	private Address address			=null;
	/** It represents the city */
	private	String 	city            = null;
	private String	opEmailId		= null;		//This String variable stores operations emailId
	private	String	iataCode		= null;		//This String variable stores iataCode
	private String	contactName		= null;
  private String contactLastName  = null; //@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005
  private String companyEIN   =  null;  //@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005
	private String  designation     = null;
} // end of class
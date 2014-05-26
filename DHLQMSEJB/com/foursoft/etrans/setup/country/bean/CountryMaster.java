/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/*	Programme Name : CountryMaster.Java.
*	Module Name    : ETrans.
*	Task Name      : Country Master
*	Sub Task Name  : Creating the CountryMaster class in which country's Name alongwith it's Id,currency and 
*	       			 it's region of existence .are to be taken as global parameters in the programme. 
*	Author		   : 
*	Date Started   :
*	Date Ended     : Sept 06, 2001.
*	Modified Date  : Sept 06, 2001(Ratan K.M.).
*	Description    :
*	Methods		   : Methods used in this class alongwith their return types,arguments and accessors are given below.
*
*  1.    public void setCountryId( String[] countryId )//For setting up countryId 
*  2.    public  String[]   getCountryId()   //To get the countryId and whose return type will be String[] 
*  3.    public void setCountryName( String[] countryName )//To set the Country's Name whose return type will be String[].    
*  4.	 public String[] getCountryName()//To get the Country Name which will return String[] array 
*  5.    public void setCurrencyId( String[] currencyId )//To set the Currencytype array.
*  6. 	 public String[] getCurrencyId()//To get the CurrencyId whose return type will be String array. 			        	 
*  7.	 public void setRegion( String[] region )// To set different regions under which various countries exist.										
*  8.    public String[] getRegion()//To get regions whose return type will be of string array type.                                       
*  9.    public String getCountryName( String strCountryId )//To get the country's name.
* 10.    public String getCountryId( String strCountryName )//To get the country's Id.
* 11.    public String getCurrencyId( String strCountryId )//to get the currencyId.
* 12.    public int size()//To get the countryId's length.
*/

package com.foursoft.etrans.setup.country.bean;

/*
 * @author:
 * @version: 1.6
 */

public class CountryMaster implements java.io.Serializable
{
	//Default Constructor.
	public CountryMaster()
	{
   		countryId   = null;//Initialising country's Id .
		countryName = null;//Initialising country's name.
   		currencyId  = null;//Initialising country's currency Id.
   		region      = null;//Initialising region.
		area = null;//Initializing area
    invalidate=null;
    city=null;
    }
       
  /**
   * 
   * @param countryId
   */
    public void setCountryId( String[] countryId )//country Id setter method.
    {
        this.countryId = countryId;	
    }		
  /**
   * 
   * @return String
   */
    public String[] getCountryId()//Country Id's getter method.
    {
        return countryId;	
    }		
      	 		
  /**
   * 
   * @param countryName
   */
    public void setCountryName( String[] countryName )//This method is for setting Country's name.
    {
        this.countryName = countryName;	
    }	 	
  /**
   * 
   * @return String
   */
    public String[] getCountryName()//This method is for getting Country's name.
    {
        return countryName;	
    }	 	
    	          
  /**
   * 
   * @param currencyId
   */
    public void setCurrencyId( String[] currencyId )//This method is for setting currency method.
    {
        this.currencyId = currencyId;	
    }	 	
  /**
   * 
   * @return String
   */
    public String[] getCurrencyId()//This method is for getting Currency Id.
    {
        return currencyId;	
    }	 	

  /**
   * 
   * @param region
   */
    public void setRegion( String[] region )//This method is for setting up the region's name.
    {
        this.region = region;	
    }			
  /**
   * 
   * @return String
   */
    public String[] getRegion()//This method is for getting the region's name.
    {
        return region;	
    }		
  /**
   * 
   * @param area
   */
	  public void setArea( String area )//This method is for setting up the area's name.
    {
        this.area = area;	
    }			
  /**
   * 
   * @return String
   */
    public String getArea()//This method is for getting the area's name.
    {
        return area;	
    }	
    
    public void setInvalidate(String invalidate)
    {
      this.invalidate=invalidate;
    }
    public String getInvalidate()
    {
      return invalidate;
    }
    public void setCity(String city)
    {
      this.city=city;
    }
    public String getCity()
    {
      return city;
    }
	public void setCurrencyInvalidate(String invalidate)
	{
		this.currencyInvalidate=invalidate;
	}
  public String getCurrencyInvalidate()
  {
    return currencyInvalidate;
  }
  /**
   * 
   * @param strCountryId
   * @return String
   */
    public String getCountryName( String strCountryId )//This method is for getting country's name. 
    {
		//For storing the countryId array.
    	for( int i=0; i<size(); i++)
   		{
   			if( countryId[i].equals(strCountryId) )
        {
   				return countryName[i];
        }
		}//end of for loop.
	  return null;
    }
  /**
   * 
   * @param strCountryName
   * @return String
   */
    public String getCountryId( String strCountryName )//This method is for getting CountryId array.
    {   
		//for loop for getting country's name.
    	for( int i=0; i<size(); i++)
   		{
   			if( countryName[i].equals(strCountryName) )
        {
   				return countryId[i];
        }
		}//end of for loop.
		return null;
    }//end of method.
  /**
   * 
   * @param strCountryId
   * @return String
   */
    public String getCurrencyId( String strCountryId )//This method is for getting currency Id.
    {
		// for storing countryId array.
    	for( int i=0; i<size(); i++)
   		{
   			if( countryId[i].equals(strCountryId) )
        {
   				return currencyId[i];
        }
		}//for loop end.
		return null;
    }
    
    
  /**
   * 
   * @return int
   */
    public int size()
   	{
   		if( countryId!=null )
      {
   			return countryId.length;
      }
   		else
      {
   			return 0;
      }
	}
    
    	
    //datamembers	
    private String[] countryId   = null;	
    private String[] countryName = null;		    
    private String[] currencyId  = null;		
    private String[] region      = null;		
	private String area        = null;
  private String invalidate=null;
  private String city=null;
  private String currencyInvalidate=null;
} 
    
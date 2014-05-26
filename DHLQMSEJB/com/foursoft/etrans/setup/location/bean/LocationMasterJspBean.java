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
   Program Name		: LocationMasterJSPBean.java
   Module name		: HO Setup
   Task		        : Location
   Sub task			: Used for manipulation of locations
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   : 
   
   Description      :
    This class will be useful to manipulate the Details of Locations(i.e., the main details). When data is retriving 
    from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 
   Method Summary   :
    String getLocationId()    // to get LocationId
    getLocationName()    // to get Location name
	String getCountryId()    // to get CountryId
	setLocationId(String locationId)    // to set LocationId
	setLocationName(String locationName)    // to set LocationName
	setCountryId(String countryId)    // to set CountryId
*/
package com.foursoft.etrans.setup.location.bean;
/**
 * @author	: 
 * @date	  : 2001-08-07
 * @version : 1.6
 */
public class LocationMasterJspBean implements java.io.Serializable
	
{
	/**
	* An empty Constructor in which all the class variables are assigned to null if the variables are String data Type and 0 if
	* the variable is of Double Or Integer. 
	*/
	public LocationMasterJspBean()
	{
		locationId      = null;
		locationName    = null;
		countryId       = null; 
		city			= null;
		zipCode			= null;
		shipmentMode	= null;
	}	
         
 	/**
     * returns the locationId
     *
     * @return the locationId as String
     */
 	public String getLocationId()
 	{
 		return locationId;
 	}
 	/**
     * returns the Name of the location
     *
     * @return the locationName as String
     */    
 	public String getLocationName()
 	{
 		return locationName;
 	}
 	/**
     * returns the countryId
     *
     * @return the countryId as String
     */ 	
 	public  String getCountryId()
 	{
 		return countryId;
 	}
	 
	 
	 /**
     * returns the city
     *
     * @return the city as String
     */ 	
 	public  String getCity()
 	{
 		return city;
 	}
	 
	 /**
     * returns the zipCode
     *
     * @return the zipCode as String
     */ 	
 	public  String getZipCode()
 	{
 		return zipCode;
 	}
	 
	 /**
     * returns the shipmentMode
     *
     * @return the shipMentMode as String
     */ 	
 	public  String getShipmentMode()
 	{
 		return shipmentMode;
 	}
 	 	
 	/**
     * takes the Location Id as String argument and sets the same to the class variable locationId 
     *
     * @param locationId as String indicating Location Id
     */
 	  
 	public void setLocationId(String locationId)
    {
 	     this.locationId = locationId;
 	}
 	/**
     * takes the Location Name as String argument and sets the same to the class variable locationName 
     *
     * @param locationName as String indicating Name of the Location 
     */     
 	public void setLocationName(String locationName)
 	{
 	 	  this.locationName = locationName;
 	}
 	/**
     * takes the Country Id as String argument and sets the same to the class variable countryId 
     *
     * @param countryId as String indicating Country Id
     */ 	 
 	public void setCountryId(String countryId)
 	{
 	  		this.countryId = countryId ;
 	}	
	 
	 /**
     * takes the city as String argument and sets the same to the class variable city
     *
     * @param city as String indicating city
     */
 	  
 	public void setCity(String city)
    {
 	     this.city = city;
 	}	
	 
	 /**
     * takes the zipCode as String argument and sets the same to the class variable zipCode 
     *
     * @param zipCode as String indicating zipCode
     */
 	  
 	public void setZipCode(String zipCode)
    {
 	     this.zipCode = zipCode;
 	}  
	 
	 /**
     * takes the shipmentMode as String argument and sets the same to the class variable shipmentMode 
     *
     * @param shipmentMode as String indicating shipmentMode
     */
 	  
 	public void setShipmentMode(String shipmentMode)
    {
 	     this.shipmentMode = shipmentMode;
 	}   	 	
 public void setInvalidate(String invalidate)
  {
    this.invalidate=invalidate;
  }
  public String getInvalidate()
  {
    return invalidate;
  }  		   				
 	 
   public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }


    public String getRemarks()
    {
        return remarks;
    }		   		    
 	//   DATA  MEMEBERS
 	/** The Id of the Location. */    
 	private  String locationId   = null;
	/** The Name of the Location. */
 	private  String locationName = null;
	/** The Id of the Country. */
 	private  String countryId    = null;
	/** This is for storing city. */    
 	private  String city		 = null;
	/** The is for storing ZipCode. */
 	private  String zipCode		 = null;
	/** The is for storing shipMentMode */
 	private  String shipmentMode  = null;
  
  private String invalidate=null;
	private  String remarks  = null;      
 	       
 	} // END OF LOCATIONMASTERJspBean         	   	
			
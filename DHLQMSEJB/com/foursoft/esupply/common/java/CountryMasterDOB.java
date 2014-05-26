/**
* @(#)CountryMasterDOB.java
* Copytight(c) 2000-2001 FourSoft Pvt Ltd, all rights reserved
* This Software is Confidential and proprietaty information of FourSoft Pv tLtd
*/
package com.foursoft.esupply.common.java;

/**
* This class belongs to CountryMaster.
* This class is java utility class used in EJB Bean class.
* @see com.foursoft.elog.slsb.CountryMasterBean
*/
public class CountryMasterDOB implements java.io.Serializable
{
	/** This refers to Country Identification number */ 
    public String countryId;	
	
	/** This refers to County Name */	
    public String countryName;	
	
	/** This refers to Currency Identification number */	
    public String currencyId;	
	
	/** This refers to Region for the Country */	
    public String region;		
    public String area;
    public String invalidate;
	private String remarks     = null;
	private String currencyInvalidate=null;

	/**
	 * Its a default constructor which instantiates the class
	 * Creates class object with defalut values
	 */		
	public CountryMasterDOB()
	{
	}

	/**
	 * Its a parameterized constructor which instantiates the class
	 * Creates class object with parameter values
	 * @param java.lang.String countryId to be provided.
	 * @param java.lang.String currencyId to be provided.
	 * @param java.lang.String region to be provided.
	 */	
	public CountryMasterDOB(String countryId, String countryName, String currencyId, String region)
	{
   		this.countryId   = countryId;
   		this.countryName = countryName;
   		this.currencyId  = currencyId;
   		this.region      = region;
    }
    public CountryMasterDOB(String countryId, String countryName, String currencyId, String region,String area,String invalidate,String currencyInvalidate)
	{
   		this.countryId   = countryId;
   		this.countryName = countryName;
   		this.currencyId  = currencyId;
   		this.region      = region;
      this.area        = area;
      this.invalidate   = invalidate;
	  this.currencyInvalidate=currencyInvalidate;
    }
	/**
	 * Its a parameterized constructor which instantiates the class
	 * Creates class object with parameter values
	 * @param java.lang.String countryId to be provided.
	 * @param java.lang.String currencyId to be provided.
	 * @param java.lang.String region to be provided.
	 */	
	public CountryMasterDOB(String countryId, String countryName, String currencyId)
	{
   		this.countryId   	= countryId;
		this.countryName	= countryName;
   		this.currencyId  	= currencyId;
    }
	
	public CountryMasterDOB(String countryId,String countryName,String currencyId,String region,String area)
    {
        this.countryId = countryId;
        this.countryName = countryName;
        this.currencyId = currencyId;
        this.region = region;
        this.area = area;
    }

	/**
	 * This method is used to set CountryId to class variable.
	 * @param java.lang.String countryId to be provided.
	 */       
    public void setCountryId( String countryId )
    {
        this.countryId = countryId;	
    }	
	
	/** This method is used to get CountryId */	
    public String getCountryId()
    {
        return countryId;	
    }
	
	/**
	 * This method is used to set CountryName to class variable.
	 * @param java.lang.String countryName to be provided.
	 */       
    public void setCountryName( String countryName )
    {
        this.countryName = countryName;	
    }
	
	/** This method is used to get CountryName */	
    public String getCountryName()
    {
        return countryName;	
    }	 
	
	/**
	 * This method is used to set CurrencyId to class variable.
	 * @param java.lang.String currencyId to be provided.
	 */       
    public void setCurrencyId( String currencyId )
    {
        this.currencyId = currencyId;	
    }
	
	/** This method is used to get CurrencyId  */	
    public String getCurrencyId()
    {
        return currencyId;	
    }
	
	/**
	 * This method is used to set Region to class variable.
	 * @param java.lang.String region to be provided.
	 */       
    public void setRegion( String region )
    {
        this.region = region;	
    }
	
	/** This method is used to get Region */	
    public String getRegion()
    {
        return region;	
    }			
    
    public void setInvalidate(String invalidate)
    {
      this.invalidate=invalidate;
    }
    public String getInvalidate()
    {
      return invalidate;
    }
	public void setCurrencyInvalidate(String currencyInvalidate)
	{
		this.currencyInvalidate=currencyInvalidate;
	}
	public String getCurrencyInvalidate()
	{
		return currencyInvalidate;
	}
	public void setArea(String area)
    {
        this.area = area;
    }


    public String getArea()
    {
        return area;
    }

	public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }


    public String getRemarks()
    {
        return remarks;
    }
} 
    
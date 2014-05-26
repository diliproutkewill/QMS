package com.foursoft.etrans.setup.country.bean;

/**
*
* Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/**	Programme Name : CountryMasterDOB.Java.
*	Module Name    : QMS.
*	Task Name      : Country Master
*	Sub Task Name  : Creating the CountryMaster class in which country's Name alongwith it's Id,currency and 
*	       			 it's region of existence .are to be taken as global parameters in the programme. 
*	Author		   : Ravi Kumar
*	Date Started   : 07-Jul-05
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   : Methods used in this class alongwith their return types,arguments and accessors are given below.
*
*  
*/

public class CountryMasterDOB implements java.io.Serializable {
    //datamembers	
    private String countryId   = null;	
    private String countryName = null;		    
    private String currencyId  = null;		
    private String region      = null;		
	private String area        = null;
    private String remarks     = null;

    
    public CountryMasterDOB(String countryId,String countryName,String currencyId,String region,String area)
    {
        this.countryId = countryId;
        this.countryName = countryName;
        this.currencyId = currencyId;
        this.region = region;
        this.area = area;
    }


    public void setCountryId(String countryId)
    {
        this.countryId = countryId;
    }


    public String getCountryId()
    {
        return countryId;
    }


    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }


    public String getCountryName()
    {
        return countryName;
    }


    public void setCurrencyId(String currencyId)
    {
        this.currencyId = currencyId;
    }


    public String getCurrencyId()
    {
        return currencyId;
    }


    public void setRegion(String region)
    {
        this.region = region;
    }


    public String getRegion()
    {
        return region;
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
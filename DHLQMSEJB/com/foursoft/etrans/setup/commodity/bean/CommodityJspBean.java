/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/**
	Program Name	   : CommodityJspBean.java
	Module Name		   : ETrans
	Task			   : Commodity	
	Sub Task		   : ComodityjspBean	
	Author Name		   : Sivarama Krishna .V
	Date Started	   : September 9,2001
	Date Completed	   : September 9,2001
	Date Modified	   : 
	Description		   :
	     This file will be useful to manipulate the Details of Commodities.This file main perpose is to store the commodities data and 
	     get the commodities data from the data base.
    Method's Summary   :
	  public String getCommodityId()                 // getting the CommodityId
	  public String getCommodityDescription()        // getting the CommodityDescription
	  public String getCommodityHandlingInfo()       // getting the CommodityHandling Information
	  public String getCommodityType()               // getting the Commodity type
	  public void setCommodityId( String commodityId )  // Stores the Commodity Id
      public void setCommodityDescription( String commodityDescription ) // Stores the Commodity Description 
	  public void setCommodityHandlingInfo( String commodityHandlingInfo ) // Stores the Commodity Handling information 	
	  public void setCommodityType( String commodityType )	// Stores the Commodity type 
	  				
*/
package com.foursoft.etrans.setup.commodity.bean;
	
/**
 * @author  Sivarama Krishna .V
 * @version etrans1.6
 */
public class CommodityJspBean implements java.io.Serializable
{	
	
  /**
   * @param
   */
	public CommodityJspBean()  
 	{
 		commodityId 			= null;
		commodityDescription 	= null	;
		commodityHandlingInfo 	= null	;
		commodityType 			= null;
		hazardIndicator			= null;
    invalidate=null;
    subClass=null;
    unNumber=null;
    classType=null;
 	}		
	 
  /**
   * 
   * @return String
   */
	public String getCommodityId()
	{
		return 	commodityId;
	}
	
  /**
   * 
   * @return String
   */
	public String getCommodityDescription()
    {
    	return commodityDescription;
   	}
	
  /**
   * 
   * @return String
   */
   	public String getCommodityHandlingInfo()
    { 
    	return commodityHandlingInfo;
   	}
	
  /**
   * 
   * @return String
   */
   	public String getCommodityType()
    { 
    	return commodityType;
   	}
  /**
   * 
   * @return String
   */
	public String getHazardIndicator()
    { 
    	return hazardIndicator;
   	}   
	
  /**
   * 
   * @param commodityId
   */
    public void setCommodityId( String commodityId )
	{
		this.commodityId = commodityId ;
	}
	
  /**
   * 
   * @param commodityDescription
   */
	public void setCommodityDescription( String commodityDescription )
    {
    	this.commodityDescription = commodityDescription;
   	}
	
  /**
   * 
   * @param commodityHandlingInfo
   */
	public void setCommodityHandlingInfo( String commodityHandlingInfo )
    { 
    	this.commodityHandlingInfo = commodityHandlingInfo;
   	}   
	
  /**
   * 
   * @param commodityType
   */
	public void setCommodityType( String commodityType )
    { 
    	this.commodityType = commodityType;
   	} 	
  /**
   * 
   * @param hazardIndicator
   */
	public void setHazardIndicator( String hazardIndicator )
    { 
    	this.hazardIndicator = hazardIndicator;
   	}
    public void setSubClass(String subClass)
    {
      this.subClass=subClass;
    }
    public void setUnNumber(String unNumber)
    {
      this.unNumber=unNumber;
    }
    public void setClassType(String classType)
    {
      this.classType=classType;
    }
    public String getSubClass()
    {
      return subClass;
    }
    public String getUnNumber()
    {
      return unNumber;
    }
    public String getClassType()
    {
      return classType;
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
	// member variables 
		private	String commodityId 				= null;   //Storing CommodityId for processing getMethod() 
		private String commodityDescription 	= null; //Storing Commodity Description for processing getMethod()	
		private	String commodityHandlingInfo 	= null; //Storing CommodityHandling information for processing getMethod()
		private	String commodityType 			= null;  //Storing commodityType for processing getMethod()
		private	String hazardIndicator 			= null;	//Storing type of ComodityId
    private String invalidate=null;
    private String remarks                  = null;
    private String subClass=null;
    private String unNumber=null;
    private String classType=null;
    private String terminalId=null;


  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }


  public String getTerminalId()
  {
    return terminalId;
  }


    
} // end of class
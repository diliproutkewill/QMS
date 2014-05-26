/**
 * @(#)InvoiceDetails.java         16/01/2001
 *
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft. For more information on the Four Soft vist : 'www.four-soft.com'
 */
package com.foursoft.etrans.sea.setup.port.bean;

/**
 * This class will be useful to .
 * <p>
 * This object is used in .
 *
 * @version		
 * @author 		
 */
public class PortMasterJSPBean implements java.io.Serializable
{
	 private String portId;
	 private String portName;
	 private String countryId;
	 private String description;
   private String invalidate;
   private String remarks;
   private String scheduleD;
   private String scheduleK;
	 /**
	 * It's a default Constructor which creates a new PortMasterJSPBean Object.
	 */
	
  
    public PortMasterJSPBean()
	{
	   portId=null;
	   portName=null;
	   countryId=null;
	   description=null;
     invalidate=null;
     scheduleD=null;
     scheduleK=null;
	}
  /**
   * @param String portId
   */
   public void setInvalidate(String invalidate)
   {
     this.invalidate=invalidate;
   }
   public String getInvalidate()
   {
     return invalidate;
   }
    public void setPortId(String portId)
	{
	  this.portId=portId;
	}
  public void setScheduleD(String scheduleD)
  {
    this.scheduleD=scheduleD;
  }
  public void setScheduleK(String scheduleK)
  {
    this.scheduleK=scheduleK;
  }
  public String getScheduleD()
  {
    return scheduleD;
  }
  public String getScheduleK()
  {
    return scheduleK;
  }
	/**
     * returns the portId of either Shipper,Consignee or Bank
     *
     * @return the portId as String
     */
    public String getPortId()
	{
	  return portId;
	}
/**
     * @param the portName as String
     */
    public void setPortName(String portName)
	{
	   this.portName=portName;
	}
	/**
     * returns the portName of either Shipper,Consignee or Bank
     *
     * @return the portName as String
     */
    public String getPortName()
	{
	  return portName;
	}
  /**
   * @param String countryId
   */
    public void setCountryId(String countryId)
	{
	  this.countryId=countryId;
	}
	/**
     * returns the countryId of either Shipper,Consignee or Bank
     *
     * @return the countryId as String
     */
    public String getCountryId()
    {
	   return countryId;
	}
  /**
   * @param String description
   */
    public void setDescription(String description)
	{
	   this.description=description;
	}
	/**
     * returns the description of either Shipper,Consignee or Bank
     *
     * @return the description as String
     */
    public String getDescription()
	{
	   return description;
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

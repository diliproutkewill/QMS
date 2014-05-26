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
	Program Name	: ServiceLevelJspBean.java
	Module Name		: ETrans
	Task			: ServiceLevel	
	Sub Task		: 	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 12,2001
	Date Completed	: September 12,2001
	Date Modified	: May 15,2002
	Modified By		: Supraja CKM
	Reason for 
	Modifying		: Added the the ShipmentMode		
	Description		:
		This file main purpose is setting the values and getting the values of ServiceLevel Ids and Descriptions
	Method Summary	:
		public String getServiceLevelId() 			//This method is used to get theservice level ids
		public String getServiceLevelDescription()  //This method is used to get the Service level description
		public String getRemarks()  				//This method is used to get the Remarks if any
		public String getShipmentMode()	
		public String setServiceLevelId() 			//This method is used to set the Service Level Ids 
		public String setServiceLevelDescription()  //This method is used to set the Service Level Descriptions
		public String setRemarks()					//This method is used to set the Remarks if any
		public String setShipmentMode()	

*/


package com.foursoft.etrans.setup.servicelevel.bean;

  

/**
 * 
 * This class will be useful to .
 * 
 * File		  : ServiceLevelJspBean.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */

	public class ServiceLevelJspBean implements java.io.Serializable
	{	
	
  /**
   *  Default Constructor
   */
	public ServiceLevelJspBean()
 	{
 		serviceLevelId 			= null; //variable that represents ServiceLevelId
		serviceLevelDescription = null; //Varibale that represents ServiceLevelDescription
		remarks 		 		= null; //Varibale that represents Remarks if any
		shipmentMode	 		= 0;	//Varibale that represents ShipmentMode
        guaranteeDelivery   = null; //Variable  thst represents guaranteeDelivery information
		eventIds				= null; // Varibale that represents EventIds
        eventDesc               = null;//Variable that represents EventDescriptions
		originLocations			= null; // Varibale that represents Origin Locations
		destiLocations			= null; // Varibale that represents Destination Locations
		allocatedTime		   	= null; // Varibale that represents Allocated Times
		alertTime			    = null; // Varibale that represents Alert Times
		locEventIds             = null; //Variable that represents EventIds for Locations
		locAllocatedTime        = null; //Variable that represents AllocatedTime for Locations
		locAlertTime            = null; //Variable that represents AlertTime for Locations
		flag                    = false; //Variable that represents Sevice level Already is there or not
    invalidate              =null;
 	}
 		
  /**
   * 
   * @return String
   */
	public String getServiceLevelId()
	{
		return 	serviceLevelId;
	}
  /**
   * 
   * @return String
   */
	public String getServiceLevelDescription()
    	{
    		return serviceLevelDescription;
   	}
  /**
   * 
   * @return String
   */
   	public String getRemarks()
   	{ 
    		return remarks;
   	}
  /**
   * 
   * @return int
   */
	public int getShipmentMode()
   	{ 
    		return shipmentMode;
   	}
  /**
   * 
   * @return String
   */
    public String getGuaranteeDelivery()
     {
     return guaranteeDelivery;
     }
  /**
   * 
   * @return String Array
   */
   public String[] getEventIds()
   	{ 
    		return eventIds;
   	}
  /**
   * 
   * @return String Array
   */
   public String[] getEventDesc()
   	{ 
    		return eventDesc;
   	} 
	public String[] getOriginLocations()
   	{ 
    		return originLocations;
   	}
  /**
   * 
   * @return String Array
   */
	public String[] getDestiLocations()
   	{ 
    		return destiLocations;
   	}
  /**
   * 
   * @return int Array
   */
	public int[] getAllocatedTime()
   	{ 
    		return allocatedTime;
   	}
  /**
   * 
   * @return int Array
   */
	public int[] getAlertTime()
   	{ 
    		return alertTime;
   	}
  /**
   * 
   * @return String Array
   */
  public String[] getLocEventIds()
    {
      return locEventIds;
    }
  /**
   * 
   * @return String Array
   */
    public String[] getLocEventDesc()
    {
      return locEventDesc;
    }
  /**
   * 
   * @return int Array
   */
   public int[] getLocAllocatedTime()
     {
       return locAllocatedTime;
     }
  /**
   * 
   * @return int Array
   */
   public int[] getLocAlertTime()
    {
      return locAlertTime;
    }
  /**
   * 
   * @return boolean
   */
  public boolean getFlag()
    { 
    		return flag;
   	}  
  /**
   * 
   * @param serviceLevelId
   */
	public void setServiceLevelId( String serviceLevelId )
	{
		this.serviceLevelId = serviceLevelId ;
	}
	
  /**
   * 
   * @param serviceLevelDescription
   */
	public void setServiceLevelDescription( String serviceLevelDescription )
    	{
    		this.serviceLevelDescription = serviceLevelDescription;
   	}
  /**
   * 
   * @param remarks
   */
   	public void setRemarks( String remarks )
    { 
    		this.remarks = remarks;
   	}   	
  /**
   * 
   * @param shipmentMode
   */
	public void setShipmentMode(int shipmentMode)
    { 
    		this.shipmentMode = shipmentMode;
   	}   
  /**
   * 
   * @param eventIds
   */
	public void setEventIds(String[] eventIds)
    { 
    		this.eventIds = eventIds;
   	}   
  /**
   * 
   * @param eventDesc
   */
   public void setEventDesc(String[] eventDesc)
    { 
    		this.eventDesc = eventDesc;
   	}    
  /**
   * 
   * @param originLocations
   */
	public void setOriginLocations(String[] originLocations)
    { 
    		this.originLocations = originLocations;
   	}   
  /**
   * 
   * @param destiLocations
   */
	public void setDestiLocations(String[] destiLocations)
    { 
    		this.destiLocations = destiLocations;
   	}   
  /**
   * 
   * @param allocatedTime
   */
	public void setAllocatedTime(int[] allocatedTime)
    { 
    		this.allocatedTime = allocatedTime;
   	}   
  /**
   * 
   * @param alertTime
   */
	public void setAlertTime(int[] alertTime)
    { 
    		this.alertTime = alertTime;
   	}   
  /**
   * 
   * @param locEventIds
   */
  public void setLocEventIds(String[] locEventIds)
    {
      this.locEventIds=locEventIds;
    }
  /**
   * 
   * @param locEventDesc
   */
  public void setLocEventDesc(String[] locEventDesc)
    {
      this.locEventDesc=locEventDesc;
    }  
  /**
   * 
   * @param locAllocatedTime
   */
  public void setLocAllocatedTime(int[] locAllocatedTime)
    {
      this.locAllocatedTime = locAllocatedTime;
    }
  /**
   * 
   * @param locAlertTime
   */
  public void setLocAlertTime(int[] locAlertTime) 
    {
      this.locAlertTime = locAlertTime;
    }
  /**
   * 
   * @param guaranteeDelivery
   */
 public void setGuaranteeDelivery(String guaranteeDelivery)
     {
       this.guaranteeDelivery = guaranteeDelivery;
     }
  /**
   * 
   * @param flag
   */
  public void setFlag(boolean flag)
    { 
    		this.flag = flag;
   	} 
    public void setInvalidate(String invalidate)
    {
      this.invalidate=invalidate;
    }
    public String getInvalidate()
    {
      return invalidate;
    }
	// member variables 
		private	String serviceLevelId = null;
		private String serviceLevelDescription = null;	
		private	String remarks 		= null;
		private int    shipmentMode = 0;
		private String[] eventIds=null;
        private String[] eventDesc=null;
		private String[] originLocations=null;
		private String[] destiLocations=null;
		private int[]    allocatedTime=null;
		private int[]    alertTime=null;
		private String[] locEventIds=null;
		private String[] locEventDesc=null;
		private int[]    locAllocatedTime=null;
		private int[]    locAlertTime = null; 
        private String guaranteeDelivery     = null;
		private boolean flag=false;
    private String invalidate=null;
	} // end of class



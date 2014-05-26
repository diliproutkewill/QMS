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
	
	Program Name		:	GatewayJSPBean.java
	Module Name			:   ETrans
	Task				:	Gateway Master
	Sub Task			:	Add
	Author Name			:   NageswaraRao . D
	Date Started		:	October 30,2001
	Date Ended			:	October 1,2001
	Date Modified		:  
	
	Description			:	This is a Gateway Master DOB. It acts as a intermediate between the JSPs
	                        and the Session Bean. All the Details entered by the user are captured 
							 

    Method Summary      :   Address  getAddress()               // for getting the Address Details
                            String   getIndicator()       		// for getting the Indicator 
                            String   getGatewayId()       		// for getting the GatewayId
							String   getGatewayName()     		// for getting the Gateway Name
							String   getContactName()     		// for getting the Contact Name
							String   getCompanyName()     		// for getting the Company Name
							String[] getGatewayType()     		// for getting the GateWay Type String Array
							String   getTypeOfGateway()   		// for getting the Gateway Type 
							String   getNotes()           		// for getting the Notes
							String[] getTerminals()       		// for getting the Terminal String Array
							  
                                       
                            setAddress(Address address)         // for setting the Address Details
							setIndicator(String indic)        	// for setting the Indicator Details
							setGatewayId(String gatewayId)    	// for setting the Gateway Id
							setGatewayName(String gatewayName)	// for setting the Gateway Name
							setContactName(String contactName)	// for setting the Contact Name
							setCompanyName(String companyName)	// for setting the Company Name
							setGatewayType(String[] gatewayType)//for setting the Gateway Type
							setTypeOfGateway(String typeOfGateway)// for setting the Type Of Gateway
							setNotes(String notes)               // for setting the Notes
							setTerminals(String[] Terminal)      // for setting the Terminals       
                            
*/

package com.foursoft.etrans.setup.gateway.bean;

/**
 * @author	: 
 * @date	: 2001-08-07
 * @version     : 1.6
 */
public class GatewayJSPBean implements java.io.Serializable
 	{
 		 private String    gatewayId          =  null;
 		 private String    GatewayName        =  null;
 		 private String    CompanyName        =  null;
 		 private String    TerminalId         =  null;
 		 private String    ContactName        =  null;
 		 private String    GatewayType	      =  null;
 		 private String    Address1           =  null;
// @@ Unused private members are commented for TogetherJ by Suneetha on 12 Jan 05
// 		 private String    Address2           =  null;
// 		 private String    State              =  null;
// 		 private String    City               =  null;
// 		 private String    Zip                =  null;
// 		 private String    EmailId            =  null;
// 		 private String    FaxNo              =  null;
// 		 private String    PhoneNo            =  null;
// @@
 		 private String    countryId          =  null;
 		 private String    Notes              =  null;
 		 private String    Indicator          =  null;
 		 private String    typeofGateway      =  null;
		 private String    timeZone      	  =  null;
		 private String    serverTimeDiff     =  null; 
		  
 		 com.foursoft.etrans.common.bean.Address address    =  null;
 		 private String[]   terminals         = null;
  /**
   * 
   */
 		 public GatewayJSPBean()
 		 	{
 		 		 gatewayId          =  null;
 		         GatewayName        =  null;
 		         CompanyName        =  null;
 		         TerminalId         =  null;
 		         ContactName        =  null;
 		         GatewayType        =  null;
 		         typeofGateway      =  null;
 		         
 		         Notes              =  null;
 		         Indicator          =  null;
 		         address            =  null;
 		         terminals          =  new String[]{"1"}; 
				 serverTimeDiff     =  null;
 		    }
 		    
  /**
   * 
   * @return 
   */
 		 public  com.foursoft.etrans.common.bean.Address getAddress()
 		 	{
 		 		 return address;
 		 	}
  /**
   * 
   * @param address
   */
 		 public void setAddress(com.foursoft.etrans.common.bean.Address address)
 		 	{
 		 		this.address = address;
 		 	}	 
  /**
   * 
   * @param countryId
   */
		public void setCountryId(String countryId)
 		{
 			this.countryId = countryId;
 		 }	
  /**
   * 
   * @return 
   */
		public String getCountryId()
 		{
 			 return countryId;
 		 }	
		  		 
  /**
   * 
   * @return 
   */
 		   public String getIndicator()
 		  {
 		  	 return Indicator;
 		  }
  /**
   * 
   * @param Indicator
   */
 		  public void setIndicator(String Indicator)
 		  	{
 		  		 this.Indicator=Indicator;
 		  	}  
  /**
   * 
   * @param serverTimeDiff
   */
		public void setServerTimeDiff(String serverTimeDiff)
		  {
			this.serverTimeDiff  =  serverTimeDiff;
          }
  /**
   * 
   * @return 
   */
 		  public String getGatewayId()
 		  {
 		  	 return gatewayId;
 		  }
  /**
   * 
   * @param gatewayId
   */
 		  public void setGatewayId(String gatewayId)
 		  	{
 		  		 this.gatewayId=gatewayId;
 		  	}
  /**
   * 
   * @return 
   */
 		  public String getGatewayName()
 		  {
 		  	 return GatewayName;
 		  }
  /**
   * 
   * @param GatewayName
   */
 		  public void setGatewayName(String GatewayName)
 		  	{
 		  		 this.GatewayName=GatewayName;
 		  	}
  /**
   * 
   * @return 
   */
 		  public String getCompanyName()
 		   {
 		  	 return CompanyName;
 		   }
  /**
   * 
   * @param CompanyName
   */
 		  public void setCompanyName(String CompanyName)
 		  	{
 		  		 this.CompanyName=CompanyName;
 		  	}
 		  
  /**
   * 
   * @return 
   */
 		 public String getContactName()
 		   {
 		  	 return ContactName;
 		   }
  /**
   * 
   * @param ContactName
   */
 		  public void setContactName(String ContactName)
 		  	{
 		  		 this.ContactName=ContactName;
 		  	} 			 	        
  /**
   * 
   * @return 
   */
 		 public String getGatewayType()
 		   {
 		  	 return GatewayType;
 		   }
  /**
   * 
   * @param GatewayType
   */
 		 public void setGatewayType(String GatewayType)
 		  	{
 		  		 this.GatewayType=GatewayType;
 		  	}
  /**
   * 
   * @return 
   */
 		  public String getTypeofGateway()
 		   {
 		  	 return typeofGateway;
 		   }
  /**
   * 
   * @param typeofGateway
   */
 		 public void setTypeofGateway(String typeofGateway)
 		  	{
 		  		 this.typeofGateway=typeofGateway;
 		  	}	
  /**
   * 
   * @return 
   */
 		 public String getAddress1()
 		   {
 		  	 return Address1;
 		   }
 		 
  /**
   * 
   * @return 
   */
 		  public String getNotes()
 		  {
 		  	 return Notes;
 		  }
  /**
   * 
   * @param Notes
   */
 		  public void setNotes(String Notes)
 		  	{
 		  		 this.Notes = Notes;
 		  	}
  /**
   * 
   * @return 
   */
 		 public String[] getTerminals()
 		  {
 		  	 return terminals;
 		  }
  /**
   * 
   * @param terminals
   */
 		  public void setTerminals(String[] terminals)
 		  	{
 		  		 this.terminals = terminals;
 		  	} 	
	    
  /**
   * 
   * @return 
   */
	    public String getTimeZone()
	   	{
			return timeZone;
	   	}
  /**
   * 
   * @param timeZone
   */
	   public void setTimeZone( String timeZone)
       { 
          this.timeZone            = timeZone;
       }	   
  /**
   * 
   * @return 
   */
	   public String getServerTimeDiff()
		{
		   return serverTimeDiff;
		}
 	}	  	
 		  		
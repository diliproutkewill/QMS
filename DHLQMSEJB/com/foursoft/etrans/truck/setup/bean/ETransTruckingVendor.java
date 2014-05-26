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
	Program Name	: ETransTruckingVendor.java
	Module Name		: ETrans
	Task			: VendorMaster
	Sub Task		: VendorMasterJavaObject
	Author Name		: Shailendra Chak
	Date Started	: September 21,2001
	Date Completed	: September 21,2001 
	Date Modified	: 
	Description		: The main purpose is to get and set all the datafields in the database.
	
	* To retrieve values from database, get methods are used.
	* To set values from database, set methods are used.
*/ 

package com.foursoft.etrans.truck.setup.bean;
/* @Author Name		: Shailendra Chak
 * @version     : 1.6
 */
	public class ETransTruckingVendor implements java.io.Serializable
	{
		/*
		* An empty Constructor in which all the class variables are assigned to null if the variables are String data Type and 0 if
		* the variable is of Double Or Integer. 
		*/	
  /**
   * 
   */
		public ETransTruckingVendor()
		{
			this.vendorId	=	null;
			this.vendorName	=	null;
			this.vendorAdd	= 	null;
			this.addLine1	=	null;
			this.addLine2	=	null;
			this.city		=	null;	
			this.state		=	null;	
			this.contactNo	=	null;	
			this.fax		=	null;
			this.zipCode	=	null;
			this.email		=	null;	
			this.countryId	=	null;	
			this.helpLine	=	null;	
			this.operation	=	null;
			this.vendorType =   null;
			
			
		}	

// Starting get and set methods			

  /**
   * 
   * @return 
   */
  		public String getVendorId()
  		{ 
			return this.vendorId ;
		}			  	
  /**
   * 
   * @param vendorId
   */
		public void setVendorId(String vendorId)
  		{
 			this.vendorId = vendorId;
		}	
  /**
   * 
   * @return 
   */
		public String getVendorName()
  		{ 
			return this.vendorName ;
		}
  /**
   * 
   * @param vendorName
   */
		public void setVendorName(String vendorName)
  		{
 			this.vendorName = vendorName;
		}
  /**
   * 
   * @return 
   */
  		public String getVendorAdd()
  		{ 
			return this.vendorAdd ;
		}
		
  /**
   * 
   * @param vendorAdd
   */
		public void setVendorAdd(String vendorAdd)
  		{
 			this.vendorAdd = vendorAdd;
		}
		
  /**
   * 
   * @return 
   */
		public String getAddLine1()
  		{ 
			return this.addLine1 ;
		}
				  	
  /**
   * 
   * @param addLine1
   */
  		public void setAddLine1(String addLine1)
  		{
 			this.addLine1 = addLine1;
		}
		
		
  /**
   * 
   * @return 
   */
		public String getAddLine2()
  		{ 
			return this.addLine2 ;
		}
				  	
  /**
   * 
   * @param addLine2
   */
  		public void setAddLine2(String addLine2)
  		{
 			this.addLine2 = addLine2;
		}
		

  /**
   * 
   * @return 
   */
		public String getCity()
  		{ 
			return this.city ;
		}
				  	
  /**
   * 
   * @param city
   */
  		public void setCity(String city)
  		{
 			this.city = city;
		}
		
		
  /**
   * 
   * @return 
   */
		public String getState()
  		{ 
			return this.state ;
		}
				  	
  /**
   * 
   * @param state
   */
  		public void setState(String state)
  		{
 			this.state = state;
		}
		
		
  /**
   * 
   * @return 
   */
		public String getContactNo()
  		{ 
			return this.contactNo ;
		}
				  	
  /**
   * 
   * @param contactNo
   */
  		public void setContactNo(String contactNo)
  		{
 			this.contactNo = contactNo;
		}
		
		
  /**
   * 
   * @return 
   */
		public String getFax()
  		{ 
			return this.fax ;
		}
				  	
  /**
   * 
   * @param fax
   */
  		public void setFax(String fax)
  		{
 			this.fax = fax;
		}

			
  /**
   * 
   * @return 
   */
			public String getZipCode()
  		{ 
			return this.zipCode ;
		}
				  	
  /**
   * 
   * @param zipCode
   */
  		public void setZipCode(String zipCode)
  		{
 			this.zipCode = zipCode;
		}
			
		
  /**
   * 
   * @return 
   */
		public String getEmail()
  		{ 
			return this.email ;
		}
				  	
  /**
   * 
   * @param email
   */
  		public void setEmail(String email)
  		{
 			this.email = email;
		}

		
  /**
   * 
   * @return 
   */
		public String getCountryId()
  		{ 
			return this.countryId ;
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
		public String getHelpLine()
  		{ 
			return this.helpLine ;
		}
				  	
  /**
   * 
   * @param helpLine
   */
  		public void setHelpLine(String helpLine)
  		{
 			this.helpLine = helpLine;
		}
	
  /**
   * 
   * @return 
   */
		public String getOperation()
  		{ 
			return this.operation ;
		}
				  	
  /**
   * 
   * @param operation
   */
  		public void setOperation(String operation)
  		{
 			this.operation = operation;
  		}	
		  
  /**
   * 
   * @return 
   */
		  public String  getVendorType()
  		{ 
			return this.vendorType ;
		}
				  	
  /**
   * 
   * @param vendorTYpe
   */
  		public void setVendorType(String vendorTYpe)
  		{
 			this.vendorType = vendorTYpe;
  		}	 
  		
  		
		   
		private String vendorId  	 = null;
		private String vendorName    = null;
		private String vendorAdd	 = null;
		private String addLine1		 = null;
		private String addLine2 	 = null;
		private String city  		 = null;
		private String state  		 = null;
		private String contactNo 	 = null;		
		private String fax   		 = null;
		private String zipCode 		 = null;
		private String email   		 = null;
		private String countryId  	 = null;
		private String helpLine 	 = null;
		private String operation 	 = null;
		private String vendorType    = null;
		}
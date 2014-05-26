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
 * @(#)CustomerDetail.java
 *
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *s
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft.
 */
 /*
   Program Name		: CustomerDetail.java
   Module name		: HO Setup
   Task		        : Adding Customer
   Sub task			: to manipulate details of customer
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   : September 11, 2001

   Description      :
    This class will be useful to manipulate all the details of Customer.
    This class consist of accessors and mutators for these objects.
    This object is used in add, modify and view modules of the Customer. In modify, view modules the Customer
    details  data will be fetched( with respect to FS_FR_CUSTOMERMASTER table in the database ) and the fetched data
    will be stored in  this object,and this object will be returned back to the client. In add module, entered Customer
    details by the client will send to the server through this object.

   Method Summary   :
    String getCustomerId()    // this method  returns Customer ID
	String getCompanyName()    // this method returns ComanyName
	String getTerminalId()    // this method  returns Terminal Id
    String getSCode()      // this method  returns Salesmen code
	int getCreditDays()      // this method  returns Credit days
	double getCreditLimit()    // this method  returns CreditLimit
	String getCurrency()    // this method returns Currency
	String getContactName()    // this method returns ContactName
    String getDesignation()    // this method returns Designation
	int getCustomerAddressId()  // this method returns Customer Address Id
	String getNotes()    // this method returns Notes
	String getAbbrName()	// this method returns Abbreviated Name
	String getRegistered()    // this method returns registered
	setCustomerId(String customerId)    // this method sets CustomerId
	setCompanyName(String companyName)    // this method sets CompanyName
	setTerminalId(String terminalId)     // this method sets TerminalId
	setSCode(String scode)	    // this method sets salesmen code
	setCreditDays(int creditdays)	  // this method sets credit days
	setCreditLimit(double creditlimit)    // this method sets credit limit
	setCurrency(String currency)	  // this method sets Currency
	setContactName(String contactName)    // this method sets ContactName
	setDesignation(String designation)    // this method sets Designation
	setCustomerAddressId(int customerAddressId)   // this method sets Address Id
	setNotes(String notes)     // this method sets notes
	setAbbrName(String abbrName)     // this method sets abbreviated Name
	setRegistered(String registered)    // this method sets registered
*/

package com.foursoft.etrans.setup.customer.java;
import java.sql.Timestamp;
import java.util.ArrayList;

/*
 * @author
 * @version 1.6
 */
public class CustomerModel implements java.io.Serializable
{

	/**
	* An empty Constructor in which all the member variables are assigned to null if the variables are of String data Type and 0 if
	* the variable is of Double Or Integer data Type.
	*/

  	public CustomerModel()
  	{
		customerId  		= null;
		companyName 		= null;
		terminalId 			= null;
		scode              	= null;
		creditdays         	= 0;
		creditlimit        	= 0.0;
		currencyId          = null;
		contactName 		= null;
		contactLastName		=null;
		designation			= null;
		customerAddressId	= 0;
		notes				= null;
		abbrName			= null;
		registered			= null;
		opEmailId			= null;
		typeOfCustomer		= null;
		projectId			= null;
		corpCustomerId		= null;
    city=null;
    invalidate=null;

		prqCreateFlag		=	null;
		prqModifyFlag		=	null;
		prqDeleteFlag		=	null;

		houseCreateFlag		=	null;
		houseModifyFlag		=	null;
		houseDeleteFlag		=	null;

		masterCreateFlag	=	null;
		masterModifyFlag	=	null;
		masterCloseFlag		=	null;
		masterDeleteFlag	=	null;

		bbFlag				=	null;
		bbModifyFlag		=	null;

		doCreateFlag		=	null;
		creditFlag			=	null;
        serviceLevelId      =   null;

		// @@ Suneetha added on 20050305 for Bulk Invoicing
		invoiceFrequencyValidDate = null;
		invoiceFrequencyFlag	  = null;	
		invoiceInfo				  = null;
		bulkInvoiceRequired		  = null;
		// @@ 20050305 for Bulk Invoicing
	}//constructor
  /**
   * 
   * @param customerId
   * @param companyName
   * @param terminalId
   */
	public CustomerModel(String customerId, String companyName,  String terminalId)
	{
		this.customerId 		= customerId;
		this.companyName		= companyName;
		this.terminalId			= terminalId;
	
	}

	// This Constructor Added by JS

  /**
   * 
   * @param customerId
   * @param terminalId
   * @param customerAddressId
   * @param contactName
   * @param designation
   * @param delFlag
   * @param addressType
   */
	public CustomerModel(String customerId, String terminalId,int customerAddressId,String contactName,String designation,String delFlag,String addressType){
		this.customerId			=	customerId;
		this.terminalId			=	terminalId;
		this.customerAddressId	=	customerAddressId;
		this.contactName		=	contactName;
		this.designation		=	designation;
		this.delFlag			=	delFlag;
		this.addressType		=	addressType;

	}
	

	//End of the Constructor


	/**
	* Returns the Customer Id.
	*
	* @returns the customerId as String.
	*/
	public String getCustomerId()
	{
   		return customerId;
 	}

	/**
     * Returns the Company Name.
     *
     * @returns the companyName as String.
     */
	public String getCompanyName()
	{
   		return companyName;
 	}

	/**
     * Returns the Terminal Id.
     *
     * @returns the terminalId as String.
     */
	public String getTerminalId()
	{
   		return terminalId;
 	}

    /**
     * Returns the SalesManCode.
     *
     * @returns the scode as String.
     */
	public String getSCode()
	{
   		return scode;
 	}

	/**
    /**
     * Returns the CreaditDays.
     *
     * @returns the creditdays as int.
     */
	public int getCreditDays()
	{
   		return creditdays;
 	}

	/**
	/**
     * Returns the CreditLimit.
     *
     * @returns the scode as double.
     */
	public double getCreditLimit()
	{
   		return creditlimit;
 	}

	/**

	/**
     * Returns the Currency.
     *
     * @returns the currency as String.
     */
	public String getCurrency()
	{
   		return currencyId;
 	}

	/**


	/**

	/**
     * Returns the Contact Name.
     *
     * @returns the contactName as String.
     */
	public String getContactName()
	{
   		return contactName;
 	}
	//added by Santhosam for PR-ET-1174-AES/SED on 17-02-2005
	public String getContactLastName()
	{
		return contactLastName;
	}
	public String getEINSSNNo()
	{
		return einsinNo;
	}
	public String getcustType()
	{
		return custType;
	}
	//@@ ends here
	/**
	* Returns the Designation.
	*
	* @returns the designation as String.
	*/
	public String getDesignation()
	{
		return designation;
	}

	/**
	* Returns the Customer Address Id.
	*
	* @returns the customerAddressId as Integer.
	*/
	public int getCustomerAddressId()
	{
		return customerAddressId;
	}

	/**
	* Returns the Notes. It indicates the brief description of the Customer.
	*
	* @returns the notes as String.
	*/
	public String getNotes()
	{
		return notes;
	}

	/**
     * Returns the Abbreivated Name. It is the Abbreviated Name of the Company.
     *
     * @returns the abbrName as String.
     */
	public String getAbbrName()
	{
		return abbrName;
	}

	/**
     * Returns the Registered. It is to indicate that whether the Customer is registered one or not.
     *
     * @returns the registered as String.
     */
	public String getRegistered()
	{
		return registered;
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
   * @return String
   */
   public String getProjectId()
   {
      	return projectId;
   }
  /**
   * 
   * @return String
   */
   public  String getTypeOfCustomer()
   {
   	 return  typeOfCustomer;
   }
  /**
   * 
   * @return String
   */
 	  public String getCorpCustomerId()
   {
      	return corpCustomerId;
   }

// @@ Suneetha Added on 20050305 for Bulk Invoicing

    /**
     * Takes the invoiceFrequencyValidDate as TimeStamp argument and sets the same to the member variable, invoiceFrequencyValidDate
     *
     * @param invoiceFrequencyValidDate this TimeStamp is used to set invoiceFrequencyValidDate.
     */
    public void setInvoiceFrequencyValidDate( Timestamp invoiceFrequencyValidDate )
 	 {
    	this.invoiceFrequencyValidDate = invoiceFrequencyValidDate;
     }
  /**
   * 
   * @param invoiceFrequencyFlag
   */
  	public void setInvoiceFrequencyFlag(String invoiceFrequencyFlag)
	{
   		this.invoiceFrequencyFlag = invoiceFrequencyFlag;
 	}

  /**
   * 
   * @param invoiceInfo
   */
  	public void setInvoiceInfo(String invoiceInfo)
	{
   		this.invoiceInfo = invoiceInfo;
 	}

  /**
   * 
   * @param invoiceTypeFlag
   */
  	public void setBulkInvoiceRequired(String bulkInvoiceRequired)
	{
   		this.bulkInvoiceRequired = bulkInvoiceRequired;
 	}

  /**
   * 
   * @return String
   */
   public String getBulkInvoiceRequired()
   {
      	return bulkInvoiceRequired;
   }

  /**
   * 
   * @return String
   */
  public Timestamp getInvoiceFrequencyValidDate()
   {
      	return invoiceFrequencyValidDate;
   }

  /**
   * 
   * @return String
   */
  public String getInvoiceFrequencyFlag()
   {
      	return invoiceFrequencyFlag;
   }
  /**
   * 
   * @return String
   */
  public String getInvoiceInfo()
   {
      	return invoiceInfo;
   }
	
// @@ 20050305 Bulk invoicing


    /**
     * Takes the Customer Id as String argument and sets the same to the member variable, customerId
     *
     * @param customerId this String is used to set Customer Id.
     */
	   public void setCorpCustomerId( String corpCustomerId )
 	 {
    	this.corpCustomerId = corpCustomerId;
     }
  /**
   * 
   * @param customerId
   */
  	public void setCustomerId(String customerId)
	{
   		this.customerId = customerId;
 	}

	/**
     * Takes the Company Name as String argument and sets the same to the member variable, companyName
     *
     * @param companyName this String is used to set Company Name.
     */
  	public void setCompanyName(String companyName)
	{
   		this.companyName = companyName;
 	}

    /**
     * Takes the Terminal Id as String argument and sets the same to the member variable, terminalId
     *
     * @param terminalId this String is used to set Terminal Id.
     */
	public void setTerminalId(String terminalId)
	{
   		this.terminalId = terminalId;
 	}


   /**
     * Takes the SalemmanCode as String argument and sets the same to the member variable, scode
     *
     * @param scode this String is used to set SCode.
     */
	public void setSCode(String scode)
	{
   		this.scode = scode;
 	}

   /**
     * Takes the CreditDays as int argument and sets the same to the member variable, creditdays
     *
     * @param creditdays this int is used to set CreditDays.
     */
	public void setCreditDays(int creditdays)
	{
   		this.creditdays = creditdays;
 	}

  /**
     * Takes the CreditLimit as double argument and sets the same to the member variable, creditlimit
     *
     * @param creditlimit this double is used to set CreditLimit.
     */
	public void setCreditLimit(double creditlimit)
	{
   		this.creditlimit = creditlimit;
 	}

   /**
     * Takes the Currency as String argument and sets the same to the member variable, currency
     *
     * @param currency this int is used to set Currency.
     */
	public void setCurrencyId(String currency)
	{
   		this.currencyId = currency;
 	}


    /**
     * Takes the Contact Name as String argument and sets the same to the member variable, contactName
     *
     * @param contactName this String is used to set Contact Name.
     */
	public void setContactName(String contactName)
	{
   		this.contactName = contactName;
 	}
	//added by Santhosam for PR-ET-1174-AES/SED on 17-02-2005
	//Modified by Srivegi on 20050225 AES_SED Parameters are not set 
	public void setContactLastName(String contactLastName)
	{
		this.contactLastName=contactLastName;
	}
	public void setEINSSNNo(String einsinNo)
	{
		this.einsinNo=einsinNo;
	}
	public void setcustType(String custType)
	{
		this.custType=custType;
	}
	// @@ ends here
    /**
     * Takes the Designation as String argument and sets the same to the member variable, designation
     *
     * @param designation this String is used to set Designation.
     */
	public void setDesignation(String designation)
 	{
   		this.designation = designation;
 	}

    /**
     * Takes the Customer Address Id as Integer argument and sets the same to the member variable, customerAddressId
     *
     * @param customerAddressId this Integer is used to set Customer Address Id.
     */
  	public void setCustomerAddressId(int customerAddressId)
	{
   		this.customerAddressId = customerAddressId;
 	}

    /**
     * Takes the Notes as String argument and sets the same to the member variable, notes
     *
     * @param notes this String is used to set Notes.
     */
 	public void setNotes(String notes)
	{
   		this.notes = notes;
 	}

    /**
     * Takes the Abbreivated Name as String argument and sets the same to the member variable, abbrName
     *
     * @param abbrName this String is used to set Abbreivated Name.
     */
	public void setAbbrName(String abbrName)
	{
   		this.abbrName = abbrName;
 	}

    /**
     * Takes the Registered as String argument and sets the same to the member variable, registered
     *
     * @param registered this String is used to set Registered.
     */
	public void setRegistered(String registered)
	{
   		this.registered = registered;
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
   	* sets the company projectId.
   	* @param projectId is a String used to set company projectId.
   	*/
 	 public void setProjectId( String projectId )
 	 {
    	this.projectId = projectId;
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
	 /**
	* sets the company typeOfCustomer.
	* @param typeOfCustomer is a String used to set company typeOfCustomer.
	*/
	public void setTypeOfCustomer( String typeOfCustomer )
	{
		this.typeOfCustomer = typeOfCustomer;
	}

// get Methods for customerNotify
  /**
   * 
   * @return String
   */
	public String getPrqCreateFlag()
	{
		return prqCreateFlag;
	}
  /**
   * 
   * @return String
   */
	public String getPrqModifyFlag()
	{
		return prqModifyFlag;
	}
  /**
   * 
   * @return String
   */
	public String getPrqDeleteFlag()
	{
		return prqCreateFlag;
	}

  /**
   * 
   * @return String
   */
	public String getHouseCreateFlag()
	{
		return houseCreateFlag;
	}
  /**
   * 
   * @return String
   */
	public String getHouseModifyFlag()
	{
		return houseModifyFlag;
	}
  /**
   * 
   * @return String
   */
	public String getHouseDeleteFlag()
	{
		return houseDeleteFlag;
	}

  /**
   * 
   * @return String
   */
	public String getMasterCreateFlag()
	{
		return masterCreateFlag;
	}
  /**
   * 
   * @return String
   */
	public String getMasterModifyFlag()
	{
		return masterModifyFlag;
	}
  /**
   * 
   * @return String
   */
	public String getMasterDeleteFlag()
	{
		return masterDeleteFlag;
	}
  /**
   * 
   * @return String
   */
	public String getMasterCloseFlag()
	{
		return masterCloseFlag;
	}

  /**
   * 
   * @return String
   */
	public String getBBFlag()
	{
		return bbFlag;
	}
  /**
   * 
   * @return String
   */
	public String getBBModifyFlag()
	{
		return bbModifyFlag;
	}
	
	
  /**
   * 
   * @return String
   */
	public String getDoCreateFlag()
	{
		return doCreateFlag;
	}
  /**
   * 
   * @return String
   */
	public String getCreditFlag()
	{
		return creditFlag;
	}
  /**
   * 
   * @return String
   */
    public String getIndicator()
    {
       return indicator;
    }
  /**
   * 
   * @return boolean
   */
    public boolean getUpgradeFlag()
    {
      return upgradeFlag;
    }
  /**
   * 
   * @return String
   */
     public String getServiceLevelId()
     {
      return serviceLevelId;
	 }

// setMethods for CustomerNotify

  /**
   * 
   * @param prqCreateFlag
   */
	public void setPrqCreateFlag( String prqCreateFlag )
	{
		this.prqCreateFlag = prqCreateFlag;
	}
  /**
   * 
   * @param prqModifyFlag
   */
	public void setPrqModifyFlag( String prqModifyFlag )
	{
		this.prqModifyFlag = prqModifyFlag;
	}
  /**
   * 
   * @param prqDeleteFlag
   */
	public void setPrqDeleteFlag( String prqDeleteFlag )
	{
		this.prqDeleteFlag = prqDeleteFlag;
	}

  /**
   * 
   * @param houseCreateFlag
   */
	public void setHouseCreateFlag( String houseCreateFlag )
	{
		this.houseCreateFlag = houseCreateFlag;
	}
  /**
   * 
   * @param houseModifyFlag
   */
	public void setHouseModifyFlag( String houseModifyFlag )
	{
		this.houseModifyFlag = houseModifyFlag;
	}
  /**
   * 
   * @param houseDeleteFlag
   */
	public void setHouseDeleteFlag( String houseDeleteFlag )
	{
		this.houseDeleteFlag = houseDeleteFlag;
	}

  /**
   * 
   * @param masterCreateFlag
   */
	public void setMasterCreateFlag( String masterCreateFlag )
	{
		this.masterCreateFlag = masterCreateFlag;
	}
  /**
   * 
   * @param masterModifyFlag
   */
	public void setMasterModifyFlag( String masterModifyFlag )
	{
		this.masterModifyFlag = masterModifyFlag;
	}
  /**
   * 
   * @param masterDeleteFlag
   */
	public void setMasterDeleteFlag( String masterDeleteFlag )
	{
		this.masterDeleteFlag = masterDeleteFlag;
	}
  /**
   * 
   * @param masterCloseFlag
   */
	public void setMasterCloseFlag( String masterCloseFlag )
	{
		this.masterCloseFlag = masterCloseFlag;
	}

  /**
   * 
   * @param bbFlag
   */
	public void setBBFlag( String bbFlag )
	{
		this.bbFlag = bbFlag;
	}
  /**
   * 
   * @param bbModifyFlag
   */
	public void setBBModifyFlag( String bbModifyFlag )
	{
		this.bbModifyFlag = bbModifyFlag;
	}

  /**
   * 
   * @param doCreateFlag
   */
	public void setDoCreateFlag( String doCreateFlag )
	{
		this.doCreateFlag = doCreateFlag;
	}
  /**
   * 
   * @param creditFlag
   */
	public void setCreditFlag( String creditFlag )
	{
		this.creditFlag = creditFlag;
	}

  /**
   * 
   * @param indicator
   */
    public void setIndicator(String indicator)
    {
        this.indicator = indicator;
    }
  /**
   * 
   * @param upgradeFlag
   */
  	public void setUpgradeFlag(boolean upgradeFlag)
  	{
  	   this.upgradeFlag =  upgradeFlag;
  	}

  /**
   * 
   * @param delFlag
   */
	public void	setDelFlag(String delFlag){
		this.delFlag	=	delFlag;
	}
  /**
   * 
   * @return String 
   */
	public String getDelFlag(){
		return delFlag;
	}
  /**
   * 
   * @return String
   */
	public String getAddressType(){
		return addressType;
	}
  /**
   * 
   * @param addressType
   */
	public void setAddressType(String addressType){
		this.addressType	=	addressType;
	}
  
  /**
   * 
   * @param serviceLevelId
   */
    public void setServiceLevelId(String serviceLevelId)
    {
      this.serviceLevelId =serviceLevelId;
    } 
    public void setContactDtl(ArrayList contactDtl)
    {
      this.contactDtl=contactDtl;
    }
    public ArrayList getContactDtl()
    {
      return contactDtl;
    }
    public void setPaymentTerms(String paymentTerms)
    {
      this.paymentTerms=paymentTerms;
    }
    public String getPaymentTerms()
    {
      return paymentTerms;
    }
    public void setDivision(String division)
    {
      this.division=division;
    }
    public String getDivision()
    {
      return division;
    }
	public void setCurrencyInvalidate(String currencyinvalidate)
	{
      this.currencyInvalidate=currencyInvalidate;
	}
  public String getCurrencyInvalidate()
  {
    return currencyInvalidate;
  }
  	// Data Members

	/** The customerId that uses to store Customer Id */
	public String customerId	  	= null;

	/** The companyName that uses to store Company Name */
	public String companyName 		= null;

	/** The terminalId that uses to store Terminal Id */
  	public String terminalId 		= null;

	/** The scode that uses to store SalesmenCode */
  	public String scode 		    = null;

	/** The creditdays that uses to store Credit Days */
  	public int creditdays 	    	= 0;

    /** The creditlimit that uses to store Credit Limit */
  	public double creditlimit 		= 0.0;

   /** The currency that uses to store Currency */
  	public String currencyId   		= null;

	/** The contactName that uses to store Contact Name */
	public String contactName 		= null;
  //added by Santhosam for PR-ET-1174-AES/SED on 17-02-2005
  public String contactLastName    =null;
  public String einsinNo=null;
  public String custType=null;
  //@@ ends here
	/** The designation that uses to store Designation */
	public String designation 		= null;

	/** The customerAddressId that uses to store Customer Address Id */
	public int customerAddressId;

	/** The notes that uses to store Notes */
	public String notes		 		= null;

	/** The abbrName that uses to store Abbreivated Name */
	public String abbrName			= null;

	/** The registered that uses to store Registered */
	public String registered		= null;

	
	
	private String	opEmailId			=	null;		//This String variable stores operations emailId
	private	String	typeOfCustomer		=	null;		//This String variable stores customerType
	private String	projectId			=	null;
	private String	corpCustomerId		=	null;

	private String	prqCreateFlag		=	null;
	private	String	prqModifyFlag		=	null;
	private String	prqDeleteFlag		=	null;

	private String	houseCreateFlag		=	null;
	private String	houseModifyFlag		=	null;
	private String	houseDeleteFlag		=	null;

	private	String	masterCreateFlag	=	null;
	private String	masterModifyFlag	=	null;
	private String	masterCloseFlag		=	null;
	private String	masterDeleteFlag	=	null;

	private String	bbFlag				=	null;
	private	String	bbModifyFlag		=	null;

	private String	doCreateFlag		=	null;
	private String	creditFlag			=	null;
    private String	indicator			=	null;
    private boolean upgradeFlag			=   false;  // Added by Anand.A
	 private String invalidate=null;
	 private String currencyInvalidate=null;
    private String city=null;
	private String	delFlag				=	null;  //Added by JS 
	private String	addressType			=	null;  // Added by JS 
    private String  serviceLevelId      =   null; //KNVP Reddy 
   
    //@@ Srivegi added on 20050224 (AES-SED)
    private String	contactPersonFirstName 			=	null;
	private String	contactPersonLastNameindicator	=	null;
	private String	einssn			=	null;
    //@@ 20050224
	// @@ Suneetha added on 20050305 for Bulk Invoicing
	private Timestamp  invoiceFrequencyValidDate = null;
	private String 	invoiceFrequencyFlag	  = null;	
	private String  invoiceInfo				  = null;
	private String  bulkInvoiceRequired		  = null;
  private ArrayList contactDtl=null;
  private String paymentTerms=null;
  private String division=null;
  private String salesPersonCode = null;
	// @@ 20050305 for Bulk Invoicing
public String getSalesPersonCode() {
	return salesPersonCode;
}
public void setSalesPersonCode(String salesPersonCode) {
	this.salesPersonCode = salesPersonCode;
}
  }	//CustomerrDetail

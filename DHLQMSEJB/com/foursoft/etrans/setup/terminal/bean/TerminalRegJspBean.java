/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/* Program Name		: TerminalRegJspBean.java
   Module name		: HO Setup
   Task		        : Adding Terminal
   Sub task			: to set and get terminal informatiom
   Author Name		: A.Hemanth Kumar
   Date Started     : September 11, 2001
   Date completed   : September 12, 2001
   Description      : To set and get terminal information
   Date Modified	: 04-11-2001
   Modififed By		: Anand Kumar.A
   Reason to Modify	: New Bank related fields are added to this Java File.			
     
   Methods Summary  : 
                      
					  
		   // Get Methods
					  
	String getIataCode()								// this method retrieves in IATA Code 
    String getAccountCode()							    // this method retrieves Account Code
	String getTaxRegNo()								// this method retrieves Tax Reg No
	String getTerminalId()							    // this method retrieves Terminal Id
	String getLocationId()							    // this method retrieves Location Id
	String getCompanyId()								// this method retrieves Company Id
	String getContactName()							    // this method retrieves Contact Name
	String getDesignation()							    // this method retrieves Designation	
	String getNotes()									// this method retrieves Notes
	String getContactAddressId()						// this method retrieves Customers Address Id	
	String getAgentInd()								// this method retrieves Agent Indicator
	Address getAddress()								// this method retrieves Address Object
	double getInterestRate()							// this method retrieves Interest Rate
	int    getDiscrepancy()							    // this method retrieves Discrepancy
	String getBankCity()								// this method retrieves Bank City
	String getBankAddress()							    // this method retrieves Bank Address Details
    String getBranchName()							    // this method retrieves Branch Name
	String getBankName()								// this method retrieves Bank Name
	String getAccountNumber()							// this method retrieves Account Number
	String getInvoiceCategory()						    // this method retrieves Invoice Category
    String getTerminalType()							// this method retrieves Terminal Type
                      

			// Set Methods

	void setIataCode(String iatacode)                   // this method sets Iata Code
	void setAccountCode(String accountcode)			    // this method sets Account Code
	void setTaxRegNo(String taxregno)					// this method sets Tax Registration Number
	void setTerminalId(String terminalId)				// this method sets TerminalId
	void setLocationID(String locationId)				// this method sets locationId
	void setCompanyId(String companyId)				    // this method sets companyId
	void setContactName(String contactName)			    // this method sets contactName
	void setDesignation(String designation)			    // this method sets designation	
	void setNotes(String notes)						    // this method sets Notes
	void setContactAddressId(String contactAddressId)   // this method sets Address Id
	void setAgentInd(String agentInd)					// this method sets Agent Id
	void setAddress(Address address)					// this method sets Address
	void setAccountNumber(int accountNumber)			// this method sets Account Number
	void setBranchName(String branchName)				// this method sets Branch Name
	void setBankName(String bankName)					// this method sets Bank Name
	void setBankAddress(String branchAddress)		    // this method sets Branch Address
	void setBBankcity(String bankcity)				    // this method sets Bank Name
	void setDiscrepancy(int discrepancy)				// this method sets Discrepancy
	void setInterestRate(double interestRate)			// this method sets Interest Rate
	void setInvoiceCategory(String invoiceCategory)	    // this method sets Invoice Category 
    void setTerminalType(String terminalType)           // this method sts Terminal Type   
*/
package com.foursoft.etrans.setup.terminal.bean;

import com.foursoft.etrans.common.bean.Address;

import java.util.Vector;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : TerminalRegJspBean.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */

public class TerminalRegJspBean implements java.io.Serializable
	
  {
  /**
   * Default Constructor
   */

// @@ Modified address to address1,2,3,4.. by G.Srinivas for TogetherArchitect on 20050111

     public TerminalRegJspBean()
     {
      terminalId          = null;
      locationId          = null;
      companyId           = null;
      contactName         = null;
      designation         = null;
      notes               = null;
      contactAddressId    = 0;
      agentInd            = null;
      Address  address    = null;
	  iatacode            = null;
	  accountcode         = null;
	  taxregno            = null;
      accountNumber		  = null;
	  bankName     		  = null;
	  branchName     	  = null;
      bankAddress    	  = null;
      bankCity            = null;
	  invoiceCategory     = null; 
      discrepancy         = 0;
      interestRate        = 0.0;
	  
	  // Added by Shravan on 27th Feb
	   
	  emailStatus		  = null;
	  collectShipment	  = null;
	  timeZone			  = null;
	  opTerminalType	  = null;
      serverTimeDiff      = null;
  
	  weightScale		  = 0;
	  //@@ Srivegi Added on 20050419 (Invoice-PR)
	  stockedInvoiceIdsCheck = null;
	  //@@ 20050419 (Invoice-PR)
	  	
      } // Default Constructor
      
  /**
   * 
   * @param terminalId
   * @param locationId
   * @param companyId
   * @param contactName
   * @param designation
   * @param notes
   * @param contactAddressId
   * @param agentId
   * @param iata
   * @param account
   * @param regno
   * @param address
   */
     public TerminalRegJspBean(String terminalId, String  locationId[] ,  String companyId,String contactName, String designation, String notes , int contactAddressId, String agentId,String iata,String account,String regno, Address address3)
     	{
     		
     	 this.terminalId       = terminalId;
     	 this.locationId       = locationId;
     	 this.companyId        = companyId;
     	 this.contactName      = contactName;
     	 this.designation      = designation;
     	 this.notes            = notes;
     	 this.contactAddressId = contactAddressId;
     	 this.agentInd         = agentId;
		 this.iatacode         = iata;	 
		 this.accountcode      = account;	 
		 this.taxregno         = regno;	 
     	 this.address1          = address3;	 
		 
     	 
     	 }
     	 
  /**
   * 
   * @param terminalId
   * @param locationId
   * @param companyId
   * @param contactName
   * @param designation
   * @param notes
   * @param contactAddressId
   * @param agentId
   * @param iata
   * @param account
   * @param regno
   * @param address
   * @param bankName
   * @param branchName
   * @param accountNo
   * @param bankAddress
   * @param bankCity
   * @param discrepancy
   * @param interestRate
   * @param invoiceCategory
   * @param terminalType
   * @param opTerminalType
   * @param mailStatus
   * @param timeZone
   * @param shipmentCollect
   * @param shipmentMode
   * @param weightScale
   */
   //@@ Srivegi Modified on 20050419 (Invoice-PR) - stockedInvoiceIdsCheck parameter is added int he below method
   //Modified By RajKumari on 11/28/2008 for 146448 
     public TerminalRegJspBean(String terminalId, String  locationId[] ,  String companyId,
     String contactName, String designation, String notes , int contactAddressId, String agentId,
     String iata,String account,String regno, Address address4,String bankName,String branchName,String accountNo,
     String bankAddress,String bankCity,int discrepancy,double interestRate,String invoiceCategory,String terminalType,
     String opTerminalType,String mailStatus,String timeZone ,String shipmentCollect,String shipmentMode,int weightScale,
     String stockedInvoiceIdsCheck,String frequency,String carrier,String transitTime,String rateValidity)
     	{
     		
     	 this.terminalId       = terminalId;
     	 this.locationId       = locationId;
     	 this.companyId        = companyId;
     	 this.contactName      = contactName;
     	 this.designation      = designation;
     	 this.notes            = notes;
     	 this.contactAddressId = contactAddressId;
     	 this.agentInd         = agentId;
		 this.iatacode         = iata;	 
		 this.accountcode      = account;	 
		 this.taxregno         = regno;	 
     	 this.address1          = address4;	 
         this.bankName		   = bankName;
		 this.branchName       = branchName;
		 this.accountNumber    = accountNo;
		 this.bankAddress	   = bankAddress;
		 this.bankCity		   = bankCity;
		 this.discrepancy      = discrepancy;
		 this.interestRate	   = interestRate;
		 this.invoiceCategory  = invoiceCategory;
		 this.terminalType     = terminalType;	 	  	
		 this.opTerminalType   = opTerminalType;
		 this.emailStatus	   = mailStatus;
		 this.timeZone		   = timeZone;
		 this.collectShipment  = shipmentCollect;	 
		 this.shipmentMode	   = shipmentMode;
		 this.weightScale	   = weightScale;	
         //@@ Srivegi Added on 20050419 (Invoice-PR)
	     this.stockedInvoiceIdsCheck = stockedInvoiceIdsCheck;
         //@@ 20050419 (Invoice-PR)
    //Added By RajKumari on 11/28/2008 for 146448 starts
    this.frequency	   =   frequency;
    this.carrier	     =   carrier;
    this.transitTime	 =   transitTime;
    this.rateValidity	 =   rateValidity;
    //Added By RajKumari on 11/28/2008 for 146448 ends
    	 }
  
     
     
     
     // ACCESSORS
     
     
     
  /**
   * 
   * @return String
   */
      public  String getIataCode()	  
      	{
      	 return  iatacode;	
      	}	
      	
  /**
   * 
   * @return String
   */
      public  String  getAccountCode()
     	{
      	  return  accountcode;
        }  		
	   
  /**
   * 
   * @return String
   */
	  public  String getTaxRegNo()	  
      	{
      	 return  taxregno;	
      	}	
  /**
   * 
   * @return String
   */
      public  String getTerminalId()	  
      	{
      	 return  terminalId;	
      	}	
  /**
   * 
   * @return String
   */
      public  String[]  getLocationId()
     	{
      	  return  locationId;
        }  		
  /**
   * 
   * @return Vector
   */
      public  Vector  getCBTLocationId()
     	  {
      	  return  cbtLocationId;
        } 
  /**
   * 
   * @return String
   */
      public  String getCompanyId()
      	{
      	  return  companyId;
      	}	  	
  /**
   * 
   * @return String
   */
      public String getContactName()
       	{
       	   return contactName;
       	}  		
  /**
   * 
   * @return String
   */
      public  String getDesignation()
      	{
      	 return  designation;
      	}
  /**
   * 
   * @return String
   */
      public String getNotes()
      	{
      	 return notes;
      	}
  /**
   * 
   * @return int
   */
      public int getContactAddressId() 
      	{
      	 return contactAddressId;
      	}
  /**
   * 
   * @return String
   */
      public String getAgentInd()
      	{
      	 return agentInd ;
      	}
  /**
   * 
   * @return Address
   */
      public Address getAddress()
      	{
      	 return address1;
      	}		 	
  /**
   * 
   * @return double
   */
      public double getInterestRate()
        {
         return interestRate;
        }
  /**
   * 
   * @return int
   */
      public int getDiscrepancy()
        {
         return discrepancy ;
        }
  /**
   * 
   * @return String
   */
      public String getBankCity()
        {
         return  bankCity ;
        }
  /**
   * 
   * @return String
   */
	  public String getBankAddress()
        {
         return bankAddress;
        }
  /**
   * 
   * @return String
   */
	  public String getBranchName()
        {
         return branchName;
        }
  /**
   * 
   * @return String
   */
	  public String getBankName()
        {
         return bankName;
        }
  /**
   * 
   * @return String
   */
   	  public String getAccountNumber()
	    {
	     return accountNumber;
	    }  	
  /**
   * 
   * @return String
   */
	  public String getInvoiceCategory()
	    {
	     return invoiceCategory;
	    }  		  	   			 
  /**
   * 
   * @return String
   */
      public String getTerminalType()
      {
		return terminalType;
      }	  
  /**
   * 
   * @return String
   */
	  public int getWeightScale()
	  {
		return weightScale;
	  }
	  
  /**
   * 
   * @return String
   */
	   public String getServerTimeDiff()
	    {
		  return serverTimeDiff;
	    }
	  // MUTATORS
		  
  /**
   * 
   * @param iatacode
   */
	   public void  setIataCode(String iatacode)	 
        {
      	 this.iatacode = iatacode;
        }
  /**
   * 
   * @param accountcode
   */
	   public void  setAccountCode(String accountcode)	 
      	{
      	 this.accountcode = accountcode;
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
   * 
   * @param taxregno
   */
	   public void  setTaxRegNo(String taxregno)	 
      	{
      	 this.taxregno = taxregno;
      	}     
  /**
   * 
   * @param terminalId
   */
	   public void  setTerminalId(String terminalId)	 
      	{
      	 this.terminalId = terminalId;
      	}
  /**
   * 
   * @param locationId
   */
       public  void  setLocationId(String[] locationId)
     	{
     	 this.locationId = locationId;
     	} 	 	 
  /**
   * 
   * @param cbtLocationId
   */
      public  void  setCBTLocationId(Vector cbtLocationId)
     	{
     	 this.cbtLocationId = cbtLocationId;
     	}	 
  /**
   * 
   * @param companyId
   */
      public void  setCompanyId(String companyId)
      	{
      	  this.companyId = companyId;
      	}
      	
  /**
   * 
   * @param contactName
   */
      public  void setContactName(String contactName)
      	{
      		this.contactName = contactName;
      	}		
  /**
   * 
   * @param designation
   */
      public void  setDesignation(String designation)	  	 	 	
      	{
      	  this.designation = designation;
      	}
  /**
   * 
   * @param notes
   */
      public void setNotes(String notes)
      	{
      	   this.notes = notes;
      	}
  /**
   * 
   * @param contactAddressId
   */
      public void 	setContactAddressId(int contactAddressId)   		  	 
      	{
      	  this.contactAddressId= contactAddressId;
      	} 	
      	 
  /**
   * 
   * @param agentId
   */
      public void  	setAgentInd(String agentId)
      	{
      	  this.agentInd = agentId;
      	} 	 
  /**
   * 
   * @param address
   */
      public void setAddress(Address address)
       	{
       	  this.address1 = address;
       	}
       	 
  /**
   * 
   * @param accountNumber
   */
      public void setAccountNumber ( String  accountNumber ) 
       {
     	  this.accountNumber   		= accountNumber;
       }
  /**
   * 
   * @param bankName
   */
	  public void setBankName ( String bankName ) 
       {
     	  this.bankName   	= bankName;
       }
  /**
   * 
   * @param branchName
   */
      public void setBranchName ( String branchName ) 
       {
     	  this.branchName   		= branchName;
       }
  /**
   * 
   * @param bankAddress
   */
      public void setBankAddress( String bankAddress )
       {
          this.bankAddress     = bankAddress;
       }
  /**
   * 
   * @param bankCity
   */
      public void setBankCity( String  bankCity )
       {
          this.bankCity     = bankCity;          
       }
  /**
   * 
   * @param discrepancy
   */
      public void setDiscrepancy( int discrepancy)
       {
          this.discrepancy   	        = discrepancy;
       }
  /**
   * 
   * @param interestRate
   */
      public void setIntrestRate( double interestRate)
       { 
          this.interestRate            = interestRate;
       }
  /**
   * 
   * @param invoiceCategory
   */
      public void setInvoiceCategory( String invoiceCategory)
       { 
          this.invoiceCategory            = invoiceCategory;
       }
  /**
   * 
   * @param terminalType
   */
      public void setTerminalType(String terminalType)
      {
		 this.terminalType  =  terminalType;
      } 	 	
    
	  // Added by Shravan on 27th Feb
		
  /**
   * 
   * @return String
   */
	   public String getEmailStatus()
	   {
			return emailStatus;
	   }		      
  /**
   * 
   * @return String
   */
	   public String getTimeZone()
	   {
			return timeZone;
	   }
  /**
   * 
   * @return String
   */
	   public String getCollectShipment()
	   {
			return collectShipment;
	   }
  /**
   * 
   * @return String
   */
	   public String getOperationTerminalType()
	   {
			return opTerminalType;
	   }
	   
  /**
   * 
   * @return String
   */
	   public String getShipmentMode()
	   {
			return shipmentMode;
	   }
	   
  /**
   * 
   * @param emailStatus
   */
	   public void setEmailStatus( String emailStatus)
       {
          this.emailStatus   	        = emailStatus;
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
   * @param collectShipment
   */
      public void setCollectShipment( String collectShipment)
       { 
          this.collectShipment            = collectShipment;
       }
  /**
   * 
   * @param opTerminalType
   */
      public void setOperationTerminalType(String opTerminalType)
      {
		 this.opTerminalType  =  opTerminalType;
      } 	
  /**
   * 
   * @param shipmentMode
   */
	  public void setShipmentMode(String shipmentMode)
      {
		 this.shipmentMode  =  shipmentMode;
      } 
	  
  /**
   * 
   * @param weightScale
   */
	  public void setWeightScale(int weightScale)
	  {
		this.weightScale	=	weightScale;
	  } 	 
		 
  /**
   * 
   * @param serverTimeDiff
   */
     public void setServerTimeDiff(String serverTimeDiff)
	    {
		  this.serverTimeDiff  =  serverTimeDiff;
        } 
    //@@ Srivegi Added on 20050419 (Invoice-PR)
	public String getStockedInvoiceIdsCheck()
       	{
       	   return stockedInvoiceIdsCheck;
       	}
	public void setStockedInvoiceIdsCheck(String stockedInvoiceIdsCheck)
       	{
       	   this.stockedInvoiceIdsCheck=stockedInvoiceIdsCheck;
       	}
    //@@ 20050419 (Invoice-PR) 
  public void setChildTerminalFlag(String childTerminalFlag)
  {
    this.childTerminalFlag = childTerminalFlag;
  }


  public String getChildTerminalFlag()
  {
    return childTerminalFlag;
  }	         	 
// Added By RajKumari on 11/28/2008 for 146448 starts 
  public void setFrequency(String frequency)
  {
    this.frequency = frequency;
  }


  public String getFrequency()
  {
    return frequency;
  }	        
  public void setCarrier(String carrier)
  {
    this.carrier = carrier;
  }


  public String getCarrier()
  {
    return carrier;
  }	        
  public void setTransitTime(String transitTime)
  {
    this.transitTime = transitTime;
  }


  public String getTransitTime()
  {
    return transitTime;
  }	        
  public void setRateValidity(String rateValidity)
  {
    this.rateValidity = rateValidity;
  }


  public String getRateValidity()
  {
    return rateValidity;
  }
// Added By RajKumari on 11/28/2008 for 146448 ends 
      private String 	 terminalId          = null;
      private String   locationId[]          = null;
      private Vector   cbtLocationId         = null;
      private String     companyId           = null;
      private String     contactName         = null;
      private String     designation         = null;
      private String     notes               = null;
      private int        contactAddressId    = 0;
      private String     agentInd            = null;
     // private Address    address             = null;
	  private Address    address1             = null;
	  private String    iatacode             = null;
	  private String    accountcode          = null;
	  private String    taxregno             = null;	
	  private String    terminalType         = null;		 		
      private String  accountNumber			= null;
	  private String  bankName     			= null;
	  private String  branchName     		= null;
      private String  bankAddress    		= null;
      private String  bankCity           	= null;
	  private String  invoiceCategory    	= null;
    private String city=null;
    private String invalidate=null;
	 
      private int     discrepancy       	= 0;
      private double  interestRate        = 0.0;
	  
	  // Added by Shravan on 27th Feb
	  
	  private String 	emailStatus		 = null;
	  private String 	collectShipment	 = null;
	  private String 	timeZone		 = null;
	  private String 	opTerminalType	 = null;
	  private String 	shipmentMode	 = null;
	  private int		weightScale		 = 0;
	  private String    serverTimeDiff   = null;
	  //@@ Srivegi Added on 20050419 (Invoice-PR)
	  private String    stockedInvoiceIdsCheck = null;
      //@@ 20050419 (Invoice-PR)
    private String childTerminalFlag  = null;//Added By I.V.Sekhar
    // Added By RajKumari on 11/28/2008 for 146448 starts
    private String 	frequency	 = null;
    private String 	carrier	 = null;
    private String 	transitTime	 = null;
    private String 	rateValidity	 = null;
   // Added By RajKumari on 11/28/2008 for 146448 ends
//  added by phani sekhar for wpbn 170758  on 20090626
  private String marginType = null;
  private String discountType = null;

  public void setMarginType(String marginType)
  {
    this.marginType = marginType;
  }


  public String getMarginType()
  {
    return marginType;
  }


  public void setDiscountType(String discountType)
  {
    this.discountType = discountType;
  }


  public String getDiscountType()
  {
    return discountType;
  }
//ends 170758 

      }  // END OF TERMINALREGJSPBEAN
         	 			  		
	

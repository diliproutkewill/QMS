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

	Program Name	:	JointVentureJspBean.java
	Module Name		:	HOSETUP
	Task			:	Agents/Joint Venture 
	Sub Task		:	Add/Modify/View/Delete
	Author Name		:	Anand Kumar . A
	Date Started	:	September 07,2001			  
    Date Completed	:   September 07,2001			  
    Date Modified	:		
	
	
	Description		:	This Java File is used as a DataBase Object[DOB] for FS_FR_AGENTJV Table.
	                    This DOB is used as a  Communication Channel between JSP's and 
						Enterprise Beans and Vice versa.
						
    Methods Summary	:  
	
	       String  getTerminalId()   	 			// used for getting TerminalId
		   String  getCompanyId()    	 			// used for getting CompanyId
		   int     getAddressId()		 			// used for getting AddressId
		   int     getPercentOfEquity()  			// used for getting Percentage of Equity 
	       String  getLocalCurrency()    			// used for getting LocalCurrency
		   String  getSalesTaxRegNo()    			// used for getting SalesTaxRegN0
		   String  getDesignation()      			// used for getting Designation
		   String  getContactPerson()      			// used for getting Contact Person
		   String  getAgentJV()          			// used for getting AgentJV i.e.,['A',J']
		   String  getNotes()            			// used for getting Notes
		   Address getAddress()          			// used for getting Address
						   						
	       void setTerminalId(String terminalId)   	// used for setting TerminalId
		   void setCompanyId(String companyId)      // used for setting companyId
		   void setAddressId(int addressId)  		// used for setting addressId
		   setPercentOfEquity( int percentOfEquity )// used for setting Percentage of Equity
		   setLocalCurrency(String localCurrency)   // used for setting LocalCurrency
		   setSalesTaxRegNo(String salesTaxRegNo)   // used for setting SalesTaxRegNo 		
		   setDesignation(String designation)       // used for setting Designation
		   setContactPerson(String contactPerson)   // used for setting ContactPerson
		   setAgentJV()                             // used for setting AgentJv
		   setNotes()                               // used for setting Notes
		   setAddress()                             // used for setting Address Object
		            
*/                          

package com.foursoft.etrans.setup.jointventure.bean;
import com.foursoft.etrans.common.bean.Address;
/**
 * @author	: 
 * @date	  : 2001-08-07
 * @version : 1.6
 */
public class JointVentureJspBean implements java.io.Serializable
{	
  /**
   * 
   */
	public JointVentureJspBean()
 	{
 		  terminalId   	  = null;
 		  companyId   	  = null;
		  companyName 	  = null;
		  addressId   	  = 0;
		  percentOfEquity = 0;
		  currencyId	  = null;
		  salesTaxRegNo   = null;
		  designation 	  = null;
		  contactPerson   = null;
		  AgentJV	      = "A";
		  notes			  = null;
		  address		  = null;
		  city            = null;
 	}

  /**
   * 
   * @return 
   */
	public String getTerminalId()
	{
  		return 	terminalId;
	}
  /**
   * 
   * @return 
   */
	public String getCompanyId()
	{
	  	return 	companyId;
	}
  /**
   * 
   * @return 
   */
	public String getCompanyName()
    {
    	return companyName;
    }
  /**
   * 
   * @return 
   */
    public int getAddressId()
    {
    	return addressId;
    }
  /**
   * 
   * @return 
   */
    public int getPercentOfEquity()
    { 
    	return percentOfEquity;
    }
  /**
   * 
   * @return 
   */
    public String getLocalCurrency()
    {
        return currencyId;
    }
  /**
   * 
   * @return 
   */
    public String getSalesTaxRegNo()
    {
       	return salesTaxRegNo;
    }
  /**
   * 
   * @return 
   */
    public String getDesignation()
    {
     	return designation;
    }
  /**
   * 
   * @return 
   */
    public String getContactPerson()
    {
     	return contactPerson;
    }
  /**
   * 
   * @return 
   */
    public String getAgentJV()
    {
        return AgentJV;
    }
  /**
   * 
   * @return 
   */
    public String getNotes()
    {
    	return notes;
    }
  /**
   * 
   * @return 
   */
    public Address getAddress()
    {
    	return address;
    }
  /**
   * 
   * @param terminalId
   */
    public void setTerminalId( String terminalId)
	{
	  	this.terminalId = terminalId;
	}
  /**
   * 
   * @param companyId
   */
	public void setCompanyId( String companyId )
	{
		  this.companyId = companyId;
	}
  /**
   * 
   * @param companyName
   */
	public void setCompanyName( String companyName )
    {
    	this.companyName = companyName;
    }
  /**
   * 
   * @param addressId
   */
    public void setAddressId( int addressId )
    {
    	this.addressId = addressId;
    } 
  /**
   * 
   * @param percentOfEquity
   */
    public void setPercentOfEquity( int percentOfEquity )
    { 
    	this.percentOfEquity = percentOfEquity;
    }
  /**
   * 
   * @param currencyId
   */
    public void setCurrencyId( String currencyId )
    {
    	this.currencyId = currencyId;
    }
  /**
   * 
   * @param currencyId
   */
    public void setLocalCurrency( String currencyId )
    {
    	this.currencyId = currencyId;
    }
  /**
   * 
   * @param salesTaxRegNo
   */
    public void setSalesTaxRegNo( String salesTaxRegNo )
    {
    	this.salesTaxRegNo = salesTaxRegNo;
    }
  /**
   * 
   * @param contactPerson
   */
    public void setContactPerson( String contactPerson )
    {
    	this.contactPerson = contactPerson;
    }
  /**
   * 
   * @param designation
   */
    public void setDesignation( String designation )
    {
    	this.designation = designation;
    }
  /**
   * 
   * @param AgentJV
   */
    public void setAgentJV( String AgentJV )
    {
    	this.AgentJV = AgentJV;
    }
  /**
   * 
   * @return 
   */
    public String getCity()
    {
    	return city;
 	}
  /**
   * 
   * @param city
   */
    public void setCity( String city)
    {
    	this.city = city;
    }
  /**
   * 
   * @param notes
   */
    public void setNotes( String notes )
    {
    	this.notes = notes;
    }
  /**
   * 
   * @param address
   */
    public void setAddress( Address address )
    {
    	this.address = address;
    }

	// member variables
	private	String 	terminalId   	= null;
	private	String 	companyId   	= null;
	private	String 	companyName 	= null;
	private	int 	addressId   	= 0;
	private	int 	percentOfEquity	= 0;
	private	String 	currencyId	= null;
	private	String 	salesTaxRegNo   = null;
	private	String 	designation 	= null;
	private	String 	contactPerson   = null;
	private	String 	AgentJV	        = "A";
	private	String 	notes			= null;
	private Address address			= null;
	private	String 	city            = null;
} // end of class
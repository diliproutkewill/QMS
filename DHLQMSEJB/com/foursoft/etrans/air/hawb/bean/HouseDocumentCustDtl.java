/**
 * @(#)BreakBulkSessionBean.java         07/08/2001
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
package com.foursoft.etrans.air.hawb.bean;

/**
 * File		: BreakBulkSessionBean.java
 * @author	: 
 * @date	: 2001-08-07
 * @version : 1.6
 */

public class HouseDocumentCustDtl implements java.io.Serializable
{
	private String customerId;
	private String shipperId;
	private String contactName;
	private String companyName;
	private String abbrName;
	private String terminalId;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String countryId;
	private String onlycountryId;
	private String zipCode;
	private String EMailId;
	private String fax;
	private String phoneNo;
	private long   addressId = 0;

	/** 
	 * It's a default Constructor which creates a new HouseDocumentCustDtl Object. 
	 */
	public HouseDocumentCustDtl()
	{
	}

	/**
	 * returns the customer id of the Customer
	 *
	 * @return the customer id as String
	 */
	public String getCustomerId()
	{
		return customerId;
	}
	/**
	 * returns the Shipper id of the Customer
	 *
	 * @return the Shipper id as String
	 */
	public String getShipperId()
	{
		return shipperId;
	}
	
	/**
	 * returns the company Name of the Customer
	 *
	 * @return the company Name as String
	 */
	public String getCompanyName()
	{
		return companyName;
	}

	/**
	 * returns the contact id of the Customer
	 *
	 * @return the contact id as String
	 */
	public String getContactName()
	{
		return contactName;
	}
	
	/**
	 * returns the multiple abbrName names 
	 *
	 * @return the multiple abbrNames as String array
	 */
	public String getAbbrName()
	{
		return abbrName;
	}
	
	/**
	 * returns the terminal id of the Customer
	 *
	 * @return the terminal id as String
	 */
	public String getTerminalId()
	{
		return terminalId;
	}
	
	/**
	 * returns the customer's address line 1
	 *
	 * @return the customer's address line 1 as String
	 */
	public String getAddress1()
	{
		return address1;
	}
	
	/**
	 * returns the customer's address line 2
	 *
	 * @return the customer's address line 2 as String
	 */
	public String getAddress2()
	{
		return address2;
	}
	
	/**
	 * returns the customer city name
	 *
	 * @return the customer city name as String
	 */
	public String getCity()
	{
		return city;
	}
	
	/**
	 * returns the customer state name
	 *
	 * @return the customer state name as String
	 */
	public String getState()
	{
		return state;
	}
	
	/**
	 * returns the customer country id
	 *
	 * @return the customer country id as String
	 */
	public String getCountryId()
	{
		return countryId;
	}
	/**
	 * returns the CountryId
	 *
	 * @return the CountryId as String
	 */
	public String getOnlyCountryId()
	{
		return onlycountryId;
	}
	
	/**
	 * returns the customer zip code
	 *
	 * @return the customer zip code as String
	 */
	public String getZipCode()
	{
		return zipCode;
	}
	
	/**
	 * returns the customer email id
	 *
	 * @return the customer email id as String
	 */
	public String getEMailId()
	{
		return EMailId;
	}
	
	/**
	 * returns the customer fax number
	 *
	 * @return the customer fax number as String
	 */
	public String getFax()
	{
		return fax;
	}
	
	/**
	 * returns the customer contact number
	 *
	 * @return the customer contact number as String
	 */
	public String getPhoneNo()
	{
		return phoneNo;
	}

	/**
	 * returns the customer address id
	 *
	 * @return the customer address id
	 */
	public long getAddressId()
	{
		return addressId;
	}	


	/**
	 * sets customer Id.
	 *
	 * @param customerId the String used to set customer id
	 */
	public void setCustomerId(String customerId)
	{
			this.customerId = customerId;
	}
	/**
	 * sets shipper Id.
	 *
	 * @param shipperid the String used to set shipper id
	 */
	public void setShipperId(String shipperId)
	{
			this.shipperId = shipperId;
	}	
	
	/**
	 * sets customer company name.
	 *
	 * @param companyName the String used to set customer company name
	 */
	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}
	
	/**
	 * sets customer contact name.
	 *
	 * @param contactName the String used to set contact company name
	 */
	public void setContactName(String contactName)
	{
		this.contactName = contactName;
	}
	
	/**
	 * sets multiple abbrNames names
	 *
	 * @param abbrName the String array used to set abbrName  names
	 */
	public void setAbbrName(String abbrName)
	{
			this.abbrName = abbrName;
	}
	
	/**
	 * sets terminal Id.
	 *
	 * @param terminalId the String used to set terminal id
	 */
	public void setTerminalId(String terminalId)
	{
		this.terminalId = terminalId;
	}
	
	/**
	 * sets customer address line 1
	 *
	 * @param address1 the String used to set customer address line 1
	 */
	public void setAddress1(String address1)
	{
			this.address1 = address1;
	}
	
	/**
	 * sets customer address line 2
	 *
	 * @param address2 the String used to set customer address line 2
	 */
	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}
	
	/**
	 * sets customer city name.
	 *
	 * @param city the String used to set customer city name.
	 */ 	
	public void setCity(String city)
	{
		this.city = city;
	}
	
	/**
	 * sets customer state name
	 *
	 * @param state the String used to set customer state name
	 */	
	public void setState(String state)
	{
		this.state = state;
	}
	
	/**
	 * sets customer country Id.
	 *
	 * @param countryId the String used to set customer country id
	 */
	public void setCountryId(String countryId)
	{
		this.countryId = countryId;
	}
	/**
	 * sets CountryId
	 *
	 * @param onlycountryId the String used to set CountryId
	 */
	public void setOnlyCountryId(String onlycountryId)
	{
		this.onlycountryId = onlycountryId;
	}
	
	/**
	 * sets customer  zip code
	 *
	 * @param zipCode the String used to set customer zip code
	 */
	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}
	
	/**
	 * sets customer eamil id
	 *
	 * @param customerId the String used to set customer email id
	 */
	public void setEMailId(String EMailId)
	{
			this.EMailId = EMailId;
	}	
	
	/**
	 * sets customer fax number
	 *
	 * @param fax the String used to set customer fax number
	 */
	public void setFax(String fax)
	{
		this.fax = fax;
	}	
	
	/**
	 * sets customer contact number
	 *
	 * @param phoneNo the String used to set customer contact number
	 */
	public void setPhoneNo(String phoneNo)
	{
		this.phoneNo = phoneNo;
	}
	
	/**
	 * sets customer address id
	 *
	 * @param addressId the long used to set customer address Id
	 */
	public void setAddressId(long addressId)
	{
		this.addressId = addressId;
	}	
}	

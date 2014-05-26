package com.foursoft.etrans.setup.vendorregistration.java;

/**
 * Copyright (c) 2001-2001 Four-Soft Pvt Ltd, 
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information 
 * and shall use it only in accordance with the terms of the license agreement you entered 
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 */
/*	Program Name  	: VendorRegistration.java
	Module Name		: eTrans
	Task					: VendorRegistration
	SubTask			: VendorRegistration
	Author				: Nageswara Rao.D
	Date Started		: January 20,2003
	Date Completed	: January 20,2003
	Date Modified	:
	
	Description			: 
*/
import com.foursoft.etrans.common.bean.Address;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationJava.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */

 public class VendorRegistrationJava implements java.io.Serializable
 {
	 private		String	vendorId				=		null;
	 private		String	terminalId				=		null;
	 private		String	abbrName				=		null;
	 private		String	shipmentMode		=		null;
	 private		String	companyName		  =		null;
	 private		String	contactName		=		null;
	 private		String	carrierId				=		null;
	 private		String	operationMailId	=		null;
	 private		String	indicator				=		null;
	 private		String	designation			=		null;
	 private		String	notes						=		null;
	 private		Address	 addressObj			=	null;
   private    String  vendRegFlag     = null;

//Start the setMethods

  /**
   * 
   * @param vendorId
   */
	public void setVendorId(String vendorId)
	 {
		this.vendorId	=	vendorId;
	 }
  /**
   * 
   * @param terminalId
   */
	public void setTerminalId(String terminalId)
	 {
		this.terminalId = terminalId;
	 }
  /**
   * 
   * @param abbrName
   */
	 public void setAbbrName(String abbrName)
	 {
		 this.abbrName	=	abbrName;
	 }
  /**
   * 
   * @param shipmentMode
   */
	public void setShipmentMode(String shipmentMode)
	 {
		this.shipmentMode	=	shipmentMode;
	 }
  /**
   * 
   * @param companyName
   */
	public void setCompanyName(String companyName)
	 {
		this.companyName	=	companyName;
	 }
  /**
   * 
   * @param carrierId
   */
	public void setCarrierId(String carrierId)
	 {
		this.carrierId	=	carrierId;
	 }
  /**
   * 
   * @param operationMailId
   */
	public void setOperationMailId(String operationMailId)
	 {
		this.operationMailId	=	operationMailId;
	 }
  /**
   * 
   * @param indicator
   */
	public void setIndicator(String indicator)
	 {
		this.indicator	=	indicator;
	 }
  /**
   * 
   * @param notes
   */
	public void setNotes(String notes)
	 {
		this.notes	=	notes;
	 }
  /**
   * 
   * @param contactName
   */
	public void setContactName(String contactName)
	 {
		this.contactName	=	contactName;
	 }
  /**
   * 
   * @param designation
   */
	public void setDesignation(String designation)
	 {
		this.designation =designation;
	 }
  /**
   * 
   * @param addressObj
   */
	public void setAddressObj(Address addressObj)
	 {
		this.addressObj	=addressObj;
	 }
  /**
   * 
   * @param vendRegFlag
   */
  public void setVendRegFlag(String vendRegFlag)
   {
    this.vendRegFlag=vendRegFlag;
   }
//ends here

// Start the getMethods

  /**
   * 
   * @return String
   */
   public String getVendorId()
	 {
		return	vendorId;
	 }
  /**
   * 
   * @return String
   */
	public String getTerminalId()
	 {
		return	terminalId;
	 }
  /**
   * 
   * @return String
   */
	public String getAbbrName()
	 {
		return	abbrName;
	 }
  /**
   * 
   * @return String
   */
	public String	 getShipmentMode()
	 {
		 return	shipmentMode;
	 }
  /**
   * 
   * @return String
   */
	public String getCompanyName()
	 {
		return	companyName;
	 }
  /**
   * 
   * @return String
   */
	public String	getCarrierId()
	 {
		return	carrierId;
	 }
  /**
   * 
   * @return String
   */
	public String	getOperationMailId()
	 {
		return	operationMailId;
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
   * @return String
   */
    public String getNotes()
	 {
		return notes;
	 }
  /**
   * 
   * @return String
   */
	public String getContactName()
	 {
		return contactName;
	 }
	public String getDesignation()
	 {
		return designation;
	 }
  /**
   * 
   * @return Address
   */
  public Address getAddressObj()
	 {
		return addressObj;
	 }
  /**
   * 
   * @return String
   */
  public String getVendRegFlag()
   {
    return vendRegFlag;
   }
 
 }
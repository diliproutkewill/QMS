/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 *
 

 * File					: MultiQuoteMasterDOB.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */
package com.qms.operations.multiquote.dob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;

import javax.management.loading.PrivateClassLoader;
public class MultiQuoteMasterDOB implements java.io.Serializable
{
  //private long quoteId;//@@ Commented By subrahmanyam for the Enhancement #146971 on 02/12/2008
  private String quoteId;//@@ Addedd By subrahmanyam for the Enhancement  #146971 on 02/12/2008
  private int shipmentMode;
  //private long preQuoteId; //@@ Commented By subrahmanyam for the Enhancement #146971 on 02/12/2008
  private String preQuoteId; //@@ Added By subrahmanyam for the Enhancement #146971 on 02/12/2008
  private boolean impFlag;//if important true
  private Timestamp effDate;
  private Timestamp validTo;
  private int accValidityPeriod;
  private String customerId;
  private int customerAddressId;
  private String[] customerContacts;
  private Timestamp createdDate;
  private String createdBy;
  private String salesPersonCode;
  private String industryId;
  private String commodityId;
  private boolean hazardousInd;//if hazardous true
  private String unNumber;
  private String commodityClass;
  private String serviceLevelId;
  private String quotingStation;
  private String multiquoteweightBrake;
  private String[] originLocation;
  private String[] originPort;
  private String[] destLocation;
  private String[] destPort;
  private String[] shipperZipCode;
  private String[] consigneeZipCode;
  private String[] shipperZones;
  private String[] consigneeZones;
  private String[] cargoAccPlace;
  private String[] cargoAcceptance;
  private String[] incoTermsId;
  private String[] spotRatesFlag; 
  private String overLengthCargoNotes;
  private String routeId;
  private String[] headerFooter;
  private String[] contentOnQuote;
  private String[] levels;
  private String[] align;
  private boolean emailFlag;//if email is checked then true
  private boolean faxFlag;//if fax is checked then true
  private boolean printFlag;//if print flag is checked then true
  private long versionNo;
  private String terminalId;
  private String quoteWith;//Added Anil.k
  private String customerAddress;
  private String accessLevel;
  private String buyRatesPermission;
  private Timestamp modifiedDate;
  private String modifiedBy;
  private String terminalCurrency;
  private String marginLimit;
  private long uniqueId;
  private String originTerminal;
  private String destinationTerminal;
  private String userId;
  private String userEmailId;
  private String operation;
  private String[] custContactNames;
  private String commodityType;
  private String salesPersonName;
  private String salesPersonEmail;
  private String[] customerContactsEmailIds;
  private String creatorDetails;
  private String terminalAddress;
  private String[] customerContactsFax;
  private String empId;
  private String[] defaultHeaderFooter;
  private String[] defaultContent;
  private String shipperMode;
  private String consigneeMode;
  private String shipperConsoleType;
  private String consigneeConsoleType;
  private String salesPersonFlag;
  private String phoneNo;//@@Added by Kameswari for the WPBN issue-61303
  private String faxNo;//@@Added by Kameswari for the WPBN issue-61303
  private String mobileNo;//@@Added by Kameswari for the WPBN issue-61303
  private String companyName;//@@Added by Kameswari for the WPBN issue-61303
  private String companyId;//@@Added by Kameswari for the WPBN issue-61303
  private String countryId;//@@Added by Kameswari for the Change Request -71229
  private String quoteStatus;//@@Added by VLAKSHMI 
  private String ActiveFlag;//@@Added by VLAKSHMI
  private HashMap attentionToDetails;// @@ added by phani sekhar for wpbn 167678 
  private Timestamp custDate;//Added by Rakesh on 23-02-2011 for  Issue:236359
  private String custTime;//Added by Rakesh on 23-02-2011 for  Issue:
  private String[] customerContactNo;
  private long[] uniqueIds;//Added by Anil.k for Spot Rates
  private String[] spotrateSurchargeCount;
  private String frequencyChecked;//@@Added by kiran.v on 08/08/2011 for Wpbn Issue 258778
  private String transitTimeChecked;
  private String carrierChecked;
  private String frequencyValidityChecked;
  private String serviceChecked;
  private boolean incomplete_screen;

public String getFrequencyChecked() {
	return frequencyChecked;
}

public void setFrequencyChecked(String frequencyChecked) {
	this.frequencyChecked = frequencyChecked;
}

public String getTransitTimeChecked() {
	return transitTimeChecked;
}

public void setTransitTimeChecked(String transitTimeChecked) {
	this.transitTimeChecked = transitTimeChecked;
}

public String getCarrierChecked() {
	return carrierChecked;
}

public void setCarrierChecked(String carrierChecked) {
	this.carrierChecked = carrierChecked;
}

public String getFrequencyValidityChecked() {
	return frequencyValidityChecked;
}

public void setFrequencyValidityChecked(String frequencyValidityChecked) {
	this.frequencyValidityChecked = frequencyValidityChecked;
}

public String getServiceChecked() {
	return serviceChecked;
}

public void setServiceChecked(String serviceChecked) {
	this.serviceChecked = serviceChecked;
}

public long[] getUniqueIds() {
	return uniqueIds;
}

public void setUniqueIds(long[] uniqueIds) {
	this.uniqueIds = uniqueIds;
}
//Ended by Anil.k for SpotRates
//Added to get Attention To values in Modify Case
  public String[] getCustomerContactNo() {
	return customerContactNo;
}

public void setCustomerContactNo(String[] customerContactNo) {
	this.customerContactNo = customerContactNo;
}//Ended to get Attention To values in Modify Case

  /** 
   * 
   * default constructor
   */
  public MultiQuoteMasterDOB()
  {
  }

//Getter and Setter methods for the variables defined above.
//@@ Commented by subrahmanyam for the Enhancement #146971 on 02/12/2008
  /*public long getQuoteId()
  {
    return quoteId;
  }

  public void setQuoteId(long quoteId)
  {
    this.quoteId = quoteId;
  }*/
//@@ Added by subrahmanyam for the enhancement #146971 on 02/12/2008
  public String getQuoteId()
  {
    return quoteId;
  }

  public void setQuoteId(String quoteId)
  {
    this.quoteId = quoteId;
  }
//@@ Ended by subrahmanyam for the enhancement #146971 on 02/12/2008

  public int getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(int shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }
//@@ Commented by subrahmanyam for the enhancement #146971 on 02/12/2008
  /*public long getPreQuoteId()
  {
    return preQuoteId;
  }

  public void setPreQuoteId(long preQuoteId)
  {
    this.preQuoteId = preQuoteId;
  }*/
//@@ Added by subrahmanyam for the enhancement #146971 on 02/12/2008
public String getPreQuoteId()
  {
    return preQuoteId;
  }

  public void setPreQuoteId(String preQuoteId)
  {
    this.preQuoteId = preQuoteId;
  }
//@@ Ended by subrahmanyam for the enhancement  #146971 on 02/12/2008
  public boolean isImpFlag()
  {
    return impFlag;
  }

  public void setImpFlag(boolean impFlag)
  {
    this.impFlag = impFlag;
  }

  public Timestamp getEffDate()
  {
    return effDate;
  }

  public void setEffDate(Timestamp effDate)
  {
    this.effDate = effDate;
  }

  public Timestamp getValidTo()
  {
    return validTo;
  }

  public void setValidTo(Timestamp validTo)
  {
    this.validTo = validTo;
  }

  public int getAccValidityPeriod()
  {
    return accValidityPeriod;
  }

  public void setAccValidityPeriod(int accValidityPeriod)
  {
    this.accValidityPeriod = accValidityPeriod;
  }

  public String getCustomerId()
  {
    return customerId;
  }

  public void setCustomerId(String customerId)
  {
    this.customerId = customerId;
  }

  public int getCustomerAddressId()
  {
    return customerAddressId;
  }

  public void setCustomerAddressId(int customerAddressId)
  {
    this.customerAddressId = customerAddressId;
  }

  public String[] getCustomerContacts()
  {
    return customerContacts;
  }

  public void setCustomerContacts(String[] customerContacts)
  {
    this.customerContacts = customerContacts;
  }

  public Timestamp getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Timestamp createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }

  public String getSalesPersonCode()
  {
    return salesPersonCode;
  }

  public void setSalesPersonCode(String salesPersonCode)
  {
    this.salesPersonCode = salesPersonCode;
  }

  public String getIndustryId()
  {
    return industryId;
  }

  public void setIndustryId(String industryId)
  {
    this.industryId = industryId;
  }

  public String getCommodityId()
  {
    return commodityId;
  }

  public void setCommodityId(String commodityId)
  {
    this.commodityId = commodityId;
  }

  public boolean isHazardousInd()
  {
    return hazardousInd;
  }

  public void setHazardousInd(boolean hazardousInd)
  {
    this.hazardousInd = hazardousInd;
  }

  public String getUnNumber()
  {
    return unNumber;
  }

  public void setUnNumber(String unNumber)
  {
    this.unNumber = unNumber;
  }

  public String getCommodityClass()
  {
    return commodityClass;
  }

  public void setCommodityClass(String commodityClass)
  {
    this.commodityClass = commodityClass;
  }

  public String getServiceLevelId()
  {
    return serviceLevelId;
  }

  public void setServiceLevelId(String serviceLevelId)
  {
    this.serviceLevelId = serviceLevelId;
  }

  public String[] getIncoTermsId()
  {
    return incoTermsId;
  }

  public void setIncoTermsId(String[] incoTermsId)
  {
    this.incoTermsId = incoTermsId;
  }

 public void setSpotRatesFlag(String[] spotRatesFlag)
	{
	 this.spotRatesFlag=spotRatesFlag;
	}

	public String[] getSpotRatesFlag()
	{
         return spotRatesFlag;
	}




  public String getQuotingStation()
  {
    return quotingStation;
  }

  public void setQuotingStation(String quotingStation)
  {
    this.quotingStation = quotingStation;
  }

  public String[] getOriginLocation()
  {
    return originLocation;
  }

  public void setOriginLocation(String[] originLocation)
  {
    this.originLocation = originLocation;
  }

  public String[] getShipperZipCode()
  {
    return shipperZipCode;
  }

  public void setShipperZipCode(String[] shipperZipCode)
  {
    this.shipperZipCode = shipperZipCode;
  }

  public String[] getOriginPort()
  {
    return originPort;
  }
  public void setOriginPort(String[] originPort)
  {
    this.originPort = originPort;
  }

  public String[] getDestLocation()
  {
    return destLocation;
  }

  public void setDestLocation(String[] destLocation)
  {
    this.destLocation = destLocation;
  }

  public String getOverLengthCargoNotes()
  {
    return overLengthCargoNotes;
  }

  public void setOverLengthCargoNotes(String overLengthCargoNotes)
  {
    this.overLengthCargoNotes = overLengthCargoNotes;
  }

  public String getRouteId()
  {
    return routeId;
  }

  public void setRouteId(String routeId)
  {
    this.routeId = routeId;
  }

  public String[] getConsigneeZipCode()
  {
    return consigneeZipCode;
  }

  public void setConsigneeZipCode(String[] consigneeZipCode)
  {
    this.consigneeZipCode = consigneeZipCode;
  }

  public String[] getDestPort()
  {
    return destPort;
  }

  public void setDestPort(String[] destPort)
  {
    this.destPort = destPort;
  }





 

  public String[] getContentOnQuote()
  {
    return contentOnQuote;
  }

  public void setContentOnQuote(String[] contentOnQuote)
  {
    this.contentOnQuote = contentOnQuote;
  }

  public String[] getLevels()
  {
    return levels;
  }

  public void setLevels(String[] levels)
  {
    this.levels = levels;
  }

  public String[] getAlign()
  {
    return align;
  }

  public void setAlign(String[] align)
  {
    this.align = align;
  }

  public String[] getHeaderFooter()
  {
    return headerFooter;
  }



  public boolean isEmailFlag()
  {
    return emailFlag;
  }

  public void setEmailFlag(boolean emailFlag)
  {
    this.emailFlag = emailFlag;
  }

  public boolean isFaxFlag()
  {
    return faxFlag;
  }

  public void setFaxFlag(boolean faxFlag)
  {
    this.faxFlag = faxFlag;
  }

  public boolean isPrintFlag()
  {
    return printFlag;
  }

  public void setPrintFlag(boolean printFlag)
  {
    this.printFlag = printFlag;
  }

  public void setHeaderFooter(String[] headerFooter)
  {
    this.headerFooter = headerFooter;
  }

  public long getVersionNo()
  {
    return versionNo;
  }

  public void setVersionNo(long versionNo)
  {
    this.versionNo = versionNo;
  }



  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }

  public String[] getCargoAccPlace()
  {
    return cargoAccPlace;
  }

  public void setCargoAccPlace(String[] cargoAccPlace)
  {
    this.cargoAccPlace = cargoAccPlace;
  }

  public String[] getShipperZones()
  {
    return shipperZones;
  }

  public void setShipperZones(String[] shipperZones)
  {
    this.shipperZones = shipperZones;
  }

  public String[] getConsigneeZones()
  {
    return consigneeZones;
  }

  public void setConsigneeZones(String[] consigneeZones)
  {
    this.consigneeZones = consigneeZones;
  }

  public String getCustomerAddress()
  {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress)
  {
    this.customerAddress = customerAddress;
  }

  public String getAccessLevel()
  {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }

  public String[] getCargoAcceptance()
  {
    return cargoAcceptance;
  }

  public void setCargoAcceptance(String[] cargoAcceptance)
  {
    this.cargoAcceptance = cargoAcceptance;
  }

  public String getBuyRatesPermission()
  {
    return buyRatesPermission;
  }

  public void setBuyRatesPermission(String buyRatesPermission)
  {
    this.buyRatesPermission = buyRatesPermission;
  }



  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy)
  {
    this.modifiedBy = modifiedBy;
  }

  public Timestamp getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Timestamp modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public String getTerminalCurrency()
  {
    return terminalCurrency;
  }

  public void setTerminalCurrency(String terminalCurrency)
  {
    this.terminalCurrency = terminalCurrency;
  }

  public String getMarginLimit()
  {
    return marginLimit;
  }

  public void setMarginLimit(String marginLimit)
  {
    this.marginLimit = marginLimit;
  }

  public long getUniqueId()
  {
    return uniqueId;
  }

  public void setUniqueId(long uniqueId)
  {
    this.uniqueId = uniqueId;
  }

  public String getOriginTerminal()
  {
    return originTerminal;
  }

  public void setOriginTerminal(String originTerminal)
  {
    this.originTerminal = originTerminal;
  }

  public String getDestinationTerminal()
  {
    return destinationTerminal;
  }

  public void setDestinationTerminal(String destinationTerminal)
  {
    this.destinationTerminal = destinationTerminal;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getUserEmailId()
  {
    return userEmailId;
  }

  public void setUserEmailId(String userEmailId)
  {
    this.userEmailId = userEmailId;
  }

  public String getOperation()
  {
    return operation; 
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }

  public String[] getCustContactNames()
  {
    return custContactNames;
  }

  public void setCustContactNames(String[] custContactNames)
  {
    this.custContactNames = custContactNames;
  }

  public String getCommodityType()
  {
    return commodityType;
  }

  public void setCommodityType(String commodityType)
  {
    this.commodityType = commodityType;
  }

  public String getSalesPersonName()
  {
    return salesPersonName;
  }

  public void setSalesPersonName(String salesPersonName)
  {
    this.salesPersonName = salesPersonName;
  }

  public String getSalesPersonEmail()
  {
    return salesPersonEmail;
  }

  public void setSalesPersonEmail(String salesPersonEmail)
  {
    this.salesPersonEmail = salesPersonEmail;
  }

  public String[] getCustomerContactsEmailIds()
  {
    return customerContactsEmailIds;
  }

  public void setCustomerContactsEmailIds(String[] customerContactsEmailIds)
  {
    this.customerContactsEmailIds = customerContactsEmailIds;
  }

  public String getCreatorDetails()
  {
    return creatorDetails;
  }

  public void setCreatorDetails(String creatorDetails)
  {
    this.creatorDetails = creatorDetails;
  }

  public String getTerminalAddress()
  {
    return terminalAddress;
  }

  public void setTerminalAddress(String terminalAddress)
  {
    this.terminalAddress = terminalAddress;
  }

  public String[] getCustomerContactsFax()
  {
    return customerContactsFax;
  }

  public void setCustomerContactsFax(String[] customerContactsFax)
  {
    this.customerContactsFax = customerContactsFax;
  }

  public String getEmpId()
  {
    return empId;
  }

  public void setEmpId(String empId)
  {
    this.empId = empId;
  }

  public String[] getDefaultHeaderFooter()
  {
    return defaultHeaderFooter;
  }

  public void setDefaultHeaderFooter(String[] defaultHeaderFooter)
  {
    this.defaultHeaderFooter = defaultHeaderFooter;
  }

  public String[] getDefaultContent()
  {
    return defaultContent;
  }

  public void setDefaultContent(String[] defaultContent)
  {
    this.defaultContent = defaultContent;
  }

  public String getShipperMode()
  {
    return shipperMode;
  }

  public void setShipperMode(String shipperMode)
  {
    this.shipperMode = shipperMode;
  }

  public String getConsigneeMode()
  {
    return consigneeMode;
  }

  public void setConsigneeMode(String consigneeMode)
  {
    this.consigneeMode = consigneeMode;
  }

  public String getShipperConsoleType()
  {
    return shipperConsoleType;
  }

  public void setShipperConsoleType(String shipperConsoleType)
  {
    this.shipperConsoleType = shipperConsoleType;
  }

  public String getConsigneeConsoleType()
  {
    return consigneeConsoleType;
  }

  public void setConsigneeConsoleType(String consigneeConsoleType)
  {
    this.consigneeConsoleType = consigneeConsoleType;
  }

  public String getSalesPersonFlag()
  {
    return salesPersonFlag;
  }

  public void setSalesPersonFlag(String salesPersonFlag)
  {
    this.salesPersonFlag = salesPersonFlag;
  }
//@@Added by Kameswari for the WPBN issue-61303
  public String getPhoneNo()
  {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo)
  {
    this.phoneNo = phoneNo;
  }

  public String getFaxNo()
  {
    return faxNo;
  }

  public void setFaxNo(String faxNo)
  {
    this.faxNo = faxNo;
  }

  public String getMobileNo()
  {
    return mobileNo;
  }

  public void setMobileNo(String mobileNo)
  {
    this.mobileNo = mobileNo;
  }

  public String getCompanyName()
  {
    return companyName;
  }

  public void setCompanyName(String companyName)
  {
    this.companyName = companyName;
  }

  public String getCompanyId()
  {
    return companyId;
  }

  public void setCompanyId(String companyId)
  {
    this.companyId = companyId;
  }
//@@WPBN issue-61303
//@@Added by Kameswari for the Change Request -71229
  public void setCountryId(String countryId)
  {
    this.countryId = countryId;
  }


  public String getCountryId()
  {
    return countryId;
  }


  public void setQuoteStatus(String quoteStatus)
  {
    this.quoteStatus = quoteStatus;
  }


  public String getQuoteStatus()
  {
    return quoteStatus;
  }


  public void setActiveFlag(String ActiveFlag)
  {
    this.ActiveFlag = ActiveFlag;
  }


  public String getActiveFlag()
  {
    return ActiveFlag;
  }


  public void setAttentionToDetails(HashMap attentionToDetails)
  {
    this.attentionToDetails = attentionToDetails;
  }


  public HashMap getAttentionToDetails()
  {
    return attentionToDetails;
  }

public String getMultiquoteweightBrake() {
	return multiquoteweightBrake;
}

public void setMultiquoteweightBrake(String multiquoteweightBrake) {
	this.multiquoteweightBrake = multiquoteweightBrake;
}

public String getQuoteWith() {
	return quoteWith;
}

public void setQuoteWith(String quoteWith) {
	this.quoteWith = quoteWith;
}

public Timestamp getCustDate() {
	return custDate;
}

public void setCustDate(Timestamp custDate) {
	this.custDate = custDate;
}

public String getCustTime() {
	return custTime;
}

public void setCustTime(String custTime) {
	this.custTime = custTime;
}

public String[] getSpotrateSurchargeCount() {
	return spotrateSurchargeCount;
}

public void setSpotrateSurchargeCount(String[] spotrateSurchargeCount) {
	this.spotrateSurchargeCount = spotrateSurchargeCount;
}

public boolean isIncomplete_screen() {
	return incomplete_screen;
}

public void setIncomplete_screen(boolean incomplete_screen) {
	this.incomplete_screen = incomplete_screen;
}



//@@Change Request -71229
}
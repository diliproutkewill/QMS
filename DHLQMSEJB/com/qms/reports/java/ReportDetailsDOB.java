package com.qms.reports.java;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.omg.CORBA.PRIVATE_MEMBER;
public class ReportDetailsDOB implements Serializable
{

//attributes for buyrates expiry report//
  private String fromCountry = "";
  private String fromLocation= "";
  private String toCountry   = "";
  private String toLocation  = "";
  private String carrierId    = "";
  private String serviceLevel = "";
  private Timestamp createDate     = null;//@@Added by Kameswari for the WPBN issue-61310
  private Timestamp effectiveFrom  = null;
  private Timestamp validUpto      = null;
  private long expiryinDays    = 0;
//end of attributes for buyrates expiry report//  
  
  private String customerId   = "";
  private String dueDateNTime      = "";
  //private String approvedBy   = "";
  //private String approvedDateNTime  = "";
  private String shipmentMode = "";
  private String quoteId      = "";
  private String approvedRrejectedBy   = "";
  private String approvedRrejectedDtNtime  = "";
  private String internalRemarks  = "";
  private String externalRemarks  = "";
  private String remarks          = "";
  private String isMultiQuote		  = "";  //Added by Anil.k 
  private String createDateStr		  = "";//added by silpa
  private String accept = "";
  private String important  = "";

  private String salesPerson  = "";
  private String quoteDate    = "";
  private String locationId   = "";
  private String quoteStatus  = "";
  
  private String averageYield = "";
  private String countryId      = "";
  private String operEmailId    = "";
  
  private ArrayList weightBreakRates  = null;
  private String userId;
  private Timestamp approvedRejTstmp;
  private Timestamp dueDateTmstmp;
  private Timestamp quoteDateTstmp;
  private String activeFlag;
  private String dateFormat;
  private String sendOptions;
  private String customerName;//added by kameswari for the WPBN issue-30313
  private String createdBy;//@@Added by subrahmanyam for the 173831
  private String statusReason; //Added By Kishore Podili for CR:231109 on 28-Jan-11
  private String inActive;//Added by Anil.k on 28Feb2011
  //Added by Rakesh on 22-02-2011 for Issue:236359
  private Timestamp modifyDate ;
  private String incoTerms ;
  private String country;
  private String terminalId;
  private Timestamp validTo;
  private Timestamp custDate;
  //Ended by Rakesh on 22-02-2011 for Issue:236359
  //Added by Rakesh on 09-03-2011 for Issue:
  private String currency;
  private String frequency;
  private String is_MultiQuote;
  private String Update_flag;
  private String notes;
  private int versionNo;//Added by Rakesh on 21-03-2011
  private int maxVersionNo;//Added by Rakesh on 21-03-2011
  private String custReqTime;//Added by Rakesh on 21-03-2011
  //Ended by Rakesh on 09-03-2011 for Issue:

  public String getInActive() {
	return inActive;
}

public void setInActive(String inActive) {
	this.inActive = inActive;
}//Ended by Anil.k on 28Feb2011
  
  public ReportDetailsDOB()
  {
  }
  
  public void setCarrierId(String carrierId)
  {
    this.carrierId=  carrierId;
  }
  public String getCarrierId()
  {
    return this.carrierId;
  }
  
  public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel=  serviceLevel;
  }
  public String getServiceLevel()
  {
    return this.serviceLevel;
  }
  
  public void setFromCountry(String fromCountry)
  {
    this.fromCountry=  fromCountry;
  }
  public String getFromCountry()
  {
    return this.fromCountry;
  }
  
  public void setFromLocation(String fromLocation)
  {
    this.fromLocation=  fromLocation;
  }
  public String getFromLocation()
  {
    return this.fromLocation;
  }  
  public void setToCountry(String toCountry)
  {
    this.toCountry=  toCountry;
  }
  public String getToCountry()
  {
    return this.toCountry;
  } 

  public void setToLocation(String toLocation)
  {
    this.toLocation=  toLocation;
  }
  public String getToLocation()
  {
    return this.toLocation;
  } 
  
//@@Added by Kameswari for the WPBN issue-61310
  public void setCreateDate(Timestamp createDate)
  {
    this.createDate=  createDate;
  }
  public Timestamp getCreateDate()
  {
    return this.createDate;
  } 
  //@@ WPBN issue-61310

  public void setEffectiveFrom(Timestamp effectiveFrom)
  {
    this.effectiveFrom=  effectiveFrom;
  }
  public Timestamp getEffectiveFrom()
  {
    return this.effectiveFrom;
  }   

  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto=  validUpto;
  }
  public Timestamp getValidUpto()
  {
    return this.validUpto;
  } 
  

  public void setExpiryinDays(long expiryinDays)
  {
    this.expiryinDays=  expiryinDays;
  }
  public long getExpiryinDays()
  {
    return this.expiryinDays;
  }   
  
  public void setCustomerId(String customerId)
  {
    this.customerId = customerId;
  }
  public String getCustomerId()
  {
    return this.customerId;
  }
  
  public void setDueDateNTime(String dueDateNTime)
  {
    this.dueDateNTime = dueDateNTime;
  }
  public String getDueDateNTime()
  {
    return this.dueDateNTime;
  }
  
  /*public void setApprovedBy(String approvedBy)
  {
    this.approvedBy = approvedBy;
  }
  public String getApprovedBy()
  {
    return this.approvedBy;
  }
  
  public void setApprovedDateNTime(String approvedDateNTime)
  {
    this.approvedDateNTime = approvedDateNTime;
  }
  public String getApprovedDateNTime()
  {
    return this.approvedDateNTime;
  }*/
  
  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }
  public String getShipmentMode()
  {
    return this.shipmentMode;
  }
  
  public void setQuoteId(String quoteId)
  {
    this.quoteId = quoteId;
  }
  public String getQuoteId()
  {
    return this.quoteId;
  }
  
  public void setApprovedRrejectedBy(String approvedRrejectedBy)
  {
    this.approvedRrejectedBy = approvedRrejectedBy;
  }
  public String getApprovedRrejectedBy()
  {
    return this.approvedRrejectedBy;
  }
  
  public void setApprovedRrejectedDtNtime(String approvedRrejectedDtNtime)
  {
    this.approvedRrejectedDtNtime = approvedRrejectedDtNtime;
  }
  public String getApprovedRrejectedDtNtime()
  {
    return this.approvedRrejectedDtNtime;
  }
  
  public void setAccept(String accept)
  {
    this.accept = accept;
  }
  public String getAccept()
  {
    return this.accept;
  }
  
  public void setImportant(String important)
  {
    this.important = important;
  }
  public String getImportant()
  {
    return this.important;
  }

  public void setInternalRemarks(String internalRemarks)
  {
    this.internalRemarks = internalRemarks;
  }
  public String getInternalRemarks()
  {
    return this.internalRemarks;
  }
  
  public void setExternalRemarks(String externalRemarks)
  {
    this.externalRemarks = externalRemarks;
  }
  public String getExternalRemarks()
  {
    return this.externalRemarks;
  }
  
  public void setRemarks(String remarks)
  {
    this.remarks  = remarks;
  }
  public String getRemarks()
  {
    return this.remarks;
  }
  
  public void setSalesPerson(String salesPerson)
  {
    this.salesPerson = salesPerson;
  }
  public String getSalesPerson()
  {
    return this.salesPerson;
  }
  
  public void setQuoteDate(String quoteDate)
  {
    this.quoteDate = quoteDate;
  }
  public String getQuoteDate()
  {
    return this.quoteDate;
  }
  

  public void setLocationId(String locationId)
  {
    this.locationId = locationId;
  }
  public String getLocationId()
  {
    return this.locationId;
  }
  
  public void setQuoteStatus(String quoteStatus)
  {
    this.quoteStatus = quoteStatus;
  }
  public String getQuoteStatus()
  {
    return this.quoteStatus;
  }
  
  public void setAverageYield(String averageYield)
  {
    this.averageYield  = averageYield;
  }
  public String getAverageYield()
  {
    return this.averageYield;
  }
  
  public void setCountryId(String countryId)
  {
    this.countryId = countryId;
  }
  public String getCountryId()
  {
    return this.countryId;
  }
  public void setWeightBreakRates(ArrayList weightBreakRates)
  {
    this.weightBreakRates = weightBreakRates;
  }
  public ArrayList  getWeightBreakRates()
  {
    return this.weightBreakRates;
  }


  public void setOperEmailId(String operEmailId)
  {
    this.operEmailId = operEmailId;
  }


  public String getOperEmailId()
  {
    return operEmailId;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public Timestamp getApprovedRejTstmp()
  {
    return approvedRejTstmp;
  }

  public void setApprovedRejTstmp(Timestamp approvedRejTstmp)
  {
    this.approvedRejTstmp = approvedRejTstmp;
  }

  public Timestamp getDueDateTmstmp()
  {
    return dueDateTmstmp;
  }

  public void setDueDateTmstmp(Timestamp dueDateTmstmp)
  {
    this.dueDateTmstmp = dueDateTmstmp;
  }

  public Timestamp getQuoteDateTstmp()
  {
    return quoteDateTstmp;
  }

  public void setQuoteDateTstmp(Timestamp quoteDateTstmp)
  {
    this.quoteDateTstmp = quoteDateTstmp;
  }

  public String getActiveFlag()
  {
    return activeFlag;
  }

  public void setActiveFlag(String activeFlag)
  {
    this.activeFlag = activeFlag;
  }

  public String getDateFormat()
  {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat)
  {
    this.dateFormat = dateFormat;
  }

  public String getSendOptions()
  {
    return sendOptions;
  }

  public void setSendOptions(String sendOptions)
  {
    this.sendOptions = sendOptions;
  }
  //added by kameswari for the WPBN issue-30313
  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }
//@@Added by subrahmanyam for the Enhancement 173831 on 18-Jun-09  
  public void setCreatedBy(String createdBy)
  {
    this.createdBy=createdBy;
  }
  public String getCreatedBy()
  {
    return createdBy;
  }
//@@Ended by subrahmanyam for the Enhancement 173831 on 18-Jun-09  
//Added by Anil.k 
public String getIsMultiQuote() {
	return isMultiQuote;
}

public void setIsMultiQuote(String isMultiQuote) {
	this.isMultiQuote = isMultiQuote;
}//Ended by Anil.k
  
public String getStatusReason() {
	return statusReason;
}

public void setStatusReason(String statusReason) {
	this.statusReason = statusReason;
}
//Added by Rakesh on 22-02-2011 for Issue:236359

public String getIncoTerms() {
	return incoTerms;
}

public void setIncoTerms(String incoTerms) {
	this.incoTerms = incoTerms;
}

public Timestamp getModifyDate() {
	return modifyDate;
}

public void setModifyDate(Timestamp modifyDate) {
	this.modifyDate = modifyDate;
}

public String getCountry() {
	return country;
}

public void setCountry(String country) {
	this.country = country;
}

public String getTerminalId() {
	return terminalId;
}

public void setTerminalId(String terminalId) {
	this.terminalId = terminalId;
}

public Timestamp getValidTo() {
	return validTo;
}

public void setValidTo(Timestamp validTo) {
	this.validTo = validTo;
}

public Timestamp getCustDate() {
	return custDate;
}

public void setCustDate(Timestamp custDate) {
	this.custDate = custDate;
}

public String getCurrency() {
	return currency;
}

public void setCurrency(String currency) {
	this.currency = currency;
}

public String getFrequency() {
	return frequency;
}

public void setFrequency(String frequency) {
	this.frequency = frequency;
}

public String getNotes() {
	return notes;
}

public void setNotes(String notes) {
	this.notes = notes;
}

public int getVersionNo() {
	return versionNo;
}

public void setVersionNo(int versionNo) {
	this.versionNo = versionNo;
}

public String getCustReqTime() {
	return custReqTime;
}

public void setCustReqTime(String custReqTime) {
	this.custReqTime = custReqTime;
}

public int getMaxVersionNo() {
	return maxVersionNo;
}

public void setMaxVersionNo(int maxVersionNo) {
	this.maxVersionNo = maxVersionNo;
}
//added by silpa.p on 4-05-11
public String getCreateDateStr() {
	return createDateStr;
}

public void setCreateDateStr(String createDateStr) {
	this.createDateStr = createDateStr;//modified by silpa.p
}//ended

public String getIs_MultiQuote() {
	return is_MultiQuote;
}

public void setIs_MultiQuote(String is_MultiQuote) {
	this.is_MultiQuote = is_MultiQuote;
}

public String getUpdate_flag() {
	return Update_flag;
}

public void setUpdate_flag(String update_flag) {
	Update_flag = update_flag;
}



  

  
//Ended by Rakesh on 22-02-2011 for Issue:236359
  //WPBN-30313
  
}
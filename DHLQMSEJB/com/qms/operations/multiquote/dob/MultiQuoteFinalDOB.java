
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
 

 * File					: MultiQuoteFinalDOB.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */







package com.qms.operations.multiquote.dob;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.HashMap;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;

public class MultiQuoteFinalDOB implements java.io.Serializable
{
  private MultiQuoteMasterDOB masterDOB;//this holds the master information
  private MultiQuoteTiedCustomerInfo tiedCustomerInfo;
  
  private ArrayList legDetails;
  
  private MultiQuoteHeader headerDOB;//this holds the header information of the quote
  private ArrayList pickUpCartageRatesList;
  private String delCartageRatesList;
  private ArrayList deliveryCartageRatesList;//Added by Sanjay 
  private ArrayList pickUpZoneZipMap;
  private ArrayList delZoneZipMap;
  private HashMap pickZoneZipMap;//Added by Sanjay 
  private HashMap deliveryZoneZipMap;//Added by Sanjay 
  private MultiQuoteFlagsDOB flagsDOB;
  private MultiQuoteFlagsDOB preFlagsDOB;
  private double cartageMargin;
  private double chargesDiscount;
  private String[] internalNotes;
  private String[] externalNotes;
  private String reportingOfficer;
  private String escalatedTo;
  private String userEmailId;
  private String spotRatesFlag;
  private boolean compareFlag;
  private CostingMasterDOB costingMasterDOB;
  private ArrayList tiedCustomerInfoFreightList;
  private UpdatedQuotesReportDOB updatedReportDOB;
  private boolean cartageMarginDefined;
  private boolean chargesMarginDefined;
  private String reportingOfficerEmail;
  private double chargesMargin;
  private boolean multiModalQuote;
  private String allottedTime;
  private String emailChargeName;
  private ArrayList deliveryWeightBreaks;
  private ArrayList pickupWeightBreaks;
  private String emailText;//@@Added by Kameswari for  the WPBN issue -61295
  private String defaultFlag;//@@Added by kameswari for  the WPBN issue-61289  
  private ArrayList attachmentDOBList;//@@Added by Kameswari for the WPBN issue-61289  
  private String update;
//@@ Added by subrahmanyam for 154381 on 03/02/09  
  private String multiQuoteSelectedBreaks;   
//Ended by subrahmanyam for 154381 on 03/02/09  
  private String updateQuoteFlag ;// added  by phani sekhar for wpbn 173666 on 20090615
  private double cartageDiscount;
  private String operation;
  private ArrayList originChargesList;//Added for the issue 234719
  private ArrayList destChargesList;//Added for the issue 234719
  //Added by Rakesh on 18-02-2011
  private String[] iNotes;
  private String[] eNotes;
  
  public ArrayList getOriginChargesList() {//Added for the issue 234719
	return originChargesList;
}

public void setOriginChargesList(ArrayList originChargesList) {
	this.originChargesList = originChargesList;
}

public ArrayList getDestChargesList() {
	return destChargesList;
}

public void setDestChargesList(ArrayList destChargesList) {
	this.destChargesList = destChargesList;
}//Ended for the Issue 234719

  public String getOperation() {
	return operation;
}

public void setOperation(String operation) {
	this.operation = operation;
}

  public MultiQuoteFinalDOB()
  {
  }

  public MultiQuoteMasterDOB getMasterDOB()
  {
    return masterDOB;
  }

  public void setMasterDOB(MultiQuoteMasterDOB masterDOB)
  {
    this.masterDOB = masterDOB;
  }

  public MultiQuoteTiedCustomerInfo getTiedCustomerInfo()
  {
    return tiedCustomerInfo;
  }

  public void setTiedCustomerInfo(MultiQuoteTiedCustomerInfo tiedCustomerInfo)
  {
    this.tiedCustomerInfo = tiedCustomerInfo;
  }

  

  public MultiQuoteHeader getHeaderDOB()
  {
    return headerDOB;
  }

  public void setHeaderDOB(MultiQuoteHeader headerDOB)
  {
    this.headerDOB = headerDOB;
  }



  public ArrayList getPickUpCartageRatesList()
  {
    return pickUpCartageRatesList;
  }

  public void setPickUpCartageRatesList(ArrayList pickUpCartageRatesList)
  {
    this.pickUpCartageRatesList = pickUpCartageRatesList;
  }

  public String getDelCartageRatesList()
  {
    return delCartageRatesList;
  }

  public void setDelCartageRatesList(String delCartageRatesList)
  {
    this.delCartageRatesList = delCartageRatesList;
  }
  
  public ArrayList getDeliveryCartageRatesList()
  {
    return deliveryCartageRatesList;
  }

  public void setDeliveryCartageRatesList(ArrayList deliveryCartageRatesList)
  {
    this.deliveryCartageRatesList = deliveryCartageRatesList;
  }

  public ArrayList getPickUpZoneZipMap()
  {
    return pickUpZoneZipMap;
  }

  public void setPickUpZoneZipMap(ArrayList pickUpZoneZipMap)
  {
    this.pickUpZoneZipMap = pickUpZoneZipMap;
  }

  public ArrayList getDelZoneZipMap()
  {
    return delZoneZipMap;
  }

  public void setDelZoneZipMap(ArrayList delZoneZipMap)
  {
    this.delZoneZipMap = delZoneZipMap;
  }
  
  public HashMap getDeliveryZoneZipMap()
  {
    return deliveryZoneZipMap;
  }

  public void setDeliveryZoneZipMap(HashMap deliveryZoneZipMap)
  {
    this.deliveryZoneZipMap = deliveryZoneZipMap;
  }
  
  public HashMap getPickZoneZipMap()
  {
    return pickZoneZipMap;
  }

  public void setPickZoneZipMap(HashMap pickZoneZipMap)
  {
    this.pickZoneZipMap = pickZoneZipMap;
  }
  
  public MultiQuoteFlagsDOB getFlagsDOB()
  {
    return flagsDOB;
  }

  public void setFlagsDOB(MultiQuoteFlagsDOB flagsDOB)
  {
    this.flagsDOB = flagsDOB;
  }
  
  public MultiQuoteFlagsDOB getPreFlagsDOB()
  {
    return preFlagsDOB;
  }

  public void setPreFlagsDOB(MultiQuoteFlagsDOB preFlagsDOB)
  {
    this.preFlagsDOB = preFlagsDOB;
  }


  public ArrayList getLegDetails()
  {
    return legDetails;
  }

  public void setLegDetails(ArrayList legDetails)
  {
    this.legDetails = legDetails;
  }
  public String[] getInternalNotes()
  {
    return internalNotes;
  }

  public void setInternalNotes(String[] internalNotes)
  {
    this.internalNotes = internalNotes;
  }

  public String[] getExternalNotes()
  {
    return externalNotes;
  }

  public void setExternalNotes(String[] externalNotes)
  {
    this.externalNotes = externalNotes;
  }

 
  
  

  public String getReportingOfficer()
  {
    return reportingOfficer;
  }

  public void setReportingOfficer(String reportingOfficer)
  {
    this.reportingOfficer = reportingOfficer;
  }

  public String getEscalatedTo()
  {
    return escalatedTo;
  }

  public void setEscalatedTo(String escalatedTo)
  {
    this.escalatedTo = escalatedTo;
  }

  public String getUserEmailId()
  {
    return userEmailId;
  }

  public void setUserEmailId(String userEmailId)
  {
    this.userEmailId = userEmailId;
  }

  public String getSpotRatesFlag()
  {
    return spotRatesFlag;
  }

  public void setSpotRatesFlag(String spotRatesFlag)
  {
    this.spotRatesFlag = spotRatesFlag;
  }

  public boolean isCompareFlag()
  {
    return compareFlag;
  }

  public void setCompareFlag(boolean compareFlag)
  {
    this.compareFlag = compareFlag;
  }

  public CostingMasterDOB getCostingMasterDOB()
  {
    return costingMasterDOB;
  }

  public void setCostingMasterDOB(CostingMasterDOB costingMasterDOB)
  {
    this.costingMasterDOB = costingMasterDOB;
  }

  public ArrayList getTiedCustomerInfoFreightList()
  {
    return tiedCustomerInfoFreightList;
  }

  public void setTiedCustomerInfoFreightList(ArrayList tiedCustomerInfoFreightList)
  {
    this.tiedCustomerInfoFreightList = tiedCustomerInfoFreightList;
  }


 

  public UpdatedQuotesReportDOB getUpdatedReportDOB()
  {
    return updatedReportDOB;
  }

  public void setUpdatedReportDOB(UpdatedQuotesReportDOB updatedReportDOB)
  {
    this.updatedReportDOB = updatedReportDOB;
  }

  public String getReportingOfficerEmail()
  {
    return reportingOfficerEmail;
  }

  public void setReportingOfficerEmail(String reportingOfficerEmail)
  {
    this.reportingOfficerEmail = reportingOfficerEmail;
  }

 

  public boolean isMultiModalQuote()
  {
    return multiModalQuote;
  }

  public void setMultiModalQuote(boolean multiModalQuote)
  {
    this.multiModalQuote = multiModalQuote;
  }

  public String getAllottedTime()
  {
    return allottedTime;
  }

  public void setAllottedTime(String allottedTime)
  {
    this.allottedTime = allottedTime;
  }

  public String getEmailChargeName()
  {
    return emailChargeName;
  }

  public void setEmailChargeName(String emailChargeName)
  {
    this.emailChargeName = emailChargeName;
  }

  public ArrayList getDeliveryWeightBreaks()
  {
    return deliveryWeightBreaks;
  }

  public void setDeliveryWeightBreaks(ArrayList deliveryWeightBreaks)
  {
    this.deliveryWeightBreaks = deliveryWeightBreaks;
  }

  public ArrayList getPickupWeightBreaks()
  {
    return pickupWeightBreaks;
  }

  public void setPickupWeightBreaks(ArrayList pickupWeightBreaks)
  {
    this.pickupWeightBreaks = pickupWeightBreaks;
  }
//@@Added by kameswari for for WPBN issue-61295
  public String getEmailText()
  {
    return emailText;
  }

  public void setEmailText(String emailText)
  {
    this.emailText = emailText;
  }
//@@WPBN issue-61295
//@@Added by kameswari for for WPBN issue-61289
  public String getDefaultFlag()
  {
    return defaultFlag;
  }

  public void setDefaultFlag(String defaultFlag)
  {
    this.defaultFlag = defaultFlag;
  }

  public ArrayList getAttachmentDOBList()
  {
    return attachmentDOBList;
  }

  public void setAttachmentDOBList(ArrayList attachmentDOBList)
  {
    this.attachmentDOBList = attachmentDOBList;
  }

  public String getUpdate()
  {
    return update;
  }

  public void setUpdate(String update)
  {
    this.update = update;
  }
//@@ Added by subrahmanyam for the Enhancement 154381 on 03/02/09
  
  public void setUpdateQuoteFlag(String updateQuoteFlag)
  {
    this.updateQuoteFlag = updateQuoteFlag;
  }


  public String getUpdateQuoteFlag()
  {
    return updateQuoteFlag;
  }
//@@Ended by subrahmanyam for the Enhancement 154381 on 03/02/09
//@@WPBN issue-61289

public double getCartageMargin() {
	return cartageMargin;
}

public void setCartageMargin(double cartageMargin) {
	this.cartageMargin = cartageMargin;
}

public double getChargesDiscount() {
	return chargesDiscount;
}

public void setChargesDiscount(double chargesDiscount) {
	this.chargesDiscount = chargesDiscount;
}

public boolean isCartageMarginDefined()
{
  return cartageMarginDefined;
}

public void setCartageMarginDefined(boolean cartageMarginDefined)
{
  this.cartageMarginDefined = cartageMarginDefined;
}

public boolean isChargesMarginDefined()
{
  return chargesMarginDefined;
}

public void setChargesMarginDefined(boolean chargesMarginDefined)
{
  this.chargesMarginDefined = chargesMarginDefined;
}



public void setChargesMargin(double chargesMargin)
{
  this.chargesMargin = chargesMargin;
}


public double getChargesMargin()
{
  return chargesMargin;
}

public double getCartageDiscount() {
	return cartageDiscount;
}

public void setCartageDiscount(double cartageDiscount) {
	this.cartageDiscount = cartageDiscount;
}

public String getMultiQuoteSelectedBreaks() {
	return multiQuoteSelectedBreaks;
}

public void setMultiQuoteSelectedBreaks(String multiQuoteSelectedBreaks) {
	this.multiQuoteSelectedBreaks = multiQuoteSelectedBreaks;
}

/*public void setCartageDiscount(double cartageDiscount)
{
  this.cartageDiscount = cartageDiscount;
}*/
//Added by Rakesh on 12-01-2011
private int[] selectedOriginChargesListIndices;//to get the indices in the corresponding Origin Charges List which are selected for the quote
public int[] getSelectedOriginChargesListIndices()
{
  return selectedOriginChargesListIndices;
}

public void setSelectedOriginChargesListIndices(int[] selectedOriginChargesListIndices)
{
  this.selectedOriginChargesListIndices = selectedOriginChargesListIndices;
}
private int[] selctedDestChargesListIndices;//to get the indices in the corresponding Dest Charges List which are selected for the quote
public int[] getSelctedDestChargesListIndices()
{
  return selctedDestChargesListIndices;
}

public void setSelctedDestChargesListIndices(int[] selctedDestChargesListIndices)
{
  this.selctedDestChargesListIndices = selctedDestChargesListIndices;
}
//Added by Rakesh on 18-02-2011
public String[] getINotes() {
	return iNotes;
}

public void setINotes(String[] notes) {
	iNotes = notes;
}

public String[] getENotes() {
	return eNotes;
}

public void setENotes(String[] notes) {
	eNotes = notes;
}
}
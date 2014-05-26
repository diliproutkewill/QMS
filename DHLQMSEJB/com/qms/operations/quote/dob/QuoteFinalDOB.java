package com.qms.operations.quote.dob;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.management.loading.PrivateClassLoader;

import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;

public class QuoteFinalDOB implements java.io.Serializable
{
  private QuoteMasterDOB masterDOB;//this holds the master information
  private QuoteTiedCustomerInfo tiedCustomerInfo;
  
  private ArrayList legDetails;
  
  private QuoteHeader headerDOB;//this holds the header information of the quote
  private ArrayList pickUpCartageRatesList;
  private String delCartageRatesList;
  private ArrayList deliveryCartageRatesList;//Added by Sanjay 
  private ArrayList pickUpZoneZipMap;
  private ArrayList delZoneZipMap;
  private HashMap pickZoneZipMap;//Added by Sanjay 
  private HashMap deliveryZoneZipMap;//Added by Sanjay 
  private QuoteFlagsDOB flagsDOB;
  private QuoteFlagsDOB preFlagsDOB;
  
  private ArrayList originChargesList;//to store different origin charges List of Dobs
  private ArrayList destChargesList;//to store different destination charges List of Dobs
  
  private int[] selectedOriginChargesListIndices;//to get the indices in the corresponding Origin Charges List which are selected for the quote
  private int[] selctedDestChargesListIndices;//to get the indices in the corresponding Dest Charges List which are selected for the quote 
  private String[] internalNotes;
  private String[] externalNotes;
  private double originChargesMargin;
  private double destChargesMargin;
  private double originChargesDiscount;
  private double destChargesDiscount;
  private String reportingOfficer;
  private String escalatedTo;
  private String userEmailId;
  private String spotRatesFlag;
  private boolean compareFlag;
  private CostingMasterDOB costingMasterDOB;
  private ArrayList tiedCustomerInfoFreightList;
    
  private double cartageMargin;
  private double chargesMargin;
  private double chargesDiscount;
  private double cartageDiscount;
  private UpdatedQuotesReportDOB updatedReportDOB;
  private String reportingOfficerEmail;
  private boolean cartageMarginDefined;
  private boolean chargesMarginDefined;
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
   private String[] originChargesSelectedFlag;
  private String[] destChargesSelectedFlag;
  private String mlutiQuoteLaneNo;
//Ended by subrahmanyam for 154381 on 03/02/09  
  private String updateQuoteFlag ;// added  by phani sekhar for wpbn 173666 on 20090615
  
  //Added By Kishore For the ChargeBasis in the Annexure PDF on 06-Jun-11
  private ArrayList pickupChargeBasisList;
  private ArrayList delChargeBasisList;
  
  
  public QuoteFinalDOB()
  {
  }

  public QuoteMasterDOB getMasterDOB()
  {
    return masterDOB;
  }

  public void setMasterDOB(QuoteMasterDOB masterDOB)
  {
    this.masterDOB = masterDOB;
  }

  public QuoteTiedCustomerInfo getTiedCustomerInfo()
  {
    return tiedCustomerInfo;
  }

  public void setTiedCustomerInfo(QuoteTiedCustomerInfo tiedCustomerInfo)
  {
    this.tiedCustomerInfo = tiedCustomerInfo;
  }

  

  public QuoteHeader getHeaderDOB()
  {
    return headerDOB;
  }

  public void setHeaderDOB(QuoteHeader headerDOB)
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
  
  public QuoteFlagsDOB getFlagsDOB()
  {
    return flagsDOB;
  }

  public void setFlagsDOB(QuoteFlagsDOB flagsDOB)
  {
    this.flagsDOB = flagsDOB;
  }
  
  public QuoteFlagsDOB getPreFlagsDOB()
  {
    return preFlagsDOB;
  }

  public void setPreFlagsDOB(QuoteFlagsDOB preFlagsDOB)
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
  
  public ArrayList getOriginChargesList()
  {
    return originChargesList;
  }

  public void setOriginChargesList(ArrayList originChargesList)
  {
    this.originChargesList = originChargesList;
  }

  public ArrayList getDestChargesList()
  {
    return destChargesList;
  }

  public void setDestChargesList(ArrayList destChargesList)
  {
    this.destChargesList = destChargesList;
  }
  
  public int[] getSelectedOriginChargesListIndices()
  {
    return selectedOriginChargesListIndices;
  }

  public void setSelectedOriginChargesListIndices(int[] selectedOriginChargesListIndices)
  {
    this.selectedOriginChargesListIndices = selectedOriginChargesListIndices;
  }

  public int[] getSelctedDestChargesListIndices()
  {
    return selctedDestChargesListIndices;
  }

  public void setSelctedDestChargesListIndices(int[] selctedDestChargesListIndices)
  {
    this.selctedDestChargesListIndices = selctedDestChargesListIndices;
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

  public double getOriginChargesMargin()
  {
    return originChargesMargin;
  }

  public void setOriginChargesMargin(double originChargesMargin)
  {
    this.originChargesMargin = originChargesMargin;
  }
  
  public double getDestChargesMargin()
  {
    return destChargesMargin;
  }

  public void setDestChargesMargin(double destChargesMargin)
  {
    this.destChargesMargin = destChargesMargin;
  }

  public double getOriginChargesDiscount()
  {
    return originChargesDiscount;
  }

  public void setOriginChargesDiscount(double originChargesDiscount)
  {
    this.originChargesDiscount = originChargesDiscount;
  }

  public double getDestChargesDiscount()
  {
    return destChargesDiscount;
  }

  public void setDestChargesDiscount(double destChargesDiscount)
  {
    this.destChargesDiscount = destChargesDiscount;
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


  public void setCartageMargin(double cartageMargin)
  {
    this.cartageMargin = cartageMargin;
  }


  public double getCartageMargin()
  {
    return cartageMargin;
  }


  public void setChargesMargin(double chargesMargin)
  {
    this.chargesMargin = chargesMargin;
  }


  public double getChargesMargin()
  {
    return chargesMargin;
  }


  public void setChargesDiscount(double chargesDiscount)
  {
    this.chargesDiscount = chargesDiscount;
  }


  public double getChargesDiscount()
  {
    return chargesDiscount;
  }


  public void setCartageDiscount(double cartageDiscount)
  {
    this.cartageDiscount = cartageDiscount;
  }


  public double getCartageDiscount()
  {
    return cartageDiscount;
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
  public String[] getOriginChargesSelectedFlag()
  {
    return originChargesSelectedFlag;
  }

  public void setOriginChargesSelectedFlag(String[] originChargesSelectedFlag)
  {
    this.originChargesSelectedFlag = originChargesSelectedFlag;
  }


 public String[] getDestChargesSelectedFlag()
  {
    return destChargesSelectedFlag;
  }

  public void setDestChargesSelectedFlag(String[] destChargesSelectedFlag)
  {
    this.destChargesSelectedFlag = destChargesSelectedFlag;
  }


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

public String getMlutiQuoteLaneNo() {
	return mlutiQuoteLaneNo;
}

public void setMlutiQuoteLaneNo(String mlutiQuoteLaneNo) {
	this.mlutiQuoteLaneNo = mlutiQuoteLaneNo;
}

public ArrayList getPickupChargeBasisList() {
	return pickupChargeBasisList;
}

public void setPickupChargeBasisList(ArrayList pickupChargeBasisList) {
	this.pickupChargeBasisList = pickupChargeBasisList;
}

public ArrayList getDelChargeBasisList() {
	return delChargeBasisList;
}

public void setDelChargeBasisList(ArrayList delChargeBasisList) {
	this.delChargeBasisList = delChargeBasisList;
}
}
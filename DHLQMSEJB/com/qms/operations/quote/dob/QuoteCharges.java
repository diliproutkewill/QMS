/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
* 
* This class will be useful to store the charge information such as charge name, margin/discount 
* ,sell rate etc... for different weight breaks. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
* @version 	
* 
*/

/*
Programme Name		  :QuoteCharges.java
Module  Name  		  :Quote.
Task           		  :         
SubTask       		  :   
Author Name         :S Anil Kumar.
Date Started        :
Date Finished       :4th Aug 2005 
Date Modified       :
Description         :
Method's Summary	  :
*/

package com.qms.operations.quote.dob;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.sql.Timestamp;
public class QuoteCharges implements Serializable
{
  private String chargeId;
  private String chargeDescriptionId;
  private String buyChargeId;
  private ArrayList chargeInfoList;
  private String sellChargeId;
  private String buyChargeLaneNo;
  private String sellBuyFlag;
  private String costIncurredAt;
  private String terminalId;
  private String marginDiscountFlag;
  private String selectedFlag;
  private String internalName;
  private String externalName;
  private String consoleType;
  private String frequency;
  private String carrier;
  private String transitTime;
  private Timestamp validUpto;
  private String frequencyChecked;
  private String transitTimeChecked;
  private String carrierChecked;
  private String rateValidityChecked;
  private String servicelevel;
  private String versionNo;// added by VLAKSHMI for issue 146968 on 10/12/2008  
  private String zoneCode; // Added By Kishore podili for multiple Zone Codes Issue
  
  
  public QuoteCharges()
  {
  }
  
  public String getChargeId()
  {
    return chargeId;
  }

  public void setChargeId(String chargeId)
  {
    this.chargeId = chargeId;
  }

  public String getChargeDescriptionId()
  {
    return chargeDescriptionId;
  }

  public void setChargeDescriptionId(String chargeDescriptionId)
  {
    this.chargeDescriptionId = chargeDescriptionId;
  }



  public ArrayList getChargeInfoList()
  {
    return chargeInfoList;
  }

  public void setChargeInfoList(ArrayList chargeInfoList)
  {
    this.chargeInfoList = chargeInfoList;
  }

  public String getBuyChargeId()
  {
    return buyChargeId;
  }

  public void setBuyChargeId(String buyChargeId)
  {
    this.buyChargeId = buyChargeId;
  }

  public String getSellChargeId()
  {
    return sellChargeId;
  }

  public void setSellChargeId(String sellChargeId)
  {
    this.sellChargeId = sellChargeId;
  }

  public String getBuyChargeLaneNo()
  {
    return buyChargeLaneNo;
  }

  public void setBuyChargeLaneNo(String buyChargeLaneNo)
  {
    this.buyChargeLaneNo = buyChargeLaneNo;
  }

  public String getSellBuyFlag()
  {
    return sellBuyFlag;
  }

  public void setSellBuyFlag(String sellBuyFlag)
  {
    this.sellBuyFlag = sellBuyFlag;
  }

  public String getCostIncurredAt()
  {
    return costIncurredAt;
  }

  public void setCostIncurredAt(String costIncurredAt)
  {
    this.costIncurredAt = costIncurredAt;
  }

  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }

  public String getMarginDiscountFlag()
  {
    return marginDiscountFlag;
  }

  public void setMarginDiscountFlag(String marginDiscountFlag)
  {
    this.marginDiscountFlag = marginDiscountFlag;
  }

  public String getSelectedFlag()
  {
    return selectedFlag;
  }

  public void setSelectedFlag(String selectedFlag)
  {
    this.selectedFlag = selectedFlag;
  }

  public String getInternalName()
  {
    return internalName;
  }

  public void setInternalName(String internalName)
  {
    this.internalName = internalName;
  }

  public String getExternalName()
  {
    return externalName;
  }

  public void setExternalName(String externalName)
  {
    this.externalName = externalName;
  }

  public String getConsoleType()
  {
    return consoleType;
  }

  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }

  public String getFrequency()
  {
    return frequency;
  }

  public void setFrequency(String frequency)
  {
    this.frequency = frequency;
  }

  public String getCarrier()
  {
    return carrier;
  }

  public void setCarrier(String carrier)
  {
    this.carrier = carrier;
  }

  public String getTransitTime()
  {
    return transitTime;
  }

  public void setTransitTime(String transitTime)
  {
    this.transitTime = transitTime;
  }

  public Timestamp getValidUpto()
  {
    return validUpto;
  }

  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto = validUpto;
  }

  public String getFrequencyChecked()
  {
    return frequencyChecked;
  }

  public void setFrequencyChecked(String frequencyChecked)
  {
    this.frequencyChecked = frequencyChecked;
  }

  public String getTransitTimeChecked()
  {
    return transitTimeChecked;
  }

  public void setTransitTimeChecked(String transitTimeChecked)
  {
    this.transitTimeChecked = transitTimeChecked;
  }

  public String getCarrierChecked()
  {
    return carrierChecked;
  }

  public void setCarrierChecked(String carrierChecked)
  {
    this.carrierChecked = carrierChecked;
  }

  public String getRateValidityChecked()
  {
    return rateValidityChecked;
  }

  public void setRateValidityChecked(String rateValidityChecked)
  {
    this.rateValidityChecked = rateValidityChecked;
  }

  public String getServicelevel()
  {
    return servicelevel;
  }

  public void setServicelevel(String servicelevel)
  {
    this.servicelevel = servicelevel;
  }

  public String getVersionNo()
  {
    return versionNo;
  }

  public void setVersionNo(String versionNo)
  {
    this.versionNo = versionNo;
  }

public String getZoneCode() {
	return zoneCode;
}

public void setZoneCode(String zoneCode) {
	this.zoneCode = zoneCode;
}




}
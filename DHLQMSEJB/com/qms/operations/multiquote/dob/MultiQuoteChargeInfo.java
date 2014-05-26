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
 

 * File					: MultiQuoteChargeInfo.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */




package com.qms.operations.multiquote.dob;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class MultiQuoteChargeInfo implements Serializable
{
  public MultiQuoteChargeInfo()
  {
  }
  
  private String  breakPoint;
  private String  currency;
  private double  buyRate;
  private double  recOrConSellRrate;
  private String  marginType;
  private double  margin;
  private String  discountType;
  private double  discount;
  private double  sellRate;
  private String  basis;
  private String  ratio;
  private double  sellChargeMargin;
  private String  sellChargeMarginType;
  private boolean marginTestFailed;
  private boolean multiMarginTestFailed;
  private boolean multiDiscountTestFailed;
  private int lineNumber;
  private boolean percentValue;
  private String rateIndicator;
  private String rateDescription;//@@Added by kameswari for Surcharges
//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10     
  private double tieMarginDiscountValue;
  private double tieSellrateValue;
  private String selectedFlag;
//@@Ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   
  private int selectedLaneNum;
// Added for 219979 & 80
  private String[] multiBreakPoints;
  private String[] multiBuyRates;
  private String[] multiSellRates;
  private String[] multiMargins;
  private String[] multiMarginTypes;
  private String[] multiCalSellRates;
  private String[] multiRateDescriptions;
  private String[] multiDiscounts;
  private String[] multiTieMarginDiscountValue;
  private String[] multiDiscountTypes;
  private String[] multiTieCalSellRateValue;
  private String[] multiSelectedFlag;
  private String[] multiLineNo;
  private String[] multiMarginDiscount;
  private String[] multiDiscountMargin;
  private boolean[] freightBrekMarginTest;
  private String  originPort;
  private String  destPort;
  private String  incoTerms;
  private String  carrier;
  private String  serviceLevel;
  private String  frequency;
  private String  transitTime;
  private Timestamp  freightRateValidity;
  //Added by kiran.v on 26/09/2011 for Wpbn Issue 272712
  private String RateValidity;
//  Ended for 219979 & 80 
  
//  MultiQuoteCharges Properties
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
  private String internalName;
  private String externalName;
  private String consoleType;
  private String carrierName;
  private Timestamp validUpto;
  private String frequencyChecked;
  private String transitTimeChecked;
  private String carrierChecked;
  private String rateValidityChecked;
  private String servicelevel;
  private String versionNo;
  private String orginLoc;
  private String destLoc;
  private String checked_Flag;
  private String spotRatesFlag;//Added by Anil.k for SpotRates
  private int multiQuote_SerialNo;// Added by govind for the lanes order issue
private int legNumber; // Added By Gowtham For PDF View Issue
//  End Of MultiQuoteCharges Properties
  private Map<String,String> surChragesMap; //Added by Kishore For SurhCharges in PDF  
  private String serviceChecked; //@@Added by kiran.v on 08/08/2011 for Wpbn Issue 258778
  
  public String getServiceChecked() {
	return serviceChecked;
}

public void setServiceChecked(String serviceChecked) {
	this.serviceChecked = serviceChecked;
}
  
  public String getOrginLoc() {
	return orginLoc;
}

public void setOrginLoc(String orginLoc) {
	this.orginLoc = orginLoc;
}

public String getDestLoc() {
	return destLoc;
}

public void setDestLoc(String destLoc) {
	this.destLoc = destLoc;
}

public String getBasis()
  {
    return basis;
  }

  public void setBasis(String basis)
  {
    this.basis = basis;
  }

  public String getBreakPoint()
  {
    return breakPoint;
  }

  public void setBreakPoint(String breakPoint)
  {
    this.breakPoint = breakPoint;
  }

  public double getBuyRate()
  {
    return buyRate;
  }

  public void setBuyRate(double buyRate)
  {
    this.buyRate = buyRate;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency(String currency)
  {
    this.currency = currency;
  }

  public double getDiscount()
  {
    return discount;
  }

  public void setDiscount(double discount)
  {
    this.discount = discount;
  }

  public String getDiscountType()
  {
    return discountType;
  }

  public void setDiscountType(String discountType)
  {
    this.discountType = discountType;
  }

  public double getMargin()
  {
    return margin;
  }

  public void setMargin(double margin)
  {
    this.margin = margin;
  }

  public String getMarginType()
  {
    return marginType;
  }

  public void setMarginType(String marginType)
  {
    this.marginType = marginType;
  }

  public String getRatio()
  {
    return ratio;
  }

  public void setRatio(String ratio)
  {
    this.ratio = ratio;
  }

  public double getRecOrConSellRrate()
  {
    return recOrConSellRrate;
  }

  public void setRecOrConSellRrate(double recOrConSellRrate)
  {
    this.recOrConSellRrate = recOrConSellRrate;
  }



  public double getSellRate()
  {
    return sellRate;
  }

  public void setSellRate(double sellRate)
  {
    this.sellRate = sellRate;
  }

  public double getSellChargeMargin()
  {
    return sellChargeMargin;
  }

  public void setSellChargeMargin(double sellChargeMargin)
  {
    this.sellChargeMargin = sellChargeMargin;
  }

  public String getSellChargeMarginType()
  {
    return sellChargeMarginType;
  }

  public void setSellChargeMarginType(String sellChargeMarginType)
  {
    this.sellChargeMarginType = sellChargeMarginType;
  }

  public boolean isMarginTestFailed()
  {
    return marginTestFailed;
  }

  public void setMarginTestFailed(boolean marginTestFailed)
  {
    this.marginTestFailed = marginTestFailed;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber)
  {
    this.lineNumber = lineNumber;
  }

  public boolean isPercentValue()
  {
    return percentValue;
  }

  public void setPercentValue(boolean percentValue)
  {
    this.percentValue = percentValue;
  }

  public String getRateIndicator()
  {
    return rateIndicator;
  }

  public void setRateIndicator(String rateIndicator)
  {
    this.rateIndicator = rateIndicator;
  }
//@@Added by kameswari for Surcharge Enahncements
  public String getRateDescription()
  {
    return rateDescription;
  }

  public void setRateDescription(String rateDescription)
  {
    this.rateDescription = rateDescription;
  }
//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   
  
  public void setTieMarginDiscountValue(double tieMarginDiscountValue)
  {
	  this.tieMarginDiscountValue	=	tieMarginDiscountValue;
  }
  public double getTieMarginDiscountValue()
  {
	  return tieMarginDiscountValue;
  }
  public void setTieSellRateValue(double tieSellrateValue)
  {
	  this.tieSellrateValue	=	tieSellrateValue;
  }
  public double getTieSellRateValue()
  {
	  return tieSellrateValue;
  }
  public void setSelectedFlag(String selectedFlag)
  {
	  this.selectedFlag		= selectedFlag;
	  
  }
  public String getSelectedFlag()
  {
	  return selectedFlag;
  }
//@@Ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
  
//  @@ Added for 219979 &80
  public String[] getMultiBreakPoints() {
		return multiBreakPoints;
	}
	public void setMultiBreakPoints(String[] multiBreakPoints) {
		this.multiBreakPoints = multiBreakPoints;
	}
	public String[] getMultiBuyRates() {
		return multiBuyRates;
	}
	public void setMultiBuyRates(String[] multiBuyRates) {
		this.multiBuyRates = multiBuyRates;
	}
	public String[] getMultiSellRates() {
		return multiSellRates;
	}
	public void setMultiSellRates(String[] multiSellRates) {
		this.multiSellRates = multiSellRates;
	}
	public String[] getMultiMargins() {
		return multiMargins;
	}
	public void setMultiMargins(String[] multiMargins) {
		this.multiMargins = multiMargins;
	}
	public String[] getMultiMarginTypes() {
		return multiMarginTypes;
	}
	public void setMultiMarginTypes(String[] multiMarginTypes) {
		this.multiMarginTypes = multiMarginTypes;
	}
	public String[] getMultiCalSellRates() {
		return multiCalSellRates;
	}
	public void setMultiCalSellRates(String[] multiCalSellRates) {
		this.multiCalSellRates = multiCalSellRates;
	}
	public String[] getMultiRateDescriptions() {
		return multiRateDescriptions;
	}
	public void setMultiRateDescriptions(String[] multiRateDescriptions) {
		this.multiRateDescriptions = multiRateDescriptions;
	}
	public String[] getMultiDiscounts() {
		return multiDiscounts;
	}
	public void setMultiDiscounts(String[] multiDiscounts) {
		this.multiDiscounts = multiDiscounts;
	}
	public String[] getMultiTieMarginDiscountValue() {
		return multiTieMarginDiscountValue;
	}
	public void setMultiTieMarginDiscountValue(String[] multiTieMarginDiscountValue) {
		this.multiTieMarginDiscountValue = multiTieMarginDiscountValue;
	}
	public String[] getMultiDiscountTypes() {
		return multiDiscountTypes;
	}
	public void setMultiDiscountTypes(String[] multiDiscountTypes) {
		this.multiDiscountTypes = multiDiscountTypes;
	}
	public String[] getMultiTieCalSellRateValue() {
		return multiTieCalSellRateValue;
	}
	public void setMultiTieCalSellRateValue(String[] multiTieCalSellRateValue) {
		this.multiTieCalSellRateValue = multiTieCalSellRateValue;
	}
	public String[] getMultiSelectedFlag() {
		return multiSelectedFlag;
	}
	public void setMultiSelectedFlag(String[] multiSelectedFlag) {
		this.multiSelectedFlag = multiSelectedFlag;
	}
	public String[] getMultiLineNo() {
		return multiLineNo;
	}
	public void setMultiLineNo(String[] multiLineNo) {
		this.multiLineNo = multiLineNo;
	}

	public double getTieSellrateValue() {
		return tieSellrateValue;
	}

	public void setTieSellrateValue(double tieSellrateValue) {
		this.tieSellrateValue = tieSellrateValue;
	}

	public String[] getMultiMarginDiscount() {
		return multiMarginDiscount;
	}

	public void setMultiMarginDiscount(String[] multiMarginDiscount) {
		this.multiMarginDiscount = multiMarginDiscount;
	}

	public String getOriginPort() {
		return originPort;
	}

	public void setOriginPort(String originPort) {
		this.originPort = originPort;
	}

	public String getDestPort() {
		return destPort;
	}

	public void setDestPort(String destPort) {
		this.destPort = destPort;
	}

	public String getIncoTerms() {
		return incoTerms;
	}

	public void setIncoTerms(String incoTerms) {
		this.incoTerms = incoTerms;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getTransitTime() {
		return transitTime;
	}

	public void setTransitTime(String transitTime) {
		this.transitTime = transitTime;
	}

	public Timestamp getFreightRateValidity() {
		return freightRateValidity;
	}

	public void setFreightRateValidity(Timestamp freightRateValidity) {
		this.freightRateValidity = freightRateValidity;
	}

	public String getChargeId() {
		return chargeId;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}

	public String getChargeDescriptionId() {
		return chargeDescriptionId;
	}

	public void setChargeDescriptionId(String chargeDescriptionId) {
		this.chargeDescriptionId = chargeDescriptionId;
	}

	public String getBuyChargeId() {
		return buyChargeId;
	}

	public void setBuyChargeId(String buyChargeId) {
		this.buyChargeId = buyChargeId;
	}

	public ArrayList getChargeInfoList() {
		return chargeInfoList;
	}

	public void setChargeInfoList(ArrayList chargeInfoList) {
		this.chargeInfoList = chargeInfoList;
	}

	public String getSellChargeId() {
		return sellChargeId;
	}

	public void setSellChargeId(String sellChargeId) {
		this.sellChargeId = sellChargeId;
	}

	public String getBuyChargeLaneNo() {
		return buyChargeLaneNo;
	}

	public void setBuyChargeLaneNo(String buyChargeLaneNo) {
		this.buyChargeLaneNo = buyChargeLaneNo;
	}

	public String getSellBuyFlag() {
		return sellBuyFlag;
	}

	public void setSellBuyFlag(String sellBuyFlag) {
		this.sellBuyFlag = sellBuyFlag;
	}

	public String getCostIncurredAt() {
		return costIncurredAt;
	}

	public void setCostIncurredAt(String costIncurredAt) {
		this.costIncurredAt = costIncurredAt;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getMarginDiscountFlag() {
		return marginDiscountFlag;
	}

	public void setMarginDiscountFlag(String marginDiscountFlag) {
		this.marginDiscountFlag = marginDiscountFlag;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public String getExternalName() {
		return externalName;
	}

	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}

	public String getConsoleType() {
		return consoleType;
	}

	public void setConsoleType(String consoleType) {
		this.consoleType = consoleType;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public Timestamp getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(Timestamp validUpto) {
		this.validUpto = validUpto;
	}

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

	public String getRateValidityChecked() {
		return rateValidityChecked;
	}

	public void setRateValidityChecked(String rateValidityChecked) {
		this.rateValidityChecked = rateValidityChecked;
	}

	public String getServicelevel() {
		return servicelevel;
	}

	public void setServicelevel(String servicelevel) {
		this.servicelevel = servicelevel;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String[] getMultiDiscountMargin() {
		return multiDiscountMargin;
	}

	public void setMultiDiscountMargin(String[] multiDiscountMargin) {
		this.multiDiscountMargin = multiDiscountMargin;
	}

	public boolean isMultiMarginTestFailed() {
		return multiMarginTestFailed;
	}

	public void setMultiMarginTestFailed(boolean multiMarginTestFailed) {
		this.multiMarginTestFailed = multiMarginTestFailed;
	}

	public boolean isMultiDiscountTestFailed() {
		return multiDiscountTestFailed;
	}

	public void setMultiDiscountTestFailed(boolean multiDiscountTestFailed) {
		this.multiDiscountTestFailed = multiDiscountTestFailed;
	}

	public int getSelectedLaneNum() {
		return selectedLaneNum;
	}

	public void setSelectedLaneNum(int selectedLaneNum) {
		this.selectedLaneNum = selectedLaneNum;
	}

	public String getChecked_Flag() {
		return checked_Flag;
	}

	public void setChecked_Flag(String checked_Flag) {
		this.checked_Flag = checked_Flag;
	}

		public int getLegNumber() {
		return legNumber;
	}

	public void setLegNumber(int legNumber) {
		this.legNumber = legNumber;
	}

	public boolean[] getFreightBrekMarginTest() {
		return freightBrekMarginTest;
	}

	public void setFreightBrekMarginTest(boolean[] freightBrekMarginTest) {
		this.freightBrekMarginTest = freightBrekMarginTest;
	}
	//Added by Anil.k for SpotRates
	public String getSpotRatesFlag() {
		return spotRatesFlag;
	}

	public void setSpotRatesFlag(String spotRatesFlag) {
		this.spotRatesFlag = spotRatesFlag;
	}
	//Ended by Anil.k for SpotRates

	public int getMultiQuote_SerialNo() {
		return multiQuote_SerialNo;
	}

	public void setMultiQuote_SerialNo(int multiQuote_SerialNo) {
		this.multiQuote_SerialNo = multiQuote_SerialNo;
	}
	
	public Map<String, String> getSurChragesMap() {
		return surChragesMap;
	}

	public void setSurChragesMap(Map<String, String> surChragesMap) {
		this.surChragesMap = surChragesMap;
	}
 //Added by kiran.v on 26/09/2011 for Wpbn Issue 272712
	public String getRateValidity() {
		return RateValidity;
	}

	public void setRateValidity(String rateValidity) {
		RateValidity = rateValidity;
	}

	
	
	
	
	
//	@@ Ended for 219979 & 80
}
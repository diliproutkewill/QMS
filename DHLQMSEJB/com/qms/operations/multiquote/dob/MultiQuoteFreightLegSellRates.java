/**
  *
  * Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
  * This software is the proprietary information of FourSoft, Pvt Ltd.
  * Use is subject to license terms.
  *
  * QMS - v 1.x
  * 
  * This class will be useful to store the freight sell rates between origin location and destination location. When data is retrived
  * from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
  * @version 	
  *  

  Programme Name		  :MultiQuoteFreightLegSellRates.java
  Module  Name  		  :Quote.
  Task           		  :         
  SubTask       		  :   
  Author Name         	  :Govind
  Date Started            :
  Date Finished           : 
  Date Modified           :
  Description             :
  Method's Summary	      :

  */

package com.qms.operations.multiquote.dob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;

public class MultiQuoteFreightLegSellRates implements java.io.Serializable
{
  private String origin;//legs origin
  private String destination;//legs destination
  
  private ArrayList rates;//it holds the list of QuoteFreightRSRCSRDOBs for this leg
  private String[] slabWeightBreaks;//list of slab weight breaks
  private String[] listWeightBreaks;//list of list weight breaks
  
  //it holds index of the sell rate DOb in the rates arraylist
  //that is going to be selected for the quote or the one that is already selected.
  private Integer[] selectedFreightSellRateIndex;
  
  private ArrayList freightChargesList;//to store different freight charges List of Dobs
  
  private int[] selectedFreightChargesListIndices;//to get the indices in the corresponding freight Charges List which are selected for the quote
  
  private Hashtable spotRateDetails;//used to store the spot rates if any defined for the leg
  private boolean spotRatesFlag;//falg for spot rates if spot rates are defined then true else false
  private int spotrateSurchargeCount;
  private String spotRatesType;//whether the given spot rates are FLAT or SLAB type
  private ArrayList weightBreaks;
  private int shipmentMode;
  private double marginLimit;
  private boolean marginFlag;
  private String uom;
  private String serviceLevel;
  private String densityRatio;
  private double discountLimit;
  private boolean tiedCustInfoFlag;
  private boolean forwardFlag;
  private int legSerialNo;
  private String Currency;
  //@@ added by govind for the cr multi - lane 
  private String[] chargeGroupIds;
  private ArrayList originChargesList;//to store different origin charges List of Dobs
  private ArrayList destChargesList;//to store different destination charges List of Dobs
  private String[] originChargesSelectedFlag;
  private String[] destChargesSelectedFlag;
  private int[] selectedOriginChargesListIndices;//to get the indices in the corresponding Origin Charges List which are selected for the quote
  private int[] selctedDestChargesListIndices;//to get the indices in the corresponding Dest Charges List which are selected for the quote
  private int multiQuote_SerialNo;
  ////////////////////////////////////////////////
  private ArrayList pickUpCartageRatesList;
  private String delCartageRatesList;
  private ArrayList deliveryCartageRatesList;//Added by Sanjay 
  private ArrayList pickUpZoneZipMap;
  private ArrayList delZoneZipMap;
  private HashMap pickZoneZipMap;//Added by Sanjay 
  private HashMap deliveryZoneZipMap;//Added by Sanjay 
  
  private double cartageDiscount;
  private double originChargesMargin;
  private double destChargesMargin;
  private double originChargesDiscount;
  private double destChargesDiscount;
  private ArrayList deliveryWeightBreaks;
  private ArrayList pickupWeightBreaks;
 //@@ govind end
  private String[] containerTypes;   //added by VLAKSHMI for issue 146968 on 5/12/2008                                    
   private String consoleType;  //@@Added by Kameswari for the WPBN issue-179373 on 13/08/09
   
   private String[] flatWeightBreaks;//Added for 219973
   private String[] rateDescriptions;//Added for 219973
   private String orgFullName;//Added by Rakesh on 24-03-2011
   private String destFullName;//Added by Rakesh on 24-03-2011
  
 //Added by Anil.k for Spot Rates
   private String carrier;
   private String frequency;
   private String transitTime;
   private ArrayList spotRateDescription;
   private ArrayList  surchargeId;    
   private ArrayList  chargeRateIndicator;
   private LinkedHashSet weightBreak;
   private LinkedHashSet surCurrency;
   private ArrayList checkedFlag;   
   private ArrayList marginType;
   private ArrayList marginValue;
 //Ended by Anil.k for Spot Rates
 //Added by Anil.k for Spot Rates
   private ArrayList pickupChargeBasisList;
   private ArrayList delChargeBasisList;
 public ArrayList getSurchargeId() {
	return surchargeId;
}

public void setSurchargeId(ArrayList surchargeId) {
	this.surchargeId = surchargeId;
}

public ArrayList getChargeRateIndicator() {
	return chargeRateIndicator;
}

public void setChargeRateIndicator(ArrayList chargeRateIndicator) {
	this.chargeRateIndicator = chargeRateIndicator;
}
//Ended by Anil.k for Spot Rates
//Added for 219973
 //Added for 219973
   public String[] getRateDescriptions() {
	return rateDescriptions;
}

public void setRateDescriptions(String[] rateDescriptions) {
	this.rateDescriptions = rateDescriptions;
}
//Added for 219973
public MultiQuoteFreightLegSellRates()
  {
    //this.selectedFreightSellRateIndex=-1;
  }

  public String getOrigin()
  {
    return origin;
  }

  public void setOrigin(String origin)
  {
    this.origin = origin;
  }

  public String getDestination()
  {
    return destination;
  }

  public void setDestination(String destination)
  {
    this.destination = destination;
  }

  public ArrayList getRates()
  {
    return rates;
  }

  public void setRates(ArrayList rates)
  {
    this.rates = rates;
  }

  public String[] getSlabWeightBreaks()
  {
    return slabWeightBreaks;
  }

  public void setSlabWeightBreaks(String[] slabWeightBreaks)
  {
    this.slabWeightBreaks = slabWeightBreaks;
  }

  public String[] getListWeightBreaks()
  {
    return listWeightBreaks;
  }

  public void setListWeightBreaks(String[] listWeightBreaks)
  {
    this.listWeightBreaks = listWeightBreaks;
  }

  public Integer[] getSelectedFreightSellRateIndex()
  {
    return selectedFreightSellRateIndex;
  }

  public void setSelectedFreightSellRateIndex(Integer[] selectedFreightSellRateIndex)
  {
    this.selectedFreightSellRateIndex = selectedFreightSellRateIndex;
  }

  public ArrayList getFreightChargesList()
  {
    return freightChargesList;
  }

  public void setFreightChargesList(ArrayList freightChargesList)
  {
    this.freightChargesList = freightChargesList;
  }

  public int[] getSelectedFreightChargesListIndices()
  {
    return selectedFreightChargesListIndices;
  }

  public void setSelectedFreightChargesListIndices(int[] selectedFreightChargesListIndices)
  {
    this.selectedFreightChargesListIndices = selectedFreightChargesListIndices;
  }

  public Hashtable getSpotRateDetails()
  {
    return spotRateDetails;
  }

  public void setSpotRateDetails(Hashtable spotRateDetails)
  {
    this.spotRateDetails = spotRateDetails;
  }

  public boolean isSpotRatesFlag()
  {
    return spotRatesFlag;
  }

  public void setSpotRatesFlag(boolean spotRatesFlag)
  {
    this.spotRatesFlag = spotRatesFlag;
  }

  public String getSpotRatesType()
  {
    return spotRatesType;
  }

  public void setSpotRatesType(String spotRatesType)
  {
    this.spotRatesType = spotRatesType;
  }

  public ArrayList getWeightBreaks()
  {
    return weightBreaks;
  }

  public void setWeightBreaks(ArrayList weightBreaks)
  {
    this.weightBreaks = weightBreaks;
  }

  public int getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(int shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }

  public double getMarginLimit()
  {
    return marginLimit;
  }

  public void setMarginLimit(double marginLimit)
  {
    this.marginLimit = marginLimit;
  }

  public boolean isMarginFlag()
  {
    return marginFlag;
  }

  public void setMarginFlag(boolean marginFlag)
  {
    this.marginFlag = marginFlag;
  }

  public String getUom()
  {
    return uom;
  }

  public void setUom(String uom)
  {
    this.uom = uom;
  }

  public String getServiceLevel()
  {
    return serviceLevel;
  }

  public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel = serviceLevel;
  }

  public String getDensityRatio()
  {
    return densityRatio;
  }

  public void setDensityRatio(String densityRatio)
  {
    this.densityRatio = densityRatio;
  }

  public double getDiscountLimit()
  {
    return discountLimit;
  }

  public void setDiscountLimit(double discountLimit)
  {
    this.discountLimit = discountLimit;
  }

  public boolean isTiedCustInfoFlag()
  {
    return tiedCustInfoFlag;
  }

  public void setTiedCustInfoFlag(boolean tiedCustInfoFlag)
  {
    this.tiedCustInfoFlag = tiedCustInfoFlag;
  }

  public boolean isForwardFlag()
  {
    return forwardFlag;
  }

  public void setForwardFlag(boolean forwardFlag)
  {
    this.forwardFlag = forwardFlag;
  }

  public int getLegSerialNo()
  {
    return legSerialNo;
  }

  public void setLegSerialNo(int legSerialNo)
  {
    this.legSerialNo = legSerialNo;
  }

  public String getCurrency()
  {
    return Currency;
  }

  public void setCurrency(String Currency)
  {
    this.Currency = Currency;
  }


  public void setContainerTypes(String[] containerTypes)
  {
    this.containerTypes = containerTypes;
  }


  public String[] getContainerTypes()
  {
    return containerTypes;
  }
  //@@Added by Kameswari for the WPBN issue-179373 on 13/08/09
    public String getConsoleType()
  {
    return consoleType;
  }

  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }
//@@WPBN issue-179373

//Added for 219973
public String[] getFlatWeightBreaks() {
	return flatWeightBreaks;
}

public void setFlatWeightBreaks(String[] flatWeightBreaks) {
	this.flatWeightBreaks = flatWeightBreaks;
}
//Added for 219973

public String[] getChargeGroupIds() {
	return chargeGroupIds;
}

public void setChargeGroupIds(String[] chargeGroupIds) {
	this.chargeGroupIds = chargeGroupIds;
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



public double getCartageDiscount()
{
  return cartageDiscount;
}
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

public String getDelCartageRatesList() {
	return delCartageRatesList;
}

public void setDelCartageRatesList(String delCartageRatesList) {
	this.delCartageRatesList = delCartageRatesList;
}

public ArrayList getDeliveryCartageRatesList() {
	return deliveryCartageRatesList;
}

public void setDeliveryCartageRatesList(ArrayList deliveryCartageRatesList) {
	this.deliveryCartageRatesList = deliveryCartageRatesList;
}

public HashMap getDeliveryZoneZipMap() {
	return deliveryZoneZipMap;
}

public void setDeliveryZoneZipMap(HashMap deliveryZoneZipMap) {
	this.deliveryZoneZipMap = deliveryZoneZipMap;
}

public ArrayList getDelZoneZipMap() {
	return delZoneZipMap;
}

public void setDelZoneZipMap(ArrayList delZoneZipMap) {
	this.delZoneZipMap = delZoneZipMap;
}

public ArrayList getPickUpCartageRatesList() {
	return pickUpCartageRatesList;
}

public void setPickUpCartageRatesList(ArrayList pickUpCartageRatesList) {
	this.pickUpCartageRatesList = pickUpCartageRatesList;
}

public ArrayList getPickUpZoneZipMap() {
	return pickUpZoneZipMap;
}

public void setPickUpZoneZipMap(ArrayList pickUpZoneZipMap) {
	this.pickUpZoneZipMap = pickUpZoneZipMap;
}

public HashMap getPickZoneZipMap() {
	return pickZoneZipMap;
}

public void setPickZoneZipMap(HashMap pickZoneZipMap) {
	this.pickZoneZipMap = pickZoneZipMap;
}

public void setCartageDiscount(double cartageDiscount) {
	this.cartageDiscount = cartageDiscount;
}

public ArrayList getDeliveryWeightBreaks() {
	return deliveryWeightBreaks;
}

public void setDeliveryWeightBreaks(ArrayList deliveryWeightBreaks) {
	this.deliveryWeightBreaks = deliveryWeightBreaks;
}

public ArrayList getPickupWeightBreaks() {
	return pickupWeightBreaks;
}

public void setPickupWeightBreaks(ArrayList pickupWeightBreaks) {
	this.pickupWeightBreaks = pickupWeightBreaks;
}

public String getOrgFullName() {
	return orgFullName;
}

public void setOrgFullName(String orgFullName) {
	this.orgFullName = orgFullName;
}

public String getDestFullName() {
	return destFullName;
}

public void setDestFullName(String destFullName) {
	this.destFullName = destFullName;
}

//Added by Anil.k for Spot Rates
public String getCarrier() {
	return carrier;
}

public void setCarrier(String carrier) {
	this.carrier = carrier;
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

public ArrayList getSpotRateDescription() {
	return spotRateDescription;
}

public void setSpotRateDescription(ArrayList spotRateDescription) {
	this.spotRateDescription = spotRateDescription;
}

public LinkedHashSet getWeightBreak() {
	return weightBreak;
}

public void setWeightBreak(LinkedHashSet weightBreak) {
	this.weightBreak = weightBreak;
}

public LinkedHashSet getSurCurrency() {
	return surCurrency;
}

public void setSurCurrency(LinkedHashSet surCurrency) {
	this.surCurrency = surCurrency;
}

public ArrayList getCheckedFlag() {
	return checkedFlag;
}

public void setCheckedFlag(ArrayList checkedFlag) {
	this.checkedFlag = checkedFlag;
}

public ArrayList getMarginType() {
	return marginType;
}

public void setMarginType(ArrayList marginType) {
	this.marginType = marginType;
}

public ArrayList getMarginValue() {
	return marginValue;
}

public void setMarginValue(ArrayList marginValue) {
	this.marginValue = marginValue;
}
//Ended by Anil.k for Spot Rates

public int getMultiQuote_SerialNo() {
	return multiQuote_SerialNo;
}

public void setMultiQuote_SerialNo(int multiQuote_SerialNo) {
	this.multiQuote_SerialNo = multiQuote_SerialNo;
}

public int getSpotrateSurchargeCount() {
	return spotrateSurchargeCount;
}

public void setSpotrateSurchargeCount(int spotrateSurchargeCount) {
	this.spotrateSurchargeCount = spotrateSurchargeCount;
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
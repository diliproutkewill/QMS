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

  Programme Name		  :QuoteFreightLegSellRates.java
  Module  Name  		  :Quote.
  Task           		  :         
  SubTask       		  :   
  Author Name         :S Anil Kumar.
  Date Started        :
  Date Finished       :3ist Aug 2005 
  Date Modified       :
  Description         :
  Method's Summary	  :

  */

package com.qms.operations.quote.dob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class QuoteFreightLegSellRates implements java.io.Serializable
{
  private String origin;//legs origin
  private String destination;//legs destination
  
  private ArrayList rates;//it holds the list of QuoteFreightRSRCSRDOBs for this leg
  private String[] slabWeightBreaks;//list of slab weight breaks
  private String[] listWeightBreaks;//list of list weight breaks
  
  //it holds index of the sell rate DOb in the rates arraylist
  //that is going to be selected for the quote or the one that is already selected.
  private int selectedFreightSellRateIndex;
  
  private ArrayList freightChargesList;//to store different freight charges List of Dobs
  
  private int[] selectedFreightChargesListIndices;//to get the indices in the corresponding freight Charges List which are selected for the quote
  
  private Hashtable spotRateDetails;//used to store the spot rates if any defined for the leg
  private boolean spotRatesFlag;//falg for spot rates if spot rates are defined then true else false
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
  private String[] containerTypes;   //added by VLAKSHMI for issue 146968 on 5/12/2008                                    
   private String consoleType;  //@@Added by Kameswari for the WPBN issue-179373 on 13/08/09
   
   private String[] flatWeightBreaks;//Added for 219973
   private String[] rateDescriptions;//Added for 219973
 //Added for 219973
   public String[] getRateDescriptions() {
	return rateDescriptions;
}

public void setRateDescriptions(String[] rateDescriptions) {
	this.rateDescriptions = rateDescriptions;
}
//Added for 219973
public QuoteFreightLegSellRates()
  {
    this.selectedFreightSellRateIndex=-1;
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

  public int getSelectedFreightSellRateIndex()
  {
    return selectedFreightSellRateIndex;
  }

  public void setSelectedFreightSellRateIndex(int selectedFreightSellRateIndex)
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
}
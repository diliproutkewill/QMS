

/**
 * @ (#) QMSSellRatesDOB.java
 * Copyright (c) 2001 The Four Soft Pvt Ltd., 
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */
/**
 * File       : QMSSellRatesDOB.java
 * Sub-Module : 
 * Module     : QMS
 * @author    : Madhu.Y,Yuvraj Waghray
 * * @date    : 29-07-2005
 * Modified by      Date     Reason
 */

package com.qms.operations.sellrates.java;

import java.sql.Timestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class QMSSellRatesDOB implements Serializable
{
  private String shipmentMode;
  private String weightBreak;
  private String rateType;
  private String currencyId;
  private String weightClass;
  private String origin;
  private String originCountry;
  private String destination;
  private String destinationCountry;
  private String[] carriers;
  private String serviceLevel;
  private int rec_cons_id;
  private String rec_cons_flag;
  private String overAllMargin;
  private String marginType;
  private String marginBasis;
  private String createdBy;
  private Timestamp createdTimestamp;
  private String lastUpdatedBy;
  private Timestamp lastUpdatedTimestamp;
  private String activeInactiveFlag;
  private String accessLevel;
  private String terminalId;
  private String consoleType;
  private String carrier_id;
  private String[] serviceLevel_Id;
  private double minimumRate;
  private double flatRate;
  private double palletCapacity;
  private double palletBuyRate;
  private double averageUplift;
  private double loseSpace;
  private long upperBound;
  private long lowerBound;
  private double csr_sellRate;
  private double marginPer;
  private int margin_per;
  private String notes;
  //@@Added by Yuvraj
  private String buyRateId;
  private double chargeRate;
  //@@Yuvraj
  private HashMap sellRates;
  private HashMap surchargesIds;//Added by Govind for the CR-219973
  private String weightBreakSlab;
  private String lowerBoundString;
  private String upperBoundString;
  private String frequency;//@@Yuvraj
  private int laneNumber;
  private int lineNumber;
  private String chargeRateInd;
  private String transitTime;
  private String operation;
  private ArrayList list;
  private String noteValue;
  private double    abpersentWithBasic;// @@ Added by govind for the issue RSR view
  private double    abpersentWithMin;
  private double    abpersentWithFlat;
  private String minFlat;
  private int lanNumber;
  private double lowerBd;//Modified by Mohan for issue no. on 12112010;
  private double upperBd;//Modified by Mohan for issue no. on 12112010
  private String[] mOrigin;
  private String[] mOriginCut;
  private String[] mDestination;
  private String[] mDestinationCut;
  private String[] mTransitTime;
  private String[] mFrequency;
  private String[]  mMinimumRates;
  private String[]  mFlatRates;
  private String[]  marign;
  private double minimumRates;
  private double flatRates;
  private String[] buyRateIds;
  private String[] lanNumbers;
  private String[] minimumRateArray;
  private String[] flatRateArray;
  private String[] notesArray;
  private String invalidate;
  private double buyRate;
  private Timestamp effectiveFrom;
  private Timestamp validUpto;
  private Timestamp fromdate;
  private Timestamp toDate; 
  private String[] weightBreaks;
  private String[] chargeRates;
  private double[] chargeRateValues;
  private double[]  sameOvrMargin;
  private String  chargerateIndicator;
  private String[]  weightAllBreaks;
  private String[] lBound;
  private String[] uBound;
  private String[]  chargeInr;
  private double[] buyChargeRates;
  private String[] buyChrRates;
  private String   densityRatio;
  private String[]  marginValues;
  private String    pageNo;

  private String    rec_buyrate_id;
  private String uom;
  private long sellRateId;
  private String loginTerminalId;
  private String sortBy;
  private String sortOrder;
  private String[] rateDescription;
  private String[] surChargeCurency; // Added By Gowtham For SurCharge Currency View Issue 
  private double abperWithSurcharge;//@@Added by Kameswari for Surcharge Enhancements
  private String versionNo;
 //added by phani sekhar for wpbn 171213 on 20090605  
  private String originRegions;
  private String destRegions;
  //ends phani 171213
  private String extNotes;//Added by Mohan Gajjala for Issue No.219976
    private String surChargeDescription;//Added by Subbu
  public String getExtNotes() {
	return extNotes;
}

public void setExtNotes(String extNotes) {
	this.extNotes = extNotes;
}

public QMSSellRatesDOB()
  {
  }

  //@@Getter Methods 
  public String getRec_buyrate_id()           {    return rec_buyrate_id;         }
  public String getPageNo()                   {    return pageNo;                 }
  public String getInvalidate()               {    return invalidate;             }
  public String getShipmentMode()             {    return shipmentMode;           }
  public String getWeightBreak()              {    return weightBreak;            }
  public String getRateType()                 {    return rateType;               }
  public String getCurrencyId()               {    return currencyId;             }
  public String getWeightClass()              {    return weightClass;            }
  public String getOrigin()                   {    return origin;                 }
  public String getOriginCountry()            {    return originCountry;          }
  public String getDestination()              {    return destination;            }
  public String getDestinationCountry()       {    return destinationCountry;     }
  public String[] getCarriers()               {    return carriers;               }
  public int getRec_cons_id()                 {    return rec_cons_id;            }
  public String getServiceLevel()             {    return serviceLevel;           }
  public String getRec_cons_flag()            {    return rec_cons_flag;          }
  public String getOverAllMargin()            {    return overAllMargin;          }
  public String getMarginType()               {    return marginType;             }
  public String getMarginBasis()              {    return marginBasis;            }
  public String getCreatedBy()                {    return createdBy;              }
  public Timestamp getCreatedTimestamp()      {    return createdTimestamp;       }
  public String getLastUpdatedBy()            {    return lastUpdatedBy;          }
  public Timestamp getLastUpdatedTimestamp()  {    return lastUpdatedTimestamp;   }
  public String getActiveInactiveFlag()       {    return activeInactiveFlag;     }
  public String getAccessLevel()              {    return accessLevel;            }
  public String getTerminalId()               {    return terminalId;             }
  public String getConsoleType()              {    return consoleType;            }
  public String getCarrier_id()               {    return carrier_id;             }
  public String[] getServiceLevel_Id()        {    return serviceLevel_Id;        }
  public double getMinimumRate()              {    return minimumRate;            }
  public double getFlatRate()                 {    return flatRate;               }
  public double getPalletCapacity()           {    return palletCapacity;         }
  public double getPalletBuyRate()            {    return palletBuyRate;          }
  public double getAverageUplift()            {    return averageUplift;          }
  public double getLoseSpace()                {    return loseSpace;              }
  public long getUpperBound()                 {    return upperBound;             }
  public long getLowerBound()                 {    return lowerBound;             }
  public double getCsr_sellRate()             {    return csr_sellRate;           }
  public double getMarginPer()                {    return marginPer;              }
  public int getMargin_per()                  {    return margin_per;             }
  public String getNotes()                    {    return notes;                  }
  public HashMap getSellRates()               {    return sellRates;              }
  public String getBuyRateId()                {    return buyRateId;              }//@@Added by Yuvraj
  public ArrayList getBoundryList()           {    return list;                   }
  public String getNoteValue()                {    return noteValue;              }
  public String[] getMminimumRates()          {    return mMinimumRates;          }
  public String[] getMflatRates()             {    return mFlatRates;             }
  public String[] getMarign()                 {    return marign;                 }
  public double getMinimumRates()             {    return minimumRates;           }
  public double getFlatRates()                {    return flatRates;              }
  public Timestamp getEffectiveFrom()         {    return this.effectiveFrom;     }
  public Timestamp getValidUpto()             {    return this.validUpto;         }
  
  public String[] getWeightBreaks()           {    return weightBreaks;           }
  public String[] getChargeRates()            {    return chargeRates;            }
  public double[] getChargeRatesValues()      {    return chargeRateValues;       }
  public double[] getSameOvrMargin()          {    return sameOvrMargin;          }
  public String[] getAllWeightBreaks()        {    return weightAllBreaks;        }
  
  public String[] getLBound()                 {    return lBound;                 }
  public String[] getUBound()                 {    return uBound;                 }
  public String[] getChargeInr()              {    return chargeInr;              }

  public String[] getBuyChrRates()		        {    return buyChrRates;            }
  public double[] getBuyChargeRates()		      {    return buyChargeRates;         }
  public String getDensityRatio()             {    return this.densityRatio;      }

  //public double[] getBuyChargeRates()		      {    return buyChargeRates;         }
  public String[] getMarginValues()		        {    return marginValues;           }


  
  public void setDensityRatio(String densityRatio)
  {
    this.densityRatio = densityRatio;
  }

 //@@End 
 //@@End 
 public void setRec_buyrate_id(String rec_buyrate_id)                   
 {    
    this.rec_buyrate_id=rec_buyrate_id;                 
 }
 public void setPageNo(String pageNo)                   
 {    
    this.pageNo=pageNo;                 
 }
 public void setMarginValues(String[] marginValues)        
 {    
    this.marginValues=marginValues;           
 }
 public void setBuyChrRates(String[] buyChrRates)        
 {    
    this.buyChrRates=buyChrRates;           
 }
 public void setBuyChargeRates(double[] buyChargeRates)        
 {    
    this.buyChargeRates=buyChargeRates;           
 }
 /*
  * Setter Method
  * Parameter String invalidate
 */
  public void setEffectiveFrom(Timestamp effectiveFrom)
  {
    this.effectiveFrom = effectiveFrom;
  }
 /*
  * Setter Method
  * Parameter String shipmentMode
 */
  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto = validUpto;
  }
 /*
  * Setter Method
  * Parameter String invalidate
 */
  public void setInvalidate(String invalidate)
  {
    this.invalidate = invalidate;
  }
 /*
  * Setter Method
  * Parameter String shipmentMode
 */
  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }
 /*
  * Setter Method
  * Parameter String weightBreak
 */
  public void setWeightBreak(String weightBreak)
  {
    this.weightBreak = weightBreak;
  }
 /*
  * Setter Method
  * Parameter String rateType
 */
  public void setRateType(String rateType)
  {
    this.rateType = rateType;
  }
 /*
  * Setter Method
  * Parameter String currencyId
 */
  public void setCurrencyId(String currencyId)
  {
    this.currencyId = currencyId;
  }
 /*
  * Setter Method
  * Parameter String weightClass
 */
  public void setWeightClass(String weightClass)
  {
    this.weightClass = weightClass;
  }
 /*
  * Setter Method
  * Parameter String origin
 */
  public void setOrigin(String origin)
  {
    this.origin = origin;
  }
 /*
  * Setter Method
  * Parameter String originCountry
 */
  public void setOriginCountry(String originCountry)
  {
    this.originCountry = originCountry;
  }
 /*
  * Setter Method
  * Parameter String destination
 */
  public void setDestination(String destination)
  {
    this.destination = destination;
  }
 /*
  * Setter Method
  * Parameter String destinationCountry
 */
  public void setDestinationCountry(String destinationCountry)
  {
    this.destinationCountry = destinationCountry;
  }
 /*
  * Setter Method
  * Parameter String[] carriers
 */
  public void setCarriers(String[] carriers)
  {
    this.carriers = carriers;
  }
 /*
  * Setter Method
  * Parameter String serviceLevel
 */
  public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel = serviceLevel;
  }
 /*
  * Setter Method
  * Parameter int rec_cons_id
 */
  public void setRec_cons_id(int rec_cons_id)
  {
    this.rec_cons_id = rec_cons_id;
  }
 /*
  * Setter Method
  * Parameter String rec_cons_flag
 */
  public void setRec_cons_flag(String rec_cons_flag)
  {
    this.rec_cons_flag = rec_cons_flag;
  }
 /*
  * Setter Method
  * Parameter String overAllMargin
  */
  public void setOverAllMargin(String overAllMargin)
  {
    this.overAllMargin = overAllMargin;
  }
 /*
  * Setter Method
  * Parameter String marginType
  */
  public void setMarginType(String marginType)
  {
    this.marginType = marginType;
  }
 /*
  * Setter Method
  * Parameter String marginBasis
  */
  public void setMarginBasis(String marginBasis)
  {
    this.marginBasis = marginBasis;
  }
 /*
  * Setter Method
  * Parameter String createdBy
  */
  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }
 /*
  * Setter Method
  * Parameter Timestamp createdTimestamp
  */
  public void setCreatedTimestamp(Timestamp createdTimestamp)
  {
    this.createdTimestamp = createdTimestamp;
  }
 /*
  * Setter Method
  * Parameter String lastUpdatedBy
  */
  public void setLastUpdatedBy(String lastUpdatedBy)
  {
    this.lastUpdatedBy = lastUpdatedBy;
  }
 /*
  * Setter Method
  * Parameter Timestamp lastUpdatedTimestamp
  */
  public void setLastUpdatedTimestamp(Timestamp lastUpdatedTimestamp)
  {
    this.lastUpdatedTimestamp = lastUpdatedTimestamp;
  }
 /*
  * Setter Method
  * Parameter String activeInactiveFlag
  */
  public void setActiveInactiveFlag(String activeInactiveFlag)
  {
    this.activeInactiveFlag = activeInactiveFlag;
  }
 /*
  * Setter Method
  * Parameter String accessLevel
  */
  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }
 /*
  * Setter Method
  * Parameter String terminalId
  */
  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }
 /*
  * Setter Method
  * Parameter String consoleType
  */
  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }
 /*
  * Setter Method
  * Parameter String carrier_id
  */
  public void setCarrier_id(String carrier_id)
  {
    this.carrier_id = carrier_id;
  }
 /*
  * Setter Method
  * Parameter String[] serviceLevel_Id
  */
  public void setServiceLevel_Id(String[] serviceLevel_Id)
  {
    this.serviceLevel_Id = serviceLevel_Id;
  }
 /*
  * Setter Method
  * Parameter double[] minimumRate
  */
  public void setMinimumRate(double minimumRate)
  {
    this.minimumRate = minimumRate;
  }
 /*
  * Setter Method
  * Parameter double[] flatRate
  */
  public void setFlatRate(double flatRate)
  {
    this.flatRate = flatRate;
  }
 /*
  * Setter Method
  * Parameter double[] palletCapacity
  */
  public void setPalletCapacity(double palletCapacity)
  {
    this.palletCapacity = palletCapacity;
  }
 /*
  * Setter Method
  * Parameter double[] palletBuyRate
  */
  public void setPalletBuyRate(double palletBuyRate)
  {
    this.palletBuyRate = palletBuyRate;
  }
 /*
  * Setter Method
  * Parameter double[] averageUplift
  */
  public void setAverageUplift(double averageUplift)
  {
    this.averageUplift = averageUplift;
  }
 /*
  * Setter Method
  * Parameter double[] loseSpace
  */
  public void setLoseSpace(double loseSpace)
  {
    this.loseSpace = loseSpace;
  }
 /*
  * Setter Method
  * Parameter int[] upperBound
  */
  public void setUpperBound(long upperBound)
  {
    this.upperBound = upperBound;
  }
 /*
  * Setter Method
  * Parameter int[] lowerBound
  */
  public void setLowerBound(long lowerBound)
  {
    this.lowerBound = lowerBound;
  }
 /*
  * Setter Method
  * Parameter double[] csr_sellRate
  */
  public void setCsr_sellRate(double csr_sellRate)
  {
    this.csr_sellRate = csr_sellRate;
  }
 /*
  * Setter Method
  * Parameter int marginPer
  */
  public void setMarginPer(double marginPer)
  {
    this.marginPer = marginPer;
  }
 /*
  * Setter Method
  * Parameter int[] margin_per
  */
  public void setMargin_per(int margin_per)
  {
    this.margin_per = margin_per;
  }
 /*
  * Setter Method
  * Parameter iString[] notes
  */
  public void setNotes(String notes)
  {
    this.notes = notes;
  }
  public void setSellRates(HashMap sellRates)             
  {    
    this.sellRates = sellRates;              
  }
  /*
  * Setter Method
  * Parameter ArrayList list
  */
  public void setBoundryList(ArrayList list)
  {
    this.list = list;
  }
  /*
  * Setter Method
  * Parameter String[] marign
  */
  public void setMarign(String[] marign)
  {
    this.marign = marign;
  }
   /*
  * Setter Method
  * Parameter String[] mMinimumRates
  */
  public void setMminimumRates(String[] mMinimumRates)
  {
    this.mMinimumRates = mMinimumRates;
  }
  /*
  * Setter Method
  * Parameter String[] mFlatRates
  */
  public void setMflatRates(String[] mFlatRates)
  {
    this.mFlatRates = mFlatRates;
  }
  /*
  * Setter Method
  * Parameter double minimumRates
  */
  public void setMinimumRates(double minimumRates)
  {
    this.minimumRates = minimumRates;
  }
 /*
  * Setter Method
  * Parameter double flatRates
  */
  public void setFlatRates(double flatRates)
  {
    this.flatRates = flatRates;
  }
  //@@Added by Yuvraj
  /*
  * Setter Method
  * Parameter String buyRateId
  */
  /*
  * Setter Method
  * Parameter String notes
  */
  public void setNoteValue(String noteValue)
  {
    this.noteValue = noteValue;
  }
  
  public void setBuyRateId(String buyRateId)
  {
    this.buyRateId = buyRateId;
  }

  public double getChargeRate()
  {
    return chargeRate;
  }

  public void setChargeRate(double chargeRate)
  {
    this.chargeRate = chargeRate;
  }

  public String getWeightBreakSlab()
  {
    return weightBreakSlab;
  }

  public void setWeightBreakSlab(String weightBreakSlab)
  {
    this.weightBreakSlab = weightBreakSlab;
  }

  public String getLowerBoundString()
  {
    return lowerBoundString;
  }

  public void setLowerBoundString(String lowerBoundString)
  {
    this.lowerBoundString = lowerBoundString;
  }

  public String getUpperBoundString()
  {
    return upperBoundString;
  }

  public void setUpperBoundString(String upperBoundString)
  {
    this.upperBoundString = upperBoundString;
  }

  public String getFrequency()
  {
    return frequency;
  }

  public void setFrequency(String frequency)
  {
    this.frequency = frequency;
  }

  public int getLaneNumber()
  {
    return laneNumber;
  }

  public void setLaneNumber(int laneNumber)
  {
    this.laneNumber = laneNumber;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber)
  {
    this.lineNumber = lineNumber;
  }

  public String getChargeRateInd()
  {
    return chargeRateInd;
  }

  public void setChargeRateInd(String chargeRateInd)
  {
    this.chargeRateInd = chargeRateInd;
  }

  public String getTransitTime()
  {
    return transitTime;
  }

  public void setTransitTime(String transitTime)
  {
    this.transitTime = transitTime;
  }

  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }
//@@Yuvraj
    public String getMinFlat()
    {
        return minFlat;
    }

    public void setMinFlat(String minFlat)
    {
        this.minFlat = minFlat;
    }

    public double getAbpersentWithFlat()
    {
        return abpersentWithFlat;
    }

    public void setAbpersentWithFlat(double abpersentWithFlat)
    {
        this.abpersentWithFlat = abpersentWithFlat;
    }

    public double getAbpersentWithMin()
    {
        return abpersentWithMin;
    }

    public void setAbpersentWithMin(double abpersentWithMin)
    {
        this.abpersentWithMin = abpersentWithMin;
    }

    public int getLanNumber()
    {
        return lanNumber;
    }

    public void setLanNumber(int lanNumber)
    {
        this.lanNumber = lanNumber;
    }
//Modified by Mohan for issue no. on 12112010
    public double getLowerBd()
    {
        return lowerBd;
    }

    public void setLowerBd(double lowerBd)
    {
        this.lowerBd = lowerBd;
    }

    public double getUpperBd()
    {
        return upperBd;
    }

    public void setUpperBd(double upperBd)
    {
        this.upperBd = upperBd;
    }

    public String[] getMDestination()
    {
        return mDestination;
    }

    public void setMDestination(String[] mDestination)
    {
        this.mDestination = mDestination;
    }

    public String[] getMDestinationCut()
    {
        return mDestinationCut;
    }

    public void setMDestinationCut(String[] mDestinationCut)
    {
        this.mDestinationCut = mDestinationCut;
    }

    public String[] getMFrequency()
    {
        return mFrequency;
    }

    public void setMFrequency(String[] mFrequency)
    {
        this.mFrequency = mFrequency;
    }

    public String[] getMOrigin()
    {
        return mOrigin;
    }

    public void setMOrigin(String[] mOrigin)
    {
        this.mOrigin = mOrigin;
    }

    public String[] getMOriginCut()
    {
        return mOriginCut;
    }

    public void setMOriginCut(String[] mOriginCut)
    {
        this.mOriginCut = mOriginCut;
    }

    public String[] getMTransitTime()
    {
        return mTransitTime;
    }

    public void setMTransitTime(String[] mTransitTime)
    {
        this.mTransitTime = mTransitTime;
    }

    public String[] getBuyRateIds()
    {
        return buyRateIds;
    }

    public void setBuyRateIds(String[] buyRateIds)
    {
        this.buyRateIds = buyRateIds;
    }

    public String[] getLanNumbers()
    {
        return lanNumbers;
    }

    public void setLanNumbers(String[] lanNumbers)
    {
        this.lanNumbers = lanNumbers;
    }

    public String[] getMinimumRateArray()
    {
        return minimumRateArray;
    }

    public void setMinimumRateArray(String[] minimumRateArray)
    {
        this.minimumRateArray = minimumRateArray;
    }

    public String[] getFlatRateArray()
    {
        return flatRateArray;
    }

    public void setFlatRateArray(String[] flatRateArray)
    {
        this.flatRateArray = flatRateArray;
    }

    public String[] getNotesArray()
    {
        return notesArray;
    }

    public void setNotesArray(String[] notesArray)
    {
        this.notesArray = notesArray;
    }
//@@End

public double getBuyRate()
  {
    return buyRate;
  }

  public void setBuyRate(double buyRate)
  {
    this.buyRate = buyRate;
  }
  
  public void setWeightBreaks(String[] weightBreaks)
  {
    this.weightBreaks =  weightBreaks;
  }
  public void setChargeRates(String[] chargeRates)
  {
    this.chargeRates  = chargeRates;
  }
  public void setChargeRatesValues(double[] chargeRateValues)
  {
    this.chargeRateValues  = chargeRateValues;
  }
  public void setChargeRateValues(double[] chargeRateValues)
  {
    this.chargeRateValues  = chargeRateValues;
  }  
   /*
  * Setter Method
  * Parameter double chargeRate
  */
  public double[] getChargeRateValues()
  {
    return chargeRateValues;
  }
  public void setSameOvrMargin(double[] sameOvrMargin)
  {
    this.sameOvrMargin = sameOvrMargin;
  }
   public void setChargerateIndicator(String chargerateIndicator)
  {
    this.chargerateIndicator = chargerateIndicator;
  }
  public String getChargerateIndicator()
  {
    return chargerateIndicator;
  }
  public void setAllWeightBreaks(String[] weightAllBreaks)
  {
    this.weightAllBreaks  = weightAllBreaks;
  }

  public String[] getWeightAllBreaks()
  {
    return weightAllBreaks;
  }

  public void setLBound(String[] lBound)
  {
    this.lBound  = lBound;
  }
  public void setUBound(String[] uBound)
  {
    this.uBound  = uBound;
  }
  public void setChargeInr(String[] chargeInr)
  {
    this.chargeInr  = chargeInr;
  }

  public String getUom()
  {
    return uom;
  }

  public void setUom(String uom)
  {
    this.uom = uom;
  }

  public long getSellRateId()
  {
    return sellRateId;
  }

  public void setSellRateId(long sellRateId)
  {
    this.sellRateId = sellRateId;
  }

  public String getLoginTerminalId()
  {
    return loginTerminalId;
  }

  public void setLoginTerminalId(String loginTerminalId)
  {
    this.loginTerminalId = loginTerminalId;
  }

  public String getSortBy()
  {
    return sortBy;
  }

  public void setSortBy(String sortBy)
  {
    this.sortBy = sortBy;
  }

  public String getSortOrder()
  {
    return sortOrder;
  }

  public void setSortOrder(String sortOrder)
  {
    this.sortOrder = sortOrder;
  }

  public String[] getRateDescription()
  {
    return rateDescription;
  }

  public void setRateDescription(String[] rateDescription)
  {
    this.rateDescription = rateDescription;
  }
//@@Added by Kameswari for Surcharge Enhancements
  public double getAbperWithSurcharge()
  {
    return abperWithSurcharge;
  }

  public void setAbperWithSurcharge(double abperWithSurcharge)
  {
    this.abperWithSurcharge = abperWithSurcharge;
  }

  public String getVersionNo()
  {
    return versionNo;
  }

  public void setVersionNo(String versionNo)
  {
    this.versionNo = versionNo;
  }
  
   public void setOriginRegions(String originRegions)
  {
    this.originRegions = originRegions;
  }


  public String getOriginRegions()
  {
    return originRegions;
  }


  public void setDestRegions(String destRegions)
  {
    this.destRegions = destRegions;
  }


  public String getDestRegions()
  {
    return destRegions;
  }

public String getSurChargeDescription() {
	return surChargeDescription;
}

public void setSurChargeDescription(String surChargeDescription) {
	this.surChargeDescription = surChargeDescription;
}
//Added by Govind for the CR-219973
public HashMap getSurchargesIds() {
	return surchargesIds;
}
//Added by Govind for the CR-219973
public void setSurchargesIds(HashMap surchargesIds) {
	this.surchargesIds = surchargesIds;
}
// Added By Gowtham For Surcharge Currency 
public String[] getSurChargeCurency() {
	return surChargeCurency;
}

public void setSurChargeCurency(String[] surChargeCurency) {
	this.surChargeCurency = surChargeCurency;
}

public Timestamp getFromdate() {
	return fromdate;
}

public void setFromdate(Timestamp fromdate) {
	this.fromdate = fromdate;
}

public Timestamp getToDate() {
	return toDate;
}

public void setToDate(Timestamp toDate) {
	this.toDate = toDate;
}

public double getAbpersentWithBasic() {
	return abpersentWithBasic;
}

public void setAbpersentWithBasic(double abpersentWithBasic) {
	this.abpersentWithBasic = abpersentWithBasic;
}
}
/**
 * @ (#) QMSCartageBuyDtlDOB.java
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
 * File       : QMSCartageSellDtlDOB.java
 * Sub-Module : Cartage Sell charges
 * Module     : QMS
 * @author    : Yuvraj Waghray
 * @date      : 30-08-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.java;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Timestamp;

public class QMSCartageSellDtlDOB implements java.io.Serializable
{
  private long cartageId;
  private String zoneCode;
  private String vendorId;
  private String overallMargin;
  private String marginType;
  private String marginBasis;
  private double margin;
  private double minMargin;
  private double slabMargin;
  private double maxMargin;
  private double chargeRate;
  private String chargeSlab;
  private String lowerBound;
  private String upperBound;
  private String chargeType;
  private double flatMargin;
  private String uom;
  private String currencyId;
  private double buyChargeAmount;
  private String locationId;
  private String operation;
  private String terminalId;
  private int lineNumber;
  private double minRate;
  private double flatRate;
  private double maxRate;  
  private ArrayList slabList;
  private HashMap  slabRates;
  private String chargeBasis;
  private Timestamp effectiveFrom;
  private Timestamp validUpto;
  private String chargeRateIndicator;
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
  private double baseMargin;
  private double baseRate;
//@@Ended by subrahmanyam for the Enhancement 170759 on 02/06/09  
  public long getCartageId()
  {
    return cartageId;
  }

  public void setCartageId(long cartageId)
  {
    this.cartageId = cartageId;
  }

  public String getZoneCode()
  {
    return zoneCode;
  }

  public void setZoneCode(String zoneCode)
  {
    this.zoneCode = zoneCode;
  }

  public String getVendorId()
  {
    return vendorId;
  }

  public void setVendorId(String vendorId)
  {
    this.vendorId = vendorId;
  }

  public String getOverallMargin()
  {
    return overallMargin;
  }

  public void setOverallMargin(String overallMargin)
  {
    this.overallMargin = overallMargin;
  }

  public String getMarginType()
  {
    return marginType;
  }

  public void setMarginType(String marginType)
  {
    this.marginType = marginType;
  }

  public String getMarginBasis()
  {
    return marginBasis;
  }

  public void setMarginBasis(String marginBasis)
  {
    this.marginBasis = marginBasis;
  }

  public double getMargin()
  {
    return margin;
  }

  public void setMargin(double margin)
  {
    this.margin = margin;
  }

  public double getMinMargin()
  {
    return minMargin;
  }

  public void setMinMargin(double minMargin)
  {
    this.minMargin = minMargin;
  }

  public double getSlabMargin()
  {
    return slabMargin;
  }

  public void setSlabMargin(double slabMargin)
  {
    this.slabMargin = slabMargin;
  }

  public double getMaxMargin()
  {
    return maxMargin;
  }

  public void setMaxMargin(double maxMargin)
  {
    this.maxMargin = maxMargin;
  }

  public double getChargeRate()
  {
    return chargeRate;
  }

  public void setChargeRate(double chargeRate)
  {
    this.chargeRate = chargeRate;
  }

  public String getChargeSlab()
  {
    return chargeSlab;
  }

  public void setChargeSlab(String chargeSlab)
  {
    this.chargeSlab = chargeSlab;
  }

  public String getLowerBound()
  {
    return lowerBound;
  }

  public void setLowerBound(String lowerBound)
  {
    this.lowerBound = lowerBound;
  }

  public String getUpperBound()
  {
    return upperBound;
  }

  public void setUpperBound(String upperBound)
  {
    this.upperBound = upperBound;
  }

  public String getChargeType()
  {
    return chargeType;
  }

  public void setChargeType(String chargeType)
  {
    this.chargeType = chargeType;
  }

  public double getFlatMargin()
  {
    return flatMargin;
  }

  public void setFlatMargin(double flatMargin)
  {
    this.flatMargin = flatMargin;
  }

  public String getUom()
  {
    return uom;
  }

  public void setUom(String uom)
  {
    this.uom = uom;
  }

  public String getCurrencyId()
  {
    return currencyId;
  }

  public void setCurrencyId(String currencyId)
  {
    this.currencyId = currencyId;
  }

  public double getBuyChargeAmount()
  {
    return buyChargeAmount;
  }

  public void setBuyChargeAmount(double buyChargeAmount)
  {
    this.buyChargeAmount = buyChargeAmount;
  }


  public void setLocationId(String locationId)
  {
    this.locationId = locationId;
  }


  public String getLocationId()
  {
    return locationId;
  }


  public void setOperation(String operation)
  {
    this.operation = operation;
  }


  public String getOperation()
  {
    return operation;
  }


  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }


  public String getTerminalId()
  {
    return terminalId;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber)
  {
    this.lineNumber = lineNumber;
  }


  public void setMinRate(double minRate)
  {
    this.minRate = minRate;
  }


  public double getMinRate()
  {
    return minRate;
  }


  public void setFlatRate(double flatRate)
  {
    this.flatRate = flatRate;
  }


  public double getFlatRate()
  {
    return flatRate;
  }


  public void setMaxRate(double maxRate)
  {
    this.maxRate = maxRate;
  }


  public double getMaxRate()
  {
    return maxRate;
  }


  public void setSlabList(ArrayList slabList)
  {
    this.slabList = slabList;
  }


  public ArrayList getSlabList()
  {
    return slabList;
  }


  public void setSlabRates(HashMap slabRates)
  {
    this.slabRates = slabRates;
  }


  public HashMap getSlabRates()
  {
    return slabRates;
  }

  public String getChargeBasis()
  {
    return chargeBasis;
  }

  public void setChargeBasis(String chargeBasis)
  {
    this.chargeBasis = chargeBasis;
  }

  public Timestamp getEffectiveFrom()
  {
    return effectiveFrom;
  }

  public void setEffectiveFrom(Timestamp effectiveFrom)
  {
    this.effectiveFrom = effectiveFrom;
  }

  public Timestamp getValidUpto()
  {
    return validUpto;
  }

  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto = validUpto;
  }

  public String getChargeRateIndicator()
  {
    return chargeRateIndicator;
  }

  public void setChargeRateIndicator(String chargeRateIndicator)
  {
    this.chargeRateIndicator = chargeRateIndicator;
  }
  //@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09  
  public void setBaseRate(double baseRate)
  {
      this.baseRate = baseRate;
  }
  
  
  public double getBaseRate()
  {
      return baseRate;
  }
    
  public double getBaseMargin()
  {
      return baseMargin;
  }
  
  public void setBaseMargin(double baseMargin)
  {
      this.baseMargin = baseMargin;
  }
  //@@Ended by subrahmanyam for the Enhancement 170759 on 02/06/09  
}
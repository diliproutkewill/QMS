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
 * File       : QMSCartageBuyDtlDOB.java
 * Sub-Module : Cartage Buy charges
 * Module     : QMS
 * @author    : Yuvraj Waghray
 * @date      : 30-08-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.java;
import java.sql.Timestamp;
import java.util.HashMap;

public class QMSCartageBuyDtlDOB implements java.io.Serializable
{
  private long cartageId;
  private String zoneCode;
  private String vendorId;
  private double chargeRate;
  private String weightBreakSlab;
  private String lowerBound;
  private String upperBound;
  private String chargeRateIndicator;
  private double minRate;
  private double maxRate;
  private double flatRate;
  private String chargeType;
  private Timestamp effectiveFrom;
  private Timestamp validUpto;
  private String maxChargeFlag;
  private String[] slabBreak;
  private HashMap slabRates;
  private String  densityRatio;
  private int lineNumber;
  private String chargeBasis;
  private double baseRate; //@@ Added by subrahmanyam for Enhancement 170759 on 25/May/09
  public QMSCartageBuyDtlDOB()
  {
  }
  
  public String getDensityRatio()         {    return this.densityRatio;      }
  
  public void setDensityRatio(String densityRatio)
  {
    this.densityRatio = densityRatio;
  }

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

  public String getChargeRateIndicator()
  {
    return chargeRateIndicator;
  }

  public void setChargeRateIndicator(String chargeRateIndicator)
  {
    this.chargeRateIndicator = chargeRateIndicator;
  }

  public double getMinRate()
  {
    return minRate;
  }

  public void setMinRate(double minRate)
  {
    this.minRate = minRate;
  }

  public double getMaxRate()
  {
    return maxRate;
  }

  public void setMaxRate(double maxRate)
  {
    this.maxRate = maxRate;
  }

  public double getFlatRate()
  {
    return flatRate;
  }

  public void setFlatRate(double flatRate)
  {
    this.flatRate = flatRate;
  }

  public String getChargeType()
  {
    return chargeType;
  }

  public void setChargeType(String chargeType)
  {
    this.chargeType = chargeType;
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
  
  public String getMaxChargeFlag()
  {
    return maxChargeFlag;
  }

  public void setMaxChargeFlag(String maxChargeFlag)
  {
    this.maxChargeFlag = maxChargeFlag;
  }

  public String[] getSlabBreak()
  {
    return slabBreak;
  }

  public void setSlabBreak(String[] slabBreak)
  {
    this.slabBreak = slabBreak;
  }

  public HashMap getSlabRates()
  {
    return slabRates;
  }

  public void setSlabRates(HashMap slabRates)
  {
    this.slabRates = slabRates;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber)
  {
    this.lineNumber = lineNumber;
  }

  public String getChargeBasis()
  {
    return chargeBasis;
  }

  public void setChargeBasis(String chargeBasis)
  {
    this.chargeBasis = chargeBasis;
  }
//@@ Added by subrahmanyam for Enhancement 170759 on 25/May/09
  public void setBaseRate(double baseRate)
  {
    this.baseRate = baseRate;
  }

  public double getBaseRate()
  {
    return baseRate;
  }
//@@ Ended by subrahmanyam for Enhancement 170759 on 25/May/09

}
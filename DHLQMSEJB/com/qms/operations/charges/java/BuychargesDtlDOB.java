/**
 * @ (#) BuychargesDtlDOB.java
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
 * File       : BuychargesDtlDOB.java
 * Sub-Module : Buy charges
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * @date      : 25-06-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.java;

public class BuychargesDtlDOB implements java.io.Serializable
{
  private double chargeRate = 0.0;
  private double lowerBound = 0.0;
  private double upperBound = 0.0;
  private String chargeRate_indicator = null;
  private String chargeSlab = "";
  private String marginValue= "";//Added for sellcharesMaster to enter margins//
  
  public BuychargesDtlDOB()
  {
  }
  
  public void setChargeRate(double chargeRate)
  {
    this.chargeRate = chargeRate;
  }
  public double getChargeRate()
  {
    return this.chargeRate;
  }
  
  public void setLowerBound(double lowerBound)
  {
    this.lowerBound = lowerBound;
  }
  public double getLowerBound()
  {
    return this.lowerBound;
  }

  public void setUpperBound(double upperBound)
  {
    this.upperBound = upperBound;
  }
  public double getUpperBound()
  {
    return this.upperBound;
  }
  
  public void setChargeRate_indicator(String chargeRate_indicator)
  {
    this.chargeRate_indicator = chargeRate_indicator;
  }
  public String getChargeRate_indicator()
  {
    return this.chargeRate_indicator;
  }
  
  public void setChargeSlab(String chargeSlab)
  {
    this.chargeSlab = chargeSlab;
  }
  public String getChargeSlab()
  {
    return this.chargeSlab;
  }
  
  public void setMarginValue(String marginValue)
  {
    this.marginValue = marginValue;
  }
  public String getMarginValue()
  {
    return this.marginValue;
  }
}
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
 * @author    : Madhu.Y
 * * @date    : 08-08-2005
 * Modified by      Date     Reason
 */

package com.qms.operations.sellrates.java;
import java.sql.Timestamp;
import java.io.Serializable;

public class QMSBoundryDOB implements Serializable
{
    public QMSBoundryDOB()
    {
    }
    private double  chargeRate;
    private String  weightBreak;
    private long    lowerBound;
    private long    uperBound;
    private double  sameOvrMargin;
    private String  chargerateIndicator;
  
  //@@Getter Methods 
    public double getSameOvrMargin()            {    return sameOvrMargin;              }
    public double getChargeRate()               {    return chargeRate;                 }
    public String getWeightBreak()              {    return weightBreak;                }
    public long getLowerBound()                 {    return lowerBound;                 }
    public long getUperBound()                  {    return uperBound;                  }
  //End
  /*
  * Setter Method
  * Parameter double chargeRate
  */
  public void setSameOvrMargin(double sameOvrMargin)
  {
    this.sameOvrMargin = sameOvrMargin;
  }
 /*
  * Setter Method
  * Parameter double chargeRate
  */
  public void setChargeRate(double chargeRate)
  {
    this.chargeRate = chargeRate;
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
  * Parameter long lowerBound
  */
  public void setLowerBound(long lowerBound)
  {
    this.lowerBound = lowerBound;
  }
 /*
  * Setter Method
  * Parameter long uperBound
  */
  public void setUperBound(long uperBound)
  {
    this.uperBound = uperBound;
  }

    public void setChargerateIndicator(String chargerateIndicator)
  {
    this.chargerateIndicator = chargerateIndicator;
  }


  public String getChargerateIndicator()
  {
    return chargerateIndicator;
  }
}
/**
 * @ (#) SellChargesEntityBeanPK.java
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
 * File : BuyChargesEntityBeanPK.java
 * Sub-Module : Buy charges master
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.operations.charges.sellcharges.ejb.bmp;
import java.io.Serializable;

public class SellChargesEntityBeanPK implements Serializable 
{
  public String chargeId;
  //public String chargeBasisId;
  //public String rateBreak;
  //public String rateType;
  public String chargeDescId;
  public String terminalId;
    public String operation;//@@Addded by Kameswari for the WPBN issue-154398 on 15/02/09
  public SellChargesEntityBeanPK()
  {
  }

  public boolean equals(Object other)
  {
    if (other instanceof SellChargesEntityBeanPK)
    {
      final SellChargesEntityBeanPK otherSellChargesEntityBeanPK = (SellChargesEntityBeanPK)other;

      // The following assignment statement is auto-maintained and may be overwritten.
      boolean areEqual = true;

      return areEqual;
    }

    return false;
  }

  public int hashCode()
  {
    // Add custom hashCode() impl here
    return super.hashCode();
  }
}
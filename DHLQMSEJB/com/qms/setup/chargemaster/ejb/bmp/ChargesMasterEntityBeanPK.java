/**
 * @ (#) ChargesMasterEntityBeanPK.java
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
 * File : ChargesMasterEntityBeanPK.java
 * Sub-Module : chargesmaster
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */

package com.qms.setup.chargemaster.ejb.bmp;
import java.io.Serializable;

public class ChargesMasterEntityBeanPK implements Serializable 
{
  public java.lang.String chargeId;
  public ChargesMasterEntityBeanPK()
  {
  }

  public boolean equals(Object other)
  {
    if (other instanceof ChargesMasterEntityBeanPK)
    {
      final ChargesMasterEntityBeanPK otherChargesMasterEntityBeanPK = (ChargesMasterEntityBeanPK)other;

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
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

public class BuySellChargesEnterIdDOB implements java.io.Serializable
{
  String chargeId = null;
  String chargeDescId = null;
  String operation  = null;
  String terminalId   = null;
  String fromWhere    = null;
  String chargeGroupId  = null;
  public BuySellChargesEnterIdDOB()
  {
  }


  public void setChargeId(String chargeId)
  {
    this.chargeId = chargeId;
  }


  public String getChargeId()
  {
    return chargeId;
  }


  public void setChargeDescId(String chargeDescId)
  {
    this.chargeDescId = chargeDescId;
  }


  public String getChargeDescId()
  {
    return chargeDescId;
  }


  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }


  public String getTerminalId()
  {
    return terminalId;
  }


  public void setOperation(String operation)
  {
    this.operation = operation;
  }


  public String getOperation()
  {
    return operation;
  }


  public void setFromWhere(String fromWhere)
  {
    this.fromWhere = fromWhere;
  }


  public String getFromWhere()
  {
    return fromWhere;
  }
  
  public void setChargeGroupId(String chargeGroupId)
  {
    this.chargeGroupId = chargeGroupId;
  }


  public String getChargeGroupId()
  {
    return chargeGroupId;
  }
  
}
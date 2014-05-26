/**
 * @ (#) BuychargesHDRDOB.java
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
 * File       : BuychargesHDRDOB.java
 * Sub-Module : Buy charges
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * @date      : 25-06-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.java;
import java.sql.Timestamp;
import java.util.ArrayList;

public class BuychargesHDRDOB implements java.io.Serializable
{
  private double buySellChargeId= 0.0;
  private String chargeId       = null;
  private String chargeDescId     = null;
  private String chargeBasisId  = null;
  private String chargeBasisDesc= null;
  private String currencyId     = null;
  private String densityGrpCode	=null;
  private String rateBreak      = null;
  private String rateType       = null;
  private String weightClass    = null;
  private String terminalId     = null;
  private String accessLevel    = null;
  private String userId         = null;
  private Timestamp create_Time = null;
  private ArrayList buychargeDtlList    = null;
  //Added these for sell charges////
  private String marginBasis    = null;
  private String overallMargin  = null;
  private String marginType     = null;
  private String dummy_buycharges_flag=null;
  //end//
  private String dataAtHigher   = null;
  
  private String chargeDesc     = null;
    private int shipmentMode   ;
  private String remarks        = null;
   private String invalidate     = null;
  
  private String primaryUnit;
  
  private double buychargeId    = 0;
  private String externalChargeName;
  private String shipModeString;
  private String operation;//@@Addded by Kameswari for the WPBN issue-154398 on 15/02/09
  
  
  public BuychargesHDRDOB()
  {
  }
  public void setBuySellChargeId(double buySellChargeId)
  {
    this.buySellChargeId  = buySellChargeId;
  }
  public double getBuySellChargeId()
  {
    return this.buySellChargeId;
  }
  public void setDensityGrpCode(String densityGrpCode)/*added by naresh*/
  {
	  this.densityGrpCode=densityGrpCode;
  }
  public String getDensityGrpCode()
  {
	  return densityGrpCode;
  }
  public void setChargeId(String chargeId)
  {
    this.chargeId = chargeId;
  }
  public String getChargeId()
  {
    return this.chargeId;
  }

  public void setChargeBasisId(String chargeBasisId)
  {
    this.chargeBasisId = chargeBasisId;
  }
  public String getChargeBasisId()
  {
    return this.chargeBasisId;
  }
  
  public void setChargeBasisDesc(String chargeBasisDesc)
  {
    this.chargeBasisDesc = chargeBasisDesc;
  }
  public String getChargeBasisDesc()
  {
    return this.chargeBasisDesc;
  }
  
  public void setCurrencyId(String currencyId)
  {
    this.currencyId = currencyId;
  }
  public String getCurrencyId()
  {
    return this.currencyId;
  }
  
  public void setRateBreak(String rateBreak)
  {
    this.rateBreak = rateBreak;
  }
  public String getRateBreak()
  {
    return this.rateBreak;
  }

  public void setRateType(String rateType)
  {
    this.rateType = rateType;
  }
  public String getRateType()
  {
    return this.rateType;
  }

  public void setWeightClass(String weightClass)
  {
    this.weightClass = weightClass;
  }
  public String getWeightClass()
  {
    return this.weightClass;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }
  public String getTerminalId()
  {
    return this.terminalId;
  }

  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }
  public String getAccessLevel()
  {
    return this.accessLevel;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }
  public String getUserId()
  {
    return this.userId;
  }

  public void setCreateTime(Timestamp create_Time)
  {
    this.create_Time = create_Time;
  }
  public Timestamp getCreate_Time()
  {
    return this.create_Time;
  }  
  
  public void setBuyChargeDtlList(ArrayList buychargeDtlList)
  {
    this.buychargeDtlList = buychargeDtlList;
  }
  public ArrayList getBuyChargeDtlList()
  {
    return this.buychargeDtlList;
  }

  //Added these for sell charges////
  public void setMarginBasis(String marginBasis)
  {
    this.marginBasis = marginBasis;
  }
  public String getMarginBasis()
  {
    return this.marginBasis;
  }
  public void setOverallMargin(String overallMargin)
  {
    this.overallMargin = overallMargin;
  }
  public String getOverallMargin()
  {
    return this.overallMargin;
  }
  public void setMarginType(String marginType)
  {
    this.marginType = marginType;
  }
  public String getMarginType()
  {
    return this.marginType;
  }  
  public void setDummyBuychargesFlag(String dummy_buycharges_flag)
  {
    this.dummy_buycharges_flag = dummy_buycharges_flag;
  }
  public String getDummyBuychargesFlag()
  {
    return this.dummy_buycharges_flag;
  }


  public void setChargeDescId(String chargeDescId)
  {
    this.chargeDescId = chargeDescId;
  }


  public String getChargeDescId()
  {
    return chargeDescId;
  }


  public void setDataAtHigher(String dataAtHigher)
  {
    this.dataAtHigher = dataAtHigher;
  }


  public String getDataAtHigher()
  {
    return dataAtHigher;
  }


  public void setChargeDesc(String chargeDesc)
  {
    this.chargeDesc = chargeDesc;
  }


  public String getChargeDesc()
  {
    return chargeDesc;
  }


  public void setShipmentMode(int shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public int getShipmentMode()
  {
    return shipmentMode;
  }


  public void setRemarks(String remarks)
  {
    this.remarks = remarks;
  }


  public String getRemarks()
  {
    return remarks;
  }


  public void setInvalidate(String invalidate)
  {
    this.invalidate = invalidate;
  }


  public String getInvalidate()
  {
    return invalidate;
  }


  public void setBuychargeId(double buychargeId)
  {
    this.buychargeId = buychargeId;
  }


  public double getBuychargeId()
  {
    return buychargeId;
  }


  public void setPrimaryUnit(String primaryUnit)
  {
    this.primaryUnit = primaryUnit;
  }


  public String getPrimaryUnit()
  {
    return primaryUnit;
  }

  public String getExternalChargeName()
  {
    return externalChargeName;
  }

  public void setExternalChargeName(String externalChargeName)
  {
    this.externalChargeName = externalChargeName;
  }

  public String getShipModeString()
  {
    return shipModeString;
  }

  public void setShipModeString(String shipModeString)
  {
    this.shipModeString = shipModeString;
  }
//@@Addded by Kameswari for the WPBN issue-154398 on 15/02/09
  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }
//@@WPBN issue-154398 on 15/02/09
  
  
  //end//
}
/**
 * @ (#) ChargeGroupingDOB.java
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
 * File       : ChargeGroupingDOB.java
 * Sub-Module : chargesgroup
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * * @date    : 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.java;
import java.util.ArrayList;

public class ChargeGroupingDOB implements java.io.Serializable
{

  String chargeGroup    = null;
  String chargeIds      = null;
  int    shipmentMode   = 0; 
  String chargeDescId   = null;
  String terminalId     = null;
  String invalidate     = null;
ArrayList chargeIdList = null;
  ArrayList descriptionIdList = null;
  private String remarks;
  private String shipModeString;
  private String originCountry = null;//Added by Anil.k for Enhancement 231214 on 24Jan2011
  private String destinationCountry = null;//Added by Anil.k for Enhancement 231214 on 24Jan2011
  private String buySellChargeId = null;//Added by Anil.k for issue 236357 on 22Feb2011 
  private String chargeBasis = null;//Added by Anil.k for issue 236357 on 22Feb2011   
  private String currency = null;//Added by Anil.k for issue 236357 on 22Feb2011 
  private double buyChargeRate = 0.0;//Added by Anil.k for issue 236357 on 22Feb2011 
  private String densityCode = null;//Added by Anil.k for issue 236357 on 22Feb2011 
  private String chargeSlab = null;//Added by Anil.k for issue 236357 on 22Feb2011 
  private String costIncurredIn = null;//Added by Anil.k for issue 236357 on 22Feb2011 
  private double sellChargeRate	= 0.0;//Added by Anil.k for issue 236357 on 22Feb2011 
  

  public ChargeGroupingDOB()
  {
  }
 
   public void setChargeIdList(ArrayList chargeIdList)
  {
    this.chargeIdList  = chargeIdList;
  }
  public ArrayList getChargeIdList()
  {
    return this.chargeIdList;
  }
   public void setDescriptionIdList(ArrayList descriptionIdList)
  {
    this.descriptionIdList  = descriptionIdList;
  }
  public ArrayList getDescriptionIdList()
  {
    return this.descriptionIdList;
  }
  public void setInvalidate(String invalidate)
  {
    this.invalidate  = invalidate;
  }
  public String getInvalidate()
  {
    return this.invalidate;
  }
  public void setChargeGroup(String chargeGroup)
  {
    this.chargeGroup  = chargeGroup;
  }
  public String getChargeGroup()
  {
    return this.chargeGroup;
  }
  
  public void setChargeIds(String setchargeids)
  {
    this.chargeIds  = setchargeids;    
  }
  
  public String getChargeIds()
  {
    return this.chargeIds;
  }
  
  public void setShipmentMode(int shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }
  public int getShipmentMode()
  {
    return this.shipmentMode;
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

  public String getRemarks()
  {
    return remarks;
  }

  public void setRemarks(String remarks)
  {
    this.remarks = remarks;
  }

  public String getShipModeString()
  {
    return shipModeString;
  }

  public void setShipModeString(String shipModeString)
  {
    this.shipModeString = shipModeString;
  }
//Added by Anil.k for Enhancement 231214 on 24Jan2011
public String getOriginCountry() {
	return originCountry;
}

public void setOriginCountry(String originCountry) {
	this.originCountry = originCountry;
}

public String getDestinationCountry() {
	return destinationCountry;
}

public void setDestinationCountry(String destinationCountry) {
	this.destinationCountry = destinationCountry;
}
//Ended by Anil.k for Enhancement 231214 on 24Jan2011
//Added by Anil.k for issue 236357 on 22Feb2011 
public String getBuySellChargeId() {
	return buySellChargeId;
}

public void setBuySellChargeId(String buySellChargeId) {
	this.buySellChargeId = buySellChargeId;
}

public String getChargeBasis() {
	return chargeBasis;
}

public void setChargeBasis(String chargeBasis) {
	this.chargeBasis = chargeBasis;
}

public String getCurrency() {
	return currency;
}

public void setCurrency(String currency) {
	this.currency = currency;
}

public String getDensityCode() {
	return densityCode;
}

public void setDensityCode(String densityCode) {
	this.densityCode = densityCode;
}

public String getChargeSlab() {
	return chargeSlab;
}

public void setChargeSlab(String chargeSlab) {
	this.chargeSlab = chargeSlab;
}

public String getCostIncurredIn() {
	return costIncurredIn;
}

public void setCostIncurredIn(String costIncurredIn) {
	this.costIncurredIn = costIncurredIn;
}
//Ended by Anil.k for issue 236357 on 22Feb2011 

public double getBuyChargeRate() {
	return buyChargeRate;
}

public void setBuyChargeRate(double buyChargeRate) {
	this.buyChargeRate = buyChargeRate;
}

public double getSellChargeRate() {
	return sellChargeRate;
}

public void setSellChargeRate(double sellChargeRate) {
	this.sellChargeRate = sellChargeRate;
}
 
  
}


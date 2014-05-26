/**
 * @ (#) QMSCartageMasterDOB.java
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
 * File       : QMSCartageMasterDOB.java
 * Sub-Module : Cartage Buy/Sell charges
 * Module     : QMS
 * @author    : Yuvraj Waghray
 * @date      : 30-08-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.java;
import java.sql.Timestamp;

public class QMSCartageMasterDOB implements java.io.Serializable
{
  private long cartageId;
  private String locationId;
  private String chargeType;
  private String weightBreak;
  private String rateType;
  private Timestamp effectiveFrom;
  private Timestamp validUpto;
  private String currencyId;
  private String chargeBasis;
  private String uom;
  private String maxChargeFlag;
  private String createdBy;
  private Timestamp createdTimestamp;
  private String accessLevel;
  private String terminalId;
  private String[] zoneCodes;
  private String vendorId;
  private String[] slabRates;
  private String zoneCode;
  private String[] vendorIds;
  private String   operation;
  private String shipmentMode;
  private String consoleType;
  private String[] listValues;
  private String primaryUnit;
  
  public QMSCartageMasterDOB()
  {
  }
  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }
  public long getCartageId()
  {
    return cartageId;
  }

  public void setCartageId(long cartageId)
  {
    this.cartageId = cartageId;
  }

  public String getLocationId()
  {
    return locationId;
  }

  public void setLocationId(String locationId)
  {
    this.locationId = locationId;
  }

  public String getChargeType()
  {
    return chargeType;
  }

  public void setChargeType(String chargeType)
  {
    this.chargeType = chargeType;
  }

  public String getWeightBreak()
  {
    return weightBreak;
  }

  public void setWeightBreak(String weightBreak)
  {
    this.weightBreak = weightBreak;
  }

  public String getRateType()
  {
    return rateType;
  }

  public void setRateType(String rateType)
  {
    this.rateType = rateType;
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

  public String getCurrencyId()
  {
    return currencyId;
  }

  public void setCurrencyId(String currencyId)
  {
    this.currencyId = currencyId;
  }

  public String getChargeBasis()
  {
    return chargeBasis;
  }

  public void setChargeBasis(String chargeBasis)
  {
    this.chargeBasis = chargeBasis;
  }

  public String getUom()
  {
    return uom;
  }

  public void setUom(String uom)
  {
    this.uom = uom;
  }

  public String getMaxChargeFlag()
  {
    return maxChargeFlag;
  }

  public void setMaxChargeFlag(String maxChargeFlag)
  {
    this.maxChargeFlag = maxChargeFlag;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }

  public Timestamp getCreatedTimestamp()
  {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Timestamp createdTimestamp)
  {
    this.createdTimestamp = createdTimestamp;
  }

  public String getAccessLevel()
  {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }

  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }

  public String[] getZoneCodes()
  {
    return zoneCodes;
  }

  public void setZoneCodes(String[] zoneCodes)
  {
    this.zoneCodes = zoneCodes;
  }

  public String getVendorId()
  {
    return vendorId;
  }

  public void setVendorId(String vendorId)
  {
    this.vendorId = vendorId;
  }

  public String[] getSlabRates()
  {
    return slabRates;
  }

  public void setSlabRates(String[] slabRates)
  {
    this.slabRates = slabRates;
  }

  public String getZoneCode()
  {
    return zoneCode;
  }

  public void setZoneCode(String zoneCode)
  {
    this.zoneCode = zoneCode;
  }

  public String[] getVendorIds()
  {
    return vendorIds;
  }

  public void setVendorIds(String[] vendorIds)
  {
    this.vendorIds = vendorIds;
  }

  public String getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }

  public String getConsoleType()
  {
    return consoleType;
  }

  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }

  public String[] getListValues()
  {
    return listValues;
  }

  public void setListValues(String[] listValues)
  {
    this.listValues = listValues;
  }

  public String getPrimaryUnit()
  {
    return primaryUnit;
  }

  public void setPrimaryUnit(String primaryUnit)
  {
    this.primaryUnit = primaryUnit;
  }
}
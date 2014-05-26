package com.qms.operations.costing.dob;
import java.io.Serializable;
import java.util.ArrayList;

public class CostingChargeDetailsDOB implements Serializable 
{

  //to fetch the data
  private String buyChargeId;
  private String sellChargeId;
  private String chargeId;
  private String chargeDescId;
  private String currency;
  private String chargeBasisDesc;
  private String weightBreak;
  private String rateType;
  private String primaryBasis;
  private String secondaryBasis;
  private String tertiaryBasis;
  private double block;
  private String weightClass;
  private String densityRatio;
  private String shipmentMode;
  private String costIncurred;
  private double convFactor; 
  
  private ArrayList costingRateInfoDOB;
  //end for fectching data
  
  
  private String brkPoint;
  private String rate;
  private String chargeBasis;
  private String primaryUnitValue;
  private String secUnitValue;
  private String rateValue;
  
  private String[]  primaryUnitArray;
  private String[]  secUnitArray;
  
  private String checked;
  
  private boolean customerAdvFlag;
  
  
  private String marginDiscountType;
  private String internalName;
  private String externalName;
  private String rateDescription;//@@Added by Kameswari for Surcharge Enhancements
  
  
  public CostingChargeDetailsDOB()
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


  public void setCurrency(String currency)
  {
    this.currency = currency;
  }


  public String getCurrency()
  {
    return currency;
  }


  public void setChargeBasisDesc(String chargeBasisDesc)
  {
    this.chargeBasisDesc = chargeBasisDesc;
  }


  public String getChargeBasisDesc()
  {
    return chargeBasisDesc;
  }


  public void setWeightBreak(String weightBreak)
  {
    this.weightBreak = weightBreak;
  }


  public String getWeightBreak()
  {
    return weightBreak;
  }


  public void setRateType(String rateType)
  {
    this.rateType = rateType;
  }


  public String getRateType()
  {
    return rateType;
  }


  public void setPrimaryBasis(String primaryBasis)
  {
    this.primaryBasis = primaryBasis;
  }


  public String getPrimaryBasis()
  {
    return primaryBasis;
  }


  public void setSecondaryBasis(String secondaryBasis)
  {
    this.secondaryBasis = secondaryBasis;
  }


  public String getSecondaryBasis()
  {
    return secondaryBasis;
  }


  public void setTertiaryBasis(String tertiaryBasis)
  {
    this.tertiaryBasis = tertiaryBasis;
  }


  public String getTertiaryBasis()
  {
    return tertiaryBasis;
  }


  public void setBlock(double block)
  {
    this.block = block;
  }


  public double getBlock()
  {
    return block;
  }


  public void setWeightClass(String weightClass)
  {
    this.weightClass = weightClass;
  }


  public String getWeightClass()
  {
    return weightClass;
  }


  public void setDensityRatio(String densityRatio)
  {
    this.densityRatio = densityRatio;
  }


  public String getDensityRatio()
  {
    return densityRatio;
  }


  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public String getShipmentMode()
  {
    return shipmentMode;
  }


  public void setCostingRateInfoDOB(ArrayList costingRateInfoDOB)
  {
    this.costingRateInfoDOB = costingRateInfoDOB;
  }


  public ArrayList getCostingRateInfoDOB()
  {
    return costingRateInfoDOB;
  }


  public void setCostIncurred(String costIncurred)
  {
    this.costIncurred = costIncurred;
  }


  public String getCostIncurred()
  {
    return costIncurred;
  }


  public void setBuyChargeId(String buyChargeId)
  {
    this.buyChargeId = buyChargeId;
  }


  public String getBuyChargeId()
  {
    return buyChargeId;
  }


  public void setSellChargeId(String sellChargeId)
  {
    this.sellChargeId = sellChargeId;
  }


  public String getSellChargeId()
  {
    return sellChargeId;
  }


  public void setConvFactor(double convFactor)
  {
    this.convFactor = convFactor;
  }


  public double getConvFactor()
  {
    return convFactor;
  }


  public void setBrkPoint(String brkPoint)
  {
    this.brkPoint = brkPoint;
  }


  public String getBrkPoint()
  {
    return brkPoint;
  }


  public void setRate(String rate)
  {
    this.rate = rate;
  }


  public String getRate()
  {
    return rate;
  }


  public void setChargeBasis(String chargeBasis)
  {
    this.chargeBasis = chargeBasis;
  }


  public String getChargeBasis()
  {
    return chargeBasis;
  }


  public void setPrimaryUnitValue(String primaryUnitValue)
  {
    this.primaryUnitValue = primaryUnitValue;
  }


  public String getPrimaryUnitValue()
  {
    return primaryUnitValue;
  }


  public void setSecUnitValue(String secUnitValue)
  {
    this.secUnitValue = secUnitValue;
  }


  public String getSecUnitValue()
  {
    return secUnitValue;
  }


  public void setRateValue(String rateValue)
  {
    this.rateValue = rateValue;
  }


  public String getRateValue()
  {
    return rateValue;
  }


  public void setPrimaryUnitArray(String[] primaryUnitArray)
  {
    this.primaryUnitArray = primaryUnitArray;
  }


  public String[] getPrimaryUnitArray()
  {
    return primaryUnitArray;
  }


  public void setSecUnitArray(String[] secUnitArray)
  {
    this.secUnitArray = secUnitArray;
  }


  public String[] getSecUnitArray()
  {
    return secUnitArray;
  }


  public void setChecked(String checked)
  {
    this.checked = checked;
  }


  public String getChecked()
  {
    return checked;
  }


  public void setMarginDiscountType(String marginDiscountType)
  {
    this.marginDiscountType = marginDiscountType;
  }


  public String getMarginDiscountType()
  {
    return marginDiscountType;
  }


  public void setCustomerAdvFlag(boolean customerAdvFlag)
  {
    this.customerAdvFlag = customerAdvFlag;
  }


  public boolean isCustomerAdvFlag()
  {
    return customerAdvFlag;
  }

  public String getInternalName()
  {
    return internalName;
  }

  public void setInternalName(String internalName)
  {
    this.internalName = internalName;
  }

  public String getExternalName()
  {
    return externalName;
  }

  public void setExternalName(String externalName)
  {
    this.externalName = externalName;
  }
//@@Added by Kameswari for Surcharge Enhancements
  public String getRateDescription()
  {
    return rateDescription;
  }

  public void setRateDescription(String rateDescription)
  {
    this.rateDescription = rateDescription;
  }
//@@Enhancements





}
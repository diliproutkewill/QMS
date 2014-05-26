package com.qms.operations.costing.dob;
import java.io.Serializable;


public class CostingRateInfoDOB implements Serializable
{

  private String weightBreakSlab;
  private double rate;
  private double lowerBound;
  private double upperBound;
  private String rateIndicator;
  private String sellRateId;
  private String buyRateId;
  
  private String primaryUnitValue;
  private String secUnitValue;
  private String rateValue;
  
  private double margin;
  private String marginType;
  private double discount;
  private String discountType;
  private String rateDescription;//@@Added by Kameswari for Surcharge Enhancements
  private String mutilQuoteCurrency;
  /**
 * @return the mutilQuoteCurrency
 */
public String getMutilQuoteCurrency() {
	return mutilQuoteCurrency;
}


/**
 * @param mutilQuoteCurrency the mutilQuoteCurrency to set
 */
public void setMutilQuoteCurrency(String mutilQuoteCurrency) {
	this.mutilQuoteCurrency = mutilQuoteCurrency;
}

  
  public CostingRateInfoDOB()
  {
  }


  public void setWeightBreakSlab(String weightBreakSlab)
  {
    this.weightBreakSlab = weightBreakSlab;
  }


  public String getWeightBreakSlab()
  {
    return weightBreakSlab;
  }


  public void setRate(double rate)
  {
    this.rate = rate;
  }


  public double getRate()
  {
    return rate;
  }


  public void setLowerBound(double lowerBound)
  {
    this.lowerBound = lowerBound;
  }


  public double getLowerBound()
  {
    return lowerBound;
  }


  public void setUpperBound(double upperBound)
  {
    this.upperBound = upperBound;
  }


  public double getUpperBound()
  {
    return upperBound;
  }


  public void setRateIndicator(String rateIndicator)
  {
    this.rateIndicator = rateIndicator;
  }


  public String getRateIndicator()
  {
    return rateIndicator;
  }


  public void setSellRateId(String sellRateId)
  {
    this.sellRateId = sellRateId;
  }


  public String getSellRateId()
  {
    return sellRateId;
  }


  public void setBuyRateId(String buyRateId)
  {
    this.buyRateId = buyRateId;
  }


  public String getBuyRateId()
  {
    return buyRateId;
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


  public void setMargin(double margin)
  {
    this.margin = margin;
  }


  public double getMargin()
  {
    return margin;
  }


  public void setMarginType(String marginType)
  {
    this.marginType = marginType;
  }


  public String getMarginType()
  {
    return marginType;
  }


  public void setDiscount(double discount)
  {
    this.discount = discount;
  }


  public double getDiscount()
  {
    return discount;
  }


  public void setDiscountType(String discountType)
  {
    this.discountType = discountType;
  }


  public String getDiscountType()
  {
    return discountType;
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
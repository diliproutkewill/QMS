package com.qms.operations.quote.dob;
import java.io.Serializable;

public class QuoteChargeInfo implements Serializable
{
  public QuoteChargeInfo()
  {
  }
  
  private String  breakPoint;
  private String  currency;
  private double  buyRate;
  private double  recOrConSellRrate;
  private String  marginType;
  private double  margin;
  private String  discountType;
  private double  discount;
  private double  sellRate;
  private String  basis;
  private String  ratio;
  private double  sellChargeMargin;
  private String  sellChargeMarginType;
  private String  weight_break;//@@ Added by govind for the issue 264638 on 09-08-2011
  private String  rate_Type;//@@ Added by govind for the issue 264638 on 09-08-2011
  private boolean marginTestFailed;
  private int lineNumber;
  private boolean percentValue;
  private String rateIndicator;
  private String rateDescription;//@@Added by kameswari for Surcharges
//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10     
  private double tieMarginDiscountValue;
  private double tieSellrateValue;
  private String selectedFlag;
//@@Ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   
  
  public String getBasis()
  {
    return basis;
  }

  public void setBasis(String basis)
  {
    this.basis = basis;
  }

  public String getBreakPoint()
  {
    return breakPoint;
  }

  public void setBreakPoint(String breakPoint)
  {
    this.breakPoint = breakPoint;
  }

  public double getBuyRate()
  {
    return buyRate;
  }

  public void setBuyRate(double buyRate)
  {
    this.buyRate = buyRate;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency(String currency)
  {
    this.currency = currency;
  }

  public double getDiscount()
  {
    return discount;
  }

  public void setDiscount(double discount)
  {
    this.discount = discount;
  }

  public String getDiscountType()
  {
    return discountType;
  }

  public void setDiscountType(String discountType)
  {
    this.discountType = discountType;
  }

  public double getMargin()
  {
    return margin;
  }

  public void setMargin(double margin)
  {
    this.margin = margin;
  }

  public String getMarginType()
  {
    return marginType;
  }

  public void setMarginType(String marginType)
  {
    this.marginType = marginType;
  }

  public String getRatio()
  {
    return ratio;
  }

  public void setRatio(String ratio)
  {
    this.ratio = ratio;
  }

  public double getRecOrConSellRrate()
  {
    return recOrConSellRrate;
  }

  public void setRecOrConSellRrate(double recOrConSellRrate)
  {
    this.recOrConSellRrate = recOrConSellRrate;
  }



  public double getSellRate()
  {
    return sellRate;
  }

  public void setSellRate(double sellRate)
  {
    this.sellRate = sellRate;
  }

  public double getSellChargeMargin()
  {
    return sellChargeMargin;
  }

  public void setSellChargeMargin(double sellChargeMargin)
  {
    this.sellChargeMargin = sellChargeMargin;
  }

  public String getSellChargeMarginType()
  {
    return sellChargeMarginType;
  }

  public void setSellChargeMarginType(String sellChargeMarginType)
  {
    this.sellChargeMarginType = sellChargeMarginType;
  }

  public boolean isMarginTestFailed()
  {
    return marginTestFailed;
  }

  public void setMarginTestFailed(boolean marginTestFailed)
  {
    this.marginTestFailed = marginTestFailed;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber)
  {
    this.lineNumber = lineNumber;
  }

  public boolean isPercentValue()
  {
    return percentValue;
  }

  public void setPercentValue(boolean percentValue)
  {
    this.percentValue = percentValue;
  }

  public String getRateIndicator()
  {
    return rateIndicator;
  }

  public void setRateIndicator(String rateIndicator)
  {
    this.rateIndicator = rateIndicator;
  }
//@@Added by kameswari for Surcharge Enahncements
  public String getRateDescription()
  {
    return rateDescription;
  }

  public void setRateDescription(String rateDescription)
  {
    this.rateDescription = rateDescription;
  }
//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   
  
  public void setTieMarginDiscountValue(double tieMarginDiscountValue)
  {
	  this.tieMarginDiscountValue	=	tieMarginDiscountValue;
  }
  public double getTieMarginDiscountValue()
  {
	  return tieMarginDiscountValue;
  }
  public void setTieSellRateValue(double tieSellrateValue)
  {
	  this.tieSellrateValue	=	tieSellrateValue;
  }
  public double getTieSellRateValue()
  {
	  return tieSellrateValue;
  }
  public void setSelectedFlag(String selectedFlag)
  {
	  this.selectedFlag		= selectedFlag;
	  
  }
  public String getSelectedFlag()
  {
	  return selectedFlag;
  }
//@@Ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10     

public String getWeight_break() {
	return weight_break;
}

public void setWeight_break(String weight_break) {
	this.weight_break = weight_break;
}

public String getRate_Type() {
	return rate_Type;
}

public void setRate_Type(String rate_Type) {
	this.rate_Type = rate_Type;
}
}
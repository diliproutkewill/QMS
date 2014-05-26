package com.qms.operations.quote.dob;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

public class QuoteCartageRates implements Serializable
{
  private String cartageId;
  private String buyOrSellRate;
  private String zone;
  private Timestamp effDate;
  private Timestamp validUpTo;
  private HashMap rates;
  private String currency;
  
  public QuoteCartageRates()
  {
  }

  public String getCartageId()
  {
    return cartageId;
  }

  public void setCartageId(String cartageId)
  {
    this.cartageId = cartageId;
  }

  public String getBuyOrSellRate()
  {
    return buyOrSellRate;
  }

  public void setBuyOrSellRate(String buyOrSellRate)
  {
    this.buyOrSellRate = buyOrSellRate;
  }

  public String getZone()
  {
    return zone;
  }

  public void setZone(String zone)
  {
    this.zone = zone;
  }

  public Timestamp getEffDate()
  {
    return effDate;
  }

  public void setEffDate(Timestamp effDate)
  {
    this.effDate = effDate;
  }

  public Timestamp getValidUpTo()
  {
    return validUpTo;
  }

  public void setValidUpTo(Timestamp validUpTo)
  {
    this.validUpTo = validUpTo;
  }

  public HashMap getRates()
  {
    return rates;
  }

  public void setRates(HashMap rates)
  {
    this.rates = rates;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency(String currency)
  {
    this.currency = currency;
  }
}
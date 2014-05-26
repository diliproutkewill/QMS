
/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 *
 

 * File					: MultiQuoteCartageRates.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */




package com.qms.operations.multiquote.dob;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

public class MultiQuoteCartageRates implements Serializable
{
  private String cartageId;
  private String buyOrSellRate;
  private String zone;
  private Timestamp effDate;
  private Timestamp validUpTo;
  private HashMap rates;
  private String currency;
  
  public MultiQuoteCartageRates()
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
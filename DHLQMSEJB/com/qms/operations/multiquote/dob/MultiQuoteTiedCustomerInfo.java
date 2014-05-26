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
 

 * File					: MultiQuoteTiedCustomerInfo.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */
package com.qms.operations.multiquote.dob;
import java.io.Serializable;
import java.util.Hashtable;

/*
 * This class will be useful to store the datails of charges and their sell rates that are qouted in previous quotes. When data is retrived
 * from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 * @version 	
 * 
*/

public class MultiQuoteTiedCustomerInfo implements Serializable
{
  private String chargeId;
  private String chargeDesc;
  private Hashtable quoteSellRates;//used to store the sell rates of the charges that are quoted in the previous quotes as quoteid-sellRate (as the key-value pair) 

  /**
   * 
   * default constructor
   */
  public MultiQuoteTiedCustomerInfo()
  {
  }

  public String getChargeId()
  {
    return chargeId;
  }

  public void setChargeId(String chargeId)
  {
    this.chargeId = chargeId;
  }

  public String getChargeDesc()
  {
    return chargeDesc;
  }

  public void setChargeDesc(String chargeDesc)
  {
    this.chargeDesc = chargeDesc;
  }

  public Hashtable getQuoteSellRates()
  {
    return quoteSellRates;
  }

  public void setQuoteSellRates(Hashtable quoteSellRates)
  {
    this.quoteSellRates = quoteSellRates;
  }
}
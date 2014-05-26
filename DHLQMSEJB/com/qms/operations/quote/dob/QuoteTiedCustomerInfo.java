/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
*
*/
/*
Programme Name		  :QuoteTiedCustomerInfo.java
Module  Name  		  :Quote.
Task           		  :         
SubTask       		  :   
Author Name         :S Anil Kumar.
Date Started        :
Date Finished       :3rd Aug 2005 
Date Modified       :
Description         :
Method's Summary	  :
*/

package com.qms.operations.quote.dob;
import java.io.Serializable;
import java.util.Hashtable;

/*
 * This class will be useful to store the datails of charges and their sell rates that are qouted in previous quotes. When data is retrived
 * from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 * @version 	
 * 
*/

public class QuoteTiedCustomerInfo implements Serializable
{
  private String chargeId;
  private String chargeDesc;
  private Hashtable quoteSellRates;//used to store the sell rates of the charges that are quoted in the previous quotes as quoteid-sellRate (as the key-value pair) 

  /**
   * 
   * default constructor
   */
  public QuoteTiedCustomerInfo()
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
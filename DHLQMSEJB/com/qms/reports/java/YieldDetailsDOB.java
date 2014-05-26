package com.qms.reports.java;
import java.util.ArrayList;
import java.util.HashMap;

public class YieldDetailsDOB implements java.io.Serializable
{
  
  private String quoteId;
  private String customerId;
  private String shipmentMode;
  private String serviveLevel;
  private String orgCountry;
  private String destCountry;
  
  private String orgLocation;
  private String destLocation;
  private String legSLNo;
  
  private ArrayList brkPoint;
  private ArrayList yieldValue;
  private String customerName;//@@added by kameswari for the WPBN issue-30313
  

  
  public YieldDetailsDOB()
  {
  }


  public void setOrgLocation(String orgLocation)
  {
    this.orgLocation = orgLocation;
  }


  public String getOrgLocation()
  {
    return orgLocation;
  }


  public void setDestLocation(String destLocation)
  {
    this.destLocation = destLocation;
  }


  public String getDestLocation()
  {
    return destLocation;
  }




  public void setLegSLNo(String legSLNo)
  {
    this.legSLNo = legSLNo;
  }


  public String getLegSLNo()
  {
    return legSLNo;
  }



  public void setBrkPoint(ArrayList brkPoint)
  {
    this.brkPoint = brkPoint;
  }


  public ArrayList getBrkPoint()
  {
    return brkPoint;
  }


  public void setYieldValue(ArrayList yieldValue)
  {
    this.yieldValue = yieldValue;
  }


  public ArrayList getYieldValue()
  {
    return yieldValue;
  }


  public void setQuoteId(String quoteId)
  {
    this.quoteId = quoteId;
  }


  public String getQuoteId()
  {
    return quoteId;
  }


  public void setCustomerId(String customerId)
  {
    this.customerId = customerId;
  }


  public String getCustomerId()
  {
    return customerId;
  }


  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public String getShipmentMode()
  {
    return shipmentMode;
  }


  public void setServiveLevel(String serviveLevel)
  {
    this.serviveLevel = serviveLevel;
  }


  public String getServiveLevel()
  {
    return serviveLevel;
  }


  public void setOrgCountry(String orgCountry)
  {
    this.orgCountry = orgCountry;
  }


  public String getOrgCountry()
  {
    return orgCountry;
  }


  public void setDestCountry(String destCountry)
  {
    this.destCountry = destCountry;
  }


  public String getDestCountry()
  {
    return destCountry;
  }
  
  //@@added by kameswari for the WPBN issue-30313
  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }
  //@@WPBN-30313
}
package com.qms.setup.java;
import java.io.Serializable;

public class MarginLimitMasterDOB implements Serializable
{
  //String shipmentMode =null;
  //String consoleType  =null;
  String    marginId     =null;
  String levelId      =null;
  String serviceLevel =null;
  String maxDiscount  =null;
  String minMargin    =null;
  String invalidate   =null;
  String chargeType   = null;
  String shipmentMode = null;
  String loginTerminal = null;//@@ Added by subrahmanyam for the pbn id: 203354 on 22-APR-10
  /**
   * 
   */
  public MarginLimitMasterDOB()
  {
  }
  
/*  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }
  public String getShipmentMode()
  {
    return this.shipmentMode;
  }
  public void setConsoleType(String consoleType)
  {
    this.consoleType  = consoleType;
  }
  public String getConsoleType()
  {
    return this.consoleType;
  }*/
  
  /**
   * 
   * @param marginId
   */
  public void setMarginId(String marginId)
  {
    this.marginId = marginId;
  }
  /**
   * 
   * @return 
   */
  public String getMarginId()
  {
    return this.marginId;
  }
  /**
   * 
   * @param levelId
   */
  public void setLevelId(String levelId)
  {
    this.levelId   =  levelId;
  }
  /**
   * 
   * @return 
   */
  public String getLevelId()
  {
    return this.levelId;
  }
  /**
   * 
   * @param serviceLevel
   */
    public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel = serviceLevel;
  }
  /**
   * 
   * @return 
   */
  public String getServiceLevel()
  {
    return this.serviceLevel;
  }
  /**
   * 
   * @param maxDiscount
   */
  public void setMaxDiscount(String maxDiscount)
  {
    this.maxDiscount  = maxDiscount;
  }
  /**
   * 
   * @return 
   */
  public String getMaxDiscount()
  {
    return this.maxDiscount;
  }
  /**
   * 
   * @param minMargin
   */
  public void setMinMargin(String minMargin)
  {
    this.minMargin  = minMargin;
  }
  /**
   * 
   * @return 
   */
  public String getMinMargin()
  {
    return this.minMargin;
  }
  
  /**
   * 
   * @param invalidate
   */
  public void setInvalidate(String invalidate)
  {
    this.invalidate = invalidate;
  }
  /**
   * 
   * @return 
   */
  public String getInvalidate()
  {
    return this.invalidate;
  }
  
  public void setChargeType(String chargeType)
  {
    this.chargeType = chargeType;
  }
  public String getChargeType()
  {
    return this.chargeType;
  }
  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }
  public String getShipmentMode()
  {
    return this.shipmentMode;
  }
  
//@@ Added by subrahmanyam for the pbn id: 203354 on 22-APR-10
  public void setLoginTerminal(String loginTerminal)
  {
	  this.loginTerminal	= loginTerminal;
  }
  public String getLoginTerminal()
  {
	  return this.loginTerminal;
  }
//@@ Ended by subrahmanyam for the pbn id: 203354 on 22-APR-10
}
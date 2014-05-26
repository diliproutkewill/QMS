package com.qms.setup.java;

public class ChargesMasterDOB implements java.io.Serializable
{
  String chargeId     = null;
  String chargeDesc   = null;
  int shipmentMode    = 0;
  String costIncurr   = null;
  String invalid   = null;
  public ChargesMasterDOB()
  {
  }
   public void setInvalidate(String invalid)
  {
    this.invalid=invalid  ;
  }
  public String getInvalidate()
  {
    return this.invalid;
  }
  public void setChargeId(String chargeId)
  {
    this.chargeId=chargeId  ;
  }
  public String getChargeId()
  {
    return this.chargeId;
  }
    public void setChargeDesc(String chargeDesc)
  {
    this.chargeDesc= chargeDesc ;
  }
  public String getChargeDesc()
  {
    return this.chargeDesc;
  }
    public void setShipmentMode(int shipmentMode)
  {
    this.shipmentMode=shipmentMode  ;
  }
  public int getShipmentMode()
  {
    return this.shipmentMode;
  }
    public void setCostIncurr(String costIncurr)
  {
    this.costIncurr= costIncurr ;
  }
  public String getCostIncurr()
  {
    return this.costIncurr;
  }
}
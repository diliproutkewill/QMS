package com.foursoft.etrans.setup.IATARateMaster.java;
import java.io.Serializable;

public class IATAChargeDtlsModel implements java.io.Serializable
{
  public IATAChargeDtlsModel()
  {
  }
   private int slNo		=	0;
   private String rateValue		=	null;
   private String slabValue		=	null;
   
   
    public void setRateValue(String rateValue)
    {
      this.rateValue = rateValue;
    } 
     public void setSlabValue(String slabValue)
    {
      this.slabValue = slabValue;
    } 
     public void setSlNo(int slNo)
    {
      this.slNo = slNo;
    }
    //get methods
    
    
    public String getRateValue()
    {
      return rateValue;
    } 
     public String getSlabValue()
    {
      return slabValue;
    } 
     public int getSlNo()
    {
      return slNo;
    }
}
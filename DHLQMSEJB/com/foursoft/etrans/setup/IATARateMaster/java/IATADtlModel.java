package com.foursoft.etrans.setup.IATARateMaster.java;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;



public class IATADtlModel implements Serializable
{  
    private int IATAMasterId			=	0;	 
    private String originLocation		=	null;
	private String destLocation		=	null;
 	private String serviceLevel			=	null;
 	private Timestamp validUptoDate        =   null;
	private Timestamp validFromDate        =   null;
	private String rateType                =   null;
    private String weightClass             =   null;
	private String operation              = null;
	private String uom					=	null;	
	private String currencyId			=	null;	 
	private IATAChargeDtlsModel rateDtls		=	null;	 
    
	public  IATADtlModel()
	{
	
	}
	 public void setIATAMasterId(int IATAMasterId)
    {
      this.IATAMasterId = IATAMasterId;
    } 
    public void setRateDtls(IATAChargeDtlsModel rateDtls)
    {
      this.rateDtls = rateDtls;
    } 
    public void setOriginLocation(String originLocation)
    {
      this.originLocation = originLocation;
    } 
    public void setDestLocation(String destLocation)
    {
      this.destLocation = destLocation;
    } 
    public void setServiceLevel(String serviceLevel)
    {
      this.serviceLevel = serviceLevel;
    }
    public void setValidUptoDate(Timestamp validUptoDate)
	{ 
       this.validUptoDate= validUptoDate;  
	}
	 public void setValidFromDate(Timestamp validFromDate)
    {
      this.validFromDate = validFromDate;
    }
	public void setRateType(String rateType)
	{ 
       this.rateType= rateType;  
	}
     public void setCurrencyId(String currencyId)
    {
      this.currencyId = currencyId;
    }
   
    public void setUOM(String uom)
    {
      this.uom = uom;
    }
    public void setWeightClass(String weightClass)
    {
      this.weightClass = weightClass;
    }
	public void setOperation(String operation)
    {
      this.operation = operation;
    }
    
     // Get Methods
	 
	public IATAChargeDtlsModel getRateDtls()
	{ 
      return rateDtls;  
	}
	public int getIATAMasterId()
	{ 
      return IATAMasterId;  
	}
	public String getOperation()
	{ 
      return operation;  
	}
	public Timestamp getValidUptoDate()
	{ 
      return validUptoDate;  
	}
	public Timestamp getValidFromDate()
	{ 
      return validFromDate;  
	}
    public String getWeightClass()
	{
	  return weightClass;
	}
	
    public String getOriginLocation()
    {
      return originLocation;
    } 
    public String getDestLocation()
    {
      return destLocation;
    }
    public String getServiceLevel()
    {
      return serviceLevel;
    }
    public String getRateType()
    {
      return rateType; 
    }
   
    public String getCurrencyId()
    {
      return currencyId;
    }
   
    public String getUOM()
    {
      return uom;
    }
	 

}
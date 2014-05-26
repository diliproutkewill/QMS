package com.qms.operations.rates.dob;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;


public class RateDOB implements Serializable
{
  private String shipmentMode;
  private Timestamp effectiveFrom;
  private Timestamp validUpto;
  private String serviceLevel;
  private String currency;
  private String carrierId;
  private String uom;
  private String weightBreak;
  private String rateType;
  private ArrayList rateDtls;
  private String[]  slabValues;
  private String[]  listValues;
  private String consoleType;
  private String user;
  private Timestamp createdTime;
  private String    weightClass ;
  private String accessLevel;
  private String  terminalId;
  private String  remarks;
  private ArrayList wtBreakList;
  private ArrayList invalidList;
//Added by Mohan for issue no.219973 on 01122010
  private ArrayList wtBreakDescList; 
  private ArrayList surChargeCurrList;
  private ArrayList wtBreakTypesList;
  private ArrayList rateTypeList;
  //End by Mohan for issue no.219973 on 01122010
  
  public ArrayList getWtBreakTypesList() {
	return wtBreakTypesList;
}


public void setWtBreakTypesList(ArrayList wtBreakTypesList) {
	this.wtBreakTypesList = wtBreakTypesList;
}


public ArrayList getSurChargeCurrList() {
	return surChargeCurrList;
}


public void setSurChargeCurrList(ArrayList surChargeCurrList) {
	this.surChargeCurrList = surChargeCurrList;
}


public ArrayList getWtBreakDescList() {
	return wtBreakDescList;
}


public void setWtBreakDescList(ArrayList wtBreakDescList) {
	this.wtBreakDescList = wtBreakDescList;
}


public void setWtBreakList(ArrayList wtBreakList)
  {
    this.wtBreakList = wtBreakList;
  }


  public ArrayList getWtBreakList()
  {
    return wtBreakList;
  }
  
   public void setRemarks(String remarks)
  {
    this.remarks = remarks;
  }


  public String getRemarks()
  {
    return remarks;
  }

  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public String getShipmentMode()
  {
    return shipmentMode;
  }


  public void setEffectiveFrom(Timestamp effectiveFrom)
  {
    this.effectiveFrom = effectiveFrom;
  }


  public Timestamp getEffectiveFrom()
  {
    return effectiveFrom;
  }


  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto = validUpto;
  }


  public Timestamp getValidUpto()
  {
    return validUpto;
  }


  public void setCurrency(String currency)
  {
    this.currency = currency;
  }


  public String getCurrency()
  {
    return currency;
  }


  public void setCarrierId(String carrierId)
  {
    this.carrierId = carrierId;
  }


  public String getCarrierId()
  {
    return carrierId;
  }


  public void setUom(String uom)
  {
    this.uom = uom;
  }


  public String getUom()
  {
    return uom;
  }


  public void setWeightBreak(String weightBreak)
  {
    this.weightBreak = weightBreak;
  }


  public String getWeightBreak()
  {
    return weightBreak;
  }


  public void setRateType(String rateType)
  {
    this.rateType = rateType;
  }


  public String getRateType()
  {
    return rateType;
  }


  public void setRateDtls(ArrayList rateDtls)
  {
    this.rateDtls = rateDtls;
  }


  public ArrayList getRateDtls()
  {
    return rateDtls;
  }


  public void setSlabValues(String[] slabValues)
  {
    this.slabValues = slabValues;
  }


  public String[] getSlabValues()
  {
    return slabValues;
  }


  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }


  public String getConsoleType()
  {
    return consoleType;
  }


  public void setUser(String user)
  {
    this.user = user;
  }


  public String getUser()
  {
    return user;
  }


  public void setCreatedTime(Timestamp createdTime)
  {
    this.createdTime = createdTime;
  }


  public Timestamp getCreatedTime()
  {
    return createdTime;
  }


  public void setWeightClass(String weightClass)
  {
    this.weightClass = weightClass;
  }


  public String getWeightClass()
  {
    return weightClass;
  }


  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }


  public String getAccessLevel()
  {
    return accessLevel;
  }


  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }


  public String getTerminalId()
  {
    return terminalId;
  }


  public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel = serviceLevel;
  }


  public String getServiceLevel()
  {
    return serviceLevel;
  }


  public void setListValues(String[] listValues)
  {
    this.listValues = listValues;
  }


  public String[] getListValues()
  {
    return listValues;
  }


  public void setInvalidList(ArrayList invalidList)
  {
    this.invalidList = invalidList;
  }


  public ArrayList getInvalidList()
  {
    return invalidList;
  }


public ArrayList getRateTypeList() {
	return rateTypeList;
}


public void setRateTypeList(ArrayList rateTypeList) {
	this.rateTypeList = rateTypeList;
}







  
  
  
}
package com.qms.setup.java;
import java.io.Serializable;
import java.util.ArrayList;

public class ZoneCodeMasterDOB implements Serializable
{
  private String originLocation        =    null;
  private String city                  =    null;
  private String state                 =    null;
  private String zipCode               =    null;  
  private String   zoneCode            =    null;
  private String   remarks             =    null;
  private String terminalId            =    null;
  private ArrayList      zoneList          =    null;
  private String shipmentMode;
  private String consoleType;
  private int rowId;
 
  public ZoneCodeMasterDOB()
  {
  }
  public ZoneCodeMasterDOB(String originLocation,String terminalId,String city,String state,String zipCode)
  {
    this.originLocation   =   originLocation;
    this.terminalId       =   terminalId;
    this.city             =   city;
    this.state            =   state;
    this.zipCode          =   zipCode;
    //this.port             =   port;
  }
  public void setOriginLocation(String originLocation)
  {
    this.originLocation    =   originLocation;
  }
  public String getOriginLocation()
  {
    return originLocation;
  }
  
  
  public void setCity(String city)
  {
    this.city    =   city;
  }
  public String getCity()
  {
    return city;
  }
  public void setState(String state)
  {
    this.state    =   state;
  }
  public String getState()
  {
    return state;
  }
  public void setZipCode(String zipCode)
  {
    this.zipCode    =   zipCode;
  }
  public String getZipCode()
  {
    return zipCode;
  }
 
 
  public void setZoneCode(String zoneCode)
  {
    this.zoneCode    =   zoneCode;
  }
  public String getZoneCode()
  {
    return zoneCode;
  }
  
  public void setZoneCodeList(ArrayList zoneList)
  {
    this.zoneList    =   zoneList;
  }
  public ArrayList getZoneCodeList()
  {
    return zoneList;
  }
  public void setRemarks(String remarks)
  {
    this.remarks    =   remarks;
  }
  public String getRemarks()
  {
    return remarks;
  }


  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }


  public String getTerminalId()
  {
    return terminalId;
  }

  public String getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }

  public String getConsoleType()
  {
    return consoleType;
  }

  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }

  public int getRowId()
  {
    return rowId;
  }

  public void setRowId(int rowId)
  {
    this.rowId = rowId;
  }
  
}
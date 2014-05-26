package com.qms.setup.java;
import java.io.Serializable;

public class ZoneCodeChildDOB implements Serializable 
{

  private String fromZipCode         =    null;
  private String toZipCode           =    null;
  private String zone                =    null;
  private String estimationTime      =    null;
  private String estimatedDistance   =    null;
  private String alphaNumaric        =    null;
  private String zoneCode           =    null;
  private String rowNo               =    null;
  private String invalidate          =    null;
  private String remarks             =    null;
  
  public ZoneCodeChildDOB()
  {
  }
  
  public ZoneCodeChildDOB(String alphaNumaric,String fromZipCode,String toZipCode,String zone,String estimationTime,String estimatedDistance)
  {
     this.alphaNumaric         =    alphaNumaric;
     this.fromZipCode         =    fromZipCode;
     this.toZipCode         =    toZipCode;
     this.zone         =    zone;
     this.estimationTime         =    estimationTime;
     this.estimatedDistance         =    estimatedDistance;
  }
  
   public void setFromZipCode(String fromZipCode)
  {
    this.fromZipCode    =   fromZipCode;
  }
  public String getFromZipCode()
  {
    return fromZipCode;
  }
  public void setToZipCode(String toZipCode)
  {
    this.toZipCode    =   toZipCode;
  }
  public String getToZipCode()
  {
    return toZipCode;
  }
  public void setZone(String zone)
  {
    this.zone    =   zone;
  }
  public String getZone()
  {
    return zone;
  }
  public void setEstimationTime(String estimationTime)
  {
    this.estimationTime    =   estimationTime;
  }
  public String getEstimationTime()
  {
    return estimationTime;
  }
  public void setEstimatedDistance(String estimatedDistance)
  {
    this.estimatedDistance    =   estimatedDistance;
  }
  public String getEstimatedDistance()
  {
    return estimatedDistance;
  }
  public void setAlphaNumaric(String alphaNumaric)
  {
    this.alphaNumaric    =   alphaNumaric;
  }
  public String getAlphaNumaric()
  {
    return alphaNumaric;
  }
  public void setZoneCode(String zoneCode)
  {
    this.zoneCode    =   zoneCode;
  }
  public String getZoneCode()
  {
    return zoneCode;
  }
  public void setRowNo(String rowNo)
  {
    this.rowNo    =   rowNo;
  }
  public String getRowNo()
  {
    return rowNo;
  }
  public void setInvalidate(String invalidate)
  {
    this.invalidate    =   invalidate;
  }
  public String getInvalidate()
  {
    return invalidate;
  }


  public void setRemarks(String remarks)
  {
    this.remarks = remarks;
  }


  public String getRemarks()
  {
    return remarks;
  }
}
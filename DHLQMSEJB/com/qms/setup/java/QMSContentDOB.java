package com.qms.setup.java;
import java.io.Serializable;

public class QMSContentDOB implements Serializable
{
  public QMSContentDOB()
  {
  }
  private int  shipment;
  private String  headerFooter;
  private String  contentId;
  private String  contentDescription;
  private String  defaultFlag;
  private String  terminalId;
  private String  remarks;
  private String  accessLevel;
private String  invalidate;
  
  
  public void setInvalidate(String invalidate)
  {
    this.invalidate=invalidate;
  }
  public String getInvalidate()
  {
    return invalidate;
  }
  public void setRemarks(String remarks)
  {
    this.remarks=remarks;
  }
  public String getRemarks()
  {
    return remarks;
  }
  
  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel=accessLevel;
  }
  public String getAccessLevel()
  {
    return accessLevel;
  }
  public void setTerminalId(String terminalId)
  {
    this.terminalId=terminalId;
  }
  public String getTerminalId()
  {
    return terminalId;
  }
  public void setShipmentMode(int shipment)
  { 
    this.shipment=shipment;
    
  }
  public int getShipmentMode()
  {
    return shipment;
  }
  public void setHeaderFooter(String headerFooter)
  {
    this.headerFooter=headerFooter;
  }
  public String getHeaderFooter()
  {
    return headerFooter;
  }
  public void setContentId(String contentId)
  {
    this.contentId=contentId;
  }
  public String getContentId()
  {
    return contentId;
  }
  public void setContentDescription(String contentDescription)
  {
    this.contentDescription=contentDescription;
  }
  public String getContentDescription()
  {
    return contentDescription;
  }
  public void setDefaultFlag(String defaultFlag)
  {
    this.defaultFlag=defaultFlag;
  }
  public String getDefaultFlag()
  {
    return defaultFlag;
  }
}
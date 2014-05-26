/**
*
* Copyright (c) 2007-2008 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
* @version 
* @author--P.Kameswari
* @Created Date--20/01/2007
* 
*/
package com.qms.setup.java;
import java.io.Serializable;
/**
 * This class will be useful to store the attachment criteria of attachmentId in attachment master. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 */
public class QMSAttachmentDetailDOB implements Serializable
{
  private String shipmentMode;
  private String consoleType;
  private String quoteType;
  private String defaultFlag;
  private String fromCountry;
  private String fromLocation;
  private String toCountry;
  private String toLocation;
  private String carrierId;
  private String serviceLevelId;
  private String industry; 
  private long id;
  private String attachmentId;
  public QMSAttachmentDetailDOB()
  {
  }


  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public String getShipmentMode()
  {
    return shipmentMode;
  }


  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }


  public String getConsoleType()
  {
    return consoleType;
  }


  public void setQuoteType(String quoteType)
  {
    this.quoteType = quoteType;
  }


  public String getQuoteType()
  {
    return quoteType;
  }


  public void setDefaultFlag(String defaultFlag)
  {
    this.defaultFlag = defaultFlag;
  }


  public String getDefaultFlag()
  {
    return defaultFlag;
  }


 



  public void setId(long id)
  {
    this.id = id;
  }


  public long getId()
  {
    return id;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId)
  {
    this.attachmentId = attachmentId;
  }


  public void setFromCountry(String fromCountry)
  {
    this.fromCountry = fromCountry;
  }


  public String getFromCountry()
  {
    return fromCountry;
  }


  public void setFromLocation(String fromLocation)
  {
    this.fromLocation = fromLocation;
  }


  public String getFromLocation()
  {
    return fromLocation;
  }


  public void setToCountry(String toCountry)
  {
    this.toCountry = toCountry;
  }


  public String getToCountry()
  {
    return toCountry;
  }


  public void setToLocation(String toLocation)
  {
    this.toLocation = toLocation;
  }


  public String getToLocation()
  {
    return toLocation;
  }


  public void setCarrierId(String carrierId)
  {
    this.carrierId = carrierId;
  }


  public String getCarrierId()
  {
    return carrierId;
  }


  public void setServiceLevelId(String serviceLevelId)
  {
    this.serviceLevelId = serviceLevelId;
  }


  public String getServiceLevelId()
  {
    return serviceLevelId;
  }


  public void setIndustry(String industry)
  {
    this.industry = industry;
  }


  public String getIndustry()
  {
    return industry;
  }
}
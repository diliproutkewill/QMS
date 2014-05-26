package com.qms.operations.costing.dob;

import java.io.Serializable;
import java.sql.Timestamp;

public class CostingHDRDOB implements Serializable
{
   private String origin;
   private String destination;
   private String advantage;
   private String customerid;
   private String quoteid;
   private Timestamp validtill;
   
   private double  noOfPieces;
   private String  uom;
   private double  actualWeight;
   private double  volume;
   private double  invValue;
   private String  invCurrency;
   private String  baseCurrency;
   private String  volumeUom;
   
   private String incoterms;
   private String commodityType;
   private String versionNo;
   private String terminalId;
   private String userId;
   private String emailId;
   private String originCountry;
   private String destCountry;
   
   private String accessLevel;
  private String notes;
private String salesPersonEmail;//added by subrahmanyam for 146444   
 private int shipmentMode; // added by VLAKSHMI for the WPBN issue-154389
   private String companyName;// added by VLAKSHMI for the WPBN issue-154389
   private long quoteId;
   private String serviceLevel;
   private String carrier;
   private String laneNo;
   private java.util.ArrayList quoteLanes;
   
  public CostingHDRDOB()
  {
  }


  public void setOrigin(String origin)
  {
    this.origin = origin;
  }


  public String getOrigin()
  {
    return origin;
  }


  public void setDestination(String destination)
  {
    this.destination = destination;
  }


  public String getDestination()
  {
    return destination;
  }


  public void setAdvantage(String advantage)
  {
    this.advantage = advantage;
  }


  public String getAdvantage()
  {
    return advantage;
  }


  public void setCustomerid(String customerid)
  {
    this.customerid = customerid;
  }


  public String getCustomerid()
  {
    return customerid;
  }


  public void setQuoteid(String quoteid)
  {
    this.quoteid = quoteid;
  }


  public String getQuoteid()
  {
    return quoteid;
  }


  public void setValidtill(Timestamp validtill)
  {
    this.validtill = validtill;
  }


  public Timestamp getValidtill()
  {
    return validtill;
  }


  public void setNoOfPieces(double noOfPieces)
  {
    this.noOfPieces = noOfPieces;
  }


  public double getNoOfPieces()
  {
    return noOfPieces;
  }


  public void setUom(String uom)
  {
    this.uom = uom;
  }


  public String getUom()
  {
    return uom;
  }


 


  public void setInvCurrency(String invCurrency)
  {
    this.invCurrency = invCurrency;
  }


  public String getInvCurrency()
  {
    return invCurrency;
  }


  public void setBaseCurrency(String baseCurrency)
  {
    this.baseCurrency = baseCurrency;
  }


  public String getBaseCurrency()
  {
    return baseCurrency;
  }


  public void setActualWeight(double actualWeight)
  {
    this.actualWeight = actualWeight;
  }


  public double getActualWeight()
  {
    return actualWeight;
  }


  public void setVolume(double volume)
  {
    this.volume = volume;
  }


  public double getVolume()
  {
    return volume;
  }


  public void setInvValue(double invValue)
  {
    this.invValue = invValue;
  }


  public double getInvValue()
  {
    return invValue;
  }


  public void setVolumeUom(String volumeUom)
  {
    this.volumeUom = volumeUom;
  }


  public String getVolumeUom()
  {
    return volumeUom;
  }


  public void setIncoterms(String incoterms)
  {
    this.incoterms = incoterms;
  }


  public String getIncoterms()
  {
    return incoterms;
  }


  public void setCommodityType(String commodityType)
  {
    this.commodityType = commodityType;
  }


  public String getCommodityType()
  {
    return commodityType;
  }


  public void setVersionNo(String versionNo)
  {
    this.versionNo = versionNo;
  }


  public String getVersionNo()
  {
    return versionNo;
  }


  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }


  public String getTerminalId()
  {
    return terminalId;
  }


  public void setUserId(String userId)
  {
    this.userId = userId;
  }


  public String getUserId()
  {
    return userId;
  }


  public void setEmailId(String emailId)
  {
    this.emailId = emailId;
  }


  public String getEmailId()
  {
    return emailId;
  }
   public void setOriginCountry(String originCountry)
  {
    this.originCountry = originCountry;
  }


  public String getOriginCountry()
  {
    return originCountry;
  }


  public void setDestCountry(String destCountry)
  {
    this.destCountry = destCountry;
  }


  public String getDestCountry()
  {
    return destCountry;
  }


  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }


  public String getAccessLevel()
  {
    return accessLevel;
  }

  public String getNotes()
  {
    return notes;
  }

  public void setNotes(String notes)
  {
    this.notes = notes;
  }
  //@@Added by subrahmanyam for the Enhancement 146444 on 10/02/09
      public String getSalesPersonEmail()
  {
    return salesPersonEmail;
  }

  public void setSalesPersonEmail(String salesPersonEmail)
  {
    this.salesPersonEmail = salesPersonEmail;
  }

 //@@Ended by subrahmanyam for the Enhancement 146444 on 10/02/09
  // Added by VLAKSHMI for the WPBN issue-154389
  public void setShipmentMode(int shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public int getShipmentMode()
  {
    return shipmentMode;
  }


  public void setCompanyName(String companyName)
  {
    this.companyName = companyName;
  }


  public String getCompanyName()
  {
    return companyName;
  }
  // Added by VLAKSHMI for the WPBN issue-154389


/**
 * @return the carrier
 */
public String getCarrier() {
	return carrier;
}


/**
 * @param carrier the carrier to set
 */
public void setCarrier(String carrier) {
	this.carrier = carrier;
}


/**
 * @return the laneNo
 */
public String getLaneNo() {
	return laneNo;
}


/**
 * @param laneNo the laneNo to set
 */
public void setLaneNo(String laneNo) {
	this.laneNo = laneNo;
}


/**
 * @return the quoteId
 */
public long getQuoteId() {
	return quoteId;
}


/**
 * @param quoteId the quoteId to set
 */
public void setQuoteId(long quoteId) {
	this.quoteId = quoteId;
}


/**
 * @return the serviceLevel
 */
public String getServiceLevel() {
	return serviceLevel;
}


/**
 * @param serviceLevel the serviceLevel to set
 */
public void setServiceLevel(String serviceLevel) {
	this.serviceLevel = serviceLevel;
}


/**
 * @return the quoteLanes
 */
public java.util.ArrayList getQuoteLanes() {
	return quoteLanes;
}


/**
 * @param quoteLanes the quoteLanes to set
 */
public void setQuoteLanes(java.util.ArrayList quoteLanes) {
	this.quoteLanes = quoteLanes;
}



}
package com.qms.reports.java;
import java.io.Serializable;
import java.sql.Timestamp;

public class UpdatedQuotesReportDOB implements Serializable
{
  private String impFlag;
  private String customerId;
  private long prevQuoteId;
  private String shipmentMode;
  private String serviceLevel;
  private String originLocation;
  private String originCountry;
  private String destLocation;
  private String destCountry;
  private long uniqueId;
  //private long quoteId;  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
  private String quoteId;  //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
  private String sellBuyFlag;
  private String changeDesc;
  private long newQuoteId;
  private String faxEmailPrintFlag;
  private String customerName;//added by kameswari for the WPBN issue-30313
  private String createdBy;//Added by Anil.k for Excel page
  private String salesPerson;//Added by Anil.k for Excel page
  private Timestamp createdDate;//Added by Anil.k for Excel page
  private String createdDateStr;
  private String isMultiQuote;//Added by Anil.k for CR 231104
  private String dontModify;//Added by Anil.k for CR 231104
//Added by Anil.k for CR 231104
  public String getDontModify() {
	return dontModify;
}

public void setDontModify(String dontModify) {
	this.dontModify = dontModify;
}//Ended by Anil.k for CR 231104

  public String getImpFlag()
  {
    return impFlag;
  }

  public void setImpFlag(String impFlag)
  {
    this.impFlag = impFlag;
  }

  public String getCustomerId()
  {
    return customerId;
  }

  public void setCustomerId(String customerId)
  {
    this.customerId = customerId;
  }

  public long getPrevQuoteId()
  {
    return prevQuoteId;
  }

  public void setPrevQuoteId(long prevQuoteId)
  {
    this.prevQuoteId = prevQuoteId;
  }

  public String getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }

  public String getServiceLevel()
  {
    return serviceLevel;
  }

  public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel = serviceLevel;
  }

  public String getOriginLocation()
  {
    return originLocation;
  }

  public void setOriginLocation(String originLocation)
  {
    this.originLocation = originLocation;
  }

  public String getOriginCountry()
  {
    return originCountry;
  }

  public void setOriginCountry(String originCountry)
  {
    this.originCountry = originCountry;
  }

  public String getDestLocation()
  {
    return destLocation;
  }

  public void setDestLocation(String destLocation)
  {
    this.destLocation = destLocation;
  }

  public String getDestCountry()
  {
    return destCountry;
  }

  public void setDestCountry(String destCountry)
  {
    this.destCountry = destCountry;
  }

  public long getUniqueId()
  {
    return uniqueId;
  }

  public void setUniqueId(long uniqueId)
  {
    this.uniqueId = uniqueId;
  }
//@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
  /*public long getQuoteId()
  {
    return quoteId;
  }

  public void setQuoteId(long quoteId)
  {
    this.quoteId = quoteId;
  }*/
//@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
public String getQuoteId()
  {
    return quoteId;
  }

  public void setQuoteId(String quoteId)
  {
    this.quoteId = quoteId;
  }
//@@ Ended by subrahmanyam for the enhancement #146971 on 03/12/2008
  public String getSellBuyFlag()
  {
    return sellBuyFlag;
  }

  public void setSellBuyFlag(String sellBuyFlag)
  {
    this.sellBuyFlag = sellBuyFlag;
  }

  public String getChangeDesc()
  {
    return changeDesc;
  }

  public void setChangeDesc(String changeDesc)
  {
    this.changeDesc = changeDesc;
  }

  public long getNewQuoteId()
  {
    return newQuoteId;
  }

  public void setNewQuoteId(long newQuoteId)
  {
    this.newQuoteId = newQuoteId;
  }

  public String getFaxEmailPrintFlag()
  {
    return faxEmailPrintFlag;
  }

  public void setFaxEmailPrintFlag(String faxEmailPrintFlag)
  {
    this.faxEmailPrintFlag = faxEmailPrintFlag;
  }
  
  //@@added by kameswari for the WPBN issue-30313
  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }
//Added by Anil.k for CR 231104
public String getIsMultiQuote() {
	return isMultiQuote;
}

public void setIsMultiQuote(String isMultiQuote) {
	this.isMultiQuote = isMultiQuote;
}//Ended by Anil.k for CR 231104 on 31Jan2011
//Added by Anil.k for Excel page
public String getCreatedBy() {
	return createdBy;
}

public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}

public String getSalesPerson() {
	return salesPerson;
}

public void setSalesPerson(String salesPerson) {
	this.salesPerson = salesPerson;
}

public Timestamp getCreatedDate() {
	return createdDate;
}

public void setCreatedDate(Timestamp createdDate) {
	this.createdDate = createdDate;
}//Ended by Anil.k for Excel page

//modified by silpa.p
public String getCreatedDateStr() {
	return createdDateStr;
}

public void setCreatedDateStr(String createdDate) {
	this.createdDateStr = createdDate;
}
//@@WPBN-30313
}
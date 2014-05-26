package com.qms.reports.java;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class QMSRatesReportDOB  implements Serializable
{
  private String customerId;
  private String customerName;
  private String origin;
  private String destination;
  private String originCountry;
  private String destCountry;
  private String carrierId;
  private String serviceLevelId;
  private String frequency;
  private String transitTime;
  private String quoteId;
  private ArrayList wtBreakList;
  private ArrayList chargeRateList;
  private String shipmentMode;
  private String currency;
  private String consoleType;
  private String sellBuyFlag;
  private Timestamp validUpto;
  private String[] rateList;
  private String wtBreak;
  private String wtBreakSlab;
  //added by phani sekhar for CR 167656
  private String rateType;
  private String weightBreak;
  private Timestamp frmDate;
  private Timestamp toDate;
  private String terminalId;
  private String acessLevel;
 private Timestamp creationDate;
  private Timestamp quoteValidDate;
 private Timestamp freightValidDate;
  private String densityCode;
   private String densityType;
   private String internalNotes;//Added by Mohan for issue 219976 on 01112010
   private String externalNotes;//Added by Mohan for issue 219976 on 01112010
  //ends 167656
    private Timestamp quoteValidUpto;//ADDED BY SUBRAHMANYAM FOR 181328
    //Added by Anil.k for Issue 236362 on 24Feb2011
    private Timestamp customerReqDate;
    private String createdBy;
    private String salesPerson;
    private Timestamp modifiedDate;
  //Ended by Anil.k for Issue 236362 on 24Feb2011
    private String multiQuote;

  public QMSRatesReportDOB()
  {
  }

  public String getCustomerId()
  {
    return customerId;
  }

  public void setCustomerId(String customerId)
  {
    this.customerId = customerId;
  }

  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }

  public String getOrigin()
  {
    return origin;
  }

  public void setOrigin(String origin)
  {
    this.origin = origin;
  }

  public String getDestination()
  {
    return destination;
  }

  public void setDestination(String destination)
  {
    this.destination = destination;
  }

  public String getOriginCountry()
  {
    return originCountry;
  }

  public void setOriginCountry(String originCountry)
  {
    this.originCountry = originCountry;
  }

  public String getDestCountry()
  {
    return destCountry;
  }

  public void setDestCountry(String destCountry)
  {
    this.destCountry = destCountry;
  }

  public String getCarrierId()
  {
    return carrierId;
  }

  public void setCarrierId(String carrierId)
  {
    this.carrierId = carrierId;
  }

  public String getServiceLevelId()
  {
    return serviceLevelId;
  }

  public void setServiceLevelId(String serviceLevelId)
  {
    this.serviceLevelId = serviceLevelId;
  }

  public String getFrequency()
  {
    return frequency;
  }

  public void setFrequency(String frequency)
  {
    this.frequency = frequency;
  }

  public String getTransitTime()
  {
    return transitTime;
  }

  public void setTransitTime(String transitTime)
  {
    this.transitTime = transitTime;
  }

  public String getQuoteId()
  {
    return quoteId;
  }

  public void setQuoteId(String quoteId)
  {
    this.quoteId = quoteId;
  }

  public ArrayList getWtBreakList()
  {
    return wtBreakList;
  }

  public void setWtBreakList(ArrayList wtBreakList)
  {
    this.wtBreakList = wtBreakList;
  }

  public ArrayList getChargeRateList()
  {
    return chargeRateList;
  }

  public void setChargeRateList(ArrayList chargeRateList)
  {
    this.chargeRateList = chargeRateList;
  }

  public String getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency(String currency)
  {
    this.currency = currency;
  }

  public String getConsoleType()
  {
    return consoleType;
  }

  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }

  public String getSellBuyFlag()
  {
    return sellBuyFlag;
  }

  public void setSellBuyFlag(String sellBuyFlag)
  {
    this.sellBuyFlag = sellBuyFlag;
  }

  public Timestamp getValidUpto()
  {
    return validUpto;
  }

  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto = validUpto;
  }

  public String[] getRateList()
  {
    return rateList;
  }

  public void setRateList(String[] rateList)
  {
    this.rateList = rateList;
  }

  public String getWtBreak()
  {
    return wtBreak;
  }

  public void setWtBreak(String wtBreak)
  {
    this.wtBreak = wtBreak;
  }

  public String getWtBreakSlab()
  {
    return wtBreakSlab;
  }

  public void setWtBreakSlab(String wtBreakSlab)
  {
    this.wtBreakSlab = wtBreakSlab;
  }
  public void setRateType(String rateType)
  {
    this.rateType = rateType;
  }


  public String getRateType()
  {
    return rateType;
  }


  public void setWeightBreak(String weightBreak)
  {
    this.weightBreak = weightBreak;
  }


  public String getWeightBreak()
  {
    return weightBreak;
  }


  public void setFrmDate(Timestamp frmDate)
  {
    this.frmDate = frmDate;
  }


  public Timestamp getFrmDate()
  {
    return frmDate;
  }


  public void setToDate(Timestamp toDate)
  {
    this.toDate = toDate;
  }


  public Timestamp getToDate()
  {
    return toDate;
  }


  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }


  public String getTerminalId()
  {
    return terminalId;
  }


  public void setAcessLevel(String acessLevel)
  {
    this.acessLevel = acessLevel;
  }


  public String getAcessLevel()
  {
    return acessLevel;
  }
    public void setCreationDate(Timestamp creationDate)
  {
    this.creationDate = creationDate;
  }


  public Timestamp getCreationDate()
  {
    return creationDate;
  }
    public void setQuoteValidDate(Timestamp quoteValidDate)
  {
    this.quoteValidDate = quoteValidDate;
  }


  public Timestamp getQuoteValidDate()
  {
    return quoteValidDate;
  }


  public void setFreightValidDate(Timestamp freightValidDate)
  {
    this.freightValidDate = freightValidDate;
  }


  public Timestamp getFreightValidDate()
  {
    return freightValidDate;
  }

  public void setDensityCode(String densityCode)
  {
    this.densityCode = densityCode;
  }


  public String getDensityCode()
  {
    return densityCode;
  }


  public void setDensityType(String densityType)
  {
    this.densityType = densityType;
  }


  public String getDensityType()
  {
    return densityType;
  }
      //added by subrahmanyam for 181328 on 12/09/09
  public Timestamp getQuoteValidUpto()
  {
    return quoteValidUpto;
  }

  public void setQuoteValidUpto(Timestamp quoteValidUpto)
  {
    this.quoteValidUpto = quoteValidUpto;
  }
//ended for 181328

/**
 * @return the extternalNotes
 */
public String getExternalNotes() {
	return externalNotes;
}

/**
 * @param extternalNotes the extternalNotes to set
 */
public void setExternalNotes(String externalNotes) {
	this.externalNotes = externalNotes;
}

/**
 * @return the internalNotes
 */
public String getInternalNotes() {
	return internalNotes;
}

/**
 * @param internalNotes the internalNotes to set
 */
public void setInternalNotes(String internalNotes) {
	this.internalNotes = internalNotes;
}
//Added by Anil.k for Issue 236362 on 24Feb2011
public Timestamp getCustomerReqDate() {
	return customerReqDate;
}

public void setCustomerReqDate(Timestamp customerReqDate) {
	this.customerReqDate = customerReqDate;
}

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

public Timestamp getModifiedDate() {
	return modifiedDate;
}

public void setModifiedDate(Timestamp modifiedDate) {
	this.modifiedDate = modifiedDate;
}
//Ended by Anil.k for Issue 236362 on 24Feb2011

public String getMultiQuote() {
	return multiQuote;
}

public void setMultiQuote(String multiQuote) {
	this.multiQuote = multiQuote;
}

}
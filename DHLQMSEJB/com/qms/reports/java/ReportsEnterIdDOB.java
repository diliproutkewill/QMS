package com.qms.reports.java;
import java.sql.Timestamp;


public class ReportsEnterIdDOB implements java.io.Serializable
{
//attributes for buyrates expiry report//
  private String shipmentMode = "";
  private String carrierId    = "";
  private String serviceLevel = "";
  private String expiryActiveIndicator ="";
  private String expiryActivePeriod    ="";
  private String basis        = "";
  private int    basisCount   = 0;
  
  private String currentDate = null;
  private String fromDate    = null;
  private String toDate      = null;
  private Timestamp fromDate1 = null;
  private Timestamp toDate1      = null;
//end of attributes for buyrates expiry report//
  
  //pending quote report//
  private String consoleType  = "";
  private String primaryOption  = "";
  private String customerId = "";
  private String orginLocation  = "";
  private String destLocation   = "";
  private String salesPersonId  ="";
  //end for pending quore report//
  
  private String chargeDescription  = "";
  private int quotesConfirmed       = 0;
  private int quotesNotConfirmed    = 0;
  private int totalQuotes           = 0;
  
  //Activity Report//
  
  private String countryId        = "";
  private String locationId       = "";
  private String quoteStatus      = "";
  private String fromCountry      = "";
  private String toCountry        = "";
  //end activity report
  
  private String terminalId   = "";
  private String userId       = "";
  private String dateFormat   = "";
  private String approvedFlag = "";
  
  private int noofrecords     = 0;
  private int pageNo          = 0;
  private String userDateFormat;
  private String sortBy       = "";
  private String sortOrder    = "";
  private String  formateSerch  = "";
  private String loginTerminal;
  private String autoUpdated="";
  private String Weight_break = null;
  private String Rate_type = null;
  
  
  public ReportsEnterIdDOB()
  {
  }
  public void setFormateSerch(String formateSerch)
  {
    this.formateSerch =  formateSerch;
  }
  public String getFormateSerch()
  {
    return formateSerch;
  }
  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode=  shipmentMode;
  }
  public String getShipmentMode()
  {
    return this.shipmentMode;
  }

  public void setCarrierId(String carrierId)
  {
    this.carrierId=  carrierId;
  }
  public String getCarrierId()
  {
    return this.carrierId;
  }
  
  public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel=  serviceLevel;
  }
  public String getServiceLevel()
  {
    return this.serviceLevel;
  }

  public void setExpiryActiveIndicator(String expiryActiveIndicator)
  {
    this.expiryActiveIndicator=  expiryActiveIndicator;
  }
  public String getExpiryActiveIndicator()
  {
    return this.expiryActiveIndicator;
  }

  public void setExpiryActivePeriod(String expiryActivePeriod)
  {
    this.expiryActivePeriod=  expiryActivePeriod;
  }
  public String getExpiryActivePeriod()
  {
    return this.expiryActivePeriod;
  }

  public void setBasis(String basis)
  {
    this.basis=  basis;
  }
  public String getBasis()
  {
    return this.basis;
  }

  public void setBasisCount(int basisCount)
  {
    this.basisCount=  basisCount;
  }
  public int getBasisCount()
  {
    return this.basisCount;
  }

   public void setCurrentDate(String currentDate)
   {
     this.currentDate = currentDate;
   }
   public String getCurrentDate()
   {
     return this.currentDate;
   }
   
  public void setFromDate(String fromDate)
  {
    this.fromDate=  fromDate;
  }
  public String getFromDate()
  {
    return this.fromDate;
  }

  public void setToDate(String toDate)
  {
    this.toDate=  toDate;
  }
  public String getToDate()
  {
    return this.toDate;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId=  terminalId;
  }
  public String getTerminalId()
  {
    return this.terminalId;
  }  
  
  public void setDateFormat(String dateFormat)
  {
    this.dateFormat = dateFormat;
  }
  public String getDateFormat()
  {
    return this.dateFormat;
  }
  public void setConsoleType(String consoleType)
  {
    this.consoleType = consoleType;
  }
  public String getConsoleType()
  {
    return this.consoleType;
  }
  
  public void setPrimaryOption(String primaryOption)
  {
    this.primaryOption = primaryOption;
  }
  public String getPrimaryOption()
  {
    return this.primaryOption;
  }
  

  public void setCustomerId(String customerId)
  {
    this.customerId = customerId;
  }
  public String getCustomerId()
  {
    return this.customerId;
  }
  

  public void setOrginLocation(String orginLocation)
  {
    this.orginLocation = orginLocation;
  }
  public String getOrginLocation()
  {
    return this.orginLocation;
  }
  

  public void setDestLocation(String destLocation)
  {
    this.destLocation = destLocation;
  }
  public String getDestLocation()
  {
    return this.destLocation;
  }
  

  public void setSalesPersonId(String salesPersonId)
  {
    this.salesPersonId = salesPersonId;
  }
  public String getSalesPersonId()
  {
    return this.salesPersonId;
  }
  
  public void setCountryId(String countryId)
  {
    this.countryId = countryId;
  }
  public String getCountryId()
  {
    return this.countryId;
  }
  
  public void setLocationId(String locationId)
  {
    this.locationId = locationId;
  }
  public String getLocationId()
  {
    return this.locationId;
  }
  
  public void setQuoteStatus(String quoteStatus)
  {
    this.quoteStatus = quoteStatus;
  }
  public String getQuoteStatus()
  {
    return this.quoteStatus;
  }
  
  public void setFromCountry(String fromCountry)
  {
    this.fromCountry = fromCountry;
  }
  public String getFromCountry()
  {
    return this.fromCountry;
  }
  
  public void setToCountry(String toCountry)
  {
    this.toCountry = toCountry;
  }
  public String getToCountry()
  {
    return this.toCountry;
  }  


  public void setChargeDescription(String chargeDescription)
  {
    this.chargeDescription = chargeDescription;
  }


  public String getChargeDescription()
  {
    return chargeDescription;
  }


  public void setQuotesConfirmed(int quotesConfirmed)
  {
    this.quotesConfirmed = quotesConfirmed;
  }


  public int getQuotesConfirmed()
  {
    return quotesConfirmed;
  }


  public void setQuotesNotConfirmed(int quotesNotConfirmed)
  {
    this.quotesNotConfirmed = quotesNotConfirmed;
  }


  public int getQuotesNotConfirmed()
  {
    return quotesNotConfirmed;
  }


  public void setTotalQuotes(int totalQuotes)
  {
    this.totalQuotes = totalQuotes;
  }


  public int getTotalQuotes()
  {
    return totalQuotes;
  }


  public void setUserId(String userId)
  {
    this.userId = userId;
  }


  public String getUserId()
  {
    return userId;
  }


  public void setFromDate1(Timestamp fromDate1)
  {
    this.fromDate1 = fromDate1;
  }


  public Timestamp getFromDate1()
  {
    return fromDate1;
  }


  public void setToDate1(Timestamp toDate1)
  {
    this.toDate1 = toDate1;
  }


  public Timestamp getToDate1()
  {
    return toDate1;
  }
  
   public void setApprovedFlag(String approvedFlag)
  {
    this.approvedFlag = approvedFlag;
  }


  public String getApprovedFlag()
  {
    return approvedFlag;
  }
  public void setNoOfRecords(int noofrecords)
  {
    this.noofrecords = noofrecords;
  }

  public int getNoOfRecords()
  {
    return noofrecords;
  }
    public void setPageNo(int pageNo)
  {
    this.pageNo = pageNo;
  }

  public int getPageNo()
  {
    return pageNo;
  }

  public String getUserDateFormat()
  {
    return userDateFormat;
  }

  public void setUserDateFormat(String userDateFormat)
  {
    this.userDateFormat = userDateFormat;
  }


  public void setSortBy(String sortBy)
  {
    this.sortBy = sortBy;
  }


  public String getSortBy()
  {
    return sortBy;
  }


  public void setSortOrder(String sortOrder)
  {
    this.sortOrder = sortOrder;
  }


  public String getSortOrder()
  {
    return sortOrder;
  }

  public String getLoginTerminal()
  {
    return loginTerminal;
  }

  public void setLoginTerminal(String loginTerminal)
  {
    this.loginTerminal = loginTerminal;
  }

//@@Added by VLAKHSMI for the WPBN issue-154390
  public void setAutoUpdated(String autoUpdated)
  {
    this.autoUpdated = autoUpdated;
  }


  public String getAutoUpdated()
  {
    return autoUpdated;
  }
  //@@WPBN issue-154390
//@@Added by Kameswari for the WPBN issue-71825
public String getWeight_break() {
	return Weight_break;
}
public void setWeight_break(String weight_break) {
	Weight_break = weight_break;
}
public String getRate_type() {
	return Rate_type;
}
public void setRate_type(String rate_type) {
	Rate_type = rate_type;
}

  //@@WPBN issue-71825
}
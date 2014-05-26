package com.qms.operations.costing.dob;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
   
public class CostingMasterDOB implements Serializable
{

  private ArrayList originList;
  private ArrayList destinationList;
  private ArrayList costingLegDetailsList;
  
   private String incoterms;
   private String commodityType;
   private String versionNo;
   private String origin;
   private String destination;
   private String customerid;
   private String emailId;
  
   private String originTotal;
   private String destTotal;
   private String frtTotal;
   private String total;

   private String orginCountry;
   private String destCountry;
   
   private String fromMailId;
   
   private ArrayList externalNotes;
   private String shipmentMode;
  private String creatorDetails;
  private String terminalAddress;
  private String[] contactEmailIds;
  private String[] contactPersonNames;
  private String originCountryName;
  private String destCountryName;
  private String customerName;
  private Timestamp dateOfQuotation;
  private String emailFlag;
  private String faxFlag;
  private String printFlag;
  private String[] contactsFax;
  private String customerCountryId;
  private String customerFax;
  private String[] contactPersonIds;
  private ArrayList exchangeRatesList;
  //@@Added by Kameswari for the WPBN issue-61303
  private String phoneNo;
  private String mobileNo;
  private String faxNo;
  private String companyName;
  private String extNotes;//Added by Rakesh on 21-03-2011
  private Timestamp effectiveFrom;
  private Timestamp validityOfQuote; // Added by Gowtham.
 //@@WPBN issue-61303
  public CostingMasterDOB()
  {
  }


  public void setOriginList(ArrayList originList)
  {
    this.originList = originList;
  }


  public ArrayList getOriginList()
  {
    return originList;
  }


  public void setDestinationList(ArrayList destinationList)
  {
    this.destinationList = destinationList;
  }


  public ArrayList getDestinationList()
  {
    return destinationList;
  }


  public void setCostingLegDetailsList(ArrayList costingLegDetailsList)
  {
    this.costingLegDetailsList = costingLegDetailsList;
  }


  public ArrayList getCostingLegDetailsList()
  {
    return costingLegDetailsList;
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


  public void setCustomerid(String customerid)
  {
    this.customerid = customerid;
  }


  public String getCustomerid()
  {
    return customerid;
  }


  public void setEmailId(String emailId)
  {
    this.emailId = emailId;
  }


  public String getEmailId()
  {
    return emailId;
  }


  public void setOriginTotal(String originTotal)
  {
    this.originTotal = originTotal;
  }


  public String getOriginTotal()
  {
    return originTotal;
  }


  public void setDestTotal(String destTotal)
  {
    this.destTotal = destTotal;
  }


  public String getDestTotal()
  {
    return destTotal;
  }


  public void setFrtTotal(String frtTotal)
  {
    this.frtTotal = frtTotal;
  }


  public String getFrtTotal()
  {
    return frtTotal;
  }


  public void setTotal(String total)
  {
    this.total = total;
  }


  public String getTotal()
  {
    return total;
  }


  public void setOrginCountry(String orginCountry)
  {
    this.orginCountry = orginCountry;
  }


  public String getOrginCountry()
  {
    return orginCountry;
  }


  public void setDestCountry(String destCountry)
  {
    this.destCountry = destCountry;
  }


  public String getDestCountry()
  {
    return destCountry;
  }


  public void setFromMailId(String fromMailId)
  {
    this.fromMailId = fromMailId;
  }


  public String getFromMailId()
  {
    return fromMailId;
  }


  public void setExternalNotes(ArrayList externalNotes)
  {
    this.externalNotes = externalNotes;
  }


  public ArrayList getExternalNotes()
  {
    return externalNotes;
  }


  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public String getShipmentMode()
  {
    return shipmentMode;
  }

  public String getCreatorDetails()
  {
    return creatorDetails;
  }

  public void setCreatorDetails(String creatorDetails)
  {
    this.creatorDetails = creatorDetails;
  }

  public String getTerminalAddress()
  {
    return terminalAddress;
  }

  public void setTerminalAddress(String terminalAddress)
  {
    this.terminalAddress = terminalAddress;
  }

  public String[] getContactEmailIds()
  {
    return contactEmailIds;
  }

  public void setContactEmailIds(String[] contactEmailIds)
  {
    this.contactEmailIds = contactEmailIds;
  }

  public String[] getContactPersonNames()
  {
    return contactPersonNames;
  }

  public void setContactPersonNames(String[] contactPersonNames)
  {
    this.contactPersonNames = contactPersonNames;
  }

  public String getOriginCountryName()
  {
    return originCountryName;
  }

  public void setOriginCountryName(String originCountryName)
  {
    this.originCountryName = originCountryName;
  }

  public String getDestCountryName()
  {
    return destCountryName;
  }

  public void setDestCountryName(String destCountryName)
  {
    this.destCountryName = destCountryName;
  }

  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }

  public Timestamp getDateOfQuotation()
  {
    return dateOfQuotation;
  }

  public void setDateOfQuotation(Timestamp dateOfQuotation)
  {
    this.dateOfQuotation = dateOfQuotation;
  }

  public String getEmailFlag()
  {
    return emailFlag;
  }

  public void setEmailFlag(String emailFlag)
  {
    this.emailFlag = emailFlag;
  }

  public String getFaxFlag()
  {
    return faxFlag;
  }

  public void setFaxFlag(String faxFlag)
  {
    this.faxFlag = faxFlag;
  }

  public String getPrintFlag()
  {
    return printFlag;
  }

  public void setPrintFlag(String printFlag)
  {
    this.printFlag = printFlag;
  }

  public String[] getContactsFax()
  {
    return contactsFax;
  }

  public void setContactsFax(String[] contactsFax)
  {
    this.contactsFax = contactsFax;
  }

  public String getCustomerCountryId()
  {
    return customerCountryId;
  }

  public void setCustomerCountryId(String customerCountryId)
  {
    this.customerCountryId = customerCountryId;
  }

  public String getCustomerFax()
  {
    return customerFax;
  }

  public void setCustomerFax(String customerFax)
  {
    this.customerFax = customerFax;
  }

  public String[] getContactPersonIds()
  {
    return contactPersonIds;
  }

  public void setContactPersonIds(String[] contactPersonIds)
  {
    this.contactPersonIds = contactPersonIds;
  }

  public ArrayList getExchangeRatesList()
  {
    return exchangeRatesList;
  }

  public void setExchangeRatesList(ArrayList exchangeRatesList)
  {
    this.exchangeRatesList = exchangeRatesList;
  }
 //@@Added by Kameswari for the WPBN issue-61303
  public String getPhoneNo()
  {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo)
  {
    this.phoneNo = phoneNo;
  }

  public String getMobileNo()
  {
    return mobileNo;
  }

  public void setMobileNo(String mobileNo)
  {
    this.mobileNo = mobileNo;
  }

  public String getFaxNo()
  {
    return faxNo;
  }

  public void setFaxNo(String faxNo)
  {
    this.faxNo = faxNo;
  }

  public String getCompanyName()
  {
    return companyName;
  }

  public void setCompanyName(String companyName)
  {
    this.companyName = companyName;
  }
 //@@WPBN issue-61303
  //Added by Rakesh on 24-01-2011 for CR:231219
  String 					  transittimeCheck = null;
  String 					  frequencyCheck = null;
  String 					  costingNotes =null;
  String 					  commodityTypeCheck = null;
  String 					  carrierCheck		 = null;
  String 					  serviceLevelCheck	 = null;
  String 					  noOfPiecesCheck	 = null;
  String 					  actualWeightCheck = null;
  String 					  uomCheck = null;
  String 					  volumeCheck = null;
  public String getTransittimeCheck() {
	return transittimeCheck;
  }


  public void setTransittimeCheck(String transittimeCheck) {
	this.transittimeCheck = transittimeCheck;
  }

  public String getFrequencyCheck() {
	return frequencyCheck;
  }

  public void setFrequencyCheck(String frequencyCheck) {
	this.frequencyCheck = frequencyCheck;
  }


public String getCostingNotes() {
	return costingNotes;
}


public void setCostingNotes(String costingNotes) {
	this.costingNotes = costingNotes;
}
	//Added by Rakesh on 24-01-2011 for CR:231219
 	String terminalId = null;
 	String quoteId 	  = null;
 	String operation  =	null;
	public String getTerminalId() {
		return terminalId;
	}


	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}


	public String getQuoteId() {
		return quoteId;
	}


	public void setQuoteId(String quoteId) {
		this.quoteId = quoteId;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}
	  private String[] headerFooter;
	  private String[] contentOnQuote;
	  private String[] levels;
	  private String[] align;
	  private String[] defaultHeaderFooter;
	  private String[] defaultContent;
	public String[] getHeaderFooter() {
		return headerFooter;
	}


	public void setHeaderFooter(String[] headerFooter) {
		this.headerFooter = headerFooter;
	}


	public String[] getContentOnQuote() {
		return contentOnQuote;
	}


	public void setContentOnQuote(String[] contentOnQuote) {
		this.contentOnQuote = contentOnQuote;
	}


	public String[] getLevels() {
		return levels;
	}


	public void setLevels(String[] levels) {
		this.levels = levels;
	}


	public String[] getAlign() {
		return align;
	}


	public void setAlign(String[] align) {
		this.align = align;
	}


	public String[] getDefaultHeaderFooter() {
		return defaultHeaderFooter;
	}


	public void setDefaultHeaderFooter(String[] defaultHeaderFooter) {
		this.defaultHeaderFooter = defaultHeaderFooter;
	}


	public String[] getDefaultContent() {
		return defaultContent;
	}


	public void setDefaultContent(String[] defaultContent) {
		this.defaultContent = defaultContent;
	}


	public String getCommodityTypeCheck() {
		return commodityTypeCheck;
	}


	public void setCommodityTypeCheck(String commodityTypeCheck) {
		this.commodityTypeCheck = commodityTypeCheck;
	}


	public String getCarrierCheck() {
		return carrierCheck;
	}


	public void setCarrierCheck(String carrierCheck) {
		this.carrierCheck = carrierCheck;
	}


	public String getServiceLevelCheck() {
		return serviceLevelCheck;
	}


	public void setServiceLevelCheck(String serviceLevelCheck) {
		this.serviceLevelCheck = serviceLevelCheck;
	}


	public String getNoOfPiecesCheck() {
		return noOfPiecesCheck;
	}


	public void setNoOfPiecesCheck(String noOfPiecesCheck) {
		this.noOfPiecesCheck = noOfPiecesCheck;
	}


	public String getActualWeightCheck() {
		return actualWeightCheck;
	}


	public void setActualWeightCheck(String actualWeightCheck) {
		this.actualWeightCheck = actualWeightCheck;
	}


	public String getUomCheck() {
		return uomCheck;
	}


	public void setUomCheck(String uomCheck) {
		this.uomCheck = uomCheck;
	}


	public String getVolumeCheck() {
		return volumeCheck;
	}


	public void setVolumeCheck(String volumeCheck) {
		this.volumeCheck = volumeCheck;
	}


	public String getExtNotes() {
		return extNotes;
	}


	public void setExtNotes(String extNotes) {
		this.extNotes = extNotes;
	}


	public Timestamp getValidityOfQuote() {
		return validityOfQuote;
	}


	public void setValidityOfQuote(Timestamp validityOfQuote) {
		this.validityOfQuote = validityOfQuote;
	}


	public Timestamp getEffectiveFrom() { 
		return effectiveFrom;
	}


	public void setEffectiveFrom(Timestamp effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

}
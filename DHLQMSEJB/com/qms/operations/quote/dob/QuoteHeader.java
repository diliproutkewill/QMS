/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
* This class will be useful to store the header part of any quote. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
* @version 	
* 
*/
/*
Programme Name		  :QuoteHeader.java
Module  Name  		  :Quote.
Task           		  :         
SubTask       		  :   
Author Name         :S Anil Kumar.
Date Started        :
Date Finished       :4th Aug 2005 
Date Modified       :
Description         :
Method's Summary	  :
*/

package com.qms.operations.quote.dob;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class QuoteHeader implements Serializable
{
  private String originCountry;
  private String destinationCountry;
  private String customerName;
  private String[] attentionTo;
  private Timestamp dateOfQuotation;
  private String preparedBy;
  private String agent;
  private String cargoAcceptancePlace;
  private String originPortName;
  private String destPortName;
  private String routing;
  private String commodity;
  private String typeOfService;
  private String incoTerms;
  private String notes;
  private Timestamp effDate;
  private Timestamp validUpto;
  private ArrayList internalNotes;
  private ArrayList externalNotes;
  private String originLocName;
  private String destLocName;
  private String originPortCountry;
  private String destPortCountry;
  private String custEmailId;
  private String custFaxNo;
  private String custCountyCode;
  private String paymentTerms;
  private String originCountryId;
  private String destinationCountryId;

  public QuoteHeader()
  {
  }

  public String getOriginCountry()
  {
    return originCountry;
  }

  public void setOriginCountry(String originCountry)
  {
    this.originCountry = originCountry;
  }

  public String getDestinationCountry()
  {
    return destinationCountry;
  }

  public void setDestinationCountry(String destinationCountry)
  {
    this.destinationCountry = destinationCountry;
  }

  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }

  public String[] getAttentionTo()
  {
    return attentionTo;
  }

  public void setAttentionTo(String[] attentionTo)
  {
    this.attentionTo = attentionTo;
  }

  public Timestamp getDateOfQuotation()
  {
    return dateOfQuotation;
  }

  public void setDateOfQuotation(Timestamp dateOfQuotation)
  {
    this.dateOfQuotation = dateOfQuotation;
  }

  public String getPreparedBy()
  {
    return preparedBy;
  }

  public void setPreparedBy(String preparedBy)
  {
    this.preparedBy = preparedBy;
  }

  public String getAgent()
  {
    return agent;
  }

  public void setAgent(String agent)
  {
    this.agent = agent;
  }

  public String getCargoAcceptancePlace()
  {
    return cargoAcceptancePlace;
  }

  public void setCargoAcceptancePlace(String cargoAcceptancePlace)
  {
    this.cargoAcceptancePlace = cargoAcceptancePlace;
  }

  public String getOriginPortName()
  {
    return originPortName;
  }

  public void setOriginPortName(String originPortName)
  {
    this.originPortName = originPortName;
  }

  public String getDestPortName()
  {
    return destPortName;
  }

  public void setDestPortName(String destPortName)
  {
    this.destPortName = destPortName;
  }

  public String getRouting()
  {
    return routing;
  }

  public void setRouting(String routing)
  {
    this.routing = routing;
  }

  public String getCommodity()
  {
    return commodity;
  }

  public void setCommodity(String commodity)
  {
    this.commodity = commodity;
  }

  public String getTypeOfService()
  {
    return typeOfService;
  }

  public void setTypeOfService(String typeOfService)
  {
    this.typeOfService = typeOfService;
  }

  public String getIncoTerms()
  {
    return incoTerms;
  }

  public void setIncoTerms(String incoTerms)
  {
    this.incoTerms = incoTerms;
  }

  public String getNotes()
  {
    return notes;
  }

  public void setNotes(String notes)
  {
    this.notes = notes;
  }

  public Timestamp getEffDate()
  {
    return effDate;
  }

  public void setEffDate(Timestamp effDate)
  {
    this.effDate = effDate;
  }

  public Timestamp getValidUpto()
  {
    return validUpto;
  }

  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto = validUpto;
  }

  public ArrayList getInternalNotes()
  {
    return internalNotes;
  }

  public void setInternalNotes(ArrayList internalNotes)
  {
    this.internalNotes = internalNotes;
  }

  public ArrayList getExternalNotes()
  {
    return externalNotes;
  }

  public void setExternalNotes(ArrayList externalNotes)
  {
    this.externalNotes = externalNotes;
  }

  public String getOriginLocName()
  {
    return originLocName;
  }

  public void setOriginLocName(String originLocName)
  {
    this.originLocName = originLocName;
  }

  public String getDestLocName()
  {
    return destLocName;
  }

  public void setDestLocName(String destLocName)
  {
    this.destLocName = destLocName;
  }

  public String getOriginPortCountry()
  {
    return originPortCountry;
  }

  public void setOriginPortCountry(String originPortCountry)
  {
    this.originPortCountry = originPortCountry;
  }

  public String getDestPortCountry()
  {
    return destPortCountry;
  }

  public void setDestPortCountry(String destPortCountry)
  {
    this.destPortCountry = destPortCountry;
  }

  public String getCustEmailId()
  {
    return custEmailId;
  }

  public void setCustEmailId(String custEmailId)
  {
    this.custEmailId = custEmailId;
  }

  public String getCustFaxNo()
  {
    return custFaxNo;
  }

  public void setCustFaxNo(String custFaxNo)
  {
    this.custFaxNo = custFaxNo;
  }


  public void setCustCountyCode(String custCountyCode)
  {
    this.custCountyCode = custCountyCode;
  }


  public String getCustCountyCode()
  {
    return custCountyCode;
  }

  public String getPaymentTerms()
  {
    return paymentTerms;
  }

  public void setPaymentTerms(String paymentTerms)
  {
    this.paymentTerms = paymentTerms;
  }

  public String getOriginCountryId()
  {
    return originCountryId;
  }

  public void setOriginCountryId(String originCountryId)
  {
    this.originCountryId = originCountryId;
  }

  public String getDestinationCountryId()
  {
    return destinationCountryId;
  }

  public void setDestinationCountryId(String destinationCountryId)
  {
    this.destinationCountryId = destinationCountryId;
  }

}
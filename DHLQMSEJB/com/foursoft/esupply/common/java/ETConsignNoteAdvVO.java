/* 
*
* Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
* This Software is the proprietary information of FourSoft,Pvt Ltd.
* Use is subject to license terms
*
*
* esupply-V 1.x
*
		File					:	ETConsignNoteAdvVO.java
		Sub-module name			:	Advanced LOV
		Module name				:	ETrans
		Purpose of the class	:	
		Author					:	Ravi Kumar G.
		Date					:	15-Mar-2005
		Modified By				:	
*
*/

package com.foursoft.esupply.common.java;

import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;

public class ETConsignNoteAdvVO extends ETAdvancedLOVMasterVO 
{
    protected String	consignNoteId;
    protected String	shipperName;	
    protected String	consigneeName;
    protected String	originCountry;	
    protected String	destinationCountry;
    protected String	originTerminal;
    protected String	destinationTerminal;

    protected String	confirmToAccounts;
    protected String	payAt;
    protected String	invoiceAt;
    protected String	truckLoad;    
    protected String	invoiceType;        
    protected String	invoiceGenerationType;    

    protected String	shipperId;
    protected String	consigneeId;
    protected String	actualWeight;
    protected String	noOfPacks;
    protected String	totalVolume;
    protected String fromDate;
    protected String toDate;
  protected String dateFormat;

	public void setConsignNoteId(String consignNoteId)
	{
		this.consignNoteId=consignNoteId;
	}

	public String getConsignNoteId()
	{
	return consignNoteId;
	}

	public void setShipperName(String shipperName)
	{
		this.shipperName=shipperName;
	}

	public String getShipperName()
	{
	return shipperName;
	}

	public void setConsigneeName(String consigneeName)
	{
		this.consigneeName=consigneeName;
	}

	public String getConsigneeName()
	{
	return consigneeName;
	}

	public void setOriginCountry(String originCountry)
	{
		this.originCountry=originCountry;
	}

	public String getOriginCountry()
	{
	return originCountry;
	}

	public void setDestinationCountry(String destinationCountry)
	{
		this.destinationCountry=destinationCountry;
	}

	public String getDestinationCountry()
	{
	return destinationCountry;
	}

	public void setOriginTerminal(String originTerminal)
	{
		this.originTerminal=originTerminal;
	}

	public String getOriginTerminal()
	{
	return originTerminal;
	}

	public void setDestinationTerminal(String destinationTerminal)
	{
		this.destinationTerminal=destinationTerminal;
	}

	public String getDestinationTerminal()
	{
		return destinationTerminal;
	}

	public void setInvoiceAt(String invoiceAt)
	{
		this.invoiceAt=invoiceAt;
	}

	public String getInvoiceAt()
	{
		return invoiceAt;
	}

	public void setTruckLoad(String truckLoad)
	{
		this.truckLoad=truckLoad;
	}

	public String getTruckLoad()
	{
		return truckLoad;
	}

	public void setInvoiceType(String invoiceType)
	{
		this.invoiceType=invoiceType;
	}

	public String getInvoiceType()
	{
		return invoiceType;
	}

	public void setShipperId(String shipperId)
	{
		this.shipperId=shipperId;
	}

	public String getShipperId()
	{
		return shipperId;
	}

	public void setConsigneeId(String consigneeId)
	{
		this.consigneeId=consigneeId;
	}

	public String getConsigneeId()
	{
		return consigneeId;
	}

	public void setActualWeight(String actualWeight)
	{
		this.actualWeight=actualWeight;
	}

	public String getActualWeight()
	{
		return actualWeight;
	}

	public void setNoOfPacks(String noOfPacks)
	{
		this.noOfPacks=noOfPacks;
	}

	public String getNoOfPacks()
	{
		return noOfPacks;
	}

	public void setTotalVolume(String totalVolume)
	{
		this.totalVolume=totalVolume;
	}

	public String getTotalVolume()
	{
		return totalVolume;
	}

  public String getPayAt()
  {
    return payAt;
  }

  public void setPayAt(String payAt)
  {
    this.payAt = payAt;
  }

  public String getConfirmToAccounts()
  {
    return confirmToAccounts;
  }

  public void setConfirmToAccounts(String confirmToAccounts)
  {
    this.confirmToAccounts = confirmToAccounts;
  }

  public String getInvoiceGenerationType()
  {
    return invoiceGenerationType;
  }

  public void setInvoiceGenerationType(String invoiceGenerationType)
  {
    this.invoiceGenerationType = invoiceGenerationType;
  }

  public String getFromDate()
  {
    return fromDate;
  }

  public void setFromDate(String fromDate)
  {
    this.fromDate = fromDate;
  }

  public String getToDate()
  {
    return toDate;
  }

  public void setToDate(String toDate)
  {
    this.toDate = toDate;
  }

  public String getDateFormat()
  {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat)
  {
    this.dateFormat = dateFormat;
  }
}
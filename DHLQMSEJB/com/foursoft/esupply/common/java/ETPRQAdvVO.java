   package com.foursoft.esupply.common.java;

import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;

public class ETPRQAdvVO extends ETAdvancedLOVMasterVO
{
    protected String	prqId;
    protected String	shipperName;	
    protected String	consigneeName;
    protected String	originLocation;	
    protected String	destinationLocation;
    
    protected String	actualWeight;
    protected String	noOfPacks;	
    protected String	totalVolume;	
    protected String	UOM;
    protected String	UOW;
    protected String	consoleType;
	
		
	protected String    subOperation;
	
	protected String    terminalType;

    protected String   shipperId;
	protected String   consigneeId;
	protected String   originCountryId;
	protected String   destinationCountryId;
	protected String   operationType;
	protected String   bookType;
	
		
	
	public void setOperationType(String operationType)
	{
		this.operationType=operationType;
	}

	public void setBookType(String bookType)
	{
		this.bookType=bookType;
	}

	public void setConsoleType(String consoleType)
	{
		this.consoleType=consoleType;
	}

	public void setPrqId(String prqId)
	{
		this.prqId=prqId;
	}

	public void setShipperName(String shipperName)
	{
		this.shipperName=shipperName;
	}

	public void setConsigneeName(String consigneeName)
	{
		this.consigneeName=consigneeName;
	}

	public void setOriginLocation(String originLocation)
	{
		this.originLocation=originLocation;
	}

	public void setDestinationLocation(String destinationLocation)
	{
		this.destinationLocation=destinationLocation;
	}

	public void setActualWeight(String actualWeight)
	{
		this.actualWeight=actualWeight;
	}

	public void setNoOfPacks(String noOfPacks)
	{
		this.noOfPacks=noOfPacks;
	}
	// @@ Modified by Sailaja on 2005 05 12 for SPETI-5238
	public void setTotalVolume(String totalVolume)
	{
		this.totalVolume=totalVolume;
	}
	// @@ 2005 05 12 for SPETI-5238
	public void setUOM(String UOM)
	{
		this.UOM=UOM;
	}

	public void setUOW(String UOW)
	{
		this.UOW=UOW;
	}

	public void setOriginCountryId(String originCountryId)
	{
		this.originCountryId=originCountryId;
	}

	public void setDestinationCountryId(String destinationCountryId)
	{
		this.destinationCountryId=destinationCountryId;
	}

	public void setShipperId(String shipperId)
	{
		this.shipperId=shipperId;
	}

	public void setConsigneeId(String consigneeId)
	{
		this.consigneeId=consigneeId;
	}


	public void setSubOperation(String subOperation)
	{
		this.subOperation=subOperation;
	}

	
	public void setTerminalType(String terminalType)
	{
		this.terminalType=terminalType;
	}

/* ****************  GET METHODS ******************* */

	public String getShipperId()
	{
		return shipperId;
	}

	public String getConsigneeId()
	{
		return consigneeId;
	}

	public String getOriginCountryId()
	{
		return originCountryId;
	}

	public String getDestinationCountryId()
	{
		return destinationCountryId;
	}

	public String getPrqId()
	{
		return prqId;
	}

	public String getShipperName()
	{
		return shipperName;
	}

	public String getConsigneeName()
	{
		return consigneeName;
	}

	public String getOriginLocation()
	{
		return originLocation;
	}

	public String getDestinationLocation()
	{
		return destinationLocation;
	}

	public String getActualWeight()
	{
		return actualWeight;
	}

	public String getNoOfPacks()
	{
		return noOfPacks;
	}

	public String getTotalVolume()
	{
		return totalVolume;
	}

	public String getUOM()
	{
		return UOM;
	}

	public String getUOW()
	{
		return UOW;
	}

	public String getConsoleType()
	{
		return consoleType;
	}

	public String getBookType()
	{
		return bookType;
	}
	public String getOperationType()
	{
		return operationType;
	}

	
	public String getSubOperation()
	{
		return subOperation;
	}

	
	public String getTerminalType()
	{
		return terminalType;
	} 
}
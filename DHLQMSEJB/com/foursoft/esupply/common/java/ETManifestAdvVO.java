package com.foursoft.esupply.common.java;

import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;

public class ETManifestAdvVO extends ETAdvancedLOVMasterVO 
{
    protected String	manifestId;
    protected String	manifestType;
    protected String	shipperName;	
    protected String	consigneeName;
    protected String	originLocation;	
    protected String	destinationLocation;
    protected String	originTerminal;
    protected String	destinationTerminal;
    protected String	serviceLevel;
 
    protected String	shipperId;
    protected String	consigneeId;
    protected String	grossWeight;
    protected String	noOfPacks;  

	public void setManifestId(String manifestId)
	{
		this.manifestId=manifestId;
	}

	public String getManifestId()
	{
		return manifestId;
	}

	public void setManifestType(String manifestType)
	{
		this.manifestType=manifestType;
	}

	public String getManifestType()
	{
		return manifestType;
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

	public void setOriginLocation(String originLocation)
	{
		this.originLocation=originLocation;
	}

	public String getOriginLocation()
	{
		return originLocation;
	}

	public void setDestinationLocation(String destinationLocation)
	{
		this.destinationLocation=destinationLocation;
	}

	public String getDestinationLocation()
	{
		return destinationLocation;
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

	public void setServiceLevel(String serviceLevel)
	{
		this.serviceLevel=serviceLevel;
	}

	public String getServiceLevel()
	{
		return serviceLevel;
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

	public void setGrossWeight(String grossWeight)
	{
		this.grossWeight=grossWeight;
	}

	public String getGrossWeight()
	{
		return grossWeight;
	}

	public void setNoOfPacks(String noOfPacks)
	{
		this.noOfPacks=noOfPacks;
	}

	public String getNoOfPacks()
	{
		return noOfPacks;
	}

}
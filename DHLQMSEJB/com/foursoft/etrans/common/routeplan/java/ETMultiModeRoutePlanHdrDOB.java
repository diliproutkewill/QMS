package com.foursoft.etrans.common.routeplan.java;
/**
 * Copyright (c) 2001-2001 Four-Soft Pvt Ltd, 
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information 
 * and shall use it only in accordance with the terms of the license agreement you entered 
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 */
import java.io.Serializable;
import java.sql.Timestamp;
/**
 * File		: ETMultiModeRoutePlanHdrDOB.java
 * @author	: Srinivasa Rao Koppurauri 
 * @date	: 2002-05-20
 * @version :1.6
 *
 */
public class ETMultiModeRoutePlanHdrDOB implements Serializable
{
	private long		routePlanId;
	private String		prqId;
	private String		houseDocumentId;
	private String		originTerminalId;
	private String		destinationTerminalId;
	private String		originLocationId;
	private String		destinationLocationId;
	private String		originTerminalLocation;
	private String		destinationTerminalLocation;
	private String		originLocationName;
	private String		destinationLocationName;
	private String		shipperId;
	private String		consigneeId;
	private int			primaryMode;
	private String		overWriteFlag;
	private Timestamp	createTimestamp;
	private Timestamp	lastUpdateTimestamp;
	private String		shipmentStatus;
	private String		deliveryTerms;
	private String		serviceLevelId;
	private String		serviceLevelDesc;
	private int			totalPieces;
	private double		weight;
	private String		prqStatus;
	private String		whStatus;

    private boolean     isNonSystemInBound;

	private ETMultiModeRoutePlanDtlDOB[] routePlanDtlDOB;

  /**
   * 
   */
	public ETMultiModeRoutePlanHdrDOB()
	{
	}

  /**
   * 
   * @param routePlanId
   * @param prqId
   * @param houseDocumentId
   * @param originTerminalId
   * @param destinationTerminalId
   * @param originLocationId
   * @param destinationLocationId
   * @param shipperId
   * @param consigneeId
   * @param primaryMode
   * @param overWriteFlag
   * @param createTimestamp
   * @param lastUpdateTimestamp
   * @param shipmentStatus
   */
	public ETMultiModeRoutePlanHdrDOB(long routePlanId, String prqId, String houseDocumentId, String originTerminalId, 
										String destinationTerminalId, String originLocationId, String destinationLocationId,
										String shipperId, String consigneeId, int primaryMode, String overWriteFlag,
										Timestamp createTimestamp, Timestamp lastUpdateTimestamp, String shipmentStatus)
	{
		this.routePlanId			= routePlanId;
		this.prqId					= prqId;	
		this.houseDocumentId		= houseDocumentId;
		this.originTerminalId		= originTerminalId;
		this.destinationTerminalId	= destinationTerminalId;
		this.originLocationId		= originLocationId;
		this.destinationLocationId	= destinationLocationId;
		this.shipperId				= shipperId;
		this.consigneeId			= consigneeId;
		this.primaryMode			= primaryMode;
		this.overWriteFlag			= overWriteFlag;
		this.createTimestamp		= createTimestamp;
		this.lastUpdateTimestamp	= lastUpdateTimestamp;
		this.shipmentStatus			= shipmentStatus;
	}

  /**
   * 
   * @param routePlanId
   */
	public void setRoutePlanId(long routePlanId)
	{
		this.routePlanId = routePlanId;
	}

  /**
   * 
   * @return routePlanId
   */
	public long getRoutePlanId()
	{
		return routePlanId;
	}

  /**
   * 
   * @param prqId
   */
	public void setPRQId(String prqId)
	{
		this.prqId = prqId;
	}

  /**
   * 
   * @return prqId
   */
	public String getPRQId()
	{
		return prqId;
	}

  /**
   * 
   * @param houseDocumentId
   */
	public void setHouseDocumentId(String houseDocumentId)
	{
		this.houseDocumentId = houseDocumentId;
	}

  /**
   * 
   * @return houseDocumentId
   */
	public String getHouseDocumentId()
	{
		return houseDocumentId;
	}

  /**
   * 
   * @param originTerminalId
   */
	public void setOriginTerminalId(String originTerminalId)
	{
		this.originTerminalId = originTerminalId;
	}

  /**
   * 
   * @return originTerminalId
   */
	public String getOriginTerminalId()
	{
		return originTerminalId;
	}

  /**
   * 
   * @param destinationTerminalId
   */
	public void setDestinationTerminalId(String destinationTerminalId)
	{
		this.destinationTerminalId = destinationTerminalId;
	}

  /**
   * 
   * @return destinationTerminalId
   */
	public String getDestinationTerminalId()
	{
		return destinationTerminalId;
	}

  /**
   * 
   * @param originLocationId
   */
	public void setOriginLocationId(String originLocationId)
	{
		this.originLocationId = originLocationId;
	}

  /**
   * 
   * @return originLocationId
   */
	public String getOriginLocationId()
	{
		return originLocationId;
	}

  /**
   * 
   * @param destinationLocationId
   */
	public void setDestinationLocationId(String destinationLocationId)
	{
		this.destinationLocationId = destinationLocationId;
	}

  /**
   * 
   * @return destinationLocationId
   */
	public String getDestinationLocationId()
	{
		return destinationLocationId;
	}

  /**
   * 
   * @param shipperId
   */
	public void setShipperId(String shipperId)
	{
		this.shipperId = shipperId;
	}

  /**
   * 
   * @return shipperId
   */
	public String getShipperId()
	{
		return shipperId;
	}

  /**
   * 
   * @param consigneeId
   */
	public void setConsigneeId(String consigneeId)
	{
		this.consigneeId = consigneeId;
	}

  /**
   * 
   * @return consigneeId
   */
	public String getConsigneeId()
	{
		return consigneeId;
	}

  /**
   * 
   * @param routePlanDtlDOB
   */
	public void setRoutePlanDtlDOB(ETMultiModeRoutePlanDtlDOB[] routePlanDtlDOB)
	{
		this.routePlanDtlDOB = routePlanDtlDOB;
	}

  /**
   * 
   * @return routePlanDtlDOB
   */
	public ETMultiModeRoutePlanDtlDOB[] getRoutePlanDtlDOB()
	{
		return routePlanDtlDOB;
	}

  /**
   * 
   * @param primaryMode
   */
	public void setPrimaryMode(int primaryMode)
	{
		this.primaryMode = primaryMode;
	}

  /**
   * 
   * @return primaryMode
   */
	public int getPrimaryMode()
	{
		return primaryMode;
	}

  /**
   * 
   * @param overWriteFlag
   */
	public void setOverWriteFlag(String overWriteFlag)
	{
		this.overWriteFlag = overWriteFlag;
	}

  /**
   * 
   * @return overWriteFlag
   */
	public String getOverWriteFlag()
	{
		return overWriteFlag;
	}

  /**
   * 
   * @param createTimestamp
   */
	public void setCreateTimestamp(Timestamp createTimestamp)
	{
		this.createTimestamp = createTimestamp;
	}

  /**
   * 
   * @return createTimestamp
   */
	public Timestamp getCreateTimestamp()
	{
		return createTimestamp;
	}

  /**
   * 
   * @param lastUpdateTimestamp
   */
	public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp)
	{
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

  /**
   * 
   * @return lastUpdateTimestamp
   */
	public Timestamp getLastUpdateTimestamp()
	{
		return lastUpdateTimestamp;
	}

  /**
   * 
   * @param shipmentStatus
   */
	public void setShipmentStatus(String shipmentStatus)
	{
		this.shipmentStatus = shipmentStatus;
	}

  /**
   * 
   * @return shipmentStatus
   */
	public String getShipmentStatus()
	{
		return shipmentStatus;
	}

  /**
   * 
   * @param originTerminalLocation
   */
	public void setOriginTerminalLocation(String originTerminalLocation)
	{
		this.originTerminalLocation = originTerminalLocation;
	}

  /**
   * 
   * @return originTerminalLocation
   */
	public String getOriginTerminalLocation()
	{
		return originTerminalLocation;
	}

  /**
   * 
   * @param destinationTerminalLocation
   */
	public void setDestinationTerminalLocation(String destinationTerminalLocation)
	{
		this.destinationTerminalLocation = destinationTerminalLocation;
	}

  /**
   * 
   * @return destinationTerminalLocation
   */
	public String getDestinationTerminalLocation()
	{
		return destinationTerminalLocation;
	}

  /**
   * 
   * @param originLocationName
   */
	public void setOriginLocationName(String originLocationName)
	{
		this.originLocationName = originLocationName;
	}

  /**
   * 
   * @return originLocationName
   */
	public String getOriginLocationName()
	{
		return originLocationName;
	}

  /**
   * 
   * @param destinationLocationName
   */
	public void setDestinationLocationName(String destinationLocationName)
	{
		this.destinationLocationName = destinationLocationName;
	}

  /**
   * 
   * @return destinationLocationName
   */
	public String getDestinationLocationName()
	{
		return destinationLocationName;
	}

  /**
   * 
   * @param serviceLevelId
   */
	public void setServiceLevelId(String serviceLevelId)
	{
		this.serviceLevelId = serviceLevelId;
	}

  /**
   * 
   * @return serviceLevelId
   */
	public String getServiceLevelId()
	{
		return serviceLevelId;
	}

  /**
   * 
   * @param serviceLevelDesc
   */
	public void setServiceLevelDesc(String serviceLevelDesc)
	{
		this.serviceLevelDesc = serviceLevelDesc;
	}

  /**
   * 
   * @return serviceLevelDesc
   */
	public String getServiceLevelDesc()
	{
		return serviceLevelDesc;
	}

  /**
   * 
   * @param totalPieces
   */
	public void setTotalPieces(int totalPieces)
	{
		this.totalPieces = totalPieces;
	}

  /**
   * 
   * @return totalPieces
   */
	public int getTotalPieces()
	{
		return totalPieces;
	}

  /**
   * 
   * @param weight
   */
	public void setWeight(double weight)
	{
		this.weight = weight;
	}

  /**
   * 
   * @return weight
   */
	public double getWeight()
	{
		return weight;
	}

  /**
   * 
   * @param deliveryTerms
   */
	public void setDeliveryTerms(String deliveryTerms)
	{
		this.deliveryTerms = deliveryTerms;
	}

  /**
   * 
   * @return deliveryTerms
   */
	public String getDeliveryTerms()
	{
		return deliveryTerms;
	}

  /**
   * 
   * @return prqStatus
   */
    public String getPRQStatus()
	{
	   return prqStatus;
	}

  /**
   * 
   * @param prqStatus
   */
    public void setPRQStatus(String prqStatus)
	{
	  this.prqStatus = prqStatus;
	}

  /**
   * 
   * @return whStatus
   */
    public String getWHStatus()
	{
	   return whStatus;
	}

  /**
   * 
   * @param whStatus
   */
    public void setWHStatus(String whStatus) 
	{
	   this.whStatus = whStatus;
	}

  /**
   * 
   * @param isNonSystemInBound
   */
    public void setIsNonSystemInBound(boolean isNonSystemInBound)
    {
      this.isNonSystemInBound = isNonSystemInBound;
    }

  /**
   * 
   * @return isNonSystemInBound
   */
    public boolean getIsNonSystemInBound()
    {
      return isNonSystemInBound;
    }
  
}
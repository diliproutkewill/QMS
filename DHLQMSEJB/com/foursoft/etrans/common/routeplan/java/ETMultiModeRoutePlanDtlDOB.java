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
 * File		: ETMultiModeRoutePlanDtlDOB.java
 * @author	: Srinivasa Rao Koppurauri 
 * @date	: 2002-05-20
 * @version :1.6
 *
 */
public class ETMultiModeRoutePlanDtlDOB implements Serializable
{
	private int		serialNo;
	private long	routePlanId;
	private String	originTerminalId;
	private String	destinationTerminalId;
	private int		shipmentMode;
	private String	shipmentStatus;
	private String	autoManualFlag;
	private String	masterDocId;
	private String	legType;
	private String	legValidFlag;
	private String	destinationTerminalLocation;
	private String	originTerminalLocation;
	private String	remarks;
	private int piecesReceived;
	private Timestamp receivedDate;
	private double  costAmount;
	private String orgLoc;
  private String destLoc;

	private String carrier;
	private Timestamp ETD;
	private Timestamp ETA;

  /**
   * 
   */
	public ETMultiModeRoutePlanDtlDOB()
	{
	}

  /**
   * 
   * @param serialNo
   * @param routePlanId
   * @param originTerminalId
   * @param destinationTerminalId
   * @param shipmentMode
   * @param shipmentStatus
   * @param autoManualFlag
   * @param masterDocId
   * @param legType
   * @param legValidFlag
   */
	public ETMultiModeRoutePlanDtlDOB(int serialNo, long routePlanId, String originTerminalId, String destinationTerminalId, int shipmentMode, String shipmentStatus, String autoManualFlag, String masterDocId, String legType, String legValidFlag)
	{
		this.serialNo				= serialNo;
		this.routePlanId			= routePlanId;
		this.originTerminalId		= originTerminalId;
		this.destinationTerminalId	= destinationTerminalId;
		this.shipmentMode			= shipmentMode;
		this.shipmentStatus			= shipmentStatus;
		this.autoManualFlag			= autoManualFlag;
		this.masterDocId			= masterDocId;
		this.legType				= legType;
		this.legValidFlag			= legValidFlag;
	}
  public ETMultiModeRoutePlanDtlDOB(int serialNo, long routePlanId, String originTerminalId, String destinationTerminalId, int shipmentMode, String shipmentStatus, String autoManualFlag, String masterDocId, String legType, String legValidFlag,String orgLoc,String destLoc)
	{
		this.serialNo				= serialNo;
		this.routePlanId			= routePlanId;
		this.originTerminalId		= originTerminalId;
		this.destinationTerminalId	= destinationTerminalId;
		this.shipmentMode			= shipmentMode;
		this.shipmentStatus			= shipmentStatus;
		this.autoManualFlag			= autoManualFlag;
		this.masterDocId			= masterDocId;
		this.legType				= legType;
		this.legValidFlag			= legValidFlag;
    this.orgLoc = orgLoc;
    this.destLoc  = destLoc;
    
	}
	public void setOrgLoc(String orgLoc)
	{
		this.orgLoc = orgLoc;
	}
  public void setDestLoc(String destLoc)
	{
		this.destLoc = destLoc;
	}
  public String getOrgLoc()
	{
		return orgLoc;
	}
  public String getDestLoc()
	{
		return destLoc;
	}
  /**
   * 
   * @param serialNo
   */
	public void setSerialNo(int serialNo)
	{
		this.serialNo = serialNo;
	}

  /**
   * 
   * @return serialNo
   */
	public int getSerialNo()
	{
		return serialNo;
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
   * @return routePlanId as long
   */
	public long getRoutePlanId()
	{
		return routePlanId;
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
   * @param shipmentMode
   */
	public void setShipmentMode(int shipmentMode)
	{
		this.shipmentMode = shipmentMode;
	}

  /**
   * 
   * @return shipmentMode
   */
	public int getShipmentMode()
	{
		return shipmentMode;
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
   * @param autoManualFlag
   */
	public void setAutoManualFlag(String autoManualFlag)
	{
		this.autoManualFlag = autoManualFlag;
	}

  /**
   * 
   * @return autoManualFlag
   */
	public String getAutoManualFlag()
	{
		return autoManualFlag;
	}

  /**
   * 
   * @param masterDocId
   */
	public void setMasterDocId(String masterDocId)
	{
		this.masterDocId = masterDocId;
	}

  /**
   * 
   * @return masterDocId
   */
	public String getMasterDocId()
	{
		return masterDocId;
	}

  /**
   * 
   * @param legType
   */
	public void setLegType(String legType)
	{
		this.legType = legType;
	}

  /**
   * 
   * @return legType
   */
	public String getLegType()
	{
		return legType;
	}

  /**
   * 
   * @param legValidFlag
   */
	public void setLegValidFlag(String legValidFlag)
	{
		this.legValidFlag = legValidFlag;
	}

  /**
   * 
   * @return legValidFlag
   */
	public String getLegValidFlag()
	{
		return legValidFlag;
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
   * @return  destinationTerminalLocation
   */
	public String getDestinationTerminalLocation()
	{
		return destinationTerminalLocation;
	}

  /**
   * 
   * @param remarks
   */
	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

  /**
   * 
   * @return remarks
   */
	public String getRemarks()
	{
		return remarks;
	}

  /**
   * 
   * @param carrier
   */
	public void setCarrier(String carrier)
	{
		this.carrier = carrier;
	}

  /**
   * 
   * @return carrier
   */
	public String getCarrier()
	{
		return carrier;
	}

  /**
   * 
   * @param piecesReceived
   */
	public void setPiecesReceived(int piecesReceived)
	{
		this.piecesReceived = piecesReceived;
	}

  /**
   * 
   * @return piecesReceived
   */
	public int getPiecesReceived()
	{
		return piecesReceived;
	}

  /**
   * 
   * @param costAmount
   */
	public void setCostAmount(double costAmount)
	{
		this.costAmount = costAmount;
	}

  /**
   * 
   * @return costAmount
   */
	public double getCostAmount()
	{
		return costAmount;
	}

  /**
   * 
   * @param receivedDate
   */
	public void setReceivedDate(Timestamp receivedDate)
	{
		this.receivedDate = receivedDate;
	}

  /**
   * 
   * @return receivedDate
   */
	public Timestamp getReceivedDate()
	{
		return receivedDate;
	}

  /**
   * 
   * @param ETD
   */
	public void setETD(Timestamp ETD)
	{
		this.ETD = ETD;
	}

  /**
   * 
   * @return ETD
   */
	public Timestamp getETD()
	{
		return ETD;
	}

  /**
   * 
   * @param ETA
   */
	public void setETA(Timestamp ETA)
	{
		this.ETA = ETA;
	}

  /**
   * 
   * @return ETA
   */
	public Timestamp getETA()
	{
		return ETA;
	}
}
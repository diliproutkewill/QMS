/*<%--
 %
 % Copyright (c) 1999-2005 by FourSoft,Pvt Ltd.Reserved.
 % This software is the proprietary information of FourSoft Ltd.
 % Use is subject to license terms.
 % 
 % esupply - v 1.8
 %
--%>


<%--
% File		    :   ETHBLAdvVO.java
% Sub-Module    :   HBL - Advanced LOV Search. 
% Module        :   ETrans
%
% This is the Value Object for the LOV of the HBLIds based on Different Parameters
% 
% Author        :   G.Srinivas 
% Date 			:   14/03/2005
% Modified date :   14/03/2005
--%>*/

package com.foursoft.esupply.common.java;


import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import java.util.HashMap;

/**
 * 
 */
public class ETHBLAdvVO extends ETAdvancedLOVMasterVO
{
   // protected String	prqId;
    protected String	shipperName;	
    protected String	consigneeName;
    protected String	originTerminal;	
    protected String	destinationTerminal;
    protected String	consoleType;
    protected String	hblId;
    protected String	actualWeight;
    protected String	noOfPacks;	
    protected String	volume;	
    protected String	UOM;
    
   
    
    protected String   shipperId;
	protected String   consigneeId;
	protected String   originCountryId;
	protected String   destinationCountryId;
	protected String   noOfDaysControl;
    protected String   noOfDays;

    HashMap	params=null;
 
  /**
   * 
   */
    public ETHBLAdvVO()
    {
    }
  /*  public void setPrqId(String prqId)
	{
		this.prqId=prqId;
	}*/
  
    public void setShipperName(String shipperName)
	{
		this.shipperName=shipperName;
	}
  
  /**
   * 
   * @param consigneeName
   */
	public void setConsigneeName(String consigneeName)
	{
		this.consigneeName=consigneeName;
	}
  /**
   * 
   * @param originTerminal
   */
	public void setOriginTerminal(String originTerminal)
	{
		this.originTerminal=originTerminal;
	}
  /**
   * 
   * @param destinationTerminal
   */
	public void setDestinationTerminal(String destinationTerminal)
	{
		this.destinationTerminal=destinationTerminal;
	}
  /**
   * 
   * @param consoleType
   */
	public void setConsoleType(String consoleType)
	{
		this.consoleType=consoleType;
	}
  /**
   * 
   * @param hblId
   */
	public void setHblId(String hblId)
	{
		this.hblId=hblId;
	}
  /**
   * 
   * @param actualWeight
   */
	public void setActualWeight(String actualWeight)
	{
		this.actualWeight=actualWeight;
	}
  /**
   * 
   * @param noOfPacks
   */
	public void setNoOfPacks(String noOfPacks)
	{
		this.noOfPacks=noOfPacks;
	}
  /**
   * 
   * @param volume
   */
	public void setVolume(String volume)
	{
		this.volume=volume;
	}
  /**
   * 
   * @param UOM
   */
	public void setUOM(String UOM)
	{
		this.UOM=UOM;
	}
///////////////////////////
  /**
   * 
   * @param originCountryId
   */
  public void setOriginCountryId(String originCountryId)
	{
		this.originCountryId=originCountryId;
	}
  /**
   * 
   * @param destinationCountryId
   */
  public void setDestinationCountryId(String destinationCountryId)
	{
		this.destinationCountryId=destinationCountryId;
	}
  /**
   * 
   * @param shipperId
   */
  public void setShipperId(String shipperId)
	{
		this.shipperId=shipperId;
	}
 
  /**
   * 
   * @param consigneeId
   */
 public void setConsigneeId(String consigneeId)
	{
		this.consigneeId=consigneeId;
	}
	//get methods
  /**
   * 
   * @return 
   */
  public String getShipperId()
	{
		return shipperId;
	}
 
  /**
   * 
   * @return 
   */
 public String getConsigneeId()
	{
		return consigneeId;
	}
  /**
   * 
   * @return 
   */
	public String getOriginCountryId()
	{
		return originCountryId;
	}
  /**
   * 
   * @return 
   */
  public String getDestinationCountryId()
	{
		return destinationCountryId;
	}
  
 ////////
/*   public String getPrqId()
	{
		return prqId;
	}*/
  /**
   * 
   * @return 
   */
  public String getShipperName()
	{
		return shipperName;
	}
  /**
   * 
   * @return 
   */
	public String getConsigneeName()
	{
		return consigneeName;
	}
  /**
   * 
   * @return 
   */
	public String getOriginTerminal()
	{
		return originTerminal;
	}
  /**
   * 
   * @return 
   */
	public String getDestinationTerminal()
	{
		return destinationTerminal;
	}
  /**
   * 
   * @return 
   */
	public String getConsoleType()
	{
		return consoleType;
	}
  /**
   * 
   * @return 
   */
	public String getHblId()
	{
		return hblId;
	}
  /**
   * 
   * @return 
   */
	public String getActualWeight()
	{
		return actualWeight;
	}
  /**
   * 
   * @return 
   */
	public String getNoOfPacks()
	{
		return noOfPacks;
	}
  /**
   * 
   * @return 
   */
	public String getVolume()
	{
		return volume;
	}
  /**
   * 
   * @return 
   */
	public String getUOM()
	{
		return UOM;
	}
 
}
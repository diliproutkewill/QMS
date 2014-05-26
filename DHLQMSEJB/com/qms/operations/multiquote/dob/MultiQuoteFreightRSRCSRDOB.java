/**
 *
 * Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * QMS - v 1.x
 * 
 * This class will be useful to store the freight sell rates between origin location and destination location. When data is retrived
 * from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 * @version 	
 * 
 */
/**
Programme Name		  	:MultiQuoteFreightRSRCSRDOB.java
Module  Name  		  	:Quote.
Task           		  	:         
SubTask       		  	:   
Author Name         	:Govind
Date Started        	:
Date Finished       	: 
Date Modified       	:
Description         	:
Method's Summary	  	:
 */

package com.qms.operations.multiquote.dob;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Timestamp;

public class MultiQuoteFreightRSRCSRDOB implements java.io.Serializable
{
	private String origin;
	private String destination;
	private int shipmentMode;
	private String carrierId;
	private String serviceLevelId;
	private String frequency;
	private String transitTime;
	private int sellRateId;
	private int buyRateId;
	private int laneNo;//The lane no of the buy rates
	private String weightBreakType;//flat,slab or list
	private String rsrOrCsrFlag;//whether RSR or CSR
	private String freightLeg;// as 'origin-destination'
	private String notes;
	private String weightClass;
	private String currency;
	private Timestamp effDate;
	private Timestamp validUpTo;
	private String createdTerminalId;
	private String[] chargeRates;
	private String selectedFlag;
	private String serviceLevelDesc;//@@Added by Kameswari for the WPBN issue-31330
	private String consoleType;//@@Added  by VLAKSHMI for issue 146968 on 5/12/2008
	private String[] checkedFalg;//@@Added  by VLAKSHMI for issue 146968 on 5/12/2008
	private String extNotes;//Added by Mohan for Issue No.219976 on 04-11-2010
	private String[] flatWeightBreaks;
	private String[] listWeightBreaks;
	private String[] slabWeightBreaks;
	private String[] rateDescriptions;
	private String  incoTerms;
	private String  densityRatio;
	public MultiQuoteFreightRSRCSRDOB()
	{
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

	public int getShipmentMode()
	{
		return shipmentMode;
	}

	public void setShipmentMode(int shipmentMode)
	{
		this.shipmentMode = shipmentMode;
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

	public int getSellRateId()
	{
		return sellRateId;
	}

	public void setSellRateId(int sellRateId)
	{
		this.sellRateId = sellRateId;
	}

	public int getLaneNo()
	{
		return laneNo;
	}

	public void setLaneNo(int laneNo)
	{
		this.laneNo = laneNo;
	}

	public String getWeightBreakType()
	{
		return weightBreakType;
	}

	public void setWeightBreakType(String weightBreakType)
	{
		this.weightBreakType = weightBreakType;
	}



	public String getRsrOrCsrFlag()
	{
		return rsrOrCsrFlag;
	}

	public void setRsrOrCsrFlag(String rsrOrCsrFlag)
	{
		this.rsrOrCsrFlag = rsrOrCsrFlag;
	}

	public String getFreightLeg()
	{
		return freightLeg;
	}

	public void setFreightLeg(String freightLeg)
	{
		this.freightLeg = freightLeg;
	}

	public int getBuyRateId()
	{
		return buyRateId;
	}

	public void setBuyRateId(int buyRateId)
	{
		this.buyRateId = buyRateId;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public String getWeightClass()
	{
		return weightClass;
	}

	public void setWeightClass(String weightClass)
	{
		this.weightClass = weightClass;
	}

	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(String currency)
	{
		this.currency = currency;
	}

	public Timestamp getEffDate()
	{
		return effDate;
	}

	public void setEffDate(Timestamp effDate)
	{
		this.effDate = effDate;
	}

	public Timestamp getValidUpTo()
	{
		return validUpTo;
	}

	public void setValidUpTo(Timestamp validUpTo)
	{
		this.validUpTo = validUpTo;
	}

	public String getCreatedTerminalId()
	{
		return createdTerminalId;
	}

	public void setCreatedTerminalId(String createdTerminalId)
	{
		this.createdTerminalId = createdTerminalId;
	}

	public String[] getChargeRates()
	{
		return chargeRates;
	}

	public void setChargeRates(String[] chargeRates)
	{
		this.chargeRates = chargeRates;
	}

	public String getSelectedFlag()
	{
		return selectedFlag;
	}

	public void setSelectedFlag(String selectedFlag)
	{
		this.selectedFlag = selectedFlag;
	}
	//@@Added by Kameswari for the WPBN issue-31330
	public String getServiceLevelDesc()
	{
		return serviceLevelDesc;
	}

	public void setServiceLevelDesc(String serviceLevelDesc)
	{
		this.serviceLevelDesc = serviceLevelDesc;
	}


	public void setConsoleType(String consoleType)
	{
		this.consoleType = consoleType;
	}


	public String getConsoleType()
	{
		return consoleType;
	}


	public void setCheckedFalg(String[] checkedFalg)
	{
		this.checkedFalg = checkedFalg;
	}


	public String[] getCheckedFalg()
	{
		return checkedFalg;
	}
//	Added by Mohan for Issue No.219976 on 04-11-2010
	public String getExtNotes() {
		return extNotes;
	}

	public void setExtNotes(String extNotes) {
		this.extNotes = extNotes;
	}

	public String[] getFlatWeightBreaks() {
		return flatWeightBreaks;
	}

	public void setFlatWeightBreaks(String[] flatWeightBreaks) {
		this.flatWeightBreaks = flatWeightBreaks;
	}

	public String[] getListWeightBreaks() {
		return listWeightBreaks;
	}

	public void setListWeightBreaks(String[] listWeightBreaks) {
		this.listWeightBreaks = listWeightBreaks;
	}

	public String[] getSlabWeightBreaks() {
		return slabWeightBreaks;
	}

	public void setSlabWeightBreaks(String[] slabWeightBreaks) {
		this.slabWeightBreaks = slabWeightBreaks;
	}

	public String[] getRateDescriptions() {
		return rateDescriptions;
	}

	public void setRateDescriptions(String[] rateDescriptions) {
		this.rateDescriptions = rateDescriptions;
	}

	public String getIncoTerms() {
		return incoTerms;
	}

	public void setIncoTerms(String incoTerms) {
		this.incoTerms = incoTerms;
	}

	public String getDensityRatio() {
		return densityRatio;
	}

	public void setDensityRatio(String densityRatio) {
		this.densityRatio = densityRatio;
	}

//	@@WPBN issue-31330
}
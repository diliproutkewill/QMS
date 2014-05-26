/**
 * @ (#) ChargeGroupingDOB.java
 * Copyright (c) 2001 The Four Soft Pvt Ltd., 
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */

/**
 * File       : ChargeGroupingDOB.java
 * Sub-Module : chargesgroup
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * * @date    : 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.java;

public class SurchargeDOB implements java.io.Serializable
{
	String  	surchargeid			=	"";
	String  	surchargeDesc		=	"";
	String 		rateBreak			=	"";
	String 		rateType			=	"";
	String 		weightBreaks		=	"";
	int   		shipmentMode		=	0;
	String      serch               =   "";// added  by Silpa.p For SurCharge View All on 15-06-11
	
	
	

	public String getSurchargeid() {
		return surchargeid;
	}
	public void setSurchargeid(String surchargeid) {
		this.surchargeid = surchargeid;
	}
	public String getSurchargeDesc() {
		return surchargeDesc;
	}
	public void setSurchargeDesc(String surchargeDesc) {
		this.surchargeDesc = surchargeDesc;
	}
	public int getShipmentMode() {
		return shipmentMode;
	}
	public void setShipmentMode(int shipmentMode) {
		this.shipmentMode = shipmentMode;
	}
	public String getRateBreak() {
		return rateBreak;
	}
	public void setRateBreak(String rateBreak) {
		this.rateBreak = rateBreak;
	}
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	public String getWeightBreaks() {
		return weightBreaks;
	}
	public void setWeightBreaks(String weightBreaks) {
		this.weightBreaks = weightBreaks;
	}
	public String getSerch() {
		return serch;
	}
	public void setSerch(String serch) {
		this.serch = serch;
	}
	
   
}


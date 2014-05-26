/**
 * @ (#) QMSChargesReportDOB.java
 * Copyright (c) 2011 The Four Soft Pvt Ltd., 
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */

/**
 * File       : QMSChargesReportDOB.java
 * Sub-Module : QuoteGroupExcelReport
 * Module     : QMS
 * @author    : Venkata Kishore Podili
 * * @date 08-Feb-2011
 * Modified by      Date     Reason
 */
package com.qms.reports.java;

import java.io.Serializable;

public class QMSChargesReportDOB implements Serializable{
	
	 private String quoteId;
	 private String chargeName;
	 private String definedBy;
	 private String	breakPoint;
	 private String	marginDiscountFlag;
	 private String	currency;
	 private String	buyRate;
	 private String	rsr;
	 private String	ratio;
	 private String	origin;
	 private String	destination;
	 private String	sellRate;
	 private String	chargeAt;
	 private String basis;
	 private String	externalChargeName; // Added By Kishore Podili For External Name in the QuoteGroupExcelReport
	 
	public String getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(String quoteId) {
		this.quoteId = quoteId;
	}
	public String getChargeName() {
		return chargeName;
	}
	public void setChargeName(String chargeName) {
		this.chargeName = chargeName;
	}
	public String getDefinedBy() {
		return definedBy;
	}
	public void setDefinedBy(String definedBy) {
		this.definedBy = definedBy;
	}
	public String getBreakPoint() {
		return breakPoint;
	}
	public void setBreakPoint(String breakPoint) {
		this.breakPoint = breakPoint;
	}
	public String getMarginDiscountFlag() {
		return marginDiscountFlag;
	}
	public void setMarginDiscountFlag(String marginDiscountFlag) {
		this.marginDiscountFlag = marginDiscountFlag;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBuyRate() {
		return buyRate;
	}
	public void setBuyRate(String buyRate) {
		this.buyRate = buyRate;
	}
	public String getRsr() {
		return rsr;
	}
	public void setRsr(String rsr) {
		this.rsr = rsr;
	}
	public String getRatio() {
		return ratio;
	}
	public void setRatio(String ratio) {
		this.ratio = ratio;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getSellRate() {
		return sellRate;
	}
	public void setSellRate(String sellRate) {
		this.sellRate = sellRate;
	}
	public String getChargeAt() {
		return chargeAt;
	}
	public void setChargeAt(String chargeAt) {
		this.chargeAt = chargeAt;
	}
	public String getBasis() {
		return basis;
	}
	public void setBasis(String basis) {
		this.basis = basis;
	}
	public String getExternalChargeName() {
		return externalChargeName;
	}
	public void setExternalChargeName(String externalChargeName) {
		this.externalChargeName = externalChargeName;
	}
	 
	
	 
	
	 
	 
	 
	 

}

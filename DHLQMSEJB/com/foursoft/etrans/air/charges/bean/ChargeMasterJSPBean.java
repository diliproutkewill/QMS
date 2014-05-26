/**
 * @(#)ChargeMasterJSPBean.java         19/01/2001
 *
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft. For more information on the Four Soft vist : 'www.four-soft.com'
 */
package com.foursoft.etrans.air.charges.bean;
import java.io.Serializable;

/**
 * File		: ChargeMasterJSPBean.java
 * @author	: 
 * @date	: 2001-08-07
 * @version :   
 */

public class ChargeMasterJSPBean implements java.io.Serializable 
{	
 	 private int chargeId;
 	 private String description;
 	 private String costIncurredAt;

	/**
	 * An empty Constructor in which all the class variables are assigned to null if the variables are String data Type and 0 if
	 * the variable is of Double Or Integer. 
	 */
    public ChargeMasterJSPBean(){}

	/**
	 * takes the charge Id as integer argument and sets the same to the class variable chargeId 
	 * @param chargeId as Iteger indicating Charge Id
	 */  	   	  
	public void setChargeId(int chargeId)
	{
  		this.chargeId=chargeId;
	}

	/**
	 * returns the Charge Id 
	 * @return the chargeId as Integer
	 */ 	 	    
	public int getChargeId()
	{
		return chargeId;
	}

	/**
	 * takes the Description about charge as String argument and sets the same to the class variable description 
	 * @param description as String indicating Charge Description
	 */ 
	public void setDescription(String description)
	{
		this.description=description;
	}

	/**
	 * returns the  description 
	 * @return the description as String
	 */
	public String getDescription()
	{
 		return description;
	}

	/**
	 * takes the CostIncurredAt about charge as String argument and sets the same to the class variable costIncurredAt 
	 * @param costIncurredAt as String indicating either Charges Incurred at origin or at destination.
	 */ 
	public void setCostIncurredAt(String costIncurredAt)
	{
  		this.costIncurredAt=costIncurredAt;
	}

	/**
	 * returns the costIncurredAt
	 * @return the costIncurredAt as String
	 */
	public String getCostIncurredAt()
	{
  		return costIncurredAt;
	}
 }  	  	  		 	 	  	  	 	 	 
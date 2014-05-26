/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/*
	Program Name	:TaxMaster.java
	Module Name		:ETrans
	Task			:TaxMaster
	Sub Task		:TaxMasterJavaObject
	Author Name		:Ushasree.Petluri
	Date Started	:September 11,2001
	Date Completed	:September 12,2001 
	Date Modified	:September 11,2001 by Ushasree.P
	Description		:This file main purpose is to get and set all the datafields in the database.
*/


	/**
	* This class will be useful to manipulate the Details of TaxMaster (i.e., the main details). When data is retriving 
	* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
	*/ 


package com.foursoft.etrans.setup.taxes.bean;
import java.util.ArrayList ;
/**
 * 
 * This class will be useful to .
 * 
 * File		  : TaxMaster.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */


	public class TaxMaster implements java.io.Serializable
	{
		/**
		* An empty Constructor in which all the class variables are assigned to null if the variables are String data Type and 0 if
		* the variable is of Double Or Integer. 
		*/	
		public TaxMaster()
		{
			this.taxId	=	null;
			this.desc		=	null;
			this.remarks	=	null;	
			this.chargeId	=   null;
			this.surchargeId=   null;
			this.taxPer		=   0.0;
			this.accessLevel = null;
			this.taxType	 = null;
			this.countyIds	 = null;	
			this.termId      = null;	
			this.countryList = null;
			this.selectedTerminalId = null;	
		}	
		/**
		* This method is used for retreiving the taxId .
		* returns taxId  String taxId
		*/  	
  		public String getTaxId()
  		{ 
			return this.taxId ;
		}
		/**
   * This method is used for setting the taxId and takes taxId as argument.
   * param taxId  String taxId
   * @param taxId
   */  	
  		public void setTaxId(String taxId)
  		{
 			this.taxId = taxId;
		}
		/**
   * This method is used for retreiving the desc .
   * returns desc  String desc
   * @return String
   */ 	
  		public String getDesc()
  		{
			return this.desc ;
		}
		/**
   * This method is used for setting the desc and takes desc as argument.
   * param desc  String desc
   * @param desc
   */  
  		public void setDesc(String desc)
  		{
 			this.desc = desc;
		}
		/**
		* This method is used for retreiving the remarks .
		* returns remarks  String remarks
		*/ 	
  		public String getRemarks()
  		{
			return this.remarks ;
		}
		/**
		* This method is used for setting the remarks and takes remarks as argument.
		* param remarks  String remarks
		*/  
  		public void setRemarks(String remarks)
  		{
 			this.remarks = remarks;
		}
		
  /**
   * 
   * @return String
   */
		public String getChargeId()
  		{
			return this.chargeId ;
		}
  /**
   * 
   * @param chargeId
   */
		public void setChargeId(String chargeId)
  		{
 			this.chargeId = chargeId;
		}
		
		
  /**
   * 
   * @return String
   */
		public String getSurchargeId()
  		{
			return this.surchargeId ;
		}
  /**
   * 
   * @param surchargeId
   */
		public void setSurchargeId(String surchargeId)
  		{
 			this.surchargeId = surchargeId;
		}
		
  /**
   * 
   * @return String
   */
		public String getAccessLevel()
  		{
			return this.accessLevel ;
		}
  /**
   * 
   * @param accessLevel
   */
		public void setAccessLevel(String accessLevel)
  		{
 			this.accessLevel = accessLevel;
		}
		
  /**
   * 
   * @return String
   */
		public String getTaxType()
  		{
			return this.taxType ;
		}
  /**
   * 
   * @param taxType
   */
		public void setTaxType(String taxType)
  		{
 			this.taxType = taxType;
		}
		
  /**
   * 
   * @return double
   */
		public double getTaxPer()
  		{
			return this.taxPer ;
		}
  /**
   * 
   * @param taxPer
   */
		public void setTaxPer(double taxPer)
  		{
 			this.taxPer = taxPer;
		}
		
  /**
   * 
   * @return   String Array

   */
		public String[] getCountyIds()
  		{
			return this.countyIds ;
		}
  /**
   * 
   * @param countyIds
   */
		public void setCountyIds(String[] countyIds)
  		{
 			this.countyIds = countyIds;
		}
		
  /**
   * 
   * @return String
   */
		public String getTermId()
  		{
			return this.termId ;
		}
  /**
   * 
   * @param termId
   */
		public void setTermId(String termId)
  		{
 			this.termId = termId;
		}
		
  /**
   * 
   * @return String
   */
		public String getSelectedTerminalId()
  		{
			return this.selectedTerminalId ;
		}
  /**
   * 
   * @param SelectedTerminalId
   */
		public void setSelectedTerminalId(String SelectedTerminalId)
  		{
 			this.selectedTerminalId = selectedTerminalId;
		}
  /**
   * 
   * @param countryList
   */
		public void setCountryList(ArrayList countryList)
		{
			this.countryList = countryList;
		}
		
  /**
   * 
   * @return ArrayList
   */
		public ArrayList getCountryList()
		{
			return countryList;
		}
		/* 
		* Declaring the datamembers
		*/	
		private String taxId   = null;
		private String desc    = null;
		private String remarks = null;
		private String chargeId		= null;
		private String surchargeId	= null;
		private double taxPer		= 0.0;
		private String accessLevel	= null;
		private String taxType	    = null;
		private String termId  	    = null;
		private String[] countyIds	 = null;	
		private String   selectedTerminalId = null;
		private ArrayList countryList   = null;
	}
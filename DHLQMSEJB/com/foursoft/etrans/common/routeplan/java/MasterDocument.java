/*
 * @(#)MasterDocument.java         07/09/2001
 *
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd, 
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft.
*/
package com.foursoft.etrans.common.routeplan.java;

import  com.foursoft.etrans.common.routeplan.java.MasterDocumentHeader;
import  com.foursoft.etrans.common.routeplan.java.MasterDocumentFlightDetails;
//import  com.foursoft.etrans.air.mawb.bean.MasterDocumentCharges;


   /**
	 * @file : MasterDocument.java
	 * @author : 
	 * @date : 2003-08-07
	 * @version : 1.6 
	 */
public class MasterDocument implements java.io.Serializable
{
	public MasterDocument()
	{
		masterdocHeader         =  null;	// Variable for java Object MasterDocumentHeader.java
		//housedocInfo            =  null;	// Variable for java Object MasterDocumentHouseDocInfo.java
		masterDocFlightDetails  =  null;	// Variable for java Object MasterDocumentFlightDetails.java
		//masterDocChargesDetails =  null; 	// Variable for java Object MasterDocumentCharges.java
	}
	

	/*
     * Takes the MasterDocumentHeader, java Object, as Object argument and sets the same to the member variable, masterdocHeader
     * @param : masterdocHeader this Object is used to set Details in Object MasterDocumentHeader.
	 * @return :
	 * @author :
	 *
	 */
	public  void setMasterDocHeader( MasterDocumentHeader masterdocHeader)
	{
		this.masterdocHeader = masterdocHeader;
	}
	

	/*
     * 
     * @param : 
	 * @return : the MasterDocumentHeader.
	 * @author :
	 *
	 */
 	public MasterDocumentHeader getMasterDocHeader()
	{
		return masterdocHeader;
	}
	

	/*
     * Takes the MasterDocumentHouseDocInfo, java Object, as Object argument and sets the same to the member variable, housedocInfo
     * @param : housedocInfo this Object is used to set Details in Object MasterDocumentHouseDocInfo.
	 * @return : 
	 * @author :
	 *
	 */
	
	/**
     * Returns the MasterDocumentHouseDocInfo. It is the java Object.
     *
     * @returns the MasterDocumentHouseDocInfo as Object.
     */

	/*
     * 
     * @param : 
	 * @return : Returns the MasterDocumentHouseDocInfo. It is the java Object.
	 * @author :
	 *
	 */
 
	

	/*
     * Takes the masterDocFlightDetails, java Object, as Object argument and sets the same to the member variable, masterDocFlightDetails
     * @param : masterDocFlightDetails this Object is used to set Details in Object MasterDocumentFlightDetails.
	 * @return : 
	 * @author :
	 *
	 */
	public  void setMasterDocFlightDetails( MasterDocumentFlightDetails masterDocFlightDetails )
	{
		this.masterDocFlightDetails = masterDocFlightDetails;
	}
	

	/*
     * 
     * @param : 
	 * @return : the MasterDocumentFlightDetails as Object
	 * @author :
	 *
	 */
 	public MasterDocumentFlightDetails getMasterDocFlightDetails()
	{
		return masterDocFlightDetails;
	}
	

	/*
     * 
     * @param : masterDocChargesDetails as Object
	 * @return : 
	 * @author :
	 *
	 */
	
	 /*
     * 
     * @param : 
	 * @return : the MasterDocumentCharges as Object.
	 * @author :
	 *
	 */
 
	//datamembers
	private 	MasterDocumentHeader   		masterdocHeader 			=  null;
	
	private 	MasterDocumentFlightDetails masterDocFlightDetails 		=  null;
}
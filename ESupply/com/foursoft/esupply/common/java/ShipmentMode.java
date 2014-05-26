/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.java;

/**
 * File			: ShipmentMode.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents The Shipment Modules available in the ported application
 * 
 * @author	Amit Parekh 
 * @date	18-07-2002
 */
 
public final class ShipmentMode 
{
	/** The class is declared to be final so that it cannot be subclassed.
	  * The private prevents instantiation of this class.
	  */
	private ShipmentMode()
	{
	}
	


	private static final String[]	AIR				=	{"Setup","Air"};
	private static final String[]	SEA				=	{"Setup","Sea"};
	private static final String[]	TRUCK			=	{"Setup","Truck"};
	private static final String[]	AIR_SEA			=	{"Setup","Air","Sea"};
	private static final String[]	AIR_TRUCK		=	{"Setup","Air","Truck"};
	private static final String[]	SEA_TRUCK		=	{"Setup","Sea","Truck"};
	private static final String[]	AIR_SEA_TRUCK	=	{"Setup","Air","Sea","Truck"};

	
	public static String[] getShipmentModes(int shipmentMode) {
		
		// The array is declared with size as Zero to prevent the User of this class from getting a
		// NullPointerException when the static vaibale shipmentMode is not in the range of 1 to 7
		
		String[] saModes = new String[0];
		
		if(shipmentMode==1)
			saModes = AIR;
		else if(shipmentMode==2)
			saModes = SEA;
		else if(shipmentMode==3)
			saModes = AIR_SEA;
		else if(shipmentMode==4)
			saModes = TRUCK;
		else if(shipmentMode==5)
			saModes = AIR_TRUCK;
		else if(shipmentMode==6)
			saModes = SEA_TRUCK;
		else if(shipmentMode==7)
			saModes = AIR_SEA_TRUCK;

		return saModes;
	}
	
	public static int getShipmentMode(String shipmentMode) {
		
		int mode = -1;

		if(shipmentMode.equalsIgnoreCase("Setup")) {
			mode = 0;
		}
		if(shipmentMode.equalsIgnoreCase("Air")) {
			mode = 1;
		}
		if(shipmentMode.equalsIgnoreCase("Sea")) {
			mode = 2;
		}
		if(shipmentMode.equalsIgnoreCase("Truck")) {
			mode = 4;
		}
		
		return mode;
	}
	
	public static String getShipmentMode(int shipmentMode) {
		
		String mode = "";

		if(shipmentMode == 0) {
			mode = "Setup";
		}
		if(shipmentMode == 1) {
			mode = "Air";
		}
		if(shipmentMode == 2) {
			mode = "Sea";
		}
		if(shipmentMode == 4) {
			mode = "Truck";
		}
		
		return mode;
	}

}
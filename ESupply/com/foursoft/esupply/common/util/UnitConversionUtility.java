/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.util;

/**
 * File			: UnitConversionUtility.java
 * sub-module 	: Common
 * module 		: eSupply
 * 
 * This is a Utility Class that encapsulates the interface to the functionality for Unit Conversion
 * and also Volume to Weight conversion. This class can be Used in Enterprise Beans, DAOs and
 * also JSPs and Servlets.
 * 
 * This class is intended to be the single point of contact
 * for Unit Conversion Functionality like
 * (1) Displaying Unit Labels 
 * (2) Displaying Unit Symbols
 * (3) Converting External Numerical values to Internal Numerical values for storage
 * (4) Converting Internal Numerical values to External Numerical values for display
 * (5) Calculation of Chargeable Weight
 * (6) Calculation of Chargeable Volume
 * 
 * @author	Amit Parekh 
 * @date	27-12-2002
 */

import java.util.HashMap;
import java.math.BigDecimal;

import com.foursoft.esupply.common.exception.InvalidUnitException;
import com.foursoft.esupply.common.java.FoursoftConfig;
import com.foursoft.esupply.common.java.UnitConverter;
import com.foursoft.esupply.common.java.UnitConverterFactory;
import com.foursoft.esupply.common.java.UnitConversion;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

public final class UnitConversionUtility  {

	/**
	  * The name of this file for the purpose of Logging 
	  */
	private static final String	fileName	=	"UnitConversionUtility.java";
  private static Logger logger = null;
	/**
	  * Interface to the object that implements the UnitConversion functionality and the one
	  * that is returned by the factory.
	  */
	private UnitConverter	unitConverter =	null;

	/**
	  * Default Public Constructor for use wherever needed. 
	  */
	public	UnitConversionUtility() {
    logger  = Logger.getLogger(UnitConversionUtility.class);
		unitConverter	=	( UnitConverterFactory.getUnitConverterFactory() ).getUnitConverter();
	}

	/**
	  * Converts a given String to an equivalent BigDecimal representation provided the String is
	  * a valid numeric value. If all other cases like the String being 'null' or not having
	  * purely numeric characters, this method will return a BigDecimal with the value equivalent
	  * to Zero.
	  *
	  * @param		String		To be converted to a BigDecimal numeric representation
	  * 
	  * @return		Value converted to equivalent BigDecimal format
	  */
	public BigDecimal	getBigDecimal(String valueToBeConverted) {
		if(valueToBeConverted == null) {
			valueToBeConverted = "0.0";
		}
		double	d = 0;
		try {
			d = Double.parseDouble( valueToBeConverted );
		} catch(NumberFormatException nfe) {
			valueToBeConverted = "0.0";
		}
		return new BigDecimal( valueToBeConverted );
	}

	/**
	  * Converts a given String array to an equivalent BigDecimal representation provided the String is
	  * a valid numeric value. If all other cases like the String being 'null' or not having
	  * purely numeric characters, this method will return a BigDecimal with the value equivalent
	  * to Zero. Calls a method in this same class for help.
	  *
	  * @param		String[]		Array of Strings To be converted to a BigDecimal numeric representation
	  * 
	  * @return		Array of BigDecimal values converted to equivalent BigDecimal format
	  */
	public BigDecimal[]	getBigDecimals(String[] valuesToBeConverted) {

		BigDecimal[]	convertedValues		=	new BigDecimal[ valuesToBeConverted.length ];
		int valuesToBConvLen	=	valuesToBeConverted.length;
		for(int i=0; i < valuesToBConvLen; i++) {;
			convertedValues[i]	=	getBigDecimal( valuesToBeConverted[i] );
		}
		
		return convertedValues;
	}

	/**
	  * Converts a given BigDecimal array to an equivalent String array
	  *
	  * @param		BigDecimal[]	Array of BigDecimal Values to be converted to Strings
	  * 
	  * @return		Array of String values converted from BigDecimal Values
	  */
	public String[]		getStringValues(BigDecimal[] valuesToBeConverted) {

		String[]	convertedValues		=	new String[ valuesToBeConverted.length ];
		int valToBConvLen	=	valuesToBeConverted.length;
		for(int i=0; i < valToBConvLen; i++) {

			if(valuesToBeConverted[i]==null) {
				valuesToBeConverted[i] = new BigDecimal("0.0");
			}
			convertedValues[i]	=	valuesToBeConverted[i].toString();
		}
		
		return convertedValues;
	}

	/**
	  * Delegates the request to a method of this same Component.
	  * Converts and returns a value that is an equivalent representation of the given value
	  * in a Micro Unit, pre-defined for the measurement types as shown below.
	  * 
	  * Measurement Type	Internal Implicit Unit
	  * ----------------	----------------------
	  * LENGTH				MICROMETER
	  * VOLUME				CUBIC_MILLIMETER
	  * WEIGHT				MICROGRAM
	  * 
	  * The value returned by this method is the one that will be actually stored in the database.
	  * This value is implicitly in the Units shown above for the given measurement types.
	  *
	  * @param		double		Value To be converted to equivalent Micro Unit
	  * @param		String		Input Micro Unit
	  * 
	  * @return		Value converted to equivalent Input Micro Unit
	  *
	  * @throws		InvalidUnitException if the input unit passed as a parameter to this method is not among the pre-defined ones
	  */

	public BigDecimal getInternalValue( double value, String inputUnit ) throws InvalidUnitException
	{
		return unitConverter.getInternalValue( value + "", inputUnit );
	}

	/**
	  * Delegates the request to a method with the same name, parameters and return type in class UnitConversion.
	  * Converts and returns a value that is an equivalent representation of the given value
	  * in a Micro Unit, pre-defined for the measurement types as shown below.
	  * 
	  * Measurement Type	Internal Implicit Unit
	  * ----------------	----------------------
	  * LENGTH				MICROMETER
	  * VOLUME				CUBIC_MILLIMETER
	  * WEIGHT				MICROGRAM
	  * 
	  * The value returned by this method is the one that will be actually stored in the database.
	  * This value is implicitly in the Units shown above for the given measurement types.
	  *
	  * @param		String		Numerical Value in String form to be converted to equivalent Micro Unit.
	  * 						Should be a valid numerical value. If its not a numerical value, a NumberFormatException
	  *                         will be thrown.
	  * @param		String		Input Micro Unit
	  * 
	  * @return		Value converted to equivalent Input Micro Unit
	  *
	  * @throws		InvalidUnitException if the input unit passed as a parameter to this method is not among the pre-defined ones
	  */
	public BigDecimal getInternalValue( String value, String inputUnit ) throws InvalidUnitException
	{
		return unitConverter.getInternalValue( value, inputUnit );
	}

	/**
	  * Converts and returns an array of values that are an equivalent representation of the given
	  * array of values in a Micro Unit, pre-defined for the measurement types as shown below.
	  * Internally uses a method of this same instance
	  * 
	  * Measurement Type	Internal Implicit Unit
	  * ----------------	----------------------
	  * LENGTH				MICROMETER
	  * VOLUME				CUBIC_MILLIMETER
	  * WEIGHT				MICROGRAM
	  * 
	  * The values returned by this method are the ones that will be actually stored in the database.
	  * These values are implicitly in the Units shown above for the given measurement types.
	  *
	  * @param		String[]	An array of Values To be converted to equivalent Micro Unit.
	  * 						All the values should be valid numericals. If even one of them is invalid
	  *                         a NumberFormatException will be thrown by this method
	  * @param		String		Input Micro Unit
	  * 
	  * @return		An array of Values converted to equivalent Input Micro Unit
	  *
	  * @throws		InvalidUnitException if the input unit passed as a parameter to this method is not among the pre-defined ones
	  */

	public BigDecimal[] getInternalValues( String[] values, String inputUnit ) throws InvalidUnitException {

		BigDecimal[]	resultantValues	=	new BigDecimal[ values.length ];
		int valLen	=	values.length;
		for(int i=0; i < valLen; i++) {
			resultantValues[i] = getInternalValue( values[i], inputUnit );
		}
		return resultantValues;
	}


	/**
	  * Delegates the request to a method of this same Component.
	  * Convert a value internally represented in an implicit Micro Unit (shown below) to an
	  * equivalent value in a User preferred Macro Unit (The Output Unit).
	  * 
	  * Measurement Type	Internal Implicit Unit
	  * ----------------	----------------------
	  * LENGTH				MICROMETER
	  * VOLUME				CUBIC_MILLIMETER
	  * WEIGHT				MICROGRAM
	  * 
	  * The value returned by this method is the one that will be displayed to the User.
	  * This value is in the Output (User-preferred) Macro Unit passed as a parameter to this method.
	  *
	  * @param		double		Value To be converted to equivalent Output Macro Unit
	  * @param		String		Output Macro Unit
	  * 
	  * @return		Value converted to equivalent Output Macro Unit
	  *
	  * @throws		InvalidUnitException if the output unit passed as a parameter to this method is not among the pre-defined ones
	  */

	public BigDecimal getConvertedValue( double value, String outputUnit ) throws InvalidUnitException
	{
		return unitConverter.getConvertedValue( value + "", outputUnit );
	}

	/**
	  * Delegates the request to a method with the same name, parameters and return type in class UnitConversion.
	  * Convert a value internally represented in an implicit Micro Unit (shown below) to an
	  * equivalent value in a User preferred Macro Unit (The Output Unit).
	  * 
	  * Measurement Type	Internal Implicit Unit
	  * ----------------	----------------------
	  * LENGTH				MICROMETER
	  * VOLUME				CUBIC_MILLIMETER
	  * WEIGHT				MICROGRAM
	  * 
	  * The value returned by this method is the one that will be displayed to the User.
	  * This value is in the Output (User-preferred) Macro Unit passed as a parameter to this method.
	  *
	  * @param		String		Numerical Value in String form to be converted to equivalent Output Macro Unit.
	  * 						Should be a valid numerical value. If its not a numerical value, a NumberFormatException
	  *                         will be thrown.
	  * @param		String		Output Macro Unit
	  * 
	  * @return		Value converted to equivalent Output Macro Unit
	  *
	  * @throws		InvalidUnitException if the output unit passed as a parameter to this method is not among the pre-defined ones
	  */
	public BigDecimal getConvertedValue( String value, String outputUnit ) throws InvalidUnitException
	{
		return unitConverter.getConvertedValue( value, outputUnit );
	}

	/**
	  * Converts an array of values internally represented in an implicit Micro Unit (shown below) to an
	  * equivalent array of values in a User preferred Macro Unit (The Output Unit).
	  * Internally uses a method of this same instance.
	  * 
	  * Measurement Type	Internal Implicit Unit
	  * ----------------	----------------------
	  * LENGTH				MICROMETER
	  * VOLUME				CUBIC_MILLIMETER
	  * WEIGHT				MICROGRAM
	  * 
	  * The values returned by this method are the ones that will be displayed to the User.
	  * These values are in the Output (User-preferred) Macro Unit passed as a parameter to this method.
	  *
	  * @param		String[]	An array of Values To be converted to equivalent Output Macro Unit.
	  * 						All the values should be valid numericals. If even one of them is invalid
	  *                         a NumberFormatException will be thrown by this method
	  * @param		String		Output Macro Unit
	  * 
	  * @return		An array of Valuea converted to equivalent Output Macro Unit
	  *
	  * @throws		InvalidUnitException if the output unit passed as a parameter to this method is not among the pre-defined ones
	  */
	public BigDecimal[] getConvertedValues( String[] values, String outputUnit ) throws InvalidUnitException {

		BigDecimal[]	resultantValues	=	new BigDecimal[ values.length ];
		int valLen	=	values.length;
		for(int i=0; i < valLen; i++) {
			resultantValues[i] = getConvertedValue( values[i], outputUnit );
		}
		return resultantValues;
	}
	
	/**
	  * Delegates the request to a method with the same name, parameters and return type in class UnitConversion.
	  * Gets the fully qualified label for a given Unit.
	  *
	  * @param		String	Unit
	  * 
	  * @return		Label of the Unit
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined ones
	  */

	public String getLabelOfUnit(String unit) throws InvalidUnitException
	{
		return unitConverter.getLabelOfUnit( unit );
	}


	/**
	  * Delegates the request to a method with the same name, parameters and return type in class UnitConversion.
	  * Gets the SI Notation / Symbol for a given Unit.
	  *
	  * @param		String	Unit
	  * @return		Symbol (Systeme International i.e. SI Notation) of the Unit
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined (constants) ones
	  */

	public String getSymbolOfUnit(String unit) throws InvalidUnitException
	{
		return unitConverter.getSymbolOfUnit( unit );
	}



	/**
	  * This method calculates the Chargeable Weight. It accepts as parameters, the pack details, the gross weight, the units
	  * of Length and Weight (both in User Preferences). From the data it calculates the volumetric weight.
	  *
	  * A potential Client/User of this method is a Web Component that passes the request (String) data as parameters
	  * to this method and gets the Chargeable weight for display to the User.
	  *
	  * Note: UOM stands for "Unit-Of-Measurement"
	  * 
	  * The Equation for Volume-to-Weight Conversion is as shown below:
	  * 
	  * X 'Weight Unit' = Y 'Volume Unit'
	  * 
	  * Usage of Parameters in the order found:
	  * 
	  * Input Volume		 : The Input Vaolume Value to be converted to weight using an equation
	  * Input Volume Unit	 : The Unit of the Volume Value
	  * Output Weight Unit	 : The Weight Unit in which the Output Value of this method will be
	  * Equated Weight Value : The 'X' in the above Equation
	  * Equation Weight Unit : The 'Weight Unit' in the above Equation
	  * Equated Volume Value : The 'Y' in the above Equation
	  * Equation Volume Unit : The 'Volume Unit' in the above Equation
	  *
	  * @param		String[]	Length of the pack
	  * @param		String[]	Width of the Pack
	  * @param		String[]	Height of the Pack
	  * @param		String[]	Number of individual Packs having the above corresponding measurements
	  * @param		String		User Provided Gross Weight in User-preferred Weight Unit
	  * @param		String		User-preferred Length Unit (from User Preferences)
	  * @param		String		User-preferred Volume Unit (from User Preferences)
	  * @param		String		User-preferred Weight Unit (from User Preferences) 
	  * @param		BigDecimal	Equated Weight Value
	  * @param		String		Equation Weight Unit
	  * @param		BigDecimal	Equated Volume Value
	  * @param		String		Equation Volume Unit
	  * 
	  * @return		Calculated Chargeable Weight Value in the User-preferred Weight Unit
	  *
	  * @throws		InvalidUnitException if any of the units passed as parameters to this method are not among the pre-defined ones
	  */

	public	BigDecimal	getChargeableWeight(	String[]	 lengths,			// The Length of pack(s)
												String[]	 widths,			// The Width of pack(s)
												String[]	 heights,			// The Height of pack(s)
												String[]	 noOfPacks,			// Number of Packs
												String		 grossWeight,		// Actual Weight (Gross Weight) entered by the User
												String		 lengthUOM,			// the LENGTH UOM from Login User Preferences
												String		 volumeUOM,			// the VOLUME UOM from Login User Preferences
												String		 weightUOM,			// the WEIGHT UOM from Login User Preferences

																				// Consider the Equation :
																				// X Weight Unit(s) = Y Volume Unit(s)
												BigDecimal	 equatedWeightValue,// The 'X' in the above eqation
												String		 equationWeightUnit,// The 'Weight Unit' in the above equation
												BigDecimal	 equatedVolumeValue,// The 'Y' in the above eqation
												String		 equationVolumeUnit	// The 'Volume Unit' in the above equation
											) throws InvalidUnitException
	{

		//Logger.info( fileName, "grossWeight = "+grossWeight);

		// The storage variable for Chargeable Weight for returning to the client
		BigDecimal	chargeableWeight	=	null;

		// The Pack Details	i.e. Length, Width and Height for each pack along with the number of such Packs
		BigDecimal[][]	packDetails	=	null;

		// Calculated by totaling the Volumes of each pack
		BigDecimal	totalVolumeOfPacks		=	new BigDecimal("0.0");

		// Calculated from the Total Volume of the Packs
		BigDecimal	totalVolumetricWeight	=	new BigDecimal("0.0");

		// The Actual Weight (Gross Weight) represented as a number instead of the String value
		BigDecimal	totalGrossWeight	=	new BigDecimal( grossWeight );

		// Convert Inputs (Strings) to BigDecimal Values (Numbers)

			packDetails	=	new BigDecimal[ lengths.length ][];
			int pakDtlLen	=	packDetails.length;
			for(int i=0; i < pakDtlLen; i++) {
				for(int j=0; j < packDetails[i].length; j++) {
					packDetails[i]	=	new BigDecimal[  packDetails[i].length ];
				}
				//System.out.println("packDetails["+i+"].length = "+packDetails[i].length);
			}

		// Calculate Volume from Pack Details
			
			for(int i=0; i < pakDtlLen; i++) {

				BigDecimal	volumeOfPacks		=	null;
				boolean		volumeCalculated	=	false;

				// Calculate Toal Volume for each pack and multiply it with the number of packs
				for(int j=0; j < packDetails[i].length; j++) {
					if(j==0) {
						volumeOfPacks	=	new BigDecimal("1.0");
					}
					// Keep on multiplying i.e. L * B * H * Number-of-Packs
					volumeOfPacks		=	volumeOfPacks.multiply( packDetails[i][j] );
					volumeCalculated	=	true;
				}
				// Add the volumetric weight of each pack to the total volumetric weight of packs
				if(volumeOfPacks != null && volumeCalculated) {
					totalVolumeOfPacks = totalVolumeOfPacks.add( volumeOfPacks );
				}
			}

		// Decide the volumetric measurement

			String	equivalentVolumeUOM	=	unitConverter.getVolumeUnit( lengthUOM );

		// Calculate Volumetric Weight

			totalVolumetricWeight = unitConverter.convertVolumeToWeight(	totalVolumeOfPacks,
																			equivalentVolumeUOM,
																			weightUOM,
																			equatedWeightValue,
																			equationWeightUnit,
																			equatedVolumeValue,
																			equationVolumeUnit
																	);

			//Logger.info( fileName, "totalVolumeOfPacks = "+totalVolumeOfPacks+" "+equivalentVolumeUOM+", totalVolumetricWeight = "+totalVolumetricWeight );
			
		// Decide Chargeable Weight by comparing Volumetric Weight with Gross Weight.

			int compareFlag	=	totalGrossWeight.compareTo( totalVolumetricWeight  );

			// Decide which is higher

			if(compareFlag < 0) {		// totalGrossWeight is < totalVolumetricWeight
				chargeableWeight	=	totalVolumetricWeight;
			}
			if(compareFlag == 0) {		// totalGrossWeight is = totalVolumetricWeight
				chargeableWeight	=	totalGrossWeight;
			}
			if(compareFlag > 0) {		// totalGrossWeight is > totalVolumetricWeight
				chargeableWeight	=	totalGrossWeight;
			}

			// set the precision and the rounding mode
			chargeableWeight	=	chargeableWeight.setScale( FoursoftConfig.WEIGHT_DECIMAL_PRECISION, BigDecimal.ROUND_HALF_EVEN );
			//Logger.info( fileName, "chargeableWeight = "+chargeableWeight );
			
			// return the Final Chargeable Weight
			return chargeableWeight;

	} // end getChargeableWeight



	/**
	  * This method calculates the Chargeable Weight. It accepts as parameters, the pack details, the gross weight, the units
	  * of Length and Weight (both in User Preferences). From the data it calculates the volumetric weight.
	  *
	  * A potential Client/User of this method is a Web Component that passes the request (String) data as parameters
	  * to this method and gets the Chargeable weight for display to the User.
	  *
	  * Note: UOM stands for "Unit-Of-Measurement"
	  * 
	  * The Equation for Volume-to-Weight Conversion is as shown below:
	  * 
	  * X 'Weight Unit' = Y 'Volume Unit'
	  * 
	  * Usage of Parameters in the order found:
	  * 
	  * Input Volume		 : The Input Vaolume Value to be converted to weight using an equation
	  * Input Volume Unit	 : The Unit of the Volume Value
	  * Output Weight Unit	 : The Weight Unit in which the Output Value of this method will be
	  * Equated Weight Value : The 'X' in the above Equation
	  * Equation Weight Unit : The 'Weight Unit' in the above Equation
	  * Equated Volume Value : The 'Y' in the above Equation
	  * Equation Volume Unit : The 'Volume Unit' in the above Equation
	  *
	  * @param		String[]	Length of the pack
	  * @param		String[]	Width of the Pack
	  * @param		String[]	Height of the Pack
	  * @param		String[]	Number of individual Packs having the above corresponding measurements
	  * @param		String		User Provided Gross Weight in User-preferred Weight Unit
	  * @param		String		User-preferred Length Unit (from User Preferences)
	  * @param		String		User-preferred Volume Unit (from User Preferences)
	  * @param		String		User-preferred Weight Unit (from User Preferences) 
	  * @param		BigDecimal	Equated Weight Value
	  * @param		String		Equation Weight Unit
	  * @param		BigDecimal	Equated Volume Value
	  * @param		String		Equation Volume Unit
	  * 
	  * @return		Calculated Chargeable Weight Value in the User-preferred Weight Unit
	  *
	  * @throws		InvalidUnitException if any of the units passed as parameters to this method are not among the pre-defined ones
	  */

	public	BigDecimal	getChargeableVolume(	String[]	 lengths,			// The Length of pack(s)
												String[]	 widths,			// The Width of pack(s)
												String[]	 heights,			// The Height of pack(s)
												String[]	 noOfPacks,			// Number of Packs
												String		 grossWeight,		// Actual Weight (Gross Weight) entered by the User
												String		 lengthUOM,			// the LENGTH UOM from Login User Preferences
												String		 volumeUOM,			// the VOLUME UOM from Login User Preferences
												String		 weightUOM,			// the WEIGHT UOM from Login User Preferences

																				// Consider the Equation :
																				// X Weight Unit(s) = Y Volume Unit(s)
												BigDecimal	 equatedWeightValue,// The 'X' in the above eqation
												String		 equationWeightUnit,// The 'Weight Unit' in the above equation
												BigDecimal	 equatedVolumeValue,// The 'Y' in the above eqation
												String		 equationVolumeUnit	// The 'Volume Unit' in the above equation
											) throws InvalidUnitException
	{

		//Logger.info( fileName, "grossWeight = "+grossWeight);

		// The storage variable for Chargeable Weight for returning to the client
		BigDecimal	chargeableWeight	=	null;

		// The Pack Details	i.e. Length, Width and Height for each pack along with the number of such Packs
		BigDecimal[][]	packDetails	=	null;

		// Calculated by totaling the Volumes of each pack
		BigDecimal	totalVolumeOfPacks		=	new BigDecimal("0.0");

		// Calculated from the Total Volume of the Packs
		BigDecimal	totalVolumetricWeight	=	new BigDecimal("0.0");

		// The Actual Weight (Gross Weight) represented as a number instead of the String value
		BigDecimal	totalGrossWeight	=	new BigDecimal( grossWeight );

		// Convert Inputs (Strings) to BigDecimal Values (Numbers)

			packDetails	=	new BigDecimal[ lengths.length ][];
				int pakDtlLen	=	packDetails.length;
			for(int i=0; i < pakDtlLen; i++) {
				for(int j=0; j < packDetails[i].length; j++) {
					packDetails[i]	=	new BigDecimal[  packDetails[i].length ];
				}
				//System.out.println("packDetails["+i+"].length = "+packDetails[i].length);
			}

		// Calculate Volume from Pack Details

			for(int i=0; i < pakDtlLen; i++) {

				BigDecimal	volumeOfPacks		=	null;
				boolean		volumeCalculated	=	false;

				// Calculate Toal Volume for each pack and multiply it with the number of packs
				for(int j=0; j < packDetails[i].length; j++) {
					if(j==0) {
						volumeOfPacks	=	new BigDecimal("1.0");
					}
					// Keep on multiplying i.e. L * B * H * Number-of-Packs
					volumeOfPacks		=	volumeOfPacks.multiply( packDetails[i][j] );
					volumeCalculated	=	true;
				}
				// Add the volumetric weight of each pack to the total volumetric weight of packs
				if(volumeOfPacks != null && volumeCalculated) {
					totalVolumeOfPacks = totalVolumeOfPacks.add( volumeOfPacks );
				}
			}

		// Decide the volumetric measurement

			String	equivalentVolumeUOM	=	unitConverter.getVolumeUnit( lengthUOM );

		// Calculate Volumetric Weight

			totalVolumetricWeight = unitConverter.convertWeightToVolume(	totalVolumeOfPacks,
																			equivalentVolumeUOM,
																			weightUOM,
																			equatedWeightValue,
																			equationWeightUnit,
																			equatedVolumeValue,
																			equationVolumeUnit
																	);

			//Logger.info( fileName, "totalVolumeOfPacks = "+totalVolumeOfPacks+" "+equivalentVolumeUOM+", totalVolumetricWeight = "+totalVolumetricWeight );
			
		// Decide Chargeable Weight by comparing Volumetric Weight with Gross Weight.

			int compareFlag	=	totalGrossWeight.compareTo( totalVolumetricWeight  );

			// Decide which is higher

			if(compareFlag < 0) {		// totalGrossWeight is < totalVolumetricWeight
				chargeableWeight	=	totalVolumetricWeight;
			}
			if(compareFlag == 0) {		// totalGrossWeight is = totalVolumetricWeight
				chargeableWeight	=	totalGrossWeight;
			}
			if(compareFlag > 0) {		// totalGrossWeight is > totalVolumetricWeight
				chargeableWeight	=	totalGrossWeight;
			}

			// set the precision and the rounding mode
			chargeableWeight	=	chargeableWeight.setScale( FoursoftConfig.WEIGHT_DECIMAL_PRECISION, BigDecimal.ROUND_HALF_EVEN );
			//Logger.info( fileName, "chargeable Volume = "+chargeableWeight );
			
			// return the Final Chargeable Weight
			return chargeableWeight;

	} // end getChargeableVolume

	/**
	  * Converts the given Input Rate Amount Value (in BigDecimal form) in a given Input Unit
	  * to a BigDecimal Value in a given Output Unit.
	  *
	  * @param		BigDecimal	Input Value
	  * @param		String		Input Unit
	  * @param		String		Output Unit
	  * 
	  * @return		Value in Output Unit
	  *
	  * @throws		InvalidUnitException if the input unit or the ourput unit
	  * 			passed as a parameters to this method are not among the
	  *             pre-defined ones
	  */

	public BigDecimal getConvertedRate(  BigDecimal value, String inputUnit, String outputUnit ) throws InvalidUnitException {
		return unitConverter.getUnitConversion( value, inputUnit, outputUnit );
	}

	/**
	  * Converts the given Input Rate Amount Value (in double form) in a given Input Unit
	  * to a BigDecimal Value in a given Output Unit.
	  * An over-loaded version of the method :
	  * getConvertedRate(  BigDecimal value, String inputUnit, String outputUnit ).
	  * Calls this same method internally.
	  *
	  * @param		double		Input Value
	  * @param		String		Input Unit
	  * @param		String		Output Unit
	  * 
	  * @return		Value in Output Unit
	  *
	  * @throws		InvalidUnitException if the input unit or the ourput unit
	  * 			passed as a parameters to this method are not among the
	  *             pre-defined ones
	  */

	public BigDecimal getConvertedRate(  double value, String inputUnit, String outputUnit ) throws InvalidUnitException {
		return unitConverter.getUnitConversion( new BigDecimal(value), inputUnit, outputUnit );
	}

	/**
	  * Converts the given Input Rate Amount Value (in String form) in a given Input Unit
	  * to a BigDecimal Value in a given Output Unit.
	  * An over-loaded version of the method :
	  * getConvertedRate(  BigDecimal value, String inputUnit, String outputUnit ).
	  * Calls this same method internally.
	  *
	  * @param		String		Input Value
	  * @param		String		Input Unit
	  * @param		String		Output Unit
	  * 
	  * @return		Value in Output Unit
	  *
	  * @throws		InvalidUnitException if the input unit or the ourput unit
	  * 			passed as a parameters to this method are not among the
	  *             pre-defined ones
	  */

	public BigDecimal getConvertedRate(  String value, String inputUnit, String outputUnit ) throws InvalidUnitException {
		return unitConverter.getUnitConversion( new BigDecimal(value), inputUnit, outputUnit );
	}

}
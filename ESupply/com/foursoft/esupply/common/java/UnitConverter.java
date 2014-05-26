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
 * File			: UnitConverter.java
 * sub-module 	: Common
 * module 		: eSupply
 * 
 * This is an interface UnitConverter that defines all methods related to conversion of units and
 * also volume to weight for different shipment modes
 * 
 * @author	Amit Parekh 
 * @date	27-12-2002
 */

import java.math.BigDecimal;
import com.foursoft.esupply.common.exception.InvalidUnitException;

public interface UnitConverter  {

	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  MEASUREMENTS TYPES  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

	// Pre-defined internal symbols for measurements
	/**
	 * The Constant for the Measurement Type of "LENGTH" that will be referred throughout the application
	 */
	public String	LENGTH		=	"LENGTH";

	/**
	 * The Constant for the Measurement Type of "VOLUME" that will be referred throughout the application
	 */
	public String	VOLUME		=	"VOLUME";

	/**
	 * The Constant for the Measurement Type of "WEIGHT" that will be referred throughout the application
	 */
	public String	WEIGHT		=	"WEIGHT";

	/**
	 * Refers to the Measurement Types already defined as constants
	 */
	// Supported measurements out of the ones defined as constants
	public String[]	SUPPORTED_MEASUREMENTS	=	{	LENGTH,
													VOLUME,
													WEIGHT
												};

	/**
	 * Systeme International Compliant Notations for the Measurement Types
	 */
	// S.I. compliant nonations for Supported LENGTH units
	public String[]	MEASUREMENT_LABELS		=	{	"Length",
													"Volume",
													"Mass"
												};

	// Pre-defined internal symbols for measurement of LENGTH
	/**
	 * The Constant for the LENGTH Unit Meter, that will be referred throughout the application
	 */
	public String	METER		=	"mt";
	
	/**
	 * The Constant for the LENGTH Unit Centimeter, that will be referred throughout the application
	 */
	public String	CENTIMETER	=	"cm";
	
	/**
	 * The Constant for the LENGTH Unit Feet, that will be referred throughout the application
	 */
	public String	FEET		=	"ft";
	
	/**
	 * The Constant for the LENGTH Unit Inch, that will be referred throughout the application
	 */
	public String	INCH		=	"in";

	// Pre-defined internal symbol of internal unit MICROMETER for conversion of measurements of LENGTH
	/**
	 * The Constant for the LENGTH Unit Micrometer, that will be referred throughout the application
	 */
	public String	MICROMETER	=	"Mm";

	/**
	 * Enumeration of the Constants for LENGTH Units that will be referred throughout the application
	 */
	// Supported LENGTH units out of the ones defined as constants
	public String[]	SUPPORTED_LENGTH_UNITS	=	{	CENTIMETER,
													INCH,
													FEET,
													METER
												};

	/**
	 * Enumeration of the Labels for LENGTH Units that will be referred throughout the application
	 */
	// Fully qualified labels for LENGTH Units
	public String[]	LENGTH_UNIT_LABELS		=	{	"Centimeter",
													"Inch",
													"Feet",
													"Meter"
												};

	/**
	 * Enumeration of the S.I. Compliant Notations for LENGTH Units that will be referred throughout the application
	 */
	// S.I. compliant nonations for Supported LENGTH units
	public String[]	LENGTH_UNIT_SYMBOLS		=	{	"cm",
													"in",
													"ft",
													"m"
												};

	// Pre-defined internal symbols for measurement of VOLUME
	/**
	 * The Constant for the VOLUME Unit Meter, that will be referred throughout the application
	 */
	public String	CUBIC_METER			=	"mt3";

	/**
	 * The Constant for the VOLUME Unit Cubic Centimeter, that will be referred throughout the application
	 */
	public String	CUBIC_CENTIMETER	=	"cm3";

	/**
	 * The Constant for the VOLUME Unit Cubic  Feet, that will be referred throughout the application
	 */
	public String	CUBIC_FEET			=	"ft3";

	/**
	 * The Constant for the VOLUME Unit Cubic Inch, that will be referred throughout the application
	 */
	public String	CUBIC_INCH			=	"in3";

	// Pre-defined internal symbol of internal unit CUBIC MILLIMETER for conversion of measurements of VOLUME
	/**
	 * The Constant for the VOLUME Unit Cubic Millimeter, that will be referred throughout the application
	 */
	public String	CUBIC_MILLIMETER	=	"mm3";

	// Supported VOLUME units out of the ones defined as constants
	/**
	 * Enumeration of the Constants for VOLUME Units that will be referred throughout the application
	 */
	public String[]	SUPPORTED_VOLUME_UNITS	=	{	CUBIC_CENTIMETER,
													CUBIC_INCH,
													CUBIC_FEET,
													CUBIC_METER
												};

	// Fully qualified labels for VOLUME Units
	/**
	 * Enumeration of the Labels for VOLUME Units that will be referred throughout the application
	 */
	public String[]	VOLUME_UNIT_LABELS		=	{	"Cubic Centimeter",
													"Cubic Inch",
													"Cubic Feet",
													"Cubic Meter"
												};

	// S.I. compliant nonations for Supported VOLUME units
	/**
	 * Enumeration of the S.I. Compliant Notations for VOLUME Units that will be referred throughout the application
	 */
	public String[]	VOLUME_UNIT_SYMBOLS		=	{	"cu.cm",
													"cu.in",
													"cu.ft",
													"cu.m"
												};

	// Pre-defined internal symbols for measurement of WEIGHT
	/**
	 * The Constant for the WEIGHT Unit Kilogram, that will be referred throughout the application
	 */
	public String	KILOGRAM	=	"kg";

	/**
	 * The Constant for the WEIGHT Unit Pouns, that will be referred throughout the application
	 */
	public String	POUND		=	"lb";

	// Pre-defined internal symbol of internal unit MILLIGRAMS for conversion of measurements of WEIGHT
	/**
	 * The Constant for the WEIGHT Unit Microgram, that will be referred throughout the application
	 */
	public String	MICROGRAM	=	"Mg";

	// Supported WEIGHT units out of the ones defined as constants
	/**
	 * Enumeration of the Constants for WEIGHT Units that will be referred throughout the application
	 */
	public String[]	SUPPORTED_WEIGHT_UNITS	=	{	KILOGRAM,
													POUND
												};

	// Fully qualified labels for WEIGHT Units
	/**
	 * Enumeration of the Labels for WEIGHT Units that will be referred throughout the application
	 */
	public String[]	WEIGHT_UNIT_LABELS		=	{	"Kilogram",
													"Pound"
												};

	// S.I. compliant nonations for Supported WEIGHT units
	/**
	 * Enumeration of the S.I. Compliant Notations for WEIGHT Units that will be referred throughout the application
	 */
	public String[]	WEIGHT_UNIT_SYMBOLS		=	{	"kg",
													"lb"
												};


	/**
	  *	Gets the fully qualified label for a given Unit.
	  *
	  * @param		String	Unit
	  * 
	  * @return		Label of the Unit
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined ones
	  */
	public	String		getLabelOfUnit(String unit) throws InvalidUnitException;


	/**
	  *	Gets the SI Notation / Symbol for a given Unit.
	  *
	  * @param		String	Unit
	  * @return		Symbol (Systeme International i.e. SI Notation) of the Unit
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined (constants) ones
	  */
	public	String		getSymbolOfUnit(String unit) throws InvalidUnitException;


	/**
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
	public	BigDecimal	getInternalValue(double value, String inputUnit) throws InvalidUnitException;

	/**
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
	public	BigDecimal	getInternalValue(String value, String inputUnit) throws InvalidUnitException;


	/**
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
	public	BigDecimal	getConvertedValue(double value, String outputUnit) throws InvalidUnitException;

	/**
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
	public	BigDecimal	getConvertedValue(String value, String outputUnit) throws InvalidUnitException;

	/**
	  * Converts a Volume Value in a given Volume Unit to an equivalent Weight Value in a given Weight Unit
	  * using the Volume-to-Weight Conversion equation passed as a parameter to it.
	  * 
	  * The Equation is as shown below:
	  * 
	  * X 'Weight Unit' = Y 'Volume Unit'
	  * 
	  * Usage of Parameters in the order found:
	  * 
	  * Input Volume		 : The Input Volume Value to be converted to weight using an equation
	  * Input Volume Unit	 : The Unit of the Volume Value
	  * Output Weight Unit	 : The Weight Unit in which the Output Value of this method will be
	  * Equated Weight Value : The 'X' in the above Equation
	  * Equation Weight Unit : The 'Weight Unit' in the above Equation
	  * Equated Volume Value : The 'Y' in the above Equation
	  * Equation Volume Unit : The 'Volume Unit' in the above Equation
	  * 
	  *
	  * @param		BigDecimal	Input Volume
	  * @param		String		Input Volume Unit
	  * @param		String		Output Weight Unit
	  * @param		BigDecimal	Equated Weight Value
	  * @param		String		Equation Weight Unit
	  * @param		BigDecimal	Equated Volume Value
	  * @param		String		Equation Volume Unit
	  * 
	  * @return		Calculated Weight Value in Output Weight Unit
	  *
	  * @throws		InvalidUnitException if any of the units passed as parameters to this method are not among the pre-defined ones
	  */
	public	BigDecimal	convertVolumeToWeight(	BigDecimal	inputVolume,
												String		volumeUnit,
												String		weightUnit,
												BigDecimal	equatedWeightValue,
												String		equationWeightUnit,
												BigDecimal	equatedVolumeValue,
												String		equationVolumeUnit
											) throws InvalidUnitException;	


	/**
	  * Converts a Weight Value in a given Weight Unit to an equivalent Volume Value in a given Volume Unit
	  * using the Weight-to-Volume Conversion equation passed as a parameter to it.
	  * 
	  * The Equation is as shown below:
	  * 
	  * X 'Volume Unit' = Y 'Weight Unit'
	  * 
	  * Usage of Parameters in the order found:
	  * 
	  * Input Weight		 : The Input Weight Value to be converted to volume using an equation
	  * Input Weight Unit	 : The Unit of the Weight Value
	  * Output Volume Unit	 : The Volume Unit in which the Output Value of this method will be
	  * Equated Volume Value : The 'X' in the above Equation
	  * Equation Volume Unit : The 'Volume Unit' in the above Equation
	  * Equated Weight Value : The 'Y' in the above Equation
	  * Equation Weight Unit : The 'Weight Unit' in the above Equation
	  *
	  * @param		BigDecimal	Input Weight
	  * @param		String		Input Weight Unit
	  * @param		String		Output Volume Unit
	  * @param		BigDecimal	Equated Volume Value
	  * @param		String		Equation Volume Unit
	  * @param		BigDecimal	Equated Weight Value
	  * @param		String		Equation Weight Unit
	  * 
	  * @return		Calculated Volume Value in Output Volume Unit
	  *
	  * @throws		InvalidUnitException if any of the units passed as parameters to this method are not among the pre-defined ones
	  */
	public	BigDecimal	convertWeightToVolume(	BigDecimal	inputWeight,
												String		weightUnit,
												String		volumeUnit,
												BigDecimal	equatedVolumeValue,
												String		equationVolumeUnit,
												BigDecimal	equatedWeightValue,
												String		equationWeightUnit
											) throws InvalidUnitException;	

	/**
	  *	Finds the equivalent Volume nit of s Given Length Unit
	  * e.g. CUBIC_CENTIMETER for CENTIMETER or CUBIC_INCH for INCH and so on. 
	  *
	  * @param		String	Length Unit
	  * @return		Equivalent Volume Unit
	  *
	  * @throws		InvalidUnitException if the length unit passed as a parameter to this method is not among the pre-defined (constants) ones
	  */
	public String getVolumeUnit(String lengthUnit) throws InvalidUnitException;


	/**
	  * Converts a Input Value in a given Input Unit to a Value in a given Output Unit.
	  *
	  * @param		BigDecimal	Input Value
	  * @param		String		Input Unit
	  * @param		String		Output Unit
	  * 
	  * @return		Value in Output Unit
	  *
	  * @throws		InvalidUnitException if the input unit or ourput units
	  * 			passed as a parameter to this method are not among the pre-defined ones
	  */
	public BigDecimal getUnitConversion(	BigDecimal	value,
											String		inputUnit,
											String		outputUnit
										) throws InvalidUnitException;

	/**
	  * Gets the Rounding Mode for conversion to Internally represented Value from External Value
	  * 
	  * @return		Rounding Mode
	  */
	public int getInternalValueRMode();


	/**
	  * Gets the Rounding Mode for conversion from Internally represented Value to External Value
	  * 
	  * @return		Rounding Mode
	  */
	public int getConvertedValueRMode();


	/**
	  * Gets the Rounding Mode for calculation of Volumetric Weight
	  * 
	  * @return		Rounding Mode
	  */
	public int getVolumetricWeightRMode();
	

	/**
	  * Sets the Rounding Mode for conversion to Internally represented Value from External Value
	  *
	  * @param		int		Rounding Mode
	  * 
	  * @return		void
	  */
	public void setInternalValueRMode( int mode );


	/**
	  * Sets the Rounding Mode for conversion from Internally represented Value to External Value
	  *
	  * @param		int		Rounding Mode
	  * 
	  * @return		void
	  */
	public void setConvertedValueRMode( int mode );

	/**
	  * Sets the Rounding Mode for calculation of Volumetric Weight
	  *
	  * @param		int		Rounding Mode
	  * 
	  * @return		void
	  */
	public void setVolumetricWeightRMode( int mode );

}
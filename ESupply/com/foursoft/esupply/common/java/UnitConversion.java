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
 * File			: UnitConversion.java
 * sub-module 	: Common
 * module 		: eSupply
 * 
 * This is a helper Class which actually implements the interface UnitConverter and hence implements
 * all the methods related to conversion of units and also volume to weight for different
 * shipment modes. For each User interacting with the system, there will be an associated object
 * of this class being used through its interface UnitConverter
 * 
 * @author	Amit Parekh 
 * @date	27-12-2002
 */
 
import java.util.HashMap;
import java.math.BigDecimal;

import com.foursoft.esupply.common.exception.InvalidUnitException;
import com.foursoft.esupply.common.java.FoursoftWebConfig;
import com.foursoft.esupply.common.java.UnitConverter;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

public final class UnitConversion implements UnitConverter {

	/**
	 * The name of this File for used for the purpose of Logging
	 */
	private static final String	fileName	=	"UnitConversion.java";
  private static Logger logger = null;
	/**
	 * The Rounding Mode for conversion to Internally represented Value from External Value
	 * Default : BigDecimal.ROUND_HALF_EVEN
	 */
	private int internalValueRMode		=	BigDecimal.ROUND_HALF_EVEN;

	/**
	 * The Rounding Mode for conversion from Internally represented Value to External Value
	 * Default : BigDecimal.ROUND_HALF_EVEN
	 */
	private int convertedValueRMode		=	BigDecimal.ROUND_HALF_EVEN;

	/**
	 * The Rounding Mode for calculation of Volumetric Weight
	 * Default : BigDecimal.ROUND_HALF_EVEN
	 */
	private int volumetricWeightRMode	=	BigDecimal.ROUND_HALF_EVEN;

	// Default Protected Constructor. Used by the Factory to create an instance of this class
	/**
	 * Sole Constructor (For invocation by subclass constructors, typically
     * implicit.)
	 */
	protected UnitConversion(){
    logger  = Logger.getLogger(UnitConversion.class);
	}

	// LENGTH

	/**
	 * The Conversion factors to convert from a given value to an  equivalent Internal Value
	 */
	private	static final HashMap	LENGTH_CONVERSIONS	=	new HashMap(5);

	// Conversions for LENGTH units to MICROMETER
	static {	// Initialize values when the class is loaded
		LENGTH_CONVERSIONS.put( METER			, new BigDecimal( "1000000" ) );
		LENGTH_CONVERSIONS.put( CENTIMETER		, new BigDecimal(   "10000" ) );
		LENGTH_CONVERSIONS.put( FEET			, new BigDecimal(  "304800" ) );
		LENGTH_CONVERSIONS.put( INCH			, new BigDecimal(   "25400" ) );
	}

	// VOLUME
	/**
	 * The Conversion factors to convert from a given value to an  equivalent Internal Value
	 */ 
	private	static final HashMap	VOLUME_CONVERSIONS	=	new HashMap(4);

	// Conversions for VOLUME units to CUBIC MILLIMETER
	static {	// Initialize values when the class is loaded
		VOLUME_CONVERSIONS.put( CUBIC_METER		, new BigDecimal( "1000000000.000" ) );
		VOLUME_CONVERSIONS.put( CUBIC_CENTIMETER, new BigDecimal(       "1000.000" ) );
		VOLUME_CONVERSIONS.put( CUBIC_FEET		, new BigDecimal(   "28316846.592" ) );
		VOLUME_CONVERSIONS.put( CUBIC_INCH		, new BigDecimal(      "16387.064" ) );
	}
	
	// WEIGHT
	/**
	 * The Conversion factors to convert from a given value to an  equivalent Internal Value
	 */ 
	private	static final HashMap	WEIGHT_CONVERSIONS	=	new HashMap(2);

	// Conversions for WEIGHT units to MICROGRAMS
	static {	// Initialize values when the class is loaded
		WEIGHT_CONVERSIONS.put( KILOGRAM	, new BigDecimal( "1000000000" ) );
		WEIGHT_CONVERSIONS.put( POUND		, new BigDecimal(  "453592370" ) );
	}

	/**
	  * Gets the Notation or Label for a given Measurement Type (LENGTH, VOLUME, WEIGHT )
	  *
	  * @param		String		Measurement Type Internal Constant
	  * 
	  * @return		Measurement Type Label
	  */

	// Get nonations/labels for measurements
	public String getMeasurementLabel(String measurement) {

		String label = "";
		for(int i=0; i < MEASUREMENT_LABELS.length; i++) {
			if(measurement.equals( SUPPORTED_MEASUREMENTS[i] )) {
				label	=	MEASUREMENT_LABELS[i];
				break;
			}
		}
		return label;
	}

	// TUNING METHODS

	/**
	  * Sets the Rounding Mode for conversion to Internally represented Value from External Value
	  *
	  * @param		int		Rounding Mode
	  * 
	  * @return		void
	  */
	  
	public void setInternalValueRMode(int roundingMode) {
		this.internalValueRMode		=	roundingMode;
	}

	/**
	  * Sets the Rounding Mode for conversion from Internally represented Value to External Value
	  *
	  * @param		int		Rounding Mode
	  * 
	  * @return		void
	  */
	  
	public void setConvertedValueRMode(int roundingMode) {
		this.convertedValueRMode	=	roundingMode;
	}

	/**
	  * Sets the Rounding Mode for calculation of Volumetric Weight
	  *
	  * @param		int		Rounding Mode
	  * 
	  * @return		void
	  */
	  
	public void setVolumetricWeightRMode(int roundingMode) {
		this.volumetricWeightRMode	=	roundingMode;
	}

	/**
	  * Gets the Rounding Mode for conversion to Internally represented Value from External Value
	  * 
	  * @return		Rounding Mode
	  */
	  
	public int getInternalValueRMode() {
		return this.internalValueRMode;
	}

	/**
	  * Gets the Rounding Mode for conversion from Internally represented Value to External Value
	  * 
	  * @return		Rounding Mode
	  */
	  
	public int getConvertedValueRMode() {
		return this.convertedValueRMode;
	}

	/**
	  * Gets the Rounding Mode for calculation of Volumetric Weight
	  * 
	  * @return		Rounding Mode
	  */
	  
	public int getVolumetricWeightRMode() {
		return this.volumetricWeightRMode;
	}

	// UTILITY METHODS

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

	public BigDecimal getUnitConversion(  BigDecimal value, String inputUnit, String outputUnit ) throws InvalidUnitException {

		BigDecimal convValue	=	null;

		boolean	found		=	false;

		HashMap	CONVERSIONS	=	null;

		String	measurementTypeInputUnit	=	getMeasurementTypeOfUnit( inputUnit );
		String	measurementTypeOutputUnit	=	getMeasurementTypeOfUnit( outputUnit );

		if(measurementTypeInputUnit!=null && measurementTypeOutputUnit != null && measurementTypeInputUnit.equals( measurementTypeOutputUnit )) {

			// Only if the two Units are of the same measurement type, proceed, else throw execption
			found = true;

			if(measurementTypeInputUnit.equals( LENGTH )) {
				// Set the reference to the LENGTH conversion tables
				CONVERSIONS	=	LENGTH_CONVERSIONS;
			}
			if(measurementTypeInputUnit.equals( VOLUME )) {
				// Set the reference to the VOLUME conversion tables
				CONVERSIONS	=	VOLUME_CONVERSIONS;
			}
			if(measurementTypeInputUnit.equals( WEIGHT )) {
				// Set the reference to the WEIGHT conversion tables
				CONVERSIONS	=	WEIGHT_CONVERSIONS;
			}

			try {

				if(!inputUnit.equals( outputUnit )) {

					
					if(outputUnit.equals( MICROMETER ) || outputUnit.equals( CUBIC_MILLIMETER) || outputUnit.equals( MICROGRAM )) {
						// If the Output Units are Micro Units
						// Directly return the valye from the table.
						convValue	=	(BigDecimal) CONVERSIONS.get( inputUnit );
					} else {
						// If the Output Units are Macro Units
						BigDecimal	inputConvFactor		= (BigDecimal) CONVERSIONS.get( inputUnit  );
						BigDecimal	outputConvFactor	= (BigDecimal) CONVERSIONS.get( outputUnit );

						convValue = ( value.multiply( inputConvFactor ) ).divide( outputConvFactor, FoursoftWebConfig.INTERNAL_CALCULATION_PRECISION, BigDecimal.ROUND_UP );

						//Logger.info( fileName,"           value = "+value );
						//Logger.info( fileName,"       inputUnit = "+inputUnit );
						//Logger.info( fileName,"      outputUnit = "+outputUnit );
						//Logger.info( fileName," inputConvFactor = "+inputConvFactor );
						//Logger.info( fileName,"outputConvFactor = "+outputConvFactor );
						//Logger.info( fileName,"       convValue = "+convValue );
					}

				} else {
					// No conversion required
					convValue = value;
				}

				//Logger.info( fileName,"    CONVERTED VALUE = "+convValue);

			} catch (Exception e) {
				//Logger.error( fileName, "Exception while converting unit", e);
        logger.error( fileName+ "Exception while converting unit"+ e);
			}

		}

		if(!found) {
			throw new InvalidUnitException("The Unit(s) seems to be invalid.");
		}

		return convValue;
	}

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
	  * Input Volume		 : The Input Vaolume Value to be converted to weight using an equation
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
												String		inputVolumeUnit,
												String		outputWeightUnit,
												BigDecimal	equatedWeightValue,
												String		equationWeightUnit,
												BigDecimal	equatedVolumeValue,
												String		equationVolumeUnit
											) throws InvalidUnitException
	{

		BigDecimal	volumetricWeight	= new BigDecimal("0.0");
		boolean		found				= false;

		// 1.	Check if the Units passed as parameters to the method are valid or not. If not throw an exception.
		// 2.	Check if the Volume's Volume Unit is valid or not. If not throw an exception.

		if(inputVolumeUnit==null || inputVolumeUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+inputVolumeUnit+"' is not a valid Volume unit.");
		}
		if(outputWeightUnit==null || outputWeightUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+outputWeightUnit+"' is not a valid Weight unit.");
		}
		if(equationVolumeUnit==null || equationVolumeUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+equationVolumeUnit+"' is not a valid Volume unit.");
		}
		if(equationWeightUnit==null || equationWeightUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+equationWeightUnit+"' is not a valid Weight unit.");
		}		

		// Get the type of measurement for the unit
		String	measurementType	=	getMeasurementTypeOfUnit( inputVolumeUnit );

		if(!found && measurementType!=null && measurementType.equals( VOLUME )) {
			found = true;

			//Logger.info( fileName," equatedWeightValue = "+ equatedWeightValue + " equationWeightUnit = "+equationWeightUnit);
			//Logger.info( fileName," equatedVolumeValue = "+ equatedVolumeValue + " equationVolumeUnit = "+equationVolumeUnit);

			// 3.	Convert the 'Equation Weight Unit' to 'Output Weight Unit'

			equatedWeightValue	=	getUnitConversion( equatedWeightValue, equationWeightUnit, outputWeightUnit );

			// 4.	Convert the 'Equation Volume Unit' to 'Input Volume Unit' Now both, the Weight and the Volume Units are the same for consecutive calculations.0

			equatedVolumeValue	=	getUnitConversion( equatedVolumeValue, equationVolumeUnit, inputVolumeUnit );

			//Logger.info( fileName," equatedWeightValue = "+ equatedWeightValue + " "+outputWeightUnit);
			//Logger.info( fileName," equatedVolumeValue = "+ equatedVolumeValue + " "+inputVolumeUnit);

			//		Hence, the equation units are now in terms of the input volume unit and the output weight unit.

			// 5.	Get the 'Conversion Factor' for 1 Weight Unit (viz. the output/equated weight unit)
			//		Divide The 'Equated Volume Value' by the 'Equated Weight Value'. The resulting value is the Conversion factor.
			//		Hence, the equation is.
			//		1 Weight Unit = Conversion Factor * 1 Volume Unit

			BigDecimal	convFactor	=	equatedVolumeValue.divide( equatedWeightValue, FoursoftWebConfig.INTERNAL_CALCULATION_PRECISION, BigDecimal.ROUND_HALF_EVEN );
			
			// 6.	Volumetric Weight in Weight Unit = Input Volume Value / Conversion Factor

			volumetricWeight		=	inputVolume.divide( convFactor, FoursoftWebConfig.INTERNAL_CALCULATION_PRECISION, BigDecimal.ROUND_HALF_EVEN );

			//Logger.info( fileName," equatedWeightValue = "+ equatedWeightValue );
			//Logger.info( fileName," equatedVolumeValue = "+ equatedVolumeValue );
			//Logger.info( fileName," Conv. Factor       = "+ convFactor );
			//Logger.info( fileName," inputVolume        = "+ inputVolume );
			//Logger.info( fileName," volumetricWeight = "+ volumetricWeight + " " + outputWeightUnit);

		}

		if(!found) {
			throw new InvalidUnitException("The unit '"+inputVolumeUnit+"' is not a valid Volume unit.");
		}

		// 7.	Return the Volumetric Weight.
		return volumetricWeight;
	}


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
												String		inputWeightUnit,
												String		outputVolumeUnit,
												BigDecimal	equatedVolumeValue,
												String		equationVolumeUnit,
												BigDecimal	equatedWeightValue,
												String		equationWeightUnit
											) throws InvalidUnitException
	{

		BigDecimal	equivalentVolume	= new BigDecimal("0.0");
		boolean		found				= false;

		// 1.	Check if the Units passed as parameters to the method are valid or not. If not throw an exception.
		// 2.	Check if the Volume's Volume Unit is valid or not. If not throw an exception.

		if(outputVolumeUnit==null || outputVolumeUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+outputVolumeUnit+"' is not a valid Volume unit.");
		}
		if(inputWeightUnit==null || inputWeightUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+inputWeightUnit+"' is not a valid Weight unit.");
		}
		if(equationVolumeUnit==null || equationVolumeUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+equationVolumeUnit+"' is not a valid Volume unit.");
		}
		if(equationWeightUnit==null || equationWeightUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+equationWeightUnit+"' is not a valid Weight unit.");
		}		

		// Get the type of measurement for the unit
		String	measurementType	=	getMeasurementTypeOfUnit( inputWeightUnit );

		if(!found && measurementType!=null && measurementType.equals( WEIGHT )) {
			found = true;

			//Logger.info( fileName," equatedWeightValue = "+ equatedWeightValue + " equationWeightUnit = "+equationWeightUnit);
			//Logger.info( fileName," equatedVolumeValue = "+ equatedVolumeValue + " equationVolumeUnit = "+equationVolumeUnit);

			// 3.	Convert the 'Equation Weight Unit' to 'Output Weight Unit'

			equatedVolumeValue	=	getUnitConversion( equatedVolumeValue, equationVolumeUnit, outputVolumeUnit );

			// 4.	Convert the 'Equation Volume Unit' to 'Input Volume Unit' Now both, the Weight and the Volume Units are the same for consecutive calculations.0

			equatedWeightValue	=	getUnitConversion( equatedWeightValue, equationWeightUnit, inputWeightUnit );

			//Logger.info( fileName," equatedWeightValue = "+ equatedWeightValue + " "+inputWeightUnit);
			//Logger.info( fileName," equatedVolumeValue = "+ equatedVolumeValue + " "+outputVolumeUnit );

			//		Hence, the equation units are now in terms of the input volume unit and the output weight unit.

			// 5.	Get the 'Conversion Factor' for 1 Weight Unit (viz. the output/equated weight unit)
			//		Divide The 'Equated Volume Value' by the 'Equated Weight Value'. The resulting value is the Conversion factor.
			//		Hence, the equation is.
			//		1 Volume Unit = Conversion Factor * 1 Weight Unit

			BigDecimal	convFactor	=	equatedWeightValue.divide( equatedVolumeValue, FoursoftWebConfig.INTERNAL_CALCULATION_PRECISION, BigDecimal.ROUND_HALF_EVEN );
			
			// 6.	Volumetric Weight in Weight Unit = Input Volume Value / Conversion Factor

			equivalentVolume		=	inputWeight.divide( convFactor, FoursoftWebConfig.INTERNAL_CALCULATION_PRECISION, BigDecimal.ROUND_HALF_EVEN );

			//Logger.info( fileName," equatedWeightValue = "+ equatedWeightValue );
			//Logger.info( fileName," equatedVolumeValue = "+ equatedVolumeValue );
			//Logger.info( fileName," Conv. Factor       = "+ convFactor );
			//Logger.info( fileName," inputVolume        = "+ inputVolume );
			//Logger.info( fileName," volumetricWeight = "+ volumetricWeight + " " + outputWeightUnit);

		}

		if(!found) {
			throw new InvalidUnitException("The unit '"+inputWeightUnit+"' is not a valid Weight unit.");
		}

		// 7.	Return the Volumetric Weight.
		return equivalentVolume;
	}

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

	public BigDecimal getInternalValue( double value, String inputUnit ) throws InvalidUnitException {
		return getInternalValue( ""+value, inputUnit );
	}

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
	public BigDecimal getInternalValue( String value, String inputUnit ) throws InvalidUnitException {

		String	label	=	"";
		boolean	found	=	false;

		if(inputUnit==null || inputUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+inputUnit+"' is not a valid unit.");
		}

		// The type of measurement for the unit
		String		measurementType	=	"";

		BigDecimal	convValue		=	null;
		BigDecimal	inputValue		=	new BigDecimal( value + "");
		BigDecimal	convFactor		=	null;

		try {

			measurementType	=	getMeasurementTypeOfUnit( inputUnit );

			// If LENGTH unit, process  for LENGTH labels
			if(measurementType!=null && measurementType.equals( LENGTH )) {
				convFactor	=	(BigDecimal) LENGTH_CONVERSIONS.get( inputUnit  );
				found = true;
			}
			// If VOLUME unit, process  for VOLUME labels
			if(measurementType!=null && measurementType.equals( VOLUME )) {
				convFactor	=	(BigDecimal) VOLUME_CONVERSIONS.get( inputUnit  );
				found = true;
			}
			// If WEIGHT unit, process  for WEIGHT labels
			if(measurementType!=null && measurementType.equals( WEIGHT )) {
				convFactor	=	(BigDecimal) WEIGHT_CONVERSIONS.get( inputUnit  );
				found = true;
			}

			if(found) {
				convValue = inputValue.multiply( convFactor );
				convValue = convValue.setScale( FoursoftWebConfig.INTERNAL_CALCULATION_PRECISION, internalValueRMode );
			} else {
				throw new InvalidUnitException("The unit '"+inputUnit+"' is not a valid unit.");
			}

		} catch (Exception e) {
			//Logger.error( fileName, "Exception while converting unit", e);
      logger.error( fileName+ "Exception while converting unit"+ e);
		}

		return convValue;
	}


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

	public BigDecimal getConvertedValue( double value, String outputUnit ) throws InvalidUnitException {

		return getConvertedValue( ""+value, outputUnit);
	}

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
	public BigDecimal getConvertedValue( String value, String outputUnit ) throws InvalidUnitException {

		String	label	=	"";
		boolean	found	=	false;

		if(outputUnit==null || outputUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+outputUnit+"' is not a valid unit.");
		}

		// The type of measurement for the unit
		String		measurementType	=	"";
		int			measurementDecimalPrecision	=	FoursoftWebConfig.DEFAULT_DECIMAL_PRECISION;

		BigDecimal	inputValue	=	new BigDecimal( value + "");
		BigDecimal	convValue	=	null;
		BigDecimal	convFactor	=	null;

		try {

			measurementType	=	getMeasurementTypeOfUnit( outputUnit );


			// If LENGTH unit, process  for LENGTH labels
			if(measurementType!=null && measurementType.equals( LENGTH )) {
				convFactor	=	(BigDecimal) LENGTH_CONVERSIONS.get( outputUnit );
				measurementDecimalPrecision	=	FoursoftWebConfig.LENGTH_DECIMAL_PRECISION;
				found = true;
			}
			// If VOLUME unit, process  for VOLUME labels
			if(measurementType!=null && measurementType.equals( VOLUME )) {
				convFactor	=	(BigDecimal) VOLUME_CONVERSIONS.get( outputUnit );
				measurementDecimalPrecision	=	FoursoftWebConfig.VOLUME_DECIMAL_PRECISION;
				found = true;
			}
			// If WEIGHT unit, process  for WEIGHT labels
			if(measurementType!=null && measurementType.equals( WEIGHT )) {
				convFactor	=	(BigDecimal) WEIGHT_CONVERSIONS.get( outputUnit );
				measurementDecimalPrecision	=	FoursoftWebConfig.WEIGHT_DECIMAL_PRECISION;
				found = true;
			}

			if(found) {

				convValue = inputValue.divide( convFactor, FoursoftWebConfig.INTERNAL_CALCULATION_PRECISION, convertedValueRMode );
				convValue = convValue.setScale( measurementDecimalPrecision, convertedValueRMode);

			} else {
				throw new InvalidUnitException("The unit '"+outputUnit+"' is not a valid unit.");
			}

		} catch (Exception e) {
			//Logger.error( fileName, "Exception while converting unit", e);
      logger.error( fileName+ "Exception while converting unit"+ e);
		}

		return convValue;
	}


	/**
	  * Gets the equivalent Weight Unit for the particular Standard System Length Unit
	  * First checks if the Length Unit is an M.K.S. or F.P.S. Unit.
	  *
	  * @param		String	Length Unit
	  * 
	  * @return		Equivalent Weight Unit
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined ones
	  */

	public String getWeightUnit(String lengthUnit) throws InvalidUnitException {
		String	equivalentWeightUnit	=	"";
		boolean	found					=	false;

		// Get the type of measurement for the unit
		String	measurementType	=	getMeasurementTypeOfUnit( lengthUnit );

		// If LENGTH unit, process  for LENGTH labels
		if(!found && measurementType!=null && measurementType.equals( LENGTH )) {
			if(lengthUnit.equals(CENTIMETER) || lengthUnit.equals(METER)) {
				equivalentWeightUnit	=	KILOGRAM;
				found = true;
			}
			if(lengthUnit.equals(INCH) || lengthUnit.equals( FEET )) {
				equivalentWeightUnit	=	POUND;
				found = true;
			}
		}
		if(!found) {
			throw new InvalidUnitException("The unit '"+lengthUnit+"' is not a valid Length unit.");
		}
		return equivalentWeightUnit;
	}

	/**
	  *	Gets the fully qualified label for a given Unit.
	  *
	  * @param		String	Unit
	  * 
	  * @return		Label of the Unit
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined ones
	  */

	public String getLabelOfUnit(String unit) throws InvalidUnitException
	{
		String	label	=	"";
		boolean	found	=	false;

		if(unit==null || unit.equals("")) {
			throw new InvalidUnitException("The unit '"+unit+"' is not a valid unit.");
		}

		// Get the type of measurement for the unit
		String	measurementType	=	getMeasurementTypeOfUnit( unit );

		// If LENGTH unit, process  for LENGTH labels
		if(!found && measurementType!=null && measurementType.equals( LENGTH )) {

			for(int i=0; i < LENGTH_UNIT_LABELS.length; i++) {
				if(unit.equals( SUPPORTED_LENGTH_UNITS[i] )) {
					label	=	LENGTH_UNIT_LABELS[i];
					found	=	true;
					break;
				}
			}
		}

		// If VOLUME unit, process  for VOLUME labels
		if(!found && measurementType!=null && measurementType.equals( VOLUME )) {

			for(int i=0; i < VOLUME_UNIT_LABELS.length; i++) {
				if(unit.equals( SUPPORTED_VOLUME_UNITS[i] )) {
					label	=	VOLUME_UNIT_LABELS[i];
					found	=	true;
					break;
				}
			}
		}

		// If WEIGHT unit, process  for WEIGHT labels
		if(!found && measurementType!=null && measurementType.equals( WEIGHT )) {

			for(int i=0; i < WEIGHT_UNIT_LABELS.length; i++) {
				if(unit.equals( SUPPORTED_WEIGHT_UNITS[i] )) {
					label	=	WEIGHT_UNIT_LABELS[i];
					found	=	true;
					break;
				}
			}
		}

		if(!found) {
			throw new InvalidUnitException("The unit '"+unit+"' is not a valid unit.");
		}
		return label;
	}


	/**
	  *	Gets the SI Notation / Symbol for a given Unit.
	  *
	  * @param		String	Unit
	  * @return		Symbol (Systeme International i.e. SI Notation) of the Unit
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined (constants) ones
	  */

	public String getSymbolOfUnit(String unit) throws InvalidUnitException
	{
		String	symbol	=	"";
		boolean	found	=	false;

		if(unit==null || unit.equals("")) {
			throw new InvalidUnitException("The unit '"+unit+"' is not a valid unit.");
		}

		// Get the type of measurement for the unit
		String	measurementType	=	getMeasurementTypeOfUnit( unit );

		// If LENGTH unit, process  for LENGTH labels
		if(!found && measurementType!=null && measurementType.equals( LENGTH )) {

			for(int i=0; i < LENGTH_UNIT_LABELS.length; i++) {
				if(unit.equals( SUPPORTED_LENGTH_UNITS[i] )) {
					symbol	=	LENGTH_UNIT_SYMBOLS[i];
					found	=	true;
					break;
				}
			}
		}

		// If VOLUME unit, process  for VOLUME labels
		if(!found && measurementType!=null && measurementType.equals( VOLUME )) {

			for(int i=0; i < VOLUME_UNIT_LABELS.length; i++) {
				if(unit.equals( SUPPORTED_VOLUME_UNITS[i] )) {
					symbol	=	VOLUME_UNIT_SYMBOLS[i];
					found	=	true;
					break;
				}
			}
		}

		// If WEIGHT unit, process  for WEIGHT labels
		if(!found && measurementType!=null && measurementType.equals( WEIGHT )) {

			for(int i=0; i < WEIGHT_UNIT_LABELS.length; i++) {
				if(unit.equals( SUPPORTED_WEIGHT_UNITS[i] )) {
					symbol	=	WEIGHT_UNIT_SYMBOLS[i];
					found	=	true;
					break;
				}
			}
		}

		if(!found) {
			throw new InvalidUnitException("The unit '"+unit+"' is not a valid unit.");
		}
		return symbol;
	}


	/**
	  *	Finds the Measurement Type (LENGTH, VOLUME, WEIGHT) for a given unit
	  *
	  * @param		String	Unit
	  * @return		Measurement Type of the Unit (LENGTH / VOLUME / WEIGHT)
	  *
	  * @throws		InvalidUnitException if the unit passed as a parameter to this method is not among the pre-defined (constants) ones
	  */

	private String getMeasurementTypeOfUnit(String unit) throws InvalidUnitException 
	{

		if(unit==null || unit.equals("")) {
			throw new InvalidUnitException("The unit '"+unit+"' is not a valid unit.");
		}

		boolean	found			=	false;
		String	measurementType	=	"";

		// Check in LENGTH Units if not yet found
		if(!found) {
			for(int i=0; i < SUPPORTED_LENGTH_UNITS.length; i++) {
				if(SUPPORTED_LENGTH_UNITS[i].equals(unit)) {
					measurementType	=	LENGTH;
					found	=	true;
					break;
				}
			}
		}

		// Check in VOLUME Units if not yet found
		if(!found) {
			for(int i=0; i < SUPPORTED_VOLUME_UNITS.length; i++) {
				if(SUPPORTED_VOLUME_UNITS[i].equals(unit)) {
					measurementType	=	VOLUME;
					found	=	true;
					break;
				}
			}
		}

		// Check in WEIGHT Units if not yet found
		if(!found) {
			for(int i=0; i < SUPPORTED_WEIGHT_UNITS.length; i++) {
				if(SUPPORTED_WEIGHT_UNITS[i].equals(unit)) {
					measurementType	=	WEIGHT;
					found	=	true;
					break;
				}
			}
		}

		if(!found) {
			throw new InvalidUnitException("The unit '"+unit+"' is not a valid unit.");
		}
		return  measurementType;
	}


	/**
	  *	Finds the equivalent Volume nit of s Given Length Unit
	  * e.g. CUBIC_CENTIMETER for CENTIMETER or CUBIC_INCH for INCH and so on. 
	  *
	  * @param		String	Length Unit
	  * @return		Equivalent Volume Unit
	  *
	  * @throws		InvalidUnitException if the length unit passed as a parameter to this method is not among the pre-defined (constants) ones
	  */
	public String getVolumeUnit(String lengthUnit) throws InvalidUnitException {
		String	equivalentVolumeUnit	=	"";
		boolean	found					=	false;

		if(lengthUnit==null || lengthUnit.equals("")) {
			throw new InvalidUnitException("The unit '"+lengthUnit+"' is not a valid Length unit.");
		}

		// Get the type of measurement for the unit
		String	measurementType	=	getMeasurementTypeOfUnit( lengthUnit );

		// If LENGTH unit, process  for LENGTH labels
		if(!found && measurementType!=null && measurementType.equals( LENGTH )) {
			if(lengthUnit.equals(CENTIMETER)) {
				equivalentVolumeUnit	=	CUBIC_CENTIMETER;
				found = true;
			}
			if(lengthUnit.equals(METER)) {
				equivalentVolumeUnit	=	CUBIC_METER;
				found = true;
			}
			if(lengthUnit.equals(INCH)) {
				equivalentVolumeUnit	=	CUBIC_INCH;
				found = true;
			}
			if(lengthUnit.equals(FEET)) {
				equivalentVolumeUnit	=	CUBIC_FEET;
				found = true;
			}
		}
		if(!found) {
			throw new InvalidUnitException("The unit '"+lengthUnit+"' is not a valid Length unit.");
		}
		return equivalentVolumeUnit;
	}

	/**
	  *	Converts the Rounding Mode in Integer form to an equivalent String Description
	  *
	  * @param		int		Rounding Mode
	  * @return		Rounding Mode Description
	  *
	  */

	public static String getRoundingModeLabel( int mode ) {

		String	label	=	"";

		if(mode==BigDecimal.ROUND_UP) {
			label = "ROUND_UP";
		}
		if(mode==BigDecimal.ROUND_DOWN) {
			label = "ROUND_DOWN";
		}
		if(mode==BigDecimal.ROUND_HALF_UP) {
			label = "ROUND_HALF_UP";
		}
		if(mode==BigDecimal.ROUND_HALF_DOWN) {
			label = "ROUND_HALF_DOWN";
		}
		if(mode==BigDecimal.ROUND_HALF_EVEN) {
			label = "ROUND_HALF_EVEN";
		}
		if(mode==BigDecimal.ROUND_CEILING) {
			label = "ROUND_CEILING";
		}
		if(mode==BigDecimal.ROUND_FLOOR) {
			label = "ROUND_FLOOR";
		}
		if(mode==BigDecimal.ROUND_UNNECESSARY) {
			label = "ROUND_UNNECESSARY";
		}

		return label;
	}


}
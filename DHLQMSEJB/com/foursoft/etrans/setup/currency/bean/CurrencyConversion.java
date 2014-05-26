/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/**
	Program Name	:CurrencyConversion.java
	Module Name		:ETrans
	Task			:CurrencyConversion Master
	Sub Task		:CurrencyConversionJavaObject
	Author Name		:Subba Reddy V
	Date Started	:September 12,2001
	Date Completed	:September 13,2001
	Date Modified	:September 12,2001 by Subba Reddy V
	Description		:This file main purpose is to get and set all the datafields in the database.
*/

/*
 * @(#)CurrencyConversion.java         10/01/2001
 *
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd, 
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft. For more information on the Four Soft 
 */


package com.foursoft.etrans.setup.currency.bean;

import java.text.NumberFormat;

/**
 * This class will be useful to manipulate all the details of Currency Conversion . 
 * This class consist of accessors and mutators for these objects. 
 *<p>
 * This object is used in add, modify and view modules of the Currency Conversion. In modify, view modules the currency 
 * conversion  data will be fetched( with respect to FS_FR_CURRENCYMASTER table in the database ) and the fetched data
 * will be stored in  this object,and this object will be returned back to client.In add module, entered currency conversion 
 * data by client will send to the server through this object.
 *
 * @version		1.00, 10 01 2001
 * @author 		K.RAGHAVENDRA
 */




public class CurrencyConversion implements java.io.Serializable
{
	/** Creates a new CurrencyConversion. It's a default Constructor, which sets the class variables to null. */	
	
	public CurrencyConversion()
	{
		currency1			=	null;
		currency2			=	null;
		conversionFactor	=	null;
	}
	
	/**
* sets Currency1, Currency1 always sets the base Currency i.e HCurrency of CompanyInfo of login Terminal Id.
*
* @param Currency1 the String array used to set Currency1
*/ 

	public void  setCurrency1( String[] currency1 )
	{
		this.currency1 = currency1;
	}

/**
* Returns the currency1,Currency1 always takes the base Currency i.e HCurrency of CompanyInfo of login Terminal Id.
*
* @returns currency1 as String array 
*/
	public String[] getCurrency1()
	{
		return currency1;
	}

/**
* sets Currency2 , This currency is selected from LOV provided which consists of Currency Ids taken from CountryMaster Table.
*
* @param Currency2 the String array used to set Currency2
*/
	
	public void setCurrency2( String[] currency2 )
	{
		this.currency2 = currency2;	
	}

/*
Returns the currency2,This currency is selected from LOV provided which consists of Currency Ids taken from CountryMaster Table
@returns currency2 as String array 
*/

	public String[] getCurrency2()
	{
		return  currency2;	
	}

/*
sets ConversionFactor , this is always related with base currency1 i.e Currency1 and Currency2 (selected from LOV) , it is defined as  ConversionFactor  = Currency1 / Currency2 .
@param ConversionFactor the double array used to set conversionFactor
*/

	public void setConversionFactor( double[] conversionFactor )
	{
		this.conversionFactor = conversionFactor;
	}

/**
* Returns the conversionFactor, which is ratio of Currency1 and Currency2 i.e conversionFactor = Currency1 / Currency2 *
* @returns conversionFactor as double array .
*/

	public double[] getConversionFactor()
	{
		return conversionFactor;
	}
/*
Gets the conversionFactor for a given two currencies , Currency1 indicates the HCurrency of the CompanyInfo related 
to the  login terminalId.Currency2 indicates the user entered currency through the GUI. There exist three cases
for this method .
Case1:         1.Currency1  and Currency2  exists
Ex  Say Currency1=INR  and Currency2=SGD  whose Conversionfactor= 0.04 exits,
then this methods gets the value as 0.04 for above two currencies.
Case2:   Say  2.Currency1=SGD  and Currency2= INR  ,in this case this method gets the conversionFactor=25.0 it uses
the formula CURRENCY1= CURRENCY2 * CONVERSIONFACTOR.
Case3:   Say  3.Currency1=INR   and  Currency2 = SGD  Whose ConversionFactor = 0.04
Currency1=INR   and  Currency2 = UGD  Whose ConversionFactor = 0.0222..
If User requires the ConversionFactor  for Currency1=SGD and Currency2=USD , than this method uses
following formula to get conversion Factor  
convFactor = secondConversionFactor / firstConversionFactor
In above example   conversionFactor for above two currencies is 0.55
@returns the double  contains ConversionFactor        
*/

  /**
   * 
   * @param strCurrency1
   * @param strCurrency2
   * @return double
   */
	public double getConversionFactor( String strCurrency1, String strCurrency2 )
	{
		int len = size();
		if(strCurrency1.equalsIgnoreCase(strCurrency2))
    {
      return 1.0;	
    }
		for( int row=0; row<len; row++ )
		{
		if(currency1[row].equals(strCurrency1) )
		{
		if( currency2[row].equals(strCurrency2) )
		{
			return conversionFactor[ row ];
		}
		}
		}
		for( int row=0; row<len; row++ )
		{
		if( currency1[row].equals(strCurrency2) )
		{
		if( currency2[row].equals(strCurrency1) )
		{
			double convFactor = 1/conversionFactor[ row ];		
			NumberFormat mystring = NumberFormat.getInstance();
			mystring.setMaximumFractionDigits(2);
			String myString = mystring.format(convFactor);
			convFactor = Double.parseDouble(myString);	
			return convFactor;
		}
		}
		}
		double fConversionFactor = 0;
		double sConversionFactor = 0;
		double convFactor  = 0;
		int flag = 0;
		String temp="";
		String temp1="";
		for( int row1=0; row1<len; row1++ )
		{
		flag = 0;
		for( int row=0; row<len; row++ )
		{
		if(currency1[row1].equals(currency1[row]))
		{
		if( currency2[row].equals(strCurrency1) )
		{
			temp=currency2[row];
			fConversionFactor = conversionFactor[ row ];
			flag++;
		}
		if( currency2[row].equals(strCurrency2) )
		{
			temp1=currency2[row];
			sConversionFactor = conversionFactor[ row ];
			flag++;
		}
		}
		}
		if( flag==2 )
		{
		break;
		}
		}
		if(flag == 2 && sConversionFactor > 0 && fConversionFactor > 0)
		{
			convFactor = sConversionFactor / fConversionFactor;		
		}
		else
		{
			sConversionFactor  = 0.0;
			fConversionFactor  = 1.0;
			convFactor = sConversionFactor / fConversionFactor;		
		}
			NumberFormat mystring = NumberFormat.getInstance();
			mystring.setMaximumFractionDigits(2);
			String myString = mystring.format(convFactor);
			convFactor = Double.parseDouble(myString);
			return convFactor;
	//return 0;
	}

/**
* Returns the Length of  Currency1  in the database of the table FS_FR_CURRENCYMASTER.
*
* @returns the length of Currency1
*/

	private int size()
		{
			if( currency1!=null && currency2!=null && conversionFactor!=null )
      {
        return currency1.length;
      }
			return 0;
		}
	
/** This string array  currency1  uses to store currency1 */			
private String[]  currency1            = null;
/** This string array  currency2  uses to store currency1 */			
private String[]  currency2            = null;
/** This double array  conversionFactor  uses to store conversionFactor */			
private double[]  conversionFactor     = null;
}
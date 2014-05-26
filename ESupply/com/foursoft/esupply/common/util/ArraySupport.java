
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
 * File			: ArraySupport.java
 * sub-module	: common
 * module		: esupply
 *
 *
 * This is an Utility Class.
 * This class contains static methods to working with
 * arrays. It is used to serch a String in String Arry
 * 
 *
 * @author	Madhusudhan Rao. P, 
 * @date	28-08-2001
 */
import com.foursoft.esupply.common.exception.InvalidDataException; 
 
public class ArraySupport
{
	
	/**
	 * Returns true if the specified value matches one of the elements
	 * in the specified array.
	 *
	 * @param array		String Array
	 * @param value		String you are going search
	 *
	 * @return boolen   returns true if the passed String is in the String Arry
	 *					it will return false, either specified String is not in the String Array or
	 *					if String Array itself is null
	 */
	public static boolean contains( String[] array, String value )
	{
		boolean isIncluded = false; //it holds the boolen value such that weather String is in Array or not
		int arrayLen	= array.length;//Added by subrahmanyam for the loopPerformance
		if( array == null || value == null )
		{
			return false;
		}
		for( int i = 0;i < arrayLen;i++ )
		{
			if( value.equals( array[i] ) )
			{
				isIncluded = true;
				break;
			}
		}
		return isIncluded;
	}
	
	public static String[] getBinaryNumbers(int n)
	{
		int[] tempArr = new int[10];
		int i=0;
		int rem = 0;
		while(n > 0)
		{
			rem = n % 2;
			tempArr[i]	= rem;
			i++;
			n/= 2;
		}
		int size = i;
		int multiplier = 1;
		String[] binNumbers = new String[size];
		int k=0;
		for(int j =0; j < size; j++)
		{
//			System.out.println("Vale : and ABS Value "+tempArr[j]+" -- "+multiplier*tempArr[j] );
			if(tempArr[j] != 0)
			{
				binNumbers[k] = "" + multiplier*tempArr[j];
				k++;
			}
			multiplier *= 2;
		}
//		System.out.println("Size is : "+k);
		String[] finalNumbers = new String[k];
		for(int m =0; m < k; m++)
		{
			finalNumbers[m] = binNumbers[m];
		}
		
		return finalNumbers;
	}
	
	public String getBinaryNumber(int n, int base) throws InvalidDataException
	{
		String returnString = "";
		if (base <= 0 || !(new Integer(base) instanceof Integer))
		{
			while(n > 0)
			{
				returnString = (n % base)+returnString;
				n/= base;
			}
		}
		else
		{
			throw new InvalidDataException("Invalid base Number");
		}		
		return returnString;
	}
	
}

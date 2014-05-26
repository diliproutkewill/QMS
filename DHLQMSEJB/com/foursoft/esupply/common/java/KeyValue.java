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
 * File			: KeyValue.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This reprersents Key Value pair
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	30-10-2001
 */
 
public class KeyValue 
{
	private String name;	// name of the parameter
	private String value;	// value of the parameter

	/**
	* Constuructor which takes name and value as arguments
	* @param name
	* @param value
	*/
	public KeyValue(String name, String value) 
	{
		this.name = name;
		this.value = value;
	}
	
	/**
	* getter method for name of the parameter
	*/
	public String getName() {
		return name;
	}

	/**
	* getter method for value of the parameter
	*/
	public String getValue() {
		return value;
	}
	
	/**
	* to string implementation
	*/
	public String toString()
	{
		return name+"$"+value;
	}
}

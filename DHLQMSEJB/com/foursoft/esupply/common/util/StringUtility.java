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

import java.util.*;
import java.io.*;

/**
 * File			: StringUtility.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is staic utility Class which takes String and Returns empty String if it is null
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	30-10-2001
 */
public class StringUtility 
{
	public static String noNull(String s)
	{
		if(s == null)
			return "";
		else 
			return s;
	}

	/**
	 * This method performs the wrapping of the text. This will be used to disply the content of any text displayed in HTML view.
	 * @param mainStr - contented to be wrapped
	 * @param alineSize - alignment size
	 * @author	Madhusudhana Rao. V, 
	 */
	public static String wrap(String mainStr, int alineSize)
	{
		if (mainStr.length() < alineSize)
			return mainStr;
      
		StringTokenizer st = new StringTokenizer(mainStr);
		String token = null;
    
		boolean first = true;
		while(st.hasMoreElements())
		{
			token = st.nextToken();
			if (first)
			{
				tempBuff = new StringBuffer(); 
				first = false;
		        if (token.length() > alineSize)
					makeDivide(token,alineSize);
				else
					tempBuff.append(token);
			}
			else
			{  
				if (token.length() > alineSize)
					makeDivide(token,alineSize);    
				else
				{
					if (tempBuff.length() < alineSize)
					{
						if ((tempBuff.length()+token.length()) < alineSize )
							tempBuff.append(" "+token);
						else if ((tempBuff.length()+token.length()) == alineSize-1 )
							tempBuff.append(" "+token);
						else
						{
							returnStr.append(tempBuff+"<br>");
							tempBuff = new StringBuffer(token);
						}  
					}
					else
					{
						returnStr.append(tempBuff+"<br>");
						tempBuff = new StringBuffer(token);
					}
				}
			}
		}
		returnStr.append(tempBuff);
		return returnStr.toString();
	}
	
	/**
	 *	This method is called inside from wrap(String mainStr, int alineSize)
	 */
	private static void makeDivide(String str, int alineSize) 
	{
		int counter = 0;
		while (counter+alineSize < str.length())
		{
			returnStr.append(str.substring(counter,counter+alineSize));
			returnStr.append("<br>");
			counter+=alineSize;
		}
		tempBuff = new StringBuffer(str.substring(counter));
	}

	private static StringBuffer tempBuff = null;
	private static StringBuffer returnStr = new StringBuffer();
}
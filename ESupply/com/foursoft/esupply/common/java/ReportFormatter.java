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

import java.util.ArrayList;

/**
 * File			: ReportFormatter.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This class is used to implement the Page-by-Page Itearator Pattern 
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	13-03-2002
 */

public class ReportFormatter implements java.io.Serializable
{
	public ReportFormatter()
	{
	}
	public ReportFormatter(ArrayList data, int noOfRecordsPerPage, int noOfSegments)
	{
		this.data				= data;
		this.noOfRecordsPerPage	= noOfRecordsPerPage;
		this.noOfSegments		= noOfSegments;
		this.segmentStartNo		= 1;
		this.segmentEndNo		= noOfSegments;
	}

	public Object[] getCurrentPageData(int currentIndex)
	{
		ArrayList tempList = new ArrayList(noOfRecordsPerPage);
		int startNo	= noOfRecordsPerPage*(currentIndex-1);

		for(int i = startNo; i < (startNo + noOfRecordsPerPage); i++)
		{
			if(i > data.size()-1)
				break;			
			tempList.add(data.get(i) );
		}
		return tempList.toArray();
	}
	public int getNoOfPages()
	{
		int recCount = data.size()/noOfRecordsPerPage;
		if (data.size()%noOfRecordsPerPage == 0)
			return  recCount;
		else 
			return  recCount + 1;
	}		
	public int currentPageIndex;
	public int noOfRecordsPerPage	= 6;
	public int noOfSegments			= 10;
	public int segmentStartNo;
	public int segmentEndNo;
	
	public ArrayList data;
	
}

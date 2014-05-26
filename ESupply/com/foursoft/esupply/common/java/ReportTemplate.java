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

import java.util.*;
import java.io.*;
import java.sql.Timestamp;

/**
 * @author  madhu
 */
public class ReportTemplate 
{
	public Timestamp 	date;
	public String		title;
	public int 			noOfRecords;
	public ArrayList 	reportData;
	public ArrayList	lables;
	public int			currentPageNo;
	public int			noOfRecordsPerPage;
	
	public ArrayList currentPageReport()
	{
		ArrayList tempList = new ArrayList(noOfRecordsPerPage);
		int startNo	= noOfRecordsPerPage*(currentPageNo-1);
		//System.out.println("Start No in currentPageReport() : "+startNo);
		int repDataSize	=	reportData.size();
		for(int i = startNo; i < (startNo + noOfRecordsPerPage) && startNo < repDataSize ; i++)
		{
			tempList.add(reportData.get(i) );
		}
		//System.out.println("Temp List : "+tempList);
		return tempList;
	}
	public ArrayList getListLabels()
	{
		return lables;
	}
	public int getNoOfPages()
	{
		int recCount = reportData.size()/noOfRecordsPerPage;
		//System.out.println("REC Count : "+recCount);
		return  recCount + 1;
	}

}
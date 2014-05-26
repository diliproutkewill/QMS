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
 * @author  :
 * @date     :
 * @version :
 */

public class LOVListHandler implements java.io.Serializable
{
  /**
   * constructor
   */
	public LOVListHandler()
	{

	}

  /**
   * 
   * @param data
   * @param noOfRecordsPerPage
   */
	public LOVListHandler(ArrayList data, int noOfRecordsPerPage)
	{
		this.data				= data;
		this.noOfRecordsPerPage	= noOfRecordsPerPage;
	}

  /**
   * 
   * @param data
   * @param noOfRecordsPerPage
   * @param requiredAttributes
   */
    public LOVListHandler(ArrayList data, int noOfRecordsPerPage,ArrayList requiredAttributes)
	{
		this.data				= data;
		this.noOfRecordsPerPage	= noOfRecordsPerPage;
        this.requiredAttributes = requiredAttributes;
	}

  /**
   * 
   * @return CurrentPageData
   */
	public ArrayList getCurrentPageData()
	{
		ArrayList tempList = new ArrayList(noOfRecordsPerPage);
		int startNo	       = noOfRecordsPerPage*(currentPageIndex-1);

		for(int i = startNo; i < (startNo + noOfRecordsPerPage); i++)
		{
			if(i > data.size()-1)
				break;			
			tempList.add(data.get(i) );
		}

		return tempList;
	}
    
  /**
   * 
   * @return NoOfPages
   */
	public int getNoOfPages()
	{
		int recCount = data.size()/noOfRecordsPerPage;
		if (data.size()%noOfRecordsPerPage == 0)
			return  recCount;
		else 
			return  recCount + 1;
	}		
    

	public ArrayList data                  =  null;
    public ArrayList requiredAttributes    =  null;  
   	public int       currentPageIndex      =  0;
	public int       noOfRecordsPerPage    =  0;
	
}

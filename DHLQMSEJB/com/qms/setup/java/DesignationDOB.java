/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
/*	Programme Name : DesignationDOB.Java.
*	Module Name    : DHL-QMS.
*	Task Name      : Designation
*	Sub Task Name  : 
*	Author		   : K.NareshKumar Reddy
*	Date Started   :6/17/2005
*	Date Ended     :
*	Modified Date  :
*	Description    :
*	Methods		   : Methods used in this class alongwith their return types,arguments and accessors are given below.
*
*  1.    public void setDesignationId( String designationId )//For setting up designationId 
*  2.    public  String  getDesignationId()   //To get the designationId and whose return type will be String
*  3.    public void setdescription( String description )//To set the description  
*  4.	 public String getDescription()//To get the description which will return String 
*  5.    public void setLevelNo( String levelNo )//To set levelNo.
*  6. 	 public String getLevelNo()//To get the levelNo whose return type will be String. 			        	 
*  7.	 public void setInvalidate( Char region )// To set Invalidate
*  8.    public Char getInvalidate()//To get Invalidate value it returs Char.
*/
package com.qms.setup.java;


public class  DesignationDOB implements java.io.Serializable
{
    private String designationId;
		private String description;
		private String levelNo;
		private char invalidate1;
    private String invalidate;
    public DesignationDOB()
    {
		
    }
	public void  setDesignationId(String designationId)
	{
		this.designationId=designationId;
	}
  public  void  setDescription(String description)
  {
    this.description=description;
  }
  public  void  setLevelNo(String levelNo)
  {
    this.levelNo=levelNo;
  }
  public   void setInvalidate(char invalidate1)
  {
    this.invalidate1=invalidate1;
  }
  public void setInvalidate(String invalidate)
  {
    this.invalidate=invalidate;
  }
  public  String getDesignationId()
  {
    return designationId;
  }
  public  String getDescription()
  {
    return description;
  }
  public String getInvalidate()
  {
    return invalidate;
  }
  public  String getLevelNo()
  {
    return levelNo;
  }
  public char getInvalidate1()
  {
    return invalidate1;
  }
}

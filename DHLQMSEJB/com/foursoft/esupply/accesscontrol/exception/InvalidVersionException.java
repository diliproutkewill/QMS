package com.foursoft.esupply.accesscontrol.exception;

import java.sql.Timestamp;
import com.foursoft.esupply.common.exception.FoursoftApplicationException;

/**
 * File			: InvalidVersionException.java
 * sub-module 	: accesscontrol
 * module 		: esupply
 * 
 * This class is added for displaying the throwable errors on browser when stale data
 * trying to update into database.
 * 	   
 * @Author by Madhu V
 * @date   18-06-2003
 */
 
public class InvalidVersionException extends FoursoftApplicationException 
{
  
  public static String ERRORCODE = "AE-30015";
  /**
   * Default constructor
   */
  public InvalidVersionException()
  {
  }

  /**
   * This calls the super constructor
   */
  public InvalidVersionException(String message)
  {
    super(message);
  }

  /**
   * Create exception by calling super of this
   */
/*
  public InvalidVersionException(String message, Exception e)
	{
		super(message, e);
	}  
*/

  /**
   * Create exception by calling super
   */
/*
  public InvalidVersionException(Exception e,String fileName,String methodName)
	{
    super(e,fileName,methodName);
  }
*/

  /**
   * This constructor is the most suitable exception to be used to have new error 
   * codes applicable. When any stale data problems comes then better to throw 
   * this constructor exception to added all features.
   */
/*
  public InvalidVersionException(Exception e,String fileName,String methodName, String modifiedBy, Timestamp modifiedTime)
	{
    super(e,fileName,methodName);
    this.modifiedBy = modifiedBy;
    this.modifiedTime = modifiedTime;
  }
*/

  /**
   * This constructor used when user wants to propagate last modified user with timestamp.
   */
  public InvalidVersionException(String modifiedBy, Timestamp modifiedTime)
	{
    this.modifiedBy = modifiedBy;
    this.modifiedTime = modifiedTime;
  }

  public Timestamp getModifiedTime()
  {
    return modifiedTime;
  }

  public void setModifiedTime(Timestamp newModifiedTime)
  {
    modifiedTime = newModifiedTime;
  }

  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public void setModifiedBy(String newModifiedBy)
  {
    modifiedBy = newModifiedBy;
  }

  private Timestamp modifiedTime = null;
  private String modifiedBy = null;
}
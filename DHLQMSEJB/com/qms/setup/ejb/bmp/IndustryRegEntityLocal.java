package com.qms.setup.ejb.bmp;
import javax.ejb.EJBLocalObject;

import com.qms.setup.java.IndustryRegDOB;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : IndustryRegEntityLocal.java
 * @author 	: I.V.Sekhar Merrinti
 *  
 * @version : QMS
 * @date 	  : 2005-06-17
 * 
 */
public interface IndustryRegEntityLocal extends EJBLocalObject 
{
  /**
   * 
   *@param  industryRegDOB
   * 
   */
  public void setIndustryRegDOB(IndustryRegDOB industryRegDOB);
   /**
   * 
   * @return IndustryRegDOB
   * 
   */
  public IndustryRegDOB getIndustryRegDOB();
   /**
   * 
   * @return industryList
   * 
   */  
  /*public void invalidateIndustryDtls(java.util.ArrayList industryList)
  throws javax.ejb.EJBException;*/
  
}
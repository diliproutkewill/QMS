package com.qms.setup.ejb.bmp;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;


/**
 * 
 * This class will be useful to .
 * 
 * File		  : IndustryRegEntityLocalHome.java
 * @version	: QMS
 * @author	: I.V.Sekhar Merrinti
 * 
 * @date	  : 2005-06-17
 *
 */
public interface IndustryRegEntityLocalHome extends EJBLocalHome 
{

  /**
   * 
   * @param param0
   * @return IndustryRegEntityLocal
   * @throws javax.ejb.CreateException
   */
  com.qms.setup.ejb.bmp.IndustryRegEntityLocal create(com.qms.setup.java.IndustryRegDOB param0) throws CreateException;
  /**
   * 
   * @param primaryKey
   * @return IndustryRegEntityLocal
   * @throws javax.ejb.FinderException
   */
  com.qms.setup.ejb.bmp.IndustryRegEntityLocal findByPrimaryKey(com.qms.setup.ejb.bmp.IndustryRegEntityPK primaryKey) throws FinderException;
}
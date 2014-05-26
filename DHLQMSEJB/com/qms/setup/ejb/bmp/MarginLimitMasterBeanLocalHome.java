package com.qms.setup.ejb.bmp;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface MarginLimitMasterBeanLocalHome extends EJBLocalHome 
{

  /**
   * 
   * @throws javax.ejb.CreateException
   * @return 
   * @param param0
   */
  MarginLimitMasterBeanLocal create(java.util.ArrayList param0) throws CreateException;
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.FinderException
   * @return 
   * @param primaryKey
   */
  MarginLimitMasterBeanLocal findByPrimaryKey(MarginLimitMasterBeanPK primaryKey) throws FinderException,javax.ejb.ObjectNotFoundException;
}
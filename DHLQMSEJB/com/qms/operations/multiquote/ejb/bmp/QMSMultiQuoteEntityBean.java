
/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 *
 

 * File					: QMSMultiQuoteEntityBean.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */




package com.qms.operations.multiquote.ejb.bmp;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.operations.multiquote.dao.QMSMultiQuoteDAO;
import com.qms.operations.multiquote.dob.MultiQuoteFinalDOB;
import com.qms.operations.multiquote.dob.MultiQuoteMasterDOB;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.ObjectNotFoundException;

public class QMSMultiQuoteEntityBean implements EntityBean 
{
  private EntityContext     context;
  private QMSMultiQuoteDAO       quoteDAO  = null;
  private QMSMultiQuoteEntityPK  pkObj     = null;
  private static final String FILE_NAME		= "QMSMultiQuoteEntityBean.java";
  private static Logger logger = null;

  public QMSMultiQuoteEntityBean()
  {
    logger  = Logger.getLogger(QMSMultiQuoteEntityBean.class);
  }
  public QMSMultiQuoteEntityPK ejbCreate(MultiQuoteFinalDOB finalDOB)
  {
    MultiQuoteMasterDOB  masterDOB = null;
    try
    {
      quoteDAO  = new QMSMultiQuoteDAO();
      quoteDAO.create(finalDOB);
      pkObj = new QMSMultiQuoteEntityPK();
      masterDOB = (MultiQuoteMasterDOB)finalDOB.getMasterDOB();
      pkObj.quoteId = masterDOB.getUniqueId();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Finally : QMSMultiQuoteEntityBean[ejbCreate(masterDOB)]-> "+e.toString());
      logger.error(FILE_NAME+"Finally : QMSMultiQuoteEntityBean[ejbCreate(masterDOB)]-> "+e.toString());
      throw new EJBException(e.toString());
    }
    return pkObj;
  }

    
  public void ejbPostCreate(MultiQuoteFinalDOB finalDOB)
  {
  }

  public QMSMultiQuoteEntityPK ejbFindByPrimaryKey(QMSMultiQuoteEntityPK primaryKey)
  {
    try
    {
      quoteDAO  = new QMSMultiQuoteDAO();
      quoteDAO.findByPrimaryKey(primaryKey.quoteId);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Finally : QMSMultiQuoteEntityBean[ejbFindByPrimaryKey(primaryKey)]-> "+e.toString());
      logger.error(FILE_NAME+"Finally : QMSMultiQuoteEntityBean[ejbFindByPrimaryKey(primaryKey)]-> "+e.toString());
      throw new EJBException(e.toString());
    }
    return primaryKey;
  }

  public void ejbActivate()
  {
  }

  public void ejbLoad()
  {
  }

  public void ejbPassivate()
  {
  }

  public void ejbRemove()
  {
    quoteDAO=null;
    pkObj=null;
  }

  public void ejbStore()
  {
  }

  public void setEntityContext(EntityContext ctx)
  {
    this.context = ctx;
  }

  public void unsetEntityContext()
  {
    this.context = null;
  }
}
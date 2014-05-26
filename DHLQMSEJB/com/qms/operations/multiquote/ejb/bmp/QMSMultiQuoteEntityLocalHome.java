
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
 

 * File					: QMSMultiQuoteEntityLocalHome.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */



package com.qms.operations.multiquote.ejb.bmp;
import com.qms.operations.multiquote.dob.MultiQuoteFinalDOB;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface QMSMultiQuoteEntityLocalHome extends EJBLocalHome 
{
  QMSMultiQuoteEntityLocal create(MultiQuoteFinalDOB finalDOB) throws CreateException;

  QMSMultiQuoteEntityLocal findByPrimaryKey(QMSMultiQuoteEntityPK primaryKey) throws FinderException;
}
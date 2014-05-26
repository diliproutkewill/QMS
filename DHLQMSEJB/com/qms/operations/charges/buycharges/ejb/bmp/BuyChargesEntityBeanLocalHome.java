/**
 * @ (#) BuyChargesEntityBeanLocalHome.java
 * Copyright (c) 2001 The Four Soft Pvt Ltd., 
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */

/**
 * File : BuyChargesEntityBeanLocalHome.java
 * Sub-Module : Buy charges master
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.operations.charges.buycharges.ejb.bmp;

import com.foursoft.esupply.common.exception.FoursoftException;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

public interface BuyChargesEntityBeanLocalHome extends EJBLocalHome 
{
  BuyChargesEntityBeanLocal create(com.qms.operations.charges.java.BuychargesHDRDOB param0)
  throws com.foursoft.esupply.common.exception.FoursoftException,CreateException,javax.ejb.EJBException,java.sql.SQLException;

  BuyChargesEntityBeanLocal findByPrimaryKey(BuyChargesEntityBeanPK primaryKey)
  throws ObjectNotFoundException,FinderException,java.sql.SQLException;
}
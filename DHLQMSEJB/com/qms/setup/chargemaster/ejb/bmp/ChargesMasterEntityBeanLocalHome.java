/**
 * @ (#) ChargesMasterEntityBeanLocalHome.java
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
 * File : ChargesMasterEntityBeanLocalHome.java
 * Sub-Module : chargesmaster
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.chargemaster.ejb.bmp;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface ChargesMasterEntityBeanLocalHome extends EJBLocalHome 
{
  
  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @throws javax.ejb.CreateException
   * @return 
   * @param param0
   */
  ChargesMasterEntityBeanLocal create(java.util.ArrayList param0)throws CreateException,javax.ejb.EJBException,java.sql.SQLException;

  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.FinderException
   * @return 
   * @param primaryKey
   */
  ChargesMasterEntityBeanLocal findByPrimaryKey(ChargesMasterEntityBeanPK primaryKey) throws FinderException,javax.ejb.ObjectNotFoundException;
}
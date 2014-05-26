/**
 * @ (#) ChargeMasterSessionHome.java
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
 * File       : ChargeMasterSessionHome.java
 * Sub-Module : Buy charges
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * @date      : 25-06-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface ChargeMasterSessionHome extends EJBHome 
{
  com.qms.operations.charges.ejb.sls.ChargeMasterSession create() throws RemoteException, CreateException;
}
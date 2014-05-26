/**
 * @ (#) SellChargesEntityLocal.java
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
 * File : SellChargesEntityLocal.java
 * Sub-Module : Buy charges master
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.operations.charges.sellcharges.ejb.bmp;
import javax.ejb.EJBLocalObject;
import com.qms.operations.charges.java.BuychargesHDRDOB;

public interface SellChargesEntityLocal extends EJBLocalObject 
{
    /**
     * 
     * @throws java.rmi.RemoteException
     * @param param0
     */



   public void setSellchargesHDRDOB(BuychargesHDRDOB param0);


   
    /**
     * 
     * @throws java.rmi.RemoteException
     * @return 
     */


   public BuychargesHDRDOB getSellchargesHDRDOB();

}
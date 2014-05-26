/**
 * @ (#) ChargeMasterSession.java
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
 * File       : ChargeMasterSession.java
 * Sub-Module : Buy charges
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * @date      : 25-06-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.ejb.sls;
import com.qms.operations.charges.java.BuychargesHDRDOB;
import com.qms.operations.charges.java.QMSCartageMasterDOB;
import com.qms.operations.charges.java.QMSCartageSellDtlDOB;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.qms.operations.charges.java.BuySellChargesEnterIdDOB;

public interface ChargeMasterSession extends EJBObject 
{

  public java.util.ArrayList  insertBuyChargesDetails(java.util.ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
    
  /*public com.qms.operations.charges.java.BuychargesHDRDOB loadBuychargeDetails(String param0,String param1,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
  throws javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;  
  */
  
  public com.qms.operations.charges.java.BuychargesHDRDOB loadBuychargeDetails(BuySellChargesEnterIdDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
  throws FoursoftException,javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;  
  
  public boolean modifyBuyChargesDetails(com.qms.operations.charges.java.BuychargesHDRDOB  param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
  throws javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;
  
  public java.util.ArrayList getTerminalListForBuyRSellCharges(BuySellChargesEnterIdDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
  throws javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;

  public java.util.ArrayList getBuySellChargeIds(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
 
  public java.util.ArrayList getBuySellChargeDescIds(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public java.util.ArrayList getBuyChargeIds(String param0,String param1,String param2,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param3)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public java.util.ArrayList getBuyChargeDescIds(String param0,String param1,String param2,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param3)
  throws javax.ejb.EJBException,java.rmi.RemoteException;

  public java.util.ArrayList getBuyChargeBasisIds(String param0,String param1,String param2,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param3)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public String validateHeaderData(BuychargesHDRDOB buychargesHDRDOB)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  //@@Added by Kameswari for the WPBN issue- on 31/03/09
   public ArrayList getAcceptanceDetails() throws javax.ejb.EJBException,java.rmi.RemoteException;
  public int insertSellChargeAccDtls(ArrayList chargesList) throws javax.ejb.EJBException,java.rmi.RemoteException;
   //@@WPBN issue- on 31/03/09
  public com.qms.operations.charges.java.BuychargesHDRDOB loadBuychargeDetailsForSellCharges(String param0,String param1,String param3,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
  throws javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;
  
  public String  insertSellChargesDetails(com.qms.operations.charges.java.BuychargesHDRDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public com.qms.operations.charges.java.BuychargesHDRDOB loadSellchargeDetails(BuySellChargesEnterIdDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
  throws FoursoftException,javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;  
  
  /*public com.qms.operations.charges.java.BuychargesHDRDOB loadSellchargeDetails(String param0,String param1,String param2,String param3,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param4)
  throws javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;
  */  
  public boolean modifySellChargesDetails(com.qms.operations.charges.java.BuychargesHDRDOB  param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
  throws javax.ejb.ObjectNotFoundException,java.rmi.RemoteException,java.sql.SQLException;
  
  public StringBuffer validateCartageBuyChargesHdr(com.qms.operations.charges.java.QMSCartageMasterDOB param0) 
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  public StringBuffer validateCartageSellChargesHdr(com.qms.operations.charges.java.QMSCartageMasterDOB param0) 
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public java.util.ArrayList insertCartageBuyCharges(java.util.ArrayList param0) throws javax.ejb.EJBException,java.rmi.RemoteException;
  public void insertCartageSellCharges(java.util.ArrayList param0) throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public java.util.ArrayList getBuyCartageChargesFlat(com.qms.operations.charges.java.QMSCartageMasterDOB param0) throws javax.ejb.EJBException,java.rmi.RemoteException;
  public java.util.ArrayList getBuyCartageChargesSlab(com.qms.operations.charges.java.QMSCartageMasterDOB param0) throws javax.ejb.EJBException,java.rmi.RemoteException;
  public java.util.ArrayList getBuyCartageChargesList(com.qms.operations.charges.java.QMSCartageMasterDOB param0) throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public HashMap addChargeDiscriptionDtls(ArrayList chargeDescList,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public boolean deleteChargeDiscriptionDtls(String chargeId,String descId,String terminalId) throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  //public BuychargesHDRDOB selectChargeDiscriptionDtls(String chargeId,String descId ,String terminalId) throws javax.ejb.EJBException,java.rmi.RemoteException;
  public BuychargesHDRDOB selectChargeDiscriptionDtls(BuySellChargesEnterIdDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
  throws FoursoftException,javax.ejb.EJBException,java.rmi.RemoteException;
  
     public boolean modifyChargeDiscriptionDtls(BuychargesHDRDOB  chargeDOB) throws javax.ejb.EJBException,java.rmi.RemoteException;
     
     public ArrayList getDesriptionIds(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB)throws javax.ejb.EJBException,java.rmi.RemoteException;
     
     //public ArrayList getChargeDescDetails() throws java.rmi.RemoteException;
    public ArrayList getChargeDescDetails(String param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
    
    public void invalidateChargeDescId(ArrayList chargeList) throws java.rmi.RemoteException;

  public java.util.ArrayList getDesriptionIds(String param0,String param1,String param2,String param3,String chargeGroupId)
  throws javax.ejb.EJBException,java.rmi.RemoteException;//@@Modified by subrahmanyam for pbn id: 195270 on 20-Jan-10
  
  public ArrayList getChargeIdsForChargeDesc(String param0,String param1)
  throws javax.ejb.EJBException,java.rmi.RemoteException; 
  public ArrayList getUpdatedBuyCharges(QMSCartageMasterDOB masterDOB) throws javax.ejb.EJBException,java.rmi.RemoteException; 
  public QMSCartageSellDtlDOB getSellChargesDtls(String cartageId,String zoneCode,String chargeType) throws javax.ejb.EJBException,java.rmi.RemoteException; 
  
  public ArrayList getSellCartageChargesFlat(QMSCartageMasterDOB cartageMaster) throws EJBException,SQLException,RemoteException;
  
  public String insertNewCartageSellDtl(ArrayList param0)throws EJBException,RemoteException;
  
  public void updateSellDtls(ArrayList list)throws EJBException,RemoteException;
  
  public QMSCartageSellDtlDOB getSellChargesDtlsSlab(String cartageId,String zoneCode,String chargeType,String chargeBasis,String unitofMeasure)throws EJBException,RemoteException;
  public QMSCartageSellDtlDOB getListUpdatedCharges(String cartageId,String zoneCode,String chargeType)throws EJBException,RemoteException;
  public HashMap uploadChargeDescriptionDetails(ArrayList successList,String process, com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
  public ArrayList getDensityGroupCodeList()throws EJBException,RemoteException;  //@@Added by Kameswari for the WPBBN issue-54554
 // Commented by subrahmanyam for the pbn id: 186783  on 22/oct/09
 // public ArrayList getDensityGroupCodesList(String shipmentMode)throws EJBException,RemoteException;//@@Added by subrahmanyam for the WPBN issue-145057 on 13/11/08
  public ArrayList getDensityGroupCodesList(String shipmentMode, String chargeBasis)throws EJBException,RemoteException;//@@ Added by subrahmanyam for the pbn id: 186783 on/oct/09
  public ArrayList  getChargeBasisList()throws EJBException,RemoteException;  //@@Added by Kameswari for the WPBN issue-106698
}
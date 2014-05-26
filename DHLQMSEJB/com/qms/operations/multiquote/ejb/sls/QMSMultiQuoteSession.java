
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
 

 * File					: QMSMultiQuoteSession.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */





package com.qms.operations.multiquote.ejb.sls;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFinalDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates;
import com.qms.operations.multiquote.dob.MultiQuoteMasterDOB;
import com.qms.operations.multiquote.dob.MultiQuoteTiedCustomerInfo;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.reports.java.UpdatedQuotesFinalDOB;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;

public interface QMSMultiQuoteSession extends EJBObject 
{
  public StringBuffer validateQuoteMaster(MultiQuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
  public MultiQuoteFinalDOB  getFreightSellRates(MultiQuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public MultiQuoteMasterDOB getShipperConsigneeZones(MultiQuoteMasterDOB masterDOB) throws	EJBException,RemoteException;
  public void  updateAttentionToContacts(HashMap details) throws EJBException,RemoteException;// added by phani sekhar for wpbn 167678
  public MultiQuoteFinalDOB getMarginLimit(MultiQuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public MultiQuoteFinalDOB  getChargesAndHeader(MultiQuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public MultiQuoteFinalDOB getQuoteHeader(MultiQuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public ArrayList getAttachmentDtls(MultiQuoteFinalDOB finalDOB)throws EJBException ,RemoteException;
  public ArrayList getAttachmentIdList(MultiQuoteFinalDOB finalDOB, String attachmentId)throws EJBException,RemoteException;
  public String getCountryId(int addressId)throws EJBException ,RemoteException;
  public String insertQuoteMasterDtls(MultiQuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
  public MultiQuoteFinalDOB getQuoteContentDtl(MultiQuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public ArrayList getQuoteAttachmentDtls(MultiQuoteFinalDOB finalDOB)throws EJBException,RemoteException;
  public String getEmailText(String terminalId,String quoteType) throws EJBException,RemoteException;//@@Added by Kameswari for the WPBN issue-61295
  public String  updateSendMailFlag(String quoteId,String userId,String operation,boolean compareFlag,int mailStatus) throws EJBException,RemoteException;
  public StringBuffer validateQuoteId(MultiQuoteMasterDOB masterDOB) throws	EJBException,RemoteException;
  public MultiQuoteFinalDOB getMasterInfo(String quoteId,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException,SQLException;
  public String modifyQuoteMasterDtls(MultiQuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
  public MultiQuoteFinalDOB getQuoteDetails(String quoteId,String buyRatesFlag,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
  public String getServiceLevelDesc(String QuoteId) throws EJBException,RemoteException;
  //Added by Rakesh on 11-01-2011
  public ArrayList  getQuoteGroupIds(QuoteMasterDOB masterDOB) throws EJBException,RemoteException;
  public ArrayList  getQuoteGroups(String[] quoteIds,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
  public CostingMasterDOB getQuoteRateInfo(CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean)throws RemoteException; 
  public MultiQuoteFinalDOB getUpdatedQuoteInfo(long QuoteId,String changeDesc,String sellBuyFlag,String buyRatesFlag,ESupplyGlobalParameters loginbean,String quoteType) throws EJBException,RemoteException;//Added for the Issue 234719
  public String getLocationName(String locName) throws EJBException,RemoteException,SQLException; // Added by Gowtham to get Location Name in pdf Landscape Issue.
  public ArrayList getChargeInfoDetailsforView(String quoteId) throws EJBException,RemoteException,SQLException; // Added by Gowtham for Pdf Landscape Issue in View Case. on 09Mar2011.
  public String getServiceLevelName(String ServLevelName) throws EJBException,RemoteException,SQLException; // Added by Gowtham to get Service Level Description in pdf Landscape Issue.
  public String getPortName(String portName) throws EJBException,RemoteException,SQLException; // Added by Gowtham to get Service Level Description in pdf Landscape Issue.
  public MultiQuoteFreightLegSellRates getLocFullNames(MultiQuoteFreightLegSellRates legDOB,String org,String dest,String orgLoc,String destLoc) throws EJBException,RemoteException;
 public String getCarrierName(String CarrieDesc) throws EJBException,RemoteException,SQLException;//added by silpa.p on 16-05-11
}
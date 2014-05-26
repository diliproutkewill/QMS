package com.qms.operations.quote.ejb.sls;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.quote.dob.QuoteAttachmentDOB;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.operations.quote.dob.QuoteTiedCustomerInfo;
import com.qms.reports.java.UpdatedQuotesFinalDOB;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;

public interface QMSQuoteSession extends EJBObject 
{
//@@ Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
 // public long insert0QuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
//@@ Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
  public String insertQuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
  public ArrayList  getQuoteIds(QuoteMasterDOB masterDOB,String searchString) throws	EJBException,RemoteException;
  public StringBuffer validateQuoteMaster(QuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
  //public QuoteFinalDOB  getQuoteTiedCustomerInfo(QuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public QuoteFinalDOB  getFreightSellRates(QuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public QuoteFinalDOB  getChargesAndHeader(QuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public QuoteFinalDOB getMarginLimit(QuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public ArrayList  getQuoteIds(String customerId,String searchString,String originLoc,String destLocation,String serviceLevel,String terminalid,String searchStr) throws EJBException,RemoteException;
//@@ Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
  //public String  updateSendMailFlag(long quoteId,String userId,String operation,boolean compareFlag,int mailStatus) throws EJBException,RemoteException;
//@@ Below Stmt added by subrahmanyam for the enhancement #146971 on 03/12/2008
  public String  updateSendMailFlag(String quoteId,String userId,String operation,boolean compareFlag,int mailStatus) throws EJBException,RemoteException;  
  public CostingMasterDOB getQuoteRateInfo(CostingHDRDOB param0,ESupplyGlobalParameters param1)
  throws EJBException,RemoteException;
  
  public String  validateCostingHDR(CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean)
  throws EJBException,RemoteException;
  
  public boolean insertCostingHDR(CostingHDRDOB costingHDRDOB)
  throws EJBException,RemoteException;

  public QuoteFinalDOB getMasterInfo(String quoteId,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
//@@ Commented By subrahmanyam for the Enhancement #146971 on 03/12/2008
 // public long modifyQuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
//@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
  public String modifyQuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException,RemoteException;
  public QuoteMasterDOB getShipperConsigneeZones(QuoteMasterDOB masterDOB) throws	EJBException,RemoteException;
  public StringBuffer validateQuoteId(QuoteMasterDOB masterDOB) throws	EJBException,RemoteException;
  public QuoteFinalDOB getQuoteHeader(QuoteFinalDOB finalDOB) throws EJBException,RemoteException; 

  
  public ArrayList  getCostingQuoteIds(QuoteMasterDOB masterDOB,String searchString)
  throws	EJBException,RemoteException;

  public ArrayList  getQuoteGroups(String[] quoteIds,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
  public ArrayList  getQuoteGroupIds(QuoteMasterDOB masterDOB) throws EJBException,RemoteException;
//  public QuoteFinalDOB getUpdatedQuoteInfo(long QuoteId,String changeDesc,String sellBuyFlag,String buyRatesFlag,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
  public QuoteFinalDOB getUpdatedQuoteInfo(long QuoteId,String changeDesc,String sellBuyFlag,String buyRatesFlag,ESupplyGlobalParameters loginbean,String quoteType) throws EJBException,RemoteException;
  public ArrayList getUpdatedQuoteDetails(UpdatedQuotesFinalDOB reportFinalDOB,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
  public QuoteFinalDOB getQuoteContentDtl(QuoteFinalDOB finalDOB) throws EJBException,RemoteException;
  public String validateCurrency(String baseCurrency)throws EJBException,RemoteException;
  public QuoteFinalDOB getQuoteDetails(String quoteId,String buyRatesFlag,ESupplyGlobalParameters loginbean)  throws EJBException,RemoteException;
  public CostingMasterDOB getContactPersonDetails(CostingMasterDOB costingMasterDOB) throws EJBException,RemoteException;
   public String getEmailText(String terminalId,String quoteType) throws EJBException,RemoteException;//@@Added by Kameswari for the WPBN issue-61295
   //@@Added by Kameswari for the WPBN issue-61289
  public ArrayList getAttachmentIdList(QuoteFinalDOB finalDOB, String attachmentId)throws EJBException,RemoteException;
  public ArrayList getQuoteAttachmentDtls(QuoteFinalDOB finalDOB)throws EJBException,RemoteException;
  public ArrayList getAttachmentDtls(QuoteFinalDOB finalDOB)throws EJBException ,RemoteException;
//@@WPBN issue-61289
   public String getCountryId(int addressId)throws EJBException ,RemoteException;//@@Added for the WPBN Change Request-71229
   public ArrayList getUpdatedQuotesList(ArrayList updatedQuotesList)throws EJBException ,RemoteException;//@@Added by Kameswari for the WPBN Issue-100070
   public String getLocation(String salesPersonName) throws EJBException, RemoteException; //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
   public void  updateAttentionToContacts(HashMap details) throws EJBException,RemoteException;// added by phani sekhar for wpbn 167678
   public String getServiceLevelDesc(String QuoteId) throws EJBException, RemoteException; //@@ Added by subrahmanyam for the wpbn #185127 on 05/oct/2009

}
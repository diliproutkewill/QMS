package com.qms.reports.ejb.sls;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.reports.java.ReportsEnterIdDOB;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;

public interface ReportsSession extends EJBObject 
{   
  public java.util.ArrayList getBuyRatesExpiryReport(ReportsEnterIdDOB reportsEnterIdDOB)
  throws javax.ejb.EJBException,java.rmi.RemoteException;

  public java.util.ArrayList getPendingQuoteReportDetails(ReportsEnterIdDOB reportsEnterIdDOB)
  throws javax.ejb.EJBException,java.rmi.RemoteException;

  public java.util.ArrayList getUpdatedQuotesReportDetails(String changeDesc,int pageNo,String sortBy,String sortOrder,String terminalId,String repFormat,String userId,String empId)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public java.util.ArrayList getAproveRRejectQuoteDetail(ReportsEnterIdDOB	 reportenterDob)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public java.util.ArrayList getEscalatedQuoteReportDetails(ReportsEnterIdDOB	 reportenterDob)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  
  public java.util.ArrayList getQuoteExpiryReport(ReportsEnterIdDOB reportsEnterIdDOB)
  throws javax.ejb.EJBException,java.rmi.RemoteException;

  public java.util.ArrayList getActivityReportDetails(ReportsEnterIdDOB reportsEnterIdDOB)
  throws javax.ejb.EJBException,java.rmi.RemoteException;

  public java.util.ArrayList getYieldReportDetails(ReportsEnterIdDOB reportsEnterIdDOB)
  throws javax.ejb.EJBException,java.rmi.RemoteException;  
  public void sendMail(String frmAddress, String toAddress, String message, String attachment) throws EJBException,RemoteException;

  public String updateQuoteExpiryReport(ArrayList updateList)throws EJBException,RemoteException;
  public String updatePendingQuoteReport( ArrayList updateDataList)throws EJBException,RemoteException;
  public String updateAprovedQuoteDetail(ArrayList updateList, ESupplyGlobalParameters loginbean)throws EJBException,RemoteException;
  public String updateEscalatedQuoteReportDetails(ArrayList updateList)throws EJBException,RemoteException;
  public void sendMail(String frmAddress, String toAddress, String message, String attachments,File[] fArr) throws EJBException,RemoteException;
  public ArrayList getUpdatedQuotes(String terminalId,String userId,String empId) throws EJBException,RemoteException;
  
  public ArrayList getYieldReportDetailsLegs(String quoteId)
  throws EJBException,RemoteException;
  public HashMap getActivitySummaryReportDetails(ESupplyGlobalParameters loginbean)throws EJBException,RemoteException;
  
  public String[] getAppRejMailDetails(String quoteId, String userId) throws EJBException, RemoteException;
  //modified by phani sekhar for wpbn 181670 on 20090909
  public HashMap updateActivityFlag(HashMap checkMap)throws EJBException,RemoteException;//Shyam added for Issue No: 14048
  public String updateSendMailFlag (ArrayList sentList, String userId) throws EJBException,RemoteException;
//@@Added by Kameswari for the WPBN issue-167655 on 23/04/09
  //Modified By Kishore Podili FOr QuoteGroup Excel Report with Charges
 public HashMap getGroupingExcelDetails(HashMap<String,String> paramsMap, ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
//@@WPBN issue-167655 on 23/04/09
public String validateRateDetails(String customerId,String originCountry,String originCity,String destCountry,String destCity,ESupplyGlobalParameters loginbean)  throws EJBException,RemoteException;
  public ArrayList getFreightRatesExcelDetails(com.qms.reports.java.QMSRatesReportDOB ratesReportDOB) throws EJBException,RemoteException;//added by phani for 167656
   public StringBuffer validateFreightReportData(com.qms.reports.java.QMSRatesReportDOB ratesReportDOB)throws EJBException,RemoteException;//added by phani for 167656
   public String getPlaceName(String placeId) throws EJBException,RemoteException;

}
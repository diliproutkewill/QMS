import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.ejb.sls.Mailer;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.ejb.sls.MailerHome;
import com.foursoft.esupply.common.java.EMailMessage;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
//import com.foursoft.esupply.common.util.Logger;
import com.qms.operations.quote.dob.QuoteAttachmentDOB;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.TreeSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import com.qms.operations.quote.dob.QuoteCartageRates;
import com.qms.operations.quote.dob.QuoteCharges;
import com.qms.operations.quote.dob.QuoteFreightLegSellRates;
import com.qms.operations.quote.dob.QuoteHeader;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.reports.ejb.sls.ReportsSession;
import com.qms.reports.ejb.sls.ReportsSessionBeanHome;
import com.qms.operations.quote.ejb.sls.QMSQuoteSession;
import com.qms.operations.quote.ejb.sls.QMSQuoteSessionHome;
import com.qms.reports.java.ReportDetailsDOB;
import com.qms.reports.java.ReportsEnterIdDOB;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteChargeInfo;
import com.qms.operations.rates.ejb.sls.BuyRatesSession;
import com.qms.operations.rates.ejb.sls.BuyRatesSessionHome;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;

import com.qms.reports.java.UpdatedQuotesDOB;
import com.qms.reports.java.UpdatedQuotesFinalDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

import java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;
import java.sql.Timestamp;


import java.util.Iterator;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.servlet.*;
import javax.servlet.http.*;
//import com.lowagie.text.Font;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;

public class QMSReportController extends HttpServlet 
{
 private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private PrintWriter out ;
  private ReportDetailsDOB  detailsDOB;
  private String FILE_NAME="QMSReportController.java";
  private static Logger logger = null;
  //private LookUpBean      lookUpBean      =   null;
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSReportController.class);
    super.init(config);
  }

  /**
   * Process the HTTP doGet request.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);
  }

  /**
   * Process the HTTP doPost request.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  { 
      HttpSession session                 = request.getSession();
      ESupplyGlobalParameters loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      
      ESupplyDateUtility  dateutil        =  new ESupplyDateUtility();
      String              format          =  loginbean.getUserPreferences().getDateFormat();
      ErrorMessage	errorMessageObject		=  null;
      ArrayList			keyValueList			    =  new ArrayList();
      String        errorMessage          =  "";
      String        errorCode             =  "";
	    String        operation             =  request.getParameter("Operation");
      String        subOperation          =  request.getParameter("subOperation");
      String        nextOperation         =  request.getParameter("nextOperation");
      String        nextNavigation        =  null;
      int           noofrec               =  0;
      String        fromaddress           =  null;
      String        successFlag           =  null;
      String        dd                    =  "";
      String        date                  =  "";
      String        datedate              =  "";
      String        datetime              =  "";
      String        updated               = null;
      String        terminalId            = null; 
      String        quoteType             = null; 
      String        emailText             = null; 
      int           listSize              =  0;
      int           datasize              = 0;
      int           relativeOffset	      =	loginbean.getRelativeOffset();
      Timestamp     localTime             =	null;
      String        fromSummary           = request.getParameter("fromSummary");//146463
      try
      {
        format                   =  loginbean.getUserPreferences().getDateFormat();
        dateutil.setPatternWithTime(format);
        
        ArrayList  dataList      =  new ArrayList();
        ArrayList  mainDataList  =  null;
        ArrayList  updateDataList=  new ArrayList();
        HashMap    updatedMapValue= new HashMap();
        ReportDetailsDOB  updateReportdetailsDOB =null;
        ReportsEnterIdDOB	 reportenterDob	= null;
        ReportsSession                 remote     =  null;
        ReportsSessionBeanHome         home       =  null;
        QMSQuoteSessionHome            qmshome    = null;
         QMSQuoteSession               qmsremote  = null;
        MailerHome         mailHome    =  null;
        Mailer             mailRemote  =  null;
        String             toaddress   = "";
       qmshome        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");//146460
       qmsremote      = qmshome.create();
     
        if("Approved".equalsIgnoreCase(operation))
        {
          dataList			    = (ArrayList)session.getAttribute("dataList");
          
          if("ApprovedUpdate".equalsIgnoreCase(subOperation))
          {
            ArrayList     quoteFinalDOBList = null;
            QuoteFinalDOB tmpFinalDOB       = null;
            String[]      contactPersons    = null;
            StringBuffer  toEmailIds        = null;
            int           mailFlag          = 0;
            String        msg               = "";
            ArrayList     filesList         = null;  
            //dataList			    = (ArrayList)session.getAttribute("dataList");
            doPageValues(request,response,dataList);
            
            if(session.getAttribute("HashList")!=null)
            {
              updatedMapValue					=	(HashMap)session.getAttribute("HashList");
              updateDataList  =   dogetFinalValues(request,response,updatedMapValue);
             
              listSize        =   updateDataList.size();
                  if(listSize==0)
              {
                errorMessageObject = new ErrorMessage("No Records Selected","QMSReportController?Operation=Approved"); 
                keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                //doFileDispatch(request,response,"ESupplyErrorPage.jsp");
                nextNavigation  = "ESupplyErrorPage.jsp";
              }
              else
              {
                int appListSize         =     0;
                quoteFinalDOBList       =     doSendApprovedQuotes(request,response,updateDataList);
              
                home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
                remote                  =     (ReportsSession)home.create();
               // fromaddress             =     remote.updateAprovedQuoteDetail(updateDataList,loginbean);
                
                if(quoteFinalDOBList!=null)
                {
                 appListSize  = quoteFinalDOBList.size();
                 ArrayList    list           =   null;
             
                 ArrayList    sentMailList   =   null;
                 ArrayList    unsentMailList =   null;
                 ArrayList    sentFaxList    =   null;
                 ArrayList    unsentFaxList  =   null;
                 for(int k=0;k<appListSize;k++)
                 {
                   toEmailIds     = new StringBuffer("");
                   tmpFinalDOB    = (QuoteFinalDOB) quoteFinalDOBList.get(k);
                   //tmpFinalDOB.getFlagsDOB().setEmailFlag("Y");
                   //@@Added by Kameswari for the internal issue on 10/04/09
                      filesList  = qmsremote.getQuoteAttachmentDtls(tmpFinalDOB); 
                      if(filesList!=null)
                      {
                          tmpFinalDOB.setAttachmentDOBList(filesList);           
                      }
                      //@@internal issue on 10/04/09
                   if("Y".equalsIgnoreCase(tmpFinalDOB.getFlagsDOB().getEmailFlag()) || "Y".equalsIgnoreCase(tmpFinalDOB.getFlagsDOB().getFaxFlag()))
                   {
                    //@@Added by Kameswari for the internal issue on 10/04/09
                    /* if(tmpFinalDOB.getUpdatedReportDOB()!=null)
                         quoteType  = "U";
                     else 
                          quoteType = "N";
                     terminalId  = loginbean.getTerminalId();
                     emailText = qmsremote.getEmailText(terminalId,quoteType); 
                     tmpFinalDOB.setEmailText(emailText);
                     //@@internal issue on 10/04/09*/
                     list           = doPDFGeneration(tmpFinalDOB, request, response);
                     if(list !=null && list.size()>0)
                     {
                        sentMailList   = (ArrayList)list.get(0);
                        unsentMailList = (ArrayList)list.get(1);
                        sentFaxList    = (ArrayList)list.get(2);
                        unsentFaxList  = (ArrayList)list.get(3);
                     }
                     msg  =   msg + " \n For Quote Id "+tmpFinalDOB.getMasterDOB().getQuoteId() +":";
                     
                     if(sentMailList!=null && sentMailList.size()>0)
                        msg =   msg + "Quote has been successfully sent through Email to "+sentMailList + ", ";
                     if(unsentMailList != null && unsentMailList.size()>0)
                        msg =   msg + "Email(s) Were Not Sent To "+unsentMailList + ", ";
                     if(sentFaxList != null && sentFaxList.size()>0)
                        msg =   msg + "Quote has been successfully sent through Fax to "+sentFaxList + ", ";
                     if(unsentFaxList != null && unsentFaxList.size()>0)
                        msg =   msg + "Fax Was Not Sent to  "+ unsentFaxList;
                        
                      if(sentMailList.size() == 0 && sentFaxList.size()==0)
                      {
                          updateDataList.remove(k);
                          updateDataList.add(k,null);
                      }
                   }
                   else
                   {
                      // msg =   msg + "\nQuote Id "+tmpFinalDOB.getMasterDOB().getQuoteId()+" Has Not Been Sent to the Customer as neither Email nor Fax Was Selected in this Quote's Send Options. ";
                    //@@ Commented & Added by subrahmanyam for the Updation issue: 204871 Of Approved Quotes with out Any Flag
                	   //msg =   msg + "\nQuote Id "+tmpFinalDOB.getMasterDOB().getQuoteId()+" Has Not Been Sent to the Customer as neither Email nor Fax Was Selected in this Quote's Send Options.Choose Quote View option to print the Quote PDF";
                	   msg =   msg + "\nQuote Id "+tmpFinalDOB.getMasterDOB().getQuoteId()+" Has Beed Updated to Pending, But Not Been Sent to the Customer as neither Email nor Fax Was Selected in this Quote's Send Options.";
                       //updateDataList.remove(k);
                       //updateDataList.add(k,null);
                    //@@ Ended by subrahmanyam for the Updation  issue: 204871 Of Approved Quotes with out Any Flag                	   
                   }
                   msg    =   msg.trim();
                   //@@ To Remove the Comma at the End of the message (if any).
                   if (msg.indexOf(",")!=-1)
                   {
                     if(msg.indexOf(",") == msg.length()-1)
                        msg = msg.substring(0,msg.length()-1);
                   }
                   /*if(mailFlag==1)
                    sentList.add(toEmailIds+" for Quote Id:"+tmpFinalDOB.getMasterDOB().getQuoteId());
                   else
                   {
                    unSentList.add(toEmailIds+" for Quote Id:"+tmpFinalDOB.getMasterDOB().getQuoteId());
                    updateDataList.remove(k);
                   }*/
                 }
              
                 remote.updateAprovedQuoteDetail(updateDataList,loginbean);
                 
                }
              /*String msg = "";
              if(sentList.size()>0)
               msg = " have successfully been sent to"+sentList.toString();
              if(unSentList.size()>0 && sentList.size()>0)
                 msg = msg + "\n but have not been sent to"+unSentList.toString();
              else if(unSentList.size()>0 && sentList.size()==0)
                 msg = " have not been sent to"+unSentList.toString();*/
               
              errorMessage = msg;
              errorCode    = "MSG";
              //commented by subrahmanyam for 146463
              //errorMessageObject = new ErrorMessage("No Records Selected","QMSReportController?Operation=Approved"); 
              //errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController?Operation=Approved"); 
              //added by subrahmanyam for 146463
              if("yes".equalsIgnoreCase(fromSummary))
                  errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController?Operation=ActivitySummary"); 
              else
                  errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController?Operation=Approved"); 
              keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
              keyValueList.add(new KeyValue("Operation",operation)); 	
              errorMessageObject.setKeyValueList(keyValueList);
              request.setAttribute("ErrorMessage",errorMessageObject); 
              nextNavigation = "ESupplyErrorPage.jsp";
              session.removeAttribute("dataList");
              session.removeAttribute("HashList");
              /*successFlag   =   doGeneratePDF(request,response,loginbean,updateDataList,fromaddress);
              
              if("success".equalsIgnoreCase(successFlag))
              {  
                errorMessageObject = new ErrorMessage("Email(s) Successfully Sent",request.getContextPath()+"/ESMenuRightPanel.jsp?link=es-et-Administration-Reports"); 
                keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doFileDispatch(request,response,"ESupplyErrorPage.jsp");
              }
              else if("Error".equalsIgnoreCase(successFlag))
              {
                 errorMessageObject = new ErrorMessage("The Email(s) were not sent. Please Check the Email Ids of the Customer(s)","QMSReportController?Operation=Approved"); 
                keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doFileDispatch(request,response,"ESupplyErrorPage.jsp");
              }*/
              }
            }       
          }       
          else if(subOperation==null || "null".equalsIgnoreCase(subOperation))
          {
            if("Approvedpagging".equals(nextOperation))
            {
                reportenterDob=new ReportsEnterIdDOB();
                reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
                if(request.getParameter("pageNo")!=null)
                {
                  String pageno=request.getParameter("pageNo");
                  reportenterDob.setPageNo(Integer.parseInt(pageno)); 
                }
               doPageValues(request,response,dataList);
             }
             else if("pageBrowse".equals(nextOperation))
              {
                reportenterDob=new ReportsEnterIdDOB();
                reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
                if(request.getParameter("PageNo")!=null)
                {
                  String pageno=request.getParameter("PageNo");
                  reportenterDob.setPageNo(Integer.parseInt(pageno)); 
                }
               String SortBy    = request.getParameter("SortBy");
               String SortOrder = request.getParameter("SortOrder");                
               reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
               reportenterDob.setSortBy(SortBy);
               reportenterDob.setSortOrder(SortOrder);               
               doPageValues(request,response,dataList);
               nextNavigation   =  "reports/QMSQuoteApprovedReport.jsp?Operation="+operation+"&nextOperation="+nextOperation+"&SortBy="+SortBy+"&SortOrder="+SortOrder+"&PageNo="+request.getParameter("PageNo");
             }
            else
            {
              reportenterDob=new ReportsEnterIdDOB();
              reportenterDob.setUserId(loginbean.getUserId());
              reportenterDob.setTerminalId(loginbean.getTerminalId());
              reportenterDob.setQuoteStatus("APP");   
              reportenterDob.setPageNo(1);
              reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
              reportenterDob.setSortBy("QuoteId");//@@Modified by Kameswari for the WPBN issue-38116
              reportenterDob.setSortOrder("ASC");
              nextNavigation   =  "reports/QMSQuoteApprovedReport.jsp?Operation="+operation+"&nextOperation="+nextOperation+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";//@@Modified by Kameswari for the WPBN issue-38116
          
           /*   if (request.getParameter("noofrecords")!=null && !"".equals(request.getParameter("noofrecords")))
              {
               String noofrecs = request.getParameter("noofrecords");
               reportenterDob.setNoOfRecords(Integer.parseInt(noofrecs));
              }*/
            }
            try 
            {     
             
              mainDataList  = new ArrayList();
              reportenterDob.setFormateSerch(request.getParameter("format"));
              home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
              remote	= (ReportsSession)home.create();
              mainDataList  = (ArrayList)  remote.getAproveRRejectQuoteDetail(reportenterDob);
              request.setAttribute("mainDataList",mainDataList);
           
            }
            catch(Exception e)
            {
              e.printStackTrace();
            }
          
            session.setAttribute("reportenterDob",reportenterDob);
            //nextNavigation   =  "reports/QMSQuoteApprovedReport.jsp?Operation="+operation+"&nextOperation="+nextOperation;
          
          if("Submit".equalsIgnoreCase(nextOperation))
          {
            if("Excel".equalsIgnoreCase(request.getParameter("format")))
            {
              nextNavigation        = null;
              String str[]          = null;
              StringBuffer sb       = null;
              String shipmentMode   = "";
              
              if(mainDataList!=null && mainDataList.size()>0)
              {       		
                dataList		   =	(ArrayList)mainDataList.get(1);
               
                out  =  response.getWriter();
              
                response.setContentType("application/vnd.ms-excel");	
                String contentDisposition = " :attachment;";	
                response.setHeader("Content-Disposition","attachment;filename=ApprovedReport.xls");
                sb   =   new StringBuffer("");
                sb.append("<html>");
                sb.append("<body>");
                sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");              
                sb.append("<tr align='center'>");
                sb.append("<td><b>IMPORTANT</b></td>");
                sb.append("<td><b>CUSTOMER ID</b></td>");
                sb.append("<td><b>CUSTOMER NAME</b></td>");//@@added by kameswari for the WPBN issue-30313
                sb.append("<td><b>QUOTE ID</b></td>");
                sb.append("<td><b>MODE</b></td>");
                sb.append("<td><b>SERVICE LEVEL</b></td>");
                sb.append("<td><b>FROM COUNTRY</b></td>");
                sb.append("<td><b>FROM LOCATION</b></td>");
                sb.append("<td><b>TO COUNTRY</b></td>");
                sb.append("<td><b>TO LOCATION</b></td>");
                sb.append("<td><b>DUE DATE</b></td>");
                sb.append("<td><b>DUE TIME</b></td>");
                sb.append("<td><b>APPROVED BY</b></td>");
                sb.append("<td><b>APPROVED DATE</b></td>");
                sb.append("<td><b>APPROVED TIME</b></td>");
                sb.append("<td><b>INTERNAL NOTES</b></td>");
                sb.append("<td><b>EXTERNAL NOTES</b></td>");
                sb.append("</tr>");
                int dataListSize =	dataList.size();
                for(int i=0;i<dataListSize;i++)
                {
                    sb.append("<tr align='center'>");
                    detailsDOB  = (ReportDetailsDOB)dataList.get(i);
                    
                    if("1".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Air";
                    else if("2".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Sea";
                    else if("4".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Truck";
                    else if("100".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Multi-Modal";
                        
                    sb.append("<td>").append("I".equalsIgnoreCase(detailsDOB.getImportant())?"IMP":"").append("</td>");
                    sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>"); //@@added by kameswari for the WPBN issue-30313
                    sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                    sb.append("<td>").append(shipmentMode).append("</td>");
                    sb.append("<td>").append(detailsDOB.getServiceLevel()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                    
                    if(detailsDOB.getDueDateTmstmp()!=null)			
                    {
                      localTime = new Timestamp(detailsDOB.getDueDateTmstmp().getTime() + relativeOffset);
                      str = dateutil.getDisplayStringArray(localTime);
                      datedate=str[0];
                      datetime=str[1];
                    }
                    
                    sb.append("<td>").append(datedate).append("</td>");
                    sb.append("<td>").append(datetime).append("</td>");
                    sb.append("<td>").append(detailsDOB.getApprovedRrejectedBy()).append("</td>");
                    if(detailsDOB.getApprovedRejTstmp()!=null)			
                    {
                      localTime = new Timestamp(detailsDOB.getApprovedRejTstmp().getTime() + relativeOffset);
                      str = dateutil.getDisplayStringArray(localTime);
                      datedate=str[0];
                      datetime=str[1];
                    }
                    sb.append("<td>").append(datedate).append("</td>");
                    sb.append("<td>").append(datetime).append("</td>");
                    sb.append("<td>").append(detailsDOB.getInternalRemarks()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getExternalRemarks()).append("</td>");
                    
                    sb.append("</tr>");
                }
                sb.append("</body>");
                sb.append("</html>");
                out.print(sb.toString());
              }
            }          
          }
        }
      }
      else if("Rejected".equalsIgnoreCase(operation))
      {
       if("RejectedUpdate".equalsIgnoreCase(subOperation))
       {
            dataList			    = (ArrayList)session.getAttribute("dataList");
            doPageValues(request,response,dataList);
            if(session.getAttribute("HashList")!=null)
            {
               updatedMapValue					=	(HashMap)session.getAttribute("HashList");
               updateDataList           = dogetFinalValues(request,response,updatedMapValue);
               home                     = (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
               remote                   = (ReportsSession)home.create();
                
              //  updated=remote.updateAproveRRejectQuoteDetail(updateDataList); 
            }
            if(updated!=null)
            {  
                errorMessageObject = new ErrorMessage("Successfully Updated the status",""); 
                //keyValueList.add(new KeyValue("ErrorCode","RSU")); 	
                //@@Commented and Modified by Kameswari for the WPBN issue-143523
                keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doFileDispatch(request,response,"ESupplyErrorPage.jsp");
            }
        }    
        if(subOperation==null)
        {
          if("Rejectedpagging".equals(nextOperation))
          {
              reportenterDob=new ReportsEnterIdDOB();
              reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
              
              if(request.getParameter("pageNo")!=null)
              {
                String pageno=request.getParameter("pageNo");
                reportenterDob.setPageNo(Integer.parseInt(pageno)); 
               }
                doPageValues(request,response,dataList);
            }
            else if("pageBrowse".equals(nextOperation))
            {
                reportenterDob=new ReportsEnterIdDOB();
                reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
                if(request.getParameter("PageNo")!=null)
                {
                String pageno=request.getParameter("PageNo");
                reportenterDob.setPageNo(Integer.parseInt(pageno)); 
                }
                String SortBy    = request.getParameter("SortBy");
                String SortOrder = request.getParameter("SortOrder");                
                reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
                reportenterDob.setSortBy(SortBy);
                reportenterDob.setSortOrder(SortOrder);
                doPageValues(request,response,dataList);
                nextNavigation   =  "reports/QMSQuoteRejectedReport.jsp?Operation="+operation+"&nextOperation="+nextOperation+"&SortBy="+SortBy+"&SortOrder="+SortOrder+"&PageNo="+request.getParameter("PageNo");
             }
            else
            {

              reportenterDob=new ReportsEnterIdDOB();
              reportenterDob.setUserId(loginbean.getUserId());
              reportenterDob.setTerminalId(loginbean.getTerminalId());
              reportenterDob.setQuoteStatus("REJ");
              reportenterDob.setPageNo(1);
              reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
              reportenterDob.setSortBy("QuoteId");//@@Modified by Kameswari for the WPBN issue-38116
              reportenterDob.setSortOrder("ASC");
              nextNavigation   =  "reports/QMSQuoteRejectedReport.jsp?Operation="+operation+"&nextOperation="+nextOperation+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";//@@Modified by Kameswari for the WPBN issue-38116
            }
              
              doPageValues(request,response,dataList);
           }
          else
          {
            reportenterDob=new ReportsEnterIdDOB();
            reportenterDob.setUserId(loginbean.getUserId());
            reportenterDob.setTerminalId(loginbean.getTerminalId());
            reportenterDob.setQuoteStatus("REJ");
            reportenterDob.setPageNo(1);
            reportenterDob.setNoOfRecords(10);
          }
          try
          {
              mainDataList  = new ArrayList();
              reportenterDob.setFormateSerch(request.getParameter("Submit"));
              home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
              remote	= (ReportsSession)home.create();
              mainDataList  = (ArrayList)  remote.getAproveRRejectQuoteDetail(reportenterDob);
              request.setAttribute("mainDataList",mainDataList);
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
          session.setAttribute("reportenterDob",reportenterDob);
            //nextNavigation   =  "reports/QMSQuoteRejectedReport.jsp?Operation="+operation+"&nextOperation="+nextOperation;
       if("Submit".equalsIgnoreCase(nextOperation))
        {
          if("Excel".equalsIgnoreCase(request.getParameter("Submit")))
          {
            nextNavigation      = null;
            String str[]        = null;
            StringBuffer sb     = null;
            String shipmentMode = ""; 
            
            if(mainDataList!=null && mainDataList.size()>0)
            {       		
              dataList		   =	(ArrayList)mainDataList.get(1);
              out  =  response.getWriter();
              response.setContentType("application/vnd.ms-excel");	
              String contentDisposition = " :attachment;";	
              response.setHeader("Content-Disposition","attachment;filename=RejectedQuotesReport.xls");
              
              sb   =   new StringBuffer("");
              sb.append("<html>");
              sb.append("<body>");
              sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");              
              sb.append("<tr align='center'>");
              sb.append("<td><b>IMPORTANT</b></td>");
              sb.append("<td><b>CUSTOMER ID</b></td>");
              sb.append("<td><b>CUSTOMER NAME</b></td>");//@@added by kameswari for the WPBN issue-30313
              sb.append("<td><b>QUOTE ID</b></td>");
              sb.append("<td><b>MODE</b></td>");
              sb.append("<td><b>SERVICE LEVEL</b></td>");
              sb.append("<td><b>FROM COUNTRY</b></td>");
              sb.append("<td><b>FROM LOCATION</b></td>");
              sb.append("<td><b>TO COUNTRY</b></td>");
              sb.append("<td><b>TO LOCATION</b></td>");
              sb.append("<td><b>DUE DATE</b></td>");
              sb.append("<td><b>DUE TIME</b></td>");
              sb.append("<td><b>REJECTED BY</b></td>");
              sb.append("<td><b>REJECTED DATE</b></td>");
              sb.append("<td><b>REJECTED TIME</b></td>");
              sb.append("<td><b>INTERNAL NOTES</b></td>");
              sb.append("<td><b>EXTERNAL NOTES</b></td>");
              sb.append("</tr>");
              int dListSize	= dataList.size();
              for(int i=0;i<dListSize;i++)
              {
                  sb.append("<tr align='center'>");
                  detailsDOB  = (ReportDetailsDOB)dataList.get(i);
                  if("1".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Air";
                  else if("2".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                      shipmentMode  = "Sea";
                  else if("4".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                      shipmentMode  = "Truck";
                  else if("100".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                      shipmentMode  = "Multi-Modal";
                      
                  sb.append("<td>").append("I".equalsIgnoreCase(detailsDOB.getImportant())?"IMP":"").append("</td>");
                  sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>"); //@@added by kameswari for the WPBN issue-30313
                  sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                  sb.append("<td>").append(shipmentMode).append("</td>");
                  sb.append("<td>").append(detailsDOB.getServiceLevel()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                  
                  if(detailsDOB.getDueDateTmstmp()!=null)			
                  {
                    localTime = new Timestamp(detailsDOB.getDueDateTmstmp().getTime() + relativeOffset);
                    str = dateutil.getDisplayStringArray(localTime);
                    datedate=str[0];
                    datetime=str[1];
                  }
                  
                  sb.append("<td>").append(datedate).append("</td>");
                  sb.append("<td>").append(datetime).append("</td>");
                  sb.append("<td>").append(detailsDOB.getApprovedRrejectedBy()).append("</td>");
                  if(detailsDOB.getApprovedRejTstmp()!=null)			
                  {
                    localTime = new Timestamp(detailsDOB.getApprovedRejTstmp().getTime() + relativeOffset);
                    str = dateutil.getDisplayStringArray(localTime);
                    datedate=str[0];
                    datetime=str[1];
                  }
                  sb.append("<td>").append(datedate).append("</td>");
                  sb.append("<td>").append(datetime).append("</td>");
                  sb.append("<td>").append(detailsDOB.getInternalRemarks()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getExternalRemarks()).append("</td>");
                  
                  sb.append("</tr>");
              }
              sb.append("</body>");
              sb.append("</html>");
              out.print(sb.toString());
              }
            }
          }
      }
      else if("Escalated".equalsIgnoreCase(operation))
      {
    
           if("yes".equalsIgnoreCase(fromSummary)&& subOperation==null)
            nextNavigation = "QMSReportController?Operation=ActivitySummary";
            
        else if(subOperation==null)
           nextNavigation   =  "reports/QMSQuoteEscalatedEnterId.jsp";
//@@ Ended by subrahmanyam for the Enhancement 146463 on 12/02/09             
        
//@@ Commented by subrahmanyam for the Enhancement 146463 on 12/02/09 
            /*if(subOperation==null)
           nextNavigation   =  "reports/QMSQuoteEscalatedEnterId.jsp";*/


             
        else if("EscalatedUpdate".equalsIgnoreCase(subOperation))
          {
            dataList			    = (ArrayList)session.getAttribute("dataList");
            doEscalatedPageValues(request,response,dataList,loginbean);
            ArrayList    totUpdatedList = new ArrayList();
            if(session.getAttribute("HashList1")!=null)
            {
               updatedMapValue					=	(HashMap)session.getAttribute("HashList1");
               updateDataList=dogetFinalValues(request,response,updatedMapValue);
               datasize=updateDataList.size();
               for(int i=0;i<datasize;i++)
               {
               updateReportdetailsDOB =(ReportDetailsDOB)updateDataList.get(i);
               totUpdatedList.add(updateReportdetailsDOB);
               }
            }
            if(session.getAttribute("HashList2")!=null)
            {
               updatedMapValue					=	(HashMap)session.getAttribute("HashList2");
               updateDataList=dogetFinalValues(request,response,updatedMapValue);
               datasize=updateDataList.size();
               for(int i=0;i<datasize;i++)
               {
               updateReportdetailsDOB =(ReportDetailsDOB)updateDataList.get(i);
               totUpdatedList.add(updateReportdetailsDOB);
               }  
            }
            if(totUpdatedList!=null && totUpdatedList.size()>0)
            {
               home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
               remote                  =     (ReportsSession)home.create();
                
                updated=remote.updateEscalatedQuoteReportDetails(totUpdatedList); 
            }
            if(updated!=null)
            {
                int size  = totUpdatedList.size();
                
                String[]  appRejMailDetails = null;
                String    subject           = null;
                String    body              = null;
             
                for(int i=0;i<size;i++)
                {
                  updateReportdetailsDOB  = (ReportDetailsDOB)totUpdatedList.get(i);
                  appRejMailDetails = remote.getAppRejMailDetails(updateReportdetailsDOB.getQuoteId(), updateReportdetailsDOB.getUserId());
                
                  if("App".equalsIgnoreCase(updateReportdetailsDOB.getQuoteStatus()))
                  {
                    subject = "Quote Approved by Manager, "+appRejMailDetails[3]+", "+updateReportdetailsDOB.getQuoteId();
                    body    = "Quote Reference "+updateReportdetailsDOB.getQuoteId()+" was Approved by Manager.  Please send the quote to the customer.";
                  }
                  else if("Rej".equalsIgnoreCase(updateReportdetailsDOB.getQuoteStatus()))
                  {
                    subject = "Quote Rejected by Manager, "+appRejMailDetails[3]+", "+updateReportdetailsDOB.getQuoteId();
                    body    = "Quote Reference "+updateReportdetailsDOB.getQuoteId()+" was Rejected by Manager.  Please alter the quote as required.";
                  }
               
                  sendMail(appRejMailDetails[0],appRejMailDetails[1],subject,body,"",null);
                  if(appRejMailDetails[1]!=null && !appRejMailDetails[1].equalsIgnoreCase(appRejMailDetails[2]))
                    sendMail(appRejMailDetails[0],appRejMailDetails[2],subject,body,"",null);
                }
                //commented by subrahmanyam for 146463
                //errorMessageObject = new ErrorMessage("Successfully Updated the status","QMSReportController?Operation=Escalated"); 
              //added by subrahmanyam for 146463
                if("yes".equalsIgnoreCase(fromSummary))
                  errorMessageObject = new ErrorMessage("Successfully Updated the status","QMSReportController?Operation=ActivitySummary"); 
                else
                errorMessageObject = new ErrorMessage("Successfully Updated the status","QMSReportController?Operation=Escalated"); 
                /*keyValueList.add(new KeyValue("ErrorCode","RSU")); 	*/
                //@@Commented and Modified by kameswari for the WPBN issue-143523
                 keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                session.removeAttribute("HashList2");
                 session.removeAttribute("HashList1");
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doFileDispatch(request,response,"ESupplyErrorPage.jsp");
            }
          }   
        else if("html/excel".equalsIgnoreCase(subOperation))
        {
          if("Escalatedpagging".equalsIgnoreCase(nextOperation))
              {
                reportenterDob=new ReportsEnterIdDOB();
                reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
                dataList			    = (ArrayList)session.getAttribute("dataList");
                if(request.getParameter("PageNo")!=null)
                {
                String pageno=request.getParameter("PageNo");
                reportenterDob.setPageNo(Integer.parseInt(pageno)); 
                }
                reportenterDob.setSortBy(request.getParameter("SortBy"));
                reportenterDob.setSortOrder(request.getParameter("SortOrder"));
                doEscalatedPageValues(request,response,dataList,loginbean);
             }
             else if("EscalatedPageBrowse".equalsIgnoreCase(nextOperation))
              {
                reportenterDob=new ReportsEnterIdDOB();
                reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
                dataList			    = (ArrayList)session.getAttribute("dataList");
                if(request.getParameter("PageNo")!=null)
                {
                String pageno=request.getParameter("PageNo");
                reportenterDob.setPageNo(Integer.parseInt(pageno)); 
                }
                reportenterDob.setSortBy(request.getParameter("SortBy"));
                reportenterDob.setSortOrder(request.getParameter("SortOrder"));
                doEscalatedPageValues(request,response,dataList,loginbean);
             }
            else
              {
                reportenterDob=new ReportsEnterIdDOB();
                reportenterDob.setUserId(loginbean.getUserId());
                if(request.getParameter("radiobutton").equalsIgnoreCase("self"))
                {
                reportenterDob.setApprovedFlag("S");
                }
                else
                {
                //reportenterDob.setUserId("");
                reportenterDob.setApprovedFlag("O");
                }
                reportenterDob.setTerminalId(loginbean.getTerminalId());
                reportenterDob.setQuoteStatus("ESC");
                if (request.getParameter("noofrecords")!=null && !"".equals(request.getParameter("noofrecords")))
                {
                String noofrecs = request.getParameter("noofrecords");
                reportenterDob.setNoOfRecords(Integer.parseInt(noofrecs));
                }
                else
                {
                reportenterDob.setNoOfRecords(50);  
                }
                session.setAttribute("reportenterDob",reportenterDob);
                reportenterDob.setPageNo(1);
                reportenterDob.setSortBy("QuoteId");//@@Modified by Kameswari for the WPBN issue-38116
                reportenterDob.setSortOrder("ASC");
            //  reportenterDob.setNoOfRecords(10);
                
              }
          try
          {
          
              mainDataList  = new ArrayList();
              reportenterDob.setFormateSerch(request.getParameter("format"));
              home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
              remote	= (ReportsSession)home.create();
              mainDataList  = (ArrayList)  remote.getEscalatedQuoteReportDetails(reportenterDob);
              request.setAttribute("mainDataList",mainDataList);
 //             nextNavigation   = "reports/QuotesExpiryReports.jsp?Operation="+operation+"subOperation="+subOperation;

          }
          catch(Exception e)
          {
            e.printStackTrace();
          }
          session.setAttribute("Escalated",mainDataList);
          
          if( "Excel".equalsIgnoreCase(request.getParameter("format")))
          {         
             StringBuffer   sb            = null;
             String         shipmentMode  = "";
             if(mainDataList!=null && mainDataList.size()>0)
             {       		
                dataList		   =	(ArrayList)mainDataList.get(1);
    
                out  =  response.getWriter();
                response.setContentType("application/vnd.ms-excel");	
                String contentDisposition = " :attachment;";	
                response.setHeader("Content-Disposition","attachment;filename=Escalated Report.xls");
                
                sb   =   new StringBuffer("");
                sb.append("<html>");
                sb.append("<body>");
                sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");              
                sb.append("<tr align='center'>");
                sb.append("<td><b>IMPORTANT</b></td>");
                sb.append("<td><b>CUSTOMER ID</b></td>");
                sb.append("<td><b>CUSTOMER NAME</b></td>");//@@added by kameswari for the WPBN issue-30313
                sb.append("<td><b>QUOTE ID</b></td>");
                sb.append("<td><b>MODE</b></td>");
                sb.append("<td><b>SERVICE LEVEL</b></td>");
                sb.append("<td><b>FROM COUNTRY</b></td>");
                sb.append("<td><b>FROM LOCATION</b></td>");
                sb.append("<td><b>TO COUNTRY</b></td>");
                sb.append("<td><b>TO LOCATION</b></td>");
                sb.append("<td><b>INTERNAL NOTES</b></td>");
                sb.append("<td><b>EXTERNAL NOTES</b></td>");
                sb.append("</tr>");
                   int dListSize	= dataList.size();
                for(int i=0;i<dListSize;i++)
                {
                  detailsDOB  = (ReportDetailsDOB)dataList.get(i);
                  sb.append("<tr align='center'>");
                    detailsDOB  = (ReportDetailsDOB)dataList.get(i);
                    if("1".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                          shipmentMode  = "Air";
                    else if("2".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Sea";
                    else if("4".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Truck";
                    else if("100".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Multi-Modal";
                        
                    sb.append("<td>").append("I".equalsIgnoreCase(detailsDOB.getImportant())?"IMP":"").append("</td>");
                    sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>"); //@@added by kameswari for the WPBN issue-30313
                    sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                    sb.append("<td>").append(shipmentMode).append("</td>");
                    sb.append("<td>").append(detailsDOB.getServiceLevel()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getInternalRemarks()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getExternalRemarks()).append("</td>");
                    
                    sb.append("</tr>");
                }
                sb.append("</body>");
                sb.append("</html>");
                out.print(sb.toString());
             }
          }
          else
          {            
            if("EscalatedPageBrowse".equalsIgnoreCase(nextOperation)) 
             nextNavigation   =  "reports/QMSQuoteEscalatedReport.jsp?Operation="+operation+"&subOperation=html/excel&nextOperation=EscalatedPageBrowse&SortBy="+request.getParameter("SortBy")+"&SortOrder="+request.getParameter("SortOrder")+"&PageNo="+request.getParameter("PageNo")+"&radiobutton="+request.getParameter("radiobutton");
            else
             nextNavigation   =  "reports/QMSQuoteEscalatedReport.jsp?Operation="+operation+"&subOperation=html/excel&nextOperation=EscalatedPageBrowse&SortBy=QuoteId&SortOrder=ASC&PageNo=1&radiobutton="+request.getParameter("radiobutton");//@@Modified by Kameswari for the WPBN issue-38116
          }
        }
        else if("Submit".equalsIgnoreCase(subOperation))
        {
          //Insert into DB
        }
      }
      else if("pendingquotes".equalsIgnoreCase(operation))
      {
         
          if("pendingUpdate".equalsIgnoreCase(subOperation))
          {
        	  //reportenterDob=new ReportsEnterIdDOB();
        	  reportenterDob = null;
            dataList			    = (ArrayList)session.getAttribute("dataList");
              doEscalatedPageValues(request,response,dataList,loginbean);
            ArrayList    totUpdatedList = new ArrayList();
            if(session.getAttribute("HashList1")!=null)
            {
               updatedMapValue					=	(HashMap)session.getAttribute("HashList1");
               updateDataList=dogetFinalValues(request,response,updatedMapValue);
               datasize=updateDataList.size();
               for(int i=0;i<datasize;i++)
               {
               updateReportdetailsDOB =(ReportDetailsDOB)updateDataList.get(i);
               totUpdatedList.add(updateReportdetailsDOB);
               }
            }
            if(session.getAttribute("HashList2")!=null)
            {
               updatedMapValue					=	(HashMap)session.getAttribute("HashList2");
               updateDataList=dogetFinalValues(request,response,updatedMapValue);
               datasize=updateDataList.size();
               for(int i=0;i<datasize;i++)
               {
               updateReportdetailsDOB =(ReportDetailsDOB)updateDataList.get(i);
               totUpdatedList.add(updateReportdetailsDOB);
               } 
            }
            
            if(totUpdatedList!=null && totUpdatedList.size()>0)
            {
               home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
               remote                  =     (ReportsSession)home.create();
               updated=remote.updatePendingQuoteReport(totUpdatedList); 
            }
            if(updated!=null)
            {  
              if("yes".equalsIgnoreCase(fromSummary))
                errorMessageObject = new ErrorMessage("Successfully Updated the status","QMSReportController?Operation=ActivitySummary"); 
              else
                errorMessageObject = new ErrorMessage("Successfully Updated the status","reports/PendingQuotesEnterId.jsp"); //""reports/PendingQuotesEnterId.jsp" 
               // keyValueList.add(new KeyValue("ErrorCode","RSU")); 	
               keyValueList.add(new KeyValue("ErrorCode","RSM"));
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doFileDispatch(request,response,"ESupplyErrorPage.jsp");
            }
           }   
         
         else if("pendingquotespagging".equalsIgnoreCase(subOperation))
          {
          
             reportenterDob=new ReportsEnterIdDOB();
             reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
          
             dataList			    = (ArrayList)session.getAttribute("dataList");
          
             if(request.getParameter("pageNo")!=null)
             {
              String pageno=request.getParameter("pageNo");
              reportenterDob.setPageNo(Integer.parseInt(pageno)); 
             }        
              doEscalatedPageValues(request,response,dataList,loginbean);

          }
          else if("pageBrowse".equalsIgnoreCase(subOperation))
          {
            reportenterDob  =new ReportsEnterIdDOB();
            reportenterDob	= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");          
            dataList			  = (ArrayList)session.getAttribute("dataList");
          
            if(request.getParameter("PageNo")!=null)
             {
              String pageno=request.getParameter("PageNo");
              reportenterDob.setPageNo(Integer.parseInt(pageno)); 
             }
            reportenterDob.setSortBy(request.getParameter("SortBy"));
            reportenterDob.setSortOrder(request.getParameter("SortOrder"));
            doEscalatedPageValues(request,response,dataList,loginbean);
          }
          else
          {
          reportenterDob=new ReportsEnterIdDOB();
          reportenterDob.setShipmentMode((request.getParameter("shipmentMode")==null?"":request.getParameter("shipmentMode")));
          reportenterDob.setCustomerId(request.getParameter("customerId")==null?"":request.getParameter("customerId"));
          reportenterDob.setTerminalId(loginbean.getTerminalId());
          reportenterDob.setPrimaryOption(request.getParameter("wiseoptions")==null?"":request.getParameter("wiseoptions"));
          reportenterDob.setUserDateFormat(loginbean.getUserPreferences().getDateFormat());
          if(reportenterDob.getPrimaryOption().equals("W"))
          {
            datedate  = null;
            
            datedate  = request.getParameter("fromdate");
            datedate  = dateutil.getDisplayDateStringWithTime(dateutil.getTimestampWithTime(format, datedate, "00:00"),format);
            
            reportenterDob.setFromDate(datedate);
            
            datedate  = null;
            
            datedate  = request.getParameter("todate");
            datedate  = dateutil.getDisplayDateStringWithTime(dateutil.getTimestampWithTime(format, datedate,"23:59"),format);
            
            reportenterDob.setToDate(datedate);
            
            datedate  = null;
            
          }
          else if(reportenterDob.getPrimaryOption().equals("C"))
          {
            reportenterDob.setFromDate(request.getParameter("customerId")==null?"":request.getParameter("customerId"));
            reportenterDob.setToDate("");
          
          }
          else if(reportenterDob.getPrimaryOption().equals("L"))
          {
            reportenterDob.setFromDate(request.getParameter("originId")==null?"":request.getParameter("originId"));
            reportenterDob.setToDate(request.getParameter("destinationId")==null?"":request.getParameter("destinationId"));
            
          }else if(reportenterDob.getPrimaryOption().equals("SP"))
          {
            reportenterDob.setFromDate(request.getParameter("salesPersonCode")==null?"":request.getParameter("salesPersonCode"));
            reportenterDob.setToDate("");          
          }
          else if(reportenterDob.getPrimaryOption().equals("servicelevelwise"))
          {
            reportenterDob.setFromDate(request.getParameter("serviceLevelId")==null?"":request.getParameter("serviceLevelId"));
            reportenterDob.setToDate("");          
          }
          reportenterDob.setConsoleType(request.getParameter("conType")==null?"":request.getParameter("conType"));
          reportenterDob.setPageNo(1);
          if (request.getParameter("noofrecords")!=null && !"".equals(request.getParameter("noofrecords")))
          {
            String noofrecs = request.getParameter("noofrecords");
            reportenterDob.setNoOfRecords(Integer.parseInt(noofrecs));
          }
          else
          {
            reportenterDob.setNoOfRecords(50);
          }
          reportenterDob.setSortBy("QuoteId");//@@Modified by Kameswari for the WPBN issue-38116
          reportenterDob.setSortOrder("ASC");
          }
          
          
              try
              { 
            	if(reportenterDob==null)
            		reportenterDob = (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
            	
                reportenterDob.setFormateSerch(nextOperation);
                home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
                remote                  =     (ReportsSession)home.create();	          
                mainDataList=remote.getPendingQuoteReportDetails(reportenterDob);        
                request.setAttribute("mainDataList",mainDataList);          
              }
              catch(Exception	e)
              {
                e.printStackTrace();
              }
             
              
              if("Excel".equalsIgnoreCase(nextOperation))
              {
                  StringBuffer  sb    = null;
                  ESupplyDateUtility eSupplyDateUtil = new  ESupplyDateUtility();//@@Added by kameswari for the WPBN issue-61310
                    eSupplyDateUtil.setPattern(loginbean.getUserPreferences().getDateFormat());//@@Added by kameswari for the WPBN issue-61310
                  if(mainDataList!=null && mainDataList.size()>0)
    			        {       		
                      dataList		   =	(ArrayList)mainDataList.get(1);
                        
                      out  =  response.getWriter();
                      response.setContentType("application/vnd.ms-excel");	
                      String contentDisposition = " :attachment;";	
                      response.setHeader("Content-Disposition","attachment;filename=PendingReport.xls");
                      
                      sb   =   new StringBuffer("");
                      sb.append("<html>");
                      sb.append("<body>");
                      sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                      
                      
                      sb.append("<tr align='center'>");
                      sb.append("<td><b>IMPORTANT</b></td>");
                      sb.append("<td><b>QUOTE REFERENCE</b></td>");
                      sb.append("<td><b>CREATED DATE</b></td>");//@@Added by kameswari for the WPBN issue-61310
                      sb.append("<td><b>CUSTOMER ID</b></td>");
                      sb.append("<td><b>CUSTOMER NAME</b></td>");//@@added by kameswari for the WPBN issue-30313
                      sb.append("<td><b>SERVICE LEVEL</b></td>");
                      sb.append("<td><b>FROM COUNTRY</b></td>");
                      sb.append("<td><b>FROM LOCATION</b></td>");
                      sb.append("<td><b>TO COUNTRY</b></td>");
                      sb.append("<td><b>TO LOCATION</b></td>");
                      sb.append("</tr>");
                      int dListSize	= dataList.size();
                      for(int i=0;i<dListSize;i++)
                      {
                          detailsDOB  = (ReportDetailsDOB)dataList.get(i);
                          sb.append("<tr align='center'>");
                          sb.append("<td>").append(((detailsDOB.getImportant()).equalsIgnoreCase("I"))?detailsDOB.getImportant():"").append("</td>");//@@Modified by kameswari
                          sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                          sb.append("<td>").append(eSupplyDateUtil.getDisplayString(detailsDOB.getCreateDate())).append("</td>");//@@Added by kameswari for the WPBN issue-61310
                          sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");
                          sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>"); //@@added by kameswari for the WPBN issue-30313
                          sb.append("<td>").append(detailsDOB.getServiceLevel()).append("</td>");
                          sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                          sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                          sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                          sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                          sb.append("</tr>");
                      }
                        
                        sb.append("</body>");
                        sb.append("</html>");
                        out.print(sb.toString());
                  }
              }
              else
              {
                 session.setAttribute("reportenterDob",reportenterDob);
                 //nextNavigation   = "reports/PendingQuotesReports.jsp";
                 if("Html".equalsIgnoreCase(subOperation))
                  nextNavigation   = "reports/PendingQuotesReports.jsp?Operation="+operation+"&subOperation="+subOperation+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";//@@Modified by Kameswari for the WPBN issue-38116
                 else if("pageBrowse".equalsIgnoreCase(subOperation))
                  {
                    int         pageNo       =   Integer.parseInt(request.getParameter("PageNo"));
                    String      sortBy       =   request.getParameter("SortBy");
                    String      sortOrder    =   request.getParameter("SortOrder");
                    String      repFormat    =   request.getParameter("format");
                    nextNavigation   = "reports/PendingQuotesReports.jsp?Operation="+operation+"&subOperation=Html&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo+"&format="+repFormat;
                  }
                else
                  nextNavigation   = "reports/PendingQuotesReports.jsp?Operation="+operation+"&subOperation="+subOperation;
              }
   }   
  else if("quotesExpiryReport".equalsIgnoreCase(operation))
  {
        nextNavigation   = "reports/QuotesExpiryEnterId.jsp?Operation="+operation;
  }
  else if("expiryquotes".equalsIgnoreCase(operation))
	{
     if("expiryQuotesUpdate".equalsIgnoreCase(subOperation))
     {
      dataList			    = (ArrayList)session.getAttribute("dataList");
      doPageValues(request,response,dataList);
      if(session.getAttribute("HashList")!=null)
       {
          updatedMapValue					=	    (HashMap)session.getAttribute("HashList");
          updateDataList          =     dogetFinalValues(request,response,updatedMapValue);
          home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
          remote                  =     (ReportsSession)home.create();
          updated                 =     remote.updateQuoteExpiryReport(updateDataList);            
		   }
      if(updated!=null)
       {  
            session.removeAttribute("HashList");
            session.removeAttribute("reportenterDob");
            session.removeAttribute("dataList");
          //commented by subrahmanyam for 146463
            //errorMessageObject = new ErrorMessage("Successfully Updated the status","QMSReportController?Operation=quotesExpiryReport"); 
            //added by subrahmanyam for 146463
            if("yes".equalsIgnoreCase(fromSummary))
              errorMessageObject = new ErrorMessage("Successfully Updated the status","QMSReportController?Operation=ActivitySummary"); 
            else
            errorMessageObject = new ErrorMessage("Successfully Updated the status","QMSReportController?Operation=quotesExpiryReport"); 
          //  keyValueList.add(new KeyValue("ErrorCode","RSU")); 
           keyValueList.add(new KeyValue("ErrorCode","RSM")); 
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject);
            doFileDispatch(request,response,"ESupplyErrorPage.jsp");
       }
     }    
     /*else if("quoteratespagging".equalsIgnoreCase(subOperation))
     {
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob		= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
        dataList			    = (ArrayList)session.getAttribute("dataList");
        if(request.getParameter("pageNo")!=null)
        {
        String pageno=request.getParameter("pageNo");
        reportenterDob.setPageNo(Integer.parseInt(pageno)); 
        }       
        doPageValues(request,response,dataList);
     }*/
     else if("pageBrowse".equalsIgnoreCase(subOperation))
     {
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob		= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
        dataList			    = (ArrayList)session.getAttribute("dataList");
        if(request.getParameter("PageNo")!=null)
        {
        String pageno=request.getParameter("PageNo");
        reportenterDob.setPageNo(Integer.parseInt(pageno)); 
        }
        reportenterDob.setSortBy(request.getParameter("SortBy"));
        reportenterDob.setSortOrder(request.getParameter("SortOrder"));
        doPageValues(request,response,dataList);
     }
     else
     {
        //Date d1=new Date(1);
        //System.out.println("d1d1d1d1---date=----- "+new java.sql.Timestamp((new java.util.Date()).getTime()));
        
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob.setShipmentMode(request.getParameter("shipmentMode"));
		String custId="";
		if(request.getParameter("customerId")==null)
		{
			custId="";
		}
		else
		{
			custId=request.getParameter("customerId");
			reportenterDob.setCustomerId(custId.replaceAll(",","~"));
		}
		//Added by Rakesh on 22-03-2011
		if(request.getParameter("fromDate")!=null){
        	reportenterDob.setFromDate(request.getParameter("fromDate"));
        }
        if(request.getParameter("toDate")!=null){
        	reportenterDob.setToDate(request.getParameter("toDate"));
        }
        //Ended by Rakesh on 22-03-2011
        
        String serviceLevel=request.getParameter("serviceLevelId");
		
		if(serviceLevel!=null)
		 {
		    reportenterDob.setServiceLevel(serviceLevel.replaceAll(",","~"));
		 }
		 else
		 {
			reportenterDob.setServiceLevel("");
		 }
        String termianlId=loginbean.getTerminalId();
        reportenterDob.setTerminalId(termianlId);
        reportenterDob.setDateFormat(loginbean.getUserPreferences().getDateFormat());
        reportenterDob.setExpiryActiveIndicator(request.getParameter("expDate"));
        reportenterDob.setExpiryActivePeriod(request.getParameter("expperiod"));
		
		if(request.getParameter("basis")==null)
	        reportenterDob.setBasis("");
		else
			reportenterDob.setBasis(request.getParameter("basis"));

        String past=request.getParameter("past");
        reportenterDob.setSalesPersonId(request.getParameter("salesPersonCode"));//Included by Shyam for DHL on 3/7/2006
        
		if(past!=null)
			reportenterDob.setBasisCount(Integer.parseInt(past));
		
        reportenterDob.setPageNo(1);
        if (request.getParameter("noofrecords")!=null && !"".equals(request.getParameter("noofrecords")))
        {
           String noofrecs = request.getParameter("noofrecords");
           reportenterDob.setNoOfRecords(Integer.parseInt(noofrecs));
        }
        else
        {
           reportenterDob.setNoOfRecords(20);  
        }
		reportenterDob.setUserId(loginbean.getUserId());//Included by Shyam for DHL on 3/8/2006
        reportenterDob.setSortBy("QuoteId");//@@Modified by Kameswari for the WPBN issue-38116
        reportenterDob.setSortOrder("ASC");
		
        dateutil  = new  ESupplyDateUtility();
        
        dateutil.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
        
		
		if(request.getParameter("basis")!=null)
		 {
			 reportenterDob.setCurrentDate(dateutil.getCurrentDateString(loginbean.getUserPreferences().getDateFormat()));
			 reportenterDob = setFromdateAndToDate(reportenterDob);
		 }
		 else
		 {
			 reportenterDob.setToDate1(new java.sql.Timestamp((new java.util.Date()).getTime()));

       //reportenterDob.setToDate(dateutil.getDisplayString(reportenterDob.getToDate1())+"HH24:MI");

			 if(request.getParameter("toDate")==null)//Added by Rakesh on 29-03-2011
			 reportenterDob.setToDate(dateutil.getDisplayDateStringWithTime(reportenterDob.getToDate1(),loginbean.getUserPreferences().getDateFormat()));

		 }
        
		
        
       }
        try
        { 
          reportenterDob.setFormateSerch(subOperation);
          home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
          remote                  =     (ReportsSession)home.create();
         
          mainDataList  =  new ArrayList();
          mainDataList=remote.getQuoteExpiryReport(reportenterDob);
          request.setAttribute("mainDataList",mainDataList);
          session.setAttribute("reportenterDob",reportenterDob);
          if(subOperation.equalsIgnoreCase("html"))
           nextNavigation   = "reports/QuotesExpiryReports.jsp?Operation="+operation+"&subOperation="+subOperation+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";
          else if(subOperation.equalsIgnoreCase("pageBrowse"))
          {
            int         pageNo       =   Integer.parseInt(request.getParameter("PageNo"));
            String      sortBy       =   request.getParameter("SortBy");
            String      sortOrder    =   request.getParameter("SortOrder");
            String      repFormat    =   request.getParameter("format");
            nextNavigation   = "reports/QuotesExpiryReports.jsp?Operation="+operation+"&subOperation=html&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo+"&format="+repFormat;
          }
          else
           nextNavigation   = "reports/QuotesExpiryReports.jsp?Operation="+operation+"&subOperation="+subOperation;
          
        }
        catch(Exception	e)
        {
          e.printStackTrace();
          
        }
        
        if("Excel".equalsIgnoreCase(subOperation))
        {
          nextNavigation    = null;
          StringBuffer sb   = null;  
          
          if(mainDataList!=null && mainDataList.size()>0)
          {       		

        	  dataList		   =	(ArrayList)mainDataList.get(1);
              ESupplyDateUtility eSupplyDateUtil = new  ESupplyDateUtility();//added by silpa on 29-04-2011
              eSupplyDateUtil.setPattern("DD-MON-YY");//added by silpa 0n 29-04-2011
              out  =  response.getWriter();
              response.setContentType("application/vnd.ms-excel");	
              String contentDisposition = " :attachment;";	
              response.setHeader("Content-Disposition","attachment;filename=QuoteExpiryReport.xls");
              sb    =   new StringBuffer("");
              sb.append("<html>");
              sb.append("<body>");
              sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");              
              sb.append("<tr align='center'>");
              //sb.append("<td><b>IMPORTANT</b></td>");//modified by silpa on 29-04-2011
              sb.append("<td><b>CUSTOMER ID</b></td>");
              sb.append("<td><b>CUSTOMER NAME</b></td>");//@@ added by kameswari for the WPBN issue-30313
              sb.append("<td><b>QUOTE ID</b></td>");
              sb.append("<td><b>SERVICE LEVEL</b></td>");
              sb.append("<td><b>FROM COUNTRY</b></td>");
              sb.append("<td><b>FROM LOCATION</b></td>");
              sb.append("<td><b>TO COUNTRY</b></td>");
              sb.append("<td><b>TO LOCATION</b></td>");
              sb.append("<td><b>SHIPMENT MODE</b></td>");//added silpa on 29-04-2011
              sb.append("<td><b>CREATED DATE</b></td>");
              sb.append("<td><b>VALID TO DATE</b></td>");
              sb.append("<td><b>QUOTE STATUS</b></td>");//ended
              sb.append("</tr>");
              int dListSize	= dataList.size();
              for(int i=0;i<dListSize;i++)
              {
            	  String  shipmentMode= "";//added by silpa on 29-04-20112
                  detailsDOB  = (ReportDetailsDOB)dataList.get(i);
              //added by silpa on 29-04-2011
                  if("1".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                      shipmentMode  = "Air";
                else if("2".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                    shipmentMode  = "Sea";
                else if("4".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                    shipmentMode  = "Truck";
                else if("100".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                    shipmentMode  = "Multi-Modal";//ended
                                       
                  //sb.append("<td>").append("I".equalsIgnoreCase(detailsDOB.getImportant())?"IMP":"").append("</td>");//modified by silpa on 29-04-2011
                  sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>");//@@added by kameswari for the WPBN issue-30313
                  sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getServiceLevel()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                  sb.append("<td>").append(shipmentMode).append("</td>");//added by silpa on 29-04-2011
                  sb.append("<td>").append(detailsDOB.getCreateDateStr()).append("</td>");//added by silpa on 29-04-2011
                  //sb.append("<td>").append(detailsDOB.getCreateDateStr()!=null?eSupplyDateUtil.getDisplayString(detailsDOB.getCreateDateStr()):"").append("</td>");
                  sb.append("<td>").append(detailsDOB.getValidUpto()!=null?eSupplyDateUtil.getDisplayString(detailsDOB.getValidUpto()):"").append("</td>");//added by silpa on 29-04-2011
                  sb.append("<td>").append(detailsDOB.getQuoteStatus()).append("</td>");//added by silpa on 29-04-2011
                  sb.append("</tr>");
              }
              sb.append("</table>");
              sb.append("</body>");
              sb.append("</html>");
              out.print(sb);
          }
        }
    
	}
  else if("buyratesExpiryReport".equalsIgnoreCase(operation))
  {
      nextNavigation   = "reports/QuotesExpiryEnterId.jsp?Operation="+operation;
  }
  else if("buyratesexpiry".equalsIgnoreCase(operation))
  {
     
      if("buyratespagging".equalsIgnoreCase(subOperation))
      {
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
        if(request.getParameter("pageNo")!=null)
        {
        String pageno=request.getParameter("pageNo");
        reportenterDob.setPageNo(Integer.parseInt(pageno)); 
        }        
      }
      else if("BuyRatesPageBrowse".equalsIgnoreCase(subOperation))
      {
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
        if(request.getParameter("PageNo")!=null)
        {
        String pageno=request.getParameter("PageNo");
        reportenterDob.setPageNo(Integer.parseInt(pageno)); 
        }        
        reportenterDob.setSortBy(request.getParameter("SortBy"));
        reportenterDob.setSortOrder(request.getParameter("SortOrder"));
      }//Added by Rakesh for Issue:          on 09-03-2011
      else if("Excel".equalsIgnoreCase(subOperation))
      {
    	  doDownLoadProcess( request,response,loginbean);
    	 /* Timestamp fromDate = null;
    	  Timestamp toDate = null;
    	  fromDate = new ESupplyDateUtility().getTimestamp("DD/MM/YY",request.getParameter("fromDate"));  
    	  toDate   = new ESupplyDateUtility().getTimestamp("DD/MM/YY",request.getParameter("toDate"));  
    	  reportenterDob=new ReportsEnterIdDOB();
          
          reportenterDob.setShipmentMode(request.getParameter("shipmentMode"));
          
          reportenterDob.setWeight_break(request.getParameter("weightBreak"));
          reportenterDob.setRate_type(request.getParameter("ratetype"));
          
          reportenterDob.setFromDate1(fromDate);
          reportenterDob.setToDate1(toDate);
          reportenterDob.setPageNo(1);
          reportenterDob.setSortBy("FromCountry");
          reportenterDob.setSortOrder("ASC");*/
       
      }//Ended by Rakesh for Issue:          on 09-03-2011
      else
      {
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob.setDateFormat(loginbean.getUserPreferences().getDateFormat());
        reportenterDob.setShipmentMode(request.getParameter("shipmentMode"));
        reportenterDob.setCarrierId(request.getParameter("carrierId"));
        reportenterDob.setServiceLevel(request.getParameter("serviceLevelId"));
        reportenterDob.setExpiryActiveIndicator(request.getParameter("expDate"));
        reportenterDob.setExpiryActivePeriod(request.getParameter("expperiod"));
        reportenterDob.setBasis(request.getParameter("basis"));
        String past=request.getParameter("past");
        reportenterDob.setBasisCount(Integer.parseInt(past));
        reportenterDob.setExpiryActiveIndicator(request.getParameter("expDate"));        
        if (request.getParameter("noofrecords")!=null && !"".equals(request.getParameter("noofrecords")))
          {
            String noofrecs = request.getParameter("noofrecords");
            reportenterDob.setNoOfRecords(Integer.parseInt(noofrecs));
          }
        else
          {
            reportenterDob.setNoOfRecords(50);  
          }
        reportenterDob.setCurrentDate(new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY"));
        reportenterDob = setFromdateAndToDate(reportenterDob);
        
        reportenterDob.setPageNo(1);
        reportenterDob.setSortBy("FromCountry");
        reportenterDob.setSortOrder("ASC");
      }
      try
      { 
    	  if(!"Excel".equalsIgnoreCase(subOperation)){
    	  reportenterDob.setFormateSerch(subOperation);
        home                     =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
        remote                   =     (ReportsSession)home.create();
        //mainDataList  =  new ArrayList();
        
        mainDataList=remote.getBuyRatesExpiryReport(reportenterDob);
        request.setAttribute("mainDataList",mainDataList);
        session.setAttribute("reportenterDob",reportenterDob);
      }
      }
      catch(Exception	e)
      {
      e.printStackTrace();
      
      logger.error(FILE_NAME+"Exception while calling remote method"+ e.toString());
      }
      if("BuyRatesPageBrowse".equalsIgnoreCase(subOperation))
      {
        int         pageNo       =   Integer.parseInt(request.getParameter("PageNo"));
        String      sortBy       =   request.getParameter("SortBy");
        String      sortOrder    =   request.getParameter("SortOrder");
        nextNavigation   = "reports/BuyRatesExpiryReport.jsp?Operation="+operation+"&subOperation="+subOperation+"&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
      }
      else
        nextNavigation   = "reports/BuyRatesExpiryReport.jsp?Operation="+operation+"&subOperation="+subOperation+"&SortBy=FromCountry&SortOrder=ASC&PageNo=1";
      if("Excel".equalsIgnoreCase(subOperation))
      {
              nextNavigation                     =  null;
              /* StringBuffer       sb              =  null;
              ESupplyDateUtility eSupplyDateUtil = new  ESupplyDateUtility();
              
              eSupplyDateUtil.setPattern(loginbean.getUserPreferences().getDateFormat());
              
              if(mainDataList!=null && mainDataList.size()>0)
              {       		
                  dataList		   =	(ArrayList)mainDataList.get(1);

                  out  =  response.getWriter();
                  response.setContentType("application/vnd.ms-excel");	
                  String contentDisposition = " :attachment;";	
                  response.setHeader("Content-Disposition","attachment;filename=BuyRatesExpiry.xls");
                  
                  sb    =   new StringBuffer("");
                  sb.append("<html>");
                  sb.append("<body>");
                  sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");              
                  sb.append("<tr align='center'>");
                  
                  sb.append("<td><b>FROM COUNTRY</b></td>");
                  sb.append("<td><b>FROM LOCATION</b></td>");
                  sb.append("<td><b>TO COUNTRY</b></td>");
                  sb.append("<td><b>TO LOCATION</b></td>"); 
                  sb.append("<td><b>ORIGIN</b></td>");
                  sb.append("<td><b>DESTINATION</b></td>");
                  sb.append("<td><b>CARRIER</b></td>");
                  sb.append("<td><b>SERVICE LEVEL</b></td>");
                  sb.append("<td><b>FREQUENCY</b></td>");
                  sb.append("<td><b>EFFECTIVE FROM</b></td>");
                  sb.append("<td><b>VALID UPTO</b></td>");
                  sb.append("<td><b>CURRENCY</b></td>");
                  sb.append("<td><b>CREATED DATE</b></td>");//@@Added by Kameswari for the WPBN issue-61310
                  sb.append("<td><b>EXPIRY WITHIN</b></td>");
                  sb.append("<td><b>NOTES</b></td>");
                  sb.append("<td><b>TERMINAL ID</b></td>");
                  
                  sb.append("</tr>");
                  int dListSize	=	dataList.size();
                 for(int i=0;i<dListSize;i++)
                 {
                    detailsDOB  = (ReportDetailsDOB)dataList.get(i);
                    //Commented by Rakesh for Issue:                    
                    sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                    //Comment Ended by Rakesh for Issue:
                    sb.append("<td>").append(detailsDOB.getFromCountry()).append(detailsDOB.getFromLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToCountry()).append(detailsDOB.getToLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getCarrierId()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getServiceLevel()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFrequency()!=null?detailsDOB.getFrequency():"").append("</td>");
                    sb.append("<td>").append(detailsDOB.getEffectiveFrom()!=null?eSupplyDateUtil.getDisplayString(detailsDOB.getEffectiveFrom()):"").append("</td>");
                    sb.append("<td>").append(detailsDOB.getValidUpto()!=null?eSupplyDateUtil.getDisplayString(detailsDOB.getValidUpto()):"").append("</td>");
                    sb.append("<td>").append(detailsDOB.getCurrency()!=null?detailsDOB.getCurrency():"").append("</td>");
                    sb.append("<td>").append(detailsDOB.getCreateDate()!=null?eSupplyDateUtil.getDisplayString(detailsDOB.getCreateDate()):"").append("</td>");//@@Added by Kameswari for the WPBN issue-61310
                    sb.append("<td>").append(detailsDOB.getExpiryinDays()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getNotes()!=null?detailsDOB.getNotes():"").append("</td>");
                    sb.append("<td>").append(detailsDOB.getTerminalId()!=null?detailsDOB.getTerminalId():"").append("</td>");
                    sb.append("</tr>");
                 }
                 sb.append("</table>");
                 sb.append("</body>");
                 sb.append("</html>");
                 out.print(sb);
              }
      */}
  }
 /* else if("s".equalsIgnoreCase(operation))
  {
      nextNavigation   = "reports/QuotesExpiryEnterId.jsp?Operation="+operation;
  }
  else if("buyratesexpiry".equalsIgnoreCase(operation))
  {
      if("Excel".equalsIgnoreCase(subOperation))
      {
        out  =  response.getWriter();
        response.setContentType("application/vnd.ms-excel");	
        String contentDisposition = " :attachment;";	
        response.setHeader("Content-Disposition","attachment;filename=RejectReport.xls");
        out.println("hello\t");
        out.print("dhafdh\t");
        out.print("dfaljfa\t");        
      }
      else if("buyratespagging".equalsIgnoreCase(subOperation))
      {
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
        if(request.getParameter("pageNo")!=null)
        {
        String pageno=request.getParameter("pageNo");
        reportenterDob.setPageNo(Integer.parseInt(pageno)); 
        }        
      }
      else
      {
        reportenterDob=new ReportsEnterIdDOB();
        reportenterDob.setShipmentMode(request.getParameter("shipmentMode"));
        String carrier=request.getParameter("carrierId");
        reportenterDob.setCarrierId(carrier);
        String serviceLevel=request.getParameter("serviceLevelId");
        reportenterDob.setServiceLevel(serviceLevel);
        reportenterDob.setExpiryActiveIndicator(request.getParameter("expDate"));
        reportenterDob.setExpiryActivePeriod(request.getParameter("expperiod"));
        reportenterDob.setBasis(request.getParameter("basis"));
        String past=request.getParameter("past");
        reportenterDob.setBasisCount(Integer.parseInt(past));
        reportenterDob.setExpiryActiveIndicator(request.getParameter("expDate"));
        reportenterDob.setPageNo(1);
        if (request.getParameter("noofrecords")!=null && !"".equals(request.getParameter("noofrecords")))
           {
           String noofrecs = request.getParameter("noofrecords");
           reportenterDob.setNoOfRecords(Integer.parseInt(noofrecs));
           }
      }
      try
      { 
//			InitialContext initial					        =	    new InitialContext();	
       
        home                     =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
        remote                   =     (ReportsSession)home.create();
        mainDataList  =  new ArrayList();
        mainDataList=remote.getBuyRatesExpiryReport(reportenterDob);
        request.setAttribute("mainDataList",mainDataList);
        session.setAttribute("reportenterDob",reportenterDob);
      }
      catch(Exception	e)
      {
      e.printStackTrace();
      //Logger.error(fileName,"Exception while calling remote method", e.toString());
      }
  
	  nextNavigation   = "reports/BuyRatesExpiryReport.jsp?Operation="+operation+"subOperation="+subOperation ;
  }
  */

    if("yieldAdd".equals(operation))
    {    
      if("details".equals(subOperation))
      {
        String      quoteId = request.getParameter("QuoteId");
        ArrayList   detailsList = null;
        if( quoteId!=null && !"".equals(quoteId) )
        {
          home                     =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
          remote                   =     (ReportsSession)home.create();     
          
          detailsList = remote.getYieldReportDetailsLegs(quoteId);
          request.setAttribute("yielddetailsList",detailsList);
          
          nextNavigation  = "reports/QMSYieldReportDetails.jsp";
        }        
      }else
      {
        nextNavigation  = "reports/QMSYieldReportPreInfo.jsp";
      }
    }
    else if("activityAdd".equals(operation))
    {
      nextNavigation  = "reports/QMSActivityReportPreInfo.jsp";
    }     
    else if("getActivityDetails".equals(operation))
    { 
        String[] terminalIds  =null;
        String[] salesPerIds  =null;
        String[] customerIds  =null;
        String[] fromCountry  =null;
        String[] fromLocation =null;
        String[] toCountry    =null;
        String[] toLocation   =null;
        String[] serviceLevels=null;
        String[] quoteStatuses=null;
        ESupplyDateUtility formatter = new ESupplyDateUtility();
        String   fromDate     =null;
        String   toDate       =null;
        StringBuffer appTerm  =new StringBuffer("");
        StringBuffer appSales =new StringBuffer(""); 
        StringBuffer appCusto =new StringBuffer(""); 
        StringBuffer appFromCo=new StringBuffer(""); 
        StringBuffer appFromLo=new StringBuffer(""); 
        StringBuffer appToCo  =new StringBuffer(""); 
        StringBuffer appToLo  =new StringBuffer(""); 
        StringBuffer appServ  =new StringBuffer(""); 
        StringBuffer appSta   =new StringBuffer(""); 
        String       shMode   =null;
        String       autoUpdated    = null;//@@Added by VLAKSHMI for the WPBN issue-154390
        String       quoteStatus  = "";
        String       str[]      =null; 
        String       str1[]    		=null;//Added by Rakesh on 22-02-2011 for Issue:236359
        String       validTo[]      =null;//Added by Rakesh on 22-02-2011 for Issue:236359
        String       custDate[]     =null; //Added by Rakesh on 22-02-2011 for Issue:236359
        
        terminalIds  = request.getParameterValues("terminalId"); 
        salesPerIds  = request.getParameterValues("salesPersonCode"); 
        customerIds  = request.getParameterValues("customerId"); 
        fromCountry  = request.getParameterValues("fromCountry"); 
        fromLocation = request.getParameterValues("fromLocation"); 
        toCountry    = request.getParameterValues("toCountry"); 
        toLocation   = request.getParameterValues("toLocation"); 
        serviceLevels= request.getParameterValues("serviceLevelId"); 
        quoteStatuses= request.getParameterValues("quoteStatus");
       //@@Added by VLAKSHMI for the WPBN issue-154390
        autoUpdated       = request.getParameter("autoUpdated")!=null?request.getParameter("autoUpdated"):"";
        //@@WPBN issue-154390
        //fromDate = formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("fromDate"),"00:00:01");
        
       // toDate = formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("toDate"),"23:59:59").toString();
        
        //toDate     = request.getParameter("fromDate");
        //System.out.println("fromDate"+fromDate);
        //System.out.println("toDate"+toDate);
        
        
        //toDate       = request.getParameter("toDate");
        shMode       = request.getParameter("shipmentMode")!=null?request.getParameter("shipmentMode"):"";
        
       
        if(request.getParameterValues("terminalId")!=null)
        {
        	int termIdLen	=	terminalIds.length;
        for(int i=0;i<termIdLen;i++)
         appTerm.append("'").append(terminalIds[i]).append("',");
        
        appTerm.append(appTerm.substring(0,appTerm.length()-1));
        }
        if(request.getParameterValues("salesPersonCode")!=null)
        {
        	int salesPerIdLen	=	salesPerIds.length;
        for(int j=0;j<salesPerIdLen;j++)
         appSales.append("'").append(salesPerIds[j]).append("',");
        
        appSales.append(appSales.substring(0,appSales.length()-1));
        }
        //@@Modified by Kameswari for the WPBN issue- on 07/01/09
       /*if(request.getParameterValues("customerId")!=null&&request.getParameterValues("customerId").toString().trim().length()>0)
        {
        for(int k=0;k<customerIds.length;k++)
         appCusto.append("'").append(customerIds[k]).append("',");
        
        appCusto.append(appCusto.substring(0,appCusto.length()-1));
        }*/
        if(request.getParameter("customerId")!=null&&request.getParameter("customerId").trim().length()>0)
        {
            appCusto.append("'").append(request.getParameter("customerId")).append("'");
        }
        if(request.getParameterValues("fromCountry")!=null)
        {
        	int froCntrLen	=	fromCountry.length;
        for(int l=0;l<froCntrLen;l++)
         appFromCo.append("'").append(fromCountry[l]).append("',");
        
        appFromCo.append(appFromCo.substring(0,appFromCo.length()-1));
        }
        if(request.getParameterValues("fromLocation")!=null)
        {
        	int fromLocLen	=	fromLocation.length;
        for(int m=0;m<fromLocLen;m++)
         appFromLo.append("'").append(fromLocation[m]).append("',");
        
        appFromLo.append(appFromLo.substring(0,appFromLo.length()-1));
        }
        if(request.getParameterValues("toCountry")!=null)
        {
        	int toCntryLen	= toCountry.length;
        for(int n=0;n<toCntryLen;n++)
         appToCo.append("'").append(toCountry[n]).append("',");
        
        appToCo.append(appToCo.substring(0,appToCo.length()-1));
        }
        if(request.getParameterValues("toLocation")!=null)
        {
        	int toLocLen	= toLocation.length;
        for(int o=0;o<toLocLen;o++)
         appToLo.append("'").append(toLocation[o]).append("',");
        
        appToLo.append(appToLo.substring(0,appToLo.length()-1));
        }
        if(request.getParameterValues("serviceLevelId")!=null)
        {
        	int srvLevelLen	=	serviceLevels.length;
        for(int i=0;i<srvLevelLen;i++)
         appServ.append("'").append(serviceLevels[i]).append("',");
         
         appServ.append(appServ.substring(0,appServ.length()-1));
        }
        if(request.getParameterValues("quoteStatus")!=null)
        {
        	int qStatusUseLen	= quoteStatuses.length;
          for(int i=0;i<qStatusUseLen;i++)
          {
            if((i+1)==quoteStatuses.length)
                quoteStatus  = quoteStatus + "'"+quoteStatuses[i]+"'";
            else        
              quoteStatus  = quoteStatus + "'"+quoteStatuses[i]+"',";
          }
         //appSta.append("'").append(quoteStatuses[i]).append("',");
         
         appSta.append(quoteStatus);
        }
        reportenterDob=new ReportsEnterIdDOB();
        
        //reportenterDob.setFromDate1(formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("fromDate"),"00:00:01"));
        //reportenterDob.setToDate1(formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("toDate"),"23:59:59"));
        
        reportenterDob.setFromDate(request.getParameter("fromDate")+" 00:00:01");
        reportenterDob.setToDate(request.getParameter("toDate")+" 23:59:59");
        reportenterDob.setDateFormat(loginbean.getUserPreferences().getDateFormat());
        
        reportenterDob.setShipmentMode(shMode);
        
        
        reportenterDob.setTerminalId(appTerm.toString());
        reportenterDob.setCustomerId(appCusto.toString());
        reportenterDob.setSalesPersonId(appSales.toString());
        reportenterDob.setFromCountry(appFromCo.toString());
        reportenterDob.setOrginLocation(appFromLo.toString());
        reportenterDob.setToCountry(appToCo.toString());
        reportenterDob.setDestLocation(appToLo.toString());
        reportenterDob.setServiceLevel(appServ.toString());
        reportenterDob.setQuoteStatus(appSta.toString());
        reportenterDob.setAutoUpdated(autoUpdated);//@@Added by VLAKSHMI for the WPBN issue-154390
        reportenterDob.setLoginTerminal(loginbean.getTerminalId());//@@Added by Kameswari for the WPBN issue-71825
        session.setAttribute("ActivityEnterData",reportenterDob);
                       
        reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
        reportenterDob.setPageNo(1);
        reportenterDob.setSortBy("QuoteId");//@@Modified by Kameswari for the WPBN issue-38116
        reportenterDob.setSortOrder("ASC");
        try
        {
          reportenterDob.setFormateSerch(request.getParameter("format"));
          home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
          remote                  =     (ReportsSession)home.create();	          
          mainDataList            =      remote.getActivityReportDetails(reportenterDob);          
        }catch(Exception	e)
        {
         e.printStackTrace();
        }
        if("html".equals(request.getParameter("format")))
        {
          request.setAttribute("mainDataList",mainDataList); 
          nextNavigation  = "reports/QMSActivityReport.jsp?Operation="+operation+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";
          //nextNavigation  = "reports/QMSActivityReport.jsp";
        }
        else
        {
          StringBuffer sb    =  null;
          if(mainDataList!=null && mainDataList.size()>0)
          {       		
              dataList		   =	(ArrayList)mainDataList.get(1);

              out  =  response.getWriter();
              response.setContentType("application/vnd.ms-excel");	
              String contentDisposition = " :attachment;";	
              response.setHeader("Content-Disposition","attachment;filename=ActivityReport.xls");
              
              sb   =   new StringBuffer("");
              sb.append("<html>");
              sb.append("<body>");
              sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
              
              
              sb.append("<tr align='center'>");
              sb.append("<td><b>CREATED BY</b></td>");//@@ Added by subrahmanyam for wpbn issue: 173831
              sb.append("<td><b>SALES PERSON</b></td>");
              //Commented and Modified by Rakesh on 22-02-2011 for Issue:236359
              //sb.append("<td><b>QUOTEDATE</b></td>");
              sb.append("<td><b>CREATED DATE</b></td>");
              sb.append("<td><b>CREATED TIME</b></td>");
              sb.append("<td><b>MODIFY DATE</b></td>");
              sb.append("<td><b>MODIFY TIME</b></td>");
              sb.append("<td><b>VALID TO</b></td>");
              sb.append("<td><b>CUSTOMER REQUESTED DATE</b></td>");              
              sb.append("<td><b>CUSTOMER REQUESTED TIME</b></td>");//Added by Rakesh on 22-03-2011
              //Ended by Rakesh on 22-02-2011 for Issue:236359 
              sb.append("<td><b>QUOTE REFERENCE</b></td>");
              sb.append("<td><b>CUSTOMER ID</b></td>");
              sb.append("<td><b>CUSTOMER NAME</b></td>");//added by kameswari for the WPBN issue-30313
              //sb.append("<td><b>QUOTE STATUS</b></td>");//modified by silpa on 29-04-2011
              sb.append("<td><b>INCO TERMS</b></td>");//Added by Rakesh on 22-02-2011 for Issue:236359
              sb.append("<td><b>MODE</b></td>");
              sb.append("<td><b>SERVICE LEVEL</b></td>");
              sb.append("<td><b>FROM COUNTRY</b></td>");
              sb.append("<td><b>FROM LOCATION</b></td>");
              sb.append("<td><b>TO COUNTRY</b></td>");
              sb.append("<td><b>TO LOCATION</b></td>");
              sb.append("<td><b>QUOTE STATUS</b></td>");//modified by silpa on 29-04-2011
              sb.append("<td><b>STATUS REASON</b></td>");
              sb.append("<td><b>COUNTRY ID</b></td>");
              sb.append("<td><b>TERMINAL</b></td>");
             // sb.append("<td><b>QUOTE STATUS</b></td>");//modified by silpa on 29-04-2011
              sb.append("<td><b>ACTIVITY STATUS</b></td>");
              //sb.append("<td><b>VERSION NO</b></td>");
              sb.append("</tr>");
              
             /* out.print("SalesPerson \t");
              out.print("Quote Date \t");
              out.print("Quote Id \t");
              out.print("Customer Id \t");
              out.print("Quote Status \t"); 
              out.print("Mode \t");
              out.print("Service Level \t");
              out.print("From Country \t");
              out.print("From Location \t");
              out.print("To Country \t");
              out.print("To Location \t");
              out.print(" \n");   */        
               formatter.setPatternWithTime(format);
               int dataListSize	= dataList.size();
               String tempQuoteId = "";//Added by Anil.k for Multi Quote
               String activityStatus = "";//Added by Rakesh
               String activeQuoteId	=	"";//Added by Rakesh
               int maxVersionNo=0;//Added by Rakesh
                              
              for(int i=0;i<dataListSize;i++)
              {
                  detailsDOB  = (ReportDetailsDOB)dataList.get(i);
            	  maxVersionNo=detailsDOB.getMaxVersionNo();
              //@@Added by kameswari   
		         	if(detailsDOB.getQuoteDateTstmp()!=null)			
		         	{
		         /*	   if(detailsDOB.getVersionNo()==1)	Commented by govind for not displaying the Created date */
			           str = formatter.getDisplayStringArray(detailsDOB.getQuoteDateTstmp());
                      }            	
            	  if(maxVersionNo<2 || detailsDOB.getVersionNo()>maxVersionNo-2){
                
            		//Added by Rakesh on 22-02-2011 for Issue:236359
			         if(detailsDOB.getModifyDate()!=null)			
			         	{
	               
	                 
			        	 str1 = formatter.getDisplayStringArray(detailsDOB.getModifyDate());
	                
				         }//Ended by Rakesh on 22-02-2011 for Issue:236359
                   if(detailsDOB.getValidTo()!=null){
                	   validTo = formatter.getDisplayStringArray(detailsDOB.getValidTo());
                   }
                   if(detailsDOB.getCustDate()!=null){
                	   custDate = formatter.getDisplayStringArray(detailsDOB.getCustDate());
			         }
                  
                  sb.append("<tr align='center'>");
                //sb            =  new StringBuffer("");
                  
                  
                  //if(tempQuoteId.equalsIgnoreCase(detailsDOB.getQuoteId()))//Added by Anil.k for Multi Quote
                  //if(!((versionNo == 0 && "".equalsIgnoreCase(activeQuoteId)) || !(versionNo == detailsDOB.getVerionNo() && (activeQuoteId.equalsIgnoreCase(detailsDOB.getQuoteId())))))//Modified by Rakesh.k for Issue id:239925
                  if(!(("".equalsIgnoreCase(activityStatus)  && "".equalsIgnoreCase(activeQuoteId)) || !(activityStatus.equalsIgnoreCase(detailsDOB.getActiveFlag())) || !(activeQuoteId.equalsIgnoreCase(detailsDOB.getQuoteId()))) && "Y".equalsIgnoreCase(detailsDOB.getIs_MultiQuote()))//Modified by Rakesh.k for Issue id:239925
                  {
                	  sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      //Added by Rakesh on 22-02-2011 for Issue:236359 
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      sb.append("<td>").append("").append("</td>");
                      //sb.append("<td>").append("").append("</td>");//modified by silpa on 3-05-11
                      //Ended by Rakesh on 22-02-2011 for Issue:236359 
                      //sb.append("<td>").append("").append("</td>");  COmmented by Rakesh 
                  }
                  else
                  {//Ended by Anil.
                  sb.append("<td>").append(detailsDOB.getCreatedBy()).append("</td>");//@@Added by subrahmanyam for 173831 on 18-Jun-09
                  sb.append("<td>").append(detailsDOB.getSalesPerson()).append("</td>");
                  sb.append("<td>").append(((detailsDOB.getQuoteDateTstmp())!=null)?str!= null?str[0]:"":"").append("</td>");//@@Modified by kameswari
                  //Added by Rakesh on 22-02-2011 for Issue:236359 
                  sb.append("<td>").append(((detailsDOB.getQuoteDateTstmp())!=null)?str!= null?str[1]:"":"").append("</td>");//@@Modified by kameswari
                  sb.append("<td>").append(detailsDOB.getModifyDate()!=null ? str!= null?str1[0]:"":"").append("</td>");
                  sb.append("<td>").append(detailsDOB.getModifyDate()!=null ? str!= null?str1[1]:"":"").append("</td>");
                  sb.append("<td>").append(detailsDOB.getValidTo()!=null ? validTo[0]: "").append("</td>");
                  sb.append("<td>").append(detailsDOB.getCustDate()!=null? custDate[0]: "").append("</td>");
                  sb.append("<td>").append(detailsDOB.getCustReqTime()!=null? detailsDOB.getCustReqTime(): "").append("</td>");//Added by Rakesh on 22-03-2011
                  //Ended by Rakesh on 22-02-2011 for Issue:236359 
                  sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>");//@@added by kameswari for the WPBN issue-30313
                 // sb.append("<td>").append(detailsDOB.getQuoteStatus()).append("</td>");//modified by silpa on 3-05-11
                  sb.append("<td>").append(detailsDOB.getIncoTerms()!=null ? detailsDOB.getIncoTerms():"").append("</td>");//Added by Rakesh on 22-02-2011 for Issue:236359
                  sb.append("<td>").append(detailsDOB.getShipmentMode()).append("</td>");
                	  tempQuoteId = detailsDOB.getQuoteId();
                  }//Added by Anil.k for Multi Quote
                  
                  sb.append("<td>").append(detailsDOB.getServiceLevel()!=null ? detailsDOB.getServiceLevel():"").append("</td>");
                  sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                  sb.append("<td>").append(detailsDOB.getQuoteStatus()!= null ? detailsDOB.getQuoteStatus():"").append("</td>");//modified by silpa on 29-04-2011
                  sb.append("<td>").append(detailsDOB.getStatusReason()!= null ? detailsDOB.getStatusReason():"").append("</td>"); // Added By Kishroe Podili For statusReason
                  //Added by Rakesh on 22-02-2011 for Issue:236359 
                  sb.append("<td>").append(detailsDOB.getCountry()!= null ? detailsDOB.getCountry():"").append("</td>");
                  sb.append("<td>").append(detailsDOB.getTerminalId()!= null ? detailsDOB.getTerminalId():"").append("</td>");
                  //sb.append("<td>").append(detailsDOB.getQuoteStatus()!= null ? detailsDOB.getQuoteStatus():"").append("</td>");//modified by silpa on 29-04-2011
                  sb.append("<td>").append(detailsDOB.getActiveFlag()!= null ? detailsDOB.getActiveFlag():"").append("</td>");
                  //sb.append("<td>").append(detailsDOB.getVersionNo()).append("</td>");
                  //Ended by Rakesh on 22-02-2011 for Issue:236359 
                  sb.append("</tr>");
                  
                  activityStatus 		=	detailsDOB.getActiveFlag();
                  activeQuoteId		=	detailsDOB.getQuoteId();
                  //versionNo			=   detailsDOB.getVersionNo();//Added by Rakesh 
              	//} 
              	} 
               }
               sb.append("</body>");
              sb.append("</html>");
              out.print(sb.toString());
            }
        }
        
    }
    else if("ActivityPageBrowse".equals(operation))
    {           
        
        reportenterDob=(ReportsEnterIdDOB)session.getAttribute("ActivityEnterData"); 
        System.out.println("ActivityPageBrowse ActivityPageBrowse");
        if(request.getParameter("PageNo")!=null)
        {
          String pageno=request.getParameter("PageNo");
          reportenterDob.setPageNo(Integer.parseInt(pageno)); 
        }
        String SortBy    = request.getParameter("SortBy");
        String SortOrder = request.getParameter("SortOrder");
        
        //shyam starts here
        //ArrayList     checkList     = null;
        HashMap       checkList     = null;
        String        update        = "";        
        checkList = (HashMap)session.getAttribute("checkList");
        update    = request.getParameter("chkUpdate");
        //added by phani sekhar for wpbn 181670 on 20090910
        HashMap resultMap = null;
        HashMap successMap = null;
        HashMap versionHigherMap = null;
        HashMap updateReportMap = null;
        Iterator        quoteIterator     = null;
        String          qKey               = "";
        StringBuffer successmsg = null;
        StringBuffer higVermsg = null;
        StringBuffer updateReprtmsg = null;
        //ends 181670

        
        if(checkList==null)
          checkList = new HashMap();
        
        String []     quoteIdChked  = null;
        
        ReportDetailsDOB reportDetailDob  = null;
        
        quoteIdChked= request.getParameterValues("quoteIdChked");
        String []     actInact  = null;
        
        actInact= request.getParameterValues("checkValue");
        int actLength = 0;
        
        //Added by Kishore For statusReason
       
       String[]	status_Reasons	= request.getParameterValues("statusReason");
        
       
        if(quoteIdChked!=null)
        {
          actLength = actInact.length;
          for (int i=0;i<actLength;i++)
          {
              if(actInact[i].equals("I")||actInact[i].equals("A"))
              {
                  /*reportDetailDob=new ReportDetailsDOB();
                  reportDetailDob.setQuoteId(activityCheck[i]);
                  reportDetailDob.setActiveFlag(actInact[i]);         
                  checkList.add(reportDetailDob);*/
            	//Added by Kishore For statusReason
            	  //if("H".equals(loginbean.getUserTerminalType()))
            		  checkList.put(quoteIdChked[i],actInact[i]+status_Reasons[i]);
            	  /*else
                  checkList.put(quoteIdChked[i],actInact[i]);*/
              }
              else
              {
                  if(checkList.containsKey(quoteIdChked[i]))
                      checkList.remove(quoteIdChked[i]);
              }
            //System.out.println("actInact.length---"+actInact.length);
          }
        }
        session.setAttribute("checkList",checkList);
        //shyam ends here
        reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
        reportenterDob.setSortBy(SortBy);
        reportenterDob.setSortOrder(SortOrder); 
        
        try
        {
          if(update.equalsIgnoreCase("no"))
          {
              home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
              remote                  =     (ReportsSession)home.create();	          
              mainDataList            =      remote.getActivityReportDetails(reportenterDob);          
          }
          else if(update.equalsIgnoreCase("yes"))
          {
              if(checkList!=null && checkList.size()>0)
              {
                home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
                remote                  =     (ReportsSession)home.create();	          
                 //remote.updateActivityFlag(checkList);//commented and modified by phani sekhar for wpbn 181670 on 20090909
               resultMap = remote.updateActivityFlag(checkList);

              }
              
          }
        }catch(Exception	e)
        {
         e.printStackTrace();
        } 
        if(update.equalsIgnoreCase("no"))
        {
          request.setAttribute("mainDataList",mainDataList);
          nextNavigation  = "reports/QMSActivityReport.jsp?Operation="+operation+"&SortBy="+SortBy+"&SortOrder="+SortOrder+"&PageNo="+request.getParameter("PageNo");
        }
        else if(update.equalsIgnoreCase("yes"))
        {
          session.removeAttribute("checkList");
          errorMessage = "Successfully Updated";
          errorCode    = "MSG";
         //added by phani sekhar for wpbn 181670 on 20090909
          if(resultMap!=null)
          {
          successMap= (HashMap) resultMap.get("succes");          
          versionHigherMap = (HashMap) resultMap.get("fail1");
          updateReportMap = (HashMap) resultMap.get("fail2");
          
          quoteIterator  = successMap.keySet().iterator();
          if(successMap.size()>0) {
          successmsg = new StringBuffer();
          errorMessage = "Successfully Updated: ";
          }
          while(quoteIterator.hasNext())
          {            
          qKey = (String)quoteIterator.next();
          successmsg.append(qKey);
          if(quoteIterator.hasNext())//added by subrahmanyam for the wpbn id: 186630 on 15-oct-09
            successmsg.append(",");
          }
          if(successmsg!=null && successmsg.length()>0)
          errorMessage=errorMessage+successmsg.toString();
          
         
         if(versionHigherMap.size()>0)
         {
            higVermsg = new StringBuffer();
          higVermsg.append("\nHigher version updated quote is available, hence these quotes cannot be made active :");
          higVermsg.append("\n");
        
         }
         quoteIterator  = versionHigherMap.keySet().iterator();
           while(quoteIterator.hasNext())
          {            
          qKey = (String)quoteIterator.next();
          higVermsg.append(qKey);
          if(quoteIterator.hasNext())//added by subrahmanyam for the wpbn id: 186630 on 15-oct-09
          higVermsg.append(",");
          }
         if(higVermsg!=null)
         errorMessage=errorMessage+higVermsg.toString()+"\n";
          
          
          if(updateReportMap.size()>0)
         {
            updateReprtmsg = new StringBuffer();
          updateReprtmsg.append("\nQuotes  in updated report, hence cannot be made inactive: ");
          updateReprtmsg.append("\n");
        
         }
         quoteIterator  = updateReportMap.keySet().iterator();
           while(quoteIterator.hasNext())
          {            
          qKey = (String)quoteIterator.next();
          updateReprtmsg.append(qKey);
           if(quoteIterator.hasNext())//added by subrahmanyam for the wpbn id: 186630 on 15-oct-09
          updateReprtmsg.append(",");
          }
         if(updateReprtmsg!=null){
         errorMessage=errorMessage+updateReprtmsg.toString()+"\n";
         }
          
          
          }
          //ends 181670

          errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation","activityAdd")); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject); 
          nextNavigation = "ESupplyErrorPage.jsp";
        }
        //nextNavigation  = "reports/QMSActivityReport.jsp";         
    }
    else if("getYieldDetails".equals(operation))
    {

        String[] terminalIds  =null;
        String[] salesPerIds  =null;
        String[] customerIds  =null;
        String[] fromCountry  =null;
        String[] fromLocation =null;
        String[] toCountry    =null;
        String[] toLocation   =null;
        String[] serviceLevels=null;
        String[] quoteStatuses=null;
        ESupplyDateUtility formatter = new ESupplyDateUtility();
        String   fromDate     =null;
        String   toDate       =null;
        StringBuffer appTerm  =new StringBuffer("");
        StringBuffer appSales =new StringBuffer(""); 
        StringBuffer appCusto =new StringBuffer(""); 
        StringBuffer appFromCo=new StringBuffer(""); 
        StringBuffer appFromLo=new StringBuffer(""); 
        StringBuffer appToCo  =new StringBuffer(""); 
        StringBuffer appToLo  =new StringBuffer(""); 
        StringBuffer appServ  =new StringBuffer(""); 
        StringBuffer appSta   =new StringBuffer(""); 
        String       shMode   =null;
        
        terminalIds  = request.getParameterValues("terminalId"); 
        salesPerIds  = request.getParameterValues("salesPersonCode"); 
        customerIds  = request.getParameterValues("customerId"); 
        fromCountry  = request.getParameterValues("fromCountry"); 
        fromLocation = request.getParameterValues("fromLocation"); 
        toCountry    = request.getParameterValues("toCountry"); 
        toLocation   = request.getParameterValues("toLocation"); 
        serviceLevels= request.getParameterValues("serviceLevelId"); 
        quoteStatuses= request.getParameterValues("quoteStatus");
        
        
        //fromDate = formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("fromDate"),"00:00:01");
        
       // toDate = formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("toDate"),"23:59:59").toString();
        
        //toDate     = request.getParameter("fromDate");
        
        
        
        //toDate       = request.getParameter("toDate");
        shMode       = request.getParameter("shipmentMode")!=null?request.getParameter("shipmentMode"):"";
        
        
        if(request.getParameterValues("terminalId")!=null)
        {
        	int termIdsLen	=	terminalIds.length;
        for(int i=0;i<termIdsLen;i++)
         appTerm.append("'").append(terminalIds[i]).append("',");
        
        appTerm.append(appTerm.substring(0,appTerm.length()-1));
        }
        if(request.getParameterValues("salesPersonCode")!=null)
        {
        	int salesPersIdLen	= salesPerIds.length;
        for(int j=0;j<salesPersIdLen;j++)
         appSales.append("'").append(salesPerIds[j]).append("',");
        
        appSales.append(appSales.substring(0,appSales.length()-1));
        }
         //@@Modified by Kameswari for the WPBN issue- on 07/01/09
       /*if(request.getParameterValues("customerId")!=null&&request.getParameterValues("customerId").toString().trim().length()>0)
        {
        for(int k=0;k<customerIds.length;k++)
         appCusto.append("'").append(customerIds[k]).append("',");
        
        appCusto.append(appCusto.substring(0,appCusto.length()-1));
        }*/
        if(request.getParameter("customerId")!=null&&request.getParameter("customerId").trim().length()>0)
        {
            appCusto.append("'").append(request.getParameter("customerId")).append("'");
        }
        if(request.getParameterValues("fromCountry")!=null)
        {
        	int frmCntrLen	= fromCountry.length;
        for(int l=0;l<frmCntrLen;l++)
         appFromCo.append("'").append(fromCountry[l]).append("',");
        
        appFromCo.append(appFromCo.substring(0,appFromCo.length()-1));
        }
        if(request.getParameterValues("fromLocation")!=null)
        {
        	int frmLocLen	= fromLocation.length;
        for(int m=0;m<frmLocLen;m++)
         appFromLo.append("'").append(fromLocation[m]).append("',");
        
        appFromLo.append(appFromLo.substring(0,appFromLo.length()-1));
        }
        if(request.getParameterValues("toCountry")!=null)
        {
        	int toCntryLen	= toCountry.length;
        for(int n=0;n<toCntryLen;n++)
         appToCo.append("'").append(toCountry[n]).append("',");
        
        appToCo.append(appToCo.substring(0,appToCo.length()-1));
        }
        if(request.getParameterValues("toLocation")!=null)
        {
        	int toLocLen	= toLocation.length;
        for(int o=0;o<toLocLen;o++)
         appToLo.append("'").append(toLocation[o]).append("',");
        
        appToLo.append(appToLo.substring(0,appToLo.length()-1));
        }
        if(request.getParameterValues("serviceLevelId")!=null)
        {
        	int srvLevelIdsLen	=	serviceLevels.length;
        for(int i=0;i<srvLevelIdsLen;i++)
         appServ.append("'").append(serviceLevels[i]).append("',");
         
         appServ.append(appServ.substring(0,appServ.length()-1));
        }
        if(request.getParameterValues("quoteStatus")!=null)
        {
        	int quotStatusLen	= quoteStatuses.length;
         for(int i=0;i<quotStatusLen;i++)
         appSta.append("'").append(quoteStatuses[i]).append("',");
         
         appSta.append(appSta.substring(0,appSta.length()-1)); 
        }
        reportenterDob=new ReportsEnterIdDOB();
        
        //reportenterDob.setFromDate1(formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("fromDate"),"00:00:01"));
        //reportenterDob.setToDate1(formatter.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),request.getParameter("toDate"),"23:59:59"));
        reportenterDob.setDateFormat(loginbean.getUserPreferences().getDateFormat());
        reportenterDob.setFromDate(request.getParameter("fromDate")+" 00:00:01");
        reportenterDob.setToDate(request.getParameter("toDate")+" 23:59:59");
        reportenterDob.setShipmentMode(shMode);
        
        
        reportenterDob.setTerminalId(appTerm.toString());
        reportenterDob.setCustomerId(appCusto.toString());
        reportenterDob.setSalesPersonId(appSales.toString());
        reportenterDob.setFromCountry(appFromCo.toString());
        reportenterDob.setOrginLocation(appFromLo.toString());
        reportenterDob.setToCountry(appToCo.toString());
        reportenterDob.setDestLocation(appToLo.toString());
        reportenterDob.setServiceLevel(appServ.toString());
        reportenterDob.setQuoteStatus(appSta.toString());
         reportenterDob.setAutoUpdated("UPD");//@@Added by Kameswari for the WPBN issue-154390 on 23/02/09
        reportenterDob.setLoginTerminal(loginbean.getTerminalId());//@@Added by Kameswari for the WPBN issue-71825
        session.setAttribute("YieldEnterData",reportenterDob);
                       
        reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
        reportenterDob.setPageNo(1);
        reportenterDob.setSortBy("QuoteId");//@@Modified by Kameswari for the WPBN issue-38116
        reportenterDob.setSortOrder("ASC");
               
        try
        {
            reportenterDob.setFormateSerch(request.getParameter("format"));
             home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
             remote                  =     (ReportsSession)home.create();	          
             mainDataList            =      remote.getYieldReportDetails(reportenterDob);          
        }catch(Exception	e)
        {
         e.printStackTrace();
        }
        if("html".equals(request.getParameter("format")))
        {
          //session.setAttribute("mainDataList",mainDataList);
          request.setAttribute("mainDataList",mainDataList);
          nextNavigation   =  "reports/QMSYieldReport.jsp?Operation="+operation+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";//@@Modified by Kameswari for the WPBN issue-38116
          //nextNavigation  = "reports/QMSYieldReport.jsp";
        }
        else
        {
          StringBuffer sb    =  null;
          if(mainDataList!=null && mainDataList.size()>0)
          {       		
              dataList		   =	(ArrayList)mainDataList.get(1);
              sb   =   new StringBuffer("");
              
              out  =  response.getWriter();
              response.setContentType("application/vnd.ms-excel");	
              String contentDisposition = " :attachment;";	
              response.setHeader("Content-Disposition","attachment;filename=YieldReport.xls");
              
              sb.append("<html>");
              sb.append("<body>");
              sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
              sb.append("<tr align='center'>");
              sb.append("<td><b>SALES PERSON</b></td>");
              sb.append("<td><b>QUOTE ID</b></td>");
              sb.append("<td><b>CUSTOMER ID</b></td>");
              sb.append("<td><b>CUSTOMER NAME</b></td>");//@@added by kameswari for the WPBN issue-30313
              sb.append("<td><b>QUOTE STATUS</b></td>");
              sb.append("<td><b>MODE</b></td>");
              sb.append("<td><b>SERVICE LEVEL</b></td>");
              sb.append("<td><b>FROM COUNTRY</b></td>");
              sb.append("<td><b>FROM LOCATION</b></td>");
              sb.append("<td><b>TO COUNTRY</b></td>");
              sb.append("<td><b>TO LOCATION</b></td>");
              sb.append("<td><b>AVERAGE YIELD</b></td>");
              sb.append("</tr><br>");
              int dListSize	= dataList.size();
                for(int i=0;i<dListSize;i++)
                {
                    detailsDOB  = (ReportDetailsDOB)dataList.get(i);
                    sb.append("<tr align='center'>");
                    sb.append("<td>").append(detailsDOB.getSalesPerson()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>");//@@added by kameswari for the WPBN issue-30313
                    sb.append("<td>").append(detailsDOB.getQuoteStatus()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getShipmentMode()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getServiceLevel()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFromCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getFromLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToCountry()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getToLocation()).append("</td>");
                    sb.append("<td>").append(detailsDOB.getAverageYield()).append("</td>");
                    sb.append("</tr>");
                 }
                 sb.append("</body>");
                 sb.append("</html>");
                 out.print(sb.toString());
          }
        }        
      }
      else if("YieldPageBrowse".equals(operation))
      {
        reportenterDob=(ReportsEnterIdDOB)session.getAttribute("YieldEnterData"); 
        
        if(request.getParameter("PageNo")!=null)
        {
          String pageno=request.getParameter("PageNo");
          reportenterDob.setPageNo(Integer.parseInt(pageno)); 
        }
        String SortBy    = request.getParameter("SortBy");
        String SortOrder = request.getParameter("SortOrder");                
        reportenterDob.setNoOfRecords(Integer.parseInt(loginbean.getUserPreferences().getLovSize()));
        reportenterDob.setSortBy(SortBy);
        reportenterDob.setSortOrder(SortOrder); 
               
        try
        {
             home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
             remote                  =     (ReportsSession)home.create();	          
             mainDataList            =      remote.getYieldReportDetails(reportenterDob);          
        }catch(Exception	e)
        {
         e.printStackTrace();
        } 
        
        //session.setAttribute("mainDataList",mainDataList);
        request.setAttribute("mainDataList",mainDataList);
        nextNavigation   =  "reports/QMSYieldReport.jsp?Operation="+operation+"&SortBy="+SortBy+"&SortOrder="+SortOrder+"&PageNo="+request.getParameter("PageNo");
        //nextNavigation  = "reports/QMSYieldReport.jsp";                       
      }
      else if("ActivitySummary".equals(operation)  && subOperation==null)
      {
        
        HashMap summaryDetails  =     new HashMap();             
        home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
        remote                  =     (ReportsSession)home.create();	          
        summaryDetails          =      remote.getActivitySummaryReportDetails(loginbean);
        
        request.setAttribute("summaryDetails",summaryDetails);   
        nextNavigation  = "reports/QMSActivitySummaryReport.jsp";
      
      }
      else if("updatedQuotes".equalsIgnoreCase(operation) && (subOperation==null || "activitySummary".equalsIgnoreCase(subOperation) || "".equalsIgnoreCase(subOperation)))
      {
        ArrayList   list    =   getUpdatedQuotes(request,response,loginbean);
        UpdatedQuotesFinalDOB finalDOB  =   new UpdatedQuotesFinalDOB();
        finalDOB.setChangeDescList(list);
        session.setAttribute("reportFinalDOB",finalDOB);
        //nextNavigation      =   "reports/UpdateQuotesEnterId.jsp";//146463
        nextNavigation      =   "reports/UpdateQuotesEnterId.jsp?fromSummary="+fromSummary;
      }
      else if("updatedQuotes".equalsIgnoreCase(operation) && "Report".equalsIgnoreCase(subOperation))
      {
        int selectedIndex        =   0;
        String selectedIndexArr	 =	 "";
        String[] chargeDesc	 	 =	 null;
        ArrayList excelList      =   new ArrayList();
        ArrayList   reportList   =   null; 
        int         pageNo       =   Integer.parseInt(request.getParameter("PageNo"));
        String      sortBy       =   request.getParameter("SortBy");
        String      sortOrder    =   request.getParameter("SortOrder");
        String      repFormat    =   request.getParameter("format");
        String      fromWhere    =   request.getParameter("fromWhat");
       // System.out.println("sortOrder sortOrder : "+sortOrder);
        UpdatedQuotesFinalDOB finalDOB  =   (UpdatedQuotesFinalDOB)session.getAttribute("reportFinalDOB");
        
        if(finalDOB==null)
            finalDOB    =   new UpdatedQuotesFinalDOB();
         
        ArrayList   list                =   finalDOB.getChangeDescList();
         
        UpdatedQuotesDOB  updatedQuotes  =   null;
        //Added by Anil.k for Excel Page
        if(request.getParameter("htxl")!=null && "HTML".equalsIgnoreCase(request.getParameter("htxl")))
        {
        if(request.getParameter("selectedIndex")!=null && request.getParameter("selectedIndex").trim().length()!=0)
             selectedIndex  =  Integer.parseInt(request.getParameter("selectedIndex"));
        
        finalDOB.setChangeDescSelectedIndex(selectedIndex);
             
        if(list != null)
          updatedQuotes    =   (UpdatedQuotesDOB)list.get(selectedIndex);
        else
          updatedQuotes    =   new UpdatedQuotesDOB();
        
          reportList  = getUpdatedQuotesReport(updatedQuotes.getChangeDesc(),pageNo,sortBy,sortOrder,loginbean.getTerminalId(),repFormat,fromWhere,loginbean);
          
          finalDOB.setUpdatedQuotesList(reportList);
          
          session.setAttribute("reportFinalDOB",finalDOB);
          //nextNavigation  = "reports/UpdatedQuotesReport.jsp?ChargeDesc="+updatedQuotes.getChangeDesc()+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";//146463
          nextNavigation  = "reports/UpdatedQuotesReport.jsp?ChargeDesc="+updatedQuotes.getChangeDesc()+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1&fromSummary="+fromSummary;
        }//Added by Anil.k for Excel Page
        else if(request.getParameter("htxl")!=null && "EXCEL".equalsIgnoreCase(request.getParameter("htxl")))
        {	
        	if(request.getParameter("selectedIndex")!=null && request.getParameter("selectedIndex").trim().length()!=0)
                selectedIndexArr  =  request.getParameter("selectedIndex");
        	chargeDesc = selectedIndexArr.split(",");
           finalDOB.setChangeDescSelectedIndexArr(chargeDesc);
           for(int i=0;i<chargeDesc.length;i++)
           {
           if(list != null)
             updatedQuotes    =   (UpdatedQuotesDOB)list.get(Integer.parseInt(chargeDesc[i]));
           else
             updatedQuotes    =   new UpdatedQuotesDOB();
           
             reportList  = getUpdatedQuotesReport(updatedQuotes.getChangeDesc(),pageNo,sortBy,sortOrder,loginbean.getTerminalId(),repFormat,fromWhere,loginbean);
             excelList.add(reportList);
           }
             finalDOB.setUpdatedQuotesList(excelList);
             
             session.setAttribute("reportFinalDOB",finalDOB);
             //nextNavigation  = "reports/UpdatedQuotesReport.jsp?ChargeDesc="+updatedQuotes.getChangeDesc()+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1";//146463
             nextNavigation  = "reports/UpdatedQuotesReport.jsp?ChargeDesc="+updatedQuotes.getChangeDesc()+"&SortBy=QuoteId&SortOrder=ASC&PageNo=1&fromSummary="+fromSummary;
           //Ended by Anil.k for Excel Page
        }
      }
      else if("updatedQuotes".equalsIgnoreCase(operation) && "pageBrowse".equalsIgnoreCase(subOperation))
      {
        //System.out.println("In suboperation pageBrowse");
        ArrayList   reportList   =   null;
        ArrayList   checkedList  =   null;  
        
        String      chargeDesc   =   request.getParameter("ChargeDesc");
        int         pageNo       =   Integer.parseInt(request.getParameter("PageNo"));
        String      sortBy       =   request.getParameter("SortBy");
        String      sortOrder    =   request.getParameter("SortOrder");
        String      repFormat    =   request.getParameter("format");
        String      fromWhere    =   request.getParameter("fromWhat");
        UpdatedQuotesFinalDOB finalDOB  =   (UpdatedQuotesFinalDOB)session.getAttribute("reportFinalDOB");
        
        if(finalDOB==null)
            finalDOB    =   new UpdatedQuotesFinalDOB();
            
        checkedList     =   finalDOB.getUpdatedQuotesList();
        
       
        doPageValuesForUpdatedQuotes(request,response,(ArrayList)checkedList.get(2));
       
        reportList  = getUpdatedQuotesReport(chargeDesc,pageNo,sortBy,sortOrder,loginbean.getTerminalId(),repFormat,fromWhere,loginbean);
        finalDOB.setUpdatedQuotesList(reportList);
        session.setAttribute("reportFinalDOB",finalDOB);
    
        nextNavigation  = "reports/UpdatedQuotesReport.jsp?ChargeDesc="+chargeDesc+"&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo+"&format="+repFormat;
        
      }
      else if("updatedQuotes".equalsIgnoreCase(operation) && "Continue".equalsIgnoreCase(subOperation))
      {
        session.removeAttribute("reportFinalDOB");
        nextNavigation  = "ESMenuRightPanel.jsp?link=es-et-Administration-Reports";
      }
      else if("updatedQuotes".equalsIgnoreCase(operation) && "Confirm".equalsIgnoreCase(subOperation))
      { 
        if("<<Back".equalsIgnoreCase(request.getParameter("submit1")))
          nextNavigation      =   "reports/UpdateQuotesEnterId.jsp";
        else if("Confirm>>".equalsIgnoreCase(request.getParameter("submit1")))
        { 
          //String[] selectedIndices = request.getParameterValues("checkBoxValue");
          updatedMapValue          = null;
          updateDataList           = null;
             ArrayList   emailList  =   null;  
              ArrayList   updatelist  =   null;  
          ArrayList   emaildataList  =   null; 
          listSize                 = 0;
          String  fromWhat         = request.getParameter("fromWhat");
          StringBuffer dispNtUpdatedQts = new StringBuffer();  // added  by phani sekhar for wpbn 173666 on 20090615
          if("summaryReport".equalsIgnoreCase(fromWhat))
              fromWhat  = "activitySummary";
              
  
          int[]    selIndices      = null;     
          UpdatedQuotesFinalDOB reportFinalDOB  = (UpdatedQuotesFinalDOB)session.getAttribute("reportFinalDOB");
          
          if(reportFinalDOB==null)
            reportFinalDOB    =   new UpdatedQuotesFinalDOB();
          
          ArrayList             reportList      = reportFinalDOB.getUpdatedQuotesList();
          
          doPageValuesForUpdatedQuotes(request,response,(ArrayList)reportList.get(2));
          
          if(session.getAttribute("HashList")!=null)
          {
              updatedMapValue					=	(HashMap)session.getAttribute("HashList");
              //  updateDataList          =  doGetFinalUpdatedList(request,response,updatedMapValue);
               //@@Commented and  by Kameswari for the WPBN issue -142381  on 23/10/08
               updatelist          =  doGetFinalUpdatedList(request,response,updatedMapValue);
              //@@Added by Kameswari for the WPBN issue -142381
                if(updatelist!=null&&updatelist.size()>0)
                {
                    updateDataList          =  (ArrayList)updatelist.get(0);
                   
                    emailList                =   (ArrayList)updatelist.get(1);
                }  
                 //@@WPBN issue -142381
          }
          long                  quoteId         = 0L;
          ArrayList             quoteFinalDOBs  = null;
          QuoteFinalDOB         tmpFinalDOB     = null;
          int                   l               =  0;
          String                msg             =   "";
          String[]              contactPersons  = null;
          StringBuffer          toEmailIds      = null;   
         
          if(updateDataList!=null)
          {
            listSize  = updateDataList.size();
            Hashtable accessList  =  (Hashtable)session.getAttribute("accessList");
      
            //@@setting the buy rates permissions flag based on user role permissions.
            if(accessList.get("10605")!=null)
              reportFinalDOB.setBuyRatesFlag("Y");
            else
              reportFinalDOB.setBuyRatesFlag("N"); 
            
            selIndices  = new int[listSize];
            reportFinalDOB.setSelectedQuotesIndices(selIndices);
            reportFinalDOB.setUpdatedQuotesList(updateDataList);
            quoteFinalDOBs = getUpdatedQuotesDetails(reportFinalDOB,loginbean);
            if(quoteFinalDOBs!=null)
            {
             ArrayList    list           =   null;
             
             ArrayList    sentMailList   =   null;
             ArrayList    unsentMailList =   null;
             ArrayList    sentFaxList    =   null;
             ArrayList    unsentFaxList  =   null;
             int            count           = 0;
             ArrayList    sentList       =   new ArrayList();
             ArrayList    filesList        = null;
             int quotFinalDobSize	=	quoteFinalDOBs.size();
             for(int k=0;k<quotFinalDobSize;k++)
             {
               toEmailIds     = new StringBuffer("");
               tmpFinalDOB    = (QuoteFinalDOB) quoteFinalDOBs.get(k);
               //@@Added by Kameswari for the WPBN issue -142381  on 23/10/08
                count           = 0;
                 // added  by phani sekhar for wpbn 173666 on 20090615
                if("N".equals(tmpFinalDOB.getUpdateQuoteFlag()))
                {
                if(tmpFinalDOB.getMasterDOB()!=null)
                  dispNtUpdatedQts.append(tmpFinalDOB.getMasterDOB().getQuoteId());
                  dispNtUpdatedQts.append(",");
                  continue;
                }//ends 173666
                int emailListSize	= emailList.size();
               for(int i =0;i<emailListSize;i++)
               {
              
               // if(Long.parseLong((String)emailList.get(i))==tmpFinalDOB.getUpdatedReportDOB().getQuoteId())//@@Commented and Modified by Kameswari for the WPBN issue-146971 on 04/12/08
               
//@@ Commented by subrahmanyam for the Enhancement 146971 and 146970  on 02/02/09            
               /*if((String)emailList.get(i)==tmpFinalDOB.getUpdatedReportDOB().getQuoteId())
               {
                  
                    count++;
                      break;
                }*/
//@@ Added by subrahmanyam for the Enhancement 146971 and 146970 on 02/02/09          
                 if(((String)emailList.get(i)).equalsIgnoreCase(tmpFinalDOB.getUpdatedReportDOB().getQuoteId()))
                {
                  
                    count++;
                      break;
                }
//@@ Ended by subrahmanyam for the Enhancement 146971 and 146970  on 02/02/09           
             }
             //@@23/10/08
             if(count>0)
             {
               contactPersons = tmpFinalDOB.getMasterDOB().getCustContactNames();
             //@@Added by Kameswari for the internal issue on 10/04/09
              filesList  = qmsremote.getQuoteAttachmentDtls(tmpFinalDOB); 
             if(filesList!=null)
              {
                  tmpFinalDOB.setAttachmentDOBList(filesList);           
              }
              //@@Added by Kameswari for the internal issue on 10/04/09
             /*  if(tmpFinalDOB.getUpdatedReportDOB()!=null)
                   quoteType  = "U";
               else 
                    quoteType = "N";
               terminalId  = loginbean.getTerminalId();
               emailText = qmsremote.getEmailText(terminalId,quoteType); 
                logger.info("emailText"+emailText);
               tmpFinalDOB.setEmailText(emailText);*/
                     //@@internal issue on 10/04/09
              //@@internal issue on 10/04/09
           //@@Modified by Kameswari for the WPBN issue-116548
               if("ACC".equalsIgnoreCase(tmpFinalDOB.getFlagsDOB().getQuoteStatusFlag())||"NAC".equalsIgnoreCase(tmpFinalDOB.getFlagsDOB().getQuoteStatusFlag())
               ||"PEN".equalsIgnoreCase(tmpFinalDOB.getFlagsDOB().getQuoteStatusFlag()))
               {
                  list           = doPDFGeneration(tmpFinalDOB, request, response);
              
               if(list !=null && list.size()>0)
               {
                  sentMailList   = (ArrayList)list.get(0);
                  unsentMailList = (ArrayList)list.get(1);
                  sentFaxList    = (ArrayList)list.get(2);
                  unsentFaxList  = (ArrayList)list.get(3);
               }
               msg  =   msg + "\nFor Quote Id "+tmpFinalDOB.getMasterDOB().getQuoteId() +":";
               if(sentMailList!=null && sentMailList.size()>0)
               /*  msg =   msg + "Quote has been successfully sent through Email to "+sentMailList + ", ";*///@@Commented and Modified by kameswari for teh WPBN issue-142381
                 msg =   msg + "Quote has been updated and  successfully sent through Email ";
              
               if(unsentMailList != null && unsentMailList.size()>0)
                  msg =   msg + "Email(s) Were Not Sent To "+unsentMailList + ", ";
               if(sentFaxList != null && sentFaxList.size()>0)
                   /*  msg =   msg + "Quote has been successfully sent through Fax to "+sentFaxList + ", ";*///@@Commented and Modified by kameswari for teh WPBN issue-142381
                 msg =   msg + "Quote has been updated and  successfully sent through Fax ";
    
               if(unsentFaxList != null && unsentFaxList.size()>0)
                  msg =   msg + "Fax Was Not Sent to  "+ unsentFaxList;
               
               msg    =   msg.trim();
               
               //@@ To Remove the Comma at the End of the message (if any).
               if (msg.indexOf(",")!=-1)
               {
                 if(msg.indexOf(",") == msg.length()-1)
                    msg = msg.substring(0,msg.length()-1);
               }
               }
              if((sentMailList!=null&&sentMailList.size() > 0 )|| (sentFaxList!=null&&sentFaxList.size() > 0))
                      sentList.add(tmpFinalDOB.getMasterDOB().getQuoteId()+"");
             
                 //@@Added by Kameswari for the WPBN issue-116548
                  else if("QUE".equalsIgnoreCase(tmpFinalDOB.getFlagsDOB().getQuoteStatusFlag()))
                  {
                        msg  = msg+"\nFor Quote Id "+tmpFinalDOB.getMasterDOB().getQuoteId()+":Quote has been escalated to"+tmpFinalDOB.getEscalatedTo();
                        String  message  = "Please refer to Quote Reference "+tmpFinalDOB.getMasterDOB().getQuoteId()+" pending your approval. Quote was created by "+tmpFinalDOB.getMasterDOB().getSalesPersonName()+" for "+tmpFinalDOB.getHeaderDOB().getCustomerName()+
                              ". Quote must be reviewed within "+tmpFinalDOB.getAllottedTime()+" hours.";
                   sendMail(tmpFinalDOB.getMasterDOB().getUserEmailId(),tmpFinalDOB.getReportingOfficerEmail(),"Quote Pending Your Approval, "+tmpFinalDOB.getHeaderDOB().getCustomerName()+", "+tmpFinalDOB.getMasterDOB().getQuoteId(),message,"",null);
                 }
                  else
                  {
                     msg = msg+"\nFor Quote Id "+tmpFinalDOB.getMasterDOB().getQuoteId()+":Quote has been Updated.";
                  }
                  //@@WPBN issue -
             } 
             else
             {
                if("QUE".equalsIgnoreCase(tmpFinalDOB.getFlagsDOB().getQuoteStatusFlag()))
                  {
                        msg  = msg+"\nFor Quote Id "+tmpFinalDOB.getMasterDOB().getQuoteId()+":Quote has been escalated to"+tmpFinalDOB.getEscalatedTo();
                        String  message  = "Please refer to Quote Reference "+tmpFinalDOB.getMasterDOB().getQuoteId()+" pending your approval. Quote was created by "+tmpFinalDOB.getMasterDOB().getSalesPersonName()+" for "+tmpFinalDOB.getHeaderDOB().getCustomerName()+
                              ". Quote must be reviewed within "+tmpFinalDOB.getAllottedTime()+" hours.";
                   sendMail(tmpFinalDOB.getMasterDOB().getUserEmailId(),tmpFinalDOB.getReportingOfficerEmail(),"Quote Pending Your Approval, "+tmpFinalDOB.getHeaderDOB().getCustomerName()+", "+tmpFinalDOB.getMasterDOB().getQuoteId(),message,"",null);
                 }
                 else
                 {
                    msg = msg+"\nFor Quote Id "+tmpFinalDOB.getMasterDOB().getQuoteId()+":Quote has been Updated.";
                 }
               }
             }  
               home                    =     (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
               remote                  =     (ReportsSession)home.create();
               
               if(sentList!=null && sentList.size()>0)
                  remote.updateSendMailFlag(sentList,loginbean.getUserId());
          }
        }
        //  errorMessage = "Quotes Successfully Updated."+msg;
         //@@Commented and  by Kameswari for the WPBN issue -142381  on 23/10/08
          errorMessage = "Quotes Successfully Updated."+'\n'+msg;
          errorCode    = "MSG";
              if( dispNtUpdatedQts.toString().length()>0 ) // added  by phani sekhar for wpbn 173666 on 20090615
         errorMessage = errorMessage+'\n'+"Following quotes were not Updated "+ '\n'+dispNtUpdatedQts.toString();

          //errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController");// commented by subrahmanyam for 146463
          //added by subrahmanyam for 146463
          if("yes".equalsIgnoreCase(fromSummary))
            errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController?Operation=ActivitySummary");
           else 
          errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController");
         //ended by subrahmanyam for 146463  
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation));
          //keyValueList.add(new KeyValue("subOperation","activitySummary"));//subrahmanyam for error in confirm the quote 146463
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          session.removeAttribute("reportFinalDOB");
          session.removeAttribute("HashList");
          nextNavigation = "ESupplyErrorPage.jsp";
        }  
      }
    }
    catch(FoursoftException fs)
    {
      
      logger.error(FILE_NAME+"Error in Controller"+fs);
      fs.printStackTrace();
      errorMessage = fs.getMessage();
      errorCode    = "ERR";
      //errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController");// commented by subrahmanyam for 146463
          //added by subrahmanyam for 146463
          if("yes".equalsIgnoreCase(fromSummary))
            errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController?Operation=ActivitySummary");
           else 
      errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController");
         //ended by subrahmanyam for 146463  
      keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
      keyValueList.add(new KeyValue("Operation",operation)); 	
      //keyValueList.add(new KeyValue("subOperation","activitySummary"));//added by subrahmanyam for error in confirming quote 146463
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 
      session.removeAttribute("reportFinalDOB");
       session.removeAttribute("HashList");//@@Added by Kameswari for the WPBN issue-13837
      nextNavigation = "ESupplyErrorPage.jsp";
    }
    catch(Exception e)
    {
      e.printStackTrace();
      
      logger.error(FILE_NAME+"Error in Servlet"+e.toString());
      errorMessage = "An Error has Occurred. Please Try Again";
      errorCode    = "ERR";
      errorMessageObject = new ErrorMessage(errorMessage,"QMSReportController");
      keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
      keyValueList.add(new KeyValue("Operation",operation)); 	
      keyValueList.add(new KeyValue("subOperation","activitySummary"));//subbu
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 
      session.removeAttribute("reportFinalDOB");
       session.removeAttribute("HashList");//@@Added by Kameswari for the WPBN issue-13837
      nextNavigation = "ESupplyErrorPage.jsp";
    }
    finally
    {
      //Logger.info(FILE_NAME,"nextNavigation::"+nextNavigation);
    //  logger.info(FILE_NAME+"nextNavigation::"+nextNavigation);
      if(nextNavigation!=null)
         doFileDispatch(request,response,nextNavigation);
    }
   
  }
  private ArrayList doGetReportDetails(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
    ReportsEnterIdDOB reportDOB   =   null;
    
    ArrayList         list        =   null;
    
    String countryId              =   null;
    String locationId             =   null;
    String salesPersonId          =   null;
    String fromDate               =   null;
    String toDate                 =   null;
    String customerId             =   null;
    String status                 =   null;
    String fromLocation           =   null;
    String fromCountry            =   null;
    String toLocation             =   null;
    String toCountry              =   null;
    String shipmentMode           =   null;
    String serviceLevelId         =   null;
    
    ReportsSession    remote       =  null;
    ReportsSessionBeanHome home        =  null;
    
    try
    {
      countryId         =   request.getParameter("countryId");
      locationId        =   request.getParameter("locationId");
      salesPersonId     =   request.getParameter("salesPersonCode");
      fromDate          =   request.getParameter("fromDate");
      toDate            =   request.getParameter("toDate");
      customerId        =   request.getParameter("customerId");
      status            =   request.getParameter("status");
      fromLocation      =   request.getParameter("fromLocation");
      fromCountry       =   request.getParameter("fromCountry");
      toLocation        =   request.getParameter("toLocation");  
      toCountry         =   request.getParameter("toCountry");
      shipmentMode      =   request.getParameter("shipmentMode");
      serviceLevelId    =   request.getParameter("serviceLevelId");
      
      reportDOB         =   new ReportsEnterIdDOB();
      
      reportDOB.setCountryId(countryId);
      reportDOB.setLocationId(locationId);
      reportDOB.setSalesPersonId(salesPersonId);
      reportDOB.setFromDate(fromDate);
      reportDOB.setToDate(toDate);
      reportDOB.setCustomerId(customerId);
      reportDOB.setQuoteStatus(status);
      reportDOB.setOrginLocation(fromLocation);
      reportDOB.setDestLocation(toLocation);
      reportDOB.setFromCountry(fromCountry);
      reportDOB.setToCountry(toCountry);
      reportDOB.setShipmentMode(shipmentMode);
      reportDOB.setServiceLevel(serviceLevelId);
      
      home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
		  remote	= (ReportsSession)home.create();
      
      if("getYieldDetails".equals(request.getParameter("Operation")))
        list    = remote.getYieldReportDetails(reportDOB);
      else
        list    = remote.getActivityReportDetails(reportDOB);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while setting yield report details");
      logger.error(FILE_NAME+"Error while setting yield report details");
      e.printStackTrace();
      throw new FoursoftException("An Error has occurred While Fetching Report Details.Please Try Again",e);
    }
    return list;
  }
  public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)throws IOException, ServletException
	{
		//Logger.info(FILE_NAME," In Dispatcher " + forwardFile );
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doFileDispatch() ", " Exception in forwarding ---> "+ ex.toString());
      logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}
  
  /**This private method set the dates by calculation 
   * 
   */
  private ReportsEnterIdDOB setFromdateAndToDate(ReportsEnterIdDOB reportsEnterIdDOB)throws Exception
  {
    
    String expiryActiveIndicator = "";
    String expiryActivePeriod    = "";
    String basis                 = "";
    int    basisCount            = 0;
    
    Timestamp fromDate            = null;//Timestamp.valueOf(reportsEnterIdDOB.getCurrentDate());
    Timestamp toDate              = null;//Timestamp.valueOf(reportsEnterIdDOB.getCurrentDate());
    long  totTime                 = 0L;
    long  multiFactor             = 86400000L;
    String fromDateStr  = null;
    String toDateStr    = null;
    ESupplyDateUtility  dateFormatter = new ESupplyDateUtility();
    Calendar calendar;
    
    try
    {
      
      dateFormatter.setPatternWithTime(reportsEnterIdDOB.getDateFormat());
      Timestamp currentDate         = new ESupplyDateUtility().getTimestamp("DD/MM/YY",reportsEnterIdDOB.getCurrentDate());   //Timestamp.valueOf(reportsEnterIdDOB.getCurrentDate());
      
      String currentDateStr                = currentDate.toString();
      
      if(reportsEnterIdDOB!=null)
      {
        expiryActiveIndicator = reportsEnterIdDOB.getExpiryActiveIndicator();
        expiryActivePeriod    = reportsEnterIdDOB.getExpiryActivePeriod();
        basis                 = reportsEnterIdDOB.getBasis();
        basisCount            = reportsEnterIdDOB.getBasisCount();
        //@@Commented and Added by subrahmanyam for the pbn id: 202840 on 14-Apr-010
        //if("Years".equalsIgnoreCase(expiryActiveIndicator))
        if("Years".equalsIgnoreCase(expiryActivePeriod))
        {
          basisCount  = basisCount*12;
        }
        
          if(expiryActivePeriod.equals("Days") || expiryActivePeriod.equals("Weeks"))
          {
            if(expiryActivePeriod.equals("Weeks"))
              { totTime = multiFactor*basisCount*7;}
            else
              {
                totTime = multiFactor*basisCount;
              }
            if(basis.equals("<="))
            {
              if(expiryActiveIndicator.equals("Y"))
                {
                  fromDate  = currentDate;
                  toDate = new Timestamp(currentDate.getTime()+totTime);
//                  toDate.setTime(currentDate.getTime()+totTime);
                }else
                {
//                  fromDate.setTime(currentDate.getTime()-totTime);
                  fromDate = new Timestamp(currentDate.getTime()-totTime);
                  toDate  = currentDate;
                }
              //toDate    = currentDate;
            }else if(basis.equals(">="))
            {
              if(expiryActiveIndicator.equals("Y"))
                {      
//                  fromDate.setTime(currentDate.getTime()+totTime);
                  fromDate = new Timestamp(currentDate.getTime()+totTime);
                  toDate  = null;
                }else
                {
                  fromDate  = null;
//                  toDate.setTime(currentDate.getTime()-totTime);
                    toDate = new Timestamp(currentDate.getTime()-totTime);
                }
            }else if(basis.equals("="))
            {
              //currentDate.setTime(currentDate.getTime()+totTime);
              //fromDate  = currentDate.getDate();
              if(expiryActiveIndicator.equals("Y"))
                {        
//                  fromDate.setTime(currentDate.getTime()+totTime);
//                  toDate.setTime(currentDate.getTime()+totTime); 
                    fromDate = new Timestamp(currentDate.getTime()+totTime);
                    toDate = new Timestamp(currentDate.getTime()+totTime);                    
                }else
                {
//                  fromDate.setTime(currentDate.getTime()-totTime);
//                  toDate.setTime(currentDate.getTime()-totTime);    
                    fromDate = new Timestamp(currentDate.getTime()-totTime);
                    toDate = new Timestamp(currentDate.getTime()-totTime);       
                }
                
                
                
                calendar = Calendar.getInstance();
                fromDateStr = fromDate.toString();
                calendar.set(Integer.parseInt(fromDateStr.substring(0,4)),(Integer.parseInt(fromDateStr.substring(5,7))-1),Integer.parseInt(fromDateStr.substring(8,10)),0,0,0);
                fromDate = new Timestamp(calendar.getTimeInMillis()); 
                
                
                toDateStr = toDate.toString();
                calendar.set(Integer.parseInt(toDateStr.substring(0,4)),(Integer.parseInt(toDateStr.substring(5,7))-1),Integer.parseInt(toDateStr.substring(8,10)),23,59,59);
                toDate = new Timestamp(calendar.getTimeInMillis());                
                
                  /*fromDate.setHours(0);
                  fromDate.setMinutes(0);
                  fromDate.setSeconds(0);
                  toDate.setHours(23);
                  toDate.setMinutes(59);
                  toDate.setSeconds(59);*/
                  
            }
          }else if(expiryActivePeriod.equals("Month") || "Years".equalsIgnoreCase(expiryActivePeriod))        //@@Modified in place of "expiryActiveIndicator" "expiryActivePeriod" by subrahmanyam for the pbn id: 202840 on 14-Apr-010
          {
            calendar = Calendar.getInstance();
            
            calendar.set(Integer.parseInt(currentDateStr.substring(0,4)),(Integer.parseInt(currentDateStr.substring(5,7))-1),Integer.parseInt(currentDateStr.substring(8,10)));
            
            if(basis.equals("<="))
            {
              if(expiryActiveIndicator.equals("Y"))
                {        
                   calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH),calendar.get(calendar.DATE),0,0,0);
                   fromDate = new Timestamp(calendar.getTimeInMillis());
                   //fromDate  = currentDate;
 //                toDate.setMonth(toDate.getMonth()+basisCount);
                   //toDate = new Timestamp(currentDate.getMonth()+basisCount);
                   
                   calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH)+basisCount,calendar.get(calendar.DATE),23,59,59);
                   toDate = new Timestamp(calendar.getTimeInMillis());

                }else
                {
 //                 fromDate.setMonth(toDate.getMonth()-basisCount);
//                  fromDate = new Timestamp(currentDate.getMonth()-basisCount);
//                  toDate  = currentDate;      

                    calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH),calendar.get(calendar.DATE),23,59,59);
                    toDate  = new Timestamp(calendar.getTimeInMillis());
                    
                    calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH)-basisCount,calendar.get(calendar.DATE),0,0,0);
                    fromDate = new Timestamp(calendar.getTimeInMillis());
                    
                   
                }
            }else if(basis.equals(">="))
            {
              if(expiryActiveIndicator.equals("Y"))
                {      
//                  fromDate.setMonth(toDate.getMonth()+basisCount);
                  //fromDate = new Timestamp(currentDate.getMonth()+basisCount);
                  calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH)+basisCount,calendar.get(calendar.DATE),0,0,0);
                  fromDate = new Timestamp(calendar.getTimeInMillis());
                  toDate = null;
                }else
                {
                  fromDate  = null;
//                  toDate.setMonth(toDate.getMonth()-basisCount);
                  //toDate = new Timestamp(currentDate.getMonth()-basisCount);   
                  calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH)-basisCount,calendar.get(calendar.DATE),23,59,59);
                  toDate = new Timestamp(calendar.getTimeInMillis());
                  
                }
                
            }
			else
            {
              if(expiryActiveIndicator.equals("Y"))
                { 
//                  fromDate.setMonth(toDate.getMonth()+basisCount);
//                  toDate.setMonth(toDate.getMonth()+basisCount); 
//                 fromDate = new Timestamp(currentDate.getMonth()+basisCount);
//                  toDate = new Timestamp(currentDate.getMonth()+basisCount);   
                  calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH)+basisCount,calendar.get(calendar.DATE));
                  fromDate = new Timestamp(calendar.getTimeInMillis());
                  toDate = new Timestamp(calendar.getTimeInMillis());
                  
                }else
                {
//                  fromDate.setMonth(toDate.getMonth()-basisCount);
//                  toDate.setMonth(toDate.getMonth()-basisCount);   
//                  fromDate = new Timestamp(currentDate.getMonth()-basisCount);
//                  toDate = new Timestamp(currentDate.getMonth()-basisCount);   
                  calendar.set(calendar.get(calendar.YEAR),calendar.get(calendar.MONTH)-basisCount,calendar.get(calendar.DATE));
                  fromDate = new Timestamp(calendar.getTimeInMillis());
                  toDate = new Timestamp(calendar.getTimeInMillis());
                }
                
                calendar = Calendar.getInstance();
                fromDateStr = fromDate.toString();
                calendar.set(Integer.parseInt(fromDateStr.substring(0,4)),(Integer.parseInt(fromDateStr.substring(5,7))-1),Integer.parseInt(fromDateStr.substring(8,10)),0,0,0);
                fromDate = new Timestamp(calendar.getTimeInMillis()); 
                
                
                toDateStr = toDate.toString();
                calendar.set(Integer.parseInt(toDateStr.substring(0,4)),(Integer.parseInt(toDateStr.substring(5,7))-1),Integer.parseInt(toDateStr.substring(8,10)),23,59,59);
                toDate = new Timestamp(calendar.getTimeInMillis());                

                /*fromDate.setHours(0);
                fromDate.setMinutes(0);
                fromDate.setSeconds(0);
                toDate.setHours(23);
                toDate.setMinutes(59);
                toDate.setSeconds(59);       */
            }
            
          }
          reportsEnterIdDOB.setFromDate1(fromDate);
          reportsEnterIdDOB.setToDate1(toDate);
          
          if(reportsEnterIdDOB.getFromDate1()!=null)
              reportsEnterIdDOB.setFromDate(dateFormatter.getDisplayDateStringWithTime(reportsEnterIdDOB.getFromDate1(),reportsEnterIdDOB.getDateFormat()));
          
          if(reportsEnterIdDOB.getToDate1()!=null)
            reportsEnterIdDOB.setToDate(dateFormatter.getDisplayDateStringWithTime(reportsEnterIdDOB.getToDate1(),reportsEnterIdDOB.getDateFormat()));
          //Logger.info(FILE_NAME,"fromdate"+fromDate);
         // Logger.info(FILE_NAME,"todate"+toDate);
        
      }else
      {
        throw new Exception();
      }
    }catch(Exception e)
    {

      //Logger.info(FILE_NAME,"error while setting dates"+e);
      logger.info(FILE_NAME+"error while setting dates"+e);

      //Logger.info(FILE_NAME,"error while setting dates"+e);
      e.printStackTrace();

      throw new Exception();
    }
    logger.info("From Date..."+reportsEnterIdDOB.getFromDate());
    logger.info("From Date1..."+reportsEnterIdDOB.getFromDate1());
    logger.info("To Date..."+reportsEnterIdDOB.getToDate());
    logger.info("To Date1..."+reportsEnterIdDOB.getToDate1());
    return reportsEnterIdDOB;
  }

/**
 * This private method used for checking the checked values for reports
 * 
 */
 private void doPageValues(HttpServletRequest request, HttpServletResponse response,ArrayList dataList)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    String[]                  mfValues                          =   null;
    String[]                  checkValue                        =   null;
    String[]				  checkInActiveValue				=	null;//Added by Anil.k on 28Feb2011
    String[]				  InActiveValue						=	null;//Added by Anil.k on 28Feb2011
    ArrayList                 listValues                        =   null;
    ArrayList                 accList                           =   null;
    ArrayList                 dobList                           =   null;
    ReportDetailsDOB          reportDetailsDOB                  =   null;
    HashMap                   mapDob                            =   null;
    String                    val                               =   null;
    Timestamp                 validdate                         =   null;
    ESupplyGlobalParameters   loginbean                         =   null;
    try
    {
        mfValues        =   request.getParameterValues("mfValues");
        checkValue      =   request.getParameterValues("checkValue");
        InActiveValue        =   request.getParameterValues("inActiveValues");//Added by Anil.k on 28Feb2011
        checkInActiveValue   =   request.getParameterValues("inActiveCheckValue");//Added by Anil.k on 28Feb2011
        mapDob			    =	  (HashMap)session.getAttribute("HashList");
        loginbean       =   (ESupplyGlobalParameters)session.getAttribute("loginbean");
      
        if(mapDob==null)
          mapDob    =   new HashMap();
        if(checkValue!=null)
        {
          
           int   checkValuelength  = checkValue.length;
           String   hiddenChecked  =  null;
           String   hiddenInActiveChecked  =  null;//Added by Anil.k on 28Feb2011
            for(int j=0;j<checkValuelength;j++)
            {
            
              hiddenChecked = checkValue[j];
              hiddenInActiveChecked = checkInActiveValue!=null ? checkInActiveValue[j]:"";
              if(mfValues!=null || InActiveValue!=null)//Added by Anil.k on 28Feb2011
              {
                int mfValuesLength  = mfValues!=null?mfValues.length:InActiveValue.length;//Added by Anil.k on 28Feb2011
                boolean checkflag     = false;
                boolean checkInActiveflag     = false;
                for(int i=0;i<mfValuesLength;i++)
                {
                  if(mfValues!=null && hiddenChecked.equals(mfValues[i]))
                  {
                    val=request.getParameter(hiddenChecked)!=null?request.getParameter(hiddenChecked):"";
                    checkflag = true;
                    break;
                  }            
                  else if(InActiveValue!=null && hiddenInActiveChecked.equals(InActiveValue[i]))//Added by Anil.k on 28Feb2011
                  {
                    val=request.getParameter(hiddenInActiveChecked)!=null?request.getParameter(hiddenInActiveChecked):"";
                    checkInActiveflag = true;
                    break;
                  }//Ended by Anil.k on 28Feb2011
                }
                
               reportDetailsDOB   = (ReportDetailsDOB)dataList.get(j);
               if(checkflag)
               {
                  mapDob.remove(hiddenChecked);
                  if(val!=null && val.trim().length()!=0)
                  {
                  validdate=new ESupplyDateUtility().getTimestampWithTime(loginbean.getUserPreferences().getDateFormat(),val,"23:59:59");
                  reportDetailsDOB.setValidUpto(validdate);  
                  reportDetailsDOB.setDateFormat(loginbean.getUserPreferences().getDateFormat());
                  }
                  mapDob.put(hiddenChecked,reportDetailsDOB);
        
                }
               else if(checkInActiveflag)//Added by Anil.k on 28Feb2011
               {
                  mapDob.remove(hiddenChecked);
                  reportDetailsDOB.setInActive("inActive");
                  /*if(val!=null && val.trim().length()!=0)
                  {
                  validdate=new ESupplyDateUtility().getTimestampWithTime(loginbean.getUserPreferences().getDateFormat(),val,"23:59:59");
                  reportDetailsDOB.setValidUpto(validdate);*/  
                  reportDetailsDOB.setDateFormat(loginbean.getUserPreferences().getDateFormat());
                  //}
                  mapDob.put(hiddenInActiveChecked,reportDetailsDOB);
        
                }//Ended by Anil.k on 28Feb2011
                else
                {
                  mapDob.remove(hiddenChecked);
                }
              }
              else
              {
                mapDob.remove(hiddenChecked);
              }
          }
          session.setAttribute("HashList",mapDob);
      }
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("An Error Has Occurred While Paging. Please Try Again.","QMSReportController?Operation="+request.getParameter("Operation")+"&subOperation="+request.getParameter("subOperation")); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation","")); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
 private ArrayList dogetFinalValues(HttpServletRequest request, HttpServletResponse response, HashMap updatedMapValue)throws IOException,ServletException
 {
      ErrorMessage			        errorMessageObject                =   null;
      ArrayList			            keyValueList	                    =   new ArrayList();
      ArrayList    finalDataList = new ArrayList();
      try
      {
        java.util.Set setKeys	  =	updatedMapValue.keySet();
        java.util.Iterator	quoteIds	=	setKeys.iterator();
        int a=0;
        while(quoteIds.hasNext())
        {
            String updatequotes	=	(String)quoteIds.next();
            ReportDetailsDOB  detailsDOB		  = (ReportDetailsDOB)updatedMapValue.get(updatequotes);
            finalDataList.add(detailsDOB);
        }
       }
        catch(Exception e)
        {
            e.printStackTrace();
            errorMessageObject = new ErrorMessage("Error while dogetFinalValues()",""); 
            keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
            keyValueList.add(new KeyValue("Operation","")); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject);
            doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
        return finalDataList;
 }
 
 private void doPageValuesForUpdatedQuotes(HttpServletRequest request, HttpServletResponse response,ArrayList dataList)throws FoursoftException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    String[]                  mfValues                          =   null;
    String[]                  checkValue                        =   null;
    String[]				  dontModify						=	null;//Added by Anil.k for CR 231104
    ArrayList                 listValues                        =   null;
    ArrayList                 accList                           =   null;
    ArrayList                 dobList                           =   null;
    //ReportDetailsDOB          reportDetailsDOB                  =   null;
    UpdatedQuotesReportDOB    reportDOB                         =   null;
    HashMap                   mapDob                            =   null;
    String                    val                               =   null;
    Timestamp                 validdate                         =   null;
    try
    {
        mfValues        =   request.getParameterValues("mfValues");
        checkValue      =   request.getParameterValues("checkValue");
        dontModify		=	request.getParameterValues("dontModifyValue");//Added by Anil.k for CR 231104
        mapDob			    =	  (HashMap)session.getAttribute("HashList");
        if(mapDob==null)
          mapDob    =   new HashMap();
        if(checkValue!=null)
        {
           int   checkValuelength  = checkValue.length;
           String   hiddenChecked  =  null;
            for(int j=0;j<checkValuelength;j++)
            {
            
              hiddenChecked = checkValue[j];              
              if(mfValues!=null)
              {
                int mfValuesLength  = mfValues.length;
                boolean checkflag     = false;
                for(int i=0;i<mfValuesLength;i++)
                {
                  if(hiddenChecked.equals(mfValues[i]))
                  {
                    checkflag = true;
                    break;
                  }
                 
                }
                
               reportDOB   = (UpdatedQuotesReportDOB)dataList.get(j);
             //Added by Anil.k for CR 231104
               if(dontModify!=null)
               {
            	   int dontModifyLength  = dontModify.length;
            	   for(int i=0;i<dontModifyLength;i++)
                   {
                     if(hiddenChecked.equals(dontModify[i]))
                     {
                    	 reportDOB.setDontModify("Checked");
                         break;
                     }
                    
                   }
               }//Ended by Anil.k for CR 231104 on 31Jan2011               
               if(checkflag)
               {
                  mapDob.remove(hiddenChecked);
                  mapDob.put(hiddenChecked,reportDOB);
               
                  
                }
                else
                {
                  mapDob.remove(hiddenChecked);
                }
              }
              else
              {
                mapDob.remove(hiddenChecked);
              }
          }
          session.setAttribute("HashList",mapDob);
      }
    }
    catch(Exception e)
    {
        session.removeAttribute("HashList");
        session.removeAttribute("reportFinalDOB");
        //Logger.error(FILE_NAME,"Error in doPageValuesForUpdatedQuotes "+e);
        logger.error(FILE_NAME+"Error in doPageValuesForUpdatedQuotes "+e);
        e.printStackTrace();
        throw new FoursoftException("An Error Has Occurred While Paging. Please Try Again.",e);
    }
}
 
 
 
 private ArrayList doGetFinalUpdatedList(HttpServletRequest request, HttpServletResponse response, HashMap updatedMapValue)throws FoursoftException
 {
      ErrorMessage			        errorMessageObject    = null;
      ArrayList			            keyValueList	        = new ArrayList();
      ArrayList                 finalDataList         = new ArrayList();
      String                    updatequotes	        = null;
      UpdatedQuotesReportDOB    reportDOB             = null;
      ArrayList                updatedQuotesList     = null;
     ArrayList                quotesList             = new ArrayList();
      QMSQuoteSessionHome      home                  = null;
      QMSQuoteSession          remote                = null;
      String                  errorMessage           = null;
      String                  errorCode              = null;
       String                  operation             = null;
      String                  fromWhat               = null;
      HttpSession            session                 = null;
      String                nextNavigation           = null;
      ArrayList             list                     = new ArrayList();
      String emailCheck     = null;
        ArrayList             emailList                     = new ArrayList();
        ArrayList             finalList                     = new ArrayList();  
      try
      {
        Set       setKeys	  =	updatedMapValue.keySet();
        Iterator	quoteIds	=	setKeys.iterator();
         home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
         remote      = home.create();
       session       = request.getSession();
      
            while(quoteIds.hasNext())
        {
            updatequotes	=	(String)quoteIds.next();
        
            reportDOB		  = (UpdatedQuotesReportDOB)updatedMapValue.get(updatequotes);
              //@@Added by Kameswari for the WPBN issue-142381 - on 23/10/08
             
            emailCheck    = request.getParameter("emailCheck"+updatequotes);
         
            if("On".equalsIgnoreCase(emailCheck))
            {
               emailList.add(updatequotes);
            }
          
             //@@23/10/08
             
            finalDataList.add(reportDOB);
          
        }
      
            finalList.add(finalDataList);
         
             
              finalList.add(emailList);  //@@Added by Kameswari for the enhancement requested by Karen(NZ) - on 23/10/08
             
              

       }
        catch(Exception e)
        {
            //Logger.error(FILE_NAME,"Error in doGetFinalUpdatedList "+e);
            logger.error(FILE_NAME+"Error in doGetFinalUpdatedList "+e);
            e.printStackTrace();
            throw new FoursoftException("An Exception Has Occurred While Updating Quote(s). Please Try Again.",e);
        }
       // return finalDataList;
       return finalList;
 }


/**
 * This private method used for checking the checked values for reports
 * 
 */
 private void doEscalatedPageValues(HttpServletRequest request, HttpServletResponse response,ArrayList dataList,ESupplyGlobalParameters loginbean)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    String[]                  mfValues1                         =   null;
    String[]                  mfValues2                         =   null;
    String[]                  checkValue1                       =   null;
    String[]                  checkValue2                       =   null;
    String[]				  status_Reasons					= 	null; 	//Added by Kishore For statusReason
    String					  statusReason						= 	null;	
    boolean					  tempStatus						= 	true;
    String					  tempQuoteId						=	null;
    String					  tempStatusReason					= 	null;
    ReportDetailsDOB          reportDetailsDOB                  =   null;
    HashMap                   mapDob1                           =   null;
    HashMap                   mapDob2                           =   null;
    String                    valA                              =   null;
    String                    valB                              =   null;
    Timestamp                 validdate                         =   null;
    try
    {
    
        //Logger.info(FILE_NAME,"dataListdataList::"+dataList.size());
        mfValues1        =   request.getParameterValues("mfValues1");
        mfValues2        =   request.getParameterValues("mfValues2");
        checkValue1      =   request.getParameterValues("checkValue1");
        checkValue2      =   request.getParameterValues("checkValue2");
        mapDob1			    =	  (HashMap)session.getAttribute("HashList1");
        mapDob2			    =	  (HashMap)session.getAttribute("HashList2");
        status_Reasons		= request.getParameterValues("statusReason");		//Added by Kishore For statusReason
        if(mapDob1==null)
          mapDob1    =   new HashMap();
        if(mapDob2==null)
          mapDob2    =   new HashMap();
    
        //Logger.info(FILE_NAME,"checkValue1checkValue1::"+checkValue1.length);
      
        if(checkValue1!=null)
        {
           int   checkValuelength  = checkValue1.length;
           String   hiddenChecked  =  null;
            for(int j=0;j<checkValuelength;j++)
            {
            
              hiddenChecked = checkValue1[j];
              if(mfValues1!=null)
              {
                int mfValuesLength  = mfValues1.length;
                boolean checkflag     = false;
                for(int i=0;i<mfValuesLength;i++)
                {
                  if(hiddenChecked.equals(mfValues1[i]))
                  {
                    valA=request.getParameter(hiddenChecked+"A")!=null?request.getParameter(hiddenChecked+"A"):"";
                    valB=request.getParameter(hiddenChecked+"B")!=null?request.getParameter(hiddenChecked+"B"):"";
                 
                    //Added by Kishore For statusReason	
                   //if("H".equals(loginbean.getUserTerminalType())){ 
	                    if(tempStatus){
	                  	  tempQuoteId = hiddenChecked;
	                  	  tempStatusReason =(status_Reasons)!=null?status_Reasons[j]:"";//added by silpa.p on 7-06-11 for approved quotes updates
	                  	  tempStatus = false;
	                    }
	                    
	                    if(tempQuoteId.equals(hiddenChecked))
	                  	  statusReason	= tempStatusReason;
	                    else{
	                      statusReason =(status_Reasons)!=null?status_Reasons[j]:"";//added by silpa.p on 7-06-11 for approved quotes updates
	                      String temp	= "";
	                  	  //@@Modified by kiran.v on 27/07/2011 for Wpbn Issue- 259113
	                      if(j<checkValuelength-1 && !tempQuoteId.equals(checkValue1[j+1]))
	                  		  tempStatus = true;
	                    }
	                 
                   //} End Of Kishore For statusReason
                    	
                    checkflag = true;
                    break;
                  }            
                }
                
               reportDetailsDOB   = (ReportDetailsDOB)dataList.get(j);
               if(checkflag)
               {
                  mapDob1.remove(hiddenChecked);
                  if(valA!=null)
                      reportDetailsDOB.setInternalRemarks(valA);  
                  if(valB!=null)
                      reportDetailsDOB.setExternalRemarks(valB);
                      
                  reportDetailsDOB.setQuoteStatus("APP");
                  reportDetailsDOB.setUserId(loginbean.getUserId());
                  
                  reportDetailsDOB.setStatusReason(statusReason);	//Added by Kishore For statusReason
                                    
                  mapDob1.put(hiddenChecked,reportDetailsDOB);
                }
                else
                {
                  mapDob1.remove(hiddenChecked);
                }
              }
              else
              {
                mapDob1.remove(hiddenChecked);
              }
          }
          session.setAttribute("HashList1",mapDob1);
      }
      if(checkValue2!=null)
        {
           int   checkValuelength  = checkValue2.length;
           String   hiddenChecked  =  null;
           
           statusReason						= 	null;	
           tempStatus						= 	true;
           tempQuoteId						=	null;
           tempStatusReason					= 	null;
          
            for(int j=0;j<checkValuelength;j++)
            {
            
              hiddenChecked = checkValue2[j];
              if(mfValues2!=null)
              {
                int mfValuesLength  = mfValues2.length;
                boolean checkflag     = false;
                for(int i=0;i<mfValuesLength;i++)
                {
                  if(hiddenChecked.equals(mfValues2[i]))
                  {
                    valA=request.getParameter(hiddenChecked+"A")!=null?request.getParameter(hiddenChecked+"A"):"";
                    valB=request.getParameter(hiddenChecked+"B")!=null?request.getParameter(hiddenChecked+"B"):"";
                  
                    //Added by Kishore For statusReason
                   // if("H".equals(loginbean.getUserTerminalType())){ 
	                    if(tempStatus){
	                  	  tempQuoteId = hiddenChecked;
	                  	  tempStatusReason =(status_Reasons)!=null?status_Reasons[j]:"";//added by silpa.p on 7-06-11 for approved quotes updates
	                  	  tempStatus = false;
	                    }
	                    
	                    if(tempQuoteId.equals(hiddenChecked))
	                  	  statusReason	= tempStatusReason;
	                    else{
	                      statusReason =(status_Reasons)!=null?status_Reasons[j]:"";//added by silpa.p on 7-06-11 for approved quotes updates
	                      
	                      if(j<checkValuelength-1 && !tempQuoteId.equals(checkValue2[j+1]))
	                    	  tempStatus = true;
	                    }
                    
                  // } End Of Kishore For statusReason
                    
                   checkflag = true;
                    break;
                  }            
                }
                
               reportDetailsDOB   = (ReportDetailsDOB)dataList.get(j);
               if(checkflag)
               {
                  mapDob2.remove(hiddenChecked);
                  if(valA!=null)
                     reportDetailsDOB.setInternalRemarks(valA);
                  if(valB!=null)
                     reportDetailsDOB.setExternalRemarks(valB);
                     
                     reportDetailsDOB.setQuoteStatus("REJ");
                     reportDetailsDOB.setUserId(loginbean.getUserId());
 
                     reportDetailsDOB.setStatusReason(statusReason);	//Added by Kishore For statusReason
 
                  mapDob2.put(hiddenChecked,reportDetailsDOB);
                }
                else
                {
                  mapDob2.remove(hiddenChecked);
                }
              }
              else
              {
                mapDob2.remove(hiddenChecked);
              }
          }
          session.setAttribute("HashList2",mapDob2);
    }
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doPageVlues()",""); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation","")); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}


private String doGeneratePDF(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginbean,ArrayList dataList,String fromaddress) throws ServletException
{
    
    int                     updateDataListSize  = 0;
    String                  toAddress           = "";
    
    ReportsSession          remote              = null;
    ReportsSessionBeanHome  home                = null;
    File[]                  fArr                = null;
    String                  successFlag         = null;
  try
  {
    updateDataListSize     =      dataList.size();
    
    for(int i=0;i<updateDataListSize;i++)
    {
      detailsDOB = (ReportDetailsDOB)dataList.get(i);
      toAddress        = detailsDOB.getOperEmailId();
      Document document = new Document(PageSize.A4.rotate()); 
      String PDF_FILE_NAME = "ApprovedQuote.pdf";
      document.addTitle("Approved Quote");
      document.addSubject("Report PDF");
      document.addKeywords("Test, Key Words");
      document.addAuthor(loginbean.getUserId());
      document.addCreator("Auto Generated through 4S DHL");
      document.addCreationDate();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PdfWriter writer	=	PdfWriter.getInstance(document, baos);
      document.open();
      Table mainT = new Table(3);  
      mainT.setWidth(100);
      // vanishing table border
       mainT.setBorderWidth(1);
       mainT.setBorderColor(Color.white);
       mainT.setPadding(1);
       mainT.setSpacing(1);
       
       Phrase customerP = new Phrase(""+"Approved Details Report",FontFactory.getFont("ARIAL", 15, Font.BOLD));
       //customerP.add(new Phrase(" :"+"Approved Details Report",FontFactory.getFont("ARIAL", 9, Font.BOLD)));
  
      Cell cell3 = new Cell(customerP);
      cell3.setBorderColor(new Color(255,255, 255));
      mainT.addCell(cell3);
   
  
      Phrase dateP = new Phrase("DATE ",FontFactory.getFont("ARIAL",10, Font.BOLD));
      dateP.add(new Phrase(" :",FontFactory.getFont("ARIAL", 9, Font.BOLD)));
      Cell cell = new Cell(dateP);
      cell.setBorderColor(new Color(255,255, 255));
      mainT.addCell(cell);
      
      document.add(mainT);
      
      Table partT = new Table(15,dataList.size()); 
      partT.setBorderWidth(1);
      partT.setBorderColor(Color.black);
      partT.setPadding(1);
      partT.setSpacing(1);
      partT.setAutoFillEmptyCells(true);
      partT.setTableFitsPage(true);
      
      partT.setWidth(100.0f);
      int[] width = {15,10,10,10,10,10,10,10,10,10,12,10,10,10,10};
      partT.setWidths(width);
      
      Chunk chk = new Chunk("CUSTOMER ID",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);
    cell.setWidth("100");
    cell.setNoWrap(true); 
    cell.setLeading(10.0f);           
    partT.addCell(cell);                    
  
    chk = new Chunk("QUOTE ID",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);
    cell.setWidth("200");cell.setLeading(10.0f); 
    partT.addCell(cell);
  
    chk = new Chunk("SHIPMENT MODE",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("SERVICE LEVEL",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("FROM COUNTRY",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("FROM LOCATION",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("TO COUNTRY",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("TO LOCATION",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("DUE DATE",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("DUE TIME",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("APPROVED BY",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("APPROVED DATE",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("APPROVED TIME ",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("INTERNAL NOTES",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    chk = new Chunk("EXTERNAL NOTES",FontFactory.getFont("ARIAL", 9, Font.BOLD));
    cell = new Cell(chk);
    cell.setHeader(true);cell.setLeading(10.0f); 
    partT.addCell(cell);
    
    partT.endHeaders();
    
      detailsDOB  = (ReportDetailsDOB)dataList.get(i);
      chk = new Chunk(""+detailsDOB.getCustomerId(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getQuoteId(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getShipmentMode(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getServiceLevel(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getFromCountry(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getFromLocation(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getToCountry(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getToLocation(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getDueDateNTime(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getDueDateNTime(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getApprovedRrejectedBy(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getApprovedRrejectedDtNtime(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getApprovedRrejectedDtNtime(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getInternalRemarks(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      
      chk = new Chunk(""+detailsDOB.getExternalRemarks(),FontFactory.getFont("ARIAL", 9));
      cell = new Cell(chk);  cell.setLeading(10.0f);         
      partT.addCell(cell);
      //added by rk
      //This is for sending FAX message and delivery confirmation on my Mail ID.
      //StringBuffer toAddress = new StringBuffer("ifax#");
      //toAddress.append("914023100602");
      //toAddress.append("@tcdhl.com");
      
      
      document.add(partT);            
      document.newPage();
      document.close();
      // write ByteArrayOutputStream to the ServletOutputStream            
      /*ServletOutputStream sout = response.getOutputStream();
      baos.writeTo(sout);
      sout.flush();*/
      //dataList  = (ArrayList)  remote.sendEmail(dataList);
      File f = new File("ApprovedQuote.pdf");
      FileOutputStream  fileOutputStream= new FileOutputStream(f);
      baos.writeTo(fileOutputStream);
      fArr = new File[1];
      fArr[0] = f;
     home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
     remote	= (ReportsSession)home.create();
   
     remote.sendMail(fromaddress,toAddress,"Approved Report","ApprovedQuote.pdf",fArr);
    
      successFlag = "success";
    }
      
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error while generating PDF"+e);
    logger.error(FILE_NAME+"Error while generating PDF"+e);
    e.printStackTrace();
    successFlag   = "Error";
    throw new ServletException(e.toString());
  }
     return successFlag;      
            
  }
  
  private ArrayList getUpdatedQuotes(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginbean) throws FoursoftException
  {
    ReportsSession         remote      =  null;
    ReportsSessionBeanHome home        =  null;
    ArrayList              list        =  null;
    
    String                 userId      =  null;
    String                 empId       =  null;
    
    try
    {
      home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
		  remote	= (ReportsSession)home.create();
      
      if("activitySummary".equalsIgnoreCase(request.getParameter("subOperation")))
      {
          userId    =   loginbean.getUserId();
          empId     =   loginbean.getEmpId();
      }
      
      list    = remote.getUpdatedQuotes(loginbean.getTerminalId(),userId,empId);
      
      //Logger.info(FILE_NAME,"getUpdatedQuotes::"+list);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in Controller in getUpdatedQuotes::"+e.toString());
      logger.error(FILE_NAME+"Error in Controller in getUpdatedQuotes::"+e.toString());
      e.printStackTrace();
      throw new FoursoftException("An Error has Ocuured While Fetching Updated Quotes. Please Try Again",e);
    }
    return list;
  }
  
  private ArrayList getUpdatedQuotesReport(String changeDesc,int pageNo,String sortBy,String sortOrder,String terminalId,String repFormat,String fromWhere,ESupplyGlobalParameters loginbean) throws FoursoftException
  {
    ReportsSession         remote      =  null;
    ReportsSessionBeanHome home        =  null;
    ArrayList              list        =  null;
    String                 userId      =  null;
    String                 empId       =  null;
    
    try
    {
      home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
		  remote	= (ReportsSession)home.create();
      
      if("summaryReport".equalsIgnoreCase(fromWhere))
      {
          userId  = loginbean.getUserId();
          empId   = loginbean.getEmpId();
      }
      
     list    = remote.getUpdatedQuotesReportDetails(changeDesc,pageNo,sortBy,sortOrder,terminalId,repFormat,userId,empId);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while fetching Report Details"+e);
      logger.error(FILE_NAME+"Error while fetching Report Details"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error has Occured While Fetching Report Details. Please Try Again",e);
    }
    return list;
  }
  private ArrayList getUpdatedQuotesDetails(UpdatedQuotesFinalDOB reportFinalDOB,ESupplyGlobalParameters loginbean)  throws FoursoftException
  {
    QMSQuoteSessionHome     home                 = null;
    QMSQuoteSession         remote               = null;
    ArrayList               quoteFinalDOBList    = null;
    try
    {
      //lookUpBean  = new LookUpBean();
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      quoteFinalDOBList    = remote.getUpdatedQuoteDetails(reportFinalDOB,loginbean);     
    }    
    catch(Exception ex)
		{
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [error in getChargesForUpdatedQuotes()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [error in getChargesForUpdatedQuotes()] -> "+ex.toString());
      throw new FoursoftException("An Error Has Occurred While Updating Quote. Please Try Again.",ex);
		}
    return quoteFinalDOBList;
  }
 private ArrayList doPDFGeneration(QuoteFinalDOB finalDOB,HttpServletRequest request,HttpServletResponse response)throws Exception
  {
      int mailFlag   = 1;
    int faxFlag    = 0;
    int returnFlag = 3;
    String[] contents = null;
    String[] levels   = null;
    String[] aligns   = null;
    String[] headFoot = null;
    ArrayList sentMailList    =   new ArrayList();
    ArrayList unsentMailList  =   new ArrayList();
    ArrayList sentFaxList     =   new ArrayList();
    ArrayList unsentFaxList   =   new ArrayList();
    ArrayList returnList      =   new ArrayList();
    
    ArrayList                 keyList           = new ArrayList();
    int                       keySize           = 0;
    boolean                   isMultiModal      = false;
    int m =0;
    String breakPoint = null;
    String transitTime = null;
    HttpSession  session  = null;
      //@@Added by Kameswari for the WPBN issue-146448

  String shipmentMode = null;
    QuoteCharges       chargesDOB			 = null;
    ArrayList          freightCharges  = null;
 QuoteFreightLegSellRates legCharges   = null;
     String		         str1[]				   = null;
     ArrayList         frequency       = new ArrayList();
     ArrayList         carrier         = new ArrayList();
     ArrayList         transittime     = new ArrayList();
     ArrayList         ratevalidity    = new ArrayList();
     ArrayList         frequency_o     = new ArrayList();
     ArrayList         frequency_d     = new ArrayList();
      ArrayList        carrier_o       = new ArrayList();
     ArrayList         carrier_d       = new ArrayList();
      ArrayList        transit_o       = new ArrayList();
     ArrayList         transit_d       = new ArrayList();
      ArrayList        validity_o      = new ArrayList();
     ArrayList         validity_d      = new ArrayList();
     int               size            = 0;
    //@@WPBN issue-146448
    try
    {       
          //System.out.println("PDF Generation Startd----------------------------------->");
            DecimalFormat df  = new DecimalFormat("##,###,##0.00");
            QuoteHeader    headerDOB		          =	 finalDOB.getHeaderDOB();
            QuoteMasterDOB masterDOB              =   finalDOB.getMasterDOB();
            ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
            ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
            eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
            String[] strDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
            String[] effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
            String[] validDate ;
            String   validUptoStr = null;
            if(headerDOB.getValidUpto()!=null){
                validDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getValidUpto());
                validUptoStr = validDate[0];
            }
            
            StringBuffer   attentionTo            =   new StringBuffer("");
       
            if(masterDOB.getCustContactNames()!=null)
            {
              for(int i=0;i<masterDOB.getCustContactNames().length;i++)
              {
                attentionTo.append(masterDOB.getCustContactNames()[i]!=null?masterDOB.getCustContactNames()[i]:"");
                if(i!=(masterDOB.getCustContactNames().length-1))
                  attentionTo.append(",");
              }
            }
           
            Document document = new Document(PageSize.A4,54f,54f,68.4f,68.4f);//@@ 36 points represent 0.5 inch
            String PDF_FILE_NAME = "Approved.pdf";
            document.addTitle("Approved Report");
            document.addSubject("Report PDF");
            document.addKeywords("Test, Key Words");
            document.addAuthor("Rama Krishna");
            document.addCreator("Auto Generated through 4S DHL");
            document.addCreationDate();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	          PdfWriter writer	=	PdfWriter.getInstance(document, baos);            
            document.open();
            //document.setMargins(15,15,15,15);            
            // Draw a rectangle inside the page's margins.
            //PdfContentByte cb = writer.getDirectContent ();
            //cb.rectangle (document.left (), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
            //cb.stroke ();
            int[] widths = {12,12,12,12,12,12,28};
            
            Table mainT = new Table(7);  
            mainT.setWidth(100);                     
            mainT.setWidths(widths);
            mainT.setBorderColor(Color.white);
            mainT.setPadding(1);
            mainT.setSpacing(0);
                         
              
            Phrase  headingPhrase    =  new Phrase("",FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            Cell cellHeading = new Cell(headingPhrase);	
            cellHeading.setBorderColor(new Color(255,255, 255));
            cellHeading.setHorizontalAlignment(cellHeading.ALIGN_CENTER);
            cellHeading.setColspan(6);
            mainT.addCell(cellHeading);
            
            Cell imageCell = new Cell();            
            java.net.URL url =  getServletConfig().getServletContext().getResource("/images/DHLlogo.gif");
            Image img0 = Image.getInstance(url);           
            imageCell.add(img0);            
            imageCell.setHorizontalAlignment(imageCell.ALIGN_RIGHT);
            imageCell.setBorderWidth(0);
            imageCell.setNoWrap(true);
            mainT.addCell(imageCell);
            mainT.setAlignment(mainT.ALIGN_CENTER);
            document.add(mainT);  
            
            //System.out.println("After Image && Before Content--------------------------->");
                                   
            Table partCountry = new Table(1,6); 
            partCountry.setBorderWidth(0);
            partCountry.setWidth(100);
            partCountry.setBorderColor(Color.black);            
            partCountry.setPadding(1);
            partCountry.setSpacing(0);
            partCountry.setAutoFillEmptyCells(true);
            partCountry.setTableFitsPage(true);
            partCountry.setAlignment(partCountry.ALIGN_CENTER);
            partCountry.setBorderWidth(0);;
            Cell cellCountry;
              if(!finalDOB.isMultiModalQuote())
            {
               if(finalDOB.getMasterDOB().getShipmentMode()==1)
          {	
            shipmentMode = "AIR FREIGHT PROPOSAL";
            transitTime  ="Approximate Transit Time";
           }
               else if(finalDOB.getMasterDOB().getShipmentMode()==2)
            {
              shipmentMode = "SEA FREIGHT PROPOSAL";
              transitTime  ="Approximate Transit Days";
            }
               else if(finalDOB.getMasterDOB().getShipmentMode()==4)
			  {
				    shipmentMode = "TRUCK FREIGHT PROPOSAL";
                    transitTime  ="Approximate Transit Time";
			  }
            }
            else
            {
                   shipmentMode  = " MULTI-MODAL FREIGHT PROPOSAL ";
				   transitTime  ="Approximate Transit time and Days";
				 
            }  
               ArrayList charges				                      = finalDOB.getLegDetails();
            int       chargesSize			                    = charges.size();
            
                    //@@Added by Kameswari for the WPBN issue-146448 on 03/12/08
         for(int i=0;i<chargesSize;i++)
        {
          legCharges	   = (QuoteFreightLegSellRates)charges.get(i);
      
      
          freightCharges = legCharges.getFreightChargesList();
          //@@Added by kiran.v on 26/07/2011 for Wpbn Issue-255976
          if(freightCharges!=null)
          {	  
          chargesDOB			= (QuoteCharges)freightCharges.get(0);
          if(chargesDOB.getValidUpto()!=null)
          {
            str1					  =eSupplyDateUtility.getDisplayStringArray(chargesDOB.getValidUpto());
          }
            ///////////////////////////////////////////Second Table////////////////////////////
            if(chargesSize>1)
           {
               if("Y".equalsIgnoreCase(chargesDOB.getFrequencyChecked())||"on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
               {
                  frequency.add(chargesDOB.getFrequency()); 
                  frequency_o.add(legCharges.getOrigin());
                  frequency_d.add(legCharges.getDestination());
               }
                if("Y".equalsIgnoreCase(chargesDOB.getTransitTimeChecked())||"on".equalsIgnoreCase(chargesDOB.getTransitTimeChecked()))
               {
                  transittime.add(chargesDOB.getTransitTime()); 
                  transit_o.add(legCharges.getOrigin());
                  transit_d.add(legCharges.getDestination());
               }
                if("Y".equalsIgnoreCase(chargesDOB.getCarrierChecked())||"on".equalsIgnoreCase(chargesDOB.getCarrierChecked()))
               {
                  carrier.add(chargesDOB.getCarrier()); 
                  carrier_o.add(legCharges.getOrigin());
                  carrier_d.add(legCharges.getDestination());
               }
                if("Y".equalsIgnoreCase(chargesDOB.getRateValidityChecked())||"on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
               {
                 if(chargesDOB.getValidUpto()!=null)
                {
                  str1					  =eSupplyDateUtility.getDisplayStringArray(chargesDOB.getValidUpto());
                }
                  ratevalidity.add(str1[0]); 
                  validity_o.add(legCharges.getOrigin());
                  validity_d.add(legCharges.getDestination());
               }
           }    
           else
           {
         
               if("Y".equalsIgnoreCase(chargesDOB.getFrequencyChecked())||"on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
               {
                  frequency.add(chargesDOB.getFrequency()); 
                }
                if("Y".equalsIgnoreCase(chargesDOB.getTransitTimeChecked())||"on".equalsIgnoreCase(chargesDOB.getTransitTimeChecked()))
               {
                  transittime.add(chargesDOB.getTransitTime()); 
                }
                if("Y".equalsIgnoreCase(chargesDOB.getCarrierChecked())||"on".equalsIgnoreCase(chargesDOB.getCarrierChecked()))
               {
                  carrier.add(chargesDOB.getCarrier()); 
                 }
                if("Y".equalsIgnoreCase(chargesDOB.getRateValidityChecked())||"on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
               {
                 if(chargesDOB.getValidUpto()!=null)
                {
                  str1					  =eSupplyDateUtility.getDisplayStringArray(chargesDOB.getValidUpto());
                   ratevalidity.add(str1[0]); 
                  }
                
               }
           
           }
        }
        }
         
           shipmentMode = "";
             if(!finalDOB.isMultiModalQuote())
            {
               if(finalDOB.getMasterDOB().getShipmentMode()==1)
                shipmentMode = "AIR FREIGHT PROPOSAL";
               else if(finalDOB.getMasterDOB().getShipmentMode()==2)
                shipmentMode = "SEA FREIGHT PROPOSAL";
               else if(finalDOB.getMasterDOB().getShipmentMode()==4)
                shipmentMode = "TRUCK FREIGHT PROPOSAL";
            }
            else
            {
              shipmentMode  = " MULTI-MODAL FREIGHT PROPOSAL ";
            }
              
            
            Chunk chk = new Chunk(shipmentMode,FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f); 
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            if("MY".equalsIgnoreCase(masterDOB.getCountryId()))
            {
              chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLUE));
              cellCountry = new Cell(chk);
              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
              cellCountry.setNoWrap(true); 
              cellCountry.setLeading(13.0f);//@@Do Not Decrease.
              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
              partCountry.addCell(cellCountry);
              
              cellCountry = new Cell("");
              cellCountry.setBorderWidth(0);
              cellCountry.setLeading(5.0f);
              partCountry.addCell(cellCountry);
              
              chk = new Chunk(headerDOB.getCustomerName(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.BLUE));
              cellCountry = new Cell(chk);
              cellCountry.setHeader(true);
              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
              cellCountry.setNoWrap(true); 
              cellCountry.setLeading(10.0f);
              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
              partCountry.addCell(cellCountry);
              
              chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.BLUE));
              cellCountry = new Cell(chk);
              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
              cellCountry.setNoWrap(true); 
              cellCountry.setLeading(10.0f);
              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
              partCountry.addCell(cellCountry);
              
              cellCountry = new Cell("");
              cellCountry.setBorderWidth(0);
              cellCountry.setLeading(5.0f);
              partCountry.addCell(cellCountry);
            }
            else
            {
              chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
              cellCountry = new Cell(chk);
              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
              cellCountry.setNoWrap(true); 
              cellCountry.setLeading(13.0f);//@@Do Not Decrease.
              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
              partCountry.addCell(cellCountry);
              
              cellCountry = new Cell("");
              cellCountry.setBorderWidth(0);
              cellCountry.setLeading(5.0f);
              partCountry.addCell(cellCountry);
              
              chk = new Chunk(headerDOB.getCustomerName(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.RED));
              cellCountry = new Cell(chk);
              cellCountry.setHeader(true);
              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
              cellCountry.setNoWrap(true); 
              cellCountry.setLeading(10.0f);
              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
              partCountry.addCell(cellCountry);
              
              chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.RED));
              cellCountry = new Cell(chk);
              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
              cellCountry.setNoWrap(true); 
              cellCountry.setLeading(10.0f);
              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
              partCountry.addCell(cellCountry);
              
              cellCountry = new Cell("");
              cellCountry.setBorderWidth(0);
              cellCountry.setLeading(5.0f);
              partCountry.addCell(cellCountry);
            
            }
            chk = new Chunk("QUOTE REFERENCE: "+masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");
            cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("DATE OF QUOTATION: "+strDate[0],FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);            
            
            document.add(partCountry); 
            
            contents = masterDOB.getContentOnQuote();
            levels   = masterDOB.getLevels();
            aligns   = masterDOB.getAlign();
            headFoot = masterDOB.getHeaderFooter();
            int hFootLen	= headFoot.length;
            Table content  = null;
            if(contents!=null && contents.length>0)
            {
              content  =  new Table(1);
              content.setOffset(5);
              content.setWidth(100);
              content.setPadding(1);
              content.setSpacing(0);
              content.setBackgroundColor(Color.WHITE);
              content.setBorderColor(Color.black);
              content.setBorderWidth(1f);
              Cell  cellContent =  null;
              chk         =  null;
              for(int i=0;i<hFootLen;i++)
              {
                if(headFoot[i]!=null && "H".equalsIgnoreCase(headFoot[i]))
                {
                  chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                  cellContent = new Cell(chk);
                  cellContent.setBorder(0);
                  cellContent.setLeading(8.0f);
                  cellContent.setBackgroundColor(Color.LIGHT_GRAY);
                  if("L".equalsIgnoreCase(aligns[i]))
                   cellContent.setHorizontalAlignment(cellContent.ALIGN_LEFT);
                  else if("C".equalsIgnoreCase(aligns[i]))
                   cellContent.setHorizontalAlignment(cellContent.ALIGN_CENTER);
                  else if("R".equalsIgnoreCase(aligns[i]))
                   cellContent.setHorizontalAlignment(cellContent.ALIGN_RIGHT);                   
                  content.addCell(cellContent);                  
                }
              }
              document.add(content);
            }
            
            ///////////////////////////////////////////Second Table////////////////////////////
         
            partCountry  =  new Table(2,13);
            partCountry.setOffset(5);
            partCountry.setWidth(100);
            partCountry.setPadding(1);
            partCountry.setSpacing(0);
            partCountry.setBackgroundColor(Color.WHITE);
            partCountry.setBorderColor(Color.black);
            partCountry.setBorderWidth(1f);
            
            chk = new Chunk("PREPARED BY: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("SERVICE INFORMATION: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setColspan(2);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            cellCountry.setBackgroundColor(Color.ORANGE);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("Agent: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk); 
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getAgent()!=null?headerDOB.getAgent().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
// @@ Commented by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008            
            /*chk = new Chunk("Cargo Acceptance Place: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);*/
//@@ Added by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008            
            if("EXW".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FAS".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FCA".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FOB".equalsIgnoreCase(headerDOB.getIncoTerms()))
            {
                  chk = new Chunk("Place Of Acceptance: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
            }
            else
            {
                  chk = new Chunk("Place Of Delivery: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
            }
//@@ Ended by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008            
            chk = new Chunk("   "+(headerDOB.getCargoAcceptancePlace()!=null?headerDOB.getCargoAcceptancePlace().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            
            chk = new Chunk("Origin Port: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+((headerDOB.getOriginPortName()!=null?headerDOB.getOriginPortName().toUpperCase()+", ":""))+(headerDOB.getOriginPortCountry()!=null?headerDOB.getOriginPortCountry().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            
            chk = new Chunk("Destination Port: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+((headerDOB.getDestPortName()!=null?headerDOB.getDestPortName().toUpperCase()+", ":""))+(headerDOB.getDestPortCountry()!=null?headerDOB.getDestPortCountry().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
//@@ Commented by subrahmanyam for the enhancement #148546 on 09/12/2008          
            /*chk = new Chunk("Routing: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            chk = new Chunk("   "+(headerDOB.getRouting()!=null?headerDOB.getRouting().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);*/
            
//@@ Ended by subrahmanyam for the enhancement #148546 on 09/12/2008
            
            chk = new Chunk("Commodity or Product: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getCommodity()!=null?headerDOB.getCommodity().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("Type of service quoted: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            //@@Added by Kameswari for the WPBN issue-146448 on 03/12/08
            int freqSize	=	 frequency.size();
          for(int i=0;i<freqSize;i++)
          {
               if(chargesSize>1)
               {
                  chk = new Chunk("Frequency ("+frequency_o.get(i)+"-"+frequency_d.get(i)+")",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+frequency.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
               else
               {
                  chk = new Chunk("Frequency ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+frequency.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
          }   
          int carrierSize	=	carrier.size();
             for(int i=0;i<carrierSize;i++)
          {
               if(chargesSize>1)
               {
                  chk = new Chunk("Carrier ("+carrier_o.get(i)+"-"+carrier_d.get(i)+")",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+carrier.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
               else
               {
                  chk = new Chunk("Carrier ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+carrier.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
          }
             int tTimeSize	=	transittime.size();
          for(int i=0;i<tTimeSize;i++)
          {
               if(chargesSize>1)
               {
                  chk = new Chunk(transitTime+"("+transit_o.get(i)+"-"+transit_d.get(i)+")",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+transittime.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
               else
               {
                  chk = new Chunk(transitTime+" ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+transittime.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
          }
          int rateValiditySize	= ratevalidity.size();
          for(int i=0;i<rateValiditySize;i++)
          {
               if(chargesSize>1)
               {
                  chk = new Chunk("Freight Rate Validity("+validity_o.get(i)+"-"+validity_d.get(i)+")",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+ratevalidity.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
               else
               {
                  chk = new Chunk("Freight Rate Validity ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setBackgroundColor(Color.lightGray);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
                  chk = new Chunk("   "+ratevalidity.get(i).toString().toUpperCase(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  cellCountry = new Cell(chk);
                  cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
                  partCountry.addCell(cellCountry);
               }
          }
           //@@WPBN issue-146448 on 03/12/08
      
            chk = new Chunk("Incoterms: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getIncoTerms()!=null?headerDOB.getIncoTerms().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("Notes: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getNotes()!=null?headerDOB.getNotes().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("Date Effective: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(effDate[0]!=null?effDate[0]:""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("Validity of Quote: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(validUptoStr!=null?validUptoStr:"VALID UNTIL FURTHER NOTICE"),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
             if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
            {
              chk = new Chunk("Payment Terms: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
              cellCountry = new Cell(chk);
              cellCountry.setBackgroundColor(Color.lightGray);
              cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
              partCountry.addCell(cellCountry);
              
              chk = new Chunk("   "+headerDOB.getPaymentTerms(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
              cellCountry = new Cell(chk);
              cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
              partCountry.addCell(cellCountry);
            }
            
            document.add(partCountry);
            partCountry.complete();
            //System.out.println("After Page Country-------------------------------->");
            //Origin Charges
            //document.setMargins(10,10,10,10);
            ArrayList originCharges  = finalDOB.getOriginChargesList();
            
            int[] originIndices				= finalDOB.getSelectedOriginChargesListIndices();
            int   originChargesSize   = 0;
            if(originIndices!=null)
              originChargesSize		= originIndices.length;
            else
              originChargesSize		= 0;
              
            //Destination
            ArrayList destCharges		= finalDOB.getDestChargesList();
            int[]      destIndices		= finalDOB.getSelctedDestChargesListIndices();
            int      destChargesSize = 0;
            if(destIndices!=null)
              destChargesSize	= destIndices.length;
            else
              destChargesSize	= 0;
            
           
            //ArrayList charges				                      = finalDOB.getLegDetails();
            //int       chargesSize			                    = charges.size();
            
            Table chargeCountry  = null ;
            Cell  cell           = null ;
            //float cellWidths[]   = {40,10,10,15,25};
            float cellWidths[]   = {40,10,10,15,25,25};
           /* chk = new Chunk("Charge Name",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            Cell cell = new Cell(chk);
            cell.setHeader(true);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
            chargeCountry.addCell(cell);
            
            chk = new Chunk("Breakpoint",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
             cell = new Cell(chk);
            cell.setHeader(true);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
            chargeCountry.addCell(cell);
            
            chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
             cell = new Cell(chk);
            cell.setHeader(true);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
            chargeCountry.addCell(cell);
            
            chk = new Chunk("Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
             cell = new Cell(chk);
            cell.setHeader(true);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
            chargeCountry.addCell(cell);
            
            chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
             cell = new Cell(chk);
            cell.setHeader(true);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
            chargeCountry.addCell(cell);           
            
            chargeCountry.endHeaders();
            System.out.println("End of Charge Headers--------------------------------->");
            System.out.println("");*/
            
          //  QuoteCharges chargesDOB           = null;
            ArrayList    originChargeInfo     = null;
            int          originChargesInfoSize= 0;
            QuoteChargeInfo chargeInfo        = null;
            if(originChargesSize>0)
				    {
               // chargeCountry  = new Table(5);
               chargeCountry  = new Table(6);//@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                chargeCountry.setWidth(100);
                chargeCountry.setWidths(cellWidths);
                chargeCountry.setPadding(1);
                chargeCountry.setSpacing(0);
                chargeCountry.setOffset(5);
                chargeCountry.setBackgroundColor(Color.WHITE);
                chargeCountry.setBorderColor(Color.black);
                chargeCountry.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
                chargeCountry.setBorderWidth(1f);
//@@ Added by subrahmanyam for wpbn id:174487   &175657  on 07-jul-09            
                chargeCountry.setTableFitsPage(true);
                chargeCountry.setCellsFitPage(true);
//@@ Ended for 174487  &175657               
                //@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                /*chk = new Chunk("ORIGIN CHARGES",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setColspan(5);cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);*/
                 chk = new Chunk("Origin Charges",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
               // cell.setColspan(6);
                cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);
                chk = new Chunk("Weight Break Slab",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
               // cell.setColspan(6);
                cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);
                chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
               // cell.setColspan(6);
                cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);
                chk = new Chunk("Charge Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
               // cell.setColspan(6);
                cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);
                chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
               // cell.setColspan(6);
                cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);
                chk = new Chunk("Density Ratio",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
               // cell.setColspan(6);
                cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);
                //chargeCountry.endHeaders();
                
                for(int i=0;i<originChargesSize;i++)
                {
                  chargesDOB				    = (QuoteCharges)originCharges.get(originIndices[i]);
                  originChargeInfo		  = chargesDOB.getChargeInfoList();
                  originChargesInfoSize	= originChargeInfo.size();
                  for(int k=0;k<originChargesInfoSize;k++)
                  {
                    chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
                    if(k==0)
                    {
                      if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
                        chk = new Chunk(chargesDOB.getExternalName()!=null?chargesDOB.getExternalName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      else
                        chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setLeading(9.0f);
                      //cell.setRowspan(originChargesInfoSize);
                      //cell.setBorderWidth(0);
                      cell.setBackgroundColor(Color.lightGray);
                      cell.setHeader(true);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      chargeCountry.addCell(cell);
                    }
                    else
                    {
                      cell = new Cell("");cell.setLeading(8.0f);                          
                      cell.setBackgroundColor(Color.lightGray);cell.setHeader(true);
                      cell.setBorderWidth(0);
                      chargeCountry.addCell(cell);
                    }
                      chk = new Chunk((chargeInfo.getBreakPoint()!=null && !"Absolute".equalsIgnoreCase(chargeInfo.getBreakPoint()) && !"Percent".equalsIgnoreCase(chargeInfo.getBreakPoint()))?chargeInfo.getBreakPoint().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setLeading(8.0f);
                      cell.setBackgroundColor(Color.lightGray);cell.setHeader(true);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(chargeInfo.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      //cell.setBackgroundColor(Color.lightGray);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(df.format(chargeInfo.getSellRate())+(chargeInfo.isPercentValue()?" %":""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setHeader(true);  
                      cell.setLeading(8.0f);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      chargeCountry.addCell(cell);                      
                       //@@Added by Kameswari for the internal issue on 09/04/09
                      chk = new Chunk(chargeInfo.getRatio()==null?"":"1:"+chargeInfo.getRatio(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk); 
                      cell.setNoWrap(true); cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);   
                       //@@internal issue on 09/04/09
                  }
                }
               document.add(chargeCountry); 
            }
            
              //Freight Charges
              //document.setMargins(10,10,10,10);
              //System.out.println("After         Origin Charges --------------------------------->");
              int       freightChargesSize                  = 0;
              int       freightChargesInfoSize              = 0;
              int[]     frtIndices                          = null;
             // QuoteFreightLegSellRates       legCharges	    = null;
             // ArrayList                      freightCharges = null;
              ArrayList                      freightChargeInfo = null;
           
                if(chargesSize>0)
             
                {
                 // chargeCountry  = new Table(5);
                 chargeCountry  = new Table(6);//@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                  chargeCountry.setWidth(100);
                  chargeCountry.setWidths(cellWidths);
                  chargeCountry.setPadding(1);
                  chargeCountry.setSpacing(0);
                  chargeCountry.setOffset(5);
                  chargeCountry.setBackgroundColor(Color.WHITE);
                  chargeCountry.setBorderColor(Color.black);
                  chargeCountry.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
                  chargeCountry.setBorderWidth(1f);
//@@ Added by subrahmanyam for 174487 &175657on 07-jul-09
                  chargeCountry.setTableFitsPage(true);
                  chargeCountry.setCellsFitPage(true);
//@@ Ended for 174487 &175657
                  for(int i=0;i<chargesSize;i++)
                  {
                     legCharges	   = (QuoteFreightLegSellRates)charges.get(i);
                     freightCharges =  legCharges.getFreightChargesList();
                     
                     keyList.add(""+legCharges.getShipmentMode());
                      
                      frtIndices	   =  legCharges.getSelectedFreightChargesListIndices();
                      
                      if(frtIndices!=null)
                        freightChargesSize = frtIndices.length;
                      else
                        freightChargesSize	= 0;
              
                      if(i==0 && freightChargesSize>0)
                      {
                            /*chk = new Chunk("FREIGHT CHARGES",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
                            cell = new Cell(chk);cell.setLeading(8.0f);
                            cell.setBackgroundColor(Color.ORANGE);
                            cell.setColspan(5);
                            cell.setHeader(true);
                            chargeCountry.addCell(cell);*/
                            //@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                        chk = new Chunk("Freight Charges",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Weight Break Slab",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Charge Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Density Ratio",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                                    //chargeCountry.endHeaders();
                      }
                    for(int j=0;j<freightChargesSize;j++)
                    {
                      chargesDOB				= (QuoteCharges)freightCharges.get(frtIndices[j]);
                      freightChargeInfo		= chargesDOB.getChargeInfoList();
                      freightChargesInfoSize	= freightChargeInfo.size();                    
                      int count=0; //@@ ADDED by subrahmanyam for 181430 on 10/09/09
                      String tempDesc = "";
                      for(int k=0;k<freightChargesInfoSize;k++)
                      {
                          String temp="";
                          if(k>0)
                          {
                            chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k-1);
                            temp =chargeInfo.getRateDescription();
                        
                          }
                        chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k);
                         /* if(k==0)
                        {
                          chk = new Chunk(legCharges.getOrigin()+"-"+legCharges.getDestination(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                          cell = new Cell(chk);
                          cell.setColspan(5);
                          cell.setBackgroundColor(Color.WHITE);
                          cell.setLeading(8.0f);
                          chargeCountry.addCell(cell);
                          
                          chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId():"Freight Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setHeader(true);
                          //cell.setRowspan(freightChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setLeading(9.0f);                         
                          chargeCountry.addCell(cell);
                        }
                        else
                        {
                          cell = new Cell("");                          
                          cell.setBackgroundColor(Color.lightGray);cell.setHeader(true);
                          cell.setBorderWidth(0);cell.setLeading(9.0f);
                          chargeCountry.addCell(cell);
                        }*/
                         if(k==0)
                        {
                          chk = new Chunk(legCharges.getOrigin()+"-"+legCharges.getDestination(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                          cell = new Cell(chk);
                         // cell.setColspan(5);
                          cell.setColspan(6);//@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                          cell.setBackgroundColor(Color.WHITE);
                          cell.setLeading(8.0f);
                          chargeCountry.addCell(cell);
                          
                          //chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId():"Freight Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          chk = new Chunk(chargeInfo.getRateDescription(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setHeader(true);
                          //cell.setRowspan(freightChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setLeading(9.0f);                         
                          chargeCountry.addCell(cell);
                        }
                        else  if(temp.equalsIgnoreCase(chargeInfo.getRateDescription()))
                        {
                          cell = new Cell("");                          
                          cell.setBackgroundColor(Color.lightGray);cell.setHeader(true);
                          cell.setBorderWidth(0);cell.setLeading(9.0f);
                          chargeCountry.addCell(cell);
                        }
                        else
                        {
                          chk = new Chunk(chargeInfo.getRateDescription(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setHeader(true);
                          //cell.setRowspan(freightChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setLeading(9.0f);                         
                          chargeCountry.addCell(cell);
                        }
                           /* if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                                              ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                             {
                                          // breakPoint = "OR";  // commented by subrahmanyam for the wpbn id: 181430 on 08-sep-09
                                          // added by subrahmanyam for the wpbn id: 181430 on 08-sep-09
                                           if(count ==1 && tempDesc.equalsIgnoreCase(chargeInfo.getRateDescription())){
                                           breakPoint = "Or";
                                           count =0;
                                           }
                                           else
                                           breakPoint = "Absolute";
                                           //ended for 181430

                             }
                            else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                                           ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN"))
                              {
                                        // added by subrahmanyam for the wpbn id: 181430 on 08-sep-09
                                        if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                                        ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")){
                                        count = 1;
                                        tempDesc = chargeInfo.getRateDescription();
                                        }
                                        //ended for 181430
                                        breakPoint = "MIN";
                              }
                         else
                           if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC"))
                         {
                                      breakPoint = "BASIC";
                         }
                           else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG"))
                             {
                                          breakPoint = "FLAT";
                         }
                           else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
                        {
                                          breakPoint ="ABSOLUTE";
                       } 
                        else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
                          {
                                        breakPoint ="PERCENT";
                        }
                    else if (
                      chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
                                       ||chargeInfo.getBreakPoint().endsWith("CSS")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")
                                       ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("css")||chargeInfo.getBreakPoint().endsWith("pss"))
                    {
                                            m=  chargeInfo.getBreakPoint().length()-3;
                                           breakPoint= chargeInfo.getBreakPoint().substring(0,m);
                                      
                   }
                    else
                    {
                                      breakPoint= chargeInfo.getBreakPoint();
                    }@@ Commented by govind for the issue 260762*/
                         //@@ Added by govind for the issue 260762
                         if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")
                                 ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                {
                              //breakPoint = "Or";   // commented by subrahmanyam for the wpbn id: 181430 on 08-sep-09
                              // added by subrahmanyam for the wpbn id: 181430 on 08-sep-09
                              if(count ==0 ||(count ==1 && tempDesc.equalsIgnoreCase(chargeInfo.getRateDescription()))){
                              breakPoint = "Flat";
                              count=0;
                              }
                              else
                              breakPoint = "Absolute";
                              //ended for 181430

                }else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
               		 || chargeInfo.getBreakPoint().equalsIgnoreCase("CAFPFPERCENT"))
                {
                    if(count ==0 ||(count ==1 && tempDesc.equalsIgnoreCase(chargeInfo.getRateDescription()))){
                    breakPoint = "Percent";
                    count=0;
               }
               else
               breakPoint = "Absolute";
               

                }
 
               else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                              ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN")
                              ||chargeInfo.getBreakPoint().equalsIgnoreCase("MIN")||chargeInfo.getBreakPoint().toUpperCase().endsWith("MIN")) // Added by gowtham to show surcharge description as min
                 {
                           if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                              ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN"))
                              {
                                         count =1;
                                         tempDesc = chargeInfo.getRateDescription();
                              }
                                         //ended for 181430

                           breakPoint = "Min";
                 }
            else
              if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC")||chargeInfo.getBreakPoint().toUpperCase().endsWith("BASIC")) //Added by gowtham to show surcharge description as min
            {
                         breakPoint = "Basic";
            }
           
            else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG")
            ||chargeInfo.getBreakPoint().equalsIgnoreCase("FLAT")||chargeInfo.getBreakPoint().toUpperCase().endsWith("FLAT"))//Added by gowtham to show surcharge description as min
                {
                             breakPoint = "Flat";
            }
              else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF")||chargeInfo.getBreakPoint().toUpperCase().endsWith("ABSOLUTE"))// Added by Gowtham on 18-Feb2011
           {
                             breakPoint ="Absolute";
          } 
           else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE")||chargeInfo.getBreakPoint().toUpperCase().endsWith("PERCENT"))// Added by Gowtham on 18-Feb2011)
             {
                           breakPoint ="Percent";
           }
       else if (
         chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
                          ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")
                          ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("pss"))
       {
                               m=  chargeInfo.getBreakPoint().length()-3;
                              breakPoint= chargeInfo.getBreakPoint().substring(0,m);
                         
      }
       else
       {
                         breakPoint= chargeInfo.getBreakPoint().length()>5?chargeInfo.getBreakPoint().substring(0,4):chargeInfo.getBreakPoint(); // Modified by Gowtham for Quote View Issue on 17 Feb2011
     }     
                         
           //260762 ends here              
                         
                         
                         
                     // chk = new Chunk(chargeInfo.getBreakPoint()!=null?chargeInfo.getBreakPoint().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                       chk = new Chunk(breakPoint!=null?breakPoint.toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                       cell = new Cell(chk);cell.setHeader(true);
                      cell.setBackgroundColor(Color.lightGray);cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():loginbean.getCurrencyId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      //cell.setBackgroundColor(Color.lightGray);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(df.format(chargeInfo.getSellRate()),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setHeader(true);	
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setLeading(8.0f);                      
                      chargeCountry.addCell(cell);
                       //@@Added by Kameswari for the internal issue on 09/04/09
                      chk = new Chunk(chargeInfo.getRatio()==null?"":"1:"+chargeInfo.getRatio(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk); 
                      cell.setNoWrap(true); cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);   
                       //@@internal issue on 09/04/09
                    }
                   }
                  }
                  keySize   =  keyList.size();
                  document.add(chargeCountry);
                  QuoteFreightLegSellRates  tempDOB = null;
                  for(int i=0;i<chargesSize;i++)
                  {
                      tempDOB = (QuoteFreightLegSellRates)charges.get(i);
                      for(int j=0;j<keySize;j++)
                      {
                        if(i==j)
                          break;
                        else
                        {
                          if(!((String)keyList.get(j)).equalsIgnoreCase(""+tempDOB.getShipmentMode()))
                              isMultiModal  = true;
                        }
                      }
                  }
                  
                }//end of if(charge)
                
               // System.out.println("After Charges--------------------------------->");
                //document.setMargins(10,10,10,10);
                ArrayList  destChargeInfo             =  null;
                //int[]      destChargeInfo        =  null;
                int        destChargesInfoSize        =  0;
                if(destChargesSize>0)
                {                   
                   // chargeCountry  = new Table(5);
                   chargeCountry  = new Table(6);
                    chargeCountry.setWidth(100);
                    chargeCountry.setWidths(cellWidths);
                    chargeCountry.setPadding(1);
                    chargeCountry.setSpacing(0);
                    chargeCountry.setOffset(5);
                    chargeCountry.setBackgroundColor(Color.WHITE);
                    chargeCountry.setBorderColor(Color.black);
                    chargeCountry.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
                    chargeCountry.setBorderWidth(1f);
//@@ Added by subrahmanyam for wpbn id: 174487 & 175657 on 07-jul-09
                    chargeCountry.setTableFitsPage(true);
                    chargeCountry.setCellsFitPage(true);
// @@ Ended for 174487 &175657
                    /*chk = new Chunk("DESTINATION CHARGES",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBackgroundColor(Color.ORANGE);
                    cell.setColspan(5);cell.setLeading(8.0f);
                    cell.setHeader(true);
                    chargeCountry.addCell(cell);*/
                         //@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                        chk = new Chunk("Destination Charges",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Weight Break Slab",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Charge Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                        chk = new Chunk("Density Ratio",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                       // cell.setColspan(6);
                        cell.setLeading(10.0f);
                        cell.setBackgroundColor(Color.ORANGE);                
                        cell.setHeader(true);
                        chargeCountry.addCell(cell);
                       
                    //chargeCountry.endHeaders();
                    
                    for(int j=0;j<destChargesSize;j++)
                    {
                      chargesDOB				= (QuoteCharges)destCharges.get(destIndices[j]);
                      destChargeInfo			= chargesDOB.getChargeInfoList();
                      destChargesInfoSize		= destChargeInfo.size();
                      for(int k=0;k<destChargesInfoSize;k++)
                      {
                        chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);
                        if(k==0)
                        {
                          if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
                            chk = new Chunk(chargesDOB.getExternalName()!=null?chargesDOB.getExternalName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          else
                            chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          
                          cell = new Cell(chk);cell.setLeading(9.0f);
                          //cell.setRowspan(destChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setHeader(true);                          
                          chargeCountry.addCell(cell);
                        }
                        else
                        {
                          cell = new Cell("");                          
                          cell.setBackgroundColor(Color.lightGray);cell.setHeader(true);
                          cell.setBorderWidth(0);
                          cell.setLeading(9.0f);
                          chargeCountry.addCell(cell);
                        }
                        chk = new Chunk((chargeInfo.getBreakPoint()!=null && !"Percent".equalsIgnoreCase(chargeInfo.getBreakPoint()) && !"Absolute".equalsIgnoreCase(chargeInfo.getBreakPoint()))?chargeInfo.getBreakPoint().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);cell.setHeader(true);
                        cell.setBackgroundColor(Color.lightGray);cell.setLeading(8.0f);
                        chargeCountry.addCell(cell);
                        
                        chk = new Chunk(chargeInfo.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                        //cell.setBackgroundColor(Color.lightGray);
                        chargeCountry.addCell(cell);
                        
                        chk = new Chunk(df.format(chargeInfo.getSellRate())+(chargeInfo.isPercentValue()?" %":""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                        chargeCountry.addCell(cell);
                        
                        chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);   cell.setHeader(true);cell.setLeading(8.0f);
                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                        chargeCountry.addCell(cell); 
                         //@@Added by Kameswari for the internal issue on 09/04/09
                      chk = new Chunk(chargeInfo.getRatio()==null?"":"1:"+chargeInfo.getRatio(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk); 
                      cell.setNoWrap(true); cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);   
                       //@@internal issue on 09/04/09
                      }
                    }
                    document.add(chargeCountry);
                }
                
                //document.add(chargeCountry);
                if(finalDOB.getExternalNotes()!=null && finalDOB.getExternalNotes().length>0)
                {
                    //Logger.info(FILE_NAME,"finalDOB.getExternalNotes().length::"+finalDOB.getExternalNotes().length);
                    Table notes  =  new Table(1,finalDOB.getExternalNotes().length+1);
                    notes.setWidth(100);
                    notes.setPadding(1);
                    notes.setSpacing(0);
                    notes.setOffset(5);
                    notes.setBackgroundColor(Color.WHITE);
                    notes.setBorderColor(Color.black);
                     //@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                    //notes.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
                     notes.setDefaultHorizontalAlignment(Element.ALIGN_LEFT);
                    notes.setBorderWidth(1f);
                    Cell notesCell ;
                    chk = new Chunk("NOTES",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
                    notesCell = new Cell(chk);
                    notesCell.setHeader(true);notesCell.setLeading(8.0f);
                    notesCell.setBackgroundColor(Color.ORANGE);
                    notes.addCell(notesCell);
                    for(int i=0;i<finalDOB.getExternalNotes().length;i++)
                    {
                      chk = new Chunk(finalDOB.getExternalNotes()[i]!=null?finalDOB.getExternalNotes()[i].toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      notesCell = new Cell(chk);  notesCell.setLeading(8.0f);                      
                      notes.addCell(notesCell);
                    }
                    document.add(notes);
                }
                
                if(contents!=null && contents.length>0)
                {
                  content  =  new Table(1);
                  content.setOffset(5);
                  content.setWidth(100);
                  content.setPadding(1);
                  content.setSpacing(0);
                  content.setBackgroundColor(Color.WHITE);
                  content.setBorderColor(Color.black);
                  content.setBorderWidth(1f);
                  content.setTableFitsPage(true);
                  Cell  cellContent =  null;
                  for(int i=0;i<hFootLen;i++)
                  {
                    if(headFoot[i]!=null && "F".equalsIgnoreCase(headFoot[i]))
                    {
                      chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                      cellContent = new Cell(chk);
                      cellContent.setBorder(0);
                      cellContent.setLeading(8.0f);
                      cellContent.setBackgroundColor(Color.LIGHT_GRAY);
                      if("L".equalsIgnoreCase(aligns[i]))
                       cellContent.setHorizontalAlignment(cellContent.ALIGN_LEFT);
                      else if("C".equalsIgnoreCase(aligns[i]))
                       cellContent.setHorizontalAlignment(cellContent.ALIGN_CENTER);
                      else if("R".equalsIgnoreCase(aligns[i]))
                       cellContent.setHorizontalAlignment(cellContent.ALIGN_RIGHT);                   
                      content.addCell(cellContent);                      
                    }
                  }
                  document.add(content);
                }
                
                document.close();
                //System.out.println("After     document Close----------------------------------------->");
                String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime())+masterDOB.getQuoteId();
                file_tsmp        = file_tsmp.replaceAll("\\:","");
                file_tsmp        = file_tsmp.replaceAll("\\.","");
                file_tsmp        = file_tsmp.replaceAll("\\-","");
                file_tsmp        = file_tsmp.replaceAll(" ","");
                File fs = new File("Quote.pdf");//@@Commented and Modified by Kameswari for the WPBN issue
          
                FileOutputStream  fileOutputStream= new FileOutputStream(fs);
                baos.writeTo(fileOutputStream);
                //Logger.info(FILE_NAME,"file_tsmp::"+file_tsmp);
             //   File f = new File("Quote"+file_tsmp+".pdf");
              //@@Added by Kameswari for the WPBN issue-80440
             PdfReader reader = new PdfReader("Quote.pdf");
            int n = reader.getNumberOfPages();
               File f = new File("Quote"+file_tsmp+".pdf");//@@Commented and Modified by Kameswari for the WPBN issue
          
            
            // we create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(f));
        
            // adding some metadata
            // adding content to each page
           
            int k = 0;
            PdfContentByte under = null;
            PdfContentByte over=null;
              BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            while (k < n) {
            	k++;
            	 	over = stamp.getOverContent(k);
                under= stamp.getOverContent(k);
            	over.beginText();
            	over.setFontAndSize(bf, 8);
            	over.setTextMatrix(15, 15);
             	over.showText("page " + k+" of "+n);
              if(k>1)
              {
              // over.setFontAndSize(bf, 10);
              over.setFontAndSize(bf, 7);
               over.showText("                                                                                                    QUOTE REFERENCE:"+masterDOB.getQuoteId());
               //@@ Added by subrahmanyam for WPBN:146452 on 12/12/2008               
               over.endText();
               over.beginText();
                over.showText("                                                                                                                                CUSTOMER NAME: "+headerDOB.getCustomerName()+"\n\n\n");//subrahmanyam 12/12/2008
          
//@@ Ended by subrahmanyam for WPBN ISSUE:146452 on 12/12/2008               
               

               }
             	over.endText();
            }
            stamp.close();
               //@@WPBN issue-80440    
           
    
                request.getSession().setAttribute("QuoteOuptutStream",f);
                baos.close();
                //
                
                String[]  contactPersons      =    masterDOB.getCustContactNames();
                String    contactName         =    null;
                StringBuffer subject  = new StringBuffer("DHL Global Forwarding Quotation,");
                String to_emailIds    = finalDOB.getHeaderDOB().getCustEmailId();
                String       body     = "";
                
                if(!isMultiModal)
                {
                  if(masterDOB.getShipmentMode()==1)
                    subject.append(" Airfreight ");
                  if(masterDOB.getShipmentMode()==2)
                    subject.append(" Seafreight ");
                  else if(masterDOB.getShipmentMode()==4)
                    subject.append(" Truckfreight ");
                }
                else
                {
                  subject.append(" Multi-Modal ");
                }
                subject.append((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():"")).append(" to ").append((headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""));
                if(finalDOB.getUpdatedReportDOB()!=null)
                  subject.append(", Quote Reference ").append(masterDOB.getQuoteId()).append(" Replacing ").append(finalDOB.getUpdatedReportDOB().getQuoteId());
                else
                  //subject.append(", Quote Reference ").append(masterDOB.getQuoteId()!=0?masterDOB.getQuoteId()+"":request.getAttribute("quoteId"));   
                subject.append(", Quote Reference ").append(masterDOB.getQuoteId()!=null?masterDOB.getQuoteId()+"":request.getAttribute("quoteId"));   //@@Commented and Modified by Kameswari fot the WPBN issue-146971 on 04/12/08
                String filename  = "Annexure"+file_tsmp+".pdf";
               
               if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
               {
                  if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                    || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                    doGenerateCartagePDF(filename,finalDOB,request,response);
               }
               boolean isEmailIdGiven = true;
               if("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEmailFlag()))
                {                  
                  if(contactPersons!=null)
                  {
                	  int contPersnLen	= contactPersons.length;
                      for(int i=0;i<contPersnLen;i++)
                      {
                        contactName   =   masterDOB.getCustContactNames()[i];
                        
                        if(masterDOB.getCustomerContactsEmailIds()[i]== null || (masterDOB.getCustomerContactsEmailIds()[i]!=null && masterDOB.getCustomerContactsEmailIds()[i].trim().length()==0))
                            isEmailIdGiven = false;
                            
                       /* if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                            body = "Dear "+contactName+",\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                          }
                          else
                          {
                            body = "Dear "+contactName+",\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
                                 " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                           }  */
                         /*  body  = "Dear "+contactName+",\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                            
                         if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                            
                               body  = "Dear "+(contactName!=null?contactName:"")+",\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }
                          else
                          {
                             body  = "Dear "+(contactName!=null?contactName:"")+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                        "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }
                            if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                        {
                          if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                          || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                          {
                              if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                 { sentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                                  }
                              else
                                  unsentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]!=null?masterDOB.getCustomerContactsEmailIds()[i]:"No Email Id Provided.");
                          }
                          else
                          {
                            if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                              { sentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                              }
                            else
                               unsentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]!=null?masterDOB.getCustomerContactsEmailIds()[i]:"No Email Id Provided.");
                          }
                        }
                        else
                        {
                           if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                              { sentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                              }
                             
                          else
                               unsentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]!=null?masterDOB.getCustomerContactsEmailIds()[i]:"No Email Id Provided.");
                        }
                        
                      }
                  }
                  else
                  {
                     /* if(finalDOB.getUpdatedReportDOB()!=null)
                      {
                        body = "Dear Customer,\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                              ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                      }
                      else
                      {
                        body = "Dear Customer,\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
                                 " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                      }*/   
                     /*   body  = "Dear Customer,\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                          if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                          
                               body  = "Dear Customer,\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }
                          else
                          {
                             body  = "Dear Customer,\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }
                  
                            
                            to_emailIds = to_emailIds.replaceAll(";",",");
                      if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                      {
                        if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                          || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                        {
                            if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                {
                                sentMailList.add(to_emailIds);
                             
                                }
                            else
                                unsentMailList.add(to_emailIds);
                        }
                        else
                        {
                          // if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))//@@Modified by Kameswari for the WPBN issue-
                               if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                              {
                              sentMailList.add(to_emailIds);
                              
                                }
                          else
                              unsentMailList.add(to_emailIds);
                        }
                      }
                      else
                      {
                         if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                            {
                            sentMailList.add(to_emailIds);
               
                                }
                         else
                            unsentMailList.add(to_emailIds);
                      }
                  }
                }
                 if("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getFaxFlag()))
                  {
                    String customerFax = headerDOB.getCustFaxNo()!=null?headerDOB.getCustFaxNo():"";
                    String countrycode = headerDOB.getCustCountyCode()!=null?headerDOB.getCustCountyCode():"";
                    String contactFax  = null;
                    if("SG".equalsIgnoreCase(countrycode))
                    {
                      if(customerFax!=null && customerFax.length()>0)
                       customerFax = "fax#"+customerFax+"@tcdhl.com";
                    }
                    else
                    {
                      if(customerFax!=null && customerFax.length()>0)
                       customerFax = "ifax#"+customerFax+"@tcdhl.com";
                    }
                    
                    if(contactPersons!=null)
                    {
                    	int contactPerLen	= contactPersons.length;
                      for(int i=0;i<contactPerLen;i++)
                      {
                        contactName   =   masterDOB.getCustContactNames()[i];
                        
                        if(masterDOB.getCustomerContactsFax()[i]!=null && masterDOB.getCustomerContactsFax()[i].trim().length()!=0)
                        {
                          if("SG".equalsIgnoreCase(countrycode))
                            contactFax  = "fax#"+masterDOB.getCustomerContactsFax()[i]+"@tcdhl.com";
                          else
                            contactFax  = "ifax#"+masterDOB.getCustomerContactsFax()[i]+"@tcdhl.com";
                          
                          /*if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                            body = "Dear "+contactName+",\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                          }
                          else
                          {
                            body = "Dear "+contactName+",\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
                                 " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                          }*/
                           /*  body  = "Dear "+contactName+",\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                                       
                        if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                            //logger.info("sell buy flag::"+finalDOB.getUpdatedReportDOB().getSellBuyFlag());
                          //  logger.info("ext. charge name ::"+finalDOB.getEmailChar//geName());
                               body  = "Dear "+(contactName!=null?contactName:"")+",\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }
                          else
                          {
                             body  = "Dear "+(contactName!=null?contactName:"")+",\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }                 
                                     
                         if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                          {
                            if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                              || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                            {
                                if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                  sentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                                else
                                  unsentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                            }
                            else
                            {
                              if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                sentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                              else
                                unsentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                            }
                          }
                          else
                          {
                            if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                              sentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                            else
                              unsentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                          }
                        }
                      }
                    }
                    else
                    {
                      if(customerFax!=null && customerFax.length()>0)
                      {
                      /*  if(finalDOB.getUpdatedReportDOB()!=null)
                      {
                        body = "Dear Customer,\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                              ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                      }
                      else
                      {
                        body = "Dear Customer,\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
                                 " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
                      }*/
                      /*  body  = "Dear Customer,\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                           if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                            //logger.info("sell buy flag::"+finalDOB.getUpdatedReportDOB().getSellBuyFlag());
                          //  logger.info("ext. charge name ::"+finalDOB.getEmailChar//geName());
                               body  = "Dear Customer,\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }
                          else
                          {
                             body  = "Dear Customer,\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                          }
                            
                                  if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                        {
                          if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                            || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                            {
                              if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),customerFax,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                sentFaxList.add(headerDOB.getCustFaxNo());
                              else
                                unsentFaxList.add(headerDOB.getCustFaxNo());
                            }
                          else
                          {
                            if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),customerFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                sentFaxList.add(headerDOB.getCustFaxNo());
                            else
                                unsentFaxList.add(headerDOB.getCustFaxNo());
                          }
                        }                          
                        else
                        {
                          if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),customerFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                              sentFaxList.add(headerDOB.getCustFaxNo());
                          else
                              unsentFaxList.add(headerDOB.getCustFaxNo());
                        }
                      }
                      else
                        unsentFaxList.add("No Fax Number is Specified.");
                    }
                  }
                  if(finalDOB.getUpdatedReportDOB()!=null && "Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getPrintFlag()) && ("N".equalsIgnoreCase(finalDOB.getFlagsDOB().getEmailFlag()) || ("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEmailFlag()) && !isEmailIdGiven)))
                  {
                      if(contactPersons!=null)
                      {
                        String countrycode = headerDOB.getCustCountyCode()!=null?headerDOB.getCustCountyCode():"";
                        String contactFax  = null;
                        int contPersLen	= contactPersons.length;
                        for(int i=0;i<contPersLen;i++)
                        {
                          contactName   =   masterDOB.getCustContactNames()[i];
      
                         /* body = "Dear "+contactName+",\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                                  ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
                                  "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();*/
                      /*     body  = "Dear "+contactName+",\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                            body  = "Dear "+(contactName!=null?contactName:"")+",\n\nThis is a replacement quotation reference "+(masterDOB.getQuoteId()!=null?masterDOB.getQuoteId():"")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                    
                         if(masterDOB.getCustomerContactsEmailIds()[i]!=null && masterDOB.getCustomerContactsEmailIds()[i].trim().length()!=0 && !sentMailList.contains(masterDOB.getCustomerContactsEmailIds()[i]))
                          {    
                            if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                            {
                              if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                              || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                              {
                                  if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                      sentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                                  else
                                      unsentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                              }
                              else
                              {
                                if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                   sentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                                else
                                   unsentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                              }
                            }
                            else
                            {
                               if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                  sentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                              else
                                   unsentMailList.add(masterDOB.getCustomerContactsEmailIds()[i]);
                            }
                         }
                         else
                         {
                            if("SG".equalsIgnoreCase(countrycode))
                              contactFax  = "fax#"+masterDOB.getCustomerContactsFax()[i]+"@tcdhl.com";
                            else
                              contactFax  = "ifax#"+masterDOB.getCustomerContactsFax()[i]+"@tcdhl.com";
                            
                            if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                            {
                              if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                              || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                              {
                                  if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                      sentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                                  else
                                      unsentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                              }
                              else
                              {
                                if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                   sentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                                else
                                   unsentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                              }
                            }
                            else
                            {
                               if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                  sentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                              else
                                   unsentFaxList.add(masterDOB.getCustomerContactsFax()[i]);
                            }
                         }
                      }
                    }
                    else
                    {
                      
                      /*body = "Dear Customer,\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                              ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();*/
                   /*  body  = "Dear Customer,\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
                                       "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId();  //@@Modified by Kameswari for the WPBN issue-61303*/
                      body  = "Dear Customer,\n\nThis is a replacement quotation reference "+masterDOB.getQuoteId()+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                     ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                             "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303*/
                           
                         to_emailIds = to_emailIds.replaceAll(";",",");
                      if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                      {
                        if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                          || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                        {
                            if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                                sentMailList.add(to_emailIds);
                            else
                                unsentMailList.add(to_emailIds);
                        }
                        else
                        {
                          // if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                            if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))//@@modified by Kameswari for the WPBN issue-
                              sentMailList.add(to_emailIds);
                          else
                              unsentMailList.add(to_emailIds);
                        }
                      }
                      else
                      {
                         if(sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList()))
                            sentMailList.add(to_emailIds);
                         else
                            unsentMailList.add(to_emailIds);
                      }
                    }
                  }
              returnList.add(0,sentMailList);
              returnList.add(1,unsentMailList);
              returnList.add(2,sentFaxList);
              returnList.add(3,unsentFaxList);
    }   
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error while generating the PDF"+e.toString());
      logger.error(FILE_NAME+"Error while generating the PDF"+e.toString());
      mailFlag  =  0;
      //throw new Exception("Error while generating PDF format");
    }
    return returnList;
  }  
  private int doGenerateCartagePDF(String fileName,QuoteFinalDOB finalDOB,HttpServletRequest request,HttpServletResponse response)throws Exception
  {
    try
    {      //System.out.println("Starting zone PDF Generation------------------------------>");
            DecimalFormat df  = new DecimalFormat("###,###,###,##0.00");
            QuoteCartageRates pickQuoteCartageRates     = null;
            QuoteCartageRates deliQuoteCartageRates     = null;
            ArrayList         pickUpQuoteCartageRates   = null;
            ArrayList         deliveryQuoteCartageRates = null;
            Set               pickBreaks                = null;
            Set               deliBreaks                = null;
            Set               pickUpZoneCodeSet         = null;
            Set               deliveryZoneCodeSet       = null;
            HashMap           pickUpZoneCodeMap         = null;
            HashMap           deliveryZoneCodeMap       = null;
            HashMap           pickUpZoneZipMap          = null;
            HashMap           deliveryZoneZipMap        = null;
            ArrayList         zipList                   = null; 
            ArrayList         pickupWeightBreaks        = null;
            ArrayList         delWeightBreaks           = null;
            Iterator          zones                     = null;
            Iterator          breaksSet                 = null;
            int               pickupWeightBreaksSize    = 0;
            int               delWeightBreaksSize       = 0;
            
            pickUpQuoteCartageRates   =  finalDOB.getPickUpCartageRatesList();
            deliveryQuoteCartageRates =  finalDOB.getDeliveryCartageRatesList(); 
            pickupWeightBreaks        =  finalDOB.getPickupWeightBreaks();
            delWeightBreaks           =  finalDOB.getDeliveryWeightBreaks();
            
            if(pickupWeightBreaks!=null)
                pickupWeightBreaksSize  = pickupWeightBreaks.size();
            if(delWeightBreaks!=null)
                delWeightBreaksSize     = delWeightBreaks.size();
            
            QuoteHeader    headerDOB	=	 finalDOB.getHeaderDOB();
            QuoteMasterDOB masterDOB  =   finalDOB.getMasterDOB();
            ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
            ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
            eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
            //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
            String[] strDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
            String[] effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
            String[] validDate ;
            String   validUptoStr = "";
            if(headerDOB.getValidUpto()!=null)
            {
                validDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getValidUpto());
                validUptoStr = validDate[0];
            }
            
            StringBuffer   attentionTo            =   new StringBuffer("");
            if(masterDOB.getCustContactNames()!=null)
            {
              for(int i=0;i<masterDOB.getCustContactNames().length;i++)
              {
                //Logger.info(FILE_NAME,"masterDOB.getCustContactNames()[i]::"+masterDOB.getCustomerContacts()[i]);
                attentionTo.append(masterDOB.getCustContactNames()[i]!=null?masterDOB.getCustContactNames()[i]:"");
                if(i!=(masterDOB.getCustContactNames().length-1))
                  attentionTo.append(",");
              }
            }
            
            String  chargeRate        =  null;
            
              
            //System.out.println("After getting data------------------------------>"+pickUpQuoteCartageRates.size());
            Document document = new Document(PageSize.A4,54f,54f,68.4f,68.4f);//@@ 36 points represent 0.5 inch            
            document.addTitle("Approved Report");
            document.addSubject("Report PDF");
            document.addKeywords("Test, Key Words");
            document.addAuthor("DHL");
            document.addCreator("Auto Generated through 4S DHL");
            document.addCreationDate();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	          PdfWriter writer	=	PdfWriter.getInstance(document, baos);
            document.open();           
            int[] widths = {10,10,10,10,10,10,12,28};
            
            Table mainT = new Table(8);  
            mainT.setWidth(100);                     
            mainT.setWidths(widths);
            mainT.setBorderColor(Color.white);
            mainT.setPadding(1);
            mainT.setSpacing(0);
                         
              
            Phrase  headingPhrase    =  new Phrase("",FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            Cell cellHeading = new Cell(headingPhrase);	
            cellHeading.setBorderColor(new Color(255,255, 255));
            cellHeading.setHorizontalAlignment(cellHeading.ALIGN_CENTER);
            cellHeading.setColspan(7);
            mainT.addCell(cellHeading);
            
            Cell imageCell = new Cell();            
            java.net.URL url =  getServletConfig().getServletContext().getResource("/images/DHLlogo.gif");
            Image img0 = Image.getInstance(url);            
            imageCell.add(img0);            
            imageCell.setHorizontalAlignment(imageCell.ALIGN_RIGHT);
            imageCell.setBorderWidth(0);
            imageCell.setNoWrap(true);
            mainT.addCell(imageCell);
            mainT.setAlignment(mainT.ALIGN_CENTER);
            document.add(mainT);
            
            //pickBreaks = pickQuoteCartageRates.getRates().keySet();
            //System.out.println("After Heading ----------------pickBreaks----------->");            
            Table partCountry = new Table(1,4); 
            partCountry.setBorderWidth(0);
            partCountry.setWidth(100);
            partCountry.setBorderColor(Color.black);
            //partCountry.setBackgroundColor(Color.ORANGE);
            partCountry.setPadding(1);
            partCountry.setSpacing(1);
            partCountry.setAutoFillEmptyCells(true);
            //partCountry.setTableFitsPage(true);
            partCountry.setAlignment(partCountry.ALIGN_CENTER);
           // partCountry.setWidth(100.0f);
            Cell cellCountry;
            Chunk chk;            
            
            chk = new Chunk("ANNEXURE",FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f); 
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
           if("MY".equalsIgnoreCase(masterDOB.getCountryId()))
          {
            chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""),FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.BLUE));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f); 
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
            cellCountry = new Cell("");
            cellCountry.setBorderWidth(0);
            cellCountry.setLeading(5.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk(headerDOB.getCustomerName(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.BLUE));
            cellCountry = new Cell(chk);
            cellCountry.setHeader(true);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            //Logger.info(FILE_NAME,"attentionTo.toString():"+attentionTo.toString());
            
            chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.BLUE));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
            cellCountry = new Cell("");
            cellCountry.setBorderWidth(0);
            cellCountry.setLeading(5.0f);
            partCountry.addCell(cellCountry);
          }
          else
          {
             chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""),FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.RED));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f); 
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
            cellCountry = new Cell("");
            cellCountry.setBorderWidth(0);
            cellCountry.setLeading(5.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk(headerDOB.getCustomerName(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.RED));
            cellCountry = new Cell(chk);
            cellCountry.setHeader(true);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            //Logger.info(FILE_NAME,"attentionTo.toString():"+attentionTo.toString());
            
            chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.RED));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
            cellCountry = new Cell("");
            cellCountry.setBorderWidth(0);
            cellCountry.setLeading(5.0f);
            partCountry.addCell(cellCountry);
          }
            chk = new Chunk("QUOTE REFERENCE: "+masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");
            cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("DATE OF QUOTATION: "+strDate[0],FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
            cellCountry.setNoWrap(true); 
            cellCountry.setLeading(10.0f);
            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
            partCountry.addCell(cellCountry);
            
            document.add(partCountry);
            
           Table partZone = null;
           Cell cellZone;
          if(pickUpQuoteCartageRates!=null&&pickUpQuoteCartageRates.size()>0)
          {
            /*Set keys = null;            
            for(int i=0;i<pickUpQuoteCartageRates.size();i++)
            {              
              if(i==0)
              { 
                pickBreaks = new TreeSet();//@@This is needed as only a new Set Implementation obj supports addAll() 
              }
              pickQuoteCartageRates = (QuoteCartageRates) pickUpQuoteCartageRates.get(i);
              keys = pickQuoteCartageRates.getRates().keySet();
              pickBreaks.addAll(keys);
            }*/
            
            //if(pickBreaks!=null && pickBreaks.size()>0)
            //if(pickupWeightBreaks != null && pickupWeightBreaksSize>0)
            partZone = new Table(pickupWeightBreaksSize+2);
            partZone.setOffset(30);
            partZone.setDefaultHorizontalAlignment(partZone.ALIGN_CENTER);
            partZone.setBorderWidth(1);
            partZone.setBorderColor(Color.black);
            partZone.setPadding(1);
            partZone.setSpacing(1);
            partZone.setWidth(100);
            partZone.setAutoFillEmptyCells(true);
            //partZone.setTableFitsPage(true);
            //@@ Added by subrahmanyam for the enhancement #147062 on 09/12/2008
            chk = new Chunk("PICKUP CARTAGE RATES AT: "+headerDOB.getOriginLocName(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
            cellZone = new Cell(chk);
            cellZone.setNoWrap(true);
            cellZone.setHeader(true);
            cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
            cellZone.setLeading(10.0f);
            cellZone.setBackgroundColor(Color.WHITE);
            cellZone.setColspan(pickupWeightBreaksSize+2);
            partZone.addCell(cellZone);
           //@@ Ended by subrahmanyam for the enhancement #147062 on 09/12/2008
            chk = new Chunk("PICKUP CARTAGE RATES",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cellZone = new Cell(chk);
            cellZone.setNoWrap(true);
            cellZone.setHeader(true);
            cellZone.setLeading(10.0f);
            cellZone.setBackgroundColor(Color.ORANGE);
            cellZone.setColspan(pickupWeightBreaksSize+2);
            partZone.addCell(cellZone);
            
            chk = new Chunk("Zone",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cellZone = new Cell(chk);
            cellZone.setLeading(8.0f);
            //cellZone.setNoWrap(true);
            cellZone.setHeader(true);
            cellZone.setBackgroundColor(Color.LIGHT_GRAY);
            partZone.addCell(cellZone);
            
            chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cellZone = new Cell(chk);
            cellZone.setLeading(8.0f);
            //cellZone.setNoWrap(true);
            cellZone.setHeader(true);
            cellZone.setBackgroundColor(Color.LIGHT_GRAY);
            partZone.addCell(cellZone);
            //breaksSet = pickBreaks.iterator();
           // logger.info("breaksSetbreaksSet::"+breaksSet);
            /*while(breaksSet.hasNext())
            {              
              chk = new Chunk((String)breaksSet.next(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);              
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              partZone.addCell(cellZone);
            }*/
            //if(pickupWeightBreaks!=null)
            //{
              //logger.info("pickupWeightBreaks"+pickupWeightBreaks);
              for(int i=0;i<pickupWeightBreaksSize;i++)
              {
                chk = new Chunk((String)pickupWeightBreaks.get(i),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);
                cellZone.setLeading(8.0f);
                //cellZone.setNoWrap(true);
                cellZone.setHeader(true);
                cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                partZone.addCell(cellZone);
              }
            //}
            partZone.endHeaders();
            int pikUpQuotCartRtSize	=	pickUpQuoteCartageRates.size();
            for(int i=0;i<pikUpQuotCartRtSize;i++)
            {  
              pickQuoteCartageRates = (QuoteCartageRates) pickUpQuoteCartageRates.get(i);
              chk = new Chunk(pickQuoteCartageRates.getZone(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              //cellZone.setNoWrap(true);            
              partZone.addCell(cellZone);
              
              chk = new Chunk(pickQuoteCartageRates.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              //cellZone.setNoWrap(true);            
              partZone.addCell(cellZone);
              
              pickUpZoneCodeMap   =  pickQuoteCartageRates.getRates();
              /*breaksSet = pickBreaks.iterator();
              while(breaksSet.hasNext())
               {
                 String wBreak = (String)breaksSet.next();
                 if(pickUpZoneCodeMap.containsKey(wBreak))
                 {
                  chargeRate = (String)pickUpZoneCodeMap.get(wBreak);
                  chargeRate  = df.format(Double.parseDouble(chargeRate));
                 }
                 else
                 {
                  chargeRate = "--";              
                 }
                chk = new Chunk(chargeRate,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);
                cellZone.setNoWrap(true);            
                partZone.addCell(cellZone); 
               }*/
               if(pickupWeightBreaks!=null)
               {
                 String wBreak =  null;
                 for(int k=0;k<pickupWeightBreaksSize;k++)
                 {
                   wBreak = (String)pickupWeightBreaks.get(k);
                   if(wBreak!=null && pickUpZoneCodeMap.containsKey(wBreak))
                   {
                    chargeRate = (String)pickUpZoneCodeMap.get(wBreak);
                    chargeRate  = df.format(Double.parseDouble(chargeRate));
                   }
                   else
                   {
                    chargeRate = "--";              
                   }
                  chk = new Chunk(chargeRate,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                  cellZone = new Cell(chk);
                  cellZone.setLeading(8.0f);
                  //cellZone.setNoWrap(true);            
                  partZone.addCell(cellZone); 
                 }
               }
             }
            //System.out.println("After zone header------------------------------>");
            document.add(partZone);
            }
            //Logger.info(FILE_NAME,"deliveryQuoteCartageRates::"+deliveryQuoteCartageRates);
            //logger.info(FILE_NAME+"deliveryQuoteCartageRates::"+deliveryQuoteCartageRates);
            if(deliveryQuoteCartageRates!=null&&deliveryQuoteCartageRates.size()>0)
            {
             /*Set dkeys         = null;
            for(int i=0;i<deliveryQuoteCartageRates.size();i++)
            {       
              if(i==0)
              { 
                deliBreaks = new TreeSet();//@@This is needed as only a new Set Implementation obj supports addAll() 
              }
              deliQuoteCartageRates = (QuoteCartageRates) deliveryQuoteCartageRates.get(i);
              dkeys = deliQuoteCartageRates.getRates().keySet();
              deliBreaks.addAll(dkeys);
              //System.out.println("Before zone header------------------------------>"+deliBreaks);
              //System.out.println("Before zone header------------------------------>"+deliBreaks.size());              
            }*/ 
           
            partZone = new Table(delWeightBreaksSize+2);
            partZone.setOffset(30);
            partZone.setDefaultHorizontalAlignment(partZone.ALIGN_CENTER);
            partZone.setBorderWidth(1);
            partZone.setBorderColor(Color.black);
            partZone.setPadding(1);
            partZone.setSpacing(1);
            partZone.setWidth(100);
            partZone.setAutoFillEmptyCells(true);
            //partZone.setTableFitsPage(true);
            //@@ Added by subrahmanyam for the enhancement #147062 on 08/12/2008
            chk = new Chunk("DELIVERY CARTAGE RATES AT: "+headerDOB.getDestLocName(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
                  cellZone = new Cell(chk);
                  cellZone.setNoWrap(true);
                  cellZone.setLeading(10.0f);
                  cellZone.setHeader(true);
                  cellZone.setBackgroundColor(Color.WHITE);
                  cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
                  cellZone.setColspan(delWeightBreaksSize+2);
                  partZone.addCell(cellZone);
      //@@ Ended by subrahmanyam for the enhancement #147062 on 08/12/2008
            chk = new Chunk("DELIVERY CARTAGE RATES",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cellZone = new Cell(chk);
            cellZone.setNoWrap(true);
            cellZone.setHeader(true);
            cellZone.setLeading(10.0f);
            cellZone.setBackgroundColor(Color.ORANGE);
            cellZone.setColspan(delWeightBreaksSize+2);
            partZone.addCell(cellZone);
            
            chk = new Chunk("Zone",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cellZone = new Cell(chk);
            cellZone.setLeading(8.0f);
            //cellZone.setNoWrap(true);
            cellZone.setHeader(true);
            cellZone.setBackgroundColor(Color.LIGHT_GRAY);
            partZone.addCell(cellZone);
            
            chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cellZone = new Cell(chk);
            cellZone.setLeading(8.0f);
            //cellZone.setNoWrap(true);
            cellZone.setHeader(true);
            cellZone.setBackgroundColor(Color.LIGHT_GRAY);
            partZone.addCell(cellZone);
            
            //breaksSet = deliBreaks.iterator();
            
            /*while(breaksSet.hasNext())
            {              
              String breaks = (String)breaksSet.next();
      
              chk = new Chunk(breaks,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);              
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              partZone.addCell(cellZone);
            }*/
            for(int i=0;i<delWeightBreaksSize;i++)
            {
              chk = new Chunk((String)delWeightBreaks.get(i),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);              
              cellZone.setLeading(8.0f);
              //cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              cellZone.setBackgroundColor(Color.LIGHT_GRAY);
              partZone.addCell(cellZone);
            }
            partZone.endHeaders();
            int delQuoteCartRtSize	=	deliveryQuoteCartageRates.size();
            for(int i=0;i<delQuoteCartRtSize;i++)
            {  
              deliQuoteCartageRates = (QuoteCartageRates) deliveryQuoteCartageRates.get(i);
              
              chk = new Chunk(deliQuoteCartageRates.getZone(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              //cellZone.setNoWrap(true);            
              partZone.addCell(cellZone);
              
              chk = new Chunk(deliQuoteCartageRates.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              cellZone.setNoWrap(true);            
              partZone.addCell(cellZone);
              
              
              deliveryZoneCodeMap   =  deliQuoteCartageRates.getRates();
              
              //breaksSet = deliBreaks.iterator();
              /*while(breaksSet.hasNext())
               {
                 String wBreak = (String)breaksSet.next();
                 if(deliveryZoneCodeMap.containsKey(wBreak))
                 {
                  chargeRate = (String)deliveryZoneCodeMap.get(wBreak);  
                  chargeRate  = df.format(Double.parseDouble(chargeRate));
                 }
                 else
                 {
                  chargeRate = "--";              
                 }
                chk = new Chunk(chargeRate,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);
                cellZone.setNoWrap(true);            
                partZone.addCell(cellZone); 
               }*/
               String wBreak = null;
               for(int k=0;k<delWeightBreaksSize;k++)
               {
                 wBreak = (String)delWeightBreaks.get(k);
                 if(wBreak!=null && deliveryZoneCodeMap.containsKey(wBreak))
                 {
                  chargeRate = (String)deliveryZoneCodeMap.get(wBreak);  
                  chargeRate  = df.format(Double.parseDouble(chargeRate));
                 }
                 else
                 {
                  chargeRate = "--";              
                 }
                chk = new Chunk(chargeRate,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);
                cellZone.setLeading(8.0f);
                //cellZone.setNoWrap(true);            
                partZone.addCell(cellZone); 
               }
             }
            //System.out.println("After zone header------------------------------>");
            document.add(partZone);
            
            }
       
            pickUpZoneZipMap  =  finalDOB.getPickZoneZipMap();
            //@@For Sorting the Zone Codes in an Order.
            java.util.List     list     =  new ArrayList();
            Set      zoneSet;
            //@@End of Declarations - Sorting
            if(pickUpZoneZipMap!=null && pickUpZoneZipMap.size()>0)
            {
              //System.out.println("Before zipCode header--------Set size------->"+pickUpZoneZipMap.keySet().size());
              //@@Sorting the Zone Codes (as TreeSet implements java.util.SortedSet)
              list.addAll(pickUpZoneZipMap.keySet());
              zoneSet = new TreeSet(list);
              //@@End of Sorting- Added by Yuvraj
              
              zones   = zoneSet.iterator();
              
              
              partZone = new Table(2);
              partZone.setOffset(30);
              partZone.setDefaultHorizontalAlignment(partZone.ALIGN_CENTER);
              partZone.setBorderWidth(1);
              partZone.setBorderColor(Color.black);
              partZone.setPadding(1);
              partZone.setSpacing(1);
              partZone.setWidth(100);
              //partZone.setAutoFillEmptyCells(true);
              //partZone.setTableFitsPage(true);
              //@@ Added by subrahmanyam for the enhancement #147062 on 08/12/2008
              chk = new Chunk("PICKUP CHARGES AT: "+headerDOB.getOriginLocName(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
              cellZone = new Cell(chk);
              cellZone.setLeading(10.0f);
              //cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              cellZone.setBackgroundColor(Color.WHITE);
              cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
              cellZone.setColspan(2);
              partZone.addCell(cellZone);
              
            //@@ Ended by subrahmanyam for the enhancement #147062 on  08/12/2008
              chk = new Chunk("PICKUP ZONE ZIP MAPPING",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              cellZone.setLeading(10.0f);
              cellZone.setBackgroundColor(Color.ORANGE);
              cellZone.setColspan(2);
              partZone.addCell(cellZone);
              
              chk = new Chunk("Zone",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              cellZone.setBackgroundColor(Color.LIGHT_GRAY);
              partZone.addCell(cellZone);
              chk = new Chunk("Zip Code",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              partZone.addCell(cellZone);
              partZone.endHeaders();
             // System.out.println("Before zipCode Body------------------------------>");
              
              while(zones.hasNext())
              {
                  String zone = (String)zones.next();
                 // System.out.println("zone------------------------------>"+zone);
                  zipList = (ArrayList)pickUpZoneZipMap.get(zone);
                  //System.out.println("zipList--------------------------->"+zipList);
                  int zipListSize	= zipList.size();
                  for(int i=0;i<zipListSize;i++){
                  //System.out.println("zone for ------------------------------>"+zone);
                  if(zone!=null && !zone.equals("null")&& zipList.get(i)!=null&& !((String)zipList.get(i)).equals("null"))
                  {
                    chk = new Chunk(zone,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                    cellZone = new Cell(chk);
                    cellZone.setLeading(8.0f);
                    cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                    cellZone.setNoWrap(true);            
                    partZone.addCell(cellZone);
                    //System.out.println("zipList.get(i)--------------------------->"+(String)zipList.get(i));
                    chk = new Chunk((String)zipList.get(i),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                    cellZone = new Cell(chk);
                    cellZone.setLeading(8.0f);
                    cellZone.setNoWrap(true);            
                    partZone.addCell(cellZone);
                  }
                 }
              }
              document.add(partZone);
            }
           
           deliveryZoneZipMap =  finalDOB.getDeliveryZoneZipMap();
           
           list     =  new ArrayList();
           zoneSet  =  null;
           
           if(deliveryZoneZipMap!=null && deliveryZoneZipMap.size()>0)
            {
              //System.out.println("Before zipCode header--------Set size------->"+deliveryZoneZipMap.keySet().size());
              list.addAll(deliveryZoneZipMap.keySet());
              zoneSet = new TreeSet(list);
              zones   = zoneSet.iterator();
              
              
              partZone = new Table(2);
              partZone.setOffset(30);
              partZone.setDefaultHorizontalAlignment(partZone.ALIGN_CENTER);
              partZone.setBorderWidth(1);
              partZone.setBorderColor(Color.black);
              partZone.setPadding(1);
              partZone.setSpacing(1);
              partZone.setWidth(100);
              //partZone.setAutoFillEmptyCells(true);
              //partZone.setTableFitsPage(true);
              //@@ Added by subrahmanyam for the enhancement #147062 on 08/12/2008
              chk = new Chunk("DELIVERY CHARGES AT: "+headerDOB.getDestLocName(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
              cellZone = new Cell(chk);
              cellZone.setLeading(10.0f);
              //cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              cellZone.setBackgroundColor(Color.WHITE);
              cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
              cellZone.setColspan(2);
              partZone.addCell(cellZone);
            //@@ Ended by subrahmanyam for the enhancement #147062 on 08/12/2008 
              chk = new Chunk("DELIVERY ZONE ZIP MAPPING",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              cellZone.setLeading(10.0f);
              cellZone.setBackgroundColor(Color.ORANGE);
              cellZone.setColspan(2);
              partZone.addCell(cellZone);
              
              chk = new Chunk("Zone",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              cellZone.setBackgroundColor(Color.LIGHT_GRAY);
              partZone.addCell(cellZone);
              chk = new Chunk("Zip Code",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8.0f);
              cellZone.setNoWrap(true);
              cellZone.setHeader(true);
              partZone.addCell(cellZone);
              partZone.endHeaders();
              //System.out.println("Before zipCode Body------------------------------>");
              
              while(zones.hasNext())
              {
                  String zone = (String)zones.next();
                  //System.out.println("zone------------------------------>"+zone);
                  zipList = (ArrayList)deliveryZoneZipMap.get(zone);
                  if(zipList!=null)
                  {
                  //System.out.println("zipList--------------------------->"+zipList);
                	  int zipListSize	=	zipList.size();
                  for(int i=0;i<zipListSize;i++){
                  //System.out.println("zone for ------------------------------>"+zone);
                  if(zone!=null && !zone.equals("null")&& zipList.get(i)!=null&& !((String)zipList.get(i)).equals("null"))
                  {
                    chk = new Chunk(zone,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                    cellZone = new Cell(chk);
                    cellZone.setLeading(8.0f);
                    cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                    cellZone.setNoWrap(true);            
                    partZone.addCell(cellZone);
                    //System.out.println("zipList.get(i)--------------------------->"+(String)zipList.get(i));
                    chk = new Chunk((String)zipList.get(i),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                    cellZone = new Cell(chk);
                    cellZone.setLeading(8.0f);
                    cellZone.setNoWrap(true);            
                    partZone.addCell(cellZone);
                  }
                  }
                 }
                }
                document.add(partZone);
            }
            //System.out.println("After zipCode header------------------------------>");
                 
                document.close();
                File f = new File("Cartage.pdf");
                FileOutputStream  fileOutputStream= new FileOutputStream(f);
                baos.writeTo(fileOutputStream);
                fileOutputStream.close();//added by sanjay on 20/03/2006
                //@@Added by Kameswari for the WPBN issue-80440
             PdfReader reader = new PdfReader("Cartage.pdf");
            int n = reader.getNumberOfPages();
            File fs = new File(fileName);
            String   space = " ";
            // we create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(fs));
        
            // adding some metadata
            // adding content to each page
            
            int k = 0;
            PdfContentByte under;
            PdfContentByte over=null;
              BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            while (k < n) {
            	k++;
            	 	over = stamp.getOverContent(k);
            	over.beginText();
            	over.setFontAndSize(bf, 8);
            	over.setTextMatrix(15, 15);
             	over.showText("page " + k+" of "+n);
              
              if(k>1)
              {
               over.setFontAndSize(bf, 7);
               over.showText("                                                                                                       QUOTE REFERENCE:"+masterDOB.getQuoteId());
               //@@Added by subrahmanyam for the WPBN:146452 on 12/12/2008
                over.endText();
               over.beginText();
               over.showText("                                                                                                                                    CUSTOMER NAME: "+headerDOB.getCustomerName());//subrahmanyam 12/12/2008
              //@@ Ended by subrahmanyam for the WPBM:146452 on 12/12/2008               
                }
             	over.endText();
            }
            stamp.close();
               //@@WPBN issue-80440    
      //@@ Commented by subrahmanyam for 146460         
               /* if("on".equalsIgnoreCase(request.getParameter("print")))
                {
               
                   request.getSession().setAttribute("CartageOuptutStream",fs);
                }*/
  //@@ Added by subrahmanyam for  146460                
                 if("on".equalsIgnoreCase(request.getParameter("print"))||"View".equalsIgnoreCase(request.getParameter("Operation")))
                {
                   
                   request.getSession().setAttribute("CartageOuptutStream",fs);
                }
  //@@ Ended by subrahmanyam for 146460
                //f.delete();
                baos.close();
                //System.out.println("End of generation header------------------------------>");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error while generating the PDF"+e.toString());
      logger.error(FILE_NAME+"Error while generating the PDF"+e.toString());
      throw new Exception("Error while generating PDF format");
    }
    return 1;
  }
  //@@ Added BY Govind for Breaks in expiry report
  
  private void doDownLoadProcess(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginbean)
  {
        QMSSellRatesDOB sellRatesDOB = null;
        String    operation          = null;
        ArrayList headerValues       =  null;
        ArrayList fslListValues      =  null;
        ArrayList finalList          =  null;
        HttpSession				    session			                  =	  request.getSession();
        QMSSellRatesDOB       sellDob = null;
        PrintWriter          out     =null;
        StringBuffer         errorMessage_data  = null;
        QMSSellRatesDOB     sellRatesDob1 = null;
        String[] chargesValue       = null;
        String[] chargeRateIndicator = null;
        String[] chargeDescription   = null;//Added BYGovind for the CR-219973
        String   srChargeId          = null;//Added BYGovind for the CR-219973
        HashMap  srChargeMap		 = null;//Added BYGovind for the CR-219973
        int chargesValueSize    = 0;
        String    weightBrk = null;
        String    rateTpe   = null;
        String    rate      = null;
        String[] effFrom      = null;
        String[] validUpto    = null;
        ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
        int temp1			=0;
       
        
    try
    {
            errorMessage_data   = new StringBuffer();
            operation = request.getParameter("Operation");
            sellRatesDOB  = new QMSSellRatesDOB();
            doGetBuyRates(request,response,loginbean,operation,sellRatesDOB);
            
            headerValues  = (ArrayList)session.getAttribute("HeaderValues");
            session.removeAttribute("HeaderValues");
            response.setContentType("application/vnd.ms-excel"); 
            response.setHeader("Content-Disposition","attachment;filename=BuyRatesExpiry.xls");
            
            eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
            //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
            
            
            out = response.getWriter();            
            
            if(headerValues!=null && headerValues.size()>1)
            {
             
               sellDob					=	(QMSSellRatesDOB)headerValues.get(0);
               finalList				=	(ArrayList)headerValues.get(1);
               fslListValues    = (ArrayList)finalList.get(0);
              
             
              weightBrk				=	sellDob.getWeightBreak();
              rateTpe					=	sellDob.getRateType();
              
              //Added By Kishore to form the common header and data
            Set<String> 			commonHeaderSet 				=    new LinkedHashSet<String>();
              QMSSellRatesDOB[] objs = new  QMSSellRatesDOB[fslListValues.size()];
              int allTotalHeadcount = 0;
              String tempdesc = "";
              
              for (int i = 0; i < fslListValues.size(); i++){
					objs[i] = (QMSSellRatesDOB) fslListValues.get(i);
					allTotalHeadcount = allTotalHeadcount + objs[i].getRateDescription().length;
				}
              
              Map map = new LinkedHashMap();
              for(int i = 0; i < objs.length; i++){
					QMSSellRatesDOB obj = objs[i];
					String rateDesArry[] = obj.getRateDescription();
					String wtBreaksArry[] = obj.getWeightBreaks();
					for (int j = 0; j < allTotalHeadcount; j++) {
						if (j < rateDesArry.length){
							String rateDes  = rateDesArry[j];
							if (map.containsKey(rateDes)){
								Set s = (Set) map.get(rateDes);
								s.add(wtBreaksArry[j].toUpperCase()); //Added by Kishore
								map.put(rateDes, s);
						}else{
								Set s = new HashSet();
								s.add(wtBreaksArry[j].toUpperCase());
								map.put(rateDes, s);
							}
						}else{
							j = allTotalHeadcount;
						}
					}
				}
           
                
              if(fslListValues!=null && fslListValues.size()>0)
              {

                  
                       errorMessage_data.append("ORIGIN:\t" );
                       errorMessage_data.append("DESTINATION:\t" );
                       //errorMessage_data.append("CURRENCY:\t" ); // Commented by Gowtham for Download Issue.
                       errorMessage_data.append("CARRIER ID:\t" );
                       errorMessage_data.append("SERVICELEVEL:\t" );
                       errorMessage_data.append("FREQUENCY:\t" );
                       if("2".equalsIgnoreCase(sellDob.getShipmentMode()))
                         errorMessage_data.append("APPROXIMATE TRANSIT DAYS:\t" ); 
                      else
                         errorMessage_data.append("APPROXIMATE TRANSIT TIME:\t" );
                      
                       errorMessage_data.append("EFFECTIVE FROM:\t" );
                       errorMessage_data.append("VALID UPTO:\t" );
                       
                       String finalStr1 ="";
                       
                       Iterator it = map.keySet().iterator();
                      
                       while(it.hasNext()){
                    	   String finalStr = "";
                    	   String rateDes = (String) it.next();
                    	   Set s = (Set) map.get(rateDes);
                    	   Object[] objs1 = null;
                    	   
                    	   if(rateDes.equals("A FREIGHT RATE")){
                    		   commonHeaderSet.add(rateDes);
                    		  if( s.contains("BASIC")){
                    			  s.remove("BASIC");
                    			  commonHeaderSet.add("BASIC~A FREIGHT RATE");
                    		  }
                    		  
                    		  if(s.contains("MIN")){
                    			   s.remove("MIN");
                    			   commonHeaderSet.add("MIN~A FREIGHT RATE");
                    		  }
                    		  
                    		  if(s.contains("FLAT")){
                    			   s.remove("FLAT");
                    			   commonHeaderSet.add("FLAT~A FREIGHT RATE");
                    		  }
                    		  
                    		  if(s.size()!= 0 ){
	                    		  objs1 = s.toArray();
	                    		  
	                    		  if(!"LIST".equalsIgnoreCase(weightBrk)){
	                    		  Double[] brksArray = new Double [objs1.length];	
		                  		  
	                    		  for(int i = 0; i < objs1.length; i++) {
		                  				brksArray[i] = Double.parseDouble(objs1[i].toString());
		                  			}
	                  			
	                  			 Arrays.sort(brksArray);
	                  			
	                  			 for (int i = 0; i < brksArray.length; i++) {
	                  				if (brksArray[i].floatValue() == brksArray[i].intValue()){
	                  					commonHeaderSet.add(brksArray[i].intValue()+"~"+rateDes);
	                  				}else{
	                  					 commonHeaderSet.add(brksArray[i]+"~"+rateDes);
	                  				}
	                  			}
	                    		  }else{
	                    			  
	                    			  	String[] brksArray = new String [objs1.length];	
			                  		  
		                    		  for(int i = 0; i < objs1.length; i++) 
			                  				brksArray[i] = objs1[i].toString();
			                  			
		                  			  
		                    		  for (int i = 0; i < brksArray.length; i++) 
		                  					 commonHeaderSet.add(brksArray[i]+"~"+rateDes);
                    		  
                    	 }
                    		  }
	                    }
                    	 
                    	 else if (rateDes.endsWith("-SS") || rateDes.endsWith("-SB") || rateDes.endsWith("-SF")){
                    		   
                		   s.remove("MIN");
                		   commonHeaderSet.add(rateDes);
                		   commonHeaderSet.add("CURRENCY~"+rateDes); // Added By Kishore For Surcharge Currency
                		   commonHeaderSet.add("MIN~"+rateDes);
                		   objs1 = s.toArray();
                		   
                		   Double[] brksArray = new Double [objs1.length];	
                			for(int i = 0; i < objs1.length; i++) {
                				brksArray[i] = Double.parseDouble(objs1[i].toString());
                			}
                			
                			Arrays.sort(brksArray);
                			
                			for (int i = 0; i < brksArray.length; i++) {
                				if (brksArray[i].floatValue() == brksArray[i].intValue()){
                					commonHeaderSet.add(brksArray[i].intValue()+"~"+rateDes);
                				}else{
                					 commonHeaderSet.add(brksArray[i]+"~"+rateDes);
                				}
                			}
                    			
                	   }else{
                		   commonHeaderSet.add(rateDes);
                		   commonHeaderSet.add("CURRENCY~"+rateDes); // Added By Kishore For Surcharge Currency
                		   objs1 = s.toArray();
                		   for(int i = 0; i<objs1.length;i++){
                			  if (objs1[i].toString().endsWith("BASIC")){
                				  commonHeaderSet.add(objs1[i]+"~"+rateDes);
                				  finalStr = finalStr + "\t";
                				  objs1[i]="";
                				  i = objs1.length;
                			  }
                		   }
                		   for(int i = 0; i<objs1.length;i++){
                 			  if (objs1[i].toString().endsWith("MIN")){
                 				 commonHeaderSet.add(objs1[i]+"~"+rateDes);
                 				  finalStr = finalStr + "\t";
                 				  objs1[i]="";
                 				  i = objs1.length;
                 			  }
                 		   }
                		   for(int i = 0; i<objs1.length;i++){
                 			  if (objs1[i].toString().endsWith("FLAT")){
                 				 commonHeaderSet.add(objs1[i]+"~"+rateDes);
                 				  finalStr = finalStr + "\t";
                 				  objs1[i]="";
                 				  i = objs1.length;
                 			  }
                 		   }
                		   for(int i = 0; i<objs1.length;i++){
                  			  if (objs1[i].toString().endsWith("PERCENT")){
                  				 commonHeaderSet.add(objs1[i]+"~"+rateDes);
                  				  finalStr = finalStr + "\t";
                  				  objs1[i]="";
                  				  i = objs1.length;
                  			  }
                  		   }
                		   for(int i = 0; i<objs1.length;i++){
                			   if (!objs1[i].toString().equals("")){
                				   commonHeaderSet.add(objs1[i]+"~"+rateDes);
                   				  finalStr = finalStr + "\t";
                   			}
                			   finalStr = finalStr + "\t";
                		   }
                	   }
                    	  
                    }
                       sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
                       String[] weighttBkValues		=	sellRatesDob1.getWeightBreaks();
                       int	wtBreakLength				=	weighttBkValues.length;
                       srChargeMap       		= 	sellRatesDob1.getSurchargesIds();   //Added by Govind for cr 219973
                       chargeDescription = sellRatesDob1.getRateDescription();
                    		   
                       
                       //Adding the Freight Rate and Surhcarge Weight Breaks : Kishore
                       int tempCount = 0;
                       String headerBreak="";
                       for( String s : commonHeaderSet){
                    	    if(s.indexOf('~') == -1)
                    	    	tempCount = s.length();
                    	    else
                    	    	tempCount = s.indexOf('~');
                    	   
                    	   headerBreak = s.substring(0,tempCount).toUpperCase();
                    	   if(!"LIST".equalsIgnoreCase(weightBrk)){
                    		   		errorMessage_data.append(headerBreak.length()>5  && (headerBreak.endsWith("BASIC") || headerBreak.endsWith("MIN") || headerBreak.endsWith("FLAT") || headerBreak.endsWith("PERCENT"))?headerBreak.substring(5)+"\t":headerBreak+"\t");
                    	   }
                    	   else{
                    		   errorMessage_data.append((headerBreak.length()>4&& headerBreak.length()<10 && !headerBreak.startsWith("CURRENCY")) ?headerBreak.substring(0,headerBreak.length()-5)+"\t":headerBreak+"\t");
                    	   }
                    	  if(s.equalsIgnoreCase("A FREIGHT RATE"))
                    		errorMessage_data.append("CURRENCY:\t");  
                       }
                        //Modified by Mohan for Issue No.219976 on 4-11-2010
                       errorMessage_data.append("INTERNAL NOTES:\t");
                       errorMessage_data.append("EXTERNAL NOTES:\t");
                       errorMessage_data.append("TERMINALID:\t");
                       //errorMessage_data.append("RATES DESCRIPTION:\t");
                       errorMessage_data.append("\n");
                        int listSize = fslListValues.size();
                        for(int i=0;i<listSize;i++)
                        {
                            effFrom       = null;
                            validUpto     = null;
                            sellRatesDob1 = (QMSSellRatesDOB)fslListValues.get(i);
                            temp1=0; // Added by Gowtham
                        
                            errorMessage_data.append(sellRatesDob1.getOrigin()+"\t");
                            errorMessage_data.append(sellRatesDob1.getDestination()+"\t");
                            //errorMessage_data.append(sellRatesDob1.getCurrencyId()+"\t"); // Commented by Gowtham for Buyrate Download Issue.
                            errorMessage_data.append(sellRatesDob1.getCarrier_id()+"\t");
                            errorMessage_data.append(sellRatesDob1.getServiceLevel()+"\t");
                            errorMessage_data.append(sellRatesDob1.getFrequency()+"\t");
                            errorMessage_data.append(sellRatesDob1.getTransitTime()+"\t");
                            
                                if(sellRatesDob1.getEffectiveFrom()!=null)
                                effFrom   = eSupplyDateUtility.getDisplayStringArray(sellRatesDob1.getEffectiveFrom());
                            
                            errorMessage_data.append(effFrom!=null?effFrom[0]:"");
                            errorMessage_data.append("\t");
                            
                            if(sellRatesDob1.getValidUpto()!=null)
                                validUpto = eSupplyDateUtility.getDisplayStringArray(sellRatesDob1.getValidUpto());
                            
                          errorMessage_data.append(validUpto!=null?validUpto[0]:"");
                            errorMessage_data.append("\t");
                            chargesValue			=	sellRatesDob1.getChargeRates();
                            chargeRateIndicator	=	sellRatesDob1.getChargeInr();
                            chargesValueSize  = chargesValue.length;
                            weighttBkValues		=	sellRatesDob1.getWeightBreaks(); // Added By kishore
                            String[] rateDescArry			= 	sellRatesDob1.getRateDescription();
                          
                            //Added by Kishroe For SurCharge Currency
                           
                            HashMap<String, String>	 surChargeMap = new HashMap<String, String>();
                            
                            int currCount=0; 
                            String[] surChargeCurrency		=   sellRatesDob1.getSurChargeCurency();
                            String   tempRateDesc = "";
                          
                            for (int j = 0; j < rateDescArry.length; j++) {
                            	weighttBkValues[j] = weighttBkValues[j].toUpperCase()+"~"+rateDescArry[j]; //Added by Kishore 
                            //Modified By Kishore for SCH-Currency
                            	if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescArry[j])
                            			&& ("".equalsIgnoreCase(tempRateDesc)
                            				|| !tempRateDesc.equalsIgnoreCase(rateDescArry[j]))
                            	 ){
                            		surChargeMap.put("CURRENCY~"+rateDescArry[j],surChargeCurrency[j]);
                            		tempRateDesc=rateDescArry[j];
                            		currCount++;
                            	}
							}
                           
                            String[] commonHeaderArray1 = new String[commonHeaderSet.size()];
                            commonHeaderArray1 = (String[])commonHeaderSet.toArray(commonHeaderArray1);
                            
                            List  dataList = (List) Arrays.asList(weighttBkValues);
                            int temp = 0;
                            
                            //Added By Kishore For SCH- Currency 
                            String ratedesc = "" ; 
                            
                            for (int k=0; k<commonHeaderArray1.length; k++)
                			{
                		
                				temp = dataList.indexOf(commonHeaderArray1[k]); //kishore
                				ratedesc = commonHeaderArray1[k].indexOf('~')==-1?commonHeaderArray1[k]:"~~~"; //sch-currency
                				if (temp!= -1){
                					if("SLAB".equalsIgnoreCase(chargeRateIndicator[temp]))
                						rate = chargesValue[temp]+"S";
                					else if("FLAT".equalsIgnoreCase(chargeRateIndicator[temp]))
                						rate = chargesValue[temp]+"F";
                					else
                						rate = chargesValue[temp]+"";
                					
                					errorMessage_data.append(rate +"\t");
                                }else
                                {
                					          					
                					//Added By Kishore For SCH- Currency ..
                                	if("A FREIGHT RATE".equalsIgnoreCase(ratedesc)) {
                					errorMessage_data.append("-"+"\t");
                					if(temp1==0){
                					errorMessage_data.append(sellRatesDob1.getCurrencyId()+"\t"); // Added by Gowtham
                					temp1++;
                					}
                                }
                                	else{ 
                                		
                                		errorMessage_data.append(surChargeMap.containsKey(commonHeaderArray1[k])
                                								 ? (surChargeMap.get(commonHeaderArray1[k])!=null && !"-".equals(surChargeMap.get(commonHeaderArray1[k]))? surChargeMap.get(commonHeaderArray1[k]):sellRatesDob1.getCurrencyId() )+"\t"
                                								 : "-\t"
                                								);
                                	}
                                }
                				//End of Kishore For SCH- Currency ..
                            }
                            
                            errorMessage_data.append((sellRatesDob1.getNotes()!=null)?sellRatesDob1.getNotes():"");
                            errorMessage_data.append("\t");
                             //Modified by Mohan for Issue No.219976 on 2-11-2010
                            errorMessage_data.append((sellRatesDob1.getExtNotes()!=null)?sellRatesDob1.getExtNotes():"");
                            errorMessage_data.append("\t");
                            errorMessage_data.append(sellRatesDob1.getTerminalId()+"\t");
                            System.out.println(sellRatesDob1.getRateDescription()+"\t");
                            errorMessage_data.append("\n");
                        }
                        
              }else
              { errorMessage_data.append("No data Found");}
          }else
          {
            errorMessage_data.append("No data Found");
          }
                        out.print(errorMessage_data.toString());
    }catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,""+e);
      logger.info(FILE_NAME+""+e);
    }
  }
  
  
  private void doGetBuyRates(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB)throws IOException,ServletException
  {
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                  =   new ArrayList();
    BuyRatesSessionHome			sellRatesHome                     =   null;
    BuyRatesSession				sellRatesRemote                   =   null;
    ArrayList			            headerValues	                  =   null;
    ArrayList			            headerVal	                      =   null;
    StringBuffer					errorMassege                      =   null;
    HttpSession				    session			                  =	  request.getSession();
    String        subOperation          =  request.getParameter("subOperation");
    String                nextNavigation                = null;
  	try
  	{  

  	insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
  	sellRatesHome		  =	  (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
  	sellRatesRemote	  =	  (BuyRatesSession)sellRatesHome.create();
      
     /* errorMassege      =   sellRatesRemote.validateSellRatesHdrData(sellRatesDOB);
      if(errorMassege.length() > 0)
      {
          headerVal = new ArrayList();
          //System.out.println("************After Look up**************** ");
          request.setAttribute("Errors",errorMassege.toString());
          headerVal.add(sellRatesDOB);
          session.setAttribute("HeaderValue",headerVal);
          nextNavigation			=	 "etrans/BuyRatesDownLoad.jsp";
          
          doDispatcher(request,response,nextNavigation);
          
      }
      else
      {*/
          headerValues      =   sellRatesRemote.getSellRatesValues(sellRatesDOB,loginBean,operation);   
          //System.out.println("************After Look up**************** "+headerValues.size());
          session.setAttribute("HeaderValues",headerValues);
          
          
          
     // }
      nextNavigation			=	nextNavigation   = "reports/BuyRatesExpiryReport.jsp?Operation="+operation+"subOperation="+subOperation ;
  	}
  	catch(Exception e)
  	{
  		e.printStackTrace();
  		errorMessageObject = new ErrorMessage("Error while doGetBuyRates()",nextNavigation); 
  		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
  		keyValueList.add(new KeyValue("Operation",operation)); 	
  		errorMessageObject.setKeyValueList(keyValueList);
  		request.setAttribute("ErrorMessage",errorMessageObject);
  		doDispatcher(request,response,"ESupplyErrorPage.jsp");
  	}
  }  
  
  
  
  private void insertValues(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
  {
      ErrorMessage			        errorMessageObject                =   null;
      ArrayList			            keyValueList	                    =   new ArrayList();
      String[]			            carriers			                    =	  null;
      String[]                  marginValues                      =   null;
       String                   shipmentMode                      =   null;
       Timestamp fromDate;
       Timestamp toDate;
      try
      {  
    	  fromDate = new ESupplyDateUtility().getTimestamp("DD/MM/YY",request.getParameter("fromDate"));  
    	  toDate   = new ESupplyDateUtility().getTimestamp("DD/MM/YY",request.getParameter("toDate"));  
          if(request.getParameter("shipmentMode")!=null)
            sellRatesDOB.setShipmentMode(request.getParameter("shipmentMode"));
          else
            sellRatesDOB.setShipmentMode("");
            
         
          if(request.getParameter("weightBreak")!=null)
            sellRatesDOB.setWeightBreak(request.getParameter("weightBreak"));
          else
            sellRatesDOB.setWeightBreak("");
          if(request.getParameter("rateType")!=null)
            sellRatesDOB.setRateType(request.getParameter("rateType"));
          else
            sellRatesDOB.setRateType("");
          if(request.getParameter("baseCurrency")!=null)
            sellRatesDOB.setCurrencyId(request.getParameter("baseCurrency"));
          else
            sellRatesDOB.setCurrencyId(loginBean.getCurrencyId());
          if(request.getParameter("weightClass")!=null)
            sellRatesDOB.setWeightClass(request.getParameter("weightClass"));
          else
            sellRatesDOB.setWeightClass("");
          if(request.getParameter("origin")!=null)
            sellRatesDOB.setOrigin(request.getParameter("origin"));
          else
            sellRatesDOB.setOrigin("");
         
          if(request.getParameter("destination")!=null)
            sellRatesDOB.setDestination(request.getParameter("destination"));
          else
            sellRatesDOB.setDestination("");
          
       if(request.getParameter("carriers")!=null)
            sellRatesDOB.setCarrier_id(request.getParameter("carriers"));
          else
            sellRatesDOB.setCarrier_id("");  
       
       sellRatesDOB.setFromdate(fromDate);
       sellRatesDOB.setToDate(toDate);

      }
      catch(Exception e)
      {
          e.printStackTrace();
          errorMessageObject = new ErrorMessage("Error while insertValues()",nextNavigation); 
          keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          doDispatcher(request,response,"ESupplyErrorPage.jsp");
      }
  }
  
  
  public void doDispatcher(HttpServletRequest request,
			HttpServletResponse response,
			String forwardFile) throws ServletException, IOException
{
try
{
RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
rd.forward(request, response);
}
catch(Exception ex)
{
//Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
}
} 
  
  
  
  
  
  
  
  
  
  
  
  
  private ArrayList doSendApprovedQuotes(HttpServletRequest request,HttpServletResponse response,ArrayList approvedList) throws FoursoftException
  {
    ESupplyGlobalParameters loginbean         = null; 
    UpdatedQuotesFinalDOB   reportFinalDOB    = null;
    int[]                   selIndices        = null;
    int                     listSize          = 0;
    
    HttpSession             session           = request.getSession();
    
    QMSQuoteSessionHome     home              = null;
    QMSQuoteSession         remote            = null;
    ArrayList               quoteFinalDOBList = null;     
    
    try
    {
      loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      //lookUpBean  = new LookUpBean();
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      
      reportFinalDOB  = new UpdatedQuotesFinalDOB();
      if(approvedList!=null)
        listSize  = approvedList.size();

      Hashtable accessList  =  (Hashtable)session.getAttribute("accessList");      
      //@@setting the buy rates permissions flag based on user role permissions.
      if(accessList.get("10605")!=null)
        reportFinalDOB.setBuyRatesFlag("Y");
      else
        reportFinalDOB.setBuyRatesFlag("N");
        
      selIndices  = new int[listSize];
      reportFinalDOB.setSelectedQuotesIndices(selIndices);
      reportFinalDOB.setApprovedQuotesList(approvedList);
      
      quoteFinalDOBList = remote.getUpdatedQuoteDetails(reportFinalDOB,loginbean);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in doSendApprovedQuotes "+e);
      logger.error(FILE_NAME+"Error in doSendApprovedQuotes "+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Sending Quote(s). Please Try Again.",e);
    }
    return quoteFinalDOBList;
  }
  private boolean sendMail(String frmAddress, String toAddress, String subject ,String message, String attachments,ArrayList list)
		{
		 
       	boolean returnFlag  =   true;
        ArrayList fileList = new ArrayList();
        try 
        {  
          
          Context initial = new InitialContext();
          Session session = (Session) initial.lookup("java:comp/env/mail/MS");
          InternetAddress fromAddress = new InternetAddress(frmAddress);
         
            
          Message msg = new MimeMessage(session); 
          msg.setFrom(fromAddress); 
          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress) ); 
          msg.setSubject(subject); 
          msg.setSentDate(new java.util.Date()); 
          
          // create and fill the first message part 
          MimeBodyPart mbp1 = new MimeBodyPart(); 
          mbp1.setText(message); 
          Multipart mp = new MimeMultipart();
          mp.addBodyPart(mbp1);
          
         // Logger.info(FILE_NAME,"mbp1  "+mbp1);
          //Logger.info(FILE_NAME,"mp  "+mp);
          
          String attachs[] = null;          
          
          if(attachments!=null && attachments.trim().length()!=0)
            attachs = attachments.split(",");
          
          if(attachs!=null && attachs.length>0)
          {
           ///---------
           Multipart multipart = new MimeMultipart();
          int attachLen	=	attachs.length;
           for(int i=0;i<attachLen;i++)
           {
             BodyPart mbp2 = new MimeBodyPart();
             DataSource fds = new FileDataSource(attachs[i]);          
             //Logger.info(FILE_NAME,"fds.getName()  "+fds.getName());
             mbp2.setDataHandler(new DataHandler(fds));
             mbp2.setFileName(fds.getName()); 
             mp.addBodyPart(mbp2);
           }
          }
            if(list!=null)
        {
          QuoteAttachmentDOB dob        = null;
          File               file       = null;
          FileOutputStream   fileStream = null;
          int listSize	= list.size();
           for(int i=0;i<listSize;i++)
          {
            dob = (QuoteAttachmentDOB)list.get(i);
            file   = new File(dob.getUserId()+dob.getTerminalId()+dob.getFileName());
            fileStream = new FileOutputStream(file);
            fileStream.write(dob.getPdfFile());
            fileList.add(file);
            
           
             BodyPart mbp2 = new MimeBodyPart();
            DataSource fds = new FileDataSource(file);
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName(dob.getFileName());
            mp.addBodyPart(mbp2);
            fileStream.flush();
            fileStream.close();
            }
        }
	      msg.setContent(mp);
	   		Transport.send(msg); 
       
	   }
	   catch(MessagingException me)
	   {
        //Logger.error(FILE_NAME,"MessagingException in Send Mail :"+ me);
        logger.error(FILE_NAME+"MessagingException in Send Mail :"+ me);
        me.printStackTrace();
        returnFlag  = false;
        //		System.out.println("Message Exception in send Mails ... "+me.toString());
        //throw new Exception(me.toString());
	   } 
	   catch(Exception e)
		 {
        //Logger.error(FILE_NAME,"Exception in send Mail "+e);
        logger.error(FILE_NAME+"Exception in send Mail "+e);
        e.printStackTrace();
        returnFlag = false;
//		   System.out.println("Exception in sendMails CustomizedReportBean..."+e.toString());	
	       //throw new Exception(e.getMessage());
	   }
     return returnFlag;
	}
  private double round(double number)
  {
    double  returnValue = 0;
    boolean flag        = false;
    int     d           = 2;
    double  f           = Math.pow(10, d);
    String  value       = "";
    
    try
    {
      if(number < 0)
      {
        flag      = true;
        number    = Math.abs(number);
      }
      number        = number + Math.pow(10, - (d + 1));
      number        = Math.round(number * f) / f;
      number        = number + Math.pow(10, - (d + 1)); 
      value         = number+"";
      if(flag)
        returnValue   = -(Double.parseDouble(value.substring(0, value.indexOf('.') + d + 1)));
      else
        returnValue   = Double.parseDouble(value.substring(0, value.indexOf('.') + d + 1));
    }
    catch(NumberFormatException nf)
    {
      returnValue = number;
    }
    return returnValue;
  }
}
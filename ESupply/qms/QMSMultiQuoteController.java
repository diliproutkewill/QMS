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
 

 * File					: QMSMultiQuoteController.java
 * @author				: Govind
 * @date				: 
 *CR- 219979&80         : CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */
 
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.formula.Ptg;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Graphic;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.qms.operations.costing.dob.CostingChargeDetailsDOB;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingLegDetailsDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.costing.dob.CostingRateInfoDOB;
import com.qms.operations.multiquote.dob.MultiQuoteAttachmentDOB;
import com.qms.operations.multiquote.dob.MultiQuoteCartageRates;
import com.qms.operations.multiquote.dob.MultiQuoteChargeInfo;
import com.qms.operations.multiquote.dob.MultiQuoteCharges;
import com.qms.operations.multiquote.dob.MultiQuoteFinalDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFlagsDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates;
import com.qms.operations.multiquote.dob.MultiQuoteFreightRSRCSRDOB;
import com.qms.operations.multiquote.dob.MultiQuoteHeader;
import com.qms.operations.multiquote.dob.MultiQuoteMasterDOB;
import com.qms.operations.multiquote.dob.MultiQuoteTiedCustomerInfo;
import com.qms.operations.multiquote.ejb.sls.QMSMultiQuoteSession;
import com.qms.operations.multiquote.ejb.sls.QMSMultiQuoteSessionHome;
import com.qms.operations.quote.dao.QMSQuoteDAO;
import com.qms.operations.quote.dob.QuoteCartageRates;
import com.qms.operations.quote.dob.QuoteCharges;
import com.qms.operations.quote.dob.QuoteFreightLegSellRates;
import com.qms.reports.java.UpdatedQuotesReportDOB;



public class QMSMultiQuoteController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static final String FILE_NAME = "QMSMultiQuoteController.java";
  private static Logger logger = null;
  //private LookUpBean      lookUpBean      =   null;
 // HttpSession             session           = null;
  /**
	 * Called by the servlet container to indicate to a servlet that the
	 * servlet is being placed into service.
	 *
	 * @param config the ServletConfig object that contains configutation information for this servlet
	 *
	 * @exception ServletException if an exception occurs that interrupts the servlet's NORMAL operation.
	 */
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    logger  = Logger.getLogger(FILE_NAME);	
  }

  /**
   * Process the HTTP doGet request.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request, response);
  }

  /**
   * Process the HTTP doPost request.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String                  operation           = null;
    String                  subOperation        = null;
    HttpSession             session             = null;
    ESupplyGlobalParameters loginbean           = null;
    
		    
    try
    {
      session       =  request.getSession();
      operation     =  request.getParameter("Operation");
      subOperation  =  request.getParameter("subOperation");
     
     
      loginbean     =  (ESupplyGlobalParameters)session.getAttribute("loginbean");
       
      //Depending up on the operation the request is forwarded to different methods which handle operations of different pages

      if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation)  || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation) || "Grouping".equalsIgnoreCase(operation)) && subOperation==null)
      {//this is the initial request

        doForwardToInitialJSP(request,response);
      }
      else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) && "MASTER".equalsIgnoreCase(subOperation))
      {//from the master page i.e the first page

        doMasterInfo(request,response);
      }else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) && "SELLRATES".equalsIgnoreCase(subOperation))
      {//from the sell rates page
          doSellRates(request,response);
        }
      else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) && "SELLCHARGES".equalsIgnoreCase(subOperation))
      {//from the sell Charges page
      
        doSellCharges(request,response);
      }
      else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) && "SUMMARY".equalsIgnoreCase(subOperation))
      {
      
        doSummary(request,response);
      }else if(("Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation))&& "EnterId".equalsIgnoreCase(subOperation))
      {
          doModifyProcess(request,response);
       }else if("View".equalsIgnoreCase(operation) && "Report".equalsIgnoreCase(subOperation))
       {
           doGenerateQuoteView(request,response);
       }else if("View".equalsIgnoreCase(operation) && "SUMMARY".equalsIgnoreCase(subOperation)){//Added by Rakesh on 11-01-2011
    	   doDispatcher(request, response, "qms/QMSMultiQuoteEnterId.jsp?operation=View");  
       }else if("Grouping".equalsIgnoreCase(operation) && "charges".equalsIgnoreCase(subOperation))
       {
           chargeGroupsView(request,response);
       } else if("Grouping".equalsIgnoreCase(operation) && "QuoteGroupInform".equalsIgnoreCase(subOperation))
       {
           quoteGroupsDtl(request,response);
       }else if("Grouping".equalsIgnoreCase(operation) && "sendPdf".equalsIgnoreCase(subOperation))
       {
           quoteGroupsSendOptions(request,response);
       }
       else if("Update".equalsIgnoreCase(operation) && subOperation==null)//Added for the Issue 234719
       {
         getUpdatedQuoteInfo(request,response,loginbean);
       }//Ended for Issue 234719
      
    }
    catch(Exception e)
    {
      session.removeAttribute("finalDOB");
      session.removeAttribute("PreFlagsDOB");
      session.removeAttribute("updatedSendOptions");
      e.printStackTrace();
      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSMultiQuoteController?Operation=Add");
      doDispatcher(request, response, "ESupplyErrorPage.jsp");
    }
    
  }
  
  
  private void doGenerateQuoteView(HttpServletRequest request,HttpServletResponse response) throws ServletException
  {
	    QMSMultiQuoteSessionHome       home              = null;
	    QMSMultiQuoteSession           remote            = null;
	    ESupplyGlobalParameters   loginbean         = null;
	    MultiQuoteFinalDOB        finalDOB          = null;
	    String                    quoteId           = null;
	    CostingMasterDOB          costingMasterDOB  = null;
	    CostingHDRDOB             costingHDRDOB     = null;
	    String	                    customerId      = null;
	    String[]                    origin          = null;
	    String[]                    destination     = null;
	    String                    operation         = null;
	    int                       shipmentMode      = 0;
	    MultiQuoteMasterDOB       masterDOB         = null;
	    String                    accessLevel       = "";
	    Hashtable				  accessList		= null;
	    String                    escalatedReport   = null;
	    String					  buyRatesPermission= "Y";
	    String					  isEscalated		= null; // Added by Gowtham	
	    ArrayList                 attachmentIdList  = null;
	    ArrayList                 attachmentIdList1  = null;
	    HttpSession               session           = request.getSession();
	    MultiQuoteFlagsDOB        flagsDOB          = null;
	    String[] attachmentId						= null;
	    String                   quoteStatus        = ""; 
	    String                   customerName       = "";
	    String 					 quoteActive		= "";
	      //subrahmanyam
	    StringBuffer            toEmailIds        = new StringBuffer("");
	    StringBuffer            toFaxIds          = new StringBuffer("");
	    String[]                contactPersons    = null;
	    String                 print              = null; 
	    String                 email              = null; 
	    String                 fax                = null; 
	    String                 emailText          = null;  
	    String                 countryId          = null;
	    //subrahmanyam
	    String                 serviceLevelDes    = "";// added by subrahmanyam  for 185127 ON 02-OCT-09
	    try
	    {attachmentId = request.getParameterValues("attachmentId");

	       
	        masterDOB       = new MultiQuoteMasterDOB();
	        quoteId         = request.getParameter("QuoteId");
	        operation       = request.getParameter("Operation");
	        escalatedReport = request.getParameter("radiobutton");
	        isEscalated		= request.getParameter("isEscalated");
	      
	        loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
	        
	        accessList  =  (Hashtable)session.getAttribute("accessList");

	        if(accessList.get("10605")==null)
	          buyRatesPermission	= "N";
	        
	        if("HO_TERMINAL".equals(loginbean.getAccessType()))
	          accessLevel = "H";
	        else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
	          accessLevel = "A";
	        else if("OPER_TERMINAL".equals(loginbean.getAccessType()))
	          accessLevel = "O";
	      
	        if(request.getParameterValues("originLoc")!=null && request.getParameterValues("originLoc").length>0)
	            origin = request.getParameterValues("originLoc");
	        
	        if(request.getParameterValues("destLoc")!=null && request.getParameterValues("destLoc").length>0)
	            destination = request.getParameterValues("destLoc");
	        
	        if(request.getParameter("customerId")!=null && request.getParameter("customerId").trim().length()>0)
	            customerId = request.getParameter("customerId");
	        
	        if(request.getParameter("QuoteId")!=null && request.getParameter("QuoteId").trim().length()>0)
	            quoteId = (request.getParameter("QuoteId")).trim();
	     /*   if(request.getParameter("shipmentMode")!=null && request.getParameter("shipmentMode").trim().length()>0)
	            shipmentMode =  Integer.parseInt(request.getParameter("shipmentMode"));*/
	        
	          if(request.getParameter("shipmentMode")!=null && request.getParameter("shipmentMode").trim().length()>0)
	      {
	      if("AIR".equalsIgnoreCase(request.getParameter("shipmentMode")))
	        shipmentMode =  1;
	        else if("SEA".equalsIgnoreCase(request.getParameter("shipmentMode")))
	         shipmentMode =  2;
	         else if("TRUCK".equalsIgnoreCase(request.getParameter("shipmentMode")))
	          shipmentMode =  4;
	      }
	      if(request.getParameter("CustomerName")!=null && request.getParameter("CustomerName").trim().length()>0)
	             customerName = request.getParameter("CustomerName");
	          if(request.getParameter("Status")!=null && request.getParameter("Status").trim().length()>0)
	             quoteStatus = request.getParameter("Status");  
	       if(request.getParameter("ActiveFlag")!=null && request.getParameter("ActiveFlag").trim().length()>0)
	             quoteActive = request.getParameter("ActiveFlag");
	 // logger.info("doModifyProcessqqqqqqqqq1111111"+quoteActive);
	 // logger.info("doModifyProcessqqqqqqqqq111111111"+quoteStatus);
	    //  if(masterDOB.getShipmentMode()==1)Status
	    if(quoteActive!=null &&  quoteActive.trim().length()>0)
	    {
	    if("Active".equalsIgnoreCase(quoteActive) || "ACTIVE".equalsIgnoreCase(quoteActive) || "A".equalsIgnoreCase(quoteActive))
	    {
	    quoteActive="A";
	    }
	    else if("InActive".equalsIgnoreCase(quoteActive) || "INACTIVE".equalsIgnoreCase(quoteActive) || "I".equalsIgnoreCase(quoteActive))
	    {
	    quoteActive="I";
	    }
	    //logger.info("doModifyProcessqqqqqqqqq"+quoteActive);//VLAKSHMI  
	    }
	    if(quoteStatus!=null && quoteStatus.trim().length()>0)
	    {
	if("Pending".equalsIgnoreCase(quoteStatus) || "PENDING".equalsIgnoreCase(quoteStatus) || "P".equalsIgnoreCase(quoteStatus))
	   {
	    quoteStatus="PEN";
	   }
	else if("Positive".equalsIgnoreCase(quoteStatus) ||"POSITIVE".equalsIgnoreCase(quoteStatus) || "POS".equalsIgnoreCase(quoteStatus))
		{
	  quoteStatus="ACC";
	  }
	else if("Rejected".equalsIgnoreCase(quoteStatus) || "REJECTED".equalsIgnoreCase(quoteStatus) || "R".equalsIgnoreCase(quoteStatus))
		{
	  quoteStatus="REJ";
	  }
	else if("Generated".equalsIgnoreCase(quoteStatus) || "GENERATED".equalsIgnoreCase(quoteStatus) || "G".equalsIgnoreCase(quoteStatus))
		{
	  quoteStatus="GEN";
	  }
	else if("Approved".equalsIgnoreCase(quoteStatus) || "APPROVED".equalsIgnoreCase(quoteStatus) || "A".equalsIgnoreCase(quoteStatus))
	    {
	     quoteStatus="APP";
	    }
	else if("Queued".equalsIgnoreCase(quoteStatus) || "QUEUED".equalsIgnoreCase(quoteStatus) || "Q".equalsIgnoreCase(quoteStatus))
		{
	  quoteStatus="QUE";
	  }
	  else if("NEGATIVE".equalsIgnoreCase(quoteStatus) || "Negative".equalsIgnoreCase(quoteStatus) || "N".equalsIgnoreCase(quoteStatus))
		{
	  quoteStatus="NAC";
	  }
	   //logger.info("doModifyProcessqqqqqqqqq"+quoteStatus);//VLAKSHMI
	    }     
	        masterDOB.setOriginLocation(origin); 
	        masterDOB.setDestLocation(destination);
	        masterDOB.setCustomerId(customerId);
	        masterDOB.setShipmentMode(shipmentMode);
	        masterDOB.setOperation(operation);
	        masterDOB.setAccessLevel(accessLevel);
	        masterDOB.setTerminalId(loginbean.getTerminalId());
	        masterDOB.setBuyRatesPermission(buyRatesPermission);
	        masterDOB.setUserId(loginbean.getEmpId());
	        masterDOB.setCompanyName(customerName);
	        masterDOB.setQuoteStatus(quoteStatus);
	        masterDOB.setActiveFlag(quoteActive);
	        if(quoteId!=null && quoteId.trim().length()!=0)
	          //masterDOB.setQuoteId(Long.parseLong(quoteId.trim()));  //@@ Commented by subrahmanyam for the enhancement #146971 on 2/12/08
	          masterDOB.setQuoteId(quoteId);  //@@ Added by subrahmanyam for the enhancement #146971 on 2/12/08
	      
	      home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
	      remote      = home.create();
	      
	      StringBuffer errors = remote.validateQuoteId(masterDOB);
	      
	      if(errors!=null && errors.length()>0)
	      { 
	    	  request.setAttribute("errors",errors.toString());
	          String originValue = "";
	          for(String s:origin)
	       	   originValue = s+",";
	          originValue = originValue.substring(0, originValue.length()-1);
	          
	          String destValue = "";
	          for(String s:destination)
	       	   destValue = s+",";
	          destValue = destValue.substring(0, destValue.length()-1);
	          
	         request.setAttribute("errors",errors.toString());
	         doDispatcher(request,response,"qms/QMSMultiQuoteEnterId.jsp?operation=Modify&origin="+originValue+"&destination="+destValue+"&customerId="+customerId+"&quoteId="+quoteId+"&customerName="+customerName+"&quoteStatus="+quoteStatus+"&quoteActive="+quoteActive);
	      }
	      else
	      {
	          finalDOB    = remote.getQuoteDetails(quoteId,buyRatesPermission,loginbean);
	         
	          if("View".equalsIgnoreCase(operation))
	          {
	               finalDOB.getHeaderDOB().setDateOfQuotation(finalDOB.getMasterDOB().getCreatedDate());   
	          }
	           attachmentIdList =   remote.getAttachmentDtls(finalDOB);
	          
	           if(attachmentIdList!=null && attachmentIdList.size()>0)
	           {
	          finalDOB.setAttachmentDOBList(attachmentIdList);
	           }
	           attachmentIdList= remote.getQuoteAttachmentDtls(finalDOB);
	           if(attachmentIdList!=null && attachmentIdList.size()>0)//@@ Added by govind for the issue 252166 for not getting default attachments in view case
	           {
	          finalDOB.setAttachmentDOBList(attachmentIdList);
	           }
	          session.setAttribute("viewFinalDOB",finalDOB);
	          
	          if(escalatedReport!=null && "self".equalsIgnoreCase(escalatedReport.trim()))
	          {
	        	 // finalDOB    = remote.getMasterInfo(quoteId,loginbean);
	        	  //finalDOB.setMasterDOB(masterDOB); 
	        	  	masterDOB = finalDOB.getMasterDOB();
	        	  	masterDOB.setOperation("View");
	        	  	finalDOB.setMasterDOB(masterDOB);
	              	finalDOB  = remote.getFreightSellRates(finalDOB);
	              	masterDOB.setOperation("View");
	              	finalDOB.setMasterDOB(masterDOB);  
	              	request.setAttribute("finalDOB",finalDOB);
	              	finalDOB  = doSellGetHeaderAndCharges(request,response);
	        	 // session.setAttribute("viewFinalDOB",finalDOB);
	              	//finalDOB =  doMarginTest(request,response,finalDOB);
	  	        	session.setAttribute("finalDOB",finalDOB); 
	  	        	session.setAttribute("selectedIndexList",finalDOB.getMultiQuoteSelectedBreaks());
	  	        	//request.setAttribute("isMarginTestPerformed","Y");
	        	//doDispatcher(request,response,"qms/QMSMultiQuoteMasterView.jsp");//Modified by Anil.k
	            //doDispatcher(request,response,"qms/QMSMultiQuoteChargesView.jsp");
	  	        	doDispatcher(request,response,"qms/QMSMultiQuoteEscalatedChargesSelect.jsp?MarginTest=Yes&count=1");
	          }
	    //@@ Commented by subrahmanyam for 146460
	         /* else
	             doDispatcher(request,response,"qms/QMSQuoteView.jsp");*/
	 //@@ Added by subrahmanyam for 146460
	        else{
	              masterDOB =finalDOB.getMasterDOB();
	             // String countryId  = null;
	                //@@ Added by subrahmanyam for the enhancement 146460
	             //@@ Added by subrahmanyam for the enhancement 146460
	                if("PDF".equalsIgnoreCase(request.getParameter("pdf")))
	                {
	                
	                        flagsDOB = finalDOB.getFlagsDOB();
	                           //@@Added by Kameswari for the WPBN issue - 175526 on 07-07-09
	                        countryId   = remote.getCountryId(masterDOB.getCustomerAddressId());
	                      masterDOB.setCountryId(countryId);
	                     //@@ WPBN issue - 175526 on 07-07-09
	                        contactPersons =    masterDOB.getCustContactNames();
	                        print   =  request.getParameter("print");
	                        email   =  request.getParameter("email");
	                        fax     =   request.getParameter("fax");
	                         if(print!=null&&"on".equalsIgnoreCase(print))
	                            flagsDOB.setPrintFlag("Y");
	                        else
	                            flagsDOB.setPrintFlag("N");
	                       
	                       // if(email!=null&&"on".equalsIgnoreCase(email))
	                         if(email!=null&&"on".equalsIgnoreCase(email) && !"QUE".equalsIgnoreCase(flagsDOB.getQuoteStatusFlag())) //@@Modified  by subrahmanyam for the wpbn issue 178144 on jul-30-09
	                             flagsDOB.setEmailFlag("Y");
	                        else
	                            flagsDOB.setEmailFlag("N");
	                       
	                        if(fax!=null&&"on".equalsIgnoreCase(fax))
	                            flagsDOB.setFaxFlag("Y");
	                        else
	                            flagsDOB.setFaxFlag("N");
	                            emailText = remote.getEmailText(masterDOB.getTerminalId(),"N");
	                            finalDOB.setEmailText(emailText);
	                            finalDOB.setFlagsDOB(flagsDOB);
	      // masterDOB.setOperation(operation);
	 
	      if(contactPersons!=null)
	      {
	    	  int contPerLen	=	contactPersons.length;
	        for(int i=0;i<contPerLen;i++)
	        {
	          if(i==(contactPersons.length-1))
	          {
	            if(masterDOB.getCustomerContactsEmailIds()[i]!=null && masterDOB.getCustomerContactsEmailIds()[i].trim().length()!=0)
	                toEmailIds.append(masterDOB.getCustomerContactsEmailIds()[i]);
	            if(masterDOB.getCustomerContactsFax()[i]!=null && masterDOB.getCustomerContactsFax()[i].trim().length()!=0)
	                toFaxIds.append(masterDOB.getCustomerContactsFax()[i]);
	              
	          }
	          else
	          {
	            if(masterDOB.getCustomerContactsEmailIds()[i]!=null && masterDOB.getCustomerContactsEmailIds()[i].trim().length()!=0)
	              toEmailIds.append(masterDOB.getCustomerContactsEmailIds()[i]).append(",");
	            if(masterDOB.getCustomerContactsFax()[i]!=null && masterDOB.getCustomerContactsFax()[i].trim().length()!=0)
	              toFaxIds.append(masterDOB.getCustomerContactsFax()[i]).append(",");
	          }
	        }
	      }
	      else
	      {
	        toEmailIds.append(finalDOB.getHeaderDOB().getCustEmailId());
	        toFaxIds.append(finalDOB.getHeaderDOB().getCustFaxNo());
	      }
	      //@@Added by Kameswari for the WPBN issue-61306
	      if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
	      {
	         toEmailIds.append(",").append(masterDOB.getSalesPersonEmail());
	      }
	                       // flagsDOB.setPrintFlag("Y");
	                          /* flagsDOB.setEmailFlag("N");
	                        flagsDOB.setFaxFlag("N");
	                        */
	                        String actionPage    =   "QMSMultiQuoteController?from=summary";
	                        //added by subrahmanyam for 185127 on 02-oct-09
	                        serviceLevelDes = remote.getServiceLevelDesc(quoteId);
	                        finalDOB.getHeaderDOB().setTypeOfService(serviceLevelDes);
	                        //ended by subrahmanyam for 185127 on 02-oct-09
	                        int  mailStatus  = doPDFGeneration(finalDOB,request,response);
	                          //
	                         if(mailStatus == 3 || mailStatus == 7)
	                  {
	                    setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & E-mail Has Been Sent To :"+toEmailIds.toString()+" & Fax Has Been Sent To :"+toFaxIds , operation , "QMSMultiQuoteController");
	                  }
	                  else if(mailStatus == 2  || mailStatus == 6)
	                  {
	                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()))
	                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax Has Been Sent To "+toFaxIds , operation , "QMSMultiQuoteController");
	                    else
	                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & Fax Has Been Sent To :"+toFaxIds , operation , "QMSMultiQuoteController");
	                  }
	                  else if(mailStatus == 1 || mailStatus == 5)
	                  {
	                    if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
	                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & E-mail Has Been Sent To :"+toEmailIds.toString()+" & Fax Has Been Sent To :"+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
	                    else
	                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & E-mail Has Been Sent To :"+toEmailIds.toString(), operation , "QMSMultiQuoteController");
	                  }
	                  else if(mailStatus == 0  || mailStatus==4)
	                  {
	                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "N".equalsIgnoreCase(flagsDOB.getFaxFlag()))
	                      setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)"), operation , "QMSMultiQuoteController");
	                    else if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) && "N".equalsIgnoreCase(flagsDOB.getEmailFlag()))
	                      setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To : "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
	                    else if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
	                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To : "+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax Has Been Sent To :"+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
	                    else
	                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId, operation , "QMSMultiQuoteController");
	                      
	                  }
	                        //
	                        //setErrorRequestValues(request, "MSG", "The PDF was successfully generated for Quote Id "+masterDOB.getQuoteId()+".", operation , actionPage);
	                        session.removeAttribute("viewFinalDOB");
	                        session.removeAttribute("PreFlagsDOB");
	                        doDispatcher(request, response, "QMSErrorPage.jsp"); 
	                }
	          else
	        	  doDispatcher(request,response,"qms/QMSMultiQuoteMasterView.jsp");//Modified by Anil.k
	             //doDispatcher(request,response,"qms/QMSMultiQuoteView.jsp");
	         }
	    //@@ Ended by subrahmanyam for the enhancement  146460

	    //@@ Ended by subrahmanyam for the enhancement  146460
	                
	      }
	    }
	    catch(Exception e)
	    {
	      //Logger.error(FILE_NAME,"Error in Controller in doModifyProcess");
	      logger.error(FILE_NAME+"Error in Controller in doModifyProcess");
	      e.printStackTrace();
	      throw new ServletException();
	    }
  }
public void doForwardToInitialJSP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String  operation = null;
   
    try
    {
      operation = request.getParameter("Operation");
      //Logger.info(FILE_NAME,"In doForwardToInitialJSP operation :"+operation);
        if("Add".equalsIgnoreCase(operation))
      {
       doDispatcher(request,response,"qms/QMSMultiQuoteMaster.jsp");
      }
      else if("View".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation)  || "Copy".equalsIgnoreCase(operation))
      {
        doDispatcher(request,response,"qms/QMSMultiQuoteEnterId.jsp");
      }
      else if("Grouping".equalsIgnoreCase(operation))
      {
        doDispatcher(request,response,"qms/QMSMultiQuoteGroupingEnterId.jsp");
      }
    }
    catch(Exception ex)
		{
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [setMultiQuoteMasterDOB()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [setMultiQuoteMasterDOB()] -> "+ex.toString());
		}
    }
  
  public void doDispatcher(HttpServletRequest request, HttpServletResponse response, String forwardFile) throws ServletException, IOException
	{
		//Logger.info(FILE_NAME,"in doDispatcher forwardFile::"+forwardFile);

  try
		{
    
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
   // logger.info("forwardFile"+forwardFile);
			rd.forward(request, response);
    
    //response.sendRedirect(request.getContextPath()+"/"+forwardFile);
    //logger.info("after redirect");
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
    logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile, ex);
		}
	}
  
  private void setErrorRequestValues(HttpServletRequest request,
			String errorCode,
			String errorMessage,
			String operation,
			String nextNavigation) throws ServletException, IOException
{
ErrorMessage	          errorMessageObject  =  null;
ArrayList		            keyValueList		    =  new ArrayList();
//@@Added by Yuvraj for WPBN-10261
HttpSession             session             =  request.getSession();
MultiQuoteFinalDOB           finalDOB            =  null;
ESupplyDateUtility      dateFormatter       =  new ESupplyDateUtility();
ESupplyGlobalParameters loginbean           =  null;
String                  validTo             =  null;
//@@Yuvraj
try
{
errorMessageObject = new ErrorMessage(errorMessage, nextNavigation);
keyValueList.add(new KeyValue("ErrorCode", errorCode));
keyValueList.add(new KeyValue("Operation",operation));
//@@Added by Yuvraj for WPBN-10261
if((session.getAttribute("finalDOB")!=null || session.getAttribute("viewFinalDOB")!=null) && "SUMMARY".equalsIgnoreCase(request.getParameter("subOperation")))
{
loginbean = (ESupplyGlobalParameters)session.getAttribute("loginbean");
dateFormatter.setPattern(loginbean.getUserPreferences().getDateFormat());

if(session.getAttribute("finalDOB")!=null)
finalDOB  = (MultiQuoteFinalDOB)session.getAttribute("finalDOB");
else if(session.getAttribute("viewFinalDOB")!=null)
finalDOB  = (MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");

keyValueList.add(new KeyValue("quoteId",""+finalDOB.getMasterDOB().getQuoteId()));
if(finalDOB.getMasterDOB().getValidTo()!=null)
{
validTo				=	 dateFormatter.getDisplayString(finalDOB.getMasterDOB().getValidTo());
keyValueList.add(new KeyValue("validTo",validTo));
}
if(!finalDOB.isMultiModalQuote())
{
keyValueList.add(new KeyValue("shipmentMode",""+finalDOB.getMasterDOB().getShipmentMode()));
}
if(!finalDOB.isMultiModalQuote())
{
keyValueList.add(new KeyValue("customerName",""+finalDOB.getMasterDOB().getCustomerId()));//added by silpa.p on 3-06-11
}
}
//@@Yuvraj
errorMessageObject.setKeyValueList(keyValueList);
request.setAttribute("ErrorMessage",errorMessageObject);
}
catch(Exception ex)
{
//Logger.error(FILE_NAME, " [setSessionValues(request, errorCode, errorMessage, operation, nextNavigation)] -> "+ex.toString());
logger.error(FILE_NAME+ " [setSessionValues(request, errorCode, errorMessage, operation, nextNavigation)] -> "+ex.toString());
}
}  
 
  /**
	 * This method helps in validating the master info and forwards request to the next jsp as per the operation from the initial screen 
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doMasterinfo request

	 * @exception ServletException if the request for the doMasterInfo could not be handled.
	 */
public void doMasterInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
{
  String                    operation         = null;
  MultiQuoteMasterDOB            masterDOB         = null;
  ESupplyGlobalParameters   loginbean	        = null;
  StringBuffer              errors            = new StringBuffer();
  MultiQuoteFinalDOB             finalDOB          = null;
  MultiQuoteTiedCustomerInfo     tiedCustomerInfo  = null;
  QMSMultiQuoteSessionHome       home              = null;
  QMSMultiQuoteSession           remote            = null;
  MultiQuoteFlagsDOB             flagsDOB          = null;
  MultiQuoteFreightLegSellRates  legDOB            = null;
  MultiQuoteFreightLegSellRates  tempDOB           = null;
  MultiQuoteFreightLegSellRates  tiedCustLegDOB    = null;
  ArrayList                 freightRates      = null;
  ArrayList                 frtTiedCustInfo   = null;
  ArrayList                 attachmentIdList  = null;
  int                       freightRatesSize  = 0;
  StringBuffer              sb                = new StringBuffer("");
  boolean                   isMarginDefined   = true;
  
  //long quoteId; //@@ Commented by subrahmanyam for the enhancement #146971 on 2/12/08
  String quoteId = null; //@@ Added by subrahmanyam for the enhancement #146971 on 2/12/08
  String                    status            = "";
  String                    errStatus         = "";
  String                    msgStatus         = "";
  
  int                       tiedCustInfoSize  = 0;
  boolean                   isTiedCustInfo    = true;
  ArrayList                 keyList           = new ArrayList();
  int                       keySize           = 0;
  boolean                   isMultiModal      = false;
  HttpSession  session       =  null;
  boolean                   finalForwardFlag  = true;//@@If true, then forward to the third screen.
  int orgLocLen								  = 0;
  int count = 0;    
  ESupplyDateUtility        eSupplyDateUtility  = new ESupplyDateUtility();
  try
  {
    operation   = request.getParameter("Operation");
    session     = request.getSession();
    loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
    //lookUpBean  = new LookUpBean();
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = home.create();
    
    /*if("View".equalsIgnoreCase(operation))
    	finalDOB = (MultiQuoteFinalDOB) request.getSession().getAttribute("viewFinalDOB");
    else*/
    finalDOB  = setQuoteMasterDOB(request,response,loginbean);//set the master dob   define new mwthod
    
    
    
    masterDOB = finalDOB.getMasterDOB(); 
     if(!"View".equalsIgnoreCase(operation) && !"Charges".equalsIgnoreCase(request.getParameter("Quotewith")))
        errors    = remote.validateQuoteMaster(finalDOB);//validate the data set in the master dob
    
    if(errors.length()==0 )//if no errors are there
    {
      ArrayList legDetails = finalDOB.getLegDetails();
      int legSize = legDetails.size();
      boolean isSpotRates  = true;
      
      for(int i=0;i<legSize;i++)
      {
        legDOB  = (MultiQuoteFreightLegSellRates)legDetails.get(i);
        if(!legDOB.isSpotRatesFlag())
            isSpotRates = false;
      }
      //finalDOB.setSelectedOriginChargesListIndices(null);
      //finalDOB.setSelctedDestChargesListIndices(null); 
      if(masterDOB.getAttentionToDetails()!=null)
      {
        remote.updateAttentionToContacts(masterDOB.getAttentionToDetails());//added by phani sekhar for wpbn 167678      
      }
      if("Save & Exit".equalsIgnoreCase(request.getParameter("submit")))//Modified by Anil.k for Save and Exit in 1st Screen
      {
        finalDOB.setSelectedOriginChargesListIndices(null);
        finalDOB.setSelctedDestChargesListIndices(null);
       if("Charges".equalsIgnoreCase(request.getParameter("Quotewith")))
        finalDOB = doQuoteWithChargesProcess(request,response,finalDOB);
        
        for(int i=0;i<legSize;i++)
        {
          legDOB  = (MultiQuoteFreightLegSellRates)legDetails.get(i);
          legDOB.setSelectedFreightChargesListIndices(null);
          legDetails.remove(i);
          legDetails.add(i,legDOB);            
        }
        finalDOB.setLegDetails(legDetails);
        
        if(!"Modify".equalsIgnoreCase(operation))
        {
          status    = "Inserted";
          errStatus = "Inserting";
          msgStatus = "RSI";
          flagsDOB = new MultiQuoteFlagsDOB();
          flagsDOB.setCompleteFlag("I");
          flagsDOB.setInternalExternalFlag("I");
          flagsDOB.setQuoteStatusFlag("GEN");
          flagsDOB.setSentFlag("U");
          flagsDOB.setActiveFlag("A");
          flagsDOB.setEscalationFlag("N");
          
          if(isSpotRates)
            finalDOB.setSpotRatesFlag("Y");
          else
            finalDOB.setSpotRatesFlag("N");
            
          finalDOB.setFlagsDOB(flagsDOB);
          masterDOB.setVersionNo(1);//@@The version for this quote will be 1
          finalDOB.setMasterDOB(masterDOB); 
          quoteId = remote.insertQuoteMasterDtls(finalDOB);//insert the quote details
        }
        else
        {
         status   = "Modified";
         errStatus = "Modifying";
         msgStatus = "RSM";
         flagsDOB = (MultiQuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
         if(flagsDOB!=null)
            finalDOB.setFlagsDOB(flagsDOB);
         else
            finalDOB.setFlagsDOB(new MultiQuoteFlagsDOB());
          
          finalDOB.setCompareFlag(true);//@@For Save & Exit, no new Version will be created.
          
          quoteId = remote.modifyQuoteMasterDtls(finalDOB);
        }
        //if(quoteId>0)  //@@ Commented by subrahmanyam for the enhancement #146971 on 2/12/08
        if(quoteId!=null)  //@@ Added by subrahmanyam for the enhancement #146971 on 2/12/08
        {
          setErrorRequestValues(request, msgStatus, "The Quote Details are Succesfully "+status+" with the QuoteId "+quoteId, operation , "QMSQuoteController");
          doDispatcher(request, response, "ESupplyErrorPage.jsp");
        }
        else
        {
          setErrorRequestValues(request, "ERR", "An error occured while "+errStatus+" the Quote Details ", operation , "QMSQuoteController");
          doDispatcher(request, response, "ESupplyErrorPage.jsp");
        }
      }//Ended by Anil.k for Save and Exit in 1st Screen
      else if("Next >>".equalsIgnoreCase(request.getParameter("submit")))
      {          
          long start = System.currentTimeMillis();
        /*  if(isSpotRates)//if spot rates are given, forward directly 
          {                               //to the charges select page
            finalDOB          =   getMarginLimit(finalDOB);
            finalDOB.setSpotRatesFlag("Y");
          
            freightRates      =   finalDOB.getLegDetails();
            freightRatesSize  =   freightRates.size();
             
            for(int i=0;i<freightRatesSize;i++)
            {
              legDOB          =  (MultiQuoteFreightLegSellRates)freightRates.get(i);
              keyList.add(""+legDOB.getShipmentMode());
               
              if(!legDOB.isMarginFlag())
              {
                  sb.append("&nbsp;").append(legDOB.getOrigin()).append("-").append(legDOB.getDestination());
                  isMarginDefined = false;
              }
            }
            keySize   =  keyList.size();
           
            for(int i=0;i<freightRatesSize;i++)
            {
              tempDOB = (MultiQuoteFreightLegSellRates)freightRates.get(i);
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
            
            if(!isMarginDefined)
            {
              request.setAttribute("error","The Margin Limit is Not Defined For the Selected Shipment Mode & Service Level for the Leg(s): "+sb.toString());
              session.setAttribute("finalDOB",finalDOB);
              doDispatcher(request, response, "qms/QMSQuoteMaster.jsp");
            }
          }*/
          /*else//@@Tied Customer Info Functionallity Changed according to changed requirements.
          {
            finalDOB            = remote.getQuoteTiedCustomerInfo(finalDOB);
        
            frtTiedCustInfo     = finalDOB.getTiedCustomerInfoFreightList();
          
            if(frtTiedCustInfo!=null)
              tiedCustInfoSize  = frtTiedCustInfo.size();
          }*/
          if(isMarginDefined)
          {
            freightRates      =   finalDOB.getLegDetails();
           
            freightRatesSize  =   freightRates.size();
           /* for(int i=0;i<freightRatesSize;i++)
            {
              legDOB          =  (MultiQuoteFreightLegSellRates)freightRates.get(i);
              if(frtTiedCustInfo!=null)
                tiedCustLegDOB  =  (MultiMultiQuoteFreightLegSellRates)frtTiedCustInfo.get(i);
              keyList.add(""+legDOB.getShipmentMode());
              
              if(tiedCustLegDOB!=null)
              {
                legDOB.setTiedCustInfoFlag(true);
                legDOB.setSpotRatesType(tiedCustLegDOB.getSpotRatesType());
                legDOB.setServiceLevel(tiedCustLegDOB.getServiceLevel());
              }
                
              if(!legDOB.isSpotRatesFlag() && !legDOB.isTiedCustInfoFlag())
                legDOB.setForwardFlag(false);
              else
                legDOB.setForwardFlag(true);
            }
            keySize   =  keyList.size();
            for(int i=0;i<freightRatesSize;i++)
            {
              legDOB          =  (MultiQuoteFreightLegSellRates)freightRates.get(i);
             
              if(!legDOB.isForwardFlag())
                finalForwardFlag  = false;
              tempDOB = (MultiQuoteFreightLegSellRates)freightRates.get(i);
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
            }*/
             
          /*  if(!isSpotRates && finalForwardFlag)
              finalDOB          =   getMarginLimit(finalDOB);*/
            //@@Added by kiran.v on 19/01/2012 for backbutton in modify
        	//if("Modify".equalsIgnoreCase(operation)){
           // Timestamp custDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("createdDate"));
           // System.out.print("==custDate==="+custDate);
           // finalDOB.getMasterDOB().setCreatedDate(custDate);
        	//}
            if(masterDOB.getQuoteWith().equalsIgnoreCase("Charges"))
            {
            	if("View".equalsIgnoreCase(operation))
                {
            		request.setAttribute("finalDOB",finalDOB);
            		finalDOB = doQuoteWithChargesProcess(request,response,finalDOB);
            		//finalDOB  = doGetHeaderAndCharges(request,response);
            		session.setAttribute("viewFinalDOB",finalDOB);
            		request.setAttribute("fromWhere","qms/QMSMultiQuoteMasterView.jsp");
            		doDispatcher(request, response, "qms/QMSMultiQuoteChargesView.jsp?count="+count);
                }
            	else
            	{
            	request.setAttribute("finalDOB",finalDOB);
            	finalDOB = doQuoteWithChargesProcess(request,response,finalDOB);
            	//finalDOB  = doGetHeaderAndCharges(request,response);
                session.setAttribute("finalDOB",finalDOB);
                request.setAttribute("fromWhere","qms/QMSMultiQuoteMaster.jsp");
                
                doDispatcher(request, response, "qms/QMSMultiQuoteChargesSelect.jsp?count="+count);
            }
            }
            else
            {
              finalDOB  = remote.getFreightSellRates(finalDOB);
              System.out.println("finalDOB---------->"+finalDOB);
              finalDOB.setSpotRatesFlag("N");
             // finalDOB.setMultiModalQuote(isMultiModal);
         
              //session.setAttribute("finalDOB",finalDOB);
              request.setAttribute("finalDOB",finalDOB);
              if("View".equalsIgnoreCase(operation))
              {
                session.setAttribute("viewFinalDOB",finalDOB);
                request.setAttribute("fromWhere","qms/QMSMultiQuoteFreightSellRatesView.jsp");
                doDispatcher(request, response, "qms/QMSMultiQuoteFreightSellRatesView.jsp");
              }
              else
              {
            	 
                session.setAttribute("finalDOB",finalDOB);
                request.setAttribute("fromWhere","qms/QMSMultiQuoteFreightSellRates.jsp");
                logger.info("Server Response Time for 2nd screen in milli seconds :    "+ ((System.currentTimeMillis()) - start) + "    User Id :: "+ finalDOB.getMasterDOB().getUserId()+ " Customer Id :: "+finalDOB.getMasterDOB().getCustomerId());
                doDispatcher(request, response, "qms/QMSMultiQuoteFreightSellRates.jsp");
              }
            }
          }
      }
    }
    else//if some errors are there forward it to the Master Page and display the errors and the master information that is set
    {
      request.setAttribute("errors",errors);
      request.setAttribute("finalDOB",finalDOB);
      session.setAttribute("finalDOB",finalDOB);
      doDispatcher(request, response, "qms/QMSMultiQuoteMaster.jsp");
    }
    
  }
  catch(Exception ex)
		{
    session.removeAttribute("finalDOB");
    session.removeAttribute("PreFlagsDOB"); 
    ex.printStackTrace();
			//Logger.error(FILE_NAME, " [error in doMasterInfo()] -> "+ex.toString());
    logger.error(FILE_NAME+ " [error in doMasterInfo()] -> "+ex.toString());
    setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSMultiQuoteController?Operation=Add");
    doDispatcher(request, response, "ESupplyErrorPage.jsp");
		}
}  
  
  

/**
 * This method is used to set the master info from the request to the MultiMultiQuoteMasterDOB
 *
 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
* @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
 * @param loginbean an object which stores the login info
* 
 * @exception IOException if an input or output error is detected when the servlet handles the doMasterInfo request
 * @exception ServletException if the request for the doMasterInfo could not be handled.
 */
public MultiQuoteFinalDOB setQuoteMasterDOB(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean) throws ServletException, IOException
{
MultiQuoteMasterDOB            masterDOB           = null;
MultiQuoteFinalDOB             finalDOB            = null;
ESupplyDateUtility        eSupplyDateUtility  = new ESupplyDateUtility();
String                    operation           = request.getParameter("Operation");
String                    accessLevel         = "";
String 					  weightBreak		  = "";
ArrayList                 legDetails          = null;
MultiQuoteFreightLegSellRates  legDOB         = null;
MultiQuoteFreightLegSellRates  chargeid         = null;
String[]					orgLoc            = null;
String[]				   orgPort            = null;
String[]					dstLoc            = null;
String[]					dstPort            = null;
long                      quoteId             = 0;
HttpSession  session       =  null;
HashMap attentionLOV = null; // @@ added by phani sekhar for wpbn 167678
QMSMultiQuoteSessionHome       home1              = null;//Added by Rakesh
QMSMultiQuoteSession           remote1            = null;//Added by Rakesh
java.sql.Timestamp   created_date                 =null; //@@Added by kiran.v
	try
	{
  if("View".equalsIgnoreCase(operation))
	finalDOB = (MultiQuoteFinalDOB)request.getSession().getAttribute("viewFinalDOB");
  else
    finalDOB = (MultiQuoteFinalDOB)request.getSession().getAttribute("finalDOB");
  if(finalDOB!=null)
  created_date=finalDOB.getMasterDOB().getCreatedDate();//@@Added by kiran.v 
		if(finalDOB==null)
  {
    finalDOB  = new MultiQuoteFinalDOB();
	masterDOB = new MultiQuoteMasterDOB();
  }
  else//modified  by govind for detail view mails on 25-05-11
  {
    if("View".equalsIgnoreCase(operation) && finalDOB!=null)
    	masterDOB = finalDOB.getMasterDOB();//ended
  else
    masterDOB = new MultiQuoteMasterDOB();
  }//ended
  
  if(finalDOB.getMasterDOB()!=null)
  {
      masterDOB.setQuoteId(finalDOB.getMasterDOB().getQuoteId());
      masterDOB.setVersionNo(finalDOB.getMasterDOB().getVersionNo());
      masterDOB.setUniqueId(finalDOB.getMasterDOB().getUniqueId());
      masterDOB.setUniqueIds(finalDOB.getMasterDOB().getUniqueIds());//Added by Anil.k for Spot Rates
  }
 //System.out.println("for load test---QMSMultiQuoteController--"+masterDOB);      
  session     = request.getSession();
  
  Hashtable accessList  =  (Hashtable)session.getAttribute("accessList");
    
  //@@setting the buy rates permissions flag based on user role permissions.
  if(accessList.get("10605")!=null)
    masterDOB.setBuyRatesPermission("Y");
  else
    masterDOB.setBuyRatesPermission("N");
 // Logger.info(FILE_NAME,"accessListaccessList:::"+accessList.get("10605"));
  
   if("HO_TERMINAL".equals(loginbean.getAccessType()))
      accessLevel = "H";
  else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
      accessLevel = "A";
  else if("OPER_TERMINAL".equals(loginbean.getAccessType()))
      accessLevel = "O";
  
  if(request.getParameter("quoteId")!=null && request.getParameter("quoteId").trim().length()!=0)
    //masterDOB.setQuoteId(Integer.parseInt(request.getParameter("quoteId")));  //@@ Commented by subrahmanyam for the enhancement #146971 on 2/12/08
    masterDOB.setQuoteId(request.getParameter("quoteId"));  //@@ Added by subrahmanyam for the enhancement #146971 on 2/12/08
  if(request.getParameter("shipmentMode")!=null)
		{
			if("Air".equalsIgnoreCase(request.getParameter("shipmentMode")))
				masterDOB.setShipmentMode(1);
			else if("Sea".equalsIgnoreCase(request.getParameter("shipmentMode")))
				masterDOB.setShipmentMode(2);
    else if("Truck".equalsIgnoreCase(request.getParameter("shipmentMode")))
				masterDOB.setShipmentMode(4);
		}
  if(request.getParameter("WeightBreak")!=null && request.getParameter("WeightBreak").trim().length()!=0)
	  masterDOB.setMultiquoteweightBrake(request.getParameter("WeightBreak"));
  
  if(request.getParameter("preQuoteId")!=null && request.getParameter("preQuoteId").trim().length()!=0)
    //masterDOB.setPreQuoteId(Integer.parseInt(request.getParameter("preQuoteId")));  //@@ Commented By subrahmanyam for the Enhancement #146971 on 2/12/08
  masterDOB.setPreQuoteId(request.getParameter("preQuoteId"));  //@@ Commented By subrahmanyam for the Enhancement #146971 on 2/12/08
  if(request.getParameter("impFlag")!=null && request.getParameter("impFlag").trim().length()!=0)
		{
			if("U".equalsIgnoreCase(request.getParameter("impFlag")))
				masterDOB.setImpFlag(false);
			else if("I".equalsIgnoreCase(request.getParameter("impFlag")))
				masterDOB.setImpFlag(true);
		}
  
  eSupplyDateUtility.setPattern(loginbean.getUserPreferences().getDateFormat());

  if(request.getParameter("effDate")!=null && request.getParameter("effDate").trim().length()!=0)
  {
    Timestamp effDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("effDate"));
    masterDOB.setEffDate(effDate);
  }
  if(request.getParameter("modifiedDate")!=null && request.getParameter("modifiedDate").trim().length()!=0)
  {
    Timestamp modifiedDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("modifiedDate"));
    masterDOB.setModifiedDate(modifiedDate);
  }else{
	  if("View".equalsIgnoreCase(operation))
		  masterDOB.setModifiedDate(((MultiQuoteFinalDOB) request.getSession().getAttribute("viewFinalDOB")).getMasterDOB().getModifiedDate());
		  /*if("View".equalsIgnoreCase(operation))
	  	   finalDOB = (MultiQuoteFinalDOB) request.getSession().getAttribute("viewFinalDOB");
	  	   else
	  	 */
  }

  
  if(request.getParameter("validTo")!=null && request.getParameter("validTo").trim().length()!=0)
  {
    Timestamp validTo = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("validTo"));
    masterDOB.setValidTo(validTo);
  }
  
  if(request.getParameter("createdDate")!=null && request.getParameter("createdDate").trim().length()!=0)
  {
    Timestamp createdDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("createdDate"));
      if("View".equalsIgnoreCase(operation))
      {
          masterDOB.setCreatedDate(createdDate);
      }
      else if("Modify".equalsIgnoreCase(operation)){//@@Modified by kiran.v for back button in modify
   // masterDOB.setCreatedDate(new java.sql.Timestamp((new java.util.Date()).getTime()));
    	  masterDOB.setCreatedDate(created_date);
      }
      else
    	  masterDOB.setCreatedDate(new java.sql.Timestamp((new java.util.Date()).getTime()));
    }
  
  if(request.getParameter("accValidityPeriod")!=null && request.getParameter("accValidityPeriod").trim().length()!=0)
    masterDOB.setAccValidityPeriod(Integer.parseInt(request.getParameter("accValidityPeriod")));
  
  if(request.getParameter("customerId")!=null && request.getParameter("customerId").trim().length()!=0)
    masterDOB.setCustomerId(request.getParameter("customerId"));
  
//Added by Rakesh on 23-02-2011 for  Issue:236359
  if(request.getParameter("custDate")!=null && request.getParameter("custDate").trim().length()!=0){
	  Timestamp custDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("custDate"));
	  masterDOB.setCustDate(custDate);
	}
  if(request.getParameter("custTime")!=null && request.getParameter("custTime").trim().length()!=0){
	  masterDOB.setCustTime(request.getParameter("custTime"));
	}
  //Ended by Rakesh on 23-02-2011 for  Issue:236359
  
  
  if(request.getParameter("addressId")!=null && request.getParameter("addressId").trim().length()!=0)
    masterDOB.setCustomerAddressId(Integer.parseInt(request.getParameter("addressId")));
  
  if(request.getParameterValues("contactPersons")!=null && request.getParameterValues("contactPersons").length!=0)
  {
	    masterDOB.setCustContactNames(request.getParameterValues("contactPersons"));
      //adeed by phani sekhar for wpbn 167678 on 20090415
      if(request.getParameter("userModifiedMailIds")!=null && request.getParameter("userModifiedMailIds").length()>0)
    masterDOB.setCustomerContactsEmailIds(request.getParameter("userModifiedMailIds").split("&"));   
    masterDOB.setCustomerContacts(request.getParameter("contactIds").split(","));
     if(request.getParameter("attentionCustomerId")!=null && request.getParameter("attentionCustomerId").length()>0)
     {
    attentionLOV = new HashMap();
    
    if(request.getParameter("attentionCustomerId")!=null && request.getParameter("attentionCustomerId").length()>0)
    attentionLOV.put("customerId",request.getParameter("attentionCustomerId").split(","));
    
    if(request.getParameter("attentionSlno")!=null && request.getParameter("attentionSlno").length()>0)
    {
    attentionLOV.put("slNo",request.getParameter("attentionSlno").split(","));
    	masterDOB.setCustomerContacts(request.getParameter("attentionSlno").split(","));
    }
    
    if(request.getParameter("attentionEmailId")!=null && request.getParameter("attentionEmailId").length()>0)
    {
    attentionLOV.put("emailId",request.getParameter("attentionEmailId").split(","));
    	masterDOB.setCustomerContactsEmailIds((request.getParameter("attentionEmailId").split(",")));
    }
    
    if(request.getParameter("attentionFaxNo")!=null && request.getParameter("attentionFaxNo").length()>0)
    {
    attentionLOV.put("faxNo",request.getParameter("attentionFaxNo").split(","));
    	masterDOB.setCustomerContactsFax(((request.getParameter("attentionFaxNo").split(","))));
    }
    
     if(request.getParameter("attentionContactNo")!=null && request.getParameter("attentionContactNo").length()>0)
     {
    attentionLOV.put("contactNo",request.getParameter("attentionContactNo").split(","));
    	 masterDOB.setCustomerContactNo((((request.getParameter("attentionContactNo").split(",")))));
     }
    masterDOB.setAttentionToDetails(attentionLOV);
     }
     else
     {
       if(finalDOB.getMasterDOB()!=null)
        masterDOB.setAttentionToDetails(finalDOB.getMasterDOB().getAttentionToDetails());
     }
    //ends 167768
  }//added by rk
       
  if(request.getParameter("createdBy")!=null && request.getParameter("createdBy").trim().length()!=0)
    masterDOB.setCreatedBy(request.getParameter("createdBy"));
  
  if(request.getParameter("salesPersonCode")!=null && request.getParameter("salesPersonCode").trim().length()!=0)
    masterDOB.setSalesPersonCode(request.getParameter("salesPersonCode"));
  
  //@@Added by kameswari for the WPBN issue-61306
  if(request.getParameter("salesPersonFlag")!=null && "0".equalsIgnoreCase(request.getParameter("salesPersonFlag")))
    masterDOB.setSalesPersonFlag("Y");
    else
     masterDOB.setSalesPersonFlag("N");
  //@@WPBN issue-61306
  if(request.getParameter("industryId")!=null && request.getParameter("industryId").trim().length()!=0)
    masterDOB.setIndustryId(request.getParameter("industryId"));
  
  if(request.getParameter("commodityType")!=null && request.getParameter("commodityType").trim().length()!=0)
    masterDOB.setCommodityType(request.getParameter("commodityType"));
  
  if(request.getParameter("commodityId")!=null && request.getParameter("commodityId").trim().length()!=0)
    masterDOB.setCommodityId(request.getParameter("commodityId"));
  
  if(request.getParameter("hazardousInd")!=null && request.getParameter("hazardousInd").trim().length()!=0)
    masterDOB.setHazardousInd(true);
  else
    masterDOB.setHazardousInd(false);
  
  if(request.getParameter("unNo")!=null && request.getParameter("unNo").trim().length()!=0)
    masterDOB.setUnNumber(request.getParameter("unNo"));
  
  if(request.getParameter("commodityClass")!=null && request.getParameter("commodityClass").trim().length()!=0)
    masterDOB.setCommodityClass(request.getParameter("commodityClass"));
  
  if(request.getParameter("serviceLevelId")!=null && request.getParameter("serviceLevelId").trim().length()!=0)
    masterDOB.setServiceLevelId(request.getParameter("serviceLevelId"));
 // if(request.getParameter("quotingStation")!=null && request.getParameter("quotingStation").trim().length()!=0)
	    masterDOB.setQuotingStation(loginbean.getTerminalId());
  
  masterDOB.setQuoteWith(request.getParameter("Quotewith")!=null?request.getParameter("Quotewith"):"");
  if("Charges".equalsIgnoreCase(request.getParameter("Quotewith")))
  {	  
	  masterDOB.setOriginLocation(request.getParameterValues("originLocCharge"));
	  masterDOB.setDestLocation(request.getParameterValues("destLocCharge"));
	  masterDOB.setOriginPort(request.getParameterValues("originLocCharge"));
	  masterDOB.setDestPort(request.getParameterValues("destLocCharge"));
	 //Added by Anil.k for ZipCodes Quote With Charges on 1Mar2011
	  if(request.getParameterValues("shipperZipCodeChg")!=null )
		    masterDOB.setShipperZipCode(request.getParameterValues("shipperZipCodeChg"));

	  if(request.getParameterValues("shipperZoneChg")!=null )
		    masterDOB.setShipperZones(request.getParameterValues("shipperZoneChg"));
		  
	  if(request.getParameterValues("consigneeZoneChg")!=null )
		    masterDOB.setConsigneeZones(request.getParameterValues("consigneeZoneChg"));
			  
	  if(request.getParameterValues("consigneeZipCodeChg")!=null )
			    masterDOB.setConsigneeZipCode(request.getParameterValues("consigneeZipCodeChg"));
	  if(request.getParameterValues("chargeincoTerms")!= null)
		       masterDOB.setIncoTermsId(request.getParameterValues("chargeincoTerms"));
	  if(request.getParameterValues("chargecargoAcceptance")!=null )
		    masterDOB.setCargoAcceptance(request.getParameterValues("chargecargoAcceptance"));
	  if(request.getParameterValues("chargecargoAccPlace")!= null )
		         masterDOB.setCargoAccPlace(request.getParameterValues("chargecargoAccPlace"));
	//Ended by Anil.k for ZipCodes Quote With Charges on 1Mar2011
  }
  else
  {
//Govind starts here
  if(request.getParameterValues("cargoAcceptance")!=null )
	    masterDOB.setCargoAcceptance(request.getParameterValues("cargoAcceptance"));
	  
 if(request.getParameterValues("cargoAccPlace")!=null )
	    masterDOB.setCargoAccPlace(request.getParameterValues("cargoAccPlace"));
  if(request.getParameterValues("incoTerms")!=null )
    masterDOB.setIncoTermsId(request.getParameterValues("incoTerms"));
  
  if(request.getParameterValues("originLoc")!=null )
    masterDOB.setOriginLocation(request.getParameterValues("originLoc"));
  
  if(request.getParameterValues("originPort")!=null )
	    masterDOB.setOriginPort(request.getParameterValues("originPort"));
  
  if(request.getParameterValues("shipperZipCode")!=null )
    masterDOB.setShipperZipCode(request.getParameterValues("shipperZipCode"));

  if(request.getParameterValues("shipperZone")!=null )
    masterDOB.setShipperZones(request.getParameterValues("shipperZone"));
  
  if(request.getParameterValues("consigneeZone")!=null )
    masterDOB.setConsigneeZones(request.getParameterValues("consigneeZone"));
  
  if(request.getParameterValues("destLoc")!=null )
	    masterDOB.setDestLocation(request.getParameterValues("destLoc"));
	  
  if(request.getParameterValues("consigneeZipCode")!=null )
	    masterDOB.setConsigneeZipCode(request.getParameterValues("consigneeZipCode"));
	  
  if(request.getParameterValues("destPort")!=null )
	    masterDOB.setDestPort(request.getParameterValues("destPort"));
  
  if(request.getParameterValues("spotRateFlag")!= null )
	    masterDOB.setSpotRatesFlag(request.getParameterValues("spotRateFlag"));
  
  if(request.getParameterValues("spotRateSurchargeCount1")!= null)
	  masterDOB.setSpotrateSurchargeCount(request.getParameterValues("spotRateSurchargeCount1"));
  }
  
  
  /*if(request.getParameterValues("shipperMode")!=null )
	    masterDOB.setShipperMode(request.getParameter("shipperMode"));
  
  if("1".equalsIgnoreCase(masterDOB.getShipperMode()) || "".equalsIgnoreCase(masterDOB.getShipperMode()) || masterDOB.getShipperMode()==null)
	   masterDOB.setShipperConsoleType(null);
  else{
 if(request.getParameter("shipperConsoleType")!=null )
   masterDOB.setShipperConsoleType(request.getParameter("shipperConsoleType"));
  }
       
 if(request.getParameter("consigneeMode")!=null )
   masterDOB.setConsigneeMode(request.getParameter("consigneeMode"));
    
 if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()) || "".equalsIgnoreCase(masterDOB.getConsigneeMode()) || masterDOB.getConsigneeMode()==null  )
	  masterDOB.setConsigneeConsoleType(null);
 else
 {
 if(request.getParameter("consigneeConsoleType")!=null )
   masterDOB.setConsigneeConsoleType(request.getParameter("consigneeConsoleType"));*/
  
  //Govind ends here
  
  if(request.getParameter("overLengthCargoNotes")!=null  && request.getParameter("overLengthCargoNotes").trim().length()!=0)
    masterDOB.setOverLengthCargoNotes(request.getParameter("overLengthCargoNotes"));
  
  if(request.getParameter("routeId")!=null && request.getParameter("routeId").trim().length()!=0)
    masterDOB.setRouteId(request.getParameter("routeId"));
  
  
//@@ Added & Commented by subrahmanyam for the pickupCharges Missing issues  
   /*
         if(request.getParameter("shipperConsoleType")!=null && request.getParameter("shipperConsoleType").trim().length()!=0 )
    masterDOB.setShipperConsoleType(request.getParameter("shipperConsoleType"));

    */
   
  
//@@ Ended by subrahmanyam for the pickupCharges Missing issues            
  if("Add".equals(operation))
      masterDOB.setModifiedDate(new java.sql.Timestamp((new java.util.Date()).getTime()));
    
  int legSize = 0;
  //@@Since a route-plan is specific to a Quote, the route details will not be copied from the previous Quote(For Operation Copy).  
  if("Add".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
  {
    /*legDetails  = new ArrayList();
    legSize     = 1;*/
	  legDetails  = new ArrayList();
	  legSize     = masterDOB.getOriginLocation().length; 
  }
  else
  {
    legDetails  = finalDOB.getLegDetails();
    legSize     = legDetails.size();
  }
  
  Hashtable spotRateDetails  = null;
  ArrayList weightBreakSlabs = null;
  double[] rateDetail = null;
  orgLoc    = masterDOB.getOriginLocation();
  orgPort   = masterDOB.getOriginPort();
  dstLoc    = masterDOB.getDestLocation();
  dstPort   = masterDOB.getDestPort();

  
  for(int j=0;j<legSize;j++)
  {
		  
	 if("Charges".equalsIgnoreCase(masterDOB.getQuoteWith()) || ((orgLoc[j]!= null && !"".equals(orgLoc[j]) )&& (orgPort[j]!= null && !"".equals(orgPort[j]) ))||
		((dstLoc[j]!= null && !"".equals(dstLoc[j]) )&& (dstPort[j]!= null && !"".equals(dstPort[j]) ))	 ) {
	  if("Add".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
    {
      legDOB  = new MultiQuoteFreightLegSellRates();
      
      //@@The freight Rates are always to be fetched between Ports only.
      if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
      {
      legDOB.setOrigin(masterDOB.getOriginPort()[j]);
      legDOB.setDestination(masterDOB.getDestPort()[j]);
      legDOB.setMultiQuote_SerialNo(j);//Added by govind for getting the leane datails order as in the Quote first screen for Quote Third screen
      legDOB.setSpotrateSurchargeCount(Integer.parseInt((masterDOB.getSpotrateSurchargeCount()[j]!= null && !"".equals(masterDOB.getSpotrateSurchargeCount()[j]))?masterDOB.getSpotrateSurchargeCount()[j]:"0"));
      home1        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
      remote1      = home1.create();
      //Changed By Kishore Podili on 06-Apr-11 For Sea Quote 3rd Page Origin and Destination Charges 'null' Issue 
      //legDOB	=	remote1.getLocFullNames(legDOB,masterDOB.getOriginPort()[j],masterDOB.getDestPort()[j]);
      legDOB	=	remote1.getLocFullNames(legDOB,masterDOB.getOriginLocation()[j],masterDOB.getDestLocation()[j],masterDOB.getOriginPort()[j],masterDOB.getDestPort()[j]);
      }
      else
      {
    	  legDOB.setOrigin(masterDOB.getOriginLocation()[j]);
          legDOB.setDestination(masterDOB.getDestLocation()[j]);
          home1        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
          remote1      = home1.create();
          //Changed By Kishore Podili on 06-Apr-11 For Sea Quote 3rd Page Origin and Destination Charges 'null' Issue
          // legDOB	=	remote1.getLocFullNames(legDOB,masterDOB.getOriginPort()[j],masterDOB.getDestPort()[j]);
          legDOB	=	remote1.getLocFullNames(legDOB,masterDOB.getOriginLocation()[j],masterDOB.getDestLocation()[j],masterDOB.getOriginPort()[j],masterDOB.getDestPort()[j]);
          
      }
    
      if(masterDOB.getServiceLevelId()!=null)
        legDOB.setServiceLevel(masterDOB.getServiceLevelId());
      legDOB.setShipmentMode(masterDOB.getShipmentMode());
      if("Copy".equalsIgnoreCase(operation) && "Both".equalsIgnoreCase(masterDOB.getQuoteWith())){//Added by govind for appcharge groups not coming in quote second screen while quote copy
    	  if(j< finalDOB.getLegDetails().size())
    	  chargeid = (MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(j);
    	  else
    	chargeid = null;  
    	  if(chargeid != null )
    	  legDOB.setChargeGroupIds(chargeid.getChargeGroupIds());
    	  else
    		  legDOB.setChargeGroupIds(null);		  
      }//end if added by govind
      legDetails.add(j,legDOB);
    }
    else
    { 
    	    	
      legDOB  = (MultiQuoteFreightLegSellRates)legDetails.get(j);
      legDOB.setMultiQuote_SerialNo(j);//Added by govind for getting the leane datails order as in the Quote first screen for Quote Third screen
     }
    
  
   
      //Modified by Anil.k for Spot Rates
	  if(request.getParameterValues("spotRateFlag")!= null)
      legDOB.setSpotRatesFlag("Y".equalsIgnoreCase(request.getParameterValues("spotRateFlag")[j])?true:false);
   
    legDetails.remove(j);
    legDetails.add(j,legDOB);
	 }
  }
  
/*  if(request.getParameterValues("chargeGroupId")!=null && request.getParameterValues("chargeGroupId").length!=0)
      masterDOB.setChargeGroupIds(request.getParameterValues("chargeGroupId"));*/
    
  if(request.getParameterValues("headerFooter")!=null && request.getParameterValues("headerFooter").length!=0)
    masterDOB.setHeaderFooter(request.getParameterValues("headerFooter"));
  
  if(request.getParameterValues("content")!=null && request.getParameterValues("content").length!=0)
    masterDOB.setContentOnQuote(request.getParameterValues("content"));
  
  if(request.getParameterValues("level")!=null && request.getParameterValues("level").length!=0)
    masterDOB.setLevels(request.getParameterValues("level"));
  
  if(request.getParameterValues("align")!=null && request.getParameterValues("align").length!=0)
    masterDOB.setAlign(request.getParameterValues("align"));
    
  if(request.getParameter("addressId")!=null && request.getParameter("addressId").length()!=0)
    masterDOB.setCustomerAddressId(Integer.parseInt(request.getParameter("addressId")));
    
  if(request.getParameter("address")!=null && request.getParameter("address").length()!=0)
    masterDOB.setCustomerAddress(request.getParameter("address"));
   
  if((masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZones()==null) || (masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZones()==null))
  {
    QMSMultiQuoteSessionHome       home              = null;
    QMSMultiQuoteSession           remote            = null;

    //lookUpBean  = new LookUpBean();
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = home.create();
    masterDOB   = remote.getShipperConsigneeZones(masterDOB);
  }
  masterDOB.setUserId(loginbean.getUserId());
  masterDOB.setEmpId(loginbean.getEmpId());
  
  if("Modify".equalsIgnoreCase(operation))
  {
    masterDOB.setTerminalId(finalDOB.getMasterDOB().getTerminalId());
    masterDOB.setAccessLevel(finalDOB.getMasterDOB().getAccessLevel());
  }
  else
  {
    masterDOB.setTerminalId(loginbean.getTerminalId());
    masterDOB.setAccessLevel(accessLevel);
  }
  

  masterDOB.setTerminalCurrency(loginbean.getCurrencyId());
  masterDOB.setOperation(operation);
  //@@Added by kiran.v on 09/01/2012 for Wpbn Issue
  if(request.getParameter("incompletescreen")!=null&&"true".equalsIgnoreCase(request.getParameter("incompletescreen")))
	    masterDOB.setIncomplete_screen(true);
  finalDOB.setMasterDOB(masterDOB);
  finalDOB.setLegDetails(legDetails);
}
catch(Exception ex)
	{
  ex.printStackTrace();
		//Logger.error(FILE_NAME, " [setMultiQuoteMasterDOB()] -> "+ex.toString());
  logger.error(FILE_NAME+ " [setMultiQuoteMasterDOB()] -> "+ex.toString());
	}
return finalDOB;
}


/**
 * This method helps in processing the request from the sell rates screen
 *
 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
 * 
 * @exception IOException if an input or output error is detected when the servlet handles the doMasterInfo request
 * @exception ServletException if the request for the doMasterInfo could not be handled.
 */
private void doSellRates(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
{
String                    		operation         = null;
String                    		subOperation      = null;
// QMSQuoteSessionHome       home              = null;
// QMSQuoteSession           remote            = null;
ArrayList                 		freightRates      = null;
ArrayList                 		updatedFreightRates = null;
ArrayList                 		attachmentIdList    = null; 
ArrayList   					intNotes		 = new ArrayList();
ArrayList   					extNotes 		 = new ArrayList();
ArrayList 						containerList 	 = new ArrayList();
MultiQuoteFinalDOB             finalDOB          = null;
MultiQuoteMasterDOB            masterDOB         = null;
MultiQuoteTiedCustomerInfo     tiedCustomerInfo  = null;
MultiQuoteFreightLegSellRates  legRateDetails    = null;
MultiQuoteHeader               quoteHeader       = null;
 String                    		weightBreakType  = null;
String                    		legServiceLevel  = null;
StringBuffer              			sb  		 = new StringBuffer("");
boolean[]                  		isMarginDefined   = null;
boolean selectedflag[]     = null;
boolean                     isFinalMarginDefined = true;
boolean                     isFinalSelectedFlag = true;
boolean						tempflag     =false;
String[] selInternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
String[] selExternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
String[] selTempInternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
Object[]  toArrayIntNotes = null;
Object[]  toArrayExtNotes = null;
String selectedWeightBreak = null;//@@ Added by Mohan Gajjala for on 30102010
String selectedConsoleType = null;//@@ Added by Mohan Gajjala for on 30102010
int toArrayCount     = 0;
int selectCntrCount = 0;//@@ Added by Mohan Gajjala for on 30102010
int selIndex = 0;//@@ Added by Mohan Gajjala for on 30102010
int sellRateSize		=	0;//Added by govind for the cr-219979
ArrayList selectedFreightSellRateIndex = null;
int 						ratecount =0;
int                       freightRatesSize =0;
int 	count 				= 0;
int spotrateSurchargeCount  = 0;
HttpSession   session       = request.getSession();
//Added by Anil.k for Spot Rates
Hashtable spotRateDetails   = null;
ArrayList weightBreak      	= new ArrayList();
LinkedHashSet surchgweightBreak  		= new LinkedHashSet();
LinkedHashSet surCurrencyID     		= new LinkedHashSet();
LinkedHashSet surweightBreak  			= new LinkedHashSet();
ArrayList weightBreakSlabs  = null;
String[] surchargeBreaks 	= null; 
String[] surchargeRates  	= null;
String[] listBreaks 	 	= null; 
String[] listRates  		= null;
String[] weightBreaks 		= null; 
String[] rates        		= null;
double[] rateDetail 		= null;
String   currencyId  		= null;
ArrayList checkedFlag 		= null;
ArrayList chargeRateIndicator = new ArrayList();
ArrayList surChargeId   	= new ArrayList();
//Ended by Anil.k for Spot Rates
try
{
  operation   = request.getParameter("Operation");
  subOperation  = request.getParameter("subOperation");

     if("View".equalsIgnoreCase(operation))
    finalDOB  = (MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");
  else
    finalDOB  = (MultiQuoteFinalDOB)session.getAttribute("finalDOB");
  masterDOB = finalDOB.getMasterDOB();
  System.out.println("For-Load test -QMSMUltiQuoteController-1430---"+masterDOB);
  tiedCustomerInfo  = finalDOB.getTiedCustomerInfo();
  
  freightRates  = finalDOB.getLegDetails();
   
  // // added by VLAKSHMI for issue 146968 on 5/12/2008
String[]  tempContainerTypes=null;
String[]	containerTypes			    =	null;
  if(freightRates!=null)
  {

    freightRatesSize  = freightRates.size();
    selectedflag = new boolean[freightRatesSize];
    isMarginDefined = new boolean[freightRatesSize];
    for(int i=0;i<freightRatesSize;i++)
    {
    	  tempflag = false;
    	selectedFreightSellRateIndex	=	new ArrayList();
      legRateDetails  = (MultiQuoteFreightLegSellRates)freightRates.get(i);
      if(masterDOB.getSpotRatesFlag()!=null && "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i])){//Added by Anil.k for Spot Rates
      sellRateSize    = legRateDetails.getRates().size();
      for(int rt=0;rt<sellRateSize;rt++){
      
    	if(request.getParameter("hid"+i+rt)!=null && request.getParameter("hid"+i+rt).trim().length()!=0)
     {
         tempContainerTypes			=	request.getParameterValues("con"+i+Integer.parseInt(request.getParameter("hid"+i+rt)));
      
    if(tempContainerTypes!=null)
    {

          containerTypes			    =	new String[tempContainerTypes.length];
          int tempContLen	=	tempContainerTypes.length;
         for(int t=0;t<tempContLen;t++)
         {
           if(tempContainerTypes[t]!=null && tempContainerTypes[t].trim().length()>0 )
           {
           //logger.info("ContainerTypes"+tempContainerTypes[t].trim().length());
          
           containerTypes[t]=tempContainerTypes[t];
           selectCntrCount=selectCntrCount+1;    //Added by Mohan Gajjala for on 30102010
          }
         }
    }// end  for issue 146968 on 5/12/2008
          }	  
       	  
    	  
    	  
      if(request.getParameterValues("chargeGroupIds"+i)!= null && request.getParameterValues("chargeGroupIds"+i).length >0){
  		legRateDetails.setChargeGroupIds(request.getParameterValues("chargeGroupIds"+i));
  	}
      
      if(request.getParameter("hid"+i+rt)!=null && request.getParameter("hid"+i+rt).trim().length()!=0)
      {
    	  
        //Added by Mohan Gajjala for on 30102010 
    	tempflag = true;
    	  selIndex =Integer.parseInt(request.getParameter("hid"+i+rt));
    	selectedFreightSellRateIndex.add(selIndex);
//    	ratecount++;
        legRateDetails.setContainerTypes(containerTypes);// // added by VLAKSHMI for issue 146968 on 5/12/2008
         selectedWeightBreak = ((MultiQuoteFreightRSRCSRDOB)legRateDetails.getRates().get(selIndex)).getWeightBreakType();
        selectedConsoleType = ((MultiQuoteFreightRSRCSRDOB)legRateDetails.getRates().get(selIndex)).getConsoleType();
       
        int contrCount = 0;
        if(containerTypes!=null)
        	contrCount = containerTypes.length;
        //Modified by mohan for Issue no .223726 UTS 
        //Comment and modified by Rakesh for Issue:       on 18-03-2011
        //if(request.getParameter("notes"+selIndex)!=null )
        if(request.getParameter("note"+i+rt)!=null )
        {
         	System.out.println("INT Weight Break Selected --->"+selectedWeightBreak);
        	System.out.println("INT Console Type Selected --->"+selectedConsoleType);
        	
        	/*if(selectedWeightBreak!=null && "List".equalsIgnoreCase(selectedWeightBreak) && selectedConsoleType!=null && "FCL".equals(selectedConsoleType))
        	{
        		selTempInternalNotes = request.getParameterValues("notes"+selIndex);
        		
        		selInternalNotes = new String[selectCntrCount];
        		for(int seledIndex=0;seledIndex<contrCount;seledIndex++)
        		{
        			if(containerTypes[seledIndex]!=null && !"".equals(containerTypes[seledIndex]))
            			selInternalNotes[seledIndex] = selTempInternalNotes[seledIndex];
        		}
        	}else*/
            //Comment and modified by Rakesh for Issue:       on 18-03-2011
        	//intNotes.add((String)request.getParameter("notes"+selIndex));
        	intNotes.add((String)request.getParameter("note"+i+rt));
        }
        //Comment and modified by Rakesh for Issue:       on 18-03-2011
        //if(request.getParameter("extNotes"+selIndex)!=null )
        if(request.getParameter("extNote"+i+rt)!=null )
        {
        	System.out.println("EXT Weight Break Selected --->"+selectedWeightBreak);
        	System.out.println("EXt Console Type Selected --->"+selectedConsoleType);
         	
        	/*if(selectedWeightBreak!=null && "List".equalsIgnoreCase(selectedWeightBreak) && selectedConsoleType!=null && "FCL".equals(selectedConsoleType))
        	{
        		selTempInternalNotes = request.getParameterValues("extNotes"+selIndex);
        		//int selIndexCount = containerTypes.length;
        		selExternalNotes = new String[selectCntrCount];
        		for(int seledIndex=0;seledIndex<contrCount;seledIndex++)
        		{
        			if(containerTypes[seledIndex]!=null && !"".equals(containerTypes[seledIndex]))
        				selExternalNotes[seledIndex] = selTempInternalNotes[seledIndex];
        		}
        	}else*/
            //Comment and modified by Rakesh for Issue:       on 18-03-2011
            	extNotes.add((String)request.getParameter("extNote"+i+rt));//End by mohan for Issue no .223726 UTS
        		
        	
        }//End by Mohan Gajjala for on 30102010
      }
     
    }
      if(!tempflag)
    	  selectedflag[i] = true;  
      int indexSize	=	selectedFreightSellRateIndex.size();
      Integer[] indexArray	= new Integer[indexSize];
      for(int j=0;j<indexSize;j++)
    	  indexArray[j] = (Integer)selectedFreightSellRateIndex.get(j);
      legRateDetails.setSelectedFreightSellRateIndex(indexArray);
      freightRates.remove(i);
      freightRates.add(i,legRateDetails);
    }
    else{//Added by Anil.k for Spot Rates
    	
            spotRateDetails  = new Hashtable();
            weightBreakSlabs = new ArrayList();
            ArrayList rateDescription = new ArrayList();
            chargeRateIndicator = new ArrayList();
            surChargeId		 = new ArrayList();
            surchargeBreaks = null; 
            surchargeRates  = null;      
            checkedFlag = null;            
            listBreaks = null; 
            listRates  = null;
            legRateDetails.setSpotRatesFlag(true);
            legRateDetails.setSpotRatesType(request.getParameter("spotRateType"+i).toUpperCase());            
            spotrateSurchargeCount = legRateDetails.getSpotrateSurchargeCount();
            
            if("Flat".equalsIgnoreCase(request.getParameter("spotRateType"+i)))
            {
              weightBreaks = request.getParameterValues("flatWeightBreak"+i);
              rates = request.getParameterValues("flatRate"+i);
            }
            else if("Slab".equalsIgnoreCase(request.getParameter("spotRateType"+i)))
            {
              weightBreaks = request.getParameterValues("slabWeightBreak"+i);
              rates = request.getParameterValues("slabRate"+i);
            }
            else if("List".equalsIgnoreCase(request.getParameter("spotRateType"+i)))
            {
              weightBreaks = request.getParameterValues("listWeightBreak"+i);
              rates = request.getParameterValues("listRate"+i);
            }   
            
            if("List".equalsIgnoreCase(request.getParameter("spotRateType"+i)))
            {
            	int rTLen	=	rates.length;
            	for(int j=0;j<rTLen;j++)
            	{
                  if(weightBreaks[j]!=null && rates[j]!=null && weightBreaks[j].trim().length()!=0 && rates[j].trim().length()!=0)
                  {
                    rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    rateDetail[0] = 0;//upper bound
                    rateDetail[1] = 0;//lower bound
                    rateDetail[2] = Double.parseDouble(rates[j]);//rate
                    spotRateDetails.put(weightBreaks[j],rateDetail);
                    weightBreakSlabs.add(weightBreaks[j]);
                    rateDescription.add("A FREIGHT RATE");
                    chargeRateIndicator.add("");
                    surChargeId.add("");
                    if(currencyId==null)
                    	currencyId = request.getParameter("currencyId"+i)!=null && request.getParameter("currencyId"+i).trim().length()!=0?request.getParameter("currencyId"+i):"-";
                    else
                    	currencyId = currencyId+","+(request.getParameter("currencyId"+i)!=null && request.getParameter("currencyId"+i).trim().length()!=0?request.getParameter("currencyId"+i):"-");
                    
                  }                  
            	}
            }
           else
            {
        	   int wtBreakLen	=	weightBreaks.length;
                for(int j=0;j<wtBreakLen;j++)
                {
                  if(weightBreaks[j]!=null && rates[j]!=null && weightBreaks[j].trim().length()!=0 && rates[j].trim().length()!=0)
                  {
                     rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    if("BASIC".equalsIgnoreCase(weightBreaks[j]) || "MIN".equalsIgnoreCase(weightBreaks[j]) ||  "FLAT".equalsIgnoreCase(weightBreaks[j]))
                    //@@Modified by Kameswari for Surcharge Enhancement
                    {
                       rateDetail[0] = 0;//upper bound
                       rateDetail[1] = 0;//lower bound
                       rateDetail[2] = Double.parseDouble(rates[j]);//rate 
                    }
                    else
                    {
                      if(j!=(weightBreaks.length-1) && weightBreaks[j+1]!=null && weightBreaks[j+1].trim().length()!=0)
                        rateDetail[0] = Double.parseDouble(weightBreaks[j+1]);//upper bound
                      else
                        rateDetail[0] = 100000;//since this is the last weight break, upper bound is set to 1,00,000
                      
                      if(Double.parseDouble(weightBreaks[j])<0)
                        rateDetail[1] = 0;//since the weightBreaks[i] is -ve lower bound is set as zero
                      else
                        rateDetail[1] = Double.parseDouble(weightBreaks[j]);//lower bound
                      
                       rateDetail[2] = Double.parseDouble(rates[j]);//rate
                     }
                     spotRateDetails.put(weightBreaks[j],rateDetail);
                     weightBreakSlabs.add(weightBreaks[j]);
                     rateDescription.add("A FREIGHT RATE");
                     chargeRateIndicator.add("");
                     surChargeId.add("");
                     if(currencyId==null)
                     	currencyId = request.getParameter("currencyId"+i)!=null && request.getParameter("currencyId"+i).trim().length()!=0?request.getParameter("currencyId"+i):"-";
                     else
                     	currencyId = currencyId+","+(request.getParameter("currencyId"+i)!=null && request.getParameter("currencyId"+i).trim().length()!=0?request.getParameter("currencyId"+i):"-");
                     
                  }                    
                }
            }
            for(int sur=0;sur<spotrateSurchargeCount;sur++){
            if(request.getParameter("surchargeWeightBreaks"+sur+i)!=null && request.getParameter("surchargeWeightBreaks"+sur+i).trim().length()!=0)
            {
            	if("SLAB".equalsIgnoreCase(request.getParameter("surchargeWeightBreaks"+sur+i)))
            	{
            		int slabLength = request.getParameterValues("srslabvalues"+sur+i).length;
            		weightBreaks = request.getParameterValues("srslabbreaks"+sur+i);
            		rates = request.getParameterValues("srslabvalues"+sur+i);
            		for(int sl=0;sl<slabLength;sl++)
            		{
            			if(weightBreaks[sl]!=null && rates[sl]!=null && weightBreaks[sl].trim().length()!=0 && rates[sl].trim().length()!=0)
            			{
            				rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
            				if(weightBreaks[sl].contains("MIN"))
            				{
            					rateDetail[0] = 0;//upper bound
                                rateDetail[1] = 0;//lower bound
                                rateDetail[2] = Double.parseDouble(rates[sl]);//rate 
                                spotRateDetails.put(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sl],rateDetail);
                                weightBreakSlabs.add(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sl]);
                                surchgweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+weightBreaks[sl]);//Added by govind for displaying of spotrate surcharges in back button
                                surweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("surchargeWeightBreaks"+sur+i));
                                
            				}
            				else
            				{
            					if(sl!=(weightBreaks.length-1) && weightBreaks[sl+1]!=null && weightBreaks[sl+1].trim().length()!=0)
            						rateDetail[0] = Double.parseDouble(weightBreaks[sl+1]);//upper bound
            					else
            						rateDetail[0] = 100000;//since this is the last weight break, upper bound is set to 1,00,000
                              
            					if(Double.parseDouble(weightBreaks[sl])<0)
            						rateDetail[1] = 0;//since the weightBreaks[i] is -ve lower bound is set as zero
            					else
            						rateDetail[1] = Double.parseDouble(weightBreaks[sl]);//lower bound
            					rateDetail[2] = Double.parseDouble(rates[sl]);
            					spotRateDetails.put(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sl],rateDetail);
            					weightBreakSlabs.add(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sl]);
            					surchgweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+weightBreaks[sl]);//Added by govind for displaying of spotrate surcharges in back button
            					surweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("surchargeWeightBreaks"+sur+i));
            				}   
            				rateDescription.add(toTitleCase(request.getParameter("surchargeDesc"+sur+i)).trim()+"-"+(request.getParameter("surchargeIds"+sur+i).substring(request.getParameter("surchargeIds"+sur+i).length()-2)).toUpperCase());
            				chargeRateIndicator.add("");
            				surChargeId.add(request.getParameter("surchargeIds"+sur+i));
            				if(currencyId==null)
                            	currencyId = request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-";
                            else
                            	currencyId = currencyId+","+(request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-");
                            surCurrencyID.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("currencyId"+sur+i));//Added by Govind for SpotRates back Button
            			
            			}            			
            		}
            	}
            	else if("Both".equalsIgnoreCase(request.getParameter("surchargeWeightBreaks"+sur+i)))
            	{
            		int bothLength = request.getParameterValues("srbothbreaks"+sur+i).length;
            		weightBreaks = request.getParameterValues("srbothbreaks"+sur+i);
            		rates = request.getParameterValues("srslabvalue"+sur+i);            		
            		String[] flatValue = request.getParameterValues("srflatvalue"+sur+i);
            		for(int sb1=0;sb1<bothLength;sb1++)
            		{
            			if(weightBreaks[sb1]!=null && (rates[sb1]!=null || flatValue[sb1]!=null) && weightBreaks[sb1].trim().length()!=0 && (flatValue[sb1].trim().length()!=0 || rates[sb1].trim().length()!=0))
            			{
            				rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
            				if(weightBreaks[sb1].contains("MIN"))
            				{
            					rateDetail[0] = 0;//upper bound
                                rateDetail[1] = 0;//lower bound
                                rateDetail[2] = Double.parseDouble((rates[sb1]!=null && !"".equalsIgnoreCase(rates[sb1]))?rates[sb1]:flatValue[sb1]); //Added by Govind 
                                spotRateDetails.put(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sb1],rateDetail);
                                weightBreakSlabs.add(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sb1]);
                                surchgweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+weightBreaks[sb1]);//Added by govind for displaying of spotrate surcharges in back button
                                surweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("surchargeWeightBreaks"+sur+i));
                                chargeRateIndicator.add(rates[sb1]!=null && rates[sb1].trim().length()!=0?"slab":"flat");
            				}
            				else
            				{
            					if(sb1!=(weightBreaks.length-1) && weightBreaks[sb1+1]!=null && weightBreaks[sb1+1].trim().length()!=0)
            						rateDetail[0] = Double.parseDouble(weightBreaks[sb1+1]);//upper bound
            					else
            						rateDetail[0] = 100000;//since this is the last weight break, upper bound is set to 1,00,000
                              
            					if(Double.parseDouble(weightBreaks[sb1])<0)
            						rateDetail[1] = 0;//since the wsb1ghtBreaks[i] is -ve lower bound is set as zero
            					else
            						rateDetail[1] = Double.parseDouble(weightBreaks[sb1]);//lower bound
                            
            					rateDetail[2] = Double.parseDouble((rates[sb1]!=null && !"".equalsIgnoreCase(rates[sb1]))?rates[sb1]:flatValue[sb1]);
            					spotRateDetails.put(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sb1],rateDetail);
            					weightBreakSlabs.add(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sb1]);
            					surchgweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+weightBreaks[sb1]);//Added by govind for displaying of spotrate surcharges in back button
            					surweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("surchargeWeightBreaks"+sur+i));
            					
            					chargeRateIndicator.add(rates[sb1]!=null && rates[sb1].trim().length()!=0?"slab":"flat");
            				}
            				rateDescription.add(request.getParameter("surchargeDesc"+sur+i).trim()+"-"+(request.getParameter("surchargeIds"+sur+i).substring(request.getParameter("surchargeIds"+sur+i).length()-2)).toUpperCase());            				
            				surChargeId.add(request.getParameter("surchargeIds"+sur+i));
            				if(currencyId==null)
                            	currencyId = request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-";
                            else
                            	currencyId = currencyId+","+(request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-");
            				 surCurrencyID.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("currencyId"+sur+i));//Added by Govind for SpotRates back Button
            			}
            		}
            	}
            	else if("List".equalsIgnoreCase(request.getParameter("surchargeWeightBreaks"+sur+i)))
            	{
            		int bothLength = request.getParameterValues("srList"+sur+i).length;
            		weightBreaks = request.getParameterValues("srList"+sur+i);
            		rates = request.getParameterValues("srListValue"+sur+i);  
            		for(int sb1=0;sb1<bothLength;sb1++)
            		{
            			
            			if(weightBreaks[sb1]!=null && rates[sb1]!=null  && weightBreaks[sb1].trim().length()!=0 &&  rates[sb1].trim().length()!=0)
            			{ 
            				rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
            				rateDetail[0] = 0;//upper bound
                            rateDetail[1] = 0;//lower bound
                            rateDetail[2] = Double.parseDouble(rates[sb1]);//rate  
            				spotRateDetails.put(weightBreaks[sb1]+request.getParameter("surchargeIds"+sur+i),rateDetail);
            				weightBreakSlabs.add(weightBreaks[sb1]+request.getParameter("surchargeIds"+sur+i));
            				surchgweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+weightBreaks[sb1]);//Added by govind for displaying of spotrate surcharges in back button
            				surweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("surchargeWeightBreaks"+sur+i));
            				rateDescription.add(request.getParameter("surchargeDesc"+sur+i).trim()+"-"+(request.getParameter("surchargeIds"+sur+i).substring(request.getParameter("surchargeIds"+sur+i).length()-2)).toUpperCase());
            				chargeRateIndicator.add("");
            				surChargeId.add(request.getParameter("surchargeIds"+sur+i));
            				if(currencyId==null)
                            	currencyId = request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-";
                            else
                            	currencyId = currencyId+","+(request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-");
            				 surCurrencyID.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("currencyId"+sur+i));//Added by Govind for SpotRates back Button
            			}
            		}
            	}
            	else
            	{
            		int srlength = request.getParameterValues("srelse"+sur+i).length;
            		weightBreaks = request.getParameterValues("srelse"+sur+i);
            		rates = request.getParameterValues("srelseValue"+sur+i);
            		for(int sb1=0;sb1<srlength;sb1++)
            		{
            			if(weightBreaks[sb1]!=null && rates[sb1]!=null && weightBreaks[sb1].trim().length()!=0 && rates[sb1].trim().length()!=0)
            			{
            				rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
            				rateDetail[0] = 0;//upper bound
                            rateDetail[1] = 0;//lower bound
            				rateDetail[2] = Double.parseDouble(rates[sb1]);
            				spotRateDetails.put(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sb1],rateDetail);
            				weightBreakSlabs.add(request.getParameter("surchargeIds"+sur+i)+weightBreaks[sb1]);  
            				surchgweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+weightBreaks[sb1]);//Added by govind for displaying of spotrate surcharges in back button
            				surweightBreak.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("surchargeWeightBreaks"+sur+i));
            				rateDescription.add(request.getParameter("surchargeDesc"+sur+i).trim()+"-"+(request.getParameter("surchargeIds"+sur+i).substring(request.getParameter("surchargeIds"+sur+i).length()-2)));
            				chargeRateIndicator.add("");
            				surChargeId.add(request.getParameter("surchargeIds"+sur+i));
            				if(currencyId==null)
                            	currencyId = request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-";
                            else
                            	currencyId = currencyId+","+(request.getParameter("currencyId"+sur+i)!=null && request.getParameter("currencyId"+sur+i).trim().length()!=0?request.getParameter("currencyId"+sur+i):"-");
            				 surCurrencyID.add(request.getParameter("surchargeIds"+sur+i)+","+request.getParameter("currencyId"+sur+i));//Added by Govind for SpotRates back Button
            			}
            		}
            	}
            }         
    }
            
            legRateDetails.setSpotRateDescription(rateDescription);
            
            if(request.getParameterValues("checkedFlag"+i)!=null)
            {
            	checkedFlag = new ArrayList();
            	for(int c=0;c<request.getParameterValues("checkedFlag"+i).length;c++)
            		{
            			checkedFlag.add(request.getParameterValues("checkedFlag"+i)[c]) ;
            		}
            }
            legRateDetails.setCheckedFlag(checkedFlag);
            if(chargeRateIndicator!=null)
            	legRateDetails.setChargeRateIndicator(chargeRateIndicator);
            
            if(surChargeId!=null)
            	legRateDetails.setSurchargeId(surChargeId);
             
            if(request.getParameter("serviceLevel"+i)!=null && request.getParameter("serviceLevel"+i).trim().length()!=0)
            	legRateDetails.setServiceLevel(request.getParameter("serviceLevel"+i));
              
             if(request.getParameter("uom"+i)!=null && request.getParameter("uom"+i).trim().length()!=0)
            	 legRateDetails.setUom(request.getParameter("uom"+i));
            
            if(request.getParameter("densityRatio"+i)!=null && request.getParameter("densityRatio"+i).trim().length()!=0)
            	legRateDetails.setDensityRatio(request.getParameter("densityRatio"+i));
            
           // if(currencyId!=null && currencyId.trim().length()!=0)
            //	legRateDetails.setCurrency(currencyId);
            //@@Added by kiran.v on 01/08/2011 for Wpbn Issue 257513
            if(request.getParameter("currencyId"+i)!=null)
            legRateDetails.setCurrency(request.getParameter("currencyId"+i));
            
            if(surCurrencyID != null )//Added by Govind for the back Button in Spot rate suircharges surcharge
            	legRateDetails.setSurCurrency(surCurrencyID);
            
            if(request.getParameter("frequency"+i)!=null && request.getParameter("frequency"+i).trim().length()!=0)
            	legRateDetails.setFrequency(request.getParameter("frequency"+i));
            
            if(request.getParameter("transitTime"+i)!=null && request.getParameter("transitTime"+i).trim().length()!=0)
            	legRateDetails.setTransitTime(request.getParameter("transitTime"+i));
            
            if(request.getParameter("carrierId"+i)!=null && request.getParameter("carrierId"+i).trim().length()!=0)
            	legRateDetails.setCarrier(request.getParameter("carrierId"+i));
                
            if(spotRateDetails!=null)//only if some spot rates are there assign it to the Master DOB
            	legRateDetails.setSpotRateDetails(spotRateDetails);
              
            if(weightBreakSlabs!=null)
            {
            	legRateDetails.setWeightBreaks(weightBreakSlabs);
            }
            if(surweightBreak != null)//Added by govind for backbutton in Spotrates
            {
            	legRateDetails.setWeightBreak(surweightBreak);
            }
            /*if(surchgweightBreak != null)//Added by govind for backbutton in Spotrates
            {
            	legRateDetails.setWeightBreak(surchgweightBreak);
            }*/
            if(request.getParameterValues("chargeGroupIds"+i)!= null && request.getParameterValues("chargeGroupIds"+i).length >0){
          		legRateDetails.setChargeGroupIds(request.getParameterValues("chargeGroupIds"+i));
          	}
            freightRates.remove(i);
            freightRates.add(i,legRateDetails);  
    }//Ended by Anil.k for Spot Rates
    }
    finalDOB.setLegDetails(freightRates);
    
  }
  toArrayIntNotes = intNotes.toArray();
  toArrayExtNotes = extNotes.toArray();
  toArrayCount = toArrayIntNotes.length;
  selInternalNotes = new String[toArrayCount];
  selExternalNotes = new String[toArrayCount];
  for(int note=0; note<toArrayCount;note++)
  {
	  selInternalNotes[note] = (String)toArrayIntNotes[note];
	  selExternalNotes[note] = (String)toArrayExtNotes[note];
  }
  if("Next >>".equalsIgnoreCase(request.getParameter("submit")))
  {
    long start = System.currentTimeMillis();
   // finalDOB.setSelectedOriginChargesListIndices(null);
   // finalDOB.setSelctedDestChargesListIndices(null);
    finalDOB          =   getMarginLimit(finalDOB);
    // Added by Mohan Gajjala for on 30102010
    if(!"Copy".equalsIgnoreCase(masterDOB.getOperation()))//Added by govind for getting the internal and external notes of quote when copy 
    {
    finalDOB.setInternalNotes(selInternalNotes);
    finalDOB.setExternalNotes(selExternalNotes);
    }
    request.setAttribute("finalDOB",finalDOB);
    freightRates      =   finalDOB.getLegDetails();
    freightRatesSize  =   freightRates.size();
    
    for(int i=0;i<freightRatesSize;i++)
    {
    	if(masterDOB.getSpotRatesFlag()!=null && "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i])){
      legRateDetails  =   (MultiQuoteFreightLegSellRates)freightRates.get(i);
     
      if(selectedflag[i]){
    	  sb.append(" No Rates Are selected for the Lane "+legRateDetails.getOrigin()).append("-").append(legRateDetails.getDestination()+"\n");
    	  isFinalSelectedFlag =false;
      }
      if(!selectedflag[i] && !legRateDetails.isMarginFlag() && !legRateDetails.isTiedCustInfoFlag())
      {
          sb.append("&nbsp;The Margin Limit is Not Defined For the Selected Shipment Mode & Service Level for the Leg(s) ").append(legRateDetails.getOrigin()).append("-").append(legRateDetails.getDestination());
          isMarginDefined[i] = false;
          isFinalMarginDefined = false;
      }
    }
    }
    
    if( isFinalSelectedFlag && (isFinalMarginDefined || "View".equalsIgnoreCase(operation)))
    {
      finalDOB  = doGetHeaderAndCharges(request,response);
  
      //session.setAttribute("finalDOB",finalDOB);
      if("View".equalsIgnoreCase(operation))
      {
        session.setAttribute("viewFinalDOB",finalDOB);
        request.setAttribute("fromWhere","qms/QMSMultiQuoteFreightSellRatesView.jsp");
        doDispatcher(request, response, "qms/QMSMultiQuoteChargesView.jsp");
      }
      else
      {
        session.setAttribute("finalDOB",finalDOB);
        request.setAttribute("fromWhere","qms/QMSMultiQuoteFreightSellRates.jsp");
  
        //doDispatcher(request, response, "qms/QMSQuoteChargesSelect.jsp?count="+count);
        logger.info("Server Response Time for 3rd screen in milli seconds :      "+ ((System.currentTimeMillis()) - start) + "     User Id :: "+ finalDOB.getMasterDOB().getUserId()+ " Customer Id :: "+finalDOB.getMasterDOB().getCustomerId());
         doDispatcher(request, response, "qms/QMSMultiQuoteChargesSelect.jsp?count="+count);
      }
    }
    else
    {
      request.setAttribute("errors",": "+sb.toString());
      session.setAttribute("finalDOB",finalDOB);
      doDispatcher(request, response, "qms/QMSMultiQuoteFreightSellRates.jsp");
    }
  }
  else if("<< Back".equalsIgnoreCase(request.getParameter("submit")))
  {
    /*if(tiedCustomerInfo!=null)//the previous page is tied customer info page
    {
      finalDOB      =   (MultiQuoteFinalDOB)session.getAttribute("finalDOB");
      request.setAttribute("finalDOB",finalDOB);
      session.setAttribute("finalDOB",finalDOB);
      doDispatcher(request, response, "qms/QMSQuoteTiedCustomerInfo.jsp");
    }
    else//the previous page is master page
    {*/
      if("View".equalsIgnoreCase(operation))
          finalDOB      =   (MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");
      else
          finalDOB      =   (MultiQuoteFinalDOB)session.getAttribute("finalDOB");
          
      request.setAttribute("finalDOB",finalDOB);
      request.setAttribute("fromBack","BackButton");
  
      //session.setAttribute("finalDOB",finalDOB);
      if("View".equalsIgnoreCase(operation))
      {
        session.setAttribute("viewFinalDOB",finalDOB);
        doDispatcher(request, response, "qms/QMSMultiQuoteMasterView.jsp");
      }
      else
      {
        session.setAttribute("finalDOB",finalDOB);
        session.setAttribute("fromBack","BackButton");   
        doDispatcher(request, response, "qms/QMSMultiQuoteMaster.jsp");
      }
   // }
  }
}
catch(Exception ex)
	{
  session.removeAttribute("finalDOB");
  session.removeAttribute("PreFlagsDOB");
  ex.printStackTrace();
		//Logger.error(FILE_NAME, " [error in doSellRates()] -> "+ex.toString());
  logger.error(FILE_NAME+ " [error in doSellRates()] -> "+ex.toString());
  setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSMultiQuoteController?Operation=Add");
  doDispatcher(request, response, "ESupplyErrorPage.jsp");
	}

}

private MultiQuoteFinalDOB getMarginLimit(MultiQuoteFinalDOB finalDOB) throws ServletException
{
  QMSMultiQuoteSessionHome       home              = null;
  QMSMultiQuoteSession           remote            = null;
  try
  {
    //lookUpBean  = new LookUpBean();
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = home.create();
    finalDOB    = remote.getMarginLimit(finalDOB);
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in Controller:"+e);
    logger.error(FILE_NAME+"Error in Controller:"+e);
  }
  return finalDOB;
}


/**
	 * This method helps in getting the charges and their description and the necessary information i.e the header to be displayed on the charges select screen
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doMasterInfo request
	 * @exception ServletException if the request for the doMasterInfo could not be handled.
	 */
private MultiQuoteFinalDOB doGetHeaderAndCharges(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
{
  String                  operation   = null;
  MultiQuoteHeader             quoteHeader = null;
  MultiQuoteFinalDOB           finalDOB    = null;
  MultiQuoteMasterDOB           masterDOB    = null;
  QMSMultiQuoteSessionHome     home        = null;
  QMSMultiQuoteSession         remote      = null;
  HttpSession	session	= request.getSession();
  try
  {
    //lookUpBean  = new LookUpBean();
   
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = home.create();
    operation   = request.getParameter("Operation");
     ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
     
     if(!"Charges".equalsIgnoreCase(request.getParameter("Quotewith")))
    	 finalDOB    = (MultiQuoteFinalDOB)request.getAttribute("finalDOB")!= null?(MultiQuoteFinalDOB)request.getAttribute("finalDOB"):(MultiQuoteFinalDOB)session.getAttribute("finlaDOB");
     else
    	 finalDOB    = (MultiQuoteFinalDOB)session.getAttribute("finlaDOB")!= null ?(MultiQuoteFinalDOB)session.getAttribute("finlaDOB"):(MultiQuoteFinalDOB)request.getAttribute("finalDOB");
     
    masterDOB   = finalDOB.getMasterDOB();

  
     masterDOB.setCompanyId(loginbean.getCompanyId());
   
       finalDOB    = remote.getChargesAndHeader(finalDOB);
   
  }
  catch(Exception ex)
		{
    ex.printStackTrace();
			//Logger.error(FILE_NAME, " [error in doGetHeaderAndCharges()] -> "+ex.toString());
    logger.error(FILE_NAME+ " [error in doGetHeaderAndCharges()] -> "+ex.toString());
		}
  return finalDOB;
}

private void doSellCharges(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
	
	
	
	QMSMultiQuoteSessionHome     home              =    null;//@@added for the WPBN issue-61289
    QMSMultiQuoteSession         remote            =    null;
    MultiQuoteFlagsDOB           flagsDOB          =    null;
    MultiQuoteFlagsDOB           preFlagsDOB       =    null;
    MultiQuoteFlagsDOB          updatedFlagsDOB    =    null;
    MultiQuoteFreightLegSellRates legRateDetails   =    null;
    String                  	operation          = 	null;
	HttpSession             	session            = 	null;
	String 						update			   =	"";
	ESupplyGlobalParameters		loginbean		   =	null;
	MultiQuoteFinalDOB			finalDOB		   =	null;
	int							count			   =    1;
	int 						oCcount			   =	0;
	int							dCcount			   =	0;
	ArrayList                   attachmentIdList   =    null;
	ArrayList               	attachmentIdLOVList= 	null;
	ArrayList               	legDetails         = 	null;
	ArrayList					originChargesTemp  =    null;
	ArrayList					destChargesTemp    =    new ArrayList();
	ArrayList					finalLegRateDetails=    new ArrayList();
	String						forwardPage		   = 	null;
	String						marginTestFlag	   =	null;
	String                      multiQuoteSelectedBreaks = null;
	String[]					originChargeIndices1=   null;
	String[]					originChargeIndices=	null;
	String[]					destChargeIndices  =	null;
	String[]					destChargeIndices1 =    null;
	int[]						originIndices      =    null;
	int[]						destIndices        =    null;
	boolean                 	isCheating         = 	false;
	StringBuffer            	sb                 = 	new StringBuffer("");
	//Added by Rakesh
	MultiQuoteMasterDOB			masterDOB				= null;
	String                  	quoteId             	= null; 
       ArrayList               freightCharges      = null;
	try{
	      operation   = request.getParameter("Operation");
	      update   = request.getParameter("update");
	      session     = request.getSession();
	      loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
	      operation   = request.getParameter("Operation");
	      update   = request.getParameter("update");
	      session     = request.getSession();
	         
	      home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
	      remote      = home.create();
	      if("View".equalsIgnoreCase(operation))
	          finalDOB      =   (MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");
	      else
	          finalDOB      =   (MultiQuoteFinalDOB)session.getAttribute("finalDOB");
	      //@@Added by kiran.v on 08/08/2011 for Wpbn Issue 258778
	      if(!"View".equalsIgnoreCase(operation)){
	      masterDOB=finalDOB.getMasterDOB();
	      if("checked".equalsIgnoreCase(request.getParameter("selectCarrier")))
	    	  masterDOB.setCarrierChecked("on");
              else
            	  masterDOB.setCarrierChecked(""); 
              if("checked".equalsIgnoreCase(request.getParameter("selectService")))
            	  masterDOB.setServiceChecked("on");
                  else
                	  masterDOB.setServiceChecked(""); 
              if("checked".equalsIgnoreCase(request.getParameter("selectFrequency")))
            	  masterDOB.setFrequencyChecked("on");
                  else
                	  masterDOB.setFrequencyChecked(""); 
              if("checked".equalsIgnoreCase(request.getParameter("selectTransitTime")))
            	  masterDOB.setTransitTimeChecked("on");
                  else
                	  masterDOB.setTransitTimeChecked(""); 
              if("checked".equalsIgnoreCase(request.getParameter("selectFrieghtValidity")))
            	  masterDOB.setFrequencyValidityChecked("on");
                  else
                	  masterDOB.setFrequencyValidityChecked(""); 
              finalDOB.setMasterDOB(masterDOB);
              session.setAttribute("selectcheck",finalDOB);
	      }
	      multiQuoteSelectedBreaks = request.getParameter("selectedIndexList");
	      System.out.println("Forload test --QMSMultiQuoteController--1771"+multiQuoteSelectedBreaks);
	      if(multiQuoteSelectedBreaks != null && !"".equals(multiQuoteSelectedBreaks)){//Modified by Anil.k
	    	  multiQuoteSelectedBreaks = multiQuoteSelectedBreaks.substring(0,multiQuoteSelectedBreaks.length()-1);
	    	
	    	  finalDOB.setMultiQuoteSelectedBreaks(multiQuoteSelectedBreaks);
	      }
	      if("<<Back".equalsIgnoreCase(request.getParameter("submit")))
	      {
	 
	       if("View".equalsIgnoreCase(operation))
	          session.setAttribute("viewFinalDOB",finalDOB);
	        else
	          session.setAttribute("finalDOB",finalDOB);
	          request.setAttribute("BackButton","BackButton");
	          session.setAttribute("fromBack","BackButton");
	        
	        doDispatcher(request,response,request.getParameter("fromWhere"));
	      } else if("Save & Exit".equalsIgnoreCase(request.getParameter("submit")))
	      {
	    	  if(!("Modify".equalsIgnoreCase(operation)))	
	        {
	    		  flagsDOB = new MultiQuoteFlagsDOB();
	    		  flagsDOB.setCompleteFlag("I");
	              flagsDOB.setInternalExternalFlag("I");
	              flagsDOB.setSentFlag("U");
	              flagsDOB.setActiveFlag("A");
	              flagsDOB.setEscalationFlag("N");
	              
	              flagsDOB.setQuoteStatusFlag("GEN");
	              masterDOB = finalDOB.getMasterDOB();
	              finalDOB.setFlagsDOB(flagsDOB);
	              masterDOB.setVersionNo(1);//@@The version for this quote will be 1
	              finalDOB.setMasterDOB(masterDOB);   
	                    
	            finalDOB.setUpdate(update);
	            doMarginTest(request,response,finalDOB);
	              quoteId = remote.insertQuoteMasterDtls(finalDOB);//insert the quote details
	               finalDOB.getMasterDOB().setQuoteId(quoteId);
	                         
	              request.setAttribute("quoteId",""+quoteId);
         
	              
	                if(quoteId!=null && !"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))  //@@ Added by subrahmanyam for the Enhancement #146971 on 2/12/08
	                {
	                  setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId, operation , "QMSMultiQuoteController");
	                  session.removeAttribute("finalDOB");
	                  session.removeAttribute("PreFlagsDOB");
	                  doDispatcher(request, response, "QMSErrorPage.jsp");
	                  }
	        
	                      
	                //remote.updateSendMailFlag(quoteId,masterDOB.getUserId(),operation,false,0);
	        		
	        }else{
	        	flagsDOB = (MultiQuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
	        	flagsDOB.setCompleteFlag("I");
	            if(flagsDOB!=null)
	               finalDOB.setFlagsDOB(flagsDOB);
	            else
	               finalDOB.setFlagsDOB(new MultiQuoteFlagsDOB());

	             finalDOB.setCompareFlag(true);//@@For Save & Exit, no new Version will be created.
	        	
	              masterDOB = finalDOB.getMasterDOB();
	              finalDOB.setMasterDOB(masterDOB);   
	              finalDOB.setUpdate(update);
	              doMarginTest(request,response,finalDOB);
	              quoteId = remote.modifyQuoteMasterDtls(finalDOB);//insert the quote details
	               finalDOB.getMasterDOB().setQuoteId(quoteId);
	                         
	              request.setAttribute("quoteId",""+quoteId);
       
	              
	                if(quoteId!=null && !"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))  //@@ Added by subrahmanyam for the Enhancement #146971 on 2/12/08
	                {
	                  setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId, operation , "QMSMultiQuoteController");
	                  session.removeAttribute("finalDOB");
	                  session.removeAttribute("PreFlagsDOB");
	                  doDispatcher(request, response, "QMSErrorPage.jsp");
	                  }
	        
	                remote.updateSendMailFlag(quoteId,masterDOB.getUserId(),operation,false,0);	
	        
	        	
	        }
	      }else if("Margin Test".equalsIgnoreCase(request.getParameter("submit")))//Ended by Rakesh for the CR:231217
	      {
	        finalDOB =  doMarginTest(request,response,finalDOB);
	        session.setAttribute("finalDOB",finalDOB);
	        request.setAttribute("isMarginTestPerformed","Y");
	        if("charges".equalsIgnoreCase(finalDOB.getMasterDOB().getQuoteWith()))
	        request.setAttribute("fromWhere","qms/QMSMultiQuoteMaster.jsp");
	        else
	        request.setAttribute("fromWhere","qms/QMSMultiQuoteFreightSellRates.jsp");
	        if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))  // Added by Gowtham for multiquoteescalted modify
	        	doDispatcher(request,response,"qms/QMSMultiQuoteEscalatedChargesSelect.jsp?MarginTest=Yes&count="+count);
	        else  	
	        doDispatcher(request,response,"qms/QMSMultiQuoteChargesSelect.jsp?MarginTest=Yes&count="+count);
	      }else if("Next>>".equalsIgnoreCase(request.getParameter("submit"))|| "Update".equalsIgnoreCase(request.getParameter("submit"))) // Added by Gowtham for Multiquote escalated modify Issue.
	      {       
	          attachmentIdList =   remote.getAttachmentDtls(finalDOB);//@@added for the WPBN issue-61289
	         finalDOB.setAttachmentDOBList(attachmentIdList);  //@@added for the WPBN issue-61289
	        
	        finalDOB.setDefaultFlag("N");
	        attachmentIdLOVList  =   remote.getAttachmentIdList(finalDOB,"");
	        
	       if("View".equalsIgnoreCase(operation))
	       {
	    	//finalDOB =  doMarginTest(request,response,finalDOB);Commented by govind for stopping margin test while detail view
	    	 if(  "Update".equalsIgnoreCase(request.getParameter("submit"))){
	    		 finalDOB =  doMarginTest(request,response,finalDOB);//Modifed by gowtham to e
	    		 
	    	 }
	    	 session.setAttribute("finalDOB",finalDOB);
		     request.setAttribute("isMarginTestPerformed","Y");
		     request.setAttribute("fromWhere","qms/QMSMultiQuoteFreightSellRates.jsp");
	         //session.setAttribute("viewFinalDOB",finalDOB);
	         forwardPage = "qms/QMSMultiQuoteSummaryView.jsp";
	         marginTestFlag   = request.getParameter("marginTestFlag");//Added by Rakesh on 18-01-2011

	           
	           if(finalDOB!=null)
	             legDetails  = finalDOB.getLegDetails();
	           else
	             legDetails  = new ArrayList();
	           
	          int noOfLegs    = legDetails.size();
	             
	          for(int fr=0;fr<noOfLegs;fr++){
	               legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
	               originChargesTemp  =    new ArrayList();
	               destChargesTemp    =    new ArrayList();
	           originChargeIndices1 = request.getParameterValues("originChargeIndices"+fr);//added for duplicate charges
	            oCcount=0;
	  			 dCcount=0;
	  			//Added by Mohan Gajjala for on 30102010
	         //  request.getParameterValues("surChkBox");
	           if(originChargeIndices1!=null)
	           {
	           	int origCharInd1Len	=	originChargeIndices1.length;
	               for(int oc=0;oc<origCharInd1Len;oc++)
	               {
	                 if(originChargeIndices1[oc].trim().length()>0)
	                 {
	                 originChargesTemp.add(originChargeIndices1[oc].trim());
	                 // originChargeIndices[oCcount]=originChargeIndices1[oc];
	                 oCcount++;
	                 }
	               }
	               originChargeIndices = new String[oCcount];
	                 if(originChargesTemp!=null&&originChargesTemp.size()>0)
	                 {
	                 logger.info("userId and doSellCharges originChargesTemp :"+finalDOB.getMasterDOB().getUserId()+" :"+originChargesTemp);
	                  int origChargTempSize	= originChargesTemp.size();
	                 	for(int oc=0;oc<origChargTempSize;oc++)
	                   {
	                     originChargeIndices[oc]  = (String)originChargesTemp.get(oc);
	                   }
	               }
	           }
	           //ended for duplicate charge        
	          // destChargeIndices   = request.getParameterValues("destChargeIndices");// commented for duplicate charges
	          //added for duplicate charges
	           destChargeIndices1 = request.getParameterValues("destChargeIndices"+fr);
	           if(destChargeIndices1 !=null)
	           {
	           	int destChargeInd1Len	= destChargeIndices1.length;
	           for(int dc=0;dc<destChargeInd1Len;dc++)
	           {
	             if(destChargeIndices1[dc].trim().length()>0)
	             {
	               destChargesTemp.add(destChargeIndices1[dc].trim());
	               dCcount++;
	             }
	           }
	            destChargeIndices = new String[dCcount];
	             if(destChargesTemp!=null&&destChargesTemp.size()>0)
	           {
	           logger.info("userId and doSellCharges destChargesTemp :"+finalDOB.getMasterDOB().getUserId()+" :"+destChargesTemp);
	             int destTempChargSize	= destChargesTemp.size();	 
	             for(int dc=0;dc<destTempChargSize;dc++)
	             {
	                 destChargeIndices[dc]  = (String)destChargesTemp.get(dc);
	             }
	           }
	           }
	           //ended for duplicate charge
	          
	           if(originChargeIndices!=null)
	           {
	             
	             logger.info(" Userid and doSellCharges Selected originChargeIndices.length  : "+finalDOB.getMasterDOB().getUserId()+" :"+originChargeIndices.length); //newly added            
	             originIndices   = new int[originChargeIndices.length];
	          logger.info("originChargeIndices.length : "+originChargeIndices.length);
	          		int orgChargIndLen	=	originChargeIndices.length;
	             for(int i=0;i<orgChargIndLen;i++)
	             {
	               
	                if(originChargeIndices[i].trim().length()!=0)
	               {
	               logger.info("doSellCharges Selected originChargeIndices if block: "+finalDOB.getMasterDOB().getUserId()+" :"+ originChargeIndices[i]); //newly added
	                  originIndices[i] =  Integer.parseInt(originChargeIndices[i]);

	               }else{
	                   logger.info("doSellCharges Selected originChargeIndices else Block : "+finalDOB.getMasterDOB().getUserId()+" :"+ originChargeIndices[i]); //newly added
	                  originIndices[i] =  -1;

	               }
	                
	             }
	           }
	        
	           if(destChargeIndices!=null)
	           {
	             destIndices   = new int[destChargeIndices.length];
	             logger.info(" Userid and doSellCharges Selecetd destChargeIndices.length  : "+finalDOB.getMasterDOB().getUserId()+" :"+destChargeIndices.length); //newly added
	             int destChagIndLen	=	destChargeIndices.length;
	             for(int i=0;i<destChagIndLen;i++)
	             {
	                if(destChargeIndices[i].trim().length()!=0){
	                 logger.info("doSellCharges Selected destChargeIndices if Block : "+finalDOB.getMasterDOB().getUserId()+" :"+ destChargeIndices[i]); //newly added
	                 destIndices[i] =  Integer.parseInt(destChargeIndices[i]);
	               }else{
	                 logger.info("doSellCharges Selected destChargeIndices else Block : "+finalDOB.getMasterDOB().getUserId()+" :"+ destChargeIndices[i]); //newly added
	                 destIndices[i] =  -1;
	               }
	   
	             }
	           }

	      if(legRateDetails.getSelectedOriginChargesListIndices()!=null && originChargeIndices!=null)
	             {
	               if(originChargeIndices.length > legRateDetails.getSelectedOriginChargesListIndices().length)
	               {
	                   isCheating  = true;
	                   sb.append("One of the Selected Origin Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
	               }
	             }
	             if(legRateDetails.getSelctedDestChargesListIndices()!=null && destChargeIndices!=null)
	             {
	               if(destChargeIndices.length > legRateDetails.getSelctedDestChargesListIndices().length)
	               {
	                   isCheating  = true;
	                   sb.append("One of the Selected Destination Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
	               }
	             }
	             if(legRateDetails.getSelectedOriginChargesListIndices()==null && originChargeIndices!=null)
	             {
	                 isCheating  = true;
	                 sb.append("One of the Selected Origin Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
	             }
	             
	             if(legRateDetails.getSelctedDestChargesListIndices()==null && destChargeIndices!=null)
	             {
	                 isCheating  = true;
	                 sb.append("One of the Selected Destination Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");    
	             }
	             
	             if(isCheating)
	             {
	               forwardPage = "qms/QMSMultiQuoteChargesSelect.jsp?count=0";
	             }

	             legRateDetails.setSelectedOriginChargesListIndices(originIndices);
		         legRateDetails.setSelctedDestChargesListIndices(destIndices);
		         finalLegRateDetails.add(legRateDetails);
		         originIndices = null;
		         destIndices   = null;
		         originChargeIndices = null;
		         destChargeIndices = null;
	  		}
	          finalDOB.setLegDetails(finalLegRateDetails);
	  	  
	       }
	       else
	       {
	           marginTestFlag   = request.getParameter("marginTestFlag");
	           if(finalDOB!=null)
	             legDetails  = finalDOB.getLegDetails();
	           else
	             legDetails  = new ArrayList();
	           
	          int noOfLegs    = legDetails.size();
	             
	          for(int fr=0;fr<noOfLegs;fr++){
	               legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
	               originChargesTemp  =    new ArrayList();
	               destChargesTemp    =    new ArrayList();
	           originChargeIndices1 = request.getParameterValues("originChargeIndices"+fr);//added for duplicate charges
	            oCcount=0;
	  			 dCcount=0;
	  			//Added by Mohan Gajjala for on 30102010
	         //  request.getParameterValues("surChkBox");
	           if(originChargeIndices1!=null)
	           {
	           	int origCharInd1Len	=	originChargeIndices1.length;
	               for(int oc=0;oc<origCharInd1Len;oc++)
	               {
	                 if(originChargeIndices1[oc].trim().length()>0)
	                 {
	                 originChargesTemp.add(originChargeIndices1[oc].trim());
	                 // originChargeIndices[oCcount]=originChargeIndices1[oc];
	                 oCcount++;
	                 }
	               }
	               originChargeIndices = new String[oCcount];
	                 if(originChargesTemp!=null&&originChargesTemp.size()>0)
	                 {
	                 logger.info("userId and doSellCharges originChargesTemp :"+finalDOB.getMasterDOB().getUserId()+" :"+originChargesTemp);
	                  int origChargTempSize	= originChargesTemp.size();
	                 	for(int oc=0;oc<origChargTempSize;oc++)
	                   {
	                     originChargeIndices[oc]  = (String)originChargesTemp.get(oc);
	                   }
	               }
	           }
	           //ended for duplicate charge        
	          // destChargeIndices   = request.getParameterValues("destChargeIndices");// commented for duplicate charges
	          //added for duplicate charges
	           destChargeIndices1 = request.getParameterValues("destChargeIndices"+fr);
	           if(destChargeIndices1 !=null)
	           {
	           	int destChargeInd1Len	= destChargeIndices1.length;
	           for(int dc=0;dc<destChargeInd1Len;dc++)
	           {
	             if(destChargeIndices1[dc].trim().length()>0)
	             {
	               destChargesTemp.add(destChargeIndices1[dc].trim());
	               dCcount++;
	             }
	           }
	            destChargeIndices = new String[dCcount];
	             if(destChargesTemp!=null&&destChargesTemp.size()>0)
	           {
	           logger.info("userId and doSellCharges destChargesTemp :"+finalDOB.getMasterDOB().getUserId()+" :"+destChargesTemp);
	             int destTempChargSize	= destChargesTemp.size();	 
	             for(int dc=0;dc<destTempChargSize;dc++)
	             {
	                 destChargeIndices[dc]  = (String)destChargesTemp.get(dc);
	             }
	           }
	           }
	           //ended for duplicate charge
	          
	           if(originChargeIndices!=null)
	           {
	             
	             logger.info(" Userid and doSellCharges Selected originChargeIndices.length  : "+finalDOB.getMasterDOB().getUserId()+" :"+originChargeIndices.length); //newly added            
	             originIndices   = new int[originChargeIndices.length];
	          logger.info("originChargeIndices.length : "+originChargeIndices.length);
	          		int orgChargIndLen	=	originChargeIndices.length;
	             for(int i=0;i<orgChargIndLen;i++)
	             {
	               
	                if(originChargeIndices[i].trim().length()!=0)
	               {
	               logger.info("doSellCharges Selected originChargeIndices if block: "+finalDOB.getMasterDOB().getUserId()+" :"+ originChargeIndices[i]); //newly added
	                  originIndices[i] =  Integer.parseInt(originChargeIndices[i]);

	               }else{
	                   logger.info("doSellCharges Selected originChargeIndices else Block : "+finalDOB.getMasterDOB().getUserId()+" :"+ originChargeIndices[i]); //newly added
	                  originIndices[i] =  -1;

	               }
	                
	             }
	           }
	        
	           if(destChargeIndices!=null)
	           {
	             destIndices   = new int[destChargeIndices.length];
	             logger.info(" Userid and doSellCharges Selecetd destChargeIndices.length  : "+finalDOB.getMasterDOB().getUserId()+" :"+destChargeIndices.length); //newly added
	             int destChagIndLen	=	destChargeIndices.length;
	             for(int i=0;i<destChagIndLen;i++)
	             {
	                if(destChargeIndices[i].trim().length()!=0){
	                 logger.info("doSellCharges Selected destChargeIndices if Block : "+finalDOB.getMasterDOB().getUserId()+" :"+ destChargeIndices[i]); //newly added
	                 destIndices[i] =  Integer.parseInt(destChargeIndices[i]);
	               }else{
	                 logger.info("doSellCharges Selected destChargeIndices else Block : "+finalDOB.getMasterDOB().getUserId()+" :"+ destChargeIndices[i]); //newly added
	                 destIndices[i] =  -1;
	               }
	   
	             }
	           }

	      if(legRateDetails.getSelectedOriginChargesListIndices()!=null && originChargeIndices!=null)
	             {
	               if(originChargeIndices.length > legRateDetails.getSelectedOriginChargesListIndices().length)
	               {
	                   isCheating  = true;
	                   sb.append("One of the Selected Origin Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
	               }
	             }
	             if(legRateDetails.getSelctedDestChargesListIndices()!=null && destChargeIndices!=null)
	             {
	               if(destChargeIndices.length > legRateDetails.getSelctedDestChargesListIndices().length)
	               {
	                   isCheating  = true;
	                   sb.append("One of the Selected Destination Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
	               }
	             }
	             if(legRateDetails.getSelectedOriginChargesListIndices()==null && originChargeIndices!=null)
	             {
	                 isCheating  = true;
	                 sb.append("One of the Selected Origin Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
	             }
	             
	             if(legRateDetails.getSelctedDestChargesListIndices()==null && destChargeIndices!=null)
	             {
	                 isCheating  = true;
	                 sb.append("One of the Selected Destination Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");    
	             }
	             
	             if(isCheating)
	             {
	               forwardPage = "qms/QMSMultiQuoteChargesSelect.jsp?count=0";
	             }

	             legRateDetails.setSelectedOriginChargesListIndices(originIndices);
		         legRateDetails.setSelctedDestChargesListIndices(destIndices);
		         finalLegRateDetails.add(legRateDetails);
		         originIndices = null;
		         destIndices   = null;
		         originChargeIndices = null;
		         destChargeIndices = null;
	  		}
	          finalDOB.setLegDetails(finalLegRateDetails);
	  	  }
	          
	  //@@ Added by the subrahmanyam for the notes Issue 194328.            
	           finalDOB.setInternalNotes(removeEnterForNotes(request.getParameterValues("internalNotes")));
	           finalDOB.setExternalNotes(removeEnterForNotes(request.getParameterValues("externalNotes")));
	  //@@ Ended for notes Issue 194328.            
	           finalDOB.setUpdate(update);//@@Added by Kameswari on 08/01/09
	           preFlagsDOB      = (MultiQuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
	           updatedFlagsDOB  = (MultiQuoteFlagsDOB)session.getAttribute("updatedSendOptions");
	           session.removeAttribute("updatedSendOptions");
	           flagsDOB  = new MultiQuoteFlagsDOB();
	           
	         
	           if(marginTestFlag != null)
	           {          
	        	   //Modified by Rakesh on 18-01-2011
	        	   if("View".equalsIgnoreCase(operation))
	    	       {
	        		   forwardPage = "qms/QMSMultiQuoteSummaryView.jsp";
	    	       	}else{
	               forwardPage = "qms/QMSMultiQuoteSummary.jsp?update=update";
	    	       }
	               flagsDOB.setSentFlag("U");//@@Quote is still unsent.
	               if("F".equals(marginTestFlag))
	               {
	                  finalDOB.setEscalatedTo(finalDOB.getReportingOfficer());
	                  flagsDOB.setQuoteStatusFlag("QUE");//@@Quote is Queued.
	                  flagsDOB.setEscalationFlag("Y");
	                  flagsDOB.setInternalExternalFlag("I");
	               }
	               else
	               {
	                 flagsDOB.setInternalExternalFlag("E");
	                 flagsDOB.setEscalationFlag("N");
	               }
	               if(preFlagsDOB != null)//@@In Case Of Operation -- Modify/Copy
	               {
	                 flagsDOB.setEmailFlag(preFlagsDOB.getEmailFlag());
	                 flagsDOB.setPrintFlag(preFlagsDOB.getPrintFlag());
	                 flagsDOB.setFaxFlag(preFlagsDOB.getFaxFlag());
	                 flagsDOB.setUpdateFlag(preFlagsDOB.getUpdateFlag());
	               }
	               if(updatedFlagsDOB!=null)//@@When the user Clicks on the link to Open an Updated Quote
	               {
	                 flagsDOB.setEmailFlag(updatedFlagsDOB.getEmailFlag());
	                 flagsDOB.setPrintFlag(updatedFlagsDOB.getPrintFlag());
	                 flagsDOB.setFaxFlag(updatedFlagsDOB.getFaxFlag());
	                 flagsDOB.setUpdateFlag(updatedFlagsDOB.getUpdateFlag());
	               }
	               updatedFlagsDOB = null;
	             }
	           
	           finalDOB.setFlagsDOB(flagsDOB);
	           
	           session.setAttribute("finalDOB",finalDOB);
	           request.setAttribute("attachmentIdLOVList",attachmentIdLOVList);

	     
	      // Logger.info(FILE_NAME,"sbsbsbsb::"+sb.length());
	       request.setAttribute("errors",sb);
	       //session.setAttribute("finalDOB",finalDOB);
	       if("Update".equalsIgnoreCase(request.getParameter("submit"))) // Added by Gowtham for escalated quote issue.
	         {	//quoteId = request.getParameter("QuoteId");
	    	    doSummary(request,response);
	         	//quoteId = remote.modifyQuoteMasterDtls(finalDOB);
	        	setErrorRequestValues(request, "RSI", "The Escalated Quote Updated Successfully ", operation , "QMSMultiQuoteController");
	        	session.removeAttribute("finalDOB");
	        	doDispatcher(request, response,"QMSErrorPage.jsp");
	         }
	       else
	       doDispatcher(request,response,forwardPage);
	     }
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	}catch(Exception ex){
	     session.removeAttribute("finalDOB");
	      session.removeAttribute("PreFlagsDOB");
	      session.removeAttribute("updatedSendOptions");
	      ex.printStackTrace();
	      logger.error(FILE_NAME+ " [error in doSellCharges()] -> "+ex.toString());
	      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSMultiQuoteController?Operation=Add");
	      doDispatcher(request, response, "ESupplyErrorPage.jsp");

	} 
}

	private MultiQuoteFinalDOB doMarginTest(HttpServletRequest request,HttpServletResponse response,MultiQuoteFinalDOB finalDOB) throws ServletException,IOException
{
    MultiQuoteFreightLegSellRates legDOB            	= null;
    MultiQuoteFreightLegSellRates legRateDetails		= null;
    MultiQuoteChargeInfo         chargeInfo         	= null;
    MultiQuoteCharges             chargesDOB        	= null;
    MultiQuoteMasterDOB			masterDOB				= null;//Added by Anil.k
    ArrayList 					originCharges			= null;
    ArrayList 					destCharges				= null;
    ArrayList               	destChargeInfo     		= null;
    String[]               		frtChargeIndices    	= null;
    String[]               		marginType          	= null;
    String[]                	margin              	= null;
    String[]                	sellRate            	= null;
    String[]                	discountType        	= null;
    String[]                	discount            	= null;
    ArrayList                   frtCharges          	= null;
    ArrayList                   frtChargeInfo       	= null;
    ArrayList					chargeInfoList	    	= null;
    ArrayList               	originChargeInfo   		= null;
    String[]					selectedFrtindicies		= null;
    ArrayList					selectedBrkIndicies		= new ArrayList();
    String[]					multiMarginTypes		= null;
    String[]					multiDiscountTypes		= null;
    String[]					multiMarginValues		= null;
    String[]					multiDiscountValues		= null;
    String[]					multiCalSellRates		= null;
    String[]  					tempArray               = null;
    String[]                    originChargeIndices     = null;
    String[]                	destChargeIndices  		= null;
    String[]					originChargeIndices1    = null;
    String[]					destChargeIndices1		= null;
    String[]            		originChargeSelectedFlag= null;
    String[]            		destChargeSelectedFlag  = null;
    boolean[]                   freightBrekMarginTest   = null;
    String					    multiSelectedFlag		= null;
    String                      selectedIndexList		= null;
    int							chargesInfoSize	    	= 0;
    int							selectedsize			= 0;
    int 						originChargesSize		= 0;
    int                     	destChargesInfoSize		= 0;
    int 						oCcount 				= 0;
    int 						dCcount 				= 0;
    double                  	originChargesMargin     = 0;
    double                  	originChargesDiscount 	= 0;
    double                  	destChargesMargin     	= 0;
    double                  	destChargesDiscount   	= 0;
    int[]                   	originIndices      		= null;
    int[]                   	destIndices        		= null;
    ArrayList               	originChargesTemp     	= null;
    ArrayList               	destChargesTemp       	= null;
    HttpSession 				session					= null;
    
    
     

    try{
    	session = request.getSession();
    	finalDOB.setInternalNotes(removeEnterForNotes(request.getParameterValues("internalNotes")));
        finalDOB.setExternalNotes(removeEnterForNotes(request.getParameterValues("externalNotes")));
        legDOB = (MultiQuoteFreightLegSellRates)(finalDOB.getLegDetails().get(0));
        //Added by Anil.k
        masterDOB = (MultiQuoteMasterDOB)finalDOB.getMasterDOB();
        if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith())){//END
        chargeInfoList = ((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(0)).getFreightChargesList();
        chargesInfoSize	= chargeInfoList.size();
        }//Added by Anil.k
        selectedIndexList = request.getParameter("selectedIndexList");
        session.setAttribute("selectedIndexList", selectedIndexList);
      if(selectedIndexList != null && !"".equalsIgnoreCase(selectedIndexList) && selectedIndexList.length()!=0){//Modified by Anil.k
        selectedIndexList = selectedIndexList.substring(0,selectedIndexList.length()-1);
        selectedFrtindicies = selectedIndexList.split(",");
        
        selectedsize = selectedFrtindicies.length;
      }else {
    	  selectedsize =0;
	}
       //for charges
        
        int frtsize = finalDOB.getLegDetails().size();
        
        
        for(int fr=0;fr<frtsize;fr++){
        	oCcount=0;
        	dCcount=0;
        	originChargesTemp     	= new ArrayList();
            destChargesTemp       	= new ArrayList(); 
        legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
       		 originCharges  = legRateDetails.getOriginChargesList();
                destCharges    = legRateDetails.getDestChargesList();
             
             if(originCharges!=null)
       					originChargesSize		= originCharges.size();
       			else
       					originChargesSize		= 0;
              
              
               
       //commented for duplicate charges
             /*  originChargeIndices = request.getParameterValues("originChargeIndices");
               destChargeIndices   = request.getParameterValues("destChargeIndices");
               */
       //added for duplicate charges for pbn id: 186507
               originChargeIndices1 = request.getParameterValues("originChargeIndices"+fr);
               destChargeIndices1   = request.getParameterValues("destChargeIndices"+fr);
               if(originChargeIndices1!=null)
               {
               	int orgChargInd1Len	=	originChargeIndices1.length;
                 for(int oc=0;oc<orgChargInd1Len;oc++)
                 {
                     if(originChargeIndices1[oc].trim().length()>0)
                     {
                       originChargesTemp.add(originChargeIndices1[oc].trim());
                       oCcount++;
                     }
                 }
               originChargeIndices = new String[oCcount];
                 if(originChargesTemp!=null && originChargesTemp.size()>0)
                 {
                    logger.info(" Userid and doMargintest originChargesTemp  : "+finalDOB.getMasterDOB().getUserId()+" :"+originChargesTemp); //newly added
                    int orgChargTempSize	=	originChargesTemp.size();
                    for(int oc=0;oc<orgChargTempSize;oc++)
                   {
                       originChargeIndices[oc]= (String)originChargesTemp.get(oc);
                   }
                 }
               }
               if(destChargeIndices1!=null)
               {
               	int destChargInd1Len	=	destChargeIndices1.length;
                 for(int dc=0;dc<destChargInd1Len;dc++)
                 {
                     if(destChargeIndices1[dc].trim().length()>0)
                     {
                       destChargesTemp.add(destChargeIndices1[dc].trim());
                       dCcount++;
                     }
                 }
                 destChargeIndices = new String[dCcount];
                  if(destChargesTemp!=null && destChargesTemp.size()>0)
                 {
                  logger.info(" Userid and doMargintest destChargesTemp  : "+finalDOB.getMasterDOB().getUserId()+" :"+destChargesTemp); //newly added
                  int destChargTempSize	=	destChargesTemp.size(); 
                  for(int dc=0;dc<destChargTempSize;dc++)
                   {
                       destChargeIndices[dc]= (String)destChargesTemp.get(dc);
                   }
                 }
               }
       //ended for duplicate charges for 186507
              
               finalDOB.setInternalNotes(removeEnterForNotes(request.getParameterValues("internalNotes")));
               finalDOB.setExternalNotes(removeEnterForNotes(request.getParameterValues("externalNotes")));
               //@@Ended for the notes Issue 194328
               originChargeSelectedFlag = request.getParameterValues("originChargeSelectedFlag"+fr);//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09    
                destChargeSelectedFlag = request.getParameterValues("destChargeSelectedFlag"+fr);//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09    
                
             
               if(originChargeIndices!=null)
               {
                 originIndices   = new int[originChargeIndices.length];
                 logger.info(" Userid and doMargintest originChargeIndices.length  : "+finalDOB.getMasterDOB().getUserId()+" :"+originChargeIndices.length); //newly added          
                 int orgChargIndLen	=	originChargeIndices.length;
                 for(int i=0;i<orgChargIndLen;i++)
                 {
                   if(i==0)
                   {
                   if(originChargeIndices[i].trim().length()!=0)
                     originIndices[i] =  Integer.parseInt(originChargeIndices[i]);
                   else
                     originIndices[i] = -1;//else condition added by subrahmanyam for the duplicate charges

                   }
                   else
                   {
                     if(originChargeIndices[i]!=originChargeIndices[i-1])
                     {
                        if(originChargeIndices[i].trim().length()!=0){
                         logger.info("domargintest originChargeIndices if block: "+finalDOB.getMasterDOB().getUserId()+" :"+ originChargeIndices[i]); //newly added
                         originIndices[i] =  Integer.parseInt(originChargeIndices[i]);
                       }else{
                         logger.info("domargintest originChargeIndices else block: "+finalDOB.getMasterDOB().getUserId()+" :"+ originChargeIndices[i]); //newly added
                         originIndices[i] = -1;
                       }

                    }
                    else
                      originIndices[i] =-1;
                   }
                   }
                 int orgIndLen	=	originIndices.length;
                 for(int i=0;i<orgIndLen;i++)  
                 {
                  
                   if(originIndices[i]!=-1)
                   {
                   chargesDOB				      = (MultiQuoteCharges)originCharges.get(originIndices[i]);
                   logger.info("User Id : chargesDOB : "+finalDOB.getMasterDOB().getUserId()+" :"+chargesDOB);//newly added          
                   originChargeInfo		    = chargesDOB.getChargeInfoList();
                   
                    if("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "SC".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
                   {
                       originChargesMargin   = finalDOB.getCartageMargin();
                       originChargesDiscount = finalDOB.getCartageDiscount();
                   }
                   else
                   {
                       originChargesMargin   = finalDOB.getChargesMargin();
                       originChargesDiscount = finalDOB.getChargesDiscount();
                   }
                   
                   //if(request.getParameterValues("originMarginType"+originIndices[i])!=null)
                   if(request.getParameterValues("originMarginType"+fr+originIndices[i])!=null)
                   {
                     marginType  =  request.getParameterValues("originMarginType"+fr+originIndices[i]);
                     margin      =  request.getParameterValues("originMargin"+fr+originIndices[i]);
                     sellRate    =  request.getParameterValues("originSellRate"+fr+originIndices[i]);
                     if(margin!=null)
                     {
                     chargesDOB.setMarginDiscountFlag("M");
                     
                     int marginLen	=	margin.length;
                     for(int k=0;k<marginLen;k++)
                     {
                       chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);
                       chargeInfo.setMarginDiscountFlag("M");
                       chargeInfo.setMarginType(marginType[k]);
                       chargeInfo.setMargin(Double.parseDouble(margin[k]));
                       chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                       if("P".equalsIgnoreCase(marginType[k]))
                       {
                    	   if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))  // Added by Gowtham.
                        	  {
                        		if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > originChargesDiscount)
                                 chargeInfo.setMarginTestFailed(true);
                             else
                                 chargeInfo.setMarginTestFailed(false);
                        	  }
       //@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10     & 205785 on 17-May-10           	
                    	   else if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                      			   ||("Modify".equalsIgnoreCase(request.getParameter("Operation"))&& !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()) ) ) )
                      	  {
                       		  if(Double.parseDouble(margin[k]) == chargeInfo.getTieMarginDiscountValue() ||
                       				  (Double.parseDouble(margin[k])>chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(margin[k])<=originChargesMargin) )
                      		  {
                      			    chargeInfo.setMarginTestFailed(false);
                      		  }
                      		  else
                      		  {
                         		if(Double.parseDouble(margin[k]) < originChargesMargin)
                       			chargeInfo.setMarginTestFailed(true);
                       		else
                       			chargeInfo.setMarginTestFailed(false);

                      		  }
                      	  }//@@Ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                      	else
                       	{
                       		if(Double.parseDouble(margin[k]) < originChargesMargin)
                       			chargeInfo.setMarginTestFailed(true);
                       		else
                       			chargeInfo.setMarginTestFailed(false);
                       	}
                       }
                       else if("A".equalsIgnoreCase(marginType[k]))
                       {
                    	   if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))  // Added by Gowtham.
                        	  {
                        		if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > originChargesDiscount)
                                 chargeInfo.setMarginTestFailed(true);
                             else
                                 chargeInfo.setMarginTestFailed(false);
                        	  }
                       	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10     & 205785 on 17-May-10           	
                    	   else if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                       			   ||("Modify".equalsIgnoreCase(request.getParameter("Operation"))&& !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag())) ) )
                       	  {
                        		  if(Double.parseDouble(margin[k]) == chargeInfo.getTieMarginDiscountValue() ||
                        				 (Double.parseDouble(margin[k])>chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(margin[k])<=originChargesMargin) )
                       		  {
                       			    chargeInfo.setMarginTestFailed(false);
                       		  }
                       		  else
                       		  {
                                 	if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < originChargesMargin)
                               		chargeInfo.setMarginTestFailed(true);
                               	else
                               		chargeInfo.setMarginTestFailed(false);

                       		  }
                       	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                           else
                       	{
                           	if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < originChargesMargin)
                           		chargeInfo.setMarginTestFailed(true);
                           	else
                           		chargeInfo.setMarginTestFailed(false);
                       	}
                       }
                       originChargeInfo.remove(k);
                       originChargeInfo.add(k,chargeInfo);
                      }
                     }
                   }
               //  } //newly commented
                   //if(!"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))//@@If discount is given by the user
                     if("SC".equals(chargesDOB.getSellBuyFlag())||"S".equals(chargesDOB.getSellBuyFlag()))//@@Modified by Kameswari for the enhancement
                   {
                     if(request.getParameter("originDiscountType"+fr+originIndices[i])!=null)
                     {
                       discountType  =  request.getParameterValues("originDiscountType"+fr+originIndices[i]);
                       discount      =  request.getParameterValues("originDiscount"+fr+originIndices[i]);
                       sellRate      =  request.getParameterValues("originSellRate"+fr+originIndices[i]);
                        if(discount!=null)//@@Added by Kameswari for the WPBN issue-139966
                       {
                       chargesDOB.setMarginDiscountFlag("D");
                       int disLen	=	discount.length;
                        for(int k=0;k<disLen;k++)
                       {
                         chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);
                         chargeInfo.setDiscountType(discountType[k]);
                        //double d = Double.parseDouble(discount[k]);
                         chargeInfo.setDiscount(Double.parseDouble(discount[k]));
                         chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                         
                         if("P".equalsIgnoreCase(discountType[k]))
                         { 
                        	 if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))  // Added by Gowtham.
                          	  {
                          		if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > originChargesDiscount)
                                   chargeInfo.setMarginTestFailed(true);
                               else
                                   chargeInfo.setMarginTestFailed(false);
                          	  }
                        	 else if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                       			   ||("Modify".equalsIgnoreCase(request.getParameter("Operation")) && !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag())) )  )
                       	  {
                        		  if(Double.parseDouble(discount[k]) == chargeInfo.getTieMarginDiscountValue() ||
                        				  (Double.parseDouble(discount[k]) < chargeInfo.getTieMarginDiscountValue() && 
                        						 Double.parseDouble(discount[k])>= originChargesDiscount) )
                       		  {
                       			    chargeInfo.setMarginTestFailed(false);
                       		  }
                       		  else
                       		  {
                                     if(Double.parseDouble(discount[k]) > originChargesDiscount)
                                         chargeInfo.setMarginTestFailed(true);
                                       else
                                         chargeInfo.setMarginTestFailed(false);

                       		  }
                       	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                       	  else
                       	  {
                       		  if(Double.parseDouble(discount[k]) > originChargesDiscount)
                       			  chargeInfo.setMarginTestFailed(true);
                       		  else
                       			  chargeInfo.setMarginTestFailed(false);
                       	  }
                         }
                         else if("A".equalsIgnoreCase(discountType[k]))
                         {
                       	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   & 205785 on 17-May-10             	  
                          	  /*if("Y".equalsIgnoreCase(chargeInfo.getMartinTeStFlag()) &&"COPY".equalsIgnoreCase(request.getParameter("Operation")) 
                       			  && "S".equalsIgnoreCase(finalDOB.getFlagsDOB().getSentFlag()) && "E".equalsIgnoreCase(finalDOB.getFlagsDOB().getInternalExternalFlag())
                       			  && !"".equalsIgnoreCase(finalDOB.getEscalatedTo()))
                       	  */if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))  // Added by Gowtham.
                       	  {
                       		if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > originChargesDiscount)
                                chargeInfo.setMarginTestFailed(true);
                            else
                                chargeInfo.setMarginTestFailed(false);
                       	  }
                       	  else if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                      			   ||("Modify".equalsIgnoreCase(request.getParameter("Operation")) && !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag())) ) )
                       	  
                       	  {
                          		if(Double.parseDouble(discount[k]) == chargeInfo.getTieMarginDiscountValue() ||
                          				(Double.parseDouble(discount[k])<chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(discount[k])>= originChargesDiscount) )
                       		  chargeInfo.setMarginTestFailed(false);
                          		else
                          		{
                                   if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > originChargesDiscount)
                                       chargeInfo.setMarginTestFailed(true);
                                   else
                                       chargeInfo.setMarginTestFailed(false);

                          		}
                       	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                            else{
                           if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > originChargesDiscount)
                               chargeInfo.setMarginTestFailed(true);
                           else
                               chargeInfo.setMarginTestFailed(false);
                          	  }
                         }
                         originChargeInfo.remove(k);
                         originChargeInfo.add(k,chargeInfo);
                         }
                       }
                     }
                   }
                   chargesDOB.setChargeInfoList(originChargeInfo);
                   originCharges.remove(originIndices[i]);
                   originCharges.add(originIndices[i],chargesDOB);
                  }//newly added if end           
                 }
                 legRateDetails.setOriginChargesList(originCharges);
                 legRateDetails.setSelectedOriginChargesListIndices(originIndices);
                 legRateDetails.setOriginChargesSelectedFlag(originChargeSelectedFlag);//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09  
               }
               else
               {
            	   legRateDetails.setSelectedOriginChargesListIndices(new int[0]);
               }
             
               if(destChargeIndices!=null)//@@ To Perform margin Test on Destination Charges
               {
                 
                 destIndices   = new int[destChargeIndices.length];
                           logger.info(" Userid and domargintest Selected destChargeIndices.length  : "+finalDOB.getMasterDOB().getUserId()+" :"+destChargeIndices.length); //newly added
                int destChargIndLen	=	destChargeIndices.length; 
                for(int i=0;i<destChargIndLen;i++)
                 {
                    if(destChargeIndices[i].trim().length()!=0){
                    logger.info("domargintest Selected destChargeIndices if block: "+finalDOB.getMasterDOB().getUserId()+" :"+ destChargeIndices[i]); //newly added
                     destIndices[i] =  Integer.parseInt(destChargeIndices[i]);
                   }else{ //newly added
                     logger.info("domargintest Selected destChargeIndices else block: "+finalDOB.getMasterDOB().getUserId()+" :"+ destChargeIndices[i]); //newly added
                     destIndices[i] =  -1;
                   }
                   if(destIndices[i] !=-1)
                   {
                   chargesDOB				      =  (MultiQuoteCharges)destCharges.get(destIndices[i]);
                   destChargeInfo		      =  chargesDOB.getChargeInfoList();
                   destChargesInfoSize     =  destChargeInfo.size();
                   
                   if("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "SC".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
                   {
                       destChargesMargin     = finalDOB.getCartageMargin();
                       destChargesDiscount   = finalDOB.getCartageDiscount();
                   }
                   else
                   {
                       destChargesMargin     = finalDOB.getChargesMargin();
                       destChargesDiscount   = finalDOB.getChargesDiscount();
                   }
                   
                   if(request.getParameterValues("destMarginType"+fr+destIndices[i])!=null)
                   {
                     marginType  =  request.getParameterValues("destMarginType"+fr+destIndices[i]);
                      margin      =  request.getParameterValues("destMargin"+fr+destIndices[i]);
                     sellRate    =  request.getParameterValues("destSellRate"+fr+destIndices[i]);
                   
                     
                     if(margin!=null)
                     {
                       chargesDOB.setMarginDiscountFlag("M");
                       int marginLen	=	margin.length;
                     for(int k=0;k<marginLen;k++)
                     {
                       chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);
                       chargeInfo.setMarginType(marginType[k]);
                       
                       chargeInfo.setMargin(Double.parseDouble(margin[k]));
                       chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                       
                       if("P".equalsIgnoreCase(marginType[k]))
                       {
                       	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   & 205785 on 17-May-10             	
                           if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                       			   ||("Modify".equalsIgnoreCase(request.getParameter("Operation")) && !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag())) ) )
                       	  {
                        		  if(Double.parseDouble(margin[k]) == chargeInfo.getTieMarginDiscountValue()
                        				  || (Double.parseDouble(margin[k]) >chargeInfo.getTieMarginDiscountValue() && 
                        						 Double.parseDouble(margin[k])<=destChargesMargin ))
                       		  {
                       			    chargeInfo.setMarginTestFailed(false);
                       		  }
                       		  else
                       		  {
                                    	if(Double.parseDouble(margin[k]) < destChargesMargin)
                                   		chargeInfo.setMarginTestFailed(true);
                                   	else
                                   		chargeInfo.setMarginTestFailed(false);

                       		  }
                       	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                           else
                           {
                           	if(Double.parseDouble(margin[k]) < destChargesMargin)
                           		chargeInfo.setMarginTestFailed(true);
                           	else
                           		chargeInfo.setMarginTestFailed(false);
                           }
                       }
                       else if("A".equalsIgnoreCase(marginType[k]))
                       {
                       	//@@added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10    & 205785 on 17-May-10            	
                           if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                       			   || ("Modify".equalsIgnoreCase(request.getParameter("Operation")) && !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()) ) ) )
                       	  {
                        		  if(Double.parseDouble(margin[k]) == chargeInfo.getTieMarginDiscountValue() 
                        				  || (Double.parseDouble(margin[k])>chargeInfo.getTieMarginDiscountValue() && 
                        						 Double.parseDouble(margin[k])<=destChargesMargin) )
                       		  {
                       			    chargeInfo.setMarginTestFailed(false);
                       		  }
                       		  else
                       		  {
                                    	if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < destChargesMargin)
                                   		chargeInfo.setMarginTestFailed(true);
                                   	else
                                   		chargeInfo.setMarginTestFailed(false);
                
                       		  }
                       	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                           else
                           {
                           	if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < destChargesMargin)
                           		chargeInfo.setMarginTestFailed(true);
                           	else
                           		chargeInfo.setMarginTestFailed(false);
                           }
                       }
                       destChargeInfo.remove(k);
                       destChargeInfo.add(k,chargeInfo);
                     }
                     }
                   }
                   // if(!"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))//@@For Discount
                   if("SC".equals(chargesDOB.getSellBuyFlag())||"S".equals(chargesDOB.getSellBuyFlag()))//@@Modified by Kameswari for the enhancement
                   {
                     if(request.getParameter("destDiscountType"+fr+destIndices[i])!=null)
                     {
                       discountType  =  request.getParameterValues("destDiscountType"+fr+destIndices[i]);
                       discount      =  request.getParameterValues("destDiscount"+fr+destIndices[i]);
                       sellRate      =  request.getParameterValues("destSellRate"+fr+destIndices[i]);
                     
                       if(discount!=null)//@@Added by Kameswari for the WPBN issue-139966logge
                       {
                         chargesDOB.setMarginDiscountFlag("D");
                         int disLen	=	discount.length;
                         for(int k=0;k<disLen;k++)
                       {
                         chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);
                         chargeInfo.setDiscountType(discountType[k]);
                         //Logger.info(FILE_NAME,"chargeInfo...margin type:"+chargeInfo.getMarginType());
                         chargeInfo.setDiscount(Double.parseDouble(discount[k]));
                         chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                         
                         if("P".equalsIgnoreCase(discountType[k]))
                         {
                       	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   & 205785 on 17-May-10             	  
                             if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                      			   ||("Modify".equalsIgnoreCase(request.getParameter("Operation")) && !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag())) ) )
                      	  {
                       		  if(Double.parseDouble(discount[k]) == chargeInfo.getTieMarginDiscountValue()
                       				  || (Double.parseDouble(discount[k]) < chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(discount[k])>= destChargesDiscount))
                      		  {
                      			    chargeInfo.setMarginTestFailed(false);
                      		  }
                      		  else
                      		  {
                          		  if(Double.parseDouble(discount[k]) > destChargesDiscount)
                       			  chargeInfo.setMarginTestFailed(true);
                       		  else
                       			  chargeInfo.setMarginTestFailed(false);

                      		  }
                      	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                       	  else
                       	  {
                       		  if(Double.parseDouble(discount[k]) > destChargesDiscount)
                       			  chargeInfo.setMarginTestFailed(true);
                       		  else
                       			  chargeInfo.setMarginTestFailed(false);
                       	  }
                         }
                         else if("A".equalsIgnoreCase(discountType[k]))
                         {
                       	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10                	  
                             if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                         			   ||("Modify".equalsIgnoreCase(request.getParameter("Operation")) && !"REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag())) ) )
                         	  {
                          		  if(Double.parseDouble(discount[k]) == chargeInfo.getTieMarginDiscountValue() 
                          				  || (Double.parseDouble(discount[k])< chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(discount[k])>=destChargesDiscount ) )
                         		  {
                         			    chargeInfo.setMarginTestFailed(false);
                         		  }
                         		  else
                         		  {
          		                 	  
                           		  if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > destChargesDiscount)
                           			  chargeInfo.setMarginTestFailed(true);
                           		  else
                           			  chargeInfo.setMarginTestFailed(false);

                         		  }
                         	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                      	  else
                       	  {
                       		                 	  
                       		  if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > destChargesDiscount)
                       			  chargeInfo.setMarginTestFailed(true);
                       		  else
                       			  chargeInfo.setMarginTestFailed(false);
                       	  }
                         }
                         destChargeInfo.remove(k);
                         destChargeInfo.add(k,chargeInfo);
                       }
                       }
                     }
                   }
                   chargesDOB.setChargeInfoList(destChargeInfo);
                   destCharges.remove(destIndices[i]);
                   destCharges.add(destIndices[i],chargesDOB);
       			}
                 }
                 legRateDetails.setDestChargesList(destCharges);
                 legRateDetails.setSelctedDestChargesListIndices(destIndices);
                 legRateDetails.setDestChargesSelectedFlag(destChargeSelectedFlag);//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09    
                
               }
               else
               {
            	   legRateDetails.setSelctedDestChargesListIndices(new int[0]);
               }
               originChargeIndices = null;
               destChargeIndices = null;
        }
       //Added by Anil.k
        if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith())){//END
        
  //Margin test for freight rates     
        for(int i=0; i<chargesInfoSize; i++){//@@ Setting the values into charge
        	chargeInfo			=	(MultiQuoteChargeInfo)chargeInfoList.get(i);
        	freightBrekMarginTest = chargeInfo.getFreightBrekMarginTest();
        	multiSelectedFlag	=	request.getParameter("multiSelectedFlag"+i);
        	
        	chargeInfo.setMarginTestFailed(false);
        	if("BR".equalsIgnoreCase(multiSelectedFlag) || "SBR".equalsIgnoreCase(multiSelectedFlag))//Added by Anil.k for Spot Rates
        	{
        		multiMarginTypes	=	request.getParameterValues("marginTypes"+i);
        		multiMarginValues	=	request.getParameterValues("MarginVal"+i);
        	}
        	
        	if("RSR".equalsIgnoreCase(multiSelectedFlag))
        	{
        	multiDiscountTypes	=	request.getParameterValues("discountTypes"+i);
        	multiDiscountValues	=	request.getParameterValues("DiscountVal"+i);
        }
        	multiCalSellRates	=	request.getParameterValues("calsellrate"+i);
        	
        	
        	for(int selected=0;selected<selectedsize; selected++){
        		int selectedIndex  =	Integer.parseInt(selectedFrtindicies[selected]);
        		
        		if("BR".equalsIgnoreCase(multiSelectedFlag) || "SBR".equalsIgnoreCase(multiSelectedFlag))//Added by Anil.k for Spot Rates
            	{	
        			chargeInfo.setMarginDiscountFlag("M");

        		tempArray = chargeInfo.getMultiMarginTypes();//for margin types
        		tempArray[selectedIndex]= multiMarginTypes[selectedIndex];
              	chargeInfo.setMultiMarginTypes(tempArray);
              	
              	tempArray = chargeInfo.getMultiMargins();//for margin values
              	tempArray[selectedIndex]= multiMarginValues[selectedIndex];
              	chargeInfo.setMultiMargins(tempArray);
                 	
            	}
        	   if("RSR".equalsIgnoreCase(multiSelectedFlag))
            	{ 
        		   chargeInfo.setMarginDiscountFlag("D");   
            	tempArray         =  chargeInfo.getMultiMarginTypes()!=null ? chargeInfo.getMultiMarginTypes(): chargeInfo.getMultiDiscountTypes() ;//for discount types
              	tempArray[selectedIndex]= multiDiscountTypes[selectedIndex];
              	chargeInfo.setMultiMarginTypes(tempArray);
              	chargeInfo.setMultiDiscountTypes(tempArray);
                tempArray		  =	chargeInfo.getMultiDiscountMargin();//for discount values;
              	tempArray[selectedIndex]= multiDiscountValues[selectedIndex];
                chargeInfo.setMultiDiscountMargin(tempArray);
            	}
        	   System.out.println("MarginDiscountFlag--------"+chargeInfo.getMarginDiscountFlag());
                tempArray		 =	chargeInfo.getMultiCalSellRates();//for callsellRate values;
              	tempArray[selectedIndex]= multiCalSellRates[selectedIndex];
                chargeInfo.setMultiCalSellRates(tempArray);
                
                if("BR".equalsIgnoreCase(multiSelectedFlag) || "SBR".equalsIgnoreCase(multiSelectedFlag)){//Added by Anil.k for Spot Rates
                	
                	if(Double.parseDouble("-".equals((chargeInfo.getMultiMargins()[selectedIndex]))?"0.0":chargeInfo.getMultiMargins()[selectedIndex]) < legDOB.getMarginLimit()){
                		freightBrekMarginTest[selectedIndex] = true;
                		chargeInfo.setMultiMarginTestFailed(true);
                		chargeInfo.setMarginTestFailed(true);
                	}
                }else if("RSR".equalsIgnoreCase(multiSelectedFlag)){
                	if(Double.parseDouble(("-".equals(chargeInfo.getMultiDiscountMargin()[selectedIndex]))?"0.0":chargeInfo.getMultiDiscountMargin()[selectedIndex]) > legDOB.getDiscountLimit()){
                		freightBrekMarginTest[selectedIndex] = true;
                		chargeInfo.setMultiDiscountTestFailed(true);
                		chargeInfo.setMarginTestFailed(true);
                	}
                }
          }
        	chargeInfo.setFreightBrekMarginTest(freightBrekMarginTest);
        	
        }
    }//Added by Anil.k
    }
    catch(Exception e)
    {
      logger.error(FILE_NAME+"Error in doMarginTest()"+e);
      e.printStackTrace();
    }
    return finalDOB;

}

	
	 /**
	 * This method helps in processing the request from the Summary screen
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doMasterInfo request
	 * @exception ServletException if the request for the doMasterInfo could not be handled.
	 */
  public void doSummary(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String                  operation         = null;
    String                  operate         = null;//@@Added by Kameswari for the WPBN issue-
    
    String                  email             = null;
    String                  fax               = null;
    String                  print             = null;
    
    HttpSession             session           = null;
    
    MultiQuoteFinalDOB           finalDOB          = null;
    MultiQuoteMasterDOB          masterDOB         = null;
    MultiQuoteFlagsDOB           flagsDOB          = null;
    MultiQuoteFlagsDOB           preFlagsDOB       = null;
    MultiQuoteAttachmentDOB      attachmentDOB     = null; 
    QMSMultiQuoteSessionHome     home              = null;
    QMSMultiQuoteSession         remote            = null;
    String[]                contactPersons    = null;
    
    //long                    quoteId;   //@@ Commented by subrahmanyam for the enhancement #146971 on 2/12/08
    String                  quoteId             =null; //@@ Added by subrahmanyam fot the enhancement #146971 on 2/12/08
    int                     mailStatus        = 0;
    String                  terminalId        = null; 
    String                  update           = null;
    String                  quoteType         = null; 
    String                  emailText         = null; 
    String                  countryId         = null;
    String[]                attachmentId      = null;//@@Added by kameswari for the WPBN issue-61289
    StringBuffer            toEmailIds        = new StringBuffer("");
    StringBuffer            toFaxIds          = new StringBuffer("");
    ArrayList               attachmentDOBList  = new ArrayList();
    ArrayList               filesList         = new ArrayList();
    String                  location          = null; //@@ Added by subrahmanyam for the enhancement #146971 on 2/12/08
    boolean					 compareFlag	= false;	
    ArrayList				chargeInfoList	=	new ArrayList(); // Added by Gowtham.
    int 					laneSize		=0;
    MultiQuoteChargeInfo   freightRates = null;
    try
    {
      operation    = request.getParameter("Operation");
       operate   = request.getParameter("operate");
       update    = request.getParameter("update");
      email        = request.getParameter("email");
      fax          = request.getParameter("fax");
      print        = request.getParameter("print");
      attachmentId = request.getParameterValues("attachmentId");//@@Added by kameswari for the WPBN issue-61289
       session     = request.getSession();
      
      //lookUpBean  = new LookUpBean();
      home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
      remote      = home.create();
       ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
       if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))
    	   operation = "Modify";
      if("View".equalsIgnoreCase(operation))
          finalDOB      =   (MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");
      else
          finalDOB      =   (MultiQuoteFinalDOB)session.getAttribute("finalDOB");
   
      masterDOB   = finalDOB.getMasterDOB();
     System.out.println("for load test --QMSMULTIQUOTECONTROLLER-2955"+masterDOB);

       countryId = remote.getCountryId(masterDOB.getCustomerAddressId());
      
        masterDOB.setCountryId(countryId);
          
        if(attachmentId!=null)
       {
        	int attchmentIdLen	= attachmentId.length;
         for(int i=0;i<attchmentIdLen;i++)
         {
           
              attachmentDOB   =   new MultiQuoteAttachmentDOB();
              if(!("".equalsIgnoreCase(attachmentId[i])))
              {
              attachmentDOB = new MultiQuoteAttachmentDOB();
              attachmentDOB.setAttachmentId(attachmentId[i]);
              attachmentDOBList.add(attachmentDOB);
            }
         }
       }
        if(attachmentDOBList.size()>0)
        {
         finalDOB.setAttachmentDOBList(attachmentDOBList); 
        }
     
     
       contactPersons =    masterDOB.getCustContactNames();
       masterDOB.setOperation(operation);
 
      if(contactPersons!=null)
      {
    	  int contactPerLen	=	contactPersons.length;
        for(int i=0;i<contactPerLen;i++)
        {
          if(i==(contactPersons.length-1))
          {
            if(masterDOB.getCustomerContactsEmailIds()!= null && masterDOB.getCustomerContactsEmailIds()[i]!=null && masterDOB.getCustomerContactsEmailIds()[i].trim().length()!=0)
                toEmailIds.append(masterDOB.getCustomerContactsEmailIds()[i]);
            if(masterDOB.getCustomerContactsFax()!= null && masterDOB.getCustomerContactsFax()[i]!=null && masterDOB.getCustomerContactsFax()[i].trim().length()!=0)
                toFaxIds.append(masterDOB.getCustomerContactsFax()[i]);
              
          }
          else
          {
            if(masterDOB.getCustomerContactsEmailIds()!= null && masterDOB.getCustomerContactsEmailIds()[i]!=null && masterDOB.getCustomerContactsEmailIds()[i].trim().length()!=0)
              toEmailIds.append(masterDOB.getCustomerContactsEmailIds()[i]).append(",");
            if(masterDOB.getCustomerContactsFax()!=null && masterDOB.getCustomerContactsFax()[i]!=null && masterDOB.getCustomerContactsFax()[i].trim().length()!=0)
              toFaxIds.append(masterDOB.getCustomerContactsFax()[i]).append(",");
          }
        }
      }
      else
      {
        toEmailIds.append(finalDOB.getHeaderDOB().getCustEmailId());
        toFaxIds.append(finalDOB.getHeaderDOB().getCustFaxNo());
      }
      //@@Added by Kameswari for the WPBN issue-61306
      if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
      {
         toEmailIds.append(",").append(masterDOB.getSalesPersonEmail());
      }
      //@@WPBN isssue-61306
      if("<<Back".equalsIgnoreCase(request.getParameter("submit")))
      {
        //session.setAttribute("finalDOB",finalDOB);
        if("View".equalsIgnoreCase(operation))
        {
            session.setAttribute("viewFinalDOB",finalDOB);
            doDispatcher(request,response,"qms/QMSMultiQuoteChargesView.jsp");
        }
        else
        {
          
            session.setAttribute("finalDOB",finalDOB);
            if("charges".equalsIgnoreCase(finalDOB.getMasterDOB().getQuoteWith()))
    	        request.setAttribute("fromWhere","qms/QMSMultiQuoteMaster.jsp");
    	        else
    	        request.setAttribute("fromWhere","qms/QMSMultiQuoteFreightSellRates.jsp");
            if("true".equalsIgnoreCase(request.getParameter("flag")))
              doDispatcher(request,response,request.getParameter("fromWhere"));
            else
              doDispatcher(request,response,"qms/QMSMultiQuoteChargesSelect.jsp?count=0");
        }
      }
      else
      {      
        if("Add".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation)||("View".equalsIgnoreCase(operation)&&"Update".equalsIgnoreCase(update)&&("on".equalsIgnoreCase(email)||"on".equalsIgnoreCase(fax)
        ||"on".equalsIgnoreCase(print))))
        {
          if("Next>>".equalsIgnoreCase(request.getParameter("submit")))
          {
            flagsDOB = finalDOB.getFlagsDOB();
            flagsDOB.setCompleteFlag("C");
            flagsDOB.setActiveFlag("A");
            
            if(!"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))
              flagsDOB.setQuoteStatusFlag("GEN");//(once the quote is sent, this status is updated to P-Pending)
            
            flagsDOB.setSentFlag("U");//(once the quote is sent, this status is updated to S-Sent)
            
            if("on".equalsIgnoreCase(email))
                flagsDOB.setEmailFlag("Y");
            else
                flagsDOB.setEmailFlag("N");
            if("on".equalsIgnoreCase(fax))
                flagsDOB.setFaxFlag("Y");
            else flagsDOB.setFaxFlag("N");
            if("on".equalsIgnoreCase(print))
                flagsDOB.setPrintFlag("Y");
            else flagsDOB.setPrintFlag("N");
            
            finalDOB.setFlagsDOB(flagsDOB);
            masterDOB.setVersionNo(1);//@@The version for this quote will be 1
            finalDOB.setMasterDOB(masterDOB);   
                  
          finalDOB.setUpdate(update);
            quoteId = remote.insertQuoteMasterDtls(finalDOB);//insert the quote details
             finalDOB.getMasterDOB().setQuoteId(quoteId);
             finalDOB =  remote.getQuoteContentDtl(finalDOB);
            filesList  = remote.getQuoteAttachmentDtls(finalDOB); 
             if(filesList!=null)
            {
             finalDOB.setAttachmentDOBList(filesList);
            }
            request.setAttribute("quoteId",""+quoteId);
            
              //if(quoteId>0 && !"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))  //@@ Commented by subrahmanyam for the Enhancement #146971 on 2/12/08
              if(quoteId!=null && !"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))  //@@ Added by subrahmanyam for the Enhancement #146971 on 2/12/08
              {
                if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) || "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) || "Y".equalsIgnoreCase(flagsDOB.getPrintFlag()))
                {
                   terminalId  = masterDOB.getTerminalId();
                  if(finalDOB.getUpdatedReportDOB()!=null)
                         quoteType  = "U";
                  else
                         quoteType  = "N";
                   emailText = remote.getEmailText(terminalId,quoteType);
                   /*if(finalDOB.getEmailText()!=null&&!("null".equalsIgnoreCase(finalDOB.getEmailText())))
                  finalDOB.setEmailText(emailText);
                  else
                   finalDOB.setEmailText("");*/
                  finalDOB.setEmailText(emailText);
                   
                  mailStatus  = doPDFGeneration(finalDOB,request,response); 
                 // System.out.println("Sent mail is :"+mailStatus+"  Operation :"+operation);                
                  
                  if(mailStatus == 3 || mailStatus == 7)
                  {
                    setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds.toString()+" & Fax has been sent to "+toFaxIds , operation , "QMSMultiQuoteController");
                  }
                  else if(mailStatus == 2  || mailStatus == 6)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax has been sent to "+toFaxIds , operation , "QMSMultiQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & Fax has been sent to "+toFaxIds , operation , "QMSMultiQuoteController");
                  }
                  else if(mailStatus == 1 || mailStatus == 5)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds.toString()+" & Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds.toString(), operation , "QMSMultiQuoteController");
                  }
                  else if(mailStatus == 0  || mailStatus==4)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "N".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                      setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)"), operation , "QMSMultiQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) && "N".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                      setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId, operation , "QMSMultiQuoteController");
                      
                  }
                  session.removeAttribute("finalDOB");
                  session.removeAttribute("PreFlagsDOB");
                  session.setAttribute("isMultiQuote", "MultiQuote");//added by silpa.p on 6-06-11 for redirect costing
                  doDispatcher(request, response, "QMSErrorPage.jsp");              
                }
               else
               {
                setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId, operation , "QMSMultiQuoteController");
                session.removeAttribute("finalDOB");
                session.removeAttribute("PreFlagsDOB");
                session.setAttribute("isMultiQuote", "MultiQuote");//added by silpa.p on 6-06-11 for redirect costing
                doDispatcher(request, response, "QMSErrorPage.jsp");
               }          
            }
            else
            {
              try
              {
              /*String msg  = "Please refer to Quote Reference "+quoteId+" pending your approval. Quote was created by "+masterDOB.getSalesPersonName()+" for "+finalDOB.getHeaderDOB().getCustomerName()+
                              ". Quote must be reviewed within "+finalDOB.getAllottedTime()+" hours.";*/
             logger.info("send mail");
               String msg  = "Please refer to Quote Reference "+quoteId+" pending your approval. Quote was created by "+masterDOB.getCreatedBy()+" for the sales person "+masterDOB.getSalesPersonName()+" for "+finalDOB.getHeaderDOB().getCustomerName()+
                        ". Quote must be reviewed within "+finalDOB.getAllottedTime()+" hours.";
                sendMail(finalDOB.getMasterDOB().getUserEmailId(),finalDOB.getReportingOfficerEmail(),"Quote Pending Your Approval, "+finalDOB.getHeaderDOB().getCustomerName()+", "+quoteId,msg,"",null);
              }
              catch(FoursoftException fs)
              {
                //Logger.error(FILE_NAME,"Error While Sending Mail "+fs.getMessage());
                logger.error(FILE_NAME+"Error While Sending Mail "+fs.getMessage());
              }
              setErrorRequestValues(request, "RSI", "The Quote is generated with Id "+quoteId+" and has been escalated to "+finalDOB.getReportingOfficer(), operation , "QMSMultiQuoteController");
              session.removeAttribute("finalDOB");
              session.removeAttribute("PreFlagsDOB");
              doDispatcher(request, response, "QMSErrorPage.jsp");
            }          
            remote.updateSendMailFlag(quoteId,masterDOB.getUserId(),operation,false,mailStatus);          
          }
        }
        else if("Modify".equalsIgnoreCase(operation))
        {

            if("Next>>".equalsIgnoreCase(request.getParameter("submit"))||"Update".equalsIgnoreCase(request.getParameter("submit"))) // Added by Gowtham
           {
             flagsDOB = finalDOB.getFlagsDOB();
             flagsDOB.setCompleteFlag("C");
             
             flagsDOB.setActiveFlag("A");
             
             if(!"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()) && !"Update".equalsIgnoreCase(request.getParameter("submit")))
               flagsDOB.setQuoteStatusFlag("GEN");//(once the quote is sent, this status is updated to P-Pending)
             else if(!"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()) && "Update".equalsIgnoreCase(request.getParameter("submit")))
                 flagsDOB.setQuoteStatusFlag("APP");
             flagsDOB.setSentFlag("U");//(once the quote is sent, this status is updated to S-Sent)
             
             if("on".equalsIgnoreCase(email))
                 flagsDOB.setEmailFlag("Y");
             else
                 flagsDOB.setEmailFlag("N");
                 
             if("on".equalsIgnoreCase(fax))
                 flagsDOB.setFaxFlag("Y");
             else 
                 flagsDOB.setFaxFlag("N");
                 
             if("on".equalsIgnoreCase(print))
                 flagsDOB.setPrintFlag("Y");
             else 
                 flagsDOB.setPrintFlag("N");
             
             finalDOB.setFlagsDOB(flagsDOB);
             
             preFlagsDOB = (MultiQuoteFlagsDOB)request.getSession().getAttribute("PreFlagsDOB");
             if(preFlagsDOB!=null)
              compareFlag = flagsDOB.equals(preFlagsDOB);//overloaded method.
        
             finalDOB.setCompareFlag(compareFlag);
             finalDOB.setMasterDOB(masterDOB);
              quoteId = remote.modifyQuoteMasterDtls(finalDOB);//modify the quote details
             request.setAttribute("quoteId",""+quoteId);
            // System.out.println("quoteId  :"+quoteId);
          
             finalDOB =  remote.getQuoteContentDtl(finalDOB);
             filesList  = remote.getQuoteAttachmentDtls(finalDOB); 
              finalDOB.setAttachmentDOBList(filesList);
             //if(quoteId>0 && !"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))  //@@ Commented by subrahmanyam for the Enhancement #146971 on 2/12/08
             if(quoteId!=null && !"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))  //@@ Added by subrahmanyam for the Enhancement #146971 on 2/12/08
               {
                 if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) || "Y".equalsIgnoreCase(flagsDOB.getFaxFlag())  || "Y".equalsIgnoreCase(flagsDOB.getPrintFlag()))
                 {
                    terminalId  = masterDOB.getTerminalId();
                    if(finalDOB.getUpdatedReportDOB()!=null)
                          quoteType  = "U";
                    else
                          quoteType  = "N";
                   emailText = remote.getEmailText(terminalId,quoteType);
                   finalDOB.setEmailText(emailText);
                 mailStatus  = doPDFGeneration(finalDOB,request,response);
                  // System.out.println("Sent mail is :"+mailStatus+"  Operation :"+operation);                
                   
                   if(mailStatus == 3 || mailStatus == 7)
                   {
                     setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds+" & Fax has been sent to "+toFaxIds , operation , "QMSMultiQuoteController");
                   }
                   else if(mailStatus == 2 || mailStatus == 6)
                   {
                     if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax has been sent to "+toFaxIds , operation , "QMSMultiQuoteController");
                     else
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & Fax has been sent to "+toFaxIds , operation , "QMSMultiQuoteController");
                   }
                   else if(mailStatus == 1 || mailStatus == 5)
                   {
                     if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds+" but Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
                     else
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds, operation , "QMSMultiQuoteController");
                   }
                   else if(mailStatus == 0 || mailStatus==4)
                   {
                     if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "N".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                       setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)"), operation , "QMSMultiQuoteController");
                     else if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) && "N".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                       setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
                     else if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSMultiQuoteController");
                     else
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId, operation , "QMSMultiQuoteController");
                       
                   }
                   session.removeAttribute("finalDOB");
                   session.removeAttribute("PreFlagsDOB");
                   session.setAttribute("isMultiQuote", "MultiQuote");//added by silpa.p on 6-06-11 for redirect costing
                   doDispatcher(request, response, "QMSErrorPage.jsp");
                 }
                else
                {
                 setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId, operation , "QMSMultiQuoteController");
                 session.removeAttribute("finalDOB");
                 session.removeAttribute("PreFlagsDOB");
                 session.setAttribute("isMultiQuote", "MultiQuote");//added by silpa.p on 6-06-11 for redirect costing
                 doDispatcher(request, response, "QMSErrorPage.jsp");
                }          
            } else
            {
                try
                {
                  /*String msg  = "Please refer to Quote Reference "+quoteId+" pending your approval. Quote was created by "+masterDOB.getSalesPersonName()+" for "+finalDOB.getHeaderDOB().getCustomerName()+
                                ". Quote must be reviewed within "+finalDOB.getAllottedTime()+" hours.";*/
                                
              String msg  = "Please refer to Quote Reference "+quoteId+" pending your approval. Quote was created by "+masterDOB.getCreatedBy()+" for the sales person "+masterDOB.getSalesPersonName()+" for "+finalDOB.getHeaderDOB().getCustomerName()+
              ". Quote must be reviewed within "+finalDOB.getAllottedTime()+" hours.";
               sendMail(finalDOB.getMasterDOB().getUserEmailId(),finalDOB.getReportingOfficerEmail(),"Quote Pending Your Approval, "+finalDOB.getHeaderDOB().getCustomerName()+", "+quoteId,msg,"",null);
                 }
                catch(FoursoftException fs)
                {
                  //Logger.error(FILE_NAME,"Error While Sending Mail "+fs.getMessage());
                  logger.error(FILE_NAME+"Error While Sending Mail "+fs.getMessage());
                }
                setErrorRequestValues(request, "RSI", "The Quote is generated with Id "+quoteId+" and has been escalated to "+finalDOB.getReportingOfficer(), operation , "QMSMultiQuoteController");
                session.removeAttribute("finalDOB");
                session.removeAttribute("PreFlagsDOB");
                session.setAttribute("isMultiQuote", "MultiQuote");//added by silpa.p on 6-06-11 for redirect costing
                doDispatcher(request, response, "QMSErrorPage.jsp");
              }	
        	
          remote.updateSendMailFlag(quoteId,masterDOB.getUserId(),operation,compareFlag,mailStatus);
        	
        	
        }
       /* else if("View".equalsIgnoreCase(operation))
        {
		}*/
        }else if("View".equalsIgnoreCase(operation))
        {
            String actionPage = "";
            
            if("Approved".equalsIgnoreCase(request.getParameter("fromWhere")))
                actionPage    =   "QMSReportController?Operation=Approved"; 
            else
            {
            if("view".equalsIgnoreCase(operate))
                actionPage    =   "QMSMultiQuoteController?from=summary"; 
             else
                actionPage    =   "QMSMultiQuoteController"; 
            }
            
            flagsDOB = finalDOB.getFlagsDOB();
            
            if("on".equalsIgnoreCase(email))
                flagsDOB.setEmailFlag("Y");
            else
                flagsDOB.setEmailFlag("N");
            if("on".equalsIgnoreCase(fax))
                flagsDOB.setFaxFlag("Y");
            else flagsDOB.setFaxFlag("N");
            if("on".equalsIgnoreCase(print))
                flagsDOB.setPrintFlag("Y");
            else flagsDOB.setPrintFlag("N");
            
            finalDOB.setFlagsDOB(flagsDOB);
            
            finalDOB =  remote.getQuoteContentDtl(finalDOB);
            filesList  = remote.getQuoteAttachmentDtls(finalDOB); 
             finalDOB.setAttachmentDOBList(filesList);
            request.setAttribute("quoteId",""+finalDOB.getMasterDOB().getQuoteId());
            
            if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) || "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) || "Y".equalsIgnoreCase(flagsDOB.getPrintFlag()))
            {
              terminalId  = masterDOB.getTerminalId();
              if(finalDOB.getUpdatedReportDOB()!=null)
                     quoteType  = "U";
              else
                     quoteType  = "N";
              emailText = remote.getEmailText(terminalId,quoteType);
              finalDOB.setEmailText(emailText);
              
              mailStatus  = doPDFGeneration(finalDOB,request,response);
              //System.out.println("Sent mail is :"+mailStatus+"  Operation :"+operation);                
              
              if(mailStatus == 3 || mailStatus == 7)
              {
                setErrorRequestValues(request, "MSG", "The Quote "+masterDOB.getQuoteId()+" has been successfully sent via eMail to :"+toEmailIds.toString()+" & via Fax to "+toFaxIds , operation , actionPage);
              }
              else if(mailStatus == 2  || mailStatus == 6)
              {
                if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                 setErrorRequestValues(request, "MSG", "The Quote "+masterDOB.getQuoteId()+" has been successfully sent via eMail to :"+toEmailIds.toString()+" & via Fax to "+toFaxIds , operation , actionPage);
                else
                 setErrorRequestValues(request, "MSG", "The Quote "+masterDOB.getQuoteId()+" has been successfully sent via Fax to "+toFaxIds , operation , actionPage);
              }
              else if(mailStatus == 1 || mailStatus == 5)
              {
                if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                 setErrorRequestValues(request, "MSG", "The Quote "+masterDOB.getQuoteId()+" has been successfully sent via E-Mail to :"+toEmailIds.toString()+" but Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , actionPage);
                else
                 setErrorRequestValues(request, "MSG", "The Quote "+masterDOB.getQuoteId()+" has been successfully sent via E-Mail to :"+toEmailIds.toString(), operation , actionPage);
              }
              else if(mailStatus == 0  || mailStatus==4)
              {
                if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "N".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                  setErrorRequestValues(request, "ERR", "The E-Mail of the Quote "+masterDOB.getQuoteId()+" was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)"), operation ,actionPage);
                else if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) && "N".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                  setErrorRequestValues(request, "ERR", "The Fax of the Quote "+masterDOB.getQuoteId()+" was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , actionPage);
                else if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                 setErrorRequestValues(request, "ERR", "The E-Mail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , actionPage);
                else
                 setErrorRequestValues(request, "MSG", "The PDF was successfully generated for Quote Id "+masterDOB.getQuoteId()+".", operation , actionPage);
              }
              session.removeAttribute("viewFinalDOB");
              session.removeAttribute("PreFlagsDOB");
              doDispatcher(request, response, "QMSErrorPage.jsp");              
            }
           else
           {
            session.removeAttribute("viewFinalDOB");
            session.removeAttribute("PreFlagsDOB");
            
            if("Approved".equalsIgnoreCase(request.getParameter("fromWhere")))
                doDispatcher(request, response, "QMSReportController?Operation=Approved&subOperation="+null);
            else
                doDispatcher(request, response, "qms/QMSMultiQuoteEnterId.jsp?Operation=View");
           }
           //@@ WPBN Issue Ids - 26515 & 40212
           if("GEN".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()) || "APP".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()))
              remote.updateSendMailFlag(finalDOB.getMasterDOB().getQuoteId(),masterDOB.getUserId(),operation,false,mailStatus);
           //@@Yuvraj              
        }
      }
    }
    catch(Exception ex)
		{
      session.removeAttribute("finalDOB");
      session.removeAttribute("PreFlagsDOB");
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [error in doSellCharges()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [error in doSummary()] -> "+ex.toString());
      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSMultiQuoteController?Operation="+operation);
      doDispatcher(request, response, "ESupplyErrorPage.jsp");
		}
    
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

//@@ Added by subrahmanyam for the pbn id: 194328
public String[] removeEnterForNotes(String[] notes)
{
	  String senter = new String();
	  String[] s3 =null;
	  ArrayList noteFinal = new ArrayList();
	  String[] notesArray=null;
	  int notesLen	=	notes!=null?notes.length:0;
  if(notes!=null && notes.length>0)
  {
		for(int i=0;i<notesLen; i++)
		{
			 s3 = notes[i].split("\\n");
			 int s3Len	=	s3.length;
			 char[] enterOmitt = null;
			 if(s3.length>1)
			{
				 senter="";
				 for(int j=0;j<s3Len;j++){
					 String enterOmittedString="";
					 enterOmitt = s3[j].toCharArray();
					 	for (int c=0; c<enterOmitt.length-1;c++)
					 		enterOmittedString+=enterOmitt[c];
					  senter+=enterOmittedString+" ";
				 }
				 noteFinal.add(senter);
			}
			else
			{
				for(int j=0;j<s3Len; j++)
					noteFinal.add(s3[j]);
			}
		}
  }
  	notesArray = new String[noteFinal!=null ? noteFinal.size():0];
  	int noteFinalSize	=	noteFinal!=null?noteFinal.size():0;
  		for(int x=0; x<noteFinalSize;x++)
  			notesArray[x] = (String)noteFinal.get(x);
  
  return notesArray ;
}
//ended for removeEnterForNotes() for PBN ID: 194328
  

public void sendMail(String frmAddress, String toAddress,String subject, String message, String attachments,ArrayList list) throws FoursoftException
{
//	System.out.println("sendMail in CustomizedReportBean called.. attchment :: "+attachment);
 ArrayList          fileList   = new ArrayList();

	try 
{   //attachment = "c:/"+attachment;

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
   
  if(attachments!=null && attachments.trim().length()!=0)
{
  String attachs[] = attachments.split(",");          
  
  if(attachs!=null && attachs.length>0)
  {
     //Multipart multipart = new MimeMultipart();
    int attachLen	=	attachs.length;
     for(int i=0;i<attachLen;i++)
     {
       BodyPart mbp2 = new MimeBodyPart();
       DataSource fds = new FileDataSource(attachs[i]);     
       mbp2.setDataHandler(new DataHandler(fds));
       mbp2.setFileName(fds.getName());
       mp.addBodyPart(mbp2);
     
    }
  }
}
 //@@For attachments from the master
  if(list!=null)
{
  MultiQuoteAttachmentDOB dob        = null;//Modified by Anil.k
  File               file       = null;
  FileOutputStream   fileStream = null;
  int listSize	=	list.size();
   for(int i=0;i<listSize;i++)
  {
    dob = (MultiQuoteAttachmentDOB)list.get(i);//Modified by Anil.k
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
//Logger.error(FILE_NAME,"Message Exception in send Mails ... "+me.toString());
logger.error(FILE_NAME+"Message Exception in send Mails ... "+me.toString());
me.printStackTrace();
throw new FoursoftException(me.toString());
} 
catch(Exception e)
 {
 //Logger.error(FILE_NAME,"Message Exception in send Mails ... "+e.toString());
 logger.error(FILE_NAME+"Message Exception in send Mails ... "+e.toString());
 e.printStackTrace();
   throw new FoursoftException(e.toString());
}
finally
{
 int fileListSize	= fileList.size();
for(int i=0;i<fileListSize;i++)
{
  ((File)fileList.get(i)).delete();
}
}
}


private void doModifyProcess(HttpServletRequest request,HttpServletResponse response) throws ServletException
{


	  QMSMultiQuoteSessionHome     home              	= null;
	  QMSMultiQuoteSession         remote            	= null;
	  ESupplyGlobalParameters      loginbean         	= null;
	  MultiQuoteFinalDOB           finalDOB          	= null;
	  MultiQuoteMasterDOB          masterDOB         	= null;
	  String                       quoteId         		= "";
	  String                       customerId      		= "";
	  String[]                     origin            	= null;
	  String[]                     destination       	= null;
	  String                       operation       		= null;
	  int                          shipmentMode    		= 0;
	  HttpSession                  session     			= request.getSession();
	  Hashtable				       accessList		    = null;
	  String					   buyRatesPermission	= "Y";
	  String					   accessLevel       	= null;
	  String                       customerName      	= "";
	  String                       quoteStatus       	= "";
	  String                       quoteActive       	= "";
  try
  {
    quoteId     = request.getParameter("QuoteId").trim();//Added by Rakesh on 25-02-2011 for Issue:236363 
    operation   = request.getParameter("Operation");
    
    accessList  =  (Hashtable)session.getAttribute("accessList");

    if(accessList.get("10605")==null)
        buyRatesPermission	= "N";
    
    masterDOB   = new MultiQuoteMasterDOB();
    loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
    //lookUpBean  = new LookUpBean();
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = home.create();
    
    if("HO_TERMINAL".equals(loginbean.getAccessType()))
        accessLevel = "H";
    else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
        accessLevel = "A";
    else
        accessLevel = "O";
   
    if(request.getParameterValues("originLoc")!=null && request.getParameterValues("originLoc").length>0)
      origin = request.getParameterValues("originLoc");
    if(request.getParameterValues("destLoc")!=null && request.getParameterValues("destLoc").length>0)
      destination = request.getParameterValues("destLoc");
    if(request.getParameter("customerId")!=null && request.getParameter("customerId").trim().length()>0)
      customerId = request.getParameter("customerId");
  
    if(request.getParameter("shipmentMode")!=null && request.getParameter("shipmentMode").trim().length()>0)
    {
    if("AIR".equalsIgnoreCase(request.getParameter("shipmentMode")))
       shipmentMode =  1;
    else if("SEA".equalsIgnoreCase(request.getParameter("shipmentMode")))
       shipmentMode =  2;
    else if("TRUCK".equalsIgnoreCase(request.getParameter("shipmentMode")))
        shipmentMode =  4;
    }
      if(request.getParameter("CustomerName")!=null && request.getParameter("CustomerName").trim().length()>0)
           customerName = request.getParameter("CustomerName");
     if(request.getParameter("Status")!=null && request.getParameter("Status").trim().length()>0)
           quoteStatus = request.getParameter("Status");
           if(request.getParameter("ActiveFlag")!=null && request.getParameter("ActiveFlag").trim().length()>0)
           quoteActive = request.getParameter("ActiveFlag");

  if(quoteActive!=null &&  quoteActive.trim().length()>0)
  {
  if("Active".equalsIgnoreCase(quoteActive) || "ACTIVE".equalsIgnoreCase(quoteActive) || "A".equalsIgnoreCase(quoteActive))
  {
  quoteActive="A";
  }
  else if("InActive".equalsIgnoreCase(quoteActive) || "INACTIVE".equalsIgnoreCase(quoteActive) || "I".equalsIgnoreCase(quoteActive))
  {
  quoteActive="I";
  }
   }
  if(quoteStatus!=null && quoteStatus.trim().length()>0)
  {
if("Pending".equalsIgnoreCase(quoteStatus) || "PENDING".equalsIgnoreCase(quoteStatus) || "P".equalsIgnoreCase(quoteStatus))
 {
  quoteStatus="PEN";
 }
else if("Positive".equalsIgnoreCase(quoteStatus) ||"POSITIVE".equalsIgnoreCase(quoteStatus) || "POS".equalsIgnoreCase(quoteStatus))
	{
quoteStatus="ACC";
}
else if("Rejected".equalsIgnoreCase(quoteStatus) || "REJECTED".equalsIgnoreCase(quoteStatus) || "R".equalsIgnoreCase(quoteStatus))
	{
quoteStatus="REJ";
}
else if("Generated".equalsIgnoreCase(quoteStatus) || "GENERATED".equalsIgnoreCase(quoteStatus) || "G".equalsIgnoreCase(quoteStatus))
	{
quoteStatus="GEN";                        
}
else if("Approved".equalsIgnoreCase(quoteStatus) || "APPROVED".equalsIgnoreCase(quoteStatus) || "A".equalsIgnoreCase(quoteStatus))
  {
   quoteStatus="APP";
  }
else if("Queued".equalsIgnoreCase(quoteStatus) || "QUEUED".equalsIgnoreCase(quoteStatus) || "Q".equalsIgnoreCase(quoteStatus))
	{
quoteStatus="QUE";
}
else if("Negative".equalsIgnoreCase(quoteStatus) || "NEGATIVE".equalsIgnoreCase(quoteStatus) || "N".equalsIgnoreCase(quoteStatus))
	{
quoteStatus="NAC";
}
  }

   masterDOB.setOriginLocation(origin); 
   masterDOB.setDestLocation(destination);
   masterDOB.setCustomerId(customerId);
   masterDOB.setShipmentMode(shipmentMode);
   masterDOB.setCompanyName(customerName);
   masterDOB.setQuoteStatus(quoteStatus);
   masterDOB.setActiveFlag(quoteActive);
   if(quoteId!=null && quoteId.trim().length()!=0)
   masterDOB.setQuoteId(quoteId);  //@@ Added by subrahmanyam for the enhancement #146971 on 2/12/08
   masterDOB.setUserId(loginbean.getEmpId());
   masterDOB.setBuyRatesPermission(buyRatesPermission);
	 masterDOB.setOperation(operation); 
   masterDOB.setAccessLevel(accessLevel);
   masterDOB.setTerminalId(loginbean.getTerminalId());
      
    StringBuffer errors = remote.validateQuoteId(masterDOB);
    
    if(errors!=null && errors.length()>0)
    { 
       request.setAttribute("errors",errors.toString());
       String originValue = "";
       for(String s:origin)
    	   originValue = s+",";
       originValue = originValue.substring(0, originValue.length()-1);
       
       String destValue = "";
       for(String s:destination)
    	   destValue = s+",";
       destValue = destValue.substring(0, destValue.length()-1);
       
        doDispatcher(request,response,"qms/QMSMultiQuoteEnterId.jsp?operation=Modify&origin="+originValue+"&destination="+destValue+"&customerId="+customerId+"&quoteId="+quoteId+"&quoteStatus="+quoteStatus+"&customerName="+customerName+"&quoteActive="+quoteActive);
    }
    else
    {         
       finalDOB    = remote.getMasterInfo(quoteId,loginbean);
     //@@Added by kiran.v on 18/11/2011 for Wpbn Issue-280269
       if("Copy".equalsIgnoreCase(operation))
       {
    	   finalDOB.getMasterDOB().setCustTime(null);
    	   finalDOB.getMasterDOB().setCustDate(null);
       }
       //@@Ended by kiran.v
      /* if("Copy".equalsIgnoreCase(operation))
       {
         finalDOB.setLegDetails(null);
         finalDOB.setMultiModalQuote(false);
         finalDOB.getMasterDOB().setRouteId(null);
       }*/
       session.setAttribute("PreFlagsDOB",finalDOB.getFlagsDOB());
       if("View".equalsIgnoreCase(operation))
       {
         session.setAttribute("viewFinalDOB",finalDOB);
         doDispatcher(request,response,"qms/QMSMultiQuoteMasterView.jsp");
       }
       else
       {
        session.setAttribute("finalDOB",finalDOB);
        doDispatcher(request,response,"qms/QMSMultiQuoteMaster.jsp");
       }
    }
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in Controller in doModifyProcess");
    logger.error(FILE_NAME+"Error in Controller in doModifyProcess");
    e.printStackTrace();
  
    throw new ServletException();
  }



}




 private int doPDFGeneration(MultiQuoteFinalDOB finalDOB,HttpServletRequest request,HttpServletResponse response)throws Exception
  {
	    int                mailFlag        = 0;
	    int                faxFlag         = 0;
	    int                printFlag       = 0;
	    int                returnFlag      = 3;
	  //@@Modified by kiran.v on 01/08/2011 for Wpbn Issue 271485
	    ArrayList Sd=null;
	    ArrayList Cd=null;
	    ArrayList Cdn=null;
	    boolean frequencyFlag=false;
	    boolean carrierFlag=false;
	    boolean serviceFlag=false;
	    Map<String, String> surChargesMap = null;
	    //Kiran ends
	    String             transitTime     = null;
	    String[]           contents        = null;
	    String[]           levels          = null;
	    String[]           aligns          = null;
	    String[]           headFoot        = null;
	    String             charge_desc     = null;//@@Added by govind for the issue 258189
	     //@@Added by Kameswari for the WPBN issue-61289
	    ArrayList          dobList         = new ArrayList();
	    MultiQuoteAttachmentDOB attachmentDOB   = null;
	    //@@Added by Kameswari for the WPBN issue-146448
	    ArrayList          charges         = null;
	     int               chargesSize     = 0;
	     int               noOfLanes     = 0;
	    MultiQuoteCharges       chargesDOB			 = null;
	    MultiQuoteChargeInfo       multiQuoteChargeInfo = null;
	    ArrayList          freightCharges  = null;
	    MultiQuoteFreightLegSellRates legCharges   = null;
	    MultiQuoteFreightLegSellRates legOrginCharges   = null;
	    MultiQuoteFreightLegSellRates legDestCharges    = null;
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
	 
	     int 	      gTemp  		   = 0; // Added By Gowtham For PDF View Issue
	     String 		placeDesc		= null; // Added By Gowtham For PDF View Issue
	     String 			tmpOrgPort	 = ""; // Added by Gowtham
	     int 		  gTemp1			=0;
	     String 		tmpfrq ="";
	     String    	tmpServl="";
	     String 		tmpCarrier="";
	     String 				tmpDestPort="";
	     int  LegSize         = 0;
	     HashSet    PortAbbSet          = null;
	    //@@WPBN issue-146448
	    ArrayList          filesList       = new ArrayList();
	    File               file            = null;
	    byte[]             buffer          = null;
	    ArrayList             bufferList    = new ArrayList();
	     ArrayList         pdfFilesList    = new ArrayList();
	    HttpSession        session         = request.getSession();
	    // Added by kiran.v on 16/09/2011
	    String operation = finalDOB.getOperation()!= null?finalDOB.getOperation():finalDOB.getMasterDOB().getOperation();
	    
	PdfWriter writer                       =null;        //Method: doPDFGeneration Defect: PdfWriter is not closedSuggestion: Close and nullify the writer in the finally block.
//	@@ Added by subrahmanyam for the WPBN ISSUE: 146460 on 29/01/2009    
	    QMSMultiQuoteSessionHome     home              = null;
	    QMSMultiQuoteSession         remote            = null;
	    int incoSize	=	0; // Added by Gowtham on 24Feb2011 for IncoTerms display in PDF
//	@@ Ended by subrahmanyam for the WPBN ISSUE: 146460 on 29/01/2009   

	      //@@ WPBN issue-61289
	    PdfPTable pTable;
	    PdfPCell  pCell;
	 
	
	   try
	      {       
		   Sd=new ArrayList();
		   Cd=new ArrayList();
		   Cdn=new ArrayList(); 
		   String carrierChecked= request.getParameter("selectCarrier");
           String serviceLevelChecked= request.getParameter("selectService");
           String frequencyChecked=request.getParameter("selectFrequecy");
           String transitTimeChecked=request.getParameter("selectTransitTime");
           String validityChecked=request.getParameter("selectFrieghtValidity");
           boolean carrierflag=false;
           boolean serviceflag=false;
           boolean frequencyflag=false;
           //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
           boolean transittimeflag=false;
           boolean freightValidity=false;
	            DecimalFormat df  = new DecimalFormat("###,###,###,##0.00");
	            MultiQuoteHeader    headerDOB		          =	  finalDOB.getHeaderDOB();
	            MultiQuoteMasterDOB masterDOB              =   finalDOB.getMasterDOB();
	             ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
	            ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");

	            eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
	            //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
	           //@@ Commented & Added by subrahmanyam for the Effective pbn Issue 212006 on # 26-Jul-10
	            /* String[] strDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
	            String[] effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
	            */
	            String[] strDate	= null;
	            String[] effDate	= null;
	            if("View".equalsIgnoreCase(request.getParameter("Operation")))
	            {
	                if("PDF".equalsIgnoreCase(request.getParameter("pdf")))
	                {
	                	strDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getModifiedDate());
	                	effDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getEffDate());
	                }
	                else{
	                	strDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getModifiedDate());
	                	//effDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getCreatedDate());
	                	//@@Modified by kiran.v on 28/07/2011 for Wpbn Issue -256087
	                	effDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getEffDate());
	                }
	                
	            }
	            else{
	            	 strDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
	                 effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());

	            }
	            	
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
	            charges				  = finalDOB.getLegDetails();

			        chargesSize			  = charges.size();
	           // System.out.println("Before Document Objec--------------------------->");
	            Document document = new Document(PageSize.A4,54f,54f,68.4f,68.4f);//@@ 36 points represent 0.5 inch
	            if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
	            	document.setPageSize(PageSize.A4.rotate());
	            String PDF_FILE_NAME = "Approved.pdf";
	            document.addTitle("Approved Report");
	            document.addSubject("Report PDF");
	            document.addKeywords("Test, Key Words");
	            document.addAuthor("QuoteShop");
	            document.addCreator("QuoteShop");
	            document.addCreationDate();
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ByteArrayOutputStream baosFile = new ByteArrayOutputStream();
	          writer	=	PdfWriter.getInstance(document, baos); 
	           // int b = writer.getPageNumber();
	            baos.close();
	        
	            document.open();
	            
	            //jyothi
	            PdfContentByte cb = writer.getDirectContent();
	            cb.rectangle (document.left (), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
	          
	           cb.stroke ();
	           //jyothi
	           // Graphic horizontalLine = new Graphic();
	            //horizontalLine.setHorizontalLine(1f, 100f);
	           // horizontalLine.setColorStroke(Color.BLACK);
	            //horizontalLine.setLineWidth(100);
	            
	           
	            
	           // PdfFileStamp fileStamp = new PdfFileStamp("Approved.pdf");
	            
	            //PdfPageEventHelper helper
	            
	            //writer.setPageEvent(new PdfPageEventHelper());
	            
	          //document.setMargins(15,15,15,15);            
	            // Draw a rectangle inside the page's margins.
	            //PdfContentByte cb = writer.getDirectContent();
	            //cb.rectangle (document.left (), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
	            //cb.stroke ();
	            int[] widths = {12,12,12,12,12,12,28};
	            /*Table mainT = new Table(2);
	            mainT.setWidth(80);
	            //mainT.setWidths(widths);
	            mainT.setBorderColor(Color.white);
	            mainT.setPadding(1);
	            mainT.setSpacing(0);*/
	            
	            int[] width = {4,1};
	            Table mainT = new Table(2,2);
	            mainT.setWidth(100);
	            mainT.setWidths(width);
	            mainT.setBorderColor(Color.white);
	            mainT.setPadding(3);
	            mainT.setSpacing(0);
	            

	                         
	            Phrase  headingPhrase    =  new Phrase("",FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
	            Cell cellHeading = new Cell(headingPhrase);
	            cellHeading.setBorderColor(new Color(255,255, 255));
	            cellHeading.setHorizontalAlignment(cellHeading.ALIGN_CENTER);
	            cellHeading.setBorderWidth(0);
	           // cellHeading.setColspan(6);
	            mainT.addCell(cellHeading);
	            
	            Cell imageCell = new Cell();            
	            java.net.URL url =  getServletConfig().getServletContext().getResource("/images/DHLlogo.gif");
	            Image img0 = Image.getInstance(url);
	            img0.setAlignment(Image.ALIGN_RIGHT);
	           // img0.scaleToFit(6.0f, 1.9f);
	            //System.out.println("Lower Left:	"" Upper	Left:	"+img0.getRight()+"	"+);
	            
	           // imageCell.setWidth("6");
	            
	           // imageCell.setColspan(2);
	            imageCell.setHorizontalAlignment(imageCell.ALIGN_LEFT);
	            imageCell.add(img0);
	            imageCell.setBorderWidth(0);
	            imageCell.setNoWrap(true);
	            System.out.println(imageCell.cellWidth());
	            mainT.addCell(imageCell);
	            mainT.setAlignment(mainT.ALIGN_CENTER);
	            
	            document.add(mainT);
	          
	             pTable = new PdfPTable(1);
	              pTable.setSpacingAfter(10f);
	              pCell	 = new PdfPCell(new Phrase(new Chunk("")));
	              pCell.setBorder(0);
	              pTable.addCell(pCell);
	              
	              document.add(pTable); 
	           // System.out.println("After Image && Before Content--------------------------->");
	                                   
	            Table partCountry = new Table(4,5); 
	            
	            Table partCountry1 = new Table(4);
	            int[] widths1 = {30,20,30,20};
	            //partCountry1.setBorderWidth(0);
	            partCountry1.setBorderWidth(1f);//modified by silpa.p on 3-06-11
	            partCountry1.setWidths(widths1);
	            partCountry1.setWidth(100);
	            partCountry1.setBorderColor(Color.black);            
	            partCountry1.setPadding(1);
	            partCountry1.setSpacing(0);
	            partCountry1.setBorder(1);//added by silpa.p on 3-06-11
	            partCountry1.setAutoFillEmptyCells(true);
	            //partCountry.setTableFitsPage(true);
	            partCountry.setAlignment(partCountry.ALIGN_CENTER);
	            partCountry.setBorderWidth(0);;
	            
	            int[] widths2 = {20,30,20,30};
	            partCountry.setBorderWidth(0);
	            partCountry.setBorderWidth(1f);//modified by silpa.p on 3-06-11
	            partCountry.setWidths(widths2);
	            partCountry.setWidth(100);
	            partCountry.setBorderColor(Color.black);            
	            partCountry.setPadding(1);
	            partCountry.setBorder(1);//added by silpa.p on 3-06-11
	            partCountry.setSpacing(0);
	            partCountry.setAutoFillEmptyCells(true);
	            //partCountry.setTableFitsPage(true);
	            partCountry.setAlignment(partCountry.ALIGN_CENTER);
	            //Jyothi
	            partCountry.setBorderWidthBottom(1);
	            partCountry.setBorderWidthTop(0);
	            partCountry.setBorderWidthLeft(1);
	            partCountry.setBorderWidthRight(1);
	            //Jyothi
	            partCountry.setBorderWidth(0);;
	            Cell cellCountry;
	            
	            String shipmentMode = "";
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
	            
	            Chunk chk = new Chunk(shipmentMode,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
	              cellCountry = new Cell(chk);
	            cellCountry.setWidth("100");
	            //cellCountry.setColspan(1);
	            cellCountry.setBorderWidth(0);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
	            partCountry1.addCell(cellCountry);
	            
	            chk = new Chunk("");
	            cellCountry = new Cell(chk);
	            cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	            cellCountry.setWidth("100");
	            //cellCountry.setColspan(1);
	            cellCountry.setBorderWidth(0);
	            partCountry1.addCell(cellCountry);

	             chk = new Chunk("SERVICE INFORMATION",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
	              cellCountry = new Cell(chk);
	            cellCountry.setWidth("100");
	            //cellCountry.setColspan(2);
	            cellCountry.setBorderWidth(0);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
	            partCountry1.addCell(cellCountry);
	            
	              chk = new Chunk("");
	              cellCountry = new Cell(chk);
	            cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	            cellCountry.setWidth("100");
	            //cellCountry.setColspan(1);
	            cellCountry.setBorderWidth(0);
	            partCountry1.addCell(cellCountry);
	            

	            document.add(partCountry1);

	              chk = new Chunk("Customer Name: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("30");cellCountry.setBorderWidth(0);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk(headerDOB.getCustomerName()!= null ? toTitleCase(headerDOB.getCustomerName()) :"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              

	              chk = new Chunk("Agent: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("30");cellCountry.setBorderWidth(0);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk(headerDOB.getAgent()!=null?headerDOB.getAgent():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");
	              cellCountry.setBorderWidth(0);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              

	              chk = new Chunk("Attention To: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk(toTitleCase(attentionTo.toString()),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);

	              chk = new Chunk("Commodity Or Product: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              //chk = new Chunk(headerDOB.getCommodity()!=null?toTitleCase(headerDOB.getCommodity()):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//commented by silpa.p on 21-06-11
	              chk = new Chunk(headerDOB.getCommodity()!=null?(headerDOB.getCommodity()):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//added by silpa.p on 21-06-11
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	            

	              chk = new Chunk("Quote Reference: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
                      cellCountry.setBackgroundColor(Color.LIGHT_GRAY);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk(masterDOB.getQuoteId()!=null ?masterDOB.getQuoteId():"" ,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk("Notes: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//modified by silpa.p on 13-06-11
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk(headerDOB.getNotes()!=null?toTitleCase(headerDOB.getNotes()):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);

	              chk = new Chunk("Date Of Quotation: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk(strDate[0],FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk("Date Effective: ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk((effDate[0]!=null?effDate[0]:""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk("Sales Person : ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk((headerDOB.getPreparedBy()!=null?toTitleCase(headerDOB.getPreparedBy()):""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setLeading(10.0f);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk("Validity Of Quote: " ,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk((validUptoStr!=null?validUptoStr:"VALID UNTIL FURTHER NOTICE"),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setHeader(true);
	              cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setLeading(10.0f);
	              cellCountry.setBackgroundColor(Color.LIGHT_GRAY);//modified by silpa.p on 3-06-11
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	              partCountry.addCell(cellCountry);
	              
	              document.add(partCountry); 
	              
	              pTable = new PdfPTable(1);
	              pTable.setSpacingAfter(10f);
	              pCell	 = new PdfPCell(new Phrase(new Chunk("")));
	              pCell.setBorder(0);
	              pTable.addCell(pCell);
	              
	              document.add(pTable); 
	                     
	              
	              
	              
	              
	              
	              
	              
	              
	           /* if(chargesSize==1)
	            {
	            if("MY".equalsIgnoreCase(masterDOB.getCountryId()))
	            {
	            chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry()[0].toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry()[0].toUpperCase():""),FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.BLUE));
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
	            }
	            else
	            {
	              chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry()[0].toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry()[0].toUpperCase():""),FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.RED));
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
	            }
	            }else{
		            if("MY".equalsIgnoreCase(masterDOB.getCountryId()))
		            {
		            //chk = new Chunk("Multi-Lane/Multi-Carrier",FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.BLUE)); // Commented by Gowtham on 24Feb2011
		            chk = new Chunk("Multiple Origins And/Or Destinations",FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.BLUE));
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
		            }
		            else
		            {
		              //chk = new Chunk("Multi-Lane/Multi-Carrier",FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.RED));
		              chk = new Chunk("Multiple Origins And/Or Destinations",FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.RED)); // Commented by Gowtham on 24Feb2011
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
		            }
	            }
	            cellCountry = new Cell("");
	            cellCountry.setBorderWidth(0);
	            cellCountry.setLeading(5.0f);
	            partCountry.addCell(cellCountry);
	            
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
	            partCountry.addCell(cellCountry); */         
	            
	           
	            
	            contents = masterDOB.getContentOnQuote();
	            levels   = masterDOB.getLevels();
	            aligns   = masterDOB.getAlign();
	            headFoot = masterDOB.getHeaderFooter();
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
	              int headFootLen	= headFoot.length;
	              for(int i=0;i<headFootLen;i++)
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
	              /*content = null;
	              cellContent =  null;*/
	            }
	            /*content = new Table(1);
	            Cell cellContent1 = new Cell(new Chunk(""));
	            content.addCell(cellContent1); */
	            	
	            	
	            	
	            	
	            
	            
	            if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
	            {
	            //@@Added by Kameswari for the WPBN issue-146448 on 03/12/08
	        for(int i=0;i<chargesSize;i++)
	        {
	          legCharges	   = (MultiQuoteFreightLegSellRates)charges.get(0);
	          freightCharges = legCharges.getFreightChargesList();
	          int tempCount = freightCharges.size();
	          for(int j=0;j<tempCount;j++)
		        {
	          multiQuoteChargeInfo			= (MultiQuoteChargeInfo)freightCharges.get(i);//govind
	          
	          if(multiQuoteChargeInfo.getSelectedLaneNum()==i){
	          if(multiQuoteChargeInfo != null && multiQuoteChargeInfo.getValidUpto()!=null  )
	          {
	            str1					  =eSupplyDateUtility.getDisplayStringArray(multiQuoteChargeInfo.getValidUpto());
	          }
	            ///////////////////////////////////////////Second Table////////////////////////////
	            if(chargesSize>1)
	           {
	               if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked()))
	               {
	            	   //Added by kiran.v on 16/09/2011
	            	  frequencyFlag=true;
	                  frequency.add(multiQuoteChargeInfo.getFrequency()); 
	                  frequency_o.add(legCharges.getOrigin());
	                  frequency_d.add(legCharges.getDestination());
	               }
	                if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked()))
	               {
	                  transittime.add(multiQuoteChargeInfo.getTransitTime()); 
	                  transit_o.add(legCharges.getOrigin());
	                  transit_d.add(legCharges.getDestination());
	               }
	                if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked()))
	               {
	                	carrierFlag=true;
	                  carrier.add(multiQuoteChargeInfo.getCarrier()); 
	                  carrier_o.add(legCharges.getOrigin());
	                  carrier_d.add(legCharges.getDestination());
	               }
	                if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked()))
	               {
	                 if(multiQuoteChargeInfo.getValidUpto()!=null)
	                {
	                  str1					  =eSupplyDateUtility.getDisplayStringArray(multiQuoteChargeInfo.getValidUpto());
	                  ratevalidity.add(str1[0]); 
	                }
	                
	                  validity_o.add(legCharges.getOrigin());
	                  validity_d.add(legCharges.getDestination());
	               }
	           }    
	           else
	           {
	         
	               if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked()))
	               {
	                  frequency.add(multiQuoteChargeInfo.getFrequency()); 
	                }
	                if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked()))
	               {
	                  transittime.add(multiQuoteChargeInfo.getTransitTime()); 
	                }
	                if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked()))
	               {
	                  carrier.add(multiQuoteChargeInfo.getCarrier()); 
	                 }
	                if("Y".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked()))
	               {
	                 if(multiQuoteChargeInfo.getValidUpto()!=null)
	                {
	                  str1					  =eSupplyDateUtility.getDisplayStringArray(multiQuoteChargeInfo.getValidUpto());
	                   ratevalidity.add(str1[0]); 
	                  }
	                
	               }
	           }
		        }
	           }
	        }
	        }  // partCountry  =  new Table(2,13);
	            size   =  frequency.size()+transittime.size()+carrier.size()+ratevalidity.size();
	           /* Table prepareTable = new Table(1) ;
	            prepareTable.setWidth(100);
	            prepareTable.setBackgroundColor(Color.white);
	            prepareTable.setBorderColor(Color.white);
	            prepareTable.setPadding(1);
	            chk = new Chunk("Prepared By: "+(headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy().toUpperCase():""),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
	            cellCountry = new Cell(chk);
	           // cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setBorder(0);
	            cellCountry.setNoWrap(true);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_LEFT);
	            cellCountry.setBorder(0);
	            cellCountry.setLeading(8.0f);
	            prepareTable.addCell(cellCountry);
	          
	            document.add(prepareTable); */
	            
	            
	          /*  partCountry  =  new Table(2,13+size);
	            
	             //@@WPBN issue-146448 on 03/12/08
	            partCountry.setOffset(5);
	            partCountry.setWidth(100);
	            partCountry.setPadding(1);
	            partCountry.setSpacing(0);
	            partCountry.setBackgroundColor(Color.WHITE);
	            partCountry.setBorderColor(Color.WHITE);
	            partCountry.setBorderWidth(1f);
	            
	            /*chk = new Chunk("Prepared By: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	           // cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setBorder(0);
	            cellCountry.setNoWrap(true);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	            cellCountry.setBorder(0);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);*/
	            
	            
	          
	            
	           /* chk = new Chunk("SERVICE INFORMATION ",FontFactory.getFont("Courier-Bold", 12, Font.UNDERLINE,Color.blue));
	            cellCountry = new Cell(chk);
	            cellCountry.setColspan(2);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
	            //cellCountry.setBackgroundColor(Color.ORANGE);
	            cellCountry.setBorder(0);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("Agent: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk); 
	            //cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setBorder(0);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("   "+(headerDOB.getAgent()!=null?headerDOB.getAgent().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	            cellCountry.setNoWrap(true);
	            cellCountry.setBorder(0);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	           
	            chk = new Chunk("Commodity or Product: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	           // cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("   "+(headerDOB.getCommodity()!=null?headerDOB.getCommodity().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("Type Of Service Quoted: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	           // cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("   "+(headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setLeading(8.0f);
		            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("Notes: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	           // cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk(" "+(headerDOB.getNotes()!=null?headerDOB.getNotes().toUpperCase()+'\n':""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	            cellCountry.setLeading(8.0f);
	            cellCountry.setBorder(0);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("Date Effective: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	            //cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("   "+(effDate[0]!=null?effDate[0]:""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("Validity Of Quote: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	          //  cellCountry.setBackgroundColor(Color.lightGray);
	            cellCountry.setNoWrap(true); 
	            cellCountry.setBorder(0);
	            cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            chk = new Chunk("   "+(validUptoStr!=null?validUptoStr:"VALID UNTIL FURTHER NOTICE"),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	            cellCountry = new Cell(chk);
	            cellCountry.setNoWrap(true);
	            cellCountry.setBorder(0);
	            cellCountry.setLeading(8.0f);
	            partCountry.addCell(cellCountry);
	            
	            if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
	            {
	              chk = new Chunk("Payment Terms: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              //cellCountry.setBackgroundColor(Color.lightGray);
	              cellCountry.setNoWrap(true);
	              cellCountry.setBorder(0);
	              cellCountry.setHorizontalAlignment(cellCountry.ALIGN_RIGHT);
	              cellCountry.setLeading(8.0f);
	              partCountry.addCell(cellCountry);
	              
	              chk = new Chunk("   "+headerDOB.getPaymentTerms(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	              cellCountry = new Cell(chk);
	              cellCountry.setNoWrap(true); 
	              cellCountry.setBorder(0);
	              cellCountry.setLeading(8.0f);
	              partCountry.addCell(cellCountry);
	            }
	            
	            document.add(partCountry);*/
	            partCountry.complete();
	            //System.out.println("After Page Country-------------------------------->");
	            //Origin Charges
	            //document.setMargins(10,10,10,10);
	          // b1= writer.getPageNumber();
	      

	              

	            
	           
	            //@@Added by Kameswari for the WPBN issue-146448
	             charges				                      = finalDOB.getLegDetails();
	             noOfLanes			                    = charges.size();
	             //Added by kiran.v on 16/09/2011
	           // session.setAttribute("legSize",noOfLanes);           
	            PdfPTable chargeCountry2  = null ;
	             Table chargeCountry  = null ;
	            PdfPCell cell2			= null;
	            Cell cell			= null;
	            Table frtHeader 	= null;
	            Table chargeTitle = null;
	            Table chargeCountry1= null;
	            Cell  cell1           = null ;
	            CustomCell border = new CustomCell();
	           // float cellWidths[]   = {40,20,10,15,25};//@@Added by Kameswari for the WPBN issue - on 12/11/08
	           float cellWidths[]   = {40,15,15,15,25,15};
	              float cellWidths1   = 40;
	            
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
	         //    boolean b3 = document.newPage();
	          //  QuoteCharges chargesDOB           = null;
	            ArrayList    originChargeInfo     = null;
	            int          originChargesInfoSize= 0;
	            MultiQuoteChargeInfo chargeInfo        = null;
	            ArrayList originLaneCharges  =finalDOB.getLegDetails();//.get(0)).getOriginChargesList();
	            for(int c=0;c<noOfLanes;c++)      //charges........
	            {
	            legOrginCharges = (MultiQuoteFreightLegSellRates) originLaneCharges.get(c);
	            ArrayList originCharges = legOrginCharges.getOriginChargesList(); 	
	            int[] originIndices				= legOrginCharges.getSelectedOriginChargesListIndices();
	            int   originChargesSize   = 0;
	            if(originIndices!=null)
	              originChargesSize		= originIndices.length;
	            else
	              originChargesSize		= 0;
	            
	            /*PdfPCell hLine = new PdfPCell(new Phrase(""));
	            hLine.setBorder(PdfPCell.NO_BORDER);*/
	            
	            if(originChargesSize<=0)
					    {
	            	continue;
					    }
	               /*  if(b1>1)
	                {
	                 chk = new Chunk("QUOTE REFERENCE:"+masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
	                cell = new Cell(chk);
	                cell.setColspan(5);cell.setLeading(10.0f);
	                cell.setBackgroundColor(Color.WHITE);                
	                cell.setHeader(true);
	                cell.setBorder(0);
	                chargeCountry.addCell(cell); 
	                }*/
	               // chargeCountry  = new Table(5);
	            	home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
            		remote      = home.create();
	            	//document.add(horizontalLine);
	            	
	            	chargeTitle = new Table(1);
	            	chargeTitle.setWidth(100);
	            	chargeTitle.setPadding(1);
	            	chargeTitle.setSpacing(1);
	            	chargeTitle.setBorderColor(Color.BLACK);
	            	chargeTitle.setDefaultHorizontalAlignment(Element.ALIGN_LEFT);
	            	chargeTitle.setBackgroundColor(Color.ORANGE);//modified by silpa.p on 3-06-11
	            	chargeTitle.setBorderWidth(1f);
	            	//chargeTitle.setBorder(1);
	            	
	            	if(legOrginCharges.getShipmentMode() == 2)
	                chk = new Chunk(remote.getPortName(legOrginCharges.getOrigin())+"-Origin Charges",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.black));
	            	else
	            	chk = new Chunk(remote.getLocationName(legOrginCharges.getOrigin())+"-Origin Charges",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.black));	
	            	cell1 = new Cell(chk);
	            	cell1.setLeading(10.0f); 
	            	cell1.setBorder(0);
	            	cell1.setHeader(true);
	            	chargeTitle.addCell(cell1);
	            	document.add(chargeTitle);
	            	
	            	//chargeCountry  = new PdfPTable(6);
	            	chargeCountry  = new Table(6);
	                //chargeCountry.setWidth(100);
	                chargeCountry.setWidths(cellWidths);
	                chargeCountry.setBorder(1);
                    chargeCountry.setWidth(100);
                    chargeCountry.setDefaultVerticalAlignment(cell.ALIGN_MIDDLE);
                    chargeCountry.setDefaultHorizontalAlignment(cell.ALIGN_CENTER);
                    chargeCountry.setPadding(3);
                    chargeCountry.setSpacing(0);
	                //chargeCountry.setOffset(5);
	                //chargeCountry.setBackgroundColor(Color.WHITE);
	               // chargeCountry.setBorderColor(Color.WHITE);
	               // chargeCountry.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
	                chargeCountry.setBorderWidth(1f);
	                //chargeCountry.setWidthPercentage(100);
	
	    			chargeCountry.setWidths(cellWidths);
	                //document.add(horizontalLine);
	                chk = new Chunk("Charge Description",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK)); // Added by Gowtham on 01-Feb-2011.
	               // cell = new PdfPCell(new Phrase(chk));
	                cell = new Cell(new Phrase(chk));
	                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
	               // cell.setColspan(6);
	                //cell.setLeading(10.0f);
	                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
	               // cell.setBackgroundColor(Color.ORANGE);  
	                ////cell.setBorder(0);
	                //cell.setHeader(true);
	                //cell.setBorder(Rectangle.BOTTOM);
	                cell.setBorderWidth(1);	                
	    			//cell.setCellEvent(border); 
	                chargeCountry.addCell(cell);
	                
	                chk = new Chunk("Weight Break Slab",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
	                cell =  new Cell(new Phrase(chk));
	                //cell.setBorder(Rectangle.BOTTOM);
	    			//cell.setCellEvent(border); 
	    			cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	    			cell.setBorderWidth(1);
	                chargeCountry.addCell(cell);
	                
	              
	                chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
	                cell =  new Cell(new Phrase(chk));
	                //cell.setBorder(Rectangle.BOTTOM);
	    			//cell.setCellEvent(border);
	                cell.setBorderWidth(1);
	    			cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                chargeCountry.addCell(cell);
	                
	                
	                chk = new Chunk("Charge Rate",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
	                cell =  new Cell(new Phrase(chk));
	                //cell.setBorder(Rectangle.BOTTOM);
	    			//cell.setCellEvent(border);
	                cell.setBorderWidth(1);
	    			cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                chargeCountry.addCell(cell);
	                
	                chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
	                cell =  new Cell(new Phrase(chk));
	                //cell.setBorder(Rectangle.BOTTOM);
	    			//cell.setCellEvent(border);
	                cell.setBorderWidth(1);
	    			cell.setHorizontalAlignment(cell.ALIGN_LEFT);
	                chargeCountry.addCell(cell);
	    			
	                chk = new Chunk("Density Ratio",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
	                cell =  new Cell(new Phrase(chk));
	                //cell.setBorder(Rectangle.BOTTOM);
	    			//cell.setCellEvent(border);
	                cell.setBorderWidth(1);
	    			cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                chargeCountry.addCell(cell);
	            
	            
	                //@@Commented and Modified by Kameswari for the internal issue on 09/04/09
	                /*chk = new Chunk("ORIGIN CHARGES",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
	                cell = new Cell(chk);
	                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
	                cell.setColspan(6);
	                cell.setLeading(10.0f);
	                cell.setBackgroundColor(Color.ORANGE);                
	                cell.setHeader(true);
	                chargeCountry.addCell(cell);*/
	             //   boolean b = document.newPage();
	              // charge_desc  = "";
	             for(int lc = 0;lc<originChargesSize;lc++)
	             {
	            	if((operation!=null && "view".equalsIgnoreCase(operation) || ("Y".equalsIgnoreCase(legOrginCharges.getOriginChargesSelectedFlag()[originIndices[lc]]))))
	            	{
	           
	                
	                //chargeCountry.endHeaders();
	                chargesDOB				    = (MultiQuoteCharges)originCharges.get(originIndices[lc]);
	                
	               // for(int i=0;i<originChargesSize;i++)
	                //{
	                  if(originIndices[lc] !=-1)
	                  {
	                  

	                   logger.info("Origin Charges doPDFGeneration::"+lc+":"+chargesDOB); // newly added                  
	                  originChargeInfo		  = chargesDOB.getChargeInfoList();
	                  originChargesInfoSize	= originChargeInfo.size();
	                  int m =0;//146455
	              String breakPoint = null;//146455
	              
	                  for(int k=0;k<originChargesInfoSize;k++)
	                  {
	                    chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);
	                   if(k==0)  
	                    {
	                      if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))//||"BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"SC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())) // Added by Gowtham
	                        chk = new Chunk(chargesDOB.getExternalName()!=null?chargesDOB.getExternalName():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      else
	                        chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      
	                      cell =  new Cell(new Phrase(chk));
	                      cell.setLeading(7.0f);
	                      cell.setBorder(1);
	                      cell.setRowspan(originChargesInfoSize);
	                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
	                      cell.setVerticalAlignment(cell.ALIGN_LEFT);      
	                      cell.setBorderWidthTop(0);
	                     cell.setBorderWidthBottom(0.1f);
	                     if( lc==originChargesSize-1&& k==originChargesInfoSize-1){
	                         	cell.setBorderWidthBottom(0.1f);
	                    	  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
		                      //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
		  	    			  
	                      }else if(k==originChargesInfoSize-1){
	                    	  cell.setBorderWidthBottom(0.1f);
	                    	  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
		                      //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
	  	                    	//cell.setBorderWidthBottom(0.1f); 	                    	 
	                      }
	                      else{
	                    	  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
	                    	//cell.setVerticalAlignment(cell.ALIGN_CENTER);
	                    
	                    	  
	                      }
	                      chargeCountry.addCell(cell);
	  	                
	                    }
	                   /* else if (k==originChargesInfoSize-1) 
	                    {
	                      cell = new Cell(new Phrase(new Chunk("")));
	                    //  cell.setLeading(15.0f);
	                      //cell.setBorder(Rectangle.BOTTOM);
	  	    			 // cell.setCellEvent(border); 	                    
	                      if(lc!=originChargesSize-1){
	                      cell.setBorder(0);
	                      cell.setBorderWidth(0f);
	                      }	 
	                      else{
	                    	  cell.setBorderWidthBottom(0.1f);  
	                     
	                      }
	  	    			  cell.setHorizontalAlignment(cell.ALIGN_BOTTOM);
	                     
	  	    			 cell.setVerticalAlignment(cell.ALIGN_CENTER);
	  	    			  chargeCountry.addCell(cell);
	  	    			
	                    }*/
	               
	                    /*else{
	                    	cell = new Cell(new Phrase(new Chunk("")));
	                      //cell.setBorder(0);
		  	    			  //cell.setCellEvent(border); 
	                      cell.setBorder(0);
		                      cell.setBorderWidth(0f);
		                     // cell.setLeading(15.0f);
		  	    			  cell.setHorizontalAlignment(cell.ALIGN_BOTTOM);
		  	    			
		  	    			 cell.setVerticalAlignment(cell.ALIGN_CENTER);
		  	    			
	                      chargeCountry.addCell(cell);
	                    	
	                    }*/
	        //@@ Commented by subrahmanyam for 146455 on 19/02/09
	                     /* chk = new Chunk((chargeInfo.getBreakPoint()!=null && !"Absolute".equalsIgnoreCase(chargeInfo.getBreakPoint()) && !"Percent".equalsIgnoreCase(chargeInfo.getBreakPoint()))?chargeInfo.getBreakPoint().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	                      cell = new Cell(chk);cell.setLeading(8.0f);
	                      cell.setBackgroundColor(Color.lightGray);
	                      cell.setHeader(true);
	                      chargeCountry.addCell(cell);*/
	        //@@ Added by subrahmanyam for 146455 on 19/02/09                      
	                      
	                             if(chargeInfo.getBreakPoint().equalsIgnoreCase("MIN"))
	                              {
	                                        breakPoint = "Min";
	                              }
	                         
	                           else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FLAT"))
	                             {
	                                          breakPoint = "Flat";
	                         }                         
	                        
	                        else if(chargeInfo.getBreakPoint().equalsIgnoreCase("MAX"))
	                        {
	                              breakPoint ="Max";
	                        }
	                        else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
	                        {
	                                          breakPoint ="Absolute";
	                       } 
	                        else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
	                          {
	                                        breakPoint ="Percent";
	                        }
	                       else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BASE"))
	                        {
	                              breakPoint ="Base";
	                        }
	                   
	                    else
	                    {
	                                      breakPoint= chargeInfo.getBreakPoint();
	                  }
	                 
	                  //chk = new Chunk(breakPoint!=null&& !"Absolute".equalsIgnoreCase(breakPoint)&&!"Percent".equalsIgnoreCase(breakPoint)?breakPoint:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK)); Commented by silpa for not displaying the abslute brak in pdf
	                   chk = new Chunk(breakPoint!=null?breakPoint:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//added by silpa.p on 16-06-11
	                  cell = new Cell(new Phrase(chk));
                	  cell.setLeading(7.0f);
	                  if (k==originChargesInfoSize-1) 
	                    {
	                      //cell.setBorder(Rectangle.ALIGN_CENTER);
	  	    			 // cell.setCellEvent(border); 
	                	//  cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
	  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	  	    		   }
	                    else{;
	                      //cell.setBorder(0);
		  	    			  //cell.setCellEvent(border); 
	                    	 // cell.setBorder(Rectangle.ALIGN_CENTER);	   	                 
	                    //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
		  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		  	    		}
                      
	                      chargeCountry.addCell(cell);
//	@@ Ended by subrahmanyam for 146455 on 19/02/09                
	                      
	                      chk = new Chunk(chargeInfo.getCurrency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      cell = new Cell(new Phrase(chk));
	                      cell.setLeading(7.0f);
	                      if (k==originChargesInfoSize-1) 
		                    {
		                      //cell.setBorder(Rectangle.BOTTOM);
		  	    			 // cell.setCellEvent(border); 
		  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		  	    		   }
		                    else{
	                      //cell.setBorder(0);
			  	    			  //cell.setCellEvent(border); 
			  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
			  	    		}
	                      chargeCountry.addCell(cell);
	                      
	                      chk = new Chunk(df.format(chargeInfo.getSellRate())+(chargeInfo.isPercentValue()?" %":""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      cell = new Cell(new Phrase(chk));
	                      cell.setLeading(7.0f);
	                      if (k==originChargesInfoSize-1) 
		                    {
		                      //cell.setBorder(Rectangle.BOTTOM);
		  	    			  //cell.setCellEvent(border); 
		  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		  	    		   }
		                    else{
	                      //cell.setBorder(0);
			  	    			  //cell.setCellEvent(border); 
			  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
			  	    		}
	                      chargeCountry.addCell(cell);
	                      
	                      chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      cell = new Cell(new Phrase(chk));
	                      cell.setLeading(7.0f);
	                      if (k==originChargesInfoSize-1) 
		                    {
		                      //cell.setBorder(Rectangle.BOTTOM);
		  	    			  //cell.setCellEvent(border); 
		  	    			  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
		  	    		   }
		                    else{
	                      //cell.setBorder(0);
			  	    			  //cell.setCellEvent(border); 
			  	    			  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
			  	    		}
	                      chargeCountry.addCell(cell);  
	                      
	                     //@@Added by Kameswari for the WPBN issue- on 12/11/08
	                      chk = new Chunk(chargeInfo.getRatio()!=null?"1:"+chargeInfo.getRatio():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      cell = new Cell(new Phrase(chk));
	                      cell.setLeading(7.0f);
	                      if (k==originChargesInfoSize-1) 
		                    {
		                      //cell.setBorder(Rectangle.BOTTOM);
		  	    			  //cell.setCellEvent(border); 
		  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		  	    		   }
		                    else{
	                      //cell.setBorder(0);
			  	    			  //cell.setCellEvent(border); 
			  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
			  	    		}
	                      chargeCountry.addCell(cell);  
	                      
	                  }
	                  }
	                //}
	                }
	            					    }
	             	
					
	            if(chargeCountry!=null)
	            	document.add(chargeCountry);
	            	
	            	pTable = new PdfPTable(1);
	            	pTable.setSpacingAfter(10f);
	            	pCell	 = new PdfPCell(new Phrase(new Chunk("")));
	            	pCell.setBorder(0);
	            	pTable.addCell(pCell);
	            	document.add(pTable);
	            	
					//} // Added by Gowtham on 01-Feb-2011.
	          //@@Added by kiran.v on 31/01/2012 for Wpbn Issue-287659
	           
	            }
	              pTable = new PdfPTable(1);
	              pTable.setSpacingAfter(10f);
	              pCell	 = new PdfPCell(new Phrase(new Chunk("")));
	              pCell.setBorder(0);
	              pTable.addCell(pCell);
	              
	              document.add(pTable); 
	              
	           // document.newPage();
	          
	            // boolean b1 = document.newPage();
	             
	              //Freight Charges
	              //document.setMargins(10,10,10,10);
	              //System.out.println("After         Origin Charges --------------------------------->");
	            
	            if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith())) {
	            	// Added by Gowtham. to skip in case of quote with charges.
	            	//Table	chargeCountry1 = null;
	            	//Cell	cell1			=null;
	              int       freightChargesSize                  = 0;
	              int       freightChargesInfoSize              = 0;
	              int[]     frtIndices                          = null;
	              int		tableColwidth						= 	0; //Added by Gowtham for Landscape Issue.
	              String 	tmpBasis							=	"";
	              int		tmpBrkptsize						= 	0;
	              String	tmpmultiBrkpt						= 	"";
	              int 		tmpTableWidth						=	0; 
	              String 	rates								=	 null;  
	              Double    rates1								= 	0.00;
	              int		tmpCount							=	0; 
	              String 	basis								= 	""; 
	              String 	orgDesc								=	""	;  
	              String 	destDesc							=	""	;  
	              String 	breakpt								= 	""; 
	              String	tmpFreq								=	"" ;  
	              String	tmpFreq1							=	"" ;	
	              int		tmpFrCnt							=	0;  //Added by Gowtham for Landscape Issue
	              MultiQuoteChargeInfo		viewDOB				= null;	
	              ArrayList					chargesInfolist		= new ArrayList();
	              int[] tmpBrksize								= null;
	              
	              //QuoteFreightLegSellRates       legCharges	    = null;
	             // ArrayList                      freightCharges = null;
	              ArrayList                      freightChargeInfo = null;
	              int m =0;
	              String breakPoint = null;
	              String   space   ="";
	              Table country = null;
	            String serviceLevelDesc							="" ;//Added by silpa on 16-05-2011
	            String carrierDesc               				="";//Added by silpa on 16-05-2011
	             
	                // b2= writer.getPageNumber();
	             // int[] frtCellWidths = {15,16,15,15,18,12,25,12,12,20,15}; // Modified by Gowtham on 24Feb2011
	              
	              if("View".equalsIgnoreCase(request.getParameter("Operation")))
	              {
	            	  	home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
                		remote      = home.create();
                		
                		chargesInfolist = remote.getChargeInfoDetailsforView( masterDOB.getQuoteId());
                		viewDOB = (MultiQuoteChargeInfo)chargesInfolist.get(0);
                		tmpBrkptsize = viewDOB.getMultiBreakPoints().length;
                		surChargesMap = viewDOB.getSurChragesMap();
                		
	              }
	              else
	              {
	             
	              surChargesMap= new HashMap<String, String>();
	              for(int lane=0; lane<noOfLanes; lane++){
		              legCharges	   = (MultiQuoteFreightLegSellRates)charges.get(lane);
	                  freightCharges =  legCharges.getFreightChargesList();
	                  MultiQuoteChargeInfo	tempChargeInfo = freightCharges!=null?(MultiQuoteChargeInfo)freightCharges.get(lane):null;
	                 // tmpBrkptsize =	tempChargeInfo!=null?finalDOB.getMultiQuoteSelectedBreaks().split(",").length:0;
	                 
	                  String[] breakpoints = tempChargeInfo!=null?tempChargeInfo.getMultiBreakPoints():null;
	                  String[]rateDescs	   = tempChargeInfo!=null?tempChargeInfo.getMultiRateDescriptions():null;
	                  String chargeDesc = null;
	                 
	                  for(int i =0; i<(breakpoints!=null?breakpoints.length:0); i++){
	                	  chargeDesc = rateDescs[i];
	                	 /*if(chargeDesc!=null && !"-".equals(chargeDesc) && !"A FREIGHT RATE".equals(chargeDesc)){
	                		 surChargesMap.put(breakpoints[i].length()>=7?breakpoints[i].substring(0, 3):breakpoints[i],chargeDesc.substring(0, chargeDesc.length()-3) );
	                	 }*/
                          if(!"List".equalsIgnoreCase(finalDOB.getMasterDOB().getMultiquoteweightBrake())){
	                	   if(chargeDesc!=null && !"-".equals(chargeDesc) && !"A FREIGHT RATE".equals(chargeDesc)){
	                		   if(breakpoints[i].length()>=7)
		                		 surChargesMap.put(breakpoints[i].substring(0, 3),chargeDesc.substring(0, chargeDesc.length()-3) );
		                	 }
	                  	}
	                	
	                   if("List".equalsIgnoreCase(finalDOB.getMasterDOB().getMultiquoteweightBrake())){
	                	 if(chargeDesc!=null && !"-".equals(chargeDesc) && !"A FREIGHT RATE".equals(chargeDesc)){
	                		   	if(breakpoints[i].length()>4 && breakpoints[i].length()<10)
		                		 surChargesMap.put(breakpoints[i].substring(4,7),chargeDesc.substring(0,chargeDesc.length()-3) );
		                	 }
	                	 }
	                  }
	              }
                  
	              legCharges	   = (MultiQuoteFreightLegSellRates)charges.get(0);
                  freightCharges =  legCharges.getFreightChargesList();
                  MultiQuoteChargeInfo	tempChargeInfo = freightCharges!=null?(MultiQuoteChargeInfo)freightCharges.get(0):null;
                  tmpBrkptsize =	tempChargeInfo!=null?finalDOB.getMultiQuoteSelectedBreaks().split(",").length:0;
                  
                  	tmpBrksize = new int[tmpBrkptsize]; 
                  int 	tmpBrk		= 0 ;
                	  for (int frtCnt1=0; frtCnt1<tmpBrkptsize;frtCnt1++)
                	  {		tmpBrk = Integer.parseInt(finalDOB.getMultiQuoteSelectedBreaks().split(",")[frtCnt1]);
                		  //if("Y".equalsIgnoreCase(tempChargeInfo.getChecked_Flag().split(",")[frtCnt1]))
                		  //{ 
                			  tmpBasis = tmpBasis!=""?(tmpBasis+tempChargeInfo.getBasis().split(",")[tmpBrk]+","): (tempChargeInfo.getBasis().split(",")[tmpBrk]+",");
                			  tmpmultiBrkpt = tmpmultiBrkpt!= ""?(tmpmultiBrkpt+tempChargeInfo.getMultiBreakPoints()[tmpBrk]+","): (tempChargeInfo.getMultiBreakPoints()[tmpBrk]+",") ;
                			  tmpBrksize[frtCnt1] = tmpBrk;
                			 // tmpTableWidth++;
                		 // }
                	  }
	              }
                
                 /* if("Add".equalsIgnoreCase(request.getParameter("Operation"))||"Modify".equalsIgnoreCase(request.getParameter("Operation"))||"Copy".equalsIgnoreCase(request.getParameter("Operation")))
                  {		
                	  
                	  tableColwidth = tmpBrkptsize;
                	  
                  }
                  else
                	  tableColwidth =  tmpBrkptsize;*/
	              
	             // tableColwidth = tmpBrkptsize + 4;   //added by silpa.p on 16-05-11//commented by silpa.p on 23-06-11
	              tableColwidth = tmpBrkptsize + 5; //added by silpa.p on 23-06-11 for currency add
	              //@@Modified by kiran on 10/08/2011 for WPBN Issue-258778
	              // 09/11/2011
	              if("add".equalsIgnoreCase(operation)||"modify".equalsIgnoreCase(operation)||"Copy".equalsIgnoreCase(operation)){
	            if("checked".equalsIgnoreCase(request.getParameter("selectCarrier"))){
                	  tableColwidth=tableColwidth+1;
                 }
                  if("checked".equalsIgnoreCase(request.getParameter("selectService"))){
                	  tableColwidth=tableColwidth+1;
                  }
                  if("checked".equalsIgnoreCase(request.getParameter("selectFrequecy"))){
                	  tableColwidth=tableColwidth+1;
                  }
                  //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
                  if("checked".equalsIgnoreCase(request.getParameter("selectTransitTime"))){
                	  tableColwidth=tableColwidth+1;
                  }
                  if("checked".equalsIgnoreCase(request.getParameter("selectFrieghtValidity"))){
                	  tableColwidth=tableColwidth+1;
                  }
	           }
	           else{
	        	   if("checked".equalsIgnoreCase(request.getParameter("selectCarrier"))||"Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getCarrierChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getCarrierChecked():"")){
	                	  tableColwidth=tableColwidth+1;
	                 }
	                  if("checked".equalsIgnoreCase(request.getParameter("selectService"))||"Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getServiceChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getServiceChecked():"")){
	                	  tableColwidth=tableColwidth+1;
	                  }
	                  if("checked".equalsIgnoreCase(request.getParameter("selectFrequecy"))|| "Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getFrequencyChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getFrequencyChecked():"")){
	                	  tableColwidth=tableColwidth+1;
	                  }
	                  //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
	                  if("checked".equalsIgnoreCase(request.getParameter("selectTransitTime"))|| "Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getTransitTimeChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getTransitTimeChecked():"")){
	                	  tableColwidth=tableColwidth+1;
	                  }
	                  if("checked".equalsIgnoreCase(request.getParameter("selectFrieghtValidity"))|| "Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getRateValidityChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getRateValidityChecked():"")){
	                	  tableColwidth=tableColwidth+1;
	                  }
	           }
	              //@@Ended by kiran.v
                  int[]   frtCellWidths = new int[tableColwidth];//modified by silpa.p 0n 16-05-11
                  for(int frtcnt = 0;frtcnt<tableColwidth;frtcnt++)//modified by silpa.p 0n 16-05-11
	                {
                	  frtCellWidths[frtcnt] = 10;
                  }
	                if(noOfLanes>0)
	                {		
	                	
	                	//if( originChargesSize >0)
	                	//document.newPage(); 
	                	//document.add(horizontalLine);
	                	frtHeader = new Table(1);
	                	frtHeader.setWidth(100);
	                	frtHeader.setBackgroundColor(Color.white);
	                	frtHeader.setBorderColor(Color.BLACK);
	                	//frtHeader.setBorder(Rectangle.TOP);
	                	frtHeader.setBorderWidth(1f);
	                	frtHeader.setDefaultHorizontalAlignment(Element.ALIGN_LEFT);
	                	frtHeader.setPadding(1);
	                	frtHeader.setSpacing(1);
	                	frtHeader.setTableFitsPage(true);
	                	
	                	chk = new Chunk("FREIGHT CHARGES",FontFactory.getFont("ARIAL", 8,Font.BOLD,Color.BLACK));
	                	cell1 = new Cell(chk);
	                	cell1.setLeading(10.0f); 
	                	cell1.setBorder(0);
	                	 cell1.setBackgroundColor(Color.ORANGE);//modified by silpa.p on 3-06-11
	                	frtHeader.addCell(cell1);
	                	document.add(frtHeader);
	                	//document.add(horizontalLine);
	                	chargeCountry2  = new PdfPTable(tableColwidth);//modified by silpa.p on 16-05-11
	                  chargeCountry2.setWidthPercentage(100);
	                  chargeCountry2.setWidths(frtCellWidths);
	                  
	                  /*chargeCountry.setPadding(1);
	                  chargeCountry.setSpacing(0);
	                  chargeCountry.setOffset(5);*/
	                  
	                  /*
	                  cell = new PdfPCell(new Phrase(new Chunk("")));
                      cell.setBorder(Rectangle.BOTTOM);
  	    			  cell.setCellEvent(border); 
  	    			  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
  	    			  chargeCountry.addCell(cell);*/
	                  
	                 /* chargeCountry.setBackgroundColor(Color.WHITE);
	                  chargeCountry.setBorderColor(Color.white);
	                  chargeCountry.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
	                  //chargeCountry.setBorderWidth(1f);
	                  chargeCountry.setTableFitsPage(true);//@@Added by kameswari 
	                 */
	                 if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith())){//Added by Anil.k
	                  //for(int i=0;i<noOfLanes;i++)//testttttttttttinggggggggg
	                  
	                     legCharges	   = (MultiQuoteFreightLegSellRates)charges.get(0);
	                      freightCharges =  legCharges.getFreightChargesList();
	            
	                    freightChargesSize =  freightCharges.size();//No of rates per lane
                             //Added By Kishore Podili (this change was over rided)
	                    if("View".equalsIgnoreCase(request.getParameter("Operation")))
	                    	freightChargesSize = chargesInfolist.size();
	             
	                    //added by silpa.p on 16-05-11
	                 //ended	                   
	                    if(freightChargesSize>0)
	                    {
	                    		
                    	chk = new Chunk("Origin",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
                    	cell2 = new PdfPCell(new Phrase(chk));
                        cell2.setBorder(Rectangle.BOTTOM);
    	    			cell2.setCellEvent(border); 
    	    			cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
 	                  	chargeCountry2.addCell(cell2);
 	                  	
 	                    chk = new Chunk("Destination",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
 	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                    chargeCountry2.addCell(cell2);
	                    System.out.println("operation"+operation);
	                   //@@Modified by kiran on 10/08/2011 for WPBN Issue-258778
	                    // 09/11/2011
	                  if("add".equalsIgnoreCase(operation)||"modify".equalsIgnoreCase(operation)||"Copy".equalsIgnoreCase(operation)){
	                    if("checked".equalsIgnoreCase(carrierChecked)){//added by silpa.p on 14-05-11
	                    chk = new Chunk("Carrier",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                    chargeCountry2.addCell(cell2);
	                    }//ended
	                    
	                    
	                    if("checked".equalsIgnoreCase(serviceLevelChecked)){//added by silpa.p on 14-05-11
	                    chk = new Chunk("Service level",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
	                   // serviceflag=true;
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                    chargeCountry2.addCell(cell2);              
	                    
	                    }//ended
	                    if("checked".equalsIgnoreCase(frequencyChecked)){//added by silpa.p on 16-05-11
	                    chk = new Chunk("Frequency",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
	                   // frequencyflag=true;
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                    chargeCountry2.addCell(cell2);      
	                    }  
	                    //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
	                    if("checked".equalsIgnoreCase(transitTimeChecked)){
		                    chk = new Chunk("TransitTime",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
		                    cell2 = new PdfPCell(new Phrase(chk));
		                   // frequencyflag=true;
	 	                    cell2.setBorder(Rectangle.BOTTOM);
	   	    				cell2.setCellEvent(border); 
	   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                    chargeCountry2.addCell(cell2);      
		                    }
	                    if("checked".equalsIgnoreCase(validityChecked)){
		                    chk = new Chunk("FreightValidity",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
		                    cell2 = new PdfPCell(new Phrase(chk));
	                   // frequencyflag=true;
	 	                    cell2.setBorder(Rectangle.BOTTOM);
	   	    				cell2.setCellEvent(border); 
	   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                    chargeCountry2.addCell(cell2);      
		                    }
	                  }
	                  else{
	                	  if("checked".equalsIgnoreCase(carrierChecked)||"Y".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked())){//added by silpa.p on 14-05-11
	  	                    chk = new Chunk("Carrier",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	  	                    carrierFlag=true;
	  	                    cell2 = new PdfPCell(new Phrase(chk));
	   	                    cell2.setBorder(Rectangle.BOTTOM);
	     	    				cell2.setCellEvent(border); 
	     	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	  	                    chargeCountry2.addCell(cell2);
	  	                    }//ended
	  	                    if("checked".equalsIgnoreCase(serviceLevelChecked)||"Y".equalsIgnoreCase(multiQuoteChargeInfo.getServiceChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getServiceChecked())){//added by silpa.p on 14-05-11
	  	                    chk = new Chunk("Service level",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	  	                    cell2 = new PdfPCell(new Phrase(chk));
	  	                    serviceflag=true;
	  	                  serviceFlag=true;
	   	                    cell2.setBorder(Rectangle.BOTTOM);
	     	    				cell2.setCellEvent(border); 
	     	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	  	                    chargeCountry2.addCell(cell2);              
	                    
	  	                    }//ended
	  	                    if("checked".equalsIgnoreCase(frequencyChecked)|| "Y".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked())){//added by silpa.p on 16-05-11
	  	                    chk = new Chunk("Frequency",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	  	                    cell2 = new PdfPCell(new Phrase(chk));
	  	                    frequencyflag=true;
	   	                    cell2.setBorder(Rectangle.BOTTOM);
	     	    				cell2.setCellEvent(border); 
	     	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	  	                    chargeCountry2.addCell(cell2);      
	  	                    }
	  	                    //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
	  	                  if("checked".equalsIgnoreCase(transitTimeChecked)|| "Y".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked())){
		  	                    chk = new Chunk("TransitTime",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
		  	                    cell2 = new PdfPCell(new Phrase(chk));
		  	                  transittimeflag=true;
		   	                    cell2.setBorder(Rectangle.BOTTOM);
		     	    				cell2.setCellEvent(border); 
		     	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		  	                    chargeCountry2.addCell(cell2);      
		  	                    }
	  	                if("checked".equalsIgnoreCase(validityChecked)|| "Y".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked())){
	  	                    chk = new Chunk("FreightValidity",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	  	                    cell2 = new PdfPCell(new Phrase(chk));
	  	                  freightValidity=true;
	   	                    cell2.setBorder(Rectangle.BOTTOM);
	     	    				cell2.setCellEvent(border); 
	     	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	  	                    chargeCountry2.addCell(cell2);      
	  	                    }
	                  }
	                  //@@ Ended by kiran.v
	                    chk = new Chunk("Incoterms",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                    chargeCountry2.addCell(cell2);
	                  //added by silpa.p on 23-06-11 for currency add
	                    chk = new Chunk("Currency",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                    chargeCountry2.addCell(cell2);//ended
	    	         
	                    if("Add".equalsIgnoreCase(request.getParameter("Operation"))||"Modify".equalsIgnoreCase(request.getParameter("Operation"))||"Copy".equalsIgnoreCase(request.getParameter("Operation")) )
	                      { 
	                    	for(int brk1=0;brk1<tmpBrkptsize;brk1++)
	                        {
	                      
	                    		if(tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("MIN"))
	                        {
	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>7)
	                    				breakpt = "MIN"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    			//breakpt = "MIN";
	                          else
	                    			breakpt = "MIN";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("BASIC"))
	                        {
                       
	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>7)
	                    				breakpt = "BASIC"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    				//breakpt = "BASIC";
	                    			else
	                    			breakpt = "BASIC";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("FLAT"))
	                        {

	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>7)
	                    				breakpt = "FLAT"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    					//breakpt ="FLAT";
	                    			else
	                    			breakpt = "FLAT";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("ABSOLUTE"))
	                        {

	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>10)
	                    				breakpt = "ABSOLUTE"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    				//breakpt = "ABSOLUTE";
	                    			else
	                    			breakpt = "ABSOLUTE";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("PERCENT"))
	                        {

	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>10)
	                    				breakpt = "PERCENT"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    			//breakpt = "PERCENT";
	                    			else
	                    			breakpt = "PERCENT";
	                        }
	                    		else if(isInteger(tmpmultiBrkpt.split(",")[brk1])|| isDouble(tmpmultiBrkpt.split(",")[brk1]))
	                        {
	                    			breakpt	=	tmpmultiBrkpt.split(",")[brk1];
	                        }
	                        else
	                    			breakpt = tmpmultiBrkpt.split(",")[brk1]!= null ? ( tmpmultiBrkpt.split(",")[brk1].length() >5 ? tmpmultiBrkpt.split(",")[brk1].substring(0,4)+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(4,tmpmultiBrkpt.split(",")[brk1].length()-2):tmpmultiBrkpt.split(",")[brk1]):"";
	                    		basis =  tmpBasis.split(",")[brk1]!=null ? tmpBasis.split(",")[brk1] : "";

	                    		//chk = new Chunk(breakpt+"\n"+basis,FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	                    		chk = new Chunk(breakpt,FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    		cell2 = new PdfPCell(new Phrase(chk));
	     	                    cell2.setBorder(Rectangle.BOTTOM);
	       	    				cell2.setCellEvent(border); 
	       	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                          chargeCountry2.addCell(cell2);
	                      }
	                       }
	                    	 else
	                    	 {
	                    	for(int t=0;t<tmpBrkptsize;t++)
	                    {
	                    		if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("MIN"))
	                    		{	
	                    			if(viewDOB.getMultiBreakPoints()[t].length()>7)
	                    				breakpt= "MIN"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
	                    			//breakpt= "MIN";
	                    			else	                    				
	                    			breakpt = "MIN";
	                    		}
	                    		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("BASIC"))
	                    		{	
	                    			if(viewDOB.getMultiBreakPoints()[t].length()>7)
	                    				breakpt= "BASIC"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
	                    			//breakpt= "BASIC";
	                    			else	                    				
	                    			breakpt = "BASIC";
	                    		}
	                    		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("FLAT"))
	                    		{	
	                    			if(viewDOB.getMultiBreakPoints()[t].length()>7)
	                    				breakpt= "FLAT"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
	                    			//breakpt= "FLAT";
	                    			
	                    			else	                    				
	                    			breakpt = "FLAT";
	                    		}
	                    		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("ABSOLUTE"))
	                    		{	
	                    			if(viewDOB.getMultiBreakPoints()[t].length()>10)
	                    				breakpt= "ABSOLUTE"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
	                    			//breakpt= "ABSOLUTE";
	                    			else	                    				
	                    			breakpt = "ABSOLUTE";
	                    		}
	                    		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("PERCENT"))
	                    		{	
	                    			if(viewDOB.getMultiBreakPoints()[t].length()>7)
	                    				breakpt= "PERCENT"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
	                    			//breakpt= "PERCENT";
	                    			else	                    				
	                    			breakpt = "PERCENT";
	                    		}
	                    		else
	                    			breakpt = viewDOB.getMultiBreakPoints()[t]!= null ? ( viewDOB.getMultiBreakPoints()[t].length() >5 ? viewDOB.getMultiBreakPoints()[t].substring(0,4)+"\n"+viewDOB.getMultiBreakPoints()[t].substring(4,viewDOB.getMultiBreakPoints()[t].length()-2):viewDOB.getMultiBreakPoints()[t]):"";
            							
	                    //chk = new Chunk(breakpt+"\n"+viewDOB.getBasis().split(",")[t],FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	                    			chk = new Chunk(breakpt,FontFactory.getFont("Courier-Bold ",7, Font.BOLD,Color.BLACK));	
	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                    chargeCountry2.addCell(cell2);
  	                    		  
            					}
            						}

	                    chk = new Chunk("Density Ratio",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry2.addCell(cell2);
		    	                    		  
	                  					}
	                    
	                    //added by silpa.p on 16-05-11 for adding new line for pdf
	                    if(freightChargesSize>0)
	                    {
	                    	
	                    chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry2.addCell(cell2);
		                
   	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry2.addCell(cell2);
		                chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
 	                    cell2.setBorder(Rectangle.BOTTOM);
   	    				cell2.setCellEvent(border); 
						//@@ Modified by kiran.v on 28/08/2011 for Wpbn Issue -258778
   	    				// 09/11/2011
   	    			if("add".equalsIgnoreCase(operation)||"modify".equalsIgnoreCase(operation)||"Copy".equalsIgnoreCase(operation)){	
   	    			 if("checked".equalsIgnoreCase(carrierChecked)){//added by silpa.p on 14-05-11
 	                    chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
 	                    cell2 = new PdfPCell(new Phrase(chk));
  	                    cell2.setBorder(Rectangle.BOTTOM);
    	    				cell2.setCellEvent(border); 
    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
 	                    chargeCountry2.addCell(cell2);
 	                    }
 	                    
 	                    
 	                    if("checked".equalsIgnoreCase(serviceLevelChecked)){//added by silpa.p on 14-05-11
 	                    chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
 	                    cell2 = new PdfPCell(new Phrase(chk));
  	                    cell2.setBorder(Rectangle.BOTTOM);
    	    				cell2.setCellEvent(border); 
    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
 	                    chargeCountry2.addCell(cell2);              
 	                    
 	                    }
 	                    if("checked".equalsIgnoreCase(frequencyChecked)){//added by silpa.p on 16-05-11
 	                    chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
 	                    cell2 = new PdfPCell(new Phrase(chk));
  	                    cell2.setBorder(Rectangle.BOTTOM);
    	    				cell2.setCellEvent(border); 
    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
 	                    chargeCountry2.addCell(cell2);      
 	                    }
 	                 //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
 	                   if("checked".equalsIgnoreCase(transitTimeChecked)){
 	 	                    chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
 	 	                    cell2 = new PdfPCell(new Phrase(chk));
 	  	                    cell2.setBorder(Rectangle.BOTTOM);
 	    	    				cell2.setCellEvent(border); 
 	    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
 	 	                    chargeCountry2.addCell(cell2);      
 	 	                    }
 	                  if("checked".equalsIgnoreCase(validityChecked)){
	 	                    chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	 	                    cell2 = new PdfPCell(new Phrase(chk));
	  	                    cell2.setBorder(Rectangle.BOTTOM);
	    	    				cell2.setCellEvent(border); 
	    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	 	                    chargeCountry2.addCell(cell2);      
 	                    }
					}
					else{
   	    				 if("Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getCarrierChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getCarrierChecked():"")){//added by silpa.p on 14-05-11
   	 	                    chk = new Chunk(" ",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
   	 	                    cell2 = new PdfPCell(new Phrase(chk));
   	  	                    cell2.setBorder(Rectangle.BOTTOM);
   	    	    				cell2.setCellEvent(border); 
   	    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
   	 	                    chargeCountry2.addCell(cell2);
   	 	                    }
   	 	                    
   	 	                    
   	 	                    if( "Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getServiceChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getServiceChecked():"")){//added by silpa.p on 14-05-11
   	 	                    chk = new Chunk(" ",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
   	 	                    cell2 = new PdfPCell(new Phrase(chk));
   	  	                    cell2.setBorder(Rectangle.BOTTOM);
   	    	    				cell2.setCellEvent(border); 
   	    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
   	 	                    chargeCountry2.addCell(cell2);              
 	                    
   	 	                    }
   	 	                    if("Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getFrequencyChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getFrequencyChecked():"")){//added by silpa.p on 16-05-11
   	 	                    chk = new Chunk(" ",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
   	 	                    cell2 = new PdfPCell(new Phrase(chk));
   	  	                    cell2.setBorder(Rectangle.BOTTOM);
   	    	    				cell2.setCellEvent(border); 
   	    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
   	 	                    chargeCountry2.addCell(cell2);      
   	 	                    }	
   	 	                //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
   	 	                if("Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getTransitTimeChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getTransitTimeChecked():"")){
   	 	                    chk = new Chunk(" ",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
   	 	                    cell2 = new PdfPCell(new Phrase(chk));
   	  	                    cell2.setBorder(Rectangle.BOTTOM);
   	    	    				cell2.setCellEvent(border); 
   	    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
   	 	                    chargeCountry2.addCell(cell2);      
   	 	                    }
   	 	            if("Y".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getRateValidityChecked():"N")||"on".equalsIgnoreCase(multiQuoteChargeInfo!=null?multiQuoteChargeInfo.getRateValidityChecked():"")){
	 	                    chk = new Chunk(" ",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	 	                    cell2 = new PdfPCell(new Phrase(chk));
	  	                    cell2.setBorder(Rectangle.BOTTOM);
	    	    				cell2.setCellEvent(border); 
	    	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	 	                    chargeCountry2.addCell(cell2);      
   	 	                    }	
   	    				}
 	                   chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
	                    cell2.setBorder(Rectangle.BOTTOM);
  	    				cell2.setCellEvent(border); 
  	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry2.addCell(cell2);
		              //added by silpa.p on 23-06-11 for currency add
		                chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
	                    cell2.setBorder(Rectangle.BOTTOM);
  	    				cell2.setCellEvent(border); 
  	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry2.addCell(cell2);//ended
 	                    
	                    
	                    
	                    if("Add".equalsIgnoreCase(request.getParameter("Operation"))||"Modify".equalsIgnoreCase(request.getParameter("Operation"))||"Copy".equalsIgnoreCase(request.getParameter("Operation")) )
	                      { 
	                    	for(int brk1=0;brk1<tmpBrkptsize;brk1++)
	                        {
	                      
	                    		if(tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("MIN"))
	                        {
	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>7)
	                    				breakpt = "MIN"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    			//breakpt = "MIN";
	                          else
	                    			breakpt = "MIN";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("BASIC"))
	                        {
                     
	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>7)
	                    				breakpt = "BASIC"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    				//breakpt = "BASIC";
	                    			else
	                    			breakpt = "BASIC";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("FLAT"))
	                        {

	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>7)
	                    				breakpt = "FLAT"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    					//breakpt ="FLAT";
	                    			else
	                    			breakpt = "FLAT";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("ABSOLUTE"))
	                        {

	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>10)
	                    				breakpt = "ABSOLUTE"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    				//breakpt = "ABSOLUTE";
	                    			else
	                    			breakpt = "ABSOLUTE";
	                        }
	                    		else if (tmpmultiBrkpt.split(",")[brk1]!=null && tmpmultiBrkpt.split(",")[brk1].toUpperCase().endsWith("PERCENT"))
	                        {

	                    			if(tmpmultiBrkpt.split(",")[brk1].length()>10)
	                    				breakpt = "PERCENT"+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(0,3);
	                    			//breakpt = "PERCENT";
	                    			else
	                    			breakpt = "PERCENT";
	                        }
	                    		else if(isInteger(tmpmultiBrkpt.split(",")[brk1])|| isDouble(tmpmultiBrkpt.split(",")[brk1]))
	                        {
	                    			breakpt	=	tmpmultiBrkpt.split(",")[brk1];
	                        }
	                        else
	                    			breakpt = tmpmultiBrkpt.split(",")[brk1]!= null ? ( tmpmultiBrkpt.split(",")[brk1].length() >5 ? tmpmultiBrkpt.split(",")[brk1].substring(0,4)+"\n"+tmpmultiBrkpt.split(",")[brk1].substring(4,tmpmultiBrkpt.split(",")[brk1].length()-2):tmpmultiBrkpt.split(",")[brk1]):"";
	                    		basis =  tmpBasis.split(",")[brk1]!=null ? tmpBasis.split(",")[brk1] : "";

	                    		//chk = new Chunk(breakpt+"\n"+basis,FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
	                    		chk = new Chunk(basis,FontFactory.getFont("Courier-Bold ", 7, Font.NORMAL,Color.BLACK));
	                    		cell2 = new PdfPCell(new Phrase(chk));
	     	                    cell2.setBorder(Rectangle.BOTTOM);
	       	    				cell2.setCellEvent(border); 
	       	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                          chargeCountry2.addCell(cell2);
	                      }
	                    	
	                       }
	                    else
                   	 {
                   	for(int t=0;t<tmpBrkptsize;t++)
                   {
                   		if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("MIN"))
                   		{	
                   			if(viewDOB.getMultiBreakPoints()[t].length()>7)
                   				breakpt= "MIN"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
                   			//breakpt= "MIN";
                   			else	                    				
                   			breakpt = "MIN";
                   		}
                   		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("BASIC"))
                   		{	
                   			if(viewDOB.getMultiBreakPoints()[t].length()>7)
                   				breakpt= "BASIC"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
                   			//breakpt= "BASIC";
                   			else	                    				
                   			breakpt = "BASIC";
                   		}
                   		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("FLAT"))
                   		{	
                   			if(viewDOB.getMultiBreakPoints()[t].length()>7)
                   				breakpt= "FLAT"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
                   			//breakpt= "FLAT";
                   			
                   			else	                    				
                   			breakpt = "FLAT";
                   		}
                   		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("ABSOLUTE"))
                   		{	
                   			if(viewDOB.getMultiBreakPoints()[t].length()>10)
                   				breakpt= "ABSOLUTE"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
                   			//breakpt= "ABSOLUTE";
                   			else	                    				
                   			breakpt = "ABSOLUTE";
                   		}
                   		else if(viewDOB.getMultiBreakPoints()[t].toUpperCase().endsWith("PERCENT"))
                   		{	
                   			if(viewDOB.getMultiBreakPoints()[t].length()>7)
                   				breakpt= "PERCENT"+"\n"+viewDOB.getMultiBreakPoints()[t].substring(0,3);
                   			//breakpt= "PERCENT";
                   			else	                    				
                   			breakpt = "PERCENT";
                   		}
                   		else
                   			breakpt = viewDOB.getMultiBreakPoints()[t]!= null ? ( viewDOB.getMultiBreakPoints()[t].length() >5 ? viewDOB.getMultiBreakPoints()[t].substring(0,4)+"\n"+viewDOB.getMultiBreakPoints()[t].substring(4,viewDOB.getMultiBreakPoints()[t].length()-2):viewDOB.getMultiBreakPoints()[t]):"";
       							
                   //chk = new Chunk(breakpt+"\n"+viewDOB.getBasis().split(",")[t],FontFactory.getFont("Courier-Bold ", 8, Font.BOLD,Color.BLACK));
                   			chk = new Chunk(viewDOB.getBasis().split(",")[t],FontFactory.getFont("Courier-Bold ", 7, Font.NORMAL,Color.BLACK));	
                   cell2 = new PdfPCell(new Phrase(chk));
                    cell2.setBorder(Rectangle.BOTTOM);
	    				cell2.setCellEvent(border); 
	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry2.addCell(cell2);
		    	                    		  
	                  					}
       						}
	                    chk = new Chunk("",FontFactory.getFont("Courier-Bold ", 7, Font.BOLD,Color.BLACK));
	                    cell2 = new PdfPCell(new Phrase(chk));
	                    cell2.setBorder(Rectangle.BOTTOM);
  	    				cell2.setCellEvent(border); 
  	    				cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry2.addCell(cell2);
 	                    
	                    }//ended
	                    if("View".equalsIgnoreCase(request.getParameter("Operation")))
	                    	freightChargesSize = chargesInfolist.size();
	                    for(int j=0;j<freightChargesSize;j++)
            					{
	                    	if("View".equalsIgnoreCase(request.getParameter("Operation")))
	                    	{
	                    		multiQuoteChargeInfo = (MultiQuoteChargeInfo)chargesInfolist.get(j);
	                    		// multiQuoteChargeInfo			= (MultiQuoteChargeInfo)freightCharges.get(j);
	                    	}
	                    	else
	                      multiQuoteChargeInfo			= (MultiQuoteChargeInfo)freightCharges.get(j);
	                      logger.info("Freight Charges doPDFGeneration::"+j+":"+chargesDOB); // newly added

	                      		home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
	                      		remote      = home.create();
	                      			//orgDesc = remote.getLocationName(multiQuoteChargeInfo.getOrginLoc());//Commented by silpa.p on 5-07-11
	                      orgDesc =multiQuoteChargeInfo.getOrginLoc();//modified by silpa.p on 5-07-11
	                      		  chk = new Chunk(orgDesc!=null?orgDesc:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      		cell2 = new PdfPCell(new Phrase(chk));
	     	                    cell2.setBorder(Rectangle.BOTTOM);
	       	    				cell2.setCellEvent(border); 
		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry2.addCell(cell2);
	                    	  
		                          	//destDesc = remote.getLocationName(multiQuoteChargeInfo.getDestLoc());//commented by silpa.p on 5-07-11
		                          destDesc =multiQuoteChargeInfo.getDestLoc();//modified by silpa.p on 5-07-11
		                          	
	                    		  chk = new Chunk(destDesc!=null?destDesc:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
		                          	cell2 = new PdfPCell(new Phrase(chk));
		     	                    cell2.setBorder(Rectangle.BOTTOM);
		       	    				cell2.setCellEvent(border); 
		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry2.addCell(cell2);

		                         // tmpCarrier1 = multiQuoteChargeInfo.getCarrier(); Commented by Gowtham
		                         /* if("View".equalsIgnoreCase(request.getParameter("Operation"))&& ("C".equalsIgnoreCase(CarrierChecked)))
		                          {	  
		                          chk = new Chunk(multiQuoteChargeInfo.getCarrier()!=null?multiQuoteChargeInfo.getCarrier():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
		                          cell = new PdfPCell(new Phrase(chk));
		     	                    cell.setBorder(Rectangle.BOTTOM);
		       	    				cell.setCellEvent(border); 
		                          cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry.addCell(cell);
		       	    				
		                          }
		                          else
		                          {
		                        	  if("C".equalsIgnoreCase(CarrierChecked)){
		                        	  chk = new Chunk(multiQuoteChargeInfo.getCarrierName()!=null?multiQuoteChargeInfo.getCarrierName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
		                        	  cell = new PdfPCell(new Phrase(chk));
			     	                    cell.setBorder(Rectangle.BOTTOM);
			       	    				cell.setCellEvent(border); 
			                          cell.setHorizontalAlignment(cell.ALIGN_CENTER);
			                          chargeCountry.addCell(cell);
		                        	  }
		                          }*/ //carrierDesc = remote.getServiceLevelName(multiQuoteChargeInfo.getServiceLevel());
		                        //@@Added by kiran.v on 01/08/2011 for Wpbn Issue 258778
		                          carrierDesc = multiQuoteChargeInfo.getCarrier();
		                          Cd.add(carrierDesc);
		                          String carrierName=remote.getCarrierName(multiQuoteChargeInfo.getCarrier());		          
		                          Cdn.add(carrierName);
		                          //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
		                          String   time=multiQuoteChargeInfo.getTransitTime();
		                          //Cdn.add(multiQuoteChargeInfo.getCarrierName());
		                          // carrierDesc = remote.getCarrierName(multiQuoteChargeInfo.getCarrier());//added by silpa.p on 16-05-11
		                          if("View".equalsIgnoreCase(request.getParameter("Operation")))
		                        	  //chk = new Chunk(multiQuoteChargeInfo.getCarrierName()!=null?multiQuoteChargeInfo.getCarrierName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
			                          chk = new Chunk(carrierDesc !=null?carrierDesc :"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//modified by silpa.p on 16-05-11
		                          //@@Modified by kiran on 10/08/2011 for WPBN Issue-258778
		                          // 09/11/2011
		                          if("add".equalsIgnoreCase(operation)||"modify".equalsIgnoreCase(operation)||"Copy".equalsIgnoreCase(operation)){
		                          if("checked".equalsIgnoreCase(carrierChecked)){//added by silpa.p on 14-05-11		                      
			                        	// chk = new Chunk(multiQuoteChargeInfo.getCarrier()!=null?multiQuoteChargeInfo.getCarrier():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
			                        chk = new Chunk(carrierDesc!=null?carrierDesc:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
		                          	cell2 = new PdfPCell(new Phrase(chk));
		     	                    cell2.setBorder(Rectangle.BOTTOM);
		       	    				cell2.setCellEvent(border); 
		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry2.addCell(cell2);
			                        	  }//ended
		                        //@@Added by kiran.v on 01/08/2011 for Wpbn Issue 258778
		                          serviceLevelDesc=multiQuoteChargeInfo.getServiceLevel();
		       	    				
		                          Sd.add(serviceLevelDesc);
		                         // serviceLevelDesc = remote.getServiceLevelName(multiQuoteChargeInfo.getServiceLevel());//added by silpa.p on 16-05-11
		                          //tmpServl1=multiQuoteChargeInfo.getServiceLevel(); Commented by Gowtham
		                          if("checked".equalsIgnoreCase(serviceLevelChecked)){//added by silpa.p on 14-05-11
		                        	  //chk = new Chunk(multiQuoteChargeInfo.getServiceLevel()!=null?multiQuoteChargeInfo.getServiceLevel():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
		                          chk = new Chunk(serviceLevelDesc!=null?serviceLevelDesc:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//modified by silpa.p on 16-05-11
		                          cell2 = new PdfPCell(new Phrase(chk));
		     	                  cell2.setBorder(Rectangle.BOTTOM);
		       	    			  cell2.setCellEvent(border); 
		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry2.addCell(cell2);	  
		                          }
		                          if("checked".equalsIgnoreCase(frequencyChecked)){//added by silpa.p on 16-05-11
		                          tmpFrCnt = multiQuoteChargeInfo.getFrequency()!=null?multiQuoteChargeInfo.getFrequency().split(",").length:0;
		                          if(tmpFrCnt>1)
		                          {
		                        	  for(int tmpFc=0;tmpFc<tmpFrCnt;tmpFc++)
		  	    		       {
		                        		  if("1".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
		                        			  	tmpFreq ="Mon";
		                        		  else if("2".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
		                        			  	tmpFreq = "Tue";
		                        		  else if("3".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
		                        			  	tmpFreq = "Wed";
		                        		  else if("4".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
		                        			  	tmpFreq = "Thr";
		                        		  else if("5".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
		                        			  	tmpFreq = "Fri";
		                        		  else if("6".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
		                        			  	tmpFreq = "Sat";
		                        		  else if("7".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
		                        			  	tmpFreq = "Sun";
		                        		  if(tmpFc==tmpFrCnt-1)
		                        			  tmpFreq1 = tmpFreq1 + tmpFreq;
		                        		  else
		                        			  tmpFreq1 = tmpFreq1+tmpFreq+",";  
	                  			 }
	                  			 }
		                          else if(multiQuoteChargeInfo.getFrequency()!=null && isInteger(multiQuoteChargeInfo.getFrequency()))
	          			{
		                        	  if("1".equals(multiQuoteChargeInfo.getFrequency()))
	                        			  tmpFreq1 ="Mon";
	                        		  else if("2".equals(multiQuoteChargeInfo.getFrequency()))
	                        			  tmpFreq1 = "Tue";
	                        		  else if("3".equals(multiQuoteChargeInfo.getFrequency()))
	                        			  tmpFreq1 = "Wed";
	                        		  else if("4".equals(multiQuoteChargeInfo.getFrequency()))
	                        			  tmpFreq1 = "Thr";
	                        		  else if("5".equals(multiQuoteChargeInfo.getFrequency()))
	                        			  tmpFreq1 = "Fri";
	                        		  else if("6".equals(multiQuoteChargeInfo.getFrequency()))
	                        			  tmpFreq1 = "Sat";
	                        		  else if("7".equals(multiQuoteChargeInfo.getFrequency()))
	                        			  tmpFreq1 = "Sun";
	                  					  		}
	                  					  	else
		                        	  tmpFreq1 = multiQuoteChargeInfo.getFrequency()!=null ?multiQuoteChargeInfo.getFrequency():"" ;
	                  	  
		                          chk = new Chunk(tmpFreq1!=null? tmpFreq1:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
		                          cell2 = new PdfPCell(new Phrase(chk));
		     	                  cell2.setBorder(Rectangle.BOTTOM);
		       	    			  cell2.setCellEvent(border); 
	        		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	        		                          chargeCountry2.addCell(cell2);
		                          }
		                          //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
		                          String   time1=multiQuoteChargeInfo.getTransitTime();
		                          String validity =multiQuoteChargeInfo.getRateValidity();			                    
		                          if("checked".equalsIgnoreCase(transitTimeChecked)){
		                        	  //chk = new Chunk(multiQuoteChargeInfo.getServiceLevel()!=null?multiQuoteChargeInfo.getServiceLevel():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
		                          chk = new Chunk(time1!=null?time1:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
		                          cell2 = new PdfPCell(new Phrase(chk));
		     	                  cell2.setBorder(Rectangle.BOTTOM);
		       	    			  cell2.setCellEvent(border); 
		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry2.addCell(cell2);	  
		                          }
		       	    		 if("checked".equalsIgnoreCase(validityChecked)){
	                        	  //chk = new Chunk(multiQuoteChargeInfo.getServiceLevel()!=null?multiQuoteChargeInfo.getServiceLevel():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	                          chk = new Chunk(validity!=null?validity:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                          cell2 = new PdfPCell(new Phrase(chk));
	     	                  cell2.setBorder(Rectangle.BOTTOM);
	       	    			  cell2.setCellEvent(border); 
	                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                          chargeCountry2.addCell(cell2);	  
		                          }
			                      }
			                      else{
			                    	  if("checked".equalsIgnoreCase(carrierChecked)||"Y".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getCarrierChecked()) ||carrierFlag){//added by silpa.p on 14-05-11		                      
				                        	// chk = new Chunk(multiQuoteChargeInfo.getCarrier()!=null?multiQuoteChargeInfo.getCarrier():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
				                        chk = new Chunk(carrierDesc!=null?carrierDesc:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
				                        cell2 = new PdfPCell(new Phrase(chk));
			     	                    cell2.setBorder(Rectangle.BOTTOM);
			       	    				cell2.setCellEvent(border); 
			                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
			                          chargeCountry2.addCell(cell2);
		                          }//ended
			                        //@@Added by kiran.v on 01/08/2011 for Wpbn Issue 258778
			                          serviceLevelDesc=multiQuoteChargeInfo.getServiceLevel();
			                          
			                          Sd.add(serviceLevelDesc);
			                         // serviceLevelDesc = remote.getServiceLevelName(multiQuoteChargeInfo.getServiceLevel());//added by silpa.p on 16-05-11
			                          //tmpServl1=multiQuoteChargeInfo.getServiceLevel(); Commented by Gowtham                     
			                          if("checked".equalsIgnoreCase(serviceLevelChecked)||"Y".equalsIgnoreCase(multiQuoteChargeInfo.getServiceChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getServiceChecked())|| serviceflag){//added by silpa.p on 14-05-11
			                        	  //chk = new Chunk(multiQuoteChargeInfo.getServiceLevel()!=null?multiQuoteChargeInfo.getServiceLevel():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
			                          chk = new Chunk(serviceLevelDesc!=null?serviceLevelDesc:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//modified by silpa.p on 16-05-11
			                          cell2 = new PdfPCell(new Phrase(chk));
			     	                  cell2.setBorder(Rectangle.BOTTOM);
			       	    			  cell2.setCellEvent(border); 
			                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
			                          chargeCountry2.addCell(cell2);	  
			                          }
			                          if("checked".equalsIgnoreCase(frequencyChecked)||"Y".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getFrequencyChecked())||frequencyflag){//added by silpa.p on 16-05-11
			                          tmpFrCnt = multiQuoteChargeInfo.getFrequency()!=null?multiQuoteChargeInfo.getFrequency().split(",").length:0;
			                          if(tmpFrCnt>1)
			                          {
			                        	  for(int tmpFc=0;tmpFc<tmpFrCnt;tmpFc++)
			  	    		       {
			                        		  if("1".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
			                        			  	tmpFreq ="Mon";
			                        		  else if("2".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
			                        			  	tmpFreq = "Tue";
			                        		  else if("3".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
			                        			  	tmpFreq = "Wed";
			                        		  else if("4".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
			                        			  	tmpFreq = "Thr";
			                        		  else if("5".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
			                        			  	tmpFreq = "Fri";
			                        		  else if("6".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
			                        			  	tmpFreq = "Sat";
			                        		  else if("7".equals(multiQuoteChargeInfo.getFrequency().split(",")[tmpFc]))
			                        			  	tmpFreq = "Sun";
			                        		  if(tmpFc==tmpFrCnt-1)
			                        			  tmpFreq1 = tmpFreq1 + tmpFreq;
			                        		  else
			                        			  tmpFreq1 = tmpFreq1+tmpFreq+",";  
		                  			 }
		                  			 }
			                          else if(multiQuoteChargeInfo.getFrequency()!=null && isInteger(multiQuoteChargeInfo.getFrequency()))
		          			{
			                        	  if("1".equals(multiQuoteChargeInfo.getFrequency()))
		                        			  tmpFreq1 ="Mon";
		                        		  else if("2".equals(multiQuoteChargeInfo.getFrequency()))
		                        			  tmpFreq1 = "Tue";
		                        		  else if("3".equals(multiQuoteChargeInfo.getFrequency()))
		                        			  tmpFreq1 = "Wed";
		                        		  else if("4".equals(multiQuoteChargeInfo.getFrequency()))
		                        			  tmpFreq1 = "Thr";
		                        		  else if("5".equals(multiQuoteChargeInfo.getFrequency()))
		                        			  tmpFreq1 = "Fri";
		                        		  else if("6".equals(multiQuoteChargeInfo.getFrequency()))
		                        			  tmpFreq1 = "Sat";
		                        		  else if("7".equals(multiQuoteChargeInfo.getFrequency()))
		                        			  tmpFreq1 = "Sun";
		                  					  		}
		                  					  	else
			                        	  tmpFreq1 = multiQuoteChargeInfo.getFrequency()!=null ?multiQuoteChargeInfo.getFrequency():"" ;
		                  	  
			                          chk = new Chunk(tmpFreq1!=null? tmpFreq1:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
			                          cell2 = new PdfPCell(new Phrase(chk));
			     	                  cell2.setBorder(Rectangle.BOTTOM);
			       	    			  cell2.setCellEvent(border); 
		        		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		        		                          chargeCountry2.addCell(cell2);
			                          }
			                          //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
				                       String   time1=multiQuoteChargeInfo.getTransitTime();
				                       if("checked".equalsIgnoreCase(transitTimeChecked)||"Y".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getTransitTimeChecked())|| transittimeflag ){
				                        	  //chk = new Chunk(multiQuoteChargeInfo.getServiceLevel()!=null?multiQuoteChargeInfo.getServiceLevel():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
				                          chk = new Chunk(time1!=null?time1:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
				                          cell2 = new PdfPCell(new Phrase(chk));
				     	                  cell2.setBorder(Rectangle.BOTTOM);
				       	    			  cell2.setCellEvent(border); 
				                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
				                          chargeCountry2.addCell(cell2);	  
				                          }
				                       String   validity=multiQuoteChargeInfo.getRateValidity();
				                     
				                       if("checked".equalsIgnoreCase(validityChecked)||"Y".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked())||"on".equalsIgnoreCase(multiQuoteChargeInfo.getRateValidityChecked())|| freightValidity ){
				                        	  //chk = new Chunk(multiQuoteChargeInfo.getServiceLevel()!=null?multiQuoteChargeInfo.getServiceLevel():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
				                          chk = new Chunk(validity!=null?validity:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
				                          cell2 = new PdfPCell(new Phrase(chk));
				     	                  cell2.setBorder(Rectangle.BOTTOM);
				       	    			  cell2.setCellEvent(border); 
				                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
				                          chargeCountry2.addCell(cell2);	  
			                          }
			                      }
		                          //@@Ended by kiran.v
		                          chk = new Chunk((multiQuoteChargeInfo.getIncoTerms()!=null?multiQuoteChargeInfo.getIncoTerms():""),FontFactory.getFont("ARIAL",7, Font.NORMAL,Color.BLACK));
		                          cell2 = new PdfPCell(new Phrase(chk));
		     	                  cell2.setBorder(Rectangle.BOTTOM);
		       	    			  cell2.setCellEvent(border); 
		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry2.addCell(cell2);
		                        //added by silpa.p on 23-06-11 for currency add
		                          if("View".equalsIgnoreCase(request.getParameter("Operation"))){
		                        	  chk = new Chunk((multiQuoteChargeInfo.getCurrency()!=null?multiQuoteChargeInfo.getCurrency():""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
		                          }
		                        	  else{
		                          chk = new Chunk((multiQuoteChargeInfo.getCurrency()!=null?multiQuoteChargeInfo.getCurrency():""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
		                        	  }
		                          cell2 = new PdfPCell(new Phrase(chk));
		     	                  cell2.setBorder(Rectangle.BOTTOM);
		       	    			  cell2.setCellEvent(border); 
		                          cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
		                          chargeCountry2.addCell(cell2);//ended
	          
		                          for(int trc=0;trc<tmpBrkptsize;trc++)
	                    {
		                        	  if("View".equalsIgnoreCase(request.getParameter("Operation")))
		                        	  {
		                        		rates1 = Double.parseDouble(multiQuoteChargeInfo.getMultiBuyRates()[trc]);
		                        	  	rates = ("0.00".equals(rates1.toString())||"0".equals(rates1.toString()))?"-":rates1.toString();
		                        	  }
		                        	 else
		                        	  rates = multiQuoteChargeInfo.getMultiCalSellRates()[tmpBrksize[trc]]!=null ? ("0.00".equals(multiQuoteChargeInfo.getMultiCalSellRates()[tmpBrksize[trc]]) ||("0".equals(multiQuoteChargeInfo.getMultiCalSellRates()[tmpBrksize[trc]]))? "-" : multiQuoteChargeInfo.getMultiCalSellRates()[tmpBrksize[trc]]):"0.00";//modified by silpa.p on 3-06-11
		                        
		                          chk = new Chunk(rates,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
		                          cell2 = new PdfPCell(new Phrase(chk));
		     	                  cell2.setBorder(Rectangle.BOTTOM);
		       	    			  cell2.setCellEvent(border); 
		       	    			  cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                      chargeCountry2.addCell(cell2);
		                          }
	                      
	                      
	                      chk = new Chunk(multiQuoteChargeInfo.getRatio()!=null?"1:"+multiQuoteChargeInfo.getRatio():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      cell2 = new PdfPCell(new Phrase(chk));
     	                  cell2.setBorder(Rectangle.BOTTOM);
       	    			  cell2.setCellEvent(border); 
	                      cell2.setHorizontalAlignment(cell.ALIGN_CENTER);
	                      chargeCountry2.addCell(cell2);  
	                      
	                      tmpFreq1 = "" ; 
	                      }
	                  		 //02Mar11
	                    }
	  
	                  document.add(chargeCountry2);
	             
	                } //end of if(charge)
	                //document.newPage();
	                
	    }  // Added by Gowtham. to skip in case of quote with charges.
	              
	              pTable = new PdfPTable(1);
	              pTable.setSpacingAfter(20f);
	              pCell	 = new PdfPCell(new Phrase(new Chunk("")));
	              pCell.setBorder(0);
	              pTable.addCell(pCell);
	              
	              document.add(pTable); 

	                ArrayList  destChargeInfo             =  null;
		            //Destination
	                MultiQuoteChargeInfo chargeInfo1        = null;
		            ArrayList destLaneCharges  =finalDOB.getLegDetails();//.get(0)).getOriginChargesList();
		            
		            
		            for(int dc=0;dc<noOfLanes;dc++)      
		            {
		            	legDestCharges = (MultiQuoteFreightLegSellRates) destLaneCharges.get(dc);
		            	ArrayList destCharges = legDestCharges.getDestChargesList(); 	
		            	int[]      destIndices		= legDestCharges.getSelctedDestChargesListIndices();
			            int      destChargesSize = 0;
			            String breakPoint ="";
			            if(destIndices!=null)
			              destChargesSize	= destIndices.length;
			            else
			              destChargesSize	= 0;
		           
	                int        destChargesInfoSize        =  0;
	                if(destChargesSize>0)
	                {          //document.newPage();  
	                	//document.add(horizontalLine);
	                	chargeTitle = new Table(1);
		            	chargeTitle.setWidth(100);
		            	chargeTitle.setPadding(1);
		            	chargeTitle.setSpacing(1);
		            	chargeTitle.setBorderColor(Color.BLACK);
		            	chargeTitle.setDefaultHorizontalAlignment(Element.ALIGN_LEFT);
		            	chargeTitle.setBackgroundColor(Color.ORANGE);//modified by silpa.p on 3-06-11
		            	chargeTitle.setBorderWidth(1f);
		            	
		            	
		            	home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
                		remote      = home.create();
	                    if(legOrginCharges.getShipmentMode()==2 && !"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))//added by silpa.p on 16-06-11
	                    {	 	
		                	chk = new Chunk((legDestCharges.getDestination()!=null?remote.getPortName(legDestCharges.getDestination()):"")+"-Destination Charges",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK)); // Added by Gowtham on 01-Feb-2011.	
	                    }
		                	else
		                chk = new Chunk((legDestCharges.getDestination()!=null ? remote.getLocationName(legDestCharges.getDestination()):"")+"-Destination Charges",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));// Added by Gowtham on 01-Feb-2011.
		                cell1 = new Cell(chk);

		                cell1.setLeading(10.0f);
		                cell1.setBackgroundColor(Color.WHITE);                
		                cell1.setHeader(true);
		                cell1.setBorder(0);
		                cell1.setBackgroundColor(Color.ORANGE);//modified by silpa.p on 3-06-11
		                chargeTitle.addCell(cell1);
		                //document.newPage();
		                document.add(chargeTitle);
		                
		                //document.add(horizontalLine);
	                	
	                   /* chargeCountry1  = new Table(6);
	                    chargeCountry1.setWidth(100);
	                    chargeCountry1.setWidths(cellWidths);
	                    chargeCountry1.setPadding(1);
	                    chargeCountry1.setSpacing(0);
	                    chargeCountry1.setOffset(5);
	                    chargeCountry1.setBackgroundColor(Color.WHITE);
	                    chargeCountry1.setBorderColor(Color.WHITE);
	                    chargeCountry1.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
	                    chargeCountry1.setBorderWidth(1f);
	                    chargeCountry1.setTableFitsPage(true);*/
	                    
		            	chargeTitle.setWidth(100);		 
		            	chargeTitle.setBorderWidth(1f);
	                    
	                    chargeCountry  = new Table(6);
		                chargeCountry.setWidths(cellWidths);
		                //chargeCountry.setWidthPercentage(100);
		                chargeCountry.setPadding(3);
		                chargeCountry.setSpacing(0);
	                    chargeCountry.setWidths(cellWidths);
	                    chargeCountry.setBorder(1);
	                    chargeCountry.setWidth(100);		 
	                    chargeCountry.setBorderWidth(1f);
	                   /* home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");//146460
                		remote      = home.create();
	                    if(legOrginCharges.getShipmentMode()==2)
	                    {	 	
		                	chk = new Chunk((legDestCharges.getDestination()!=null?remote.getPortName(legDestCharges.getDestination()):"")+"-Destination Charges",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)); // Added by Gowtham on 01-Feb-2011.	
	                    }
		                	else
		                chk = new Chunk((legDestCharges.getDestination()!=null ? remote.getLocationName(legDestCharges.getDestination()):"")+"-Destination Charges",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));// Added by Gowtham on 01-Feb-2011.
		                cell = new Cell(chk);

		                cell.setLeading(10.0f);
		                cell.setBackgroundColor(Color.ORANGE);                
		                cell.setHeader(true);
		                chargeCountry.addCell(cell);*/
	                    chk = new Chunk("Charge Description",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
		                cell = new Cell(new Phrase(chk));
		                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
		                ////cell.setBorder(Rectangle.BOTTOM);
		                cell.setBorderWidth(1);	                
		                chargeCountry.addCell(cell);
	                    
		                chk = new Chunk("Weight Break Slab",FontFactory.getFont("ARIAL",7, Font.BOLD,Color.BLACK));
		                cell = new Cell(new Phrase(chk));
		                ////cell.setBorder(Rectangle.BOTTOM);
		                cell.setBorderWidth(1);	                
		                cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry.addCell(cell);


		                chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
		                cell = new Cell(new Phrase(chk));
		                ////cell.setBorder(Rectangle.BOTTOM);
		                cell.setBorderWidth(1);	                
		                cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry.addCell(cell);


		                chk = new Chunk("Charge Rate",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
		                cell = new Cell(new Phrase(chk));
		                ////cell.setBorder(Rectangle.BOTTOM);
		                cell.setBorderWidth(1);	                
		                cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry.addCell(cell);
		    			
		    			
		                chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
		                cell = new Cell(new Phrase(chk));
		                ////cell.setBorder(Rectangle.BOTTOM);
		                cell.setBorderWidth(1);	                
		                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
		                chargeCountry.addCell(cell);
		    			
		    			
		                chk = new Chunk("Density Ratio",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
		                cell = new Cell(new Phrase(chk));
		                ////cell.setBorder(Rectangle.BOTTOM);
		                cell.setBorderWidth(1);	                
		                cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		                chargeCountry.addCell(cell);
		                
		    			
		                
	           for(int dcc = 0;dcc<destChargesSize;dcc++) 
	           {     
	        	   if((operation!=null && operation.equals("View"))|| ("Y".equalsIgnoreCase(legDestCharges.getDestChargesSelectedFlag()[destIndices[dcc]])))//modified by silpa.p on 21-06-11
	            	{
	             
	                	chargesDOB				    = (MultiQuoteCharges)destCharges.get(destIndices[dcc]);
	                
	                  //  for(int j=0;j<destChargesSize;j++)
	                    //{
	                      if(destIndices[dcc] !=-1)
	                     {
	                     // chargesDOB				= (MultiQuoteCharges)destCharges.get(destIndices[j]);
	                      logger.info("Destination Charges doPDFGeneration::"+dcc+":"+chargesDOB); // newly added                      
	                      destChargeInfo			= chargesDOB.getChargeInfoList();
	                      destChargesInfoSize		= destChargeInfo.size();
	                      for(int k=0;k<destChargesInfoSize;k++)
	                      {
	                        chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);
	                        // if(k==destChargesInfoSize/2 )
	                        if(k==0)
	                        {
	                          if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
	                            chk = new Chunk(chargesDOB.getExternalName()!=null?chargesDOB.getExternalName():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                          else
	                            chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                          
	                          /*cell1 = new Cell(chk);
	                          cell1.setLeading(9.0f);
	                          //cell.setRowspan(destChargesInfoSize);
	                          //cell.setBorderWidth(0);
	                          //cell.setBackgroundColor(Color.lightGray);
	                          cell1.setBorder(0);
	                          cell1.setHeader(true);       
	                          cell1.setHorizontalAlignment(cell1.ALIGN_LEFT);
	                          chargeCountry1.addCell(cell1)*/;
	                          
	                          cell =  new Cell(new Phrase(chk));
	                          cell.setLeading(7.0f);
	                          cell.setBorder(1);
	                          cell.setRowspan(destChargesInfoSize);	
	                          cell.setBorderWidthTop(0);
		                      cell.setBorderWidthBottom(0.1f);
	                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
		                      cell.setVerticalAlignment(cell.ALIGN_LEFT);      
		                      
	                          if(dcc==destChargesSize-1 && k==destChargesInfoSize-1 ){
	                        	cell.setBorderWidthBottom(0.1f);  
	                        	cell.setHorizontalAlignment(cell.ALIGN_LEFT);
	                          }
	                          else if(k==destChargesInfoSize-1){
		                    	  cell.setBorderWidthBottom(0.1f);
		                    	  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
			  	    			  
		                      }
		                      else{
		                    	  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
		                      }
	                         // cell.setBorderWidthTop(0);
		                   // cell.setHorizontalAlignment(cell.ALIGN_CENTER);
		  	    			chargeCountry.addCell(cell);
	                          
	                        }
	                        /*else if (k==destChargesInfoSize-1) 
		                    {
	  	                      cell = new Cell(new Phrase(new Chunk("")));
	  	                      ////cell.setBorder(Rectangle.BOTTOM);
	  	  	    			  ////cell.setCellEvent(border); 
	  	                      if(dcc!=destChargesSize-1){
	  	                    cell.setBorder(0);
		                      cell.setBorderWidth(0f);
		                      cell.setLeading(7.0f);
	  	                      }
	  	                      else
		                    {
	  	                    	cell.setBorder(1);  
	  	                    	cell.setBorderWidthTop(0);
	  	                    	cell.setBorderWidthBottom(0.1f);
	  	                    	cell.setLeading(7.0f);
	  	                    	
	  	                      }
	                          cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                          chargeCountry.addCell(cell);
	  	  	    			
	                        }
	                        else
	                        {
	                        	  cell = new Cell(new Phrase(new Chunk("")));
	                          ////cell.setBorder(0);
			  	    			  ////cell.setCellEvent(border); 
	                          cell.setBorder(0);
	                        	  cell.setLeading(7.0f);
			                      cell.setBorderWidth(0f);	                        	  
			  	    			  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                          chargeCountry.addCell(cell);
	                        }*/
	                        
	     //@@Commented by subrahmanyam for 146455 on 18/02/09                   
	                     /* chk = new Chunk((chargeInfo.getBreakPoint()!=null && !"Absolute".equalsIgnoreCase(chargeInfo.getBreakPoint()) && !"Percent".equalsIgnoreCase(chargeInfo.getBreakPoint()))?chargeInfo.getBreakPoint().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
	                      cell = new Cell(chk);cell.setLeading(8.0f);
	                      cell.setBackgroundColor(Color.lightGray);
	                      cell.setHeader(true);
	                      chargeCountry.addCell(cell);*/
	  //@@Added by subrahmanyam for 146455 on 18/02/09                        
	                       if(chargeInfo.getBreakPoint().equalsIgnoreCase("MIN"))
	                              {
	                                        breakPoint = "Min";
	                              }
	                         
	                           else if( chargeInfo.getBreakPoint().equalsIgnoreCase("FLAT"))
	                             {
	                                          breakPoint = "Flat";
	                         }
	                           else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
	                        {
	                                          breakPoint ="Absolute";
	                       } 
	                        else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
	                          {
	                                        breakPoint ="Percent";
	                        }
	                        else if(chargeInfo.getBreakPoint().equalsIgnoreCase("MAX"))
	                        {
	                              breakPoint ="Max";
	                        }
	                       else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BASE"))
	                        {
	                              breakPoint ="Base";
	                        }
	                    
	                    else
	                    {
	                                      breakPoint= chargeInfo.getBreakPoint();
	                  }
	                  
	                 // chk = new Chunk(breakPoint!=null&& !"Absolute".equalsIgnoreCase(breakPoint)&&!"Percent".equalsIgnoreCase(breakPoint)?breakPoint:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                 
	                	  chk = new Chunk(breakPoint!=null?breakPoint:"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//added by silpa.p on 16-06-11
	                      /*cell1 = new Cell(chk);cell1.setLeading(8.0f);
	                     // cell.setBackgroundColor(Color.lightGray);
	                      cell1.setHeader(true);
	                      cell1.setBorder(0);
	                        chargeCountry1.addCell(cell1);*/
	                  
	                  cell =  new Cell(new Phrase(chk));
	                  cell.setLeading(7.0f);
	                  if(k==destChargesInfoSize-1){
                    	  ////cell.setBorder(Rectangle.BOTTOM);
	  	    			  ////cell.setCellEvent(border); 
	  	    			  
                      }
                      else{
	                     // //cell.setBorder(0);
                      }
	                  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                        chargeCountry.addCell(cell);
	                  
	  	    			  
	  //@@Ended by subrahmanyam for 146455 on 18/02/09   
	                        
	                        chk = new Chunk(chargeInfo.getCurrency(),FontFactory.getFont("ARIAL",7, Font.NORMAL,Color.BLACK));
	                        cell =  new Cell(new Phrase(chk));
	                        cell.setLeading(7.0f);
	  	                    if(k==destChargesInfoSize-1){
	                      	  ////cell.setBorder(Rectangle.BOTTOM);
	  	  	    			  ////cell.setCellEvent(border); 
	  	  	    			  
	                        }
	                        else{
	                        ////cell.setBorder(0);
	                        }
	  	                  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                        chargeCountry.addCell(cell);
	                        
	                        chk = new Chunk(df.format(chargeInfo.getSellRate())+(chargeInfo.isPercentValue()?" %":""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                        cell =  new Cell(new Phrase(chk));
	                        cell.setLeading(7.0f);
	  	                    if(k==destChargesInfoSize-1){
	                      	  ////cell.setBorder(Rectangle.BOTTOM);
	  	  	    			 // //cell.setCellEvent(border); 
	  	  	    			  
	                        }
	                        else{
	                        ////cell.setBorder(0);
	                        }
	  	                  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
	                        chargeCountry.addCell(cell);
	                        
	                        chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                        cell =  new Cell(new Phrase(chk));
	                        cell.setLeading(7.0f);
	  	                    if(k==destChargesInfoSize-1){
	                      	  ////cell.setBorder(Rectangle.BOTTOM);
	  	  	    			  ////cell.setCellEvent(border); 
	  	  	    			  
	                        }
	                        else{
	                        //cell.setBorder(0);
	                        }
	                        cell.setHorizontalAlignment(cell1.ALIGN_LEFT);
	                        chargeCountry.addCell(cell); 
	                         
	                        //@@Added by Kameswari for the WPBN issue- on 12/11/08
	                      chk = new Chunk(chargeInfo.getRatio()!=null?"1:"+chargeInfo.getRatio():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
	                      cell =  new Cell(new Phrase(chk));
	                      cell.setLeading(7.0f);
	  	                    if(k==destChargesInfoSize-1){
	                      	  ////cell.setBorder(Rectangle.BOTTOM);
	  	  	    			//  //cell.setCellEvent(border); 
	  	  	    			  
	                        }
	                        else{
	                      //cell.setBorder(0);
	                        }
	                        cell.setHorizontalAlignment(cell1.ALIGN_CENTER);
	                      chargeCountry.addCell(cell);
	                      }
	                    }
						//}
	           		}
	        	   }	
	           		  document.add(chargeCountry);
	           		  pTable = new PdfPTable(1);
		              pTable.setSpacingAfter(10f);
		              pCell	 = new PdfPCell(new Phrase(new Chunk("")));
		              pCell.setBorder(0);
		              pTable.addCell(pCell);
		              
		              document.add(pTable); 
	                }
	                
	                    
	                }
	                 if(finalDOB.getExternalNotes()!=null && finalDOB.getExternalNotes().length>0)
	                {
	                    Table notes  =  new Table(1,finalDOB.getExternalNotes().length+1);
	                    notes.setWidth(100);
	                    notes.setPadding(1);
	                    notes.setSpacing(1);
	                    notes.setOffset(5);
	                    notes.setBackgroundColor(Color.WHITE);
	                    notes.setBorderColor(Color.black);
	                     //@@Commented and Modified by Kameswari for the internal issue on 08/04/09
	                   notes.setDefaultHorizontalAlignment(Element.ALIGN_LEFT);
	                    notes.setBorderWidth(1f);
	                    Cell notesCell ;
	                 
	                    chk = new Chunk("NOTES",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
	                    notesCell = new Cell(chk);
	                    notesCell.setHeader(true);notesCell.setLeading(8.0f);
	                    notesCell.setBackgroundColor(Color.ORANGE);//MODIFIED BY SILPA.P ON 3-06-11
	                    notes.addCell(notesCell);
	                    for(int i=0;i<finalDOB.getExternalNotes().length;i++)
	                    {
	                    	if(finalDOB.getExternalNotes()[i]!=null && !"".equals(finalDOB.getExternalNotes()[i].trim()))
	                    	{
	                      chk = new Chunk(finalDOB.getExternalNotes()[i]!=null?finalDOB.getExternalNotes()[i]:"",FontFactory.getFont("ARIAL",7, Font.NORMAL,Color.BLACK));
	                      notesCell = new Cell(chk);  notesCell.setLeading(8.0f);                      
	                      notes.addCell(notesCell);
	                    }
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
	                  int hFLen	=	headFoot.length;
	                  for(int i=0;i<hFLen;i++)
	                  {
	                    if(headFoot[i]!=null && "F".equalsIgnoreCase(headFoot[i]))
	                    {
	                      chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));//modified by silpa.p on 2-06-11
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
	                	//@@Modified by kiran.v on 19/09/2011 for Wpbn Issue 271485
	                if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith())){
	                	String filename  = "Abbrivation.pdf";
	                	doPdfAbbrivations(filename,finalDOB,request,response,Sd,Cd,Cdn,frequencyFlag,carrierFlag,serviceFlag,surChargesMap);
               }
 				 //@@Ended by kiran.v
	  				   PdfContentByte cb1 = writer.getDirectContent();
		                cb1.rectangle (document.left (), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
		               cb1.stroke ();
	                
	                //Jyothi
	  				     document.close();
	               
	                
	                
	                //System.out.println("After     document Close----------------------------------------->");
	                // write ByteArrayOutputStream to the ServletOutputStream            
	               // ServletOutputStream sout = response.getOutputStream();
	                //baos.writeTo(sout);
	                //System.out.println("after writer");
	               // sout.flush();
	                //dataList  = (ArrayList)  remote.sendEmail(dataList);
	               // Thread.sleep(1000);
	            
	                String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime())+masterDOB.getQuoteId();
	                file_tsmp        = file_tsmp.replaceAll("\\:","");
	                file_tsmp        = file_tsmp.replaceAll("\\.","");
	                file_tsmp        = file_tsmp.replaceAll("\\-","");
	                file_tsmp        = file_tsmp.replaceAll(" ","");
	                File f = new File("Quote.pdf");
	                FileOutputStream  fileOutputStream= new FileOutputStream(f);
	                 baos.writeTo(fileOutputStream);
	           //@@Added by Kameswari for the WPBN issue-80440
	             PdfReader reader = new PdfReader("Quote.pdf");
	            int n = reader.getNumberOfPages();
	            File fs = new File("Quote"+file_tsmp+".pdf");
	            
	            // we create a stamper that will copy the document to a new file
	            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(fs));
	        
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
	              //jyothi
					//over.rectangle(30, 30, 550, 800);
					over.rectangle (document.left(), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
					over.stroke ();
					//jyothi
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
	          
//	@@ Ended by subrahmanyam for WPBN ISSUE:146452 on 12/12/2008               
	               

	               }
	             	over.endText();
	            }
	            stamp.close();
	               //@@WPBN issue-80440    
	                 file = new File("Concatenated"+file_tsmp+".pdf");
	                  pdfFilesList.add((String)file.getName());
	                   //@@Added by Kameswari for the WPBN issue-61289
	 //@@ Added by subrahmanyam for the  WPBN ISSUE:146460 on 29/01/09             
	                if(finalDOB.getAttachmentDOBList()!=null && finalDOB.getAttachmentDOBList().size()==0 && "View".equalsIgnoreCase(request.getParameter("Operation")))
	                {
	                      
	                      home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");//146460
	                      remote      = home.create();//146460
	                       filesList  = remote.getQuoteAttachmentDtls(finalDOB);
	                      if(filesList!=null)
	                      {
	                       finalDOB.setAttachmentDOBList(filesList);
	                      }
	                }
//	@@ Ended by subrahmanyam for the WPBN ISSUE: 146460 on 29/01/09   
	                  /* if(finalDOB.getAttachmentDOBList()!=null)
	                {
	                	 //filesList = finalDOB.getAttachmentDOBList(); // commented for for 192431 on 16-dec-09
	                       //@@ Added by subrahmanyam for 192431 on 16-dec-09
	                         if("pdf".equalsIgnoreCase(request.getParameter("pdf")))
	                         {
	                           home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");	
	                           remote      = home.create();//146460
	                           filesList  = remote.getQuoteAttachmentDtls(finalDOB);
	                         }
	                        else
	                          filesList = finalDOB.getAttachmentDOBList();
	                          // ended for 192431 on 16-dec-09
	                  
	                         int fileListSize	= filesList.size();	
	                  for(int i=0;i<fileListSize;i++)
	                  {
	                       attachmentDOB = (MultiQuoteAttachmentDOB)filesList.get(i);
	                        FileOutputStream  fileStream= new FileOutputStream(attachmentDOB.getFileName());
	                        fileStream.write(attachmentDOB.getPdfFile());
	                        pdfFilesList.add((String)attachmentDOB.getFileName());
	                  }
	                }
	                       buffer = concatPDF(pdfFilesList);
	                  //@@WPBN issue-61289  
	                  
	    //@@ Commented By Subrahmanyam for enhancement 146460           
	             //  if("on".equalsIgnoreCase(request.getParameter("print")))
	                {     
	               //     request.getSession().setAttribute("QuoteOuptutStream",fs);
	               //     request.getSession().setAttribute("filepdf",buffer); //@@Added by Kameswari for the WPBN issue-61289
	              
	               
	                //  }
	      //@@ Added by subrahmanyam for the enhancement 146460
	               if("PDF".equalsIgnoreCase(request.getParameter("pdf"))||"on".equalsIgnoreCase(request.getParameter("print")))
	               {
	                  request.getSession().setAttribute("QuoteOuptutStream",fs);
	                  request.getSession().setAttribute("filepdf",buffer);
	                  }
	               */
	      //@@ Ended by subrahmanyam for the enhancement 146460
	                //f.delete();
	                //baos.close();
	                
	                String[]  contactPersons      =    masterDOB.getCustContactNames();
	                String    contactName         =    "";
	               int contactPersonsLength=0;//added by silpa.p for sent mail modifications on 24-05-11
	               boolean mailSent=false;//added by silpa.p for sent mail modifications  on 24-05-11
	                
	                String filename  = "Annexure"+file_tsmp+".pdf";
	                StringBuffer subject  = new StringBuffer("DHL Global Forwarding Quotation,");
	                String       body     = "";
	                
	                if(!finalDOB.isMultiModalQuote())
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
	                
	                subject.append((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry()[0].toUpperCase():"")).append(" to ").append((headerDOB.getDestinationCountry()[0]!=null?headerDOB.getDestinationCountry()[0].toUpperCase():""));
	                subject.append(" Quote Reference ");
	                if(finalDOB.getUpdatedReportDOB()!=null)
	                  subject.append(request.getAttribute("quoteId")).append(" Replacing ").append(finalDOB.getUpdatedReportDOB().getQuoteId());
	                else
	                  subject.append(masterDOB.getQuoteId()!=null?masterDOB.getQuoteId()+"":request.getAttribute("quoteId"));
	               
	            
                     doGenerateCartagePDF(filename,finalDOB,request,response);
                     
                     if(finalDOB.getAttachmentDOBList()!=null)
 	                {
 	                	 //filesList = finalDOB.getAttachmentDOBList(); // commented for for 192431 on 16-dec-09
 	                       //@@ Added by subrahmanyam for 192431 on 16-dec-09
 	                         if("pdf".equalsIgnoreCase(request.getParameter("pdf")))
 	                         {
 	                           home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");	
 	                           remote      = home.create();//146460
 	                           filesList  = remote.getQuoteAttachmentDtls(finalDOB);
 	                         }
 	                        else
 	                          filesList = finalDOB.getAttachmentDOBList();
 	                          // ended for 192431 on 16-dec-09
 	                  
 	                  int fileListSize	= filesList.size();	
 	                  for(int i=0;i<fileListSize;i++)
 	                  {
 	                       attachmentDOB = (MultiQuoteAttachmentDOB)filesList.get(i);
 	                        FileOutputStream  fileStream= new FileOutputStream(attachmentDOB.getFileName());
 	                        fileStream.write(attachmentDOB.getPdfFile());
 	                        pdfFilesList.add((String)attachmentDOB.getFileName());
 	                       fileStream.close();
 	                  }
 	                }
                     
                     
                     if(pdfFilesList.size()>1)
                     {
                     for(int l =1;l<pdfFilesList.size();l++)
                     {
                  	   FileInputStream inputStream = new FileInputStream((String)pdfFilesList.get(l));
                         buffer = new byte[inputStream.available()];
                         inputStream.read(buffer);
                         bufferList.add(buffer);
                         inputStream.close();
                     }
                     }
                     
                     
                     
                     
 	                  //buffer = concatPDF(pdfFilesList); 
 	                
 	               //@@WPBN issue-61289
 	               /* //@@ Commented By Subrahmanyam for enhancement 146460           
 	              /* if("on".equalsIgnoreCase(request.getParameter("print")))
 	                {     
 	               		request.getSession().setAttribute("QuoteOuptutStream",fs);
 	                    request.getSession().setAttribute("filepdf",buffer); //@@Added by Kameswari for the WPBN issue-61289
 	               }*/
 	                       
 	             
 	              //@@ Added by subrahmanyam for the enhancement 146460
 	               if("PDF".equalsIgnoreCase(request.getParameter("pdf"))||"on".equalsIgnoreCase(request.getParameter("print")))
 	               {
 	                  request.getSession().setAttribute("QuoteOuptutStream",fs);
 	                  request.getSession().setAttribute("filepdf",bufferList);
 	                  }
 	               
 	               
 	               
			       for(int dc=0;dc<noOfLanes;dc++)      
	               {
	                  legDestCharges = (MultiQuoteFreightLegSellRates) destLaneCharges.get(dc);
	               
	                try
	                {
	                  if("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEmailFlag()))
	                   {
	                     // mailFlag  = 1;
	                      //finalDOB.getFlagsDOB().setQuoteStatusFlag("PEN");//@@Quote is Pending
	                      //ReportsSessionBeanHome home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
	                      //ReportsSession remote	= (ReportsSession)home.create();
	                     //@@Modified by kameswari for the WPBN issue-61289
	                     //String to_emailIds = finalDOB.getHeaderDOB().getCustEmailId();
	                      String to_emailIds = null;
	                      if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
	                         to_emailIds = finalDOB.getHeaderDOB().getCustEmailId()+','+masterDOB.getSalesPersonEmail();
	                       else
	                         to_emailIds = finalDOB.getHeaderDOB().getCustEmailId();
	                          //@@WPBN issue-61289
	                       to_emailIds = to_emailIds.replaceAll(";",",");
	                     
	                       //System.out.println("Before Sending Mail------------------------------>");
	                      
	                    if(contactPersons!=null)
	                    {
	                    	int contPersonLen	=	contactPersons.length;
	                        for(int i=0;i<contPersonLen;i++)
	                        {
	                          mailFlag  = 1;
	                        
	                           // if(i<contactPersons.length)//@@Added by Kameswari for the WPBN issue-61289
	                       //    {
	                          if(masterDOB.getCustomerContactsEmailIds()[i]!=null && masterDOB.getCustomerContactsEmailIds()[i].trim().length()!=0)
	                          {
	                      
	                            contactName   =   masterDOB.getCustContactNames()[i];
	                            if(masterDOB.getCustContactNames()[i].indexOf("[")!=-1)
	                                contactName = contactName.substring(0,masterDOB.getCustContactNames()[i].indexOf("["));
	                           /* if(finalDOB.getUpdatedReportDOB()!=null)
	                            {
	                               
	                                
	                               body = "Dear "+contactName+",\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                                 ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
	                                "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                            }
	                            else
	                            {
	                              body = "Dear "+contactName+",\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
	                                   " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                            }*/  
	                              //body  = "Dear "+contactName+",\n\n"+finalDOB.getEmailText()+"\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                       if(finalDOB.getUpdatedReportDOB()!=null)
	                            {
	                               body  = "Dear "+(contactName!=null?contactName:"")+",\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                                 ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                                       
	                            }
	                            else
	                            {
	                                 body  = "Dear "+(contactName!=null?contactName:"")+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                 "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                                 
	                            }            
	                            if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
	                             {
	                               if((legDestCharges.getPickZoneZipMap()!=null && legDestCharges.getPickZoneZipMap().size()>0)
	                                  || (legDestCharges.getDeliveryZoneZipMap()!=null && legDestCharges.getDeliveryZoneZipMap().size()>0))
	                               {
	                            	   if(!mailSent ||  contactPersonsLength<contPersonLen){//added by silpa.p for sent mail modifications  on 24-05-11
	                                      sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                            	   mailSent=true;
	                            	    contactPersonsLength++;
	                               }//ended
	                               }
	                               
	                                else
	                                	if(!mailSent || contactPersonsLength<contPersonLen){//added by silpa.p for sent mail modifications  on 24-05-11
	                              
	                                  sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                                		mailSent=true;
	                                		contactPersonsLength++;
	                             }//ended
	                               
	                             }
	                              else
	                            	  if(!mailSent ||contactPersonsLength<contPersonLen){//added by silpa.p for sent mail modifications  on 24-05-11
	                                sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                                mailSent=true;
	                                contactPersonsLength++;
	                            	  }//ended
	                        //  }
	                          //@@Added by Kameswari for the WPBN issue-61289
	                          }
	                        }
	                              if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
	                              {
	                                 if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
	                             {
	                               if((legDestCharges.getPickZoneZipMap()!=null && legDestCharges.getPickZoneZipMap().size()>0)
	                                  || (legDestCharges.getDeliveryZoneZipMap()!=null && legDestCharges.getDeliveryZoneZipMap().size()>0))
	                            	   if(!mailSent||contactPersonsLength<contPersonLen){//added by silpa.p for sent mail modifications  on 24-05-11
	                   
	                                  sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getSalesPersonEmail(),subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                            	mailSent=true;
	                            	 contactPersonsLength++;
	                            	   }//ended
	                             else
	                            	 if(!mailSent ||contactPersonsLength<contPersonLen){//added by silpa.p for sent mail modifications on 24-05-11
	                                   sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getSalesPersonEmail(),subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                                   mailSent=true;
	                                   contactPersonsLength++;
	                               }//ended
	                               }
	                             else
	                            	 if(!mailSent||contactPersonsLength<contPersonLen){//added by silpa.p for sent mail modifications  on 24-05-11
	                                 sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getSalesPersonEmail(),subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                                 mailSent=true;
	                                 contactPersonsLength++;
	                            	 }//ended
	                              }
	                        //@@ the WPBN issue-61289
	                  
	                    }
	                          else
	                          {
	                              mailFlag  = 0;
	                  
	                      /*if(finalDOB.getUpdatedReportDOB()!=null)
	                      {
	                        
	                        body = "Dear Customer,\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                              ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
	                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                     }
	                      else
	                      {
	                        body = "Dear Customer,\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
	                                 " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                      }*/
	                       //
	                       //body  ="Dear Customer,\n\n"+finalDOB.getEmailText()+"\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                        /*body  = "Dear Customer,\n\n"+finalDOB.getEmailText()+"\n\n\n"+masterDOB.getCreatorDetails()+"\n\n"+masterDOB.getCompanyName()+
	                            "\n"+masterDOB.getTerminalAddress()+"\n\n"+"Phone  "+masterDOB.getPhoneNo()+"\n"+"Fax    "+masterDOB.getFaxNo()+"\n"+"Mobile "+masterDOB.getMobileNo()+"\n\nEmail "+masterDOB.getUserEmailId(); */
	                            //@@Modified by Kameswari for the WPBN issue-61303
	                       
	                        if(finalDOB.getUpdatedReportDOB()!=null)
	                            {
	                               body  = "Dear Customer,\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                                 ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                            }
	                            else
	                            {
	                                 body  = "Dear Customer,\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                 "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                            }
	                            if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
	                       {
	                         if((legDestCharges.getPickZoneZipMap()!=null && legDestCharges.getPickZoneZipMap().size()>0)
	                            || (legDestCharges.getDeliveryZoneZipMap()!=null && legDestCharges.getDeliveryZoneZipMap().size()>0))
	                        	 if(!mailSent ){//added by silpa.p for sent mail modifications  on 24-05-11
	                                sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                                mailSent=true;
	                                
	                        	 }//ended
	                         else
	                        	 if(!mailSent){//added by silpa.p for sent mail modifications mail on 24-05-11
	                          sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                          mailSent=true;
	                        	 }//ended
	                       }
	                      else
	                    	  if(!mailSent){//added by silpa.p for sent mail modifications mail on 24-05-11
	                        sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                        mailSent=true;
	                    	  }//ended
	                    }
	                      
	                      //System.out.println("End of mail sending and PDF Generation --------------------------------->");
	                  }
	                }
	                catch(Exception e)
	                {
	                 //Logger.error(FILE_NAME,"Error while sending mail "+e); 
	                 logger.error(FILE_NAME+"Error while sending mail "+e); 
	                 e.printStackTrace();
	                 mailFlag = 0;
	                }
	                try
	                {
	                  if("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getFaxFlag()))
	                  {
	                    //faxFlag   = 2;
	                    //finalDOB.getFlagsDOB().setQuoteStatusFlag("PEN");//@@Quote is Pending
	                    //ReportsSessionBeanHome home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
	                   //ReportsSession  remote	= (ReportsSession)home.create();
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
	                    	int contPersonLen	=	contactPersons.length;
	                      for(int i=0;i<contPersonLen;i++)
	                      {
	                        faxFlag   = 2;
	                        contactName   =   masterDOB.getCustContactNames()[i];
	                        if(masterDOB.getCustContactNames()[i].indexOf("[")!=-1)
	                            contactName = contactName.substring(0,masterDOB.getCustContactNames()[i].indexOf("["));
	                        if(masterDOB.getCustomerContactsFax()[i]!=null && masterDOB.getCustomerContactsFax()[i].trim().length()!=0)
	                        {
	                          if("SG".equalsIgnoreCase(countrycode))
	                            contactFax  = "fax#"+masterDOB.getCustomerContactsFax()[i]+"@tcdhl.com";
	                          else
	                            contactFax  = "ifax#"+masterDOB.getCustomerContactsFax()[i]+"@tcdhl.com";
	                          
	                          /*if(finalDOB.getUpdatedReportDOB()!=null)
	                          {
	                            body = "Dear "+contactName+",\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
	                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                           }
	                          else
	                          {
	                            body = "Dear "+contactName+",\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
	                                 " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                         }*/  
	                          // body  = "Dear "+contactName+",\n\n"+finalDOB.getEmailText()+"\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                     if(finalDOB.getUpdatedReportDOB()!=null)
	                            {
	                               body  = "Dear "+(contactName!=null?contactName:"")+",\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                                 ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                            }
	                            else
	                            {
	                                 body  = "Dear "+(contactName!=null?contactName:"")+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                 "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                                 
	                            }              
	                          if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
	                          {
	                            if((legDestCharges.getPickZoneZipMap()!=null && legDestCharges.getPickZoneZipMap().size()>0)
	                              || (legDestCharges.getDeliveryZoneZipMap()!=null && legDestCharges.getDeliveryZoneZipMap().size()>0))
	                                sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                            else
	                              sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                          }
	                          else
	                            sendMail(finalDOB.getMasterDOB().getUserEmailId(),contactFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                        }
	                        else
	                          faxFlag   = 0;
	                      }
	                    }
	                    else
	                    {
	                      if(customerFax!=null && customerFax.length()>0)
	                      {
	                       /* if(finalDOB.getUpdatedReportDOB()!=null)
	                      {
	                        body = "Dear Customer,\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                              ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+". All information is contained within the attachment. "+
	                              "Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                      }
	                      else
	                      {
	                        body = "Dear Customer,\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
	                                 " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress();
	                      }*/
	                          // body  = "Dear Customer,\n\n"+finalDOB.getEmailText()+"\n\n\nRegards,\n"+masterDOB.getCreatorDetails()+"\n"+masterDOB.getTerminalAddress(); 
	                     if(finalDOB.getUpdatedReportDOB()!=null)
	                            {
	                               body  = "Dear Customer,\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
	                                 ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                       "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                            }
	                            else
	                            {
	                                 body  = "Dear Customer,\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
	                                 "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
	                            }   
	                            if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
	                        {
	                          if((legDestCharges.getPickZoneZipMap()!=null && legDestCharges.getPickZoneZipMap().size()>0)
	                            || (legDestCharges.getDeliveryZoneZipMap()!=null && legDestCharges.getDeliveryZoneZipMap().size()>0))
	                              sendMail(finalDOB.getMasterDOB().getUserEmailId(),customerFax,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                          else
	                            sendMail(finalDOB.getMasterDOB().getUserEmailId(),customerFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                        }                          
	                        else
	                          sendMail(finalDOB.getMasterDOB().getUserEmailId(),customerFax,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
	                      }
	                      else  
	                        faxFlag   = 0;
	                    }
	                  }
	                }
	                catch(Exception e)
	                {
	                   //Logger.error(FILE_NAME,"Error while sending fax "+e); 
	                   logger.error(FILE_NAME+"Error while sending fax "+e); 
	                  e.printStackTrace();
	                  faxFlag = 0;
	                }
	              }
	                if("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getPrintFlag()))
	                {
	                  //finalDOB.getFlagsDOB().setQuoteStatusFlag("PEN");//@@Quote is Pending
	                  printFlag    =  4;
	                }
	                else
	                {
	                  f.delete();
	                  File annexure = new File(filename);
	                  annexure.delete();
	                }
	              returnFlag  = mailFlag + faxFlag + printFlag;
	    }   
	    catch(Exception e)
	    {
	      e.printStackTrace();
	      //Logger.error(FILE_NAME,"Error while generating the PDF"+e.toString());
	      logger.error(FILE_NAME+"Error while generating the PDF"+e.toString());
	      returnFlag  = 0;
	      //throw new Exception("Error while generating PDF format");
	    }
	     finally
	    {
	        try
	        {
	        //Method: doPDFGeneration Defect: PdfWriter is not closedSuggestion: Close and nullify the writer in the finally block.
	                   if(writer != null){
	          writer.close();
	          writer = null;
	        }
	              charges         = null;
	              freightCharges  = null;
	              frequency       = null;
	              carrier         = null;
	              transittime     = null;
	              ratevalidity    = null;
	              frequency_o     = null;
	              frequency_d     = null;
	              carrier_o       = null;
	              carrier_d       = null;
	              transit_o       = null;
	              transit_d       = null;
	              validity_o      = null;
	              validity_d      = null;
	              filesList       = null;

	        }
	        catch(Exception ex)
	        {
	            //Logger.error(FILE_NAME,"Exception caught :: finally :: insertFclValues() " + ex.toString() );
	            logger.error(FILE_NAME+"Error while generating the PDF"+ex.toString());
	        }
	    }
	    return returnFlag;
  }
 //@@Added by kiran.v on 19/09/2011 for Wpbn Issue 271485
private void doPdfAbbrivations(String fileName,MultiQuoteFinalDOB finalDOB,HttpServletRequest request,HttpServletResponse response,ArrayList Sd,ArrayList Cd,ArrayList Cdn,boolean frequencyFlag,boolean carrierFlag,boolean serviceFlag,Map<String,String> surChargesMap){
    ArrayList          filesList       = new ArrayList();
    File               file            = null;
    byte[]             buffer          = null;
    ArrayList             bufferList    = new ArrayList();
     ArrayList         pdfFilesList    = new ArrayList();
    HttpSession        session         = request.getSession();
    HashSet    PortAbbSet          = null;
    PdfWriter writer                       =null;       
    QMSMultiQuoteSessionHome     home              = null;
    QMSMultiQuoteSession         remote            = null;
    int incoSize	=	0; 
    PdfPTable pTable;
    PdfPCell  pCell;
    ESupplyDateUtility  eSupplyDateUtility =	new ESupplyDateUtility();
    MultiQuoteHeader    headerDOB		           =	null;
    MultiQuoteMasterDOB masterDOB               =  null ;
    int LegSize=0;
    MultiQuoteChargeInfo       multiQuoteChargeInfo = null;
   
    try
    {      

				 String carrierChecked= request.getParameter("selectCarrier");
			     String serviceLevelChecked= request.getParameter("selectService");
			     String frequencyChecked=request.getParameter("selectFrequecy");
			     String operation=request.getParameter("Operation");
			     DecimalFormat df  = new DecimalFormat("###,###,###,##0.00");
			     ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
			     eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
			
		        Document document = new Document(PageSize.A4,54f,54f,68.4f,68.4f);//@@ 36 points represent 0.5 inch
		        document.setPageSize(PageSize.A4.rotate());
		        String PDF_FILE_NAME = "Approved.pdf";
		        document.addTitle("Approved Report");
		        document.addSubject("Report PDF");
		        document.addKeywords("Test, Key Words");
		        document.addAuthor("QuoteShop");
		        document.addCreator("QuoteShop");
		        document.addCreationDate();
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ByteArrayOutputStream baosFile = new ByteArrayOutputStream();
		        writer	=	PdfWriter.getInstance(document, baos); 
		        document.open();
		        PdfContentByte cb = writer.getDirectContent();
		        cb.rectangle (document.left (), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
		
		         cb.stroke ();

		            int[] widths = {12,12,12,12,12,12,28};
		            
		            int[] width = {4,1};
		            Table mainT = new Table(2,2);
		            mainT.setWidth(100);
		            mainT.setWidths(width);
		            mainT.setBorderColor(Color.white);
		            mainT.setPadding(3);
		            mainT.setSpacing(0);
		            
		
		                         
		            Phrase  headingPhrase    =  new Phrase("",FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
		            Cell cellHeading = new Cell(headingPhrase);
		            cellHeading.setBorderColor(new Color(255,255, 255));
		            cellHeading.setHorizontalAlignment(cellHeading.ALIGN_CENTER);
		            cellHeading.setBorderWidth(0);
		            mainT.addCell(cellHeading);
		            
		            Cell imageCell = new Cell();            
		            java.net.URL url =  getServletConfig().getServletContext().getResource("/images/DHLlogo.gif");
		            Image img0 = Image.getInstance(url);
		            img0.setAlignment(Image.ALIGN_RIGHT);
		            imageCell.setHorizontalAlignment(imageCell.ALIGN_LEFT);
		            imageCell.add(img0);
		            imageCell.setBorderWidth(0);
		            imageCell.setNoWrap(true);
		            System.out.println(imageCell.cellWidth());
		            mainT.addCell(imageCell);
		            mainT.setAlignment(mainT.ALIGN_CENTER);
		            
		            document.add(mainT);
		          
		             pTable = new PdfPTable(1);
		              pTable.setSpacingAfter(10f);
		              pCell	 = new PdfPCell(new Phrase(new Chunk("")));
		              pCell.setBorder(0);
		              pTable.addCell(pCell);
		              
		              document.add(pTable); 		                 
					String orgDesc  ="";
					String destDesc ="";
				  pTable = new PdfPTable(1);
				  pTable.setSpacingAfter(10f);
				  pCell	 = new PdfPCell(new Phrase(new Chunk("")));
				  pCell.setBorder(0);
				  pTable.addCell(pCell);
					  
			     document.add(pTable); 
				 Table surChargesTable  =  new Table(2);
				 surChargesTable.setOffset(5);
				 surChargesTable.setWidth(100);
				 surChargesTable.setPadding(1);
				 surChargesTable.setSpacing(0);
				
				 surChargesTable.setBackgroundColor(Color.WHITE);
				 surChargesTable.setBorderColor(Color.black);
				 surChargesTable.setBorderWidth(1f);
				 Cell cellContent = new Cell( new Chunk("Abbreviation",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
				 cellContent.setColspan(2);
				 	cellContent.setBackgroundColor(Color.ORANGE);
				 cellContent.setBorder(1);
				 	 surChargesTable.addCell(cellContent);
				 	home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
			  		remote      = home.create();
				
		     if(surChargesMap!=null){
			
				 cellContent = new Cell( new Chunk("Surcharge ID",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
				 cellContent.setBorder(1);
				 surChargesTable.addCell(cellContent);
				 
				 cellContent = new Cell( new Chunk("Surcharge Description",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
				 cellContent.setBorder(1);
				 surChargesTable.addCell(cellContent);
				
				 Iterator<Entry<String, String>> it = surChargesMap.entrySet().iterator();
				   
					     while (it.hasNext()) {
					        Map.Entry<String, String> pairs = it.next();					      
					      cellContent = new Cell( new Chunk(pairs.getKey(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK)));
					      cellContent.setBorder(1);
					      surChargesTable.addCell(cellContent);
					      
					      cellContent = new Cell( new Chunk(pairs.getValue(),FontFactory.getFont("ARIAL", 7, Font.NORMAL, Color.BLACK)));
					      cellContent.setBorder(1);
					      surChargesTable.addCell(cellContent);
					     }
		      } 
					// Cell cellContentForBasis = new Cell( new Chunk("",FontFactory.getFont("ARIAL", 9, Font.BOLD,Color.BLACK)));
					cellContent = new Cell( new Chunk("",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK)));
					cellContent.setColspan(2);
					cellContent.setBorder(1);
					   surChargesTable.addCell(cellContent);
					
						cellContent = new Cell( new Chunk("Charge Basis ID",FontFactory.getFont("ARIAL",10, Font.BOLD,Color.BLACK)));
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					
					cellContent = new Cell( new Chunk("Charge Basis Description",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					cellContent = new Cell( new Chunk("Per KG",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					
					cellContent= new Cell( new Chunk("Per Kilogram",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					
					cellContent = new Cell( new Chunk("Per Shpt",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					cellContent = new Cell( new Chunk("Per Shipment",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					cellContent = new Cell( new Chunk("Per W/M",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					cellContent = new Cell( new Chunk("Per Weight Measurement",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					cellContent = new Cell( new Chunk("% of freight rates",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					cellContent = new Cell( new Chunk("Percent Of Freight Rates",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					  
					cellContent = new Cell( new Chunk("Per Ctr",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));//added by silpa.p on 5-07-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					cellContent = new Cell( new Chunk("Per Container",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//added by silpa.p on 5-07-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					 LegSize= finalDOB.getLegDetails().size();
					 //LegSize=(Integer)session.getAttribute("legSize");
					if(LegSize>0)  { 
						PortAbbSet       = new HashSet();	
					cellContent = new Cell( new Chunk("Port ID",FontFactory.getFont("ARIAL",10, Font.BOLD,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					
					cellContent = new Cell( new Chunk("Port Name",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));//Modified by silpa.p on 2-06-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					
					for(int ln=0;ln<LegSize;ln++){
					//orgDesc =multiQuoteChargeInfo.getOrginLoc();
					orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getOrigin();  
					orgDesc = orgDesc.length()>3?orgDesc.substring(2):orgDesc;
						 if(!PortAbbSet.contains(orgDesc))
						 {
					cellContent = new Cell( new Chunk(orgDesc!=null?orgDesc:"",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));//added by silpa.p on 5-07-11
					cellContent.setBorder(1);
					
					surChargesTable.addCell(cellContent);
					PortAbbSet.add(orgDesc);
						 }
					
					// 	orgDesc = remote.getLocationName(multiQuoteChargeInfo.getOrginLoc());
					orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getOrgFullName();	
					if(!PortAbbSet.contains(orgDesc))
					{
					cellContent = new Cell( new Chunk(orgDesc!=null?orgDesc:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//added by silpa.p on 5-07-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					PortAbbSet.add(orgDesc);
					}
					//destDesc =multiQuoteChargeInfo.getDestLoc();
					destDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getDestination();
					destDesc = destDesc.length()>3?destDesc.substring(2):destDesc;
					if(!PortAbbSet.contains(destDesc))
					{
					cellContent = new Cell( new Chunk(destDesc!=null?destDesc:"",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));//added by silpa.p on 5-07-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					PortAbbSet.add(destDesc);
					}
					
					// 	destDesc = remote.getLocationName(multiQuoteChargeInfo.getDestLoc());
					destDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getDestFullName();
					if(!PortAbbSet.contains(destDesc))
					{
					cellContent = new Cell( new Chunk(destDesc!=null?destDesc:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));//added by silpa.p on 5-07-11
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					PortAbbSet.add(destDesc);
					}
					}
					
					}
				//@@Modified by kiran on 10/08/2011 for WPBN Issue-258778
					// 09/11/2011
				if("add".equalsIgnoreCase(operation)||"modify".equalsIgnoreCase(operation)||"Copy".equalsIgnoreCase(operation)){
				if(("checked".equalsIgnoreCase(carrierChecked)) ){
				String Carrier="";
				String CarrierName="";
				System.out.println("Cd.size()"+Cd.size());
		    if(Cd.size()>0)  {
					PortAbbSet       = new HashSet();	
					cellContent = new Cell( new Chunk("Carrier ID",FontFactory.getFont("ARIAL",10, Font.BOLD,Color.BLACK)));
					cellContent.setBorder(1);
					 surChargesTable.addCell(cellContent);
					 
					cellContent = new Cell( new Chunk("Carrier Name",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
					cellContent.setBorder(1);
					 surChargesTable.addCell(cellContent);
				  
					 for(int ln=0;ln<Cd.size();ln++){
				     	//orgDesc =multiQuoteChargeInfo.getOrginLoc();
				     //orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();  
						Carrier=(String)Cd.get(ln);
				   
				     // orgDesc = orgDesc;
				     		 if(!PortAbbSet.contains(Carrier))
				     		 {
				     	System.out.println("Carrier=="+Carrier);
				     	cellContent = new Cell( new Chunk(Carrier!=null?Carrier:"",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));
				     	cellContent.setBorder(1);
				     	 surChargesTable.addCell(cellContent);
				     	PortAbbSet.add(Carrier);
				     		 
				     	 
				    // 	orgDesc = remote.getLocationName(multiQuoteChargeInfo.getOrginLoc());
				     	CarrierName = (String)Cdn.get(ln);
				     		// orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();	
				     	//if(!PortAbbSet.contains(Carrier))
						// {
				     	cellContent = new Cell( new Chunk(CarrierName!=null?CarrierName:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));
				     	 cellContent.setBorder(1);
				    	 surChargesTable.addCell(cellContent);
				    //	PortAbbSet.add(orgDesc);
						// }
				     		 }
				   
				     	 }
  
			  }
		  }
		         if(("checked".equalsIgnoreCase(serviceLevelChecked)) ){
		          String serviceId="";
			if(Sd.size()>0)  {
					PortAbbSet       = new HashSet();	
					cellContent = new Cell( new Chunk("Service ID",FontFactory.getFont("ARIAL",10, Font.BOLD,Color.BLACK)));
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					
					cellContent = new Cell( new Chunk("Service  Name",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
					cellContent.setBorder(1);
					surChargesTable.addCell(cellContent);
					
					for(int ln=0;ln<Sd.size();ln++){
						//orgDesc =multiQuoteChargeInfo.getOrginLoc();
					//orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();  
					serviceId=(String)Sd.get(ln);
					
					// orgDesc = orgDesc;
							 if(!PortAbbSet.contains(serviceId))
							 {
						cellContent = new Cell( new Chunk(serviceId!=null?serviceId:"",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));
						cellContent.setBorder(1);
						 surChargesTable.addCell(cellContent);
						PortAbbSet.add(serviceId);
							 
						 
					// 	orgDesc = remote.getLocationName(multiQuoteChargeInfo.getOrginLoc());
							 serviceId = remote.getServiceLevelName(serviceId);
							// orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();	
					
						cellContent = new Cell( new Chunk(serviceId!=null?serviceId:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));
						 cellContent.setBorder(1);
						 surChargesTable.addCell(cellContent);
					//	PortAbbSet.add(orgDesc);
						 }
					
					
						 }
						   
					}
		    }
		  }
		else{
		     if(("checked".equalsIgnoreCase(carrierChecked)) ||  carrierFlag ){
			 String Carrier="";
			 String CarrierName="";
			 System.out.println("Cd.size()"+Cd.size());
				if(Cd.size()>0)  {
						PortAbbSet       = new HashSet();	
				      	cellContent = new Cell( new Chunk("Carrier ID",FontFactory.getFont("ARIAL",10, Font.BOLD,Color.BLACK)));
				      	cellContent.setBorder(1);
				      	 surChargesTable.addCell(cellContent);
				      	 
				      	cellContent = new Cell( new Chunk("Carrier Name",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
				      	cellContent.setBorder(1);
				       surChargesTable.addCell(cellContent);
				        
				      	 for(int ln=0;ln<Cd.size();ln++){
				        //orgDesc =multiQuoteChargeInfo.getOrginLoc();
				          //orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();  
				      	Carrier=(String)Cd.get(ln);
				         
				           // orgDesc = orgDesc;
				            if(!PortAbbSet.contains(Carrier))
				           	 {
				           	System.out.println("Carrier=="+Carrier);
				           	cellContent = new Cell( new Chunk(Carrier!=null?Carrier:"",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));
				           	cellContent.setBorder(1);
				           	 surChargesTable.addCell(cellContent);
				           	PortAbbSet.add(Carrier);
				           		 
				           	 
				          // 	orgDesc = remote.getLocationName(multiQuoteChargeInfo.getOrginLoc());
				           	CarrierName = (String)Cdn.get(ln);
				           		// orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();	
				           	//if(!PortAbbSet.contains(Carrier))
				      		// {
				           	cellContent = new Cell( new Chunk(CarrierName!=null?CarrierName:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));
				           	 cellContent.setBorder(1);
				          	 surChargesTable.addCell(cellContent);
				          //	PortAbbSet.add(orgDesc);
				      		// }
				           		 }
				         
				           	 }
				         
								   
						}
		      }
			 if(("checked".equalsIgnoreCase(serviceLevelChecked)) || serviceFlag ){
				 String serviceId="";
				if(Sd.size()>0)  {
				PortAbbSet       = new HashSet();	
			 	cellContent = new Cell( new Chunk("Service ID",FontFactory.getFont("ARIAL",10, Font.BOLD,Color.BLACK)));
			 	cellContent.setBorder(1);
			 	 surChargesTable.addCell(cellContent);
			 	 
			 	cellContent = new Cell( new Chunk("Service  Name",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK)));
			 	cellContent.setBorder(1);
			 	 surChargesTable.addCell(cellContent);
			
			 	 for(int ln=0;ln<Sd.size();ln++){
			      	//orgDesc =multiQuoteChargeInfo.getOrginLoc();
			      //orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();  
			      serviceId=(String)Sd.get(ln);
			    
			      // orgDesc = orgDesc;
			      		 if(!PortAbbSet.contains(serviceId))
			      		 {
			      	cellContent = new Cell( new Chunk(serviceId!=null?serviceId:"",FontFactory.getFont("ARIAL",8, Font.NORMAL,Color.BLACK)));
			      	cellContent.setBorder(1);
			      	 surChargesTable.addCell(cellContent);
			      	PortAbbSet.add(serviceId);
			      		 
			      	 
			     // 	orgDesc = remote.getLocationName(multiQuoteChargeInfo.getOrginLoc());
			      		 serviceId = remote.getServiceLevelName(serviceId);
			      		// orgDesc =((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(ln)).getServiceLevel();	
			      
			      	cellContent = new Cell( new Chunk(serviceId!=null?serviceId:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)));
			      	 cellContent.setBorder(1);
			     	 surChargesTable.addCell(cellContent);
			     //	PortAbbSet.add(orgDesc);
			 		 }
			     
			    
			      	 }
					   
				}
				 }
		}
		      
		        //@@Ended by kiran.v
			     document.add(surChargesTable);
			     PdfContentByte cb1 = writer.getDirectContent();
		         cb1.rectangle (document.left (), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
		          cb1.stroke ();
				     if(document!=null)
				     {
				        document.close();
						File f = new File("Cartage.pdf");
						FileOutputStream fileOutputStream = new FileOutputStream(f);
						baos.writeTo(fileOutputStream);
						fileOutputStream.close();
						PdfReader reader = new PdfReader("Cartage.pdf");
						int n = reader.getNumberOfPages();
						File fs = new File(fileName);
						String space = " ";
						PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(fs));		
						int k = 0;
						PdfContentByte under;
						PdfContentByte over = null;
						BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
								BaseFont.WINANSI, BaseFont.EMBEDDED);
						while (k < n) {
							k++;
							over = stamp.getOverContent(k);
							over.rectangle (document.left(), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
							over.stroke ();
							over.beginText();
							over.setFontAndSize(bf, 8);
							over.setTextMatrix(15, 15);
							over.showText("page " + k + " of " + n);
	
							if (k > 1) {
								over.setFontAndSize(bf, 7);
								if(masterDOB!=null )
								over.showText("QUOTE REFERENCE:"+ masterDOB.getQuoteId());
								over.endText();
								over.beginText();
								if(headerDOB!=null)
								over.showText("CUSTOMER NAME: "	+ headerDOB.getCustomerName());
							}
								over.endText();
							}
					stamp.close();
					request.getSession().setAttribute("AbbrivationOuptutStream", fs);

					baos.close();			        			        
				     }
		
		     
		      } catch(Exception exception)
      {
          exception.printStackTrace();
      }

}
//@@ Ended by kiran.v
private byte[] concatPDF(ArrayList pdfFilesList)
{
  byte[] buffer = null;
  FileOutputStream  filestream  = null;
   try
          {
              int i = 1;
             
              Document document = null;
              PdfCopy pdfcopy = null;
              
              if(pdfFilesList.size()>1)
              {
               String s = (String)pdfFilesList.get(0);
           int pdfFilListSize	=	pdfFilesList.size();
           for(; i < pdfFilListSize ; i++)
              {
                  PdfReader pdfreader = new PdfReader((String)pdfFilesList.get(i));
                  int j = pdfreader.getNumberOfPages();
                  if(i == 1)
                  {
                      document = new Document(pdfreader.getPageSizeWithRotation(1));
                      filestream = new FileOutputStream(s);
                      pdfcopy = new PdfCopy(document,filestream);
                       document.open();
                  }
                  PdfImportedPage pdfimportedpage = null;
                  for(int k = 0; k < j;)
                  {
                      k++;
                      pdfimportedpage = pdfcopy.getImportedPage(pdfreader, k);
                      if(pdfimportedpage!=null)
                      {
                        pdfcopy.addPage(pdfimportedpage);
                      }
                  }
                    
                  com.lowagie.text.pdf.PRAcroForm pracroform = pdfreader.getAcroForm();
                  if(pracroform != null)
                      pdfcopy.copyAcroForm(pdfreader);
              }
             
             if(document!=null)
             {
              document.close();
             }
        
              FileInputStream inputStream = new FileInputStream(s);
              buffer = new byte[inputStream.available()];
              inputStream.read(buffer);
              }     
              // buffer  = pdfcopy.getDirectContent();
      
          }
          catch(Exception exception)
          {
              exception.printStackTrace();
          }
          return buffer;
}



private int doGenerateCartagePDF(String fileName,MultiQuoteFinalDOB finalDOB,HttpServletRequest request,HttpServletResponse response)throws Exception
{
try
{      //System.out.println("Starting zone PDF Generation------------------------------>");
        DecimalFormat df  = new DecimalFormat("###,###,###,##0.00");
        // Added By Kishore Podili For Multi Zone Codes
        MultiQuoteCartageRates pickQuoteCartageRates     = null;
        MultiQuoteCartageRates deliQuoteCartageRates     = null;
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
        HttpSession      session                    = null;
        MultiQuoteFreightLegSellRates legDestCharges = null;
        ArrayList destLaneCharges = finalDOB.getLegDetails();
        int noOfLanes = destLaneCharges.size();
        MultiQuoteMasterDOB masterDOB = finalDOB.getMasterDOB();
       
        //Added by Kishore For Multiple Zip and Zone Codes
        
        Document document = null;
        ByteArrayOutputStream baos = null;
        PdfWriter writer	=	null;
        
        MultiQuoteHeader    headerDOB	=	 finalDOB.getHeaderDOB();
        
        ArrayList         pickupChargeBasisList        = null;
        ArrayList         delChargeBasisList           = null;
        int				  pickupChargeBasisSize		   = 0;
        int         delChargeBasisSize           = 0;
         
        for(int dc=0;dc<noOfLanes;dc++)      
        {
        	legDestCharges = (MultiQuoteFreightLegSellRates) destLaneCharges.get(dc);
        //Added by Anil.k kishore for Quote Charges
        //if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith())){//END
        //Added By Kishore Podili For CartagePDF Quote Add
		 //if((masterDOB.getShipperZipCode()!=null && (masterDOB.getShipperZipCode()[dc]==null || "".equals(masterDOB.getShipperZipCode()[dc])))||( masterDOB.getConsigneeZipCode()!=null && (masterDOB.getConsigneeZipCode()[dc]==null || "".equals(masterDOB.getConsigneeZipCode()[dc]))))
	     //{
        	 //Commented By Kishore Podili
        	 if((legDestCharges.getPickZoneZipMap()!=null && legDestCharges.getPickZoneZipMap().size()>0)
                     || (legDestCharges.getDeliveryZoneZipMap()!=null && legDestCharges.getDeliveryZoneZipMap().size()>0))
             {
        
        pickUpQuoteCartageRates   =  legDestCharges.getPickUpCartageRatesList();
      
        deliveryQuoteCartageRates =  legDestCharges.getDeliveryCartageRatesList(); 
      //Modified By Kishore For MultiZoneCodes
        pickupWeightBreaks        =  legDestCharges.getPickupWeightBreaks();   //finalDOB.getPickupWeightBreaks();
        delWeightBreaks           =  legDestCharges.getDeliveryWeightBreaks(); //finalDOB.getDeliveryWeightBreaks();
        
        if(pickupWeightBreaks!=null)
            pickupWeightBreaksSize  = pickupWeightBreaks.size();
        if(delWeightBreaks!=null)
            delWeightBreaksSize     = delWeightBreaks.size();
        
        
        pickupChargeBasisList        =  legDestCharges.getPickupChargeBasisList();   //finalDOB.getPickupWeightBreaks();
        delChargeBasisList           =  legDestCharges.getDelChargeBasisList(); //finalDOB.getDeliveryWeightBreaks();
        
        if(pickupChargeBasisList!=null)
        	pickupChargeBasisSize  = pickupChargeBasisList.size();
        if(delChargeBasisList!=null)
            delChargeBasisSize     = delChargeBasisList.size();
        
        //Commented By Kishroe Podili
        
        //MultiQuoteHeader    headerDOB	=	 finalDOB.getHeaderDOB();
        
        //MultiQuoteMasterDOB masterDOB  =   finalDOB.getMasterDOB();
        ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
        ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
        eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
        //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
//@@ Commented and added by subrahmanyam for the pbn issue 212006 on #26-jul-10
        /*          String[] strDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
        String[] effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
        */
        String[] strDate	=null;
        String[] effDate	= null;
        if("View".equalsIgnoreCase(request.getParameter("Operation")))
        {
            if("PDF".equalsIgnoreCase(request.getParameter("PDF")))
            {
            	strDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getModifiedDate());
            	effDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getEffDate());
            }
            else{
            	strDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getModifiedDate());
            	effDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getCreatedDate());
            }
            
        }
        else{
        	 strDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
             effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());

        }

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
                 
        
        //Added By Kishore For MultiZone Codes
        Cell cellCountry;
        Chunk chk;
        if(dc==0 ){
        	
	        	//document = new Document(PageSize.A4,54f,54f,68.4f,68.4f);//@@ 36 points represent 0.5 inch  
	        	 document = new Document(PageSize.A4,10f,10f,20f,20f); //added by silpa on 27-06-11
        document.addTitle("Approved Report");
        document.addSubject("Report PDF");
        document.addKeywords("Test, Key Words");
        document.addAuthor("DHL");
        document.addCreator("Auto Generated through 4S DHL");
        document.addCreationDate();
	            
	            baos = new ByteArrayOutputStream();
	            writer	=	PdfWriter.getInstance(document, baos);
	            //added by silpa on 27-06-11
        document.open();           
        PdfContentByte cb = writer.getDirectContent();
        cb.rectangle (document.left (), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
       cb.stroke ();//ended
        
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
        //img0.scalePercent(75);
        imageCell.add(img0);
        imageCell.setHorizontalAlignment(imageCell.ALIGN_RIGHT);
        imageCell.setBorderWidth(0);
        imageCell.setNoWrap(true);
        imageCell.setColspan(1);
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
		        /*Cell cellCountry;
		        Chunk chk;*/
        
        chk = new Chunk("ANNEXURE",FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
        cellCountry = new Cell(chk);
        cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
        cellCountry.setNoWrap(true); 
        cellCountry.setLeading(10.0f); 
        cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
        partCountry.addCell(cellCountry);
        
        if("MY".equalsIgnoreCase(masterDOB.getCountryId()))
        {
        chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry()[dc].toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry()[dc].toUpperCase():""),FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.BLUE));
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
        
        chk = new Chunk("ATTENTION TO: "+toTitleCase(attentionTo.toString()),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.BLUE));
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
        chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry()[dc].toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry()[dc].toUpperCase():""),FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.RED));
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
        
        chk = new Chunk("ATTENTION TO: "+toTitleCase(attentionTo.toString()),FontFactory.getFont("ARIAL", 14, Font.BOLD,Color.RED));
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
        
        }
        
        if(dc>0){
        	document.newPage();
        	
        }
       
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
        partZone.setAutoFillEmptyCells(true);
        partZone.setWidth(100);
        //partZone.setTableFitsPage(true);
        //@@ Added by subrahmanyam for the enhancement #147062 on 09/12/2008
        chk = new Chunk("PICKUP CARTAGE RATES AT: "+headerDOB.getOriginLocName()[dc],FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
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
        cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
        cellZone.setLeading(10.0f);
        cellZone.setBackgroundColor(Color.ORANGE);
        cellZone.setColspan(pickupWeightBreaksSize+2);
        partZone.addCell(cellZone);
        
        chk = new Chunk("Zone",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
        cellZone = new Cell(chk);
        //cellZone.setNoWrap(true);
        cellZone.setLeading(8.0f);
        cellZone.setHeader(true);
        cellZone.setBackgroundColor(Color.LIGHT_GRAY);
        partZone.addCell(cellZone);
        
        chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
        cellZone = new Cell(chk);
        //cellZone.setNoWrap(true);
        cellZone.setHeader(true);
        cellZone.setLeading(8.0f);
        cellZone.setBackgroundColor(Color.LIGHT_GRAY);
        partZone.addCell(cellZone);
        //breaksSet = pickBreaks.iterator();  // logger.info("breaksSetbreaksSet::"+breaksSet);
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
            //cellZone.setNoWrap(true);
            cellZone.setLeading(8.0f);
            cellZone.setHeader(true);
            cellZone.setBackgroundColor(Color.LIGHT_GRAY);
            partZone.addCell(cellZone);
          }
        //}
        partZone.endHeaders();
        
        chk = new Chunk("");
        cellZone = new Cell(chk);              
        cellZone.setLeading(8.0f);
       // cellZone.setHeader(true);
       // cellZone.setBackgroundColor(Color.LIGHT_GRAY);
        partZone.addCell(cellZone);
        
        chk = new Chunk("");
        cellZone = new Cell(chk);              
        cellZone.setLeading(8.0f);
        //cellZone.setHeader(true);
        //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
        partZone.addCell(cellZone);
        
        for(int i=0;i<pickupChargeBasisSize;i++)
        {
          chk = new Chunk(toTitleCase((String)pickupChargeBasisList.get(i)),FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
          cellZone = new Cell(chk);              
          //cellZone.setNoWrap(true);
          cellZone.setLeading(8.0f);
          cellZone.setHeader(true);
          //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
          partZone.addCell(cellZone);
        }
        
        
        int pikupQuoteCartRatSize	=	pickUpQuoteCartageRates.size();
        label0:
        for(int i=0;i<pikupQuoteCartRatSize;i++)
        {  
          pickQuoteCartageRates = (MultiQuoteCartageRates) pickUpQuoteCartageRates.get(i); // By Kishore For Multiple Zone Code
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
          
         
      // ------------------------------------------------------  //added by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
          if(pickupWeightBreaks == null)
          {
              continue;
          }
          String wBreak = null;
          int k = 0;
          do
          {
              if(k >= pickupWeightBreaksSize)
              {
                  continue label0;
              }
              wBreak = (String)pickupWeightBreaks.get(k);
              if(wBreak != null && pickUpZoneCodeMap.containsKey(wBreak))
              {
                  chargeRate = (String)pickUpZoneCodeMap.get(wBreak);
                  chargeRate = df.format(Double.parseDouble(chargeRate));
              } else
              {
                  chargeRate = "--";
              }
              chk = new Chunk(chargeRate, FontFactory.getFont("ARIAL", 8F, 1, Color.BLACK));
              cellZone = new Cell(chk);
              cellZone.setLeading(8F);
              partZone.addCell(cellZone);
              k++;
          } while(true);
      }
        document.add(partZone);
        }
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
        partZone.setWidth(100);
        partZone.setBorderColor(Color.black);
        partZone.setPadding(1);
        partZone.setSpacing(1);
        partZone.setAutoFillEmptyCells(true);
        //partZone.setTableFitsPage(true);
        //@@ Added by subrahmanyam for the enhancement #147062 on 08/12/2008
        chk = new Chunk("DELIVERY CARTAGE RATES AT: "+headerDOB.getDestLocName()[dc],FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
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
        cellZone.setLeading(10.0f);
        cellZone.setHeader(true);
        cellZone.setBackgroundColor(Color.ORANGE);
        cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
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
        
        
        
        chk = new Chunk("");
        cellZone = new Cell(chk);              
        cellZone.setLeading(8.0f);
        //cellZone.setHeader(true);
        //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
        partZone.addCell(cellZone);
        
        chk = new Chunk("");
        cellZone = new Cell(chk);              
        cellZone.setLeading(8.0f);
        //cellZone.setHeader(true);
        //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
        partZone.addCell(cellZone);
        
        for(int i=0;i<delChargeBasisSize;i++)
        {
          chk = new Chunk(toTitleCase((String)delChargeBasisList.get(i)),FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
          cellZone = new Cell(chk);              
          //cellZone.setNoWrap(true);
          cellZone.setLeading(8.0f);
          cellZone.setHeader(true);
          //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
          partZone.addCell(cellZone);
        }
        
        
        int delQuoteCartRtSize	=	deliveryQuoteCartageRates.size();
        for(int i=0;i<delQuoteCartRtSize;i++)
        {  
          deliQuoteCartageRates = (MultiQuoteCartageRates) deliveryQuoteCartageRates.get(i); // By Kishore For Multiple Zone Code
          
          chk = new Chunk(deliQuoteCartageRates.getZone(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
          cellZone = new Cell(chk);
          cellZone.setLeading(8.0f);
          //cellZone.setNoWrap(true);            
          partZone.addCell(cellZone);
          
          chk = new Chunk(deliQuoteCartageRates.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
          cellZone = new Cell(chk);
          cellZone.setLeading(8.0f);
          //cellZone.setNoWrap(true);            
          partZone.addCell(cellZone);
         // String charge_type=deliQuoteCartageRates.getBuyOrSellRate();  //@@Added by kiran.v
          
          
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
   
        pickUpZoneZipMap  =  legDestCharges.getPickZoneZipMap();
        //@@For Sorting the Zone Codes in an Order.
        List     list     =  new ArrayList();
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
          chk = new Chunk("PICKUP CHARGES AT: "+headerDOB.getOriginLocName()[dc],FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
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
          cellZone.setLeading(10.0f);
          //cellZone.setNoWrap(true);
          cellZone.setHeader(true);
          cellZone.setBackgroundColor(Color.ORANGE);
          cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
          cellZone.setColspan(2);
          partZone.addCell(cellZone);
          
          chk = new Chunk("Zone",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
          cellZone = new Cell(chk);
          cellZone.setLeading(8.0f);
          //cellZone.setNoWrap(true);
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
              int zipListSize	=	zipList.size();
              for(int i=0;i<zipListSize;i++)
              {
              //System.out.println("zone for ------------------------------>"+zone);
              if(zone!=null && !zone.equals("null")&& zipList.get(i)!=null&& !((String)zipList.get(i)).equals("null"))
              {
                chk = new Chunk(zone,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);
                cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                cellZone.setLeading(8.0f);
                //cellZone.setNoWrap(true);            
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
       
       deliveryZoneZipMap =  legDestCharges.getDeliveryZoneZipMap();
       
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
          chk = new Chunk("DELIVERY CHARGES AT: "+headerDOB.getDestLocName()[dc],FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
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
          cellZone.setLeading(10.0f);
          //cellZone.setNoWrap(true);
          cellZone.setHeader(true);
          cellZone.setBackgroundColor(Color.ORANGE);
          cellZone.setVerticalAlignment(cellZone.ALIGN_BOTTOM);
          
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
              //logger.info("zipList--------------------------->"+zipList);
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
        	
       // Commmented by Kishore to move the code after the for loop for Multiple lanes 
       
        //System.out.println("After zipCode header------------------------------>");
             
            /*document.close();
            //System.out.println("After     document Close----------------------------------------->");               
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
            if("on".equalsIgnoreCase(request.getParameter("print")))
            {
           
               request.getSession().setAttribute("CartageOuptutStream",fs);
            }
//@@ Added by subrahmanyam for  146460                
   
             if("on".equalsIgnoreCase(request.getParameter("print"))||"PDF".equalsIgnoreCase(request.getParameter("pdf")))
            {
               
               request.getSession().setAttribute("CartageOuptutStream",fs);
            }
      
//@@ Ended by subrahmanyam for 146460
            //f.delete();
            baos.close();*/
        //}
        }
        //} //Added by Anil.k   kishore Charges
        }
            //System.out.println("End of generation header------------------------------>");
      //Added By Kishore Podili fpr multiple Zone Codes
        if(document != null){
        	
        
		        document.close();
				// System.out.println("After document
				// Close----------------------------------------->");
				File f = new File("Cartage.pdf");
				FileOutputStream fileOutputStream = new FileOutputStream(f);
				baos.writeTo(fileOutputStream);
				fileOutputStream.close();// added by sanjay on 20/03/2006
				// @@Added by Kameswari for the WPBN issue-80440
				PdfReader reader = new PdfReader("Cartage.pdf");
				int n = reader.getNumberOfPages();
				File fs = new File(fileName);
				String space = " ";
				// we create a stamper that will copy the document to a new file
				PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(fs));

				// adding some metadata
				// adding content to each page

				int k = 0;
				PdfContentByte under;
				PdfContentByte over = null;
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
						BaseFont.WINANSI, BaseFont.EMBEDDED);
				while (k < n) {
					k++;
					over = stamp.getOverContent(k);
					//jyothi
					//over.rectangle(30, 30, 550, 800);
					over.rectangle (document.left(), document.bottom (), document.right ()-document.left (),document.top ()-document.bottom ());
					over.stroke ();
					//jyothi
					over.beginText();
					over.setFontAndSize(bf, 8);
					over.setTextMatrix(15, 15);
					over.showText("page " + k + " of " + n);

					if (k > 1) {
						over.setFontAndSize(bf, 7);
						over.showText("QUOTE REFERENCE:"+ masterDOB.getQuoteId());
						// @@Added by subrahmanyam for the WPBN:146452 on
						// 12/12/2008
						over.endText();
						over.beginText();
						over.showText("CUSTOMER NAME: "	+ headerDOB.getCustomerName());// subrahmanyam
																// 12/12/2008
						// @@ Ended by subrahmanyam for the WPBM:146452 on
						// 12/12/2008
					}
					over.endText();
				}
				stamp.close();
				// @@WPBN issue-80440
				// @@ Commented by subrahmanyam for 146460
				/*
				 * if("on".equalsIgnoreCase(request.getParameter("print"))) {
				 * 
				 * request.getSession().setAttribute("CartageOuptutStream",fs); }
				 */
				// @@ Added by subrahmanyam for 146460
				if ("on".equalsIgnoreCase(request.getParameter("print"))
						|| "PDF".equalsIgnoreCase(request.getParameter("pdf"))) {

					request.getSession()
							.setAttribute("CartageOuptutStream", fs);
				}

				// @@ Ended by subrahmanyam for 146460
				// f.delete();
				baos.close();
        
				// Added by Kishore Podili For Cartage PDF Attachement in the MAIL.  
        FileInputStream fis = new FileInputStream(fileName);
       // PdfReader preader = new PdfReader()
        ArrayList attachmentDOBList = finalDOB.getAttachmentDOBList();
        
        if(attachmentDOBList==null)
        	attachmentDOBList = new ArrayList();
    	
        MultiQuoteAttachmentDOB attachementDOB = new MultiQuoteAttachmentDOB();
    	attachementDOB.setFileName(fileName);
    	byte[] buffer = new byte[fis.available()];
    	fis.read(buffer);
        attachementDOB.setPdfFile(buffer);
    	
    	attachmentDOBList.add(attachementDOB);
    	finalDOB.setAttachmentDOBList(attachmentDOBList);
        fis.close();
        }
        
       
        
        
        
        
        
            // System.out.println("End of generation
			// header------------------------------>");
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
private MultiQuoteFinalDOB doQuoteWithChargesProcess(HttpServletRequest request,HttpServletResponse response,MultiQuoteFinalDOB finalDOB)
{
	MultiQuoteMasterDOB masterDOB = null;
	MultiQuoteFreightLegSellRates legRates = null;	
	HttpSession session = request.getSession();	
	ArrayList legArr = null;
	try
	{		
		legArr = finalDOB.getLegDetails();
		legRates = (MultiQuoteFreightLegSellRates)legArr.get(0);
		legArr.remove(0);
		if(request.getParameterValues("chargeGroupId")!=null && request.getParameterValues("chargeGroupId").length!=0)
			legRates.setChargeGroupIds(request.getParameterValues("chargeGroupId"));		
		legArr.add(0,legRates);
		finalDOB.setLegDetails(legArr);
		session.setAttribute("finlaDOB", finalDOB);
		finalDOB  = doGetHeaderAndCharges(request,response);		
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return finalDOB;
}
//Added by Rakesh on 12-01-2011
private void chargeGroupsView(HttpServletRequest request,HttpServletResponse response) throws ServletException
{
  QMSMultiQuoteSessionHome       home              = null;
  QMSMultiQuoteSession           remote            = null;
  ESupplyGlobalParameters   loginbean         = null;
                           
  String                    quoteId[]         = null;
  HttpSession                session         = request.getSession();
  ArrayList                   chargeGroups    = new ArrayList();
  String                    quoteIds          = null;
  String[]                  quoteGroupCharges = null;
  String                    chargeFlag        = "";
  String[]                  quoteGroupDesc    = null;
  String                    quoteValues       = ""; 
  HashMap                   quoteGroups       = new HashMap();
  
  try
  { 
    quoteId     = request.getParameterValues("QuoteId");
      
    loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
    //lookUpBean  = new LookUpBean();
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = (QMSMultiQuoteSession)home.create();
    chargeGroups = remote.getQuoteGroups(quoteId,loginbean);
    String temp=""; 
    int chargGrpSize	=	chargeGroups.size();
     for(int m=0;m<chargGrpSize;m++)
			 {
          quoteIds=(String)chargeGroups.get(m);
          quoteGroupCharges=(String[])quoteIds.split(",");
          if(!quoteGroupCharges[1].equals(temp))
          chargeFlag=quoteGroupCharges[1]+","+chargeFlag;
        
          temp=quoteGroupCharges[1];
      }
        quoteGroupCharges=null;
        quoteIds="";
        quoteGroupDesc=chargeFlag.split(",");	
        int quotGrpDesLen	=	quoteGroupDesc.length;
     for(int n=0;n<quotGrpDesLen;n++) 
			 {
			    quoteValues="";
				   for(int s=0;s<chargGrpSize;s++)
			     {
               quoteIds=(String)chargeGroups.get(s);
               quoteGroupCharges=(String[])quoteIds.split(",");
               if(quoteGroupDesc[n].equals(quoteGroupCharges[1])) 
                 quoteValues=quoteGroupCharges[0]+","+quoteValues;
         }
			    quoteGroups.put(quoteGroupDesc[n],quoteValues); 
			 }
    
    session.setAttribute("quoteId",quoteId); 
    session.setAttribute("quoteGroups",quoteGroups); 
    
    doDispatcher(request,response,"qms/QMSMultiQuoteChargeGroups.jsp");
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in Controller in doModifyProcess");
    logger.error(FILE_NAME+"Error in Controller in doModifyProcess");
    e.printStackTrace();
    throw new ServletException();
  }
}
//Added by Rakesh on 12-01-2011
private void quoteGroupsDtl(HttpServletRequest request,HttpServletResponse response) throws ServletException
{
  QMSMultiQuoteSessionHome       home              = null;
  QMSMultiQuoteSession           remote            = null;
  ESupplyGlobalParameters   loginbean         = null;
  MultiQuoteFinalDOB             finalDOB          = null;
  MultiQuoteMasterDOB            masterDOB         = null;
  String[]                  quoteId           = null;
  CostingMasterDOB          costingMasterDOB  = null;
  CostingHDRDOB             costingHDRDOB     = null;
  String[]                  quoteValues       = null;
 HttpSession  session      = request.getSession();
  ArrayList                 mainDtl           = new ArrayList();
  
  QMSQuoteDAO qqd=new QMSQuoteDAO();
  List lanedtls;
  try
  {
    quoteId     = (String[])session.getAttribute("quoteIdDtl");
    
    
    loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
    //lookUpBean  = new LookUpBean();
    
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = home.create();
    
    int quoteIdLen	=	quoteId.length;
    for(int m=0;m<quoteIdLen;m++)
    {
     costingHDRDOB = new CostingHDRDOB();
     finalDOB      = null; 
     costingHDRDOB.setQuoteid(quoteId[m]);          
     /*finalDOB    = remote.getMasterInfo(quoteId[m],loginbean); 
     masterDOB   = finalDOB.getMasterDOB();
     masterDOB.setCompanyId(loginbean.getCompanyId());
      finalDOB    = remote.getQuoteHeader(finalDOB);*/
     lanedtls=qqd.getQuoteLaneDetails(costingHDRDOB);
     for(int i=0;i<lanedtls.size();i++)
     {
    	 finalDOB      = null; 
    	 finalDOB    = remote.getMasterInfo(quoteId[m],loginbean); 
    	 masterDOB   = finalDOB.getMasterDOB();
         masterDOB.setCompanyId(loginbean.getCompanyId());
          finalDOB    = remote.getQuoteHeader(finalDOB);
    	 System.out.println("lane count"+lanedtls.size());
     costingHDRDOB =(CostingHDRDOB)lanedtls.get(i);
      //mainDtl.add(finalDOB);   
      costingMasterDOB = remote.getQuoteRateInfo(costingHDRDOB,loginbean);
finalDOB.setCostingMasterDOB(costingMasterDOB);
      masterDOB   = finalDOB.getMasterDOB();
      masterDOB.setOperation("QuoteGrouping");
      finalDOB.setMasterDOB(masterDOB);
      finalDOB    = remote.getQuoteContentDtl(finalDOB);
      mainDtl.add(finalDOB);
//MultiQuoteFinalDOB cmd=(MultiQuoteFinalDOB) mainDtl.get(i);
     
   }
    
    

    }
     
    session.setAttribute("mainDtl",mainDtl);
    
    doDispatcher(request,response,"qms/QMSMultiQuoteGroupView.jsp");
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in Controller in doModifyProcess");
    logger.error(FILE_NAME+"Error in Controller in doModifyProcess");
   
    e.printStackTrace();
    throw new ServletException();
  }
}
//Added by Rakesh on 12-01-2010
private void quoteGroupsSendOptions(HttpServletRequest request,HttpServletResponse response) throws ServletException
{
 
  ESupplyGlobalParameters   loginbean         = null;
  MultiQuoteFinalDOB             finalDOB          = null;
  HttpSession                    session       =  request.getSession();
      ArrayList                 mainDtl           = new ArrayList();
  ArrayList                 returnList        = null;
  ArrayList                 sentMailList      = null;
  ArrayList                 unsentMailList    = null;
  ArrayList                 sentFaxList       = null;
  ArrayList                 unsentFaxList     = null;
 //ArrayList                 filesList         = null; //@@Added for the WPBN issue-61289
  String                    operation         = null;
  String                    email             = null;
  String                    fax               = null;
  String                    print             = null;
  String                    errMsg            = "";
  int                       mailStatus        = 0;
  String                  terminalId        = null; 
  String                  quoteType         = null; 
  String                  emailText         = null; 
  QMSMultiQuoteSessionHome       home              = null;
  QMSMultiQuoteSession           remote            = null;
  try
  {
    mainDtl     = (ArrayList)session.getAttribute("mainDtl");
    operation   = request.getParameter("Operation");
    email       = request.getParameter("email");
    fax         = request.getParameter("fax");
    print       = request.getParameter("print");
    
    loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
    //lookUpBean  = new LookUpBean();
    
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean");
    remote      = home.create();
    
      if("on".equalsIgnoreCase(email) || "on".equalsIgnoreCase(fax) || "on".equalsIgnoreCase(print))
      {
         terminalId  = loginbean.getTerminalId();
         int mainDtlSize	=	mainDtl.size();
         for(int i=0;i<mainDtlSize;i++)
       {
          finalDOB  = (MultiQuoteFinalDOB)mainDtl.get(i);   
          //filesList  =  remote.getQuoteAttachmentDtls(finalDOB);//@@Added for the WPBN issue-
          //finalDOB.setAttachmentDOBList(filesList);//@@Added for the WPBN issue-
       }
         logger.info("finalDOB...."+finalDOB);
        if(finalDOB.getUpdatedReportDOB()!=null)
             quoteType  = "U";
        else
             quoteType  = "N";
        emailText = remote.getEmailText(terminalId,quoteType);
       
        finalDOB.setEmailText(emailText);
       
       returnList  = doPDFGenerationForQuoteGroup(mainDtl,request,response);
        if(returnList!=null)
        {
          sentMailList    =   (ArrayList)returnList.get(0);
          unsentMailList  =   (ArrayList)returnList.get(1);
          sentFaxList     =   (ArrayList)returnList.get(2);
          unsentFaxList   =   (ArrayList)returnList.get(3);
          
          for(int i=0;i<mainDtlSize;i++)
          {
            finalDOB  = (MultiQuoteFinalDOB)mainDtl.get(i);              
            remote.updateSendMailFlag(finalDOB.getMasterDOB().getQuoteId(),loginbean.getUserId(),"Grouping",true,1);
          }
        }
        errMsg  = "Quotes Have Been Successfully Grouped ";
        if(sentMailList!=null && sentMailList.size()>0)
          errMsg  = errMsg+" and Email(s) have been sent to "+sentMailList;
        if(sentFaxList!=null && sentFaxList.size()>0)
          errMsg  = errMsg+" and Fax has been sent to "+sentFaxList;
        if(unsentMailList!=null && unsentMailList.size()>0)
          errMsg  = errMsg+" but Email(s) have not been sent to "+unsentMailList;
        if(unsentFaxList!=null && unsentFaxList.size()>0)
          errMsg  = errMsg+" and fax was not sent to "+unsentFaxList;
        /*System.out.println("Sent mail is :"+mailStatus+"  Operation :"+operation);
        if(mailStatus==1)
        {
          setErrorRequestValues(request, "RSI", "The Quote Details  sent ", operation , "QMSQuoteController");
        }
        else
        {
          setErrorRequestValues(request, "RSI", "The Quote Details  sent ", operation , "QMSQuoteController");
        }*/
        setErrorRequestValues(request,"RSI",errMsg,operation,"QMSQuoteController");
        doDispatcher(request, response, "QMSErrorPage.jsp");
            
      }
      else
      {
        setErrorRequestValues(request, "RSI", "Quotes Have Been Successfully Grouped ", operation , "QMSQuoteController");
        doDispatcher(request, response, "QMSErrorPage.jsp");
      }        
   }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in Controller in doModifyProcess");
    logger.error(FILE_NAME+"Error in Controller in quoteGroupsSendOptions");
    e.printStackTrace();
    throw new ServletException();
  }
}
//Added by Rakesh on 12-01-2011
private ArrayList doPDFGenerationForQuoteGroup(ArrayList mainDtl,HttpServletRequest request,HttpServletResponse response)throws Exception
{
  String[]  contents            = null;
  String[]  levels              = null;
  String[]  aligns              = null;
  String[]  headFoot            = null;
  Table     content             = null;
  String[]  contactPersons      = null;
  String    contactName         = null;
  ArrayList contactList         = new ArrayList();
  ArrayList contactEmailList    = new ArrayList();
  ArrayList contactFaxList      = new ArrayList();
  ArrayList returnList          = new ArrayList();
  ArrayList sentEmailsList      = new ArrayList();
  ArrayList unsentEmailsList    = new ArrayList();
  ArrayList sentFaxList         = new ArrayList();
  ArrayList unsentFaxList       = new ArrayList();
  ArrayList  filesDOBList       = new ArrayList();
  ArrayList  filesList       = new ArrayList();
  HttpSession  session       =  null;
  
  try
  {       
          //System.out.println("PDF Generation Startd----------------------------------->");
          MultiQuoteFinalDOB             finalDOB     =  null;
          ESupplyDateUtility  eSupplyDateUtility =	new ESupplyDateUtility();
          MultiQuoteHeader    headerDOB		           =	null;
          MultiQuoteMasterDOB masterDOB               =  null ;
          CostingMasterDOB costingMasterDOB      =  null; 
          CostingChargeDetailsDOB detailsDOB     =  null; 
	      CostingLegDetailsDOB  legDetails       =  null;
	      CostingRateInfoDOB   rateDetailsDOB    =  null;
          ArrayList rateDetails                  =  new ArrayList();
          ByteArrayOutputStream baos             =  new ByteArrayOutputStream();
          String  formMailId                     =  ""; 
          String  to_emailIds                    =  "";
          String  to_FaxIds                      =  "";
          String  faxMailIds                     =  "";
          Document document                      =  new Document(PageSize.A4);
          String PDF_FILE_NAME = "Approved.pdf";
          document.addTitle("Approved Report");
          document.addSubject("Report PDF");
          document.addKeywords("Test, Key Words");
          document.addAuthor("DHL");
          document.addCreator("Auto Generated through 4S DHL");
          document.addCreationDate();
          PdfWriter writer	=	PdfWriter.getInstance(document, baos);
          document.open();
          Table partCountry;
          Chunk chk;
          Cell cellCountry;
          if(mainDtl!=null && mainDtl.size()>0)
          {
           
           finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(0);
           headerDOB		          =	  finalDOB.getHeaderDOB();
           masterDOB              =   finalDOB.getMasterDOB();
           formMailId             =   "webmaster@four-soft.com";
           ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
           eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
           //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
          
          String[] strDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
          String[] effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
          String[] validDate ;            
          
         // System.out.println("Before Document Objec--------------------------->");
          
          
          
          //document.setMargins(10,10,10,10);
          
          Table mainT = new Table(8);  
          mainT.setWidth(100);                     
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
          
          //Default Header Content Starts
          contents = masterDOB.getDefaultContent();
          headFoot = masterDOB.getDefaultHeaderFooter();
          
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
            int hFLen	=	headFoot.length;
            for(int i=0;i<hFLen;i++)
            {
              if(headFoot[i]!=null && "H".equalsIgnoreCase(headFoot[i]))
              {
                chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                cellContent = new Cell(chk);
                cellContent.setBorder(0);cellContent.setLeading(6.0f);
                cellContent.setBackgroundColor(Color.LIGHT_GRAY);                  
                cellContent.setHorizontalAlignment(cellContent.ALIGN_LEFT);//Default content is left aligned                                    
                content.addCell(cellContent);                  
              }
            }
            document.add(content);
            contents            = null;  
            headFoot            = null;
            content             = null;
          }
          //End Default Header Content
          
          //System.out.println("After Heading --------------------------->");
          }
          String   validUptoStr = null;
          String[] strDate      = null;
          String[] effDate      = null;
          String[] validDate    = null;
          StringBuffer   attentionTo  =   null;
          int mainDtlSize	=	mainDtl.size();
          for(int m=0;m<mainDtlSize;m++)
          {
           
           finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(m);
           headerDOB		          =	  finalDOB.getHeaderDOB();
           masterDOB              =   finalDOB.getMasterDOB();
           //formMailId             =   "webmaster@four-soft.com";
           ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
           eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
           //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
          
          strDate     = eSupplyDateUtility.getDisplayStringArray(headerDOB.getDateOfQuotation());
          effDate     = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
          attentionTo =  new StringBuffer("");
         
          if(headerDOB.getValidUpto()!=null)
          {
              validDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getValidUpto());
              validUptoStr = validDate[0];
          }
          if(masterDOB.getCustContactNames()!=null)
          {
            for(int i=0;i<masterDOB.getCustContactNames().length;i++)
            {
             // Logger.info(FILE_NAME,"headerDOB.getAttentionTo()::"+masterDOB.getCustomerContacts()[i]);
              attentionTo.append(masterDOB.getCustContactNames()[i]!=null?masterDOB.getCustContactNames()[i]:"");
              if(i!=masterDOB.getCustContactNames().length)
                attentionTo.append(",");
            }
          }
          partCountry = new Table(1,5); 
          partCountry.setBorderWidth(0);
          partCountry.setWidth(100);
          partCountry.setBorderColor(Color.black);
          //partCountry.setBackgroundColor(Color.ORANGE);
          partCountry.setPadding(1);
          partCountry.setBorder(0);//Modified by silpa.p on 3-06-11
          partCountry.setSpacing(0);
          partCountry.setAutoFillEmptyCells(true);
          //partCountry.setTableFitsPage(true);
          partCountry.setAlignment(partCountry.ALIGN_CENTER);
         // partCountry.setWidth(100.0f);
          
          String shipmentMode = "";
           if(finalDOB.getMasterDOB().getShipmentMode()==1)
            shipmentMode = "AIR FREIGHT PROPOSAL";
           else if(finalDOB.getMasterDOB().getShipmentMode()==2)
            shipmentMode = "SEA FREIGHT PROPOSAL";
           else if(finalDOB.getMasterDOB().getShipmentMode()==4)
            shipmentMode = "TRUCK FREIGHT PROPOSAL";
            
          
          chk = new Chunk(shipmentMode,FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
          cellCountry.setNoWrap(true); 
          cellCountry.setLeading(10.0f); 
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          partCountry.addCell(cellCountry);
          //@@Modified for the WPBN Change Request-71229
          if("MY".equalsIgnoreCase(masterDOB.getCountryId()))
          {
          chk= new Chunk(headerDOB.getOriginCountry()+" TO "+headerDOB.getDestinationCountry(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
          cellCountry = new Cell(chk);
          cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
          cellCountry.setNoWrap(true); 
          cellCountry.setLeading(10.0f); 
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk(headerDOB.getCustomerName()==null?"":headerDOB.getCustomerName(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
          cellCountry = new Cell(chk);
          cellCountry.setHeader(true);
          cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
          cellCountry.setNoWrap(true); 
          cellCountry.setLeading(10.0f);
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("ATTENTION TO: "+toTitleCase(attentionTo.toString()),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
          cellCountry = new Cell(chk);
          cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
          cellCountry.setNoWrap(true); 
          cellCountry.setLeading(10.0f);
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          partCountry.addCell(cellCountry);
          }
          else
          {
          chk= new Chunk(headerDOB.getOriginCountry()+" TO "+headerDOB.getDestinationCountry(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
          cellCountry = new Cell(chk);
          cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
          cellCountry.setNoWrap(true); 
          cellCountry.setLeading(10.0f); 
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk(headerDOB.getCustomerName()==null?"":headerDOB.getCustomerName(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
          cellCountry = new Cell(chk);
          cellCountry.setHeader(true);
          cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
          cellCountry.setNoWrap(true); 
          cellCountry.setLeading(10.0f);
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("ATTENTION TO: "+toTitleCase(attentionTo.toString()),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
          cellCountry = new Cell(chk);
          cellCountry.setWidth("100");cellCountry.setBorderWidth(0);
          cellCountry.setNoWrap(true); 
          cellCountry.setLeading(10.0f);
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          partCountry.addCell(cellCountry);
          }
          //@@WPBN Change Request-71229
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
          
          contents = masterDOB.getContentOnQuote();
          levels   = masterDOB.getLevels();
          aligns   = masterDOB.getAlign();
          headFoot = masterDOB.getHeaderFooter();
          
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
            int hFlen	=	headFoot.length;
            for(int i=0;i<hFlen;i++)
            {
              if(headFoot[i]!=null && "H".equalsIgnoreCase(headFoot[i]))
              {
                chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                cellContent = new Cell(chk);
                cellContent.setBorder(0);cellContent.setLeading(6.0f);
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
          
          partCountry  =  new Table(2,15);
          partCountry.setOffset(2);
          partCountry.setWidth(100);
          partCountry.setPadding(1);
          partCountry.setSpacing(0);
          partCountry.setBackgroundColor(Color.WHITE);
          partCountry.setBorderColor(Color.black);
          partCountry.setBorderWidth(1);
         //added by subrahmanyam for 182516
          partCountry.setTableFitsPage(true);
          partCountry.setCellsFitPage(true);
          //ended for 182516

          
          
          chk = new Chunk("SERVICE INFORMATION: ",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
          cellCountry = new Cell(chk);
          
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          cellCountry.setBackgroundColor(Color.ORANGE);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
        //@@ Commented by subrahmanyam for the enhancement #146970 & #146971            
          /*chk = new Chunk("QUOTE ID:"+new Long(masterDOB.getQuoteId()).toString(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          cellCountry.setBackgroundColor(Color.ORANGE);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);*/
//@@ Added by subrahmanyam for the enhancement #146970 & #146971            
        chk = new Chunk("QUOTE ID:"+masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setHorizontalAlignment(cellCountry.ALIGN_CENTER);
          cellCountry.setBackgroundColor(Color.ORANGE);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
         /** chk = new Chunk("Origin : ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk); 
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("   "+(headerDOB.getOriginLocName()!=null?headerDOB.getOriginLocName():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("Destination : ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk); 
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("   "+(headerDOB.getDestLocName()!=null?headerDOB.getDestLocName():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);**/
          
          chk = new Chunk("Customer : ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk); 
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("   "+(headerDOB.getCustomerName()!=null?headerDOB.getCustomerName():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
         
          
          
          chk = new Chunk("Agent: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk); 
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("   "+(headerDOB.getAgent()!=null?headerDOB.getAgent():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
      //@@ Added by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008                
         
         
//@@ Ended by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008  
                    
          
    
          
     
          
      
          
   // @@Commented by subrahmanyam for the enhancement #148546 on 09/12/2008            
         /* chk = new Chunk("Routing: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);          
          chk = new Chunk("   "+(headerDOB.getRouting()!=null?headerDOB.getRouting():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);*/
//@@ Ended by subrahmanyam for the enhanement #148546 on 09/12/2008 
          
          
          chk = new Chunk("Commodity or Product: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          //chk = new Chunk("   "+(headerDOB.getCommodity()!=null?toTitleCase(headerDOB.getCommodity()):""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//commented by silpa.p on 21-06-11
          chk = new Chunk("   "+(headerDOB.getCommodity()!=null?(headerDOB.getCommodity()):""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//modified by silpa.p on 21-06-11
          cellCountry = new Cell(chk);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("Type of service quoted: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("   "+(headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
                 
          chk = new Chunk("Notes: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
       //@@ Commented by subrahmanyam for WPBN ISSUE:-145510                                      
          /* chk = new Chunk("   "+(headerDOB.getNotes()!=null?headerDOB.getNotes().toUpperCase()+'\n'+"nnnnnnnnnn":""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          Commented by subrahmanyam for WPBN ISSUE:-145510   
         cellCountry = new Cell(chk);
         cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          
         partCountry.addCell(cellCountry);
		   */
      //@@ Ended by subrahmanyam for WPBN ISSUE:-145510  
             //@@ Added by subrahmanyam for WPBN ISSUE:-145510  
                    chk = new Chunk("   "+(headerDOB.getNotes()!=null?headerDOB.getNotes().toUpperCase():" "),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cellCountry = new Cell(chk);  
                    cellCountry.setLeading(8.0f);                      
                     partCountry.addCell(cellCountry);
              //@@ Ended by subrahmanyam for WPBN ISSUE:-145510   
          
          chk = new Chunk("Date Effective: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cellCountry = new Cell(chk);
          cellCountry.setBackgroundColor(Color.lightGray);
          cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
          partCountry.addCell(cellCountry);
          
          chk = new Chunk("   "+effDate[0],FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
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
          
          document.add(partCountry);
          partCountry.complete();
          }
          int originCount  = 0;
          int freightCount = 0;
          int destCount    = 0;
          for(int m=0;m<mainDtlSize;m++)
          {
           
           finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(m);
           // added by kiran.v on 16/09/2011
           //added by Anusha V.
           session=request.getSession();
           session.setAttribute("finalDOBPdf",finalDOB );
           headerDOB		          =	  finalDOB.getHeaderDOB();
           masterDOB              =   finalDOB.getMasterDOB();
         // System.out.println("After Page Country-------------------------------->");
          //Origin Charges
          
          costingMasterDOB         =  finalDOB.getCostingMasterDOB();
                          
          ArrayList originCharges  = (ArrayList)costingMasterDOB.getOriginList();
          
          
          
          
          int[] originIndices				= finalDOB.getSelectedOriginChargesListIndices();
          int   originChargesSize   = 0;
          
          if(originCharges!=null)
            originChargesSize		= originCharges.size();
          else
            originChargesSize		= 0;
            
          //Destination
          ArrayList destCharges		= new ArrayList();
          int[]      destIndices		= finalDOB.getSelctedDestChargesListIndices();
          int      destChargesSize = 0;
          if(destIndices!=null)
            destChargesSize	= destIndices.length;
          else
            destChargesSize	= 0;
         
          ArrayList                      frieghtChargeDetails = new ArrayList();
          destCharges =(ArrayList)costingMasterDOB.getDestinationList();
          ArrayList                      costingLegDetailsList = new ArrayList();
          costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
          //////////////////////////////
           int orgChargSize	= originCharges.size();
           for(int i=0;i<orgChargSize;i++)
           {
                detailsDOB = (CostingChargeDetailsDOB)originCharges.get(i);    
                rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                int rateDtlSize	=	rateDetails.size();
                for(int k=0;k<rateDtlSize;k++)
                {
                originCount++;
                }
           } 
           int destChargSize	=	destCharges.size();
           for(int i=0;i<destChargSize;i++)
           {
                detailsDOB = (CostingChargeDetailsDOB)destCharges.get(i);    
                rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                int rateDtlSize	=	rateDetails.size();
                for(int k=0;k<rateDtlSize;k++)
                {
                destCount++;
                }
           } 
           int costLegDtlSize	=	costingLegDetailsList.size();
           for(int s=0;s<costLegDtlSize;s++)
			        {
               legDetails = (CostingLegDetailsDOB)costingLegDetailsList.get(s);    
               frieghtChargeDetails      =  (ArrayList)legDetails.getCostingChargeDetailList();
               int frtChargDtlSize	=	frieghtChargeDetails.size();
               for(int n=0;n<frtChargDtlSize;n++)
				         {
                  detailsDOB = (CostingChargeDetailsDOB)frieghtChargeDetails.get(n);   
                  rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                  int rateDtlSize	=	rateDetails.size();
	               for(int j=0;j<rateDtlSize;j++)
                 {
                     freightCount++;                  
                 }
               }
            } 
                 
          }
          
          //////////////////////////////
          ArrayList charges				                      = finalDOB.getLegDetails();
          int       chargesSize			                    = charges.size();
          if(originCount>0)
          {
          Table chargeCountry  =  new Table(7,originCount);
          chargeCountry.setWidth(100);
          chargeCountry.setPadding(1);
          chargeCountry.setSpacing(0);
          chargeCountry.setOffset(25);
          chargeCountry.setBackgroundColor(Color.WHITE);
          chargeCountry.setBorderColor(Color.black);
          chargeCountry.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
          chargeCountry.setBorderWidth(1);
          //@@ Added by subrahmanyam for wpbn id: 181349 on 07-sep-09
              chargeCountry.setTableFitsPage(true);
              chargeCountry.setCellsFitPage(true);
           // @@ Ended for wpbn id: 181349 on 07-sep-09

          Cell cell ;
           String wBslab = "";
          chk = new Chunk("QUOTEID ",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
              //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
              
          chk = new Chunk("CHARGE NAME ",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
             // cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
              
              chk = new Chunk("BREAKPOINT",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
             // cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
              
              chk = new Chunk("CURRENCY",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
             // cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
              
              chk = new Chunk("RATE",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
             // cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
              
              chk = new Chunk("BASIS",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
              //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
              
              chk = new Chunk("RATIO",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
              //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
              
              chk = new Chunk("ORIGIN CHARGES",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setColspan(7);
              cell.setBackgroundColor(Color.ORANGE);
              cell.setNoWrap(true); cell.setLeading(8.0f);
             // cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
              chargeCountry.addCell(cell);
          for(int m=0;m<mainDtlSize;m++)
          {
           
           finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(m);
           headerDOB		          =	  finalDOB.getHeaderDOB();
           masterDOB              =   finalDOB.getMasterDOB();
           
          
           costingMasterDOB         =  finalDOB.getCostingMasterDOB();
           ArrayList originCharges  = (ArrayList)costingMasterDOB.getOriginList();
          
          MultiQuoteCharges chargesDOB           = null;
          ArrayList    originChargeInfo     = null;
          int          originChargesInfoSize= 0;
          MultiQuoteChargeInfo chargeInfo        = null;
          if(originCharges.size()>0)
				    {
              
              int orgChargSize	=	originCharges.size();
              for(int i=0;i<orgChargSize;i++)
              {
                detailsDOB = (CostingChargeDetailsDOB)originCharges.get(i);    
                rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                
                originChargesInfoSize	= rateDetails.size();
                for(int k=0;k<originChargesInfoSize;k++)
                {
                  CostingRateInfoDOB crateDetailsDOB = (CostingRateInfoDOB)rateDetails.get(k);
                  if(k==0)
                  {
                   //@@ Commented by subrahmanyam for the enhancement #146971 and #146970 on 10/12/2008                    
                    /*chk = new Chunk(new Long(masterDOB.getQuoteId()).toString(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    //cell.setRowspan(originChargesInfoSize);
                    cell.setBackgroundColor(Color.lightGray);
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
                    chargeCountry.addCell(cell);*/
                       //@@ Added by subrahmanyam for the enhancement #146971 and 146970 on 10/12/2008    
                    chk = new Chunk(masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBackgroundColor(Color.lightGray);
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    chargeCountry.addCell(cell);
                     //@@ Ended by subrahmanyam for the enhanemenet #146971 and #146970 on 10/12/2008 
                    
                    //@@ commented and modified by subrahmanyam for 181349  on 07-sep-09 & 202166 on 7-apr-10
                   // chk = new Chunk(detailsDOB.getChargeDescId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                   chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():detailsDOB.getChargeDescId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//ended for 181349
                    cell = new Cell(chk);
                    //cell.setRowspan(originChargesInfoSize);
                    cell.setBackgroundColor(Color.lightGray);
                    // cell.setNoWrap(true); //commented for 181349
                    cell.setLeading(8.0f);
                    //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
                    chargeCountry.addCell(cell);
                  }   
                  else
                  {                    
                    cell = new Cell("");
                    //cell.setRowspan(originChargesInfoSize);
                    cell.setBackgroundColor(Color.lightGray);
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    cell.setBorderWidth(0f);
                    //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
                    chargeCountry.addCell(cell);                      
                    
                    cell = new Cell("");
                    //cell.setRowspan(originChargesInfoSize);
                    cell.setBackgroundColor(Color.lightGray);
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    cell.setBorderWidth(0f);
                    //cell.setVerticalAlignment(cell.ALIGN_MIDDLE);
                    chargeCountry.addCell(cell);
                  }    
                    chk = new Chunk(crateDetailsDOB.getWeightBreakSlab(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBackgroundColor(Color.lightGray);
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    chargeCountry.addCell(cell);
                    
                    chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBackgroundColor(Color.lightGray);
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    chargeCountry.addCell(cell);
                    
                    chk = new Chunk(crateDetailsDOB.getRate()+"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    chargeCountry.addCell(cell);
                    
                     //added by subrahmanyam for 183812
                     if(crateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") || ("BOTH".equalsIgnoreCase(detailsDOB.getRateType()) && "F".equalsIgnoreCase(crateDetailsDOB.getRateIndicator()) ))
                    {
                      wBslab = "MIN";
                    }
                    else
                    wBslab =detailsDOB.getChargeBasisDesc();
                    // @@ Commented & Added by subrahmanyam for the wpbn id: 182516 on 10/09/09
                    //chk = new Chunk(crateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Pershipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    //chk = new Chunk(crateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?" Per Shipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    chk = new Chunk(wBslab.equalsIgnoreCase("Min")?" Per Shipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                   cell = new Cell(chk);       
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    chargeCountry.addCell(cell);                      
                    
                    chk = new Chunk(detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);           
                    cell.setNoWrap(true); cell.setLeading(8.0f);
                    chargeCountry.addCell(cell);                     
                    
                }
              }
          }
         } 
          document.add(chargeCountry);
        } 
          if(freightCount>0)
          {
          Table chargeCountry1  =  new Table(7,freightCount);
          chargeCountry1.setWidth(100);
          chargeCountry1.setPadding(1);
          chargeCountry1.setSpacing(0);
          chargeCountry1.setOffset(25);
          chargeCountry1.setBackgroundColor(Color.WHITE);
          chargeCountry1.setBorderColor(Color.black);
          chargeCountry1.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
          chargeCountry1.setBorderWidth(1);
          //@@ Added by subrahmanyam for wpbn id: 181349 on 07-sep-09
          chargeCountry1.setTableFitsPage(true);
          chargeCountry1.setCellsFitPage(true);
          // @@ Ended for wpbn id: 181349 on 07-sep-09

          Cell cell1 ;
            chk = new Chunk(" QUOTEID",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setBackgroundColor(Color.ORANGE);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
              //cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
            chk = new Chunk("CHARGE NAME ",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setBackgroundColor(Color.ORANGE);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
              //cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
              
              chk = new Chunk("BREAKPOINT",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setBackgroundColor(Color.ORANGE);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
              //cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
              
              chk = new Chunk("CURRENCY",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setBackgroundColor(Color.ORANGE);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
              //cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
              
              chk = new Chunk("RATE",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setBackgroundColor(Color.ORANGE);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
             // cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
              
              chk = new Chunk("BASIS",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setBackgroundColor(Color.ORANGE);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
              //cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
              
              chk = new Chunk("RATIO",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setBackgroundColor(Color.ORANGE);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
             // cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
              
           chk = new Chunk("FREIGHT CHARGES",FontFactory.getFont("ARIAL",8, Font.BOLD,Color.BLACK));
              cell1 = new Cell(chk);
              cell1.setColspan(7);
              cell1.setNoWrap(true); cell1.setLeading(8.0f);
              cell1.setBackgroundColor(Color.ORANGE);
              //cell1.setVerticalAlignment(cell1.ALIGN_MIDDLE);
              chargeCountry1.addCell(cell1);
            //Freight Charges
           // System.out.println("After         Origin Charges --------------------------------->");
            int       freightChargesSize                  = 0;
            int       freightChargesInfoSize              = 0;
            int[]     frtIndices                          = null;
            MultiQuoteFreightLegSellRates       legCharges	    = null;
            String                          wBSlab        = "";//added by subrahmanyam for 182516 on 10-sep-09             
            int mDtlSize	= mainDtl.size();
            for(int m=0;m<mDtlSize;m++)
          {
           
            finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(m);
            headerDOB		           =	  finalDOB.getHeaderDOB();
            masterDOB              =    finalDOB.getMasterDOB();
            ArrayList costingLegDetailsList = new ArrayList();
            costingMasterDOB        =  finalDOB.getCostingMasterDOB();
            costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
            ArrayList frieghtChargeDetails = new ArrayList();
            String          rDescription  = ""; // Added by subrahmanyam for 182516 on 10/09/09
            if(costingLegDetailsList.size()>0)
				       {
              
              int costLegDtlSize	=	costingLegDetailsList.size();
              for(int s=0;s<costLegDtlSize;s++)
			           {
               legDetails = (CostingLegDetailsDOB)costingLegDetailsList.get(s);    
               frieghtChargeDetails      =  (ArrayList)legDetails.getCostingChargeDetailList();
               int frtLegDtlSize	=	frieghtChargeDetails.size();
               int frtChargDtlSize	=	frieghtChargeDetails.size();
               for(int n=0;n<frtChargDtlSize;n++)
				           {
                  detailsDOB = (CostingChargeDetailsDOB)frieghtChargeDetails.get(n);   
                  rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                  int rateDtlSize	=	rateDetails.size();
	                for(int j=0;j<rateDtlSize;j++)
				           {
				            rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);
                  
                  
                    
                  
                  if(j==0)
                  {
                    
                    chk = new Chunk(legDetails.getOrigin()+"-"+legDetails.getDestination(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                    cell1 = new Cell(chk);
                    cell1.setColspan(7);
                    cell1.setBackgroundColor(Color.WHITE);
                    cell1.setLeading(8.0f);
                    chargeCountry1.addCell(cell1);
                    
                  //@@ Commented by subrahmanyam for the enhancement #146971 and 1469710 on 10/12/2008                      
                    /*chk = new Chunk(new Long(masterDOB.getQuoteId()).toString(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk);
                    //cell1.setRowspan(rateDetails.size());
                    cell1.setNoWrap(true); cell2.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    chargeCountry1.addCell(cell1);*/
//@@ Added by subrahmanyam for the enhancement #146971 and #146970 on 10/12/2008
                  chk = new Chunk(masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk);
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    chargeCountry1.addCell(cell1);
//@@ Ended by subrahmanyam for the enhancement #146971 and 146970 on 10/12/2008    
                    // COMMENTED AND MODIFIED BY SUBRAHMANYAM FOR 182516
                    //chk = new Chunk(detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    chk = new Chunk(rateDetailsDOB.getRateDescription()==null?"":rateDetailsDOB.getRateDescription(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk);
                    //cell1.setRowspan(rateDetails.size());
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    chargeCountry1.addCell(cell1);
                    rDescription = rateDetailsDOB.getRateDescription();
                  }
                  else
                  {
                    cell1 = new Cell("");
                    //cell1.setRowspan(rateDetails.size());
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    cell1.setBorderWidth(0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    chargeCountry1.addCell(cell1);
                    
                                     //@@ Commented by subrahmanyam for 182516 on 10/09/09
                   /*
                    cell1 = new Cell("");
                    //cell1.setRowspan(rateDetails.size());
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    cell1.setBorderWidth(0f);
                    chargeCountry1.addCell(cell1);
                    */
                //@@ added by subrahmanyam for 182516 on 10/09/09
                    if(rDescription.equalsIgnoreCase(rateDetailsDOB.getRateDescription()))
                    {
                     chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell1 = new Cell(chk);
                    //cell1.setRowspan(rateDetails.size());
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    cell1.setBorderWidth(0f);
                    chargeCountry1.addCell(cell1);
                    }
                    else
                    {
                      chk = new Chunk(rateDetailsDOB.getRateDescription()==null?"":rateDetailsDOB.getRateDescription(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk);
                    //cell1.setRowspan(rateDetails.size());
                    //cell1.setNoWrap(true);
                    cell1.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    chargeCountry1.addCell(cell1);
                    rDescription = rateDetailsDOB.getRateDescription();
                    }
                    //ended by subrahmanyam for 182516 on 10/09/09

                  } 
                    if("FSBASIC".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSBASIC".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()))
                        rateDetailsDOB.setWeightBreakSlab("Basic Charge");
                    if("FSMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()))
                        rateDetailsDOB.setWeightBreakSlab("Minimum");
                    if("FSKG".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSKG".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()))
                        rateDetailsDOB.setWeightBreakSlab("Flat Rate");
                        
                   chk = new Chunk(rateDetailsDOB.getWeightBreakSlab(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk);
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    chargeCountry1.addCell(cell1);
                    
                    chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk);
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    cell1.setBackgroundColor(Color.lightGray);
                    chargeCountry1.addCell(cell1);
                    
                    chk = new Chunk(rateDetailsDOB.getRate()+"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk);
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    chargeCountry1.addCell(cell1);
                    
                    //@@ Added by subrahmanyam for wpbn id: 182516
                    // MODIFIED BY SUBRAHMANYAM FOR 183812
                    if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") || rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Minimum") || ("BOTH".equalsIgnoreCase(detailsDOB.getRateType()) && "FLAT".equalsIgnoreCase(rateDetailsDOB.getRateIndicator())))
                    {
                      wBSlab  = "MIN";
                    }
                    else
                    {
                      wBSlab  = rateDetailsDOB.getWeightBreakSlab();
                    }
                    //@@ commented by subrahmanyam for wpbn id: 182516
                    //chk = new Chunk(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Pershipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    //@@ Added by subrahmanyam for wpbn id: 182516
                    chk = new Chunk(wBSlab.equalsIgnoreCase("Min")?" Per Shipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    //@@ Ended  for wpbn id: 182516
                    cell1 = new Cell(chk); 
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    chargeCountry1.addCell(cell1);                      
                    
                    chk = new Chunk(detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell1 = new Cell(chk); 
                    cell1.setNoWrap(true); cell1.setLeading(8.0f);
                    chargeCountry1.addCell(cell1);                      
                    
                }
              }
            }
          }
          }
             document.add(chargeCountry1);
          } 
            // System.out.println("After Charges--------------------------------->");
             ArrayList  destChargeInfo             =  null;
             //int[]      destChargeInfo        =  null;
             int        destChargesInfoSize        =  0;
          if(destCount>0)
          { 
          Table chargeCountry2  =  new Table(7,destCount);
          chargeCountry2.setWidth(100);
          chargeCountry2.setPadding(1);
          chargeCountry2.setSpacing(0);
          chargeCountry2.setOffset(25);
          chargeCountry2.setBackgroundColor(Color.WHITE);
          chargeCountry2.setBorderColor(Color.black);
          chargeCountry2.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
          chargeCountry2.setBorderWidth(1);
         //@@ Added by subrahmanyam for wpbn id: 181349 on 07-sep-09
          chargeCountry2.setTableFitsPage(true);
          chargeCountry2.setCellsFitPage(true);
          // @@ Ended for wpbn id: 181349 on 07-sep-09

          Cell cell2 ;
          String wBslab ="";
          chk = new Chunk("QUOTEID",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell2 = new Cell(chk);
              cell2.setNoWrap(true); cell2.setLeading(8.0f);
              cell2.setBackgroundColor(Color.ORANGE);
              //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
              chargeCountry2.addCell(cell2);
              
               chk = new Chunk("CHARGE NAME",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell2 = new Cell(chk);
              cell2.setNoWrap(true); cell2.setLeading(8.0f);
              cell2.setBackgroundColor(Color.ORANGE);
              //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
              chargeCountry2.addCell(cell2);
              
              chk = new Chunk("BREAKPOINT",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell2 = new Cell(chk);
              cell2.setNoWrap(true); cell2.setLeading(8.0f);
              cell2.setBackgroundColor(Color.ORANGE);
              //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
              chargeCountry2.addCell(cell2);
              
              chk = new Chunk("CURRENCY",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell2 = new Cell(chk);
              cell2.setNoWrap(true); cell2.setLeading(8.0f);
              cell2.setBackgroundColor(Color.ORANGE);
              //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
              chargeCountry2.addCell(cell2);
              
              chk = new Chunk("RATE",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell2 = new Cell(chk);
              cell2.setNoWrap(true); cell2.setLeading(8.0f);
              cell2.setBackgroundColor(Color.ORANGE);
              //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
              chargeCountry2.addCell(cell2); 
              
              chk = new Chunk("BASIS",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell2 = new Cell(chk);
              cell2.setNoWrap(true); cell2.setLeading(8.0f);
              cell2.setBackgroundColor(Color.ORANGE);
              //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
              chargeCountry2.addCell(cell2);
              
              chk = new Chunk("RATIO",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
              cell2 = new Cell(chk);
              cell2.setNoWrap(true); cell2.setLeading(8.0f);
              cell2.setBackgroundColor(Color.ORANGE);
              //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
              chargeCountry2.addCell(cell2);
              
             chk = new Chunk("DESTINATION CHARGES",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
             cell2 = new Cell(chk);
             cell2.setNoWrap(true); cell2.setLeading(8.0f);
             cell2.setColspan(7);
             cell2.setBackgroundColor(Color.ORANGE);
             //cell2.setVerticalAlignment(cell2.ALIGN_MIDDLE);
             chargeCountry2.addCell(cell2);
              int mDtlSize	=	mainDtl.size();
         for(int m=0;m<mDtlSize;m++)
          {
           
            finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(m);
            headerDOB		           =	  finalDOB.getHeaderDOB();
            masterDOB              =    finalDOB.getMasterDOB();
            costingMasterDOB        =  finalDOB.getCostingMasterDOB();
            ArrayList destCharges =(ArrayList)costingMasterDOB.getDestinationList();
          if(destCharges.size()>0)
				    {
             
              int destChargSize	=	destCharges.size();
              
              for(int i=0;i<destChargSize;i++)
              {
                
                detailsDOB = (CostingChargeDetailsDOB)destCharges.get(i);    
                rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                
                //originChargesInfoSize	= rateDetails.size();
                int rDtlSize	=	rateDetails.size();
                for(int k=0;k<rDtlSize;k++)
                {
                  CostingRateInfoDOB crateDetailsDOB = (CostingRateInfoDOB)rateDetails.get(k);
                
                
                  if(k==0)
                  {
                    
                //@@ Commented by subrahmanyam for the enhancement #146971 and 1469710 on 10/12/2008                      
                    /*chk = new Chunk(new Long(masterDOB.getQuoteId()).toString(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell2 = new Cell(chk);
                    //cell2.setRowspan(rateDetails.size());
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    cell2.setBackgroundColor(Color.lightGray);
                    chargeCountry2.addCell(cell2);*/
              //@@ Added by subrahmanyam for the enhancement #146971 and #146970 on 10/12/2008
                  chk = new Chunk(masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell2 = new Cell(chk);
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    cell2.setBackgroundColor(Color.lightGray);
                    chargeCountry2.addCell(cell2);
              //@@ Ended by subrahmanyam for the enhancement #146971 and 146970 on 10/12/2008    
                    
                  //  chk = new Chunk(detailsDOB.getChargeDescId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    //@@ Added by subrahmanyam for the wpbn id: 181349  on 07-sep-09
                   chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():detailsDOB.getChargeDescId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//ended for 181349
                    cell2 = new Cell(chk);
                    //cell2.setRowspan(rateDetails.size());
                    //cell2.setNoWrap(true); //commented for 181349
                    cell2.setLeading(8.0f);
                    cell2.setBackgroundColor(Color.lightGray);
                    chargeCountry2.addCell(cell2);
                  } 
                  else
                  {                                           
                    cell2 = new Cell("");
                    //cell2.setRowspan(rateDetails.size());
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    cell2.setBackgroundColor(Color.lightGray);
                    cell2.setBorderWidth(0f);
                    chargeCountry2.addCell(cell2);
                                          
                    cell2 = new Cell("");
                    //cell2.setRowspan(rateDetails.size());
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    cell2.setBackgroundColor(Color.lightGray);
                     cell2.setBorderWidth(0f);
                    chargeCountry2.addCell(cell2);
                  }
                    chk = new Chunk(crateDetailsDOB.getWeightBreakSlab(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell2 = new Cell(chk);
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    cell2.setBackgroundColor(Color.lightGray);
                    chargeCountry2.addCell(cell2);
                    
                    chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell2 = new Cell(chk);
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    cell2.setBackgroundColor(Color.lightGray);
                    chargeCountry2.addCell(cell2);
                    
                    chk = new Chunk(crateDetailsDOB.getRate()+"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell2 = new Cell(chk);
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    chargeCountry2.addCell(cell2);
                    
                     //@@ Commented & Added  by subrahmanyam for 182516 on 10/09/09
                   // chk = new Chunk(crateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Pershipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                  //ADDED BY SUBRAHMANYAM FOR 183812
                   if(crateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") || ( "BOTH".equalsIgnoreCase(detailsDOB.getRateType())&&"F".equalsIgnoreCase(crateDetailsDOB.getRateIndicator())) )
                   {
                     wBslab = "MIN";
                   }
                   else
                   wBslab = detailsDOB.getChargeBasisDesc();
                   // COMMENTED & added FOR 183812 ON 24-09-09
                   //chk = new Chunk(crateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?" Per Shipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                   chk = new Chunk(wBslab.equalsIgnoreCase("Min")?" Per Shipment ":detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                   cell2 = new Cell(chk); 
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    chargeCountry2.addCell(cell2);                      
                    
                    chk = new Chunk(detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell2 = new Cell(chk);       
                    cell2.setNoWrap(true); cell2.setLeading(8.0f);
                    chargeCountry2.addCell(cell2);
                }
              }
           }
         }     
              document.add(chargeCountry2); 
          }
          
          Table notes  =  new Table(2,mainDtl.size());
          boolean displayFlag   = false;
          notes.setWidth(100);
          notes.setPadding(1);
          notes.setSpacing(0);
          notes.setOffset(5);
          notes.setBackgroundColor(Color.WHITE);
          notes.setBorderColor(Color.black);
          notes.setDefaultHorizontalAlignment(Element.ALIGN_CENTER);
          notes.setBorderWidth(1);
          notes.setTableFitsPage(true);// added by subrahmanyam for quoteGrouping issue 184848
          notes.setCellsFitPage(true);// added by subrahmanyam for quoteGrouping issue 184848

          Cell cell3 ;
          chk = new Chunk("QUOTEID ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cell3 = new Cell(chk);
          cell3.setNoWrap(true); cell3.setLeading(8.0f);
          cell3.setBackgroundColor(Color.ORANGE);
          //cell3.setVerticalAlignment(cell3.ALIGN_MIDDLE);
          //cell3.setBackgroundColor(Color.lightGray);
          notes.addCell(cell3);
          
          chk = new Chunk("NOTES",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
          cell3 = new Cell(chk);
          cell3.setNoWrap(true); cell3.setLeading(8.0f);
          cell3.setBackgroundColor(Color.ORANGE);
          //cell3.setVerticalAlignment(cell3.ALIGN_MIDDLE);
          
          notes.addCell(cell3);
           int mDtlSize	=	mainDtl.size();
          for(int m=0;m<mDtlSize;m++)
          {
           
            finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(m);
            headerDOB		           =	  finalDOB.getHeaderDOB();
            masterDOB              =    finalDOB.getMasterDOB();
            costingMasterDOB        =  finalDOB.getCostingMasterDOB();
            if(costingMasterDOB!=null)
				      {
					   	}
           //@@ Commented by subrahmanyam for the enhancement #146971 and 1469710 on 10/12/2008                      
                    /*chk = new Chunk(new Long(masterDOB.getQuoteId()).toString(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell3 = new Cell(chk);
                    //cell3.setRowspan(rateDetails.size());
                    cell3.setNoWrap(true); cell3.setLeading(8.0f);
                    cell3.setBackgroundColor(Color.lightGray);
                    notes.addCell(cell3);*/
             //@@ Added by subrahmanyam for the enhancement #146971 and #146970 on 10/12/2008
                                      //commented by subrahmanyam for quote grouping issue
             /*
                  chk = new Chunk(masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell3 = new Cell(chk);
                    cell3.setNoWrap(true); cell3.setLeading(8.0f);
                    cell3.setBackgroundColor(Color.lightGray);
                    notes.addCell(cell3);
                 //@@ Ended by subrahmanyam for the enhancement #146971 and 146970 on 10/12/2008

           chk = new Chunk(" ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cell3 = new Cell(chk);
            //cell3.setRowspan();
            cell3.setNoWrap(true); cell3.setLeading(8.0f);
            cell3.setBackgroundColor(Color.lightGray);
            notes.addCell(cell3);
        */
//@@ Added by subrahmanyam for Quote Grouping Issue 184848 on 30/sep/09
            if(finalDOB.getExternalNotes()!=null && finalDOB.getExternalNotes().length>0)
            {
                int notesLength = finalDOB.getExternalNotes().length;
              for(int n=0;n<notesLength;n++)
              {
                  if(n==0)
                  {
                    chk = new Chunk(masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell3 = new Cell(chk);
                        cell3.setNoWrap(true); cell3.setLeading(8.0f);
                        cell3.setBackgroundColor(Color.lightGray);
                        notes.addCell(cell3);
                        //@@ Ended by subrahmanyam for the enhancement #146971 and 146970 on 10/12/2008

                    chk = new Chunk(finalDOB.getExternalNotes()[n],FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell3 = new Cell(chk);
                        //cell3.setRowspan();
                        //cell3.setNoWrap(true);
                        cell3.setHorizontalAlignment(cell3.ALIGN_LEFT);
                        cell3.setLeading(8.0f);
                        cell3.setBackgroundColor(Color.lightGray);
                        notes.addCell(cell3);
                  }
                  else
                  {
                    chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell3 = new Cell(chk);
                        cell3.setNoWrap(true);
                        cell3.setLeading(8.0f);
                        cell3.setBackgroundColor(Color.lightGray);
                        notes.addCell(cell3);
                        //@@ Ended by subrahmanyam for the enhancement #146971 and 146970 on 10/12/2008

                    chk = new Chunk(finalDOB.getExternalNotes()[n],FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell3 = new Cell(chk);
                        //cell3.setRowspan();
                       // cell3.setNoWrap(true);
                       cell3.setHorizontalAlignment(cell3.ALIGN_LEFT);
                        cell3.setLeading(8.0f);
                        cell3.setBackgroundColor(Color.lightGray);
                        notes.addCell(cell3);
                  }
              }
            }
            else
            {
              chk = new Chunk(masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    cell3 = new Cell(chk);
                    cell3.setNoWrap(true); cell3.setLeading(8.0f);
                    cell3.setBackgroundColor(Color.lightGray);
                    notes.addCell(cell3);

           chk = new Chunk(" ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cell3 = new Cell(chk);
            //cell3.setRowspan();
            cell3.setNoWrap(true); cell3.setLeading(8.0f);
            cell3.setBackgroundColor(Color.lightGray);
            notes.addCell(cell3);
            }
//@@ Ended by subrahmanyam for Quote Grouping Issue 184848 on 30/09/09

              
            contents = masterDOB.getContentOnQuote();
            levels   = masterDOB.getLevels();
            aligns   = masterDOB.getAlign();
            headFoot = masterDOB.getHeaderFooter();
            
            if(contents!=null && contents.length>0)
            {
          	  int hFLen	=	headFoot.length;
              for(int i=0;i<hFLen;i++)
              {
                if(headFoot[i]!=null && "F".equalsIgnoreCase(headFoot[i]))
                {
                    displayFlag = true;
                    break;
                }
              }
              if(displayFlag)
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
                chk = new Chunk("QUOTEID : "+masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                cellContent = new Cell(chk);
                cellContent.setBorder(0);
                cellContent.setLeading(6.0f);
                cellContent.setHorizontalAlignment(cellContent.ALIGN_CENTER);
                cellContent.setBackgroundColor(Color.ORANGE);
                content.addCell(cellContent);                
                for(int i=0;i<hFLen;i++)
                {
                  if(headFoot[i]!=null && "F".equalsIgnoreCase(headFoot[i]))
                  {
                    chk = new Chunk(contents[i]!=null?contents[i].toUpperCase():"",FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                    cellContent = new Cell(chk);
                    cellContent.setBorder(0);cellContent.setLeading(6.0f); 
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
            }
           }             
           document.add(notes); 
          //Default Footer Content Starts
          contents = masterDOB.getDefaultContent();
          headFoot = masterDOB.getDefaultHeaderFooter();
          int hFLen=0;
          if(headFoot!=null)
          hFLen	=	headFoot.length;
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
            for(int i=0;i<hFLen;i++)
            {
              if(headFoot[i]!=null && "F".equalsIgnoreCase(headFoot[i]))
              {
                chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                cellContent = new Cell(chk);
                cellContent.setBorder(0);cellContent.setLeading(6.0f);
                cellContent.setBackgroundColor(Color.LIGHT_GRAY);                  
                cellContent.setHorizontalAlignment(cellContent.ALIGN_LEFT);//Default content is left aligned                                    
                content.addCell(cellContent);  
                
              }
            }
            document.add(content);            
          }
          //End Default Footer Content
          
           // System.out.println("After     document Close----------------------------------------->");
 
            document.close();
           // Thread.sleep(100);
           //logger.info("thread");
            //String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime()+masterDOB.getQuoteId());  //@@ Commented by subrahmanyam for the Enhancement #146971 on 2/12/08
           // String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime()+Long.parseLong(masterDOB.getQuoteId()));  //@@ Added by subrahmanyam for the Enhancement #146971 on 2/12/08
            //Commenetd by Anusha V 
            //String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime())+masterDOB.getQuoteId(); //@@ Added by subrahmanyam for the Enhancement #146971 on 2/12/08
            //Added by Anusha V
             String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime());
            file_tsmp        = file_tsmp.replaceAll("\\:","");
            file_tsmp        = file_tsmp.replaceAll("\\.","");
            file_tsmp        = file_tsmp.replaceAll("\\-","");
            file_tsmp        = file_tsmp.replaceAll(" ","");
            File f = new File("Quote"+file_tsmp+".pdf");
            FileOutputStream  fileOutputStream= new FileOutputStream(f);
            baos.writeTo(fileOutputStream);
            fileOutputStream.close();
          //  logger.info("masterDOB.getUserId()"+masterDOB.getUserId());
            if("on".equalsIgnoreCase(request.getParameter("print")))
            {
            
              request.getSession().setAttribute("QuoteOuptutStream",f);
            }
             //  session.setAttribute("UserId",masterDOB.getUserId());
            
            int noOfQuotes   = mainDtl.size();
            StringBuffer    quoteIds          =   new StringBuffer("");
            String          quoteType         =   "";
            String          terminalAddress   =   "";
            String          creatorDetails    =   "";
            String          fromEmailId       =   "";
            String          body              =   "";
            String          countryCode       =   "";
            for(int i=0;i<noOfQuotes;i++)
            { 
              finalDOB               =   (MultiQuoteFinalDOB)mainDtl.get(i); 
            /*  //@@Added for the WPBN issue-
              filesDOBList           =   finalDOB.getAttachmentDOBList(); 
              for(int j=0;j<filesDOBList.size();j++)
              {
               filesList.add(filesDOBList.get(j));
              } //@@Added for the WPBN issue-*/
              
              if(i==0)
              {
                quoteType            =   ""+finalDOB.getMasterDOB().getShipmentMode();
                terminalAddress      =   finalDOB.getMasterDOB().getTerminalAddress();
                creatorDetails       =   finalDOB.getMasterDOB().getCreatorDetails();
                fromEmailId          =   finalDOB.getMasterDOB().getUserEmailId();
                countryCode          =   finalDOB.getHeaderDOB().getCustCountyCode();
                to_FaxIds            =   finalDOB.getHeaderDOB().getCustFaxNo();
                to_emailIds          =   finalDOB.getHeaderDOB().getCustEmailId();
              }
              quoteIds.append(finalDOB.getMasterDOB().getQuoteId());
              if(i!=noOfQuotes-1)
                quoteIds.append(",");
              
              contactPersons  =  finalDOB.getMasterDOB().getCustContactNames();
              if(contactPersons!=null)
              {
              	int contPersLen	=	contactPersons.length;
                for(int k=0;k<contPersLen;k++)
                {
                  if(!contactList.contains(finalDOB.getMasterDOB().getCustContactNames()[k]))
                  {
                      contactList.add(finalDOB.getMasterDOB().getCustContactNames()[k]);
                      contactEmailList.add(finalDOB.getMasterDOB().getCustomerContactsEmailIds()[k]);
                      contactFaxList.add(finalDOB.getMasterDOB().getCustomerContactsFax()[k]);
                  }
                }
                  
              }
              
            }
              
              if("1".equalsIgnoreCase(quoteType))
                  quoteType = "Airfreight";
              else if("2".equalsIgnoreCase(quoteType))
                  quoteType = "Seafreight";
              else if("4".equalsIgnoreCase(quoteType))
                  quoteType = "Truckfreight";
              
                String subject  = "DHL Global Forwarding Quotation, Multiple "+quoteType+", Quote References "+quoteIds.toString();
                String message  = "";
                //finalDOB.getFlagsDOB().setQuoteStatusFlag("PEN");//@@Quote is Pending
                //ReportsSessionBeanHome home		= (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
                //ReportsSession remote	= (ReportsSession)home.create();
                //if(contactList.size()==0)
                //{
                    to_emailIds            =   to_emailIds.replaceAll(";",",");
                  /*message  = "Dear Customer,\n\nThank you for the opportunity to provide this quotation.  All information is contained within the attachment."+
                              "  Should you have any queries please do not hesitate to contact us.\n\n\nRegards,\n"+creatorDetails+"\n"+terminalAddress;*/
                    //message   = "Dear Customer,\n\n"+finalDOB.getEmailText()+"\n\n\nRegards,\n"+creatorDetails+"\n"+terminalAddress+"\n"+;       
                         if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                             body  = "Dear Customer,\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                                     "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
                          }
                          else
                          {
                               body  = "Dear Customer,\n\n"+(finalDOB.getEmailText()!=null?finalDOB.getEmailText():"")+"\n\n\n"+(masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():"")+"\n\n"+(masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():"")+
                               "\n"+(masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():"")+"\n\n"+"Phone  "+(masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():"")+"\n"+"Fax    "+(masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():"")+"\n"+"Mobile "+(masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():"")+"\n\nEmail "+(masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"");  //@@Modified by Kameswari for the WPBN issue-61303
                          } 
                     if("on".equalsIgnoreCase(request.getParameter("email")))
                    {
                    try
                    {
                      sendMail(fromEmailId,to_emailIds,subject,message,"Quote"+file_tsmp+".pdf",null);
                      sentEmailsList.add(to_emailIds);
                    }
                    catch(FoursoftException fs)
                    {
                      //Logger.error(FILE_NAME,"Error While Sending Mail.."+fs.getMessage());
                      logger.error(FILE_NAME+"Error While Sending Mail.."+fs.getMessage());
                      fs.printStackTrace();
                      unsentEmailsList.add(to_emailIds);
                    }
                  }
                  if("on".equalsIgnoreCase(request.getParameter("fax")))
                  {
                    if("SG".equalsIgnoreCase(countryCode))
                        faxMailIds   = "fax#"+to_FaxIds+"@tcdhl.com";
                    else
                        faxMailIds   =  "ifax#"+to_FaxIds+"@tcdhl.com";
                    try
                    {
                      sendMail(fromEmailId,faxMailIds,subject,message,"Quote"+file_tsmp+".pdf",null);
                      sentFaxList.add(to_FaxIds);
                    }
                    catch(FoursoftException fs)
                    {
                      //Logger.error(FILE_NAME,"Error While Sending Fax.."+fs.getMessage());
                      logger.error(FILE_NAME+"Error While Sending Fax.."+fs.getMessage());
                      fs.printStackTrace();
                      unsentFaxList.add(to_FaxIds);
                    }
                  }
                //}
                //else
                //{
                  int contactSize = contactList.size();
                  for(int i=0;i<contactSize;i++)
                  {
                    contactName = (String)contactList.get(i);
                    /*message = "Dear "+contactName+",\n\nThank you for the opportunity to provide this Quotation. All information is contained within the attachment."+
                     " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+creatorDetails+"\n"+terminalAddress;*/
                   // message = "Dear "+contactName+",\n\n"+finalDOB.getEmailText()+"\n\n\nRegards,\n"+creatorDetails+"\n"+terminalAddress;         
                 if(finalDOB.getUpdatedReportDOB()!=null)
                          {
                             body  = "Dear "+(contactName!=null?contactName:"")+",\n\nThis is a replacement quotation reference "+request.getAttribute("quoteId")+",replacing "+finalDOB.getUpdatedReportDOB().getQuoteId()+
                               ", due to a change in "+(finalDOB.getEmailChargeName()!=null?finalDOB.getEmailChargeName():finalDOB.getUpdatedReportDOB().getChangeDesc())+",\n\n"+finalDOB.getEmailText()!=null?finalDOB.getEmailText():""+"\n\n\n"+masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():""+"\n\n"+masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():""+
                                     "\n"+masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():""+"\n\n"+"Phone  "+masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():""+"\n"+"Fax    "+masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():""+"\n"+"Mobile "+masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():""+"\n\nEmail "+masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"";  //@@Modified by Kameswari for the WPBN issue-61303
                          }
                          else
                          {
                               body  = "Dear "+(contactName!=null?contactName:"")+",\n\n"+finalDOB.getEmailText()!=null?finalDOB.getEmailText():""+"\n\n\n"+masterDOB.getCreatorDetails()!=null?masterDOB.getCreatorDetails():""+"\n\n"+masterDOB.getCompanyName()!=null?masterDOB.getCompanyName():""+
                               "\n"+masterDOB.getTerminalAddress()!=null?masterDOB.getTerminalAddress():""+"\n\n"+"Phone  "+masterDOB.getPhoneNo()!=null?masterDOB.getPhoneNo():""+"\n"+"Fax    "+masterDOB.getFaxNo()!=null?masterDOB.getFaxNo():""+"\n"+"Mobile "+masterDOB.getMobileNo()!=null?masterDOB.getMobileNo():""+"\n\nEmail "+masterDOB.getUserEmailId()!=null?masterDOB.getUserEmailId():"";  //@@Modified by Kameswari for the WPBN issue-61303
                               
                          }            
                    if("on".equalsIgnoreCase(request.getParameter("email")))
                    {          
                      try
                      {
                        sendMail(fromEmailId,(String)contactEmailList.get(i),subject,message,"Quote"+file_tsmp+".pdf",null);
                        sentEmailsList.add(contactEmailList.get(i));
                      }
                      catch(FoursoftException fs)
                      {
                        //Logger.error(FILE_NAME,"Error While Sending Mail.."+fs.getMessage());
                        logger.error(FILE_NAME+"Error While Sending Mail.."+fs.getMessage());
                        fs.printStackTrace();
                        unsentEmailsList.add(contactEmailList.get(i));
                      }
                    }
                    if("on".equalsIgnoreCase(request.getParameter("fax")))
                    {
                      if("SG".equalsIgnoreCase(countryCode))
                          to_FaxIds   = "fax#"+contactFaxList.get(i)+"@tcdhl.com";
                      else
                          to_FaxIds   =  "ifax#"+contactFaxList.get(i)+"@tcdhl.com";
                      try
                      {
                        sendMail(fromEmailId,to_FaxIds,subject,message,"Quote"+file_tsmp+".pdf",null);
                        sentFaxList.add(contactFaxList.get(i));
                      }
                      catch(FoursoftException fs)
                      {
                        //Logger.error(FILE_NAME,"Error While Sending Fax.."+fs.getMessage());
                        logger.error(FILE_NAME+"Error While Sending Fax.."+fs.getMessage());
                        fs.printStackTrace();
                        unsentFaxList.add(contactFaxList.get(i)!=null?contactFaxList.get(i):"No Fax Number Provided");
                      }
                    }
                  }
                //}
                returnList.add(sentEmailsList);
                returnList.add(unsentEmailsList);
                returnList.add(sentFaxList);
                returnList.add(unsentFaxList);
                
               // System.out.println("Before Sending Mail------------------------------>");

               // System.out.println("End of mail sending and PDF Generation --------------------------------->");


  }   
  catch(Exception e)
  {
    e.printStackTrace();
    //Logger.error(FILE_NAME,"Error while generating the PDF"+e.toString());
    logger.error(FILE_NAME+"Error while generating the PDF"+e.toString());
    //return 0;
    //throw new Exception("Error while generating PDF format");
  }
  return returnList;
}

//Added  for the Issue 234719
private void getUpdatedQuoteInfo(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginbean) throws ServletException
{
  MultiQuoteFinalDOB   finalDOB    =   null;
  String          quoteId     =   null;
  String          changeDesc  =   null;
  String          sellBuyFlag =   null;
  String          masterId    =   null;
  //HttpSession    session       =  null;
  String          buyRatesFlag=   null;
//  String          flag        =   null;
  String          quoteType       =   null;
  String          quoteOption     =   null;
  MultiQuoteMasterDOB  masterDOB         =   null;
  UpdatedQuotesReportDOB reportDOB  =   null;
  MultiQuoteFreightLegSellRates  legRateDetails    = null;
  MultiQuoteChargeInfo mqci  = null;
  
  QMSMultiQuoteSessionHome       home              = null;
  QMSMultiQuoteSession           remote            = null;
  
  HttpSession session   =   request.getSession();
  
  try
  {
    //lookUpBean  = new LookUpBean();
    home        = (QMSMultiQuoteSessionHome)LookUpBean.getEJBHome("QMSMultiQuoteSessionBean"); 
    remote      = home.create();
    
    quoteId     = request.getParameter("quoteId"); 
    sellBuyFlag = request.getParameter("sellBuyFlag");
    changeDesc  = request.getParameter("changeDesc");
    masterId    = request.getParameter("masterId");
    quoteType   = request.getParameter("quoteType");
    quoteOption = request.getParameter("quoteOption");
    reportDOB   = new UpdatedQuotesReportDOB();
   
    if(quoteId!=null && quoteId.trim().length()!=0)
      reportDOB.setUniqueId(Long.parseLong(quoteId));
    if(sellBuyFlag!=null && sellBuyFlag.trim().length()!=0)
      reportDOB.setSellBuyFlag(sellBuyFlag);
    if(changeDesc!=null && changeDesc.trim().length()!=0)
      reportDOB.setChangeDesc(changeDesc);
    if(masterId!=null && masterId.trim().length()!=0)
      //reportDOB.setQuoteId(Long.parseLong(masterId));   //@@ Commented by subrahmanyam for the enhancement #146971 on 2/12/08
      reportDOB.setQuoteId(masterId);   //@@ Added by subrahmanyam for the enhancement #146971 on 2/12/08
    
    Hashtable accessList  =  (Hashtable)session.getAttribute("accessList");
    
    //@@setting the buy rates permissions flag based on user role permissions.
    if(accessList.get("10605")!=null)
      buyRatesFlag  = "Y";
    else
      buyRatesFlag  = "N"; 
    

    finalDOB    = remote.getUpdatedQuoteInfo(Long.parseLong(quoteId),changeDesc,sellBuyFlag,buyRatesFlag,loginbean,quoteType);
    masterDOB   = finalDOB.getMasterDOB();
    masterDOB.setBuyRatesPermission(buyRatesFlag); 
   finalDOB.setMasterDOB(masterDOB);   
    finalDOB.setUpdatedReportDOB(reportDOB);
    
    request.setAttribute("fromWhere","reports/UpdatedQuotesReport.jsp?Operation=updatedQuotes&format=html&SortBy="+request.getParameter("sortedBy")+"&SortOrder="+request.getParameter("sortedOrder")+"&PageNo="+request.getParameter("pageNo"));
    masterDOB.setOperation("View/Print");//Added ob 07Feb2011
    if("Modify".equalsIgnoreCase(quoteOption)||"previousQuote".equalsIgnoreCase(quoteType))
    {
    	masterDOB.setOperation("Modify/Print");//Added ob 07Feb2011
        finalDOB.setMasterDOB(masterDOB);  
        finalDOB  = remote.getFreightSellRates(finalDOB);      
        masterDOB.setOperation("Modify");
        finalDOB.setMasterDOB(masterDOB);  
        request.setAttribute("finalDOB",finalDOB);
        finalDOB  = doSellGetHeaderAndCharges(request,response);
      session.setAttribute("updatedSendOptions",finalDOB.getFlagsDOB());
      session.setAttribute("finalDOB",finalDOB);
      doDispatcher(request,response,"qms/QMSMultiQuoteChargesSelect.jsp?Operation=Add&flag=true&count=0&update=update&updateValue=Modify");
    }
    else
    {
      if(finalDOB.getOriginChargesList()!=null)
      {
        int originChargesSize = 0;
        originChargesSize     = finalDOB.getOriginChargesList().size();
       //logger.info("originChargesSize::"+originChargesSize);
        int[] originSelectedIndices = new int[originChargesSize];
        
        for(int i=0;i<originChargesSize;i++)
        {
          originSelectedIndices[i]  = i;
        }
        finalDOB.setSelectedOriginChargesListIndices(originSelectedIndices);
      }
      if(finalDOB.getDestChargesList()!=null)
      {
        int destChargesSize = 0;
        destChargesSize     = finalDOB.getDestChargesList().size();
       // logger.info("destChargesSize::"+destChargesSize);
        int[] destSelectedIndices = new int[destChargesSize];
            
        for(int i=0;i<destChargesSize;i++)
        {
          destSelectedIndices[i]  = i;
        }
        finalDOB.setSelctedDestChargesListIndices(destSelectedIndices);
      }
      finalDOB.setMasterDOB(masterDOB);  
      finalDOB  = remote.getFreightSellRates(finalDOB);
      masterDOB.setOperation("View");//Added ob 07Feb2011
      finalDOB.setMasterDOB(masterDOB);  
      request.setAttribute("finalDOB",finalDOB);
      finalDOB  = doSellGetHeaderAndCharges(request,response);
      session.setAttribute("viewFinalDOB",finalDOB);
      doDispatcher(request,response,"qms/QMSMultiQuoteSummaryView.jsp?Operation=View&flag=true&update=Update");
    }
  }
  catch(Exception e)
  {
    e.printStackTrace();
    //Logger.error(FILE_NAME,"Error in getUpdatedQuoteInfo "+e);
    logger.error(FILE_NAME+"Error in getUpdatedQuoteInfo "+e);
    throw new ServletException(e);
  } 
  
  
}
private MultiQuoteFinalDOB doSellGetHeaderAndCharges(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
{
	MultiQuoteFinalDOB finalDOB = null;
	MultiQuoteChargeInfo mqci  = null;
	String                    operation         = null;
	String                    subOperation      = null;
	// QMSQuoteSessionHome       home              = null;
	// QMSQuoteSession           remote            = null;
	ArrayList                 freightRates      = null;
	ArrayList                 updatedFreightRates = null;
	ArrayList                 attachmentIdList    = null; 	
	MultiQuoteMasterDOB            masterDOB         = null;
	MultiQuoteTiedCustomerInfo     tiedCustomerInfo  = null;
	MultiQuoteFreightLegSellRates  legRateDetails    = null;
	MultiQuoteHeader               quoteHeader       = null;
	 String                    weightBreakType   = null;
	String                    legServiceLevel   = null;
	StringBuffer              sb                = new StringBuffer("");
	ArrayList containerList = new ArrayList();
	boolean[]                  isMarginDefined   = null;
	boolean selectedflag[]     = null;
	boolean                     isFinalMarginDefined = true;
	boolean                     isFinalSelectedFlag = true;
	boolean						tempflag     =false;
	String[] selInternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
	String[] selExternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
	String[] selTempInternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
	String selectedWeightBreak = null;//@@ Added by Mohan Gajjala for on 30102010
	String selectedConsoleType = null;//@@ Added by Mohan Gajjala for on 30102010
	int selectCntrCount = 0;//@@ Added by Mohan Gajjala for on 30102010
	int selIndex = 0;//@@ Added by Mohan Gajjala for on 30102010
	int sellRateSize		=	0;//Added by govind for the cr-219979
	ArrayList selectedFreightSellRateIndex = null;
	int ratecount =0;

	int                       freightRatesSize =0;
	HttpSession   session     = request.getSession();
	int count = 0;
	try
	{
	  operation   = request.getParameter("Operation");
	  subOperation  = request.getParameter("subOperation");
	  finalDOB  = (MultiQuoteFinalDOB)request.getAttribute("finalDOB");
	    /* if("View".equalsIgnoreCase(operation))
	    finalDOB  = (MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");
	  /else
	    finalDOB  = (MultiQuoteFinalDOB)session.getAttribute("finalDOB");*/
	 
	  masterDOB = finalDOB.getMasterDOB();
	  
	  tiedCustomerInfo  = finalDOB.getTiedCustomerInfo();
	   
	  freightRates  = finalDOB.getLegDetails();
	   
	  // // added by VLAKSHMI for issue 146968 on 5/12/2008
	String[]  tempContainerTypes=null;
	String[]	containerTypes			    =	null;
	  if(freightRates!=null)
	  {

	    freightRatesSize  = freightRates.size();
	    selectedflag = new boolean[freightRatesSize];
	    isMarginDefined = new boolean[freightRatesSize];
	    for(int i=0;i<freightRatesSize;i++)
	    {
	    	  tempflag = false;
	    	selectedFreightSellRateIndex	=	new ArrayList();
	      legRateDetails  = (MultiQuoteFreightLegSellRates)freightRates.get(i);
	      sellRateSize    = (legRateDetails.getRates()!=null)?legRateDetails.getRates().size():0; // Added by Gowtham.
	      for(int rt=0;rt<sellRateSize;rt++){
	      
	    	if(request.getParameter("hid"+i+rt)!=null && request.getParameter("hid"+i+rt).trim().length()!=0)
	     {
	         tempContainerTypes			=	request.getParameterValues("con"+i+Integer.parseInt(request.getParameter("hid"+i+rt)));
	      
	    if(tempContainerTypes!=null)
	    {

	          containerTypes			    =	new String[tempContainerTypes.length];
	          int tempContLen	=	tempContainerTypes.length;
	         for(int t=0;t<tempContLen;t++)
	         {
	           if(tempContainerTypes[t]!=null && tempContainerTypes[t].trim().length()>0 )
	           {
	           //logger.info("ContainerTypes"+tempContainerTypes[t].trim().length());
	          
	           containerTypes[t]=tempContainerTypes[t];
	           selectCntrCount=selectCntrCount+1;    //Added by Mohan Gajjala for on 30102010
	          }
	         }
	    }// end  for issue 146968 on 5/12/2008
	          }	  
	       	  
	    	  
	    	  
	     /* if(request.getParameterValues("chargeGroupIds"+i)!= null && request.getParameterValues("chargeGroupIds"+i).length >0){
	  		legRateDetails.setChargeGroupIds(request.getParameterValues("chargeGroupIds"+i));
	  	}	*/  
	        //Added by Mohan Gajjala for on 30102010 
	    	tempflag = true;
	    	  selIndex =rt;
	    	selectedFreightSellRateIndex.add(selIndex);
//	    	ratecount++;
	        legRateDetails.setContainerTypes(containerTypes);// // added by VLAKSHMI for issue 146968 on 5/12/2008
	         selectedWeightBreak = ((MultiQuoteFreightRSRCSRDOB)legRateDetails.getRates().get(selIndex)).getWeightBreakType();
	        selectedConsoleType = ((MultiQuoteFreightRSRCSRDOB)legRateDetails.getRates().get(selIndex)).getConsoleType();
	       
	        int contrCount = 0;
	        if(containerTypes!=null)
	        	contrCount = containerTypes.length;
	        //Modified by mohan for Issue no .223726 UTS 
	       /* if(request.getParameter("notes"+selIndex)!=null && request.getParameter("notes"+selIndex).trim().length()!=0)
	        {
	         	System.out.println("INT Weight Break Selected --->"+selectedWeightBreak);
	        	System.out.println("INT Console Type Selected --->"+selectedConsoleType);
	        	
	        	if(selectedWeightBreak!=null && "List".equalsIgnoreCase(selectedWeightBreak) && selectedConsoleType!=null && "FCL".equals(selectedConsoleType))
	        	{
	        		selTempInternalNotes = request.getParameterValues("notes"+selIndex);
	        		
	        		selInternalNotes = new String[selectCntrCount];
	        		for(int seledIndex=0;seledIndex<contrCount;seledIndex++)
	        		{
	        			if(containerTypes[seledIndex]!=null && !"".equals(containerTypes[seledIndex]))
	            			selInternalNotes[seledIndex] = selTempInternalNotes[seledIndex];
	        		}
	        	}else
	        		selInternalNotes = request.getParameterValues("notes"+selIndex);
	        }
	        
	        if(request.getParameter("extNotes"+selIndex)!=null && request.getParameter("extNotes"+selIndex).trim().length()!=0)
	        {
	        	System.out.println("EXT Weight Break Selected --->"+selectedWeightBreak);
	        	System.out.println("EXt Console Type Selected --->"+selectedConsoleType);
	         	
	        	if(selectedWeightBreak!=null && "List".equalsIgnoreCase(selectedWeightBreak) && selectedConsoleType!=null && "FCL".equals(selectedConsoleType))
	        	{
	        		selTempInternalNotes = request.getParameterValues("extNotes"+selIndex);
	        		//int selIndexCount = containerTypes.length;
	        		selExternalNotes = new String[selectCntrCount];
	        		for(int seledIndex=0;seledIndex<contrCount;seledIndex++)
	        		{
	        			if(containerTypes[seledIndex]!=null && !"".equals(containerTypes[seledIndex]))
	        				selExternalNotes[seledIndex] = selTempInternalNotes[seledIndex];
	        		}
	        	}else
	            	selExternalNotes= request.getParameterValues("extNotes"+selIndex);  //End by mohan for Issue no .223726 UTS 
	        }*/ //End by Mohan Gajjala for on 30102010
	      
	     
	    }
	      if(!tempflag)
	    	  selectedflag[i] = true;  
	      int indexSize	=	selectedFreightSellRateIndex.size();
	      Integer[] indexArray	= new Integer[indexSize];
	      for(int j=0;j<indexSize;j++)
	    	  indexArray[j] = (Integer)selectedFreightSellRateIndex.get(j);
	      legRateDetails.setSelectedFreightSellRateIndex(indexArray);
	      freightRates.remove(i);
	      freightRates.add(i,legRateDetails);
	    }
	    finalDOB.setLegDetails(freightRates);
	    
	  }
	 
	    long start = System.currentTimeMillis();
	  
	    finalDOB          =   getMarginLimit(finalDOB);
	   
	    finalDOB.setInternalNotes(selInternalNotes);
	    finalDOB.setExternalNotes(selExternalNotes);       
	    request.setAttribute("finalDOB",finalDOB);
	    freightRates      =   finalDOB.getLegDetails();
	    freightRatesSize  =   freightRates.size();
	    
	    for(int i=0;i<freightRatesSize;i++)
	    {
	      legRateDetails  =   (MultiQuoteFreightLegSellRates)freightRates.get(i);
	     
	      if(selectedflag[i]){
	    	  sb.append(" No Rates Are selected for the Lane "+legRateDetails.getOrigin()).append("-").append(legRateDetails.getDestination()+"\n");
	    	  isFinalSelectedFlag =false;
	      }
	      if(!selectedflag[i] && !legRateDetails.isMarginFlag() && !legRateDetails.isTiedCustInfoFlag())
	      {
	          sb.append("&nbsp;The Margin Limit is Not Defined For the Selected Shipment Mode & Service Level for the Leg(s) ").append(legRateDetails.getOrigin()).append("-").append(legRateDetails.getDestination());
	          isMarginDefined[i] = false;
	          isFinalMarginDefined = false;
	      }
	    }
	    finalDOB = doGetHeaderAndCharges(request,response);
	    legRateDetails = (MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(0);
	    if(legRateDetails.getFreightChargesList().size()!=0){ // Added by Gowtham .
	    mqci = (MultiQuoteChargeInfo)legRateDetails.getFreightChargesList().get(0);	   
	   String[] temp = mqci.getChecked_Flag().split(",");
	   int lenChecked = temp.length;
	   String temp1= "";
	   for(int i=0;i<lenChecked;i++)
	   {
		   if("Y".equalsIgnoreCase(temp[i]))
			   temp1=temp1+i+",";
	   }
	   finalDOB.setMultiQuoteSelectedBreaks(temp1);
	}
	}
	catch(Exception ex)
	{
  session.removeAttribute("finalDOB");
  session.removeAttribute("PreFlagsDOB");
  ex.printStackTrace();		
  logger.error(FILE_NAME+ " [error in doSellRates()] -> "+ex.toString());
  setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSMultiQuoteController?Operation=Add");
  doDispatcher(request, response, "ESupplyErrorPage.jsp");
	}
	
	return finalDOB;
}
//Ended for the Issue 234719

// Added by Gowtham on 23-Feb-2011 to check string is integer or not
 private boolean isInteger( String input )  
 {  
    try  
    {  
       Integer.parseInt( input );  
       return true;  
    }  
    catch( NumberFormatException nfe )  
    {  
       return false;  
    }  
 }
 private boolean isDouble( String input )  
 {  
    try  
    {  
       Double.parseDouble( input );  
       return true;  
    }  
    catch( NumberFormatException nfe )  
    {  
       return false;  
    }  
 }

 /*private   String toTitleCase(String str){
		StringBuffer sb = new StringBuffer();     
		str = str.toLowerCase();
		str =str.replace('(','~');
		str =str.replace(')','#');
		StringTokenizer strTitleCase = new StringTokenizer(str);
		while(strTitleCase.hasMoreTokens()){
			String s = strTitleCase.nextToken();
			
			sb.append(s.replaceFirst(s.substring(0,1),s.substring(0,1).toUpperCase()) + " ");
		}
		str= sb.toString();
		str =str.replace('~','(');
		str =str.replace('#',')');
		return str;
	}*/
 private   String toTitleCase(String str){
	   StringBuffer sb = new StringBuffer();
	   StringTokenizer strTitleCase=null;
	   str = str.toLowerCase();
	   str =str.replace('(','~');
	   str =str.replace(')','#');
	   
	   String[] names = str.split(",");
	   
	   for(String name:names ){  
	    strTitleCase = new StringTokenizer(name);
	    while(strTitleCase.hasMoreTokens()){
	     String s = strTitleCase.nextToken();
	     sb.append(s.replaceFirst(s.substring(0,1),s.substring(0,1).toUpperCase()) + " ");
	    }
	    sb.append(",");
	   }
	   
	   str= sb.substring(0, sb.length()-1).toString();
	   
	   str =str.replace('~','(');
	   str =str.replace('#',')');
	   return str;
	  }
	 
	 

}

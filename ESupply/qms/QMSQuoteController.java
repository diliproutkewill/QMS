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
 

 * File					: QMSQuoteController.java
 * @author			: Anil Kumar.S
 * @date				: 4th Aug 2005
 *


 *	This Controller is used to control the flow in the quote module
 */
 
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.qms.operations.costing.dob.CostingChargeDetailsDOB;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingLegDetailsDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.costing.dob.CostingRateInfoDOB;
import com.qms.operations.quote.dao.QMSQuoteDAO;
import com.qms.operations.quote.dob.QuoteAttachmentDOB;
import com.qms.operations.quote.dob.QuoteCartageRates;
import com.qms.operations.quote.dob.QuoteChargeInfo;
import com.qms.operations.quote.dob.QuoteCharges;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteFlagsDOB;
import com.qms.operations.quote.dob.QuoteFreightLegSellRates;
import com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB;
import com.qms.operations.quote.dob.QuoteHeader;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.operations.quote.dob.QuoteTiedCustomerInfo;
import com.qms.operations.quote.ejb.sls.QMSQuoteSession;
import com.qms.operations.quote.ejb.sls.QMSQuoteSessionHome;
import com.qms.reports.java.UpdatedQuotesReportDOB;

public class QMSQuoteController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static final String FILE_NAME = "QMSQuoteController.java";
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

      if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation)  || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation) || "Grouping".equalsIgnoreCase(operation))&& subOperation == null)
      {//this is the initial request

        doForwardToInitialJSP(request,response);
      }
      else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) && "MASTER".equalsIgnoreCase(subOperation))
      {//from the master page i.e the first page
    
        doMasterInfo(request,response);
      }
      else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) && "SELLRATES".equalsIgnoreCase(subOperation))
      {//from the sell rates page
        doSellRates(request,response);
      }
      else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) && "SELLCHARGES".equalsIgnoreCase(subOperation))
      {//from the sell Charges page
      
        doSellCharges(request,response);
      }
      else if(("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation))&& "SUMMARY".equalsIgnoreCase(subOperation))
      {
            
    	  doSummary(request,response);
      }
      else if(("Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation))&& "EnterId".equalsIgnoreCase(subOperation))
      {
         doModifyProcess(request,response);
      }
      else if("View".equalsIgnoreCase(operation) && "Report".equalsIgnoreCase(subOperation))
      {
        doGenerateQuoteView(request,response);
      }
      else if("Grouping".equalsIgnoreCase(operation) && "charges".equalsIgnoreCase(subOperation))
      {
        chargeGroupsView(request,response);
      } 
      else if("Grouping".equalsIgnoreCase(operation) && "QuoteGroupInform".equalsIgnoreCase(subOperation))
      {
        quoteGroupsDtl(request,response);
      }
      else if("Grouping".equalsIgnoreCase(operation) && "sendPdf".equalsIgnoreCase(subOperation))
      {
        quoteGroupsSendOptions(request,response);
      }
      else if("Update".equalsIgnoreCase(operation) && subOperation==null)
      {
        getUpdatedQuoteInfo(request,response,loginbean);
      }
    
      
    }
    catch(Exception e)
    {
      session.removeAttribute("finalDOB");
      session.removeAttribute("PreFlagsDOB");
      session.removeAttribute("updatedSendOptions");
      e.printStackTrace();
      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSQuoteController?Operation=Add");
      doDispatcher(request, response, "ESupplyErrorPage.jsp");
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
    QuoteMasterDOB            masterDOB         = null;
    ESupplyGlobalParameters   loginbean	        = null;
    StringBuffer              errors            = new StringBuffer();
    QuoteFinalDOB             finalDOB          = null;
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
    QuoteFlagsDOB             flagsDOB          = null;
    QuoteFreightLegSellRates  legDOB            = null;
    QuoteFreightLegSellRates  tempDOB           = null;
    ArrayList                 freightRates      = null;
    ArrayList                 frtTiedCustInfo   = null;
    ArrayList                 attachmentIdList  = null;
    int                       freightRatesSize  = 0;
    StringBuffer              sb                = new StringBuffer("");
    boolean                   isMarginDefined   = true;
   String                     quoteId 			= null; 
    String                    status            = "";
    String                    errStatus         = "";
    String                    msgStatus         = "";
    ArrayList                 keyList           = new ArrayList();
    int                       keySize           = 0;
    boolean                   isMultiModal      = false;
    HttpSession  session       =  null;
    boolean                   finalForwardFlag  = true;//@@If true, then forward to the third screen.
    int count = 0;    
    ESupplyDateUtility        eSupplyDateUtility  = new ESupplyDateUtility();
    try
    {
      operation   = request.getParameter("Operation");
      session     = request.getSession();
      loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      //lookUpBean  = new LookUpBean();
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      
     
      finalDOB  = setQuoteMasterDOB(request,response,loginbean);//set the master dob
      
      masterDOB = finalDOB.getMasterDOB(); 
       if(!"View".equalsIgnoreCase(operation))
          errors    = remote.validateQuoteMaster(finalDOB);//validate the data set in the master dob
      
      if(errors.length()==0)//if no errors are there
      {
        ArrayList legDetails = finalDOB.getLegDetails();
        int legSize = legDetails.size();
        boolean isSpotRates  = true;
        
        for(int i=0;i<legSize;i++)
        {
          legDOB  = (QuoteFreightLegSellRates)legDetails.get(i);
          if(!legDOB.isSpotRatesFlag())
              isSpotRates = false;
        }
        finalDOB.setSelectedOriginChargesListIndices(null);
        finalDOB.setSelctedDestChargesListIndices(null); 
        if(masterDOB.getAttentionToDetails()!=null)
        {
          remote.updateAttentionToContacts(masterDOB.getAttentionToDetails());//added by phani sekhar for wpbn 167678      
        }
        if("Save & Exit".equalsIgnoreCase(request.getParameter("submit"))|| "Save & Exit".equalsIgnoreCase(request.getParameter("submitt1")))
        {
          finalDOB.setSelectedOriginChargesListIndices(null);
          finalDOB.setSelctedDestChargesListIndices(null);
          
          for(int i=0;i<legSize;i++)
          {
            legDOB  = (QuoteFreightLegSellRates)legDetails.get(i);
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
            flagsDOB = new QuoteFlagsDOB();
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
           flagsDOB = (QuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
           if(flagsDOB!=null)
              finalDOB.setFlagsDOB(flagsDOB);
           else
              finalDOB.setFlagsDOB(new QuoteFlagsDOB());
            
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
        }
        else if("Next >>".equalsIgnoreCase(request.getParameter("submit")) ||"Next >>".equalsIgnoreCase(request.getParameter("submitt1")) )
        {
            long start = System.currentTimeMillis();
            if(isSpotRates)//if spot rates are given, forward directly 
            {                               //to the charges select page
              finalDOB          =   getMarginLimit(finalDOB);
              finalDOB.setSpotRatesFlag("Y");
            
              freightRates      =   finalDOB.getLegDetails();
              freightRatesSize  =   freightRates.size();
               
              for(int i=0;i<freightRatesSize;i++)
              {
                legDOB          =  (QuoteFreightLegSellRates)freightRates.get(i);
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
                tempDOB = (QuoteFreightLegSellRates)freightRates.get(i);
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
            }
           
            if(isMarginDefined)
            {
              freightRates      =   finalDOB.getLegDetails();
             
              freightRatesSize  =   freightRates.size();
              for(int i=0;i<freightRatesSize;i++)
              {
                legDOB          =  (QuoteFreightLegSellRates)freightRates.get(i);
                keyList.add(""+legDOB.getShipmentMode());
                              
                if(!legDOB.isSpotRatesFlag() && !legDOB.isTiedCustInfoFlag())
                  legDOB.setForwardFlag(false);
                else
                  legDOB.setForwardFlag(true);
              }
              keySize   =  keyList.size();
              for(int i=0;i<freightRatesSize;i++)
              {
                legDOB          =  (QuoteFreightLegSellRates)freightRates.get(i);
               
                if(!legDOB.isForwardFlag())
                  finalForwardFlag  = false;
                tempDOB = (QuoteFreightLegSellRates)freightRates.get(i);
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
               
              if(!isSpotRates && finalForwardFlag)
                finalDOB          =   getMarginLimit(finalDOB);
             
              if(finalForwardFlag)
              {
                request.setAttribute("finalDOB",finalDOB);
                finalDOB  = doGetHeaderAndCharges(request,response);
                finalDOB.setMultiModalQuote(isMultiModal);
                //session.setAttribute("finalDOB",finalDOB);
                if("View".equalsIgnoreCase(operation))
                {
                  session.setAttribute("viewFinalDOB",finalDOB);
                  request.setAttribute("fromWhere","qms/QMSQuoteMasterView.jsp");
                  doDispatcher(request, response, "qms/QMSQuoteChargesView.jsp");
                }
                else
                { 
                	//@@Added for backbutton in modify
                	//if("Modify".equalsIgnoreCase(operation)){
                   // Timestamp custDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("createdDate"));
                   // finalDOB.getMasterDOB().setCreatedDate(custDate);
                	//}
                  session.setAttribute("finalDOB",finalDOB);
                  request.setAttribute("fromWhere","qms/QMSQuoteMaster.jsp");
                  doDispatcher(request, response, "qms/QMSQuoteChargesSelect.jsp?count="+count);
                }
              }
              else
              {
                finalDOB  = remote.getFreightSellRates(finalDOB);
                finalDOB.setSpotRatesFlag("N");
                finalDOB.setMultiModalQuote(isMultiModal);
           
                //session.setAttribute("finalDOB",finalDOB);
                request.setAttribute("finalDOB",finalDOB);
                if("View".equalsIgnoreCase(operation))
                {
                  session.setAttribute("viewFinalDOB",finalDOB);
                  request.setAttribute("fromWhere","qms/QMSQuoteFreightSellRatesView.jsp");
                  doDispatcher(request, response, "qms/QMSQuoteFreightSellRatesView.jsp");
                }
                else
                {	
                	//if("Modify".equalsIgnoreCase(operation)){
                   // Timestamp custDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("createdDate"));
                   // finalDOB.getMasterDOB().setCreatedDate(custDate);
                	//}
                  session.setAttribute("finalDOB",finalDOB);
                  request.setAttribute("fromWhere","qms/QMSQuoteFreightSellRates.jsp");
                  logger.info("Server Response Time for 2nd screen in milli seconds :    "+ ((System.currentTimeMillis()) - start) + "    User Id :: "+ finalDOB.getMasterDOB().getUserId()+ " Customer Id :: "+finalDOB.getMasterDOB().getCustomerId());
                  doDispatcher(request, response, "qms/QMSQuoteFreightSellRates.jsp");
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
        doDispatcher(request, response, "qms/QMSQuoteMaster.jsp");
      }
      
    }
    catch(Exception ex)
		{
      session.removeAttribute("finalDOB");
      session.removeAttribute("PreFlagsDOB"); 
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [error in doMasterInfo()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [error in doMasterInfo()] -> "+ex.toString());
      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSQuoteController?Operation=Add");
      doDispatcher(request, response, "ESupplyErrorPage.jsp");
		}
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
    String                    operation         = null;
    String                    subOperation      = null;
   // QMSQuoteSessionHome       home              = null;
   // QMSQuoteSession           remote            = null;
    ArrayList                 freightRates      = null;
    ArrayList                 updatedFreightRates = null;
    ArrayList                 attachmentIdList    = null; 
    QuoteFinalDOB             finalDOB          = null;
    QuoteMasterDOB            masterDOB         = null;
    QuoteTiedCustomerInfo     tiedCustomerInfo  = null;
    QuoteFreightLegSellRates  legRateDetails    = null;
    QuoteHeader               quoteHeader       = null;
     String                    weightBreakType   = null;
    String                    legServiceLevel   = null;
    StringBuffer              sb                = new StringBuffer("");
    ArrayList containerList = new ArrayList();
    boolean                   isMarginDefined   = true;
    String[] selInternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
    String[] selExternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
    String[] selTempInternalNotes = null;//@@ Added by Mohan Gajjala for on 30102010
    String selectedWeightBreak = null;//@@ Added by Mohan Gajjala for on 30102010
    String selectedConsoleType = null;//@@ Added by Mohan Gajjala for on 30102010
    int selectCntrCount = 0;//@@ Added by Mohan Gajjala for on 30102010
    int selIndex = 0;//@@ Added by Mohan Gajjala for on 30102010
    int                       freightRatesSize;
    HttpSession   session     = request.getSession();
    int count = 0;
    try
    {
      operation   = request.getParameter("Operation");
      subOperation  = request.getParameter("subOperation");
   
         if("View".equalsIgnoreCase(operation))
        finalDOB  = (QuoteFinalDOB)session.getAttribute("viewFinalDOB");
      else
        finalDOB  = (QuoteFinalDOB)session.getAttribute("finalDOB");
     
      masterDOB = finalDOB.getMasterDOB();
     
      tiedCustomerInfo  = finalDOB.getTiedCustomerInfo();
       
      freightRates  = finalDOB.getLegDetails();
     
      // // added by VLAKSHMI for issue 146968 on 5/12/2008
   String[]  tempContainerTypes=null;
   String[]	containerTypes			    =	null;
      if(freightRates!=null)
      {

        freightRatesSize  = freightRates.size();
        for(int i=0;i<freightRatesSize;i++)
        {
             if(request.getParameter("hid"+i)!=null && request.getParameter("hid"+i).trim().length()!=0)
             {
               tempContainerTypes			=	request.getParameterValues("con"+i+Integer.parseInt(request.getParameter("hid"+i)));
         
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
          legRateDetails  = (QuoteFreightLegSellRates)freightRates.get(i);
          if(request.getParameter("hid"+i)!=null && request.getParameter("hid"+i).trim().length()!=0)
          {
            //Added by Mohan Gajjala for on 30102010
        	selIndex =Integer.parseInt(request.getParameter("hid"+i));
            legRateDetails.setSelectedFreightSellRateIndex(selIndex);
            legRateDetails.setContainerTypes(containerTypes);// // added by VLAKSHMI for issue 146968 on 5/12/2008
             selectedWeightBreak = ((QuoteFreightRSRCSRDOB)legRateDetails.getRates().get(selIndex)).getWeightBreakType();
            selectedConsoleType = ((QuoteFreightRSRCSRDOB)legRateDetails.getRates().get(selIndex)).getConsoleType();
           
            int contrCount = 0;
            if(containerTypes!=null)
            	contrCount = containerTypes.length;
            //Modified by mohan for Issue no .223726 UTS 
            try{
            if(request.getParameter("notes"+selIndex)!=null && request.getParameter("notes"+selIndex).trim().length()!=0)
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
            		selInternalNotes = request.getParameterValues("notes"+selIndex);
            }
            
            if(request.getParameter("extNotes"+selIndex)!=null && request.getParameter("extNotes"+selIndex).trim().length()!=0)
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
	            	selExternalNotes= request.getParameterValues("extNotes"+selIndex);  //End by mohan for Issue no .223726 UTS 
            }//End by Mohan Gajjala for on 30102010
            }
            catch(Exception e){
            	logger.info(e.getMessage());
            }
          }
          freightRates.remove(i);
          freightRates.add(i,legRateDetails);
          
          
        }
        finalDOB.setLegDetails(freightRates);
      
        
      }
      if("Next >>".equalsIgnoreCase(request.getParameter("submit")))
      {
        long start = System.currentTimeMillis();
        finalDOB.setSelectedOriginChargesListIndices(null);
        finalDOB.setSelctedDestChargesListIndices(null);
        finalDOB          =   getMarginLimit(finalDOB);
        if(!"copy".equalsIgnoreCase(masterDOB.getOperation())){
        // Added by Mohan Gajjala for on 30102010
        finalDOB.setInternalNotes(selInternalNotes);
        finalDOB.setExternalNotes(selExternalNotes);       
        }
        request.setAttribute("finalDOB",finalDOB);
        freightRates      =   finalDOB.getLegDetails();
        freightRatesSize  =   freightRates.size();
        
        for(int i=0;i<freightRatesSize;i++)
        {
          legRateDetails  =   (QuoteFreightLegSellRates)freightRates.get(i);
          
          if(!legRateDetails.isMarginFlag() && !legRateDetails.isTiedCustInfoFlag())
          {
              sb.append("&nbsp;").append(legRateDetails.getOrigin()).append("-").append(legRateDetails.getDestination());
              isMarginDefined = false;
          }
        }
        if(isMarginDefined || "View".equalsIgnoreCase(operation))
        {
          finalDOB  = doGetHeaderAndCharges(request,response);
      
          //session.setAttribute("finalDOB",finalDOB);
          if("View".equalsIgnoreCase(operation))
          {
            session.setAttribute("viewFinalDOB",finalDOB);
            request.setAttribute("fromWhere","qms/QMSQuoteFreightSellRatesView.jsp");
            doDispatcher(request, response, "qms/QMSQuoteChargesView.jsp");
          }
          else
          {
            session.setAttribute("finalDOB",finalDOB);
            request.setAttribute("fromWhere","qms/QMSQuoteFreightSellRates.jsp");
      
            //doDispatcher(request, response, "qms/QMSQuoteChargesSelect.jsp?count="+count);
            logger.info("Server Response Time for 3rd screen in milli seconds :      "+ ((System.currentTimeMillis()) - start) + "     User Id :: "+ finalDOB.getMasterDOB().getUserId()+ " Customer Id :: "+finalDOB.getMasterDOB().getCustomerId());
             doDispatcher(request, response, "qms/QMSQuoteChargesSelect.jsp?count="+count);
          }
        }
        else
        {
          request.setAttribute("errors","The Margin Limit is Not Defined For the Selected Shipment Mode & Service Level for the Leg(s): "+sb.toString());
          session.setAttribute("finalDOB",finalDOB);
          doDispatcher(request, response, "qms/QMSQuoteFreightSellRates.jsp");
        }
      }
      else if("<< Back".equalsIgnoreCase(request.getParameter("submit")))
      {
        /*if(tiedCustomerInfo!=null)//the previous page is tied customer info page
        {
          finalDOB      =   (QuoteFinalDOB)session.getAttribute("finalDOB");
          request.setAttribute("finalDOB",finalDOB);
          session.setAttribute("finalDOB",finalDOB);
          doDispatcher(request, response, "qms/QMSQuoteTiedCustomerInfo.jsp");
        }
        else//the previous page is master page
        {*/
          if("View".equalsIgnoreCase(operation))
              finalDOB      =   (QuoteFinalDOB)session.getAttribute("viewFinalDOB");
          else
              finalDOB      =   (QuoteFinalDOB)session.getAttribute("finalDOB");
              
          request.setAttribute("finalDOB",finalDOB);
      
          //session.setAttribute("finalDOB",finalDOB);
          if("View".equalsIgnoreCase(operation))
          {
            session.setAttribute("viewFinalDOB",finalDOB);
            doDispatcher(request, response, "qms/QMSQuoteMasterView.jsp");
          }
          else
          {
            session.setAttribute("finalDOB",finalDOB);
                 
            doDispatcher(request, response, "qms/QMSQuoteMaster.jsp");
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
      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSQuoteController?Operation=Add");
      doDispatcher(request, response, "ESupplyErrorPage.jsp");
		}
    
  }
  
  /**
	 * This method helps in processing the request from the sell Charges screen
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doMasterInfo request
	 * @exception ServletException if the request for the doMasterInfo could not be handled.
	 */
  public void doSellCharges(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String                  operation          = null;
    String                  marginTestFlag     = null;
    HttpSession             session            = null;
    ESupplyGlobalParameters loginbean	         = null;//@@added for the WPBN issue-61289
    QuoteFinalDOB           finalDOB            = null;
    QuoteMasterDOB          masterDOB           = null;
    QuoteFreightLegSellRates legDOB             = null;
    QuoteFlagsDOB           flagsDOB            = null;
    QuoteFlagsDOB           preFlagsDOB         = null;
    QuoteFlagsDOB           updatedFlagsDOB     = null;
    QMSQuoteSessionHome       home              = null;//@@added for the WPBN issue-61289
    QMSQuoteSession           remote            = null;//@@added for the WPBN issue-61289
    ArrayList               legDetails          = null;
    ArrayList               attachmentIdList   = null;
    ArrayList               attachmentIdLOVList = null;
    String[]                originChargeIndices = null;
    String[]                originChargeIndices1 = null;//added for duplicate charges
    String[]                frtChargeIndices    = null;
    String[]                destChargeIndices   = null;
    String[]                destChargeIndices1   = null;//added for duplicate charges
    String[]                surChargeIndices     = null;
    int[]                   originIndices       = null;
    int[]                   frtIndices          = null;
    int[]                   destIndices         = null;
    QuoteCharges           chargesDOB           = null;
    ArrayList               freightCharges      = null;
    //ADDED BY KAM FOR DUPLICATE CHARGES
    ArrayList             originChargesTemp     = new ArrayList();
    ArrayList             destChargesTemp     = new ArrayList();
    //ENDED BY KAM FOR DUPLICATE CHARGES
    //Added by Rakesh
    String                  quoteId             =null;
    
    int                     noOfLegs            =  0;
    
    boolean                 isCheating         = false;
    
    StringBuffer            sb                 = new StringBuffer("");
    String                  forwardPage        = "";
     String                  update        =null;
    int count = 1;
     int mTCount = 0;
     //added for duplicate charges
     int oCcount =0;
     int dCcount =0;
     //ended for duplicate charges
     
    Integer MarginTestCount=null;//Added by subrahmanyam for the Enhancement 154381 on 13/02/09
    try
    {
      operation   = request.getParameter("Operation");
      update   = request.getParameter("update");
      session     = request.getSession();
     //@@Added by kameswari for the WPBN issue-61289
      loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      //lookUpBean  = new LookUpBean();
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      //@@added for the WPBN issue-61289
      if("View".equalsIgnoreCase(operation))
          finalDOB      =   (QuoteFinalDOB)session.getAttribute("viewFinalDOB");
      else
          finalDOB      =   (QuoteFinalDOB)session.getAttribute("finalDOB");
     // flagsDOB    = finalDOB.getFlagsDOB();
      
      //Added by Rakesh 
      
      
      /*if("Add".equalsIgnoreCase(operation)||"Modify".equalsIgnoreCase(operation)||"Copy".equalsIgnoreCase(operation)){
    	  ArrayList ratelist=finalDOB.getPickUpCartageRatesList(); 
    	  ArrayList deliveryratelist=finalDOB.getDeliveryCartageRatesList();
    	  QuoteCartageRates rates;
    	  QuoteCartageRates charge;
    	  HashMap hs;
    	  HashMap hs1;
    	  String[] sellrates;
    	  String[] breakpoints;
    	  if(ratelist!=null&&ratelist.size()>0){
    	  for(int i=0;i<ratelist.size();i++){
    		  rates=(QuoteCartageRates)ratelist.get(i);
    		  hs=rates.getRates();
    		  sellrates=request.getParameterValues("originSellRate"+i);
    		  breakpoints=request.getParameterValues("bp"+i);
    		  if(sellrates!=null && breakpoints!=null)
    		  for(int j=0;j<sellrates.length;j++)
    		  hs.put(breakpoints[j],sellrates[j]);
    	  }
    	  }
    	  if(deliveryratelist!=null&&deliveryratelist.size()>0){
    		  int temp=0,temp1=0;
    	  for(int i=0;i<deliveryratelist.size();i++,temp1++){
    		  charge=(QuoteCartageRates)deliveryratelist.get(i);
    		  hs1=charge.getRates();
    		  System.out.println("===before==="+hs1);
    		  String[] tempsell;
    		  if(i==0){
    		  do
    		  {
    			  tempsell=request.getParameterValues("destSellRatei"+temp);  
    		  temp1=temp;
    		  temp++;
    		  }while(tempsell==null);
    		  }
    		  sellrates=request.getParameterValues("destSellRate"+temp1);
    		  breakpoints=request.getParameterValues("dbpi"+temp1);
    		  if(sellrates!=null && breakpoints!=null)
    		  for(int j=0;j<sellrates.length;j++){
    		  if(breakpoints!=null&&breakpoints[j]!=null&&hs1!=null)
    		  hs1.put(breakpoints[j],sellrates[j]);
    		  }
    		  System.out.println("===after==="+hs1);
    	  }
    	  }
    		  if(deliveryratelist!=null&&deliveryratelist.size()>0){
		  int temp=0,temp1=0;
	  for(int i=0;i<deliveryratelist.size();i++,temp1++){
		  charge=(QuoteCartageRates)deliveryratelist.get(i);
		  hs1=charge.getRates();
		  System.out.println("===before==="+hs1);
		  String[] tempsell;
		  if(i==0){
		  do
		  {
			  tempsell=request.getParameterValues("destSellRatei"+temp);  
		  temp1=temp;
		  temp++;
		  }while(tempsell==null);
		  }
		  sellrates=request.getParameterValues("destSellRate"+temp1);
		  breakpoints=request.getParameterValues("dbpi"+temp1);
		  if(sellrates!=null && breakpoints!=null)
		  for(int j=0;j<sellrates.length;j++){
		  if(breakpoints!=null&&breakpoints[j]!=null&&hs1!=null)
		  hs1.put(breakpoints[j],sellrates[j]);
		  }
		  System.out.println("===after==="+hs1);
	  }
	  }
      }*/
      if("Add".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
      {
          ArrayList ratelist = finalDOB.getPickUpCartageRatesList();
          ArrayList deliveryratelist = finalDOB.getDeliveryCartageRatesList();
          if(ratelist != null && ratelist.size() > 0)
          {
              for(int i = 0; i < ratelist.size(); i++)
              {
                  QuoteCartageRates rates = (QuoteCartageRates)ratelist.get(i);
                  HashMap hs = rates.getRates();
                  String sellrates[] = request.getParameterValues((new StringBuilder()).append("originSellRate").append(i).toString());
                  String breakpoints[] = request.getParameterValues((new StringBuilder()).append("bp").append(i).toString());
                  if(sellrates == null || breakpoints == null)
                  {
                      continue;
                  }
                  for(int j = 0; j < sellrates.length; j++)
                  {
                      hs.put(breakpoints[j], sellrates[j]);
                  }

              }

          }
      }
      if("Save & Exit".equalsIgnoreCase(request.getParameter("submit")))
      {
        
        	if("Add".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))	
        {
          flagsDOB = new QuoteFlagsDOB();
          flagsDOB.setCompleteFlag("I");
          flagsDOB.setInternalExternalFlag("I");
          flagsDOB.setSentFlag("U");
          flagsDOB.setActiveFlag("A");
          flagsDOB.setEscalationFlag("N");
          flagsDOB.setQuoteStatusFlag("GEN");        
          masterDOB=finalDOB.getMasterDOB();
          finalDOB.setFlagsDOB(flagsDOB);
          masterDOB.setVersionNo(1);//@@The version for this quote will be 1
          finalDOB.setMasterDOB(masterDOB);   
          finalDOB.setUpdate(update);

          
          
          if(finalDOB!=null)
            legDetails  = finalDOB.getLegDetails();
          else
            legDetails  = new ArrayList();
          
          noOfLegs    = legDetails.size();
          
          originChargeIndices1 = request.getParameterValues("originChargeIndices");//added for duplicate charges
          //Added by Mohan Gajjala for on 30102010
          request.getParameterValues("surChkBox");
          if(originChargeIndices1!=null)
          {
          	int origCharInd1Len	=	originChargeIndices1.length;
              for(int oc=0;oc<origCharInd1Len;oc++)
              {
                if(originChargeIndices1[oc].trim().length()>0)
                {
                originChargesTemp.add(originChargeIndices1[oc].trim());
               
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
          
          destChargeIndices1 = request.getParameterValues("destChargeIndices");
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
          for(int i=0;i<noOfLegs;i++)
          {
            frtChargeIndices  = request.getParameterValues("frtChargeIndices"+i);
             surChargeIndices  = request.getParameterValues("surChargeIndices"+i);
             if(frtChargeIndices!=null)
            {
              
                frtIndices   = new int[frtChargeIndices.length];
                logger.info("doSellCharges Selecetd frtIndices.length"+frtIndices.length);//newly added                  
                int frtChargIndLen	=	frtChargeIndices.length;
                for(int j=0;j<frtChargIndLen;j++)
                {
                  if(frtChargeIndices[j].trim().length()!=0)
                    frtIndices[j] =  Integer.parseInt(frtChargeIndices[j]);
                    logger.info("doSellCharges Selected frtChargeIndices : "+ frtIndices[j]); //newly added
                  
                }
                
             
            }
          
        
            legDOB  =   (QuoteFreightLegSellRates)legDetails.get(i);
			      freightCharges = legDOB.getFreightChargesList();
           chargesDOB			= (QuoteCharges)freightCharges.get(0);
           chargesDOB.setFrequencyChecked(request.getParameter("frequencyCheck"+i));
           chargesDOB.setTransitTimeChecked(request.getParameter("transitTimeCheck"+i));
           chargesDOB.setCarrierChecked(request.getParameter("carrierCheck"+i));
           chargesDOB.setRateValidityChecked(request.getParameter("rateValidityCheck"+i));
           freightCharges.remove(0);
           freightCharges.add(0,chargesDOB);
           legDOB.setFreightChargesList(freightCharges);
            legDOB.setSelectedFreightChargesListIndices(frtIndices);
            legDetails.remove(i);
            legDetails.add(i,legDOB);
          }
          
          finalDOB.setLegDetails(legDetails);
          
          finalDOB.setInternalNotes(removeEnterForNotes(request.getParameterValues("internalNotes")));
          finalDOB.setExternalNotes(removeEnterForNotes(request.getParameterValues("externalNotes")));
      
          finalDOB.setUpdate(update);
          preFlagsDOB      = (QuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
          updatedFlagsDOB  = (QuoteFlagsDOB)session.getAttribute("updatedSendOptions");
          session.removeAttribute("updatedSendOptions");
          finalDOB.setSelectedOriginChargesListIndices(originIndices);
          finalDOB.setSelctedDestChargesListIndices(destIndices);
          session.setAttribute("finalDOB",finalDOB);
          request.setAttribute("attachmentIdLOVList",attachmentIdLOVList);


          quoteId = remote.insertQuoteMasterDtls(finalDOB);//insert the quote details
          finalDOB.getMasterDOB().setQuoteId(quoteId);
          //finalDOB =  remote.getQuoteContentDtl(finalDOB);
          request.setAttribute("quoteId",""+quoteId);
         
          setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId, operation , "QMSQuoteController");
          session.removeAttribute("finalDOB");
          session.removeAttribute("PreFlagsDOB");
          doDispatcher(request, response, "QMSErrorPage.jsp");
        }else if("Modify".equalsIgnoreCase(operation)){
        	
        	
        	flagsDOB = (QuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
            if(flagsDOB!=null)
               finalDOB.setFlagsDOB(flagsDOB);
            else
               finalDOB.setFlagsDOB(new QuoteFlagsDOB());
                        
            masterDOB=finalDOB.getMasterDOB();
            masterDOB.setVersionNo(1);//@@The version for this quote will be 1
            finalDOB.setMasterDOB(masterDOB);   
            finalDOB.setUpdate(update);

            
            
            if(finalDOB!=null)
              legDetails  = finalDOB.getLegDetails();
            else
              legDetails  = new ArrayList();
            
            noOfLegs    = legDetails.size();
            
            originChargeIndices1 = request.getParameterValues("originChargeIndices");//added for duplicate charges
            //Added by Mohan Gajjala for on 30102010
            request.getParameterValues("surChkBox");
            if(originChargeIndices1!=null)
            {
            	int origCharInd1Len	=	originChargeIndices1.length;
                for(int oc=0;oc<origCharInd1Len;oc++)
                {
                  if(originChargeIndices1[oc].trim().length()>0)
                  {
                  originChargesTemp.add(originChargeIndices1[oc].trim());
                 
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
            
            destChargeIndices1 = request.getParameterValues("destChargeIndices");
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
            for(int i=0;i<noOfLegs;i++)
            {
              frtChargeIndices  = request.getParameterValues("frtChargeIndices"+i);
               surChargeIndices  = request.getParameterValues("surChargeIndices"+i);
               if(frtChargeIndices!=null)
              {
                
                  frtIndices   = new int[frtChargeIndices.length];
                  logger.info("doSellCharges Selecetd frtIndices.length"+frtIndices.length);//newly added                  
                  int frtChargIndLen	=	frtChargeIndices.length;
                  for(int j=0;j<frtChargIndLen;j++)
                  {
                    if(frtChargeIndices[j].trim().length()!=0)
                      frtIndices[j] =  Integer.parseInt(frtChargeIndices[j]);
                      logger.info("doSellCharges Selected frtChargeIndices : "+ frtIndices[j]); //newly added
                    
                  }
                  
               
              }
            
          
              legDOB  =   (QuoteFreightLegSellRates)legDetails.get(i);
  			      freightCharges = legDOB.getFreightChargesList();
             chargesDOB			= (QuoteCharges)freightCharges.get(0);
             chargesDOB.setFrequencyChecked(request.getParameter("frequencyCheck"+i));
             chargesDOB.setTransitTimeChecked(request.getParameter("transitTimeCheck"+i));
             chargesDOB.setCarrierChecked(request.getParameter("carrierCheck"+i));
             chargesDOB.setRateValidityChecked(request.getParameter("rateValidityCheck"+i));
             freightCharges.remove(0);
             freightCharges.add(0,chargesDOB);
             legDOB.setFreightChargesList(freightCharges);
              legDOB.setSelectedFreightChargesListIndices(frtIndices);
              legDetails.remove(i);
              legDetails.add(i,legDOB);
            }
            
            finalDOB.setLegDetails(legDetails);
            
            finalDOB.setInternalNotes(removeEnterForNotes(request.getParameterValues("internalNotes")));
            finalDOB.setExternalNotes(removeEnterForNotes(request.getParameterValues("externalNotes")));
        
            finalDOB.setUpdate(update);
            preFlagsDOB      = (QuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
            updatedFlagsDOB  = (QuoteFlagsDOB)session.getAttribute("updatedSendOptions");
            session.removeAttribute("updatedSendOptions");
            finalDOB.setSelectedOriginChargesListIndices(originIndices);
            finalDOB.setSelctedDestChargesListIndices(destIndices);
            session.setAttribute("finalDOB",finalDOB);
            request.setAttribute("attachmentIdLOVList",attachmentIdLOVList);

            finalDOB.setCompareFlag(true);
            quoteId = remote.modifyQuoteMasterDtls(finalDOB);//Modify the quote details
            finalDOB.getMasterDOB().setQuoteId(quoteId);
            //finalDOB =  remote.getQuoteContentDtl(finalDOB);
            request.setAttribute("quoteId",""+quoteId);
           
            setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId, operation , "QMSQuoteController");
            session.removeAttribute("finalDOB");
            session.removeAttribute("PreFlagsDOB");
            doDispatcher(request, response, "QMSErrorPage.jsp");
        }
      }
      //ended by Rakesh
      else if("<<Back".equalsIgnoreCase(request.getParameter("submit")))
      {
 
       if("View".equalsIgnoreCase(operation))
          session.setAttribute("viewFinalDOB",finalDOB);
        else
          session.setAttribute("finalDOB",finalDOB);
        
        doDispatcher(request,response,request.getParameter("fromWhere"));
      }
      else if("Next>>".equalsIgnoreCase(request.getParameter("submit"))|| "Update".equalsIgnoreCase(request.getParameter("submit")))// Added by Gowtham for Escalated Quote Updatation.
      {       
    	  if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))  // Added by Gowtham.
    	  {
    		  finalDOB =  doMarginTest(request,response,finalDOB);
    		  session.setAttribute("finalDOB",finalDOB);   
    	  }
    	  
           attachmentIdList =   remote.getAttachmentDtls(finalDOB);//@@added for the WPBN issue-61289
          finalDOB.setAttachmentDOBList(attachmentIdList);  //@@added for the WPBN issue-61289
         
         finalDOB.setDefaultFlag("N");
         attachmentIdLOVList  =   remote.getAttachmentIdList(finalDOB,"");
         
        if("View".equalsIgnoreCase(operation))
        {
          session.setAttribute("viewFinalDOB",finalDOB);
          forwardPage = "qms/QMSQuoteSummaryView.jsp";
        }
        else
        {
            marginTestFlag   = request.getParameter("marginTestFlag");
            if(finalDOB!=null)
              legDetails  = finalDOB.getLegDetails();
            else
              legDetails  = new ArrayList();
            
            noOfLegs    = legDetails.size();
            
           // originChargeIndices = request.getParameterValues("originChargeIndices");//commented for duplicate charges
            originChargeIndices1 = request.getParameterValues("originChargeIndices");//added for duplicate charges
            //Added by Mohan Gajjala for on 30102010
            request.getParameterValues("surChkBox");
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
            destChargeIndices1 = request.getParameterValues("destChargeIndices");
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
            for(int i=0;i<noOfLegs;i++)
            {
              frtChargeIndices  = request.getParameterValues("frtChargeIndices"+i);
               surChargeIndices  = request.getParameterValues("surChargeIndices"+i);
               if(frtChargeIndices!=null)
              {
                
                  frtIndices   = new int[frtChargeIndices.length];
                  logger.info("doSellCharges Selecetd frtIndices.length"+frtIndices.length);//newly added                  
                  int frtChargIndLen	=	frtChargeIndices.length;
                  for(int j=0;j<frtChargIndLen;j++)
                  {
                    if(frtChargeIndices[j].trim().length()!=0)
                      frtIndices[j] =  Integer.parseInt(frtChargeIndices[j]);
                      logger.info("doSellCharges Selected frtChargeIndices : "+ frtIndices[j]); //newly added
                    
                  }
                  //System.out.println("surChargeIndices--->"+surChargeIndices);
                  //commneted by MOhan for issue 
                 /* if(surChargeIndices!=null)
                  {
                	  int surChrgLength =  surChargeIndices.length;
                	  for(int k=0;k<surChrgLength;k++)
                	  {
                		  System.out.println("Selected Surcharge "+surChargeIndices[k]);
                		  if(surChargeIndices[k]==null || "".equals(surChargeIndices[k]))
                		  {
                			  legDOB  =   (QuoteFreightLegSellRates)legDetails.get(i);
                			  ArrayList alist = (ArrayList)legDOB.getFreightChargesList();
                			  for(int fchrgLst=0; fchrgLst <alist.size();fchrgLst++)
                			  {
                				  QuoteCharges chrgs = (QuoteCharges)alist.get(fchrgLst);
                				  ArrayList clist = (ArrayList)chrgs.getChargeInfoList();
                				  for (int chrgsindex = 0; chrgsindex< clist.size(); chrgsindex++)
                				  {
                					  QuoteChargeInfo qci  = (QuoteChargeInfo)clist.get(chrgsindex);
                					  System.out.println("Quote Rate Desc-->"+qci.getRateDescription());
                					  if(qci.getRateDescription().equals("SECURITY SURCHARGE") || qci.getRateDescription().equals("FUEL SURCHARGE"))
                					  { 
                						 ((ArrayList) ((QuoteCharges)((ArrayList)(legDOB.getFreightChargesList())).get(fchrgLst)).getChargeInfoList()).remove(chrgsindex);
                						  //clist.remove(chrgsindex);
                					  }

                				  }
                			  }
                		  }
                	  }

                  }*/
               
              }
            
          
              legDOB  =   (QuoteFreightLegSellRates)legDetails.get(i);
			      freightCharges = legDOB.getFreightChargesList();
             chargesDOB			= (QuoteCharges)freightCharges.get(0);
             chargesDOB.setFrequencyChecked(request.getParameter("frequencyCheck"+i));
             chargesDOB.setTransitTimeChecked(request.getParameter("transitTimeCheck"+i));
             chargesDOB.setCarrierChecked(request.getParameter("carrierCheck"+i));
             chargesDOB.setRateValidityChecked(request.getParameter("rateValidityCheck"+i));
             freightCharges.remove(0);
             freightCharges.add(0,chargesDOB);
             legDOB.setFreightChargesList(freightCharges);
              legDOB.setSelectedFreightChargesListIndices(frtIndices);
              legDetails.remove(i);
              legDetails.add(i,legDOB);
            }
            
            finalDOB.setLegDetails(legDetails);
//@@ Commented by subrahmanyam for the Notes Issue 194328.
            /*
            finalDOB.setInternalNotes(request.getParameterValues("internalNotes"));
            finalDOB.setExternalNotes(request.getParameterValues("externalNotes"));
            
             */
//@@ Added by the subrahmanyam for the notes Issue 194328.            
            finalDOB.setInternalNotes(removeEnterForNotes(request.getParameterValues("internalNotes")));
            finalDOB.setExternalNotes(removeEnterForNotes(request.getParameterValues("externalNotes")));
//@@ Ended for notes Issue 194328.            
            finalDOB.setUpdate(update);//@@Added by Kameswari on 08/01/09
            preFlagsDOB      = (QuoteFlagsDOB)session.getAttribute("PreFlagsDOB");
            updatedFlagsDOB  = (QuoteFlagsDOB)session.getAttribute("updatedSendOptions");
            session.removeAttribute("updatedSendOptions");
            flagsDOB  = new QuoteFlagsDOB();
            
          
            if(marginTestFlag != null)
            {          
              if(finalDOB.getSelectedOriginChargesListIndices()!=null && originChargeIndices!=null)
              {
                if(originChargeIndices.length > finalDOB.getSelectedOriginChargesListIndices().length)
                {
                    isCheating  = true;
                    sb.append("One of the Selected Origin Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
                }
              }
              if(finalDOB.getSelctedDestChargesListIndices()!=null && destChargeIndices!=null)
              {
                if(destChargeIndices.length > finalDOB.getSelctedDestChargesListIndices().length)
                {
                    isCheating  = true;
                    sb.append("One of the Selected Destination Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
                }
              }
              if(finalDOB.getSelectedOriginChargesListIndices()==null && originChargeIndices!=null)
              {
                  isCheating  = true;
                  sb.append("One of the Selected Origin Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");
              }
              
              if(finalDOB.getSelctedDestChargesListIndices()==null && destChargeIndices!=null)
              {
                  isCheating  = true;
                  sb.append("One of the Selected Destination Charges Has Not Been Tested for Margin. Please Perform the Margin Test to Proceed.<br>");    
              }
              
              if(isCheating)
              {
                forwardPage = "qms/QMSQuoteChargesSelect.jsp?count=0";
              }
              
              else
              {
                forwardPage = "qms/QMSQuoteSummary.jsp?update=update";
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
            }
           if("Y".equalsIgnoreCase(request.getParameter("isEscalated"))) // Added by Gowtham
           {
        	   flagsDOB.setQuoteStatusFlag("APP");
        	   finalDOB.setFlagsDOB(flagsDOB);
           }
           else
            finalDOB.setFlagsDOB(flagsDOB);
            finalDOB.setSelectedOriginChargesListIndices(originIndices);
            finalDOB.setSelctedDestChargesListIndices(destIndices);
            session.setAttribute("finalDOB",finalDOB);
            request.setAttribute("attachmentIdLOVList",attachmentIdLOVList);

        }
       // Logger.info(FILE_NAME,"sbsbsbsb::"+sb.length());
        request.setAttribute("errors",sb);
        //session.setAttribute("finalDOB",finalDOB);
        if("Y".equalsIgnoreCase(request.getParameter("isEscalated")))  // Added by Gowtham for Escalated Issue.
        {		doSummary(request,response);
    	  setErrorRequestValues(request, "RSI", "The Escalated Quote Updated Successfully ", operation , "QMSQuoteController");
    	  session.removeAttribute("finalDOB");
    	  forwardPage="QMSErrorPage.jsp";  // Added by Gowtham for Escalated Issue./
      	}
        else
        doDispatcher(request,response,forwardPage);
      }
      else if("Margin Test".equalsIgnoreCase(request.getParameter("submit")))
      {
        finalDOB =  doMarginTest(request,response,finalDOB);
        session.setAttribute("finalDOB",finalDOB);
        request.setAttribute("isMarginTestPerformed","Y");
        //@@ Commented by subrahmanyam for the Enhancement 154381 on 03/02/09        
        //doDispatcher(request,response,"qms/QMSQuoteChargesSelect.jsp?count="+count);
//@@ Added by subrahmanyam for the Enhancement 154381 on 03/02/09        
        MarginTestCount=(Integer)session.getAttribute("MarginTestCount");
       if(MarginTestCount!=null)
       {
         mTCount=MarginTestCount.intValue();
       }
        mTCount++;
        MarginTestCount=new Integer(mTCount);
        session.setAttribute("MarginTestCount",MarginTestCount);
        if("Y".equalsIgnoreCase(request.getParameter("isEscalated"))) // Added by Gowtham.
        	doDispatcher(request,response,"qms/QMSQuoteEscalatedChargesSelect.jsp?MarginTest=Yes&count="+count);
        else
        doDispatcher(request,response,"qms/QMSQuoteChargesSelect.jsp?MarginTest=Yes&count="+count);
//@@ Ended by subrahmanyam for the Enhancement 154381 on 03/02/09        
      }
    }
    catch(Exception ex)
		{
      session.removeAttribute("finalDOB");
      session.removeAttribute("PreFlagsDOB");
      session.removeAttribute("updatedSendOptions");
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [error in doSellCharges()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [error in doSellCharges()] -> "+ex.toString());
      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSQuoteController?Operation=Add");
      doDispatcher(request, response, "ESupplyErrorPage.jsp");
		}
    
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
    
    QuoteFinalDOB           finalDOB          = null;
    QuoteMasterDOB          masterDOB         = null;
    QuoteFlagsDOB           flagsDOB          = null;
    QuoteFlagsDOB           preFlagsDOB       = null;
    QuoteAttachmentDOB      attachmentDOB     = null; 
    QMSQuoteSessionHome     home              = null;
    QMSQuoteSession         remote            = null;
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
    boolean 				compareFlag		  = false; // Added by Gowtham.	
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
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
       ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
      if("View".equalsIgnoreCase(operation))
          finalDOB      =   (QuoteFinalDOB)session.getAttribute("viewFinalDOB");
      else
          finalDOB      =   (QuoteFinalDOB)session.getAttribute("finalDOB");
   
      masterDOB   = finalDOB.getMasterDOB();
     

       countryId = remote.getCountryId(masterDOB.getCustomerAddressId());
      
        masterDOB.setCountryId(countryId);
          
        if(attachmentId!=null)
       {
        	int attchmentIdLen	= attachmentId.length;
         for(int i=0;i<attchmentIdLen;i++)
         {
           
              attachmentDOB   =   new QuoteAttachmentDOB();
              if(!("".equalsIgnoreCase(attachmentId[i])))
              {
              attachmentDOB = new QuoteAttachmentDOB();
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
      //@@WPBN isssue-61306
      if("<<Back".equalsIgnoreCase(request.getParameter("submit")))
      {
        //session.setAttribute("finalDOB",finalDOB);
        if("View".equalsIgnoreCase(operation))
        {
            session.setAttribute("viewFinalDOB",finalDOB);
            doDispatcher(request,response,"qms/QMSQuoteChargesView.jsp");
        }
        else
        {
          
            session.setAttribute("finalDOB",finalDOB);
            if("true".equalsIgnoreCase(request.getParameter("flag")))
              doDispatcher(request,response,request.getParameter("fromWhere"));
            else
              doDispatcher(request,response,"qms/QMSQuoteChargesSelect.jsp?count=0");
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
            //location  = remote.getLocation(masterDOB.getSalesPersonName());
            //location =location.substring(3);
            //quoteId=location+"_"+quoteId;
      
             finalDOB.getMasterDOB().setQuoteId(quoteId);
             
            //System.out.println("quoteId  :"+quoteId);
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
                    setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds.toString()+" & Fax has been sent to "+toFaxIds , operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 2  || mailStatus == 6)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax has been sent to "+toFaxIds , operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & Fax has been sent to "+toFaxIds , operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 1 || mailStatus == 5)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds.toString()+" & Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds.toString(), operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 0  || mailStatus==4)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "N".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                      setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)"), operation , "QMSQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) && "N".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                      setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId, operation , "QMSQuoteController");
                      
                  }
                  session.removeAttribute("finalDOB");
                  session.removeAttribute("PreFlagsDOB");
                  session.setAttribute("isMultiQuote", "SingleQuote");//added by silpa.p on 6-06-11 for redirect costing
                  doDispatcher(request, response, "QMSErrorPage.jsp");              
                }
               else
               {
                setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully inserted with the QuoteId "+quoteId, operation , "QMSQuoteController");
                session.removeAttribute("finalDOB");
                session.removeAttribute("PreFlagsDOB");
                session.setAttribute("isMultiQuote", "SingleQuote");//added by silpa.p on 6-06-11 for redirect costing
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
              setErrorRequestValues(request, "RSI", "The Quote is generated with Id "+quoteId+" and has been escalated to "+finalDOB.getReportingOfficer(), operation , "QMSQuoteController");
              session.removeAttribute("finalDOB");
              session.removeAttribute("PreFlagsDOB");
              session.setAttribute("isMultiQuote", "SingleQuote");//added by silpa.p on 6-06-11 for redirect costing
              doDispatcher(request, response, "QMSErrorPage.jsp");
            }          
            remote.updateSendMailFlag(quoteId,masterDOB.getUserId(),operation,false,mailStatus);          
          }
        }
        else if("Modify".equalsIgnoreCase(operation))
        {
           if("Next>>".equalsIgnoreCase(request.getParameter("submit"))||"Update".equalsIgnoreCase(request.getParameter("submit"))) // Added by Gowtham for Escalted Quote Modify Issue.
          {
            flagsDOB = finalDOB.getFlagsDOB();
            flagsDOB.setCompleteFlag("C");
            
            flagsDOB.setActiveFlag("A");
            
            if(!"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()) && !"Update".equalsIgnoreCase(request.getParameter("submit")))
              flagsDOB.setQuoteStatusFlag("GEN");//(once the quote is sent, this status is updated to P-Pending)
            if("Update".equalsIgnoreCase(request.getParameter("submit")) && !"Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()))
            		 flagsDOB.setQuoteStatusFlag("APP");//(once the quote is sent, this status is updated to P-Pending)	
            
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
            
            preFlagsDOB = (QuoteFlagsDOB)request.getSession().getAttribute("PreFlagsDOB");
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
                    setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds+" & Fax has been sent to "+toFaxIds , operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 2 || mailStatus == 6)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                     setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax has been sent to "+toFaxIds , operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & Fax has been sent to "+toFaxIds , operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 1 || mailStatus == 5)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds+" but Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" & eMail has been sent to :"+toEmailIds, operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 0 || mailStatus==4)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "N".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)"), operation , "QMSQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) && "N".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                      setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId+" but eMail was not sent to :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax was not sent to "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSM", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId, operation , "QMSQuoteController");
                      
                  }
                  session.removeAttribute("finalDOB");
                  session.removeAttribute("PreFlagsDOB");
                  session.setAttribute("isMultiQuote", "SingleQuote");//added by silpa.p on 6-06-11 for redirect costing
                  doDispatcher(request, response, "QMSErrorPage.jsp");
                }
               else
               {
                setErrorRequestValues(request, "RSI", "The Quote Details are Succesfully Modified with the QuoteId "+quoteId, operation , "QMSQuoteController");
                session.removeAttribute("finalDOB");
                session.removeAttribute("PreFlagsDOB");
                session.setAttribute("isMultiQuote", "SingleQuote");//added by silpa.p on 6-06-11 for redirect costing
                doDispatcher(request, response, "QMSErrorPage.jsp");
               }          
            }
            else
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
              setErrorRequestValues(request, "RSI", "The Quote is generated with Id "+quoteId+" and has been escalated to "+finalDOB.getReportingOfficer(), operation , "QMSQuoteController");
              session.removeAttribute("finalDOB");
              session.removeAttribute("PreFlagsDOB");
              session.setAttribute("isMultiQuote", "SingleQuote");//added by silpa.p on 6-06-11 for redirect costing
              doDispatcher(request, response, "QMSErrorPage.jsp");
            }
            
            remote.updateSendMailFlag(quoteId,masterDOB.getUserId(),operation,compareFlag,mailStatus);
          }
        }
        else if("View".equalsIgnoreCase(operation))
        {
            String actionPage = "";
            
            if("Approved".equalsIgnoreCase(request.getParameter("fromWhere")))
                actionPage    =   "QMSReportController?Operation=Approved"; 
            else
            {
            if("view".equalsIgnoreCase(operate))
                actionPage    =   "QMSQuoteController?from=summary"; 
             else
                actionPage    =   "QMSQuoteController"; 
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
                doDispatcher(request, response, "qms/QMSQuoteEnterId.jsp?Operation=View");
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
      setErrorRequestValues(request, "ERR", "Error occured while processing.Please redo the operation ", operation , "QMSQuoteController?Operation="+operation);
      doDispatcher(request, response, "ESupplyErrorPage.jsp");
		}
    
  }
  
 
  private   int doPDFGeneration(QuoteFinalDOB finalDOB,HttpServletRequest request,HttpServletResponse response)throws Exception
  {
   
    int                mailFlag        = 0;
    int                faxFlag         = 0;
    int                printFlag       = 0;
    int                returnFlag      = 3;
    String             transitTime     = null;
    String[]           contents        = null;
    String[]           levels          = null;
    String[]           aligns          = null;
    String[]           headFoot        = null;
     //@@Added by Kameswari for the WPBN issue-61289
    ArrayList          dobList         = new ArrayList();
    QuoteAttachmentDOB attachmentDOB   = null;
    //@@Added by Kameswari for the WPBN issue-146448
    ArrayList          charges         = null;
     int               chargesSize     = 0;
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
    ArrayList          filesList       = new ArrayList();
    File               file            = null;
    byte[]             buffer          = null;
    ArrayList          bufferList      = new ArrayList();
     ArrayList         pdfFilesList    = new ArrayList();
    HttpSession        session         = null;
    String             percent         ="";//Added by kiranv on 16/11/2011
PdfWriter writer                       =null;        //Method: doPDFGeneration Defect: PdfWriter is not closedSuggestion: Close and nullify the writer in the finally block.
//@@ Added by subrahmanyam for the WPBN ISSUE: 146460 on 29/01/2009    
    QMSQuoteSessionHome     home              = null;
    QMSQuoteSession         remote            = null;
//@@ Ended by subrahmanyam for the WPBN ISSUE: 146460 on 29/01/2009   

      //@@ WPBN issue-61289
   try
    {       
          
            DecimalFormat df  						  =   new DecimalFormat("###,###,###,##0.00");
            QuoteHeader    headerDOB		          =	  finalDOB.getHeaderDOB();
            QuoteMasterDOB masterDOB              	  =   finalDOB.getMasterDOB();
            ESupplyDateUtility  eSupplyDateUtility    =	  new ESupplyDateUtility();
            ESupplyGlobalParameters loginbean  		  =   (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
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
            	//@@Modified by kiran.v on 05/08/2011 for Wpbn Issue-	256087
                if("PDF".equalsIgnoreCase(request.getParameter("pdf")))
                {
                	strDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getCreatedDate());
                //	effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
                	effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
                }
                else{
                	
                	strDate  = eSupplyDateUtility.getDisplayStringArray(masterDOB.getCreatedDate());
                	  effDate  = eSupplyDateUtility.getDisplayStringArray(headerDOB.getEffDate());
                        
                }//@@Ended by kiran.v
                
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
           // PdfFileStamp fileStamp = new PdfFileStamp("Approved.pdf");
            
            //PdfPageEventHelper helper
            
            //writer.setPageEvent(new PdfPageEventHelper());
         
          //document.setMargins(15,15,15,15);            
            // Draw a rectangle inside the page's margins.
            //PdfContentByte cb = writer.getDirectContent();
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
            cellHeading.setBorderWidth(0);
            cellHeading.setColspan(6);
            mainT.addCell(cellHeading);
            
            Cell imageCell = new Cell();            
            java.net.URL url =  getServletConfig().getServletContext().getResource("/images/DHLlogo.gif");
            Image img0 = Image.getInstance(url);
            img0.setAlignment(Image.ALIGN_RIGHT);
            
            //imageCell.setWidth("");
            imageCell.setColspan(1);
            imageCell.setHorizontalAlignment(imageCell.ALIGN_RIGHT);
            imageCell.add(img0);
            imageCell.setBorderWidth(0);
            imageCell.setBorderWidth(0);
            imageCell.setNoWrap(true);
            mainT.addCell(imageCell);
            mainT.setAlignment(mainT.ALIGN_CENTER);
            document.add(mainT);
          
           // System.out.println("After Image && Before Content--------------------------->");
                                   
            Table partCountry = new Table(1,6); 
            partCountry.setBorderWidth(0);
            partCountry.setWidth(100);
            partCountry.setBorderColor(Color.black);            
            partCountry.setPadding(1);
            partCountry.setSpacing(0);
            partCountry.setAutoFillEmptyCells(true);
            //partCountry.setTableFitsPage(true);
            partCountry.setAlignment(partCountry.ALIGN_CENTER);
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
            
            Chunk chk = new Chunk(shipmentMode,FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
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
              chk = new Chunk((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():"")+" TO "+(headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""),FontFactory.getFont("ARIAL", 16, Font.BOLD,Color.RED));
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
            partCountry.addCell(cellCountry);          
            
            document.add(partCountry); 
            
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
            }
            //@@Added by Kameswari for the WPBN issue-146448 on 03/12/08
         for(int i=0;i<chargesSize;i++)
        {
          legCharges	   = (QuoteFreightLegSellRates)charges.get(i);
      
      
          freightCharges = legCharges.getFreightChargesList();
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
        }  // partCountry  =  new Table(2,13);
            size   =  frequency.size()+transittime.size()+carrier.size()+ratevalidity.size();
            
            partCountry  =  new Table(2,13+size);
             //@@WPBN issue-146448 on 03/12/08
            partCountry.setOffset(5);
            partCountry.setWidth(100);
            partCountry.setPadding(1);
            partCountry.setSpacing(0);
            partCountry.setBackgroundColor(Color.WHITE);
            partCountry.setBorderColor(Color.black);
            partCountry.setBorderWidth(1f);
            
            chk = new Chunk("Prepared By: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
          
            
            chk = new Chunk("SERVICE INFORMATION: ",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
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
            
         // @@Commented by subrahmanyam for the enhancement #148546 on 09/12/2008            
           /* chk = new Chunk("Routing: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);          
            chk = new Chunk("   "+(headerDOB.getRouting()!=null?headerDOB.getRouting():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
           
 
            chk = new Chunk("   "+(headerDOB.getRouting()!=null?headerDOB.getRouting().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);*/
//@@ Ended by subrahmanyam for the enhanement #148546 on 09/12/2008             
            
            chk = new Chunk("Commodity or Product: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getCommodity()!=null?headerDOB.getCommodity().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("Type Of Service Quoted: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
          //@@Added by kiran.v on 04/11/2011 for Wpbn Issue -277534
            int index=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService().length():0;
            if(index<47)
            chk = new Chunk("   "+(headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService().toUpperCase():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            else
            	 chk = new Chunk("   "+(headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService().toUpperCase().substring(0, 47)+"\n   "+headerDOB.getTypeOfService().toUpperCase().substring(47):"")
            			 ,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));	
            // ended by kiran.v
            cellCountry = new Cell(chk);
            //@@commented by kiran.v on 03/11/2011 for Wpbn Issue
            //cellCountry.setNoWrap(true); 
            cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
          
        //@@Added by Kameswari for the WPBN issue-146448 on 03/12/08
            int freqSize	= frequency.size();
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
          int carierSize	=	carrier.size();
             for(int i=0;i<carierSize;i++)
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
             int tTimeSize	= transittime.size();
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
          int rTvalSize	=	ratevalidity.size();
          for(int i=0;i<rTvalSize;i++)
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
            
            //@@ COmmented by subrahmanyam for the wpbn issue:145510  on 10/12/2008     
            /*  chk = new Chunk("   "+(headerDOB.getNotes()!=null?headerDOB.getNotes().toUpperCase()+'\n'+"nnnnnnnnnn":""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);*/
             //@@ Added by subrahmanyam for the wpbn issue: 145510on 10/12/2008          
            chk = new Chunk(" "+(headerDOB.getNotes()!=null?headerDOB.getNotes().toUpperCase()+'\n':""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
        //@@ ended by subrahmanyam for the wpbn issue: 145510  on 10/12/2008 
            
            chk = new Chunk("Date Effective: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(effDate[0]!=null?effDate[0]:""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("Validity Of Quote: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
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
          // b1= writer.getPageNumber();
      
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
            
           
            //@@Added by Kameswari for the WPBN issue-146448
             charges				                      = finalDOB.getLegDetails();
              chargesSize			                    = charges.size();
            
            Table chargeCountry  = null ;
            //Table chargeCountry1= null;
            Cell  cell           = null ;
           // float cellWidths[]   = {40,20,10,15,25};//@@Added by Kameswari for the WPBN issue - on 12/11/08
           float cellWidths[]   = {40,20,10,15,25,15};
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
            QuoteChargeInfo chargeInfo        = null;
            if(originChargesSize>0)
				    {
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
                chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
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
                  if(originIndices[i] !=-1)
                  {
                  chargesDOB				    = (QuoteCharges)originCharges.get(originIndices[i]);
                   logger.info("Origin Charges doPDFGeneration::"+i+":"+chargesDOB); // newly added                  
                  originChargeInfo		  = chargesDOB.getChargeInfoList();
                  originChargesInfoSize	= originChargeInfo.size();
                  int m =0;//146455
              String breakPoint = null;//146455
                  for(int k=0;k<originChargesInfoSize;k++)
                  {
                    chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
                    if(k==0)
                    {
                      if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
                        chk = new Chunk(chargesDOB.getExternalName()!=null?chargesDOB.getExternalName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      else
                        chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      
                      cell = new Cell(chk);
                      cell.setLeading(9.0f);
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
                      cell.setBackgroundColor(Color.lightGray);
                      cell.setHeader(true);
                      cell.setBorderWidth(0);
                      chargeCountry.addCell(cell);
                    }
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
                 
                  chk = new Chunk(breakPoint!=null&& !"Absolute".equalsIgnoreCase(breakPoint)&&!"Percent".equalsIgnoreCase(breakPoint)?breakPoint:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setLeading(8.0f);
                      cell.setBackgroundColor(Color.lightGray);
                      cell.setHeader(true);
                      chargeCountry.addCell(cell);
//@@ Ended by subrahmanyam for 146455 on 19/02/09                
                      
                      chk = new Chunk(chargeInfo.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      //cell.setBackgroundColor(Color.lightGray);
                      chargeCountry.addCell(cell);
                      //@@Modified by kiran.v on 03/11/2011 for Wpbn Issue
                     // chk = new Chunk(df.format(chargeInfo.getSellRate())+(chargeInfo.isPercentValue()?" %":""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                    //@@Added by kiran.v on 16/11/2011
                      percent=chargeInfo.isPercentValue()?"%":"";
                      chk = new Chunk(round1(chargeInfo.getSellRate(),percent)+(percent),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);  
                      cell.setLeading(8.0f);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      chargeCountry.addCell(cell);  
                      
                     //@@Added by Kameswari for the WPBN issue- on 12/11/08
                      chk = new Chunk(chargeInfo.getRatio()!=null?"1:"+chargeInfo.getRatio():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);  
                      cell.setLeading(8.0f);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      chargeCountry.addCell(cell);  
                      
                  }
                }
				}
               document.add(chargeCountry); 
            }
            
          
            // boolean b1 = document.newPage();
             
              //Freight Charges
              //document.setMargins(10,10,10,10);
              //System.out.println("After         Origin Charges --------------------------------->");
              int       freightChargesSize                  = 0;
              int       freightChargesInfoSize              = 0;
              int[]     frtIndices                          = null;
              //QuoteFreightLegSellRates       legCharges	    = null;
             // ArrayList                      freightCharges = null;
              ArrayList                      freightChargeInfo = null;
              int m =0;
              String breakPoint = null;
              String   space   ="";
              Table country = null;
                // b2= writer.getPageNumber();
        
                if(chargesSize>0)
                {
                  // chargeCountry  = new Table(5);//@@Added by Kameswari for the WPBN issue - on 12/11/08
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
                  chargeCountry.setTableFitsPage(true);//@@Added by kameswari 
                 
                  for(int i=0;i<chargesSize;i++)
                  {
                     legCharges	   = (QuoteFreightLegSellRates)charges.get(i);
                      freightCharges =  legCharges.getFreightChargesList();
              
                      frtIndices	   =  legCharges.getSelectedFreightChargesListIndices();
                      
                      if(frtIndices!=null)
                        freightChargesSize = frtIndices.length;
                      else
                        freightChargesSize	= 0;
               
             
                    if(i==0 && freightChargesSize>0)
                    {
                      //@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                         /* chk = new Chunk("FREIGHT CHARGES",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                          cell = new Cell(chk);
                          cell.setLeading(8.0f);
                          cell.setBackgroundColor(Color.ORANGE);
                         //cell.setColspan(5);//@@Added by Kameswari for the WPBN issue - on 12/11/08
                          cell.setColspan(6);
                          cell.setHeader(true);
                          chargeCountry.addCell(cell);*/
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
                      logger.info("Freight Charges doPDFGeneration::"+j+":"+chargesDOB); // newly added
                      freightChargeInfo		= chargesDOB.getChargeInfoList();
                      freightChargesInfoSize	= freightChargeInfo.size();                    
                       int count=0; //Added by subrahmanyam for 181430
                       String tempDesc = "";//Added by subrahmanyam for 181430
                      for(int k=0;k<freightChargesInfoSize;k++)
                      {
                        String temp="";
                        if(k>0)
                        {
                          chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k-1);
                          temp =chargeInfo.getRateDescription();
                      
                        }
                         chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k);
                        if(k==0)
                        {
                          chk = new Chunk(legCharges.getOrigin()+"-"+legCharges.getDestination(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                          cell = new Cell(chk);
                         // cell.setColspan(5);//@@Added by Kameswari for the WPBN issue - on 12/11/08
                          cell.setColspan(6);
                          cell.setBackgroundColor(Color.WHITE);
                          cell.setLeading(8.0f);
                          chargeCountry.addCell(cell);
    //@@ Commented by subrahmanyam for 146455 on 19/02/09                      
                          /*//chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId():"Freight Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          chk = new Chunk(chargeInfo.getRateDescription(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setHeader(true);
                          //cell.setRowspan(freightChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setLeading(9.0f);                         
                          chargeCountry.addCell(cell);*/
  //@@Added by subrahmanyam for 146455 on 18/02/09
                          String fRate="";
                          if("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))
                          {
                              fRate="Freight Rate";
                          }
                          else
                            fRate=chargeInfo.getRateDescription();
                          chk = new Chunk(fRate!=null?fRate:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setHeader(true);
                          //cell.setRowspan(freightChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setLeading(9.0f);                         
                          chargeCountry.addCell(cell);
 //@@ Ended by subrahmanym for 146455 on 18/02/09
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
    //@@ Commented by subrahmanyam for 146455 on 19/02/09  
                       /* chk = new Chunk(chargeInfo.getRateDescription(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setHeader(true);
                          //cell.setRowspan(freightChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setLeading(9.0f);                         
                          chargeCountry.addCell(cell);*/
         //@@Added by subrahmanyam for 146455 on 18/02/09                          
                        String rateDes="";
                        if("FUEL SURCHARGE".equalsIgnoreCase(chargeInfo.getRateDescription()))
                        {
                          rateDes="Fuel Surcharge";
                        }
                        else if("SECURITY SURCHARGE".equalsIgnoreCase(chargeInfo.getRateDescription()))
                        {
                          rateDes="Security Surcharge";
                        }
                        else if("C.A.F%".equalsIgnoreCase(chargeInfo.getRateDescription()) ||  "CAF%".equalsIgnoreCase(chargeInfo.getRateDescription()))
                        {
                          rateDes="C.a.f%";
                        }
                        else if("B.A.F".equalsIgnoreCase(chargeInfo.getRateDescription()) || "BAF".equalsIgnoreCase(chargeInfo.getRateDescription()))
                        {
                          rateDes="B.a.f";
                        }
                        else if("P.S.S".equalsIgnoreCase(chargeInfo.getRateDescription()) || "PSS".equalsIgnoreCase(chargeInfo.getRateDescription()))
                        {
                          rateDes="P.s.s";
                        }
                        else if("C.S.F".equalsIgnoreCase(chargeInfo.getRateDescription()) || "CSF".equalsIgnoreCase(chargeInfo.getRateDescription()))
                        {
                          rateDes="C.s.f";
                        }
                        else{
                        
                         rateDes=chargeInfo.getRateDescription();
                        	rateDes = rateDes!= null && rateDes.indexOf(".")!= -1 ?rateDes.substring(0,rateDes.length()-3):(rateDes!= null && rateDes.indexOf("-")==-1)?rateDes:rateDes.substring(0,rateDes.length()-3); // Added by Gowtham to trim surcharge Description
                        }
                        
                          chk = new Chunk(rateDes!=null?rateDes:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setHeader(true);
                          //cell.setRowspan(freightChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setLeading(9.0f);                         
                          chargeCountry.addCell(cell);
   //@@ Ended by subrahmanym for 146455 on 18/02/09
                        }
                     /*if("FSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                         chargeInfo.setBreakPoint("Basic Charge");
                     if("FSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                         chargeInfo.setBreakPoint("Minimum");
                     if("FSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSKG".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                         chargeInfo.setBreakPoint("Flat Rate");*/
                          
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
                //@@ Commented by subrahmanyam for 146455 on 19/02/09  
                             /*else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                                           ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN")
                                           )
                              {
                                        breakPoint = "Min";
                              }*/
               //@@Added by subrahmanyam for 146455 on 18/02/09  
                            else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                                           ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN")
                                           ||chargeInfo.getBreakPoint().equalsIgnoreCase("MIN")||chargeInfo.getBreakPoint().toUpperCase().endsWith("MIN")) // Added by gowtham to show surcharge description as min
                              {
                                      // added by subrahmanyam for the wpbn id: 181430 on 08-sep-09
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
                          /* else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG"))
                             {
                                          breakPoint = "Flat";
                         }*/
                    //Added by subrahmanyam for 154381
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
              //@@Added by subrahmanyam for 146455 on 18/02/09  
                   else if(chargeInfo.getBreakPoint().toUpperCase().equalsIgnoreCase("MAX")) // Added by Gowtham on18-Feb-2011
                   {
                      breakPoint ="Max";
                   }
                    else
                    {
                                      breakPoint= chargeInfo.getBreakPoint().length()>5?chargeInfo.getBreakPoint().substring(0,4):chargeInfo.getBreakPoint(); // Modified by Gowtham for Quote View Issue on 17 Feb2011
                  }
                     /* chk = new Chunk(chargeInfo.getBreakPoint()!=null?chargeInfo.getBreakPoint().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);
                      cell.setBackgroundColor(Color.lightGray);cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);*/
                      //chk = new Chunk(breakPoint!=null?breakPoint.toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      chk = new Chunk(breakPoint!=null?breakPoint:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);
                      cell.setBackgroundColor(Color.lightGray);cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(chargeInfo.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      //cell.setBackgroundColor(Color.lightGray);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(df.format(chargeInfo.getSellRate()),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                      chargeCountry.addCell(cell);
                      
                      chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setHeader(true);	
                      cell.setLeading(8.0f);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      chargeCountry.addCell(cell);
                      //@@Added by Kameswari for the WPBN issue- on 12/11/08
                      chk = new Chunk(chargeInfo.getRatio()!=null?"1:"+chargeInfo.getRatio():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);  
                      cell.setLeading(8.0f);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      chargeCountry.addCell(cell);  

                    }
                   }
                  }
                  document.add(chargeCountry);
             
                }//end of if(charge)
               //  boolean b2 = document.newPage();
             
                        
                //System.out.println("After Charges--------------------------------->");
                //document.setMargins(10,10,10,10);
                ArrayList  destChargeInfo             =  null;
                //int[]      destChargeInfo        =  null;
                int        destChargesInfoSize        =  0;
                if(destChargesSize>0)
                {                   
                    //chargeCountry  = new Table(5);
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
                    chargeCountry.setTableFitsPage(true);
                    
                  //    b2 = writer.getPageNumber();
                 
             /*   if(b2-b1>0)
                {
                 chk = new Chunk("QUOTE REFERENCE:"+masterDOB.getQuoteId(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setColspan(5);cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.WHITE);                
                cell.setHeader(true);
                cell.setBorder(0);
                chargeCountry.addCell(cell); 
                }*/
                //@@Commented and Modified by Kameswari for the internal issue on 09/04/09
                   /* chk = new Chunk("DESTINATION CHARGES",FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBackgroundColor(Color.ORANGE);
                   // cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
                   cell.setColspan(6);
                    cell.setLeading(8.0f);
                    cell.setHeader(true);
                    chargeCountry.addCell(cell);*/
                    //chargeCountry.endHeaders();
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
                chk = new Chunk("Density Ratio",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                //cell.setColspan(5);/@@Added by Kameswari for the WPBN issue - on 12/11/08
               // cell.setColspan(6);
                cell.setLeading(10.0f);
                cell.setBackgroundColor(Color.ORANGE);                
                cell.setHeader(true);
                chargeCountry.addCell(cell);
             
                    for(int j=0;j<destChargesSize;j++)
                    {
                      if(destIndices[j] !=-1)
                     {
                      chargesDOB				= (QuoteCharges)destCharges.get(destIndices[j]);
                      logger.info("Destination Charges doPDFGeneration::"+j+":"+chargesDOB); // newly added                      
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
                            chk = new Chunk(chargesDOB.getChargeDescriptionId()!=null?chargesDOB.getChargeDescriptionId():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);cell.setLeading(9.0f);
                          //cell.setRowspan(destChargesInfoSize);
                          //cell.setBorderWidth(0);
                          cell.setBackgroundColor(Color.lightGray);
                          cell.setHeader(true);       
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
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
                  
                  chk = new Chunk(breakPoint!=null&& !"Absolute".equalsIgnoreCase(breakPoint)&&!"Percent".equalsIgnoreCase(breakPoint)?breakPoint:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setLeading(8.0f);
                      cell.setBackgroundColor(Color.lightGray);
                      cell.setHeader(true);
                        chargeCountry.addCell(cell);
  //@@Ended by subrahmanyam for 146455 on 18/02/09   
                        
                        chk = new Chunk(chargeInfo.getCurrency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                        //cell.setBackgroundColor(Color.lightGray);
                        chargeCountry.addCell(cell);
                        //@@Modified by kiran.v on 03/11/2011 for Wpbn Issue
                       // chk = new Chunk(df.format(chargeInfo.getSellRate())+(chargeInfo.isPercentValue()?" %":""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                       //@@Added by kiran.v on 16/11/2011
                        percent=chargeInfo.isPercentValue()?"%":"";
                        chk = new Chunk(round1(chargeInfo.getSellRate(),percent)+(percent),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);cell.setHeader(true);cell.setLeading(8.0f);
                        chargeCountry.addCell(cell);
                        
                        chk = new Chunk(chargeInfo.getBasis()!=null?chargeInfo.getBasis():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                        cell.setHeader(true);cell.setLeading(8.0f);                     
                        chargeCountry.addCell(cell); 
                        //@@Added by Kameswari for the WPBN issue- on 12/11/08
                      chk = new Chunk(chargeInfo.getRatio()!=null?"1:"+chargeInfo.getRatio():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);cell.setHeader(true);  
                      cell.setLeading(8.0f);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      chargeCountry.addCell(cell);
                      }
                    }
					}
                    document.add(chargeCountry);
                }
                 if(finalDOB.getExternalNotes()!=null && finalDOB.getExternalNotes().length>0)
                {
                    Table notes  =  new Table(1,finalDOB.getExternalNotes().length+1);
                    notes.setWidth(100);
                    notes.setPadding(1);
                    notes.setSpacing(0);
                    notes.setOffset(5);
                    notes.setBackgroundColor(Color.WHITE);
                    notes.setBorderColor(Color.black);
                     //@@Commented and Modified by Kameswari for the internal issue on 08/04/09
                   notes.setDefaultHorizontalAlignment(Element.ALIGN_LEFT);
                    notes.setBorderWidth(1f);
                    Cell notesCell ;
                 
                    chk = new Chunk("NOTES",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
                    notesCell = new Cell(chk);
                    notesCell.setHeader(true);notesCell.setLeading(8.0f);
                    notesCell.setBackgroundColor(Color.WHITE);
                    notes.addCell(notesCell);
                    for(int i=0;i<finalDOB.getExternalNotes().length;i++)
                    {
                      chk = new Chunk(finalDOB.getExternalNotes()[i]!=null?finalDOB.getExternalNotes()[i]:"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
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
                  int hFLen	=	headFoot.length;
                  
                  
                  for(int i=0;i<hFLen;i++)
                  {
                    if(headFoot[i]!=null && "F".equalsIgnoreCase(headFoot[i]))
                    {
                      chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                      /////////////////chk.setUnderline(+1f,-2f);
                      
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
                 file = new File("Concatenated.pdf"+file_tsmp);
                  pdfFilesList.add((String)file.getName());
                   //@@Added by Kameswari for the WPBN issue-61289
 //@@ Added by subrahmanyam for the  WPBN ISSUE:146460 on 29/01/09             
                if(finalDOB.getAttachmentDOBList().size()==0 && "View".equalsIgnoreCase(request.getParameter("Operation")))
                {
                      
                      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");//146460
                      remote      = home.create();//146460
                       filesList  = remote.getQuoteAttachmentDtls(finalDOB);
                      if(filesList!=null)
                      {
                       finalDOB.setAttachmentDOBList(filesList);
                      }
                }
//@@ Ended by subrahmanyam for the WPBN ISSUE: 146460 on 29/01/09   
                   if(finalDOB.getAttachmentDOBList()!=null)
                {
                	 //filesList = finalDOB.getAttachmentDOBList(); // commented for for 192431 on 16-dec-09
                       //@@ Added by subrahmanyam for 192431 on 16-dec-09
                         if("pdf".equalsIgnoreCase(request.getParameter("pdf")))
                         {
                           home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");	
                           remote      = home.create();//146460
                           filesList  = remote.getQuoteAttachmentDtls(finalDOB);
                         }
                        else
                          filesList = finalDOB.getAttachmentDOBList();
                          // ended for 192431 on 16-dec-09
                  
                         int fileListSize	= filesList.size();	
                  for(int i=0;i<fileListSize;i++)
                  {
                       attachmentDOB = (QuoteAttachmentDOB)filesList.get(i);
                        FileOutputStream  fileStream= new FileOutputStream(attachmentDOB.getFileName());
                        fileStream.write(attachmentDOB.getPdfFile());
                        pdfFilesList.add((String)attachmentDOB.getFileName());
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
                   
                     // buffer = concatPDF(pdfFilesList);GOVIND COMMENTED 
                  //@@WPBN issue-61289  
                  
    //@@ Commented By Subrahmanyam for enhancement 146460           
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
      //@@ Ended by subrahmanyam for the enhancement 146460
                //f.delete();
                //baos.close();
                
                String[]  contactPersons      =    masterDOB.getCustContactNames();
                String    contactName         =    "";
                
                
                
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
                
                subject.append((headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():"")).append(" to ").append((headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""));
                subject.append(" Quote Reference ");
                if(finalDOB.getUpdatedReportDOB()!=null)
                  subject.append(request.getAttribute("quoteId")).append(" Replacing ").append(finalDOB.getUpdatedReportDOB().getQuoteId());
                else
                  subject.append(masterDOB.getQuoteId()!=null?masterDOB.getQuoteId()+"":request.getAttribute("quoteId"));
               //Commented By Kishore Podili For Multiple Zone Codes: 236286
               //if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
              // {
                 if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                    || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                      
                        doGenerateCartagePDF(filename,finalDOB,request,response);
               //}
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
                               if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                                  || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                                      sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                                else
                                  sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                             }
                              else
                                sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getCustomerContactsEmailIds()[i],subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                        //  }
                          //@@Added by Kameswari for the WPBN issue-61289
                          }
                        }
                              if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
                              {
                                 if(masterDOB.getShipperZipCode()==null || masterDOB.getConsigneeZipCode()==null)
                             {
                               if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                                  || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                   
                                  sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getSalesPersonEmail(),subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                             else
                                   sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getSalesPersonEmail(),subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                               }
                             else
                                 sendMail(finalDOB.getMasterDOB().getUserEmailId(),masterDOB.getSalesPersonEmail(),subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                       
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
                         if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                            || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
                                sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf,Annexure"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                         else
                          sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
                       }
                      else
                        sendMail(finalDOB.getMasterDOB().getUserEmailId(),to_emailIds,subject.toString(),body,"Quote"+file_tsmp+".pdf",finalDOB.getAttachmentDOBList());
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
                            if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                              || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
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
                          if((finalDOB.getPickZoneZipMap()!=null && finalDOB.getPickZoneZipMap().size()>0)
                            || (finalDOB.getDeliveryZoneZipMap()!=null && finalDOB.getDeliveryZoneZipMap().size()>0))
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
            HttpSession      session                    = null;
            
            // Added by Kishore Podili For Charge Basis in the CartagePDF
            ArrayList         pickupChargeBasisList        = null;
            ArrayList         delChargeBasisList           = null;
            int				  pickupChargeBasisSize		   = 0;
            int         	  delChargeBasisSize           = 0;
            
            
            pickupChargeBasisList        =  finalDOB.getPickupChargeBasisList();  
            delChargeBasisList           =  finalDOB.getDelChargeBasisList(); 
            
            if(pickupChargeBasisList!=null)
            	pickupChargeBasisSize  = pickupChargeBasisList.size();
            if(delChargeBasisList!=null)
                delChargeBasisSize     = delChargeBasisList.size();
            
            //End Of Kishore Podili For Charge Basis in the CartagePDF
            
            pickUpQuoteCartageRates   =  finalDOB.getPickUpCartageRatesList();
          
            deliveryQuoteCartageRates =  finalDOB.getDeliveryCartageRatesList(); 
            pickupWeightBreaks        =  finalDOB.getPickupWeightBreaks();
           // ArrayList pickupWeightBreaksFOr = finalDOB.getOriginChargesList();
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
            
            String  chargeRate =null;
            
              
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
            partZone.setAutoFillEmptyCells(true);
            partZone.setWidth(100);
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
          
           
          
              
              //Added By Kishore For the ChargeBasis in the Annexure PDF on 06-Jun-11
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
              
              for(int i=0;i<pickupChargeBasisSize;i++)////commented by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
              {
                chk = new Chunk(toTitleCase((String)pickupChargeBasisList.get(i)),FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
              
                cellZone = new Cell(chk);              
                //cellZone.setNoWrap(true);
                cellZone.setLeading(8.0f);
                cellZone.setHeader(true);
                //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                partZone.addCell(cellZone);
              }
             ////added by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
          /*    for(int J=0;J<originChargesSize;J++)
              {
                if(originIndices[J] !=-1)
                {
                chargesDOB				    = (QuoteCharges)originCharges.get(originIndices[J]);
                 logger.info("Origin Charges doPDFGeneration::"+J+":"+chargesDOB); // newly added                  
                originChargeInfo		  = chargesDOB.getChargeInfoList();
                originChargesInfoSize	= originChargeInfo.size();
                int m =0;
            String breakPoint = null;
                for(int k=0;k<originChargesInfoSize;k++)
                {
                  chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
                  String chargeBasis = (String)(chargeInfo.getBasis());
                 // chargeBasis  = df.format(Double.parseDouble(chargeBasis));
                  chk = new Chunk(chargeBasis,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                  cellZone = new Cell(chk);              
                  //cellZone.setNoWrap(true);
                  cellZone.setLeading(8.0f);
                  //cellZone.setHeader(true);
                //  cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                  partZone.addCell(cellZone);
                  
                }
                }
              }//ended by Brahmaiah .R */
            //Added By Kishore For the ChargeBasis in the Annexure PDF on 06-Jun-11 
              
            //}
            partZone.endHeaders();
            int pikupQuoteCartRatSize	=	pickUpQuoteCartageRates.size();
            for(int i=0;i<pikupQuoteCartRatSize;i++)
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
             //-------------------------------------------Commented  by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
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
            ////added by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
             /* for(int J=0;J<originChargesSize;J++)
            {
              if(originIndices[J] !=-1)
              {
              chargesDOB				    = (QuoteCharges)originCharges.get(originIndices[J]);
               logger.info("Origin Charges doPDFGeneration::"+J+":"+chargesDOB); // newly added                  
              originChargeInfo		  = chargesDOB.getChargeInfoList();
              originChargesInfoSize	= originChargeInfo.size();
              int m =0;
          String breakPoint = null;
              for(int k=0;k<originChargesInfoSize;k++)
              {
                chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
                chargeRate =  ((Double)chargeInfo.getSellRate()).toString();
                chargeRate  = df.format(Double.parseDouble(chargeRate));
                chk = new Chunk(chargeRate,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);              
                //cellZone.setNoWrap(true);
                cellZone.setLeading(8.0f);
               // cellZone.setHeader(true);
                //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                partZone.addCell(cellZone);
              }
              }
            }
            }//ended by Brahmaiah.R */
            //System.out.println("After zone header------------------------------>");
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
            
            
            //Added By Kishore For the ChargeBasis in the Annexure PDF on 06-Jun-11
            
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
           ////commented  by Brahmaiah.R on 31/5/2012 for WPBN issue 304241 
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
            //added by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
        /*    for(int J=0;J<destChargesSize;J++)
            {
              if(destIndices[J] !=-1)
              {
              chargesDOB				    = (QuoteCharges)destCharges.get(destIndices[J]);
               logger.info("Dest Charges doPDFGeneration::"+J+":"+chargesDOB); // newly added                  
              ArrayList destChargeInfo = chargesDOB.getChargeInfoList();
              int destChargesInfoSize = destChargeInfo.size();
              int m =0;
          String breakPoint = null;
              for(int k=0;k<destChargesInfoSize;k++)
              {
                chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);
                String chargeDestBasis = (String)chargeInfo.getBasis();
                //chargeRate  = df.format(Double.parseDouble(chargeRate));
                chk = new Chunk(chargeDestBasis,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);              
                //cellZone.setNoWrap(true);
                cellZone.setLeading(8.0f);
               // cellZone.setHeader(true);
                //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                partZone.addCell(cellZone);
              }
              }
            }
            }//ended by Brahmaiah.R  */
             //End of Kishore For the ChargeBasis in the Annexure PDF on 06-Jun-11
            
            
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
              //cellZone.setNoWrap(true);            
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
            //commented  by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
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
            //added by Brahmaiah.R on 31/5/2012 for WPBN issue 304241
       /*     for(int J=0;J<destChargesSize;J++)
            {
              if(destIndices[J] !=-1)
              {
              chargesDOB				    = (QuoteCharges)destCharges.get(destIndices[J]);
               logger.info("Dest Charges doPDFGeneration::"+J+":"+chargesDOB); // newly added                  
              ArrayList destChargeInfo = chargesDOB.getChargeInfoList();
              int destChargesInfoSize = destChargeInfo.size();
              int m =0;
          String breakPoint = null;
              for(int k=0;k<destChargesInfoSize;k++)
              {
                chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);
                String percent = chargeInfo.isPercentValue()?"%":"";
                String chargeDestRate = round1(((Double)chargeInfo.getSellRate()),percent).toString();
                chargeDestRate  = df.format(Double.parseDouble(chargeDestRate));
                chk = new Chunk(chargeDestRate,FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
                cellZone = new Cell(chk);              
                //cellZone.setNoWrap(true);
                cellZone.setLeading(8.0f);
               // cellZone.setHeader(true);
                //cellZone.setBackgroundColor(Color.LIGHT_GRAY);
                partZone.addCell(cellZone);
              }
              }
            }*/
            document.add(partZone);
            }
            //ended by Brahmaiah.R 
            //System.out.println("After zone header------------------------------>");
            
           
            
       
            pickUpZoneZipMap  =  finalDOB.getPickZoneZipMap();
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
            //System.out.println("After zipCode header------------------------------>");
                 
                document.close();
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
               /* if("on".equalsIgnoreCase(request.getParameter("print")))
                {
               
                   request.getSession().setAttribute("CartageOuptutStream",fs);
                }*/
  //@@ Added by subrahmanyam for  146460                
       
                 if("on".equalsIgnoreCase(request.getParameter("print"))||"PDF".equalsIgnoreCase(request.getParameter("pdf")))
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
  
  
 /**
	 * This method helps in getting the charges and their description and the necessary information i.e the header to be displayed on the charges select screen
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doMasterInfo request
	 * @exception ServletException if the request for the doMasterInfo could not be handled.
	 */
  private QuoteFinalDOB doGetHeaderAndCharges(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String                  operation   = null;
    QuoteHeader             quoteHeader = null;
    QuoteFinalDOB           finalDOB    = null;
   QuoteMasterDOB           masterDOB    = null;
    QMSQuoteSessionHome     home        = null;
    QMSQuoteSession         remote      = null;
    try
    {
      //lookUpBean  = new LookUpBean();
     
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      operation   = request.getParameter("Operation");
       ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
       
      finalDOB    = (QuoteFinalDOB)request.getAttribute("finalDOB");
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
  
  /**
	 * This method is used to set the master info from the request to the QuoteMasterDOB
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
   * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @param loginbean an object which stores the login info
   * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doMasterInfo request
	 * @exception ServletException if the request for the doMasterInfo could not be handled.
	 */
  public QuoteFinalDOB setQuoteMasterDOB(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean) throws ServletException, IOException
  {
    QuoteMasterDOB            masterDOB           = null;
    QuoteFinalDOB             finalDOB            = null;
    ESupplyDateUtility        eSupplyDateUtility  = new ESupplyDateUtility();
    String                    operation           = request.getParameter("Operation");
    String                    accessLevel         = "";
    ArrayList                 legDetails          = null;
    QuoteFreightLegSellRates  legDOB              = null;
    long                      quoteId             = 0;
    HttpSession  session       =  null;
    HashMap attentionLOV = null; // @@ added by phani sekhar for wpbn 167678
    java.sql.Timestamp created_date=null; //@@Added by kiran.v
		try
		{
      if("View".equalsIgnoreCase(operation))
        finalDOB = (QuoteFinalDOB)request.getSession().getAttribute("viewFinalDOB");
      else
        finalDOB = (QuoteFinalDOB)request.getSession().getAttribute("finalDOB");
      if(finalDOB!=null)
      created_date=finalDOB.getMasterDOB().getCreatedDate();//@@Added by kiran.v
			if(finalDOB==null)
      {
        finalDOB  = new QuoteFinalDOB();
				masterDOB = new QuoteMasterDOB();
      }
      else
      {
        masterDOB = new QuoteMasterDOB();
      }
      
      if(finalDOB.getMasterDOB()!=null)
      {
          masterDOB.setQuoteId(finalDOB.getMasterDOB().getQuoteId());
          masterDOB.setVersionNo(finalDOB.getMasterDOB().getVersionNo());
          masterDOB.setUniqueId(finalDOB.getMasterDOB().getUniqueId());
      }
      /*else if(finalDOB.getMasterDOB()!=null)
      {
        masterDOB = finalDOB.getMasterDOB();
      }
      else
      {
        masterDOB = new QuoteMasterDOB();
      }*/      
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
      
      // Added By Kishore For Weight Break in Single Quote  
      if(request.getParameter("WeightBreak")!=null && request.getParameter("WeightBreak").trim().length()!=0)
    	  masterDOB.setWeightBreak(request.getParameter("WeightBreak"));
      
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
      //@@Modified by kiran.v on 05/08/2011 for Wpbn Issue-	256087
        String effdate1=request.getParameter("effDate");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
		 Date date = new Date();
		 String d1=dateFormat.format(date);  
		 Timestamp date1 = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),d1);
		 if(!"View".equalsIgnoreCase(operation)){
			if(date1.before(effDate))//@@ Modified by govind for the issue 270694
		masterDOB.setEffDate(effDate);
        else
        masterDOB.setEffDate(date1);
		 }
		 else
		masterDOB.setEffDate(effDate);
        //@@Ended by kiran.v
      }
      if(request.getParameter("modifiedDate")!=null && request.getParameter("modifiedDate").trim().length()!=0)
      {
        Timestamp modifiedDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("modifiedDate"));
        masterDOB.setModifiedDate(modifiedDate);
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
          else if("Modify".equalsIgnoreCase(operation)) { //@@Modified by kiran.v for back button in modify
      //  masterDOB.setCreatedDate(new java.sql.Timestamp((new java.util.Date()).getTime()));
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
    	  Timestamp custDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),request.getParameter("custDate"));
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
      if(request.getParameter("cargoAcceptance")!=null && request.getParameter("cargoAcceptance").trim().length()!=0)
        masterDOB.setCargoAcceptance(request.getParameter("cargoAcceptance"));
      
      if(request.getParameter("cargoAccPlace")!=null && request.getParameter("cargoAccPlace").trim().length()!=0)
        masterDOB.setCargoAccPlace(request.getParameter("cargoAccPlace"));
      
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
 
      if(request.getParameter("incoTerms")!=null && request.getParameter("incoTerms").trim().length()!=0)
        masterDOB.setIncoTermsId(request.getParameter("incoTerms"));
      
      if(request.getParameter("quotingStation")!=null && request.getParameter("quotingStation").trim().length()!=0)
        masterDOB.setQuotingStation(request.getParameter("quotingStation"));
      
      if(request.getParameter("originLoc")!=null && request.getParameter("originLoc").trim().length()!=0)
        masterDOB.setOriginLocation(request.getParameter("originLoc"));
      
      if(request.getParameter("shipperZipCode")!=null && request.getParameter("shipperZipCode").trim().length()!=0)
        masterDOB.setShipperZipCode(request.getParameter("shipperZipCode"));
  
      if(request.getParameter("shipperZone")!=null && request.getParameter("shipperZone").trim().length()!=0)
        masterDOB.setShipperZones(request.getParameter("shipperZone"));
      
      if(request.getParameter("consigneeZone")!=null && request.getParameter("consigneeZone").trim().length()!=0)
        masterDOB.setConsigneeZones(request.getParameter("consigneeZone"));
      
      if(request.getParameter("originPort")!=null && request.getParameter("originPort").trim().length()!=0)
        masterDOB.setOriginPort(request.getParameter("originPort"));
      
      if(request.getParameter("overLengthCargoNotes")!=null  && request.getParameter("overLengthCargoNotes").trim().length()!=0)
        masterDOB.setOverLengthCargoNotes(request.getParameter("overLengthCargoNotes"));
      
      if(request.getParameter("routeId")!=null && request.getParameter("routeId").trim().length()!=0)
        masterDOB.setRouteId(request.getParameter("routeId"));
      
      if(request.getParameter("destLoc")!=null && request.getParameter("destLoc").trim().length()!=0)
        masterDOB.setDestLocation(request.getParameter("destLoc"));
      
      if(request.getParameter("consigneeZipCode")!=null && request.getParameter("consigneeZipCode").trim().length()!=0)
        masterDOB.setConsigneeZipCode(request.getParameter("consigneeZipCode"));
      
      if(request.getParameter("destPort")!=null && request.getParameter("destPort").trim().length()!=0)
        masterDOB.setDestPort(request.getParameter("destPort"));
       if(request.getParameter("shipperMode")!=null && request.getParameter("shipperMode").trim().length()!=0)
        masterDOB.setShipperMode(request.getParameter("shipperMode"));
//@@ Added & Commented by subrahmanyam for the pickupCharges Missing issues  
       /*
             if(request.getParameter("shipperConsoleType")!=null && request.getParameter("shipperConsoleType").trim().length()!=0 )
        masterDOB.setShipperConsoleType(request.getParameter("shipperConsoleType"));
 
        */
       if("1".equalsIgnoreCase(masterDOB.getShipperMode()) || "".equalsIgnoreCase(masterDOB.getShipperMode()) || masterDOB.getShipperMode()==null)
    	   masterDOB.setShipperConsoleType(null);
       else{
      if(request.getParameter("shipperConsoleType")!=null && request.getParameter("shipperConsoleType").trim().length()!=0 )
        masterDOB.setShipperConsoleType(request.getParameter("shipperConsoleType"));
       }
 //@@ Ended by subrahmanyam for the pickupCharges Missing issues       
      if(request.getParameter("consigneeMode")!=null && request.getParameter("consigneeMode").trim().length()!=0)
        masterDOB.setConsigneeMode(request.getParameter("consigneeMode"));
    //@@ Added & Commented by subrahmanyam for the pickupCharges Missing issues  
      /*
 			      if(request.getParameter("consigneeConsoleType")!=null && request.getParameter("consigneeConsoleType").length()!=0)
        masterDOB.setConsigneeConsoleType(request.getParameter("consigneeConsoleType"));

       */      
      if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()) || "".equalsIgnoreCase(masterDOB.getConsigneeMode()) || masterDOB.getConsigneeMode()==null  )
    	  masterDOB.setConsigneeConsoleType(null);
      else
      {
      if(request.getParameter("consigneeConsoleType")!=null && request.getParameter("consigneeConsoleType").length()!=0)
        masterDOB.setConsigneeConsoleType(request.getParameter("consigneeConsoleType"));
      }
//@@ Ended by subrahmanyam for the pickupCharges Missing issues            
      if("Add".equals(operation))
          masterDOB.setModifiedDate(new java.sql.Timestamp((new java.util.Date()).getTime()));
        
      int legSize = 0;
      //@@Since a route-plan is specific to a Quote, the route details will not be copied from the previous Quote(For Operation Copy).  
      if("Add".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
      {
        legDetails  = new ArrayList();
        legSize     = 1;
      }
      else
      {
        legDetails  = finalDOB.getLegDetails();
        legSize     = legDetails.size();
      }
      
      Hashtable spotRateDetails  = null;
      ArrayList weightBreakSlabs = null;
      double[] rateDetail = null;
      
      String[] weightBreaks = null; 
      String[] rates        = null;
      String[] surchargeBreaks = null; 
      String[] surchargeRates  = null;
       String[] airBreaks = null; 
      String[] airRates  = null;
       String[] seaBreaks = null; 
      String[] seaRates  = null;
       String[] listBreaks = null; 
      String[] listRates  = null;
       String[] truckBreaks = null; 
      String[] truckRates  = null;
      String[] bafRates  = null;
      String[] cafRates  = null;
      String[] cssRates  = null;
      String[] pssRates  = null;
    
      
      for(int j=0;j<legSize;j++)
      {
        if("Add".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
        {
          legDOB  = new QuoteFreightLegSellRates();
          
          //@@The freight Rates are always to be fetched between Ports only.
          legDOB.setOrigin(masterDOB.getOriginPort());
          legDOB.setDestination(masterDOB.getDestPort());
        
          if(masterDOB.getServiceLevelId()!=null)
            legDOB.setServiceLevel(masterDOB.getServiceLevelId());
          legDOB.setShipmentMode(masterDOB.getShipmentMode());
          legDetails.add(j,legDOB);
        }
        else
        {
          legDOB  = (QuoteFreightLegSellRates)legDetails.get(j);
         }
        
        if(request.getParameter("spotRateFlag"+j)!=null && request.getParameter("spotRateFlag"+j).trim().length()!=0)
        {
          if("Y".equalsIgnoreCase(request.getParameter("spotRateFlag"+j)))
          {
            spotRateDetails  = new Hashtable();
            weightBreakSlabs = new ArrayList();
             surchargeBreaks = null; 
   surchargeRates  = null;
       airBreaks = null; 
     airRates  = null;
     seaBreaks = null; 
     seaRates  = null;
     listBreaks = null; 
      listRates  = null;
       truckBreaks = null; 
     truckRates  = null;
    bafRates  = null;
      cafRates  = null;
   cssRates  = null;
      pssRates  = null;
            legDOB.setSpotRatesFlag(true);
            legDOB.setSpotRatesType(request.getParameter("spotRateType"+j));
            
            
            if("Flat".equalsIgnoreCase(request.getParameter("spotRateType"+j)))
            {
              weightBreaks = request.getParameterValues("flatWeightBreak"+j);
              
              rates = request.getParameterValues("flatRate"+j);
            }
            else if("Slab".equalsIgnoreCase(request.getParameter("spotRateType"+j)))
            {
              weightBreaks = request.getParameterValues("slabWeightBreak"+j);
              rates = request.getParameterValues("slabRate"+j);
            }
            else if("List".equalsIgnoreCase(request.getParameter("spotRateType"+j)))
            
            {
              weightBreaks = request.getParameterValues("listWeightBreak"+j);
              rates = request.getParameterValues("listRate"+j);
            }
             
           
             if(legDOB.getShipmentMode()==1)
             {
              
                airBreaks = request.getParameterValues("airWeightBreak"+j);
                 airRates = request.getParameterValues("airRate"+j);
             }
             else  if(legDOB.getShipmentMode()==2)  
             {
                    if("List".equalsIgnoreCase(request.getParameter("spotRateType"+j)))
                {
                 surchargeBreaks = request.getParameterValues("listWeightBreak"+j);
                     cafRates = request.getParameterValues("cafRate"+j);
                     bafRates = request.getParameterValues("bafRate"+j);
                     cssRates = request.getParameterValues("cssRate"+j);
                     pssRates = request.getParameterValues("pssRate"+j);
     
                }
                 
                 else
                 {
                   seaBreaks = request.getParameterValues("seaWeightBreak"+j);
                   seaRates = request.getParameterValues("seaRate"+j);
                 }
             }
             else
             {
                 truckBreaks = request.getParameterValues("truckWeightBreak"+j);
                 truckRates = request.getParameterValues("truckRate"+j);
                             
             }
          
            if("List".equalsIgnoreCase(request.getParameter("spotRateType"+j)))
            {
            	int rTLen	=	rates.length;
            for(int i=0;i<rTLen;i++)
              {

                  if(weightBreaks[i]!=null && rates[i]!=null && weightBreaks[i].trim().length()!=0 && rates[i].trim().length()!=0)
                  {
                    rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    rateDetail[0] = 0;//upper bound
                    rateDetail[1] = 0;//lower bound
                    rateDetail[2] = Double.parseDouble(rates[i]);//rate
                    spotRateDetails.put(weightBreaks[i],rateDetail);
                    weightBreakSlabs.add(weightBreaks[i]);
                  }
              }
            }
           else
            {
        	   int wtBreakLen	=	weightBreaks.length;
                for(int i=0;i<wtBreakLen;i++)
                {
                   if(weightBreaks[i]!=null && rates[i]!=null && weightBreaks[i].trim().length()!=0 && rates[i].trim().length()!=0)
                  {
                     rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    if("MIN".equalsIgnoreCase(weightBreaks[i]) || "FLAT".equalsIgnoreCase(weightBreaks[i]))
                    //@@Modified by Kameswari for Surcharge Enhancement
                     {
                       rateDetail[0] = 0;//upper bound
                      rateDetail[1] = 0;//lower bound
                      rateDetail[2] = Double.parseDouble(rates[i]);//rate 
                    }
                     else
                    {
                      if(i!=(weightBreaks.length-1) && weightBreaks[i+1]!=null && weightBreaks[i+1].trim().length()!=0)
                        rateDetail[0] = Double.parseDouble(weightBreaks[i+1]);//upper bound
                      else
                        rateDetail[0] = 100000;//since this is the last weight break, upper bound is set to 1,00,000
                      
                      if(Double.parseDouble(weightBreaks[i])<0)
                        rateDetail[1] = 0;//since the weightBreaks[i] is -ve lower bound is set as zero
                      else
                        rateDetail[1] = Double.parseDouble(weightBreaks[i]);//lower bound
                      
                      rateDetail[2] = Double.parseDouble(rates[i]);//rate
                    }
                      spotRateDetails.put(weightBreaks[i],rateDetail);
                      weightBreakSlabs.add(weightBreaks[i]);
                  }
                }
            }
         /* if(surchargeBreaks!=null)
          {
            for(int i=0;i<surchargeBreaks.length;i++)
              {
                   if(surchargeRates!=null)
                   {
                  if(surchargeBreaks[i]!=null && Double.parseDouble(surchargeRates[i])!=0.0&& surchargeBreaks[i].trim().length()!=0 && surchargeRates[i].trim().length()!=0)
                  {
                    rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    rateDetail[0] = 0;//upper bound
                    rateDetail[1] = 0;//lower bound
                    rateDetail[2] = Double.parseDouble(surchargeRates[i]);//rate
                    spotRateDetails.put(surchargeBreaks[i],rateDetail);
                    weightBreakSlabs.add(surchargeBreaks[i]);
                  }
                 }
              }    */
              if(airBreaks!=null)
          {
            	  int airBrkLen	=	airBreaks.length;
            for(int i=0;i<airBrkLen;i++)
              {
                   if(airRates!=null)
                   {
                  if(airBreaks[i]!=null && Double.parseDouble(airRates[i])!=0.0&& airBreaks[i].trim().length()!=0 && airRates[i].trim().length()!=0)
                  {
                    rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    rateDetail[0] = 0;//upper bound
                    rateDetail[1] = 0;//lower bound
                    rateDetail[2] = Double.parseDouble(airRates[i]);//rate
                spotRateDetails.put(airBreaks[i],rateDetail);
                    weightBreakSlabs.add(airBreaks[i]);
                  }
                 }
              }  
          }
              if(seaBreaks!=null)
          {
            	  int seaBrkLen	=	seaBreaks.length;
            for(int i=0;i<seaBrkLen;i++)
              {
                   if(seaRates!=null)
                   {
                  if(seaBreaks[i]!=null && Double.parseDouble(seaRates[i])!=0.0&& seaBreaks[i].trim().length()!=0 && seaRates[i].trim().length()!=0)
                  {
                    rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    rateDetail[0] = 0;//upper bound
                    rateDetail[1] = 0;//lower bound
                    rateDetail[2] = Double.parseDouble(seaRates[i]);//rate
                    spotRateDetails.put(seaBreaks[i],rateDetail);
                    weightBreakSlabs.add(seaBreaks[i]);
                  }
                 }
              } 
          }
              if(truckBreaks!=null)
          {
            	  int trkBrkLen	=	truckBreaks.length;
            for(int i=0;i<trkBrkLen;i++)
              {
                   if(truckRates!=null)
                   {
                  if(truckBreaks[i]!=null && Double.parseDouble(truckRates[i])!=0.0&& truckBreaks[i].trim().length()!=0 && truckRates[i].trim().length()!=0)
                  {
                    rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    rateDetail[0] = 0;//upper bound
                    rateDetail[1] = 0;//lower bound
                    rateDetail[2] = Double.parseDouble(truckRates[i]);//rate
                    spotRateDetails.put(truckBreaks[i],rateDetail);
                    weightBreakSlabs.add(truckBreaks[i]);
                  }
                 }
              } 
          }
              if(surchargeBreaks!=null)
             {
            	  int surChargBrkLen	=	surchargeBreaks.length;
                  for(int i=0;i<surChargBrkLen;i++)
                 {
                    if(cafRates!=null)
                   {
                  if(surchargeBreaks[i]!=null &&Double.parseDouble(cafRates[i])!=0.0 && surchargeBreaks[i].trim().length()!=0 && cafRates[i].trim().length()!=0)
                  {
                    rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                    rateDetail[0] = 0;//upper bound
                    rateDetail[1] = 0;//lower bound
                    rateDetail[2] = Double.parseDouble(cafRates[i]);//rate
                    spotRateDetails.put(surchargeBreaks[i]+"caf",rateDetail);
                    weightBreakSlabs.add(surchargeBreaks[i]+"caf");
                  }
                   }
                 }
                  for(int i=0;i<surChargBrkLen;i++)
                {
                   if(bafRates!=null)
                   {
                    if(surchargeBreaks[i]!=null && Double.parseDouble(bafRates[i])!=0.0 && surchargeBreaks[i].trim().length()!=0 && bafRates[i].trim().length()!=0)
                    {
                      rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                      rateDetail[0] = 0;//upper bound
                      rateDetail[1] = 0;//lower bound
                      rateDetail[2] = Double.parseDouble(bafRates[i]);//rate
                      spotRateDetails.put(surchargeBreaks[i]+"baf",rateDetail);
                      weightBreakSlabs.add(surchargeBreaks[i]+"baf");
                      }
                  }
                }
                  for(int i=0;i<surChargBrkLen;i++)
              {
                  if(cssRates!=null)
                   {
                    if(surchargeBreaks[i]!=null && Double.parseDouble(cssRates[i])!=0.0 && surchargeBreaks[i].trim().length()!=0 && cssRates[i].trim().length()!=0)
                    {
                      rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                      rateDetail[0] = 0;//upper bound
                      rateDetail[1] = 0;//lower bound
                      rateDetail[2] = Double.parseDouble(cssRates[i]);//rate
                      spotRateDetails.put(surchargeBreaks[i]+"csf",rateDetail);
                      weightBreakSlabs.add(surchargeBreaks[i]+"csf");
                    }
                   }
              }
               for(int i=0;i<surChargBrkLen;i++)
              {
                   if(pssRates!=null)
                   {
                      if(surchargeBreaks[i]!=null && Double.parseDouble(pssRates[i])!=0.0 && surchargeBreaks[i].trim().length()!=0 && pssRates[i].trim().length()!=0)
                      {
                        rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
                        rateDetail[0] = 0;//upper bound
                        rateDetail[1] = 0;//lower bound
                        rateDetail[2] = Double.parseDouble(pssRates[i]);//rate
                        spotRateDetails.put(surchargeBreaks[i]+"pss",rateDetail);
                        weightBreakSlabs.add(surchargeBreaks[i]+"pss");
                      }
                   }
              
              }
          }
            if(request.getParameter("serviceLevel"+j)!=null && request.getParameter("serviceLevel"+j).trim().length()!=0)
                legDOB.setServiceLevel(request.getParameter("serviceLevel"+j));
              
             if(request.getParameter("uom"+j)!=null && request.getParameter("uom"+j).trim().length()!=0)
                legDOB.setUom(request.getParameter("uom"+j));
            
            if(request.getParameter("densityRatio"+j)!=null && request.getParameter("densityRatio"+j).trim().length()!=0)
                legDOB.setDensityRatio(request.getParameter("densityRatio"+j));
            
            if(request.getParameter("currencyId"+j)!=null && request.getParameter("currencyId"+j).trim().length()!=0)
                legDOB.setCurrency(request.getParameter("currencyId"+j));
                
            if(spotRateDetails!=null)//only if some spot rates are there assign it to the Master DOB
              legDOB.setSpotRateDetails(spotRateDetails);
              
            if(weightBreakSlabs!=null)
            {
             
             
              legDOB.setWeightBreaks(weightBreakSlabs);
            }
          }
       
          else if("N".equalsIgnoreCase(request.getParameter("spotRateFlag"+j)))
            legDOB.setSpotRatesFlag(false);
        }
        else
        {
          legDOB.setSpotRatesFlag(false);
        }
        legDetails.remove(j);
        legDetails.add(j,legDOB);
      }
      
      if(request.getParameterValues("chargeGroupId")!=null && request.getParameterValues("chargeGroupId").length!=0)
          masterDOB.setChargeGroupIds(request.getParameterValues("chargeGroupId"));
        
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
        QMSQuoteSessionHome       home              = null;
        QMSQuoteSession           remote            = null;
   
        //lookUpBean  = new LookUpBean();
        home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
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
      finalDOB.setMasterDOB(masterDOB);
      finalDOB.setLegDetails(legDetails);
    }
    catch(Exception ex)
		{
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [setQuoteMasterDOB()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [setQuoteMasterDOB()] -> "+ex.toString());
		}
    return finalDOB;
  }
  
  private QuoteFinalDOB getMarginLimit(QuoteFinalDOB finalDOB) throws ServletException
  {
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
    try
    {
      //lookUpBean  = new LookUpBean();
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
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
  
  private QuoteFinalDOB doMarginTest(HttpServletRequest request,HttpServletResponse response,QuoteFinalDOB finalDOB) throws ServletException,IOException
  {
    
    QuoteFreightLegSellRates legDOB            = null;
    QuoteCharges             chargesDOB        = null;
    QuoteChargeInfo         chargeInfo         = null;
    
    String[]                originChargeIndices= null;
    String[]                originChargeIndices1= null;//added for duplicate charges    
    String[]                destChargeIndices  = null;
    String[]                destChargeIndices1  = null;//added for duplicate charges
    String[]                frtChargeIndices   = null;
    String[]                marginType         = null;
    String[]                margin             = null;
    String[]                sellRate          = null;
    
    String[]                discountType       = null;
    String[]                discount           = null;
    
    int[]                   originIndices      = null;
    int[]                   frtIndices         = null;
    int[]                   destIndices        = null;
    
    int                     noOfLegs           =  0;
    ArrayList               legDetails         = null;
    ArrayList               originCharges      = null;
    ArrayList               originChargeInfo   = null;
    ArrayList               destCharges        = null;
    ArrayList               destChargeInfo     = null;
    ArrayList               frtCharges        = null;
    ArrayList               frtChargeInfo     = null;
    int                     frtsize           = 0;
    int                     originChargesSize  =	0;
    int                     destChargesInfoSize=  0;
    int                     frtChargesInfoSize =  0;
   
    double                  originChargesMargin   = 0;
    double                  originChargesDiscount = 0;
    
    double                  destChargesMargin     = 0;
    double                  destChargesDiscount   = 0;
 //@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09    
    String[]            originChargeSelectedFlag = null;
    String[]            destChargeSelectedFlag = null;
//@@ Ended by subrahmanyam for the Enhancement 154381 on 14/02/09    
//added for duplicate charges
    int oCcount = 0;
    int dCcount = 0;
    ArrayList               originChargesTemp     = new ArrayList();
    ArrayList               destChargesTemp       = new ArrayList();
   

// ended for duplicate charges

    try
    {       
    	
    		
    	
      originCharges  = finalDOB.getOriginChargesList();
      destCharges    = finalDOB.getDestChargesList(); 
      
      if(originCharges!=null)
					originChargesSize		= originCharges.size();
			else
					originChargesSize		= 0;
       
        if(finalDOB!=null)
          legDetails  = finalDOB.getLegDetails();
        else
          legDetails  = new ArrayList();
        
        noOfLegs    = legDetails.size();
        
//commented for duplicate charges
      /*  originChargeIndices = request.getParameterValues("originChargeIndices");
        destChargeIndices   = request.getParameterValues("destChargeIndices");
        */
//added for duplicate charges for pbn id: 186507
        originChargeIndices1 = request.getParameterValues("originChargeIndices");
        destChargeIndices1   = request.getParameterValues("destChargeIndices");
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
        //@@ Commented by subrahmanyam for the notes issue 194328.
        /*
         finalDOB.setInternalNotes(request.getParameterValues("internalNotes"));
         finalDOB.setExternalNotes(request.getParameterValues("externalNotes"));
         */
        //@@Added by subrahmanyam for the notes issue 194328.
        if(request.getParameterValues("internalNotes") != null && !"".equals(request.getParameterValues("internalNotes")))
        finalDOB.setInternalNotes(removeEnterForNotes(request.getParameterValues("internalNotes")));
        else//esle condition added by govind for the issue not getting th einternal and external notes in quote escalated view
        finalDOB.setInternalNotes(removeEnterForNotes(finalDOB.getInternalNotes()));	
        if(request.getParameterValues("externalNotes") != null && !"".equals(request.getParameterValues("externalNotes")))
        finalDOB.setExternalNotes(removeEnterForNotes(request.getParameterValues("externalNotes")));
        else//esle condition added by govind for the issue not getting th einternal and external notes in quote escalated view
        finalDOB.setInternalNotes(removeEnterForNotes(finalDOB.getExternalNotes()));	
        //@@Ended for the notes Issue 194328
        originChargeSelectedFlag = request.getParameterValues("originChargeSelectedFlag");//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09    
         destChargeSelectedFlag = request.getParameterValues("destChargeSelectedFlag");//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09    
         
      
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
            chargesDOB				      = (QuoteCharges)originCharges.get(originIndices[i]);
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
            if(request.getParameterValues("originMarginType"+originIndices[i])!=null)
            {
              marginType  =  request.getParameterValues("originMarginType"+originIndices[i]);
              margin      =  request.getParameterValues("originMargin"+originIndices[i]);
              sellRate    =  request.getParameterValues("originSellRate"+originIndices[i]);
              
              if(margin!=null)
              {
              chargesDOB.setMarginDiscountFlag("M");
              int marginLen	=	margin.length;
              
              /*ArrayList pqcl=finalDOB.getPickupChargeBasisList();
              int basisSize=pqcl.size();
              for(i=0;i<basisSize;i++)
              {
            	  String basislist =(String)pqcl.get(i);
            	  pickupChargeBasisList.add(basislist);
              }
              finalDOB.setPickupChargeBasisList(pickupChargeBasisList);
              ArrayList pqwb=finalDOB.getPickupWeightBreaks();
              int pickuSize = pqwb.size();
              int j=0;
              while(j<pickuSize)
              {*/
              for(int k=0;k<marginLen;k++)
              {
                chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
             /*  if("Add".equalsIgnoreCase(request.getParameter("Operation"))||"Modify".equalsIgnoreCase(request.getParameter("Operation")))
            		   {
                ArrayList pqcr = finalDOB.getPickUpCartageRatesList();
               
                
               // Object PickupChargeBasis = pqcl.get(i);
                pickQuoteCartageRates = new QuoteCartageRates();
              logger.info("WeightBreak@@"+pqwb.get(j)+"SellRate@@"+sellRate[k]);
                charge.put((String)pqwb.get(j),sellRate[k]);
                pickQuoteCartageRates.setRates(charge);
                pickUpQuoteCartageRates
				.add(pickQuoteCartageRates);
                pickQuoteCartageRates = (QuoteCartageRates) pickUpZoneCode.get(
        				pickQuoteCartageRates.getZone()
        						+ pickQuoteCartageRates.getCartageId());
                pickUpZoneCode.put(pickQuoteCartageRates.getZone()
						+ pickQuoteCartageRates.getCartageId(),
						pickQuoteCartageRates);
                */
               
             /*if("Add".equalsIgnoreCase(request.getParameter("Operation"))||"Modify".equalsIgnoreCase(request.getParameter("Operation")))
                {
              
                
                	for(int x=0;x<pickupWeightBreaksSize;x++)
                	{
                	//charge = pickQuoteCartageRates.getRates();
              	  charge.put((String)pickupWeightBreaks.get(x),sellRate[k]);
              	pickQuoteCartageRates.setRates(charge);
              	
                	
              	
                
                
               
                pickUpQuoteCartageRates.add(pickQuoteCartageRates.getZone()+pickQuoteCartageRates.getCartageId());
                pickUpQuoteCartageRates.add(pickQuoteCartageRates);
                */
                
               
                chargeInfo.setMarginType(marginType[k]);
                chargeInfo.setMargin(Double.parseDouble(margin[k]));
                chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                if("P".equalsIgnoreCase(marginType[k]))
                {
//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10     & 205785 on 17-May-10           	
                   if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
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
                	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10     & 205785 on 17-May-10           	
                    if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
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
               /*j++;
            		   }
              
                }*/
              }
            }
              
            
        //  } //newly commented
            //if(!"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))//@@If discount is given by the user
              if("SC".equals(chargesDOB.getSellBuyFlag())||"S".equals(chargesDOB.getSellBuyFlag()))//@@Modified by Kameswari for the enhancement
            {
              if(request.getParameter("originDiscountType"+originIndices[i])!=null)
              {
                discountType  =  request.getParameterValues("originDiscountType"+originIndices[i]);
                discount      =  request.getParameterValues("originDiscount"+originIndices[i]);
                sellRate      =  request.getParameterValues("originSellRate"+originIndices[i]);
                 if(discount!=null)//@@Added by Kameswari for the WPBN issue-139966
                {
                chargesDOB.setMarginDiscountFlag("D");
                int disLen	=	discount.length;
                 for(int k=0;k<disLen;k++)
                {
                  chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
                  chargeInfo.setDiscountType(discountType[k]);
                 //double d = Double.parseDouble(discount[k]);
                  chargeInfo.setDiscount(Double.parseDouble(discount[k]));
                  chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                  
                  if("P".equalsIgnoreCase(discountType[k]))
                  { 
//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10   & 205785 on 17-May-10   	  
                	 /* if("Y".equalsIgnoreCase(chargeInfo.getMartinTeStFlag()) &&"COPY".equalsIgnoreCase(request.getParameter("Operation")) 
                			  && "S".equalsIgnoreCase(finalDOB.getFlagsDOB().getSentFlag()) && "E".equalsIgnoreCase(finalDOB.getFlagsDOB().getInternalExternalFlag())
                			  && !"".equalsIgnoreCase(finalDOB.getEscalatedTo()))
                	  {
                		  if(Double.parseDouble(discount[k]) == chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(sellRate[k])== chargeInfo.getTieSellRateValue())
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
                	  }*/
                	  if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
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
                	  */
                  	  if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
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
          
           finalDOB.setOriginChargesList(originCharges);
          finalDOB.setSelectedOriginChargesListIndices(originIndices);
          finalDOB.setOriginChargesSelectedFlag(originChargeSelectedFlag);//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09  
        }
        else
        {
          finalDOB.setSelectedOriginChargesListIndices(new int[0]);
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
            chargesDOB				      =  (QuoteCharges)destCharges.get(destIndices[i]);
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
            
            if(request.getParameterValues("destMarginType"+destIndices[i])!=null)
            {
              marginType  =  request.getParameterValues("destMarginType"+destIndices[i]);
               margin      =  request.getParameterValues("destMargin"+destIndices[i]);
              sellRate    =  request.getParameterValues("destSellRate"+destIndices[i]);
            
              
              if(margin!=null)
              {
                chargesDOB.setMarginDiscountFlag("M");
                int marginLen	=	margin.length;
              for(int k=0;k<marginLen;k++)
              {
                chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);
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
              if(request.getParameter("destDiscountType"+destIndices[i])!=null)
              {
                discountType  =  request.getParameterValues("destDiscountType"+destIndices[i]);
                discount      =  request.getParameterValues("destDiscount"+destIndices[i]);
                sellRate      =  request.getParameterValues("destSellRate"+destIndices[i]);
              
                if(discount!=null)//@@Added by Kameswari for the WPBN issue-139966logge
                {
                  chargesDOB.setMarginDiscountFlag("D");
                  int disLen	=	discount.length;
                  for(int k=0;k<disLen;k++)
                {
                  chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);
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
          finalDOB.setDestChargesList(destCharges);
          finalDOB.setSelctedDestChargesListIndices(destIndices);
          finalDOB.setDestChargesSelectedFlag(destChargeSelectedFlag);//@@ Added by subrahmanyam for the Enhancement 154381 on 14/02/09    
         
        }
        else
        {
          finalDOB.setSelctedDestChargesListIndices(new int[0]);
        } 
        for(int i=0;i<noOfLegs;i++)
        {
          legDOB            = (QuoteFreightLegSellRates)legDetails.get(i);
          frtCharges        = legDOB.getFreightChargesList();
          frtsize           = frtCharges.size();
          frtChargeIndices  = request.getParameterValues("frtChargeIndices"+i);
          if(frtChargeIndices!=null)
          {
            if(frtsize>0)
        	  for(int cr=0;cr<frtsize;cr++)
            	((QuoteCharges)frtCharges.get(cr)).setSelectedFlag("N");
            
        	frtIndices   = new int[frtChargeIndices.length];
            int frtChargIndLen	=	frtChargeIndices.length;
            for(int j=0;j<frtChargIndLen;j++)
            {
            	
            
            	if(frtChargeIndices[j].trim().length()!=0)
                 frtIndices[j] =  Integer.parseInt(frtChargeIndices[j]);
            	 chargesDOB				      = (QuoteCharges)frtCharges.get(frtIndices[j]);
            	 chargesDOB.setSelectedFlag("Y");
              frtChargeInfo		        =  chargesDOB.getChargeInfoList();
              frtChargesInfoSize      =  frtChargeInfo.size();
              
              if(request.getParameterValues("frtMargin"+i+"Type"+frtIndices[j])!=null)
              {
                marginType  =  request.getParameterValues("frtMargin"+i+"Type"+frtIndices[j]);
                margin      =  request.getParameterValues("frtMargin"+i+frtIndices[j]);
                sellRate    =  request.getParameterValues("frtSellRate"+i+frtIndices[j]);
                //    chargesDOB.setMarginDiscountFlag("M");
                 if(margin!=null)
                {
                chargesDOB.setMarginDiscountFlag("M");
                int marginLen	=	margin.length;
                for(int k=0;k<marginLen;k++)
                {
                  chargeInfo = (QuoteChargeInfo)frtChargeInfo.get(k);
                  if("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))
                  {
                  chargeInfo.setMarginType(marginType[k]);
                  //Logger.info(FILE_NAME,"chargeInfo...margin type:"+chargeInfo.getMarginType());
                  chargeInfo.setMargin(Double.parseDouble(margin[k]));
                  chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                     
                  if("P".equalsIgnoreCase(marginType[k]))
                  {
                	  /*
                    //Logger.info(FILE_NAME,"legDOB.getMarginLimit()::"+legDOB.getMarginLimit());
                    //Logger.info(FILE_NAME,"margin[k]:"+margin[k]);
                    if(Double.parseDouble(margin[k]) < legDOB.getMarginLimit())
                      chargeInfo.setMarginTestFailed(true);
                    else
                      chargeInfo.setMarginTestFailed(false);
                      */
                	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10                	  
                	  if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                 			   ||"Modify".equalsIgnoreCase(request.getParameter("Operation"))) )
                	  {
                    		 if("Y".equalsIgnoreCase(request.getParameter("isEscalated"))) // Added by Gowtham.
                		  {
                			  if(Double.parseDouble(margin[k]) < legDOB.getMarginLimit())
                                  chargeInfo.setMarginTestFailed(true);
                                else
                                  chargeInfo.setMarginTestFailed(false);
                		  }else if("Modify".equalsIgnoreCase(request.getParameter("Operation")) && "REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()))
                    		 {
                                 if(Double.parseDouble(margin[k]) < legDOB.getMarginLimit())
                                     chargeInfo.setMarginTestFailed(true);
                                   else
                                     chargeInfo.setMarginTestFailed(false);

                    		 }
                    		 else{
                    			 
                		  if(Double.parseDouble(margin[k]) == chargeInfo.getTieMarginDiscountValue() 
                				  || (Double.parseDouble(margin[k]) > chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(margin[k])<= legDOB.getMarginLimit() ) )
                		  {
                			  chargeInfo.setMarginTestFailed(false);
                		  }
                		  else
                		  {
                              if(Double.parseDouble(margin[k]) < legDOB.getMarginLimit())
                                  chargeInfo.setMarginTestFailed(true);
                                else
                                  chargeInfo.setMarginTestFailed(false);
                     			  
                		  }
                    		 }
                	  }
                	  else
                	  {
                          if(Double.parseDouble(margin[k]) < legDOB.getMarginLimit())
                              chargeInfo.setMarginTestFailed(true);
                            else
                              chargeInfo.setMarginTestFailed(false);
                		  
                	  }
                	//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10                	  
                  }
                   else if("A".equalsIgnoreCase(marginType[k]))
                  {
                	   /*
                    if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < legDOB.getMarginLimit())
                        chargeInfo.setMarginTestFailed(true);
                    else
                        chargeInfo.setMarginTestFailed(false);
                        */
                	 //@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10                	   
                    	  if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                    			   ||"Modify".equalsIgnoreCase(request.getParameter("Operation"))) )
                    	{
                    		  if("Y".equalsIgnoreCase(request.getParameter("isEscalated"))) // Added by Gowtham for escalated quoteupdate issue.
                    		  {
                    			  if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < legDOB.getMarginLimit())
                                      chargeInfo.setMarginTestFailed(true);
                                  else
                                      chargeInfo.setMarginTestFailed(false);
                    		  }
                    		  else if("Modify".equalsIgnoreCase(request.getParameter("Operation")) && "REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()))
                    		  {
                                  if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < legDOB.getMarginLimit())
                                      chargeInfo.setMarginTestFailed(true);
                                  else
                                      chargeInfo.setMarginTestFailed(false);

                    		  }
                    		  else
                    		  {
                    		  if(Double.parseDouble(margin[k]) == chargeInfo.getTieMarginDiscountValue() 
                    				  || (Double.parseDouble(margin[k]) > chargeInfo.getTieMarginDiscountValue() && Double.parseDouble(margin[k])<= legDOB.getMarginLimit() ) )
                    		  {
                    			  chargeInfo.setMarginTestFailed(false);
                    		  }
                    		  else
                    		  {
                                  if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < legDOB.getMarginLimit())
                                      chargeInfo.setMarginTestFailed(true);
                                  else
                                      chargeInfo.setMarginTestFailed(false);
                    			  
                    		  }
                    		  }
                		   
                	   }
                	   else
                	   {
                           if(round((Double.parseDouble(sellRate[k])-chargeInfo.getBuyRate())*100/chargeInfo.getBuyRate()) < legDOB.getMarginLimit())
                               chargeInfo.setMarginTestFailed(true);
                           else
                               chargeInfo.setMarginTestFailed(false);
                        		   
                	   }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                   }
                  }
                  else
                  {
                   chargeInfo.setMarginTestFailed(false);
                   chargeInfo.setMarginType(marginType[k]);
                    chargeInfo.setMargin(Double.parseDouble(margin[k]));
                  chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                  }
                  frtChargeInfo.remove(k);
                  frtChargeInfo.add(k,chargeInfo);
                }
              }
              }
            
               //if(!"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
              if("RSR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"CSR".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))//@@Modified by Kameswari for the enhancement
              {
               if(request.getParameter("frtDiscount"+i+"Type"+frtIndices[j])!=null)
              {
                discountType  =  request.getParameterValues("frtDiscount"+i+"Type"+frtIndices[j]);
                discount      =  request.getParameterValues("frtDiscount"+i+frtIndices[j]);
                sellRate      =  request.getParameterValues("frtSellRate"+i+frtIndices[j]);
                  
                 if(discount!=null)
                 {
                  chargesDOB.setMarginDiscountFlag("D");
                  int disLen	=	discount.length;
                for(int k=0;k<disLen;k++)
                {
                  chargeInfo = (QuoteChargeInfo)frtChargeInfo.get(k);
                  
                  chargeInfo.setDiscountType(discountType[k]);
                  //Logger.info(FILE_NAME,"chargeInfo...margin type:"+chargeInfo.getMarginType());
                  chargeInfo.setDiscount(Double.parseDouble(discount[k]));
                  chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                    if("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))
                  {
                  
                  if("P".equalsIgnoreCase(discountType[k]))
                  {
                	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10                	  
                   /*
                	  if(Double.parseDouble(discount[k]) > legDOB.getDiscountLimit())
                      chargeInfo.setMarginTestFailed(true);
                    else
                      chargeInfo.setMarginTestFailed(false);
                      */
                   	  if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
               			   ||"Modify".equalsIgnoreCase(request.getParameter("Operation"))) )

                	  {
                   		 if("Modify".equalsIgnoreCase(request.getParameter("Operation")) && "REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()))
                   		  {
                          	  if(Double.parseDouble(discount[k]) > legDOB.getDiscountLimit())
                                  chargeInfo.setMarginTestFailed(true);
                                else
                                  chargeInfo.setMarginTestFailed(false);
             
                   		  }
                   		  else{
                		  if(Double.parseDouble(discount[k]) == chargeInfo.getTieMarginDiscountValue() ||
                				  (Double.parseDouble(discount[k])< chargeInfo.getTieMarginDiscountValue() &&
                						  Double.parseDouble(discount[k])>=legDOB.getDiscountLimit()) )
                		  {
                			  chargeInfo.setMarginTestFailed(false);
                		  }
                		  else
                		  {
                          	  if(Double.parseDouble(discount[k]) > legDOB.getDiscountLimit())
                                  chargeInfo.setMarginTestFailed(true);
                                else
                                  chargeInfo.setMarginTestFailed(false);
                			  
                		  }
                   		  }
                	  }
                	  else
                	  {
                    	  if(Double.parseDouble(discount[k]) > legDOB.getDiscountLimit())
                              chargeInfo.setMarginTestFailed(true);
                            else
                              chargeInfo.setMarginTestFailed(false);
                		  
                	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                  }
                  else if("A".equalsIgnoreCase(discountType[k]))
                  {
                	//@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10               	  
                	  /*
                    if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > legDOB.getDiscountLimit())
                        chargeInfo.setMarginTestFailed(true);
                    else
                        chargeInfo.setMarginTestFailed(false);
                        */
                  	  if("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag()) && ("COPY".equalsIgnoreCase(request.getParameter("Operation"))
                  			   ||"Modify".equalsIgnoreCase(request.getParameter("Operation"))) )

                	  {
                  		  if("Modify".equalsIgnoreCase(request.getParameter("Operation")) && "REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()))
                  		  {
                                  if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > legDOB.getDiscountLimit())
                                      chargeInfo.setMarginTestFailed(true);
                                  else
                                      chargeInfo.setMarginTestFailed(false);
             
                  		  }
                  		  else
                  		  {
                   		  if(Double.parseDouble(discount[k]) == chargeInfo.getTieMarginDiscountValue() ||
                				  (Double.parseDouble(discount[k])< chargeInfo.getTieMarginDiscountValue() &&
                						  Double.parseDouble(discount[k])>=legDOB.getDiscountLimit()) )
                		  {
                			  chargeInfo.setMarginTestFailed(false);
                		  }
                 		  else
                		  {
                              if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > legDOB.getDiscountLimit())
                                  chargeInfo.setMarginTestFailed(true);
                              else
                                  chargeInfo.setMarginTestFailed(false);
                			  
                		  }
                  		  }
                	  }//@@ended by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
                	  else
                	  {
                          if(round((chargeInfo.getRecOrConSellRrate()-Double.parseDouble(sellRate[k]))*100/chargeInfo.getRecOrConSellRrate()) > legDOB.getDiscountLimit())
                              chargeInfo.setMarginTestFailed(true);
                          else
                              chargeInfo.setMarginTestFailed(false);
                		  
                	  }
                  }
                }
                else
                  {
                   chargeInfo.setMarginTestFailed(false);
                 //  chargeInfo.setMarginType(marginType[k]);
                   // chargeInfo.setMargin(Double.parseDouble(margin[k]));
                  //chargeInfo.setSellRate(Double.parseDouble(sellRate[k]));
                  }
                
                  frtChargeInfo.remove(k);
                  frtChargeInfo.add(k,chargeInfo);
                }
                 }
              }
            }
            chargesDOB.setChargeInfoList(frtChargeInfo);
             chargesDOB.setFrequencyChecked(request.getParameter("frequencyCheck"+i));
             chargesDOB.setTransitTimeChecked(request.getParameter("transitTimeCheck"+i));
        
             chargesDOB.setCarrierChecked(request.getParameter("carrierCheck"+i));
             chargesDOB.setRateValidityChecked(request.getParameter("rateValidityCheck"+i));
            frtCharges.remove(frtIndices[j]);
            frtCharges.add(frtIndices[j],chargesDOB);
            }
          }
          legDOB.setSelectedFreightChargesListIndices(frtIndices);
          legDetails.remove(i);
          legDetails.add(i,legDOB);
        }
        //Logger.info(FILE_NAME,"originIndices::"+originIndices.length);
        finalDOB.setLegDetails(legDetails);
       
        //finalDOB.setSelectedOriginChargesListIndices(originIndices);
        //finalDOB.setSelctedDestChargesListIndices(destIndices);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in doMarginTest()"+e);
      logger.error(FILE_NAME+"Error in doMarginTest()"+e);
      e.printStackTrace();
    }
    return finalDOB;
  }
  private void doModifyProcess(HttpServletRequest request,HttpServletResponse response) throws ServletException
  {
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
    ESupplyGlobalParameters   loginbean         = null;
    QuoteFinalDOB             finalDOB          = null;
    QuoteMasterDOB            masterDOB         = null;
   String                    quoteId           = "";
    String                    customerId        = "";
    String                    origin            = "";
    String                    destination       = "";
    String                    operation         = null;
    int                       shipmentMode      = 0;
    HttpSession               session      = request.getSession();
    
    Hashtable				          accessList			  =	 null;
    String					          buyRatesPermission=	 "Y";
    String					          accessLevel       =	 null;
    String                    customerName      =  "";
    String                    quoteStatus       =  "";
    String                    quoteActive       =  "";
    try
    {
      quoteId     = request.getParameter("QuoteId").trim();//Added by Rakesh on 25-02-2011 for Issue:236363 
      operation   = request.getParameter("Operation");
      
      accessList  =  (Hashtable)session.getAttribute("accessList");

      if(accessList.get("10605")==null)
          buyRatesPermission	= "N";
      
      masterDOB   = new QuoteMasterDOB();
      loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      //lookUpBean  = new LookUpBean();
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      
      if("HO_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "H";
      else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "A";
      else
          accessLevel = "O";
       //@@Commented by Kameswari for the WPBN issue-26514
     /* if(request.getParameter("originLoc")!=null && request.getParameter("originLoc").trim().length()>0)
        origin = request.getParameter("originLoc");
      if(request.getParameter("destLoc")!=null && request.getParameter("destLoc").trim().length()>0)
        destination = request.getParameter("destLoc");
      if(request.getParameter("customerId")!=null && request.getParameter("customerId").trim().length()>0)
        customerId = request.getParameter("customerId");
    
      if(request.getParameter("shipmentMode")!=null && request.getParameter("shipmentMode").trim().length()>0)
        shipmentMode =  Integer.parseInt(request.getParameter("shipmentMode"));
      
     masterDOB.setOriginLocation(origin); 
     masterDOB.setDestLocation(destination);
     masterDOB.setCustomerId(customerId);
     masterDOB.setShipmentMode(shipmentMode);*/
     //@@WPBN issue-26514
      if(request.getParameter("originLoc")!=null && request.getParameter("originLoc").trim().length()>0)
        origin = request.getParameter("originLoc");
      if(request.getParameter("destLoc")!=null && request.getParameter("destLoc").trim().length()>0)
        destination = request.getParameter("destLoc");
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
        //masterDOB.setQuoteId(Long.parseLong(quoteId.trim()));  //@@ COmmented by subrahmanyam for the enhancement #146971 on 2/12/08
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
          doDispatcher(request,response,"qms/QMSQuoteEnterId.jsp?operation=Modify&origin="+origin+"&destination="+destination+"&customerId="+customerId+"&quoteId="+quoteId+"&quoteStatus="+quoteStatus+"&customerName="+customerName+"&quoteActive="+quoteActive);
      }
      else
      {         
         finalDOB    = remote.getMasterInfo(quoteId,loginbean);
         //@@Since a route-plan is specific to a Quote, the route details will not be copied from the previous Quote.
         //@@(As Per WPBN Issue No 10799)
         if("Copy".equalsIgnoreCase(operation))
         {
        	 //@@Added by kiran.v on 18/11/2011 for Wpbn Issue-280269
        	 finalDOB.getMasterDOB().setCustTime("");
        	 finalDOB.getMasterDOB().setCustDate(null);
        	 //@@Ended by kiran.v
           finalDOB.setLegDetails(null);
           finalDOB.setMultiModalQuote(false);
           finalDOB.getMasterDOB().setRouteId(null);
         }//@@
         //session.setAttribute("finalDOB",finalDOB);
         session.setAttribute("PreFlagsDOB",finalDOB.getFlagsDOB());
         if("View".equalsIgnoreCase(operation))
         {
           session.setAttribute("viewFinalDOB",finalDOB);
           doDispatcher(request,response,"qms/QMSQuoteMasterView.jsp");
         }
         else
         {
          session.setAttribute("finalDOB",finalDOB);
          doDispatcher(request,response,"qms/QMSQuoteMaster.jsp");
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
  /**
	 * This method helps in forwarding the request to the respective initial page by getting the necessary information depending up on the operation
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doDispatcher request
	 * @exception ServletException if the request for the doDispatcher could not be handled.
	 */ 
  public void doForwardToInitialJSP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String  operation = null;
   
    try
    {
      operation = request.getParameter("Operation");
      //Logger.info(FILE_NAME,"In doForwardToInitialJSP operation :"+operation);
        if("Add".equalsIgnoreCase(operation))
      {
       doDispatcher(request,response,"qms/QMSQuoteMaster.jsp");
      }
      else if("View".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation)  || "Copy".equalsIgnoreCase(operation))
      {
        doDispatcher(request,response,"qms/QMSQuoteEnterId.jsp");
      }
      else if("Grouping".equalsIgnoreCase(operation))
      {
        doDispatcher(request,response,"qms/QMSQuoteGroupingEnterId.jsp");
      }
      
      
    }
    catch(Exception ex)
		{
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [setQuoteMasterDOB()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [setQuoteMasterDOB()] -> "+ex.toString());
		}
    
  }
  
  /**
	 * This method is used for Quote View
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @param forwardFile a string which represents the path of the file to be forwarded.
   * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doDispatcher request
	 * @exception ServletException if the request for the doDispatcher could not be handled.
	 */
   
  private void doGenerateQuoteView(HttpServletRequest request,HttpServletResponse response) throws ServletException
  {
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
    ESupplyGlobalParameters   loginbean         = null;
    QuoteFinalDOB             finalDOB          = null;
    String                    quoteId           = null;
    CostingMasterDOB          costingMasterDOB  = null;
    CostingHDRDOB             costingHDRDOB     = null;
    String                    customerId        = "";
    String                    origin            = "";
    String                    destination       = "";
    String                    operation         = null;
    int                       shipmentMode      = 0;
    QuoteMasterDOB            masterDOB         = null;
    String                    accessLevel       = "";
    Hashtable				          accessList			  =	null;
    String                    escalatedReport   = null;
    String					  isEscalated		= null; // Added by Gowtham	
    String					          buyRatesPermission=	 "Y";
    ArrayList                attachmentIdList   = null;
    HttpSession                session                   = request.getSession();
    QuoteFlagsDOB           flagsDOB          = null;
    String[] attachmentId= null;
    String                   quoteStatus      = ""; 
    String                   customerName      = "";
    String quoteActive="";
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

       
        masterDOB       = new QuoteMasterDOB();
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
      
        if(request.getParameter("originLoc")!=null && request.getParameter("originLoc").trim().length()>0)
            origin = request.getParameter("originLoc");
        if(request.getParameter("destLoc")!=null && request.getParameter("destLoc").trim().length()>0)
            destination = request.getParameter("destLoc");
        if(request.getParameter("customerId")!=null && request.getParameter("customerId").trim().length()>0)
            customerId = request.getParameter("customerId");
        if(request.getParameter("QuoteId")!=null && request.getParameter("QuoteId").trim().length()>0)
            quoteId = request.getParameter("QuoteId").trim();//
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
      
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      
      StringBuffer errors = remote.validateQuoteId(masterDOB);
      
      if(errors!=null && errors.length()>0)
      { 
         request.setAttribute("errors",errors.toString());
         doDispatcher(request,response,"qms/QMSQuoteEnterId.jsp?operation=Modify&origin="+origin+"&destination="+destination+"&customerId="+customerId+"&quoteId="+quoteId+"&customerName="+customerName+"&quoteStatus="+quoteStatus+"&quoteActive="+quoteActive);
      }
      else
      {
          finalDOB    = remote.getQuoteDetails(quoteId,buyRatesPermission,loginbean);
         
          if("View".equalsIgnoreCase(operation))
          {
               finalDOB.getHeaderDOB().setDateOfQuotation(finalDOB.getMasterDOB().getCreatedDate());   
          }
           attachmentIdList =   remote.getAttachmentDtls(finalDOB);
           if(attachmentIdList!=null)
           {
          finalDOB.setAttachmentDOBList(attachmentIdList);
           }
          
          session.setAttribute("viewFinalDOB",finalDOB);
          // Added by Gowtham for Escalated Quote Modify Issue.
          if(escalatedReport!= null &&  "self".equalsIgnoreCase(escalatedReport.trim()))
          {		
        	  masterDOB = finalDOB.getMasterDOB();
        	  masterDOB.setOperation("View");
        	  finalDOB.setMasterDOB(masterDOB);
         //     finalDOB  = remote.getFreightSellRates(finalDOB); commented by govind for the issue of showing all rates for escalated quotes which are not selected in rates page
             // masterDOB.setOperation("View");
              masterDOB.setOperation("Modify");
              finalDOB.setMasterDOB(masterDOB);  
              request.setAttribute("finalDOB",finalDOB);
             // finalDOB  = doGetHeaderAndCharges(request,response); -- Commented by govind when working on escalatedQuoteView issue not showing color Govind
              session.setAttribute("viewFinalDOB",finalDOB);
              finalDOB =  doMarginTest(request,response,finalDOB);
  	       // request.setAttribute("isMarginTestPerformed","Y");
              masterDOB.setOperation("View");
              finalDOB.setMasterDOB(masterDOB);
  	        session.setAttribute("finalDOB",finalDOB);
              doDispatcher(request,response,"qms/QMSQuoteEscalatedChargesSelect.jsp?MarginTest=Yes&count=1");
          }
       // Added by Gowtham for Escalated Quote Modify Issue.
         /* if("self".equalsIgnoreCase(escalatedReport))
            doDispatcher(request,response,"qms/QMSQuoteChargesView.jsp");*/
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
                        String actionPage    =   "QMSQuoteController?from=summary";
                        //added by subrahmanyam for 185127 on 02-oct-09
                        serviceLevelDes = remote.getServiceLevelDesc(quoteId);
                        finalDOB.getHeaderDOB().setTypeOfService(serviceLevelDes);
                        //ended by subrahmanyam for 185127 on 02-oct-09
                        int  mailStatus  = doPDFGeneration(finalDOB,request,response);
                          //
                         if(mailStatus == 3 || mailStatus == 7)
                  {
                    setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & E-mail Has Been Sent To :"+toEmailIds.toString()+" & Fax Has Been Sent To :"+toFaxIds , operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 2  || mailStatus == 6)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax Has Been Sent To "+toFaxIds , operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & Fax Has Been Sent To :"+toFaxIds , operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 1 || mailStatus == 5)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & E-mail Has Been Sent To :"+toEmailIds.toString()+" & Fax Has Been Sent To :"+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" & E-mail Has Been Sent To :"+toEmailIds.toString(), operation , "QMSQuoteController");
                  }
                  else if(mailStatus == 0  || mailStatus==4)
                  {
                    if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "N".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                      setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To :"+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)"), operation , "QMSQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getFaxFlag()) && "N".equalsIgnoreCase(flagsDOB.getEmailFlag()))
                      setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To : "+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else if("Y".equalsIgnoreCase(flagsDOB.getEmailFlag()) && "Y".equalsIgnoreCase(flagsDOB.getFaxFlag()))
                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId+" But E-mail Was Not Sent To : "+(toEmailIds.length()>0?toEmailIds.toString():"(No Email Id Provided.)")+" & Fax Has Been Sent To :"+(toFaxIds.length()>0?toFaxIds.toString():("(No Fax Number Provided)")), operation , "QMSQuoteController");
                    else
                     setErrorRequestValues(request, "RSI", "The PDF Was Successfully Generated With QuoteId: "+quoteId, operation , "QMSQuoteController");
                      
                  }
                        //
                        //setErrorRequestValues(request, "MSG", "The PDF was successfully generated for Quote Id "+masterDOB.getQuoteId()+".", operation , actionPage);
                        session.removeAttribute("viewFinalDOB");
                        session.removeAttribute("PreFlagsDOB");
                        doDispatcher(request, response, "QMSErrorPage.jsp"); 
                }
          else
             doDispatcher(request,response,"qms/QMSQuoteView.jsp");
                
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
  
  /**
	 * This method is used for ChargeGroups
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @param forwardFile a string which represents the path of the file to be forwarded.
   * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doDispatcher request
	 * @exception ServletException if the request for the doDispatcher could not be handled.
	 */
  
  private void chargeGroupsView(HttpServletRequest request,HttpServletResponse response) throws ServletException
  {
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
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
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = (QMSQuoteSession)home.create();
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
      
      doDispatcher(request,response,"qms/QMSQuoteChargeGroups.jsp");
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in Controller in doModifyProcess");
      logger.error(FILE_NAME+"Error in Controller in doModifyProcess");
      e.printStackTrace();
      throw new ServletException();
    }
  }
  
  
  /**
	 * This method helps in forwarding the request to the respective page
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @param forwardFile a string which represents the path of the file to be forwarded.
   * 
	 * @exception IOException if an input or output error is detected when the servlet handles the doDispatcher request
	 * @exception ServletException if the request for the doDispatcher could not be handled.
	 */
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
  
  /**
	 * This is a Help method of setting of dtl for Errorpage
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param errorCode a String that contains error code
	 * @param errorMessage a String that contains error message
	 * @param operation a String that contains main Operatiion that it has come from
	 * @param nextNavigation a String that contains where the next navigation is
	 *
	 * @exception IOException if an input or output error is detected when the servlet handles the setErrorRequestValues request
	 * @exception ServletException if the request for the setErrorRequestValues could not be handled.
	 */
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
    QuoteFinalDOB           finalDOB            =  null;
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
          finalDOB  = (QuoteFinalDOB)session.getAttribute("finalDOB");
        else if(session.getAttribute("viewFinalDOB")!=null)
          finalDOB  = (QuoteFinalDOB)session.getAttribute("viewFinalDOB");
        
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
  
  
  private void quoteGroupsDtl(HttpServletRequest request,HttpServletResponse response) throws ServletException
  {
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
    ESupplyGlobalParameters   loginbean         = null;
    QuoteFinalDOB             finalDOB          = null;
    QuoteMasterDOB            masterDOB         = null;
    String[]                  quoteId           = null;
    CostingMasterDOB          costingMasterDOB  = null;
    CostingHDRDOB             costingHDRDOB     = null;
    String[]                  quoteValues       = null;
   HttpSession  session      = request.getSession();
   //Added by Anusha V
   QMSQuoteDAO qqd=new QMSQuoteDAO();
   List lanedtls;
    ArrayList                 mainDtl           = new ArrayList();
    try
    {
      quoteId     = (String[])session.getAttribute("quoteIdDtl");
      
      
      loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      //lookUpBean  = new LookUpBean();
      
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      
      int quoteIdLen	=	quoteId.length;
      for(int m=0;m<quoteIdLen;m++)
      {
       
       costingHDRDOB = new CostingHDRDOB();
       finalDOB      = null; 
       costingHDRDOB.setQuoteid(quoteId[m]);          
       finalDOB    = remote.getMasterInfo(quoteId[m],loginbean); 
       masterDOB   = finalDOB.getMasterDOB();
       masterDOB.setCompanyId(loginbean.getCompanyId());
        finalDOB    = remote.getQuoteHeader(finalDOB);
        //Commented by Anusha V
        //costingMasterDOB = remote.getQuoteRateInfo(costingHDRDOB,loginbean);
        //Added by Anusha V
        lanedtls=qqd.getQuoteLaneDetails(costingHDRDOB);
        for(int i=0;i<lanedtls.size();i++)
        {
         costingHDRDOB =(CostingHDRDOB)lanedtls.get(i);
             
         costingMasterDOB = remote.getQuoteRateInfo(costingHDRDOB,loginbean);
       }
       finalDOB.setCostingMasterDOB(costingMasterDOB);
       masterDOB   = finalDOB.getMasterDOB();
       masterDOB.setOperation("QuoteGrouping");
       finalDOB.setMasterDOB(masterDOB);
       finalDOB    = remote.getQuoteContentDtl(finalDOB);
       mainDtl.add(finalDOB);
      }
       
      session.setAttribute("mainDtl",mainDtl);
      
      doDispatcher(request,response,"qms/QMSQuoteGroupView.jsp");
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in Controller in doModifyProcess");
      logger.error(FILE_NAME+"Error in Controller in doModifyProcess");
     
      e.printStackTrace();
      throw new ServletException();
    }
  }
  
  private void quoteGroupsSendOptions(HttpServletRequest request,HttpServletResponse response) throws ServletException
  {
   
    ESupplyGlobalParameters   loginbean         = null;
    QuoteFinalDOB             finalDOB          = null;
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
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
    try
    {
      mainDtl     = (ArrayList)session.getAttribute("mainDtl");
      operation   = request.getParameter("Operation");
      email       = request.getParameter("email");
      fax         = request.getParameter("fax");
      print       = request.getParameter("print");
      
      loginbean   = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      //lookUpBean  = new LookUpBean();
      
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = home.create();
      
        if("on".equalsIgnoreCase(email) || "on".equalsIgnoreCase(fax) || "on".equalsIgnoreCase(print))
        {
           terminalId  = loginbean.getTerminalId();
           int mainDtlSize	=	mainDtl.size();
           for(int i=0;i<mainDtlSize;i++)
         {
            finalDOB  = (QuoteFinalDOB)mainDtl.get(i);   
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
              finalDOB  = (QuoteFinalDOB)mainDtl.get(i);              
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
      logger.error(FILE_NAME+"Error in Controller in doModifyProcess");
      e.printStackTrace();
      throw new ServletException();
    }
  }
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
            QuoteFinalDOB             finalDOB     =  null;
            ESupplyDateUtility  eSupplyDateUtility =	new ESupplyDateUtility();
            QuoteHeader    headerDOB		           =	null;
            QuoteMasterDOB masterDOB               =  null ;
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
             
             finalDOB               =   (QuoteFinalDOB)mainDtl.get(0);
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
             
             finalDOB               =   (QuoteFinalDOB)mainDtl.get(m);
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
            
            chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
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
            
            chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.RED));
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
            
            chk = new Chunk("Origin : ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
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
            partCountry.addCell(cellCountry);
            
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
            
            chk = new Chunk("   "+(headerDOB.getCommodity()!=null?headerDOB.getCommodity():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
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
            
            chk = new Chunk("Incoterms: ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cellCountry = new Cell(chk);
            cellCountry.setBackgroundColor(Color.lightGray);
            cellCountry.setNoWrap(true); cellCountry.setLeading(8.0f);
            partCountry.addCell(cellCountry);
            
            chk = new Chunk("   "+(headerDOB.getIncoTerms()!=null?headerDOB.getIncoTerms():""),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
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
                      chk = new Chunk("   "+(headerDOB.getNotes()!=null?headerDOB.getNotes().toUpperCase():" "),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
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
             
             finalDOB               =   (QuoteFinalDOB)mainDtl.get(m);
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
             
             finalDOB               =   (QuoteFinalDOB)mainDtl.get(m);
             headerDOB		          =	  finalDOB.getHeaderDOB();
             masterDOB              =   finalDOB.getMasterDOB();
             
            
             costingMasterDOB         =  finalDOB.getCostingMasterDOB();
             ArrayList originCharges  = (ArrayList)costingMasterDOB.getOriginList();
            
            QuoteCharges chargesDOB           = null;
            ArrayList    originChargeInfo     = null;
            int          originChargesInfoSize= 0;
            QuoteChargeInfo chargeInfo        = null;
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
              QuoteFreightLegSellRates       legCharges	    = null;
              String                          wBSlab        = "";//added by subrahmanyam for 182516 on 10-sep-09             
              int mDtlSize	= mainDtl.size();
              for(int m=0;m<mDtlSize;m++)
            {
             
              finalDOB               =   (QuoteFinalDOB)mainDtl.get(m);
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
             
              finalDOB               =   (QuoteFinalDOB)mainDtl.get(m);
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
             
              finalDOB               =   (QuoteFinalDOB)mainDtl.get(m);
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
            //Added by Anusha V
            int hFLen = 0;
            headFoot = masterDOB.getDefaultHeaderFooter();
            //if(headFoot.length>0){
            //hFLen	=	headFoot.length;}
            //Commented by Anusha V
            //int hFLen = headFoot.length;
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
              
              for(int i=0;i<headFoot.length;i++)
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
              //Commented by Anusha V
              //String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime())+masterDOB.getQuoteId();//@@ Added by subrahmanyam for the Enhancement #146971 on 2/12/08
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
                finalDOB               =   (QuoteFinalDOB)mainDtl.get(i); 
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
  
  private void getUpdatedQuoteInfo(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginbean) throws ServletException
  {
    QuoteFinalDOB   finalDOB    =   null;
    String          quoteId     =   null;
    String          changeDesc  =   null;
    String          sellBuyFlag =   null;
    String          masterId    =   null;
    //HttpSession    session       =  null;
    String          buyRatesFlag=   null;
  //  String          flag        =   null;
    String          quoteType       =   null;
    String          quoteOption     =   null;
    QuoteMasterDOB  masterDOB         =   null;
    UpdatedQuotesReportDOB reportDOB  =   null;
    
    QMSQuoteSessionHome       home              = null;
    QMSQuoteSession           remote            = null;
    
    HttpSession session   =   request.getSession();
    
    try
    {
      //lookUpBean  = new LookUpBean();
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean"); 
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
       finalDOB    =   getMarginLimit(finalDOB);
      
      finalDOB.setUpdatedReportDOB(reportDOB);
      
      request.setAttribute("fromWhere","reports/UpdatedQuotesReport.jsp?Operation=updatedQuotes&format=html&SortBy="+request.getParameter("sortedBy")+"&SortOrder="+request.getParameter("sortedOrder")+"&PageNo="+request.getParameter("pageNo"));
      if("Modify".equalsIgnoreCase(quoteOption)||"previousQuote".equalsIgnoreCase(quoteType))
      {
        session.setAttribute("updatedSendOptions",finalDOB.getFlagsDOB());
        session.setAttribute("finalDOB",finalDOB);
        doDispatcher(request,response,"qms/QMSQuoteChargesSelect.jsp?Operation=Add&flag=true&count=0&update=update");
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
        session.setAttribute("viewFinalDOB",finalDOB);
        doDispatcher(request,response,"qms/QMSQuoteSummaryView.jsp?Operation=View&flag=true&update=Update");
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
  
    public void sendMail(String frmAddress, String toAddress,String subject, String message, String attachments,ArrayList list) throws FoursoftException
		{
//			System.out.println("sendMail in CustomizedReportBean called.. attchment :: "+attachment);
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
          QuoteAttachmentDOB dob        = null;
          File               file       = null;
          FileOutputStream   fileStream = null;
          int listSize	=	list.size();
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
  //@@Added by Kameswari for the WPBN issue-61289
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
    	notesArray = new String[noteFinal!=null ?noteFinal.size():0];
    	int noteFinalSize	=	noteFinal!=null?noteFinal.size():0;
    		for(int x=0; x<noteFinalSize;x++)
    			notesArray[x] = (String)noteFinal.get(x);
    
    return notesArray ;
  }
  //ended for removeEnterForNotes() for PBN ID: 194328
	//@@Added by kiran.v on 03/11/2011 for Wpbn Issue
//@@Added by kiran.v on 16/11/2011
	public String round1(double sellRate,String percent)
{
  
java.text.DecimalFormat dfDecimal	 =	new java.text.DecimalFormat("#######0.00000");
java.text.DecimalFormat dfDecimal2	 =	new java.text.DecimalFormat("#######0.00");
String rateString ="";
int k = 0;
int l = 0;
int m = 0;
rateString = Double.toString(sellRate);
k = rateString.length();
l = rateString.indexOf(".");
m = (k - l)-1;
if(m>5){
	if("%".equals(percent))
rateString = dfDecimal.format(sellRate);
	else
		rateString = dfDecimal2.format(sellRate);
return rateString;
}
else if(m==1 || l==-1)
{

	rateString = dfDecimal2.format(sellRate);
	return rateString;
}
else
	return Double.toString(sellRate);

}
//@@Ended by kiran.v
  
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
  
//}
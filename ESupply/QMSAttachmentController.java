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
 

 * File					: QMSAttachmentController.java
 * @author			: Kameswari
 * @date				: 23/01/07
 *


 *	This Controller is used to control the flow in the attachment  module
 */
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;
import com.qms.setup.java.QMSAttachmentDOB;
import com.qms.setup.java.QMSAttachmentDetailDOB;
import com.qms.setup.java.QMSAttachmentFileDOB;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

public class QMSAttachmentController extends HttpServlet 
{
  private static       Logger logger       = null;
   private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
  private String       FILE_NAME           = "QMSAttachmentController.java";  

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
    logger  = Logger.getLogger(QMSAttachmentController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    try
    {
        doPost(request,response);  
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.info(FILE_NAME+""+e.toString());
    }   
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
      HttpSession          session               =  null;
      QMSAttachmentDOB     attachmentDOB         =  null;
      QMSAttachmentDOB     dob                   =  null;
      String               operation             =  null;
      String               subOperation          =  null;    
      ErrorMessage	       errorMessageObject	   =  null;
      String               errorMessage          =  "";
      String               errorCode             =  "";
      String               nextNavigation        =  "";
      String               accessType            =  null;
      String               terminalId            =  null;
      String               attachmentId          =  null;
      String               consoleType           =  null;
      String               Id                    = null;
      String               fileName              =  "";
      ArrayList            viewList              =  null;
      ArrayList			       keyValueList			     =  new ArrayList(); 
      ArrayList            stringList            =  new ArrayList();
      ArrayList            filesList             =  new ArrayList();
      ArrayList            quoteAttachmentIdList =  new ArrayList();
      byte[]               buffer                =  new byte[9216];
      long                 uniqueId;  
      boolean              flag;     
      int  i,c,k=0;  
     
      
      try
     {
        operation           =   request.getParameter("Operation");
        subOperation        =   request.getParameter("subOperation");  
        accessType          =   request.getParameter("accessType");
        terminalId          =   request.getParameter("terminalId");
        attachmentId        =   request.getParameter("attachmentId");
        session             =   request.getSession(); 
        if(("Add".equalsIgnoreCase(operation)||"Modify".equalsIgnoreCase(operation)||"View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation))&&(subOperation==null))
           nextNavigation    =  "QMSAttachmentEnterId.jsp";
        else if("Add".equalsIgnoreCase(operation)&&"Next".equalsIgnoreCase(subOperation))
        {
          attachmentDOB      =    new QMSAttachmentDOB();
          attachmentDOB.setAccessType(accessType);
          attachmentDOB.setAttachmentId(attachmentId);
          attachmentDOB.setTerminalId(terminalId);
          
           c            =     validateFields(attachmentDOB);
          if(c==3)
          {
             session.setAttribute("attachmentDOB",attachmentDOB);
             nextNavigation     =  "QMSAttachmentMaster.jsp?Operation="+operation;
            }
          else 
          {
             if(c==1)
                errorMessage  = "AttachmentId already exists.Please enter valid attachmentId to continue";
             else if(c==2)
                errorMessage  = "TerminalId is invalid,Please enter valid terminalId to continue";
            else
                errorMessage  = "TerminalId is invalid,AttachmentId already exists,Please enter valid attachmentId and terminalId to continue ";
            
             request.setAttribute("ErrorMessage",errorMessage);
             nextNavigation      = "QMSAttachmentEnterId.jsp?Operaion="+operation;
           }
        }
        else if(("Modify".equalsIgnoreCase(operation)||"View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation)||"ViewDetails".equalsIgnoreCase(operation))&&"Next".equalsIgnoreCase(subOperation))
        {
          attachmentDOB      =    new QMSAttachmentDOB();
          attachmentDOB.setAccessType(accessType);
          
          attachmentDOB.setAttachmentId(attachmentId);
          attachmentDOB.setTerminalId(terminalId);
        
          c            =     validateFields(attachmentDOB);
        
          if(c==3)
          {
             errorMessageObject  =   new ErrorMessage("Record Not Found",request.getContextPath()+"/QMSAttachmentEnterId.jsp");
             keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
             keyValueList.add(new KeyValue("Operation",operation)); 	
             errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
             nextNavigation      = "ESupplyErrorPage.jsp";
          }
          else if(c==1)
          {
            
             session.setAttribute("QMSAttachmentDOB",attachmentDOB);
             dob                    =   viewAttachmentDtls(request,response);
          
             quoteAttachmentIdList  =   quoteAttachemntIdList(request,response);
             if(dob!=null)
             {
                request.setAttribute("QMSAttachmentDOB",dob);
                request.setAttribute("quoteAttachmentIdList",quoteAttachmentIdList);
                nextNavigation     =  "QMSAttachmentMasterView.jsp?Operation="+operation;
             }  
         }
          else      
          {
             errorMessage  = "TerminalId is invalid,Please enter valid terminalId to continue ";
             request.setAttribute("ErrorMessage",errorMessage);
             nextNavigation      = "QMSAttachmentEnterId.jsp?Operaion="+operation;
          }
        }
          else if(("Add".equalsIgnoreCase(operation)||"Modify".equalsIgnoreCase(operation))&&"Submit".equalsIgnoreCase(subOperation))
         {
            i   =   addAttachmentDetails(request,response);
            if(i>0)
           {
                  if("Add".equalsIgnoreCase(operation))
                  {
                    errorMessageObject  =   new ErrorMessage("Record successfully inserted",request.getContextPath()+"/QMSAttachmentEnterId.jsp");
                    keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
                  }
                  else if("Modify".equalsIgnoreCase(operation))
                  {
                    errorMessageObject  =   new ErrorMessage("Record successfully updated",request.getContextPath()+"/QMSAttachmentEnterId.jsp");
                    keyValueList.add(new KeyValue("ErrorCode","RSM")); 
                  }
           }
            else
           {
                  if("Add".equalsIgnoreCase(operation))
                    errorMessageObject   =   new ErrorMessage("Error occured while inserting the Record",request.getContextPath()+"/QMSAttachmentEnterId.jsp");
                  else if("Modify".equalsIgnoreCase(operation))
                    errorMessageObject   =   new ErrorMessage("Error occured while updating the Record",request.getContextPath()+"/QMSAttachmentEnterId.jsp");
                    keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
           } 
             keyValueList.add(new KeyValue("Operation",operation)); 	
             errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
             nextNavigation      = "ESupplyErrorPage.jsp";
            }
            else if("Delete".equalsIgnoreCase(operation)&&"Submit".equalsIgnoreCase(subOperation))
            {
                i   =  deleteAttachmentdtls(request,response);
              if(i>0)
               {
                    errorMessageObject  =   new ErrorMessage("Record successfully deleted",request.getContextPath()+"/QMSAttachmentEnterId.jsp"); 
                    keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
               }
               else
               {
                   errorMessageObject  =   new ErrorMessage("Error occured while deleting the Record",request.getContextPath()+"/QMSAttachmentEnterId.jsp"); 
                   keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
               }
             keyValueList.add(new KeyValue("Operation",operation)); 	
             errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
             nextNavigation      = "ESupplyErrorPage.jsp";
            }
          if(("ViewAll".equalsIgnoreCase(operation)||"Invalidate".equalsIgnoreCase(operation))&&(subOperation==null))
          {
              nextNavigation   =  "QMSAttachmentViewAll.jsp"; 
          }
          else if(("ViewAll".equalsIgnoreCase(operation)||"Invalidate".equalsIgnoreCase(operation))&&("Next".equalsIgnoreCase(subOperation)))
          {
              stringList.add("Attachment ID");
            if(request.getParameter("terminalId")!=null && request.getParameter("terminalId").equals("0"))
                stringList.add("Terminal ID");
            if (request.getParameter("fromCountry")!=null && request.getParameter("fromCountry").equals("0"))
                stringList.add("From Country");
            if (request.getParameter("fromLocation")!=null && request.getParameter("fromLocation").equals("0"))
                stringList.add("From Location");
            if (request.getParameter("toCountry")!=null && request.getParameter("toCountry").equals("0"))
                stringList.add("To Country");
            if (request.getParameter("toLocation")!=null && request.getParameter("toLocation").equals("0"))
                 stringList.add("To Location");
            if (request.getParameter("carrierId")!=null && request.getParameter("carrierId").equals("0"))
                stringList.add("Carrier");
            if (request.getParameter("serviceLevelId")!=null && request.getParameter("serviceLevelId").equals("0"))
                stringList.add("Service Level");
            if (request.getParameter("industryId")!=null && request.getParameter("industryId").equals("0"))
                stringList.add("Industry");
            if (request.getParameter("fileName")!=null && request.getParameter("fileName").equals("0"))
               stringList.add("File");
               stringList.add(operation);
             //Commented & Added by subrahmanyam for the pbn id:203350
               //viewList        =  viewAllAttachmentDtls();
            viewList        =  viewAllAttachmentDtls(request); 
            //ended for 203350
           if(viewList!=null)
            {
                
                request.setAttribute("viewList",viewList);
                request.setAttribute("stringList",stringList);
                nextNavigation  = "QMSAttachmentViewAllDtl.jsp?operation="+operation;
            }
            else
            {
                errorMessageObject  =   new ErrorMessage("Error occured while retreiving the values",request.getContextPath()+"/QMSAttachmentEnterId.jsp"); 
                keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject); 
                nextNavigation      = "ESupplyErrorPage.jsp";
            }
          }
          else if("viewFile".equalsIgnoreCase(operation))
          {
              uniqueId    =   Long.parseLong(request.getParameter("uniqueId"));
              buffer         =   viewPDFFile(uniqueId);
              if(buffer!=null)
             {
                 request.setAttribute("buffer",buffer);
                 nextNavigation  = "QMSAttachmentPDF.jsp?Operation=manageAttachment";
              }
           }
          else if("manageAttachment".equalsIgnoreCase(operation))
          {
              if("viewFile".equalsIgnoreCase(subOperation))
             {
               
               //request.setCharacterEncoding("UTF-8");
               attachmentId  = request.getParameter("attachmentId");
                // attachmentId  = request.getQueryString();
               
               // attachmentId = java.net.URLDecoder.decode("" + request.getParameter("attachmentId"),"UTF8");
                logger .info("attachmentId"+attachmentId);
               attachmentId =  attachmentId.replace(',','&');
                
                 filesList = viewFile(attachmentId); 
                if(filesList!=null)
                 {
                  request.setAttribute("fileslist",filesList);
                   attachmentId =  attachmentId.replace('&',',');
                  nextNavigation = "QMSAttachments.jsp?attachmentId="+attachmentId;
                 }
                else
               {
                  errorMessageObject  =   new ErrorMessage("Error occured while retreiving  the files",request.getContextPath()+"/QMSAttachments.jsp"); 
                  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject); 
                  nextNavigation      = "ESupplyErrorPage.jsp";
               }
             }
             else  if("attachFile".equalsIgnoreCase(subOperation))
            {
               attachmentId  = request.getParameter("attachmentId");
                 attachmentId =  attachmentId.replace(',','&');
               filesList =  insertFile(request,response); 
               if(filesList!=null)
                 {
                  request.setAttribute("fileslist",filesList);
                  attachmentId =  attachmentId.replace('&',',');
                  nextNavigation = "QMSAttachments.jsp?attachmentId="+attachmentId;
                 }
                 else
                {
                  errorMessageObject  =   new ErrorMessage("Error occured while retreiving  the files",request.getContextPath()+"/QMSAttachments.jsp"); 
                  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject); 
                  nextNavigation      = "ESupplyErrorPage.jsp";
                }
              }
           else if("deleteFile".equalsIgnoreCase(subOperation))
          {
              attachmentId  = request.getParameter("attachmentId");
                attachmentId =  attachmentId.replace(',','&');
              Id            = request.getParameter("uniqueId");
              filesList = deleteFile(attachmentId,Id);
              if(filesList!=null)
              {
                request.setAttribute("fileslist",filesList);
                
                nextNavigation = "QMSAttachments.jsp?Operation="+operation;
              }
               else
              {
                errorMessageObject  =   new ErrorMessage("Error occured while retreiving  the files",request.getContextPath()+"/QMSAttachments.jsp"); 
                keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject); 
                nextNavigation      = "ESupplyErrorPage.jsp";
              }
          }
        }
          else if("Invalidate".equalsIgnoreCase(operation)&&"Submit".equalsIgnoreCase(subOperation))
         {
            i = invalidateAttachmentId(request,response);
            if(i>0)
            {
                  errorMessageObject  =   new ErrorMessage("Record was suucesfully invalidated",request.getContextPath()+"/QMSAttachmentViewAll.jsp"); 
                  keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject); 
                  nextNavigation      = "ESupplyErrorPage.jsp";
            }
            else
            {
                  errorMessageObject  =   new ErrorMessage("Error occured while invalidating  the attachmentId",request.getContextPath()+"/QMSAttachmentViewAll.jsp"); 
                  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject); 
                  nextNavigation      = "ESupplyErrorPage.jsp";
            }
         }
       }
      catch(FoursoftException fs)
    {
        fs.printStackTrace();
        errorMessage          =  fs.getMessage(); 
        errorMessageObject    =  new ErrorMessage(errorMessage,"QMSAttachmentController");      
        keyValueList.add(new KeyValue("ErrorCode","ERR"));
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject); 	
        nextNavigation      =  "ESupplyErrorPage.jsp";  
   }
     finally
     {
       try
      {
            doFileDispatch(request,response,nextNavigation);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error while forwarding page "+e.toString());
      }
     }
  }
  private void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)throws IOException, ServletException
	{
		try
		{
	    RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
		  logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}
  /**
	 * This method helps in adding attachmentdetails to the respective table in database
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @exception FourSoftException if the request for the addAttachmentDetails could not be handled.
	 * @returns an integer value to check whether the values are inserted or not in the respective table in database
   */
  private int addAttachmentDetails(HttpServletRequest request,HttpServletResponse response) throws FoursoftException
  {
     SetUpSessionHome         home                 =  null;
     SetUpSession             remote               =  null;
     QMSAttachmentDOB         attachmentDOB        =  null;
     QMSAttachmentDetailDOB   detailDOB            =  null;
     QMSAttachmentFileDOB     fileDOB              =  null;
     MultipartParser		      mp                   =  null;
     Part					            part                 =  null;
     String				            name                 =  null;
     FilePart				          filePart             =  null;
     ParamPart				        paramPart            =  null;
     File	                    fileRead             =  null;
     String				            fileName             =  null;
     BufferedReader		        br                   =  null;
     String                   data                 =  null;  
     HttpSession              session              =  null;
     String                   shipmentMode         =  null;  
     String                   consoleType          =  null;
     String                   quoteType            =  null;  
     String                   defaultFlag          =  null;  
     String                   fromCountry          =  null;   
     String                   fromLocation         =  null;   
     String                   toLocation           =  null;   
     String                   toCountry            =  null;   
     String                   carrierId            =  null;  
     String                   serviceLevelId       =  null;   
     String                   industry             =  null;  
     String                   operation            =  null;
     String[]                 fromCountryList      =  null;   
     String[]                 fromLocationList     =  null; 
     String[]                 toLocationList       =  null; 
     String[]                 toCountryList        =  null; 
     String[]                 carrierIdList        =  null; 
     String[]                 serviceLevelIdList   =  null; 
     String[]                 industryList         =  null; 
     ArrayList                value                =  new ArrayList(); 
     ArrayList                fieldsList           =  new ArrayList();
     ArrayList                fileData             =  new ArrayList();
     ArrayList                order                =  new ArrayList();
     int maxLength;
      int i = 0;
      Collections.sort(order);
      try
      {
           operation    =    request.getParameter("Operation");
           session       =   request.getSession();
           if("Add".equalsIgnoreCase(operation))
             attachmentDOB  = (QMSAttachmentDOB)session.getAttribute("attachmentDOB");
           else
             attachmentDOB  = (QMSAttachmentDOB)session.getAttribute("QMSAttachmentDOB");
         
                mp            =   new MultipartParser(request, 10*1024*1024); // 1MB   
          while ((part = mp.readNextPart()) != null)
          {
                name          =  part.getName();
           if (name != null)
                name          =  name.trim();
          
            if (part.isParam())
           {   				  
              paramPart         =  (ParamPart) part;
              if("shipmentMode".equals(paramPart.getName()))
                  shipmentMode  =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("consoleType".equals(paramPart.getName()))
                  consoleType   =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
         
              if("quoteType".equals(paramPart.getName()))
                  quoteType     =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("defaultFlag".equals(paramPart.getName()))
                  defaultFlag   =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("fromCountry".equals(paramPart.getName()))
                  fromCountry   =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("toCountry".equals(paramPart.getName()))
                  toCountry     =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("fromLocation".equals(paramPart.getName()))
                  fromLocation  =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("toLocation".equals(paramPart.getName()))
                  toLocation    =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("carriers".equals(paramPart.getName()))
                  carrierId     =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("serviceLevelId".equals(paramPart.getName()))
                serviceLevelId  =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
              if("industryId".equals(paramPart.getName()))
                  industry      =   paramPart.getStringValue()!=null?paramPart.getStringValue():"";
         }
         else if (part.isFile())
         {
              if("Add".equalsIgnoreCase(operation))
              {
                filePart         =   (FilePart) part;
                fileName         =   filePart.getFileName();
                filePart.writeTo(new File("./"));
                fileRead         =   new File(filePart.getFileName());	
               // logger.info("path"+ fileRead.getAbsolutePath());
                fileDOB          =   new QMSAttachmentFileDOB();
                fileDOB.setAttachmentId(attachmentDOB.getAttachmentId());
                fileDOB.setFileName(fileName);
                fileDOB.setPdfFile(fileRead);
                fileData.add(fileDOB);
              }
         }
        }
             fromCountryList     =   fromCountry.split(",");
             fromLocationList    =   fromLocation.split(",");
             toLocationList      =   toLocation.split(","); 
             toCountryList       =   toCountry.split(",");
             carrierIdList       =   carrierId.split(",");
             serviceLevelIdList  =   serviceLevelId.split(",");
             industryList        =   industry.split(","); 
         
             order.add(new Integer(fromCountryList.length));
             order.add(new Integer(fromLocationList.length));
             order.add(new Integer(toLocationList.length));
             order.add(new Integer(toCountryList.length));
             order.add(new Integer(carrierIdList.length));
             order.add(new Integer(serviceLevelIdList.length));
             order.add(new Integer(industryList.length)); 
             Collections.sort(order);
             maxLength              =  Integer.parseInt(order.get(6).toString());
             for(int j=0;j<maxLength;j++)
             {
                detailDOB          =   new QMSAttachmentDetailDOB();
                detailDOB.setAttachmentId(attachmentDOB.getAttachmentId());
                detailDOB.setDefaultFlag(defaultFlag);
                detailDOB.setConsoleType(consoleType);
                detailDOB.setQuoteType(quoteType);
                detailDOB.setShipmentMode(shipmentMode);
                if(j<fromCountryList.length)
                    detailDOB.setFromCountry(fromCountryList[j]);
                else 
                    detailDOB.setFromCountry("");
                if(j<fromLocationList.length)  
                    detailDOB.setFromLocation(fromLocationList[j]);
                else
                    detailDOB.setFromLocation("");
                if(j<toCountryList.length)  
                    detailDOB.setToCountry(toCountryList[j]);
                else
                    detailDOB.setToCountry("");
                if(j<toLocationList.length)       
                    detailDOB.setToLocation(toLocationList[j]);
                else
                    detailDOB.setToLocation("");
                if(j<carrierIdList.length)
                    detailDOB.setCarrierId(carrierIdList[j]);
                else
                    detailDOB.setCarrierId("");
                if(j<serviceLevelIdList.length)
                    detailDOB.setServiceLevelId(serviceLevelIdList[j]);
                else
                    detailDOB.setServiceLevelId("");
                if(j<industryList.length)
                    detailDOB.setIndustry(industryList[j]);
                else
                    detailDOB.setIndustry("");
                fieldsList.add(detailDOB);
            }
              home                    =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
              remote                  =  home.create();
           
             attachmentDOB.setQmsAttachmentDetailDOBList(fieldsList);
             if("Add".equalsIgnoreCase(operation))
               attachmentDOB.setQmsAttachmentFileDOBList(fileData);
            attachmentDOB.setInvalidate("F");
            if("Add".equalsIgnoreCase(operation))
              i                       =  remote.addAttachmentDtls(attachmentDOB);
              else 
              i                       =  remote.updateAttachmentDtls(attachmentDOB);
     }
    catch(Exception e)
    {
       e.printStackTrace();
       if("Add".equalsIgnoreCase(operation))
       {
         logger.error(FILE_NAME+"Error while inserting the values"+e.toString());
         i  = 0;
         throw new FoursoftException("An error occured while inserting the details 'Please try again");
       }
       else
       {
         logger.error(FILE_NAME+"Error while updating the values"+e.toString());
         i  = 0;
         throw new FoursoftException("An error occured while updating the details 'Please try again");
       }
      }
      return i;
  }
  /**
	 * This method helps in validating   values in QMSAttachmentEnterId.jsp and  forward to the next jsp
	 *
	 * @param attachmentDOB 	a QMSAttachmentDOB object that contains the values of fields in QMSAttachmentEnterId.jsp
	 * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an integer value to check whether the values in QMSAttachmentEnterId.jsp are valid or not.
   */
 private int validateFields(QMSAttachmentDOB attachmentDOB)throws  FoursoftException 
 {
   
    SetUpSessionHome         home            =   null;
    SetUpSession             remote          =   null;
    int k;
    try
    {
        home      =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
        remote    =  home.create();
              
        k      =   remote.validateFields(attachmentDOB);
    }
     catch(Exception e)
    {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while validating the attachmentId"+e.toString());
        throw new FoursoftException("An error occured while validating the attachmentId,Please try again");
	  }
    return  k;
 }
  /**
	 * This method helps in retrieving the details of a particular attachmentId.
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
   * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns a QMSAttachmentDOB object that contains the details of a particular attachmentId.
   */
   private  QMSAttachmentDOB viewAttachmentDtls(HttpServletRequest request,HttpServletResponse response)throws FoursoftException
  {
     HttpSession              session         =   null; 
     SetUpSessionHome         home            =   null;
     SetUpSession             remote          =   null;
     QMSAttachmentDOB         attachmentDOB   =   null;
     QMSAttachmentDOB         dob             =   null;
     try
     {
        session                =   request.getSession();
        attachmentDOB          = (QMSAttachmentDOB)session.getAttribute("QMSAttachmentDOB");
        home                   =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
        remote                 =  home.create();
        
        dob                    =  remote.viewAttachmentDtls(attachmentDOB);
      }
       catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while retrieving the values"+e.toString());
        throw new FoursoftException("An error occured while retrieving the values,Please try again");
	  }
    return  dob;
  }
   /**
	 * This method helps in deleting the attachmentId.
  * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
   * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an integer value to check whether the attachmentId is deleted or not.
   */
  private int deleteAttachmentdtls(HttpServletRequest request,HttpServletResponse response)throws FoursoftException
  {
     HttpSession              session          =   null; 
     SetUpSessionHome         home            =   null;
     SetUpSession             remote          =   null;
     QMSAttachmentDOB         attachmentDOB   =   null;
     int  i;
     try
     {
        session                =  request.getSession();
        attachmentDOB          = (QMSAttachmentDOB)session.getAttribute("QMSAttachmentDOB");
     
        home                   = (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
        remote                 =  home.create();
        
        i                      = remote.deleteAttachmentDtls(attachmentDOB);
      }
       catch(Exception e)
     {
        e.printStackTrace();
        i = 0;
        logger.error(FILE_NAME+"Error occurred while deleting the values"+e.toString());
        throw new FoursoftException("An error occured while deleting the values,Please try again");
	  }
      return  i;
  }
   /**
	 * This method helps in retreiving the details of all attachmentIds.
	 * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an ArrayList containing the details of all attachmentIds.
   */
//Commented & Added by subrahmanyam for the pbn id:203350
  //private ArrayList viewAllAttachmentDtls()throws FoursoftException
  private ArrayList viewAllAttachmentDtls(HttpServletRequest request )throws FoursoftException
  {
   
     SetUpSessionHome       home            =   null;
     SetUpSession           remote          =   null;
     ArrayList              viewList        =   new ArrayList();
   //Added by subrahmanyam for the pbn id:203350     
     HttpSession             session             = null;
     ESupplyGlobalParameters loginbean           = null;
     //203350
      try
     {   
    	// Added by subrahmanyam for the pbn id:203350    	  
    	 session       =  request.getSession();
    	loginbean     =  (ESupplyGlobalParameters)session.getAttribute("loginbean");
    	//203350
        home            =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
        remote          =  home.create();
        viewList        =  remote.viewAllAttachmentDtls(loginbean.getAccessType(),loginbean.getTerminalId());
      }
      catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while retreiving the values"+e.toString());
        throw new FoursoftException("An error occured while retreiving the values,Please try again");
	  }
      return  viewList;
  }
   /**
	 * This method helps in retrieving the PDF file.
	 * @param uniqueId long.
	 * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns a byte array containing the bytes of a particular PDF file.
   */
 private  byte[]   viewPDFFile(long uniqueId) throws FoursoftException
 {
     SetUpSessionHome       home                 =   null;
     SetUpSession           remote               =   null;
     InputStream            inputStream          =   null;
     byte[]   buffer                           =  new byte[9216];                
      try 
     {
        home            =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
        remote          =  home.create();
    
        buffer          =  remote.viewPDFFile(uniqueId);
        
   }
      catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while retreiving  the file"+e.toString());
        throw new FoursoftException("An error occured while retreiving  the file,Please try again");
	  }
      return  buffer;
 }
  /**
	 * This method helps in inserting the list of files of a particular attachmentId.
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an an ArrayList containing the list of files along with added files of a particular attachmentId. 
   */
 private ArrayList insertFile(HttpServletRequest request,HttpServletResponse response)throws FoursoftException
 {
     SetUpSessionHome         home                 =   null;
     SetUpSession             remote               =   null;
     QMSAttachmentFileDOB     fileDOB              =   null;
     MultipartParser		      mp                   =   null;
     Part					            part                 =   null;
     String				            name                 =   null;
     String				            attachmentId         =   null;
     FilePart				          filePart             =   null;
     ParamPart				        paramPart            =   null;
     File	                    fileRead             =   null;   
     String				            fileName             =   null;
     ArrayList                filesList            =   new ArrayList();   
     int i = 0;
     try
     {
        attachmentId  = request.getParameter("attachmentId");
        attachmentId = attachmentId.replace(',','&');
        mp            =   new MultipartParser(request, 10*1024*1024); // 1MB   
        while ((part = mp.readNextPart()) != null)
         {
              if (part.isFile())
            {
              filePart         =   (FilePart) part;
              fileName         =    filePart.getFileName();
               filePart.writeTo(new File("./"));
              // logger.info("filePart"+filePart.getFilePath()+"  "+filePart.isFile()+"   "+filePart.getFileName());
               fileRead         =   new File(filePart.getFileName());	
              //fileRead         =    new File(filePart.getFilePath());	
               // logger.info("fileRead"+fileRead.getAbsolutePath()+" "+fileRead.getAbsoluteFile()+" "+fileRead.canRead()+" "+fileRead.isFile()+" "+fileRead.length());
              fileDOB  =  new QMSAttachmentFileDOB();
              fileDOB.setFileName(fileName);
              fileDOB.setPdfFile(fileRead);
           }
                fileDOB.setAttachmentId(attachmentId);
        }
     
                 home            =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
                 remote          =  home.create();
    
                 filesList       =   remote.attachFile(fileDOB); 
     }
     catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while retreiving  the file"+e.toString());
        throw new FoursoftException("An error occured while retreiving  the file,Please try again");
     }
     return filesList;
 }
  /**
	 * This method helps in deleting the  files and retrieving the remaining files of a particular attachmentId.
	 *@param  uniqueId 	     String. 
	 * @param attachmentId 	 String. 
	 * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an ArrayList containing the remaining files of a particular attachmentId. 
   */
 private ArrayList deleteFile(String attachmentId,String  uniqueId)throws FoursoftException
 {
     SetUpSessionHome         home                =  null;
     SetUpSession             remote              =  null;
     HttpSession              session             =  null;
     QMSAttachmentFileDOB     fileDOB             =  null;
     String[]                 deleteIdsList       =  null;  
     ArrayList                listofFiles         =  new ArrayList();   
     ArrayList                dobList             =  new ArrayList();
     int i = 0;
     int delListLen	=0;
     try
     {
           deleteIdsList        = uniqueId.split(",");
           delListLen		=	deleteIdsList.length;
          for(int j=0;j<delListLen;j++)
          {
              fileDOB       =  new QMSAttachmentFileDOB();
              fileDOB.setId(Long.parseLong(deleteIdsList[j]));
               fileDOB.setAttachmentId(attachmentId);
              dobList.add(fileDOB);
          }
          
               home           =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
               remote         =  home.create();
               
               listofFiles    =   remote.deleteFile(dobList); 
     }
     catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while deleting  the file"+e.toString());
        throw new FoursoftException("An error occured while deleting  the file,Please try again");
     }
     return listofFiles;
 }
  /**
	 * This method helps in retrieving the list of files of a particular attachmentId.
	 * @param attachmentId 	 String. 
	 * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an ArrayList value that contains the list of files.
   */
 private ArrayList viewFile(String attachmentId)throws FoursoftException
 {
     SetUpSessionHome         home                 =   null;
     SetUpSession             remote               =   null;
     ArrayList                filesList            =   new ArrayList();   
     try
     {
        home            =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
        remote          =  home.create();
        
        filesList       =  remote.viewFile(attachmentId);
     }
      catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while retreiving  the file"+e.toString());
        throw new FoursoftException("An error occured while retreiving  the file,Please try again");
     }
     return filesList;
 }
  /**
	 * This method helps in invalidating the selected attachmentId.
 	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
   * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an integer value to invalidate the selected attachmentId.
   */
 private int invalidateAttachmentId(HttpServletRequest request,HttpServletResponse response)throws FoursoftException
 {
     SetUpSessionHome        home                 = null;
     SetUpSession            remote               = null;
     HttpSession             session              = null; 
     ArrayList               viewList             = null;
     ArrayList               dobList             = new ArrayList();
     String                  invalidate           = null;
     String                  attachmentId         = null;
     QMSAttachmentDOB        attachmentDOB        = null; 
     QMSAttachmentDOB        dob                  = null; 
     
     int noofcols;
     int i;
     int viewListSize			= 	0;
      try
     {
            session     = request.getSession();
            viewList    = (ArrayList)session.getAttribute("viewList");
            viewListSize	=	viewList.size();
           for(int j=0;j<viewListSize;j++)
           { 
              dob   = (QMSAttachmentDOB)viewList.get(j);
              attachmentId = dob.getAttachmentId();
              attachmentDOB  = new QMSAttachmentDOB();
              attachmentDOB.setAttachmentId(attachmentId);
              invalidate  =  request.getParameter("Invalidate"+j);
              if("0".equalsIgnoreCase(invalidate))
                 attachmentDOB.setInvalidate("T");
              else
                attachmentDOB.setInvalidate("F");
              dobList.add(attachmentDOB);
           }
         
           home            =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
           remote          =  home.create();
          
          i               =  remote.invalidateAttachmentId(dobList);
     }
      catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while retreiving  the file"+e.toString());
        throw new FoursoftException("An error occured while retreiving  the file,Please try again");
     }
     return i;
 }
  /**
	 * This method helps in retrieving attachmentIdList in quote
   * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
   * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an ArrayList containing the list of attachmentIds.
   */
 private ArrayList quoteAttachemntIdList(HttpServletRequest request,HttpServletResponse response)throws FoursoftException
 {
     SetUpSessionHome   home                    =  null;
     SetUpSession       remote                  =  null;
     ArrayList          quoteAttachemntIdList   =  null;  
     try
     {
           home                    =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
           remote                  =  home.create();
          quoteAttachemntIdList    =  remote.quoteAttachmentIdList();
     }
     catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error occurred while retreiving  the attachmentIds in quote"+e.toString());
        throw new FoursoftException("An error occured while retreiving  the file,Please try again");
     }
     return quoteAttachemntIdList;
 }
}
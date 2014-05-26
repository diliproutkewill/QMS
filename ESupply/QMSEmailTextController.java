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
 

 * File					: QMSEmailTextController.java
 * @author			: Kameswari
 * @date				: 10/01/07
 *


 *	This Controller is used to control the flow in the email text  module
 */
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.delegate.UserRoleDelegate;
import com.qms.setup.java.QMSEmailTextDOB;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import org.apache.log4j.Logger;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;

public class QMSEmailTextController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static       Logger logger       = null;
  private String       FILE_NAME           = "QMSEmailTextController.java";  
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
    logger  = Logger.getLogger(QMSEmailTextController.class);
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
      HttpSession   session               =   null;
      String        operation                   =   null;
      String        subOperation                =   null;    
      ErrorMessage	errorMessageObject		=   null;
      ArrayList			keyValueList			    =   new ArrayList();
      String        errorMessage          =   "";
      String        errorCode             =   "";
      String        nextNavigation        =   "";
      
       int  i;       
     
      try
     {
       operation      =   request.getParameter("Operation");
       subOperation   =   request.getParameter("subOperation");  
    
       if("Add".equalsIgnoreCase(operation))
       {
        if(subOperation == null)
        {
           nextNavigation    =  "QMSEmailTextMaster.jsp";
        }
        else
        {
           i                 =  addEmailTextDetails(request,response);           
           if(i>0)
           {
                  errorMessageObject  =   new ErrorMessage("Record successfully inserted",request.getContextPath()+"/QMSEmailTextMaster.jsp");
                  keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
           }
           else if(i<0)
           {
             
                 errorMessageObject   =   new ErrorMessage("Record Already Exists",request.getContextPath()+"/QMSEmailTextMaster.jsp");
                 keyValueList.add(new KeyValue("ErrorCode","RAE"));
           }
           else
           {
                 errorMessageObject   =   new ErrorMessage("Error occured while inserting the Record",request.getContextPath()+"/QMSEmailTextMaster.jsp");
                 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
           } 
             keyValueList.add(new KeyValue("Operation",operation)); 	
             errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
             nextNavigation      = "ESupplyErrorPage.jsp";
        }
      }
      else if("Modify".equalsIgnoreCase(operation)||"View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation))
      {
         if(subOperation == null)
        {
             nextNavigation    =   "QMSEmailTextMasterEnterId.jsp";
        }
        else if("View".equalsIgnoreCase(operation)&&"Continue".equalsIgnoreCase(subOperation))
        {
             nextNavigation    =   "QMSEmailTextMasterEnterId.jsp";
        }
        else if("Modify".equalsIgnoreCase(operation)&&"update".equalsIgnoreCase(subOperation))
        {
             i             =   updateEmailTextDetails(request,response);   
           if(i>0)
           {
                  errorMessageObject  =   new ErrorMessage("Record successfully updated",request.getContextPath()+"/QMSEmailTextMasterEnterId.jsp");
                  keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
           }
            else
           {
                 errorMessageObject   =   new ErrorMessage("Error occured while updating the Record",request.getContextPath()+"/QMSEmailTextMasterEnterId.jsp");
                 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
           } 
             keyValueList.add(new KeyValue("Operation",operation)); 	
             errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
             nextNavigation      = "ESupplyErrorPage.jsp"; 
        
        }
       else if(("Modify".equalsIgnoreCase(operation)||"View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation))&&"submit".equalsIgnoreCase(subOperation))
       {
            QMSEmailTextDOB   dob  = viewEmailTextDetails(request,response);
             if(dob!=null)
             {
                    session   =   request.getSession();
                    session.setAttribute("emailTextDOB",dob);   
                     nextNavigation    =   "QMSEmailTextMaster.jsp?Operation="+operation;
               
                   /* errorMessageObject   =   new ErrorMessage("Record not found",request.getContextPath()+"/QMSEmailTextMasterEnterId.jsp");
                    keyValueList.add(new KeyValue("ErrorCode","RNF"));
                    keyValueList.add(new KeyValue("Operation",operation)); 	
                    errorMessageObject.setKeyValueList(keyValueList);
                    request.setAttribute("ErrorMessage",errorMessageObject); 
                    nextNavigation      = "ESupplyErrorPage.jsp";*/
             }
             else
             {
                errorMessage  = "Terminal Id is invalid,please enter valid terminal id to continue";
                request.setAttribute("errorMessage",errorMessage);                   
                nextNavigation    =   "QMSEmailTextMasterEnterId.jsp";
             }
        }   
       
         else if("delete".equalsIgnoreCase(subOperation))
        {
            i          =       deleteEmailTextDetails(request,response);
            if(i>0)
            {
                  errorMessageObject  =   new ErrorMessage("Record successfully deleted",request.getContextPath()+"/QMSEmailTextMasterEnterId.jsp");
                  keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
            }
            else
           {
                 errorMessageObject   =   new ErrorMessage("Error occured while deleting the Record",request.getContextPath()+"/QMSEmailTextMasterEnterId.jsp");
                 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
           } 
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
        errorMessage         =  fs.getMessage(); 
       errorMessageObject   =  new ErrorMessage(errorMessage,"QMSEmailTextController");      
        keyValueList.add(new KeyValue("ErrorCode","RNF"));
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
	 * This method helps in adding emailtextdetails to the respective table in database
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @exception FourSoftException if the request for the addAttachmentDetails could not be handled.
	 * @returns an integer value to check whether the values are inserted or not in the respective table in database
   */
  private int addEmailTextDetails(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
     SetUpSessionHome   home             =   null;
     SetUpSession       remote           =   null;
     QMSEmailTextDOB    dob              =   null;
     HttpSession        session          =   null;
     String             quoteType        =   request.getParameter("quoteType");
     String             emailText        =   request.getParameter("emailText");	
     int  i;   
     try
     {
      session                              =  request.getSession();
      ESupplyGlobalParameters   loginbean  =  (ESupplyGlobalParameters)session.getAttribute("loginbean");  
      String  createdBy                    =  loginbean.getUserId();
      String  terminalId                   =  loginbean.getTerminalId();  
      dob                                  =  new QMSEmailTextDOB();
      home                                 =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
      remote                               =  home.create();
    
      dob.setQuoteType(quoteType);
      dob.setEmailText(emailText);
      dob.setCreatedBy(createdBy);
      dob.setTerminalId(terminalId);
      
		  i                                 =  remote.addEmailTextDtls(dob);
      
      
    } 
    catch(Exception e)
	  {
       e.printStackTrace();
       logger.error(FILE_NAME+"Error while inserting the values"+e.toString());
       i  = 0;
       throw new FoursoftException("An error occured while inserting the details 'Please try again");
	  }
    return i;
  }
   /**
	 * This method helps in retrieving  emailtextdetails of a particular terminalid and quotetype
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
	 * @exception FourSoftException if the request for the addAttachmentDetails could not be handled.
	 * @returns QMSEmailTextDOB that contains the emailtextdetails of a particular terminalid and quotetype.
   */
   private QMSEmailTextDOB viewEmailTextDetails(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
      SetUpSessionHome   home             =   null;
      SetUpSession       remote           =   null;
      QMSEmailTextDOB    dob              =   null;
      QMSEmailTextDOB   emailTextDOB      =   null;
      HttpSession        session          =   null;
      String             accessType       =   request.getParameter("accessType"); 
      String             terminalId       =   request.getParameter("terminalId"); 
      String             quoteType        =   request.getParameter("quoteType");

        try
      {
             session                              =  request.getSession();
             ESupplyGlobalParameters   loginbean  =  (ESupplyGlobalParameters)session.getAttribute("loginbean");
             dob                                  =  new QMSEmailTextDOB();
             home                                 =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
             remote                               =  home.create();
             
          
             dob.setQuoteType(quoteType);
             dob.setTerminalId(terminalId);
             dob.setAccessType(accessType);
             dob.setModifiedBy(loginbean.getUserId());
             dob.setLoginTerminal(loginbean.getTerminalId());//subrahmanyam
             dob.setLoginAccessType(loginbean.getAccessType());
             emailTextDOB   =  remote.viewEmailTextDtls(dob);
             
          
                  
      }
       catch(Exception e)
			 {
		      e.printStackTrace();
          logger.error(FILE_NAME+"Error in getting the values"+e.toString());
           throw new FoursoftException("Record not found");
      }
      
          return emailTextDOB;
  }
  /**
	 * This method helps in modifying  the emailtext for a particular terminalid and quotetype.
  * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
   * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an integer value to check whether the emailtext is modified or not.
   */
   private int updateEmailTextDetails(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
     SetUpSessionHome   home             =   null;
     SetUpSession       remote           =   null;
     QMSEmailTextDOB    dob              =   null;
     HttpSession        session          =   null;
     String             emailText        =   request.getParameter("emailText"); 
     int  i;   
     try
       {
          session                        =  request.getSession();
          home                           =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
          remote                         =  home.create();
       
          dob                            = (QMSEmailTextDOB)session.getAttribute("emailTextDOB");
          dob.setEmailText(emailText);
          i                              =  remote.updateEmailTextDtls(dob);
         session.removeAttribute("emailTextDOB");
    } 
    catch(Exception e)
	  {
       e.printStackTrace();
       logger.error(FILE_NAME+"Error occurred while updating the values"+e.toString());
       i  = 0;
       throw new FoursoftException("An error occured while updating the details 'Please try again");
	  }
    return i;
  }
   /**
	 * This method helps in deleting the emailtext for a particular terminalid and quotetype.
  * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the response the client has made of the servlet
   * @exception FourSoftException if the request for the validateFields could not be handled.
	 * @returns an integer value to check whether the emailtext is deleted or not.
   */
  private int deleteEmailTextDetails(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
      SetUpSessionHome   home              =   null;
      SetUpSession       remote            =   null;
      QMSEmailTextDOB    emailTextDOB      =   null;
      HttpSession        session           =   null;
      String             nextNavigation    =   "";
      int  i;
      try
      {
           session                              =  request.getSession();
           home                                 =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
           remote                               =  home.create();
           emailTextDOB  =(QMSEmailTextDOB)session.getAttribute("emailTextDOB");
           
           i                                    =  remote.deleteEmailTextDtls(emailTextDOB);
     
           session.removeAttribute("emailTextDOB");       
      }
      catch(Exception e)
	   {
       e.printStackTrace();
       logger.error(FILE_NAME+"Error occurred while updating the values"+e.toString());
       i  = 0;
       throw new FoursoftException("An error occured while deleting the record 'Please try again");
	  }
       return i;
  }
}

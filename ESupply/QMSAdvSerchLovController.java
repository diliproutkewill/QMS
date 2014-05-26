/*

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSAdvSerchLovController.java
Product Name	: QMS
Module Name		: Advanced LOVs
Task		      : Advanced LOVs
Date started	: 	
Date Completed	: 
Date modified	:  
Author    		: Madhusudhan Reddy .Y
Description		: 
Actor           :
Related Document: 
*/

import com.qms.setup.java.QMSAdvSearchLOVDOB;
import java.util.Hashtable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.ejb.EJBException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.sql.Timestamp;
import java.io.IOException;
//import javax.naming.InitialContext;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;

import com.foursoft.esupply.common.java.LookUpBean;

public class QMSAdvSerchLovController extends HttpServlet 
{
  
  private static final String 	   FILE_NAME 		= "QMSAdvSerchLovController.java";
  private static Logger logger = null;
  
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSAdvSerchLovController.class);
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
      ErrorMessage			        errorMessageObject= null;
      ArrayList			            keyValueList	    = new ArrayList();
      ArrayList                 valuesOfList      = null;
      ArrayList                 addressList       = null;//@@Added by Kameswari for enhancements
      SetUpSession              remote            = null;
      SetUpSessionHome          home              = null;
      String                    nextNavigation    = null;
      String                    operation         = null;
      HttpSession	              session           = null;
      String                    lovWhere          = null;
      String                    pg                = null;
      String                    lovid             = null;
      String                    tabArray          = null;
      String                    formArray         = null;
      String                    sortType          = null;
      String                    sortField         = null;
      String                    terminalId        = null;
      String                    designationID     = null;
      String                    shipmentMode      = null;
      String                    accessLevel       = null;
      String                    serch             = null;
      String                    buyRatesPermission  = null;//@@Added by Kameswari for the WPBN issue-26514
      String                    customerId        = null;
     // String                    forwardFile       = null;
      //StringBuffer              sb                = null;
      QMSAdvSearchLOVDOB        advSearchDOB      = null;
      ESupplyGlobalParameters   loginbean         = null;
       String localTerminal   =    null; ///added by VLAKSHMI on 22/05/2009
		   String localAcceslevel =    null;///added by VLAKSHMI on 22/05/2009
       String                    accessLocal      = null;
       String 	isMultiQuote					  = null;//Added by Rakesh
      //String    temp=;	
      try
      {
        session   = request.getSession();
        
        loginbean = (ESupplyGlobalParameters)session.getAttribute("loginbean");
        
        lovWhere  = request.getParameter("lovWhere");
        serch     = request.getParameter("search");
   
       // lovWhere  = request.getParameter("search");
        pg        = request.getParameter("pg");
        lovid     = request.getParameter("lovid");
        tabArray  = request.getParameter("tabArray");
        formArray = request.getParameter("formArray");
        sortType  = request.getParameter("sortType");
        sortField = request.getParameter("sortField");
         designationID = request.getParameter("designationID");
        operation = request.getParameter("operation");
        shipmentMode=request.getParameter("shipmentMode");
        terminalId= loginbean.getTerminalId();
        localTerminal		=   request.getParameter("localTerminal");
			  localAcceslevel =  request.getParameter("localAcceslevel");
		isMultiQuote	=  request.getParameter("multiQuote");//Added by Rakesh on 16-03-2011
        
        if("LICENSEE".equalsIgnoreCase(loginbean.getAccessType()))
          accessLevel = "L";
        else if("HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
          accessLevel = "H";
        else if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
          accessLevel = "A";
        else   
          accessLevel = "O";
          ///added by VLAKSHMI on 22/05/2009
         if("LICENSEE".equalsIgnoreCase(localAcceslevel))
          accessLocal = "L";
        else if("HO_TERMINAL".equalsIgnoreCase(localAcceslevel))
          accessLocal = "H";
        else if("ADMN_TERMINAL".equalsIgnoreCase(localAcceslevel))
          accessLocal = "A";
        else   
          accessLocal = "O";  
          //end of on 22/05/2009
        if(serch!=null)
          serch     = serch.replaceAll("~","%");
      
      Hashtable accessList  =  (Hashtable)session.getAttribute("accessList");
      
      //@@setting the buy rates permissions flag based on user role permissions.
      if(accessList.get("10605")!=null)
        buyRatesPermission  = "Y";
      else
        buyRatesPermission  = "N";
        
        if(pg==null)
          pg="1";
        advSearchDOB  = new QMSAdvSearchLOVDOB();
        advSearchDOB.setLovWhere(serch);
        advSearchDOB.setPageNo(pg);
        advSearchDOB.setLovId(lovid);
        advSearchDOB.setTabArray(tabArray);
        advSearchDOB.setFormArray(formArray);
        advSearchDOB.setSortType(sortType);
        advSearchDOB.setSortFeild(sortField);
        advSearchDOB.setTerminalId(terminalId);
        advSearchDOB.setOperation(operation);
        advSearchDOB.setDesignationId(designationID);
        advSearchDOB.setShipmentMode(shipmentMode);
        advSearchDOB.setAccessLevel(accessLevel);
        advSearchDOB.setEmpId(loginbean.getEmpId());//@@Added by Kameswari for the WPBN issue-26514
        advSearchDOB.setBuyRatesPermission(buyRatesPermission);//@@Added by Kameswari for the WPBN issue-26514
       advSearchDOB.setLocalAcceslevel(accessLocal);
       advSearchDOB.setLocalTerminal(localTerminal);
       advSearchDOB.setMultiQuote(isMultiQuote);//Added by Rakesh on 16-03-2011
        home			    =	  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
        remote			  =	  (SetUpSession)home.create();
        valuesOfList  =   remote.getAdvSerchLov(advSearchDOB);
    
        session.setAttribute("lovData",valuesOfList);
      
       if("where CUSTOMERID LIKE '%'".equalsIgnoreCase(serch))
        {
             addressList   =   remote.getCustomerAddress();//@@Added by Kameswari for the WPBN issue-61314
          session.setAttribute("addressList",addressList);//@@Added by Kameswari for the WPBN issue-61314

        }
        if(lovWhere!=null)
          lovWhere  = lovWhere.replaceAll("%","~");
        if(serch!=null)
          serch  = serch.replaceAll("%","~");
        
        
        doFileDispatch(request,response,"qms/ListOfValues.jsp?formArray="+formArray+"&tabArray="+tabArray+"&pg="+pg+"&lovid="+lovid+"&sortType="+sortType+"&sortField="+sortField+"&operation="+operation+"&designationID="+designationID+"&search="+serch+"&shipmentMode="+shipmentMode+"&lovWhere"+lovWhere);
    
      }
      catch(Exception e)
      {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doPost()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
      }
  }
 /**
  * Dispatch the respective jsp
  */
	public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)
			throws IOException, ServletException
	{
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doFileDispatch() ", " Exception in forwarding ---> "+ ex.toString());
      logger.error(FILE_NAME+ " [doFileDispatch() "+" Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}
}
/*

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSSellRateController.java
Product Name	: QMS
Module Name		: Recommended Sell Rates
Task		    : Adding/View/Modify/Invalidate Industry
Date started	: 26-07-2005 	
Date Completed	: 
Date modified	:  
Author    		: Madhusudhan Reddy .Y
Description		: 
Actor           :
Related Document: CR_DHLQMS_1004
*/

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
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import com.qms.operations.sellrates.java.QMSBoundryDOB;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSessionHome;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSession;
import com.foursoft.esupply.common.java.LookUpBean;

public class QMSSellRateController extends HttpServlet 
{
  
  private static final String 	   FILE_NAME 		= "QMSSellRateController.java";
  private static Logger logger = null;
  
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSSellRateController.class);
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

		String 						        operation 			      = 	null;
		String 						        subOperation 		      = 	null;
		String                    			nextNavigation        =   null;
		ESupplyGlobalParameters				loginBean			        = 	null;
		QMSSellRatesDOB				    	sellRatesDOB		      =	  null;
		ErrorMessage			        		errorMessageObject    =   null;
		ArrayList			            		keyValueList	        =   new ArrayList();
		HttpSession 			        		session 		          = 	request.getSession();
		
		try
		{
			sellRatesDOB						=   new QMSSellRatesDOB();
			operation							=	  request.getParameter("Operation");
			subOperation						=	  request.getParameter("subOperation");
			//System.out.println("operationoperationoperationoperation :: "+operation);
			//System.out.println("subOperationsubOperationsubOperation :: "+subOperation);
			loginBean									  = 	(ESupplyGlobalParameters)session.getAttribute("loginbean");
			
			nextNavigation		=	"QMSSellRateController?Operation="+operation+"&subOperation=Enter";

			if("Add".equals(operation) && "Enter".equals(subOperation))
			{
				doFileDispatch(request,response,"etrans/QMSRecommendedSellRate.jsp");
			}
			if("Modify".equals(operation) && "Enter".equals(subOperation))
			{
        doFileDispatchOfModify(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
			}
			else if(("Add".equals(operation) || "Modify".equals(operation)) && "Values".equals(subOperation))
			{
				doGetBuyRates(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
			}
			else if(("Add".equals(operation) && "Add".equals(subOperation)) || ("Modify".equals(operation) && "Modify".equals(subOperation)))
			{
				doGetSellRates(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
			}
			else if(("Add".equals(operation) || "Modify".equals(operation)) && "Insert".equals(subOperation))
			{
				insertSellRates(request,response,loginBean,operation,nextNavigation);
			}
			else if(("View".equals(operation) || "Invalidate".equals(operation)) && "Enter".equals(subOperation))
			{
				doFileDispatch(request,response,"etrans/QMSRecommendedSellRateEnter.jsp");
			}
			else if(("View".equals(operation) && "View".equals(subOperation)) || ("Invalidate".equals(operation) && "Invalidate".equals(subOperation)))
			{
				doGetSellRateValuesOfView(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
			}
			else if("Invalidate".equals(operation) && "ChangeValue".equals(subOperation))
			{
				doChangeStatus(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
			}
			 else if("Acceptance".equals(operation) && "Enter".equals(subOperation))
			{
				doFileDispatch(request,response,"etrans/QMSRecommendedSellRatesAcceptanceEnter.jsp");
			}
			else if("Acceptance".equals(operation) && "Acceptance".equals(subOperation))
			{
				doGetAcceptanceValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
			}
			else if("Acceptance".equals(operation) && "Modify".equals(subOperation))
			{
				doDisplayAcceptanceValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
      }
      else if("Acceptance".equals(operation) && "Insert".equals(subOperation))
			{
          doPageVlues(request,response,operation,nextNavigation,sellRatesDOB);
          ArrayList listValues				=	(ArrayList)session.getAttribute("HeaderValues");
          session.removeAttribute("HeaderValues");
          doAcceptanceValuesCal(request,response,loginBean,listValues,operation,sellRatesDOB,nextNavigation);
      }
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
private void doFileDispatchOfModify(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
	ErrorMessage			        errorMessageObject              =   null;
	ArrayList			            keyValueList	                  =   new ArrayList();
	QMSSellRatesSessionHome		sellRatesHome                   =   null;
	QMSSellRatesSession				sellRatesRemote                 =   null;
	ArrayList			            terminalList	                  =   null;
	HttpSession				        session			                    =	  request.getSession();
	try
	{  
		
 
    //System.out.println("************Before Look up****************");
		sellRatesHome			  =	  (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
		sellRatesRemote			=	  (QMSSellRatesSession)sellRatesHome.create();

		terminalList			=   sellRatesRemote.getTerminalIds(loginBean,operation);
    
		session.setAttribute("TerminalValues",terminalList);
		doFileDispatch(request,response,"etrans/QMSRecommendedSellRatesModify.jsp");
	}
	catch(Exception e)
	{
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Error while doGetBuyRates()",nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		doFileDispatch(request,response,"ESupplyErrorPage.jsp");
	}
}
private void doGetBuyRates(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
	ErrorMessage			        errorMessageObject                =   null;
	ArrayList			            keyValueList	                  =   new ArrayList();
	//InitialContext 			    initialContext		              =   null;
	QMSSellRatesSessionHome			sellRatesHome                     =   null;
	QMSSellRatesSession				sellRatesRemote                   =   null;
	ArrayList			            headerValues	                  =   null;
	ArrayList			            headerVal	                      =   null;
	StringBuffer					errorMassege                      =   null;
	HttpSession				        session			                  =	  request.getSession();
	try
	{  
		insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
		//System.out.println("************Before Look up****************");
		//initialContext 		=	  new InitialContext();
		sellRatesHome			=	  (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
		sellRatesRemote			=	  (QMSSellRatesSession)sellRatesHome.create();

		errorMassege			=   sellRatesRemote.validateSellRatesHdrData(sellRatesDOB);
    doPageVlues(request,response,operation,nextNavigation,sellRatesDOB);
		if(errorMassege.length() > 0)
		{
			headerVal = new ArrayList();
      
			//System.out.println("************After Look up**************** ");
			request.setAttribute("Errors",errorMassege.toString());
			headerVal.add(sellRatesDOB);
			headerVal.add(headerValues);
			session.setAttribute("HeaderValues",headerVal);
		}
		else
		{
			headerValues      =   sellRatesRemote.getSellRatesValues(sellRatesDOB,loginBean,operation);   
			//System.out.println("************After Look up**************** "+headerValues.size());
			session.setAttribute("HeaderValues",headerValues);
		}
		if("Modify".equals(operation))
		  doFileDispatch(request,response,"etrans/QMSRecommendedSellRatesModify.jsp");
		else if("Add".equals(operation))
		  doFileDispatch(request,response,"etrans/QMSRecommendedSellRate.jsp");
	}
	catch(Exception e)
	{
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Error while doGetBuyRates()",nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		doFileDispatch(request,response,"ESupplyErrorPage.jsp");
	}
}
private void doGetSellRates(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    
    HttpSession				        session			                      =	  request.getSession();
    
    QMSSellRatesSessionHome   sellRatesHome                     =   null;
    QMSSellRatesSession       sellRatesRemote                   =   null;
    ArrayList			            sellRatesList	                    =   null;
    ArrayList			            sellRatesdtl	                    =   null;
    ArrayList                 listValues                        =   null;
   
    try
    {  
    
      //System.out.println("************Before Look up****************");
        insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
        doPageVlues(request,response,operation,nextNavigation,sellRatesDOB);
        listValues				=	(ArrayList)session.getAttribute("HeaderValues");
       // session.removeAttribute("HeaderValues");
        if("1".equals(sellRatesDOB.getShipmentMode()) || ("2".equals(sellRatesDOB.getShipmentMode()) && "LCL".equals(sellRatesDOB.getConsoleType())) || ("4".equals(sellRatesDOB.getShipmentMode()) && "LTL".equals(sellRatesDOB.getConsoleType())))
        {
          if(sellRatesDOB!=null && "FLAT".equalsIgnoreCase(sellRatesDOB.getWeightBreak()))
          {
            doGetSellRatesCal(request,response,listValues,loginBean,operation,sellRatesDOB,nextNavigation);
          }
          else if(sellRatesDOB!=null && "SLAB".equalsIgnoreCase(sellRatesDOB.getWeightBreak()))
          {
            doGetSellRatesCal(request,response,listValues,loginBean,operation,sellRatesDOB,nextNavigation);
          }
          else if(sellRatesDOB!=null && "LIST".equalsIgnoreCase(sellRatesDOB.getWeightBreak()))
          {
            doGetSellRatesCal(request,response,listValues,loginBean,operation,sellRatesDOB,nextNavigation);
          }
        }
        else
        {
          doGetSellRatesCal(request,response,listValues,loginBean,operation,sellRatesDOB,nextNavigation);
        }
        
       // System.out.println("************After Look up**************** ");
       
        
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doGetSellRates()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
private void doChangeStatus(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    String[]                  flag                              =   null;
    String[]                  sellRatesId                       =   null;
    
    String[]                  orign                             =   null;
    String[]                  destination                       =   null;
    String[]                  carrier                           =   null;
    String[]                  serviceLeve                       =   null;
    String[]                  frequency                         =   null;
    
    QMSSellRatesDOB           sellDob                           =   null;
    QMSSellRatesDOB           detlSellRatesDob                  =   null;
    QMSSellRatesSessionHome   sellRatesHome                     =   null;
    QMSSellRatesSession       sellRatesRemote                   =   null;
    String                    sellRatesdtl                      =   null;
    ArrayList			            valueList	                        =   null;
    ArrayList			            listValues	                      =   null;
    Iterator			            itr						                    =	  null;
    HashMap				            mapValues				                  =	  null;
    HashMap				            totalMapValues				            =	  null;
    String				            keyHash					                  =	  null;
    try
    {
        doInvalidatePageVlues(request,response,operation,nextNavigation,sellRatesDOB);
        //flag               =     request.getParameterValues("mMinimumRates"); 
       // sellRatesId        =     request.getParameterValues("sellRatesId"); 
        
        //orign              =     request.getParameterValues("origin");
       // destination        =     request.getParameterValues("destination");
        //carrier            =     request.getParameterValues("carrier");
       // serviceLeve        =     request.getParameterValues("serviceLevel");
        //frequency          =     request.getParameterValues("frequency");
        listValues				=	(ArrayList)session.getAttribute("HeaderValues");
        session.removeAttribute("HeaderValues");
        valueList          =    new ArrayList();
        //int flagSize  = flag.length;
        //System.out.println("flagSizeflagSizeflagSizeflagSize in con :: "+flagSize);
        mapValues				        =	  (HashMap)session.getAttribute("HashList");
        session.removeAttribute("HashList");
        totalMapValues          =   (HashMap)session.getAttribute("TotalHashList");
        session.removeAttribute("TotalHashList");
        
        itr				              =	  (totalMapValues.keySet()).iterator();
        while(itr.hasNext())
        {
          keyHash	=	(String)itr.next();
          sellDob   =  new QMSSellRatesDOB();
          //System.out.println("keyHashkeyHashkeyHashkeyHashkeyHashkeyHash :: "+keyHash);
          detlSellRatesDob  =   (QMSSellRatesDOB)totalMapValues.get(keyHash);
          
          //System.out.println("Invalidate in controller :: "+detlSellRatesDob.getInvalidate()+"Buyrate Id in controller ::"+detlSellRatesDob.getBuyRateId());
          sellDob.setRec_buyrate_id(detlSellRatesDob.getRec_buyrate_id());
          sellDob.setBuyRateId(detlSellRatesDob.getBuyRateId());
          sellDob.setVersionNo(detlSellRatesDob.getVersionNo());//@@Added for the WPBN issue-146448 on 23/12/08
          sellDob.setOrigin(detlSellRatesDob.getOrigin());
          sellDob.setDestination(detlSellRatesDob.getDestination());
          sellDob.setCarrier_id(detlSellRatesDob.getCarrier_id());
          sellDob.setServiceLevel(detlSellRatesDob.getServiceLevel());
          sellDob.setFrequency(detlSellRatesDob.getFrequency());
          sellDob.setLanNumber(detlSellRatesDob.getLanNumber());
          sellDob.setInvalidate(detlSellRatesDob.getInvalidate());
          valueList.add(sellDob);
        }
        sellRatesHome		  =	  (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
        sellRatesRemote	  =	  (QMSSellRatesSession)sellRatesHome.create();
        sellRatesdtl      =   sellRatesRemote.updateInvalidate(valueList); 
        
        //System.out.println("************After Look up**************** "+sellRatesdtl);
        if(sellRatesdtl!=null)
        {
            errorMessageObject = new ErrorMessage("Successfully Updated Invalidate status",nextNavigation); 
            keyValueList.add(new KeyValue("ErrorCode","RSU")); 	
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject);
            doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
        else
        {
            errorMessageObject = new ErrorMessage("Error while inseration",nextNavigation); 
            keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject);
            doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doChangeStatus()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
private void doGetSellRateValuesOfView(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    QMSSellRatesSessionHome   sellRatesHome                     =   null;
    QMSSellRatesSession       sellRatesRemote                   =   null;
    ArrayList			            headerValues	                    =   null;
    ArrayList			            headerVal	                        =   null;
    ArrayList                 sellRatesList                     =   null;
    StringBuffer              errorMassege                      =   null;
    try
    {  
        insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
        sellRatesHome		  =	  (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
        sellRatesRemote	  =	  (QMSSellRatesSession)sellRatesHome.create();
    
        errorMassege      =   sellRatesRemote.validateSellRatesHdrData(sellRatesDOB);
        if("Invalidate".equals(operation))
          doInvalidatePageVlues(request,response,operation,nextNavigation,sellRatesDOB);
        
        if(errorMassege.length() > 0)
        {
          headerVal = new ArrayList();
          //System.out.println("************After Look up**************** doGetSellRateValuesOfView");
         
          request.setAttribute("Errors",errorMassege.toString());
          headerVal.add(sellRatesDOB);
          session.setAttribute("HeaderValue",headerVal);
        }
        else
        {
            sellRatesList     = new ArrayList();
            sellRatesList.add(sellRatesDOB);
            headerValues      =   sellRatesRemote.getSellRatesOfValues(sellRatesDOB,loginBean,operation);   
            //System.out.println("************After Look up****************doGetSellRateValuesOfView "+headerValues.size());
            sellRatesList.add(headerValues);
            session.setAttribute("HeaderValues",sellRatesList);
        }
    if(errorMassege.length() > 0)
    { 

        doFileDispatch(request,response,"etrans/QMSRecommendedSellRateEnter.jsp");
    }
    else
    {
     if("View".equals(operation))   
      doFileDispatch(request,response,"etrans/QMSRecommendedSelRatesView.jsp");
     else
      doFileDispatch(request,response,"etrans/QMSRecommendedSelRatesInvalidate.jsp");        
        }
     }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doGetSellRateValuesOfView()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
private void doGetSellRatesCal(HttpServletRequest request,HttpServletResponse response,ArrayList listValues,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDob,String nextNavigation)throws IOException,ServletException
{

      ErrorMessage			        errorMessageObject                =   null;
      HttpSession				        session			                      =	  request.getSession();
      QMSSellRatesDOB           sellRatesDob1                     =   null;
      QMSSellRatesDOB           sellRatesDob2                     =   null;
      ArrayList			            sellRatesList	                    =   null;
      ArrayList                 arrayList                         =   null;
      ArrayList                 fslListValues                     =   null;  
      ArrayList			            keyValueList	                    =   new ArrayList();
      String[]                  marign                            =   null;
      String[]                  surcharge                            =   null;
      String[]                  checkBoxValue                     =   null;
      String[]                  noteValue                         =   null;
      double                    minAbValue                        =   0.0;
      double                    flatAbValue                       =   0.0;
     // double	                  commonOvrMarign                 =   0.0;
      Iterator			            itr						                    =	  null;
      HashMap				            mapValues				                  =	  null;
      String				            keyHash					                  =	  null;
      //System.out.println("44444444444444444444444:: in Bean ");
      //System.out.println("44444444444444444444444:: in Bean ");
      try
      {
          sellRatesList           =   new ArrayList();
          arrayList               =   new ArrayList();
					fslListValues			      =	  (ArrayList)listValues.get(1);          
          //sellRatesList.add(sellRatesDob);
          marign            =     request.getParameterValues("margenValue");
          surcharge            =     request.getParameterValues("surchargeValue");
    
          
         // checkBoxValue           =   request.getParameterValues("mfValues");
         // noteValue               =   request.getParameterValues("notes");
          mapValues				        =	  (HashMap)session.getAttribute("HashList");
					itr				              =	  (mapValues.keySet()).iterator();
          
          //int fslListValuesSize   =   checkBoxValue.length;
          //System.out.println("in controller fslListValuesSizefslListValuesSizefslListValuesSizefslListValuesSize : "+fslListValuesSize);
         // for(int i=0;i<fslListValuesSize;i++)
         // {
         int i=0;
          while(itr.hasNext())
          {
                keyHash	=	(String)itr.next();
                //System.out.println("keyHashkeyHashkeyHashkeyHashkeyHash in controller: "+keyHash);
            
                sellRatesDob2 = new QMSSellRatesDOB();
                //sellRatesDob1	=	(QMSSellRatesDOB)fslListValues.get(Integer.parseInt(checkBoxValue[i]));
                sellRatesDob1	= (QMSSellRatesDOB)mapValues.get(keyHash);
                //System.out.println("sellRatesDob1sellRatesDob1sellRatesDob1sellRatesDob1: "+sellRatesDob1);
                //System.out.println("sellRatesDob1.getCarrier_id()sellRatesDob1.getCarrier_id(): "+sellRatesDob1.getCarrier_id());
                sellRatesDob2.setCarrier_id(sellRatesDob1.getCarrier_id());
                sellRatesDob2.setOrigin(sellRatesDob1.getOrigin());
                sellRatesDob2.setOriginCountry(sellRatesDob1.getOriginCountry());
                sellRatesDob2.setDestination(sellRatesDob1.getDestination());
                sellRatesDob2.setDestinationCountry(sellRatesDob1.getDestinationCountry());
                sellRatesDob2.setServiceLevel(sellRatesDob1.getServiceLevel());
                sellRatesDob2.setTransitTime(sellRatesDob1.getTransitTime());
                sellRatesDob2.setFrequency(sellRatesDob1.getFrequency());
                //System.out.println("getLanNumbergetLanNumbergetLanNumber :: "+sellRatesDob1.getLanNumber());
                sellRatesDob2.setLanNumber(sellRatesDob1.getLanNumber());
                //sellRatesDob2.setNoteValue(noteValue[i]);
                sellRatesDob2.setNoteValue(sellRatesDob1.getNotes());
                sellRatesDob2.setLBound(sellRatesDob1.getLBound());
                sellRatesDob2.setUBound(sellRatesDob1.getUBound());
                sellRatesDob2.setChargeInr(sellRatesDob1.getChargeInr());
                sellRatesDob2.setBuyRateId(sellRatesDob1.getBuyRateId());
                sellRatesDob2.setVersionNo(sellRatesDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 23/12/08
                sellRatesDob2.setWeightBreaks(sellRatesDob1.getWeightBreaks());
                sellRatesDob2.setAllWeightBreaks(sellRatesDob1.getAllWeightBreaks());
                //sellRatesDob2.setBuyChargeRates(sellRatesDob1.getChargeRatesValues());
                sellRatesDob2.setBuyChrRates(sellRatesDob1.getChargeRates());
               sellRatesDob2.setRateDescription(sellRatesDob1.getRateDescription());
               sellRatesDob2.setExtNotes(sellRatesDob1.getExtNotes());//Modified by Mohan for Issue No.219976 on 2-11-2010
                //double[] chargesValue	    =	sellRatesDob1.getChargeRatesValues();
               sellRatesDob2.setSurChargeCurency(sellRatesDob1.getSurChargeCurency());//Added by Kishore For SurCharge Currency
                String[] chargesValue	    =	sellRatesDob1.getChargeRates();
                String[] wtBreakValue     = sellRatesDob1.getAllWeightBreaks();
                int chargesLength		      =	chargesValue.length;
                String[]  chargeCal       = new String[chargesLength];
                double[]  commonOvrMarign = new double[chargesLength];
                String[]  rateDescription	=	new String[sellRatesDob1.getRateDescription().length];
                rateDescription				=	sellRatesDob1.getRateDescription();
                
                //System.out.println("sellRatesDob.getWeightBreak()sellRatesDob.getWeightBreak() :: "+sellRatesDob.getWeightBreak());
    
      if("1".equals(sellRatesDob.getShipmentMode()) || ("2".equals(sellRatesDob.getShipmentMode()) && "LCL".equals(sellRatesDob.getConsoleType())) || ("4".equals(sellRatesDob.getShipmentMode()) && "LTL".equals(sellRatesDob.getConsoleType())))
      {
         for(int j=0;j<chargesLength;j++)
             {
            //@@ Modified by subrahmanyam for the CR-219973
        	 //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
              if("FSBASIC".equalsIgnoreCase(wtBreakValue[j])||"FSMIN".equalsIgnoreCase(wtBreakValue[j])
              ||"FSKG".equalsIgnoreCase(wtBreakValue[j])||"SSBASIC".equalsIgnoreCase(wtBreakValue[j])
              ||"SSMIN".equalsIgnoreCase(wtBreakValue[j])||"SSKG".equalsIgnoreCase(wtBreakValue[j])
              ||"CAFMIN".equalsIgnoreCase(wtBreakValue[j])||"CAF%".equalsIgnoreCase(wtBreakValue[j])
              ||"BAFMIN".equalsIgnoreCase(wtBreakValue[j])||"BAFM3".equalsIgnoreCase(wtBreakValue[j])
              ||"PSSMIN".equalsIgnoreCase(wtBreakValue[j]) ||"PSSM3".equalsIgnoreCase(wtBreakValue[j])
              ||"SURCHARGE".equalsIgnoreCase(wtBreakValue[j])||"CSF".equalsIgnoreCase(wtBreakValue[j]) || !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))
              {
                if("-".equalsIgnoreCase(chargesValue[j]))
                {
                    chargeCal[j]="-";
                }
                else
                {
                   if(surcharge!=null)
                   {
                        if("A".equals(sellRatesDob.getMarginType()))
                        {
                          chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(surcharge[0]));
                        }
                        else
                        {
                          chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(surcharge[0])/100)));
                        }
                   sellRatesDob2.setAbperWithSurcharge(Double.parseDouble(surcharge[0]));
                   }
                   else
                   {
                     chargeCal[j]=""+Double.parseDouble(chargesValue[j]);
                     sellRatesDob2.setAbperWithSurcharge(0.0);
                   }
                 }
                
               }
              else
              {
                 if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
                 {
                  if(sellRatesDob!=null && "Y".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
                  { 
                      
                
                    if("-".equalsIgnoreCase(chargesValue[j]))
                      {
                          chargeCal[j]="-";
                      }
                      else
                      {
                        chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                      }
                sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0])); //@@Added by govind for the RSR issue
                sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                }
            
                else if(sellRatesDob!=null && ("Y".equals(sellRatesDob.getOverAllMargin())|| "N".equals(sellRatesDob.getOverAllMargin())) && "A".equals(sellRatesDob.getMarginType()))
                {
                                            //System.out.println("yyyyyyyy wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                    if("-".equalsIgnoreCase(chargesValue[j]))
                    {
                      if("MIN".equals(wtBreakValue[j]))
                        chargeCal[j]="-";
                      else
                        chargeCal[j]="-";
                      
                    }
                    else
                    { //Added by Rakesh for Issue:      on 03-03-2011
                      if("BASIC".equals(wtBreakValue[j]))
                        chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                      else
                      if("MIN".equals(wtBreakValue[j]))
                        chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[1]));
                      else
                        chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[2]));
                    }
                    sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                  sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[1]));
                  sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[2]));
                  
                }
               else if(sellRatesDob!=null && "N".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
               {
                       
                  //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                   if("-".equalsIgnoreCase(chargesValue[j]))
                   {
                      if("MIN".equals(wtBreakValue[j]))
                        chargeCal[j]="-";
                      else
                        chargeCal[j]="-";
                    }
                    else
                    {  //Added by Rakesh for Issue:       on 03-03-2011
                       if("BASIC".equals(wtBreakValue[j]))
                        chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                       else
                       if("MIN".equals(wtBreakValue[j]))
                        chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[1])/100)));
                       else
                        chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[2])/100)));
                       
                    }
                   sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                  sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[1]));
                  sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[2]));
               }
                 }   
             else if("SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
            {
                if(sellRatesDob!=null && "Y".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
                { 
                 
                    //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                  if("-".equalsIgnoreCase(chargesValue[j]))
                  {
                    chargeCal[j]="-";
                  }
                  else
                  {
                    chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                  }
                  sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
              }
              else if(sellRatesDob!=null && "Y".equals(sellRatesDob.getOverAllMargin()) && "A".equals(sellRatesDob.getMarginType()))
              {
                
                  //System.out.println("yyyyyyyy wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                  if("-".equalsIgnoreCase(chargesValue[j]))
                  {
                    if("MIN".equals(wtBreakValue[j]))
                      chargeCal[j]="-";
                    else
                      chargeCal[j]="-";
                  }
                  else
                  { //Modified by Rakesh for Issue:    on 03-03-2011
                	if("BASIC".equals(wtBreakValue[j]))
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                    else
                    if("MIN".equals(wtBreakValue[j]))
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[1]));
                    else
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[2]));
                  }
                  sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[1]));
                sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[2]));
              }
             else if(sellRatesDob!=null && "N".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
             {
              
                  if(request.getParameter(wtBreakValue[j])!=null)
                        commonOvrMarign[j]   =   Double.parseDouble(request.getParameter(wtBreakValue[j]));
                //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                  if("-".equalsIgnoreCase(chargesValue[j]))
                  {
                    if("MIN".equals(wtBreakValue[j]))
                      chargeCal[j]="-";
                    else
                      chargeCal[j]="-";
                  }     
                  
                  else
                  {
                	 if("BASIC".equals(wtBreakValue[j]))
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                     else 
                     if("MIN".equals(wtBreakValue[j]))
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                     else
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                  }
               
                sellRatesDob2.setSameOvrMargin(commonOvrMarign);
             }
             else if(sellRatesDob!=null && "N".equals(sellRatesDob.getOverAllMargin()) && "A".equals(sellRatesDob.getMarginType()))
             {
              
                  if(request.getParameter(wtBreakValue[j])!=null)
                        commonOvrMarign[j]   =   Double.parseDouble(request.getParameter(wtBreakValue[j]));
                //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                  if("-".equalsIgnoreCase(chargesValue[j]))
                  {
                      if("MIN".equals(wtBreakValue[j]))
                        chargeCal[j]="-";
                      else
                        chargeCal[j]="-";
                  }
                  else
                  {
                	if("BASIC".equals(wtBreakValue[j]))
                       chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                    else  
                    if("MIN".equals(wtBreakValue[j]))
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                    else
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                }
              sellRatesDob2.setSameOvrMargin(commonOvrMarign);
           }
        }
          
        else if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
        {
          
            if(sellRatesDob!=null && "Y".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
            { 
                //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                if("-".equalsIgnoreCase(chargesValue[j]))
                {
                  chargeCal[j]="-";
                }
                else
                {
                  chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                }
              sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
              sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
              
            }
              else if(sellRatesDob!=null && "Y".equals(sellRatesDob.getOverAllMargin()) && "A".equals(sellRatesDob.getMarginType()))
              {
                 //System.out.println("yyyyyyyy wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                  if("-".equalsIgnoreCase(chargesValue[j]))
                  {
                     if("overPivot".equals(wtBreakValue[j]))
                      chargeCal[j]="-";
                    else
                      chargeCal[j]="-";
                  }
                  else
                  {
                    if("overPivot".equals(wtBreakValue[j]))
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[1]));
                    else
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                  }
                sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[1]));
                
              }
             else if(sellRatesDob!=null && "N".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
             {
                 if(request.getParameter(wtBreakValue[j])!=null)
                        commonOvrMarign[j]   =   Double.parseDouble(request.getParameter(wtBreakValue[j]));
                //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                  if("-".equalsIgnoreCase(chargesValue[j]))
                  {
                     if("overPivot".equals(wtBreakValue[j]))
                      chargeCal[j]="-";
                    else
                      chargeCal[j]="-";
                  }
                  else
                  {
                    if("overPivot".equals(wtBreakValue[j]))
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                    else
                      chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                  }
                sellRatesDob2.setSameOvrMargin(commonOvrMarign);
             }
             else if(sellRatesDob!=null && "N".equals(sellRatesDob.getOverAllMargin()) && "A".equals(sellRatesDob.getMarginType()))
            {
                     if(request.getParameter(wtBreakValue[j])!=null)
                            commonOvrMarign[j]   =   Double.parseDouble(request.getParameter(wtBreakValue[j]));
                    //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                    //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                      if("-".equalsIgnoreCase(chargesValue[j]))
                      {
                         if("overPivot".equals(wtBreakValue[j]))
                          chargeCal[j]="-";
                        else
                          chargeCal[j]="-";
                      }
                      else
                      {
                        if("overPivot".equals(wtBreakValue[j]))
                          chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                        else
                          chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                      }
                    sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                 }
              }
            
                  }
             
             }
      }
                else
                {
                  if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
                  {
                	  int chargValLen	= chargesValue.length;
                     for(int j=0;j<chargValLen;j++)
                   {
                        if(((String)wtBreakValue[j]).endsWith("CAF")||((String)wtBreakValue[j]).endsWith("BAF")
                        ||((String)wtBreakValue[j]).endsWith("CSF")||((String)wtBreakValue[j]).endsWith("PSS")
                        || !("A FREIGHT RATE".equalsIgnoreCase(sellRatesDob1.getRateDescription()[j]) || "FREIGHT RATE".equalsIgnoreCase(sellRatesDob1.getRateDescription()[j])))//@@ Added by govind for the issue 266973
                        { if("-".equalsIgnoreCase(chargesValue[j]))
                {
                    chargeCal[j]="-";
                }
                else
                {
                   if(surcharge!=null)
                   {
                        if("A".equals(sellRatesDob.getMarginType()))
                        {
                          chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(surcharge[0]));
                        }
                        else
                        {
                          chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(surcharge[0])/100)));
                        }
                   sellRatesDob2.setAbperWithSurcharge(Double.parseDouble(surcharge[0]));
                   }
                   else
                   {
                     chargeCal[j]=""+Double.parseDouble(chargesValue[j]);
                     sellRatesDob2.setAbperWithSurcharge(0.0);
                   }
                }
                }
                    else
                    {
                      if(sellRatesDob!=null && "Y".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
                      { 
                        //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                          if("-".equalsIgnoreCase(chargesValue[j]))
                          {
                            chargeCal[j]="-";
                          }
                          else
                          {
                            chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                          }
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                        
                      }
                      else if(sellRatesDob!=null && "Y".equals(sellRatesDob.getOverAllMargin()) && "A".equals(sellRatesDob.getMarginType()))
                      {
                       if("-".equalsIgnoreCase(chargesValue[j]))
                          {
                            chargeCal[j]="-";
                          }
                          else
                          {
                            chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                          }
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                        
                      }
                     else if(sellRatesDob!=null && "N".equals(sellRatesDob.getOverAllMargin()) && "P".equals(sellRatesDob.getMarginType()))
                     {
                          if(request.getParameter(wtBreakValue[j])!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(request.getParameter(wtBreakValue[j]));
                        //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                        if("-".equalsIgnoreCase(chargesValue[j]))
                        {
                          chargeCal[j]="-";
                        }
                        else
                        {
                          chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                        }
                           
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                     }
                     else if(sellRatesDob!=null && "N".equals(sellRatesDob.getOverAllMargin()) && "A".equals(sellRatesDob.getMarginType()))
                     {
                          if(request.getParameter(wtBreakValue[j])!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(request.getParameter(wtBreakValue[j]));
                        //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                          if("-".equalsIgnoreCase(chargesValue[j]))
                          {
                            chargeCal[j]="-";
                          }
                          else
                          {
                            chargeCal[j]=""+(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                          }
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                     }
                     
                    }
                   }
                  }
                }
                sellRatesDob2.setChargeRates(chargeCal);
                  arrayList.add(sellRatesDob2);
                i++;
        }      
        sellRatesList.add(sellRatesDob);
        sellRatesList.add(arrayList);
        session.setAttribute("DisplysellRatesValues",sellRatesList);
        doFileDispatch(request,response,"etrans/QMSRecommendedSellRatesView.jsp");
      }

      catch(Exception e)
      {
          e.printStackTrace();
          errorMessageObject = new ErrorMessage("Error while doGetSellRatesCal()",nextNavigation); 
          keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          doFileDispatch(request,response,"ESupplyErrorPage.jsp");
      }
}
private void insertSellRates(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,String nextNavigation)throws IOException,ServletException
{
      ErrorMessage			        errorMessageObject                =   null;
      ArrayList			            keyValueList	                    =   new ArrayList();
      HttpSession				        session			                      =	  request.getSession();
      ArrayList			            listValues	                      =   null;
      String                    sellRatesdtl                      =   null;
    
      QMSSellRatesSessionHome   sellRatesHome                     =   null;
      QMSSellRatesSession       sellRatesRemote                   =   null;
      try
      { 
            listValues				=	(ArrayList)session.getAttribute("DisplysellRatesValues");
            session.removeAttribute("DisplysellRatesValues");
            session.removeAttribute("HashList");
            session.removeAttribute("HeaderValues");
             //System.out.println("************Before Look up****************");
            sellRatesHome		  =	  (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
            sellRatesRemote	  =	  (QMSSellRatesSession)sellRatesHome.create();
         
            sellRatesdtl      =   sellRatesRemote.insertSellRates(listValues,loginBean,operation);
           
            //System.out.println("************After Look up**************** "+sellRatesdtl);
            if(sellRatesdtl!=null)
            {
              if(sellRatesdtl.equalsIgnoreCase("successfully"))
            {
              errorMessageObject = new ErrorMessage("Successfully inserted sell Rates",nextNavigation); 
              keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
              keyValueList.add(new KeyValue("Operation",operation)); 	
              errorMessageObject.setKeyValueList(keyValueList);
              request.setAttribute("ErrorMessage",errorMessageObject);
              doFileDispatch(request,response,"ESupplyErrorPage.jsp");
            }
            }
            else
            {
              errorMessageObject = new ErrorMessage("Error while inseration",nextNavigation); 
              keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
              keyValueList.add(new KeyValue("Operation",operation)); 	
              errorMessageObject.setKeyValueList(keyValueList);
              request.setAttribute("ErrorMessage",errorMessageObject);
              doFileDispatch(request,response,"ESupplyErrorPage.jsp");
            }
            
      }
      catch(Exception e)
      {
          e.printStackTrace();
          // e.toString().c
           
        if(e.toString().endsWith("5"))
        {
          errorMessageObject = new ErrorMessage("Already Defined in Higher Level",nextNavigation); 
          keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }else{
          errorMessageObject = new ErrorMessage("Error while insertSellRates()",nextNavigation); 
          keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
      }
}
private void insertValues(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    String[]			            carriers			                    =	  null;
    String[]                  marginValues                      =   null;
    String                    shipmentMode                       =   null;
    String                    accessLevel                       =   null;
    try
    {  
        if(request.getParameter("shipmentMode")!=null)
          sellRatesDOB.setShipmentMode(request.getParameter("shipmentMode"));
        else
          sellRatesDOB.setShipmentMode("");
          //System.out.println("33333333 ::: "+request.getParameter("consoleType"));
        if(request.getParameter("consoleType")!=null && "2".equals(request.getParameter("shipmentMode")))
          sellRatesDOB.setConsoleType(request.getParameter("consoleType"));
        else  if(request.getParameter("consoleTypes")!=null && "4".equals(request.getParameter("shipmentMode")))
          sellRatesDOB.setConsoleType(request.getParameter("consoleTypes"));
        else
          sellRatesDOB.setConsoleType("");  
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
          sellRatesDOB.setCurrencyId("");
        if(request.getParameter("weightClass")!=null)
          sellRatesDOB.setWeightClass(request.getParameter("weightClass"));
        else
          sellRatesDOB.setWeightClass("");
        if(request.getParameter("origin")!=null)
          sellRatesDOB.setOrigin(request.getParameter("origin"));
        else
          sellRatesDOB.setOrigin("");
        if(request.getParameter("originCountry")!=null)
          sellRatesDOB.setOriginCountry(request.getParameter("originCountry"));
        else
          sellRatesDOB.setOriginCountry("");
        if(request.getParameter("destination")!=null)
          sellRatesDOB.setDestination(request.getParameter("destination"));
        else
          sellRatesDOB.setDestination("");
        if(request.getParameter("destinationCountry")!=null)
          sellRatesDOB.setDestinationCountry(request.getParameter("destinationCountry"));
        else
          sellRatesDOB.setDestinationCountry("");
        if(request.getParameter("serviceLevelId")!=null)
          sellRatesDOB.setServiceLevel(request.getParameter("serviceLevelId"));
        else
          sellRatesDOB.setServiceLevel("");
          
        if(request.getParameter("overMargin")!=null)
          sellRatesDOB.setOverAllMargin(request.getParameter("overMargin"));
        else
          sellRatesDOB.setOverAllMargin("");
          
        if(request.getParameter("marginType")!=null)
          sellRatesDOB.setMarginType(request.getParameter("marginType"));
        else
          sellRatesDOB.setMarginType("");
        if(request.getParameter("marginBasis")!=null)
          sellRatesDOB.setMarginBasis(request.getParameter("marginBasis"));
        else
          sellRatesDOB.setMarginBasis("");
          
        if(request.getParameter("carriers")!=null)
          sellRatesDOB.setCarrier_id(request.getParameter("carriers"));
        else
          sellRatesDOB.setCarrier_id("");  
          
        accessLevel        =	loginBean.getUserLevel();
        if("OPER_TERMINAL".equalsIgnoreCase(accessLevel))
        {
          accessLevel="O";
        }else if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
        {
          accessLevel ="H";
        }else if("ADMN_TERMINAL".equalsIgnoreCase(accessLevel))
        { 
          accessLevel ="A";
        }
        sellRatesDOB.setAccessLevel(accessLevel);
        //System.out.println("pageNopageNo in controller ::: "+request.getParameter("pageNo"));
        if(request.getParameter("pageNo")!=null)
        {
          sellRatesDOB.setPageNo(request.getParameter("pageNo"));
          
        }
        else
          sellRatesDOB.setPageNo("");
        if("Modify".equalsIgnoreCase(operation))
          sellRatesDOB.setTerminalId(request.getParameter("terminalId"));
       /* System.out.println("accessLevelaccessLevelaccessLevelaccessLevel :: "+accessLevel);
        System.out.println("5555555555555555555 ::: in controller "+request.getParameterValues("mOrigin"));
        System.out.println("***********************START PRINTS****************************"); 
        
        System.out.println("11111111ccc "+request.getParameter("carriers"));
        System.out.println("11111111 "+request.getParameter("shipmentMode"));
        System.out.println("22222222 "+request.getParameter("weightBreak"));
        System.out.println("33333333 "+request.getParameter("rateType"));
        System.out.println("44444444 "+request.getParameter("baseCurrency"));
        System.out.println("55555555 "+request.getParameter("weightClass"));
        System.out.println("66666666 "+request.getParameter("origin"));
        System.out.println("77777777 "+request.getParameter("originCountry"));
        System.out.println("88888888 "+request.getParameter("destination"));
        System.out.println("99999999 "+request.getParameter("destinationCountry"));
        System.out.println("00000000 "+request.getParameter("serviceLevelId"));
        System.out.println("00111111111111 "+request.getParameter("overMargin"));
        System.out.println("00222222222222 "+request.getParameter("marginType"));*/
         
        //System.out.println("***********************END PRINTS****************************");
         //added by phani sekhar for wpbn 171213 on 20090605
         if(request.getParameter("originRegion")!=null && !"".equals(request.getParameter("originRegion")))
          sellRatesDOB.setOriginRegions(request.getParameter("originRegion"));
        else
          sellRatesDOB.setOriginRegions("");
          
           if(request.getParameter("destinationRegion")!=null && !"".equals(request.getParameter("destinationRegion")))
          sellRatesDOB.setDestRegions(request.getParameter("destinationRegion"));
        else
          sellRatesDOB.setDestRegions("");
          //ends 171213
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while insertValues()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
public void doGetAcceptanceValues(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    QMSSellRatesSessionHome		sellRatesHome                     =   null;
    QMSSellRatesSession				sellRatesRemote                   =   null;
    ArrayList			            headerValues	                    =   null;
    String                    accessLevel                       =   null;
    try
    {
        if(request.getParameter("shipmentMode")!=null)
          sellRatesDOB.setShipmentMode(request.getParameter("shipmentMode"));
        else
          sellRatesDOB.setShipmentMode("");
        if(request.getParameter("consoleType")!=null && "2".equals(request.getParameter("shipmentMode")))
          sellRatesDOB.setConsoleType(request.getParameter("consoleType"));
        else  if(request.getParameter("consoleTypes")!=null && "4".equals(request.getParameter("shipmentMode")))
          sellRatesDOB.setConsoleType(request.getParameter("consoleTypes"));
        else
          sellRatesDOB.setConsoleType(""); 
        if(request.getParameter("origin")!=null)
            sellRatesDOB.setOrigin(request.getParameter("origin"));
          else
            sellRatesDOB.setOrigin("");
        if(request.getParameter("PageNo")!=null)
        {
          sellRatesDOB.setPageNo(request.getParameter("PageNo"));          
        }
        else
          sellRatesDOB.setPageNo("1");
          
        sellRatesDOB.setSortBy(request.getParameter("SortBy"));
        sellRatesDOB.setSortOrder(request.getParameter("SortOrder"));
        
        accessLevel        =	loginBean.getUserLevel();
        if("OPER_TERMINAL".equalsIgnoreCase(accessLevel))
        {
          accessLevel="O";
        }else if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
        {
          accessLevel ="H";
        }else if("ADMN_TERMINAL".equalsIgnoreCase(accessLevel))
        { 
          accessLevel ="A"; 
        }
        sellRatesDOB.setAccessLevel(accessLevel);
          
        doPageVlues(request,response,operation,nextNavigation,sellRatesDOB);  
        //System.out.println("sellRatesDOB.setShipmentMode()sellRatesDOB.setShipmentMode() :: "+sellRatesDOB.getShipmentMode());
        //System.out.println("sellRatesDOB.setConsoleType()sellRatesDOB.setConsoleType():: "+ sellRatesDOB.getConsoleType());
              
        sellRatesHome			=	  (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
        sellRatesRemote		=	  (QMSSellRatesSession)sellRatesHome.create();
        headerValues      =   sellRatesRemote.getAcceptanceSellRatesValues(sellRatesDOB,loginBean,operation);
        //System.out.println("headerValuesheaderValuesheaderValues in controller :: "+headerValues.size());
        session.setAttribute("HeaderValues",headerValues);
        doFileDispatch(request,response,"etrans/QMSRecommendedSellRatesAcceptance.jsp?SortBy="+request.getParameter("SortBy")+"&SortOrder="+request.getParameter("SortOrder")+"&PageNo="+request.getParameter("PageNo"));
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doGetAcceptanceValues()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
private void doInvalidatePageVlues(HttpServletRequest request, HttpServletResponse response,String operation,String nextNavigation,QMSSellRatesDOB sellRatesDOB)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    String[]                  mfValues                          =   null;
    String[]                  checkValue                        =   null;
    String[]                  noteValue                         =   null;
    String[]                  extNoteValue                      =   null;//Modified by Mohan for Issue No.219976 on 2-11-2010
    ArrayList                 listValues                        =   null;
    ArrayList                 accList                           =   null;
    ArrayList                 dobList                           =   null;
    QMSSellRatesDOB           dtlsellRatesDOB                   =   null;
    HashMap                   mapDob                            =   null;
    HashMap                   totalMapDob                       =   null;
    try
    {
        mfValues        =   request.getParameterValues("mfValues");
        checkValue      =   request.getParameterValues("checkValue");
        noteValue       =   request.getParameterValues("notes");
        extNoteValue    =   request.getParameterValues("extNotes");//Modified by Mohan for Issue No.219976 on 2-11-2010
        listValues			=	  (ArrayList)session.getAttribute("HeaderValues");
        mapDob			    =	  (HashMap)session.getAttribute("HashList");
        totalMapDob			=	(HashMap)session.getAttribute("TotalHashList");
        if(totalMapDob==null)
          totalMapDob	=	new HashMap();
        if(mapDob==null)
          mapDob    =   new HashMap();
        if(checkValue!=null)
        {
           int   checkValuelength  = checkValue.length;
           String   hiddenChecked  =  null;
           accList					=	  (ArrayList)listValues.get(1);
           dobList					=	  (ArrayList)accList.get(0);
            for(int j=0;j<checkValuelength;j++)
            {
              hiddenChecked = checkValue[j];
              dtlsellRatesDOB   = (QMSSellRatesDOB)dobList.get(j);
              if(noteValue[j]!=null && noteValue[j].trim().length()!=0)
               dtlsellRatesDOB.setNotes(noteValue[j]);
              else
               dtlsellRatesDOB.setNotes(""); 
              //Modified by Mohan for Issue No.219976 on 2-11-2010
              if(extNoteValue[j]!=null && extNoteValue[j].trim().length()!=0)
                  dtlsellRatesDOB.setExtNotes(extNoteValue[j]);
                 else
                  dtlsellRatesDOB.setExtNotes("");
              if(mfValues!=null)
              {
                int mfValuesLength  = mfValues.length;
                boolean checkflag     = false;
                //System.out.println("checkValueLengthcheckValueLengthcheckValueLengthcheckValueLength :: "+mfValuesLength);
                for(int i=0;i<mfValuesLength;i++)
                {
                  //System.out.println("checkValuecheckValue:: "+hiddenChecked+"mfValuesmfValues :: "+mfValues[i]);
                  if(hiddenChecked.equals(mfValues[i]))
                  {
                    checkflag = true;
                    break;
                  }
                }
               if(checkflag)
               {
                  mapDob.remove(hiddenChecked);
                  mapDob.put(hiddenChecked,dtlsellRatesDOB);
                  totalMapDob.remove(hiddenChecked);
                  dtlsellRatesDOB.setInvalidate("T");
                  totalMapDob.put(hiddenChecked,dtlsellRatesDOB);
                }
                else
                {
                  mapDob.remove(hiddenChecked);
                  totalMapDob.remove(hiddenChecked);
                  dtlsellRatesDOB.setInvalidate("F");
                  totalMapDob.put(hiddenChecked,dtlsellRatesDOB);
                }
              }
              else
              {
                mapDob.remove(hiddenChecked);
                totalMapDob.remove(hiddenChecked);
                dtlsellRatesDOB.setInvalidate("F");
                totalMapDob.put(hiddenChecked,dtlsellRatesDOB);
              }
          }
          
      }
      session.setAttribute("HashList",mapDob);
      session.setAttribute("TotalHashList",totalMapDob);
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doPageValues()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
private void doPageVlues(HttpServletRequest request, HttpServletResponse response,String operation,String nextNavigation,QMSSellRatesDOB sellRatesDOB)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    String[]                  mfValues                          =   null;
    String[]                  checkValue                        =   null;
    String[]                  noteValue                         =   null;
    String[]                  extNoteValue                      =   null;
    ArrayList                 listValues                        =   null;
    ArrayList                 accList                           =   null;
    ArrayList                 dobList                           =   null;
    QMSSellRatesDOB           dtlsellRatesDOB                   =   null;
    HashMap                   mapDob                            =   null;
    try
    {
        mfValues        =   request.getParameterValues("mfValues");
        checkValue      =   request.getParameterValues("checkValue");
        noteValue       =   request.getParameterValues("notes");
        extNoteValue    =   request.getParameterValues("extNotes");//Modified by Mohan for Issue No.219976 on 2-11-2010
        listValues			=	  (ArrayList)session.getAttribute("HeaderValues");
        mapDob			    =	  (HashMap)session.getAttribute("HashList");
        if(mapDob==null)
          mapDob    =   new HashMap();
        if(checkValue!=null)
        {
           int   checkValuelength  = checkValue.length;
           String   hiddenChecked  =  null;
           accList					=	  (ArrayList)listValues.get(1);
           dobList					=	  (ArrayList)accList.get(0);
            for(int j=0;j<checkValuelength;j++)
            {
              hiddenChecked = checkValue[j];
              if(mfValues!=null)
              {
                int mfValuesLength  = mfValues.length;
                boolean checkflag     = false;
                //System.out.println("checkValueLengthcheckValueLengthcheckValueLengthcheckValueLength :: "+mfValuesLength);
                for(int i=0;i<mfValuesLength;i++)
                {
                  //System.out.println("checkValuecheckValue:: "+hiddenChecked+"mfValuesmfValues :: "+mfValues[i]);
                  if(hiddenChecked.equals(mfValues[i]))
                  {
                    checkflag = true;
                    break;
                  }
            
                }
               dtlsellRatesDOB   = (QMSSellRatesDOB)dobList.get(j);
         
               if(noteValue[j]!=null && noteValue[j].trim().length()!=0)
                dtlsellRatesDOB.setNotes(noteValue[j]);
               else
                dtlsellRatesDOB.setNotes("");
              //Modified by Mohan for Issue No.219976 on 2-11-2010
               if(extNoteValue[j]!=null && extNoteValue[j].trim().length()!=0)
                   dtlsellRatesDOB.setExtNotes(extNoteValue[j]);
                  else
                   dtlsellRatesDOB.setExtNotes("");
               if(checkflag)
               {
                  mapDob.remove(hiddenChecked);
                  mapDob.put(hiddenChecked,dtlsellRatesDOB);
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
          
      }
      session.setAttribute("HashList",mapDob);
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doPageValues()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
private void doDisplayAcceptanceValues(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    HttpSession				        session			                      =	  request.getSession();
    try
    {
        doFileDispatch(request,response,"etrans/QMSRecommendedSellRatesAcceptanceModify.jsp?index="+request.getParameter("index")+"&serchStr=Enter");
    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while doDisplayAcceptanceValues()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}

private void doAcceptanceValuesCal(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,ArrayList listValues,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
      ErrorMessage			        errorMessageObject                =   null;
      ArrayList			            keyValueList	                    =   new ArrayList();
      HttpSession				        session			                      =	  request.getSession();
      QMSSellRatesDOB           hederSellRatesDob                 =   null;
      QMSSellRatesDOB           detlSellRatesDob                  =   null;
      QMSSellRatesDOB           sellRatesDob2                     =   null;
      ArrayList			            sellRatesList	                    =   null;
      ArrayList                 arrayList                         =   null;
      ArrayList                 fslListValues                     =   null; 
      ArrayList                 dobList                           =   null;
      //String[]                  marign                            =   null;
      String[]                  checkBoxValue                     =   null;
      String[]                  noteValue                         =   null;
      String[]                  extNoteValue                      =   null;//Added by Mohan for issue 
      double                    minAbValue                        =   0.0;
      double                    flatAbValue                       =   0.0;
      //double	                  commonOvrMarign                   =   0.0;
      QMSSellRatesSessionHome   sellRatesHome                     =   null;
      QMSSellRatesSession       sellRatesRemote                   =   null;
      String                    sellRatesdtl                      =   null;
      Iterator			            itr						                    =	  null;
      HashMap				            mapValues				                  =	  null;
      String				            keyHash					                  =	  null;
      //System.out.println("44444444444444444444444:: in Bean ");
      try
      {
       
          sellRatesList           =   new ArrayList();
          arrayList               =   new ArrayList();
          
          hederSellRatesDob			  =	  (QMSSellRatesDOB)listValues.get(0); 
          mapValues				        =	  (HashMap)session.getAttribute("HashList");
          session.removeAttribute("HashList");
					itr				              =	  (mapValues.keySet()).iterator();
          
          //checkBoxValue           =   request.getParameterValues("mfValues");
          noteValue               =   request.getParameterValues("notes");
          extNoteValue            =   request.getParameterValues("extNotes");//Modified by Mohan for Issue No.219976 on 2-11-2010
          //int fslListValuesSize   =   checkBoxValue.length;
          //int dobListSize         =   dobList.size();
          //System.out.println("fslListValuesSizefslListValuesSizefslListValuesSize :: "+fslListValuesSize);
          int i=0;
          String[] rateDescription	= null;
          while(itr.hasNext())
          {
            keyHash	=	(String)itr.next();
            //System.out.println("keyHashkeyHashkeyHashkeyHashkeyHashkeyHash in controller :: "+keyHash);
            sellRatesDob2     =   new QMSSellRatesDOB();
            detlSellRatesDob  =   (QMSSellRatesDOB)mapValues.get(keyHash);
            rateDescription	  = detlSellRatesDob.getRateDescription();
            //dobList.get(Integer.parseInt(checkBoxValue[i]));
            //System.out.println("getCarrier_idgetCarrier_idgetCarrier_idgetCarrier_id :: "+detlSellRatesDob.getCarrier_id());
            sellRatesDob2.setCarrier_id(detlSellRatesDob.getCarrier_id());
            //System.out.println("getOrigingetOrigingetOrigingetOrigingetOrigin :: "+detlSellRatesDob.getOrigin());
            sellRatesDob2.setOrigin(detlSellRatesDob.getOrigin());
            //System.out.println("getOriginCountrygetOriginCountrygetOriginCountrygetOriginCountry :: "+detlSellRatesDob.getOriginCountry());
            sellRatesDob2.setOriginCountry(detlSellRatesDob.getOriginCountry());
            sellRatesDob2.setDestination(detlSellRatesDob.getDestination());
            sellRatesDob2.setDestinationCountry(detlSellRatesDob.getDestinationCountry());
            sellRatesDob2.setServiceLevel(detlSellRatesDob.getServiceLevel());
            sellRatesDob2.setTransitTime(detlSellRatesDob.getTransitTime());
            sellRatesDob2.setFrequency(detlSellRatesDob.getFrequency());
            sellRatesDob2.setCurrencyId(detlSellRatesDob.getCurrencyId());
            sellRatesDob2.setRec_buyrate_id(detlSellRatesDob.getRec_buyrate_id());
            //System.out.println("getLanNumbergetLanNumbergetLanNumber :: "+sellRatesDob1.getLanNumber());
            sellRatesDob2.setLanNumber(detlSellRatesDob.getLanNumber());
            //System.out.println("notesnotesnotesnotesnotesnotesnotes in controller :: "+request.getParameter(keyHash));
            sellRatesDob2.setNoteValue(detlSellRatesDob.getNotes());
            sellRatesDob2.setExtNotes(detlSellRatesDob.getExtNotes());//Modified by Mohan for Issue No.219976 on 2-11-2010
            sellRatesDob2.setLBound(detlSellRatesDob.getLBound());
            sellRatesDob2.setUBound(detlSellRatesDob.getUBound());
            sellRatesDob2.setChargeInr(detlSellRatesDob.getChargeInr());
            sellRatesDob2.setBuyRateId(detlSellRatesDob.getBuyRateId());
           sellRatesDob2.setVersionNo(detlSellRatesDob.getVersionNo());//@@Added for the WPBN issue-146448 on 23/12/08
            sellRatesDob2.setWeightBreaks(detlSellRatesDob.getWeightBreaks());
           
            sellRatesDob2.setAllWeightBreaks(detlSellRatesDob.getAllWeightBreaks());
            //sellRatesDob2.setBuyChargeRates(detlSellRatesDob.getChargeRatesValues());
            sellRatesDob2.setChargeRates(detlSellRatesDob.getChargeRates());
            String[]  chargesValue	    =	detlSellRatesDob.getChargeRates();
            String[]  marign            = detlSellRatesDob.getMarginValues();
            String[] wtBreakValue       = detlSellRatesDob.getAllWeightBreaks();
            int chargesLength		        =	chargesValue.length;
            double[]  chargeCal         = new double[chargesLength];
            double[]  commonOvrMarign   = new double[chargesLength];
            double    cgargeParse       = 0.0;
            sellRatesDob2.setWeightBreak(detlSellRatesDob.getWeightBreak());
            sellRatesDob2.setOverAllMargin(detlSellRatesDob.getOverAllMargin());
            sellRatesDob2.setMarginType(detlSellRatesDob.getMarginType());
            sellRatesDob2.setRateType(detlSellRatesDob.getRateType());
            sellRatesDob2.setWeightClass(detlSellRatesDob.getWeightClass());
            sellRatesDob2.setRateDescription(detlSellRatesDob.getRateDescription());
              //System.out.println("hederSellRatesDob.getOverAllMargin() ::: "+hederSellRatesDob.getOverAllMargin());
            //System.out.println("chargesLengthchargesLengthchargesLengthchargesLength :: "+chargesLength);
            //System.out.println("swrwrwrwrwrwrwrwrwrwrwrwrw OverAllMarginOverAllMargin :: "+detlSellRatesDob.getOverAllMargin());
         if("1".equals(hederSellRatesDob.getShipmentMode()) || ("2".equals(hederSellRatesDob.getShipmentMode()) && "LCL".equals(hederSellRatesDob.getConsoleType())) || ("4".equals(hederSellRatesDob.getShipmentMode()) && "LTL".equals(hederSellRatesDob.getConsoleType())))
            {
            //System.out.println("swrwrwrwrwrwrwrwrwrwrwrwrw getMarginTypegetMarginType :: "+detlSellRatesDob.getMarginType());
          for(int j=0;j<chargesLength;j++)
           {
           
           /* if("FSBASIC".equalsIgnoreCase(wtBreakValue[j])||"FSMIN".equalsIgnoreCase(wtBreakValue[j])
              ||"FSKG".equalsIgnoreCase(wtBreakValue[j])||"SSBASIC".equalsIgnoreCase(wtBreakValue[j])
              ||"SSMIN".equalsIgnoreCase(wtBreakValue[j])||"SSKG".equalsIgnoreCase(wtBreakValue[j])
              ||"CAFMIN".equalsIgnoreCase(wtBreakValue[j])||"CAF%".equalsIgnoreCase(wtBreakValue[j])
              ||"BAFMIN".equalsIgnoreCase(wtBreakValue[j])||"BAFM3".equalsIgnoreCase(wtBreakValue[j])
              ||"PSSMIN".equalsIgnoreCase(wtBreakValue[j]) ||"PSSM3".equalsIgnoreCase(wtBreakValue[j])//ADDED BY SUBRAHMANYAM FOR pbn id: 188751
              ||"SURCHARGE".equalsIgnoreCase(wtBreakValue[j])||"CSF".equalsIgnoreCase(wtBreakValue[j]))
*/				if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))
        	  {
               
                  chargeCal[j]=Double.parseDouble(chargesValue[j]);
               
           }
           else
           {
            
                  if("FLAT".equalsIgnoreCase(detlSellRatesDob.getWeightBreak()))
                  {
                     
                      if(detlSellRatesDob!=null && "Y".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                      { 
                       
                          //cgargeParse = Double.parseDouble(chargesValue[j]);
                          //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                          //{
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                          /*}
                          else
                          {
                            chargeCal[j]=0.0;
                          }*/
                        sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));      
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                      }
                      else if(detlSellRatesDob!=null && ("Y".equals(detlSellRatesDob.getOverAllMargin())|| "N".equals(detlSellRatesDob.getOverAllMargin())) && "A".equals(detlSellRatesDob.getMarginType()))
                      {
                       
                          //cgargeParse = Double.parseDouble(chargesValue[j]);
                          //System.out.println("yyyyyyyy wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                    	  //@@Modify by kameswari.p on 25/07/2011 for Wpbn Issue 259371
                    	  if(marign.length>2)
                    	  {
                    		  if("BASIC".equals(wtBreakValue[j].trim()))
                    			  chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                    		  else if ("MIN".equals(wtBreakValue[j].trim()))
                    			  chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[1]));
                    		  else
                            	 chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[2]));
                    		  sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                              sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[1]));
                              sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[2]));
                    	  }
                    	  else
                    	  {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                            else
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[1]));
                            sellRatesDob2.setAbpersentWithBasic(Double.parseDouble("0.0"));
                            sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                            sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[1]));
                    	  }
                    	  //@@Ended by kameswari.p
                         /* }
                          else
                          {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                    	
                     }
                     else if(detlSellRatesDob!=null && "N".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                     {
                       
                          //cgargeParse = Double.parseDouble(chargesValue[j]);
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                          //if(Double.parseDouble(chargesValue[j])>0)
                          //{
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                            else
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[1])/100)));
                         /* }
                          else
                          {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                        sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[1]));
                     }
                  }
                  else if("SLAB".equalsIgnoreCase(detlSellRatesDob.getWeightBreak()))
                  {
                      if(detlSellRatesDob!=null && "Y".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                      { 
                        
                          //System.out.println("chargesValuechargesValuechargesValuechargesValue :: "+Double.parseDouble(chargesValue[j]));
                          //cgargeParse = Double.parseDouble(chargesValue[j]);
                          //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                            chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                         /* }
                          else
                          {
                            chargeCal[j]=0.0;
                          }*/
                        sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                      }
                      else if(detlSellRatesDob!=null && "Y".equals(detlSellRatesDob.getOverAllMargin()) && "A".equals(detlSellRatesDob.getMarginType()))
                      {
                         
                          //System.out.println("chargesValuechargesValuechargesValuechargesValue :: "+Double.parseDouble(chargesValue[j]));
                          //cgargeParse = Double.parseDouble(chargesValue[j]);
                          //System.out.println("yyyyyyyy wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                            //System.out.println("marignmarignmarignmarignmarignmarignmarign000000000 :: "+Double.parseDouble(marign[0]));
                            //System.out.println("marignmarignmarignmarignmarignmarignmarign111111111 :: "+Double.parseDouble(marign[1]));
                            //System.out.println("marignmarign[0] in Con : "+marign[0]+"marignmarign[1] in Con : "+marign[1]);
                            // System.out.println("wtBreakValue[j]wtBreakValue[j]wtBreakValue[j] : "+wtBreakValue[j]);
                            if("MIN".equals(wtBreakValue[j].trim()))
                            {
                              //System.out.println("marignmarign[0] in Con : "+marign[0]);
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                             // System.out.println("chargeCalchargeCalchargeCalchargeCal MIn : "+chargeCal[j]);
                              
                            }
                            else
                            {
                              //System.out.println("marignmarign[1] in Con : "+marign[1]);
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[1]));
                              
                              //System.out.println("chargeCalchargeCalchargeCalchargeCal Flat : "+chargeCal[j]);
                            }
                         /* }
                          else
                          {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                        sellRatesDob2.setAbpersentWithBasic(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[1]));
                      }
                     else if(detlSellRatesDob!=null && "N".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                     {
                       
                          //cgargeParse = Double.parseDouble(chargesValue[j]);
                          if(marign[j]!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(marign[j]);
                        //System.out.println("chargesValuechargesValuechargesValuechargesValue :: "+Double.parseDouble(chargesValue[j]));
                        //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                            else
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                         /* }
                          else
                          {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                       
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                     }
                     else if(detlSellRatesDob!=null && "N".equals(detlSellRatesDob.getOverAllMargin()) && "A".equals(detlSellRatesDob.getMarginType()))
                     {
                       
                          //cgargeParse = Double.parseDouble(chargesValue[j]);
                          if(marign[j]!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(marign[j]);
                        //System.out.println("chargesValuechargesValuechargesValuechargesValue :: "+Double.parseDouble(chargesValue[j]));
                       // System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                            else
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                        /*  }
                          else
                          {
                            if("MIN".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                           
                       
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                     }
                  }
                  else if("LIST".equalsIgnoreCase(detlSellRatesDob.getWeightBreak()))
                  {
                      if(detlSellRatesDob!=null && "Y".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                      { 
                        
                          //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                            chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                         /* }
                          else
                          {
                            chargeCal[j]=0.0;
                          }*/
                       
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                        
                      }
                      else if(detlSellRatesDob!=null && "Y".equals(detlSellRatesDob.getOverAllMargin()) && "A".equals(detlSellRatesDob.getMarginType()))
                      {
                        
                          //System.out.println("yyyyyyyy wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                          //System.out.println("chargesValuechargesValuechargesValuechargesValue :: "+Double.parseDouble(chargesValue[j]));
                         // if(Double.parseDouble(chargesValue[j])>0)
                          //{
                            if("overPivot".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[1]));
                            else
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                         /* }
                          else
                          {
                            if("overPivot".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                       
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[1]));
                        
                      }
                     else if(detlSellRatesDob!=null && "N".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                     {
                      if(marign[j]!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(marign[j]);
                        //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                            if("overPivot".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                            else
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                         /* }
                          else
                          {
                            if("overPivot".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                           
                   
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                     }
                     else if(detlSellRatesDob!=null && "N".equals(detlSellRatesDob.getOverAllMargin()) && "A".equals(detlSellRatesDob.getMarginType()))
                     {
                       
                          if(marign[j]!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(marign[j]);
                        //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                         // {
                            if("overPivot".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                            else
                              chargeCal[j]=(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                         /* }
                          else
                          {
                            if("overPivot".equals(wtBreakValue[j].trim()))
                              chargeCal[j]=0.0;
                            else
                              chargeCal[j]=0.0;
                          }*/
                    
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                     }
                   
                  }
            }
           }
            }
          
             else
             {
                   if("LIST".equalsIgnoreCase(detlSellRatesDob.getWeightBreak()))
                  {
                	   int chargValLen	= chargesValue.length;
                       for(int j=0;j<chargValLen;j++)
                   {
                     /*   if(((String)wtBreakValue[j]).endsWith("CAF")||((String)wtBreakValue[j]).endsWith("BAF")
                        ||((String)wtBreakValue[j]).endsWith("CSF")||((String)wtBreakValue[j]).endsWith("PSS"))
                        {
                            
                                chargeCal[j]=Double.parseDouble(chargesValue[j]);
                             
                         }*/
                   /* else
                    {*/
                       if(detlSellRatesDob!=null && "Y".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                      { 
                           //System.out.println("wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                         // if(Double.parseDouble(chargesValue[j])>0)
                       //
                         
                            chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(Double.parseDouble(marign[0])/100)));
                         /* }
                          * }
                          else
                          {
                            chargeCal[j]=0.0;
                          }*/
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                        
                      }
                      else if(detlSellRatesDob!=null && "Y".equals(detlSellRatesDob.getOverAllMargin()) && "A".equals(detlSellRatesDob.getMarginType()))
                      {
                        
                          //if(Double.parseDouble(chargesValue[j])>0)
                          //{
                            chargeCal[j]=(Double.parseDouble(chargesValue[j])+Double.parseDouble(marign[0]));
                         /* }
                          else
                          {
                            chargeCal[j]=0.0;
                          }*/
                      
                      
                        sellRatesDob2.setAbpersentWithMin(Double.parseDouble(marign[0]));
                        sellRatesDob2.setAbpersentWithFlat(Double.parseDouble(marign[0]));
                      }
                     else if(detlSellRatesDob!=null && "N".equals(detlSellRatesDob.getOverAllMargin()) && "P".equals(detlSellRatesDob.getMarginType()))
                     {
                          if(marign[j]!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(marign[j]);
                        //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                       // if(Double.parseDouble(chargesValue[j])>0)
                       // {
                          chargeCal[j]=(Double.parseDouble(chargesValue[j])+(Double.parseDouble(chargesValue[j])*(commonOvrMarign[j]/100)));
                      /*        
                     
                       * }
                        else
                        {
                          chargeCal[j]=0.0;
                        }*/
                           
                  
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                
                     }
                     else if(detlSellRatesDob!=null && "N".equals(detlSellRatesDob.getOverAllMargin()) && "A".equals(detlSellRatesDob.getMarginType()))
                     {
                       
                          if(marign[j]!=null)
                                commonOvrMarign[j]   =   Double.parseDouble(marign[j]);
                   
                        //System.out.println("nnnn commonOvrMarigncommonOvrMarigncommonOvrMarign :: "+commonOvrMarign[j]);  
                        //System.out.println("nnnn wtBreakValuewtBreakValuewtBreakValuewtBreakValue :: "+wtBreakValue[j]);
                        //  if(Double.parseDouble(chargesValue[j])>0)
                        //  {
                            chargeCal[j]=(Double.parseDouble(chargesValue[j])+commonOvrMarign[j]);
                        /*  }
                          else
                          {
                            chargeCal[j]=0.0;
                          }*/
                    
                        sellRatesDob2.setSameOvrMargin(commonOvrMarign);
                
                     }
                  //}
                }
                  }
             }
               
                sellRatesDob2.setChargeRatesValues(chargeCal);
                arrayList.add(sellRatesDob2);
              i++;
          }
        sellRatesList.add(hederSellRatesDob);
        sellRatesList.add(arrayList);
        sellRatesHome		  =	  (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
        sellRatesRemote	  =	  (QMSSellRatesSession)sellRatesHome.create();
        sellRatesdtl      =   sellRatesRemote.insertAcceptanceSellRates(sellRatesList,loginBean,operation);
        if(sellRatesdtl!=null)
        {
            System.out.println("errormsg");
            if(sellRatesdtl.equalsIgnoreCase("successfully"))
            {
            errorMessageObject = new ErrorMessage("Successfully inserted Acceptance sell Rates",nextNavigation); 
            keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject);
            doFileDispatch(request,response,"ESupplyErrorPage.jsp");
            }
        }
        else
        {
            errorMessageObject = new ErrorMessage("Error while inseration",nextNavigation); 
            keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject);
            doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
    }
    catch(Exception e)
    {
       e.printStackTrace();
        if(e.toString().endsWith("5"))
        {
          errorMessageObject = new ErrorMessage("Already Defined in Higher Level",nextNavigation); 
          keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }else{
        errorMessageObject = new ErrorMessage("Error while doAcceptanceValuesCal()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
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
      logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}
}
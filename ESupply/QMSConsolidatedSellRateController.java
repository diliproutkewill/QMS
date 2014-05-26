/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 */
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.sellrates.dao.QMSSellRatesDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.PrintWriter;
import java.io.IOException;

import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSessionHome;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSession;
import com.foursoft.esupply.common.java.LookUpBean;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger; 

import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;


/*** 
File			      : QMSConsolidatedSellRateController.java
Product Name	  : QMS
Module Name		  : Recommended Sell Rates
Task		        : Adding/Modify CSR
Date started	  : 26-07-2005 	
Date Completed  : 
Date modified	  :  
Author    		  : Yuvraj Waghray
Description		  : 
Actor           :
Related Document: CR_DHLQMS_1006
 */

public class QMSConsolidatedSellRateController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  public static String FILE_NAME = "QMSConsolidatedSellRateController.java";
  private static Logger logger = null;
    
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSConsolidatedSellRateController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType(CONTENT_TYPE);
    HttpSession session = request.getSession();
		ESupplyGlobalParameters loginbean = (ESupplyGlobalParameters)session.getAttribute("loginbean");
		
		String        operation          = (String)request.getParameter("Operation");
		String        subOperation       = (String)request.getParameter("SubOperation");
		String        shipmentMode       = (String)request.getParameter("shipmentMode");
		String        fromWhere          = request.getParameter("fromWhere");
		//String        terminalId         = null;
    
    ErrorMessage	errorMessageObject		=   null;
    ArrayList			keyValueList			    =   new ArrayList();
    String        errorMessage          =   "";

		//Logger.info(FILE_NAME, " operation ", operation);
		//Logger.info(FILE_NAME, " subOperation ", subOperation);
		//Logger.info(FILE_NAME, " shipmentMode ", shipmentMode);
    //Logger.info(FILE_NAME, " loginbean::"+loginbean);
		try
		{
      if("Add".equalsIgnoreCase(operation) && subOperation==null)
			{
        doFileDispatch(request,response,"/etrans/QMSCSellRate.jsp?Operation="+operation+"&shipmentMode="+shipmentMode+"&fromWhere="+fromWhere);
			}
			else if("Modify".equalsIgnoreCase(operation) && subOperation==null)
			{
        doFileDispatch(request,response,"/etrans/QMSCSellRate.jsp?Operation="+operation+"&shipmentMode="+shipmentMode+"&fromWhere="+fromWhere);
			}
			else if(("Add".equalsIgnoreCase(operation) || ("Modify".equalsIgnoreCase(operation))) && "Details".equalsIgnoreCase(subOperation))
			{
        String result = doSearchProcess(loginbean,request,response);
        
        if(result!=null)
          doFileDispatch(request,response,"/etrans/QMSCSellRate.jsp?Operation="+operation);
        else
        {
          errorMessage = "An Unexpected Error has Occured in the Search Process .Please Try Again. ";
          errorMessageObject = new ErrorMessage(errorMessage,"QMSConsolidatedSellRateController?Operation="+operation+"&shipmentMode="+shipmentMode);
          keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
          keyValueList.add(new KeyValue("Operation","Add")); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject); 
          doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
			}
      else if(("Add".equalsIgnoreCase(operation) || ("Modify".equalsIgnoreCase(operation))) && "setSellRates".equalsIgnoreCase(subOperation))
      {        
        String message = "";
        String errorCode = "";
        ArrayList ratesList =  doAddProcess(loginbean,request,response);
        if(ratesList!=null)
        {
          if("Add".equalsIgnoreCase(operation))
          {
            message = "Added";
            errorCode = "RSI";
          }
          else
          {
            message = "Modified";
            errorCode = "RSM";
          }
          errorMessage = "Sell Rates Successfully "+message;
          errorMessageObject = new ErrorMessage(errorMessage,"QMSConsolidatedSellRateController?Operation="+operation+"&shipmentMode="+shipmentMode);
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation","Add")); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject); 
          doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
        else 
        {
          if("Add".equalsIgnoreCase(operation))
            message = "Adding";
          else
            message = "Modifying";
          errorMessage = "An Unexpected Error has Occured While "+message+" Sell Rates. Please Try Again ";
          errorMessageObject = new ErrorMessage(errorMessage,"QMSConsolidatedSellRateController?Operation="+operation+"&shipmentMode="+shipmentMode);
          keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
          keyValueList.add(new KeyValue("Operation","Add")); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject); 
          doFileDispatch(request,response,"ESupplyErrorPage.jsp");
        }
          
      }
		}
		catch(Exception e)
		{
		  //Logger.error(FILE_NAME,"Error in Controller"+e.toString());
      logger.error(FILE_NAME+"Error in Controller"+e.toString());
		  e.printStackTrace();
		}
    
  }
  private void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile) throws IOException, ServletException
  {
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
      logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
   }

  private String doSearchProcess(ESupplyGlobalParameters loginbean,HttpServletRequest request,HttpServletResponse response)
  {
    ErrorMessage	errorMessageObject    =  null;
		ArrayList		keyValueList		        =  new ArrayList();
    
    //Logger.info(FILE_NAME,"inside doSearchProcess()");
    
    QMSSellRatesDOB sellRateDetails     = new QMSSellRatesDOB();
    
    String originLocation       = null;
    String originCountry        = null;
    String destinationLocation  = null;
    String destinationCountry   = null;
    String shipmentMode         = null;
    String[] carriers           = null;
    String serviceLevel         = null;
    String currencyId           = null;
    String weightBreak          = null;
    String rateType             = null;
    String weightClass          = null;
    String result               = null;
    String accessLevel          = null;
    String modifyTerminal       = null;
    
    StringBuffer errors         = null;
    
    QMSSellRatesSessionHome home   = null;
    QMSSellRatesSession     remote = null;
    
    try
    {  
      String        operation          = request.getParameter("Operation");
      String        fromWhere          = request.getParameter("fromWhere");
      String        currentDate        = request.getParameter("currentDate");
      String        dateFormat         = loginbean.getUserPreferences().getDateFormat();
      
      home    = (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
      remote  = (QMSSellRatesSession)home.create();
      
      shipmentMode        = request.getParameter("shipmentMode");
      originLocation      = request.getParameter("origin");
      originCountry       = request.getParameter("originCountry");
      destinationLocation = request.getParameter("destination");
      destinationCountry  = request.getParameter("destinationCountry");
      carriers            = request.getParameterValues("carriers");
      serviceLevel        = request.getParameter("serviceLevelId");
      currencyId          = request.getParameter("baseCurrency");
      weightBreak         = request.getParameter("weightBreak");
      rateType            = request.getParameter("rateType");
      weightClass         = request.getParameter("weightClass");
      
      if("HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
          accessLevel = "H";
      else if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
          accessLevel = "A";
      else if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
          accessLevel = "O";
      
      sellRateDetails.setShipmentMode(shipmentMode);
      sellRateDetails.setOrigin(originLocation);
      sellRateDetails.setOriginCountry(originCountry);
      sellRateDetails.setDestination(destinationLocation);
      sellRateDetails.setDestinationCountry(destinationCountry);
      sellRateDetails.setCarriers(carriers);
      sellRateDetails.setServiceLevel(serviceLevel);
      sellRateDetails.setCurrencyId(currencyId);
      sellRateDetails.setWeightBreak(weightBreak);
      sellRateDetails.setWeightClass(weightClass);
      sellRateDetails.setRateType(rateType);
      sellRateDetails.setOperation(operation);
      sellRateDetails.setAccessLevel(accessLevel);
      
      if("Modify".equalsIgnoreCase(operation))
      {
        modifyTerminal  = request.getParameter("modifyTerminal");
        if(modifyTerminal!=null && modifyTerminal.trim().length()!=0)
          sellRateDetails.setTerminalId(modifyTerminal);
        else
          sellRateDetails.setTerminalId(loginbean.getTerminalId());
      }
      else
        sellRateDetails.setTerminalId(loginbean.getTerminalId());
        
      sellRateDetails.setLoginTerminalId(loginbean.getTerminalId());
      
      //System.out.println(" carriers "+carriers);
      //Logger.info(FILE_NAME,"Before the remote call");
      errors = remote.validateSellRateFormHdr(sellRateDetails);
      
      if(errors == null || errors.length() > 0)
      {
        request.setAttribute("Errors",errors.toString());
        request.setAttribute("sellRateDetails",sellRateDetails);
        doFileDispatch(request,response,"/etrans/QMSCSellRate.jsp?Operation="+operation+"&shipmentMode="+shipmentMode+"&fromWhere="+fromWhere);
      }
      else
      {
        ArrayList list   = null;
        request.setAttribute("sellRateDetails",sellRateDetails);
        //QMSSellRatesDAO tempDAO = new QMSSellRatesDAO();
        list = remote.getBuySellRateDetails(sellRateDetails);
        //Logger.info(FILE_NAME,"listlistlist::"+list);
        request.setAttribute("buyRateDetailsList",list);
        result = "Success";
      }
      
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in the Search Process:"+e.toString());
      logger.error(FILE_NAME+"Error in the Search Process:"+e.toString());
      e.printStackTrace();
      result=null;
    }
    return result;
  }
  private ArrayList doAddProcess(ESupplyGlobalParameters loginbean,HttpServletRequest request,HttpServletResponse response)
  {
    
    
    QMSSellRatesDOB sellRates        = null;
    ArrayList       ratesList        = new ArrayList();
    QMSSellRatesDOB sellRatesDtl     = null;
    HashMap  slabRates               = null;
    
    String  operation                = null;
    
    String[] checkBoxValue           = null;
    String[] buyRateId               = null;
    String[] versionNo               = null;//@@Added by Kameswari for the WPBN issue-146448 on 03/01/09
    String[] sellRateId              = null;
    String[] carrier_id              = null;
    String[] serviceLevel            = null;
    String[] minValue                = null;
   // String[] flatValue               = null;
    String   flatValue               = null;
    String[] palletCapacity          = null;
    String[] palletBuyRate           = null;
    String[] averageUplift           = null;
    String[] perBuyRate              = null;
    String[] looseSpace              = null;
    String[] lowerBound              = null;
    String[] upperBound              = null;
    String[] notes                   = null;
    String[] frequency               = null;
    String[] laneNumber              = null;
    String[] consoleType             = null;
    String[] transitTime             = null;
    
    String   lineNumber              = null;
    
    String[] weightBreakSlab         = null;
    String[] slabValue               = null;
    String[] csrValues               = null;
    
    String[] minLineNumber           = null;
    String flatLineNumber          = null;
    String[] servicelevel            = null;

    String  minimumRate              = null;
    String[]  flatRate                 = null;
    
    String originLocation            = null;
    String originCountry             = null;
    String destinationLocation       = null;
    String destinationCountry        = null;
    String shipmentMode              = null;
    String currencyId                = null;
    String weightBreak               = null;
    String rateType                  = null;
    String weightClass               = null;
    String surValue                  = null;
    
    String accessLevel               = null;
    String slabValues                = null;
    String slabLowerBound            = null;
    String slabUpperBound            = null;
    String chargeRateInd             = null;
    String terminalId                = null;
    
    double marginPercentage           = 0;
    
    
    QMSSellRatesSessionHome home   = null;
    QMSSellRatesSession     remote = null;
    
    try
    {
      checkBoxValue   = request.getParameterValues("checkBoxValue");
      buyRateId       = request.getParameterValues("buyRateId");
      versionNo       = request.getParameterValues("versionNo");//@@Added by Kameswari for the WPBN issue-146448 on 03/01/09
      sellRateId      = request.getParameterValues("sellRateId");
      carrier_id      = request.getParameterValues("carrier_id");
      serviceLevel    = request.getParameterValues("serviceLevel");
      servicelevel    = request.getParameterValues("servicelevel");
      minValue        = request.getParameterValues("minValue");
     // flatValue       = request.getParameterValues("flatValue");
      palletCapacity  = request.getParameterValues("palletCapacity");
      palletBuyRate   = request.getParameterValues("palletBuyRate");
      averageUplift   = request.getParameterValues("averageUplift");
      perBuyRate      = request.getParameterValues("perBuyRate");
      looseSpace      = request.getParameterValues("looseSpace");
      lowerBound      = request.getParameterValues("lowerBound");
      upperBound      = request.getParameterValues("upperBound");
      notes           = request.getParameterValues("notes");
      frequency       = request.getParameterValues("frequency");
      laneNumber      = request.getParameterValues("laneNumber");
      consoleType     = request.getParameterValues("consoleType");
      transitTime     = request.getParameterValues("transitTime");
      //lineNumber      = request.getParameterValues("lineNumber");
      
      minLineNumber   = request.getParameterValues("minLineNumber");
      //flatLineNumber  = request.getParameterValues("flatLineNumber");
      
      weightBreakSlab = request.getParameterValues("weightBreakSlab");
      csrValues       = request.getParameterValues("csrValues");
  //    surValue       =   request.getParameter("surValue");
      minimumRate     = request.getParameter("minRate");
      // flatRate        = request.getParameter("flatRate");//@@MOdified by Kameswari for Surcharge Enhancements

      flatRate        = request.getParameterValues("flatRate");
      
      
      operation           = request.getParameter("Operation");
      shipmentMode        = request.getParameter("shipmentMode");
      originLocation      = request.getParameter("origin");
      originCountry       = request.getParameter("originCountry");
      destinationLocation = request.getParameter("destination");
      destinationCountry  = request.getParameter("destinationCountry");
      currencyId          = request.getParameter("baseCurrency");
      weightBreak         = request.getParameter("weightBreak");
      rateType            = request.getParameter("rateType");
      weightClass         = request.getParameter("weightClass");
      terminalId          = request.getParameter("terminalId");
      accessLevel         = request.getParameter("accessLevel");
      
      if(accessLevel==null || "".equalsIgnoreCase(accessLevel))
      {
      
          if("HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
              accessLevel = "H";
          else if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
              accessLevel = "A";
          else if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
              accessLevel = "O";
      }
      
     // Logger.info(FILE_NAME,"accessLevel..."+accessLevel);
      
      sellRates = new QMSSellRatesDOB();
      sellRates.setRec_cons_flag("C");
      sellRates.setOperation(operation);
      sellRates.setShipmentMode(shipmentMode);
      sellRates.setWeightBreak(weightBreak);
      sellRates.setRateType(rateType);
      sellRates.setCurrencyId(currencyId);
      sellRates.setWeightClass(weightClass);
      sellRates.setOrigin(originLocation);
      sellRates.setOriginCountry(originCountry);
      sellRates.setDestination(destinationLocation);
      sellRates.setDestinationCountry(destinationCountry);
      sellRates.setCreatedBy(loginbean.getUserId());
      sellRates.setCreatedTimestamp(new java.sql.Timestamp((new java.util.Date()).getTime()));
      sellRates.setActiveInactiveFlag("A");
      
      //@@In Case of Modify, the sell rate will be modified for that specific terminal.
      sellRates.setAccessLevel(accessLevel);
      
      if(terminalId!=null && terminalId.trim().length()!=0)
          sellRates.setTerminalId(terminalId);
      else
          sellRates.setTerminalId(loginbean.getTerminalId());
      
      
      ratesList.add(0,sellRates);
      
      sellRates   = null;
      
      for(int i=0;i<checkBoxValue.length;i++)
      {
        if("YES".equalsIgnoreCase(checkBoxValue[i]))
        {
          sellRates = new QMSSellRatesDOB();
          slabRates = new HashMap();
          
          sellRates.setCarrier_id(carrier_id[i]);
          sellRates.setBuyRateId(buyRateId[i]);
          sellRates.setVersionNo(versionNo[i]);//@@Added by Kameswari for the WPBN issue-146448 on 03/01/09
          sellRates.setServiceLevel(serviceLevel[i]);
          if(sellRateId!=null)
          {
            if(sellRateId[i]!=null && sellRateId[i].trim().length()!=0)
              sellRates.setSellRateId(Long.parseLong(sellRateId[i]));
          }
          sellRates.setNotes(notes!=null?notes[i]:"");
          sellRates.setFrequency(frequency[i]);
          sellRates.setConsoleType(!"null".equalsIgnoreCase(consoleType[i])?consoleType[i]:null);
          sellRates.setLaneNumber(Integer.parseInt(laneNumber[i]));
          sellRates.setTransitTime(!"null".equalsIgnoreCase(transitTime[i])?transitTime[i]:null);
          //sellRates.setLineNumber(Integer.parseInt(lineNumber[i]));
          
          if("Flat".equalsIgnoreCase(weightBreak))
          {
            sellRates.setPalletCapacity(Double.parseDouble(palletCapacity[i]));
            sellRates.setPalletBuyRate(Double.parseDouble(palletBuyRate[i]));
            sellRates.setAverageUplift(Double.parseDouble(averageUplift[i]));
            sellRates.setLoseSpace(Double.parseDouble(looseSpace[i]));
            //sellRates.setServiceLevel(serviceLevel[i]);
            for(int j=0;j<weightBreakSlab.length;j++)//@@since two rows will get inserted, one with minimum rate & other with flat rate (and will avoid repetitive code in DAO).
            {
             if(j>0)
             {
              flatValue      = request.getParameter("flatValue"+i+""+j);
            
             
             // logger.info("weightBreakSlab"+weightBreakSlab[j]);
             flatLineNumber      = request.getParameter("flatLineNumber"+i+""+j);
             }
             if(j==0||(j>0&&flatValue!=null&&flatValue.trim().length()>0&&!("null".equalsIgnoreCase(flatValue))))
              {
                
              sellRatesDtl  = new QMSSellRatesDOB();
              sellRatesDtl.setUpperBound(Integer.parseInt(upperBound[i]));
              sellRatesDtl.setLowerBound(Integer.parseInt(lowerBound[i]));
              sellRatesDtl.setServiceLevel(servicelevel[j]);
             // sellRatesDtl.setWeightBreakSlab(j==0?"Min":"Flat");
               sellRatesDtl.setWeightBreakSlab(weightBreakSlab[j]);
              sellRatesDtl.setBuyRate(j==0?Double.parseDouble(minValue[i]):Double.parseDouble(flatValue));
              sellRatesDtl.setChargeRate(j==0?Double.parseDouble(minimumRate):Double.parseDouble(flatRate[j-1]));
              sellRatesDtl.setLineNumber(j==0?Integer.parseInt(minLineNumber[i]):Integer.parseInt(flatLineNumber));
              if(j==0)
                marginPercentage = ((Double.parseDouble(minimumRate)-Double.parseDouble(minValue[i]))/Double.parseDouble(minValue[i]))*100;
              else
                marginPercentage = ((Double.parseDouble(flatRate[j-1])-Double.parseDouble(flatValue))/Double.parseDouble(flatValue))*100;
              
              //System.out.println("marginPercentage::"+round(marginPercentage));
              
              sellRatesDtl.setMarginPer(round(marginPercentage));
              slabRates.put(""+j,sellRatesDtl);
              }
            }
          }
          else if("Slab".equalsIgnoreCase(weightBreak))
          {
            for(int j=0;j<csrValues.length;j++)
            {
              slabValues      = request.getParameter("slabValue"+i+""+j);
              slabLowerBound  = request.getParameter("lowerBound"+i+""+j);
              slabUpperBound  = request.getParameter("upperBound"+i+""+j);
              chargeRateInd   = request.getParameter("chargeRateInd"+i+""+j);
              lineNumber      = request.getParameter("lineNumber"+i+""+j);
              
              //Logger.info(FILE_NAME,"weightBreakSlab::"+weightBreakSlab[j]);
              //Logger.info(FILE_NAME,"slabValue::"+slabValues);
              
              if(!"-".equalsIgnoreCase(slabValues))
              {
                sellRatesDtl  = new QMSSellRatesDOB();
                sellRatesDtl.setServiceLevel(servicelevel[j]);
                sellRatesDtl.setWeightBreakSlab(weightBreakSlab[j]);
                sellRatesDtl.setBuyRate(Double.parseDouble(slabValues));
                sellRatesDtl.setChargeRate(Double.parseDouble(csrValues[j]));
                sellRatesDtl.setUpperBound(Long.parseLong(slabUpperBound));
                sellRatesDtl.setLowerBound(Long.parseLong(slabLowerBound));
                sellRatesDtl.setLineNumber(Integer.parseInt(lineNumber));
                sellRatesDtl.setChargeRateInd(!"null".equalsIgnoreCase(chargeRateInd)?chargeRateInd:null);
                marginPercentage = ((Double.parseDouble(csrValues[j])-Double.parseDouble(slabValues))/Double.parseDouble(slabValues))*100;
                sellRatesDtl.setMarginPer(round(marginPercentage));
                slabRates.put(""+j,sellRatesDtl);
              }
            }
          /* for(int k=csrValues.length;k<weightBreakSlab.length;k++)
            {
               slabValues      = request.getParameter("slabValue"+i+""+k);
              slabLowerBound  = request.getParameter("lowerBound"+i+""+k);
              slabUpperBound  = request.getParameter("upperBound"+i+""+k);
              chargeRateInd   = request.getParameter("chargeRateInd"+i+""+k);
              lineNumber      = request.getParameter("lineNumber"+i+""+k);
              if(!"-".equalsIgnoreCase(slabValues))
              {
                sellRatesDtl  = new QMSSellRatesDOB();
                sellRatesDtl.setServiceLevel("SCH");
                sellRatesDtl.setWeightBreakSlab(weightBreakSlab[k]);
                sellRatesDtl.setBuyRate(Double.parseDouble(slabValues));
                sellRatesDtl.setChargeRate(Double.parseDouble(surValue));
                sellRatesDtl.setUpperBound(Long.parseLong(slabUpperBound));
                sellRatesDtl.setLowerBound(Long.parseLong(slabLowerBound));
                sellRatesDtl.setLineNumber(Integer.parseInt(lineNumber));
                sellRatesDtl.setChargeRateInd(!"null".equalsIgnoreCase(chargeRateInd)?chargeRateInd:null);
                marginPercentage = ((Double.parseDouble(surValue)-Double.parseDouble(slabValues))/Double.parseDouble(slabValues))*100;
                sellRatesDtl.setMarginPer(round(marginPercentage));
                slabRates.put(""+k,sellRatesDtl);
              }
            }*/
            //Logger.info(FILE_NAME,"slabRates::"+slabRates.size());
          }
          sellRates.setSellRates(slabRates);
          ratesList.add(sellRates);
        }
      }
      
      home    = (QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
      remote  = (QMSSellRatesSession)home.create();
      
      boolean flag = remote.insertSellRateDetails(ratesList);
      
      //Logger.info(FILE_NAME,"flagflagflagflagflagflag::::"+flag);
      //QMSSellRatesDAO tempDAO = new QMSSellRatesDAO();
      //tempDAO.create(ratesList,request.getParameter("Operation"));
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in Add Process:"+e.toString());
      logger.error(FILE_NAME+"Error in Add Process:"+e.toString());
      ratesList = null;
      e.printStackTrace();
    }
    return ratesList;
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
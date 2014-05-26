
/**
 * @ (#) QMSBuyChargesController.java
 * Copyright (c) 2001 The Four Soft Pvt Ltd.,
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */

/**
 * File       : QMSBuyChargesController.java
 * Sub-Module : Buy charges
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * @date      : 28-07-2005
 * Modified by: Date     Reason
 */
 

import com.foursoft.esupply.common.exception.FoursoftException;
import com.qms.operations.charges.java.BuySellChargesEnterIdDOB;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.PrintWriter;
import java.io.IOException;
import javax.ejb.ObjectNotFoundException;

import java.util.ArrayList;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.LookUpBean;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;

import com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome;
import com.qms.operations.charges.ejb.sls.ChargeMasterSession;
import com.qms.operations.charges.java.BuychargesDtlDOB;
import com.qms.operations.charges.java.BuychargesHDRDOB;

public class QMSBuyChargesController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static final String FILE_NAME = "QMSBuyChargesController";
  private static Logger logger = null;
  
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSBuyChargesController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
		  doPost(request,response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {

		ESupplyGlobalParameters	loginbean			= null;    
    HttpSession 			    session 	      =	request.getSession();
    String operation                      = request.getParameter("Operation");
    String subOperation                   = request.getParameter("subOperation");
		loginbean						                  = (ESupplyGlobalParameters)session.getAttribute("loginbean");
    BuychargesHDRDOB   buychargesHDRDOB   =null;
    StringBuffer        errorMsg          = new StringBuffer("");
    String              chargeId          = "";
    //String              chargeBasisId     = "";
    String              chargeDescriptionId = "";
    String              terminalId          = "";
    ArrayList            densityGroupList      = null;
    ArrayList            chargeBasisList      = null;
    try
    {
     // Logger.info(FILE_NAME,"in dopost method::operation::1234"+operation);
      //Logger.info(FILE_NAME,"in dopost method::subOperation::"+subOperation);
      if(operation!=null && subOperation!=null)
      {
        if(operation.equals("Add"))
        {
          if(subOperation.equals("Add"))
            { doAddProcess(loginbean,request,response);}
          else 
            {
        		 //@@Added by Kameswari for the WPBN issue-54554
           densityGroupList   =   doGetDensityGroupList(request,response);  
           chargeBasisList   =  doGetChargeBasisList(request,response);
          session.setAttribute("densityGroupList",densityGroupList); 
          session.setAttribute("chargeBasisList",chargeBasisList); 
          //@@WPBN issue-54554
                doDispatcher(request, response, "etrans/QMSBuyChargesMaster.jsp?Operation="+operation+"&subOperation="+operation);            
            }
        }else if(operation.equals("Modify") || operation.equals("View") || operation.equals("Delete"))
        {
          if(subOperation.equals("enter"))
          {
            chargeId         = request.getParameter("chargeId");
            //chargeBasisId    = request.getParameter("chargeBasisId"); 
            chargeDescriptionId = request.getParameter("chargeDescriptionId");
            terminalId        = request.getParameter("terminalId");
             //@@Added by Kameswari for the WPBN issue-54554
             densityGroupList   =   doGetDensityGroupList(request,response);  
           chargeBasisList   =  doGetChargeBasisList(request,response);
          session.setAttribute("densityGroupList",densityGroupList); 	
          session.setAttribute("chargeBasisList",chargeBasisList); 
              //@@Added by Kameswari for the WPBN issue-54554
            if(terminalId==null || terminalId.equals(""))
            { terminalId=loginbean.getTerminalId();}
            request.setAttribute("terminalId",terminalId);
            buychargesHDRDOB = doRetriveDetails(loginbean,request,response);
            if(buychargesHDRDOB!=null)
              { 
                request.setAttribute("BuyChargesHDRDtls",buychargesHDRDOB);
                if(operation.equals("Modify"))
                  { doDispatcher(request, response, "etrans/QMSBuyChargesMaster.jsp?Operation="+operation+"&subOperation="+operation);}
                else
                  { doDispatcher(request, response, "etrans/QMSBuyChargesMasterDetails.jsp?");}
              }
            else
              { throw new Exception("exception while retriving the data-->");}
          }else if(subOperation.equals("Modify"))
          {
            doModifyProcess(loginbean,request,response);
          }else if(subOperation.equals("Delete"))
          {
            
          }else if(subOperation.equals("View"))
          {
            
          }else
          {
                //Logger.info(FILE_NAME,"Operation::::in else");
                //terminalIdList = doGetTerminalList(loginbean.getTerminalId());
         				doDispatcher(request, response, "etrans/QMSBuyChargesMasterEnterId.jsp?Operation="+operation+"&subOperation=");            
          }
        }
      }
    }catch(ObjectNotFoundException e)
    {
      //Logger.info(FILE_NAME,"Exception While doPost Retrieving the data :"+e);
      logger.info(FILE_NAME+"Exception While doPost Retrieving the data :"+e);
      errorMsg.append("No BuyCharges defined for the data:\n");
      errorMsg.append("ChargeId:");
      errorMsg.append(chargeId);
      //errorMsg.append("\tChargeBasisId:");
      //errorMsg.append(chargeBasisId);
      errorMsg.append("\tChargeDescriptionId:");
      errorMsg.append(chargeDescriptionId);
      request.setAttribute("errorMsg",errorMsg.toString());
      doForwardToErrorPage(loginbean,request,response);
    }

    catch(Exception e)
    {
        //Logger.info(FILE_NAME,"Exception in doPost method while forwarding");
        logger.info(FILE_NAME+"Exception in doPost method while forwarding");
        doForwardToErrorPage(loginbean,request,response);
    }
  }
  
 /* private ArrayList doGetTerminalList(String terminalId)throws Exception
  {
    ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    ArrayList               terminalList  = null;
    
    try
    {
      if(terminalId!=null && !"".equals(terminalId))
      {
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();        
      }else
      {
        throw new Exception();
      }
      
    }catch(Exception e)
    {
       Logger.info(FILE_NAME,"Exception while retrieving the details---->"+e);
       throw new Exception();
    }
    
    return terminalList
  }*/
  private BuychargesHDRDOB doRetriveDetails(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)throws ObjectNotFoundException
  {
    ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    BuychargesHDRDOB    buychargesHDRDOB  = null;
    String              chargeId          = "";
    //String              chargeBasisId     = "";
    String            chargeDescriptionId = "";
    String            operation           = request.getParameter("Operation");
    BuySellChargesEnterIdDOB buySellChargesEnterIdDOB = new BuySellChargesEnterIdDOB();
    StringBuffer        errorMsg          = new StringBuffer("");
    try
    {
      chargeId      = request.getParameter("chargeId");
      //chargeBasisId = request.getParameter("chargeBasisId");
      chargeDescriptionId = request.getParameter("chargeDescriptionId");
      String terminalId   = (String)request.getAttribute("terminalId");
      buySellChargesEnterIdDOB.setChargeId(chargeId);
      buySellChargesEnterIdDOB.setChargeDescId(chargeDescriptionId);
      buySellChargesEnterIdDOB.setTerminalId(terminalId);
      buySellChargesEnterIdDOB.setOperation(operation);
      if(operation!=null && !operation.equals("") &&  chargeId!=null &&  !"".equals(chargeId) && chargeDescriptionId!=null && !"".equals(chargeDescriptionId))
      {
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();
        try{
        buychargesHDRDOB =  remote.loadBuychargeDetails(buySellChargesEnterIdDOB,loginbean);
        }catch(FoursoftException e)
        {
            //Logger.info(FILE_NAME,"Exception While doPost Retrieving the data :"+e);
            logger.info(FILE_NAME+"Exception While doPost Retrieving the data :"+e);
            if("Modify".equals(operation))
            { errorMsg.append("Invalid Access of data,You cannot access Modify the data due to following Reasons:\n");
              errorMsg.append("1.you are trying to access higher level data.");
              errorMsg.append("2.you are trying to access Terminal which is not in your hirarchy.");}
            else if("View".equals(operation))
            { errorMsg.append("Invalid Access of data,As the terminal is not in your hirarchy:\n");}
            errorMsg.append("ChargeId:");
            errorMsg.append(chargeId);
            //errorMsg.append("\tChargeBasisId:");
            //errorMsg.append(chargeBasisId);
            errorMsg.append("\tChargeDescriptionId:");
            errorMsg.append(chargeDescriptionId);
            errorMsg.append("\tTerminalId:");
            errorMsg.append(terminalId);
            request.setAttribute("errorMsg",errorMsg.toString());
            doForwardToErrorPage(loginbean,request,response);          
        }
      }else
      {
        throw new Exception("Invalid data entered--->");
      }
    }catch(ObjectNotFoundException e)
    {
      //Logger.info(FILE_NAME,"Exception While Retrieving the data :"+e);
      logger.info(FILE_NAME+"Exception While Retrieving the data :"+e);
      throw new ObjectNotFoundException("NO data found");
    }
    catch(Exception e)
    {
      //Logger.info(FILE_NAME,"Exception while retrieving the details---->"+e);
      logger.info(FILE_NAME+"Exception while retrieving the details---->"+e);
      doForwardToErrorPage(loginbean,request,response);
    }
    return buychargesHDRDOB;
  }
  private void doAddProcess(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)
  {
    ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    ArrayList               dataList      = null;
    ArrayList               resultList    = null;
    ArrayList               insertedList  = null;
    ArrayList               invalidList   = null;
    ArrayList               dataAtHiger   = null;
		ArrayList						keyValueList			= new ArrayList();
		StringBuffer				errorMessage		  =	new StringBuffer();
		ErrorMessage				errorMessageObject= null;
    BuychargesHDRDOB    buychargesHDRDOB  = null;
    String operation    = request.getParameter("Operation");
    String subOperation = request.getParameter("subOperation");  
    String chargeId     = "";
    //String chargeBasis  = "";
    String chargeDescId = "";
    String terminalId  = "";
    try
    {
      dataList  = doGetDetails(loginbean,request,response);
      //Logger.info(FILE_NAME,"datalist::"+dataList);
      if(dataList!=null && dataList.size()>0)
      {
          home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote    = (ChargeMasterSession)home.create();
          resultList= remote.insertBuyChargesDetails(dataList,loginbean);
          if(resultList!=null && resultList.size()>0)
          {
                insertedList  = (ArrayList)resultList.get(0);
                invalidList   = (ArrayList)resultList.get(1);
                dataAtHiger   = (ArrayList)resultList.get(2);
                if(insertedList!=null && insertedList.size()>0)
                {
                  errorMessage.append("Records inserted Successfully for chargeIds:\n");
                  int insertedListSize  = insertedList.size();
                  for(int i=0;i<insertedListSize;i++)
                  {
                    buychargesHDRDOB  = (BuychargesHDRDOB)insertedList.get(i);
                    chargeId          = buychargesHDRDOB.getChargeId();
                    chargeDescId       = buychargesHDRDOB.getChargeDescId();
                    errorMessage.append("ChargeId :");
                    errorMessage.append(chargeId);
                    errorMessage.append("\t");
                    errorMessage.append("ChargeDescriptionId :");
                    errorMessage.append(chargeDescId);
                    errorMessage.append("\n");
                  }
                }
                if(dataAtHiger!=null && dataAtHiger.size()>0)
                {
                  errorMessage.append("Records not inserted As Buycharge is defined At higerLevel:\n");
                  int insertedListSize  = insertedList.size();
                  for(int i=0;i<insertedListSize;i++)
                  {
                    buychargesHDRDOB  = (BuychargesHDRDOB)insertedList.get(i);
                    chargeId          = buychargesHDRDOB.getChargeId();
                    chargeDescId      = buychargesHDRDOB.getChargeDescId();
                    terminalId        = buychargesHDRDOB.getDataAtHigher();
                    errorMessage.append("ChargeId :");
                    errorMessage.append(chargeId);
                    errorMessage.append("\t");
                    errorMessage.append("ChargeDescriptionId :");
                    errorMessage.append(chargeDescId);
                    errorMessage.append("\t");
                    /*errorMessage.append("HigherTerminalId:");
                    errorMessage.append(terminalId);
                    errorMessage.append("\n");*/
                  }                  
                }
                if(invalidList!=null && invalidList.size()>0)
                {
                  errorMessage.append("Records not inserted As Entered Invalid data/Buycharges already exist for:\n");
                  int invalidListSize  = invalidList.size();
                  for(int i=0;i<invalidListSize;i++)
                  {
                    buychargesHDRDOB  = (BuychargesHDRDOB)invalidList.get(i);
                    chargeId          = buychargesHDRDOB.getChargeId();
                    chargeDescId       = buychargesHDRDOB.getChargeDescId();
                    errorMessage.append("ChargeId :");
                    errorMessage.append(chargeId);
                    errorMessage.append("\t");
                    errorMessage.append("ChargeDescriptionId :");
                    errorMessage.append(chargeDescId);
                    errorMessage.append("\n");
                  }
                }        		
                errorMessageObject = new ErrorMessage(errorMessage.toString(),"QMSBuyChargesController?Operation=Add&subOperation="); 
                keyValueList.add(new KeyValue("ErrorCode","QMS")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
          }else
          {
            throw new Exception("Nodata inserted");
          }
      }else
      {
            throw new Exception("Nodata inserted");
      }
    }catch(Exception e)
    {
            //Logger.info(FILE_NAME,"Exception while calling bean the data"+e);
            logger.info(FILE_NAME+"Exception while calling bean the data"+e);
            doForwardToErrorPage(loginbean,request,response);
    }
    
  }
  
  private void doModifyProcess(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)
  {
    ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    ArrayList               dataList      = null;
    boolean                 modified      = false;
		ArrayList						keyValueList			= new ArrayList();
		StringBuffer				errorMessage		  =	new StringBuffer();
		ErrorMessage				errorMessageObject= null;  
    BuychargesHDRDOB    buychargesHDRDOB  = null;
    String operation    = request.getParameter("Operation");
    String subOperation = request.getParameter("subOperation");     
    try
    {
      dataList  = doGetDetails(loginbean,request,response);
      if(dataList!=null && dataList.size()>0)
      {
        buychargesHDRDOB  = (BuychargesHDRDOB)dataList.get(0);
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();        
        modified  = remote.modifyBuyChargesDetails(buychargesHDRDOB,loginbean);
        if(modified)
        {
                errorMessage.append("Record successfully modified for the Ids:\n");
                errorMessage.append("ChargeId:");
                errorMessage.append(buychargesHDRDOB.getChargeId());
                errorMessage.append("\tChargeDescriptionId:");
                errorMessage.append(buychargesHDRDOB.getChargeDescId());
                
                errorMessageObject = new ErrorMessage(errorMessage.toString(),"QMSBuyChargesController?Operation=Modify&subOperation="); 
                keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
        }else
        {
          throw new Exception("Record not modified");
        }
      }else
      {
        throw new Exception("Invalid data");
      }
    }catch(Exception e)
    {
            //Logger.info(FILE_NAME,"Exception while calling bean the data"+e);
            logger.info(FILE_NAME+"Exception while calling bean the data"+e);
            doForwardToErrorPage(loginbean,request,response);
    }
  }
  private ArrayList doGetDetails(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)
  {

      String[] chargeId					          =	request.getParameterValues("chargeId");
      String[] chargeDescId               = request.getParameterValues("chargeDescriptionId");
      String[] chargeBasisId				      =	request.getParameterValues("chargeBasisId");
      String[] chargeCurrencyId			      =	request.getParameterValues("chargeCurrencyId");
      String[] rateBreak					        =	request.getParameterValues("rateBreak");
      String[] chargeRateType				      =	request.getParameterValues("chargeRateType");
      String[] rateCalculation			      =	request.getParameterValues("rateCalculation");
	  String[]	densityGrpCode				  =	request.getParameterValues("densityGrpCode");/* naresh*/
      String[] charge						          =	request.getParameterValues("charge");
      String[] chargeSlab					        =	request.getParameterValues("chargeSlab");
      String[] chargeRate					        =	request.getParameterValues("chargeRate");
      String[] chargeFlatRate				      =	request.getParameterValues("chargeFlatRate");
      
    	BuychargesHDRDOB	buychargesHDRDOB  =	null;
      BuychargesDtlDOB	buychargesDtlDOB  =	null;
      ArrayList			dataList		          =	new ArrayList();
      ArrayList			dtlList			          =	null;
      String			tempChargeSlab	        =	"";
      double			tempChargeSlabD	        =	0.0;
      double			tempChargeRate	        =	0.0;
      int					rateI			              =	0;
      int					index			              =	0;
      int					rateILength		          =	0;
      int					flatRateI		            =	0;
      int					flatIndex		            =	0;
      int					flatRateILength	        =	0;
      String      terminalId               = request.getParameter("terminalId");
      String      accessLevel             = loginbean.getUserLevel();
      String      userId                  = loginbean.getUserId();
      java.sql.Timestamp   createDateTime = new java.sql.Timestamp((new java.util.Date()).getTime());
      try{
          if("HO_TERMINAL".equals(accessLevel))
            { accessLevel = "H";}
          else if("ADMN_TERMINAL".equals(accessLevel))
            { accessLevel = "A";}
          else if("OPER_TERMINAL".equals(accessLevel))
            { accessLevel = "O";}            
          int chargeLength  = chargeId.length;
          
          if(terminalId==null && "".equals(terminalId.trim()))
             terminalId = loginbean.getTerminalId();
          
          
          for(int i=0;i<chargeLength;i++)
          {
            buychargesHDRDOB	=	new BuychargesHDRDOB();
            buychargesHDRDOB.setChargeId(chargeId[i]);
            buychargesHDRDOB.setChargeDescId(chargeDescId[i]);
            buychargesHDRDOB.setChargeBasisId(chargeBasisId[i]);
            buychargesHDRDOB.setCurrencyId(chargeCurrencyId[i]);
            buychargesHDRDOB.setRateBreak(rateBreak[i]);
            buychargesHDRDOB.setRateType(chargeRateType[i]);
            buychargesHDRDOB.setWeightClass(rateCalculation[i]);
            buychargesHDRDOB.setTerminalId(terminalId);
            buychargesHDRDOB.setAccessLevel(accessLevel);
            buychargesHDRDOB.setUserId(userId);
            buychargesHDRDOB.setCreateTime(createDateTime);
			      buychargesHDRDOB.setDensityGrpCode((densityGrpCode[i]!=null) ?densityGrpCode[i]  : "");
        
          //	rateI	=	rateIndex;
            
            if(rateBreak[i]!=null && (rateBreak[i].equals("Absolute") || rateBreak[i].equals("Percent")))
            {
              index		=	1;
            }else if(rateBreak[i]!=null && (rateBreak[i].equals("Flat") || rateBreak[i].equals("Flat%")))
            {
              index		=	4;
            }else if(rateBreak[i]!=null && (rateBreak[i].equals("Slab") || rateBreak[i].equals("Slab%")))
            {
              index		=	14;
              if(chargeRateType[i].equals("Both"))
                {	flatIndex	=	11;}
            }
            rateILength		=	rateI+index;
            flatRateILength	=	flatRateI+flatIndex;
            dtlList				=	new ArrayList();
            while(rateI<rateILength)
            {
              buychargesDtlDOB	=	new BuychargesDtlDOB();
              if(chargeSlab[rateI]!=null && !chargeSlab[rateI].equals("") && (chargeSlab[rateI].equals("BASE") || chargeSlab[rateI].equals("MIN") || chargeSlab[rateI].equals("MAX") || chargeSlab[rateI].equals("Flat") || chargeSlab[rateI].equals("AbsRPersent")))
              {
                if(!"".equals(chargeRate[rateI].trim()))
                {
                  if((Double.parseDouble(chargeRate[rateI]))>0.0)
                  {
                      buychargesDtlDOB.setChargeRate(Double.parseDouble(chargeRate[rateI]));
                      buychargesDtlDOB.setChargeSlab(chargeSlab[rateI]);
                      dtlList.add(buychargesDtlDOB);
                  }
                }
              }else if(chargeSlab[rateI]!=null && !chargeSlab[rateI].equals(""))
              {
                tempChargeSlab	=	chargeSlab[rateI];
                tempChargeSlabD = Double.parseDouble(tempChargeSlab);
                if(chargeRateType[i].equals("Both"))
                {
                  if(chargeRate[rateI].trim().equals(""))
                  {
                    if(chargeFlatRate!=null && chargeFlatRate[flatRateI]!=null)
                    {
                      tempChargeRate = Double.parseDouble(chargeFlatRate[flatRateI]);
                      //buychargesDtlDOB.setChargeRate_indicator("F");
                      buychargesDtlDOB.setChargeRate_indicator("S");//Higlighted text is for Slab
                    }
                  }
                  flatRateI	= flatRateI+1;
                }
                if(!chargeRate[rateI].trim().equals(""))
                {	
                  tempChargeRate  =   Double.parseDouble(chargeRate[rateI]);
                  //buychargesDtlDOB.setChargeRate_indicator("S");
                  buychargesDtlDOB.setChargeRate_indicator("F");
                }
                buychargesDtlDOB.setChargeRate(tempChargeRate);
                buychargesDtlDOB.setChargeSlab(tempChargeSlab);
                if(tempChargeSlabD<0)
                {
                  buychargesDtlDOB.setLowerBound(0.0);
                }else
                {
                  buychargesDtlDOB.setLowerBound(tempChargeSlabD);
                }
                if(rateI+1<rateILength && !chargeSlab[rateI+1].equals(""))
                {
                  buychargesDtlDOB.setUpperBound(Double.parseDouble(chargeSlab[rateI+1]));
                }else
                {
                  buychargesDtlDOB.setUpperBound(999999);
                }
        
                dtlList.add(buychargesDtlDOB);
              }else if(chargeSlab[rateI].equals(""))
              {
                rateI=rateILength-1;
                if(chargeRateType[i].equals("Both"))
                  {	flatRateI=flatRateILength;}
              }
              rateI	=	rateI+1;
            }
            buychargesHDRDOB.setBuyChargeDtlList(dtlList);
        
            dataList.add(buychargesHDRDOB);
          }
      }catch(Exception e)
      {
            //Logger.info(FILE_NAME,"Exception while reading the data :"+e);
            logger.info(FILE_NAME+"Exception while reading the data :"+e);
            doForwardToErrorPage(loginbean,request,response);
      }
      
      return dataList;
    }
  
  private void doForwardToErrorPage(ESupplyGlobalParameters loginbean,HttpServletRequest request,
								HttpServletResponse response)
  {
    String operation    = request.getParameter("Operation");
    String subOperation = request.getParameter("subOperation");
    String nextNavigation = "";
		ArrayList						keyValueList			= new ArrayList();
		StringBuffer					errorMessage		=	new StringBuffer();
		ErrorMessage				errorMessageObject= null;
    String              errorMsg          = "";
    nextNavigation  = "QMSBuyChargesController?Operation="+operation+"&subOperation=";
    try{
             errorMsg = (String)request.getAttribute("errorMsg");
             //Logger.info(FILE_NAME,"insdfadnfa--------"+errorMsg);
            if("".equals(errorMsg) || errorMsg==null)
            {
              if(operation.equals("Add"))
                { errorMessage.append("Exception While inserting the records :");}
              else if(operation.equals("Modify"))
                { errorMessage.append("Exception While Modifying the records :");}
              else if(operation.equals("View"))
                { errorMessage.append("Exception While Viewing the records :");}              
            }else
            {
              errorMessage.append(errorMsg);
            }
            errorMessageObject = new ErrorMessage(errorMessage.toString(),nextNavigation); 
						keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
						keyValueList.add(new KeyValue("Operation",operation)); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);    
						doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
    }catch(Exception e)
    {
      //Logger.info(FILE_NAME,"Exception while forwarding the controller"+e);
      logger.info(FILE_NAME+"Exception while forwarding the controller"+e);
    }
    
  }
  //@@Added by Kameswari for the WPBN issue-54554
   private ArrayList  doGetDensityGroupList(HttpServletRequest request,HttpServletResponse response)
  {
    ChargeMasterSessionHome home                = null;
    ChargeMasterSession     remote              = null;
    ArrayList              densityGroupCodeList = null;
    try
    {
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();
        
        densityGroupCodeList  = remote.getDensityGroupCodeList();
    }
    catch(Exception e)
    {
      //Logger.info(FILE_NAME,"Exception while forwarding the controller"+e);
      logger.info(FILE_NAME+"Exception while retreiving density group code list"+e);
    }
    return densityGroupCodeList;
  }
  //@@WPBN issue-54554
    private ArrayList  doGetChargeBasisList(HttpServletRequest request,HttpServletResponse response)
  {
    ChargeMasterSessionHome home                = null;
    ChargeMasterSession     remote              = null;
    ArrayList           getChargeBasisList        = null;
    try
    {
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();
        
        getChargeBasisList  = remote.getChargeBasisList();
    }
    catch(Exception e)
    {
      //Logger.info(FILE_NAME,"Exception while forwarding the controller"+e);
      logger.info(FILE_NAME+"Exception while retreiving density group code list"+e);
    }
    return getChargeBasisList;
  }
/**
	 * This is method is used for dispatching to a particular file
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the responsethe servlet sends to the client
	 * @param forwardFile 	a String that contains where to forward
	 *
	 * @exception IOException if an input or output error is detected when the servlet handles the POST request
	 * @exception ServletException if the request for the POST could not be handled.
	 */
	public void doDispatcher(HttpServletRequest request,
								HttpServletResponse response,
								String forwardFile) throws ServletException, IOException
	{
		try
		{
		
      RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
      logger.info("rd"+response.getBufferSize());
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
      logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}
}
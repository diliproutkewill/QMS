/**
 * @ (#) QMSSellChargesController.java

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
 * File : QMSSellChargesController.java
 * Sub-Module : Buy charges master
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package etrans;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.qms.operations.charges.java.BuySellChargesEnterIdDOB;
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

public class QMSSellChargesController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
    private static final String FILE_NAME = "QMSSellChargesController";
	private static Logger logger = null;

  public void init(ServletConfig config) throws ServletException
  {
	logger  = Logger.getLogger(QMSSellChargesController.class);
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
    /*String              chargeBasisId     = "";
    String              rateBreak         = "";
    String              rateType          = "";*/
    String              chargeDescId      = "";
    String              nextNavigation      = "";
    String              terminalId        = "";
    String[]              sellchargeIds   = null;  
    String[]              chargeIds        = null;  
    /*String              chargeBasisId     = "";
    String              rateBreak         = "";
    String              rateType          = "";*/
    String[]               chargeDescIds    = null;
    String[]               terminalIds      = null;
    ArrayList						  chargesList			= new ArrayList();
		ArrayList						keyValueList			= new ArrayList();
   	ArrayList						acceptanceList		= null;
		StringBuffer				errorMessage		  =	new StringBuffer();
		ErrorMessage				errorMessageObject= null;    
    String              str_error          = null;
    ChargeMasterSessionHome  home          = null;
    ChargeMasterSession      remote        = null;
    int        count   = 0;
    String                   nodata       = null;
    try
    {
      //Logger.info(FILE_NAME,"in dopost method::operation   ::"+operation);
      //Logger.info(FILE_NAME,"in dopost method::subOperation::"+subOperation);
      request.removeAttribute("BuyChargesHDRDtls");
      if("Accept".equals(operation))
      {
          home             = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote           = (ChargeMasterSession)home.create();
       
        if("Insert".equalsIgnoreCase(subOperation))
         {
              chargeIds         = request.getParameterValues("chargeId");
              chargeDescIds     = request.getParameterValues("chargeDescriptionId");
              /*rateBreak        = request.getParameter("rateBreak");
              rateType         = request.getParameter("rateType"); */
              terminalIds       = request.getParameterValues("terminalId");
              sellchargeIds     = request.getParameterValues("sellchargeId");
    
              chargesList.add(sellchargeIds);
              chargesList.add(chargeIds);
              chargesList.add(chargeDescIds);
              chargesList.add(terminalIds);
              count = remote.insertSellChargeAccDtls(chargesList);
              nextNavigation  = "etrans/QMSSellChargesAccept.jsp?Operation="+operation+"&subOperation=search";
              if(count>0)
              {
                 errorMessage.append("Records were succesffuly inserted");
                 keyValueList.add(new KeyValue("ErrorCode","RSI")); 
              }
              else
              {
                 
                errorMessage.append("Error occurred while processing the data");
                keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
              }
             errorMessageObject = new ErrorMessage(errorMessage.toString(),nextNavigation); 
						
						keyValueList.add(new KeyValue("Operation",operation)); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);    
						doDispatcher(request,response,"QMSESupplyErrorPage.jsp"); 
         }
         else if("Modify".equalsIgnoreCase(subOperation))
         {
              chargeId         = request.getParameter("chargeId");
              chargeDescId     = request.getParameter("chargeDescriptionId");
              /*rateBreak        = request.getParameter("rateBreak");
              rateType         = request.getParameter("rateType"); */
              terminalId      = request.getParameter("terminalId");
              request.setAttribute("terminalId",terminalId);
              buychargesHDRDOB = doRetriveDetails(loginbean,request,response);
              if(buychargesHDRDOB!=null)
              { 
                request.setAttribute("SellChargesHDRDtls",buychargesHDRDOB);              
                doDispatcher(request, response, "etrans/QMSSellChargesDetails.jsp?Operation="+operation+"&subOperation="+subOperation);
              }   
         }
         else if("Add".equalsIgnoreCase(subOperation))
        {
          acceptanceList   =    remote.getAcceptanceDetails();
          if(acceptanceList!=null)
          {
              request.setAttribute("acceptanceList",acceptanceList);
              doDispatcher(request, response, "etrans/QMSSellChargesAccept.jsp?Operation="+operation+"&subOperation=search");
          }
          else
          {
              nodata    =   "Data Not Found";
              request.setAttribute("errorMsg",nodata);
              doDispatcher(request, response, "etrans/QMSSellChargesAccept.jsp?Operation="+operation+"&subOperation=search");
  
          }
         }
          else if("Accept".equalsIgnoreCase(subOperation))
         {
            doModifyProcess(loginbean,request,response);
         }
      }
      
     else  if("Add".equals(operation))
      {
        chargeId         = request.getParameter("chargeId");
            //chargeBasisId    = request.getParameter("chargeBasisId");
        chargeDescId     = request.getParameter("chargeDescriptionId");
            
        if("Add".equals(subOperation))
        {
            if(chargeId!=null && chargeId.trim().length()>0 
               && chargeDescId!=null && chargeDescId.trim().length()>0) 
            {
                 
               
              buychargesHDRDOB = new BuychargesHDRDOB();
            
              buychargesHDRDOB.setChargeId(chargeId.trim());
              buychargesHDRDOB.setChargeDescId(chargeDescId.trim());
              buychargesHDRDOB.setChargeBasisId((request.getParameter("chargeBasisId")!=null)?request.getParameter("chargeBasisId"):"");
              buychargesHDRDOB.setDensityGrpCode((request.getParameter("densityGrpCode")!=null)?request.getParameter("densityGrpCode").trim():"");
              buychargesHDRDOB.setCurrencyId((request.getParameter("currencyId")!=null)?request.getParameter("currencyId").trim():"");
              buychargesHDRDOB.setTerminalId(loginbean.getTerminalId());
              
              str_error         = validateHeaderData(buychargesHDRDOB);
              
              //System.out.println("errormsg::"+str_error);
              if(str_error!=null && str_error.length()>0)
              {
                 request.setAttribute("errorMsg",str_error);
                 request.removeAttribute("BuyChargesHDRDtls");
                 doDispatcher(request, response, "etrans/QMSSellCharges.jsp?Operation="+operation+"&subOperation=search");
              }else
              {
                doAddProcess(loginbean,request,response);              
              }
            }else
            {
                 request.setAttribute("errorMsg","ChargeId and ChargeDescriptionId should not be empty");
                 doDispatcher(request, response, "etrans/QMSSellCharges.jsp?Operation="+operation+"&subOperation=search");
            }
          
        }else if("search".equals(subOperation))
        {
            chargeId         = request.getParameter("chargeId");
            //chargeBasisId    = request.getParameter("chargeBasisId");
            chargeDescId     = request.getParameter("chargeDescriptionId"); 
            
            if(chargeId!=null && chargeId.trim().length()>0 
               && chargeDescId!=null && chargeDescId.trim().length()>0) 
            {
                 
               
              buychargesHDRDOB = new BuychargesHDRDOB();
            
              buychargesHDRDOB.setChargeId(chargeId.trim());
              buychargesHDRDOB.setChargeDescId(chargeDescId.trim());
              buychargesHDRDOB.setChargeBasisId((request.getParameter("chargeBasisId")!=null)?request.getParameter("chargeBasisId"):"");
              buychargesHDRDOB.setDensityGrpCode((request.getParameter("densityGrpCode")!=null)?request.getParameter("densityGrpCode").trim():"");
              buychargesHDRDOB.setCurrencyId((request.getParameter("currencyId")!=null)?request.getParameter("currencyId").trim():"");
              buychargesHDRDOB.setTerminalId(loginbean.getTerminalId());
              
              str_error         = validateHeaderData(buychargesHDRDOB);
              
              //System.out.println("errormsg::"+str_error);
              if(str_error!=null && str_error.length()>0)
              {
                 request.setAttribute("errorMsg",str_error);
                 doDispatcher(request, response, "etrans/QMSSellCharges.jsp?Operation="+operation+"&subOperation="+subOperation);
              }else
              {
                  buychargesHDRDOB = doSearchDetails(loginbean,request,response);
                  request.setAttribute("BuyChargesHDRDtls",buychargesHDRDOB);
                  doDispatcher(request, response, "etrans/QMSSellCharges.jsp?Operation="+operation+"&subOperation="+subOperation);
              }
            }else
            {
                 request.setAttribute("errorMsg","ChargeId and ChargeDescriptionId should not be empty");
                 doDispatcher(request, response, "etrans/QMSSellCharges.jsp?Operation="+operation+"&subOperation="+subOperation);
            }
            
        }else if("BuyView".equals(subOperation))
        {
            chargeId         = request.getParameter("chargeId");
            //chargeBasisId    = request.getParameter("chargeBasisId");
            chargeDescId     = request.getParameter("chargeDescriptionId"); 
            buychargesHDRDOB = doSearchDetails(loginbean,request,response);
            if(buychargesHDRDOB!=null)
            {
              request.setAttribute("BuyChargesHDRDtls",buychargesHDRDOB);
              doDispatcher(request, response, "etrans/QMSBuyChargesMasterDetails.jsp?Operation="+operation+"&subOperation="+subOperation);
            }else
            {
              //Logger.info(FILE_NAME,"in else block");
              errorMsg.append("No BuyCharges defined for the data:\n");
              errorMsg.append("ChargeId:");
              errorMsg.append(chargeId);
              errorMsg.append("\tchargeDescriptionId:");
              errorMsg.append(chargeDescId);
              request.setAttribute("errorMsg",errorMsg.toString());
              errorMessageObject = new ErrorMessage(errorMsg.toString(),"QMSSellChargesController?Operation=Add&subOperation="+subOperation); 
              keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
              keyValueList.add(new KeyValue("Operation",operation)); 	
              errorMessageObject.setKeyValueList(keyValueList); 
              request.setAttribute("ErrorMessageObj",errorMessageObject);
              //session.setAttribute("ErrorMessageObj",errorMessageObject);
              request.setAttribute("close","close");
              //doDispatcher(request,response,"QMSESupplyErrorPage.jsp");             
              doForwardToErrorPage(loginbean,request,response);
            }
        }
        else
        {
               //Logger.info(FILE_NAME,"Operation::::in else");
               doDispatcher(request, response, "etrans/QMSSellCharges.jsp?Operation="+operation+"&subOperation=");          
        }
      }else if("Modify".equals(operation) || "View".equals(operation))
      {
        if("Modify".equals(subOperation))
        {
          doModifyProcess(loginbean,request,response);
        }
        else if("enter".equals(subOperation))
        {
              chargeId         = request.getParameter("chargeId");
              chargeDescId    = request.getParameter("chargeDescriptionId");
              /*rateBreak        = request.getParameter("rateBreak");
              rateType         = request.getParameter("rateType"); */
              terminalId      = request.getParameter("terminalId");
              request.setAttribute("terminalId",terminalId);
              buychargesHDRDOB = doRetriveDetails(loginbean,request,response);
              if(buychargesHDRDOB!=null)
              { 
                request.setAttribute("SellChargesHDRDtls",buychargesHDRDOB);              
                doDispatcher(request, response, "etrans/QMSSellChargesDetails.jsp?Operation="+operation+"&subOperation="+subOperation);
              }
        }else
        {
              doDispatcher(request, response, "etrans/QMSSellChargesEnterId.jsp?Operation="+operation+"&subOperation=");
        }
      }
    }catch(ObjectNotFoundException e)
    {
//      Logger.info(FILE_NAME,"Exception While doPost Retrieving the data :"+e);
      logger.info(FILE_NAME+"Exception While doPost Retrieving the data :"+e);
      errorMsg.append("No SellCharges defined for the data:\n");
      errorMsg.append("ChargeId:");
      errorMsg.append(chargeId);
      errorMsg.append("\tchargeDescriptionId:");
      errorMsg.append(chargeDescId);
      request.setAttribute("errorMsg",errorMsg.toString());
      doForwardToErrorPage(loginbean,request,response);
    }
    catch(Exception e)
    {
        e.printStackTrace();
//        logger.info(FILE_NAME,"Exception in doPost method while forwarding"+e);
        logger.info(FILE_NAME+"Exception in doPost method while forwarding"+e);
        doForwardToErrorPage(loginbean,request,response);
    }
  }
  private BuychargesHDRDOB doSearchDetails(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)
  {
    ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    BuychargesHDRDOB    buychargesHDRDOB  = null;
    String              chargeId          = "";
    // String              chargeBasisId     = "";
    String         chargeDescId            = null;
    StringBuffer        errorMsg          = new StringBuffer("");
    String         currencyId             = request.getParameter("currencyId");
    try
    {
      chargeId      = request.getParameter("chargeId");
      //chargeBasisId = request.getParameter("chargeBasisId");
      chargeDescId  = request.getParameter("chargeDescriptionId");
      
      if(currencyId==null || currencyId.equals(""))
        { currencyId = loginbean.getCurrencyId();}
      
      if(chargeId!=null && !"".equals(chargeId) && chargeDescId!=null && !"".equals(chargeDescId))
      {
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();
        buychargesHDRDOB =  remote.loadBuychargeDetailsForSellCharges(chargeId,chargeDescId,currencyId,loginbean);
        //this method throws FourSoftException when Invalid chargeID or invalid chargeBasisId entered
      }else
      {
        throw new Exception("Invalid data entered--->");
      }
    }catch(ObjectNotFoundException e)
    {
//      Logger.info(FILE_NAME,"Object Not Found :"+e);
      logger.info(FILE_NAME+"Object Not Found :"+e);
    }
    catch(Exception e)
    {
//      Logger.info(FILE_NAME,"Exception while retrieving the details---->"+e);
      logger.info(FILE_NAME+"Exception while retrieving the details---->"+e);
      doForwardToErrorPage(loginbean,request,response);
    }
    return buychargesHDRDOB;
  }
  private BuychargesHDRDOB doRetriveDetails(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)throws ObjectNotFoundException
  {
     ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    BuychargesHDRDOB    sellchargesHDRDOB  = null;
    String              chargeId          = "";
    /*String              chargeBasisId     = "";
    String              rateBreak         = "";
    String              rateType          = "";*/
    String              chargeDescId      = null;
    String              terminalId        = null;
    StringBuffer        errorMsg          = new StringBuffer("");
    String              operation         = request.getParameter("Operation");
    BuySellChargesEnterIdDOB buySellChargesEnterIdDOB = new BuySellChargesEnterIdDOB();
    try
    {
      chargeId      = request.getParameter("chargeId");
      /*chargeBasisId = request.getParameter("chargeBasisId");
      rateBreak     = request.getParameter("rateBreak");
      rateType      = request.getParameter("rateType");*/
      chargeDescId  = request.getParameter("chargeDescriptionId");
      terminalId    = request.getParameter("terminalId");
      if(terminalId==null || "".equals(terminalId))
      { terminalId = loginbean.getTerminalId();}
      if(operation!=null && !operation.equals("") && chargeId!=null && !"".equals(chargeId) && chargeDescId !=null && !"".equals(chargeDescId))
      {
        
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();
        buySellChargesEnterIdDOB.setChargeId(chargeId);
        buySellChargesEnterIdDOB.setChargeDescId(chargeDescId);
        buySellChargesEnterIdDOB.setTerminalId(terminalId);
        buySellChargesEnterIdDOB.setOperation(operation);
        try{
        sellchargesHDRDOB =  remote.loadSellchargeDetails(buySellChargesEnterIdDOB,loginbean);
        }catch(FoursoftException e)
        {
//            Logger.info(FILE_NAME,"Exception While doPost Retrieving the data :"+e);
            logger.info(FILE_NAME+"Exception While doPost Retrieving the data :"+e);
            if("Modify".equals(operation))
            { errorMsg.append("Invalid Access of data,You cannot access Modify the data due to following Reasons:\n");
              errorMsg.append("1.you are trying to access higher level data.\n");
              errorMsg.append("2.you are trying to access Terminal which is not in your hirarchy.");
            }
            else if("View".equals(operation))
            { 
              errorMsg.append("Invalid Access of data,As the terminal is not in your hirarchy:\n");
            }
            errorMsg.append("ChargeId:");
            errorMsg.append(chargeId);
            //errorMsg.append("\tChargeBasisId:");
            //errorMsg.append(chargeBasisId);
            errorMsg.append("\tChargeDescriptionId:");
            errorMsg.append(chargeDescId);
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
//      Logger.info(FILE_NAME,"Exception While Retrieving the data :"+e);
      logger.info(FILE_NAME+"Exception While Retrieving the data :"+e);
      throw new ObjectNotFoundException("NO data found");
    }
    catch(Exception e)
    {
//      Logger.info(FILE_NAME,"Exception while retrieving the details---->"+e);
      logger.info(FILE_NAME+"Exception while retrieving the details---->"+e);
      doForwardToErrorPage(loginbean,request,response);
    }
    return sellchargesHDRDOB;   
  }
  private void doAddProcess(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)
  {
    ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    ArrayList               dataList      = null;
    ArrayList               resultList    = null;
    ArrayList               insertedList  = null;
    ArrayList               invalidList   = null;
		ArrayList						keyValueList			= new ArrayList();
		StringBuffer				errorMessage		  =	new StringBuffer();
		ErrorMessage				errorMessageObject= null;
    BuychargesHDRDOB    sellchargesHDRDOB  = null;
    BuychargesDtlDOB    sellchargesDtlDOB  = null;
    String operation    = request.getParameter("Operation");
    String subOperation = request.getParameter("subOperation");  
    String chargeId     = "";
    String chargeBasis  = "";
    boolean  success    = true;
    HttpSession   session = request.getSession();
    String returnMsg = "";
    try
    {
      //Logger.info(FILE_NAME,"doAddProcess");
      sellchargesHDRDOB  = doGetDetails(loginbean,request,response);
      sellchargesHDRDOB.setOperation(operation);//Added by Kameswari for the WPBN issue-154398 on 24/02/09
      //Logger.info(FILE_NAME,"sellchargesHDRDOB::"+sellchargesHDRDOB);
      if(sellchargesHDRDOB!=null)
      {
			/*Logger.info(FILE_NAME,"buychargesHDRDOB.getChargeId(::::"+sellchargesHDRDOB.getChargeId());
			Logger.info(FILE_NAME,"buychargesHDRDOB.getChargeBasisId(::::"+sellchargesHDRDOB.getChargeBasisId());
			Logger.info(FILE_NAME,"buychargesHDRDOB.getCurrencyId(::::"+sellchargesHDRDOB.getCurrencyId());
			Logger.info(FILE_NAME,"buychargesHDRDOB.getRateBreak(::::"+sellchargesHDRDOB.getRateBreak());
			Logger.info(FILE_NAME,"buychargesHDRDOB.getRateType(::::"+sellchargesHDRDOB.getRateType());
			Logger.info(FILE_NAME,"buychargesHDRDOB.getWeightClass(::::"+sellchargesHDRDOB.getWeightClass());
			Logger.info(FILE_NAME,"buychargesHDRDOB.getOverallMargin(::::"+sellchargesHDRDOB.getOverallMargin());
      Logger.info(FILE_NAME,"buychargesHDRDOB.getMarginType(::::"+sellchargesHDRDOB.getMarginType());
      Logger.info(FILE_NAME,"buychargesHDRDOB.getMarginBasis(::::"+sellchargesHDRDOB.getMarginBasis());
			dataList	=	sellchargesHDRDOB.getBuyChargeDtlList();
			for(int k=0;k<dataList.size();k++)
			{
				sellchargesDtlDOB	=	(BuychargesDtlDOB)dataList.get(k);
				Logger.info(FILE_NAME,"buychargesDtlDOB.getChargeSlab()::::"+sellchargesDtlDOB.getChargeSlab());
				Logger.info(FILE_NAME,"buychargesDtlDOB.setChargeRate()::::"+sellchargesDtlDOB.getChargeRate());
        Logger.info(FILE_NAME,"buychargesDtlDOB.getMarginValue()::::"+sellchargesDtlDOB.getMarginValue());
				Logger.info(FILE_NAME,"buychargesDtlDOB.getLowerBound()::::"+sellchargesDtlDOB.getLowerBound());
				Logger.info(FILE_NAME,"buychargesDtlDOB.getUpperBound()::::"+sellchargesDtlDOB.getUpperBound());
				Logger.info(FILE_NAME,"buychargesDtlDOB.getChargeRate_indicator()::::"+sellchargesDtlDOB.getChargeRate_indicator());
			Logger.info("",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

			}*/
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();
        returnMsg  = remote.insertSellChargesDetails(sellchargesHDRDOB,loginbean);
        //Logger.info(FILE_NAME,"success:"+returnMsg);
        if(returnMsg!=null && returnMsg.equals("success"))
        {
                errorMessage.append("Sell Charges are successfully defined for:\n");
                errorMessage.append("ChargeId :");
                errorMessage.append(sellchargesHDRDOB.getChargeId());
                errorMessage.append("\t");
                errorMessage.append("ChargeDescriptionId :");
                errorMessage.append(sellchargesHDRDOB.getChargeDescId());
                errorMessageObject = new ErrorMessage(errorMessage.toString(),"QMSSellChargesController?Operation=Add&subOperation="); 
                keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
                //session.setAttribute("ErrorMessageObj",errorMessageObject);
                //response.sendRedirect("QMSESupplyErrorPage.jsp");                
                
        }else if(returnMsg!=null && returnMsg.startsWith("Data exist at higer levels"))
        {
                errorMessage.append("Sell Charges are not defined As Sellcharge is defined At higerLevel:\n");
                errorMessage.append("ChargeId :");
                errorMessage.append(sellchargesHDRDOB.getChargeId());
                errorMessage.append("\t");
                errorMessage.append("ChargeDescriptionId :");
                errorMessage.append(sellchargesHDRDOB.getChargeDescId());
                errorMessageObject = new ErrorMessage(errorMessage.toString(),"QMSSellChargesController?Operation=Add&subOperation="); 
                keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
                //session.setAttribute("ErrorMessageObj",errorMessageObject);
                //response.sendRedirect("QMSESupplyErrorPage.jsp");           
        }
        else
        {
                errorMessage.append("Records arenot inserted,Sellcharges already exist or Invalid data entered:\n");
                errorMessage.append("ChargeId :");
                errorMessage.append(sellchargesHDRDOB.getChargeId());
                errorMessage.append("\t");
                errorMessage.append("ChargeDescriptionId :");
                errorMessage.append(sellchargesHDRDOB.getChargeDescId());
                errorMessageObject = new ErrorMessage(errorMessage.toString(),"QMSSellChargesController?Operation=Add&subOperation="); 
                keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
                //session.setAttribute("ErrorMessageObj",errorMessageObject);
                //response.sendRedirect("QMSESupplyErrorPage.jsp");                  
        }
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
    boolean                 modified      = false;
		ArrayList						keyValueList			= new ArrayList();
		StringBuffer				errorMessage		  =	new StringBuffer();
		ErrorMessage				errorMessageObject= null;  
    BuychargesHDRDOB    sellchargesHDRDOB  = null;
    String operation    = request.getParameter("Operation");
    String subOperation = request.getParameter("subOperation");     
    HttpSession session = request.getSession();
    try
    {
      sellchargesHDRDOB  = doGetDetails(loginbean,request,response);
      sellchargesHDRDOB.setOperation(operation);//@@Added by Kameswari for the WPBN issue-154398  
      home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
      remote    = (ChargeMasterSession)home.create();        
      modified  = remote.modifySellChargesDetails(sellchargesHDRDOB,loginbean);
        if(modified)
        {
                errorMessage.append("Record successfully modified for the Ids:\n");
                errorMessage.append("ChargeId:");
                errorMessage.append(sellchargesHDRDOB.getChargeId());
                errorMessage.append("\tChargeDescriptionId:");
                errorMessage.append(sellchargesHDRDOB.getChargeDescId());
                errorMessageObject = new ErrorMessage(errorMessage.toString(),"QMSSellChargesController?Operation="+operation+"&subOperation="); 
                keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
                keyValueList.add(new KeyValue("Operation",operation)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
                //session.setAttribute("ErrorMessageObj",errorMessageObject);
                //response.sendRedirect("QMSESupplyErrorPage.jsp");
                doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
        }else
        {
          throw new Exception("Record not modified");
        }
    }catch(Exception e)
    {
            //Logger.info(FILE_NAME,"Exception while calling bean the data"+e);
            logger.info(FILE_NAME+"Exception while calling bean the data"+e);
            doForwardToErrorPage(loginbean,request,response);
    }
  }
  private BuychargesHDRDOB doGetDetails(ESupplyGlobalParameters loginbean,HttpServletRequest request, HttpServletResponse response)throws Exception
  {
      String chargeId					          =	request.getParameter("chargeId");
      String chargeDescId               = request.getParameter("chargeDescriptionId");
      String chargeBasisId				      =	request.getParameter("chargeBasisId");
      String chargeCurrencyId			      =	request.getParameter("currencyId");
      String rateBreak					        =	request.getParameter("rateBreak");
      String chargeRateType				      =	request.getParameter("rateType");
      String rateCalculation			      =	request.getParameter("weightClass");
      String overAllMargin              = request.getParameter("overallmargin");
      String marginType                 = request.getParameter("margintype");
      String marginBasis                = request.getParameter("marginbasis");
      String dummyBuychargesflag        = request.getParameter("dummyBuychargesflag");
      String   densityGrpCode           = request.getParameter("densityGrpCode");
      
      String[] chargeSlab					        =	request.getParameterValues("chargeSlab");
      String[] chargeRate					        =	request.getParameterValues("chargeRate");
      String[] chargeFlatRate				      =	request.getParameterValues("chargeFlatRate");
      String[] marginValue                = request.getParameterValues("marginRate");
      String[] marginSlab                 = request.getParameterValues("marginSlab");
      
    	BuychargesHDRDOB	buychargesHDRDOB  =	null;
      BuychargesDtlDOB	buychargesDtlDOB  =	null;
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
        
      String buyChargeId  = request.getParameter("buychargeid");
        
      //Logger.info(FILE_NAME,"doGetDetails");
      logger.info(FILE_NAME+"doGetDetails");
          if("HO_TERMINAL".equals(accessLevel))
            { accessLevel = "H";}
          else if("ADMN_TERMINAL".equals(accessLevel))
            { accessLevel = "A";}
          else if("OPER_TERMINAL".equals(accessLevel))
            { accessLevel = "O";}
            buychargesHDRDOB	=	new BuychargesHDRDOB();
            buychargesHDRDOB.setChargeId(chargeId);
            buychargesHDRDOB.setChargeDescId(chargeDescId);
            buychargesHDRDOB.setChargeBasisId(chargeBasisId);
            buychargesHDRDOB.setCurrencyId(chargeCurrencyId);
            buychargesHDRDOB.setRateBreak(rateBreak);
            buychargesHDRDOB.setRateType(chargeRateType);
            buychargesHDRDOB.setWeightClass(rateCalculation);
            buychargesHDRDOB.setTerminalId(terminalId);
            buychargesHDRDOB.setAccessLevel(accessLevel);
            buychargesHDRDOB.setUserId(userId);
            buychargesHDRDOB.setCreateTime(createDateTime);
            buychargesHDRDOB.setOverallMargin(overAllMargin);
            buychargesHDRDOB.setMarginType(marginType);
            buychargesHDRDOB.setMarginBasis((marginBasis!=null)?marginBasis:"");
            buychargesHDRDOB.setDummyBuychargesFlag(dummyBuychargesflag);
            buychargesHDRDOB.setDensityGrpCode((densityGrpCode!=null)?densityGrpCode:"");
            
            if(buyChargeId!=null && !"".equals(buyChargeId) && !"null".equals(buyChargeId) )
            { buychargesHDRDOB.setBuychargeId(Double.parseDouble(buyChargeId));}
            
          //	rateI	=	rateIndex;
            if(rateBreak!=null && (rateBreak.equals("Absolute") || rateBreak.equals("Percent")))
            {
              index		=	1;
            }else if(rateBreak!=null && (rateBreak.equals("Flat") || rateBreak.equals("Flat%")))
            {
              index		=	4;
              if("V".equals(marginBasis))
              { index = 14;}
            }else if(rateBreak!=null && (rateBreak.equals("Slab") || rateBreak.equals("Slab%")))
            {
              index		=	14;
              if(chargeRateType.equals("Both"))
                {	flatIndex	=	11;}
            }
            rateILength		=	rateI+index;
            flatRateILength	=	flatRateI+flatIndex;
            dtlList				=	new ArrayList();
            while(rateI<rateILength)
            {
              buychargesDtlDOB	=	new BuychargesDtlDOB();
              if((rateBreak.equals("Flat") || rateBreak.equals("Flat%")) && "V".equals(marginBasis) || 
              (chargeSlab[rateI]!=null && !chargeSlab[rateI].equals("") && 
              (chargeSlab[rateI].equals("BASE") || chargeSlab[rateI].equals("MIN") || 
              chargeSlab[rateI].equals("MAX") || chargeSlab[rateI].equals("Flat") || 
              chargeSlab[rateI].equals("AbsRPersent"))))
              {
              //Logger.info(FILE_NAME,"rateI"+rateI);
                if((rateBreak.equals("Flat") || rateBreak.equals("Flat%")) && "V".equals(marginBasis))
                {
                  if(rateI<4)
                  {
                      if(chargeRate[rateI]!=null && !"".equals(chargeRate[rateI]))
                      {
                        buychargesDtlDOB.setChargeRate(Double.parseDouble((chargeRate[rateI]!=null && !"".equals(chargeRate[rateI]))?chargeRate[rateI]:"0.0"));
                        buychargesDtlDOB.setChargeSlab(chargeSlab[rateI]);
                        buychargesDtlDOB.setMarginValue("");
                        dtlList.add(buychargesDtlDOB);
                      }
                  }else
                  {
                    if(marginSlab!=null)
                    {
                      //Logger.info(FILE_NAME,"marginSlab[rateI-4]"+marginSlab[rateI-4]);
                      if(!"".equals(marginSlab[rateI-4].trim()))
                      {
                          buychargesDtlDOB.setChargeRate(0.0);
                          buychargesDtlDOB.setChargeSlab(marginSlab[rateI-4]);
                          buychargesDtlDOB.setMarginValue(marginValue[rateI-4]);
                          if(Double.parseDouble(marginSlab[rateI-4])<0)
                            { buychargesDtlDOB.setLowerBound(0);}
                          else
                            { buychargesDtlDOB.setLowerBound(Double.parseDouble(marginSlab[rateI-4]));}
                          
                          if(rateI-3<rateILength && !"".equals(marginSlab[rateI-3]))
                            { buychargesDtlDOB.setUpperBound(Double.parseDouble(marginSlab[rateI-3]));}
                          else
                            { buychargesDtlDOB.setUpperBound(999999);}
                          dtlList.add(buychargesDtlDOB);
                      }else
                      {
                        rateI=rateILength;
                      }
                    }
                  }
                }else
                {
                  if(("Slab".equals(rateBreak) || "Slab%".equals(rateBreak)) && 
                    "Y".equals(overAllMargin) && "P".equals(marginType))
                  {
                       if("MAX".equalsIgnoreCase(chargeSlab[rateI])||"BASE".equalsIgnoreCase(chargeSlab[rateI])
                       ||"MIN".equalsIgnoreCase(chargeSlab[rateI]))
                       {
                         if(chargeRate[rateI]!=null&&chargeRate[rateI].trim().length()>0&&!("0.0".equalsIgnoreCase(chargeRate[rateI])))
                         {
                            buychargesDtlDOB.setChargeRate(Double.parseDouble((chargeRate[rateI]!=null && !"".equals(chargeRate[rateI]))?chargeRate[rateI]:"0.0"));
                            buychargesDtlDOB.setChargeSlab(chargeSlab[rateI]);                  
                            buychargesDtlDOB.setMarginValue(marginValue[0]);
                            dtlList.add(buychargesDtlDOB);
                         }
                       }
                       else
                       {
                        buychargesDtlDOB.setChargeRate(Double.parseDouble((chargeRate[rateI]!=null && !"".equals(chargeRate[rateI]))?chargeRate[rateI]:"0.0"));
                        buychargesDtlDOB.setChargeSlab(chargeSlab[rateI]);                  
                        buychargesDtlDOB.setMarginValue(marginValue[0]);
                        dtlList.add(buychargesDtlDOB);
                       }
                  }else
                  {
                    if((!"".equals(chargeRate[rateI].trim()) && (Double.parseDouble(chargeRate[rateI]))>0.0) ||
                    !"".equals(marginValue[rateI].trim()) && (Double.parseDouble(marginValue[rateI]))>0.0)
                    {
                          buychargesDtlDOB.setChargeRate(Double.parseDouble((chargeRate[rateI]!=null && !"".equals(chargeRate[rateI]))?chargeRate[rateI]:"0.0"));
                          buychargesDtlDOB.setChargeSlab(chargeSlab[rateI]);                    
                          buychargesDtlDOB.setMarginValue(marginValue[rateI]);
                          dtlList.add(buychargesDtlDOB);
                    }
                  }
                }
              }else if(chargeSlab[rateI]!=null && !chargeSlab[rateI].equals(""))
              {
                tempChargeSlab	=	chargeSlab[rateI];
                tempChargeSlabD = Double.parseDouble(tempChargeSlab);
                if(chargeRateType.equals("Both"))
                {
                      if(chargeRate[rateI].trim().equals(""))
                      {
                        if(chargeFlatRate!=null && chargeFlatRate[flatRateI]!=null)
                        {
                          tempChargeRate = Double.parseDouble(chargeFlatRate[flatRateI]);
                          //buychargesDtlDOB.setChargeRate_indicator("F");//Higlighted text is for Slab
                          buychargesDtlDOB.setChargeRate_indicator("S");
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
                    if("Y".equals(overAllMargin))
                    {
                        if("P".equals(marginType))
                          buychargesDtlDOB.setMarginValue(marginValue[0]);
                        else if("A".equals(marginType))
                          buychargesDtlDOB.setMarginValue(marginValue[3]);
                    }
                    else if("N".equals(overAllMargin))
                        buychargesDtlDOB.setMarginValue(marginValue[rateI]);
                    
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
                if(chargeRateType.equals("Both"))
                  {	flatRateI=flatRateILength;}
              }
              rateI	=	rateI+1;
            }
            buychargesHDRDOB.setBuyChargeDtlList(dtlList);
        
            //dataList.add(buychargesHDRDOB);
   
      }catch(Exception e)
      {
            //Logger.info(FILE_NAME,"Exception while reading the data :"+e);
            logger.info(FILE_NAME+"Exception while reading the data :"+e);
            e.printStackTrace();
            throw new Exception("Exception while reading the data");
            //doForwardToErrorPage(loginbean,request,response);
      }          
      return buychargesHDRDOB;
  }
  
  private String validateHeaderData(BuychargesHDRDOB buychargesHDRDOB)throws Exception
  {
    ChargeMasterSessionHome home          = null;
    ChargeMasterSession     remote        = null;
    String                  errorMsg      = null;
    try 
    {
      home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
      remote    = (ChargeMasterSession)home.create();
      
      errorMsg  = remote.validateHeaderData(buychargesHDRDOB);
      
    }catch(Exception e)
    {
      throw new Exception();
    }
    return errorMsg;
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
    HttpSession         session           = request.getSession();
    nextNavigation  = "QMSSellChargesController?Operation="+operation+"&subOperation=";
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
						doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
            //session.setAttribute("ErrorMessageObj",errorMessageObject);
            //response.sendRedirect("QMSESupplyErrorPage.jsp");
    }catch(Exception e)
    {
      //Logger.info(FILE_NAME,"Exception while forwarding the controller"+e);
      logger.info(FILE_NAME+"Exception while forwarding the controller"+e);
    }
    
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
      //Logger.info(FILE_NAME," forwardFile "+forwardFile);
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
//			Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
			logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}
}
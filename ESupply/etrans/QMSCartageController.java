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
 
 /**
 * File       : QMSCartageController.java
 * Sub-Module : Cartage Charges
 * Module     : QMS
 * @author    : Yuvraj Waghray
 * @date      : 27/08/2005
 * Modified by: Date     Reason
 */
 
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
//import com.foursoft.esupply.common.util.Logger;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.qms.operations.charges.ejb.sls.ChargeMasterSession;
import com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome;
import com.qms.operations.charges.java.QMSCartageMasterDOB;
import com.qms.operations.charges.java.QMSCartageBuyDtlDOB;
import com.qms.operations.charges.java.QMSCartageSellDtlDOB;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;

public class QMSCartageController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  public static String FILE_NAME = "QMSCartageController.java";
  private static Logger logger = null;
  
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSCartageController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    HttpSession session = request.getSession();
		ESupplyGlobalParameters loginbean = (ESupplyGlobalParameters)session.getAttribute("loginbean");
    
    ErrorMessage	errorMessageObject		=   null;
    ArrayList			keyValueList			    =   new ArrayList();
    String        errorMessage          =   "";
    String        errorCode             =   "";
    String        nextNavigation        =   "";
    ArrayList     invalidList           =   null; 
    ArrayList     densityGroupList     =   null;//@@Added by Kameswari for the WPBN issue-54554
    ArrayList			densityList			     =   new ArrayList();//@@Added by Kameswari for the WPBN issue-54554
    QMSCartageBuyDtlDOB buyCartageDOB   =   null;
		
		String        operation          = request.getParameter("Operation"); 
		String        subOperation       = request.getParameter("subOperation");
		String        shipmentMode       = request.getParameter("shipmentMode");
    
    StringBuffer  sb                    =   new StringBuffer("");
    String        primaryUnit       = null;//@@ Added by subrahmanyam for the pbn id: 186783 on 22/oct/09
    
    try
    {
    
      if("buyAdd".equals(operation) && subOperation==null)
      {
       // nextNavigation = "/etrans/QMSCartageBuyCharges.jsp";
       nextNavigation = "/etrans/QMSCartageBuyCharges.jsp";
      }
      else if("buyAdd".equals(operation) && "Details".equals(subOperation))
      {
        session.removeAttribute("cartageMaster");
        QMSCartageMasterDOB cartageMaster = doGetMasterInfo(request,response,loginbean);
        
        StringBuffer errors =  doValidateCartageHdr(request,cartageMaster);
         
        if(errors.length() > 0)
        {
          request.setAttribute("cartageMaster",cartageMaster);
          request.setAttribute("Errors",errors.toString());
          nextNavigation = "/etrans/QMSCartageBuyCharges.jsp";
        }
        else
        {
           //@@Added by Kameswari for the WPBN issue-54554
           densityGroupList   =   doGetDensityGroupList(request,response);  
//@@ Added by subrahmanyam for the pbn id: 186783 on 22/oct/09                 
           primaryUnit  =(String) densityGroupList.get((densityGroupList.size())-1);
           cartageMaster.setPrimaryUnit(primaryUnit);
           densityGroupList.remove(densityGroupList.size()-1);
//@@ Ended by subrahmanyam for the pbn id: 186783  on 22/oct/09
           	int dGrpListSize	=	densityGroupList.size();
           if("KG".equalsIgnoreCase(cartageMaster.getPrimaryUnit())||"CBM".equalsIgnoreCase(cartageMaster.getPrimaryUnit()))
           {
              for(int i=0;i<dGrpListSize;i=i+2)
              {
                densityList.add(densityGroupList.get(i));
              }
           }
            else if("LB".equalsIgnoreCase(cartageMaster.getPrimaryUnit())||"CFT".equalsIgnoreCase(cartageMaster.getPrimaryUnit()))
           {
              for(int i=1;i<dGrpListSize;i=i+2)
              {
                densityList.add(densityGroupList.get(i));
              }
           }
           
          //@@WPBN issue-54554
          session.setAttribute("cartageMaster",cartageMaster);
          request.setAttribute("densityList",densityList); 
          
          
      
             if("Flat".equals(cartageMaster.getWeightBreak()))
          {
            
            
            nextNavigation = "/etrans/QMSCartageBuyChargesFlat.jsp";
          }
          else if ("List".equalsIgnoreCase(cartageMaster.getWeightBreak()))
          {
            nextNavigation  = "/etrans/QMSCartageBuyChargesList.jsp";
          }
          else if("Slab".equals(cartageMaster.getWeightBreak()) && !"Both".equals(cartageMaster.getRateType()))
          {
             nextNavigation = "/etrans/QMSCartageBuyChargesSlab.jsp";
          }
          else
          {
             nextNavigation = "/etrans/QMSCartageBuyChargesBoth.jsp";
          }
          
        }
      }
      else if("buyAdd".equals(operation) && "flatAdd".equals(subOperation))
      {
          invalidList  = doFlatBuyChargeAddProcess(request,response,loginbean);
          
          if(invalidList!=null && invalidList.size()>0)
          {
            sb.append("The Following Records Were Not Inserted:\n");
            int invListSize	=	invalidList.size();
            for(int i=0;i<invListSize;i++)
            {
              buyCartageDOB = (QMSCartageBuyDtlDOB)invalidList.get(i);
              if("Min".equalsIgnoreCase(buyCartageDOB.getWeightBreakSlab()))
                  sb.append("Zone :"+buyCartageDOB.getZoneCode()).append(", Charge Type :").append(buyCartageDOB.getChargeType()).append("\n");
            }
            sb.append("This Could be Due to the Following Reason(s):\n");
            sb.append("For the Same Combination, Buy Charge Might Already have Been Defined at A Higher Terminal");
            errorMessage = sb.toString();
            errorCode    = "ERR";// added by VLAKSHMI for  issue -143512 on 12/11/2008 form RSI to ERR
          }
          else
          {
            errorMessage  = "Records Successfully Inserted";
            errorCode    = "RSI"; // added by VLAKSHMI for  issue -143512  on 12/11/2008  for issue 143512
          }
         // errorCode    = "RSI";//commented by VLAKSHMI for issue 143512
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
      }
      else if("buyAdd".equalsIgnoreCase(operation) && "listAdd".equalsIgnoreCase(subOperation))
      {
            invalidList =  doListChargesAddProcess(request,response,loginbean);
            if(invalidList!=null && invalidList.size()>0)
            {
              sb.append("The Following Records Were Not Inserted:\n");
              int invListSize	=	invalidList.size();
              for(int i=0;i<invListSize;i++)
              {
                buyCartageDOB = (QMSCartageBuyDtlDOB)invalidList.get(i);
                if(buyCartageDOB.getLineNumber()==0)
                    sb.append("Zone :"+buyCartageDOB.getZoneCode()).append(", Charge Type :").append(buyCartageDOB.getChargeType()).append("\n");
              }
              sb.append("This Could be Due to the Following Reason(s):\n");
              sb.append("For the Same Combination, Buy Charge Might Already have Been Defined at A Higher Terminal");
              errorMessage = sb.toString();
              errorCode    = "MSG";
            }
            else
            {
              errorMessage  = "Records Successfully Inserted";
              errorCode    = "RSI";
            }
          
            errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
            keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject);
            nextNavigation = "ESupplyErrorPage.jsp";
      }
      else if("buyAdd".equals(operation) && "slabAdd".equals(subOperation))
      {
          invalidList   =   doSlabBuyChargesAddProcess(request,response,loginbean);
          
          if(invalidList!=null && invalidList.size()>0)
          {
            sb.append("The Following Records Were Not Inserted:\n");
            int invalListSize	=	invalidList.size();
            for(int i=0;i<invalListSize;i++)
            {
              buyCartageDOB = (QMSCartageBuyDtlDOB)invalidList.get(i);
              if("Min".equalsIgnoreCase(buyCartageDOB.getWeightBreakSlab()))
                  sb.append("Zone :"+buyCartageDOB.getZoneCode()).append(", Charge Type :").append(buyCartageDOB.getChargeType()).append("\n");
            }
            sb.append("This Could be Due to the Following Reason(s):\n");
            sb.append("For the Same Combination, Buy Charge Might Already have Been Defined at A Higher Terminal");
            errorMessage = sb.toString();
            errorCode    = "MSG";
          }
          else
          {
            errorMessage  = "Records Successfully Inserted";
            errorCode    = "RSI";
          }
          
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
      }//@@Start of Sell Charges
      else if("sellAdd".equals(operation) && subOperation==null)
      {
        nextNavigation = "/etrans/QMSCartageSellChargesFlat.jsp";
      }
      else if("sellAdd".equals(operation) && "Details".equals(subOperation))
      {
        QMSCartageMasterDOB cartageMaster = doGetSellChargesMaster(request,response,loginbean);
        
        StringBuffer errors =  doValidateCartageHdr(request,cartageMaster);
        request.setAttribute("cartageMaster",cartageMaster);
        session.setAttribute("cartageMaster",cartageMaster);
        if(errors.length() > 0)
        {
          request.setAttribute("Errors",errors.toString());
        }
        else
        {
          ArrayList list = doSearchProcess(cartageMaster,request.getParameter("weightBreak"),operation,request);
          if("List".equalsIgnoreCase(cartageMaster.getWeightBreak()))
          {
              session.setAttribute("cartageMaster",cartageMaster);
              session.setAttribute("buyChargesList",list);
          }
          else
            request.setAttribute("buyChargesList",list);
        }
          
        if("Flat".equals(cartageMaster.getWeightBreak()))
          nextNavigation = "/etrans/QMSCartageSellChargesFlat.jsp";
        else if("Slab".equalsIgnoreCase(cartageMaster.getWeightBreak()))
          nextNavigation = "/etrans/QMSCartageSellChargesSlab.jsp";
        else
          nextNavigation = "/etrans/QMSCartageSellChargesList.jsp";
      }
      else if("sellAdd".equals(operation))
      {   
          if("setFlatSellRates".equals(subOperation))
              doFlatSellChargeAddProcess(request,response,loginbean,operation);
          else if("setSlabSellRates".equals(subOperation))
              doSlabSellChargesAddProcess(request,response,loginbean);
          else if("setListSellRates".equalsIgnoreCase(subOperation))
              doListSellChargesAddProcess(request,response);
          session.removeAttribute("cartageMaster");
          session.removeAttribute("buyChargesList");
          errorMessage  = "Record Successfully Inserted";
          errorCode    = "RSI";
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
      }
	  else if("sellModify".equalsIgnoreCase(operation))
      {
        if(subOperation == null)
        {
          //getUpdatedCartageBuyCharges(request,response);
          nextNavigation = "/etrans/QMSCartageSellChargesFlat.jsp";         
        }
        else if("Details".equals(subOperation))
        {
          QMSCartageMasterDOB cartageMaster = doGetSellChargesMaster(request,response,loginbean);
        
          StringBuffer errors =  doValidateCartageHdr(request,cartageMaster);
          request.setAttribute("cartageMaster",cartageMaster);
          session.setAttribute("cartageMaster",cartageMaster);
          if(errors.length() > 0)
          {
            request.setAttribute("Errors",errors.toString());
          }
          else
          {
            ArrayList list = doSearchProcess(cartageMaster,request.getParameter("weightBreak"),operation,request);
            if("List".equalsIgnoreCase(cartageMaster.getWeightBreak()))
                session.setAttribute("buyChargesList",list);
            else
              request.setAttribute("buyChargesList",list);
          }
            
         if("Flat".equals(cartageMaster.getWeightBreak()))
          nextNavigation = "/etrans/QMSCartageSellChargesFlat.jsp";
        else if("Slab".equalsIgnoreCase(cartageMaster.getWeightBreak()))
          nextNavigation = "/etrans/QMSCartageSellChargesSlab.jsp";
        else
          nextNavigation = "/etrans/QMSCartageSellChargesList.jsp";
        }
        else
        { 
          if("setFlatSellRates".equals(subOperation))
              doFlatSellChargeAddProcess(request,response,loginbean,operation);
          else if("setSlabSellRates".equals(subOperation))
              doSlabSellChargesAddProcess(request,response,loginbean);
          else if("setListSellRates".equalsIgnoreCase(subOperation))
              doListSellChargesAddProcess(request,response);
          session.removeAttribute("cartageMaster");
          session.removeAttribute("buyChargesList");
          errorMessage  = "Record Successfully Modified";
          errorCode    = "RSI";
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
        }
          
      }else if("sellView".equalsIgnoreCase(operation))
      {
        if(subOperation == null)
        {
          //getUpdatedCartageBuyCharges(request,response);
          nextNavigation = "/etrans/QMSCartageSellChargesFlat.jsp";         
        }
        else if("Details".equals(subOperation))
        {
          QMSCartageMasterDOB cartageMaster = doGetSellChargesMaster(request,response,loginbean);
        
          StringBuffer errors =  doValidateCartageHdr(request,cartageMaster);
          request.setAttribute("cartageMaster",cartageMaster);
          if(errors.length() > 0)
          {
            request.setAttribute("Errors",errors.toString());
          }
          else
          {
            ArrayList list = doSearchProcess(cartageMaster,request.getParameter("weightBreak"),operation,request);
            if("List".equalsIgnoreCase(cartageMaster.getWeightBreak()))
                session.setAttribute("buyChargesList",list);
            else
              request.setAttribute("buyChargesList",list);
          }
            
          if("Flat".equals(cartageMaster.getWeightBreak()))
            nextNavigation = "/etrans/QMSCartageSellChargesFlat.jsp";
          else if("Slab".equalsIgnoreCase(cartageMaster.getWeightBreak()))
            nextNavigation = "/etrans/QMSCartageSellChargesSlab.jsp";
          else
            nextNavigation = "/etrans/QMSCartageSellChargesList.jsp";
        }
        else if("Continue".equalsIgnoreCase(subOperation))
        {
          nextNavigation = "/etrans/QMSCartageSellChargesFlat.jsp?Operation=sellView";
        }
      }
		  else if("cartageAccept".equalsIgnoreCase(operation))
      {
        if(subOperation == null)
        {          
          nextNavigation = "etrans/QMSCartageAcceptanceEnter.jsp";          
        }
        else if("Details".equalsIgnoreCase(subOperation) )
        {
          getUpdatedCartageBuyCharges(request,response);
          nextNavigation = "etrans/QMSCartageAccept.jsp";          
        }
        else if("acceptModify".equalsIgnoreCase(subOperation))
        {
            doGetSellChargesDtls(request,response,loginbean);
            
          if("Flat".equals(request.getParameter("weightBreak")))
            nextNavigation = "/etrans/QMSCartageSellAccepetFlat.jsp?Operation=Modify&subOperation=acceptModify";
          else if("Slab".equalsIgnoreCase(request.getParameter("weightBreak")))
            nextNavigation = "/etrans/QMSCartageSellAccepetSlab.jsp?Operation=Modify&subOperation=acceptModify";
          else
            nextNavigation = "/etrans/QMSCartageSellAcceptList.jsp?Operation=Modify&subOperation=acceptModify";
        }
        else if("updateSellRate".equalsIgnoreCase(subOperation))
        {
          //System.out.println("request.getParameter(weightBreak)"+request.getParameter("weightBreak"));
          if("Flat".equals(request.getParameter("weightBreak")))
             doProcessModifiedRatesFlat(request,response);
          else if("Slab".equalsIgnoreCase(request.getParameter("weightBreak")))
             doProcessModifiedRatesSlab(request,response);
          else
             doProcessModifiedRatesList(request,response);
        }
        else if("submit".equalsIgnoreCase(subOperation))
        {
          doInsertSellDtls(request,response);
          errorMessage  = "Sell Rates Successfully Modified";
          errorCode    = "RSI";
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
        }
      }
	   else if("buyModify".equals(operation) && "flatAdd".equals(subOperation))
      {
          QMSCartageMasterDOB cartageMaster = doGetSellChargesMaster(request,response,loginbean);
		      invalidList =  doFlatBuyChargeModifyProcess(request,response,loginbean,cartageMaster);
          
          if(invalidList!=null && invalidList.size()>0)
          {
            sb.append("The Following Records Were Not Modified:\n");
            int invalListSize	=	invalidList.size();
            for(int i=0;i<invalListSize;i++)
            {
              buyCartageDOB = (QMSCartageBuyDtlDOB)invalidList.get(i);
              if("Min".equalsIgnoreCase(buyCartageDOB.getWeightBreakSlab()))
                  sb.append("Zone :"+buyCartageDOB.getZoneCode()).append(", Charge Type :").append(buyCartageDOB.getChargeType()).append("\n");
            }
            sb.append("This Could be Due to the Following Reason(s):\n");
            sb.append("For the Same Combination, Buy Charge Might Already have Been Defined at Higher Terminals");
            errorMessage = sb.toString();
            errorCode    = "ERR";// added by VLAKSHMI for  issue -143512 on 12/11/2008  for issue 143512
          }
          else
          {
            errorMessage  = "Records Successfully Modified";
            errorCode    = "RSM";// added by VLAKSHMI for  issue -143512 on 12/11/2008  for issue 143512
          }
          session.removeAttribute("cartageMaster");
          //errorCode    = "RSM";// commented by VLAKSHMI for  issue -143512 on 12/11/2008  for issue 143512
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
      }
	   else if("buyModify".equals(operation) && "modifySlab".equals(subOperation))
      {
          //QMSCartageMasterDOB cartageMaster = doGetSellChargesMaster(request,response,loginbean);
          QMSCartageMasterDOB cartageMaster = (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
          doSlabBuyChargesModifyProcess(request,response,loginbean,cartageMaster);
          session.removeAttribute("cartageMaster");
          errorMessage  = "Record Successfully Modified";
          errorCode    = "RSM";
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
      }
      else if("buyModify".equals(operation) && "modifyList".equals(subOperation))
      {
          QMSCartageMasterDOB cartageMaster = (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
          //doSlabBuyChargesModifyProcess(request,response,loginbean,cartageMaster);
          doListBuyChargesModify(request,response,loginbean,cartageMaster);
          session.removeAttribute("cartageMaster");
          errorMessage  = "Record Successfully Modified";
          errorCode    = "RSM";
          errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
          keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
          keyValueList.add(new KeyValue("Operation",operation)); 	
          errorMessageObject.setKeyValueList(keyValueList);
          request.setAttribute("ErrorMessage",errorMessageObject);
          nextNavigation = "ESupplyErrorPage.jsp";
      }
	  else if("buyModify".equals(operation) && subOperation==null)
      {
			 nextNavigation = "/etrans/QMSCartageBuyChargesModify.jsp";
	  }
	  else if("buyView".equals(operation) && (subOperation==null ||"".equals(subOperation)) )
      {
        session.removeAttribute("cartageMaster");
        nextNavigation = "/etrans/QMSCartageBuyChargesModify.jsp";
      }
	  
	  else if("buyModify".equals(operation) && "Details".equals(subOperation))
      {
        
        QMSCartageMasterDOB cartageMaster = doGetSellChargesMaster(request,response,loginbean);
        
        StringBuffer errors =  doValidateCartageHdr(request,cartageMaster);
        session.setAttribute("cartageMaster",cartageMaster);
        if(errors.length() > 0)
        {
          request.setAttribute("Errors",errors.toString());
        }
        else
        {

          ArrayList list = doSearchProcess(cartageMaster,request.getParameter("weightBreak"),operation,request);

          if("List".equalsIgnoreCase(cartageMaster.getWeightBreak()))
              session.setAttribute("buyChargesList",list);
          else
            request.setAttribute("buyChargesList",list);
        }
          
        if("Flat".equals(cartageMaster.getWeightBreak()))
         nextNavigation = "/etrans/QMSCartageBuyChargesModify.jsp";
        else if("Slab".equalsIgnoreCase(cartageMaster.getWeightBreak()))
          nextNavigation = "/etrans/QMSCartageBuyChargesModifySlab.jsp";
        else
          nextNavigation = "/etrans/QMSCartageBuyChargesModifyList.jsp";
        
      }
	 
	  else if("buyView".equals(operation) && "Details".equals(subOperation))
      {
        QMSCartageMasterDOB cartageMaster = doGetSellChargesMaster(request,response,loginbean);
        
        StringBuffer errors =  doValidateCartageHdr(request,cartageMaster);
        session.setAttribute("cartageMaster",cartageMaster);
        if(errors.length() > 0)
        {
          request.setAttribute("Errors",errors.toString());
        }
        else
        {
          ArrayList list = doSearchProcess(cartageMaster,request.getParameter("weightBreak"),operation,request);
          if("List".equalsIgnoreCase(cartageMaster.getWeightBreak()))
              session.setAttribute("buyChargesList",list);
          else
            request.setAttribute("buyChargesList",list);
        }
          
        if("Flat".equals(cartageMaster.getWeightBreak()))
         nextNavigation = "/etrans/QMSCartageBuyChargesModify.jsp";
        else if("Slab".equalsIgnoreCase(cartageMaster.getWeightBreak()))
          nextNavigation = "/etrans/QMSCartageBuyChargesModifySlab.jsp";
        else
          nextNavigation = "/etrans/QMSCartageBuyChargesModifyList.jsp";
        
      }
    }
    catch(FoursoftException fs)
    {
      session.removeAttribute("cartageMaster");
//      Logger.error(FILE_NAME,"Error in Controller"+fs);
      logger.error(FILE_NAME+"Error in Controller"+fs);
      fs.printStackTrace();
      errorMessage = fs.getMessage();
      errorCode    = "ERR";
      errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
      keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
      keyValueList.add(new KeyValue("Operation",operation)); 	
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 
      nextNavigation = "ESupplyErrorPage.jsp";
    }
    catch(Exception e)
    {
      session.removeAttribute("cartageMaster");
     
//      Logger.error(FILE_NAME,"Error in Controller"+e);
      logger.error(FILE_NAME+"Error in Controller"+e);
      e.printStackTrace();
      errorMessage = "An Unexpected Error has Occurred.Please Try Again";
      errorCode    = "ERR";
      errorMessageObject = new ErrorMessage(errorMessage,"QMSCartageController");
      keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
      keyValueList.add(new KeyValue("Operation",operation)); 	
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 
      nextNavigation = "ESupplyErrorPage.jsp";
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
//        Logger.error(FILE_NAME,"Error while forwarding page "+e.toString());
        logger.error(FILE_NAME+"Error while forwarding page "+e.toString());
      }
    }
  }
  public void doInsertSellDtls(HttpServletRequest request,HttpServletResponse response)
  {
    String[] checkBoxValue  = request.getParameterValues("checkBoxValue");
    ArrayList newList       = new ArrayList();
    ArrayList oldList       = null;
    QMSCartageSellDtlDOB  sellDOB             =   null;
    QMSCartageMasterDOB   dob                 =   null;
    ChargeMasterSessionHome home              =   null;
    ChargeMasterSession     remote            =   null;
    ESupplyGlobalParameters loginbean         =   (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
    try
    {
      oldList     = (ArrayList)request.getSession().getAttribute("CartageBuyCharges");
      request.getSession().removeAttribute("CartageBuyCharges");
      //System.out.println("oldList  "+oldList);
      //System.out.println("checkBoxValue  "+checkBoxValue);
      //System.out.println("checkBoxValue.length  "+checkBoxValue.length);
      int chkBoxValLen	=	checkBoxValue.length;
      for(int i=0;i<chkBoxValLen;i++)
      {  
        dob    =    (QMSCartageMasterDOB)oldList.get(i);
        dob.setCreatedBy(loginbean.getUserId());
        dob.setCreatedTimestamp(new Timestamp((new java.util.Date()).getTime()));
        //System.out.println("checkBoxValue[i]  "+checkBoxValue[i]);
        if("Yes".equalsIgnoreCase(checkBoxValue[i]))
        {
          newList.add(dob);
        }
      }
      //System.out.println("newList  "+newList);
      //System.out.println("newList.size()  "+newList.size());
      if(newList.size()>0)
      {
        try
        {
          home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote  =   (ChargeMasterSession)home.create();
          remote.insertNewCartageSellDtl(newList);
        }
        catch(Exception e)
        {
          e.printStackTrace();
          throw new FoursoftException("Error while updating the details");
        } 
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
//      Logger.error(FILE_NAME,"Error while inserting details"+e.toString());
      logger.error(FILE_NAME+"Error while inserting details"+e.toString());
    }
  }
  
  
  //This is for updating when submitting the cartage acceptence Modify Screen
  private void doProcessModifiedRatesFlat(HttpServletRequest request,HttpServletResponse response)
  {
    QMSCartageSellDtlDOB  sellDOB             =   null;
    QMSCartageSellDtlDOB  dob                 =   null;
    int                   index               =   0;
    String                baseMargin          =   null;//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    String                minMargin           =   null;
    String                maxMargin           =   null;
    String                flatMargin          =   null;
    String                margin              =   null;
    ArrayList             list                =   new ArrayList();
    int                   counter             =   0;
    double                baseMarginDouble     =   0.0;//@@Added by subrahmanyam for the enhancement 170759 on 02/06/09
    double                minMarginDouble     =   0.0;
    double                flatMarginDouble    =   0.0;
    double                maxMarginDouble     =   0.0;
    double                marginDouble        =   0.0;
    String                chargeSlab          =   null;
    double                marginPercentage    =   0.0;
    double                chargeRate          =   0.0;
    ChargeMasterSessionHome home              =   null;
    ChargeMasterSession     remote            =   null;
    String                  marginType        =   null;
    
    try
    {
      index               =   new Integer(request.getParameter("index")).intValue();
      sellDOB             =   (QMSCartageSellDtlDOB)request.getSession().getAttribute("sellDOB");
      request.getSession().removeAttribute("sellDOB");
      
      if("P".equalsIgnoreCase(sellDOB.getMarginType()))
         marginType = "Percentage";
      else
         marginType = "Absolute";
      //System.out.println("sellDOB  "+sellDOB);  
      if(!request.getParameter("marginType").equalsIgnoreCase(sellDOB.getMarginType()) || !request.getParameter("overAllMargin").equalsIgnoreCase(sellDOB.getOverallMargin()))
      {              
            baseMargin  =  request.getParameter("baseMargin");//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
            minMargin  =  request.getParameter("minMargin");
            maxMargin  =  request.getParameter("maxMargin");
            flatMargin  =  request.getParameter("flatMargin");      
            //System.out.println("request.getParameter(minMargin)"+request.getParameter("minMargin"));   
            if(("No".equals(request.getParameter("overAllMargin"))) || ("Yes".equals(request.getParameter("overAllMargin")) && "Absolute".equals(request.getParameter("marginType"))))
            {
               //@@ Commented by subrahmanyam for the Enhancement 170759 on 02/06/09            
                /*if("0.0".equals(new Double(sellDOB.getMaxRate()).toString()) && "".equals(maxMargin))
                  counter   = 2;
                else
                  counter   = 3;
                  */
//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09     
              if(sellDOB.getBaseRate()!=0.0) //@@ Added by subrahmanyam for the WPBN issue-174836 on 01/7/09     
              {
                if("0.0".equals(new Double(sellDOB.getMaxRate()).toString()) && "".equals(maxMargin))
                  counter   = 3;
                else
                  counter   = 4;
              }
              else
              {
                if("0.0".equals(new Double(sellDOB.getMaxRate()).toString()) && "".equals(maxMargin))
                  counter   = 2;
                else
                  counter   = 3;
              }
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09            

            }
            else//@@This condition is for "Yes & Percentage"
            {
               //@@ Commented by subrahmanyam for the Enhancement 170759 on 02/06/09            
               /*
               if("0.0".equals(new Double(sellDOB.getMaxRate()).toString()))
                counter   = 2;
              else
                counter   = 3;
                */
//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09 
              if(sellDOB.getBaseRate()!=0.0)//@@ Added by subrahmanyam for the WPBN issue-174836 on 01/7/09 
              {
                if("0.0".equals(new Double(sellDOB.getMaxRate()).toString()))
                counter   = 3;
              else
                counter   = 4;
              }
              else
              {
                if("0.0".equals(new Double(sellDOB.getMaxRate()).toString()))
                counter   = 2;
              else
                counter   = 3;
              }
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09 
            }
           // System.out.println("counter  "+counter);   
            for(int j=0;j<counter;j++)
            {
              //System.out.println("j  "+j);
              dob    =   new QMSCartageSellDtlDOB();
              dob.setCartageId(sellDOB.getCartageId());
              dob.setTerminalId(sellDOB.getTerminalId());              
              dob.setZoneCode(sellDOB.getZoneCode());
              dob.setChargeType(sellDOB.getChargeType());
              dob.setUpperBound("0");
              dob.setLowerBound("0");
              dob.setMarginType("Absolute".equals(request.getParameter("marginType"))?"A":"P");
              dob.setOverallMargin(request.getParameter("overAllMargin"));
              
              if(("No".equals(request.getParameter("overAllMargin"))) || ("Yes".equals(request.getParameter("overAllMargin")) && "Absolute".equals(request.getParameter("marginType"))))
              {
                 if(baseMargin.equals(""))
                  baseMargin="0.0";
                 baseMarginDouble = Double.parseDouble(baseMargin);//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
                 minMarginDouble = Double.parseDouble(minMargin);
                 flatMarginDouble= Double.parseDouble(flatMargin);
                 maxMarginDouble = Double.parseDouble(maxMargin);
                 
  //@@ COmmented by subrahmanyam for the Enhancement 170759 on 02/06/09                 
               /*
                 if(j==0)
                 {
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getMinRate()+minMarginDouble):((sellDOB.getMinRate()*minMarginDouble)/100+sellDOB.getMinRate());
                   
                   dob.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getFlatRate()+flatMarginDouble):((sellDOB.getFlatRate()*flatMarginDouble)/100+sellDOB.getFlatRate());
                  // buyChargeAmount= flatRateDouble;
                   dob.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getMaxRate()+maxMarginDouble):((sellDOB.getMaxRate()*maxMarginDouble)/100+sellDOB.getMaxRate());
                  // buyChargeAmount= maxRateDouble;
                   dob.setLineNumber(2);
                 }
                 */
//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09 
              if(sellDOB.getBaseRate()==0.0)
              {
                  if(j==0)
                 {
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getMinRate()+minMarginDouble):((sellDOB.getMinRate()*minMarginDouble)/100+sellDOB.getMinRate());
                   
                   dob.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getFlatRate()+flatMarginDouble):((sellDOB.getFlatRate()*flatMarginDouble)/100+sellDOB.getFlatRate());
                  // buyChargeAmount= flatRateDouble;
                   dob.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getMaxRate()+maxMarginDouble):((sellDOB.getMaxRate()*maxMarginDouble)/100+sellDOB.getMaxRate());
                  // buyChargeAmount= maxRateDouble;
                   dob.setLineNumber(2);
                 }
              }
              else
              {
                if(j==0)
                 {
                  
                   chargeSlab     = "BASE";
                   marginDouble   = baseMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getBaseRate()+baseMarginDouble):((sellDOB.getBaseRate()*baseMarginDouble)/100+sellDOB.getBaseRate());
                   
                   dob.setLineNumber(j);
                 }
               else if(j==1)
                 {
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getMinRate()+minMarginDouble):((sellDOB.getMinRate()*minMarginDouble)/100+sellDOB.getMinRate());
                   
                   dob.setLineNumber(j);
                 }
                 else if(j==2)
                 {
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getFlatRate()+flatMarginDouble):((sellDOB.getFlatRate()*flatMarginDouble)/100+sellDOB.getFlatRate());
                  // buyChargeAmount= flatRateDouble;
                   dob.setLineNumber(j);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(request.getParameter("marginType"))?(sellDOB.getMaxRate()+maxMarginDouble):((sellDOB.getMaxRate()*maxMarginDouble)/100+sellDOB.getMaxRate());
                  // buyChargeAmount= maxRateDouble;
                   dob.setLineNumber(j);
                 }
              }
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09 
                 dob.setChargeSlab(chargeSlab);
                 dob.setMargin(marginDouble);
                 //dob.setBuyChargeAmount(buyChargeAmount);
                 dob.setChargeRate(chargeRate);               
              }
              else//@@This condition is for "Yes & Percentage"
              {
                marginPercentage  = Double.parseDouble(request.getParameter("marginPercent"));
                
     //@@ Commented by subrahmanyam for the Enhancement 170759 on 02/06/09                
          /*      
                if(j==0)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (sellDOB.getMinRate()*marginPercentage)/100+sellDOB.getMinRate();
                  // buyChargeAmount= minRateDouble;
                   dob.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (sellDOB.getFlatRate()*marginPercentage)/100+sellDOB.getFlatRate();
                   //buyChargeAmount= sellDOB.getMinRate();
                   dob.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (sellDOB.getMaxRate()*marginPercentage)/100+sellDOB.getMaxRate();
                   //buyChargeAmount= maxRateDouble;
                   dob.setLineNumber(2);
                 }
              */  
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09    

            if(sellDOB.getBaseRate()==0.0)
            {
                 if(j==0)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (sellDOB.getMinRate()*marginPercentage)/100+sellDOB.getMinRate();
                  // buyChargeAmount= minRateDouble;
                   dob.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (sellDOB.getFlatRate()*marginPercentage)/100+sellDOB.getFlatRate();
                   //buyChargeAmount= sellDOB.getMinRate();
                   dob.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (sellDOB.getMaxRate()*marginPercentage)/100+sellDOB.getMaxRate();
                   //buyChargeAmount= maxRateDouble;
                   dob.setLineNumber(2);
                 }
            }
            else
            {
                if(j==0)
                 {
                   chargeSlab     = "BASE";
                   chargeRate     = (sellDOB.getBaseRate()*marginPercentage)/100+sellDOB.getBaseRate();
                  // buyChargeAmount= minRateDouble;
                   dob.setLineNumber(j);
                 }
               else if(j==1)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (sellDOB.getMinRate()*marginPercentage)/100+sellDOB.getMinRate();
                  // buyChargeAmount= minRateDouble;
                   dob.setLineNumber(j);
                 }
                 else if(j==2)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (sellDOB.getFlatRate()*marginPercentage)/100+sellDOB.getFlatRate();
                   //buyChargeAmount= sellDOB.getMinRate();
                   dob.setLineNumber(j);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (sellDOB.getMaxRate()*marginPercentage)/100+sellDOB.getMaxRate();
                   //buyChargeAmount= maxRateDouble;
                   dob.setLineNumber(j);
                 }
            }
//@@Ended by subrahmanyam for the Enhancement 170759 on 02/06/09
                dob.setChargeSlab(chargeSlab);
                dob.setMargin(marginPercentage);
                //dob.setBuyChargeAmount(buyChargeAmount);
                dob.setChargeRate(chargeRate);
              }
              
              //Logger.info(FILE_NAME,"cartageSellDtl.getChargeRate::"+cartageSellDtl.getChargeRate());
              //System.out.println("dob  "+dob); 
              list.add(dob);
            } 
            //System.out.println("list.size()  "+list.size()); 
          if(list.size()>0)
          {
            try
            {
              home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
              remote  =   (ChargeMasterSession)home.create();      
              remote.updateSellDtls(list);              
            }
            catch(Exception e)
            {
              e.printStackTrace();
              throw new FoursoftException("Error while updating the details");
            }         
          }
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
//      Logger.error(FILE_NAME,""+e.toString());
      logger.error(FILE_NAME+""+e.toString());
    }
  }
  private void doGetSellChargesDtls(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginbean)throws FoursoftException
  {
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    QMSCartageSellDtlDOB    dob       = null;
    try
    {
      home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
      remote  =   (ChargeMasterSession)home.create();  
      if("Flat".equalsIgnoreCase(request.getParameter("weightBreak")))
         dob     =   (QMSCartageSellDtlDOB)remote.getSellChargesDtls(request.getParameter("cartageId"),request.getParameter("zoneCode"),request.getParameter("chargeType"));
      else if("Slab".equalsIgnoreCase(request.getParameter("weightBreak")))
         dob     =   (QMSCartageSellDtlDOB)remote.getSellChargesDtlsSlab(request.getParameter("cartageId"),request.getParameter("zoneCode"),request.getParameter("chargeType"),request.getParameter("chargeType"),request.getParameter("uom"));
      else 
         dob     =   (QMSCartageSellDtlDOB)remote.getListUpdatedCharges(request.getParameter("cartageId"),request.getParameter("zoneCode"),request.getParameter("chargeType")); 
      request.getSession().setAttribute("sellDOB",dob);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new FoursoftException("Error while getting the details");
    }
  }
  
  private void doProcessModifiedRatesSlab(HttpServletRequest request,HttpServletResponse response) throws FoursoftException
  {
    QMSCartageSellDtlDOB  sellDOB             =   null;
    QMSCartageSellDtlDOB  dob                 =   null;
    int                   index               =   0;
    String                baseMargin           =   null;//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    String                minMargin           =   null;
    String                maxMargin           =   null;
    String                flatMargin          =   null;
    String                margin              =   null;
    ArrayList             list                =   new ArrayList();
    int                   counter             =   0;    
    String                chargeSlab          =   null;
    double                marginPercentage    =   0.0;
    double                chargeRate          =   0.0;
    ChargeMasterSessionHome home              =   null;
    ChargeMasterSession     remote            =   null;
    String                  marginType        =   null;
    int                  lowerBound           =   0;
    int                  upperBound           =   0;
    int                  count                =   0;   
    String               slabMargin           =   null;
    String[]             marginValues         =   null;
    
    try
    {
      //System.out.println("inside slab slab   "+sellDOB);
      marginValues        =   request.getParameterValues("marginValues");
      index               =   new Integer(request.getParameter("index")).intValue();
      sellDOB             =   (QMSCartageSellDtlDOB)request.getSession().getAttribute("sellDOB");
      request.getSession().removeAttribute("sellDOB");
      
      if("P".equalsIgnoreCase(sellDOB.getMarginType()))
         marginType = "Percentage";
      else
         marginType = "Absolute";
      //System.out.println("sellDOB slab   "+sellDOB);  
      
      /*if(!request.getParameter("marginType").equalsIgnoreCase(sellDOB.getMarginType()) || !request.getParameter("overAllMargin").equalsIgnoreCase(sellDOB.getOverallMargin()))
      {   */           
          baseMargin       =   request.getParameter("baseMargin");//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
          minMargin       =   request.getParameter("minMargin");
          slabMargin      =   request.getParameter("slabMargin");
          maxMargin       =   request.getParameter("maxMargin");
          //System.out.println("sellDOB.getSlabList().size() "+sellDOB.getSlabList().size());
          for(int j=0;j<sellDOB.getSlabList().size();j++)
          {
           // Logger.info(FILE_NAME,"slabValuesslabValues::"+j);
            if(request.getParameter("slabValue"+j)!=null && request.getParameter("slabValue"+j).trim().length()!=0)
            {
              dob = new QMSCartageSellDtlDOB(); 
              if("No".equals(request.getParameter("overAllMargin")))
              {
                    if("Absolute".equals(request.getParameter("marginType")))//@@No & Abs
                      dob.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+j))+Double.parseDouble(marginValues[j]));
                    else
                      dob.setChargeRate((Double.parseDouble(request.getParameter("slabValue"+j))*Double.parseDouble(marginValues[j]))/100+Double.parseDouble(request.getParameter("slabValue"+j)));
                    
                    
                    dob.setMargin(Double.parseDouble(marginValues[j]));
               }
               else if("Yes".equals(request.getParameter("overAllMargin")) && "Absolute".equals(request.getParameter("marginType")))
               {            
                if("MIN".equals(request.getParameter("slabBreak"+j)))
                {
                  dob.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+j))+Double.parseDouble(minMargin));
                  dob.setMargin(Double.parseDouble(minMargin));
                }
                else if("MAX".equals(request.getParameter("slabBreak"+j)))
                {
                  dob.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+j))+Double.parseDouble(maxMargin));
                  dob.setMargin(Double.parseDouble(maxMargin));
                }
                //@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09                
                else if("BASE".equals(request.getParameter("slabBreak"+j)))
                {
                  dob.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+j))+Double.parseDouble(baseMargin));
                  dob.setMargin(Double.parseDouble(baseMargin));
                }
//@@Ended by subrahmanyam for the Enhancement 170759 on 02/06/09
                else
                {
                  dob.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+j))+Double.parseDouble(slabMargin));
                  dob.setMargin(Double.parseDouble(slabMargin));
                }
              }
              else
              {
                dob.setChargeRate((Double.parseDouble(request.getParameter("slabValue"+j))*Double.parseDouble(request.getParameter("marginPercent"))/100+Double.parseDouble(request.getParameter("slabValue"+j))));
                dob.setMargin(Double.parseDouble(request.getParameter("marginPercent")));
              }
              dob.setZoneCode(sellDOB.getZoneCode());  
              dob.setMarginType("Absolute".equalsIgnoreCase(request.getParameter("marginType"))?"A":"P");
              dob.setOverallMargin(request.getParameter("overAllMargin"));
              dob.setCartageId(sellDOB.getCartageId());
              dob.setChargeType(sellDOB.getChargeType());
              dob.setLineNumber(counter);
              counter++;
              
              list.add(dob);//@@Adding Slab rates
              count = (j+2);
              dob = null;
            }
          }      
              
      
            //System.out.println("list.size()  "+list.size()); 
          if(list.size()>0)
          {
            try
            {
              home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
              remote  =   (ChargeMasterSession)home.create();      
              remote.updateSellDtls(list);              
            }
            catch(Exception e)
            {
              e.printStackTrace();
              throw new FoursoftException("Error while updating the details");
            }         
          }
      //}
    }
    catch(Exception e)
    {
      e.printStackTrace();
//      Logger.error(FILE_NAME,""+e.toString());
      logger.error(FILE_NAME+""+e.toString());
      throw new FoursoftException("An Error Has Occurred While Updating Sell Rate Details. Please Try Again.");
    }
  }
  
  private void doProcessModifiedRatesList(HttpServletRequest request,HttpServletResponse response) throws FoursoftException
  {
    QMSCartageSellDtlDOB  sellDOB             =   null;
    QMSCartageSellDtlDOB  dob                 =   null;
    String                margin              =   null;
    ArrayList             list                =   new ArrayList();
    int                   counter             =   0;    
    String                chargeSlab          =   null;
    double                chargeRate          =   0.0;
    String                marginType          =   null;
    String                slabMargin          =   null;
    String                overAllMargin       =   null;
    String                marginPercent       =   null;
    String[]              marginValues        =   null;
    String[]              absMargins          =   null;
    
    ChargeMasterSessionHome home              =   null;
    ChargeMasterSession     remote            =   null;
    
    
    try
    {
        marginValues        =   request.getParameterValues("marginValues");
        absMargins          =   request.getParameterValues("absMargins");
        overAllMargin       =   request.getParameter("overAllMargin");
        marginPercent       =   request.getParameter("marginPercent");
        marginType          =   request.getParameter("marginType");
        sellDOB             =   (QMSCartageSellDtlDOB)request.getSession().getAttribute("sellDOB");
        
        request.getSession().removeAttribute("sellDOB");
        
       
           
        for(int j=0;j<sellDOB.getSlabList().size();j++)
        {
           if(request.getParameter("slabValue"+j)!=null && request.getParameter("slabValue"+j).trim().length()!=0)
            {
              dob = new QMSCartageSellDtlDOB(); 
              if("No".equals(overAllMargin))
              {
                if("Absolute".equals(marginType))//@@No & Abs
                  dob.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+j))+Double.parseDouble(marginValues[j]));
                else
                  dob.setChargeRate((Double.parseDouble(request.getParameter("slabValue"+j))*Double.parseDouble(marginValues[j]))/100+Double.parseDouble(request.getParameter("slabValue"+j)));                    
                
                dob.setMargin(Double.parseDouble(marginValues[j]));
              }
              else if("Yes".equals(overAllMargin) && "Absolute".equals(marginType))
              {
                  dob.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+j))+Double.parseDouble(absMargins[j]));
                  dob.setMargin(Double.parseDouble(absMargins[j]));
              }
              else//@@Yes & Percent
              {
                dob.setChargeRate((Double.parseDouble(request.getParameter("slabValue"+j))*Double.parseDouble(marginPercent)/100+Double.parseDouble(request.getParameter("slabValue"+j))));
                dob.setMargin(Double.parseDouble(marginPercent));
              }
              
              dob.setZoneCode(sellDOB.getZoneCode());  
              dob.setMarginType("Absolute".equalsIgnoreCase(request.getParameter("marginType"))?"A":"P");
              dob.setOverallMargin(overAllMargin);
              dob.setCartageId(sellDOB.getCartageId());
              dob.setChargeType(sellDOB.getChargeType());
              dob.setLineNumber(counter);
              counter++;
              
              list.add(dob);//@@Adding Slab rates
              dob = null;
            }
        }
        
        if(list.size()>0)
        {
          try
          {
            home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
            remote  =   (ChargeMasterSession)home.create();      
            remote.updateSellDtls(list);
          }
          catch(Exception e)
          {
            e.printStackTrace();
            throw new FoursoftException("An Error Has Occurred While Updating Sell Rate Details. Please Try Again.");
          }         
        }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+""+e.toString());
      throw new FoursoftException("An Error Has Occurred While Updating Sell Rate Details. Please Try Again.");
    }
  }
  
  private QMSCartageMasterDOB doGetMasterInfo(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginbean) throws FoursoftException
  {
    String  locationId    = null;
    String[]  zoneCodes     = null;
    String  vendorId      = null;
    String  effectiveFrom = null;
    String  validUpto     = null;
    String  currencyId    = null;
    String  chargeType    = null;
    String  chargeBasis   = null;
    String  unitofMeasure = null;
    String  weightBreak   = null;
    String  rateType      = null;
    String  maxCharge     = null;
    String  shipmentMode  = null;
    String  consoleType   = null;
    String  primaryUnit   = null;

    String  dateFormat    = null;
    Timestamp effectiveFromTstmp = null;
    Timestamp validUptoTstmp  = null;
    
    
    String  maxChargeFlag = null;
    String  accessLevel   = null;
    
    String[] slabRates    = null;
    String[] listValues   = null;
    
    QMSCartageMasterDOB cartageMaster       = null;
    ESupplyDateUtility  eSupplyDateUtility	= null;
    
    try
    {
      cartageMaster       = new QMSCartageMasterDOB();
      eSupplyDateUtility	=	new ESupplyDateUtility();
      dateFormat          = loginbean.getUserPreferences().getDateFormat();
      eSupplyDateUtility.setPattern(dateFormat);
      
      
      locationId          = request.getParameter("locationId");
      zoneCodes           = request.getParameterValues("zoneCodes");
      //vendorId            = request.getParameter("vendorId");
      effectiveFrom       = request.getParameter("effectiveFrom");
      validUpto           = request.getParameter("validUpto");
      currencyId          = request.getParameter("baseCurrency");
      chargeType          = request.getParameter("chargeType");
      chargeBasis         = request.getParameter("chargeBasis");
      //unitofMeasure       = request.getParameter("uom");
      weightBreak         = request.getParameter("weightBreak");
      rateType            = request.getParameter("rateType");
      maxCharge           = request.getParameter("maxCharge");
      
      //@@Added By Yuvraj for WPBN-25535
      shipmentMode        = request.getParameter("shipMode");
      consoleType         = request.getParameter("consoleType");
      primaryUnit         = request.getParameter("primaryUnit");
      listValues          = request.getParameterValues("listValues");
      //@@Yuvraj
      slabRates           = request.getParameterValues("slabRates");
      
      
      //Logger.info(FILE_NAME,"slabRatesslabRates::"+slabRates);
        
      
      if("HO_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "H";
      else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "A";
      else
          accessLevel = "O";
      
      effectiveFromTstmp  = eSupplyDateUtility.getTimestampWithTime(dateFormat,effectiveFrom,"00:00");
      if(validUpto!=null && validUpto.trim().length()!=0)
         validUptoTstmp      = eSupplyDateUtility.getTimestampWithTime(dateFormat,validUpto,"23:59");
      
      
     // Logger.info(FILE_NAME,"validUptoTstmpvalidUptoTstmp:"+validUptoTstmp);
      if("on".equals(maxCharge))
          maxChargeFlag = "Y";
      else
          maxChargeFlag = "N";
      
      cartageMaster.setLocationId(locationId);
      cartageMaster.setZoneCodes(zoneCodes);
      //cartageMaster.setVendorId(vendorId);
      cartageMaster.setEffectiveFrom(effectiveFromTstmp);
      cartageMaster.setValidUpto(validUptoTstmp);
      cartageMaster.setCurrencyId(currencyId);
      cartageMaster.setChargeType(chargeType);
      cartageMaster.setChargeBasis(chargeBasis);
      //cartageMaster.setUom(unitofMeasure);
      cartageMaster.setWeightBreak(weightBreak);
      cartageMaster.setRateType(rateType);
      cartageMaster.setMaxChargeFlag(maxChargeFlag);
      cartageMaster.setCreatedBy(loginbean.getUserId());
      cartageMaster.setCreatedTimestamp(new java.sql.Timestamp((new java.util.Date()).getTime()));
      cartageMaster.setAccessLevel(accessLevel);
      cartageMaster.setTerminalId(loginbean.getTerminalId());
      cartageMaster.setOperation(request.getParameter("Operation"));
      
      //@@Added by Yuvraj for WPBN-25535
      cartageMaster.setShipmentMode(shipmentMode);
      cartageMaster.setConsoleType(consoleType);
      cartageMaster.setPrimaryUnit(primaryUnit);
      
      if(listValues!=null)
        cartageMaster.setListValues(listValues);
      //@@Yuvraj
      
      if(slabRates!=null)
        cartageMaster.setSlabRates(slabRates);
    }
    catch(Exception e)
    {
//      Logger.error(FILE_NAME,"Error in doGetMasterInfo()"+e);
      logger.error(FILE_NAME+"Error in doGetMasterInfo()"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred. Please Try Again",e);
    }
    return cartageMaster;
  }
  
  private StringBuffer doValidateCartageHdr(HttpServletRequest request, QMSCartageMasterDOB cartageMaster) throws FoursoftException
  {
    StringBuffer            errors    = null;
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    HttpSession             session   = request.getSession();
    
    String                  operation = null;
    
    try 
    {
      operation = request.getParameter("Operation");
      home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
      remote  =   (ChargeMasterSession)home.create();
      cartageMaster.setOperation(operation);
      if("sellAdd".equals(operation)||"buyModify".equals(operation)||"buyView".equals(operation)||"sellModify".equals(operation)||"sellView".equals(operation))
        errors  = remote.validateCartageSellChargesHdr(cartageMaster);
      else
        errors  = remote.validateCartageBuyChargesHdr(cartageMaster);
    }
    catch(Exception e)
    {
      session.removeAttribute("cartageMaster");
//      Logger.error(FILE_NAME,"Error in doValidateCartageHdr"+e);
      logger.error(FILE_NAME+"Error in doValidateCartageHdr"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Validating Data. Please Try Again",e);
    }
    return errors;
    
  }
  
  private ArrayList doFlatBuyChargeAddProcess(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean) throws FoursoftException
  {
    QMSCartageMasterDOB cartageMaster = null;
    QMSCartageBuyDtlDOB cartageBuyDtl = null;
    QMSCartageBuyDtlDOB cartageSlabDtl = null;
    QMSCartageBuyDtlDOB cartageDlvryDtl = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    ArrayList flatBuyCharges = null;
    ArrayList invalidList    = null;  
    
    String[] minCharges   = null;
    String[] flatCharges  = null;
    String[] maxCharges   = null;
    String[] dMinCharges  = null;
    String[] dFlatCharges = null;
    String[] dMaxCharges  = null;
    
    String[] zoneCodes    = null;
    
    int counter           = 0;
    
    HttpSession session = request.getSession();
    String[] densityRatio = null;
    String[] dDensityRatio = null;
 //@@ Added by subrahmanyam for the Enhancement 170759 on 01/0609    
    String[] baseCharges    = null;
    String[] dBaseCharges    = null;
//@@Ended by subrahmanyam for the Enhancement 170759 on 01/0609     

    try
    {
        cartageMaster = (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
        session.removeAttribute("cartageMaster");
        
        flatBuyCharges     =  new ArrayList();
        
        flatBuyCharges.add(0,cartageMaster);//@@Adding Hdr information
        
        minCharges         =  request.getParameterValues("minCharge");
        flatCharges        =  request.getParameterValues("flatCharge");
        maxCharges         =  request.getParameterValues("maxCharge");
        densityRatio       =  request.getParameterValues("densityRatio");
        dMinCharges        =  request.getParameterValues("dMinCharge");
        dFlatCharges       =  request.getParameterValues("dFlatCharge");
        dMaxCharges        =  request.getParameterValues("dMaxCharge");
        dDensityRatio       =  request.getParameterValues("dDensityRatio");
        zoneCodes          =  cartageMaster.getZoneCodes();
  //@@Added by subrahmanyam for the Enhancement 170759 on 01/06/09
        baseCharges       =   request.getParameterValues("baseCharge");
        dBaseCharges        =  request.getParameterValues("dBaseCharge");
//@@Ended by subrahmanyam for the Enhancement 170759 on 01/06/09                 

        String densityRatio1 ="";
        int zoneCodeLen	=	zoneCodes.length;
          for(int i=0;i<zoneCodeLen;i++)
          {
        //for(int j=0;j<2;j++)//@@Commented by subrahmanyam for 170759
//@@ Added by subrahmanyam for the Enhancement 170759         
           if(baseCharges[i]!=null && !baseCharges[i].equals("") && baseCharges[i].trim().length()>0 && Double.parseDouble(baseCharges[i])!=0.0)
           {
             
              for(int j=0;j<3;j++)

            {
              cartageBuyDtl = new QMSCartageBuyDtlDOB();
              cartageBuyDtl.setZoneCode(zoneCodes[i]);
              cartageBuyDtl.setVendorId(cartageMaster.getVendorId());
             cartageBuyDtl.setWeightBreakSlab(j==0?"BASE":j==1?"MIN":"FLAT");
              cartageBuyDtl.setChargeRate(j==0?Double.parseDouble(baseCharges[i]):j==1?Double.parseDouble(minCharges[i]):Double.parseDouble(flatCharges[i]));
              densityRatio1 = "densityRatio"+i;
              cartageBuyDtl.setLineNumber(j);
              cartageBuyDtl.setDensityRatio(request.getParameter(densityRatio1));
              //cartageBuyDtl.setMinRate(Double.parseDouble(minCharges[i]));
              //cartageBuyDtl.setFlatRate(Double.parseDouble(flatCharges[i]));
              
              if(dMinCharges==null)
                cartageBuyDtl.setChargeType(cartageMaster.getChargeType());
              else
                cartageBuyDtl.setChargeType("Pickup");
              
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageBuyDtl.setMaxRate(Double.parseDouble(maxCharges[i]));
                
              cartageBuyDtl.setLowerBound("0");
              cartageBuyDtl.setUpperBound("0");
              cartageBuyDtl.setChargeBasis(cartageMaster.getChargeBasis());
              cartageBuyDtl.setEffectiveFrom(cartageMaster.getEffectiveFrom());
              cartageBuyDtl.setValidUpto(cartageMaster.getValidUpto());
                
              flatBuyCharges.add(cartageBuyDtl);//@@Adding Dtl information
             }
           }
           else
           {
            for(int j=0;j<2;j++)

            {
              cartageBuyDtl = new QMSCartageBuyDtlDOB();
              cartageBuyDtl.setZoneCode(zoneCodes[i]);
              cartageBuyDtl.setVendorId(cartageMaster.getVendorId());
              cartageBuyDtl.setWeightBreakSlab(j==0?"MIN":"FLAT");
              cartageBuyDtl.setChargeRate(j==0?Double.parseDouble(minCharges[i]):Double.parseDouble(flatCharges[i]));
              densityRatio1 = "densityRatio"+i;
              cartageBuyDtl.setLineNumber(j);
              cartageBuyDtl.setDensityRatio(request.getParameter(densityRatio1));
              //cartageBuyDtl.setMinRate(Double.parseDouble(minCharges[i]));
              //cartageBuyDtl.setFlatRate(Double.parseDouble(flatCharges[i]));
              
              if(dMinCharges==null)
                cartageBuyDtl.setChargeType(cartageMaster.getChargeType());
              else
                cartageBuyDtl.setChargeType("Pickup");
              
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageBuyDtl.setMaxRate(Double.parseDouble(maxCharges[i]));
                
              cartageBuyDtl.setLowerBound("0");
              cartageBuyDtl.setUpperBound("0");
              cartageBuyDtl.setChargeBasis(cartageMaster.getChargeBasis());
              cartageBuyDtl.setEffectiveFrom(cartageMaster.getEffectiveFrom());
              cartageBuyDtl.setValidUpto(cartageMaster.getValidUpto());
                
              flatBuyCharges.add(cartageBuyDtl);//@@Adding Dtl information
             }
          }
             // if("Y".equals(cartageMaster.getMaxChargeFlag())//@@Commented and Modified by Kameswari for the WPBN issue-166729 on 23/04/09
             if("Y".equals(cartageMaster.getMaxChargeFlag())&&Double.parseDouble(maxCharges[i])>0.0)
             {
                cartageBuyDtl = new QMSCartageBuyDtlDOB();
                cartageBuyDtl.setZoneCode(zoneCodes[i]);
                cartageBuyDtl.setVendorId(cartageMaster.getVendorId());
                cartageBuyDtl.setWeightBreakSlab("MAX");
              
                cartageBuyDtl.setChargeRate(Double.parseDouble(maxCharges[i]));
                densityRatio1 = "densityRatio"+i;
                cartageBuyDtl.setDensityRatio(request.getParameter(densityRatio1));
               //@@ Commented by subrahmanyam for the Enhancement 170759 on 03/06/09                
                //cartageBuyDtl.setLineNumber(2);//@@Hard Coding the line number as in case of flat, there will be only two other rows.
//@@ Added by subrahmanyam for the Enhancement 170759 on 03/06/09    
                if(baseCharges[i]!=null && !baseCharges[i].equals("") && baseCharges[i].trim().length()>0)
                    cartageBuyDtl.setLineNumber(3);
                else
                  cartageBuyDtl.setLineNumber(2);
//@@ Ended for 170759  
                //cartageBuyDtl.setMinRate(Double.parseDouble(minCharges[i]));
                //cartageBuyDtl.setFlatRate(Double.parseDouble(flatCharges[i]));
                
                if(dMinCharges==null)
                  cartageBuyDtl.setChargeType(cartageMaster.getChargeType());
                else
                  cartageBuyDtl.setChargeType("Pickup");
                
                //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                  //cartageBuyDtl.setMaxRate(Double.parseDouble(maxCharges[i]));
                  
                cartageBuyDtl.setLowerBound("0");
                cartageBuyDtl.setUpperBound("0");
                cartageBuyDtl.setChargeBasis(cartageMaster.getChargeBasis());
                cartageBuyDtl.setEffectiveFrom(cartageMaster.getEffectiveFrom());
                cartageBuyDtl.setValidUpto(cartageMaster.getValidUpto());
                flatBuyCharges.add(cartageBuyDtl);//@@Adding Dtl information
             }
           }
           
          if(dMinCharges!=null)
          {
            for(int i=0;i<zoneCodeLen;i++)
            {
              //for(int j=0;j<2;j++)//@@ Commented by subrahmanyam for wpbn id 170759 on 01/06/09
              if(dBaseCharges[i]!=null && !dBaseCharges[i].equals("") && dBaseCharges[i].trim().length()>0 && Double.parseDouble(dBaseCharges[i])!=0.0)
              {
                 for(int j=0;j<3;j++)//@@ Added by subrahmanyam for wpbn id 170759 on 01/06/09
              {
                cartageDlvryDtl = new QMSCartageBuyDtlDOB();
                cartageDlvryDtl.setChargeType("Delivery");
                cartageDlvryDtl.setZoneCode(zoneCodes[i]);
                cartageDlvryDtl.setVendorId(cartageMaster.getVendorId());
               cartageDlvryDtl.setWeightBreakSlab(j==0?"BASE":j==1?"MIN":"FLAT");
               cartageDlvryDtl.setChargeRate(j==0?Double.parseDouble(dBaseCharges[i]):j==1?Double.parseDouble(dMinCharges[i]):Double.parseDouble(dFlatCharges[i]));
               
                densityRatio1 = "dDensityRatio"+i;
                cartageDlvryDtl.setDensityRatio(request.getParameter(densityRatio1));
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageDlvryDtl.setMaxRate(Double.parseDouble(dMaxCharges[i]));
                cartageDlvryDtl.setLineNumber(j);
                cartageDlvryDtl.setLowerBound("0");
                cartageDlvryDtl.setUpperBound("0");
                cartageDlvryDtl.setChargeBasis(cartageMaster.getChargeBasis());
                cartageDlvryDtl.setEffectiveFrom(cartageMaster.getEffectiveFrom());
                cartageDlvryDtl.setValidUpto(cartageMaster.getValidUpto());
                flatBuyCharges.add(cartageDlvryDtl);//@@Adding Dtl information
              }
              }
              else
              {
              for(int j=0;j<2;j++)//@@ Added by subrahmanyam for wpbn id 170759 on 01/06/09
              {
                cartageDlvryDtl = new QMSCartageBuyDtlDOB();
                cartageDlvryDtl.setChargeType("Delivery");
                cartageDlvryDtl.setZoneCode(zoneCodes[i]);
                cartageDlvryDtl.setVendorId(cartageMaster.getVendorId());
                //@@ Commented by subrahmanyam for wpbn id 170759 on 01/06/09    
                //cartageDlvryDtl.setWeightBreakSlab(j==0?"MIN":"FLAT");
                //cartageDlvryDtl.setChargeRate(j==0?Double.parseDouble(dMinCharges[i]):Double.parseDouble(dFlatCharges[i]));
 //@@ Added by subrahmanyam for wpbn id 170759 on 01/06/09               
                cartageDlvryDtl.setWeightBreakSlab(j==0?"MIN":"FLAT");
          
                cartageDlvryDtl.setChargeRate(j==0?Double.parseDouble(dMinCharges[i]):Double.parseDouble(dFlatCharges[i]));
//@@ Ended by subrahmanyam for wpbn id 170759 on 01/06/09                
                densityRatio1 = "dDensityRatio"+i;
                cartageDlvryDtl.setDensityRatio(request.getParameter(densityRatio1));
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageDlvryDtl.setMaxRate(Double.parseDouble(dMaxCharges[i]));
                cartageDlvryDtl.setLineNumber(j);
                cartageDlvryDtl.setLowerBound("0");
                cartageDlvryDtl.setUpperBound("0");
                cartageDlvryDtl.setChargeBasis(cartageMaster.getChargeBasis());
                cartageDlvryDtl.setEffectiveFrom(cartageMaster.getEffectiveFrom());
                cartageDlvryDtl.setValidUpto(cartageMaster.getValidUpto());
                flatBuyCharges.add(cartageDlvryDtl);//@@Adding Dtl information
              }
              }
              // if("Y".equals(cartageMaster.getMaxChargeFlag())//@@Commented and Modified by Kameswari for the WPBN issue-166729 on 23/04/09
             if("Y".equals(cartageMaster.getMaxChargeFlag())&&Double.parseDouble(dMaxCharges[i])>0.0)
             {
                cartageDlvryDtl = new QMSCartageBuyDtlDOB();
                cartageDlvryDtl.setChargeType("Delivery");
                cartageDlvryDtl.setZoneCode(zoneCodes[i]);
                cartageDlvryDtl.setVendorId(cartageMaster.getVendorId());
                cartageDlvryDtl.setWeightBreakSlab("MAX");
         
                cartageDlvryDtl.setChargeRate(Double.parseDouble(dMaxCharges[i]));
                densityRatio1 = "dDensityRatio"+i;
                cartageDlvryDtl.setDensityRatio(request.getParameter(densityRatio1));
               //@@ Commented by subrahmanyam for the Enhancement 170759 on 03/06/09                
                //cartageDlvryDtl.setLineNumber(2);//@@Hard Coding the line number as in case of flat, there will be only two other rows.
//@@Added by subrahmanyam for the Enhancement 170759 on 03/06/09                
               if(baseCharges[i]!=null && !baseCharges[i].equals("") && baseCharges[i].trim().length()>0)
                      cartageDlvryDtl.setLineNumber(3);
                else
                  cartageDlvryDtl.setLineNumber(2);
//@Ended by subrahmanyam for the Enhancement 170759 on 03/06/09                                  
                cartageDlvryDtl.setLowerBound("0");
                cartageDlvryDtl.setUpperBound("0");
                cartageDlvryDtl.setChargeBasis(cartageMaster.getChargeBasis()); 
                cartageDlvryDtl.setEffectiveFrom(cartageMaster.getEffectiveFrom());
                cartageDlvryDtl.setValidUpto(cartageMaster.getValidUpto());
                flatBuyCharges.add(cartageDlvryDtl);//@@Adding Dtl information
              }
            }
          }
          
          home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote  =   (ChargeMasterSession)home.create();
          
          invalidList = remote.insertCartageBuyCharges(flatBuyCharges);
          //flatBuyCharges.add(cartageBuyDtl);
        
        //Logger.info(FILE_NAME,"flatBuyChargesflatBuyCharges::"+flatBuyCharges.size());
      
    }
    catch(Exception e)
    {
      session.removeAttribute("cartageMaster");
     //      logger.error(FILE_NAME,"Error in doFlatBuyChargeAddProcess"+e);
      logger.error(FILE_NAME+"Error in doFlatBuyChargeAddProcess"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Charges. Please Try Again",e);
    }
    return invalidList;
  }
   private ArrayList doFlatBuyChargeModifyProcess(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean,QMSCartageMasterDOB cartageMaster) throws FoursoftException
  {
    //QMSCartageMasterDOB cartageMaster = null;
    QMSCartageBuyDtlDOB cartageBuyDtl = null;
    QMSCartageBuyDtlDOB cartageSlabDtl = null;
    QMSCartageBuyDtlDOB cartageDlvryDtl = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    ArrayList flatBuyCharges = null;
    
    String[] minCharges     = null;
    String[] flatCharges    = null;
    String[] maxCharges     = null;
    String[] dMinCharges    = null;
    String[] dFlatCharges   = null;
    String[] dMaxCharges    = null;
    String[]  checkBoxValue = null;
    String[] cartageId      = null;
    String[] delCartageId   = null;
    String[] chargeBasis    = null;
    String[] delChargeBasis = null;
    String[] effectiveFrom    = null;
    String[] validUpto        = null;
    String[] dEffectiveFrom   = null;
    String[] dValidUpto       = null;
    Timestamp effectiveFromTstmp  =null;
    Timestamp validUptoTstmp      =null;
    ESupplyDateUtility eSupplyDateUtility=new ESupplyDateUtility();
    String dateFormat     =null;
    
    String[] zoneCodes    = null;
    
    int counter           = 0;
    String densityRatio1  = null;
    
    ArrayList invalidList = null;
   //@@ Added by subrahmanyam for 170759 on 01/06/09   
     String[]       baseCharges      = null;
     String[]       dBaseCharges     = null;
//@@ Ended by subrahmanyam for 170759 on 01/06/09

    
    try
    {
        
        flatBuyCharges     =  new ArrayList();
        
        flatBuyCharges.add(0,cartageMaster);//@@Adding Hdr information
        
        minCharges         = request.getParameterValues("minRt");
        flatCharges        = request.getParameterValues("flatRt");
        maxCharges         = request.getParameterValues("maxRt");
 //@@ Added by subrahmanyam for 170759 on 01/06/09   
      baseCharges           = request.getParameterValues("baseRt");
      dBaseCharges          = request.getParameterValues("dBaseRt");
//@@ Ended by subrahmanyam for 170759 on 01/06/09       
        dMinCharges        = request.getParameterValues("dMinRt");
        dFlatCharges       = request.getParameterValues("dFlatRt");
        dMaxCharges        = request.getParameterValues("dMaxRt");
        chargeBasis        = request.getParameterValues("chargeBasis");
        delChargeBasis     = request.getParameterValues("delChargeBasis");
        checkBoxValue	     = request.getParameterValues("checkBoxValue");
        cartageId		       = request.getParameterValues("cartageId");
        effectiveFrom	     = request.getParameterValues("effectiveFrom");
        validUpto		       = request.getParameterValues("validUpto");
        zoneCodes		       = request.getParameterValues("zoneCode");
        cartageMaster.setZoneCodes(zoneCodes);
        dEffectiveFrom	   = request.getParameterValues("dEffectiveFrom");
        dValidUpto		     = request.getParameterValues("dValidUpto");
      
        delCartageId	   =   request.getParameterValues("dCartageId");
        
        //zoneCode          =  cartageMaster.getZoneCode();
        dateFormat          = loginbean.getUserPreferences().getDateFormat();
        eSupplyDateUtility.setPattern(dateFormat);
      //System.out.println("effectiveFrom:"+effectiveFrom+"dfafa");
      /*if((effectiveFrom!=null && effectiveFrom.trim().length()!=0) && !"null".equalsIgnoreCase(effectiveFrom))
         effectiveFromTstmp=eSupplyDateUtility.getTimestampWithTime(dateFormat,effectiveFrom,"00:00");
       if((validUpto!=null && validUpto.trim().length()!=0) && !"null".equalsIgnoreCase( validUpto))
          validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,validUpto,"23:59");
		 
		 cartageMaster.setEffectiveFrom(effectiveFromTstmp);
		 cartageMaster.setValidUpto(validUptoTstmp);*/
		
		//cartageMaster.setCartageId(Long.parseLong(cartageId[cartageId.length-1]));
        int chkBoxValLen	=	checkBoxValue.length;	
      for(int k=0;k<chkBoxValLen;k++)
      {
        if("YES".equals(checkBoxValue[k]))
        {
          if(!"-".equals(cartageId[k]))
          {
            if(baseCharges[k]!=null && !baseCharges[k].equals("") && baseCharges[k].trim().length()>0 && Double.parseDouble(baseCharges[k])!=0.0)
            {
             
              for(int j=0;j<3;j++)//@@ Added by subrahmanyam for the 170759
            {
              cartageBuyDtl = new QMSCartageBuyDtlDOB();
              cartageBuyDtl.setZoneCode(zoneCodes[k]);
              cartageBuyDtl.setVendorId(cartageMaster.getVendorId());
              cartageBuyDtl.setWeightBreakSlab(j==0?"BASE":j==1?"MIN":"FLAT");
              cartageBuyDtl.setChargeRate(j==0?Double.parseDouble(baseCharges[k]):j==1?Double.parseDouble(minCharges[k]):Double.parseDouble(flatCharges[k]));
//@@ Ended by subrahmanyam for the Enhancement 170759 on 01/06/09             
            cartageBuyDtl.setCartageId(Long.parseLong(cartageId[k]));
              cartageBuyDtl.setLineNumber(j);
              densityRatio1 = "densityRatio"+k;              
              cartageBuyDtl.setDensityRatio(request.getParameter(densityRatio1));
              if(dMinCharges==null && !"BOTH".equalsIgnoreCase(cartageMaster.getChargeType()))//BOTH is included for issue 172884 by VLAKSHMI on 8/06/2009
                cartageBuyDtl.setChargeType(cartageMaster.getChargeType());
              else
                cartageBuyDtl.setChargeType("Pickup");
              
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageBuyDtl.setMaxRate(Double.parseDouble(maxCharges[i]));
                
              cartageBuyDtl.setLowerBound("0");
              cartageBuyDtl.setUpperBound("0");
              cartageBuyDtl.setChargeBasis(chargeBasis[k]);
              if(effectiveFrom!=null)
              {
                if(effectiveFrom[k]!=null && effectiveFrom[k].trim().length()!=0)
                    effectiveFromTstmp=eSupplyDateUtility.getTimestampWithTime(dateFormat,effectiveFrom[k],"00:00");
              }
              if(validUpto!=null)
              {
                if(validUpto[k]!=null && validUpto[k].trim().length()!=0)
                  validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,validUpto[k],"23:59");
              }
              //cartageBuyDtl.setEffectiveFrom(effectiveFromTstmp);
              cartageBuyDtl.setEffectiveFrom(cartageMaster.getCreatedTimestamp());//@@Commented and Modified by Kameswari for the WPBN issue-141959 on 13/11/08
              cartageBuyDtl.setValidUpto(validUptoTstmp);
              flatBuyCharges.add(cartageBuyDtl);//@@Adding Dtl information
              
              effectiveFromTstmp = null;
              validUptoTstmp     = null;
             } 
            }
            //for(int j=0;j<2;j++)//@@ Commented by subrahmanyam for the 170759
            else
            {
            for(int j=0;j<2;j++)//@@ Added by subrahmanyam for the 170759
            {
              cartageBuyDtl = new QMSCartageBuyDtlDOB();
              cartageBuyDtl.setZoneCode(zoneCodes[k]);
              cartageBuyDtl.setVendorId(cartageMaster.getVendorId());
                         
              cartageBuyDtl.setWeightBreakSlab(j==0?"MIN":"FLAT");
             cartageBuyDtl.setChargeRate(j==0?Double.parseDouble(minCharges[k]):Double.parseDouble(flatCharges[k]));
         
             cartageBuyDtl.setChargeRate(j==0?Double.parseDouble(minCharges[k]):Double.parseDouble(flatCharges[k]));
//@@ Ended by subrahmanyam for the Enhancement 170759 on 01/06/09             
            cartageBuyDtl.setCartageId(Long.parseLong(cartageId[k]));
              cartageBuyDtl.setLineNumber(j);
              densityRatio1 = "densityRatio"+k;              
              cartageBuyDtl.setDensityRatio(request.getParameter(densityRatio1));
              if(dMinCharges==null && !"BOTH".equalsIgnoreCase(cartageMaster.getChargeType()))//BOTH is included for issue 172884 by VLAKSHMI on 8/06/2009
                cartageBuyDtl.setChargeType(cartageMaster.getChargeType());
              else
                cartageBuyDtl.setChargeType("Pickup");
              
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageBuyDtl.setMaxRate(Double.parseDouble(maxCharges[i]));
                
              cartageBuyDtl.setLowerBound("0");
              cartageBuyDtl.setUpperBound("0");
              cartageBuyDtl.setChargeBasis(chargeBasis[k]);
              if(effectiveFrom!=null)
              {
                if(effectiveFrom[k]!=null && effectiveFrom[k].trim().length()!=0)
                    effectiveFromTstmp=eSupplyDateUtility.getTimestampWithTime(dateFormat,effectiveFrom[k],"00:00");
              }
              if(validUpto!=null)
              {
                if(validUpto[k]!=null && validUpto[k].trim().length()!=0)
                  validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,validUpto[k],"23:59");
              }
              //cartageBuyDtl.setEffectiveFrom(effectiveFromTstmp);
              cartageBuyDtl.setEffectiveFrom(cartageMaster.getCreatedTimestamp());//@@Commented and Modified by Kameswari for the WPBN issue-141959 on 13/11/08
              cartageBuyDtl.setValidUpto(validUptoTstmp);
              flatBuyCharges.add(cartageBuyDtl);//@@Adding Dtl information
              
              effectiveFromTstmp = null;
              validUptoTstmp     = null;
             }
          }
             if(maxCharges[k]!=null && maxCharges[k].trim().length()>0)
             {
                //if(Double.parseDouble(maxCharges[k])>0)
                //@@Commented and Modified by Kameswari for the WPBN issue-166729 on 23/0/09
              if(Double.parseDouble(maxCharges[k])>0.0) 
              {
                  cartageBuyDtl = new QMSCartageBuyDtlDOB();
                  cartageBuyDtl.setZoneCode(zoneCodes[k]);
                  cartageBuyDtl.setVendorId(cartageMaster.getVendorId());
                  cartageBuyDtl.setWeightBreakSlab("MAX");
            
                  cartageBuyDtl.setChargeRate(Double.parseDouble(maxCharges[k]));
                  //cartageBuyDtl.setMinRate(Double.parseDouble(minCharges[i]));
                  //cartageBuyDtl.setFlatRate(Double.parseDouble(flatCharges[i]));
                  // cartageBuyDtl.setLineNumber(2);//@@ Commented by subrahmanyam for the Enhancement 170759 on 03/06/09
//@@ Added by subrahmanyam for the Enhancement 170759 on 03/06/09                  
                 if(baseCharges[k]!=null && !baseCharges[k].equals("") && baseCharges[k].trim().length()>0)
                 cartageBuyDtl.setLineNumber(3);
                 else
                 cartageBuyDtl.setLineNumber(2);
//@@ended by subrahmanyam for the Enhancement 170759                  
                /*  if(dMinCharges==null)
                    cartageBuyDtl.setChargeType(cartageMaster.getChargeType());
                  else*/           //@@Modified for the uat issue-
                   if(dMinCharges==null && !"BOTH".equalsIgnoreCase(cartageMaster.getChargeType()))////BOTH is included for issue 172884 by VLAKSHMI on 8/06/2009
                    cartageBuyDtl.setChargeType(cartageMaster.getChargeType());
                  else
                    cartageBuyDtl.setChargeType("Pickup");
                  densityRatio1 = "densityRatio"+k;              
                  cartageBuyDtl.setDensityRatio(request.getParameter(densityRatio1));
                  //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                    //cartageBuyDtl.setMaxRate(Double.parseDouble(maxCharges[i]));
                    
                  cartageBuyDtl.setLowerBound("0");
                  cartageBuyDtl.setUpperBound("0");
                  cartageBuyDtl.setChargeBasis(chargeBasis[k]);
                  if(effectiveFrom!=null)
                  {
                    if(effectiveFrom[k]!=null && effectiveFrom[k].trim().length()!=0)
                        effectiveFromTstmp=eSupplyDateUtility.getTimestampWithTime(dateFormat,effectiveFrom[k],"00:00");
                  }
                  if(validUpto!=null)
                  {
                    if(validUpto[k]!=null && validUpto[k].trim().length()!=0)
                      validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,validUpto[k],"23:59");
                  }
                  //cartageBuyDtl.setEffectiveFrom(effectiveFromTstmp);
                  cartageBuyDtl.setEffectiveFrom(cartageMaster.getCreatedTimestamp());//@@Commented and Modified by Kameswari for the WPBN issue-141959 on 13/11/08
          
                  cartageBuyDtl.setValidUpto(validUptoTstmp);
                  flatBuyCharges.add(cartageBuyDtl);//@@Adding Dtl information
                  effectiveFromTstmp = null;
                  validUptoTstmp     = null;
                }
             }
           }
           
          if(dMinCharges!=null)//@@This loop will execute if Delivery Charges Exist in case of Charge Type Both.
          {
             if(!"-".equalsIgnoreCase(delCartageId[k]))
            {
              if(dBaseCharges[k]!=null && !dBaseCharges[k].equals("") && dBaseCharges[k].trim().length()>0 && Double.parseDouble(dBaseCharges[k])!=0.0)
              {
                 for(int j=0;j<3;j++)
              {
                cartageDlvryDtl = new QMSCartageBuyDtlDOB();
                cartageDlvryDtl.setChargeType("Delivery");
                cartageDlvryDtl.setCartageId(Long.parseLong(delCartageId[k]));
                cartageDlvryDtl.setZoneCode(zoneCodes[k]);
                cartageDlvryDtl.setVendorId(cartageMaster.getVendorId());
                cartageDlvryDtl.setWeightBreakSlab(j==0?"BASE":j==1?"MIN":"FLAT");
                 cartageDlvryDtl.setChargeRate(j==0?Double.parseDouble(dBaseCharges[k]):j==1?Double.parseDouble(dMinCharges[k]):Double.parseDouble(dFlatCharges[k]));

                cartageDlvryDtl.setLineNumber(j);
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageDlvryDtl.setMaxRate(Double.parseDouble(dMaxCharges[i]));
                densityRatio1 = "dDensityRatio"+k;              
                cartageDlvryDtl.setDensityRatio(request.getParameter(densityRatio1));
                cartageDlvryDtl.setLowerBound("0");
                cartageDlvryDtl.setUpperBound("0");
                cartageDlvryDtl.setChargeBasis(delChargeBasis[k]);
                if(dEffectiveFrom!=null)
                {
                  if(dEffectiveFrom[k]!=null && dEffectiveFrom[k].trim().length()!=0)
                      effectiveFromTstmp=eSupplyDateUtility.getTimestampWithTime(dateFormat,dEffectiveFrom[k],"00:00");
                }
                if(dValidUpto!=null)
                {
                  if(dValidUpto[k]!=null && dValidUpto[k].trim().length()!=0)
                    validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,dValidUpto[k],"23:59");
                }
                //cartageDlvryDtl.setEffectiveFrom(effectiveFromTstmp);
                cartageDlvryDtl.setEffectiveFrom(cartageMaster.getCreatedTimestamp());//@@Commented and Modified by Kameswari for the WPBN issue-141959 on 13/11/08
          
                cartageDlvryDtl.setValidUpto(validUptoTstmp);
                  
                flatBuyCharges.add(cartageDlvryDtl);//@@Adding Dtl information
                
                effectiveFromTstmp  = null;
                validUptoTstmp      = null;
              }
                
              }
              else
              {
              for(int j=0;j<2;j++)
              {
                cartageDlvryDtl = new QMSCartageBuyDtlDOB();
                cartageDlvryDtl.setChargeType("Delivery");
                cartageDlvryDtl.setCartageId(Long.parseLong(delCartageId[k]));
                cartageDlvryDtl.setZoneCode(zoneCodes[k]);
                cartageDlvryDtl.setVendorId(cartageMaster.getVendorId());
                //@@ Commented by subrahmanyam for wpbnId: 170759                 
                //cartageDlvryDtl.setWeightBreakSlab(j==0?"MIN":"FLAT");
                //cartageDlvryDtl.setChargeRate(j==0?Double.parseDouble(dMinCharges[k]):Double.parseDouble(dFlatCharges[k]));
//@@ Added by subrahmanyam for wpbnId: 170759                  
                cartageDlvryDtl.setWeightBreakSlab(j==0?"MIN":"FLAT");
         
                cartageDlvryDtl.setChargeRate(j==0?Double.parseDouble(dMinCharges[k]):Double.parseDouble(dFlatCharges[k]));
//@@ Ended by subrahmanyam for wpbnId: 170759 
                cartageDlvryDtl.setLineNumber(j);
              //if("Y".equals(cartageMaster.getMaxChargeFlag()))
                //cartageDlvryDtl.setMaxRate(Double.parseDouble(dMaxCharges[i]));
                densityRatio1 = "dDensityRatio"+k;              
                cartageDlvryDtl.setDensityRatio(request.getParameter(densityRatio1));
                cartageDlvryDtl.setLowerBound("0");
                cartageDlvryDtl.setUpperBound("0");
                cartageDlvryDtl.setChargeBasis(delChargeBasis[k]);
                if(dEffectiveFrom!=null)
                {
                  if(dEffectiveFrom[k]!=null && dEffectiveFrom[k].trim().length()!=0)
                      effectiveFromTstmp=eSupplyDateUtility.getTimestampWithTime(dateFormat,dEffectiveFrom[k],"00:00");
                }
                if(dValidUpto!=null)
                {
                  if(dValidUpto[k]!=null && dValidUpto[k].trim().length()!=0)
                    validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,dValidUpto[k],"23:59");
                }
                //cartageDlvryDtl.setEffectiveFrom(effectiveFromTstmp);
                cartageDlvryDtl.setEffectiveFrom(cartageMaster.getCreatedTimestamp());//@@Commented and Modified by Kameswari for the WPBN issue-141959 on 13/11/08
          
                cartageDlvryDtl.setValidUpto(validUptoTstmp);
                  
                flatBuyCharges.add(cartageDlvryDtl);//@@Adding Dtl information
                
                effectiveFromTstmp  = null;
                validUptoTstmp      = null;
              }
              }
              if(dMaxCharges[k]!=null && dMaxCharges[k].trim().length()>0)
              {
                if(Double.parseDouble(dMaxCharges[k])>0.0)
                //@@Commented and Modified by Kameswari for the WPBN issue-166729 on 23/0/09
                    {
                  cartageDlvryDtl = new QMSCartageBuyDtlDOB();
                  cartageDlvryDtl.setChargeType("Delivery");
                  cartageDlvryDtl.setZoneCode(zoneCodes[k]);
                  cartageDlvryDtl.setVendorId(cartageMaster.getVendorId());
                  cartageDlvryDtl.setWeightBreakSlab("MAX");
        
                  cartageDlvryDtl.setChargeRate(Double.parseDouble(dMaxCharges[k]));
                 //cartageDlvryDtl.setLineNumber(2);//@@ Commented by subrahmanyam for the Enhancement 170759 on 03/06/09
//@@ Added by subrahmanyam for the Enhancement 170759 on 03/06/09                 
                  if(dBaseCharges[k]!=null && !dBaseCharges[k].equals("") && dBaseCharges[k].trim().length()>0)
                     cartageDlvryDtl.setLineNumber(3);
                     else
                     cartageDlvryDtl.setLineNumber(2);
//@@ Ended by subrahmanyam for the enhancement 170759                     
                  densityRatio1 = "dDensityRatio"+k;              
                  cartageDlvryDtl.setDensityRatio(request.getParameter(densityRatio1));  
                  cartageDlvryDtl.setLowerBound("0");
                  cartageDlvryDtl.setUpperBound("0");
                  cartageDlvryDtl.setChargeBasis(delChargeBasis[k]);
                  if(dEffectiveFrom!=null)
                  {
                    if(dEffectiveFrom[k]!=null && dEffectiveFrom[k].trim().length()!=0)
                        effectiveFromTstmp=eSupplyDateUtility.getTimestampWithTime(dateFormat,dEffectiveFrom[k],"00:00");
                  }
                  if(dValidUpto!=null)
                  {
                    if(dValidUpto[k]!=null && dValidUpto[k].trim().length()!=0)
                      validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,dValidUpto[k],"23:59");
                  }
                  //cartageDlvryDtl.setEffectiveFrom(effectiveFromTstmp);
                  cartageDlvryDtl.setEffectiveFrom(cartageMaster.getCreatedTimestamp());//@@Commented and Modified by Kameswari for the WPBN issu-141959 on 13/11/08
                  cartageDlvryDtl.setValidUpto(validUptoTstmp);
                  flatBuyCharges.add(cartageDlvryDtl);//@@Adding Dtl information
                  effectiveFromTstmp  = null;
                  validUptoTstmp      = null;
                }
              }
            }
          
          
		  }
		}
	  }
          //Logger.info(FILE_NAME,"before calling flat insertCartageBuyCharges"); 
          home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote  =   (ChargeMasterSession)home.create();
          
          invalidList   = remote.insertCartageBuyCharges(flatBuyCharges);
    
    }
    catch(Exception e)
    {
      //session.removeAttribute("cartageMaster");
//      Logger.error(FILE_NAME,"Error in doFlatBuyChargeAddProcess"+e);
      logger.error(FILE_NAME+"Error in doFlatBuyChargeAddProcess"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Charges. Please Try Again",e);
    }
    return invalidList;
  }
  private ArrayList doSlabBuyChargesAddProcess(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean) throws FoursoftException
  {
    QMSCartageMasterDOB cartageMaster    = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    ArrayList slabBuyCharges = null;
    
    String[]  minValues      = null;
    String[]  slabValues     = null;
    String[]  flatValues     = null;
    String[]  maxValues      = null;
    String[]  indicator      = null;
    
    String[]  dMinValues      = null;
    String[]  dSlabValues     = null;
    String[]  dFlatValues     = null;
    String[]  dMaxValues      = null;
    String[]  dIndicator      = null;
    //@@ Added by subrahmanyam for the Enhancement 170759 on 01/06/09    
    String[]  baseValues      = null;
    String[]  dBaseValues      = null;
//@@ Ende by subrahmanyam for the Enhancement 170759 on 01/06/09

    
    
    String  chargeType        = null;
    ArrayList invalidList     = null;
    
    HttpSession session = request.getSession();
    try
    {
        cartageMaster = (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
        session.removeAttribute("cartageMaster");
        
        slabBuyCharges     =  new ArrayList();
        
        slabBuyCharges.add(0,cartageMaster);//@@Adding Hdr information
        
        minValues         =  request.getParameterValues("minValue");
        slabValues        =  request.getParameterValues("slabValues");
        flatValues        =  request.getParameterValues("flatValues");//@@In case of Rate Type Both
        maxValues         =  request.getParameterValues("maxValue");
        indicator         =  request.getParameterValues("indicator");//@@In case of Rate Type Both
        //@@ Added by subrahmanyam for the Enhancement 170759 01/06/09        
        baseValues         =  request.getParameterValues("baseValue");
        dBaseValues         =  request.getParameterValues("dBaseValue");
//@@ Ended by subrahmanyam for the Enhancement 170759 01/06/09

        dMinValues        =  request.getParameterValues("dMinValue");
        dSlabValues       =  request.getParameterValues("dSlabValues");
        dFlatValues       =  request.getParameterValues("dFlatValues");//@@In case of Charge Type & Rate Type Both 
        dMaxValues        =  request.getParameterValues("dMaxValue");
        dIndicator        =  request.getParameterValues("dIndicator");//@@In case of Charge Type & Rate Type Both
        
       
        if(dMinValues==null)
              chargeType = cartageMaster.getChargeType();
            else
              chargeType =  "Pickup";
        
       //@@ Commented by subrahmanyam for the Enhancement 170759 on 01/06/09        
        //slabBuyCharges = doSlabAddtoList(slabBuyCharges,cartageMaster,minValues,slabValues,maxValues,flatValues,indicator,chargeType,request);
//@@ Added by subrahmanyam for the Enhancement 170759 on 01/06/09        
        slabBuyCharges = doSlabAddtoList(slabBuyCharges,cartageMaster,baseValues,minValues,slabValues,maxValues,flatValues,indicator,chargeType,request);
 
         if(dMinValues!=null)
         {
            chargeType =  "Delivery";
            //@@ Commented by subrahmanyam for the Enhancement 170759 on 01/06/09            
           // slabBuyCharges = doSlabAddtoList(slabBuyCharges,cartageMaster,dMinValues,dSlabValues,dMaxValues,dFlatValues,dIndicator,chargeType,request);  
//@@ Added by subrahmanyam for the Enhancement 170759 on 01/06/09                   
           slabBuyCharges = doSlabAddtoList(slabBuyCharges,cartageMaster,dBaseValues,dMinValues,dSlabValues,dMaxValues,dFlatValues,dIndicator,chargeType,request);  
           }
        
        
        //Logger.info(FILE_NAME,"slabBuyCharges::"+slabBuyCharges.size());
        
        home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote  =   (ChargeMasterSession)home.create();
        //Logger.info(FILE_NAME,"before calling insertCartageBuyCharges");  
        logger.info(FILE_NAME+"before calling insertCartageBuyCharges");  
        invalidList = remote.insertCartageBuyCharges(slabBuyCharges);
    }
    catch(FoursoftException fs)
    {
      session.removeAttribute("cartageMaster");
     
//      Logger.error(FILE_NAME,"Error in doSlabBuyChargesAddProcess:"+fs);
      logger.error(FILE_NAME+"Error in doSlabBuyChargesAddProcess:"+fs);
      fs.printStackTrace();
      throw new FoursoftException(fs.getMessage(),fs);
    }
    catch(Exception e)
    {
      session.removeAttribute("cartageMaster");
     
//      Logger.error(FILE_NAME,"Error in doSlabBuyChargesAddProcess:"+e);
      logger.error(FILE_NAME+"Error in doSlabBuyChargesAddProcess:"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Charges. Please Try Again",e);
    }
    return invalidList;
  }
  
    // Method signature commented & added by subrahmanyam for the Enhancement 170759 on 01/06/09
  //private ArrayList doSlabAddtoList(ArrayList slabBuyCharges,QMSCartageMasterDOB cartageMaster,String[] minValues, String[] slabValues, String[] maxValues,String[] flatValues,String[] indicator,String chargeType,HttpServletRequest request) throws FoursoftException 
  private ArrayList doSlabAddtoList(ArrayList slabBuyCharges,QMSCartageMasterDOB cartageMaster,String[] baseValues,String[] minValues, String[] slabValues, String[] maxValues,String[] flatValues,String[] indicator,String chargeType,HttpServletRequest request) throws FoursoftException
{
    QMSCartageBuyDtlDOB cartageBuyDtlMin = null;
    QMSCartageBuyDtlDOB cartageBuyDtlSlab= null;
    QMSCartageBuyDtlDOB cartageBuyDtlMax = null;
     QMSCartageBuyDtlDOB cartageBuyDtlBase = null;//@@Added by subrahmanyam for the Enhancement 170759 on 01/06/09
    String[]  zoneCodes       = null;
    String[]  slabRates       = null;
    
    int counter               = 0;
    int lowerBound            = 0;
    int upperBound            = 0;
    int count                 = 0;
    int lineNo                = 1;//@@ Added by subrahmanyam for the Enhancemente 170759 on 03/06/09
 
    try
    {
      zoneCodes         =  cartageMaster.getZoneCodes();
      slabRates         =  cartageMaster.getSlabRates();
      int zoneCodesLen	=	zoneCodes.length;
        for(int i=0;i<zoneCodesLen;i++)
        {
        //@@ Added by subrahmanyam for the Enhancement 170759    
        if(baseValues[i]!=null && !baseValues[i].equals("") && baseValues[i].trim().length()>0 && Double.parseDouble(baseValues[i])!=0.0)
        {
          cartageBuyDtlBase = new QMSCartageBuyDtlDOB();
          cartageBuyDtlBase.setZoneCode(zoneCodes[i]);
          cartageBuyDtlBase.setVendorId(cartageMaster.getVendorId());
          cartageBuyDtlBase.setWeightBreakSlab("BASE");
          cartageBuyDtlBase.setChargeRate(Double.parseDouble(baseValues[i]));
          cartageBuyDtlBase.setLowerBound("0");
          cartageBuyDtlBase.setUpperBound("0");
          cartageBuyDtlBase.setChargeType(chargeType); 
          cartageBuyDtlBase.setDensityRatio(request.getParameter("densityRatio"+i));
          cartageBuyDtlBase.setLineNumber(0);
          cartageBuyDtlBase.setChargeBasis(cartageMaster.getChargeBasis());
          cartageBuyDtlBase.setEffectiveFrom(cartageMaster.getEffectiveFrom());
          cartageBuyDtlBase.setValidUpto(cartageMaster.getValidUpto());
          slabBuyCharges.add(cartageBuyDtlBase);//@@Adding Base rates
          
          cartageBuyDtlBase = null;
        }
//@@ Ended bys subrahmanyam for the Enhancement 170759
          //Logger.info(FILE_NAME,"minValuesminValues::"+minValues[i]);
          cartageBuyDtlMin = new QMSCartageBuyDtlDOB();
          cartageBuyDtlMin.setZoneCode(zoneCodes[i]);
          cartageBuyDtlMin.setVendorId(cartageMaster.getVendorId());
          cartageBuyDtlMin.setWeightBreakSlab("MIN");
          cartageBuyDtlMin.setChargeRate(Double.parseDouble(minValues[i]));
          cartageBuyDtlMin.setLowerBound("0");
          cartageBuyDtlMin.setUpperBound("0");
          cartageBuyDtlMin.setChargeType(chargeType); 
          cartageBuyDtlMin.setDensityRatio(request.getParameter("densityRatio"+i));
           //cartageBuyDtlMin.setLineNumber(0);//@@ Commented by subrahmanyam for the Enhancement 170759 on 03/06/09
//@@ Ended by subrahmanyam for the Enhancement 170759 on 03/06/09           
         if(baseValues[i]!=null && !baseValues[i].equals("") && baseValues[i].trim().length()>0)
          cartageBuyDtlMin.setLineNumber(1);
          else
          cartageBuyDtlMin.setLineNumber(0);
//@@ Ended by subrahmanyam for 170759          
          cartageBuyDtlMin.setChargeBasis(cartageMaster.getChargeBasis());
          cartageBuyDtlMin.setEffectiveFrom(cartageMaster.getEffectiveFrom());
          cartageBuyDtlMin.setValidUpto(cartageMaster.getValidUpto());
          slabBuyCharges.add(cartageBuyDtlMin);//@@Adding Min rates
          
          cartageBuyDtlMin = null;
          int slabRateLen	=	slabRates.length;
          for(int j=0;j<slabRateLen;j++)
          {
            //Logger.info(FILE_NAME,"slabValuesslabValues::"+slabValues[counter]);
            if(j==0)
            {
              lowerBound = 0;
              upperBound = Math.abs(Integer.parseInt(slabRates[j]));
            }
            else if(j==(slabRates.length-1))
            {
              lowerBound = Integer.parseInt(slabRates[j]);
              upperBound = 100000;
            }
            else
            {
              lowerBound = Integer.parseInt(slabRates[j]);
              upperBound = Integer.parseInt(slabRates[j+1]);
            }         
            
            cartageBuyDtlSlab = new QMSCartageBuyDtlDOB();
            cartageBuyDtlSlab.setZoneCode(zoneCodes[i]);
            cartageBuyDtlSlab.setVendorId(cartageMaster.getVendorId());
            cartageBuyDtlSlab.setWeightBreakSlab(slabRates[j]);
            cartageBuyDtlSlab.setDensityRatio(request.getParameter("densityRatio"+i));
            if(indicator!=null)
              cartageBuyDtlSlab.setChargeRateIndicator(indicator[counter]);
            
            if(indicator!=null && "S".equals(indicator[counter]))
              cartageBuyDtlSlab.setChargeRate(Double.parseDouble(slabValues[counter]));
            else if(indicator!=null && "F".equals(indicator[counter]))
              cartageBuyDtlSlab.setChargeRate(Double.parseDouble(flatValues[counter]));
            else
              cartageBuyDtlSlab.setChargeRate(Double.parseDouble(slabValues[counter]));
            
            cartageBuyDtlSlab.setLowerBound(Integer.toString(lowerBound));
            cartageBuyDtlSlab.setUpperBound(Integer.toString(upperBound));
            
            cartageBuyDtlSlab.setChargeType(chargeType);
              if(baseValues[i]!=null && !baseValues[i].equals("") && baseValues[i].trim().length()>0)
              {
                 cartageBuyDtlSlab.setLineNumber((lineNo+1));
                  lineNo++;
              }
              else
                cartageBuyDtlSlab.setLineNumber((j+1));
             //cartageBuyDtlSlab.setLineNumber((j+1));//@@ Commented by subrahmanyam for the Enhancement 03/06/09
//@@ Added by subrahmanyam for the Enhancement 03/06/09            
          //  cartageBuyDtlSlab.setLineNumber((lineNo+1));
           // lineNo++;
//@@ Ended for 170759  
            counter++;
            cartageBuyDtlSlab.setChargeBasis(cartageMaster.getChargeBasis());
            cartageBuyDtlSlab.setEffectiveFrom(cartageMaster.getEffectiveFrom());
            cartageBuyDtlSlab.setValidUpto(cartageMaster.getValidUpto());
            slabBuyCharges.add(cartageBuyDtlSlab);//@@Adding Slab rates
            count = (j+2);
            cartageBuyDtlSlab = null;
          }
        //  Logger.info(FILE_NAME,"count::"+count);
          //if("Y".equals(cartageMaster.getMaxChargeFlag())) //@@Commented and Modified by Kameswari for the WPBN  issue-16769 on 23/04/2009
          if("Y".equals(cartageMaster.getMaxChargeFlag())&&Double.parseDouble(maxValues[i])>0.0)
          {
            cartageBuyDtlMax = new QMSCartageBuyDtlDOB();
            cartageBuyDtlMax.setZoneCode(zoneCodes[i]);
            cartageBuyDtlMax.setVendorId(cartageMaster.getVendorId());
            cartageBuyDtlMax.setWeightBreakSlab("MAX");
            cartageBuyDtlMax.setChargeRate(Double.parseDouble(maxValues[i]));
            cartageBuyDtlMax.setLowerBound("0");
            cartageBuyDtlMax.setUpperBound("0");
            cartageBuyDtlMax.setChargeType(chargeType);
            cartageBuyDtlMax.setLineNumber(count);
            cartageBuyDtlMax.setDensityRatio(request.getParameter("densityRatio"+i));
            cartageBuyDtlMax.setChargeBasis(cartageMaster.getChargeBasis());
            cartageBuyDtlMax.setEffectiveFrom(cartageMaster.getEffectiveFrom());
            cartageBuyDtlMax.setValidUpto(cartageMaster.getValidUpto());
            slabBuyCharges.add(cartageBuyDtlMax);//@@Adding Max rates
            
            cartageBuyDtlMax = null;
          }
        }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Charges. Please Try Again",e);
    }
    return slabBuyCharges;
  }
  private void doSlabBuyChargesModifyProcess(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean,QMSCartageMasterDOB cartageMaster) throws FoursoftException
  {
    //QMSCartageMasterDOB cartageMaster    = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    String    chargeType              = null;
    String    chargeTypeTemp          = null;
    String[]  cartageId               = null;
    String[]  delCartageId            = null;
    
    ArrayList slabBuyCharges          = null;
    
    String[]  indicator               = null;
    String[]  dIndicator              = null;
    String[]  flatValues              = null;
    String[]  slabValues              = null;
    String[]  dSlabValues             = null;
    String[]  dFlatValues             = null;
  
    try
    {
      chargeType      =   request.getParameter("chargeType");
  
      cartageId       =  request.getParameterValues("cartageId");
      delCartageId    =  request.getParameterValues("dCartageId");
      slabBuyCharges  =  new ArrayList();
      
      slabBuyCharges.add(0,cartageMaster);//@@Adding Hdr information
     //uncommented by VLAKSHMI for issue 172623 on 040609
     if(delCartageId==null )
          chargeTypeTemp  = chargeType;
      else
          chargeTypeTemp  = "Pickup";
     
      if(cartageId!=null)
        slabBuyCharges = doSlabBuyChargesAddtoList(request,slabBuyCharges,chargeTypeTemp,"cartageId");
      
      if(delCartageId!=null)
      {
          chargeTypeTemp  = "Delivery";
          slabBuyCharges = doSlabBuyChargesAddtoList(request,slabBuyCharges,chargeTypeTemp,"dCartageId");
      }
        
        //Logger.info(FILE_NAME,"slabBuyCharges::"+slabBuyCharges.size());
         
        home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote  =   (ChargeMasterSession)home.create();
          
        remote.insertCartageBuyCharges(slabBuyCharges);
    }
    catch(FoursoftException fs)
    {
      //session.removeAttribute("cartageMaster");
//      Logger.error(FILE_NAME,"Error in doSlabBuyChargesAddProcess:"+fs);
      logger.error(FILE_NAME+"Error in doSlabBuyChargesAddProcess:"+fs);
      fs.printStackTrace();
      throw new FoursoftException(fs.getMessage(),fs);
    }
    catch(Exception e)
    {
      //session.removeAttribute("cartageMaster");
//      Logger.error(FILE_NAME,"Error in doSlabBuyChargesAddProcess:"+e);
      logger.error(FILE_NAME+"Error in doSlabBuyChargesAddProcess:"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Modifying Charges. Please Try Again",e);
    }
  }
  private ArrayList doSlabBuyChargesAddtoList(HttpServletRequest request,ArrayList slabBuyChargesList,String chargeType,String cartageIdType) throws FoursoftException
  {
    QMSCartageMasterDOB   masterDOB   = null;
    QMSCartageBuyDtlDOB   buyDtlDOB   = null;
    HttpSession           session     = null; 
    String[]              zoneCodes   = null;
    
    String[]  cartageId               = null;
    String[]  checkBoxValues          = null;
    
    String[]  slabRates               = null;
    
    String  lowerBound                = null;
    String  upperBound                = null;
    
    String  slabBreak                 = null;
    String  slabValue                 = null;
    
    String  indicator                 = null;
    String[] chargeBasis              = null;
    String[] effectiveFrom            = null;
    String[] validUpto                = null;
    Timestamp effectiveFromTstmp      = null;
    Timestamp validUptoTstmp          = null;
    ESupplyGlobalParameters loginbean = null;
    ESupplyDateUtility dateFormatter  = new ESupplyDateUtility();
    String dateFormat                 = null;
    
    String  lineNo                    = null;
    String  dLineNo                   = null;
    
    String  densityRatio              = null;
    
    ArrayList slabList                = null;
    
    int     counter                   = 0;
    int         count                 = 0; 
    try
    {
     
      session            =  request.getSession();
      loginbean          =  (ESupplyGlobalParameters)session.getAttribute("loginbean");
      slabList		       =  (ArrayList)session.getAttribute("slabList");      
      masterDOB          =  (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
      
      dateFormat          = loginbean.getUserPreferences().getDateFormat();
      dateFormatter.setPattern(dateFormat);
  //commented by VLAKSHMI for issue 172623 on 040609
     // if(masterDOB!=null)
       // zoneCodes         =  masterDOB.getZoneCodes();
     // else
     //   zoneCodes         =  new String[0];
     //end 
      if("cartageId".equals(cartageIdType))
      {
        checkBoxValues  =  request.getParameterValues("checkBoxValue");
        cartageId       =  request.getParameterValues("cartageId");
        chargeBasis     =  request.getParameterValues("chargeBasis");
        effectiveFrom   =  request.getParameterValues("effectiveFrom");
        validUpto       =  request.getParameterValues("validUpto"); 
        zoneCodes        =  request.getParameterValues("zoneCode"); //Add by VLAKSHMI for issue 172623 on 040609
        
      }
      else
      {
        checkBoxValues  =  request.getParameterValues("dCheckBoxValue");
        cartageId       =  request.getParameterValues("dCartageId");
        chargeBasis     =  request.getParameterValues("delChargeBasis");
        effectiveFrom   =  request.getParameterValues("dEffectiveFrom");
        validUpto       =  request.getParameterValues("dValidUpto");  
        zoneCodes        =  request.getParameterValues("dZoneCode"); //Add by VLAKSHMI for issue 172623 on 040609
      }
      int chkBoxValLen	=	checkBoxValues.length;
      for(int i=0;i<chkBoxValLen;i++)
      {
        if("YES".equalsIgnoreCase(checkBoxValues[i]))
        {
         count = 0;
         int slabListSize	=	slabList.size();
          for(int j=0;j<slabListSize;j++)
          {
             if("cartageId".equals(cartageIdType))
             {
                  densityRatio  = request.getParameter("densityRatio"+i);
                  indicator   =   request.getParameter("indicator"+i+j);
                  
                  if("F".equalsIgnoreCase(indicator))
                    slabValue   =   request.getParameter("flatValue"+i+j);
                  else
                    slabValue   =   request.getParameter("slabValue"+i+j);
                  
                  slabBreak   =   request.getParameter("slabBreak"+i+j);
             
                  lowerBound  =   request.getParameter("lowerBound"+i+j);
                  upperBound  =   request.getParameter("upperBound"+i+j);
                  lineNo      =   request.getParameter("lineNumber"+i+j);
                  chargeType    =   request.getParameter("chargeType"+i);//Add by VLAKSHMI for issue 172623 on 040609
              }
              else
              {
                  densityRatio  = request.getParameter("dDensityRatio"+i);
                  indicator   =   request.getParameter("dIndicator"+i+j);
                  
                  if("F".equalsIgnoreCase(indicator))
                    slabValue   =   request.getParameter("dFlatValue"+i+j);
                  else
                    slabValue   =   request.getParameter("dSlabValue"+i+j);
                    
                  slabBreak   =   request.getParameter("dSlabBreak"+i+j);
                  lowerBound  =   request.getParameter("dLowerBound"+i+j);
                  upperBound  =   request.getParameter("dUpperBound"+i+j);
                  lineNo      =   request.getParameter("dLineNumber"+i+j);
                  chargeType    =   request.getParameter("dChargeType"+i);//Add by VLAKSHMI for issue 172623 on 040609
               }
               if(slabValue!=null && slabValue.trim().length()!=0 && Double.parseDouble(slabValue)!=0)
               {
                  buyDtlDOB    =   new QMSCartageBuyDtlDOB();
                  buyDtlDOB.setDensityRatio(densityRatio);
                  buyDtlDOB.setCartageId(Long.parseLong(cartageId[i]));
                  buyDtlDOB.setZoneCode(zoneCodes[i]);
                  buyDtlDOB.setWeightBreakSlab(slabBreak);
                  buyDtlDOB.setChargeType(chargeType);
                  buyDtlDOB.setLowerBound(lowerBound);
                  buyDtlDOB.setUpperBound(upperBound);
                 // buyDtlDOB.setLineNumber(Integer.parseInt(lineNo));
                 buyDtlDOB.setLineNumber(count);//@@Modified by Kameswari for the WPBN issue-138502
                  buyDtlDOB.setChargeRate(Double.parseDouble(slabValue));
                  buyDtlDOB.setChargeRateIndicator(indicator);
                  buyDtlDOB.setChargeBasis(chargeBasis[i]);
                  
                  if(effectiveFrom!=null)
                  {
                    if(effectiveFrom[i]!=null && effectiveFrom[i].trim().length()!=0)
                        effectiveFromTstmp=dateFormatter.getTimestampWithTime(dateFormat,effectiveFrom[i],"00:00");
                  }
                  if(validUpto!=null)
                  {
                    if(validUpto[i]!=null && validUpto[i].trim().length()!=0)
                      validUptoTstmp =dateFormatter.getTimestampWithTime(dateFormat,validUpto[i],"23:59");
                  }
                  //buyDtlDOB.setEffectiveFrom(effectiveFromTstmp);
                  buyDtlDOB.setEffectiveFrom(new Timestamp((new java.util.Date()).getTime()));//@@Commented and Modified by Kameswari for the WPBN issue-141959
                  
                  buyDtlDOB.setValidUpto(validUptoTstmp);
                  slabBuyChargesList.add(buyDtlDOB);
                  effectiveFromTstmp  = null;
                  validUptoTstmp      = null;
                  count++;
                }
              }
        }
      }
    }
    catch(Exception e)
    {
//      Logger.error(FILE_NAME,"Error While Adding Slab Sell Charges.."+e);
      logger.error(FILE_NAME+"Error While Adding Slab Sell Charges.."+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Sell Charges! Please Try Again",e);
    }
    return slabBuyChargesList;
  }
  private ArrayList doListChargesAddProcess(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean) throws FoursoftException
  {
    QMSCartageMasterDOB masterDOB   = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    ArrayList listBuyCharges = null;
    
    String[]  listValues     = null;
    String[]  dListValues    = null;
    
    
    String  chargeType        = null;
    ArrayList invalidList     = null;
    
    HttpSession session = request.getSession();
    try
    {
        masterDOB = (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
        session.removeAttribute("cartageMaster");
        
        listBuyCharges     =  new ArrayList();
        
        listBuyCharges.add(0,masterDOB);//@@Adding Hdr information
        
        listValues        =  request.getParameterValues("listValues");
        dListValues       =  request.getParameterValues("dListValues");
        
       
        if(dListValues==null)
              chargeType = masterDOB.getChargeType();
            else
              chargeType =  "Pickup";
        
        listBuyCharges = doProcessListCharges(listBuyCharges,masterDOB,listValues,chargeType,request);
        
         if(dListValues!=null)
         {
            chargeType =  "Delivery";
            listBuyCharges = doProcessListCharges(listBuyCharges,masterDOB,dListValues,chargeType,request);  
         }
        
        
       
        home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote  =   (ChargeMasterSession)home.create();
        
        logger.info(FILE_NAME+"before calling insertCartageBuyCharges");  
        
        invalidList = remote.insertCartageBuyCharges(listBuyCharges);
    }
    catch (FoursoftException fs)
    {
      logger.error("Exception in doListChargesAddProcess" + fs);
      fs.printStackTrace();
      throw new FoursoftException(fs.getMessage(),fs);
    }
    catch(Exception e)
    {
      logger.error(FILE_NAME+"Error While Adding List Buy Charges.."+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Buy Charges! Please Try Again.",e);
    }
    return invalidList;
  }
  private ArrayList doProcessListCharges(ArrayList buyChargesList,QMSCartageMasterDOB masterDOB,String[] values,String tempChargeType,HttpServletRequest request) throws FoursoftException
  {
    QMSCartageBuyDtlDOB cartageBuyDtl = null;
    
    String[]  zoneCodes       = null;
    String[]  listTypes       = null;
    
    int counter               = 0;
    int count                 = 0;
    
    try
    {
      zoneCodes = masterDOB.getZoneCodes();
      listTypes = masterDOB.getListValues();
      
      int zoneCodeLen	=	zoneCodes.length	;
      int listTypeLen	=	listTypes.length	;
      for(int i=0;i<zoneCodeLen;i++)
      {    
          for(int j=0;j<listTypeLen;j++)
          {
            cartageBuyDtl = new QMSCartageBuyDtlDOB();
            cartageBuyDtl.setZoneCode(zoneCodes[i]);
            cartageBuyDtl.setWeightBreakSlab(listTypes[j]);
            cartageBuyDtl.setDensityRatio(request.getParameter("densityRatio"+i));
            //cartageBuyDtl.setChargeRate(Double.parseDouble(values[j]));//commented by VLAKSHMI for issue 173789 on 16/06/2009
            cartageBuyDtl.setChargeRate(Double.parseDouble(values[count]));//added by VLAKSHMI for issue 173789 on 16/06/2009
            cartageBuyDtl.setLowerBound("0");
            cartageBuyDtl.setUpperBound("0");
            
            cartageBuyDtl.setChargeType(tempChargeType);
            cartageBuyDtl.setLineNumber(j);
            cartageBuyDtl.setChargeBasis(masterDOB.getChargeBasis());
            cartageBuyDtl.setEffectiveFrom(masterDOB.getEffectiveFrom());
            cartageBuyDtl.setValidUpto(masterDOB.getValidUpto());
            buyChargesList.add(cartageBuyDtl);//@@Adding Slab rates
            cartageBuyDtl = null;
            count++;//added by VLAKSHMI for issue 173789 on 16/06/2009
          }
        }
    }
    catch(Exception e)
    {
      logger.error("Error !!!!!! "+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Processing the Data. Please Try Again!",e);      
    }
    return buyChargesList;
  }
  private void doListBuyChargesModify(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean,QMSCartageMasterDOB masterDOB) throws FoursoftException
  {
      ArrayList             buyChargesList  = null;
      ArrayList             selectedChargesList = new ArrayList();
      QMSCartageBuyDtlDOB   buyDtlDOB       = null;
      QMSCartageBuyDtlDOB   selectedDtlDOB  = null;
      HttpSession           session         = request.getSession();
      String[]              checkBoxValues  = null;
      String[]              dCheckBoxValues = null;
      ArrayList             pickUpList      = null;
      ArrayList             delList         = null;
      ArrayList             slabList        = null;
      
      int                   pickupSize      = 0;
      int                   delSize         = 0;
      int                   noOfSlabs       = 0;
      int                   checkBoxLength  = 0;
      int                   dCheckBoxLength = 0;
      
      ChargeMasterSessionHome home      = null;
      ChargeMasterSession     remote    = null;
      
      try
      {
          home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote  =   (ChargeMasterSession)home.create();
          
          selectedChargesList.add(0,masterDOB);
          buyChargesList    =   (ArrayList)session.getAttribute("buyChargesList");
          if(buyChargesList!=null && buyChargesList.size()>0)
          {
            pickUpList		= (ArrayList)buyChargesList.get(0);
            delList			  = (ArrayList)buyChargesList.get(1);
            slabList		  = (ArrayList)buyChargesList.get(2);      
            
            if(pickUpList!=null)
              pickupSize = pickUpList.size();
            if(delList!=null)
              delSize	   = delList.size();
            if(slabList!=null)
              noOfSlabs  = slabList.size();      
          }
          
          checkBoxValues   =  request.getParameterValues("checkBoxValue");
          dCheckBoxValues  =  request.getParameterValues("dCheckBoxValue");
          
          
          for(int i=0;i<pickupSize;i++)
          {
            if("YES".equalsIgnoreCase(checkBoxValues[i]))
            {
              buyDtlDOB   = (QMSCartageBuyDtlDOB)pickUpList.get(i);
              for(int j=0;j<noOfSlabs;j++)
              {
                selectedDtlDOB  = new QMSCartageBuyDtlDOB();
                selectedDtlDOB.setCartageId(buyDtlDOB.getCartageId());
                selectedDtlDOB.setZoneCode(buyDtlDOB.getZoneCode());
                selectedDtlDOB.setChargeType(buyDtlDOB.getChargeType());
                selectedDtlDOB.setChargeBasis(buyDtlDOB.getChargeBasis());
               // selectedDtlDOB.setEffectiveFrom(buyDtlDOB.getEffectiveFrom());
                selectedDtlDOB.setEffectiveFrom(new Timestamp((new java.util.Date()).getTime()));//@@Commented and Modified by Kameswari for the WPBN issue-141959
                selectedDtlDOB.setValidUpto(buyDtlDOB.getValidUpto());
                selectedDtlDOB.setDensityRatio(buyDtlDOB.getDensityRatio());
                selectedDtlDOB.setChargeRate(Double.parseDouble(request.getParameter("slabValue"+i+j)));
                selectedDtlDOB.setLineNumber(j);
                selectedDtlDOB.setWeightBreakSlab((String)slabList.get(j));
                selectedDtlDOB.setLowerBound("0");
                selectedDtlDOB.setUpperBound("0");
                selectedChargesList.add(selectedDtlDOB);
              }              
            }
          }
          
          for(int i=0;i<delSize;i++)
          {
            if("YES".equalsIgnoreCase(dCheckBoxValues[i]))
            {
              buyDtlDOB   = (QMSCartageBuyDtlDOB)delList.get(i);
              for(int j=0;j<noOfSlabs;j++)
              {
                selectedDtlDOB  = new QMSCartageBuyDtlDOB();
                selectedDtlDOB.setCartageId(buyDtlDOB.getCartageId());
                selectedDtlDOB.setZoneCode(buyDtlDOB.getZoneCode());
                selectedDtlDOB.setChargeType(buyDtlDOB.getChargeType());
                selectedDtlDOB.setChargeBasis(buyDtlDOB.getChargeBasis());
                 // selectedDtlDOB.setEffectiveFrom(buyDtlDOB.getEffectiveFrom());
                selectedDtlDOB.setEffectiveFrom(new Timestamp((new java.util.Date()).getTime()));//@@Commented and Modified by Kameswari for the WPBN issue-141959
                 selectedDtlDOB.setValidUpto(buyDtlDOB.getValidUpto());
                selectedDtlDOB.setDensityRatio(buyDtlDOB.getDensityRatio());
                selectedDtlDOB.setChargeRate(Double.parseDouble(request.getParameter("dSlabValue"+i+j)));
                selectedDtlDOB.setLineNumber(j);
                selectedDtlDOB.setWeightBreakSlab((String)slabList.get(j));
                selectedDtlDOB.setLowerBound("0");
                selectedDtlDOB.setUpperBound("0");
                selectedChargesList.add(selectedDtlDOB);
              }
              
            }
          }
          
          home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote  =   (ChargeMasterSession)home.create();
            
          remote.insertCartageBuyCharges(selectedChargesList);
      }
      catch(Exception e)
      {
        logger.error("Exception while modifying FCL Cartage ::"+e);
        e.printStackTrace();
        throw new FoursoftException("An Error Has Occurred While Modifying FCL Cartage Charges. Please Try Again!");
      }
  }
  private QMSCartageMasterDOB doGetSellChargesMaster(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean)  throws FoursoftException
  {
    String  locationId              = null;
    String[]  zoneCode              = null;
    String[]vendorIds               = null;
    String  currencyId              = null;
    String  chargeType              = null;
    String  chargeBasis             = null;
    String  unitofMeasure           = null;
    String  weightBreak             = null;
    String  rateType                = null;
    String  shipmentMode            = null;
    String  consoleType             = null;
    
    ArrayList list                  = null;
    
    String accessLevel              = "";
    
    QMSCartageMasterDOB cartageMaster = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    try
    {
      cartageMaster       = new QMSCartageMasterDOB();
      
      locationId          = request.getParameter("locationId");
      zoneCode            = request.getParameterValues("zoneCodes");
      //vendorIds           = request.getParameterValues("vendorIds");
      currencyId          = request.getParameter("baseCurrency");
      chargeType          = request.getParameter("chargeType");
      //chargeBasis         = request.getParameter("chargeBasis");
      //unitofMeasure       = request.getParameter("uom");
      weightBreak         = request.getParameter("weightBreak");
      rateType            = request.getParameter("rateType");
      shipmentMode        = request.getParameter("shipmentMode");
      consoleType         = request.getParameter("consoleType");
      
      if("HO_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "H";
      else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "A";
      else
          accessLevel = "O";
   
      cartageMaster.setLocationId(locationId);
      cartageMaster.setZoneCodes(zoneCode);
      //cartageMaster.setVendorIds(vendorIds);
      cartageMaster.setCurrencyId(currencyId);
      cartageMaster.setChargeType(chargeType);
      //cartageMaster.setChargeBasis(chargeBasis);
      //cartageMaster.setUom(unitofMeasure);
      cartageMaster.setWeightBreak(weightBreak);
      cartageMaster.setRateType(rateType);
      cartageMaster.setAccessLevel(accessLevel);
      cartageMaster.setTerminalId(loginbean.getTerminalId());
      cartageMaster.setCreatedBy(loginbean.getUserId());
      cartageMaster.setShipmentMode(shipmentMode);
      cartageMaster.setConsoleType(consoleType);
      cartageMaster.setCreatedTimestamp(new java.sql.Timestamp(new java.util.Date().getTime()));
      cartageMaster.setOperation(request.getParameter("Operation"));
    }
    catch(Exception e)
    {
//      Logger.error(FILE_NAME,"Error in search Process"+e);
      logger.error(FILE_NAME+"Error in search Process"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error has Occurred in the Search Process. Please Try Again",e);
    }
    return cartageMaster;
    
  }
  private ArrayList doSearchProcess(QMSCartageMasterDOB cartageMaster,String weightBreak,String operation,HttpServletRequest request)  throws FoursoftException
  {
    
    ArrayList list                    = null;
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    try
    {      
      ESupplyGlobalParameters loginbean = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
      home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
      remote  =   (ChargeMasterSession)home.create();
      cartageMaster.setOperation(operation);
      cartageMaster.setTerminalId(loginbean.getTerminalId());
      if("Flat".equals(weightBreak))
        list    =   remote.getBuyCartageChargesFlat(cartageMaster);
      else if("Slab".equalsIgnoreCase(weightBreak))
        list    =   remote.getBuyCartageChargesSlab(cartageMaster);
      else
        list    =   remote.getBuyCartageChargesList(cartageMaster);
    }
    catch(Exception e)
    {
//      Logger.error(FILE_NAME,"Error in search Process"+e);
      logger.error(FILE_NAME+"Error in search Process"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error has Occurred in the Search Process. Please Try Again",e);
    }
    return list;
  }
  
  private void doFlatSellChargeAddProcess(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean,String operation) throws FoursoftException
  {
    
    QMSCartageSellDtlDOB cartageSellDtl         = null;
    QMSCartageSellDtlDOB cartageSellMaxDtl      = null;
    QMSCartageSellDtlDOB cartageDlvrySellDtl    = null;
    QMSCartageSellDtlDOB cartageDlvrySellMaxDtl = null;
    QMSCartageMasterDOB  masterDOB              = null;
    ArrayList            flatSellChargesList    = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    String[]  zoneCodes               = null;
    String    uom                     = null;
    String    currencyId              = null;
    String    chargeType              = null;
    
    String    marginType              = null;
    String    overAllMargin           = null;
    
    String[]  cartageId               = null;
    String[]  pickupCartageId         = null;
    String[]  delCartageId            = null;
    String[]  checkBoxValue           = null;
    String[]  baseRate                = null;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    String[]  minRate                 = null;
    String[]  flatRate                = null;
    String[]  maxRate                 = null;
    
    String[]  dBaseRate                = null;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    String[]  dMinRate                = null;
    String[]  dFlatRate               = null;
    String[]  dMaxRate                = null;
    
    String[]  chargeBasis             = null;
    String[]  dChargeBasis            = null;
    String[]  effectiveFrom           = null;
    String[]  validUpto               = null;
    
    String[]  dEffectiveFrom          = null;
    String[]  dValidUpto              = null;
    
    Timestamp effFromTstmp            = null;
    Timestamp validUptoTstmp          = null;

    String  baseMargin                 = null;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    String  minMargin                 = null;
    String  flatMargin                = null;
    String  maxMargin                 = null;
    
    String  dBaseMargin               = null;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    String  dMinMargin                = null;
    String  dFlatMargin               = null;
    String  dMaxMargin                = null;
    
    String  marginPercent             = null;
    
    double  baseRateDouble            = 0;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    double  minRateDouble             = 0;
    double  flatRateDouble            = 0;
    double  maxRateDouble             = 0;
    
    double  baseMarginDouble            = 0;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    double  minMarginDouble           = 0;
    double  flatMarginDouble          = 0;
    double  maxMarginDouble           = 0;
    
    double  marginPercentage          = 0;
    
    int     counter                   = 0;
    
    String chargeSlab                 = "";
    double marginDouble               = 0;
    double chargeRate                 = 0;
    double buyChargeAmount            = 0;
    String locationId                 = null;
    //Logger.info(FILE_NAME,"Inside doFlatSellChargeAddProcess::");
    logger.info(FILE_NAME+"Inside doFlatSellChargeAddProcess::");
    
    ESupplyDateUtility eSupplyDateUtility=new ESupplyDateUtility();
    String dateFormat     =null;
    HttpSession        session        = request.getSession();
    
    try
    {
      dateFormat       =   loginbean.getUserPreferences().getDateFormat();
      eSupplyDateUtility.setPatternWithTime(dateFormat);
      masterDOB        =   (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
      flatSellChargesList = new ArrayList();
      zoneCodes        =   request.getParameterValues("zoneCode");
      //chargeType      =   request.getParameter("chargeType");
      marginType       =   request.getParameter("marginType");
      overAllMargin    =   request.getParameter("overAllMargin");
      //uom             =   request.getParameter("uom");
      //currencyId      =   request.getParameter("baseCurrencyTemp");
      //locationId      =   request.getParameter("locationId");//added by rk
      checkBoxValue    =   request.getParameterValues("checkBoxValue");
      cartageId        =   request.getParameterValues("cartageId");

      delCartageId     =   request.getParameterValues("dCartageId");
      
      baseRate          =   request.getParameterValues("baseRate");//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
      minRate          =   request.getParameterValues("minRate");
      flatRate         =   request.getParameterValues("flatRate");
      maxRate          =   request.getParameterValues("maxRate");
      
      dBaseRate        =   request.getParameterValues("dBaseRate");//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
      dMinRate         =   request.getParameterValues("dMinRate");
      dFlatRate        =   request.getParameterValues("dFlatRate");
      dMaxRate         =   request.getParameterValues("dMaxRate");
      
      chargeBasis      =   request.getParameterValues("chargeBasis");
      dChargeBasis     =   request.getParameterValues("dChargeBasis");
      
      effectiveFrom    =   request.getParameterValues("effectiveFrom");
      validUpto        =   request.getParameterValues("validUpto");
      
      dEffectiveFrom   =   request.getParameterValues("dEffectiveFrom");
      dValidUpto       =   request.getParameterValues("dValidUpto");
      
      baseMargin      =   request.getParameter("baseMargin");//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
      minMargin       =   request.getParameter("minMargin");
      flatMargin      =   request.getParameter("flatMargin");
      maxMargin       =   request.getParameter("maxMargin");
      
      marginPercent   =   request.getParameter("marginPercent");
      
      dBaseMargin      =   request.getParameter("dBaseMargin");//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
      dMinMargin      =   request.getParameter("dMinMargin");
      dFlatMargin     =   request.getParameter("dFlatMargin");
      dMaxMargin      =   request.getParameter("dMaxMargin");
      
      locationId      =   masterDOB.getLocationId();
      currencyId      =   masterDOB.getCurrencyId();
      chargeType      =   masterDOB.getChargeType();
      
      String zoneCode ="";
      int chkBoxValLen	=	checkBoxValue.length;
      for(int i=0;i<chkBoxValLen;i++)
      {
        if("YES".equals(checkBoxValue[i]))
        {
          zoneCode = zoneCodes[i];
          //System.out.println("zoneCodes[j]  1  "+zoneCode);
          if(!"-".equals(cartageId[i]))
          {
            if(("No".equals(overAllMargin)) || ("Yes".equals(overAllMargin) && "Absolute".equals(marginType)))
            {
                /*if("0.0".equals(maxRate[i]) && "".equals(maxMargin))
                  //counter   = 2;//@@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09
                  counter   = 3;//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
                else
                  counter   = 3;
                   //counter   = 3;//@@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09
                  counter   = 4;//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
                  */
        //@@ Added by subrahmanyam for the enhancement 170759        
              if( Double.parseDouble(baseRate[i])!=0.0)
              {
                  if("0.0".equals(maxRate[i]) && "".equals(maxMargin))
                  counter   = 3;
                  else 
                  counter   = 4;
              }
              else
              {
                if("0.0".equals(maxRate[i]) && "".equals(maxMargin))
                  counter   = 2;
                  else 
                  counter   = 3;
              }
      //@@ Ended by subrahmanyam for the enhancement 170759                  
            }
            else//@@This condition is for "Yes & Percentage"
            {
//@@ COMMENTED BY SUBRAHMANYAM FOR 170759            
              /*
               if("0.0".equals(maxRate[i]))
                counter   = 2;
              else
                counter   = 3;
                */
//@@ ADDED BY SUBRAHMANYAM FOR 170759       
            if(Double.parseDouble(baseRate[i])!=0.0)
            {
                if("0.0".equals(maxRate[i]))
                counter   = 3;
              else
                counter   = 4;
            }
            else
            {
               if("0.0".equals(maxRate[i]))
                counter   = 2;
              else
                counter   = 3;
              }
            }
//@@ ENDED FOR 170759            
            for(int j=0;j<counter;j++)
            {
              cartageSellDtl    =   new QMSCartageSellDtlDOB();
              cartageSellDtl.setCartageId(Long.parseLong(cartageId[i]));
              cartageSellDtl.setTerminalId(loginbean.getTerminalId());
              cartageSellDtl.setOperation(operation);
              cartageSellDtl.setZoneCode(zoneCode);
              cartageSellDtl.setUpperBound("0");
              cartageSellDtl.setLowerBound("0");
              cartageSellDtl.setMarginType("Absolute".equals(marginType)?"A":"P");
              cartageSellDtl.setOverallMargin(overAllMargin);
              //cartageSellDtl.setUom(uom);
              cartageSellDtl.setCurrencyId(currencyId);
              cartageSellDtl.setChargeBasis(chargeBasis[i]);
              if(effectiveFrom!=null)
              {
                if(effectiveFrom[i]!=null && effectiveFrom[i].trim().length()!=0)
                    effFromTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,effectiveFrom[i],"00:00");
              }
              if(validUpto!=null)
              {
                if(validUpto[i]!=null && validUpto[i].trim().length()!=0)
                  validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,validUpto[i],"23:59");
              }
              
              if(delCartageId==null)
                cartageSellDtl.setChargeType(chargeType);
              else
                cartageSellDtl.setChargeType("Pickup");
              cartageSellDtl.setLocationId(locationId);
              
              cartageSellDtl.setEffectiveFrom(effFromTstmp);
              cartageSellDtl.setValidUpto(validUptoTstmp);
              effFromTstmp       = null;
              validUptoTstmp     = null;
              baseRateDouble   = Double.parseDouble(baseRate[i]); //@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
              minRateDouble   = Double.parseDouble(minRate[i]);            
              flatRateDouble  = Double.parseDouble(flatRate[i]);            
              maxRateDouble   = Double.parseDouble(maxRate[i]);
              
              
              if(("No".equals(overAllMargin)) || ("Yes".equals(overAllMargin) && "Absolute".equals(marginType)))
              {
               //@@ Commented by subrahmanyam for the Enhancement 170759 on 02/06/09  
               /*
                *  if(j==0)
                 {
                   minMarginDouble = Double.parseDouble(minMargin);
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(minRateDouble+minMarginDouble):((minRateDouble*minMarginDouble)/100+minRateDouble);
                   buyChargeAmount= minRateDouble;
                   cartageSellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   flatMarginDouble= Double.parseDouble(flatMargin);
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(flatRateDouble+flatMarginDouble):((flatRateDouble*flatMarginDouble)/100+flatRateDouble);
                   buyChargeAmount= flatRateDouble;
                   cartageSellDtl.setLineNumber(1);
                 }
                 else
                 {
                   maxMarginDouble = Double.parseDouble(maxMargin);
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(maxRateDouble+maxMarginDouble):((maxRateDouble*maxMarginDouble)/100+maxRateDouble);
                   buyChargeAmount= maxRateDouble;
                   cartageSellDtl.setLineNumber(2);
                
               }
                */
 //@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09               
               if(baseRateDouble==0.0)
               {
               if(j==0)
                 {
                   minMarginDouble = Double.parseDouble(minMargin);
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(minRateDouble+minMarginDouble):((minRateDouble*minMarginDouble)/100+minRateDouble);
                   buyChargeAmount= minRateDouble;
                   cartageSellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   flatMarginDouble= Double.parseDouble(flatMargin);
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(flatRateDouble+flatMarginDouble):((flatRateDouble*flatMarginDouble)/100+flatRateDouble);
                   buyChargeAmount= flatRateDouble;
                   cartageSellDtl.setLineNumber(1);
                 }
                 else
                 {
                  if(maxMargin==null || "".equals(maxMargin))
                    maxMargin="0.0";
                   maxMarginDouble = Double.parseDouble(maxMargin);
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(maxRateDouble+maxMarginDouble):((maxRateDouble*maxMarginDouble)/100+maxRateDouble);
                   buyChargeAmount= maxRateDouble;
                   cartageSellDtl.setLineNumber(2);
                
               }
               }

          else
              {
              if(j==0)
                 {
                  if(baseMargin.equals(""))
                      baseMargin="0.0";
                   baseMarginDouble = Double.parseDouble(baseMargin);
                   chargeSlab     = "BASE";
                   marginDouble   = baseMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(baseRateDouble+baseMarginDouble):((baseRateDouble*baseMarginDouble)/100+baseRateDouble);
                   buyChargeAmount= baseRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
                 else if(j==1)
                 {
                   minMarginDouble = Double.parseDouble(minMargin);
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(minRateDouble+minMarginDouble):((minRateDouble*minMarginDouble)/100+minRateDouble);
                   buyChargeAmount= minRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
                 else if(j==2)
                 {
                   flatMarginDouble= Double.parseDouble(flatMargin);
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(flatRateDouble+flatMarginDouble):((flatRateDouble*flatMarginDouble)/100+flatRateDouble);
                   buyChargeAmount= flatRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
                 else
                 {
                  if(maxMargin==null)
                  maxMargin="0.0";
                   maxMarginDouble = Double.parseDouble(maxMargin);
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(maxRateDouble+maxMarginDouble):((maxRateDouble*maxMarginDouble)/100+maxRateDouble);
                   buyChargeAmount= maxRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
              }
//Ended by subrahmanyam for the Enhancement 170759 on 02/06/09  
                 cartageSellDtl.setChargeSlab(chargeSlab);
                 cartageSellDtl.setMargin(marginDouble);
                 cartageSellDtl.setBuyChargeAmount(buyChargeAmount);
                 cartageSellDtl.setChargeRate(chargeRate);               
              }
              else//@@This condition is for "Yes & Percentage"
              {
                marginPercentage  = Double.parseDouble(request.getParameter("marginPercent"));
                
              //@@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09                
               /* if(j==0)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (minRateDouble*marginPercentage)/100+minRateDouble;
                   buyChargeAmount= minRateDouble;
                   cartageSellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (flatRateDouble*marginPercentage)/100+flatRateDouble;
                   buyChargeAmount= flatRateDouble;
                   cartageSellDtl.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (maxRateDouble*marginPercentage)/100+maxRateDouble;
                   buyChargeAmount= maxRateDouble;
                   cartageSellDtl.setLineNumber(2);
                 }
                */
//@@Added by subrahmanyam for the Enhancement on 02/06/09
              if(baseRateDouble==0.0)
              {
                if(j==0)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (minRateDouble*marginPercentage)/100+minRateDouble;
                   buyChargeAmount= minRateDouble;
                   cartageSellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (flatRateDouble*marginPercentage)/100+flatRateDouble;
                   buyChargeAmount= flatRateDouble;
                   cartageSellDtl.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (maxRateDouble*marginPercentage)/100+maxRateDouble;
                   buyChargeAmount= maxRateDouble;
                   cartageSellDtl.setLineNumber(2);
                 }
              }
              else
              {
                if(j==0)
                 {
                   chargeSlab     = "BASE";
                   chargeRate     = (baseRateDouble*marginPercentage)/100+baseRateDouble;
                   buyChargeAmount= baseRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
                else if(j==1)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (minRateDouble*marginPercentage)/100+minRateDouble;
                   buyChargeAmount= minRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
                 else if(j==2)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (flatRateDouble*marginPercentage)/100+flatRateDouble;
                   buyChargeAmount= flatRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (maxRateDouble*marginPercentage)/100+maxRateDouble;
                   buyChargeAmount= maxRateDouble;
                   cartageSellDtl.setLineNumber(j);
                 }
              }
//@@ Ended by subrahmanyam for the Enhancement on 02/06/09
                
                cartageSellDtl.setChargeSlab(chargeSlab);
                cartageSellDtl.setMargin(marginPercentage);
                cartageSellDtl.setBuyChargeAmount(buyChargeAmount);
                cartageSellDtl.setChargeRate(chargeRate);
              }
              
              //Logger.info(FILE_NAME,"cartageSellDtl.getChargeRate::"+cartageSellDtl.getChargeRate());
              logger.info(FILE_NAME+"cartageSellDtl.getChargeRate::"+cartageSellDtl.getChargeRate());
              
              flatSellChargesList.add(cartageSellDtl);
            }
          }
          
          if("Both".equals(chargeType) && !"-".equals(delCartageId[i]))
          {
            counter = 0;
            if(("No".equals(overAllMargin)) || ("Yes".equals(overAllMargin) && "Absolute".equals(marginType)))
            {
                //@@ Commented by subrahmanyam for the Enhancement 170759 on 02/06/09            
                /*if("0.0".equals(dMaxRate[i]) && "".equals(dMaxMargin))
                  counter   = 2;
                else
                  counter   = 3;
                  */
//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09                  
              if("0.0".equals(dMaxRate[i]) && "".equals(dMaxMargin))
                  counter   = 3;
                else
                  counter   = 4;
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09    
            }
            else//@@This condition is for "Yes & Percentage"
            {
              //@@ Commented by subrahmanyam for the Enhancement 170759 on 02/06/09                        
              /*
               if("0.0".equals(dMaxRate[i]))
                counter   = 2;
              else
                counter   = 3;
            */                
//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09             
            if("0.0".equals(dMaxRate[i]))
                counter   = 3;
              else
                counter   = 4;
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09   
            }
              
            baseRateDouble   = Double.parseDouble(dBaseRate[i]);     //@@ Added by subrahmanyam for  170759       
            minRateDouble   = Double.parseDouble(dMinRate[i]);            
            flatRateDouble  = Double.parseDouble(dFlatRate[i]);            
            maxRateDouble   = Double.parseDouble(dMaxRate[i]);
            
            
            for(int j=0;j<counter;j++)
            {
              cartageDlvrySellDtl = new QMSCartageSellDtlDOB();
              cartageDlvrySellDtl.setCartageId(Long.parseLong(delCartageId[i]));
              cartageSellDtl.setTerminalId(loginbean.getTerminalId());
              cartageSellDtl.setOperation(operation);
              cartageDlvrySellDtl.setZoneCode(zoneCode);
              cartageDlvrySellDtl.setUpperBound("0");
              cartageDlvrySellDtl.setLowerBound("0");
              cartageDlvrySellDtl.setMarginType("Absolute".equals(marginType)?"A":"P");
              cartageDlvrySellDtl.setOverallMargin(overAllMargin);
              cartageDlvrySellDtl.setUom(uom);
              cartageDlvrySellDtl.setCurrencyId(currencyId);
              cartageDlvrySellDtl.setChargeType("Delivery");
              cartageDlvrySellDtl.setLocationId(locationId);
              
              cartageDlvrySellDtl.setChargeBasis(dChargeBasis[i]);
              if(dEffectiveFrom!=null)
              {
                if(dEffectiveFrom[i]!=null && dEffectiveFrom[i].trim().length()!=0)
                    effFromTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,dEffectiveFrom[i],"00:00");
              }
              if(dValidUpto!=null)
              {
                if(dValidUpto[i]!=null && dValidUpto[i].trim().length()!=0)
                  validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,dValidUpto[i],"23:59");
              }
              cartageDlvrySellDtl.setEffectiveFrom(effFromTstmp);
              cartageDlvrySellDtl.setValidUpto(validUptoTstmp);
              
              effFromTstmp    =   null;
              validUptoTstmp  =   null;
              
              if(("No".equals(overAllMargin)) || ("Yes".equals(overAllMargin) && "Absolute".equals(marginType)))
              {
          //@@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09
                /*if(j==0)
                 {
                   minMarginDouble = Double.parseDouble(dMinMargin);
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(minRateDouble+minMarginDouble):((minRateDouble*minMarginDouble)/100+minRateDouble);
                   buyChargeAmount= minRateDouble;
                   cartageDlvrySellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   flatMarginDouble= Double.parseDouble(dFlatMargin);
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(flatRateDouble+flatMarginDouble):((flatRateDouble*flatMarginDouble)/100+flatRateDouble);
                   buyChargeAmount= flatRateDouble;
                   cartageDlvrySellDtl.setLineNumber(1);
                 }
                 else
                 {
                   maxMarginDouble = Double.parseDouble(dMaxMargin);
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(maxRateDouble+maxMarginDouble):((maxRateDouble*maxMarginDouble)/100+maxRateDouble);
                   buyChargeAmount= maxRateDouble;
                   cartageDlvrySellDtl.setLineNumber(2);
                 }*/
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
              if(baseRateDouble==0.0)
              {
                if(j==0)
                 {
                   minMarginDouble = Double.parseDouble(dMinMargin);
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(minRateDouble+minMarginDouble):((minRateDouble*minMarginDouble)/100+minRateDouble);
                   buyChargeAmount= minRateDouble;
                   cartageDlvrySellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   flatMarginDouble= Double.parseDouble(dFlatMargin);
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(flatRateDouble+flatMarginDouble):((flatRateDouble*flatMarginDouble)/100+flatRateDouble);
                   buyChargeAmount= flatRateDouble;
                   cartageDlvrySellDtl.setLineNumber(1);
                 }
                 else
                 {
                   maxMarginDouble = Double.parseDouble(dMaxMargin);
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(maxRateDouble+maxMarginDouble):((maxRateDouble*maxMarginDouble)/100+maxRateDouble);
                   buyChargeAmount= maxRateDouble;
                   cartageDlvrySellDtl.setLineNumber(2);
                 }
              }
              else
              {
                 if(j==0)
                 {
                  if(dBaseMargin.equals(""))
                    dBaseMargin="0.0";
                   baseMarginDouble = Double.parseDouble(dBaseMargin);
                   chargeSlab     = "BASE";
                   marginDouble   = baseMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(baseRateDouble+baseMarginDouble):((baseRateDouble*baseMarginDouble)/100+baseRateDouble);
                   buyChargeAmount= baseRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
                else if(j==1)
                 {
                   minMarginDouble = Double.parseDouble(dMinMargin);
                   chargeSlab     = "MIN";
                   marginDouble   = minMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(minRateDouble+minMarginDouble):((minRateDouble*minMarginDouble)/100+minRateDouble);
                   buyChargeAmount= minRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
                 else if(j==2)
                 {
                   flatMarginDouble= Double.parseDouble(dFlatMargin);
                   chargeSlab     = "FLAT";
                   marginDouble   = flatMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(flatRateDouble+flatMarginDouble):((flatRateDouble*flatMarginDouble)/100+flatRateDouble);
                   buyChargeAmount= flatRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
                 else
                 {
                   maxMarginDouble = Double.parseDouble(dMaxMargin);
                   chargeSlab     = "MAX";
                   marginDouble   = maxMarginDouble;
                   chargeRate     = "Absolute".equals(marginType)?(maxRateDouble+maxMarginDouble):((maxRateDouble*maxMarginDouble)/100+maxRateDouble);
                   buyChargeAmount= maxRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
              }
//@@Ended by subrahmanyam for the Enhancement 170759 on 02/06/09    
                //cartageDlvrySellDtl.setMinMargin(!"".equals(dMinMargin)?Double.parseDouble(dMinMargin):0);
                //cartageDlvrySellDtl.setFlatMargin(!"".equals(dFlatMargin)?Double.parseDouble(dFlatMargin):0);
                //cartageDlvrySellDtl.setMaxMargin(!"".equals(dMaxMargin)?Double.parseDouble(dMaxMargin):0);
                
                  cartageDlvrySellDtl.setChargeSlab(chargeSlab);
                  cartageDlvrySellDtl.setMargin(marginDouble);
                  cartageDlvrySellDtl.setChargeRate(chargeRate);
                  cartageDlvrySellDtl.setBuyChargeAmount(buyChargeAmount);
              }
              else
              {
                marginPercentage  = Double.parseDouble(request.getParameter("marginPercent"));
                
               //@@Commented by subrahmanyam for the enhancement 170759 on 02/06/09                
            /*    
                if(j==0)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (minRateDouble*marginPercentage)/100+minRateDouble;
                   buyChargeAmount= minRateDouble;
                   cartageDlvrySellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (flatRateDouble*marginPercentage)/100+flatRateDouble;
                   buyChargeAmount= flatRateDouble;
                   cartageDlvrySellDtl.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (maxRateDouble*marginPercentage)/100+maxRateDouble;
                   buyChargeAmount= maxRateDouble;
                   cartageDlvrySellDtl.setLineNumber(2);
                 }
               */ 
//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09  
            if(baseRateDouble==0.0)
            {
               if(j==0)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (minRateDouble*marginPercentage)/100+minRateDouble;
                   buyChargeAmount= minRateDouble;
                   cartageDlvrySellDtl.setLineNumber(0);
                 }
                 else if(j==1)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (flatRateDouble*marginPercentage)/100+flatRateDouble;
                   buyChargeAmount= flatRateDouble;
                   cartageDlvrySellDtl.setLineNumber(1);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (maxRateDouble*marginPercentage)/100+maxRateDouble;
                   buyChargeAmount= maxRateDouble;
                   cartageDlvrySellDtl.setLineNumber(2);
                 }
            }
            else
            {
                if(j==0)
                 {
                   chargeSlab     = "BASE";
                   chargeRate     = (baseRateDouble*marginPercentage)/100+baseRateDouble;
                   buyChargeAmount= baseRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
                else if(j==1)
                 {
                   chargeSlab     = "MIN";
                   chargeRate     = (minRateDouble*marginPercentage)/100+minRateDouble;
                   buyChargeAmount= minRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
                 else if(j==2)
                 {
                   chargeSlab     = "FLAT";
                   chargeRate     = (flatRateDouble*marginPercentage)/100+flatRateDouble;
                   buyChargeAmount= flatRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
                 else
                 {
                   chargeSlab     = "MAX";
                   chargeRate     = (maxRateDouble*marginPercentage)/100+maxRateDouble;
                   buyChargeAmount= maxRateDouble;
                   cartageDlvrySellDtl.setLineNumber(j);
                 }
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09   
                
                cartageDlvrySellDtl.setChargeSlab(chargeSlab);
                cartageDlvrySellDtl.setMargin(marginPercentage);
                cartageDlvrySellDtl.setBuyChargeAmount(buyChargeAmount);
                cartageDlvrySellDtl.setChargeRate(chargeRate);
                //cartageDlvrySellDtl.setMargin(Double.parseDouble(request.getParameter("marginPercent")));
              }
              }
              //Logger.info(FILE_NAME,"cartageSellDtl.getChargeRate::"+cartageSellDtl.getChargeRate());
              logger.info(FILE_NAME+"cartageSellDtl.getChargeRate::"+cartageSellDtl.getChargeRate());
              flatSellChargesList.add(cartageDlvrySellDtl);
            }
          }
        }
      }
      flatSellChargesList.add(masterDOB);
      home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
      remote  =   (ChargeMasterSession)home.create();
      
      remote.insertCartageSellCharges(flatSellChargesList);
      
      logger.info(FILE_NAME+"flatSellChargesList::"+flatSellChargesList.size());
    }
    catch(Exception e)
    {
//      Logger.error(FILE_NAME,"Error While Adding Flat Sell Charges.."+e);
      logger.error(FILE_NAME+"Error While Adding Flat Sell Charges.."+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Sell Charges! Please Try Again",e);
    }
  }
  private void doSlabSellChargesAddProcess(HttpServletRequest request, HttpServletResponse response, ESupplyGlobalParameters loginbean) throws FoursoftException
  {
    
    ArrayList           slabSellChargesList  = null;
    QMSCartageMasterDOB masterDOB            = null;
    
    ChargeMasterSessionHome home      = null;
    ChargeMasterSession     remote    = null;
    
    String    chargeType              = null;
    String    chargeTypeTemp          = null;
    String[]  cartageId               = null;
    String[]  delCartageId            = null;
    HttpSession session               = request.getSession();
    
    try
    {
      masterDOB       =   (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
      
      slabSellChargesList           = new ArrayList();
      
      chargeType      =   request.getParameter("chargeType");
  
      cartageId       =  request.getParameterValues("cartageId");
      delCartageId    =  request.getParameterValues("dCartageId"); 
      
     
     
     
      if(delCartageId==null)
          chargeTypeTemp  = chargeType;
      else
          chargeTypeTemp  = "Pickup";
     
      if(cartageId!=null)
        slabSellChargesList = doSlabSellChargesAddtoList(request,slabSellChargesList,chargeTypeTemp,"cartageId");
      
      if(delCartageId!=null)
      {
          chargeTypeTemp  = "Delivery";
          slabSellChargesList = doSlabSellChargesAddtoList(request,slabSellChargesList,chargeTypeTemp,"dCartageId");
      }
      
      slabSellChargesList.add(masterDOB);
      
      //Logger.info(FILE_NAME,"slabSellChargesList::"+slabSellChargesList.size());
      logger.info(FILE_NAME+"slabSellChargesList::"+slabSellChargesList.size());
      
       home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
       remote  =   (ChargeMasterSession)home.create();
      
      remote.insertCartageSellCharges(slabSellChargesList);
    }
    catch(Exception e)
    {
//      Logger.error(FILE_NAME,"Error While Adding Slab Sell Charges.."+e);
      logger.error(FILE_NAME+"Error While Adding Slab Sell Charges.."+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Sell Charges! Please Try Again",e);
    }
  }
  
  private ArrayList doSlabSellChargesAddtoList(HttpServletRequest request,ArrayList slabSellChargesList,String chargeType,String cartageIdType) throws FoursoftException
  {
    QMSCartageSellDtlDOB cartageSellDtl         = null;
    QMSCartageMasterDOB  masterDOB              = null;
    
    QMSCartageSellDtlDOB cartageSellDtlMargin   = null;
    
    String    locationId              = null;  
    String[]  zoneCodes               = null;
    String    uom                     = null;
    String    currencyId              = null;
    
    String    marginType              = null;
    String    overAllMargin           = null;
    
    String[]  cartageId               = null;
    String[]  checkBoxValues          = null;
    String[]  marginValues            = null;
    
    String[]  chargeBasis             = null;
    String[]  effectiveFrom           = null;
    String[]  validUpto               = null;
    
    
    String  lowerBound                = null;
    String  upperBound                = null;
    
    String  slabBreak                 = null;
    String  slabValue                 = null;
    String  rateIndicator             = null;
    
    String  baseMargin                = null;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
    String  minMargin                 = null;
    String  slabMargin                = null;
    String  maxMargin                 = null;
    
    String  marginPercent             = null;
    String  lineNo                    = null;
    String  dLineNo                   = null;
    String  dateFormat                = null;
    Timestamp effFromTstmp            = null;
    Timestamp validUptoTstmp          = null;
    ESupplyGlobalParameters loginbean           = null;
    ESupplyDateUtility      eSupplyDateUtility  = new ESupplyDateUtility();
    int count = 0;
    
    
    HttpSession  session              = request.getSession();
    try
    {
      loginbean       =   (ESupplyGlobalParameters)session.getAttribute("loginbean");
      masterDOB       =   (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
      dateFormat      =   loginbean.getUserPreferences().getDateFormat();      
      eSupplyDateUtility.setPatternWithTime(dateFormat);      
      locationId      =   masterDOB.getLocationId();
      zoneCodes       =   request.getParameterValues("zoneCode");
      marginType      =   request.getParameter("marginType");
      overAllMargin   =   request.getParameter("overAllMargin");
      //uom             =   request.getParameter("uom");
      currencyId      =   masterDOB.getCurrencyId();
      
      baseMargin       =   request.getParameter("baseMargin");//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
      minMargin       =   request.getParameter("minMargin");
      slabMargin      =   request.getParameter("slabMargin");
      maxMargin       =   request.getParameter("maxMargin");
      
      if("cartageId".equals(cartageIdType))
      {
        checkBoxValues  =  request.getParameterValues("checkBoxValue");
        cartageId       =  request.getParameterValues("cartageId");
        marginValues    =  request.getParameterValues("marginValues");
        chargeBasis     =  request.getParameterValues("chargeBasis");
        effectiveFrom   =  request.getParameterValues("effectiveFrom");
        validUpto       =  request.getParameterValues("validUpto");
      }
      else
      {
        checkBoxValues  =  request.getParameterValues("dCheckBoxValue");
        cartageId       =  request.getParameterValues("dCartageId");
        marginValues    =  request.getParameterValues("dMarginValues");
        chargeBasis     =  request.getParameterValues("dChargeBasis");
        effectiveFrom   =  request.getParameterValues("dEffectiveFrom");
        validUpto       =  request.getParameterValues("dValidUpto");
      }
      String  zoneCode="";
      int chkBoxValLen	=	checkBoxValues.length;
      
      for(int i=0;i<chkBoxValLen;i++)
      {
        if("YES".equals(checkBoxValues[i]))
        {
          zoneCode  = zoneCodes[i];
          count=0;//added for issue 172884 by VLAKSHMI on 8/06/2009
          int marginValLen	= marginValues.length;
          for(int j=0;j<marginValLen;j++)
          {
             if("cartageId".equals(cartageIdType))
             {
                slabValue     =   request.getParameter("slabValue"+i+j);
                slabBreak     =   request.getParameter("slabBreak"+i+j);
                lowerBound    =   request.getParameter("lowerBound"+i+j);
                upperBound    =   request.getParameter("upperBound"+i+j);
                lineNo        =   request.getParameter("lineNumber"+i+j);
                chargeType    =   request.getParameter("chargeType"+i);
                rateIndicator =   request.getParameter("rateInd"+i+j);
              }
              else
              {
                slabValue     =   request.getParameter("dSlabValue"+i+j);
                slabBreak     =   request.getParameter("dSlabBreak"+i+j);
                lowerBound    =   request.getParameter("dLowerBound"+i+j);
                upperBound    =   request.getParameter("dUpperBound"+i+j);
                lineNo        =   request.getParameter("dLineNumber"+i+j);
                chargeType    =   request.getParameter("dChargeType"+i);
                rateIndicator =   request.getParameter("dRateInd"+i+j);
               }
             
               if(!"-".equals(slabValue))
               {
                  cartageSellDtl    =   new QMSCartageSellDtlDOB();
                  cartageSellDtl.setCartageId(Long.parseLong(cartageId[i]));
                  cartageSellDtl.setLocationId(locationId);
                  cartageSellDtl.setZoneCode(zoneCode);        
                  cartageSellDtl.setMarginType("Absolute".equals(marginType)?"A":"P");
                  cartageSellDtl.setOverallMargin(overAllMargin);
                  cartageSellDtl.setUom(uom);
                  cartageSellDtl.setCurrencyId(currencyId);
                  cartageSellDtl.setChargeSlab(slabBreak);
                  cartageSellDtl.setChargeType(chargeType);
                  cartageSellDtl.setLowerBound(lowerBound);
                  cartageSellDtl.setUpperBound(upperBound);
                // cartageSellDtl.setLineNumber(Integer.parseInt(lineNo));
                 cartageSellDtl.setLineNumber(count);
                  cartageSellDtl.setBuyChargeAmount(Double.parseDouble(slabValue));
                  cartageSellDtl.setChargeBasis(chargeBasis[i]);
                  cartageSellDtl.setChargeRateIndicator(rateIndicator);
                  
                  if(effectiveFrom!=null)
                  {
                    if(effectiveFrom[i]!=null && effectiveFrom[i].trim().length()!=0)
                        effFromTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,effectiveFrom[i],"00:00");
                  }
                  if(validUpto!=null)
                  {
                    if(validUpto[i]!=null && validUpto[i].trim().length()!=0)
                      validUptoTstmp =eSupplyDateUtility.getTimestampWithTime(dateFormat,validUpto[i],"23:59");
                  }
                  cartageSellDtl.setEffectiveFrom(effFromTstmp);
                  cartageSellDtl.setValidUpto(validUptoTstmp);
                  
                  effFromTstmp      = null;
                  validUptoTstmp    = null;
                  
                if("No".equals(overAllMargin))
                {
                  if("Absolute".equals(marginType))//@@No & Abs
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(marginValues[j]));
                  else
                    cartageSellDtl.setChargeRate((Double.parseDouble(slabValue)*Double.parseDouble(marginValues[j]))/100+Double.parseDouble(slabValue));
                  
                  
                  cartageSellDtl.setMargin(Double.parseDouble(marginValues[j]));
                }
                else if("Yes".equals(overAllMargin) && "Absolute".equals(marginType))
                {            
                  //@@ Commented by subrahmanyam for the Enhancement 170759 on 02/06/09                
                /*  
                  if("MIN".equals(slabBreak))
                  {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(minMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(minMargin));
                  }
                  else if("MAX".equals(slabBreak))
                  {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(maxMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(maxMargin));
                  }
                  */
//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09     
                if(baseMargin==null)
                {
                      if("MIN".equals(slabBreak))
                    {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(minMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(minMargin));
                   }
                  else if("MAX".equals(slabBreak))
                  {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(maxMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(maxMargin));
                  }
                }
                else
                {
                  if("BASE".equals(slabBreak))
                  {
                    if(baseMargin.equals(""))
                      baseMargin="0.0";
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(baseMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(baseMargin));
                  }
                  else if("MIN".equals(slabBreak))
                  {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(minMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(minMargin));
                  }
                  else if("MAX".equals(slabBreak))
                  {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(maxMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(maxMargin));
                  }
                    else
                  {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(slabMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(slabMargin));
                  }
                }
                
                  //@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09                   
            
                 /* else
                  {
                    cartageSellDtl.setChargeRate(Double.parseDouble(slabValue)+Double.parseDouble(slabMargin));
                    cartageSellDtl.setMargin(Double.parseDouble(slabMargin));
                  }
                  */
                }
                else
                {
                  cartageSellDtl.setChargeRate((Double.parseDouble(slabValue)*Double.parseDouble(request.getParameter("marginPercent"))/100+Double.parseDouble(slabValue)));
                  cartageSellDtl.setMargin(Double.parseDouble(request.getParameter("marginPercent")));
                }
                  
                  slabSellChargesList.add(cartageSellDtl);
                  count++;
                }
              }
        }
      }
    }
    catch(Exception e)
    {
//      Logger.error(FILE_NAME,"Error While Adding Slab Sell Charges.."+e);
      logger.error(FILE_NAME+"Error While Adding Slab Sell Charges.."+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Adding Sell Charges! Please Try Again",e);
    }
    return slabSellChargesList;
  }
  
  private void doListSellChargesAddProcess(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
    QMSCartageMasterDOB     masterDOB           =   null;
    QMSCartageBuyDtlDOB     buyDtlDOB           =   null;
    QMSCartageSellDtlDOB    sellDtlDOB          =   null;
    HttpSession             session             =   request.getSession();
    ArrayList               buyChargesList      =   null;
    ArrayList               pickUpList          =   null;
    ArrayList               delList             =   null;
    ArrayList               slabList            =   null;
    ArrayList               sellChargesList     =   new ArrayList();
    
    int                     pickupSize          =   0;
    int                     delSize             =   0;
    int                     noOfSlabs           =   0;
    
    HashMap                 hMap                =   null;
    HashMap                 dHMap               =   null;
    
    String[]                marginValues        =   null;
    String[]                dMarginValues       =   null;
    String[]                absMargins          =   null;
    String                  marginType          =   null;
    String                  overAllMargin       =   null;
    String                  marginPercent       =   null;
    
    String[]                checkBoxValues      =   null;
    String[]                dCheckBoxValues     =   null;
    
    ChargeMasterSessionHome home                =   null;
    ChargeMasterSession     remote              =   null;
    
    try
    {
        masterDOB          =     (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
        buyChargesList     =     (ArrayList)session.getAttribute("buyChargesList");
        
        marginType         =      request.getParameter("marginType");
        overAllMargin      =      request.getParameter("overAllMargin");
        marginValues       =      request.getParameterValues("marginValues");
        dMarginValues      =      request.getParameterValues("dMarginValues");
        absMargins         =      request.getParameterValues("absMargins");
        marginPercent      =      request.getParameter("marginPercent");
        
        
        if(buyChargesList!=null && buyChargesList.size()>0)
        {
          pickUpList		= (ArrayList)buyChargesList.get(0);
          delList			  = (ArrayList)buyChargesList.get(1);
          slabList		  = (ArrayList)buyChargesList.get(2);      
          
          if(pickUpList!=null)
            pickupSize = pickUpList.size();
          if(delList!=null)
            delSize	   = delList.size();
          if(slabList!=null)
            noOfSlabs  = slabList.size();      
        }
          
          checkBoxValues   =  request.getParameterValues("checkBoxValue");
          dCheckBoxValues  =  request.getParameterValues("dCheckBoxValue");
          
          for(int i=0;i<pickupSize;i++)
          {
            if("YES".equalsIgnoreCase(checkBoxValues[i]))
            {
              buyDtlDOB   = (QMSCartageBuyDtlDOB)pickUpList.get(i);
              hMap				= buyDtlDOB.getSlabRates();
              
              for(int j=0;j<noOfSlabs;j++)
              {
                if(hMap.get(slabList.get(j))!=null)
                {
                  sellDtlDOB  = new QMSCartageSellDtlDOB();
                  sellDtlDOB.setCartageId(buyDtlDOB.getCartageId());
                  sellDtlDOB.setZoneCode(buyDtlDOB.getZoneCode());
                  sellDtlDOB.setChargeType(buyDtlDOB.getChargeType());
                  sellDtlDOB.setChargeBasis(buyDtlDOB.getChargeBasis());
                  sellDtlDOB.setEffectiveFrom(buyDtlDOB.getEffectiveFrom());
                  sellDtlDOB.setValidUpto(buyDtlDOB.getValidUpto());
                  sellDtlDOB.setLineNumber(j);
                  sellDtlDOB.setChargeSlab((String)slabList.get(j));
                  sellDtlDOB.setLowerBound("0");
                  sellDtlDOB.setUpperBound("0");
                  sellDtlDOB.setMarginType("Absolute".equals(marginType)?"A":"P");
                  sellDtlDOB.setOverallMargin(overAllMargin);
                  sellDtlDOB.setCurrencyId(masterDOB.getCurrencyId());
                  sellDtlDOB.setBuyChargeAmount(Double.parseDouble((String)hMap.get(slabList.get(j))));
                  
                  if("No".equals(overAllMargin))
                  {
                    if("Absolute".equals(marginType))//@@No & Abs
                      sellDtlDOB.setChargeRate(Double.parseDouble((String)hMap.get(slabList.get(j)))+Double.parseDouble(marginValues[j]));
                    else
                      sellDtlDOB.setChargeRate((Double.parseDouble((String)hMap.get(slabList.get(j)))*Double.parseDouble(marginValues[j]))/100+Double.parseDouble((String)hMap.get(slabList.get(j))));                    
                    
                    sellDtlDOB.setMargin(Double.parseDouble(marginValues[j]));
                  }
                  else if("Yes".equals(overAllMargin) && "Absolute".equals(marginType))
                  {
                      sellDtlDOB.setChargeRate(Double.parseDouble((String)hMap.get(slabList.get(j)))+Double.parseDouble(absMargins[j]));
                      sellDtlDOB.setMargin(Double.parseDouble(absMargins[j]));
                  }
                  else//@@Yes & Percent
                  {
                    sellDtlDOB.setChargeRate((Double.parseDouble((String)hMap.get(slabList.get(j)))*Double.parseDouble(marginPercent)/100+Double.parseDouble((String)hMap.get(slabList.get(j)))));
                    sellDtlDOB.setMargin(Double.parseDouble(marginPercent));
                  }
                  sellChargesList.add(sellDtlDOB);
                }
              }              
            }
          }
          
          for(int i=0;i<delSize;i++)
          {
            if("YES".equalsIgnoreCase(dCheckBoxValues[i]))
            {
              buyDtlDOB   = (QMSCartageBuyDtlDOB)delList.get(i);
              dHMap       =  buyDtlDOB.getSlabRates();
              for(int j=0;j<noOfSlabs;j++)
              {
                if(dHMap.get(slabList.get(j))!=null)
                {
                  sellDtlDOB  = new QMSCartageSellDtlDOB();
                  sellDtlDOB.setCartageId(buyDtlDOB.getCartageId());
                  sellDtlDOB.setZoneCode(buyDtlDOB.getZoneCode());
                  sellDtlDOB.setChargeType(buyDtlDOB.getChargeType());
                  sellDtlDOB.setChargeBasis(buyDtlDOB.getChargeBasis());
                  sellDtlDOB.setEffectiveFrom(buyDtlDOB.getEffectiveFrom());
                  sellDtlDOB.setValidUpto(buyDtlDOB.getValidUpto());
                  //selectedDtlDOB.setChargeRate(Double.parseDouble(request.getParameter("dSlabValue"+i+j)));
                  sellDtlDOB.setLineNumber(j);
                  sellDtlDOB.setChargeSlab((String)slabList.get(j));
                  sellDtlDOB.setLowerBound("0");
                  sellDtlDOB.setUpperBound("0");
                  sellDtlDOB.setMarginType("Absolute".equals(marginType)?"A":"P");
                  sellDtlDOB.setOverallMargin(overAllMargin);
                  sellDtlDOB.setCurrencyId(masterDOB.getCurrencyId());
                  sellDtlDOB.setBuyChargeAmount(Double.parseDouble((String)dHMap.get(slabList.get(j))));
                  //sellDtlDOB  = calculateValuesForMargins(request,sellDtlDOB,"D");
                                   
                  if("No".equals(overAllMargin))
                  {
                    if("Absolute".equals(marginType))//@@No & Abs
                      sellDtlDOB.setChargeRate(Double.parseDouble((String)dHMap.get(slabList.get(j)))+Double.parseDouble(dMarginValues[j]));
                    else
                      sellDtlDOB.setChargeRate((Double.parseDouble((String)dHMap.get(slabList.get(j)))*Double.parseDouble(dMarginValues[j]))/100+Double.parseDouble((String)dHMap.get(slabList.get(j))));                    
                    
                    sellDtlDOB.setMargin(Double.parseDouble(dMarginValues[j]));
                  }
                  else if("Yes".equals(overAllMargin) && "Absolute".equals(marginType))
                  {
                      sellDtlDOB.setChargeRate(Double.parseDouble((String)dHMap.get(slabList.get(j)))+Double.parseDouble(absMargins[j]));
                      sellDtlDOB.setMargin(Double.parseDouble(absMargins[j]));
                  }
                  else//@@Yes & Percent
                  {
                    sellDtlDOB.setChargeRate((Double.parseDouble((String)dHMap.get(slabList.get(j)))*Double.parseDouble(marginPercent)/100+Double.parseDouble((String)dHMap.get(slabList.get(j)))));
                    sellDtlDOB.setMargin(Double.parseDouble(marginPercent));
                  }
                  sellChargesList.add(sellDtlDOB);
                }
              }  
            }
          }
          
          sellChargesList.add(masterDOB);
          
          home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote  =   (ChargeMasterSession)home.create();
          
          remote.insertCartageSellCharges(sellChargesList);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error("Error in doListSellChargesAddProcess :"+e);
      throw new FoursoftException("An Error Has Occurred While Adding FCL Cartage Charges. Please Try Again!",e);
    }
    
  }
  private void getUpdatedCartageBuyCharges(HttpServletRequest request,HttpServletResponse response)throws FoursoftException
	{
	  ChargeMasterSessionHome home          = null;
	  ChargeMasterSession     remote        = null;
	  ArrayList               list          = null;
    QMSCartageMasterDOB     masterDOB     = new QMSCartageMasterDOB();
    String                  shipmentMode  = request.getParameter("shipmentMode");
    String                  consoleType   = request.getParameter("consoleType");
    
	  try
	  {
		  ESupplyGlobalParameters loginbean = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
      
      masterDOB.setTerminalId(loginbean.getTerminalId());
      masterDOB.setAccessLevel(loginbean.getAccessType());
      masterDOB.setChargeType(request.getParameter("chargeType"));
      masterDOB.setLocationId(request.getParameter("locationId"));
      masterDOB.setShipmentMode(shipmentMode);
      masterDOB.setConsoleType(consoleType);
      
		  home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
		  remote  =   (ChargeMasterSession)home.create();  
		  list    =    remote.getUpdatedBuyCharges(masterDOB);
      request.getSession().setAttribute("CartageBuyCharges",list); 
	  }
	  catch(Exception e)
	  {
		 e.printStackTrace();
//		 Logger.error(FILE_NAME,"Error while getting the updated Derails ",e.toString());
		 logger.error(FILE_NAME+"Error while getting the updated Derails "+e.toString());
		 throw new FoursoftException("An error occured while getting the details 'Please try again");
	  }
	}
  //@@Added by Kameswari for the WPBN issue-54554
    private ArrayList  doGetDensityGroupList(HttpServletRequest request,HttpServletResponse response)
  {
    ChargeMasterSessionHome home                = null;
    ChargeMasterSession     remote              = null;
    ArrayList              densityGroupCodeList = null;
    String shipmentMode = request.getParameter("shipMode");
    String                 chargeBasis          = null; // @@ Added by subrahmanyam for the pbn id: 186783   on 22/oct/09   
    try
    {
     
        home      = (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
        remote    = (ChargeMasterSession)home.create();
        //@@ Commented by subrahmanyam on 13/11/08
        //densityGroupCodeList  = remote.getDensityGroupCodeList();
        //@@ Added By subrahmanyam for the WPBN issue -145057 On 13/11/08
         chargeBasis     = request.getParameter("primaryUnit");//@@ Added by subrahmanyam for the pbn id: 186783 on 22/oct/09
      // @@ Commented by subrahmanyam for the pbn id: 186783 on 22/oct/09
      //   densityGroupCodeList  = remote.getDensityGroupCodesList(shipmentMode);
           densityGroupCodeList  = remote.getDensityGroupCodesList(shipmentMode,chargeBasis);//@@ Added by subrahmanyam for the pbn id: 186783 on 22/oct/09
 

    }
    catch(Exception e)
    {
      //Logger.info(FILE_NAME,"Exception while forwarding the controller"+e);
      logger.info(FILE_NAME+"Exception while retreiving density group code list"+e);
    }
    return densityGroupCodeList;
  }
  //@@WPBN issue-54554
  private void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile) throws IOException, ServletException
  {
		try
		{
    //Logger.info(FILE_NAME," forwardFile "+forwardFile);
  // response.reset();
    //logger.info(FILE_NAME+" forwardFile "+forwardFile);
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
//			Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
	      logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
   }
}
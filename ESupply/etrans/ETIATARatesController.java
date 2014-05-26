
/**
	 * @file : ETIATARatesController.java
	 * @author : Srivegi
	 * @date : 23-03-1005
	 * @version : 1.8 
	 */

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.setup.IATARateMaster.java.IATAChargeDtlsModel;
import com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import javax.naming.InitialContext;

public class ETIATARatesController extends HttpServlet 
{
  private static final String FILE_NAME = "ETIATARatesController";
  private static Logger logger = null;

  
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(ETIATARatesController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
      //Logger.info(FILE_NAME,"In doPost() of ETIATARatesController.java");    
      logger.info(FILE_NAME+"In doPost() of ETIATARatesController.java");    
      
      String  operation      = request.getParameter("Operation");
      String  errorMessage	 = null;	
	  HttpSession 			session 		= 	request.getSession();
      ESupplyGlobalParameters loginBean		= 	null;
	  loginBean								= 	(ESupplyGlobalParameters)session.getAttribute("loginbean");
		
	  ErrorMessage  errorMessageObject = null;
	  ArrayList		keyValueList	   = new ArrayList();	

      //Logger.info(FILE_NAME,"Operation is " + operation);
      
      try
      {
		 if(operation.equals("Add"))
         {
           generateIATARates(request,response,loginBean);
           errorMessage = " Error While Creating IATA Rates ";
         } 
         else if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("ModifyDtls")  )
         {
		   modifyIATARates(request,response,loginBean);
         }
		 else if(operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("ViewDtls") )
		     {
			      displayIATARates(request,response,loginBean);
		     }
         else if(operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("DeleteDtls") )
		     {
			      removeIATARates(request,response,loginBean);
		     }
         else if(operation.equalsIgnoreCase("MAWBView"))
		     {
			      viewIATARates(request,response,loginBean);
		     }
	   }
     catch(Exception e)
     {
     }
  }


//// Add Method for IATA Rates

private void generateIATARates(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginBean)throws ServletException,IOException
    {
        
                
        IATADtlModel   rateDtlModel      = new IATADtlModel();
     
        ESupplyGlobalParameters   loginbean			 		= (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
        String  				  processFlag  		 		= null;// processFlag is to check the Controll coming from which screen of the Process 
        String		originTerminalId		 = "";
		String		destinationTerminalId	 = "";
		String		serviceLevelId			 = "";
		String		currencyId				 = "";	
	    String      subOperation             = "";
		String      result                   = "";

		String[]  slabValues      = new String[6];
		String[]  rateValues      = new String[6]; 
		ArrayList  chargeValues  = new ArrayList();

		InitialContext  		  jndiContext   	      = null;
		SetUpSessionHome     setupHome   = null;
		SetUpSession	      setupRemote = null;
        
        HttpSession session  =  request.getSession();
        
        ErrorMessage  errorMessageObject = null;
	    ArrayList	  keyValueList	     = new ArrayList();

        
        try
        {
            InitialContext initial		  = new InitialContext();
		    setupHome   = (SetUpSessionHome)initial.lookup("SetUpSessionBean");
			setupRemote = (SetUpSession)setupHome.create();
			processFlag	=  request.getParameter("processFlag");    
			subOperation = request.getParameter("subOperation");    
			session.removeAttribute("errorCodes");

            //Logger.info(FILE_NAME,"processFlag============================= " + processFlag);    
			//Logger.info(FILE_NAME,"subOperation============================= " + subOperation);   
			
		      if(processFlag==null)
		      {
			      doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
		      }
			  else if(subOperation.equalsIgnoreCase("Next"))
			  {
				   rateDtlModel = setRateLaneInfo(request,response);
				   session.setAttribute("displayData",rateDtlModel);
				   originTerminalId 		= request.getParameter("originGatewayId");
				   destinationTerminalId    = request.getParameter("destinationGatewayId");
				   serviceLevelId		    = request.getParameter("serviceLevelId");
				   currencyId			    = request.getParameter("currencyId");
                   
                   
                   String errorCodes = "" ;
				   errorCodes  = setupRemote.validateInfo(originTerminalId,destinationTerminalId,serviceLevelId,currencyId,"Add");
                   	//Logger.info(FILE_NAME,"================errorCodes============= " +errorCodes);   
				   if(!errorCodes.equals(""))
				  {
                      //Logger.info(FILE_NAME,"=======errorCodes================= "+errorCodes);  
      			     session.setAttribute("errorCodes",errorCodes);
					 doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }
				  else
				  {
                   rateDtlModel=setupRemote.getIATADtls(originTerminalId,destinationTerminalId,serviceLevelId,"Add",1);	 
 
                   if(rateDtlModel.getOriginLocation()!=null)
				  {
                 errorMessageObject = new ErrorMessage("Rates Defined Between The Terminals",""); 
				 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject);
				 session.removeAttribute("displayData");
				 doFileDispatch(request,response,"/ESupplyErrorPage.jsp");            
				  }
				  else
				  {
					  doFileDispatch(request,response,"/etrans/ETIATARateLaneInfo.jsp");
                  }
				  }
				  
			  } 
			  else if(processFlag.equalsIgnoreCase("FLAT") && subOperation.equalsIgnoreCase("Submit"))
    			  {  
				     //Logger.info(FILE_NAME," FLAT SUBMIT================= "+request.getParameter("minRate")); 

				     rateDtlModel = (IATADtlModel)session.getAttribute("displayData");
					 IATAChargeDtlsModel rateDtls = new IATAChargeDtlsModel();
					 rateDtls.setSlabValue(request.getParameter("minRate"));
                     rateDtls.setRateValue(request.getParameter("flatRate"));
					 
					 rateDtlModel.setRateDtls(rateDtls);	 
					 
					 //Logger.info(FILE_NAME," slab value ============================= "+rateDtls.getSlabValue()); 
					 result = setupRemote.addIATADtls(rateDtlModel);
					 
				  }
              else if(processFlag.equalsIgnoreCase("Slab") && subOperation.equalsIgnoreCase("Submit"))
    			  {
				     //Logger.info(FILE_NAME," SLAB SUBMIT ============================= "); 
				     rateDtlModel = (IATADtlModel)session.getAttribute("displayData");
                     slabValues = request.getParameterValues("slabValues");
        			 rateValues = request.getParameterValues("rateValues"); 
					 //Logger.info(FILE_NAME," slabValues.length ============================= "+slabValues.length); 
        			 int slabValLen	=	slabValues.length;
        			 for(int i=0;i<slabValLen;i++)
		             {
						 IATAChargeDtlsModel rateDtls = new IATAChargeDtlsModel();
						 IATADtlModel   rateDtlobj      = new IATADtlModel();
						/*  if(i==0)
						 {
							rateDtls.setSlNo(i+1);
                            rateDtls.setSlabValue("MIN");
							rateDtls.setRateValue("10");
							rateDtlobj.setRateDtls(rateDtls);
							continue;
						 }*/
						    rateDtls.setSlNo(i+1);
                            rateDtls.setSlabValue(slabValues[i]);
							//Logger.info(FILE_NAME," in slabValues[i] ============================= "+slabValues[i]); 
							rateDtls.setRateValue(rateValues[i]);

                         rateDtlobj.setRateDtls(rateDtls);
						 chargeValues.add(rateDtlobj);
                     }
				     result = setupRemote.addIATADtls(rateDtlModel,chargeValues);
				  }
			  else  if(processFlag.equalsIgnoreCase("Pivot") && subOperation.equalsIgnoreCase("Submit"))
    			  {
				     //Logger.info(FILE_NAME," PIVOT SUBMIT============================= "); 
				     rateDtlModel = (IATADtlModel)session.getAttribute("displayData");
                     //Logger.info(FILE_NAME," rateDtlModel ============================= "+rateDtlModel); 
					 IATADtlModel   chargeDtlModel      = new IATADtlModel();
					  IATAChargeDtlsModel rateDtls = new IATAChargeDtlsModel();
                    
                     rateValues[0] = request.getParameter("pivotWeight");
					 rateValues[1] = request.getParameter("pivotWeightRate");
					 rateValues[2] = request.getParameter("overPivotWeight");
					 rateValues[3] = request.getParameter("overPivotRate");
					 rateValues[4] = request.getParameter("overFlowRate");
					 rateValues[5] = request.getParameter("maximumCharge");
                      //Logger.info(FILE_NAME," rateValues ============================= "+rateValues[5]); 

                     slabValues[0] = "PW";
					 slabValues[1] = "PWR";
					 slabValues[2] = "OPW";
					 slabValues[3] = "OPR";
					 slabValues[4] = "OFR";
					 slabValues[5] = "MC";
                     //Logger.info(FILE_NAME," slabValues ============================= "+slabValues[5]); 
					 int slabValLen	=	slabValues.length;
					 for(int i=0;i<slabValLen;i++)
		             { 
						 //Logger.info(FILE_NAME," slabValues.length============================= "+slabValues.length); 
						 chargeDtlModel      = new IATADtlModel();
						 rateDtls = new IATAChargeDtlsModel();

						    rateDtls.setSlNo(i+1);
                            rateDtls.setSlabValue(slabValues[i]);
							rateDtls.setRateValue(rateValues[i]);

                         chargeDtlModel.setRateDtls(rateDtls);
						 chargeValues.add(chargeDtlModel);
                     }
				     result = setupRemote.addIATADtls(rateDtlModel,chargeValues);
				  }
			  
			  if (result.equals("Added"))
			  {
      			 errorMessageObject = new ErrorMessage("Record Successfully Added ",""); 
				 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject);
				 session.removeAttribute("displayData");
				 doFileDispatch(request,response,"/ESupplyErrorPage.jsp");            
			  }
			 
        }
        catch(Exception e)
        {
			//Logger.error(FILE_NAME," error ============================= "+e.getMessage());
      logger.error(FILE_NAME+" error ============================= "+e.getMessage());
			e.printStackTrace();
			
        }
	}	

 private void modifyIATARates(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginBean)throws ServletException,IOException
    {
	    IATADtlModel   rateDtlModel      = new IATADtlModel();
        IATAChargeDtlsModel   rateDtls      = new IATAChargeDtlsModel();
		String         processFlag       = "";
		String         result            = "";
        ErrorMessage  errorMessageObject = null;
	    ArrayList	  keyValueList	     = new ArrayList();
		String[]  slabValues      = new String[6];
		String[]  rateValues      = new String[6]; 
		ArrayList  chargeValues  = new ArrayList();

 		try{
			 HttpSession session  =  request.getSession();
			 SetUpSessionHome     setupHome   = null;
             SetUpSession	      setupRemote = null;
			 processFlag	=  request.getParameter("processFlag");    
             String subOperation =request.getParameter("subOperation");
             String operation =request.getParameter("Operation");
             setupHome   = (SetUpSessionHome)loginBean.getEjbHome("SetUpSessionBean");
		     setupRemote = (SetUpSession)setupHome.create();
			 //Logger.info(FILE_NAME,"=======processFlag=================== "+processFlag);  
			 //Logger.info(FILE_NAME,"=======subOperation=================== "+subOperation);  
   	         //Logger.info(FILE_NAME,"=======operation=================== "+operation);  
             session.removeAttribute("displayDtls");
			 session.removeAttribute("displayRateData");	
  			session.removeAttribute("errorCodes");
			 	  
              if(processFlag==null )
		      {
			      doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
		      }
			  else if(operation.equalsIgnoreCase("ModifyDtls"))
			  { 
				   //Logger.info(FILE_NAME,"=======MODIFY DTLS=================== "+operation);  
				   String originTerminalId 		   = request.getParameter("originGatewayId");
				   String destinationTerminalId    = request.getParameter("destinationGatewayId");
				   String serviceLevelId		   = request.getParameter("serviceLevelId");
		           String errorCodes = "" ;
				   errorCodes  = setupRemote.validateInfo(originTerminalId,destinationTerminalId,serviceLevelId,"","ModifyDtls");
                   if(!errorCodes.equals(""))
				  { //Logger.info(FILE_NAME,"=======errorCodes================= "+errorCodes);  
                     session.setAttribute("errorCodes",errorCodes);
					 doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }
				  else
				  {
		           
				   rateDtlModel=setupRemote.getIATADtls(originTerminalId,destinationTerminalId,serviceLevelId,operation,1);	  
				   session.setAttribute("displayDtls",rateDtlModel);	
   			       doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }
                      
			  }
			  else if (subOperation.equalsIgnoreCase("Next"))
			  {
                	  //Logger.info(FILE_NAME,"========goinn to ETIATARateLaneInfo=================== "); 
                      ArrayList chargeDtl = new ArrayList();

                      int masterId= Integer.parseInt(request.getParameter("IATAMasterId"));
					  //Logger.info(FILE_NAME,"======== masterId=================== "+masterId); 
                      rateDtlModel = setRateLaneInfo(request,response);
					  rateDtlModel.setIATAMasterId(masterId);
					  session.setAttribute("displayData",rateDtlModel);	
					  String errorCodes = "" ;
				       errorCodes  = setupRemote.validateInfo("","","",rateDtlModel.getCurrencyId(),"Modify");
                      if(!errorCodes.equals(""))
				  {
                      //Logger.info(FILE_NAME,"=======errorCodes================= "+errorCodes);  
                     session.setAttribute("errorCodes",errorCodes);
					 doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }
				  else
				  {
					   chargeDtl=setupRemote.getIATADtls(masterId);

					   //Logger.info(FILE_NAME,"================rateDtlModel========== "+rateDtlModel);
					   //Logger.info(FILE_NAME,"================rateDtlModel========== "+rateDtlModel.getRateDtls()); 
					   session.setAttribute("displayChargeData",chargeDtl);	
					   doFileDispatch(request,response,"/etrans/ETIATARateLaneInfo.jsp");
              }  
              }  
				  
			else if(subOperation.equalsIgnoreCase("Submit") && processFlag.equalsIgnoreCase("Flat"))
    			  {
				 int slNo = 0;
				 //Logger.info(FILE_NAME,"================FLAT SUBMIT========== "); 
				 rateDtlModel=(IATADtlModel)session.getAttribute("displayData");	
				 //Logger.info(FILE_NAME,"================rateDtlModel========== "+rateDtlModel); 
            	 rateDtls.setSlNo(slNo+1);
				 rateDtls.setSlabValue(request.getParameter("minRate"));
				 //Logger.info(FILE_NAME,"================getSlabValue========== "+rateDtls.getSlabValue()); 
                 rateDtls.setRateValue(request.getParameter("flatRate"));
				 //Logger.info(FILE_NAME,"================flatRate========== "+rateDtls.getRateValue()); 
				 rateDtlModel.setRateDtls(rateDtls);
				 //Logger.info(FILE_NAME,"================rateDtlModel========== "+rateDtlModel.getRateDtls()); 
					
				     result = setupRemote.setIATADtls(rateDtlModel);
					 //Logger.info(FILE_NAME,"================result========== "+result); 
					 if(result.equals("Modified"))
			         {
						 errorMessageObject = new ErrorMessage("Record Successfully Modified ",""); 
						 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
						 keyValueList.add(new KeyValue("Operation","Modify")); 	
						 errorMessageObject.setKeyValueList(keyValueList);
						 request.setAttribute("ErrorMessage",errorMessageObject);
						 doFileDispatch(request,response,"/ESupplyErrorPage.jsp");            
					 }
					
				  }
             else if(subOperation.equalsIgnoreCase("Submit") &&  processFlag.equalsIgnoreCase("PIVOT"))
    			  {
				     //Logger.info(FILE_NAME,"PIVOT SUBMIT============================= "); 
                     rateDtlModel=(IATADtlModel)session.getAttribute("displayData");	
					 IATADtlModel   chargeDtlModel      = new IATADtlModel();
					   rateDtls = new IATAChargeDtlsModel();
					 rateValues[0] = request.getParameter("pivotWeight");
					 rateValues[1] = request.getParameter("pivotWeightRate");
					 rateValues[2] = request.getParameter("overPivotWeight");
					 rateValues[3] = request.getParameter("overPivotRate");
					 rateValues[4] = request.getParameter("overFlowRate");
					 rateValues[5] = request.getParameter("maximumCharge");

					 slabValues[0] = "PW";
					 slabValues[1] = "PWR";
					 slabValues[2] = "OPW";
					 slabValues[3] = "OPR";
					 slabValues[4] = "OFR";
					 slabValues[5] = "MC";
                     //Logger.info(FILE_NAME," slabValues ============================= "+slabValues[5]); 
					 int slabValLen	= slabValues.length;
					 for(int i=0;i<slabValLen;i++)
		             { 
						 //Logger.info(FILE_NAME," slabValues.length============================= "+slabValues.length); 
						 chargeDtlModel      = new IATADtlModel();
						 rateDtls = new IATAChargeDtlsModel();

						    rateDtls.setSlNo(i+1);
                            rateDtls.setSlabValue(slabValues[i]);
							rateDtls.setRateValue(rateValues[i]);

                         chargeDtlModel.setRateDtls(rateDtls);
						 chargeValues.add(chargeDtlModel);
                     }
				     result = setupRemote.updateIATADtls(rateDtlModel,chargeValues);
					 if(result.equals("Modified"))
			         {
						 errorMessageObject = new ErrorMessage("Record Successfully Modified ",""); 
						 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
						 keyValueList.add(new KeyValue("Operation","Modify")); 	
						 errorMessageObject.setKeyValueList(keyValueList);
						 request.setAttribute("ErrorMessage",errorMessageObject);
						 doFileDispatch(request,response,"/ESupplyErrorPage.jsp");            
					 }
				  }
				  else if(subOperation.equalsIgnoreCase("Submit") && processFlag.equalsIgnoreCase("SLAB"))
			       {
					  //Logger.info(FILE_NAME,"SLAB SUBMIT============================= ");  
                      rateDtlModel=(IATADtlModel)session.getAttribute("displayData");
				      slabValues = request.getParameterValues("slabValues");
        			 rateValues = request.getParameterValues("rateValues"); 
					 //Logger.info(FILE_NAME," slabValues.length ============================= "+slabValues.length); 
					 int slabValLen	=	slabValues.length;
        			 for(int i=0;i<slabValLen;i++)
		             {
						    rateDtls = new IATAChargeDtlsModel();
					        IATADtlModel   rateDtlobj      = new IATADtlModel();
					       /* if(i==0)
						 {
							rateDtls.setSlNo(i+1);
                            rateDtls.setSlabValue("MIN");
							rateDtls.setRateValue("10");
							rateDtlobj.setRateDtls(rateDtls);
							continue;
						 }*/

							rateDtls.setSlNo(i+1);
                            rateDtls.setSlabValue(slabValues[i]);
							//Logger.info(FILE_NAME," in slabValues[i] ============================= "+slabValues[i]); 
							rateDtls.setRateValue(rateValues[i]);

                         rateDtlobj.setRateDtls(rateDtls);
						 chargeValues.add(rateDtlobj);
                     }
                    result = setupRemote.updateIATADtls(rateDtlModel,chargeValues);
					if(result.equals("Modified"))
			         {
						 errorMessageObject = new ErrorMessage("Record Successfully Modified ",""); 
						 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
						 keyValueList.add(new KeyValue("Operation","Modify")); 	
						 errorMessageObject.setKeyValueList(keyValueList);
						 request.setAttribute("ErrorMessage",errorMessageObject);
						 session.removeAttribute("displayData");
						 session.removeAttribute("displayChargeData");	
						 doFileDispatch(request,response,"/ESupplyErrorPage.jsp");            
					 }
			}
    }//try end
    catch(Exception e)
    {
         //Logger.error(FILE_NAME,"Exception in setRateLaneInfo()","Exception While Assigning Lane Dtls to Object ",e);
         logger.error(FILE_NAME+"Exception in setRateLaneInfo()"+"Exception While Assigning Lane Dtls to Object ",e);
           throw new IOException(e.toString());
    }
    }
 private void displayIATARates(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginBean)throws ServletException,IOException
    {
        IATADtlModel   rateDtlModel      = new IATADtlModel();
        IATAChargeDtlsModel   rateDtls      = new IATAChargeDtlsModel();
		String         processFlag       = null;
		String         result            = null;
        ErrorMessage  errorMessageObject = null;
	    ArrayList	  keyValueList	     = new ArrayList();

 		try{
			 HttpSession session  =  request.getSession();
			 SetUpSessionHome     setupHome   = null;
             SetUpSession	      setupRemote = null;
			 processFlag	=  request.getParameter("processFlag");    
             String subOperation =request.getParameter("subOperation");
             String operation =request.getParameter("Operation");
             setupHome   = (SetUpSessionHome)loginBean.getEjbHome("SetUpSessionBean");
		     setupRemote = (SetUpSession)setupHome.create();
			 //Logger.info(FILE_NAME,"=======processFlag=================== "+processFlag);  
			 //Logger.info(FILE_NAME,"=======subOperation=================== "+subOperation);  
		     //Logger.info(FILE_NAME,"=======operation=================== "+operation);  
			 session.removeAttribute("displayDtls");
		     session.removeAttribute("displayRateData");	
 			session.removeAttribute("errorCodes");
			 	  
              if(processFlag==null )
		      {
			      doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
		      }
			  else if(operation.equalsIgnoreCase("ViewDtls"))
			  {  
				   //Logger.info(FILE_NAME,"=======operation=================== "+operation);  
				   String originTerminalId 		= request.getParameter("originGatewayId");
				   String destinationTerminalId    = request.getParameter("destinationGatewayId");
				   String serviceLevelId		    = request.getParameter("serviceLevelId");
		           String errorCodes = "" ;
				   errorCodes  = setupRemote.validateInfo(originTerminalId,destinationTerminalId,serviceLevelId,"","ModifyDtls");
                   if(!errorCodes.equals(""))
				  {
                      //Logger.info(FILE_NAME,"=======errorCodes================= "+errorCodes);  
                     session.setAttribute("errorCodes",errorCodes);
					 doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }
				  else
				  {
                   rateDtlModel=setupRemote.getIATADtls(originTerminalId,destinationTerminalId,serviceLevelId,"Add",1);	 
                  
		           
				   rateDtlModel=setupRemote.getIATADtls(originTerminalId,destinationTerminalId,serviceLevelId,operation,1);	  
				   session.setAttribute("displayDtls",rateDtlModel);	
   			       doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }
                      
			  }
			  else if (subOperation.equalsIgnoreCase("Next"))
			  {
				       ArrayList chargeDtl = new ArrayList();
            	      //Logger.info(FILE_NAME,"========goinn to ETIATARateLaneInfo=================== "); 
                      int masterId= Integer.parseInt(request.getParameter("IATAMasterId"));
			    	  //Logger.info(FILE_NAME,"======== masterId=================== "+masterId); 
                      rateDtlModel = setRateLaneInfo(request,response);
					  rateDtlModel.setIATAMasterId(masterId);
			          //rateDtlModel=setupRemote.getIATADtls(rateDtlModel.getOriginLocation(),rateDtlModel.getDestLocation(),rateDtlModel.getServiceLevel(),operation,masterId);
					  chargeDtl=setupRemote.getIATADtls(masterId);
					 // session.setAttribute("displayRateData",rateDtlModel);	
					  session.setAttribute("displayChargeData",chargeDtl);	

                      doFileDispatch(request,response,"/etrans/ETIATARateLaneInfo.jsp");
              }  
			 
				  
			
    }//try end
    catch(Exception e)
    {
         //Logger.error(FILE_NAME,"Exception in setRateLaneInfo()","Exception While Assigning Lane Dtls to Object ",e);
         logger.error(FILE_NAME+"Exception in setRateLaneInfo()"+"Exception While Assigning Lane Dtls to Object "+e);
           throw new IOException(e.toString());
    }

	}
	private void removeIATARates(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginBean)throws ServletException,IOException
    {
        IATADtlModel   rateDtlModel      = new IATADtlModel();
        IATAChargeDtlsModel   rateDtls      = new IATAChargeDtlsModel();
		String         processFlag       = null;
		String         result            = null;
        ErrorMessage  errorMessageObject = null;
	    ArrayList	  keyValueList	     = new ArrayList();

 		try{
			 HttpSession session  =  request.getSession();
			 SetUpSessionHome     setupHome   = null;
             SetUpSession	      setupRemote = null;
			 processFlag	=  request.getParameter("processFlag");    
             String subOperation =request.getParameter("subOperation");
             String operation =request.getParameter("Operation");
             setupHome   = (SetUpSessionHome)loginBean.getEjbHome("SetUpSessionBean");
		     setupRemote = (SetUpSession)setupHome.create();
			 //Logger.info(FILE_NAME,"=======processFlag=================== "+processFlag);  
			 //Logger.info(FILE_NAME,"=======subOperation=================== "+subOperation);  
			        //Logger.info(FILE_NAME,"=======operation=================== "+operation);  
					session.removeAttribute("displayDtls");
					session.removeAttribute("errorCodes");
					
              if(processFlag==null )
		      {
			      doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
		      }
			  else if(operation.equalsIgnoreCase("DeleteDtls"))
			  {  
				   //Logger.info(FILE_NAME,"=======operation=================== "+operation);  
				   String originTerminalId 		= request.getParameter("originGatewayId");
				   String destinationTerminalId    = request.getParameter("destinationGatewayId");
				   String serviceLevelId		    = request.getParameter("serviceLevelId");
		           String errorCodes = "" ;
				   errorCodes  = setupRemote.validateInfo(originTerminalId,destinationTerminalId,serviceLevelId,"","ModifyDtls");
                   if(!errorCodes.equals(""))
				  {
					  //Logger.info(FILE_NAME,"=======errorCodes================= "+errorCodes);     
                     session.setAttribute("errorCodes",errorCodes);
					 doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }
				  else
				  {
                   rateDtlModel=setupRemote.getIATADtls(originTerminalId,destinationTerminalId,serviceLevelId,"Add",1);	 
		           
				   rateDtlModel=setupRemote.getIATADtls(originTerminalId,destinationTerminalId,serviceLevelId,operation,1);	  
				   session.setAttribute("displayDtls",rateDtlModel);	
   			       doFileDispatch(request,response,"/etrans/ETIATARatePreInfo.jsp");
				  }   
			  }
			  else if (subOperation.equalsIgnoreCase("Next"))
			  {
				       ArrayList chargeDtl = new ArrayList();
            	      //Logger.info(FILE_NAME,"========goinn to ETIATARateLaneInfo=================== "); 
                      int masterId= Integer.parseInt(request.getParameter("IATAMasterId"));
			    	  //Logger.info(FILE_NAME,"======== masterId=================== "+masterId); 
                      //rateDtlModel=setupRemote.getIATADtls(rateDtlModel.getOriginLocation(),rateDtlModel.getDestLocation(),rateDtlModel.getServiceLevel(),operation,masterId);
               
                      chargeDtl=setupRemote.getIATADtls(masterId); 
					  rateDtlModel.setIATAMasterId(masterId);
                     
					//  session.setAttribute("displayRateData",rateDtlModel);	
					  session.setAttribute("displayChargeData",chargeDtl);	

                      doFileDispatch(request,response,"/etrans/ETIATARateLaneInfo.jsp?&masterId="+masterId);
              }  
			  else if (subOperation.equalsIgnoreCase("Delete"))
			  {
				      //Logger.info(FILE_NAME,"===============delete method======== "); 
				       int masterId=Integer.parseInt(request.getParameter("IATAMasterId"));
			    	  //Logger.info(FILE_NAME,"======== masterId=================== "+masterId); 
            	      result= setupRemote.removeIATADtls(masterId);
					 //Logger.info(FILE_NAME,"================result========== "+result); 
					 if(result.equals("Deleted"))
			         {
						 errorMessageObject = new ErrorMessage("Record Successfully Deleted ",""); 
						 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
						 keyValueList.add(new KeyValue("Operation","Delete")); 	
						 errorMessageObject.setKeyValueList(keyValueList);
						 request.setAttribute("ErrorMessage",errorMessageObject);
						 session.removeAttribute("displayData");
						 session.removeAttribute("displayChargeData");	
						 doFileDispatch(request,response,"/ESupplyErrorPage.jsp");            
					 }
              }  
				  
			
    }//try end
    catch(Exception e)
    {
         //Logger.error(FILE_NAME,"Exception in setRateLaneInfo()","Exception While Assigning Lane Dtls to Object ",e);
         logger.error(FILE_NAME+"Exception in setRateLaneInfo()"+"Exception While Assigning Lane Dtls to Object "+e);
           throw new IOException(e.toString());
    }

	}
 private IATADtlModel setRateLaneInfo(HttpServletRequest request,HttpServletResponse response)
	    throws IOException
	{
         			IATADtlModel rateDtlObject = new IATADtlModel();	
					ESupplyDateUtility	eSupplyDateUtility	=	new ESupplyDateUtility();
          
	     try
	     {
			 //Logger.info(FILE_NAME," gggggg =====dateFormat======================= "+request.getParameter("dateFormat")); 
			 String	dateFormat	=	request.getParameter("dateFormat");
			 //Logger.info(FILE_NAME," gggggg =====validUpto======================= "+request.getParameter("validUpto")); 
             rateDtlObject.setValidUptoDate(eSupplyDateUtility.getTimestamp(dateFormat,request.getParameter("validUpto")));
			 //Logger.info(FILE_NAME," gggggg =====validFrom======================= "+request.getParameter("validFrom")); 
		   	 rateDtlObject.setValidFromDate(eSupplyDateUtility.getTimestamp(dateFormat,request.getParameter("validFrom")));
			 //Logger.info(FILE_NAME," gggggg =====originGatewayId======================= "+request.getParameter("originGatewayId")); 
	         rateDtlObject.setOriginLocation(request.getParameter("originGatewayId"));     
	         rateDtlObject.setDestLocation(request.getParameter("destinationGatewayId"));
	         rateDtlObject.setServiceLevel(request.getParameter("serviceLevelId"));
			 rateDtlObject.setRateType(request.getParameter("processFlag"));
			 rateDtlObject.setUOM(request.getParameter("uom"));
			 rateDtlObject.setCurrencyId(request.getParameter("currencyId"));
			 rateDtlObject.setWeightClass(request.getParameter("wgtClass"));
             rateDtlObject.setOperation(request.getParameter("Operation"));
				
			// Logger.info(FILE_NAME," gggggg =====Operation======================= "+request.getParameter("Operation")); 
				     
         }
		 catch(Exception ex)
	     {
	       //Logger.error(FILE_NAME,"Exception in setRateLaneInfo()","Exception While Assigning Lane Dtls to Object ",ex);
         logger.error(FILE_NAME+"Exception in setRateLaneInfo()"+"Exception While Assigning Lane Dtls to Object "+ex);
           throw new IOException(ex.toString());
         }
	   
	   return rateDtlObject;		
	}   
 public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile) 
		throws IOException, ServletException
	{
		
		//Logger.info(FILE_NAME," In Dispatcher " + forwardFile );
		try
		{
			RequestDispatcher rd = getServletConfig().getServletContext().getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doFileDispatch() ", " Exception in forwarding ---> "+ ex.toString());
      logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
		}
 }
 private void viewIATARates(HttpServletRequest request,HttpServletResponse response,ESupplyGlobalParameters loginBean)throws ServletException,IOException
    {
        IATADtlModel   rateDtlModel      = new IATADtlModel();
        IATAChargeDtlsModel   rateDtls      = new IATAChargeDtlsModel();
		String         processFlag       = null;
		String         result            = null;
        ErrorMessage  errorMessageObject = null;
	    ArrayList	  keyValueList	     = new ArrayList();

 		try{
			 HttpSession session  =  request.getSession();
			 SetUpSessionHome     setupHome   = null;
             SetUpSession	      setupRemote = null;
			 String operation =request.getParameter("Operation");
             setupHome   = (SetUpSessionHome)loginBean.getEjbHome("SetUpSessionBean");
		     setupRemote = (SetUpSession)setupHome.create();
			 //Logger.info(FILE_NAME,"=======operation=================== "+operation);  
		     	  
            	   String originTerminalId 		= request.getParameter("originGatewayId");
				   String destinationTerminalId    = request.getParameter("destinationGatewayId");
				   String serviceLevelId		    = request.getParameter("serviceLevelId");
		       String origin= originTerminalId.substring(3,6);
           String dest= destinationTerminalId.substring(3,6);
          // Logger.info(FILE_NAME,"=======origin=================== "+origin);      
           //Logger.info(FILE_NAME,"=======dest=================== "+dest);      
           
				   rateDtlModel=setupRemote.getIATADtls(origin,dest,serviceLevelId,operation,1);	  
				  
				   ArrayList chargeDtl = new ArrayList();
            	   //Logger.info(FILE_NAME,"========goinn to ETIATARateLaneInfo=================== "); 
                   int masterId =  rateDtlModel.getIATAMasterId();
                   String rateType =  rateDtlModel.getRateType();
                   chargeDtl=setupRemote.getIATADtls(masterId);
				   if(chargeDtl==null)
			         {
					     errorMessageObject = new ErrorMessage("Rates Are Not Defined  ",""); 
						 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
						 keyValueList.add(new KeyValue("Operation","Delete")); 	
						 errorMessageObject.setKeyValueList(keyValueList);
						 request.setAttribute("ErrorMessage",errorMessageObject);
						 session.removeAttribute("displayData");
						 session.removeAttribute("displayChargeData");	
						 doFileDispatch(request,response,"/ESupplyErrorPage.jsp");       
			          }
                   else
			        {
    			   session.setAttribute("displayChargeData",chargeDtl);	
                   doFileDispatch(request,response,"/etrans/ETIATARateLaneInfo.jsp?&processFlag="+rateDtlModel.getRateType());
			       }
    }//try end
    catch(Exception e)
    {
         //Logger.error(FILE_NAME,"Exception in setRateLaneInfo()","Exception While Assigning Lane Dtls to Object ",e);
         logger.error(FILE_NAME+"Exception in setRateLaneInfo()"+"Exception While Assigning Lane Dtls to Object "+e);
           throw new IOException(e.toString());
    }

	}
}
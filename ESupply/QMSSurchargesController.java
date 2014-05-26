import com.foursoft.esupply.common.java.LookUpBean;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSession;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSessionHome;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import com.qms.operations.rates.dob.FlatRatesDOB;
import com.qms.operations.rates.dob.RateDOB;
import com.foursoft.esupply.common.bean.DateFormatter;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.operations.rates.ejb.sls.BuyRatesSessionBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.qms.operations.rates.ejb.sls.BuyRatesSession;
import com.qms.operations.rates.ejb.sls.BuyRatesSessionHome;
import com.qms.setup.dao.SurChargesDAO;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;
import com.qms.setup.java.SurchargeDOB;

import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.util.ESupplyDateUtility;

public class QMSSurchargesController extends HttpServlet 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String 	   FILE_NAME 		= "QMSSurchargesController.java";
	private static Logger logger = null;
	
  SetUpSessionHome         home                 =  null;
  SetUpSession             remote               =  null;
  HttpSession				session					=	 null;
  String					nextNavigation			=	 null;
  String 					operation				=	 null;
  String 					subOperation			=	 null;
  String					surChargeId				=	"";
  String					surChargeDesc			=	"";
  String					shipmentMode			=	"";
  int 						sMode					=	0;
  String					rateBreak				=	"";
  String					rateType				=	"";  
  String					weightBreaks			=	"";
  StringBuffer				errors					=	null;
  ArrayList				    keyValueList			=	null;	 
  ErrorMessage 				errorMessageObject 		=	null;
  // Added by Silpa For SurCharge View All on 22-Apr-11
  ArrayList                   surChargesVOList                   = null;
  String                    serch             = null;// Added by Silpa For SurCharge View All on 15-06-11
  
  
  public void init(ServletConfig config) throws ServletException
  {
	logger  = Logger.getLogger(QMSSurchargesController.class);
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
		  operation     	=  request.getParameter("Operation");
		  subOperation      =  request.getParameter("subOperation");	  
		  serch     = request.getParameter("search");
		  try{
 			  handleRequest(request,response);
		  	}catch(Exception e)
		  {
          doFileDispatch(request,response,"ESupplyErrorPage.jsp"); 
		  }
	}


	private void handleRequest(HttpServletRequest request,
		HttpServletResponse response) throws FoursoftException {
		try {
			SurchargeDOB	surChargeDOB	= new SurchargeDOB();
			SurChargesDAO	surChargeDAO	= new SurChargesDAO();
			 errors = new StringBuffer();
			 keyValueList	=	new ArrayList();
			 session		=	request.getSession();
			  System.out.println("Operation..."+operation);
			  System.out.println("subOperation..."+subOperation);
		   if("Add".equalsIgnoreCase(operation) && ("".equalsIgnoreCase(subOperation) || subOperation ==null) )
			doFileDispatch(request, response, "etrans/QMSSurChargesMaster.jsp");
			else if("Add".equalsIgnoreCase(operation) && "Enter".equalsIgnoreCase(subOperation) )
			{
					logger.info("In Handle Request for Suboperation Enter..");
				surChargeId			=	request.getParameter("scid");
				surChargeDesc		=	request.getParameter("scdesc");
				shipmentMode		=	request.getParameter("smode");
				rateBreak			=	request.getParameter("rbreak");
				rateType			=	request.getParameter("rtype");
				weightBreaks		=	request.getParameter("wtbreaks");
				if("Air".equalsIgnoreCase(shipmentMode))
					sMode	=	1;
				else if("Sea".equalsIgnoreCase(shipmentMode))
					sMode	=	2;
				else if("Truck".equalsIgnoreCase(shipmentMode))
					sMode	=	4;
				
				// Added by Kishore Podili for issue id:226491 on 20-Dec-10
				//surChargeDOB.setSurchargeid(surChargeId+rateBreak.substring(0, 1)+rateType.substring(0, 1));
				surChargeDOB.setSurchargeid(surChargeId);
				// End of Kishore Podili for issue id:226491 on 20-Dec-10
				surChargeDOB.setSurchargeDesc(surChargeDesc);
				surChargeDOB.setShipmentMode(sMode);
				surChargeDOB.setRateBreak(rateBreak);
				surChargeDOB.setRateType(rateType);
				surChargeDOB.setWeightBreaks(weightBreaks);
				surChargeDOB.setSerch(serch);// Added by Silpa For SurCharge View All on 15-06-11
				
	            if(surChargeDAO.insertSurCharges(surChargeDOB)==5){
    					errors.append(" SurChargeId:' "+surChargeId +"' Is Already Existed For The Shipment Mode: "+shipmentMode);
						errorMessageObject = new ErrorMessage(errors.toString(),"QMSSurchargesController?Operation=Add"); 
						keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
						keyValueList.add(new KeyValue("Operation",operation)); 	

					}
				else{		    
						errors.append("SurCharges Are SuccessFully Inserted for the SurChargeId: "+surChargeDOB.getSurchargeid() +" And SurCharge Description: "+surChargeDesc.substring(0,surChargeDesc.length()-3));
						errorMessageObject = new ErrorMessage(errors.toString(),"QMSSurchargesController?Operation=Add"); 
						keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
						keyValueList.add(new KeyValue("Operation",operation)); 	
				}
				errorMessageObject.setKeyValueList(keyValueList);

				request.setAttribute("ErrorMessage",errorMessageObject);
				session.setAttribute("ErrorMessage","SurCharges added Sucessfully");
				session.setAttribute("ErrorCode","RSI");
				session.setAttribute("NextNavigation","QMSSurchargesController?Operation=Add");
				doFileDispatch(request,response,"ESupplyErrorPage.jsp")	;
				
			}
		   
		   // Added By Kishore For the Issue: Surcharge Modify Continue option
			else if("Modify".equalsIgnoreCase(operation)&& ("".equalsIgnoreCase(subOperation) || subOperation ==null)){
				doFileDispatch(request, response, "etrans/QMSSurChargesMasterEnterId.jsp?Operation=Modify");
			}
		   //End Of Kishore For the Issue: Surcharge Modify Continue option
		   
                   // Added by Silpa For SurCharge View All on 22-Apr-11
	   
                     else if("View All".equalsIgnoreCase(operation)&& ("".equalsIgnoreCase(subOperation) || subOperation==null)){
                    	 if(serch==null){// modified  by Silpa.p For SurCharge View All on 15-06-11
				surChargesVOList= surChargeDAO.getSurchargeDetailsforviewall();
                    	 }
                    	 else{
                    		 surChargesVOList= surChargeDAO.getSurchargeDetailsforviewallSort(serch);
                    	 }//ended
				//System.out.println( "ALIST--------->"+alist);
				request.setAttribute("surChargesVOList" ,surChargesVOList);
				doFileDispatch(request, response, "etrans/QMSSurChargesMasterEnterIdviewall.jsp");
				//getServletContext().getRequestDispatcher("/etrans/QMSSurChargesMasterEnterIdviewall.jsp").forward(request,response);		 
					
					
                    		 
					
				}
		   
		   // ended by Silpa For SurCharge View All
		   
			else if(("Modify".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation) ) && "Enter".equalsIgnoreCase(subOperation) )
			{
					String returnVal	=	"";
					surChargeDOB = new SurchargeDOB();
					surChargeDAO	= new SurChargesDAO();
					int modeInt	=	0;
					String modeString	=	"";
					modeString	=	request.getParameter("shipmentMode");
					if("1".equalsIgnoreCase(modeString))
						modeInt	= 1;
					else if("2".equalsIgnoreCase(modeString))
						modeInt	= 2;
					else if("4".equalsIgnoreCase(modeString))
						modeInt	= 4;
					surChargeDOB.setShipmentMode(modeInt);
					surChargeDOB.setSurchargeid(request.getParameter("surcharge_Id"));
					returnVal	=	surChargeDAO.getSurchargeDetails(surChargeDOB);
					if("No SurCharges".equalsIgnoreCase(returnVal)){
					       errorMessageObject = new ErrorMessage("There Is No Specified SurCharge With The ChargeCode: "+surChargeDOB.getSurchargeid(),"QMSSurchargesController?Operation="+operation); 
					        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
					        keyValueList.add(new KeyValue("Operation",operation)); 	
					        errorMessageObject.setKeyValueList(keyValueList);
					        request.setAttribute("ErrorMessage",errorMessageObject);
					        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
					}
					else{
						logger.info("mode..."+surChargeDOB.getShipmentMode());
						logger.info("brk..."+surChargeDOB.getRateBreak());
						logger.info("type..."+surChargeDOB.getRateType());
						logger.info("desc..."+surChargeDOB.getSurchargeDesc());
						logger.info("id..."+surChargeDOB.getSurchargeid());
					request.getSession().setAttribute("surChargeDOB", surChargeDOB);
					doFileDispatch(request, response, "etrans/QMSSurChargesDetails.jsp?Operation="+operation);
					}
			}
			else if(("Modify".equalsIgnoreCase(operation)  && "Details".equalsIgnoreCase(subOperation))) 
			{
				int sMode	=	0;
				String surchargeId		=	"";
				String surchargeDesc	=	"";
				String 	modeString		=	"";
				surChargeDAO	= new SurChargesDAO();
				surChargeDOB = new SurchargeDOB();
				modeString	=	request.getParameter("shipmentMode");
				if("1".equalsIgnoreCase(modeString))
					sMode	= 1;
				else if("2".equalsIgnoreCase(modeString))
					sMode	= 2;
				else if("4".equalsIgnoreCase(modeString))
					sMode	= 4;
				surChargeDOB.setShipmentMode(sMode);
				surChargeDOB.setSurchargeDesc(request.getParameter("surcharge_Desc"));
				surChargeDOB.setSurchargeid(request.getParameter("surchargeid"));
				surChargeDAO.modifySurCharges(surChargeDOB);
			       errorMessageObject = new ErrorMessage("Surcharge '"+surChargeDOB.getSurchargeid()+"' Successfully Modified. ","QMSSurchargesController?Operation="+operation); 
			        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			        keyValueList.add(new KeyValue("Operation",operation)); 	
			        errorMessageObject.setKeyValueList(keyValueList);
			        request.setAttribute("ErrorMessage",errorMessageObject);
			        doFileDispatch(request,response,"ESupplyErrorPage.jsp");

			}
		   
		   // Added By Kishore For the Issue: Surcharge Delete Continue option
			else if("Delete".equalsIgnoreCase(operation)&& ("".equalsIgnoreCase(subOperation) || subOperation ==null)){
				doFileDispatch(request, response, "etrans/QMSSurChargesMasterEnterId.jsp?Operation=Delete");
			}
		   //End Of Kishore For the Issue: Surcharge Delete Continue option
				
			else if( "Delete".equalsIgnoreCase(operation)  && "Details".equalsIgnoreCase(subOperation)) {
				
				try{
				int sMode	=	0;
				int delStausCount	=	0;
				String 	modeString		=	"";
				surChargeDAO	= new SurChargesDAO();
				surChargeDOB = new SurchargeDOB();
				modeString	=	request.getParameter("shipmentMode");
				if("1".equalsIgnoreCase(modeString))
					sMode	= 1;
				else if("2".equalsIgnoreCase(modeString))
					sMode	= 2;
				else if("4".equalsIgnoreCase(modeString))
					sMode	= 4;
				surChargeDOB.setShipmentMode(sMode);
				surChargeDOB.setSurchargeDesc(request.getParameter("surcharge_Desc"));
				surChargeDOB.setSurchargeid(request.getParameter("surchargeid"));
				delStausCount	=	surChargeDAO.deleteSurCharges(surChargeDOB);
				if(delStausCount==5)
				{
			       errorMessageObject = new ErrorMessage("For Surcharge '"+surChargeDOB.getSurchargeid()+"' Has Already Linked With BR. ","QMSSurchargesController?Operation="+operation); 
			        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			        keyValueList.add(new KeyValue("Operation",operation)); 	
			        errorMessageObject.setKeyValueList(keyValueList);
			        request.setAttribute("ErrorMessage",errorMessageObject);
			        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
				}
				else
				{
			       errorMessageObject = new ErrorMessage("Surcharge '"+surChargeDOB.getSurchargeid()+"' Successfully Deleted. ","QMSSurchargesController?Operation="+operation); 
			        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			        keyValueList.add(new KeyValue("Operation",operation)); 	
			        errorMessageObject.setKeyValueList(keyValueList);
			        request.setAttribute("ErrorMessage",errorMessageObject);
			        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
				}
				}catch(Exception e){
				       errorMessageObject = new ErrorMessage("Some Thing Has Happend, Please Check The Data. ","QMSSurchargesController?Operation="+operation); 
				        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
				        keyValueList.add(new KeyValue("Operation",operation)); 	
				        errorMessageObject.setKeyValueList(keyValueList);
				        request.setAttribute("ErrorMessage",errorMessageObject);
				        doFileDispatch(request,response,"ESupplyErrorPage.jsp");

				}

		
			}
			else if("View".equalsIgnoreCase(operation)){
				doFileDispatch(request, response, "etrans/QMSSurChargesMasterEnterId.jsp?Operation=View");
			}

		} catch (Exception e) {
			e.printStackTrace();
		      errorMessageObject = new ErrorMessage("Surcharge Is Not Inserted","QMSSurchargesController?Operation="+operation); 
		        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
		        keyValueList.add(new KeyValue("Operation",operation)); 	
		  
		        errorMessageObject.setKeyValueList(keyValueList);
		        request.setAttribute("ErrorMessage",errorMessageObject);
		        try {
					doFileDispatch(request,response,"ESupplyErrorPage.jsp");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ServletException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 

		} 
	
}

	public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)throws IOException, ServletException
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
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ejb.ObjectNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityHome;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitity;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityPK;
import com.foursoft.etrans.setup.vendorregistration.ejb.sls.VendorRegistrationSessionHome;
import com.foursoft.etrans.setup.vendorregistration.ejb.sls.VendorRegistrationSession;
import com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;
import com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;
import com.foursoft.etrans.common.bean.Address;
import com.foursoft.etrans.setup.codecust.bean.CodeCustModelDOB;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;

/**
 * File					: ETVendorRegistrationControleer
 *
 * @author			: Nageswara Rao .D
 * @date				: 20-01-2003(DD/MM/YYYY)
 */

/**
 *
 *	This Controller is used for Managing Vendor related Activities
 *
 *	Common Utility methods
 *
 */
public class ETVendorRegistrationController extends HttpServlet 
{
    private static final String FILE_NAME = "ETVendorRegistrationController";
	private  InitialContext initialContext	= null; 
	VendorRegistrationSessionHome  home				=	null;
	VendorRegistrationSession			 remote				=	null;
	private static Logger logger = null;
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(ETVendorRegistrationController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
		  doPost(request,response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
		String								operation			=	null;
		String								subOperation		=	null;
		ESupplyGlobalParameters	loginbean			= null;
		HttpSession 			    session 		= 	request.getSession();
		operation						=	request.getParameter("Operation");
		subOperation					=	request.getParameter("SubOperation");

		loginbean						= (ESupplyGlobalParameters)session.getAttribute("loginbean");
		//loginbean						= new ESupplyGlobalParameters();
		if(operation!=null )
		{
			if(operation.equals("Add"))
				doGenerateVendor(loginbean,request,response);
			if((operation.equals("Modify")&&(subOperation.equals("modify") || subOperation.equals("Accounts")))||
				(operation.equals("View")&&(subOperation.equals("view") || subOperation.equals("Accounts")))||
				(operation.equals("Delete")&&(subOperation.equals("delete") || subOperation.equals("Accounts"))))
				dogetVendorDetails(loginbean,request,response);
			if(operation.equals("Modify")&&subOperation.equals("update"))
				doUpdateVendorDetails(request,response);
			if(operation.equals("Delete")&&subOperation.equals("remove"))
				doDeleteVendorDetails(request,response);

		}
   }
private void doGenerateVendor(ESupplyGlobalParameters loginbean,HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
{
		VendorRegistrationJava	vendorRegistrationJava	=	null;
		Address							addressObj					=	new  Address();
		String							operation							=	null;
		String							subOperation					=	null;
		String							vendorId							=	null;
		String							message							=	null;
		String							nextNavigation				=	"";			
		String  							errorCodes						=	null;
		int								vendorAddressOID		=	0;
		String							errorMessage					=	"";
		ArrayList						keyValueList					= new ArrayList();
		StringBuffer					errorMessage1					=	null;
		ErrorMessage				errorMessageObject		= null;
		String							countryId						=	null;
		HttpSession					session							=	null;
		session																=	request.getSession();
		String							carrierId			=	null;
		operation															=	request.getParameter("Operation");
		subOperation														=	request.getParameter("SubOperation");
		String	moduleType														=	request.getParameter("moduleType");
		
		vendorRegistrationJava										=	new VendorRegistrationJava();

		VendorRegistrationSessionHome  home				=	null;
  		VendorRegistrationSession			 remote				=	null;
		OIDSessionHome oidHome									= null;
		OIDSession oidRemote										= null;
		nextNavigation													="ETVendorRegistrationController?Operation="+operation;
    CodeCustModelDOB	codeCustDOB	=	new CodeCustModelDOB();
		try
		{
			if( operation.equals("Add") && (subOperation == null || subOperation.equals("Accounts")) )
			{
				session.removeAttribute("VendorDetails");
				if(subOperation == null)
					subOperation	=	"Idcreate";
				doDispatcher(request, response, "etrans/ETVendorRegistration.jsp?Operation="+operation+"&SubOperation="+subOperation);
			}
			else if( operation.equals("Add") && (subOperation.equals("Idcreate") || subOperation.equals("Accounts")) )
			{

				vendorRegistrationJava		=	doVendorRequest(request,vendorRegistrationJava);
				initialContext   				=  new InitialContext();
				oidHome					  = (OIDSessionHome)initialContext.lookup("OIDSessionBean");
				oidRemote					= (OIDSession)oidHome.create();
		//		vendorId					=	oidRemote.getVendorOID();
        try{
					String locationId			=	loginbean.getLocationId();
					locationId					=	locationId.trim();
					if(locationId.length() <= 4)
						codeCustDOB.locationId	=	locationId.substring(0,3);
					else if (locationId.length() == 6)
						codeCustDOB.locationId	=	locationId.substring(3,6);         
					codeCustDOB.companyId		=	vendorRegistrationJava.getAbbrName();        
					vendorId					= 	String.valueOf(oidRemote.getCodeCustomisationId("ETSVENDORID",codeCustDOB));
                 
				}catch(CodeCustNotDoneException ce){
  				//Logger.info(FILE_NAME,"setAddVendorDetails() codecust" +ce,ce);
          logger.info(FILE_NAME+"setAddVendorDetails() codecust" +ce,ce);
					//Logger.info(FILE_NAME,"11111:	"+ vendorId.trim().length());

					if( vendorId == null){
						errorMessageObject = new ErrorMessage( "Code Customization not done for ETSVENDORID" ,"ETTVendorMasterAdd.jsp"); 
						keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
						keyValueList.add(new KeyValue("Operation","Add")); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
					}
				}
				vendorAddressOID		=	oidRemote.getAddressOID();
				home						 =	(VendorRegistrationSessionHome)initialContext.lookup("VendorRegistrationSessionBean");
				remote						=	(VendorRegistrationSession)home.create();
				if(vendorId!=null)
				{
					vendorRegistrationJava.setVendorId(vendorId);
					addressObj =  vendorRegistrationJava.getAddressObj();
					addressObj.setAddressId(vendorAddressOID);
					vendorRegistrationJava.setAddressObj(addressObj);
						
				}
				session.removeAttribute("vendorErrors");
				addressObj									=	vendorRegistrationJava.getAddressObj();
				countryId									=	addressObj.getCountryId();
				carrierId										=	vendorRegistrationJava.getCarrierId();
				errorMessage1							=	remote.getValidityFields(carrierId,countryId);
				if(errorMessage1.length()>0)
				{		
					session.setAttribute("vendorErrors",errorMessage1);
					session.setAttribute("VendorDetails",vendorRegistrationJava);
				doDispatcher(request,response,"etrans/ETVendorRegistration.jsp?Operation="+operation+"&SubOperation="+subOperation);
				}
				else
				{
					message					=	remote.insertVendorDetails(vendorRegistrationJava,loginbean);
					if(message!=null)
					{
						session.removeAttribute("VendorDetails");
						errorMessage = "Record Successfully Added with  vendor Id :" + vendorId ;
						if(moduleType!=null && moduleType.equals("Accounts"))
								subOperation="Accounts";
						nextNavigation=nextNavigation+"&Type="+moduleType;
						errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
						keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
						keyValueList.add(new KeyValue("Operation",operation)); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
						doDispatcher(request,response,"ESupplyErrorPage.jsp");
					}
					else
					{
						session.removeAttribute("VendorDetails");
						errorMessage = "Error occured while generating Vendor . ";
						errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
						keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
						keyValueList.add(new KeyValue("Operation",operation)); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
						doDispatcher(request,response,"ESupplyErrorPage.jsp");
					}
			}
		}

		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				errorMessageObject = new ErrorMessage("Error occured while generating Vendor Registration. ",nextNavigation); 
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				doDispatcher(request,response,"ESupplyErrorPage.jsp");
				return;

		}

}

private	 void	 dogetVendorDetails(ESupplyGlobalParameters loginbean,HttpServletRequest request,HttpServletResponse 
response) throws IOException,ServletException
{
	String											terminalId						=	null;
	String											operation						=	null;
	String											subOperation					=	null;
	String											isValid							=	null;
//	VendorRegistrationSessionHome		home							=	null;
//	VendorRegistrationSession				remote							=	null;
	VendorRegistrationJava					vendorRegistrationJava	=	null;
	HttpSession									session							= null;
	ErrorMessage									errorMessageObject		= null;
	ArrayList										keyValueList					= new ArrayList(); 
	VendorRegistrationEnitityPK				pkObj							=	new VendorRegistrationEnitityPK();
	String											vendorId						=	request.getParameter("vendorId");
	terminalId										=	request.getParameter("terminalId");
	operation										=	request.getParameter("Operation");
	subOperation									=	request.getParameter("SubOperation");
//@@ Modified By Ravi Kumar to Resolve the Issue SPETI-5895 on 26-04-2005
	String vendAcctOperation        =	request.getParameter("vendAcctOperation");	
//@@ 26-04-2005
	session											= request.getSession();
	try
	{
			pkObj.vendorId					=	vendorId;
			initialContext					=	new InitialContext();
			home							=	(VendorRegistrationSessionHome)initialContext.lookup("VendorRegistrationSessionBean");
			remote							=	(VendorRegistrationSession)home.create();
			vendorRegistrationJava			=	remote.isValidIdGetData(vendorId,terminalId,subOperation,vendAcctOperation); //Modified By Ravi Kumar on  26-04-2005
	}
	catch(ObjectNotFoundException ex)
	{
		isValid	=	ex.getMessage();
		//Logger.error(FILE_NAME,"doPost[]--->","Exception While Jndi Lookup of VendorRegistrationSessionbean -->" +ex.toString());
    logger.error(FILE_NAME+"doPost[]--->"+"Exception While Jndi Lookup of VendorRegistrationSessionbean -->" +ex.toString());
		ex.printStackTrace();
	}
	catch(Exception ex)
	{
		//Logger.error(FILE_NAME,"doPost[]--->","Exception While Jndi Lookup of VendorRegistrationSessionbean -->" +ex.toString()s);
    logger.error(FILE_NAME+"doPost[]--->"+"Exception While Jndi Lookup of VendorRegistrationSessionbean -->" +ex.toString());
		ex.printStackTrace();
	}
	if(isValid!=null)
	{
		errorMessageObject = new ErrorMessage(isValid,"etrans/ETVendorRegistrationEnterId.jsp?Operation="+operation+"&SubOperation="+subOperation);
		keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		doDispatcher(request,response,"ESupplyErrorPage.jsp");
	}
	else
	{
		session.setAttribute("VendorDetails",vendorRegistrationJava);
		doDispatcher(request,response,"/etrans/ETVendorRegistration.jsp?Operation="+operation+"&SubOperation="+subOperation);
		return;
	}


}
private void doUpdateVendorDetails(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
{
	String								vendorId						=	request.getParameter("vendorId");
	String								returnMessage				=	null;
	String								operation						=	null;
	String								subOperation					=	null;
	String								errorMessage					= null;
	ErrorMessage					errorMessageObject		= null;
	ArrayList							keyValueList					= new ArrayList(); 	
	StringBuffer					errorMessage1					=	null;
	String							countryId						=	null;
	HttpSession					session							=	null;
	session																=	request.getSession();
	String							carrierId			=	null;
	VendorRegistrationJava					vendorRegistrationJava	=	new VendorRegistrationJava();
	Address							addressObj					=	new  Address();
	operation							    		=	request.getParameter("Operation");
	subOperation								  	=	"modify";
//@@ Added By Ravi Kumar to Resolve the Issue SPETI-5895 on 26-04-2005
	String vendAcctOperation =	request.getParameter("vendAcctOperation");
	String		nextNavigation				= "	etrans/ETVendorRegistrationEnterId.jsp?Operation="+operation+"&SubOperation="+subOperation+"&vendAcctOperation="+vendAcctOperation; //Modified By Ravi Kumar
//@@  26-04-2005
	try
	{
		vendorRegistrationJava	=	doVendorRequest(request,vendorRegistrationJava);
		vendorRegistrationJava.setVendorId(vendorId);
		initialContext					=  new InitialContext();
		home							=	(VendorRegistrationSessionHome)initialContext.lookup("VendorRegistrationSessionBean");
		remote							=	(VendorRegistrationSession)home.create();

		session.removeAttribute("vendorErrors");
		addressObj									=	vendorRegistrationJava.getAddressObj();
		countryId									=	addressObj.getCountryId();
		carrierId										=	vendorRegistrationJava.getCarrierId();
		errorMessage1							=	remote.getValidityFields(carrierId,countryId);
		if(errorMessage1.length()>0)
		{		
			session.setAttribute("vendorErrors",errorMessage1);
			session.setAttribute("VendorDetails",vendorRegistrationJava);
		doDispatcher(request,response,"etrans/ETVendorRegistration.jsp?Operation="+operation+"&SubOperation="+subOperation);
		}
		else
		{
			returnMessage				=	remote.updateVendorDetails(vendorRegistrationJava);
			if(returnMessage!=null)
			{
				errorMessage = "";
				errorMessage = "Record sucessfully modified with vendor Id : " + vendorId ;
				errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
				keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				doDispatcher(request,response,"ESupplyErrorPage.jsp");
			}
			else
			{
				errorMessage = " Unable to Modify Details for  VendorId " +  vendorRegistrationJava.getVendorId();
				errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				doDispatcher(request,response,"ESupplyErrorPage.jsp");
			}
		}

	}
	catch(Exception ex)
	{
			//Logger.error(FILE_NAME,"doUpdateVendorDetails()-->","Exception while modifying the vendor details"+ex.toString());
      logger.error(FILE_NAME+"doUpdateVendorDetails()-->"+"Exception while modifying the vendor details"+ex.toString());
			ex.printStackTrace();
	}
	
}
private void doDeleteVendorDetails(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
{
	String								vendorId						=	request.getParameter("vendorId");
	String								returnMessage				=	null;
	String								operation						=	null;
	String								subOperation					=	null;
	String								errorMessage					= null;
	ErrorMessage						errorMessageObject		= null;
	ArrayList							keyValueList					= new ArrayList(); 								
	//VendorRegistrationSessionHome		home				=	null;
//	VendorRegistrationSession				remote				=	null;
	operation							    		=	request.getParameter("Operation");
	subOperation								  	=	"delete";

//@@ Added By Ravi Kumar to Resolve the Issue SPETI-5895 on 26-04-2005
	String vendAcctOperation =	request.getParameter("vendAcctOperation");
	String		nextNavigation				= "	etrans/ETVendorRegistrationEnterId.jsp?Operation="+operation+"&SubOperation="+subOperation+"&vendAcctOperation="+vendAcctOperation; //Modified By Ravi Kumar
//@@ 26-04-2005
	try
	{
		initialContext					=  new InitialContext();
		home							=	(VendorRegistrationSessionHome)initialContext.lookup("VendorRegistrationSessionBean");
		remote							=	(VendorRegistrationSession)home.create();
		returnMessage				=	remote.deleteVendorDetails(vendorId);
	}
	catch(Exception ex)
	{
			//Logger.error(FILE_NAME,"doDeleteVendorDetails()-->","Exception while deleting vendor details"+ex.toString());
      logger.error(FILE_NAME+"doDeleteVendorDetails()-->"+"Exception while deleting vendor details"+ex.toString());
			ex.printStackTrace();
	}
	if(returnMessage!=null)
	{
		errorMessage = "";
		errorMessage = "Record sucessfully deleted with vendor Id  " + vendorId ;
		errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		doDispatcher(request,response,"ESupplyErrorPage.jsp");
	}
	else
	{
  //Added by senthil prabhu For SPETI-4578 20051205
		errorMessage = " Unable to Delete Details for VendorId " +  vendorId;
		errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		doDispatcher(request,response,"ESupplyErrorPage.jsp");
	}

}
private	 VendorRegistrationJava doVendorRequest(HttpServletRequest request,VendorRegistrationJava vendorRegistration)
{
		String							subOperation		=	null;
		Address							addressObj		=	new Address();
		String							operation           = request.getParameter("Operation");
		try
		{
			HttpSession session 	= 	request.getSession(true);
			if(request.getParameter("terminalId")!=null)
				vendorRegistration.setTerminalId(request.getParameter("terminalId"));
			if(request.getParameter("abbrName")!=null)
				vendorRegistration.setAbbrName(request.getParameter("abbrName"));
			if(request.getParameter("shipmentMode").length()>0)
				vendorRegistration.setShipmentMode(request.getParameter("shipmentMode"));
			else
				vendorRegistration.setShipmentMode("0");
			if(request.getParameter("companyName")!=null)
				vendorRegistration.setCompanyName(request.getParameter("companyName"));
			if(request.getParameter("contactPerson")!=null)
				vendorRegistration.setContactName(request.getParameter("contactPerson"));
			if(request.getParameter("carrierId")!=null)
				vendorRegistration.setCarrierId(request.getParameter("carrierId"));
			if(request.getParameter("designation")!=null)
				vendorRegistration.setDesignation(request.getParameter("designation"));
			if(request.getParameter("indicator")!=null)
				vendorRegistration.setIndicator(request.getParameter("indicator"));
			if(request.getParameter("address1")!=null)
				addressObj.setAddressLine1(request.getParameter("address1"));
			if(request.getParameter("address2")!=null)
				addressObj.setAddressLine2(request.getParameter("address2"));
			if(request.getParameter("zipCode")!=null)
				addressObj.setZipCode(request.getParameter("zipCode"));
			if(request.getParameter("phoneNo")!=null)
				addressObj.setPhoneNo(request.getParameter("phoneNo"));
			if(request.getParameter("city")!=null)
				addressObj.setCity(request.getParameter("city"));
			if(request.getParameter("state")!=null)
				addressObj.setState(request.getParameter("state"));
			if(request.getParameter("faxNo")!=null)
				addressObj.setFax(request.getParameter("faxNo"));
			if(request.getParameter("countryId")!=null)
				addressObj.setCountryId(request.getParameter("countryId"));
			if(request.getParameter("operationEmailId")!=null)
				vendorRegistration.setOperationMailId(request.getParameter("operationEmailId"));
			if(request.getParameter("notes")!=null)
				vendorRegistration.setNotes(request.getParameter("notes"));
			if(request.getParameter("mailId")!=null)
				addressObj.setEmailId(request.getParameter("mailId"));
			if(operation.equals("Modify")||operation.equals("Delete"))
				addressObj.setAddressId(Integer.parseInt(request.getParameter("addressId")));
      if(request.getParameter("vendRegFlag")!=null)
        vendorRegistration.setVendRegFlag(request.getParameter("vendRegFlag"));

			vendorRegistration.setAddressObj(addressObj);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME ,"[doVendorRequest method]---- " + ex.toString());
      logger.error(FILE_NAME +"[doVendorRequest method]---- " + ex.toString());
		}
	return vendorRegistration;
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
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
      logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}

}
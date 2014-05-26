<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/**	Program Name	: ETTVendorMasterProcess.jsp
	Module Name		: ETrans
	Task			: VendorMaster
	Sub Task		: Process
	Author Name		: Shailendra Chak
	Date Started	: September 22,2001
	Date Completed	: october 4,2001
	Date Modified	:
	Description		: The purpose is to process and set the Vendor Informations to the database as per the operation, Add/Modify/Delete operation.
	Method Summary  : getVendorOID() // to get the generated vendorId by Bean
					  getAddressOID() // to get the generated AddressId by Bean.				  
					  setAddVendorDetails(vendorDtlObj,vendorId,vendorAdd) // to set the vendor Details in database for Add operation. 
					  setModifyVendorDetails(vendorDtlObj)// to set the vendor Details in database for Delete operation.
*/
%>
<%@ page import="
				
				com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome,
				com.foursoft.etrans.common.util.ejb.sls.OIDSession,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.etrans.setup.codecust.bean.CodeCustModelDOB,
				com.foursoft.esupply.common.java.KeyValue,
				 org.apache.log4j.Logger,
				com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException,
				java.util.ArrayList" %>

<jsp:useBean id="vendorDtlObj" scope="request" class="com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor" />
<jsp:setProperty name="vendorDtlObj" property="*"/>
<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" />								

<%!
  private static Logger logger = null;
%>

<%
System.out.println("Haiiiiiiiiiiiiiiiiiiiiiiii:	");
    String FILE_NAME					= "ETTVendorMasterAddProcess.jsp ";
    logger  = Logger.getLogger(FILE_NAME);	
	String vendorId	    		= "";    
	String operation			= "";
	String vendorAdd			= "";
	String country				= "";		
	String ven					= "";
	operation 					= request.getParameter("Operation");
	SetUpSessionHome home = null;
	SetUpSession remote   = null;
	OIDSessionHome home1 = null;
	OIDSession remote1	= null;

    ErrorMessage errorMessageObject = null;
    ArrayList	 keyValueList	    = new ArrayList();

%>
<%
	try 
	{		//try
						
			home = (SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
			remote = (SetUpSession)home.create();				
			country	= vendorDtlObj.getCountryId();			

			CodeCustModelDOB	codeCustDOB	=	new CodeCustModelDOB();
			if(operation.equals("Add"))
			{ 							
				try	
		    	{						
				home1 		= 	(OIDSessionHome)loginbean.getEjbHome("OIDSessionBean");				
				remote1 	= 	home1.create(); 								
				//String ven 	= 	remote1.getVendorOID();
//System.out.println("OriginId:	"+loginbean.getLocationId());
				codeCustDOB.originId	=	loginbean.getLocationId().substring(3,6);
//System.out.println("codeCustDOB.originId:	"+codeCustDOB.originId);
				codeCustDOB.companyId	=	loginbean.getCompanyId();

				 ven	=	remote1.getCodeCustId(loginbean,"VENDOR");
//System.out.println("VEN:	"+ven);
				vendorId	= 	String.valueOf(ven);						

//System.out.println("vendorId:	"+vendorId);
				int vAdd	= 	remote1.getAddressOID();
				vendorAdd	=	String.valueOf(vAdd);			
				}
				catch(CodeCustNotDoneException ce)
				{
					//System.out.println("ven 090909:	"+ven);
					//Logger.info(FILE_NAME,"setAddVendorDetails() codecust" +ce,ce);
					//Logger.info(FILE_NAME,"11111:	"+ vendorId.trim().length());
					if( vendorId.trim().length() == 0)
					{

						errorMessageObject = new ErrorMessage( "Code Customization not done for ETSVENDORID" ,"ETTVendorMasterAdd.jsp"); 
						keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
						keyValueList.add(new KeyValue("Operation","Add")); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
					}
				}
				catch( Exception ex )
				{
					System.out.println("ETTVendorMasterAddProcess.jsp : Add "+ex.toString() );							
				}
			try
			{	
				int flag =remote.setAddVendorDetails(vendorDtlObj,vendorId,vendorAdd);
				if(flag!=0)
				{			
					
					errorMessageObject = new ErrorMessage("Record successfully added with VendorId : "+vendorId,"ETTVendorMasterAdd.jsp"); 
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
					keyValueList.add(new KeyValue("Operation",operation)); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
					
					/**
					String errorMessage = "Record successfully added with VendorId : "+vendorId;
					session.setAttribute("ErrorCode","RSI");
					session.setAttribute("ErrorMessage",errorMessage);
					session.setAttribute("Operation",operation);
					session.setAttribute("NextNavigation","ETTVendorMasterAdd.jsp");  */
%>       
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%	
				} 									
				else
				{ 	
//Logger.info(FILE_NAME,"00000:	"+ vendorId.trim().length());
					if( vendorId.trim().length() == 0)
					{
						//Logger.info(FILE_NAME,"11111:	"+ vendorId.trim().length());
						errorMessageObject = new ErrorMessage( "CodeCustomisationNot done while generating VendorId. ","ETTVendorMasterAdd.jsp");
					}
					else
					{
					errorMessageObject = new ErrorMessage( "Error while adding the record ","ETTVendorMasterAdd.jsp"); 

					}
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Add")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
					
					/**			
					
					String errorMessage = "Error while adding the record ";
					session.setAttribute("Operation", "Add");
					session.setAttribute("ErrorCode","ERR");
					session.setAttribute("ErrorMessage",errorMessage);
					session.setAttribute("NextNavigation","ETTVendorMasterAdd.jsp?Operation="+operation);   */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%			
				} 				 	
			} 		
			catch (Exception e)
			{
				
				errorMessageObject = new ErrorMessage( "Error while adding the record with VendorId : "+vendorId,"ETTVendorMasterAdd.jsp"); 
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
				/**				
				String errorMessage = "Error while adding the record with VendorId : "+vendorId;
				session.setAttribute("Operation", "Add");
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETTVendorMasterAdd.jsp?Operation="+operation);   */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%			
		}
	}
	if(operation.equals("Modify"))
	{	
			try
				{
					vendorId	= vendorDtlObj.getVendorId();	
					vendorAdd	= vendorDtlObj.getVendorAdd();												   
					int flag = remote.setModifyVendorDetails(vendorDtlObj);
					if(flag!=0)
			 		{ 
						errorMessageObject = new ErrorMessage(  "Record successfully modified with VendorId : "+vendorId,"ETTVendorMasterEnterId.jsp"); 
						keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
						keyValueList.add(new KeyValue("Operation",operation)); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
																		
						/**
						String errorMessage = "Record successfully modified with VendorId : "+vendorId;
						session.setAttribute("ErrorCode","RSM");
						session.setAttribute("ErrorMessage",errorMessage);
						session.setAttribute("Operation",operation);
						session.setAttribute("NextNavigation","ETTVendorMasterEnterId.jsp");  */
%>       
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%					}	
					else
			 		{	
						errorMessageObject = new ErrorMessage( "Error while modifying the record with VendorId : "+vendorId,"ETTVendorMasterEnterId.jsp"); 
						keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
						keyValueList.add(new KeyValue("Operation","Modify")); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
												
						/**
						String errorMessage = "Error while modifying the record with VendorId : "+vendorId;
						session.setAttribute("Operation", "Modify");
						session.setAttribute("ErrorCode","ERR");
						session.setAttribute("ErrorMessage",errorMessage);
						session.setAttribute("NextNavigation","ETTVendorMasterEnterId.jsp?Operation="+operation);   */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%							 						 
					}	
				}
				catch (Exception e)
				{
					
					errorMessageObject = new ErrorMessage(  "Error while modifying the record with VendorId : "+vendorId,"ETTVendorMasterEnterId.jsp"); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
									
					/**
					String errorMessage = "Error while modifying the record with VendorId : "+vendorId;
					session.setAttribute("Operation", "Modify");
					session.setAttribute("ErrorCode","ERR");
					session.setAttribute("ErrorMessage",errorMessage);
					session.setAttribute("NextNavigation","ETTVendorMasterEnterId.jsp?Operation="+operation);  */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%							
			}
		}		 	   	  
		if(operation.equals("Delete"))
		{
			try	
			{
				vendorId	= vendorDtlObj.getVendorId();	
				vendorAdd	= vendorDtlObj.getVendorAdd();						
				boolean flag    =  remote.deleteVendorDetails(vendorId,vendorAdd);				
				if(flag)
	   			{	
					
					errorMessageObject = new ErrorMessage(  "Record successfully deleted with VendorId : "+vendorId,"ETTVendorMasterEnterId.jsp"); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation",operation)); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
					
					/**
					String errorMessage = "Record successfully deleted with VendorId : "+vendorId;
					session.setAttribute("ErrorCode","RSD");
					session.setAttribute("ErrorMessage",errorMessage);
					session.setAttribute("Operation",operation);
					session.setAttribute("NextNavigation","ETTVendorMasterEnterId.jsp");   */
%>
					<jsp:forward page="../../ESupplyErrorPage.jsp"/>   
<%
				} 
				else
				{				
					errorMessageObject = new ErrorMessage(  "Error while deleting the record with VendorId : "+vendorId,"ETTVendorMasterEnterId.jsp"); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation",operation)); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
					/**				
					
					String errorMessage = "Error while deleting the record with VendorId : "+vendorId;
					session.setAttribute("Operation", operation);
					session.setAttribute("ErrorCode","ERR");
					session.setAttribute("ErrorMessage",errorMessage);
					session.setAttribute("NextNavigation","ETTVendorMasterEnterId.jsp?Operation="+operation);  */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%
				}	
			}	 
			catch (Exception e)
			{
				
				errorMessageObject = new ErrorMessage(  "Error while deleting the record with VendorId : "+vendorId,"ETTVendorMasterEnterId.jsp"); 
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
				/**
				String errorMessage = "Error while deleting the record with VendorId : "+vendorId;
				session.setAttribute("Operation", operation);
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETTVendorMasterEnterId.jsp?Operation="+operation);   */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%
			}	
		}
	} // try ends here 
	catch(Exception e)
	{
		System.out.println("ETTVendorMasterProcess.jsp: "+e);

		errorMessageObject = new ErrorMessage(  "Error While Accessing this Page","ETTVendorMasterEnterId.jsp"); 
		
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
					

%>       
<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%
	}
%>


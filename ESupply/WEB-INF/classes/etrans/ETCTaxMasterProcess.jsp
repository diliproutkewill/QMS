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
/*
	Program Name	:ETCTaxProcess.jsp
	Module Name		:ETrans
	Task			:TaxMaster
	Sub Task		:Process
	Author Name		:Ushasree.Petluri
	Date Started	:September 11th ,2001
	Date Completed	:September 12th,2001
	Date Modified	:September 11th,2001 by Ushashree.P
	Description		:This File main purpose is to to modify,view,delete the data 
					 from the database based on the parameter that is passed.
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.taxes.bean.TaxMaster,
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue,
				    java.util.ArrayList" %>
					
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:setProperty name = "loginbean" property = "*" />

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCTaxMasterProcess.jsp ";
%>

<%  
	String operation 		= 	request.getParameter("Operation");	// for storing operation
	session.setAttribute("Operation",operation);
	
	String taxId 			= 	null;
	String remarks			=	null;
	String desc  			= 	null;
	
	String chargeId			=   null;
	String surChargeId		=   null;
	String accessLevel		=   null;
	String[] counttyIds		=   null;
	String taxType			=   null;
//	int    taxPer			=	0;
	double  taxPer			=	0.0;
	ArrayList countryList	= 	null;
	String	selTerminalId	=   "";
	
	 
	
	TaxMaster taxMaster		= 	null;
	InitialContext	ictx 	= 	null;					// variable for storing JNDI Initial Context 
    com.qms.setup.ejb.sls.SetUpSessionHome home = null;	//ETransHOSuperUser home reference
   	com.qms.setup.ejb.sls.SetUpSession remote   = null;	//ETransHOSuperUser remote reference
    
	ErrorMessage errorMessageObject = null;
    ArrayList	 keyValueList	    = new ArrayList();

     try	
	 {
	 	String errorMessage = "";			 
		String errorCode    = "";
		ictx 		= 	new InitialContext();
		home 		=	(com.qms.setup.ejb.sls.SetUpSessionHome)ictx.lookup("SetUpSessionBean");
		remote 		= 	home.create();		  	
		taxId 		= 	request.getParameter("taxId");	// for storing taxId
		remarks		=	request.getParameter("remarks"); // for storing remarks
		desc 		=	request.getParameter("desc");   // for storing desc
		selTerminalId=  request.getParameter("selTerminalId");
		
		
		String[] test = request.getParameterValues("countryCodes");
		//System.out.println("the value of test arrayis -->"+test);
		
		if(test !=null)
		{	
			 countryList = new ArrayList();
			//System.out.println("the size of test is==>"+test.length);
			for(int i=0;i<test.length;i++)
			{
				//System.out.println("the value of test is"+test[i]);
				int index = test[i].indexOf("[");
				//System.out.println("the index value is->"+index);
				//System.out.println("the sub string is -->"+test[i].substring(index+1,index+3));
				countryList.add(test[i].substring(index+1,index+3));
			}	
		}		
		//new added fields                        
		chargeId		= request.getParameter("ChargeId");
		surChargeId		= request.getParameter("surChargeId");
		accessLevel		= "OT";//request.getParameter("accessLevel");
		taxType			= request.getParameter("taxType");
		//counttyIds	=   null;
		
		//System.out.println("the chargeid->"+chargeId+":surChargeId-->"+surChargeId+":taxType-->"+taxType);
		
		//System.out.println("THE TAX PER IS --->"+request.getParameter("taxPer"));
		if(request.getParameter("taxPer") !=null)
		taxPer			= Double.parseDouble(request.getParameter("taxPer"));
		
			
		//end of adding the fields
		taxMaster	=	new TaxMaster();
		taxMaster.setTaxId(taxId);
		taxMaster.setDesc(desc);
		taxMaster.setSelectedTerminalId(selTerminalId);
		
		
		if(remarks==null)
		{
			taxMaster.setRemarks(" ");
		}	 
		else
		{
			taxMaster.setRemarks(remarks);
		}
		taxMaster.setTaxPer(taxPer);
		taxMaster.setChargeId(chargeId);
		taxMaster.setSurchargeId(surChargeId);
		taxMaster.setAccessLevel(accessLevel);
		taxMaster.setTaxType(taxType);	
		
					
		
		 			
		if(operation.equals("Add"))
		{	
			String errors= remote.isTaxMasterTaxIdExists(taxId,loginbean,taxMaster);
			
			//System.out.println("THE ERRORS IS -->"+errors);
			//remote.isTaxMasterTaxIdExists(taxId,loginbean,taxMaster)	
			
			/*
			if(errors != null && errors.equals("Correct"))
		 	{
		    	errorMessage 	= "Record already exists with Tax Id  : "+taxMaster.getTaxId();
				errorCode 		= "RAE";
		 	} */
			if(errors!=null && (errors.equals("CorrectData") || errors.equals("Correct")|| errors.equals("")))
			{	
				 if( remote.addTaxMasterDetails(taxMaster,loginbean,countryList))
			 	{
					errorMessage 	= 	"Record successfully added with Tax Id : "+taxMaster.getTaxId();
					errorCode 		=	"RSI";
			 	}   	 		      
			}
			else  
			{
				if(errors !=null && errors.equals("Wrong_TaxId"))
				errorMessage = "Record already exists with Tax Id  : "+taxMaster.getTaxId();
				else if(errors !=null && errors.equals("Wrong_ChargeId"))
				errorMessage = "Please check the Charge Id ";
				else
				errorMessage = errors;
				errorCode 	 = "RAE";
			}		
				errorMessageObject = new ErrorMessage(errorMessage,"ETCTaxMasterAdd.jsp"); 
			    keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
			    keyValueList.add(new KeyValue("Operation",operation)); 	
			    errorMessageObject.setKeyValueList(keyValueList);
			    request.setAttribute("ErrorMessage",errorMessageObject);
				
				
				/**
				session.setAttribute("ErrorCode",errorCode);
				session.setAttribute("Operation",operation);
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETCTaxMasterAdd.jsp");  */
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<% 		  
		} // Add
		else if(operation.equals("Modify"))
		{
		
		   // System.out.println("THIS  IS INSIDE THE MODIFY IN ETCTAXMASTERPROCESS JSP FILE...");
			if(remote.updateTaxMasterDetails(taxMaster,loginbean,countryList))
			{
  				errorMessage 	= "Record successfully modified with Tax Id : "+taxMaster.getTaxId();
				errorCode 		= "RSM";  
			}	  
		 	else
		 	{
				errorMessage 	= "Error while  modifying record with Tax Id : "+taxMaster.getTaxId();
				errorCode 		= "ERR";		 
		 	} 
		    	
		    	//System.out.println("this bla bla.."+errorCode);
				errorMessageObject = new ErrorMessage(errorMessage,"ETCTaxMasterEnterId.jsp"); 
			    keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
			    keyValueList.add(new KeyValue("Operation",operation)); 	
			    errorMessageObject.setKeyValueList(keyValueList);
			    request.setAttribute("ErrorMessage",errorMessageObject);
				
				
				/**
				session.setAttribute("ErrorCode",errorCode);
				session.setAttribute("Operation",operation);
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETCTaxMasterEnterId.jsp");  */
%>
			    <jsp:forward page="../ESupplyErrorPage.jsp" />				
<%
		} // modify
		else if(operation.equals("Delete"))
		{			
			if( remote.deleteTaxMasterDetails(taxMaster,loginbean) )
			{
				errorMessage = "Record successfully deleted with Tax Id : " +taxMaster.getTaxId();
				errorCode    ="RSD";
			} 
			else
			{
			 	errorMessage = "You are not allowed to delete this Tax Id :"+taxMaster.getTaxId()+ ", this Id under usage";
				errorCode    = "ERR";
			}
    	
		      	errorMessageObject = new ErrorMessage(errorMessage,"ETCTaxMasterEnterId.jsp"); 
			    keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
			    keyValueList.add(new KeyValue("Operation",operation)); 	
			    errorMessageObject.setKeyValueList(keyValueList);
			    request.setAttribute("ErrorMessage",errorMessageObject);
				
				/**				
				session.setAttribute("ErrorCode",errorCode);
			    session.setAttribute("Operation",operation);
			    session.setAttribute("ErrorMessage",errorMessage);
			 	session.setAttribute("NextNavigation","ETCTaxMasterEnterId.jsp");  */
%>
			   <jsp:forward page="../ESupplyErrorPage.jsp" />				
<%
	     } // delete	    	 			
	   }catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"Exception in ETCTaxMasterProcess.jsp : ", exp.toString());
      logger.error(FILE_NAME+"Exception in ETCTaxMasterProcess.jsp : "+ exp.toString());
			
			if(operation.equals("Modify") || operation.equals("Delete"))
			     errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","ETCTaxMasterEnterId.jsp"); 
			else
				 errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","ETCTaxMasterAdd.jsp"); 
			
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	
			
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			
			/**
			String errorMessage = "Unable to "+operation+" the record";		      
			session.setAttribute("ErrorCode","ERR");
			session.setAttribute("Operation",operation);
			session.setAttribute("ErrorMessage",errorMessage);
			if(operation.equals("Modify") || operation.equals("Delete"))
  	    		session.setAttribute("NextNavigation","ETCTaxMasterEnterId.jsp");
			else
				session.setAttribute("NextNavigation","ETCTaxMasterAdd.jsp");    */
%>
		   <jsp:forward page="../ESupplyErrorPage.jsp" />				
<%		  
		}
%>

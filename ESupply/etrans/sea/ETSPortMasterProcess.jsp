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
/* Program Name		: ESLocationProcess.jsp
   Module name		: HO Setup
   Task		        : Location
   Sub task			: View,Modify,Delete processes
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   : 
   
   Description      :
     This file is invoked on submission of LocationView.jsp.This file is used to set the  Location details into the FS_FR_LOCATIONMASTER after  
     Modifying the Location details from the screen LocationView.jsp . This file will interacts with LocationMasterSessionBean and then calls the  
     method updateLocationMasterDetails or deleteLocationMaster. 
     details are then set to the respective variables through Object LocationMasterJSPBean.
*/
%>
<%@ page import = "javax.ejb.CreateException,
				javax.naming.InitialContext,
				javax.naming.Context,
				javax.sql.DataSource,
				java.sql.Connection,
				org.apache.log4j.Logger,
				com.foursoft.etrans.sea.common.dao.IsExistsDAO,	
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.esupply.common.java.ErrorMessage,
			    com.foursoft.esupply.common.java.KeyValue,
			    java.util.ArrayList" %>
				
<jsp:useBean id ="portMasterObj" class= "com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean" scope ="session" />
<jsp:setProperty name="portMasterObj" property="*" />
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
	String FILE_NAME = "ETSPortMasterProcess.jsp";
  logger  = Logger.getLogger(FILE_NAME);
	InitialContext initial=new InitialContext();
	Connection con = null;
   try
   {
    // checking for terminalID
    if(loginbean.getTerminalId() == null )
    {
%>
   <jsp:forward page="ESupplyLogin.jsp" />
<%
   }
   else
   {
%>    	
<%
	String operation   = null;    // String to store the type of operation
	String portId      = null;    // String to store locationId
    String description = request.getParameter("description");	
    portMasterObj.setDescription(description);
    //Logger.info(FILE_NAME,"Description is "  + portMasterObj.getDescription());
  
    ErrorMessage errorMessageObject = null;
    ArrayList	 keyValueList	    = new ArrayList();
  
  try
   {
 //      SetUpSessionHome homeObject        = ( SetUpSessionHome )initial.lookup( "SetUpSessionBean" );    // Home interface variable
       SetUpSessionHome homeObject        = ( SetUpSessionHome )loginbean.getEjbHome( "SetUpSessionBean" );
	   SetUpSession     remoteObject      = (SetUpSession)homeObject.create();    // Session bean instance
       operation								   = request.getParameter("Operation");	
       portId									   = request.getParameter("portId"); 	
       if(operation.equalsIgnoreCase("Invalidate"))
	   {
		   java.util.ArrayList dobList=(java.util.ArrayList)session.getAttribute("dobList");
			  String invalidater[]=request.getParameterValues("checkBoxValue");
		   boolean flag=false;

		 

		   for(int i=0;i<dobList.size();++i)
		 {
			   if(invalidater[i].equalsIgnoreCase("false"))
					invalidater[i]="F";
			   else if(invalidater[i].equalsIgnoreCase("true"))
					invalidater[i]="T";
			com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean portDOB=(com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean)dobList.get(i);
			portDOB.setInvalidate(invalidater[i]);
		 }
 			flag=remoteObject.invalidatePortMaster(dobList);
			if( flag )
			{
				String errorMessage = "Record successfully Validated  ";
				session.setAttribute("ErrorCode","RSV");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Invalidate");
				session.setAttribute("NextNavigation","../../Invalidate.jsp?View=portmaster");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%
			}
			else
			{
				String errorMessage = "Recored could updated: ";
				session.setAttribute("ErrorCode","RSU");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Invalidate");
				session.setAttribute("NextNavigation","../../Invalidate.jsp?View=portmaster");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp"/>
<%
			}

	   }
	   else if(operation.equalsIgnoreCase("Add"))
	   {
			try
			{
				con = ((DataSource) initial.lookup("java:comp/env/jdbc/DB")).getConnection();
			}
			catch(Exception exp ) 
			{
				//Logger.error(FILE_NAME,"Error in datasource: "+exp.toString());
        logger.error(FILE_NAME+"Error in datasource: "+exp.toString());
			}
			IsExistsDAO isexistsdao_obj = new IsExistsDAO();
					
		  if(!isexistsdao_obj.isPortIdExists(con,portId))
		  {	portMasterObj.setScheduleD(request.getParameter("scheduleD"));;
			portMasterObj.setScheduleK(request.getParameter("scheduleK"));
		    boolean flag = remoteObject.addPortMasterDetails(portMasterObj,loginbean);	// flag to verify the success of record addition
		    if( flag)
		     {
    	       
			    errorMessageObject = new ErrorMessage("New Port Generated with Port Id : "+portId,"ETSPortMasterAdd.jsp"); 
			    keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			    keyValueList.add(new KeyValue("Operation",operation)); 	
			    errorMessageObject.setKeyValueList(keyValueList);
			    request.setAttribute("ErrorMessage",errorMessageObject);
			   
			   /**
			    String errorMessage = "New Port Generated with Port Id : "+portId;		// String to store error message
			   session.setAttribute("ErrorCode","RSI");
			   session.setAttribute("ErrorMessage",errorMessage);
			   session.setAttribute("Operation",operation);
			   session.setAttribute("NextNavigation","ETSPortMasterAdd.jsp");  */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
   		     }
		    else
		     {
        	   errorMessageObject = new ErrorMessage("Error while generating Port Id : "+portId,"ETSPortMasterAdd.jsp"); 
			   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			   keyValueList.add(new KeyValue("Operation","Add")); 	
			   errorMessageObject.setKeyValueList(keyValueList);
			   request.setAttribute("ErrorMessage",errorMessageObject);
			   
			   
			   
			   /**
			   String errorMessage = "Error while generating Port Id : "+portId;		// String to store error message
			   session.setAttribute("Operation", "Add");
			   session.setAttribute("ErrorCode","ERR");
			    session.setAttribute("Operation",operation);
			   session.setAttribute("ErrorMessage",errorMessage);
			   session.setAttribute("NextNavigation","ETSPortMasterAdd.jsp");   */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
			 }
 		  }
		  else
		  {
			
			errorMessageObject = new ErrorMessage("Record already exists with Port Id : "+portId,"ETSPortMasterAdd.jsp"); 
			keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
											
			/**
			String errorMessage = "Record already exists with Port Id : "+portId;		// String to store error message
			session.setAttribute("Operation", "Add");
			session.setAttribute("ErrorCode","RAE");
			 session.setAttribute("Operation",operation);
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("NextNavigation","ETSPortMasterAdd.jsp");   */
			%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
		  }
	   }
	   if(operation.equalsIgnoreCase("Modify"))
		{
			 portMasterObj.setScheduleD(request.getParameter("scheduleD"));;
			portMasterObj.setScheduleK(request.getParameter("scheduleK"));
		   boolean flag = remoteObject.updatePortMasterDetails(portMasterObj,loginbean);    // flag denoting the success of modification operation
  		   // Checking for successful modification 
  		   if( flag)
	       {
		    errorMessageObject = new ErrorMessage("Port Details successfully modified with Port Id  : "+portId,"ETSPortMasterEnterId.jsp"); 
			keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
			keyValueList.add(new KeyValue("Operation","Modify")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			 
			/**				 
			 String errorMessage = "Port Deatils successfully modified with Port Id : "+portId;    // String to store error message
			 session.setAttribute("Operation", "Modify");
			 session.setAttribute("ErrorCode","RSM");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETSPortMasterEnterId.jsp");  */
%>
			<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
		}
	    else
		   {
		     
			errorMessageObject = new ErrorMessage("Error while modifying the record with Port Id : "+portId,"ETSPortMasterEnterId.jsp"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Modify")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			 
			/** 
			 String errorMessage = "Error while modifying the record with Port Id : "+portId;    // String to store error message
			 session.setAttribute("Operation", "Modify");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETSPortMasterEnterId.jsp");  */
%>
			<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
            }
	   }				
      if(operation.equalsIgnoreCase("Delete"))
	   {
			
		    portId = request.getParameter("portId");
		   	//Logger.info(FILE_NAME,"Port Id is " + portId);
		   	boolean flag = remoteObject.deletePortMasterDetails(portId,loginbean);
		  	//Logger.info(FILE_NAME,"flag is  " + flag);
		  	if(flag)
	         {
			  
			  errorMessageObject = new ErrorMessage("Record successfully deleted with Port Id : "+portId,"ETSPortMasterEnterId.jsp"); 
			  keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
			  keyValueList.add(new KeyValue("Operation","Delete")); 	
			  errorMessageObject.setKeyValueList(keyValueList);
			  request.setAttribute("ErrorMessage",errorMessageObject);
			 /**	 
			 String errorMessage = "Record successfully deleted with Port Id : "+portId ;
			 session.setAttribute("Operation", "Delete");
			 session.setAttribute("ErrorCode","RSD");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETSPortMasterEnterId.jsp");  */
%>
			 <jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
	         }
		    else
		    {
			 
			  errorMessageObject = new ErrorMessage("Sorry unable to Delete this Port Id : "+portId +" because this Port under use","ETSPortMasterEnterId.jsp"); 
			  keyValueList.add(new KeyValue("ErrorCode","MSG")); //@@Modified by subrahmanyam for wpbn id:196133 on 02/Feb/10	
			  keyValueList.add(new KeyValue("Operation","Delete")); 	
			  errorMessageObject.setKeyValueList(keyValueList);
			  request.setAttribute("ErrorMessage",errorMessageObject);
			 
			 
			 /**			 
			 String errorMessage = "Sorry unable to Delete this Port Id : "+portId +" because this Port under use";
			 session.setAttribute("Operation", "Delete");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETSPortMasterEnterId.jsp");  */
%>
			<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
		    }
		 
		}
 	  
 }
 catch(Exception exp)
 {
	 //Logger.error(FILE_NAME,"Error in ETSPortMasterProcess.jsp file : "+exp.toString());
   logger.error(FILE_NAME+"Error in ETSPortMasterProcess.jsp file : "+exp.toString());
	 
	 if(operation.equals("Add"))
	   errorMessageObject = new ErrorMessage("Error while "+operation+"ing the record","ETSPortMasterAdd.jsp"); 
	 else
       errorMessageObject = new ErrorMessage("Error while "+operation+"ing the record","ETSPortMasterEnterId.jsp"); 
	 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	 keyValueList.add(new KeyValue("Operation",operation)); 	
	 errorMessageObject.setKeyValueList(keyValueList);
	 request.setAttribute("ErrorMessage",errorMessageObject);
	 
	 /**
	 session.setAttribute("ErrorCode","ERR");
	 session.setAttribute("ErrorMessage","Error while "+operation+"ing the record");
	 session.setAttribute("Operation",operation);
	 if(operation.equals("Add"))
		 session.setAttribute("NextNavigation","ETSPortMasterAdd.jsp");
	 else
		 session.setAttribute("NextNavigation","ETSPortMasterEnterId.jsp");  */
%>
	 <jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
     }
	 finally
  {
	  try
	{
		if(con!=null)
	  con.close();
	  }catch(Exception e)
	  {
	 //Logger.error(FILE_NAME,"Error in closing connection "+e.toString());
   logger.error(FILE_NAME+"Error in closing connection "+e.toString());
		e.printStackTrace();
	  }
  }
   }
 }
 catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in ETSPortMasterProcess.jsp file : "+e.toString());
  logger.error(FILE_NAME+"Error in ETSPortMasterProcess.jsp file : "+e.toString());
  }
 
%>

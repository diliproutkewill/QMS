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
/*	Programme Name : QMSDesignationProcess.jsp.
*	Module Name    : DHL-QMS.
*	Task Name      : Designation 
*	Sub Task Name  : Modifying or deleting the Designation etc..
*	Author		   : K.NareshKumarReddy
*	Date Started   :6/17/2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.QMSSetUpSession,
					com.qms.setup.java.DesignationDOB,
          com.qms.setup.ejb.sls.QMSSetUpSessionHome,
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue,
				    java.util.ArrayList" %>
					
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;  
	private static final String FILE_NAME	=	"QMSDesignationProcess.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);
  DesignationDOB desiDob=new DesignationDOB();
  
  ErrorMessage errorMessageObject = null;
  ArrayList    keyValueList	      = null;	  
 
 try
 {
       
	   
	   InitialContext initial = new InitialContext();
	   QMSSetUpSessionHome 	QMSSetupUserHome	=	(QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");
	   QMSSetUpSession 		QMSSetupRemote	=	(QMSSetUpSession)QMSSetupUserHome.create();
       
	   String strParam	=request.getParameter("operation");
	   //System.out.println("strParam:"+strParam);

	   if(strParam.equalsIgnoreCase("Add"))
	   {
	  desiDob.setDesignationId(request.getParameter("designationId"));
	   desiDob.setDescription(request.getParameter("description"));
	   desiDob.setLevelNo(request.getParameter("levelNo"));
		java.lang.String message= QMSSetupRemote.designationRegistration(desiDob,"F");

		if(message.equalsIgnoreCase("success"))
		   {
				
			 keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record successfully added with designation Id :"+desiDob.getDesignationId(),"QMSDesignationAdd.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
		     keyValueList.add(new KeyValue("Operation","Add")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 %>
					<jsp:forward page="ESupplyErrorPage.jsp"/>
			<%

		   }
		 else if(message.equalsIgnoreCase("failure"))

		   {
        keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record Already Exist with or Error While Adding designation Id :"+desiDob.getDesignationId(),"QMSDesignationAdd.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		     keyValueList.add(new KeyValue("Operation","Add")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 %>
					<jsp:forward page="ESupplyErrorPage.jsp"/>
			<%
			   	
		   }
			 		 
%>
<%
			}
		if(strParam.equalsIgnoreCase("modify"))
		 {
			desiDob.setDesignationId(request.getParameter("designationId"));
		    desiDob.setDescription(request.getParameter("description"));
			desiDob.setLevelNo(request.getParameter("levelNo"));
			if(QMSSetupRemote. updateDesignationDetails(desiDob))
			 {
			 keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record successfully Updated with designation Id :"+desiDob.getDesignationId(),"QMSDesignationEnterId.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
		     keyValueList.add(new KeyValue("Operation","Modify")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 %>
					<jsp:forward page="ESupplyErrorPage.jsp"/>
			<%

			 }
			 else
			 {
			keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Error while adding  designation Id :"+desiDob.getDesignationId(),"QMSDesignationEnterId.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		     keyValueList.add(new KeyValue("Operation","Modify")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 }
		 }

		 if(strParam.equalsIgnoreCase("Delete"))
		{
			 String designationId=request.getParameter("designationId");
			 if(QMSSetupRemote.deleteDesignation(designationId))
			 {keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record successfully deleted with designation Id :"+designationId,"QMSDesignationEnterId.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
		     keyValueList.add(new KeyValue("Operation","delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);

			 }
			 else
			 {
			keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("You can not Delete this Designation Id : "+designationId+",as this is under use.","QMSDesignationEnterId.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		     keyValueList.add(new KeyValue("Operation","delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 }
%>
					<jsp:forward page="ESupplyErrorPage.jsp"/>
<%

		}
		 if(strParam.equalsIgnoreCase("Invalidate"))
		{
			 ArrayList dobList=(ArrayList)session.getAttribute("dobList");
			 ArrayList dobModList=new ArrayList();
			 String checkBoxValue[]=   request.getParameterValues("checkBoxValue");
			 for(int i=0;i<checkBoxValue.length;++i)
			{
				 DesignationDOB desiDOB=new DesignationDOB();
				 desiDOB=(DesignationDOB)dobList.get(i);
				 if("true".equals(checkBoxValue[i]))
				 {
					desiDOB.setInvalidate("T");
				 }
				 else
				{
					 desiDOB.setInvalidate("F");
				}
				dobModList.add(desiDOB);
			}

			if(QMSSetupRemote.invalidateDesignation(dobModList))
			{keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record successfully Updated :","");
		     keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
		     keyValueList.add(new KeyValue("Operation","delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 %>
					<jsp:forward page="ESupplyErrorPage.jsp"/>
			<%

			 }
			 else
			 {
			keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Error while adding  designation Id :"+desiDob.getDesignationId(),"QMSDesignationEnterId.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		     keyValueList.add(new KeyValue("Operation","delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 }
		}
 }
 catch(Exception exp)
 {
			 //Logger.error(FILE_NAME,"Error in QMSDesignationProcess.jsp file s",exp.toString());
       logger.error(FILE_NAME+"Error in QMSDesignationProcess.jsp file "+exp.toString());
			 keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record already exists with Country Id :"+ desiDob.getDesignationId(),"ETCCountryAdd.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
		     keyValueList.add(new KeyValue("Operation","Add")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
%>
<%
 }
%>


     

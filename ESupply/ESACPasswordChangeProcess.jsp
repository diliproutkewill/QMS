<%--
 % 
 % Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 % This software is the proprietary information of FourSoft, Pvt Ltd.
 % Use is subject to license terms.
 %
 % esupply - v 1.x
 %
 --%>
<%--
 % ESACUsersViewAll.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to change the User's Password
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import="com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
				com.foursoft.esupply.delegate.UserRoleDelegate,
                com.foursoft.esupply.common.exception.FoursoftException,
				org.apache.log4j.Logger,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%!
		String fileName	= "ESACPasswordChangeProcess.jsp";
    private static Logger logger = null;
%>
<%
  logger  = Logger.getLogger(fileName);
  String   language = loginbean.getUserPreferences().getLanguage();
  %>  
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
	String							userId		= "";
	String							oldPassword	= "";
	String							newPassword	= "";
	String							locationId	= "";
	String							mailword	="";
	int								status		= 0;

	//String							errorMessage = bundle.getBundle().getString("100074");
	String							errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100074");
	
	UserRoleDelegate userDelegate = null;
    try
    {
        userDelegate = new UserRoleDelegate();
    }
    catch(FoursoftException exp)
    {
       // errorMessage = bundle.getBundle().getString("100240")+exp.getErrorCode();
        errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();


    }

	userId		= request.getParameter("userId");
	oldPassword	= request.getParameter("oldPassword");
	newPassword	= request.getParameter("newPassword");		
	locationId	= request.getParameter("locationId");
	mailword	= request.getParameter("eMailNotification");

	/*
	* It will send the userId, locationd, oldPassword and newPassword
	* If the oldPassword is wrong it send the satatus as 3
	* if change is success it will send satatus as 2
	* if there is any other error, it will send status as 1
	* based on the status, user will be displayed with appropriate messages
	*/
	try
	{
		status = userDelegate.changePassword(userId, locationId, oldPassword, newPassword,mailword);
		//status = remote.changePassword(userId, locationId, oldPassword, newPassword);
		
	}
	catch(FoursoftException ex)
	{
		//Logger.error(fileName,"  Error in changing the Password ",ex);
    logger.error(fileName+"  Error in changing the Password "+ex);
	}		
	if(status == 2)
	{
		//errorMessage = bundle.getBundle().getString("100237");
		errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100237");
	}
	else if(status == 1)
	{
		//errorMessage = bundle.getBundle().getString("100089");
		errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100089");
	}
	else
	{
		//errorMessage = bundle.getBundle().getString("100239");
		errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100239");
	}
	ErrorMessage errMsg = new ErrorMessage(errorMessage, "ESACPasswordChangeView.jsp");
	request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />

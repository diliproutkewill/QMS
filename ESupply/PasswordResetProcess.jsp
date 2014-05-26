<%--
 % 
 % Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 % This software is the proprietary information of FourSoft, Pvt Ltd.
 % Use is subject to license terms.
 %
 % esupply - v 1.x 
 %
 --%>

<%@ page import="com.foursoft.esupply.common.exception.FoursoftException,com.foursoft.esupply.common.java.ErrorMessage, com.foursoft.esupply.delegate.UserRoleDelegate, com.foursoft.esupply.common.java.LookUpBean,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
	
<%
	   String language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>
<%!
	String fileName="PasswordResetProcess.jsp";
%>
<%
    
    String locationId = request.getParameter("locationId");
    String userId = request.getParameter("userId");
	String pwd = request.getParameter("password");
	String emailN = request.getParameter("eMailNotification");

    String email = "";
    String status = "";

    UserRoleDelegate userDelegate = null;
    
    try
    {
    userDelegate = new UserRoleDelegate();
	email = userDelegate.resetUserPassword(userId,locationId,pwd,emailN);
	//status = bundle.getBundle().getString("100265")+userId+bundle.getBundle().getString("100266")+email;
    status = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100265")+email;
	}
    catch(FoursoftException exp)
    {
       
		String errorMessage = "There is no User with Location '"+locationId+"'";
        ErrorMessage errMsg = new ErrorMessage(errorMessage, "PasswordReset.jsp",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }
    
%>
<fmt:message key="100263" bundle="${lang}"/>
</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT LANGUAGE="JavaScript">
function fun(){
window.location.href="PasswordReset.jsp"
}
</SCRIPT>
</head>
<body>
<form method="post" >
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
        <table width="100%" class='formlabel' border="0">
          <tr valign="top" class='formlabel'> 
            <td class="formlabel" colspan=2 ><fmt:message key="100264" bundle="${lang}"/></td>
			<td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"")%></td>
          </tr>	
		</table>
	
	    <br>
	
	    <table width="100%" cellpadding="1" cellspacing="1" border="0">  
          <tr valign="top" class='formdata'> 
			<td>
				<%=status%>
            </td>
          </tr>		  
          <tr valign="top"> 
            <td width="50%" align="right">
				<input type=button name="continue" value='<fmt:message key="7777" bundle="${lang}"/>' onClick='fun()' class='input'> 
            </td>
          </tr>
        </table>
	  <td>	  
    </tr>
  </table>
</form>
</body>
</html>

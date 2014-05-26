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
 % File			: SecuritySettingsUpdate.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This file defines password duration in days per application
 % 
 % author		: Madhu. V
 % date			: 03-11-2003
--%>
<%@ page import="com.foursoft.esupply.common.exception.FoursoftException,com.foursoft.esupply.common.util.BundleFile,com.foursoft.esupply.common.java.ErrorMessage, com.foursoft.esupply.delegate.UserRoleDelegate, com.foursoft.esupply.common.java.LookUpBean,java.util.ArrayList,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean  id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
	String fileName="SecuritySettingsUpdate.jsp";	
%>
<%
	  String  language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
    
    ArrayList arrayList = new ArrayList();
	arrayList.add(new String(request.getParameter("pwdDuration")));
	arrayList.add((String)request.getParameter("pwdMinlen"));
	arrayList.add((String)request.getParameter("pwdMaxlen"));
	arrayList.add((String)request.getParameter("pwdCombination"));
	arrayList.add((String)request.getParameter("pwdAttempts"));
	arrayList.add((String)request.getParameter("previousPwds"));


    UserRoleDelegate userDelegate = null;
    BundleFile label = (BundleFile) session.getAttribute("label");
    
    try
    {
        userDelegate = new UserRoleDelegate();
		userDelegate.updateSecuritySettings(arrayList);
		
    }
    catch(FoursoftException exp)
    {
        String errorMessage = "Error in Updation "+exp.getErrorCode();
        ErrorMessage errMsg = new ErrorMessage(errorMessage, "SecuritySettingsUpdate.jsp",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }
    
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>
</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT LANGUAGE="JavaScript">
function fun(){
window.location.href="SecuritySettings.jsp";
}
</SCRIPT>
</head>
<body>
<form method="post" >
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
        <table width="100%" class='formlabel' border="0">
          <tr> 
            <td class="formlabel" colspan=2 ><fmt:message key="100264" bundle="${lang}"/></td>
            <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"")%></td>
          </tr>	
		</table>
	    <br>
	
	    <table width="100%" cellpadding="1" cellspacing="1" border="0">  
          <tr valign="top" class='formdata'> 
			<td><fmt:message key="100273" bundle="${lang}"/></td>
          </tr>		  
          <tr valign="top"> 
            <td width="50%" align="right">
				<input type=button name="continue" value='<fmt:message key="7777" bundle="${lang}"/>' onClick="javascript:fun()" class='input'> 
            </td>
          </tr>
        </table>
	  <td>	 
    </tr>
  </table>
</form>
</body>
</html>
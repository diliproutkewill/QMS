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
 % ESupplyAcccessDenied.jsp
 % sub-module	: AccessControl
 % module			: esupply
 %
 % This is the UI for Unothorised Access 
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import="java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
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
<title><fmt:message key="100315" bundle="${lang}"/></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
<%@ include file="/ESEventHandler.jsp" %>
</head>
<body >
<form method="get" action="">
  <table width="760" border="0" cellspacing="1" cellpadding="1" bgcolor="ffffff" >
          <tr class="formlabel" > 
            <td>
              &nbsp;<fmt:message key="100315" bundle="${lang}"/>
            </td>
          </tr>
          <tr>
            <td bgcolor="#FFFFFF">&nbsp;</td>
          </tr>
          <tr valign="top" class="formdata"> 
            <td >&nbsp;<b><fmt:message key="100029" bundle="${lang}"/></b><br>
              <fmt:message key="100030" bundle="${lang}"/> <input type="button"  value='<fmt:message key="8891" bundle="${lang}"/>' name="B1" onClick="javascript:history.go(-1);" class="input">
          </tr>
          <tr valign="top" > 
            <td> 
              <p style="line-height: 100%; word-spacing: 0; margin-top: 0; margin-bottom: 0"><font size="2" face="Verdana"><b><fmt:message key="100031" bundle="${lang}"/></b><fmt:message key="100032" bundle="${lang}"/><b> <br>
              </b></font>
            </td>
          </tr>
        </table>
</form>
<br>
</body>
</html>

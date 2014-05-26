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
 % File			: ESACUsersViewAll.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the all Users information and their roles
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
				com.foursoft.esupply.accesscontrol.java.UserModel,
				com.foursoft.esupply.accesscontrol.java.UserRoleDOB,
				org.apache.log4j.Logger,
				java.util.ArrayList,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>        
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
	String fileName		= "ESACUsersViewAll.jsp";
	String title		= "Users - View All";	
%>
<%
    logger  = Logger.getLogger(fileName);
	  String  language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<html>
<head>
<title><fmt:message key="100292" bundle="${lang}"/></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<style type="text/css">
tr.report {
	background-color: #f2f2f9;
	border-width : thin;
	font-family : Verdana, Geneva, Arial, Helvetica, sans-serif;
	font-size : 10px;
}
tr.report1 {
	background-color: #E7F2F5;
	border-width : thin;
	font-family : Verdana;
	font-size : 10px;
}
tr.heading {
	background-color: #b3b3d9;
	border-width : thin;
	font-family : Verdana;
	font-size : 11px;
	font-weight : bold;	
}
</style>
<% 
if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ETRANS))
{
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
}
%>


<%@ include file="/ESEventHandler.jsp" %>
</head>

<body>
<form>

<table width="800" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>


	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><%= title %></td>
		  <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"ViewAll")%></td>
    	</tr>
	</table>
	
	<table width="100%" cellpadding="3" cellspacing="1" border="0">
<%
	String	errorMessage	= "";
	String	locationId		= "";
	String	accessType		= "";
	
	
	UserRoleDOB						userRoleDOB			= null;
	ArrayList						userList			= null;;
	userList = (ArrayList)request.getAttribute("usersList");
	if(userList != null && userList.size() > 0)
	{
%> 
	    <tr class="formheader" valign="top">
			<td width="9%"><fmt:message key="100011" bundle="${lang}"/></td>
			<td width="9%"><fmt:message key="100012" bundle="${lang}"/></td>
			<td width="12%"><fmt:message key="100206" bundle="${lang}"/></td>
			<td width="10%"><fmt:message key="100293" bundle="${lang}"/></td>
			<td width="10%"><fmt:message key="100010" bundle="${lang}"/></td>			
			<td width="36%"><fmt:message key="100294" bundle="${lang}"/></td>
			<td width="14%"><fmt:message key="100295" bundle="${lang}"/></td>
			<td width="10%">EmpId</td>			
			<td width="36%">DesignationId</td>
			<td width="14%">LevelNo</td>
		
		
		</tr>
<br>
<%	for(int i=0; i < userList.size(); i++)	{
		userRoleDOB	= (UserRoleDOB)userList.get(i);
%>
		<tr valign="top" class="formdata" align="left"> 
			<td width="12%"><%= userRoleDOB.userId %></td>
			<td width="12%"><%= userRoleDOB.locationId %></td>
			<td width="18%"><%= userRoleDOB.userName %></td>
			<td width="12%"><%= userRoleDOB.accessType %></td>
			<td width="21%"><%= userRoleDOB.roleLocationId %>.<%= userRoleDOB.roleId %></td>			
			<td width="21%"><%= userRoleDOB.emailId%></td>
			<td width="25%"><%= ( (userRoleDOB.roleDescription==null || userRoleDOB.roleDescription.equals("null")) ? "" : userRoleDOB.roleDescription) %></td>
	    <td width="12%" nowrap><%= userRoleDOB.empId %></td>
			<td width="21%"><%= userRoleDOB.designationId %></td>			
			<td width="21%"><%= userRoleDOB.levelNo==null?"":userRoleDOB.levelNo%></td>
		</tr>
<%	} %>
		<tr  class="formdata"> 
			<td colspan=6></td>
		</tr>
<%	}
	else
	{
		out.println("<tr  bgcolor='#E5E5E5'><td colspan=6><textarea name=data><fmt:message key=\"100028\" bundle=\"${lang}\"/> </textarea></td></tr>");
	}
%>
		<tr> 
			<td colspan=10 align="right">
      			<input type=button name=continue value='<fmt:message key="7777" bundle="${lang}"/>' onClick="history.go(-1)" class="input">
    		</td>
		</tr>
		
	</table>

  </td>
 </tr>
</table>

</form>
</body>
</html>

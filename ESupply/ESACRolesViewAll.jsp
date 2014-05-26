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
 % File			: ESACRolesViewAll.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the all Roles information 
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters, com.foursoft.esupply.accesscontrol.java.RoleModel, com.foursoft.esupply.accesscontrol.util.UserAccessUtility, org.apache.log4j.Logger, java.util.ArrayList,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>		   
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
	  String  language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<html>
<head>


<title><fmt:message key="100297" bundle="${lang}"/></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<style type="text/css">
tr.report {
	background-color: #f2f2f9;
	border-width : thin;
	font-family : Verdana;
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
<%@ include file="/ESEventHandler.jsp" %>
<% 
/*
if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.EP))
{
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
}*/
%>
</head>
<%!
  private static Logger logger = null;
	String fileName		= "ESACRoleViewAll.jsp";
	String title		= "Roles - View All ";
%>
<%
  logger  = Logger.getLogger(fileName);  
	String	errorMessage	= "";
	String	locationId		= "";
	String	accessType		= "";
	
	locationId	= request.getParameter("locationId");
	accessType	= request.getParameter("accessType");

	//Logger.info(fileName,"Location Id - AccessType :: "+locationId+  " - "+accessType );
	
	RoleModel				roleModel	= null;
	ArrayList         roleList  = (ArrayList)request.getAttribute("roleList");
	
%>				
<body>

<form method="POST" action="" >

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>


	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><%= title %></td>
		  <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"VIEWALL")%></td>
    	</tr>
	</table> 
	
	<table width="100%" cellpadding="3" cellspacing="1" border="0">

<%	if(roleList != null && roleList.size() > 0)	{ %>		 

			<tr class="formlabel" align="left" valign="top" bgcolor="#f0f0f0"> 
				<th width="15%" ><fmt:message key="100012" bundle="${lang}"/></th>
				<th width="15%" ><fmt:message key="100010" bundle="${lang}"/></th>
				<th width="20%" ><fmt:message key="100298" bundle="${lang}"/></th>
				<th width="20%" ><fmt:message key="100293" bundle="${lang}"/></th>
				<th width="30%" ><fmt:message key="100299" bundle="${lang}"/></th>
			</tr>
<br>
<%		String roleModule = "";
	   for(int i = 0; i < roleList.size(); i++)
	   {
			roleModel = (RoleModel)roleList.get(i);
			try
			{
				roleModule	= UserAccessUtility.convertIntoModuleString(Integer.parseInt(roleModel.getRoleModule()) );
%>
			<tr class="formdata" align="left" valign="top" >
				<td><%= roleModel.getLocationId() %></td>	
				<td><%= roleModel.getRoleId() %></td>
				<td><%= roleModule %></td>
				<td><%= (roleModel.getRoleLevel()!=null? roleModel.getRoleLevel():"")%></td>
				<td><%= ( (roleModel.getDescription()==null || roleModel.getDescription().equals("null")) ? "" : roleModel.getDescription()) %></td>
			</tr>
			<%
			}
			catch(NumberFormatException e)
			{
				//Logger.warning(fileName, "This role has got wrong in their ModuleName : "+roleModel.getRoleModule() );
        logger.warn(fileName+ "This role has got wrong in their ModuleName : "+roleModel.getRoleModule() );
			}				
		}
%>
		<tr  class="formdata"> 
			<td colspan=4></td>
		</tr>
<%	
	}
	else
	{%>
		<tr  bgcolor='#E5E5E5'><td colspan=6><fmt:message key="100028" bundle="${lang}"/></td></tr>
	<%}
%>
<tr  > 
		  <td colspan=4><font color="#FF3333"><b><fmt:message key="9999" bundle="${lang}"/></b></font><font face="Verdana" size="1"><fmt:message key="100234" bundle="${lang}"/></font>
		  </td>		
		  <td align="right">			  
      		<input type=button name=continue value='<fmt:message key="7777" bundle="${lang}"/>' onClick="history.go(-1)" class="input">
    	  </td>
		</tr>		
	</table>

  </td>
 </tr>
</table>

</form>
</body>

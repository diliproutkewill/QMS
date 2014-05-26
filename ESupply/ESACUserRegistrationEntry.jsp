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
 % File			: ESACUserRegistrationEntry.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is first UI in registering the User,
 % It displays the AccessType, modules assosiated with the user which is to be created
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
				com.foursoft.esupply.accesscontrol.java.UserAccessConfig,
				com.foursoft.esupply.accesscontrol.util.UserAccessUtility,
				com.foursoft.esupply.accesscontrol.java.UserAccessConfigFactory,
				org.apache.log4j.Logger,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	String fileName = "ESACUserRegistrationEntry.jsp";			
%>
<%
    logger  = Logger.getLogger(fileName);
	  String  language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
	String actionPage	= "";
	String locationId	= "";
	String	accessType	= "";

	String idName		= "";
	String UIName		= "";

	UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();
	String modules[] = accessConfig.getModules();
	//System.out.println(" modules[] ="+modules.length);
	int		moduleIndexes[] = accessConfig.getModuleIndexes();
	session.removeAttribute("userModel");

	locationId		= loginbean.getLocationId();
	accessType		= loginbean.getAccessType();

	actionPage	= "ESACUserRegistrationController.jsp";
%>

<html>
<head>
	<title><fmt:message key="100282" bundle="${lang}"/> </title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
var isNS4 = (navigator.appName=="Netscape")?1:0;

	function stringFilter (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  // Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase();
		}
		input.value = returnString;
	}
	function showLOV(url)
	{
		var	width		=	360;//Modified for new access control 
		var	height		=	270;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		
		var Bars		= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;

		if(Win !=null)
		{
			Win.close();
			Win = open(url,'Doc',Features);
		}		
		else
			Win = window.open(url,'Doc',Features);

		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;

	}
	
	var Win  = null;

	function showLocationIdsLOV()
	{
				
		var locationIdFilter = document.forms[0].locationId.value;
		var accessType = document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;
		var Url			= "ESACLocationIdsLOV1.jsp?accessType="+accessType+"&locationIdFilter="+locationIdFilter;
		showLOV(Url);
	}

	function checkAccessType()
	{
		if(document.forms[0].accessType.selectedIndex == 0)
		{
			alert('<fmt:message key="100560" bundle="${lang}"/>');
			return false;
		}
	}

	function clearLocation(input)
	{
		var cell = document.getElementById("idType");
		document.forms[0].locationId.value = "";
		cell.innerText = "Location Id :"
		
		//if(input.value=="ETCRM")
		//	cell.replaceChild(document.createTextNode("Customer Id :"),cell.firstChild);
		//else 
		//	cell.replaceChild(document.createTextNode("Location Id :"),cell.firstChild);
			if(input.value=="ETCRM")
			cell.innerHTML="Customer Id:<font color=red>*</font>";
			else
			cell.innerHTML="Location Id:<font color=red>*</font>";
	}
	
	function checkForm()
	{
		if(document.forms[0].locationId.value=="")
		{
			alert('<fmt:message key="100561" bundle="${lang}"/>');
			document.forms[0].locationId.focus();
			return false;
		}
		var flag = false;
		/*
		if(document.forms[0].module.checked)
		{
				flag = true;
		}
		else
		{
			for(  i=0;i<document.forms[0].module.length;i++)
			{
			
				if (document.forms[0].module[i].checked )
				{
					flag = true;
					break;
				}
			}
		}
		if( ! flag )
		{
			alert("Select at least one Module ");
			return false;
		}
		*/
	}
</script>
<% 
if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ETRANS))
{
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
}

%>
</head>
<body >
<form method="POST" action="<%= actionPage %>" onSubmit="return checkForm();">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>
   
	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><fmt:message key="100100" bundle="${lang}"/></td>
		  <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"Add")%></td>
    	</tr>
	</table>
	
	<br>
	
	<table width="100%" cellpadding="1" cellspacing="0" border="0">
	
		<tr class="formdata" valign="top">
		  
		  <td width="50%" valign="bottom"><fmt:message key="100283" bundle="${lang}"/><font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font></td>
		  
		  <td id="idType" width="50%" valign="bottom"><fmt:message key="100625" bundle="${lang}"/>:<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font></td>
		  
		  <td rowspan="2" width="33%"><!--Select Module(s): <font face="verdana" size=1 color="#FF0000">*</font><br>
		  
<%		for(int i=0; i < modules.length; i++)	{ %>		
			<input type="checkbox" name="module" value="<%= moduleIndexes[i] %>"><%= UserAccessConfig.getModuleLabel( modules[i] ) %><br>
<%		}
%>-->

		  </td>

		</tr>
		
		<tr class="formdata" valign="top"> 

		  <td>
		  
			<select size="1" name="accessType"  class="select" onChange ="clearLocation(this)">
				<option value="<%= accessType %>" selected><%= UserAccessUtility.getAccessTypeLable(accessType) %></option>
<%--
 %		what are the allowed access types for to create roles by this User
 %		this will taken for the UserAccessConfig by passing his accesss type
--%>
<%      
		String[] allowedAccessTypes = accessConfig.getAccessTypes(loginbean.getAccessType());
		for(int i=0; i < allowedAccessTypes.length; i++)	{	
			if(!accessType.equalsIgnoreCase(allowedAccessTypes[i]) ) {	%>		
				<option value="<%= allowedAccessTypes[i]  %>" ><%= UserAccessUtility.getAccessTypeLable(allowedAccessTypes[i]) %></option>
<%			}	
		}		  %>
        	</select>
		  </td>
		  
		  <td>
			<input type="text" name="locationId"  value = "<%= locationId %>" onBlur="stringFilter(this)" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}" >
			<input type="button" value="..." name="locationIdBtn"  onClick="return showLocationIdsLOV()" class="input">
		  </td>

		</tr>
		
	</table>
	
	<table width="100%" cellpadding="4" cellspacing="1" border="0">
		<tr>
		  <td valign=top>
          	<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
			<font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		  </td>
		  <td colspan=3 align="right">
			<input type="hidden" name="screen_name"  value="user_registration_entry">
			<input type="hidden" name="action" value="add">
			<input type="submit" name="Submit"  value='<fmt:message key="3334" bundle="${lang}"/>' class="input">
			<input type="reset" class="input" value='<fmt:message key="8890" bundle="${lang}"/>' name="submit">
		  </td>
		</tr>
	</table>

  </td>
 </tr>
</table>

</form>
</body>
</html>

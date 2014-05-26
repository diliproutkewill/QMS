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
 % File			: ESACLggedUsers.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This file gives a list of currently logged in Users to a Super User / Admin / Licensee
 % This Super User can remove a Locked User from the Logged In list
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
 % 07/05/2003		Amit Parekh			The User will be shown in the list only if his session is alive
--%>
<%@ page import= "com.foursoft.esupply.common.bean.UserCredentials,java.util.*,
					org.apache.log4j.Logger,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>

<jsp:useBean id="appSettings" class="java.util.Hashtable" scope="application"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>

<%!
	String fileName	= "ESACLoggedUsers.jsp";
  private static Logger logger = null;
%>
<%
      logger  = Logger.getLogger(fileName);
	    String language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
	//String msg = bundle.getBundle().getString("100235");
  String msg = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100235");
  %>  
<html>
<head>	
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>

<script language = "JavaScript">

function validate() {

	var goahead = false;

	var index = document.forms[0].user.selectedIndex;

	var USER = document.forms[0].user[index].text;

	goahead = confirm(" <%=msg%>'"+USER+"'?");

	if(goahead) {
		return true;
	} else {
		return false;
	}
}

</script>
<% 

try {

	if(session!=null && loginbean!=null) {

		String accessType	=	(String)	loginbean.getUserLevel();
		
		if(accessType!=null)
    {
			if(accessType.equals("LICENSEE") || accessType.equals("COMPANY") || accessType.equals("HO_TERMINAL") || accessType.equals("HO"))
      {
			} 
      else
      {
				%><jsp:forward  page="ESupplyAccessDenied.jsp" /><%
			}
		} 
    else
    {
			%><jsp:forward  page="ESACSessionExpired.jsp" /><%
		}

	} else {
		%><jsp:forward  page="ESACSessionExpired.jsp" /><%
	}

	response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
	response.setHeader("Pragma","no-cache"); // HTTP 1.0
	response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
</head>
<body >
<form name="f1" action="ESLogout" method="POST" onSubmit="return validate()">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>

	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><fmt:message key="100236" bundle="${lang}"/></td>
		<td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"")%></td>
    	</tr>
	</table>
	
	<br>
	
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		
		<tr class="formdata" valign="top">
		  <td>
			<select size="1" name="user" class="select">
<%
	synchronized(this) {
		
		Enumeration	keys	=	appSettings.keys();		
		
		while(keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			UserCredentials userCredentials = (UserCredentials) appSettings.get( key );

			String	userId	=	userCredentials.getUserId();
			
			if(userCredentials.getSession() != null) {
%>		
				<option value="<%= key %>" ><%= userId %></option>
<%
			} else {
				appSettings.remove( userCredentials.getSessionId() );
			}
		}
		
	} // sync block
%>
			</select>

			:<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>

		  </td>
		</tr>
		
	</table>

	<table width="100%" cellpadding="4" cellspacing="1" border="0">
		<tr>
		  <td align="left">
          	<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
			<font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		  </td>

		  <td colspan=3 align="right">
			<input type="hidden" value="unlog"  name="from">
			<input type="hidden" name="key">
        	<input type="submit" value='<fmt:message key="5555" bundle="${lang}"/>'  name="B1" class="input">
		  </td>
    	</tr>
	</table>

  </td>
 </tr>
</table>

</form>
</body>
</html>
<%
} catch(Exception e) {
	//Logger.error(fileName, "Error while getting logged out users");
  logger.error(fileName+ "Error while getting logged out users");
	%><jsp:forward  page="ESACSessionExpired.jsp" /><%
}
%>
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
  % File		: ESupplyMessagePage.jsp
  % sub-module	: AccessControl
  % module		: esupply
  %
  % Is used to disply the Error Page
  % It takes Model from the request as ErrorMessage Objects and renders it to develope the view
  % 
  % author		: Madhu. P
  % date			: 12-10-2001
  %
  % Modified History
  % Madhu. P  ---- Added ErrorMessage Object to generate the Message page View
  % Ramakumar.B-----Modified to include error code,componenet details and image to the message page.
  % Date----20/05/2003.	

 --%>
<%@ page import = "com.foursoft.esupply.common.java.ErrorMessage, com.foursoft.esupply.common.java.KeyValue, org.apache.log4j.Logger, com.foursoft.esupply.common.exception.FoursoftException, java.util.ArrayList,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
	
<%!
  private static Logger logger = null;
%>
  
<%
    logger  = Logger.getLogger("ESupplyMessagePage.jsp");
	  String  language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>


<%
	
	ErrorMessage errorMessage = (ErrorMessage)request.getAttribute("errorMessage");
	request.removeAttribute("errorMessage");
	//Logger.info("ESupplyMessagePage.jsp", "Error Message Object : "+errorMessage);
	ArrayList listKeyValues = errorMessage.getKeyValueList();

	String errorCode = errorMessage.getErrorCode();
	String img= "RSI" ;
	String color="black" ;

	if(errorCode != null)
	{
		img = "ERR" ;
		color="red" ;
	}

%>
<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<title><fmt:message key="100311" bundle="${lang}"/></title>

<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>

<%@ include file="/ESEventHandler.jsp" %>
</head>
<body >
<form method="POST" action="<%= errorMessage.getNextNavigation() %>">

  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
	  
        <table width="760" cellpadding="4" cellspacing="1">
		
          <tr valign="top" class='formlabel'> 
            <td colspan="2" height="24"><img border="0" src="images/<%=img%>.gif" width="25" height="25" align="left"> <b><fmt:message key="100312" bundle="${lang}"/> </b></font></td>
          </tr>

<%		  
		if(errorCode != null)
		{
%>
          <tr width="760" valign="top" class='formdata'> 
            <td align="left"> 
              <font color="<%=color%>"><fmt:message key="100313" bundle="${lang}"/> <%=errorCode.trim()%></font>
			</td>
		  </tr> 		
<%	
		}
%>
          <tr width="760" valign="top" class='formdata'>  
            <td align="left"> 
			<font color="<%=color%>"><%=errorMessage.getMessage()%></font>
			</td>
		  </tr>

<%
		if(!FoursoftWebConfig.APPLICATION_MODE.equals("PRODUCTION") && (errorMessage.getComponentDetails() != null))
		{
%>
		  <tr width="760" valign="top" class='formdata'> 
			<td align="left">  
			<font color="<%=color%>"><fmt:message key="100314" bundle="${lang}"/>- <%=errorMessage.getComponentDetails()%></font>
			</td>
		  </tr>	
<% 
		}
%>	
 		  <tr width="760" valign="top">
            <td align="right">
				<input type=submit name="continue" value='<fmt:message key="7777" bundle="${lang}"/>' class='input'> 
            </td>
          </tr>
        </table>
		
<%	if(listKeyValues != null)	{
		KeyValue keyValue = null;
		for(int i=0; i < listKeyValues.size(); i++)		{
			keyValue = (KeyValue)listKeyValues.get(i);
			out.println("<input type=hidden name="+keyValue.getName()+" value="+keyValue.getValue()+" >");
		}
	}

listKeyValues=null;
errorMessage=null;
%>
      </td>
    </tr>
  </table>
  </form>
</body>
</html>

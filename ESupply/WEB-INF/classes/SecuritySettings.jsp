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
 % File			: SecuritySettings.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This file defines password duration in days per application
 % 
 % author		: Madhu. V
 % date			: 03-11-2003
--%>
<%@ page import= "com.foursoft.esupply.common.bean.UserCredentials,java.util.*,com.foursoft.esupply.common.util.BundleFile, org.apache.log4j.Logger,com.foursoft.esupply.common.exception.FoursoftException,com.foursoft.esupply.common.java.ErrorMessage, com.foursoft.esupply.delegate.UserRoleDelegate, com.foursoft.esupply.common.java.LookUpBean,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean  id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%!
  private static Logger logger = null;
	String fileName	= "SecuritySettings.jsp";
 %>
<%
    logger  = Logger.getLogger(fileName);
	   String language = loginbean.getUserPreferences().getLanguage();
		%>
  <fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
  <%
   
	 UserRoleDelegate userDelegate = null;
	ArrayList settings =null;
	 try
    {
        userDelegate = new UserRoleDelegate();
    }
    catch(FoursoftException exp)
    {
        String errorMessage = "Error in Lookup "+exp.getErrorCode();
        ErrorMessage errMsg = new ErrorMessage(errorMessage, "SecuritySettings.jsp",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }


 try
    {
        settings=userDelegate.getSecuritySettings();

    }
    catch(FoursoftException exp)
    {
        String errorMessage = "Error in getSecuritySettings "+exp.getErrorCode();
        ErrorMessage errMsg = new ErrorMessage(errorMessage, "SecuritySettings.jsp",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }
   
%>

<html>
<head>	
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language = "JavaScript">

function validate() 
{

	var duration = document.forms[0].pwdDuration.value;
	var pwdMinlen = document.forms[0].pwdMinlen.value;
	var pwdMaxlen = document.forms[0].pwdMaxlen.value;
	var pwdCombination = document.forms[0].pwdCombination.value;
	var pwdAttempts = document.forms[0].pwdAttempts.value;
	var previousPwds = document.forms[0].previousPwds.value;
	if(duration == "")
	{
		alert('<fmt:message key="100552" bundle="${lang}"/>');
		document.forms[0].pwdDuration.focus();
		return false;
	}
	if(pwdMinlen == "")
	{
		alert('<fmt:message key="100101" bundle="${lang}"/>');
		document.forms[0].pwdMinlen.focus();
		return false;
	}
	if(parseInt(pwdMinlen) ==0)
	{
	alert('<fmt:message key="100108" bundle="${lang}"/>');
		document.forms[0].pwdMinlen.focus();
		return false;
	}
	if(pwdMaxlen == "")
	{
		alert('<fmt:message key="100102" bundle="${lang}"/>');
		document.forms[0].pwdMaxlen.focus();
		return false;
	}
	if(parseInt(pwdMaxlen) < parseInt(pwdMinlen))
	{
		alert('<fmt:message key="100109" bundle="${lang}"/>');
		document.forms[0].pwdMaxlen.focus();
		return false;
	}
	if(pwdAttempts == "")
	{
		alert('<fmt:message key="100103" bundle="${lang}"/>');
		document.forms[0].pwdAttempts.focus();
		return false;
	}
	if(parseInt(pwdAttempts) < 2)
	{
		alert('<fmt:message key="100110" bundle="${lang}"/>');
		document.forms[0].pwdAttempts.focus();
		return false;
	}

	if(parseInt(pwdAttempts)>5)
	{
		alert('<fmt:message key="100106" bundle="${lang}"/>');
		document.forms[0].pwdAttempts.focus();
		return false;
	}

	if(previousPwds == "")
	{
		alert('<fmt:message key="100104" bundle="${lang}"/>');
		document.forms[0].previousPwds.focus();
		return false;
	}
	
	if(parseInt(previousPwds) == 0)
	{
		alert('<fmt:message key="100111" bundle="${lang}"/>');
		document.forms[0].previousPwds.focus();
		return false;
	}
	
	return true;

}

function intValues(input) 
	{
		s = input;
		var i;
		if (isNaN(s))
		{
			alert('<fmt:message key="100614" bundle="${lang}"/>');
			return false;
		}
		return true;
	}
</script>
</head>

<body >
<form name="f1" action="SecuritySettingsUpdate.jsp" method="POST" onSubmit="return validate()">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>
	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><fmt:message key="100087" bundle="${lang}"/></td>
		<td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"")%></td>
    	</tr>
	</table>	
	<br>	
	<table width="100%" cellpadding="1" cellspacing="1" border="0">	
		<tr class="formdata" valign="top">
		  <td width="35%">
		  <font face="Verdana" size="2"><fmt:message key="100275" bundle="${lang}"/><font face="verdana" size=2 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		  </td>
		  <td align="left">
			<input type='text' name='pwdDuration' maxlength='3' value='<%=settings.get(0)%>' size='8' ONKEYPRESS="if((event.keyCode<48)|| (event.keyCode>57))event.returnValue=false" >&nbsp;<font face="Verdana" size="2"><fmt:message key="100624" bundle="${lang}"/>
          </font>
			
		  </td>
		</tr>
		<tr class="formdata" valign="top">
		  <td width="35%">
		  <font face="Verdana" size="2"><fmt:message key="100091" bundle="${lang}"/><font face="verdana" size=2 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		  </td>
		  <td align="left">
			<input type='text' name='pwdMinlen' maxlength='2' value='<%=settings.get(1)%>' size='8' ONKEYPRESS="if((event.keyCode<48)|| (event.keyCode>57))event.returnValue=false" >&nbsp;<font face="Verdana" size="2"><fmt:message key="100096" bundle="${lang}"/>
          </font>
		  </td>
		</tr>
		<tr class="formdata" valign="top">
		  <td width="35%">
		  <font face="Verdana" size="2"><fmt:message key="100092" bundle="${lang}"/><font face="verdana" size=2 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		  </td>
		  <td align="left">
			<input type='text' name='pwdMaxlen' maxlength='2' value='<%=settings.get(2)%>' size='8' ONKEYPRESS="if((event.keyCode<48)|| (event.keyCode>57))event.returnValue=false" >&nbsp;<font face="Verdana" size="2"><fmt:message key="100096" bundle="${lang}"/>
          </font>
		  </td>
		</tr>
		<tr class="formdata" valign="top">
		  <td width="35%">
		  <font face="Verdana" size="2"><fmt:message key="100093" bundle="${lang}"/><font face="verdana" size=2 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		  </td>
		  <td align="left">
			<Select name='pwdCombination'>
			<%if(settings.get(3).equals("1")){ %>
			<option value=1 selected>Enable</option>
			<option value=0>Disable</option>
			<%}else{%>
			<option value=0 selected>Disable</option>
			<option value=1 >Enable</option>
			<% }%>
			</td>
		</tr>
		<tr class="formdata" valign="top">
		  <td width="35%">
		  <font face="Verdana" size="2"><fmt:message key="100094" bundle="${lang}"/><font face="verdana" size=2 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		  </td>
		  <td align="left">
			<input type='text' name='pwdAttempts' maxlength='1' value='<%=settings.get(4)%>' size='8' ONKEYPRESS="if((event.keyCode<48)|| (event.keyCode>57))event.returnValue=false" >&nbsp;<font face="Verdana" size="2"><fmt:message key="100097" bundle="${lang}"/>
          </font>			
		  </td>
		</tr>
		<tr class="formdata" valign="top">
		  <td width="35%">
		  <font face="Verdana" size="2"><fmt:message key="100095" bundle="${lang}"/><font face="verdana" size=2 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		  </td>
		  <td align="left">
			<input type='text' name='previousPwds' maxlength='2' value='<%=settings.get(5)%>' size='8' ONKEYPRESS="if((event.keyCode<48)|| (event.keyCode>57))event.returnValue=false" >&nbsp;<font face="Verdana" size="2"><fmt:message key="100098" bundle="${lang}"/>
          </font>			
		  </td>
		</tr>
	</table>
	<table width="100%" cellpadding="4" cellspacing="1" border="0">
		<tr >
		  <td align="left">
          	<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
			<font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		  </td>
		  <td colspan=3 align="right">
        	<input type="submit" value="Submit"  name="B1" class="input">
			<INPUT TYPE="reset" class="input">
		  </td>
    	</tr>
	</table>
  </td>
 </tr>
</table>
</form>
</body>
</html>
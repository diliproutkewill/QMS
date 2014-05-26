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
 % File			: PasswordReset.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This file gives a list of currently logged in Users to a Super User / Admin / Licensee
 % This Super User can remove a Locked User from the Logged In list
 % 
 % author		: Madhu. V
 % date			: 04-09-2003
--%>
<%@ page import= "com.foursoft.esupply.common.bean.UserCredentials, com.foursoft.esupply.common.util.BundleFile,com.foursoft.esupply.accesscontrol.util.UserAccessUtility,,com.foursoft.esupply.accesscontrol.java.UserAccessConfigFactory,com.foursoft.esupply.accesscontrol.java.UserAccessConfig, java.util.*,com.foursoft.esupply.common.exception.FoursoftException,
com.foursoft.esupply.common.java.ErrorMessage,javax.servlet.jsp.jstl.fmt.LocalizationContext,
org.apache.log4j.Logger,com.foursoft.esupply.delegate.UserRoleDelegate" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="appSettings" class="java.util.Hashtable" scope="application"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!

  private static Logger logger = null;
	String fileName	= "PasswordReset.jsp";  

%>
<%
      logger  = Logger.getLogger(fileName);
	    String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
    String lab1 = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100500");
	String accessType	= "";

	UserRoleDelegate userDelegate = null;
    try
    {
        userDelegate = new UserRoleDelegate();
    }
    catch(FoursoftException exp)
    {
        String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();
        //Logger.error("ESACPasswordChangeView.jsp","Error While Looking Home");
        logger.error("ESACPasswordChangeView.jsp"+"Error While Looking Home");
    }

	ArrayList settings =null;

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
<SCRIPT language="JavaScript" src="sha1.js"></SCRIPT>
<script language = "JavaScript">
var isNS4 = (navigator.appName=="Netscape")?1:0;

function validateCombination(strng){

	var sizechar =strng.value.length;			
	var upassID=strng;
	
	var upass_string = upassID.value;
		
    var valid="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!%?@#$^*()_-+[]\{}|:;,./><";

    for (var i=0; i<sizechar; i++) {
        if (valid.indexOf(upass_string.charAt(i)) < 0) {
            alert('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100086")%>');
			upassID.focus();
            return false;
        }
    }

	var num_valid="1234567890";
	var upper_valid="abcdefghijklmnopqrstuvwxyz";
	var lower_valid="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var spchars_valid="~!%?@#$^*()_-+[]\{}|:;,./><";
	
	var upper =false;
	var lower =false;
	var spchars =false;
	var num =false;
	
    for (var i=0; i<sizechar; i++) 
	{
		if (num_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
		num=true;
		
		}
		else if (upper_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
            lower=true;
			
        }
		else if (lower_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
            upper=true;
			
        }
		else if (spchars_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
            spchars=true;
			
        }

    }
	

	if((upper==true)&&(lower==true)&&(num==true))
	{
	return true;
	}
	else if ((upper==true)&&(lower==true)&&(spchars==true)){
	return true;
	}
	else if ((upper==true)&&(spchars==true)&&(num==true)){
	return true;
	}
	else if ((spchars==true)&&(lower==true)&&(num==true)){
	return true;
	}
	else {
	alert('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100085")%>');
	upassID.focus();
	return false;
	}
 }

	function validate() 
	{
		var locId = document.f1.locationId.value;
		var user  =	document.f1.userId.value;
		
		if(locId==""){
		alert('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100549")%>');
		document.f1.locationId.focus();
		return false;
		}

		if(user==""){
		alert('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100551")%>');
		document.f1.userId.focus();
		return false;
		}
		

		
		if(document.forms[0].password.value.length !=0) {
			var	passWord1	=	document.forms[0].password.value;
			var	passWord2	=	document.forms[0].confirmPassword.value;
			
			if (user.length != 0 && (user.indexOf(passWord1) >= 0 || passWord1.indexOf(user) >= 0)){
			alert('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100107")%>');
			document.forms[0].password.focus();
			return false;
			}	

			if((passWord1.length > <%=settings.get(2)%>)||(passWord1.length < <%=settings.get(1)%>))
			{
			alert(checkEscape('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100084")%> <%=settings.get(1)%> <%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100081")%> <%=settings.get(2)%> <%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100096")%>'));
			document.forms[0].password.focus();
			return false;
			}

			if(passWord1 != passWord2) {
				alert('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100569")%>');
				document.forms[0].confirmPassword.focus();
				return false;
			}else{

				<%
				if(settings.get(3).equals("1")){
				%>
				var check=validateCombination(document.forms[0].password);
				<%}else{%>
				var check=true;
				<%}%>
						
			if(check==true){
			/*document.forms[0].eMailNotification.value=document.forms[0].password.value;
			document.forms[0].password.value=document.forms[0].password.value;
			document.forms[0].confirmPassword.value=document.forms[0].confirmPassword.value;*/
				document.forms[0].eMailNotification.value=mailword(document.forms[0].password.value);
				document.forms[0].password.value=hex_sha1(document.forms[0].password.value);
				document.forms[0].confirmPassword.value=hex_sha1(document.forms[0].confirmPassword.value);
			}else
				return false;

			}
		}else{
			//document.forms[0].eMailNotification.value="tLxLOf";
			document.forms[0].password.value="A1MyGHTY";
			document.forms[0].confirmPassword.value="A1MyGHTY";
		}

		var goahead = false;
		var USER = document.f1.userId.value;

		goahead = confirm("<%=lab1%> '"+USER+"'?");
		
		if(goahead) 
		{
			return true;
		} 
		else 
		{
			document.forms[0].password.value="";
			document.forms[0].confirmPassword.value="";
			return false;
		}
	}

	function stringFilter (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  
		// Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase();
		}
		input.value = returnString;
	}


	function showUserIdsLOV()
	{
		var locId = document.f1.locationId.value;
		var	width		=	360;
		var	height		=	270;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		var filter		=	document.f1.userId.value;

		var Url			= "ESACUserIdsLOV.jsp?accessType=password&locationId="+locId+"&filterString="+filter;
		var Bars		= 'directories=no, location=no,menubar=no,status=no,titlebar=no';		
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';

		if(locId.length == 0)
		{
			alert('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100549")%>');
			document.f1.locationId.focus();
			return false;
		}
		
		var Features	= Bars+' '+Options;
		var Win=open(Url,'Doc1',Features);
		if (!Win.opener) 
			this.Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;

	}
/*
	function openLocWin()
	{
					
		var accessType 		= document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;
		var locationIdFilter = document.forms[0].locationId.value;
		var Url			= "ESACLocationIdsLOV.jsp?accessType="+accessType+"&locationIdFilter="+locationIdFilter;
		showLOV(Url);
	}*/
	function showLocationIdsLOV()
	{
		var	width		=	360;
		var	height		=	270;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		var filter		=	document.f1.locationId.value;
		var accessType 		= document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;

		//var Url			=	"ESACLocationIdsLOV.jsp?accessType=password&locationIdFilter="+filter;
		var Url			= "ESACLocationIdsLOV.jsp?accessType="+accessType+"&locationIdFilter="+filter;
		var Bars		=	'directories=no, location=no,menubar=no,status=no,titlebar=no';		
		var Options		=	'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;
		var Win=open(Url,'Doc1',Features);
		
		if (!Win.opener) 
			this.Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;

	}

	function clearLocation(input)
	{
		var otherid = "userId";
		 //alert(document.f1.elements[otherid].value); Modified For new access Control.

		document.f1.locationId.value = "";
		
		document.f1.elements[otherid].value = "" ;
	
	}

</script>

<% 
UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();

	accessType	= loginbean.getAccessType();
	String locationId = loginbean.getLocationId();
   //accessType = (String) loginbean.getUserLevel();	
	
%>
</head>
<body >
<form name="f1" action="PasswordResetProcess.jsp" method="POST" onSubmit="return validate()">

  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
        <table width="760" border="0" class="formlabel" >
          <tr > 
            <td class="formlabel"><fmt:message key="100022" bundle="${lang}"/></td>
            <td  class="formlabel" align="right"><%=loginbean.generateUniqueId(fileName,"")%></td>
          </tr>
         
       </table>
	   <input type=hidden name="eMailNotification">
	   <br>
	   <table width="760" border="0" cellspacing="1" cellpadding="4">
          <tr class='formdata'> 
		   <td><fmt:message key="100267" bundle="${lang}"/> <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
			<select size="1" name="accessType" class="select" onChange ="clearLocation(this)"  >
				<option value="<%= accessType %>" selected><%= UserAccessUtility.getAccessTypeLable(accessType) %></option>
	<%--
	 % This is used as get the allowed access types by providing his access types
	--%>
<%
		//System.out.println("loginbean.getAccessType() = "+loginbean.getAccessType());
		String[] allowedAccessTypes = accessConfig.getAccessTypes(loginbean.getAccessType());

		for(int i=0; i < allowedAccessTypes.length; i++)	{
			if(!accessType.equalsIgnoreCase(allowedAccessTypes[i]) ) {
%>		
				<option value="<%= allowedAccessTypes[i] %>" ><%= UserAccessUtility.getAccessTypeLable(allowedAccessTypes[i]) %></option>
<%			}	
		}		  
%>
			</select>
		  </td>
            <td><fmt:message key="100268" bundle="${lang}"/><font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" name="locationId" size="16" onBlur = "stringFilter(this)" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}" >
              <input type="button" name="locationIdButton" value="..." onClick="return showLocationIdsLOV()" class='input'>
            </td>
			<td><fmt:message key="100276" bundle="${lang}"/><font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" name="userId" size="16" onBlur = "stringFilter(this)" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}" >
              <input type="button" name="userIdButton" value="..." onClick="return showUserIdsLOV()" class='input'>
            </td>
          </tr>
		  <tr valign="top" class="formdata"> 
		  <td ><fmt:message key="100211" bundle="${lang}"/><br>
			<input type="password" name="password" maxlength="20" size="20" value="" >
		  </td>
		  <td  ><fmt:message key="100212" bundle="${lang}"/><br>
              <input type="password" name="confirmPassword" maxlength="20" size="20" value="" >
		  </td>
		  <td>&nbsp;</td>
		</tr>
          <tr> 
		  <td align="left">
          	<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
			<font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		  </td>
            <td colspan=2 align="right">
				 <input type="submit" name="submit" value='<fmt:message key="6666" bundle="${lang}"/>' class='input'>
              <input type="Reset" class='input'>
             
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>
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
 % ESACPasswordChangeView.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is UI for changing Password, It displays the information about the user who is logged in.
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
 % Modification History
 % 09/05/2003	Amit Parekh		A bug related to password change was fixed. When the User switched his profile
 %								to a facility instaed of Company in EP, this page gave NullPointerexception.
 %								This will now give a message indicating the condition.
--%>
<%@ page import="com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
				com.foursoft.esupply.accesscontrol.java.UserModel,
				org.apache.log4j.Logger,
                com.foursoft.esupply.common.exception.FoursoftException,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
				com.foursoft.esupply.accesscontrol.java.MenuModule,
				java.util.ArrayList,
				com.foursoft.esupply.delegate.UserRoleDelegate,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>

<%!
	String fileName = "ESACPasswordChangeView.jsp";
  private static Logger logger = null;
%>
<%
      logger  = Logger.getLogger(fileName);
	    String  language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
    String	actionPage	= "ESACPasswordChangeProcess.jsp";  
  %>		

	<%try
	{
		if(loginbean.getUserId() == null)
		{
			request.setAttribute("ErrorMessage",((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100241"));
	%>
		<jsp:forward page="ESACSessionExpired.jsp" />
	<%		
		}
	}
	catch(NullPointerException ne)
	{
	}	
	

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

	String userId = loginbean.getUserId();
	String locationId = loginbean.getLocationId();


	/*
	* this will be diplayed the UserInformation of the current user by interacting UserRoleRegistrationSessionBean
	*/
	UserModel	userModel = null;
	String employeeId	=	null;
	String employeeDept	=	null;
	String errorMessage	=	null;
	try
	{
	userModel	= userDelegate.getUserInformation(locationId, userId);
	//userModel	= (UserModel)remote.getUserInformation(locationId, userId);


	if(userModel != null) 
	{
		employeeId		=	userModel.getEmpId();
		employeeDept	=	userModel.getDepartment();
	} 

	}
	catch(FoursoftException exp)
    {
		errorMessage	=	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100242")+"\n\n"+
							((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100243")+
							((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100244");
		ErrorMessage errMsg = new ErrorMessage(errorMessage, "ESACPasswordChangeView.jsp");
		request.setAttribute("errorMessage", errMsg); 

	%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
	<%
    }
	if(employeeId == null)
		employeeId	="";
	if(employeeDept == null)
		employeeDept	=	"";

%>
<html>
<head>
<title><fmt:message key="100245" bundle="${lang}"/></title>
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT language="JavaScript" src="sha1.js"></SCRIPT>
<script language="JavaScript">
//validate combination
 function validateCombination(strng){

	var sizechar =strng.value.length;			
	var upassID=strng;
	
	var upass_string = upassID.value;
		
    var valid="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~%?!@#$^*()_-+[]\{}|:;,./><";

    for (var i=0; i<sizechar; i++) {
        if (valid.indexOf(upass_string.charAt(i)) < 0) {
            alert('<fmt:message key="100086" bundle="${lang}"/>');
			upassID.focus();
            return false;
        }
    }

	var num_valid="1234567890";
	var upper_valid="abcdefghijklmnopqrstuvwxyz";
	var lower_valid="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var spchars_valid="~!@%?#$^*()_+[]\{}|:;,./><";
	
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
	alert('<fmt:message key="100085" bundle="${lang}"/>');
	upassID.focus();
	return false;
	}
	
	}
//
function toLower (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  // Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toLowerCase();
		}
		
		return returnString;
		
	}

function checkForm()
{
	var form1 = document.userreg;
	var user = toLower(form1.userId);
	var passWord1= form1.newPassword.value;
	
	

	if(form1.oldPassword.value=="")
	{
		alert('<fmt:message key="100543" bundle="${lang}"/>');
		form1.oldPassword.focus();
		return false;
	}
	if(form1.newPassword.value=="")
	{
		alert('<fmt:message key="100544" bundle="${lang}"/>');
		form1.newPassword.focus();
		return false;
	}
	if(form1.confirmPassword.value=="")
	{
		alert('<fmt:message key="100545" bundle="${lang}"/>');
		form1.confirmPassword.focus();
		return false;
	}

	if(form1.oldPassword.value==form1.newPassword.value)
	{
		alert('<fmt:message key="100546" bundle="${lang}"/>');
		form1.newPassword.value = "";
		form1.confirmPassword.value = "";
		form1.newPassword.focus();
		return false;
	}
	if((form1.newPassword.value.length > <%=settings.get(2)%>)||(form1.newPassword.value.length < <%=settings.get(1)%>))
	{
			alert('<fmt:message key="100084" bundle="${lang}"/> <%=settings.get(1)%> <fmt:message key="100081" bundle="${lang}"/> <%=settings.get(2)%> <fmt:message key="100096" bundle="${lang}"/>');
			form1.newPassword.focus();
			return false;
	}
		
	if (user.length != 0 && (user.indexOf(passWord1) >= 0 || passWord1.indexOf(user) >= 0))
		{
			alert('<fmt:message key="100107" bundle="${lang}"/>');
			form1.newPassword.focus();
			return false;
		}

	if(form1.confirmPassword.value != form1.newPassword.value)
	{
		alert('<fmt:message key="100547" bundle="${lang}"/>');
		form1.newPassword.value = "";
		form1.confirmPassword.value = "";
		form1.newPassword.focus();
		return false;
	}
	
				<%
				if(settings.get(3).equals("1")){
				%>
				var checkCombination=validateCombination(form1.newPassword);
				<%}else{%>
				var checkCombination=true;
				<%}%>

	if(checkCombination==true){

		if(parent.frames[0].blnIsOpen == false)
		parent.frames[0].OpenClose();
    
	/*form1.oldPassword.value=form1.oldPassword.value;
	form1.eMailNotification.value=form1.newPassword.value;
	form1.newPassword.value =form1.newPassword.value;
	form1.confirmPassword.value =form1.confirmPassword.value;    */

	form1.oldPassword.value=hex_sha1(form1.oldPassword.value);
	form1.eMailNotification.value=mailword(form1.newPassword.value);
	form1.newPassword.value =hex_sha1(form1.newPassword.value);
	form1.confirmPassword.value =hex_sha1(form1.confirmPassword.value);
	return true;
	}else return false;
}
</script>

<% 
if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ETRANS))
{
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
}  
String screenUniqueId = loginbean.generateUniqueId("ESACPasswordChangeView.jsp","");
if(screenUniqueId==null)
	screenUniqueId="";
%>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body  >

<form method="POST" action="<%= actionPage %>" name="userreg" onSubmit="return checkForm();">
<input type=hidden name="eMailNotification">
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td bgcolor="ffffff">
        
        <table width="760" border="0" cellspacing="0" cellpadding="0" >
          <tr height="25"> 
            <td  class="formlabel" ><fmt:message key="100246" bundle="${lang}"/></td>
			 <td  class="formlabel" align="right"><%=screenUniqueId%></td>
          </tr>
		</table>

		<br>
		
		<table width="760" border="0" cellspacing="1" cellpadding="4" >
	  	  <tr class="formdata" valign="top">
	  		<td ><fmt:message key="100247" bundle="${lang}"/> <br>
				<input type="text" name="locationId" value="<%=locationId%>" size="16" readonly  maxlength="16" >
			</td>
	   		<td class="formdata" width="50%">&nbsp;
	   			<input type="hidden" name="userLevel" value="TERMINAL" >
		  	</td>
	  	  </tr>

		  <tr class="formdata" valign="top">
			<td><fmt:message key="100203" bundle="${lang}"/> <br>
				<input type="text" name="userId" size="12" maxlength="16"  value="<%= userId %>" readonly >
		  	</td>

			<td  width="50%"><fmt:message key="100248" bundle="${lang}"/> <br>
		  		<input type="text" name="userName" size="30" maxlength="30" value="<%= userModel.getUserName() %>" readonly ></td>
	  	  </tr>
	  
	  	  <tr class="formdata" valign="top">
			<td><fmt:message key="100249" bundle="${lang}"/> <br>
		  		<input type="text" name="empId" size="16" maxlength="16" value="<%= employeeId %>" readonly ></td>

			<td><fmt:message key="100250" bundle="${lang}"/> <br>
		  		<input type="text" name="department" maxlength="16" size="20" value="<%= employeeDept %>" readonly ></td>
	  	  </tr>
	  	  
	  	  <tr class="formdata" valign="top">
			<td><fmt:message key="100251" bundle="${lang}"/> <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		  		<input type="password" name="oldPassword" size="16" maxlength="16">  </td>
			<td width="50%">&nbsp;</td>
	  	  </tr>
	  
	  	  <tr class="formdata" valign="top">
			<td><fmt:message key="100252" bundle="${lang}"/><font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font> <br>
				<input type="password" name="newPassword" size="16" maxlength="16">
			</td>
			<td ><fmt:message key="100253" bundle="${lang}"/> <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
		 		<input type="password" name="confirmPassword" size="16" maxlength="16">
		 	</td>
	  	  </tr>
	  	  
	  	  <tr class="formdata" valign="top">
			<td><fmt:message key="100024" bundle="${lang}"/> <br>
		  		<input type="text" name="roleId" size="20" value="<%= userModel.getRoleId() %>" readonly >
		  	</td>

			<td>&nbsp;</td>
	  	  </tr>
		</table>
		
		<table width="760" border="0" cellspacing="1" cellpadding="4" >
          <tr>
		   <td align="left">
          	<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
			<font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		  </td>
			<td colspan=2 align="right">
				<input type="submit" value='<fmt:message key="6666" bundle="${lang}"/>' name="SUBMIT" class="input">
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

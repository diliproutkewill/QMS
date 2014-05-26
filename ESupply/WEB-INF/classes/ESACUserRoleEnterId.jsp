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
 % File			: ESACUserRoleEnterId.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is Enter Id Screen for User and Role
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import= "com.foursoft.esupply.accesscontrol.java.UserAccessConfig,com.foursoft.esupply.accesscontrol.util.UserAccessUtility,com.foursoft.esupply.accesscontrol.java.UserAccessConfigFactory,org.apache.log4j.Logger,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	String fileName	= "ESACUserRoleEnterId.jsp";
		
%>
<%  
     logger  = Logger.getLogger(fileName);
	   String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
    String action		= "";
	String actionPage	= "";
	String accessType	= "";
	String locationId	= "";
	String idName		= "";
	String UIName		= "";
	String UIName1		= "";
    String userTerminalType ="";
	String actionLabel	= "";

	UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();
	UIName		= request.getParameter("UIName");
	if(UIName.equalsIgnoreCase("role") )
	{
		idName	= "roleId";
		actionPage	= "ESACRoleRegistrationController.jsp";		
		UIName1 = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100270");
	}
	else
	{
		idName	= "userId";
		actionPage	= "ESACUserRegistrationController.jsp";		
		UIName1 = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100271");
	}
	
	
	accessType	= loginbean.getAccessType();
	locationId = loginbean.getLocationId();
	userTerminalType=loginbean.getUserTerminalType();
	if(request.getParameter("action") != null)
	{
		action	= request.getParameter("action");
	}
	else
	{
		action	= (String)session.getAttribute("action");	
	}

	//Logger.info(fileName,"UIName -Process - ActionPage "+UIName+" - "+action+" - "+actionPage );
	
	if(action!=null && action.equalsIgnoreCase("MODIFY")) {
		//actionLabel = "Modify";
		actionLabel = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100005");
	}
	if(action!=null && action.equalsIgnoreCase("DELETE")) {
		//actionLabel = "Delete";
		actionLabel = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100004");
	}
	if(action!=null && action.equalsIgnoreCase("VIEW")) {
		//actionLabel = "View";
		actionLabel = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100006");
	}
	
%>

<html>
<head>	
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language = "JavaScript">
var isNS4 = (navigator.appName=="Netscape")?1:0;
	var Win  = null;

    function specialCharFilter(input)
    {
        //var s = trim(input.value);
        //alert(s);
        filteredValues = "'`#;~!+'";
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < input.value.length; i++)
        {
            var c = input.value.charAt(i);
            if ( filteredValues.indexOf(c) == -1 )
                returnString += c.toUpperCase();
            else
            {
                alert('<fmt:message key="100557" bundle="${lang}"/>');
                input.focus();
                input.select();
                return false;
            }
        }

        return true;
    }

	function showLOV(url)
	{    
		var	width		=	360;
		var	height		=	270;  // Changed According to new  UI Version.
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

	function openLocWin()
	{
		//alert();
					
		var accessType 		= document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;
		var locationIdFilter = document.forms[0].locationId.value;
		
		var Url			= "ESACLocationIdsLOV.jsp?accessType="+accessType+"&locationIdFilter="+locationIdFilter;
		showLOV(Url);
	}
	function clearLocation(input)
	{
		
		var otherid = "<%= idName %>";
		 //alert(document.f1.elements[otherid].value); Modified For new access Control.

		var cell = document.getElementById("idType");
		document.f1.locationId.value = "";
		
		document.f1.elements[otherid].value = "" ;
		cell.innerText = "Location Id :";
		//if(input.value=="ETCRM")
			//cell.replaceChild(document.createTextNode("Customer Id :"),cell.firstChild);
		//else 
			//cell.replaceChild(document.createTextNode("Location Id :"),cell.firstChild);
			if(input.value=="ETCRM")
			cell.innerHTML="Customer Id:<font color=red>*</font>";
			else
			cell.innerHTML="Location Id:<font color=red>*</font>";
	}

	function openWin()
	{
		var locId = document.f1.locationId.value;
		
		var accessType = document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;
        
		if(locId.length == 0)
		{
			var accessTypeText = document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].text;
			
		 alert (' please Enter Location Id');	
			document.f1.locationId.focus();
		}
		else
		{
	<%		
			if(idName.equalsIgnoreCase("roleId") )
			{
	%>
				var roleIdFilter = document.forms[0].roleId.value;
				var myUrl = "ESACRoleIdsLOV.jsp?accessType="+accessType+"&locationId="+locId+"&filterString="+roleIdFilter;
				showLOV(myUrl);
	<%
			}
			else
			{
	%>
		if((document.forms[0].accessType.value=='<%=accessType%>') && document.forms[0].locationId.value!='<%=loginbean.getTerminalId()%>')
		{
                 alert("Please  Enter Correct  TerminalId");
                 document.forms[0].locationId.focus();
				 return false;
		}
				var userIdFilter = document.forms[0].userId.value;
				var myUrl = "ESACUserIdsLOV.jsp?accessType="+accessType+"&locationId="+locId+"&filterString="+userIdFilter;
				showLOV(myUrl);
<%
			}
	%>
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
    
	function mandatory()
	{
		// added by VALKSHMI For issue 174465 on 27/06/09
		if((document.forms[0].accessType.value=='<%=accessType%>') && document.forms[0].locationId.value!='<%=loginbean.getTerminalId()%>')
		{
                 alert("Please  Enter Correct  TerminalId");
                 document.forms[0].locationId.focus();
				 return false;
		}
		if(document.forms[0].locationId.value.length == 0)
		{
			var accessTypeText = document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].text;
			
			alert('<fmt:message key="100549" bundle="${lang}"/>');
			
			document.forms[0].locationId.focus();
			return false;
		}
		if(document.forms[0].<%= idName %>.value.length == 0)
		{
			var lbName= "<%= UIName %>";
			<%		
			if(idName.equalsIgnoreCase("roleId") )
			{
	%>
    	alert('<fmt:message key="100564" bundle="${lang}"/>');
      <%
			}
			else
			{
	%>
          alert('<fmt:message key="100554" bundle="${lang}"/>');
   <%       
            }
   %>   
			document.forms[0].<%= idName %>.focus();
			return false;
		}
    for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='text')
			{
				document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
			}
		}
	}		
	// New Access Control

	function resetCheckBoxes() {
		for(var i=0; i < document.forms[0].elements.length; i++) 
        {
			if(document.forms[0].elements[i].name == "mode") 
            {
				document.forms[0].elements[i].checked = false;
				document.forms[0].elements[i].disabled = true;
			}
		}
	}

	function placeFocus()
	{
		
		if(userTerminalType=='A'&& accessType=='ADMN_TERMINAL')
		{
			
			document.forms[0].terminalId.readOnly = true;
			//document.forms[0].button1.disabled	  = true;
		}
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
<!--Modified For new access Control-->
<form name="f1" action="<%= actionPage %>" method="POST" onSubmit="return mandatory();"onReset="resetCheckBoxes();">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>

	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><%= UIName1 %> - <%= actionLabel %></td>
		  <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,action+UIName)%></td>
    	</tr>
	</table>
	
	<br>
	
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
	
    	<tr class="formdata" valign="top">
		  <td><fmt:message key="100267" bundle="${lang}"/> <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font></td>
		  <td id=idType><!--<fmt:message key="100268" bundle="${lang}"/>-->Terminal Id: <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font></td><!--@@Modified by kameswari for the issue-66145-->
		  <td><%= UIName %> <fmt:message key="100269" bundle="${lang}"/> <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font></td>
		</tr>
		
		<tr class="formdata" valign="top">
		  <td>
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
		
		  <td>
		  	<input type="text" name="locationId" value="<%= locationId %>" onBlur = "specialCharFilter(this);stringFilter(this)" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 45) ||(event.which > 46 && event.which < 48)|| (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 45) ||(event.which > 46 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}">
			  <input type="button" value="..." name="locBtn" onClick = 'return openLocWin()' class="input">
		  </td>
		
		  <td>
			<input type="text" name="<%= idName %>" size="20" onBlur = "specialCharFilter(this);stringFilter(this)" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 45)||(event.which > 46 && event.which < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 45)||(event.which > 46 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}">
			<input type="button" value="..." name="roleBtn" onClick = 'return openWin()' class="input">
		  </td>
		</tr>
		
	</table>

	<table width="100%" cellpadding="4" cellspacing="1" border="0">
		<tr>
		  <td align="left">
          	<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
			<font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		  </td>

		  <td colspan=2 align="right">
			<input type=hidden name=screen_name value="user_role_entry" >
			<input type="hidden" name="action" value="<%= action %>" size="20" >
        	<input type="submit" value='<fmt:message key="3334" bundle="${lang}"/>'  name="B1" class="input">
			<!-- Modified For new access Control Add new Line Below For Reset-->
			<input type="reset" name="Submit" value='<fmt:message key="8890" bundle="${lang}"/>' class="input">
		  </td>
    	</tr>
	</table>

  </td>
 </tr>
</table>

</form>
</body>
</html>
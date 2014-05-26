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
 % File			: ESACUserRoleCreation.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is first UI in registering the Role and User,
 % It displays the AccessType, modules assosiated with the role/user which is to be created
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters, com.foursoft.esupply.accesscontrol.java.UserAccessConfig, com.foursoft.esupply.accesscontrol.util.UserAccessUtility, com.foursoft.esupply.accesscontrol.java.UserAccessConfigFactory, com.foursoft.esupply.common.java.ShipmentMode, org.apache.log4j.Logger,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
	String fileName = "ESACRoleRegistration.jsp";
	
%>
<%
  String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
    String 	actionPage	= "";
	String 	locationId	= "";
	String	accessType	= "";
	String  onLoad	= "";

	String idName		= "";
	String UIName		= "";
	String module = FoursoftWebConfig.MODULE;

	UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();
	String modules[] = accessConfig.getModules();
	int		moduleIndexes[] = accessConfig.getModuleIndexes();
	
	String	ETRANS	=	accessConfig.getETrans();
	session.removeAttribute("roleModel");	// Just to make sure

	locationId	= loginbean.getLocationId();
	accessType	= loginbean.getAccessType();

	actionPage	= "ESACRoleRegistrationController.jsp";

	String[] allowedAccessTypes1 = accessConfig.getAccessTypes(loginbean.getAccessType());
	for(int i=0; i < allowedAccessTypes1.length; i++)	
	{	
		onLoad = UserAccessUtility.getAccessTypeLable(allowedAccessTypes1[i]);
		break;
	}

	//Logger.info(fileName, "UIName : LocationId : accessType :: "+UIName+" : "+locationId+" : "+accessType );
%>

<html>
<head>
	<title><fmt:message key="100282" bundle="${lang}"/> </title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">

<%@ include file="/ESEventHandler.jsp" %>

<script language="JavaScript">
  
	var Win  = null;
	var isNS4 = (navigator.appName=="Netscape")?1:0;
	function showLocationIdsLOV()
	{
		var accessType = document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;

		var	width		=	260;
		var	height		=	260;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		var locationIdFilter = document.forms[0].locationId.value;

		var Url			= "ESACLocationIdsLOV.jsp?accessType="+accessType+"&locationIdFilter="+locationIdFilter;
		var Bars		= 'directories=no, location=no,menubar=no,status=no,titlebar=no';		
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;

		if(Win !=null)
		{
			Win.close();
			Win = open(Url,'Doc',Features);
		}		
		else
			Win = window.open(Url,'Doc',Features);

		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;

	}
	function trimAll(obj) 
	{
		var sString = obj.value;
		while (sString.substring(0,1) == ' ')
		{
			sString = sString.substring(1, sString.length);
		}
		while (sString.substring(sString.length-1, sString.length) == ' ')
		{
			sString = sString.substring(0,sString.length-1);
		}
		obj.value = sString;
	}

    function stringFilter (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < input.value.length; i++) 
		{  
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase();
		}
		input.value = returnString;
	}
    
    function specialCharFilter(input)
    {
        filteredValues = "'`;~#!+%'";
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

	function checkAccessType()
	{
		if(document.forms[0].accessType.selectedIndex == 0)
		{
			alert('<fmt:message key="100560" bundle="${lang}"/>');
			return false;
		}
	}

	function disp(val)
	{
		var data1 = "";
<%
		if (module.equals("ETRANS"))
		{
%>
			if(val != 'OPER_TERMINAL' && val != 'ETCRM' && val != 'ETVRM')
				data1= "<input type='checkbox' name='module' value='1'>Administration<br>";
				data1+= "<input type='checkbox' name='module' value='2' >QuoteShop<br>";
			//data1+= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Shipment Mode(s):<input type='checkbox' name='mode' value='Setup' style='VISIBILITY: hidden'><br>";
			//data1+= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='mode' value='Air' disabled>Air<br>";
			//data1+= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='mode' value='Sea' disabled>Sea<br>";
			//data1+= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='mode' value='Truck' disabled>Truck<br>";
			//data1+= "<input type='checkbox' name='module' value='8'>eAccounts<br>";
<%
		}

%>
		if(document.layers)
		{
			document.layers.cust1.document.write(data1);// here 'cust' is the nameof span(see below for name span)
			document.layers.cust1.document.close();
		}
		else
		{
			if(document.all)
			{
				cust1.innerHTML = data1;
			}
		}		
	}

	function clearLocation(input)
	{
		var cell = document.getElementById("idType");
		document.forms[0].locationId.value = "";
		cell.innerText = "Location Id :"
		
		//if(input.value=="ETCRM")
			//cell.replaceChild(document.createTextNode("Customer Id :"), cell.firstChild);
		//else 
			//cell.replaceChild(document.createTextNode("Location Id :"), cell.firstChild);

			if(input.value=="ETCRM")
			cell.innerHTML="Customer Id:<font color=red>*</font>";
			else
			cell.innerHTML="Location Id:<font color=red>*</font>";
	
		disp(input.value);
	
	}
	
	function checkForm()
	{
		trimAll(document.forms[0].roleId);
		if (!specialCharFilter(document.forms[0].roleId))
		{
			return false;
		}

		if(document.forms[0].locationId.value=="")
		{
			alert('<fmt:message key="100561" bundle="${lang}"/>');
			document.forms[0].locationId.focus();
			return false;
		}
		var flag = false;
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
			alert('<fmt:message key="100562" bundle="${lang}"/>');
			return false;
		}
		if(etransSelected == true) {
			
			var modeSelected = false;
			
			// If ETrans module is selected force the User to select at least one mode
			var selectedModeCount	=	0;
			
			for(var i=0; i < document.forms[0].elements.length; i++) {
				if(document.forms[0].elements[i].name == "mode") {
					if(document.forms[0].elements[i].checked) {
						selectedModeCount++;
						if(selectedModeCount == 2) {
							modeSelected = true;
							break;
						}
					}
				}
			}
			
			if(modeSelected==false) 
			{
				alert(checkEscape('<fmt:message key="100563" bundle="${lang}"/>'));
				return false;
			}
		}
		
		if(document.forms[0].roleId.value=="")
		{
			alert('<fmt:message key="100564" bundle="${lang}"/>');
			document.forms[0].roleId.focus();
			return false;
		}		
		if(document.forms[0].roleId.value=="")
		{
			alert('<fmt:message key="100565" bundle="${lang}"/>');
			document.forms[0].description.focus();
			return false;
		}		
	}
	

	var etransSelected = false;

	function manipulateModes( chkBoxObj ) {

		if(chkBoxObj.checked) {

			etransSelected = true;
			
			var etransSetupChecked	=	false;
			
			for(var i=0; i < document.forms[0].elements.length; i++) {
				if(document.forms[0].elements[i].name == "mode") {
					if(etransSetupChecked==false) {
						document.forms[0].elements[i].checked = true;
						etransSetupChecked = true;
					} else {
						document.forms[0].elements[i].checked = false;
						document.forms[0].elements[i].disabled = false;
					}
				}
			}
			
		} else {
			
			etransSelected = false;
			
			var count = 0;
			
			for(var i=0; i < document.forms[0].elements.length; i++) {
				if(document.forms[0].elements[i].name == "mode") {
					if(count==0) {
						document.forms[0].elements[i].checked = false;
					} else {
						document.forms[0].elements[i].checked = false;
						document.forms[0].elements[i].disabled = true;
					}
					count++;
				}
			}
		}
		
	}

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
</script>
</head>
<body onLoad="disp('<%=onLoad%>')">
<form method="POST" action="<%=actionPage%>"  onSubmit="return checkForm();" onReset="resetCheckBoxes();">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF">
   <td> 

	<table width="100%" class='formlabel' border="0">
	  <tr> 
	 	<td class="formlabel" colspan="3"><fmt:message key="100282" bundle="${lang}"/></td>
		<td class="formlabel" align="right"><%=loginbean.generateUniqueId(fileName,"Add")%></td>
	  </tr>
	</table>
	
	<br>
	
	<table width="100%" cellpadding="1" cellspacing="0" border="0">
	  
	  <tr class="formdata" valign="top">
	  
	  	<td width="34%" height="10%" valign=bottom><fmt:message key="100267" bundle="${lang}"/></td>
		  
		<td id=idType width="33%" height="10%" valign=bottom> Terminal Id<font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font></td><!--<font color="#FF0000"><fmt:message key="100247" bundle="${lang}"/>--><!--@@Modified by Kameswari for the WPBN issue-66145-->

		<td rowspan="4" width="33%"><fmt:message key="100214" bundle="${lang}"/> <font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
			<span id=cust1 style='position:relative' > </span>
		</td>
	  </tr>
	  
	  <tr class="formdata" valign="top">
	    <td height="35%">
          <select size="1" name="accessType"  class="select" onChange ="clearLocation(this)">
			  <option value="<%= accessType %>" selected><%= UserAccessUtility.getAccessTypeLable(accessType) %></option>

<%--
 %		what are the allowed access types for to create roles by this User
 %		this will taken for the UserAccessConfig by passing his accesss type
--%>
<%      
		String[] allowedAccessTypes = accessConfig.getAccessTypes(loginbean.getAccessType());
		for(int i=0; i < allowedAccessTypes.length; i++)	{	
			if(!accessType.equalsIgnoreCase(allowedAccessTypes[i]) ) {					
%>		
			  <option value="<%= allowedAccessTypes[i]  %>" ><%= UserAccessUtility.getAccessTypeLable(allowedAccessTypes[i]) %></option>
<%			}	
		}		  %>
          </select>
		</td>
		
		<td height="35%">
			<input type="text" name="locationId" value = "<%= locationId %>" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}" onBlur="stringFilter(this)" >
			<input type="button" value="..." name="locationIdBtn"  onClick="return showLocationIdsLOV()" class="input">
		</td>
	  </tr>
	  
	  <tr class="formdata" valign="top"> 
		<td width="34%">
			<fmt:message key="100284" bundle="${lang}"/> <font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
			<input type="text" name="roleId"  size="20" maxlength="16" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}" onBlur="stringFilter(this);trimAll(this)">
		</td>
		
		<td width="33%">
			<fmt:message key="100285" bundle="${lang}"/><br>
			<input type="text" name="description"  size="20" maxlength="30" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 48) || (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}" onBlur="stringFilter(this)">&nbsp;
		</td>
	  </tr>
	  
	</table>
	
	<table width="100%" cellpadding="4" cellspacing="1" border="0">
	  <tr>
		<td align="right">
          <p align="left"><font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font> <font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		</td>
		<td colspan=2 align="right">
			<input type="hidden" name="screen_name"  value="role_registration">
			<input type="hidden" name="action"  value="add">
			<input type="submit" name="Submit"  value='<fmt:message key="3333" bundle="${lang}"/>' class="input"> 
			<input type="reset" name="Submit"  value='<fmt:message key="8890" bundle="${lang}"/>' class="input">
		</td>
	  </tr>
		
	</table>
		
  </td>
 </tr>
</table>
</form>
</body>
</html>

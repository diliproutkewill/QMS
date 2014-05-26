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
 % File			: ESLoggingEnterId.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is Enter Id Screen for Usage Activity View
 % 
 % author		: Madhu. P
 % date			: 10-01-2002
--%>
<%@ page import=   "com.foursoft.esupply.accesscontrol.java.UserAccessConfig,java.util.*,									com.foursoft.esupply.accesscontrol.util.UserAccessUtility,
			com.foursoft.esupply.accesscontrol.java.UserModel,
						com.foursoft.esupply.common.util.ESupplyDateUtility,
                    com.foursoft.esupply.common.bean.UserPreferences,
                    com.foursoft.esupply.accesscontrol.java.UserAccessConfigFactory,
					org.apache.log4j.Logger ,
                    java.sql.Timestamp,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:useBean id="dateUtility"  class="com.foursoft.esupply.common.util.ESupplyDateUtility" scope="session"/>
 
 <%!
  private static Logger logger = null;
 %>
 
 <%
    logger  = Logger.getLogger("ESLoggingEnterId.jsp");
 				String  language = "";

%>
<%
	    language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	String additionalString	="";	
	
    String UIName       = request.getParameter("UIName");
		if(UIName.equalsIgnoreCase("VIEWALL"))
		{
			additionalString= ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100083");
		}
		
	//System.out.println(" UIName from request is : "+UIName);
	UserPreferences userPreferences = loginbean.getUserPreferences();
	String dateFormat = userPreferences.getDateFormat();

	if (dateFormat == null || dateFormat.equals("")) {
		dateFormat = "MM/DD/YY";
	}
	
	//System.out.println("dateFormat =1"+dateFormat);
	dateUtility.setPatternWithTime(dateFormat);
		//System.out.println("dateFormat =2"+dateFormat);
	
	String actionPage   = null;

	String	strCCDate	=	dateUtility.getCurrentDateString(dateFormat);
	//System.out.println("dateFormat =  =  =  3"+dateFormat);
  
	//if(UIName.equalsIgnoreCase("View"))
		actionPage = "ESLoggingActivityReport.jsp";
	/*else
	{
		actionPage = "ESLoggingActivityViewAllReport.jsp";
	//	actionPage = "TempJSP1.jsp";
	}*/
	String accessType	= "";
	String locationId	= "";

	accessType	= loginbean.getAccessType();
	locationId = loginbean.getLocationId();
	UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();
%>

<html>
<head>
<title><fmt:message key="100277" bundle="${lang}"/></title>

<jsp:include page="ESACDateValidation.jsp" >
  <jsp:param name="format" value="<%=dateFormat%>"/>
  <jsp:param name="currentDate" value="<%=strCCDate%>"/>
</jsp:include>

<%@ include file="/ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<SCRIPT language=javascript src="popcalendar.js"></SCRIPT>
<SCRIPT language=JavaScript>
dateFormat = "<%=dateFormat%>";
</SCRIPT>
<script language="JavaScript">
var isNS4 = (navigator.appName=="Netscape")?1:0;
function upper(input)
{ 
  input.value=input.value.toUpperCase();
}
//// ========================================================================/////
//// =========== date related Stufff starts here ============================/////
//// ========================================================================/////
function setReportType()
{
<%
	if(UIName.equals("View"))
	{
%>
		document.forms[0].reportType.value="SINGLE";
<%		
	}
	else
	{
%>
		document.forms[0].reportType.value="MULTIPLE";
<%		
	}
%>	
}
function y2k1(num) {

	var strNumber = ""+num;
	var number = parseInt(strNumber, 10);
	
	//alert("year = '"+number+"'");

	if(number == 0) {
		number =  '0'+number;
	}

	if (number >= 1  && number <= 99 )
	{
		if(number >=1 & number <= 9) {
			number =  '0' + number;
		}
	}
	//alert("year = "+number);
	return number 
}

//
function getLongYear(num)
{
	var strNumber = ""+num;
	var number = parseInt(strNumber, 10);
	
	//alert("year = '"+number+"'");

	if(number == 0) {
		number =  '0'+number;
	}

	if (number >= 1  && number <= 99 )
	{
		if(number >=1 & number <= 9) {
			number =  '200' + number;
		}
		if(number >=11 & number <= 99) {
			number =  '20' + number;
		}
	}

	//alert("year = "+number);
	return number 

}
//

////////////////////////////// padout1(number) /////////////////////////////////

function padout1(number)  {

	if (number >= '01' && number<= '09' ) {
		return number;
	}
	else if (number >= 1 && number<= 9 ) {
		number =  '0' + number ;
	}

	return number 

}

////////////////////////////// LeapYear(intYear) ///////////////////////////////

function padout(number) { return (number < 10) ? '0' + number : number; }

function LeapYear(intYear)  {

	if(intYear % 100 == 0) {
		if (intYear % 400 == 0) {
			return true;
		}
	} else if ((intYear % 4) == 0) {
		return true;
	}

	return false;
}



//// ========================================================================/////
//// =========== date related Stufff ends here ============================/////
//// ========================================================================/////


	function Mandatory(from)
	{
		if(document.forms[0].locationId.value==0)
		{
			document.forms[0].locationId.focus();
			alert('<fmt:message key="100542" bundle="${lang}"/>');
			return false;
		}
	<%
		if(UIName.equalsIgnoreCase("View"))
		{
	%>	
		if(document.forms[0].userId.value==0)
		{
			document.forms[0].userId.focus();
			alert('<fmt:message key="100554" bundle="${lang}"/>');
			return false;
		}
	<%
		}
	%>
		if(document.forms[0].fromDate.value==0)
		{
			document.forms[0].fromDate.focus();
			alert('<fmt:message key="100555" bundle="${lang}"/>');
			return false;
		}
		if(document.forms[0].toDate.value==0)
		{
			document.forms[0].toDate.focus();
			alert('<fmt:message key="100556" bundle="${lang}"/>');
			return false;
		}
		
		 return nullChecker(document.forms[0]);
	}

	var Win  = null;

	function openLocWin()
	{
					
		var accessType 		= document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;
		var locationIdFilter = document.forms[0].locationId.value;
		var Url			= "ESACLocationIdsLOV.jsp?accessType="+accessType+"&locationIdFilter="+locationIdFilter;
//			alert(Url);

		var	width		=	360;
		var	height		=	270;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;

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

	function clearLocation()
	{
		document.forms[0].locationId.value = "";
	}

	function openWin()
	{
		var locId = document.forms[0].locationId.value;
		if(locId.length == 0)
		{
			alert('<fmt:message key="100549" bundle="${lang}"/>');
			return false;
		}
		var accessType = document.forms[0].accessType.options[document.forms[0].accessType.selectedIndex].value;
//			alert(locId);
		var userIdFilter = document.forms[0].userId.value;
		
		var	width		=	360;
		var	height		=	270;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		
		var myUrl = "ESACUserIdsLOV.jsp?accessType="+accessType+"&locationId="+locId+"&filterString="+userIdFilter;;
//					alert(myUrl);
		
		var myBars = 'directories=no, location=no, menubar=no, status=no, titlebar=no, toolbar=no'; 
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var myFeatures = myBars+','+Options;
		var newWin = open(myUrl,'myDoc',myFeatures);
		if (!newWin.opener) 
			newWin.opener = self;
		if (newWin.focus != null) 
			newWin.focus();
		return false;
	}

	function placeFocus() 
	{
		setReportType();
		document.forms[0].locationId.focus(); 
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

<body onLoad="placeFocus()" >

<form name='f1' method="post" action="<%=actionPage%>"   onReset="placeFocus()" onSubmit="return Mandatory(this.form)">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>

	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><fmt:message key="100278" bundle="${lang}"/><%=additionalString%></td>
		  <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId("ESLoggingEnterId.jsp",UIName)%></td>
    	</tr>
	</table>
	
	<br>
	
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr class="formdata">
		  <td width="34%"><fmt:message key="100279" bundle="${lang}"/> <font color="red"><fmt:message key="9999" bundle="${lang}"/></font><br>
			<select size="1" name="accessType" class="select" onChange ="clearLocation(this)">
			  <option value="<%= accessType %>" selected><%= UserAccessUtility.getAccessTypeLable(accessType) %></option>
	<%--
	 % This is used as get the allowed access types by providing his access types
	--%>
<%      String[] allowedAccessTypes = accessConfig.getAccessTypes(loginbean.getAccessType());
		for(int i=0; i < allowedAccessTypes.length; i++)	{	
			if(!accessType.equalsIgnoreCase(allowedAccessTypes[i]) ) {	%>		
			  <option value="<%= allowedAccessTypes[i] %>" ><%= UserAccessUtility.getAccessTypeLable(allowedAccessTypes[i]) %></option>
<%			}	
		}		  %>
			</select> 
		  </td>
          <td width="36%"><fmt:message key="100247" bundle="${lang}"/> <font color="red"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" name="locationId" maxlength="16" onBlur ='upper(this)' size="20" onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 45) ||(event.which > 46 && event.which < 48)|| (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 45) ||(event.which > 46 && event.which < 48) || (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}">
			  <input type="button" value="..." name="locBtn" onClick = 'return openLocWin()' class="input"> 
		  </td>
          <td width="30%">
<%
		if(UIName.equalsIgnoreCase("View"))
		{
%>
			<fmt:message key="100276" bundle="${lang}"/> <font color="red"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" name="userId" maxlength="16" onBlur ='upper(this)' onKeypress="if(!isNS4){if ((event.keyCode > 32 && event.keyCode < 45) ||(event.which > 46 && event.which < 48)|| (event.keyCode > 57 && event.keyCode < 65) || (event.keyCode > 90 && event.keyCode < 97)) event.returnValue = false;}else{if ((event.which > 32 && event.which < 45) ||(event.which > 46 && event.which < 48)|| (event.which > 57 && event.which < 65) || (event.which > 90 && event.which < 97)) return false;}">
			  <input type="button" name="usrBtn" value="..." onClick = 'return openWin()' class="input">
 <%
		}
%>   
		  </td>
		</tr>
	

 
		<tr class="formdata">
          
          <td><fmt:message key="100280" bundle="${lang}"/> <font color=red><fmt:message key="9999" bundle="${lang}"/></font><br>
		  	<input type="text" name="fromDate" value="<%=strCCDate%>" maxlength="11"  size="15" onBlur = "return dtCheck(this)">
			<input type="button" value="..."  onClick="newWindow('fromDate');" name="button2" class='input'>
			<font size="2">(<%=dateFormat%>)</font>
		  </td>
		  
          <td><fmt:message key="100281" bundle="${lang}"/> <font color=red><fmt:message key="9999" bundle="${lang}"/></font><br>
			<input type="text" name="toDate"  value="<%=strCCDate%>" maxlength="11" size="15" onBlur = "return dtCheck(this)">
            <input type="button" value="..."  onClick="newWindow('toDate');" name="button" class='input'>
			        <!-- <SCRIPT language=javascript>
					<!--
						if (!document.layers) {
							document.write("<img align=absmiddle style='CURSOR:hand' src='img/calendar.gif' onclick='popUpCalendar(this, f1.toDate, dateFormat)' alt='select'>")
						}
					//
					</SCRIPT> -->
            <font size="2">(<%=dateFormat%>)</font>  
		  </td>
          <td>&nbsp;</td>
		</tr>
		<tr class="formdata"><td><fmt:message key="100112" bundle="${lang}"/><INPUT TYPE="radio" NAME="openFormat" value="html" checked><fmt:message key="100113" bundle="${lang}"/>&nbsp;&nbsp;<INPUT TYPE="radio"  value="xls" NAME="openFormat"><fmt:message key="100114" bundle="${lang}"/>&nbsp</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	</table>

	<table width="100%" cellpadding="4" cellspacing="1" border="0">

		<tr>
          <td>
		  	<font color=red><fmt:message key="9999" bundle="${lang}"/></font>
			<font size=1 face=verdana> <fmt:message key="100233" bundle="${lang}"/></font>
		  </td>
		  
          <td colspan="2" align="right">
		  	<input type=hidden name='reportType' value=''>
			<input type="submit" name="Submit" value='<fmt:message key="6666" bundle="${lang}"/>' class="input">
			<input type="reset" value='<fmt:message key="8890" bundle="${lang}"/>' class="input">
			<!-- <input type="reset" name="Reset" value="Reset" class="input"> -->
		  </td>
		</tr>
		
	</table>
	
  </td>
 </tr>
</table>
  
</form>
</body>
</html>


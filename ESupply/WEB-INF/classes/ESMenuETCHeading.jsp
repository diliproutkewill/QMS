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
 % File			: ESMenuHeading.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is a Used to display Top Tool Bar and Others
 % 
 % author		: Sasibhushan. P
 % date			: 22-12-2001
--%>
<%@ page import= "java.util.ArrayList, com.foursoft.esupply.accesscontrol.util.UserAccessUtility,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
	
<%
	  String  language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<html>
<head>
<STYLE>
<!--
body{margin:0px;background-color:#C0C0C0}
body,p,li,td,nobr {font-size:10pt;font-family:"Verdana",Arial;font-weight:normal;color:#000000}
A:link{color:#000000}
A:visited{color:#000000}
A:hover{color:#FF0000}
//-->
</STYLE>
<%@ include file="/ESEventHandler.jsp" %>
<script>
function goBack()
{
	parent.frames[2].history.back();
}
function goFwd()
{
	parent.frames[2].history.forward();
}
function showAboutPage()
{
	var	width		=	280;
	var	height		=	180;
	var	top			=	(screen.availHeight - height) / 2;
	var	left		=	(screen.availWidth  - width)  / 2;

	var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=no, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
	var Features=Bars+' '+Options;
    window.open('html/ESupplyTreeAboutPage.html','Doc',Features)	
}
function openChat()
{
	var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=no,width=275,height=100,resizable=no';
	var Features=Bars+' '+Options;
    window.open('http://chat.4s-esupply.com/esupply/CustomerChat.html','Doc',Features)
}
var message='<fmt:message key="100553" bundle="${lang}"/>';
function clickIE() {if (document.all) {alert(message);return false;}}
function clickNS(e) {if 
(document.layers||(document.getElementById&&!document.all)) {
if (e.which==2||e.which==3) {alert(message);return false;}}}
if (document.layers) 
{document.captureEvents(Event.MOUSEDOWN);document.onmousedown=clickNS;}
else{document.onmouseup=clickNS;document.oncontextmenu=clickIE;}

document.oncontextmenu=new Function("return false")
	
	
	function unloader() {

		var DIM		= 20;
		var TOP		= screen.availHeight-DIM
		var LEFT	= screen.availWidth-DIM;
	
		var goahead = false;
			
		
				alert(goahead);
		if(goahead==true) {
			alert("in goahead======================>");
			newWin = window.open("ESLogout?from=logout",'_blank', "width="+DIM+", height="+DIM+", top="+TOP+", left="+LEFT+", toolbar=no,status=no,location=no,menubar=no,directories=no,scrollbars=no,resizable=no, visible=no");
			//parent.close();
		} else {
			return false;
		}
	}
</script>
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>

</head>
<BODY >
<form>
<table border='0' cellspacing='0' cellpadding='0' width='100%' height='30' bgcolor='#C0C0C0'>
<tr><td valign='middle'>
<table border='0' cellspacing='0' cellpadding='0' height='24' bgcolor='#C0C0C0'>
<tr>  
	<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="http://www.four-soft.com" target = "_target"><IMG SRC="images/Toolbar_4slogo.gif" ALT="Four soft Home" BORDER="0" onmouseover="status = 'Four soft Home';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:void(0);" onClick="goBack();" ><IMG SRC="images/Toolbar_backward.gif" ALT="Back" BORDER="0" onmouseover="status = 'Back';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:void(0)" onClick="goFwd();"><IMG SRC="images/Toolbar_forward.gif" ALT="Forward" BORDER="0" onmouseover="status = 'Forward';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:void(0)" onClick="javascript:parent.frames[2].document.clear();"><IMG SRC="images/Toolbar_stop.gif" ALT="Stop" BORDER="0" onmouseover="status = 'Stop';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:parent.frames[2].location.reload();"><IMG SRC="images/Toolbar_refresh.gif" ALT="Refresh" BORDER="0" onmouseover="status = 'Refresh';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:parent.frames[2].print();" ><IMG SRC="images/Toolbar_print.gif" ALT="Print" BORDER="0" onmouseover="status = 'Print';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="http://www.4s-esupply.com" target = "_target"><IMG SRC="images/Toolbar_home.gif" ALT="Home www.4s-esupply.com" BORDER="0" onmouseover="status = 'Home www.4s-esupply.com';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="ESACPasswordChangeView.jsp" target=main ><IMG SRC="images/Toolbar_changePassword.gif" ALT="Change Profile" BORDER="0" onmouseover="status = 'change Password';return true;" onmouseout="status = '';return true;" width="16" height="16" ></a></td>
	<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
	<!--<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="ESACLogout.jsp" target=main ><IMG SRC="images/Toolbar_logout.gif" ALT="Logout" BORDER="0" onmouseover="status = 'Logout';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>-->
	
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a name="logoutLink" href="" onClick="unloader(); return false;"><IMG SRC="images/Toolbar_logout.gif" ALT="Logout" BORDER="0" onmouseover="status = 'Logout';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	
	<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:showAboutPage()" ><IMG SRC="images/Toolbar_about.gif" ALT="About" BORDER="0" onmouseover="status = 'About';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:openChat()" ><IMG SRC="images/Toolbar_customersupport.gif" ALT="Customer Support" BORDER="0" onmouseover="status = 'Customer Support';return true;" onmouseout=" status = '';return true;" height="16" width="16"></a></td>
	<td colspan=2>&nbsp;</td>
	<td align=center width=100% ><b><%= loginbean.getUserId() %> 
 <fmt:message key="100322" bundle="${lang}"/><%= loginbean.getLocationId() %>(<%= UserAccessUtility.getAccessTypeLable(loginbean.getAccessType() ) %>)</font></b></td>
	<td align=right WIDTH="1024" HEIGHT="5" ><img src="images/eSupply_product_logo.gif"></td>
</tr>
</table>
</td>
</tr>
</table>
</form>
</body>
</html>
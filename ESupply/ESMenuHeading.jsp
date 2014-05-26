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
 % Modification  history
 % Amit Parekh		19/11/2002		Changes made to handle facility switching in EP for a COMPANY User and
 %									warehouse customer location switching for a WAREHOUSE level User in ELOG
 % Amit Parekh		27/02/2003		Changes made to handle switching location/warehouse bug free.
 %									This procedure is now possible only once at a time and multiple clicks on the
 %									switching lever button are now avoided. Also added SP as a Module to make
 %									switching happen correctly in SP.
 % Amit Parekh		07/05/2003		While loading, the Switching Lever is made invisible just after it is put on the page
--%>
<%@ page import= "java.util.ArrayList, com.foursoft.esupply.accesscontrol.util.UserAccessUtility,java.util.ResourceBundle" %>
<%@ page import="javax.naming.*, org.apache.log4j.Logger,javax.servlet.jsp.jstl.fmt.LocalizationContext, com.foursoft.esupply.common.bean.UserCredentials "%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	String fileName = "ESMenuHeading.jsp";	
%>
<%
  logger  = Logger.getLogger(fileName);
  String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
    boolean	errorOcurred	=	false;

	String	accessType		=	(String) session.getAttribute("ACCESSTYPE");
	String	userType		=	loginbean.getUserType();

	UserCredentials	uc		=	(UserCredentials) session.getAttribute("userCredentials");
		
	String	windowName		=	(String)	uc.getWindowName();
	String	loginPage		=	"ESupplyLogin?userType="+userType+"&windowName="+windowName;
	String	errorMessage	=	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100034");

	try {
%>

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

<%@ include file="ESEventHandler.jsp" %>

<script language="JavaScript">
<%

	ArrayList warehouseList	=	null;
	
	if(accessType==null) {
		accessType = "";
	}
	
	if(userType==null) {
		userType = "";
	}

	if(	(userType.equals("ELG") && accessType.equals("WAREHOUSE")) ||
		(userType.equals("ESP") && accessType.equals("WAREHOUSE")) ||
		(userType.equals("EEP") && accessType.equals("COMPANY")) 	)
	{

		String parentId	=	"";

		if(	userType.equals("ELG") && accessType.equals("WAREHOUSE")) {
			parentId	=	loginbean.getWareHouseId();
		}
		if(	userType.equals("ESP") && accessType.equals("WAREHOUSE")) {
			parentId	=	loginbean.getWareHouseId();
		}
		if(	userType.equals("EEP") && accessType.equals("COMPANY")) {
			parentId	=	loginbean.getCompanyId();
		}
%>
		var warehouseList	=	new Array();
<%
		/*	Here we add the parent as the first element of the select box
			For ELOG it wiil be the Warehouse Id and for EP it will be the Company Id
		*/
%>
		warehouseList[0]	=	'<%= parentId %>';
		
		var presentLocation	=	'<%=loginbean.getLocationId()%>';	
<%
		//warehouseList = loginbean.getELogCredentials().getCustWHIdInfo();
		warehouseList = loginbean.getELogCredentials().getPermittedWHIds();

		for(int i=0;i < warehouseList.size();i++) {
%>
			warehouseList[<%=i+1%>] = '<%= warehouseList.get(i)%>';
<%
		}
%>
	
	function shiftWH()
	{
		document.links.item("switch").style.visibility = "hidden";
		document.forms[0].elements["custWH"].style.visibility = "hidden";
		
		opennew = false;		
		parent.from = 'logout';
		parent.mskey='toggle';
		parent.key='toggle';
		
		var index = document.forms[0].custWH.selectedIndex;
		var type  ;
		
		if(index==0) {
			type = 'PT';
		} else {
			type = 'WH';
		}
			
		top.location.href = "ESMenuIndex.jsp?warehouse="+document.forms[0].custWH.options[index].value+"&type="+type+"&logoff=Y";
	}

	var parentId	=	"<%= parentId %>";

	function populate()
	{
		
		if(document.forms[0].custWH) {

			document.links.item("switch").style.visibility = "hidden";
			
			for(i=0;i<warehouseList.length;i++)
			{
				if(presentLocation==warehouseList[i])
				{
					document.forms[0].custWH.options[i] = new Option(warehouseList[i],warehouseList[i]);
					document.forms[0].custWH.options[i].selected=true;
				}
				else
				{
					document.forms[0].custWH.options[i] = new Option(warehouseList[i],warehouseList[i]);
				}

				if(warehouseList[i] == parentId) {
					document.forms[0].custWH.options[i].style.color = "RED";
				} else {
					document.forms[0].custWH.options[i].style.color = "BLUE";
				}
			}
		}
	}
<%
}
%>
	function goBack()
	{
		parent.frames[3].history.back();
	}
	function goFwd()
	{
		parent.frames[3].history.forward();
	}
	function showAboutPage()
	{
		var	width		=	350;
		var	height		=	180;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;

		var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=no, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		var Features=Bars+' '+Options;
		window.open('html/ESupplyTreeAboutPage.html','Doc',Features)
	}
  function showHelpPage(helpURL)
	{
		var TOP		= screen.availHeight;
		var LEFT	= screen.availWidth;
		var	top			=	(screen.availHeight - TOP);
		var	left		=	(screen.availWidth  - LEFT);

		var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=no, width='+LEFT+', height='+TOP+', top='+top+', left='+left+', resizable=yes';
		var Features=Bars+' '+Options;
		window.open(helpURL,'Doc',Features)
	}
	function openChat()
	{
		var	width		=	350;
		var	height		=	350;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		
		var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=no,width='+width+', height='+height+', top='+top+', left='+left+',resizable=no';
		var Features=Bars+' '+Options;
	    window.open('http://support.4s-esupply.com/esupply/CustomerChat.html','Doc',Features)
	}
	var message='<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100553")%>';
	function clickIE() {if (document.all) {alert(message);return false;}}
	function clickNS(e) {if 
	(document.layers||(document.getElementById&&!document.all)) {
	if (e.which==2||e.which==3) {alert(message);return false;}}}
	if (document.layers) 
	{document.captureEvents(Event.MOUSEDOWN);document.onmousedown=clickNS;}
	else{document.onmouseup=clickNS;document.oncontextmenu=clickIE;}

	document.oncontextmenu = new Function("return false")

	function printCommand() {
		parent.frames["text"].focus();
		parent.frames["text"].print();
	}
	
	var module = "<%= loginbean.getModuleIndicator()%>";
	var opennew = true;

	function unloader() {

		if(opennew==true) {
		
			if(parent.key != 'F5') {

				var DIM		= 20;
				var TOP		= screen.availHeight-DIM
				var LEFT	= screen.availWidth-DIM;
	
				var goahead = false;
			
				goahead = confirm('<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100531")%>');
				
				if(goahead==true) {
					parent.from = 'logout';
					document.logout.target="_parent";
					document.logout.submit();
					
				} else {
					return false;
				}
			}
		}
	}

	//calling this function from ESMenuIndex.jsp
	function Xclose(){
				var TOP		= screen.availHeight;
				var LEFT	= screen.availWidth;
		//alert(parent.mskey);
	if(parent.mskey=="obul"){
	newWin = window.open("",'logout1', "width="+LEFT+", height="+TOP+",top=0,left=0, toolbar=no,status=no,location=no,menubar=no,directories=no,scrollbars=no,resizable=no");
	
	document.logout.target="logout1";
	document.logout.submit();
	}
	
	}

	function showLever() 
	{
		document.links.item("switch").style.visibility = "visible";
		document.links.item("switch").focus();
	}
var blnIsOpen;

blnIsOpen = true;
function OpenClose()
{
     if (blnIsOpen) {
          parent.TreeFrame.cols = "0, *";
          blnIsOpen = false;
     }else{
          parent.TreeFrame.cols = "175,*";
          blnIsOpen = true;
     }
}
</script>
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server

	response.setHeader("Expires", "0"); // For I.E 6.0 
	response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	response.setHeader("Pragma", "public");
%>
</head>
<%
	if(	(userType.equals("ELG") && accessType.equals("WAREHOUSE")) ||
	    (userType.equals("ESP") && accessType.equals("WAREHOUSE")) ||
		(userType.equals("EEP") && accessType.equals("COMPANY")) 	)
	{
%>
<BODY onLoad="populate()" >
<%
}
else
{
%>
<BODY >
<%
}
%>
<form>
<table border='0' cellspacing='0' cellpadding='0' width='100%' height='30' bgcolor='#C0C0C0'>
  <tr>
	<td valign='middle'>
	 <table border='0' cellspacing='0' cellpadding='0' height='24' bgcolor='#C0C0C0'>
	  <tr>  
		<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="http://www.dhl.com" target = "_target"><IMG SRC="images/DHL.gif" ALT="DHL Home" BORDER="0" onmouseover="status = 'DHL Home';return true;" onmouseout="status = '';return true;" height="22" width="155"></a></td>
		<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:void(0);" onClick="goBack();" ><IMG SRC="images/Toolbar_backward.gif" ALT="Back" BORDER="0" onmouseover="status = 'Back';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
		<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:void(0)" onClick="goFwd();"><IMG SRC="images/Toolbar_forward.gif" ALT="Forward" BORDER="0" onmouseover="status = 'Forward';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
		<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:void(0)" onClick="javascript:parent.frames[3].document.clear();"><IMG SRC="images/Toolbar_stop.gif" ALT="Stop" BORDER="0" onmouseover="status = 'Stop';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
		<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:parent.frames[3].location.reload();"><IMG SRC="images/Toolbar_refresh.gif" ALT="Refresh" BORDER="0" onmouseover="status = 'Refresh';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
		<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="" onClick="printCommand(); return false;"><IMG SRC="images/Toolbar_print.gif" ALT="Print" BORDER="0" onmouseover="status = 'Print';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
		<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="http://www.dhl.com" target = "_target"><IMG SRC="images/Toolbar_home.gif" ALT="Home www.dhl.com" BORDER="0" onmouseover="status = 'Home www.dhl.com';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
		<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>
		
		
		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="ESACPasswordChangeView.jsp" target=text ><IMG SRC="images/Toolbar_changePassword.gif" ALT="Change Profile" BORDER="0" onmouseover="status = 'change Password';return true;" onmouseout="status = '';return true;" width="16" height="16" ></a></td>
		<td valign="middle" WIDTH="4" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/empty.gif" height="16" width="4"></td>

		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a name="logoutLink" href="" onClick="unloader(); return false;"><IMG onClick="" name="logoutImage" SRC="images/Toolbar_logout.gif" ALT="Logout (F12)" BORDER="0" onmouseover="status = 'Logout';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
	
		<td valign="middle" WIDTH="12" HEIGHT="24"BGCOLOR="#C0C0C0"><IMG SRC="images/sep.gif" height="16" width="12"></td>

		<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:showAboutPage()" ><IMG SRC="images/Toolbar_about.gif" ALT="About" BORDER="0" onmouseover="status = 'About';return true;" onmouseout="status = '';return true;" height="16" width="16"></a></td>
    <%
	    if("LICENSEE".equalsIgnoreCase(loginbean.getUserLevel()))
		{%>
			<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href='javascript:showHelpPage("help/Licensee Level/!SSL!/WebHelp_Pro/Licensee_Level.htm")' ><IMG SRC="images/Toolbar_help.gif" ALT="Quote Shop Online Help" BORDER="0" onmouseover="status = 'Quote Shop Online Help';return true;" onmouseout=" status = '';return true;" height="16" width="16"></a></td>
	<% }
	   else
	   {%>
		   <td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href='javascript:showHelpPage("help/Quote shop Online Help/!SSL!/WebHelp_Pro/Quote_shop_Online_Help.htm")' ><IMG SRC="images/Toolbar_help.gif" ALT="Quote Shop Online Help" BORDER="0" onmouseover="status = 'Quote Shop Online Help';return true;" onmouseout=" status = '';return true;" height="16" width="16"></a></td>
	<% }%>
		<!-- <td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0"><a href="javascript:openChat()" ><IMG SRC="images/Toolbar_customersupport.gif" ALT="Customer Support" BORDER="0" onmouseover="status = 'Customer Support';return true;" onmouseout=" status = '';return true;" height="16" width="16"></a></td> -->
		<td valign="middle"><a href="javascript:OpenClose()"><IMG SRC="images/show.gif" ALT="Hide Tree" height="12" width="10"></a></td>
		<td colspan=2>&nbsp;</td>
		<td align=center width=100% ><b><%= loginbean.getUserId() %> <%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100322")%> <%= loginbean.getLocationId() %> (<%= UserAccessUtility.getAccessTypeLable(loginbean.getAccessType() ) %>)</font></b></td>
<!--        <td align=center width=100% >Business Delegation</td> -->
<%
	if(	(userType.equals("ELG") && accessType.equals("WAREHOUSE")) ||
	    (userType.equals("ESP") && accessType.equals("WAREHOUSE")) ||
		(userType.equals("EEP") && accessType.equals("COMPANY")) 	)
	{
		if(warehouseList!=null && warehouseList.size() > 0) {
%>
			<td valign="middle" WIDTH="20" HEIGHT="24" BGCOLOR="#C0C0C0">
			  <a id="switch" href="javascript:void(0)" onClick="shiftWH();" >
				<IMG SRC="images/warehousego.gif" ALT="Switch To Action" BORDER="0" onmouseover="status = 'Switch To Action';return true;" onmouseout="status = '';return true;" height="16" width="16">
			  </a>
			</td>
			<script language="javascript">
				document.links.item("switch").style.visibility = "hidden";
			</script>
			<td colspan=2 valign="bottom">
				<select id="custWH" name="custWH" onChange="return shiftWH();"></select>&nbsp;&nbsp;
			</td>
<%
		}
	}
%>
	   </tr>
	  </table>
	</td>
  </tr>
</table>

<input type="hidden" name="refresh" value="">
</form>
<form name=logout action="ESLogout">
<input type="hidden" name="from" value="logout">
</form>
</body>
</html>
<%
	} catch(Exception ex) {
		errorOcurred = true;
		inValidateSession( request );
		//Logger.error(fileName, "Log-in process was terminated as an error occurred while opening application operations page.");				
    logger.error(fileName+ "Log-in process was terminated as an error occurred while opening application operations page.");				
		request.setAttribute("Login_Message",errorMessage);
		%><jsp:forward page="<%=loginPage%>" /><%
	} finally {
		if(errorOcurred==false) {
			request.setAttribute("windowName", windowName);
		}
	}

%>
<%!
	private void inValidateSession( HttpServletRequest request ) {

		try {
			if(request!=null) {
				HttpSession		session	=	request.getSession();
				if(session!=null) {
					session.invalidate();
					//Logger.error(fileName, "Log-in process was terminated as an error occurred while logging in.");				
          logger.error(fileName+ "Log-in process was terminated as an error occurred while logging in.");				
				}
			}
		} catch(Exception ex) {}
		
	}
%>
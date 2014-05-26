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
 % File			: ESMenuCRMRightPanel.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display menulist of modules for perticular user and perticular group. 
 % Group means setup ,operation ,reports etc.
 % 
 % author		: Sasi Bhushan. P
 % date			: 22-12-2001
 % 
--%>
<%@ page import = "com.foursoft.esupply.accesscontrol.java.MenuModule,
					com.foursoft.esupply.accesscontrol.java.UserInterface,
					com.foursoft.esupply.accesscontrol.util.CRMMenuUtility,
					java.util.Hashtable,
					org.apache.log4j.Logger,java.util.ArrayList,
					com.foursoft.esupply.accesscontrol.java.CRMUIPermissions,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" 
%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	String fileName	= "ESMenuCRMRightPanel.jsp";				
%>
<%
      logger  = Logger.getLogger(fileName);
	    String  language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
    
	//Logger.info(fileName, "session["+session.getId()+"] -- "+loginbean.getLocationId()+"/"+loginbean.getUserId()+" accessed : "+request.getParameter("link") );
  logger.info(fileName+ "session["+session.getId()+"] -- "+loginbean.getLocationId()+"/"+loginbean.getUserId()+" accessed : "+request.getParameter("link") );
	try
	{
		if(loginbean.getUserId() == null)
		{
			request.setAttribute("ErrorMessage","Users Session Expired! Please ReLogin ");
	%>
		<jsp:forward page="ESACSessionExpired.jsp" />
	<%		
		}
	}
	catch(NullPointerException ne)
	{
	}	
%>
<%
    CRMUIPermissions uiPermissionsETCRM = CRMUIPermissions.getInstance();
    /*<jsp:useBean id ="uiPermissionsETCRM" class= "com.foursoft.esupply.accesscontrol.java.CRMUIPermissions" scope ="application"/>*/
%>
<%
	String browserName = (String)session.getAttribute("browserName");
	String linkName  = request.getParameter("link");
	//Logger.info(fileName," Browsername is:"+browserName);
	//Logger.info(fileName," BEFORE STARTING MENU:"+new java.sql.Timestamp(new Date().getTime()));
	Hashtable alevel = null;
	//Logger.info(fileName,"  AFTER GETTING HASHTABLE AND transactionsTable"+uiPermissions.transactionsTable);
	ArrayList tabList = (ArrayList)uiPermissionsETCRM.transactionsTable.get(linkName);
	//Logger.info(fileName,"  AFTER GETTING ARRAYLIST");
	String [] scriptArray;
	alevel = (Hashtable)session.getAttribute("accessList");
	
	scriptArray = new CRMMenuUtility().getScript(tabList,alevel);
	//Logger.info(fileName," AFTER MENU:"+new java.sql.Timestamp(new Date().getTime()));

%>
<HTML>
<HEAD>
<style>
.menuItem
{
 padding-left:12px;
 padding-right:12px;
 font-size:10pt;
 font-family:verdana;
}
.tdClass
{
	font-family:verdana;
	font-size:12;
	color:white;
	font-weight:bold;
}
a:link
{
	font-family:verdana;
	font-size:12;
	color:white;
	/*font-weight:bold;*/
	text-decoration:none;
}

a:visited
{
	font-family:verdana;
	font-size:12;
	color:white;
	text-decoration:none;
}
</style>
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT LANGUAGE="JavaScript">
menuCounter=<%=Integer.parseInt(scriptArray[2])%>;
var menuFlag=false;
function hideAll(j)
{
	if(j==1)
		menuFlag=false;
	if(!menuFlag)
	{
		for(i=0;i<menuCounter;i++)
		{
			document.getElementById("divMenu"+i).style.visibility="hidden";
		}
	}
}
function showMenu(i)
{	
	hideAll(1);
	menuFlag=true;
	document.getElementById("divMenu"+i).style.left=document.getElementById("tdItem"+i).offsetLeft;
	document.getElementById("divMenu"+i).style.top=document.getElementById("tdItem"+i).offsetHeight+5;
	document.getElementById("divMenu"+i).style.visibility="visible";
}
function change(obj,i)
{
if(i==0)
	obj.bgColor='#6699CC';
else
	obj.bgColor='#006699';

}
</SCRIPT>
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
</HEAD>
<BODY  bgcolor=#e5e5e5 leftmargin=0 topmargin=0 onLoad="hideAll();">
<div id="clearAll" style="position:absolute; top:23px; left:0px; width:100%; height:100%;" onMouseOver="hideAll();">
</div>
<%=scriptArray[0]%>
<%=scriptArray[1]%>
</BODY>
</HTML>
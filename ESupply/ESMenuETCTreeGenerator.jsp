<%@ page import= "org.apache.log4j.Logger, java.util.*,java.io.*,java.net.URL,com.foursoft.esupply.accesscontrol.util.CRMTreeUtility" %>
<%@ page import="com.foursoft.esupply.common.exception.FoursoftException,com.foursoft.esupply.accesscontrol.java.CRMUIPermissions,java.util.ResourceBundle"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
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
 % File			: ESMenuTreeGenerator.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to Generate the Tree
 % 
 % author		: Sasi Bhushan. P
 % date			: 22-12-2001
--%>
<%!
  private static Logger logger = null;
	String fileName	= "ESMenuETCTreeGenerator.jsp";   
   
%>
<%
    logger  = Logger.getLogger(fileName);
      String  language = "";
  CRMUIPermissions uiPermissionsETCRM = null;
  CRMTreeUtility treeUtilityETCRM     = null;
	    language = loginbean.getUserPreferences().getLanguage();
%>
	
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<%
    uiPermissionsETCRM = CRMUIPermissions.getInstance();
/*<jsp:useBean id ="uiPermissionsETCRM" class= "com.foursoft.esupply.accesscontrol.java.CRMUIPermissions" scope ="application">
</jsp:useBean>*/
%>


<%
    
	URL u = application.getResource("/WEB-INF/xml/menu-config-etc.xml");
	String configFile = u.toString();
	
	//Logger.info(fileName,"configFile : "+configFile);
  logger.info(fileName+"configFile : "+configFile);
	long presentTime = new java.io.File(configFile).lastModified();

	if(uiPermissionsETCRM.transactionsTable == null)
		uiPermissionsETCRM.process(configFile);	
	
	Hashtable proTable = null;
	Hashtable alevel = null;

	alevel = (Hashtable)session.getAttribute("accessList");

%>
<%
   treeUtilityETCRM = CRMTreeUtility.getInstance(); 
/*<jsp:useBean id ="treeUtilityETCRM" class= "com.foursoft.esupply.accesscontrol.util.CRMTreeUtility" scope ="application">
</jsp:useBean>*/
%>
<%
	URL u1	=	application.getResource("/WEB-INF/xml/tree-config-etc.xml");
	String treeFile = u1.toString();
	
	//Logger.info(fileName,"treeFile : "+treeFile);
  logger.info(fileName+"treeFile : "+treeFile);
	
	presentTime = new java.io.File(application.getResource("/WEB-INF/xml/tree-config-etc.xml").getFile()).lastModified();

	session.setAttribute("proTable",alevel);
	treeUtilityETCRM.setMenuListTable(uiPermissionsETCRM.getLeafTable(alevel));

	try
	{
		if(treeUtilityETCRM.xmlDocument == null)
		{			
			treeUtilityETCRM.setTreeFile( treeFile );
			treeUtilityETCRM.parse();
		}
	}
	catch(FoursoftException fse)
	{
		//Logger.warning(fileName,"IN FOURSOFT EXCEPTION  "+fse.toString());
    logger.warn(fileName+"IN FOURSOFT EXCEPTION  "+fse.toString());
		String errorMessage = fse.toString();
		request.setAttribute("Login_Message",errorMessage);
	}
	catch(Exception e)
	{
		//Logger.warning(fileName,"EXCEPTION OCCURED WHILE PARSING "+e.toString());
    logger.warn(fileName+"EXCEPTION OCCURED WHILE PARSING "+e.toString());
	}
	//Logger.info(fileName," AFTER MENU:"+new java.sql.Timestamp(new java.util.Date().getTime()));
  logger.info(fileName+" AFTER MENU:"+new java.sql.Timestamp(new java.util.Date().getTime()));
%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>New Page 5</title>
<style>
.menulines{border:1px ;}
.menulines a{text-decoration:none;}
</style>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript1.2">
function over_effect(e,state){
	if (document.all)
		source4=event.srcElement
	else if (document.getElementById)
		source4=e.target
	if (source4.className=="menulines")
		source4.style.borderStyle=state
	else{
		while(source4.tagName!="TABLE"){
			source4=document.getElementById? source4.parentNode : source4.parentElement
			if (source4.className=="menulines")
			source4.style.borderStyle=state
		}
	}
}
</script>
</head>
<body topmargin="10" leftmargin="0"  background="images/bg_CrmMenu.gif">
<table border="0"  cellspacing="1" cellpadding="2" onMouseOver="over_effect(event,'outset')" onMouseOut="over_effect(event,'')" onMouseDown="over_effect(event,'inset')" onMouseUp="over_effect(event,'outset')" >
  <tr> 
    <td ><img src="images/ecrm.gif" width="120" height="48"></td>
  </tr>
  <tr> 
    
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    
  </tr>
  <tr> 
    <td class="menulines"><img src="images/arrow.gif" width="12" height="11"><a href="html/ETCHome.html" target="main"><font face="Arial" size="2" color="#ffffff"><fmt:message key="100321" bundle="${lang}"/></font></a></td>
  </tr>
<%
	String mustr = treeUtilityETCRM.process();
%>
	<%=mustr%>
</table>
</body>
</html>

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
 % File			: ESLoggingActivityReport.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the Usage Activities View filtered on User
 % 
 % author		: Madhu. P
 % date			: 10-01-2002
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,com.foursoft.esupply.common.java.ReportFormatter,org.apache.log4j.Logger,com.foursoft.esupply.common.java.UserLogVOB,com.foursoft.esupply.common.java.ErrorMessage,com.foursoft.esupply.common.java.KeyValue,java.sql.Timestamp,	java.util.ArrayList,java.text.DateFormat,java.util.Locale,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:useBean id="dateUtility"  class="com.foursoft.esupply.common.util.ESupplyDateUtility" scope="session"/>

<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"%>

<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
	String fileName		= "ESLoggingReportView.jsp";
	String title		= "Usage Statistics";
%>
<%   
  logger  = Logger.getLogger(fileName);
  String forName="";
	String  language = "";
  ReportFormatter formatter	= null;
	//in the above code line breaks are removed to reduce empty rows in the xl sheet
  language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
   if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ETRANS))
   {  
    response.setHeader("Cache-Control","no-cache");  
    response.setHeader("Pragma","no-cache"); 
    response.setDateHeader ("Expires", -1); 
   }	
   formatter	= (ReportFormatter)session.getAttribute("report");
   String pageNo = request.getParameter("pageNo");	
   String locationId = (String)session.getAttribute("locationId");
   String userId = (String)session.getAttribute("userId");	
    forName = "'"+ userId +"' of '"+ locationId +"'";	
   if(pageNo == null)
   {
    pageNo = "1";
    formatter.currentPageIndex = 1;
   }
   else
   {
    formatter.currentPageIndex = Integer.parseInt(pageNo);
   }
   String logDetails ="";
   String openFormat	= (String)session.getAttribute("openFormat");
   if(openFormat.equals("xls"))
   { 
     response.setContentType( "application/vnd.ms-excel" );
     response.setHeader("Content-disposition", "attachment; filename="+"View.xls" );
     logDetails = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100289")+"\t"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100290")+"\t"+	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100258")+"\t"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100291")+"\n";
     for(int k=1; k<=formatter.getNoOfPages(); k++)
     { 
       Object[]	userLogList = formatter.getCurrentPageData(k);	
       if(userLogList != null && userLogList.length > 0)
       {
         UserLogVOB userLogVOB = null;
         for(int i=0; i < userLogList.length; i++)
         {	
           userLogVOB	= (UserLogVOB)userLogList[i];
           logDetails += dateUtility.getDisplayString(userLogVOB.documentDate)+"\t"+userLogVOB.documentType+"\t"+userLogVOB.documentRefNo+"\t"+userLogVOB.transactionType+"\n";
         }
       }
     }
     if(formatter.getNoOfPages()==0)
     {
     logDetails=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100028");
     }
     out.print(logDetails);
   }
   else
   {%>

<html>
<head>
<title><fmt:message key="100288" bundle="${lang}"/></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<style type="text/css">
PRE {
	font-size : 9pt;
}
A {
	font-weight : bold;
	text-decoration : none;
}
A:hover {
	text-decoration : underline;
}
tr.report {
	background-color: #f2f2f9;
	border-width : thin;
	font-family : Verdana;
	font-size : 10px;
}
tr.report1 {
	background-color: #E7F2F5;
	border-width : thin;
	font-family : Verdana;
	font-size : 10px;
}
tr.heading {
	background-color: #b3b3d9;
	border-width : thin;
	font-family : Verdana;
	font-size : 11px;
	font-weight : bold;	
}	
</style>
<%@ include file="/ESEventHandler.jsp" %>
<script>
function setURL()
{
	document.forms[0].action="ESLoggingEnterId.jsp?UIName=View"+"";
	alert(document.forms[0].action)
	document.forms[0].submit();
}
</script>
</head>
<body bgcolor="#ffffff"
 link="#003399" 
 vlink="#990099"
 alink="#99ccff"  marginheight="0" topmargin="0">
<form method="GET" action="ESLoggingEnterId.jsp">


<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>
	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><%= title %> - <%= forName %></td>
		  <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"")%></td>
    	</tr>
	</table>
	
	<table width="100%" cellpadding="3" cellspacing="1" border="0">
<%	
	//System.out.println("Integer.parseInt(pageNo) :: "+Integer.parseInt(pageNo));
	Object[]	userLogList = formatter.getCurrentPageData(Integer.parseInt(pageNo) );
	//System.out.println("userLogList : "+userLogList.length);
	if(userLogList != null && userLogList.length > 0)
	{
		UserLogVOB userLogVOB = null;
%> 
	    <tr class="formheader" valign="top">
			<td width="12%"><fmt:message key="100289" bundle="${lang}"/></td>
			<td width="12%"><fmt:message key="100290" bundle="${lang}"/></td>
			<td width="18%"><fmt:message key="100258" bundle="${lang}"/></td>
			<td width="15%"><fmt:message key="100291" bundle="${lang}"/></td>
		</tr>
   <br>
<%	for(int i=0; i < userLogList.length; i++)	{
		userLogVOB	= (UserLogVOB)userLogList[i];
%>
		<tr valign="top" class="formdata" align="left"> 
			<td width="12%"><%= dateUtility.getDisplayString(userLogVOB.documentDate)%></td>
			<td width="15%"><%= userLogVOB.documentType %></td>
			<td width="15%"><%= userLogVOB.documentRefNo %></td>
			<td width="15%"><%= userLogVOB.transactionType %></td>
		</tr>
		
<%	} %>

		<tr  class="formdata"> 
		  <td colspan=4>
<%--
	//System.out.println("C : T : S : "+formatter.currentPageIndex+" : "+formatter.getNoOfPages()+" : "+formatter.noOfSegments );
--%>
Page: <pageIterator:PageIterator currentPageNo="<%= formatter.currentPageIndex %>" noOfPages="<%= formatter.getNoOfPages() %>" noOfSegments="<%= formatter.noOfSegments %>" fileName="<%= fileName %>"/>
		  </td>
		</tr>
<%	}
	else
	{
		//out.println("<tr class='formdata'><td colspan=6><textarea name=data rows = 6 cols = 75>"+bundle.getBundle().getString("100028")+" </textarea></td></tr>");
		String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100028");
        ErrorMessage errMsg = new ErrorMessage(errorMessage, "ESLoggingEnterId.jsp","AE-30016","");
		ArrayList keyVlaueList = new ArrayList();
		keyVlaueList.add(new KeyValue("UIName", "View") );
		//keyVlaueList.add(new KeyValue("action", "ADD") );
		errMsg.setKeyValueList(keyVlaueList);
        request.setAttribute("errorMessage", errMsg);
	%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
	<%
	}
%>
		<tr>
		  <td colspan=4 align="right">
		  	<input type=hidden name="UIName" value='<fmt:message key="100006" bundle="${lang}"/>'>
      		<input type=submit name=continue value='<fmt:message key="7777" bundle="${lang}"/>'  class="input">
    	  </td>
		</tr>
	
	</table>
	
  </td>
 </tr>
</table>
	
</form>
</body>
</html>
<%}%>
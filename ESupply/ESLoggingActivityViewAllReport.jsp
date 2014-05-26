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
 % File			: ESLoggingActivityViewAllReport.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the Usage Activities View All
 % 
 % author		: Sasi Bhushan. P
 % date			: 10-01-2002
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters, com.foursoft.esupply.common.java.ReportFormatter, org.apache.log4j.Logger, com.foursoft.esupply.common.java.UserLogVOB, java.sql.Timestamp, java.text.DateFormat, java.util.Locale, java.util.Date,javax.servlet.jsp.jstl.fmt.LocalizationContext, java.util.ArrayList,com.foursoft.esupply.common.java.ErrorMessage,com.foursoft.esupply.common.java.KeyValue,java.util.ResourceBundle" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:useBean id="dateUtility"  class="com.foursoft.esupply.common.util.ESupplyDateUtility" scope="session"/>

<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"%>

<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>

<%
	   String language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>


<%
ReportFormatter formatter	= (ReportFormatter)session.getAttribute("report");
String locationId = (String)session.getAttribute("locationId"); String logDetails =""; String openFormat	= (String)session.getAttribute("openFormat");
   if(openFormat.equals("xls")){
	   
 response.setContentType( "application/vnd.ms-excel" );
 //@@ Commented and added by subrahmanyam for the wpbn id:201363
/*
 response.setHeader("Content-disposition", "filename=\"ViewAll.xls\";" );
   logDetails = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100271")+"\t"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100289")+"\t"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100290")+"\t"+	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100258")+"\t"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100291")+"\n";
   for(int k=1; k<=formatter.getNoOfPages(); k++){
    Object[]	userLogList = formatter.getCurrentPageData(k);
	if(userLogList != null && userLogList.length > 0){	UserLogVOB userLogVOB = null;
	for(int i=0; i < userLogList.length; i++){
			userLogVOB	= (UserLogVOB)userLogList[i];	if((userLogVOB.userId!=null)&&(dateUtility.getDisplayString(userLogVOB.documentDate)!=null)&&(userLogVOB.documentType!=null)&&(userLogVOB.documentRefNo!=null)&&(userLogVOB.transactionType!=null)){
			logDetails +=userLogVOB.userId+"\t"+ dateUtility.getDisplayString(userLogVOB.documentDate)+"\t"+userLogVOB.documentType+"\t"+userLogVOB.documentRefNo+"\t"+userLogVOB.transactionType+"\n";}
			}	}   }if(formatter.getNoOfPages()==0){logDetails=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100028");}
  
    out.print(logDetails);
*/
//response.setHeader("Content-disposition", "filename=\"ViewAll.xls\";" );
 response.setHeader("Content-Disposition","attachment;filename=DHL Usage Statistics.xls");
   logDetails = "<html><body><table border=1><TR><TD><b>"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100271")+"</b></TD><TD><b>"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100289")+"</b></TD><TD><b>"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100290")+"</b></TD><TD><b>"+	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100258")+"</b></TD><TD><b>"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100291")+"</b></TD></TR>";
   for(int k=1; k<=formatter.getNoOfPages(); k++){
    Object[]	userLogList = formatter.getCurrentPageData(k);
	if(userLogList != null && userLogList.length > 0){	UserLogVOB userLogVOB = null;
	for(int i=0; i < userLogList.length; i++){
			userLogVOB	= (UserLogVOB)userLogList[i];	if((userLogVOB.userId!=null)&&(dateUtility.getDisplayString(userLogVOB.documentDate)!=null)&&(userLogVOB.documentType!=null)&&(userLogVOB.documentRefNo!=null)&&(userLogVOB.transactionType!=null)){
			logDetails +="<TR><TD>"+userLogVOB.userId+"</TD><TD>"+ dateUtility.getDisplayString(userLogVOB.documentDate)+"</TD><TD>"+userLogVOB.documentType+"</TD><TD>"+userLogVOB.documentRefNo+"</TD><TD>"+userLogVOB.transactionType+"</TD></TR>";}
			}	}   }if(formatter.getNoOfPages()==0){logDetails=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100028");}
  
    out.print(logDetails+"</table></body></html>");
	//@@ Ended by subrahmanyam for the wpbn id:201363
   }
  
else{
	//in the above code line breaks are removed to reduce empty rows in the xl sheet
%>
<%!
  private static Logger logger = null;
	String fileName		= "ESLoggingActivityViewAllReport.jsp";
	String title		= "Usage Statistics ";
%>
<% 
  logger  = Logger.getLogger(fileName);
if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ETRANS))
{
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
}
%>
<html>
<head>
<title><fmt:message key="100296" bundle="${lang}"/></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<style type="text/css">
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
	document.forms[0].action="ESLoggingEnterId.jsp?UIName=View";
	document.forms[0].submit();
}
</script>
<%	
	String pageNo = request.getParameter("pageNo");	
	if(pageNo == null)
	{
		pageNo = "1";
		formatter.currentPageIndex = 1;
	}
	else
	{
		formatter.currentPageIndex = Integer.parseInt(pageNo);
	}
%>
</head>
<body>
<form method="GET" action="ESLoggingEnterId.jsp">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>


	<table width="100%" class='formlabel' border="0">
    	<tr>
		  <td class="formlabel" colspan=2><%= title %> - <%= locationId %></td>
		  <td class="formlabel" colspan=2 align="right"><%=loginbean.generateUniqueId(fileName,"")%></td>
    	</tr>
	</table>
	
	<table width="100%" cellpadding="3" cellspacing="1" border="0">
<%

	//System.out.println("Integer.parseInt(pageNo) :: "+Integer.parseInt(pageNo));
	Object[]	userLogList = formatter.getCurrentPageData(Integer.parseInt(pageNo) );
	if(userLogList != null && userLogList.length > 0)
	{
		UserLogVOB userLogVOB = null;
%> 
	    <tr class="formheader" valign="top">
			<td width="10%"><fmt:message key="100271" bundle="${lang}"/></td>
			<td width="15%"><fmt:message key="100289" bundle="${lang}"/></td>
			<td width="12%"><fmt:message key="100290" bundle="${lang}"/></td>
			<td width="15%"><fmt:message key="100258" bundle="${lang}"/></td>
			<td width="15%"><fmt:message key="100291" bundle="${lang}"/></td>
		</tr>
<br>
<%	for(int i=0; i < userLogList.length; i++)	{
		userLogVOB	= (UserLogVOB)userLogList[i];
		if((userLogVOB.userId!=null)&&(dateUtility.getDisplayString(userLogVOB.documentDate)!=null)&&(userLogVOB.documentType!=null)&&(userLogVOB.documentRefNo!=null)&&(userLogVOB.transactionType!=null)){
%>
		<tr valign="top" class="formdata" align="left">
			<td width="10%"><%= userLogVOB.userId%></td> 
			<td width="15%"><%= dateUtility.getDisplayString(userLogVOB.documentDate)%></td>
			<td width="12%"><%= userLogVOB.documentType %></td>
			<td width="15%"><%= userLogVOB.documentRefNo %></td>
			<td width="15%"><%= userLogVOB.transactionType %></td>
		</tr>
<%}	} %>
		<tr  class="formdata"> 
		  <td colspan=5>
<%--
	//System.out.println("C : T : S : "+formatter.currentPageIndex+" : "+formatter.getNoOfPages()+" : "+formatter.noOfSegments );
--%>

		Page:<pageIterator:PageIterator currentPageNo="<%= formatter.currentPageIndex %>" noOfPages="<%= formatter.getNoOfPages() %>" noOfSegments="<%= formatter.noOfSegments %>" fileName="<%= fileName %>"/>
		  </td>
		</tr>
<%	}
	else
	{
		//out.println("<tr class='formdata'><td colspan=6><textarea name=data rows = 6 cols = 75>"+bundle.getBundle().getString("100028")+" </textarea></td></tr>");
		String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100028");
        ErrorMessage errMsg = new ErrorMessage(errorMessage, "ESLoggingEnterId.jsp","AE-30016","");
		ArrayList keyVlaueList = new ArrayList();
		keyVlaueList.add(new KeyValue("UIName", "ViewAll") );
		//keyVlaueList.add(new KeyValue("action", "ADD") );
		errMsg.setKeyValueList(keyVlaueList);
        request.setAttribute("errorMessage", errMsg);
		%>
		<jsp:forward page="ESupplyMessagePage.jsp" />
		<%
	}
%>
		<tr>
		  <td colspan=5 align="right">
		  	<input type=hidden name="UIName" value="ViewAll">
      		<input type=submit name=continue value='<fmt:message key="7777" bundle="${lang}"/>' class="input">
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

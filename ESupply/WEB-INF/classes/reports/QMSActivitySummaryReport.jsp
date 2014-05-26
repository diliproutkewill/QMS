<%--
% 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.0 
%
--%>
<%@ page import = "com.foursoft.esupply.common.util.ESupplyDateUtility,java.util.HashMap" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
      private static final  String  FILE_NAME = "QMSActivitySummaryReport.jsp" ;
%>
<%
HashMap                 summaryDetails      =   null;
ESupplyDateUtility		eSupplyDateUtility	=	new ESupplyDateUtility();
String					dateFormat			=	loginbean.getUserPreferences().getDateFormat();
String					currentDate			=	eSupplyDateUtility.getCurrentDateString(dateFormat);
try{
     summaryDetails =(HashMap)request.getAttribute("summaryDetails"); 
   }
   catch(Exception e)
   {
     e.printStackTrace();
   }
%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Yield Report</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
	<body>
		<form>
          <table border="0" cellPadding="4" cellSpacing="1" width='100%' bgcolor="#FFFFFF">
              <tr class="formlabel" vAlign="top">
                <td align="center" colspan="2">Activity Summary Report</td>
              </tr>
			  <tr class="formdata" vAlign="middle">
                <td align="left">Terminal Id : <%=loginbean.getTerminalId()%></td>
				<td align="left">User Id : <%=loginbean.getUserId()%> &nbsp;&nbsp;&nbsp;&nbsp; Date : <%=currentDate%></td>				
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left" colspan="2" ><B>PENDING QUOTES</B></td>
              </tr>
			  <!-- in this page at the hyperlink position, &fromSummary=yes is added by subrahmanyam for 146463-->
			  <tr class="formdata" vAlign="top">
                <td align="left">Pending Quotes Requiring Follow up<BR>with Customer for Acceptance :</td>
				<td align="left"><B><a href="QMSReportController?Operation=pendingquotes&wiseoptions=SP&salesPersonCode=<%=loginbean.getEmpId()%>&noofrecords=<%=loginbean.getUserPreferences().getLovSize()%>&subOperation=Html&fromSummary=yes">  ( <%=summaryDetails.get("pending")%> ) Quotes</a></B></td>
              </tr>
			  <tr class="formdata"vAlign="top">
                <td align="left" colspan="2" ><B>ESCALATED QUOTES</B></td>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left">Approval Required By :</td>
				<td align="left"><B><%="0".equalsIgnoreCase((String)summaryDetails.get("self"))?"":"<a href='QMSReportController?Operation=Escalated&subOperation=html/excel&radiobutton=self&fromSummary=yes'>"%>Self&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;(<%=summaryDetails.get("self")%>) Quotes</a></B><BR><B><%="0".equalsIgnoreCase((String)summaryDetails.get("others"))?"":"<a href='QMSReportController?Operation=Escalated&subOperation=html/excel&radiobutton=others&fromSummary=yes'>"%>Others&nbsp;:&nbsp;(<%=summaryDetails.get("others")%>) Quotes</a></B></td>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left" colspan="2" ><B>APPROVED QUOTES</B></td>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left">Approved Quotes Not Sent To Customer :</td>
				<td align="left"><B><a href="QMSReportController?Operation=Approved&fromSummary=yes">( <%=summaryDetails.get("approved")%> ) Quotes</a></B></td>				
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left" colspan="2" ><B>REJECTED QUOTES</B></td>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left">Quotes Rejected by the Manager :</td>
				<td align="left"><B><a href="QMSReportController?Operation=Rejected&fromSummary=yes">( <%=summaryDetails.get("rejected")%> ) Quotes</a></B></td>				
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left" colspan="2" ><B>UPDATED QUOTES</B></td>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left">Updated Quotes but Not Confirmed :</td>
				<td align="left"><B><a href="QMSReportController?Operation=updatedQuotes&subOperation=activitySummary&fromSummary=yes">( <%=summaryDetails.get("updated")%> ) Quotes</a></B></td>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left" colspan="2" ><B>EXPIRING QUOTES</B></td>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td align="left">Expired Quotes Not Updated :</td>
				<td align="left"><B><a href="QMSReportController?Operation=expiryquotes&noofrecords=<%=loginbean.getUserPreferences().getLovSize()%>&subOperation=html&expDate=Y&fromSummary=yes&salesPersonCode=<%=loginbean.getEmpId()%>">( <%=summaryDetails.get("expired")%> ) Quotes</a></B></td><!--from noofrecords parameters included by shyam  -->
              </tr>
			  
		  </table>
		</form>
   </body>
</html>
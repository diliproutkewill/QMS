<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSActivityReport.jsp
Product Name	: QMS
Module Name		: PendigQuotesReports
Date started	: 16 September 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="javax.naming.InitialContext,
				 java.util.ArrayList,
				 java.util.HashMap,
				 com.foursoft.esupply.common.java.KeyValue,
				 com.qms.reports.java.ReportsEnterIdDOB,
				 com.qms.reports.java.ReportDetailsDOB,
				 com.qms.reports.ejb.sls.ReportsSession,
                 com.qms.reports.ejb.sls.ReportsSessionBeanHome,
				 com.qms.reports.java.YieldDetailsDOB,
		         com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.util.Logger,
				 com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
	private static final String FILE_NAME="QMSYieldReportDetails.jsp";
%>

<%

		 ErrorMessage errorMessageObject = null;
		 ArrayList	  keyValueList	 	 = new ArrayList();
			ArrayList detailsList = null;
			ArrayList brkList     = null;
			ArrayList yieldList   = null;	
			YieldDetailsDOB yieldDetailsDOB = null;
		try{
			  
			detailsList = (ArrayList) request.getAttribute("yielddetailsList");
			
				
			if(detailsList!=null && detailsList.size()>0)
			{

				 
					
%>
<html>
<head>
<title>YieldDetails Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
</script>
</head>
<body>
<form>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				Yield Details </td><td align='right'><!-- Screen Id goes here --></td></tr></table></td>
			  </tr>
			</table>
            
			 <table width="100%"  cellspacing="1" cellpadding="4">
				 <tr valign="top" class='formdata'>
					
				 	<td>Terminal Id:<%=loginbean.getTerminalId()%></td>
					<td>User Id: <%=loginbean.getUserId()%></td>
					<td>Date :<%=loginbean.getCurrentDateString()%> </td>
				 </tr>	
			</table>
			<table width="100%"  cellspacing="1" cellpadding="4">
				
				<tr class='formdata'><td align='center' colspan='21' nowrap> <b>Yield per weight Break<b></td></tr>
<%
				int size = 		detailsList.size();
				Logger.info("","size"+detailsList);
				for(int i=0;i<size;i++)
				{  
					 //Logger.info("",""+detailsList.get(i).getClass());
					yieldDetailsDOB = (YieldDetailsDOB)detailsList.get(i);
					brkList         = yieldDetailsDOB.getBrkPoint();
					yieldList		= yieldDetailsDOB.getYieldValue();

%>
					<tr valign="top" class='formheader'>
									<td>Customer Id </td>
									<!--@@Modified by Kameswari for the WPBN issue-38175-->
									<td NOWRAP>Customer Name</td><!--@@added by kameswari for the WPBN issue 30313-->
									<td>Quote Id </td>
									<td>Mode</td>
									<td>Service Level </td>
									<td>From Country </td>
									<td>From Location </td>
									<td>To Country </td>
									<td>To Location</td>
									<!--@@WPBN issue-38175-->
<%
									for(int j=0;j<brkList.size();j++)	
									{
%>
										<td nowrap><%=(String)brkList.get(j)%></td>
<%									}
									%>								

					</tr>					
					<tr valign="top" class='formdata'>
									<td><%=yieldDetailsDOB.getCustomerId()%> </td>
									<td><%=yieldDetailsDOB.getCustomerName()%> </td>
									<td><%=yieldDetailsDOB.getQuoteId()%> </td>
									<td><%=("1".equals(yieldDetailsDOB.getShipmentMode()) ?"Air" : ( "2".equals(yieldDetailsDOB.getShipmentMode())? "Sea":"Truck" )  )%></td>
									<td><%=yieldDetailsDOB.getServiveLevel()%></td>
									<td><%=yieldDetailsDOB.getOrgCountry()%></td>
									<td><%=yieldDetailsDOB.getOrgLocation()%></td>
									<td><%=yieldDetailsDOB.getDestCountry()%></td>
									<td><%=yieldDetailsDOB.getDestLocation()%></td>
<%
									for(int j=0;j<yieldList.size();j++)	
									{
%>
										<td nowrap><%=(String)yieldList.get(j)%></td>
<%									}
								
				}
%>				


			</table>
			<table width="100%"  cellspacing="1" cellpadding="4">
				
				<tr valign="top" class='denotes'>
				 <td align='right'><input type='button' class='input'  name='b1' value='Close' onclick='window.close()'></td>
				</tr>
			</table>

			
         </td>
	    </tr>
	  </table>


</form>
</body>
</html>
<%      }
		else
		{
			errorMessageObject = new ErrorMessage(  "No records found","ESMenuRightPanel.jsp?link=es-et-Administration-Reports"); 
            keyValueList       = new ArrayList(3);
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
			<%
		 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Logger.error(FILE_NAME,"Error in QMSYieldReport.jsp "+e);
			keyValueList       = new ArrayList(3);
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details","QMSReportController?Operation=buyratesExpiryReport"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
%>
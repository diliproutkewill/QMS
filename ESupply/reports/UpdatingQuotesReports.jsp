
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	:UpdatingQuotesReports.jsp
Product Name	: QMS
Module Name		: UpdatingQuotesReports
Date started	: 16 September 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="javax.naming.InitialContext,
				 java.util.ArrayList,
				 com.qms.reports.java.ReportsEnterIdDOB,
				 com.qms.reports.java.ReportDetailsDOB,
				 com.qms.reports.ejb.sls.ReportsSession,
         com.qms.reports.ejb.sls.ReportsSessionBeanHome,"%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
	private static final String FILE_NAME="UpdatingQuotesReports.jsp";
%>

<%
		ReportsSession          remote				=     null;
		ReportsSessionBeanHome   home					=     null;
		InitialContext			  initial	            =     null;
		ArrayList					dataList=new ArrayList();
		ReportsEnterIdDOB reportsenterdob=new ReportsEnterIdDOB();
		try
		{ 
			initial					=	  new InitialContext();	
			home                    =     (ReportsSessionBeanHome)initial.lookup("ReportsSessionBean");
			remote                  =     (ReportsSession)home.create();
			
			dataList=remote.getUpdatedQuotesReportDetails(reportsenterdob);
		}
		catch(Exception	e)
		{
			e.printStackTrace();
			//Logger.error(fileName,"Exception while calling remote method", e.toString());
		}

%>



<html>
  <head>
	<title>Customer Contracts - Add</title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">

  </head>

  <body>
	<form method='POST' action='' onSubmit=''>
	  <table width="800" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="800" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="790" border="0" ><tr class='formlabel'>
			      <td>Pending Quotes  - View Module </td>
			      <td align=right>&nbsp;</td></tr></table></td>
			  </tr>
		    </table>
			  
        <table width="800"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan=4>&nbsp;
				</td>
				
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td width="174" >Terminal Id:<br>
              <%=loginbean.getTerminalId()%>
				</td>
				
            <td width="229">User:<br>
             <%=loginbean.getUserId()%>
				</td>
				
            <td width="209"> Date:<br>
             <%=loginbean.getCurrentDateString()%>
			  </td>
			 
		  </tr>
			</table>
				<table width="800"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>
			    <td>Conform & Send</td>
			    <td>Important</td>
			    <td>Quote Id </td>
			    <td>Mode </td>
			    <td>Service Level </td>
			    <td>From Country </td>
			    <td>From Location </td>
			    <td>To Country </td>
			    <td>To Location</td>
			  </tr>

<%				for(int i=0;i<dataList.size();i++)
				{
					ReportDetailsDOB detailsDob=(ReportDetailsDOB)dataList.get(i);
%>

			  <tr valign="top" class='formdata'>
			    
            <td width="146"><input type="checkbox" name="checkbox" value="checkbox">
				</td>
				
            <td width="232">
			<%=detailsDob.getImportant()%>
			  </td>

				
            <td width="237"> 
			 <%=detailsDob.getQuoteId()%>
				</td>
            <td width="146" ><%=detailsDob.getShipmentMode()%></td>
			  <td width="146" ><%=detailsDob.getServiceLevel()%></td>
			  <td width="146" ><%=detailsDob.getFromCountry()%></td>
			  <td width="146" ><%=detailsDob.getFromLocation()%></td>
			  <td width="146" ><%=detailsDob.getToCountry()%></td>
			  <td width="146" ><%=detailsDob.getToLocation()%></td>
			  </tr>
<%
				}
%>
			  <tr valign="top" class='denotes'>
			    <td colspan="5"><font color="#ff0000">*</font> Denotes Mandatory </td>
			    <td colspan="4" align="right"><input type='button' class='input'  name='b1' value='Continue'></td>
			    </tr>
			</table>

		  
	 
				
          </td>
	    </tr>
	  </table>
	  
	</form>
  </body>
</html>

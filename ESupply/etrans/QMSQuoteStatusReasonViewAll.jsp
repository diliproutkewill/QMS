<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program Name	: QMSQuoteStatusReasonMasterEnterId.jsp
Product Name	: QMS
Module Name		: Status Reason  master
Task		    : Adding/View/Modify/Delete SurChargesMaster
Date started	: 25-Jan-11
Date modified	:  
Author    		: Silpa Pallempati
Description		: The application "View/Modify/Delete"  Status Reason Masterinformation
Actor           :
Related Document: CR-231109-12-Reports-Pending Status Add Notes Filter-Status Reasons-V1.0.doc
--%>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSQuoteStatusReasonViewAll.jsp";
%>
<%@ page import= "com.qms.setup.java.QuoteStatusReasonDOB"%>
<%
	String operation	=	request.getParameter("Operation");
	String terminalId	=	loginbean.getTerminalId();
	 QuoteStatusReasonDOB	 statusReasonDOB		= null; 

%>

<html>
<head>
<title>StatusReason <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>

</head>
<body>
<%
ArrayList statusreasonVOList=(ArrayList)request.getAttribute("statusreasonVOList");%>


 <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td >
       <table width="100%" cellpadding="4" cellspacing="0">
			  <tr valign="top" class='formlabel'>
			    	<td >STATUS REASON-- VIEW ALL 
			    	</td>
			  </tr>
		</table>

         <table width="100%"  cellpadding="4" cellspacing="1"> 
		 <tr valign="top" class='formdata'>
		   <tr ALIGN="CENTER" class='formheader'> 
			<td>STATUS REASON</td>
			</tr>
					<%for(int r=0;r<statusreasonVOList.size();r++)
				 {%>
		<tr  align="center" class="formdata" bgcolor="#FFFFFF" valign="top">
		<td>
							<%=statusreasonVOList.get(r)%>
		</td>			
		
		</tr>
		<%}%>
				</tr>
				</table>

	</td>
 	</tr>
	</table>	
</form>
</body>
</html>
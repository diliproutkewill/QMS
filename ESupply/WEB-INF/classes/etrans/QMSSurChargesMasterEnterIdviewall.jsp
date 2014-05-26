<%--
<%--

Copyright (c) 1999-2011 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSSurChargesMasterEnterIdviewall.jsp.jsp
Product Name	: QMS
Module Name		: Charge Basis Master
Task		    : View All Surcharges
Date started	: 22-Apr-11 	
Author    		: Silpa Pallempati
Description		: The application "ViewAll" SurCharges

--%>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSSurChargesMasterEnterIdviewall.jsp";
%>
<%
	String operation	=	request.getParameter("Operation");
	String terminalId	=	loginbean.getTerminalId();

%>
<html>
<head>
<title>StatusReason <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>
<!--added by silpa.p on 15-06-11-->
  <script language="javascript">
 function clckLinkForSort(id,sortType)
 {

var  sortClause	=	'';
		
var whereClause= "" ;


 whereClause="SELECT SURCHARGE_ID, substr(SURCHARGE_DESC,0,length(SURCHARGE_DESC)-3) SURCHARGE_DESC, SHIPMENT_MODE, RATE_BREAK, RATE_TYPE  FROM QMS_SURCHARGE_MASTER ";

sortClause =  " order by "+id+" "+ sortType;

 document.theform.search.value = whereClause +  sortClause; 


document.theform.action ="QMSSurchargesController?Operation=View All";
document.theform.submit();

		
	 }
</script>
<!--ended-->
</head>
<body>
 <form name="theform" method="POST">
<%	ArrayList surChargesVOList = null;
	surChargesVOList = (ArrayList)request.getAttribute("surChargesVOList");
%>


 <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td >
		       <table width="100%" cellpadding="4" cellspacing="0">
					  <tr valign="top" class='formlabel'>
					    <td >SurCharge- VIEW ALL </td>
						
					  </tr>
				</table>
		
		
		
		         <table width="100%"  cellpadding="4" cellspacing="1"> 

					 <tr valign="top" class='formdata'>
							 <tr ALIGN="CENTER" class='formheader'> 
							<!--added by silpa.p on 15-06-11-->
									<td nowrap> <a href="Javascript:clckLinkForSort('SURCHARGE_ID','ASC')">SurCharge Id </a></td>
									<td nowrap> <a href="Javascript:clckLinkForSort('SURCHARGE_DESC','ASC')">SurCharge Desc</a></td>
									<td nowrap> <a href="Javascript:clckLinkForSort('SHIPMENT_MODE','ASC')">Shipment Mode</a></td>
								<td nowrap> <a href="Javascript:clckLinkForSort('RATE_BREAK','ASC')">Rate Break</a></td>
									<td nowrap> <a href="Javascript:clckLinkForSort('RATE_TYPE','ASC')">Rate Type</a></td><!--ended-->
							  </tr>
						<%for(int r=0;r<surChargesVOList.size();r++)
						 {
						%>
							<tr  align="left" class="formdata" bgcolor="#FFFFFF" valign="top">
								
								<td> <%=surChargesVOList.get(r)%> </td> <%r++;%>
								<td> <%=surChargesVOList.get(r)%> </td> <%r++;%>
								<td> <%="1".equals(surChargesVOList.get(r))?"Air":"2".equals(surChargesVOList.get(r))?"Sea":"Truck"%> </td> <%r++;%>
								<td> <%=surChargesVOList.get(r)%> </td> <%r++;%>
								<td> <%=surChargesVOList.get(r)%> </td>			
							</tr>
						<%}
						%>
					</tr>
				</table>
			</td>
		</tr>
		
	</table>	
	<input type="hidden" name="search" value=""><!--added by silpa.p on 15-06-11-->
</form>


</body>
</html>
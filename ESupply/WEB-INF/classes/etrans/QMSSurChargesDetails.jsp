<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSSurChargesDetails.jsp
Product Name	: QMS
Module Name		: Charge Basis Master
Task		    : View/Modify/Delete Surcharges
Date started	: 28-10-2010 	
Author    		: K.C SUBRAHMANYAM
Description		: The application "View/Modify/Delete" SurCharges

--%>
<%@page import = "com.qms.setup.java.SurchargeDOB,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  javax.ejb.ObjectNotFoundException"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSSurChargesMasterEnterId.jsp";
%>
<%
try{
	String operation	=	request.getParameter("Operation");
	String terminalId	=	loginbean.getTerminalId();
	String surchargeId	=	"";
	String surchargeDesc	=	"";
	String shipmentMode	=	"";
	String rateBreak			=	"";
	String rateType				=	"";
	int Mode						= 0;
    String readOnly			=	"";
	SurchargeDOB  surchargeDOB	=	new SurchargeDOB();
	surchargeDOB	=	(SurchargeDOB)session.getAttribute("surChargeDOB");

	if(surchargeDOB!=null){
	shipmentMode	=surchargeDOB.getShipmentMode()==1?"Air":surchargeDOB.getShipmentMode()==2?"Sea":"Truck";
	rateBreak			=	surchargeDOB.getRateBreak();
	rateType			=	surchargeDOB.getRateType();
	surchargeDesc	=	surchargeDOB.getSurchargeDesc().substring(0,surchargeDOB.getSurchargeDesc().length()-3);
    surchargeId		=	surchargeDOB.getSurchargeid();
	System.out.println("operation..."+operation);
	if("View".equalsIgnoreCase(operation) || "Delete".equalsIgnoreCase(operation))
		readOnly	=	"readOnly";
	}
%>
<html>
<head>
<title>Sur Charges <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>
<script >

	var Win  = null;

 
	function chkMandatory(){
		if(document.forms[0].surchargeDesc.value=='')
		{
				alert("Please Enter Any SurCharge Desc To Modify.")
					document.forms[0].surchargeDesc.focus();
					return false;
		}
		else{
		document.forms[0].surcharge_Desc.value	=	document.forms[0].surchargeDesc.value+'<%=surchargeDOB.getSurchargeDesc().substring(surchargeDOB.getSurchargeDesc().length()-3)%>';

			return true;
		}
}
 function toUpper(obj){
	obj.value = obj.value.toUpperCase();
 }
</script>
</head>
<body>
<form name="surchargesmaster" method="post" action="<%=request.getContextPath()%>/QMSSurchargesController" onSubmit="return chkMandatory();">
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> SurCharge Details - <%=operation%> </td>
						<td align=right>QS1050521
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="100%" cellpadding="0" cellspacing="0">
			<tr class='formdata' width='50%'>
				<td align ='left' width='20%' class='formheader'>
				<b>Shipment Mode:</b><br>
			</td>
				<td align ='left' width='20%' class='formheader'>
				<b>SurCharge Id:</b>
				</td>
				<td align ='left' width='20%' class='formheader'>
				<b>SurCharge Desc:<font color="#FF0000">*</font></b>
				</td>	
				<td align ='left' width='20%' class='formheader'>
				<b>Rate Break:</b>
				</td>	
				<td align ='left' width='20%' class='formheader'>
				<b>Rate Type:</b>
				</td>	
				</tr>
				<tr>
					<td align ='center' width='20%' class='formdata'>
					<b><%=shipmentMode%></b>
					</td>
					<td align ='center' width='20%' class='formdata'>
					<b><%=surchargeId%></b>
					</td>
					<td align ='center' width='20%' class='formdata'>
					<input type='text' name='surchargeDesc' value='<%=surchargeDesc%>' size='30' maxlength='100'   <%=readOnly%>>  
					</td>
					<td align ='center' width='20%' class='formdata'>
					<b><%=rateBreak%></b>
					</td>
					<td align ='center' width='20%' class='formdata'>
					<b><%=rateType%></b>
					</td>					
				</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory<br>
            </td>
            <td valign="top" align="right">
				<input type="reset" value="Reset" name="reset" class="input">
				<%if("Modify".equalsIgnoreCase(operation)){%>
                <input type="submit" value="Modify" name="submit" class="input" >
				<%}
			else if("Delete".equalsIgnoreCase(operation)){%>
                <input type="submit" value="Delete" name="submit" class="input" >
				<%}else{%>
                <input type="submit" value="Continue" name="submit" class="input" >
				<%}%>
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='Details'>
				<input type='hidden' name='shipmentMode' value='<%=surchargeDOB.getShipmentMode()%>'>
				<input type='hidden' name='surcharge_Desc' value=''>
				<input type='hidden' name='surchargeid' value='<%=surchargeId%>'/>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
<%}catch(Exception e){}%>
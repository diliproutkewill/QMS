<%@ page import = " org.apache.log4j.Logger" %>

<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%! 
  private static Logger logger = null;
  private static final String FILE_NAME = "ETMultiModeRoutePlanEnterId.jsp"; %>

<%--
	% 
	% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	% This software is the proprietary information of FourSoft, Pvt Ltd.
	% Use is subject to license terms.
	%
	% esupply - v 1.x 
	%

	Program Name	:	ETMultiModeRoutePlanEnterId.jsp
	Module Name		:	ETrans
	Task			:	Route Plan
	SubTask			:	Accepts the House Document No to display the details of the given id
	Date started	:	16/05/2002
	Date Completed	:	
	Date modified	:	
	Author Name		:	Srinivasa Rao Koppurauri
	Description		: 	The following methods helps to to display the details of the given id
						with client-side validations
--%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	try
	{
		String operation	= request.getParameter("Operation");
		String shipmentMode	= request.getParameter("ShipmentMode");
		System.out.println("operationoperation : "+operation+" shipmentModeshipmentMode : "+shipmentMode);
		String terminalId	= loginbean.getTerminalId();
		String errorMessage	= null;
		
		if(request.getAttribute("ERROR")  != null)
		{
			errorMessage = (String)request.getAttribute("ERROR");
			request.removeAttribute("ERROR");
		}
		
%>
<html>
<head>
<title>Route Plan</title>
<link rel='stylesheet' href='ESFoursoft_css.jsp'>
<%@ include file="/ESEventHandler.jsp" %>
<script language='JavaScript'>
function validateForm()
{
	if(document.forms[0].qouteId.value == 0)
	{
		alert('Please Enter Document No.');
		document.forms[0].qouteId.focus();
		return false;
	}
	document.forms[0].B2.disabled='true';
}
function changeValue()
{
	document.forms[0].qouteId.value = '';
}
function showLOV()
{
	var documentType = document.forms[0].documentType.options[document.forms[0].documentType.options.selectedIndex].value;
	var searchStr	 = document.forms[0].qouteId.value;
	var	URL		 = 'etrans/ETLOVMultiModeRoutePlanQuoteIds.jsp?documentType='+searchStr;
	var	Bars	 = 'directories=no,	location=no, menubar=no, status=no,	titlebar=no';
	var	Options	 = 'scrollbars=yes,width=450,height=400,resizable=no';
	var	Features = Bars+' '+Options;
	var	Win		 = open(URL,'Doc',Features);
	if(Win.opener != null)
		Win.opener = self
	if(Win != null)
		Win.focus();
}
function upper(field)
{
	field.value	= field.value.toUpperCase();
}
/*function getNumberCode(input)    // Numbers 
{
	if(event.keyCode!=13)
	{	
		if((event.keyCode < 48 || event.keyCode > 57))
		return false;	
		else
		return true;	
	}
}*/
</script>
</head>
<body bgcolor="#FFFFFF">
	<form method = 'POST' action = 'ETMultiModeRoutePlanController' onSubmit='return validateForm()'>
	<input type=hidden name='Operation' value='<%=operation%>'>
	<input type=hidden name='SubOperation' value='EnterId'>
	<input type=hidden name='terminalId' value='<%=terminalId%>'>
	<input type=hidden name='ShipmentMode' value='<%=shipmentMode%>'>
		<table width="800" border="0" cellspacing="0" cellpadding="0">
			<tr valign="top" bgcolor="#FFFFFF">
				<td> 
					<table border='0' cellspacing='1' cellpading='4' width="800">
						<tr class='formlabel'> 
							<td colspan="2" ><table width="800" border="0" ><tr class='formlabel'><td>Route Plan - Add</td><td align=right>QS10621</td></tr></table></td>
						</tr>
<%
		if(errorMessage != null)
		{
%>
						<tr class='formdata'> 
							<td width=800 colspan=2><b><font color=red><%=errorMessage%></font></b></td>
						</tr>
<%
		}
%>
							<tr class='formdata'> 
							<td width=800 colspan=2>&nbsp;</td>
						</tr>
						<tr class='formdata'> 
							<td width=40%>Document Type:<br>
								<select name='documentType' size=1 class='select' onChange='changeValue()'>
									<option value='Quote'>Quote</option>
								</select>
							</td>
							<td width=60%>Document No:<font color='red'>*</font><br>
								<input type="text" class="text" name="qouteId" size="20" maxlength="25" onBlur='upper(this)' >
								<input type="button" value="..." name="B1" class='input' onClick='showLOV()'>
							</td>
						</tr>
						<tr> 
							<td><font face="Verdana" size="2"><font color="#FF0000" size="1">*</font><font size="1">Denotes Mandatory</font></font></td>
							<td colspan='3' align="right"> 
								<input type="submit" value="Next >>" name="B2" class='input'>
								<input type="reset" value="Reset" name="gg" class='input'>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
<%
	}
	catch(Exception ex)
	{
		//Logger.error(FILE_NAME, " -> "+ex.toString());
    logger.error(FILE_NAME+ " -> "+ex.toString());
	}
%>

<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSIndustryRegistrationEnterId.jsp
Product Name	: QMS
Module Name		: Industry Registration
Task		    : Adding/View/Modify/Delete/Invalidate Industry
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete/Invalidate" Industry information
Actor           :
Related Document: CR_DHLQMS_1002
--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSChargesGroupingMasterEnterId.jsp";
%>
<%
	String operation	=	request.getParameter("Operation");	
%>
<html>
<head>
<title>CarrierRegistration Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="../html/eSupply.js"></script>
<script >

	var Win  = null;
	function mandatory()
	{
		if(chargegroup.chargeGroupId.value=='')
		{
			alert("Please Select the Charge Group Id");
			chargegroup.b1.focus();
			return false;
		}
		if(chargegroup.submit!=null)
		{
			chargegroup.submit.disabled	=true;
		}
		if(chargegroup.resetB!=null)
		{
			chargegroup.resetB.disabled	=true;
		}
		return true;
	}
	function openChargeGroupIdLOV()
	{
		var searchStr	=	 document.forms[0].chargeGroupId.value;

		var Url			= "QMSLOVChargeGroupIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name=chargeGroupId&fromWhere=chargeGroupEnterId&terminalId="+document.forms[0].terminalId.value;
		showLOV(Url);
	}
	function showterminalIdLOV()
	{
		var name		=	"terminalId";
		var searchStr	=	document.getElementById(name).value;
		//var fromWhere	=	"sellchargesenterid";
		var fromWhere	=	"terminal";

			var Url			= "../QMSLOVAllLevelTerminalIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere;
			var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
			var Options='scrollbar=yes,width=400,height=360,resizable=no';
			var Features=Bars+' '+Options;
			var Win=open(Url,'Doc',Features);
	}
</script>
</head>
<body>
<form name="chargegroup" method="post" action="QMSChargesGroupingMaster.jsp" onSubmit ='return mandatory()'>
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> Charge Grouping - <%=operation%> </td>
						<td align=right>QS1050321
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="800" cellpadding="0" cellspacing="0">
			<tr class='formdata'>
				<td width="20%"><b>Terminal Id:</b>
				<input type	='text' name ="terminalId" id='terminalId' size='10' maxLength='10' class ='text' value ='' onkeyPress='specialCharFilter(this,"terminalId")'  onBlur="toUpperCase()">
				<input type	='button' name='terminalIdB' class ='input' value ="..." Onclick ="showterminalIdLOV()">
				</td>
				<td align ='left'><b>Charge Group Id:<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="chargeGroupId" size='15' maxLength='40' class ='text' value ='' onkeyPress='specialCharFilter1(this.value,"chargeGroupId")'  onBlur="toUpperCase()">
				<input type	='button' name='b1' class ='input' value ="..." Onclick ="openChargeGroupIdLOV()">
				</td>
			</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
                <input type="submit" value="Submit" name="submit" class="input">
				<input type="reset" value="Reset" name="resetB" class="input">
				<input type='hidden' name='Operation' value='<%=operation%>'>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
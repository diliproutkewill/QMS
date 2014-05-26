<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSChargesMasterEnterId.jsp
Product Name	: QMS
Module Name		: Charges master
Task		    : Adding/View/Modify/Delete ChargesMaster
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" ChargesMaster information
Actor           :
Related Document: CR_DHLQMS_1005
--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSChargeBasisMasterEnterId.jsp";
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
		if(chargesenter.chargeBasisId.value=='')
		{
			alert("Please Select the Charge Id");
			chargesenter.b1.focus();
			return false;
		}
		if(chargesenter.submit!=null)
		{
			chargesenter.submit.disabled	=true;
		}
		if(chargesenter.resetB!=null)
		{
			chargesenter.resetB.disabled	=true;
		}
		return true;
	}

	function openChargeBasisIdLOV()
	{
		var searchStr	=	document.forms[0].chargeBasisId.value;
		var operation	=	'<%=operation%>'.toUpperCase();
		if(operation=='VIEW')
			operation = 'BUYCHARGEBASIS';
		else if(operation == 'MODIFY' || operation == 'DELETE')
			operation = 'CHARGEBASIS';
		/*var Url			= "QMSLOVChargeBasisIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+document.forms[0].chargeBasisId.name;
		showLOV(Url);*/
		var tabArray = '';
		var formArray = '';
		var lovWhere	=	"";
		formArray = 'chargeBasisId';		
		tabArray = 'CHARGEBASIS';
		Url		="<%=request.getContextPath()%>/qms/ListOfValues.jsp?lovid=CHARGEBASIS_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation="+operation+"&search=where CHARGEBASIS LIKE '"+searchStr+"~'";
		Options	='width=750,height=750,resizable=yes';
		
    
    Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
		Features=Bars+','+Options;

		Win=open(Url,'Doc',Features);
	}

</script>
</head>
<body>
<form name="chargesenter" method="post" action="QMSChargeBasisMaster.jsp" onSubmit ='return mandatory()'>
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> ChargeBasis EnterId - <%=operation%> </td>
						<td align=right>QS1050421
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="800" cellpadding="0" cellspacing="0">
			<tr class='formdata'>
				<td align ='left'><b>Charge Basis Id:<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="chargeBasisId" size='10' maxLength='10' class ='text' value ='' onkeyPress='specialCharFilter(this,"chargeBasisId")'  onBlur="toUpperCase()">
				<input type	='button' name='b1' class ='input' value ="..." Onclick ="openChargeBasisIdLOV()">
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
				<input type="reset" value="Reset" name="reset" class="input">
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
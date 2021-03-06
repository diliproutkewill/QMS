<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSBuyChargesMasterEnterId.jsp
Product Name	: QMS
Module Name		: Charge Basis Master
Task		    : Adding/View/Modify/Delete chargebasis
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" chargebasis information
Actor           :
Related Document: CR_DHLQMS_1005
--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSBuyChargesMasterEnterId.jsp";
%>
<%
	String operation	=	request.getParameter("Operation");
	String terminalId	=	loginbean.getTerminalId();
%>
<html>
<head>
<title>BuyCharges <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>
<script >

	var Win  = null;
	function mandatory()
	{
		if(buychargesenter.chargeId.value=='')
		{
			alert("Please Select the Charge Id");
			buychargesenter.chargeIdB.focus();
			return false;
		}
		if(buychargesenter.chargeDescriptionId.value=='')
		{
      //@@Modified by kameswari for WPBN-38114
			//alert("Please Select the chargeBasis Id");
      alert("Please Select the Charge Description Id");
      //@@
			buychargesenter.chargeDescriptionIdB.focus();
			return false;
		}
		if(buychargesenter.submit!=null)
		{
			buychargesenter.submit.disabled	=true;
		}
		if(buychargesenter.resetB!=null)
		{
			buychargesenter.resetB.disabled	=true;
		}
		return true;
	}
	function openChargeIdLOV()
	{
		var searchStr		=	document.forms[0].chargeId.value;
		var chargeDesc		=	document.getElementById("chargeDescriptionId").value;
		var terminalId		=	document.forms[0].terminalId.value;
		var GroupId	=	 document.forms[0].chargeGroupId.value; //added by VLAKSHMI for CR #170761 on 20090626
    ////Chnged by VLAKSHMI for CR #170761 on 20090626
		var Url				=	  'etrans/QMSLOVChargeIds.jsp?searchString='+searchStr+'&shipmentMode=&index=&Operation=<%=operation%>&terminalId='+terminalId+'&name=chargeId&fromWhere=buychargesenterid&selection=single&chargeDescriptionId='+chargeDesc+'&chargeGroupId='+GroupId;
		showLOV(Url);
	}
  ////added by VLAKSHMI for CR #170761 on 20090626
	function openChargeGroupIdLOV()
	{
		//alert(); 
		var searchStr	=	 document.forms[0].chargeGroupId.value;
		var chargeId	=	document.getElementById("chargeId").value;
		var chargeIdDesc	=	document.getElementById("chargeDescriptionId").value;
       //alert(searchStr)
		var Url			= "etrans/QMSLOVChargeGroupIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name=chargeGroupId&fromWhere=chargeGroupEnterId&terminalId="+document.forms[0].terminalId.value+"&chargeId="+chargeId+"&chargeIdDesc="+chargeIdDesc;
		showLOV(Url);
	}
/*	function openChargeBasisIdLOV()
	{
		var searchStr	=	document.forms[0].chargeBasisId.value;
		var chargeId	=	document.forms[0].chargeId.value;
		var Url			= "etrans/QMSLOVChargeBasisIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&chargeId="+chargeId+"&name=chargeBasisId&teminalId=<%=terminalId%>&fromWhere=buychargesenterid&selection=single";
		showLOV(Url);
	}*/
function openChargeDescIdsLOV()
{

	var name		=	"chargeDescriptionId";
	var searchStr	=	document.getElementById(name).value;
	var chargeId	=	document.getElementById("chargeId").value;
	var chargeGroupId	=	document.getElementById("chargeGroupId").value;//@@Added by subrahmanyam for pbn id: 195270 on 20-Jan-10
	var terminalId		=	document.forms[0].terminalId.value;
	var fromWhere	=	"buychargesenterid";

		if(chargeId=='')
		{ alert("Please Select chargeId");return false;}
		//@@Modify by subrahmanyam for pbn id: 195270 on 20-Jan-10
		var Url			= "etrans/QMSLOVDescriptionIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere+"&terminalId="+terminalId+"&chargeId="+chargeId+"&chargeGroupId="+chargeGroupId;
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
}

function showterminalIdLOV()
{
	var name		=	"terminalId";
	var searchStr	=	document.getElementById(name).value;
 	//var fromWhere	=	"sellchargesenterid";
	var fromWhere	=	"terminal";
		var Url			= "QMSLOVAllLevelTerminalIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere;
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
}
</script>
</head>
<body>
<form name="buychargesenter" method="post" action="QMSBuyChargesController" onSubmit ='return mandatory()'>
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> BuyCharge EnterId - <%=operation%> </td>
						<td align=right>QS1050521
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
        <!-- Added by VLAKSHMI for CR #170761 on 20090626 -->
				<td align ='left' width='30%'><b>Charge Group Id:</b><br>
				<input type	='text' name ="chargeGroupId" size='15' maxLength='40' class ='text' value ='' onkeyPress='specialCharFilter1(this.value,"chargeGroupId")'  onBlur="toUpperCase()">
				<input type	='button' name='b1' class ='input' value ="..." Onclick ="openChargeGroupIdLOV()">
				</td>
				<td align ='left' width='20%'><b>Charge Id:<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="chargeId" id='chargeId' size='10' maxLength='10' class ='text' value ='' onkeyPress='specialCharFilter(this,"chargeId")'  onBlur="toUpperCase()">
				<input type	='button' name='chargeIdB' class ='input' value ="..." Onclick ="openChargeIdLOV()">
				</td>
<!-- 				<td align ='left' width='20%'><b>ChargeBasis Id:<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="chargeBasisId" size='10' maxLength='10' class ='text' value ='' onkeyPress='specialCharFilter(this,"chargeBasisId")'  onBlur="toUpperCase()">
				<input type	='button' name='chargeBasisIdB' class ='input' value ="..." Onclick ="openChargeBasisIdLOV()">
				</td> -->
				<!--@@Commented and  Modified by Subrahmanyam for the WPBN issue-144405 on 12/11/08-->
				<td align ='left' width='30%'><b>ChargeDescription Id:<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="chargeDescriptionId" id='chargeDescriptionId' size='10' maxLength='100' class ='text' value ='' onkeyPress='specialCharFilter(this,"chargeDescriptionId")'  onBlur="toUpperCase()">
				<input type	='button' name='chargeDescriptionIdB' class ='input' value ="..." Onclick ="openChargeDescIdsLOV()">
				</td>
				<!--@@WPBN issue-144405-->
				<td width='30%'></td>
			</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory<br>
			  <font color="#FF0000">**</font>Default terminalId is loginTerminalId
            </td>
            <td valign="top" align="right">
                <input type="submit" value="Submit" name="submit" class="input">
				<input type="reset" value="Reset" name="reset" class="input">
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='enter'>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
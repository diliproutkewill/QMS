<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSurChargesMasterEnterId.jsp
Product Name	: QMS
Module Name		: Charge Basis Master
Task		    : View/Modify/Delete Surcharges
Date started	: 28-10-2010 	
Author    		: K.C SUBRAHMANYAM
Description		: The application "View/Modify/Delete" SurCharges

--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSSurChargesMasterEnterId.jsp";
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
function openSurchargeLov()
{
	var tabArray = 'SURCHARGE_ID';
	var formArray = 'surchargeId';
	var smode	 =	'';
	var lovWhere	=	"";
        Url		="<%=request.getContextPath()%>"+"/qms/ListOfValues.jsp?lovid=SURCHARGE_LOV&tabArray="+tabArray+"&shipmentMode="+document.forms[0].shipmentmode.value+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SURCHARGE&search=where shipment_mode="+document.forms[0].shipmentmode.value+" and surcharge_id like '"+document.getElementById('surchargeId').value+"~'  order by surcharge_id" ; <!-- // Added by Kishore Podili for issue id:  on 20-Dec-10 -->
	Bars	='directories=0,location=0,menubar=no,status=no,titlebar=0,scrollbars=1';
	Options	='width=700,height=600,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
 }
 
	function chkMandatory(){
		if(document.forms[0].surchargeId.value=='')
		{
				alert("Please Enter Any SurCharge Id To "+'<%=operation%>')
					document.forms[0].surchargeId.focus();
					return false;
		}
		else{
		document.forms[0].surcharge_Id.value	=	document.forms[0].surchargeId.value;
		document.forms[0].shipmentMode.value	 =	document.forms[0].shipmentmode.value;
			return true;
		}
}

// Added by Kishore Podili for issue id:26488  on 20-Dec-10
	function toUpper(obj){
		obj.value = obj.value.toUpperCase();
	}
// End Of Kishore Podili for issue id: 226488 on 20-Dec-10


// Added by Kishore Podili for issue id:26488  on 03-Jan-11
function getDotNumberCode(input)    // Numbers + Dot
{
  if(event.keyCode!=13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
 // Added by Kishore Podili for issue id:26488  on 03-Jan-11



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
						<td> SurCharge - <%=operation%> </td>
						<td align=right>QS1050521
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="100%" cellpadding="0" cellspacing="0">
			<tr class='formdata' width='50%'>
				<td align ='left' width='20%' class='formdata'>
				<b>Shipment Mode:<font color="#FF0000">*</font></b><br>
     			<select name='shipmentmode'  class="select">
				<option value='1'>Air</option>
				<option value='2'>Sea</option>
				<option value='4'>Truck</option>
				</select>
			</td>
							<td align ='left' width='20%' class='formdata'>
				<b>SurCharge Id:<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="surchargeId" id='surchargeId' size='10' maxLength='5' class ='text' value =''  onBlur="toUpper(this)"    onkeyPress= "getDotNumberCode(this)"> <!-- Modified by  Kishore Podili for issue id: 226488 on 20-Dec-10 onkeyPress='specialCharFilter(this,"surchargeId")'  -->
				<input type	='button' name='chargeIdB' class ='input' value ="..." Onclick ="openSurchargeLov()">
				</td>
				<td width='60%' class='formdata'>
			</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory<br>
            </td>
            <td valign="top" align="right">
				<input type="reset" value="Reset" name="reset" class="input">
                <input type="submit" value="Next>>" name="submit" class="input" >
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='Enter'>
				<input type='hidden' name='shipmentMode' value=''>
				<input type='hidden' name='surcharge_Id' value=''>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
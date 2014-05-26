<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program Name	: QMSSurchargesMaster.jsp
Product Name	: QMS
Module Name		: SurCharges master
Task		    : Adding/View/Modify/Delete SurChargesMaster
Date started	: 26-10-2010	
Date modified	:  
Author    		: K. C SUBRAHMANYAM
Description		: The application "Adding/View/Modify/Delete"  Sur ChargesMaster information
Actor           :
Related Document: CR_DHLQMS_4.0_Phase I_219973
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
  private static Logger logger = null;
	private static final String FILE_NAME	=	" QMSSurchargesMaster.jsp";
%>
<%
String onLoad ="";
String operation	=	request.getParameter("Operation");
String surchargeId	=	"";
String surchargeDesc	=	"";
String shipmentMode	=	"";
String rateBreak			=	"";
String rateType				=	"";
int Mode						= 0;
try{

		SurchargeDOB  surchargeDOB	=	new SurchargeDOB();
	surchargeDOB	=	(SurchargeDOB)session.getAttribute("surChargeDOB");

	if(surchargeDOB!=null){
	shipmentMode	=surchargeDOB.getShipmentMode()==1?"Air":surchargeDOB.getShipmentMode()==2?"Sea":"Truck";
	rateBreak			=	surchargeDOB.getRateBreak();
	rateType			=	surchargeDOB.getRateType();
	surchargeDesc	=	surchargeDOB.getSurchargeDesc();
    surchargeId		=	surchargeDOB.getSurchargeid();
	}

%>

<html>
<head>
<title>Surcharge -<%=operation%></title>    <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="./ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="./html/dynamicContent.js"></script>
<script src ='./html/eSupply.js'></script>
<script>
function setratetype(obj){
		if(document.forms[0].shipmentmode.value==''){
				alert("Please Enter Shipment Mode");
				document.forms[0].shipmentmode.focus();
				document.forms[0].ratebreak.value='';
				return false;
		}
	if(obj.value=='Flat' ||obj.value=='Flat%' )
				  document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value='Flat'>Flat</option></select>";
	else 	if(obj.value=='Slab%' )
				  document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value='Slab'>Slab</option></select>";
	else 	if(obj.value=='Slab' )
				  document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value='Flat' >Flat</option><option value='Slab' selected>Slab</option><option value='Both' >Both</option></select>";
	else if(obj.value=='' )
				  document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value='' ></option><option value='Flat' >Flat</option><option value='Slab' >Slab</option><option value='Both' >Both</option></select>";

else 	if(document.forms[0].shipmentmode.value=='Air' && obj.value=='List')
				 document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value='Pivot'>Pivot</option></select>";
// Added By Kishore Podili for Percent in Sea List 
else 	if(document.forms[0].shipmentmode.value=='Sea' && obj.value=='List')
			document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value='Flat'>Flat</option> <option value='Percent'>Percentage</option> </select> ";
	else 
	document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value='Flat'>Flat</option></select>";





}
function chkrtbrk(){
		if(document.forms[0].ratebreak.value==''){
				alert("Please Enter RateBreak.");
				document.forms[0].ratebreak.focus();
				document.forms[0].rateType.value='';
				return false;
		}
}
function initialize(){
	
		  document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value=''></option><option value='Flat'>Flat</option><option value='Slab'>Slab</option><option value='Both'>Both</option><option value='Pivot'>Pivot</option></select>";
		  document.getElementById("ratebreak").innerHTML="<select name='ratebreak' onchange='setratetype(this);' class='select' size=1><option value=''></option><option value='Flat'>Flat</option><option value='Flat%'>Flat%</option> <option value='Slab'>Slab</option><option value='Slab%'>Slab%</option>			  <option value='Absolute'>Absolute</option> <option value='Percent'>Percent</option> <option value='List'>List</option> </select>";

	
}
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
 function toUpper(obj){
	obj.value = obj.value.toUpperCase();
 }
function  validateForm(){
	if(document.forms[0].surchargeid.value==''){
		alert("Please Enter SurCharge Id.")
			document.forms[0].surchargeid.focus();
			return false;
	}
	if(document.forms[0].surchargedesc.value==''){
		alert("Please Enter SurCharge Description.")
			document.forms[0].surchargedesc.focus();
			return false;
	}
		if(document.forms[0].shipmentmode.value==''){
		alert("Please Enter Shipment Mode.")
			document.forms[0].shipmentmode.focus();
			return false;
	}
	if(document.forms[0].ratebreak.value==''){
		alert("Please Enter Rate Break.")
			document.forms[0].ratebreak.focus();
			return false;
	}
	if(!chkSize()){
		return false;
	}
	setValues();
	return true;

}
function setValues()
{

		document.forms[0].scid.value=document.forms[0].surchargeid.value+document.forms[0].ratebreak.value.substring(0,1)+document.forms[0].rateType.value.substring(0,1);
		document.forms[0].scdesc.value=document.forms[0].surchargedesc.value+"-"+document.forms[0].ratebreak.value.substring(0,1)+document.forms[0].rateType.value.substring(0,1);
		document.forms[0].smode.value=document.forms[0].shipmentmode.value;
		document.forms[0].rbreak.value=document.forms[0].ratebreak.value;
		document.forms[0].rtype.value=document.forms[0].rateType.value;
		if((document.forms[0].ratebreak.value == 'Flat' || document.forms[0].ratebreak.value == 'Flat%'  )&& document.forms[0].rateType.value =='Flat'){
			if(document.forms[0].smode.value == 'Air' || document.forms[0].smode.value == 'Truck' )
				document.forms[0].wtbreaks.value='BASIC,MIN,FLAT';
			if(document.forms[0].smode.value == 'Sea' )
				document.forms[0].wtbreaks.value='BASIC,MIN,FLAT';
		}
		else	if(document.forms[0].ratebreak.value == 'Slab'  && document.forms[0].rateType.value == 'Slab'  ){
				document.forms[0].wtbreaks.value='SLAB';
		}
		else	if(document.forms[0].ratebreak.value == 'Slab'  && document.forms[0].rateType.value == 'Flat'  ){
				document.forms[0].wtbreaks.value='SLAB';
		        }
				else	if(document.forms[0].ratebreak.value == 'Slab'  && document.forms[0].rateType.value == 'Both'  ){
				document.forms[0].wtbreaks.value='Both';
				}
		else	if(document.forms[0].ratebreak.value == 'Absolute' && document.forms[0].rateType.value == 'Flat'){
				document.forms[0].wtbreaks.value='Absolute';
		}
		else	if(document.forms[0].ratebreak.value == 'List' ){
				document.forms[0].wtbreaks.value='LIST';
		}
		else if(document.forms[0].ratebreak.value == 'Percent' || document.forms[0].rateType.value == 'Flat'){
		       document.forms[0].wtbreaks.value='Min,Percent';
		}
		return true;

}
function resetAll(){
		  document.getElementById("rateType").innerHTML="<select name='rateType' onchange='chkrtbrk();' class='select' size=1><option value=''></option><option value='Flat'>Flat</option><option value='Slab'>Slab</option><option value='Both'>Both</option></select>";
		  document.getElementById("ratebreak").innerHTML="<select name='ratebreak' onchange='setratetype(this);' class='select' size=1><option value=''></option><option value='Flat'>Flat</option><option value='Flat%'>Flat%</option> <option value='Slab'>Slab</option><option value='Slab%'>Slab%</option>			  <option value='Absolute'>Absolute</option> <option value='Percent'>Percent</option> <option value='List'>List</option> </select>";
}
function chkSize(){
if(document.forms[0].surchargeid.value.length<3){
	alert('Sur Charge Id Should be 3 Characters');
	document.forms[0].surchargeid.focus();
	return false;
}
return true;

}
</script>
</head>
<body bgcolor="#FFFFFF" onload='initialize();' >
<form method="post" name=webform id=form1 action="QMSSurchargesController"  onSubmit="return validateForm();">
<div id="mainDiv" style="visibility:visible;position:absolute;">
<table bgcolor="#FFFFFF" width="100%" border="0" cellspacing="0" cellpadding="0">
<tr >
	<td bgcolor="#FFFFFF">
		<table width="100%" border="0" cellspacing="1" cellpadding="4">
			<tr class='formlabel' > 
				<td ><table width="100%" border="0" ><tr class='formlabel'><td>SurCharge - <%=operation%></b></td><td				align=right>QS1050521</td></tr>
				</table>
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
      <table class='formdata' border="0" cellpadding="4" cellspacing="1"  width='100%'>
		  <tr valign='center' class='formheader' colspan='5'>
				<td width='15%'>SurCharge Id:<font color="#FF0000">*</font></td>
	            <td width='15%'>SurCharge Description:<font color="#FF0000">*</font></td>
		        <td width='10%'>Shipment Mode:<font color="#FF0000">*</font></td>
			    <td width='10%'>Rate Break:<font color="#FF0000">*</font></td>
				<td width='10%'>Rate Type:<font color="#FF0000">*</font></td>
				<td width='40%'/>
	          </tr>
		  <tr  valign=top class='formdata'>
			  <td  width='10%' ><input type='text'  name='surchargeid' onBlur='toUpper(this);' onkeypress='getDotNumberCode(this);'  value='' size='5' maxlength='3'></td>
			  <td width='30%'><input type='text' name='surchargedesc' onBlur='' value='' size='30' maxlength='100'></td>
  			  <td width='20%'>
				  <select name='shipmentmode' class='select'  onchange='resetAll();'>
					  <option value=''></option>
					  <option value='Air'>Air</option>
					  <option value='Sea' >Sea</option>
					  <option value='Truck'>Truck</option>
				  </select>
			  </td>
			 <td width='20%'>
			 <span id="ratebreak"></span>
		     </td>
				 <td width='20%'><span id="rateType"></span></td>
		  </tr>
		 </table>
		</tr>
		<tr>

				<table  class='formdata' border="0" width="100%"   cellspacing="1" cellpadding="4" >
	    <tr class='denotes'>
		  <td valign=top >
		    <font color="#FF0000">*</font>Denotes Mandatory
		  </td>
		  <td align="right" valign = top >
			<input name=Reset type=reset value=Reset class='input'/>
		    <input name=submit type='submit' value="Submit" class='input'/>
			<input type='hidden' name='scid' value=''>
			<input type='hidden' name='scdesc' value=''/>
			<input type='hidden' name='smode' value=''/>
			<input type='hidden' name='Operation' value='<%=operation%>'/>
			<input type='hidden' name='rbreak' value=''/>
			<input type='hidden' name='rtype' value=''/>
			<input type='hidden' name='wtbreaks' value=''/>
			<input type='hidden' name='subOperation' value='Enter'/>
	  </td>
		</tr>
	  </table>
		</tr>
  </table>  
</form>
</div>
</body>
</html>
<%}catch(Exception e){

				}
%>
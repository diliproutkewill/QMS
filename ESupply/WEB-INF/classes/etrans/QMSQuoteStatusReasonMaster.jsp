<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program Name	: QMSQuoteStatusReasonMaster.jsp
Product Name	: QMS
Module Name		: Status Reason  master
Task		    : Adding/View/Modify/Delete SurChargesMaster
Date started	: 25-Jan-11
Date modified	:  
Author    		: Venkata kishore Podili
Description		: The application "Adding/View/Modify/Delete"  Status Reason Masterinformation
Actor           :
Related Document: CR-231109-12-Reports-Pending Status Add Notes Filter-Status Reasons-V1.0.doc
--%>
<%@page import = " org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  javax.ejb.ObjectNotFoundException"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	" QMSQuoteStatusReasonMaster.jsp";
%>
<%
String onLoad ="";
String operation	=	request.getParameter("Operation");
%>

<html>
<head>
<title>Status Report -<%=operation%></title>    <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="./ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="./html/dynamicContent.js"></script>
<script src ='./html/eSupply.js'></script>
<script>

function getDotNumberCode(input)    // Numbers + Dot
{
	//alert(event.keyCode)
	if(event.keyCode!=13)
    {
     if ((event.keyCode > 32 && event.keyCode < 40) ||(event.keyCode > 41 && event.keyCode < 44) ||(event.keyCode > 47 && event.keyCode < 65)  ||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
	 
    }
  return true;
 }

 function toUpper(obj){
	obj.value = obj.value.toUpperCase();
 }	


function validateForm(){

	if(document.forms[0].statusReason.value==''){
		alert("Please Enter  Status Reason1")
			document.forms[0].surchargeid.focus();
			return false;
	}

	  return true;
	}



</script>
</head>

<body bgcolor="#FFFFFF" >
<form method="post" name=webform id=form1 action=""  onSubmit="return validateForm();">
<div id="mainDiv" style="visibility:visible;position:absolute;">
<table bgcolor="#FFFFFF" width="100%" border="0" cellspacing="0" cellpadding="0">
<tr >
	<td bgcolor="#FFFFFF">
		<table width="100%" border="0" cellspacing="1" cellpadding="4">
			<tr class='formlabel' > 
				<td ><table width="100%" border="0" ><tr class='formlabel'><td>Status Reason - <%=operation%></b></td><td				align=right>QS1050521</td></tr>
				</table>
				</td>
			</tr>
		</table>
	</td>
</tr>

		<%	if(request.getParameter("errMsg")!=null)
				{	%>
				<tr  class='formdata' >
				<td colspan='25' >
						<font  color=#ff0000> <%=(String)request.getParameter("errMsg")%></font>
				</td>
				</tr>
			<%
				}
			%>
<tr>
      <table class='formdata' border="0" cellpadding="4" cellspacing="1"  width='100%'>
		 
		  <tr  valign=top class='formdata'>
			  <td > Status Reason </td>
			  <td		><input type='text' name='statusReason' onBlur='toUpper(this); getDotNumberCode(this);'	 onkeyPress= "getDotNumberCode(this)" value='' size='30' maxlength='100'></td>
  			 
			 
		  </tr>
		 </table>
		</tr>
		<tr>

				<table  class='formdata' border="0" width="100%"   cellspacing="1" cellpadding="4" >
						  <tr class='denotes'>
								  <td align="right" valign = top >
									<input name=Reset type=reset value=Reset class='input'/>
									<input name=submit type='submit' value="Submit" class='input'/>
									
									<input type='hidden' name='Operation' value='<%=operation%>'/>
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

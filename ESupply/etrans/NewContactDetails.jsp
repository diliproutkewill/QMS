<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/*	Programme Name	:NewAddress.jsp
*	Module Name		:Quote
*	Task Name		:add more customer address
*	Author Name		:K.NareshKumarReddy
*	Date Started	:31 August 2005 
*	Date Completed	:31 August 2005 

*/
%>

<%@ page import =  "java.util.*,
					org.apache.log4j.Logger"
%> 
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<html>
<head>
<title>More Customer Address Add</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language="javascript" src="html/eSupplyDateValidation.js"></script>
<script language="javascript" src ="../html/dynamicContent.js"></script>
<script language="javascript" src="html/eSupply.js"></script>
<script>

	function initialize()
	{

		importXML('../xml/customerreg.xml');
	}
	function validateBeforeDeletion()
	{
		return true;
	}
	function validateBeforeCreation()
	{
		return true;
	}
	function initializeDynamicContentRows()
	{

		setValues();

	}
	function setValues()
	{
		var tableObj = document.getElementById("customerreg");
		if(false)
		{}
		else
		{
			if(tableObj.getAttribute("idcounter")==1)
						createDynamicContentRow(tableObj.getAttribute("id"));
		}

	}
	function defaultFunction(currentRow)
	{
	}
	function defaultDeleteFunction()
	{
	}
function checkSpecialKeyCode()
{
	if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34)
	{
		return false;
	}
	return true;
}
function upper(e){
	e.value	= e.value.toUpperCase();	
}
function checkEmail()//added by rk
{
	var str = document.forms[0].Email.value;
	var i=0,j=-1;
	var str1;
	var flag=false;
	while(1)
	{
		if(document.forms[0].Email.value=="")
		{
			alert(" Email Id cannot be empty");	
			document.forms[0].Email.focus();
			return false;
		}
		else
		{
			j=str.indexOf(";",j);
			if(j==-1)
			{
				break;
			}
			str1=str.substring(i,j);
			//alert(str1);
			if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
			{
				alert(str1 + " is an Invalid E-mail Address!");	
				document.forms[0].Email.focus();
				flag=true;
				break;
			}
			
			i=j+1;
			j=j+1;
			continue;
		}
	}
	str1=str.substring(i);
	//alert("last "+str1);
	if(str1!=''&& !flag)
	if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
	{
		alert(str1 + " is an Invalid E-mail Address!");
		document.forms[0].Email.focus();
		return false;
	}
return true;
	
}
function stringFilter(input){//added by rk
	s = input.value;
	input.value = s.toUpperCase();
}
function chrnum(input)
{
	s = input.value;
	filteredValues = "'~!@#$%^&*()_+=|\:;<>,./?";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if(filteredValues.indexOf(c) == -1)
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
			if( field.elements[i] == input )
				document.forms[0].elements[i].focus();
	}

	input.value = returnString;
	if(flag==1) return false
	if(flag==0) return true
}
function checkNumeric(input)
{
	input.value = input.value.toUpperCase();

	if(isNaN(input.value))
	{
		alert('Please Enter Numeric Values Only.');
		input.value='';
		input.focus();
		return false;
	}
}
function Mandatory()
{
	
	var contact = document.getElementsByName('contactPerson');
	
	for(var i=0;i<contact.length;i++)
	{
		if(contact[i].value.length==0)
		{
			alert('Please Enter the Contact Person Name at row '+(i+1));
			contact[i].focus();
			return false;
		}
		else upper(contact[i]);
	}
	var flag = checkEmail();
	if(!flag)
		return false;
}

</script>
<body onLoad ='initialize()' bgcolor='#ffffff'>
<form  method="post"  onSubmit="return Mandatory()" action="ETCustomerRegistrationProcess.jsp?Operation=addContactDetails">
<table border='0' width='100%' cellpadding='4' cellspacing='1' bgcolor='#FFFFFF'>
	 <tr valign='top' class='formlabel'>    	  
		 <td colspan='2'>Add Address -  Module</td>
	 </tr>
	<tr valign='top' class='formlabel'>
		<td colspan='2'>
			<table border='0' width='100%' id='customerreg' idcounter='1' 
			defaultElement='Type' xmlTagName='customerreg' functionName='defaultFunction'" +"deleteFunctionName='defaultDeleteFunction' onBeforeDelete='' onBeforeCreate='' maxRows=25 bgcolor='#FFFFFF'>
				<tr class='formheader'>
					<td>&nbsp;</td>
					<td>AddressType</td>
					<td>Contact Person</td>
					<td>Designation</td>
					<td>Dept</td>
					<td>ZipCode</td>
					<td>Contact</td>
					<td>Fax</td>
					<td>E-Mail</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
			<td  align="left" class="denotes" ><font color="red">*</font>Denotes Mandatory</font></td>
			<td  align="right"><input class="input" name="submit" type="submit" value="Save & Exit" ></td>
	</tr>	
</table>
<input type='hidden' name='custId' value='<%=request.getParameter("custId")%>'>

</body>	
</form>
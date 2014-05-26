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
<script>
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
function Mandatory()
{
	if(document.forms[0].addressLine1dm.value.length==0)
	{
		alert("The Address Field Cannot be Empty");
		document.forms[0].addressLine1dm.focus();
		return false;
	}
	if(document.forms[0].citydm.value.length==0)
	{
		alert("Please Enter the City");
		document.forms[0].citydm.focus();
		return false;
	}
	if(document.forms[0].countryId.value.length==0)
	{
		alert("Plese Enter the Country Id");
		document.forms[0].countryId.focus();
		return false;
	}
	//return true;

}
function showCountryIds1(row){


	
		var URL 	= 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
			var Bars 	= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options = 'scrollbars = yes,width = 360,height = 360,resizable = yes';
	var Features = Bars +' '+ Options;
	var Win 	 = window.open(URL,'Doc1',Features);
}

</script>
<form  method="post"  onSubmit="return Mandatory()" action="ETCustomerRegistrationProcess.jsp?Operation=addAddress">
<table border='0' width='800' cellpadding='4' cellspacing='1' bgcolor='#ffffff'>
	 <tr valign='top' class='formlabel'>    	  
					 <td colspan='3'>Add Address -  Module</td>
					 </tr>
<tr  class='formdata'> 
	<td colspan=4 align='left'> ADDRESS </td></tr>
		<tr  class='formdata'>
			 <td >Contact Person : <br> <input type='text' class='text' maxlength='30' size='20' name='contactPersondm'
			 onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' >
			 <input type='hidden' maxlength='20' size='20' name='addressIddm'></td>
			<td >Designation :<br><input type='text' class='text' maxlength='30' size='20' name='designationdm' onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)'>
			</td>
			<td>AddressType :<font color='#FF0000'>*</font>
			<br><select size='1' name='add_Flagdm'  class='select'>
			<option value='P' selected>PickUp Address</option>
			<option value='B' >Billing Address</option>
			<option value='D' >Delivery Address</option></select><br>

				Delete :<select size='1' name='del_Flagdm'  class='select'>
			<option value='Y' >YES</option>
			<option value='N' selected >NO</option>
			</select>
			</td>
		<tr  class='formdata'>
			<td >Address :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=75 size='20' name='addressLine1dm' onBlur = 'upper(this)'><br><input type='text' class='text' maxlength=75 size='20' name='addressLine2dm' onBlur = 'upper(this)'>
			</td>		 						
			<td >City :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=30 size='20' name='citydm' onBlur = 'upper(this)'>
			</td>
			<td >ZipCode :<input type='text' class='text' maxlength=10 size='5' name='zipOrPostalCodedm' onBlur = 'upper(this)'><br>
			CountryId :<font color='#FF0000'>*</font> <input type='text' class='text' maxlength=2 size='5' name='countryId' onBlur = 'upper(this)'>&nbsp;
			<input type='button' class='input' value='...' name='lov_CountryId'  onClick='showCountryIds1("+i+")' >
			</td>
			
			
		</tr>
		<tr  class='formdata'>
			<td >State or Province :<br> <input type='text' class='text' maxlength=30 size='20' name='statedm' value=""  onBlur = 'upper(this)' ></td>							
			<td >Contact No :<br> <input type='text' class='text' maxlength=20 size='20' name='contactNodm' value=""  onBlur = 'upper(this)'></td>
			<td >Email :<br> <input type='text' class='text' maxlength=50 size='20' name='emailIddm'></td>
			<input type='hidden' name='custId' value='<%=request.getParameter("custId")%>'>
		</tr>
			<tr class="text" >
			<td  align="left" colspan=2 ><font color="#ff0000">*</font>Denotes Mandatory</font></td>
			<td  align="center">
				
				<input class="input" name="submit" type="submit" value="Save & Exit" >
				
			</td>
		</tr>	

</table>

		
</form>
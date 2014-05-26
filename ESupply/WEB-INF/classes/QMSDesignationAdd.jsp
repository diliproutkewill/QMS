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
/*	Programme Name : QMSDesignationAdd.jsp.
*	Module Name    : QMS.
*	Task Name      : DHL-QMS
*	Sub Task Name  : Adding the Designation Within the Organization.
*	Author		   : K.NareshKumarReddy.
*	Date Started   :6/17/2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
    logger  = Logger.getLogger("QMSDesignationAdd.jsp");
		 String FILE_NAME	=	"QMSDesignationAdd.jsp";
  try 			
  {

	 if(loginbean.getTerminalId() == null) 
	 {											 
%>
   <jsp:forward page="../ESupplyLogin.jsp" />
<%
	 }
	 else
	 {   
 %>
<html>
<head>
<title>Designation Add </title>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
<!--

function upperCase(input)
 {
  input.value=input.value.toUpperCase();
 }
function getKeyCode()
 {
  if(event.keyCode!=13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
function getSpecialCode()
 {
	
  if(event.keyCode!=13)
    {
      if((event.keyCode > 32 && event.keyCode < 40) || event.keyCode == 64   ||event.keyCode==96 || event.keyCode==126 || event.keyCode==45)
	 event.returnValue =false;
    }
  return true;
 }


function specialCharFilter(input,label) 
	{
		
		s = input.value;
		
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,.?"+'"';		
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 ) 
					returnString += c.toUpperCase();
			else
			{
				alert("Please do not enter special characters "+label);
				input.value = "";
				input.focus();
				return false;
			}
		}
		return true;
	}

function placeFocus() 
{
  document.forms[0].designationId.focus(); 
 }

function Mandatory()
{
	for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		  document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }
	   
   description  =  document.forms[0].description.value;
    designationId   =  document.forms[0].designationId.value; 
	levelNo=document.forms[0].levelNo.value
    if(designationId.length ==0)
	{
	    alert(" Please enter Designation Id");
		document.forms[0].designationId.focus();
		return false;
	}
	
    
	if(designationId.length ==0)
	{
		alert("Please enter Designation Id ");
		document.forms[0].designationId.focus();
		return false;
	}
	if(description.length == 0)
	{
		alert("Please enter Designation description");
		document.forms[0].description.focus();
		return false;
	}
	if(levelNo.length== 0)
	{
		alert("Please enter levelNo ");
		document.forms[0].levelNo.focus();
		return false;
	}
	document.forms[0].desigSubmit.disabled='true';				
  	return true;	
}
function checkNumeric(input)
{
	if(isNaN(input.value))
	{
		alert("Please Enter Numeric Values Only.");
		input.value='';
		input.focus();
		return false;
	}
}
</script>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body OnLoad="placeFocus()">
<form method="POST"  onSubmit="return Mandatory()" action="QMSDesignationProcess.jsp?operation=add" name="designation" onLoad="placeFocus()" >
<table width="800"  border="0" cellspacing="0" cellpadding="0" >
  <tr valign="top"> 
    <td  colspan="2" bgcolor="ffffff">
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'> 
			 <td ><table width="790" border="0" >
			 <tr class='formlabel'><td>Designation-Add </td>
			 <td align=right>QS1020111</td></tr></table></td>
        </tr>
        </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
			  <tr class='formdata'><td colspan="5">&nbsp;</td></tr>
                <tr valign="top" class='formdata'> 
                  <td width="200" >Designation Id:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="designationId" size="20"  maxlength="15" onBlur="upperCase(designationId)" onkeyPress='return getSpecialCode(designationId);specialCharFilter(this,"") '>
                    </td>
                  <td width="241">Description:<font color="#FF0000">*</font>
                    <br>
                    <input type='text' class='text' name="description" size="55" maxlength="50" onBlur='upperCase(description)' onkeyPress="return getSpecialCode(description)">
                    </td>
                  <td width="102">Level No:<font color="#FF0000">*</font><br>
                    <input type="text" class='text' name="levelNo" size="5" maxlength="2" onBlur="return checkNumeric(this)">
                    </td>
                  </tr>
              </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'> 
                  <td valign="top" colspan="4"> 
					  <font color="#FF0000">*</font><font face="Verdana" size="1">Denotes Mandatory</font>
                </tr>
                <tr class='denotes' > 
                	<td  align="right">
					 <input type="submit" value="Submit" name="desigSubmit"  class='input'>
					 <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
                  </td>
                </tr>
              </table>
          </td>
        </tr>
      </table></form>
</body>
</html>
<%
  }
  	}
	catch(Exception e)
  	{
		 //Logger.error(FILE_NAME,"Error in QMSDesignationAdd.jsp file : "+e.toString());
     logger.error(FILE_NAME+"Error in QMSDesignationAdd.jsp file : "+e.toString());
		 
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("Error while accessing the page","QMSDesignationAdd.jsp");
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
	
%>
		 <jsp:forward page="../ESupplyErrorPage.jsp" /> 
<%
 	}	   
%>  


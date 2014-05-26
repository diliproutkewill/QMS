<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSAttachmentEnterId.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: Add/Modify/View/Delete
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61289

--%>

<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				         com.foursoft.esupply.common.java.KeyValue,
				         java.util.ArrayList"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%! private static Logger logger = null;
    public final static String   FILE_NAME = "QMSAttachmentEnterId.jsp";%>

<%
    logger                          = Logger.getLogger(FILE_NAME);  
    String       operation          =  request.getParameter("Operation");
	String       userTerminalType   = loginbean.getUserTerminalType(); 
    try 			
   {
        
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Quote Attachment Master - <%=operation%></title> 
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script>
var	userTerminalType 			= '<%=userTerminalType%>';
var accessType					= '<%=loginbean.getAccessType()%>';
var	operation					= '<%=operation%>';
var terminalId					= '<%=loginbean.getTerminalId()%>';	
function mandatory()
{
   if(document.forms[0].terminalId.value=="")
	{
      alert("Please Enter Terminal Id");
	  document.forms[0].terminalId.focus();
      return false;
   }
   else if(document.forms[0].attachmentId.value=="")
   {
      alert("Please Enter Attachment Id");
	  document.forms[0].attachmentId.focus();
      return false;
   } 
    else if(document.forms[0].attachmentId.value.length>40)
   {
      alert("AttachmentId length shouldnot exceed 40 characters");
	  document.forms[0].attachmentId.focus();
      return false;
   } 
    return true;
}
 function checkKeyCode()
{
	if(event.keyCode!=13)
	{	
		if((event.keyCode < 48 ||event.keyCode > 57) && (event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) && event.keyCode!=32 && event.keyCode!=38 && event.keyCode!=40 && event.keyCode!=41 && event.keyCode!=45 && event.keyCode!=95)
 	   		return false;	
	}
	return true;	
}
function changeToUpper(field)
{
	field.value	= field.value.toUpperCase();
	
}
function showUserterminal(input)
{
   if(input.value==userTerminalType)
	   document.forms[0].terminalId.value	='<%=loginbean.getTerminalId()%>';
   else
	   document.forms[0].terminalId.value	=	"";

}
 function specialCharFilter(input)
    {
        filteredValues = "';~#!+%?,$^=*~"+"@";
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < input.value.length; i++)
        {
            var c = input.value.charAt(i);
            if ( filteredValues.indexOf(c) == -1 )
                returnString += c.toUpperCase();
            else
            {
                alert("Please dont enter following special charcters:';~#!+%?,$^=*~"+"@");
                input.focus();
                input.select();
                return false;
            }
        }	
        return true;
    }
function showLOV()
{
	//var condition;
	
	var Url	   = 'ETCLOVTerminalIds.jsp?terminalId='+terminalId+'&terminalType='+document.forms[0].accessType.value+'&searchString='+document.forms[0].terminalId.value.toUpperCase()+'&module=AttachmentMaster&shipmentMode=';	

	var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options    = 'scrollbars=yes,width=360,height=360,resizable=yes';
	var Features   =  Bars+''+Options;
	var Win	       =  open(Url,'Doc',Features);
}
function showAttachmentIds()
{
	if(document.forms[0].terminalId.value=="")
	{
		  alert("Please Enter Terminal Id");
		  document.forms[0].terminalId.focus();
		  return false;
	}
	else
   {
		var Url   = 'QMSAttachmentIdList.jsp?accessType='+document.forms[0].accessType.value+'&terminalId='+document.forms[0].terminalId.value.toUpperCase()+'&attachmentId='+document.forms[0].attachmentId.value;	
		var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options    = 'scrollbars=yes,width=360,height=360,resizable=yes';
		var Features   =  Bars+''+Options;
		var Win	       =  open(Url,'Doc',Features);
   }
}
function verifyTerminalId()
{
  
	if(document.forms[0].terminalId.value=="")
	{
		  alert("Please Enter Terminal Id");
		  document.forms[0].terminalId.focus();
		  return false;
	}

}
</script>
<body>
<form  action='QMSAttachmentController' onSubmit='return mandatory()' method='post'>
<table border=0 cellpadding=4 cellspacing=0 width="100%">
	<tr class='formlabel' valign=top> 
		<td colspan=4>Quote Attachment Master- <%=operation%> </td>
		<td align=right><%=loginbean.generateUniqueId("QMSAttachmentEnterId.jsp",operation)%></td>
	</tr>
</table>
 <%

	if(request.getAttribute("ErrorMessage")!=null)
	{
%>	
	<table width="100%"   border="0" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr color="#FFFFFF">
			<td colspan="8">
				<font face="Verdana" size="2" color='red'><b>The form has not been submitted because of the following error(s):</b><br><br>
					<%=request.getAttribute("ErrorMessage")%></font>
			</td>
		</tr>
	</table>
<%
	}
%>
<table width="100%" border='0' cellspacing='0' cellpadding='1' bgcolor="#FFFFFF">
	<tr>
		<td colspan='3'>&nbsp;</td>
	</tr>
	<tr  class="formdata">
		<td valign="top">Access Type<font color='#FF0000'>*</font></td>
		<td valign="top">Created At terminal<font color='#FF0000'>*</font></td>
		<td valign="top">Attachment ID<font color='#FF0000'>*</font></td>
	</tr>
	<tr class="formdata">
		<td valign="middle">
			<select size="1" name="accessType" class='select' onChange='showUserterminal(this)'>
				 <%if(userTerminalType.equals("H"))
				    {
				%>
						<option value="H" >HO Terminal</option>
						<option value="A" >Admin Terminal</option>
						<option value="O">Operation Terminal</option> 
				<%
				  }
				  else if(userTerminalType.equals("A"))
				 {
				%>
						<option value="A" >Admin Terminal</option>
						<option value="O" >Operation Terminal</option> 
		
				<%
				 }
				%>
			</select>
		</td>
		<td valign="middle">
			<input type="text" class="text" name="terminalId"	size="18" onBlur=changeToUpper(terminalId) >
			<input type=button value="..." onClick=showLOV() name="button1" class="input">
		</td>
		<td valign="middle">
			<!--<input type="text" class="text" name="attachmentId" size="25" onBlur=changeToUpper(attachmentId)  onKeypress = "verifyTerminalId();return checkKeyCode();">-->
          <input type="text" class="text" name="attachmentId" size="25" onBlur="changeToUpper(attachmentId);return specialCharFilter(this); " onKeypress = "verifyTerminalId();return checkKeyCode();">
		 <%if(operation.equals("View")||operation.equals("Modify")||operation.equals("Delete")){%>
	       	<input type=button value="..." onClick=showAttachmentIds() name="button1" class="input">
		   <%}%>
		</td>
	</tr>
</table>
<table width="100%" border='0' cellspacing='0' cellpadding='0' bgcolor='#FFFFFF'>
	<tr> 
		<td valign=top class='denotes'><font color='#FF0000'>*</font>Denotes Mandatory 
		</td>
		<td valign=top align='right'>
				<input name=Reset type=reset value=Reset class='input'>
				<input name=B1 type=submit value="Next>>" class='input'>
				<input name="Operation" type="hidden" value="<%=operation%>" class='input'>
				<input name="subOperation" type="hidden" value="Next" class='input'>
	   </td>
	</tr>
</table>
</form>
</body>
</html>
<%
   }
	catch(Exception e)
	{
		e.printStackTrace();
	    logger.error(FILE_NAME+"Error in QMSAttachmentEnterId.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSAttachmentEnterId.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>

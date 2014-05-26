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
  /**
	
	Program Name		:	ETCGatewayRegistrationEnterId.jsp
	Module Name			:   ETrans
	Task				:	Gateway Master
	Sub Task			:	Modify/View/Delte
	Author Name			:   Anand.A
	Date Started		:	September 11,2001
	Date Ended			:	September 11,2001
	Date Modified		:  
	
	
	Description			:	This File main Purpose is to capture the Gateway Master Id and then
	                        submit that value to ETCGatewayRegistrationEnterId.jsp. Depending the 
							Parameter passed i.e., Modify/View/Delete the user is allowed to 
							Modify,View,Delete respectively.    
  */
%>
<%@ page import	=	"org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCGatewayRegistrationEnterId.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
   try
   {
    if(loginbean.getTerminalId() == null )
    {
%>
      <jsp:forward page="../ESupplyLogin.jsp"/>
<%
    }
   }catch(Exception e)
    {
	 //Logger.error(FILE_NAME,"Error in ETGatewayRegistrationModifyEnterId.jsp file : ", e.toString());
   logger.error(FILE_NAME+"Error in ETGatewayRegistrationModifyEnterId.jsp file : "+ e.toString());
	} 
%>    	
<%
    
	String operation   = null;
    try
	{
      operation = request.getParameter("Operation");
	  session.setAttribute("Operation",operation);
    }catch(Exception e)
     {
	  //Logger.error(FILE_NAME,"Error in ETGatewayRegistrationModifyEnterId.jsp file : ", e.toString());
    logger.error(FILE_NAME+"Error in ETGatewayRegistrationModifyEnterId.jsp file : "+ e.toString());
	 }
%>
<html>
<head>
<title>Gateway Registration</title>
<script language="JavaScript">
<!--
function fillFields()
  {
	if( document.forms.length > 0 ) 
	  	{
			var field = document.forms[0];
			for(i = 0; i < field.length; i++) 
			{
		 		if( (field.elements[i].type == "text") || (field.elements[i].type == "textarea") )		
		 		{	       		       			
		   			document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
	     		}
	    	}
	   	}
    if(document.forms[0].gatewayId.value.length==0)
     {
       alert("Please enter GatewayId");
       document.forms[0].gatewayId.focus();
	   return false
     }
     document.forms[0].submit.disabled='true';
     return true;

  }   
  
function setCursor()
  { 
    document.forms[0].gatewayId.focus();
  } 
function populateGateway()
  {
    var gatewayId1  = document.forms[0].gatewayId.value;
	var Url			= 'ETCLOVGatewayIds.jsp?type=two&searchString='+gatewayId1;
	var Bars		= 'directories=no,location=no,menubar=no,status=no,titlebar=no';
    var Options		= 'scrollbars=yes,width=360,height=360,resizable=no';
    var Features	= Bars+''+Options; 
    var Win			=  open(Url,'Doc',Features);
    return false;
  }
      	   
function checkKeyCode()
  {
   if(event.keyCode != 13)
	{
	 if((event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122)) 
	 {
	  return false;
     }
    }
   return true; 
  }
function changeToUpper(field)
{
  field.value = field.value.toUpperCase();
}		                                                   
   
//-->
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body " onLoad="setCursor()">
<form method="Post"  name="gway" onSubmit="return fillFields()" action="ETCGatewayRegistrationDetail.jsp" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
	<tr valign="top" bgcolor="#FFFFFF"> 
    <td> 
<table border="0" width="800" cellpadding="4" cellspacing="1">
	<tr valign="top" class='formlabel'> 
    <td colspan="4" ><table width="790" border="0" ><tr class='formlabel'><td>Gateway - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETCGatewayRegistrationEnterId.jsp",operation)%></td></tr></table></td>
	</tr></table>
	<table border="0" width="800" cellpadding="4" cellspacing="1">
	<tr valign="top" class='formdata'> 
    <td colspan="4" >&nbsp;</td>
	</tr>
    <tr valign="top" class='formdata'> 
    <td colspan="4" ><b>Enter the Gateway Id</b></td>
	</tr>
    <tr valign="top" class='formdata'> 
    <td colspan="4">Gateway Id:<font color="#FF0000">*</font><br>
	<input type="text" class="text" name="gatewayId" maxlength="16" size="20" onBlur="changeToUpper(this)"  onKeyPress="return checkKeyCode()">
    <input type="button" class='input' value="..." name="jbt_LOVGatewayId" onClick="populateGateway()"></td> 
	</tr>
    <tr valign="top" class='denotes'> 
    <td colspan="2" ><font color="#FF0000">*</font>stands for mandatory</td>
    <td colspan="2"   align="right">
    <input type="submit" name="submit" value="Next>>" class='input'>
	<input type="reset" name="Reset" value="Reset" class='input'>
    </td>
	</tr>
</table>
	</td>
    </tr>
</table>
</form>
</body>
</html>
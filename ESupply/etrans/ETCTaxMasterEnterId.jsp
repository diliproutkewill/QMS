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
/*	Program Name	:ETCTaxMasterEnterId.jsp
	Module Name		:ETrans
	Task			:TaxMaster
	Sub Task		:EnterId
	Author Name		:Ushasree.Petluri
	Date Started	:September 11,2001
	Date Completed	:September 12,2001
	Date Modified	:September 11,2001 by Ushasree.P
	Description	:This file main purpose is to capture Ids and get relavent data from the database.
*/
%>
<%@ page import	=	"org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" />
<%!
  private static Logger logger = null;  
	private static final String FILE_NAME	=	"ETCTaxMasterEnterId.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	try
	{// try begins
		if(loginbean.getTerminalId()== null)
		{
%>		
   <jsp:forward page="../ESupplyLogin.jsp" />
 <%
		}
  		else
  		{	// else begins    
%>
 <%
	    	String operation =request.getParameter("Operation"); // for storing operation
%>
<html>
<head>
<title>TaxMaster EnterId</title>
<script language="javascript">

function placeFocus() 
{
   document.forms[0].taxId.focus();
}
function showLOV()
{
	//var Url 	= 'ETCLOVTaxIds.jsp?searchString='+document.forms[0].taxId.value.toUpperCase();	
	<% // @@ Anup modified for SPETI-3718 on 20050124  %>
	var Url 	= 'ETCLOVTaxIds.jsp?searchString='+document.forms[0].taxId.value.toUpperCase()+'&Operation='+'<%=operation%>';	
	<% // @@  20050124 %>
	var Bars 	= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options	='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
    var Win		=open(Url,'Doc',Features);
}
function upperCase(input)
{    	
    input.value = input.value.toUpperCase();
}

function getKeyCode()
{
  if(event.keyCode!==13)
  {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
  }
  return true;
}	
function Mandatory()
{
	document.forms[0].taxId.value=document.forms[0].taxId.value.toUpperCase();
	taxId = document.forms[0].taxId.value.length;
	if(taxId ==0)
	{
		alert("Please enter Tax Id ");
		document.forms[0].taxId.focus();
		return false;
	}
	else if(taxId < 3)
	{
		alert("Please enter three characters for Tax Id");
		document.forms[0].taxId.focus();
		return false;
	}
	document.forms[0].enter.disabled='true';
	return true;
}	
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad="placeFocus();">
<form method="GET" onSubmit="return Mandatory()" action="ETCTaxMasterAdd.jsp" name="tax" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" > 
    <td bgcolor="ffffff"> 
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td><table width="790" border="0" ><tr class='formlabel'><td>Tax - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETCTaxMasterEnterId.jsp",operation,loginbean.getAccessType())%></td></tr></table></td>
        </tr>
        </table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
		<tr class='formdata'><td colspan="2">&nbsp;</td></tr>
			  <tr class='formdata'><td colspan="2"><b>Enter TaxId to <%=operation%> Tax Information:</b></td></tr>
                <tr class='formdata'> 
                  <td colspan="2" >Tax Id:<font color="#FF0000">*</font><br>
				    <input type="text" class="text" name="taxId" maxlength="3" size="8"  onBlur="upperCase(taxId)" onkeyPress="return getKeyCode(taxId)">
				    <input type="button" class='input' value="..." name="taxIdNames" onClick="showLOV()"  >
				    <input type="hidden" name=Operation value="<%= operation %>">	    		
                    <input type="hidden" name="termId" >
                    </td>
                </tr>
                <tr class='denotes'> 
                  <td  valign="top" ><font color="#FF0000">*</font>Denotes 
                    Mandatory </td>
                  <td  valign="top" align="right"> 
					<input type="submit" value="Next>>" name="enter" class='input'>
					<input type="reset" name="Reset" value="Reset" onClick="placeFocus()" class='input'>
                      
                     
                  </td>
                </tr>
              </table>
            
          </td>
        </tr>
      </table></form>
    
</body>
</html>
<%
		}// else ends here
	} // ends try
	 catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception in ETCTaxMasterEnterId.jsp:", e.toString());
    logger.error(FILE_NAME+"Exception in ETCTaxMasterEnterId.jsp:"+ e.toString());
	}
%>
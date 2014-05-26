<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSEmailTextMasterEnterId.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: Modify/View/Delete
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61295

--%>

<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
                 com.foursoft.esupply.common.java.KeyValue,
                 com.foursoft.esupply.delegate.UserRoleDelegate,
                 java.util.ArrayList"%>

<jsp:useBean id="loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%! 
  private static Logger logger = null;  
  public final static String   FILE_NAME = "QMSEmailTextMasterEnterId.jsp";%>

<%
 	logger  = Logger.getLogger(FILE_NAME);  
    String       operation      =  request.getParameter("Operation");
	String userTerminalType     = loginbean.getUserTerminalType();
	String		terminalId	    =  null;
	String		accessType		=  null;
	String		quoteType		=  null;
    try 			
   {
       terminalId	=	request.getParameter("terminalId");
	   accessType   =	request.getParameter("accessType");
	   quoteType   =	request.getParameter("quoteType"); 
	 
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>EmailTextMaster</title> 
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script language="javascript">
var Win  = null;
var	userTerminalType 			= '<%=userTerminalType%>';
var accessType					= '<%=loginbean.getAccessType()%>';
var	operation					= '<%=operation%>';
var companyId					= '<%=loginbean.getCompanyId()%>';	

function initialize()
{
	document.forms[0].terminalId.value	='<%=loginbean.getTerminalId()%>';   

}
function mandatory()
 {
	var index	=	document.forms[0].type.value;
	//alert(index);
	if(index=="")
	 {
		alert("Please Select Type")
		return false;
	 }
	
	if(document.forms[0].terminalId.value.length ==	0)
	{
		alert("Please Enter TerminalId");
		document.forms[0].terminalId.focus();
		return false;
	}
	if(document.forms[0].terminalId.value.length == 0)
	{
		alert("Please Enter	Correct	TerminalId");
		document.forms[0].terminalId.focus();
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

function	showLOV()
	{
		var condition;
		
		if(userTerminalType!='A' && userTerminalType!='O')
			companyId	=	"";
		var Url	   = 'ETCLOVTerminalIds.jsp?companyId='+companyId+'&terminalType='+document.forms[0].accessType.value+'&searchString='+document.forms[0].terminalId.value.toUpperCase()+'&module=EmailTextMaster'+'&shipmentMode=';	

		var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;
		var Win	   =  open(Url,'Doc',Features);
	}
	//added by VLAKSHMI for WPBN issue 167673 (CR) on 22/04/2009
function showHide()
{

	var data="";
	var data1="";
	var index	=	document.forms[0].type.value;
	//alert(index)
	if(index=="quote")
	{
		data1='Quote&nbsp;Type:<font  color=#ff0000>*</font>';

		 data='<select size="1" name="quoteType" class="select" ><option value = "N">New</option><option value = "U">Updated</option></select>';
	}
	else if(index=="cost")
	{
     <!--modified by VLAKSHMI for issue 169861 on 06/05/09 -->
		//data='Quote&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="quoteType" class="select" ><option value = //"C">Costing</option></select>';
		data='<input type ="hidden"  name="quoteType" value="C" />';
	}
		if( document.layers)
	{
		document.layers.cust1.document.write(data);
		document.layers.cust1.document.close();
		document.layers.cust2.document.write(data1);
		document.layers.cust2.document.close();
	}
	else
	{
		if(document.all)
		{
		   cust1.innerHTML = data;
		   cust2.innerHTML = data1;
		 }
	 }
	
}
function showHide1()
{

	var data="";
	var data1="";
	var index	=	document.forms[0].type.value;
	
	
		data1='';

		 data='';
	
		//data='Quote&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="quoteType" class="select" ><option value = //"C">Costing</option></select>';
		data='';
	
		if( document.layers)
	{
		document.layers.cust1.document.write(data);
		document.layers.cust1.document.close();
		document.layers.cust2.document.write(data1);
		document.layers.cust2.document.close();
	}
	else
	{
		if(document.all)
		{
		   cust1.innerHTML = data;
		   cust2.innerHTML = data1;
		 }
	 }
	
}

//end
 </script>
 <body >
 <form  action = 'QMSEmailTextController' onSubmit='return mandatory();' method='post' >
 <table border=0 cellpadding=4 cellspacing=0 width="100%">
	<tr class='formlabel' valign=top> 
	    <td colspan=2>Email Text Master - <%=operation%> </td>
        <td align=right><%=loginbean.generateUniqueId("QMSEmailTextMasterEnterId.jsp",operation)%></td>
     </tr>
  </table>
   <%

	if(request.getAttribute("errorMessage")!=null)
	{
%>	
	<table width="100%"   border="0" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
	<tr color="#FFFFFF">
	<td colspan="8">
	<font face="Verdana" size="2" color='red'><b>The form has not been submitted because of the following error(s):</b><br><br>
	<%=request.getAttribute("errorMessage")%></font>
	</td>
	</tr>
	</table>
<%
	}
%>
  <table width="100%" border='0' cellspacing='1' cellpadding='2' bgcolor="#FFFFFF">
     <tr class="formdata">
        <td colspan='4'>&nbsp;</td>
     </tr>
     <tr class="formheader">
       <td width="25%" valign="middle">Access Type</td>
       <td width="25%" valign="middle">Terminal Id</td>
       <td valign="middle">Type</td>
	  <td valign="middle"><div id='cust2' style='position:relative;'></div> </td>
     </tr>
     <tr class="formdata">
        <td  valign="top" >

			  <select size="1" name="accessType" class='select' onChange='showUserterminal(this)'>

			  <%if(userTerminalType.equals("H"))
				{
			%>

					<option value="H" <%="H".equalsIgnoreCase(accessType)?"selected":""%>>HO Terminal</option>
					<option value="A" <%="A".equalsIgnoreCase(accessType)?"selected":""%>>Admin Terminal</option>
					<option value="O" <%="O".equalsIgnoreCase(accessType)?"selected":""%>>Operation Terminal</option> 
					  
			<%
						  }
			  else if(userTerminalType.equals("A"))
			 {
			%>
					<option value="A" <%="A".equalsIgnoreCase(accessType)?"selected":""%>>Admin Terminal</option>
					<option value="O" <%="O".equalsIgnoreCase(accessType)?"selected":""%>>Operation Terminal</option> 
					
			<%
			 }
			 else
			 {
			%>
				   <option value="O" <%="O".equalsIgnoreCase(accessType)?"selected":""%>>Operation Terminal</option> 
			<%
			 }
			%>

			</select>
		</td>
		<td valign="top">
			<input type="text" class="text" name="terminalId"	size="18" onBlur=changeToUpper(terminalId) value = <%=loginbean.getTerminalId()%>>
			<input type=button value="..." onClick=showLOV() name="button1" class="input">
		</td>
		<td valign="top">
			<select size="1" name="type" class='select' onChange='showHide()'>
			<option value = ""></option>
			<option value = "quote">Quote</option>
			<option value = "cost">Costing</option>
			</select>
		</td>
		<td valign="top">
				  <div  id='cust1' style='position:relative;'></div>
				</td>
	</tr>
</table>
<table width="100%" border='0' cellspacing='1' cellpadding='4' bgcolor='#FFFFFF'>
	<tr> 
		<td colspan=3 valign=top class='denotes'>All Fields are Mandatory
		</td>
		<td colspan=2 valign=top align='right'> 
			<input name=B1 type=submit value=Submit class='input'>
			<input name="Operation" type="hidden" value="<%=operation%>" class='input'>
			<input name="subOperation" type="hidden" value="submit" class='input' >
			<input name=Reset type=reset value=Reset class='input' onClick='showHide1()'>
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
	  logger.error(FILE_NAME+"Error in QMSEmailTextMasterEnterId.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSEmailTextController");
		keyValueList.add(new KeyValue("Operation",operation));
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}
%>
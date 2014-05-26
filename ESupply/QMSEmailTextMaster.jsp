<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSEmailTextMaster.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: Add/Modify/View/Delete
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61295

--%>
<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				         com.foursoft.esupply.common.java.KeyValue,
				         java.util.ArrayList, com.qms.setup.java.QMSEmailTextDOB "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%! 
  private static   Logger logger = null;
  String		 quoteType      = null;
  String		 emailText      = null;	
  String type= null;
 
  public final static String   FILE_NAME = "QMSEmailTextMaster.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);  
    String       operation      =  request.getParameter("Operation");
	type=request.getParameter("type");
	try 			
   {

	     QMSEmailTextDOB	 emailTextDOB    =   (QMSEmailTextDOB)session.getAttribute("emailTextDOB");
		 if(emailTextDOB!=null)
	    {
		   quoteType                 =   emailTextDOB.getQuoteType();
		   if("N".equalsIgnoreCase(quoteType))
                quoteType			 =	 "New";
		   else if("U".equalsIgnoreCase(quoteType))
			    quoteType			 =	 "Updated";
		     else
			    quoteType			 =	 "Costing"; //added by VLAKSHMI for WPBN issue 167673 (CR) on 22/04/2009

		   emailText                 =   emailTextDOB.getEmailText();
	    }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>EmailTextMaster</title> 
 <link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="javascript">
 function mandatory()
{
	var index	=	document.forms[0].type.value;
	//alert(index);
	if(index=="")
	 {
		alert("Please Select Type")
		return false;
	 }
	if(document.forms[0].emailText.value=="")
	{
		alert("Please Enter the Email Text Description");
		document.forms[0].emailText.focus();
		return false;
	}
   
	return true;
}	
function confirmDelete()
{
	var r	=	confirm("Are you sure to delete it");
	if(r==true)
	{
		document.forms[0].action  =	'QMSEmailTextController?Operation=Delete&subOperation=delete';
		document.forms[0].submit();
	}
	else
	{	
		document.forms[0].action  =	'QMSEmailTextMasterEnterId.jsp?Operation=Delete';
		document.forms[0].submit();
	}
}
//added by VLAKSHMI for WPBN issue 167673 (CR) on 22/04/2009
function showHide()
{

	var data="";
	var data1="";
	var index	=	document.forms[0].type.value;
	if(index=="quote")
	{
  // modified by VLAKSHMI for issue 169861 on 06/05/09
		//<tr class="formheader">
		//<td width="25%" valign="middle">Type</td>
		 data1='Quote&nbsp;Type:<font  color=#ff0000>*</font>';

		 data='<select size="1" name="quoteType" class="select" ><option value = "N">New</option><option value = "U">Updated</option></select>';
	}
	else if(index=="cost")
	{
	//	data='Quote&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="quoteType" class="select" ><option value = //"C">Costing</option></select>';
		data='<input type ="hidden"  name="quoteType" value="C" />';
		//<input name=B1 type=submit value=Submit class='input'>
		//document.getElementByName('quoteType').value="C";
		//document.forms[0].quoteType.value="C";
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
 </head>
<body >
<form  action = 'QMSEmailTextController' onSubmit='return mandatory()' method='post'>
<table border=0 cellpadding=4 cellspacing=0 width="100%">
	<tr class='formlabel' valign=top> 
		<td  colspan=2>Email Text Master - <%=operation%> </td>
	    <td align=right><%=loginbean.generateUniqueId("QMSEmailTextMaster.jsp",operation)%></td>
	</tr>
</table>
<table width="100%" border='0' cellspacing='1' cellpadding='2' bgcolor="#FFFFFF">
	<tr class="formdata">
		<td colspan='3'>&nbsp;</td>
	</tr>
	<%if("Modify".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation))
  {
%>

	<tr class="formheader">
		<td width="25%" valign="middle">Type</td>
		
		<td valign="middle">Email Text</td>
	</tr>
	<% } else { %>
<tr class="formheader">
		<td width="25%" valign="middle">Type</td>
		<td><div id='cust2' style='position:relative;'></div></td>
		<td valign="middle">Email Text</td>
	</tr>
	<% }%>
<%if("View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation))
  {
%>
	<tr class="formdata">
		<td  valign="top" >
			<select size="1" name="quoteType" class='select' readOnly>
				<option value = "<%=quoteType%>"><%=quoteType%></option>
			</select>
		</td>
		<td>
			<textarea  class='text' cols='50' rows='5' name='emailText' readOnly><%=emailText%></textarea>
		</td>
	</tr>
<%
  }
  else	if("Modify".equalsIgnoreCase(operation))
  {
%>
<tr class="formdata">
	<td  valign="top" >
	<% if("quote".equalsIgnoreCase(type))  { %>
		<select size="1" name="quoteType" class='select' >
			<option value = "N"<%="New".equalsIgnoreCase(quoteType)?"selected":""%>>New</option>
			<option value = "U" <%="Updated".equalsIgnoreCase(quoteType)?"selected":""%>>Updated</option>
			</select>
			<%} else { %>
			<select size="1" name="quoteType" class='select' >
			<option value = "C" <%="Costing".equalsIgnoreCase(quoteType)?"selected":""%>>Costing</option>
			<%}%>
		</select>
	</td>
	<td>
		<textarea  class='text' cols='50' rows='5' name='emailText' value=''><%=emailText!=null?emailText:""%></textarea>
	</td>
 </tr>
<%
  }
  else
  {
%>
 <tr class="formdata">
	<td  valign="top" >
		<select size="1" name="type"  class='select' onChange='showHide()' >
		<option value = ""></option>
			<option value = "quote">Quote</option>
			 <option value = "cost">Costing</option>
			
		</select>
	</td>
	<td>
				  <div id='cust1' style='position:relative;'></div>
				</td>
	<td>
		<textarea  class='text' cols='50' rows='5' name='emailText'></textarea>
	</td>
 </tr>
 <%
  }
%>
</table>
<table width="100%" border='0' cellspacing='1' cellpadding='4' bgcolor='#FFFFFF'>
	<tr> 
		<td colspan=3 valign=top class='denotes'>All Fields are Mandatory
		</td>
		<td colspan=2 valign=top align='right'> 
<%if("View".equalsIgnoreCase(operation))
  {
%>
	 <input name=B1 type=submit value=Continue class='input'>
	 <input name="Operation" type="hidden" value="<%=operation%>" class='input'>
	<input name="subOperation" type="hidden" value="continue" class='input'>
<%
  }
  else	if("Delete".equalsIgnoreCase(operation))
  {
%>
    <input name="Delete" type=button value="Delete" class='input' onClick=confirmDelete()>
	
<%
}
  else
 {
%>
	<input name=B1 type=submit value=Submit class='input'>
	<input name="Operation" type="hidden" value="<%=operation%>" class='input'>
	<input name="subOperation" type="hidden" value="update" class='input'>
	<input name=Reset type=reset value=Reset class='input' onClick='showHide1()'>
<%
   }
%>
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
	    logger.error(FILE_NAME+"Error in QMSEmailTextMaster.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSEmailTextMaster.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>
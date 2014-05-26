<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program Name	: QMSQuoteStatusReasonDetails.jsp
Product Name	: QMS
Module Name		: Status Reason  master
Task		    : Adding/View/Modify/Delete SurChargesMaster
Date started	: 25-Jan-11
Date modified	:  
Author    		: Venkata kishore Podili
Description		: The application "View/Modify/Delete"  Status Reason Masterinformation
Actor           :
Related Document: CR-231109-12-Reports-Pending Status Add Notes Filter-Status Reasons-V1.0.doc
--%>

<%@page import = "com.qms.setup.java.QuoteStatusReasonDOB,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  javax.ejb.ObjectNotFoundException"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSQuoteStatusReasonDetails.jsp";
%>
<%
try{
	String operation	=	request.getParameter("Operation");
	String terminalId	=	loginbean.getTerminalId();
	int id	=	0;
	String statusReason	=	"";
	String invalid					= "";
	String readOnly			=	"";
	QuoteStatusReasonDOB  statusReasonDOB	=	null;
	statusReasonDOB	=	(QuoteStatusReasonDOB)session.getAttribute("statusReasonDOB");

	if(statusReasonDOB!=null){
	
	statusReason	=	statusReasonDOB.getStatusReason();
    id		=	statusReasonDOB.getId();
	invalid =statusReasonDOB.getInvalid();
	System.out.println("operation..."+operation);
	if("View".equalsIgnoreCase(operation) || "Delete".equalsIgnoreCase(operation)|| "Invalidate".equals(operation) )
		readOnly	=	"readOnly";
	}
%>
<html>
<head>
<title>Status Reason <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>
<script >

	var Win  = null;

 
	function chkMandatory(){
		
		if(document.forms[0].statusReason.value=='')
		{
				alert("Please Enter Any statusReason")
					document.forms[0].statusReason.focus();
					return false;
		}

		<% if("Invalidate".equals(operation)) {%>
		 if(document.forms[0].invalid1.checked)
				document.forms[0].invalid.value= 'T'
		else
				document.forms[0].invalid.value= 'F'
    	<%}%>

		return true;
		
}


 function toUpper(obj){
	obj.value = obj.value.toUpperCase();
 }

 function getDotNumberCode(input)    // Numbers + Dot
{
	if(event.keyCode!=13)
    {
     if ((event.keyCode > 32 && event.keyCode < 40) ||(event.keyCode > 41 && event.keyCode < 44) ||(event.keyCode > 47 && event.keyCode < 65)  ||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
	 
    }
  return true;
 }
</script>
</head>
<body>
<form name="surchargesmaster" method="post" action="<%=request.getContextPath()%>/QMSQuoteStatusReasonController" onSubmit="return chkMandatory();">
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> Status Reason - <%=operation%> </td>
						<td align=right>QS1050521
						</td>
					</tr>
			  </table>
			</td>
		  </tr>
		</table>
		<table border='0' width="100%" cellpadding="0" cellspacing="0">
			<tr class='formdata' width='50%'>
				
				<%if("Invalidate".equals(operation)) {%>

						<td align ='left' class='formheader'>
							<b>Invalid<font color="#FF0000">*</font></b>
						</td>	
						<td  width='20%' class='formheader'>
							<b>StatusReason:<font color="#FF0000">*</font></b>
					</td>	

			<%	} if(!"Invalidate".equals(operation)) {%>
					<td align ='center'  class='formheader'>
				<b>StatusReason:<font color="#FF0000">*</font></b>
				</td>		
				<%}%>
				
								
				</tr>
				<tr>
					<%if("Invalidate".equals(operation)) {%>
					<td  class='formdata'>
					<input type='checkbox' name='invalid1' value='<%=invalid%>'  <%="T".equals(invalid)?"checked":""%>  />
					</td>
								
					<%}%>
					<td align ='center'  class='formdata'>
					<input type='text' name='statusReason' value='<%=statusReason%>' size='30' maxlength='100' onBlur='toUpper(this);'  <%=readOnly%>	 onkeyPress= "getDotNumberCode(this);">
					</td>
									
				</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory<br>
            </td>
            <td valign="top" align="right">
				<input type="reset" value="Reset" name="reset" class="input">
				<%if("Modify".equalsIgnoreCase(operation)){%>
                <input type="submit" value="Modify" name="submit" class="input" >
				<%}
			else if("Delete".equalsIgnoreCase(operation)){%>
                <input type="submit" value="Delete" name="submit" class="input" >
				<%}else{%>
                <input type="submit" value="Continue" name="submit" class="input" >
				<%}%>
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='Details'>
				<input type='hidden' name='id' value='<%=id%>'/>
				<input type='hidden' name='invalid' value='<%=invalid%>'/>
				
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
<%}catch(Exception e){}%>
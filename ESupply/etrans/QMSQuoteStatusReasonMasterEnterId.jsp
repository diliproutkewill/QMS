<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program Name	: QMSQuoteStatusReasonMasterEnterId.jsp
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

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSQuoteStatusReasonMasterEnterId.jsp";
%>
<%
	String operation	=	request.getParameter("Operation");
	String terminalId	=	loginbean.getTerminalId();

%>
<html>
<head>
<title>StatusReason <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>
<script >

	var Win  = null;
function openSurchargeLov()
{
	var tabArray = 'STATUS_REASON'; // It Should be the column name
	var formArray = 'statusReason';
	var smode	 =	'';
	var lovWhere	=	"";
	var invalid = " and invalid='F'";
	if("Invalidate"=='<%=operation%>' || "View"=='<%=operation%>' )
		invalid="";

	Url	="<%=request.getContextPath()%>"+"/qms/ListOfValues.jsp?lovid=STATUS_REASON_LOV&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SURCHARGE&search=where  status_reason like '"+document.getElementById('statusReason').value+ "~' "+invalid ;
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=700,height=600,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
 }
 
	function chkMandatory(){
		if(document.forms[0].satusReason.value=='')
		{
				alert("Please Enter Any satusReason Id To "+'<%=operation%>')
					document.forms[0].satusReason.focus();
					return false;
		}
		else{
		document.forms[0].STATUS_REASON.value	=	document.forms[0].satusReason.value;

			return true;
		}
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
<form name="StatusReasonmaster" method="post" action="<%=request.getContextPath()%>/QMSQuoteStatusReasonController" onSubmit="return chkMandatory();">
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> StatusReason EnterId - <%=operation%> </td>
						<td align=right>QS1050521
						</td></tr>
						
						
						</table>
			</td>
			
		  </tr>
		</table>

		<table border='0' width="100%" cellpadding="0" cellspacing="0">
		
		<%	if(request.getParameter("errMsg")!=null) { %>
							<tr  class='formdata' >
								<td colspan='25' >
									<font  color=#ff0000> <%=(String)request.getParameter("errMsg")%></font>
						   </td>
					  </tr>
				<%} %>
			<tr class='formdata' width='50%'>
				<td align ='left' width='20%' class='formdata'>
				<b>Status Reason:<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="statusReason" id='statusReason' size='10' maxLength='20' class ='text' value =''  onBlur="toUpper(this)"    onkeyPress= "getDotNumberCode(this)"> 
				<input type	='button' name='satusReasonLOV' class ='input' value ="..." Onclick ="openSurchargeLov()">
				</td>
				<td width='60%' class='formdata'>
			</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" align="right">
				<input type="reset" value="Reset" name="reset" class="input">
                <input type="submit" value="Next>>" name="submit" class="input" >
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='Enter'>
				<input type='hidden' name='STATUS_REASON' value=''>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
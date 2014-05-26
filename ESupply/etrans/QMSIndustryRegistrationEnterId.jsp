
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSIndustryRegistrationEnterId.jsp
Product Name	: QMS
Module Name		: Industry Registration
Task		    : Adding/View/Modify/Delete/Invalidate Industry
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete/Invalidate" Industry information
Actor           :
Related Document: CR_DHLQMS_1002
--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSIndustryRegistrationEnterId.jsp";
%>
<%
	String operation	=	request.getParameter("Operation");	
%>
<html>
<head>
<title>CarrierRegistration Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="../html/eSupply.js"></script>
<script >

	var Win  = null;
	function mandatory()
	{
		if(industry.industryId.value=='')
		{
			alert("Pls,Select the Industry Id");
			industry.b1.focus();
			return false;
		}
		if(industry.submit!=null)
		{
			industry.submit.disabled	=true;
		}
		if(industry.reset!=null)
		{
			industry.reset.disabled	=true;
		}
		return true;
	}
	function showLOV(url)
	{    
		var	width		=	360;
		var	height		=	270;  // Changed According to new  UI Version.
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		
		var Bars		= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;

		if(Win !=null)
		{
			Win.close();
			Win = open(url,'Doc',Features);
		}		
		else
			Win = window.open(url,'Doc',Features);

		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;
	}
	function openIndustryIdLOV()
	{
		var searchStr	=	document.forms[0].industryId.value;
		var Url			= "QMSIndustryIdLOV.jsp?Operation=<%=operation%>&searchString="+searchStr;
		showLOV(Url);
	}

</script>
</head>
<body>
<form name="industry" method="post" action="QMSIndustryRegistration.jsp" onSubmit ='return mandatory()'>
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> Industry Registration - <%=operation%> </td>
						<td align=right>QS1010521
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="800" cellpadding="0" cellspacing="0">
			<tr class='formdata'>
				<td align ='left'><b>Industry Id :<font color="#FF0000">*</font></b><br>
				<input type	='text' name ="industryId" size='15' maxLength='25' class ='text' value ='' onkeyPress='specialCharFilter(this,"industryId")'  onBlur="toUpperCase()">
				<input type	='button' name='b1' class ='input' value ="..." Onclick ="openIndustryIdLOV()"">
				</td>
			</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
                <input type="submit" value="Next>>" name="submit" class="input">
				<input type="reset" value="Reset" name="reset" class="input">
				<input type='hidden' name='Operation' value='<%=operation%>'>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
<%-- 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--
% File				: QMSChargesUploadIndex.jsp
% Sub-module		: General Index Page For All Charges Uploads
% Module			: QMS
%
%
% author			: Yuvraj
% date				: 05-06-2006
% Purpose			: Acts as Initial Screen For Charge Uploads
% Modified Date	Modified By			Reason
% 
% 
--%>

<%@ page language = "java"
		 import   = "java.util.ArrayList,
					com.foursoft.esupply.common.java.ErrorMessage,
					com.foursoft.esupply.common.java.KeyValue,
					com.foursoft.esupply.common.util.ESupplyDateUtility,
					java.security.MessageDigest,
					java.security.NoSuchAlgorithmException,
					com.foursoft.esupply.common.util.Logger"
%>
<%!
      private static final  String  FILE_NAME = "QMSChargesUploadIndex.jsp" ;
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
			String			   operation		  = request.getParameter("Operation");
			String			   subOperation		  = request.getParameter("subOperation");
	
	try 
	{
			ESupplyDateUtility eSupplyDateUtility = new ESupplyDateUtility(); 
			String			   dateFormat		  =	loginbean.getUserPreferences().getDateFormat();
			String			   currentDate		  = eSupplyDateUtility.getCurrentDateString(dateFormat);
			String			   label			  = "";
			
			eSupplyDateUtility.setPattern(dateFormat);
			
			String spId	= "";
			//System.out.println(FILE_NAME+operation+type);
			spId=(String)loginbean.generateUniqueId(FILE_NAME,operation+subOperation);
			if((spId==null)||(spId!=null && spId.equalsIgnoreCase("null"))) spId="";
			
			if(subOperation.equalsIgnoreCase("chargeDesc")) 
				label	=	"Charge Description UpLoad";
			else if(subOperation.equalsIgnoreCase("chargeGroup"))
				label	=	"Charges Grouping UpLoad";

%>
<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="JavaScript" src='html/eSupply.js'></script>

<script language="javascript">

function setFlag(Obj)
{
	if(Obj.checked==true){
		document.frm.errorMode.value = 'Y';
	}else{
		document.frm.errorMode.value = 'N';
	}
}

function validate()
{
	if (document.frm.fileName.value == '')
	{
		alert('Please enter the File name to be uploaded');
		document.frm.fileName.focus();
		return false;
	}
    var fName=document.frm.fileName.value;		
	var c=".csv";   
	
    if(fName.length!=0 && fName.indexOf(c)==-1)
	{
		alert("This is Not a Valid File Type");
		document.frm.fileName.focus();
		return false;
	}
	document.frm.process.value = document.frm.addMod[document.frm.addMod.selectedIndex].value;

	document.frm.action='QMSChargesUploadController?Operation=<%=operation%>&subOperation=<%=subOperation%>&process='+document.frm.process.value+'&errorMode='+document.frm.errorMode.value;
	document.frm.method="post";
	document.frm.submit();
}
function openFormatFile(type)
{   
     Features = 'directories=no,location=no,menubar=no,status=yes,titlebar=no,scrollbars=yes,resizable=yes';
	 if(type=='chargeDesc')
		 Url='html/ChargeDescriptionUpload.html';
	 else
		Url='html/ChargeGroupUpload.html';
	 Win=open(Url,'Doc',Features); 

}
</script>
</head>
<body   bgcolor="#FFFFFF" onLoad="document.frm.fileName.focus();">
<form name = "frm"  ENCTYPE="multipart/form-data" >
<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
<tr>
	<td>
	<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td><%=label%></td>
			<td align=right><%=spId%></td></tr></table></td>
		  </tr></table>
				<table width="100%" cellpadding="4" cellspacing="1">
					<tr class="formdata" vAlign="top">
						<td class="formdata">Terminal Id :<br> <%= loginbean.getTerminalId()%></td>
						<td class="formdata">User Id: <br> <%= loginbean.getUserId()%></td>
						<td class="formdata" colspan="2">Date :<br> <%= currentDate%>&nbsp;(<%= dateFormat%>)</td>
					</tr>
					<tr valign="top" class="formdata">
					<td >File to UpLoad :<font color="red">*</font><br>
						<input type="file" name="fileName" size=40 class='text' onKeyPress="return getSpecialCode()" onBlur="specialCharFilter(this,'File to Upload');" >
					</td>
					<td colspan="2">Process :<br>
						<select name = "addMod" class='select'> 
							<option  value= "ADD" selected>Add</option>
							<option  value= "MODIFY" >Modify</option>
						</select>
						</td>
						<td colspan="1">Export Exceptions To Excel Format:
							<input type="checkbox" name="errorCheck" onClick='setFlag(this)'>
						</td>
					</tr>		
					<tr valign="top" class='formdata'> 
						<td colspan="4"><a href="javascript:openFormatFile('<%=subOperation%>')">Sample File</a>
						</td>
			   		</tr>
					<tr valign="top" class='denotes'>
						<td colspan='3' align="left" >
							Note:<br><font color=red>*&nbsp;&nbsp;</font>Denotes Mandatory Fields.<br>
						</td>
						<td align="right">
							<input type='button' name="next1" value="Next>>" class="input" onclick='validate();'>
							<input type=reset value=Reset class="input">
							<input type="hidden" name="Operation" value='<%=operation%>'>
							<input type="hidden"  name="subOperation" value='<%=subOperation%>'>
							<input type="hidden" name="process" value="">
							<input type="hidden" value="N" name="errorMode">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form>
</body>
<%
	}
	catch(Exception e)
	{
		 Logger.error(FILE_NAME,"Error in QMSChargesUploadIndex.jsp file : "+e.toString());
		 e.printStackTrace();
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("An Error has Occurred While Loading the Page. Please Try Again.","QMSChargesUploadController?Operation="+operation+"&subOperation="+subOperation);
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
	
%>
		 <jsp:forward page="../ESupplyErrorPage.jsp" /> 
<%
 	}
%>
</html>




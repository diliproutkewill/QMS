<%-- 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--
% File				: QMSZoneCodeMasterUploadIndex.jsp
% Sub-module		: Upload Index Page For Zone Code Master
% Module			: QMS
%
%
% author			: RamaKrishna Y
% date				: 14-07-2005
% Purpose			: Acts Has Initial Screen For Master Uploads
% Modified Date	Modified By			Reason
% 
% 
--%>

<%@ page language = "java"
		 import   = "java.util.ArrayList,
					com.foursoft.esupply.common.java.ErrorMessage,
					com.foursoft.esupply.common.java.KeyValue,
					com.foursoft.esupply.common.util.ESupplyDateUtility,
					org.apache.log4j.Logger"
%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
  
	String FILENAME="QMSZoneCodeMasterUploadIndex.jsp";
  logger  = Logger.getLogger(FILENAME);
	try {
		if(loginbean.getTerminalId() == null){
%>
		<jsp:forward page="../ESupplyLogin.jsp" />
<%
		}else{   
			String operation = request.getParameter("Operation");
				
%>
<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="JavaScript" src='../html/eSupply.js'></script>

<script language="javascript">

function setFlag(Obj){
	if(Obj.checked==true){
		document.frm.errorMode.value = 'Y';
	}else{
		document.frm.errorMode.value = 'N';
	}
}

function validate(){
	if (document.frm.fileName.value == ''){
		alert('Please enter the File name to be uploaded');
		document.frm.fileName.focus();
		return false;
	}
    var fName=document.frm.fileName.value;		
	var c=".csv";   
	
    if(fName != "" && fName.indexOf(c)==-1){
		alert("Check the file type");
		document.frm.fileName.focus();
		return false;
	}
	document.frm.Process.value = document.frm.addMod.value;//[document.frm.addMod.selectedIndex]
	//document.frm.next1.disabled = true;	// modified by VALKSHMI for issue 168080 on 20/04/09
	 document.frm.next1.disabled = false; //Added by subrahmanyam for id: 218119
	window.document.frm.action="QMSZoneCodeMasterUploadProcess.jsp";
	
	window.document.frm.method="post";
	window.document.frm.submit();
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
		
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,?"+'"';		
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
function openFormatFile()
{   
     Features = 'directories=no,location=no,menubar=no,status=yes,titlebar=no,scrollbars=yes,resizable=yes';
	 Url='html/ZoneCodeMasterFormat.html';
	 Win=open(Url,'Doc',Features);
}
</script>
</head>
<body  leftmargin="5" topmargin="0" marginwidth="0" marginheight="0" onLoad="document.frm.fileName.focus();">
<form method="post" name = "frm"  ENCTYPE="multipart/form-data" >
	<table width="800" border="0" cellspacing="0" cellpadding="0">
		<tr valign="top" bgcolor="#FFFFFF"> 
			<td>
				<table width="800" cellpadding="4" cellspacing="1">
					<tr valign="top" class="formlabel"> 
						<td height="20"  width="800" colspan="2"> 
							<b>Zone Code Master &nbsp; UpLoad </b>
								&nbsp;
						</td>
						<td height="20"  align="right"> 
							<b>QS1000141</b>
						</td>
					</tr>
				</table>
				<table width="800" cellpadding="4" cellspacing="1">
					
					<tr valign="top" class="formdata"> 
						<td >
							<font face=Verdana size=2>
								File to UpLoad :
							</font>
							<font color="red">
								*
							</font>
								<br>
								<input type="file" name="fileName" size=40 class='text' onKeyPress="return getSpecialCode()"  ><!-- onBlur="specialCharFilter(this,'File to Upload');" -->
							</font>
						</td>
						<td   width="400" colspan="2">
						<input type='hidden' name='addMod' value='ADD'>
							 <!-- <font face="Verdana" size="2">
								Process :
							</font><br>
							<select name = "addMod" class='select'> 
								<option  value= "ADD" selected>ADD</option>		
								<option  value= "MODIFY" >MODIFY</option>
							</select> --> 
						</td>
						<td colspan="1" width="20%">
							<font face="Verdana" size="2">
								Export Exceptions To Excel Format:
							</font>
							<input type="checkbox" name="errorCheck" onClick='setFlag(this)'> 
						</td>
					</tr>		
					 <tr valign="top" class='formdata'> 
						<td colspan="4">							
								<a href="javascript:openFormatFile()">Sample File </a>				
						</td>
			   		</tr> 
					<tr valign="top" > 
						<td colspan="3" >
							<font face="verdana" size=1 color="#FF0000">
								*
							</font> 
							<font face="verdana" size=1>
								Denotes Mandatory
							</font>
							
							<br>
							<font face="Verdana" size="1">
								Note :
							</font>
							<br>
							<font face="Verdana" size="1">
								
							</font>							
						</td>
						<td   align="right">
							<input type='button' name="next1" value="Next>>" class="input"  onClick="validate();" >&nbsp;&nbsp;&nbsp;<input type=reset value=Reset class="input">
							<input type="hidden" value="<%=operation%>" name="Operation">
							<input type="hidden" value="<%//=type%>" name="type">
							<input type="hidden" value="" name="Process">
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
	}catch(Exception e){
		//Logger.error(FILENAME,"Error in ETCUploadIndex.jsp file : "+e.toString());
    logger.error(FILENAME+"Error in ETCUploadIndex.jsp file : "+e.toString());
		 
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("Error while accessing the page","ETCUploadIndex.jsp");
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
	
%>
		 <jsp:forward page="../ESupplyErrorPage.jsp" /> 
<%
 	}	   
%>
</html>


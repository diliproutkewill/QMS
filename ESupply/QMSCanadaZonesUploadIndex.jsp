<%--
% File				: QMSCanadaZonesUploadIndex.jsp
% Sub-module		: Upload Index Page For Canada Zone Code Master
% Module			: QMS
% author			: Yuvraj Waghray
% date				: 19-10-2006
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
  
	String FILENAME="QMSCanadaZonesUploadIndex.jsp";
	logger  = Logger.getLogger(FILENAME);

	
	try 
	{
		String operation = request.getParameter("Operation");
		ESupplyDateUtility eSupplyDateUtility = new ESupplyDateUtility(); 
		String			   dateFormat		  =	loginbean.getUserPreferences().getDateFormat();
		String			   currentDate		  = eSupplyDateUtility.getCurrentDateString(dateFormat);
		String			   label			  = "";
		
		eSupplyDateUtility.setPattern(dateFormat);
				
%>
<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">

<script language="javascript">

function setFlag(Obj){
	if(Obj.checked==true){
		document.frm.errorMode.value = 'Y';
	}else{
		document.frm.errorMode.value = 'N';
	}
}

function validate()
{
	try
	{
		if (document.frm.fileName.value == ''){
			alert('Please enter the File name to be uploaded');
			document.frm.fileName.focus();
			return false;
		}
		var fName=document.frm.fileName.value;		
		var c=".csv";   
		
		if(fName != "" && fName.indexOf(c)==-1){
			alert("Please Check the file type");
			document.frm.fileName.focus();
			return false;
		}
		//document.frm.next1.disabled = true;
		
		window.document.frm.action="QMSCanadaZonesController?Operation=<%=operation%>&subOperation="+document.forms[0].subOperation.value+'&errorMode='+document.frm.errorMode.value;
		
		window.document.frm.method="post";
		window.document.frm.submit();
	}
	catch(err)
	{
		if(err.description.indexOf("denied")!=-1)
		{
			alert("Please Enter a Valid File Name or Click on the Browse Button.");
		}
		else
		{
			alert(err.description);
			return true;
		}
	}
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

function openFormatFile()
{   
     Features = 'directories=no,location=no,menubar=no,status=yes,titlebar=no,scrollbars=yes,resizable=yes';
	 Url='html/CanadaZoneCodeUploadFormat.html';
	 Win=open(Url,'Doc',Features);
	 Win.resizeTo(800,600);
}
</script>
</head>
<body  leftmargin="5" topmargin="0" marginwidth="0" marginheight="0" onLoad="document.frm.fileName.focus();">
<form method="post" name = "frm"  ENCTYPE="multipart/form-data" >
<table width="100%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>
		<tr class="formlabel">
			<td >Canada Zone Code Master - Upload</td><td align='right'>QS1040511</td>
		</tr>
</table>
	<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
					<tr class="formdata" vAlign="top">
						<td>Terminal Id :<br> <%= loginbean.getTerminalId()%></td>
						<td>User Id: <br> <%= loginbean.getUserId()%></td>
						<td colspan="2">Date :<br> <%= currentDate%>&nbsp;(<%= dateFormat%>)</td>
					</tr>
					<tr valign="top" class="formdata">
					<td colspan="2">File to UpLoad :<font color="red">*</font><br>
						<input type="file" name="fileName" size=40 class='text' onKeyPress="return getSpecialCode()" >
					</td>
					<td colspan="2">Export Exceptions To Excel Format:
							<input type="checkbox" name="errorCheck" onClick='setFlag(this)'>
						</td>
					</tr>		
					<tr valign="top" class='formdata'> 
						<td colspan="4"><a href="javascript:openFormatFile()">Sample File</a>
						</td>
			   		</tr>
					<tr valign="top" class='denotes'>
						<td colspan='3' align="left" >
							Note:<br><font color=red>*&nbsp;&nbsp;</font>Denotes Mandatory Fields.<br>
						</td>
						<td align="right">
							<input type='button' name="next1" value="Next>>" class="input"  onClick="validate();" >&nbsp;&nbsp;&nbsp;<input type=reset value=Reset class="input">
							<input type="hidden" value="<%=operation%>" name="Operation">
							<input type="hidden" value="process" name="subOperation">
							<input type="hidden" value="<%//=type%>" name="type">
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
		//}
	}catch(Exception e){
		//Logger.error(FILENAME,"Error in ETCUploadIndex.jsp file : "+e.toString());
    logger.error(FILENAME+"Error in QMSCanadaZonesUploadIndex file : "+e.toString());
		 
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("Error while accessing the page","QMSCanadaZonesUploadIndex.jsp");
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
	
%>
		 <jsp:forward page="ESupplyErrorPage.jsp" /> 
<%
 	}	   
%>
</html>


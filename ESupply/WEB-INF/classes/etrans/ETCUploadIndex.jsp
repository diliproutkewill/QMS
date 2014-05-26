<%-- 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--
% File				: ETCUploadIndex.jsp
% Sub-module		: General Index Page For All Uploads
% Module			: QMS
%
%
% author			: Ravi Kumar
% date				: 06-07-2005
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
	String FILENAME="ETCUploadIndex.jsp";
  logger  = Logger.getLogger(FILENAME);	
	try {
		if(loginbean.getTerminalId() == null){
%>
		<jsp:forward page="../ESupplyLogin.jsp" />
<%
		}else{   


			ESupplyDateUtility eSupplyDateUtility = null; 
			eSupplyDateUtility 	=	new ESupplyDateUtility();
			String dateFormat	=loginbean.getUserPreferences().getDateFormat();
			eSupplyDateUtility.setPattern(dateFormat);
			String currentDate=eSupplyDateUtility.getCurrentDateString(dateFormat);
			String operation = request.getParameter("Operation");
			String type = request.getParameter("Type");
			String spId	= "";
			//System.out.println(FILENAME+operation+type);
			spId=(String)loginbean.generateUniqueId(FILENAME,operation+type);
			if((spId==null)||(spId.equalsIgnoreCase("null"))) spId="";	 	
%>
<html>
<head>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
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
	document.frm.Process.value = document.frm.addMod[document.frm.addMod.selectedIndex].value;
	document.frm.next1.disabled = true;	
	if('<%=type.toUpperCase()%>' == 'COUNTRY'){
		window.document.frm.action="ETCCountryUploadProcess.jsp";
	}else if('<%=type.toUpperCase()%>' == 'LOCATION'){
		window.document.frm.action="ETCLocationUploadProcess.jsp";
	
	}else if('<%=type.toUpperCase()%>' == 'COMMODITY'){
		window.document.frm.action="ETCCommodityUploadProcess.jsp";
	
	}else if('<%=type.toUpperCase()%>' == 'PORT'){
		window.document.frm.action="ETCPortUploadProcess.jsp";
	
	}
	window.document.frm.method="post";
	window.document.frm.submit();
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
							<%if(type.equalsIgnoreCase("COUNTRY")) { %>Country &nbsp; UpLoad 
								&nbsp;<% } else if(type.equalsIgnoreCase("LOCATION")){ %> Location &nbsp; UpLoad <%}else if(type.equalsIgnoreCase("Commodity")){ %> Commodity &nbsp; UpLoad <%}else if(type.equalsIgnoreCase("Port")){ %> Port &nbsp; UpLoad <%}%>
						</td>
						<td height="20"  align="right"> 
							<%= spId%>
						</td>
					</tr>
				</table>
				<table width="800" cellpadding="4" cellspacing="1">
					<tr >
						<td colspan="3" width="800">&nbsp;</td>
 					</tr>
					<tr class="formdata" vAlign="top">
						<td class="formdata" width="261" bgcolor="#FDD01C">
							<font face="Verdana" size="2">
								Terminal Id :<br> <%= loginbean.getTerminalId()%>
							</font>
						</td>
						<td class="formdata" width="204" bgcolor="#FDD01C">
							<font face="Verdana" size="2">
								User Id: <br> <%= loginbean.getESupplyLoginId()%>
							</font>
						</td>
						<td class="formdata" width="323" bgcolor="#FDD01C" colspan="2">
							<font face="Verdana" size="2">
								Date :<br> <%= currentDate%>&nbsp;(<%= dateFormat%>)
							</font>
						</td>
					</tr>
					<tr valign="top" class="formdata"> 
						<td >
							<font face=Verdana size=2>
								File to UpLoad :
							</font>
							<font color="red">
								*
							</font>
								<br>
								<input type="file" name="fileName" size=40 class='text' onKeyPress="return getSpecialCode()" onBlur="specialCharFilter(this,'File to Upload');" >
							</font>
						</td>
						<td   width="400" colspan="2">
							<font face="Verdana" size="2">
								Process :
							</font><br>
							<select name = "addMod" class='select'> 
								<option  value= "ADD" selected>ADD</option>
								<option  value= "MODIFY" >MODIFY</option>
							</select>
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
							<%if(type.equalsIgnoreCase("COUNTRY")){%>
								<a href="../html/CountryFormat.html" target="_blank">Sample File 
							</a>
							<%}else if(type.equalsIgnoreCase("Location")){%>
								<a href="../html/LocationFormat.htm" target="_blank">Sample File
								</a>
							<%}else if(type.equalsIgnoreCase("Commodity")){%>
								<a href="../html/CommodityFormat.html" target="_blank">Sample File
								</a>
							<%}else if(type.equalsIgnoreCase("Port")){%>
								<a href="../html/PortFormat.html" target="_blank">Sample File
								</a>
							<%}%>
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
							<%if(type.equalsIgnoreCase("Country")){%>
							<br>
							<font face="Verdana" size="1">
								Note :
							</font>
							<br>
							<font face="Verdana" size="1">
								1)Country Ids to conform to IATA rule No 1.3.1<br>
								2)Currency Ids to conform to IATA rule No 5.7.1
							</font>
							<% }else if(type.equalsIgnoreCase("Location")){ %>
								<br>
								<font face="Verdana" size="1">
									Note :
								</font>
								<br>
								<font face="Verdana" size="1">
									1)Location Ids to conform to IATA rule No 1.2.3
								</font>
							<% }else if(type.equalsIgnoreCase("Commodity")){ %>
								<br>
								<font face="Verdana" size="1">
									Note :
								</font>
								<br>
								<font face="Verdana" size="1">  
									1)Commodity Ids to conform to Master Item Numbering and Description System of IATA
								</font>
							<% } %>
						</td>
						<td   align="right">
							<input type='button' name="next1" value="Next>>" class="input"  onClick="validate();" >&nbsp;&nbsp;&nbsp;<input type=reset value=Reset class="input">
							<input type="hidden" value="<%=operation%>" name="Operation">
							<input type="hidden" value="<%=type%>" name="type">
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


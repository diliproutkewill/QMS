<!-- 
File				: BuyRatesUpLoad.jsp
Sub-module		    : Upload Index Page For Buy Rates
Module			    : QMS
Author              : RamaKrishna Y.
date				: 14-07-2005

Modified Date	Modified By			Reason
-->

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
  public static final String FILE_NAME  =  "BuyRatesUpLoad.jsp"; %>

<%
    logger  = Logger.getLogger(FILE_NAME);
  try
  {
	  if(loginbean.getTerminalId()==null)
	  {
%>
      
<%
	  }
	  else
	  {
		  String  operation         =   request.getParameter("Operation");
                  //Added by ashlesh for DHL CR 154393 on 23/01/2009
		   String  subOperation      =   request.getParameter("subOperation");
		   String  actionValue      =null;		  
		   if(subOperation.equals("upLoadCustomer")){
              actionValue="QMSBuyRatesUploadController?Operation=UpLoad&subOperation=UpLoadCustomerDetails";
		   }
        //@@Added by Kameswari for the WPBN issue-171210
       else if(subOperation.equalsIgnoreCase("uploadDelete")){
              actionValue="QMSBuyRatesUploadController?Operation=UpLoad&subOperation=UploadDeleteDetails";
			
		   }
		   else{
              actionValue="QMSBuyRatesUploadController?Operation=UpLoad&subOperation=UpLoad";
		   }
%>
<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language='javaScript' src='html/eSupply.js'>
</script>
<script language="javascript">
function setFlag(Obj){
	if(Obj.checked==true){
		document.frm.errorMode.value = 'Y';
	}else{
		document.frm.errorMode.value = 'N';
	}
}
function fileType()
		  {
	var csv=".csv";
	var exl =".xls";
	var fileTypes="";
	var subops='<%=subOperation%>';
	var url ="";
  var fName=document.frm.fileName.value;		
  if (document.frm.fileName.value == ''){
		alert('Please enter the File name to be uploaded');
		document.frm.fileName.focus();
		return false;
	}
  if(fName != "" && fName.indexOf(csv)!=-1){
		document.frm.endsWith.value="CSV";
		fileTypes="CSV";
	}
	else if(fName != "" && fName.indexOf(exl)!=-1){
         document.frm.endsWith.value="EXCEL";
		 		fileTypes="EXCEL";
	}
	else
	{
		alert("Check the File Type");
		document.frm.fileName.focus();
		return false;
	}
	if(subops=="upLoadCustomer")
	url='QMSBuyRatesUploadController?Operation=UpLoad&subOperation=UpLoadCustomerDetails';
	else if(subops=="uploadDelete"){
		document.frm.errorMode.value = 'Y';//added by subrahmanyam
	   url='QMSBuyRatesUploadController?Operation=UpLoad&subOperation=UploadDeleteDetails';
	}
	else
			url='QMSBuyRatesUploadController?Operation=UpLoad&subOperation=UpLoad';
	document.forms[0].action=url+'&endsWith='+fileTypes;
	//document.forms[0].action='QMSBuyRatesUploadController?Operation=UpLoad&subOperation=UpLoad&endsWith='+fileTypes;
    document.forms[0].submit;
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

	//document.frm.Process.value = document.frm.addMod[document.frm.addMod.selectedIndex].value;
	document.frm.next1.disabled = true;		
	//document.frm.action="<%=request.getContextPath()%>/QMSBuyRatesUploadController";	
	//document.frm.method="post";
	//document.frm.submit();
	return true;
}
</script>
</head>
<body onLoad='document.frm.fileName.focus();'>
<!-- Modified by ashlesh for DHL CR 154393 on 23/01/2009        -->
<form name='frm'  method="post"  onsubmit='fileType()'  ENCTYPE="multipart/form-data" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
		<tr valign="top" bgcolor="#FFFFFF"> 
			<td>
				<table width="800" cellpadding="4" cellspacing="1">
					<tr valign="top" class="formlabel"> 
						<td height="20"  width="800" colspan="2"> 
                                                 <!-- Modified by ashlesh for DHL CR 154393 on 23/01/2009 -->
						    <% if(subOperation.equals("upLoadCustomer")){%>
	                            <b>Customer&nbsp;UpLoad</b>
                             <%}else if(subOperation.equals("uploadDelete")){%>
							<b>Rates&nbsp;Delete</b>
							<%}else {%>
						    <b>Buy&nbsp;Rates&nbsp;Master&nbsp;UpLoad</b>
						    <%}%>
						</td>
						<td height="20"  align="right"> 
							<b>QS1060141</b>
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
								<input type="file" name="fileName" size=40 class='text'  ><!-- onKeyPress="return getSpecialCode()" onBlur="specialCharFilter(this,'File to Upload');" -->
							</font>
						</td>
						<td   width="400" colspan="2">
							 <!-- <font face="Verdana" size="2">
								Process :
							</font><br>
							<select name = "addMod" class='select'> 
								<option  value= "ADD" selected>ADD</option>		
								<option  value= "MODIFY" >MODIFY</option>
							</select>  -->
						</td>
						<td colspan="1" width="20%">
							<font face="Verdana" size="2">
								Export Exceptions To Excel Format:
							</font>
							<input type="checkbox" name="errorCheck" onClick='setFlag(this)'> 
						</td>
					</tr>	
					<%if("UpLoad".equalsIgnoreCase(subOperation))
		      {%>
					 <tr valign="top" class='formdata'> 
						<td colspan="4">
						
								<a href="<%=request.getContextPath()%>/html/BuyRatesFormat.html" target="_blank">Sample File </a>				
						</td>
			   		</tr> 
					<%}%>
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
							<input type='submit' name="next1" value="Next>>" class="input"  >&nbsp;&nbsp;&nbsp;<input type=reset value=Reset class="input">
							<input type="hidden" value="<%=operation%>" name="Operation">							
                                                        <!-- Modified by ashlesh for DHL CR 154393 on 23/01/2009 -->	
							<% if(subOperation.equals("upLoadCustomer")){%>
                               <input type="hidden" value="UpLoadCustomerDetails" name="subOperation">
							<%}
               //@@Added by Kameswari for the WPBN issue-171210
							else if(subOperation.equalsIgnoreCase("uploadDelete")){%>
                               <input type="hidden" value="UploadDeleteDetails" name="subOperation">
							<%}
							 else {%>
   							<input type="hidden" value="UpLoad" name="subOperation">
							<%}%>
							<input type="hidden" value="" name="Process">
							<input type="hidden" value="N" name="errorMode">
							<input type="hidden"  name="endsWith">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form>
</body>
</html>
<%
	  }
  }
  catch(Exception e)
  {
	  e.printStackTrace();
	  //Logger.error(FILE_NAME,"Error while accessing this page"+e.toString());
    logger.error(FILE_NAME+"Error while accessing this page"+e.toString());
  }
%>

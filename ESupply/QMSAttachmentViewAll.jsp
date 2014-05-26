<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSAttachmentViewAll.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: ViewAll/Invalidate
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61289

--%>
<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%! private static Logger logger = null;
    public final static String   FILE_NAME = "QMSAttachmentViewAll.jsp";%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
  String  operation  = null;
  try
  {
	  operation  = (String)request.getParameter("Operation");
  %>

<html>
<head>
<title>QMSAttachmentViewAll</title>
<link rel="stylesheet" href="./ESFoursoft_css.jsp">
<script language='javascript'>

	function selectAll()
	{
	  if(document.forms[0].select.checked)
	  {
	    for(var i=0;i<document.forms[0].elements.length;i++)
	    {
	     if(document.forms[0].elements[i].type=="checkbox")
	     {
	        document.forms[0].elements[i].checked=true;
	     }
	    }
	  } 
	  else
	  {
	    for(var i=0;i<document.forms[0].elements.length;i++)
	    {
	     if(document.forms[0].elements[i].type=="checkbox")
	      {
	         document.forms[0].elements[i].checked=false;
	      }
	    }
	    document.forms[0].attachmentId.checked=true;//@@ Added by subrahmanyam for the pbn id:203350 
	  } 
	}
</script>
</head>
<body>
 <form method="post" action="QMSAttachmentController?Operation=<%=operation%>">
	<table width="103%" border="0" cellspacing="0" cellpadding="0" >
 		<tr bgcolor="#FFFFFF"> 
    		<td>
				<table width="100%" border="0" cellspacing="1" cellpadding="4" >
					<tr  class='formlabel'> 
						<td >Quote Attachment Master -<%=operation%></td>
						<td align="right"><%=loginbean.generateUniqueId("QMSAttachmentViewAll.jsp",operation)%></td>
					</tr>
					<tr  valign="top" class='formheader'> 
						<td colspan='2'>Select the fields you  wish to View:
			            </td>			
					</tr>
				</table>
	           <table border="0" width="100%" cellspacing="0" cellpadding="0" >
					<tr class='formdata'> 
						<td width="50%" align="right">
							<input type="checkbox" name="attachmentId" value="0" checked disabled>
						</td>
						<td align="left">Attachment Id
						</td>
					</tr>
   					<tr class='formdata'>
						<td align="right">
							<input type="checkbox" name="terminalId" value="0">
						</td>
						<td  align="left" >Terminal Id
						</td>
					</tr>
					<tr class='formdata'>
						<td  align="right">
							<input type="checkbox" name="fromCountry" value="0">
						</td>
						<td align="left">From Country
						</td>
					</tr>
					<tr class='formdata'>
						<td  align="right" >
							<input type="checkbox" name="fromLocation" value="0">
						</td>
						<td align="left">From Location</td>
					</tr>
					<tr class='formdata'>
						<td align="right" >
							<input type="checkbox" name="toCountry" value="0">
						</td>
						<td align="left">To Country</td>
					</tr>
					<tr class='formdata'>
						<td  align="right" >
							<input type="checkbox" name="toLocation" value="0">
						</td>
						<td align="left">To Location</td>
					</tr>
					<tr class='formdata'>
						<td  align="right" >
							<input type="checkbox" name="carrierId" value="0">
						</td>
						<td align="left">Carrier </td>
					</tr>
					<tr class='formdata'>
						<td  align="right" >
							<input type="checkbox" name="serviceLevelId" value="0">
						</td>
						<td align="left">Service Level </td>
					</tr>
					<tr class='formdata'>
						<td  align="right" >
							<input type="checkbox" name="industryId" value="0">
						</td>
						<td align="left">Industry</td>
					</tr>
					<tr class='formdata'>
						<td  align="right" >
							<input type="checkbox" name="fileName" value="0">
						</td>
						<td align="left">File</td>
					</tr>
					<tr class='formdata'>
						<td  align="right">
							<input type="checkbox" name="select" value="0" onClick="selectAll()">
						<td align="left">Select All
						</td>
					</tr>
				</table>
  		       <table border="0" width="100%" cellpadding="4" cellspacing="1" height="39">
					<tr class="formdata"> 
						<td valign="top" bgcolor="white" align="right"> 
							<input name=B1 type=submit value="Submit" class='input'>
							<input type="reset" value="Reset" name="reset" class='input'>
							<input type="hidden" value="Next" name="subOperation">
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
	catch(Exception e)
	{
		e.printStackTrace();
	    logger.error(FILE_NAME+"Error in QMSAttachmentViewAll.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSAttachmentViewAll.jsp");
		
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>

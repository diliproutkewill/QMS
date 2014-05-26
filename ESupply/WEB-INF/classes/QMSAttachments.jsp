<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSAttachments.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: Modify 
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61289

--%>
<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 com.qms.setup.java.QMSAttachmentDOB,
				 com.qms.setup.java.QMSAttachmentDetailDOB,
				 com.qms.setup.java.QMSAttachmentFileDOB,
				  java.util.ArrayList"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%! private static Logger logger = null;
    public final static String   FILE_NAME = "QMSAttachments.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME); 
	ArrayList			        filesList	 =  null;
	ArrayList			        deletefilesList	 =  null;
	String                      attachmentId = null;
	String                      attachment = null;
	QMSAttachmentFileDOB        fileDOB      = null;    
	try
	{
		filesList    = (ArrayList)request.getAttribute("fileslist");
		attachmentId = (String)request.getParameter("attachmentId");
		logger.info("attachmentId"+attachmentId);
		// attachment =attachmentId.replace('&',',');
		 logger.info("attachment"+attachment);
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Manage Attachments</title> 
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="javascript">
function attachFile()
{
	document.forms[0].method='POST';
  	document.forms[0].action="QMSAttachmentController?Operation=manageAttachment&subOperation=attachFile&attachmentId=<%=attachmentId%>";
	document.forms[0].submit();
}
function deleteFile()
{
	var count=0;
	var uniqueId="";
	<%
	for(int j=0;j<filesList.size();j++)
	 {
		 fileDOB	= (QMSAttachmentFileDOB)filesList.get(j);
	%>
		if(document.forms[0].delete<%=j%>.checked==true)
		 {
	        if(uniqueId=="")
				uniqueId= uniqueId+<%=fileDOB.getId()%> ;
			else
               uniqueId  = uniqueId+","+<%=fileDOB.getId()%>;
		   count++;
		 
		 }	
	    <%}%>
	   if(count==0)
		   alert("Please select atleast one file to remove");
		else if(count==<%=filesList.size()%>)
		   alert("Cannot delete all the files in the list");
		else 
		 {
			
		document.forms[0].method='POST';
		document.forms[0].action="QMSAttachmentController?Operation=manageAttachment&subOperation=deleteFile&attachmentId=<%=attachmentId%>&uniqueId="+uniqueId;
		   document.forms[0].submit();
		 }
}
function Close()
{
    window.close();
	window.opener.location.reload(true);
}
</script>
</head>
<body>
<form  action = "" method="GET" ENCTYPE="multipart/form-data" >
<table border=0 cellpadding=1 cellspacing=0 width="101%">
	<tr class='formlabel' valign=top> 
		<td>
			<table width="101%"  border=0 cellpadding=1 cellspacing=0 >
				<tr class='formlabel'>
					<td>Manage Attachments </td>
					<td align=right><%=loginbean.generateUniqueId("QMSAttachments.jsp","")%></td>
					</tr>
			</table>
		</td>
	</tr>
</table>
<table border=0 cellpadding=1 cellspacing=0 width="101%">
	<tr class='formdata'>
		<td>
			<input type="file" class="text" name="file" size="55">
			<input type="button" class="input" name="attachfile" value="Attach" onClick="attachFile()">
		</td>
	</tr>
	<tr class="formheader">
		<td >Current Files in the list</td>
	</tr>
	<tr class="formdata">
		<td  align="right">
			<input type="button" class="input" name="deletefile" value="Remove" onClick="deleteFile()">
		</td>
	</tr>
	<%
	for(int j=0;j<filesList.size();j++)
	 {
		 fileDOB	= (QMSAttachmentFileDOB)filesList.get(j);
	%>
	<tr class="formdata">
		<td colspan="2">
			<input type="checkbox"  value="0" name="delete<%=j%>">
			<a href="QMSAttachmentController?Operation=viewFile&uniqueId=<%=fileDOB.getId()%>" target="_blank"><%=(String)fileDOB.getFileName()%></a>
		</td>
	</tr>
	<%}%>
	<tr class="formdata">
		<td align="right">
			<input type="button" class="input" name="close" value="Close" onClick="Close()">
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
	    logger.error(FILE_NAME+"Error in QMSAttachments.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSAttachments.jsp");
		
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>

<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSAttachmentViewAllDtl.jsp
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
				 com.qms.setup.java.QMSAttachmentDOB,
				 com.qms.setup.java.QMSAttachmentDetailDOB,
				 com.qms.setup.java.QMSAttachmentFileDOB,
				  java.util.ArrayList"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%! private static Logger logger = null;
    public final static String   FILE_NAME = "QMSAttachmentViewAllDtl.jsp";%>

<%
	logger  = Logger.getLogger(FILE_NAME);	
	String                  operation     = null;
	ArrayList               stringList    = null;
	ArrayList			    viewList	  = null;
	ArrayList               fieldsList    = null;
	ArrayList			    filesList	  = null;
	QMSAttachmentDOB        attachmentDOB  =  null;
	QMSAttachmentDetailDOB  detailDOB     =  null;
	QMSAttachmentFileDOB     fileDOB      =  null;
 
	try
	{
		operation   = (String)request.getParameter("Operation");
		viewList    =  (ArrayList)request.getAttribute("viewList");
		stringList  =  (ArrayList)request.getAttribute("stringList");
		session.setAttribute("viewList",viewList);
	%>
<html>
<head>
<title>QMSAttachmentViewAllDtl.jsp</title>
<link rel="stylesheet" href="./ESFoursoft_css.jsp">
<script language='javascript'>
function previousePage()
{
	document.forms[0].action="QMSAttachmentViewAll.jsp?Operation=ViewAll";	
	document.forms[0].submit();
}
</script>
<body>
 <form method="post" action="QMSAttachmentController?Operation=<%=operation%>&subOperation=Submit">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" >
 		<tr bgcolor="#FFFFFF"> 
    		<td>
				<table width="100%" border="0" cellspacing="1" cellpadding="4" >
					<tr  class='formlabel'> 
						<td >Quote Attachment Master -<%=operation%></td>
						<td align="right"><%=loginbean.generateUniqueId("QMSAttachmentViewAll.jsp",operation)%></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="1" cellpadding="4" >
				<tr  class='formheader'> 
				<%for(int i=0;i<stringList.size();i++)
				{
		            if(i!=stringList.size()-1)
					{%>
						<td><%=stringList.get(i)%></td>
					<%}
					else
					{
						if("Invalidate".equalsIgnoreCase(operation))
						 {%>
							<td><%=operation%></td>
						<%}
					}}
				%>
				</tr>
				<%for(int j=0;j<viewList.size();j++)
		         {
					attachmentDOB = (QMSAttachmentDOB)viewList.get(j);
					fieldsList    = (ArrayList)attachmentDOB.getQmsAttachmentDetailDOBList();
					detailDOB     = (QMSAttachmentDetailDOB)fieldsList.get(j);
					filesList     = (ArrayList)attachmentDOB.getQmsAttachmentFileDOBList();
					
					
					for(int k=0;k<filesList.size();k++)
					 {
						fileDOB       = (QMSAttachmentFileDOB)filesList.get(k);
					if(k==0)
					 {%>
					<tr class="formdata">
					<%for(int m=0;m<stringList.size();m++)
					{
						if("Attachment ID".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><%=(String)attachmentDOB.getAttachmentId()%></td>
			            <%}  
						if("Terminal ID".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><%=(String)attachmentDOB.getTerminalId()%></td>
			            <%}  
						if("From Country".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><input type="text" class="text" name="fromCountry" value='<%=((String)detailDOB.getFromCountry()!=null)?(String)detailDOB.getFromCountry():""%>' readOnly></td>
			            <%}
						if("From Location".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><input type="text" class="text" name="fromLocation" value='<%=((String)detailDOB.getFromLocation()!=null)?(String)detailDOB.getFromLocation():""%>' readOnly></td>
			            <%}
						if("To Country".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><input type="text" class="text" name="toCountry" value='<%=((String)detailDOB.getToCountry()!=null)?(String)detailDOB.getToCountry():""%>' readOnly></td>
			            <%}
						if("To Location".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><input type="text" class="text" name="toLocation" value='<%=((String)detailDOB.getToLocation()!=null)?(String)detailDOB.getToLocation():""%>' readOnly></td>
			            <%}
						if("Carrier".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><input type="text" class="text" name="carrierId" value='<%=((String)detailDOB.getCarrierId()!=null)?(String)detailDOB.getCarrierId():""%>' readOnly></td>
			            <%}
						if("Service Level".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><input type="text" class="text" name="serviceLevelId" value='<%=((String)detailDOB.getServiceLevelId()!=null)?(String)detailDOB.getServiceLevelId():""%>' readOnly></td>
			            <%}
						if("Industry".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><input type="text" class="text" name="industryId" value='<%=((String)detailDOB.getIndustry()!=null)?(String)detailDOB.getIndustry():""%>' readOnly></td>
			            <%}
						if("File".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td><a href="QMSAttachmentController?Operation=viewFile&uniqueId=<%=fileDOB.getId()%>" target="_blank"><%=(String)fileDOB.getFileName()%></a></td>
			            <%}
						if("Invalidate".equalsIgnoreCase((String)stringList.get(m)))
						{%>
								<td>
								<%if("T".equalsIgnoreCase((String)attachmentDOB.getInvalidate()))
							    {%>
								<input type="checkbox"  name="Invalidate<%=j%>" value='0' checked='true'>
								<%}
						    else 
						 	{
							%>
							<input type="checkbox"  name="Invalidate<%=j%>" value='0'>
							<%}%>
							</td>
			         <%}
					}%>  
					</tr>
					<%}
					else
					 {%>
					  <tr class="formdata">
						<%
						if("ViewAll".equalsIgnoreCase(operation))
						 {
						for(int m=0;m<stringList.size()-1;m++)
						{
							if("File".equalsIgnoreCase((String)stringList.get(m)))
							{%>

								<td><a href="QMSAttachmentController?Operation=viewFile&uniqueId=<%=fileDOB.getId()%>" target="_blank"><%=(String)fileDOB.getFileName()%></a></td>
							 <%}
							else
							{%>
								 <td>&nbsp;</td>
							 <%}
								}
						 }
						 else
						 {for(int m=0;m<stringList.size();m++)
						{
							if("File".equalsIgnoreCase((String)stringList.get(m)))
							{%>

								<td><a href="QMSAttachmentController?Operation=viewFile&uniqueId=<%=fileDOB.getId()%>" target="_blank"><%=(String)fileDOB.getFileName()%></a></td>
							 <%}
							else
							{%>
								 <td>&nbsp;</td>
							 <%}
								}
						 }%>
							 
					  </tr>
						<%}
						 }
						 }
					 if("ViewAll".equalsIgnoreCase(operation))
		              { %>
						<tr class="formdata">
							 <td colspan="<%=stringList.size()-1%>" align="right">
								 <input type="button" class="input" value="Continue" name="continue" onClick="previousePage()">
							</td>
						 </tr>
					 <%}
					 else
		            {%>
						 <tr class="formdata">
							<td colspan="<%=stringList.size()%>" align="right">
								<input name=B1 type=submit value="Submit" class='input'>
								 <input type="reset" class="input" value="Reset" name="reset" >
							 </td>
						 </tr>
					 <%}%>
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
	    logger.error(FILE_NAME+"Error in QMSAttachmentViewAllDtl.jsp"+e.toString());
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
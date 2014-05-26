<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QuoteAttachmentIdList.jsp
Product Name		: QMS
Module Name			: Quote
Task				: AttachmentIdLOV
Date started		: 
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61289

--%>

<%@ page import	=	"javax.naming.InitialContext,
					java.util.ArrayList,
					java.util.ResourceBundle,
					java.util.Locale,
				 	org.apache.log4j.Logger,					
					com.qms.operations.quote.ejb.sls.QMSQuoteSession,
          com.qms.operations.quote.ejb.sls.QMSQuoteSessionHome,
					com.foursoft.etrans.setup.country.bean.CountryMaster,
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.foursoft.esupply.common.java.LOVListHandler,
				 com.qms.operations.quote.dob.QuoteFinalDOB,
				 com.qms.operations.quote.dob.QuoteAttachmentDOB,
					com.foursoft.esupply.common.java.FoursoftConfig,javax.servlet.jsp.jstl.fmt.LocalizationContext,
					com.foursoft.esupply.common.java.LookUpBean" %>
<%!
    private static    Logger    logger      =    null;
	private static final String FILE_NAME	=	"QuoteAttachmentIdList.jsp ";  	
%>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
  response.setContentType("text/html; charset=UTF-8");
  LOVListHandler      listHandler			= null;
  ArrayList           requiredAttributes	= null;
  ArrayList           currentPageList		= null; 
  String              accessType			= null;   
  String              terminalId			= null;
  String              attachmentId			= null;
  String              btnId                 = null;
  ArrayList           attachmentIdList		= new ArrayList();
  QuoteFinalDOB       finalDOB			     = null;
  QuoteAttachmentDOB   attachmentDOB         = new QuoteAttachmentDOB();
   QMSQuoteSessionHome     home        = null;
    QMSQuoteSession         remote      = null;
  	attachmentId	=  request.getParameter("attachmentId");
	btnId	        =  request.getParameter("btnId");
	finalDOB        = (QuoteFinalDOB)session.getAttribute("finalDOB");  
	finalDOB.setDefaultFlag("N");
	if(request.getParameter("pageNo")!=null)  
	{
		try
		{
		  
				listHandler           = (LOVListHandler)session.getAttribute("terminalId");
				requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
				listHandler = null;
		}
	}
	if(listHandler == null)
	{
      requiredAttributes  = new ArrayList();

     
      requiredAttributes.add(attachmentId);
     
  	try
	  {
	      home              = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
        remote            = home.create();
        attachmentIdList  =   remote.getAttachmentIdList(finalDOB,attachmentId);
		
	
	 	if(attachmentIdList!=null)
	    {
			listHandler   = new LOVListHandler(attachmentIdList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			session.setAttribute("attachmentIdList", listHandler);
			listHandler = (LOVListHandler)session.getAttribute("attachmentIdList");
		   }
	  }
		 catch(Exception ex)
      {
      //Logger.error(FILE_NAME,"ETCLOVTrackingIds.jsp : " +ex);
		logger.error(FILE_NAME+"QuoteAttachmentIdList.jsp : " +ex);
      }
	}
	String pageNo			= request.getParameter("pageNo");
 	if(pageNo == null)
	{
		pageNo = "1";
		listHandler.currentPageIndex = 1;
	}
	else
	{
		listHandler.currentPageIndex = Integer.parseInt(pageNo);
	}

	currentPageList	= listHandler.getCurrentPageData();
	String 		fileName	    = "QuoteAttachmentIdList.jsp";
   try
    {
        if(requiredAttributes!=null)
        {
            accessType          = (String)requiredAttributes.get(0);
            terminalId          = (String)requiredAttributes.get(1);
            attachmentId        = (String)requiredAttributes.get(2);
         }
    }
    catch(Exception ex)
    {
       logger.error(FILE_NAME+"QMSAttachmentIdList.jsp : " +ex);
    }

%>
<html>
 <head>
 <title>Select </title>
 <link rel="stylesheet" href="./ESFoursoft_css.jsp">
<script language="javascript">

var isAttributeRemoved  = 'false';
function populateList()
{
	
<%
	if(currentPageList.size() > 0)
	{	
%>
		var len = window.document.forms[0].AttachmentId.length;
<%
		for(int i = 0; i < currentPageList.size(); i++)
		{
%>
			window.document.forms[0].AttachmentId.options[len] = new Option('<%=currentPageList.get(i)%>','<%=currentPageList.get(i)%>')
			len++;
<%
		}
%>
			document.forms[0].AttachmentId.options[0].selected = true;	
			document.forms[0].AttachmentId.focus();
<%
	}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}
function onEnterKey()
{

	if(event.keyCode == 27){
		resetValues();
	}
	if(event.keyCode == 13)
	{
		if(document.forms[0].AttachmentId.selectedIndex == -1)
		{
			alert("Please select  attachmentId");
		}
		else
		{
			temp 		= document.forms[0].AttachmentId.options[document.forms[0].AttachmentId.selectedIndex].value
			 window.opener.document.forms[0].attachmentId<%=btnId%>.value=temp;
		}
		
			resetValues();
		}
	
}
function setAttachmentId()
{
	if(document.forms[0].AttachmentId.selectedIndex == -1)
	{
		alert("Please select AttachmentId");
	}
	else
	{
		var temp = document.forms[0].AttachmentId.options[document.forms[0].AttachmentId.selectedIndex].value;
		window.opener.document.forms[0].attachmentId<%=btnId%>.value= temp;
		resetValues();
	}
}
var closeWindow = 'true';
function resetValues()
{
   	isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=AttachmentId";
    document.forms[0].submit();   
}
function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=AttachmentId";
   }
}
function onEscKey()
{
	if(event.keyCode == 27)
	{
		resetValues();
	}
}
</script>
</head>
<body onLoad='populateList()' onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'>
<form method="post" action="">
<b> <center>
<%
   if(currentPageList.size() > 0)
	{
%>
<center>
<select size=10 name="AttachmentId"  onDblClick='setAttachmentId()' onKeyPress='onEnterKey()' class=select>
</select>
</center>
<center>
<br>
<input type="button" value="Ok"   name="B1" onClick="setAttachmentId()" class="input">
<input type="button" value="Cancel" name="B2" class="input" onClick="resetValues()"></center>
<TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table><%
	}
	else
	{
%>
<center>
<textarea rows="6" name="notes" cols="30" class=select>No Data Available.
</textarea>
<br><br>
<input type="button" value="Close" name="B2"  onClick="window.close()" class="input">
<%
	}
%>

</center>
</form>
</body>
</html>

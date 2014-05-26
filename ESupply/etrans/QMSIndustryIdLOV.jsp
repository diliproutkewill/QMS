
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSIndustryIdLOV.jsp
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
<%@page import = "com.qms.setup.java.IndustryRegDOB,
				  com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
		          javax.naming.InitialContext,
				  javax.naming.Context,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList, com.foursoft.esupply.common.java.LOVListHandler,
				  java.util.Iterator"
				  %>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>				 

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSIndustryIdLOV.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList	industryList		=	null;
	ArrayList	industryPageList	=	new ArrayList();
	IndustryRegDOB industryRegDOB	=	null;
	String[] industryId				=	null;
	String[] industryDesc			=	null;
	boolean		success				=	true;

    LOVListHandler listHandler		=	null;
    ArrayList requiredAttributes	=	null;
	ArrayList currentPageList		=	null;
	String	searchStr				=	request.getParameter("searchString");
	String  operation				=	request.getParameter("Operation");
	int size		=10;
	try
	{  
		if(request.getParameter("pageNo")!= null)
		{
			listHandler           = (LOVListHandler)session.getAttribute("industryList");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
	}
	catch (Exception e)
	{
		listHandler = null;
	} 

	try{

			if(listHandler==null)
			{
				InitialContext initial		= new InitialContext();
				SetUpSessionHome	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
				SetUpSession		remote	=	(SetUpSession)home.create();
				industryList				=	remote.getAllIndustryDetails(searchStr,operation,loginbean);
				
				if(industryList!=null && industryList.size()>0)
				{
					/*industryId		=	new String[industryList.size()];
					industryDesc	=	new String[industryList.size()];*/
					for(int i=0;i<industryList.size();i++)
					{
						industryRegDOB	=	(IndustryRegDOB)industryList.get(i);
						industryPageList.add(industryRegDOB);
						//industryPageList.add(industryRegDOB.getDescription());
					}

				}
				if(industryPageList!=null )
				{
					if(loginbean.getUserPreferences().getLovSize()!=null && !(loginbean.getUserPreferences().getLovSize().equals("")))
						{	size	=	Integer.parseInt(loginbean.getUserPreferences().getLovSize());}        
					listHandler  = new LOVListHandler(industryPageList,size,requiredAttributes);
					session.setAttribute("industryList", listHandler);		
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

	}catch(Exception e)
	{
		success=false;
		//Logger.error(FILE_NAME,"Exception while retrieving the data"+e);
    logger.error(FILE_NAME+"Exception while retrieving the data"+e);

	}

%>
<html>
<head>
<title>CarrierRegistration Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language=	"javascript">

	function selectIndustryId()
	{
		var indx = document.forms[0].industryId.selectedIndex;
		var industryId = document.forms[0].industryId.options[indx].value;


		window.opener.document.forms[0].industryId.value = industryId;
		resetValues();
		window.close();
	}
	function selectFirstOption()
	{
		if(document.forms[0].industryId != null) 
		{
			if(document.forms[0].industryId.options.length > 0) 
			{
				document.forms[0].industryId.options[0].selected = true;
			}
			document.forms[0].industryId.focus();
		}
	}
	function handleEvent( type )
	{
		
		if(type=='enter')
		{			
			var keycode	= (window.Event) ? window.event.which : window.event.keyCode;

			if(keycode == 13)	
			{ 				
				if(document.forms[0].industryId.selectedIndex == -1) 
				{
					alert('Please select a Industry Id');
				} 
				else 
				{
					selectIndustryId();
				}
			}
			if(keycode == 27) 
			{
				window.close();
			}
		}
		
		if(type=='dblClick') 
		{
			if(document.forms[0].industryId.selectedIndex == -1) 
			{
				alert('Please select a Industry Id');
			} 
			else 
			{
				selectIndustryId();
			}
		}
	}
///// Added for LOV Paging /////

	function onEscKey()
	{
		if(event.keyCode == 27)
		{
			resetValues();
		}
	}

	var isAttributeRemoved  = 'false';
	var isRegistered  = 'false';
	
	function onCancel()
	{
		//removeLOVValues();
		resetValues();
		window.close();
	}

	var closeWindow = 'true';

	function setVar()
	{
		closeWindow = 'false';
	}

	function toKillSession()
	{
		if(closeWindow == 'true' && isAttributeRemoved=='false')
		{
			window.location.href="../ESupplyRemoveAttribute.jsp?valueList=industryList";
		}
	}

	function resetValues()
	{
		isAttributeRemoved  = 'true';
		document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=industryList";
		document.forms[0].submit();   
	}
///// Added for LOV Paging /////
</script>
</head>
<body class='formdata' onLoad='selectFirstOption();' onKeyPress="handleEvent('enter');onEscKey();" onUnLoad='toKillSession()'>
<form name='f1' method='post' >
<%	if(currentPageList.size() > 0)	
	{ 
%>
	  <table width="210" align="center" height="250">
		<tr valign="top" >
			<td height="15" >
			  <p align="center"><b>Industry Ids<br></b></p>
			</td>
		</tr>
		<tr valign="top">

			<TD height="192">
				<p align="center">
				<select size="10" name="industryId" class="select" onDblClick="handleEvent('dblClick')" onKeyPress="handleEvent('enter');">
<%
				String		industry	= "";
				int i =0;
				for(int j=0;j<currentPageList.size();j++)
				{
					//industry		= currentPageList.get(j).toString();
					industryRegDOB  = (IndustryRegDOB)currentPageList.get(j);
%>					<option value="<%= industryRegDOB.getIndustry() %>"> <%= industryRegDOB.getIndustry() %>[<%= industryRegDOB.getDescription() %>]</option>
<%		
				} 
%>
				</select>
				</p>
				<table cellSpacing=0 width=95%>
				<tr  class="formdata"> 
					<td width=100% align='center'>Pages : &nbsp;
						<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= size %>" fileName="<%= FILE_NAME %>"/>
					</td>
				</tr>	
			</table>
			</TD>
			
		</TR>
		
		<TR>
			<TD height="27">
			  <p align="center"><INPUT TYPE="submit" NAME="Select" VALUE='Ok' onClick = "selectIndustryId()"  class='input'>
			  <INPUT TYPE='submit' NAME='Select' VALUE='Cancel' onClick='onCancel()'  class='input'></p>
			</TD>
		</TR>
<%	}
	else 
	{
%>     
	
             <p align="center">
			 <br>
			 <br>
      			<SELECT SIZE="10" NAME="locationId" class="select" >
            
            <option > No Data Found</option>
            </select>


          <p align='center'><INPUT TYPE=BUTTON VALUE="Close" onClick='javascript:window.close()'  class='input'>
    
<%    
	}
%>
	</table>
</form>
</body>
</html>

<!--
% 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%

	Program Name	:ETCLOVLocationIds.jsp
	Module Name		:ETrans
	Task			:Location
	Sub Task		:LocationLOV
	Author Name		:
	Date Started	:28-06-2005
	Date Completed	:September 20,2001
	Date Modified	:
	Description		:This file is invoked when clicked on the Location LOV in the LocationEnterId Screen. In this all the Location details particular to that
					LocationId are displayed in the List Box. Once Selected any one of the Location ID ,Details related to that LocationId
					are displayed in the respective Text Fields. If no Locations are available for this LocationId then a Text Area with a message
					'No Location Details are available for this LocationId'.
					This file will interacts with ETransUtiltitesSessionBean the method getLocationIds which inturn
					retrive the details.		

-->

<%@ page import="javax.naming.InitialContext,org.apache.log4j.Logger,
				java.util.ArrayList,
				com.qms.setup.ejb.sls.QMSSetUpSession,
                com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				com.foursoft.etrans.setup.country.bean.CountryMaster,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
        
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVLocationIds.jsp ";
%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList			requiredAttributes	=	null;
	LOVListHandler		listHandler			=	null;
	ArrayList			locationIds			=	null;	// ArrayList to store locationIds
	int					len					=	0;		//len to store length of locationIds
	String				whereClause			=	null;
	String				wheretoset			=	"";
	String				ArrayOrNot			=	null;
	String				fromWhat			=	request.getParameter("fromWhat");
	String				from				=	request.getParameter("from");
	String				city				=	request.getParameter("city");
	String				module				=	null;
    String				searchString		=	"";	
	String              operation           =   request.getParameter("Operation");
	String              controlStation      =   request.getParameter("controlStationId");

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("locationIds");
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
        if(fromWhat == null)
			fromWhat="0";
		else
			fromWhat = fromWhat;
		searchString	= request.getParameter("searchString");
		if(searchString == null)
			searchString="";
		else
			searchString = searchString.trim();
		module = request.getParameter("Module");
		
		if(request.getParameter("wheretoset")!=null)
			wheretoset=request.getParameter("wheretoset");	
		else
			wheretoset = "locationId";	
		
		if(request.getParameter("ArrayOrNot")!=null)
			ArrayOrNot=request.getParameter("ArrayOrNot");
		else
			ArrayOrNot="NoArray";
		
		if(request.getParameter("whereClause")!=null)
			whereClause=request.getParameter("whereClause");
		else
			whereClause="";
		if(request.getParameter("city")!=null)
			city=request.getParameter("city");
		else
			city="";
		requiredAttributes.add(whereClause);
		requiredAttributes.add(wheretoset);
		requiredAttributes.add(ArrayOrNot);	
		requiredAttributes.add(city);
		try
			{
				InitialContext initial			    =	new InitialContext(); 
				QMSSetUpSessionHome home			=	(QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");	
				QMSSetUpSession remote				=	(QMSSetUpSession)home.create();
				//Logger.info(FILE_NAME,"controlStationId        "+controlStation);
				if("DownLoad".equals(operation))
					locationIds						    =	remote.getMultipleLocationIds(controlStation);
				else
				    locationIds						    =	remote.getLocationIds(searchString);
				//Logger.info(FILE_NAME,"locationIds        "+locationIds);
				if(locationIds != null)
				{
					len	=	locationIds.size();
				}
				if(locationIds!=null)
				{
					listHandler   = new LOVListHandler(locationIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
					session.setAttribute("locationIds", listHandler);
					listHandler = (LOVListHandler)session.getAttribute("locationIds");
				}
			}
			catch(Exception e)
			{
				//Logger.error(FILE_NAME,"ETCLOVLocationIds.jsp : " , e.toString());
        logger.error(FILE_NAME+"ETCLOVLocationIds.jsp : " + e.toString());
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

	ArrayList	currentPageList	= listHandler.getCurrentPageData();
	String 		fileName	= "QMSLOVLocationIds.jsp";  

	try
    {   
        if(requiredAttributes!=null)
        {
            whereClause       = (String)requiredAttributes.get(0);
            wheretoset        = (String)requiredAttributes.get(1);
            ArrayOrNot        = (String)requiredAttributes.get(2);
			city              = (String)requiredAttributes.get(3);            
        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETCLOVTrackingIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETCLOVTrackingIds.jsp : " +ex);
    }

%>
<html>
<head>
<title>Select </title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populatelocationNames()
{

<%

	for( int i=0;i<currentPageList.size();i++ )
	{
		String str	=	currentPageList.get(i).toString();
%>
		document.forms[0].locationNames.options[ <%= i %> ] = new Option('<%= str %>','<%=str%>','<%= str %>');
<%
	}
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].locationNames.options[0].selected = true;	
			document.forms[0].locationNames.focus();

<%
	}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}
function setlocationNames()
{
	if(document.forms[0].locationNames.selectedIndex == -1)
	{
		alert("Please select atleast one LocationId")
	}
	else
	{	
		<%
			if("DownLoad".equals(operation))
		    {
		%>
			var indx = document.forms[0].locationNames.length;
			var k=0;
			var locationIds = new Array();
			for(var index=0;index<indx;index++)
			{
				if(document.forms[0].locationNames.options[index].selected== true)
				{
						locationIds[k]=document.forms[0].locationNames.options[index].value;
						k++;					
				}
			}
		window.opener.selectLocationId(locationIds);
	<%
		}
	else
		{%>
		var len		=<%=fromWhat%>;		
		var cond 	='<%= ArrayOrNot%>' ;
		var index	=   document.forms[0].locationNames.selectedIndex;
		firstTemp	=   document.forms[0].locationNames.options[index].value;
		secondTemp	=   document.forms[0].locationNames.options[index].value;
		firstIndex	=	firstTemp.indexOf(0);
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		temp		=   firstTemp.toString();
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);		

		var whereClause = "<%=whereClause%>";			
				window.opener.document.forms[0].<%=wheretoset%>.value=temp;
				window.opener.document.forms[0].<%=wheretoset%>.focus();
				window.opener.document.forms[0].city.value=temp1;		
	<%}%>
			resetValues();
	}		
}
function onEnterKey()
{

	if(event.keyCode == 13)
	{
			if(document.forms[0].locationNames.selectedIndex == -1)
			{
				alert("Please select atleast one LocationId")
			}
			else
			{
		<%
			if("DownLoad".equals(operation))
		    {
		%>
			var indx = document.forms[0].locationNames.length;
			var k=0;
			var locationIds = new Array();
			for(var index=0;index<indx;index++)
			{
				if(document.forms[0].locationNames.options[index].selected== true)
				{
						locationIds[k]=document.forms[0].locationNames.options[index].value;
						k++;					
				}
			}
		window.opener.selectLocationId(locationIds);
			
	<%
		}
	else
		{%>

							var len		=<%=fromWhat%>;		
							var cond 	='<%= ArrayOrNot%>' ;
							var index	=   document.forms[0].locationNames.selectedIndex;
							firstTemp	=   document.forms[0].locationNames.options[index].value;
							secondTemp	=   document.forms[0].locationNames.options[index].value;
							firstIndex	=	firstTemp.indexOf(0);
							lastIndex	=	firstTemp.indexOf('[');	
							firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
							temp		=   firstTemp.toString();
							lastIndex1	=	secondTemp.lastIndexOf('[')+1;
							lastIndex2	=	secondTemp.lastIndexOf(']');	
							temp1		=	secondTemp.substring(lastIndex1,lastIndex2);		

							var whereClause = "<%=whereClause%>";			
							window.opener.document.forms[0].<%=wheretoset%>.value=temp;
							window.opener.document.forms[0].<%=wheretoset%>.focus();
							window.opener.document.forms[0].city.value=temp1;							
							resetValues();	
<%				
	}
%>  			
		}		
	}
		if(event.keyCode == 27){
			resetValues();
		}
}
function onEscKey(){
	if(event.keyCode == 27){
		resetValues();
	}
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=locationIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="ESupplyRemoveAttribute.jsp?valueList=locationIds";
	
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populatelocationNames()" onKeyPress='onEscKey()'>
<form method="post" action="">
<center>
<b><font face="Verdana">Location Nos</font></b>
</center>
<br>
<%
	if(currentPageList.size() > 0)
	{
%>

<center>
<select size="10" name="locationNames" <%="DownLoad".equals(operation) ? "multiple" : ""%> onDblClick='setlocationNames()' onKeyPress='onEnterKey()' class="select">
</select>
</center>
<br>
<center>
<input type="button" value=" Ok " name="OK" onClick="setlocationNames()" class="input">
<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
<TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}
	else
	{// begin of else
%>
<br><br>
<center>
<textarea cols=30 class='select' rows=6 readOnly >No Location Nos available.
</textarea>
<br><br>
<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
<%
	}// end of else
%>
</center>
</form>
</body>
</html>

<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/**
	Program Name	:ETSLOVPortIds.jsp
	Module Name		:ETrans
	Task			:Port
	Sub Task		:PortLOV
	Author Name		:UshaSree.Petluri
	Date Started	:September 20,2001
	Date Completed	:September 20,2001
	Date Modified	:
	Description		:This file is invoked when clicked on the Location LOV in the LocationEnterId Screen. In this all the Location details particular to that
					LocationId are displayed in the List Box. Once Selected any one of the Location ID ,Details related to that LocationId
					are displayed in the respective Text Fields. If no Locations are available for this LocationId then a Text Area with a message
					'No Location Details are available for this LocationId'.
					This file will interacts with ETransUtiltitesSessionBean the method getLocationIds which inturn
					retrive the details.		
*/
%>
<%@ page import="java.util.ArrayList,
				 org.apache.log4j.Logger,
				 com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib  uri="../../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
%>

<%
	String FILE_NAME = "ETSLOVPortIds.jsp";
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList portIds	    =	null;	// ArrayList to store locationIds
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	int       len			=	0;		//len to store length of locationIds
	String wheretoSet =request.getParameter("whereToSet");
	String routeId	  =request.getParameter("routeId");
	String consoleNo  =request.getParameter("consoleNo");
    String portId	  =	null;
	String searchStr  =	request.getParameter("searchStr");
	String operation  = request.getParameter("Operation");//added by rk
	String fromWhere  =	request.getParameter("fromWhere");
	if(request.getParameter("pageNo")!=null)  
	{
		try
		{

			listHandler           = (LOVListHandler)session.getAttribute("portIds");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
   if(listHandler == null)
    {
        //System.out.println("Handler is not null");
        requiredAttributes  = new ArrayList();
       
		//Logger.info(FILE_NAME," THE ----> Searchstr->"+searchStr);
  		if (searchStr==null)
			searchStr="";

      requiredAttributes.add(wheretoSet);
      requiredAttributes.add(routeId);
      requiredAttributes.add(consoleNo);
      requiredAttributes.add(searchStr);
	  requiredAttributes.add(fromWhere);
    try
    {      
		portId      		  			       = request.getParameter("portId");
//		SetUpSessionHome homeObject   = (SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up the Bean
		SetUpSessionHome homeObject   = (SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");	// looking up the Bean
		SetUpSession     remoteObject = (SetUpSession)homeObject.create();
		portIds						           = remoteObject.getPortIds(routeId,consoleNo,wheretoSet,searchStr,operation,loginbean.getTerminalId());//added by rk
		
	//len									   = portIds.size();
    if(portIds != null)
		{
			len =	portIds.size();
		}
        //System.out.println("Length is " + len);

        if(portIds!=null)
        {
            listHandler                     = new LOVListHandler(portIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("portIds", listHandler);
            listHandler = (LOVListHandler)session.getAttribute("portIds");
        }
   }
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"ETSPortMasterLOV.jsp : Exception " + e.toString());
    logger.error(FILE_NAME+"ETSPortMasterLOV.jsp : Exception " + e.toString());
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
	String 		fileName	= "ETSLOVPortIds.jsp";  

  try
    {
        //System.out.println("Hai Size is " + currentPageList.size());
        if(requiredAttributes!=null)
        {
            wheretoSet        = (String)requiredAttributes.get(0);
            routeId        = (String)requiredAttributes.get(1);
            consoleNo         = (String)requiredAttributes.get(2);
            searchStr     = (String)requiredAttributes.get(3);
			fromWhere     = (String)requiredAttributes.get(4);
            //System.out.println("Hai334555 " + wheretoSet);
    
        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETransLOVPRQIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETransLOVPRQIds.jsp : " +ex);
    }
%>
<html>
<head>
<title>Select </title>       <%@ include file="../../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
<script>
var isAttributeRemoved  = 'false';
function  populatelocationNames()
{
<%
	for( int i=0;i<currentPageList.size();i++ )
	{
		String str	=	currentPageList.get(i).toString();
%>
		document.forms[0].portNames.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
<%
	}
	if(currentPageList.size() > 0)
	{
%>
		document.forms[0].portNames.options[0].selected=true;
		document.forms[0].portNames.focus();
<%
	}else{
%>
	document.forms[0].B2.focus();
<%
	}
%>
}
function setportNames()
{
	if(document.forms[0].portNames.selectedIndex == -1)
	{
		alert("Please select atleast one PortId")
	}
	else
	{
		var index	=	document.forms[0].portNames.selectedIndex;
		firstTemp	=	document.forms[0].portNames.options[index].value;
		secondTemp	=	document.forms[0].portNames.options[index].value;
		firstIndex	=	firstTemp.indexOf('[');
		lastIndex	=	firstTemp.indexOf(']');

		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);

		//alert(temp1)

		if(firstIndex!=-1)
		{
			firstTemp	=	firstTemp.substring(0,firstIndex);
		}
		temp		=	firstTemp.toString();

<%		if("Quote".equals(fromWhere))
		{
%>			window.opener.document.forms[0].portName.value=temp1;
			window.opener.populateCAP();		
<%
		}
%>

		<% if(wheretoSet !=null){ %>
			opener.parent.text.document.forms[0].<%=wheretoSet%>.value=temp;
		<% }else{ %>
			opener.parent.text.document.forms[0].portId.value=temp;
		<% } %>
		resetValues();
	}
}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		 setportNames();
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
      window.location.href="../../ESupplyRemoveAttribute.jsp?valueList=portIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../../ESupplyRemoveAttribute.jsp?valueList=portIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populatelocationNames()" onKeyPress='onEscKey()'>
<form method="post" action="">
<center>
<b>Port Ids</b>
</center>
<br>
<%
	if(currentPageList.size() > 0)
	{// begin of if
%>
<center>
<select size="10" name="portNames" onKeyPress='onEnterKey()' onDblClick='setportNames()' class="select">
</select>
</center>
<br>
<center>
<input type="button" value=" Ok " name="OK" onClick="setportNames()" class="input">
<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">

 <TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>

<%
	}// end if
	else
	{// begin of else
%>
<br><br>
<center>
<textarea cols=30 rows=6 readOnly class="select">No Port Ids are  available.
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

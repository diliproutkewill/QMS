<!--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%

	Program Name	:ETCLOVCommodityIds.jsp
	Module Name		:ETrans
	Task			:Commodity	
	Sub Task		:CommodityLOV	
	Author Name		:UshaSree.Petluri
	Date Started	:September 19,2001
	Date Completed	:September 19,2001
	Date Modified	:
	Description		:This file is invoked when clicked on the CommodityLOV in the CommodityEnterId Screen. 
					Commodity Ids are displayed in the List Box . 
					This file will interacts with SetUpSession Bean and then calls the method getCommodityIds which inturn 
					retrive the all the Commodity Ids .
		

-->

<%@ page import="javax.naming.InitialContext,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.country.bean.CountryMaster,
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig" %>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>         
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVCommodityIds.jsp ";
%>

<%	
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	ArrayList	commodityIds	=	null; //Vector to store CommodityIds
	int			len				=	0;	 // variable to store commodityIds size
	String		whereClause		= "";	
	String		searchString	="";	
	String operation            =   request.getParameter("Operation");//added by rk
    String      commodityType   = request.getParameter("commodityType");

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("commodityIds");
			requiredAttributes    = listHandler.requiredAttributes;
			commodityType		  = (String)requiredAttributes.get(0);
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
	
	try
	{
	 if(listHandler == null)
	{
        System.out.println("Handler is not null");
        requiredAttributes  = new ArrayList();
		requiredAttributes.add(commodityType);
		searchString	= request.getParameter("searchString");
		if(searchString != null){
			searchString = searchString.trim();	
		}else{
			searchString="";
		}
		  


		//if(commodityIds==null)
		//{
			InitialContext initial			= 	new InitialContext(); // variable to get initial context for JNDI
			SetUpSessionHome home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up ETransUtitlitiesSessionBean
			SetUpSession remote		=	(SetUpSession)home.create();
			if(commodityType==null)
   			   commodityIds					=	remote.getCommodityIds(searchString,loginbean.getTerminalId(),operation);		
			else
			   commodityIds					=	remote.getCommodityIds(searchString,commodityType,loginbean);		//added by rk			
			/*
				Here where condition should be passed within quotes when required in of passsing in query.
				If no condition is to passed then keep the String empty with quotes.
			*/		
		if(commodityIds != null);
		{	
			len	=	commodityIds.size();	//To find the length of commodityIds		
		}
	if(commodityIds!=null)
	{
            listHandler                     = new LOVListHandler(commodityIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("commodityIds", listHandler);
            listHandler = (LOVListHandler)session.getAttribute("commodityIds");
        }
	}	
	}
	catch(Exception ee)
	{
	//Logger.error(FILE_NAME,"Error in CommodityMasterCommodityLov.jsp file: Exception3 : ", ee.toString());
  logger.error(FILE_NAME+"Error in CommodityMasterCommodityLov.jsp file: Exception3 : "+ ee.toString());
	}

  String pageNo = request.getParameter("pageNo");
  if(pageNo == null)
  {
    pageNo = "1";
    listHandler.currentPageIndex = 1;
  }
  else
  {
  listHandler.currentPageIndex = Integer.parseInt(pageNo);
  }
  ArrayList currentPageList = listHandler.getCurrentPageData();
  String fileName = "ETCLOVCommodityIds.jsp";
%>
<html>
<head>
<title>Select</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
var first =new Array();//added by rk
var last =new Array();//added by rk
var classt=new Array();
var unno=new Array();
var haza=new Array();
function  populateCommodityIds()
{	
 
<%			
		for( int i=0;i<currentPageList.size();i++ )
		{// for loop begin , this for loop is to get the commodityids and there description
			String str	=currentPageList.get(i).toString(); // variable to store CommodityIds
%>
			val	= '<%=str%>'.split(',');//added by rk
       
			first  = val[0];
			last   = val[1];
			haza[<%= i %> ]  = val[2];
			classt[<%= i %> ] = val[3];
			unno[<%= i %> ]   = val[4];
			document.forms[0].commodityNames.options[ <%= i %> ] = new Option(first + "\ [" + last+" \ ]",first);
<%
		}//for loop end
		if(currentPageList.size() > 0)
		{
%>
			document.forms[0].commodityNames.options[0].selected = true;	
			document.forms[0].commodityNames.focus();

<%
		}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}

function setCommodityIds()
{
	if(document.forms[0].commodityNames.selectedIndex == -1)
	{
		alert("Please select atleast one CommodityId  ")
	}
	else
	{
		
		firstTemp		=	document.forms[0].commodityNames.options[document.forms[0].commodityNames.selectedIndex].value
		temp			=	firstTemp.toString();
		var whereClause =	"<%=whereClause%>"
		if(whereClause=="")
		window.opener.document.forms[0].commodityId.value=temp;
		else
		window.opener.document.forms[0].withCommodityId.value=temp;
		<%if(commodityType!=null){%>//added by rk
           
			if(haza[document.forms[0].commodityNames.selectedIndex]=='N')
 			     window.opener.document.forms[0].hazardousInd.checked=false;
			else
				window.opener.document.forms[0].hazardousInd.checked=true;
  		    window.opener.document.forms[0].unNo.value=unno[document.forms[0].commodityNames.selectedIndex];
			window.opener.document.getElementById("class1").value=classt[document.forms[0].commodityNames.selectedIndex];
			<%}%>
		resetValues();
	}
}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		if(document.forms[0].commodityNames.selectedIndex == -1)
		{
			alert("Please select atleast one CommodityId  ")
		}
		else
		{
			
			firstTemp		=	document.forms[0].commodityNames.options[document.forms[0].commodityNames.selectedIndex].value
			temp			=	firstTemp.toString();
			var whereClause =	"<%=whereClause%>"
			if(whereClause=="")
			window.opener.document.forms[0].commodityId.value=temp;
			else
			window.opener.document.forms[0].withCommodityId.value=temp;
			<%if(commodityType!=null){%>//added by rk
           
			if(haza[document.forms[0].commodityNames.selectedIndex]=='N')
 			     window.opener.document.forms[0].hazardousInd.checked=false;
			else
				window.opener.document.forms[0].hazardousInd.checked=true;
  		    window.opener.document.forms[0].unNo.value=unno[document.forms[0].commodityNames.selectedIndex];
			window.opener.document.getElementById("class1").value=classt[document.forms[0].commodityNames.selectedIndex];
			<%}%>
			resetValues();
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
closeWindow = 'false'
}

function toKillSession()
{
  if(closeWindow == 'true' && isAttributeRemoved=='false')
  {
  window.location.href="../ESupplyRemoveAttribute.jsp?valueList=commodityIds";
  }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=commodityIds";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">

</head>
<body onUnLoad='toKillSession()' onLoad="populateCommodityIds()" class='formdata' onKeyPress='onEscKey()'>
  <form method="post" action=""> 
   <center><b>Commodity Ids</b></center>
<%
	if(currentPageList.size() > 0)
	{
%>
			<br>
			<center>
			<select size="10" name="commodityNames" onDblClick='setCommodityIds()' onKeyPress='onEnterKey()' class="select">
			</select>
			<br>
			<br>
			<input type="button" value=" Ok " name="OK" onClick="setCommodityIds()" class="input">
			<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
			</center>
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
	{
%>
			<br>
			<center>
			<textarea cols=30 class='select' rows = 6 readOnly >No Commodity Ids available
			</textarea>
			<br><br>

			<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
			</center>
<%
	}
%>
</form>
</body>
</html>

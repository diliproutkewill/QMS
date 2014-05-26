<%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%
 %		File					:	ETCRegionIdsLOV.jsp
 %		Sub-module name			:	
 %		Module name				:	EAccounts
 %		Purpose of the class	:	This File brings all Region ids when the LOV button is	pressed
 %    	Autor					:	P.Uma Phani Sekhar
 %		Date					:	6/2/2009
 %		Modified history		:
--%>





<%@ page import="javax.naming.InitialContext,
					java.util.ArrayList,
					java.util.ResourceBundle,
					java.util.Locale,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.country.bean.CountryMaster,
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCRegionIdsLOV.jsp";
	
	%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
  String  language = "";  	
  LOVListHandler listHandler   = null;
  ArrayList requiredAttributes = null;
	ArrayList currentPageList = null;   
  language = loginbean.getUserPreferences().getLanguage();
%>

<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<%
  

	 response.setContentType("text/html; charset=UTF-8");
	ArrayList regionIds		=	null;
	int len				=	0;
	String str			=	null;
	String value			=	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("No_Country_Ids_are_available");
	SetUpSessionHome home	= null;
	SetUpSession remote 	= null;
	String whereClause		= null;
	String wheretoset		= null;
	String	locationIds	= "";	
	String	countryIds   = "";	
	String	regions	= "";	
	String   rows           = null;
	String  locationId		= request.getParameter("locationId");
	String  listTypes1[]	= request.getParameterValues("listTypes1");
	String  shipmentMode    = request.getParameter("shipmentMode");
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"originRegion";

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("regionIds");
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
	locationIds	= request.getParameter("searchString");
	rows	= request.getParameter("row");
	
	countryIds	= request.getParameter("searchString2");
	regions	= request.getParameter("searchString3");
	if(locationIds == null)
		locationIds="";
	else
		locationIds = locationIds.trim();

	if(countryIds == null)
		countryIds="";
	else
		countryIds = countryIds.trim();

	if(regions == null)
		regions="";
	else
		regions = regions.trim();

	if(request.getParameter("row")!=null)
		rows=request.getParameter("row");
	else
		rows = "";

		

	

		if(request.getParameter("wheretoset")!=null)
			wheretoset=request.getParameter("wheretoset");	
		else
			wheretoset = "originRegion";	
		
		
		requiredAttributes.add(wheretoset);
		requiredAttributes.add(locationIds);					
		requiredAttributes.add(rows);					
			
		try
		{ 
		InitialContext	initial			=	new InitialContext();	
		home					=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	
		remote					=	(SetUpSession)home.create();

		
			regionIds				=	remote.getRegionIds(regions,countryIds,locationIds,shipmentMode);
		
		if(regionIds != null)
        {
          len	= regionIds.size();
        }
        if(regionIds!=null)
        {
           listHandler                     = new LOVListHandler(regionIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
           session.setAttribute("regionIds", listHandler);
           listHandler = (LOVListHandler)session.getAttribute("regionIds");
        }
	}
	catch(Exception	e)
	{
		
    logger.error(FILE_NAME+"ETCRegionIdsLOV.jsp: Exception5"+ e.toString());
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
	String 		fileName	= "ETCRegionIdsLOV.jsp"; 

  try
    {
        if(requiredAttributes!=null)
        {
            whereClause        = (String)requiredAttributes.get(0);
            wheretoset        = (String)requiredAttributes.get(1);
            locationIds         = (String)requiredAttributes.get(2);
            rows         = (String)requiredAttributes.get(3);

        }
    }
    catch(Exception ex)
    {
      logger.error(FILE_NAME+"ETCRegionIdsLOV.jsp : " +ex);
    }
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';


function  populateCountryNames()
{
<%
	boolean flag = true;
    int     count = 0;
	for(int i=0;i<currentPageList.size();i++ )
	{
		str=currentPageList.get(i).toString();
		
		

%>
	
		document.forms[0].countryNames.options[ <%= count %> ] = new Option('<%= str %>','<%= str %>');
<%		count++;
	}
	if(count>0 && currentPageList.size() > 0)
	{
%>
			document.forms[0].countryNames.options[0].selected = true;	
			document.forms[0].countryNames.focus();

<%
	}else{

%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setCountryNames()
{
	
	if(document.forms[0].countryNames.selectedIndex == -1)
	{
		alert("Please enter RegionId");
	}
	else
	{
		
		for(var i=0;i<document.forms[0].countryNames.options.length;i++)
		{
			
			if(document.forms[0].countryNames.options[i].selected)
			{
				
			var	index	=document.forms[0].countryNames.selectedIndex;
			var result;
			
			firstTemp	=document.forms[0].countryNames.options[index].value;
			
			firstIndex	=firstTemp.indexOf('[');
			lastIndex	=firstTemp.indexOf(']');			
			result	=firstTemp.substring(firstIndex+1,lastIndex);
			}
		}
		opener.parent.text.document.forms[0].<%=name%>.value=result;
				resetValues();	
	}
}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		if(document.forms[0].countryNames.selectedIndex == -1)
		{
			alert("Please enter RegionId");
		}
		else
		{
			//window.opener.showLocationValues(document.forms[0].listTypes1,'<%=wheretoset%>');
		
			//resetValues();
				for(var i=0;i<document.forms[0].countryNames.options.length;i++)
		{
			
			if(document.forms[0].countryNames.options[i].selected)
			{
				
			var	index	=document.forms[0].countryNames.selectedIndex;
			var result;
			
			firstTemp	=document.forms[0].countryNames.options[index].value;
			
			firstIndex	=firstTemp.indexOf('[');
			lastIndex	=firstTemp.indexOf(']');			
			result	=firstTemp.substring(firstIndex+1,lastIndex);
			}
		}
		opener.parent.text.document.forms[0].<%=name%>.value=result;
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

function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=regionIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=regionIds";
    document.forms[0].submit();   
}
function setVar()
{
  closeWindow = 'false';
  document.forms[0].action="ETCRegionIdsLOV.jsp?pageNo="+<%=pageNo%>;
	document.forms[0].submit();
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateCountryNames()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">Region Ids</font></b>
		</center>
		

<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
<center>
	<select size="10" name="countryNames" selected=0 class="select"  onDblClick='setCountryNames()' onKeyPress='onEnterKey()'></select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setCountryNames()" class="input">
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
	}// end of if loop
	else
	{// begin of else loop
%>
			<br><br>
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly ><%= value %></textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>

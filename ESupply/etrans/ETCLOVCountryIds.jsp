 <%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%
 %		File					:	ETCLOVCountryIds.jsp
 %		Sub-module name			:	
 %		Module name				:	EAccounts
 %		Purpose of the class	:	This File brings all CountryLOVs when the button in enterId screen	was	pressed
 %    	Autor					:	UshaSree.Petluri
 %		Date					:	September 19th	2001.	
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
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVCountryIds.jsp ";
	%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
    String  language = "";
    LOVListHandler listHandler   = null;
    ArrayList requiredAttributes = null;
    ArrayList currentPageList = null;
    language = loginbean.getUserPreferences().getLanguage();
%>


<fmt:setLocale value="${language}"/>

	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<%
	
  

  //@@ Srivegi Added on 20050503
	 response.setContentType("text/html; charset=UTF-8");
	/* String language = loginbean.getUserPreferences().getLanguage();
	 ResourceBundle rb = ResourceBundle.getBundle("../Lang_"+language);
	//@@ 20050503	
	ArrayList requiredAttributes	=	null;
	LOVListHandler listHandler	=	null;*/
	ArrayList countryIds		=	null;
	
	int len					=	0;
	String str				=	null;
	String value			=	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("No_Country_Ids_are_available");
	SetUpSessionHome home	=	null;
	SetUpSession remote		=	null;
	String whereClause		=	null;
	String wheretoset		=	null;
	String searchString		=	"";	
	String rows				=   null;
	String locationId		=	request.getParameter("locationId");//@@Added by Yuvraj for CR_DHLQMS_1006
	String shipmentMode		=	request.getParameter("shipmentMode");//@@Added by Yuvraj for PBN-13145
	String operation		=   request.getParameter("Operation");//added by rk
	String country			=	request.getParameter("country");//Added by Anil.k for Enhancement 231214 on 24Jan2011
	
	//System.out.println("Rows ................. "+request.getParameter("row"));
	
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("countryIds");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}

	if(listHandler == null)
	{
		System.out.println("Handler is  null");
		requiredAttributes  = new ArrayList();
	searchString	= request.getParameter("searchString");
	rows	= request.getParameter("row");

	if(searchString == null)
		searchString="";
	else
		searchString = searchString.trim();

	if(request.getParameter("row")!=null)
		rows=request.getParameter("row");
	else
		rows = "";

		//Logger.info(FILE_NAME,"searchString "+searchString);

		if(request.getParameter("whereClause")!=null)
			
			 whereClause=request.getParameter("whereClause");
		
		else
			 whereClause="";
  
		String new_whereClause="";

		if(request.getParameter("wheretoset")!=null)
			wheretoset=request.getParameter("wheretoset");	
		else
			wheretoset = "countryId";	
		
		requiredAttributes.add(whereClause);
		requiredAttributes.add(wheretoset);
		requiredAttributes.add(searchString);					
		requiredAttributes.add(rows);					
			
		try
		{ 
		InitialContext	initial			=	new InitialContext();	// looking up JNDI context
		home					=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up ETransUtitlities Session Bean	
		remote					=	(SetUpSession)home.create();
		if("CSR".equals(whereClause) && locationId!=null)//@@Added by Yuvraj for CR_DHLQMS_1006
			countryIds				=	remote.getCountryIds1(searchString,locationId,shipmentMode);
// added by subrahmanyam for 186695 on 20/oct/09 
		else if("FreightRate".equalsIgnoreCase(request.getParameter("forWhat")))
					
				countryIds				=	remote.getCountryIds1(locationId,searchString,shipmentMode);
// ended by subrahmanyam for 186695 on 20/oct/09

		else//@@Yuvraj
		    countryIds				=	remote.getCountryIds(searchString,loginbean.getTerminalId(),operation);	//added by rk
		
		if(countryIds != null)
        {
          len	= countryIds.size();
        }
        if(countryIds!=null)
        {
           listHandler                     = new LOVListHandler(countryIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
           session.setAttribute("countryIds", listHandler);
           listHandler = (LOVListHandler)session.getAttribute("countryIds");
        }
	}
	catch(Exception	e)
	{
		//Logger.error(FILE_NAME,"ETCLOVCountryIds.jsp: Exception5", e.toString());
    logger.error(FILE_NAME+"ETCLOVCountryIds.jsp: Exception5"+ e.toString());
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
	String 		fileName	= "ETCLOVCountryIds.jsp"; 

  try
    {
       // System.out.println("Hai Size is " + currentPageList.size());
        if(requiredAttributes!=null)
        {
            whereClause        = (String)requiredAttributes.get(0);
            wheretoset        = (String)requiredAttributes.get(1);
            searchString         = (String)requiredAttributes.get(2);
            rows         = (String)requiredAttributes.get(3);

            //System.out.println("Hai334555 " + wheretoset);
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
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateCountryNames()
{
<%
	//System.out.println("currentPageList.size():"+currentPageList.size());

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
		str=currentPageList.get(i).toString();

%>
		document.forms[0].countryNames.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
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
		alert('<fmt:message key="Please_enter_Country_Id" bundle="${lang}"/>')
	}
	else
	{
		var	index	=document.forms[0].countryNames.selectedIndex;
		firstTemp	=document.forms[0].countryNames.options[index].value;
		firstIndex	=firstTemp.indexOf('[');
		lastIndex	=firstTemp.indexOf(']');
		firstTemp	=firstTemp.substring(firstIndex+1,lastIndex);
		temp		=firstTemp.toString();
        var whereClause = "<%=whereClause%>"
		var rows = "<%=rows%>";
		if(whereClause=="")
		{
			window.opener.document.forms[0].countryId.value=temp;
			//window.opener.document.forms[0].countryId1.value=temp; Modified by rk it is reflecting in all setupmodule countryids before modifing this check all the setup country modify module
		}
		else if(whereClause=="CountryMaster")
    {
			window.opener.document.forms[0].countryId1.value=temp;
      window.opener.document.forms[0].countryId.value=temp;
    }
		else if(whereClause=="Bank")
			window.opener.document.forms[0].bankCountryId.value=temp;	
		else if(whereClause=="Consignee")
			window.opener.document.forms[0].consigneeCountryId.value=temp;	
		else if(whereClause=="Shipper" || whereClause=="CSR")//@@Modified by Yuvraj for CR_DHLQMS_1006
			window.opener.document.forms[0].<%=wheretoset%>.value=temp;		
		else if(whereClause=="custMultAdd")
		{
			if(rows==0)
			{
				window.opener.document.forms[0].countryIdd.value=temp;		
			}
			else
			{
//				Changed by murali on 31012005 regarding SPETI-2636
//				window.opener.document.forms[0].countryIdd[rows].value=temp;
				// @@ Murali Modified On 20050519 Regarding SPETI - 6660
				window.opener.document.forms[0].countryIdd<%=rows%>.value=temp;		
				// @@ Murali SPETI - 6660
//				End murali
			}

		}
		else if(whereClause=="fromCountry")
		{
			window.opener.document.forms[0].fromCountry.value=temp;
		}
		else if(whereClause=="toCountry")
		{
			window.opener.document.forms[0].toCountry.value=temp;
		}
		else if(whereClause=="ChargesGroupingCountryMaster")//Added by Anil.k for the Enhancement 231214 on 24Jan2011
		{
			var country = '<%=country%>';
			if(country=="originCountry")
			{
				window.opener.document.forms[0].originCountry.value=temp;
				window.opener.document.forms[0].originCountry.focus();
			}
			else if(country == "destination")
			{
				window.opener.document.forms[0].destination.value=temp;
				window.opener.document.forms[0].destination.focus();
			}
		}//Ended by Anil.k for Enhancement 231214 on 24Jan2011
		else
			window.opener.document.forms[0].withCountryId.value=temp;
			
				resetValues();	
	}
}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		if(document.forms[0].countryNames.selectedIndex == -1)
		{
			alert('<fmt:message key="Please_enter_Country_Id" bundle="${lang}"/>')
		}
		else
		{
			var	index	=document.forms[0].countryNames.selectedIndex;
			firstTemp	=document.forms[0].countryNames.options[index].value;
			firstIndex	=firstTemp.indexOf('[');
			lastIndex	=firstTemp.indexOf(']');
			firstTemp	=firstTemp.substring(firstIndex+1,lastIndex);
			temp		=firstTemp.toString();
			var whereClause = "<%=whereClause%>"
			if(whereClause=="")
				window.opener.document.forms[0].countryId.value=temp;
      else if(whereClause=="CountryMaster")
    {
			window.opener.document.forms[0].countryId1.value=temp;
      window.opener.document.forms[0].countryId.value=temp;
    }
			else if(whereClause=="Bank")
				window.opener.document.forms[0].bankCountryId.value=temp;	
			else if(whereClause=="Consignee")
				window.opener.document.forms[0].consigneeCountryId.value=temp;	
			else if(whereClause=="Shipper" || whereClause=="CSR")//@@Modified by Yuvraj for CR_DHLQMS_1006
				window.opener.document.forms[0].<%=wheretoset%>.value=temp;		
			else if(whereClause=="custMultAdd")
			{
				if(<%=request.getParameter("row")%>==0)
					window.opener.document.forms[0].countryIdd.value=temp;		
				else
					window.opener.document.forms[0].countryIdd[<%=request.getParameter("row")%>].value=temp;		

			}
			else if(whereClause=="ChargesGroupingCountryMaster")//Added by Anil.k for the Enhancement 231214 on 24Jan2011
			{
				var country = '<%=country%>';
				if(country=="originCountry")
					window.opener.document.forms[0].originCountry.value=temp;
				else if(country == "destination")
					window.opener.document.forms[0].destination.value=temp;
			}//Ended by Anil.k for Enhancement 231214 on 24Jan2011
			else
				window.opener.document.forms[0].withCountryId.value=temp;
				
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
  closeWindow = 'false';
}

function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=countryIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=countryIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateCountryNames()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2"><fmt:message key="Country_Ids" bundle="${lang}"/></font></b>
		</center>
		

<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="countryNames" onDblClick='setCountryNames()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value='<fmt:message key="Ok" bundle="${lang}"/>' name="OK" onClick=setCountryNames() class="input">
					<input type="button" value='<fmt:message key="Cancel" bundle="${lang}"/>' name="B2" onClick="resetValues()" class="input">
				</center>
				<TABLE cellSpacing=0 width=95%>   
		<tr  class="formdata"> 
		<td width=100% align='center'><fmt:message key="Pages" bundle="${lang}"/>: &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= Integer.parseInt(loginbean.getUserPreferences().getSegmentSize()) %>" fileName="<%= fileName %>"/>
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

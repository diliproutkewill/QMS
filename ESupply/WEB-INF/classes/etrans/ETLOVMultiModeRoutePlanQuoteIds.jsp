

<%@ page import="java.util.ArrayList,					
				java.util.Locale,
				com.foursoft.esupply.common.util.Logger,
				com.foursoft.etrans.common.routeplan.ejb.sls.ETMultiModeRoutePlanSessionHome,
				com.foursoft.etrans.common.routeplan.ejb.sls.ETMultiModeRoutePlanSession,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%
	ETMultiModeRoutePlanSessionHome home				=	null;
	ETMultiModeRoutePlanSession		remote				=	null;
	String 							fileName	        =   "ETLOVMultiModeRoutePlanQuoteIds.jsp"; 
	ArrayList						requiredAttributes	=	null;
	LOVListHandler					listHandler			=	null;	 
	String							pageIndex			=	null;	 
	String							values			    =	null;	 
	String							rows				=	null;
	String							searchString        =   request.getParameter("documentType");
	ArrayList						quoteList           =   new ArrayList();
	//String							whereToSet          =   request.getParameter("whereToSet");
	String							optionValue         =   null;
	//System.out.println("searchStringsearchStringsearchStringsearchStringsearchStringsearchStringsearchString :: "+searchString);
  String terminalId	= loginbean.getTerminalId();
	String userTerminalType=loginbean.getUserTerminalType(); 
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("quoteList");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
			e.printStackTrace();
			Logger.error(fileName,"Exception while getting ListHandler"+e.toString());
		}
	}
	if(listHandler == null)
	{
		requiredAttributes  =	new ArrayList();	
		rows				=	request.getParameter("row");

		if(request.getParameter("row")!=null)
			rows	=	request.getParameter("row");
		else
			rows	=	"";

		requiredAttributes.add(rows);					
			
		try
		{ 
			home                    =     (ETMultiModeRoutePlanSessionHome)loginbean.getEjbHome("ETMultiModeRoutePlanSessionBean");
			remote                  =     (ETMultiModeRoutePlanSession)home.create();
			searchString            =      searchString!=null?searchString:"";
// modofied by VLAKSHMI for issue  174469 on 26/06/09
			quoteList				=     remote.getQuoteIds(searchString,terminalId,userTerminalType);
				
			

			if(quoteList!=null)
			{
			   listHandler                     =	new LOVListHandler(quoteList,FoursoftConfig.NOOFRECORDSPERPAGE,requiredAttributes);
			   session.setAttribute("quoteList", listHandler);
			   listHandler = (LOVListHandler)session.getAttribute("quoteList");
			}
		}
		catch(Exception	e)
		{
			e.printStackTrace();
			Logger.error(fileName,"Exception while calling remote method", e.toString());
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


	try
	{        
		if(requiredAttributes!=null)
		{          
			rows         = (String)requiredAttributes.get(0);
		}
	}
	catch(Exception ex)
	{
	  Logger.error(fileName,"Error while getting rows : " +ex);
	}
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="../ESEventHandler.jsp" %>

<script language="javascript">
var isAttributeRemoved  = 'false';
function  populatePerKgIds()
{
<%

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
	    values     =(String)currentPageList.get(i);
		pageIndex	=	values;
        optionValue	=	values;
		System.out.println("pageIndex in jsp "+pageIndex);
		System.out.println("optionValueoptionValueoptionValueoptionValue::: "+optionValue);
%>
		document.forms[0].quoteId.options[<%= i %>] = new Option('<%=pageIndex%>','<%=optionValue%>');
<%
	}// for loop end

	if(currentPageList.size() > 0)
	{
		System.out.println("currentPageListcurrentPageList size ::: "+currentPageList.size());
%>          
			document.forms[0].quoteId.options[0].selected = true;	
			document.forms[0].quoteId.focus();
			
<%
	}else{
%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setQuoteId()
{
		if(document.forms[0].quoteId.selectedIndex == -1)
		{
			alert("Please select a Quote ID. ")
		}
		else
		{
			window.opener.document.forms[0].qouteId.value=document.forms[0].quoteId.value;			
			resetValues();		
			
			window.close();
		}
}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		setQuoteId();
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
      window.location.href	=	"../ESupplyRemoveAttribute.jsp?valueList=quoteId";
   }
}

function resetValues()
{
    isAttributeRemoved			=	'true';
    document.forms[0].action	=	"../ESupplyRemoveAttribute.jsp?valueList=quoteId";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populatePerKgIds()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">Quote Id's</font></b>
		</center>
<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="quoteId" onDblClick='setQuoteId()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value='Ok' name="OK" onClick=setQuoteId() class="input">
					<input type="button" value='Cancel' name="B2" onClick="resetValues()" class="input">
				</center>
				<TABLE cellSpacing=0 width=95%>   
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= FoursoftConfig.NOOFSEGMENTS %>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}// end of if loop
	else
	{// begin of else loop
%>
			<br><br>
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly >No Quote Id's Found</textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>

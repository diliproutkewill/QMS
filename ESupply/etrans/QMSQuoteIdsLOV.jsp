<%
/*
	Program Name	:QMSQuoteIdsLOV.jsp
	Module Name		:QMSSetup
	Task			:SalesPersonRegistration Master
	Sub Task		:LOV
	Author Name		:RamaKrishna Y
	Date Started	:June 28,2005
	Date Completed	:
	Date Modified	:
	Description		:
*/
%>

<%@ page import="java.util.ArrayList,					
				java.util.Locale,
				java.util.Hashtable,
				org.apache.log4j.Logger,
				com.qms.operations.quote.ejb.sls.QMSQuoteSession,
				com.qms.operations.quote.ejb.sls.QMSQuoteSessionHome,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.qms.operations.quote.dob.QuoteMasterDOB,
				com.foursoft.esupply.common.java.FoursoftConfig" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteIdsLOV.jsp"; %>


<%
  logger  = Logger.getLogger(FILE_NAME);	
	String 					  fileName	            =     "QMSQuoteIdsLOV.jsp";
	ArrayList			      requiredAttributes	=	  null;
	LOVListHandler		      listHandler			=	  null;	 
	QMSQuoteSession           remote				=     null;
	QMSQuoteSessionHome       home					=     null;
	String					  pageIndex			    =	  null;	 
	String					  values			    =	  null;	 
	String					  rows					=	  null;
	String                   searchString          =     request.getParameter("searchString");
	ArrayList                quoteList             =     new ArrayList();
	String                   customerId            =     request.getParameter("customerId");
	String                   originLoc			   =     request.getParameter("originLoc");
	String                   destination           =     request.getParameter("destLoc");
	String                   shipmentMode          =     request.getParameter("shipmentMode");
	String                   whereToSet            =     request.getParameter("whereToSet");
	String                   optionValue           =     null;
	QuoteMasterDOB			 masterDOB			   =	 null;
	Hashtable				 accessList			   =	 null;
	String					 operation			   =	 request.getParameter("operation");
	String					 buyRatesPermission	   =	 "Y";
	String					 accessLevel		   =	 null;
    
    if(whereToSet!=null)
	session.setAttribute("whereToSet",whereToSet);

	accessList  =  (Hashtable)session.getAttribute("accessList");

	if(accessList.get("10605")==null)
		buyRatesPermission	= "N";

	if("HO_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "H";
      else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
          accessLevel = "A";
      else
          accessLevel = "O";

		

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
			//Logger.error(fileName,"Exception while getting ListHandler"+e.toString());
      logger.error(fileName+"Exception while getting ListHandler"+e.toString());
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
			masterDOB				=	  new QuoteMasterDOB();
			
			masterDOB.setCustomerId(customerId);
			masterDOB.setUserId(loginbean.getEmpId());
			masterDOB.setBuyRatesPermission(buyRatesPermission);
			masterDOB.setOperation(operation);
			masterDOB.setTerminalId(loginbean.getTerminalId());
			masterDOB.setAccessLevel(accessLevel);
			masterDOB.setOriginLocation(originLoc);
			masterDOB.setDestLocation(destination);
			if(shipmentMode!=null && shipmentMode.trim().length()!=0)
				masterDOB.setShipmentMode(Integer.parseInt(shipmentMode));

			home                    =     (QMSQuoteSessionHome)loginbean.getEjbHome("QMSQuoteSessionBean");
			remote                  =     (QMSQuoteSession)home.create();
			searchString            =      searchString!=null?searchString:"";
			
			if("costingAdd".equals(operation))
			{
					quoteList				=     remote.getCostingQuoteIds(masterDOB,searchString);
			}
			else
			{
					quoteList				=     remote.getQuoteIds(masterDOB,searchString);
			}
				
			

			if(quoteList!=null)
			{
			   listHandler                     =	new LOVListHandler(quoteList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			   session.setAttribute("quoteList", listHandler);
			   listHandler = (LOVListHandler)session.getAttribute("quoteList");
			}
		}
		catch(Exception	e)
		{
			e.printStackTrace();
			//Logger.error(fileName,"Exception while calling remote method", e.toString());
      logger.error(fileName+"Exception while calling remote method"+ e.toString());
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
	  //Logger.error(fileName,"Error while getting rows : " +ex);
    logger.error(fileName+"Error while getting rows : " +ex);
	}
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>

<script language="javascript">
var isAttributeRemoved  = 'false';
<%
  String whereTSet=(String)session.getAttribute("whereToSet");
%>
function  populatePerKgIds()
{
<%

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
	    values     =(String)currentPageList.get(i);
		pageIndex=values;
        optionValue  =  values;
		//System.out.println("pageIndex  "+currentPageList.get(i));
%>
		document.forms[0].quoteId.options[ <%= i %> ] = new Option('<%= pageIndex %>','<%= optionValue %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
	{
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
			window.opener.document.forms[0].<%=whereTSet%>.value=document.forms[0].quoteId.value;			
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
      window.location.href	=	"ESupplyRemoveAttribute.jsp?valueList=quoteId";
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
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly >No Quote Id's Found</textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>

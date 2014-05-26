<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSLOVAllLevelTerminalIds.jsp
Product Name	: QMS
Module Name		: Charges master
Task		    : Adding/View/Modify/Delete ChargesMaster
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: 
Actor           :
Related Document: CR_DHLQMS_1005
--%>

<%@ page import="javax.naming.InitialContext,
				java.util.ArrayList,
				org.apache.log4j.Logger,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig,
				com.qms.operations.charges.ejb.sls.ChargeMasterSession,
				com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome,
				com.qms.operations.charges.java.BuySellChargesEnterIdDOB"
				%>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSLOVAllLevelTerminalIds.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList terminalList		=	null; //varibale to store levelIds
	int len						=	0;	  //varibale to store the size of the level Ids vector
	//String whereClause		=	null;
	LOVListHandler listHandler	=	null;
	ArrayList requiredAttributes= null;
    ArrayList	currentPageList	=	new ArrayList();
	String  operation			=	request.getParameter("Operation");
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"terminalId";
	String  index				=	(request.getParameter("index")!=null)?request.getParameter("index"):"1";
	String	searchString		=	(request.getParameter("searchString")!=null)?request.getParameter("searchString"):"";
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  fromWhere			=	(request.getParameter("fromWhere")!=null)?request.getParameter("fromWhere"):"chargemaster";
	String  multiple			=	"";
	int	    shipMode			=	0;
	String	shipModeStr			=	"";
	String	terminalId			=   "";
	
	BuySellChargesEnterIdDOB buySellChargesEnterIdDOB = null;
	
	//Logger.info(FILE_NAME,""+request.getContextPath());

	if(selection!=null && selection.equals(""))
	{
		selection	=	"single";
	}
	if(selection!=null && selection.equals("multiple"))
	{
		multiple	=	"multiple";
	}
	InitialContext initial				=	null; //object that represent initial context
	
	if(request.getParameter("pageNo")!=null && selection.equals("single"))
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("terminalList");
			requiredAttributes    = listHandler.requiredAttributes; 
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
			requiredAttributes  = new ArrayList();
			requiredAttributes.add(name);
			requiredAttributes.add(index);
			requiredAttributes.add(fromWhere);
			requiredAttributes.add(operation);
			requiredAttributes.add(selection);
			initial									=		new InitialContext();	// looking up JNDI

				buySellChargesEnterIdDOB	=	new BuySellChargesEnterIdDOB();
				//buySellChargesEnterIdDOB.setChargeId(chargeId);
				//buySellChargesEnterIdDOB.setChargeDescId(searchString);
				buySellChargesEnterIdDOB.setTerminalId(searchString);
				buySellChargesEnterIdDOB.setOperation(operation);
				ChargeMasterSessionHome home		=		(ChargeMasterSessionHome)initial.lookup("ChargeMasterSession");
				ChargeMasterSession		remote		=		(ChargeMasterSession)home.create();
				terminalList     =     remote.getTerminalListForBuyRSellCharges(buySellChargesEnterIdDOB,loginbean);				
			
			if(terminalList != null)
			{
					len	= terminalList.size();
			}
			if(terminalList!=null && selection.equals("single"))
			{
			  listHandler                     = new LOVListHandler(terminalList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			  session.setAttribute("terminalList", listHandler);
			  listHandler = (LOVListHandler)session.getAttribute("terminalList");
			}
	   }
   }catch(Exception ee)
	{
		//Logger.error(FILE_NAME," ------ : ", ee.toString());
    logger.error(FILE_NAME+" ------ : "+ ee.toString());
	}

	String pageNo			= request.getParameter("pageNo");
	String 		fileName	= "QMSLOVAllLevelTerminalIds.jsp";
	if(pageNo == null && multiple.equals(""))
	{
		pageNo = "1";
		listHandler.currentPageIndex = 1;
	}
	else if(selection.equals("single"))
	{
		listHandler.currentPageIndex = Integer.parseInt(pageNo);
	}
	if(selection.equals("single"))
	{
		currentPageList	= listHandler.getCurrentPageData();
		name		=	(String)requiredAttributes.get(0);
		index		=	(String)requiredAttributes.get(1);
		fromWhere	=	(String)requiredAttributes.get(2);
		operation	=	(String)requiredAttributes.get(3);
		selection	=	(String)requiredAttributes.get(4);
  
	}else if(selection.equals("multiple"))
	{
		currentPageList = terminalList;
	}
	
%>
<html>
<head>
<title>ServiceLevel LOV</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateTerminalIds()
{
<%
		for( int i=0;i<currentPageList.size();i++ )
		{// begin of for loop
			terminalId		=	currentPageList.get(i).toString();
%>
			window.document.forms[0].terminalIdNames.options[ <%= i %> ] = new Option('<%= terminalId %>','<%= terminalId %>','<%= terminalId %>');
<%
		}// end of for loop
	if(currentPageList.size() > 0)
	{
%>
		
			window.document.forms[0].terminalIdNames.options[0].selected = true;	
			window.document.forms[0].terminalIdNames.focus();
<%
	}else{
%>
	window.document.forms[0].B2.focus();
<%
	}	
%>
}

function setTerminalIdNames()
{
	var count =0;
	var chargeValue	='';
	var chargeDesc	='';
	if (document.forms[0].terminalIdNames.selectedIndex == -1)
	{
		alert("Please select a Terminal Id")
	}
	else
	{
		for(var i=0;i<document.forms[0].terminalIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].terminalIdNames.options[i].selected)
			{
				count++;
			}
		}
		var terminalTemp = new Array(count);
		var sIndex=0;

		for(var i=0;i<document.forms[0].terminalIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].terminalIdNames.options[i].selected)
			{
				strTemp		=	document.forms[0].terminalIdNames.options[i].value;
				terminalTemp[sIndex]	=	strTemp;
				sIndex++;
			}
		}
<%
		if(multiple!=null && multiple.equals("multiple"))
		{	
%>
			window.opener.setTerminalIdValues(terminalTemp,'<%=name%>');
<%
		}else{
%>
					window.opener.document.forms[0].<%=name%>.value=terminalTemp[0];
<%			
			 }
%>
		resetValues();

	}

}


function onEnterKey()
{
	var count =0;
	if(event.keyCode == 13)
	{
			setTerminalIdNames();
	}
	if(event.keyCode == 27 ){
		resetValues();
	}

}
function onEscKey(){
	if(event.keyCode == 27 ){
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
      window.location.href="ESupplyRemoveAttribute.jsp?valueList=terminalList";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="ESupplyRemoveAttribute.jsp?valueList=terminalList";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body onLoad="populateTerminalIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'><font face="Verdana" size="2">
<center><b>Terminal Ids </b></center>
</font>
<form>
<%
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
	<center>
	<select size="10" name="terminalIdNames" selected=0 class="select" onDblClick='setTerminalIdNames()' onKeyPress='onEnterKey()' <%=multiple%>></select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setTerminalIdNames()" class="input">
	<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
	</center>
<%
		if(multiple.equals(""))
		{
%>
			<TABLE cellSpacing=0 width=95%>
			<tr  class="formdata"> 
			<td width=100% align='center'>Pages : &nbsp;
			
			<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
			</td>
			</tr>	
			</table>
<%
		}
%>  
<%
	}
	else
	{
%>
	<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No Terminal Ids available</textarea></center>
	<br>
	<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>

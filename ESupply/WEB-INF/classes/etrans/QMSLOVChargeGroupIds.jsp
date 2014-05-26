<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSLOVChargeIds.jsp
Product Name	: QMS
Module Name		: Charges master
Task		    : Adding/View/Modify/Delete ChargesMaster
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" ChargesMaster information
Actor           :
Related Document: CR_DHLQMS_1005
--%>

<%@ page import="javax.naming.InitialContext,
				java.util.ArrayList,
				org.apache.log4j.Logger,
				com.qms.setup.ejb.sls.SetUpSession,
				com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.setup.country.bean.CountryMaster,
				com.qms.operations.charges.java.BuySellChargesEnterIdDOB,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>  
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSLOVChargeGroupIds.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList chargeGroupList		=	null; //varibale to store levelIds
	int len						=	0;	  //varibale to store the size of the level Ids vector
	//String whereClause		=	null;
		LOVListHandler listHandler	=	null;
			ArrayList requiredAttributes = null;
    ArrayList	currentPageList	=	new ArrayList();
	String  operation			=	request.getParameter("Operation");
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"chargeGroupId";
	String	searchString		=	(request.getParameter("searchString")!=null)?request.getParameter("searchString"):"";
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  fromWhere			=	(request.getParameter("fromWhere")!=null)?request.getParameter("fromWhere"):"";
	String  terminalId			=	request.getParameter("terminalId");
	String	accessLevel			=	request.getParameter("accessLevel");
	String  shipmentMode		=	request.getParameter("shipmentMode");
	String  originLocation		=	request.getParameter("originLocation");//Added by Anil.k for Enhancement 231214 on 25Jan2011
	String  destLocation		=	request.getParameter("destLocation");//Added by Anil.k for Enhancement 231214 on 25Jan2011
	String  multiple			=	"";
  ////Added by VLAKSHMI for CR #170761 on 20090626
	String chargeId = (request.getParameter("chargeId")!=null)?request.getParameter("chargeId"):"";
	String chargeIdDesc = (request.getParameter("chargeIdDesc")!=null)?request.getParameter("chargeIdDesc"):"";
  // end for  for CR #170761
	BuySellChargesEnterIdDOB buySellChargesEnterIdDOB = null;
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
			listHandler           = (LOVListHandler)session.getAttribute("chargeGroupList");
			requiredAttributes    = listHandler.requiredAttributes;
      name                  = (String)requiredAttributes.get(0);
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
	try
	{
	//	System.out.println("fromWhere="+fromWhere);
		if(terminalId==null || terminalId.equals(""))
			{	terminalId = loginbean.getTerminalId();}

	 if(listHandler == null)
	{
	requiredAttributes  = new ArrayList();
  requiredAttributes.add(name);
	initial								=		new InitialContext();	// looking up JNDI
	SetUpSessionHome	home			=		(SetUpSessionHome )initial.lookup("SetUpSessionBean" );	// looking up SetUpSessionBean	
	SetUpSession	    remote			=		(SetUpSession)home.create();
	if(fromWhere!=null && fromWhere.equals("chargeGroupEnterId"))
	{	
		//logger.info("chargeId"+chargeId);
		buySellChargesEnterIdDOB    = new BuySellChargesEnterIdDOB();
		buySellChargesEnterIdDOB.setChargeGroupId(searchString);
		buySellChargesEnterIdDOB.setOperation(operation);
		buySellChargesEnterIdDOB.setTerminalId(terminalId);
		buySellChargesEnterIdDOB.setChargeId(chargeId); //added by VLAKSHMI for CR #170761 on 20090626
		buySellChargesEnterIdDOB.setChargeDescId(chargeIdDesc); //added by VLAKSHMI for CR #170761 on 20090626
		chargeGroupList	=	remote.getAllChargeGroupIds(buySellChargesEnterIdDOB,loginbean);
		
	}
	else if(fromWhere!=null && fromWhere.equals("MultiQuote"))//Added by Anil.k for Enhancement 231214 on 25Jan2011
	{
	//	System.out.println(destLocation+"22222222222"+originLocation);
		chargeGroupList	=	remote.getAllChargeGroupIds(searchString, terminalId, shipmentMode, accessLevel, originLocation, destLocation);
	}
	else if(fromWhere!=null && fromWhere.equals("Quote"))
	{
	//	System.out.println(destLocation+"22222222222"+originLocation);
		chargeGroupList	=	remote.getAllChargeGroupIds(searchString, terminalId, shipmentMode, accessLevel, originLocation, destLocation);
	}//End
	else
	{	
		chargeGroupList	=	remote.getAllChargeGroupIds(searchString, terminalId, shipmentMode, accessLevel);
	}
	if(chargeGroupList != null)
    {
			len	= chargeGroupList.size();
    }
    if(chargeGroupList!=null && selection.equals("single"))
    {
      listHandler                     = new LOVListHandler(chargeGroupList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
      session.setAttribute("chargeGroupList", listHandler);
      listHandler = (LOVListHandler)session.getAttribute("chargeGroupList");
    }
   }
   } 
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME," ------ : ", ee.toString());
    logger.error(FILE_NAME+" ------ : "+ ee.toString());
	}

	String pageNo			= request.getParameter("pageNo");
	String 		fileName	= "QMSLOVChargeGroupIds.jsp";
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
  
	}else if(selection.equals("multiple"))
	{
		currentPageList = chargeGroupList;
	}
	
%>
<html>
<head>
<title>ChargeGroupId LOV</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateChargeIds()
{
<%
		for( int i=0;i<currentPageList.size();i++ )
	{// begin of for loop
		String chargeGroupId = currentPageList.get(i).toString();
%>
		window.document.forms[0].chargeGroupIdNames.options[ <%= i %> ] = new Option('<%= chargeGroupId %>','<%= chargeGroupId %>','<%= chargeGroupId %>');
<%
	}// end of for loop
	if(currentPageList.size() > 0)
	{
%>
		
			window.document.forms[0].chargeGroupIdNames.options[0].selected = true;	
			window.document.forms[0].chargeGroupIdNames.focus();
<%
	}else{
%>
	window.document.forms[0].B2.focus();
<%
	}	
%>
}

function setChargeGroupIdNames()
{
	var count =0;
	if (document.forms[0].chargeGroupIdNames.selectedIndex == -1)
	{
		alert("Please select a ChargeGroupId")
	}
	else
	{
		for(var i=0;i<document.forms[0].chargeGroupIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].chargeGroupIdNames.options[i].selected)
			{
				count++;
			}
		}
		var chargeTemp = new Array(count);
		var sIndex=0;

		for(var i=0;i<document.forms[0].chargeGroupIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].chargeGroupIdNames.options[i].selected)
			{
				strTemp		=	document.forms[0].chargeGroupIdNames.options[i].value;
				chargeTemp[sIndex]	=	strTemp;
				sIndex++;
			}
		}
<%
		if(multiple!=null && multiple.equals("multiple"))
		{	
%>
			window.opener.setChargeGroupIdValues(chargeTemp,'<%=name%>');
<%
		}else{
%>
			opener.parent.text.document.forms[0].<%=name%>.value=chargeTemp[0];
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
			if (document.forms[0].chargeGroupIdNames.selectedIndex == -1)
			{
				alert("Please select a chargeGroupId")
			}
			else
			{
				for(var i=0;i<document.forms[0].chargeGroupIdNames.options.length;i++)
				{
					if(document.forms[0].chargeGroupIdNames.options[i].selected)
					{
						count++;
					}
				}
				var chargeTemp = new Array(count);
				var sIndex=0;
				for(var i=0;i<document.forms[0].chargeGroupIdNames.options.length;i++)
				{
					if(document.forms[0].chargeGroupIdNames.options[i].selected)
					{
						strTemp		=	document.forms[0].chargeGroupIdNames.options[i].value;
						chargeTemp[sIndex]	=	strTemp;
						sIndex++;
					}
				}
<%
				if(multiple!=null && multiple.equals("multiple"))
				{	
%>
					window.opener.setChargeGroupIdValues(chargeTemp);//For multiple selection the calling page should have this function to assign the values in the array..
<%
				}else{
%>
					opener.parent.text.document.forms[0].<%=name%>.value=chargeTemp[0];
<%
				}
%>
			resetValues();
			}
		
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=levelIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=levelIds";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad="populateChargeIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'><font face="Verdana" size="2">
<center><b>ChargeGroup Ids </b></center>
</font>
<form>
<%
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
	<center>
	<select size="10" name="chargeGroupIdNames" selected=0 class="select" onDblClick='setChargeGroupIdNames()' onKeyPress='onEnterKey()' <%=multiple%>></select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setChargeGroupIdNames()" class="input">
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
	<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No ChargeGroupIds available</textarea></center>
	<br>
	<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>

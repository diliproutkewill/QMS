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
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig,
				com.qms.operations.charges.ejb.sls.ChargeMasterSession,
				com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome,
				com.qms.operations.charges.java.BuySellChargesEnterIdDOB"
				%>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSLOVChargeIds.jsp ";
%>

<%  
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList chargeList		=	null; //varibale to store levelIds
	int len						=	0;	  //varibale to store the size of the level Ids vector
	//String whereClause		=	null;
	LOVListHandler listHandler	=	null;
	ArrayList requiredAttributes= null;
    ArrayList	currentPageList	=	new ArrayList();
	String  operation			=	request.getParameter("Operation");
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"chargeId";
	String  index				=	(request.getParameter("index")!=null)?request.getParameter("index"):"1";
	String	searchString		=	(request.getParameter("searchString")!=null)?request.getParameter("searchString"):"";
	//String  chargeBasisId		=	(request.getParameter("chargeBasisId")!=null)?request.getParameter("chargeBasisId"):"";
	String  chargeDescId		=   (request.getParameter("chargeDescriptionId")!=null)?request.getParameter("chargeDescriptionId"):"";
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  costIncurred		=	(request.getParameter("costincurred")!=null)?request.getParameter("costincurred"):"";
	String  fromWhere			=	(request.getParameter("fromWhere")!=null)?request.getParameter("fromWhere"):"chargemaster";
	String  aircheck			=	(request.getParameter("aircheck")!=null)?request.getParameter("aircheck"):"";
	String  seacheck			=	(request.getParameter("seacheck")!=null)?request.getParameter("seacheck"):"";
	String  truckcheck			=	(request.getParameter("truckcheck")!=null)?request.getParameter("truckcheck"):"";
	String  terminalId          =     (request.getParameter("terminalId")!=null?request.getParameter("terminalId"):"");
	String  multiple			=	"";
	int	    shipMode			=	0;
	String	shipModeStr			=	"";
	String	chargeId			=   "";
	String	chargeIdvalue		=	"";
	BuySellChargesEnterIdDOB buySellChargesEnterIdDOB = null;
  //Added by VLAKSHMI for CR #170761 on 20090626
	String chargeGroupId = (request.getParameter("chargeGroupId")!=null)?request.getParameter("chargeGroupId"):"";
	//Logger.info(FILE_NAME,""+request.getContextPath());

	if(selection!=null && selection.equals(""))
	{
		selection	=	"single";
	}
	if(selection!=null && selection.equals("multiple"))
	{
		multiple	=	"multiple";
	}
					if(aircheck.equals("checked"))
					{
						if(seacheck.equals("checked"))
						{
							if(truckcheck.equals("checked"))
							{
								shipMode	=	7;
								//shipModeStr	=	"1,2,3,4,5,6,7";
								shipModeStr	=	"7";
							}else
							{
								shipMode	=	3;
								shipModeStr	=	"3,7";
							}
						}else if(truckcheck.equals("checked"))
						{
							shipMode	=	5;
							shipModeStr	=	"5,7";
						}else
						{
							shipMode	=	1;
							shipModeStr	=	"1,3,5,7";
						}
					}else if(seacheck.equals("checked"))
					{
						if(truckcheck.equals("checked"))
						{
							shipMode	=	6;
							//shipModeStr	=	"2,3,4,5,6,7";
							shipModeStr	=	"6,7";
						}else
						{
							shipMode	=	2;
							shipModeStr	=	"2,3,6,7";
						}
					}else if(truckcheck.equals("checked"))
					{
						shipMode	=	4;
						shipModeStr	=	"4,5,6,7";
					}

	InitialContext initial				=	null; //object that represent initial context
	
	if(request.getParameter("pageNo")!=null && selection.equals("single"))
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("chargeList");
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
	//		logger.info("fromWhere"+fromWhere);
			if("buychargesenterid".equals(fromWhere) || "sellchargesenterid".equals(fromWhere))
			{
				buySellChargesEnterIdDOB            =		new BuySellChargesEnterIdDOB();
				buySellChargesEnterIdDOB.setChargeId(searchString);
				buySellChargesEnterIdDOB.setTerminalId((terminalId==null || terminalId.equals(""))?loginbean.getTerminalId():terminalId);
				buySellChargesEnterIdDOB.setChargeDescId(chargeDescId);
				buySellChargesEnterIdDOB.setFromWhere(fromWhere);
				buySellChargesEnterIdDOB.setChargeGroupId(chargeGroupId);////Added by VLAKSHMI for CR #170761 on 20090626
				//logger.info("chargeGroupId"+chargeGroupId);
				ChargeMasterSessionHome home		=		(ChargeMasterSessionHome)initial.lookup("ChargeMasterSession");
				ChargeMasterSession		remote		=		(ChargeMasterSession)home.create();
				//chargeList	=		remote.getBuyChargeIds(searchString,fromWhere,chargeDescId,loginbean);

				chargeList							=	remote.getBuySellChargeIds(buySellChargesEnterIdDOB,loginbean);
			}else if("chargeDescriptionEnterId".equals(fromWhere))
			{
				ChargeMasterSessionHome home		=		(ChargeMasterSessionHome)initial.lookup("ChargeMasterSession");
				ChargeMasterSession		remote		=		(ChargeMasterSession)home.create();
				if(terminalId==null || terminalId.equals(""))
					{	terminalId = loginbean.getTerminalId();}
				chargeList							=	remote.getChargeIdsForChargeDesc(searchString,terminalId);
			}
			else
			{
				//logger.info("chargeGroupId::::::::"+chargeGroupId);
				SetUpSessionHome	home			=		(SetUpSessionHome )initial.lookup("SetUpSessionBean" );	// looking up SetUpSessionBean	
				SetUpSession	    remote			=		(SetUpSession)home.create(); 
        ////Modified by VLAKSHMI for CR #170761 on 20090626
									chargeList 		=		remote.getAllChargeIds(searchString,fromWhere,shipModeStr,operation,loginbean,chargeGroupId);
			}
			if(chargeList != null)
			{
					len	= chargeList.size();
			}
			if(chargeList!=null && selection.equals("single"))
			{
			  listHandler                     = new LOVListHandler(chargeList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			  session.setAttribute("chargeList", listHandler);
			  listHandler = (LOVListHandler)session.getAttribute("chargeList");
			}
	   }
   }catch(Exception ee)
	{
		//Logger.error(FILE_NAME," ------ : ", ee.toString());
    logger.error(FILE_NAME+" ------ : "+ ee.toString());
	}

	String pageNo			= request.getParameter("pageNo");
	String 		fileName	= "QMSLOVChargeIds.jsp";
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
		currentPageList = chargeList;
	}
	
%>
<html>
<head>
<title>ChargeIds LOV</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateChargeIds()
{
	//alert(populateChargeIds)
<%
		for( int i=0;i<currentPageList.size();i++ )
		{// begin of for loop
			chargeId		=	currentPageList.get(i).toString();
			if("buycharges".equals(fromWhere) || "sellcharges".equals(fromWhere))
				{	chargeIdvalue	=	chargeId;}
			else if("chargegroup".equals(fromWhere) || "chargeDescription".equals(fromWhere) )
				{	chargeIdvalue	=	chargeId.substring(0,chargeId.indexOf("("));}
			else
			{
				 if(chargeId.indexOf("--")>0)
					chargeIdvalue	=	chargeId.substring(0,chargeId.indexOf("--"));
				 else
					chargeIdvalue	=	chargeId;
			}
%>
			window.document.forms[0].chargeIdNames.options[ <%= i %> ] = new Option('<%= chargeId %>','<%= chargeIdvalue %>','<%= chargeIdvalue %>');
<%
		}// end of for loop
	if(currentPageList.size() > 0)
	{
%>
		
			window.document.forms[0].chargeIdNames.options[0].selected = true;	
			window.document.forms[0].chargeIdNames.focus();
<%
	}else{
%>
	window.document.forms[0].B2.focus();
<%
	}	
%>
}

function setChargeIdNames()
{
	var count =0;
	var chargeValue	='';
	var chargeDesc	='';
	if (document.forms[0].chargeIdNames.selectedIndex == -1)
	{
		alert("Please select a Charge Id")
	}
	else
	{
		for(var i=0;i<document.forms[0].chargeIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].chargeIdNames.options[i].selected)
			{
				count++;
			}
		}
		var chargeTemp = new Array(count);
		var sIndex=0;

		for(var i=0;i<document.forms[0].chargeIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].chargeIdNames.options[i].selected)
			{
				strTemp		=	document.forms[0].chargeIdNames.options[i].value;
				chargeTemp[sIndex]	=	strTemp;
				sIndex++;
			}
		}
<%
		if(multiple!=null && multiple.equals("multiple"))
		{	
%>
			window.opener.setChargeIdValues(chargeTemp,'<%=name%>');
<%
		}else{
			  if("buycharges".equals(fromWhere))
			  {

%>
					chargeValue		=	chargeTemp[0].substring(0,chargeTemp[0].indexOf("["));
					if(chargeTemp[0].indexOf("--")>0)
						chargeDesc		=	chargeTemp[0].substring(chargeTemp[0].indexOf("--")+2);
					else
						chargeDesc		=	"";
					window.opener.document.getElementById("<%=name%>").value=chargeValue;
					//window.opener.document.getElementById("chargeDescription<%=index%>").value=chargeDesc;
<%
			  }else if("sellcharges".equals(fromWhere))
			  {

%>
					chargeValue		=	chargeTemp[0].substring(0,chargeTemp[0].indexOf("["));
					if(chargeTemp[0].indexOf("--")>0)
						chargeDesc		=	chargeTemp[0].substring(chargeTemp[0].indexOf("--")+2);
					else
						chargeDesc		=	"";
					window.opener.document.getElementById("<%=name%><%=index%>").value=chargeValue;
					//window.opener.document.getElementById("chargeDescription<%=index%>").value=chargeDesc;
					window.opener.document.forms[0].<%=name%>.onchange();//Added to call onBlurr events on placing the data
<%
			  }
			 else if ("chargeDescriptionEnterId".equalsIgnoreCase(fromWhere))
			{
%>				
				 if(chargeTemp[0].indexOf("(")!=-1)
					window.opener.document.forms[0].<%=name%>.value=chargeTemp[0].substring(0,chargeTemp[0].indexOf("("));
				else window.opener.document.forms[0].<%=name%>.value=chargeTemp[0]; 
<%			}
			 else
			{
%>
					window.opener.document.forms[0].<%=name%>.value=chargeTemp[0];
<%			
			 }
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
			setChargeIdNames();
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
      window.location.href="<%=request.getContextPath()%>/ESupplyRemoveAttribute.jsp?valueList=chargeList";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="<%=request.getContextPath()%>/ESupplyRemoveAttribute.jsp?valueList=chargeList";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body onLoad="populateChargeIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'><font face="Verdana" size="2">
<center><b>Charge Ids </b></center>
</font>
<form>
<%
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
	<center>
	<select size="10" name="chargeIdNames" selected=0 class="select" onDblClick='setChargeIdNames()' onKeyPress='onEnterKey()' <%=multiple%>></select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setChargeIdNames()" class="input">
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
	<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No Charge Ids available</textarea></center>
	<br>
	<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>

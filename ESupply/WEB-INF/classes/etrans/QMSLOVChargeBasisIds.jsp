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
				com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome"
				%>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSLOVChargeBasisIds.jsp";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList chargeBasisList		=	null; //varibale to store levelIds
	int len						=	0;	  //varibale to store the size of the level Ids vector
	//String whereClause		=	null;
		LOVListHandler listHandler	=	null;
			ArrayList requiredAttributes = null;
    ArrayList	currentPageList	=	new ArrayList();
	String  operation			=	request.getParameter("Operation");
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"chargeBasisId";
	String  index				=	(request.getParameter("index")!=null)?request.getParameter("index"):"1";
	String  fromWhere			=	(request.getParameter("fromWhere")!=null)?request.getParameter("fromWhere"):"";
	String	searchString		=	(request.getParameter("searchString")!=null)?request.getParameter("searchString"):"";
	String	chargeId			=	(request.getParameter("chargeId")!=null)?request.getParameter("chargeId"):"";
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  multiple			=	"";
	String  chargeBasisId		=	"";
	String  chargeBasisIdValue	=	"";

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
			listHandler           = (LOVListHandler)session.getAttribute("chargeBasisList");
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

			initial								=		new InitialContext();	// looking up JNDI
			if("buychargesenterid".equals(fromWhere) || "sellchargesenterid".equals(fromWhere))
			{
				ChargeMasterSessionHome home		=		(ChargeMasterSessionHome)initial.lookup("ChargeMasterSession");
				ChargeMasterSession		remote		=		(ChargeMasterSession)home.create();
									chargeBasisList	=		remote.getBuyChargeBasisIds(searchString,fromWhere,chargeId,loginbean);

			}else
			{
				SetUpSessionHome	home			=		(SetUpSessionHome )initial.lookup("SetUpSessionBean" );	// looking up SetUpSessionBean	
				SetUpSession	    remote			=		(SetUpSession)home.create();
				chargeBasisList						=		remote.getAllChargeBasisIds(searchString,fromWhere,operation,loginbean);
			}
			if(chargeBasisList != null)
			{
					len	= chargeBasisList.size();
			}
			if(chargeBasisList!=null && selection.equals("single"))
			{
			  listHandler                     = new LOVListHandler(chargeBasisList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			  session.setAttribute("chargeBasisList", listHandler);
			  listHandler = (LOVListHandler)session.getAttribute("chargeBasisList");
			}
		 }
    } 
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME," ------ : ", ee.toString());
    logger.error(FILE_NAME+" ------ : "+ ee.toString());
	}

	String pageNo			= request.getParameter("pageNo");
	String 		fileName	= "QMSLOVChargeBasisIds.jsp";
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
		currentPageList = chargeBasisList;
	}
	
%>
<html>
<head>
<title>ChargeBasis LOV</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateChargeBasisIds()
{
<%
		for( int i=0;i<currentPageList.size();i++ )
		{// begin of for loop
			chargeBasisId = currentPageList.get(i).toString();
			if("buychargesenterid".equals(fromWhere) || "sellchargesenterid".equals(fromWhere))
				{	chargeBasisIdValue	=	chargeBasisId.substring(0,chargeBasisId.indexOf("--"));}
			else if("buycharges".equals(fromWhere) || "sellcharges".equals(fromWhere))
				{	
					chargeBasisIdValue	=	chargeBasisId;
					chargeBasisId       =  chargeBasisId.substring(0,chargeBasisId.indexOf("@"));
					
				}else
				{
					chargeBasisIdValue = chargeBasisId;

				}
%>
			window.document.forms[0].chargeBasisIdNames.options[ <%= i %> ] = new Option('<%= chargeBasisId %>','<%= chargeBasisIdValue %>','<%= chargeBasisIdValue %>');
<%
		}// end of for loop
	if(currentPageList.size() > 0)
	{
%>
		
			window.document.forms[0].chargeBasisIdNames.options[0].selected = true;	
			window.document.forms[0].chargeBasisIdNames.focus();
<%
	}else{
%>
	window.document.forms[0].B2.focus();
<%
	}	
%>
}

function setChargeBasisIdNames()
{
	var count =0;
	if (document.forms[0].chargeBasisIdNames.selectedIndex == -1)
	{
		alert("Please select a ChargeBasisId")
	}
	else
	{
		for(var i=0;i<document.forms[0].chargeBasisIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].chargeBasisIdNames.options[i].selected)
			{
				count++;
			}
		}
		var chargeTemp = new Array(count);
		var sIndex=0;

		for(var i=0;i<document.forms[0].chargeBasisIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].chargeBasisIdNames.options[i].selected)
			{
				strTemp		=	document.forms[0].chargeBasisIdNames.options[i].value;
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
			  if("buycharges".equals(fromWhere))
			  {

%>
					chargeValue		=	chargeTemp[0].substring(0,chargeTemp[0].indexOf("--"));
					if(chargeTemp[0].indexOf("--")>0)
						chargeDesc		=	chargeTemp[0].substring(chargeTemp[0].indexOf("--")+2,chargeTemp[0].indexOf("@"));
					else
						chargeDesc = '';

					window.opener.document.getElementById("<%=name%>").value=chargeValue;
					window.opener.document.getElementById("primaryUnit<%=index%>").value=chargeTemp[0].substring(chargeTemp[0].indexOf("@")+1,chargeTemp[0].lastIndexOf("@"));
					//alert('primary unit::'+window.opener.document.getElementById("primaryUnit<%=index%>").value);
					window.opener.document.getElementById("secondaryUnit<%=index%>").value=chargeTemp[0].substring(chargeTemp[0].lastIndexOf("@")+1,chargeTemp[0].length);

					//alert('secondary unit::'+window.opener.document.getElementById("secondaryUnit<%=index%>").value);
					window.opener.document.getElementById("chargeBasisDescription<%=index%>").value=chargeDesc;


<%
			  }else if("sellcharges".equals(fromWhere))
			  {

%>
					chargeValue		=	chargeTemp[0].substring(0,chargeTemp[0].indexOf("--"));
					if(chargeTemp[0].indexOf("--")>0)
						chargeDesc		=	chargeTemp[0].substring(chargeTemp[0].indexOf("--")+2,chargeTemp[0].indexOf("@"));
					else
						chargeDesc		=	'';
					window.opener.document.getElementById("<%=name%><%=index%>").value=chargeValue;
					window.opener.document.getElementById("primaryUnit<%=index%>").value=chargeTemp[0].substring(chargeTemp[0].indexOf("@")+1,chargeTemp[0].lastIndexOf("@"));
					window.opener.document.getElementById("secondaryUnit<%=index%>").value=chargeTemp[0].substring(chargeTemp[0].lastIndexOf("@")+1,chargeTemp[0].length);
					window.opener.document.getElementById("chargeBasisDescription<%=index%>").value=chargeDesc;
					window.opener.document.forms[0].<%=name%>.onchange();//Added to call onBlurr events on placing the data
<%
			  }else{
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
		setChargeBasisIdNames();
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=chargeBasisList";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=chargeBasisList";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body onLoad="populateChargeBasisIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'><font face="Verdana" size="2">
<center><b>Charge Basis Ids </b></center>
</font>
<form>
<%
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
	<center>
	<select size="10" name="chargeBasisIdNames" selected=0 class="select" onDblClick='setChargeBasisIdNames()' onKeyPress='onEnterKey()' <%=multiple%>></select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setChargeBasisIdNames()" class="input">
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
	<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No ChargeBasisIds available</textarea></center>
	<br>
	<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>

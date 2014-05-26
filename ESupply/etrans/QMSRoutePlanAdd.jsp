<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRoutePlanAdd.jsp
	Product Name	: QMS
	Module Name		: Route Plan
	Task		    : Adding Route plan
	Date started	: 26-10-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Madhusudhan Reddy .Y
	Description		: The application "Add" Route Plan
	Actor           :
	Related Document: CR_DHLQMS_1002

--%>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanDtlDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSRoutePlanAdd.jsp";
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
	ArrayList			keyValueList			=   new ArrayList();
	ETMultiModeRoutePlanDtlDOB routePlanDob		=	null;
	ErrorMessage		errorMessageObject		=   null;
	ArrayList			routeList				=	null;
	String				operation				=	null;
	String				documentType			=	null;
	String				shipmentMode			=	null;
	long				roughtPlanId			=	0;
	String				orignLoc				=	null;
	String				destinationLoc			=	null;
	int					shipMode				=	0;
	String				legType					=	null;
	String				quoteId					=	null;
	String				remarks					=	null;
	String				contextPath				=	null;
	try
	{
		operation		= request.getParameter("Operation");
		contextPath		= request.getContextPath();
		documentType	= request.getParameter("documentType");
		shipmentMode	= request.getParameter("ShipmentMode");
		quoteId			= request.getParameter("qouteId");
		routeList	=	(ArrayList)session.getAttribute("RoutePlanDtls");
		session.removeAttribute("RoutePlanDtls");
		//System.out.println("routeList.sizerouteList.sizerouteList.sizerouteList.size :: "+routeList.size());
		///System.out.println("quoteIdquoteIdquoteIdquoteIdquoteIdquoteIdquoteIdquoteId :: "+quoteId);
	
%>


<html>

<head>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>New Page 1</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script src ='../html/eSupply.js'></script>
<script>
function validateForm()
{
	var orginLocId				=	document.getElementsByName("orginLocId");
	var destinationLocId		=	document.getElementsByName("destinationLocId");
	var orginLocIdLength		=	orginLocId.length;
	var destinationLocIdLength	=	destinationLocId.length;
	for( i=0;i<orginLocIdLength;i++)
	{
		if(orginLocId[i].value.length==0)
		{
			alert("Please Select/Enter Orginlocation.");
			orginLocId[i].focus();
			return false;
		}
	}
	for( i=0;i<destinationLocIdLength;i++)
	{
		if(destinationLocId[i].value.length==0)
		{
			alert("Please Select/Enter Destinationlocation.");
			destinationLocId[i].focus();
			return false;
		}
		if(orginLocId[i].value==destinationLocId[i].value)
		{
			alert("Dont select Orginlocation and Destinationlocation same.");
			destinationLocId[i].focus();
			return false;
		}
	}

	document.getElementById("shipmentMode1").disabled	=	false;	
	document.getElementById("orginLocId1").readOnly		=	false;
	document.getElementById("orgBut1").disabled			=	false;
	return true;
}
function initialize()
{

	importXML('<%=request.getContextPath()%>/html/RouteplanContent.xml');	
}
function initializeDynamicContentRows() 
{ 
	var routePlanTableObj	= document.getElementById("routePlanTable");
<%	
	if(routeList!=null && routeList.size()>0)
	{
		
		int	routeListSize	=	routeList.size();
		for(int i=0;i<routeListSize;i++)
		{
			routePlanDob	=	(ETMultiModeRoutePlanDtlDOB)routeList.get(i);
			roughtPlanId	=	routePlanDob.getRoutePlanId();
			orignLoc		=	routePlanDob.getOrgLoc();
			destinationLoc	=	routePlanDob.getDestLoc();
			shipMode		=	routePlanDob.getShipmentMode();
			legType			=	routePlanDob.getLegType();
			remarks			=	routePlanDob.getRemarks();
%>
			createDynamicContentRow(routePlanTableObj.getAttribute("id"));
			currentIndex = (routePlanTableObj.getAttribute("idcounter")*1-1);
			document.getElementById("shipmentMode"+currentIndex).value		='<%=shipMode%>';
			document.getElementById("orginLocId"+currentIndex).value			='<%=orignLoc%>';	
			document.getElementById("destinationLocId"+currentIndex).value	='<%=destinationLoc%>';
			document.getElementById("remarks"+currentIndex).value	='<%=remarks!=null?remarks:""%>';
<%
		}
%>
			document.getElementById("shipmentMode1").disabled	=	true;	
			document.getElementById("orginLocId1").readOnly		=	true;
			document.getElementById("orgBut1").disabled			=	true;
<%
	}
	else
	{
%>
		  if(routePlanTableObj.getAttribute("idcounter")==1)
		      	createDynamicContentRow(routePlanTableObj.getAttribute("id")); 
		  
<%
	}
%>
}

function defaultFunction(currentRow)
{
	//alert(currentRow)
  if(currentRow>1)
	{
    document.getElementById("destinationLocId"+currentRow).value=document.getElementById("destinationLocId"+(currentRow-1)).value;
	//document.getElementById("destinationLocId"+(currentRow-1)).value='';
	}
    
  return true;
}
function showLocation(obj,objName)
{
	
	var index		=	(obj.id).substring(obj.name.length,obj.name.length+1);
	var objvalue	=	objName+index
	var shipmentMode=	document.getElementById("shipmentMode"+index).value;
	//alert(document.getElementById(objvalue).value.toUpperCase())
	var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
	var Features =  Bars+''+Options;
	//+"&shipmentMode="+
	myUrl=  '<%=contextPath%>/etrans/ETCLOVLocationIds.jsp?wheretoset='+objvalue+'&searchString='+document.getElementById(objvalue).value.toUpperCase()+"&shipmentMode="+shipmentMode;
	var Win	   =  open(myUrl,'Doc',Features);
}
function changeValue(obj)
{
	var index		=	(obj.id).substring(obj.name.length,obj.name.length+1);
	document.getElementById("orginLocId"+index).value="";
	document.getElementById("destinationLocId"+index).value="";
}


</script>
<body onLoad='initialize()'>
<form method = 'POST' action = 'ETMultiModeRoutePlanController' onSubmit='return validateForm()'>
<table border="0" cellPadding="4" cellSpacing="1" width="800" >
    <tr>
      <td >
        <table border="0" width="800" cellPadding="4" cellSpacing="0">
            <tr class="formlabel">
              <td >Route Plan - Add</td>
              <td align="right" >QS10101010</td>
            </tr>
        </table>
<%
		if(request.getAttribute("ERROR")!=null)
		{
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="1">
				<tr class='formdata' valign="top">
					<td width="33%"><font color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
						<%=((String)request.getAttribute("ERROR"))%>
					</font></td>
				</tr>
			</table>
<%
		}
%>
	 <table width=800 border='0' cellspacing='1' cellpadding='4' nowrap id='routePlanTable' idcounter="1"  defaultElement="defaultElement" xmlTagName="Routeplan" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20 bgcolor='#FFFFFF'>
	 <tr class="formheader">
	  <td align="center" ><font size="1">-</font></td>
      <td align="center" >Mode</td>
      <td align="center" >Origin Location/Port</td>
      <td align="center" >Destination Location/Port</td>
      <td align="center" >Remarks</td>
      <td align="center" ><font size="1">-</font></td>
    </tr>
   </table>
   <table width="800" border='0' cellspacing='0' cellpadding='4'>
    <tr class="text">
		<input type="hidden" name="qouteId" value="<%=quoteId%>">
		<input type="hidden" name="Operation" value="<%=operation%>">
		<input type="hidden" name="SubOperation" value="Detail">
      <td align="right" ><input type="submit" class="input" name="but_submit" value="Submit">&nbsp;
        <!--<input class="input" type="reset" value="Reset">-->
      </td>
    </tr>
	</table>
 </td>
</tr>
</table>
</form>
</body>
</html>
<%
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in QMSRoutePlanAdd.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSRoutePlanAdd.jsp "+e);
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details  ","ETMultiModeRoutePlanController?Operation="+operation+"&subOperation="); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
%>
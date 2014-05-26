<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSMultiLaneCosting.jsp
Product Name	: QMS
Module Name		: Costing
Task		    : Adding
Date started	:
Date Completed	:
Date modified	:
Author    		:
Description		: The application "Adding
Actor           :
Related Document: CR_DHLQMS_1008
--%>
<%@ page import="java.util.ArrayList,
				org.apache.log4j.Logger,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
				com.qms.operations.costing.dob.CostingChargeDetailsDOB,
				com.qms.operations.costing.dob.CostingLegDetailsDOB,
				com.qms.operations.costing.dob.CostingMasterDOB,
				com.qms.operations.costing.dob.CostingRateInfoDOB,
				com.qms.operations.costing.dob.CostingHDRDOB"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSCostingDetails.jsp";


%>
<%
  logger  = Logger.getLogger(FILE_NAME);
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	String      nextNavigation		=	"";
	CostingMasterDOB costingMasterDOB=  null;
	CostingChargeDetailsDOB detailsDOB= null;
	CostingLegDetailsDOB  legDetails=   null;
	CostingRateInfoDOB   rateDetailsDOB=   null;
	String operation	            =	request.getParameter("Operation");
	String subOperation 	        =	request.getParameter("subOperation");
	String terminalId	            =	loginbean.getTerminalId();
    ArrayList  originList           =   new ArrayList();
	ArrayList  destinationList      =   new ArrayList();
	ArrayList  costingLegDetailsList=   new ArrayList();
    ArrayList  rateDetails          =   new ArrayList();
	ArrayList frieghtChargeDetails  =   new ArrayList();
	ArrayList list_exNotes          =  null;
	ArrayList  detailsList          = new ArrayList();
	String       currency           =   "";
	String       basis              =   "";

	String origin		=	null;
	String destination	=	null;
	String customerId	=	null;
	String quoteId		=	null;

	String noOfPieces	=	null;
	String uom			=	null;
	String actualWt		=	null;
	String volume		=   null;
	String volumeUom	=	null;
	String invCurrency	=	null;
	String invValue		=	null;
	String baseCurreny	=	null;
	String shipmentMode	=	null;
	String[] primaryUnitArray=null;
	String[] secondaryUnitArray=null;
	String[] rateValue = null;
	String disabled		=	"";
	String advantageTo  =   null;  
	double originTotal	=	0;
	double destTotal	=	0;
	double frtTotal		=	0;
	double total		=	0;
	java.text.DecimalFormat   deciFormat     = null;
	String rateIndicator = "";
	boolean listRateFlag = false;
	String errorMsg = null;
	String percentFlag = "";
	String custContactIds	=	"";
    int countDetail = 0;
	int s =0;
	String breakPoint = null;
	String salesPersonEmail = null;
	int shipment = 0;
	try{

		    deciFormat						 = new java.text.DecimalFormat("##0.00");
			java.text.DecimalFormat   df     = new java.text.DecimalFormat("0.000000");
			java.text.DecimalFormat df1			  =	new java.text.DecimalFormat("##,###,##0.00");

			list_exNotes = new ArrayList();

		   CostingHDRDOB	costingHDRDOB = (CostingHDRDOB)session.getAttribute("costingHDRDOB");
           costingMasterDOB			=	(CostingMasterDOB)session.getAttribute("costingMasterDOB");
		   if((String)request.getParameter("countDetail")!=null)
			countDetail            = Integer.parseInt((String)request.getParameter("countDetail"));


		   errorMsg      = (String)request.getAttribute("errorMsg");
		   //Logger.info("",""+costingMasterDOB);
			if(costingHDRDOB!=null)
			{
				quoteId	   = costingHDRDOB.getQuoteid();
				shipment = costingHDRDOB.getShipmentMode();
				if(shipment==1)
					shipmentMode = "Air";
				else if(shipment==2)
					shipmentMode="Sea";
				else if(shipment==4)
					shipmentMode="Truck";
				else
					shipmentMode="";
			}
			session.setAttribute("costingHDRDOB",costingHDRDOB);
			session.setAttribute("costingMasterDOB",costingMasterDOB);
			ArrayList list = costingHDRDOB.getQuoteLanes();

			int listSize = 0;
			if(list!=null)
				listSize = list.size();
			System.out.println("listlistlistlistlistlistlistlistlist"+listSize);
			CostingHDRDOB temp = null;
			advantageTo = (String)request.getParameter("advantage");
%>

<html>
<head>
<title>Export Air Freight Costing</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
	function submitForm()
	{
		var radioObject = document.forms[0].selectedLanes;
		var lanesSize = <%=listSize%>;
		if(lanesSize==1){
			value= document.forms[0].selectedLanes.value;
			document.forms[0].lane.value=0;	       //Added by Rakesh on 24-01-2011 for CR:231219
		}else{
			for (var i=0; i<radioObject.length; i++) 
			{
				if (radioObject[i].checked) 
				{
				    document.forms[0].lane.value=i;	//Added by Rakesh on 24-01-2011 for CR:231219
					value = radioObject[i].value;
					break ;
				}
			}
		}
		document.forms[0].laneNo.value=value;
		document.multiLaneForm.action ="QMSCostingController?Operation=Add&subOperation=enter";
		document.multiLaneForm.submit();
	}
</script>
</head>
<body >
<form method='post' name="multiLaneForm" >
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="100%" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="100%" >
					<tr valign="top" class="formlabel">
						<td><%=shipmentMode%> Multi Lane Freight Costing </td>
						<td align=right>QS1050623
						</td></tr></table>
			</td>
		  </tr>
		</table>
<%
		if(errorMsg!=null && errorMsg.trim().length()>0)
		{
%>
		<table border='0' width="100%" cellpadding="0" cellspacing="0">
			<tr class='formdata'>
				<font color='#ff0000'><%=errorMsg%></font>
			</tr>
		</table>
<%
		}
%>
		<table border='0' width="100%" cellpadding="8" cellspacing="1">
			<tr class='formheader'>
				<td colspan='8'>Lane Details for Quote(<%=quoteId%>)</td>
			</tr>
			<tr class='formdata'>
					<td>Select</td>
					<td>Lane No</td>
					<td>Origin Port</td>
					<td>Destination Port</td>
					<td>Customer</td>
					<td>Shipment Mode</td>
					<td>Carrier</td>
					<td>Service Level</td>
				</tr>
				 <input type='hidden' name='listSize' value=<%=listSize%>><!-- Added by Rakesh  -->
			<% 
				for(int i = 0; i<listSize;i++)
				{
					temp = (CostingHDRDOB)list.get(i);
				%>
					<tr  align=center class='formdata'>
					<% if(i==0) {%>
						<td><input type="radio" value="<%=temp.getLaneNo()%>" name="selectedLanes" checked ></td>
						<%} else { %>
						<td><input type="radio" value="<%=temp.getLaneNo()%>" name="selectedLanes"></td>
						<% }%>
					<td><%=temp.getLaneNo()%></td>
					<td><%=temp.getOrigin()!= null?temp.getOrigin():""%></td>
					<td><%=temp.getDestination()!= null?temp.getDestination():""%></td>
					<td><%=temp.getCustomerid()%></td>
					<td><%=shipmentMode%></td>
					<td><%=temp.getCarrier()!=null?temp.getCarrier():""%></td>
					<td><%=temp.getServiceLevel()!=null?temp.getServiceLevel():""%></td>
					<input type='hidden' name="carrier<%=i%>" value='<%=temp.getCarrier()!=null?temp.getCarrier():""%>'> <!-- //Added by Rakesh on 24-01-2011 for CR:231219 -->
					<input type='hidden' name="serviceLevel<%=i%>" value='<%=temp.getServiceLevel()!=null?temp.getServiceLevel():""%>'> <!-- Added by Rakesh on 24-01-2011 for CR:231219 -->
				   	
					</tr>
				<%}%><input type='hidden' name='lane'>

		</table>
		<table border="0" width="100%" cellpadding="4" cellspacing="1">
		<tr class='denotes'>
			 <td valign="top" align="right">
				 <input type="button" value="Next" name="Next" class="input" onclick='submitForm()'>
				 <input type='hidden' name="quoteid" id="quoteid" value ='<%=quoteId%>'  >
 				 <input type='hidden' name="fromPage" id="fromPage" value ='<%=request.getParameter("fromPage")%>' >
				 <input type='hidden' name="isFetchNext" id="isFetchNext" value ='Y' >
				  <input type='hidden' name="laneNo" id="laneNo" value ='' >
				  <input type ='hidden' name = 'advantage' value = '<%=advantageTo%>'>
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
	}catch(Exception e)
	{

		//Logger.error(FILE_NAME,"Error in JSP "+e);
    logger.error(FILE_NAME+"Error in JSP "+e);
		e.printStackTrace();
		errorMessage = "An Error Has Occurred While Displaying Costing Details. Please Try Again.";
		errorMessageObject = new ErrorMessage(errorMessage,nextNavigation);
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		keyValueList.add(new KeyValue("Operation",operation));
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
%>
					<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
%>

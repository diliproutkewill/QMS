<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSMultiQuoteGroupView.jsp
Product Name	: QMS
Module Name		: MultiQuote
Task		    : Adding
Date started	:
Date Completed	:
Date modified	:
Author    		: Rakesh K
Description		: The application "Adding
Actor           :
Related Document: 
--%>
<%@ page import="java.util.ArrayList,
                 java.sql.Timestamp,
				com.foursoft.esupply.common.util.Logger,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
				com.foursoft.esupply.common.util.ESupplyDateUtility,
				com.qms.operations.costing.dob.CostingChargeDetailsDOB,
				com.qms.operations.costing.dob.CostingLegDetailsDOB,
				com.qms.operations.costing.dob.CostingMasterDOB,
				com.qms.operations.costing.dob.QuoteRateDetails,
				com.qms.operations.costing.dob.CostingRateInfoDOB,
				com.qms.operations.costing.dob.CostingHDRDOB,
				com.qms.operations.multiquote.dob.MultiQuoteMasterDOB,
				com.qms.operations.multiquote.dob.MultiQuoteFinalDOB,
				com.qms.operations.multiquote.dob.MultiQuoteCharges,
				com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates,
				com.qms.operations.multiquote.dob.MultiQuoteHeader,
				com.qms.operations.multiquote.dob.MultiQuoteFreightRSRCSRDOB"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSMultiQuoteGroupView.jsp";


%>
<%
    ESupplyDateUtility  eSupplyDateUtility =	new ESupplyDateUtility();
	 ESupplyDateUtility  eSupplyDateUtility1 =	new ESupplyDateUtility();//@@ Added by subrahmanyam for the wpbn id: 181349
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
	String       currency           =   "";
	String       basis              =   "";

	String        dateFormat			  =	loginbean.getUserPreferences().getDateFormat();
	MultiQuoteFinalDOB finalDOB				  =	null;
	MultiQuoteHeader	  headerDOB				  =	null;
	MultiQuoteMasterDOB masterDOB			  =	null;

	ArrayList     charges                 = null;//@@Info of all the Legs
	ArrayList     originCharges           = null;
	ArrayList     destCharges             = null;
	ArrayList     freightCharges          = null;
	MultiQuoteFreightLegSellRates  legCharges  = null;
	MultiQuoteCharges  chargesDOB			        = null;
	MultiQuoteFreightRSRCSRDOB   freightDOB     = null;

	ArrayList     originChargeInfo        = null;
	ArrayList     destChargeInfo          = null;
	ArrayList     freightChargeInfo       = null;

	String		  str[]				     = null;
    String		  str1[]				 = null;
	Timestamp     quoteDate				 = null;
	Timestamp     effDate				  = null;
	Timestamp     validUpto				        = null;
	String		  quoteDateStr			        = null;
	String		  effDateStr			          = null;
	String		  validUptoStr			        = null;
	String		  buyRatePermission		     = null;
      String      quoteAssign              = null;
	String		  colspan				           = "";
	String		  serviceColspan		       = "";
	String		  infoColspan			         = "";

	int           chargesSize			       =	0;

	int           originChargesSize		   =	0;
	int           originChargesInfoSize  =	0;
	int           destChargesSize		     =	0;
	int           destChargesInfoSize	   =	0;
	int           freightChargesSize	   =	0;
	int           freightChargesInfoSize =	0;

	int[]		  originIndices			         =	null;
	int[]		  destIndices			           =	null;
	int[]		  frtIndices			           =	null;

	String[]	  notes					           =	null;
    String[]	  strn					           =	null;
	int			  notesLength			           =	0;
	ArrayList     mainDtl                = null;
	String		  wBSlab				 = null;//added by subrahmanyam for 182516
	try
	{
		eSupplyDateUtility.setPatternWithTime(dateFormat);
		eSupplyDateUtility1.setPatternWithTime("DD-MONTH-YYYY");//Added by subrahmanyam for wpbn id: 181349
	}
	catch(Exception exp)
	{
		Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());

	}
	try
	{

		mainDtl		      =	(ArrayList)session.getAttribute("mainDtl");

		for(int k=0;k<mainDtl.size();k++)
		{
		finalDOB		      =	(MultiQuoteFinalDOB)mainDtl.get(k);

		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
        costingMasterDOB      =  finalDOB.getCostingMasterDOB();
        buyRatePermission	  =  masterDOB.getBuyRatesPermission();

		colspan			      = "6";
		serviceColspan	      = "3";
		infoColspan		      =	"3";
    	charges				  = finalDOB.getLegDetails();
    	chargesSize			  = charges.size();
		if(costingMasterDOB!=null)
		{
               originList=(ArrayList)costingMasterDOB.getOriginList();
               destinationList=(ArrayList)costingMasterDOB.getDestinationList();
			   costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
		}
		if(mainDtl!=null)
		session.setAttribute("mainDtl",mainDtl);
		}
%>

<html>
<head>
<title>Export Air Freight Costing</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
function showCurrency(currencyId)
{
		var termid		 = '<%=loginbean.getTerminalId()%>';
	//var searchStr	 =	document.getElementById(currencyId).value;
	Url='etrans/ETCCurrencyConversionAddLOV.jsp?searchString=&index=&Operation=<%=operation%>&teminalId='+termid+'&name='+currencyId+'&fromWhere=buycharges&selection=single';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=400,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

function submitToServer(input)
{
	var name = input.name;
	if(name=='details')
	{

		document.forms[0].action='QMSCostingController?Operation=Add&subOperation=details';
		document.forms[0].submit();
	}
}
function caliculationWindow()
{
  Url='etrans/QmsAreaCode.jsp';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=830,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);

}
function mandatory()
{

 /*if(!document.forms[0].email.checked)
 {
  alert("Please check email Option");
  return false;
 }*/

 return true;
}
</script>
</head>
<body>
<form method='post' action ='QMSMultiQuoteController' onSubmit="return mandatory()">

		<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='right'>EXPORT AIR FREIGHT QUOTE <%//=operation%></td><td align=right>QS1060212</td></tr></table></td>
		  </tr></table>
<%
        for(int k=0;k<mainDtl.size();k++)
		{
		finalDOB		      =	(MultiQuoteFinalDOB)mainDtl.get(k);

		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
        costingMasterDOB      =  finalDOB.getCostingMasterDOB();
        buyRatePermission	  =  masterDOB.getBuyRatesPermission();

		colspan			      = "6";
		serviceColspan	      = "3";
		infoColspan		      =	"3";
    	charges				  = finalDOB.getLegDetails();
    	chargesSize			  = charges.size();
		if(costingMasterDOB!=null)
		{
               originList=(ArrayList)costingMasterDOB.getOriginList();
               destinationList=(ArrayList)costingMasterDOB.getDestinationList();
			   costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
		}

%>
       <table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
              <td width='42%'>QUOTEID : <%=masterDOB.getQuoteId()%></td>
			  <td colspan="7">&nbsp;&nbsp;CUSTOMER NAME:&nbsp;<%=headerDOB.getCustomerName()!=null?headerDOB.getCustomerName():""%></td>
            </tr>
			<tr class="formdata">
              <td>&nbsp;</td>
			  <td colspan="7" >&nbsp;&nbsp;ATTENTION TO&nbsp;&nbsp;&nbsp;:&nbsp;
<%			StringBuffer   attentionTo            =   null;
			if(masterDOB.getCustContactNames()!=null)
			{
				attentionTo            =   new StringBuffer("");
				for(int i=0;i<masterDOB.getCustContactNames().length;i++)
				{
					attentionTo.append(masterDOB.getCustContactNames()[i]!=null?masterDOB.getCustContactNames()[i]:"");
					if(i!=(masterDOB.getCustContactNames().length-1))
						attentionTo.append(",");
				}
			}
%>
				<%=attentionTo!=null?attentionTo.toString():""%>
			</td>
            </tr>
			<tr class="formdata">
              <td>&nbsp;</td>
			 <!-- @@ Commented by subrahmanyam for wpbn id: 181349 on 07-Sep-09 -->
			 <!--  <td colspan="7">&nbsp;&nbsp;QUOTATION DATE:&nbsp;<%=quoteDateStr!=null?quoteDateStr:""%> </td> -->
			 <!-- @@ Added by subrahmanyam for wpbn id: 181349 on 07-Sep-09 -->
			  <td colspan="7">&nbsp;&nbsp;QUOTATION DATE:&nbsp;<%=eSupplyDateUtility1.getDisplayStringArray(headerDOB.getEffDate())[0]!=null?eSupplyDateUtility1.getDisplayStringArray(headerDOB.getEffDate())[0]:""%> </td>
 			 <!-- @@ Ended for wpbn id: 181349 on 07-Sep-09 -->
            </tr>
			<tr class="formdata">
              <td>&nbsp;</td>
			  <td colspan="7">&nbsp;&nbsp;PREPARED BY&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;<%=headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy():""%></td>
            </tr>

<%      }
%>
         </table>
		  <table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
<%
        for(int k=0;k<mainDtl.size();k++)
		{
		finalDOB		      =	(MultiQuoteFinalDOB)mainDtl.get(k);

		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
        costingMasterDOB      =  finalDOB.getCostingMasterDOB();
        buyRatePermission	  =  masterDOB.getBuyRatesPermission();

		colspan			      = "6";
		serviceColspan	      = "3";
		infoColspan		      =	"3";
    	charges				  = finalDOB.getLegDetails();
    	chargesSize			  = charges.size();
		if(costingMasterDOB!=null)
		{
               originList=(ArrayList)costingMasterDOB.getOriginList();
               destinationList=(ArrayList)costingMasterDOB.getDestinationList();
			   costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();

		}
%>
            <tr class="formheader" cellpadding="4" >
              <td colspan="6" align=center>SERVICE INFORMATION</td><td colspan="2">QuoteId :<%=masterDOB.getQuoteId()%></td>
            </tr>
			<tr class="formdata">
              <td colspan="4" >Agent: </td>
			  <td colspan="4" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
            </tr>
			<!-- <tr class="formdata"> -->
             <!-- @@ Commented by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
              <!-- <td colspan="4" >Cargo Acceptance Place: </td> -->
<!-- @@ Added by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
				<!--  -->
				
<!-- @@ Ended by subrahmanyam for the WPBN ISSUE:150460 ON 23/12/2008 -->
			 <!--  <td colspan="4" ><%=headerDOB.getCargoAcceptancePlace()!=null?headerDOB.getCargoAcceptancePlace():""%></td> -->
          <!--   </tr>
			<tr class="formdata">
              <td colspan="4" >Origin Port: </td>
			  <td colspan="4" ><%=headerDOB.getOriginPortName()!=null?headerDOB.getOriginPortName()+", ":""%><%=headerDOB.getOriginPortCountry()!=null?headerDOB.getOriginPortCountry():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="4">Destination Port: </td>
			  <td colspan="4"><%=headerDOB.getDestPortName()!=null?headerDOB.getDestPortName()+", ":""%><%=headerDOB.getDestPortCountry()!=null?headerDOB.getDestPortCountry():""%></td>
            </tr> -->
			  
              <!-- Commented by subrahmanyam for the enhancement #148546 on 09/12/2008 -->
			<!-- <tr class="formdata">
              <td colspan="4" >Routing: </td>
			  <td colspan="4" ><%=headerDOB.getRouting()!=null?headerDOB.getRouting():""%></td>
            </tr>
			  <td colspan="4" ><%=headerDOB.getRouting()!=null?headerDOB.getRouting():""%></td>
            </tr>-->
			<tr class="formdata">
              <td colspan="4" >Commodity or Product: </td>
			  <td colspan="4" ><%=headerDOB.getCommodity()!=null?headerDOB.getCommodity():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="4" >Type of service quoted: </td>
			  <td colspan="4" ><%=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService():""%> </td>
            </tr>

			<!-- <tr class="formdata">
              <td colspan="4" >Incoterms: </td>
			  <td colspan="4" ><%=headerDOB.getIncoTerms()!=null?headerDOB.getIncoTerms():""%></td>
            </tr>
            <%   for(int m=0;m<costingLegDetailsList.size();m++)
			   {
                 legDetails = (CostingLegDetailsDOB)costingLegDetailsList.get(m);
                   if(legDetails.getRateValidity()!=null)
                    {
                       strn     =   eSupplyDateUtility.getDisplayStringArray(legDetails.getRateValidity());
                    }
               if(legDetails.getFrequency()!=null&&legDetails.getFrequencyChecked()!=null&&"on".equalsIgnoreCase(legDetails.getFrequencyChecked()))
                  {%>
                 	<tr class="formdata">

              <td colspan="4" >Frequency: </td>
			  <td colspan="4" ><%=legDetails.getFrequency()%></td>

            </tr> 
              <%}
              if(legDetails.getCarrierName()!=null)
                  {%>
            <tr class="formdata">
              <td colspan="4" >Carrier: </td>
			  <td colspan="4" ><%=legDetails.getCarrierName()%></td>
            </tr>
            <%}
            if(legDetails.getTransitTime()!=null&&legDetails.getTransitChecked()!=null&&"on".equalsIgnoreCase(legDetails.getTransitChecked()))
            {%>
         		<tr class="formdata">
              <td colspan="4" >Transittime: </td>
			  <td colspan="4" ><%=legDetails.getTransitTime()%></td>
            </tr>
            <%}
            if(strn!=null)
            {%>
            	<tr class="formdata">
              <td colspan="4" >Freight Rate Validity: </td>
			  <td colspan="4" ><%=strn[0]%></td>
            </tr>
            <%}

            }%>	 -->

			<tr class="formdata">
              <td colspan="4" >Notes: </td>
			  <td colspan="4" ><%=headerDOB.getNotes()!=null?headerDOB.getNotes():""%></td>
            </tr>

			<tr class="formdata">
              <td colspan="4" >Validity of Quote: </td>
			  <td colspan="4" ><%=validUptoStr!=null?validUptoStr:"VALID TILL FURTHER NOTICE"%></td>
            </tr>


<%
		}
%>
            </table>
			<table width="100%"  bgcolor='#FFFFFF'  cellpadding="4" cellspacing="1">
			<tr class='formheader'>
			 <td>QuoteId</td>
			 <td>ChargeName</td>
			 <td>BreakPoint</td>
			 <td>Currency</td>
			 <td>Rate</td>
			 <td>Basis</td>
			 <td>Ratio</td>
			 </tr>
             <tr class='formheader'>
			 <td colspan="7" align="center">ORIGIN CHARGES</td>
			 </tr>

<% 
	for(int k=0;k<mainDtl.size();k++)
		{
		finalDOB		      =	(MultiQuoteFinalDOB)mainDtl.get(k);

		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
        costingMasterDOB      =  finalDOB.getCostingMasterDOB();
        buyRatePermission	  =  masterDOB.getBuyRatesPermission();
//@@COmmented by subrahmanyam for QuoteGrouping Issue
		//Long quoteConvert     =  new Long(masterDOB.getQuoteId());
		//String  quoteAssign   =  quoteConvert.toString();
		quoteAssign   =masterDOB.getQuoteId();//Added by subrahmanyam for the quoteGrouping issue
		colspan			      = "6";
		serviceColspan	      = "3";
		infoColspan		      =	"3";
    	charges				  = finalDOB.getLegDetails();
    	chargesSize			  = charges.size();

		String tempVar        = "";
		String wBslab        = "";//ADDED FOR 183812
		if(costingMasterDOB!=null)
		{
               originList=(ArrayList)costingMasterDOB.getOriginList();
               destinationList=(ArrayList)costingMasterDOB.getDestinationList();
			   costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
		}

%>

		<%

		   for(int m=0;m<originList.size();m++)
			 {
                 detailsDOB = (CostingChargeDetailsDOB)originList.get(m);
                 rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();


				 for(int j=0;j<rateDetails.size();j++)
				 {
				   rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);


                  %>
				  <tr class='formdata'>
				   <td><%=(!quoteAssign.equals(tempVar))?quoteAssign:""%></td>
					<!-- @@ Commented by subrahmanyam for 181349 on 07-Sep-09 -->
				   <!-- <td><%=(rateDetailsDOB.getWeightBreakSlab().equals("BASE")?detailsDOB.getChargeDescId():"")%></td> -->
				   <!-- @@ Added by subrahmanyam for 181349 on 07-Sep-09 -->
					<td><%=(j==0?detailsDOB.getChargeDescId():"")%></td>
					<!-- @@ ended by subrahmanyam for 181349 on 07-Sep-09 -->
					<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
					<td><%=detailsDOB.getCurrency()%></td>
					<td><%=rateDetailsDOB.getRate()%></td>
					<!-- <td><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td> --><!-- commented by subrahmanyam for 183812 on 25/09/09 -->
						<!-- ADDED FOR 183812  -->
					<%
					//modified by subrahmanyam on 16/dec/09
						if(("BOTH".equalsIgnoreCase(detailsDOB.getRateType()) && "F".equalsIgnoreCase(rateDetailsDOB.getRateIndicator()))
							|| rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") )
							{
							wBslab	= "MIN";
							}
							else
								wBslab =detailsDOB.getChargeBasisDesc();
					%>
						<td><%=wBslab.equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td>
					<!-- ENDED FOR 183812  -->

					<td><input type=text size=4  name=""  value='<%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%>' readOnly class='text' ></td>
				  </tr>
      <%
				  tempVar=quoteAssign;
				  }
		       }

		}
  %>

                   <tr class='formheader'>
					<td colspan="7" align="center">FREIGHT CHARGES</td>
				  </tr>
  <%

	   for(int k=0;k<mainDtl.size();k++)
	   {
		finalDOB		      =	(MultiQuoteFinalDOB)mainDtl.get(k);

		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
        costingMasterDOB      =  finalDOB.getCostingMasterDOB();
        buyRatePermission	  =  masterDOB.getBuyRatesPermission();
//@@ commented by subrahmanyam for the quote grouping issue
		//Long quoteConvert     =  new Long(masterDOB.getQuoteId());
		//String  quoteAssign   =  quoteConvert.toString();
    quoteAssign         =  masterDOB.getQuoteId();//@@ added by subrahmanyam for the quote grouping issue
		colspan			        = "6";
		serviceColspan	    = "3";
		infoColspan		      =	"3";
    	charges				    = finalDOB.getLegDetails();
    	chargesSize			  = charges.size();

		String tempVar      = "";
		 String temp1 ="";
		   String temp ="";
		   String wBslab      = "";//added for 183812
		if(costingMasterDOB!=null)
		{
               originList=(ArrayList)costingMasterDOB.getOriginList();
               destinationList=(ArrayList)costingMasterDOB.getDestinationList();
			   costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
		}

			   for(int m=0;m<costingLegDetailsList.size();m++)
			   {
                 legDetails = (CostingLegDetailsDOB)costingLegDetailsList.get(m);
                 frieghtChargeDetails      =  (ArrayList)legDetails.getCostingChargeDetailList();

	%>
	             <tr class='formdata'>
	             <td colspan="7" align="center"><b><%=legDetails.getOrigin()%>-<%=legDetails.getDestination()%></b></td>
				 </tr>
	<%
				 for(int n=0;n<frieghtChargeDetails.size();n++)
				 {
                      detailsDOB = (CostingChargeDetailsDOB)frieghtChargeDetails.get(n);
                      rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
	                for(int j=0;j<rateDetails.size();j++)
				     {
				       rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);
					  temp = rateDetailsDOB.getRateDescription();
	   %>
				  <tr class='formdata'>
					<td><%=(!quoteAssign.equals(tempVar))?quoteAssign:""%></td><!-- added by  subrahmanyam for 182516-->
						<%if(temp1.equalsIgnoreCase(temp))
						 {%>
				     <!-- <td><%=(!quoteAssign.equals(tempVar))?quoteAssign:""%></td> -->
					  <td></td>
					  <%}
					  else
					  {%>
					  <td><%=temp%></td>
					  <%}%> <!-- <td><%=rateDetailsDOB.getWeightBreakSlab().equals("MIN")?(detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId()):""%></td> -->
					<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
					<td><%=detailsDOB.getCurrency()%></td>
					<td><%=rateDetailsDOB.getRate()%></td>
					<!-- commented by  subrahmanyam for 182516-->
					<!-- <td><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td> -->
					<!-- added by  subrahmanyam for 182516--><!-- modified for 183812 -->
					<%
						if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") || rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN") || rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN")
						|| ("BOTH".equalsIgnoreCase(detailsDOB.getRateType()) && "FLAT".equalsIgnoreCase(rateDetailsDOB.getRateIndicator()))){
						 wBSlab = "MIN";
						}
						else{
								wBSlab	= rateDetailsDOB.getWeightBreakSlab();
						}
						%>
					<td><%=wBSlab.equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td>
					<!-- ended for 182516 -->
					<td><input type=text size=4  name=""  value='<%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%>' readOnly class='text' ></td>
	    		  </tr>
      <%			temp1 = rateDetailsDOB.getRateDescription();
		            tempVar=quoteAssign;
					}
				  }
			 }
	   }

	%>            <tr class='formheader'>
					<td colspan="7" align="center">DESTINATION CHARGES</td>
				   </tr>
	    <%
                for(int k=0;k<mainDtl.size();k++)
				{
				finalDOB		      =	(MultiQuoteFinalDOB)mainDtl.get(k);

				masterDOB			  =	 finalDOB.getMasterDOB();
				headerDOB		      =	 finalDOB.getHeaderDOB();
				costingMasterDOB      =  finalDOB.getCostingMasterDOB();
				buyRatePermission	  =  masterDOB.getBuyRatesPermission();
//@@ Commented by subrahmanyam for the quote grouping issue
			//	Long quoteConvert     =  new Long(masterDOB.getQuoteId());
		  //  String  quoteAssign   =  quoteConvert.toString();
		   quoteAssign       = masterDOB.getQuoteId();//added by subrahmanyam for the quote grouping issue
				colspan			      = "6";
				serviceColspan	      = "3";
				infoColspan		      =	"3";
				charges				  = finalDOB.getLegDetails();
				chargesSize			  = charges.size();

				String tempVar        = "";
				String wBslab        = "";//added for 183812
				if(costingMasterDOB!=null)
				{
					   originList=(ArrayList)costingMasterDOB.getOriginList();
					   destinationList=(ArrayList)costingMasterDOB.getDestinationList();
					   costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
				}

			  for(int m=0;m<destinationList.size();m++)
			   {
                 detailsDOB = (CostingChargeDetailsDOB)destinationList.get(m);
                 rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
	             	for(int j=0;j<rateDetails.size();j++)
				     {
		     	       rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);
		  %>
				  <tr class='formdata'>
		     		 <td><%=(!quoteAssign.equals(tempVar))?quoteAssign:""%></td>
					 <!-- @@ Commented by subrahmanyam for the wpbn id: 181349 on 07-sep-09 -->
					<!-- <td><%=rateDetailsDOB.getWeightBreakSlab().equals("MIN")?detailsDOB.getChargeDescId():""%></td> -->
					<td><%=j==0?detailsDOB.getChargeDescId():""%></td><!-- @@ Added by subrahmanyam for 181349 -->
					<!-- Ended for 181349 -->
					<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
					<td><%=detailsDOB.getCurrency()%></td>
					<td><%=rateDetailsDOB.getRate()%></td>
					<!-- ADDED FOR 183812  -->
					<%
						if(("BOTH".equalsIgnoreCase(detailsDOB.getRateType()) && "F".equalsIgnoreCase(rateDetailsDOB.getRateIndicator()))
						|| rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") )
						 {
								wBslab	= "MIN";
						 }
						 else
							wBslab =detailsDOB.getChargeBasisDesc();
					%>
					<td><%=wBslab.equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td>
					<!-- ENDED FOR 183812  -->
					<!-- <td><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td> --><!-- COMMENTED FOR 183812 -->

					<td><input type=text size=4  name=""  value='<%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%>' readOnly class='text' ></td>
				  </tr>
            <%
					tempVar=quoteAssign;
					}
				 }
				}
			%>
			 <tr class='formheader'>
			   <td colspan="7" align="center">NOTES</td>
			   </tr>
			<tr class='formheader'>
		    	<td colspan="1" align="center">QuoteId</td>
			    <td colspan="6" align="center">Note Details</td>
			  </tr>
			<%

			for(int k=0;k<mainDtl.size();k++)
				{
				finalDOB		      =	(MultiQuoteFinalDOB)mainDtl.get(k);

				masterDOB			  =	 finalDOB.getMasterDOB();
				headerDOB		      =	 finalDOB.getHeaderDOB();
				costingMasterDOB      =  finalDOB.getCostingMasterDOB();
				buyRatePermission	  =  masterDOB.getBuyRatesPermission();

				colspan			      = "6";
				serviceColspan	      = "3";
				infoColspan		      =	"3";
				charges				  = finalDOB.getLegDetails();
				chargesSize			  = charges.size();
				if(costingMasterDOB!=null)
				{
					   originList=(ArrayList)costingMasterDOB.getOriginList();
					   destinationList=(ArrayList)costingMasterDOB.getDestinationList();
					   costingLegDetailsList=(ArrayList)costingMasterDOB.getCostingLegDetailsList();
				}
				//@@ Added by subrahmanyam for the issue id:184848 on 01-oct-09
				if(finalDOB.getExternalNotes()!=null  && finalDOB.getExternalNotes().length>0)
				{
					 notesLength = finalDOB.getExternalNotes().length;
					for(int n=0;n<notesLength;n++)
					{
						if(n==0)
						{
			%>
						 <tr class='formdata'>
						<td colspan="1" align="center"><%=masterDOB.getQuoteId()%></td>

					<!--  <td colspan="6" align="center"><%=finalDOB.getExternalNotes()[n]%></td> -->
					 <td colspan="6" align="center"><input type=text size=100  name="notes"  value='<%=finalDOB.getExternalNotes()[n]%>'  class='text' ></td>
					  </tr>
					  <%}else{%>
							 <tr class='formdata'>
						<td colspan="1" align="center"></td>

					 <!-- <td colspan="6" align="center"><%=finalDOB.getExternalNotes()[n]%></td> -->
					 <td colspan="6" align="center"><input type=text size=100  name="notes"  value='<%=finalDOB.getExternalNotes()[n]%>'  class='text' ></td>
					  </tr>
					  <%}}}else{%>
						<tr class='formdata'>
						<td colspan="1" align="center"><%=masterDOB.getQuoteId()%></td>

					 <td colspan="6" align="center"><input type=text size=100  name="notes"  value=''  class='text' ></td>
					  </tr>
					  <%}%>

              <!--  <tr class='formdata'>
		    	<td colspan="1" align="center"><%=masterDOB.getQuoteId()%></td>

			 <td colspan="6" align="center"><input type=text size=50  name="notes"  value=''  class='text' ></td>

			  </tr> -->

<!-- Ended for 184848 on 01-Oct-09-->

		     <%
				}
			 %>
		    <tr class='formheader'>
		    	<td colspan="7" align="center">Send Options</td>
			</tr>
			<tr>
				<td colspan="7" class='formdata' align='center'>
					<INPUT TYPE="checkbox" NAME="email">&nbsp;Email
					<INPUT TYPE="checkbox" NAME="fax">&nbsp;Fax
					<INPUT TYPE="checkbox" NAME="print">&nbsp;Print
				</td>
			</tr>
		    <tr class='denotes'>
				<td colspan='4'>
					<font color='red'> *
					</font>Denotes Mandatory Field
				</td>
				<td colspan='7' align='right'>
	            <input type="submit" class='input' name="next" value="Submit">

				<input type="hidden" name='Operation' value='<%=request.getParameter("Operation")%>'>
	            <input type="hidden" name='subOperation' value='sendPdf'>

				</td>
			</tr>
		 </table>

</form>
</body>
</html>

<%



	}catch(Exception e)
	{

		errorMessage = "Error while displying the data";
		errorMessageObject = new ErrorMessage(errorMessage,nextNavigation);
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		keyValueList.add(new KeyValue("Operation",operation));
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
%>
					<jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}
%>
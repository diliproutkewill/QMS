<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteChargesView.jsp
Product Name		: QMS
Module Name			: Quote
Task				: View
Date started		:
Date modified		:
Author    			: Yuvraj
Related Document	: CR_DHLQMS_1007

--%>

<%@page import = "java.util.ArrayList,
				  java.sql.Timestamp,
				  java.util.StringTokenizer,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.quote.dob.QuoteMasterDOB,
				  com.qms.operations.quote.dob.QuoteFinalDOB,
				  com.qms.operations.quote.dob.QuoteHeader,
				  com.qms.operations.quote.dob.QuoteCharges,
				  com.qms.operations.quote.dob.QuoteChargeInfo,
				  com.qms.operations.quote.dob.QuoteFreightLegSellRates,
				  com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteChargesView.jsp"; 
  private   String toTitleCase(String str){
			StringBuffer sb = new StringBuffer();     
			str = str.toLowerCase();
			str =str.replace('(','~');
			str =str.replace(')','#');
			StringTokenizer strTitleCase = new StringTokenizer(str);
			while(strTitleCase.hasMoreTokens()){
				String s = strTitleCase.nextToken();
				sb.append(s.replaceFirst(s.substring(0,1),s.substring(0,1).toUpperCase()) + " ");
			}
			str= sb.toString();
		  str =str.replace('~','(');
		  str =str.replace('#',')');
			return str;
		}
  %>
<%
	logger  = Logger.getLogger(FILE_NAME);
	ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
	String		  operation				  =	request.getParameter("Operation");
	String        dateFormat			  =	loginbean.getUserPreferences().getDateFormat();
	QuoteFinalDOB finalDOB				  =	null;
	QuoteHeader	  headerDOB				  =	null;
	QuoteMasterDOB masterDOB			  =	null;

	ArrayList     charges                 = null;//@@Info of all the Legs
	ArrayList     originCharges           = null;
	ArrayList     destCharges             = null;
	ArrayList     freightCharges          = null;

	ArrayList     originChargeInfo        = null;
	ArrayList     destChargeInfo          = null;
	ArrayList     freightChargeInfo       = null;

	QuoteFreightLegSellRates  legCharges  = null;
	QuoteCharges  chargesDOB			  = null;
	QuoteChargeInfo chargeInfo			  = null;


	String		  str[]				      = null;
  String      str1[]              = null;
	Timestamp     quoteDate				  = null;
	Timestamp     effDate				  = null;
	Timestamp     validUpto				  = null;
	String		  quoteDateStr			  = null;
	String		  effDateStr			  = null;
	String		  validUptoStr			  = null;
	String		  buyRatePermission		  = null;
	String		  colspan				  = "";
	String		  serviceColspan		  = "";
	String		  infoColspan			  = "";
	String		  shipmentMode			  =	null;

	int           chargesSize			  =	0;

	int           originChargesSize		  =	0;
	int           originChargesInfoSize	  =	0;
	int           destChargesSize		  =	0;
	int           destChargesInfoSize	  =	0;
	int           freightChargesSize	  =	0;
	int           freightChargesInfoSize  =	0;

	String		  originMarginDisabled	  = "";
	String		  destMarginDisabled	  = "";
	String		  frtMarginDisabled		  = "";

	String		  originDiscDisabled	  = "";
	String		  destDiscDisabled		  = "";
	String		  frtDiscDisabled		  = "";

	int[]		  originIndices			  =	null;
	int[]		  destIndices			  =	null;
	int[]		  frtIndices			  =	null;

	String[]	  internalNotes			  =	null;
	String[]	  externalNotes			  =	null;
	int			  notesLength			  =	0;

	String		  fromWhere				  =	null;
	String		  color					  = "";
	String                          quoteStatus         =  null;
    String                          quoteName           =  null; // @@Added by VLAKSHMI for the WPBN issue-167677
	java.text.DecimalFormat df			  =	new java.text.DecimalFormat("##,###,##0.00");
	java.text.DecimalFormat df1			  =	new java.text.DecimalFormat("#######0.00");
    int n =0;
	String breakPoint = null;

	try
	{
		eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");//in order to get the full month name
	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
    logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
	}
	try
	{
		 quoteStatus          = request.getParameter("quoteStatus"); // @@Added by VLAKSHMI for the WPBN issue-167677
	    quoteName             = request.getParameter("quoteName");// @@Added by VLAKSHMI for the WPBN issue-167677
		finalDOB		      =	(QuoteFinalDOB)session.getAttribute("viewFinalDOB");
		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();

		quoteDate			  =  headerDOB.getDateOfQuotation();
//		effDate				  =  headerDOB.getEffDate();
		effDate				  =  masterDOB.getCreatedDate();
		validUpto			  =  headerDOB.getValidUpto();
		str					  = eSupplyDateUtility.getDisplayStringArray(quoteDate);
		quoteDateStr		  = str[0];

		str					  = eSupplyDateUtility.getDisplayStringArray(effDate);
		effDateStr			  =	str[0];

		if(validUpto!=null)
		{
			str					  = eSupplyDateUtility.getDisplayStringArray(validUpto);
			validUptoStr		  = str[0];
		}

		buyRatePermission	  = masterDOB.getBuyRatesPermission();

		/*colspan			  = "6";
		serviceColspan	  = "3";
		infoColspan		  =	"3";*/
		fromWhere			  =  request.getParameter("fromWhere");

		if(fromWhere==null || "null".equals(fromWhere))
			fromWhere		  =	 (String)request.getAttribute("fromWhere");

		if("Y".equals(buyRatePermission))
		{
			colspan			  = "13";
			serviceColspan	  = "7";
			infoColspan		  =	"7";
		}
		else
		{
			colspan			  = "10";
			serviceColspan	  = "5";
			infoColspan		  =	"4";
		}

		charges				  = finalDOB.getLegDetails();

		chargesSize			  = charges.size();

		if(!finalDOB.isMultiModalQuote())
		{
			if(masterDOB.getShipmentMode()==1)
				shipmentMode	=	"AIR";
			else if(masterDOB.getShipmentMode()==2)
				shipmentMode	=	"SEA";
			else if(masterDOB.getShipmentMode()==4)
				shipmentMode	=	"TRUCK";
		}
		else
		{
			shipmentMode	=	"MULTI-MODAL";
		}

%>

<%!
	private boolean isNumber(String s) {
	// TODO Auto-generated method stub
	try{
		Double d = Double.parseDouble(s);
		return true;
	}catch(Exception e){
		return false;
	}
}
public String round(double sellRate)
	{

    java.text.DecimalFormat dfDecimal	 =	new java.text.DecimalFormat("#######0.00000");
	java.text.DecimalFormat dfDecimal2	 =	new java.text.DecimalFormat("#######0.00");
      String rateString ="";
      int k = 0;
      int l = 0;
      int m = 0;
      rateString = Double.toString(sellRate);
      k = rateString.length();
      l = rateString.indexOf(".");
      m = (k - l)-1;
      if(m>5){
//		 rateString = dfDecimal.format(sellRate);
		 rateString = rateString.substring(0,l+6);
		 return rateString;
		}
		else if(m==1 || l==-1)
		{

			rateString = dfDecimal2.format(sellRate);
			return rateString;
		}
		else
			return Double.toString(sellRate);


    }


%>
<!--  @@ Ended by subrahmanyam for the enhancement 180161 -->
	<!-- @@ Ended  by subrahmanyam for the Enhancement 180161 on 28/Aug/09 -->

<HTML>
<HEAD>
<TITLE> Quote </TITLE>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
</HEAD>
<script>
var noOfLegs					=	'<%=chargesSize%>';
var internalNotesArray			=	new Array();
var externalNotesArray			=	new Array();

<%
	if(finalDOB.getInternalNotes()!=null)
	{
		internalNotes			=	finalDOB.getInternalNotes();
		for(int i=0;i<internalNotes.length;i++)
		{
%>			internalNotesArray[internalNotesArray.length]	=	"<%=internalNotes[i]!=null?internalNotes[i]:""%>";
<%
		}
	}
	if(finalDOB.getExternalNotes()!=null)
	{
		externalNotes			=	finalDOB.getExternalNotes();
		for(int i=0;i<externalNotes.length;i++)
		{
%>			externalNotesArray[externalNotesArray.length]	=	"<%=externalNotes[i]!=null?externalNotes[i]:""%>";
<%
		}
	}
%>
function initialize()
{
	importXML('<%=request.getContextPath()%>/xml/QMSQuote.xml');
}
function initializeDynamicContentRows()
{
	initializeDynamicContentDetails();
	disableButtons();
}
function initializeDynamicContentDetails()
{
	var tableObj	= document.getElementById("QuoteNotes");
	var totalLanes	=  internalNotesArray.length==0? 1 :internalNotesArray.length;
	for(var i=0;i<totalLanes;i++)
	{
		createDynamicContentRow(tableObj.getAttribute("id"));

		if(externalNotesArray==null || externalNotesArray.length==0)
			return;

		var internalNotes	=	document.getElementsByName("internalNotes");
		var externalNotes	=	document.getElementsByName("externalNotes");

		internalNotes[i].value	=	internalNotesArray[i];
		externalNotes[i].value	=	externalNotesArray[i];

	}
}
function disableButtons()
{
	for(var i=0;i<document.forms[0].elements.length;i++)
	{
		if(document.forms[0].elements[i].type=='button' && document.forms[0].elements[i].value!='Close')
			document.forms[0].elements[i].disabled=true;
		if(document.forms[0].elements[i].type=='text' || document.forms[0].elements[i].type=='textarea')
			document.forms[0].elements[i].readOnly=true;
	}
}
function chr(input)
{
	s = input.value;
	filteredValues = "'\"";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c;
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i] == input )
			{
				document.forms[0].elements[i].focus();
				break;
			}
		}
	}

	input.value = returnString;
}
</script>
<BODY onload='initialize()'>
<form method='post' action='QMSQuoteController'>
<table width="100%" cellpadding="0" cellspacing="0">
 <tr>
	<td>
		<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
		   <!-- @@Modified by VLAKSHMI for the WPBN issue-167677 -->
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT QUOTE(<%=quoteName%>,<%=quoteStatus%>) - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td></tr>
			 <!-- @@WPBN issue-167677 --></table></td>
		  </tr></table>

       <table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():""%> TO <%=headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getCustomerName()!=null?headerDOB.getCustomerName():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center>ATTENTION TO:
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
              <td colspan="<%=colspan%>" align=center>DATE OF QUOTATION:<%=quoteDateStr!=null?quoteDateStr:""%> </td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" align='right'>PREPARED BY:</td>
			  <td colspan="<%=infoColspan%>" align='left'><%=headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy():""%></td>
            </tr>
			<tr class="formheader" cellpadding="4" >
              <td colspan="<%=colspan%>" align=center>SERVICE INFORMATION</td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Agent: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
            </tr>
			<tr class="formdata">
          <!-- @@ Commented by subramanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
              <!-- <td colspan="<%=serviceColspan%>" >Cargo Acceptance Place: </td> -->
<!-- @@ Added by subrahmanyam for the WPBN ISSUE: 150460 ON  23/12/2008 -->
			  <% if("EXW".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FAS".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FCA".equalsIgnoreCase(headerDOB.getIncoTerms())|| "FOB".equalsIgnoreCase(headerDOB.getIncoTerms()))
				{%>
				<td colspan="<%=serviceColspan%>" >Place Of Acceptance: </td>
				<%}else{%>
					<td colspan="<%=serviceColspan%>" >Place Of Delivery: </td>
				<%}%>
<!-- @@ Ended by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->

			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCargoAcceptancePlace()!=null?headerDOB.getCargoAcceptancePlace():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Origin Port: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getOriginPortName()!=null?headerDOB.getOriginPortName()+", ":""%><%=headerDOB.getOriginPortCountry()!=null?headerDOB.getOriginPortCountry().toUpperCase():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>">Destination Port: </td>
			  <td colspan="<%=infoColspan%>"><%=headerDOB.getDestPortName()!=null?headerDOB.getDestPortName()+", ":""%><%=headerDOB.getDestPortCountry()!=null?headerDOB.getDestPortCountry().toUpperCase():""%></td>
            </tr>
			<!-- Commented by subrahmanyam for the enhancement #148546 on 09/12/2008 -->
			<!-- <tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Routing: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getRouting()!=null?headerDOB.getRouting():""%></td>
            </tr> -->
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Commodity or Product: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCommodity()!=null?headerDOB.getCommodity():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Type of service quoted: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService():""%> </td>
            </tr>
			<!--@@Added by Kameswari for the WPBN issue-146448-->
			<%		for(int i=0;i<chargesSize;i++)
			{
				legCharges	   = (QuoteFreightLegSellRates)charges.get(i);


				freightCharges = legCharges.getFreightChargesList();
				chargesDOB			= (QuoteCharges)freightCharges.get(0);
				if(chargesDOB.getValidUpto()!=null)
				{
					str1					  =eSupplyDateUtility.getDisplayStringArray(chargesDOB.getValidUpto());
				}
				if(chargesSize>1)
				{
        if("on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
		{
			%>             <tr class="formdata">
						  <td colspan="<%=serviceColspan%>" >Frequency (<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)></td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getFrequency()!=null)?chargesDOB.getFrequency():""%></td>
						</tr>
      <%}
		if("on".equalsIgnoreCase(chargesDOB.getCarrierChecked()))
	   {%>
						<tr class="formdata">
						  <td colspan="<%=serviceColspan%>" >Carrier(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getCarrier()!=null)?chargesDOB.getCarrier():""%></td>
						</tr>
       	<%}
		if("on".equalsIgnoreCase(chargesDOB.getTransitTimeChecked()))
	    {%>
						<tr class="formdata">
						<%if("AIR".equalsIgnoreCase(shipmentMode)||"TRUCK".equalsIgnoreCase(shipmentMode)){%>
						  <td colspan="<%=serviceColspan%>" >Approximate Transit Time(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
						  <%}else{%>
								<td colspan="<%=serviceColspan%>" >Approximate Transit Days(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
						  <%}%>
						   <td colspan="<%=infoColspan%>"><%=(chargesDOB.getTransitTime()!=null)?chargesDOB.getTransitTime():""%></td>
						</tr>
            <%}
				if("on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
				{%>
					<tr class="formdata">
					  <td colspan="<%=serviceColspan%>" >Freight Rate Validity(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
					   <td colspan="<%=infoColspan%>"><%=(str1!=null)?str1[0]:""%></td>
					</tr>
<%               }
}
                   else
					{
     if("on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
		{

    %>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Frequency</td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getFrequency()!=null)?chargesDOB.getFrequency():""%></td>
            </tr>
            <%}
		if("on".equalsIgnoreCase(chargesDOB.getCarrierChecked()))
	   {%>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Carrier</td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getCarrier()!=null)?chargesDOB.getCarrier():""%></td>
            </tr>
     	<%}
		if("on".equalsIgnoreCase(chargesDOB.getTransitTimeChecked()))
	    {%>
			<tr class="formdata">
			<%if("AIR".equalsIgnoreCase(shipmentMode)||"TRUCK".equalsIgnoreCase(shipmentMode)){%>
              <td colspan="<%=serviceColspan%>" >Approximate Transit Time: </td>
			  <%}else{%>
			                <td colspan="<%=serviceColspan%>" >Approximate Transit Days: </td>
			  <%}%>
			   <td colspan="<%=infoColspan%>"><%=(chargesDOB.getTransitTime()!=null)?chargesDOB.getTransitTime():""%></td>
            </tr>
    <%}
			if("on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
		   {%>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Freight Rate Validity </td>
			   <td colspan="<%=infoColspan%>"><%=(str1!=null)?str1[0]:""%></td>
            </tr>
			<%}
		    	}

			}
%>


			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Incoterms: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getIncoTerms()!=null?headerDOB.getIncoTerms():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Notes: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getNotes()!=null?headerDOB.getNotes():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Date Effective: </td>
			  <td colspan="<%=infoColspan%>" ><%=effDateStr!=null?effDateStr:""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Validity of Quote: </td>
			  <td colspan="<%=infoColspan%>" ><%=validUptoStr!=null?validUptoStr:"VALID TILL FURTHER NOTICE"%></td>
            </tr>
<%			if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
			{
%>				<tr class="formdata">
				  <td colspan="<%=serviceColspan%>" >Payment Terms: </td>
				  <td colspan="<%=infoColspan%>" ><%=headerDOB.getPaymentTerms()%></td>
				</tr>
<%
			}
%>
			<tr class="formheader" cellpadding="4" align='center'>
			  <td>Charge Name</td>
			  <td>Defined By</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
<%			if("Y".equals(buyRatePermission))
			{
%>			  <td>Buy Rate</td>
			  <td>Margin Type</td>
			  <td>Margin</td>
<%			}
%>
			   <td>RSR</td>
			  <td>Discount Type</td>
			  <td>Discount</td>
			  <td>Sell Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>
            </tr>

<%
				originCharges				= finalDOB.getOriginChargesList();

				originIndices				= finalDOB.getSelectedOriginChargesListIndices();

				if(originIndices!=null)
					originChargesSize		= originIndices.length;
				else
					originChargesSize		= 0;

				if(originChargesSize>0)
				{
%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center>ORIGIN CHARGES</td>
					</tr>
<%
				}
%>

<%				color	=	"#000000";
				for(int j=0;j<originChargesSize;j++)
				{
					chargesDOB				= (QuoteCharges)originCharges.get(originIndices[j]);
					//@@Modified by Kameswari for enhancement
					/*if(chargesDOB.getSellChargeId()==null || "M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						originDiscDisabled	=	"disabled";
					else
						originDiscDisabled	=	"";

					if("D".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						originMarginDisabled	=	"disabled";
					else
						originMarginDisabled	=	"";*/
					if(chargesDOB.getSellChargeId()==null)
						originDiscDisabled	=	"disabled";
					else
						originDiscDisabled	=	"";

				   if(chargesDOB.getSellChargeId()!=null)
				        originMarginDisabled	= "disabled";
				   else
						originMarginDisabled	=	"";
				   //@@Enhancement

					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);

						if("self".equalsIgnoreCase(request.getParameter("radiobutton")))
							color = (chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getInternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					   <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBreakPoint()%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>			  		  <!-- Commented for 180161 -->
			<!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td> -->
			<!-- Added by subrahmanyam for 180161 -->
			<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
				<td><font color="<%=color%>"><%=round(chargeInfo.getBuyRate())%></font></td><!-- ended for 180161 -->
				<%}else{%>
				<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td>
				<%}%>
					  <td><font color="<%=color%>">
					  <select class="select"  name="originMarginType<%=j%>" size="1" <%=originMarginDisabled%>>
						  <option value="<%=chargeInfo.getMarginType()%>"><%="A".equalsIgnoreCase(chargeInfo.getMarginType())?"Absolute":"Percent"%></option>
					  </select></font></td>
					   <td align='center'><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getMargin()))%>" size=5 name='originMargin<%=j%>' <%=originMarginDisabled%>>
					  </td>

					  </td>
<%					}
%>
					<!-- commented for 180161 -->
					  <!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td> -->
					<!-- @@ Added by subrahmanyam for 180161  -->
					<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><font color="<%=color%>"><%=round(chargeInfo.getRecOrConSellRrate())%></font></td><!-- ended for 180161 -->
					  <%}else{%>
					  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td>
					  <%}%>
					 <td><font color="<%=color%>">
					  <select class="select"  name="originDiscountType<%=j%>" <%=originDiscDisabled%> size="1">
						  <option value="<%=chargeInfo.getDiscountType()%>"><%="A".equalsIgnoreCase(chargeInfo.getDiscountType())?"Absolute":"Percent"%></option>
						</select></font>
					  </td>
					   <td><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='originDiscount<%=j%>' <%=originDiscDisabled%>></td>

					  <!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%></font></td> --><!-- commented for 180161 -->
					  <!-- @@ Added by subrahmanyam for 180161 -->
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><font color="<%=color%>"><%=round(chargeInfo.getSellRate())%></font></td><!-- ended for 180161 -->
					  <%}else{%>
					  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%></font></td>
					  <%}%>
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					</tr>
<%
					}
				}
			color	=	"#000000";
			for(int i=0;i<chargesSize;i++)
			{
				legCharges	   = (QuoteFreightLegSellRates)charges.get(i);
				freightCharges =  legCharges.getFreightChargesList();

				frtIndices	   =  legCharges.getSelectedFreightChargesListIndices();

				if(frtIndices!=null)
					freightChargesSize = frtIndices.length;
				else
					freightChargesSize	= 0;

				if(i==0 && freightChargesSize>0)
				{
%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center>FREIGHT CHARGES</td>
					</tr>
<%
				}

				for(int j=0;j<freightChargesSize;j++)
				{
					chargesDOB				= (QuoteCharges)freightCharges.get(frtIndices[j]);
					freightChargeInfo		= chargesDOB.getChargeInfoList();
					freightChargesInfoSize	= freightChargeInfo.size();

					//@@Modified by Kameswari for enhancement
					/*if(chargesDOB.getSellChargeId()==null || "M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
					  frtDiscDisabled	=	"disabled";
					else
						frtDiscDisabled	=	"";

					if("D".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						frtMarginDisabled = "disabled";
					else
						frtMarginDisabled = "";*/
					if(chargesDOB.getSellChargeId()==null )
						frtDiscDisabled	=	"disabled";
					else
						frtDiscDisabled	=	"";

					if(chargesDOB.getSellChargeId()!=null)
						frtMarginDisabled	=	"disabled";
					else
						frtMarginDisabled	=	"";
					//@@Enhancement
					if(j==0 && freightChargesInfoSize>0)
					{
%>
						<tr class="formdata" cellpadding="4" align=center>
							<td colspan="<%=colspan%>"><B><%=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
						</tr>
<%
					}
					int tempAbs = 0; //added by subrahmanyam for 181430
					String tempDesc= "";
					for(int k=0;k<freightChargesInfoSize;k++)
					{
						//@@Added by kameswari for Surcharge Enhancements
						String temp="";
						if(k>0)
						{
							chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k-1);
							temp =chargeInfo.getRateDescription();

						}
						chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k);
						if("self".equalsIgnoreCase(request.getParameter("radiobutton")))
							color = (chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";
%>
					<tr class="formdata" cellpadding="4" align=center>
					  <!--<td><font color="<%=color%>"><%=k==0?chargesDOB.getChargeDescriptionId():""%></font></td>-->
					  <!--@@Modified by Kameswari for Surcharge Enhancements-->
					  	 <%if(chargeInfo.getRateDescription()!=null)
						{if(k==0)
						{
						 %>
						 <td><%=chargeInfo.getRateDescription()%></td>
					 <%
						}else
						{
							 if(temp.equalsIgnoreCase(chargeInfo.getRateDescription()))
						{%>
							<td>&nbsp;</td>
					 <%
						}
						 else{
					%>
					<td><%if("SBR".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) ){%>
				<%=toTitleCase(chargeInfo.getRateDescription())%>
					<%}else{
					if("truck".equalsIgnoreCase(shipmentMode)){%>
					<%=chargeInfo.getRateDescription()%>
					<%}else{%>
	<%=toTitleCase(chargeInfo.getRateDescription().substring(0,chargeInfo.getRateDescription().length()-3))%><%}}%></td>
					 <%}
						}
						}
						else
						{
							if(k==0)
							{%>
                            <td>FREIGHT RATE</td>
							<%}
							else
							{%>
							<td>&nbsp;</td>
							<%}
						}%>
						 <!--@@Modified by subrahmanyam for 181430-->
					  <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					 <%if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                                  ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                               {%>
				     <%if(tempAbs == 1 && tempDesc.equalsIgnoreCase(chargeInfo.getRateDescription())){%>
				     <!-- <td>OR</td> --><td><%=chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")?"Percent":"Flat"%></td>
					 <%tempAbs=0;}
					 else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")){%>
						  <td>Percent</td>
						  <%}else{%>
					   <!-- <td>ABSOLUTE</td> --><td>Flat</td>
					 <%}
					  }
					  else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                               ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("MIN")||chargeInfo.getBreakPoint().toUpperCase().endsWith("MIN"))
                               {tempAbs=1;%>
					<td>Min</td>
                     <%tempDesc=chargeInfo.getRateDescription();}
					   else
						   if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("BASIC")||chargeInfo.getBreakPoint().toUpperCase().endsWith("BASIC"))
					   {%>
					   <td>Basic</td>
					   <%}
						   else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("FLAT")||chargeInfo.getBreakPoint().toUpperCase().endsWith("FLAT"))
                               {%>
					  <td>Flat</td>
					  <%}
						   else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF")||chargeInfo.getBreakPoint().equalsIgnoreCase("ABSOLUTE")||chargeInfo.getBreakPoint().toUpperCase().endsWith("ABSOLUTE"))
						{%>
						<td>Absolute</td>
					  <%}
					  else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE")||chargeInfo.getBreakPoint().equalsIgnoreCase("PERCENT")||chargeInfo.getBreakPoint().toUpperCase().endsWith("PERCENT"))
                       {%>
					   <td>Percent</td>
					   <%}
					  else if (
						  chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
                               ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")
                               ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("pss"))
                               {
								   n= chargeInfo.getBreakPoint().length()-3;
                                   breakPoint= chargeInfo.getBreakPoint().substring(0,n);
								   %>
						<td><%=breakPoint%></td>
					  <%}
						else
						{%>
					   <td><font color="<%=color%>">
					  <%if(chargeInfo.getBasis()!= null && !"Per Container".equals(chargeInfo.getBasis())){%>
					  <%=chargeInfo.getBreakPoint().length()>5?chargeInfo.getBreakPoint().substring(5):chargeInfo.getBreakPoint()%></font></td>
					 <%}else{%>
					  <%=chargeInfo.getBreakPoint().length()>5?chargeInfo.getBreakPoint().substring(0,4):chargeInfo.getBreakPoint()%></font></td>
					 <%}}%>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>			  		  <!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td> --><!-- commented for 180161 -->
						<!-- added by subrahmanyam for 180161  -->
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
						<td><font color="<%=color%>"><%=round(chargeInfo.getBuyRate())%></font></td><!-- ended for 180161 -->
						<%}else{%>
						<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td>
						<%}%>
					  <td><font color="<%=color%>">
					  <select class="select"  name="frtMargin<%=i%>Type<%=j%>" size="1" <%=frtMarginDisabled%>>
						  <option value="<%=chargeInfo.getMarginType()%>"><%="A".equalsIgnoreCase(chargeInfo.getMarginType())?"Absolute":"Percent"%></option>
					  </select></font></td>
					 <td align=center><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getMargin()))%>" size=5 name='frtMargin<%=i%><%=j%>' <%=frtMarginDisabled%>></td>

<%					}
%>					<!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td> --><!-- commented for 180161 -->
						<!-- added by subrahmanyam for 180161 -->
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
			<td><font color="<%=color%>"><%=round(chargeInfo.getRecOrConSellRrate())%></font></td><!-- ended for 180161 -->
						<%}else{%>
						<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td>
						<%}%>
					  <td><font color="<%=color%>">
						<select class="select"  name="frtDiscount<%=i%>Type<%=j%>" size="1" <%=frtDiscDisabled%>>
						  <option value="<%=chargeInfo.getDiscountType()%>"><%="A".equalsIgnoreCase(chargeInfo.getDiscountType())?"Absolute":"Percent"%></option>
						</select></font>
					</td>
					 <td><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size='5' name='frtDiscount<%=i%><%=j%>' <%=frtDiscDisabled%> maxlength='10'></font></td>

					 <!--  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%></font></td> --><!-- commented for 180161 -->
					 <!-- added by subrahmanyam for 180161 -->
					 <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><font color="<%=color%>"><%=round(chargeInfo.getSellRate())%></font></td><!-- ended for 180161 -->
					  <%}else{%>
					  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%></font></td>
					  <%}%>
					 <%
								  if("FSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"FSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"BAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"PSSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || chargeInfo.getBreakPoint().endsWith("BASIC") || chargeInfo.getBreakPoint().endsWith("MIN")|| chargeInfo.getBreakPoint().toUpperCase().endsWith("BASIC") || chargeInfo.getBreakPoint().toUpperCase().endsWith("MIN"))
					  {
					  %>
					   <td>Per Shipment</td>
					   <%}
					  else if("FSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSKG".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ((chargeInfo.getBreakPoint().endsWith("FLAT")||chargeInfo.getBreakPoint().toUpperCase().endsWith("FLAT")) &&	 !"Sea".equalsIgnoreCase(shipmentMode))
						  || isNumber(chargeInfo.getBreakPoint()))
					  {%>
					  <td>Per Kilogram</td>
					   <%}
					  else if("BAFM3".equalsIgnoreCase(chargeInfo.getBreakPoint())||"PSSM3".equalsIgnoreCase(chargeInfo.getBreakPoint()))
						{%>
						<!--  <td>Per Cubic Meter</td> -->
						 <td>Per Weight Measurement</td>
						 <%}
					  else if("CAF%".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SURCHARGE".equalsIgnoreCase(chargeInfo.getBreakPoint()))
						{%>
						<td>Percent</td>
						 <%}
					  else if("CSF".equalsIgnoreCase(chargeInfo.getBreakPoint()))
					  {%>
						<td>Absolute</td>
					 <%}

					  else if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().toUpperCase().endsWith("PERCENT"))
		 			 {%>
                           <td>Percent of Freight</td>
						   <%}
						    else if ("CAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||chargeInfo.getBreakPoint().toUpperCase().endsWith("ABSOLUTE"))
					 {%>
                        <td>Per Shipment</td>
					 <%
						}

					  else
					 {
					 %>
					 <td> <%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					 <%}%>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</font></td>
					</tr>
<%
					}
				}
			}

				destCharges		= finalDOB.getDestChargesList();
				destIndices		= finalDOB.getSelctedDestChargesListIndices();

				if(destIndices!=null)
					destChargesSize	= destIndices.length;
				else
					destChargesSize	= 0;
				color	=	"#000000";
				if(destChargesSize>0)
				{
%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center>DESTINATION CHARGES</td>
					</tr>
<%
				}
				for(int j=0;j<destChargesSize;j++)
				{
					chargesDOB				= (QuoteCharges)destCharges.get(destIndices[j]);
					destChargeInfo			= chargesDOB.getChargeInfoList();
					destChargesInfoSize		= destChargeInfo.size();

					//@@Modified by Kameswari for enhancement
					/*if(chargesDOB.getSellChargeId()==null || "M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						destDiscDisabled	=	"disabled";
					else
						destDiscDisabled	=	"";

					if("D".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						destMarginDisabled	=	"disabled";
					else
						destMarginDisabled	=	"";*/
					if(chargesDOB.getSellChargeId()==null)
                       destDiscDisabled	=	"disabled";
					else
						destDiscDisabled	=	"";
					if(chargesDOB.getSellChargeId()!=null)
						destMarginDisabled	=	"disabled";
					else
						destMarginDisabled	=	"";
					//@@Enhancement
					if(j==0 && destChargesInfoSize>0)
					{
%>
						<tr class="formdata" cellpadding="4" align=center>
							<td colspan="<%=colspan%>"><B><%//=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
						</tr>
<%
					}

					for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);
						if("self".equalsIgnoreCase(request.getParameter("radiobutton")))
							color = (chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getInternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					   <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBreakPoint()%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>			  		  <!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td> --><!-- commented for 180161 -->
				<!-- added by subrahmanyam for 180161 -->
				<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
			<td><font color="<%=color%>"><%=round(chargeInfo.getBuyRate())%></font></td><!-- ended for 180161 -->
			<%}else{%>
			<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td>
			<%}%>
					<td><font color="<%=color%>">
					  <select class="select"  name="destMarginType<%=j%>" size="1" <%=destMarginDisabled%>>
						  <option value="<%=chargeInfo.getMarginType()%>"><%="A".equalsIgnoreCase(chargeInfo.getMarginType())?"Absolute":"Percent"%></option>
					 </select></font>
					  </td>
					   <td align='center'><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getMargin()))%>" size=5 name='destMargin<%=j%>' <%=destMarginDisabled%> maxlength='10'></font></td>

<%					}
%>						<!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td> --><!-- comented for 180161 -->
					<!-- added by subrahmanyam for 180161 -->
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
<td><font color="<%=color%>"><%=round(chargeInfo.getRecOrConSellRrate())%></font></td><!-- ended for 180161 -->
					<%}else{%>
					<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td>
					<%}%>
						<td><font color="<%=color%>">
						<select class="select"  name="destDiscountType<%=j%>" size="1" <%=destDiscDisabled%>>
						  <option value="<%=chargeInfo.getDiscountType()%>"><%="A".equalsIgnoreCase(chargeInfo.getDiscountType())?"Absolute":"Percent"%></option>
						</select></font>
					  </td>
					  <td><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='destDiscount<%=j%>' <%=destDiscDisabled%> maxlength='10'></font></td>

					  <!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%></font></td> --><!-- commented for 180161 -->
					  <!-- @@ Added by subrahmanyam for 180161 -->
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><font color="<%=color%>"><%=round(chargeInfo.getSellRate())%></font></td><!-- ended for 180161 -->
					  <%}else{%>
					  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%></font></td>
					  <%}%>
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					</tr>
<%
					}
				}
%>
	</table>

		<table width="100%" cellpadding="4" cellspacing="1" nowrap id="QuoteNotes" idcounter="1" defaultElement="internalNotes" xmlTagName="QuoteNotes" bgcolor='#FFFFFF'>

			<tr class='formheader'>
				<td colspan="<%=colspan%>">Notes</td>
            </tr>

			<tr class='formheader' align="center">
			  <td></td>
			  <td>Internal Notes</td>
			   <td>External Notes</td>
			  <td></td>
            </tr>
		</table>
		<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class='text'>
				  <td align="right" colspan="<%=colspan%>">
<%
				  if("self".equalsIgnoreCase(request.getParameter("radiobutton")))
				  {
%>
					  <input class="input" type="button" value="Close" onclick='window.close()'>
<%
				  }
				  else
				  {
%>					  <input class="input" name="submit" type="submit" value="<<Back">
					  <input class="input" name="submit" type="submit" value="Next>>">
<%
				  }
%>
				  <input type="hidden" name="Operation" value="<%=operation%>">
				  <input type="hidden" name="subOperation" value="SELLCHARGES">
				   <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
				  <input type="hidden" name="quoteName" value="<%=quoteName%>">
				  <input type="hidden" name="quoteStatus" value="<%= quoteStatus%>">
				   <!-- @@WPBN issue-167677 -->
          <input type="hidden" name="fromWhere" value="<%=fromWhere!=null?fromWhere:""%>">
				  </td>
            </tr>
	</table>

	</td>
 </tr>
</table>
</form>
</BODY>
</HTML>
<%
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in JSP"+e);
    logger.error(FILE_NAME+"Error in JSP"+e);
		e.printStackTrace();
	}
%>
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteView.jsp
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
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.quote.dob.QuoteMasterDOB,
				  com.qms.operations.quote.dob.QuoteFinalDOB,
				  com.qms.operations.quote.dob.QuoteAttachmentDOB,
				  com.qms.operations.quote.dob.QuoteHeader,
				  com.qms.operations.quote.dob.QuoteCharges,
				  com.qms.operations.quote.dob.QuoteChargeInfo,
				  com.qms.operations.quote.dob.QuoteFreightLegSellRates,
				  com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteView.jsp"; %>
<%
	logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
	String		  operation				  =	request.getParameter("Operation");	
	String        dateFormat			  =	loginbean.getUserPreferences().getDateFormat();
	QuoteFinalDOB        finalDOB		  =	null;
	QuoteHeader	         headerDOB		  =	null;
	QuoteMasterDOB       masterDOB		  =	null;
	QuoteAttachmentDOB   attachmentDOB    = null;

	ArrayList     charges                 = null;//@@Info of all the Legs
	ArrayList     originCharges           = null;
	ArrayList     destCharges             = null;
	ArrayList     freightCharges          = null;
	
	ArrayList     originChargeInfo        = null;
	ArrayList     destChargeInfo          = null;
	ArrayList     freightChargeInfo       = null;
	ArrayList     attachmentIdList        = null;
	
	QuoteFreightLegSellRates  legCharges  = null;
	QuoteCharges  chargesDOB			  = null;
	QuoteChargeInfo chargeInfo			  = null;
   
	
	String		  str[]				      = null;
	String		  str1[]				   = null;
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

	int[]		  originIndices			  =	null;
	int[]		  destIndices			  =	null;
	int[]		  frtIndices			  =	null;

	String[]	  notes					  =	null;
	int			  notesLength			  =	0;
	String		  disabled				  = "";
	String		  color					  = "";//included by shyam on 5/15/2006
	
	java.text.DecimalFormat df			  =	new java.text.DecimalFormat("##,###,##0.00");
    int n =0;
	String breakPoint = null;
	
	try
	{
		eSupplyDateUtility.setPatternWithTime(dateFormat);
	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
    logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
	}
	try
	{
		finalDOB		      =	(QuoteFinalDOB)session.getAttribute("viewFinalDOB");
		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
        attachmentIdList      =  finalDOB.getAttachmentDOBList();
		
		quoteDate			  =  headerDOB.getDateOfQuotation();
		effDate				  =  headerDOB.getEffDate();
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

		colspan			  = "6";
		serviceColspan	  = "3";
		infoColspan		  =	"3";

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
<HTML>
<HEAD>
<TITLE> Quote </TITLE>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script>
//@@Added by kameswari for WPBN issue-61289
var attachmentIdArray = new Array();
<%
  if(attachmentIdList!=null)
	{for(int i=0;i<attachmentIdList.size();i++)
	{
  attachmentDOB = (QuoteAttachmentDOB)attachmentIdList.get(i);
  %>
	  attachmentIdArray[attachmentIdArray.length] = '<%=attachmentDOB.getAttachmentId()%>';
	  <%}
	}%>         
function initialize()
{
	importXML('<%=request.getContextPath()%>/xml/QMSQuote.xml');
}
function initializeDynamicContentRows()
{
	var tableObj	= document.getElementById("attachmentId");
	var totalLanes	= attachmentIdArray.length ==0 ? 1 : attachmentIdArray.length;
		
	for(var i=0;i<totalLanes;i++)
	{
		createDynamicContentRow(tableObj.getAttribute("id"));
		if(attachmentIdArray.length==0)
		{	//Added by Rakesh on 25-02-2011 for Issue:236363 
			<% if(!("Pending".equalsIgnoreCase(request.getParameter("fromWhere")))) {%>
			disable();
			<% } %>//Ended by Rakesh on 25-02-2011 for Issue:236363 
			return;
		}

		var attachmentId	=	document.getElementsByName("attachmentId"); 

		attachmentId[i+1].value	=	attachmentIdArray[i];
	  disable();
	
	}
}
function chr(input)
{
}
function popUpWindow(input)
{
}
function attachmentDetails(input)
{
}
function toUpper(input)
{
}

function disable()
{
 
	for(var i=0;i<document.forms[0].elements.length;i++)
	{

	if(document.forms[0].elements[i].type=='button'||document.forms[0].elements[i].type=='text')
		{
		document.forms[0].elements[i].disabled=true;
		}
	}
}
//@@WPBN issue-61289
</script>
</HEAD>
<BODY onload="initialize();">
<form method='post' action='QMSQuoteController'>
<table width="100%" cellpadding="0" cellspacing="0">
 <tr>
	<td>
		<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td></tr></table></td>
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
              <!-- @@ Commented by subrahmanyam for the wpbn issue: 150460 on 23/12/2008 -->
              <!-- <td colspan="<%=serviceColspan%>" >Cargo Acceptance Place: </td> -->
<!-- @@ Added by subrahmanyam for the WPBN issue: 150460 on 23/12/2008 -->
			  <% if("EXW".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FAS".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FCA".equalsIgnoreCase(headerDOB.getIncoTerms()) ||"FOB".equalsIgnoreCase(headerDOB.getIncoTerms()))
				{%>
					<td colspan="<%=serviceColspan%>" >Place Of Acceptance: </td>
				<%}else{%>
					<td colspan="<%=serviceColspan%>" >Place Of Delivery: </td>
					<%}%>
<!-- @@ Ended by subrahmanyam for the WPBN issue: 150460 on 23/12/2008 -->
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
				{ if("Y".equalsIgnoreCase(chargesDOB.getFrequencyChecked())||"on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
		{
			%>             <tr class="formdata">
						  <td colspan="<%=serviceColspan%>" >Frequency (<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)></td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getFrequency()!=null)?chargesDOB.getFrequency():""%></td>
						</tr>
      <%}
		if("Y".equalsIgnoreCase(chargesDOB.getCarrierChecked())||"on".equalsIgnoreCase(chargesDOB.getCarrierChecked()))
	   {%>
						<tr class="formdata">
						  <td colspan="<%=serviceColspan%>" >Carrier(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getCarrier()!=null)?chargesDOB.getCarrier():""%></td>
						</tr>
       	<%}
		if("Y".equalsIgnoreCase(chargesDOB.getTransitTimeChecked())||"on".equalsIgnoreCase(chargesDOB.getTransitTimeChecked()))
	    {%>     
						<tr class="formdata">
						  <td colspan="<%=serviceColspan%>" >Approximate Transit Time(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
						   <td colspan="<%=infoColspan%>"><%=(chargesDOB.getTransitTime()!=null)?chargesDOB.getTransitTime():""%></td>
						</tr><%}
				if("Y".equalsIgnoreCase(chargesDOB.getRateValidityChecked())||"on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
				{%>
					<tr class="formdata">
					  <td colspan="<%=serviceColspan%>" >Freight Rate Validity(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
					   <td colspan="<%=infoColspan%>"><%=(str1!=null)?str1[0]:""%></td>
					</tr>
<%               }
}
                   else
					{
     if("Y".equalsIgnoreCase(chargesDOB.getFrequencyChecked())||"on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
		{
		
    %>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Frequency</td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getFrequency()!=null)?chargesDOB.getFrequency():""%></td>
            </tr>
            <%}
		if("Y".equalsIgnoreCase(chargesDOB.getCarrierChecked())||"on".equalsIgnoreCase(chargesDOB.getCarrierChecked()))
	   {%>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Carrier</td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getCarrier()!=null)?chargesDOB.getCarrier():""%></td>
            </tr>
     	<%}
		if("Y".equalsIgnoreCase(chargesDOB.getTransitTimeChecked())||"on".equalsIgnoreCase(chargesDOB.getTransitTimeChecked()))
	    {%>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Approximate Transit Time </td>
			   <td colspan="<%=infoColspan%>"><%=(chargesDOB.getTransitTime()!=null)?chargesDOB.getTransitTime():""%></td>
            </tr>
    <%}
			if("Y".equalsIgnoreCase(chargesDOB.getRateValidityChecked())||"on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
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
			  <td>Breakpoint</td>
			  <td>Currency</td>
			  <td>Rate</td>
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
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();
					
					for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
						//System.out.println("Margin test Flag---"+request.getParameter("radiobutton"));

						//Included by Shyam on 5/15/2006
						if("self".equalsIgnoreCase(request.getParameter("radiobutton")))
							color = (chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBreakPoint()%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
					  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					</tr>
<%
					}
				}
			
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
					
					if(j==0 && freightChargesInfoSize>0)
					{
%>
						<tr class="formdata" cellpadding="4" align=center>
							<td colspan="<%=colspan%>"><B><%=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
						</tr>
<%
					}
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
					  	 <%if(k==0)
						{
						 %>
						 <td><%=chargeInfo.getRateDescription()%></td>
					 <%
						}else if(temp.equalsIgnoreCase(chargeInfo.getRateDescription()))
						{%>
							<td>&nbsp;</td>
					 <%
						}
						 else{
					%>
					<td><%=chargeInfo.getRateDescription()%></td>
					 <%}%>
					<%if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                                  ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                               {%>
				     <td>OR</td>
					 <%}
					  else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                               ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN"))
                               {%>
					<td>MIN</td>
                     <%}
					   else
						   if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC"))
					   {%>
					   <td>BASIC</td>
					   <%}
						   else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG"))
                               {%>
					  <td>FLAT</td>
					  <%}
						   else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
						{%>
						<td>ABSOLUTE</td>
					  <%} 
					  else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
                       {%>
					   <td>PERCENT</td>
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
					  <td><font color="<%=color%>"><%=chargeInfo.getBreakPoint()%></font></td>
					 <%}%>  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
					  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%></font></td>
					  <!--<td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>-->
					 
					 <td> <%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
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
					  <td><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBreakPoint()%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
					  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly></font></td>
					</tr>
<%
					}
				}
%>
	</table>
	<!--@@Added by kameswari for WPBN issue-61289-->
	<table width="100%" cellpadding="4" cellspacing="2" nowrap id="attachmentId" idcounter="1"   defaultElement="attachmentId"  xmlTagName="AttachmentId"  bgcolor='#FFFFFF'>
	<tr class='formdata'>
		<td width="25%">&nbsp;</td>
		<td width="50%" align="center"> AttachmentId</td>
		<td width="25%">&nbsp;</td>
	</tr>
   </table>
   <!--@@WPBN issue-61289-->
	<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>

			<tr class='formheader' align='center'>
				<td colspan="<%=colspan%>">Notes</td>
            </tr>
<%
			if(finalDOB!=null)
			{
				notes			=	finalDOB.getExternalNotes();
				if(notes!=null)
					notesLength		=	notes.length;
				if("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()) || 
				   "REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()) || 
				   "I".equalsIgnoreCase(finalDOB.getFlagsDOB().getActiveFlag()))
					disabled		=	"disabled";
			}

			for(int i=0;i<notesLength;i++)
			{
				if(notes[i]!=null && notes[i].trim().length()!=0)
				{
%>
			<tr class='formdata' align="center"> 
			  <td colspan="<%=colspan%>">
					<!--<textarea name='notes' class="text" style="overflow:visible;height: 15" cols="25" rows='4' readonly><%=notes[i]%></textarea>--><!--Modified by Kameswari for the internal notes enhancement-->
					<textarea name='notes' class="text" cols="25" rows='4' readonly><%=notes[i].trim()%></textarea>
			  </td>
			</tr>
<%
				}
			}
			if("summary".equalsIgnoreCase(request.getParameter("from")))
			{
%>		
			<tr align='center' class='formheader'>
				<td colspan="<%=colspan%>">Send Options</td>
			</tr>
			<tr>
				<td colspan="<%=colspan%>" class='formdata' align='center'>
					<INPUT TYPE="checkbox" NAME="email" <%="Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEmailFlag())?"checked":""%> <%=disabled%>>&nbsp;Email
					<INPUT TYPE="checkbox" NAME="fax" <%="Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getFaxFlag())?"checked":""%> <%=disabled%>>&nbsp;Fax
					<INPUT TYPE="checkbox" NAME="print" <%="Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getPrintFlag())?"checked":""%> <%=disabled%>>&nbsp;Print
				</td>
			</tr>
			<input type="hidden" name="subOperation" value="SUMMARY">
<%
			}
%>
		</table>
		
		<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class='text'> 
				  <td width="18%" align="right" colspan="<%=colspan%>"> 
<%
				  if("Pending".equalsIgnoreCase(request.getParameter("fromWhere")))
				  {
%>					<input type="button" class='input' name="next" value="Close" onclick='window.close()'>
<%				  }
				  else
				 {
%>					<input type="submit" class='input' name="next" value="<%="summary".equalsIgnoreCase(request.getParameter("from"))?"Next>>":"Continue"%>">
<%				 }
%>
				  <input type="hidden" name="Operation" value="<%=operation%>">
				  <input type="hidden" name="operate" value="view">
          <input type="hidden" name="fromWhere" value="<%=request.getParameter("fromWhere")%>">
				  <input type="hidden" name="from" value="<%=request.getParameter("from")!=null?request.getParameter("from"):""%>">
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
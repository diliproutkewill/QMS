<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteFreightSellRatesView.jsp
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
				  java.util.Set,
					java.util.LinkedHashSet,
					java.util.Iterator,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.quote.dob.QuoteMasterDOB,
				  com.qms.operations.quote.dob.QuoteFinalDOB,
				  com.qms.operations.quote.dob.QuoteFreightLegSellRates,
				  com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteFreightSellRatesView.jsp"; %>

<%
logger  = Logger.getLogger(FILE_NAME);	
 QuoteFinalDOB					finalDOB			=	null;
QuoteMasterDOB					masterDOB			=	null;
ArrayList						freightRates		=	null;
ArrayList						sellRates			=	null;
ArrayList						frtTiedCustInfo		=	null;
String[]						slabWeightBreaks	=	null;
String[]						listWeightBreaks	=	null;
String[] rateDesc=null;
QuoteFreightLegSellRates		legRateDetails		=	null;
QuoteFreightLegSellRates		tiedCustLegDOB		=	null;
QuoteFreightRSRCSRDOB			sellRateDOB			=	null;
String							shipmentMode		=	null;
String							weightBreak			=	null;
String[]						rate				=	null;
String[]						str					=	null;
String[]                        checked             =   null; 
String							operation			=	null;
int								freightRatesSize	=	0;
int								sellRatesSize		=	0;
int								tableCount			=	0;
int								tiedCustInfoSize	=	0;
int                             tempcount           =  0;
ESupplyDateUtility				eSupplyDateUtility	=	new ESupplyDateUtility();

Timestamp						effectiveFrom		=	null;
Timestamp						validUpto			=	null;

String							effectiveFromStr	=	null;
String							validUptoStr		=	null;

String							checkedFlag			=	"";
boolean            disabled            =  true;
boolean							noRatesFlag			=	false;
boolean							displayFlag			=	false;
String                          quoteStatus         =  null;	 // @@Added by VLAKSHMI for the WPBN issue-167677 
String                          quoteName           =  null;	 //@@Added by VLAKSHMI for the WPBN issue-167677 
try
{
	eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());

	java.text.DecimalFormat df	=new java.text.DecimalFormat("##0.00");
       quoteStatus      = request.getParameter("quoteStatus");	// @@Added by VLAKSHMI for the WPBN issue-167677 
	   quoteName      = request.getParameter("quoteName");	 //@@Added by VLAKSHMI for the WPBN issue-167677
	operation			=	request.getParameter("Operation");
	finalDOB			=	(QuoteFinalDOB)session.getAttribute("viewFinalDOB");
	masterDOB			=	finalDOB.getMasterDOB();
	freightRates		=	finalDOB.getLegDetails();
	//frtTiedCustInfo     =	finalDOB.getTiedCustomerInfoFreightList();

	freightRatesSize	=	freightRates.size();
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
<TITLE>Quote - Freight Sell Rates</TITLE>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript">

var noOfLegs	=	<%=freightRatesSize%>;
function validate()
{
	
	for(i=0;i<noOfLegs;i++)
	{
		var radio	=	document.getElementsByName("leg"+i);
		if(radio.length > 0 && document.getElementById("hid"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
		{
			alert("Please select one sell rate in Leg "+(i+1));
			radio[0].focus();
			return false;
		}
	}
}

function setSelectedIndex(obj,index)
{
	//alert(obj.value);
	document.getElementById("hid"+index).value	=	obj.value;
}
function setIndexForChecked(btn)
{
	var radio = '';
	
	for(var i=0;i<noOfLegs;i++)
	{
		radio = document.getElementsByName('leg'+i);
		for(var j=0;j<radio.length;j++)
		{
			if(radio[j].checked)
				document.getElementById("hid"+i).value	=	radio[j].value;
		}
	}

	setName(btn);
}
function setName(btn)
{
	document.forms[0].btnName.value=btn.value;
}

</script>
</HEAD>

<BODY>
       <form method="post" name="sellRates" action="QMSQuoteController" onSubmit='return validate()'>

	<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
		<tbody>
			<tr color='#FFFFFF'>
				<td>
				<table border="0" cellPadding="4" cellSpacing="1" width="100%">
					<tbody>
						 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
						<tr class="formlabel">
							<td colspan=""><p ><b><%=shipmentMode%> FREIGHT SELL RATES(<%=quoteName%>,<%=quoteStatus%>) - <%=operation!=null?operation.toUpperCase():""%></b></td>       
						</tr>
							 <!-- @@WPBN issue-167677 -->
					</tbody>
				</table>
<%	
	//Logger.info(FILE_NAME,"request.getAttribute(fromWhere)::"+(String)request.getAttribute("fromWhere"));

	for(int i=0;i<freightRatesSize;i++)//@@noOfLegs
	{
		noRatesFlag			=	false;
		legRateDetails		=	(QuoteFreightLegSellRates)freightRates.get(i); 

		slabWeightBreaks	=	legRateDetails.getSlabWeightBreaks();

		listWeightBreaks	=	legRateDetails.getListWeightBreaks();
		sellRates			=	legRateDetails.getRates();

		//tiedCustLegDOB		=	(QuoteFreightLegSellRates)frtTiedCustInfo.get(i);

		boolean headerSet		=	false;
		boolean flatHeaderSet	=	false;
		boolean slabHeaderSet	=	false;
		boolean listHeaderSet	=	false;
		boolean commonHeader	=	false;

		if(!legRateDetails.isSpotRatesFlag() && tiedCustLegDOB==null)
		{
	
			if(sellRates!=null)
				sellRatesSize	=	sellRates.size();
			else
				sellRatesSize	=	0;
			
			if(sellRatesSize > 0)
				noRatesFlag	= true;
	

			for(int j=0;j<sellRatesSize;j++)
			{
				sellRateDOB		=	(QuoteFreightRSRCSRDOB)sellRates.get(j);

				effectiveFrom	=   sellRateDOB.getEffDate();
				checkedFlag		=	sellRateDOB.getSelectedFlag();
				rateDesc= sellRateDOB.getRateDescriptions();

				if(effectiveFrom!=null)
				{
					str				=	eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
					effectiveFromStr= str[0];
				}
				else
				{
					effectiveFromStr	=	"";
				}
				
				validUpto		=   sellRateDOB.getValidUpTo();
				
				if(validUpto!=null)
				{
					str				= eSupplyDateUtility.getDisplayStringArray(validUpto);
					validUptoStr	= str[0];
				}
				else
				{
					validUptoStr	=	"";
				}

				if(!headerSet)
				{
					headerSet	=	true;
%>
					<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr class="formdata" >
								<td>
									<font size='0.5%'><b>Selected Freight Rates: </b><%=legRateDetails.getOrigin()+"-"+legRateDetails.getDestination()%>
								</td></font>
							</tr>
						</tbody>
					</table>
<%
				}
				
				if("Flat".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !flatHeaderSet)
				{
					commonHeader=false;
%>
					<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr class="formdata" >
							  <td>
								<font size='0.5%'><b>Weight Break:</b> FLAT
							  </td></font>
							</tr>
						</tbody>
					</table>
<%
				}
				else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !slabHeaderSet)
				{
					commonHeader=false;
					if(listHeaderSet)
					{
%>						</tbody>
						</table>
<%					}
%>
					
					<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr class="formdata">
								<font size='0.5%'><td><b>Weight Break:</b> SLAB</td></font>
							</tr>
						</tbody>
					</table>
<%
				}
				else if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !listHeaderSet)
				{
					commonHeader=false;
					if(flatHeaderSet)
					{
%>						</tbody>
						</table>
<%					}
%>
					<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr class="formdata">
								<font size='0.5%'><td><b>Weight Break:</b> LIST</td></font>
							</tr>
						</tbody>
					</table>
<%
				}
				if(!commonHeader)
				{
					commonHeader=true;
%>

					<table id="table<%=tableCount%>" border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr valign="top" class='formheader'align="center" >
								<TH ><font size='0.5%'>Select</TH></font>
								<TH ><font size='0.5%'>Org</TH></font>
								<TH ><font size='0.5%'>Dest</TH></font>
								<TH ><font size='0.5%'>Carrier</TH></font>
								<TH ><font size='0.5%'>Service&nbsp;Level</TH></font>
								<TH ><font size='0.5%'>Frequency</TH></font>
								<%if("AIR".equalsIgnoreCase(shipmentMode)||"TRUCK".equalsIgnoreCase(shipmentMode)){%>
								<TH><font size='0.5%'>Transit&nbsp;Time</TH></font>
							<%}
							else{%>
									<TH><font size='0.5%'>Transit&nbsp;Days</TH></font>
								<%}%>
<%
					if("Flat".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !flatHeaderSet)
					{
						flatHeaderSet=true;
							String[] flatwtBreaks= sellRateDOB.getFlatWeightBreaks();
						for(int k=0;k<flatwtBreaks.length;k++)
						{
							if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k])){

%>
								<TH nowrap><font size='0.5%'><%=flatwtBreaks[k]%></TH></font>
<%}
						}


%>
							<!-- 	<TH ><font size='0.25%'>MIN</TH></font>
								<TH ><font size='0.25%'>FLAT</TH></font> -->
								<!-- @@ Added by subrahmanyam for the Enhancement 180161 on 31/08/09 -->
								<%
									//if("AIR".equalsIgnoreCase(shipmentMode) || 1==legRateDetails.getShipmentMode()){
%>
								<!-- <TH ><font size='0.25%'>FS<br>BASIC</TH></font>
								<TH ><font size='0.25%'>FS<br>MIN</TH></font>
								<TH ><font size='0.25%'>FS<br>KG</TH></font>
								<TH ><font size='0.25%'>SS<br>BASIC</TH></font>
								<TH ><font size='0.25%'>SS<br>MIN</TH></font>
								<TH ><font size='0.25%'>SS<br>KG</TH></font> -->
<%
									//}else if("TRUCK".equalsIgnoreCase(shipmentMode) || 4==legRateDetails.getShipmentMode()){
									%><!-- Added by subrahmanyam for 192404 on 16-dec-09-->
								<!-- <TH ><font size='0.25%'>SURCHARGE</TH></font> -->
								<%//}
								//else{%>
							<!-- 	<TH ><font size='0.25%'>CAF<br>MIN</TH></font>
								<TH ><font size='0.25%'>CAF<br>%</TH></font>
								<TH ><font size='0.25%'>BAF<br>MIN</TH></font>
								<TH ><font size='0.25%'>BAF<br>M3</TH></font>
								<TH ><font size='0.25%'>PSS<br>MIN</TH></font>
								<TH ><font size='0.25%'>PSS<br>M3</TH></font>
								<TH ><font size='0.25%'>CSF</TH></font> -->
						<!-- @@ Ended by subrahmanyam for the Enhancement 180161 on 31/08/09 -->
<%//}
					}
					else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !slabHeaderSet)
					{
						slabHeaderSet=true;
						for(int k=0;k<slabWeightBreaks.length;k++)
						{
                     if("A FREIGHT RATE".equalsIgnoreCase(sellRateDOB.getRateDescriptions()[k])||    "FREIGHT RATE".equalsIgnoreCase(sellRateDOB.getRateDescriptions()[k])){%>
								<TH nowrap><font size='0.5%'><%=slabWeightBreaks[k]%></TH></font>
<%		}
						}
					}
					else if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !listHeaderSet)
					{
						listHeaderSet=true;
					 
							  
%>
								<TH><font size='0.25%'>Container<br>Type</TH></font>
								<TH><font size='0.25%'>Rates</TH></font>
<%		  
						
					}
%>
								
								<TH><font size='0.5%'>RSR/CSR/BR</TH></font>
								<TH><font size='0.5%'>Defined&nbsp;By</TH></font>
								<TH><font size='0.5%'>Currency&nbsp;Id</TH></font>
								<TH><font size='0.5%'>Effective&nbsp;From</TH></font>
								<TH><font size='0.5%'>Valid&nbsp;Upto</TH></font>
								<!-- Modified by Mohan for Issue 219976  on 09112010 -->
								<TH><font size='0.5%'>Internal Notes</TH></font>
								<TH><font size='0.5%'>External Notes</TH></font>
							</tr>
<%
					tableCount++;
				}

					   if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && listHeaderSet && "FCL".equalsIgnoreCase(sellRateDOB.getConsoleType()))
					{   tempcount           =   0;
						rate	=	sellRateDOB.getChargeRates();
						listWeightBreaks	=	sellRateDOB.getListWeightBreaks();
						checked =   sellRateDOB.getCheckedFalg();
	                    disabled=true;
						int ratesSize=rate.length;
						int containerCount	=	0;
						int checkIndex =0;//added by subrahmanyam for the pbn id: 186812 on 22/oct/09
							for(int k=0;k<ratesSize;k++)
								{
									if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]))
											containerCount++;
								}

								for(int k=0;k<ratesSize;k++)
								{
								if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]))
									{//##2
					            if(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k])) //commented by subrahmanyam for 180161
								  // if(rate[k]!=null)//added by subrahmanyam for 180161
									{ //##1
%>
							<tr valign="top" class='formdata' align="center">
							<% if(tempcount==0) {%>
								<td><input type="radio" value="<%=j%>" name="leg<%=i%>"  <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onClick="setSelectedIndex(this,'<%=i%>','<%=i%><%=j%>','<%=containerCount%>');">
								<input type="hidden" id="wtBreakType<%=i%><%=j%>" name="wtBreakType" value='<%=sellRateDOB.getWeightBreakType()%>'>
								<input type="hidden" id="consoleType<%=i%><%=j%>" name="consoleType" value='<%=sellRateDOB.getConsoleType()%>'>
								<input type="hidden" id="checkCount<%=i%><%=j%>" name="checkCount" value='<%=containerCount%>'>
								</td>
								<% tempcount++;} else { %>
								<!-- Modified by subrahmanyam for the pbn id: 186812 on 22/oct/09, checkIndex was replaced in the place of k -->
								<td></td> <%}%>

								<input type="hidden" id="con<%=i%><%=j%><%=checkIndex%>" name="con<%=i%><%=j%>">


								</td>
								<td><font size='0.25%'><%=sellRateDOB.getOrigin()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getDestination()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCarrierId()%></td></font>
								<!--@@Modified by kameswari for the WPBN issue-31330-->
								<!--td><%//=sellRateDOB.getServiceLevelId()%></td-->
								<td><font size='0.25%'><%=sellRateDOB.getServiceLevelDesc()%></td></font>
								<!-- @@Kameswari -->
								<td><font size='0.25%'><%=sellRateDOB.getFrequency()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getTransitTime()%></td></font>
                                  <td><font size='0.25%'><%=listWeightBreaks[k]%></td></font> 
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td></font>
								<!--td><%//=sellRateDOB.getWeightClass()%></td-->
								<td><font size='0.25%'><%=sellRateDOB.getRsrOrCsrFlag()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCreatedTerminalId()!=null?sellRateDOB.getCreatedTerminalId():""%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCurrency()!=null?sellRateDOB.getCurrency():""%></td></font>
								<td nowrap><font size='0.25%'><%=effectiveFromStr!=null?effectiveFromStr:""%></td></font>
								<td nowrap><font size='0.25%'><%=validUptoStr!=null?validUptoStr:""%></td></font>
								<%if(k==0){%>
								<td><font size='0.25%'><input class="text" id="Notes<%=j%>" name="notes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly></td></font>
								<td><font size='0.25%'><input class="text" id="extNotes<%=j%>" name="extNotes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td></font>
								<%}else{%>
								<td></td>
								<td></td>
								<%}%>

							</tr>
							<%	}//End of ##1
								}// End of ##2
								%>
								<!--@@ Commented by subrahmanyam for the Enhancement 180164 on 31/08/09  -->
								 <input type="hidden" id="rateVal<%=i%><%=j%><%=k%>" name="rateVal" value='<%=rate[k]%>'>
			<% checkIndex++;//added by subrahmanyam for the pbn id:186812 on 22/oct/09
								 //@@ Added by subrahmanyam for the Enhancement 180164 on 31/08/09  
 									if(k>=4){%>
                  <input type="hidden" id="rateVal<%=i%><%=j%><%=k-4%>" name="rateVal" value='<%=rate[k-4]%>'>
                  <%}else{%>
                  <input type="hidden" id="rateVal<%=i%><%=j%><%=k%>" name="rateVal" value='<%=rate[k]%>'>
                  <%}
					}
						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: rateDesc){
							if(!"-".equalsIgnoreCase(s))
							rdesc.add(s);
						}
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());
										for(int x=1;x<surchargeDesc.size();x++){//##3
												String surcharge	=	 (String	)surchargeDesc.get(x);
												int tempCount	=	0;
												int frtCount				=	0;
												int surChargeCount = 0;%>
						<tr valign="top" class='formdata' align="center">
						<td /><td/>
						<td ><font size='0.25%'><%=surcharge.substring(0,surcharge.length()-3)%></font></td>
						<%	for(int sd=0;sd<ratesSize;sd++)
								{//##4
										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDesc[sd]) && surcharge.equalsIgnoreCase(rateDesc[sd]) ){
%>
					<td ><font size='0.25%'><%=listWeightBreaks[sd].substring(0,4)%></font></td>


	<%									}
								}//## 4
								//@@Added by kiran.v on 27/07/2011 for Wpbn Issue-
							for(int td=0;td<11;td++){//## td
							%>
								<td/>
							<%} //End of ##td%>
						</tr>
							<tr valign="top" class='formdata' align="center">
						<td /><td/><td/>
								<%	for(int sd=0;sd<ratesSize;sd++)
								{ //##5
										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDesc[sd]) && surcharge.equalsIgnoreCase(rateDesc[sd])) {
%>
							<td ><font size='0.25%'><%=rate[sd]%></font></td>


	<%									}
								}//end of ##5
							for(int td=0;td<11;td++){
							%>
								<td/>
							<%}%>
				<%			}//end of ##3


								//}


		
		}else {  %>
							
							
							<tr valign="top" class='formdata' align="center">
								<td><input type="radio" value="<%=j%>" name="leg<%=i%>" <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onClick="setSelectedIndex(this,'<%=i%>');"></td>
								<td><font size='0.5%'><%=sellRateDOB.getOrigin()%></td></font>
								<td><font size='0.5%'><%=sellRateDOB.getDestination()%></td></font>
								<td><font size='0.5%'><%=sellRateDOB.getCarrierId()%></td></font>
								<!--@@Modified by kameswari for the WPBN issue-31330-->
								<!--td><%//=sellRateDOB.getServiceLevelId()%></td-->
								<td><font size='0.5%'><%=sellRateDOB.getServiceLevelDesc()%></td></font>
								<!-- @@Kameswari -->
								<td><font size='0.5%'><%=sellRateDOB.getFrequency()%></td></font>
								<td><font size='0.5%'><%=sellRateDOB.getTransitTime()%></td></font>
<%
								rate	=	sellRateDOB.getChargeRates();
								for(int k=0;k<rate.length;k++)
								{
									// Added By Kishore For Weight Break in Single Quote
									if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k])) {
%>
									<td  nowrap><font size='0.5%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td></font>
<%
								}
								}
%>
								<!--td><%//=sellRateDOB.getWeightClass()%></td-->
								<td><font size='0.5%'><%=sellRateDOB.getRsrOrCsrFlag()%></td></font>
								<td><font size='0.5%'><%=sellRateDOB.getCreatedTerminalId()!=null?sellRateDOB.getCreatedTerminalId():""%></td></font>
								<td><font size='0.5%'><%=sellRateDOB.getCurrency()!=null?sellRateDOB.getCurrency():""%></td></font>
								<td nowrap><font size='0.5%'><%=effectiveFromStr!=null?effectiveFromStr:""%></td></font>
								<td nowrap><font size='0.5%'><%=validUptoStr!=null?validUptoStr:""%></td></font>
								<!-- Modified by Mohan for Issue 219976  on 09112010 -->
								<td><input class="text" id="Notes" name="notes" size="8" maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly ></td>
								<td><input class="text" id="extNotes" name="extNotes" size="8" maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td>
							</tr>

<%						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: rateDesc){
							System.out.println("rateDesc----------"+s);
							if(!"-".equals(s) && !"".equals(s))
							rdesc.add(s);
						}
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());
%>

<%
	//System.out.println("surchargeDesc..."+surchargeDesc);
	for(int x=1;x<surchargeDesc.size();x++){
	String surcharge	=	 (String	)surchargeDesc.get(x);
	int tempCount	=	0;	
	int frtCount				=	0;
	int surChargeCount = 0;
	%>
	<tr valign="top" class='formdata' align="center">
	<td />
	<td ><font size='0.25%'><%=!"truck".equalsIgnoreCase(shipmentMode)?surcharge.substring(0,surcharge.length()-3):surcharge%></td></font><!--modified by silpa.p on 7-06-11 font change for rate desc-->
<%
							String[] flatwtBreaks= sellRateDOB.getFlatWeightBreaks();
							String[] slabWtBreaks= sellRateDOB.getSlabWeightBreaks();
	if("Flat".equalsIgnoreCase(sellRateDOB.getWeightBreakType())){
	for(int k=0;k<flatwtBreaks.length;k++)
	{
		if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k]))
		frtCount ++;
	}
						for(int k=0;k<flatwtBreaks.length;k++)
						{
							if(surcharge.equalsIgnoreCase(rateDesc[k])){
								surChargeCount++;
%>
								<td nowrap><font size='0.5%'><%=flatwtBreaks[k].length()>=5?flatwtBreaks[k].substring(5):flatwtBreaks[k]%></td></font>
<%}
						}
	}
	else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType())){
		for (String slab: slabWtBreaks)
			System.out.print(slab+',');
			System.out.println();
	for(int k=0;k<slabWtBreaks.length;k++)
	{
		if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k]))
		frtCount ++;
	}
						for(int k=0;k<slabWtBreaks.length;k++)
						{
							if(surcharge.equalsIgnoreCase(rateDesc[k])){
								surChargeCount++;
								System.out.println("suchrage & rateDesc[k] & slabWtBreaks.."+surcharge +" & "+rateDesc[k] +" & "+ slabWtBreaks[k] );
%>
								<td nowrap><font size='0.5%'><%=(slabWtBreaks[k].length()>=5 && !"Truck".equalsIgnoreCase(shipmentMode))?slabWtBreaks[k].substring(5):slabWtBreaks[k]%></td></font>
<%}
						}
	}
			tempCount = 14+frtCount-(surChargeCount+2);
			//System.out.println("----a-----tempCount------"+tempCount);
		//	System.out.println("----a-----frtCount------"+frtCount);
			//System.out.println("----a-----surChargeCount------"+surChargeCount);
			for(int td=0;td<tempCount;td++)
	{
%>
	<td/>
	<%}
%>
</tr>
<tr valign="top" class='formdata' align="center">
<td/><td/>

	<%							for(int k=0;k<rate.length;k++)
								{
									if(surcharge.equalsIgnoreCase(rateDesc[k]) ){
%>
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td>
<%				
			}
		}	
					//System.out.println("----b-----tempCount------"+tempCount);
					//System.out.println("----b-----frtCount------"+frtCount);
					//System.out.println("----b-----surChargeCount------"+surChargeCount);
		for(int td=0;td<tempCount;td++)
				{
%>
	<td/>
	<%}
%>
		</tr>
<%}
%>

<%}
			}
			
	
				if(!noRatesFlag)
				{
					displayFlag	=	true;
%>				<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
					<tbody>				
					 <tr color="#FFFFFF">
						<td align="center">
						<font face="Verdana" size="2" color='red'>
						<b>No Rates Are Defined for the Leg <%=legRateDetails.getOrigin()!=null?legRateDetails.getOrigin():""%>-<%=legRateDetails.getDestination()!=null?legRateDetails.getDestination():""%>.</b></font>
						</td>
					</tr>
				</tbody>
				</table>
<%
				}
%>
				<input type="hidden" id="hid<%=i%>" name="hid<%=i%>">
<%		}
%>

<%				
		}			
%>
				

			<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
				<tbody>
					<tr class="text">
						<td></td>
						<td align="right" >
						<input type="hidden" name="Operation" value="<%=operation%>">
						<input type="hidden" name="subOperation" value="SELLRATES">
						<input type="hidden" name="fromWhere" value="<%=(String)request.getAttribute("fromWhere")%>">
						<input type="hidden" name="btnName">
						 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
						<input type="hidden" name="quoteName" value="<%=quoteName%>">
				       <input type="hidden" name="quoteStatus" value="<%= quoteStatus%>">
					   	 <!-- @@WPBN issue-167677 -->
						<input class="input" name="submit" type="submit" id='submit' value="<< Back" onClick="setIndexForChecked(this)"></td>
						<%if(!displayFlag){%>
						<td><input class="input" name="submit" type="submit" value="Next >>" onClick="setIndexForChecked(this)"></td>
						<%}%>
						
					</tr>            
				</tbody>
			</table> 
		</table>

			</td>
		</tr>
	</tbody>
</table>
</form>
</BODY>
</HTML>

<%
}
catch(Exception e)
{
	e.printStackTrace();
	//Logger.error(FILE_NAME,"Error in jsp"+e.toString());
  logger.error(FILE_NAME+"Error in jsp"+e.toString());
}
finally
{
	legRateDetails		=	null;
	
	slabWeightBreaks	=	null;

	listWeightBreaks	=	null;
	masterDOB			=	null;
	finalDOB			=	null;
	sellRateDOB			=	null;
}
%>
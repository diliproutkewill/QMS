<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteSummary.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
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
				  com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB,
				   java.util.StringTokenizer"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteSummary.jsp"; 
  
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
		}%>
<%
	logger  = Logger.getLogger(FILE_NAME);
	ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
	String		  operation				  =	request.getParameter("Operation");
	String        dateFormat			  =	loginbean.getUserPreferences().getDateFormat();
	QuoteFinalDOB finalDOB				  =	null;
	QuoteHeader	  headerDOB				  =	null;
	QuoteMasterDOB masterDOB			  =	null;
	QuoteAttachmentDOB  attachmentDOB     = null;

	ArrayList     charges                 = null;//@@Info of all the Legs
	ArrayList     originCharges           = null;
	ArrayList     destCharges             = null;
	ArrayList     freightCharges          = null;
	ArrayList     attachmentIdLOVList     = null;
	ArrayList     attachmentIdList        = null;

	ArrayList     originChargeInfo        = null;
	ArrayList     destChargeInfo          = null;
	ArrayList     freightChargeInfo       = null;

	QuoteFreightLegSellRates  legCharges  = null;
	QuoteCharges  chargesDOB			  = null;
	QuoteChargeInfo chargeInfo			  = null;

	String accessType                      =null;
	String		  str[]				      = null;
	String		  str1[]				   = null;//@@Added by Kameswari for the WPBN issue-146448
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
	String		  fromWhere				  = null;
  String       update           = null;
  String    quoteName            = null; // @@Added by VLAKSHMI for the WPBN issue-167677
  String                  quoteStatus         = null;	 // @@Added by VLAKSHMI for the WPBN issue-167677
String                     completeFlag         = null;
	 int n =0;
	String breakPoint = null;
	java.text.DecimalFormat df			  =	new java.text.DecimalFormat("##,###,##0.00");


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
		quoteName			=	request.getParameter("quoteName"); //@@Added by VLAKSHMI for the WPBN issue-167677
			quoteStatus			=	request.getParameter("quoteStatus"); //@@Added by VLAKSHMI for the WPBN issue-167677
			completeFlag			=	request.getParameter("completeFlag"); //@@Added by VLAKSHMI for the WPBN issue-167677
		finalDOB		      =	(QuoteFinalDOB)session.getAttribute("finalDOB");
		attachmentIdLOVList   = (ArrayList)request.getAttribute("attachmentIdLOVList");
		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
		attachmentIdList      =  finalDOB.getAttachmentDOBList();
		fromWhere			  =  request.getParameter("fromWhere");

		if(fromWhere==null || "null".equals(fromWhere))
			fromWhere		  =	 (String)request.getAttribute("fromWhere");

		quoteDate			  =  headerDOB.getDateOfQuotation();
		effDate				  =  headerDOB.getEffDate();
			update                 =  request.getParameter("update");
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
	     session.setAttribute("finalDOB",finalDOB);
%>
<!--  @@ added by subrahmanyam for the enhancement 180161 -->
<%!
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
		 rateString = dfDecimal.format(sellRate);
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

<HTML>
<HEAD>
<TITLE> Quote </TITLE>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script>
	var operation='<%=operation%>';
	var count=0;
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
//@@Added by kameswari for WPBN issue-61289
function initialize()
{
	importXML('<%=request.getContextPath()%>/xml/QMSQuote.xml');
}
function initializeDynamicContentRows()
{
	var tableObj	= document.getElementById("attachmentId");
	var totalLanes	= attachmentIdArray.length ==0 ? 1 : attachmentIdArray.length;
	//alert("totalLanes"+totalLanes);
		for(var i=0;i<totalLanes;i++)
		{
			//alert("tableObj"+tableObj);
			createDynamicContentRow(tableObj.getAttribute("id"));
			count++;
//alert("totalLanes"+attachmentIdArray.length);
			if(attachmentIdArray.length==0)
			{
				//disable();
				//alert("hi");
				return;
			}

			var attachmentId	=	document.getElementsByName("attachmentId");

			attachmentId[i+1].value	=	attachmentIdArray[i];

		}


}
function popUpWindow(input)
{
	var btnId         = input.id.substring(input.name.length);
	var searchString  = document.getElementById("attachmentId"+btnId).value;
	var Url           ='qms/QuoteAttachmentIdList.jsp?attachmentId='+searchString+'&btnId='+btnId;

	var Bars          = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options       ='scrollbar=yes,width=700,height=300,resizable=no';
	var Features      =Bars+' '+Options;
	var Win           =open(Url,'',Features);

}
function attachmentDetails(input)
{

	var btnId         =input.id.substring(input.name.length);
	var searchString  = document.getElementById("attachmentId"+btnId).value;

	var accessType='<%=loginbean.getAccessType()%>';
	if(accessType=='HO_TERMINAL')
	{
			accessType ='H';
	}
	 else if(accessType=='OPER_TERMINAL')
	{
		   accessType ='O';
	}
	else
	{
	     	accessType ='A';
	}
	var terminalId ='<%=loginbean.getTerminalId()%>';

	var Url           ='QMSAttachmentController?Operation=ViewDetails&subOperation=Next&attachmentId='+searchString+'&accessType='+accessType+'&terminalId='+terminalId;
	var Bars          = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options       ='scrollbar=yes,width=700,height=300,resizable=no';
	var Features      =Bars+' '+Options;
	var Win           =open(Url,'Doc',Features);

}
function toUpper(input)
{
	input.value = input.value.toUpperCase();
}
function mandatory()
{
	var c=0;
	var k=1;
    while(k<=count)
	{
		<%if(attachmentIdLOVList!=null)
			{
		for(int j=0;j<attachmentIdLOVList.size();j++)
		{%>

		if(document.getElementById("attachmentId"+k).value=='<%=attachmentIdLOVList.get(j)%>')
			c++;
		<%}
			}%>
		 if(c==0&&document.getElementById("attachmentId"+k).value!="")
		  {
				alert("Please enter valid attachmentId");
				document.getElementById("attachmentId"+k).focus();
				return false;
		 }
		 else
		{
		     k++;
			  c=0;
		}

	}
	return true;
}
//@@WPBN issue-61289
</script>
</HEAD>
<BODY onload="initialize();">
<form method='post' action='QMSQuoteController' onSubmit='return mandatory()'>
<table width="100%" cellpadding="0" cellspacing="0">
 <tr>
	<td>
		<table width="100%" cellpadding="0" cellspacing="0">
		 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
		  <tr valign="top" class="formlabel">
		    <%if("Modify".equalsIgnoreCase(operation)) {%>
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT QUOTE(<%=quoteName%>,<%=quoteStatus%>,<%=completeFlag%>) - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td></tr>
			 <!-- @@WPBN issue-167677 -->
			 </table></td>
			<%}else { %>
<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td></tr></table></td>
			<%}%>
		  </tr></table>

       <table width="100%" cellpadding="2" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
    <!--@@Added by Subrahmanyam for the WPBN issue-145510-->
			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():""%> TO <%=headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""%></td>
			  <td><pre>    </td> </pre>
            </tr>
			<tr class="formdata">
			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getCustomerName()!=null?headerDOB.getCustomerName():""%></td>
			  <td><pre>    </td> </pre>
            </tr>
             <!--@@WPBN issue-145510-->
			<tr class="formdata">
			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
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
			<td><pre>    </td> </pre>
            </tr>
			<tr class="formdata">
			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
              <td colspan="<%=colspan%>" align=center>DATE OF QUOTATION:<%=quoteDateStr!=null?quoteDateStr:""%> </td>
			  <td><pre>    </td> </pre>
            </tr>
			<tr class="formdata">

			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
              <td colspan="<%=serviceColspan%>" align='right'>PREPARED BY:</td>
			  <td colspan="<%=infoColspan%>" align='left'><%=headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy():""%></td>
			  <td><pre>    </td> </pre>
			</tr>
			</table>
			<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>

			<tr class="formheader" cellpadding="4" >
              <td colspan="<%=colspan%>" align=center>SERVICE INFORMATION</td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Agent: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
            </tr>
			<tr class="formdata">
            <!-- @@ Commented by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
             <!--  <td colspan="<%=serviceColspan%>" >Cargo Acceptance Place: </td> -->
			<!-- @@ Added by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
			 <% if("EXW".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FAS".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FCA".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FOB".equalsIgnoreCase(headerDOB.getIncoTerms()))
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
		if("Y".equalsIgnoreCase(chargesDOB.getFrequencyChecked())||"on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
		{

    %>                <tr class="formdata">
						  <td colspan="<%=serviceColspan%>" >Frequency (<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td><td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getFrequency()!=null)?chargesDOB.getFrequency():""%></td>
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
						<%if("AIR".equalsIgnoreCase(shipmentMode)||"TRUCK".equalsIgnoreCase(shipmentMode)){%>
						  <td colspan="<%=serviceColspan%>">Approximate Transit Time(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
						  <%}else{%>
							<td colspan="<%=serviceColspan%>">Approximate Transit Days(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
							<%}%>
						   <td colspan="<%=infoColspan%>"><%=(chargesDOB.getTransitTime()!=null)?chargesDOB.getTransitTime():""%></td>
						</tr>
	  <%}
			if("Y".equalsIgnoreCase(chargesDOB.getRateValidityChecked())||"on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
		   {%>
					<tr class="formdata">
					  <td colspan="<%=serviceColspan%>" >Freight Rate Validity(<%=legCharges.getOrigin()%>-<%=legCharges.getDestination()%>)</td>
					   <td colspan="<%=infoColspan%>"><%=(str1!=null)?str1[0]:""%></td>
					</tr>
    <%            }
				}
		   else
			{
		   if("Y".equalsIgnoreCase(chargesDOB.getFrequencyChecked())||"on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
		  {
%>
                     <tr class="formdata">
						  <td colspan="<%=serviceColspan%>" >Frequency </td> <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getFrequency()!=null)?chargesDOB.getFrequency():""%></td>
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
						<%if("AIR".equalsIgnoreCase(shipmentMode)||"TRUCK".equalsIgnoreCase(shipmentMode)){%>
						  <td colspan="<%=serviceColspan%>" >Approximate Transit Time:</td>
						  <%}else{%>
								<td colspan="<%=serviceColspan%>" >Approximate Transit Days:</td>
								<%}%>
						   <td colspan="<%=infoColspan%>"><%=(chargesDOB.getTransitTime()!=null)?chargesDOB.getTransitTime():""%></td>
						</tr>
						<%}
				if("Y".equalsIgnoreCase(chargesDOB.getRateValidityChecked())||"on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
				{%>
					<tr class="formdata">
					  <td colspan="<%=serviceColspan%>" >Freight Rate Validity</td>
					   <td colspan="<%=infoColspan%>"><%=(str1!=null)?str1[0]:""%></td>
					</tr>

<%                  }
	}
}
%>

			<!--@@WPBN issue-146448-->
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

<%				for(int j=0;j<originChargesSize;j++)
				{
					chargesDOB				= (QuoteCharges)originCharges.get(originIndices[j]);
					 logger.info("Selected Summary Page Origin Indices: " + originIndices[j]);//newly added
				    logger.info("Selected Origin Charge : " + chargesDOB);//newly added
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td>
					  <input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>
					  </td>
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
                 if(freightChargesSize>0&&freightCharges!=null)
				{

				for(int j=0;j<freightChargesSize;j++)
				{

					chargesDOB				= (QuoteCharges)freightCharges.get(frtIndices[j]);
                    logger.info("Selected Summary Page Freight Indices: " + frtIndices[j]);//newly added
                    logger.info("Selected Freight Charge : " + chargesDOB);//newly added
					if(chargesDOB!=null)
					{
					   freightChargeInfo		= chargesDOB.getChargeInfoList();
					}
					if(freightChargeInfo!=null)
					{
					 freightChargesInfoSize	= freightChargeInfo.size();
					}

					else
					{
						freightChargesInfoSize = 0;
					}

					if(j==0 && freightChargesInfoSize>0)
					{
%>
						<tr class="formdata" cellpadding="4" align=center>
							<td colspan="<%=colspan%>"><B><%=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
						</tr>
<%
					}
                        if(freightChargesInfoSize>0)
					{
							 int tempAbs = 0;// added by subrahmanyam for 181430 on 10-sep09
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
%>
					<tr class="formdata" cellpadding="4" align=center>
					  <!--<td><%=k==0?chargesDOB.getChargeDescriptionId():""%></td>--><!--@@Modified by Kameswari for Surcharge Enhancements-->
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
		<td><%System.out.println(chargesDOB.getSellBuyFlag());
						if(!"TRUCK".equalsIgnoreCase(shipmentMode) && !"SBR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
	<%=toTitleCase(chargeInfo.getRateDescription().substring(0,chargeInfo.getRateDescription().length()-3))%>
	<%}else{%>
	<%=chargeInfo.getRateDescription()%>
	<%}%>
		</td>

					 <%}%>
					 <!-- modified by subrahmanyam for 181430 on 10/09/09 -->
					<%System.out.println(chargeInfo.getBreakPoint());
						if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                                  ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                               {if(tempAbs==1 && tempDesc.equalsIgnoreCase(chargeInfo.getRateDescription()) ){%>
				    <!--  <td>OR</td> --><td><%=chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")?"Percent":"Flat"%></td>
					  <%tempAbs=0;}else{ if(chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")){%>
						  <td>Percent</td>
						  <%}else{%>
					   <!-- <td>ABSOLUTE</td> --><td>Flat</td>
					 <%}}%>
					 <%}
					  else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                               ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN"))
                               {tempAbs=1;%>
					<td>MIN</td>
                     <%tempDesc=chargeInfo.getRateDescription();}//ended for 181430
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
					  else if(chargeInfo.getBreakPoint().equalsIgnoreCase("PERCENT"))
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
					  <td><%if("List".equalsIgnoreCase(masterDOB.getWeightBreak())){%>
					  <%=chargeInfo.getBreakPoint().length()>5?chargeInfo.getBreakPoint().substring(0,4):chargeInfo.getBreakPoint()%></font></td>
					 <%}else{%>
					  <%=chargeInfo.getBreakPoint().length()>5?chargeInfo.getBreakPoint().substring(5):chargeInfo.getBreakPoint()%></font></td>
					<%}}%>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td><%=df.format(new Double(chargeInfo.getSellRate()))%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					</tr>
<%

					}
					}
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

				for(int j=0;j<destChargesSize;j++)
				{
					chargesDOB				= (QuoteCharges)destCharges.get(destIndices[j]);
				  logger.info("Selected Summary Page Dest Indices: " + destIndices[j]);//newly added
			      logger.info("Selected Dest Charge : " + chargesDOB);//newly added
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
					if(destChargesInfoSize>0)
					{
					for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					   <!-- //@@Modified by kiran.v on 03/11/2011 for Wpbn Issue -->
					  <td><%=round(chargeInfo.getSellRate())%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					</tr>
<%                   }
					}
				  }
				}

%>
	</table>
	<!--@@Added by Kameswari for the WPBN issue-61289-->
	<table width="100%" cellpadding="4" cellspacing="2" nowrap id="attachmentId" idcounter="1"   defaultElement="attachmentId"  xmlTagName="AttachmentId"  bgcolor='#FFFFFF'>
	<tr class='formheader'>
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

				if("I".equalsIgnoreCase(finalDOB.getFlagsDOB().getActiveFlag()))
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
					<!-- Modified by Mohan for Issue No.219976 on 30-10-2010 -->
					<textarea name='notes' class="text" cols="120" rows='4' onblur='chr(this)'><%=notes[i].trim()%></textarea>
			  </td>
			</tr>
<%
				}
			}
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
		</table>

		<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class='text'>
				  <td width="18%" align="right" colspan="<%=colspan%>">
				  <input class="input" name="submit" type="submit" value="<<Back">
				  <input class="input" name="submit" type="submit" value="Next&gt;&gt;">
				  <input type="hidden" name="Operation" value="<%=operation%>">
				  <input type="hidden" name="subOperation" value="SUMMARY">
				   <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
				  <input type="hidden" name="quoteName" value="<%= quoteName!=null? quoteName:""%>">
				   <input type="hidden" name="completeFlag" value="<%= completeFlag!=null? completeFlag:""%>">
				   <!-- @@WPBN issue-167677 -->
           <input type="hidden" name="update" value="update">
				  <input type="hidden" name="fromWhere" value="<%=fromWhere%>">
				   <input type="hidden" name="quoteStatus" value="<%=quoteStatus%>">
				  <input type="hidden" name="flag" value="<%=request.getParameter("flag")!=null?request.getParameter("flag"):""%>">
				  <input type="hidden" name="fromWhat" value="<%=request.getParameter("fromWhat")!=null?request.getParameter("fromWhat"):""%>">
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
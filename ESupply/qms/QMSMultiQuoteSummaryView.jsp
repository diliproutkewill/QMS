<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSMultiQuoteSummaryView.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		:
Date modified		:
Author    			: 
Related Document	: CR_DHLQMS_217789

--%>

<%@page import = "java.util.ArrayList,
				  java.sql.Timestamp,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.multiquote.dob.MultiQuoteMasterDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteFinalDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteAttachmentDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteHeader,
				  com.qms.operations.multiquote.dob.MultiQuoteCharges,
				  com.qms.operations.multiquote.dob.MultiQuoteChargeInfo,
				  com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates,
				  com.qms.operations.multiquote.dob.MultiQuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSMultiQuoteSummaryView.jsp"; %>
<%
	logger  = Logger.getLogger(FILE_NAME);
	ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
	String		  operation				  =	request.getParameter("Operation");
//	System.out.println("quoteOption="+request.getParameter("quoteOption"));
	//Added for the Issue 234719
	String        quoteOption             = request.getParameter("quoteOption");
	String        dateFormat			  =	loginbean.getUserPreferences().getDateFormat();
	MultiQuoteFinalDOB finalDOB				  =	null;
	MultiQuoteHeader	  headerDOB				  =	null;
	MultiQuoteMasterDOB masterDOB			  =	null;
	MultiQuoteAttachmentDOB  attachmentDOB     = null;

	ArrayList     charges                 = null;//@@Info of all the Legs
	ArrayList     originCharges           = null;
	ArrayList     destCharges             = null;
	ArrayList     freightCharges          = null;
	ArrayList     attachmentIdLOVList     = null;
	ArrayList     attachmentIdList        = null;
	ArrayList		chargeInfoList		  = new ArrayList();

	ArrayList     originChargeInfo        = null;
	ArrayList     destChargeInfo          = null;
	ArrayList     freightChargeInfo       = null;

	MultiQuoteFreightLegSellRates  legCharges  = null;
	MultiQuoteFreightLegSellRates   legRateDetails = null;
	MultiQuoteCharges  chargesDOB			  = null;
	MultiQuoteChargeInfo chargeInfo			  = null;

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
 String selectedBreaks				=	null;
 String[]  selBreaks                = null;
 String[]  basis                    = null;
	 int n =0;
	String breakpoint = null;
	java.text.DecimalFormat df			  =	new java.text.DecimalFormat("##,###,##0.00");


	try
	{
		eSupplyDateUtility.setPatternWithTime("DD-MON-YYYY");//in order to get the full month name
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
		finalDOB		      =	(MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");
		attachmentIdLOVList   = (ArrayList)request.getAttribute("attachmentIdLOVList");
		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
		attachmentIdList      =  finalDOB.getAttachmentDOBList();
		fromWhere			  =  request.getParameter("fromWhere");
		//Added by Anil.k
		if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
		{
        selectedBreaks  = finalDOB.getMultiQuoteSelectedBreaks();
		if(selectedBreaks!=null)
		 selBreaks = selectedBreaks.split(",");//Added on 04Feb2011
		}


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
		//rateString = dfDecimal.format(sellRate);//modified by silpa.p for decimal caluculation on 24-05-11
		 rateString = dfDecimal2.format(sellRate);//modified by silpa.p for decimal caluculation on 24-05-11
		

		 return rateString;
		}
		else if(m==1 || l==-1)
		{

			rateString = dfDecimal2.format(sellRate);

			//System.out.println("rateString--------"+rateString);
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
	attachmentDOB = (MultiQuoteAttachmentDOB)attachmentIdList.get(i);
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
	//alert("in disableButtons");
	disableButtons();

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
function disableButtons()
{		
	for(var i=0;i<document.forms[0].elements.length;i++)
	{	 
		if(document.forms[0].elements[i].type=='button'){
			
			document.forms[0].elements[i].disabled=true;
		}
		else if(document.forms[0].elements[i].type=='text' )
			document.forms[0].elements[i].readOnly=true;
	}

	
}
function popUpWindow(input)
{
	var btnId         = input.id.substring(input.name.length);
	var searchString  = document.getElementById("attachmentId"+btnId).value;
	var Url           ='qms/MultiQuoteAttachmentIdList.jsp?attachmentId='+searchString+'&btnId='+btnId;

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
<form method='post' action='QMSMultiQuoteController' onSubmit='return mandatory()'>
<% if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))//Added by Anil.k
{ %>
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
<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td>
<td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td></tr></table></td>
			<%}%>
		  </tr></table>

       <table width="100%" cellpadding="2" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
    <!--@@Added by Subrahmanyam for the WPBN issue-145510-->
			<!--<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
			<td align = 'center' nowrap>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<%=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService():""%></td>--><!--modified by silpa.p on 25-05-11-->
            <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
			</pre>
            </tr>
			<tr class="formdata">
			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getCustomerName()!=null?headerDOB.getCustomerName():""%></td>
			  <td><pre>    </td> </pre>
			     <td ></td>
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
			   <td ></td>
            </tr>
			<tr class="formdata">
			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
              <td colspan="<%=colspan%>" align=center>DATE OF QUOTATION:<%=quoteDateStr!=null?quoteDateStr:""%> </td>
			  <td><pre>    </td> </pre>
			     <td ></td>
            </tr>
			<tr class="formdata">

			<td> </td>	<td> </td>	<!--modified by silpa.p on 25-05-11-->
              <td colspan="<%=serviceColspan%>" align='center'>PREPARED BY:<%=headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy():""%></td>
			  <td colspan="<%=infoColspan%>" align='left'></td>
			  <td><pre>    </td> </pre>
			</tr>
			</table>
			<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>

			<tr class="formheader" cellpadding="4" >
              <td colspan="<%=colspan%>" align=center>SERVICE INFORMATION</td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Agent: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>
			<!-- <tr class="formdata">
            </tr> -->
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Commodity or Product: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCommodity()!=null?headerDOB.getCommodity():""%></td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>
			<!--modified by silpa.p on 1-05-11-->
			<!--<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Type of service quoted: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService():""%> </td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>Ended-->
			<!--@@Added by Kameswari for the WPBN issue-146448-->
	<%		for(int i=0;i<chargesSize;i++)
	{
		legCharges	   = (MultiQuoteFreightLegSellRates)charges.get(i);


		freightCharges = legCharges.getFreightChargesList();
	//	chargesDOB			= (MultiQuoteCharges)freightCharges.get(0);
		
		
}
%>

			<!--@@WPBN issue-146448-->
			
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Notes: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getNotes()!=null?headerDOB.getNotes():""%></td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Date Effective: </td>
			  <td colspan="<%=infoColspan%>" ><%=effDateStr!=null?effDateStr:""%></td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Validity of Quote: </td>
			  <td colspan="<%=infoColspan%>" ><%=validUptoStr!=null?validUptoStr:"VALID TILL FURTHER NOTICE"%></td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>

<%			if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
			{
%>				<tr class="formdata">
				  <td colspan="<%=serviceColspan%>" >Payment Terms: </td>
				  <td colspan="<%=infoColspan%>" ><%=headerDOB.getPaymentTerms()%></td>
				  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
				</tr>
<%
			}
%>			 </table>
			 <table width="100%" cellpadding="2" cellspacing="1" bgcolor='#FFFFFF'>

<%              int frtsize = finalDOB.getLegDetails().size();
			//	System.out.println("frtsize---------"+frtsize);
				for(int fr=0;fr<frtsize;fr++){
				legRateDetails				=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
		//		System.out.println("legRateDetails---------"+legRateDetails);
				originCharges				= legRateDetails.getOriginChargesList();
			//	System.out.println("originCharges---------"+originCharges);

				originIndices				= (legRateDetails.getSelectedOriginChargesListIndices()!= null?legRateDetails.getSelectedOriginChargesListIndices():null);
        //   System.out.println("originIndices---------"+originIndices);
	//	  System.out.println("originChargesSize"+originChargesSize);
				if(originIndices!=null)
					originChargesSize		= originIndices.length;
				else if("View/Print".equalsIgnoreCase(quoteOption) && originCharges!=null)//Added for the issue 234719
					{
					originChargesSize		= originCharges.size();
					originIndices = new int[originChargesSize];
					}//Ended for the Issue 234719
				else
					originChargesSize		= 0;
//System.out.println("originChargesSize"+originChargesSize);
				if(originChargesSize>0 && fr == 0)
				{
%>
				<tr class="formheader" cellpadding="4" align='center'>
			  <td>Charge Name</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
			  <td>Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+5:5%>'></td>
            </tr>
<%
				}
				//Added for the Issue 234719
				else if("View/Print".equalsIgnoreCase(quoteOption) && originCharges!=null){%>
				<tr class="formheader" cellpadding="4" align='center'>
			  <td>Charge Name</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
			  <td>Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>
			  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
            </tr>
			
<%
				}//Ended for the issue 234719

				for(int j=0;j<originChargesSize;j++)
				{
	             if(j==0 && originChargesSize>0){%>
				 <tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center><%=legRateDetails.getOrgFullName()%>-ORIGIN CHARGES</td>
					  <td colspan ='<%=selBreaks!=null?selBreaks.length+5:5%>'></td>
				</tr>   
		         
		<%}System.out.println("originChargesSize="+originChargesSize);
		//Added for the Issue 234719
	   if("View/Print".equalsIgnoreCase(quoteOption) && originCharges!=null)
		   originIndices[j] = j;
	   //Ended for the issue 234719
				//	if(legRateDetails.getOriginChargesSelectedFlag() != null && "Y".equalsIgnoreCase(legRateDetails.getOriginChargesSelectedFlag()[j])){%>

				<%	chargesDOB				= (MultiQuoteCharges)originCharges.get(originIndices[j]);
					logger.info("Selected Summary Page Origin Indices: " + originIndices[j]);//newly added
				    logger.info("Selected Origin Charge : " + chargesDOB);//newly added
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td>
					  <input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>
					  </td>
					  <td colspan ='<%=selBreaks!=null?selBreaks.length+5:5%>'></td>
					</tr>
<%
					}
				//	}
					//Added for the Issue 234719
					 if("View/Print".equalsIgnoreCase(quoteOption) && originCharges!=null){%>
					<%	chargesDOB				= (MultiQuoteCharges)originCharges.get(originIndices[j]);
					logger.info("Selected Summary Page Origin Indices: " + originIndices[j]);//newly added
				    logger.info("Selected Origin Charge : " + chargesDOB);//newly added
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td>
					  <input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>
					  </td>
					  <td colspan ='<%=selBreaks!=null?selBreaks.length+4:4%>'></td>
					</tr>
<%
					}
					}//Ended for the issue 234719

				}
	}//@@ origin charges end%>


<tr class="formheader" cellpadding="4" align='center'>
			  <td>Origin Port</td>
			  <td>DestPort</td>
			  <td>Incoterms</td>
			  <td>Carrier</td>
			  <td>Service Level</td>
			  <td>Frequency</td>
			  <td>Approx Transit Time</td>
			  <td>Freight Validity</td>
			  <td>Currency</td>
			  <td>BR/RSR</td>

<%  //@@ freight rates end
	chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
                selectedBreaks  = finalDOB.getMultiQuoteSelectedBreaks();
				if(selectedBreaks!=null){
				   selBreaks = selectedBreaks.split(",");
				 
				}
					int infoSize	=	chargeInfoList.size();
						int brkCount	=	0;
					//	System.out.println("chargeInfoList="+chargeInfoList);
							chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(0);
							  basis          = chargeInfo.getBasis().split(",");
							for(int br=0;br<(selBreaks!=null?selBreaks.length:0);br++){
								breakpoint="";
								breakpoint = chargeInfo.getMultiBreakPoints()[Integer.parseInt(selBreaks[br])];	%>
							      					
                        
						  <td nowrap>
							<%if(!"List".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())){%>
							<%=	(!"A FREIGHT RATE".equalsIgnoreCase(chargeInfo.getMultiRateDescriptions()[Integer.parseInt(selBreaks[br])]) && breakpoint.length()>5)?breakpoint.substring(0,3)+breakpoint.substring(5):breakpoint%>
							<%}else{%>
					        <%=	(!"A FREIGHT RATE".equalsIgnoreCase(chargeInfo.getMultiRateDescriptions()[Integer.parseInt(selBreaks[br])])) ?breakpoint.substring(0,breakpoint.length()-2):breakpoint%>
							<%}%><br>
						               <%=basis[Integer.parseInt(selBreaks[br])]%></td>
			 <%brkCount++;}%><td>Density Ratio </td>
           </tr>
              <%	chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
					 infoSize	=	chargeInfoList.size();
					for(int info=0;info<infoSize;info++)
					{
							chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(info);
							System.out.println("sellbuyflag.."+chargeInfo.getSellBuyFlag());
				
%>						   <input type = 'hidden'   name='multiSelectedFlag<%=info%>' id ='multiSelectedFlag<%=info%>'
							value = '<%=chargeInfo.getSellBuyFlag()%>' />
							<!--@@ Added by kiran.v on 08/08/2011 for Wpbn Issue 258778 -->
							<%if("on".equalsIgnoreCase(chargeInfo.getCarrierChecked())){%>
							  <input type="hidden" name="selectCarrier"	   id="selectCarrier"  value="checked">
							  <%}%>
							  <%if("on".equalsIgnoreCase(chargeInfo.getFrequencyChecked())){%>
							  <input type="hidden" name="selectFrequecy"	   id="selectFrequecy"  value="checked">
							  <%}%>
							    <%if("on".equalsIgnoreCase(chargeInfo.getServiceChecked())){%>
							  <input type="hidden" name="selectService"	   id="selectService"  value="checked">
							  <%}%> 
							    <%if("on".equalsIgnoreCase(chargeInfo.getTransitTimeChecked())){%>
							  <input type="hidden" name="selectTransitTime"	   id="selectTransitTime"  value="checked">
							  <%}%>
							    <%if("on".equalsIgnoreCase(chargeInfo.getRateValidityChecked())){%>
							  <input type="hidden" name="selectFrieghtValidity"	   id="selectFrieghtValidity"  value="checked">
							  <%}%>
						   <tr  class="formdata" cellpadding="4" valign='center' rowspan=1>
                           <td rowspan="1" align='center'><%=chargeInfo.getOriginPort()%></td>
                           <td rowspan="1"><%=chargeInfo.getDestPort()%></td>
						   <td rowspan="1"><%=chargeInfo.getIncoTerms()%></td>
						   <td rowspan="1"><%=chargeInfo.getCarrier()%></td>
						   <td rowspan="1"><%=chargeInfo.getServiceLevel()%></td>
						   <td rowspan="1"><%=chargeInfo.getFrequency()%></td>
						   <td rowspan="1"><%=chargeInfo.getTransitTime()%></td>
						   <td nowrap rowspan="1"><%=chargeInfo.getValidUpto()!=null?eSupplyDateUtility.getDisplayStringArray(chargeInfo.getValidUpto())[0]:""%></td>
						   <td rowspan="1"><%=chargeInfo.getCurrency()%></td> 
						   <td><%=chargeInfo.getSellBuyFlag()%></td>
				<%    if(selBreaks!=null)
							   for(int br=0;br<selBreaks.length;br++){
                          //    System.out.println("null".equals(chargeInfo.getMultiCalSellRates()[Integer.parseInt(selBreaks[br])])+"*****"+chargeInfo.getMultiCalSellRates()[Integer.parseInt(selBreaks[br])]!= null);%>
								<td><%=!"null".equals(chargeInfo.getMultiCalSellRates()[Integer.parseInt(selBreaks[br])]) && chargeInfo.getMultiCalSellRates()[Integer.parseInt(selBreaks[br])] != null ?
							  !"-".equals(chargeInfo.getMultiCalSellRates()[Integer.parseInt(selBreaks[br])])?
							 round(Double.parseDouble(chargeInfo.getMultiCalSellRates()[Integer.parseInt(selBreaks[br])])):"-":"0.00"%></font></td>
						<%}%><td rowspan="1"><%=chargeInfo.getRatio()%></td>
						<%}%></tr>
								
							

			<%	//@@ destination cherges begin
				for (int fr=0;fr<frtsize;fr++){
                legRateDetails				=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
				destCharges		= legRateDetails.getDestChargesList();
				destIndices		= (legRateDetails.getSelctedDestChargesListIndices()!= null?legRateDetails.getSelctedDestChargesListIndices():null );
         System.out.println("destIndices"+destIndices);
				if(destIndices!=null)
					destChargesSize	= destIndices.length;
				else if("View/Print".equalsIgnoreCase(quoteOption) && destCharges!=null)//Added for the Issue 234719
					{
					destChargesSize = destCharges.size();
					destIndices = new int[destChargesSize];
					}//Ended for the Issue 234719
				else

					destChargesSize	= 0;
					System.out.println("destChargesSize"+destChargesSize);
%>
				
<%				for(int j=0;j<destChargesSize;j++)
				{
			//Added for the Issue 234719
			if("View/Print".equalsIgnoreCase(quoteOption) && originCharges!=null)
			destIndices[j] = j;
			//Ended for the Issue 234719
			%>
				 
				  
<%				System.out.println("selected flag--"+legRateDetails.getDestChargesSelectedFlag()) ;
	          /* if(legRateDetails.getDestChargesSelectedFlag() != null && "Y".equalsIgnoreCase(legRateDetails.getDestChargesSelectedFlag()[j])){
					  System.out.println(legRateDetails.getDestChargesSelectedFlag()[j]);*/%>
				
				  
				  <%chargesDOB				= (MultiQuoteCharges)destCharges.get(destIndices[j]);
				  logger.info("Selected Summary Page Dest Indices: " + destIndices[j]);//newly added
			      logger.info("Selected Dest Charge : " + chargesDOB);//newly added
					destChargeInfo			= chargesDOB.getChargeInfoList();
					destChargesInfoSize		= destChargeInfo.size();
					if(j==0 && destChargesInfoSize>0)
					{
%>
						 <tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center><%=legRateDetails.getDestFullName()%>-DESTINATION CHARGES</td>
					  <td colspan ='<%=selBreaks!=null?selBreaks.length+5:5%>'></td>
						</tr>
<%
					}
					if(destChargesInfoSize>0)
					{
					for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td nowrap><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td nowrap><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td nowrap><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					   <td colspan ='<%=selBreaks!=null?selBreaks.length+5:5%>'></td>
					</tr>
<%                   }
					}
				//}//if end
				//Added for the Issue 234719
				else if("View/Print".equalsIgnoreCase(quoteOption) && originCharges!=null){%>
				<%chargesDOB				= (MultiQuoteCharges)destCharges.get(destIndices[j]);
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
						chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					</tr>
<%                   }
					}
				}//Ended for the Issue 234719
				  }//@@ destination charges end
	}%>
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
					<textarea name='notes' class="text" cols="50" rows='4' onblur='chr(this)' readonly><%=notes[i].trim()%></textarea><!--modified by silpa.p on 25-05-11-->
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
					<INPUT TYPE="checkbox" NAME="email" <%="Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEmailFlag())?"checked":""%> >&nbsp;Email
					<INPUT TYPE="checkbox" NAME="fax" <%="Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getFaxFlag())?"checked":""%> >&nbsp;Fax
					<INPUT TYPE="checkbox" NAME="print" <%="Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getPrintFlag())?"checked":""%> >&nbsp;Print
				</td>
			</tr>
		</table>

		<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class='text'>
				  <td width="18%" align="right" colspan="<%=colspan%>">
				  <% if("View".equalsIgnoreCase(operation)){ %>
				  <input class="input" name="submit" type="submit" value="<<Back">
				  <input class="input" name="submit" type="submit" value="Next>>">
				  <%}else{%>
				  
				  <input class="input" name="submit" type="submit" value="Next&gt;&gt;">
				  <%}%>
				  <input type="hidden" name="Operation" value="<%=operation%>">
				  <input type="hidden" name="subOperation" value="SUMMARY">
				   <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
				  <input type="hidden" name="quoteName" value="<%= quoteName!=null? quoteName:""%>">
				   <input type="hidden" name="completeFlag" value="<%= completeFlag!=null? completeFlag:""%>">
				   <!-- @@WPBN issue-167677 -->
           <input type="hidden" name="update" value="">
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
<%} else{ //Added by Anil.k%>
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
<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> MULTI FREIGHT QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td>
<td colspan ='<%//=selBreaks.length+4%>'></td></tr></table></td>
			<%}%>
		  </tr></table>

       <table width="100%" cellpadding="2" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
    <!--@@Added by Subrahmanyam for the WPBN issue-145510-->
			<td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp </td>
			<td align = 'center' nowrap>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;Quote With Charges</td>
            <td colspan ="<%=colspan%>"></td>
			
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

			<td> </td>
              <td colspan="<%=serviceColspan%>" align='center'>PREPARED BY:<%=headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy():""%></td>
			  <td colspan="<%=infoColspan%>" align='left'></td>
			  <td><pre>    </td> </pre>
			</tr>
			</table>
			<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>

			<tr class="formheader" cellpadding="4" >
              <td colspan="<%=colspan%>" align=center>SERVICE INFORMATION</td>
			  <!-- <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Agent: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
			  <!-- <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>
			<tr class="formdata">
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Commodity or Product: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCommodity()!=null?headerDOB.getCommodity():""%></td>
			  <!-- <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Origin - Destination </td>
			  <td colspan="<%=infoColspan%>" ><%=masterDOB.getOriginLocation()[0] +"-"+masterDOB.getDestLocation()[0]%> </td>
			 <!--  <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>
			<!--@@Added by Kameswari for the WPBN issue-146448-->
	<%		for(int i=0;i<chargesSize;i++)
	{
		legCharges	   = (MultiQuoteFreightLegSellRates)charges.get(i);


		freightCharges = legCharges.getFreightChargesList();
	//	chargesDOB			= (MultiQuoteCharges)freightCharges.get(0);
		
		
}
%>

			<!--@@WPBN issue-146448-->
			
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Notes: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getNotes()!=null?headerDOB.getNotes():""%></td>
			  <!-- <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Date Effective: </td>
			  <td colspan="<%=infoColspan%>" ><%=effDateStr!=null?effDateStr:""%></td>
			  <!-- <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Validity of Quote: </td>
			  <td colspan="<%=infoColspan%>" ><%=validUptoStr!=null?validUptoStr:"VALID TILL FURTHER NOTICE"%></td>
			  <!-- <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>

<%			if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
			{
%>				<tr class="formdata">
				  <td colspan="<%=serviceColspan%>" >Payment Terms: </td>
				  <td colspan="<%=infoColspan%>" ><%=headerDOB.getPaymentTerms()%></td>
				  <!-- <td colspan ='<%//=selBreaks.length+4%>'></td> -->
				</tr>
<%
			}
%>
			

<%              int frtsize = finalDOB.getLegDetails().size();
			//	System.out.println("frtsize---------"+frtsize);
				for(int fr=0;fr<frtsize;fr++){
				legRateDetails				=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
			//	System.out.println("legRateDetails---------"+legRateDetails);
				originCharges				= legRateDetails.getOriginChargesList();

				originIndices				= (legRateDetails.getSelectedOriginChargesListIndices()!= null?legRateDetails.getSelectedOriginChargesListIndices():null);
           //System.out.println(originIndices);
	//	  System.out.println("originChargesSize"+originChargesSize);
				if(originIndices!=null)
					originChargesSize		= originIndices.length;
				else
					originChargesSize		= 0;
//System.out.println("originChargesSize"+originChargesSize);
				if(originChargesSize>0)
				{
%>
				<tr class="formheader" cellpadding="4" align='center'>
			  <td>Charge Name</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
			  <td>Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>
			 <!--  <td colspan ='<%//=selBreaks.length+4%>'></td> -->
            </tr>
			
<%
				}
%>
	        	

<%				for(int j=0;j<originChargesSize;j++)
				{
				if(j==0 && originChargesSize >0){%>
                   <tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center><%=legRateDetails.getOrgFullName()%>-ORIGIN CHARGES</td>
				</tr>

				<%}	//if(legRateDetails.getOriginChargesSelectedFlag() != null && "Y".equalsIgnoreCase(legRateDetails.getOriginChargesSelectedFlag()[j])){%>

				<%	chargesDOB				= (MultiQuoteCharges)originCharges.get(originIndices[j]);
					logger.info("Selected Summary Page Origin Indices: " + originIndices[j]);//newly added
				    logger.info("Selected Origin Charge : " + chargesDOB);//newly added
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					for(int k=0;k<originChargesInfoSize;k++)
				{
						chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td>
					  <input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>
					  </td>
					 <!--  <td colspan ='<%//=selBreaks.length+4%>'></td> -->
					</tr>
<%
					}
					//}
				}
	}//@@ origin charges end%>



              </tr>
								
							

			<%	//@@ destination cherges begin
				for (int fr=0;fr<frtsize;fr++){
                legRateDetails				=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
				destCharges		= legRateDetails.getDestChargesList();
				destIndices		= (legRateDetails.getSelctedDestChargesListIndices()!= null?legRateDetails.getSelctedDestChargesListIndices():null );
         System.out.println("destIndices"+destIndices);
				if(destIndices!=null)
					destChargesSize	= destIndices.length;
				else

					destChargesSize	= 0;
					System.out.println("destChargesSize"+destChargesSize);
%>
			<% if(destChargesSize>0){%>
                	<tr class="formheader" cellpadding="4" align='center'>
			  <td>Charge Name</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
			  <td>Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>
            </tr>
				  <tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center><%=legRateDetails.getDestFullName()%>-DESTINATION CHARGES</td>
					</tr>
					<%}%>
<%				for(int j=0;j<destChargesSize;j++)
				{%>
				  
				  
<%			 /* if(legRateDetails.getDestChargesSelectedFlag() != null && "Y".equalsIgnoreCase(legRateDetails.getDestChargesSelectedFlag()[j])){
					  System.out.println(legRateDetails.getDestChargesSelectedFlag()[j]);*/%>
				
				  
				  <%chargesDOB				= (MultiQuoteCharges)destCharges.get(destIndices[j]);
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
						chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);

%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getExternalName():chargesDOB.getChargeDescriptionId()):""%></td>
					  <td><%=chargeInfo.getBreakPoint()%></td>
					  <td><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></td>
					  <td><%=df.format(new Double(chargeInfo.getSellRate()))%><%=chargeInfo.isPercentValue()?" %":""%></td>
					  <td><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					</tr>
<%                   }
					}
			//	}//if end
				  }//@@ destination charges end
	}%>
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

				if(("Y".equalsIgnoreCase(finalDOB.getFlagsDOB().getEscalationFlag()) ||
				   "REJ".equalsIgnoreCase(finalDOB.getFlagsDOB().getQuoteStatusFlag()) ||
				   "I".equalsIgnoreCase(finalDOB.getFlagsDOB().getActiveFlag()))&&!("Update".equalsIgnoreCase(update)))
				{
					disabled		=	"disabled";
			}
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
					<textarea name='notes' class="text" cols="120" rows='4' onblur='chr(this)' readonly><%=notes[i].trim()%></textarea>
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
           <input type="hidden" name="update" value="">
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
<% } //End%>
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
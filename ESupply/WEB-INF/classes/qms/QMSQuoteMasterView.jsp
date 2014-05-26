<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteMasterView.jsp
Product Name		: QMS
Module Name			: Quote
Task				: View
Date started		: 
Date modified		:  
Author    			: Yuvraj
Related Document	: CR_DHLQMS_1007

--%>

<%@page import = "java.util.ArrayList,
				  java.util.Hashtable,
				  java.util.HashMap,
				  java.util.Iterator,
				  javax.ejb.ObjectNotFoundException,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.quote.dob.QuoteMasterDOB,
				  com.qms.operations.quote.dob.QuoteFreightLegSellRates,
				  com.qms.operations.quote.dob.QuoteFinalDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteMaster.jsp"; %>

<%
  logger  = Logger.getLogger(FILE_NAME);	
QuoteMasterDOB			masterDOB			=	null;
QuoteFinalDOB			finalDOB			=	null;
QuoteFreightLegSellRates legDOB				=	null; 
ESupplyDateUtility		eSupplyDateUtility	=	new ESupplyDateUtility();
String					operation			=	request.getParameter("Operation");
String					dateFormat			=	loginbean.getUserPreferences().getDateFormat();
String					currentDate			=	eSupplyDateUtility.getCurrentDateString(dateFormat);
String					effDate				=	null;
String					validTo				=	null;
String					modifiedDate				=	null;
String					createdDate			=	null;
String[]				chargeGroups		=	null;
String[]				headerFooter		=	null;
String[]				content				=	null;
String[]				level				=	null;
String[]				align				=	null;
String[]				minRate				=	null;
String[]				flatRate			=	null;

Hashtable				spotRateDetails		=	null;
ArrayList				weightBreaks		=	null;
Iterator				weightBreaksItr		=	null;
int						weightBreaksSize	=	0;
int						noOfLegs			=	0;
ArrayList				legDetails			=	null;
Hashtable				accessList			=	null;
String					disabled			=	"";

boolean					isSpotRatesFlag		=	false;
String					spotRateType		=	"";
String					legShipmentMode		=	"";
String					legServiceLevel		=	null;
String					legUOM				=	null;
String					legDensityRatio		=	null;
String                  legCurrency         =   null;
String					leg					=	"";
String					readOnly			=	"";
String					addressId			=	"";
String					createdBy			=	"";
String					shipMode			=	"";
String					impFlag				=	"";
String					impFlagValue		=	"";

double[]				spotRate			=	null;
ArrayList               containerBreak		= new ArrayList();//@@Added by subrahmanyam for the wpbn id:196050 on 29/Jan/10

String					custContactIds		=	"";
String                  quoteStatus         = null;	 // @@Added by VLAKSHMI for the WPBN issue-167677 
String					cargoAccPlace		="";	//@@Added by subrahmanyam for 187569 on 28/oct/09
String					custDate			=	null;	//Added by Rakesh K on 23-02-2011 for Issue:236359
String					custTime			=	null;	//Added by Rakesh K on 23-02-2011 for Issue:
try
{
	
	eSupplyDateUtility.setPattern(dateFormat);
	quoteStatus = request.getParameter("quoteStatus");	 // @@Added by VLAKSHMI for the WPBN issue-167677 
	if(session.getAttribute("viewFinalDOB")!=null)
		finalDOB	=	(QuoteFinalDOB)session.getAttribute("viewFinalDOB");

	if(finalDOB!=null)
	{
		masterDOB	=	finalDOB.getMasterDOB();
		addressId	=	""+masterDOB.getCustomerAddressId();
		legDetails  =	finalDOB.getLegDetails();
	}
	else
		masterDOB	=	new QuoteMasterDOB();

	if(legDetails!=null)
		noOfLegs    =  legDetails.size();
	else
		noOfLegs    =  1;

	if(masterDOB.getEffDate()!=null)
		effDate				=	eSupplyDateUtility.getDisplayString(masterDOB.getEffDate());

	if(masterDOB.getValidTo()!=null)
		validTo				=	eSupplyDateUtility.getDisplayString(masterDOB.getValidTo());
	
	if(masterDOB.getCreatedDate()!=null)
		createdDate			=	eSupplyDateUtility.getDisplayString(masterDOB.getCreatedDate());
	if(masterDOB.getModifiedDate()!=null)
		modifiedDate		=	eSupplyDateUtility.getDisplayString(masterDOB.getModifiedDate());
	//Added by Rakesh K on 23-02-2011 for Issue:236359	
	if(masterDOB.getCustDate()!=null)
		custDate			=	eSupplyDateUtility.getDisplayString(masterDOB.getCustDate());

	if(masterDOB.getCustTime()!=null)
		custTime			=	masterDOB.getCustTime();
	// Ended by Rakesh K on 23-02-2011 for Issue:236359
	accessList  =  (Hashtable)session.getAttribute("accessList");

	if(accessList.get("10605")==null)
		disabled="disabled";
	if("Modify".equalsIgnoreCase(operation))
		readOnly = "readOnly";
	logger.info("masterDOB.getQuoteStatus()"+masterDOB.getQuoteStatus());
	if(masterDOB.getQuoteStatus()!=null)
	{
	if("PEN".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "PENDING".equalsIgnoreCase(quoteStatus))
        quoteStatus="PENDING";
   else if("ACC".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "POSITIVE".equalsIgnoreCase(quoteStatus) )
       quoteStatus="POSITIVE";
else if("REJ".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "REJECTED".equalsIgnoreCase(quoteStatus))
       quoteStatus="REJECTED";
else if("GEN".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "GENERATED".equalsIgnoreCase(quoteStatus) )
      quoteStatus="GENERATED";
else if("APP".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "APPROVED".equalsIgnoreCase(quoteStatus) )
       quoteStatus="APPROVED";
else if("QUE".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "ESCELATED".equalsIgnoreCase(quoteStatus))
       quoteStatus="ESCELATED";
else if("NAC".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "NEGATIVE".equalsIgnoreCase(quoteStatus))
     quoteStatus="NEGATIVE";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Quote  - <%=operation%></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script language="javascript">

var chargeGroupsArray	=	new Array();
var headerFooterArray	=	new Array();
var contentArray		=	new Array();
var levelArray			=	new Array();
var alignArray			=	new Array();
var spotRateFlags		=	new Array();

<%
	if(masterDOB.getChargeGroupIds()!=null)
	{
		chargeGroups	=	masterDOB.getChargeGroupIds();
		for(int i=0;i<chargeGroups.length;i++)
		{
%>
			chargeGroupsArray[chargeGroupsArray.length]	=	'<%=chargeGroups[i]!=null?chargeGroups[i]:""%>';
<%	
		}
	}

	if(masterDOB.getHeaderFooter()!=null)
	{
		headerFooter	=	masterDOB.getHeaderFooter();
		content			=	masterDOB.getContentOnQuote();
		level			=	masterDOB.getLevels();
		align			=	masterDOB.getAlign();

		for(int i=0;i<headerFooter.length;i++)
		{
%>
			headerFooterArray[headerFooterArray.length]	=	'<%=headerFooter[i]!=null?headerFooter[i]:"H"%>';
			contentArray[contentArray.length]			=	'<%=content[i]!=null?content[i]:""%>';
			levelArray[levelArray.length]				=	'<%=level[i]!=null?level[i]:""%>';
			alignArray[alignArray.length]				=	'<%=align[i]!=null?align[i]:"L"%>';
<%	
		}
	}
	for(int i=0;i<noOfLegs;i++)
	{
		if(legDetails!=null)
		{
			legDOB			=	(QuoteFreightLegSellRates)legDetails.get(i);
			if(legDOB.isSpotRatesFlag())
			{
%>			
				spotRateFlags[<%=i%>]	=	'Y';
<%		}
			else
			{
%>			
				spotRateFlags[<%=i%>]	=	'N';
<%		}
		}
	}
%>
function displaySpans()
{
	var data		= "";
	//var disableFlag	= false;
	var isDisabled	= false;
<%
	for(int i=0;i<noOfLegs;i++)
	{
		if(legDetails!=null)
		{
			legDOB					=	(QuoteFreightLegSellRates)legDetails.get(i);

			legShipmentMode			=	""+legDOB.getShipmentMode();
		}
		
%>			
		if((spotRateFlags[<%=i%>]!= document.getElementById("spotRateFlag<%=i%>").value) && '<%=operation%>'=='Modify')
		{
			document.getElementById("SaveExit").disabled=true;
			isDisabled	=	true;
		}
		else if(!isDisabled)
		{
			if(document.getElementById("SaveExit")!=null)
			{
			document.getElementById("SaveExit").disabled=false;
			}
		}
		
		if(document.getElementById("spotRateFlag<%=i%>").value=='Y' && '<%=disabled%>'!='disabled')
		{
			//disableFlag	= true;
			document.getElementById("spotRateTypeSpan<%=i%>").style.display = 'block';
			
			if(document.getElementById("spotRateType<%=i%>").value=='SLAB')
			{
				document.getElementById("spotRatesFlat<%=i%>").style.display = "none";	
				document.getElementById("spotRatesSlab<%=i%>").style.display = "block";	
				document.getElementById("spotRatesList<%=i%>").style.display = "none";
				<%if(legShipmentMode!=null&&legShipmentMode.trim().length()>0)
				{
					if("1".equalsIgnoreCase(legShipmentMode))
					{%>
						document.getElementById("slabRateAir<%=i%>").style.display = "block";
       			  document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "none";
					 <%}
					 else 	if("2".equalsIgnoreCase(legShipmentMode))
					{%>
						 document.getElementById("slabRateSea<%=i%>").style.display = "block";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
					  <%}
					  else if("4".equalsIgnoreCase(legShipmentMode))
					{%>
						    document.getElementById("slabRateTruck<%=i%>").style.display = "block";
		            document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
					  <%}
					  
				}
				else
				{%>
				if(document.forms[0].shipmentMode.value=='Air')
				{
				    document.getElementById("slabRateAir<%=i%>").style.display = "block";
       			  document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "none";
				
				}
				if(document.forms[0].shipmentMode.value=='Sea')
				{
				    
					document.getElementById("slabRateSea<%=i%>").style.display = "block";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
					
				}
				if(document.forms[0].shipmentMode.value=='Truck')
				{
				    document.getElementById("slabRateTruck<%=i%>").style.display = "block";
		            document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
				}
				
				<%}%>


			}



			else if(document.getElementById("spotRateType<%=i%>").value=='FLAT')
			{
				document.getElementById("spotRatesFlat<%=i%>").style.display = "block";	
				document.getElementById("spotRatesSlab<%=i%>").style.display = "none";	
				document.getElementById("spotRatesList<%=i%>").style.display = "none";
				 document.getElementById("seaFCL<%=i%>").style.display = "none";
				 <%if(legShipmentMode!=null&&legShipmentMode.trim().length()>0)
				{
                  if("1".equalsIgnoreCase(legShipmentMode))
					{%>
                   document.getElementById("slabRateAir<%=i%>").style.display = "block";
              		document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
					  <%}
					  else if("2".equalsIgnoreCase(legShipmentMode))
					{%>
						    document.getElementById("slabRateSea<%=i%>").style.display = "block";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
					  <%}
					  else if("4".equalsIgnoreCase(legShipmentMode))
					{%>
						   document.getElementById("slabRateTruck<%=i%>").style.display = "block";
					 document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
					  <%}
					  
				}
				else
				{
				%>
				if(document.forms[0].shipmentMode.value=='Air')
				{
				    document.getElementById("slabRateAir<%=i%>").style.display = "block";
              		document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
				}
				if(document.forms[0].shipmentMode.value=='Sea')
				{
				     document.getElementById("slabRateSea<%=i%>").style.display = "block";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					 document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
				
				}
				if(document.forms[0].shipmentMode.value=='Truck')
				{
				    document.getElementById("slabRateTruck<%=i%>").style.display = "block";
					 document.getElementById("slabRateSea<%=i%>").style.display = "none";
					 document.getElementById("slabRateAir<%=i%>").style.display = "none";
					  document.getElementById("seaFCL<%=i%>").style.display = "none";
				}
				
              <%}%>

			}

			else if(document.getElementById("spotRateType<%=i%>").value=='LIST')
			{
				document.getElementById("spotRatesFlat<%=i%>").style.display = "none";	
				document.getElementById("spotRatesSlab<%=i%>").style.display = "none";
				document.getElementById("spotRatesList<%=i%>").style.display = "block";
				 document.getElementById("seaFCL<%=i%>").style.display = "block";
				  document.getElementById("slabRateAir<%=i%>").style.display = "none";
            		document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 <%if(legShipmentMode!=null&&legShipmentMode.trim().length()>0)
				{
                  if("1".equalsIgnoreCase(legShipmentMode))
					{%>
					   document.getElementById("slabRateAir<%=i%>").style.display = "block";
            		document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "none";
					 <%}
					 else if("2".equalsIgnoreCase(legShipmentMode))
					{%>
						 document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateAir<%=i%>").style.display = "none";
					document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "block";
					 <%}
					 else if("4".equalsIgnoreCase(legShipmentMode))
					{%>
						  document.getElementById("slabRateTruck<%=i%>").style.display = "block";
					document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateAir<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "none";
					 <%}
				}
				else
				{%>
				if(document.forms[0].shipmentMode.value=='Air')
				{
				    document.getElementById("slabRateAir<%=i%>").style.display = "block";
            		document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "none";
				}
				if(document.forms[0].shipmentMode.value=='Sea')
				{
				    /*if(document.forms[0].consigneeConsoleType.value=='FCL'||document.forms[0].shipperConsoleType.value=='FCL')
					{
					document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateAir<%=i%>").style.display = "none";
					document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "block";
					}
					else
					{*/
					document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateAir<%=i%>").style.display = "none";
					document.getElementById("slabRateTruck<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "block";
					


				}
				if(document.forms[0].shipmentMode.value=='Truck')
				{
				    document.getElementById("slabRateTruck<%=i%>").style.display = "block";
					document.getElementById("slabRateSea<%=i%>").style.display = "none";
					document.getElementById("slabRateAir<%=i%>").style.display = "none";
					 document.getElementById("seaFCL<%=i%>").style.display = "none";
				}
				
           <%}%>

		}
			}
		else
	{
		document.getElementById("spotRateFlag<%=i%>").value='N';
			document.getElementById("spotRateTypeSpan<%=i%>").style.display = 'none';
			document.getElementById("spotRatesFlat<%=i%>").style.display = "none";	
			document.getElementById("spotRatesSlab<%=i%>").style.display = "none";
			document.getElementById("spotRatesList<%=i%>").style.display = "none";
       document.getElementById("slabRateTruck<%=i%>").style.display = "none";
			document.getElementById("slabRateSea<%=i%>").style.display = "none";
			document.getElementById("slabRateAir<%=i%>").style.display = "none";
		 document.getElementById("seaFCL<%=i%>").style.display = "none";
		}
		
		/*if(disableFlag)
		{
			document.forms[0].serviceLevelId.disabled=true;
			document.forms[0].serviceLvlLov.disabled=true;
		}			
		else
		{
			document.forms[0].serviceLevelId.disabled=false;
			document.forms[0].serviceLvlLov.disabled=false;
		}*/
<%
	}
%>
	
	
}

function openLink(url)
{
	if(document.forms[0].preQuoteId.value.length==0)
	{
		alert('Please Enter Quote Id');
		document.forms[0].preQuoteId.focus();
		return false;
	}
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Options='width=800,height=600,resizable=yes';
	Features=Bars+','+Options;
	Win=open(url,'View',Features);
}

var noOfLegs	  = '<%=noOfLegs%>';
var Url			  =	'';
var Bars		  = '';
var Features	  = '';
var Options		  = '';
var	shipmentMode  = '';
	


function changeToUpper(field)
{
	//alert(field.name);
	field.value = field.value.toUpperCase();
}
function chkForNumeric(input)
{
	if(isNaN(input.value))
	{
		alert('Please Enter Numeric Values Only');
		input.value='';
		input.focus();
		return false;
	}
}
function chrnum(input)
{
	s = input.value;
	var filteredValues;

	if(input.name=='shipperZipCode'||input.name=='consigneeZipCode')
		filteredValues = "''~!@#$%^&*()+=|\:;<>,./?";
	else
		filteredValues = "''~!@#$%^&*()+=|\:;<>,./?";

	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c.toUpperCase();
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
function initialize()
{
	importXML('<%=request.getContextPath()%>/xml/QMSQuote.xml');
}

function initializeDynamicContentRows()
{
	initializeDynamicChargeDetails();
	initializeDynamicContentDetails();
	disableButtons();
}

function initializeDynamicChargeDetails()
{
	var tableObj	= document.getElementById("QuoteChargeGroups");
	var totalLanes	= chargeGroupsArray.length ==0 ? 1 : chargeGroupsArray.length;
	for(var i=0;i<totalLanes;i++)
	{
		createDynamicContentRow(tableObj.getAttribute("id"));
		if(chargeGroupsArray.length==0)
			return;

		var chargeGroupId	=	document.getElementsByName("chargeGroupId"); 

		chargeGroupId[i].value	=	chargeGroupsArray[i];
	}
}

function initializeDynamicContentDetails()
{
	var tableObj	= document.getElementById("QuoteContent");
	var totalLanes	= headerFooterArray.length ==0 ? 1 : headerFooterArray.length;
	for(var i=0;i<totalLanes;i++)
	{
		createDynamicContentRow(tableObj.getAttribute("id"));

		if(headerFooterArray.length==0)
			return;

		var headerFooter	=	document.getElementsByName("headerFooter");
		var content			=	document.getElementsByName("content");
		var level			=	document.getElementsByName("level");
		var align			=	document.getElementsByName("align");

		headerFooter[i].value	=	headerFooterArray[i];
		content[i].value		=	contentArray[i];
		level[i].value			=	levelArray[i];
		align[i].value			=	alignArray[i];
	}
}

function enableDisableCAP()
{
	if(document.forms[0].cargoAcceptance.options!=null)
		{
	if(document.forms[0].cargoAcceptance.options.value!='OTHER')
		document.forms[0].cargoAccPlace.readOnly = true;
	else 
	{
		document.forms[0].cargoAccPlace.readOnly = false;
	}
		}
}
function populateCAP()//Modified by rk
{
	enableDisableCAP();
	var temp		=	'';
	if(document.forms[0].incoTerms.options!=null)
		{
	if(document.forms[0].incoTerms.options.value=='EXW' || document.forms[0].incoTerms.options.value=='FCA' || document.forms[0].incoTerms.options.value=='FAS' ||document.forms[0].incoTerms.options.value=='FOB'){
		if(document.forms[0].cargoAcceptance.options!=null)
		{
			if(document.forms[0].cargoAcceptance.options.value=='DDAO')
			{
					temp = document.forms[0].incoTerms.options.value;
					temp = temp +" DGF TERMINAL "+ document.forms[0].originLocationName.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='PORT')
			{
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].originPort.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='ZIPCODE')
			{
					if(document.forms[0].shipperZipCode.value.length==0)
					{
						alert('Please Enter Shipper Zip Code');
						document.forms[0].shipperZipCode.focus();
						document.forms[0].cargoAcceptance.options.value='';
						document.forms[0].cargoAccPlace.value = '';
						return false;
					}
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].shipperZipCode.value+" "+document.forms[0].originLocationName.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='OTHER')
			{
					temp = document.forms[0].incoTerms.options.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='')
			{
					document.forms[0].cargoAccPlace.value = '';
			}
	}
	}
	else
	{
		if(document.forms[0].cargoAcceptance.options!=null)
		{
		if(document.forms[0].cargoAcceptance.options.value=='DDAO')
			{
					temp = document.forms[0].incoTerms.options.value;
					temp = temp +" DGF TERMINAL "+ document.forms[0].destLocationName.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='PORT')
			{
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].destPort.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='ZIPCODE')
			{
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].consigneeZipCode.value+" "+document.forms[0].destLocationName.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='OTHER')
			{
					temp = document.forms[0].incoTerms.options.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='')
			{
					document.forms[0].cargoAccPlace.value = '';
			}
		}
	}
		}
}
function disableButtons()
{	
	for(var i=0;i<document.forms[0].elements.length;i++)
	{
		if(document.forms[0].elements[i].type=='button')
			document.forms[0].elements[i].disabled=true;
		else if(document.forms[0].elements[i].type=='checkbox')
			document.forms[0].elements[i].disabled=true;
		else if(document.forms[0].elements[i].type=='text' || document.forms[0].elements[i].type=='textarea')
			document.forms[0].elements[i].readOnly=true;
	}

	for(var j=0;j<document.getElementsByName("chargeGroupId").length;j++)
	{
		if(document.getElementsByName("chargeGroupId")[j].value.length>0)
			document.getElementsByName("chargeGroupIdDet")[j].disabled=false;
	}
}
function popUpWindow(input)
{
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;
	Bars = 'directories=no,location=no,menubar=no,status=yes,titlebar=no,scrollbars=yes';
	Options='width=400,height=300,resizable=yes';
	if(input.name=='chargeGroupIdDet')
	{
		btnId = input.id.substring(input.name.length);
		searchString=document.getElementById("chargeGroupId"+btnId).value;
		Url='etrans/QMSChargesGroupingMaster.jsp?Operation=View&chargeGroupId='+searchString+"&fromWhere=Quote";
		Options='width=800,height=600,resizable=yes';
	}
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}
function changeConsoleVisibility(flag)
{
	var console	 = document.getElementById(flag+'Console');
	
	if(document.getElementById(flag+'Mode').value=='2')
		console.style.display="block";
	else
	{
		console.style.display="none";
		document.getElementById(flag+'ConsoleType').options[0].selected = true;
	}
}
function salesPersonFlag()
{
	<%if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
	{%>
	      document.forms[0].salesPersonFlag.checked=true;
	<%}%>
	
}
</script>

</head>

<body onLoad="initialize();displaySpans();populateCAP();changeConsoleVisibility('shipper');changeConsoleVisibility('consignee');salesPersonFlag()">
<form  method="post" name="QuoteMaster" action="QMSQuoteController" onsubmit='document.forms[0].hazardousInd.disabled=false'>
	<table width="101%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>
        	 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
		<tr class="formlabel">
			<td >QUOTE(<%=masterDOB.getQuoteId()%>,<%=quoteStatus%>) - <%=operation!=null?operation.toUpperCase():""%></td><td align='right'>QS1060201</td>
		</tr>
			 <!-- @@WPBN issue-167677 -->
		</table>
		<table width="101%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
<%
			if(masterDOB.getShipmentMode()==1)
				shipMode = "Air";
			else if (masterDOB.getShipmentMode()==2)
				shipMode = "Sea";
			else 
				shipMode = "Truck";
				
%>
	<tr class="formdata">
			<td  width='15%'>Quote Type:<br>
				<select name="shipmentMode" size="1" class="select">
					<option value="<%=shipMode%>"><%=shipMode%></option>
				</select>
			</td>

			 <!-- Added By Kishore For Weight Break in Single Quote  -->

			<td  width='15%' valign="top" nowrap>Weight Break<br>	
				<select name="WeightBreak" size="1" class="select" >
							<option value=<%=masterDOB.getWeightBreak()%> > <%=masterDOB.getWeightBreak()%> </option>
				 </select>
			</td>

			 <!-- End Of Kishore For Weight Break in Single Quote  -->


			<td align='center' width='3%'>Flag<br>
<%
			if(masterDOB.isImpFlag())
			{
				impFlag			= "Imp";
				impFlagValue	= "I";
			}
			else
			{
				impFlag			= "&nbsp;&nbsp;&nbsp;";
				impFlagValue	= "U";
			}
%>
				<select name="impFlag" size="1" class="select">
					<option value="<%=impFlagValue%>"><%=impFlag%></option>
				</select>
			</td>
			<td width ='20%'>Quote Id:
<%			
			
			if(!"Add".equalsIgnoreCase(operation))
			{
%>
			<a href="#" onclick='openLink("QMSQuoteController?Operation=View&subOperation=Report&QuoteId=<%if(masterDOB.getPreQuoteId()!=null&& masterDOB.getPreQuoteId().length()>1){%><%=masterDOB.getPreQuoteId()%><%}%>")'><font color="#0000FF"><b>Previous</b></font></a>
			<!-- in the above line masterDOB.getPreQuoteId()!=0  replaced with masterDOB.getPreQuoteId()!=null by subrahmanyam for the enhancement #146971 and #146970 on 03/12/08-->
<%
			}
%>
				<input class="text" maxLength="20" name="preQuoteId"  size="15" onKeyPress="return getNumberCode(this)" value="<%if(masterDOB.getPreQuoteId()!=null&& masterDOB.getPreQuoteId().length()>1){%><%=masterDOB.getPreQuoteId()%><%}%>" readOnly>
				<!-- in the above line masterDOB.getPreQuoteId()!=0  replaced with masterDOB.getPreQuoteId()!=null by subrahmanyam for the enhancement #146971 and #146970 on 03/12/08-->

			</td>

			
			<td width ='15%'>Created&nbsp;Date:<br>
				<input class="text" maxLength="25" name="createdDate"  size="10" value="<%=createdDate!=null?createdDate:currentDate%>">
			</td>
			<td width ='15%'>Eff Date:<br>
				<!-- <input class="text" maxLength="25" name="effDate"  size="10" value="<%=effDate!=null?effDate:currentDate%>"> -->
				 <input class="text" maxLength="25" name="effDate"  size="10" value="<%=modifiedDate!=null?modifiedDate:effDate!=null?effDate:currentDate%>"> 
				
			</td>
			<td width ='15%'>Valid To:<br>
				<input class="text" maxLength="25" name="validTo"  size="10" value="<%=validTo!=null?validTo:""%>">
			</td>
			<td width ='25%'>
				Acceptance Validity: <br><!--Acceptance Validity Period:-->
				<input class="text" maxLength="16" name="accValidityPeriod"  size="12" onKeyPress="return getNumberCode(this)" value="<%if(masterDOB.getAccValidityPeriod()!=0){%><%=masterDOB.getAccValidityPeriod()%><%}%>">
			</td>
       <!--@@Added by kameswari for WPBN issue-61318-->
			<%
				createdBy	=	masterDOB.getCreatedBy()!=null?masterDOB.getCreatedBy():loginbean.getUserId();

%>
         
			<td width ='17%'>Created By:<br>
				<input class="text" maxLength="25" name="createdBy"  size="16" value="<%=createdBy%>" readOnly>
			</td>
			<!--@@WPBN issue-61318-->

		</tr>
        </table>
	   <table  width="101%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata" >
			<td  width ='20%'  rowspan='1'>Customer&nbsp; Id &nbsp;:&nbsp;<font color="#ff0000">*</font><br>
				<input class="text" maxLength="25" name="customerId"  size="12" value="<%=masterDOB.getCustomerId()!=null?masterDOB.getCustomerId():""%>" row="0" <%=readOnly%>><br>
			</td>
			<td width ='20%'  rowspan='1'>Address:
				<textarea class="text" cols="30" rows="4" name="address" readOnly><%=masterDOB.getCustomerAddress()!=null?masterDOB.getCustomerAddress():""%></textarea>
				<input type="hidden" name="addressId" value='<%="0".equalsIgnoreCase(addressId)?"":addressId%>'>
			</td>
			<td width ='20%'  rowspan='1'>Attention To:<!--<br>-->
				<select class="select"  name="contactPersons" size="4" multiple style="width:200px;margin:0px 0 5px 0;">
<%				
				if(masterDOB.getCustomerContacts()!=null)
				{
				  for(int i=0;i<masterDOB.getCustomerContacts().length;i++)
				  {
					  custContactIds = custContactIds+masterDOB.getCustomerContacts()[i]+",";
%>					<option value='<%=masterDOB.getCustContactNames()[i]%>'  selected><%=masterDOB.getCustContactNames()[i]%></option>
<%
				  }
				}
%>
				</select><br>
			</td>
			<td  width ='16%'>Sales Person:<br>
				<input class="text" maxLength="25" name="salesPersonCode"  size="14" value="<%=masterDOB.getSalesPersonCode()!=null?masterDOB.getSalesPersonCode():""%>">
				<!--@@Added by kameswari for WPBN issue-61306,61318-->
				<td width ='20%' valign='top'>
				Email&nbsp;Copy&nbsp;to<br>Sales&nbsp;Person<input type='checkbox' name='salesPersonFlag' value='0'>
				</td>
					<!--@@WPBN issue-61318-->
			</td>
		</tr>
		</table>
		<table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata"><!-- Added by Rakesh for Issue:        on 28-02-2011 -->
		<td  width='28%'>
				Cust Req Date 
				<input class="text" maxLength="25" name="custDate"  size="10" value="<%=custDate!=null?custDate:""%>" readonly >
				<input class="input" type="button" value="..."  disabled>
		</td>
		   <td   width='25%'>Cust Req Time:
				<input class="text" name="custTime" maxlength="6" size="10" readonly     value="<%=custTime!=null?custTime:""%>">
			</td>	   <!-- Ended by Rakesh for Issue:        on 28-02-2011 -->
			<td  width="47%" colspan='1' valign="top">Cargo&nbsp;Notes: <!-- onBlur="chrnum(this)" -->
				<input class="text" name="overLengthCargoNotes"  maxlength="100" size="35" value="<%=masterDOB.getOverLengthCargoNotes()!=null?masterDOB.getOverLengthCargoNotes():""%>" readOnly>
			</td>
		</tr>
		</table>
	   <table  width="101%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
		<!--@@Commented by kameswari for WPBN issue-61318-->
		<!--<td width ='8%'>Incoterms:<br><br>
				<select class="select"  name="incoTerms" size="1" onchange='populateCAP()'>
					<option value="<%=masterDOB.getIncoTermsId()%>"><%=masterDOB.getIncoTermsId()%></option>
				</select>
			</td>
			<td width ='19%'>Cargo Acceptance Place:<br>
				<select class='select' name='cargoAcceptance' onchange='populateCAP()' style="width:118px;margin:0px 0 5px 0;">
					<option value='<%=masterDOB.getCargoAcceptance()%>'><%="DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"DGF":masterDOB.getCargoAcceptance()%></option>
				</select><br>
				<textarea name='cargoAccPlace' class="text" cols="16" rows='4' style="height:30" cols="25" rows='4'><%=masterDOB.getCargoAccPlace()!=null?masterDOB.getCargoAccPlace():""%></textarea>
			</td>-->
			<!--@@WPBN issue-61318-->
			<td width ='12%' valign="bottom">ServiceLevel:<br>
				<input class="text" maxLength="16" name="serviceLevelId"  size="4" value="<%=masterDOB.getServiceLevelId()!=null?masterDOB.getServiceLevelId():""%>">
			</td>
			<td  width ='11%' valign="bottom">Industry:<br>
				<input class="text" maxLength="16" name="industryId"  size="10" value="<%=masterDOB.getIndustryId()!=null?masterDOB.getIndustryId():""%>">
			</td>
			<!--<td width ='50%' >
			  <table  cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
			   <tr  class="formdata">-->
				<td valign="bottom">Commodity Type:<br>
						<select size="1" name="commodityType" class='select' style="width:300px;margin:0px 0 5px 0;">
						  <option value='<%=masterDOB.getCommodityType()!=null?masterDOB.getCommodityType():""%>'><%=masterDOB.getCommodityType()!=null?masterDOB.getCommodityType():""%></option>
						</select>
				</td>
				<!--@@Commented by kameswari for WPBN issue-61318-->
			   <!--</tr>
			   <tr  class="formdata">-->
			   <!--@@WPBN issue-61318-->
				  <td width ='8%' valign="bottom">  Commodity/<br>Product:
					<input class="text" maxLength="16" name="commodityId"  size="10" value="<%=masterDOB.getCommodityId()!=null?masterDOB.getCommodityId():""%>">
					</td>
				<td valign="bottom">
					Hazardous<br>
					<input type="checkbox" value="ON" name="hazardousInd" <%if(masterDOB.isHazardousInd()){%>checked<%}%>>
				</td>
				<td valign="bottom">
					UN # <br><input class="text" maxLength="16" name="unNo" size="10" value="<%=masterDOB.getUnNumber()!=null?masterDOB.getUnNumber():""%>">
				</td>
				<td valign="bottom">
					Class # <br><input class="text" maxLength="16" name="commodityClass"  id='class1' size="10" value="<%=masterDOB.getCommodityClass()!=null?masterDOB.getCommodityClass():""%>">
			   </td>
			   <!--@@Commented by kameswari for WPBN issue-61318-->
			 <!-- </tr>
			 </table>-->
			 <!--@@WPBN issue-61318-->
			</td>		
		</tr>
       </table>
	   <table  width="101%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formheader">
			<td  colspan="8">Lane Details</td>
		</tr>

		<tr class="formdata">
			<td  width="20%"nowrap>Quoting Station<br>
				<input class="text" maxLength="16" name="quotingStation" size="12"value="<%=masterDOB.getQuotingStation()!=null?masterDOB.getQuotingStation():loginbean.getTerminalId()%>" readonly>
			</td>
			<td width="22%">Origin Location:<br>
				<input class="text" maxLength="16" name="originLoc"  size="12" value="<%=masterDOB.getOriginLocation()!=null?masterDOB.getOriginLocation():""%>" <%=readOnly%>>
			</td>
			<td width="15%">Shipper&nbsp;Mode:<br>
				<select name="shipperMode" class='select' id='shipperMode'>
<%
				if(masterDOB.getShipperMode()!=null)
				{
%>
					<option value="<%=masterDOB.getShipperMode()%>"><%="1".equalsIgnoreCase(masterDOB.getShipperMode())?"Air":"Sea"%></option>
<%
				}
				else
				{
%>					<option></option>
<%
				}
%>				</select>
			  </td>
			  <td nowrap>
				<DIV id="shipperConsole" style="DISPLAY:none">
					Console&nbsp;Type:</br>
					<select name="shipperConsoleType" class='select' id='shipperConsoleType'>
						<option value="<%=masterDOB.getShipperConsoleType()%>"><%=masterDOB.getShipperConsoleType()%></option>
					</select>
				</DIV>
			</td>
			<td width="20%" nowrap>Shipper(Zipcode/Zone)<br>
				<input class="text" maxLength="16" name="shipperZipCode"  size="6" value="<%=masterDOB.getShipperZipCode()!=null?masterDOB.getShipperZipCode():""%>">&nbsp;<input class="text" maxLength="250" name="shipperZone"  size="6" value="<%=masterDOB.getShipperZones()!=null?masterDOB.getShipperZones():""%>">
			</td>
			<td width="18%">Origin Port:<br>
				<input class="text" maxLength="16" name="originPort"  size="12" value="<%=masterDOB.getOriginPort()!=null?masterDOB.getOriginPort():""%>" <%=readOnly%>>
			</td>
			<!--td  width="20%">Overlength Cargo Notes:<br> <!-- onBlur="chrnum(this)">
				<input class="text" name="overLengthCargoNotes"  maxlength="100" size="26" value="<%//=masterDOB.getOverLengthCargoNotes()!=null?masterDOB.getOverLengthCargoNotes():""%>">
			</td-->
		</tr>

		<tr class="formdata">
			<td width="20%">Routing ID:<br>
				<input class="text" maxLength="25" name="routeId"  size="12" value="<%=masterDOB.getRouteId()!=null?masterDOB.getRouteId():""%>" readOnly>
			</td>
			<td  width="22%">Destination&nbsp;Location:<br>
				<input class="text" maxLength="16" name="destLoc"  size="12" value="<%=masterDOB.getDestLocation()!=null?masterDOB.getDestLocation():""%>" <%=readOnly%>>
			</td>
			<td width="15%">Consignee&nbsp;Mode:<br>
				<select name="consigneeMode" class='select' id='consigneeMode'>
					<option value="<%=masterDOB.getConsigneeMode()!=null?masterDOB.getConsigneeMode():""%>"><%="1".equalsIgnoreCase(masterDOB.getConsigneeMode())?"Air":("2".equalsIgnoreCase(masterDOB.getConsigneeMode())?"Sea":"")%></option>
			  </td>
			  <td nowrap>
				<DIV id="consigneeConsole" style="DISPLAY:none">
					Console&nbsp;Type:</br>
					<select name="consigneeConsoleType" class='select' id='consigneeConsoleType'>
						<option value="<%=masterDOB.getConsigneeConsoleType()%>"><%=masterDOB.getConsigneeConsoleType()%></option>
					</select>
				</DIV>
			</td>
			<td  width="20%" nowrap>Consignee(Zipcode/Zone)<br>
				<input class="text" maxLength="16" name="consigneeZipCode"  size="6" value="<%=masterDOB.getConsigneeZipCode()!=null?masterDOB.getConsigneeZipCode():""%>">
				<input class="text" maxLength="250" name="consigneeZone"  size="6" value="<%=masterDOB.getConsigneeZones()!=null?masterDOB.getConsigneeZones():""%>">
			</td>
			<td  width="38%" colspan="2">Destination Port:<br>
				<input class="text" maxLength="16" name="destPort"  size="12" value="<%=masterDOB.getDestPort()!=null?masterDOB.getDestPort():""%>" <%=readOnly%>>
			</td>
			
		</tr>

	</table>
<%
for(int i=0;i<noOfLegs;i++)
{
		if(legDetails!=null)
		{
			legDOB					=	(QuoteFreightLegSellRates)legDetails.get(i);

			legShipmentMode			=	""+legDOB.getShipmentMode();
				
			if(legDOB.isSpotRatesFlag())
			{
				isSpotRatesFlag			=	true;
				spotRateType			=	legDOB.getSpotRatesType();
				weightBreaks			=	legDOB.getWeightBreaks();
				weightBreaksSize		=	weightBreaks.size();
				if(legDOB.getSpotRateDetails()!=null)
					spotRateDetails		=	legDOB.getSpotRateDetails();

				legServiceLevel			=	legDOB.getServiceLevel();
				legUOM					=	legDOB.getUom();
				legDensityRatio			=	legDOB.getDensityRatio();
				legCurrency             =   legDOB.getCurrency();
				leg						=	"("+legDOB.getOrigin()+"-"+legDOB.getDestination()+")";

			}
			else
			{
				leg						=	"("+legDOB.getOrigin()+"-"+legDOB.getDestination()+")";
			}
		}
%>
<!--@@Added by kameswari for WPBN issue-61318-->
<table width="101%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>	<tr class='formheader'><td colspan='2'>Incoterms<%=leg%></td></tr>
	<tr class='formdata'>
	<td width ='8%' valign="bottom">Incoterms:<!--<br><br>-->
				<select class="select"  name="incoTerms" size="1" onchange='populateCAP()'>
					<option value="<%=masterDOB.getIncoTermsId()%>"><%=masterDOB.getIncoTermsId()%></option>
				</select>
			</td>
<!-- @@Commented by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
			<!-- <td width ='19%'>Cargo Acceptance Place:<br> -->
<!-- @@ Added by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
				 <td width ='19%'>Named Place:<!-- @@ Ended by subrahmanyam for 150460 -->
				<select class='select' name='cargoAcceptance' onchange='populateCAP()' style="width:118px;margin:0px 0 5px 0;">
					<option value='<%=masterDOB.getCargoAcceptance()%>'><%="DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"DGF":masterDOB.getCargoAcceptance()%></option>
				</select>
				<!-- @@ commented by subrahmanyam for the pbn id: 187569 on 28th oct09 -->
<!-- 				<textarea name='cargoAccPlace' class="text" cols="16" rows='4' style="height:30" cols="25" rows='4'><%=masterDOB.getCargoAccPlace()!=null?masterDOB.getCargoAccPlace():""%></textarea>
 -->		
 <!-- Added by subrahmanyam for pbn id: 187569   on 28th oct09 -->
 				<%
					if("Add".equalsIgnoreCase(operation) || noOfLegs==1){%>
				<textarea name='cargoAccPlace' valign='bottom' class="text" onBlur="return check_Length(255,this)"  cols="16" rows='4' style="height:30" cols="25" rows='5'><%=masterDOB.getCargoAccPlace()!=null?masterDOB.getCargoAccPlace():""%></textarea>
				<%}else{
					 if(i==0){%>
				<textarea name='cargoAccPlace' valign='bottom' class="text" onBlur="return check_Length(255,this)"  cols="16" rows='4' style="height:30" cols="25" rows='5'><%=masterDOB.getCargoAccPlace()!=null?masterDOB.getCargoAccPlace():""%></textarea>
				<%}else if(i>0){
					if(masterDOB.getIncoTermsId()!=null)
					{
					  if("EXW".equalsIgnoreCase(masterDOB.getIncoTermsId()) || "FCA".equalsIgnoreCase(masterDOB.getIncoTermsId()) ||"FAS".equalsIgnoreCase(masterDOB.getIncoTermsId()) || "FOB".equalsIgnoreCase(masterDOB.getIncoTermsId()))
					  {
						if(masterDOB.getCargoAcceptance()!=null)
						{
							if("DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
							{
								cargoAccPlace = masterDOB.getIncoTermsId();
								cargoAccPlace = cargoAccPlace +" DGF TERMINAL "+ legDOB.getOrigin();
							}
							else if("PORT".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
							{
								cargoAccPlace = masterDOB.getIncoTermsId();
								cargoAccPlace = cargoAccPlace +" "+ legDOB.getOrigin();
							}
							else if("ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
							{
								cargoAccPlace = masterDOB.getIncoTermsId();
								cargoAccPlace = cargoAccPlace +" "+masterDOB.getShipperZipCode()+" "+ legDOB.getOrigin();
							}
							else if("ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
							{
								cargoAccPlace = masterDOB.getIncoTermsId();
								cargoAccPlace = cargoAccPlace +" "+masterDOB.getShipperZones()+" "+ legDOB.getOrigin();
							}
							else if("OTHER".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
							{
								cargoAccPlace = masterDOB.getIncoTermsId();
							}
							else if("".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
							{
								cargoAccPlace = "";
							}
						}
					}
					else
						{
							if(masterDOB.getCargoAcceptance()!=null)
							{
								if("DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
								{
									cargoAccPlace = masterDOB.getIncoTermsId();
									cargoAccPlace = cargoAccPlace +" DGF TERMINAL "+legDOB.getDestination();
								}
								else if("PORT".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
								{
									cargoAccPlace = masterDOB.getIncoTermsId();
									cargoAccPlace = cargoAccPlace + " "+legDOB.getDestination();
								}
								else if("ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
								{
									cargoAccPlace = masterDOB.getIncoTermsId();
								    cargoAccPlace = cargoAccPlace + " "+masterDOB.getConsigneeZipCode()+" "+legDOB.getDestination();
								}
								else if("ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
								{
									cargoAccPlace = masterDOB.getIncoTermsId();
									cargoAccPlace = cargoAccPlace + " "+masterDOB.getConsigneeZones()+" "+legDOB.getDestination();
								}
								else if("OTHER".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
								{
									cargoAccPlace = masterDOB.getIncoTermsId();
								}
								else if("".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
								{
									cargoAccPlace = "";
								}
							}
						}
					}
					%>

				
				<textarea name='cargoAccPlace' valign='bottom' class="text" onBlur="return check_Length(255,this)"  cols="16" rows='4' style="height:30" cols="25" rows='5'><%=cargoAccPlace%></textarea>
				<%}}%>


  <!-- ended by subrahmanyam for pbn id: 187569   on 28th oct09 -->


			</td>
			</tr>
			</table>
			<!--@@WPBN issue-61318-->
	<table width="101%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
	<tr class='formheader'><td colspan='2'>Spot Buy Rates<%=leg%></td></tr>
	<tr class='formdata'>
		<td nowrap>Spot Rates: 
		<select class="select"  id="spotRateFlag<%=i%>" name="spotRateFlag<%=i%>" size="1" onChange="displaySpans()" <%=disabled%>>
					<option value="<%=isSpotRatesFlag?"Y":"N"%>"><%=isSpotRatesFlag?"YES":"NO"%></option>
				</select>
			</td>
		</tr>
		<tr class='formdata'>
		<td>
		<span id="spotRateTypeSpan<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='1' bgcolor='#FFFFFF'>
		<tr  class='formdata'>
				<td valign="bottom">
					Spot Rate Type: 				
					<select class="select"  id="spotRateType<%=i%>" name="spotRateType<%=i%>" size="1" onChange="displaySpans()">
						<option value="<%=spotRateType%>"><%=spotRateType%></option>
					</select>
				</td>
				<td valign="bottom">
					Service Level:<input type='text' class="text"  id="serviceLevelType<%=i%>" name="serviceLevel<%=i%>" size='10' value='<%=legServiceLevel!=null?legServiceLevel:""%>' onblur='this.value=this.value.toUpperCase()'>
				</td>
				<td valign="bottom">
					UOM:
					<select class="select"  id="uom<%=i%>" name="uom<%=i%>" size="1">
						<option value="<%=legUOM%>"><%=legUOM%></option>
					</select>
				</td>
				<td valign="bottom">
					Density Ratio:<input type='text' class="text" id="densityRatio<%=i%>" name="densityRatio<%=i%>" size='10' value='<%=legDensityRatio!=null?legDensityRatio:""%>' onblur='this.value=this.value.toUpperCase()'>
					<input type='hidden' name='legShipmentMode<%=i%>' value='<%=legShipmentMode%>'/>
				</td>
				<td valign="bottom">Currency ID:
			        <input type='text' class='text' name="currencyId<%=i%>" id="currencyId<%=i%>" size='10'  onBlur='this.value=this.value.toUpperCase()' value='<%=legCurrency!=null?legCurrency:""%>'>
					
				</td>
			</tr>
			</table>
			</span>
			</td>
		</tr>
		<!--<tr class='formdata'><td colspan='2'>
		<span id="spotRatesSlab<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='1' bgcolor='#FFFFFF'>
			<tr class='formdata'>
				<td>Slab</td>
				<td align='center'>MIN
				<input type='hidden' maxLength='8' name='slabWeightBreak<%=i%>' size='6' value='MIN'></td>
<%
				for(int j=0;j<11;j++)
				{
%>
					<td align='center' ><input class='text' maxLength='8' name='slabWeightBreak<%=i%>' size='6' onKeyPress='<%if(j==0){%>return getNumberCodeNegative(this)<%}else{%>return getNumberCode(this)<%}%>' value="<%=("SLAB".equalsIgnoreCase(spotRateType)&& weightBreaks!=null)?((j+1)<weightBreaksSize?weightBreaks.get(j+1):""):""%>"></td>
<%
				}
%>
			</tr>

			<tr class='formdata'>
				<td>Rate</td>
<%
				for(int j=0;j<weightBreaksSize;j++)
				{
					if(spotRateDetails!=null && "SLAB".equalsIgnoreCase(spotRateType))
						spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
					else if("SLAB".equalsIgnoreCase(spotRateType))
						spotRate[2]	=	0;
%>
					<td align='center' ><input class='text' maxLength='8' name='slabRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%="SLAB".equalsIgnoreCase(spotRateType)?spotRate[2]:0%>'></td>
<%				}
				for(int k=weightBreaksSize;k<12;k++)
				{
%>		
					<td align='center'><input class='text' maxLength='8' name='slabRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}

%>
			</tr>
		</table>
		</tr>
		<span id="spotRatesFlat<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='1' bgcolor='#FFFFFF'>
			<tr class='formdata'>			
				<td width='10%'></td>
				<td align='center' width='10%'>MIN
				<input type='hidden' maxLength='8' name='flatWeightBreak<%=i%>' size='6' value='MIN'></td>
				<td align='center' width='10%'>FLAT
				<input type='hidden' maxLength='8' name='flatWeightBreak<%=i%>' size='6' value='FLAT'></td>
				<td>&nbsp;</td>
				</tr>
				<tr class='formdata'>
				<td width='10%'>Rate</td>
				<td align='center' ><input class='text' maxLength='8' name='flatRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=("FLAT".equalsIgnoreCase(spotRateType) && spotRateDetails!=null)?((double[])spotRateDetails.get("MIN"))[2]:0%>'></td>
				<td align='center' ><input class='text' maxLength='8' name='flatRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=("FLAT".equalsIgnoreCase(spotRateType) && spotRateDetails!=null)?((double[])spotRateDetails.get("FLAT"))[2]:0%>'></td>
				<td>&nbsp;</td>
			
			</tr>
		</table>
		</span>
		<span id="spotRatesList<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='0' bgcolor='#FFFFFF'>
			<tr class='formdata'>
				<td><div id='listType<%=i%>'><%="1".equalsIgnoreCase(legShipmentMode)?"ULD Types":"Container Types"%></div></td>
<%
				for(int j=0;j<12;j++)
				{
%>
					<td align='center'><input class='text' maxLength='8' name='listWeightBreak<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value="<%=("LIST".equalsIgnoreCase(spotRateType)&& weightBreaks!=null)?(j<weightBreaksSize?weightBreaks.get(j):""):""%>" readOnly></td>
<%
				}
%>
			</tr>

			<tr class='formdata'>
				<td>Rate</td>
<%
				for(int j=0;j<weightBreaksSize;j++)
				{
					if(spotRateDetails!=null && "LIST".equalsIgnoreCase(spotRateType))
						spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
					else if("LIST".equalsIgnoreCase(spotRateType))
						spotRate[2]	=	0;
%>
					<td align='center' ><input class='text' maxLength='8' name='listRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%="LIST".equalsIgnoreCase(spotRateType)?spotRate[2]:0%>'></td>
<%				}
				for(int k=weightBreaksSize;k<12;k++)
				{
%>		
					<td align='center'><input class='text' maxLength='8' name='listRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}

%>
			</tr>
		</table>
		</span>>

	</td></tr>-->
	<tr class='formdata'><td  colspan='2'>
		  <span id="spotRatesSlab<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='0' bgcolor='#FFFFFF'>
		  <tr class='formdata'>
			<td valign="bottom">Slab</td>
				<td align='center' valign="bottom">MIN<font color=#ff0000>*</font>
				<input type='hidden' maxLength='8' name='slabWeightBreak<%=i%>' size='6' value='MIN'></td>
<%                 int count8=0;
				for(int j=0;j<weightBreaksSize;j++)
				{
					if("SLAB".equalsIgnoreCase(spotRateType)&& weightBreaks!=null&&j<weightBreaksSize-1)
					{
						if(!("FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j+1))||"FSMIN".equalsIgnoreCase(
						// modified by phani sekhar for wpbn 176749 on 20090716	
						(String)weightBreaks.get(j+1))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j+1))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"CAF%".equalsIgnoreCase((String)weightBreaks.get(j+1))||"BAFMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"BAFM3".equalsIgnoreCase((String)weightBreaks.get(j+1))||"CSF".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j+1))||"PSSMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"PSSM3".equalsIgnoreCase((String)weightBreaks.get(j+1)) ))
						{count8++;%>
				
					<td align='center' valign="bottom"><input class='text' maxLength='8' name='slabWeightBreak<%=i%>' size='6' onKeyPress='<%if(j==0){%>return getNumberCodeNegative(this)<%}else{%>return getNumberCode(this)<%}%>' value="<%=weightBreaks.get(j+1)%>"></td>
<%                      }
					
					}
				}
                   	for(int k=count8;k<11;k++)
				{%> 
                     <td align='center' valign="bottom"><input class='text' maxLength='8' name='slabWeightBreak<%=i%>' size='6'  value=""></td>
				<%
						}
			
%>
			</tr>

			<tr class='formdata' >
				<td>Freight Rate</td>
<%                 int count7=0;
				for(int j=0;j<weightBreaksSize;j++)
				{
					if(spotRateDetails!=null && "SLAB".equalsIgnoreCase(spotRateType)&&j<(weightBreaksSize))
					{
						/*if(!("FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j+1))||"FSMIN".equalsIgnoreCase(
							(String)weightBreaks.get(j))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"CAF%".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFM3".equalsIgnoreCase((String)weightBreaks.get(j))||"CSF".equalsIgnoreCase((String)weightBreaks.get(j))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSM3".equalsIgnoreCase((String)weightBreaks.get(j)) ))
						{*/
						//@@MOdified by Kameswari for the WPBN issue-118148
						if(!("FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"FSMIN".equalsIgnoreCase(
							(String)weightBreaks.get(j))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"CAF%".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFM3".equalsIgnoreCase((String)weightBreaks.get(j))||"CSF".equalsIgnoreCase((String)weightBreaks.get(j))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSM3".equalsIgnoreCase((String)weightBreaks.get(j)) ))
						{
						spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
						count7++;
						%>
						<td align='center' ><input class='text' maxLength='8' name='slabRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%="SLAB".equalsIgnoreCase(spotRateType)?spotRate[2]:0%>'></td>
					<%	}
						
					}
					
			}
				for(int k=count7;k<12;k++)
				{
%>		
					<td align='center'><input class='text' maxLength='8' name='slabRate<%=i%>' size='6' value='0.0' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}

%>
			</tr>
			</table>
			</span>
				<span id="spotRatesFlat<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='0' cellspacing='0' bgcolor='#FFFFFF' border='0'>
			<tr class='formdata'>
				<table width='101%' cellpadding='0' cellspacing='0' bgcolor='#FFFFFF' border='0'>
                <tr class='formdata'>
				<td width="13%">&nbsp;</td>
				<td  width="10%">MIN<font color=#ff0000>*</font>
				<input type='hidden' maxLength='8' name='flatWeightBreak<%=i%>' size='6' value='MIN'></td>
				<td>FLAT<font color=#ff0000>*</font>
				<input type='hidden' maxLength='8' name='flatWeightBreak<%=i%>' size='6' value='FLAT'></td>
				</tr>
				<tr class='formdata'>
				<td >Freight&nbsp;Rate</td>
				<td ><input class='text' maxLength='8' name='flatRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=("FLAT".equalsIgnoreCase(spotRateType) && spotRateDetails!=null)?((double[])spotRateDetails.get("MIN"))[2]:0%>'></td>
				<td ><input class='text' maxLength='8' name='flatRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=("FLAT".equalsIgnoreCase(spotRateType) && spotRateDetails!=null)?((double[])spotRateDetails.get("FLAT"))[2]:0%>'></td>
				</tr>
			</table>
			</span>
				<span id="spotRatesList<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='0' bgcolor='#FFFFFF'>
			<tr class='formdata'>
			<td valign="bottom"><div id='listType<%=i%>'><%="1".equalsIgnoreCase(legShipmentMode)?"ULD Types":"Container Types"%></div></td>
<%                int count=0;
if(weightBreaks!=null)
	{
				for(int j=0;j<weightBreaks.size();j++)
				{
	
	               if(!(((String)(weightBreaks.get(j))).endsWith("csf")||((String)(weightBreaks.get(j))).endsWith("pss")||((String)(weightBreaks.get(j))).endsWith("caf")||((String)(weightBreaks.get(j))).endsWith("baf")))
					{ count++;
					   
%>
					
					<td align='center'><input class='text' maxLength='8' name='listWeightBreak<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value="<%=("LIST".equalsIgnoreCase(spotRateType)&& weightBreaks!=null)?(j<weightBreaksSize?weightBreaks.get(j):""):""%>" readOnly></td>

				<%
					}
				}

				for(int k=count;k<12;k++)
					
				{%>
				    <td align='center'><input class='text' maxLength='8' name='listWeightBreak<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value="" readOnly></td>

				 <% }
				
	}
                
 else
	{
			  for(int k=0;k<12;k++)
					
				{
%>
			
			<td align='center'><input class='text' maxLength='8' name='listWeightBreak<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value="" readOnly></td>
			<%}
	}%>
			</tr>

			<tr class='formdata'>
				<td valign="bottom">Rate</td>
<%
				
			int temp=0;
			if(weightBreaks!=null)
	         {
			for(int j=0;j<weightBreaksSize;j++)
				{
				if(spotRateDetails!=null && "LIST".equalsIgnoreCase(spotRateType))
					{
			    	if(!(((String)(weightBreaks.get(j))).endsWith("csf")||((String)(weightBreaks.get(j))).endsWith("pss")||((String)(weightBreaks.get(j))).endsWith("caf")||((String)(weightBreaks.get(j))).endsWith("baf")))
					{	
   						containerBreak.add(weightBreaks.get(j));//@@Added by subrahmanyam for the wpbn id:196050 on 29/Jan/10
						spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
					temp++;
%>
					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='listRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%="LIST".equalsIgnoreCase(spotRateType)?spotRate[2]:0%>'></td>
<%				}
					}
					}
				for(int k=temp;k<12;k++)
				{
%>		
					<td align='center' valign="bottom"><input class='text' maxLength='8' name='listRate<%=i%>' value='' size='6' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}
	}
%>
			</tr>
			</table>
			</span>
			  <span id="slabRateSea<%=i%>" style='DISPLAY:none'>
				<table width='101%' cellpadding='0' cellspacing='0' bgcolor='#FFFFFF' border='0'>
			<tr class='formdata'>
				<td valign="bottom" width="13%">&nbsp;</td>
				<td valign="bottom" width="10%">MIN
				<input type='hidden' maxLength='8' name='seaWeightBreak<%=i%>' size='6' value='CAFMIN'></td>
				<td valign="bottom">Percent
				<input type='hidden' maxLength='8' name='seaWeightBreak<%=i%>' size='6' value='CAF%'></td>
				<td>&nbsp;</td>
				<td valign="bottom" width="10%">MIN
				<input type='hidden' maxLength='8' name='seaWeightBreak<%=i%>' size='6' value='BAFMIN'></td>
				<td valign="bottom">M3
				<input type='hidden' maxLength='8' name='seaWeightBreak<%=i%>' size='6' value='BAFM3'></td>
				<td>&nbsp;</td>
				<td valign="bottom" width="10%">MIN
				<input type='hidden' maxLength='8' name='seaWeightBreak<%=i%>' size='6' value='PSSMIN'></td>
				<td valign="bottom">M3
				<input type='hidden' maxLength='8' name='seaWeightBreak<%=i%>' size='6' value='PSSM3'></td>
				<td>&nbsp;</td>
				<td valign="bottom">Absolute
				<input type='hidden' maxLength='8' name='seaWeightBreak<%=i%>' size='6' value='CSF'></td>
		  </tr>
		 <tr class='formdata' >
		  <td>C.A.F%</td>
		       <%if(weightBreaks!=null&&spotRateDetails!=null)
	         {
			 int count4 = 0;
			for(int j=0;j<weightBreaksSize;j++)
			 { 
				  if("CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {count4++;
					%>
				  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("CAFMIN"))[2]%>'></td>
				  <%
				 }
			 }
			       if(count4==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				 count4=0;
			 	
				 for(int j=0;j<weightBreaksSize;j++)
			   { 
				  if("CAF%".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {count4++;
					%>
				  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("CAF%"))[2]%>'></td>
				  <%
				 }
			   }	  if(count4==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				  
					 count4=0;%>
			 	 <td>B.A.F</td>

				<%for(int j=0;j<weightBreaksSize;j++)
			    { 
				  if("BAFMIN".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count4++;%>
				  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("BAFMIN"))[2]%>'></td>
				  <%
				 }
				}  if(count4==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				
					 count4=0;
			 	 %>
		        
		  		<%for(int j=0;j<weightBreaksSize;j++)
			   { 
				  if("BAFM3".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count4++;%>
				  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("BAFM3"))[2]%>'></td>
				  <%
				 }
			   } 
					  if(count4==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				          
					 count4=0;%>
					 <td>P.S.S</td>

				<%for(int j=0;j<weightBreaksSize;j++)
			    { 
				  if("PSSMIN".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count4++;%>
				  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("PSSMIN"))[2]%>'></td>
				  <%
				 }
				}  if(count4==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				
					 count4=0;
			 	 
		        
		  		for(int j=0;j<weightBreaksSize;j++)
			   { 
				  if("PSSM3".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count4++;%>
				  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("PSSM3"))[2]%>'></td>
				  <%
				 }
			   } 
					  if(count4==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				
					 count4=0;%>
			 	  <td>C.S.F</td>
				 <% for(int j=0;j<weightBreaksSize;j++)
			    { 
				  if("CSF".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count4++;%>
				  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("CSF"))[2]%>'></td>
				  <%
				 }
				}
					  if(count4==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
			 }else
	        {%>
			 <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			  <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			    <td>B.A.F</td>
			   <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			     <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
				  <td>P.S.S</td>
			   <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			     <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
				   <td>C.S.F</td>
				 <td ><input class='text' maxLength='8' name='seaRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
				 
			<% }%>
		   </tr>    
		  </table>
		  </span>
		  	  <span id="slabRateTruck<%=i%>" style='DISPLAY:none'>
				<table width='101%' cellpadding='0' cellspacing='0' bgcolor='#FFFFFF' border='0'>
			<tr class='formdata'>
				<td width="12%">&nbsp;</td>
				<td valign="bottom">Percent</td>
				<input type='hidden' maxLength='8' name='truckWeightBreak<%=i%>' size='6' value='SURCHARGE'></td>
	         </tr>
		 <tr class='formdata'>
		  <td>Surcharge</td>
		 <%if(weightBreaks!=null&&spotRateDetails!=null)
	         {
			 int count6 = 0;
			for(int j=0;j<weightBreaksSize;j++)
			 { 
				  if("SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count6++;%>
				  <td ><input class='text' maxLength='8' name='truckRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("SURCHARGE"))[2]%>'></td>
				  <%
				 }
			 }
					  if(count6==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='truckRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				 
					 count6=0;
			 } else
	{%>
	 <td ><input class='text' maxLength='8' name='truckRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
	 <%}%>
		  </tr>    
		  </table>
		  </span>
		  <span id="slabRateAir<%=i%>" style='DISPLAY:none'>
				<table width='101%' cellpadding='0' cellspacing='0' bgcolor='#FFFFFF' border='0'>
			<tr class='formdata' >
				<td  width="13%">&nbsp;</td>
				<td valign="bottom">BASIC
				<input type='hidden' maxLength='8' name='airWeightBreak<%=i%>' size='6' value='FSBASIC'></td>
				<td  valign="bottom">MIN
				<input type='hidden' maxLength='8' name='airWeightBreak<%=i%>' size='6' value='FSMIN'></td>
				<td valign="bottom">KG
				<input type='hidden' maxLength='8' name='airWeightBreak<%=i%>' size='6' value='FSKG'></td>
				<td width="18%">&nbsp;</td>
				<td  valign="bottom">BASIC
				<input type='hidden' maxLength='8' name='airWeightBreak<%=i%>' size='6' value='SSBASIC'></td>
				<td valign="bottom">MIN
				<input type='hidden' maxLength='8' name='airWeightBreak<%=i%>' size='6' value='SSMIN'></td>
				<td  valign="bottom">KG
				<input type='hidden' maxLength='8' name='airWeightBreak<%=i%>' size='6' value='SSKG'></td>
			  </tr>
		 <tr class='formdata' >
		  <td>Fuel&nbsp;Surcharge</td>
	
		 <%if(weightBreaks!=null&&spotRateDetails!=null)
	         {
			 int count5 = 0;
			for(int j=0;j<weightBreaksSize;j++)
			 { 
				  if("FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {count5++;
					%>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("FSBASIC"))[2]%>'></td>
				  <%
				 }
			 }
			  if(count5==0)
			 {
			%>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			  <%}
			
				 count5=0;
				 for(int j=0;j<weightBreaksSize;j++)
			   { 
				  if("FSMIN".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count5++ ;%>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("FSMIN"))[2]%>'></td>
				  <%
				 }
			  }if(count5==0)
					 {
				%>
					  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				 count5=0;
			 	
				for(int j=0;j<weightBreaksSize;j++)
			    { 
				  if("FSKG".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {count5++;
					%>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("FSKG"))[2]%>'></td>
				  <%
				 }
				}if(count5==0)
					 {
				%>
					  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				   count5=0;
			 	 %>
		         <td>Security Surcharge</td>

		  		<%for(int j=0;j<weightBreaksSize;j++)
			   { 
				  if("SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {count5++;
					%>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("SSBASIC"))[2]%>'></td>
				  <%
				 }
			   }
					  if(count5==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				 count5=0;
				  for(int j=0;j<weightBreaksSize;j++)
			    { 
				  if("SSMIN".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {
					count5++;%>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("SSMIN"))[2]%>'></td>
				  <%
				 }
				}	  if(count5==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
				  count5=0;
			    for(int j=0;j<weightBreaksSize;j++)
			    { 
				  if("SSKG".equalsIgnoreCase((String)weightBreaks.get(j)))
				 {count5++;
					%>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=((double[])spotRateDetails.get("SSKG"))[2]%>'></td>
				  <%
				 }
				}  if(count5==0)
					 {
					%>
					  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
					  <%}
					 count5=0;
			 	 
			 } else
	        {%>
			 <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			   <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			   <td>Security&nbsp;Surcharge</td>
			    <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
				 <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
				  <td ><input class='text' maxLength='8' name='airRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='0.0'></td>
			<% }%>
       </tr>    
		  </table>
		  </span>
		  <span id="seaFCL<%=i%>" style='DISPLAY:none'>
			<table width='101%' cellpadding='0' cellspacing='0' bgcolor='#FFFFFF' border='0'>
			<tr class='formdata'>
				<td valign="bottom">B.A.F</td>
				<td width="9%">&nbsp;</td>
<%
			  int temp1=0;
//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
			 HashMap bafCharges = new HashMap();
		   if(weightBreaks!=null)
	          {
		          for(int j=0;j<weightBreaksSize;j++)
				{
					if(((String)weightBreaks.get(j)).endsWith("baf"))
					{
						for(int x=0;x<containerBreak.size();x++)
						{
							if(((String)containerBreak.get(x)).equalsIgnoreCase(((String)weightBreaks.get(j)).substring(0,4)))
							{

								spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
								temp1++;
								 bafCharges.put(x,spotRate[2]);
				
							}
						}%>
<!-- @@Commented by subrahmanyam for the wpbn id:196050 on 29/Jan/10 -->
			
<!-- 
					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='bafRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%="LIST".equalsIgnoreCase(spotRateType)?spotRate[2]:0.0%>'></td>
 -->
 <%				}
				}
				for(int y=0;y<containerBreak.size();y++)
				{
					%>
 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='bafRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=bafCharges.get(y)!=null?bafCharges.get(y):0.0%>'></td>


				<%}

				for(int k=containerBreak.size();k<12;k++)
				{
%>		
					<td align='center' valign="bottom"><input class='text' maxLength='8' name='bafRate<%=i%>' ' value='0.0' size='6' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}
			  }
%>
			</tr>
			<tr class='formdata'>
				<td valign="bottom">C.A.F%</td>
			<td width="9%">&nbsp;</td>
<%
				  int temp2=0;	
//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
			 HashMap cafCharges = new HashMap();
            if(weightBreaks!=null)
	       {
		          for(int j=0;j<weightBreaksSize;j++)
				 {
					if(((String)weightBreaks.get(j)).endsWith("caf"))
					{
						for(int x1=0;x1<containerBreak.size();x1++)
						{
							if(((String)containerBreak.get(x1)).equalsIgnoreCase(((String)weightBreaks.get(j)).substring(0,4)))
							{

									spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
						    	    temp2++;
	 								cafCharges.put(x1,spotRate[2]);
							}
						}%>
				
<!-- Commented by subrahmanyam for the wpbn id:196050 on 29/Jan/10 -->
<!-- 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='cafRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%="LIST".equalsIgnoreCase(spotRateType)?spotRate[2]:0.0%>'></td>
 -->
 <%				}
				}
				for(int y1=0;y1<containerBreak.size();y1++)
				{
					%>
 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='cafRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=cafCharges.get(y1)!=null?cafCharges.get(y1):0.0%>'></td>


				<%}

				for(int k=containerBreak.size();k<12;k++)
				{
%>		
					<td align='center' valign="bottom"><input class='text' maxLength='8' name='cafRate<%=i%>' value='0.0' size='6' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}
		   }
%>
			</tr>
			<tr class='formdata'>
				<td valign="bottom">C.S.F</td>
				<td width="9%">&nbsp;</td>
<%              int temp3=0;
	//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
			 HashMap csfCharges = new HashMap();
          if(weightBreaks!=null)
	         {
			     for(int j=0;j<weightBreaksSize;j++)
				 {
					if(((String)weightBreaks.get(j)).endsWith("csf"))
					{
						for(int x2=0;x2<containerBreak.size();x2++)
						{
							if(((String)containerBreak.get(x2)).equalsIgnoreCase(((String)weightBreaks.get(j)).substring(0,4)))
							{
								spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
								 temp3++;
	 							csfCharges.put(x2,spotRate[2]);
							}
						}%>
				
<!-- 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='cssRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=spotRate[2]%>'></td>
 -->
 <%				 }
				 }
 				for(int y2=0;y2<containerBreak.size();y2++)
				{
					%>
 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='cssRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=csfCharges.get(y2)!=null?csfCharges.get(y2):0.0%>'></td>
				<%}

				for(int k=containerBreak.size();k<12;k++)
				{
%>		
					<td align='center' valign="bottom"><input class='text' maxLength='8' name='cssRate<%=i%>' value='0.0' size='6' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}
			 }
%>
			</tr>
			<tr class='formdata'>
				<td valign="bottom">P.S.S</td>
					<td width="9%">&nbsp;</td>
<%
				 int temp4=0;
	//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
				 HashMap pssCharges = new HashMap();
          if(weightBreaks!=null)
	    {
				for(int j=0;j<weightBreaksSize;j++)
				 {
					if(((String)weightBreaks.get(j)).endsWith("pss"))
					{
						for(int x3=0;x3<containerBreak.size();x3++)
						{
							if(((String)containerBreak.get(x3)).equalsIgnoreCase(((String)weightBreaks.get(j)).substring(0,4)))
							{
									spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
									 temp4++;
	 							pssCharges.put(x3,spotRate[2]);
							}
						}%>
<!-- 		<td align='center'valign="bottom" ><input class='text' maxLength='8' name='pssRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=spotRate[2]%>'></td>
 -->
 <%				   }
				 }
				for(int y3=0;y3<containerBreak.size();y3++)
				{
					%>
 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='pssRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=pssCharges.get(y3)!=null?pssCharges.get(y3):0.0%>'></td>


				<%}
				for(int k=containerBreak.size();k<12;k++)
				{
%>		
					<td align='center' valign="bottom"><input class='text' maxLength='8' name='pssRate<%=i%>' value='0.0' size='6' onKeyPress='return getNumberCode(this)'>
					</td>					
<%				}
		}
%>
			</tr>
	      </table>
	       </span>
    </td>
	</tr>
	</table>

<%
		spotRateDetails		=	null;
		weightBreaks		=	null;
		weightBreaksItr		=	null;
		weightBreaksSize	=	0;
				
		accessList			=	null;
		disabled			=	"";

		isSpotRatesFlag		=	false;
		spotRateType		=	"";
		legShipmentMode		=	"";
		legServiceLevel		=	null;
		legUOM				=	null;
		legDensityRatio		=	null;
		leg					=	"";

		spotRate			=	null;
}
%>

	<table width="101%" cellpadding="4" cellspacing="1" nowrap id="QuoteChargeGroups" idcounter="1" defaultElement="chargeGroupId" xmlTagName="QuoteChargeGroups" bgcolor='#FFFFFF'>
		
		<tr class='formheader'> 
			<td colspan="13"> Applicable Charge Groups</td>
		</tr>

	</table>
		
	<table width="101%" cellpadding="4" cellspacing="1" nowrap id="QuoteContent" idcounter="1" defaultElement="headerFooter" xmlTagName="QuoteContent" bgcolor='#FFFFFF'>

		<tr class='formheader'> 
			<td colspan="13"> Content on Quote (Default Selected if Left Empty) </td>
		</tr>

		<tr class='formheader'> 
			<td></td>
			<td align="center">Header/Footer</td>
			<td align="center">Content</td>
			<td align="center">Level</td>
			<td align="center">Align</td>
			<td align="center"></td>
		</tr>
	
	</table>
		
	<table width="101%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>

		<tr class="text">
			<td  align="left">&nbsp;</td>
			<td  align="right">
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="modifiedDate" value="<%=modifiedDate%>">
				<input type="hidden" name="originLocationName">
				<input type="hidden" name="destLocationName">
				<input type="hidden" name="portName">
				<input type="hidden" name="subOperation" value="MASTER" >
				<input class="input" name="submit" type="submit" value="Next >>" >
				<input type="hidden" name="contactIds" value="<%=custContactIds!=null?custContactIds:""%>"> <!-- //added  by rk -->
			    <input type="hidden" name="checkFlag" value=""> 
					 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
				<input type="hidden" name="quoteName" value="<%= masterDOB.getQuoteId()!=null? masterDOB.getQuoteId():""%>">
				<input type="hidden" name="quoteStatus" value="<%= quoteStatus%>">
					 <!-- @@WPBN issue-167677 -->
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
	//Logger.error(FILE_NAME,"Error in the jsp  :"+e.toString());
  logger.error(FILE_NAME+"Error in the jsp  :"+e.toString());
}
finally
{
	masterDOB			=	null;
	spotRateDetails		=	null;
	weightBreaks		=	null;
	weightBreaksItr		=	null;
}
%>
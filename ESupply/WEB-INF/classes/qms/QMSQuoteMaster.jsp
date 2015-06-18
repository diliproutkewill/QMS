<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteMaster.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		: 8th Aug 2005 	
Date modified		:  
Author    			: S Anil Kumar
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
String					validTo				=	eSupplyDateUtility.getFort1NightDateString(dateFormat);
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
String					locationId			=	"";
String					createdBy			=	"";
String					custDate			=	null;	//Added by Rakesh K on 23-02-2011 for Issue:236359
String					custTime			=	null;	//Added by Rakesh K on 23-02-2011 for Issue:236359


double[]				spotRate			=	null;
ArrayList               containerBreak		= new ArrayList();//@@Added by subrahmanyam for the wpbn id:196050 on 29/Jan/10

String					custContactIds		=	"";

String                  quoteStatus         = null;
String                  completeFlag         = null;
String					cargoAccPlace		="";
String					attentionSlno		= "";
String					customerId			= "";
String					contactNo			= "";
String					emailId				= "";
String					faxNo				= "";
try
{

	quoteStatus = request.getParameter("quoteStatus");
	completeFlag = request.getParameter("completeFlag");

	eSupplyDateUtility.setPattern(dateFormat);
	
	if(session.getAttribute("finalDOB")!=null)
		finalDOB	=	(QuoteFinalDOB)session.getAttribute("finalDOB");

	/*if(session.getAttribute("addressId")!=null)
       addressId    = (String)session.getAttribute("addressId");*/
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
             quoteStatus="ESCALATED";
      else if("NAC".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "NEGATIVE".equalsIgnoreCase(quoteStatus))
           quoteStatus="NEGATIVE";
	}

if(finalDOB!=null && finalDOB.getFlagsDOB()!=null&&finalDOB.getFlagsDOB().getCompleteFlag()!=null)//modified by VLAKSHMI for issue 169959 on 07/05/09
	{
	if("C".equalsIgnoreCase(finalDOB.getFlagsDOB().getCompleteFlag()))
        completeFlag="COMPLETE";
   else if("I".equalsIgnoreCase(finalDOB.getFlagsDOB().getCompleteFlag()))
       completeFlag="INCOMPLETE";
	}

%>
<html>
<head>
<jsp:include page="../etrans/ETDateValidation.jsp">
<jsp:param name="format" value="<%=dateFormat%>"/>
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Quote - <%=operation%></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script language="javascript">
var chargeGroupsArray	=	new Array();
var headerFooterArray	=	new Array();
var contentArray		=	new Array();
var levelArray			=	new Array();
var alignArray			=	new Array();
var spotRateFlags		=	new Array();
var tempZoneId = "";

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
	var disableFlag	= false;
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
			document.getElementById("SaveExit").disabled=false;
		}
		
		if(document.getElementById("spotRateFlag<%=i%>").value=='Y' && '<%=disabled%>'!='disabled')
		{
			disableFlag	= true;
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
		
		if(disableFlag)
		{
			document.forms[0].serviceLevelId.disabled=true;
			document.forms[0].serviceLvlLov.disabled=true;
		}			
		else
		{
			document.forms[0].serviceLevelId.disabled=false;
			document.forms[0].serviceLvlLov.disabled=false;
		}
<%
	}
%>
	
	
}
//@@Added by Phani for the WPBN issue-167678
function setMailValues(values1)
{
  document.getElementById('userModifiedMailIds').length=0
	  var mStrTemp="";
  for(var i=0;i<values1.length;i++)
  {
		if(mStrTemp.length!=0)
		mStrTemp=mStrTemp+","+values1[i];
		else
		mStrTemp	=	values1[i];
	
  }
  document.getElementById('userModifiedMailIds').value =mStrTemp;
}
//@@WPBN issue-167678
function showListLov(shipMode,legNo)
{
	if(document.getElementById("spotRateType"+legNo).value=='LIST')
	{
		if(noOfLegs == 1)
			shipMode = document.forms[0].shipmentMode.value;
		else
		{
			if(shipMode == '1')
				shipMode = "Air";
			else if(shipMode == '2')
				shipMode = "Sea";
			else if(shipMode == '4')
				shipMode = "Truck";
		}
		myUrl= '<%=request.getContextPath()%>/etrans/ListMasterMultipleLOV.jsp?listValue=&shipmentMode='+shipMode+'&legNo='+legNo;
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
		
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
			newWin.opener = self;
		return true;
	}
}

function popUpWindow(input)
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var accessLevel	  =	'<%=loginbean.getAccessType()%>';
	var accsLvl		  = ('HO_TERMINAL' == accessLevel?'H':accessLevel);
	var shipmentMode  = '';
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;

	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
	Options='width=400,height=300,resizable=no';
	
	if(input=='preQuoteId')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id.");
			document.forms[0].customerId.focus();
			return false;
		}
		Url='etrans/QMSQuoteIdsLOV.jsp?customerId='+document.forms[0].customerId.value+'&searchString='+document.forms[0].preQuoteId.value+'&whereToSet='+input;
	}
	else if(input=='customerLov')
	{
		Url='ETAdvancedLOVController?entity=Customer&formfield=customerId&operation=Add&mode=1&type=single&terminalId='+terminalId+'&fromWhere=Quote';
		Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
		Options='width=820,height=600,resizable=no';
	}
	else if(input=='newCustomerLov')
	{
<%
		if(("OPER_TERMINAL").equalsIgnoreCase(loginbean.getAccessType()))
		{
%>
			Url='etrans/ETCustomerRegistrationAdd.jsp?Operation=Add&Customer=NCCS&registrationLevel=T&fromWhat=Quote';
			Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
			Options='width=820,height=700,resizable=no';
<%
		}
		else
		{
%>
			Url='etrans/ETCustomerRegistrationAdd.jsp?Operation=Add&Customer=CCS&registrationLevel=C&fromWhat=Quote';
			Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
			Options='width=820,height=700,resizable=no';
<%
		}
%>
			

	}
	else if(input=='addrLov')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
        Url='etrans/ETCustAddressLOV.jsp?searchString='+document.forms[0].customerId.value+'&flag=Quote&addrType=';
	}
	else if(input=='salesPerson')
	{
		Url='ESACUserIdsLOV.jsp?filterString='+document.forms[0].salesPersonCode.value+'&fromWhat=Quote&accessType=repOfficer&locationId=<%=loginbean.getTerminalId()%>';
	}
	else if(input=='industry')
	{
		Url='etrans/QMSIndustryIdLOV.jsp?Operation=Modify&searchString='+document.forms[0].industryId.value+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>';
	}
	else if(input=='commodity')
	{
		if(document.forms[0].commodityType.selectedIndex==0){
			alert("Please Select Commodity Type");
			document.forms[0].commodityType.focus();
			return false;
		}
	
    var commodityType = document.forms[0].commodityType[document.forms[0].commodityType.selectedIndex].value;
	commodityType     = commodityType.replace('&',';amp') ;
   Url='etrans/ETCLOVCommodityIds.jsp?searchString='+document.forms[0].commodityId.value+'&commodityType='+document.forms[0].commodityType[document.forms[0].commodityType.selectedIndex].value;
	}
	else if(input=='serviceLevel')
	{
		Url='etrans/ETCLOVServiceLevelIds.jsp?searchString='+document.forms[0].serviceLevelId.value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>';
	}
	else if(input=='quotingStation')
	{
		Url='etrans/ETCLOVTerminalIds.jsp?searchString='+document.forms[0].quotingStation.value+'&wheretoset='+input+'&shipmentMode='+document.forms[0].shipmentMode.value;
	}
	else if(input=='originLoc')
	{
		Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].originLoc.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;//added by rk
	}
	else if(input=='destLoc')
	{
		Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].destLoc.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;//added by rk
	}
	else if(input=='shipperZipCode')
	{
		if(document.forms[0].originLoc.value=='')
		{
			alert("Please enter the Origin Location");
			document.forms[0].originLoc.focus();
			return false;
		}
		if(document.forms[0].shipperMode.value.length==0)
		{
			alert("Please Select a Shipper Mode.");
			document.forms[0].shipperMode.focus();
			return false;
		}
		if(document.forms[0].shipperZipCode.value=='')
		{
			alert("Please enter the Shipper Zip Code");
			document.forms[0].shipperZipCode.focus();
			return false;
		}		Url='etrans/QMSLOVZoneCodes.jsp?searchString1='+document.forms[0].originLoc.value+'&searchString2='+document.forms[0].shipperZipCode.value+'&wheretoset=shipperZipCode&shipmentMode='+document.forms[0].shipperMode.value+'&consoleType='+document.forms[0].shipperConsoleType.value;
	}
	else if(input=='consigneeZipCode')
	{
		if(document.forms[0].destLoc.value=='')
		{
			alert("Please enter the destination Location");
			document.forms[0].destLoc.focus();
			return false;
		}
		if(document.forms[0].consigneeMode.value.length==0)
		{
			alert("Please Select a Consignee Mode.");
			document.forms[0].consigneeMode.focus();
			return false;
		}
		if(document.forms[0].consigneeZipCode.value=='')
		{
			alert("Please enter the Consignee Zip Code");
			document.forms[0].consigneeZipCode.focus();
			return false;
		}		Url='etrans/QMSLOVZoneCodes.jsp?searchString1='+document.forms[0].destLoc.value+'&searchString2='+document.forms[0].consigneeZipCode.value+'&wheretoset=consigneeZipCode&shipmentMode='+document.forms[0].consigneeMode.value+'&consoleType='+document.forms[0].consigneeConsoleType.value;
	}
	else if(input=='originPort')
	{
		if(document.forms[0].shipmentMode.value=='Sea')
			Url='etrans/sea/ETSLOVPortIds.jsp?searchStr='+document.forms[0].originPort.value+'&whereToSet='+input+'&fromWhere=Quote';
		else
			Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].originPort.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;
	}
	else if(input=='destPort')
	{
		if(document.forms[0].shipmentMode.value=='Sea')
			Url='etrans/sea/ETSLOVPortIds.jsp?searchStr='+document.forms[0].destPort.value+'&whereToSet='+input;
		else
			Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].destPort.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;
	}
	else if(input.name=='chargeGroupIdBt')
	{
		var Options='scrollbar=yes,width=700,height=300,resizable=yes';
		var originLoc = document.forms[0].originLoc.value;//Added by Anil.k for CR 231214 on 25Jan2011
		var destLoc	  = document.forms[0].destLoc.value;//Added by Anil.k for CR 231214 on 25Jan2011
		btnId = input.id.substring(input.name.length);
		searchString=document.getElementById("chargeGroupId"+btnId).value;
		Url='etrans/QMSLOVChargeGroupIds.jsp?Operation=Modify&searchString='+searchString+'&name=chargeGroupId'+btnId+'&terminalId='+terminalId+'&shipmentMode='+document.forms[0].shipmentMode.value+'&accessLevel='+accsLvl+'&fromWhere=Quote&originLocation='+originLoc+'&destLocation='+destLoc;//Modified by Anil.k for CR 231214 on 25Jan2011
	}
	else if(input.name=='chargeGroupIdDet')
	{
		var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
		btnId = input.id.substring(input.name.length);
		searchString=document.getElementById("chargeGroupId"+btnId).value;
		if(document.getElementById("chargeGroupId"+btnId).value=='')
		{
			alert("Please enter Charge Group");
			document.getElementById("chargeGroupId"+btnId).focus();
			return false;
		}
		Url='etrans/QMSChargesGroupingMaster.jsp?Operation=View&chargeGroupId='+searchString+"&fromWhere=Quote";
		Options='width=810,height=450,resizable=yes';
	}
	else if(input=='newAddrLov')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
		
		Url='etrans/NewAddress.jsp?custId='+document.forms[0].customerId.value;
		Options='width=750,height=400,resizable=no';
		
	}
	else if(input=='newContactPerson')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
		Url='etrans/NewContactDetails.jsp?custId='+document.forms[0].customerId.value;
		Options='width=810,height=450,resizable=no';

	}
	else if(input=='contactLov')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
		
		//commented and modified by phani sekhar for wpbn 167678
	//Url='etrans/ContactLOV.jsp?custId='+document.forms[0].customerId.value+'&flag=Quote&address='+document.forms[0].checkFlag.value;
	//	Options='width=400,height=300,resizable=no';
Url='etrans/AttentionToLOV.jsp?custId='+document.forms[0].customerId.value+'&flag=Quote&address='+document.forms[0].checkFlag.value+'&attentionCustomerId='+document.forms[0].attentionCustomerId.value+'&attentionSlno='+document.forms[0].attentionSlno.value+'&attentionEmailId='+document.forms[0].attentionEmailId.value+'&attentionFaxNo='+document.forms[0].attentionFaxNo.value+'&attentionContactNo='+document.forms[0].attentionContactNo.value;
		Options='width=700,height=400,resizable=yes';
	}
	else if(input=='shipperZone')
	{
		if(document.forms[0].originLoc.value.length==0)
		{
			alert('Please Enter the Location Id');
			document.forms[0].originLoc.focus();
			return false;
		}
		if(document.forms[0].shipperMode.value.length==0)
		{
			alert("Please Select a Shipper Mode.");
			document.forms[0].shipperMode.focus();
			return false;
		}
		var Options='scrollbar=yes,width=700,height=300,resizable=no';
	
		var searchString  = document.forms[0].originLoc.value;
		var searchString2 = document.forms[0].shipperZipCode.value;
		var searchString3 = document.forms[0].shipperZone.value;//Added by RajKumari on 11/10/2008 for WPBN 143511...
		var Url='etrans/QMSLOVZoneCodesMultiple.jsp?searchString1='+searchString+'&searchString2='+searchString2+'&searchString3='+searchString3+'&wheretoset=shipperZone&fromWhere=Quote&shipmentMode='+document.forms[0].shipperMode.value+'&consoleType='+document.forms[0].shipperConsoleType.value;
	}
	else if(input=='consigneeZone')
	{
		if(document.forms[0].destLoc.value.length==0)
		{
			alert('Please Enter the Destination Location Id');
			document.forms[0].destLoc.focus();
			return false;
		}
		if(document.forms[0].consigneeMode.value.length==0)
		{
			alert("Please Select a Consignee Mode.");
			document.forms[0].consigneeMode.focus();
			return false;
		}
		var Options='scrollbar=yes,width=700,height=300,resizable=no';
	
		var searchString  = document.forms[0].destLoc.value;
		var searchString2 = document.forms[0].consigneeZipCode.value;
		var searchString3 = document.forms[0].consigneeZone.value;//Added by RajKumari on 11/10/2008 for WPBN 143511...
		var Url='etrans/QMSLOVZoneCodesMultiple.jsp?searchString1='+searchString+'&searchString2='+searchString2+'&searchString3='+searchString3+'&wheretoset=consigneeZone&fromWhere=Quote&shipmentMode='+document.forms[0].consigneeMode.value+'&consoleType='+document.forms[0].consigneeConsoleType.value;
	}
	else if(input.name=='contentLOV')
	{
		btnId = input.id.substring(input.name.length);
		var searchString=document.getElementById("content"+btnId).value;
		var shipMode	= '';
		var shipModeStr = '';

		if(document.forms[0].shipmentMode.value=='Air')
		{
			shipMode = '1';
			shipModeStr='1,3,5,7';
		}
		else if(document.forms[0].shipmentMode.value=='Sea')
		{
			shipMode = '2';
			shipModeStr='2,3,6,7';
		}
		else
		{
			shipMode = '4';
			shipModeStr='4,5,6,7';
		}
		//searchString  = document.forms[0].content.value;
		Url='QMSLOVContentIds.jsp?opeartion=QUOTE&searchString='+searchString+'&shipmentMode='+shipMode+'&shipModeStr='+shipModeStr+'&whereToSet=content'+btnId;
	}
	
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}

//@@Added by kameswari for the WPBN issue 30908
function pop(index)
{
	  
	   var currency=document.getElementById("currencyId"+index).value;
	 	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+currency+'&currencyId=currencyId'+index+'&fromWhere=quote&Operation=Add';
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;

		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;
       
}
//@@WPBN-30908
function extend(obj)
{
	//alert(dtCheck(obj));
	
var operation = '<%=operation%>';//added by silpa.p on 6-06-11

if(operation!='Modify'){
	
	if(dtCheck(obj)){
	var date = document.forms[0].effDate.value;
	var dateValue = date.split("-");	

	var d = new Date(dateValue[2],dateValue[1]-1,dateValue[0]);
    d.setDate(d.getDate()+29);
	//added by silpa on 28-04-2011
	var d1=d.getDate();
	var date1= (d1 < 10) ? '0' + d1 : d1;
	var m =d.getMonth()+1;
var month = (m < 10) ? '0' + m : m;
var validDate = date1+"-"+(month)+"-"+d.getYear();
//ended
//Added by silpa.p on 6-06-11 for valid date change
if(validDate.length>0){
	  var confirmV = confirm(" Do you Want To Change The Valid To Date Default 30 days?");
	    if(confirmV){
	document.forms[0].validTo.value = validDate;	
		}
		else{

document.forms[0].validTo.value = "";	
}
}//ended
	
	}
	else{
		document.forms[0].validTo.value = "";	
		document.forms[0].effDate.value = "";
	}
}
}//Ended by silpa on 01mar11

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
	

function showDensityLOV(index,shipMode)
{
		//alert(shipMode);
	if(noOfLegs=='1')
		shipmentMode = document.forms[0].shipmentMode.value;
	else if(shipMode==1)
		shipmentMode	= 'Air';
	else if(shipMode==2)
		shipmentMode	= 'Sea';
	else
		shipmentMode	= 'Truck';

	var searchString= document.getElementById("densityRatio"+index).value;
	Url	 =  'etrans/QMSDensityRatioLOV.jsp?searchString='+searchString+'&name=densityRatio'+index+'&shipmentMode='+shipmentMode+'&fromWhere=BR&uom='+document.getElementById("uom"+index).value;
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Options='width=400,height=300,resizable=no';
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features);
}

function showLegServiceLevel(index,shipMode)
{
	if(noOfLegs=='1')
		shipmentMode = document.forms[0].shipmentMode.value;
	else if(shipMode==1)
		shipmentMode	= 'Air';
	else if(shipMode==2)
		shipmentMode	= 'Sea';
	else
		shipmentMode	= 'Truck';
	
	var searchString= document.getElementById("serviceLevel"+index).value;

	Url='etrans/ETCLOVServiceLevelIds.jsp?searchString='+searchString+'&shipmentMode='+shipmentMode+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&wheretoset=serviceLevel'+index;
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Options='width=400,height=300,resizable=no';
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features);
}

function initialize()
{
	importXML('<%=request.getContextPath()%>/xml/QMSQuote.xml');
}

function initializeDynamicContentRows()
{
	initializeDynamicChargeDetails();
	initializeDynamicContentDetails();
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

function changeToUpper(field)
{
	//alert(field.name);
	field.value = field.value.toUpperCase();
}
//Added by Mohan for Issue No.219976 on 28-10-2010
function getDotNumberCode(input)    // Numbers + Dot
{
		if(event.keyCode!=13)
		{	
			if(event.keyCode == 46 )
			{
				if(input.value.indexOf(".") == -1)
					return true;
				else
				return false;
			}
		 if((event.keyCode < 46 || event.keyCode==47 || event.keyCode > 57) )
		   return false;	
		  else
		  {
			var index = input.value.indexOf(".");
			if( index != -1 )
			{
				if(input.value.length == index+3)
				return false;
			}
		  }
		}
		return true;	
}

function getNumberCode(input)
{
	if(event.keyCode!=13)
	{
		if(input.value.lastIndexOf(".")>-1)
		{
			if(event.keyCode < 48  || event.keyCode > 57)
				return false;
		}
		else
		{
			if((event.keyCode < 48 && event.keyCode!=46)  || event.keyCode > 57)
				return false;
		}
	}
	return true;
}
function getNumberCodeNegative(input)
{
	var res = true;//Modified by Mohan for Issue No.219976 on 28-10-2010
	if(event.keyCode!=13)
	{
		if(input.value.substring("-").length>0)
		{
			//if(event.keyCode < 48 || event.keyCode > 57)
			//	return false;
				res =getDotNumberCode(input);
		}
		else
		{
			if((event.keyCode < 48 && event.keyCode!=45) || event.keyCode > 57)
				//return false;
				res = false;
		}
	}
	
	return res;
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
function validateZipCode(input)
{
	input.value = input.value.toUpperCase();

	if(isNaN(input.value))
	{
		/*if(input.value.indexOf("-")==-1)
		{
			alert("Please Use a Hyphen (-) Between Alpha and Numeric Part of the Zip Code");
			input.focus();
			return false;
		}*/
	}
}

function chr1(input)
{
	s = input.value;
	filteredValues = "''~!@#$%^&*()+=|\:;<>./?";
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
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i] == input )
			{
				//document.forms[0].elements[i].focus();
				break;
			}
		}
	}

	input.value = returnString;
}

function chr(input)
{
	s = input.value;
	filteredValues = "''~!@#$%^&*()+=|\:;<>./?";
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

function showListValues(obj,legNo)
{
	if(obj!=null)
	{
		for(var i=0;i<document.getElementsByName("listWeightBreak"+legNo).length;i++)
			document.getElementsByName("listWeightBreak"+legNo)[i].value = '';
		for(var i=0;i<obj.length;i++)
		{
			document.getElementsByName("listWeightBreak"+legNo)[i].value = obj[i].value;
		}
	}
}

function validate()
{
	if(document.forms[0].effDate.value=='')
	{
		alert("Please enter The Effective Date");
		document.forms[0].effDate.focus();
		return false;
	}
	else
	{
		if(!chkFutureDate(document.forms[0].effDate,"gt"))
		{
			alert("Please Enter the Effective Date Greater than or equal to the Current Date.");
			document.forms[0].effDate.focus();
			return false;
		}
	}
	if(document.forms[0].validTo.value!='')
	{
		if(!lessDate(document.forms[0].effDate.value,'',document.forms[0].validTo.value,''))
		{
			alert("Please Enter the Valid to Date Greater than the Effective Date.");
			document.forms[0].validTo.focus();
			return false;
		}
	}
/*if(document.forms[0].validTo.value =='')
	{
		alert("Please enter The validTo Date");
		document.forms[0].validTo.focus();
		return false;
	}*/ //modified by silpa.p on 6-06-11 for vlaid to date empty




	if(document.forms[0].accValidityPeriod.value!='')
	{
		if(parseInt(document.forms[0].accValidityPeriod.value)<=0 )
		{
			alert("Please Enter the Validity period greater than zero.");
			document.forms[0].accValidityPeriod.focus();
			return false;
		}
	}
	if(document.forms[0].customerId.value=='')
	{
		alert("Please enter the Customer Id.");
		document.forms[0].customerId.focus();
		return false;
	}
	if(document.forms[0].salesPersonCode.value=='')
	{
		alert("Please enter the Sales Person Code.");
		document.forms[0].salesPersonCode.focus();
		return false;
	}
	if(document.forms[0].cargoAccPlace.value=='')
	{
		alert("Please enter the place of Cargo Acceptance.");
		document.forms[0].cargoAccPlace.focus();
		return false;
	}
	if(document.forms[0].quotingStation.value=='')
	{
		alert("Please enter The Quoting Station.");
		document.forms[0].quotingStation.focus();
		return false;
	}
	if(document.forms[0].originLoc.value=='')
	{
		alert("Please enter the Origin location.");
		document.forms[0].originLoc.focus();
		return false;
	}
	if(document.forms[0].destLoc.value=='')
	{
		alert("Please enter destination location.");
		document.forms[0].destLoc.focus();
		return false;
	}
	if(document.forms[0].originLoc.value==document.forms[0].destLoc.value)
	{
		alert("Please Enter Different Origin & Destination Locations.");
		document.forms[0].destLoc.focus();
		return false;
	}
	//if(document.forms[0].cargoAcceptance.options.value=='ZIPCODE' && )
	if(document.forms[0].originPort.value.length==0)
	{
		alert('Please Enter the Origin Port');
		document.forms[0].originPort.focus();
		return false;
	}
	if(document.forms[0].destPort.value.length==0)
	{
		alert('Please Enter the Destination Port');
		document.forms[0].destPort.focus();
		return false;
	}
	if(document.forms[0].shipperZipCode.value.length!=0)
	{
		if(document.forms[0].shipperZipCode.value.indexOf("-")!=-1)
		{
			if(document.forms[0].shipperZipCode.value.substring((document.forms[0].shipperZipCode.value.indexOf("-")+1),document.forms[0].shipperZipCode.value.length).length==0)
			{
				alert('Please Enter the Numeric Part of Shipper Zip Code');
				document.forms[0].shipperZipCode.focus();
				return false;
			}
		}
		//@@Added by kameswari for the WPBN issue-
		else
        {
		  var shipperZipCode =  document.forms[0].shipperZipCode.value;
//commented by subrahmanyam for issue id: 185101 on 01-oct-09
		  /*if(isNaN(shipperZipCode))
			{
		  		alert('Please Enter the Numeric Part of Shipper Zip Code');
				document.forms[0].shipperZipCode.focus();
				return false;
		  }*/
		  //ended for 185101

		 
		}
		//@@WPBN issue-
	}
	if(document.forms[0].consigneeZipCode.value.length!=0)
	{
		if(document.forms[0].consigneeZipCode.value.indexOf("-")!=-1)
		{
			if(document.forms[0].consigneeZipCode.value.substring((document.forms[0].consigneeZipCode.value.indexOf("-")+1),document.forms[0].consigneeZipCode.value.length).length==0)
			{
				alert('Please Enter the Numeric Part of Consignee Zip Code');
				document.forms[0].consigneeZipCode.focus();
				return false;
			}
		}
			//@@Added by kameswari for the WPBN issue-

			else
           {
				var consigneeZipCode =  document.forms[0].consigneeZipCode.value;
			//commtented by subrahmanyam for 185101 on 01-10-09
				/*
				 if(isNaN(consigneeZipCode))
			  {
		  		alert('Please Enter the Numeric Part of Consignee Zip Code');
				document.forms[0].consigneeZipCode.focus();
				return false;
		     }
			 */
		 //ended for 185101
		 
		   }
		   //@@WPBN issue-

		
	}
	if(document.forms[0].originPort.value==document.forms[0].destPort.value)
	{
		alert("Please Enter Different Origin & Destination Ports.");
		document.forms[0].destPort.focus();
		return false;
	}
	if(document.forms[0].shipperZipCode.value.length!=0 || document.forms[0].shipperZone.value.length!=0)
	{
		if(document.forms[0].shipperMode.value.length==0)
		{
			alert("Please Select a Shipper Mode.");
			document.forms[0].shipperMode.focus();
			return false;
		}
	}
	if(document.forms[0].consigneeZipCode.value.length!=0 || document.forms[0].consigneeZone.value.length!=0)
	{
		if(document.forms[0].consigneeMode.value.length==0)
		{
			alert("Please Select a Consignee Mode.");
			document.forms[0].consigneeMode.focus();
			return false;
		}
	}
	//@@Commented By Yuvraj for DHLQMSPR-8/DHLQMSPR-15
	/*if(document.getElementsByName("chargeGroupId").length==1)
	{
		if(document.getElementsByName("chargeGroupId")[0].value.length==0)
		{
			alert('Please Enter the Charge Group Id');
			document.getElementsByName("chargeGroupId")[0].focus();
			return false;
		}	
	}
	else
	{
		for(var i=0;i<document.getElementsByName("chargeGroupId").length;i++)
		{
			if(document.getElementsByName("chargeGroupId")[i].value.length==0)
			{
				alert('Please Enter the Charge Group Id at Row Number '+(i+1));
				document.getElementsByName("chargeGroupId")[i].focus();
				return false;
			}
		}
	}*///@@Yuvraj
	if(document.forms[0].address.value.length==0)
	{
		alert('Please Select the Customer Address');
		return false;
	}
	if(document.forms[0].contactPersons.value.length==0)
	{
		alert('Please Select the Customer Address');
		return false;
	}
	
	
	
<%
	for(int i=0;i<noOfLegs;i++)
	{
%>
		if(document.getElementById("spotRateFlag<%=i%>").value=='Y')
		{
			if(document.getElementById("serviceLevelType<%=i%>").value.length==0)
			{
				alert("Please Enter the Service Level in Spot Rates for Leg "+<%=(i+1)%>);
				document.getElementById("serviceLevelType<%=i%>").focus();
				return false;
			}


			if(document.getElementById("densityRatio<%=i%>").value.length==0 )//modified by phani sekhar for wpbn 178179  on 20090730

			{
				if(document.forms[0].shipmentMode.value=='Sea' && document.getElementById("spotRateType<%=i%>").value=='LIST')
					return true;
				else
				{
				alert("Please Enter the Density Ratio in Spot Rates for Leg "+<%=(i+1)%>);
				document.getElementById("densityRatio<%=i%>").focus();
				return false;
				}//ends 178179
			}
			
			//@@added by kameswari for the WPBN issue-30908
			if(document.getElementById("currencyId<%=i%>").value.length==0)
			{
				alert("Please Enter the currencyId in Spot Rates for Leg "+<%=(i+1)%>);
				document.getElementById("currencyId<%=i%>").focus();
				return false;
			}
			//@@WPBN-30908
			if(document.getElementById("spotRateType<%=i%>").value=='FLAT')
			{
				var rates	=	document.getElementsByName("flatRate<%=i%>");
				for(j=0;j<rates.length;j++)
				{
					if(rates[j].value=='' || parseFloat(rates[j].value)==0)
					{
						if(j==0)
							alert("Please enter the Minimum Rate of Lane <%=i+1%>.");
						else
							alert("Please enter the Flat Rate of Lane <%=i+1%>.");

						rates[j].focus();
						return false;
					}
				}

			}
			else if(document.getElementById("spotRateType<%=i%>").value=='SLAB')
			{
				var rates			=	document.getElementsByName("slabRate<%=i%>");
				var weightBreaks	=	document.getElementsByName("slabWeightBreak<%=i%>");
				for(j=0;j<weightBreaks.length;j++)
				{
					if(j==0)
					{
						if(rates[j].value=='' || parseFloat(rates[j].value)==0)
						{
							alert("Please enter the Minimum Rate of Lane <%=i+1%>.");
							rates[j].focus();
							return false;
						}
					}
					else if(j==1)
					{
						if(weightBreaks[j].value=='')
						{
							alert("Please enter Negative Value for slab at Column 2 of Lane <%=i+1%>.");
							weightBreaks[j].focus();
							return false;
						}
						else 
						{
							if(parseFloat(weightBreaks[j].value)>=0)
							{
								alert("Please enter Negative Value for slab at Column 2 of Lane <%=i+1%>.");
								weightBreaks[j].focus();
								return false;
							}
							if(rates[j].value=='')
							{
								alert("Please enter the Rate at Column 2 of Lane <%=i+1%>.");
								rates[j].focus();
								return false;
							}
						}
					}
					else if(j==2)
					{
						if(weightBreaks[j].value=='')
						{
							alert("Please enter, equal Positive value of Column 2, for slab at Column 3 of Lane <%=i+1%>.");
							weightBreaks[j].focus();
							return false;
						}
						else
						{
							if(Math.abs(parseFloat(weightBreaks[j].value))!=Math.abs(parseFloat(weightBreaks[j-1].value)))
							{
								alert("Please enter, equal Positive value of Column 2, for slab at Column 3 of Lane <%=i+1%>.");
								weightBreaks[j].focus();
								return false;
							}
							if(rates[j].value=='')
							{
								alert("Please enter the Rate at Column 3 of Lane <%=i+1%>.");
								rates[j].focus();
								return false;
							}
						}
					}
					else
					{						
						if(weightBreaks[j].value!='')
						{
							if(isNaN(Math.abs(parseFloat(weightBreaks[j-1].value))))
							{
								alert("Please enter the Slab at Column "+(j+1)+" and greater than the Slab at Column "+j+" of Lane <%=i+1%>.");
								weightBreaks[j-1].focus();
								return false;
							}
							if(Math.abs(parseFloat(weightBreaks[j].value))<=Math.abs(parseFloat(weightBreaks[j-1].value)))
							{
								alert("Please enter the Slab at Column "+(j+1)+" greater than the Slab at Column "+j+" of Lane <%=i+1%>.");
								weightBreaks[j-1].focus();
								return false;
							}
							if(rates[j].value.length==0)
							{
								alert("Please enter the Rate at Column "+(j+1)+" of Lane <%=i+1%>.");
								rates[j].focus();
								return false;
							}
						}
					}
				}
			}
			else if(document.getElementById("spotRateType<%=i%>").value=='LIST')
			{
				var rates			=	document.getElementsByName("listRate<%=i%>");
				var weightBreaks	=	document.getElementsByName("listWeightBreak<%=i%>");
				var flag			=	false;
				var listType		=	document.getElementById("listType<%=i%>").innerHTML.substring(0,document.getElementById("listType<%=i%>").innerHTML.length-1);
				
				for(j=0;j<weightBreaks.length;j++)
				{
					if(weightBreaks[j].value.length>0)
					{
						flag = true;
						break;
					}
				}
				if(!flag)
				{
					alert("Please Select at least one "+listType+" in Lane <%=i+1%>.");
					return false;
				}
				for(j=0;j<rates.length;j++)
				{
					if((rates[j].value=='' || parseFloat(rates[j].value)==0) && weightBreaks[j].value.length>0)
					{
						alert("Please enter the Rate at Column "+(j+1)+" of Lane <%=i+1%>.");
						rates[j].focus();
						return false;
					}
				}

			}
		}
<%
	}
%>
	var chargesArray			=	new Array();
//@@Commented By Yuvraj for DHLQMSPR-8/DHLQMSPR-15
	/*if(document.getElementsByName("chargeGroupId").length==1)
	{
		if(document.getElementsByName("chargeGroupId")[0].value.length==0)
		{
			alert('Please Enter the Charge Group Id');
			document.getElementsByName("chargeGroupId")[0].focus();
			return false;
		}	
	}
	else
	{*///@@Yuvraj
		for(var i=0;i<document.getElementsByName("chargeGroupId").length;i++)
		{
			/*if(document.getElementsByName("chargeGroupId")[i].value.length==0)
			{
				alert('Please Enter the Charge Group Id at Row Number '+(i+1));
				document.getElementsByName("chargeGroupId")[i].focus();
				return false;
			}*/
			var charge = document.getElementsByName('chargeGroupId')[i].value;
			chargesArray[i] = charge;
		}
	//}
	for(var i=0;i<document.getElementsByName("chargeGroupId").length;i++)
	{
		for(var j=0;j<chargesArray.length;j++)
		{
			if(i==j)
			{
				break;
			}
			else
			{
				if(chargesArray[j]==document.getElementsByName('chargeGroupId')[i].value)
				{
					alert('Please Enter a Unique Charge at Row '+(i+1));
					document.getElementsByName('chargeGroupId')[i].focus();
					return false;
				}
			}
		}
	}

	var keyArray			=	new Array();
	
	for(var i=0;i<document.getElementsByName('content').length;i++)
	{
		if(document.getElementsByName('content')[i].value.length!=0 && document.getElementsByName('level')[i].value.length==0)
		{
			alert('Please Enter the Level at Row '+(i+1));
			document.getElementsByName('level')[i].focus();
			return false;
		}
		var key = document.getElementsByName('headerFooter')[i].options.value+document.getElementsByName('level')[i].value;
		keyArray[i] = key;
		
	}
	for(var i=0;i<document.getElementsByName('content').length;i++)
	{
		for(var j=0;j<keyArray.length;j++)
		{
			if(i==j)
			{
				break;
			}
			else
			{
				if(keyArray[j]==document.getElementsByName('headerFooter')[i].options.value+document.getElementsByName('level')[i].value)
				{
					alert('Please Enter a Unique Value at Row '+(i+1));
					document.getElementsByName('level')[i].focus();
					return false;
				}
			}
		}
		
	}
  for(var i=0;i<document.forms[0].elements.length;i++)
	{
		if(document.forms[0].elements[i].type=='text')
		{
			document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
		}
	}
	return true;
}

function setValues(values)
{
  document.getElementById('contactPersons').length=0
  for(var i=0;i<values.length;i++)
  {
	document.getElementById('contactPersons').options[i] = new Option(values[i],values[i],true,true);
  }
}
function setZoneCodeValues(obj,where)
{
	var temp	=	'';
	var str		=	'';
	var objRegExp =	'';
	document.getElementById(where).value='';
	for(i=0;i<obj.length;i++)
	{
		str	=	obj[i].value.split("[");

		if(temp!='')
			temp = temp+","+str[1].substring(0,str[1].indexOf("]"));
		else
			temp = temp+str[1].substring(0,str[1].indexOf("]"));

		objRegExp =	str[1].substring(0,str[1].indexOf("]"))+",";
		temp= temp.replace(objRegExp,'');
	}
	document.getElementById(where).value=temp;
}
function enableDisableCAP()
{
	if(document.forms[0].cargoAcceptance.options!=null)
	{
	
	if(document.forms[0].cargoAcceptance.options.value!='OTHER')
		document.forms[0].cargoAccPlace.readOnly = true;
	else 
	{
		//document.forms[0].cargoAccPlace.value	 = '';
		document.forms[0].cargoAccPlace.readOnly = false;
	}
		}
}
function populateCAP()//Modified by rk
{
	var operation ='<%=operation%>';
	
	enableDisableCAP();
	var temp		=	'';
	if(document.forms[0].incoTerms.options!=null)
    {
	if(document.forms[0].incoTerms.options.value=='EXW' || document.forms[0].incoTerms.options.value=='FCA' || document.forms[0].incoTerms.options.value=='FAS' ||document.forms[0].incoTerms.options.value=='FOB')
	{
			
		
			
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
						if(document.forms[0].shipperZipCode.disabled ==false ){
						alert('Please Enter Shipper Zip Code');
						document.forms[0].shipperZipCode.focus();
						document.forms[0].cargoAcceptance.options.value='';
						document.forms[0].cargoAccPlace.value = '';
						}else{
									alert(' Zip Code as Cargo Acceptence Place is not possible for MultiZones');
									document.forms[0].cargoAcceptance.options.value='';
									document.forms[0].cargoAccPlace.value = '';
						return false;
					}

						return false;
					}else
			  
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].shipperZipCode.value+" "+document.forms[0].originLocationName.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='ZONECODE')
			{
					if(document.forms[0].shipperZone.value.length==0)
					{
						alert('Please Enter Shipper Zone Code');
						document.forms[0].shipperZone.focus();
						document.forms[0].cargoAcceptance.options.value='';
						document.forms[0].cargoAccPlace.value = '';
						return false;
					}
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].shipperZone.value+" "+document.forms[0].originLocationName.value;
					
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
					if(document.forms[0].consigneeZipCode.value.length==0)
					{
						
						if(document.forms[0].consigneeZipCode .disabled ==false ){
						alert('Please Enter Consignee Zip Code');
						document.forms[0].consigneeZipCode.focus();
						document.forms[0].cargoAcceptance.options.value='';
						document.forms[0].cargoAccPlace.value = '';
						return false;
						}else{
								alert('Zip Code as Cargo Acceptence Place is not possible for MultiZones');
								document.forms[0].cargoAcceptance.options.value='';
								document.forms[0].cargoAccPlace.value = '';
								return false;
						}

					}
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].consigneeZipCode.value+" "+document.forms[0].destLocationName.value;
					document.forms[0].cargoAccPlace.value = temp;
			}
			else if(document.forms[0].cargoAcceptance.options.value=='ZONECODE')
			{
					if(document.forms[0].consigneeZone.value.length==0)
					{
						alert('Please Enter Consignee Zone Code');
						document.forms[0].consigneeZone.focus();
						document.forms[0].cargoAcceptance.options.value='';
						document.forms[0].cargoAccPlace.value = '';
						return false;
					}
					temp = document.forms[0].incoTerms.options.value;
					temp = temp + " "+document.forms[0].consigneeZone.value+" "+document.forms[0].destLocationName.value;
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

function testShipMode(shipMode)
{
	var operation = '<%=operation%>';
	
	if(operation=='Modify' && shipMode != 0)
		document.forms[0].shipmentMode.options.selectedIndex = (shipMode-1);
	else
	{	
		if(noOfLegs=='1')
		{
			var uom			= document.getElementById("uom0");
			var listType	= document.getElementById("listType0");
			uom.length=0;
			if(document.forms[0].shipmentMode.value=='Sea')
			{
				uom[0] = new Option("CBM","CBM");
				uom[1] = new Option("CFT","CFT");
				listType.innerHTML = "Container Types";
			}
			else
			{
				if(document.forms[0].shipmentMode.value=="Air")
					listType.innerHTML = "ULD Types";
				else
					listType.innerHTML = "Container Types";
				
				uom[0] = new Option("Kg","Kg");
				uom[1] = new Option("Lb","Lb");
			}
		}
		else
		{
			for(var i=0;i<noOfLegs;i++)
			{
				var uom			= document.getElementById("uom"+i);
				var listType	= document.getElementById("listType"+i);
				var shipMode	= document.getElementById("legShipmentMode"+i).value;
				uom.length=0;
				if(shipMode=='2')
				{
					uom[0] = new Option("CBM","CBM");
					uom[1] = new Option("CFT","CFT");
					listType.innerHTML = "Container Types";
				}
				else
				{
					if(shipMode=="1")
						listType.innerHTML = "ULD Types";
					else
						listType.innerHTML = "Container Types";

					uom[0] = new Option("Kg","Kg");
					uom[1] = new Option("Lb","Lb");
				}
			}
		}
		return true;
	}
	
}
function clearListValues()
{
	
	if(noOfLegs=='1')
	{
		for(var j=0;j<document.getElementsByName("listWeightBreak0").length;j++)
		{
			document.getElementsByName("listWeightBreak0")[j].value = '';
			document.getElementsByName("listRate0")[j].value = '';
		}
	}
}
function openOrgPortLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	
	if(document.forms[0].shipmentMode.value=="Sea")
	{
		tabArray = 'PORT_ID';
		formArray = 'originPort';
		Url		="qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=PORTS&search= where PORT_ID LIKE '"+document.forms[0].originPort.value+"~'";	
		Options	='width=600,height=750,resizable=0';
		
	}
	else
	{
		tabArray = 'LOCATIONID';
		formArray = 'originPort';
		/*Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].originPort.value+"~'";*/
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].originPort.value+"~'";

		Options	='width=750,height=750,resizable=yes';
	}
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}

function openDestPortLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";

	if(document.forms[0].shipmentMode.value=="Sea")
	{
		tabArray = 'PORT_ID';
		formArray = 'destPort';
		Url		="qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=PORTS&search= where PORT_ID LIKE '"+document.forms[0].destPort.value+"~'";	Options	='width=600,height=750,resizable=yes';	
		
	}
	else
	{
		tabArray = 'LOCATIONID';
		formArray = 'destPort';
		/*Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].destPort.value+"~'";*/
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].destPort.value+"~'";
		Options	='width=750,height=750,resizable=yes';
	}
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}

function openLocationLov(input)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	
	if(input=="Origin")
	{
		tabArray = 'LOCATIONID';
		formArray = 'originLoc';	
		
		/*Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].originLoc.value+"~'";*/
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].originLoc.value+"~'";

	}
	else if(input=="Dest")
	{
		tabArray = 'LOCATIONID';
		formArray = 'destLoc';

		/*Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].destLoc.value+"~'";*/
      Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].destLoc.value+"~'";

		
	}
	
	//alert(document.forms[0].formArray.value);

	//Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].formArray.value+"~'";	


	//Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
  Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';//@@Modified by Kameswari for the internal issue on 23/07/09
	Options	='width=750,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}

function openSalesPersonLov()
{
	var tabArray = 'EMPID';
	var formArray = 'salesPersonCode';
	//var lovWhere  = " where ORG_ID='<%//=sessOrgid%>' and STATUS='ACTIVE'";
	var lovWhere	=	"";
	//alert('<%=request.getContextPath()%>');
	//window.open("qms/ListOfValues.jsp?lovid=REPORTING_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"","lov","top=0,left=0,height=600,width=470,toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=0,minimize=0");

	Url		="qms/ListOfValues.jsp?lovid=REPORTING_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SALESPERSON&search= where EMPID LIKE '"+document.forms[0].salesPersonCode.value+"~'";
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 }
//@@ added by silpa   on 1-mar-11
function getCustAddressByAjax(obj) {
       //alert("hai");
	    var custid = obj.value;
		if(custid != '' )
		{
		 //window.alert("address");
	   var url ='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=CustAdd&customerId='+custid;
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        } 
        req.open("GET",url,true);
        //window.alert("address2");
		req.onreadystatechange = callback;
		req.send(null);
		}else{
          document.forms[0].address.value ='';
		}
	
    }


function callback() {



  
    if (req.readyState == 4) {
        if (req.status == 200) 
		{  
		
			var response = req.responseText.toString();
			var add  = response.split("$");
			var address ="";
			for(i=0;i<add.length;i++){
              if(i!=0)
				address += ((add[i]!='' && add[i]!= "undefined")?add[i]:'');
			    
			}
			
			document.forms[0].address.value = address;
			document.forms[0].addressId.value = add[0];
		
        }
    }
}//@@ added by silpa   on 1-mar-11

function getAttentionByAjax(obj) {
   //alert("ATTENTION");
       var len = document.forms[0].contactPersons.options.length;			
		 for (i=len-1; i>=0; i--) {
				document.forms[0].contactPersons.remove(i);  }
	    var custid = obj.value;
	    var custid = obj.value;
		if(custid != '' )
		{
		   //alert("ATTENTION1");
	   var url ='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=CustAtten1&customerId='+custid;
		 if (window.XMLHttpRequest) 
		{
            req1 = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req1 = new ActiveXObject("Microsoft.XMLHTTP");
        } 
        req1.open("GET",url,true);
       	 //alert("ATTENTION2");
		req1.onreadystatechange = callback1;
		  
		req1.send(null);
		}else{
          var len = document.forms[0].contactPersons.options.length;			
		 for (i=len-1; i>=0; i--) {
				document.forms[0].contactPersons.remove(i);  }
		}
	
    }
    
    
    function callback1() {
    if (req1.readyState == 4) {
        if (req1.status == 200) 
		{
			var response = req1.responseText.toString();
			
			var add1  = response.split(",");
			var attention ="";
			for(i=0;i<add1.length-1;i++){
             var atten  = add1[i].split("$");				
				//if(att[0]!='' && att[0] != "Customer is Invalid Or Does not exist.")
				//document.forms[0].contactPersons.options[i]=new Option(att[i],att[i],true,true);
				if(atten[0]!='' && atten[0] != "Customer is Invalid Or Does not exist."){

				document.forms[0].contactPersons.options[i]=new Option(atten[0],atten[0],true,true);
				
				//Modified for Back Button With Freight
				if(document.forms[0].contactIds.value!="")
					document.forms[0].contactIds.value = document.forms[0].contactIds.value+","+(atten[5]!=""?atten[5]:"-");
				else
					document.forms[0].contactIds.value = atten[5]!=""?atten[5]:"-";

				if(document.forms[0].attentionCustomerId.value!="")
					document.forms[0].attentionCustomerId.value = document.forms[0].attentionCustomerId.value+","+(atten[4]!=""?atten[4]:"-");
				else
					document.forms[0].attentionCustomerId.value = atten[4]!=""?atten[4]:"-";

				if(document.forms[0].attentionSlno.value!="")
					document.forms[0].attentionSlno.value = document.forms[0].attentionSlno.value+","+(atten[5]!=""?atten[5]:"-");
				else
					document.forms[0].attentionSlno.value = atten[5]!=""?atten[5]:"-";

				if(document.forms[0].attentionEmailId.value!="")
					document.forms[0].attentionEmailId.value = document.forms[0].attentionEmailId.value+","+(atten[1]!=""?atten[1]:"-");
				else
					document.forms[0].attentionEmailId.value = atten[1]!=""?atten[1]:"-";

				if(document.forms[0].attentionFaxNo.value!="")
					document.forms[0].attentionFaxNo.value = document.forms[0].attentionFaxNo.value+","+(atten[3]!=""?atten[3]:"-");
				else
					document.forms[0].attentionFaxNo.value = atten[3]!=""?atten[3]:"-";

				if(document.forms[0].attentionContactNo.value!="")
					document.forms[0].attentionContactNo.value = document.forms[0].attentionContactNo.value+","+(atten[2]!=""?atten[2]:"-");
				else
					document.forms[0].attentionContactNo.value = atten[2]!=""?atten[2]:"-";
				//Ended Modification for Back Button With Freight
				}
			}			
			//document.forms[0].address.value = address;
			//document.forms[0].addressId.value = add[0];
		
        }
    }
}//Ended Ajax call for Attention To 
//Added by Rakesh     
  function getSalesPersonByAjax(obj) {
	    var custid = obj.value;
		if(custid != '' ){
	   var url ='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=SalesPerson1&customerId='+custid+'&dummytime='+((new Date()).getTime());;
		 if (window.XMLHttpRequest) 
		{
            req2 = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req2 = new ActiveXObject("Microsoft.XMLHTTP");
        } 
        req2.open("GET",url,true);
		req2.onreadystatechange = callback3;
		req2.send(null);
		}else{
          document.forms[0].salesPersonCode.value ='';
		}
    }
function callback3() {
    if (req2.readyState == 4) {
        if (req2.status == 200) 
		{
			var response = req2.responseText.toString();
			if(response=="Customer is Invalid Or Does not exist.")
			{
			  alert(response);
			}
			else
			{
		   	 if(response.length>0)
				 
				 document.forms[0].salesPersonCode.value=response;
			 else				 
				 document.forms[0].salesPersonCode.value=document.forms[0].createdBy.value;
			}
				
    
        }
    }
}  
    
    
//Ended by Rakesh     
    
    
    
    

function openCustomerLov()
{
	var tabArray = 'CUSTOMERID';
	var formArray = 'customerId';
	var lovWhere	=	"";
	//@@ Modified by subrahmanyam for the pbn id: 210495 on 12-Jul-10
	Url		="qms/ListOfValues.jsp?lovid=CUSTOMER_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTECUSTOMER&search= where CUSTOMERID LIKE '"+document.forms[0].customerId.value+"~' ORDER BY trim(COMPANYNAME)";
	//@@ Ended by subrahmanyam for the pbn id: 210495 on 12-Jul-10
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 }
 function check_Length(maxLength,input)
{
	val = input.value;
	if(val.length > maxLength)
	alert("Character Limit reached("+maxLength+")");
	val = val.substring(0,maxLength);
	
	input.value = val;
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
//Added by Anil.k for CR 231214 on 25Jan2011
function appChrg()
{
	if(document.getElementsByName("chargeGroupId").length==1)
		document.forms[0].chargeGroupId.value="";
	else
	{
		for(var j=0;j<document.getElementsByName("chargeGroupId").length;j++)		
			document.getElementById("chargeGroupId"+(j+1)).value="";		
	}
}//Ended by Anil.k for CR 231214 on 25Jan2011

 //Added by Rakesh for Issue:   
function IsValidTime(field)
	{
		timeStr=field.value
		if(timeStr.length!=0)
		{
	
			if((timeStr.length==5 && timeStr.indexOf(':')==-1)  || (timeStr.length==6 && timeStr.indexOf(':') !=-1))
				var	timePat	= /^(\d{1,3}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;	
			else
			var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
			
				
			if((timeStr.length==4 && timeStr.indexOf(':')==-1) || (timeStr.length==5 && timeStr.indexOf(':')!=-1))
			{
				if(timeStr.length==4 && timeStr.indexOf(':')==-1)
				{
					timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
					field.value=timeStr;

				}
			}
			else if((timeStr.length==5 && timeStr.indexOf(':')==-1) || (timeStr.length==6 && timeStr.indexOf(':')!=-1))
			{
				if(timeStr.length==5 && timeStr.indexOf(':')==-1)
				{
					timeStr = timeStr.substring(0,3)+':'+timeStr.substring(3,5);
					field.value=timeStr;

				}
			}
			var	matchArray = timeStr.match(timePat);
			if (matchArray == null)
			{
				alert("Please enter in HH:MM format");
				field.value="";
				field.focus();
			}
			else
			{
				hour = matchArray[1];
				minute = matchArray[2];
				second = matchArray[4];
				ampm = matchArray[6];
				if (second=="")	{ second = null; }
				if (ampm=="") {	ampm = null	}
				if (hour < 0  || hour >	23)	
        {
          alert("Please enter correct Hours");
		  field.value="";
          field.focus();
				}
				else if	(minute<0 || minute	> 59) {
					alert ("Please enter correct Minutes.");
					field.value="";
					field.focus();
				}
			}
		}
	}
 //Ended by Rakesh for Issue:   



function checkAjaxIsValidZoneCode(zoneName,id,locationId)
	{
	
	var zoneValue = document.getElementById(id).value ;
	tempZoneId=id;
	//alert("kishore -->  "+id);
	var rownum = (zoneName=='shipper')?id.substring(11):id.substring(13);
  
	//alert(zoneValue.indexOf(','));
	
		if(zoneValue.indexOf(',')!=-1)
				disableZipElement(zoneName);
		else
				enableZipElement(zoneName);	
    
	if(zoneName !='' && document.getElementById(id).value != '' && document.getElementById(locationId).value!='')
		{
           var url='';
		url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=IsValidZoneCode&zoneCode='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&location='+document.getElementById(locationId).value+'&weightBreak='+document.forms[0].WeightBreak.value;  //+'&dummytime='+((new Date()).getTime());
		 
		 //alert(url);
		 
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }

        req.open("GET",url,true);
		req.onreadystatechange = callbackIsValidZoneCode;
		req.send(null); 
		
		}else{
          document.getElementById(id).value ='';
		} 
}
	function callbackIsValidZoneCode()
	{
       if (req.readyState == 4) {
        if (req.status == 200) 
		{	  
		var response = req.responseText.toString();	
		if(response.length>0){
			alert(response)
				 document.getElementById(tempZoneId).value = "";
				 document.getElementById(tempZoneId).focus();
			
		}
		}
	   }
	}

	function disableZipElement(zoneName){
	document.forms[0].cargoAcceptance.options.value='';
	document.forms[0].cargoAccPlace.value = '';
	//alert("Hi Kishore1");
	document.getElementById(zoneName+"ZipCode").value="";
	document.getElementById(zoneName+"ZipCode").readOnly=true;
	document.getElementById(zoneName+"ZipCodeLOV").disabled=true;
	//alert("Hi Kishore2");
	}

	function enableZipElement(zoneName){
	//alert("Hi Kishore3");
	document.forms[0].cargoAcceptance.options.value='';
	document.forms[0].cargoAccPlace.value = '';
	document.getElementById(zoneName+"ZipCode").readOnly=false;
	document.getElementById(zoneName+"ZipCodeLOV").disabled=false;
	//alert("Hi Kishore4");
	}


</script>

</head>

<body onLoad="initialize();displaySpans();populateCAP();testShipMode(0);changeConsoleVisibility('shipper');changeConsoleVisibility('consignee');salesPersonFlag()">
<form  method="post" name="QuoteMaster" action="QMSQuoteController" onSubmit="return validate()">
	<table width="101%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>
	 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
		<tr class="formlabel">

		<%if("Modify".equalsIgnoreCase(operation)) {%>
			<td >QUOTE(<%=masterDOB.getQuoteId()%>,<%=quoteStatus%>,<%=completeFlag%>) - <%=operation!=null?operation.toUpperCase():""%></td><td align='right'>QS1060201</td>

			<%}else { %>
			<td >QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align='right'>QS1060201</td>
			<%}%>
		</tr>
			 <!-- @@WPBN issue-167677 -->
		</table>
		<table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
<%
		StringBuffer	errors	=	(StringBuffer)request.getAttribute("errors");
		if(errors!=null)
		{
			String	errorMessages	=	errors.toString();
%>
			<tr color="#FFFFFF">
				<td colspan="8">
					<font face="Verdana" size="2" color='red'><b>The form has not been submitted because of the following error(s):</b><br><br>
					<%=errorMessages%></font>
				</td>
			</tr>
<%		
		}
		if(request.getAttribute("error")!=null)
		{
%>
				<tr color="#FFFFFF">
					<td colspan="8"><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error : </b> <br><br>
					<%=(String)request.getAttribute("error")%></font></td>
				</tr>
<%
		}
%>
		<tr class="formdata">
			<td  width='15%' valign="top">Quote&nbsp;Type:&nbsp;<font color="#ff0000">*</font><br>
				
				<select name="shipmentMode" size="1" class="select" onchange='testShipMode("<%=masterDOB.getShipmentMode()%>");clearListValues();displaySpans()'>
					<option value="Air"   <%=masterDOB.getShipmentMode()==1?"selected":""%>>Air</option>
					<option value="Sea"   <%=masterDOB.getShipmentMode()==2?"selected":""%>>Sea</option>
					<option value="Truck" <%=masterDOB.getShipmentMode()==4?"selected":""%>>Truck</option>
				</select>
			</td>
		 <!-- Added By Kishore For Weight Break in Single Quote  -->
			<%
				 System.out.println(masterDOB.getWeightBreak()+"------- Kish  -----"+operation);	
			
			%>
			<td  width='15%' valign="top" nowrap>Weight Break<br>	
				<select name="WeightBreak" size="1" class="select" >
							<option value="Flat"   <%=masterDOB.getWeightBreak()!=null?masterDOB.getWeightBreak().equalsIgnoreCase("Flat")?"selected":"":""%> >Flat</option>
							<option value="Slab" <%=masterDOB.getWeightBreak()!=null?masterDOB.getWeightBreak().equalsIgnoreCase("Slab")?"selected":"":""%> >Slab</option>
							<option value="List"   <%=masterDOB.getWeightBreak()!=null?masterDOB.getWeightBreak().equalsIgnoreCase("List")?"selected":"":""%>>List</option>
							<%if ( (null == masterDOB.getWeightBreak() || "".equals(masterDOB.getWeightBreak())) &&( "Modify".equalsIgnoreCase(operation) ||  "view".equalsIgnoreCase(operation)) ) {%>
							   <option value="" selected > </option>				
							 <%}%>
				 </select>
			 </td>
			 <!-- End Of  Kishore For Weight Break in Single Quote  -->

			<td align='center' width='3%' valign="top">  Flag<br>
				<select name="impFlag" size="1" class="select">
					<option value="U" <%=!masterDOB.isImpFlag()?"selected":""%>></option>
					<option value="I" <%=masterDOB.isImpFlag()?"selected":""%>>Imp</option>
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
				<input class="text" maxLength="20" name="preQuoteId"  size="15" onBlur="changeToUpper(this);" onKeyPress="return getNumberCode(this)" value="<%if(masterDOB.getPreQuoteId()!=null&& masterDOB.getPreQuoteId().length()>1){%><%=masterDOB.getPreQuoteId()%><%}%>" readOnly>
				<!-- in the above line masterDOB.getPreQuoteId()!=0  replaced with masterDOB.getPreQuoteId()!=null by subrahmanyam for the enhancement #146971 on 03/12/08-->
				<!--input class="input" type="button" value="..." onclick="popUpWindow('preQuoteId')" <%//=disabledField%>-->

			</td>
			<td width ='15%' valign="top">Created&nbsp;Date:<br>
				<input class="text" maxLength="25" name="createdDate"  size="10" value="<%=createdDate!=null?createdDate:currentDate%>" onBlur="changeToUpper(this);return dtCheck(this)" readonly>
				<!-- <input class="input" type="button" value="..." onclick="newWindow('createdDate','0','0','')" name="createdDateBut"> -->
			</td>
			<td width ='15%' valign="top">Eff&nbsp;Date:<font color="#ff0000">*</font><br>
				<input class="text" maxLength="25" name="effDate"  size="10" value="<%=effDate!=null?effDate:currentDate%>" onBlur="extend(this)">
				<input class="input" type="button" value="..." onclick="newWindow('effDate','0','0','')">
			</td>
			<td width ='15%' valign="top">Valid To:<br>
				<input class="text" maxLength="25" name="validTo"  size="10" value="<%=validTo!=null?validTo:""%>" onBlur="changeToUpper(this);return dtCheck(this)">
				<input class="input" type="button" value="..." onclick="newWindow('validTo','0','0','')">
			</td>
			<td width ='25%' valign="top">
				Acceptance Validity:<br><!--Acceptance Validity Period:-->
				<input class="text" maxLength="16" name="accValidityPeriod"  size="12" onKeyPress="return getNumberCode(this)" value="<%if(masterDOB.getAccValidityPeriod()!=0){%><%=masterDOB.getAccValidityPeriod()%><%}%>">
			</td>
			<%
			if("Copy".equalsIgnoreCase(operation))
			{
				createdBy	=	loginbean.getUserId();
			}
			else
			{
				createdBy	=	masterDOB.getCreatedBy()!=null?masterDOB.getCreatedBy():loginbean.getUserId();
			}
			
%>
			<!--@@Modified by Kameswari for the WPBN issue-6318-->
			<td width ='17%' valign="top">Created By:<br>
				<input class="text" maxLength="25" name="createdBy"  size='10'  value="<%=createdBy%>" readOnly>
			</td>
			<!--@@WPBN issue-61318-->
		</tr>
        </table>
	   <table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata" >
			<td  width ='20%' rowspan='1' valign='top'>Customer&nbsp;Id :&nbsp;<font color="#ff0000">*</font>
				<br>
				<!--added by silpa on 1-03-11-->
				<input class="text" maxLength="25" name="customerId"  size="12" value="<%=masterDOB.getCustomerId()!=null?masterDOB.getCustomerId():""%>" row="0" onBlur="changeToUpper(this);chrnum(this);getCustAddressByAjax(this);getAttentionByAjax(this);getSalesPersonByAjax(this);" <%=readOnly%>>	 <!--  Modified by Rakesh -->
				<!-- <input class="input" name="custLovBut"  type="button" value="..." row="0" onclick="popUpWindow('customerLov')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>> -->
				<input class="input" name="custLovBut"  type="button" value="..." row="0" onclick="openCustomerLov()" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
				<input class="input" type="button" value="NEW" row="0" onclick="popUpWindow('newCustomerLov')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>&nbsp;<br>
				<!-- Added by Rakesh K on 23-02-2011 for Issue:236359-->
				
			</td>	<!-- Ended by Rakesh for Issue:        on 28-02-2011 -->
			</td>
			<td width ='20%' rowspan='1' valign='top'>
				<p align="left">Address:
				<input class="input" type="button" value="..." name="addrLovBut" onclick="popUpWindow('addrLov')">&nbsp;
				<input class="input" type="button" value="NEW" row="0" onclick="popUpWindow('newAddrLov')"><br>
				<textarea class="text" cols="30" rows="4" name="address" onBlur="changeToUpper(this);" readOnly><%=masterDOB.getCustomerAddress()!=null?masterDOB.getCustomerAddress():""%></textarea>
				<input type="hidden" name="addressId" value='<%="0".equalsIgnoreCase(addressId)?"":addressId%>'>
				</p>
			</td>
			<td width ='20%' rowspan='1' valign='top'>
			<p align="left">Attention To:
			<input class="input" type="button" value="..." name="contactPersonLov" onclick="popUpWindow('contactLov')">&nbsp;
				<input class="input" type="button" value="NEW" row="0" onclick="popUpWindow('newContactPerson')">
				<select class="select"  name="contactPersons" size="4" multiple style="width:200px;margin:0px 0 5px 0;">
<%				
				if(masterDOB.getCustomerContacts()!=null && masterDOB.getCustomerContacts().length>0)
				{
				  for(int i=0;i<masterDOB.getCustomerContacts().length;i++)
				  {
					  System.out.println(masterDOB.getCustomerContactsEmailIds()+"&&&&&&&&&&&&&");
					  //Logger.info(FILE_NAME,"masterDOB.getCustomerContacts()::"+masterDOB.getCustomerContacts()[i-1]);
					  //custContactIds = custContactIds+masterDOB.getCustomerContacts()[i-1]+",";
					  //Added to get Attention To values in Modify Case
						if(attentionSlno == "")
							attentionSlno= masterDOB.getCustomerContacts()[i];
						else
							attentionSlno= attentionSlno+","+masterDOB.getCustomerContacts()[i];
						if(customerId!="")
							customerId = customerId+","+masterDOB.getCustomerId();
						else
							customerId = masterDOB.getCustomerId();	
						if(i>0)
							contactNo = contactNo+","+(masterDOB.getCustomerContactNo()!=null?(masterDOB.getCustomerContactNo()[i]!=null?masterDOB.getCustomerContactNo()[i]:""):"");
						else
							contactNo =masterDOB.getCustomerContactNo()!=null?(masterDOB.getCustomerContactNo()[i]!=null?masterDOB.getCustomerContactNo()[i]:""):"";
						if(i>0)
							emailId = emailId+","+(masterDOB.getCustomerContactsEmailIds()!=null?(masterDOB.getCustomerContactsEmailIds()[i]!=null?masterDOB.getCustomerContactsEmailIds()[i]:""):"");
						else
							emailId = masterDOB.getCustomerContactsEmailIds()!=null?(masterDOB.getCustomerContactsEmailIds()[i]!=null?masterDOB.getCustomerContactsEmailIds()[i]:""):"";
						if(i>0)
							faxNo = faxNo+","+(masterDOB.getCustomerContactsFax()!=null?(masterDOB.getCustomerContactsFax()[i]!=null?masterDOB.getCustomerContactsFax()[i]:""):"");
						else
							faxNo = masterDOB.getCustomerContactsFax()!=null?(masterDOB.getCustomerContactsFax()[i]!=null?masterDOB.getCustomerContactsFax()[i]:""):"";
						System.out.println(emailId+"@@@"+contactNo+"###"+faxNo);
						//Ended to get Attention To values in Modify Case
%>
				<option value='<%=masterDOB.getCustContactNames()[i]%>'  selected><%=masterDOB.getCustContactNames()[i]%></option>   <!-- Modified by Rakesh for Issue:        on 03-01-2010 -->
<%
				  }
				}
%>
				</select><br>
				
			</td>
			<!--@@Commented by Kameswari for the WPBN issue-61318-->
			<!--<td width ='20%'>Created By:<br>
				<input class="text" maxLength="25" name="createdBy"  size="16" value="<%=createdBy%>" readOnly>
			</td>-->
			<!--@@WPBN issue-61318-->
			<td  width ='16%' valign='top'>Sales Person:<font color="#ff0000">*</font><br>
				<input class="text" maxLength="25" name="salesPersonCode"  size="14" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getSalesPersonCode()!=null?masterDOB.getSalesPersonCode():""%>"><!--<br>-->&nbsp;<input class="input" type="button" value="..." name="salesPerLov" onclick="openSalesPersonLov()"><br>
				<!-- onclick="popUpWindow('salesPerson')" -->
			</td>

          <!--
		  <td width ='20%'>Created By:<br>
				<input class="text" maxLength="25" name="createdBy"  size="16" value="<%=createdBy%>" readOnly>
		</td>-->
			<!--@@Added by kameswari for WPBN issue-61306-->
			<td width ='15%' valign='top'>
				Email&nbsp;Copy&nbsp;to<br>Sales&nbsp;Person<input type='checkbox' name='salesPersonFlag' value='0'>
				</td>
					<!--@@WPBN issue-61306-->
			
    	</tr>
		</table>
		<table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata"> <!-- Added by Rakesh for Issue:        on 28-02-2011 -->
		<td width='28%'>
			Cust Req Date 
				<%if("Add".equalsIgnoreCase(operation)){%>
				<input class="text" maxLength="25" name="custDate"  size="10" value="<%=currentDate%>" onBlur="changeToUpper(this);return dtCheck(this)" >
				<%}else{%>
				<input class="text" maxLength="25" name="custDate"  size="10" value="<%=custDate!=null?custDate:""%>" onBlur="changeToUpper(this);return dtCheck(this)" >
				<%}%>
				<input class="input" type="button" value="..." onclick="newWindow('custDate','0','0','')">
				<br>
		</td>
		    <td  width="25%" > <!-- -**Cust Req Time:<br>onBlur="chrnum(this)" -->
				<!-- <input class="text" name="custTime"  onblur='return IsValidTime(this)'  maxlength="6" size="10"    value=""> --><!--size='26'-->
				<!-- Ended by Rakesh K on 23-02-2011 for Issue:236359-->
				Cust Req Time: <!-- onBlur="chrnum(this)" -->
				<input class="text" name="custTime"  onblur='return IsValidTime(this)'  maxlength="6" size="10"    value="<%=custTime!=null?custTime:""%>"><!--size='26'-->
			</td>	<!-- Ended by Rakesh for Issue:        on 28-02-2011 -->
			<td  width="47%" colspan='1' valign="top">Cargo Notes: <!-- onBlur="chrnum(this)" -->
				<input class="text" name="overLengthCargoNotes"  maxlength="100"  size='35'               value="<%=masterDOB.getOverLengthCargoNotes()!=null?masterDOB.getOverLengthCargoNotes():""%>"><!--size='26'-->
			</td>
		</tr>
		</table>
	   <table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
		<!--@@Commented by Kameswari for the WPBN issue-61318-->
		<!--
		<td width ='8%'>Incoterms:<font color="#ff0000">*</font><br><br>
				<select class="select"  name="incoTerms" size="1" onchange='populateCAP()'>
					<option value="EXW" <%="EXW".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>EXW</option>
					<option value="FAS" <%="FAS".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>FAS</option>
					<option value="FCA" <%="FCA".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>FCA</option>
					<option value="FOB" <%="FOB".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>FOB</option>
					<option value="CFR" <%="CFR".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CFR</option>
					<option value="CIF" <%="CIF".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CIF</option>
					<option value="CPT" <%="CPT".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CPT</option>
					<option value="CIP" <%="CIP".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CIP</option>
					<option value="DDP" <%="DDP".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DDP</option>
					<option value="DDU" <%="DDU".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DDU</option>
					<option value="DDU CC" <%="DDU CC".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DDU CC</option>
					<option value="DAF" <%="DAF".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DAF</option>
					<option value="DES" <%="DES".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DES</option>
					<option value="DEQ" <%="DEQ".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DEQ</option>
				</select>
			</td>
			<td width ='19%'>Cargo Acceptance Place:<font color="#ff0000">*</font><br>
				<select class='select' name='cargoAcceptance' onchange='populateCAP()' style="width:118px;margin:0px 0 5px 0;">
					<option value=''></option>
					<option value='ZIPCODE' <%="ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Zip Code</option>
					<option value='ZONECODE' <%="ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Zone Code</option>
					<option value='DDAO' <%="DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>DGF Terminal</option>
					<option value='PORT' <%="PORT".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Port</option>
					<option value='OTHER' <%="OTHER".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Other</option>
				</select><br>
				<textarea name='cargoAccPlace' class="text" onBlur="return check_Length(255,this)"  cols="16" rows='4' style="height:30" cols="25" rows='5'><%=masterDOB.getCargoAccPlace()!=null?masterDOB.getCargoAccPlace():""%></textarea>
			</td>-->
			<!--@@WPBN issue-61318-->
			<td width ='8%' valign="top">Service Level:<!--Service Level-->
				<input class="text" maxLength="16" name="serviceLevelId"  size="4" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getServiceLevelId()!=null?masterDOB.getServiceLevelId():""%>">&nbsp;<input class="input" type="button" name="serviceLvlLov" value="..." onclick="popUpWindow('serviceLevel')">
			</td>
			<td  width ='11%' valign="bottom">Industry:<br><!--<br>-->
				<input class="text" maxLength="16" name="industryId"  size="10" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getIndustryId()!=null?masterDOB.getIndustryId():""%>">&nbsp;<input class="input" type="button" name="industryLov" value="..." onclick="popUpWindow('industry')">
			</td>
			<!--<td width ='50%' >
			  <table  cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
			   <tr  class="formdata">-->
			<td valign="bottom">Commodity Type:<!-- <font color="#FF0000">*</font> --><br><!-- @@modified by subrahmanyam for 178964 on 11-aug-09 -->
	
						<select size="1" name="commodityType" class='select' style="width:300px;margin:0px 0 5px 0;">
						  <option selected>(Select)</option>
						  <option value='Edible,Animal & Vegetable Products' <%="Edible,Animal & Vegetable Products".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Edible,Animal & Vegetable Products</option>
						  <option value='AVI,Inedible Animal & Vegetable Products' <%="AVI,Inedible Animal & Vegetable Products".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>AVI,Inedible Animal & Vegetable Products</option>
						  <option value='Textiles,Fibres & Mfrs.' <%="Textiles,Fibres & Mfrs.".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Textiles,Fibres & Mfrs.</option>
						  <option value='Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment' <%="Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment</option>
						  <option value='Machinery,Vehicles & Electrical Equipment' <%="Machinery,Vehicles & Electrical Equipment".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Machinery,Vehicles & Electrical Equipment</option>
						  <option value='Non-Metallic Minerals & Mfrs.' <%="Non-Metallic Minerals & Mfrs.".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Non-Metallic Minerals & Mfrs.</option>
						  <option value='Chemicals & Related Products' <%="Chemicals & Related Products".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Chemicals & Related Products</option>
						  <option value='Paper,Reed,Rubber & Wood Mfrs.' <%="Paper,Reed,Rubber & Wood Mfrs.".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Paper,Reed,Rubber & Wood Mfrs.</option>
						  <option value='Scientific,Professional & Precision Instruments' <%="Scientific,Professional & Precision Instruments".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Scientific,Professional & Precision Instruments</option>
						  <option value='Miscellaneous' <%="Miscellaneous".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>> Miscellaneous</option>
						</select>
				</td>
				<!--@@MOdified by Kameswari for the WPBN issue-61318-->
			  <!-- </tr>
			   <tr  class="formdata">-->
				  <td  width ='8%' valign="top">  Commodity<br>/Product:
					<input class="text" maxLength="16" name="commodityId"  size="10" onBlur="changeToUpper(this)" value="<%=masterDOB.getCommodityId()!=null?masterDOB.getCommodityId():""%>">&nbsp;
					<input class="input" type="button" value="..." onclick="popUpWindow('commodity')"></td>
				<td valign="bottom">
					Hazardous<br>
					<input type="checkbox" value="ON" name="hazardousInd" <%if(masterDOB.isHazardousInd()){%>checked<%}%>>
				</td>
				<td valign="bottom">
					UN # <br><input class="text" maxLength="16" name="unNo" size="7" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getUnNumber()!=null?masterDOB.getUnNumber():""%>">
				</td>
				<td valign="bottom">
					Class # <br><input class="text" maxLength="16" name="commodityClass"  id='class1' size="7" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getCommodityClass()!=null?masterDOB.getCommodityClass():""%>">
			   </td>
			<!--  </tr>
			 </table>
			</td>-->		
			<!--@@WPBN Issue-61318-->
		</tr>
       </table>
	   <table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formheader">
			<td  colspan="8" valign="bottom">Lane Details</td>
		</tr>

		<tr class="formdata">
			<td  width="20%"nowrap valign="bottom">Quoting Station:<font color="#ff0000">*</font><br>
				<input class="text" maxLength="16" name="quotingStation" size="12"value="<%=masterDOB.getQuotingStation()!=null?masterDOB.getQuotingStation():loginbean.getTerminalId()%>" onBlur="changeToUpper(this);chrnum(this)" readonly>
				<!--input class="input" type="button" value="..." onclick="popUpWindow('quotingStation');"-->
			</td>
			<td width="22%" valign="bottom">Origin Location:<font color="#ff0000">*</font><br>
			<!-- Modified by Anil.k for CR 231214 on 25Jan2011 -->
				<input class="text" maxLength="16" name="originLoc" id="originLoc" size="12" onBlur="changeToUpper(this);chrnum(this)" onChange = "appChrg();" value="<%=masterDOB.getOriginLocation()!=null?masterDOB.getOriginLocation():""%>" <%=readOnly%>>
				<!-- <input class="input" type="button" value="..." onclick="popUpWindow('originLoc')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>> -->
				<input class="input" type="button" value="..." onclick="openLocationLov('Origin')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
			</td>
			<td width="15%" valign="bottom">Shipper&nbsp;Mode:<br>
				<select name="shipperMode" class='select' id='shipperMode' onchange='changeConsoleVisibility("shipper")'>
					<option></option>
					<option value="1" <%="1".equalsIgnoreCase(masterDOB.getShipperMode())?"selected":""%>>Air</option>
					<option value="2" <%="2".equalsIgnoreCase(masterDOB.getShipperMode())?"selected":""%>>Sea</option>
			  </td>
			  <td nowrap valign="bottom">
				<DIV id="shipperConsole" style="DISPLAY:none">
					Console&nbsp;Type:</br>
					<select name="shipperConsoleType" class='select' id='shipperConsoleType' onChange="displaySpans()">
						<option value="LCL" <%="LCL".equalsIgnoreCase(masterDOB.getShipperConsoleType())?"selected":""%>>LCL</option>
						<option value="FCL" <%="FCL".equalsIgnoreCase(masterDOB.getShipperConsoleType())?"selected":""%>>FCL</option>
					</select>
				</DIV>
			</td>
			<td width="20%" nowrap valign="bottom">Shipper(Zipcode/Zone)<br>
				<input class="text" maxLength="36" name="shipperZipCode"  size="6" onBlur="chr1(this);return validateZipCode(this);" onchange="populateCAP()" value="<%=masterDOB.getShipperZipCode()!=null?masterDOB.getShipperZipCode():""%>" >&nbsp;<input class="input" type="button" value="..." id="shipperZipCodeLOV"  onclick="popUpWindow('shipperZipCode')">&nbsp;
				<input class="text" maxLength="250" name="shipperZone"  id="shipperZone" size="6" onBlur="changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('shipper','shipperZone','originLoc')" onchange="populateCAP()" value="<%=masterDOB.getShipperZones()!=null?masterDOB.getShipperZones():""%>">&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow('shipperZone')" onBlur="populateCAP();">
			</td>
			<td width="18%" valign="bottom">Origin Port:<font color="#ff0000">*</font><br>
				<input class="text" maxLength="16" name="originPort"  size="12" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getOriginPort()!=null?masterDOB.getOriginPort():""%>" <%=readOnly%>>&nbsp;
				<input class="input" type="button" value="..." onclick="openOrgPortLov()" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
				<!-- <input class="input" type="button" value="..." onclick="popUpWindow('originPort')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>> -->
			</td>
			<!--td  width="20%">Overlength Cargo Notes:<br> <!-- onBlur="chrnum(this)" >
				<input class="text" name="overLengthCargoNotes"  maxlength="100" size="26" value="<%//=masterDOB.getOverLengthCargoNotes()!=null?masterDOB.getOverLengthCargoNotes():""%>">
			</td-->
		</tr>

		<tr class="formdata">
			<td width="20%" valign="bottom">Routing ID:<br>
				<input class="text" maxLength="25" name="routeId"  size="12" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getRouteId()!=null?masterDOB.getRouteId():""%>" readOnly>
				<!--input class="input" type="button" value="..." > <@@ Commented by Yuvraj on 20051011-->
			</td>
			<td  width="22%" valign="bottom">Destination&nbsp;Location:<font color="#ff0000">*</font><br>
			<!-- Modified by Anil.k for CR 231214 on 25Jan2011 -->
				<input class="text" maxLength="16" name="destLoc" id="destLoc" size="12" onBlur="changeToUpper(this);chrnum(this)" onChange = "appChrg();" value="<%=masterDOB.getDestLocation()!=null?masterDOB.getDestLocation():""%>" <%=readOnly%>>
				<!-- <input class="input" type="button" value="..." onclick="popUpWindow('destLoc')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>> -->
				<input class="input" type="button" value="..." onclick="openLocationLov('Dest')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
			</td>
			<td width="15%" valign="bottom">Consignee&nbsp;Mode:<br>
				<select name="consigneeMode" id='consigneeMode' class='select' onchange='changeConsoleVisibility("consignee")'>
					<option></option>
					<option value="1" <%="1".equalsIgnoreCase(masterDOB.getConsigneeMode())?"selected":""%>>Air</option>
					<option value="2" <%="2".equalsIgnoreCase(masterDOB.getConsigneeMode())?"selected":""%>>Sea</option>
			  </td>
			  <td valign="bottom">
				<DIV id="consigneeConsole" style="DISPLAY:none">
					Console&nbsp;Type:</br>
					<select name="consigneeConsoleType" id='consigneeConsoleType' class='select'>
						<option value="LCL" <%="LCL".equalsIgnoreCase(masterDOB.getConsigneeConsoleType())?"selected":""%>>LCL</option>
						<option value="FCL" <%="FCL".equalsIgnoreCase(masterDOB.getConsigneeConsoleType())?"selected":""%>>FCL</option>
					</select>
				</DIV>
			</td>
			<td  width="20%" nowrap valign="bottom">Consignee(Zipcode/Zone)<br>
				<input class="text" maxLength="36" name="consigneeZipCode"  size="6" onBlur="chr1(this);return validateZipCode(this)" value="<%=masterDOB.getConsigneeZipCode()!=null?masterDOB.getConsigneeZipCode():""%>" onchange="populateCAP()">
				<input class="input" type="button" value="..."  id="consigneeZipCodeLOV" onclick="popUpWindow('consigneeZipCode')" onBlur="populateCAP()">
				<input class="text" maxLength="250" name="consigneeZone" id="consigneeZone" size="6" onBlur="changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('consignee','consigneeZone','destLoc')" onchange="populateCAP()" value="<%=masterDOB.getConsigneeZones()!=null?masterDOB.getConsigneeZones():""%>">&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow('consigneeZone')" onBlur="populateCAP()">
			</td>
			<td  width="38%" colspan="2" valign="bottom">Destination&nbsp;Port:<font color="#ff0000">*</font><br>
				<input class="text" maxLength="16" name="destPort"  size="12" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getDestPort()!=null?masterDOB.getDestPort():""%>" <%=readOnly%>>
				<input class="input" type="button" value="..." onclick="openDestPortLov()" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
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
				legCurrency             =   legDOB.getCurrency();
				legDensityRatio			=	legDOB.getDensityRatio();
				leg						=	"("+legDOB.getOrigin()+"-"+legDOB.getDestination()+")";

			}
			else
			{
				leg						=	"("+legDOB.getOrigin()+"-"+legDOB.getDestination()+")";
			}
		}
%>
	<table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
	<tr class='formheader'><td colspan='2' valign="top">Incoterms<%=leg%></td></tr>
	<tr class='formdata'>
	<td width ='15%' valign="bottom">Incoterms:<font color="#ff0000">*</font><!--<br><br>-->
				<!--modified by silpa on 3-05-11-->
				<select class="select"  name="incoTerms" size="1" onchange='populateCAP()'>
					<option value="CFR" <%="CFR".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CFR</option>
					<option value="CIF" <%="CIF".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CIF</option>
					<option value="CIP" <%="CIP".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CIP</option>
					<option value="CPT" <%="CPT".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>CPT</option>
                                        <option value="DAP" <%="DAP".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DAP</option>
                                        <option value="DAT" <%="DAT".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DAT</option>
					<option value="DAF" <%="DAF".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DAF</option>
					<option value="DDP" <%="DDP".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DDP</option>
					<option value="DDU" <%="DDU".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DDU</option>
					<option value="DDU CC" <%="DDU CC".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DDU CC</option>
					<option value="DEQ" <%="DEQ".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DEQ</option>
					<option value="DES" <%="DES".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>DES</option>
					<option value="EXW" <%="EXW".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>EXW</option>
					<option value="FAS" <%="FAS".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>FAS</option>
					<option value="FCA" <%="FCA".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>FCA</option>
					<option value="FOB" <%="FOB".equalsIgnoreCase(masterDOB.getIncoTermsId())?"selected":""%>>FOB</option>
				</select>
				<!--ended-->
			</td>
<!-- @@ Commented by subrahmanyam for the WPBN ISSUE:150460 on 23/12/2008 -->
			<!-- <td width ='19%' valign='bottom'>Cargo Acceptance Place:<font color="#ff0000">*</font> --><!--<br>-->
<!-- @@ Added by subrahmanyam for the WPBN ISSUE:150460 ON 23/12/2008 -->
				<td width ='19%' valign='bottom'>Named Place :<font color="#ff0000">*</font>
				<select class='select' valign='bottom' name='cargoAcceptance' onchange='populateCAP()' style="width:118px;margin:0px 0 5px 0;">
					<option value=''></option>
					<option value='ZIPCODE' <%="ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Zip Code</option>
					<option value='ZONECODE' <%="ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Zone Code</option>
					<option value='DDAO' <%="DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>DGF Terminal</option>
					<option value='PORT' <%="PORT".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Port</option>
					<option value='OTHER' <%="OTHER".equalsIgnoreCase(masterDOB.getCargoAcceptance())?"selected":""%>>Other</option>
				</select>
				<!-- @@ commented by subrahmanyam for the pbn id: 187569 on 28th oct09 -->
				<!-- 
								<textarea name='cargoAccPlace' valign='bottom' class="text" onBlur="return check_Length(255,this)"  cols="16" rows='4' style="height:30" cols="25" rows='5'><%=masterDOB.getCargoAccPlace()!=null?masterDOB.getCargoAccPlace():""%></textarea>

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
	<table width="101%"" cellpadding="4" cellspacing="1" nowrap id="QuoteChargeGroups" idcounter="1" defaultElement="chargeGroupId" xmlTagName="QuoteChargeGroups" bgcolor='#FFFFFF'>
		
		<tr class='formheader'> 
			<td colspan="13" valign="bottom"> Applicable Charge Groups</td>
		</tr>

	</table>
	<table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
	<tr class='formheader'><td colspan='2' valign="bottom">Spot Buy Rates<%=leg%></td></tr>
	<tr class='formdata'>
		<td nowrap valign="bottom">Spot Rates: 
		<select class="select"  id="spotRateFlag<%=i%>" name="spotRateFlag<%=i%>" size="1" onChange="displaySpans()" <%=disabled%>>
					<option value="N" <%=!isSpotRatesFlag?"selected":""%>>NO</option>
					<option value="Y" <%=isSpotRatesFlag?"selected":""%>>YES</option>
				</select>
			</td>
		</tr>
		<tr class='formdata'>
		<td valign="bottom">
		<span id="spotRateTypeSpan<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='1' bgcolor='#FFFFFF'>
		<tr  class='formdata'>
				<td valign="bottom">
					Spot Rate Type:<font color=#ff0000>*</font><br>
					<select class="select"  id="spotRateType<%=i%>" name="spotRateType<%=i%>" size="1" onChange="displaySpans();showListLov('<%=legShipmentMode%>',<%=i%>)">
						<option value="FLAT" <%="FLAT".equalsIgnoreCase(spotRateType)?"selected":""%>>Flat</option>
						<option value="SLAB" <%="SLAB".equalsIgnoreCase(spotRateType)?"selected":""%>>Slab</option>
						<option value="LIST" <%="LIST".equalsIgnoreCase(spotRateType)?"selected":""%>>List</option>
					</select>
				</td>
				<td valign="bottom">
					Service Level:<font color=#ff0000>*</font><br>
					<input type='text' class="text"  id="serviceLevelType<%=i%>" name="serviceLevel<%=i%>" size='10' value='<%=legServiceLevel!=null?legServiceLevel:""%>' onblur='this.value=this.value.toUpperCase()'>&nbsp;<input class="input" type="button" name="serviceLvlLov2" value="..." onclick="showLegServiceLevel(<%=i%>,'<%=legShipmentMode%>')">
				</td>
				<td valign="bottom">
					UOM:<font color=#ff0000>*</font><br>
					<select class="select"  id="uom<%=i%>" name="uom<%=i%>" size="1" onChange="displaySpans()">
						<option value="Kg" <%="Kg".equalsIgnoreCase(legUOM)?"selected":""%>>Kg</option>
						<option value="Lb" <%="Lb".equalsIgnoreCase(legUOM)?"selected":""%>>Lb</option>
					</select>
				</td>
				<td valign="bottom">
					Density Ratio:<font color=#ff0000>*</font><br>
					<input type='text' class="text" id="densityRatio<%=i%>" name="densityRatio<%=i%>" size='10' value='<%=legDensityRatio!=null?legDensityRatio:""%>' onblur='this.value=this.value.toUpperCase()'>&nbsp;<input class="input" type="button" name="densityRatioLOV<%=i%>" id="densityRatioLOV<%=i%>" value="..." onclick="showDensityLOV(<%=i%>,'<%=legShipmentMode%>')">
					<input type='hidden' id='legShipmentMode<%=i%>' name='legShipmentMode<%=i%>' value='<%=legShipmentMode%>'/>
				</td>
				<!-- @@Added by kameswari for WPBN issue 30908--> 
				<td valign="bottom">Currency ID:<font color=#ff0000>*</font><br>
			        <input type='text' class='text' name="currencyId<%=i%>" id="currencyId<%=i%>" size='16' maxlength=10  onBlur='this.value=this.value.toUpperCase()' value='<%=legCurrency!=null?legCurrency:""%>'>
					<input type='button' class='input'  name='b1<%=i%>' id="b1<%=i%>" value='...' onclick="pop(<%=i%>);">

				</td>
            <!--WPBN-30908-->
			</tr>
			</table>
			</span>
			</td>
		</tr>
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
							(String)weightBreaks.get(j+1))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j+1))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"CAF%".equalsIgnoreCase((String)weightBreaks.get(j+1))||"BAFMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"BAFM3".equalsIgnoreCase((String)weightBreaks.get(j+1))||"CSF".equalsIgnoreCase((String)weightBreaks.get(j+1))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j+1))||"PSSMIN".equalsIgnoreCase((String)weightBreaks.get(j+1))||"PSSM3".equalsIgnoreCase((String)weightBreaks.get(j+1)) ))
						{count8++;%>
				
					<td align='center' valign="bottom"><input class='text' maxLength='8' name='slabWeightBreak<%=i%>' size='6' onKeyPress='<%if(j==0){%>return getNumberCodeNegative(this)<%}else{%>return getDotNumberCode(this)<%}%>' value="<%=weightBreaks.get(j+1)%>"></td>
<%                      }
					
					}
				}
                   	for(int k=count8;k<15;k++)
				{%> 
                     <td align='center' valign="bottom"><input class='text' maxLength='8' name='slabWeightBreak<%=i%>' size='6'  value="" onKeyPress='<%if(k==0){%>return getNumberCodeNegative(this)<%}else{%>return getDotNumberCode(this)<%}%>'></td> <%// Modified By RajKumari on 11/12/2008 for WPBN 14402 %>
				<%
						}
			
%>
			</tr>

			<tr class='formdata' >
				<td>Freight Rate</td>

			
			<%for(int j=0;j<weightBreaksSize;j++)
				{
					
					if(spotRateDetails!=null && "SLAB".equalsIgnoreCase(spotRateType)&&j<weightBreaksSize)
					{
											/*if(!("FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j+1))||"FSMIN".equalsIgnoreCase(
							(String)weightBreaks.get(j))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"CAF%".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFM3".equalsIgnoreCase((String)weightBreaks.get(j))||"CSF".equalsIgnoreCase((String)weightBreaks.get(j))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSM3".equalsIgnoreCase((String)weightBreaks.get(j)) ))
						{*/
						//@@MOdified by Kameswari for the WPBN issue-118148
						if(!("FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"FSMIN".equalsIgnoreCase(
							(String)weightBreaks.get(j))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"CAF%".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"BAFM3".equalsIgnoreCase((String)weightBreaks.get(j))||"CSF".equalsIgnoreCase((String)weightBreaks.get(j))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"PSSM3".equalsIgnoreCase((String)weightBreaks.get(j)) ))
						{
						spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
						}
						else
                              spotRate[2]	=	0;
					}
					else if("SLAB".equalsIgnoreCase(spotRateType))
						spotRate[2]	=	0;
%>
					<td align='center' ><input class='text' maxLength='8' name='slabRate<%=i%>' size='3' onKeyPress='return getDotNumberCode(this)' value='<%="SLAB".equalsIgnoreCase(spotRateType)?spotRate[2]:0%>'></td>
<%				}
				for(int k=weightBreaksSize;k<16;k++)
				{
%>		
					<td align='center'><input class='text' maxLength='8' name='slabRate<%=i%>' size='6' onKeyPress='return getDotNumberCode(this)'>
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
	
	               if(!(((String)(weightBreaks.get(j))).endsWith("csf")||((String)(weightBreaks.get(j))).endsWith("pss")||((String)(weightBreaks.get(j))).endsWith("caf")||((String)(weightBreaks.get(j))).endsWith("baf")||"FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"FSMIN".equalsIgnoreCase(
				(String)weightBreaks.get(j))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j))))
					{	
								count++;
					  
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
			for(int j=0;j<weightBreaksSize;j++)
				{
				if(spotRateDetails!=null && "LIST".equalsIgnoreCase(spotRateType))
					{
			    	if(!(((String)(weightBreaks.get(j))).endsWith("csf")||((String)(weightBreaks.get(j))).endsWith("pss")||((String)(weightBreaks.get(j))).endsWith("caf")||((String)(weightBreaks.get(j))).endsWith("baf")||"FSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"FSMIN".equalsIgnoreCase(
				(String)weightBreaks.get(j))||"FSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"SSBASIC".equalsIgnoreCase((String)weightBreaks.get(j))||"SSMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SSKG".equalsIgnoreCase((String)weightBreaks.get(j))||"CAFMIN".equalsIgnoreCase((String)weightBreaks.get(j))||"SURCHARGE".equalsIgnoreCase((String)weightBreaks.get(j))))
				{	temp++;
					    
						containerBreak.add(weightBreaks.get(j));//@@Added by subrahmanyam for the wpbn id:196050 on 29/Jan/10
						spotRate	=	(double[])spotRateDetails.get(weightBreaks.get(j));
					
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
			 } else
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
			 }
			 else
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
			 	 
			 }
			 else
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
					<td width='35'>&nbsp;</td>
<%
			  int temp1=0;	
//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
			 HashMap bafCharges = new HashMap();
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
<!-- 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='bafRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%="LIST".equalsIgnoreCase(spotRateType)?spotRate[2]:0.0%>'></td>
 -->
<%	}			
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

%>
			</tr>
			<tr class='formdata'>
				<td valign="bottom">C.A.F%</td>
			<td width='35'>&nbsp;</td>
<%
//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
				  int temp2=0;	
			 HashMap cafCharges = new HashMap();
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

%>
			</tr>
			<tr class='formdata'>
				<td valign="bottom">C.S.F</td>
			<td width='35'>&nbsp;</td>
<%
	//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
		int temp3=0;
			 HashMap csfCharges = new HashMap();
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

%>
			</tr>
			<tr class='formdata'>
				<td valign="bottom">P.S.S</td>
				<td width='35'>&nbsp;</td>
<%
	//@@Modified by subrahmanyam for the wpbn id:196050 on 29/Jan/10
				 int temp4=0;
			 HashMap pssCharges = new HashMap();
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

				
<!-- 					<td align='center'valign="bottom" ><input class='text' maxLength='8' name='pssRate<%=i%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=spotRate[2]%>'></td>
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

	<!--
	<table width="127%"" cellpadding="4" cellspacing="1" nowrap id="QuoteChargeGroups" idcounter="1" defaultElement="chargeGroupId" xmlTagName="QuoteChargeGroups" bgcolor='#FFFFFF'>
		
		<tr class='formheader'> 
			<td colspan="13"> Applicable Charge Groups</td>
		</tr>

	</table>-->
		
	<table width="101%"" cellpadding="4" cellspacing="1" nowrap id="QuoteContent" idcounter="1" defaultElement="headerFooter" xmlTagName="QuoteContent" bgcolor='#FFFFFF'>

		<tr class='formheader'> 
			<td colspan="13" valign="bottom"> Content on Quote (Default Selected if Left Empty) </td>
		</tr>

		<tr class='formheader'> 
			<td></td>
			<td align="center" valign="bottom">Header/Footer</td>
			<td align="center" valign="bottom">Content</td>
			<td align="center" valign="bottom">Level</td>
			<td align="center" valign="bottom">Align</td>
			<td align="center" valign="bottom"></td>
		</tr>
	
	</table>
		
	<table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>

		<tr class="text">
			<td  align="left" valign="bottom"><font color="#ff0000">*</font>Denotes Mandatory</font></td>
			<td  align="right">
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="originLocationName">
				<input type="hidden" name="destLocationName">
				<input type="hidden" name="portName">
				<input type="hidden" name="subOperation" value="MASTER" >
				<input class="input" name="submit" id='SaveExit' type="submit" value="Save & Exit" >
				<input class="input" name="submit" type="submit" value="Next >>" >
				<input type="hidden" name="contactIds" value="<%=custContactIds!=null?custContactIds:""%>"> <!-- //added  by rk -->
			    <input type="hidden" name="checkFlag" value=""> 
					 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
				 <input type="hidden" name="quoteName" value="<%= masterDOB.getQuoteId()!=null? masterDOB.getQuoteId():""%>">

					 <input type="hidden" name="quoteStatus" value="<%= quoteStatus%>">
         	 <!-- @@WPBN issue-167677 -->

			
				 <input type="hidden" name="completeFlag" value="<%= completeFlag%>">
				 	 <!-- @@WPBN issue-167677 -->

			   	 <!-- Added by Phani for the WPBN issue-167678 -->
			   <input type="hidden" name="userModifiedMailIds" value=>
			   <input type="hidden" name="attentionCustomerId" value='<%=customerId!=""?customerId:""%>'>
			   <input type="hidden" name="attentionSlno" value='<%=attentionSlno!=""?attentionSlno:""%>'>
			   <input type="hidden" name="attentionEmailId" value='<%=emailId!=""?emailId:""%>'>
			   <input type="hidden" name="attentionFaxNo" value='<%=faxNo!=""?faxNo:""%>'>		
          <input type="hidden" name="attentionContactNo" value='<%=contactNo!=""?contactNo:""%>'>	
			   <!--ends 167678 -->
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
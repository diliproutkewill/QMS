<%--
% 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.0 
%
--%>
<%
/**
	Program Name	: QMSCSellRate.jsp
	Module Name		: Consolidated Sell Rates
	Task			: CR_DHL_QMS_1006	
	Sub Task		: 	
	Author Name		: Yuvraj Waghray
	Date Started	: 
	Date Completed	: 
	Date Modified	: 
	Description		:
         This file is invoked when user selects Add/Modify of Consolidated Sell Rates from Menu bar of  main Tree structure .This file is 
         used to add/modify Consolidated Sell Rate Details.
         On entering all the details and clicking the submit button
         QMSConsolidatedSellRateController.java is called.
		  
		
*/
%>
<%@ page import = "javax.naming.InitialContext,
			java.util.ArrayList,
			java.util.HashMap,
			java.util.Iterator,
			org.apache.log4j.Logger,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue,
			com.qms.operations.sellrates.java.QMSSellRatesDOB,
			com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!   
      private static Logger logger = null;
      private static final  String  FILE_NAME = "QMSCSellRate.jsp" ;
%>

<%
	logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
    String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
	String				terminalId				=	loginbean.getTerminalId();
	String				operation				=	null;
	String 				shipmentMode			=	null;
	String				fromWhere				=	null;
	ErrorMessage		errorMessageObject		=   null;
	ArrayList			keyValueList			=   new ArrayList();
	QMSSellRatesDOB		sellRateDetails			=	null;
	int  count = 0;
	String originLocation       = null;
    String originCountry        = null;
    String destinationLocation  = null;
    String destinationCountry   = null;
    String[] carriers           = null;
    String serviceLevel         = null;
    String currencyId           = null;
    String weightBreak          = null;
    String rateType             = null;
    String weightClass          = null;
	String currentDate			= null;
	String modifyTerminalId		= null;
    
	ArrayList weightBreakHdrList= null;
	String[] weightBreakHdr		= null;
	String flatSelected         = "";
	String slabSelected         = "";
	String bothSelected         = "";
	String contextPath			= "";

	ArrayList list				= null;
	HashMap	  hMap				= null;
	int temp  = 0;
	java.text.DecimalFormat df	=new java.text.DecimalFormat("##,##,##0.00");
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
		operation				=	request.getParameter("Operation");
		shipmentMode			=	request.getParameter("shipmentMode");
		fromWhere				=	request.getParameter("fromWhere");
		currentDate				=	eSupplyDateUtility.getCurrentDateString(dateFormat);
		contextPath				=	request.getContextPath();
		sellRateDetails			=	(QMSSellRatesDOB)request.getAttribute("sellRateDetails");
		list					=	(ArrayList)request.getAttribute("buyRateDetailsList");

		if(sellRateDetails!=null)
		{
			shipmentMode		=	sellRateDetails.getShipmentMode();
			originLocation		=	sellRateDetails.getOrigin();
			originCountry		=	sellRateDetails.getOriginCountry();
			destinationLocation	=	sellRateDetails.getDestination();
			destinationCountry	=	sellRateDetails.getDestinationCountry();
			carriers			=	sellRateDetails.getCarriers();
			serviceLevel		=	sellRateDetails.getServiceLevel();
			currencyId			=	sellRateDetails.getCurrencyId();
			weightBreak			=	sellRateDetails.getWeightBreak();
			rateType			=	sellRateDetails.getRateType();
			weightClass			=	sellRateDetails.getWeightClass();
			modifyTerminalId	=	sellRateDetails.getTerminalId();

			if("FLAT".equalsIgnoreCase(rateType))
				flatSelected = ",true,true";
			else if("SLAB".equalsIgnoreCase(rateType))
				slabSelected = ",true,true";
			else
				bothSelected = ",true,true";
		}

		
		//Logger.info(FILE_NAME, " operation ", operation);
		//Logger.info(FILE_NAME, " shipmentMode ", shipmentMode);
		//Logger.info(FILE_NAME, " loginbean.getAccessType() ", loginbean.getAccessType());
	}
	catch(Exception e)
    {
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSCSellRate.jsp "+e);
    logger.error(FILE_NAME+"Error in QMSCSellRate.jsp "+e);
		
		errorMessageObject = new ErrorMessage(  "Error while retrieving the details  ","etrans/QMSCSellRate.jsp?Operation="+operation+"&shipmentMode="+shipmentMode); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation","Add")); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
	    	
	
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
   }
%>

<html>
<head>
<title>Consolidated Sell Rates-<%=operation%></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
function changeOptions()
{
	var rateType	   = document.getElementById("rateType");
	var weightClass	   = document.getElementById("weightClass");
	
	if(document.forms[0].weightBreak.options.value=='FLAT')
	{
		rateType.length=0;
		weightClass.length=0;
		rateType[0]		= new Option('Flat','FLAT');
		weightClass[0]	= new Option('General','G');
	}
	else
	{
		rateType.length=0;
		weightClass.length=0;
		rateType[0] = new Option('Flat','FLAT'<%=flatSelected%>);
		rateType[1] = new Option('Slab','SLAB'<%=slabSelected%>);
		rateType[2] = new Option('Both','BOTH'<%=bothSelected%>);
		weightClass[0]	= new Option('General','G'<%="G".equalsIgnoreCase(weightClass)?",true,true":""%>);
		weightClass[1]	= new Option('Weight Scale','W'<%="W".equalsIgnoreCase(weightClass)?",true,true":""%>);
	}
}
function disableSubmit()
{
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].value=="Submit")
		{
			obj[i].disabled=true;
		}
	}
}
function trimAll(obj) 
{
	var sString = obj.value;
	while (sString.substring(0,1) == ' ')
	{
		sString = sString.substring(1, sString.length);
	}
	while (sString.substring(sString.length-1, sString.length) == ' ')
	{
		sString = sString.substring(0,sString.length-1);
	}
	obj.value = sString;
}

function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2 = (toSet=='origin')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);

	var Url='etrans/ETCLOVLocationIds.jsp?wheretoset='+toSet+'&searchString='+searchString+'&from=CSR&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.options.value;
	var Win=open(Url,'Doc',Features);
}
function openLocationLov(input)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	var shMode	=	document.forms[0].shipmentMode.options.value;

	if(shMode=="Sea" || shMode=='2')
	{
		tabArray = 'PORT_ID';
		Url		="<%=contextPath%>/qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shMode+"&operation=PORTS&search= where PORT_ID LIKE '"+input.value+"~'";	
		Options	='width=600,height=750,resizable=yes';
		
	}
	else
	{
		tabArray = 'LOCATIONID';
		Url		="<%=contextPath%>/qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shMode+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+input.value+"~'";	
		Options	='width=750,height=750,resizable=yes';
	}
	
	if(input=="Origin")
	{
		tabArray = 'LOCATIONID';
		formArray = 'origin';
		
		if(shMode=="Sea" || shMode=='2')
		{
			tabArray = 'PORT_ID';
			Url		="<%=contextPath%>/qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shMode+"&operation=PORTS&search= where PORT_ID LIKE '"+document.forms[0].origin.value+"~'";	
			Options	='width=600,height=750,resizable=yes';
			
		}
		else
		{
			Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].origin.value+"~'";	
		}
	}
	else if(input=="Dest")
	{
		tabArray = 'LOCATIONID';
		formArray = 'destination';

		if(shMode=="Sea" || shMode=='2')
		{
			tabArray = 'PORT_ID';
			Url		="<%=contextPath%>/qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shMode+"&operation=PORTS&search= where PORT_ID LIKE '"+document.forms[0].destination.value+"~'";	
			Options	='width=600,height=750,resizable=yes';
			
		}
		else
		{
			Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].destination.value+"~'";
		}
		
	}
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=750,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}
function showCurrencyLOV()
{
	var searchString = document.forms[0].baseCurrency.value;
	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+searchString+'&Operation=<%=operation%>';
	var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
	var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
	var myFeatures = myBars+','+myOptions;
	
	newWin = open(myUrl,'myDoc',myFeatures);
	if (newWin.opener == null) 
		newWin.opener = self;
	return true;

}
function showTerminalLOV()
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = document.forms[0].modifyTerminal.value;

	var Url='QMSLOVAllLevelTerminalIds.jsp?Operation=Modify&searchString='+searchString+'&name=modifyTerminal&selection=single&fromWhere=terminal';
	var Win=open(Url,'Doc',Features);
}
function showServiceLevelLOV()
{   
				
	var searchString=document.forms[0].serviceLevelId.value.toUpperCase();
	var Url		=	"etrans/ETCLOVServiceLevelIds.jsp?searchString="+searchString+"&shipmentMode="+document.forms[0].shipmentMode.options.value;	
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

function showCountryLOV(toSet)
{
	var locationId;
	if(toSet=='originCountry')
		locationId = document.forms[0].origin.value;
	else if(toSet=='destinationCountry')
		locationId = document.forms[0].destination.value;
		
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='originCountry')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);

	var Url='etrans/ETCLOVCountryIds.jsp?searchString='+searchString+"&whereClause=CSR&wheretoset="+toSet+"&locationId="+locationId+'&shipmentMode='+document.forms[0].shipmentMode.options.value;

	var Win=open(Url,'Doc',Features);
}
function showCarrierIds()
{
	var shipmentMode	=	document.forms[0].shipmentMode.value;

	var URL 		= 'etrans/ETransLOVCarrierIds1.jsp?shipmentMode='+shipmentMode+'&multiple=multiple&Operation=CSR&originLoc='+document.forms[0].origin.value+'&destLoc='+document.forms[0].destination.value;
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 700,height = 300,resizable = no';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
}
function setCarrierIdValues(values)
{
	var carrierId	=	document.forms[0].carriers;
	carrierId.options.length	=	0;
	for(var i=0;i<values.length;i++)
		carrierId.options[i]=new Option(values[i].value,values[i].value,true,true);

	carrierId.readOnly = true;
}
function calcPerBuyRate(index)
{

         
			
		var averageUplift	= document.getElementsByName("averageUplift")[index].value;
	
	var palletBuyRate   = document.getElementsByName("palletBuyRate")[index].value;
	
	var perBuyRate  = round(parseFloat(palletBuyRate/averageUplift));
	if(!isNaN(perBuyRate))
		document.getElementsByName("perBuyRate")[index].value	= perBuyRate;
	else
		document.getElementsByName("perBuyRate")[index].value	= '';
}
function round (n)
{
	n = n - 0; // force number
	d = 2;
	var f = Math.pow(10, d);
	n += Math.pow(10, - (d + 1)); // round first
	n = Math.round(n * f) / f;
	n += Math.pow(10, - (d + 1)); // and again
	n += ''; // force string
	return d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 1);
}
function calcValues()
{
<%	if("Modify".equals(operation))
	{
%>		var averageUplift = document.getElementsByName("averageUplift");
		var palletBuyRate = document.getElementsByName("palletBuyRate");
		var perBuyRate	  = document.getElementsByName("perBuyRate");
		
		for(var i=0;i<perBuyRate.length;i++)
		{
			perBuyRate[i].value = round(parseFloat(palletBuyRate[i].value/averageUplift[i].value))
		}
<%
	}
%>

}
function Mandatory()
{
	if(document.forms[0].submitName.value=="Search")
	{
		var msgHeader		= '';
		var msgErrors		= '';
		var focusPosition	= new Array();

		msgHeader= '_____________________________________________________\n\n' +
			'This form has not been submitted because of the following error(s).\n' +
			'Please correct the error(s) and re-submit.\n' +
			'_____________________________________________________\n\n';

		if(document.forms[0].baseCurrency.value.length == 0)
		{
			msgErrors += 'CurrencyId cannot be empty\n';
			focusPosition[3] = 'baseCurrency';
		}
		if(document.forms[0].origin.value.length == 0)
		{
			msgErrors += 'Origin Location cannot be empty\n';
			focusPosition[6] = 'origin';
		}
		if(document.forms[0].destination.value.length == 0)
		{
			msgErrors += 'Destination Location cannot be empty\n';
			focusPosition[10] = 'destination';
		}
		/*if(document.forms[0].carriers.value.length == 0)
		{
			msgErrors += 'Carrier Ids cannot be empty\n';
			focusPosition[14] = 'carriers';
		}
		if(document.forms[0].serviceLevelId.value.length == 0)
		{
			msgErrors += 'Service Level cannot be empty\n';
			focusPosition[16] = 'serviceLevelId';
		}*/
		
		if(msgErrors.length > 0)
		{
			alert(msgHeader + msgErrors);
			for(loop =0 ;loop< focusPosition.length; loop++)
			{
				if(focusPosition[loop] != null && focusPosition[loop] != '')
				{
					document.forms[0].elements[focusPosition[loop]].focus();
					break;
				}
			}
			return false;
		}
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='text')
			{
				document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
			}
		}
	}
<%
	if("FLAT".equalsIgnoreCase(weightBreak))
	{
%>
		else if(document.forms[0].submitName.value=="Submit")
		{
			var checkBoxValue	= document.getElementsByName("checkBoxValue");
			var palletCapacity  = document.getElementsByName("palletCapacity");
			var palletBuyRate   = document.getElementsByName("palletBuyRate")
			var averageUplift	= document.getElementsByName("averageUplift");
			var looseSpace		= document.getElementsByName("looseSpace");
			var checkedFlag		= false; 

			for(var i=0;i<checkBoxValue.length;i++)
			{
				if(checkBoxValue[i].value == "YES")
				{
					checkedFlag	= true;
					if(palletCapacity[i].value.length==0 || palletCapacity[i].value==0)
					{
						alert("Please Enter Pallet Capacity at Row Number "+(i+1));
						palletCapacity[i].focus();
						return false;
					}
					if(palletBuyRate[i].value.length==0 || palletBuyRate[i].value==0)
					{
						alert("Please Enter Pallet Buy Rate at Row Number "+(i+1));
						palletBuyRate[i].focus();
						return false;
					}
					if(averageUplift[i].value.length==0 || averageUplift[i].value==0)
					{
						alert("Please Enter Average Uplift per Pallet at Row Number "+(i+1));
						averageUplift[i].focus();
						return false;
					}
					if(looseSpace[i].value.length==0)
					{
						alert("Please Enter Loose Space at Row Number "+(i+1));
						looseSpace[i].focus();
						return false;
					}
				}
			}
			if(checkedFlag)
			{
				if(document.forms[0].minRate.value.length==0 || document.forms[0].minRate.value==0)
				{
					alert("Please Enter the Minimum Rate");
					document.forms[0].minRate.focus();
					return false;
				}
				if(document.forms[0].flatRate.value.length==0 || document.forms[0].flatRate.value==0)
				{
					alert("Please Enter the Flat Rate");
					document.forms[0].flatRate.focus();
					return false;
				}
			}
			else
			{
				//return confirm('No Lane is Selected.\nDo You Wish to Continue?');
				alert('Please Select at Least One Lane');
				document.forms[0].select.focus();
				return false;
			}
		}
<%
	}
	else if("SLAB".equalsIgnoreCase(weightBreak))
	{
%>
		else if(document.forms[0].submitName.value=="Submit")
		{
			var checkBoxValue	= document.getElementsByName("checkBoxValue");
			var csrValues		= document.getElementsByName("csrValues");
			var checkedFlag		= false;
			for(var i=0;i<checkBoxValue.length;i++)
			{
				if(checkBoxValue[i].value == "YES")
				{
					checkedFlag	= true;
		
					for(var j=0;j<csrValues.length;j++)
					{
						if(checkedFlag && (csrValues[j].value.length==0 || csrValues[j].value==0) && document.getElementsByName("slabValue"+i+j)[0].value!='-' )
						{
							alert("Please Enter the Consolidated Sell Rate at Column "+(j+1));
							csrValues[j].focus();
							return false;
						}
					}
				}
			}
			if(!checkedFlag)
			{
				//return confirm('No Lane is Selected.\nDo You Wish to Continue?');
				alert('Please Select at Least One Lane');
				document.forms[0].select.focus();
				return false;
			}
				
		}
<%
	}
%>
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
		obj[i].disabled=true;
}
function setValue(index)
{
		
	if(document.getElementsByName("chkBox")[index].checked)
		document.getElementsByName("checkBoxValue")[index].value="YES";
	else
		document.getElementsByName("checkBoxValue")[index].value='';
}
function selectAll()
{
	var checkBoxes = document.getElementsByName("chkBox");
	
	if(document.forms[0].select.checked)
	{
		for(var i=0;i<checkBoxes.length;i++)
		{		
			checkBoxes[i].checked=true;
			setValue(i);
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			checkBoxes[i].checked=false;
			setValue(i);
		}
	}
}
function setName(name)
{
	document.forms[0].submitName.value	= name;
	
	if(name=="Submit")
		document.forms[0].SubOperation.value='setSellRates';
	else if(name=="Search")
		document.forms[0].SubOperation.value='Details';
}

function changeLabels()
{
	if(document.forms[0].shipmentMode.options.value=='2')
	{
		document.getElementById('origin').innerHTML = "Port of Loading";
		document.getElementById('destination').innerHTML = "Port of Discharge";
	}
	else
	{
		document.getElementById('origin').innerHTML = "Origin";
		document.getElementById('destination').innerHTML = "Destination";
	}
}
function validateNumeric(input)
{
	s = input.value;
	filteredValues = "01234567890.";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) > -1 )
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Only Numeric Values Allowed!");
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
</head>

<%
 try
 {
%>
<body bgcolor="#FFFFFF" onload='changeOptions();changeLabels();calcValues()'>
<form method="post" action="QMSConsolidatedSellRateController" onSubmit="return Mandatory()">
<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
<tr>
	<td>
	<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Consolidated Sell Rates Master - <%=operation%></td><td align=right>SP1010121</td></tr></table></td>
		  </tr></table>
<%
		if(request.getAttribute("Errors")!=null)
		{
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="0">
				<tr valign="top" bgcolor="#FFFFFF">
					<td width="33%"><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
						<%=(String)request.getAttribute("Errors")%></font></td>
				</tr>
			</table>
			
<%
		}
%>
       <table width="100%" cellpadding="4" cellspacing="1" bgcolor="#FFFFFF">
       <tr valign="top" class='formdata'>
			<td>Shipment Mode:<br>
				<select name='shipmentMode' class='select' size=1 onchange="changeLabels();disableSubmit()">
					<option value='1' <%="1".equals(shipmentMode) ? "selected" : ""%>>Air</option>
					<option value='2' <%="2".equals(shipmentMode) ? "selected" : ""%>>Sea</option>
					<option value='4' <%="4".equals(shipmentMode) ? "selected" : ""%>>Truck</option>
				</select>
			</td>
				<td >Weight Break:<br>
              <select size="1" name="weightBreak" class='select' onchange='changeOptions();disableSubmit()'>
                <option  value="FLAT" <%="FLAT".equals(weightBreak)?"selected":""%>>Flat</option>
                <option value="SLAB" <%="SLAB".equals(weightBreak)?"selected":""%>>Slab</option>
                
                  </select>
            </td>
			<td >Rate Type: <br>
              <select size="1" name="rateType" class='select' id="rateType" onchange='disableSubmit()'>
				<option value="SLAB">Slab</option>
                <option  value="FLAT">Flat</option>
                <option value="BOTH">Both</option>
                
              </select>
            </td>
			<td>Currency:<font  color=#ff0000>*</font><br>
				<input type='text' class='text' name='baseCurrency' size='3' maxlength='3' value='<%=currencyId!=null?currencyId:""%>' onblur='this.value=this.value.toUpperCase()' onchange='disableSubmit()'>
				&nbsp;<input type='button' value='...' name='currencyLOV' class='input' onclick='showCurrencyLOV()'>
			</td>
<%
			if("Modify".equalsIgnoreCase(operation))
			{
%>			
				<td colspan='2' nowrap>Terminal Id:<br>
					<input type='text' class='text' name='modifyTerminal' size='10' value='<%=modifyTerminalId!=null?modifyTerminalId:""%>' onblur='this.value=this.value.toUpperCase()' onchange='disableSubmit()'>&nbsp;<input type='button' value='...' name='currencyLOV' class='input' onclick='showTerminalLOV()'>
				</td>
<%
			}
%>
			<td colspan='5' nowrap>Weight Class: <br>
				<select size="1" name="weightClass" class='select' onchange='disableSubmit()'>
						<option  value="G">General</option>
						<option  value="W">Weight Scale</option>
              </select>
            </td>
          <tr class='formheader'> 
		  <td><span id="origin"></span>:<font color="#FFFFFF">*</font></td>
		  <!--td>Origin Country:</td-->
           <td><span id="destination"></span>:<font color="#FFFFFF">*</font></td>
            <!--td>Destination Country:</td-->
            <td colspan='2'>Carriers:<font color="#FFFFFF">**</font></td>
            <td colspan='6'>Service Level:</td>
          </tr>
          <tr class='formdata'> 
            <td>
              <input type="text" name="origin"  class='text' size="15" value='<%=originLocation!=null?originLocation:""%>' onblur='trimAll(this);this.value=this.value.toUpperCase()' onchange='disableSubmit()'>&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='openLocationLov("Origin")'>
            </td>
            <!--td >
              <input type="text"  name="originCountry"  class='text' size="15" value='<%//=originCountry!=null?originCountry:""%>'  onblur='trimAll(this);this.value=this.value.toUpperCase()' onchange='disableSubmit()'>&nbsp;<input type="button" value="..." name="originCountryLOV"  class='input' onclick='showCountryLOV("originCountry")' >
            </td-->
            <td>
              <input type="text" name="destination"  class='text' size="15" value='<%=destinationLocation!=null?destinationLocation:""%>' onblur='trimAll(this);this.value=this.value.toUpperCase()' onchange='disableSubmit()'>&nbsp;<input type="button" value="..." name="destinationLOV"  class='input' onclick='openLocationLov("Dest")'>
            </td>
            <!--td>
              <input type="text" name="destinationCountry"  class='text' size="15" value='<%//=destinationCountry!=null?destinationCountry:""%>' onblur='trimAll(this);this.value=this.value.toUpperCase()' onchange='disableSubmit()'>&nbsp;<input type="button" value="..." name="destinationCountyLOV"  class='input' onclick='showCountryLOV("destinationCountry")' >
            </td-->
            <td colspan='2'>
			  <select size='5' name='carriers' class='select' multiple>
<%
				if(carriers!=null)
				{
					for(int i=0;i<carriers.length;i++)
					{
%>					<option value="<%=carriers[i]%>" selected><%=carriers[i]%></option>		
<%					}
				}
%>
			  </select>
			  <input type="button" value="..." name="carriersLOV"  class='input' onclick='showCarrierIds()'>
            </td>
            <td colspan='6'>
              <input type="text" name="serviceLevelId"  class='text' size="15" value='<%=serviceLevel!=null?serviceLevel:""%>' onblur='this.value=this.value.toUpperCase()' onchange='disableSubmit()'>&nbsp;<input type="button" value="..." name="serviceLevelLOV"  class='input' onclick='showServiceLevelLOV()'>
            </td>
          </tr>
          <tr class='denotes'>             
            <td colspan='8' align="left" >
			Note:<br><font color=red>*&nbsp;&nbsp;</font>Denotes Mandatory Fields.<br>
			<font color=red>**</font>Denotes Multiple Carrier IDs can be selected by the user.
            </td>
			<td align="right">
			  <input type="hidden" name="Operation" value="<%=operation%>">	
			  <input type="hidden" name="SubOperation" value="Details">
			  <input type="hidden" name="currentDate" value="<%=currentDate%>">
			  <input type="hidden" name="submitName">
              <input type="submit" name="Submit" value="Search" class='input' onclick="setName(this.value)">
            </td>
          </tr>

  </table>
  <%
		if("SLAB".equals(weightBreak))
		{
			
			String chargeRateInd		= null;
			String slabValue			= null;
			String[] rateDescHdr        = null;
			ArrayList rateDescHdrList   = null;
			if((list!=null && list.size()==2))//@@Checking for list size == 1 as weightBreakHdrList will be added to the list irrespective of the no. of records
			{
%>			<table width="100%"   border="0" cellpadding="4" cellspacing="1">
			<tr bgcolor="#FFFFFF"> 
					<td colspan="13" align="center">
					<font face="Verdana" size="2" color='red'>
					<b>No Rates Are Defined for the Specified Details.</b></font>
					</td>
			</tr>
			</table>
<%			}

			else if(list!=null)
			{			
				weightBreakHdrList = (ArrayList)list.get(0);
				weightBreakHdr = new String[weightBreakHdrList.size()];
				Iterator it		  = weightBreakHdrList.iterator();
				
				for(int i=0;it.hasNext();i++)
				{
					weightBreakHdr[i] = (String)it.next();
				}
				rateDescHdrList = (ArrayList)list.get(1);
				rateDescHdr = new String[rateDescHdrList.size()];
				Iterator itr		  = rateDescHdrList.iterator();
				if(itr!=null)
				{
					for(int i=0;itr.hasNext();i++)
					{
						rateDescHdr[i] = (String)itr.next();
					}
				}
%>
        <table width="100%" cellpadding="2" cellspacing="1" bgcolor="#FFFFFF">
          <tr class='formheader' align="center"> 
			<td>Select<br>
			<input type="checkbox" name="select" onclick='selectAll()'>
            </td>
			<td>Origin</td>
			<td>Destination</td>
			<td>Service Level</td>
			<td>Carrier</td>
			<td >Approximate Transit Time</td>
			<td >Frequency</td>
			<td >Defined By</td>
<%
			 for(int j=0;j<weightBreakHdr.length;j++)
			{
            
			 if("A FREIGHT RATE".equalsIgnoreCase(rateDescHdr[j]))
				{%>				
					<td><%=weightBreakHdr[j]%></td>
<%			  }
			}
%>
			<td >Notes</td>
          </tr>
<%
			for(int i=2;i<list.size();i++)
			{
	            count = 0;
				temp = 0;
				hMap =(HashMap)list.get(i);
%>
          <tr class='formdata' align="center"> 
			<td> 
				<input type="checkbox" name="chkBox" align="center" onclick='setValue(<%=(i-2)%>)'>
				<input type="hidden" name="checkBoxValue">
				<input type="hidden" name="buyRateId" value='<%=hMap.get("BUYRATEID")%>'>
				<input type="hidden" name="sellRateId" value='<%=hMap.get("SELLRATEID")!=null?hMap.get("SELLRATEID"):""%>'>
				<input type="hidden" name="terminalId" value='<%=hMap.get("TERMINALID")!=null?hMap.get("TERMINALID"):""%>'>
				<input type="hidden" name="accessLevel" value='<%=hMap.get("ACCESSLEVEL")!=null?hMap.get("ACCESSLEVEL"):""%>'>
             </td>
			<td>
              <%=originLocation!=null?originLocation:""%>
            </td>
		   <td >
              <%=destinationLocation!=null?destinationLocation:""%>
            </td>
			<td >
              <%=hMap.get("SERVICE_LEVEL")!=null?hMap.get("SERVICE_LEVEL"):""%>
			  <input type="hidden" name="serviceLevel" value="<%=hMap.get("SERVICE_LEVEL")!=null?hMap.get("SERVICE_LEVEL"):""%>">
            </td>
			<td>
				<%=hMap.get("CARRIER_ID")!=null?hMap.get("CARRIER_ID"):""%>
			  <input type="hidden" name="carrier_id" value="<%=hMap.get("CARRIER_ID")%>">
              </td>
			<td>
              <%=hMap.get("TRANSIT_TIME")!=null?hMap.get("TRANSIT_TIME"):""%>
			  <input type="hidden" name="transitTime" value="<%=hMap.get("TRANSIT_TIME")%>">
            </td>
			<td><%=hMap.get("FREQUENCY")!=null?hMap.get("FREQUENCY"):""%></td>
<%
			if("Add".equalsIgnoreCase(operation))
			{
%>
				<td>
				  <%=hMap.get("BUYTERMINALID")!=null?hMap.get("BUYTERMINALID"):""%>
				</td>
<%			}
			else
			{
%>				<td>
				  <%=hMap.get("TERMINALID")!=null?hMap.get("TERMINALID"):""%>
				</td>
<%			}
			 for(int j=0;j<weightBreakHdr.length;j++)
			{
				 if("A FREIGHT RATE".equalsIgnoreCase(rateDescHdr[j]))
				{
					count++;
					chargeRateInd = (String)hMap.get(weightBreakHdr[j]+"CHARGERATE_INDICATOR");
					if("Modify".equals(operation))
						slabValue	  =  (String)hMap.get(weightBreakHdr[j]+"BUYRATE");
					else
						slabValue	  =   (String)hMap.get(weightBreakHdr[j]);
				
%>
			  <td nowrap> <%=((String)hMap.get(weightBreakHdr[j])!=null?df.format(Double.parseDouble((String)hMap.get(weightBreakHdr[j]))):"-")%><%=chargeRateInd!=null?("("+chargeRateInd.substring(0,1)+")"):""%>
			 </td>
			 <input type="hidden" name="servicelevel" value="<%=hMap.get("SERVICE_LEVEL")!=null?hMap.get("SERVICE_LEVEL"):""%>">
			 <input type="hidden" name="slabValue<%=(i-2)%><%=(j)%>" value="<%=slabValue!=null?slabValue:"-"%>">
			 <input TYPE="hidden" name="lowerBound<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"LOWERBOUND")%>">
			  <input TYPE="hidden" name="upperBound<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"UPPERBOUND")%>">
			  <input TYPE="hidden" name="chargeRateInd<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"CHARGERATE_INDICATOR")%>">
			  <input type='hidden' name="lineNumber<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"LINE_NO")%>">
<%			}
			}
%>			  		  
			<td>
			<input type='text' class='text' name="notes" size="8" colspan='4' value="<%=hMap.get("NOTES")!=null?hMap.get("NOTES"):""%>">
            </td>
		  </tr>
		  <tr class="formdata">
		  <td>&nbsp;</td>
		  <%
			for(int j=count;j<weightBreakHdr.length;j++)
			{
				temp++;%>
					  
		  <td align='center'><%=weightBreakHdr[j]%>
		 </td>
		  <%}
			
			for(int k=temp;k<count+8;k++)
			{%>
			<td>&nbsp;</td>
			<%}%>
		   </tr>
		    <tr class="formdata">
			 <td>&nbsp;</td>
		  <%
			for(int j=count;j<weightBreakHdr.length;j++)
			{
					          
				   chargeRateInd = (String)hMap.get(weightBreakHdr[j]+"CHARGERATE_INDICATOR");
					if("Modify".equals(operation))
						slabValue	  =  (String)hMap.get(weightBreakHdr[j]+"BUYRATE");
					else
						slabValue	  =   (String)hMap.get(weightBreakHdr[j]);
				
%>
			  <td nowrap  align='center'> <%=((String)hMap.get(weightBreakHdr[j])!=null?df.format(Double.parseDouble((String)hMap.get(weightBreakHdr[j]))):"-")%><%=chargeRateInd!=null?("("+chargeRateInd.substring(0,1)+")"):""%>
			 </td>
			 <input type="hidden" name="servicelevel" value="SCH">
			 <input type="hidden" name="slabValue<%=(i-2)%><%=(j)%>" value="<%=slabValue!=null?slabValue:"-"%>">
			 <input TYPE="hidden" name="lowerBound<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"LOWERBOUND")%>">
			  <input TYPE="hidden" name="upperBound<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"UPPERBOUND")%>">
			  <input TYPE="hidden" name="chargeRateInd<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"CHARGERATE_INDICATOR")%>">
			  <input type='hidden' name="lineNumber<%=(i-2)%><%=j%>" value="<%=hMap.get(weightBreakHdr[j]+"LINE_NO")%>">
		  <%}
			
			for(int k=temp;k<count+8;k++)
				{%>
				<td>&nbsp;</td>
				<%}%>

		   </tr>
			  <input type='hidden' name="frequency" value="<%=hMap.get("FREQUENCY")%>">
			  <input type='hidden' name="laneNumber" value="<%=hMap.get("LANE_NO")%>">
			  <input type='hidden' name="consoleType" value="<%=hMap.get("CONSOLE_TYPE")%>">
<%
			}
%>
		<tr Class=formlabel align=center>
		<td colspan="<%=weightBreakHdr.length+9%>"> <b>Consolidated
            Sell Rates</b> </td>
		</tr>
		<tr Class=formdata> 
		<td colspan=5 align="left">
		<b>Consolidated Sell Rates (including margin): </b><font color=red> * </font>
       </td>
<%
			 for(int j=0;j<count;j++)
			{
	            
%>
				<td align=center>
				<input type='text' class='text' name="csrValues" size="6"  maxlength='8' onblur='validateNumeric(this);this.value=round(this.value)'>
				<input type="hidden" name="weightBreakSlab" value="<%=weightBreakHdr[j]%>">
				</td>
<%
			  
			} 
%>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr class="formdata">
				<td colspan=5 align="left"><b>Surcharge Sell Rates</td>
					
                <%
			 for(int j=count;j<weightBreakHdr.length;j++)
			{
	         %>
			 <td align=center>
				<input type='text' class='text' name="csrValues" size="6"  maxlength='8' onblur='validateNumeric(this);this.value=round(this.value)'>
			<input type="hidden" name="weightBreakSlab" value="<%=weightBreakHdr[j]%>">
			<%}%>
				</td>
				<%
			 for(int j=0;j<count+3;j++)
			{
	            
%>
             <td>&nbsp;</td>
			 <%
			}%>
             </tr>
              <tr class='denotes'> 
				<td colspan="<%=count+7%>" align='center'>&nbsp;</td>
              <td  align='right'>
              <input type="submit" name="Submit" value="Submit" class='input' onclick="setName(this.value)">
              </td>
              </tr>

			</table>
		</td>
	</tr>
</table>

<%
			}
		}
		else if("FLAT".equals(weightBreak))
		{
			//Logger.info(FILE_NAME,"weightBreakweightBreak::"+list.size());
			if(list!=null && list.size()==1)
			{
%>			<table width="100%"   border="0" cellpadding="4" cellspacing="1">				
				<tr bgcolor="#FFFFFF"> 
					<td colspan="13" align="center">
					<font face="Verdana" size="2" color='red'>
					<b>No Rates Are Defined for the Specified Details.</b></font>
					</td>
			</tr>
			</table>
<%
			}
			else if(list!=null)
			{
%>
			<table width="100%" cellpadding="2" cellspacing="1" bgcolor="#FFFFFF">
          <tr class='formheader' align="center"> 
			<td>Select<br>
				<input type="checkbox" name="select" onclick='selectAll()'>
            </td>
			<td>Origin</td>
			<td>Destination</td>
            <td>Service Level</td>
            <td>Carrier</td>
            <td>Frequency</td>
			<td>Defined By</td>
			<td>MIN</td>
			<td>FLAT</td>
            <td>Pallet Capacity</td>
            <td >Pallet Buy Rate</td>
			<td>Average Uplift per pallet</td>
			<td colspan="2">Buy Rate per pallet per kg</td>
			<td>Loose space Rate per kg</td>
			<td>Notes</td>
          </tr>
<%
				String minValue		= null;
				String flatValue	= null;
				weightBreakHdrList = (ArrayList)list.get(0);
				weightBreakHdr = new String[weightBreakHdrList.size()];
				Iterator it		  = weightBreakHdrList.iterator();
			
			if(it!=null)
				{
				for(int k=0;it.hasNext();k++)
				{
					weightBreakHdr[k] = (String)it.next();
				}
			}
				for(int i=1;i<list.size();i++)
			{
				hMap =(HashMap)list.get(i);
				minValue	= (String)hMap.get("MINRATE");
				flatValue	= (String)hMap.get("FLATRATE");
%>			
          <tr class='formdata' align="center"> 
			<td>
            <input type="checkbox" name="chkBox" onclick='setValue(<%=i-1%>)'>
			<input type="hidden" name="checkBoxValue">
			<input type="hidden" name="buyRateId" value='<%=hMap.get("BUYRATEID")%>'>
			<input type="hidden" name="sellRateId" value='<%=hMap.get("SELLRATEID")!=null?hMap.get("SELLRATEID"):""%>'>
			<input type="hidden" name="terminalId" value='<%=hMap.get("TERMINALID")!=null?hMap.get("TERMINALID"):""%>'>
			<input type="hidden" name="accessLevel" value='<%=hMap.get("ACCESSLEVEL")!=null?hMap.get("ACCESSLEVEL"):""%>'>
			<input type='hidden' name="frequency" value="<%=hMap.get("FREQUENCY")%>">
			<input type='hidden' name="laneNumber" value="<%=hMap.get("LANE_NO")%>">
			<input type='hidden' name="consoleType" value="<%=hMap.get("CONSOLE_TYPE")%>">
            </td>
			<td><%=originLocation!=null?originLocation:""%></td>
			<td><%=destinationLocation!=null?destinationLocation:""%></td>
			  
            <td>
				<%=hMap.get("SERVICE_LEVEL")!=null?hMap.get("SERVICE_LEVEL"):""%>
				<input type="hidden" name="serviceLevel" value="<%=hMap.get("SERVICE_LEVEL")!=null?hMap.get("SERVICE_LEVEL"):""%>">
			</td>
			<td>
             <%=hMap.get("CARRIER_ID")!=null?hMap.get("CARRIER_ID"):""%>
			 <input type="hidden" name="carrier_id" value="<%=hMap.get("CARRIER_ID")%>">
			 </td>
			 <td><%=hMap.get("FREQUENCY")!=null?hMap.get("FREQUENCY"):""%></td>
<%
			if("Add".equalsIgnoreCase(operation))
			{
%>
				<td>
					<%=hMap.get("BUYTERMINALID")!=null?hMap.get("BUYTERMINALID"):""%>
				</td>
<%
			}
			else
			{
%>				<td>
				  <%=hMap.get("TERMINALID")!=null?hMap.get("TERMINALID"):""%>
				</td>
<%			}
%>
			 <td><%=minValue!=null?df.format(Double.parseDouble(minValue)):""%>
			 <input type="hidden" name="minValue" value="<%="Modify".equals(operation)?hMap.get("MINBUYRATE"):minValue%>">
			 <input type="hidden" name="minLineNumber" value="<%=hMap.get("MINLINE_NO")!=null?hMap.get("MINLINE_NO"):""%>">
			 
			 <input type="hidden" name="servicelevel" value="<%=hMap.get("SERVICE_LEVEL")!=null?hMap.get("SERVICE_LEVEL"):""%>">
             </td>
			 <td>
			 <%=flatValue!=null?df.format(Double.parseDouble(flatValue)):""%>
			 <input type="hidden" name="flatValue<%=(i-1)%><%=1%>" value="<%="Modify".equals(operation)?hMap.get("FLATBUYRATE"):flatValue%>">
			 <input type="hidden" name="flatLineNumber<%=(i-1)%><%=1%>" value="<%=hMap.get("FLATLINE_NO")!=null?hMap.get("FLATLINE_NO"):""%>">
			 
			 <input type="hidden" name="servicelevel" value="<%=hMap.get("SERVICE_LEVEL")!=null?hMap.get("SERVICE_LEVEL"):""%>">
             </td>
			<td align="center">
              <input type='text' class='text' name='palletCapacity' size='8' maxlength='8' value="<%=hMap.get("PALLET_CAPACITY")!=null?hMap.get("PALLET_CAPACITY"):""%>" onblur="validateNumeric(this);this.value=round(this.value)">
            </td>
			<td>
				<input type='text' class='text' name='palletBuyRate' size='8' maxlength='8' value="<%=hMap.get("PALLET_BYRATE")!=null?hMap.get("PALLET_BYRATE"):""%>" onPropertyChange='calcPerBuyRate(<%=i-1%>)' onblur="validateNumeric(this);this.value=round(this.value)">
            </td>
			<td>
				<input type='text' class='text' name='averageUplift' size='8' maxlength='8' id='averageUplift' value="<%=hMap.get("AVEREAGE_UPLIFT")!=null?hMap.get("AVEREAGE_UPLIFT"):""%>" onPropertyChange='calcPerBuyRate(<%=i-1%>)' onblur="validateNumeric(this);this.value=round(this.value)">
            </td>
			<td colspan="2">
				<input type='text' class='text' name='perBuyRate' size='8' readOnly maxlength='8'>
            </td>
			<td >
				<input type='text' class='text' name='looseSpace' size='8' value="<%=hMap.get("LOOSE_SPACE")!=null?hMap.get("LOOSE_SPACE"):""%>" onblur="validateNumeric(this);this.value=round(this.value)" maxlength='8'>
            </td>
			<td>
				<input type='text' class='text' name="notes" size="8" value="<%=hMap.get("NOTES")!=null?hMap.get("NOTES"):""%>" readOnly>
            </td>
          </tr>
			<input TYPE="hidden" name="lowerBound" value="<%=hMap.get("LOWERBOUND")%>">
			<input TYPE="hidden" name="upperBound" value="<%=hMap.get("UPPERBOUND")%>">
			<input type="hidden" name="transitTime" value="<%=hMap.get("TRANSIT_TIME")%>">
						
			 <tr class=formdata>
			  <td>&nbsp;</td>
          <%			
			 for(int j=0;j<weightBreakHdr.length;j++)
			{%>
              <td><%=weightBreakHdr[j]%></td>
			  <%}
			  for(int j=weightBreakHdr.length;j<15;j++)
				{%>
			  <td>&nbsp;</td>
			  <%}%>
			  </tr>
			   <tr class=formdata>
			<td>&nbsp;</td>
          <%			
			 for(int j=0;j<weightBreakHdr.length;j++)
			{%>
               
			 <td><%=(String)(hMap.get(weightBreakHdr[j]+"FLATRATE"))!=null?df.format(Double.parseDouble((String)(hMap.get(weightBreakHdr[j]+"FLATRATE")))):"-"%>
			 <input type="hidden" name="flatValue<%=(i-1)%><%=(j+2)%>" value="<%="Modify".equals(operation)?hMap.get(weightBreakHdr[j]+"FLATBUYRATE"):hMap.get(weightBreakHdr[j]+"FLATRATE")%>">
			 <input type="hidden" name="flatLineNumber<%=(i-1)%><%=(j+2)%>" value="<%=hMap.get(weightBreakHdr[j]+"FLATLINE_NO")!=null?hMap.get(weightBreakHdr[j]+"FLATLINE_NO"):""%>">
			 <input type="hidden" name="servicelevel" value="SCH">
             </td>
			 
			  <%}
			  if(weightBreakHdr!=null)
				{
			  for(int j=weightBreakHdr.length;j<15;j++)
				{%>
			  <td>&nbsp;</td>
			  <%}
				}%>
			  
			  </tr>
<%		}
%>
              <tr class=formdata>
				<td colspan="7" >&nbsp;</td>
				<td><b>MIN</b></td>
				<td  colspan="9"><b>FLAT</b></td>
              </tr>

             
             
			   <tr class=formdata>
			<td colspan="7" >
              <b>Consolidated Sell Rate (per kg)</b>
              </td>

			<td >
              <input type='text' class='text' name='minRate' size='6' maxlength='8' onblur="validateNumeric(this);this.value=round(this.value)">
			  <input type="hidden" name="weightBreakSlab" value="Min">
              </td>
			<td  colspan="9" >
              <input type='text' class='text' name='flatRate' size='6' maxlength='8' onblur="validateNumeric(this);this.value=round(this.value)">
			  <input type="hidden" name="weightBreakSlab" value="Flat">
              </td>
              </tr>
			   <tr class=formdata>
			<td colspan="7" >
              <b>Surcharge Sell Rate (per kg)</b>
              </td>
			   <%	if(weightBreakHdr!=null)
				{
			 for(int j=0;j<weightBreakHdr.length;j++)
			{%>
			<td >
              <input type='text' class='text' name='flatRate' size='6' maxlength='8' onblur="validateNumeric(this);this.value=round(this.value)">
			  <input type="hidden" name="weightBreakSlab" value="<%=weightBreakHdr[j]%>">
              </td>
			<%}
			 
			  for(int j=weightBreakHdr.length;j<10;j++)
				{%>
			  <td>&nbsp;</td>
			  <%}
				}%>
              </tr>

              <tr class='denotes'> 
              <td colspan=14 align="right" >
              </td>
              <td colspan="2" align='right'> 
              <input type="submit" name="Submit" value="Submit" class='input' onclick='setName(this.value)'>
              </td>
              </tr>
			  </table>
		</td>
	</tr>
</table>
<%			  }
		 }
 }
 catch(Exception e)
    {
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSCSellRate.jsp "+e);
    logger.error(FILE_NAME+"Error in QMSCSellRate.jsp "+e);
		
		errorMessageObject = new ErrorMessage(  "Error while retrieving the details  ","etrans/QMSCSellRate.jsp?Operation="+operation+"&shipmentMode="+shipmentMode); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation","Add")); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
	    	
	
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
   }
%>
</form>
</body>
</html>
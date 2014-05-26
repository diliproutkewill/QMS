<%--
% 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.0 
%
--%>
<%@ page import = "java.util.ArrayList,
			java.util.Iterator,
			java.util.HashMap,
			java.sql.Timestamp,
			org.apache.log4j.Logger,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue,
			com.qms.operations.charges.java.QMSCartageMasterDOB,
			com.qms.operations.charges.java.QMSCartageBuyDtlDOB,
			com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%! 
      private static Logger logger = null;
      private static final  String  FILE_NAME = "QMSCartageSellChargesSlab.jsp" ;
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
    ESupplyDateUtility  eSupplyDateUtility		= new ESupplyDateUtility();
    String              dateFormat				= loginbean.getUserPreferences().getDateFormat();
	String				terminalId				= loginbean.getTerminalId();
	String				operation				= null;
	String				subOperation			= null;
	String 				shipmentMode			= null;
	String				onLoadCalls				= "";
	ErrorMessage		errorMessageObject		= null;
	ArrayList			keyValueList			= new ArrayList();

	QMSCartageMasterDOB cartageMaster			= null;
	QMSCartageBuyDtlDOB delBuyChargesDtl		= null;
	
	QMSCartageBuyDtlDOB cartageBuyDtl			= null;

	HashMap				hMap					= null;
	HashMap				dHMap					= null;
	
	ArrayList			buyChargesList			= null;
	
	ArrayList			pickUpList				= null;
	ArrayList			delList					= null;
	ArrayList			slabList				= null;

	String				locationId				= null;
    String				zoneCode				= null;
	String[]				zoneCodes				= null;
    String[]			vendorIds				= null;
	String				str[]					= null;
    String				currencyId				= null;
    String				chargeType				= null;
    String				chargeBasis				= null;
    String				unitofMeasure			= null;
    String				weightBreak				= null;
    String				rateType				= null;
	String              densityRatio            = "";//added by rk
	String              dDensityRatio            = "";//added by rk
	int					counter					= 0;
	String				label					= "";

	Timestamp			effectiveFrom			= null;
	String				effectiveFromStr		= null;
    
	Timestamp			validUpto				= null;
    
	String				validUptoStr			= null;
	String				consoleType				= null;
	int					pickupSize				= 0;
	int					delSize					= 0;
	int					noOfSlabs				= 0;

	
	java.text.DecimalFormat df	= new java.text.DecimalFormat("##,##,##0.00");
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
		subOperation			=	request.getParameter("subOperation");
		//shipmentMode			=	request.getParameter("shipmentMode");

		//Logger.info(FILE_NAME, " operation ", operation);

		if(subOperation!=null)
			onLoadCalls = ";displayMargin()";

		cartageMaster			=	(QMSCartageMasterDOB)request.getAttribute("cartageMaster");

		if(cartageMaster!=null)
		{
			zoneCodes	    = cartageMaster.getZoneCodes();
			locationId      = cartageMaster.getLocationId();
			currencyId      = cartageMaster.getCurrencyId();
			vendorIds       = cartageMaster.getVendorIds();

			weightBreak		= cartageMaster.getWeightBreak();
			rateType		= cartageMaster.getRateType();

			chargeType		= cartageMaster.getChargeType();
			shipmentMode	= cartageMaster.getShipmentMode();
			consoleType		= cartageMaster.getConsoleType();
		}
		buyChargesList		= (ArrayList)session.getAttribute("buyChargesList");
		
		if(buyChargesList!=null && buyChargesList.size()>0)
		{
			pickUpList		= (ArrayList)buyChargesList.get(0);
			delList			= (ArrayList)buyChargesList.get(1);
			slabList		= (ArrayList)buyChargesList.get(2);
			
			if(pickUpList!=null)
				pickupSize = pickUpList.size();
			if(delList!=null)
				delSize	   = delList.size();
			if(slabList!=null)
				noOfSlabs  = slabList.size();
		}
%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Cartage Sell Charges-Slab</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">

<script>
function Mandatory()
{
	var msgHeader		= '';
	var msgErrors		= '';
	var focusPosition	= new Array();
	
	if(document.forms[0].submitName.value=="Search")
	{
		msgHeader= '_____________________________________________________\n\n' +
		'This form has not been submitted because of the following error(s).\n' +
		'Please correct the error(s) and re-submit.\n' +
		'_____________________________________________________\n\n';
		if(document.forms[0].locationId.value.length == 0)
		{
			msgErrors += 'Location Id cannot be empty\n';
			focusPosition[6] = 'locationId';
		}
		if(document.forms[0].zoneCodes.value.length == 0)
		{
			msgErrors += 'Zone Code cannot be empty\n';
			focusPosition[8] = 'zoneCodes';
		}
		if(document.forms[0].baseCurrency.value.length == 0)
		{
			msgErrors += 'CurrencyId cannot be empty\n';
			focusPosition[10] = 'baseCurrency';
		}
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
			if(document.forms[0].elements[i].type == 'text')
				document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
		}
	}
	else if(document.forms[0].submitName.value=="Submit")
	{
		 var chkBox			=   document.getElementsByName("checkBoxValue");
		 var dChkBox		=	document.getElementsByName("dCheckBoxValue");

		 var marginValues	=	document.getElementsByName("marginValues");
		 var dMarginValues	=	document.getElementsByName("dMarginValues");

		 var flag			=	true;	

		 if(chkBox!=null && chkBox.length!=0)
		{
			flag = validateMargins(chkBox,marginValues,"slabBreak","slabValue","PICKUP CHARGES");
		}

		 if(flag && dChkBox!=null && dChkBox.length!=0)
		{
			return validateMargins(dChkBox,dMarginValues,"dSlabBreak","dSlabValue","DELIVERY CHARGES");
		}
		return flag;
	}
}

function validateMargins(chkBox,marginValues,slabBreak,slabValue,label)
{
	var checkedFlag		=	false;
	for(var i=0;i<chkBox.length;i++)
	{	
	  if(chkBox[i].value == "YES")
	  {
		checkedFlag	=	true;
		if(document.forms[0].overAllMargin.options.value=='No')
		{
			for(var j=0;j<marginValues.length;j++)
			{
				if(marginValues[j].value.length==0 && document.getElementsByName(slabValue+i+j)[0].value!='-')
				{
					alert("Please Enter the Margin Value at Column "+(j+1));
					marginValues[j].focus();
					return false;
				}
			}
		}
		else if(document.forms[0].overAllMargin.options.value=='Yes' && document.forms[0].marginType.options.value=='Absolute')
		{
			var absMargins	=	document.getElementsByName("absMargins");
			for(var i=0;i<chkBox.length;i++)
			{
				for(var j=0;j<absMargins.length;j++)
				{
					if(absMargins[j].value.length==0 && document.getElementsByName(slabValue+i+j)[0].value!='-')
					{
						alert("Please Enter the Margin Value at Column "+(j+1));
						absMargins[j].focus();
						return false;
					}
				}
			}
			return true;
		}
		else
		{
			if(document.forms[0].marginPercent.value.length==0)
			{
				alert('Please Enter the Margin Percentage');
				document.forms[0].marginPercent.focus();
				return false;
			}
			else return true;
		}
	  }
  }
	if(!checkedFlag)
	 {
		alert('Please Select at least One Lane in '+label);
		return false;
	 }
	 else return true;
}

function setName(name)
{
	document.forms[0].submitName.value	= name;
	
	if(name=="Submit")
		document.forms[0].subOperation.value='setListSellRates';
	else if(name=="Search")
		document.forms[0].subOperation.value='Details';
	else if(name=="Continue")
	{
		document.forms[0].Operation.value="sellView";
		document.forms[0].subOperation.disabled=true;
		document.forms[0].submit();
		return true;
	}
}
function toUpperCase(obj)
{
	obj.value = obj.value.toUpperCase();
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
function showLocationLOV()
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = document.forms[0].locationId.value;
	var Url='etrans/ETCLOVLocationIds.jsp?from=cartage&wheretoset=locationId&searchString='+searchString+'&shipmentMode=7';
	var Win=open(Url,'Doc',Features);
}
function showZoneCodeLOV()
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = document.forms[0].locationId.value;
	var Url='etrans/QMSLOVZoneCodes.jsp?searchString1='+searchString+'&wheretoset=zoneCode&fromWhere=Cartage';
	var Win=open(Url,'Doc',Features);
}
function changeRateOptions()
{
	var rateType	   = document.getElementById("rateType");
	
	if(document.forms[0].weightBreak.options.value=='Flat')
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat');

	}
	else
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat'<%="Flat".equals(rateType)?",true,true":""%>);
		rateType[1] = new Option('Slab','Slab'<%="Slab".equals(rateType)?",true,true":""%>);
		rateType[2] = new Option('Both','Both'<%="Both".equals(rateType)?",true,true":""%>);
	}
}

function changeUOMOptions()
{
	var uom			   = document.getElementById("uom");
	if(document.forms[0].chargeBasis.options.value=='Weight')
	{
		uom.length = 0;
		uom[0] = new Option('Kg','Kg'<%="Kg".equals(unitofMeasure)?",true,true":""%>);
		uom[1] = new Option('Lb','Lb'<%="Lb".equals(unitofMeasure)?",true,true":""%>);
	}
	else
	{
		uom.length = 0;
		uom[0] = new Option('CFT','CFT'<%="CFT".equals(unitofMeasure)?",true,true":""%>);
		uom[1] = new Option('CBM','CBM'<%="CBM".equals(unitofMeasure)?",true,true":""%>);
	}
}
function displayMargin()
{
	var percent    = document.getElementById("percent");
	var absolute   = document.getElementById("absolute");
	var notOverall = document.getElementById("notOverall");
	
  if(document.forms[0].overAllMargin!=null && document.forms[0].marginType!=null)
  {	
	if(document.forms[0].overAllMargin.options.value=='No')
	{
		percent.style.display="none";
		absolute.style.display="none";
		notOverall.style.display="block";
	}
	else if(document.forms[0].overAllMargin.options.value=='Yes' && document.forms[0].marginType.options.value=='Absolute')
	{
		percent.style.display="none";
		notOverall.style.display="none";
		absolute.style.display="block";
	}
	else
	{
		absolute.style.display="none";
		notOverall.style.display="none";
		percent.style.display="block";
	}
  }

	var obj	= document.getElementsByName('marginValues');

	for(var i=0;i<obj.length;i++)
	{
		obj[i].value = '';
	}
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
function dSelectAll()
{
	var checkBoxes = document.getElementsByName("dChkBox");

	if(document.forms[0].dSelect.checked)
	{
		for(var i=0;i<checkBoxes.length;i++)
		{		
			checkBoxes[i].checked=true;
			setValueDelivery(i);
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			checkBoxes[i].checked=false;
			setValueDelivery(i);
		}
	}
}
function setValue(index)
{
	if(document.getElementsByName("chkBox")[index].checked)
		document.getElementsByName("checkBoxValue")[index].value="YES";
	else
		document.getElementsByName("checkBoxValue")[index].value='';

}
function setValueDelivery(index)
{
	if(document.getElementsByName("dChkBox")[index].checked)
		document.getElementsByName("dCheckBoxValue")[index].value="YES";
	else
		document.getElementsByName("dCheckBoxValue")[index].value='';

}
function chrnum(input)
{
	s = input.value;
	filteredValues = "'~!@#$%^&*()_+=|\:;<>,/?";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if(filteredValues.indexOf(c) == -1)
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
			if( field.elements[i] == input )
				document.forms[0].elements[i].focus();
	}

	input.value = returnString;
	if(flag==1) return false
	if(flag==0) return true
}
function getKeyCode(obj)
{
	if(isNaN(obj.value))
	{
		alert('Please Enter Numeric Values Only');
		obj.value = '';
		obj.focus();
		return false;
	}
}
function disableSubmit()
{
	var obj				= document.getElementsByName('SubmitButton');
	
	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].value=="Submit")
		{
			obj[i].disabled=true;
		}
	}
}

function showMargins(cartageId,zoneCode,chargeType,chargeBasis)
{

	var locationId  = document.forms[0].locationId.value;
	//var zoneCode    = document.forms[0].zoneCode.value;
	var rateType    = document.forms[0].rateType.value;
	var weightBreak = document.forms[0].weightBreak.value;
	//var chargeBasis = document.forms[0].chargeBasis.value;
	//var chargeType  = document.forms[0].chargeType.value;
	//var cartageId   = document.forms[0].cartageId.value;
	//alert(cartageId)
	var URL         = "etrans/QMSCartageMarginDisplay.jsp?locationId="+locationId+"&zoneCode="+zoneCode+"&rateType="+rateType+"&weightBreak="+weightBreak+"&chargeBasis="+chargeBasis+"&Operation=Modify&cartageId="+cartageId+"&chargeType="+chargeType;
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 900,height = 500,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
}
function showZoneCodesLOV()
{
	if(document.forms[0].locationId.value.length==0)
	{
		alert('Please Enter the Location Id');
		document.forms[0].locationId.focus();
		return false;
	}
	else
	{
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=700,height=300,resizable=no';
		var Features=Bars+' '+Options;
		
		var searchString1 = document.forms[0].locationId.value;
		var Url='etrans/QMSLOVZoneCodesMultiple.jsp?wheretoset=zoneCodes&searchString1='+searchString1+'&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+document.forms[0].consoleType.value;
		var Win=open(Url,'Doc',Features);
	}
}
function setZoneCodeValues(obj,where)
{
	document.getElementById(where).length=0;
	for( i=0;i<obj.length;i++)
	{
		document.getElementById(where).options[i] = new Option(obj[i].value,obj[i].value,true,true);
	}
}

function openLocationLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	var operation	=	document.forms[0].Operation.value;

	formArray = 'locationId';

	if(operation.toUpperCase()=='SELLVIEW')
		operation	=	"LOCSETUPVIEW";
	else
		operation	=	"CARTAGELOC";
	
	tabArray = 'LOCATIONID';
	Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation="+operation+"&search= where LOCATIONID LIKE '"+document.forms[0].locationId.value+"~'";

	Options	='width=750,height=750,resizable=1';
	Bars	='directories=0,location=0,menubar=no,status=0,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;

	Win=open(Url,'Doc',Features);
}
function changeConsoleVisibility()
{
	var console	 = document.getElementById('console');
	var weightBreak = document.getElementById('weightBreak');
	
	if(document.forms[0].shipmentMode.value=='2')
		console.style.display="block";
	else
	{
		console.style.display="none";
		document.forms[0].consoleType.options[0].selected = true;
	}
	changeWeightBreakOptions();
}
function changeWeightBreakOptions()
{
	var weightBreak = document.getElementById('weightBreak');
	var rateType	= document.getElementById('rateType');
	
	if(document.forms[0].consoleType.value=='FCL')
	{
		weightBreak.length=0;
		rateType.length=0;
		weightBreak[0] = new Option("List","List");
		rateType[0]= new Option("Flat","Flat");
	}
	else
	{
		weightBreak.length=0;
		weightBreak[0] = new Option("Flat","Flat");
		weightBreak[1] = new Option("Slab","Slab");
	}
}
</script>
</head>

<body onload='changeRateOptions();changeConsoleVisibility()<%=onLoadCalls%>'>
	<form method='post' action='QMSCartageController' onsubmit='return Mandatory()'>

	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				Cartage Sell Charges -  <%=("selladd".equalsIgnoreCase(operation))?"Add":("sellModify".equalsIgnoreCase(operation))?"Modify":"View"%> </td></tr></table></td>
			  </tr>
			  </table>
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
			  <table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formdata'>
			   <td>Shipment Mode:<br>
				<select name="shipmentMode" class='select' onchange='changeConsoleVisibility()'>
					<option value="1" <%="1".equalsIgnoreCase(shipmentMode)?"selected":""%>>Air</option>
					<option value="2" <%="2".equalsIgnoreCase(shipmentMode)?"selected":""%>>Sea</option>
			  </td>
			  <td>
				<DIV id="console" style="DISPLAY:none">
					Console Type:</br>
					<select name="consoleType" class='select' onchange='changeWeightBreakOptions()'>
						<option value="LCL" <%="LCL".equalsIgnoreCase(consoleType)?"selected":""%>>LCL</option>
						<option value="FCL" <%="FCL".equalsIgnoreCase(consoleType)?"selected":""%>>FCL</option>
					</select>
				</DIV>
			  </td>
			    <td >Charge Type:<br>
                <select size="1" name="chargeType" class='select' onChange='disableSubmit()'>
					<option value="Pickup" <%="Pickup".equals(chargeType)?"selected":""%>>Pickup</option>
					<option value="Delivery" <%="Delivery".equals(chargeType)?"selected":""%>>Delivery</option>
					<option value="Both" <%="Both".equals(chargeType)?"selected":""%>>Both</option>
                </select>
			  </td>
           <td>Weight Break: <font color="#FF0000">*</font><br>
              <select name="weightBreak" class='select' onchange='changeRateOptions();disableSubmit()'>
                <option value="Flat" <%="Flat".equals(weightBreak)?"selected":""%>>Flat</option>
                <option value="Slab" <%="Slab".equals(weightBreak)?"selected":""%>>Slab</option>
			 </select>
		  </td>
		  <td colspan='2'>Rate Type: <font color="red">*</font><br>
              <select name="rateType" class='select' onchange='disableSubmit()'>
                <option selected value="Flat">Flat</option>
                <option value="Slab">Slab</option>
			 </select>
		 </td>
		</tr>       
			  
		   <tr valign="top" class='formdata'>
			<td>Location Id: <font  color="red">*</font><br>
				<input type='text' class='text' name="locationId" size="10" onblur='toUpperCase(this)' value='<%=locationId!=null?locationId:""%>'>
				<input type="button" value="..." name="locationIdLOV" class='input' onclick='openLocationLov()'>
			</td>
			<td colspan="2">Zone Codes:<font color="#FF0000">*</font><br>
					<select size='5' name='zoneCodes' class='select' multiple>
<%
					if(zoneCodes!=null)
					{
					    for(int i=0;i<zoneCodes.length;i++)
						{
%>							<option value='<%=zoneCodes[i]%>' selected><%=zoneCodes[i]%></option>
<%
						}
					}
%>
					</select>
					<input type="button" value="..." name="zoneCodeLOV" class='input' onclick='showZoneCodesLOV()'>
				</td>
			<td colspan='3'>Currency:<font  color="red">*</font><br>
				<input type='text' class='text' name='baseCurrency' size='10' onblur='toUpperCase(this)' value='<%=currencyId!=null?currencyId:""%>'>
				<input type='hidden' class='text' name='baseCurrencyTemp' size='10' onblur='toUpperCase(this)' value='<%=currencyId!=null?currencyId:""%>'>
                <input type='button' value='...' name='creditcurrency' onClick='showCurrencyLOV()' class='input'>
            </td>
		  </tr>

			<tr valign="top" class='denotes'>
			   <td colspan="5" align='left'>
			   <font  color="red">*</font> Denotes Mandatory <br>
			  </td>
			   <td align='right'>
				<input name="submitButton" type="Submit" value="Search" class='input' onclick="setName(this.value)">
			  </td>
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="subOperation" value="Details">
				<input type="hidden" name="submitName">			   
			</tr>
		</table>
<%
	if(request.getAttribute("Errors")==null)
	{
%>
		<table width="100%" cellpadding="4" cellspacing="1">
<%	if(pickupSize == 0 && delSize == 0)
	{
%>
		<tr bgcolor="#FFFFFF"> 
			<td colspan="13" align="center">
			<font face="Verdana" size="2" color='red'>
			<b>No Charges Are Defined for the Specified Details.</b></font>
			</td>
		</tr>
<%
	}
	else
	{
		if(pickupSize > 0)
		{
%>		 <tr valign="top" class='formheader' align="center">
<%		if(!"sellView".equalsIgnoreCase(operation))
		{
%>
            <td rowspan="2" width='5%'>Select<br><input type='checkbox' name='select' onclick='selectAll()'></td>
<%		}
%>
            <td rowspan="2" width='10%'>Zone Code</td>
			<td rowspan="2" width='10%'>Effective From</td>
			<td rowspan="2" width='10%' nowrap>Valid Upto</td>
			<td rowspan="2" width='10%'>Charge Basis</td>
			<td rowspan="2" width='10%'>Density Ratio</td>			
            <td colspan='<%=noOfSlabs+1%>' align="center">PICKUP CHARGES</td>
		</tr>
         <tr valign="top" class='formheader' align="center">
<%
			 for(int i=0;i<noOfSlabs;i++)
			{
%>				<td><%=slabList.get(i)%></td>
<%			}
			if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation))
			{
%>
				<td>Margin Details<br>(Previous)</td>
<%			}
%>
		</tr>
<%		for(int i=0;i<pickupSize;i++)
		{
			cartageBuyDtl		= (QMSCartageBuyDtlDOB)pickUpList.get(i);
			hMap				= cartageBuyDtl.getSlabRates();

			effectiveFrom		= cartageBuyDtl.getEffectiveFrom();
			validUpto			= cartageBuyDtl.getValidUpto();
			densityRatio        = cartageBuyDtl.getDensityRatio()!=null?cartageBuyDtl.getDensityRatio():"";

			str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
			effectiveFromStr	= str[0];
			str					= null;

			str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
			validUptoStr		= str[0];
			str					= null;
%>	
         <tr valign="top" class='formdata' align="center">
<%		if(!"sellView".equalsIgnoreCase(operation))
		{
%>
            <td><input type="checkbox" name="chkBox" onclick='setValue("<%=i%>")'></td>
<%		}
%>
			<td>Zone <%=cartageBuyDtl.getZoneCode()%></td>
			<td><%=effectiveFromStr%></td>
			<td width='10%'><%=validUptoStr%></td>
			<td width='10%'><%=cartageBuyDtl.getChargeBasis()%></td>
			<td width='10%'><%=densityRatio%></td>
			<input type='hidden' name='checkBoxValue'>
<%
			 for(int j=0;j<noOfSlabs;j++)
			{
%>		
				<td>
					<%=hMap.get(slabList.get(j))!=null?df.format(Double.parseDouble((String)hMap.get(slabList.get(j)))):"-"%>
					<input type='hidden' name='slabValue<%=i%><%=j%>' value='<%=hMap.get(slabList.get(j))!=null?(String)hMap.get(slabList.get(j)):"-"%>'>
				</td>
<%			}
			if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation))
			{
%>
				<td >
					<input type='button' value='View' name='View' class='input' onClick = "showMargins('<%=cartageBuyDtl.getCartageId()%>','<%=cartageBuyDtl.getZoneCode()%>','<%=cartageBuyDtl.getChargeType()%>','<%=cartageBuyDtl.getChargeBasis()%>')">
				</td>
<%			}
%>
		</tr>
<%
		}
	}
		if(delSize > 0)
		{
%>
			<tr class="formheader" align="center">
<%			if(!"sellView".equalsIgnoreCase(operation))
			{
%>
	            <td rowspan="2" width='5%'>Select<br><input type='checkbox' name='dSelect' onclick='dSelectAll()'></td>
<%			}
%>
            <td rowspan="2"  width='10%'>Zone Code</td>
            <td rowspan="2" width='10%'>Effective From</td>
            <td rowspan="2" width='10%' nowrap>Valid Upto</td>
            <td rowspan="2" width='10%'>Charge Basis</td>
			<td rowspan="2" width='10%'>Density Ratio</td>
            <td colspan="<%=noOfSlabs+1%>">DELIVERY CHARGES</td>
           </tr>           
		   <tr class="formheader" align="center">
<%  		for(int i=0;i<noOfSlabs;i++)
			{
%>				<td><%=slabList.get(i)%></td>
<%			}
			if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation))
			{
%>
				<td>Margin Details<br>(Previous)</td>
<%			}
%>
		</tr>
<%
			for(int i=0;i<delSize;i++)
			{
				delBuyChargesDtl	= (QMSCartageBuyDtlDOB)delList.get(i);
				dHMap				=  delBuyChargesDtl.getSlabRates();

				effectiveFrom		= delBuyChargesDtl.getEffectiveFrom();
				validUpto			= delBuyChargesDtl.getValidUpto();
				dDensityRatio       = delBuyChargesDtl.getDensityRatio()!=null?delBuyChargesDtl.getDensityRatio():"";
				str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
				effectiveFromStr	= str[0];
				str					= null;

				str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
				validUptoStr		= str[0];
				str					= null;

%>
        <tr class="formdata" align="center">
<%		if(!"sellView".equalsIgnoreCase(operation))
		{
%>
            <td><input type="checkbox" name="dChkBox" onclick='setValueDelivery("<%=i%>")'></td>
<%		}
%>
            <td>Zone <%=delBuyChargesDtl.getZoneCode()%></td>
            <td><%=effectiveFromStr%></td>
            <td><%=validUptoStr%></td>
            <td><%=delBuyChargesDtl.getChargeBasis()!=null?delBuyChargesDtl.getChargeBasis():""%></td>
			<td><%=dDensityRatio%></td>
			<input type='hidden' name='dCheckBoxValue'>
<%
			 for(int j=0;j<noOfSlabs;j++)
			{
%>			 <td>
				<%=dHMap.get(slabList.get(j))!=null?df.format(Double.parseDouble((String)dHMap.get(slabList.get(j)))):"-"%>
				<input type='hidden' name='dSlabValue<%=i%><%=j%>' value='<%=dHMap.get(slabList.get(j))!=null?(String)dHMap.get(slabList.get(j)):"-"%>'>
			</td>
<%			}
			if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation))
			{
%>
			  <td ><input type='button' value='View' name='View' class='input' onClick = "showMargins('<%=delBuyChargesDtl.getCartageId()%>','<%=delBuyChargesDtl.getZoneCode()%>','<%=delBuyChargesDtl.getChargeType()%>','<%=delBuyChargesDtl.getChargeBasis()%>')"></td>
<%			}
%>
		</tr>
<%
			}
		}
		if(pickupSize == 0 && ("Pickup".equalsIgnoreCase(chargeType) || "Both".equalsIgnoreCase(chargeType)))
		{
%>
		<tr bgcolor="#FFFFFF"> 
			<td colspan="13" align="center">
			<font face="Verdana" size="2" color='red'>
			<b>No Pickup Charges Are Defined for the Specified Details.</b></font>
			</td>
		</tr>
<%		}

		if(delSize == 0  && ("Delivery".equalsIgnoreCase(chargeType) || "Both".equalsIgnoreCase(chargeType)))
		{
%>
		<tr bgcolor="#FFFFFF"> 
			<td colspan="13" align="center">
			<font face="Verdana" size="2" color='red'>
			<b>No Delivery Charges Are Defined for the Specified Details.</b></font>
			</td>
		</tr>
<%		}
}
		if((pickupSize > 0 || delSize > 0) && !"sellView".equalsIgnoreCase(operation))
		{
%>		
		<tr valign="top" class='formlabel'>
            <td colspan="20">Margin Calculations</td>
		</tr>
        <tr valign="top" class='formdata'>
            <td colspan='3'>Overall Margin:<br>
				<select size="1" name="overAllMargin" class='select' onchange='displayMargin()'>
					<option value="Yes" selected>YES</option>
					<option  value="No">NO</option>
				</select>
			</td>

            <td colspan='<%=noOfSlabs+4%>'>Margin Type:<br>
			<select size="1" name="marginType" class='select'  onchange='displayMargin()'>
                <option value="Absolute" selected>ABSOLUTE</option>
				<option value="Percentage">PERCENT</option>
              </select>
			 </td>
		</tr>
		</table>
		<div id='percent' style="DISPLAY:none">
			<table width='100%' cellpadding='4' cellspacing='1'>
				<tr valign='top' class='formdata'>
					<td align='right' width='26%'><b>Margin&nbsp;</b><font color='red'>* </font></td>
					<td colspan='6' align='center'><input type='text' class='text' name='marginPercent' size='5'  maxlength='6' onblur='getKeyCode(this);return chrnum(this)'></td>
				</tr>
			</table>
		</div>
        <div id='absolute' style="DISPLAY:none">
			<table width="100%" cellpadding="4" cellspacing="1">
			 <tr class='formdata'>
			 <td width='56%'><b>OVERALL MARGIN</b><font color='red'>* </font></td>
<%
				for(int i=0;i<noOfSlabs;i++)
				{
%>					<td align='center'>
						<input type='text' class='text' name="absMargins" size="5" maxlength='6' onblur='getKeyCode(this);return chrnum(this)'>
					</td>
<%
				}
%>
			 </tr>
			</table>
		</div>
		<div id='notOverall' style="DISPLAY:none">
			<table width="100%" cellpadding="4" cellspacing="1">
<%
			if(pickupSize > 0)
			{
%>
			 <tr class='formdata'>
			 <td width='56%'><b>PICKUP MARGIN</b><font color='red'>* </font></td>
<%
				for(int i=0;i<noOfSlabs;i++)
				{
%>					<td align='center'>
						<input type='text' class='text' name="marginValues" size="5" maxlength='6' onblur='getKeyCode(this);return chrnum(this)'>
					</td>
<%
				}
%>
			 </tr>
<%			}
			if(delSize > 0)
			{
%>
			 <tr class='formdata'>
			 <td width='56%'><b>DELIVERY MARGIN</b><font color='red'>* </font></td>
<%
				for(int i=0;i<noOfSlabs;i++)
				{
%>					<td align='center'>
						<input type='text' class='text' name="dMarginValues" size="5" maxlength='6' onblur='getKeyCode(this);return chrnum(this)'>
					</td>
<%
				}
			}
%>
			 </tr>
			</table>
		</div>

		<table width="100%" cellpadding="4" cellspacing="1">
	    <tr class=text>
			 <td colspan="7"><font color='red'>* </font>Denotes Mandatory</td>
			 <td align='right'>
				<input type="reset" value="Reset" class='input'>&nbsp;<input type="submit" name="SubmitButton" value="Submit" class='input' onclick="setName(this.value)">
			</td>
        </tr>
<%
		}
%>
	  </table>
<%
		if("sellView".equalsIgnoreCase(operation))
		{
%>
			<table width="100%" cellpadding="4" cellspacing="0">
			<tr>
				<td></td>
			</tr>
			<tr class=text>
				 <td align='right'>
					<input type="button" name="submitButton" value="Continue" class='input' onclick="setName(this.value)">
				</td>
			</tr>
			</table>
<%
		}
	}
%>
	</form>
 </body>
</html>
<%	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"error in JSP:"+e);
		logger.error(FILE_NAME+"error in JSP:"+e);
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Unable to Access the Page! Please Try Again.","QMSCartageController"); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);

%>		<jsp:forward page="../ESupplyErrorPage.jsp"/>

<%
	}
%>
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
      private static final  String  FILE_NAME = "QMSCartageSellChargesFlat.jsp" ;
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
    ESupplyDateUtility  eSupplyDateUtility		= new ESupplyDateUtility();
    String              dateFormat				= loginbean.getUserPreferences().getDateFormat();
	String				terminalId				= loginbean.getTerminalId();
	String				operation				= null;
	String				subOperation			= null;
	String 				shipmentMode			= null;
	String				fromWhere				= null;
	String				onLoadCalls				= "";
	ErrorMessage		errorMessageObject		= null;
	ArrayList			keyValueList			= new ArrayList();

	QMSCartageMasterDOB cartageMaster			= null;
	QMSCartageBuyDtlDOB pickUpbuyChargesDtl		= null;
	QMSCartageBuyDtlDOB delBuyChargesDtl		= null;
	
	QMSCartageBuyDtlDOB cartageBuyDtl			= null;
	
	ArrayList			buyChargesList			= null;
	
	ArrayList			pickUpList				= null;
	ArrayList			delList					= null;
	
	String				label					= "";
	
	String				locationId				= null;
    String				zoneCode				= null;
    String[]			vendorIds				= null;
	String				str[]					= null;
    String				currencyId				= null;
    String				chargeType				= null;
    String				chargeBasis				= null;
    String				dChargeBasis			= null;
    String				unitofMeasure			= null;
    String				weightBreak				= null;
    String				rateType				= null;

	String				maxChargeFlag			= "";
	String				delMaxChargeFlag		= "";
	String              densityRatio            = "";//added by rk
	String              dDensityRatio            = "";//added by rk

	double				baseRate				= 0;//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
	double				minRate					= 0;
	double				flatRate				= 0;
	double				maxRate					= 0;

	//String				zoneCode				= null;
	
	double				dBaseRate				= 0;//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
	double				dMinRate				= 0;
	double				dFlatRate				= 0;
	double				dMaxRate				= 0;

	Timestamp			effectiveFrom			= null;
	Timestamp			dEffectiveFrom			= null;
    
	String				effectiveFromStr		= null;
    String				dEffectiveFromStr		= null;
    
	Timestamp			validUpto				= null;
    Timestamp			dValidUpto				= null;
    
	String				validUptoStr			= null;
	String				dValidUptoStr			= null;

	long				pickupCartageId			= 0;
	long				delCartageId			= 0;
	boolean				displayMarginFlag		= true;

	int					counter					= 0;
	String[]            zoneCodes               = null;
	String              dZone                   = "";
	String              zone                   = "";
	String				consoleType				= null;
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
		subOperation			=	request.getParameter("subOperation");
		//shipmentMode			=	request.getParameter("shipmentMode");

		//Logger.info(FILE_NAME, " operation ", operation);

		if(subOperation!=null && request.getAttribute("Errors")==null)
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
			/*chargeBasis		= cartageMaster.getChargeBasis();
			unitofMeasure	= cartageMaster.getUom();*/
			shipmentMode	= cartageMaster.getShipmentMode();
			consoleType		= cartageMaster.getConsoleType();
		}

		buyChargesList			=	(ArrayList)request.getAttribute("buyChargesList");

		if(buyChargesList!=null && buyChargesList.size()>0)
		{
			pickUpList			= (ArrayList)buyChargesList.get(0);
			delList				= (ArrayList)buyChargesList.get(1);

			if("Delivery".equals(chargeType))
			{
				label	= "DELIVERY CHARGES";
				counter = delList.size();
			}
			else if("Both".equals(chargeType))
			{
				if(pickUpList.size()>delList.size())
					counter = pickUpList.size();
				else
					counter = delList.size();
			}
			else
			{	
				label	= "PICKUP CHARGES";
				counter = pickUpList.size();
			}
		}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Cartage Sell Charges</title>
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
		/*if(document.forms[0].vendorIds.value.length == 0)
		{
			msgErrors += 'Vendor Ids cannot be empty\n';
			focusPosition[10] = 'vendorId';
		}*/
	/*	if(document.forms[0].baseCurrency.value.length == 0)
		{
			msgErrors += 'CurrencyId cannot be empty\n';
			focusPosition[10] = 'baseCurrency';
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
			if(document.forms[0].elements[i].type == 'text')
				document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
		}
	}
	else if(document.forms[0].submitName.value=="Next>>")
	{
		var checkedFlag		=	false;
		var checkBoxValue	=	document.getElementsByName('checkBoxValue');
		var cartageId		=	document.getElementsByName('cartageId');
		var dCartageId		=	document.getElementsByName('dCartageId');

		for(var i=0;i<checkBoxValue.length;i++)
		{	
			  if(checkBoxValue[i].value == "YES")
			  {
				checkedFlag	=	true;
				if((document.forms[0].overAllMargin.options.value=='No') || (document.forms[0].overAllMargin.options.value=='Yes' && document.forms[0].marginType.options.value=='Absolute'))
				{ 
				// added by VLAKSHMI for issue 124262 on 19-11-2008	
				if(<%="Both".equals(chargeType)%>){
					 
					if(Number(document.getElementById("dMaxRate"+i).value)==0 &&  Number(document.forms[0].dMaxMargin.value)> 0 )
					{
					      //alert('Please Dont Enter the dMax Margin');
						   alert('As MAX value is Zero Max Margin should also be Zero');
							document.forms[0].dMaxMargin.focus();
							return false;
					}
					
					  if(Number(document.getElementById("maxRate"+i).value)==0 && Number(document.forms[0].maxMargin.value)> 0)
					{
				
						//alert('Please Dont Enter the Max Margin');
						 alert('As MAX value is Zero Max Margin should also be Zero');
							document.forms[0].maxMargin.focus();
							return false;
					}
				}
				else{
					if(Number(document.getElementById("maxRate"+i).value)==0 && (Number(document.forms[0].maxMargin.value) > 0))
					
					{
					      //alert('Please Dont Enter the ddMax Margin');
						  alert('As MAX value is Zero Max Margin should also be Zero');
							document.forms[0].maxMargin.focus();
							return false;
					}
				}// end of issue 124262
					if(document.forms[0].dMinMargin==null)
					{

						if(document.forms[0].minMargin.value.length==0)
						{
							alert('Please Enter the Minimum Margin');
							document.forms[0].minMargin.focus();
							return false;
						}
						if(document.forms[0].flatMargin.value.length==0)
						{
							alert('Please Enter the Flat Margin');
							document.forms[0].flatMargin.focus();
							return false;
						}
						if(document.forms[0].maxMargin.value.length==0 && document.getElementById("maxRate"+i).value!=0)
						{
							alert('Please Enter the Max Margin');
							document.forms[0].maxMargin.focus();
							return false;
						}
				  }
				else if(document.forms[0].minMargin==null)
				{
					if(document.forms[0].dMinMargin.value.length==0)
					{
						alert('Please Enter the Minimum Margin');
						document.forms[0].dMinMargin.focus();
						return false;
					}
					if(document.forms[0].dFlatMargin.value.length==0)
					{
						alert('Please Enter the Flat Margin');
						document.forms[0].dFlatMargin.focus();
						return false;
					}
					if(document.forms[0].dMaxMargin.value.length==0 && document.getElementById("dMaxRate"+i).value!=0)
					{
						alert('Please Enter the Max Margin');
						document.forms[0].dMaxMargin.focus();
						return false;
					}
				 }
				 else
				{
	
					if(document.forms[0].minMargin.value.length==0 && cartageId[i].value!='-')
					{
						alert('Please Enter the Minimum Margin for Pickup Charges');
						document.forms[0].minMargin.focus();
						return false;
					}
					if(document.forms[0].flatMargin.value.length==0  && cartageId[i].value!='-')
					{
						alert('Please Enter the Flat Margin for Pickup Charges');
						document.forms[0].flatMargin.focus();
						return false;
					}
					if(document.forms[0].maxMargin.value.length==0 && document.getElementById("maxRate"+i).value!=0  && cartageId[i].value!='-')
					{
						alert('Please Enter the Max Margin for Pickup Charges');
						document.forms[0].maxMargin.focus();
						return false;
					}

					if(document.forms[0].dMinMargin.value.length==0 && dCartageId[i].value!='-')
					{
						alert('Please Enter the Minimum Margin for Delivery Charges');
						document.forms[0].dMinMargin.focus();
						return false;
					}
					if(document.forms[0].dFlatMargin.value.length==0 && dCartageId[i].value!='-')
					{
						alert('Please Enter the Flat Margin for Delivery Charges');
						document.forms[0].dFlatMargin.focus();
						return false;
					}
					if(document.forms[0].dMaxMargin.value.length==0 && document.getElementById("dMaxRate"+i).value!=0 && dCartageId[i].value!='-')
					{
						////modified for issue 172884 by VLAKSHMI on 8/06/2009
					alert('Please Enter the Max Margin for Delivery Charges');
						document.forms[0].dMaxMargin.focus();
						return false;
					}
				}
			  }
			  else
			  {
				if(document.forms[0].marginPercent.value.length==0)
				{
					alert('Please Enter the Margin Percentage');
					document.forms[0].marginPercent.focus();
					return false;
				}
			
			  }
			}
		  }
		  if(!checkedFlag)
		 {
			  alert('Please Select at Least One Lane');
			  return false;
		 }
	}
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
		obj[i].disabled=true;
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
function setValue(index)
{
	if(document.getElementsByName("chkBox")[index].checked)
		document.getElementsByName("checkBoxValue")[index].value="YES";
	else
		document.getElementsByName("checkBoxValue")[index].value='';

}
function changeRateOptions()
{
	var rateType	   = document.getElementById("rateType");
	
	if(document.forms[0].weightBreak.options.value=='Flat')
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat');

	}
	else if(document.forms[0].weightBreak.options.value=='Slab')
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat'<%="Flat".equals(rateType)?",true,true":""%>);
		rateType[1] = new Option('Slab','Slab'<%="Slab".equals(rateType)?",true,true":""%>);
		rateType[2] = new Option('Both','Both'<%="Both".equals(rateType)?",true,true":""%>);
	}
	else
	{
		rateType.length=0;
		rateType[0] = new Option('Pivot','Pivot');
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
	var percent  = document.getElementById("percent");
	var absolute = document.getElementById("absolute");
	
  if(document.forms[0].overAllMargin!=null && document.forms[0].marginType!=null)
  {	
	if((document.forms[0].overAllMargin.options.value=='No') || (document.forms[0].overAllMargin.options.value=='Yes' && document.forms[0].marginType.options.value=='Absolute'))
	{
		percent.style.display="none";
		absolute.style.display="block";
	}
	else
	{
		absolute.style.display="none";
		percent.style.display="block";
	}
  }
}

function checkValidity(obj)
{
	if(obj.name=='effectiveFrom' && obj.value.length > 0 && !chkFutureDate(obj,"gt"))
	{
		alert('Please Enter Effective From Date greater than or equal to Current Date');
		obj.focus();
		return false;
	}
	else if(obj.name=='validUpto' && obj.value.length > 0 && document.forms[0].effectiveFrom.value.length > 0 && !chkFromToDate(document.forms[0].effectiveFrom.value, obj.value))
	{
		alert('Please Enter Valid Upto Date greater than or equal to Effective From Date');
		obj.focus();
		return false;
	}
}
function setName(name)
{
	document.forms[0].submitName.value	= name;
	
	if(name=="Next>>")
		document.forms[0].subOperation.value='setFlatSellRates';
	else if(name=="Search")
		document.forms[0].subOperation.value='Details';
	else if(name = 'Continue')
		document.forms[0].subOperation.value='Continue';
}
function disableSubmit()
{
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].value=="Next>>")
		{
			obj[i].disabled=true;
		}
	}
}
function showMargins(cartageId,zoneCode,chargeType,chargeBasis)
{
	if(cartageId==0)
	{
		alert('No Buy/Sell Charge is Defined for this Combination');
		return false;
	}
	var locationId  = document.forms[0].locationId.value;
//	var zoneCode    = document.forms[0].zoneCode.value;
	var rateType    = document.forms[0].rateType.value;
	var weightBreak = document.forms[0].weightBreak.value;
	//var chargeBasis = document.forms[0].chargeBasis.value;
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
	
	/*if(flag)
		document.forms[0].consoleType.options[0].selected = true;*/
	
	if(document.forms[0].shipmentMode.value=='2')
		console.style.display="block";
	else
	{
		console.style.display="none";
		document.forms[0].consoleType.options[0].selected = true;
		/*if(flag)
		{
			weightBreak.length=0;
			weightBreak[0] = new Option("Flat","Flat");
			weightBreak[1] = new Option("Slab","Slab");
			displaySlab();
		}
		document.getElementById("maxCharge").disabled=false;*/
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

<body onload='changeRateOptions();changeConsoleVisibility();<%=onLoadCalls%>'>
	<form method='post' onsubmit='return Mandatory()' action='QMSCartageController'>

	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				Cartage Sell Charges - <%=("selladd".equalsIgnoreCase(operation))?"Add":("sellModify".equalsIgnoreCase(operation))?"Modify":"View"%> </td><td align="right">QS1050811</td></tr></table></td>
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
                <select size="1" name="chargeType" class='select' onchange='disableSubmit()'>
					<option value="Pickup" <%="Pickup".equals(chargeType)?"selected":""%>>Pickup</option>
					<option value="Delivery" <%="Delivery".equals(chargeType)?"selected":""%>>Delivery</option>
					<option value="Both" <%="Both".equals(chargeType)?"selected":""%>>Both</option>
                </select>
			  </td>
			  <!--td>Charge Basis:<br>
				<select name="chargeBasis" class='select' onchange='changeUOMOptions();disableSubmit()'>
					<option value='Weight' <%="Weight".equals(chargeBasis)?"selected":""%>>Weight</option>
					<option value='Volume' <%="Volume".equals(chargeBasis)?"selected":""%>>Volume</option>
                </select>
			 </td>
			  <td>UOM: <br>
              <select name="uom" class='select' onchange='disableSubmit()'>
                <option selected value="Kg">KG</option>
				<option value="Lb">LBS</option>
              </select>
			 </td-->
           <td>Weight Break: <font color="#FF0000">*</font><br>
              <select name="weightBreak" id="weightBreak" class='select' onchange='changeRateOptions();disableSubmit()'>
                <option value="Flat" <%="Flat".equals(weightBreak)?"selected":""%>>Flat</option>
                <option value="Slab" <%="Slab".equals(weightBreak)?"selected":""%>>Slab</option>
			 </select>
		  </td>
		  <td colspan='2'>Rate Type: <font color="red">*</font><br>
              <select name="rateType" id="rateType" class='select' onchange='disableSubmit()'>
                <option selected value="Flat">Flat</option>
                <option value="Slab">Slab</option>
			 </select>
		 </td>
		</tr>         
			  
		   <tr valign="top" class='formdata'>
			<td colspan="2">Location Id: <font  color="red">*</font><br>
				<input type='text' class='text' name="locationId" size="10" onblur='toUpperCase(this)' value='<%=locationId!=null?locationId:""%>'>
				<input type="button" value="..." name="locationIdLOV" class='input' onclick=openLocationLov()>
			</td>
			<td>Zone Codes:<font color="#FF0000">*</font><br>
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
			<td colspan='3'>Currency:<br>
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
				<input name="submit" type="Submit" value="Search" class='input' onclick="setName(this.value)">
			  </td>
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="subOperation">
				<input type="hidden" name="submitName">			   
			</tr>
		</table>
<%
	if(subOperation != null && request.getAttribute("Errors")==null)
	{
%>
	<table width="100%" cellpadding="4" cellspacing="1">
<%
	if(!"Both".equals(chargeType))
	{
		if(counter==0)
		{
%>			<tr bgcolor="#FFFFFF"> 
				<td colspan="13" align="center">
				<font face="Verdana" size="2" color='red'>
				<b>No Charges Are Defined for the Specified Details.</b></font>
				</td>
			</tr>
<%
		}
		else
		{
%>				
		<tr valign="top" class='formheader' align='center'>
		<%if(!"sellView".equalsIgnoreCase(operation)){%>
            <td rowspan="2" width='10%'>Select<br><input type='checkbox' name='select' onclick='selectAll()'></td>
			<%}%>
            <td rowspan="2" width='10%'>Zone Code</td>
            <td rowspan="2" width='10%'>Effective From<br>(<%=dateFormat%>)</td>
            <td rowspan="2" width='10%'>Valid Upto<br>(<%=dateFormat%>)</td>
            <td rowspan="2" width='10%'>Charge Basis</td>
<!--             <td colspan="5"><%=label%></td> --><!-- @@ Commented by subrahmanyam for Enhancement 170759 on 02/06/09 -->
            <td colspan="7"><%=label%></td><!-- @@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
		</tr>
        
		<tr valign="top" class='formheader' align='center'>
			<td><b>Base</b></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
            <td><b>Min</b></td>
            <td><b>Flat</b></td>
			<td><b>Max</b></td>
			<td><b>Density Ratio</b></td>
			<%if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation)){%>
			<td>Margin Details<br><%if(!"sellView".equalsIgnoreCase(operation)){%>(Previous)<%}%></td>
			<%}%>
		</tr>

<%		for(int i=0;i<counter;i++)
		{
			if("Delivery".equals(chargeType))
				cartageBuyDtl	=	(QMSCartageBuyDtlDOB)delList.get(i);
			else
				cartageBuyDtl	=	(QMSCartageBuyDtlDOB)pickUpList.get(i);
				
				baseRate			=  cartageBuyDtl.getBaseRate();
				minRate				=  cartageBuyDtl.getMinRate(); 
				flatRate			=  cartageBuyDtl.getFlatRate();
				maxRate				=  cartageBuyDtl.getMaxRate();
				effectiveFrom		=  cartageBuyDtl.getEffectiveFrom();
				validUpto			=  cartageBuyDtl.getValidUpto();
				chargeBasis			=  cartageBuyDtl.getChargeBasis();
				//System.out.println(cartageBuyDtl.getDensityRatio()+"***********1");
				densityRatio        =  cartageBuyDtl.getDensityRatio()!=null?cartageBuyDtl.getDensityRatio():"";
				str					=  eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
				effectiveFromStr	=  str[0];
				str					=  null;

				str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
				validUptoStr		= str[0];
				str					= null;
%>
		<tr valign="top" class='formdata' align='center'>
<%		if(!"sellView".equalsIgnoreCase(operation))
		{
%>
            <td><input type="checkbox" name="chkBox" onclick='setValue("<%=i%>")'></td>
<%		}
%>
            <td>Zone <%=cartageBuyDtl.getZoneCode()!=null?cartageBuyDtl.getZoneCode():""%></td>
            <td nowrap><%=effectiveFromStr!=null?effectiveFromStr:"-"%></td>
            <td nowrap><%=validUptoStr!=null?validUptoStr:"-"%></td>
            <td nowrap><%=chargeBasis!=null?chargeBasis:""%></td>
			<td><%=df.format(baseRate)%></td><!-- @@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
            <td><%=df.format(minRate)%></td>
            <td><%=df.format(flatRate)%></td>
			<td><%=df.format(maxRate)%></td>
			<td><%=densityRatio%></td>

<%			if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation))
			{
%>
				<td ><input type='button' value='View' name='View' class='input' onClick = "showMargins('<%=cartageBuyDtl.getCartageId()%>','<%=cartageBuyDtl.getZoneCode()%>','<%=cartageBuyDtl.getChargeType()%>','<%=chargeBasis%>')"></td>
<%			}
%>
			<input type='hidden' name='baseRate'   value='<%=baseRate%>'><!-- @@Added by subrahmanyam for the Enhancement  on 02/06/09 -->
			<input type='hidden' name='minRate'   value='<%=minRate%>'>
			<input type='hidden' name='flatRate'  value='<%=flatRate%>'>
			<input type='hidden' id='maxRate<%=i%>' name='maxRate'   value='<%=maxRate%>'>
			<input type='hidden' name='cartageId' value='<%=cartageBuyDtl.getCartageId()%>'>
			<input type='hidden' name='chargeBasis' value='<%=chargeBasis!=null?chargeBasis:""%>'>
			<input type='hidden' name='effectiveFrom' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>
			<input type='hidden' name='validUpto' value='<%=validUptoStr!=null?validUptoStr:""%>'>
			<input type='hidden' name='zoneCode'  value='<%=cartageBuyDtl.getZoneCode()!=null?cartageBuyDtl.getZoneCode():""%>'>
			<input type='hidden' name='checkBoxValue'>
			<input type='hidden' name='chargeType<%=i%>' value='<%=cartageBuyDtl.getChargeType()%>'>
		</tr>
<%
		}
	  }
	}
	else 
	{
		if(counter==0)
		{
%>			<tr bgcolor="#FFFFFF"> 
				<td colspan="13" align="center">
				<font face="Verdana" size="2" color='red'>
				<b>No Charges Are Defined for the Specified Details.</b></font>
				</td>
			</tr>
<%
		}
		else
		{
%>		<tr valign="top" class='formheader' align='center'>
		<%if(!"sellView".equalsIgnoreCase(operation)){%>
            <td rowspan="2" width='5%'>Select<br><input type='checkbox' name='select' onclick='selectAll()'></td>
			<%}%>
            <td rowspan="2" width='5%'>Zone Code</td>
			<td colspan="<%="sellModify".equalsIgnoreCase(operation)?"10":"9"%>" width='45%'>PICKUP CHARGES</td>
			<!-- <td colspan='9' width='45%'>DELIVERY CHARGES</td> --><!-- @@Commented by subrahmanyam for the Enhancement 170759 on 03/06/09 -->
			<td colspan='10' width='45%'>DELIVERY CHARGES</td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 03/06/09 -->
		</tr>
        
		<tr valign="top" class='formheader' align='center'>
            <td width='10%'><b>Effective From</b></td>
            <td width='10%'><b>Valid Upto</b></td>
            <td width='10%'><b>Charge Basis</b></td>
			<td><b>Base</b></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
			<td><b>Min</b></td>
            <td><b>Flat</b></td>
			<td><b>Max</b></td>
			<td><b>Density Ratio</b></td>
			<%if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation)){%>
        	<td>Margins</td>
			<%}%>
			<td width='10%'><b>Effective From</b></td>
            <td width='10%'><b>Valid Upto</b></td>
            <td width='10%'><b>Charge Basis</b></td>
			<td><b>Base</b></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
			<td><b>Min</b></td>
            <td><b>Flat</b></td>
			<td><b>Max</b></td>
			<td><b>Density Ratio</b></td>
			<%if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation)){%>
        	<td>Margins</td>
			<%}%>
		</tr>

<%		for(int i=0;i<counter;i++)
		{
			//if(i < (pickUpList.size()))
			//{
				pickUpbuyChargesDtl = (QMSCartageBuyDtlDOB)pickUpList.get(i);
				if(pickUpbuyChargesDtl!=null)
				{
					baseRate			=  pickUpbuyChargesDtl.getBaseRate(); //@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
					minRate				=  pickUpbuyChargesDtl.getMinRate(); 
					flatRate			=  pickUpbuyChargesDtl.getFlatRate();
					maxRate				=  pickUpbuyChargesDtl.getMaxRate();
					pickupCartageId		=  pickUpbuyChargesDtl.getCartageId();
					//System.out.println(pickUpbuyChargesDtl.getDensityRatio()+"***********2");
					densityRatio        =  pickUpbuyChargesDtl.getDensityRatio()!=null?pickUpbuyChargesDtl.getDensityRatio():"";
					effectiveFrom		=  pickUpbuyChargesDtl.getEffectiveFrom();
					validUpto			=  pickUpbuyChargesDtl.getValidUpto();
					chargeBasis			=  pickUpbuyChargesDtl.getChargeBasis();
					str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
					effectiveFromStr	= str[0];
					str					= null;

					str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
					validUptoStr		= str[0];
					str					= null;
					zone				= pickUpbuyChargesDtl.getZoneCode()!=null?pickUpbuyChargesDtl.getZoneCode():"";
				}
				else
				{
					baseRate			=  0;//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09
					minRate				=  0; 
					flatRate			=  0;
					maxRate				=  0;
					pickupCartageId		=  0;
					effectiveFromStr	=  null;
					validUptoStr		=  null;
					zone				=  null;
				}
			/*}
			else
			{
				minRate				=  0; 
				flatRate			=  0;
				maxRate				=  0;
				pickupCartageId		=  0;
				effectiveFromStr	=  null;
				validUptoStr		=  null;
				zone				=  null;
			}*/

			//if(i < (delList.size()))
			//{
				delBuyChargesDtl	= (QMSCartageBuyDtlDOB)delList.get(i);
			
				if(delBuyChargesDtl != null)
				{
					dBaseRate			=  delBuyChargesDtl.getBaseRate();//@@Added by subrahmanyam for the Enhancement on 02/06/09
					dMinRate			=  delBuyChargesDtl.getMinRate(); 
					dFlatRate			=  delBuyChargesDtl.getFlatRate();
					dMaxRate			=  delBuyChargesDtl.getMaxRate();
					//System.out.println(delBuyChargesDtl.getDensityRatio()+"***********3");
					dDensityRatio       =  delBuyChargesDtl.getDensityRatio()!=null?delBuyChargesDtl.getDensityRatio():"";
					delCartageId		=  delBuyChargesDtl.getCartageId();
					dEffectiveFrom		=  delBuyChargesDtl.getEffectiveFrom();
					dValidUpto			=  delBuyChargesDtl.getValidUpto();
					dChargeBasis		=  delBuyChargesDtl.getChargeBasis();
					str					=  eSupplyDateUtility.getDisplayStringArray(dEffectiveFrom);
					dEffectiveFromStr	=  str[0];
					str					=  null;

					str					=  eSupplyDateUtility.getDisplayStringArray(dValidUpto);
					dValidUptoStr		=  str[0];
					str					=  null;
					dZone               =  delBuyChargesDtl.getZoneCode()!=null?delBuyChargesDtl.getZoneCode():"";
				}
				else
				{
					//delList.add(i,delBuyChargesDtl);
					dBaseRate			=  0; //@@Added by subrahmanyam for the Enhancement on 02/06/09
					dMinRate			=  0; 
					dFlatRate			=  0;
					dMaxRate			=  0;
					delCartageId		=  0;
					dEffectiveFromStr	=  null;
					dValidUptoStr		=  null;
					dZone				=  null;
				}
			/*}
			else
			{
				dMinRate			=  0; 
				dFlatRate			=  0;
				dMaxRate			=  0;
				delCartageId		=  0;
				dEffectiveFromStr	=  null;
				dValidUptoStr		=  null;
				dZone				=  null;
			}*/
%>
		<tr valign="top" class='formdata' align='center'>
<%		if(!"sellView".equalsIgnoreCase(operation))
		{
%>
            <td><input type="checkbox" name="chkBox" onclick='setValue("<%=i%>")'></td>
<%		}
%>
            <td nowrap>Zone <%=zone!=null?zone:dZone%></td>
            <td nowrap><%=effectiveFromStr!=null?effectiveFromStr:"-"%></td>
            <td nowrap><%=validUptoStr!=null?validUptoStr:"-"%></td>
            <td nowrap><%=chargeBasis!=null?chargeBasis:""%></td>
			<td><%="0".equals(Long.toString(pickupCartageId))?"-":df.format(baseRate)%></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
            <td><%="0".equals(Long.toString(pickupCartageId))?"-":df.format(minRate)%></td>
            <td><%="0".equals(Long.toString(pickupCartageId))?"-":df.format(flatRate)%></td>
		    <td><%="0".equals(Long.toString(pickupCartageId))?"-":df.format(maxRate)%></td>
			<td><%=densityRatio%></td>
<%			if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation))
			{
%>
        		<td><input type='button' value='View' class='input' name='View' onClick = "showMargins('<%=pickupCartageId%>','<%=zone%>','<%=pickUpbuyChargesDtl!=null?pickUpbuyChargesDtl.getChargeType():delBuyChargesDtl.getChargeType()%>','<%=chargeBasis%>')"></td>
<%			}
%>
			<td><%=dEffectiveFromStr!=null?dEffectiveFromStr:"-"%></td>
            <td nowrap><%=dValidUptoStr!=null?dValidUptoStr:"-"%></td>
            <td nowrap><%=dChargeBasis!=null?dChargeBasis:""%></td>
			<td><%="0".equals(Long.toString(pickupCartageId))?"-":df.format(dBaseRate)%></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
			<td><%="0".equals(Long.toString(delCartageId))?"-":df.format(dMinRate)%></td>
			<td><%="0".equals(Long.toString(delCartageId))?"-":df.format(dFlatRate)%></td>
			<td><%="0".equals(Long.toString(delCartageId))?"-":df.format(dMaxRate)%></td>
			<td><%=dDensityRatio%></td>
<%			if("sellModify".equalsIgnoreCase(operation)||"sellView".equalsIgnoreCase(operation))
			{
%>
	        	<td>
					<input class='input' type='button' value='View' name='View' onClick = "showMargins('<%=delCartageId%>','<%=dZone%>','<%=delBuyChargesDtl!=null?delBuyChargesDtl.getChargeType():pickUpbuyChargesDtl.getChargeType()%>','<%=dChargeBasis%>')">
				</td>
<%			}
%>
			<input type='hidden' name='baseRate' value='<%=baseRate%>'><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
			<input type='hidden' name='minRate' value='<%=minRate%>'>
			<input type='hidden' name='flatRate' value='<%=flatRate%>'>
			<input type='hidden' id='maxRate<%=i%>' name='maxRate' value='<%=maxRate%>'>
			<input type='hidden' name='dBaseRate' value='<%=dBaseRate%>'><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
			<input type='hidden' name='dMinRate' value='<%=dMinRate%>'>
			<input type='hidden' name='dFlatRate' value='<%=dFlatRate%>'>
			<input type='hidden' id='dMaxRate<%=i%>' name='dMaxRate' value='<%=dMaxRate%>'>
			<input type='hidden' name='cartageId' value='<%="0".equals(Long.toString(pickupCartageId))?"-":Long.toString(pickupCartageId)%>'>
			<input type='hidden' name='dCartageId' value='<%="0".equals(Long.toString(delCartageId))?"-":Long.toString(delCartageId)%>'>
			<input type='hidden' name='chargeBasis' value='<%=chargeBasis!=null?chargeBasis:""%>'>
			<input type='hidden' name='dChargeBasis' value='<%=dChargeBasis!=null?dChargeBasis:""%>'>
			<input type='hidden' name='effectiveFrom' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>
			<input type='hidden' name='validUpto' value='<%=validUptoStr!=null?validUptoStr:""%>'>
			<input type='hidden' name='dEffectiveFrom' value='<%=dEffectiveFromStr!=null?dEffectiveFromStr:""%>'>
			<input type='hidden' name='dValidUpto' value='<%=dValidUptoStr!=null?dValidUptoStr:""%>'>
			<input type='hidden' name='zoneCode' value='<%=delBuyChargesDtl!=null?delBuyChargesDtl.getZoneCode():pickUpbuyChargesDtl.getZoneCode()%>'>
			<input type='hidden' name='checkBoxValue'>
			<input type='hidden' name='dChargeType<%=i%>' value='<%=pickUpbuyChargesDtl!=null?pickUpbuyChargesDtl.getChargeType():delBuyChargesDtl.getChargeType()%>'>
		</tr>
<%
		}
	  }
	}
		if(counter!=0)
		{
%>
		<%if(!"sellView".equalsIgnoreCase(operation)){%>
        <tr valign="top" class='formheader'>
			<!-- <td colspan="16">Margin Calculations</td> --><!-- @@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
            <td colspan="18">Margin Calculations</td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
		</tr>
        <tr valign="top" class='formdata'>
            <td colspan="5">Overall Margin:<br>
                <select size="1" name="overAllMargin" class='select' onchange='displayMargin()'>
					<option value="Yes" selected>YES</option>
					<option  value="No">NO</option>               
				</select>
			</td>
			<!-- <td colspan="11">Margin Type: <br> --><!-- @@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
            <td colspan="13">Margin Type: <br><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
              <select size="1" name="marginType" class='select' onchange='displayMargin()'>
                <option value="Absolute" selected>ABSOLUTE</option>
				<option value="Percentage">PERCENT</option>
			 </select>
			</td>
		 </tr>
		 </table>
		
		<div id='percent' style="DISPLAY:none">
			<table width='100%' cellpadding='4' cellspacing='1'>
				<tr valign='top' class='formdata'>
					<td width='<%="Both".equals(chargeType)?"55%":"40%"%>' align='right'><b>Margin&nbsp;</b></td>
					<td colspan='6' align='center'><input type='text' class='text' name='marginPercent' size='5' maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
				</tr>
			</table>
		</div>

		<div id='absolute' style="DISPLAY:none">
			<table width='100%' cellpadding='4' cellspacing='1'>
				<tr valign='top' class='formdata'>
<%			if("Both".equals(chargeType) && pickUpList.size()!=0)
			{
%>
					<td width='31%' align='right'><b>Margin&nbsp;</b></td>
<!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
				
					<td align='center'><input type='text' class='text' name="baseMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
				
				<input type='hidden' value='<%=baseRate%>'>
<!-- @@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09 -->

					<td align='center'><input type='text' class='text' name="minMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="flatMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="maxMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
<%			}
			else if(!"Both".equals(chargeType))
			{
%>					<td width='40%' align='right'><b>Margin&nbsp;</b></td>
<!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
				
					<td align='center'><input type='text' class='text' name="baseMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					

<!-- @@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09 -->

					<td align='center'><input type='text' class='text' name="minMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="flatMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="maxMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
<%
			}
			if("Both".equals(chargeType) && delList.size()!=0 && pickUpList.size()==0)
			{
%>  
					<td align='right' width='75%' colspan='2'>Margin&nbsp;</td>
<!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
				
					<td align='center'><input type='text' class='text' name="dBaseMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					
<!-- @@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
					<td align='center'><input type='text' class='text' name="dMinMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="dFlatMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="dMaxMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
<%			}
			else if("Both".equals(chargeType) && delList.size()!=0 && pickUpList.size()!=0)
			{
%>				<td align='center' width='20%' colspan='2'>&nbsp;</td>
<!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
	
					<td align='center'><input type='text' class='text' name="dBaseMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					
<!-- @@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09 -->

					<td align='center'><input type='text' class='text' name="dMinMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="dFlatMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
				<td align='center'><input type='text' class='text' name="dMaxMargin" size="5" maxlength='6' onBlur='getKeyCode(this);return chrnum(this)'></td>
<%			}
			else if("Both".equals(chargeType) && delList.size()==0)
			{
%>					<td align='center' width='40%' colspan='5' maxlength='6'>&nbsp;</td>
<%
			}
%>
				</tr>
				<%}%>
			</table>
		</div>
		
		<table width='100%' cellpadding='4' cellspacing='1'>

         <tr valign="top" class='text'>
            <td colspan="8">* Denotes Mandatory</td>
            <td colspan="2" align='right'>
			<%if(!"sellView".equalsIgnoreCase(operation)){%>
				<input type="Reset" value="Reset" class='input'>
				<input type="submit" name="Submit" value="Next>>" class='input' onclick="setName(this.value)">
				<%}else {%>
				<input type="submit" name="Continue" value="Continue" class='input' onclick="setName(this.value)">
				<%}%>
			</td>
		</tr>
<%
		}
%>
	  </table>
<%
	}
%>
 </body>
</form>
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
%>ss
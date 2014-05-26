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
      private static final  String  FILE_NAME = "QMSCartageBuyChargesModify.jsp" ;
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
    String				delChargeBasis			= null;
    String				unitofMeasure			= null;
    String				weightBreak				= null;
    String				rateType				= null;
	String[]			zoneCodes				= null;
	String				maxChargeFlag			= "";
	String				delMaxChargeFlag		= "";

	double				minRate					= 0;
	double				flatRate				= 0;
	double				maxRate					= 0;
	
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
	String[]			 zoneCodes1				= null;
	String				pickZoneCode			= null;
	String				delZoneCode				= null;

	long				pickupCartageId			= 0;
	long				delCartageId			= 0;
	boolean				displayMarginFlag		= true;

	int					counter					= 0;
	String				labelButton				= null;
	String				densityRatio			= null;
	String				delDensityRatio			= null;
	String				consoleType				= null;

	String				readOnly				= "";

//@@ Added by subrahmanyam for the Enhancement 170759 on 25/May/09
	double				baseRate					= 0;
	double				dBaseRate					= 0;
//@@ Ended by subrahmanyam for the Enhancement 170759 on 25/May/09
	
	java.text.DecimalFormat df	=new java.text.DecimalFormat("######0.00");
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
		//Logger.info(FILE_NAME, " subOperation ", subOperation);

		if(subOperation!=null && request.getAttribute("Errors")==null)
			onLoadCalls = ";displayMargin()";

		cartageMaster			=	(QMSCartageMasterDOB)session.getAttribute("cartageMaster");

		if(cartageMaster!=null)
		{
			zoneCodes	    = cartageMaster.getZoneCodes();
			locationId      = cartageMaster.getLocationId();
			currencyId      = cartageMaster.getCurrencyId();
			//vendorIds       = cartageMaster.getVendorIds();
			weightBreak		= cartageMaster.getWeightBreak();
			rateType		= cartageMaster.getRateType();
			//zoneCodes1		= cartageMaster.getZoneCodes();
			chargeType		= cartageMaster.getChargeType();
			//chargeBasis		= cartageMaster.getChargeBasis();
			//unitofMeasure	= cartageMaster.getUom();
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
		if(operation.equals("buyModify"))
			labelButton="Modify";
		else if(operation.equals("buyView"))
		{
			
			labelButton="Continue";
			readOnly   ="readOnly";
		}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Cartage Buy Charges</title>
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
	else if(document.forms[0].submitName.value=="Modify")
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

function setName(name)
{
	document.forms[0].submitName.value	= name;
	
	if(name=="Modify")
		document.forms[0].subOperation.value='flatAdd';
	else if(name=="Search")
		document.forms[0].subOperation.value='Details';
	
}
function disableSubmit()
{
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].value=="Modify")
		{
			obj[i].disabled=true;
		}
	}
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
function viewContinue()//added by rk
{
	document.forms[0].subOperation.disabled=true;
	document.forms[0].action='<%=request.getContextPath()%>/QMSCartageController?Operation=buyView';
	document.forms[0].submit();
	return true;
	
}
function openLocationLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	var operation	=	document.forms[0].Operation.value;

	formArray = 'locationId';

	if(operation.toUpperCase()=='BUYVIEW')
		operation	=	"LOCSETUPVIEW";
	else
		operation	=	"CARTAGELOC";
	
	tabArray = 'LOCATIONID';
	Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation="+operation+"&search= where LOCATIONID LIKE '"+document.forms[0].locationId.value+"~'";


	Options	='width=750,height=750,resizable=yes';
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
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
			    <td colspan="4">

				<table width="100%" border="0" ><tr class='formlabel'><td>
				Cartage Buy Charges - <%="buyView".equalsIgnoreCase(operation)?"View":"Modify"%> </td><td align="right">QS1050721</td>
				</tr>
				</table>
			

				</td>
			
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
			<td colspan='2'>Location Id: <font  color="red">*</font><br>
				<input type='text' class='text' name="locationId" size="10" onblur='toUpperCase(this)' value='<%=locationId!=null?locationId:""%>'>
				<input type="button" value="..." name="locationIdLOV" class='input' onclick='openLocationLov()'>
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
			<td colspan='4'>Currency:<font  color="red">*</font><br>
				<input type='text' class='text' name='baseCurrency' size='10' onblur='toUpperCase(this)' value='<%=currencyId!=null?currencyId:""%>'>
                <input type='button' value='...' name='creditcurrency' onClick='showCurrencyLOV()' class='input'>
            </td>
		  </tr>

			<tr valign="top" class='denotes'>
			   <td colspan="5" align='left'>
			   <font  color="red">*</font> Denotes Mandatory <br>
			  </td>
			   <td align='right'>
				<input name="submit1" type="Submit" value="Search" class='input' onclick="setName(this.value)">
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
<%		if("buyModify".equals(operation))
		{
%>
            <td rowspan="2" width='10%'>Select<br><input type='checkbox' name='select' onclick='selectAll()'></td>
<%		}
%>
          <td rowspan="2" width='10%'>Zone Code:</td>
            <td rowspan="2" width='10%'>Effective From<br>(<%=dateFormat%>)</td>
            <td rowspan="2" width='10%'>Valid Upto<br>(<%=dateFormat%>)</td>
            <td rowspan="2" width='10%'>Charge Basis<br></td>
			<!-- @@@@ Commented by subrahmanyam for Enhancement 170759 on 25/may/09 -->
<!--             <td colspan="4"><%=label%></td> -->
            <td colspan="5"><%=label%></td><!-- @@ Added by subrahmanyam for Enhancement 170759 -->
		</tr>
        
		<tr valign="top" class='formheader' align='center'>
		<!-- @@ Added by subrahmanyam for Enhancement 170759 on 25/May/09 -->
		<td><b>Base</b></td>
		<!-- @@Ended for 170759 -->
            <td><b>Min</b></td>
            <td><b>Flat</b></td>
			<td><b>Max</b></td>
			<td><b>Density Ratio</b></td>
		</tr>

<%	
	
	for(int i=0;i<counter;i++)
		{
			if("Delivery".equals(chargeType))
				cartageBuyDtl	=	(QMSCartageBuyDtlDOB)delList.get(i);
			else
				cartageBuyDtl		=	(QMSCartageBuyDtlDOB)pickUpList.get(i);
				baseRate			=	cartageBuyDtl.getBaseRate();//@@Added by subrahmanyam for 170759 on 25/may/09
				minRate				=  cartageBuyDtl.getMinRate(); 
				flatRate			=  cartageBuyDtl.getFlatRate();
				maxRate				=  cartageBuyDtl.getMaxRate();
				effectiveFrom		=  cartageBuyDtl.getEffectiveFrom();
				validUpto			=  cartageBuyDtl.getValidUpto();
				chargeBasis			=  cartageBuyDtl.getChargeBasis();

				str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
				effectiveFromStr	= str[0];
				str					= null;

				str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
				validUptoStr		= str[0];
				str					= null;
%>
		<tr valign="top" class='formdata' align='center'>
<%			if("buyModify".equals(operation))
			{
%>
				<td><input type="checkbox" name="chkBox" onclick='setValue("<%=i%>")'></td>
<%			}
%>
            <td>Zone <%=cartageBuyDtl.getZoneCode()!=null?cartageBuyDtl.getZoneCode():""%></td>
            <td><%=effectiveFromStr!=null?effectiveFromStr:"-"%></td>
            <td><%=validUptoStr!=null?validUptoStr:"-"%></td>
            <td><%=chargeBasis!=null?chargeBasis:""%></td>
			<input type='hidden' name='effectiveFrom' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>
			<input type='hidden' name='validUpto' value='<%=validUptoStr!=null?validUptoStr:""%>'>
			<!--@@ Added by subrahmanyam for 170759 on 25/May/09  -->
			 <td><input type="text" name="baseRt" class="text" size="10" maxlength='8' value='<%=baseRate%>' <%=readOnly%>></td>
			<!--For 170759  -->
            <td><input type="text" name="minRt" class="text" size="10" maxlength='8' value='<%=minRate%>' <%=readOnly%>></td>
            <td><input type="text" name="flatRt" class="text" size="10" maxlength='8' value='<%=flatRate%>' <%=readOnly%>></td>
			<td><input type="text" name="maxRt" class="text" size="10" maxlength='8' value='<%=df.format(maxRate)%>' <%=readOnly%>></td>
			<td><input type="text" name="denRatio" class="text" maxlength='25' size="10" value='<%=cartageBuyDtl.getDensityRatio()!=null?cartageBuyDtl.getDensityRatio():""%>' readOnly></td>
			<input type='hidden' name='densityRatio<%=i%>' value='<%=cartageBuyDtl.getDensityRatio()!=null?cartageBuyDtl.getDensityRatio():""%>'>
			<input type='hidden' name='cartageId' value='<%=cartageBuyDtl.getCartageId()%>'>
			<input type='hidden' name='zoneCode' value='<%=cartageBuyDtl.getZoneCode()!=null?cartageBuyDtl.getZoneCode():""%>'>
			<input type='hidden' name='chargeBasis' value='<%=chargeBasis!=null?chargeBasis:""%>'>
			<input type='hidden' name='checkBoxValue'>
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
<%		if("buyModify".equals(operation))
		{
%>
			<td rowspan="2" width='5%'>Select<br><input type='checkbox' name='select' onclick='selectAll()'></td>
<%		}
%>
            <td rowspan="2" width='5%'>Zone Code</td>
			<td colspan='8' width='45%'>PICKUP CHARGES</td>
<!-- @@ Commented by subrahmanyam for wpbn id: 170759 -->
			<!-- <td colspan='7' width='45%'>DELIVERY CHARGES</td> -->
            <td colspan='8' width='45%'>DELIVERY CHARGES</td><!-- @@ Added by subrahmanyam for wpbn id: 170759 -->
		</tr>
        
		<tr valign="top" class='formheader' align='center'>
            <td width='10%'><b>Effective From</b></td>
            <td width='10%'><b>Valid Upto</b></td>
            <td width='10%'><b>Charge Basis</b></td>
			<td><b>Base</b></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 25/May/09 -->
			<td><b>Min</b></td>
            <td><b>Flat</b></td>
			<td><b>Max</b></td>
			<td><b>Density Ratio</b></td>
			<td width='10%'><b>Effective From</b></td>
            <td width='10%'><b>Valid Upto</b></td>
            <td width='10%'><b>Charge Basis</b></td>
			<td><b>Base</b></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 25/May/09 -->
			<td><b>Min</b></td>
            <td><b>Flat</b></td>
			<td><b>Max</b></td>
			<td><b>Density Ratio</b></td>
		</tr>

<%	
	String strreadonly=null;
	String strreadonly1=null;
	zoneCodes1=new String[counter];
		for(int i=0;i<counter;i++)
		{
				pickUpbuyChargesDtl = (QMSCartageBuyDtlDOB)pickUpList.get(i);
				if(pickUpbuyChargesDtl != null)
				{
					baseRate			=  pickUpbuyChargesDtl.getBaseRate();//@@ Added by subrahmanyam for Enhancement 170759 on 25/May/09
					minRate				=  pickUpbuyChargesDtl.getMinRate(); 
					flatRate			=  pickUpbuyChargesDtl.getFlatRate();
					maxRate				=  pickUpbuyChargesDtl.getMaxRate();
					pickupCartageId		=  pickUpbuyChargesDtl.getCartageId();
					pickZoneCode		=  pickUpbuyChargesDtl.getZoneCode();
					effectiveFrom		=  pickUpbuyChargesDtl.getEffectiveFrom();
					validUpto			=  pickUpbuyChargesDtl.getValidUpto();
					densityRatio		=  pickUpbuyChargesDtl.getDensityRatio();
					chargeBasis			=  pickUpbuyChargesDtl.getChargeBasis();

					str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
					effectiveFromStr	= str[0];
					str					= null;

					str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
					validUptoStr		= str[0];
					str					= null;
				}
				else
				{
					baseRate			=  0; //@@Added by subrahmanyam for Enhancement 170759 on 25/May/09
					minRate				=  0; 
					flatRate			=  0;
					maxRate				=  0;
					pickupCartageId		=  0;
					effectiveFromStr	=  null;
					validUptoStr		=  null;
					pickZoneCode		=  null;
				}
			
				delBuyChargesDtl	= (QMSCartageBuyDtlDOB)delList.get(i);
				if(delBuyChargesDtl!=null)
				{
					dBaseRate			=  delBuyChargesDtl.getBaseRate(); //@@Added by subrahmanyam for Enhancement 170759 on 25/May/09
					dMinRate			=  delBuyChargesDtl.getMinRate(); 
					dFlatRate			=  delBuyChargesDtl.getFlatRate();
					dMaxRate			=  delBuyChargesDtl.getMaxRate();
					delZoneCode			=  delBuyChargesDtl.getZoneCode();
					delCartageId		=  delBuyChargesDtl.getCartageId();
					delChargeBasis		=  delBuyChargesDtl.getChargeBasis();
					
					dEffectiveFrom		=  delBuyChargesDtl.getEffectiveFrom();
					dValidUpto			=  delBuyChargesDtl.getValidUpto();

					str					= eSupplyDateUtility.getDisplayStringArray(dEffectiveFrom);
					dEffectiveFromStr	= str[0];
					str					= null;

					str					= eSupplyDateUtility.getDisplayStringArray(dValidUpto);
					dValidUptoStr		= str[0];
					str					= null;

					delDensityRatio		= delBuyChargesDtl.getDensityRatio();
				}
				else
				{
					dBaseRate			=  0;//@@Added by subrahmanyam for Enhancement 170759 on 25/May/09
					dMinRate			=  0; 
					dFlatRate			=  0;
					dMaxRate			=  0;
					delCartageId		=  0;
					dEffectiveFromStr	=  null;
					dValidUptoStr		=  null;
					delZoneCode			=  null;
				}
%>
<%	
		strreadonly="";
		strreadonly1="";

		if("0".equals(""+pickupCartageId))
			strreadonly1="readOnly";
		else
			strreadonly1="";
		if("0".equals(""+delCartageId))
			strreadonly="readOnly";
		else
			strreadonly="";
%>
		<tr valign="top" class='formdata' align='center'>
<%		if("buyModify".equals(operation))
		{
%>
            <td><input type="checkbox" name="chkBox" onclick='setValue("<%=i%>")'></td>
<%		}
%>
            <td nowrap>Zone <%=pickZoneCode!=null?pickZoneCode:delZoneCode%></td>
            <td nowrap><%=effectiveFromStr!=null?effectiveFromStr:"-"%></td>
            <td nowrap><%=validUptoStr!=null?validUptoStr:"-"%></td>
            <td nowrap><%=chargeBasis!=null?chargeBasis:""%></td>
			<input type='hidden' name='effectiveFrom' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>
			<input type='hidden' name='validUpto' value='<%=validUptoStr!=null?validUptoStr:""%>'>
			<!--@@Added by subrahmanyam for Enhancement 170759 on 25/may/09  -->
			 <td><input type="text" value='<%=baseRate%>' name="baseRt" class="text" size='5' maxlength='8' <%=strreadonly1%> <%=readOnly%>></td>
			<!-- @@Ended by subrahmanyam for Enhancement 170759 on 25/may/09 -->
            <td><input type="text" value='<%=minRate%>' name="minRt" class="text" size='5' maxlength='8' <%=strreadonly1%> <%=readOnly%>></td>
            <td><input type="text" value='<%=flatRate%>' name="flatRt" class="text" size='5' maxlength='8' <%=strreadonly1%> <%=readOnly%>></td>
		    <td><input type="text" value='<%=df.format(maxRate)%>' name="maxRt" class="text" size='5' maxlength='8' <%=strreadonly1%> <%=readOnly%>></td>
			<td><%=densityRatio!=null?densityRatio:""%></td>
			<td nowrap><%=dEffectiveFromStr!=null?dEffectiveFromStr:"-"%></td>
            <td nowrap><%=dValidUptoStr!=null?dValidUptoStr:"-"%></td>
			<td nowrap><%=delChargeBasis!=null?delChargeBasis:""%></td>
			<!--@@Added by subrahmanyam for Enhancement 170759 on 25/may/09  -->
					<td><input name="dBaseRt" type="text" class="text" size='5' maxlength='8' <%=strreadonly%> value='<%=dBaseRate%>' <%=readOnly%>></td>
			<!-- @@Ended by subrahmanyam for Enhancement 170759 on 25/may/09 -->
			<td><input name="dMinRt" type="text" class="text" size='5' maxlength='8' <%=strreadonly%> value='<%=dMinRate%>' <%=readOnly%>></td>
			<td><input type="text" name="dFlatRt" class="text" size='5' maxlength='8' <%=strreadonly%> value='<%=dFlatRate%>' <%=readOnly%>></td>
			<td><input type="text" name="dMaxRt" class="text" size='5' maxlength='8' <%=strreadonly%> value='<%=df.format(dMaxRate)%>' <%=readOnly%>></td>
			<td><%=delDensityRatio!=null?delDensityRatio:""%></td>
			<input type='hidden' name='densityRatio<%=i%>' value='<%=densityRatio!=null?densityRatio:""%>'>
			<input type='hidden' name='dDensityRatio<%=i%>' value='<%=delDensityRatio!=null?delDensityRatio:""%>'>
			<input type='hidden' name='chargeBasis' value='<%=chargeBasis!=null?chargeBasis:""%>'>
			<input type='hidden' name='delChargeBasis' value='<%=delChargeBasis!=null?delChargeBasis:""%>'>
			<input type='hidden' name='effectiveFrom' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>
			<input type='hidden' name='validUpto' value='<%=validUptoStr!=null?validUptoStr:""%>'>
			<input type='hidden' name='dEffectiveFrom' value='<%=dEffectiveFromStr!=null?dEffectiveFromStr:""%>'>
			<input type='hidden' name='dValidUpto' value='<%=dValidUptoStr!=null?dValidUptoStr:""%>'>
			<input type='hidden' name='zoneCode' value='<%=pickZoneCode!=null?pickZoneCode:delZoneCode%>'>
			<input type='hidden' name='cartageId' value='<%="0".equals(Long.toString(pickupCartageId))?"-":Long.toString(pickupCartageId)%>'>
			<input type='hidden' name='dCartageId' value='<%="0".equals(Long.toString(delCartageId))?"-":Long.toString(delCartageId)%>'>
			<input type='hidden' name='checkBoxValue'>
		</tr>
<%
		}
	  }
	}
		
%>
<%	
	if(counter!=0)
	{
%>
<table width='100%' cellpadding='4' cellspacing='1'>

         <tr valign="top" class='text'>
            <td colspan="8">* Denotes Mandatory</td>
            <td colspan="2" align='right'>
			<%if(!"buyView".equalsIgnoreCase(operation)){%>
				<input type="Reset" value="Reset" class='input'>
				<input type="submit" name="Submit1" value='<%=labelButton%>' class='input' onclick="setName(this.value)">
				
				<%}else{%>
				<input type="button" name="Submit1" value='<%=labelButton%>' class='input' onclick="viewContinue()">
				<%}%>
			</td>
		</tr>
	  </table>
<%}
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
%>
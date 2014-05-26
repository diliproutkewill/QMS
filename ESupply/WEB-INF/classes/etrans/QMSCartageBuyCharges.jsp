<%--
% 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.0 
%
--%>
<%@ page import = "javax.naming.InitialContext,
			java.util.ArrayList,
			java.util.HashMap,
			java.util.Iterator,
			java.sql.Timestamp,
			com.qms.operations.charges.java.QMSCartageMasterDOB,
			org.apache.log4j.Logger,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue,
			com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
      private static Logger logger = null;
      private static final  String  FILE_NAME = "QMSCartageBuyCharges.jsp" ;
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility		= new ESupplyDateUtility();
    String              dateFormat				= loginbean.getUserPreferences().getDateFormat();
	String				terminalId				= loginbean.getTerminalId();
	String				operation				= null;
	String 				shipmentMode			= null;
	String				fromWhere				= null;
	ErrorMessage		errorMessageObject		= null;
	ArrayList			keyValueList			= new ArrayList();

	QMSCartageMasterDOB	cartageMaster			= null;

	String				locationId				= null;
    String[]			zoneCodes				= null;
	String[]			slabRates				= null;
	String[]			listValues				= null;
	String				str[]					= null;
    String				vendorId				= null;
    Timestamp			effectiveFrom			= null;
    String				effectiveFromStr		= null;
    Timestamp			validUpto				= null;
    String				validUptoStr			= null;
    String				currencyId				= null;
    String				chargeType				= null;
    String				chargeBasis				= null;
    String				unitofMeasure			= null;
    String				weightBreak				= null;
    String				rateType				= null;
    String				maxChargeFlag			= null;
    String				consoleType				= null;

	int					listSize				= 0;

	//session.removeAttribute("cartageMaster");

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
		//shipmentMode			=	request.getParameter("shipmentMode");
		
		cartageMaster			=	(QMSCartageMasterDOB)request.getAttribute("cartageMaster");
		
		if(cartageMaster==null)
			cartageMaster			=	(QMSCartageMasterDOB)session.getAttribute("cartageMaster");
		session.removeAttribute("cartageMaster");

		if(cartageMaster!=null)
		{
			zoneCodes       = cartageMaster.getZoneCodes();
			slabRates       = cartageMaster.getSlabRates();
			locationId      = cartageMaster.getLocationId();
			currencyId      = cartageMaster.getCurrencyId();
			vendorId        = cartageMaster.getVendorId();
			effectiveFrom   = cartageMaster.getEffectiveFrom();
			
			str				= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
			effectiveFromStr= str[0];
			str	=null;
			
			validUpto       = cartageMaster.getValidUpto();

			str				= eSupplyDateUtility.getDisplayStringArray(validUpto);
			validUptoStr	= str[0];
			str	=null;

			weightBreak		= cartageMaster.getWeightBreak();
			rateType		= cartageMaster.getRateType();

			maxChargeFlag	= cartageMaster.getMaxChargeFlag();
			chargeType		= cartageMaster.getChargeType();
			chargeBasis		= cartageMaster.getChargeBasis();
			unitofMeasure	= cartageMaster.getUom();

			shipmentMode	= cartageMaster.getShipmentMode();
			consoleType		= cartageMaster.getConsoleType();
			listValues		= cartageMaster.getListValues();

			if(listValues!=null)
				listSize = listValues.length;
		}
			//Logger.info(FILE_NAME, " operation ", operation);
%>

<html>
<head>
<title>Cartage Buy Charges-Add</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file='../ESEventHandler.jsp' %>
<jsp:include page="ETDateValidation.jsp">
<jsp:param name="format" value="<%=dateFormat%>"/>
</jsp:include>
<script>
function Mandatory()
{
	var msgHeader		= '';
	var msgErrors		= '';
	var focusPosition	= new Array();
	

	msgHeader= '_____________________________________________________\n\n' +
		'This form has not been submitted because of the following error(s).\n' +
		'Please correct the error(s) and re-submit.\n' +
		'_____________________________________________________\n\n';
	if(document.forms[0].locationId.value.length == 0)
	{
		msgErrors += 'Location Id cannot be empty\n';
		focusPosition[0] = 'locationId';
	}
	if(document.forms[0].zoneCodes.value.length == 0)
	{
		msgErrors += 'Zone Codes cannot be empty\n';
		focusPosition[2] = 'zoneCodes';
	}
	if(document.forms[0].effectiveFrom.value.length == 0)
	{
		msgErrors += 'Effective From cannot be empty\n';
		focusPosition[4] = 'effectiveFrom';
	}
	/*if(document.forms[0].validUpto.value.length == 0)
	{
		msgErrors += 'Valid Upto cannot be empty\n';
		focusPosition[6] = 'validUpto';
	}*/
	if(document.forms[0].baseCurrency.value.length == 0)
	{
		msgErrors += 'CurrencyId cannot be empty\n';
		focusPosition[8] = 'baseCurrency';
	}
	if(document.forms[0].chargeBasis.value.length == 0)
	{
		msgErrors += 'Charge Basis cannot be empty\n';
		focusPosition[9] = 'chargeBasis';
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
	if(!checkValidity(document.forms[0].effectiveFrom))
		return false;
	if(!checkValidity(document.forms[0].validUpto))
		return false;

	if(!validateChargeBasis())
		return false;

	return validateSlabRates();
}

function validateChargeBasis()
{
	//added by subrahmanyam for 146442
	//if(document.forms[0].primaryUnit.value=='')
			document.forms[0].primaryUnit.value=document.forms[0].chargeBasis.value;
	//Ended by subrahmanyam for 146442
	if(document.forms[0].primaryUnit.value.toUpperCase()=='SHIPMENT')
	{
		alert("Cartage Charges cannot be defined for Charge Basis with Primary Unit as 'Per Shipment'");
		return false;
	}
	else return true;
}

function validateSlabRates()
{
	var slabRates	   = document.getElementsByName("slabRates");

	for(var i=0;i<slabRates.length;i++)
	{
		if(!slabRates[i].disabled)
		{
			if(slabRates[0].value.length==0 || slabRates[1].value.length==0)
			{
				alert("The First Two Slabs of the Weight Break Cannot be Empty");
				return false;
			}
			if(i==0)
			{
				if(parseInt(slabRates[0].value)+parseInt(slabRates[1].value)!=0)
				{
						alert("Weight Break Slabs 1 & 2 Must Be Equal & Opposite");
						return false;
				}
			}
			else if(parseFloat(slabRates[i-1].value)>=parseFloat(slabRates[i].value))
			{
				alert('The Weight Breaks Must Be Defined in Ascending Order');
				slabRates[i].focus();
				return false;
			}
			else if(slabRates[i].value.length==0)
			{
				slabRates[i].disabled = true;
			}
		}
	}
	
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
	if(event.keyCode!=13)
	{
		if(input.value.substring("-").length>0)
		{
			if(event.keyCode < 48 || event.keyCode > 57)
				return false;
		}
		else
		{
			if((event.keyCode < 48 && event.keyCode!=45) || event.keyCode > 57)
				return false;
		}
	}
	
	return true;
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
		var Url='etrans/QMSLOVZoneCodesMultiple.jsp?wheretoset=zoneCodes&searchString1='+searchString1+'&shipmentMode='+document.forms[0].shipMode.value+'&consoleType='+document.forms[0].consoleType.value;
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
function displaySlab()
{
	slab = document.getElementById("slabValues");
	
	if(document.forms[0].weightBreak.options.value=='Slab')
		slab.style.display="block";
	else 
		slab.style.display="none";
}
function changeRateOptions()
{
	var rateType	   = document.getElementById("rateType");
	var slabRates	   = document.getElementsByName("slabRates");
	
	if(document.forms[0].weightBreak.options.value=='Flat')
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat');

		for(var i=0;i<slabRates.length;i++)
		{
			slabRates[i].value    =	'';
			slabRates[i].disabled = true;
		}
	}
	else
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat'<%="Flat".equals(rateType)?",true,true":""%>);
		rateType[1] = new Option('Slab','Slab'<%="Slab".equals(rateType)?",true,true":""%>);
		rateType[2] = new Option('Both','Both'<%="Both".equals(rateType)?",true,true":""%>);

		for(var i=0;i<slabRates.length;i++)
			slabRates[i].disabled = false;
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

function toUpperCase(obj)
{
	return obj.value = obj.value.toUpperCase();
}
function showHide()
{
	var obj = document.getElementById('max');
	if(document.forms[0].maxCharge.checked)
		obj.innerHTML = "<b>MAX</b>";
	else
		obj.innerHTML = "";
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
	else return true;
}

function populateSlabValues()
{
	var slab	=	document.getElementsByName("slabRates");
<%
	if(slabRates!=null)
	{
		for(int i=0;i<slabRates.length;i++)
		{
%>			slab[<%=i%>].value = '<%=slabRates[i]%>';
<%		}
	}
%>
}

function openLocationLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";

	formArray = 'locationId';		
	
	tabArray = 'LOCATIONID';
	Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=CARTAGELOC&search= where LOCATIONID LIKE '"+document.forms[0].locationId.value+"~'";


	Options	='width=750,height=750,resizable=yes';
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
	Features=Bars+','+Options;

	Win=open(Url,'Doc',Features);
}
function changeConsoleVisibility(flag)
{
	var console	 = document.getElementById('console');
	var FCLTypes = document.getElementById('FCLTypes');
	var listValues	= document.getElementById("listValues");
	var weightBreak = document.getElementById('weightBreak');
	
	if(flag)
		document.forms[0].consoleType.options[0].selected = true;
	
	if(document.forms[0].shipMode.value=='2')
		console.style.display="block";
	else
	{
		console.style.display="none";
		listValues.length=0;
		FCLTypes.style.display="none";
		if(flag)
		{
			weightBreak.length=0;
			weightBreak[0] = new Option("Flat","Flat");
			weightBreak[1] = new Option("Slab","Slab");
			displaySlab();
		}
		document.getElementById("maxCharge").disabled=false;
	}

}
function changeListTypeVisibility(flag)
{
	var fclTypes	= document.getElementById('FCLTypes');
	var weightBreak = document.getElementById('weightBreak');
	var rateType	= document.getElementById('rateType');
	var listValues	= document.getElementById("listValues");
	var slabValues	= document.getElementById("slabValues");
	
	if(document.forms[0].consoleType.value=='FCL')
	{
		weightBreak.length=0;
		rateType.length=0;
		weightBreak[0] = new Option("List","List");
		rateType[0]= new Option("Flat","Flat");
		fclTypes.style.display="block";
		slabValues.style.display="none";
		document.getElementById("maxCharge").disabled=true;
		if(flag)
			showListLov();
	}
	else
	{
		if(flag)
		{
			weightBreak.length=0;
			weightBreak[0] = new Option("Flat","Flat");
			weightBreak[1] = new Option("Slab","Slab");
		}
		fclTypes.style.display="none";
		document.getElementById("maxCharge").disabled=false;
		listValues.length=0;
	}
}
function showListLov()
{
	var list  =  document.forms[0].listValues;
    var temp="";
   
   if(list)
	{
	   for(m=0;m<list.length;m++)
	   {
      	     temp=list[m].value+","+temp;
	   }
	}     
	myUrl= 'etrans/ListMasterMultipleLOV.jsp?listValue='+temp+'&shipmentMode=Sea';
	var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
	var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
	var myFeatures = myBars+','+myOptions;
	
	newWin = open(myUrl,'myDoc',myFeatures);
	if (newWin.opener == null) 
		newWin.opener = self;
	return true;
}
//@@ Added by subrahmanyam for the Enhancement 13/02/09
function toUpper()
{
	var chargeBasis = document.forms[0].chargeBasis.value;
	document.forms[0].chargeBasis.value = chargeBasis.toUpperCase();
}
//@@ Ended by subrahmanyam for the Enhancement 13/02/09
function showChargeBasis()
{
		//var operation	 = document.forms[0].operation.value;
		var termid		 = '<%=loginbean.getTerminalId()%>';
		var tabArray = '';
		var formArray = '';
		var lovWhere	=	"";

		var chargeBasis = document.forms[0].chargeBasis.value; //@@ Added by subrahmanyam for the 146442 on 13/02/09
		formArray = 'chargeBasis,primaryUnit,secondaryUnit';		
		
		tabArray = 'CHARGEBASIS,PRIMARY_BASIS,SECONDARY_BASIS';
//@@ Commented by subrahmanyam for the Enhancement 146442 on 13/02/09
		//Url		="qms/ListOfValues.jsp?lovid=CHARGEBASIS_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=BUYCHARGEBASIS&search=";
//@@ Added by subrahmanyam for the Enhancement 146442 on 13/02/09
		Url		="qms/ListOfValues.jsp?lovid=CHARGEBASIS_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=BUYCHARGEBASIS&search= where CHARGEBASIS LIKE'"+chargeBasis+"~'";


		Options	='width=750,height=750,resizable=yes';
		Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
		Features=Bars+','+Options;

		Win=open(Url,'Doc',Features);
}
function showListValues(obj)
{
	var listValues=document.getElementById("listValues");

	if(obj!=null)
	{
		listValues.length=0;
		for(var i=0;i<obj.length;i++)
		{
			listValues[i] = new Option(obj[i].value,obj[i].value);
			listValues[i].selected=true;
		}
	}
}
</script>
  </head>

  <body onload='changeRateOptions();displaySlab();showHide();populateSlabValues();changeConsoleVisibility(false);changeListTypeVisibility(false);'>
	<form method='POST' action='QMSCartageController' onsubmit="return Mandatory();">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				Cartage Buy Charges - Add </td><td align="right">QS1050711</td></tr></table></td>
			  </tr>
			  </table>
<%
		if(request.getAttribute("Errors")!=null)
		{
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="0">
				<tr valign="top" bgcolor="#FFFFFF">
					<td><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
						<%=(String)request.getAttribute("Errors")%></font></td>
				</tr>
			</table>
			
<%
		}
%>
			  <table width="100%" cellpadding="4" cellspacing="0" >
			  <tr valign="top" class='formdata'>
			   <td>Shipment Mode:</br>
				 <select name="shipMode" class='select' onchange='changeConsoleVisibility(true)'>
					<option value="1" <%="1".equalsIgnoreCase(shipmentMode)?"selected":""%>>Air</option>
					<option value="2" <%="2".equalsIgnoreCase(shipmentMode)?"selected":""%>>Sea</option>
				 </select>
				</td>
				<td>
				 <DIV id="console" style="DISPLAY:none">
					Console Type:</br>
					<select name="consoleType" class='select' onchange='changeListTypeVisibility(true)'>
						<option value="LCL" <%="LCL".equalsIgnoreCase(consoleType)?"selected":""%>>LCL</option>
						<option value="FCL" <%="FCL".equalsIgnoreCase(consoleType)?"selected":""%>>FCL</option>
					</select>
				</DIV>
			  </td>
			    <td>Location Id:<font color="#FF0000">*</font><br>
					<input type='text' class='text' name="locationId" size="10" maxlength='3' onblur='toUpperCase(this)' value='<%=locationId!=null?locationId:""%>'>&nbsp;
					<input type="button" value="..." name="locationidLOV" class='input' onclick=openLocationLov()>
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
			    <!-- <td>Vendor Id:<font color="#FF0000">*</font><br>
					<input type='text' class='text' name="vendorId" size="10" onblur='toUpperCase(this)' value='<%//=vendorId!=null?vendorId:""%>'>&nbsp;
					<input type="button" value="..." name="vendorIdLOV" class='input'>
				</td> -->
				<td>Effective From</br>(<%=dateFormat%>):<font  color=#ff0000>*</FONT><br>
					<input type='text' class='text' name='effectiveFrom' size='10' onBlur='dtCheck(this);checkValidity(this)' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>&nbsp;<input type='button' class='input'  name='b1' value='...' onClick="newWindow('effectiveFrom','0','0','')">
				</td>
				<td>Valid  upto</br>(<%=dateFormat%>):<!-- <font  color=#ff0000>*</FONT> --><br>
					<input type='text' class='text' name="validUpto" size="10" onBlur='dtCheck(this);checkValidity(this)' value='<%=validUptoStr!=null?validUptoStr:""%>'>
					<input type='button' class='input'  name='b2' value='...' onClick="newWindow('validUpto','0','0','')">
				</td>
			
				<td>Currency:<font  color=#ff0000>*</font><br><br>
					<input type='text' class='text' name='baseCurrency' size='10' maxlength='3' onblur='toUpperCase(this)' value='<%=currencyId!=null?currencyId:""%>'>&nbsp;
					<input type='button' value='...' name='currencyIdLOV' onClick='showCurrencyLOV()' class='input'>
				</td>

			  </tr>
			  
			  <tr valign="top" class='formdata'>
			   <td colspan='2'>Charge Type:<br>
                <select name="chargeType" class='select'>
                <option value="Pickup" <%="Pickup".equals(chargeType)?"selected":""%>>Pickup</option>
                <option value="Delivery" <%="Delivery".equals(chargeType)?"selected":""%>>Delivery</option>
                <option value="Both" <%="Both".equals(chargeType)?"selected":""%>>Both</option>
                </select>
            </td> 
			   <td>Charge Basis:<font color="#FF0000">*</font><br>
				<!--select size="1" name="chargeBasis" class='select' onchange='changeUOMOptions()'>
					<option value='Weight' <%="Weight".equals(chargeBasis)?"selected":""%>>Weight</option>
					<option value='Volume' <%="Volume".equals(chargeBasis)?"selected":""%>>Volume</option>
                </select-->
<!-- @@ Commented by subrahmanyam for the Enhancement 146442 on 13/02/09 -->
				<!-- <input type='text' class='text' name = "chargeBasis" size='10' value='<%=chargeBasis!=null?chargeBasis:""%>' readOnly>&nbsp;<input type='button' class='input' value='...' onclick='showChargeBasis()'/> -->
<!-- @@ Added by subrahmanyam for the Enhancement 146442 on 13/02/09 -->
				<input type='text' class='text' name = "chargeBasis" size='10' value='<%=chargeBasis!=null?chargeBasis:""%>' onBlur="toUpper()">&nbsp;<input type='button' class='input' value='...' onclick='showChargeBasis()'/>
            </td> 
			   <!--td>UOM: <br>
              <select size="1" name="uom" class='select'>
                <option selected value="Kg">Kg</option>
				<option value="Lb">Lb</option>
               
                <option value="CFT">CFT</option>
                <option value="CBM">CBM</option>
               
              </select>
            </td--> 
				<td>Weight Break:<font color="#FF0000">*</font><br>
              <select size="1" name="weightBreak" id="weightBreak" class='select' onchange='changeRateOptions();displaySlab()'>
                <option value="Flat" <%="Flat".equals(weightBreak)?"selected":""%>>Flat</option>
                <option value="Slab" <%="Slab".equals(weightBreak)?"selected":""%>>Slab</option>
                
              </select>
            </td>
				<td>Rate Type: <font color="#FF0000">*</font><br>
              <select size="1" name="rateType" id="rateType" class='select'>
                <option  selected value="Flat">Flat</option>
                <option value="Slab">Slab</option>
                
              </select>
            </td>
			<td colspan="2" >
			<input type="checkbox" name="maxCharge" onclick='showHide()' <%="Y".equals(maxChargeFlag)?"checked":""%>>
              Maximum Charge per Truckload<br>
            </td>
			</tr>
			</table>
			
			<DIV id="slabValues" style="DISPLAY:none">
			<table width="100%" cellpadding="4" cellspacing="1">
			<tr class=formheader>
			   <!-- <td colspan="15"><b>Weight Break:</b> --><!-- @@Commented by subrahmanyam for 170759 -->
			   <td colspan="16"><b>Weight Break:</b><!-- @@Added by subrahmanyam for wpbn id:  170759 -->
            </td> 
              </tr>
			  
			  <tr valign="top" class='formdata'>
  			   <td><b>BASE</b></td><!-- @@Added by subrahmanyam for wpbn id:  170759 -->
			   <td><b>MIN</b></td>
<%			
			for(int i=0;i<13;i++)
			{
%>			 <td>
				<input type='text' class='text' name="slabRates" size="5"  maxlength='10' onkeypress='<%=(i==0)?"return getNumberCodeNegative(this)":"return getNumberCode(this)"%>'>
			</td>
<%			}
%>
			   <td><div id='max'></div></td>
			</tr>
			</table>
			</DIV>
			<DIV id='FCLTypes' style="DISPLAY:none">
			<table width="100%" cellpadding="4" cellspacing="1">
				<tr class='formheader'>
					<td colspan="15"><b>Container Types:</b></td>
				</tr>
				<tr valign="top" class='formdata'>
					<td>
						<select size='5' name='listValues' id='listValues' class='select' multiple>
<%
				for(int i=0;i<listSize;i++)
				{
%>					<option value='<%=listValues[i]!=null?listValues[i]:""%>' selected><%=listValues[i]!=null?listValues[i]:""%></option>
<%				}
%>
						</select>&nbsp;<input type='button' name='listLOV' onClick= 'showListLov()' value='...'class='input'>
					</td>
				</tr>
			</table>
			</DIV>
			
			<table width="100%" cellpadding="4" cellspacing="1">
               <tr valign="top" class='text'>
				<td class='denotes'>
					* Denotes Mandatory
				</td>
				<td colspan='6' align='right'>
					<input type="hidden" name="Operation" value="<%=operation%>">	
					<input type="hidden" name="subOperation" value="Details">
					<input type="hidden" name="primaryUnit">
					<input type="hidden" name="secondaryUnit">
					<input name=submit type=submit value="Next >>" class='input'>
					<input name=Reset type=reset value=Reset class='input'>
				</td>
				</tr>
			
	  </table>
	</form>
      </table>
  </body>
</html>
<%	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"error in JSP:"+e);
    logger.error(FILE_NAME+"error in JSP:"+e);
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Unable to Access the Page","QMSCartageController"); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);

%>		<jsp:forward page="../ESupplyErrorPage.jsp"/>

<%
	}
%>
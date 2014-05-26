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
			com.qms.operations.charges.java.QMSCartageSellDtlDOB,
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
	QMSCartageSellDtlDOB sellDOB                = null;

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
	String[]			zoneCodes				= null;
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
    int                 index                   = Integer.parseInt(request.getParameter("index"));
	String				validUptoStr			= null;
	String				marginType				= "";
	String				overAllMargin			= "";
	String				consoleType				= "";

	
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

		pickUpList			=	(ArrayList)session.getAttribute("CartageBuyCharges");
        sellDOB             =   (QMSCartageSellDtlDOB)session.getAttribute("sellDOB");

		if(pickUpList!=null)
		{   cartageMaster   = (QMSCartageMasterDOB)pickUpList.get(index);
			zoneCodes	    = cartageMaster.getZoneCodes();
			locationId      = cartageMaster.getLocationId();
			currencyId      = cartageMaster.getCurrencyId();
			vendorIds       = cartageMaster.getVendorIds();

			weightBreak		= cartageMaster.getWeightBreak();
			rateType		= cartageMaster.getRateType();

			chargeType		= cartageMaster.getChargeType();
			chargeBasis		= cartageMaster.getChargeBasis();
			shipmentMode	= cartageMaster.getShipmentMode();
			consoleType		= cartageMaster.getConsoleType();
			//unitofMeasure	= cartageMaster.getUom();
		}
		if(sellDOB!=null)
		{
			slabList		= sellDOB.getSlabList();
			marginType		= sellDOB.getMarginType();
			overAllMargin	= sellDOB.getOverallMargin(); 
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
	
	 if(document.forms[0].submitName.value=="Next>>")
	{
		 var chkBox			=   document.getElementsByName("checkBoxValue");
		 var dChkBox		=	document.getElementsByName("dCheckBoxValue");

		 var marginValues	=	document.getElementsByName("marginValues");
		 var dMarginValues	=	document.getElementsByName("dMarginValues");

		 var flag			=	true;	

		 if(chkBox!=null && chkBox.length!=0)
		{
			flag = validateMargins(chkBox,marginValues,"slabBreak","slabValue","<%=label%>");
		}

		if(!flag)
			return false;
	}
	document.forms[0].action="QMSCartageController?Operation=cartageAccept&subOperation=updateSellRate&index="+'<%=index%>'+"&serchStr=etValue&weightBreak=List";
	
	document.forms[0].submit();
	
	window.close();
	return true;
}

function validateMargins(chkBox,marginValues,slabBreak,slabValue,label)
{
	var checkedFlag		=	false;
	for(var i=0;i<chkBox.length;i++)
	{	
	 /* if(chkBox[i].value == "YES")
	  {*/
		checkedFlag	=	true;
		if(document.forms[0].overAllMargin.options.value=='No')
		{
			for(var j=0;j<marginValues.length;j++)
			{
				if(marginValues[j].value.length==0 && document.getElementsByName(slabValue+j)[0].value!='')
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
					if(absMargins[j].value.length==0 && document.getElementsByName(slabValue+j)[0].value!='')
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
	  //}
  }
	 return true;
}

function setName(name)
{
	document.forms[0].submitName.value	= name;
	
	if(name=="Next>>")
		document.forms[0].subOperation.value='setSlabSellRates';
	else if(name=="Search")
		document.forms[0].subOperation.value='Details';
}
function toUpperCase(obj)
{
	obj.value = obj.value.toUpperCase();
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
}
function loadMargin()
{
	var percent    = document.getElementById("percent");
	var absolute   = document.getElementById("absolute");
	var notOverall = document.getElementById("notOverall");

	var	marginType		= '<%=marginType!=null?marginType:""%>';
	var	overAllMargin	= '<%=overAllMargin!=null?overAllMargin:""%>';
	
  if(overAllMargin.length!=0 && marginType.length!=0)
  {	
	if(overAllMargin=='No')
	{
		percent.style.display="none";
		absolute.style.display="none";
		notOverall.style.display="block";
	}
	else if(overAllMargin=='Yes' && marginType=='A')
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
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].value=="Next>>")
		{
			obj[i].disabled=true;
		}
	}
}

function showMargins(cartageId,zoneCode)
{

	var locationId  = document.forms[0].locationId.value;
	//var zoneCode    = document.forms[0].zoneCode.value;
	var rateType    = document.forms[0].rateType.value;
	var weightBreak = document.forms[0].weightBreak.value;
	var chargeBasis = document.forms[0].chargeBasis.value;
	var chargeType  = document.forms[0].chargeType.value;
	//var cartageId   = document.forms[0].cartageId.value;
	//alert(cartageId)
	var URL         = "etrans/QMSCartageMarginDisplay.jsp?locationId="+locationId+"&zoneCode="+zoneCode+"&rateType="+rateType+"&weightBreak="+weightBreak+"&chargeBasis="+chargeBasis+"&Operation=Modify&cartageId="+cartageId+"&chargeType="+chargeType;
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 900,height = 500,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
}


</script>
</head>

<body onload='loadMargin()'>
	<form method='post'  >

	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td align='left'>
				Cartage Sell Charges -  Modify </td>
				<td  align='right'>QS1050812
				</td>
			  </tr>
			  </table>

			  <table width="100%" cellpadding="4" cellspacing="0">
			  <tr valign="top" class='formdata'>
			  <td>Shipment Mode:<br>
				<%="1".equalsIgnoreCase(shipmentMode)?"Air":("2".equalsIgnoreCase(shipmentMode)?"Sea":"")%>
			  </td>
<%
				 if("2".equalsIgnoreCase(shipmentMode))
				{
%>					<td>Console Type:<br>
						<%=consoleType!=null?consoleType:""%>
					</td>
<%
				}
				else
				{
%>
					<td>&nbsp;</td>
<%
				}
%>
			    <td>Charge Type:<br>
                <%=chargeType%>
			  </td>
			  <!--td>Charge Basis:<br>
				<%//=chargeBasis%>
			 </td-->
			  <!--td>UOM: <br>
              <%//=unitofMeasure%>
			 </td-->
           <td>Weight Break: <br>
              <%=weightBreak%>
		  </td>
		  <td colspan='2'>Rate Type: <br>
              <%=rateType%>
		 </td>
		</tr>           
			  
		   <tr valign="top" class='formdata'>
			<td  colspan="2">Location Id: <br>
				<%=locationId!=null?locationId:""%>	
			</td>
			<td>Zone Codes:<br>					
					<%=cartageMaster.getZoneCode()%>
				</td>
			<td colspan='3'>Currency:<br><%=currencyId!=null?currencyId:""%>
            </td>
		  </tr>
			
		</table>
<%
	
	{
%>
		<table width="100%" cellpadding="4" cellspacing="1">
<%	
	{
%>		 <tr valign="top" class='formheader' align="center">
		
            <td  width='10%'>Zone Code</td>
			<td  width='10%'>Effective From</td>
			<td  width='10%' nowrap>Valid Upto</td>
			<td  width='10%' nowrap>Charge Basis</td>
            
<%
			for(int i=0;i<slabList.size();i++)
			{
%>				<td><%=slabList.get(i)%></td>
<%			}
%>
		</tr>
         
<%		
		{
			hMap				=  sellDOB.getSlabRates();

			effectiveFrom		=  cartageMaster.getEffectiveFrom();
			validUpto			=  cartageMaster.getValidUpto();
			//densityRatio        =  sellDOB.getDensityRatio()!=null?sellDOB.getDensityRatio():"";

			str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
			effectiveFromStr	= str[0];
			str					= null;

			str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
			validUptoStr		= str[0];
			str					= null;
%>	
         <tr valign="top" class='formdata' align="center">
		
			<td>Zone <%=sellDOB.getZoneCode()%></td>
            <td><%=effectiveFromStr%></td>
            <td width='10%'><%=validUptoStr%></td>
            <td width='10%'><%=chargeBasis!=null?chargeBasis:""%></td>
			
			<input type='hidden' name='cartageId' value='<%=sellDOB.getCartageId()%>'>
			<input type='hidden' name='zoneCode' value='<%=sellDOB.getZoneCode()%>'>
			<input type='hidden' name='checkBoxValue'>
<%
			 for(int j=0;j<slabList.size();j++)
			{
				ArrayList values		= (ArrayList)hMap.get(slabList.get(j));
				String    chargeRate	= "";
				if(values!=null)
					chargeRate	=	(String)values.get(0);
						
%>			    
                <td>&nbsp;<%=!"".equalsIgnoreCase(chargeRate)?chargeRate:"-"%></td>
				<input type='hidden' name='slabBreak<%=j%>' value='<%=slabList.get(j)%>'>
				<input type='hidden' name='slabValue<%=j%>' value='<%=chargeRate%>'>
<%			}
%>         
		</tr>
<%
		}
	}		
		{
%>		
		<tr valign="top" class='formlabel'>
            <td colspan="20">Margin Calculations</td>
		</tr>
        <tr valign="top" class='formdata'>
            <td colspan='3'>Overall Margin:<br>
				<select size="1" name="overAllMargin" class='select' onchange='displayMargin()'>
					<option value="Yes" <%="Yes".equalsIgnoreCase(sellDOB.getOverallMargin()) ? "SELECTED" : "" %>>YES</option>
					<option  value="No" <%="no".equalsIgnoreCase(sellDOB.getOverallMargin()) ? "SELECTED" : "" %>>NO</option>
				</select>
			</td>

            <td colspan='<%=slabList.size()+4%>'>Margin Type:<br>
			<select size="1" name="marginType" class='select'  onchange='displayMargin()'>
                <option value="Absolute" <%="A".equalsIgnoreCase(sellDOB.getMarginType()) ? "SELECTED" : "" %>>ABSOLUTE</option>
				<option value="Percentage"  <%="P".equalsIgnoreCase(sellDOB.getMarginType()) ? "SELECTED" : "" %>>PERCENT</option>
              </select>
			 </td>
		</tr>
		</table>
		<div id='percent' style="DISPLAY:none">
			<table width='100%' cellpadding='4' cellspacing='1'>
				<tr valign='top' class='formdata'>
					<td align='right' width='26%'><b>Margin&nbsp;</b><font color='red'>* </font></td>
					<td colspan='6' align='center'><input type='text' class='text' name='marginPercent' size='5'  maxlength='6' onblur='getKeyCode(this);return chrnum(this)' value='<%=sellDOB.getMargin()%>'></td>
				</tr>
			</table>
		</div>
        <div id='absolute' style="DISPLAY:none">
			<table width="100%" cellpadding="4" cellspacing="1">
			 <tr class='formdata'>
			  <td width='40%'><b>OVERALL MARGIN</b><font color='red'>* </font></td>
<%
				for(int i=0;i<slabList.size();i++)
				{
					ArrayList values		= (ArrayList)hMap.get(slabList.get(i));
					String    margin		= "";
					if(values!=null)
						margin	 =	(String)values.get(1);
%>					
						<td align='center'>
						<input type='text' class='text' name="absMargins" size="5" maxlength='6' onblur='getKeyCode(this);return chrnum(this)' value='<%=margin%>'>
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
			
			{
%>				<tr class='formdata'>
			 <td width='40%'><b><%="Delivery".equals(chargeType)?"DELIVERY MARGIN":"PICKUP MARGIN"%></b><font color='red'>* </font></td>
<%
				for(int i=0;i<slabList.size();i++)
				{
					ArrayList values		= (ArrayList)hMap.get(slabList.get(i));
					String    margin		= "";
					if(values!=null)
						margin	 =	(String)values.get(1);
%>					
					<td align='center'>
						<input type='text' class='text' name="marginValues" value='<%=margin%>' size="5" maxlength='6' onblur='getKeyCode(this);return chrnum(this)'>
					</td>
<%
				}
%>
			 </tr>								
<%			}
			
%>
			 </tr>
			</table>
		</div>

		<table width="100%" cellpadding="4" cellspacing="1">
	    <tr class=text>
			 <td colspan="7"><font color='red'>* </font>Denotes Mandatory</td>
			 <td align='right'>
				<input type="reset" value="Reset" class='input'>
				<input type="hidden" name="submitName">
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="subOperation">
				<input type="button" name="Submit" value="Next>>" class='input' onclick="setName(this.value);return Mandatory()">
			</td>
        </tr>
<%
		}
%>
	  </table>
<%
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
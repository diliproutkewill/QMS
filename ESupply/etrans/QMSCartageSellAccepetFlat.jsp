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
			com.qms.operations.charges.java.QMSCartageSellDtlDOB,
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
	String				consoleType				= "";
	String				fromWhere				= null;
	String				onLoadCalls				= "";
	ErrorMessage		errorMessageObject		= null;
	ArrayList			keyValueList			= new ArrayList();

	QMSCartageMasterDOB cartageMaster			= null;
	QMSCartageBuyDtlDOB pickUpbuyChargesDtl		= null;
	QMSCartageBuyDtlDOB delBuyChargesDtl		= null;
	QMSCartageSellDtlDOB sellDOB                = null;
	
	QMSCartageBuyDtlDOB cartageBuyDtl			= null;
	
	ArrayList			buyChargesList			= null;
	
	ArrayList			pickUpList				= null;
	ArrayList			delList					= null;
	
	String				label					= "";
	
	String				locationId				= null;
    String				zoneCode				= null;
	String				str[]					= null;
    String				currencyId				= null;
    String				chargeType				= null;
    String				chargeBasis				= null;
    String				unitofMeasure			= null;
    String				weightBreak				= null;
    String				rateType				= null;

	String				maxChargeFlag			= "";
	String				delMaxChargeFlag		= "";
	String              densityRatio            = "";//added by rk
	String              dDensityRatio            = "";//added by rk

	double				baseRate				= 0; //@@ Added by subrahmanyam for Enhancement 170759 on 02/06/09
	double				minRate					= 0;
	double				flatRate				= 0;
	double				maxRate					= 0;
	
	double				dBaseRate				= 0; //@@ Added by subrahmanyam for Enhancement 170759 on 02/06/09
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
	int                 index                   = Integer.parseInt(request.getParameter("index"));
	String				marginType				= "";
	String				overAllMargin			= "";
	
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


		pickUpList			=	(ArrayList)session.getAttribute("CartageBuyCharges");
        sellDOB             =   (QMSCartageSellDtlDOB)session.getAttribute("sellDOB");

		if(pickUpList!=null)
		{   
			cartageMaster   = (QMSCartageMasterDOB)pickUpList.get(index);
			counter			= pickUpList.size();
			zoneCodes	    = cartageMaster.getZoneCodes();
			locationId      = cartageMaster.getLocationId();
			currencyId      = cartageMaster.getCurrencyId();

			weightBreak		= cartageMaster.getWeightBreak();
			rateType		= cartageMaster.getRateType();

			chargeType		= cartageMaster.getChargeType();
			chargeBasis		= cartageMaster.getChargeBasis();
			//unitofMeasure	= cartageMaster.getUom();
			shipmentMode	= cartageMaster.getShipmentMode();
			consoleType		= cartageMaster.getConsoleType();
		}
		if(sellDOB!=null)
		{
			marginType		= sellDOB.getMarginType();
			overAllMargin	= sellDOB.getOverallMargin(); 
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
	
	if(document.forms[0].submitName.value=="Next>>")
	{
		var checkedFlag		=	false;
		var checkBoxValue	=	document.getElementsByName('checkBoxValue');
		var cartageId		=	document.getElementsByName('cartageId');
		var dCartageId		=	document.getElementsByName('dCartageId');

		
		checkedFlag	=	true;
		if((document.forms[0].overAllMargin.options.value=='No') || (document.forms[0].overAllMargin.options.value=='Yes' && document.forms[0].marginType.options.value=='Absolute'))
		{
			if(document.forms[0].dMinMargin==null)
			{
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
				if(document.forms[0].baseMargin.value.length==0)
				{
					alert('Please Enter the Base Margin');
					document.forms[0].baseMargin.focus();
					return false;
				}
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09
////modified  for issue 172884 by VLAKSHMI on 8/06/2009
				if(document.forms[0].minMargin.value.length==0 || document.forms[0].minMargin.value==0)
				{
					alert('Please Enter the Minimum Margin');
					document.forms[0].minMargin.focus();
					return false;
				}
				if(document.forms[0].flatMargin.value.length==0 || document.forms[0].flatMargin.value==0)
				{
					alert('Please Enter the Flat Margin');
					document.forms[0].flatMargin.focus();
					return false;
				}
				if((document.forms[0].maxMargin.value.length==0  || document.forms[0].maxMargin.value==0)&& document.forms[0].maxRate.value!=0)
				{
					alert('Please Enter the Max Margin');
					document.forms[0].maxMargin.focus();
					return false;
				}
		  }
		else if(document.forms[0].minMargin==null)
		{
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
				if(document.forms[0].dBaseMargin.value.length==0)
				{
					alert('Please Enter the Base Margin');
					document.forms[0].dBaseMargin.focus();
					return false;
				}
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/09

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
			if(document.forms[0].dMaxMargin.value.length==0 && document.forms[0].dMaxRate.value!=0)
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
			if(document.forms[0].maxMargin.value.length==0 && document.forms[0].maxRate.value!=0  && cartageId[i].value!='-')
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
			if(document.forms[0].dMaxMargin.value.length==0 && document.forms[0].dMaxRate.value!=0 && dCartageId[i].value!='-')
			{
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
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
		obj[i].disabled=true;

   	document.forms[0].action="QMSCartageController?Operation=cartageAccept&subOperation=updateSellRate&index="+'<%=index%>'+"&serchStr=etValue&weightBreak=Flat";
	
	document.forms[0].submit();
	
	window.close();
	return true;
}
function closeWin()
{
	var serVaue='<%=request.getParameter("serchStr")%>';
	if(serVaue=="etValue")
		window.close();
}
function toUpperCase(obj)
{
	obj.value = obj.value.toUpperCase();
}

function loadMargin()
{
	var percent		= document.getElementById("percent");
	var absolute	= document.getElementById("absolute");

	var	marginType		= '<%=marginType!=null?marginType:""%>';
	var	overAllMargin	= '<%=overAllMargin!=null?overAllMargin:""%>';
	
  if(overAllMargin.length!=0 && marginType.length!=0)
  {	
	if((overAllMargin=='No') || (overAllMargin=='Yes' && marginType=='A'))
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
function showMargins(cartageId,zoneCode)
{

	var locationId  = document.forms[0].locationId.value;
	var rateType    = document.forms[0].rateType.value;
	var weightBreak = document.forms[0].weightBreak.value;
	var chargeBasis = document.forms[0].chargeBasis.value;
	var chargeType  = document.forms[0].chargeType.value;
	var URL         = "etrans/QMSCartageMarginDisplay.jsp?locationId="+locationId+"&zoneCode="+zoneCode+"&rateType="+rateType+"&weightBreak="+weightBreak+"&chargeBasis="+chargeBasis+"&Operation=Modify&cartageId="+cartageId+"&chargeType="+chargeType;
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 900,height = 500,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
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
</script>
</head>

<body onload='loadMargin()'>
	<form method='post' onsubmit='return Mandatory()' >

	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				Cartage Sell Charges - Modify </td><td align="right">QS1050812</td></tr></table></td>
			  </tr>
			  </table>

			  <table width="100%" cellpadding="4" cellspacing="1">

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
			  <!--td>Charge Basis:<br><%//=chargeBasis%>

			 </td>
			  <td>UOM: <br><%//=unitofMeasure%>
			 </td-->
           <td>Weight Break: <br><%=weightBreak%>
		  </td>
		  <td colspan='2'>Rate Type: <br><%=rateType%>
			 </select>
		 </td>
		</tr>           
			  
		   <tr valign="top" class='formdata'>
			<td colspan="2">Location Id: <br>
				<%=locationId!=null?locationId:""%>				
			</td>
			<td>Zone Codes:<br><%=cartageMaster.getZoneCode()%>
				</td>
			<td colspan='3'>Currency:<br>
				<%=currencyId!=null?currencyId:""%>
            </td>
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
            <td  width='10%'>Zone Code</td>
            <td  width='10%'>Effective From<br>(<%=dateFormat%>)</td>
            <td  width='10%'>Valid Upto<br>(<%=dateFormat%>)</td>            
            <td  width='10%'>Charge Basis</td>            

			<td><b>Base</b></td><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
            <td><b>Min</b></td>
            <td><b>Flat</b></td>
			<td><b>Max</b></td>			
		</tr>

<%		
				baseRate			=  sellDOB.getBaseRate();//@@ Added by subrahmanyam for the Enhancement 170759 on 02/06/09

				minRate				=  sellDOB.getMinRate(); 
				flatRate			=  sellDOB.getFlatRate();
				maxRate				=  sellDOB.getMaxRate();
				effectiveFrom		=  cartageMaster.getEffectiveFrom();
				validUpto			=  cartageMaster.getValidUpto();				
				str					=  eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
				effectiveFromStr	=  str[0];
				str					=  null;
				str					=  eSupplyDateUtility.getDisplayStringArray(validUpto);
				validUptoStr		=  str[0];
				str					=  null;		
%>
		<tr valign="top" class='formdata' align='center'>		
            <td>Zone <%=sellDOB.getZoneCode()!=null?sellDOB.getZoneCode():""%></td>
            <td><%=effectiveFromStr!=null?effectiveFromStr:"-"%></td>
            <td><%=validUptoStr!=null?validUptoStr:"-"%></td>
            <td><%=chargeBasis!=null?chargeBasis:"-"%></td>
			<td><%=df.format(baseRate)%></td><!-- @@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
            <td><%=df.format(minRate)%></td>
            <td><%=df.format(flatRate)%></td>
			<td><%=df.format(maxRate)%></td>	
			<input type='hidden' name='baseRate' value='<%=baseRate%>'><!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
			<input type='hidden' name='minRate' value='<%=minRate%>'>
			<input type='hidden' name='flatRate' value='<%=flatRate%>'>
			<input type='hidden' name='maxRate' value='<%=maxRate%>'>
			<input type='hidden' name='cartageId' value='<%=sellDOB.getCartageId()%>'>
			<input type='hidden' name='zoneCode' value='<%=sellDOB.getZoneCode()!=null?sellDOB.getZoneCode():""%>'>
			<input type='hidden' name='checkBoxValue'>
		</tr>
<%
		
	  }
	}	
		
		{
%>
		
        <tr valign="top" class='formheader'>
            <td colspan="12">Margin Calculations</td>
		</tr>
        <tr valign="top" class='formdata'>
            <td colspan="3">Overall Margin:<br>
                <select size="1" name="overAllMargin" class='select' onchange='displayMargin()'>
					<option value="Yes" <%="Yes".equalsIgnoreCase(sellDOB.getOverallMargin()) ? "SELECTED" : "" %>>YES</option>
					<option  value="No" <%="no".equalsIgnoreCase(sellDOB.getOverallMargin()) ? "SELECTED" : "" %>>NO</option>               
				</select>
			</td>
            <td colspan="9">Margin Type: <br>
              <select size="1" name="marginType" class='select' onchange='displayMargin()'>
                <option value="Absolute" <%="A".equalsIgnoreCase(sellDOB.getMarginType()) ? "SELECTED" : "" %>>ABSOLUTE</option>
				<option value="Percentage" <%="P".equalsIgnoreCase(sellDOB.getMarginType()) ? "SELECTED" : "" %>>PERCENT</option>
			 </select>
			</td>
		 </tr>
		 </table>
		
		<div id='percent' style="DISPLAY:none">
			<table width='100%' cellpadding='4' cellspacing='1'>
				<tr valign='top' class='formdata'>
					<td width='<%//="Both".equals(chargeType)?"55%":"40%"%>' align='right'><b>Margin&nbsp;</b></td>
					<td colspan='6' align='center'><input type='text' class='text' name='marginPercent' size='5' maxlength='6' value='<%=sellDOB.getMargin()%>' onBlur='getKeyCode(this);return chrnum(this)'></td>
				</tr>
			</table>
		</div>

		<div id='absolute' style="DISPLAY:none">
			<table width='100%' cellpadding='4' cellspacing='1'>
				<tr valign='top' class='formdata'>
					<td width='40%' align='right'><b>Margin&nbsp;</b></td>
<!-- @@Added by subrahmanyam for the Enhancement 170759 on 02/06/09 -->
					<td align='center'><input type='text' class='text' name="baseMargin" size="5" maxlength='6' value='<%=sellDOB.getBaseMargin()%>' onBlur='getKeyCode(this);return chrnum(this)'></td>
<!-- @@Ended by subrahmanyam for 170759 -->
					<td align='center'><input type='text' class='text' name="minMargin" size="5" maxlength='6' value='<%=sellDOB.getMinMargin()%>' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="flatMargin" size="5" maxlength='6' value='<%=sellDOB.getFlatMargin()%>' onBlur='getKeyCode(this);return chrnum(this)'></td>
					<td align='center'><input type='text' class='text' name="maxMargin" size="5" maxlength='6' value='<%=sellDOB.getMaxMargin()%>' onBlur='getKeyCode(this);return chrnum(this)'></td>
				</tr>				
			</table>
		</div>
		
		<table width='100%' cellpadding='4' cellspacing='1'>

         <tr valign="top" class='text'>
            <td colspan="8">* Denotes Mandatory</td>
            <td colspan="2" align='right'>
			<input type="hidden" name="submitName">	
			<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="subOperation">
			
				<input type="Reset" value="Reset" class='input'>
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
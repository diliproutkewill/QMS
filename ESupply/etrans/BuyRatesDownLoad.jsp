<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSellRateEnter.jsp
	Product Name	: QMS
	Module Name		: Recommended Sell Rate
	Task		    : ViewInvalidate Recommended Sell Rate
	Date started	: 26-07-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Madhusudhan Reddy .Y
	Description		: The application "View/Invalidate" Recommended Sell Rate
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						java.util.HashMap,
						java.util.Set,
						java.util.Iterator,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="BuyRatesDownLoad.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ArrayList			listValue				=	null;

		String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
		String				terminalId				=	loginbean.getTerminalId();
		String				operation				=	null;
		String				origin					=	null;
		String				originCut				=	null;
		String				distenation				=	null;
		String				distenationCut			=	null;
		String				currencyId				=	null;
		String				serviceLevel			=	null;
		String				carrierStr				=	null;
		String				shipmentMode			=	null;
		String				weightBrk				=	null;
		String				rateTpe					=	null;
		String				weigtClas				=	null;
		String				consoleTpe				=	null;
		QMSSellRatesDOB		sellDob					=	null;

		ErrorMessage		errorMessageObject		=   null;
		String				originRegion			=	null;//added by phani sekhar FOR WPBN 179351 ON 20090812
		String				distenationRegion		=	null;//added by phani sekhar FOR WPBN 179351 ON 20090812

		try
		{
			operation				=	request.getParameter("Operation");
			listValue				=	(ArrayList)session.getAttribute("HeaderValue");
			session.removeAttribute("HeaderValue");
			if(listValue!=null)
			{
					sellDob	=	(QMSSellRatesDOB)listValue.get(0);

					currencyId		=	sellDob.getCurrencyId();
					origin			=	sellDob.getOrigin();
					originCut		=	sellDob.getOriginCountry();
					distenation		=	sellDob.getDestination();
					distenationCut	=	sellDob.getDestinationCountry();
					serviceLevel	=	sellDob.getServiceLevel();
					carrierStr		=	sellDob.getCarrier_id();

					shipmentMode			=	sellDob.getShipmentMode();
					weightBrk				=	sellDob.getWeightBreak();
					rateTpe					=	sellDob.getRateType();
					weigtClas				=	sellDob.getWeightClass();
					consoleTpe				=	sellDob.getConsoleType();
					originRegion			=   sellDob.getOriginRegions();//added by phani sekhar FOR WPBN 179351 ON 20090812
					distenationRegion		=  sellDob.getDestRegions();//added by phani sekhar FOR WPBN 179351 ON 20090812

			}
			
%>
<html>
<head>
<title>Recommended Sell Rates Master</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
/*
* This function used for check Mandatory fields.
*/
function showLocationValues(obj,where)
{
	var data="";
	for( i=0;i<obj.length;i++)
		{
		firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		if(where=="serviceLevelId")
			lastIndex	=	firstTemp.indexOf(' [');	
		else
			lastIndex	=	firstTemp.indexOf('[');
		//firstTemp	=	firstTemp.substring(firstIndex,lastIndex);//@@ Commented by Govind for the pbd id: 199753
		firstTemp	=	firstTemp.substring(0,lastIndex);//@@ Added by Govind for the pbd id: 199753
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
		if(where=="originCountry"||where=="destinationCountry")
		{
			if(data!="")
			data=data+","+temp1;
		else
			data=temp1;
		}
		else
		{
			if(data!="")
				data=data+","+temp;
			else	
				data=temp;
		}
		
		}
		
	document.getElementById(where).value=data;
		
	
}
function validation(obj)
{
	var operation		=	'<%=operation%>';
	var msgHeader		=	'';
	var msgErrors		=	'';
	var focusPosition	=	new Array();

	msgHeader= '_____________________________________________________\n\n' +
		'This form has not been submitted because of the following error(s).\n' +
		'Please correct the error(s) and re-submit.\n' +
		'_____________________________________________________\n\n';
	//modified by phani sekhar FOR WPBN 179351 ON 20090812
	if(document.forms[0].origin.value.length==0 && document.forms[0].destination.value.length==0 
		&& document.forms[0].carriers.value.length==0 && document.forms[0].serviceLevelId.value.length==0&&document.forms[0].originRegion.value.length==0&&document.forms[0].originCountry.value.length==0&&document.forms[0].destinationRegion.value.length==0&&document.forms[0].destinationCountry.value.length==0)
	{
		msgErrors	+="Please Enter/Select OriginId/DestinationId/Carriers/ServiceLevelId/OriginCountry/OriginRegion/DestinationCountry/DestinationRegion.\n";
		focusPosition[1]	=	"origin";
	}


	if(msgErrors.length > 0)
	{
		alert(msgHeader + msgErrors);
		for(loop=0; loop <focusPosition.length; loop++)
		{
			if(focusPosition[loop] != null && focusPosition[loop] != '')
			{
				document.forms[0].elements[focusPosition[loop]].focus();
				break;
			}
		}
		return false;
	}
	
	return true;
	
	
}
/*
* This function used for Margins disply to respective conditions
*/

/*
* This function used for Console type disply to respective conditions
*/
function showHide()
{

	var data="";
	var index	=	document.forms[0].shipmentMode.value;
	if(index=="2")
	{
		 data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleType" class="select" onChange="changeValue(this);showRateType();disableSubmit()"><option  value="LCL" >LCL</option><option  value="FCL" >FCL</option></select>';
	}
	else if(index=="4")
	{
		data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleTypes" class="select" onChange="changeValue(this);showRateType();disableSubmit()"><option  value="LTL" >LTL</option><option  value="FTL" >FTL</option></select>';
	}
	if( document.layers)
	{
		document.layers.cust1.document.write(data);
		document.layers.cust1.document.close();
	}
	else
	{
		if(document.all)
		{
		   cust1.innerHTML = data;
		 }
	 }
}

//This function used for call the LOVs
function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2= (toSet=='origin')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
	var searchString3= (toSet=='origin')?(document.forms[0].originRegion.value):(document.forms[0].destinationRegion.value);//added by phani sekhar FOR WPBN 179351 ON 20090812

	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value+'&searchString3='+searchString3;//modified by phani sekhar FOR WPBN 179351 ON 20090812
	var Win=open(Url,'Doc',Features);
}
function showCurrencyLOV()
{
	var searchString = document.forms[0].baseCurrency.value;
	myUrl= 'ETCCurrencyConversionAddLOV.jsp?searchString='+searchString+'&Operation=<%=operation%>';
	var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
	var myOptions ='scrollbars=yes,width=500,height=360,resizable=no';
	var myFeatures = myBars+','+myOptions;
	
	newWin = open(myUrl,'myDoc',myFeatures);
	if (newWin.opener == null) 
		newWin.opener = self;
	return true;

}
function showServiceLevelLOV()
{   
				
	var searchString=document.forms[0].serviceLevelId.value.toUpperCase();
	var Url		=	"etrans/ETCLOVServiceLevelIds1.jsp?searchString="+searchString+"&shipmentMode="+document.forms[0].shipmentMode.options.value;	
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
//added by phani sekhar FOR WPBN 179351 ON 20090812
	function showRegionLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;


	var searchString = (toSet=='originRegion')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2= (toSet=='originRegion')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
	var searchString3= (toSet=='originRegion')?(document.forms[0].originRegion.value!=null?document.forms[0].originRegion.value:""):(document.forms[0].destinationRegion.value!=null?document.forms[0].destinationRegion.value:"");
	var Url='etrans/ETCRegionIdsLOV.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value+'&searchString3='+searchString3+'&name='+toSet;
	var Win=open(Url,'Doc',Features);
}//ends 179351
function showCountryLOV(toSet)
{
	var locationId;
	if(toSet=='originCountry')
	{
		locationId = document.forms[0].origin.value;

		/*if(locationId.length==0)
		{
			alert('Please Enter/Select the Origin Location');
			document.forms[0].origin.focus();
			return false;
		}*/
	}
	else if(toSet=='destinationCountry')
	{
		locationId = document.forms[0].destination.value;

		/*if(locationId.length==0)
		{
			alert('Please Enter/Select the Destination Location');
			document.forms[0].destination.focus();
			return false;
		}*/
	}
		
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='originCountry')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
	var searchString3= (toSet=='originCountry')?(document.forms[0].originRegion.value):(document.forms[0].destinationRegion.value);//added by phani sekhar FOR WPBN 179351 ON 20090812

	var Url='etrans/ETCLOVCountryIds1.jsp?searchString='+searchString+"&whereClause=CSR&wheretoset="+toSet+"&locationId="+locationId+"&shipmentMode="+document.forms[0].shipmentMode.value+'&searchString3='+searchString3;//modified by phani sekhar FOR WPBN 179351 ON 20090812

	var Win=open(Url,'Doc',Features);
}
function showCarrierIds()
{
//@@ Commented and Added by subrahmanyam for the pbnd id: 201965 on 05-04-010
//	var URL 		= 'etrans/ETransLOVCarrierIds1.jsp?shipmentMode=All&multiple=multiple';	
	var searchString=document.forms[0].carriers.value.toUpperCase();
	var URL 		= 'etrans/ETransLOVCarrierIds1.jsp?searchString='+searchString+'&shipmentMode='+document.forms[0].shipmentMode.options.value+'&multiple=multiple';	
//@@ Ended by subrahmanyam for the pbnd id: 201965 on 05-04-010
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 700,height = 360,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
}
function setCarrierIdValues(values)
{
	var carrierId	=	document.forms[0].carriers;
	carrierId.options.length	=	0;
	for(var i=0;i<values.length;i++)
		carrierId.options[i]=new Option(values[i],values[i]);
}
function setCarrierIdValues(obj)
{
	var data="";
	for( i=0;i<obj.length;i++)
		{
		
		
		if(data!="")
			data=data+","+obj[i].value;
		else
			data=obj[i].value;
		}

		document.getElementById("carriers").value=data;
	
}
//End
//This function used for trim values
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
//End
function changeValue()
{
	if(document.forms[0].shipmentMode.value=="2")
	{
		if(document.forms[0].consoleType.value=="FCL")
		{
			document.forms[0].weightBreak.value="List"; 
			document.forms[0].rateType.value="Flat";
		}
		else if(document.forms[0].consoleType.value=="LCL" && document.forms[0].weightBreak.value=="List")
		{
			document.forms[0].weightBreak.value="Flat";
		}
	}
	else if(document.forms[0].shipmentMode.value=="4")
	{
		if(document.forms[0].consoleTypes.value=="FTL")
		{
			document.forms[0].weightBreak.value="List"; 
			document.forms[0].rateType.value="Flat";
		}
		else if(document.forms[0].consoleTypes.value=="LTL" && document.forms[0].weightBreak.value=="List")
		{
			document.forms[0].weightBreak.value="Flat";
		}
	}
}
function showRateType()
{

	var data="";
	var index	=	document.forms[0].weightBreak.value;
	var mode	=	document.forms[0].shipmentMode.value;

	if((index=="Flat") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL") ))
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="Flat" <%="Flat".equals(rateTpe) ? "SELECTED" : "" %>>Flat</option></select>';
	}
	else if((index=="Slab") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL")))
	{
		  data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="Flat" <%="Flat".equals(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="Slab" <%="Slab".equals(rateTpe) ? "SELECTED" : "" %>>Slab</option><option value="Both" <%="Both".equals(rateTpe) ? "SELECTED" : "" %>>Both</option></select>';
	}
	else if(index=="List" && mode=="1")
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option value="Pivot" <%="Pivot".equals(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
	}
	else
	{
		data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="Flat" <%="Flat".equals(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="Slab" <%="Slab".equals(rateTpe) ? "SELECTED" : "" %>>Slab</option><option value="Both" <%="Both".equals(rateTpe) ? "SELECTED" : "" %>>Both</option><option value="Pivot" <%="Pivot".equals(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
	}
	if( document.layers)
	{
		document.layers.cust2.document.write(data);
		document.layers.cust2.document.close();
	}
	else
	{
		if(document.all)
		{
		   cust2.innerHTML = data;
		 }
	 }
}
function disableSubmit()
{
	if(document.forms[0].next!=null)
	{
		var obj				= document.forms[0].next.value;
		if(obj=="DownLoad")
		{
			document.forms[0].next.disabled=true;
		}
	}

}
</script>
</head>

<body bgcolor="#FFFFFF" onLoad="showHide();showRateType();">
<form   method="post" action="QMSBuyRatesUploadController?Operation=DownLoad&subOperation=DownLoad" onSubmit="return validation(this)">

  <table width="102%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
       <table width="100%" cellpadding="4" cellspacing="1">
			<tr valign="top" class='formlabel'>
			   <td colspan="12" width="903">Buy Rates Master - <%=operation%> </td>
				<td align="right">QS1060141</td>
			</tr>
	</table>
<%
		if(request.getAttribute("Errors")!=null)
		{
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="1">
				<tr class='formdata' valign="top">
					<td width="33%"><font color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
						<%=((String)request.getAttribute("Errors"))%>
					</font></td>
				</tr>
			</table>
			
<%
		}
%>
		<table width="100%" cellpadding="4" cellspacing="1">
			<tr valign="top" class='formdata'>
			   <td>Shipment&nbsp;Mode:<br>
					<select name='shipmentMode' class='select' size=1  onChange='showHide();disableSubmit();changeValue();showRateType();'>
						<option value='1' <%="1".equals(shipmentMode) ? "SELECTED" : "" %>>Air</option>
						<option value='2'<%="2".equals(shipmentMode) ? "SELECTED" : "" %>>Sea</option>
						<option value='4'<%="4".equals(shipmentMode) ? "SELECTED" : "" %>>Truck</option>
					</select>
				</td>
				
				<td>
				  <div id='cust1' style='position:relative;'></div>
				</td>
				
				<td>Weight Break: <font color="#FF0000">*</font><br>
					<select size="1" name="weightBreak" class='select' onChange="changeValue();showRateType();disableSubmit()">
						<option selected value="Flat" <%="Flat".equals(weightBrk) ? "SELECTED" : "" %>>Flat</option>
						<option  value="Slab" <%="Slab".equals(weightBrk) ? "SELECTED" : "" %>>Slab</option>
						<option  value="List" <%="List".equals(weightBrk) ? "SELECTED" : "" %>>List</option>
					</select>
				</td>
				<td>
				  <div id='cust2' style='position:relative;'></div>
				</td>
				<!-- <td>Currency:<font  color=#ff0000>*</font><br>
					<input type='text' class='text' name='baseCurrency' value='<%//=currencyId!=null?currencyId:""%>' size='10' maxlength='3' onblur='this.value=this.value.toUpperCase()'>
					&nbsp;<input type='button' value='...' name='currencyLOV' class='input' onclick='showCurrencyLOV()'> -->
				</td>
				<td>Weight Class: <br>
					<select size="1" name="weightClass" class='select'>
						<option  value="G" <%="G".equals(weigtClas) ? "SELECTED" : "" %>>General</option>
						<option  value="WS" <%="WS".equals(weigtClas) ? "SELECTED" : "" %>>Weight Scale</option>
					</select>
				</td>
				</tr>
				</table>
				<table width="100%" cellpadding="4" cellspacing="1">
			<tr class='formheader'> 
				<td >Origin</td>
				<td>Origin<br>Countries</td>
				<td>Origin<br>Region</td><!-- added by phani sekhar FOR WPBN 179351 ON 20090812 -->
				<td>Destination</td>
				<td>Destination<br>Countries</td>
				<td>Destination<br>Region</td><!-- added by phani sekhar FOR WPBN 179351 ON 20090812 -->
				<td>Carrier</td>
				<td>Service<br>Level</td>
			</tr>
			<tr class='formdata'> 

				<td>
					<input type="text" name="origin" value='<%=origin!=null?origin:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='showLocationLOV("origin")'>
				</td>
				<td>
					<input type="text" name="originCountry"  value='<%=originCut!=null?originCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="originCountryLOV"  class='input' onclick='showCountryLOV("originCountry")'>
				</td>
				<td><!-- added by phani sekhar FOR WPBN 179351 ON 20090812 -->
					<input type="text" name="originRegion"  value='<%=originRegion!=null?originRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' >&nbsp;<input type="button" value="..." name="originRegionLOV"  class='input' onclick='showRegionLOV("originRegion")'>
				</td>
				<td>
					<input type="text" name="destination" value='<%=distenation!=null?distenation:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationLOV"  class='input' onclick='showLocationLOV("destination")'>
				</td>
				<td>
					<input type="text" name="destinationCountry" value='<%=distenationCut!=null?distenationCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationCountyLOV"  class='input' onclick='showCountryLOV("destinationCountry")'>
				</td>
				<td><!-- added by phani sekhar FOR WPBN 179351 ON 20090812-->
					<input type="text" name="destinationRegion" value='<%=distenationRegion!=null?distenationRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationRegionLOV"  class='input' onclick='showRegionLOV("destinationRegion")'>
				</td>
				<td>
					<input type="text"  name="carriers" value='<%=carrierStr!=null?carrierStr:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="gatebut"  class='input' onclick='showCarrierIds()'>
					
				</td>
				<td>
					<input type="text" name="serviceLevelId" value='<%=serviceLevel!=null?serviceLevel:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="serviceLevelLOV"  class='input' onclick='showServiceLevelLOV()'>
				</td>
			</tr>
			<tr class='denotes'>             
				<td colspan='4'> 
					<font color=red> * 
					</font>Each text box is multiple selection with comma separation. User can enter manually or select from LOV
				</td>				
				<td colspan='2' align='right'> 
			
					<input type="hidden" name="Operation" value="<%=operation%>">
					<input type="hidden" name="subOperation" value="<%=operation%>">
					<input type="submit" class='input' name="DownLoad" value="DownLoad"  >
				</td>
			</tr>
			</table>

             
</form>
</body>
</html>
<%		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in QMSRecommendedSellRateEnter.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSRecommendedSellRateEnter.jsp "+e);
		
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details  ","QMSSellRateController?Operation="+operation+"&subOperation=Enter"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
	    	
	
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
%>
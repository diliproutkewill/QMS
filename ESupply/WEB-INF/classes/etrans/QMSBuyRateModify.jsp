<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSBuyRateView.jsp
	Product Name	: QMS
	Module Name		: BUY Rate View
	Task		    : Adding/View/Modify/Invalidate Recommended Sell Rate
	Date started	: 26-07-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: 
	Description		: The application "Adding/View/Modify/Invalidate" BUY Rate
	Actor           :
	Related Document: CR_DHLQMS_1003

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						java.util.HashMap,
						java.util.Set,
						java.util.LinkedHashSet,
						java.util.Iterator,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.rates.dob.FlatRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSBuyRateModify.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ArrayList			listValues				=	null;
		ArrayList			listValue				=	null;
		ArrayList			finalList				=	null;
		ArrayList			headerlist				=	null;
		ArrayList			weightBreakList			=	null;
		ArrayList			chargeDtlList			=	null;
		ErrorMessage		errorMessageObject		=   null;

		ArrayList			fslListValues			=	null;
		//QMSSellRatesDOB		sellRatesDob			=	null;
		QMSSellRatesDOB		sellDob					=	null;
		QMSBoundryDOB		boundryDob				=	null;
		QMSSellRatesDOB		sellRatesDob1			=	null;
		HashMap				mapValues				=	null;
		String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
		String				terminalId				=	loginbean.getTerminalId();
		String				operation				=	null;
		String				keyHash					=	null;
		Iterator			itr						=	null;
   		Iterator			itr1					=	null;
		Iterator			itr2					=	null; // Added by Gowtham For SurCharge Currency
		String				origin					=	null;
		String				originCut				=	null;
		String				originRegion			=	null;//added by phani sekhar for wpbn 171213 on 20090605
		String				distenation				=	null;
		String				distenationCut			=	null;
		String				distenationRegion		=	null;//added by phani sekhar for wpbn 171213 on 20090605
		String				currencyId				=	null;
		String				serviceLevel			=	null;
		String				carrierStr				=	null;

		String				shipmentMode			=	null;
		String				weightBrk				=	null;
		String				rateTpe					=	null;
		String				weigtClas				=	null;
		/*String				overallMrg				=	null;
		String				marginTpe				=	null;
		String				marginBass				=	null;*/
		String				consoleTpe				=	null;
		String				subOperation			=	null;
		String				invalidate				=	null;
		String				weightbreak_indicator	=	null;
		String				rate					=	null;
		String				rate1					=	null;

		int					noofRecords						=	0;
		int					noofPages						=	0;
		int                 count                           =   0;
		String				checked					= "";	
		FlatRatesDOB        flatRateDOB				=	null;
		String[] chargesValue			=	null;
		String[] chargesValueStr		=	null;
		String[] chargeRateIndicator	=	null;
		int      noOfSegments			=	0;
		int      temp ;
		int      dumtemp;
		int		 srCount=0;
		try
		{
			noOfSegments            =  Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
			operation				=	request.getParameter("Operation");
			subOperation			=	request.getParameter("subOperation");
			String contextPath	=	request.getContextPath();
			
			listValue				=	(ArrayList)session.getAttribute("HeaderValue");
			//session.removeAttribute("HeaderValue");

			if(listValue!=null)
			{
					sellDob					=	(QMSSellRatesDOB)listValue.get(0);
					
					currencyId				=	sellDob.getCurrencyId();
					origin					=	sellDob.getOrigin();
					originCut				=	sellDob.getOriginCountry();
					distenation				=	sellDob.getDestination();
					distenationCut			=	sellDob.getDestinationCountry();
					serviceLevel			=	sellDob.getServiceLevel();
					carrierStr				=	sellDob.getCarrier_id();
					shipmentMode			=	sellDob.getShipmentMode();
					weightBrk				=	sellDob.getWeightBreak();
					rateTpe					=	sellDob.getRateType();
					weigtClas				=	sellDob.getWeightClass();
					/*overallMrg				=	sellDob.getOverAllMargin();
					marginTpe				=	sellDob.getMarginType();
					marginBass				=	sellDob.getMarginBasis();*/
					consoleTpe				=	sellDob.getConsoleType();
					originRegion			=   sellDob.getOriginRegions();//added by phani sekhar for wpbn 171213 on 20090605
					distenationRegion		=  sellDob.getDestRegions();//added by phani sekhar for wpbn 171213 on 20090605

				}


			listValue				=	(ArrayList)session.getAttribute("HeaderValues");
			logger.info(FILE_NAME+"HeaderValues::"+listValue);
      logger.info(FILE_NAME+"HeaderValues::"+listValue);
			//session.removeAttribute("HeaderValues");

			if(listValue!=null)
			{
					sellDob					=	(QMSSellRatesDOB)listValue.get(0);
					finalList				=	(ArrayList)listValue.get(1);
					fslListValues           =   (ArrayList)finalList.get(0);
					Integer  no_ofRecords   =   (Integer)finalList.get(1);
					Integer  no_ofPages		=	(Integer)finalList.get(2);
					noofRecords				=	no_ofRecords.intValue();
					noofPages				=	no_ofPages.intValue();
					//currencyId				=	sellDob.getCurrencyId();
					origin					=	sellDob.getOrigin();
					originCut				=	sellDob.getOriginCountry();
					distenation				=	sellDob.getDestination();
					distenationCut			=	sellDob.getDestinationCountry();
					serviceLevel			=	sellDob.getServiceLevel();
					carrierStr				=	sellDob.getCarrier_id();
					shipmentMode			=	sellDob.getShipmentMode();
					weightBrk				=	sellDob.getWeightBreak();
					rateTpe					=	sellDob.getRateType();
					weigtClas				=	sellDob.getWeightClass();
					/*overallMrg				=	sellDob.getOverAllMargin();
					marginTpe				=	sellDob.getMarginType();
					marginBass				=	sellDob.getMarginBasis();*/
					consoleTpe				=	sellDob.getConsoleType();
					originRegion			=   sellDob.getOriginRegions();//added by phani sekhar for wpbn 171213 on 20090605
					distenationRegion		=  sellDob.getDestRegions();//added by phani sekhar for wpbn 171213 on 20090605

					int noofPage	=	Integer.parseInt(sellDob.getPageNo());

					mapValues				=   (HashMap)session.getAttribute("hm_buyRates");
				
					}


			/*
			if(listValues!=null)
			{
					sellRatesDob	=	(QMSSellRatesDOB)listValues.get(0);
					
					currencyId		=	sellRatesDob.getCurrencyId();
					origin			=	sellRatesDob.getOrigin();
					originCut		=	sellRatesDob.getOriginCountry();
					distenation		=	sellRatesDob.getDestination();
					distenationCut	=	sellRatesDob.getDestinationCountry();
					serviceLevel	=	sellRatesDob.getServiceLevel();
					carrierStr		=	sellRatesDob.getCarrier_id();
		
					shipmentMode			=	sellRatesDob.getShipmentMode();
					weightBrk				=	sellRatesDob.getWeightBreak();
					rateTpe					=	sellRatesDob.getRateType();
					weigtClas				=	sellRatesDob.getWeightClass();
					overallMrg				=	sellRatesDob.getOverAllMargin();
					marginTpe				=	sellRatesDob.getMarginType();
					marginBass				=	sellRatesDob.getMarginBasis();
					consoleTpe				=	sellRatesDob.getConsoleType();
					if("1".equals(shipmentMode) || ("2".equals(shipmentMode) && "LCL".equals(consoleTpe)) || ("4".equals(shipmentMode) && "LTL".equals(consoleTpe)))
					{
						if("Flat".equals(sellRatesDob.getWeightBreak()))
						{
								mapValues		=	(HashMap)listValues.get(1);
								itr				=	(mapValues.keySet()).iterator();
						}
						else if("Slab".equals(sellRatesDob.getWeightBreak()))
						{
								headerlist			=	(ArrayList)listValues.get(1);
								weightBreakList		=	(ArrayList)headerlist.get(0);
								session.setAttribute("weightBreak",weightBreakList);
								mapValues			=	(HashMap)headerlist.get(1);
								itr					=	(mapValues.keySet()).iterator();
						}
						else if("List".equals(sellRatesDob.getWeightBreak()))
						{
								headerlist			=	(ArrayList)listValues.get(1);
								weightBreakList		=	(ArrayList)headerlist.get(0);
								session.setAttribute("weightBreak",weightBreakList);
								mapValues			=	(HashMap)headerlist.get(1);
								itr					=	(mapValues.keySet()).iterator();
						}
					}
					else
					{
							headerlist			=	(ArrayList)listValues.get(1);
							weightBreakList		=	(ArrayList)headerlist.get(0);
							session.setAttribute("weightBreak",weightBreakList);
							mapValues			=	(HashMap)headerlist.get(1);
							itr					=	(mapValues.keySet()).iterator();
					}
			}*/
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

	function clearValue(obj,val)
	{
		document.getElementsByName(obj)[val].value='';
	}
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
		firstTemp	=	firstTemp.substring(0,lastIndex);		
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

// Function for surcharge currency lov 
function surchargeCurrencyLOV(obj)
{//	alert(obj)
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
	myUrl=	'<%=contextPath%>/etrans/ETCCurrencyConversionAddLOV.jsp?fromWhere=surchargeCurr&toWhere='+obj+"&searchString="+document.getElementById(obj).value.toUpperCase();
	//	alert(myUrl)
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;

}
 // End 



// Added By Gowtham For PDF View Issue
function pop(input)
{	
	//var input = document.forms[0].input1.value.toUpperCase();
//alert("pop function:: ----"+input);
		var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var Options ='scrollbars=yes,width=360,height=360,resizable=no';
		var Features = Bars+','+Options;

		Url= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+input ;              //document.forms[0].this.value.toUpperCase();
		
		var newWin = open(Url,'Doc',Features);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;

}
// End Of Gowtham For PDF View Issue

/*function getDotNumberCode(val)
{
   if(event.keyCode!=13)
	{	
		 if((event.keyCode <= 45 ) || (event.keyCode > 57) )
			 return false;	
	}
}*/

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
	

/*	if(document.forms[0].baseCurrency.value.length==0)
	{
		msgErrors	+="Please Enter/Select CurrencyId.\n";
		focusPosition[0]	=	"baseCurrency";
	} */
//modified by phani sekhar for wpbn 171213 on 20090605
	if(document.forms[0].origin.value.length==0 && document.forms[0].destination.value.length==0 
		&& document.forms[0].carriers.value.length==0 && document.forms[0].serviceLevelId.value.length==0 &&document.forms[0].originRegion.value.length==0&&document.forms[0].originCountry.value.length==0&&document.forms[0].destinationRegion.value.length==0&&document.forms[0].destinationCountry.value.length==0)
	{
		msgErrors	+="Please Enter/Select Origin/OriginCountry/OriginRegion/Destination/DestinationCountry/DestinationRegion/Carriers/ServiceLevelId.\n";
		focusPosition[1]	=	"origin";
	}
	/*
	if(document.forms[0].origin.value.length==0)
	{
		msgErrors	+="Please Enter/Select OriginId.\n";
		focusPosition[1]	=	"origin";
	}
	if(document.forms[0].destination.value.length==0)
	{
		msgErrors	+="Please Enter/Select DestinationId.\n";
		focusPosition[2]	=	"destination";
	}
	if(document.forms[0].carriers.value.length==0)
	{
		msgErrors	+="Please Enter/Select Carriers.\n";
		focusPosition[3]	=	"carriers";
	}
	if(document.forms[0].serviceLevelId.value.length==0)
	{
		msgErrors	+="Please Enter/Select ServiceLevelId.\n";
		focusPosition[4]	=	"serviceLevelId";
	}*/
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



//	alert(obj.name)
	
	


	if(obj.name=="next")
	{
		flag = false;
		var cnt = document.getElementById("count").value;
<%
	if(mapValues!=null &&  mapValues.size()<=0)
		{
%>
			for(i=0;i<cnt;i++)
			{
				if(document.getElementById(i).checked==true)
				{
					flag = true;
				}
			}
<%
		}else
		{%>
			flag = true;
<%
		}%>
		if(flag==true)
		{
			document.forms[0].action='BuyRatesController?Operation=Modify&subOperation=values';
			document.forms[0].submit();
			return true;
		}else
		{
			alert("Please select atleast one Lane to <%=operation%>");
			return false;
		}

	}
//alert('Operation='+operation+'&subOperation='+operation)
	if(obj.name=="search")
	{
		document.forms[0].action='BuyRatesController?Operation='+operation+'&subOperation='+operation;
		document.forms[0].submit();
		return true;
	}
	
	
}

function showHide()
{

	var data="";
	var index	=	document.forms[0].shipmentMode.value;
	if(index=="2")
	{
		 data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleType" class="select" onChange="changeValue(this);showRateType();disableSubmit();"><option  value="LCL" <%="LCL".equals(consoleTpe) ? "SELECTED" : "" %>>LCL</option><option  value="FCL" <%="FCL".equals(consoleTpe) ? "SELECTED" : "" %>>FCL</option></select>';
	}
	else if(index=="4")
	{
		data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleTypes" class="select" onChange="changeValue(this);showRateType();disableSubmit()"><option  value="LTL" <%="LTL".equals(consoleTpe) ? "SELECTED" : "" %>>LTL</option><option  value="FTL" <%="FTL".equals(consoleTpe) ? "SELECTED" : "" %>>FTL</option></select>';
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


/*function changeValue()
{
	if(document.forms[0].shipmentMode.value=="2")
	{
		if(document.forms[0].consoleType.value=="FCL")
		{
			document.forms[0].weightBreak.value="List"; 
			document.forms[0].rateType.value="Flat";
		}
	}
	else if(document.forms[0].shipmentMode.value=="4")
	{
		if(document.forms[0].consoleTypes.value=="FTL")
		{
			document.forms[0].weightBreak.value="List"; 
			document.forms[0].rateType.value="Flat";
		}
	}
}*/
function showHideSelectBox()
{
	if((document.forms[0].weightBreak.value=="Flat" && 
		document.forms[0].rateType.value=="Flat") || 
		(document.forms[0].shipmentMode.value=="2" && 
		document.forms[0].consoleType.value=="LCL") || 
		(document.forms[0].shipmentMode.value=="4" && 
		document.forms[0].consoleTypes.value=="LTL"))
	{
		marginBasisSpan.style.visibility='visible';
	}
	else
	{
		marginBasisSpan.style.visibility='hidden';
	}
}	//added by phani sekhar for wpbn 171213 on 20090605
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
}//ends 171213

//This function used for call the LOVs
function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2= (toSet=='origin')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
var searchString3= (toSet=='origin')?(document.forms[0].originRegion.value!=null?document.forms[0].originRegion.value:""):(document.forms[0].destinationRegion.value!=null?document.forms[0].destinationRegion.value:"");//added by phani sekhar for wpbn 171213 on 20090605
//moiified by phani sekhar for wpbn 171213 on 20090605
var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value+'&searchString3='+searchString3;
	var Win=open(Url,'Doc',Features);
}
function showDensityRatio(obj)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=no';
	var Features=Bars+' '+Options;
	myUrl=  'etrans/QMSDensityRatioLOV.jsp?shipmentMode='+document.forms[0].shipmentMode.options.value+'&name='+obj;
	var Win	   =  open(myUrl,'Doc',Features);
}
function showCurrencyLOV(id) // Added by Gowtham on 15Feb2011
{	
  // alert(id)
	var searchString = document.getElementById(id).value;
	//alert(searchString);
	//alert(id);
	
	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?Operation=<%=operation%>&name='+id+'&fromWhere=buycharges&searchString='+searchString;
	//myUrl = 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString=INR';
	//alert(myUrl);
	var myBars = 'directories=no,location=no,menubar=no,status=yes,titlebar=no,toolbar=no';
	var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
	var myFeatures = myBars+','+myOptions;
	
	newWin = open(myUrl,'myDoc',myFeatures);
	if (newWin.opener == null) 
		newWin.opener = self;
	return true;

} // Added by Gowtham on 15Feb2011

/*function showCurrencyLOV()
{
	var searchString = document.forms[0].baseCurrency.value;
	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+searchString+'&Operation=<%=operation%>';
	var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
	var myOptions ='scrollbars=yes,width=500,height=360,resizable=no';
	var myFeatures = myBars+','+myOptions;
	
	newWin = open(myUrl,'myDoc',myFeatures);
	if (newWin.opener == null) 
		newWin.opener = self;
	return true;

}*/
function showServiceLevelLOV()
{   
				
	var searchString=document.forms[0].serviceLevelId.value.toUpperCase();
	var Url		=	"etrans/ETCLOVServiceLevelIds1.jsp?searchString="+searchString+"&shipmentMode="+document.forms[0].shipmentMode.options.value;	
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

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
var searchString3= (toSet=='originCountry')?(document.forms[0].originRegion.value!=null?document.forms[0].originRegion.value:""):(document.forms[0].destinationRegion.value!=null?document.forms[0].destinationRegion.value:"");//added by phani sekhar for wpbn 171213 on 20090605
	var Url='etrans/ETCLOVCountryIds1.jsp?searchString='+searchString+"&whereClause=CSR&wheretoset="+toSet+"&locationId="+locationId+"&shipmentMode="+document.forms[0].shipmentMode.value+'&searchString3='+searchString3;//modified by phani sekhar for wpbn 171213 on 20090605

	var Win=open(Url,'Doc',Features);
}
function showCarrierIds()
{
	var URL 		= 'etrans/ETransLOVCarrierIds1.jsp?searchString='+document.forms[0].carriers.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.value+'&multiple=multiple';	
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
function checkFileds(obj,hiddenName)
{
	
	if(obj.checked)
	{
		document.getElementsByName(hiddenName)[obj.id].value="Yes";
	}
	else
	{
		document.getElementsByName(hiddenName)[obj.id].value="No";
	}
   
}
function selectOne(obj)
{
<%
	if("Slab".equalsIgnoreCase(weightBrk) || "List".equalsIgnoreCase(weightBrk))
	{
%>
		objValue	='min'+obj.name;
		var minRates=document.getElementsByName(objValue);
		//var flatRates=document.getElementsByName("mFlatRates");

			if(obj.checked)
			{
				m=0;
				for(var i=0;i<document.forms[0].elements.length;i++)
				{
					if(document.forms[0].elements[i].type=="checkbox" && document.forms[0].elements[i].name==obj.name)
					{
						document.forms[0].elements[i].checked=true;
						minRates[m].value="Yes";
					}
					
				}
			}
			else
			{
				m=0;
				for(var i=0;i<document.forms[0].elements.length;i++)
				{
					if(document.forms[0].elements[i].type=="checkbox" && document.forms[0].elements[i].name==obj.name)
					{
						document.forms[0].elements[i].checked=false;
						minRates[m].value="No";
					}
					
				}
			}
	
			
<%
	}
%>

}

function disableSubmit()
{
	if(document.forms[0].next!=null)
	{
		var obj				= document.forms[0].next.value;
		if(obj=="Submit")
		{
			document.forms[0].next.disabled=true;
		}
	}

}

function selectAll(input)
{
	var rows = 0;
<%
	if(fslListValues!=null && fslListValues.size()>0)
	{
%>
		rows = '<%=fslListValues.size()%>';
<%
	}
%>
	for(var i=0;i<rows;i++)
	{
	    
		if(input.checked)
		{
			document.getElementById(i).checked = true;
		}else
		{
			document.getElementById(i).checked = false;
		}
	}
}
function functionCall(pageNo,operation)
{
	
	document.forms[0].action='BuyRatesController?pageNo='+pageNo+'&Operation=<%=operation%>&subOperation=pageIterator';
	document.forms[0].submit();
}

function showRateType()
{
//	alert("dalfjkdsa");
	var data="";
	var index	=	document.forms[0].weightBreak.value;
	var mode	=	document.forms[0].shipmentMode.value;

	if((index=="Flat") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL") ))
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="Flat" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option></select>';
	}
	else if((index=="Slab") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL")))
	{
		  data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option value="Slab" <%="SLAB".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Slab</option><option  value="Flat" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="Both" <%="BOTH".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Both</option></select>';
	}
	else if(index=="List" && mode=="1")
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option value="Pivot" <%="PIVOT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
	}
	else
	{
		data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="Flat" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="Slab" <%="SLAB".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Slab</option><option value="Both" <%="Both".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Both</option><option value="Pivot" <%="PIVOT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
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
	function checkNumbers1(input)
	{
		if(trim(input.value).length>0)
		{
			//if(isNaN(trim(input.value))//@@Commented and Modified by Kameswari for the WPBN issue-143508 on 04/11/08
			if(isNaN(trim(input.value))||trim(input.value)<0)
			{
				//alert("Please do not enter characters for "+label);
				input.value='';
				input.focus();
				return false;
			}
		}
		return true
	}
	function trim(input)
	 { 
		while (input.substring(0,1) == ' ') 
			input = input.substring(1, input.length);

		while (input.substring(input.length-1,input.length) == ' ')
			input = input.substring(0, input.length-1);

	   return input;
	 }
//@@Added by Govind for the cr-219973
function getSlabFlatIndCheck(obj)
			{
	
	var val = event.keyCode;
	if(val == 115 || val == 102 || val == 83 || val == 70)
			return true;
	else 
		return false;
			}
function setChargeIndicatorForSurcharges(obj,m)
			{
	           var srInd = '<%=chargeRateIndicator%>';
			   alert(m)
			}

</script>
</head>

<body bgcolor="#FFFFFF" onLoad="showHide();showRateType()">
<form   method="post">

  <table width="102%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
       <table width="100%" cellpadding="4" cellspacing="1">
			<tr valign="top" class='formlabel'>
			   <td colspan="12" width="903">Buy Rates Master - Modify</td>
				<td align="right">QS1060121</td>
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
					<select name='shipmentMode' class='select' size=1  onChange='showHide();disableSubmit();showRateType();changeValue()'>
						<option value='1' <%="1".equals(shipmentMode) ? "SELECTED" : "" %>>Air</option>
						<option value='2'<%="2".equals(shipmentMode) ? "SELECTED" : "" %>>Sea</option>
						<option value='4'<%="4".equals(shipmentMode) ? "SELECTED" : "" %>>Truck</option>
					</select>
				</td>
				
				<td>
				  <div id='cust1' style='position:relative;'></div>
				</td>
				
				<td>Weight Break: <font color="#FF0000">*</font><br>
					<select size="1" name="weightBreak" class='select' onChange="changeValue();disableSubmit();showRateType()">
						<option selected value="Flat" <%="Flat".equals(weightBrk) ? "SELECTED" : "" %>>Flat</option>
						<option  value="Slab" <%="Slab".equals(weightBrk) ? "SELECTED" : "" %>>Slab</option>
						<option  value="List" <%="List".equals(weightBrk) ? "SELECTED" : "" %>>List</option>
					</select>
				</td>
				<td><!-- Rate Type: <font color="#FF0000">*</font><br> -->
					<div id='cust2' style='position:relative;'></div>
					<!-- <select size="1" name="rateType" class='select' onChange="changeValue();disableSubmit();">
						<option  value="Flat" <%="Flat".equals(rateTpe) ? "SELECTED" : "" %>>Flat</option>
						<option value="Slab" <%="Slab".equals(rateTpe) ? "SELECTED" : "" %>>Slab</option>
						<option value="Both" <%="Both".equals(rateTpe) ? "SELECTED" : "" %>>Both</option>
						<option value="Pivot" <%="Pivot".equals(rateTpe) ? "SELECTED" : "" %>>Pivot</option>
					</select> -->
				</td>
<!-- 				<td>Currency:<font  color=#ff0000>*</font><br>
					<input type='text' class='text' name='baseCurrency' value='<%=currencyId!=null?currencyId:""%>' size='10' maxlength='3' onblur='this.value=this.value.toUpperCase()'>
					&nbsp;<input type='button' value='...' name='currencyLOV' class='input' onclick='showCurrencyLOV()'>
				</td> -->
				<td>Weight Class: <br>
					<select size="1" name="weightClass" class='select' onchange='disableSubmit();'>
						<option  value="G" <%="G".equals(weigtClas) ? "SELECTED" : "" %>>General</option>
						<option  value="WS" <%="WS".equals(weigtClas) ? "SELECTED" : "" %>>Weight Scale</option>
					</select>
				</td>
				</tr>
				</table>
				<table width="100%" cellpadding="3" cellspacing="1">
			<tr class='formheader'><!-- modified by phani sekhar for 171213 on 20090605 --> 
				<td>Origin:</td>
				<td>Origin<br>Countries:</td>
				<td>Origin<br>Region:</td>
				<td >Destination:</td>
				<td >Destination<br>Countries:</td>
				<td >Destination<br>Region:</td>
				<td >Carrier:</td>
				<td >Service<br>Level</td>	<!-- <td width='10%'/> -->
			</tr>
			<tr class='formdata'> 

				<td>
					<input type="text" name="origin" value='<%=origin!=null?origin:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' onchange='disableSubmit();'>&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='showLocationLOV("origin")'>
				</td>
				<td>
					<input type="text" name="originCountry"  value='<%=originCut!=null?originCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' >&nbsp;<input type="button" value="..." name="originCountryLOV"  class='input' onclick='showCountryLOV("originCountry")'>
				</td>
				<td><!-- added by phani sekhar for wpbn 171213 on 20090605 -->
					<input type="text" name="originRegion"  value='<%=originRegion!=null?originRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' >&nbsp;<input type="button" value="..." name="originRegionLOV"  class='input' onclick='showRegionLOV("originRegion")'>
				</td>
				<td>
					<input type="text" name="destination" value='<%=distenation!=null?distenation:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' onchange='disableSubmit();'>	&nbsp;<input type="button" value="..." name="destinationLOV"  class='input' onclick='showLocationLOV("destination")'>
				</td>
				<td><!-- added by phani sekhar for wpbn 171213 on 20090605 -->
					<input type="text" name="destinationCountry" value='<%=distenationCut!=null?distenationCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationCountyLOV"  class='input' onclick='showCountryLOV("destinationCountry")'>
				</td>
				<td>
					<input type="text" name="destinationRegion" value='<%=distenationRegion!=null?distenationRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationRegionLOV"  class='input' onclick='showRegionLOV("destinationRegion")'>
				</td>
				<td>
					<input type="text"  name="carriers" value='<%=carrierStr!=null?carrierStr:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()' onchange='disableSubmit();'>&nbsp;<input type="button" value="..." name="gatebut"  class='input' onclick='showCarrierIds()'>
					
				</td>
				<td>
					<input type="text" name="serviceLevelId" value='<%=serviceLevel!=null?serviceLevel:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()' onchange='disableSubmit();'>&nbsp;<input type="button" value="..." name="serviceLevelLOV"  class='input' onclick='showServiceLevelLOV()'>
				</td>
			</tr>
			<tr class='denotes'>             
				<td colspan='4'> 
					<font color=red> * 
					</font>Each text box is multiple selection with comma separation. User can enter manually or select from LOV
				</td>
				<td colspan='2'/> 
				<td colspan='2' align='right'> 
					<input type="button" class='input' name="search" value="Search"  onClick="return validation(this)">
				</td>
				<input type='hidden' name="pageNo" value="1">
			</tr>
			</table>
			<table width="100%" cellpadding="4" cellspacing="1">

<%	
	if(fslListValues!=null && fslListValues.size()>0)
	{
	
%>
				<tr class='formheader'> 
					<td><input type='checkbox' name="select1" onClick="selectAll(this)">Modify</td>
					<td>Origin:</td>
					<td>Origin<br>Country:</td>
					<td>Destination:</td>
					<td>Destination<br>Country:</td>
					<td>Carrier:&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td >Service&nbsp;&nbsp;&nbsp;&nbsp;<br>Level:&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<%if("1".equalsIgnoreCase(shipmentMode)||"4".equalsIgnoreCase(shipmentMode)){%>
							<td>Approximate<br>Transit Time:</td>
						<%}else{%>
						<td>Approximate<br>Transit Days:</td>
						<%}%>
					<td>Frequency:</td>
					<td>Currency:</td>
<%
                    sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);   
					String[]    rateDescs         =   sellRatesDob1.getRateDescription(); 
					String[]           surChargeCurr                = null; // Added by Gowtham for Surchage Currency 
				String[] weighttBkValues		=	sellRatesDob1.getWeightBreaks();
					
					int	wtBreakLength				=	weighttBkValues.length;
					logger.info("weighttBkValues.length"+weighttBkValues.length);
					//String rateDesc              	=  sellRatesDob1.getRateDescription();   
						//logger.info("rateDescs"+rateDesc);
					chargesValue			=	sellRatesDob1.getChargeRates();
					//System.out.println("count-----"+count) ;   
					for(int j=0;j<wtBreakLength;j++)
					{
            //           System.out.println("rateDescs[j]-----"+rateDescs[j]+"---weighttBkValues[j]-------"+weighttBkValues[j]) ;   
							if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[j])))
						{
							 
							// count++;
							 if(weighttBkValues[j]!=null&&weighttBkValues[j]!="")
							{
								 %>
						
				<td ><%=weighttBkValues[j]%></td>
					 
<%				 count++; //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 

						}
                        }
					}
             
		
%>						<td>Density Ratio</td>
					<!-- Modified by Mohan for Issue No.219976 on 30-10-2010 -->
					 <td>Internal Notes</td>
					 <td>External Notes</td> <td/>
				</tr>
<%
			int	fslListValuessize				=	fslListValues.size();
				int chargesLength = 0;
				String notes = "";
				String extNotes = "";//Added by Mohan for Issue No.219976 on 30-10-2010
				String schCurrency = ""; // Gowtham for update SCHcurrency in DB
			for(int i=0;i<fslListValuessize;i++)
			{
				sellRatesDob1	=	(QMSSellRatesDOB)fslListValues.get(i);
					weighttBkValues		=	sellRatesDob1.getWeightBreaks();
						wtBreakLength				=	weighttBkValues.length;
					 rateDescs         =   sellRatesDob1.getRateDescription(); 
 					surChargeCurr	=	  sellRatesDob1.getSurChargeCurency(); // Added by Gowtham for Surcharge 
					Set rateDescset				  =   new LinkedHashSet();//Govind for srcharge display
					Set surChargeCurrset				  =   new LinkedHashSet(); // Added by Gowtham or Surcharge
					for(String s:rateDescs)
                         rateDescset.add(s);
                                       // Added by Gowtham or Surcharge

                  	for(int x = 0; x<surChargeCurr.length;x++){
						
                         surChargeCurrset.add(surChargeCurr[x]+"~"+rateDescs[x]);
                                      // End Of Gowtham or Surcharge
					}
				if(mapValues!=null &&  mapValues.get(sellRatesDob1.getBuyRateId()+"_"+sellRatesDob1.getLanNumber())!=null)
				{
					    flatRateDOB = (FlatRatesDOB)mapValues.get(sellRatesDob1.getBuyRateId()+"_"+sellRatesDob1.getLanNumber());
						chargesValueStr = flatRateDOB.getRates();
						notes       = ((flatRateDOB.getRemarks()!=null) ? flatRateDOB.getRemarks() :"" );
						//Added by Mohan for Issue No.219976 on 30-10-2010
						extNotes    = ((flatRateDOB.getExtNotes()!=null) ? flatRateDOB.getExtNotes() :"" );
						schCurrency = ((flatRateDOB.getSurchargeCurrency()!=null) ? flatRateDOB.getSurchargeCurrency() :"" ); // Gowtham for Update SCHCurrency in DB.
						//chargesLength		=	chargesValueStr.length;
						checked = "checked";
				}else
				{
						checked = "";
						notes = (sellRatesDob1.getNotes()!=null)?sellRatesDob1.getNotes():"";
						//Added by Mohan for Issue No.219976 on 30-10-2010
						extNotes    = ((sellRatesDob1.getExtNotes()!=null) ? sellRatesDob1.getExtNotes() :"" );
				}

%>
				<tr class='formdata'> 
					
					<input type="hidden" name="lanNumber<%=i%>" value='<%=sellRatesDob1.getLanNumber()%>' >
					<input type="hidden" name="buyRateId<%=i%>" value='<%=sellRatesDob1.getBuyRateId()%>' >

					<td><input type=checkbox name='<%=i%>' id='<%=i%>' <%=checked%>></td>
					<td><%=sellRatesDob1.getOrigin()!=null?sellRatesDob1.getOrigin():""%></td>
					<td><%=sellRatesDob1.getOriginCountry()!=null?sellRatesDob1.getOriginCountry():""%></td>
					<td><%=sellRatesDob1.getDestination()!=null?sellRatesDob1.getDestination():""%></td>
					<td><%=sellRatesDob1.getDestinationCountry()!=null?sellRatesDob1.getDestinationCountry():""%></td>
					<td><%=sellRatesDob1.getCarrier_id()!=null?sellRatesDob1.getCarrier_id():""%></td>
					<td><%=sellRatesDob1.getServiceLevel()!=null?sellRatesDob1.getServiceLevel():""%></td>
					<td><%=sellRatesDob1.getTransitTime()!=null?sellRatesDob1.getTransitTime():""%></td>
					<td><%=sellRatesDob1.getFrequency()!=null?sellRatesDob1.getFrequency():""%></td>
					<td><%=sellRatesDob1.getCurrencyId()!=null?sellRatesDob1.getCurrencyId():""%></td>
<%

						chargesValue			=	sellRatesDob1.getChargeRates();
					
						/*for(String s:chargesValue)
							System.out.println("chargesValue-------"+s);*/
						chargeRateIndicator	=	sellRatesDob1.getChargeInr();
						chargesLength		=	chargesValue.length;
						

					rate	=	"";
					rate1	=	"";
					
					//System.out.println("chargesLengthchargesLengthchargesLengthchargesLengthchargesLength:: "+chargesLength);
					int rateIndex = 0;
					for(int k=0;k<count;k++)
					{
						//System.out.println("chargesValue[k]chargesValue[k]chargesValue[k]chargesValue[k]:: "+chargesValue[k]);
						rate	=	"";
						rate1	=	"";
						
                     if(chargesValue[k]!=null&&chargesValue[k]!="")
						{
						if(k==0)
						{
								if(!"checked".equals(checked))
								{
									rate = chargesValue[k];
								}else
								{
									rate = chargesValueStr[rateIndex++];
								}
							if(!"-".equals(chargesValue[k]))
							{
								if("slab".equalsIgnoreCase(weightBrk) && "Both".equalsIgnoreCase(rateTpe))
								{

%>									<td><input type=text name='basic<%=i%>'  maxlength=10 value='<%=rate%>' size=5 class=text										onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>
									
<%								}else 
								{
%>
<!-- modified by subrahmanyam for the id:216340 -->
									<td><input type=text name='flat<%=i%>'  maxlength=10 value='<%=rate%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'><input type=hidden  name='indicator<%=i%>'  maxlength=1 id=<%=srCount%><%=k%> value='<%=chargeRateIndicator[k]%>' size=1 ></td>									<%
								}
							}
							else
							{
%>								<td></td>
<%							}
%>

<%						}else if(k==1){
							if(!"checked".equals(checked))
								{
									rate = chargesValue[k];
								}else
								{
									rate = chargesValueStr[rateIndex++];
								}
							if(!"-".equals(chargesValue[k]))
							{
								if("slab".equalsIgnoreCase(weightBrk) && "Both".equalsIgnoreCase(rateTpe))
								{
%>								
							<td><input type=text name='min<%=i%>'  maxlength=10 value='<%=rate%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>
<%								}else 
								{
%>
<!-- modified by subrahmanyam for the id:216340 -->
									<td><input type=text name='flat<%=i%>'  maxlength=10 value='<%=rate%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'><input type=hidden  name='indicator<%=i%>'  maxlength=1 id=<%=srCount%><%=k%> value='<%=chargeRateIndicator[k]%>' size=1 >
									</td>									<%
								}
							}
							else
							{
%>								<td></td>
<%							}
}else
						{
							//Logger.info(FILE_NAME,"chargesValue[k]::"+chargesValue[k]);
							if(!"-".equals(chargesValue[k]))
							{

								if("slab".equalsIgnoreCase(weightBrk) && "Both".equalsIgnoreCase(rateTpe))
								{
									if("SLAB".equalsIgnoreCase(chargeRateIndicator[k]))
									{
											if(!"checked".equals(checked))
											{
												rate1 = chargesValue[k];
											}else
											{
												rate1 = chargesValueStr[rateIndex++];
											}
									}else
									{
										if(!"checked".equals(checked))
										{
											rate = chargesValue[k];
										}else
										{
											rate = chargesValueStr[rateIndex++];
										}
									}
%>
									
<!-- modified by subrahmanyam for the id:216340 -->
								<td><input type=text name='slab<%=i%>' onkeypress="clearValue('flat<%=i%>','<%=k-2%>');return getDotNumberCode(this.value)" class='textHighlight' size=3 maxlength=10  value='<%=rate1%>'   onpaste='return false;'><input type=text name='flat<%=i%>' class=text size=5 maxlength=10  value='<%=rate%>' onkeypress="clearValue('slab<%=i%>','<%=k-2%>');return getDotNumberCode(this.value)" onpaste='return false;' ></td>
<%
								}else{

										if(!"checked".equals(checked))
										{
											rate = chargesValue[k];
										}else
										{
											rate = chargesValueStr[rateIndex++];
										}
%>
										<td><input type=text name='flat<%=i%>'  maxlength=10 value='<%=rate%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'><input type=hidden  name='indicator<%=i%>'  maxlength=1 id=<%=srCount%><%=k%> value='<%=chargeRateIndicator[k]%>' size=1 ></td>

<%								}
							}else
							{
%>
										<td>-</td>
<%
							}

						}
%>					
					
<%
					}
					}
%>					
					<td align="center"> 
						  <%=sellRatesDob1.getDensityRatio()!=null ? sellRatesDob1.getDensityRatio() :""%>
        			  </td>
					<input type=hidden name='count' id='count' value='<%=fslListValuessize%>'>
					<!--@@Commented and  Modified by Kameswari for the WPBN issue-143527-->
					<!-- <td> 
						<input type='text' class='text' name="notes<%=i%>" size="7" id='notes<%=i%>' maxlength='30' value='<%=notes%>' onBlur='this.value=this.value.toUpperCase()'>
					</td> -->
					 <td> 
						<input type='text' class='text' name="notes<%=i%>" size="7" id='notes<%=i%>' maxlength='1000' value='<%=notes%>' onBlur='this.value=this.value.toUpperCase()'>
					</td> 
					<!-- //Modified by Mohan for Issue No.219976 on 30-10-2010 -->
					<td> 
					<input type='text' class='text' name="extNotes<%=i%>" size="7" id='extNotes<%=i%>' maxlength='1000' value='<%=extNotes%>' onBlur='this.value=this.value.toUpperCase()'>
					</td> <td/>
				  </tr>
				  <%
						if(count<wtBreakLength)
			         	{
						  if((wtBreakLength<count*2+11)||"slab".equalsIgnoreCase(weightBrk)||"flat".equalsIgnoreCase(weightBrk)
							  ||"list".equalsIgnoreCase(weightBrk))
					       {
						 %>
						  
				   <%itr1 = rateDescset.iterator();
						itr2 = surChargeCurrset.iterator(); // Added by Gowtham
						int surChargeCurrCount = 0;
                     while(itr1.hasNext() && itr2.hasNext() ){
                     String tempDesc  =(String)itr1.next();
					 String tempDesc1 = (String)itr2.next();  // Added by Gowtham
					 //System.out.println("tempDesctempDesctempDesctempDesc..."+tempDesc);
					 int subTdCount =0; //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
				if(!tempDesc.equalsIgnoreCase("A FREIGHT RATE")){%>
				<tr class="formdata"><td width = '30%'>&nbsp;</td>
                                 <!-- // Added By Kishroe To Trim the rates Description last 3 charecters -->
				 <td width = '30%' colspan='3'> <%=tempDesc.substring(0,(tempDesc.length()-3))%></td>  <td>  Currency<br>	 <!-- <%=tempDesc1.substring(0,tempDesc1.indexOf("~"))%>   -->
                             <input type='text' name='Currency<%=i%>' id = 'surchargeCurrency<%=surChargeCurrCount%><%=i%>' maxlength=10 value='<%=tempDesc1.substring(0,tempDesc1.indexOf("~"))%>' size=5 class=text onBlur='this.value=this.value.toUpperCase()'onKeyPress='return specialCharFilter()' ;'>
				<input type='button' class='input'  name='currencyLOV' value='...' onclick="showCurrencyLOV('surchargeCurrency<%=surChargeCurrCount%><%=i%>');">
				 			 
				 </td>  <!-- Added By Gowtham -->
                                  <% 
                  for(int m=count;m<wtBreakLength;m++)
				  {
					 // System.out.println("tempDesc--"+tempDesc+"--rateDescs[m]"+rateDescs[m]);
				 if((weighttBkValues[m]!=null&&weighttBkValues[m]!="")&&(tempDesc.equalsIgnoreCase(rateDescs[m]))  )
					  {if("2".equals(shipmentMode)&&"FCL".equals(consoleTpe))
						  {
								weighttBkValues[m] = weighttBkValues[m].length() > 4?weighttBkValues[m].substring(0,4):weighttBkValues[m];
						  }
							else	
							weighttBkValues[m] = weighttBkValues[m].length() > 5?weighttBkValues[m].substring(5,weighttBkValues[m].length()):weighttBkValues[m];// Added by Gowtham on 16Feb2011
							%>
						 <td ><%=weighttBkValues[m]%><br>
						 <%if("-".equals(chargeRateIndicator[m])){%>
						 <input type=text name='flat<%=i%>'  maxlength=10 value='<%=chargesValue[m]%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'><input type=hidden  name='indicator<%=i%>'  maxlength=1 id=<%=srCount%><%=m%> value='-' size=1 > 
						 <%}else{%>
                          <input type=text name='flat<%=i%>'  maxlength=10 value='<%=chargesValue[m]%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'><input type=text class='textHighlight' name='indicator<%=i%>'  maxlength=1 id=<%=srCount%><%=m%> value='<%=chargeRateIndicator[m].substring(0,1)%>' size=1  onkeypress='return getSlabFlatIndCheck(this)' onBlur='this.value=this.value.toUpperCase()'> 
						 </td>
<%			}//end else
			     subTdCount++; //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
					  }
					  
				}//end for 
                                //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
				/* count ---> Number Of dynamic Columns in Header
					13--> static columns in header(13) 
					subTdCount --> td's added in the 
				*/
				//dumtemp	=	count*2+11-wtBreakLength;
				dumtemp	=	count+13;
				//System.out.println("dumtemp----"+(dumtemp) +"		count"+count+"		subTdCount"+subTdCount);
				// 1 <td> - check box to every row, 3 <td>s -  sur charge name occupies
				 for(int s=0;s<dumtemp-subTdCount-1-3;s++)
					 {
					  //end of Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
					 %>
			  <td>&nbsp;</td>
			 <%}%> </tr><%
						 srCount++;
					 }//end while
					 surChargeCurrCount++;
					 } //end freight rate if
					 temp	=	count*2+11-wtBreakLength;%>
				  
					<!-- // temp	=	count*2+11-wtBreakLength;-->
					 
             
			 <!--   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count;n<chargesLength;n++)
				   {
					  if(chargesValue[n]!=null&&chargesValue[n]!="")
					  {
						  if(!"-".equals(chargesValue[n]))
						  {%>
				  
				  <td><input type=text name='flat<%=i%>'  maxlength=10 value='<%=chargesValue[n]%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
                </td>
				<%}
					  else
						  {  %>
						  <td>-</td>
				 <%      }
					 }
				  
				 } 
				 for(int s=0;s<temp;s++)
					{%>
				 <td>&nbsp;</td>
			  <%}%>
			   </tr> -->
			   <%	   }else
							   {%>
				  <!--@@Added by kameswari for the CR-->
				  <tr class="formdata">
				    <td>&nbsp;</td>
				  <%
					 
				   for(int m=count;m<count*2+11;m++)
				  {
					  if(weighttBkValues[m]!=null&&weighttBkValues[m]!="")
					  {
						  %>
				     <td><%=weighttBkValues[m]%></td>
<%			        }
			     } 
		%>		  
					  
					
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count;n<count*2+11;n++)
				  {	
	                 if(chargesValue[n]!=null&&chargesValue[n]!="")
					  {
						  if(!"-".equals(chargesValue[n]))
						  {%>

				  
				  <td><input type=text name='flat<%=i%>'  maxlength=10 value='<%=chargesValue[n]%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
                </td>
				<%}
					  else
						  {  %>
						  <td>-</td>
				     <%  }
					 }
				  }
				 %>
			   </tr>
			   <tr class="formdata">
				    <td>&nbsp;</td>
				  <%
					 temp	=0;
				   for(int m=count*2+11;m<wtBreakLength;m++)
				  {
					   if(weighttBkValues[m]!=null&&weighttBkValues[m]!="")
					  {
						   temp++;%>
						<td><%=weighttBkValues[m]%></td>
<%			         }
			     } 
				  
			  for(int s=temp;s<count+11;s++)
					{
					%>		  <td>&nbsp;</td>
								 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count*2+11;n<chargesLength;n++)
				  { 
						if(chargesValue[n]!=null&&chargesValue[n]!="")
					  { 
							if(!"-".equals(chargesValue[n]))
						  {%>
				  
				  <td><input type=text name='flat<%=i%>'  maxlength=10 value='<%=chargesValue[n]%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
                </td>
				<%}
					  else
						  {  %>
						  <td>-</td>
				      <% }
					 }
				  }
				  for(int s=temp;s<count+11;s++)
					{%>
				 <td>&nbsp;</td>
			  <%}%>
			   </tr>
			   <%}
				}
			}%>
			 <tr class="text" vAlign="top">
                <td valign="middle" colspan="10" >
<%
					int  currentPageNo	=	0;
					if(request.getParameter("pageNo")!=null)
						currentPageNo = Integer.parseInt(request.getParameter("pageNo"));
					if(currentPageNo != 1)
					{
%>
						<a href="#" onClick="functionCall('<%=currentPageNo-1%>','<%=operation%>')"><img src="images/Toolbar_backward.gif" ></a>
 <%					}        
					
					int multiplier = 1;
					int startNo = 0;
					int endNo = 0;
					if(currentPageNo > noOfSegments)
					{
						multiplier = currentPageNo / noOfSegments;
						if(currentPageNo % noOfSegments != 0)
							startNo = noOfSegments * multiplier;
						else
							startNo = noOfSegments * (multiplier - 1);
					}
					if(noofPages > startNo)
					{
						if(noofPages > startNo + noOfSegments)
							endNo = startNo + noOfSegments;
						else
							endNo = startNo + (noofPages - startNo);
					} else
					{
						endNo = noofPages;
					}
					for(int p = startNo; p < endNo; p++)
						if(currentPageNo == p + 1){%>
							<font size="3"><B><%=(p + 1)%></B></font>&nbsp;
						<%}else{%>
							<a href="#" onClick="functionCall('<%=(p + 1)%>','<%=operation%>')"><%=(p + 1)%></a>&nbsp;
<%	                       }
				if(currentPageNo != noofPages)
				  {
%>
						<a href="#" onClick="functionCall('<%=currentPageNo+1%>','<%=operation%>')"><img src="images/Toolbar_forward.gif" ></a>
<%					
				  }
%>
                </td>
                
              </tr>
		  </table>
<%
			//if(request.getAttribute("Errors")==null)	  
			{
%>
			 <table width="100%" cellpadding="4" cellspacing="1">
				<tr class='denotes'> 
				  <td align='right'> 
						<input type="button" name="next" value="Submit" class='input' onClick="return validation(this)">&nbsp;
						<input type="reset" name="reset" value="Reset" class='input' onClick="">
				  </td>
			    </tr>
			</table> 
<%
			}
%>

<%	}
	
	else if(listValue!=null && (fslListValues!=null && fslListValues.size()==0))
	{
%>
		<table width="100%"   border="0" cellpadding="4" cellspacing="1">
			<tr class='formdata'> 
				<td colspan="6" align="center">
					<b>No Rates Are Defined for the Specified Details.<b>
				</td>
			</tr>
		</table>
<%
	}
%>







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
			//Logger.error(FILE_NAME,"Error in QMSBuyRateView.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSBuyRateView.jsp "+e);
		
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details  ","BuyRatesController?Operation="+operation+"&subOperation=Enter"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
	    	
	
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
%>
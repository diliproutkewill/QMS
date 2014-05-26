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
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.qms.operations.rates.dob.FlatRatesDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSBuyRateView.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ArrayList			listValues				=	null;
		ArrayList			listValue				=	null;
		ArrayList			headerlist				=	null;
		ArrayList			fslListValues			=	null;
		ArrayList			finalList				=	null;
		ArrayList			weightBreakList			=	null;
		ArrayList			chargeDtlList			=	null;
		ErrorMessage		errorMessageObject		=   null;
		QMSSellRatesDOB		sellRatesDob			=	null;
		QMSSellRatesDOB		sellDob					=	null;
		QMSBoundryDOB		boundryDob				=	null;
		QMSSellRatesDOB		sellRatesDob1			=	null;
		HashMap				mapValues				=	null;
		String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
		String				terminalId				=	loginbean.getTerminalId();
		String				operation				=	null;
		String				keyHash					=	null;
		Iterator			itr						=	null;
                Iterator			itr1						=	null;//Added by Govind for the CR-219973
		Iterator			itr2						=	null; // Added by Gowtham For SurCharge Currency
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
		/*String				overallMrg				=	null;
		String				marginTpe				=	null;
		String				marginBass				=	null;*/
		String				consoleTpe				=	null;
		String				subOperation			=	null;
		String				invalidate				=	null;
		String				opr						=	null;
		String				rate					=	"";
        String[]           rateDescs                = null;
		String[]           surChargeCurr                = null; // Added by Gowtham for Surchage Currency Description
		int					noofRecords						=	0;
		int					noofPages						=	0;
		String				checked					=	"";
		int					noOfSegments			=	0;
		int count=0,temp=0,dumtemp=0;
//		added by phani sekhar wpbn 171213 on 20090604 
		String				originRegion			=	null;
		String				destinationRegion		=	null;
		int currInd									=	 1; // Added by Gowtham on 15Feb2011 for CurrencyLOV
		int shMode									=	 0;	// Added by Gowtham on 15Feb2011 
		//ends 171213
		try
		{
			noOfSegments            =  Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
			operation				=	request.getParameter("Operation");

			if("View".equalsIgnoreCase(operation))
			{
				opr ="View";
			}
			else
			{
				
				opr ="Invalidate";
			}
			subOperation			=	request.getParameter("subOperation");
			//System.out.println("operation : "+operation+"			subOperation  "+subOperation);
			listValue				=	(ArrayList)session.getAttribute("HeaderValue");
			session.removeAttribute("HeaderValue");

			if(listValue!=null)
			{
					//System.out.println("1 ---------> listValue is not null");
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
//					added by phani sekhar wpbn 171213 on 20090604 
					originRegion			=   sellDob.getOriginRegions();
					destinationRegion		=  sellDob.getDestRegions();
//ends 171213
				//	System.out.println("consoleTpeconsoleTpe in Jsp :: "+consoleTpe);
					//System.out.println("WeightBreak : "+sellRatesDob.getWeightBreak());
					//System.out.println("RateType : "+sellRatesDob.getRateType());
			}

			listValue				=	(ArrayList)session.getAttribute("HeaderValues");
			session.removeAttribute("HeaderValues");

			if(listValue!=null)
			{
					//System.out.println("2 ---------> listValue is not null");
					sellDob					=	(QMSSellRatesDOB)listValue.get(0);
					finalList				=	(ArrayList)listValue.get(1);
					fslListValues           =   (ArrayList)finalList.get(0);
					Integer  no_ofRecords   =   (Integer)finalList.get(1);
					Integer  no_ofPages		=	(Integer)finalList.get(2);
					noofRecords				=	no_ofRecords.intValue();
					noofPages				=	no_ofPages.intValue();

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
//					added by phani sekhar wpbn 171213 on 20090604 
					originRegion			=   sellDob.getOriginRegions();
					destinationRegion		=  sellDob.getDestRegions();
//ends 171213
					int noofPage	=	Integer.parseInt(sellDob.getPageNo());

					mapValues				=   (HashMap)session.getAttribute("hm_buyRates");
					//System.out.println("in jsp::::mapValues::"+mapValues);					

					//System.out.println("consoleTpeconsoleTpe in Jsp :: "+consoleTpe);
					//System.out.println("getWeightBreakgetWeightBreakgetWeightBreak : "+sellRatesDob.getWeightBreak());
					//System.out.println("getRateTypegetRateTypegetRateTypegetRateType : "+sellRatesDob.getRateType());
			}

	/*		if(listValue!=null)
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
					overallMrg				=	sellDob.getOverAllMargin();
					marginTpe				=	sellDob.getMarginType();
					marginBass				=	sellDob.getMarginBasis();
					consoleTpe				=	sellDob.getConsoleType();
					System.out.println("consoleTpeconsoleTpe in Jsp :: "+consoleTpe);
			}
			listValues				=	(ArrayList)session.getAttribute("HeaderValues");
			System.out.println("listValueslistValueslistValueslistValueslistValueslistValues :: "+listValues);
			session.removeAttribute("HeaderValues");
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
	//alert('validation:	'+operation);
	var msgHeader		=	'';
	var msgErrors		=	'';
	var focusPosition	=	new Array();

	msgHeader= '_____________________________________________________\n\n' +
		'This form has not been submitted because of the following error(s).\n' +
		'Please correct the error(s) and re-submit.\n' +
		'_____________________________________________________\n\n';
	//modified by phani sekhar wpbn 171213 on 20090604 
	if(document.forms[0].origin.value.length==0 && document.forms[0].destination.value.length==0 
		&& document.forms[0].carriers.value.length==0 && document.forms[0].serviceLevelId.value.length==0&&document.forms[0].originRegion.value.length==0&&document.forms[0].originCountry.value.length==0&&document.forms[0].destinationRegion.value.length==0&&document.forms[0].destinationCountry.value.length==0)
	{
		msgErrors	+="Please Enter/Select OriginId/DestinationId/Carriers/ServiceLevelId.\n";
		focusPosition[1]	=	"origin";
	}
	/*if(document.forms[0].baseCurrency.value.length==0)
	{
		msgErrors	+="Please Enter/Select CurrencyId.\n";
		focusPosition[0]	=	"baseCurrency";
	}*/
	/*if(document.forms[0].origin.value.length==0)
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
	if(obj.name=="next")
	{
		flag = false;
		var cnt = document.getElementById("count").value;
		for(i=0;i<cnt;i++)
		{
			if(document.getElementById(i).checked==true)
			{
				flag = true;
			}
		}
	//if(flag==true)//@@ Commented and Added by subrahmanyam for the Wpbn Issue:201264  on 29-Mar-10.
		if(flag==true || operation=='InValide' )
		{
			flag=true;
			document.forms[0].action='BuyRatesController?Operation='+operation+'&subOperation=values';
			document.forms[0].submit();
			return true;
		}else
		{
			alert("Please select atleast one Lane to <%=operation%>");
			return false;
		}
	}
	


	if(obj.name=="search")
	{
		//alert("search");
		document.forms[0].action='BuyRatesController?Operation='+operation+'&subOperation='+operation;
		document.forms[0].submit();
		return true;
	}
	
	
}
function viewContinue()
{
	//alert('viewContinue');
		document.forms[0].action='BuyRatesController?Operation=View&subOperation=Enter';
	document.forms[0].submit();
	return true;
}
function showHide()
{

	var data="";
	var index	=	document.forms[0].shipmentMode.value;
	if(index=="2")
	{
		 data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleType" class="select" onChange="changeValue(this);showRateType();disableSubmit()"><option  value="LCL" <%="LCL".equals(consoleTpe) ? "SELECTED" : "" %>>LCL</option><option  value="FCL" <%="FCL".equals(consoleTpe) ? "SELECTED" : "" %>>FCL</option></select>';
	}
	else if(index=="4")
	{
		data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleTypes" class="select" onChange="changeValue(this);disableSubmit()"><option  value="LTL" <%="LTL".equals(consoleTpe) ? "SELECTED" : "" %>>LTL</option><option  value="FTL" <%="FTL".equals(consoleTpe) ? "SELECTED" : "" %>>FTL</option></select>';
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

function disableSubmit()
{
	if(document.forms[0].next!=null)
	{
		var obj				= document.forms[0].next.value;
		if(obj=="Next>>")
		{
			document.forms[0].next.disabled=true;
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
}
//added by phani sekhar wpbn 171213 on 20090604 
	function showRegionLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;


	var searchString = (toSet=='originRegion')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2= (toSet=='originRegion')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
	var searchString3= (toSet=='originRegion')?document.forms[0].originRegion.value:document.forms[0].destinationRegion.value;	
	var Url='etrans/ETCRegionIdsLOV.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value+'&searchString3='+searchString3+'&name='+toSet;
	var Win=open(Url,'Doc',Features);
}

//This function used for call the LOVs
function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2= (toSet=='origin')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
//	added by phani sekhar wpbn 171213 on 20090604 
	var searchString3= (toSet=='origin')?document.forms[0].originRegion.value:document.forms[0].destinationRegion.value;

	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value+'&searchString3='+searchString3;
	var Win=open(Url,'Doc',Features);
}
function showCurrencyLOV(id,index) // Added by Gowtham on 15Feb2011
{	//alert(document.forms[0].id.value);
	var searchString = id.value;
	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?Operation=<%=operation%>&name='+id+'&fromWhere=buycharges';
	//myUrl = 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString=INR';
	var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
	var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
	var myFeatures = myBars+','+myOptions;
	
	newWin = open(myUrl,'myDoc',myFeatures);
	if (newWin.opener == null) 
		newWin.opener = self;
	return true;

} // Added by Gowtham on 15Feb2011
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
	// modified by phani sekhar wpbn 171213 on 20090604
var searchString3= (toSet=='originCountry')?(document.forms[0].originRegion.value!=null?document.forms[0].originRegion.value:""):(document.forms[0].destinationRegion.value!=null?document.forms[0].destinationRegion.value:"");

	var Url='etrans/ETCLOVCountryIds1.jsp?searchString='+searchString+"&whereClause=CSR&wheretoset="+toSet+"&locationId="+locationId+"&shipmentMode="+document.forms[0].shipmentMode.value+'&searchString3='+searchString3;

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
	if("Slab".equals(weightBrk) || "List".equals(weightBrk))
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
</script>
</head>

<body bgcolor="#FFFFFF" onLoad="showHide();showRateType()">
<form   method="post">

  <table width="102%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
       <table width="100%" cellpadding="4" cellspacing="1">
			<tr valign="top" class='formlabel'>
			   <td colspan="12" width="903">Buy Rates Master - <%=opr%></td>
				<td align="right">QS1060131</td>
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
					<select size="1" name="weightBreak" class='select' onChange="changeValue();disableSubmit();showRateType();changeValue()">
						<option selected value="Flat" <%="Flat".equals(weightBrk) ? "SELECTED" : "" %>>Flat</option>
						<option  value="Slab" <%="Slab".equals(weightBrk) ? "SELECTED" : "" %>>Slab</option>
						<option  value="List" <%="List".equals(weightBrk) ? "SELECTED" : "" %>>List</option>
					</select>
				</td>
				
				 <td>
				 <div id='cust2' style='position:relative;'></div>
				<!--  Rate Type: <font color="#FF0000">*</font><br>
					<select size="1" name="rateType" class='select' onChange="changeValue();disableSubmit()">
						<option  value="Flat" <%="Flat".equals(rateTpe) ? "SELECTED" : "" %>>Flat</option>
						<option value="Slab" <%="Slab".equals(rateTpe) ? "SELECTED" : "" %>>Slab</option>
						<option value="Both" <%="Both".equals(rateTpe) ? "SELECTED" : "" %>>Both</option>
						<option value="Pivot" <%="Pivot".equals(rateTpe) ? "SELECTED" : "" %>>Pivot</option>
					</select>  -->
				</td>
<!-- 				<td>Currency:<font  color=#ff0000>*</font><br>
					<input type='text' class='text' name='baseCurrency' value='<%=currencyId!=null?currencyId:""%>' size='10' maxlength='3' onblur='this.value=this.value.toUpperCase()'>
					&nbsp;<input type='button' value='...' name='currencyLOV' class='input' onclick='showCurrencyLOV()'>
				</td> -->
				<td>Weight Class: <br>
					<select size="1" name="weightClass" class='select' onChange='disableSubmit()'>
						<option  value="G" <%="G".equals(weigtClas) ? "SELECTED" : "" %>>General</option>
						<option  value="WS" <%="WS".equals(weigtClas) ? "SELECTED" : "" %>>Weight Scale</option>
					</select>
				</td>
				</tr>
				</table>
				<table width="100%" cellpadding="4" cellspacing="1">
			<tr class='formheader'> 
				<td>Origin:</td>
				<td>Origin<br>Countries:</td>
				<td>Origin<br>Region:</td><!-- added by phani sekhar for wpbn 171213 on 20090604 -->
				<td>Destination:</td>
				<td>Destination<br>Countries:</td>
				<td>Destination<br>Region:</td><!-- added by phani sekhar for wpbn 171213 on 20090604 -->
				<td>Carrier:</td>
				<td>Service<br>Level:</td>
				<td/>
			</tr>
			<tr class='formdata'> 

				<td>
					<input type="text" name="origin" value='<%=origin!=null?origin:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>
					&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='showLocationLOV("origin")'>
				</td>
				<td>
					<input type="text" name="originCountry"  value='<%=originCut!=null?originCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>
					&nbsp;<input type="button" value="..." name="originCountryLOV"  class='input' onclick='showCountryLOV("originCountry")'>
				</td>
				<td><!-- added by phani sekhar for wpbn 171213 on 20090604 -->
					<input type="text" name="originRegion"  value='<%=originRegion!=null?originRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' >
					&nbsp;<input type="button" value="..." name="originRegionLOV"  class='input' onclick='showRegionLOV("originRegion")'>
				</td>
				<td>
					<input type="text" name="destination" value='<%=distenation!=null?distenation:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>
					&nbsp;<input type="button" value="..." name="destinationLOV"  class='input' onclick='showLocationLOV("destination")'>
				</td>
				<td>
					<input type="text" name="destinationCountry" value='<%=distenationCut!=null?distenationCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationCountyLOV"  class='input' onclick='showCountryLOV("destinationCountry")'>
				</td>
				<td><!-- added by phani sekhar for wpbn 171213 on 20090604 -->
					<input type="text" name="destinationRegion" value='<%=destinationRegion!=null?destinationRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationRegionLOV"  class='input' onclick='showRegionLOV("destinationRegion")'>
				</td>
				<td>
					<input type="text"  name="carriers" value='<%=carrierStr!=null?carrierStr:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="gatebut"  class='input' onclick='showCarrierIds()'>
					
				</td>
				<td>
					<input type="text" name="serviceLevelId" value='<%=serviceLevel!=null?serviceLevel:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="serviceLevelLOV"  class='input' onclick='showServiceLevelLOV()'>
				</td>
				<td/>
			</tr>
			<tr class='denotes'>             
				<td colspan='4'> 
					<font color=red> * 
					</font>Each text box is multiple selection with comma separation. User can enter manually or select from LOV
				</td>
				<td colspan='3'/>
				<td colspan='2' align='left'> 
					<input type="button" class='input' name="search" value="Search"  onClick="return validation(this)">
				</td> <!-- Added By Gowtham -->
				<input type='hidden' name="pageNo" value="1">
			</tr>
			</table>
			<table width="100%" cellpadding="4" cellspacing="1">
<%	
	if(fslListValues!=null && fslListValues.size()>0)
	{
				//System.out.println("-------fslListValues---------");
	
%>
				<tr class='formheader'> 
<%				if("Invalidate".equalsIgnoreCase(opr))
				{ 
%>	
					<td>
						Invalidate<br><input type='checkbox' name="select1" onClick="selectAll(this)">
					</td>
<%				}
%>
					<td>Origin</td>
					<td>Origin<br>Country</td>
					<td>Destination</td>
					<td>Destination Country</td>
					<td>Carrier</td>
					<td>Service Level</td>
					<%if("1".equalsIgnoreCase(shipmentMode)||"4".equalsIgnoreCase(shipmentMode)){%>
					<td>Approximate Transit Time</td>
					<%}else{%>
						<td>Approximate Transit Days</td>
						<%}%>
					<td>Frequency</td>
					<td>Currency</td>
<%
					sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
					String[] weighttBkValues		=	sellRatesDob1.getWeightBreaks();
					int	wtBreakLength				=	weighttBkValues.length;
				    rateDescs         =   sellRatesDob1.getRateDescription();  
					surChargeCurr	=	  sellRatesDob1.getSurChargeCurency(); // Added by Gowtham for Surcharge Description
					
					//System.out.println("wtBreakLengthwtBreakLengthwtBreakLengthwtBreakLengthwtBreakLength:: "+wtBreakLength);
				for(int j=0;j<wtBreakLength;j++)
					{
					//	System.out.println("rateDescs[j]...& weighttBkValues[j].."+rateDescs[j] +" & "+weighttBkValues[j]);
      				                //System.out.println(" ---- Gowtham+ "+surChargeCurr[j] +" & "+rateDescs[j]+"&"+weighttBkValues[j]); // Added by Gowtham 
						if("A FREIGHT RATE".equalsIgnoreCase(rateDescs[j]))
						{
							 
							 count++;%>
						
				<td ><%=weighttBkValues[j]%></td>
					 
<%
					}
}%>
					<!-- Modified by Mohan for Issue No.219976 on 1-11-2010  -->
					<td>Internal Notes</td>
					<td>External Notes</td>
					<%if("View".equalsIgnoreCase(opr)){ %><td>Terminal Id</td><td>Density Ratio</td><td/><%}else{%>
					  <td></td><td></td><%}%>
				</tr>
<%
			int	fslListValuessize				=	fslListValues.size();
					//System.out.println("-------count: "+count	+"	fslListValuessize: "+fslListValuessize);
			for(int i=0;i<fslListValuessize;i++)
			{
				sellRatesDob1	=	(QMSSellRatesDOB)fslListValues.get(i);
				//Logger.info("",sellRatesDob1.getInvalidate());
				weighttBkValues		=	sellRatesDob1.getWeightBreaks();
					wtBreakLength				=	weighttBkValues.length;
				 rateDescs         =   sellRatesDob1.getRateDescription(); 
				 surChargeCurr	=	  sellRatesDob1.getSurChargeCurency(); // Added by Gowtham for SurCharge Currency Description
			
					Set rateDescset				  =   new LinkedHashSet();//Govind for srcharge display
					Set surChargeCurrset				  =   new LinkedHashSet(); // Added by Gowtham
					for(String s:rateDescs)
                         rateDescset.add(s);

					//for(String c:surChargeCurr)
						for(int x = 0; x<surChargeCurr.length;x++){
					
                         surChargeCurrset.add(surChargeCurr[x]+"~"+rateDescs[x]); // Added by Gowtham
                         }
					

				//Logger.info(FILE_NAME,"invalidate::::"+invalidate);
				if("T".equalsIgnoreCase(sellRatesDob1.getInvalidate()) )
				{
					invalidate ="checked";
				}else
				{
					invalidate	=	"";
				}

				if(mapValues!=null &&  mapValues.get(sellRatesDob1.getBuyRateId()+"_"+sellRatesDob1.getLanNumber())!=null)
				{
					checked	=	((FlatRatesDOB)mapValues.get(sellRatesDob1.getBuyRateId()+"_"+sellRatesDob1.getLanNumber())).getInvalidate();

					if(mapValues!=null && checked!=null && checked.trim().equals("T"))
					{
						invalidate ="checked";
					}else
					{
						invalidate ="";
					}
				}



%>
				<tr class='formdata'> 
					
					<input type="hidden" name="lanNumber<%=i%>" value='<%=sellRatesDob1.getLanNumber()%>'>
					<input type="hidden" name="buyRateId<%=i%>" value='<%=sellRatesDob1.getBuyRateId()%>'>

					<%if("Invalidate".equalsIgnoreCase(opr)){ %><td><input type=checkbox name='<%=i%>' <%=invalidate%>></td><%}%>
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
					String[] chargesValue			=	sellRatesDob1.getChargeRates();
					String[] chargeRateIndicator	=	sellRatesDob1.getChargeInr();
					rate	=	"";
					int chargesLength		=	chargesValue.length;
					//System.out.println("chargesLengthchargesLengthchargesLengthchargesLengthchargesLength:: "+chargesLength);
					for(int k=0;k<count;k++)
					{
						//System.out.println("chargesValue[k]chargesValue[k]chargesValue[k]chargesValue[k]:: "+chargesValue[k]);
						
						if(k==0)
						{

%>
									<td><%=chargesValue[k]%></td>

<%						}else
						{
							if(!"-".equals(chargesValue[k]))
							{
								if("Both".equalsIgnoreCase(rateTpe))
								{
									if("SLAB".equalsIgnoreCase(chargeRateIndicator[k]))
									{
											rate = chargesValue[k]+"S";
									}else
									{
											rate = chargesValue[k]+"F";
									}
%>
									
										<td><%=rate%></td>
<%
								}else{
%>
										<td><%=chargesValue[k]%></td>

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
%>						<input type=hidden name='count' id='count' value='<%=fslListValuessize%>'>
						<td><%=(sellRatesDob1.getNotes()!=null)?sellRatesDob1.getNotes():""%></td>
						<!-- Modified by Mohan for Issue No.219976 on 1-11-2010 -->
						<td><%=(sellRatesDob1.getExtNotes()!=null)?sellRatesDob1.getExtNotes():""%></td>
						<%if("View".equalsIgnoreCase(opr)){ %><td><%=sellRatesDob1.getTerminalId()%></td><td><%=sellRatesDob1.getDensityRatio()!=null?sellRatesDob1.getDensityRatio():""%></td><td/><%}else{%>	<td></td><td></td	>	<%}%>	  <!-- Added By Gowtham -->
					</tr>
 	  <%
						if(count<wtBreakLength)
			         	{
						  if((wtBreakLength<count*2+11) || "slab".equalsIgnoreCase(weightBrk)||"flat".equalsIgnoreCase(weightBrk)
							 || "list".equalsIgnoreCase(weightBrk) )
					       {
						 %>
	
					<%itr1 = rateDescset.iterator();
						itr2 = surChargeCurrset.iterator();   
                     while(itr1.hasNext() && itr2.hasNext()  ){
                     String tempDesc  =(String)itr1.next();
					 String tempDesc1 = (String)itr2.next();  // Added by Gowtham
					 //System.out.println("tempDesc..."+tempDesc);
				//	 System.out.println("tempDesc..."+tempDesc1);
				String tempCurr = tempDesc1.substring(0,tempDesc1.indexOf("~"));

				if(!tempDesc.equalsIgnoreCase("A FREIGHT RATE")){%>
				<tr class="formdata"><td width = '30%'>&nbsp;</td>
				 <td width = '30%' colspan='3'> <%=tempDesc.substring(0,(tempDesc.length()-3))%></td>
				 <td width = '30%'>Currency<br><%="-".equals(tempCurr)?sellRatesDob1.getCurrencyId():tempCurr%></td>  <!-- Added By Gowtham   Currency &nbsp -->
				 
				 <% currInd++;
				 // Added By Kishroe To Trim the rates Description last 3 charecters
					int subTdCount = 0; //Added by Kishore Podili for the issue id:226791_Alignment on 16-Dec-2010   surChargeCurrset
                  for(int m=count;m<wtBreakLength;m++)
				  {
					  System.out.println("tempDesc --"+tempDesc+"--rateDescs[m]"+rateDescs[m]+"shipmentmode-->"+shMode);
					//System.out.println("shipment mode--------->"+(document.forms[0].shipmentMode.index));
				 if((weighttBkValues[m]!=null&&weighttBkValues[m]!="")&&(tempDesc.equalsIgnoreCase(rateDescs[m]))  )
					  {		if("2".equals(shipmentMode)&&"FCL".equals(consoleTpe))
						  {
								weighttBkValues[m] = weighttBkValues[m].length() > 4?weighttBkValues[m].substring(0,4):weighttBkValues[m];
						  }
							else	
							weighttBkValues[m] = weighttBkValues[m].length() > 5?weighttBkValues[m].substring(5,weighttBkValues[m].length()):weighttBkValues[m];// Added by Gowtham on 15Feb2011 to display slabs as min,slab and flat etc
					 %> 
						 <td><%=weighttBkValues[m]%><br>
						 <!-- <input type=text name='flat<%=i%>'  maxlength=10 value='<%=chargesValue[m]%>' size=5 class=text onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'> -->
						 <%=chargesValue[m]%>
						 </td>
<%			
					subTdCount++; //Added by Kishore Podili for the issue id:226791_Alignment on 16-Dec-2010 
					  }
					  
				}//end for 
                       //Added by Kishore Podili for the issue id:226791_Alignment on 16-Dec-2010 
				//System.out.println("count----"+count+"	wtBreakLength"+wtBreakLength+"	subTdCount"+subTdCount);
				//dumtemp	=	count*2+14-wtBreakLength; //kishore
				//System.out.println("dumtemptemp----"+(dumtemp));
				
				/* count ---> Number Of dynamic Columns in Header
					11(13 - 2)--> static columns in header(13) , 2 tds added by default to every row
					subTdCount --> td's added in the 
				*/
				dumtemp =		count+11-2; 
				 for(int s=0;s<dumtemp-subTdCount;s++)
					 {
					 %>
			  <td>&nbsp;</td>
			 <%}%> </tr><%
                               //End Of Kishore Podili for the issue id:226791_Alignment on 16-Dec-2010 
					 }//end while
					 
					 } //end freight rate if
					 temp	=	count*2+11-wtBreakLength;%>

			<!--  <tr class="formdata">
				    <td>&nbsp;</td>
				  <%for(int m=count;m<wtBreakLength;m++)
				  {
					 if(weighttBkValues[m]!=null&&weighttBkValues[m]!="")
					  {%>
						 <td><%=weighttBkValues[m]%></td>
<%			
			     
					  }
					  
				}
				  
					  temp	=	count*2+11-wtBreakLength;
					  for(int s=0;s<temp;s++)
					 {
%>
			  <td>&nbsp;</td>
			 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count;n<chargesLength;n++)
				   {
					  if(chargesValue[n]!=null&&chargesValue[n]!="")
					  {
						  if(!"-".equals(chargesValue[n]))
						  {%>
				  
				  <td><%=chargesValue[n]%>
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
			   </tr>-->
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

				  
				  <td><%=chargesValue[n]%>
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
				  
				  <td><%=chargesValue[n]%>
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
					for(int i = startNo; i < endNo; i++)
						if(currentPageNo == i + 1){%>
							<font size="3"><B><%=(i + 1)%></B></font>&nbsp;
						<%}else{%>
							<a href="#" onClick="functionCall('<%=(i + 1)%>','<%=operation%>')"><%=(i + 1)%></a>&nbsp;	
<%	                       }
				if(currentPageNo != noofPages)
				  {
%>
						<a href="#" onClick="functionCall('<%=currentPageNo+1%>','<%=operation%>')"><img src="images/Toolbar_forward.gif" border='0'></a>
<%					
				  }
%>
                </td>
                
              </tr>
<%
	}else if(listValue!=null && (fslListValues!=null && fslListValues.size()==0) )
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

		  <% if("InValide".equalsIgnoreCase(operation)  && fslListValues!=null && fslListValues.size()>0 && request.getAttribute("Errors")==null )
			{	  
		  %>
		 <table width="100%" cellpadding="4" cellspacing="1">
			 <tr class='denotes'> 
				  <td align='right'> 
						<input type="button" name="next" value="Next>>" class='input' onClick="return validation(this)" >
				  </td>
            </tr>
		</table> 
		<%
			  }
		  else if("View".equalsIgnoreCase(operation)){
		  %>
		  		 <table width="100%" cellpadding="4" cellspacing="1">
			 <tr class='denotes'> 
				  <td align='right'> 
						<input type="button" name="next" value="Continue" class='input' onClick='return viewContinue()' >
				  </td>
            </tr>
		</table> 
		<%}%>

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
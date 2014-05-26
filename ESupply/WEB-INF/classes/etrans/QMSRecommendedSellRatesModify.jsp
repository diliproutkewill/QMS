<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSellRatesModify.jsp
	Product Name	: QMS
	Module Name		: Recommended Sell Rate
	Task		    : Adding/View/Modify/Invalidate Recommended Sell Rate
	Date started	: 26-07-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Madhusudhan Reddy .Y
	Description		: The application "Adding/View/Modify/Invalidate" Recommended Sell Rate
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						java.util.LinkedHashSet,
						java.util.*,
						java.util.HashMap,
						java.text.DecimalFormat,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSRecommendedSellRatesModify.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
		DecimalFormat decimals    = new java.text.DecimalFormat("##,###,##0.00");
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ArrayList			listValues				=	null;
		//ArrayList			chargeDtlList			=	null;
		ArrayList			finalList				=	null;
		ArrayList			fslListValues			=	null;
		ArrayList			terminalList			=	null;
		String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
		//String				terminalId				=	loginbean.getTerminalId();
		String				operation				=	null;
		ErrorMessage		errorMessageObject		=   null;
		QMSSellRatesDOB		sellRatesDob			=	null;
		QMSSellRatesDOB		sellRatesDob1			=	null;
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
		String				overallMrg				=	null;
		String				marginTpe				=	null;
		String				marginBass				=	null;
		String				consoleTpe				=	null;
		String[]			weighttBkValues			=	null;
		int					noofRecords				=	0;
		int					noofPages				=	0;
		int                 noOfSegments            =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
        //int                 noOfSegments            =   2;
		HashMap				mapValue				=	null;
		String				labelOfTime				=	"";
		String				termId					=	null;
		String				terminalId				=	null;
		int count=0,temp=0;
		String				originRegion			=	null;//added by phani sekhar for wpbn 171213 on 20090605
		String				distenationRegion		=	null;//added by phani sekhar for wpbn 171213 on 20090605
		try
		{
			operation				=	request.getParameter("Operation");
			listValues				=	(ArrayList)session.getAttribute("HeaderValues");
			mapValue				=	(HashMap)session.getAttribute("HashList");
			terminalList			=	(ArrayList)session.getAttribute("TerminalValues");
			//System.out.println("mapValuemapValuemapValuemapValuemapValuemapValue injsp :: "+mapValue);
			if(mapValue==null)
				mapValue	=	new HashMap();
			//System.out.println("listValueslistValueslistValueslistValueslistValueslistValues :: "+listValues);
			//session.removeAttribute("HeaderValues");
			if(listValues!=null)
			{
					sellRatesDob			=	(QMSSellRatesDOB)listValues.get(0);
					finalList				=	(ArrayList)listValues.get(1);
					if(finalList!=null)
					{
						fslListValues	=	(ArrayList)finalList.get(0);
						Integer  no_ofRecords   =   (Integer)finalList.get(1);
						Integer  no_ofPages		=	(Integer)finalList.get(2);
						noofRecords				=	no_ofRecords.intValue();
						noofPages				=	no_ofPages.intValue();
						//System.out.println("noofRecordsnoofRecordsnoofRecordsnoofRecords :: "+noofRecords);
						//System.out.println("noofPagesnoofPagesnoofPagesnoofPagesnoofPages:: "+noofPages);
					}
					currencyId				=	sellRatesDob.getCurrencyId();
					origin					=	sellRatesDob.getOrigin();
					originCut				=	sellRatesDob.getOriginCountry();
					distenation				=	sellRatesDob.getDestination();
					distenationCut			=	sellRatesDob.getDestinationCountry();
					serviceLevel			=	sellRatesDob.getServiceLevel();
					carrierStr				=	sellRatesDob.getCarrier_id();
					shipmentMode			=	sellRatesDob.getShipmentMode();
					weightBrk				=	sellRatesDob.getWeightBreak();
					rateTpe					=	sellRatesDob.getRateType();
					weigtClas				=	sellRatesDob.getWeightClass();
					overallMrg				=	sellRatesDob.getOverAllMargin();
					marginTpe				=	sellRatesDob.getMarginType();
					marginBass				=	sellRatesDob.getMarginBasis();
					consoleTpe				=	sellRatesDob.getConsoleType();
					termId					=	sellRatesDob.getTerminalId();
					originRegion			=   sellRatesDob.getOriginRegions();//added by phani sekhar for wpbn 171213 on 20090605
					distenationRegion		=  sellRatesDob.getDestRegions();//added by phani sekhar for wpbn 171213 on 20090606
		
			}
			if("1".equalsIgnoreCase(shipmentMode))
				labelOfTime = "Approximate Transit Time";
			else if("2".equalsIgnoreCase(shipmentMode))
				labelOfTime = "Approximate Transit Days";
			else if("4".equalsIgnoreCase(shipmentMode))
				labelOfTime = "Approximate Transit Time";
			if(termId!=null)
				terminalId	=	termId;
			else
				terminalId	=	loginbean.getTerminalId();

	

%>
<html>
<head>
<title>Recommended Sell Rates Master</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
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
	
	if(document.forms[0].baseCurrency.value.length==0)
	{
		msgErrors	+="Please Enter/Select CurrencyId.\n";
		focusPosition[0]	=	"baseCurrency";
	}//modified by phani sekhar for wpbn 171213 on 20090606
	if(document.forms[0].origin.value.length==0 && document.forms[0].destination.value.length==0 && document.forms[0].carriers.value.length==0 && document.forms[0].serviceLevelId.value.length==0&&document.forms[0].originRegion.value.length==0&&document.forms[0].originCountry.value.length==0&&document.forms[0].destinationRegion.value.length==0&&document.forms[0].destinationCountry.value.length==0)
	{
		msgErrors	+="Please Enter/Select OriginId Or Destination or Carriers or Service Level.\n";
		focusPosition[1]	=	"origin";
	}
	
	/*if(document.forms[0].destination.value.length==0)
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
	if(obj.name=="search")
	{
		document.forms[0].action='QMSSellRateController?Operation='+operation+'&subOperation=Values';
		document.forms[0].submit();
		return true;
	}
}
function checkValidation(obj)
{
	var operation		=	'<%=operation%>';
	var flag=false;
	var obj		=	document.getElementsByName("margenValue");
	var obj1		=	document.getElementsByName("surchargeValue"); 
	var objSize	=	obj.length;
	for( i=0;i<objSize;i++)
	{
		if(obj[i].value.length==0)
		{
			alert("Please Enter Margin.");
			obj[i].focus();
			return false;
		}
	}
	for( i=0;i<obj1.length;i++)
	{
		if(obj1[i].value.length==0)
		{
			alert("Please Enter Surcharge Margin.");
			obj1[i].focus();
			return false;
		}
	}
<%
	if(fslListValues!=null && fslListValues.size()>0)
	{
		if("SLAB".equalsIgnoreCase(weightBrk) || "LIST".equalsIgnoreCase(weightBrk))
		{
			sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
			weighttBkValues					=	sellRatesDob1.getWeightBreaks();
			int	wtBreakLength				=	weighttBkValues.length;
			for(int i=0;i<wtBreakLength;i++)
			{			
%>
				if(document.forms[0].overMargin.value=="N")
				{
					var nameVal	='<%=weighttBkValues[i]%>';
					var name=document.getElementsByName(nameVal);
					for(n=0;n<name.length;n++)
					{
						if(name[n].value.length==0)
						{
							alert("Please Enter Margin.");
							name[n].focus();
							return false;
						}
						else
							break;
					}
				}
<%
			}
		}
	}
%>
	var minRates=document.getElementsByName("mfValues");
	for(m=0;m<minRates.length;m++)
	{
		if(minRates[m].checked==false)
			flag=false;
		else
		{
				flag=true;
				document.forms[0].action='QMSSellRateController?Operation='+operation+'&subOperation='+operation;
				document.forms[0].submit();
				return true;
		}
	}
	if(!flag)
	{
		alert("Please select atleast on check box.");
		return false;
	}
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
//This function used for call the LOVs
function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var shipmentMode	=	document.forms[0].shipmentMode.value;
	var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2= (toSet=='origin')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
	var searchString3= (toSet=='origin')?(document.forms[0].originRegion.value):(document.forms[0].destinationRegion.value);//added by phani sekhar for wpbn 171213 on 20090606

	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+"&shipmentMode="+shipmentMode+'&searchString3='+searchString3;//modified by phani sekhar for wpbn 171213 on 20090606
	var Win=open(Url,'Doc',Features);
}
function showCurrencyLOV()
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
}
function showServiceLevelLOV()
{   
	var operation		=	'<%=operation%>';//added by rk
	var searchString=document.forms[0].serviceLevelId.value.toUpperCase();
	var Url		=	"etrans/ETCLOVServiceLevelIds1.jsp?searchString="+searchString+"&shipmentMode="+document.forms[0].shipmentMode.options.value+"&Operation"+operation;	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}	//added by phani sekhar for wpbn 171213 on 20090606
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

function showCountryLOV(toSet)
{
	var locationId;
	var shipmentMode	=	document.forms[0].shipmentMode.value;
	if(toSet=='originCountry')
	{
		locationId = document.forms[0].origin.value;
	}
	else if(toSet=='destinationCountry')
	{
		locationId = document.forms[0].destination.value;
	}
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var searchString = (toSet=='originCountry')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
	var searchString3= (toSet=='originCountry')?(document.forms[0].originRegion.value):(document.forms[0].destinationRegion.value);//added by phani sekhar for wpbn 171213 on 20090606

	var Url='etrans/ETCLOVCountryIds1.jsp?searchString='+searchString+"&whereClause=CSR&wheretoset="+toSet+"&locationId="+locationId+"&shipmentMode="+shipmentMode+'&searchString3='+searchString3;//modified by phani sekhar for wpbn 171213 on 20090606
	var Win=open(Url,'Doc',Features);
}
function showCarrierIds()
{
	 var operation		=	'<%=operation%>';//added by rk
	 var shipmentMode	=	document.forms[0].shipmentMode.value;
	var URL 		= 'etrans/ETransLOVCarrierIds1.jsp?shipmentMode='+shipmentMode+'&multiple=multiple&Operation='+operation;	
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 700,height = 300,resizable = yes';
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
/*
* This function used for select all checkboxs
*/
function selectAll(obj)
{
	if(obj.name=="select1" && document.forms[0].select1.checked)
	{
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=="checkbox" && document.forms[0].elements[i].name=="mfValues")
			{
				document.forms[0].elements[i].checked=true;
			} 
		} 
	}
	else 
	{
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=="checkbox" && document.forms[0].elements[i].name=="mfValues")
			{
				document.forms[0].elements[i].checked=false;
				
			}
		}
	}
}
/*
* This function used for Margins disply to respective conditions
*/
function showWeightBreaks()
{
    var data="";
<%	
	if(fslListValues!=null && fslListValues.size()>0)
	{
%>
		if(document.forms[0].overMargin.value=="Y" && document.forms[0].marginType.value=="P" && document.forms[0].marginBasis.value=="N" && document.forms[0].weightBreak.value=="FLAT" && document.forms[0].rateType.value=="FLAT")
		{
			  data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7'></td><td colspan='3'>&nbsp;</td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		}
		else if((document.forms[0].overMargin.value=="Y" || document.forms[0].overMargin.value=="N") && (document.forms[0].marginType.value=="A" || document.forms[0].marginType.value=="P") && document.forms[0].marginBasis.value=="N" && document.forms[0].weightBreak.value=="FLAT" && document.forms[0].rateType.value=="FLAT")
		{	 //Modified by Rakesh for Issue:
			/*data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td>Margin </td><td width='70' align='left' colspan='2' ><input type=text  class='text' name='margenValue' size='7' onkeypress='return validate(event)'> <input type=text  class='text' name='margenValue' size='7' onkeypress='return validate(event)'></td><td width='108' align='left' colspan='4'><input type=text  class='text' name='margenValue' size='7'></td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";	 */  
			data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td>Margin </td><td width=''70' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7' onkeypress='return validate(event)'> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=text  class='text' name='margenValue' size='7' onkeypress='return validate(event)'></td><td width='108' align='left' colspan='4'><input type=text  class='text' name='margenValue' size='7' onkeypress='return validate(event)'></td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";  
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="A"  && document.forms[0].weightBreak.value=="SLAB" && (document.forms[0].rateType.value=="FLAT" || document.forms[0].rateType.value=="SLAB"))
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td width='90' align='left' colspan='2'>Min Margin <br><input type=text  class='text' name='margenValue' size='7'  ></td><td width='108' align='left' colspan='4'>Slab Margin <br><input type=text  class='text' name='margenValue' size='7' ></td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="P"  && document.forms[0].weightBreak.value=="SLAB" && (document.forms[0].rateType.value=="FLAT" || document.forms[0].rateType.value=="SLAB"))
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td width='90' align='left' colspan='2'>Margin <br><input type=text  class='text' name='margenValue' size='7' ></td><td colspan='4'>&nbsp;</td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="A" || document.forms[0].marginType.value=="P")  && document.forms[0].weightBreak.value=="SLAB" && (document.forms[0].rateType.value=="FLAT" || document.forms[0].rateType.value=="SLAB" || document.forms[0].rateType.value=="BOTH"))
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' width='55%'>Margin </td>";
<%
			if(fslListValues!=null)
			{
		
				sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
				weighttBkValues					=	sellRatesDob1.getWeightBreaks();
				String[]    rateDescs         =   sellRatesDob1.getRateDescription(); 
				int	wtBreakLength				=	weighttBkValues.length;
				for(int i=0;i<wtBreakLength;i++)
				{
              if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
		%>
						
				data	= data + "<td  align='right'><input type=text  class='text' name='<%=weighttBkValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}
%>
				data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue'  size='7' ></td><td colspan='6'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="A"  && document.forms[0].weightBreak.value=="LIST" &&  document.forms[0].rateType.value=="PIVOT")
		{
			  data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='305'></td><td width='90' align='left' >Pivot Margin</td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7' ></td><td width='90' align='left' >Over Pivot Margin</td><td width='108' align='left' ><input type=text  class='text' name='margenValue' size='7' ></td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="P"  && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="PIVOT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td width='90' align='left' colspan='2'>Margin </td><td width='108' align='left' ><input type=text  class='text' name='margenValue' size='7' ></td><td colspan='4'>&nbsp;</td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="A" || document.forms[0].marginType.value=="P")  && document.forms[0].weightBreak.value=="LIST" &&  document.forms[0].rateType.value=="PIVOT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' width='55%' >Pivot Margin</td>";
<%
			if(fslListValues!=null)
			{
				sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
				weighttBkValues					=	sellRatesDob1.getWeightBreaks();
				String[]    rateDescs         =   sellRatesDob1.getRateDescription(); 
				int	wtBreakLength				=	weighttBkValues.length;
				for(int i=0;i<wtBreakLength;i++)
				{
                    if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
		%>
						
				data	= data + "<td  align='right'><input type=text  class='text' name='<%=weighttBkValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}
%>
			data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue'  size='5' ></td><td colspan='6'>&nbsp;</td></tr></table>";
		}
	
		else if(document.forms[0].shipmentMode.value=="2" &&  document.forms[0].consoleType.value=="FCL" && document.forms[0].overMargin.value=="Y"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].shipmentMode.value=="2" &&  document.forms[0].consoleType.value=="FCL" && document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td width='55%' align='right'>Margin </td>";
<%
			if(fslListValues!=null)
			{
				sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
				weighttBkValues					=	sellRatesDob1.getWeightBreaks();
					String[]    rateDescs         =   sellRatesDob1.getRateDescription(); 
				int	wtBreakLength				=	weighttBkValues.length;
				for(int i=0;i<wtBreakLength;i++)
				{
                   if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
		%>
						
				data	= data + "<td  align='right'><input type=text  class='text' name='<%=weighttBkValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}
%>
			data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue'  size='5' ></td><td colspan='6'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].shipmentMode.value=="4" &&  document.forms[0].consoleTypes.value=="FTL" && document.forms[0].overMargin.value=="Y"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		}
		else if(document.forms[0].shipmentMode.value=="4" &&  document.forms[0].consoleTypes.value=="FTL" && document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td width='55%' align='right'>Margin </td>";
<%
			if(fslListValues!=null)
			{
				sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
				weighttBkValues					=	sellRatesDob1.getWeightBreaks();
				String[]    rateDescs         =   sellRatesDob1.getRateDescription(); 
				int	wtBreakLength				=	weighttBkValues.length;
									for(int i=0;i<wtBreakLength;i++)
				{			

				if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
		%>
						
				data	= data + "<td  align='right'><input type=text  class='text' name='<%=weighttBkValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}
%>
			data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue'  size='5' ></td><td colspan='6'>&nbsp;</td></tr></table>";
			}
		else
		{
			alert("This combination not available please check onther combination.");
		}
		if( document.layers)
		{
			document.layers.cust.document.write(data);
			document.layers.cust.document.close();
		}
		else
        {
            if(document.all)
            {
               cust.innerHTML = data;
             }
         }
<%
	}
%>
}
function showHide()
{
	document.forms[0].shipmentMode.focus();
	var data="";
	var index	=	document.forms[0].shipmentMode.value;
	if(index=="2")
	{
		 data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleType" class="select" onChange="changeValue(this);disableSubmit()"><option  value="LCL" <%="LCL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>LCL</option><option  value="FCL" <%="FCL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>FCL</option></select>';
	}
	else if(index=="4")
	{
		data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleTypes" class="select" onChange="changeValue(this);disableSubmit()"><option  value="LTL" <%="LTL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>LTL</option><option  value="FTL" <%="FTL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>FTL</option></select>';
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
function showRateType()
{
	var data="";
	var index	=	document.forms[0].weightBreak.value;
	var mode	=	document.forms[0].shipmentMode.value;

	if((index=="FLAT") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL") ))
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="FLAT" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option></select>';
	}
	else if((index=="SLAB") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL")))
	{
		  data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="FLAT" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="SLAB" <%="SLAB".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Slab</option><option value="BOTH" <%="BOTH".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Both</option></select>';
	}
	else if(index=="LIST" && mode=="1")
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option value="PIVOT" <%="PIVOT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
	}
	else
	{
		data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="changeValue();disableSubmit();"><option  value="FLAT" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="SLAB" <%="SLAB".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Slab</option><option value="BOTH" <%="BOTH".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Both</option><option value="PIVOT" <%="PIVOT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
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
			document.forms[0].weightBreak.value="LIST"; 
			document.forms[0].rateType.value="FLAT";
		}
		else if(document.forms[0].consoleType.value=="LCL" && document.forms[0].weightBreak.value=="LIST")
		{
			document.forms[0].weightBreak.value="FLAT";
		}
	}
	else if(document.forms[0].shipmentMode.value=="4")
	{
		if(document.forms[0].consoleTypes.value=="FTL")
		{
			document.forms[0].weightBreak.value="LIST"; 
			document.forms[0].rateType.value="FLAT";
		}
		else if(document.forms[0].consoleTypes.value=="LTL" && document.forms[0].weightBreak.value=="LIST")
		{
			document.forms[0].weightBreak.value="FLAT";
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
function displayMargins(orign,distination,carier,service,buyrateId,frquency)
{
	var weightBreak		=	document.forms[0].weightBreak.value;
	var rateType		=	document.forms[0].rateType.value;
	var shipmentMode	=	document.forms[0].shipmentMode.value;
	var currency		=	document.forms[0].baseCurrency.value
	var terminalId		=	document.forms[0].terminalId.value
	var	oper			=	'<%=operation%>';
	var URL 		= 'etrans/QMSRecommendedSellRatesMarginDisplay.jsp?shipmentMode='+shipmentMode+'&rateType='+rateType+'&weightBreak='+weightBreak+'&buyrateId='+buyrateId+'&service='+service+'&carier='+carier+'&distination='+distination+'&orign='+orign+'&currency='+currency+'&oper'+oper+'&frequency='+frquency+'&terminalId='+terminalId;	
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 900,height = 500,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
}
function functionCall(operation,pageNo)
{
	document.forms[0].action='QMSSellRateController?&pageNo='+pageNo+'&Operation='+operation+'&subOperation=Values';
	document.forms[0].submit();
}
 //Added by Rakesh for Issue:                   on 03-03-2011
function validate(e){
	if(document.forms[0].hideChargeValue0.value=='0'){
	return false;
	}else{
	if(!(e.keyCode>='48' && e.keyCode<='57')){
	    return false;
	}
	}
	 if(document.forms[0].hideChargeValue1.value=='0'){
	return false;
	}else{
	if(!(e.keyCode>='48' && e.keyCode<='57')){
	    return false;
	}
	}
	if(document.forms[0].hideChargeValue2.value=='0'){
	return false;
	}else{
	if(!(e.keyCode>='48' && e.keyCode<='57')){
	    return false;
	}
	}
	return true;
}	   //Ended by Rakesh for Issue:                   on 03-03-2011
</script>
</head>
<body bgcolor="#FFFFFF" onLoad="showHide();showRateType();showWeightBreaks();">
<form   method="post">

  <table width="102%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
       <table width="100%" cellpadding="4" cellspacing="0">
			<tr valign="top" class='formlabel'>
			   <td colspan="12" width="903">Recommended Sell Rates Master - Modify </td>
			   <td align="right">QS1060221</td>
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
					<select name='shipmentMode' class='select' size=1 onChange='showHide();disableSubmit();' >
						<option value='1' <%="1".equalsIgnoreCase(shipmentMode) ? "SELECTED" : "" %>>Air</option>
						<option value='2'<%="2".equalsIgnoreCase(shipmentMode) ? "SELECTED" : "" %>>Sea</option>
						<option value='4'<%="4".equalsIgnoreCase(shipmentMode) ? "SELECTED" : "" %>>Truck</option>
					</select>
					</select>
				</td>
				
				<td>
				  <div id='cust1' style='position:relative;'></div>
				</td>
				
				<td>Weight Break: <font color="#FF0000">*</font><br>
					<select size="1" name="weightBreak" class='select' onChange="changeValue();disableSubmit();showRateType();">
						<option selected value="FLAT" <%="FLAT".equalsIgnoreCase(weightBrk) ? "SELECTED" : "" %>>Flat</option>
						<option  value="SLAB" <%="SLAB".equalsIgnoreCase(weightBrk) ? "SELECTED" : "" %>>Slab</option>
						<option  value="LIST" <%="LIST".equalsIgnoreCase(weightBrk) ? "SELECTED" : "" %>>List</option>
					</select>
				</td>
				<td>
					<div id='cust2' style='position:relative;'></div>
				</td>
				<td>Currency:<font  color=#ff0000>*</font><br>
					<input type='text' class='text' name='baseCurrency' value='<%=currencyId!=null?currencyId:""%>' size='10' maxlength='3' onblur='this.value=this.value.toUpperCase()' onChange='disableSubmit();'>
					&nbsp;<input type='button' value='...' name='currencyLOV' class='input' onclick='showCurrencyLOV()'>
				</td>
				<td>Weight Class: <br>
					<select size="1" name="weightClass" class='select' onChange='disableSubmit();'>
						<option  value="G" <%="G".equalsIgnoreCase(weigtClas) ? "SELECTED" : "" %>>General</option>
						<option  value="W" <%="W".equalsIgnoreCase(weigtClas) ? "SELECTED" : "" %>>Weight Scale</option>
					</select>
				</td>
				<td>Terminal Id: <br>
					<select size="1" name="terminalId" class='select' onChange='disableSubmit();'>
<%
						if(terminalList!=null)
						{
							int		terminalListSize	=	terminalList.size();
							for(int l=0;l<terminalListSize;l++)
							{
%>
								<option  value="<%=(String)terminalList.get(l)%>" <%=terminalId.equalsIgnoreCase((String)terminalList.get(l)) ? "SELECTED" : "" %>><%=(String)terminalList.get(l)%></option>
<%
							}
						}
%>
						
					</select>
				</td>
				</tr>
		</table>
		<table width="100%" cellpadding="4" cellspacing="1">
			<tr class='formheader'> 
				<td>Origin:</td>
				<td>Origin Countries:</td>
				<td>Origin Region:</td><!-- added by phani sekhar for wpbn 171213 on 20090606 -->
				<td>Destination:</td>
				<td>Destination Countries:</td>
				<td>Destination Region:</td><!-- added by phani sekhar for wpbn 171213 on 20090606 -->
				<td>Carrier:</td>
				<td>Service Level:</td>
			</tr>
			<tr class='formdata'> 
				<td>
					<input type="text" name="origin" value='<%=origin!=null?origin:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' maxlength='100'>&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='showLocationLOV("origin")'>
				</td>
				<td>
					<input type="text" name="originCountry"  value='<%=originCut!=null?originCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' maxlength='100'>&nbsp;<input type="button" value="..." name="originCountryLOV"  class='input' onclick='showCountryLOV("originCountry")'>
				</td>
				<td><!-- added by phani sekhar for wpbn 171213 on 20090606 -->
					<input type="text" name="originRegion"  value='<%=originRegion!=null?originRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' >&nbsp;<input type="button" value="..." name="originRegionLOV"  class='input' onclick='showRegionLOV("originRegion")'>
				</td>
				<td>
					<input type="text" name="destination" value='<%=distenation!=null?distenation:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' maxlength='100'>&nbsp;<input type="button" value="..." name="destinationLOV"  class='input' onclick='showLocationLOV("destination")'>
				</td>
				<td>
					<input type="text" name="destinationCountry" value='<%=distenationCut!=null?distenationCut:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' maxlength='10'>&nbsp;<input type="button" value="..." name="destinationCountyLOV"  class='input' onclick='showCountryLOV("destinationCountry")'>
				</td>
				<td><!-- added by phani sekhar for wpbn 171213 on 20090606-->
					<input type="text" name="destinationRegion" value='<%=distenationRegion!=null?distenationRegion:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationRegionLOV"  class='input' onclick='showRegionLOV("destinationRegion")'>
				</td>
				<td>
					<input type="text"  name="carriers" value='<%=carrierStr!=null?carrierStr:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()' maxlength='100'>&nbsp;<input type="button" value="..." name="gatebut"  class='input' onclick='showCarrierIds()'>
					
				</td>
				<td>
					<input type="text" name="serviceLevelId" value='<%=serviceLevel!=null?serviceLevel:""%>' class='text' size="8" onblur='this.value=this.value.toUpperCase()' maxlength='100'>&nbsp;<input type="button" value="..." name="serviceLevelLOV"  class='input' onclick='showServiceLevelLOV()'>
				</td>
			</tr>
			<tr class='denotes'>             
				<td colspan='5'> 
					<font color=red> * 
					</font>Each text box is multiple selection with comma separation.User can enter manually or select from LOV
				</td>
				<td colspan='2' />
				<td colspan='1' align='right'> 
					<input type="hidden" name="pageNo" value="1">
					<input type="button" class='input' name="search" value="Search"  onClick="return validation(this)">
				</td>
			</tr>
	</table>
	<table width="100%" cellpadding="4" cellspacing="1">
<%	
	if(fslListValues!=null && fslListValues.size()>0)
	{
	
%>
				<tr class='formheader'> 
					<td>Select <input type='checkbox' name="select1" onClick="selectAll(this)"></td>
					<td>Origin</td>
					<td>Origin<br>Country</td>
					<td>Destination</td>
					<td>Destination Country</td>
					<td>Carrier</td>
					<td>Service Level</td>
					<td><%=labelOfTime%></td>
					<td>Frequency</td>
<%
					sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
					weighttBkValues					=	sellRatesDob1.getWeightBreaks();
					int	wtBreakLength				=	weighttBkValues.length;
					System.out.println("wtBreakLengthwtBreakLengthwtBreakLengthwtBreakLengthwtBreakLength:: "+wtBreakLength);
					String[]    rateDescs         =   sellRatesDob1.getRateDescription();  
				for(int j=0;j<wtBreakLength;j++)
					{
					System.out.println("weighttBkValues[j]..************."+weighttBkValues[j]);
						if("A FREIGHT RATE".equalsIgnoreCase(rateDescs[j]))
						{
							 count++;%>
						
				<td ><%=weighttBkValues[j]%></td>
					 
<%		
						}
               
					}%>
					<td>DensityRatio</td>
					<!-- Modified by Mohan for Issue No.219976 on 2-11-2010 -->
					<td>Internal Notes</td> 
					<td>External Notes</td>
					<td>Margin Details<br>(Previous)</td>
				</tr>
<%
			int	fslListValuessize				=	fslListValues.size();
			String	keyVlue						=	"";
			System.out.println("fslListValuessize..."+fslListValuessize);
			for(int i=0;i<fslListValuessize;i++)
			{
				sellRatesDob1		=	(QMSSellRatesDOB)fslListValues.get(i);
				keyVlue				=	sellRatesDob1.getBuyRateId()+"&"+sellRatesDob1.getLanNumber();
					weighttBkValues					=	sellRatesDob1.getWeightBreaks();
					wtBreakLength				=	weighttBkValues.length;

				//System.out.println("keyVluekeyVluekeyVlue :: "+keyVlue);
%>
				<tr class='formdata'> 
<%
				if(mapValue.containsKey(keyVlue))
				{
%>
					<td><input type='checkbox' name="mfValues" id='<%=i%>'  value='<%=keyVlue%>' checked></td>
<%
				}
				else
				{
%>
					<td ><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='<%=i%>'></td>
<%
				}
%>				
				<input type="hidden" name="checkValue" value="<%=keyVlue%>">
					<td><%=sellRatesDob1.getOrigin()!=null?sellRatesDob1.getOrigin():""%></td>
					<td><%=sellRatesDob1.getOriginCountry()!=null?sellRatesDob1.getOriginCountry():""%></td>
					<td><%=sellRatesDob1.getDestination()!=null?sellRatesDob1.getDestination():""%></td>
					<td><%=sellRatesDob1.getDestinationCountry()!=null?sellRatesDob1.getDestinationCountry():""%></td>
					<td><%=sellRatesDob1.getCarrier_id()!=null?sellRatesDob1.getCarrier_id():""%></td>
					<td><%=sellRatesDob1.getServiceLevel()!=null?sellRatesDob1.getServiceLevel():""%></td>
					<td><%=sellRatesDob1.getTransitTime()!=null?sellRatesDob1.getTransitTime():""%></td>
					<td><%=sellRatesDob1.getFrequency()!=null?sellRatesDob1.getFrequency():""%></td>
<%
					String[] chargesValue	=	sellRatesDob1.getChargeRates();
					//double[] chargesValue	=	sellRatesDob1.getChargeRatesValues();
					String[] chargeInd		=	sellRatesDob1.getChargeInr();
					int chargesLength		=	chargesValue.length;
					//System.out.println("chargesLengthchargesLengthchargesLengthchargesLengthchargesLength:: "+chargesLength);
				if("SLAB".equalsIgnoreCase(weightBrk) && "BOTH".equalsIgnoreCase(rateTpe))
				{
					for(int k=0;k<count;k++)
					{
						if("-".equals(chargesValue[k]))
						{
						//System.out.println("chargesValue[k]chargesValue[k]chargesValue[k]chargesValue[k]:: "+chargesValue[k]);
%>
							<td><%="-"%></td>
<%
						}
						else
						{
%>
							<td><%=decimals.format(Double.parseDouble(chargesValue[k]))%><%="FLAT".equalsIgnoreCase(chargeInd[k])?"(F)":"(S)"%><input type='hidden' value='<%=decimals.format(Double.parseDouble(chargesValue[k]))%>' name='hideChargeValue<%=k%>'><!-- Modified by Rakesh for Issue: --></td>
<%
						}
					}
				}
				else
				{
					for(int k=0;k<count;k++)
					{
						if("-".equals(chargesValue[k]) && "A FREIGHT RATE".equalsIgnoreCase(rateDescs[k]))
						{
%>
							<td><%="-"%></td>
<%
						}
						else
						{
%>
							<td><%=decimals.format(Double.parseDouble(chargesValue[k]))%><input type='hidden' value='<%=decimals.format(Double.parseDouble(chargesValue[k]))%>' name='hideChargeValue<%=k%>'><!-- Modified by Rakesh for Issue: --></td>
<%
						}
					}
				}
%>
					<td><%=sellRatesDob1.getDensityRatio()!=null?sellRatesDob1.getDensityRatio():""%></td>
					<td> 
					<!-- Modified by Mohan for Issue No.219976 on 2-11-2010 -->
						 <input type='text' class='text' name="notes" value='<%=sellRatesDob1.getNotes()!=null?sellRatesDob1.getNotes():""%>' size="7" id='1' maxlength='1000'>
					</td>
					<td> 
						 <input type='text' class='text' name="extNotes" value='<%=sellRatesDob1.getExtNotes()!=null?sellRatesDob1.getExtNotes():""%>' size="7" id='extNotes' maxlength='1000'>
					</td>
					<td align="center">
						<input type='button' class='text' name="VieButton" value="View" size="7" onClick="displayMargins('<%=sellRatesDob1.getOrigin()%>','<%=sellRatesDob1.getDestination()%>','<%=sellRatesDob1.getCarrier_id()%>','<%=sellRatesDob1.getServiceLevel()%>','<%=sellRatesDob1.getBuyRateId()%>','<%=sellRatesDob1.getFrequency()%>')">
					</td>
				  </tr>
  <%
						String[] ratedesc =sellRatesDob1.getRateDescription();
						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: ratedesc)
							rdesc.add(s);
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());
						//System.out.println("surchargeDesc..."+surchargeDesc);
						//System.out.println("wtBreakLength..."+wtBreakLength);

						// Added By Kishore Podili for SurCharge Currency 
						String[] schCurrencyArray  =sellRatesDob1.getSurChargeCurency();
						Set schCurrencySet	= new LinkedHashSet();
						ArrayList schCurrencyList	= new ArrayList();
						for(String s: schCurrencyArray)
							schCurrencySet.add(s);
						 it	= schCurrencySet.iterator();
						while(it.hasNext())
								schCurrencyList.add(it.next());

						if(count<wtBreakLength)
			         	{
						  if((wtBreakLength<count*2+11)||"slab".equalsIgnoreCase(weightBrk)||"flat".equalsIgnoreCase(weightBrk)
							  || "list".equalsIgnoreCase(weightBrk))
					       {
							  for (int r=1;r<surchargeDesc.size();r++){
								 String diffDesc	= (String)surchargeDesc.get(r);
								 String schCurr = (String)schCurrencyList.get(r);
								 int desCount	=	0;
						 %>
						  <tr class="formdata">
						  <td>&nbsp</td>
				    <td colspan=3><b><%=diffDesc.substring(0,(diffDesc.length()-3))%></b></td>  <!-- Added By Kishore for Sur Charge Desc -->
					<td>Currency</td>

				  <%for(int m=count;m<wtBreakLength;m++)
				  {//&& (diffDesc.equalsIgnoreCase(ratedesc[m]) )
					 if(weighttBkValues[m]!=null&&weighttBkValues[m]!="" && (diffDesc.equalsIgnoreCase(ratedesc[m]) ))
					  { 
						 desCount++;
						//System.out.println("(weighttBkValues[m]..."+weighttBkValues[m]);
						// Added by Kishore  on 16Feb2011 to display slabs as min,slab and flat etc
						if("2".equals(shipmentMode)&&"FCL".equals(consoleTpe))
						  {
								weighttBkValues[m] = weighttBkValues[m].length() > 4?weighttBkValues[m].substring(0,4):weighttBkValues[m];
						  }
							else	
							weighttBkValues[m] = weighttBkValues[m].length() > 5?weighttBkValues[m].substring(5,weighttBkValues[m].length()):weighttBkValues[m];
						 %> <td><%=weighttBkValues[m]%></td>
<%			
			     
					  }
					  
				} 
				  //System.out.println("desCount.."+desCount);
					//  temp	=	count*2+11-wtBreakLength;
					  temp	=	10-(desCount+2)+count;    //<!-- Added By Kishore for Allignment -->
					  for(int s=0;s<temp;s++)
					 {
%>
			  <td>&nbsp;</td>
			 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
				<td colspan=3>&nbsp</td>
				<td> <%=schCurr%></td>
                   <%for(int n=count;n<chargesLength;n++)
				   {
					  if(chargesValue[n]!=null&&chargesValue[n]!=""&& (diffDesc.equalsIgnoreCase(ratedesc[n]) ))
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
			   </tr>
			   <%	   }}else
							   {
				  for (int r=1;r<surchargeDesc.size();r++){
						 String diffDesc	= (String)surchargeDesc.get(r);
						 %>
						  <tr class="formdata">
						  <td>&nbsp</td>
				    <td><b><%=diffDesc.substring(0,(diffDesc.length()-3))%></b></td>  <!-- Added By Kishore for Sur Charge Desc -->

				  <%
					 String[] rateDesc	=	sellRatesDob1.getRateDescription();
				   for(int m=count;m<count*2+11;m++)
				  {
					   //System.out.println("rateDesc..."+rateDesc[m]);
					   // Added by Kishore on 16Feb2011 to display slabs as min,slab and flat etc
					   if("2".equals(shipmentMode)&&"FCL".equals(consoleTpe))
						  {
								weighttBkValues[m] = weighttBkValues[m].length() > 4?weighttBkValues[m].substring(0,4):weighttBkValues[m];
						  }
					   else	
							weighttBkValues[m] = weighttBkValues[m].length() > 5?weighttBkValues[m].substring(5,weighttBkValues[m].length()):weighttBkValues[m];

					  if(weighttBkValues[m]!=null&&weighttBkValues[m]!=""  && (diffDesc.equalsIgnoreCase(ratedesc[m]) ))
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
	              %>

				  
				  <td><%=chargesValue[n]%>
                </td>
				<%
				  }
				 %>
			   </tr>
			   <tr class="formdata">
				    <td>&nbsp;</td>
					 <td>&nbsp;</td>
				  <%
					 temp	=0;
				   for(int m=count*2+11;m<wtBreakLength;m++)
				  {
					   if(weighttBkValues[m]!=null&&weighttBkValues[m]!=""  && (diffDesc.equalsIgnoreCase(ratedesc[m]) ))
					  {
						   temp++;%>
						<td><%=weighttBkValues[m]%></td>
<%			         }
			     } 
				  
			  for(int s=temp;s<count+7;s++)
					{
					%>		  <td>&nbsp;</td>
								 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count*2+11;n<chargesLength;n++)
				  { 
					%>
				  
				  <td><%=chargesValue[n]%>
                </td>
				<%
				  }
				  for(int s=temp;s<count+7;s++)
					{%>
				 <td>&nbsp;</td>
			  <%} %>
			  </tr>
			<%			
				}	  
				  }
						}
			}
			%>
							  
				<tr>
  <td valign="middle" colspan="10" >
<%
					int  currentPageNo	=	0;
					if(sellRatesDob.getPageNo()!=null)
						currentPageNo = Integer.parseInt(sellRatesDob.getPageNo());

					if(currentPageNo != 1)
					{
%>
						<a href="#" onClick="functionCall('<%=operation%>','<%=currentPageNo-1%>')"><img src="images/Toolbar_backward.gif"  alt="Previous Page"></a>
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
							<a href="#" onClick="functionCall('<%=operation%>','<%=(i + 1)%>')" ><%=(i + 1)%></a>&nbsp;
<%	                       }
				if(currentPageNo != noofPages)
				  {
%>
						<a href="#" onClick="functionCall('<%=operation%>','<%=currentPageNo+1%>')"><img src="images/Toolbar_forward.gif" alt="Next Page"></a>
 <%					
				  }
%>
                </td>
				</tr>
	</table>
	 <table width="100%" cellpadding="4" cellspacing="1">
		  <tr class=formheader> <td colspan=12 > Margin Calculations </td></tr>
          <tr class='formdata'> 
            <td>
                Overall Margin: <br>
				<select size="1" name="overMargin" class='select' onChange="showWeightBreaks()">
					<option value="N" >NO</option>
					<option value="Y" >YES</option>
					
				</select>
            </td>
            <td > 
				Margin Type: <br>
				<select size="1" name="marginType" class='select' onChange="showWeightBreaks();">
					<option value="P" >PERCENTAGE</option>
					<option value="A" >ABSOLUTE</option>
				</select>
            </td>
            <input type="hidden" name="marginBasis" value="N">
           </tr>
		</table>
			<div id='cust' style='position:relative;'></div>
		<table width="100%" cellpadding="4" cellspacing="1">
			 <tr class='denotes'> 
				  <td align='right'> 
						<input type="button" name="next" value="Next>>" class='input' onClick="return checkValidation(this)">
				  </td>
            </tr>
		</table>
<%
	}
	else if(listValues!=null && (fslListValues!=null && fslListValues.size()==0))
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
			//Logger.error(FILE_NAME,"Error in QMSRecommendedSellRatesModify.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSRecommendedSellRatesModify.jsp "+e);
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details  ","QMSSellRateController?Operation="+operation+"&subOperation=Enter"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
    finally
    {
      try
      {
 
              if(listValues!=	null)
                  listValues=null;
               
              if(finalList!=null)
              finalList=null;
              
              if(fslListValues!=	null)
                fslListValues=null;
              if(terminalList!=	null)
                terminalList=null;
              if(mapValue!=null)
              mapValue=null;
      }catch(Exception e)
      {
        e.printStackTrace();
      }
    }
%>
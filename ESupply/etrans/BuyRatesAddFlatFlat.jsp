<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.x  
%
--%>
 <%@ page import = "javax.naming.InitialContext,
					org.apache.log4j.Logger,
					java.util.ArrayList,
					com.qms.operations.rates.dob.RateDOB,
					com.qms.operations.rates.dob.FlatRatesDOB,
					com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
					com.foursoft.esupply.common.bean.DateFormatter,
					java.util.*
					"%>
<%!
  private static Logger logger = null;
//com.dhl.qms.rates.dao.*
	private static final String FILE_NAME = "BuyRatesAddFlatFlat.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);
	int				noOfRows				=	0; 
	int				totalRows				=	0; 
	int				preRows					=	-1; 
	int				int_count				=	0;
	int				srcount					=	0;//Added by govind for the CR-219973
	String			carrier					=	null; 
	String			currency				=	null; 
	String			uom						=	null; 
	String			consoleType				=	null; 
	String			weightBreak				=	null; 
	String			rateType				=	null; 
	String          dummybothname			=	null;//Added by govind for the CR-219973
        String surchargeWeightBreaks[]			=	null;//Added by govind for the CR-219973
        String surchargeDesc[]					=	null;//Added by govind for the CR-219973
	String surchargeIds[]					=	null;//Added by govind for the CR-219973
	String srchargeIds[]					=	null;//Added by govind for the CR-219973
	String srwatbraks[]						=	null;//Added by govind for the CR-219973
	ArrayList		lanes					=	null; 
	ArrayList		laneslist				=	null;
	RateDOB			rateDOB					=	null;
	FlatRatesDOB	flatRatesDOB			=	null;
	ESupplyGlobalParameters e1= new ESupplyGlobalParameters();
	String				effectiveFrom	=	"";
	DateFormatter		dateUtility		=	null;
	HashMap       mapList                 =  new HashMap();
%>


<%  
	try{

            String fromWhere  = request.getParameter("fromWhere");
                //Added by govind for the CR-219973
			surchargeWeightBreaks  = (String[])session.getAttribute("surchargeWeightBreaks");//Govind
			surchargeIds		   = (String[])session.getAttribute("surchargeIds");//Govind
			surchargeDesc		   = (String[])session.getAttribute("surchargeDesc");//Govind
			if(surchargeIds!=null && surchargeDesc!=null) {
			for(String s : surchargeIds)
				System.out.println("surchargeIds..."+s);
			for(String s : surchargeWeightBreaks)
				System.out.println("surchargeWeightBreaks..."+s);
			for( String s : surchargeDesc)
				System.out.println("surchargeDesc..."+s);

			srcount				=	surchargeIds.length;
			for(int i=0;i<srcount;i++)
			{
				srwatbraks = surchargeWeightBreaks[i].split(",");
             for(int j=0;j<srwatbraks.length;j++)
//	mapList.put(surchargeIds[i].concat(srwatbraks[j])+i,(request.getParameter(surchargeIds[i].concat(srwatbraks[j])+i)!=null)?request.getParameter(surchargeIds[i].concat(srwatbraks[j])+i):"");
          System.out.println(surchargeIds[i].concat(srwatbraks[j]));
			}
			 //System.out.println("surchargeWeightBreaks--------"+surchargeWeightBreaks.length);
			 System.out.println("surchargeIds--------"+surchargeIds.length);
			 //System.out.println("surchargeIds[0]--------"+surchargeIds[1]);
			}
		//	 System.out.println(weightBreaks.length);
			if(request.getParameter("rows")!=null)
				noOfRows = Integer.parseInt(request.getParameter("rows"));
			System.out.println("noOfRows"+noOfRows);
	 
			if(request.getParameter("preRows")!=null)
				preRows = Integer.parseInt(request.getParameter("preRows"));

			System.out.println("preRowspreRowspreRow245624634s  "+preRows);
			totalRows   =	preRows  +	noOfRows;
			rateDOB		=	(RateDOB)session.getAttribute("rateDOB");
         System.out.println("rateDOB"+rateDOB);
			laneslist		=	(ArrayList)session.getAttribute("lanes");
			System.out.println("laneslist"+laneslist);

//System.out.println("preRowspreRowspreRow245624EDRQWE634s ASD "+lanes);
			String contextPath	=	request.getContextPath();

			int indexOf = contextPath.indexOf("/",1);
			if(indexOf>0)
			{
				contextPath = contextPath.substring(0,indexOf);
			}

			System.out.println("laneslist..."+laneslist);
			if(fromWhere == null && laneslist!=null )
			{	
				lanes = laneslist;
				preRows = laneslist.size()-1;
			}

			dateUtility				=	 new DateFormatter();
			effectiveFrom		=   dateUtility.convertToString(rateDOB.getEffectiveFrom()).replaceAll("/","-");

			if(lanes==null)
			{
					lanes		=	new ArrayList();
			
			for(int i=0;i<=preRows;i++)
			{
				System.out.println("origin.."+request.getParameter("origin"+i));

				flatRatesDOB = new FlatRatesDOB();

				flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
				flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
				flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
				flatRatesDOB.setFrequency	(request.getParameter("frequency"+i)		!= null ? request.getParameter("frequency"+i)	 : "");
				flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
				flatRatesDOB.setBasic			(request.getParameter("basic"+i)			!= null ? request.getParameter("basic"+i)		 : "");
				flatRatesDOB.setMin			(request.getParameter("min"+i)				!= null ? request.getParameter("min"+i)			 : "");
				flatRatesDOB.setFlat		(request.getParameter("flat"+i)				!= null ? request.getParameter("flat"+i)		 : "");
				flatRatesDOB.setNotes		(request.getParameter("notes"+i)			!= null ? request.getParameter("notes"+i)		 : "");
				flatRatesDOB.setExtNotes	(request.getParameter("extNotes"+i)			!= null ? request.getParameter("extNotes"+i)		 : "");//Modified by Mohan for Issue No.219976 on 28-10-2010
				flatRatesDOB.setDensityRatio(request.getParameter("densityRatio"+i)	!= null ? request.getParameter("densityRatio"+i) : "");
			 if(surchargeIds!=null){
			  for(int k=1;k<surchargeIds.length;k++){
				 if(surchargeIds[k].length()>1){
					 System.out.println("(k-1)+i-------------"+(k-1)+i);
                     System.out.println("srcrrr-----"+request.getParameter("surchargeCurrency"+(k-1)+i));
				  srwatbraks = surchargeWeightBreaks[k].split(",");
		mapList.put("surchargeCurrency"+(k-1)+i,(request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");	
		if((srwatbraks[0].equalsIgnoreCase("slab") && srwatbraks.length ==1) ||(srwatbraks[0].equalsIgnoreCase("both") && srwatbraks.length ==1))
					 {
			 for(int t=0;t<=11;t++){
				      if(srwatbraks[0].equalsIgnoreCase("slab")){//slab if begins
                       mapList.put("srslabbreaks"+i+(k-1)+t,(request.getParameter("srslabbreaks"+i+(k-1)+t)!=null?
				      request.getParameter("srslabbreaks"+i+(k-1)+t):"")); 
			           mapList.put("11/4/2010"+i+(k-1)+t,(request.getParameter("srslabvalues"+i+(k-1)+t)!=null?
				      request.getParameter("srslabvalues"+i+(k-1)+t) :"")); 
					  }//slab if ends
					  if(srwatbraks[0].equalsIgnoreCase("both")){//both if begins
                           dummybothname = (request.getParameter("srslabvalue"+i+(k-1)+t).length()!=0)?
							   "srslabvalue"+i+(k-1)+t:"srflatvalue"+i+(k-1)+t;
   						   System.out.println("dummybothname==========="+request.getParameter("srslabvalue"+i+(k-1)+t).length());
						   System.out.println("dummybothname==========="+dummybothname);
					mapList.put("srslabbreaks"+i+(k-1)+t,(request.getParameter("srslabbreaks"+i+(k-1)+t)!=null)?request.getParameter("srslabbreaks"+i+(k-1)+t):"");
					mapList.put(dummybothname,(request.getParameter(dummybothname)!= null)?
							   request.getParameter(dummybothname):"");
					
					  }//both if ends
			 }//end t for loop
					 }//slab and both if check end
					 else{
			for(int l=0;l<srwatbraks.length;l++){
mapList.put(surchargeIds[k]+srwatbraks[l]+i,(request.getParameter(surchargeIds[k]+srwatbraks[l]+i)!=null)?request.getParameter(surchargeIds[k]+srwatbraks[l]+i):"");
			}// l loop end
					 }//end slab if
				 }// inner if end
			  }//k loop end
			} 
	/*flatRatesDOB.setSurchargeCurrency(request.getParameter("surchargeCurrency"+i)	!= null ? request.getParameter("surchargeCurrency"+i) : "");//Govind
	mapList.put("surchargePercent",(request.getParameter("surchargePercent"+i)!=null)?request.getParameter("surchargePercent"+i):"");
	// appened i by VLAKSHMI for Issue 145908 on 20/11/2008
	mapList.put("fsbasic"+i,(request.getParameter("fsbasic"+i)!=null)?request.getParameter("fsbasic"+i):"");
	mapList.put("fsmin"+i,(request.getParameter("fsmin"+i)!=null)?request.getParameter("fsmin"+i):"");
	mapList.put("fskg"+i,(request.getParameter("fskg"+i)!=null)?request.getParameter("fskg"+i):"");
	mapList.put("ssmin"+i,(request.getParameter("ssmin"+i)!=null)?request.getParameter("ssmin"+i):"");
	mapList.put("ssbasic"+i,(request.getParameter("ssbasic"+i)!=null)?request.getParameter("ssbasic"+i):"");
	mapList.put("sskg"+i,(request.getParameter("sskg"+i)!=null)?request.getParameter("sskg"+i):"");
	mapList.put("bafmin"+i,(request.getParameter("bafmin"+i)!=null)?request.getParameter("bafmin"+i):"");
	mapList.put("bafm3"+i,(request.getParameter("bafm3"+i)!=null)?request.getParameter("bafm3"+i):"");
	mapList.put("cafmin"+i,(request.getParameter("cafmin"+i)!=null)?request.getParameter("cafmin"+i):"");
	mapList.put("caf%"+i,(request.getParameter("caf%"+i)!=null)?request.getParameter("caf%"+i):"");
	mapList.put("pssmin"+i,(request.getParameter("pssmin"+i)!=null)?request.getParameter("pssmin"+i):"");
	mapList.put("pssm3"+i,(request.getParameter("pssm3"+i)!=null)?request.getParameter("pssm3"+i):"");
	mapList.put("csf"+i,(request.getParameter("csfabsolute"+i)!=null)?request.getParameter("csfabsolute"+i):"");*/
       
		flatRatesDOB.setBreaksList(mapList);

				lanes.add(flatRatesDOB);
			}

		}

			if(rateDOB== null)
				rateDOB= new RateDOB();
			
			request.setAttribute("rateDOB",rateDOB);
%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<link rel="stylesheet" href="<%=contextPath%>/ESFoursoft_css.jsp">
</head>
<jsp:include page="ETDateValidation.jsp" >
	<jsp:param name="format" value="DD-MM-YY"/>
</jsp:include>
<script language="javascript">

		var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;

	function IsValidTime(field)
	{
		timeStr=field.value
		if(timeStr.length!=0)
		{
	//@@ Commented by subrahmanyam for the pbn id: 187501
	/*
		var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
		if(timeStr.length==4 && timeStr.indexOf(':')==-1)
		{
			timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
			field.value=timeStr;

		}


	*/
//@@ Added by subrahmanyam for the pbn id: 187501
			if((timeStr.length==5 && timeStr.indexOf(':')==-1)  || (timeStr.length==6 && timeStr.indexOf(':') !=-1))
				var	timePat	= /^(\d{1,3}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;	
			else
			var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
			
				
			if((timeStr.length==4 && timeStr.indexOf(':')==-1) || (timeStr.length==5 && timeStr.indexOf(':')!=-1))
			{
				if(timeStr.length==4 && timeStr.indexOf(':')==-1)
				{
					timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
					field.value=timeStr;

				}
			}
			else if((timeStr.length==5 && timeStr.indexOf(':')==-1) || (timeStr.length==6 && timeStr.indexOf(':')!=-1))
			{
				if(timeStr.length==5 && timeStr.indexOf(':')==-1)
				{
					timeStr = timeStr.substring(0,3)+':'+timeStr.substring(3,5);
					field.value=timeStr;

				}
			}
//@@ ended by subrahmanyam for the pbn id: 187501

			var	matchArray = timeStr.match(timePat);
			if (matchArray == null)
			{
				alert("Please enter in HH:MM format");
				field.focus();
			}
			else
			{
				hour = matchArray[1];
				minute = matchArray[2];
				second = matchArray[4];
				ampm = matchArray[6];
				if (second=="")	{ second = null; }
				if (ampm=="") {	ampm = null	}
				if (hour < 0  || hour >	999)	//@@Modified by Yuvraj for WPBN-22521
        {
          alert("Please enter correct Hours");
          field.focus();
				}
				else if	(minute<0 || minute	> 59) {
					alert ("Please enter correct Minutes.");
					field.focus();
				}
			}
		}
	}

	function increaseRows()
	{

		document.forms[0].action="<%=contextPath%>/etrans/BuyRatesAddFlatFlat.jsp?rows="+document.forms[0].rows.value+"&fromWhere=rows";
		document.forms[0].submit();
	}

	function stringFilter(input) 
	{
		s = input.value;
		input.value = s.toUpperCase();
	}
	function showLocation(obj)
	{
		
		myUrl=  '<%=contextPath%>/etrans/ETCLOVLocationIds.jsp?wheretoset='+obj+'&searchString='+document.getElementById(obj).value.toUpperCase()+'&shipmentMode=<%=rateDOB.getShipmentMode()%>&from=buyrates';
		var Win	   =  open(myUrl,'Doc',Features);
	}

	function showServiceLevel(obj)
	{
		myUrl="<%=contextPath%>/etrans/ETCLOVServiceLevelIds.jsp?wheretoset="+obj+"&shipmentMode="+<%=rateDOB.getShipmentMode()%>+"&searchString="+document.getElementById(obj).value+"&fromwhere=buyrates";
		Options	='width=400,height=400,resizable=0';
		Bars	='directories=0,location=0,menubar=no,status=0,titlebar=0,scrollbars=0';
		Features=Bars+','+Options;
		var Win	   =  open(myUrl,'Doc',Features);

	}

//Added BY Govind for the CR-219973 
   function getNumberCodeNegative(input)
{
	if(event.keyCode!=13)
	{
		if(input.value.substring("-").length>0)
		{
			if((event.keyCode < 48 && event.keyCode!=46) || event.keyCode > 57)
				return false;
		}
		else
		{
			if((event.keyCode < 48 && event.keyCode!=45 ) || event.keyCode > 57)
				return false;
		}
	}
	
	return true;
}
//Added BY Govind for the CR-219973 
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
//Added BY Govind for the CR-219973 
function srChargeSlabBreakValidation(obj,p,srcno,count)
	{
	var srChrgValue = obj.value;
     if(p==1) 
		{
		 if(Number(srChrgValue)>0){
			 alert("First Value Should be -ve and Second Value must be +ve of First Value");
			 obj.value="";
			 obj.focus();
		 }else{
				document.getElementsByName("srslabbreaks"+count+srcno+2)[0].value=srChrgValue.substring(1);
			 }
		}
		else if(p==2)
		{
			document.getElementsByName("srslabbreaks"+count+srcno+1)[0].value ='-'+srChrgValue;
		}
	}

//Added BY Govind for the CR-219973 
function srchargeSlabBreakCheck(obj,p,srcno,count)//p= column no of slab srcharge,srcno=surcharge rownumber,count =lane no)
	{
          if((p==1) && (obj.name == "srslabbreaks"+count+srcno+p))
            return  getNumberCodeNegative(obj);
		  else
			return  getNumberCode(obj)  ;

	}

//Added BY Govind for the CR-219973 
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
//Commeted by Govind for the cr-219973
	/*function getDotNumberCode(val)
	{
	   if(event.keyCode!=13)
		{	
			 if((event.keyCode <= 45 ) || (event.keyCode > 57) )
				 return false;	
		}
	}*/
//Added BY Govind for the CR-219973 
    function clearValue(obj)
	{
        obj.value ='';
	}


	function checkNumbers1(input)
	{
		
		if(trim(input.value).length>0)
		{
		//if(isNaN(trim(input.value)))
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

		function getCommaNumberCode(val)
	{
			 //alert("event.keyCode:::"+event.keyCode);
	   if(event.keyCode!=13)
		{	
		 if((event.keyCode <= 44 ) || (event.keyCode>44 && event.keyCode <49) || (event.keyCode >= 56) )
			{
			   return false;	
			}
		}
    
		if(!getDotNumberCode(val))
			return false;    
      
		appendWithComma(val);


		
		return true;
	}

	function appendWithComma(val)
	{
		if(val.value.length>0)
		{
			var temp1 = val.value.substr(0,(val.value.length));
			var temp2 = val.value.substr((val.value.length), val.value.length);
			if(val.value.length<15)
				val.value = temp1+','+temp2;
		}
	}
	function showDensityRatio(obj)
	{
	//@@Commented and Modified by Kameswari for LOV issue	
/*var myUrl=  '<%=contextPath%>/etrans/QMSDensityRatioLOV.jsp?fromWhere=BR&shipmentMode='+<%=rateDOB.getShipmentMode()%>+'&name='+obj+'&uom=<%=rateDOB.getUom() != null ? rateDOB.getUom() :  "" %>';*/
//var searchString = document.forms[0].densityRatio<%=int_count%>.value;
var searchString = document.getElementById(obj).value;
var myUrl=  '<%=contextPath%>/etrans/QMSDensityRatioLOV.jsp?fromWhere=BR&searchString='+searchString+'&shipmentMode='+<%=rateDOB.getShipmentMode()%>+'&name='+obj+'&uom=<%=rateDOB.getUom() != null ? rateDOB.getUom() :  "" %>';
//@@LOV Issue
		var Win	   =  open(myUrl,'Doc',Features);
	}  
function goBack()
	{
	  
	  document.forms[0].action="<%=contextPath%>/BuyRatesController?operation=back";
	  document.forms[0].submit();
      
	}
function onSubmit()
	{

	  document.forms[0].action="<%=contextPath%>/BuyRatesController?rateDtls=FlatFlat";
	  document.forms[0].submit();

	}
		  function numberFilter3(input)
	  {
			s = trim(input.value);


			//filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?";
			filteredValues = "1234567890.:";	
			var i;
			var returnString = "";
			var flag = 0;
			for (i = 0; i < s.length; i++)
			{
				var c = s.charAt(i);
				if ( filteredValues.indexOf(c) != -1 )
						returnString += c.toUpperCase();
				else
				{
					 alert("Please, enter valid density ratio");
					 input.value = '';
					 return false;
				}
			}
			input.value = trim(input.value);
			return true;
	}
	function getkeyCodes()
		{
			if(event.keyCode!=13)
			{
				if(event.keyCode>=45 && event.keyCode<=57)
				{
					return true;
				}
				return false;
			}
			return false;
		}
//Added by govind for the CR-219973


function surchargeCurrencyLOV(obj)
{	//alert(obj)
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
function specialCharFilter()
{
	if(event.keyCode == 33 || event.keyCode == 34 || event.keyCode == 39 || 
		event.keyCode == 59 || event.keyCode == 96 || event.keyCode == 126)
		return false;
	return true;
}

//Added by govind for the CR-219973

function upper(obj)
{ 
  obj.value = obj.value.toUpperCase();
}

function openLocationLov(input,count)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";

	var shMode	=	'<%=rateDOB.getShipmentMode()%>';
	
	formArray = input.name;		
	
	if(shMode=="Sea" || shMode=='2')
	{
		tabArray = 'PORT_ID';
		Url		="<%=contextPath%>/qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shMode+"&operation=PORTS&search= where PORT_ID LIKE '"+input.value+"~'";	
		Options	='width=600,height=750,resizable=yes';
		
	}
	else
	{
		tabArray = 'LOCATIONID';
		Url		="<%=contextPath%>/qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shMode+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+input.value+"~'";	
		Options	='width=750,height=750,resizable=yes';
	}
	
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}


</script>

<body>
<form  method='post' >
<!-- <input type = 'hidden' name='rateDtls' value='FlatFlat'> -->

		
		  <table border="0" width="100%"  cellspacing="0" cellpadding="4">
		  <tr><td bgcolor="#ffffff" valign="top">
		  <table border="0" width="100%"  cellspacing="1" cellpadding="4">
		  <tr class='formlabel'> 
              <td colspan='25'>
              Buy Rates Master - Add
              </td>
              </tr>

<%
	//System.out.println("(String)request.getAttribute(validateratedob)   "+(String)request.getAttribute("detailRateDob"))		  		;	 
	if(request.getAttribute("detailRateDob")!=null)
	{

%>
	<tr  class='formdata' >
	<td colspan='25' >
			<font  color=#ff0000> <%=(String)request.getAttribute("detailRateDob")%></font>
	</td>
	</tr>
<%
	}
%>
		  <tr class='formdata'> 
              <td <%if(!"2".equals(rateDOB.getShipmentMode())){%><%}%>>Weight Class: <br><select size="1" name="weightClass" class='select'>
               <option  value="G">General</option>
               
              </select>
              </td>
              <td>Currency ID:<br>
                <b><%=rateDOB.getCurrency() != null ? rateDOB.getCurrency() :  "" %></b>
              </td>
              <td >Carrier ID:<b><br>
                <%=rateDOB.getCarrierId() != null ? rateDOB.getCarrierId() :  "" %></b>
              </td>
              <td >UOM:<b><br>
                <%=rateDOB.getUom() != null ? rateDOB.getUom() :  "" %></b>
              </td>
<%
	 if("2".equals(rateDOB.getShipmentMode()))
	 {
%>			  <td >Console Type:<b><br>
                <%=rateDOB.getConsoleType() != null ? rateDOB.getConsoleType() :  "" %></b>
              </td>
			
<%
	 }
%>
				<td>Weight Break:<b><br>
                <%=rateDOB.getWeightBreak() != null ? rateDOB.getWeightBreak() :  "" %></b>
              </td>
              <td>Rate Type:<b><br>
                <%=rateDOB.getRateType() != null ? rateDOB.getRateType() :  "" %></b>
              </td>
			  <td >Effective&nbsp;From:&nbsp;<b><br>
			  <%=effectiveFrom !=null ? effectiveFrom:""	%></b></td>
              </tr>
			  </table>
		<table border="0" width="100%"  cellspacing="1" cellpadding="4">
          <tr class='formdata'> 
            <td colspan="40"><b>Please Enter Rates For Lanes: </b></td>
          </tr>
		   
       
<%  
	if(lanes!=null )
	{
	System.out.println("lanes----------"+lanes);
		int_count	=	0;

		while(int_count<lanes.size())
		{

				flatRatesDOB =(FlatRatesDOB)	lanes.get(int_count);
%>
          <tr class='formheader' > 
			 <td >&nbsp;</td>
			 <td  ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Loading<%}else{%>Origin<%}%></td>
			 <td  ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Discharge<%}else{%>Destination<%}%></td>
             <td  >Service Level</td>
			 <td >Frequency</td>
			 <td ><%  if("2".equals(rateDOB.getShipmentMode())){ %>Approximate Transit Days<%}else{%>Approximate Transit Time<%}%></td>
			 <td >Basic</td>
			 <td >Min</td>
			 <td >Flat</td>
			 <td >DensityRatio</td>
			<!-- Modified by Mohan for Issue No.219976 on 28-10-2010 -->
			 <td>Internal Notes</td>
			 <td>External Notes</td>
			 <td  >&nbsp;</td>
          </tr>










			<tr class='formdata'> 
			  <td > 
				  <input type='checkbox' name='<%=int_count%>' checked >
			  </td>
			  <td  > 
				  <!-- <input type="button" class='input' value="..." name="destinationIdLOV" onClick=showLocation('origin<%=int_count%>')> -->

				  <input type='text' class='text' name="origin<%=int_count%>" id="origin<%=int_count%>" value ='<%=flatRatesDOB.getOrigin() != null  ? flatRatesDOB.getOrigin() :"" %>' onblur="stringFilter(this);this.value=this.value.toUpperCase()" size="6"  >&nbsp;<input type="button" name="originbut<%=int_count%>" class='input' value="..." onClick="openLocationLov(document.forms[0].origin<%=int_count%>)">

			  </td>
			  <td  > 
				  <input type='text' class='text' name="destination<%=int_count%>"  onblur="stringFilter(this);this.value=this.value.toUpperCase()"   value='<%=flatRatesDOB.getDestination() !=null ? flatRatesDOB.getDestination():""%>' id="destination<%=int_count%>" size="6"  >&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV" onClick="openLocationLov(document.forms[0].destination<%=int_count%>)">
			  </td>
			  <td  > 
				  <input type='text' class='text' id="serviceLevel<%=int_count%>" value='<%=flatRatesDOB.getServiceLevel() !=null ? flatRatesDOB.getServiceLevel():""%>' onblur="stringFilter(this);this.value=this.value.toUpperCase()"  name="serviceLevel<%=int_count%>" size="10"  >&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV" onClick="showServiceLevel('serviceLevel<%=int_count%>')">
			  </td>

			  <td align ='center'>
<%
    if("2".equals(rateDOB.getShipmentMode()))
	{
%>	
			  <select size="1" name="frequency<%=int_count%>" class='select'>
                <!--Added by silpa.p on 05-May-11-->
				 <option  value="DAILY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("DAILY"))?"selected":""%>>Daily</option>
				 <option  value="WEEKLY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("WEEKLY"))?"selected":"" %>>Weekly</option>
				 <option  value="MONTHLY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("MONTHLY"))?"selected":"" %>>Monthly</option>
				 <option  value="FORTNIGHTLY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("FORTNIGHTLY"))?"selected":"" %>>Fortnightly </option><!--Fort Night--><!--@@Modified by Kameswari for the WPBN issue-62417-->
				 <option  value="EVERY 10 DAYS" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("EVERY 10 DAYS"))?"selected":"" %>>Every 10 Days</option>
               </select>

<%  }else
	{
%>
				  <input type='text' class='text' name="frequency<%=int_count%>" size="8" onkeypress='return getCommaNumberCode(this);'  value = '<%=flatRatesDOB.getFrequency() !=null ? flatRatesDOB.getFrequency() : ""%>'  value="" maxlength="15"  >
<%
	}
%>
              </td>
			  <td>
			  <input type='text' class='text' name="transitTime<%=int_count%>" size="5" value='<%=flatRatesDOB.getTransittime() != null ? flatRatesDOB.getTransittime():""%>'  <% if(!"2".equals(rateDOB.getShipmentMode())){%>onblur="return IsValidTime(this)"<%}else{%>onkeypress=" return getDotNumberCode(this)"   onBlur='checkNumbers1(this);' <%}%>  maxlength="15"  >
              </td>
			  <td  > <!--ADDED by Govind for the CR-219973 ----->
				  <input type='text' class='text' name="basic<%=int_count%>" size="5"  maxlength="10" value = '<%=flatRatesDOB.getBasic() != null ? flatRatesDOB.getBasic():""%>' id='basic<%=int_count%>' onkeypress='return  getDotNumberCode(this)' onBlur='checkNumbers1(this);' >
			  </td><!--Govind end-->
			  <td  > 
				  <input type='text' class='text' name="min<%=int_count%>" size="5"  maxlength="10" value = '<%=flatRatesDOB.getMin() != null ? flatRatesDOB.getMin():""%>' id='min<%=int_count%>' onkeypress='return  getDotNumberCode(this)' onBlur='checkNumbers1(this);' >
			  </td>
			  <td  > 
			<!-- modified by subrahmanyam for the id:216340 -->
				  <input type='text' class='text' name="flat<%=int_count%>" size="5" maxlength="10" value = '<%=flatRatesDOB.getFlat()!=null ? flatRatesDOB.getFlat():""%>' id='flat<%=int_count%>' onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
			  </td>
			   <td align="center"> 
						  <input type='text' class='text'name="densityRatio<%=int_count%>" onblur='checkNumbers1(this)' value='<%=flatRatesDOB.getDensityRatio()!=null ? flatRatesDOB.getDensityRatio() :"" %>' size="5"  maxlength=25 onpaste='return false;'><input type='button' class='input' value="..." name='densLOV' onClick='showDensityRatio("densityRatio<%=int_count%>")'>
			  </td>
			  <td  > 
			   <!--<input type='text' class='text' name="notes<%=int_count%>" size="5" value = '<%=flatRatesDOB.getNotes()!=null ? flatRatesDOB.getNotes() :"" %>' id='notes<%=int_count%>' maxlength='30'>-->
				<!-- Modified by Mohan for Issue No.219976 on 28-10-2010 -->
				  <input type='text' class='text' name="notes<%=int_count%>" size="5" value = '<%=flatRatesDOB.getNotes()!=null ? flatRatesDOB.getNotes() :"" %>' id='notes<%=int_count%>' maxlength='1000'>
			  </td>
			  <td  > 
				  <input type='text' class='text' name="extNotes<%=int_count%>" size="5" value = '<%=flatRatesDOB.getExtNotes()!=null ? flatRatesDOB.getExtNotes() :"" %>' id='extNotes<%=int_count%>' maxlength='1000'>
			  </td>

			 

			  <td >&nbsp;</td>
			  </tr>
			  <!--@@Added by Kameswari-->
		
		
	 <!--@@Added by Govind for creation of surcharge rows when we click >> button for the cr-219973-->
	    	 <%		 if(surchargeIds!=null)	{
			     for(int k=1;k<surchargeIds.length;k++){
				 if(surchargeIds[k].length()>1){
				  srwatbraks = surchargeWeightBreaks[k].split(",");%>
			<tr class="formdata" colspan ='11' >
			   <td width ='30%'> 
			  <input type='checkbox' name=<%=surchargeIds[k]%><%=int_count%> checked >
              <%=surchargeDesc[k]%></td>
              <td width ='20%'>Currency<br>
			  <INPUT TYPE="text" class='text' id="surchargeCurrency<%=k-1%><%=int_count%>" name="surchargeCurrency<%=k-1%><%=int_count%>" 
			  value='<%=flatRatesDOB.getBreaksList().get("surchargeCurrency"+(k-1)+int_count)!=null?flatRatesDOB.getBreaksList().get("surchargeCurrency"+(k-1)+int_count):""%>' size='5' onkeypress="return specialCharFilter()" 
			  onblur ='upper(this)'>
			  <input type='button' class='input'  name='b1' value='...' onclick='surchargeCurrencyLOV("surchargeCurrency<%=k-1%><%=int_count%>")'></td>
			  <%for(int l=0;l<srwatbraks.length;l++){//l loop
					  if(srwatbraks[l].equalsIgnoreCase("slab")){ //slab if begins%>
                   <td colspan='7'>
				<%for(int p=0;p<=11;p++){
					if(p==0){%>
              <input type=text  class='text' name='srslabbreaks<%=int_count%><%=k-1%><%=p%>'  value='MIN'  size=4 id=0 onkeypress='return getkeyCodes(this)' readonly>
			  <%}else{%>
           	  <input type=text  class='text' name='srslabbreaks<%=int_count%><%=k-1%><%=p%>'  value='<%=flatRatesDOB.getBreaksList().get("srslabbreaks"+int_count+(k-1)+p)!=null?flatRatesDOB.getBreaksList().get("srslabbreaks"+int_count+(k-1)+p):""%>'  size=4 id=0 onkeypress='return srchargeSlabBreakCheck(this,<%=p%>,<%=(k-1)%>,<%=int_count%>)' onblur = 'srChargeSlabBreakValidation(this,<%=p%>,<%=(k-1)%>,<%=int_count%>)'>
	           <% }//end p if
				}%> <br>
			      <%for( int q=0;q<=11;q++){%>
           	  <input type=text  class='text' name='srslabvalues<%=int_count%><%=k-1%><%=q%>'  value='<%=flatRatesDOB.getBreaksList().get("srslabvalues"+int_count+(k-1)+q)!=null?flatRatesDOB.getBreaksList().get("srslabvalues"+int_count+(k-1)+q):""%>'  size=4 id=0 onkeypress='return getDotNumberCode(this)' onBlur = 'checkNumbers1(this);'>
	           <% }%>
				 </td>
				 <td>Service&nbsp;Level<br> 
			  <input type='text' class='text' name="scservicelevel<%=int_count%>" size="5" value = 'SCH' id='scservicelevel<%=int_count%>' maxlength='30' readOnly> </td><td/><td/><td/></tr><!-- //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010  -->
			<%}/*slab if ends*/else if(srwatbraks[l].equalsIgnoreCase("both")){ //both if begins%>
			<td colspan='8'>
				<%for(int p=0;p<=11;p++){
					if(p==0){%>
				<input type=text  class='text' name='srslabbreaks<%=int_count%><%=k-1%><%=p%>'  value='MIN'  size=3 id=0 onkeypress='return getkeyCodes(this)' readonly>&nbsp;&nbsp;
					<%}else{%>
              <input type=text  class='text' name='srslabbreaks<%=int_count%><%=k-1%><%=p%>'  value='<%=flatRatesDOB.getBreaksList().get("srslabbreaks"+int_count+(k-1)+p)!=null?flatRatesDOB.getBreaksList().get("srslabbreaks"+int_count+(k-1)+p):""%>'  size=3 id=0 onkeypress='return srchargeSlabBreakCheck(this,<%=p%>,<%=(k-1)%>,<%=int_count%>)'onblur = 'srChargeSlabBreakValidation(this,<%=p%>,<%=(k-1)%>,<%=int_count%>)'>
			  &nbsp;&nbsp;
	        <%}/*inner if ends*/ }%> <br>
               <%for( int q=0;q<=11;q++){
			   if(q==0){%>
                <input type=text  class='text' name='srslabvalue<%=int_count%><%=k-1%><%=q%>'  value='<%=flatRatesDOB.getBreaksList().get("srslabvalue"+int_count+(k-1)+q)!=null?flatRatesDOB.getBreaksList().get("srslabvalue"+int_count+(k-1)+q):""%>'  size=1 id='srslabvalue<%=int_count%><%=k-1%><%=q%>'  onkeypress='return getDotNumberCode(this)' onBlur = 'checkNumbers1(this);'>
			   <%}else{%>
           	  <input type=text  class='textHighlight' name='srslabvalue<%=int_count%><%=k-1%><%=q%>'  value='<%=flatRatesDOB.getBreaksList().get("srslabvalue"+int_count+(k-1)+q)!=null?flatRatesDOB.getBreaksList().get("srslabvalue"+int_count+(k-1)+q):""%>'  size=1 id='srslabvalue<%=int_count%><%=k-1%><%=q%>' onKeyPress="clearValue(srflatvalue<%=int_count%><%=k-1%><%=q%>);return getDotNumberCode(this);"   onpaste='return false;' autocomplete='off' maxlength='10'><input type=text  class='text' name='srflatvalue<%=int_count%><%=k-1%><%=q%>'  
			  value='<%=flatRatesDOB.getBreaksList().get("srflatvalue"+int_count+(k-1)+q)!=null?flatRatesDOB.getBreaksList().get("srflatvalue"+int_count+(k-1)+q):""%>'  size=2 id='srflatvalue<%=int_count%><%=k-1%><%=q%>' onKeyPress="clearValue(srslabvalue<%=int_count%><%=k-1%><%=q%>);return getDotNumberCode(this);" onpaste='return false;' autocomplete='off'>
			  
	           <% }/*else end */}//for end%>
				 </td>
				 <td>Service&nbsp;Level<br> 
			  <input type='text' class='text' name="scservicelevel<%=int_count%>" size="5" value = 'SCH' id='scservicelevel<%=int_count%>' maxlength='30' readOnly><td/><td/> </tr> <!-- //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010  -->
						  
			<%}/*both if ends*/else{%>
			 <td width ='20%'><%=srwatbraks[l]%><br>
			  <input type='text' class='text' name=<%=surchargeIds[k]+srwatbraks[l]%><%=int_count%> size="5" 
			  value = '<%=flatRatesDOB.getBreaksList().get(surchargeIds[k]+srwatbraks[l]+int_count)!=null?flatRatesDOB.getBreaksList().get(surchargeIds[k]+srwatbraks[l]+int_count):""%>' 
			  id=<%=surchargeIds[k]+srwatbraks[l]%><%=int_count%> maxlength='30'></td>
			<%}//end else
				   } //end l loop 
				   if(!srwatbraks[0].equalsIgnoreCase("slab") && !srwatbraks[0].equalsIgnoreCase("both") ){%>
                    <td>Service&nbsp;Level<br> 
                    <!--  //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010  -->
			  <input type='text' class='text' name="scservicelevel<%=int_count%>" size="5" value = 'SCH' id='scservicelevel<%=int_count%>' maxlength='30' readOnly> </td><td/><td/><td/><td/><td/><td/><td/>  
			  <% if(srwatbraks[0].equalsIgnoreCase("FLAT") || srwatbraks[0].equalsIgnoreCase("LIST"))%> <td></td><td></td>
			   <%if( ! (srwatbraks[0].equalsIgnoreCase("FLAT") || srwatbraks[0].equalsIgnoreCase("BASIC") || srwatbraks[0].equalsIgnoreCase("MIN"))) %> <td></td><td></td>
				<%if (srwatbraks[0].equalsIgnoreCase("BASIC") && srwatbraks[1].equalsIgnoreCase("MIN") && ! srwatbraks[2].equalsIgnoreCase("FLAT") ) %> <td> </td>
				<%if (srwatbraks[0].equalsIgnoreCase("BASIC") && srwatbraks[1].equalsIgnoreCase("FLAT")) %> <td> </td>
				<%if (srwatbraks[0].equalsIgnoreCase("MIN") && srwatbraks[1].equalsIgnoreCase("FLAT")) %> <td> </td>
			 <%}  %> </tr> <% }// end if
			  }//end k loop
		}
		//End Of  Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
			 
			 
			 
			int_count++;	
		}//while loop end
	}
	
	  for(int i=0;i<noOfRows;i++)
		{%>

 			 <tr class='formheader'> 
			 <td >&nbsp;</td>
			 <td  ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Loading<%}else{%>Origin<%}%></td>
			 <td  ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Discharge<%}else{%>Destination<%}%></td>
             <td  >Service Level</td>
			 <td >Frequency</td>
			 <td ><%  if("2".equals(rateDOB.getShipmentMode())){ %>Approximate Transit Days<%}else{%>Approximate Transit Time<%}%></td>
			 <td >Basic</td>
			 <td >Min</td>
			 <td >Flat</td>
			 <td >DensityRatio</td>
			<!-- Modified by Mohan for Issue No.219976 on 28-10-2010 -->
			 <td >Internal Notes</td>
			 <td >External Notes</td>
			 <td >&nbsp;</td>
          </tr>
          <tr class='formdata'> 
		    <td  > 
              <input type='checkbox' name='<%=i+int_count%>' checked >
            </td>
            
			<td  > 
			  <!-- <input type="button" class='input' value="..." name="destinationIdLOV" onClick="showLocation('origin<%=i+int_count%>')"> -->
              <input type='text' class='text' onblur="stringFilter(this);this.value=this.value.toUpperCase()" name="origin<%=i+int_count%>" id="origin<%=i+int_count%>"  size="6"  >&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV" onClick="openLocationLov(document.forms[0].origin<%=i+int_count%>)">
            </td>
		    <td  > 
			  <!-- <input type="button" class='input' value="..." name="destinationIdLOV" onClick="showLocation('destination<%=i+int_count%>')"> -->
              <input type='text' class='text' name="destination<%=i+int_count%>" id="destination<%=i+int_count%>" size="6"  onblur="stringFilter(this);this.value=this.value.toUpperCase()" >&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV" onClick="openLocationLov(document.forms[0].destination<%=i+int_count%>)">
            </td>
			<td  > 
              <input type='text' class='text' id="serviceLevel<%=i+int_count%>" name="serviceLevel<%=i+int_count%>" size="10"  onblur="stringFilter(this);this.value=this.value.toUpperCase()">&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV" onClick="showServiceLevel('serviceLevel<%=i+int_count%>')">
            </td>
<!--			<td  width="105"> 
              <input type='text' class='text' name="effDate<%=i+int_count%>"  size="9"  onBlur="dtCheck(this);"  >&nbsp;<input type="button" class='input' value="..." name="date" onClick="newWindow('effDate<%=i+int_count%>','0','0','/ESupply/')">
            </td>-->
			<td  align='center' >
<%
    if("2".equals(rateDOB.getShipmentMode()))
	{
%>	
			  <select size="1" name="frequency<%=i+int_count%>" class='select'>
				 <option  value="DAILY">Daily</option><!--added by silpa.p on 5-04-11-->
				 <option  value="WEEKLY">Weekly</option>
				 <option  value="MONTHLY">Monthly</option>
				 <option  value="FORTNIGHTLY">Fortnightly</option><!--Fort Night--><!--@@Modified by Kameswari for the WPBN issue-62417-->
				 <option  value="EVERY 10 DAYS">Every 10 Days</option>
               </select>

<%  }else
	{
%>
              <input type='text' class='text' name="frequency<%=i+int_count%>" size="8"  onkeypress='return getCommaNumberCode(this);' value="" maxlength="15"  >
<%
	}
%>
            </td>
			<td  >
              <input type='text' class='text' name="transitTime<%=i+int_count%>" size="5" <% if(!"2".equals(rateDOB.getShipmentMode())){%>onblur="return IsValidTime(this)"<%}else{%>onkeypress=" return getDotNumberCode(this)"   onBlur='checkNumbers1(this);' <%}%> value=""  maxlength="15"  >
            </td>
			<td  > <!--ADDED by Govind for the CR-219973 ----->
				  <input type='text' class='text' name="basic<%=i+int_count%>" size="5"  maxlength="10" value ="" onkeypress='return  getDotNumberCode(this)' onBlur='checkNumbers1(this);' >
			  </td><!--Govind end-->
			<td  > 
           <!-- modified by subrahmanyam for the id:216340 -->
              <input type='text' class='text' name="min<%=i+int_count%>" size="5" maxlength="10" value=""  onkeypress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
            </td>
			<td  > 
              <input type='text' class='text' name="flat<%=i+int_count%>" size="5" maxlength="10" value=""  onkeypress='return     getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
            </td>
			 <td align="center"> 
						  <input type='text' class='text'name="densityRatio<%=i+int_count%>" onblur='checkNumbers1(this)'  size="5"  maxlength=25 onpaste='return false;'>
						  <input type='button' class='input' value="..." name='densLOV' onClick='showDensityRatio("densityRatio<%=i+int_count%>")'>
			  </td>
			<td  > 
              <!--<input type='text' class='text' name="notes<%=i+int_count%>" size="5" value="" maxlength='30' >-->
			 <!-- Modified by Mohan for Issue No.219976 on 28-10-2010 -->
              <input type='text' class='text' name="notes<%=i+int_count%>" size="5" value="" maxlength='1000' >
            </td>
			<td  > 
              <input type='text' class='text' name="extNotes<%=i+int_count%>" size="5" value="" maxlength='1000' >
            </td>
			<td >&nbsp;</td>
	   </tr>
	<!--@@Added by Govind for creation of surcharge rows for the cr-219973-->
	 <%    if(surchargeIds!=null)	 {
		      for(int k=1;k<surchargeIds.length;k++){
				 if(surchargeIds[k].length()>1){
				  srwatbraks = surchargeWeightBreaks[k].split(",");
				  %>
			<tr class="formdata" colspan ='11' >
			   <td width ='30%' > 
             <input type='checkbox' name=<%=surchargeIds[k]%><%=i+int_count%> checked >
              <%=surchargeDesc[k]%></td>
              <td>Currency<br>
			  <INPUT TYPE="text" class='text' id="surchargeCurrency<%=k-1%><%=i+int_count%>" value="" name="surchargeCurrency<%=k-1%><%=i+int_count%>" value="" size='5' onkeypress="return specialCharFilter()" 
			  onblur ='upper(this)'>
			  <input type='button' class='input'  name='b1' value='...' onclick='surchargeCurrencyLOV("surchargeCurrency<%=k-1%><%=i+int_count%>")'></td>
			 
			  <%for(int l=0;l<srwatbraks.length;l++){// l loop
			    if(srwatbraks[l].equalsIgnoreCase("slab")){ //slab if begins%>
			    <td colspan='7'>
				<%for(int p=0;p<=11;p++){
					if(p==0){%>
           	  <input type=text  class='text' name='srslabbreaks<%=i+int_count%><%=k-1%><%=p%>'  value='MIN'  size=4 id=0 onkeypress='return srchargeSlabBreakCheck(this,<%=p%>,<%=(k-1)%>,<%=i+int_count%>)' readonly>
			  <%}else{%>
			  <input type=text  class='text' name='srslabbreaks<%=i+int_count%><%=k-1%><%=p%>'  value=""  size=4 id=0 onkeypress='return srchargeSlabBreakCheck(this,<%=p%>,<%=(k-1)%>,<%=i+int_count%>)' onblur = 'srChargeSlabBreakValidation(this,<%=p%>,<%=(k-1)%>,<%=i+int_count%>)' >
	           <% } 
				}%> <br>
               <%for( int q=0;q<=11;q++){%>
           	  <input type=text  class='text' name='srslabvalues<%=i+int_count%><%=k-1%><%=q%>'  value="" size=4 id=0 
			  onkeypress='return getDotNumberCode(this)' onBlur = 'checkNumbers1(this);'>
	           <% }%>
				 </td>
				 <td>Service&nbsp;Level<br> 
			  <input type='text' class='text' name="scservicelevel<%=i+int_count%>" size="5" value = 'SCH' id='scservicelevel<%=int_count%>' maxlength='30' readOnly> </td><td/><td/><td/></tr> <!-- Kishore Podili -->
				<%}/*slab if ends */else if(srwatbraks[l].equalsIgnoreCase("both")){ //  slabboth if begins%>
				<td colspan='8'>
				<%for(int p=0;p<=11;p++){
					if(p==0){%>
              <input type=text  class='text' name='srslabbreaks<%=i+int_count%><%=k-1%><%=p%>'  value='MIN'  size=3 id=0 onkeypress='return srchargeSlabBreakCheck(this,<%=p%>,<%=(k-1)%>,<%=i+int_count%>)' readonly>&nbsp;
					<%}else{%>
              <input type=text  class='text' name='srslabbreaks<%=i+int_count%><%=k-1%><%=p%>'  value=""  size=3 id=0 onkeypress='return srchargeSlabBreakCheck(this,<%=p%>,<%=(k-1)%>,<%=i+int_count%>)' maxlength='10'
			  onblur = 'srChargeSlabBreakValidation(this,<%=p%>,<%=(k-1)%>,<%=i+int_count%>)'>&nbsp;&nbsp;&nbsp;
			  <%}}%> <br>
               <%for( int q=0;q<=11;q++){
				  if(q==0){%>
                <input type=text  class='text' name='srslabvalue<%=i+int_count%><%=k-1%><%=q%>'  size=3 id='srslabvalue<%=int_count%><%=k-1%><%=q%>'  onkeypress='return getDotNumberCode(this)' onBlur = 'checkNumbers1(this);'>
			   <%}else{%>
           	  <input type=text  class='textHighlight' name='srslabvalue<%=i+int_count%><%=k-1%><%=q%>'  value=""  size=1 id='srslabvalue<%=i+int_count%><%=k-1%><%=q%>' onKeyPress="clearValue(srflatvalue<%=i+int_count%><%=k-1%><%=q%>);return getDotNumberCode(this);"   onpaste='return false;' autocomplete='off' maxlength='10'><input type=text  class='text' name='srflatvalue<%=i+int_count%><%=k-1%><%=q%>'  value=""  size=2 id='srflatvalue<%=i+int_count%><%=k-1%><%=q%>' onKeyPress="clearValue(srslabvalue<%=i+int_count%><%=k-1%><%=q%>);return getDotNumberCode(this);" onpaste='return false;' autocomplete='off'>

	           <%}/*else end*/ }//for loop end%>
				 </td>
				 <td>Service&nbsp;Level<br> 
			  <input type='text' class='text' name="scservicelevel<%=i+int_count%>" size="5" value = 'SCH' id='scservicelevel<%=int_count%>' maxlength='30' readOnly><td/> <td/></tr><!-- //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
 -->
				
				
			  <%}/*slab bothif ends	*/else{%>
			  <td><%=srwatbraks[l]%><br>
			  <input type='text' class='text' name=<%=surchargeIds[k]+srwatbraks[l]%><%=i+int_count%> size="5" value ="" id=<%=surchargeIds[k]+srwatbraks[l]%><%=i+int_count%> maxlength='30'></td>
			 <%}//end else 
				  } //end l loop 
				   if(!srwatbraks[0].equalsIgnoreCase("slab") &&!srwatbraks[0].equalsIgnoreCase("both")){%>
                    <td>Service&nbsp;Level<br> 
                          <!-- //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 -->
			  <input type='text' class='text' name="scservicelevel<%=i+int_count%>" size="5" value = 'SCH' id='scservicelevel<%=int_count%>' maxlength='30' readOnly> </td><td/><td/><td/><td/><td/><td/><td/>
			 <% if(srwatbraks[0].equalsIgnoreCase("FLAT") || srwatbraks[0].equalsIgnoreCase("LIST"))%> <td></td><td></td>
			   <%if( ! (srwatbraks[0].equalsIgnoreCase("FLAT") || srwatbraks[0].equalsIgnoreCase("BASIC") || srwatbraks[0].equalsIgnoreCase("MIN"))) %> <td></td><td></td>
				<%if (srwatbraks[0].equalsIgnoreCase("BASIC") && srwatbraks[1].equalsIgnoreCase("MIN") && ! srwatbraks[2].equalsIgnoreCase("FLAT") ) %> <td> </td>
				<%if (srwatbraks[0].equalsIgnoreCase("BASIC") && srwatbraks[1].equalsIgnoreCase("FLAT")) %> <td> </td>
				<%if (srwatbraks[0].equalsIgnoreCase("MIN") && srwatbraks[1].equalsIgnoreCase("FLAT")) %> <td> </td>
		        </tr> <!-- //End Of Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 -->
			 <%}}// end if
			  }//end k loop
		}
			  
			  
			
			 
					
			preRows =i+int_count;		   
	}//i loop end

%>
			<input type=hidden name='preRows' value="<%=preRows%>">
		   <tr class='formdata'>
			<td ></td>
			<td  >&nbsp;</td>
			<td  >&nbsp;</td>
            <td  >&nbsp;</td>
			<td  >&nbsp;</td>
			<td >&nbsp;</td>
			<td >&nbsp;</td>
			<td >&nbsp;</td>
			<td >&nbsp;</td>
			<td ><input type=text name='rows' value=1 size=2 maxlength=2 class='text' onpaste='return false;' onkeypress='return getkeyCodes()'></td><td><input type=submit name='lane' value='>>' onclick='increaseRows()' class=input></td>
			<!-- Added By Kishroe Podili For UI Allignment-->
			<td >&nbsp;</td>
			<td >&nbsp;</td>
			<!-- End Of Kishore Podili -->

		   </tr>
        </table>
	    <table width="100%" cellpadding="4" cellspacing="1">
		  <tr valign="top" class='denotes'>
 <td>
<%	 if(!"2".equals(rateDOB.getShipmentMode()))
	{
%>
			  
				    <font color="#FF0000">*</font>Denotes Mandatory.<BR>
					Frequency - 1:Monday 2:Tuesday 3:Wednesday 4:Thursday 5:Friday 6:Saturday 7:Sunday . <br>Enter numbers(1-7) separated by commas

	
<%
	}
%>
		    </td> 
				<td align="right" width='200'>
					<input name=Reset type=reset value="<<Back" onclick="goBack()" class='input'> <input type='button' value="Submit" class='input' onclick='onSubmit();'>
		        </td>
		  </tr>
		</table>
		</td></tr></table>
<%		}catch(Exception e)
		{  
				e.printStackTrace();
				String errorMessage = " Problem while generating the rows" ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
				<jsp:forward page="ESupplyErrorPage.jsp" />
     
<% 
		}
%>

</form>    
</body>
</html>
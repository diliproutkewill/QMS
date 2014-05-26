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
					java.util.*,
					com.foursoft.esupply.common.bean.DateFormatter
					"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME = "BuyRatesAddSlabList.jsp ";
%>
<%		
    logger  = Logger.getLogger(FILE_NAME);
		RateDOB			rateDOB					=	null;
		FlatRatesDOB	flatRatesDOB			=	null;
		String[]		listValues				=	null;
		String[]		slabValues				=	null;
		String          dummybothname			=	null;//Govind
        String surchargeWeightBreaks[]			=	null;//Govind
        String surchargeDesc[]					=	null;//Govind
	    String surchargeIds[]					=	null;//Govind
	    String srchargeIds[]					=	null;//Govind
	    String srwatbraks[]						=	null;//Govind
		ArrayList		lanes					=	null; 
	   ArrayList		laneslist				=	null;
		int				noOfRows				=	0; 
		int				totalRows				=	0; 
		int				preRows					=	-1; 
		int				int_count				=	0;
		int				int_colspan				=	0;
		int				int_colspan1			=	0;
		//ArrayList		lanes					=	null; 
		String				effectiveFrom	=	"";
		DateFormatter		dateUtility		=	null;
		int            count                   =   0;
		HashMap       mapList                  = new HashMap();
		String[]				cafrates			    =	null;
		String[]				bafrates			    =	null;
		String[]				cssrates			    =	null;
		String[]				pssrates			    =	null;
		long l=	System.currentTimeMillis();
		//Added by Kishore Podili for the issue id:226791_Alignment on 15-Dec-2010 
                int srcountfortd = 0; 
%>
<%  
 try{

					String contextPath	=	request.getContextPath();
					
					int indexOf = contextPath.indexOf("/",1);
					if(indexOf>0)
					{
						contextPath = contextPath.substring(0,indexOf);
					}
					
					
					//lanes			=	new ArrayList();
					rateDOB			=	(RateDOB)session.getAttribute("rateDOB");

					//System.out.println("rateDOBrateDOBrateDOB "+rateDOB);
					listValues		=	(String[])session.getAttribute("listValues");
					surchargeWeightBreaks  = (String[])session.getAttribute("surchargeWeightBreaks");//Govind
					surchargeIds		   = (String[])session.getAttribute("surchargeIds");//Govind
					surchargeDesc		   = (String[])session.getAttribute("surchargeDesc");//Govind
	

			if(request.getParameter("preRows")!=null)
				preRows = Integer.parseInt(request.getParameter("preRows"));
		
			if(request.getParameter("rows")!=null)
				noOfRows = Integer.parseInt(request.getParameter("rows"));
			//System.out.println(dateUtility.convertToString(rateDOB.getEffectiveFrom())+"rateDOB.getEffectiveFrom()");
			dateUtility				=	 new DateFormatter();
			effectiveFrom		=   dateUtility.convertToString(rateDOB.getEffectiveFrom()).replaceAll("/","-");
			
	
	
	
	  lanes			=	new ArrayList();
	 
	  for(int i=0;i<=preRows;i++)
	   {
		  //logger.info("preRowspreRows11111 : "+preRows);
		flatRatesDOB = new FlatRatesDOB();

		flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
		flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
		flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
		flatRatesDOB.setFrequency	(request.getParameter("frequency"+i)		!= null ? request.getParameter("frequency"+i)	 : "");
		flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
		flatRatesDOB.setMin			(request.getParameter("min"+i)				!= null ? request.getParameter("min"+i)			 : "");
		flatRatesDOB.setListValues	(request.getParameterValues("list"+i)															 );
		//logger.info("xxxxxxxxxx"+request.getParameterValues("list"+i));
		flatRatesDOB.setNotes		(request.getParameter("overPivot"+i)			!= null ? request.getParameter("overPivot"+i) : "");
		flatRatesDOB.setDensityRatio(request.getParameter("densityRatio"+i)	!= null ? request.getParameter("densityRatio"+i) : "");
		flatRatesDOB.setNotes		(request.getParameter("notes"+i)			!= null ? request.getParameter("notes"+i)		 : "");
		//Modified by Mohan for Issue No.219976 on 28-10-2010		
		flatRatesDOB.setExtNotes		(request.getParameter("extNotes"+i)			!= null ? request.getParameter("extNotes"+i)		 : "");
	 /*Commented by Govind for the CR-219973 and the issue 223722 	mapList.put("surchargePercent",(request.getParameter("surchargePercent"+i)!=null)?request.getParameter("surchargePercent"+i):"");
		  // appened i,m by VLAKSHMI for Issue 145908 on 20/11/2008
	   mapList.put("fsbasic"+i,(request.getParameter("fsbasic"+i)!=null)?request.getParameter("fsbasic"+i):"");
	   mapList.put("fsmin"+i,(request.getParameter("fsmin"+i)!=null)?request.getParameter("fsmin"+i):"");
	   mapList.put("fskg"+i,(request.getParameter("fskg"+i)!=null)?request.getParameter("fskg"+i):"");
	   mapList.put("ssmin"+i,(request.getParameter("ssmin"+i)!=null)?request.getParameter("ssmin"+i):"");
	   mapList.put("ssbasic"+i,(request.getParameter("ssbasic"+i)!=null)?request.getParameter("ssbasic"+i):"");
	   mapList.put("sskg"+i,(request.getParameter("sskg"+i)!=null)?request.getParameter("sskg"+i):"");
	    cafrates   = request.getParameterValues("caflist"+i);
           bafrates   = request.getParameterValues("baflist"+i);
           cssrates   = request.getParameterValues("csslist"+i);
           pssrates   = request.getParameterValues("psslist"+i);
	  if(cafrates!=null)
            {
              for(int m=0;m<cafrates.length;m++)
              {
                 mapList.put("cafrates"+i+m,(cafrates[m]!=null)?cafrates[m]:"");
              } 
            }
             if(bafrates!=null)
            {
              for(int m=0;m<bafrates.length;m++)
              {

                 mapList.put("bafrates"+i+m,(bafrates[m]!=null)?bafrates[m]:"");
				 //logger.info("getBreaksList()222222222"+mapList.get("bafrates"+m));

              }
            }
            if(cssrates!=null)
            {
              for(int m=0;m<cafrates.length;m++)
              {
                mapList.put("cssrates"+i+m,(cssrates[m]!=null)?cssrates[m]:"");
              }
            }
             if(pssrates!=null)
            {
              for(int m=0;m<cafrates.length;m++)
              {
                 mapList.put("pssrates"+i+m,(pssrates[m]!=null)?pssrates[m]:"");
              }
            } 
	   mapList.put("csf",(request.getParameter("csfabsolute"+i)!=null)?request.getParameter("csfabsolute"+i):"");*/
        flatRatesDOB.setBreaksList(mapList);

		lanes.add(flatRatesDOB);
	  }
	
	if(listValues!=null )
		int_colspan	=   listValues.length;

	if(int_colspan>2 )
		int_colspan1 =8;
	else
		int_colspan1 =12;

			
			if(rateDOB== null)
				rateDOB= new RateDOB();
	request.setAttribute("rateDOB",rateDOB);
			totalRows   =	preRows  +	noOfRows;
%>
<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script>

		var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;
	
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
	
	/*function getDotNumberCode(val)
	{
	   if(event.keyCode!=13)
		{	

			 if((event.keyCode <= 45 ) || (event.keyCode > 57) )
				 return false;	
		}
	}*/

//Added by Govind for the CR-219973
    function clearValue(obj)
	{
		//alert(obj.value)
        obj.value ='';
	}

   
//Added by Govind for the CR-219973

function upper(obj)
{ 
  obj.value = obj.value.toUpperCase();
}

//Added by Govind for the CR-219973
function surchargeCurrencyLOV(obj)
{	alert(obj)
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
	myUrl=	'<%=contextPath%>/etrans/ETCCurrencyConversionAddLOV.jsp?fromWhere=surchargeCurr&toWhere='+obj+"&searchString="+document.getElementById(obj).value.toUpperCase();
		alert(myUrl)
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




	function increaseRows()
	{

		
		document.forms[0].action="<%=contextPath%>/etrans/BuyRatesAddList.jsp?rows="+document.forms[0].rows.value;
		document.forms[0].submit();
	}
	function showLocation(obj)
	{
		
		myUrl= '<%=contextPath%>/etrans/ETCLOVLocationIds.jsp?shipmentMode=<%=rateDOB.getShipmentMode()%>&wheretoset='+obj+'&searchString='+document.getElementById(obj).value.toUpperCase();
		var Win	   =  open(myUrl,'Doc',Features);
	}
	function showDensityRatio(obj)
	{
		//var searchString = document.forms[0].densityRatio<%=int_count%>.value;////@@Added by Kameswari for LOV issue	
	var searchString = document.getElementById(obj).value;
  	//@@Commented and Modified by Kameswari for LOV issue	
		/*myUrl=  '<%=contextPath%>/etrans/QMSDensityRatioLOV.jsp?fromWhere=BR&shipmentMode='+<%=rateDOB.getShipmentMode()%>+'&name='+obj+'&uom=<%=rateDOB.getUom() != null ? rateDOB.getUom() :  "" %>';*/
		myUrl=  '<%=contextPath%>/etrans/QMSDensityRatioLOV.jsp?fromWhere=BR&searchString='+searchString+'&shipmentMode='+<%=rateDOB.getShipmentMode()%>+'&name='+obj+'&uom=<%=rateDOB.getUom() != null ? rateDOB.getUom() :  "" %>';
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

	function getCommaNumberCode(val)
	{
	   if(event.keyCode!=13)
		{	
		 if((event.keyCode <= 44 ) || (event.keyCode>44 && event.keyCode <49) || (event.keyCode >= 56) )
		   return false;	
		}
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

		function IsValidTime(field)
	{
		timeStr=field.value
		if(timeStr.length!=0)
		{
			
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
//			if (hour < 0  || hour >	99)	//@@Modified by Yuvraj for WPBN-22521
	 if (hour < 0  || hour > 999)//@@ added by subrahmanyam for the pbn id: 187501
      {
        alert("Please enter correct Hours");
        field.focus();
			}
			else if	(minute<0 || minute	> 59) {
				alert ("Please enter correct Minutes.");
				field.focus();
			}
			else if	(second	!= null	&& (second < 0 || second > 59))
			{
				alert ("Please enter correct Seconds.");
				field.focus();
			}
			}
		}
	}
function goBack()
	{
	  document.forms[0].action="<%=contextPath%>/BuyRatesController?operation=back";
	  document.forms[0].submit();
      
	}
function onSubmit()
	{
	  document.forms[0].action="<%=contextPath%>/BuyRatesController?rateDtls=ListList";
	  document.forms[0].submit();

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
	function trim(input)
 { 
	while (input.substring(0,1) == ' ') 
		input = input.substring(1, input.length);

	while (input.substring(input.length-1,input.length) == ' ')
		input = input.substring(0, input.length-1);

   return input;
 } 
 	function getkeyCodes()
		{
			if(event.keyCode!=13)
			{
				if(event.keyCode>=48 && event.keyCode<=57)
				{
					return true;
				}
				return false;
			}
			return false;
		}

	function stringFilter(input) 
	{
		s = input.value;
		input.value = s.toUpperCase();
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
<form   method="post" >
  <table width="100%" border="0" cellspacing="0" cellpadding="4">
    <tr>
      <td bgcolor="#FFFFFF" valign="top">
	  <table border="0" width="100%"  cellspacing="1" cellpadding="4">
	  <tr class='formlabel'><td >Buy Rates Master - Add</td>
          </tr>
	</table>	 
<%
	//System.out.println("(String)request.getAttribute(validateratedob)   "+(String)request.getAttribute("detailRateDob"))		  		;		  			 
	if(request.getAttribute("detailRateDob")!=null)
	{

%>
<table border="0" width="100%"  cellspacing="1" cellpadding="4">
	<tr  class='formdata' >

	<td >
		<font  color=#ff0000><%=(String)request.getAttribute("detailRateDob")%></font>
	</td>
	</tr>
	</table>
<%
	}
%>


	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	  <tr class='formdata'> 
       <td >Weight Class: <br>
			  <select size="1" name="weightClass" class='select'>
				 <option  value="G">General</option>
				 <option  value="W">Weight Scale</option>
              </select>
       </td>
	   <td >Currency:&nbsp;<b><br>
        <%=rateDOB.getCurrency() != null ? rateDOB.getCurrency() :  "" %></b>
       </td>
	   <td >Carrier:<b><br>
         <%=rateDOB.getCarrierId() != null ? rateDOB.getCarrierId() :  "" %></b>
       </td>
		<td >UOM:<b><br>
         <%=rateDOB.getUom() != null ? rateDOB.getUom() :  "" %></b></td>
<%
	 if("2".equals(rateDOB.getShipmentMode()))
	{
%>			  <td >Console&nbsp;Type:&nbsp;<b><br>
                <%=rateDOB.getConsoleType() != null ? rateDOB.getConsoleType() :  "" %></b>
              </td>
			
<%
	}
%>
      <td >Weight&nbsp;Break:&nbsp;<b><br>
          <%=rateDOB.getWeightBreak() != null ? rateDOB.getWeightBreak() :  "" %></b></td>
		<td >Rate&nbsp;Type:&nbsp;<b><br>
			<%=rateDOB.getRateType() != null ? rateDOB.getRateType() :  "" %></b></td>
		<td >Effective&nbsp;From:&nbsp;<b><br>
			<%=effectiveFrom !=null ? effectiveFrom:""	%></b></td>
       </tr>
	   </table>
		  <table width="100%" border="0" cellspacing="0" cellpadding="0">
       <tr class='formdata'> 
         <td><b>Please Enter Rates For Lanes&nbsp;&nbsp;&nbsp;: </b></td>
       </tr>
	   </table>
	 <table border="0" width="100%"  cellspacing="1" cellpadding="4">
      
<%		
	if(lanes!=null)
	{
		int_count	=	0;
		while(int_count<lanes.size())			
		{
                   //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
			count = 0;
			srcountfortd=0;
			flatRatesDOB =(FlatRatesDOB)lanes.get(int_count);
		
               
		
%>
           <tr class='formheader'> 
            <td></td>
			<td ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Loading<%}else{%>Origin<%}%></td>
			<td ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Discharge<%}else{%>Destination<%}%></td>
            <td >Service Level</td>
			<td>Frequency</td>
			<td><%  if("2".equals(rateDOB.getShipmentMode())){ %>Approximate Transit Days<%}else{%>Approximate Transit Time<%}%></td>

	<%		if(listValues!=null )
			{
				
				for(int n=0;n<listValues.length;n++)
				{

					if(listValues[n]!=null && !"".equals(listValues[n].trim()))
					{
%>
						<td width="31"><%=listValues[n]%></td>
<%	count++;				
	}
				}
			}
%>
		<td >DensityRatio</td>
		<!-- //Modified by Mohan for Issue No.219976 on 28-10-2010 -->
		<td>Internal Notes</td>
		<td>External Notes</td>
          </tr>
		  <tr class='formdata'> 
			  <td> 
				 
				  <input type='checkbox' name='<%=int_count%>' checked >
			  </td>
		  <td> 
			  <!-- <input type="button" class='input' value="..." name="destinationIdLOV1" onClick="showLocation('origin<%=int_count%>')"> -->
              <input type='text' class='text' name="origin<%=int_count%>" value='<%=flatRatesDOB.getOrigin()%>' size="6" id='10'  onblur='this.value=this.value.toUpperCase()'>&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV1" onClick="openLocationLov(document.forms[0].origin<%=int_count%>)">
              </td>
		   <td> 
			  <!-- <input type="button" class='input' value="..." name="destinationIdLOV2" onClick=showLocation('destination<%=int_count%>')> -->
              <input type='text' class='text' name="destination<%=int_count%>" value='<%=flatRatesDOB.getDestination()%>' size="6" id='11'  onblur='this.value=this.value.toUpperCase()'>&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV2" onClick="openLocationLov(document.forms[0].destination<%=int_count%>)">
              </td>
			  <td > 
              <input type='text' class='text' name="serviceLevel<%=int_count%>" value='<%=flatRatesDOB.getServiceLevel()%>' size="5" id='12' onblur='this.value=this.value.toUpperCase()' >&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV3" onClick=showServiceLevel('serviceLevel<%=int_count%>')>
              </td>
			  <td align="center" width="74">
<%

	 if("2".equals(rateDOB.getShipmentMode()))
	{
%>	
			  <select size="1" name="frequency<%=int_count%>" class='select'>
				 <option  value="DAILY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("DAILY"))?"selected":"" %>>Daily</option><!--added by silpa.p on 5-04-11 -->
				 <option  value="WEEKLY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("WEEKLY"))?"selected":"" %>>Weekly</option>
				 <option  value="MONTHLY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("MONTHLY"))?"selected":"" %>>Monthly</option>
				 <option  value="FORTNIGHTLY" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("FORTNIGHTLY"))?"selected":"" %>> Fortnightly</option><!--Fort Night--><!--@@Modified by Kameswari for the WPBN issue-62417-->
				 <option  value="EVERY 10 DAYS" <%=(flatRatesDOB.getFrequency().equalsIgnoreCase("EVERY 10 DAYS"))?"selected":"" %>>Every 10 Days</option>
               </select>

<%  }else
	{
%>
				  <input type='text' class='text' name="frequency<%=int_count%>" size="8"  onkeypress='return getCommaNumberCode(this)' value = '<%=flatRatesDOB.getFrequency()%>'  value="" maxlength="15"  >
<%	}
%>
            
            </td>
			<td>
              <input type='text' class='text' name="transitTime<%=int_count%>" value = '<%=flatRatesDOB.getTransittime()%>' size="5"  maxlength="15" <% if(!"2".equals(rateDOB.getShipmentMode())){%>onblur="return IsValidTime(this)"<%}else{%>onkeypress=" return getDotNumberCode(this)"   onBlur='checkNumbers1(this);' <%}%> >
            </td>


			
<%		
			  if(listValues!=null)
				{
	             
					for(int j=0,k=0;j<listValues.length;j++)
					{
						if(listValues[j]!=null && !"".equals(listValues[j].trim()))
						{
%><!--@@Modified by VLAKSHMI for  the WPBN issue-143491-->
							<td align="center" width="31"> 
<!-- modified by subrahmanyam for the id:216340 -->
								<input type='text' class='text' name="list<%=int_count%>" onkeypress="return getDotNumberCode(this);" size="5" maxlength="10" value='<%=flatRatesDOB.getListValues()[k]%>' size="2" id='list<%=int_count%>' onBlur='checkNumbers1(this);'>
							</td>
														
<%							k++;
						}
					}
				}
%>
		<td align="center"> 
						  <input type='text' class='text'name="densityRatio<%=int_count%>"  value='<%=flatRatesDOB.getDensityRatio()!=null ? flatRatesDOB.getDensityRatio() :"" %>' size="5"  maxlength="15" onblur='checkNumbers1(this)' onpaste='return false;'><input type='button' class='input' value="..." name='densLOV' onClick='showDensityRatio("densityRatio<%=int_count%>")'>
 			  </td>
		  <!-- <td>  --> 
			  <td  > 
				 <!--<input type='text' class='text' name="notes<%=int_count%>" size="5" value = '<%=flatRatesDOB.getNotes()!=null ? flatRatesDOB.getNotes() :"" %>' id='notes<%=int_count%>' maxlength="30">-->
				 <!-- //Modified by Mohan for Issue No.219976 on 28-10-2010 -->
				  <input type='text' class='text' name="notes<%=int_count%>" size="5" value = '<%=flatRatesDOB.getNotes()!=null ? flatRatesDOB.getNotes() :"" %>' id='notes<%=int_count%>' maxlength="1000">
			  </td>
			  <td  > 
				  <input type='text' class='text' name="extNotes<%=int_count%>" size="5" value = '<%=flatRatesDOB.getExtNotes()!=null ? flatRatesDOB.getExtNotes() :"" %>' id='extNotes<%=int_count%>' maxlength="1000">
			  </td>

<!--               <input type='text' class='text' name="overPivot<%=int_count%>" value='<%=flatRatesDOB.getNotes()%>' size="5" id='112' > -->
         <!-- </td>  -->
		</tr>

	
		 <% 		if(surchargeIds!=null){//Added by Rakesh
			 for(int k=1;k<surchargeIds.length;k++){
				 if(surchargeIds[k].length()>1){
				  srwatbraks = surchargeWeightBreaks[k].split(",");
				  %>
			<tr class="formdata" colspan ='11' >
			   <td width ='30%'> 
             <input type='checkbox' name=<%=surchargeIds[k]%><%=int_count%> checked >
              <%=surchargeDesc[k]%></td>
              <td>Currency<br>
                    <!--Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 -->
			  <INPUT TYPE="text" class='text' id="surchargeCurrency<%=k-1%><%=int_count%>" 
			  name="surchargeCurrency<%=k-1%><%=int_count%>"  size='5' onkeypress="return specialCharFilter()" 
			  onblur ='upper(this)' value ='<%= (flatRatesDOB.getBreaksList().get("surchargeCurrency"+(k-1)+int_count))!=null ? (flatRatesDOB.getBreaksList().get("surchargeCurrency"+(k-1)+int_count)) :"" %>'>
			  <input type='button' class='input'  name='b1' value='...' onclick='surchargeCurrencyLOV("surchargeCurrency<%=k-1%><%=int_count%>")'></td>
     <%			//System.out.println("1-----> surchargeIds:"+surchargeIds.length+"	surchargeWeightBreaks:"+surchargeWeightBreaks.length+"		count"+count+"		srcountfortd"+srcountfortd); 
			for(int n=0;n<listValues.length;n++)
				{
					if(listValues[n]!=null && !"".equals(listValues[n].trim()))
					{%>
						<td><%=listValues[n]%><br>
                        <input type='text' class='text' name="srlistvalue<%=int_count%><%=(k-1)%><%=n%>" size="5" 
						value = '<%= (flatRatesDOB.getBreaksList().get("srlistvalue"+int_count+(k-1)+n))!=null ? (flatRatesDOB.getBreaksList().get("srlistvalue"+int_count+(k-1)+n)): "" %>' id='srlistvalue<%=int_count%><%=(k-1)%><%=n%>' maxlength='30'onkeypress='return getDotNumberCode(this)' onBlur = 'checkNumbers1(this);'></td>
					<%  srcountfortd++;
						} /*list value if end*/ 
					} // n loop end
					//System.out.println("1-1-----> surchargeIds:"+surchargeIds.length+"	surchargeWeightBreaks:"+surchargeWeightBreaks.length+"		count"+count+"		srcountfortd"+srcountfortd); 	
				//End of Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
                              %>
                   <td>Service&nbsp;Level<br> 
			  <input type='text' class='text' name="scservicelevel<%=int_count%>" size="5" value = 'SCH' id='scservicelevel<%=int_count%>' maxlength='30' readOnly> 
			  </td>
				<%
                       //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
					count = count + 9;
					if(count != srcountfortd){
					for(int td=0;td<count-srcountfortd-3;td++){%>
					<td></td>
					<%}}
                       //End Of Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
				 }//main if ends
			 } //K loop end
		}
			 %>
</tr>
			  
			 <% int_count++;
		}//while loop end
	}
       //count = 0;
		for(int i=0;i<noOfRows;i++)
		{
			 count = 0; //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 

%>
             <tr class='formheader'> 
            <td></td>
			<td ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Loading<%}else{%>Origin<%}%></td>
			<td ><%if("2".equals(rateDOB.getShipmentMode())){ %>Port Of Discharge<%}else{%>Destination<%}%></td>
            <td >Service Level</td>
			<td>Frequency</td>
			<td><%  if("2".equals(rateDOB.getShipmentMode())){ %>Approximate Transit Days<%}else{%>Approximate Transit Time<%}%></td>

	<%		if(listValues!=null )
			{
				
				for(int n=0;n<listValues.length;n++)
				{

					if(listValues[n]!=null && !"".equals(listValues[n].trim()))
					{
%>
						<td width="31"><%=listValues[n]%></td>
<%	count++;				
	}
				}
				//System.out.println("0-------> count"+count); //kish
			}
%>
		<td >DensityRatio</td>
		<!-- //Modified by Mohan for Issue No.219976 on 28-10-2010 -->
		<td>Internal Notes</td>
		<td>External Notes</td>
          </tr>
		  <tr class='formdata'> 
			  <td> 
              <input type='checkbox' name='<%=i+int_count%>' checked >
            </td>
		      <td> 
			  <!-- <input type="button" class='input' value="..." name="destinationIdLOV1" onClick="showLocation('origin<%=i+int_count%>')" > -->
              <input type='text' class='text' name="origin<%=i+int_count%>" size="6" id='10' onblur='this.value=this.value.toUpperCase()'>&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV1" onClick="openLocationLov(document.forms[0].origin<%=i+int_count%>)" >
              </td>
		   <td> 
			  <!-- <input type="button" class='input' value="..." name="destinationIdLOV2" onClick=showLocation('destination<%=i+int_count%>') > -->
              <input type='text' class='text' name="destination<%=i+int_count%>" size="6" id='11' onblur='this.value=this.value.toUpperCase()' >&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV2" onClick="openLocationLov(document.forms[0].destination<%=i+int_count%>)" >
              </td>
			  <td> 
              <input type='text' class='text' name="serviceLevel<%=i+int_count%>" size="5" id='12'onblur='this.value=this.value.toUpperCase()' >&nbsp;<input type="button" class='input' value="..." name="destinationIdLOV3" onClick=showServiceLevel('serviceLevel<%=i+int_count%>') >
              </td>
			  <td>
<%

		 if("2".equals(rateDOB.getShipmentMode()))
			{
%>	
			  <select size="1" name="frequency<%=i+int_count%>" class='select'>
				 <option  value="DAILY">Daily</option><!-- added by silap.p on 5-04-11 -->
				 <option  value="WEEKLY">Weekly</option>
				 <option  value="MONTHLY">Monthly</option>
				 <option  value="FORTNIGHTLY">Fortnightly</option><!--Fort Night--><!--@@Modified by Kameswari for the WPBN issue-62417-->
				 <option  value="EVERY 10 DAYS">Every 10 Days</option>
               </select>

<%  }else
	{
%>
				  <input type='text' class='text' name="frequency<%=i+int_count%>" size="8"  onkeypress='return getCommaNumberCode(this)' value = ''  value="" maxlength="15"  >
<%	}
%>
            </td>
			<td>
              <input type='text' class='text' name="transitTime<%=i+int_count%>"  <% if(!"2".equals(rateDOB.getShipmentMode())){%>onblur="return IsValidTime(this)"<%}else{%>onkeypress=" return getDotNumberCode(this)"   onBlur='checkNumbers1(this);' <%}%> size="5"  maxlength="15"  >
            </td>
			 <!--  <td> 
              <input type='text' class='text' name="min<%=i+int_count%>" onkeypress="return getDotNumberCode();" size="4" maxlength="5" >
              </td> -->
<%		
	//System.out.println("1-------> surchargeIds:"+surchargeIds.length+"	surchargeWeightBreaks:"+surchargeWeightBreaks.length+"		count"+count+"		srcountfortd"+srcountfortd); 
			  if(listValues!=null)
				{
					for(int j=0;j<listValues.length;j++)
					{
						if(listValues[j]!=null && !"".equals(listValues[j].trim()))
						{
%>
							<td> 
								<input type='text' class='text' name="list<%=i+int_count%>" onkeypress="return getDotNumberCode(this);" size="5" maxlength="10" onkeypress='return getDotNumberCode(this)' onBlur = 'checkNumbers1(this);' >
							</td>
<%						//count++;
						}
					}
				}
%>
		<td align="center"> 
						  <input type='text' class='text'name="densityRatio<%=i+int_count%>" onblur='checkNumbers1(this)' size="5"  maxlength=15  onpaste='return false;'><input type='button' class='input' value="..." name='densLOV' onClick='showDensityRatio("densityRatio<%=i+int_count%>")'  >
					  </td>
					  <td>  
              <!--<input type='text' class='text' name="notes<%=i+int_count%>" size="5" value=""  maxlength="30">-->
			 <!-- //Modified by Mohan for Issue No.219976 on 28-10-2010 -->
             <input type='text' class='text' name="notes<%=i+int_count%>" size="5" value=""  maxlength="1000">
            </td>
			  <td>  
             <input type='text' class='text' name="extNotes<%=i+int_count%>" size="5" value=""  maxlength="1000">
            </td>
<!-- 		 <td align="center" width="44"> 
               <input type='text' class='text' name="overPivot<%=i+int_count%>" size="5" id='112' > -->
			

		</tr>
		<% 
                  //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
			count = count+9; // 9 static td
		 //System.out.println("surchargeIds:"+surchargeIds.length+"	surchargeWeightBreaks:"+surchargeWeightBreaks.length+"		count"+count+"		srcountfortd"+srcountfortd); 
		 if(surchargeIds!=null){//Added by Rakesh
		for(int k=1;k<surchargeIds.length;k++){
				 if(surchargeIds[k].length()>1){
				  srwatbraks = surchargeWeightBreaks[k].split(",");
				   srcountfortd = srwatbraks.length+2; 
                    //End Of Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
				  %>
			<tr class="formdata" colspan ='11' >
			   <td width ='30%'> 
             <input type='checkbox' name=<%=surchargeIds[k]%><%=i+int_count%> checked >
              <%=surchargeDesc[k]%></td>
              <td>Currency<br>
			  <INPUT TYPE="text" class='text' id="surchargeCurrency<%=k-1%><%=i+int_count%>" name="surchargeCurrency<%=k-1%><%=i+int_count%>" value="" size='5' onkeypress="return specialCharFilter()" 
			  onblur ='upper(this)'>
			  <input type='button' class='input'  name='b1' value='...' onclick='surchargeCurrencyLOV("surchargeCurrency<%=k-1%><%=i+int_count%>")'></td>
     <%			for(int n=0;n<listValues.length;n++)
				{
					if(listValues[n]!=null && !"".equals(listValues[n].trim()))
					{%>
						<td><%=listValues[n]%><br>
                        <input type='text' class='text' name="srlistvalue<%=i+int_count%><%=(k-1)%><%=n%>" size="5" value = '' id='srlistvalue<%=i+int_count%><%=(k-1)%><%=n%>' maxlength='30'></td>
					<%} /*list value if end*/ 
                                         //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
					srcountfortd++;	//Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
					} // n loop end
					//System.out.println(" 2------> surchargeIds:"+surchargeIds.length+"	surchargeWeightBreaks:"+surchargeWeightBreaks.length+"		count"+count+"		srcountfortd"+srcountfortd); 
                                       //End OfKishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
					%>
                   <td>Service&nbsp;Level<br> 
			  <input type='text' class='text' name="scservicelevel<%=i+int_count%>" size="5" value = 'SCH' id='scservicelevel<%=i+int_count%>' maxlength='30' readOnly> 
			  </td>
				<%
                                   //Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
					if(count != srcountfortd){
					for(int td=0;td<count-srcountfortd;td++){%>
					<td></td>
					<%}}
                                   //End Of Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 
				 }//main if ends
			 } //K loop end
		}
			 %>
</tr>
			   
			  
			  <%preRows =i+int_count;	
		}// i loop end
%>
	<input type=hidden name='preRows' value="<%=preRows%>">		 
	</table>
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
		  <tr align=right>
		  
			
		    <td align=right> 
			   <input type=text name='rows' value=1 size=2 class=text maxlength=2 onpaste='return false;' onkeypress='return getkeyCodes()'><input type=button name='lane' value='>>' 						onclick='increaseRows()' class=input>
		    </td>
		   </tr>
		</table>
<%

	}catch(Exception e)
		{  
				e.printStackTrace();
				String errorMessage = " Problem while generating the rows" ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
	<jsp:forward page="/ESupplyErrorPage.jsp"/>
<% 
		}
%>    
     <table border="0" width="100%"  cellspacing="0" cellpadding="0">
          <tr> 
		  <td width="50%" align="right">
<%	 if(!"2".equals(rateDOB.getShipmentMode()))
	{
%>
            
              <p align="left"><font face="Verdana" size="1">Frequency -
              1:Monday 2:Tuesday 3:Wednesday 4:Thursday 5:Friday 6:Saturday 7:Sunday . <br>Enter numbers(1-7) separated by commas</font></p>

<%
	}
%>
            </td>            <td width="50%" align="right"><input name=Reset type=reset value=<<Back onclick="goBack()" class='input'><input type="button" value="Submit" onclick='onSubmit()' name="B6" class='input'>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>


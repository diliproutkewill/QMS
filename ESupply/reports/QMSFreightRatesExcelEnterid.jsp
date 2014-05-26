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
			com.qms.reports.java.QMSRatesReportDOB,
			org.apache.log4j.Logger,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue,
			com.foursoft.esupply.common.util.ESupplyDateUtility,
			com.foursoft.esupply.common.util.StringUtility"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
      private static Logger logger = null;
      private static final  String  FILE_NAME = "QMSFreightRatesExcelEnterid.jsp" ;
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility		= new ESupplyDateUtility();
    String              dateFormat				= loginbean.getUserPreferences().getDateFormat();

	ArrayList			list					=  null;
	QMSRatesReportDOB	reportDtlDOB			=  null;
	
	String customerId                          = null;
	String frmLocation                         = null;
	String toLocation						   = null;
	String serviceLevelId					   = null;
	
	try
	{
		eSupplyDateUtility.setPatternWithTime(dateFormat);
		reportDtlDOB = (QMSRatesReportDOB)request.getAttribute("freightReportDOB");
		if(reportDtlDOB!=null)
		{
			customerId = reportDtlDOB.getCustomerId();
			frmLocation = reportDtlDOB.getOrigin();
			toLocation = reportDtlDOB.getDestination();
			serviceLevelId = reportDtlDOB.getServiceLevelId();
			
		}

	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
    logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
	}
%>
<html>

<head>

<title>New Page 1</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<jsp:include page="../etrans/ETDateValidation.jsp">
<jsp:param name="format" value="<%=dateFormat%>"/>
</jsp:include>
<script>
function popUpWindow(input)
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var accessLevel	  =	'<%=loginbean.getAccessType()%>';
	var accsLvl		  = ('HO_TERMINAL' == accessLevel?'H':accessLevel);
	var shipmentMode  = '';
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;
	var locationid='';
	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
	Options='width=400,height=300,resizable=no';
if(input=="serviceLevel"){		Url='etrans/ETCLOVServiceLevelIds.jsp?searchString='+document.forms[0].serviceLevelId.value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>';
}
else if(input=="fromCountry")
	{
// commented by subrahmanyam for 186695 on 20/OCT/09
	//Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].fromCountry.value.toUpperCase()+'&whereClause='+input;
// added by subrahmanyam for 186695 on 20/oct/09 & 221329 20/oct/10
  if(document.forms[0].fromLocation.value!='')
	Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].fromLocation.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.value+'&locationId='+locationid+'&forWhat=FreightRate&whereClause='+input;
  else
	Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].fromCountry.value.toUpperCase()+'&whereClause='+input;

	}else if(input=="toCountry")
	{
// commented by subrahmanyam for 186695 on 20/OCT/09
	//Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].toCountry.value.toUpperCase()+'&whereClause='+input;
// added by subrahmanyam for 186695 on 20/oct/09 & 221329 20/oct/10
	if(document.forms[0].toLocation.value!='')
		Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].toLocation.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.value+'&locationId='+locationid+'&forWhat=FreightRate&whereClause='+input;
	else
		Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].toCountry.value.toUpperCase()+'&whereClause='+input;

	}
	else if(input == "salesPerson")//Added by Anil.k for Issue 236362 on 24Feb2011
	{
		var tabArray = 'EMPID';
		var formArray = 'salesPerson';
		var lovWhere	=	"";
		Url		="qms/ListOfValues.jsp?lovid=REPORTING_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SALESPERSON&search= where EMPID LIKE '"+document.forms[0].salesPerson.value+"~'";
		Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
		Options	='width=800,height=750,resizable=yes';
	}//Ended by Anil.k for Issue 236362 on 24Feb2011
	else
	//Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].toCountry.value.toUpperCase()+'&whereClause='+input;
		Url 	= 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].toLocation.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.value+'&locationId='+locationid+'&forWhat=FreightRate&whereClause='+input;


Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}
function openLocationLov(input)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	
	if(input=="Origin")
	{
		tabArray = 'LOCATIONID';
		formArray = 'fromLocation';	
		// commented by subrahmanyam for 186695 on 20/oct/09
		/*
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where countryid like  '"+document.forms[0].fromCountry.value.toUpperCase()+"~'"+" and LOCATIONID LIKE '"+document.forms[0].fromLocation.value+"~'";

		*/
		// added by subrahmanyam for 186695 on 20/oct/09	
		if(document.forms[0].shipmentMode.value==2)
		{
			tabArray = 'PORT_ID';
				Url		="qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=PORTS&search= WHERE COUNTRY_ID LIKE  '"+document.forms[0].fromCountry.value.toUpperCase()+"~'"+" AND PORT_ID LIKE '"+document.forms[0].fromLocation.value+"~'";


		}
		else
		{
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where countryid like  '"+document.forms[0].fromCountry.value.toUpperCase()+"~'"+" and LOCATIONID LIKE '"+document.forms[0].fromLocation.value+"~'";
		}
		// ended by subrahmanyam for 186695 on 20/oct/09
	}
	else if(input=="Dest")
	{
		tabArray = 'LOCATIONID';
		formArray = 'toLocation';
		// commented by subrahmanyam for 186695 on 20/oct/09
/*
      Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].toLocation.value+"~'";
*/
		// ADDED by subrahmanyam for 186695 on 20/oct/09
		if(document.forms[0].shipmentMode.value==2)
		{
			tabArray = 'PORT_ID';
				Url		="qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=PORTS&search= WHERE COUNTRY_ID LIKE  '"+document.forms[0].toCountry.value.toUpperCase()+"~'"+" AND PORT_ID LIKE '"+document.forms[0].toLocation.value+"~'";


		}
		else
		{
	      Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where countryid like '"+document.forms[0].toCountry.value.toUpperCase()+"~'"+" and  LOCATIONID LIKE '"+document.forms[0].toLocation.value+"~'";
		}
		// Ended by subrahmanyam for 186695 on 20/oct/09 
		
	}
	

	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=750,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}
/*function showCountryLOV()
{
	var locationId = document.forms[0].locationId.value;
		
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = document.forms[0].countryId.value;

	var Url='etrans/ETCLOVCountryIds.jsp?searchString=&whereClause=CSR&wheretoset=countryId&locationId="+locationId;

	var Win=open(Url,'Doc',Features);
}
*/
function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	
	//var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString2= (toSet=='fromLocation')?(document.forms[0].fromLocation.value):(document.forms[0].toLocation.value);
    
	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&Module=temptest&searchString=&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value;
	var Win=open(Url,'Doc',Features);
}


function openCustomerLov()
{
	var tabArray = 'CUSTOMERID';
	var formArray = 'customerId';
	var lovWhere	=	"";
	
	Url		="qms/ListOfValues.jsp?lovid=CUSTOMER_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTECUSTOMER&search= where CUSTOMERID LIKE '"+document.forms[0].customerId.value+"~'";

	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 }
function showCustomerIds()
{
	openCustomerLov();

}

function setTerminalIdValues(values,objname)
{
		var terminalId	=	document.forms[0].terminalId;
		terminalId.options.length	=	0;
		
		for(var i=0;i<values.length;i++)
	     {	
		   terminalId.options[i]=new Option(values[i].value,values[i].value);
           terminalId.options[i].selected=true;
		 }
}



function showServiceLevelLOV()
{   
				
	var searchString=document.forms[0].serviceLevelId.value.toUpperCase();
	var Url		=	"etrans/ETCLOVServiceLevelIds1.jsp?searchString="+searchString+"&shipmentMode="+document.forms[0].shipmentMode.options.value;	
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}



function checkForm()
{
  if(document.forms[0].fromDate.value.length==0)
  {
   alert("Please enter fromDate");
   document.forms[0].fromDate.focus();
   return false;
  }
  if(document.forms[0].toDate.value.length==0)
  {
   alert("Please enter toDate");
   document.forms[0].toDate.focus();
    return false;
  }
 /* 
 Commented By Kishore Podili 
  if(document.forms[0].salesPerson.value.length==0)//Added by Anil.k for Issue 236362 on 24Feb2011
  {
	alert("Please enter salesPerson");
	document.forms[0].salesPerson.focus();
    return false;
  }//Ended by Anil.k for Issue 236362 on 24Feb2011
  */
 return true;
}
//added by phani
function showConsole()
{

		var data="";
		var data1="";
		
	if(document.forms[0].shipmentMode.value=="2")
	{
		if(document.forms[0].weightBreak.value=='List')
		{
			data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();'><option value='FCL'>FCL</option></select>"
		}else
		{
			data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();'><option value='LCL'>LCL</option><option value='FCL'>FCL</option></select>"
		}
		data1="Console Type";
	     				
	}else if(document.forms[0].shipmentMode.value=="4")
	{
		 if(document.forms[0].weightBreak.value=='List')
		{
				data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();'><option value='FTL'>FTL</option></select>";
		}else
		{
				data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();'><option value='LTL'>LTL</option><option value='FTL'>FTL</option></select>";
		}

		data1="Console Type";
	}
 
	
	if( document.layers)
	{
		document.layers.console.document.write(data);
		document.layers.console.document.close();
		document.layers.consoleLable.document.write(data1);
		document.layers.consoleLable.document.close();
	}
	else
	{
		if(document.all)
		{
			console.innerHTML = data;
			consoleLable.innerHTML = data1;
		}
	}
	
	
}
function showWtBreaks()
{
		document.getElementById("wtBreak").innerHTML="<select name='weightBreak' class='select' size=1 onchange='showConsole();chage();'><option value='Flat'>Flat</option><option value='Slab'>Slab</option><option value='List'>List</option></select>";
}

function changeConsole()
{
   
   var console=document.forms[0].consoleType.options[document.forms[0].consoleType.selectedIndex].value;
   if(console=='LCL' || console=='LTL')
	{
	document.getElementById("wtBreak").innerHTML="<select name='weightBreak' class='select' size=1 onchange='showConsole();chage()'>						<option value='Flat'>Flat</option><option value='Slab'>Slab</option></select>";
	}
	else if(console=='FCL' || console=='FTL')
    {
	document.getElementById("wtBreak").innerHTML="<select name='weightBreak' class='select' size=1 onchange='showConsole();chage();'>						<option value='List'>List</option></select>";
	}
}

function chage()
{
	
 
 var selectedIndex=document.forms[0].weightBreak.selectedIndex;
 var selectedvalue=document.forms[0].weightBreak.options[selectedIndex].value;
  if(selectedvalue=='Flat')
 {
  document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Flat'>Flat</option></select>";
 }
 else if(selectedvalue=='Slab') 
 {
  document.getElementById("rateType").innerHTML=
  "<select name='rateType' class='select' size=1><option value='Slab'>Slab</option><option value='Flat'>Flat</option><option value='Both'>Both</option></select>";
 }
 else
 {
	if(document.forms[0].shipmentMode.value=="Air" )
		  document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Pivot'>Pivot</op tion></select>";
	else
		  document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Flat'>Flat</op tion></select>";

     showConsole();


 }
}

//ends phani
</script>
</head> 

<body onLoad="showWtBreaks();chage();showConsole()" >

<form method="post" action='QMSRatesReportController' onSubmit="return checkForm();">
  
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"></td>
			  </tr>
	 </table>
			<table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
              
<%
		StringBuffer	errors	=	(StringBuffer)request.getAttribute("errors");
		if(errors!=null)
		{
			String	errorMessages	=	errors.toString();
%>
			<tr color="#FFFFFF">
				<td colspan="8">
					<font face="Verdana" size="2" color='red'><b>The form has not been submitted because of the following error(s):</b><br><br>
					<%=errorMessages%></font>
				</td>
			</tr>
<%}%>			
              <tr class="formheader" bgcolor="#FFFFFF" vAlign="top">
			  <table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="6"><table width="100%" border="0" ><tr class='formlabel'><td>
				Freight Rate Details</td><td align='right'></td></tr></table></td>
               
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td colspan='2'>From Date (<%=dateFormat%>):<font color="#FF0000">*</font><br>
                  <input class="text" name="fromDate" size="13" onBlur='dtCheck(this)'>
                  <input class="input" name="button" onClick="newWindow('fromDate','0','0','')" type="button" value="..."></td>
                <td>To Date (<%=dateFormat%>):<font color="#FF0000">*</font><br>
                  <input class="text" name="toDate" size="13" onBlur='dtCheck(this)'>
                  <input class="input" name="button" onClick="newWindow('toDate','0','0','')" type="button" value="...">
                </td>
                <td>Customer ID:<br>

				   <input class="text" name="customerId" size="13" onblur='this.value=this.value.toUpperCase()' value ='<%=StringUtility.noNull(customerId)%>'> 

                  <input class="input" name="button9" type="button" value="..." onclick='showCustomerIds()'>

                </td>
				  <td>Service Level ID:<br>			
                   <input class="text" name="serviceLevelId" size="13" onblur='this.value=this.value.toUpperCase()' value='<%=StringUtility.noNull(serviceLevelId)%>'>			 
                  <input class="input" name="button6" onclick="popUpWindow('serviceLevel')"type="button" value="...">
                  &nbsp;</td>
                  
                </td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td colspan='2'>From Location:<br>
               <input class="text" name="fromLocation" size="13" onblur='this.value=this.value.toUpperCase()' value='<%=StringUtility.noNull(frmLocation)%>'> 				
                  <input class="input" name="button4" onclick="openLocationLov('Origin')" type="button" value="...">
                </td>
				 <td>From Country :<br><!-- ADDED BY PHANI SEKHAR FOR WPBN 172935 ON 20090608 -->
				 <input class="text" name="fromCountry" size="13" onblur='this.value=this.value.toUpperCase()' value='<%=StringUtility.noNull(serviceLevelId)%>'>			 
                  <input class="input" name="button6" onclick="popUpWindow('fromCountry')" type="button" value="...">
                  &nbsp;
				</td>

                <td>To Location:<br>
                  <input class="text" name="toLocation" size="13" onblur='this.value=this.value.toUpperCase()' value ='<%=StringUtility.noNull(toLocation)%>'>                 
				  <input class="input" name="button5" onclick="openLocationLov('Dest')" type="button" value="...">
                </td>               
               
				 <td>To Country :<br><!-- ADDED BY PHANI SEKHAR FOR WPBN 172935 ON 20090608 -->
				 <input class="text" name="toCountry" size="13" onblur='this.value=this.value.toUpperCase()' value='<%=StringUtility.noNull(serviceLevelId)%>'>			 
                  <input class="input" name="button6" onclick="popUpWindow('toCountry')" type="button" value="...">
                  &nbsp;
				</td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td> Mode:<br>
                  <select size="1" name="shipmentMode" class='select' onchange="showWtBreaks();showConsole();chage();">
                    <option value='1' selected>Air</option>
                    <option value='2'>Sea</option>
                    <option value='4'>Truck</option>				    
				  </select></td><td>
				  <!-- Added by Anil.k for the issue 236362 on 24Feb2011 -->
				Sales Person:<br> <!-- <font color="#FF0000">*</font><br>    Commented By Kishore Podili-->
				<input class="text" name="salesPerson" size="13" onblur='this.value=this.value.toUpperCase()' value=''>			 
                <input class="input" name="salesPersonBtn" onclick="popUpWindow('salesPerson')" type="button" value="...">
                  &nbsp;
				<!-- Ended by Anil.k for the issue 236362 on 24Feb2011 -->
                </td>
				<td><span id='consoleLable' style='position:relative;'></span><!--  <br> -->
					<br>
				 <span id='console' style='position:relative;'></span> 

			</td>
				
                 <td width="209"> Weight Break:<font  color=#ff0000>*</FONT><br>
				
				<span id="wtBreak"></span>
				
			</td>
            <td width="169" >
				Rate Type:<font  color=#ff0000>*</FONT><br><span id="rateType"></span>
				</td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td colspan="5" > Output Format <input name="format" type="radio" checked value="Excel">EXCEL
                  Sheet&nbsp;
                </td>
              </tr>
              <tr class="text" vAlign="top">
                <td colspan="3" ><font color="#FF0000">*</font>
                            Denotes Mandatory</td>
                <td colspan="2" align='right'>
                  <input class="input" type="submit" value="Next &gt;&gt;">
				 <input type="hidden" name='Operation' value="FreightRates">
				  <input type="hidden" name='subOperation' value="getfreightDetails">
                </td>
              </tr>
             
          </table>
</form>

</body>

</html>

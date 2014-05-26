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
			com.qms.reports.java.ReportDetailsDOB,
			org.apache.log4j.Logger,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue,
			com.foursoft.esupply.common.util.ESupplyDateUtility"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
      private static Logger logger = null;
      private static final  String  FILE_NAME = "QMSYieldReportPreInfo.jsp" ;
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility		= new ESupplyDateUtility();
    String              dateFormat				= loginbean.getUserPreferences().getDateFormat();

	ArrayList			list					=  null;
	ReportDetailsDOB	reportDtlDOB			=  null;

	try
	{
		eSupplyDateUtility.setPatternWithTime(dateFormat);
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
	var searchString2= (toSet=='fromLocation')?(document.forms[0].fromCountry.value):(document.forms[0].toCountry.value);
    
	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString=&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value;
	var Win=open(Url,'Doc',Features);
}

function showSalesCodeLOV()
{
	//@@Modified by Kameswari for the WPBN issue-79047
	var terminalIds = document.forms[0].terminalId;
	var ids="";
	
			for(m=0;m<terminalIds.options.length;m++)
			{
				 
				 if(ids==""&&terminalIds.options[m].selected)
					 ids=terminalIds.options[m].value;
				 else if(ids!=""&&terminalIds.options[m].selected)
                    ids=ids+","+terminalIds.options[m].value;

			}	
//@@ Added by subrahmanyam  for the pbn id:220125 on 07-oct-10
			if (ids=="")
				ids='<%=loginbean.getTerminalId()%>';
//@@ Ended by subrahmanyam  for the pbn id:220125 on 07-oct-10
   // url="ESACUserIdsLOV.jsp?fromWhat=report&selection=multiple&terminalIds="+ids;
	url='<%=request.getContextPath()%>/etrans/ETCLOVMultipleSalesPerson.jsp?&wheretoset=salesPersonCode+&terminalId='+ids;
	Options='width=700,height=300,resizable=no';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(url,'Doc',Features);
}
function showSalesPersonIds(values)
{  
	var salesPersonId   =	document.forms[0].salesPersonCode;

	salesPersonId.options.length	=	0;

	for(var i=0;i<values.length;i++)
	{
		salesPersonId.options[i]=new Option(values[i].value.substring(0,values[i].value.indexOf("[")),values[i].value.substring(0,values[i].value.indexOf("[")),true,true);
	}

}
//@@ Added by Subrahmanyam for the WPBN ISSUE: 150461 ON 19/12/2008
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
//@@ Ended by Subrahmanyam for the WPBN ISSUE: 150461 ON 19/12/2008
function showCustomerIds()
{openCustomerLov();//@@ Added by subrahmanyam for the WPBN ISSUE:150461 on 19/12/2008
//@@ Commented by subrahmanyam for the WPBN issue: 150461 on 19/12/2008		
		/*var customerType="R";

		var	URL			=	'etrans/ETCLOVMultipleCustomers.jsp?operation=View';
		var	bars		=	'directories = no, status = no, location = no, menubar = no, titlebar = no';
		var	options		=	'scrollbars = yes, width = 700, height = 300, resizable = yes';
		var	features	=	bars + ' ' + options;
		var	win			=	open(URL,'Doc',features);	*/
}

function showterminalIdLOV()
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var name		=	"terminalId";
	var searchStr	=	document.getElementById(name).value;
 	//var fromWhere	=	"sellchargesenterid";
	var fromWhere	=	"terminal";
		//var Url			= "QMSLOVAllLevelTerminalIds.jsp?Operation=View&searchString=&name="+name+"&selection=multiple&fromWhere="+fromWhere;
		var Url='etrans/QMSLOVAllLevelTerminalIds2.jsp?salesPersonCode='+name+'&wheretoset='+name+'&terminalId='+terminalId;
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=700,height=300,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
}
function setTerminalIdValues(values,objname)
{
		var terminalId	=	document.forms[0].terminalId;
		terminalId.options.length	=	0;
		//alert(values.length);
		for(var i=0;i<values.length;i++)
	     {	
		   terminalId.options[i]=new Option(values[i].value,values[i].value);
           terminalId.options[i].selected=true;
		 }
}
function setSalesPersonCode(values)
{
    
	 var salesPersonCode	=	document.forms[0].salesPersonCode;
	 salesPersonCode.options.length	=	0;
	 var index=0;
	 for(var i=0;i<values.options.length;i++)
	  {	
	    
		if(values.options[i].selected)
		{
		salesPersonCode.options[index]=new Option(values.options[i].value,values.options[i].value);
        salesPersonCode.options[index].selected=true;
	    index++;
		}
	  } 
}
function showCuctomerIds(values)
{  
	
	var customerId	=	document.forms[0].customerId;
	customerId.options.length	=	0;
	for(var i=0;i<values.length;i++)
	{
      customerId.options[i]=new Option(values[i].value,values[i].value);
	  customerId.options[i].selected=true;
	}
	
}

function showCountryLOV(toSet)
{
	var locationId;
	
	if(toSet=='fromCountry')
	{
		locationId = document.forms[0].fromLocation.value;
	}
	else if(toSet=='toCountry')
	{
		locationId = document.forms[0].toLocation.value;
    }
		
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='fromCountry')?(document.forms[0].fromCountry.value):(document.forms[0].toCountry.value);

	var Url="etrans/ETCLOVCountryIds1.jsp?searchString=&whereClause=CSR&wheretoset="+toSet+"&locationId="+locationId+"&shipmentMode="+document.forms[0].shipmentMode.value;

	var Win=open(Url,'Doc',Features);
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


function showLocationValues(obj,where)
{
	
	
	var data="";
	document.getElementById(where).options.length=0;
	for( i=0;i<obj.length;i++)
	{
		if(where=='QuoteId')
    		   temp=obj[i].value;
		else
        { 
			firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		if(where=="serviceLevelId")
			lastIndex	=	firstTemp.indexOf(' [');	
		else
			lastIndex	=	firstTemp.indexOf('[');
 //@@ Commented & Added by subrahmanyam for the pbn id: 212190 on 22-Jul-10				
		//firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		firstTemp	=	firstTemp.substring(0,lastIndex);
    //@@ Ended by subrahmanyam for the pbn id: 212190 on 22-Jul-10			
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
			if(where=="fromCountry" || where=="toCountry")
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
		if(where=="fromCountry" || where=="toCountry")
		{
			document.getElementById(where).options[i]=new Option(temp1,temp1);  
		}else
		{
			document.getElementById(where).options[i]=new Option(temp,temp);
		}  
	    document.getElementById(where).options[i].selected=true;  
	 }
	 
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
 return true;
}

</script>
</head> 

<body>

<form method="post" action='QMSReportController' onSubmit="return checkForm();">
  
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				YieldReport</td><td align='right'><!-- Screen Id goes here --></td></tr></table></td>
			  </tr>
	 </table>
			<table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
              <tr class="formdata" vAlign="top">
                <td colspan="2" >Terminal Id: <b><%=loginbean.getTerminalId()%></b></td>
                <td>User:&nbsp;<b><%=loginbean.getUserId()%></b></td>
                <td colspan="2" >Date:<b><%=eSupplyDateUtility.getCurrentDateString(dateFormat)%></b></td>
              </tr>
              <tr class="formheader" vAlign="top">
                <td colspan="5" >Sales Person Details</td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <!-- <td>Country ID:<br>
                  <input class="text" name="countryId" size="13" onblur='this.value=this.value.toUpperCase()'>
                  <input class="input" name="button2" onclick="showCountryLOV()" type="button" value="...">
                </td> -->
                <td>Terminal ID:<br>
				  <select name='terminalId'  class='select' multiple  size='0'  onKeyPress	=""></select>	
                  <!-- <input class="text" name="terminalId" size="13" onblur='this.value=this.value.toUpperCase()'> -->
                  <input class="input" name="button3" onclick="showterminalIdLOV()" type="button" value="...">
                </td>
                <td colspan="2" >Sales Person IDs:<br>
				  <select name='salesPersonCode' multiple class='select'  size='0'  onKeyPress	=""></select>
                  <!-- <input class="text" name="salesPersonCode" size="13" onblur='this.value=this.value.toUpperCase()'> -->
                  <input class="input" name="button1" onclick="showSalesCodeLOV()" type="button" value="..."></td>
				  <td></td>
              </tr>
              <tr class="formheader" vAlign="top">
                <td colspan="5" >Activity Details</td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td>From Date (<%=dateFormat%>):<font color="#FF0000">*</font><br>
                  <input class="text" name="fromDate" size="13" onBlur='dtCheck(this)'>
                  <input class="input" name="button" onClick="newWindow('fromDate','0','0','')" type="button" value="..."></td>
                <td>To Date (<%=dateFormat%>):<font color="#FF0000">*</font><br>
                  <input class="text" name="toDate" size="13" onBlur='dtCheck(this)'>
                  <input class="input" name="button" onClick="newWindow('toDate','0','0','')" type="button" value="...">
                </td>
                <td>Customer ID:<br>
<!-- @@ Commented by subrahmanyam for the WPBN issue:150461 on 19/12/2008 -->
                  <!-- <select name='customerId' multiple class='select'  size='0'  onKeyPress	=""></select> -->
<!-- @@ Added by subrahmanyam for the WPBN issue:150461 on 19/12/2008 -->
				   <input class="text" name="customerId" size="13" onblur='this.value=this.value.toUpperCase()'> 

                  <input class="input" name="button9" type="button" value="..." onclick='showCustomerIds()'>

                </td>
				 <td>Quote Status:<br>
				  <input type='checkbox' name='quoteStatus' value='QUE'>Queued <br>
				  <input type='checkbox' name='quoteStatus' value='NAC'>Negative <br>
				  <input type='checkbox' name='quoteStatus' value='ACC'>Positive <br>
				  <input type='checkbox' name='quoteStatus' value='PEN'>Pending <br>
				  <input type='checkbox' name='quoteStatus' value='GEN'>Generated <br>

                  <!-- <input class="text" name="status" size="13" onblur='this.value=this.value.toUpperCase()'>
                  <input class="input" name="button10" type="button" value="..."> -->
                </td>

                  <!-- <input class="text" name="status" size="13" onblur='this.value=this.value.toUpperCase()'>
                  <input class="input" name="button10" type="button" value="..."> -->
                </td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td>From Country:<br>
                  <!-- <input class="text" name="fromCountry" size="13" onblur='this.value=this.value.toUpperCase()'> -->
				  <select name='fromCountry' multiple class='select'  size='0'  onKeyPress	=""></select>
                  <input class="input" name="button4" onclick='showCountryLOV("fromCountry")' type="button" value="...">
                </td>
                <td>From Location:<br>
                  <!-- <input class="text" name="fromLocation" size="13" onblur='this.value=this.value.toUpperCase()'> -->
                  <select name='fromLocation' multiple class='select'  size='0'  onKeyPress	=""></select>
				  <input class="input" name="button5" onclick='showLocationLOV("fromLocation")' type="button" value="...">
                </td>
                <td>To Country:<br>
                  <!-- <input class="text" name="toCountry" size="13" onblur='this.value=this.value.toUpperCase()'> -->
				  <select name='toCountry' multiple class='select'  size='0'  onKeyPress	=""></select>
                  <input class="input" name="button6" onclick='showCountryLOV("toCountry")' type="button" value="...">
                  &nbsp;</td>
                <td>To Location:<br>
                  <!-- <input class="text" name="toLocation" size="13" onblur='this.value=this.value.toUpperCase()'> -->
				  <select name='toLocation' multiple class='select'  size='0'  onKeyPress	=""></select>
                  <input class="input" name="button7" onclick='showLocationLOV("toLocation")' type="button" value="...">
                </td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td> Mode:<br>
                  <select size="1" name="shipmentMode" class='select'>
                    <option value='1' selected>Air</option>
                    <option value='2'>Sea</option>
                    <option value='4'>Truck</option>
				    <option value=''></option>
				  </select>
                </td>
                <td colspan="3" >Service Level ID:<br>
                  <select name='serviceLevelId' multiple class='select'  size='0'  onKeyPress	=""></select>
				  <!-- <input class="text" name="serviceLevelId" size="13" onblur='this.value=this.value.toUpperCase()'> -->
                  <input class="input" name="button8" onclick='showServiceLevelLOV()' type="button" value="...">
                </td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td></td>
                <td colspan="4" > Output Format<input checked name="format" type="radio" value="html">Html&nbsp;&nbsp;<input name="format" type="radio" value="excel">EXCEL
                  Sheet&nbsp;
                </td>
              </tr>
              <tr class="text" vAlign="top">
                <td colspan="3" ><font color="#FF0000">*</font>
                            Denotes Mandatory</td>
                <td colspan="2" align='right'>
                  <input class="input" type="submit" value="Next &gt;&gt;">
				  <input type="hidden" name='Operation' value="getYieldDetails">
                </td>
              </tr>
             
          </table>
</form>

</body>

</html>

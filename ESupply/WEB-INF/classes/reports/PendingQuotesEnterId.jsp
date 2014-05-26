<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	:PendingQuotesEnterId.jsp
Product Name	: QMS
Module Name		: PendigQuotesReports
Date started	: 16 September 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="PendingQuotesEnterId.jsp";
    
%>
<%
          String                operation           = request.getParameter("operation");    
          ESupplyDateUtility	eSupplyDateUtility  = new ESupplyDateUtility();
	      String		        dateFormat          = "";

	      dateFormat = loginbean.getUserPreferences().getDateFormat();
		  eSupplyDateUtility.setPattern(dateFormat);

	      String		        currentDate         = eSupplyDateUtility.getCurrentDateString(dateFormat);
%>

<html>
  <head>
	<title>Customer Contracts - Add</title>
	<link rel="stylesheet" href="../ESFoursoft_css.jsp">
		
<jsp:include page="../etrans/ETDateValidation.jsp" >
	<jsp:param name="format" value="<%=dateFormat%>"/>
</jsp:include>

<script>
//@@ Added by Subrahmanyam for the WPBN ISSUE: 150461 ON 20/12/2008
		function openCustomerLov()
		{
			var tabArray = 'CUSTOMERID';
			var formArray = 'customerId';
			var lovWhere	=	"";
			
			Url		="../qms/ListOfValues.jsp?lovid=CUSTOMER_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTECUSTOMER&search= where CUSTOMERID LIKE '"+document.forms[0].customerId.value+"~'";

			Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
			Options	='width=800,height=750,resizable=yes';
			Features=Bars+','+Options;
			Win=open(Url,'Doc',Features);
			
		 }
//@@ Ended by Subrahmanyam for the WPBN ISSUE: 150461 ON 20/12/2008
function popUpWindow(input)
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;

	if(input=='salesPersonCode')
	{
		Url='../etrans/ETCLOVMultipleSalesPerson.jsp?salesPersonCode='+document.forms[0].salesPersonCode.value+'&wheretoset='+input+'&terminalId='+terminalId+'&fromWhere=pendingReport';//@@ Modified by subrahmanyam  for the pbn id:220125 on 07-oct-10
	    Options='width=700,height=300,resizable=no';
	}
//@@ Commented by subrahmanyam for the WPBN ISSUE: 150461 ON 20/12/2008
	/*else if(input=='customerId')
	{
		Url='../etrans/ETCLOVMultipleCustomers.jsp?wheretoset='+input;
		Options='width=700,height=300,resizable=no';
		
		
	}*/
//@@ Ended by subrahmanyam for the WPBN ISSUE: 150461 on 20/12/2008
	else if(input=='serviceLevelId')
	{
		Url='../etrans/ETCLOVServiceLevelIds.jsp?searchString='+document.forms[0].serviceLevelId.value+'&shipmentMode='+document.forms[0].shipmentMode.value;
	    Options='width=700,height=300,resizable=no';
		
	}
	else if(input=='originId')
	{
		Url='../etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].originId.value+'&wheretoset='+input+'&from=Reports&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode=Air';
	    Options='width=700,height=300,resizable=no';
		
	}
	else if(input=='destinationId')
	{
		Url='../etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].destinationId.value+'&wheretoset='+input+'&from=Reports&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode=Air';
        Options='width=700,height=300,resizable=no';
	}
//@@ Commented by subrahmanyam for the WPBN issue: 150461 on 20/12/2008
			/*Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';	
			Features=Bars+','+Options;
			Win=open(Url,'Doc2',Features);*/
//@@ Added by subrahmanyam for the WPBN ISSUE: 150461 ON 20/12/2008
	if(input=='customerId')
	{
		openCustomerLov();
	}
	else
	{
			Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';	
			Features=Bars+','+Options;
			Win=open(Url,'Doc2',Features);  
	}
//@@Ended by subrahmanyam for the WPBN ISSUE: 150461 on 20/12/2008
}

function showCuctomerIds(values)
{  
	
	var customerId	=	document.forms[0].customerId.value;

	customerId.length	=	0;
	for(var i=0;i<values.length;i++)
	{
		if(i==0)
			customerId=values[i].value;
		else
			customerId=customerId+','+values[i].value;
	}
	document.forms[0].customerId.value=customerId;
}

function showSalesPersonIds(values)
{  
	
	var customerId	=	document.forms[0].salesPersonCode.value;

	customerId.length	=	0;
	for(var i=0;i<values.length;i++)
	{
		if(i==0)
			customerId=values[i].value.substring(0,values[i].value.indexOf("["));
		else
			customerId=customerId+','+values[i].value.substring(0,values[i].value.indexOf("["));
	}
	document.forms[0].salesPersonCode.value=customerId;
}

function upper(obj)
{ 
  obj.value = obj.value.toUpperCase();
}
function specialCharFilter()
{
	if(event.keyCode == 33 || event.keyCode == 34 || event.keyCode == 39 || 
		event.keyCode == 59 || event.keyCode == 96 || event.keyCode == 126)
		return false;
	return true;
}
function changeLabel()
{
  var selectedIndex=document.forms[0].wiseoptions.selectedIndex;
  if(document.forms[0].wiseoptions.options[selectedIndex].value=="C")
	{
			document.getElementById("RefDocLabel").innerHTML=" Customer Id:<font  color=#ff0000>*</FONT><br><input type='text' class='text' name='customerId' size='16'  onBlur='upper(this)' onKeyPress='return specialCharFilter()'>&nbsp;<input type='button' class='input'  name='b1' value='...' onclick=popUpWindow('customerId')> ";
			document.getElementById("RefDocLabel1").innerHTML="";
	}
	else if(document.forms[0].wiseoptions.options[selectedIndex].value=="W")
	{
			document.getElementById("RefDocLabel").innerHTML="From Date (<%=dateFormat%>):<font  color=#ff0000>*</FONT><br><input type='text' class='text' name='fromdate' size='16' maxlength=10 value='' onBlur='dtCheck(this)' >&nbsp;<input type='button' class='input'  name='b1' value='...' onClick=newWindow('fromdate','0','0','../')>";
			document.getElementById("RefDocLabel1").innerHTML="To Date (<%=dateFormat%>):<font  color=#ff0000>*</FONT><br><input type='text' class='text' name='todate' size='16' maxlength=10 value='' onBlur='dtCheck(this)'  >&nbsp;<input type='button' class='input'  name='b1' value='...' onClick=newWindow('todate','0','0','../')>";
	}
	else if(document.forms[0].wiseoptions.options[selectedIndex].value=="L")
	{
             document.getElementById("RefDocLabel").innerHTML=" Origin Location:<font  color=#ff0000>*</FONT><br><input type='text' class='text' name='originId' size='16'  value='' onBlur='upper(this)' onKeyPress='return specialCharFilter()' >&nbsp;<input type='button' class='input'  name='b1' value='...' onclick=showLocationLOV('originId')>";
			 document.getElementById("RefDocLabel1").innerHTML="Destination Location :<font  color=#ff0000>*</FONT><br><input type='text' class='text' name='destinationId' size='16' value='' onBlur='upper(this)' onKeyPress='return specialCharFilter()' ><input type='button' class='input'  name='b1' value='...' onclick=showLocationLOV('destinationId')>";
	}
	else if(document.forms[0].wiseoptions.options[selectedIndex].value=="SP")
	{
	
             document.getElementById("RefDocLabel").innerHTML="Sales Person Id:<font  color=#ff0000>*</FONT><br><input type='text' class='text' name='salesPersonCode' size='16'  value='' onBlur='upper(this)' onKeyPress='return specialCharFilter()' >&nbsp;<input type='button' class='input'  name='b1' value='...'onclick=popUpWindow('salesPersonCode') >";
			 document.getElementById("RefDocLabel1").innerHTML="";
	}
	else if(document.forms[0].wiseoptions.options[selectedIndex].value=="servicelevelwise")
	{
		  
              document.getElementById("RefDocLabel").innerHTML="Service Level Id:<font  color=#ff0000>*</FONT><br><input type='text' class='text' name='serviceLevelId' size='16'  value='' onBlur='upper(this)' onKeyPress='return specialCharFilter()' >&nbsp;<input type='button' class='input'  name='b1' value='...' onclick=showServiceLevelLOV('serviceLevelId')>";
			  document.getElementById("RefDocLabel1").innerHTML="";
	}

}
function validate()
{
 
 var selectedOption=document.forms[0].wiseoptions;
 var field         =document.forms[0]; 
 if(selectedOption.options[selectedOption.selectedIndex].value=="C")
 {
   if(field.customerId.value.length==0)
   {
    alert("Please enter CustomerId");
	field.customerId.focus();
    return false;
   }

 }
 else if(selectedOption.options[selectedOption.selectedIndex].value=="W")
 {
   if(field.fromdate.value.length==0)
   {
    alert("Please Enter the From Date");
	field.fromdate.focus();
    return false;
   }
   
   if(field.todate.value.length==0)
   {
    alert("Please Enter the To Date");
	field.todate.focus();
    return false;
   }
 }
 else if(selectedOption.options[selectedOption.selectedIndex].value=="L")
 {
   if(field.originId.value.length==0)
   {
    alert("Please Enter Origin Id");
	field.originId.focus();
    return false;
   }
   if(field.destinationId.value.length==0)
   {
    alert("Please Enter Destination Id"); 
	field.destinationId.focus();
    return false;
   }

 }
 else if(selectedOption.options[selectedOption.selectedIndex].value=="SP")
 {
   if(field.salesPersonCode.value.length==0)
   {
    alert("Please Enter the Sales Person Code");
	field.salesPersonCode.focus();
    return false;
   }

 }
 else if(selectedOption.options[selectedOption.selectedIndex].value=="servicelevelwise")
 {
   if(field.serviceLevelId.value.length==0)
   {
    alert("Please Enter the Service Level Id");
	field.serviceLevelId.focus();
    return false;
   }

 }

  document.forms[0].subOperation.value = document.forms[0].format.value;

 return true;
}
function shipmentModeChange()
{
  if(document.forms[0].shipmentMode.options[document.forms[0].shipmentMode.selectedIndex].value=="2")
  {
    document.getElementById("consoleType").innerHTML=' ConsoleType : <br> &nbsp;<select class="select" name=conType><option value="FCL">FCL</option><option value="LCL">LCL</option></select>';
  }
  else if(document.forms[0].shipmentMode.options[document.forms[0].shipmentMode.selectedIndex].value=="4")
  {
    document.getElementById("consoleType").innerHTML=' ConsoleType : <br> &nbsp;<select class="select" name=conType><option  value="FTL">FTL</option><option  value="LTL">LTL</option></select>';
  }
  else
  {
	document.getElementById("consoleType").innerHTML='';
  }

}
function changeFormat()
{
   if(document.forms[0].format.options[document.forms[0].format.selectedIndex].value=="Html")
        document.forms[0].nextOperation.value="Html";
   else
        document.forms[0].nextOperation.value="Excel";
}

function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var shipmentMode	=	document.forms[0].shipmentMode.value;
	var Features=Bars+' '+Options;
	var searchString = (toSet=='originId')?(document.forms[0].originId.value):(document.forms[0].destinationId.value);
	var searchString2= '';
	var Url='../etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+"&shipmentMode="+shipmentMode;
	var Win=open(Url,'Doc',Features);
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
 //@@ Commented & Added by subrahmanyam for the pbn id: 212190 on 22-Jul-10				
		//firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		 firstTemp	=	firstTemp.substring(0,lastIndex);
   //@@ Ended by subrahmanyam for the pbn id: 212190 on 22-Jul-10			 
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
		if(data!="")
			data=data+","+temp;
		else	
			data=temp;
		
	}
	document.getElementById(where).value=data;
}

function showServiceLevelLOV()
{   
	var operation		=	'<%=operation%>';//added by rk
	var searchString=document.forms[0].serviceLevelId.value.toUpperCase();
	var Url		=	"../etrans/ETCLOVServiceLevelIds1.jsp?searchString="+searchString+"&shipmentMode="+document.forms[0].shipmentMode.options.value+"&Operation"+operation;	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
</script>

  </head>

  <body onLoad="changeFormat();changeLabel()">
	<form method='POST' action="../QMSReportController?Operation=pendingquotes" onsubmit="return validate();" >
	 
	  <table width="800" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="800" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="790" border="0" ><tr class='formlabel'>
			      <td>Pending Quotes  - View Module </td>
			      <td align=right>QS1012611</td></tr></table></td>
			  </tr>
		    </table>
			  
        <table width="800"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan=5>&nbsp;
				</td>
				
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td width="174" >Terminal Id:<br>
              <%=loginbean.getTerminalId()%>
				</td>
				
            <td width="229">User:<br>
              <%=loginbean.getUserId()%>
				</td>

				
				
            <td width="209"> Date:<br>
              <%=currentDate%>
			  </td>
		  </tr>
			</table>
				<table width="800"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>
			    <td colspan="4">Pending Quotes</td>
			    </tr>
			  <tr valign="top" class='formdata'>
			    
            <td width="146">&nbsp;
				</td>
				
            <td width="232">Shipment Mode:<font  color=#ff0000>*</FONT><br>
             <select name='shipmentMode' class='select' size=1 onChange="shipmentModeChange()">
                <option value="1">Air</option>
                <option value="2">Sea</option>
                <option value="4">Truck</option>
              </select>
              <br>
			  </td>

				
            <td>&nbsp;</td>
            <td width="146" >&nbsp;</td>
			  </tr>
			  <tr valign="top" class='formdata'>
			    <td>&nbsp; </td>
			    <td>Primary Option:<br>
			      <select name='wiseoptions' class='select' size=1 onChange="changeLabel()">
			        <option value="W">Week Wise</option>
					<option value="C">Customer Wise</option>
					<option value="L">Lane Wise</option>
					<option value="SP">SalesPerson Wise</option>
					<option value="servicelevelwise">ServiceLevel Wise</option>
                   </select>
				  </td>
			    <td><span id="consoleType"></span></td>
			    <td >&nbsp;</td>
			    </tr>
			  <tr colspan="4" valign="top" class='formdata'>
			   <td>&nbsp;</td>
				
 			    <td>
				<span id=RefDocLabel>From Date (<%=dateFormat%>):<font  color=#ff0000>*</font><br>
			      <input type='text' class='text' name='fromdate' size='16' maxlength=10 value='' onBlur='dtCheck(this)'>
				<span id=RefDocLabel>From Date (<%=dateFormat%>):<font  color=#ff0000>*</font><br>
			      <input type='text' class='text' name='fromdate' size='16' maxlength=10 value='' onBlur='dtCheck(this);'>
                  <input type='button' class='input'  name='b1' value='...' onClick="newWindow('fromdate','0','0','../')">
				  
				 </td>
				  
			    <td>
				<span id=RefDocLabel1>To Date (<%=dateFormat%>):<font  color=#ff0000>*</font><br>
                  <input type='text' class='text' name='todate' size='16' maxlength=10 value='' onBlur='dtCheck(this)' >
				<span id=RefDocLabel1>To Date (<%=dateFormat%>):<font  color=#ff0000>*</font><br>
                  <input type='text' class='text' name='todate' size='16' maxlength=10 value='' onBlur='dtCheck(this);' >
                  <input type='button' class='input'  name='b1' value='...' onClick="newWindow('todate','0','0','../')">
				  </span>
				 </td>
				  
			    <td>&nbsp;</td>
				
			    </tr>
			  <tr valign="top" class='formdata'>
			    <td>&nbsp;</td>
			    <td colspan="2">Output Format:<br>
			      <select name='format' class='select' size=1 onChange="changeFormat()">
                    <option value="Html">HTML</option>
                    <option value="Excel">EXCEL</option></select>	
				</td>
			    <td >No of Records Per Page:<input type="text" class='text' maxlength=3 name="noofrecords" size=4></td>
			    <input type="hidden" name="noofrecords" value='50'>
				<input type="hidden" name="subOperation" value=''>
                <input type="hidden" name="nextOperation" value=''>
				</tr>
			  <tr valign="top" class='denotes'>
			    <td colspan="2"><font color="#ff0000">*</font> Denotes Mandatory </td>
			    <td colspan="2" align="right"><input type='submit'  class='input'  value='Next>>'></td>
			    </tr>
			</table>
</td>
		  
         
            
          
              <td >&nbsp;</td>
            
        
      </table>
	</form>
  </body>
</html>

<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	:QuotesExpiryEnterId.jsp
Product Name	: QMS
Module Name		: QuotesExpiryEnterId
Date started	: 20 September 2005  
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="java.util.Date,
                 java.text.SimpleDateFormat,
				 com.foursoft.esupply.common.util.ESupplyDateUtility" %>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QuotesExpiryEnterId.jsp";

%>
<% 
	String nextNavigation="";
	
	ESupplyDateUtility	eSupplyDateUtility  = new ESupplyDateUtility();
	String		dateFormat                  = "";
	dateFormat				                = loginbean.getUserPreferences().getDateFormat();
	String		currentDate				    = eSupplyDateUtility.getCurrentDateString(dateFormat);
	String		disName						= null;

	String operation=(String)request.getParameter("Operation");
	String				 fromSummary             =  request.getParameter("fromSummary");//added by subrahmanyam  
	if("quotesExpiryReport".equalsIgnoreCase(operation))
	{
		nextNavigation="QMSReportController?Operation=expiryquotes";
		disName		="Quote";
	}
	else if(operation.equalsIgnoreCase("buyratesExpiryReport"))
	{
		//System.out.println("nextnavigationin jsp page "+operation);
		nextNavigation="QMSReportController?Operation=buyratesexpiry";
		disName		="BuyRate";
	}
	


%>



<html>
  <head>
	<title></title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
	<jsp:include page="../etrans/ETDateValidation.jsp">
<jsp:param name="format" value="<%=dateFormat%>"/>
</jsp:include>
	<script>
//This function used for call the LOVs
function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	
	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&shipmentMode='+document.forms[0].shipmentMode.value;//modified by phani sekhar FOR WPBN 179351 ON 20090812
	var Win=open(Url,'Doc',Features);
}

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


//@@ Added by Subrahmanyam for the WPBN ISSUE: 150461 ON 18/12/2008
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
//@@ Ended by Subrahmanyam for the WPBN ISSUE: 150461 ON 18/12/2008
function popUpWindow(input)
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;

	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';

	
	
	 if(input=='customerId')
		{
		Options='width=700,height=300,resizable=no';
//@@ Commented by Subrahmanyam for the WPBN ISSUE: 150461 ON 18/12/2008		
		//Url='etrans/ETCLOVMultipleCustomers.jsp';
//@@ Added by Subrahmanyam for the WPBN ISSUE: 150461 ON 18/12/2008
		openCustomerLov();
		}
	else if(input=='carrierId')
	{
		var shipmentMode	=	document.forms[0].shipmentMode.value;
// added searchString for carried and servicelevel for issue 145531 on 19/11/2008
		var Url 		= 'etrans/ETransLOVCarrierIds1.jsp?shipmentMode='+shipmentMode+'&multiple=multiple'+'&searchString='+document.forms[0].carrierId.value.toUpperCase();	
		var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options 	= 'scrollbars = yes,width = 700,height = 300,resizable = no';
		var Features 	= Bars +' '+ Options;
	}	
	else if(input=='serviceLevelId')
		{	
		Options='width=700,height=300,resizable=no';		Url='etrans/ETCLOVServiceLevelIds1.jsp?searchString='+document.forms[0].serviceLevelId.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.value;
		}
	
	
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features); 
}
// Added By govind for theBuyrates Expiry report 
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
//Commented by govind for the Buyrates Epiry report Conficts
/*function showLocationValues(values)
{
    var serviceLevelId	=	document.forms[0].serviceLevelId.value;
	serviceLevelId.length	=	0;
	for(var i=0;i<values.length;i++)
	{
		firstTemp   =   values[i].value;
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(-1,lastIndex-1);
		temp		=	firstTemp.toString();
		if(i==0)
			serviceLevelId=temp;
		else
			serviceLevelId=serviceLevelId+','+temp;
	}
	document.forms[0].serviceLevelId.value=serviceLevelId;
}*/
function setCarrierIdValues(values)
{  
	
	var carrierId	=	document.forms[0].carrierId.value;
	carrierId.length	=	0;
	for(var i=0;i<values.length;i++)
	{
		if(i==0)
			carrierId=values[i].value;
		else
			carrierId=carrierId+','+values[i].value;
	}
	document.forms[0].carrierId.value=carrierId;
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

function changeLabel()
{
  var selectedIndex=document.forms[0].expperiod.selectedIndex;
  if(document.forms[0].expperiod.options[selectedIndex].value=="Days")
	{
			document.getElementById("RefDocLabel").innerHTML="Days";
	}
	else if(document.forms[0].expperiod.options[selectedIndex].value=="Month")
	{
			document.getElementById("RefDocLabel").innerHTML="Months";
	}
	else if(document.forms[0].expperiod.options[selectedIndex].value=="Years")
	{
			document.getElementById("RefDocLabel").innerHTML="Years";
	}
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

function mandatory()
{
//@@ Commented by subrahmanyam for the pbn id: 202840 on 14-Apr-010
  <%
	  /*if("quotesExpiryReport".equalsIgnoreCase(operation))
	{*/
	  %>
		/*if(document.forms[0].customerId.value==0)
		{
		alert("Please enter customerId");
		document.forms[0].customerId.focus();
		return false;
		}
		*/
<%  
//}
%>
  
		/* if(document.forms[0].past.value=='')
		{
		alert("Please enter Nos "+document.forms[0].expperiod.value);
		document.forms[0].past.focus();
		return false;
		}	*/
		// Added by Rakesh for Issue:                    on 09-03-2011
		 if(document.forms[0].fromDate.value=='')
		{
		alert("Please enter From Date ");
		document.forms[0].fromDate.value="";
		document.forms[0].fromDate.focus();
		return false;
		}
		 if(document.forms[0].toDate.value=='')
		{
		alert("Please enter To Date ");
		document.forms[0].toDate.value="";
		document.forms[0].toDate.focus();
		return false;
		}

		 <%  if("buyratesExpiryReport".equalsIgnoreCase(operation)){%>
		if(document.forms[0].origin.value =='' ||document.forms[0].destination.value =='')
		{
			alert("Please Enter Origin/Detination");
			return false;

		}
		<%}%>
		if(document.forms[0].fromDate.value!="" && document.forms[0].toDate.value!=""){
			var flag=chkFromToDate(document.forms[0].fromDate.value,document.forms[0].toDate.value);
			if(flag==false){
				alert("Please enter Valid From/To Date");
			}
		   return	flag;
		}
		// Ended by Rakesh for Issue:                    on 09-03-2011
  return true;
}

function numberFilter(input)
  {
        
		var s = input.value.toUpperCase();
        filteredValues = "1234567890";	
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
                alert("Enter only numbers ");
                input.focus();
				input.select();
                return false;
            }
        }
        return true;
}

</script>

  </head>

  <body>
	<form method='post' action='<%=nextNavigation%>' >
	 
	  <table width="850" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="850" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="1"><table width="840" border="0" ><tr class='formlabel'>
				<%if("quotesExpiryReport".equalsIgnoreCase(operation))
	{				%>
			      <td>Quotes Expiry Report </td>
				  <%
				  }
				  else if("buyratesExpiryReport".equalsIgnoreCase(operation))
				  {
				  %>
				<td> Buy Rates Expiry Report</td>
				  <%
				  }
				  %>
			      <td align=right>QS1012521</td></tr></table></td>
			  </tr>
		    </table>
			  
        <table width="850"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan=5>&nbsp;
				</td>
				
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td  >Terminal Id:<br>
              <%=loginbean.getTerminalId()%>
				</td>
				
            <td >User:<br>
              <%=loginbean.getUserId()%>
				</td>
				
            <td > Date:<br>
              <%=currentDate%>
			  </td>
		  </tr>
			</table>
				<table width="850"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>
			    <td colspan="7"><%=disName%> Details </td>
			    </tr>
			  <tr valign="top" class='formdata'>
			    
            <td width="90" >&nbsp;
				</td>
				
            <td width="210" >Shipment Mode: <font  color=#ff0000>*</font><br>
              <select name='shipmentMode' class='select' size=1>
                <option value="1">Air</option>
                <option value="2">Sea</option>
                <option value="4">Truck</option>
              </select>
              <br>
			  </td>

				<%if("quotesExpiryReport".equalsIgnoreCase(operation))
				{%>
              <!--   Commented by Rakesh for Issue:                    on 09-03-2011 -->
			 <!-- <td width="185" > Customer Id:
				<input type='text' class='text' name='customerId' size='16'  value='' onBlur='upper(this)' onKeyPress='return specialCharFilter()' >
                <input type='button' class='input'  name='b1' value='...' onclick="popUpWindow('customerId')">
			  </td>
			 <td width="168"  >Service Level: <br>	 -->	
			 <!--   Comment Ended by Rakesh for Issue:                    on 09-03-2011 -->
			  <%}else if(operation.equalsIgnoreCase("buyratesExpiryReport")){%>

			  <td  width="210">Weight Break: <font color="#FF0000">*</font><br>
					<select size="1" name="weightBreak" class='select'>
						<option  value="Flat"> Flat</option>
						<option  value="Slab" >Slab</option>
						<option  value="List"> List</option>
					</select>
				</td>

				<td  width="210">Rate Type: <font color="#FF0000">*</font><br>
					<select size="1" name="rateType" class='select'>
						<option  value="Flat"> Flat</option>
						<option  value="Slab" >Slab</option>
						<option  value="Slab" >Both</option>
						
					</select>
				</td>

			  <!--   Commented by Rakesh for Issue:                    on 09-03-2011 -->
			  <!--  <td width="185" > Carrier Id: <br>
				<input type='text' class='text' name='carrierId' size='16' value='' onBlur='upper(this)' onKeyPress='return specialCharFilter()' >
                <input type='button' class='input'  name='b1' value='...' onclick="popUpWindow('carrierId')">
			  </td>
			  <td width="168"  >Service Level: <br> -->
			  <!--   Comment Ended by Rakesh for Issue:                    on 09-03-2011 -->
			<%}%>
			   <!--   Commented by Rakesh for Issue:                    on 09-03-2011 -->
              <!-- <input type='text' class='text' name='serviceLevelId' size='16'  value='' onBlur='upper(this)' onKeyPress='return specialCharFilter()' >
              <input type='button' class='input'  name='b1' value='...' onclick="popUpWindow('serviceLevelId')"></td>
            <td width="149"  >&nbsp;</td>
			  </tr>
			  <tr valign="top" class='formdata'>
			    <td>&nbsp;</td>
			    <td>With Expiry Date:<font  color=#ff0000>*</FONT><br>
			      <select name='expDate' class='select' size=1>
			        <option value="Y">Yes</option>
					<option value="N">No</option>
                </select></td>
			    <td >Basis:<br>
			      <select name='basis' class='select' size=1>
                    <option value="<=">=<</option>
					<option value="=">=</option>
					<option value=">=">>=</option>
                  </select></td>
			    <td >Nos. <font  color=#ff0000>*</font><br>
		        <input type='text' class='text' name='past' size='10' maxlength=10 onBlur="numberFilter(document.forms[0].past)" >
		       <span id=RefDocLabel>Days</span></td>
			   <td>Period:<br>
			      <select name='expperiod' class='select' size=1 onChange="changeLabel()">
                  <option value="Days">Days</option>
				  <option value="Month">Months</option>
				  <option value="Years">Years</option>
                </select></td>
				</tr>
			    -->
				<!--   Comment Ended by Rakesh for Issue:                    on 09-03-2011 -->
				 <!-- Added by Rakesh for Issue:                    on 09-03-2011 -->
				<td width="185" > From Date:<font color="#FF0000">*</font>		<br>
				<input type='text' class='text' name='fromDate' size='10'  value=''  onBlur='dtCheck(this)' >
                  <input class="input" name="button" onClick="newWindow('fromDate','0','0','')" type="button" value="...">
			  
			  </td>
			  <td width="185" > To Date: <font color="#FF0000">*</font>			  <br>
				<input type='text' class='text' name='toDate' size='10'  value='' onBlur='dtCheck(this)' >
                  <input class="input" name="button" onClick="newWindow('toDate','0','0','')" type="button" value="...">
			  </td>
    	  		 <!-- Ended by Rakesh for Issue:                    on 09-03-2011 -->
		 <%  if("buyratesExpiryReport".equalsIgnoreCase(operation)){%>
				 <tr valign="top" class='formdata'>
				 <td width="90" >&nbsp;</td>
          		 <td colspan="2">Origin :<font color="#FF0000">*</font><br>
				 <input type="text" name="origin" class='text' size="10" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='showLocationLOV("origin")'></td>
				 <td colspan="2">Destination :<font color="#FF0000">*</font><br>
				 <input type="text" name="destination" class='text' size="10" onblur='trimAll(this);this.value=this.value.toUpperCase()'>&nbsp;<input type="button" value="..." name="destinationLOV"  class='input' onclick='showLocationLOV("destination")'></td>
				 <td>&nbsp;</td>
				 <%}%>
				 </tr>
			  <tr valign="top" class='formdata'>
			    <td>&nbsp;</td>
			    <td colspan="7">Output Format:<br>
				<!--   Commented by Rakesh for Issue:                    on 09-03-2011 -->
				<!-- <input name="subOperation" type="radio" value="html" checked>
			      Html  -->
				  <!--   Comment Ended by Rakesh for Issue:                    on 09-03-2011 -->
		          <input name="subOperation" type="radio" value="Excel" checked>
		          Excel Sheet </td>
				  <!--   Commented by Rakesh for Issue:                    on 09-03-2011 -->
				  <!-- <td colspan="2">Report Per Page:<br>
  		          <input type='text' class='text' name='noofrecords' size='4' maxlength=4 onBlur="numberFilter(document.forms[0].noofrecords)"></td> -->
				  <!--   Comment Ended by Rakesh for Issue:                    on 09-03-2011 -->
			    </tr>
			  <tr valign="top" class='denotes'>
			    <td colspan="2"><font color="#ff0000">*</font> Denotes Mandatory </td>
			    <td colspan="3" align="right"><input type='submit' class='input'  name='b1' onClick='return mandatory()' value='Next>>'>
				<INPUT TYPE="hidden" name='fromSummary' value="<%=fromSummary%>"><!-- added by subrahmanyam for 146463 --></td>
			    </tr>
			</table>
</td></tr></table>
		  
	  
	  
	</form>
  </body>
</html>

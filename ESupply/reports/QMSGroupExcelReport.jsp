<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSGroupExcelReport.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		: 8th Aug 2005 	
Date modified		:  
Author    			: S Anil Kumar
Related Document	: CR_DHLQMS_1007

--%>

<html>
<script>
function chrnum(input)
{
	s = input.value;
	var filteredValues;

	if(input.name=='shipperZipCode'||input.name=='consigneeZipCode')
		filteredValues = "''~!@#$%^&*()+=|\:;<>,./?";
	else
		filteredValues = "''~!@#$%^&*()+=|\:;<>,./?";

	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i] == input )
			{
				document.forms[0].elements[i].focus();
				break;
			}
		}
	}

	input.value = returnString;
}
function changeToUpper(field)
{
	//alert(field.name);
	field.value = field.value.toUpperCase();
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
  function validate()
  {
     if(document.forms[0].shipmentMode.value!=null&&document.forms[0].shipmentMode.value!='')
   {	if(document.forms[0].shipmentMode.value!='AIR'&&document.forms[0].shipmentMode.value!='SEA'&&document.forms[0].shipmentMode.value!='TRUCK')
        {
             alert("Shipment Mode should be Air/Sea/Truck");
               document.forms[0].shipmentMode.focus();
           return false;
       }
	  }
    if(document.forms[0].customerId.value=='')
   {	
             alert("Please select customer Id");
               document.forms[0].customerId.focus();
           return false;
  
	  }
	  return true;
  }
 function showShipmentMode()
{
  if(document.forms[0].shipmentMode.value=="SEA")
	{	  
    document.getElementById("consoleType").innerHTML="Console&nbsp;Type:<br><select size='1' class='select' name='consoleType'><option value='LCL'>LCL</option><option value='FCL'>FCL</option></select>";
    }
  else if(document.forms[0].shipmentMode.value=="TRUCK")
	{	  
    document.getElementById("consoleType").innerHTML="Console&nbsp;Type:<br><select size='1' class='select' name='consoleType'><option value='LTL'>LTL</option><option value='FTL'>FTL</option></select>";
    }
	else
	{
	 document.getElementById("consoleType").innerHTML="";
	}
}
</script>
 <body >
 <link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<form  method="post" name="QuoteGroupExcel Report" action="QMSRatesReportController?Operation=GroupingExcel&subOperation=Report" onSubmit="return validate();">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				QuoteGroup Excel</td><td align='right'><!-- Screen Id goes here --></td></tr></table></td>
			  </tr>
			 
	 </table>

 <table width="100%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
  <%
	if(request.getAttribute("errorMsg")!=null)
	{

%>
	<tr  class='formdata' >
	<td colspan='6' >
			<font  color=#ff0000> <%=(String)request.getAttribute("errorMsg")%></font>
	</td>
	</tr>
<%
	}
%>
		<tr class="formdata" >
			<td  width ='20%' >Customer&nbsp;Id&nbsp;<font color="#ff0000">*</font>
				<br>
				<input class="text" maxLength="25" name="customerId"  value="<%=request.getParameter("customerId")!=null?request.getParameter("customerId"):""%>" size="12"  row="0" onBlur="changeToUpper(this);chrnum(this)" >
			   <input type="button" class="input" name="customerIdLov" value="..." onClick="openCustomerLov()" >
			</td>
				<td  width ='20%' align='center'>Origin&nbsp;Country 
				<br>
				<input class="text" maxLength="25" name="originCountry"  value="<%=request.getParameter("originCountry")!=null?request.getParameter("originCountry"):""%>" size="12"   onBlur="changeToUpper(this);chrnum(this)" >
			</td>
				<td  width ='20%' align='center'>Origin&nbsp;City
				<br>
				<input class="text" maxLength="25" name="originCity"  value="<%=request.getParameter("originCity")!=null?request.getParameter("originCity"):""%>" size="12"  onBlur="changeToUpper(this);chrnum(this)" >
			</td>
				<td  width ='20%' align='center'>Destination&nbsp;Country
				<br>
				<input class="text" maxLength="25" name="destCountry"  value="<%=request.getParameter("destCountry")!=null?request.getParameter("destCountry"):""%>" size="12"  onBlur="changeToUpper(this);chrnum(this)" >
			</td>
			<td  width ='20%' align='center'>Destination&nbsp;City
				<br>
				<input class="text" maxLength="25" name="destCity"  value="<%=request.getParameter("destCity")!=null?request.getParameter("destCity"):""%>" size="12"  onBlur="changeToUpper(this);chrnum(this)" >
			</td>
		<!-- 	<td  width ='10%' >Shipment&nbsp;Mode
				<br>
				<input class="text" maxLength="25" name="shipmentMode" value="<%=request.getParameter("shipmentMode")!=null?request.getParameter("shipmentMode"):""%>" size="12"  onBlur="changeToUpper(this);chrnum(this)" >
			</td> -->
			 		<td width ='20%' >Mode:<br>
			<select size="1" name="shipmentMode" class="select" onChange="showShipmentMode()">
				<option value=""></option><!--added by silpa.p on 28-06-11-->
				<option value="AIR">Air</option>
				<option value="SEA">Sea</option>
				<option value="TRUCK">Truck</option>
			</select>
		</td>
		
		<!--  Added By Kishore Podili -->
	<!--	<td width ='10%' nowrap>Weight Break<br><!--commented by silpa.p on 28-06-11-->
		<!--	<center>
				<select size="1" name="weightBreak" class="select" onChange="showShipmentMode()">
					<option value="FLAT">Flat</option>
					<option value="SLAB">Slab</option>
					<option value="LIST">List</option>
				</select>
         </center>
		
		</td>
		<!--  Added By Kishore Podili  -->
		
		<td  width ='3%'>
			 <span name ='consoleType' id='consoleType' style='position:relative;'></span> 
		</td>
   	</tr>
		<tr class="formdata" >
		</tr>
		  <tr class="formdata" vAlign="top">
         
               	<td   align='center'>	<input class="checkbox" type="checkbox" maxLength="25" name="spotRateFlag"  size="3" >Include&nbsp; Spot&nbsp;Rates 
			
			</td>
				<td colspan="6" > Output Format <input name="format" type="radio" checked value="Excel">EXCEL
                  Sheet&nbsp;
                </td> <td/>
              </tr>
              <tr class="text" vAlign="top">
                <td colspan="4" ><font color="#FF0000">*</font>
                            Denotes Mandatory</td>
                <td colspan="5" align='right'>
                  <input class="input" type="submit" value="Next &gt;&gt;">
	<!-- 	<tr class="formdata" >
              	<td colspan='7' align='right'><input type="submit" class="input" value="EXCEL" name="submit">
               </td>
              </tr> -->
	</table>
</body>
</html>
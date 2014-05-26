<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/*
	Program Name	:ETAConversionFactorView.jsp
	Module Name		:ETrans
	Task			:CurrencyConversion Master
	Sub Task		:LOV
	Author Name		:Subba Reddy V
	Date Started	:September 12,2001
	Date Completed	:September 13,2001
	Date Modified	:July1,2003 by Ramesh Kumar.P
	Description		:This file main purpose is to get LOV from the database.
*/
%>


<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<html>
<head>
<title>Currency Conversion</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
	function upper(input)
	{
		input.value = input.value.toUpperCase(); 
	}
	
	function showTerminal(input)
	{
		if( input.checked )
		{
			document.forms[0].terminalIdButton.disabled = false;
		}
		else
		{
			document.forms[0].terminalIdButton.disabled = true;
		}
	}

	function validateForm()
	{
		var flag = false;
		if( document.forms[0].locationId.value.length == 0 )
		{
			alert("Enter Location Id ");
			document.forms[0].locationId.focus();
			return false;
		}
		for(  i=0;i<document.forms[0].module.length;i++)
		{
			if (document.forms[0].module[i].checked )
			{
				flag = true;
				break;
			}
		}
		if( ! flag )
		{
			alert("Select at least one Module ");
			return false;
		}
	}

	function check()
	{
		if(document.webform.baseCurrency.length>1)
		{
			len=document.webform.baseCurrency[0].value
			if(len=="")
			{
				alert("Enter CurrencyId")
				document.webform.baseCurrency[0].focus();
				return false;
			}
		}
		var lent = document.webform.baseCurrency.length
		for(i=0;i<lent;i++)
		{
			if(document.webform.baseCurrency[i].value!="")
				{
				if(document.webform.sltdCurrency[i].value=="")
				{
					alert("Enter SelectedCurrencyId")
					document.webform.sltdCurrency[i].focus();
					return false;
				}
				}
		}
		document.forms[0].Submit2.disabled='true';
	}
	
	function placeFocus() 
	{
		if( document.forms.length > 0 ) 
		{
			var field = document.forms[0];
			for(i = 0; i < field.length; i++) 
			{
				if( (field.elements[i].type == "text") || (field.elements[i].type == "textarea") || 	(field.elements[i].type.toString().charAt(0) == "s") ) 
				{
					document.forms[0].elements[i].focus();
					break;
				}
			}
		}
	}

	function stringFilter (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  // Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase();
		}
		input.value = returnString;
	}

	
	function setTerminal()
	{
		var terminal  = new Array(37);   
		var count;
		if( document.forms[0].locationId.value.length != 0 )
		{
			for( count=0 ; count <  37 ; count++)
			{
				if ( document.forms[0].locationId.value==terminal[count] )
				{ 
					return;
				}
		    }
			document.forms[0].locationId.value="";
			alert("Please enter the correct TerminalId/LocationId OR use the LOV  ")
		}
	}

	function pop(field)
	{
	/*
		myUrl= 'ETACurrencyConversionCurrencyLOV.jsp?';
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=256,height=256,resizable=no';
		var myFeatures = myBars+','+myOptions;
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;*/
	}
	
	function openWin(obj,count)
	{
		
		var x=obj.name;
		var k=x.substring(9,10);
		var str=x.substring(0,3);
		document.webform.hiding.value=k-1;
		searchString = "";
		if(str=='get')
		{
		   
		   if(count==0)
		   {
		   
			   searchString = document.forms[0].baseCurrency[0].value.toUpperCase();
		   }
		   else if(count==1)
		   {
		   
			   searchString = document.forms[0].baseCurrency[1].value.toUpperCase();
		   }
		    else
			{
			   searchString = document.forms[0].baseCurrency[count].value.toUpperCase();
			}
	 }
     else if(str=='set')
	 {
	 
	      if(count==0)
		   {
		       searchString = document.forms[0].sltdCurrency[0].value.toUpperCase();
		  }
		  else if(count==1)
		  {
		      searchString = document.forms[0].sltdCurrency[1].value.toUpperCase();
		  }
		  else
		  {
			  searchString = document.forms[0].sltdCurrency[count].value.toUpperCase();
		  }
	 }
		
		
		var myUrl = 'ETCCurrencyLOV.jsp?str='+str+'&searchString='+searchString;
		var myBars = 'directories=no,location=right,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=280,height=350,resizable=no';
		var myFeatures = myBars+','+myOptions;
		var newWin = window.open(myUrl,'myDoc',myFeatures);
		if (!newWin.opener)
		newWin.opener = self;
		if (newWin.focus != null)
		newWin.focus();
		return false;
	}


	baseCurrency = new Array("","","","","","","","","","","","","","","","","","","","","","","","","");
	sltdCurrency = new Array("","","","","","","","","","","","","","","","","","","","","","","","","");
	<!-- Begin
	var partDet=new Array(new Array('a','b'));number=1;
	
	function createForm(numb)
	{
     /* if (numb==1)
		   document.webform.baseCurrency.focus();
		   else 
			   document.webform.sltdCurrency.focus();*/

		var number = parseInt(numb)
		var k1=0
		var k2=0
		for(j=0; j < webform.elements.length; j++)
		{
				if(webform.elements[j].name == "baseCurrency")
				{
					baseCurrency[k1] = webform.elements[j].value
					k1++
				}
				if(webform.elements[j].name == "sltdCurrency")
				{
					sltdCurrency[k2] = webform.elements[j].value
					k2++
				}

		}

		data = "";
		inter = "'";
		var y = ""
		var x = ""
		var cnt = 0

		if (number < 25 && number > -1)
		{
			data +="<table width='100%' border='0' cellspacing='1' cellpadding='4' height='13' align='center'>"

			for (i=1; i <= number; i++)
			{

					    data = "" +data
					  + "<tr class='formdata' align='center'>"
						  + "<td width='45%'><input type='text' class='text' name='baseCurrency' value=" + inter + baseCurrency[i]  + inter + "' maxLength='3' onBlur='upper(this)' size=8>"
						  + "&nbsp;&nbsp;<input type='button' name=" + inter + 'getPartId' + (i+1) + inter + " value='...'  "
						  +" onClick='return openWin(this,"+i+")' class='input'></td>"
						  + "<td width='45%'> <input size='8' value=" + inter + sltdCurrency[i]  + inter + "' maxlength='3' type='text' class='text' name='sltdCurrency' value=" + inter + (i+1)  + inter + "' onBlur='upper(this)'>&nbsp;&nbsp;<input type='button' name=" + inter + 'setPartId' + (i+1) + inter + " value='...' onClick='return openWin(this,"+i+")' class='input'><td width='10%'>";
                    if(i != number)
					 data = data+"</td>"
	      		if(i == (number))
				{
					data = "" + data + "<input class='input' type='Button' size=10  value='>>' onClick='createForm(++number);'></td>"
				}
				data = "" + data + "</td></tr>"

			}
			data = "" + data +"</table>"


			if (document.layers)
			{
				document.layers.cust.document.write(data);
				document.layers.cust.document.close();
			}
			else
			{
				if (document.all)
				{
					cust.innerHTML = data;
				}
			}
		}
		else
		{
			window.alert("Please select up to 8 entries.");
		}
	}
//  End -->
 function emptyValues()
 {	
  for(var i=0;i<document.forms[0].elements.length;i++)
   {
	if(document.forms[0].elements[i].type=="text")
	{
	  document.forms[0].elements[i].value="";
	}
   }
 } 	    		
function CallBase()
{
	document.forms[0].baseCurrency.focus();
}

</script>


</head>

<body onLoad="CallBase();createForm(counter.number.value);emptyValues();">

<form name=webform method="POST" action="ETCCurrencyConversionViewProcess.jsp" onSubmit="return check()">
<table width="800" cellpadding="0" cellspacing="0">
<tr bgcolor="ffffff"> 
<td>
<table width="800" cellspacing="1" cellpadding="4">
<tr class="formlabel" valign="top"> 
<td colspan="2"><table width="790" border="0" ><tr class='formlabel'><td>Currency Conversion - View</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionView.jsp","View")%></td></tr></table></td>
</tr>
 <table border="0" width="800" cellpadding="4" cellspacing="1" >
			  <tr class='formdata'><td colspan='6'>&nbsp;</td></tr>
                <tr valign="top" class='formdata'> 
<td >Inter Company Currency Conversion <input type="radio" value="I" checked name="R1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
External Currency Conversion 
<input type="radio" name="R1" value="E">
</td>
</tr>
<tr class="formdata" valign="top">
<td> 
&nbsp;
</td>
</tr>
</table>
<table width='800' border='0'  cellspacing='1' cellpadding='4' height="13">
<tr class="formheader" align="center" valign="top">
<td width='45%'>Base Currency : &nbsp;<font color="#FF0000">*</font>

</td>
<td width='45%'>Selected Currency : &nbsp;<font color="#FF0000">*</font>

</td>
<td  width="10%">&nbsp;</td>
</tr>
<tr class="formdata" align="center" valign="top">
<td  width="45%" >
<input type='text' class='text' name='baseCurrency' maxlength="3" onBlur="return upper(this)" size=8>&nbsp;&nbsp;
<input type='button' name='getPartId1' value='...' onClick='return openWin(this,0)'  class="input">

</td>
<td width="45%">

<input type='text' class='text' name='sltdCurrency'  size=8 maxlength="3" onBlur="return upper(this)">&nbsp;&nbsp;
<input type='button' name="setPartId1" value='...' onClick='return openWin(this,0)'  class="input">
</td>
<td  width="10%">&nbsp;</td>
</tr>
</table>
<!--
      Dynamic Table Starting Here
-->
<span id=cust style='position:relative;'></span> 
<!--
      Dynamic Table Ending Here
-->


<table width="800" cellspacing="1" cellpadding="4">
<tr class='denotes'> 
<td width="33%" ><font color="#FF0000">*</font>Denotes Mandatory</td>

<td width="33%" align="right">
<input type="submit" name="Submit2" value="Next>>" class="input">

</td>
</tr>
</table>
</td>
</tr>
</table>
<input type ="hidden" name = "hiding">
 <input type = "hidden" name = "hiding1">
</form>
<form name=counter>
<input type=hidden name=number value=1>
</form>
</body>
</html>

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
	Program Name	:ETACurrencyConversionViewAll.jsp
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

<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<html>
<head>
<title>Conversion Factor View All</title>
<%
	String[] baseCurrency = null;
	String currencyIdHide="";
	baseCurrency	=	request.getParameterValues("baseCurrency");
	currencyIdHide	=	request.getParameter("currencyIdHide");

%>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
	function pop()
	{
		myUrl= 'ETCCurrencyConversionViewAllLov.jsp';
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
	}

	function restart()
	{
		str = currid;
		document.forms[0].currLov.value=str;
		newWin.close();
	}

	function validate()
	{
		if(document.forms[0].baseCurrency.options.length == 0)
		{
			alert("Please Select Currency Id");
			return false
		}
		else
		{
			document.forms[0].submit.disabled='true';
			return true
		}
	}

	function assignLocations()
	{
		var len1= window.document.forms[0].baseCurrency.options.length;
		var index=0;
		for(var i=0;i<len1;i++)
		{
			window.document.forms[0].baseCurrency.options.remove(index);
		}
		str = hf;	
		entries = str.split("-");
		for(i=0;i<entries.length;i++)
		{
   			if(entries[i] != "-" && entries[i]!="")
			{
				window.document.forms[0].baseCurrency.options[index] = new Option(entries[i] ,entries[i] )	
				index++;
			}
		}
		len = document.forms[0].baseCurrency.options.length;
		currencyList = new Array();
		for(i=0;i<len;i++)
		{
			currencyList[i] = document.forms[0].baseCurrency.options[i].length;
		}
		if(document.forms[0].baseCurrency.options.length > 0)
		{
			var terId = document.forms[0].baseCurrency.options[0].value
	 //window.document.forms[0].terminalId.value = terId;
			document.forms[0].currencyIdHide.value = currencyList.toString();
		}
		else
		{
			document.forms[0].terminalId.value = '';
		}
	}

	function restart1()
	{
		str = hf;	
		newWin.close();
		entries = str.split("-");
// Code Added by Prabhakar
		var len1= window.document.forms[0].baseCurrency.length;
		var index=0;
		for(var i=0;i<len1;i++)
		{
			window.document.forms[0].baseCurrency.options.remove(index);
		}
		for(i=0;i<entries.length;i++)
		{
			if(entries[i] != "-" && entries[i]!="")
			{
				window.document.forms[0].baseCurrency.options[index] = new Option(entries[i] ,entries[i] )
				index++;
			}
		}
// END Code Added by Prabhakar
/*
		document.forms[0].baseCurrency.options.clear;
		var j=0;
		for(i=0;i<entries.length;i++)
		{
			if(entries[i] != "-" && entries[i]!="")
			{
				val = new Option(entries[i])
				document.forms[0].baseCurrency.options.add(val)
				document.forms[0].baseCurrency.options[j++].value=entries[i]				
			}
		}
*/
		len=document.forms[0].baseCurrency.options.length;
		for(i=0;i<len;i++)
		//document.forms[0].baseCurrency.options[i].selected=true
		document.forms[0].baseCurrency.disabled=true;
		var flag=false;
		for( i = 0; i<len;i++)
		{//Modified by subrahmanyam for the pbn id:203753 on 23-APR-10
			//if(document.forms[0].baseCurrency.options[i].value=="ALL")
			if(document.forms[0].baseCurrency.options[i].value=="SelectForAllCurrencies")
			{
				//Modified by subrahmanyam for the pbn id:203753 on 23-APR-10
				//document.forms[0].baseCurrency.value="ALL"
				document.forms[0].baseCurrency.value="SelectForAllCurrencies"
				flag = true;
				break;
			}
		}
		param="";
		var i=0;
		if(flag==false)
		{
			for(i=0; i <len-1; i++)
			{
				param = param +"'" + document.forms[0].baseCurrency.options[i].value + "',";
			}
			param = param + "'" + document.forms[0].baseCurrency.options[i].value + "'";
			document.forms[0].currencyIdHide.value = param;
		}
	}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body >
<form name="form1" method="post" action="ETCCurrencyConversionViewAllProcess.jsp" onSubmit="return validate()">
<input type="hidden" name="hcurrency" size="20" value="">  
<table width="800" border="0" cellspacing="0" cellpadding="0">
<tr>
<td bgcolor="#FFFFFF">
<table width="800" border="0" cellspacing="1" cellpadding="4">
<tr class='formlabel'> 
<td ><table width="790" border="0" ><tr class='formlabel'><td>Currency Conversion - View All </td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionViewAll.jsp","SelectForAllCurrencies")%></td></tr></table></td>
</tr>

<tr class='formdata' valign="top"> 
<td >Inter Company Currency Conversion <input type="radio" value="I" checked name="R1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
External Currency Conversion 
<input type="radio" name="R1" value="E">
</td>
</tr>
</table>
<br>
<table width="800" border="0" cellspacing="1" cellpadding="4">
<tr class='formdata'> 
<td >Currency 
:<br>
<select name="baseCurrency" size="5" multiple class='select'></select>
<input type="button" name="currLov" value="..." onClick="return pop(this)" class='input'>
</td>
</tr>
<tr> 
<input type="hidden" name="currencyIdHide" >
<td valign="top" align="right"> 
<input type="submit" name="submit" value="Next>>" class='input'>
</td>
</tr>
</table>
</td>
</tr>
</table>
</form>
</body>
</html>

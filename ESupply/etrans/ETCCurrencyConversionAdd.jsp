
<%@ page import	=	"org.apache.log4j.Logger "%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCurrencyConversionAdd.jsp ";
%>
<%
/*
	Program Name :	ETACurrencyConversionAdd.jsp
	Module Name	 :  ETrans
	Task		 :  CurrencyConversion Master
	Sub Task	 :  Add
	Author Name	 :  Subba Reddy V
	Date Started :  September 12,2001
	Date Completed: September 13,2001
	Modified By	  : Ramesh Kumar.P	
	Date Modified : July 1,2003 
	Description   : This file main purpose is to Add Interface to view Add screen. 	
	
	
*/
  logger  = Logger.getLogger(FILE_NAME);	
	String operation	= null;
	try
	{	
			operation = request.getParameter("Operation");
			session.setAttribute("Operation",operation);
			
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"CurrencyConversionAdd.jsp : Exception : ",e.toString());
    logger.error(FILE_NAME+"CurrencyConversionAdd.jsp : Exception : "+e.toString());
	}					
%>			
<html>
<head>
<title>Currency Conversion</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language="javascript">
function getAlphaNumeric()
{
	if(event.keyCode!=13)
	{
		if((event.keyCode < 48 ||event.keyCode > 57) && (event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) )
		return false;	
	}
	return true;		
}
function checkFields()
{
	selectForm();
	
	if(document.forms[0].baseCurrency.value=="")
	{
		alert("Please enter the Base CurrencyId")
		document.forms[0].baseCurrency.focus();
		return false;
	}
	if(document.forms[0].sltdCurrency.value=="")
	{
		alert("Please select other CurrencyId(s) ")
		document.forms[0].sltdCurrency.focus();
		return false;
	}
	document.forms[0].submit.disabled='true';
	return is3Letters(document.forms[0].baseCurrency);
}
function pop()
{
	if(document.currency.currLov.value == "")
	{
		
		alert("Please enter the Base Currecy. ");
		return false;
	}
	else
	{
		
		var searchString = document.forms[0].baseCurrency.value;
		myUrl= 'ETCCurrencyConversionAddLOV.jsp?searchString='+searchString;
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;
	}
}
function restart()
{
	str = currid;
	document.currency.currLov.value=str;
	newWin.close();
}

function showCurrencies()
{
		var temp='';
		if(document.forms[0].R1[0].checked)
			temp="I"
		else
			temp="E"
		
		myUrl= 'ETCCurrencyConversionViewEnterId.jsp?baseCurrency='+document.forms[0].baseCurrency.value+'&R1='+temp;
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;
}
function selectForm()
{
	var lent = document.currency.sltdCurrency.options.length
	for (var i=0; i<lent; i++)
	{
		document.currency.sltdCurrency.options[i].selected = true
	}
}


function upper(input)
{
	input.value = input.value.toUpperCase(); 
}

function is3Letters(input)
{
	if (input.value.length >0 &&input.value.length< 3)
	{
		alert('Please enter 3 characters for Currency 2 ');
		input.value='';
		input.focus();
		return false;
	}
	return true;
}
function assignLocations()
{
	var len1= window.document.forms[0].sltdCurrency.options.length;
	var index=0;
	for(var i=0;i<len1;i++)
	{
		window.document.forms[0].sltdCurrency.options.remove(index);
	}
	str = hf;	
	entries = str.split("-");
	for(i=0;i<entries.length;i++)
	{
		if(entries[i] != "-" && entries[i]!="")
		{
			window.document.forms[0].sltdCurrency.options[index] = new Option(entries[i] ,entries[i] )	
			index++;
		}
	}
	len = document.forms[0].sltdCurrency.options.length;
	currencyList = new Array();
	for(i=0;i<len;i++)
	{
		currencyList[i] = document.forms[0].sltdCurrency.options[i].length;
	}
	if(document.forms[0].sltdCurrency.options.length > 0)
	{
		var terId = document.forms[0].sltdCurrency.options[0].value
		document.forms[0].currencyIdHide.value = currencyList.toString();
	}
	else
	{
		document.forms[0].currencyIdHide.value = '';
	}
}
function CallBase()
{
	document.forms[0].baseCurrency.focus();
}
	
</script>
</head>
<body onLoad="CallBase()">
<form method="get" action="ETCCurrencyConversionAddProcess.jsp" name="currency" onSubmit="return checkFields()">
<table width="800" border="0" cellspacing="0" cellpadding="0">
<tr bgcolor="#FFFFFF" valign="top"> 
<td colspan="2" bgcolor="ffffff">
        <table width="800" border="0" cellspacing="1" cellpadding="4">
          <tr class="formlabel" valign="top"> 
            <td colspan="2"><table width="790" border="0" ><tr class='formlabel'><td>Currency Conversion - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionAdd.jsp",operation)%></td></tr></table></td>
          </tr>
		  <tr class="formdata"><td colspan="2">&nbsp;</td></tr>
          <tr class="formdata" valign="top"> 
            <td width="313"  >Inter Company Currency Conversion 
              <input type="radio" value="I" checked name="R1">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
            </td>
            <td width="426" height="31" > External Currency Conversion 
              <input type="radio" name="R1" value="E">
            </td>
          </tr>
          <tr align="right" valign="top" class="formdata"> 
            <td colspan="2" > 
              <div align="left">Select the Base Currency :<font color="#FF0000">*</font> 
                <input type="text" class="text" name="baseCurrency" maxlength="3" onBlur="upper(this)" size="5" >
                &nbsp; 
                <input type="button" value="..." name="currLov" onClick="pop()" class="input">
              </div>
            </td>
          </tr>
          <tr class="formdata"> 
            <td width="313" >Select the Currencies from the List of 
              Values for which Conversion Factor has to be filled :<font color="#FF0000">*</font> &nbsp;&nbsp; 
            </td>
            <td width="426" > 
              <select size="5" name="sltdCurrency" multiple class="select">
              </select>
              <input type="button" value="..." name="AllCurrLov" onClick="showCurrencies()" class="input">
            </td>
          </tr>
          <tr class="formdata"> 
            <td colspan="2" > 
              <input type="hidden" name="currencyIdHide" >
              <input type="hidden" name="Operation" value="<%=operation%>" >
            </td>
          </tr>
          <tr valign="top" class="denotes" align="right">
		  <td align="left">
			<font  color="#FF0000">*</font>Denotes Mandatory</td>
            <td colspan="2" > 
              <input type="submit" value="Next>>" name="submit"  class="input">
            </td>
          </tr>
        </table>
</td>
</tr>
</table>
</form>
</body>
</html>
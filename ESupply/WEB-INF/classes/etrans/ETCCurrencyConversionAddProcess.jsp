<%
/*
	Program Name	:ETACurrencyConversionAddProcess.jsp
	Module Name		:ETrans
	Task			:CurrencyConversion Master
	Sub Task		:Lov
	Author Name		:Subba Reddy V
	Date Started	:September 12,2001
	Date Completed	:September 13,2001
	Date Modified	:July 1,2003 by Ramesh Kumar.P
	Description		:This file main purpose is to Process from the database.
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					java.util.Vector, 
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"
					
%>
<%!
  private static Logger logger = null;
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<html>
<head>
<%
	String FILE_NAME		=	"ETCCurrencyConversionAddProcess.jsp";
  logger  = Logger.getLogger(FILE_NAME);	
	String baseCurrency		=	request.getParameter("baseCurrency");
	String[] sltdCurrency	=	request.getParameterValues("sltdCurrency");
	String radioChkd		=	request.getParameter("R1");
	String Operation		=	request.getParameter("Operation");
	String checkValid		=	null;
	String BuyRate[]		=	null;
	String SellRate[]		=	null;
	String SpecifiedRate[]	=	null;
	String cFactor[][]		=	null;
	
	try
	{
		SetUpSessionHome 	home	 	= null;
		SetUpSession 		remote 		= null;
		InitialContext 					initial		= new InitialContext();
		home			 	= ( SetUpSessionHome)initial.lookup("SetUpSessionBean");
		remote				= ( SetUpSession)home.create();  
				
		if(Operation.equals("Modify"))
		{
			cFactor		=	remote.getConversionFactor(baseCurrency,sltdCurrency,radioChkd);
			for(int j = 0;j< cFactor.length; j++)
			{	
				for(int i = 0;i< 3; i++)
				{	
					if(cFactor[j][i] == null)
						cFactor[j][i] = "";
				}
			}		
		}//end of modify if		 		
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"CurrencyConversionAddProcess.jsp : Exception : " + e);
    logger.error(FILE_NAME+"CurrencyConversionAddProcess.jsp : Exception : " + e);
	}
%>
<title>Currency Conversion Module</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>
	function getSpecialCode()
	{
		if(event.keyCode!=13)
		{
		if((event.keyCode==34 || event.keyCode==39 || event.keyCode==59 || event.keyCode==96 || event.keyCode==126))
		return false;
		}
		return true;
	}
	function getNumberCode()
	{
		if(event.keyCode!=13)
		{
		if((event.keyCode < 46 || event.keyCode==47  || event.keyCode > 57) )
		return false;	
		}
		return true;	
	}
	function getAlphaNumeric()
	{
		if(event.keyCode!=13)
		{
		if((event.keyCode < 48 ||event.keyCode > 57) && (event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) )
		return false;
		}
		return true;
	}
	function placeFocus()
	{
		document.forms[0].currency2.focus();
	}
	function upper(input)
	{
		input.value = input.value.toUpperCase(); 
	}
	function Mandatory()
	{
		checkCurrency1(document.forms[0].currency2);
		currency2  =	document.forms[0].currency2;
		convFactor = document.forms[0].conversionFactor;
		values = convFactor.value.split(".");
		if(currency2.value.length==0)
		{
		alert("Please enter Currency 2");
		document.forms[0].currency2.focus();
		return false;
		}
		if(convFactor.value.length==0)
		{
		alert("Please enter Conversion Factor");
		return false;
		}
		if(values.length > 2)
		{
		alert("Please enter Correct Conversion Factor");
		document.forms[0].conversionFactor.focus();
		return false;
		}
		return true;
	}
	function formAction()
	{
		var radio = "<%=radioChkd%>"
		var col = 1;
		for(i = 0 ;i < document.forms[0].length; i++)
		{
		if(document.forms[0].elements[i].type == "text" && (document.forms[0].elements[i].name == "BuyRate" || document.forms[0].elements[i].name == "SellRate" ||document.forms[0].elements[i].name == "SpecifiedRate"))
		{
		if(document.forms[0].elements[i].value.length == 0)
		{
			alert("Please enter Rates");
			document.forms[0].elements[i].focus();
			return false;
		}
		else if(document.forms[0].elements[i].value==0)
		{
			alert("Please enter Rates Greater Than Zero");
			document.forms[0].elements[i].focus();
			return false;
		}
			col++; 	  	
		}
		}
		document.forms[0].submit.disabled='true';
		return true;
	}
	function numbersOnly(Object)
	{
		var str = Object.value;
		var len = str.length;
		var  i= 0;
		var  ch;
		if(str=="") return 0;
		for(i=0;i<len;i++)
		{
			ch = str.charAt(i);
			if(ch<'0' || ch>'9')
		{
			alert("Enter Only Digits");
			Object.value="";
			Object.focus();
			return 0;
		}	
		}
		return 1;
	}
	function testUp(input)
	{
		s = input.value;
		filteredValues ="1234567890.";
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{
			var c = s.charAt(i);
			if( filteredValues.indexOf(c)!=-1) 
			returnString += c.toUpperCase();
			else
			flag = 1;
		}
		if( flag==1 )
		{
			alert("Characters are not allowed");
			input.focus();
		}
		input.value = returnString;
	}
	function checkForDecimal(input)
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
					if(input.value.length == index+6)
						return false;
				}
			}
		}
		return true;
}
</script>

</head>
<body>
<form method="POST"  name="currconv" action="ETCCurrencyConversionUpdate.jsp" onSubmit="return formAction()">
<table width="800" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
<tr valign="top" bgcolor="#FFFFFF"> 
<td width="800" >
<table border="0" width="800" cellpadding="4" cellspacing="1" >
<tr valign="top" class="formlabel"> 
<%
	if(radioChkd!=null && radioChkd.equals("I"))
	{
%>
		<td width=800><table width="790" border="0" ><tr class='formlabel'><td>
		Currency Conversion <%=Operation%>
		( <font size="1" face="Verdana">Inter Company Currency Conversion
		</font>)
		</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionAddProcess.jsp",Operation)%></td></tr></table></td>
<%
	}
	else
	{
%>
		<td width=800><table width="790" border="0" ><tr class='formlabel'><td>
		Currency Conversion <%=Operation%>
		( <font size="1" face="Verdana">External Company Currency Conversion
		</font>)
		</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionAddProcess.jsp",Operation)%></td></tr></table></td>
<%
	}
%>
</tr>
	
	</table>
	<table border="0" cellpadding="4" cellspacing="1" width="800">
	<tr valign="top" class="formheader"> 
	<td width="150" >Base Currency</td>
	<td width="150">Selected Currency</td>
	<td width="150">Buy Rate</td>
	<td width="150">Sell Rate</td>
	<td width="150">Specified Rate</td>

	</tr>
	
<%
try
{
					
	if(sltdCurrency != null)
	{
		if(Operation.equals("Add"))
		{					
		 for(int j=0;j<sltdCurrency.length;j++)
		 {
%>
			<tr class="formdata" valign="top">
			<td width="150">
			<%=baseCurrency%></td>
			<input type="hidden" name="baseCurrency" value='<%=baseCurrency%>' >
			<td width="150"><%=sltdCurrency[j]%>
			</td>
			<input type="hidden" name="sltdCurrency" value='<%=sltdCurrency[j] %>'>
			<td width="150">
			<input type="text" class="text" name="BuyRate" size="13" maxlength="10" onKeyPress=" return checkForDecimal(this)" onBlur="testUp(this)"></td>
			<td width="150">
			<input type="text" class="text" name="SellRate" size="13" maxlength="10" onKeyPress="return checkForDecimal(this)" onBlur="testUp(this)"></td>
			<td width="150">
			<input type="text" class="text" name="SpecifiedRate" size="13" maxlength="10" onKeyPress="return checkForDecimal(this)" onBlur="testUp(this)"></td>
			</tr>
<%
	 	 }
	  }	
	  else if(Operation.equals("Modify")) 
	  {	
		 for(int j = 0;j < sltdCurrency.length; j++)
		 {
%>			
			<tr class="formdata" valign="top">
			<td width="150"><%=baseCurrency%></td>
			<input type="hidden" name="baseCurrency" value='<%=baseCurrency%>'>
			<td width="150"><%=sltdCurrency[j]%>
			</td>
			<input type="hidden" name="sltdCurrency" value='<%=sltdCurrency[j] %>'>
			<td width="150">
			<input type="text" class="text" name="BuyRate" size="13" maxlength="10" value='<%=cFactor[j][0]%>' onKeyPress="return checkForDecimal(this)" onBlur="return testUp(this)"></td>
			<td width="150">
			<input type="text" class="text" name="SellRate" size="13" maxlength="10" value='<%=cFactor[j][1]%>' onKeyPress="return checkForDecimal(this)" onBlur="return testUp(this)"></td>
			<td width="150">
			<input type="text" class="text" name="SpecifiedRate" size="13" maxlength="10" value='<%=cFactor[j][2]%>' onKeyPress="return checkForDecimal(this)" onBlur="return testUp(this)"></td>
			</tr>
<%
			}
	 }
   }				
%>

	</table>

<table border="0" cellpadding="4" cellspacing="1" width="800">
	<tr valign="top"> 
	<td><B>Note:
	Base Currency = Selected Currency * Conversion Factor
	<input type="hidden" name="Operation" value="<%=Operation%>">
	</td>	
	<td  bgcolor="white" align="right">

<%
	if(radioChkd!=null && radioChkd.equalsIgnoreCase("I"))
	{
		
%>
		<input type="hidden" name="R1" value="I">
<%
	}
	else
	{
%>
		<input type="hidden" name="R1" value="E">
<%
	}
%>
	<input type="Submit" value="Submit" name="submit" class="input">
	<input type="reset" value="Reset" name="reset" class="input">

	</td>
	</tr>
	</table>
</td>
</tr>
	</table>
</table>
	</form>
<%
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		//Logger.error(FILE_NAME,"Exception in ETCCurrencyConversionAddProcess.jsp:"+ex.toString());
    logger.error(FILE_NAME+"Exception in ETCCurrencyConversionAddProcess.jsp:"+ex.toString());
		session.setAttribute("ErrorCode","ERR");
  		session.setAttribute("ErrorMessage","Unable to Add the record" );
	    session.setAttribute("NextNavigation","ETCCommodityAdd.jsp");
%>
	<jsp:forward page="../ESupplyErrorPage.jsp" />
<% 
 }
%>

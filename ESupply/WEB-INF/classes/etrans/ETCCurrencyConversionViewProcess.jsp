<%
/*
	Program Name	:ETAConversionFactorViewProcess.jsp
	Module Name		:ETrans
	Task			:CurrencyConversion Master
	Sub Task		:LOV
	Author Name		:Subba Reddy V
	Date Started	:September 12,2001
	Date Completed	:September 13,2001
	Date Modified	:July 1,2003 by Ramesh Kumar.P
	Description		:This file main purpose is to get LOV from the database.
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					java.util.Vector, 
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
          com.qms.setup.ejb.sls.SetUpSessionHome, 
					com.foursoft.etrans.setup.currency.bean.CurrencyConversion" 
%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<html>
<head>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCurrencyConversionViewProcess.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String[] baseCurrency		=   request.getParameterValues("baseCurrency");
	String[] sltdCurrency		= 	request.getParameterValues("sltdCurrency");
	String cFactor[][]			=	null;		
	String Operation			=   request.getParameter("Operation");
	String checkValid			=	null;
	int len=0;
	for(int i =0;i<baseCurrency.length;i++)
	{
		if(!baseCurrency[i].equals(""))
		len++;	
	}
	String radioChkd		=	request.getParameter("R1");

%>
<title>CurrencyConversion Add</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
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

	function is3Letters(input)
	{
		if (input.value.length >0 &&input.value.length< 3)
		{
			alert('Please enter 3 characters for Currency 2');
			input.value='';
			input.focus();
			return false;
		}
		return true;
	}

	function checkCurrency1(input)
	{
		upper(input);	
		if(document.forms[0].currency1.value==input.value)
		{
			document.forms[0].conversionFactor.value = "1";
			document.forms[0].conversionFactor.value.length = 1;
			document.forms[0].conversionFactor.readOnly = true;
			document.forms[0].Submit.focus();
		}
		else
		{
			document.forms[0].conversionFactor.readOnly = false;	
			document.forms[0].conversionFactor.focus();  
		}
	}

	function Mandatory()
	{
		checkCurrency1(document.forms[0].currency2);
		currency2  =	document.forms[0].currency2;
		//convFactor = document.forms[0].conversionFactor;
		//values = convFactor.value.split(".");
		if(currency2.value.length==0)
		{
			alert("Please enter Currency 2");
			document.forms[0].currency2.focus();
			return false;
		}
		/*if(convFactor.value.length==0)
		{
			alert("Please enter Conversion Factor");
			return false;
		}
		if(values.length > 2)
		{
			alert("Please enter Correct Conversion Factor");
			document.forms[0].conversionFactor.focus();
			return false;
		}*/
		return true;
	}

	function formAction()
	{
		document.currconv.action = "ETCCurrencyConversionView.jsp"	
	}
	function goBack()
	{
		document.forms[0].con.disabled='true';
		window.history.back(1);
	}
</script>
</head>
<%
	
	String hCurrency= null; 
	String loginterminalid="" ;
	try
	{
		InitialContext  ictx 	= new InitialContext();
		SetUpSessionHome cchome = null;
		SetUpSession ccremote = null;
		cchome        =  (SetUpSessionHome)ictx.lookup("SetUpSessionBean");
		ccremote      =  (SetUpSession)cchome.create();
		cFactor       =   ccremote.getConversionFactor1(baseCurrency,sltdCurrency,radioChkd);
		for(int j = 0;j< cFactor.length; j++)
			{	
				for(int i = 0;i< 3; i++)
				{	
					if(cFactor[j][i] == null)
						cFactor[j][i] = "";
				}
			}	
			
		//for(int i=0;i<baseCurrency.length;i++)
		//{	
		if(baseCurrency != null)
		{
			checkValid =ccremote.checkValidOrNot1(baseCurrency);	
			checkValid = checkValid.trim();
			if(checkValid.equals("C"))
			{
%>
         <script>
			
			alert("Please enter correct CurrencyId");
			history.back();
		 </script>
<%
		return ;
		}
	}
%>

<body >

<form method="POST"  name="currconv" action="" onSubmit="formAction()">
<table width="800" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
<tr valign="top" bgcolor="#FFFFFF"> 
<td width="800" >
<table border="0" width="800" cellpadding="4" cellspacing="1" >
<tr valign="top" class="formlabel"> 
<%
	if(radioChkd.equals("I"))
	 {
%>
<td  width="800"><table width="790" border="0" ><tr class='formlabel'><td>
Currency Conversion View( <font size="1" face="Verdana">Inter Company Currency Conversion</font>)
</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionViewProcess.jsp","View")%></td></tr></table></td>
<%
	}
	else
	{
%>
<td  width="800"><table width="790" border="0" ><tr class='formlabel'><td>
Currency Conversion View( <font size="1" face="Verdana">External Currency Conversion</font>)
</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionViewProcess.jsp","View")%></td></tr></table></td>
<%
}
%>
</tr>
<table border="0" width="800" cellpadding="4" cellspacing="1" >
<tr valign="top" class="formheader"> 
<td  width="150">Base Currency</td>
<td  width="150">Selected Currency</td>
<td width="150">Buy Rate</td>
<td width="150">Sell Rate</td>
<td width="150">Specified Rate</td>
</tr>
<%
	for(int j=0;j<len;j++)
	{
%>
<tr class="formdata" valign="top">
<td width="150"> <%=baseCurrency[j]%></td>
<input type="hidden" name="baseCurrency" value='<%=baseCurrency%>'>
<td width="150"><%=sltdCurrency[j]%>
</td>
<input type="hidden" name="sltdCurrency" value='<%=sltdCurrency[j] %>'>
<td width="150">
<input type="text" class='text' name="BuyRate" size="13" maxlength="15" value='<%=cFactor[j][0]%>' readonly></td>
<td width="150">
<input type="text" class='text' name="SellRate" size="13" maxlength="15" value='<%=cFactor[j][1]%>' readonly></td>
<td width="150">
<input type="text" class='text' name="SpecifiedRate" size="13" maxlength="15" value='<%=cFactor[j][2]%>' readonly></td>
</tr>
<%	
	}
%>
</table>

<tr valign="top" class='denotes'> 
<input type="hidden" name="Operation" value="<%=Operation%>">
<%
	if(radioChkd.equalsIgnoreCase("I"))
	{
		//Logger.info(FILE_NAME,"Radio Button in Modify Process" + radioChkd);
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
<td  bgcolor="white" width="800" align="right"><br>  

<input type="button" value="Continue" name="con" onClick = "goBack()" class="input">

</td>
</tr>
</table>
</td>
</tr>
</table>
</form>
<%
	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME,"ETACurrencyConversionViewFactor: Exception1 ", exp.toString());   
    logger.error(FILE_NAME+"ETACurrencyConversionViewFactor: Exception1 "+ exp.toString());   
	}
%>
</body>
</html>
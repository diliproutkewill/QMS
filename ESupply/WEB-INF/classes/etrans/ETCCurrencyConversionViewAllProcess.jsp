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
	Program Name	:ETAConversionFactorViewAllProcess.jsp
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
					java.util.StringTokenizer,
					java.util.Enumeration,
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
	private static final String FILE_NAME	=	"ETCCurrencyConversionViewAllProcess.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String[] baseCurrency		=   request.getParameterValues("baseCurrency");
	String[][] cFactor        	=   null;		
	String Operation       		=   request.getParameter("Operation");
	String radioChkd			=	request.getParameter("R1");
	String[] sltdCurrency		=	null;
	String currencyIdHide 		=	request.getParameter("currencyIdHide");
	Vector vg					=	new Vector();	
	
%>
<title>CurrencyConversion Add</title>
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

	function goBack()
	{
		window.history.back(1);
	}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<%
	String hCurrency= null; 
	String loginterminalid="" ;
	Vector vec1= new Vector();
	Vector vec2= new Vector();
	try
	{
		InitialContext  ictx 	= new InitialContext();
		SetUpSessionHome cchome = null;
		SetUpSession ccremote = null;
		cchome        =  (SetUpSessionHome)ictx.lookup("SetUpSessionBean");
		ccremote      =  (SetUpSession)cchome.create();
		vg       	 =   ccremote.getCFactorViewAll(currencyIdHide,radioChkd);
		if(vg.size() == 0)
		{
%>
<body >
<table width="800" border="0" cellspacing="0" cellpadding="0">
<tr valign="top" bgcolor="#FFFFFF"> 
<td width="800" >
<table border="0" width="800" cellpadding="4" cellspacing="1" >
<tr valign="top" class='formlabel'> 
<%
	if(radioChkd.equals("I"))
	{
%>
<td  width="800" colspan="2" ><table width="790" border="0" ><tr class='formlabel'><td>Currency Conversion View All( <font size="1" face="Verdana">Inter Company Currency Conversion</font>)
</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionViewAllProcess.jsp","ALL")%></td></tr></table></td>
<%
	}
	else
	{
%>
<td  width="800" colspan="2" >Currency Conversion View All( <font size="1" face="Verdana">External Currency Conversion</font>)
<%
	}
%>
</tr>
<tr class='formdata'>
<td colspan=2 align=center>
<b>No conversionfactor exists  for the selected currency.</b><br>
<input type="button" class='input' value="Continue" name="continue" onClick = "goBack()" align="center">&nbsp;
</tr>
</table>
</table>
</body>
<%
	}
	else
	{
	String conStr="";
	Vector vg2 = new Vector();
	for(int i=0;i<vg.size();i++)
	{
		 vg2.addElement(vg.elementAt(i));
	}
		if(vg.size() > 0)
		{
			int size = vg.size();
			String[] sacurrencyIds 		=   new String[size];
			String[] saCurr2 		    =   new String[size];
			String[] saBuyRate   		=   new String[size]; 
			String[] saSellRate   		=   new String[size]; 
			String[] saSpecifiedRate   	=   new String[size]; 
			Enumeration		efa		= vg.elements();  //enumeration forall and selected values.
			//String ts[]=new String[5];
			// added by 
			String ts[]=null; 
			// senthil prabhu SPETI-4798 
			
			int counter = 0;
			while(efa.hasMoreElements())	// FIRST WHILE
			{
				conStr=(String)efa.nextElement();
				StringTokenizer destStr = new StringTokenizer(conStr,"$");
				//added 
				int l=destStr.countTokens();
				ts=new String[l]; 
              // senthil prabhu SPETI-4798 
				int w=0;
				while(destStr.hasMoreTokens())
				{
					ts[w]=destStr.nextToken();
					w++;	
				}
					if(ts[0]!=null)
					{
						sacurrencyIds[counter] = ts[0];
					}
					if(ts[1]!=null)
					{
						saCurr2[counter]	   = ts[1];
					}
					if(ts[2]!=null)
					{
						saBuyRate[counter]	   = ts[2];	
					}
					if(ts[3]!=null)
					{
						saSellRate[counter]	   = ts[3];	
					}
					if(ts[4]!=null)
					{
						saSpecifiedRate[counter]  = ts[4];	
					}
					counter++;
			}
%>
<body >
<form method="POST"  name="currconv" action="" >
<table width="800" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
<tr valign="top" bgcolor="#FFFFFF"> 
<td width="800" >
<table border="0" width="800" cellpadding="4" cellspacing="1" >
<tr valign="top" class='formlabel'> 
<%
	if(radioChkd.equals("I"))
	{
%>
<td  width="800" colspan="2" ><table width="790" border="0" ><tr class='formlabel'><td>Currency Conversion View All( <font size="1" face="Verdana">Inter Company Currency Conversion</font>)
</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionViewAllProcess.jsp","ALL")%></td></tr></table></td>
<%
	}
	else
	{
%>
<td  width="800" colspan="2" ><table width="790" border="0" ><tr class='formlabel'><td>Currency Conversion View All( <font size="1" face="Verdana">External Currency Conversion</font>)
</td><td align=right><%=loginbean.generateUniqueId("ETCCurrencyConversionViewAllProcess.jsp","ALL")%></td></tr></table></td>
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
</tr></table>
<table border="0" cellpadding="4" cellspacing="1" width="800">
<%
	for(int j=0;j<sacurrencyIds.length;j++)
	{
%>
<table border="0" cellpadding="4" cellspacing="1" width="800">
<tr class='formdata' valign="top">
<td width="150"><%=sacurrencyIds[j]%></td>
<input type="hidden" name="baseCurrency" value='<%=baseCurrency%>'>
<td width="150"><%=saCurr2[j]%>
</td>
<input type="hidden" name="sltdCurrency" value='<%=ts[0]%>'>
<td width="150"><%=saBuyRate[j]%>
</td>
<td width="150"><%=saSellRate[j]%>
</td>
<td width="150"><%=saSpecifiedRate[j]%>
</td>
</tr>
<%	
	}
	}
%>
</table>
<table border="0" width="800" cellpadding="4" cellspacing="1" >
<tr valign="top"> 
<input type="hidden" name="Operation" value="<%=Operation%>">
<td  bgcolor="white" width="800" align="right">

<%
	if(radioChkd.equalsIgnoreCase("I"))
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
<td  bgcolor="white" width="800" align="right"><br>  
 
<input type="button" value="Continue" name="continue" onClick = "goBack()" class='input'>
</td>
</table>
</td>
</tr>
</table>
</table>
</form>


<%
	}
	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME,"ETAConversionFactorViewAllProcess : Exception1 ", exp.toString());   
    logger.error(FILE_NAME+"ETAConversionFactorViewAllProcess : Exception1 "+ exp.toString());   
	}

%>

</body>
</html>
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
	Program Name	:ETACurrencyConversionViewAllLov.jsp
	Module Name		:ETrans
	Task			:CurrencyConversion Master
	Sub Task		:LOV
	Author Name		:Subba Reddy V
	Date Started	:September 12,2001
	Date Completed	:September 13,2001
	Date Modified	:September 12,2001 by Subba Reddy V
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

<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;  
	private static final String FILE_NAME	=	"ETCCurrencyConversionViewAllLov.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	String Terminal=loginbean.getTerminalId();
	InitialContext	ictx = null;
	java.util.Vector currencyid = null;
	int len = 0;
	String str = null;  
	String value = "No Currencies are available";
	try
	{
			ictx = new InitialContext();
			SetUpSessionHome clh 	 = ( SetUpSessionHome )ictx.lookup( "SetUpSessionBean" );
			SetUpSession cl		 = (SetUpSession)clh.create();
			currencyid 							 = cl.getCurrencyView("",loginbean.getTerminalId(),"");
			if(currencyid !=null)
				len = currencyid.size();
		
	}
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME,"ETAConversionFactorViewAllLov.jsp : Exception :  ", ee.toString());	
    logger.error(FILE_NAME+"ETAConversionFactorViewAllLov.jsp : Exception :  "+ ee.toString());	
	}
%>

<html>
<head>
<title>Select CurrencyIds</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
	function sendSelected()
	{
		var str = "";
		var select = document.forms[0].Id.options.selectedIndex;
		if(select==0)
		{
			var lent = document.forms[0].Id.options.length
			for (var i=0; i<lent; i++)
			{
				document.forms[0].Id.options[0].selected = false
				document.forms[0].Id.options[i].selected = true
			}
		}
		var len = document.forms[0].Id.options.length;	
		for(i=0;i<len;i++)
		{
			if(document.data.Id.options[i].selected)
			str = str + "-" + document.data.Id.options[i].value;
		}
		opener.hf=str;
		opener.restart1();	
		self.close();
	}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		sendSelected();
	}
}
</script>
</head>

<body class='formdata' leftmargin="5" topmargin="0" marginwidth="0" marginheight="0">

<form method="POST" action="EAccountsCostSheetReport.jsp" name="data">
<center>
<br><font size="2" face="Verdana"><b>Currency Id: </font></b></td>
<br><br>
<select size="10" name="Id" multiple align="center" class='select' onKeyPress='onEnterKey()'>
<!-- Commented & Added by subrahmanyam for the pbn id:203753 on 23-APR-10 -->
<!-- <option value='ALL' selected>ALL</option> -->
<option value='SelectForAllCurrencies' selected>SelectForAllCurrencies</option>

<%
	for( int i=0;i<len;i++ )
	{
		str = currencyid.elementAt(i).toString();	
%>
<option value="<%=str%>"><%=str%></option>
<%
	}
%>
</select>

<br><br>
<input type=button name="ok" value="Ok" onClick="sendSelected();" class='input'>
<input type=button name="cancel" value="Cancel" onClick="window.close()" class='input'>
</center>
</form>
</body>
</html>


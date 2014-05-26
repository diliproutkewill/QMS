<%
/*
	Program Name	:ETACurrencyLOV.jsp
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
%><jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCurrencyLOV.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	Vector currencyid = null;
	int len 		  = 0;
	String value	  = "No Currencies are available";
	String str		  =	request.getParameter("str");

	String searchString  = null;

	if(request.getParameter("searchString")!=null)
	   searchString = request.getParameter("searchString");
    else
	   searchString = "";
	
	try
	{
			InitialContext initial 					= new InitialContext();
			SetUpSessionHome 	home 	= ( SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
			SetUpSession 		remote 	= (SetUpSession)home.create();
			currencyid 								=  remote.getCurrencyView(searchString,loginbean.getTerminalId(),"");
			if(currencyid != null)
				len = currencyid.size();
	}
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME,"CurrencyLOV.jsp : Exception : ", ee.toString());	
    logger.error(FILE_NAME+"CurrencyLOV.jsp : Exception : "+ ee.toString());	
	}
%>
<HTML>
<HEAD>
<TITLE> Select CurrencyId</TITLE>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT language = "JavaScript">

	function closeWin()
	{
		window.close();
	}

	function  populateCurrencyNames()
	{
<%
	for( int i=0;i<len;i++ )
	{
		String str1 = currencyid.elementAt(i).toString();
%>
		document.forms[0].currencyNames.options[ <%= i %> ] = new Option('<%= str1 %>','<%= str1 %>');
<%
	}
    if(len>0)
	{
%>
        document.forms[0].currencyNames.options[0].selected=true;
        document.forms[0].currencyNames.focus();
<%
    }
%>
}

// This function is used to  check whether user selects atleast one  id from  list provided.
	function setCurrencyNames()
	{
		if(document.forms[0].currencyNames.selectedIndex == -1)
		{
			alert("Please select a CurrencyId")
		}
		else
		{
			var i=parseInt(window.opener.document.webform.hiding.value);
	<%
			//Logger.info(FILE_NAME,"str :"+str);
		if(str.equals("get"))
		{
%>
			window.opener.document.webform.baseCurrency[i].value=document.forms[0].currencyNames.options[document.forms[0].currencyNames.options.selectedIndex].text;       
			window.close();
<%
		}
		else if(str.equals("set"))
		{
%>
			window.opener.document.webform.sltdCurrency[i].value=document.forms[0].currencyNames.options[document.forms[0].currencyNames.options.selectedIndex].text;
			window.close();
<%
		}
%>
		}
	}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		setCurrencyNames();
	}
}

</SCRIPT>
</HEAD>
<BODY class='formdata' onLoad="populateCurrencyNames()">
<form>
<br>
	<b><center>Currency Ids
	</center></b>
<br>
<%
	if(len >0)
	{
%>
<p align="center"><select size="10" name="currencyNames" onKeyPress='onEnterKey()'  class="select">    
</select></p>
<p align="center">
<input type="button" value=" Ok " name="OK" onClick=setCurrencyNames() class="input">
<input type="button" value="Cancel" name="B2" onClick="window.close()" class="input"></p>
<%
	}
	else
	{
%>
<center><textarea rows=6 name="ta" cols="30" class="select"><%= value %></textarea></center><br>
<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input">
<%
	}
%>

</form>
</body>
</html>


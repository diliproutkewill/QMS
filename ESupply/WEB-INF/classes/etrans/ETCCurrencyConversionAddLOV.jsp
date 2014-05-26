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
	Program Name	:ETACurrencyConversionAddLOV.jsp
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
					java.util.ArrayList,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome, 
					com.foursoft.etrans.setup.currency.bean.CurrencyConversion,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig"%>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCurrencyConversionAddLOV.jsp ";
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/><!-- added by rk -->
<%
/**
* This file is invoked when user click on LOV button provided side of currency2 .This file is  used to get the all currencyIds from 
* CountryMaster. This file will interacts with SetUpSessionBean and then calls the method getCurrencyList("ALL")
* which inturn retrive the details.
**/
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	ArrayList currencyid = new ArrayList();
	String surChargeToWhere	=	request.getParameter("toWhere");//Added by Govind for the CR-219973
	int len 					= 0;
	String  currency				=	(request.getParameter("currencyId")!=null)?request.getParameter("currencyId"):(session.getAttribute("CURR")!= null)?(String)session.getAttribute("CURR"):"CurrencyId";//added by silpa.p on 30-06-11
//@@added by kameswari for the WPBN issue-30908
	String  operation			=	(request.getParameter("Operation")!=null)?request.getParameter("Operation"):"";
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"chargeCurrencyId";
	String  index				=	(request.getParameter("index")!=null)?request.getParameter("index"):"1";
	String	searchString		=	(request.getParameter("searchString")!=null)?request.getParameter("searchString"):"";
	String  fromWhere			=	(request.getParameter("fromWhere")!=null)?request.getParameter("fromWhere"):"";
	String str 		  	    = null;  
	String value 	  	    = "No Currencies are available.";
	//String baseCurrIndex = (request.getParameter("baseCurrIndex")!=null)?request.getParameter("baseCurrIndex"):"";
	System.out.println(name);
	 if(request.getParameter("pageNo")!=null)
	{ 
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("currencyid");
			
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
	if("".equals(operation))
		operation	= (String)session.getAttribute("Operation");
	try
	{
	if(listHandler == null)
	{
        //System.out.println("Handler is not null"+operation);
		  	session.setAttribute("CURR",currency);
			requiredAttributes  = new ArrayList();
			requiredAttributes.add(name);
			requiredAttributes.add(index);
			requiredAttributes.add(fromWhere);
			requiredAttributes.add(operation);
			InitialContext initial = new InitialContext();
			SetUpSessionHome clh = ( SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
			SetUpSession cl 	  = (SetUpSession)clh.create();
			if(operation.equals("Add"))
				currencyid 						  = cl.getCurrencyList2("ALL",searchString,loginbean.getTerminalId(),operation);//added by rk
			else	
				currencyid 						  = cl.getCurrencyList("ALL",searchString);
			len = currencyid.size();
	 if(currencyid!=null)
        {
            listHandler                     = new LOVListHandler(currencyid,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("currencyid", listHandler);
            //listHandler = (LOVListHandler)session.getAttribute("currencyid");At line no 93,Unnecessary Retrieval of session 
        }
		}	
	}
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME,"ETAConversionFactorAddLOV.jsp : Exception : ", ee.toString());	
    logger.error(FILE_NAME+"ETAConversionFactorAddLOV.jsp : Exception : "+ ee.toString());	
	}
	String pageNo			= request.getParameter("pageNo");

	if(pageNo == null)
	{
		pageNo = "1";
		listHandler.currentPageIndex = 1;
	}
	else
	{
		listHandler.currentPageIndex = Integer.parseInt(pageNo);
		name		=	(String)requiredAttributes.get(0);
		index		=	(String)requiredAttributes.get(1);
		fromWhere	=	(String)requiredAttributes.get(2);
		operation	=	(String)requiredAttributes.get(3);
	}

	ArrayList	currentPageList	= listHandler.getCurrentPageData();
	String 		fileName	= "ETCCurrencyConversionAddLOV.jsp";  
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
// This function is used to  get currency Ids.

	function  populateCurrencyNames()
	{
<%
		for( int i=0;i<currentPageList.size();i++ )
		{
			str = currentPageList.get(i).toString();	
%>
			document.forms[0].currencyNames.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
<%	
		}
        
		if(currentPageList.size() > 0)
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
			alert("Please select a Currency Id")
		}
		else
		{
			var index =document.forms[0].currencyNames.selectedIndex;
			firstTemp = document.forms[0].currencyNames.options[index].value;
			temp  = firstTemp.substring(0,firstTemp.indexOf(' ['));
			//alert('<%=fromWhere%>')
			//alert('<%=name%>')
			//alert('<%=searchString%>')
<%
			if("buycharges".equals(fromWhere))
			{
%>
				window.opener.document.getElementById("<%=name%>").value	= temp;
               //alert(window.opener.document.getElementById("<%=name%>").value)
<%
			}
			//@@added by kameswari for the WPBN issue-30908
			else if("quote".equals(fromWhere))
			{
%>
	           window.opener.document.getElementById("<%=currency%>").value	= temp;
<%			}
			else if("surchargeCurr".equalsIgnoreCase(request.getParameter("fromWhere"))){%>//Added by Govind for the CR-219973 starts

			window.opener.document.getElementById("<%=surChargeToWhere%>").value=temp;
	<%		}//govind ends
			//@@WPBN-30908
			else
			{
%>
			window.opener.document.forms[0].baseCurrency.value=temp;

<%
			}
%>
				resetValues();
		}
	}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		setCurrencyNames();
	}
}

var closeWindow = 'true';

function setVar()
{
  closeWindow = 'false';
}

function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=currencyid";
	  listHandler=null;
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=currencyid";
 
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateCurrencyNames()"><form> 
<center>
	<b>
		<font size=2 face="Verdana">Currency Ids</font><br>
	</b>
</center>
<%
	if(currentPageList.size() > 0)
	{
%>
	<center>
		<select size="10" name="currencyNames" onDblClick ='setCurrencyNames()' onKeyPress='onEnterKey()' class="select" >    
	</select>
	<center>
		<input type="button" value=" Ok " name="OK" onClick='setCurrencyNames()' class="input">
		<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
	</center>
   <TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
		
<%
	}
	else
	{
%>
	<center>
		<textarea rows=6 name="ta" cols="30" class="select"><%= value %></textarea>
	</center>
	<br>
	<center>
		<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
	</center>
<%
	}
%>
	</form>
	</body>
	</html>
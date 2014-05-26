<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSQuoteEnterId.jsp
	Product Name	: QMS
	Module Name		: 
	Task		    : View/Modify Quote
	Date started	: 26-07-2005
	Date Completed	: 
	Date modified	:  
	Author    		: Sanjay
	Description		: 
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSQuoteEnterId.jsp";
%>
<%
   logger  = Logger.getLogger(FILE_NAME);	
   String operation		= request.getParameter("Operation");
   String loginTerminal = loginbean.getTerminalId();
   String origin		= request.getParameter("origin");
   String destination	= request.getParameter("destination");
   String customerId	= request.getParameter("customerId");
   String quoteId		= request.getParameter("quoteId");
   String shipmentMode	= request.getParameter("shipmentMode");
   String fromWhere		= request.getParameter("from");
  
    String customerName		= request.getParameter("customerName");
   
	
	 String quoteStatus		= request.getParameter("quoteStatus");
	String quoteActive     = request.getParameter("quoteActive");
   String display		= "";

   try
   {
		if((fromWhere==null || (fromWhere!=null && fromWhere.trim().length()==0)) && "View".equalsIgnoreCase(operation))
			display		=	"Detailed View";
		else
			display		=	operation;
   }
   catch(Exception e)
   {
		e.printStackTrace();
		display		=	operation;
   }  
%>
<html>
<head>
<title>Quote - <%=display%></title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
//@@Commented by Kameswari for the WPBN issue-26514
/*function openLocationLov(input)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	
	if(input=="originLoc")
	{
		tabArray = 'LOCATIONID';
		formArray = 'originLoc';	
		
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].originLoc.value+"~'";	
	   
	}
	else if(input=="destLoc")
	{
		tabArray = 'LOCATIONID';
		formArray = 'destLoc';

		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].destLoc.value+"~'";
		
	}
	
	//alert(document.forms[0].formArray.value);

	//Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].formArray.value+"~'";	


	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=750,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
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
function popUpWindow(input)
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var	operation	  ='<%=operation%>';
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;

	if(input=='QuoteId')
	{
		Url='etrans/QMSQuoteIdsLOV.jsp?customerId='+document.forms[0].customerId.value+'&searchString='+document.forms[0].QuoteId.value+'&whereToSet='+input+'&originLoc='+document.forms[0].originLoc.value+'&destLoc='+document.forms[0].destLoc.value+'&operation='+operation+'&shipmentMode='+document.forms[0].shipmentMode.value;
		Options='width=300,height=300,resizable=no';
	}
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';	
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}*/
//@@WPBN issue-26514
//@@Modified by kameswari for the WPBN issue-26514
function popUpWindow(input)
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var	operation	  ='<%=operation%>';
	var quoteId="";
	var companyname="";
	var orginlocation="";
	var destlocation="";
	var mode="";
	var status="";
	var active="";
	var multiQuoteFilter = " and  is_multi_quote = 'N' "; // Added by kishore For multiQuoteFilter
	
	if(operation=='Modify')
	{
		var tabArray = 'QUOTE_ID,COMPANYNAME,SHIPMENTMODE,ORIGIN_LOCATION,DEST_LOCATION,CUSTOMER_ID';
	var formArray = 'QuoteId,CustomerName,shipmentMode,originLoc,destLoc,customerId';
	}
	else{
	var tabArray = 'QUOTE_ID,COMPANYNAME,SHIPMENTMODE,ORIGIN_LOCATION,DEST_LOCATION,QUOTE_STATUS,CUSTOMER_ID,ACTIVE_FLAG';
	var formArray = 'QuoteId,CustomerName,shipmentMode,originLoc,destLoc,Status,customerId,ActiveFlag';
	}
	var lovWhere	=	"";
	var val=document.forms[0].shipmentMode.value;
	var valStatus="";
	var valActive="";
	//@@ Commented & Added by subrahmanyam for the pbn id:204096
//if(operation!='Modify' && document.forms[0].Status.value!="")
if(operation!='Modify' )
	{
valStatus=document.forms[0].Status.value;
valActive=document.forms[0].ActiveFlag.value;
if(valActive=="Active" || valActive=="ACTIVE" || valActive=="A")
    valActive="A";
else if(valActive=="Inactive" || valActive=="INACTIVE" || valActive=="I")
  valActive="I";
if(valStatus=="Pending" || valStatus=="PENDING" || valStatus=="P")
    valStatus="PEN";
else if(valStatus=="Positive" || valStatus=="POSITIVE" || valStatus=="POS")
	valStatus="ACC";
else if(valStatus=="Rejected" || valStatus=="REJECTED" || valStatus=="R")
	valStatus="REJ";
else if(valStatus=="Generated" || valStatus=="GENERATED" || valStatus=="G")
	valStatus="GEN";
else if(valStatus=="Approved" || valStatus=="APPROVED" || valStatus=="A")
     valStatus="APP";
else if(valStatus=="Queued" || valStatus=="QUEUED" || valStatus=="Q")
	valStatus="QUE";  
else if(valStatus=="NEGATIVE" || valStatus=="Negative" || valStatus=="N")
	valStatus="NAC";  
//alert(valActive)
	}
      
	var valMode="1,2,3,4,5,6,7" 
		                  if(val=='AIR' || val=='1')
										{
											valMode	=	"1,3,5,7";
										}
										else if(val=='SEA' || val=='2')
										{
											valMode	=	"2,3,6,7";
										}
										else if(val=='TRUCK' || val=='4')
										{
											valMode	=	"4,5,6,7";
										}
if(document.forms[0].QuoteId.value!="")
	{
	quoteId="QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~'";
	}
	else
	quoteId="QUOTE_ID";
if(document.forms[0].CustomerName.value!="")
	{
//@@ Commented & Added by subrahmanyam for the wpbn id: 215432 on 24/08/10
//companyname=" and COMPANYNAME LIKE '"+document.forms[0].CustomerName.value+"~'";
companyname=" and COMPANYNAME LIKE '";
var companyname1	= escape(document.forms[0].CustomerName.value);
companyname	=	companyname+companyname1+"~'";
//Ended for 215432
	}
	else
companyname="";

	if(document.forms[0].destLoc.value!="")
	{
destlocation=" and  DEST_LOCATION LIKE'"+document.forms[0].destLoc.value+"~'";
	}
	else
destlocation="";
	if(document.forms[0].originLoc.value!="")
	{
orginlocation=" and  ORIGIN_LOCATION LIKE '"+document.forms[0].originLoc.value+"~'";
	}
	else
orginlocation="";
	if(valActive!="")
	{
active=" AND ACTIVE_FLAG LIKE '"+valActive+"'";
	}
	else
active="";
	if(valMode!="")
	{
mode=" and SHIPMENTMODE IN("+valMode+")";
	}
	else
mode="";
	
	if(operation=='Modify')
	{
		//Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTEMODIFY&search= where QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~'and  COMPANYNAME LIKE '"+document.forms[0].CustomerName.value+"~' and  ORIGIN_LOCATION LIKE '"+document.forms[0].originLoc.value+"~' and  DEST_LOCATION LIKE'"+document.forms[0].destLoc.value+"~'  and SHIPMENTMODE IN("+valMode+")";
		//@@ Commented & Added by subrahmanyam for wpbn id: 215432 on 24/08/10
//	Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTEMODIFY&search= where QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~'"+companyname+" "+orginlocation+" "+destlocation+" "+mode+" ";
		Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTEMODIFY&search="+encodeURIComponent(" where QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~'"+multiQuoteFilter+companyname+" "+orginlocation+" "+destlocation+" "+mode+" "); // Modified by kishore For multiQuoteFilter
//@@ Ended for 215432
	}
	else if(operation=='Copy')
	{
		if(document.forms[0].Status.value!="")
	{
		status="and QUOTE_STATUS  LIKE '"+valStatus+"'";
	}
	else
		status="";
		//Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER_VIEW&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTECOPY&search= where QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~'and  COMPANYNAME LIKE '"+document.forms[0].CustomerName.value+"~' and  ORIGIN_LOCATION LIKE '"+document.forms[0].originLoc.value+"~' and  DEST_LOCATION LIKE'"+document.forms[0].destLoc.value+"~' and QUOTE_STATUS  LIKE '"+document.forms[0].Status.value+"~' and SHIPMENTMODE IN("+valMode+")";	
		//@@ Modified by subrahmanyam for the wpbn id: 215432 on 24/08/10
		Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER_VIEW&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTECOPY&search="+encodeURIComponent(" where QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~' "+multiQuoteFilter+companyname+" "+orginlocation+" "+destlocation+" "+status+" "+mode+" "+active+""); // Modified by kishore For multiQuoteFilter
	}
	else
	{
		if(document.forms[0].Status.value!="")
	{
	
		status="and QUOTE_STATUS  LIKE '"+valStatus+"'";
	}
	else
		status="";
		//Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER_VIEW&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTEVIEW&search= where QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~' and  COMPANYNAME LIKE '"+document.forms[0].CustomerName.value+"~' and  ORIGIN_LOCATION LIKE '"+document.forms[0].originLoc.value+"~' and  DEST_LOCATION LIKE'"+document.forms[0].destLoc.value+"~' and QUOTE_STATUS  LIKE '"+document.forms[0].Status.value+"~' and SHIPMENTMODE IN("+valMode+") ";	 
	
	//@@ Modified by subrahmanyam for the wpbn id: 215432 on 24/08/10
	Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER_VIEW&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTEVIEW&search= "+encodeURIComponent(" where QUOTE_ID LIKE '"+document.forms[0].QuoteId.value+"~' "+multiQuoteFilter+companyname+" "+orginlocation+" "+destlocation+" "+status+" "+mode+" "+active+"");	 // Modified by kishore For multiQuoteFilter
	}

	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 }
 //@@WPBN issue-26514
function chr(input)
{
	s = input.value;
	//filteredValues = "''~!@#$%^&*()_-+=|\:;<>./?"; // Commented by subrahmanyam for the enhancement #146971 on 03/12/08
	filteredValues = "''~!@#$%^*()-+=|\:;<>./?"; // Added by subrahmanyam for the enhancement #146971 on 03/12/08
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
function validate()
{
	if(document.forms[0].QuoteId.value.length==0)
	{
		alert('Please Enter the Quote Id');
		document.forms[0].QuoteId.focus();
		return false;
	}
	document.forms[0].action='QMSQuoteController';
document.forms[0].submit();
}

// Added By Kishroe Podili for the Multi Quote Validation Ajax Call 
function checkAjaxValidSingleQuote()
	{

	var value = document.forms[0].QuoteId.value;

	//alert(value)
   
	if(value !='')
		{
  
           var url='';
		  	url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=validQuote&multiQuote=N&id='+value;
		  
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }
		//alert(url)
        req.open("GET",url,true);
		req.onreadystatechange = callbackQuoteValid;
		req.send(null);
		}
}

function callbackQuoteValid()
	{
	if(req.readyState == 4) {
        if (req.status == 200) 
		{	  
	var response = req.responseText.toString();	
		if(response.length>0){
			alert("Single Quote Id "+response);
				document.forms[0].QuoteId.value="";
			document.forms[0].QuoteId.focus();
		}
	}
	   }
	}

// End Of  Kishroe Podili for the Multi Quote Validation Ajax Call 


</script>
</head>
<body bgcolor="#FFFFFF">
<form   method="post" >
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr bgcolor="#FFFFFF" valign="top"> 
        <td>
         <table width="100%" cellpadding="4" cellspacing="0">
			<tr valign="top" class='formlabel'>
			   <td  width="903">Quote - <%=display%> </td>
			   <td align="right">QS1060211</td>
			</tr>
	     </table>
<%

		if(request.getAttribute("errors")!=null)
		{
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
				<tr color="#FFFFFF">
				<td colspan="8">
					<font face="Verdana" size="2" color='red'><b>The form has not been submitted because of the following error(s):</b><br><br>
					<%=request.getAttribute("errors")%></font>
				</td>
			</tr>
			</table>
<%
		}
%>
<table width="100%" cellpadding="4" cellspacing="1">
			<tr class='formdata'><td colspan="10"><b>Enter Quote Id to <%=operation%> the Quote:</b>
		</td></tr>
			<tr class='formdata' >
			<!--@@Commented by Kameswari for the WPBN issue-26514-->
				<!--<td>Shipment Mode:</td>
				<td>Origin:</td>				
				<td>Destination:</td>				
				<td>Customer Id:</td>-->	
				
             
			<!--</tr>
			<tr class='formdata'>
				<td>
					<select class='select' name='shipmentMode'>
						<option value='' <%//="0".equalsIgnoreCase(shipmentMode)?"selected":""%>></option>
						<option value='1' <%//="1".equalsIgnoreCase(shipmentMode)?"selected":""%>>Air</option>
						<option value='2' <%//="2".equalsIgnoreCase(shipmentMode)?"selected":""%>>Sea</option>
						<option value='4' <%//="4".equalsIgnoreCase(shipmentMode)?"selected":""%>>Truck</option>
					</select>
				</td>
				<td>
					<input type="text" name="originLoc" value='<%//=origin!=null?origin:""%>' class='text' size="10" onblur='this.value=this.value.toUpperCase()' maxlength='100'>
					&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='openLocationLov("originLoc")'>
					<input  type="hidden" name="originLocationName">
				</td>
				
				<td>
					<input type="text" name="destLoc" value='<%//=destination!=null?destination:""%>' class='text' size="10" onblur='this.value=this.value.toUpperCase()' maxlength='5'>
					&nbsp;<input type="button" value="..." name="destinationLOV"  class='input' onclick='openLocationLov("destLoc")'>
					<input  type="hidden" name="destLocationName">
				</td>
				<td>
					<input class="text" maxLength="25" name="customerId"  size="12" onBlur="this.value=this.value.toUpperCase()" value='<%//=customerId!=null?customerId:""%>'>
				<input class="input" name="custLovBut"  type="button" value="..." row="0" onclick="openCustomerLov()">
			    </td>-->
				<!-- @@WPBN issue-26514-->
				<td colspan="1">Quote Id:<font color="#FF0000">*</font></td>
				<td colspan="1">Customer Name:</td>
				<td colspan="1">Shipment Mode:</td>
				<td colspan="1">Origin:</td>
				<td colspan="1">Destination:</td>
				<% if("Modify".equalsIgnoreCase(operation)){%>
				<td></td><td></td> <%} else{%>
				<td colspan="1">Status:</td>
				<td colspan="2">Active Flag:</td>
				<%}%>
				</tr>
				<tr class='formdata'>
				<td colspan="1">
					<input class="text" maxLength="20" name="QuoteId" id="quoteid" value='<%=quoteId!=null?quoteId:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this); checkAjaxValidSingleQuote(); ">
					
			</td>
			<!--@@Added by VLAKSHMI for the WPBN issue-154389-->
<td colspan="1">
					<input class="text" maxLength="20" name="CustomerName" id="CustomerName" value='<%=customerName!=null?customerName:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)">
					
			</td>
			</td>
<td colspan="1">
					<input class="text" maxLength="20" name="shipmentMode" id="shipmentMode" value='<%=shipmentMode!=null?shipmentMode:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)">
					
			</td>
			
			</td>
<td colspan="1">
					<input class="text" maxLength="20" name="originLoc" id="originLoc" value='<%=origin!=null?origin:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)">
					
			</td>
				
			</td>
<td colspan="1">
					<input class="text" maxLength="20" name="destLoc" id="destLoc" value='<%=destination!=null?destination:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)">
			
			</td>
			</td>
			<!--@@WPBN issue-154389-->
			<% if("Modify".equalsIgnoreCase(operation)){%>
			<td colspan="2">
			<input class="input" type="button" value="..." onclick='popUpWindow("QuoteId")'>		
			</td>
	<% }else{%>
<td colspan="1">
					<input class="text" maxLength="20" name="Status" id="Status" value='<%=quoteStatus!=null?quoteStatus:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)">
					</td>
			<td colspan="2">
					<input class="text" maxLength="15" name="ActiveFlag" id="ActiveFlag" value='<%=quoteActive!=null?quoteActive:""%>'  size="10" onBlur="this.value=this.value.toUpperCase();chr(this)">
			<input class="input" type="button" value="..." onclick='popUpWindow("QuoteId")'>		
			</td>

		<%}%>	
			</tr>
      		<%if("summary".equalsIgnoreCase(request.getParameter("from")))
	       {%>
		   <tr class='formdata'>
		  <td colspan='1'>Print: <INPUT  TYPE="checkbox"  name='print' checked/></td>
			  <td colspan='1'>E-Mail: <INPUT  TYPE="checkbox"name='email'/></td>
			  <td colspan='1'>Fax: <INPUT  TYPE="checkbox"  name='fax'/></td>
              <td>&nbsp;</td>
			  <td>&nbsp;</td>
			  <td>&nbsp;</td>
			  <td>&nbsp;</td>
			  <td>&nbsp;</td>
			</tr>
 <%   }%>
			<tr class='denotes'>   
			
				<td colspan='5'> 
					<font color='red'> * 
					</font>Denotes Mandatory Field
				</td>
				<td > 
					<input type="button" class='input' name="next" value="Next&gt;&gt;" onClick="validate()">
				</td>
				<td>
			    	<input name=Reset type=reset value="Reset&gt;&gt" class='input'>
				</td>
			</tr>
			<INPUT TYPE="hidden" name='customerId' id="customerId" value='<%=request.getParameter("customerId")!=null?request.getParameter("customerId"):""%>'>
			<INPUT TYPE="hidden" name='Operation' value='<%=request.getParameter("Operation")%>'>
			<INPUT TYPE="hidden" name='from' value='<%=request.getParameter("from")!=null?request.getParameter("from"):""%>'>
				<%
      if("summary".equalsIgnoreCase(request.getParameter("from")))
      {%>
			 <INPUT TYPE="hidden" name='subOperation' value='Report'>
 <%   }
      else
      {%>
      <INPUT TYPE="hidden" name='subOperation' value='EnterId'>
    <%}%>
      <INPUT TYPE="hidden" name='pdf' value='PDF'>  <!--@@Added by subrahmanyam for the WPBN issue-146460-->
	</table>
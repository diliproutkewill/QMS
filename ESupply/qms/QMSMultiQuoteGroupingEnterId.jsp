<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSQuoteGroupingEnterId.jsp
	Product Name	: QMS
	Module Name		: 
	Task		    : Grouping
	Date started	: 17-11-2005
	Date Completed	: 
	Date modified	:  
	Author    		: Govind
	Description		: 
	Actor           :
	Related Document: 

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
	private static final String FILE_NAME="QMSQuoteGroupingEnterId.jsp";
%>
<%
   logger  = Logger.getLogger(FILE_NAME);	
   String operation		= request.getParameter("Operation");
   String loginTerminal = loginbean.getTerminalId();
   String origin		= request.getParameter("origin");
   String destination	= request.getParameter("destination");
   String customerId	= request.getParameter("customerId");
   String quoteId		= request.getParameter("quoteId");
%>
<html>
<head>
<title>Recommended Sell Rates Master</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
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
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";

	if(input=='QuoteId')
	{
	   if(document.forms[0].customerId.value.length==0)
		{
			alert("Please Enter Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
	   var origin		 = "";
       var destination	 = "";
       var destAppend	 = "";
	   var originAppend  = "";
	   var quoteType	 = document.forms[0].QuoteType.options.value;
	   var shipModeStr;
	   var shipMode;
		
	   shipModeStr	= quoteType.substring(0,quoteType.lastIndexOf(' '));

	   if(shipModeStr=='Air')
		   shipMode	=	"1";
	   else if(shipModeStr=='Sea')
		   shipMode	=	"2";
	   else
		   shipMode	=	"4";
       
	   if((document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Air Export') || (document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Sea Export') || 
	   (document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Truck Export'))
		{
	       origin      = document.forms[0].originLoc.value;
	       destination = document.forms[0].destLoc;
		   for(m=0;m<destination.options.length;m++)
		   destAppend=destination.options[m].value+','+destAppend;
		   
		   destAppend=destAppend.substring(0,(destAppend.length-1));
		  Url='etrans/QMSQuoteIdsLOV1.jsp?typeQuote=multiquote&customerId='+document.forms[0].customerId.value+'&searchString='+document.forms[0].QuoteId.value+'&whereToSet='+input+'&originLoc='+origin+'&destLoc='+destAppend+'&operation='+operation+'&quoteType='+shipMode;
		   Options='width=600,height=300,resizable=no';   
		
		}
	    else
        {
		   origin      = document.forms[0].originLoc;
	       destination = document.forms[0].destLoc.value;
		   for(m=0;m<origin.options.length;m++)
		   originAppend=origin.options[m].value+','+originAppend;
		   
		   originAppend=originAppend.substring(0,(originAppend.length-1)); Url='etrans/QMSQuoteIdsLOV1.jsp?typeQuote=multiquote&customerId='+document.forms[0].customerId.value+'&searchString='+document.forms[0].QuoteId.value+'&whereToSet='+input+'&originLoc='+originAppend+'&destLoc='+destination+'&operation='+operation+'&quoteType='+shipMode;
		   Options='width=600,height=300,resizable=no';
		
		}
		
		
	}
	else if(input=='customerLov')
	{
		Url='ETAdvancedLOVController?entity=Customer&formfield=customerId&operation=Add&mode=1&type=single&terminalId='+terminalId+'&fromWhere=QuoteGroup';
		Options='width=820,height=600,resizable=no';
	}	
	else if(input=='originLoc')
	{
	    var shipmentMode    =   document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value;
		shipmentMode	    =	shipmentMode.substring(0,4);
		
			if((document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Air Export') || (document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Sea Export') ||
			(document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Truck Export'))
			{
				tabArray = 'LOCATIONID';
				formArray = 'originLoc';	
		
				Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shipmentMode+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].originLoc.value+"~'";
			}
		
			else
			{
        		var Options='scrollbar=yes,width=700,height=300,resizable=no';
			 //var searchString = (input=='originLoc')?(document.forms[0].originLoc.value):(document.forms[0].destLoc.value);
		     //var searchString2= (input=='origin')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
				var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+input+'&searchString=&searchString2=&shipmentMode='+shipmentMode;
			}

	}
	else if(input=='destLoc')
	{		      			
		  var shipmentMode    =   document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value;
		  shipmentMode	    =	shipmentMode.substring(0,4);
			  
		   if((document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Air Export') || (document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Sea Export') ||
		   (document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value=='Sea Export'))
			{
				
			  var Options='scrollbar=yes,width=700,height=300,resizable=no';
			  //var searchString = (input=='originLoc')?(document.forms[0].originLoc.value):(document.forms[0].destLoc.value);
			  //var searchString2=(input=='origin')?(document.forms[0].originCountry.value):(document.forms[0].destinationCountry.value);
			  var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+input+'&searchString=&searchString2=&shipmentMode='+shipmentMode;
					  
			}
			else
			{
			 tabArray = 'LOCATIONID';
			 formArray = 'destLoc';

			 Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+shipmentMode+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].destLoc.value+"~'";
			
			}
	}
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';	
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}
function chr(input)
{
	s = input.value;
	filteredValues = "''~!@#$%^&*()_-+=|\:;<>./?";
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

function changeLocations()
{
   var QuoteType=document.forms[0].QuoteType.options[document.forms[0].QuoteType.selectedIndex].value;
   
   if((QuoteType=='Air Export') || (QuoteType=='Sea Export') || (QuoteType=='Truck Export'))
   {
     document.getElementById("Locations").innerHTML='<input type="text" name="originLoc" value="" class="text" size="12" maxlength="100" onBlur=toUpperCase("originLoc");chr(document.forms[0].originLoc);>';
     
	 document.getElementById("Location").innerHTML='<select name="destLoc" multiple class="select" size="5" onKeyPress	="">				    </select>';
   }
   else
   {
     document.getElementById("Locations").innerHTML='<select name="originLoc" multiple class="select" size="5" onKeyPress	="">				    </select>';
     
	 document.getElementById("Location").innerHTML='<input type="text" name="destLoc" value="" class="text" size="12" maxlength="100" onBlur=toUpperCase("destLoc");chr(document.forms[0].destLoc);>';
   }
 }

function showLocationValues(obj,where)
{
	
	var data="";
	document.getElementById(where).length=0;
	for( i=0;i<obj.length;i++)
	{
		if(where=='QuoteId')
    		   temp=obj[i].value;
		else
        { 
			firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		if(where=="serviceLevelId")
			lastIndex	=	firstTemp.indexOf(' [');	
		else
			lastIndex	=	firstTemp.indexOf('[');
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
			if(where=="originCountry"||where=="destinationCountry")
			{
				if(data!="")
				data=data+","+temp1;
				else
				data=temp1;
			}
			else
			{
				if(data!="")
					data=data+","+temp;
				else	
					data=temp;
			}   
			
	    }
		document.getElementById(where).options[i]=new Option(temp,temp);  
	    document.getElementById(where).options[i].selected=true;  
	 }
	 
}
function mandatory()
{

		if(document.forms[0].customerId.value.length==0)
		{
			alert("Please Enter Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
		if(document.forms[0].QuoteId.options.length==0)
		{
			alert("Please Enter Quote Id");
			document.forms[0].QuoteId.focus();
			return false;
		}
  
 return true;
}  
function toUpperCase(obj)
{
  document.getElementById(obj).value=document.getElementById(obj).value.toUpperCase();
}
</script>
</head>
<body bgcolor="#FFFFFF" onLoad="changeLocations()">
<form   method="post" action="QMSMultiQuoteController" onSubmit="return mandatory()">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr bgcolor="#FFFFFF" valign="top"> 
        <td>
         <table width="100%" cellpadding="4" cellspacing="0">
			<tr valign="top" class='formlabel'>
			   <!-- Commented by Anusha V -->
			   <!-- <td colspan="12" width="903">Quote - Group</td> -->
			   <!-- Added by Anusha V -->
			   <td colspan="12" width="903">MultiQuote - Group</td>
			   <td align="right">QS1060211</td>
			</tr>
	     </table>

         <table width="100%" cellpadding="4" cellspacing="1">
			
			<tr class='formheader'> 
				<td></td>
				<td colspan="4">&nbsp;&nbsp;Quote Type:<font color="#ff0000">*</font></td>
			</tr>
	        <tr class='formdata'> 
				<td></td>
				<td colspan="4">&nbsp;
				<select name='QuoteType' class='select' size=1 onChange="changeLocations()">
                <option value="Air Export">Air Export</option>
                <option value="Air Import">Air Import</option>
				<option value="Sea Export">Sea Export</option>
				<option value="Sea Import">Sea Import</option>
				<option value="Truck Export">Truck Export</option>
				<option value="Truck Import">Truck Import</option>
              </select>
				</td>
            </tr>
            <tr class='formheader'> 
				<td></td>
				<td>Customer Id:<font color="#ff0000">*</font></td>				
				<td>Origin:</td>				
				<td>Destination:</td>				
				<td>Quote Id:<font color="#ff0000">*</font></td>
     		</tr>
			<tr class='formdata'> 
				<td></td>
				<td>
					<input class="text" maxLength="16" name="customerId"  size="12" onBlur="this.value=this.value.toUpperCase();chr(document.forms[0].customerId)" value='<%=customerId!=null?customerId:""%>'>
				    <input class="input" name="custLovBut"  type="button" value="..." row="0" onclick="openCustomerLov()">
				</td>
				<td>
				    <span id="Locations"></span>&nbsp;<input type="button" value="..." name="originLOV"  class="input" onclick="popUpWindow('originLoc')">
                </td>
				<td>
				    <span id="Location"></span>&nbsp;<input type="button" value="..." name="destinationLOV" class="input" onclick="popUpWindow('destLoc')">
			    </td>				
				<td>
					<select name='QuoteId' multiple class='select' size='5' onKeyPress	="">
				    </select>
					<input type	='button' name='button' class ='input' value ="..." Onclick ="popUpWindow('QuoteId')">
				 </td>
			 </tr>
			<tr class='denotes'>             
				<td colspan='4'> 
					<font color='red'> * 
					</font>Denotes Mandatory Field
				</td>
				<td colspan='1' align='right'> 
	            <input type="submit" class='input' name="next" value="Next&gt;&gt;">
	            <input type="reset" class='input' name="next" value="Reset">
				</td>
			</tr>
			<%System.out.println("operation is  ----------------->"+request.getParameter("Operation"));%>
			<input type="hidden" name='Operation' value='<%=request.getParameter("Operation")%>'>
	        <input type="hidden" name='subOperation' value='charges'>  
	</table>
	  
	</form>
  </body>
</html>
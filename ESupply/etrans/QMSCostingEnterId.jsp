
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSCostingEnterId.jsp
Product Name	: QMS
Module Name		: Costing
Task		    : Adding
Date started	: 
Date Completed	: 
Date modified	:  
Author    		: 
Description		: The application "Adding
Actor           :
Related Document: CR_DHLQMS_1008
--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSCostingEnterId.jsp";

%>

<%

	String operation	=	request.getParameter("Operation");
	String terminalId	=	loginbean.getTerminalId();
	String errorMsg	=	(String)request.getAttribute("errorMsg");
	String contextPath	=	request.getContextPath();
	String origin		=	request.getParameter("origin");
	String destination	=	request.getParameter("destination");
	String advantage	=	request.getParameter("advantage");
	String customerid	=	request.getParameter("customerid");
	String quoteid		=	request.getParameter("quoteid");
	String validtill	=	request.getParameter("validtill");
	String shipmentMode	=	request.getParameter("shipmentMode");
   String customerName		= request.getParameter("customerName");
   String fromPage		= request.getParameter("fromPage");
	String  isMutliQuote = (String)session.getAttribute("isMultiQuote");//added by silpa.p on 6-06-11 for costing redirect
	session.removeAttribute("isMultiQuote");//ended

   
  if(advantage==null)
		advantage = "Customer";
%>

<html>
<head>
<title>Costing - Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<jsp:include page="ETDateValidation.jsp" >
	<jsp:param name="format" value="<%=loginbean.getUserPreferences().getDateFormat()%>"/>
</jsp:include>
<script>
		var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;

	function showLocation(obj)
	{
		
		myUrl=  '<%=contextPath%>/etrans/ETCLOVLocationIds.jsp?wheretoset='+obj+'&searchString='+document.getElementById(obj).value.toUpperCase();
		var Win	   =  open(myUrl,'Doc',Features);
	}

	function showcustomer(obj)
	{
		var Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
		var Options  = 'width=820,height=600,resizable=no';
		var Features =  Bars+','+Options;
      Url='ETAdvancedLOVController?entity=Customer&formfield=customerid&operation=<%=operation%>&mode=1&type=single&terminalId=<%=terminalId%>&fromWhere'+obj;

		var Win	   =  open(Url,'Doc2',Features);
		
		
	}
	
	function showquoteid(input)
	{
		

		if(document.forms[0].customerid.value=='')
		{
			alert("Please enter the Customer Id.");
			document.forms[0].customerid.focus();
			return false;
		}
		Url='etrans/QMSQuoteIdsLOV.jsp?customerId='+document.forms[0].customerid.value+'&searchString='+document.forms[0].quoteid.value+'&whereToSet='+input+'&operation=costingAdd&originLoc='+document.forms[0].origin.value+'&destLoc='+document.forms[0].destination.value;
	   var Win	   =  open(Url,'Doc',Features);
	}

function openQuoteidLov()
{
	var tabArray = 'QUOTE_ID,COMPANYNAME,SHIPMENTMODE,ORIGIN_LOCATION,DEST_LOCATION,CUSTOMER_ID';
	var formArray = 'quoteid,CustomerName,shipmentMode,origin,destination,customerid';
	var lovWhere	=	"";
	var val=document.forms[0].shipmentMode.value;
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
	
	Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=COSTINGQUOTEID&shipmentMode="+document.forms[0].shipmentMode.value+"&search= where QUOTE_ID LIKE '"+document.forms[0].quoteid.value+"~'and  COMPANYNAME LIKE '"+document.forms[0].CustomerName.value+"~' and  ORIGIN_LOCATION LIKE '"+document.forms[0].origin.value+"~' and  DEST_LOCATION LIKE'"+document.forms[0].destination.value+"~'  and SHIPMENTMODE IN("+valMode+")";

	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 }	 //Added by Rakesh
 function openMultiQuoteidLov()
{		
	var tabArray = 'QUOTE_ID,COMPANYNAME,SHIPMENTMODE,ORIGIN_LOCATION,DEST_LOCATION,CUSTOMER_ID';
	var formArray = 'quoteid,CustomerName,shipmentMode,origin,destination,customerid';
	var lovWhere	=	"";
	var val=document.forms[0].shipmentMode.value;
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
	
 var Quotetype = '<%=request.getParameter("isMultiQuote")!= null?request.getParameter("isMultiQuote"):isMutliQuote%>';

	
	
	Url		="qms/ListOfValues.jsp?lovid=QUOTE_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=COSTINGQUOTEID&multiQuote="+Quotetype+"&shipmentMode="+document.forms[0].shipmentMode.value+"&search= where QUOTE_ID LIKE '"+document.forms[0].quoteid.value+"~'and  COMPANYNAME LIKE '"+document.forms[0].CustomerName.value+"~' and  ORIGIN_LOCATION LIKE '"+document.forms[0].origin.value+"~' and  DEST_LOCATION LIKE'"+document.forms[0].destination.value+"~'  and SHIPMENTMODE IN("+valMode+")";

	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 } 	 //Ended by Rakesh
function chr(input)
{
	s = input.value;
	//filteredValues = "''~!@#$%^&*()_-+=|\:;<>./?"; // Commented by subrahmanyam for the enhancement #146971 on 03/12/08
	filteredValues = "''~!@#$%^&*()-+=|\:;<>./?"; // Added by subrahmanyam for the enhancement #146971 on 03/12/08
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
</script>
</head>
<body>
<form action ='QMSCostingController?Operation="Add"&subOperation="enter"' >
	<table  width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor='#FFFFFF'>
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="100%"  cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> Costing - Add</td>
						<td align=right>QS1050621
						</td></tr></table>
			</td>
		  </tr>

		</table>

		<%
		if(errorMsg!=null && !"".equals(errorMsg))
		{
			
		%>
			<table width="100%"  cellspacing="1" cellpadding="4">
			 <tr valign="top" class='formdata'>
			  <td>
					<font  color=#ff0000><%=errorMsg%></font>
			  </td>
			</tr>
			</table>
		<%}
		%>
		<table border='0' width="100%" cellpadding="0" cellspacing="1">
		<tr class='formdata'><td colspan="9">
		</td></tr>
			<tr class='formdata'>
			   <td colspan="1">Quote Id:<font color="#FF0000">*</font></td>
			    <td colspan="2">Valid Till:(<%=loginbean.getUserPreferences().getDateFormat()%>)</td>
				<td colspan="1">Customer Name:</td>
				<td colspan="1">Shipment Mode:</td>
                <td colspan="1">Advantage : </td>
				<% if(!(fromPage!=null && "MultiCosting".equals(fromPage))){%>
				<!--<td colspan="1">Origin:</td>
				<td colspan="1">Destination:</td>-->
				<%}else{%> 
				<td colspan="1"></td>
				<td colspan="1"></td>
				<% }%>
				</tr>
			<tr class='formdata'>
				<!-- <td>Origin Location:</td>
				<td><input type='text' class='text' name="origin" id="origin" value ='<%=(origin!=null)?origin:""%>' onblur="this.value=this.value.toUpperCase()" size="6"  >
				<input type="button" class='input' value="..." name="destinationIdLOV" onClick=showLocation('origin')></td>
				<td>Destination Location:</td>
				<td><input type='text' class='text' name="destination" id="destination" value ='<%=(destination!=null)?destination:""%>' onblur="this.value=this.value.toUpperCase()" size="6"  >
				<input type="button" class='input' value="..." name="destinationIdLOV" onClick=showLocation('destination')></td> -->
				
				
				<td colspan="1"><input type='text' class='text' name="quoteid" id="quoteid" value ='<%=(quoteid!=null)?quoteid:""%>' onblur="this.value=this.value.toUpperCase()" size="15"  >
				<!-- <input type="button" class='input' value="..." name="quoteidLOV" onClick=showquoteid('quoteid')> -->
				
				</td>
				
				
				<td colspan="2">
				<input type='text' class='text' name="validtill" id="validtill" value ='<%=(validtill!=null)?validtill:""%>' onblur="dtCheck(this)" size="8"  >
				<input type='button' class='input'  name='b1' value='...' onClick="newWindow('validtill','0','0','')"></td>

				</td>
				<!--@@Added by VLAKSHMI for the WPBN issue-154389-->
<td colspan="1">
					<input class="text" maxLength="20" name="CustomerName" id="CustomerName" value='<%=(customerName!=null)?customerName:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)">
					
			</td>
				</td>
<td colspan="1">
					<input class="text" maxLength="20" name="shipmentMode" id="shipmentMode" value='<%=(shipmentMode!=null)?"1".equals(shipmentMode)?"Air":"2".equals(shipmentMode)?"Sea":"Truck":""%>'    size="15" onBlur="this.value=this.value.toUpperCase();chr(this)"><!--modified by silpa.p on 6-06-11-->
					
					
			</td>

			<td>
			        <select name="advantage" size="1" class="select">
					<option value="DGF"  selected>DGF</option>
					<option value="Customer" >Customer</option>
					</select>
			</td>
                  
			</td>
			<% if (!(fromPage!=null &&( "MultiCosting".equals(fromPage)|| "MultiQuote".equals(fromPage)))){ %>
<td colspan="1">
					<input class="text" maxLength="20" name="origin" id="origin" value='<%=(origin!=null)?origin:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)">
					
			</td>
				
			</td>
<td colspan="2">
					<input class="text" maxLength="20" name="destination" id="destination" value='<%=(destination!=null)?destination:""%>'  size="15" onBlur="this.value=this.value.toUpperCase();chr(this)"> <input type="button" class='input' value="..." name="quoteidLOV" onClick=openQuoteidLov()>					
			</td>
			<%}else{%> 
				<td colspan="1"><input type="hidden" name="origin" id="origin" value='' ><input type="hidden" name="destination" id="destination" value='' > </td>
				<td colspan="1"> <input type="button" class='input' value="..." name="quoteidLOV" onClick=openMultiQuoteidLov()></td> <!--  Added by Rakesh -->
				<% }%>
			
            <!--@@WPBN issue-154389-->
			
			</tr>
			<!-- <tr class='formdata'>
				<td>Customer Id:<font color="#FF0000">*</font></td>
				<td><input type='text' class='text' name="customerid" id="customerid" value ='<%=(customerid!=null)?customerid:""%>' onblur="this.value=this.value.toUpperCase()" size="6"  >
				<input type="button" class='input' value="..." name="customerLOV" onClick=showcustomer('customer')></td> 
				<td>Quote Id:<font color="#FF0000">*</font></td>
				<td><input type='text' class='text' name="quoteid" id="quoteid" value ='<%=(quoteid!=null)?quoteid:""%>' onblur="this.value=this.value.toUpperCase()" size="6"  >
				<input type="button" class='input' value="..." name="quoteidLOV" onClick=showquoteid('quoteid')> 
					<input type="button" class='input' value="..." name="quoteidLOV" onClick=openQuoteidLov()>
				</td>

				<td>Valid Till:
				<input type='text' class='text' name="validtill" id="validtill" value ='<%=(validtill!=null)?validtill:""%>' onblur="dtCheck(this)" size="6"  >
				<input type='button' class='input'  name='b1' value='...' onClick="newWindow('validtill','0','0','')"><%=loginbean.getUserPreferences().getDateFormat()%></td>
							
			</tr> -->
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory<br>
            </td>
            <td valign="top" align="right">
                <input type="submit" value="Next>>" name="submit" class="input">
				<input type="reset" value="Reset" name="reset" class="input">
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='enter'>
			<!--<INPUT TYPE="hidden" name='fromPage' id="fromPage" value='<%=fromPage%>'>-->
				 <input type='hidden' name="fromPage" id="fromPage" value ='MultiCosting' ><!--addedby silpa.p on 6-06-11 -->
				<INPUT TYPE="hidden" name='customerid' id="customerid" value='<%=request.getParameter("customerid")!=null?request.getParameter("customerid"):""%>'>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>


</form>
</body>
</html>

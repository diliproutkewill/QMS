<%--
/**
	Program Name	: ETAViewAllAdmin.jsp
	Module Name	: ETrans
	Task		: Reports	
	Sub Task	: Screens for 'ViewAll' Reports. 
	Author Name	: Rizwan.
	Date Started	: December 4,2001
	Date Completed	: December 5,2001
	Date Modified	: 
*/
--%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
		String view	=    request.getParameter("View");
 
%>		

<html>
<head>
<title>View All </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="../ESEventHandler.jsp" %>
<script language='javascript'>

	function selectAll()
	{
	  if(document.forms[0].select.checked)
	  {
	    for(var i=0;i<document.forms[0].elements.length;i++)
	    {
	     if(document.forms[0].elements[i].type=="checkbox")
	     {
	        document.forms[0].elements[i].checked=true;
	     }
	    }
	  } 
	  else
	  {
	    for(var i=0;i<document.forms[0].elements.length;i++)
	    {
	     if(document.forms[0].elements[i].type=="checkbox")
	      {
	         document.forms[0].elements[i].checked=false;
	      }
	    }
	  } 
	}

 function mandatery()
{
	document.forms[0].Currency1.checked=true;
	document.forms[0].Currency2.checked=true;
	document.forms[0].ConversionFactor.checked=true;
}

function selectAll()
{
  if(document.forms[0].select.checked)
  {
    for(var i=0;i<document.forms[0].elements.length;i++)
	{
	  if(document.forms[0].elements[i].type=="checkbox")
	   {
	     document.forms[0].elements[i].checked=true;
	   }
        }
   } 
 else
  {
    for(var i=0;i<document.forms[0].elements.length;i++)
	{
	  if(document.forms[0].elements[i].type=="checkbox")
	   {
	     document.forms[0].elements[i].checked=false;
	   }
     }
	 document.forms[0].shipmentMode.checked=true;//@@Added for 203873

   } 
 }

</script>

<script language=javascript>

	var windowFeatures 	= 	"directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no,scrollbars=yes,resizable=yes";
	
	var openedWindows	=	new Array();

	function showReport(flag, formObj) 
	 {

		if(flag==true) {	
			
			//Close all open windows
			for(var i=0; i < openedWindows.length; i++) {
				if(openedWindows[i].closed == false) {
					openedWindows[i].close();
				}
			}	
			var numOfOpened		=	0;
			openedWindows	=	new Array();

			var repWin = open("EAReportPage.jsp", "reportWindow", windowFeatures);
			window.repWin;

			while(repWin.frames.length < 2) {}

			formObj.target	=	"reportFrame";
			
			openedWindows[numOfOpened]	=	repWin;
			numOfOpened++;
			
			repWin.opener = self;
	   	 	repWin.focus();
					
			return true;
		} else {
			return false;	
		}
	}
	
	function closeChild() {
		if(openedWindows.length > 0) {
			for(var i=0; i < openedWindows.length; i++) {
				if(openedWindows[i].closed == false) {
					openedWindows[i].close();
				}
			}	
		}
	}
	//onUnload="closeChild();"
function check()
{
	for (var i=0 ; i < document.forms[0].elements.length ; i++)
	{
		if ((document.forms[0].ctype.checked) && (document.forms[0].CTYPE2.checked) ) 
				return true;
	}
		alert("Please select Company Type1 And Company Type2 ");
	return false;   
}  // end of check()

function CheckSelected()
{
	//document.forms[0].CARRIERID.disabled=false;
	for (var i=0 ; i < document.forms[0].elements.length ; i++)
	{
		if(document.forms[0].elements[i].type=="checkbox")
		{
			
			//if(!document.forms[0].elements[i].name=="CARRIERID")
			//{
				if ((document.forms[0].elements[i].checked) && (document.forms[0].elements[i].checked) ) 
						return true;
			//}
		}
	}
		alert("Please Check Atleast One field ");
	return false; 
}

</script>

</head>
<body onUnLoad="closeChild()">
	<table width="800" border="0" cellspacing="0" cellpadding="0" >
 		<tr bgcolor="#FFFFFF"> 
    	<td  >


<%
	
  	if( "CarrierContracts".equals(view))
	{
%>		
	 
		
				<table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel'>Carrier Contracts - View All</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">
				Select the fields you  wish to View:
				</font>
				</td>
				</tr>
				</table>

			    <form method="post" action="air/ETACarrierRegistrationReports.jsp" >

				<table border="0" width="800" cellspacing="0" cellpadding="0" >

				<tr class='formdata'> 
				<td width="50%" align="right">
				<input type="checkbox" name="CARRIERID" value="0" checked disabled>
				</td>
				<td align="left">Carrier Id
				</td>
				</tr>

				<tr class='formdata'>
				<td  width="50%"  align="right">
				<input type="checkbox" name="CARRIERNAME" value="0">
				</td>
				<td  align="left" >Carrier Name
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="CACONTACTID" value="0">
				</td>
				<td align="left">Carrier Contract Id
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right" >
				<input type="checkbox" name="TERMINALID" value="0">
				</td>
				<td align="left">Terminal Id</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="select" value="0" onClick="selectAll()">
				<td align="left">Select All
				</td>
				</tr>
				</table>

				<table border="0" width="800" cellpadding="4" cellspacing="1" height="39">
				<tr> 
				<td valign="top" bgcolor="white" align="right"> 
				<input type="submit" value="Submit" name="Submit"  class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				
				</td>
				</tr>
				</table>

  			    </form>
	

<%
}
else if("ViewTerminal".equals(view))
{
%>


			
				<table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel'><table width="790" border="0" ><tr class='formlabel'><td>Terminal - View All</td><td align=right><%=loginbean.generateUniqueId("ETAViewAllAdmin.jsp",view)%></td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>

			    <form method="post" action="air/ETATerminalMasterReports.jsp" >

				<table border="0" width="800" cellspacing="0" cellpadding="0">

				<tr class='formdata'> 
				<td width="50%" align="right">
				<input type="checkbox" name="TERMINALID" value="0" checked disabled>
				</td>
				<td align="left">Terminal-Id:
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="CITY" value="0">
				</td>
				<td align="left">City:
				</td>
				</tr>

				<tr class='formdata'>
				<td	 width="50%" align="right">
				<input type="checkbox" name="CONTACTNAME" value="0">
				</td>
				<td align="left">Contact Person:
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="DESIGNATION" value="0">
				</td>
				<td align="left">Designation:
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="select" value="0" onClick="selectAll()">
				<td align="left">Select All:
				</td>
				</tr>
				</table>

				<table border="0" width="800" cellpadding="4" cellspacing="1" height="39">
				<tr> 
				<td valign="top" bgcolor="white" align="right"> 
				<input type="hidden" value="Terminal" name="Operation">
				<%//Modified by Sreelakshmi KVA - 20050411 SPETI-5532 //%>
				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			     </form>
		

<%
}
else if("CustomerRegistration".equals(view))
{
	String customer = request.getParameter("Customer");
    String registrationLevel = request.getParameter("registrationLevel");
%>

			
				<table width="800" border="0" cellspacing="1" cellpadding="4"  >
				<tr> 
				<td  class='formlabel'><table width="790" border="0" ><tr class='formlabel'><td>Customer - View All</td><td align=right><%=loginbean.generateUniqueId("ETAViewAllAdmin.jsp",view)%></td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>

			<form method="post" action="air/ETACustomerRegistrationReports.jsp" >

				<table border="0" width="800" cellspacing="0" cellpadding="0">

				<tr class='formdata'> 
				<td width="50%" align="right">
				<input type="checkbox" name="CUSTOMERID" value="0"checked disabled>
				</td>
				<td align="left">Customer Id
				</td>
				</tr>
				 <tr class='formdata'> 
				<td width="50%" align="right">
				<input type="checkbox" name="COMPANYNAME" value="0">
				</td>
				<td align="left">Customer Name
				</td>
				</tr>
				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="CONTACTNAME" value="0">
				</td>
				<td align="left">Contact Name
				</td>
				</tr>

				<!-- <tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="REGISTERED" value="0">
				</td>
				<td align="left">Customer Status
				</td>
				</tr> -->
				<%
					if(customer==null)
					{
				%>
				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="TERMINALID" value="0">
				</td>
				<td align="left">Terminal Id
				</td>
				</tr>
				<%
					}
				%>
				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="CITY" value="0">
				</td>
				<td align="left">City
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="select" value="0" onClick="selectAll()">
				<td align="left">Select All
				</td>
				</tr>
				</table> 

				<table border="0" width="800" cellpadding="4" cellspacing="1" height="39">
				<tr> 
				<td valign="top" bgcolor="white" align="right"> 
				<input type="hidden" value="CustomerReports" name="Operation">
				<input type="hidden" value="<%=registrationLevel%>" name="registrationLevel">
				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				<input type="hidden" name=customer value=<%=customer%> >
				</td>
        <input type="hidden" value="CUSTOMERID" name="SortBy"><!-- Shyam -->
				<input type="hidden" value="ASC" name="SortOrder"><!-- Shyam -->
				<input type="hidden" value="1" name="PageNo"><!-- Shyam -->
				</tr>
				</table>
			    </form>
		

<%
}
else if("CustomerContracts".equalsIgnoreCase(view))
{
%>

		
				<table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel' ><table width="790" border="0" ><tr class='formlabel'><td>Customer Contracts - View All</td><td align=right><%=loginbean.generateUniqueId("ETAViewAllAdmin.jsp",view)%></td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>

			<form method="POST" action="air/ETACustomerContractsReports.jsp" >

				<table border="0" width="800" cellspacing="0" cellpadding="0" >
				<tr class='formdata'> 
				<td width="50%" align="right">
				<input type="checkbox" name="A.CUSTOMERID" value="0">
				</td>
				<td align="left">Customer Id:
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="A.CONTRACTID" value="0">
				</td>
				<td align="left">Contract Id:
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="B.COMPANYNAME" value="0">
				</td>
				<td align="left">Company Name:
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="A.TERMINALID" value="0">
				</td>
				<td align="left">Terminal Id:
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="select" value="0" onClick="selectAll()">
				<td align="left">Select All:
				</td>
				</tr>
				</table>

				<table border="0" width="800" cellpadding="4" cellspacing="1" >
				<tr> 
				<td valign="top" bgcolor="white" align="right"> 
				<input type="hidden" value="CustomerContracts" name="Operation">
				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			     </form>
		


<%
}
 else if("CurrencyConversion".equals(view))
{
%>

		
		<table width="800" border="0" cellspacing="1" cellpadding="4"  >
		<tr> 
		<td  class='formlabel' ><table width="790" border="0" ><tr class='formlabel'><td> Currency Conversion - View All</td><td align=right><%=loginbean.generateUniqueId("ETAViewAllAdmin.jsp",view)%></td></tr></table>
		</td>
		</tr>
		<tr  valign="top"> 
		<td>
		<font face="verdana" size="3">Select the fields you wish to View:
		</font>
		</td>
		</tr>
		</table>
		<form method="POST"  action="CurrencyConversionReports.jsp" >
		<table border="0" width="800" cellspacing="0" cellpadding="0">
		<tr class='formdata'> 
		<td width="50%" align="right">
		<input type="checkbox" name="Currency1" value="0" checked>
		</td>
		<td align="left">Currency-1:
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="Currency2" value="0" checked>
		</td>
		<td align="left">Currency-2:
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="ConversionFactor" value="0" checked>
		</td>
		<td	align="left">Conversion Factor:
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="select" value="0" onClick="selectAll()">
		<td align="left">Select All:
		</td>
		</tr>
		</table>

		<table border="0" width="800" cellpadding="4" cellspacing="1" >
		<tr> 
		<td valign="top" bgcolor="white" align="right"> 
		<input type="hidden" value="Currency" name="Operation">
		<input type="submit" value="Submit" name="Submit" class='input'>
		<input type="reset" value="Reset" name="reset" class='input'>
		
		</td>
		</tr>
		</table>
		</form>
		

<%
}
 else if("CarrierRegistration".equals(view))
{
%>

		
		<table width="800" border="0" cellspacing="1" cellpadding="4"  >
		<tr> 
		<td  class='formlabel' ><table width="790" border="0" ><tr class='formlabel'><td> Carrier - View All</td><td align=right><%=loginbean.generateUniqueId("ETAViewAllAdmin.jsp",view)%></td></tr></table>
		</td>
		</tr>
		<tr  valign="top"> 
		<td>
		<font face="verdana" size="3">Select the fields you wish to View:
		</font>
		</td>
		</tr>
		</table>
		<form method="POST" action="air/ETACarrierRegistrationReports.jsp" onSubmit="return CheckSelected()" >     
		<table border="0" width="800" cellspacing="0" cellpadding="0">
		<tr class='formdata'> 
		<td width="50%" align="right">
		<input type="checkbox" name="CARRIERID" value="0" checked disabled>
		</td>
		<td align="left">Carrier ID
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="CARRIERNAME" value="0">
		</td>
		<td align="left">Carrier Name
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="SHIPMENTNAME" value="0">
		</td>
		<td  align="left">Shipment Mode
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="CITY" value="0">
		</td>
		<td align="left">City
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="select" value="0" onClick="selectAll()">
		<td align="left">Select All
		</td>
		</tr>
		</table>

		<table border="0" width="800" cellpadding="4" cellspacing="1" height="39">
		<tr> 
		<td valign="top" bgcolor="white" align="right"> 
		<input type="hidden" value="CarrierRegistration" name="Operation">
		<input type="submit" value="Submit" name="Submit" class='input' >
		<input type="reset" value="Reset" name="reset" class='input'>
		</td>
		</tr>
		</table>
		</form>
	

<%
}
else if("RevenueSharing".equals(view))
{
%>

		
			<table width="800" border="0" cellspacing="1" cellpadding="4"  >
			<tr > 
			<td class='formlabel' ><table width="790" border="0" ><tr class='formlabel'><td> Profit Sharing - View All</td><td align=right><%=loginbean.generateUniqueId("ETAViewAllAdmin.jsp",view)%></td></tr></table>
			</td>
			</tr>
			<tr  valign="top"> 
			<td>
			<font face="verdana" size="3">Select the fields you wish to View:
			</font>
			</td>
			</tr>
			</table>
			<form method="POST" action="air/ETARevenueSharingReports.jsp" onSubmit="return check();return showReport(true,this );">    

			<table border="0" width="800" cellspacing="0" cellpadding="0">
			<tr class='formdata'> 
			<td width="50%" align="right">
			<input type="checkbox" name="ctype" value="0" checked>
			</td>
			<td align="left">Company Type-1:
			</td>
			</tr>
			<tr class='formdata'>
			<td width="50%" align="right" >
			<input type="checkbox" name="CTYPE2" value="0" checked>
			</td>
			<td align="left">Company Type-2:
			</td>
			</tr>
			<tr class='formdata'>
			<td width="50%" align="right">
			<input type="checkbox" name="code" value="0">
			</td>
			<td align="left">Code:
			</td>
			</tr>
			<tr class='formdata'>
			<td width="50%" align="right">
			<input type="checkbox" name="chargeId" value="0">
			</td>
			<td align="left">Charge-Id:
			</td>
			</tr>
			<tr class='formdata'>
			<td width="50%" align="right">
			<input type="checkbox" name="sharingValue" value="0">
			</td>
			<td align="left">Sharing Value:
			</td>
			</tr>

			<tr class='formdata'>
			<td width="50%" align="right">
			<input type="checkbox" name="indicator" value="0">
			</td>
			<td align="left">Indicator:
			</td>
			</tr>
			<tr class='formdata'>
			<td width="50%" align="right">
			<input type="checkbox" name="select" value="NO" onClick="selectAll()">
			</td>
			<td align="left">Select All:
			</td>
			</tr>
			</table>


			<table border="0" width="800" cellpadding="4" cellspacing="1" height="39">
			<tr> 
			<td valign="top" bgcolor="white" align="right"> 
			<input type ="hidden" value="RevenuSharing" name="Operation">
			

			<% // @@ Murali Modified On 20050429 Regarding SPETI - 5700 %>
			<!-- <input type="submit" value="Submit" name="Submit" class='input'>
			<input type="button" value="<< Back" name="Back" onClick="javascript:history.back()" class='input'>
			-->
			<input type="submit" value="Submit" name="Submit" class='input'>
			<% // @@ Murali Modified On 20050429 Regarding SPETI - 5700 %>
			<input type="reset" value="Reset" name="reset" class='input'>
			</td>
			</tr>
			</table>
			</form>
		
	
<%
}
else if("ContentMaster".equals(view))
{
%>
		<table width="800" border="0" cellspacing="1" cellpadding="4"  >
		<tr> 
		<td  class='formlabel' ><table width="790" border="0" ><tr class='formlabel'><td> Content Master - <%=request.getParameter("Operation")%></td><td align=right>QS1020422</td></tr></table>
		</td>
		</tr>
		<tr  valign="top"> 
		<td>
		<font face="verdana" size="3">Select the fields you wish to View:
		</font>
		</td>
		</tr>
		</table>
		<form method="POST" action="../QMSSetupController" onSubmit="return CheckSelected()" >     
		<table border="0" width="800" cellspacing="0" cellpadding="0">
		<tr class='formdata'> 
		<td width="50%" align="right">
		<input type="checkbox" name="SHIPMENTMODE" id='shipmentMode'value="0" checked disabled>
		</td>
		<td align="left">Shipment Mode
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="HEADERFOOTER" value="0">
		</td>
		<td align="left">Header/Footer
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="CONTENTID" value="0">
		</td>
		<td  align="left">Content Description Id
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="DESCRIPTION" value="0">
		</td>
		<td align="left">Content Description  
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="FLAG" value="0">
		</td>
		<td align="left">Default
		</td>
		</tr>
		<tr class='formdata'>
		<td width="50%" align="right">
		<input type="checkbox" name="select" value="0" onClick="selectAll()">
		<td align="left">Select All
		</td>
		</tr>
		</table>

		<table border="0" width="800" cellpadding="4" cellspacing="1" height="39">
		<tr> 
		<td valign="top" bgcolor="white" align="right"> 
		<input type="hidden" name="Operation" value="<%=request.getParameter("Operation")%>">
		<input type="submit" value="Submit" name="Submit" class='input' >
		<input type="reset" value="Reset" name="reset" class='input'>
		</td>
		</tr>
		</table>
		</form>
<%
}
%>
</td>
</tr>
</table>
</body>
</html>
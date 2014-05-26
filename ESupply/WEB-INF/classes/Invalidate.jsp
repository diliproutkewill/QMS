<%--
/**
	Program Name	: Invalidate.jsp
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
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="ESEventHandler.jsp" %>
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


</script>

</head>
<body onUnLoad="closeChild()">
	<table width="800" border="0" cellspacing="0" cellpadding="0" >
 		<tr bgcolor="#FFFFFF"> 
    	<td  >
<%
	
  	if( view.equals("terminalInvalidate"))
	{
%>						
				
				
				
				<table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel'><table width="790" border="0" ><tr class='formlabel'><td>Terminal - Invalidate</td><td align=right></td><td align="right">QS1222664</td> </tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>

			    <form method="post" action="etrans/TerminalInvalidate.jsp" >

				<table border="0" width="800" cellspacing="0" cellpadding="0">

				<tr class='formdata'> 
				<td width="50%" align="right">
				<input type="checkbox" name="TERMINALID" value="0" checked disabled>
				</td>
				<td align="left">Terminal-Id
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
				<td	 width="50%" align="right">
				<input type="checkbox" name="CONTACTNAME" value="0">
				</td>
				<td align="left">Contact Person
				</td>
				</tr>

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="DESIGNATION" value="0">
				</td>
				<td align="left">Designation
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
				<input type="hidden" value="Terminal" name="Operation">
				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			     </form>

	
<%
}  if ( view.equalsIgnoreCase("serviceLevel") )
		  {
%>
				<table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel'><table width="790" border="0" ><tr class='formlabel'><td>Servicelevel - Invalidate</td><td align=right>QS1222675</td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>
				<form method="post" action="etrans/ServiceLevelInvalidate.jsp">
				<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_SERVICEID" value="NO" checked disabled >
				</td>
				<td  width="50%">	
				Service Level-ID
				
				</td>
				</tr>
				<tr class='formdata' >
				    
				<td align="right" width="50%">
				<input type="checkbox" name="P_SERVICEDESC" value="NO">	
				</td>
				<td  width="50%">	
				Service Level Description
				</td>
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%">
				<input type="checkbox" name="P_REMARKS" value="NO">	
				</td>
				<td  width="50%">	
				Remarks
				</td>	
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

				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			     </form>
<%
	}
			if (view.equalsIgnoreCase("countrymaster") )
	{
%>
		<table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel'><table width="800" border="0" ><tr class='formlabel'><td>CountryMaster - Invalidate</td><td align=right>QS1000141</td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>
		<form method="post" action="etrans/CountryInvalidate.jsp">
		<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_COUNTRYID" value="NO" checked disabled>
			</td>
			<td  >
			Country-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_COUNTRYNAME" value="NO">
			</td>
			<td  >
			Country Name</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CURRENCYID" value="NO">
			</td>
			<td  >Currency-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_REGION" value="NO">
			</td>
			<td  >Region</td>
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

				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			     </form>
<% 
}
if (view.equalsIgnoreCase("locationmaster") )
{    

%>
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel'><table width="800" border="0" ><tr class='formlabel'><td>LocationMaster - Invalidate</td><td align=right>QS1222642</td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>
            <form method="GET" action="etrans/LocationInvalidate.jsp">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_LOCATIONID" value="NO" checked disabled>
			 </td>
			<td  width="50%">
			Location-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_LOCATIONNAME" value="NO">
			</td>
			<td  width="50%">Location Name</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_COUNTRYID" value="NO">
			</td>
			<td  width="50%">
			Country-ID</td>
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

				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			     </form>
              
              

<% 
}if (view.equalsIgnoreCase("commoditymaster") )
{
%>
			
			<table width="800" border="0" cellspacing="1" cellpadding="4" >
				<tr> 
				<td  class='formlabel'><table width="800" border="0" ><tr class='formlabel'><td>CommodityMaster - Invalidate</td><td align="right">QS1222631</td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>
			<form method="post" action="etrans/CommodityInvalidate.jsp" >
    		<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
           <tr class='formdata' >
          
            <td align="right" width="50%"> <input type="checkbox" name="P_COMMODITYID" value="NO" checked disabled>
			</td>
			<td  width="50%">	
              Commodity-ID</td>
          
            <tr class='formdata' >
            
           <td align="right" width="50%"> 
			<input type="checkbox" name="P_COMMODITYTYPE" value="NO">
			</td>
			<td  width="50%">	
           	Commodity Type
			</td>
             </tr>
            <tr class='formdata' >
            <td align="right" width="50%"> 
			<input type="checkbox" name="P_COMMODITYDESC" value="NO">
			</td>
			<td  width="50%">	
			Commodity Description
            </tr>
            <tr class='formdata' >
            <td align="right" width="50%"> 
             <input type="checkbox" name="P_HANDLINGINFO" value="NO">
			 </td>
			<td  width="50%">	
              Handling Information
            </td>
             </tr>
			 <tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="select" value="0" onClick="selectAll()">
				<td align="left">Select All
				</td>
				</tr>
			<input type="hidden" value="P_COMMODITYID" name="SortBy"><!-- Shyam -->
			<input type="hidden" value="ASC" name="SortOrder"><!-- Shyam -->
			<input type="hidden" value="1" name="PageNo"><!-- Shyam -->

              </table>
			  <table border="0" width="800" cellpadding="4" cellspacing="1" height="39">
				<tr> 
				<td valign="top" bgcolor="white" align="right"> 

				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			     </form>

<%
}if(view.equals("CustomerRegistration"))
{
%>

			
				<table width="800" border="0" cellspacing="1" cellpadding="4"  >
				<tr> 
				<td  class='formlabel'><table width="790" border="0" ><tr class='formlabel'><td>Customer - Invalidate</td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				
				</tr>
				</table>

				<form method="post" action="etrans/CustomerInvalidate.jsp" >

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

				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="REGISTERED" value="0">
				</td>
				<td align="left">Customer Status
				</td>
				</tr>
				
				<tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="TERMINALID" value="0">
				</td>
				<td align="left">Terminal Id
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
				<input type="submit" value="Submit" name="Submit" class='input'>

				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			    </form>
		

<%
}if (view.equalsIgnoreCase("portmaster") )
{
%>
<table width="800" border="0" cellspacing="1" cellpadding="4"  >
				<tr> 
				<td  class='formlabel'><table width="790" border="0" ><tr class='formlabel'><td>PortMaster - Invalidate</td><td align=right>QS1222653</td></tr></table>
				</td>
				</tr>
				<tr  valign="top"> 
				<td>
				<font face="verdana" size="3">Select the fields you wish to View:
				</font>
				</td>
				</tr>
				</table>
<form method="post" action="etrans/sea/PortViewAll.jsp?Operation=Invalidate">
            <table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
			<tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_PORTID" value="NO" checked disabled>
  </td>
			<td  width="50%">Port-Id</td>
			</tr><tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_PORTNAME" value="NO">
  </td>
			<td  width="50%">Port Name</td>
			</tr><tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_COUNTRYID" value="NO">
  </td>
			<td  width="50%">Country Id</td>
			</tr><tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_DESCRIPTION" value="NO">
  </td>
			<td  width="50%">Description</td>
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
				<input type="submit" value="Submit" name="Submit" class='input'>
				<input type="reset" value="Reset" name="reset" class='input'>
				</td>
				</tr>
				</table>
			    </form>
<% 
}if(view.equals("CarrierRegistration"))
{
%>

		
		<table width="800" border="0" cellspacing="1" cellpadding="4"  >
		<tr> 
		<td  class='formlabel' ><table width="790" border="0" ><tr class='formlabel'><td> Carrier - Invalidate</td></tr></table>
		</td>
		</tr>
		<tr  valign="top"> 
		<td>
		<font face="verdana" size="3">Select the fields you wish to View:
		</font>
		</td>
		</tr>
		</table>
		<form method="POST" action="etrans/CarrierInvalidate.jsp" >     
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
		<input type="submit" value="Submit" name="Submit" class='input'>
		<input type="button" value="<< Back" name="Back" onClick="javascript:history.back()" class='input'>
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
		
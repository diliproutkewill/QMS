<%@ page import = "javax.naming.InitialContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%   
	 String insetAttribute		=	null ;
 	 String insetAttribute1		=	null;
	 String terminalID			=	null; 
	 String actionValue			=	null;
	 boolean callErrorPage		=	false;
	 String errorMessage 		=	null;
	 String displayValue		=	null;      
	 int 	in					=	0;

  try 
      {
            
            insetAttribute=request.getParameter("viewParameter");
			 insetAttribute1=insetAttribute;
       }catch(Exception e)
		{
		  		 errorMessage	=	"viewParameter value not passed correctly.To continue, press the BACK Button of the Browser";  
           		 callErrorPage	=	true;
         }
	if ((insetAttribute==null) || (insetAttribute==""))
	  { 
             errorMessage	=	"ViewParameter not  passed correctly in the QueryString.To continue, press the BACK Button of the Browser"; 
             callErrorPage	= 	true;   
      }
    
	if(callErrorPage)
	 {
		  session.setAttribute("Operation",""); 
          session.setAttribute("ErrorCode","ERR");
	 	  session.setAttribute("ErrorMessage",errorMessage);
		  session.setAttribute("NextNavigation","");
          callErrorPage=false;
%>
           <jsp:forward page="ESupplyErrorPage.jsp" />
<%
    }
    else 
     {	
                callErrorPage	=	false; 
                displayValue	=	null;             
                insetAttribute		=	insetAttribute.toUpperCase();
				insetAttribute1		=	insetAttribute1.substring(0,1).toUpperCase()+insetAttribute.substring(1).toLowerCase();

				session.setAttribute("InsetAttribute",insetAttribute);
                if (insetAttribute.endsWith("WRTTERMINAL" ) )
                { 
		   				 displayValue=insetAttribute.substring(0,insetAttribute.lastIndexOf("WRTTERMINAL" ));    
                }
                if(insetAttribute.endsWith("MASTER")) 
				{
				  	 in	= insetAttribute1.indexOf("master");	
				  	 displayValue=insetAttribute1.substring(0,(in));	
					//Modified by Sreelakshmi KVA - 20050411 SPETI-5538 //
					 displayValue = displayValue +"  ";			     	 
                }
				else if(insetAttribute.endsWith("REGISTRATION")) 
				{
				 	 in	= insetAttribute1.indexOf("registration");	
				  	 displayValue=insetAttribute1.substring(0,(in));	
					 displayValue = displayValue +"  "+"Registration";
				}
				else if(insetAttribute.equals("SERVICELEVEL"))
				{
					  displayValue="Service Level";
				}
				else
				{
					  // @@ Murali Modified On 20050427 Regarding SPETI - 5713 
					  // displayValue=insetAttribute.toString();
					  if("AGENTJV".equalsIgnoreCase(insetAttribute.toString()))
					  {
							displayValue = "Agent/Joint Venture";
					  }
					  else 
					  {
					  displayValue=insetAttribute.toString();
				}
					  // @@ Murali Modified On 20050427 Regarding SPETI - 5713 


				}
                   
     }
	  
				InitialContext inic = new InitialContext();
%>
<html>
<head>
<title><%=insetAttribute%>  ViewAll page</title>
<%@ include file="../ESEventHandler.jsp" %>
<script language='javascript'>
function check()
{
for (var i=0 ; i < document.forms[0].elements.length ; i++)
{
if (document.forms[0].elements[i].checked)
       return true;
}
alert("Please select atleast one item");
return false;   
}  // end of check()
	
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

<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body onUnLoad="closeChild()">
<table width="800" border="0" cellspacing="0" cellpadding="0" >
  <tr valign="top" bgcolor='#FFFFFF'> 
    <td><table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'><td>
     	 <table width="790" border="0">
			<tr class='formlabel'> 
			<td ><%= displayValue%> - View All </td><td align=right>QS1000141</td>
			</tr>
			</table>
			</td></tr></table>
			<table width="800" border="0"  cellpadding="4" cellspacing="1">
			<tr  valign="top" class='formdata'> 
			<td><b>Select the fields you  wish to View:</b></td>
			</tr>
		</table>
            
     
                   
<%
		  if ( insetAttribute.equalsIgnoreCase("serviceLevel") )
		  {
%>
				<form method="post" action="ETASuperUserViewAllReport.jsp"   onSubmit="return check();showReport(true,this );" >
				<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_SERVICEID" value="NO" checked disabled>
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
<%
	}
	if (insetAttribute.equalsIgnoreCase("agentjv"))
	{
%>

				<form method="post" action="ETASuperUserViewAllReport.jsp"  onSubmit="return check();showReport(true,this );" >
				<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_COMPANYID" value="NO" checked>
				</td>
				<td  width="50%">	
				Company-ID
				</td>
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_COMPANYNAME" value="NO">
				</td>
				<td  width="50%">	
				Company	Name
				</td>
				</tr>
				<tr class='formdata' >
				
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_EQUITY" value="NO">
				</td>
				<td  width="50%">	
				% of Equity
				</td>
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_A_JV" value="NO">
				</td>
				<td  width="50%">	
				Agent/JV </td>
				
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_SALESNO" value="NO">
				</td>
				<td  width="50%">	
				Sales Tax Reg. No
				</td>
				
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_DESIGNATION" value="NO">
				</td>
				<td  width="50%">	
				Designation
				</td>
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_LOCALCURRENCY" value="NO">
				</td>
				<td  width="50%">	
				Local Currency-ID
				</td>
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_CONTACTPERSON" value="NO">
				</td>
				<td  width="50%">	
				Contact Person
				</td>
			
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_CITY" value="NO">
				</td>
				<td  width="50%">	
				City
				
				</tr>
				<tr class='formdata' >
				<td align="right" width="50%"> 
				<input type="checkbox" name="select" value="NO" onClick="selectAll()">
				</td>
				<td  width="50%">	
				Select All
				</td>
				</tr>
				</table>
<%
	}
	if  ( (insetAttribute.equalsIgnoreCase("carriercontracts") )   || ( insetAttribute.equalsIgnoreCase("carriercontractsWRTterminal") )    )
	{																							
		if ( insetAttribute.equalsIgnoreCase("carriercontracts") )    
				    actionValue="AdminViewAllReport.jsp";
		if ( insetAttribute.equalsIgnoreCase("carriercontractsWRTterminal") ) 
				 actionValue="ETAETAGatewayTerminalViewAllReport.jsp";
%>
			<form method="post" action="<%=actionValue%>" onSubmit="return check();showReport(true,this );">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
			<tr class='formdata' >
			<td align="right" width="50%"> 
			<input type="checkbox" name="P_CARRIERID" value="NO" >
			</td>
				<td  width="50%">	
			Carrier	ID
			</td>
			
			</tr>
			<tr class='formdata' >
			
			<td align="right" width="50%"> 
            <input type="checkbox" name="P_CARRIERNAME" value="NO">
			</td>
				<td  width="50%">	
            Carrier	Name
			</tr>
			<tr class='formdata' >
			<td align="right" width="50%"> <input type="checkbox" name="P_CARRIERCONID" value="NO">
			</td>
				<td  width="50%">	
			Carrier Contract ID
			</td>
			
			</tr>

<%
			if (insetAttribute.equalsIgnoreCase("carriercontracts"))
			{
%>

				<tr class='formdata' >
				
				<td align="right" width="50%"> 
				<input type="checkbox" name="P_TERMINALID" value="NO">
				</td>
				<td  width="50%">	
				Terminal ID
				</td>
			
				</tr>
<% 
	}
	if (insetAttribute.equalsIgnoreCase("carriercontractsWRTterminal"))
	{			
%>
			<input type="hidden" name="P_TERMINALID" value=<%=terminalID %>  >
<% 
	}
%>
			</table>

<%
	}

	if (insetAttribute.equalsIgnoreCase("carrierregistration") )
	{
%>
    	<form method="post" action="<%=actionValue%>"  onSubmit="return check();showReport(true,this );">
	 	<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
				<tr class='formdata' >

				<td align="right" width="50%"> <input type="checkbox" name="P_CARRIERID" value="NO">
				</td>
				<td  width="50%">	
				Carrier ID
				</td>
				</tr>
				<tr class='formdata' >

				<td align="right" width="50%"> 
				<input type="checkbox" name="P_CARRIERNAME" value="NO">
				</td>
				<td  width="50%">	
				Carrier  Name
				</td>
				</tr>
				<tr class='formdata' >

				<td align="right" width="50%"> <input type="checkbox" name="P_SHIPMENT" value="NO">
				</td>
				<td  width="50%">	
				Shipment Mode
				</td>
				</tr>
				<tr class='formdata' >
				
				<td align="right" width="50%"> <input type="checkbox" name="P_CITY" value="NO">
				</td>
				<td  width="50%">	
				Address Details
				</td>

				</tr>
            </table>

<%
	}

if (insetAttribute.equalsIgnoreCase("chargesmaster") )
{
%>
		<form method="GET" action="ETASuperUserViewAllReport.jsp" onSubmit="return check();showReport(true,this );">
		<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
		<tr class='formdata' >

		<td align="right" width="50%"> 
		<input type="checkbox" name="P_CHARGEID" value="NO" checked>
		</td>
				<td  width="50%">	
		Charge ID
		
		</tr> 
		<tr class='formdata' >
		<td align="right" width="50%"> 
		<input type="checkbox" name="P_CHARGEDESC" value="NO">
		</td>
				<td  width="50%">	
			Charge Description
		</td>
		</tr>
		<tr class='formdata' >

		<td align="right" width="50%"> <input type="checkbox" name="P_COSTINCURREDAT" value="NO">
		</td>
				<td  width="50%">	
		Cost Incurred At</td>
		</tr>
		</table>

<% 
}
if (insetAttribute.equalsIgnoreCase("taxmaster") )
{
%>
			<form method="post" action="ETASuperUserViewAllReport.jsp" onSubmit="return check();showReport(true,this );">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata' >
             <td align="right" width="50%"> 
            <input type="checkbox" name="P_TAXID" value="NO" checked>
			</td>
				<td  width="50%">	  
			Tax-ID</td>
           
            </tr>
            <tr class='formdata' >
        
            <td align="right" width="50%"> 
             <input type="checkbox" name="P_TAXDESC" value="NO">
			 </td>
				<td  width="50%">	
			  Tax Description</td>
             </tr>
            <tr class='formdata' >
         
            <td align="right" width="50%"> 
			<input type="checkbox" name="P_REMARKS" value="NO">
			</td>
			<td  width="50%">	
			Remarks </td>
               </tr>
              </table>

<% 
}
if (insetAttribute.equalsIgnoreCase("commoditymaster") )
{
%>
			<form method="post" action="ETASuperUserViewAllReport.jsp" onSubmit="return check();showReport(true,this );">
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
              Handling
              Information
            </td>
			<input type="hidden" value="P_COMMODITYID" name="SortBy"><!-- Shyam -->
			<input type="hidden" value="ASC" name="SortOrder"><!-- Shyam -->
			<input type="hidden" value="1" name="PageNo"><!-- Shyam -->

             </tr>
			 <tr class='formdata'>
				<td width="50%" align="right">
				<input type="checkbox" name="select" value="0" onClick="selectAll()">
				<td align="left">Select All
				</td>
				</tr>
              </table>

<%
}if (insetAttribute.equalsIgnoreCase("portmaster") )
{
%>
<form method="post" action="sea/PortViewAll.jsp?Operation=viewAll" onSubmit="return check();">
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
  <td align="right" width="50%"> 
	<input type="checkbox" name="select" value="NO" onClick="selectAll()">
  </td>
  <td  width="50%">	Select All</td>
</tr>
</table>
<% 
}
if (insetAttribute.equalsIgnoreCase("currencyconversion") )
{
%>
 			<form method="GET" action="<%=actionValue%>" onSubmit="return check();showReport(true,this );">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
             <td align="right" width="50%"> <input type="checkbox" name="P_CURRENCY1" value="NO" checked>
			  </td>
			<td  width="50%">	
			 Currency-1</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"> 
             <input type="checkbox" name="P_CURRENCY2" value="NO" checked>
			  </td>
			<td  width="50%">	
			 Currency-2</td>
            </tr>
            <tr class='formdata'>
           <td align="right" width="50%"> <input type="checkbox" name="P_CONVERSION" value="NO" checked>
		    </td>
			<td  width="50%">	
		   Conversion Factor
          
            </td>
            </tr>
         
              </table>

<%
}
if  ( (insetAttribute.equalsIgnoreCase("customercontracts") )   || ( insetAttribute.equalsIgnoreCase("customercontractsWRTterminal") )    )
{
		if (insetAttribute.equalsIgnoreCase("customercontracts") )    
	          actionValue="ETransCustomerContractsReport.rdf";
		if (insetAttribute.equalsIgnoreCase("customercontractsWRTterminal") ) 
	 		 actionValue="ETAETAGatewayTerminalViewAllReport.jsp";
%>
           <form method="post" action="<%=actionValue%>"  onSubmit="return check();showReport(true,this );">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
             <td align="right" width="50%"><input type="checkbox" name="P_CUSTOMERID" value="NO">
			  </td>
			<td  width="50%">	
			Customer-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CONTRACTID" value="NO">
			 </td>
			<td  width="50%">	
			Contract-ID</td>
            </tr>
            <tr class='formdata'>
             <td align="right" width="50%"><input type="checkbox" name="P_CONTACTNAME" value="NO">
			  </td>
			<td  width="50%">	
			Company Name</td>
            </tr>
<%           
if  ( insetAttribute.equalsIgnoreCase("customercontracts") )
{ 
%>
			 <tr class='formdata'>
             <td align="right" width="50%"><input type="checkbox" name="P_TERMINALID" value="NO">
			  </td>
			<td  width="50%">	
			Terminal-ID</td>
            </tr>
<% 
}
if  ( insetAttribute.equalsIgnoreCase("customercontractsWRTterminal") )
{
%>
<input type="hidden" name="P_TERMINALID"   value="<%=terminalID %>" >
<% 
}
%>
              </table>
              

<% 
}
if  ( (insetAttribute.equalsIgnoreCase("CustomerRegistration") )   || ( insetAttribute.equalsIgnoreCase("CustomerRegistrationWRTterminal") )    )
{
 	 if  (insetAttribute.equalsIgnoreCase("CustomerRegistration") )
	   	actionValue="ETransCustomerRegistrationReport.rdf";
 	 if  (insetAttribute.equalsIgnoreCase("CustomerRegistrationWRTterminal") ) 
		 actionValue="ETAGatewayTerminalViewAllReport.jsp"; 
%>
           <form method="post" action="<%=actionValue%>" onSubmit="return check();showReport(true,this );">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
           <td align="right" width="50%"><input type="checkbox" name="P_CUSTOMERID" value="NO">
		    </td>
			<td  width="50%">	Customer-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CUSTOMERNAME" value="NO">
			 </td>
			<td  width="50%">	
			Customer Name</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_REGISTRATION" value="NO">
			 </td>
			<td  width="50%">	
			Registration</td>
            </tr>
<%
if ( insetAttribute.equalsIgnoreCase("CustomerRegistration") )
{ 
%>           
			<tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_TERMINALID" value="NO">
			 </td>
			<td  width="50%">	Terminal-ID</td>
            </tr>
<% 
}
if  (insetAttribute.equalsIgnoreCase("CustomerRegistrationwrtTerminal") )
{
%>
				<input type="hidden" name="P_TERMINALID" value="<%=terminalID %>" >
<%
 }
%>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CITY" value="NO">
			 </td>
			<td  width="50%">
			City:</td>
            </tr>
              </table> 

<% 
}
if (insetAttribute.equalsIgnoreCase("gatewaymaster") )
{
%>
   			<form method="GET" action="ETAGatewayTerminalViewAllReport.jsp" onSubmit="return check();showReport(true,this );" >
            <table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
             
            <tr class='formdata'>
             <td align="right" width="50%"><input type="checkbox" name="P_GATEWAYID" value="NO" checked>
			  </td>
			<td  width="50%">
			Gateway ID</td>
            </tr>
            <tr class='formdata'>
             <td align="right" width="50%"><input type="checkbox" name="P_GATEWAYNAME" value="NO">
			  </td>
			<td  width="50%">
			Gateway Name</td>
             
            </tr>
     		 <tr class='formdata'>
 			<td align="right" width="50%"><input type="checkbox" name="P_GATEWAYTYPE" value="NO">
			  </td>
			<td  width="50%">
			Gateway Type</td>
             
                </tr>
			<tr class='formdata'>
 				<td align="right" width="50%"><input type="checkbox" name="P_COMPANY" value="NO">
				  </td>
			<td  width="50%">
			Company Name</td>
             
                </tr>
			<tr class='formdata'>
			 <td align="right" width="50%"><input type="checkbox" name="P_CONTACT" value="NO">
			  </td>
			<td  width="50%">
			Contact Person</td>
             
                </tr>
			<tr class='formdata'>
 			<td align="right" width="50%"><input type="checkbox" name="P_INDICAT" value="NO">
			  </td>
			<td  width="50%">
			Indicator</td>
             
                </tr>
            <tr class='formdata'>
             <td align="right" width="50%"><input type="checkbox" name="P_CITY" value="NO">
			  </td>
			<td  width="50%">
			City</td>
              
            </tr>
            
			<tr class='formdata'>
			 <td align="right" width="50%"><input type="checkbox" name="select" value="NO" onClick="selectAll()">
			  </td>
			<td  width="50%">
			Select All</td>
			 
                </tr>
              </table>
              
              
<% 
}
if (insetAttribute.equalsIgnoreCase("locationmaster") )
{    insetAttribute="Location Master";

%>
      
            <form method="GET" action="ETASuperUserViewAllReport.jsp" onSubmit="return check();showReport(true,this );">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_LOCATIONID" value="NO" checked>
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
			<td align="right" width="50%"> 
			<input type="checkbox" name="select" value="NO" onClick="selectAll()">
			</td>
			<td  width="50%">	Select All</td>
			</tr>
              </table>
              
              

<% 
}
if (insetAttribute.equalsIgnoreCase("terminalmaster") )
{
%>
<form method="post" action="ETAGatewayTerminalViewAllReport.jsp" onSubmit="return check();showReport(true,this );">
            <table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
			<tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_TERMINALID" value="NO">
  </td>
			<td  width="50%">Terminal-ID</td>
			</tr><tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_ADDRESS" value="NO">
  </td>
			<td  width="50%">City</td>
			</tr><tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_CONTACT" value="NO">
  </td>
			<td  width="50%">Contact Person</td>
			</tr><tr class='formdata'>
<td align="right" width="50%"><input type="checkbox" name="P_DESIGNATION" value="NO">
  </td>
			<td  width="50%">Designation</td>
			</tr>
              </table>
<% 
}
if  ( (insetAttribute.equalsIgnoreCase("usermaster") )   || ( insetAttribute.equalsIgnoreCase("usermasterWRTterminal") )    )
{
if (insetAttribute.equalsIgnoreCase("usermaster") )             actionValue="ETransUserMasterReport.rdf";
if (insetAttribute.equalsIgnoreCase("usermasterWRTterminal") )  actionValue="ETransUserMasterWrtTerminalReport.rdf";
%>
			<form method="post" action="<%=actionValue%>"  onSubmit="return check();showReport(true,this );">
            <table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_USERID" value="NO">
			</td>
			<td  width="50%">User-ID</td>
            </tr>
            
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_USERNAME" value="NO">
			 </td>
			<td  width="50%">
			User Name</td>
            </tr>
            
<% 
if (insetAttribute.equalsIgnoreCase("usermaster") )
{
%>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_TERMINALID" value="NO"></td>
			<td  width="50%">Terminal-ID</td>
            </tr>
<% 
}
if (insetAttribute.equalsIgnoreCase("usermasterWRTterminal") )
{
%>
<input type="hidden" name="P_TERMINALID" value="<%=terminalID%>" >
<% 
}
%>
              </table> 
<%
}
if (insetAttribute.equalsIgnoreCase("CUSTOMSMASTER") )
{
%>
			<form method="GET" action="ETAGatewayTerminalViewAllReport.jsp"  onSubmit="return check();showReport(true,this );" >
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CUSTOMSID" value="NO" checked>
			</td>
			<td  width="50%">Customs-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CONTACTNAME" value="NO">
			</td>
			<td  width="50%">Contact Name</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_DESIGNATION" value="NO">
			</td>
			<td  width="50%">Designation</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_TERMINALID" value="NO">
			</td>
			<td  width="50%">Terminal-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CITY" value="NO">
			</td>
			<td  width="50%">City</td>
            </tr>
			<tr class='formdata'>
			<td align="right" width="50%"><input type="checkbox" name="select" value="NO" onClick="selectAll()">
			</td>
			<td  width="50%">Select All</td>
                </tr>
              </table> 
              

<% 
}
if (insetAttribute.equalsIgnoreCase("countrymaster") )
{
%>
		<form method="post" action="ETASuperUserViewAllReport.jsp" onSubmit="return check();showReport(true,this );">
		<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_COUNTRYID" value="NO" checked>
			</td>
			<td  width="50%">
			Country-ID:</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_COUNTRYNAME" value="NO">
			</td>
			<td  width="50%">
			Country Name</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CURRENCYID" value="NO">
			</td>
			<td  width="50%">Currency-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_REGION" value="NO">
			</td>
			<td  width="50%">Region</td>
            </tr>
			 <tr class='formdata'>
			<td align="right" width="50%"> 
			<input type="checkbox" name="select" value="NO" onClick="selectAll()">
			</td>
			<td  width="50%">	Select All</td>
			</tr>
              </table>
<% 
}
if (insetAttribute.equalsIgnoreCase("companyregistration") )
{
%>
         
      <form method="post" action="ETASuperUserViewAllReport.jsp"  onSubmit="return check();showReport(true,this );" >
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_COMPANYID" value="NO" checked></td>
			<td  width="50%">Company-ID
			</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_COMPANYNAME" value="NO">
			</td>
			<td  width="50%">
			Company Name</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_HCURRENCY" value="NO">
			</td>
			<td  width="50%">HO Currency</td>
            </tr>
			<!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5648 on 20050415.-->
            <!-- <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_DAYLIGHTSAVING" value="NO">
			</td>
			<td  width="50%">Day Light Saving:</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_TIMEZONE" value="NO">
			</td>
			<td  width="50%">Time Zone:</td>
            </tr> -->
            <tr class='formdata'>
           	<td align="right" width="50%"><input type="checkbox" name="P_CITY" value="NO">
			</td>
			<td  width="50%">City</td>
            </tr>
			<tr class='formdata'>
			<td align="right" width="50%"><input type="checkbox" name="select" value="NO" onClick="selectAll()">
			</td>
			<td  width="50%">Select All</td>
                </tr>
              </table>              
              
              
<% 
}
if (insetAttribute.equalsIgnoreCase("REVENUESHARING") )
{
%>
    
   			<form method="GET" action="<%=actionValue%>"  onSubmit="return check();showReport(true,this );">
			<table border="0" width="800" border="0" cellspacing="1" cellpadding="4">
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CTYPE1" value="NO">
			</td>
			<td  width="50%">
			Company Type-1</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CTYPE2" value="NO">
			</td>
			<td  width="50%">
			Company Type-2</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CODE" value="NO">
			</td>
			<td  width="50%">Code</td>
            </tr>
            <tr class='formdata'>
             <td align="right" width="50%"><input type="checkbox" name="P_SHARINGVALUE" value="NO">
			</td>
			<td  width="50%">Sharing Value</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_CHARGEID" value="NO">
			</td>
			<td  width="50%">Charge-ID</td>
            </tr>
            <tr class='formdata'>
            <td align="right" width="50%"><input type="checkbox" name="P_INDICATOR" value="NO">
			</td>
			<td  width="50%">Indicator</td>
            </tr>
              </table>
<% 
}
%>
           
 				<table border="0" width="800" cellpadding="4" cellspacing="1">
                <tr > 
                  <td valign="top" bgcolor="white" align="right"> 
				  <%//Re-arranged by Sreelakshmi KVA - 20050411 SPETI-5573 //%>
					<input type="submit" value="Submit" name="Submit" class='input'>
					<input type="reset" value="Reset" name="reset" class='input'>
					</form>
  </td>
  </tr>
</table>					
</body>
</html>

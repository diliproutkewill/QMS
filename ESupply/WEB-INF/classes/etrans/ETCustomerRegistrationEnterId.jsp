<%
		/* Program Name		: ETCustomerRegistrationEnterId.jsp
		Module name		: HO Setup
		Task		        : Adding Customer
		Sub task		: to choose a CustomerId to Modify, View or Delete a record
		Author Name		: A.Hemanth Kumar
		Date Started		: September 08, 2001
		Date completed	: September 11, 2001
		Date Modified	: December 10,2001 by Rizwan.
		Corrected the problem which was not showing CorporateCustomerId when modified.
		Description      :
		This file is used choose a CustomerId to Modify, View or Delete a record.
		*/
%>
<%@		page import	=	"org.apache.log4j.Logger,
		com.foursoft.esupply.common.java.ErrorMessage,
		com.foursoft.esupply.common.java.KeyValue,
		java.util.ArrayList "
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
    private static Logger logger = null;
		private static final String FILE_NAME	=	"ETCustomerRegistrationEnterId.jsp ";
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
		String operation			=	null;   // String to store type of operation requested obtained from session
		String Customer				=	"";  // String to store Customer obtained from session
		String customerType			=	null;    // String to store customer type
		String registrationLevel	=	null; // String to store Registration Level  
		String loginTerminal		=	loginbean.getTerminalId();
		String label				=	"";

		ErrorMessage errorMessageObject = null;
		ArrayList	 keyValueList	    = new ArrayList();

		try
		{
		// checking for termianl Id
		if(loginbean.getTerminalId() == null)
		{
%>
		<jsp:forward page="../ESupplyLogin.jsp" />
<%
		}else{
			operation = request.getParameter("Operation");
			Customer = request.getParameter("Customer");
			registrationLevel=request.getParameter("registrationLevel");
			
			if(registrationLevel!=null&&registrationLevel.equalsIgnoreCase("C")){
				label="Corporate ";
			}else{
				label="Terminal ";
			}

		// if operation is 'upgrade' and customer is 'CCS'
		if(operation.equalsIgnoreCase("Upgrade") && Customer.equalsIgnoreCase("CCS")){    

			errorMessageObject = new ErrorMessage("Sorry no Upgrade for Corporate Customers","ETCustomerRegistrationEnterId.jsp?Operation="+operation+"&Customer="+Customer); 

			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%		
		}
%> 
<html>
<head>
<title>Customer Registration EnterId</title>
<%@ include file="/ESEventHandler.jsp" %>

<script language="JavaScript">
var operation='<%=operation%>';
function emptyCustomerId(){
	if(document.forms[0].corpCustomerId.value!=null)
	document.forms[0].corpCustomerId.value="";
}	

//This function is used to place the focus to a field.
function placeFocus(){
	document.forms[0].corpCustomerId.focus();
}

//This function calls 'ETransLOVCustomerIds.jsp' to get CustomerIds.
function showCustomerIds(){
	var customerType="";

<%
		if(Customer.equalsIgnoreCase("CCS")){
%>
			customerType="R";
<%
		}
		if(Customer.equalsIgnoreCase("NCCS")){
			if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") ){
%>
				if(document.forms[0].customerType[0].checked)
					customerType="R"; 
				if(document.forms[0].customerType[1].checked)
					customerType="U";  
<%
			}
		}
		if(operation.equalsIgnoreCase("Delete")){
%>
			customerType="R";
<%
		}
		if(operation.equalsIgnoreCase("Upgrade")){
%>
			customerType="U";
<%
		}
%>

/*
<%
//	if(registrationLevel.equalsIgnoreCase("C"))
//	{
%>
if(operation!='View')	
{
whereClause=" TERMINALID='<%=loginTerminal%>'";   //(CUSTOMERTYPE='Corporate' OR CUSTOMERTYPE='Customer') AND 
}else
{
whereClause='';
}
<%
//	}
//	else if(registrationLevel.equalsIgnoreCase("T"))
//	{
%>
if(customerType=="R")
whereClause=" CUSTOMERTYPE='Customer' AND TERMINALID='<%=loginTerminal%>'";	  
if(customerType=="U")
whereClause=" TERMINALID='<%=loginTerminal%>'";	  
<%
//   }      
%>
*/

//		if(whereClause.length>0)
//		var	URL		=	'ETransLOVCustomerIds.jsp?customerType='+customerType+'&searchString='+document.forms[0].corpCustomerId.value.toUpperCase()+'&whereToSet=corpCustomerId&registrationLevel=<%=registrationLevel%>&registered='+customerType;
//		else

var	URL			=	'ETransLOVCustomerIds.jsp?customerType=Customer&searchString='+document.forms[0].corpCustomerId.value.toUpperCase()+'&whereToSet=corpCustomerId&registrationLevel=T&registered='+customerType+'&operation=Modify&terminalId=<%=loginTerminal%>';
var	bars		=	'directories = no, status = no, location = no, menubar = no, titlebar = no';
var	options		=	'scrollbars = yes, width = 600, height = 360, resizable = yes';
var	features	=	bars + ' ' + options;
var	win			=	open(URL,'Doc',features);		
}
// This function is used to filter single quotes.
function stringFilter(input){
	s = input.value;
	input.value=s.toUpperCase();
}
function Mandatory(){

		customerId=document.forms[0].corpCustomerId.value;

		if(customerId.length==0){
			alert("Please enter CustomerId");
			document.forms[0].corpCustomerId.focus();
			return false;
		}else{
			s =customerId;
			var	i;
			var returningString = "";
			for(i=0;i<s.length;i++){
				var c = s.charAt(i);
				returningString += c.toUpperCase();
			}
				document.forms[0].corpCustomerId.value = returningString;
		}
			document.forms[0].enter.disabled='true';
			return true;
}


function showAdvancedLOV()
{
	
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var operation     ='<%=request.getParameter("Operation")%>';
	var shipmentMode  ='<%=request.getParameter("ShipmentMode")%>';
	if(document.forms[0].customerType[0].checked)
		var register      =document.forms[0].customerType[0].value;
	else
		var register      =document.forms[0].customerType[1].value;
	
	var Url='../ETAdvancedLOVController?entity=Customer&formfield=corpCustomerId&operation='+operation+'&registrationLevel=T&mode=1&type=single&terminalId='+terminalId+'&register='+register;

	var Bars = 'directories=no, location=no,menubar=no,status=Yes,titlebar=no';
	var Options='scrollbar=yes,width=820,height=600,resizable=no';
	var Features=Bars+','+Options;

	 Win=open(Url,'Doc',Features); 
	
	
}

function showAdvancedDeleteLOV()
{
	
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var operation     ='<%=request.getParameter("Operation")%>';
	var Url='../ETAdvancedLOVController?entity=Customer&formfield=corpCustomerId&operation='+operation+'&mode=1&type=single&terminalId='+terminalId+'&terminaldel=terminaldel';
	//<!-- Modified By G.Srinivas  on 20050406.-->
	//alert(Url);
	var Bars = 'directories=no, location=no,menubar=no,status=Yes,titlebar=no';
	var Options='scrollbar=yes,width=820,height=600,resizable=no';
	var Features=Bars+','+Options;

	 Win=open(Url,'Doc',Features); 
}

	

			
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<BODY  onload=placeFocus() >
<FORM action="ETCustomerRegistrationAddMoreAdress.jsp?Operation=<%=operation%>&Customer=<%=Customer%>&registrationLevel=<%=registrationLevel%>" method=post name=custreg onsubmit="return Mandatory()">
<TABLE border=0 cellPadding=0 cellSpacing=0 width=800>

<TR bgColor=#ffffff vAlign=top>
<TD>
<TABLE border=0 width=800 cellPadding=4 cellSpacing=1>
 
	<TR class='formlabel'> 
		<TD colSpan=2><table width="790" border="0" ><tr class='formlabel'><td>	<%=label%> Customer Registration - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETCustomerRegistrationEnterId.jsp",operation)%></td></tr></table></td>
	</TR>
	</table>
		  <TABLE border=0 width=800 cellPadding=4 cellSpacing=1>
		  <TR class='formdata'><td colspan="2">&nbsp;</td></tr>
	<TR class='formdata'> 
<%
	if(operation.equalsIgnoreCase("Upgrade")){
%>
		<TD  colSpan=2> <B>Enter Customer Id to Upgrade from "UNREGISTERED" to "REGISTERED"</B> :</TD>
<%
	}else{ 
%>      
	<TD  colSpan=2> 
		<B>Enter Customer Id to <%=operation%> Customer&nbsp; Information :</B> 
	</TD>
<%
	}
%> 


	</TR>
	<TR valign="top" class='formdata'> 
<%
	if(Customer.equalsIgnoreCase("NCCS")){
		if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") ){
%>
	<TD > 
		<input type="radio" name="customerType" value="Registered" checked onClick="emptyCustomerId()">Registered Customer 
		<input type="radio" name="customerType" value="UnRegistered" onClick="emptyCustomerId()">UnRegistered Customer 
	</TD>
	<TD >Customer Id: <font color=#ff0000>*</font><br>
		<input maxlength=25 class='text' name=corpCustomerId onKeyPress="if ((event.keyCode > 32 &amp;&amp; event.keyCode < 48) || (event.keyCode > 57 &amp;&amp; event.keyCode < 65) || (event.keyCode > 90 &amp;&amp; event.keyCode < 97) || (event.keyCode > 122 &amp;&amp; event.keyCode < 127)) event.returnValue = false;"  size=16>
		<input name=Operation type=hidden value='<%= operation%>'>
		<input name=lov_CustomerId onClick=showCustomerIds() class='input' type=button value=...>
     	<input type="button" name="AdvanceSearch" value="Advanced Search" onClick="javascript:showAdvancedLOV()" class="input">
	</TD>
<%
	}else{
%>

	<TD >Customer Id:<font color=#ff0000>*</font><br>
		<input class=text maxlength=25 name=corpCustomerId onKeyPress="if ((event.keyCode > 32 &amp;&amp; event.keyCode < 48) || (event.keyCode > 57 &amp;&amp; event.keyCode < 65) || (event.keyCode > 90 &amp;&amp; event.keyCode < 97) || (event.keyCode > 122 &amp;&amp; event.keyCode < 127)) event.returnValue = false;" size=16>
		<input name=Operation type=hidden value='<%= operation%>'>
		<input name=lov_CustomerId onClick=showCustomerIds() class='input' type=button value=...>
		<input type="button" name="DeleteAdvanceSearch" value="Advanced Search" onClick=showAdvancedDeleteLOV() class="input">
			
			
	</TD>
	<TD>
	</TD>		

<%
		} 
	}
	else
	{  //for CCS
%>
	<TD >Customer Id:<font color=#ff0000>*</font><br>
		<input class=text maxlength=25 name=corpCustomerId onKeyPress="if ((event.keyCode > 32 &amp;&amp; event.keyCode < 48) || (event.keyCode > 57 &amp;&amp; event.keyCode < 65) || (event.keyCode > 90 &amp;&amp; event.keyCode < 97) || (event.keyCode > 122 &amp;&amp; event.keyCode < 127)) event.returnValue = false;" size=16>
		<input name=Operation type=hidden value=Modify>
		<input name=lov_CustomerId onClick=showCustomerIds() class='input' type=button value=...>
	</TD>
		
<%	
	}
%>
	</TR>
	<TR class='denotes'> 
	<TD vAlign=top > 
		<FONT color=#ff0000>*</FONT>Denotes Mandatory
	</TD>
	<TD  align=right>
		<INPUT name=enter type=submit value="Next>>" class='input'>
		<INPUT name=reset type=reset value=Reset class='input'>
		

	</TD>
	</TR>
	 
	</TABLE>
		</TD></TR></TABLE></FORM></BODY></HTML>
<%
	}
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception while accessing Loginbean in CustomerRegistrationEnterId JSP : ", e.toString());
    logger.error(FILE_NAME+"Exception while accessing Loginbean in CustomerRegistrationEnterId JSP : "+ e.toString());
		e.printStackTrace();

		errorMessageObject = new ErrorMessage("Error occured while accessing the page ","ETCustomerRegistrationEnterId.jsp?Operation="+operation); 

		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);

%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		}

%>
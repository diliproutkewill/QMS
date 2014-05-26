<%
/*	Program	Name	: ETCTerminalRegistrationEnterId.jsp
	Module name		: HO Setup
	Task			: Adding	Terminal
	Sub	task		: to view, modify and delete terminals
	Author Name		: C.L.N Saravana
	Date Started	: Feb 25, 2002
	Date completed	: Feb 27, 2002
	Description		:
		This file is used to view or modify	or delete registered terminal information. On selection	of one of the
		options	control	is transferred to ETCTerminalRegistrationView.jsp
*/

%>
<%@ page import = "	javax.naming.InitialContext,
				 	org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id="loginbean"	class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCTerminalRegistrationEnterId.jsp ";
%>

<%
	logger  = Logger.getLogger(FILE_NAME);
	response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
	response.setHeader("Pragma","no-cache"); // HTTP 1.0
	String operation 		= request.getParameter("Operation");
	String userTerminalType = loginbean.getUserTerminalType();
	String availableUserTerminalType = null;
	String terminalId		= (String)session.getAttribute("terminalId");
	String opTerminalType	= (String)session.getAttribute("opTerminalType");
	session.removeAttribute("terminalId");
	session.removeAttribute("opTerminalType");

	InitialContext initialContext				= null;
	SetUpSessionHome terminalHome	= null;
	SetUpSession	terminalRemote 	= null;
	
	
	try
	{
		if(loginbean.getTerminalId()	== null	)
		{
%>
			<jsp:forward page="../ESupplyLogin.jsp"/>
<%
		}
		if((userTerminalType!=null && userTerminalType.equals("H")) || (loginbean.getAccessType()!=null && loginbean.getAccessType().equals("LICENSEE")) )
		{
			initialContext  = new InitialContext();
			terminalHome    = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
			terminalRemote  = (SetUpSession)terminalHome.create();
			availableUserTerminalType = terminalRemote.getUserTerminalType();
			//Logger.info(FILE_NAME,"availableUserTerminalType : "+availableUserTerminalType);
		} 
		
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in ETTerminalRegistrationEnterId.jsp file :	", e.toString());
    logger.error(FILE_NAME+"Error in ETTerminalRegistrationEnterId.jsp file :	"+ e.toString());
	}
%>
<html>
<head>
<title>Terminal	Registration </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script	language="JavaScript">
<!--
var	userTerminalType 			= '<%=userTerminalType%>';
var	availableUserTerminalType	= '<%=availableUserTerminalType%>';
var accessType					= '<%=loginbean.getAccessType()%>';
var	operation					= '<%=operation%>';
var companyId					= '<%=loginbean.getCompanyId()%>';	
var terminalId					= '<%=terminalId%>';	
var logInterminal               = '<%=loginbean.getTerminalId()%>';	// added by VLAKSHMI FOR ISSUE 173655 on 20090629
var userTerminalType            ='<%=userTerminalType%>';	
	function	showLOV()
	{
		var condition;
		
		if(userTerminalType!='A' && userTerminalType!='O'){
			companyId	=	"";
			//condition = " WHERE OPER_ADMIN_FLAG ='"+document.forms[0].terminalType.value+"' AND TERMINALID LIKE '"+document.forms[0].terminalId.value.toUpperCase()+"^' ";
		}else {
			//	condition = " WHERE OPER_ADMIN_FLAG ='"+document.forms[0].terminalType.value+"' AND ( TERMINALID LIKE '"+companyId +"^'  OR TERMINALID LIKE '"+document.forms[0].terminalId.value.toUpperCase()+"^' ";
		}

//		var Url	   = 'ETCLOVTerminalIds.jsp?companyId='+condition;	
		//alert(operation);
		// operationModule is added by VLAKSHMI FOR ISSUE 173655 on 20090629
		var Url	   = 'ETCLOVTerminalIds.jsp?companyId='+companyId+'&terminalType='+document.forms[0].terminalType.value+'&searchString='+document.forms[0].terminalId.value.toUpperCase()+'&operationModule='+userTerminalType+'&shipmentMode=';	

		var Bars	   = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;
		var Win	   =  open(Url,'Doc',Features);
	}




	function placeFocus()
	{
		if( document.forms.length > 0 )
		{
			var field = document.forms[0];
			for(i = 0;	i <	field.length; i++)
			{
				if( (field.elements[i].type == "text") || (field.elements[i].type ==	"textarea")	||	(field.elements[i].type.toString().charAt(0) ==	"s") )
				{
					document.forms[0].elements[i].focus();
					break;
				}
			}
		}
		if(((userTerminalType=='H' && availableUserTerminalType!='null') ||(accessType=='LICENSEE')) && terminalId=='null')
		{
			document.forms[0].terminalId.value 	  = availableUserTerminalType;
			document.forms[0].terminalId.readOnly = true;
			//document.forms[0].button1.disabled	  = true;
		}
	}
	
	function showUserTerminal(input)
	{
		if(input.value=='H')
		{
			document.forms[0].terminalId.value 	  = availableUserTerminalType;
			document.forms[0].terminalId.readOnly = true;	
			//document.forms[0].button1.disabled	  = true;
		}	
		else
		{
			document.forms[0].terminalId.value 	  ='';
			document.forms[0].terminalId.readOnly = false;	
			document.forms[0].button1.disabled	  = false;	
		}
	}
	
	function check_onSubmit()
	{
		document.forms[0].terminalId.value =	document.forms[0].terminalId.value.toUpperCase();
		//alert(document.forms[0].terminalId.value);
	
		if(document.forms[0].terminalId.value.length ==	0)
		{
			alert("Please Enter TerminalId");
			document.forms[0].terminalId.focus();
			return false;
		}// is added by VLAKSHMI FOR ISSUE 173655 on 20090629
			if((document.forms[0].terminalType.value=='<%=userTerminalType%>') && document.forms[0].terminalId.value!='<%=loginbean.getTerminalId()%>')
		{
                 alert("Please  Enter Correct  TerminalId");
                 document.forms[0].terminalId.focus();
				 return false;
		}
		if( document.forms[0].terminalId.value.length ==0)
		{
			alert("Please  Enter Correct  TerminalId");
			document.forms[0].terminalId.focus();
			return false;
		}
		if((document.forms[0].terminalType.value=='H' || accessType=='LICENSEE' ) && operation=='Delete')
		{
			alert("HOAdmin Terminal cannot be deleted");
			document.forms[0].terminalType.focus();
			return false;
		}
		document.forms[0].enter.disabled='true';
		return	true;
	}
	function checkKeyCode()
	{
		if(event.keyCode!=13)
		{	
     		if((event.keyCode < 48 ||event.keyCode > 57) && (event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) )
 	   			return false;	
		}
		return true;	
	}
	function changeToUpper(field)
	{
		field.value	= field.value.toUpperCase();
	}
//-->
</script>
</head> 
<body  onLoad='placeFocus()' >
  <%
	String param = request.getParameter("Operation");
%> 
<form method="GET" action="ETCTerminalRegistrationView.jsp" name="termenter" onSubmit="return check_onSubmit()">
  <table width="800" border="0"	cellspacing="0"	cellpadding="0">
	<tr	bgcolor="#FFFFFF" valign="top">
	  <td >
		<table border="0" width="800" cellpadding="4" cellspacing="1">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table width="790" border="0" ><tr class='formlabel'><td>Terminal - <%=param%> </td><td align=right><%=loginbean.generateUniqueId("ETCTerminalRegistrationEnterId.jsp",param)%></td></tr></table></td>
		  </tr></table>
		  <table border="0" width="800" cellpadding="4" cellspacing="1">
		  <tr valign="top" class="formdata">
			<td	colspan="2">&nbsp;</td>
		  </tr>
		  <tr valign="top" class="formdata">
			<td	colspan="2"><b>Enter
			  TerminalId to	<%=param%> Terminal	Information	:</b></td>
		  </tr>
		 
		   <tr valign="top" class="formdata">
			<td width="355"	>Please Select Terminal Type: <br>
              <select size="1" name="terminalType" class='select' onSelect= 'showUserTerminal(this)' onChange= 'showUserTerminal(this)'>
<%
	if( (loginbean.getAccessType() != null && loginbean.getAccessType().equals("LICENSEE")) && (availableUserTerminalType!=null) )
	{
%>
          			<option value="H">HO Terminal</option>
<%
	}
	else if( (userTerminalType!=null && userTerminalType.equals("H") ) && (availableUserTerminalType!=null) )
	{
%>
          			<option value="H" <%="H".equalsIgnoreCase(opTerminalType)?"selected":""%>>HO Terminal</option>
<%
	}
	if( userTerminalType!=null && !userTerminalType.equals("O") )
	{
%>
          			<option value="A" <%="A".equalsIgnoreCase(opTerminalType)?"selected":""%>>Admin Terminal</option>
<%
	}
%>          			          			
           			<option value="O" <%="O".equalsIgnoreCase(opTerminalType)?"selected":""%>>Operation Terminal</option>  

	           </select>
      	    </td>
			<td width="379"	>TerminalId:<font color="#FF0000">*</font> <br>
<%
			if( userTerminalType!=null && userTerminalType.equals("O"))
			{
%>
			   <input type="text" class="text" name="terminalId" size="18" readonly value='<%=loginbean.getTerminalId()%>'>
<%
			}
			else
			{
%>
				<input type="text" class="text" name="terminalId"	size="18" onBlur=changeToUpper(terminalId) onKeypress="return checkKeyCode()" value='<%=terminalId!=null?terminalId:""%>'>
				<input type=button value="..." onClick=showLOV() name="button1" class="input">
<%
			}
%>
			</td>
		   </tr>
		  <table border="0" width="800" cellpadding="4" cellspacing="1">
		  <tr valign="top" class='denotes'>
			<td><font	color="#FF0000">*</font>Denotes
			  Mandatory</td>
			<td	align="right">
			 
				<input type="submit" value="Next>>"	name="enter" class="input">
				<input type="reset"	name="Reset" value="Reset" class="input">
				<input type="hidden" name="Operation" value=<%=param%> >
			  
			</td>
		  </tr>
		  </table>
		
	  </td>
	</tr>
  </table>
  </form>
</body>
</html>

<%@ page import = "	javax.naming.InitialContext,
				 	org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id="loginbean"	class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCOperationTerminalRegistrationEnterId.jsp ";
%>


<%
  logger  = Logger.getLogger(FILE_NAME);	
	String operation 		= request.getParameter("Operation");
	String userTerminalType = loginbean.getUserTerminalType();
	String availableHOTerminal = null;
	
	//Logger.info(FILE_NAME,"userTerminalType 		  : "+userTerminalType);
	//Logger.info(FILE_NAME,"loginbean.getAccessType() : "+loginbean.getAccessType());
	
	InitialContext initialContext 				= null;
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
		if(loginbean.getAccessType()!=null && loginbean.getAccessType().equals("LICENSEE") )
		{
			initialContext  = new InitialContext();
			terminalHome    = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
			terminalRemote  = (SetUpSession)terminalHome.create();
			availableHOTerminal = terminalRemote.getUserTerminalType();
			//Logger.info(FILE_NAME,"availableUserTerminalType : "+availableHOTerminal);			
		}
		else if(userTerminalType !=null && userTerminalType.equals("O"))
		{
			String errorMessage = loginbean.getTerminalId()+" does not have permissions to create a new Terminal";
			session.setAttribute("ErrorCode","ERR");
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("Operation","Add");
			session.setAttribute("NextNavigation","ETCOperationTerminalRegistrationEnterId.jsp?Operation="+request.getParameter("Operation"));
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}
		else if(userTerminalType!=null && userTerminalType.equals("H"))
		{
			initialContext  = new InitialContext();
			terminalHome    = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
			terminalRemote  = (SetUpSession)terminalHome.create();
			availableHOTerminal = terminalRemote.getUserTerminalType();
			//Logger.info(FILE_NAME,"availableUserTerminalType : "+availableHOTerminal);
		} 
		
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in ETOperationTerminalRegistrationEnterId.jsp file :	", e.toString());
    logger.error(FILE_NAME+"Error in ETOperationTerminalRegistrationEnterId.jsp file :	"+ e.toString());
	}
%>

<html>
<head>
<title>Terminal	Registration </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script	language="JavaScript">
var userTerminalType = '<%=userTerminalType%>';

function ShowTerminalType(input)
{
<%	if(loginbean.getAccessType()!=null  && loginbean.getAccessType().equals("LICENSEE") && (availableHOTerminal == null) )
	{
%>		
		if(input.value=='O')
			document.forms[0].adminType.disabled=true;	
		else
			document.forms[0].adminType.disabled=false;
<%		
	}
	else if( (userTerminalType!=null && userTerminalType.equals("H") ) && (availableHOTerminal == null) )
	{
%>		
		if(input.value=='O')
			document.forms[0].adminType.disabled=true;	
		else
			document.forms[0].adminType.disabled=false;
<%
	}
%>	
	if(userTerminalType=='O')
	{
		document.forms[0].terminalType[0].disabled=true;
	}
}
</script>
</head>
<body onLoad="ShowTerminalType('O')">
<form method="GET" action="ETCTerminalRegistrationAdd.jsp">
  <table width="800" border="0"	cellspacing="0"	cellpadding="0">
	<tr	bgcolor="#FFFFFF" valign="top">
	  <td >
		<table border="0" width="800" cellpadding="4" cellspacing="1">
		  
		  <tr valign="top" class="formlabel">
			<td	><table width="790" border="0" ><tr class='formlabel'><td>Terminal - <%=operation%> </td><td align=right><%=loginbean.generateUniqueId("ETCOperationTerminalRegistrationEnterId.jsp",operation)%></td></tr></table></td>
		  </tr>
		  
		  <tr valign="top" class='formdata'>
			<td	colspan="2" >&nbsp;</td>
		  </tr>
		  
		  <tr valign="top" class="formdata">
			<td width="387"	><b>Please Select the type of Terminal</b>
			</td>
		  </tr>	
		  
		  <tr valign="top" class="formdata" align=center>
			<td width="800"	>
            
              <input type="radio" value="A" checked name="terminalType" onClick=ShowTerminalType(this)><b>Admin Terminal</b>
              <input type="radio" value="O" name="terminalType" onClick=ShowTerminalType(this)><b>Operation Terminal</b>
   
<%
	if( ((userTerminalType!=null && userTerminalType.equals("H") ) && (availableHOTerminal == null)) || ( (loginbean.getAccessType()!=null && loginbean.getAccessType().equals("LICENSEE")) && (availableHOTerminal == null) ) )
	{
%>
			 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			  <select size="1" name="adminType">
			  	<option value="H" > HO </option>
			  	<option value="A" > Admin </option>
              </select>    
<%
	}
%>
 		    </td>
		  </tr>
		  
		  <tr valign="top" class='denotes'>
			<td	width="800" valign="middle" align="right"	>
				<input type="submit" value="Next>>"	name="enter" class="input"><input type="hidden" name="Operation" value="<%=operation%>" >
            </td>
		  </tr>
		</table>
	  </td>
	</tr>
  </table>
  </form>
</body>
</html>
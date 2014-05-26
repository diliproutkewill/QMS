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
/*	Programme Name : CountryEnterIdJsp.
*	Module Name    : ETrans.
*	Task Name      : Country Master
*	Sub Task Name  : Taking inputs from the CountryAddJsp  and pass these values to "CountryMasterSessionBean" .
*	Author		   : 
*	Date Started   :
*	Date Ended     : Sept 06, 2001.
*	Modified Date  : Sept 06, 2001(By Ratan K.M.).
*	Description    :
*	Methods		   :
*/
%>
<%@ page import =	"javax.naming.InitialContext,
					javax.sql.DataSource,
					java.util.ArrayList,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>
<%
		     String    id		  = null;
			 ArrayList countryIds = null;
			 String	   FILE_NAME  = "ETCCountryEnterId.jsp";		
       logger  = Logger.getLogger(FILE_NAME);
    try
    {
	 
	 if(loginbean.getTerminalId() ==null)//this is to verify whether we can get "terminalid" through the 
	 {											  //login bean object.		
%>
	
         <jsp:forward page="../ESupplyLogin.jsp" />
<%
	}
	else
	{
		try{
             String operation = request.getParameter("Operation");//added by rk
		     id = request.getParameter("id");
   			 InitialContext initial = new InitialContext();
 			 SetUpSessionHome 	ETransUtilitiesHome	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
			 SetUpSession 		ETransUtilitiesRemote	=	(SetUpSession)ETransUtilitiesHome.create();
             countryIds = ETransUtilitiesRemote.getCountryIds("",loginbean.getTerminalId(),operation);//modified by rk
		  }
	 	  catch(Exception exp)
		  {
			//Logger.error(FILE_NAME,"Error in Country EnterId "+exp.toString());
      logger.error(FILE_NAME+"Error in Country EnterId "+exp.toString());
		  }
%>
<html>
<head>
<title>Country EnterId</title>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
function verifyCountryId()
{
<%
	if(id == null)
	{
%>
		document.forms[0].action = "ETCCountryEnterId.jsp";
		document.forms[0].submit();
<%
	}
	else
	{
		if(countryIds.contains(id))
		{
%>
			Record does not exist.
<%
		}
		else
		{
%>
			document.forms[0].action = "ETCCountryProcess.jsp";
			document.forms[0].submit();
<%
		}
	}
%>	
}
function showCountryLOV()
{
 	var Url='ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function getKeyCode()
 {
  if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
 
 function upperCase(input)
 {
  input.value=input.value.toUpperCase();
 }

function placeFocus() 
{
  document.country.countryId.focus();
}

function Mandatory()
{
   document.forms[0].countryId.value = document.forms[0].countryId.value.toUpperCase();
   countryId    =  document.forms[0].countryId.value;
   if(countryId.length == 0)
	{
	    alert(" Please enter Country Id");
		document.forms[0].countryId.focus();
		return false;
	}	
   else if(countryId.length < 2)
   {
	 alert("Please enter two characters for Country Id");	
     document.forms[0].countryId.focus();
	 return false;
   }     
   	document.forms[0].enter.disabled='true';
   	return true;	
}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<% String param = request.getParameter("Operation"); %>
<body  OnLoad="placeFocus()">
<form method="GET"  onSubmit="return Mandatory()" action="ETCCountryView.jsp" name="country">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td colspan="2" bgcolor="ffffff">
      
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Country - <%=param%>&nbsp;</td><td align=right><%=loginbean.generateUniqueId("ETCCountryEnterId.jsp",param)%></td></tr></table></td>
        </tr>
		<tr class='formdata'><td>&nbsp;</td></tr>
        </table>
            
              
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="2">Country Id to  <%=param%> Country Information:</td></tr>
                <tr class='formdata'> 
                  <td colspan="2" >Country 
                    ID:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="countryId" maxlength = "2" size="3" onBlur="upperCase(this)" onkeyPress="return getKeyCode(id)" >
                    <input type="hidden" name="Operation"   value="<%= param %>" >
                    
                    <input type="button" class='input' value="..." name="countryIdLOV" onClick="showCountryLOV()">
                    </td>
                </tr>
                <tr class='denotes' valign="top"> 
                  <td><font color="#FF0000">*</font>Denotes Mandatory </td>
                  <td align="right">
                     <input type="submit" value="Next>>" name="enter" class='input'>
					 <input type="reset" name="Reset" value="Reset" onClick="placeFocus()" class='input'>
                      
                  </td>
                </tr>
              </table>
            
          </td>
        </tr>
      </table></form>
       
</body>
</html>
<%
  }
    }
  	catch(Exception e)
  	{
	 	//Logger.error(FILE_NAME,"Exception in CountryEnterId.jsp"+e.toString());
    logger.error(FILE_NAME+"Exception in CountryEnterId.jsp"+e.toString());
  	}
 %>